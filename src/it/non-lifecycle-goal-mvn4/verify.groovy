import org.apache.maven.lifecycle.LifecycleNotFoundException

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

def targetDirectory = new File (basedir, "target")
if (!targetDirectory.exists()) {
    throw new FileNotFoundException("target directory does not exists.")
}

def logFile = buildLogFile.text

if (!logFile.contains ( '[INFO] BUILD SUCCESS') ) {
    throw new IllegalStateException("Build is not successful.")
}

if (!logFile.contains ( '[INFO] Project Build Time (reactor order):') ) {
    throw new IllegalStateException("build measurement is not active.")
}

if (!logFile.contains ( '[INFO] non-lifecycle-goal-pom:' ) ) {
    throw new IllegalStateException("Recording does not work.")
}

if (!logFile.contains ( ' ms : clean' ) ) {
    throw new LifecycleNotFoundException("clean life cycle is missing.")
}
if (!logFile.contains ( ' ms : after:resources' ) ) {
    throw new LifecycleNotFoundException("after:resources life cycle is missing.")
}
if (!logFile.contains ( ' ms : compile' ) ) {
    throw new LifecycleNotFoundException("compile life cycle is missing.")
}
if (!logFile.contains ( ' ms : after:test-resources' ) ) {
    throw new LifecycleNotFoundException("after:test-resources life cycle is missing.")
}
if (!logFile.contains ( ' ms : test-compile' ) ) {
    throw new LifecycleNotFoundException("test-compile life cycle is missing.")
}
if (!logFile.contains ( ' ms : test' ) ) {
    throw new LifecycleNotFoundException("test life cycle is missing.")
}
if (!logFile.contains ( ' ms : package' ) ) {
    throw new LifecycleNotFoundException("package life cycle is missing.")
}
if (!logFile.contains ( ' ms : install' ) ) {
    throw new LifecycleNotFoundException("install life cycle is missing.")
}
if (!logFile.contains ( ' ms : site' ) ) {
    throw new LifecycleNotFoundException("site life cycle is missing.")
}

if (!logFile.contains ( '[INFO] Lifecycle Phase summary:' ) ) {
    throw new IllegalStateException("Phase summary is missing.")
}

if (!logFile.contains ( '[INFO] ForkTime: 0' ) ) {
    throw new IllegalStateException("ForkTime 0 is missing.")
}


