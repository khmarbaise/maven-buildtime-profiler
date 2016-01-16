package com.soebes.maven.extensions;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void projectStart( ExecutionEvent event, SystemTime systemTime )
    {
        String projectId = getProjectId( event.getProject() );
        timerEvents.put( projectId, systemTime );
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
//        LOGGER.info( " Project: {}", projectId );
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
