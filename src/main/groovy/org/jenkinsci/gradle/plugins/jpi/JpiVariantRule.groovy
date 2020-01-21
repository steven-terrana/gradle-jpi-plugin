package org.jenkinsci.gradle.plugins.jpi

import org.gradle.api.artifacts.CacheableRule
import org.gradle.api.artifacts.ComponentMetadataContext
import org.gradle.api.artifacts.ComponentMetadataRule
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

@CacheableRule
class JpiVariantRule implements ComponentMetadataRule {

    @Inject
    ObjectFactory getObjects() {
        throw new UnsupportedOperationException()
    }

    @Override
    void execute(ComponentMetadataContext ctx) {
        def id = ctx.details.id
        if (!isJenkinsPackaging(ctx)) {
            return
        }

        ctx.details.withVariant('runtime') {
            it.attributes {
                it.attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements, 'jpi'))
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
    }

    private boolean isJenkinsPackaging(ComponentMetadataContext ctx) {
        // TODO we need public API for this - https://github.com/gradle/gradle/issues/11955
        String packaging = ctx.metadata.packaging
        packaging == 'jpi' ||  packaging == 'hpi'
    }
}
