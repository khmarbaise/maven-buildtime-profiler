package com.soebes.maven.extensions;

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

import org.apache.maven.eventspy.AbstractEventSpy;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
class LifeCycleOrdering
    extends AbstractEventSpy
{
    private final List<String> predefinedPhases;

    private enum Phases {
        PRE_CLEAN("pre-clean"),
        CLEAN("clean"),
        POST_CLEAN("post-clean"),
        VALIDATE("validate"),
        INITIALIZE("initialize"),
        GENERATE_SOURCES("generate-sources"),
        PROCESS_SOURCES("process-sources"),
        GENERATE_RESOURCES("generate-resources"),
        PROCESS_RESOURCES("process-resources"),
        COMPILE("compile"),
        PROCESS_CLASSES("process-classes"),
        GENERATE_TEST_SOURCES("generate-test-sources"),
        PROCESS_TEST_SOURCES("process-test-sources"),
        GENERATE_TEST_RESOURCES("generate-test-resources"),
        PROCESS_TEST_RESOURCES("process-test-resources"),
        TEST_COMPILE("test-compile"),
        PROCESS_TEST_CLASSES("process-test-classes"),
        TEST("test"),
        PREPARE_PACKAGE("prepare-package"),
        PACKAGE("package"),
        PRE_INTEGRATION_TEST("pre-integration-test"),
        INTEGRATION_TEST("integration-test"),
        POST_INTEGRATION_TEST("post-integration-test"),
        VERIFY("verify"),
        INSTALL("install"),
        DEPLOY("deploy");

        private final String lifeCyclePhaseName;

        Phases(String lifeCyclePhaseName) {
            this.lifeCyclePhaseName = lifeCyclePhaseName;
        }

        public String getLifeCyclePhaseName() {
            return lifeCyclePhaseName;
        }
    }

    public LifeCycleOrdering()
    {
        this.predefinedPhases = Arrays.stream(Phases.values())
            .map(Phases::getLifeCyclePhaseName)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    public List<String> getPredefinedPhases()
    {
        return predefinedPhases;
    }

    protected void orderLifeCycleOnPreparedOrder( List<String> lifeCyclePhases )
    {
        // Sort the lifeCyclePhases based on the given in predefinedPhases.
        Collections.sort( lifeCyclePhases, (left, right) -> Integer.compare( predefinedPhases.indexOf( left ), predefinedPhases.indexOf( right ) ));
    }

}
