package org.jenkinsci.gradle.plugins.jpi

class JpiPublishingAndConsumptionTest extends IntegrationSpec {
    private File producerBuild
    private File consumerBuild

    def setup() {
        projectDir.newFolder('producer')
        projectDir.newFolder('consumer')
        def repo = projectDir.newFolder('repo')
        projectDir.newFile('producer/settings.gradle') << 'rootProject.name = "producer"'
        projectDir.newFile('consumer/settings.gradle') << 'rootProject.name = "consumer"'
        producerBuild = projectDir.newFile('producer/build.gradle')
        consumerBuild = projectDir.newFile('consumer/build.gradle')
        producerBuild << """\
            plugins {
                id 'org.jenkins-ci.jpi'
                id 'maven-publish'
            }
            group = 'org'
            version = '1.0'
            publishing {
                repositories {
                    maven {
                        name 'test'
                        url '${repo.absolutePath}'
                    }
                }
            }
            """.stripIndent()
        consumerBuild << """\
            plugins {
                id 'org.jenkins-ci.jpi'
            }
            repositories {
                maven {
                    url '${repo.absolutePath}'
                }
            }
            tasks.create('runtime') {
                doLast {
                    configurations.runtimeClasspath.files.each { println it.name }
                }
            }
            tasks.create('compile') {
                doLast {
                    configurations.compileClasspath.files.each { println it.name }
                }
            }
            tasks.create('jenkinsRuntime') {
                doLast {
                    configurations.runtimeClasspathJenkins.incoming.artifactView { it.lenient(true) }.files.each { 
                        println it.name
                    }
                }
            }
            """.stripIndent()
    }

    def "publishes compile, runtime and jenkins runtime variant"() {
        given:
        producerBuild << """\
            dependencies {
                api 'org.jenkins-ci.plugins:credentials:1.9.4'
                implementation 'org.jenkins-ci.plugins:git:3.12.1'
                api 'org.apache.commons:commons-lang3:3.9'
                implementation 'org.apache.commons:commons-collections4:4.4'
            }
            """.stripIndent()
        publishProducer()

        when:
        consumerBuild << """
            dependencies {
                implementation 'org:producer:1.0'
            }
        """

        then:
        resolveConsumer('compile') == [
                "producer-1.0.jar",
                "credentials-1.9.4.jar",
                "commons-lang3-3.9.jar",
                "jcip-annotations-1.0.jar",
                "findbugs-annotations-1.3.9-1.jar",
                "jsr305-1.3.9.jar"
        ] as Set

        resolveConsumer('runtime') == [
                "producer-1.0.jar",
                "git-3.12.1.jar",
                "commons-collections4-4.4.jar",
                "git-client-2.7.7.jar",
                "jsch-0.1.54.1.jar",
                "ssh-credentials-1.13.jar",
                "credentials-2.1.18.jar",
                "commons-lang3-3.9.jar",
                "joda-time-2.9.5.jar",
                "scm-api-2.6.3.jar",
                "workflow-scm-step-2.7.jar",
                "workflow-step-api-2.20.jar",
                "structs-1.19.jar",
                "matrix-project-1.7.1.jar",
                "mailer-1.18.jar",
                "antlr4-runtime-4.5.jar",
                "symbol-annotation-1.19.jar",
                "org.eclipse.jgit.http.server-4.5.5.201812240535-r.jar",
                "org.eclipse.jgit.http.apache-4.5.5.201812240535-r.jar",
                "org.eclipse.jgit-4.5.5.201812240535-r.jar",
                "apache-httpcomponents-client-4-api-4.5.3-2.0.jar",
                "display-url-api-0.2.jar",
                "junit-1.3.jar",
                "script-security-1.13.jar",
                "org.abego.treelayout.core-1.0.1.jar",
                "JavaEWAH-0.7.9.jar",
                "slf4j-api-1.7.2.jar",
                "jsch-0.1.54.jar",
                "httpmime-4.5.3.jar",
                "fluent-hc-4.5.3.jar",
                "httpclient-cache-4.5.3.jar",
                "httpclient-4.5.3.jar",
                "groovy-sandbox-1.8.jar",
                "httpcore-4.4.6.jar",
                "commons-logging-1.2.jar",
                "commons-codec-1.9.jar"
        ] as Set

        resolveConsumer('jenkinsRuntime') == [
                "producer-1.0.hpi",
                "git-3.12.1.hpi",
                "git-client-2.7.7.hpi",
                "jsch-0.1.54.1.hpi",
                "ssh-credentials-1.13.hpi",
                "credentials-2.1.18.hpi",
                "scm-api-2.6.3.hpi",
                "workflow-scm-step-2.7.hpi",
                "workflow-step-api-2.20.hpi",
                "structs-1.19.hpi",
                "matrix-project-1.7.1.hpi",
                "mailer-1.18.hpi",
                "apache-httpcomponents-client-4-api-4.5.3-2.0.hpi",
                "display-url-api-0.2.hpi",
                "junit-1.3.hpi",
                "script-security-1.13.hpi"
        ] as Set
    }

    def "publishes feature variant with compile, runtime and jenkins runtime variant"() {
        given:
        producerBuild << """\
            java {
                registerFeature('credentials') {
                    usingSourceSet(sourceSets.main)
                }
            }
            dependencies {
                credentialsImplementation 'org.jenkins-ci.plugins:credentials:1.9.4'
            }
            """.stripIndent()
        publishProducer()

        when:
        consumerBuild << """
            dependencies {
                implementation 'org:producer:1.0'
            }
        """

        then:
        resolveConsumer('compile') == [ 'producer-1.0.jar' ] as Set
        resolveConsumer('runtime') == [ 'producer-1.0.jar' ] as Set
        resolveConsumer('jenkins') == [ 'producer-1.0.hpi' ] as Set

        when:
        consumerBuild << """
            dependencies {
                implementation 'org:producer:1.0'
                implementation('org:producer:1.0') {
                    capabilities { requireCapability('org:producer-credentials') }
                }
            }
        """

        then:
        resolveConsumer('compile') == [ 'producer-1.0.jar' ] as Set
        resolveConsumer('runtime') == [
                'producer-1.0.jar',
                'credentials-1.9.4.jar',
                'jcip-annotations-1.0.jar',
                'findbugs-annotations-1.3.9-1.jar',
                'jsr305-1.3.9.jar'] as Set
        resolveConsumer('jenkins') == [ 'producer-1.0.hpi', 'credentials-1.9.4.hpi' ] as Set
    }

    private void publishProducer() {
        gradleRunner().withProjectDir(producerBuild.parentFile).forwardOutput().
                withArguments('publishMavenJpiPublicationToTestRepository', '-s').build()
    }

    private Set<String> resolveConsumer(String resolveTask) {
        def result = gradleRunner().withProjectDir(consumerBuild.parentFile).forwardOutput().
                withArguments(resolveTask, '-q').build()
        return result.output.split('\n')
    }

}
