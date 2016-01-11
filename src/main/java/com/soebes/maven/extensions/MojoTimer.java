package com.soebes.maven.extensions;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MojoTimer
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private Map<String, SystemTime> timerEvents;

    public MojoTimer()
    {
        this.timerEvents = new ConcurrentHashMap<>();
    }

    private String getMojoId( MojoExecution mojo )
    {
        return mojo.getGroupId() + ":" + mojo.getArtifactId() + ":" + mojo.getVersion() + ":" + mojo.getGoal() + ":"
            + mojo.getExecutionId();
    }

    public void mojoStart( ExecutionEvent event, SystemTime systemTime )
    {
//        even.getProject();
        String projectId = getMojoId( event.getMojoExecution() );
        timerEvents.put( projectId, systemTime );

        // even.getMojoExecution();
        // even.getProject();
        // even.getSession();
        // even.getType();
    }

    public void mojoStop( ExecutionEvent event )
    {
        String mojoId = getMojoId( event.getMojoExecution() );
        if ( !timerEvents.containsKey( mojoId ) )
        {
            throw new IllegalArgumentException( "Unknown mojoId (" + mojoId + ")" );
        }
        timerEvents.get( mojoId ).stop();
    }

    public void report()
    {
        for ( Entry<String, SystemTime> item : this.timerEvents.entrySet() )
        {
            LOGGER.info( "SessionTimer: {} : {}", item.getKey(), item.getValue().getElapsedTime() );
        }
    }
}
