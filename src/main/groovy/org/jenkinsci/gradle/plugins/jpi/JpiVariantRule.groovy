package org.jenkinsci.gradle.plugins.jpi

import org.gradle.api.artifacts.CacheableRule
import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.model.ObjectFactory
import org.gradle.internal.component.external.model.ivy.IvyModuleResolveMetadata

import javax.inject.Inject

@CacheableRule
abstract class JpiVariantRule implements ComponentMetadataRule {

    @Inject
    abstract ObjectFactory getObjects()

    @Override
    void execute(ComponentMetadataContext ctx) {
        def id = ctx.details.id
        if (isIvyResolvedDependency(ctx)) {
            addEmptyJpiVariant(ctx)
        } else if (isJenkinsPackaging(ctx)) {
            ctx.details.withVariant('runtime') {
                it.attributes {
                    it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements, 'jpi'))
                }
                it.withDependencies {
                    // Dependencies with a classifier point at JARs and can be removed
                    // TODO needs public API - https://github.com/gradle/gradle/issues/11975
                    it.removeAll { it.originalMetadata?.dependencyDescriptor?.dependencyArtifact?.classifier }
                }
            }
            ctx.details.withVariant('compile') {
                it.withFiles {
                    it.removeAllFiles()
                    it.addFile("${id.name}-${id.version}.jar")
                }
            }
            ctx.details.addVariant('jarRuntimeElements', 'runtime') {
                it.attributes {
                    it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
                            objects.named(LibraryElements, LibraryElements.JAR))
                }
                it.withFiles {
                    it.removeAllFiles()
                    it.addFile("${id.name}-${id.version}.jar")
                }
            }
        } else if (isJarPackaging(ctx)) {
            addEmptyJpiVariant(ctx)
        }
    }

    /**
     * Add a variant that we can match if a JPI variant depends on something that
     * does not have a JPI variant. This is the case for all POM-based JPI modules
     * that declare dependencies to JAR modules, because the metadata does not
     * have sufficient separation of JAR/JPI variants.
     */
    private addEmptyJpiVariant(ComponentMetadataContext ctx) {
        ctx.details.addVariant('jpiEmpty') {
            it.attributes {
                it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
                        objects.named(LibraryElements, JpiPlugin.JPI))
            }
        }
        // This variant might still resolve to a jar file - https://github.com/gradle/gradle/issues/11974
    }

    private boolean isIvyResolvedDependency(ComponentMetadataContext ctx) {
        ctx.metadata instanceof IvyModuleResolveMetadata
    }

    private boolean isJenkinsPackaging(ComponentMetadataContext ctx) {
        // TODO we need public API for this - https://github.com/gradle/gradle/issues/11955
        String packaging = ctx.metadata.packaging
        packaging == 'jpi' ||  packaging == 'hpi'
    }

    private boolean isJarPackaging(ComponentMetadataContext ctx) {
        String packaging = ctx.metadata.packaging
        packaging == 'jar' || packaging == 'jenkins-module'
    }
}
