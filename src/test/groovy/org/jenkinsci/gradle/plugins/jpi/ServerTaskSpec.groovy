package org.jenkinsci.gradle.plugins.jpi

import spock.lang.Unroll

class ServerTaskSpec extends IntegrationSpec {

    @Unroll
    def 'server task is working - Jenkins #jenkinsVersion'() {
        given:
        projectDir.newFile('settings.gradle')  << """\
            rootProject.name = "test-project"
            includeBuild('${new File('').absolutePath}')
        """
        def build = projectDir.newFile('build.gradle')
        build << """\
            plugins {
                id 'org.jenkins-ci.jpi'
            }
            jenkinsPlugin {
                coreVersion = '$jenkinsVersion'
            }
            dependencies {
                jenkinsServer 'org.jenkins-ci.plugins:git:3.12.1'
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
        def gradleProcess = new File('./gradlew server -Djenkins.httpPort=8456 --no-daemon').
                absolutePath.execute(null, projectDir.root)
        def output = gradleProcess.text

        then:
        output.contains("/jenkins-war-${jenkinsVersion}.war")
        output.contains('webroot: System.getProperty("JENKINS_HOME")')
        new File(projectDir.root, 'work/plugins/git.hpi').exists()

        where:
        jenkinsVersion << ['1.580.1', '2.64']
    }

}
