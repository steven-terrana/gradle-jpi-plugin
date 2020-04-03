package org.jenkinsci.gradle.plugins.jpi.internal

import groovy.xml.QName

class LicenseDataExtractor {
    private final XmlParser parser

    LicenseDataExtractor(XmlParser parser) {
        this.parser = parser
    }

    LicenseDataExtractor() {
        this(new XmlParser(false, false))
    }

    LicenseData extractFrom(Reader reader) {
        Node pom = parser.parse(reader)

        String name = pom[QName.valueOf('name')].text()
        String description = pom[QName.valueOf('description')].text()
        String url = pom[QName.valueOf('url')].text()
        NodeList licenses = pom[QName.valueOf('licenses')]

        def mapped = licenses[QName.valueOf('license')].collect { Node license ->
            String licenseUrl = license[QName.valueOf('url')].text()
            String licenseName = license[QName.valueOf('name')].text()
            new License(licenseName, licenseUrl)
        }.toSet()
        new LicenseData(name, description, url, mapped)
    }
}
