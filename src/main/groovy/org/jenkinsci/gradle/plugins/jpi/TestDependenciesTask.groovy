package org.jenkinsci.gradle.plugins.jpi

import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Copy

class TestDependenciesTask extends Copy {
    public static final String TASK_NAME = 'resolveTestDependencies'

    private FileCollection artifacts

    protected Map<String, String> mapping = [:]

    TestDependenciesTask() {
        include('*.hpi')
        include('*.jpi')

        into {
            JavaPluginConvention javaConvention = project.convention.getPlugin(JavaPluginConvention)
            new File(javaConvention.sourceSets.test.output.resourcesDir, 'test-dependencies')
        }

        rename { mapping[it] }

        doLast {
            List<String> baseNames = source*.name.collect { mapping[it] }.collect { it[0..it.lastIndexOf('.') - 1] }
            new File(destinationDir, 'index').setText(baseNames.join('\n'), 'UTF-8')
        }
    }

    @Override
    protected void copy() {
        artifacts.each {
            def baseName = it.name[0..it.name.lastIndexOf('-') - 1]
            def extension = it.name[it.name.lastIndexOf('.') + 1..-1]
            mapping[it.name] = "${baseName}.${extension}"
        }

        super.copy()
    }

    void setConfiguration(FileCollection configuration) {
        this.artifacts = configuration
        this.from(configuration)
    }

    @Classpath
    FileCollection getConfiguration() {
        artifacts
    }
}
