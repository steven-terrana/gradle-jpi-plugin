package org.jenkinsci.gradle.plugins.jpi

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class JpiExtensionSpec extends Specification {
    Project project = Mock(Project)

    def 'work directory defaults to work if not set'() {
        when:
        Project project = ProjectBuilder.builder().build()
        JpiExtension jpiExtension = new JpiExtension(project)
        jpiExtension.workDir = null

        then:
        jpiExtension.workDir == new File(project.projectDir, 'work')
    }

    def 'work directory defaults to work in child project the extension is applied to if not set'() {
        when:
        Project parent = ProjectBuilder.builder().build()
        Project project = ProjectBuilder.builder().withParent(parent).build()
        JpiExtension jpiExtension = new JpiExtension(project)
        jpiExtension.workDir = null

        then:
        jpiExtension.workDir == new File(project.projectDir, 'work')
    }

    def 'work directory is used when set'() {
        when:
        Project project = ProjectBuilder.builder().build()
        JpiExtension jpiExtension = new JpiExtension(project)
        File dir = new File('/tmp/foo')
        jpiExtension.workDir = dir

        then:
        jpiExtension.workDir == dir
    }

    def 'jenkinsCore dependencies'() {
        setup:
        Project project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'jpi'
            jenkinsPlugin {
                coreVersion = '1.509.3'
            }
        }

        then:
        def dependencies = collectDependencies(project, 'compileOnly')
        'org.jenkins-ci.main:jenkins-core:1.509.3' in dependencies
        'javax.servlet:servlet-api:2.4' in dependencies
        'findbugs:annotations:1.0.0' in dependencies
    }

    def 'jenkinsTest dependencies before 1.505'() {
        setup:
        Project project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'jpi'
            jenkinsPlugin {
                coreVersion = '1.504'
            }
        }

        then:
        def dependenciesCompileTime = collectDependencies(project, 'testImplementation')
        def dependenciesRuntime = collectDependencies(project, 'testRuntimeOnly')
        'org.jenkins-ci.main:jenkins-test-harness:1.504' in dependenciesCompileTime
        'org.jenkins-ci.main:ui-samples-plugin:1.504' in dependenciesCompileTime
        'org.jenkins-ci.main:jenkins-war:1.504' in dependenciesRuntime
        'junit:junit-dep:4.10' in dependenciesCompileTime
    }

    def 'jenkinsTest dependencies before 1.533'() {
        setup:
        Project project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'jpi'
            jenkinsPlugin {
                coreVersion = '1.532'
            }
        }

        then:
        def dependenciesCompileTime = collectDependencies(project, 'testImplementation')
        def dependenciesRuntime = collectDependencies(project, 'testRuntimeOnly')
        'org.jenkins-ci.main:jenkins-test-harness:1.532' in dependenciesCompileTime
        'org.jenkins-ci.main:ui-samples-plugin:1.532' in dependenciesCompileTime
        'org.jenkins-ci.main:jenkins-war:1.532' in dependenciesRuntime
    }

    def 'jenkinsTest dependencies for 1.533 or later'() {
        setup:
        Project project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'jpi'
            jenkinsPlugin {
                coreVersion = '1.533'
            }
        }

        then:
        def dependenciesCompileTime = collectDependencies(project, 'testImplementation')
        def dependenciesRuntime = collectDependencies(project, 'testRuntimeOnly')
        'org.jenkins-ci.main:jenkins-test-harness:1.533' in dependenciesCompileTime
        'org.jenkins-ci.main:ui-samples-plugin:2.0' in dependenciesCompileTime
        'org.jenkins-ci.main:jenkins-war:1.533' in dependenciesRuntime
    }

    def 'jenkinsTest dependencies for 1.645 or later'() {
        setup:
        Project project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'jpi'
            jenkinsPlugin {
                coreVersion = '1.645'
            }
        }

        then:
        def dependenciesCompileTime = collectDependencies(project, 'testImplementation')
        def dependenciesRuntime = collectDependencies(project, 'testRuntimeOnly')
        'org.jenkins-ci.main:jenkins-test-harness:2.0' in dependenciesCompileTime
        'org.jenkins-ci.main:ui-samples-plugin:2.0' in dependenciesCompileTime
        'org.jenkins-ci.main:jenkins-war:1.645' in dependenciesRuntime
    }

    def 'jenkinsTest dependencies for 2.64 or later'() {
        setup:
        Project project = ProjectBuilder.builder().build()

        when:
        project.with {
            apply plugin: 'jpi'
            jenkinsPlugin {
                coreVersion = '2.64'
            }
        }

        then:
        def dependenciesCompileTime = collectDependencies(project, 'testImplementation')
        'org.jenkins-ci.main:jenkins-test-harness:2.60' in dependenciesCompileTime
        'org.jenkins-ci.main:ui-samples-plugin:2.0' in dependenciesCompileTime
    }

    private static collectDependencies(Project project, String configuration) {
        project.configurations.getByName(configuration).dependencies.collect {
            "${it.group}:${it.name}:${it.version}".toString()
        }
    }
}
