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

import static com.soebes.maven.extensions.ProjectKey.fromMavenProject;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
class ProjectMojo
{
    private ProjectKey project;

    private String projectName;

    private MojoKey mojo;

    public ProjectMojo( MavenProject project, MojoKey mojo )
    {
        super();
        this.project = fromMavenProject( project );
        this.projectName = project.getName();
        this.mojo = mojo;
    }

    public ProjectKey getProject()
    {
        return project;
    }

    public void setProject( ProjectKey project )
    {
        this.project = project;
    }

    public MojoKey getMojo()
    {
        return mojo;
    }

    public void setMojo( MojoKey mojo )
    {
        this.mojo = mojo;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( mojo == null ) ? 0 : mojo.hashCode() );
        result = prime * result + ( ( project == null ) ? 0 : project.hashCode() );
        return result;
    }

    public String getLifecycleId()
    {
        return projectName + "@" + mojo.getFullId();
    }

    public String getFullId()
    {
        return mojo.getFullIdWithPhase() + " @ " + project.getId();
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ProjectMojo other = (ProjectMojo) obj;
        if ( mojo == null )
        {
            if ( other.mojo != null )
                return false;
        }
        else if ( !mojo.equals( other.mojo ) )
            return false;
        if ( project == null )
        {
            if ( other.project != null )
                return false;
        }
        else if ( !project.equals( other.project ) )
            return false;
        return true;
    }

}