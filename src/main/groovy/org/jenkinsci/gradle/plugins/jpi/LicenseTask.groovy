package org.jenkinsci.gradle.plugins.jpi

import groovy.xml.MarkupBuilder
import groovy.xml.QName
import org.gradle.api.DefaultTask
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class LicenseTask extends DefaultTask {

    @Classpath
    final Property<Configuration> libraryConfiguration = project.objects.property(Configuration)

    @OutputDirectory
    File outputDirectory

    @TaskAction
    void generateLicenseInfo() {
        JpiExtension jpiExtension = project.extensions.getByType(JpiExtension)
        Set<ResolvedArtifact> pomArtifacts = collectPomArtifacts()

        new File(outputDirectory, 'licenses.xml').withWriter { Writer writer ->
            MarkupBuilder xmlMarkup = new MarkupBuilder(writer)

            xmlMarkup.'l:dependencies'(
                    'xmlns:l': 'licenses', version: project.version, artifactId: project.name, groupId: project.group,
            ) {
                'l:dependency'(
                        version: project.version, artifactId: project.name, groupId: project.group,
                        name: jpiExtension.displayName, url: jpiExtension.url,
                ) {
                    'l:description'(project.description)
                    jpiExtension.licenses.each { license ->
                        'l:license'(url: license.url, name: license.name)
                    }
                }

                pomArtifacts.each { ResolvedArtifact pomArtifact ->
                    Node pom = new XmlParser().parse(pomArtifact.file)

                    ModuleVersionIdentifier gav = pomArtifact.moduleVersion.id
                    String name = pom[QName.valueOf('name')].text()
                    String description = pom[QName.valueOf('description')].text()
                    String url = pom[QName.valueOf('url')].text()
                    NodeList licenses = pom[QName.valueOf('licenses')]

                    'l:dependency'(
                            version: gav.version, artifactId: gav.name, groupId: gav.group, name: name, url: url,
                    ) {
                        'l:description'(description)
                        licenses[QName.valueOf('license')].each { Node license ->
                            String licenseUrl = license[QName.valueOf('url')].text()
                            String licenseName = license[QName.valueOf('name')].text()
                            'l:license'(url: licenseUrl, name: licenseName)
                        }
                    }
                }
            }
        }
    }

    private Set<ResolvedArtifact> collectPomArtifacts() {
        project.configurations
                .detachedConfiguration(collectDependencies())
                .resolvedConfiguration
                .resolvedArtifacts
    }

    private Dependency[] collectDependencies() {
        libraryConfiguration.get().resolvedConfiguration.resolvedArtifacts.findAll { ResolvedArtifact artifact ->
            artifact.id.componentIdentifier instanceof ModuleComponentIdentifier
        }.collect { ResolvedArtifact artifact ->
            ModuleVersionIdentifier id = artifact.moduleVersion.id
            project.dependencies.create("${id.group}:${id.name}:${id.version}@pom")
        }
    }
}
