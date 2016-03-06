# Maven BuildTime Profiler

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/khmarbaise/maven-buildtime-profiler.svg?label=License)](http://www.apache.org/licenses/)
[![Maven Central](https://img.shields.io/maven-central/v/com.soebes.extensions/maven-buildtime-profiler.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cmaven-buildtime-profiler)
[![Build Status](https://travis-ci.org/mojohaus/sql-maven-plugin.svg?branch=master)](https://travis-ci.org/khmarbaise/maven-buildtime-profiler)

Often you have the problem that in large builds you need to find
where time is consumed.

This is an [EventSpy][1] implementation which collects all the information of
all phases and mojo executions and make a summarization output at the end of
the build.

If you like to use this EventSpy you need to put the resulting jar
file of this project into the `${M2_HOME}/lib/ext` directory.


If you like to use this extension in relationship with Maven 3.3.1+ you
can define the following `.mvn/extensions.xml` file:

``` xml
<extensions xmlns="http://maven.apache.org/EXTENSIONS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/EXTENSIONS/1.0.0 http://maven.apache.org/xsd/core-extensions-1.0.0.xsd">
  <extension>
    <groupId>com.soebes.maven.extensions</groupId>
    <artifactId>maven-buildtime-profiler</artifactId>
    <version>0.1.0</version>
  </extension>
</extensions>
```

The download from Maven Central will be done by Maven itself.

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
[INFO]      169 ms: org.apache.maven.plugins:maven-clean-plugin:3.0.0:clean:default-clean
[INFO] process-resources:
[INFO]      457 ms: org.apache.maven.plugins:maven-resources-plugin:2.7:resources:default-resources
[INFO] compile:
[INFO]      663 ms: org.apache.maven.plugins:maven-compiler-plugin:3.3:compile:default-compile
[INFO] process-test-resources:
[INFO]       14 ms: org.apache.maven.plugins:maven-resources-plugin:2.7:testResources:default-testResources
[INFO] test-compile:
[INFO]      197 ms: org.apache.maven.plugins:maven-compiler-plugin:3.3:testCompile:default-testCompile
[INFO] test:
[INFO]     1109 ms: org.apache.maven.plugins:maven-surefire-plugin:2.18.1:test:default-test
[INFO] package:
[INFO]      871 ms: org.apache.maven.plugins:maven-assembly-plugin:2.6:single:make-executable-jar
[INFO]      234 ms: org.apache.maven.plugins:maven-jar-plugin:2.6:jar:default-jar
[INFO] integration-test:
[INFO]      811 ms: org.apache.maven.plugins:maven-failsafe-plugin:2.18.1:integration-test:failsafe-integration-test
[INFO] verify:
[INFO]       53 ms: org.apache.maven.plugins:maven-failsafe-plugin:2.18.1:verify:failsafe-verify
[INFO] ------------------------------------------------------------------------
[INFO] ForkTime: 0
```

Prerequisites minimum for this is Maven 3.1.1 and Java 1.7 as run time.

[1]: http://maven.apache.org/ref/3.0.3/maven-core/apidocs/org/apache/maven/eventspy/AbstractEventSpy.html
