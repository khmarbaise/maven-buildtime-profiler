package com.soebes.maven.extensions;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.plugin.MojoExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.soebes.maven.extensions.ProjectKey.fromMavenProject;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
class GoalTimer
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GoalTimer.class);

    private Map<ProjectGoal, SystemTime> timerEvents;

    public GoalTimer()
    {
        this.timerEvents = new ConcurrentHashMap<>();
    }

    public boolean hasEvents()
    {
        return !timerEvents.isEmpty();
    }

    private GoalKey createGoalKey( MojoExecution mojo )
    {
        return new GoalKey( mojo.getGroupId(), mojo.getArtifactId(), mojo.getVersion(), mojo.getGoal(),
                            mojo.getExecutionId() );
    }

    public void mojoStart( ExecutionEvent event )
    {
        ProjectGoal pm =
            new ProjectGoal( fromMavenProject(event.getProject()), createGoalKey(event.getMojoExecution() ) );
        timerEvents.put( pm, new SystemTime().start() );
    }

    public void mojoStop( ExecutionEvent event )
    {
        ProjectGoal pm =
            new ProjectGoal( fromMavenProject(event.getProject()), createGoalKey(event.getMojoExecution() ) );
        if ( !timerEvents.containsKey( pm ) )
        {
            throw new IllegalArgumentException( "Unknown mojoId (" + pm.getId() + ")" );
        }
        timerEvents.get( pm ).stop();
    }

    public void report()
    {
        for ( Entry<ProjectGoal, SystemTime> item : this.timerEvents.entrySet() )
        {
            LOGGER.info( "{} ms : {}", String.format( "%8d", item.getValue().getElapsedTime() ),
                         item.getKey().getId() );
        }
    }
}
