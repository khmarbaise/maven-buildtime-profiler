/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

def buildLogFile = new File( basedir, "build.log");

if (!buildLogFile.exists()) {
    throw new FileNotFoundException("build.log does not exists.")
}

def logFile = buildLogFile.text

if (!logFile.contains ( '[INFO] BUILD SUCCESS') ) {
    throw new FileNotFoundException("Build is not successful.")
}

if (!logFile.contains ( '[INFO] Project discovery time: ') ) {
    throw new FileNotFoundException("Project discovery has not been measured.")
}
if (!logFile.contains ( '[INFO] Plugins directly called via goals:') ) {
    throw new FileNotFoundException("Plugins called directly via goal have not been recorded.")
}
if (!logFile.contains ( ' ms : org.apache.maven.plugins:maven-clean-plugin:3.0.0:clean (default-cli)') ) {
    throw new FileNotFoundException("maven-clean-plugin has not been recorded.")
}
