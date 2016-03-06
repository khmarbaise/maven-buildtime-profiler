# Maven BuildTime Profiler

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/khmarbaise/maven-buildtime-profiler.svg?label=License)](http://www.apache.org/licenses/)
[![Maven Central](https://img.shields.io/maven-central/v/com.soebes.extensions/maven-buildtime-profiler.svg?label=Maven%20Central)](http://search.maven.org/#search%7Cga%7C1%7Cmaven-buildtime-profiler)
[![Build Status](https://travis-ci.org/mojohaus/sql-maven-plugin.svg?branch=master)](https://travis-ci.org/khmarbaise/maven-buildtime-profiler)
You can find the artifacts in [Maven Central](https://repo1.maven.org/maven2/com/soebes/maven/extensions/profiler/test/test-profiler/0.1.0/).

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
...
```

Prerequisites minimum for this is Maven 3.1.1 and Java 1.7 as run time.

[1]: http://maven.apache.org/ref/3.0.3/maven-core/apidocs/org/apache/maven/eventspy/AbstractEventSpy.html
