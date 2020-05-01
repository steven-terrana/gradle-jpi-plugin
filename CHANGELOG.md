## 0.40.0 (unreleased)

 * introduce `checkAccessModifier` task as a dependency of `check`
 * support `-PcheckAccessModifier.useBeta=true` to opt into beta APIs

## 0.39.0 (2020-04-15)

  * use variant-aware dependency management - [PR #132](https://github.com/jenkinsci/gradle-jpi-plugin/pull/132), allowing
    publication of Gradle Module Metadata
  * only configure sources and javadoc variants if `configurePublishing` is `true` - [PR #140](https://github.com/jenkinsci/gradle-jpi-plugin/pull/132),
    enabling the use of opinionated publishing plugins without duplicate artifacts
  * requires Gradle 6.0 or later
  * remove `jenkinsPlugins` in favor of using `implementation`
  * remove `optionalJenkinsPlugins` in favor of using [Gradle feature variants](https://docs.gradle.org/6.1.1/userguide/feature_variants.html)
  * remove `jenkinsTest` in favor of using `testImplementation`
  * updated to Gradle 6.3

## 0.38.0 (2020-01-27)

  * upgrade dependency `org.jenkins-ci.main:jenkins-test-harness:2.31 -> 2.60` added to projects - [PR #133](https://github.com/jenkinsci/gradle-jpi-plugin/pull/133)
  * updated to Gradle 6.1.1

## 0.37.1 (2020-01-08)

  * support version constraints, including platforms, when using Gradle 5.3+ - [PR #128](https://github.com/jenkinsci/gradle-jpi-plugin/pull/128)

## 0.37.0 (unpublished)

  * publish failed part of the way through
  
## 0.36.2 (2020-01-04)

  * disable module metadata generation because it does not contain all variants - [PR #127](https://github.com/jenkinsci/gradle-jpi-plugin/pull/127)

## 0.36.1 (2020-01-02)

  * fix testClasses task dependency so it runs generate-test-hpl - [PR #125](https://github.com/jenkinsci/gradle-jpi-plugin/pull/125)

## 0.36.0 (2019-12-22)

  * include plugin classes as a jar [JENKINS-54064](https://issues.jenkins-ci.org/browse/JENKINS-54064) - [PR #123](https://github.com/jenkinsci/gradle-jpi-plugin/pull/123)
  * fix use of deprecated properties in Gradle 6.0 - [PR #124](https://github.com/jenkinsci/gradle-jpi-plugin/pull/124)

## 0.35.0 (2019-11-22)

  * configure tasks with lazy #named:
    * compileJava
    * processTestResources
    * jar
    * javadocJar
    * sourcesJar
    * war
  * configure tasks with lazy #configureEach:
    * GroovyCompile
    * JavaCompile
  * register existing tasks with lazy #register:
    * configureManifest
    * generate-test-hpl
    * generateLicenseInfo
    * insertTest
    * javadocJar
    * jpi
    * localizer
    * resolveTestDependencies
    * server
    * sourceJar
  * let Gradle create JpiExtension instead of manually instantiating
  * use of task avoidance APIs requires at least Gradle 4.9 (released: 2018-07-16)
  * updated to Gradle 6.0.1

## 0.34.0 (2019-11-15)

  * updated to Gradle 6.0
  * fix property annotation of `TestDependenciesTask#configuration` in response to Gradle warning 
  * make `GenerateTestHpl#hplDir` property `final` in response to Gradle warning
  * formatting consistency improvements to in-repo documentation
  * updated implementation dependency org.jenkins-ci:version-number:1.0 -> 1.1
  * updated implementation dependency org.jvnet.localizer:maven-localizer-plugin:1.13 -> 1.24
  * updated test dependency org.apache.commons:commons-text:1.6 -> 1.8
  * updated test dependency org.spockframework:spock-core:1.2-groovy-2.5 -> 1.3-groovy-2.5
  * updated test dependency org.xmlunit:xmlunit-core:2.3.0 -> 2.6.3
  

## 0.33.0 (2019-06-28)

  * put work directory inside the project directory instead of at root level [PR #113](https://github.com/jenkinsci/gradle-jpi-plugin/pull/113)
  * updated to Gradle 5.5

## 0.32.0 (2019-05-08)

  * Support publishing POMs with resolved versions when dependencies
    are declared with dynamic versions - [PR #112](https://github.com/jenkinsci/gradle-jpi-plugin/pull/112)
  * updated to Gradle 5.4.1

## 0.31.0 (2019-04-01)

  * Use [Gradle DSL for POM customization](https://docs.gradle.org/4.8/release-notes.html#customizing-the-generated-pom)
    to allow multiple customizations to co-exist. For example, if you have multiple
    plugins that set the POM's description from the project description, they work
    OK together if both using the DSL. This plugin was creating another node, which
    resulted in duplicate elements. This feature has been available since Gradle 4.8
    (released 2018-06-04).
  * Enable publishing gradle metadata

## 0.30.0 (2019-04-01)

  * `-SNAPSHOT` plugin version suffix format changed
    from `1.0-SNAPSHOT (private-01/31/1997 11:35-auser)`
    to `1.0-SNAPSHOT (private-1997-01-31T11:35:00Z-auser)`.
    Previously the format was timezone-dependent, had no indication of which timezone
    it was from, and did not conflict resolve correctly. ISO8601 timestamps solve all
    three issues.
  * `jenkinsTest` dependencies now have jars added to `testImplementation` instead of
    `testCompile`. `testCompile` has been deprecated since Gradle 4.7 (released
    2018-04-18).
  * Copy dependencies to desired configurations by using [`Configuration#withDependencies`](https://docs.gradle.org/5.3.1/javadoc/org/gradle/api/artifacts/Configuration.html#withDependencies-org.gradle.api.Action-)
    instead of checking the hierarchy and state of configuration resolution before
    copying. The previous way ended up making the final result dependent on the order
    configurations were resolved in, which wasn't guaranteed by Gradle. This feature
    has been present since Gradle 4.4 (released 2017-12-06).
  * Include a reason on the copied jar dependencies, using the [`because`](https://docs.gradle.org/4.6/release-notes.html#allow-declared-reasons-for-dependency-and-resolution-rules)
    api, present since Gradle 4.6 (released 2018-02-28).
  * updated to Gradle 5.3.1

## 0.29.0 (2019-01-30)

  * skip Jenkins setup wizard on server task ([PR #109](https://github.com/jenkinsci/gradle-jpi-plugin/pull/109))

## 0.28.1 (2018-11-26)

  * fix deprecation warning when running on Gradle 5.0

## 0.28.0 (2018-11-26)

  * updated to Gradle 5.0
  * add `org.jenkins-ci.main:jenkins-core` to `annotationProcessor` config if >= Gradle 4.6
  * drop java 7 testing from CI - Jenkins and Gradle both have Java 8 minimum requirement now

## 0.27.0 (2018-07-05)

  * updated to Gradle 4.8.1
  * plugin is now compatible with Gradle 4.8

## 0.26.0 (2018-04-04)

  * updated to Gradle 4.6
  * bundle license and dependency information
    ([JENKINS-27729](https://issues.jenkins-ci.org/browse/JENKINS-27729))
  * added `jenkinsServer` configuration that can be used to install extra plugins for the `server` task
  * the `junit:junit-dep:4.10` dependency is no longer added to `jenkinsTest` configuration for core versions >= 1.505
    ([JENKINS-48353](https://issues.jenkins-ci.org/browse/JENKINS-48353))
  * use Jenkins Test Harness 2.31 for core versions greater than 2.63
    ([JENKINS-47988](https://issues.jenkins-ci.org/browse/JENKINS-47988))

## 0.25.0 (2018-01-15)

  * allow to override `repoUrl` and `snapshotRepoUrl` settings from the command line
    ([JENKINS-45588](https://issues.jenkins-ci.org/browse/JENKINS-45588))

## 0.24.0 (2017-11-02)

  * fixed support for core versions >= 2.64
    ([JENKINS-46899](https://issues.jenkins-ci.org/browse/JENKINS-46899))

## 0.23.1 (2017-10-10)

  * updated to Gradle 3.5.1
  * fixed incremental build
    ([JENKINS-45126](https://issues.jenkins-ci.org/browse/JENKINS-45126))
  * fixed transitive plugin dependency resolution, plugin dependencies must not longer be specified with artifact only
    notation
    ([JENKINS-35412](https://issues.jenkins-ci.org/browse/JENKINS-35412))

## 0.22.0 (2017-02-23)

  * fixed compatibility with Gradle 3.4
  * removed the classes `org.jenkinsci.gradle.plugins.jpi.Jpi` and `org.jenkinsci.gradle.plugins.jpi.JpiComponent` as
    they are no longer used by the plugin
  * the `jpi` task has been replaced by the standard `war` task and changed to a no-op tasks that depends on the `war`
    task, use the `war` task to customize the JPI/HPI archive

## 0.21.0 (2016-12-02)

  * updated to Gradle 3.2.1
  * strip version from plugin file names in `test-dependencies` directory to mimic the behavior of the Maven HPI plugin
    better
  * allow to configure licenses for the generated POM
    ([#83](https://github.com/jenkinsci/gradle-jpi-plugin/pull/83))

## 0.20.0 (2016-11-17)

  * updated to Gradle 3.2
  * dropped support for building with Java 6

## 0.19.0 (2016-11-16)

  * do not apply "maven-publish" plugin if `configurePublishing` is `false`
    ([#80](https://github.com/jenkinsci/gradle-jpi-plugin/pull/80))
  * fixed problem with missing `Plugin-Class` attribute in generated manifest
    ([JENKINS-38920](https://issues.jenkins-ci.org/browse/JENKINS-38920))

## 0.18.1 (2016-05-22)

  * Fixed Servlet API dependency for Jenkins 2.0 and later
    ([JENKINS-34945](https://issues.jenkins-ci.org/browse/JENKINS-34945))

## 0.18.0 (2016-05-18)

  * removed reference to parent POM from generated POM and declare relevant dependencies directly
    ([JENKINS-34874](https://issues.jenkins-ci.org/browse/JENKINS-34874))

## 0.17.0 (2016-04-26)

  * updated Gradle to version 2.13
  * copy plugin dependencies to `test-dependencies` directory instead of `plugins` directory to mimic the behavior of
    the Maven HPI plugin
    ([#74](https://github.com/jenkinsci/gradle-jpi-plugin/pull/74))

## 0.16.0 (2016-03-22)

  * updated Gradle to version 2.8
  * fixed a classpath problem in the `localizer` task
    ([#71](https://github.com/jenkinsci/gradle-jpi-plugin/pull/71))
  * allow to specify the HTTP port for the `server` task with the `jenkins.httpPort` project or system property
    ([JENKINS-31881](https://issues.jenkins-ci.org/browse/JENKINS-31881))
  * changed default repository URL to `https://repo.jenkins-ci.org/releases`
  * changed default snapshot repository URL to `https://repo.jenkins-ci.org/snapshots`
  * use HTTPS URLs for `repo.jenkins-ci.org`

## 0.15.0 (2016-02-18)

  * updated Gradle to version 2.5
  * removed the `stapler` task and the `staplerStubDir` property, Gradle will generate stubs for annotation processors
    (see [Support for “annotation processing” of Groovy code](https://docs.gradle.org/2.4/release-notes#support-for-%E2%80%9Cannotation-processing%E2%80%9D-of-groovy-code))
  * set outputs for `insertTest` and `generate-test-hpl` tasks to fix problems with incremental builds
  * fixed injected test suite to avoid compile time warnings

## 0.14.3 (2016-02-17)

  * make [SezPoz](https://github.com/jglick/sezpoz) quiet by default, use `--info` or `--debug` to get output

## 0.14.2 (2016-02-16)

  * use Jenkins Test Harness 2.0 for core versions greater than 1.644
    ([JENKINS-32478](https://issues.jenkins-ci.org/browse/JENKINS-32478))

## 0.14.1 (2015-11-27)

  * added `Extension-Name` entry to `MANIFEST.MF`
    ([JENKINS-31542](https://issues.jenkins-ci.org/browse/JENKINS-31542))

## 0.14.0 (2015-11-13)

  * copy HPI/JPI dependencies from {{jenkinsTest}} configuration to {{plugin}} folder on test classpath
    ([JENKINS-31451](https://issues.jenkins-ci.org/browse/JENKINS-31451))

## 0.13.1 (2015-11-05)

  * removed install script
  * fixed a regression introduced in 0.13.0 which causes the manifest to be empty
    ([JENKINS-31426](https://issues.jenkins-ci.org/browse/JENKINS-31426))

## 0.13.0 (2015-10-27)

  * fixed a problem with incremental builds
    ([JENKINS-31186](https://issues.jenkins-ci.org/browse/JENKINS-31186))
  * fixed StackOverflowError when using Gradle 2.8
    ([JENKINS-31188](https://issues.jenkins-ci.org/browse/JENKINS-31188))

## 0.12.2 (2015-08-17)

  * allow to override system properties for the embedded Jenkins instance which is started by the `server` task
    ([JENKINS-29297](https://issues.jenkins-ci.org/browse/JENKINS-29297))

## 0.12.1 (2015-06-17)

  * added a dependency from the `assemble` to the `jpi` task to hook into the standard lifecycle

## 0.12.0 (2015-06-11)

  * allow JPI/HPI file extension and packaging type to be configured by the `fileExtension` property, which will default
    to `hpi`
    ([JENKINS-28408](https://issues.jenkins-ci.org/browse/JENKINS-28408))

## 0.11.1 (2015-05-08)

  * changed packaging in generated POM back to jpi to fix a regression bug
    ([JENKINS-28305](https://issues.jenkins-ci.org/browse/JENKINS-28305))

## 0.11.0 (2015-04-28)

  * add manifest to the plugin JAR file
    ([JENKINS-27994](https://issues.jenkins-ci.org/browse/JENKINS-27994))
  * removed `jenkinsCore` dependencies from generated POM because those are inherited from the parent POM
  * removed runtime scope from plugin and compile dependencies in generated POM
  * mark optional plugins in generated POM
  * changed packaging in generated POM to hpi for compatibility with maven-hpi-plugin

## 0.10.2 (2015-04-01)

  * copy plugin dependencies to test resources output directory to be able to use `@WithPlugin` in tests

## 0.10.1 (2015-03-23)

  * localize only `Message.properties` files
    ([JENKINS-27451](https://issues.jenkins-ci.org/browse/JENKINS-27451))

## 0.10.0 (2015-02-28)

  * renamed the `localizerDestDir` option to `localizerOutputDir`, changed its type to `Object` and fixed the
    configuration to recognize a non-default value
  * add a JAR dependency for each HPI/JPI dependency to the `testCompile` configuration
    ([JENKINS-17129](https://issues.jenkins-ci.org/browse/JENKINS-17129))
  * added `configureRepositories` option to be able to skip configuration of repositories
    ([JENKINS-17130](https://issues.jenkins-ci.org/browse/JENKINS-17130))
  * added `configurePublishing` option to be able to skip configuration of publications or repositories for the Maven
    Publishing plugin

## 0.9.1 (2015-02-17)

  * use classpath from jpi task to build classpath for server task to allow customization in build scripts
    ([JENKINS-26377](https://issues.jenkins-ci.org/browse/JENKINS-26377))
  * the `runtimeClasspath` read-only property in `org.jenkinsci.gradle.plugins.jpi.JpiExtension` has been removed

## 0.9.0 (2015-02-16)

  * added task to inject tests for checking the syntax of Jelly and other things
    ([JENKINS-12193](https://issues.jenkins-ci.org/browse/JENKINS-12193))
  * updated Gradle to version 2.3
  * publish the plugin JAR
    ([JENKINS-25007](https://issues.jenkins-ci.org/browse/JENKINS-25007))

## 0.8.1 (2015-01-28)

  * create `target` directory (for coreVersions >= 1.545 and < 1.592), clean `target` directory (for coreVersions
    < 1.598) and set `buildDirectory` system property for Jenkins test harness
    ([JENKINS-26331](https://issues.jenkins-ci.org/browse/JENKINS-26331))

## 0.8.0 (2015-01-06)

  * support Java 8
    ([JENKINS-25643](https://issues.jenkins-ci.org/browse/JENKINS-25643))
  * updated Gradle to version 2.2.1
  * migrated to the maven-publish plugin
    * use the `publish` and `publishToMavenLocal` tasks for publishing, the `install`, `deploy` and `uploadArchives`
      tasks are no longer available
    * all dependencies in the generated POM have the runtime scope

## 0.7.2 (2014-12-19)

  * re-added the SCM `connection` element in the generated POM to fix a regression with Jenkins Update Center showing
    the wrong SCM URL

## 0.7.1 (2014-11-04)

  * fixed regression that caused libraries not to be included in the JPI file
    ([JENKINS-25401](https://issues.jenkins-ci.org/browse/JENKINS-25401))
  * dependencies from the `groovy` configuration are no longer excluded from the JPI archive, use the `providedCompile`
    configuration or the transitive dependencies from Jenkins core instead

## 0.7.0 (2014-10-23)

  * updated Gradle to version 1.12
  * added support for `Plugin-Developers` manifest attribute
  * added support for `Support-Dynamic-Loading` manifest attribute
  * set `stapler.jelly.noCache` system property to `true` when running `server` task
  * removed `v` attribute from manifest
  * copy plugin dependencies from `jenkinsPlugins` configuration into working directory when running `server` task
    ([JENKINS-25219](https://issues.jenkins-ci.org/browse/JENKINS-25219))
  * ignore non-existing source directories in `localizer` task
  * ignore non-existing library paths in `server` task
  * the value set for the `snapshotRepoUrl` option is no longer ignored
  * added `findbugs:annotations:1.0.0` to `jenkinsCore` configuration to avoid compiler warnings
    ([JENKINS-14400](https://issues.jenkins-ci.org/browse/JENKINS-14400))
  * the Maven `connection` and `developerConnection` SCM information is no longer generated into the POM
  * removed `gitHubSCMConnection` and `gitHubSCMDevConnection` read-only options
  * replaced usages of deprecated `groovy` configuration by `compile` configuration
  * added a missing setter for `shortName`
  * added `org.jenkins-ci.jpi` as alternative qualified plugin id for Gradle plugin portal inclusion
  * removed `WEB_APP_GROUP` constant from `org.jenkinsci.gradle.plugins.jpi.JpiPlugin`
  * changed visibility of `org.jenkinsci.gradle.plugins.jpi.JpiPlugin.configureConfigurations` to private
  * `jpiDeployUser` and `jpiDeployPassword` properties from `org.jenkinsci.gradle.plugins.jpi.JpiExtension` were not
    used and have been removed
  * `org.jenkinsci.gradle.plugins.jpi.JpiPluginConvention` was not used and has been removed

## 0.6.0 (2014-10-01)

  * do not exclude org.jenkins-ci.modules:instance-identity from jenkinsTest configuration
    ([JENKINS-23603](https://issues.jenkins-ci.org/browse/JENKINS-23603))
  * set up the build to use http://repo.jenkins-ci.org/public/ similar to the Maven POMs
    ([JENKINS-19942](https://issues.jenkins-ci.org/browse/JENKINS-19942))
  * register input files for the localizer task to avoid that the task is skipped when the inputs change
    ([JENKINS-24298](https://issues.jenkins-ci.org/browse/JENKINS-24298))
  * added pluginFirstClassLoader attribute
    ([JENKINS-24808](https://issues.jenkins-ci.org/browse/JENKINS-24808))
  * removed deprecation warnings by using newer API which has been introduced in Gradle 1.6

## 0.5.2 (unreleased)

  * use ui-samples-plugin version 2.0 in jenkinsTests configuration when using Jenkins core 1.533 or later
    ([JENKINS-21431](https://issues.jenkins-ci.org/browse/JENKINS-21431))
