package com.soebes.maven.extensions;

import org.apache.maven.project.MavenProject;

import java.util.Properties;

final class MavenVersion {
  static String getMavenVersion() {
    try {
      // This relies on the fact that MavenProject is the in core classloader
      // and that the core classloader is for the maven-core artifact
      // and that should have a pom.properties file
      // if this ever changes, we will have to revisit this code.
      Properties properties = new Properties();
      properties.load(MavenProject.class
          .getClassLoader()
          .getResourceAsStream("META-INF/maven/org.apache.maven/maven-core/pom.properties"));
      return properties.getProperty("version", "unknown").trim();
    } catch (Exception e) {
      return null;
    }
  }

}
