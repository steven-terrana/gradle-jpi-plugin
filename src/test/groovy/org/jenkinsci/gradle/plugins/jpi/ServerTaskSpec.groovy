package org.jenkinsci.gradle.plugins.jpi

class ServerTaskSpec extends IntegrationSpec {

    def 'server task is working'() {
        given:
        projectDir.newFile('gradle.properties')  << 'org.gradle.daemon=false'
        projectDir.newFile('settings.gradle')  << """\
            rootProject.name = "test-project"
            includeBuild('${new File('').absolutePath}')
        """
        def build = projectDir.newFile('build.gradle')
        build << '''\
            plugins {
                id 'org.jenkins-ci.jpi'
            }
            jenkinsPlugin {
                coreVersion = '1.580.1'
            }
            '''.stripIndent()

        when:
        Thread.start {
            int response = 0
            Thread.sleep(5000)
            while(response != 200) {
                Thread.sleep(500)
                try {
                    def shutdown = new URL('http://localhost:8080/safeExit').openConnection()
                    shutdown.setRequestMethod("POST")
                    response = shutdown.getResponseCode()
                } catch (Exception e) {
                    println e.message
                }
            }
        }

        // run a separate process because the Jenkins shutdown kills the daemon
        def output = new File('./gradlew server').absolutePath.execute(null, projectDir.root).text

        then:
        output.contains('/jenkins-war-1.580.1.war')
        output.contains('webroot: System.getProperty("JENKINS_HOME")')
    }

}
