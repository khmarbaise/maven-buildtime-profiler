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

import org.apache.maven.project.MavenProject;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
class ProjectKey
{
    private final String groupId;

    private final String artifactId;

    private final String version;

    public static ProjectKey fromMavenProject(MavenProject project) {
        return new ProjectKey(project.getGroupId(), project.getArtifactId(), project.getVersion());
    }

    public ProjectKey( final String groupId, final String artifactId, final String version )
    {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId()
    {
        return groupId;
    }

    public String getArtifactId()
    {
        return artifactId;
    }

    public String getVersion()
    {
        return version;
    }

    public String getId()
    {
        return this.groupId + ":" + this.artifactId + ":" + this.version;
    }

    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( artifactId == null ) ? 0 : artifactId.hashCode() );
        result = prime * result + ( ( groupId == null ) ? 0 : groupId.hashCode() );
        result = prime * result + ( ( version == null ) ? 0 : version.hashCode() );
        return result;
    }

    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( !( obj instanceof ProjectKey ) )
            return false;
        ProjectKey other = (ProjectKey) obj;
        if ( artifactId == null )
        {
            if ( other.artifactId != null )
                return false;
        }
        else if ( !artifactId.equals( other.artifactId ) )
            return false;
        if ( groupId == null )
        {
            if ( other.groupId != null )
                return false;
        }
        else if ( !groupId.equals( other.groupId ) )
            return false;
        if ( version == null )
        {
            if ( other.version != null )
                return false;
        }
        else if ( !version.equals( other.version ) )
            return false;
        return true;
    }

}