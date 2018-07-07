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
class MojoKey
    extends ProjectKey
{
    private String goal;

    private String executionId;

    private String phase;

    public MojoKey( String groupId, String artifactId, String version, String goal, String executionId,
                    String lifeCyclePhase )
    {
        super( groupId, artifactId, version );
        this.goal = goal;
        this.executionId = executionId;
        this.phase = lifeCyclePhase;
    }

    public String getGoal()
    {
        return goal;
    }

    public void setGoal( String goal )
    {
        this.goal = goal;
    }

    public String getExecutionId()
    {
        return executionId;
    }

    public void setExecutionId( String executionId )
    {
        this.executionId = executionId;
    }

    public String getPhase()
    {
        return phase;
    }

    public void setPhase( String phase )
    {
        this.phase = phase;
    }

    public String getFullId()
    {
        return super.getId() + ":" + getGoal() + " (" + getExecutionId() + ")";
    }

    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( executionId == null ) ? 0 : executionId.hashCode() );
        result = prime * result + ( ( goal == null ) ? 0 : goal.hashCode() );
        result = prime * result + ( ( phase == null ) ? 0 : phase.hashCode() );
        return result;
    }

    public final boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( !super.equals( obj ) )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        MojoKey other = (MojoKey) obj;
        if ( executionId == null )
        {
            if ( other.executionId != null )
                return false;
        }
        else if ( !executionId.equals( other.executionId ) )
            return false;
        if ( goal == null )
        {
            if ( other.goal != null )
                return false;
        }
        else if ( !goal.equals( other.goal ) )
            return false;
        if ( phase == null )
        {
            if ( other.phase != null )
                return false;
        }
        else if ( !phase.equals( other.phase ) )
            return false;
        return true;
    }

}