package org.jenkinsci.gradle.plugins.jpi.internal

import groovy.transform.CompileStatic
import groovy.transform.Immutable

import java.nio.file.Path

@CompileStatic
class DependencyLicenseValidator {
    private static final String HEADER = 'Could not resolve license(s) via POM for %d %s:%n'
    private static final String DEPENDENCY = '\t- %s%n'
    private static final String FOOTER = 'The above will be missing from %s%n'
    static Result validate(Set<String> requested, Set<String> resolved, Path destination) {
        def unresolvable = (requested - resolved)
        StringBuilder sb = new StringBuilder()
        if (!unresolvable.isEmpty()) {
            def pluralized = unresolvable.size() == 1 ? 'dependency' : 'dependencies'
            sb.append(String.format(HEADER, unresolvable.size(), pluralized))
            unresolvable.toSorted().each {
                sb.append(String.format(DEPENDENCY, it))
            }
            sb.append(String.format(FOOTER, destination))
        }
        new Result(unresolvable.size() > 0, sb.toString())
    }

    @Immutable
    static class Result {
        boolean unresolved
        String message
    }
}
