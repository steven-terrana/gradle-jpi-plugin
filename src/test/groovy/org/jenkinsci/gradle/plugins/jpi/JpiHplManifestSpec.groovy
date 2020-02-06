package org.jenkinsci.gradle.plugins.jpi

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

import java.util.jar.Manifest

class JpiHplManifestSpec extends Specification {
    Project project = ProjectBuilder.builder().build()

    def 'basics'() {
        setup:
        project.with {
            apply plugin: 'jpi'
            dependencies.with {
                implementation('org.apache.commons:commons-lang3:3.9')
            }
            evaluate() // trigger 'afterEvaluate { }' configurations
        }
        def libraries = [
                new File(project.rootDir, 'src/main/resources'),
                new File(project.buildDir, 'classes/java/main'),
                new File(project.buildDir, 'resources/main'),
        ]
        libraries*.mkdirs()
        libraries += new File(project.gradle.gradleUserHomeDir,
                'caches/modules-2/files-2.1/org.apache.commons/commons-lang3/3.9/' +
                        '122c7cee69b53ed4a7681c03d4ee4c0e2765da5/commons-lang3-3.9.jar')

        when:
        Manifest manifest = new JpiHplManifest(project)

        then:
        manifest.mainAttributes.getValue('Resource-Path') == new File(project.projectDir, 'src/main/webapp').path
        manifest.mainAttributes.getValue('Libraries') == libraries*.path.join(',')
    }

    def 'non-existing libraries are ignored'() {
        setup:
        project.with {
            apply plugin: 'jpi'
        }

        when:
        JpiHplManifest manifest = new JpiHplManifest(project)

        then:
        manifest.mainAttributes.getValue('Resource-Path') == new File(project.projectDir, 'src/main/webapp').path
        manifest.mainAttributes.getValue('Libraries') == ''
    }
}
