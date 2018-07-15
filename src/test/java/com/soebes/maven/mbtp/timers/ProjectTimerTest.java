package com.soebes.maven.mbtp.timers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.testng.annotations.Test;

import com.soebes.maven.mbtp.timers.ProjectTimer;

public class ProjectTimerTest
{
    
    @Test
    public void shouldMeasureTheTime()
        throws InterruptedException
    {
        ProjectTimer t = new ProjectTimer();

        ExecutionEvent event = createEvent( "Anton" );

        t.projectStart( event );
        Thread.sleep( 10L );
        t.projectStop( event );

        assertThat( t.getTimeForProject( event.getProject() ) ).isGreaterThanOrEqualTo( 10L );
    }

    @Test( expectedExceptions = {
        IllegalArgumentException.class }, expectedExceptionsMessageRegExp = "Unknown projectId \\(Egon\\)" )
    public void projectStopShouldFailWithIllegalArgumentExceptionBasedOnUnknownProject()
    {
        ProjectTimer t = new ProjectTimer();

        ExecutionEvent event = createEvent( "Anton" );

        t.projectStart( event );

        ExecutionEvent unknownEvent = createEvent( "Egon" );

        t.projectStop( unknownEvent );

        // Intentionally no assertThat(..) cause we expect to get an IllegalArgumentException
    }

    @Test( expectedExceptions = {
        IllegalArgumentException.class }, expectedExceptionsMessageRegExp = "Unknown projectId \\(Anton\\)" )
    public void getTimeForProjectShouldFailWithIllegalArgumentExceptionBasedOnUnknownProject()
    {
        ProjectTimer t = new ProjectTimer();

        ExecutionEvent event = createEvent( "Anton" );

        t.getTimeForProject( event.getProject() );

        // Intentionally no assertThat(..) cause we expect to get an IllegalArgumentException
    }

    private ExecutionEvent createEvent( String id )
    {
        MavenProject project = mock( MavenProject.class );
        when( project.getId() ).thenReturn( id );

        ExecutionEvent event = mock( ExecutionEvent.class );
        when( event.getProject() ).thenReturn( project );
        return event;
    }
}
