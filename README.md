# Maven BuildTime Profiler

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/khmarbaise/maven-buildtime-profiler.svg?label=License)](http://www.apache.org/licenses/)
[![Maven Central](https://img.shields.io/maven-central/v/com.soebes.maven.extensions/maven-buildtime-profiler.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.soebes.maven.extensions%22%20a%3A%22maven-buildtime-profiler%22)

Often you have the problem that in large builds, you need to find
where time is consumed.

This is an [EventSpy][1] implementation which collects all the information of
all phases and mojo executions and make a summarization output at the end of
the build.

## Minimum Requirements
This extension will only work for Maven 3.3.1+ (>= 0.5.0) and requires JDK8+

## Configuration
If you like to use this extension for Maven you have to define the following `.mvn/extensions.xml` file:

``` xml
<extensions xmlns="http://maven.apache.org/EXTENSIONS/1.1.0"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://maven.apache.org/EXTENSIONS/1.1.0 http://maven.apache.org/xsd/core-extensions-1.1.0.xsd">
  <extension>
    <groupId>com.soebes.maven.extensions</groupId>
    <artifactId>maven-buildtime-profiler</artifactId>
    <version>0.5.0</version>
  </extension>
</extensions>
```
If you don't like to add the `.mvn/extensions.xml` to your project you can also go via command line like this:
```
mvn verify -Dmaven.ext.class.path=$HOME/.m2/repository/com/soebes/maven/extensions/maven-buildtime-profiles/0.5.0/maven-buildtime-profiler-0.5.0.jar
```

The download from Maven Central will be done by Maven itself.

### Deactivation
If you like to deactivate the extension without removing the `.mvn/extensions.xml` file this can easily achieved, 
by adding the following to your command line (system property):
```
mvn -Dmaven-build-time-profiler.disabled verify 
```
That means only the following lines will appear in your console output:
```text
[INFO] Maven Build Time Profiler started. (Version 0.5.0)
[INFO] Maven Build Time Profiler deactivated.
```
That will give you the explicit hint that the extension has been deactivated.

Hint: It is **NOT** possible to deactivate the extension via properties from your `pom.xml` file!


## Example Output
Here's an example of what the output will look like:

```
[INFO] ------------------------------------------------------------------------
[INFO] --             Maven Build Time Profiler Summary                      --
[INFO] ------------------------------------------------------------------------
[INFO] Project discovery time:       85 ms
[INFO] ------------------------------------------------------------------------
[INFO] Project Build Time (reactor order):
[INFO]
[INFO] parse-pom:
[INFO]          169 ms : clean
[INFO]          457 ms : process-resources
[INFO]          663 ms : compile
[INFO]           14 ms : process-test-resources
[INFO]          197 ms : test-compile
[INFO]         1109 ms : test
[INFO]         1105 ms : package
[INFO]          811 ms : integration-test
[INFO]           53 ms : verify
[INFO] ------------------------------------------------------------------------
[INFO] Lifecycle Phase summary:
[INFO]
[INFO]      169 ms : clean
[INFO]      457 ms : process-resources
[INFO]      663 ms : compile
[INFO]       14 ms : process-test-resources
[INFO]      197 ms : test-compile
[INFO]     1109 ms : test
[INFO]     1105 ms : package
[INFO]      811 ms : integration-test
[INFO]       53 ms : verify
[INFO] ------------------------------------------------------------------------
[INFO] Plugins in lifecycle Phases:
[INFO]
[INFO] clean:
[INFO]      169 ms: org.apache.maven.plugins:maven-clean-plugin:3.0.0:clean (default-clean)
[INFO] process-resources:
[INFO]      457 ms: org.apache.maven.plugins:maven-resources-plugin:2.7:resources (default-resources)
[INFO] compile:
[INFO]      663 ms: org.apache.maven.plugins:maven-compiler-plugin:3.3:compile (default-compile)
[INFO] process-test-resources:
[INFO]       14 ms: org.apache.maven.plugins:maven-resources-plugin:2.7:testResources (default-testResources)
[INFO] test-compile:
[INFO]      197 ms: org.apache.maven.plugins:maven-compiler-plugin:3.3:testCompile (default-testCompile)
[INFO] test:
[INFO]     1109 ms: org.apache.maven.plugins:maven-surefire-plugin:2.18.1:test (default-test)
[INFO] package:
[INFO]      871 ms: org.apache.maven.plugins:maven-assembly-plugin:2.6:single (make-executable-jar)
[INFO]      234 ms: org.apache.maven.plugins:maven-jar-plugin:2.6:jar (default-jar)
[INFO] integration-test:
[INFO]      811 ms: org.apache.maven.plugins:maven-failsafe-plugin:2.18.1:integration-test (failsafe-integration-test)
[INFO] verify:
[INFO]       53 ms: org.apache.maven.plugins:maven-failsafe-plugin:2.18.1:verify (failsafe-verify)
[INFO] ------------------------------------------------------------------------
[INFO] ForkTime: 0
```

Most important might be in this relationship that you will get also information about the 
time taken to install artifacts:

```
[INFO] ------------------------------------------------------------------------
[INFO] Installation summary:
[INFO]        0 ms : com.soebes.examples.j2ee:service:1.1.2-SNAPSHOT:pom
[INFO]        1 ms : com.soebes.examples.j2ee:parent:1.1.2-SNAPSHOT:pom
[INFO]        5 ms : com.soebes.examples.j2ee:shade:1.1.2-SNAPSHOT:test:jar
[INFO]        1 ms : com.soebes.examples.j2ee:service-client:1.1.2-SNAPSHOT:pom
[INFO]        0 ms : com.soebes.examples.j2ee:domain:1.1.2-SNAPSHOT:pom
[INFO]        3 ms : com.soebes.examples.j2ee:webgui:1.1.2-SNAPSHOT:pom
[INFO]        1 ms : com.soebes.examples.j2ee:service:1.1.2-SNAPSHOT:jar
[INFO]        0 ms : com.soebes.examples.j2ee:service-client:1.1.2-SNAPSHOT:jar
[INFO]        1 ms : com.soebes.examples.j2ee:domain:1.1.2-SNAPSHOT:jar
[INFO]       10 ms : com.soebes.examples.j2ee:assembly:1.1.2-SNAPSHOT:archive:zip
[INFO]       19 ms : com.soebes.examples.j2ee:assembly:1.1.2-SNAPSHOT:prod:jar
[INFO]        6 ms : com.soebes.examples.j2ee:shade:1.1.2-SNAPSHOT:dev:jar
[INFO]        6 ms : com.soebes.examples.j2ee:shade:1.1.2-SNAPSHOT:prod:jar
[INFO]        1 ms : com.soebes.examples.j2ee:shade:1.1.2-SNAPSHOT:pom
[INFO]        6 ms : com.soebes.examples.j2ee:assembly:1.1.2-SNAPSHOT:dev:jar
[INFO]        1 ms : com.soebes.examples.j2ee:assembly:1.1.2-SNAPSHOT:pom
[INFO]        0 ms : com.soebes.examples.j2ee:appasm:1.1.2-SNAPSHOT:pom
[INFO]        2 ms : com.soebes.examples.j2ee:webgui:1.1.2-SNAPSHOT:war
[INFO]        8 ms : com.soebes.examples.j2ee:app:1.1.2-SNAPSHOT:pom
[INFO]        1 ms : com.soebes.examples.j2ee:shade:1.1.2-SNAPSHOT:jar
[INFO]        3 ms : com.soebes.examples.j2ee:app:1.1.2-SNAPSHOT:ear
[INFO] 75 ms  34,888,754 bytes. 443.633 MiB / s
```

And much more important are things like the time for the deployment:

```
[INFO] ------------------------------------------------------------------------
[INFO] Deployment summary:
[INFO]       19 ms : com.soebes.examples.j2ee:assembly:1.1.2-20160306.145402-11:pom
[INFO]       20 ms : com.soebes.examples.j2ee:service-client:1.1.2-20160306.145401-11:jar
[INFO]       80 ms : com.soebes.examples.j2ee:assembly:1.1.2-20160306.145402-11:prod:jar
[INFO]       20 ms : com.soebes.examples.j2ee:shade:1.1.2-20160306.145402-11:pom
[INFO]       19 ms : com.soebes.examples.j2ee:shade:1.1.2-20160306.145402-11:jar
[INFO]       84 ms : com.soebes.examples.j2ee:webgui:1.1.2-20160306.145401-11:war
[INFO]       20 ms : com.soebes.examples.j2ee:service:1.1.2-20160306.145401-11:jar
[INFO]      192 ms : com.soebes.examples.j2ee:assembly:1.1.2-20160306.145402-11:archive:zip
[INFO]       79 ms : com.soebes.examples.j2ee:shade:1.1.2-20160306.145402-11:prod:jar
[INFO]       20 ms : com.soebes.examples.j2ee:webgui:1.1.2-20160306.145401-11:pom
[INFO]       23 ms : com.soebes.examples.j2ee:service-client:1.1.2-20160306.145401-11:pom
[INFO]       90 ms : com.soebes.examples.j2ee:shade:1.1.2-20160306.145402-11:dev:jar
[INFO]       38 ms : com.soebes.examples.j2ee:domain:1.1.2-20160306.145401-11:jar
[INFO]       19 ms : com.soebes.examples.j2ee:appasm:1.1.2-20160306.145402-11:pom
[INFO]       18 ms : com.soebes.examples.j2ee:app:1.1.2-20160306.145401-11:pom
[INFO]      103 ms : com.soebes.examples.j2ee:assembly:1.1.2-20160306.145402-11:dev:jar
[INFO]       39 ms : com.soebes.examples.j2ee:parent:1.1.2-20160306.145401-11:pom
[INFO]      101 ms : com.soebes.examples.j2ee:shade:1.1.2-20160306.145402-11:test:jar
[INFO]       93 ms : com.soebes.examples.j2ee:app:1.1.2-20160306.145401-11:ear
[INFO]       19 ms : com.soebes.examples.j2ee:service:1.1.2-20160306.145401-11:pom
[INFO]       18 ms : com.soebes.examples.j2ee:domain:1.1.2-20160306.145401-11:pom
[INFO] 1,114 ms  34,888,754 bytes. 29.868 MiB / s
```

If you have ideas for improvements etc. just fill in issues in the tracking system.

[1]: https://maven.apache.org/ref/3.3.1/maven-core/apidocs/org/apache/maven/eventspy/AbstractEventSpy.html
