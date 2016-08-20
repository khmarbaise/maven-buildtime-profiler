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

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 *
 */
public class ProjectTimer
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private Map<String, SystemTime> timerEvents;

    public ProjectTimer()
    {
        this.timerEvents = new ConcurrentHashMap<>();
    }

    private String getProjectId( MavenProject mavenProject )
    {
        return mavenProject.getId();
    }

    public void projectStart( ExecutionEvent event )
    {
        String projectId = getProjectId( event.getProject() );
        timerEvents.put( projectId, new SystemTime().start() );
    }

    public void projectStop( ExecutionEvent event )
    {
        String projectId = getProjectId( event.getProject() );
        if ( !timerEvents.containsKey( projectId ) )
        {
            throw new IllegalArgumentException( "Unknown projectId (" + projectId + ")" );
        }
        timerEvents.get( projectId ).stop();
    }

    public long getTimeForProject( MavenProject project )
    {
        String projectId = getProjectId( project );
        if ( !timerEvents.containsKey( projectId ) )
        {
            throw new IllegalArgumentException( "Unknown projectId (" + projectId + ")" );
        }
        return timerEvents.get( projectId ).getElapsedTime();
    }

    public void report()
    {
        for ( Entry<String, SystemTime> item : this.timerEvents.entrySet() )
        {
            LOGGER.info( "ProjectTimer: {} : {}", item.getKey(), item.getValue().getElapsedTime() );
        }
    }
}
