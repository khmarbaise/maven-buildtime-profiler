package com.soebes.maven.extensions;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public void mojoStart( ExecutionEvent event, SystemTime systemTime )
    {
        ProjectMojo pm =
            new ProjectMojo( createProjectKey( event.getProject() ), createMojoKey( event.getMojoExecution() ) );
        timerEvents.put( pm, systemTime );
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

    public void report()
    {
        for ( Entry<ProjectMojo, SystemTime> item : this.timerEvents.entrySet() )
        {
            LOGGER.info( "{} : {}", item.getKey().getId(), item.getValue().getElapsedTime() );
        }
    }
}
