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

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
final class GoalKey
    extends ProjectKey {

  private final String goal;

  private final String executionId;

  public GoalKey(final String groupId, final String artifactId, final String version, final String goal, final String executionId) {
    super(groupId, artifactId, version);
    this.goal = goal;
    this.executionId = executionId;
  }

  public String getGoal() {
    return goal;
  }

  public String getExecutionId() {
    return executionId;
  }

  public String getFullId() {
    return super.getId() + ":" + getGoal() + " (" + getExecutionId() + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((executionId == null) ? 0 : executionId.hashCode());
    result = prime * result + ((goal == null) ? 0 : goal.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (!(obj instanceof GoalKey)) {
      return false;
    }
    GoalKey other = (GoalKey) obj;
    if (executionId == null) {
      if (other.executionId != null) {
        return false;
      }
    } else if (!executionId.equals(other.executionId)) {
      return false;
    }
    if (goal == null) {
      if (other.goal != null) {
        return false;
      }
    } else if (!goal.equals(other.goal)) {
      return false;
    }
    return true;
  }

}