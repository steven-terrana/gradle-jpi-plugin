package org.jenkinsci.gradle.plugins.jpi

import spock.lang.IgnoreIf
import spock.lang.Unroll

@IgnoreIf({ System.getProperty('os.name').toLowerCase().contains('windows') })
class ServerTaskSpec extends IntegrationSpec {

    @Unroll
    def 'server task is working - Jenkins #jenkinsVersion'() {
        given:
        projectDir.newFile('settings.gradle')  << """\
            rootProject.name = "test-project"
            includeBuild('${path(new File(''))}')
        """
        def build = projectDir.newFile('build.gradle')
        build << """\
            plugins {
                $additionalPlugin
                id 'org.jenkins-ci.jpi'
            }
            jenkinsPlugin {
                coreVersion = '$jenkinsVersion'
            }
            dependencies {
                jenkinsServer 'org.jenkins-ci.plugins:git:3.12.1'
                implementation 'com.squareup.okio:okio:2.4.3'
            }
            """.stripIndent()

        when:
        Thread.start {
            int response = 0
            Thread.sleep(5000)
            while (response != 200) {
                Thread.sleep(1000)
                try {
                    def shutdown = new URL('http://localhost:8456/safeExit').openConnection()
                    shutdown.requestMethod = 'POST'
                    response = shutdown.responseCode
                } catch (ConnectException e) {
                    e.message
                }
            }
        }

        // run a separate process because the Jenkins shutdown kills the daemon
        def gradleProcess = "${path(new File('gradlew'))} server -Djenkins.httpPort=8456 --no-daemon".
                execute(null, projectDir.root)
        def output = gradleProcess.text

        then:
        output.contains("/jenkins-war-${jenkinsVersion}.war")
        output.contains('webroot: System.getProperty("JENKINS_HOME")')
        new File(projectDir.root, 'work/plugins/git.hpi').exists()

        where:
        jenkinsVersion | additionalPlugin
        '1.580.1'      | ''
        '2.64'         | ''
        '2.64'         | "id 'org.jetbrains.kotlin.jvm' version '1.3.70'"
    }

    private static String path(File file) {
        file.absolutePath.replaceAll('\\\\', '/')
    }
}
