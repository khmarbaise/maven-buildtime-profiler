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

if (!logFile.contains ( '[INFO] Maven Build Time Profiler started. (Version ') ) {
    throw new FileNotFoundException("Starting was not shown.")
}

if (!logFile.contains ( '[INFO] Maven Build Time Profiler deactivated.') ) {
    throw new FileNotFoundException("Deactivation has gone wrong.")
}

