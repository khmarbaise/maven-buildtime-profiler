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

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
class GoalTimerTest {

  @Test
  void mojoStopShouldFailWithIllegalArgumentException() {
    GoalTimer t = new GoalTimer();

    t.mojoStart(createEvent("Anton"));

    assertThatIllegalArgumentException()
        .isThrownBy(() -> t.mojoStop(createEvent("Egon")))
        .withMessage("Unknown mojoId (execution-groupId:execution-artifactId:execution-version" +
                     ":execution-goal (Egon))");
  }

  private ExecutionEvent createEvent(String id) {
    MavenProject project = mock(MavenProject.class);
    when(project.getGroupId()).thenReturn("groupId");
    when(project.getArtifactId()).thenReturn("artifactId");
    when(project.getVersion()).thenReturn("version");

    ExecutionEvent event = mock(ExecutionEvent.class);
    MojoExecution execution = mock(MojoExecution.class);
    when(execution.getGroupId()).thenReturn("execution-groupId");
    when(execution.getArtifactId()).thenReturn("execution-artifactId");
    when(execution.getExecutionId()).thenReturn(id);
    when(execution.getGoal()).thenReturn("execution-goal");
    when(execution.getVersion()).thenReturn("execution-version");

    when(event.getMojoExecution()).thenReturn(execution);
    when(event.getProject()).thenReturn(project);
    return event;
  }
}
