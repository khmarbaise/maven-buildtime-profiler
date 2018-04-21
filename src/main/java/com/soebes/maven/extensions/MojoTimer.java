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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
public class MojoTimer
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private Map<ProjectMojo, SystemTime> timerEvents;

    public MojoTimer()
    {
        this.timerEvents = new ConcurrentHashMap<>();
    }

    private ProjectKey createProjectKey( MavenProject project )
    {
        return new ProjectKey( project.getGroupId(), project.getArtifactId(), project.getVersion() );
    }

    private MojoKey createMojoKey( MojoExecution mojo )
    {
        return new MojoKey( mojo.getGroupId(), mojo.getArtifactId(), mojo.getVersion(), mojo.getGoal(),
                            mojo.getExecutionId(), mojo.getLifecyclePhase() );
    }

    public boolean hasEvents()
    {
        return !this.timerEvents.isEmpty();
    }

    public void mojoStart( ExecutionEvent event )
    {

        ProjectMojo pm =
            new ProjectMojo( createProjectKey( event.getProject() ), createMojoKey( event.getMojoExecution() ) );
        timerEvents.put( pm, new SystemTime().start() );
    }

    public void mojoStop( ExecutionEvent event )
    {
        ProjectMojo pm =
            new ProjectMojo( createProjectKey( event.getProject() ), createMojoKey( event.getMojoExecution() ) );
        if ( !timerEvents.containsKey( pm ) )
        {
            throw new IllegalArgumentException( "Unknown mojoId (" + pm + ")" );
        }
        timerEvents.get( pm ).stop();
    }

    public long getTimeForPhaseInMillis( String phase )
    {
        long result = 0;

        for ( Entry<ProjectMojo, SystemTime> item : this.timerEvents.entrySet() )
        {
            if ( phase.equals( item.getKey().getMojo().getPhase() ) )
            {
                result += item.getValue().getElapsedTime();
            }
        }
        return result;
    }

    public Map<ProjectMojo, SystemTime> getPluginsInPhase( String phase )
    {
        Map<ProjectMojo, SystemTime> result = new LinkedHashMap<ProjectMojo, SystemTime>();

        for ( Entry<ProjectMojo, SystemTime> item : this.timerEvents.entrySet() )
        {
            if ( phase.equals( item.getKey().getMojo().getPhase() ) )
            {
                result.put( item.getKey(), item.getValue() );
            }
        }
        return result;
    }

    public boolean hasTimeForProjectAndPhase( ProjectKey proKey, String phase )
    {
        boolean result = false;
        for ( Entry<ProjectMojo, SystemTime> item : this.timerEvents.entrySet() )
        {
            if ( item.getKey().getProject().equals( proKey ) && phase.equals( item.getKey().getMojo().getPhase() ) )
            {
                result = true;
            }
        }
        return result;
    }

    public long getTimeForProjectAndPhaseInMillis( ProjectKey proKey, String phase )
    {
        long result = 0;

        for ( Entry<ProjectMojo, SystemTime> item : this.timerEvents.entrySet() )
        {
            if ( item.getKey().getProject().equals( proKey ) && phase.equals( item.getKey().getMojo().getPhase() ) )
            {
                result += item.getValue().getElapsedTime();
            }
        }

        return result;
    }

    public void report()
    {
        for ( Entry<ProjectMojo, SystemTime> item : this.timerEvents.entrySet() )
        {
            LOGGER.info( "{} : {}", item.getKey().getId(), item.getValue().getElapsedTime() );
        }
    }
}
