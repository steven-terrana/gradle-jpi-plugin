package org.jenkinsci.gradle.plugins.jpi

import groovy.transform.CompileStatic

@CompileStatic
class TestSupport {
    static final EMBEDDED_IVY_URL = "${System.getProperty('user.dir')}/src/test/repo"
            .replace('\\', '/')
}
