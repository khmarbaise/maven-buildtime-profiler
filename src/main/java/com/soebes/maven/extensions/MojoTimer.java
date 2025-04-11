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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
class MojoTimer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(MojoTimer.class);

    private final Map<ProjectMojo, SystemTime> timerEvents;

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
        return this.timerEvents.entrySet().stream()
            .filter( mojoPhase(phase))
            .mapToLong( s -> s.getValue().getElapsedTime())
            .sum();
    }

    public Map<ProjectMojo, SystemTime> getPluginsInPhase( String phase )
    {
        return this.timerEvents.entrySet().stream()
            .filter(mojoPhase(phase))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
    }

    private static Predicate<Entry<ProjectMojo, SystemTime>> mojoPhase(String phase) {
        return item -> item.getKey().getMojo().getPhase().equals(phase);
    }
    private static Predicate<Entry<ProjectMojo, SystemTime>> projectKey(ProjectKey projectKey) {
        return item -> item.getKey().getProject().equals( projectKey );
    }

    public boolean hasTimeForProjectAndPhase( ProjectKey proKey, String phase )
    {
        return this.timerEvents.entrySet().stream()
            .anyMatch(mojoPhase(phase).and(projectKey(proKey)));
    }

    public long getTimeForProjectAndPhaseInMillis( ProjectKey proKey, String phase )
    {
        return this.timerEvents.entrySet().stream()
            .filter( mojoPhase(phase))
            .filter( projectKey(proKey))
            .mapToLong( s -> s.getValue().getElapsedTime())
            .sum();
    }

    public void report()
    {
        for ( Entry<ProjectMojo, SystemTime> item : this.timerEvents.entrySet() )
        {
            LOGGER.info( "{} : {}", item.getKey().getId(), item.getValue().getElapsedTime() );
        }
    }
}
