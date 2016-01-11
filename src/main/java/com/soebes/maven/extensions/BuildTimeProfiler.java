package com.soebes.maven.extensions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionEvent.Type;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.DependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionResult;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryEvent.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Karl Heinz Marbaise <khmarbaise@apache.org>
 *
 */
@Named
public class BuildTimeProfiler
    extends AbstractEventSpy
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private List<String> lifeCyclePhases;

    @Inject
    public BuildTimeProfiler()
    {
        LOGGER.debug( "LifeCycleProfiler ctor called." );
        this.lifeCyclePhases = Collections.<String>synchronizedList( new LinkedList<String>() );
    }

    @Override
    public void init( Context context )
        throws Exception
    {
        // TODO: Replace this with property from filtered file to get the real version number
        LOGGER.info( "Maven Build Time Profiler Version 0.1 started." );
        // Is this always in the context?
        String workingDirectory = (String) context.getData().get( "workingDirectory" );
        LOGGER.debug( "workingDirectory: " + workingDirectory );

        // data.put( "plexus", container );
        // data.put( "workingDirectory", cliRequest.workingDirectory );
        // data.put( "systemProperties", cliRequest.systemProperties );
        // data.put( "userProperties", cliRequest.userProperties );
        // data.put( "versionProperties", CLIReportingUtils.getBuildProperties() );

    }

    @Override
    public void onEvent( Object event )
        throws Exception
    {
        try
        {
            if ( event instanceof ExecutionEvent )
            {
                executionEventHandler( (ExecutionEvent) event );
            }
            else if ( event instanceof RepositoryEvent )
            {
                repositoryEventHandler( (RepositoryEvent) event );
            }
            else if ( event instanceof MavenExecutionRequest )
            {
                executionRequestEventHandler( (MavenExecutionRequest) event );
            }
            else if ( event instanceof MavenExecutionResult )
            {
                executionResultEventHandler( (MavenExecutionResult) event );
            }
            else if ( event instanceof DependencyResolutionRequest )
            {
                dependencyResolutionRequest( (DependencyResolutionRequest) event );
            }
            else if ( event instanceof DependencyResolutionResult )
            {
                dependencyResolutionResult( (DependencyResolutionResult) event );
            }
            else
            {
                // TODO: What kind of event we haven't considered?
                LOGGER.debug( "Event {}", event.getClass().getCanonicalName() );
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( "Exception", e );
        }
    }

    @Override
    public void close()
    {
        LOGGER.debug( "done." );
    }

    private void dependencyResolutionResult( DependencyResolutionResult event )
    {
        LOGGER.debug( "dependencyResolutionResult()" );
    }

    private void dependencyResolutionRequest( DependencyResolutionRequest event )
    {
        LOGGER.debug( "dependencyResolutionRequest()" );
    }

    private void repositoryEventHandler( RepositoryEvent repositoryEvent )
    {
        EventType type = repositoryEvent.getType();
        switch ( type )
        {
            case ARTIFACT_DOWNLOADING:
                break;
            case ARTIFACT_DOWNLOADED:
                break;
            case ARTIFACT_DEPLOYING:
                break;
            case ARTIFACT_DEPLOYED:
                break;
            case ARTIFACT_INSTALLING:
                break;
            case ARTIFACT_INSTALLED:
                break;
            case ARTIFACT_RESOLVING:
                break;
            case ARTIFACT_RESOLVED:
                break;
            case ARTIFACT_DESCRIPTOR_INVALID:
                break;
            case ARTIFACT_DESCRIPTOR_MISSING:
                break;

            default:
                LOGGER.error( "Event {}", type );
                break;
        }
    }

    private void executionEventHandler( ExecutionEvent executionEvent )
    {
        Type type = executionEvent.getType();
        switch ( type )
        {
            case ProjectDiscoveryStarted:
                // Start reading the pom files..
                break;
            case SessionStarted:
                // Reading of pom files done and structure now there.
                // executionEvent.getSession().getProjectDependencyGraph().getSortedProjects();
                break;
            case SessionEnded:
                // Everything is done.
                break;

            case ForkStarted:
                break;
            case ForkFailed:
                break;
            case ForkSucceeded:
                break;

            case ForkedProjectStarted:
                break;
            case ForkedProjectFailed:
                break;
            case ForkedProjectSucceeded:
                break;

            case MojoStarted:
                String phase = executionEvent.getMojoExecution().getLifecyclePhase();
                collectAllLifeCylcePhases( phase );
                break;
            case MojoFailed:
                break;
            case MojoSucceeded:
                break;
            case MojoSkipped:
                break;

            case ProjectStarted:
                break;
            case ProjectFailed:
                break;
            case ProjectSucceeded:
                break;
            case ProjectSkipped:
                break;

            default:
                break;
        }

    }

    private void executionRequestEventHandler( MavenExecutionRequest event )
    {
        // Can we do something more useful here?
        LOGGER.debug( "executionRequest: {}", event.getExecutionListener() );
    }

    private void executionResultEventHandler( MavenExecutionResult event )
    {
        LOGGER.debug( "executionRequest: {}", event.getProject() );
    }

    private void collectAllLifeCylcePhases( String phase )
    {
        if ( !lifeCyclePhases.contains( phase ) )
        {
            lifeCyclePhases.add( phase );
        }
    }

}
