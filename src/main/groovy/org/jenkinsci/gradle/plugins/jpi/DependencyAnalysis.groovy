package org.jenkinsci.gradle.plugins.jpi

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.result.DependencyResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category

@CompileStatic
class DependencyAnalysis {

    private class JpiConfigurations {
        Configuration consumableLibraries
        Configuration consumablePlugins
        Configuration resolvablePlugins

        JpiConfigurations(Configuration consumableLibraries,
                          Configuration consumablePlugins,
                          Configuration resolvablePlugins) {
            this.consumableLibraries = consumableLibraries
            this.consumablePlugins = consumablePlugins
            this.resolvablePlugins = resolvablePlugins
        }
    }

    private final Attribute categoryAttribute = Attribute.of(Category.CATEGORY_ATTRIBUTE.name, String)
    private final List<JpiConfigurations> jpiConfigurations = []

    private DependencyAnalysisResult analysisResult

    void registerJpiConfigurations(Configuration consumableLibraries,
                              Configuration consumablePlugins,
                              Configuration resolvablePlugins) {
        jpiConfigurations.add(new JpiConfigurations(consumableLibraries, consumablePlugins, resolvablePlugins))
    }

    DependencyAnalysisResult analyse(Project project) {
        if (analysisResult) {
            return analysisResult
        }

        def manifestEntry = new StringBuilder()
        def allLibraries = project.configurations.detachedConfiguration()

        jpiConfigurations.each { confs ->
            analyseDependencies(confs, allLibraries, manifestEntry)
        }
        analysisResult = new DependencyAnalysisResult(allLibraries, manifestEntry.toString())
        analysisResult
    }

    private analyseDependencies(JpiConfigurations configurations,
                                Configuration allLibraries, StringBuilder manifestEntry) {
        def optional = configurations.resolvablePlugins.name != JpiPlugin.JENKINS_RUNTIME_CLASSPATH_CONFIGURATION_NAME

        configurations.resolvablePlugins.incoming.resolutionResult.root.dependencies.each { DependencyResult result ->
            if (result.constraint || !(result instanceof ResolvedDependencyResult)) {
                return
            }
            def selected = ((ResolvedDependencyResult) result).selected
            def moduleVersion = selected.moduleVersion
            if (moduleVersion == null) {
                return
            }
            selected.variants.each { variant ->
                if (variant.attributes.getAttribute(categoryAttribute) != Category.LIBRARY) {
                    //skip platform dependencies
                    return
                }

                if (manifestEntry.length() > 0) {
                    manifestEntry.append(',')
                }
                manifestEntry.append(moduleVersion.name)
                manifestEntry.append(':')
                manifestEntry.append(moduleVersion.version)
                if (optional) {
                    manifestEntry.append(';resolution:=optional')
                }

                def moduleDependencies = configurations.resolvablePlugins.allDependencies.findAll {
                    it instanceof ModuleDependency && it.group == moduleVersion.group && it.name == moduleVersion.name
                }
                configurations.consumablePlugins.dependencies.addAll(moduleDependencies)
            }
        }
        allLibraries.dependencies.addAll(configurations.consumableLibraries.allDependencies
                - configurations.consumablePlugins.allDependencies)
    }
}
