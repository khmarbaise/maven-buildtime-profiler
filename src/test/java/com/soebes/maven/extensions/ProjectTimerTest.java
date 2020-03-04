package com.soebes.maven.extensions;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.project.MavenProject;
import org.testng.annotations.Test;

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

    @Test
    public void projectStopShouldFailWithIllegalArgumentExceptionBasedOnUnknownProject()
    {
        ProjectTimer t = new ProjectTimer();

        ExecutionEvent event = createEvent( "Anton" );

        t.projectStart( event );

        ExecutionEvent unknownEvent = createEvent( "Egon" );

        assertThatIllegalArgumentException().isThrownBy(() -> t.projectStop( unknownEvent )).withMessage("Unknown projectId (Egon)");
    }

    @Test
    public void getTimeForProjectShouldFailWithIllegalArgumentExceptionBasedOnUnknownProject()
    {
        ProjectTimer t = new ProjectTimer();

        ExecutionEvent event = createEvent( "Anton" );

        assertThatIllegalArgumentException().isThrownBy(() -> t.getTimeForProject( event.getProject() )).withMessage("Unknown projectId (Anton)");
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
