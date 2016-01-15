package com.soebes.maven.extensions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.ExecutionEvent.Type;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.DependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryEvent.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Karl Heinz Marbaise <khmarbaise@apache.org>
 */
@Named
public class BuildTimeProfiler
    extends AbstractEventSpy
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private List<String> lifeCyclePhases;

    private final DiscoveryTimer discoveryTimer;

    private final MojoTimer mojoTimer;

    private final ProjectTimer projectTimer;

    private final SessionTimer sessionTimer;

    @Inject
    public BuildTimeProfiler()
    {
        LOGGER.debug( "LifeCycleProfiler ctor called." );
        this.lifeCyclePhases = Collections.<String>synchronizedList( new LinkedList<String>() );
        this.discoveryTimer = new DiscoveryTimer();
        this.mojoTimer = new MojoTimer();
        this.projectTimer = new ProjectTimer();
        this.sessionTimer = new SessionTimer();
    }

    @Override
    public void init( Context context )
        throws Exception
    {
        super.init( context );

        // TODO: Replace this with property from filtered file to get the real version number
        LOGGER.info( "MBTP: Maven Build Time Profiler Version {} started.", BuildTimeProfilerVersion.getVersion() );
        // Is this always in the context?
        String workingDirectory = (String) context.getData().get( "workingDirectory" );
        LOGGER.debug( "MBTP: workingDirectory: " + workingDirectory );

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
            // The following event type is available since Maven 3.3.1+
            // else if ( event instanceof DefaultSettingsBuildingRequest) {
            // DefaultSettingsBuildingRequest r = null;
            // r.getGlobalSettingsFile();
            // r.getGlobalSettingsSource();
            // r.getSystemProperties();
            // r.getUserSettingsFile();
            // r.getUserSettingsSource();
            //
            // r.setGlobalSettingsFile( globalSettingsFile );
            // r.setGlobalSettingsSource( globalSettingsSource );
            // r.setSystemProperties( systemProperties );
            // r.setUserProperties( userProperties );
            // r.setUserSettingsFile( userSettingsFile );
            // r.setUserSettingsSource( userSettingsSource );
            // }
            // The following event type is available since Maven 3.3.1+
            // else if (event instanceof DefaultSettingsBuildingRequest) {
            //
            // DefaultSettingsBuildingRequest r = null;
            // r.getGlobalSettingsSource().getLocation()
            // }
            // The following event type is available since Maven 3.3.1+
            // else if (event instanceof DefaultToolchainsBuildingRequest) {
            // DefaultToolchainsBuildingRequest r = null;
            // r.getGlobalToolchainsSource().
            // }
            // The following event type is available since Maven 3.3.1+
            // else if (event instanceof DefaultToolchainsBuildingResult) {
            // DefaultToolchainsBuildingResult r = null;
            // r.getEffectiveToolchains();
            // r.getProblems();
            // }
            else
            {
                // TODO: What kind of event we haven't considered?
                LOGGER.debug( "MBTP: Event {}", event.getClass().getCanonicalName() );
            }
        }
        catch ( Exception e )
        {
            LOGGER.error( "MBTP: Exception", e );
        }
    }

    @Override
    public void close()
    {
        LOGGER.debug( "MBTP: done." );
    }

    private void dependencyResolutionResult( DependencyResolutionResult event )
    {
        LOGGER.debug( "MBTP: dependencyResolutionResult() {}", event.getResolvedDependencies().size() );
    }

    private void dependencyResolutionRequest( DependencyResolutionRequest event )
    {
        LOGGER.debug( "MBTP: dependencyResolutionRequest()" );
    }

    private void repositoryEventHandler( RepositoryEvent repositoryEvent )
    {
        EventType type = repositoryEvent.getType();
        switch ( type )
        {
            case ARTIFACT_DOWNLOADING:
                // Start Downloading ...
                break;
            case ARTIFACT_DOWNLOADED:
                // stop
                break;
            case ARTIFACT_DEPLOYING:
                // start...
                break;
            case ARTIFACT_DEPLOYED:
                // stop
                // repositoryEvent.getArtifact().getFile().length();
                // repositoryEvent.getMetadata().getFile().length()
                break;
            case ARTIFACT_INSTALLING:
                // ...start
                break;
            case ARTIFACT_INSTALLED:
                // stop...
                break;
            case ARTIFACT_RESOLVING:
                break;
            case ARTIFACT_RESOLVED:
                break;
            case ARTIFACT_DESCRIPTOR_INVALID:
                break;
            case ARTIFACT_DESCRIPTOR_MISSING:
                break;

            case METADATA_DEPLOYING:
                break;
            case METADATA_DEPLOYED:
                break;
            case METADATA_DOWNLOADED:
                break;
            case METADATA_DOWNLOADING:

            case METADATA_INSTALLING:
            case METADATA_INSTALLED:
            case METADATA_RESOLVED:
            case METADATA_RESOLVING:
            case METADATA_INVALID:
                break;

            default:
                LOGGER.error( "MBTP: repositoryEventHandler {}", type );
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
                discoveryTimer.discoveryStart( executionEvent );
                break;
            case SessionStarted:
                discoveryTimer.discoveryStop( executionEvent );
                // Reading of pom files done and structure now there.
                // executionEvent.getSession().getProjectDependencyGraph().getSortedProjects();
                sessionTimer.sessionStart( executionEvent );
                break;
            case SessionEnded:
                // Everything is done.
                sessionTimer.sessionStop( executionEvent );
                break;

            case ForkStarted:
                break;
            case ForkFailed:
            case ForkSucceeded:
                break;

            case ForkedProjectStarted:
                break;
            case ForkedProjectFailed:
            case ForkedProjectSucceeded:
                break;

            case MojoStarted:
                String phase = executionEvent.getMojoExecution().getLifecyclePhase();
                collectAllLifeCylcePhases( phase );

                // Key: phase, project, mojo
                mojoTimer.mojoStart( executionEvent, new SystemTime().start() );
                break;

            case MojoFailed:
            case MojoSucceeded:
            case MojoSkipped:
                mojoTimer.mojoStop( executionEvent );
                break;

            case ProjectStarted:
                projectTimer.projectStart( executionEvent, new SystemTime().start() );
                break;

            case ProjectFailed:
            case ProjectSucceeded:
            case ProjectSkipped:
                projectTimer.projectStop( executionEvent );
                break;

            default:
                LOGGER.error( "MBTP: executionEventHandler: {}", type );
                break;
        }

    }

    private void executionRequestEventHandler( MavenExecutionRequest event )
    {
        // Can we do something more useful here?
        LOGGER.debug( "MBTP: executionRequestEventHandler: {}", event.getExecutionListener() );
    }

    private void executionResultEventHandler( MavenExecutionResult event )
    {
        LOGGER.debug( "MBTP: executionResultEventHandler: {}", event.getProject() );

        discoveryTimer.report();
        LOGGER.info( "------------------------------------------------------------------------" );
        LOGGER.info( "Project Build Time (reactor order):" );
        LOGGER.info( "" );
        List<MavenProject> topologicallySortedProjects = event.getTopologicallySortedProjects();
        for ( MavenProject mavenProject : topologicallySortedProjects )
        {
            LOGGER.info( "{}:", mavenProject.getName() );

            for ( String phase : lifeCyclePhases )
            {
                ProjectKey proKey = mavenProjectToProjectKey( mavenProject );

                if ( !mojoTimer.hasTimeForProjectAndPhase( proKey, phase ) )
                {
                    continue;
                }

                long timeForPhaseAndProjectInMillis = mojoTimer.getTimeForProjectAndPhaseInMillis( proKey, phase );
                LOGGER.info( "    {} ms : {}", String.format( "%8d", timeForPhaseAndProjectInMillis ), phase );

            }

        }

        LOGGER.info( "------------------------------------------------------------------------" );
        LOGGER.info( "Lifecycle Phase summary:" );
        LOGGER.info( "" );
        for ( String phase : lifeCyclePhases )
        {
            long timeForPhaseInMillis = mojoTimer.getTimeForPhaseInMillis( phase );
            LOGGER.info( "{} ms : {}", String.format( "%8d", timeForPhaseInMillis ), phase );
        }

        // List all plugins per phase
        LOGGER.info( "------------------------------------------------------------------------" );
        LOGGER.info( "Plugins in lifecycle Phases:" );
        LOGGER.info( "" );
        for ( String phase : lifeCyclePhases )
        {
            LOGGER.info( "{}:", phase );
            Map<ProjectMojo, SystemTime> plugisInPhase = mojoTimer.getPluginsInPhase( phase );
            for ( Entry<ProjectMojo, SystemTime> pluginInPhase : plugisInPhase.entrySet() )
            {
                LOGGER.info( "{} ms: {}", String.format( "%8d", pluginInPhase.getValue().getElapsedTime() ),
                             pluginInPhase.getKey().getMojo().getFullId() );
            }

        }
        LOGGER.info( "------------------------------------------------------------------------" );

    }

    private ProjectKey mavenProjectToProjectKey( MavenProject project )
    {
        return new ProjectKey( project.getGroupId(), project.getArtifactId(), project.getVersion() );
    }

    private void collectAllLifeCylcePhases( String phase )
    {
        if ( !lifeCyclePhases.contains( phase ) )
        {
            lifeCyclePhases.add( phase );
        }
    }

}
