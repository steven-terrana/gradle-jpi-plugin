package org.jenkinsci.gradle.plugins.jpi.internal

import groovy.transform.Immutable

@Immutable
class LicenseData {
    String name
    String description
    String url
    Set<License> licenses
}
