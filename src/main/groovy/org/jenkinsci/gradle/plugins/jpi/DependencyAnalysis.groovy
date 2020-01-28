package org.jenkinsci.gradle.plugins.jpi

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.result.DependencyResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.attributes.Attribute
import org.gradle.api.attributes.Category
import org.gradle.api.attributes.LibraryElements

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

    final Configuration allLibraryDependencies

    private static final Attribute CATEGORY_ATTRIBUTE =
            Attribute.of(Category.CATEGORY_ATTRIBUTE.name, String)
    private static final Attribute LIBRARY_ELEMENTS_ATTRIBUTE =
            Attribute.of(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE.name, String)
    private final List<JpiConfigurations> jpiConfigurations = []

    private DependencyAnalysisResult analysisResult

    DependencyAnalysis(Project project) {
        this.allLibraryDependencies = project.configurations.detachedConfiguration()
        this.allLibraryDependencies.withDependencies {
            // do the analysis when this configuration is resolved
            analyse()
        }
    }

    void registerJpiConfigurations(Configuration consumableLibraries,
                                   Configuration consumablePlugins,
                                   Configuration resolvablePlugins) {
        jpiConfigurations.add(new JpiConfigurations(consumableLibraries, consumablePlugins, resolvablePlugins))
    }

    DependencyAnalysisResult analyse() {
        if (analysisResult) {
            return analysisResult
        }

        def manifestEntry = new StringBuilder()

        jpiConfigurations.each { confs ->
            analyseDependencies(confs, allLibraryDependencies, manifestEntry)
        }
        analysisResult = new DependencyAnalysisResult(manifestEntry.toString())
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
                if (variant.attributes.getAttribute(CATEGORY_ATTRIBUTE) != Category.LIBRARY
                        || variant.attributes.getAttribute(LIBRARY_ELEMENTS_ATTRIBUTE) != JpiPlugin.JPI) {
                    // Skip dependencies that are not libraries with JPI files.
                    // We request these in the setup in JpiPlugin.configureConfigurations().
                    // However, an individual dependency can override attributes, for example 'category=platform'.
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
