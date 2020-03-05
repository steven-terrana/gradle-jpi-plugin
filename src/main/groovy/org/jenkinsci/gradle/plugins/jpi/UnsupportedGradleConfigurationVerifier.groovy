package org.jenkinsci.gradle.plugins.jpi

import org.gradle.api.GradleException
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration

class UnsupportedGradleConfigurationVerifier {
    /**
     * Represented the dependencies on other Jenkins plugins.
     * Now it should be replaced with implementation
     */
    public static final String PLUGINS_DEPENDENCY_CONFIGURATION_NAME = 'jenkinsPlugins'

    /**
     * Represented the dependencies on other Jenkins plugins.
     * Now it should be replaced with gradle feature variants
     */
    public static final String OPTIONAL_PLUGINS_DEPENDENCY_CONFIGURATION_NAME = 'optionalJenkinsPlugins'

    /**
     * Represented the Jenkins plugin test dependencies.
     * Now it should be replaced with testImplementation
     */
    public static final String JENKINS_TEST_DEPENDENCY_CONFIGURATION_NAME = 'jenkinsTest'

    static void configureDeprecatedConfigurations(Project project) {
        configureDeprecatedConfiguration(project,
                PLUGINS_DEPENDENCY_CONFIGURATION_NAME,
                'is not supported anymore. Please use implementation configuration')
        configureDeprecatedConfiguration(project,
                OPTIONAL_PLUGINS_DEPENDENCY_CONFIGURATION_NAME,
                'is not supported anymore. Please use Gradle feature variants')
        configureDeprecatedConfiguration(project,
                JENKINS_TEST_DEPENDENCY_CONFIGURATION_NAME,
                'is not supported anymore. Please use testImplementation configuration')
    }

    private static void configureDeprecatedConfiguration(Project project, String confName, String errorMessage) {
        Configuration configurationToFail = project.configurations.create(confName)
        configurationToFail.visible = false

        configurationToFail.incoming.beforeResolve {
            if (it.dependencies.size() > 0) {
                throw new GradleException("$confName $errorMessage")
            }
        }
    }
}
