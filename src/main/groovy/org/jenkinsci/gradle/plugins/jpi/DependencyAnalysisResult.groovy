package org.jenkinsci.gradle.plugins.jpi

import groovy.transform.CompileStatic

@CompileStatic
class DependencyAnalysisResult {

    final String manifestPluginDependencies

    DependencyAnalysisResult(String manifestPluginDependencies) {
        this.manifestPluginDependencies = manifestPluginDependencies
    }
}
