package org.jenkinsci.gradle.plugins.jpi

import groovy.transform.CompileStatic
import org.gradle.api.artifacts.Configuration

@CompileStatic
class DependencyAnalysisResult {

    final Configuration allLibraryDependencies
    final String manifestPluginDependencies

    DependencyAnalysisResult(Configuration allLibraryDependencies, String manifestPluginDependencies) {
        this.allLibraryDependencies = allLibraryDependencies
        this.manifestPluginDependencies = manifestPluginDependencies
    }
}
