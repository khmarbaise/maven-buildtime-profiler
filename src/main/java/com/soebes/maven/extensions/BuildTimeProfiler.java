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

import com.soebes.maven.extensions.artifact.DeployTimer;
import com.soebes.maven.extensions.artifact.DownloadTimer;
import com.soebes.maven.extensions.artifact.InstallTimer;
import com.soebes.maven.extensions.metadata.MetadataDeploymentTimer;
import com.soebes.maven.extensions.metadata.MetadataDownloadTimer;
import com.soebes.maven.extensions.metadata.MetadataInstallTimer;
import org.apache.maven.execution.ExecutionEvent;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.DependencyResolutionRequest;
import org.apache.maven.project.DependencyResolutionResult;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositoryEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
@Named
@Singleton
public class BuildTimeProfiler
    extends LifeCycleOrdering
{
    private static final String SEPARATION_LINE = "------------------------------------------------------------------------";

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildTimeProfiler.class);

    private final List<String> lifeCyclePhases;

    private final DiscoveryTimer discoveryTimer;

    private final GoalTimer goalTimer;

    private final MojoTimer mojoTimer;

    private final ProjectTimer projectTimer;

    private final SessionTimer sessionTimer;

    private final InstallTimer installTimer;

    private final DeployTimer deployTimer;

    private final DownloadTimer downloadTimer;

    private final MetadataDownloadTimer metadataDownloadTimer;

    private final MetadataDeploymentTimer metadataDeploymentTimer;

    private final MetadataInstallTimer metadataInstallTimer;

    private final ForkTimer forkTimer;

    private final ProjectTimer forkProject;

    private boolean activated;

    public BuildTimeProfiler()
    {
        LOGGER.debug( "LifeCycleProfiler ctor called." );
        this.lifeCyclePhases = Collections.synchronizedList(new LinkedList<>() );
        this.discoveryTimer = new DiscoveryTimer();
        this.goalTimer = new GoalTimer();
        this.mojoTimer = new MojoTimer();
        this.projectTimer = new ProjectTimer();
        this.sessionTimer = new SessionTimer();
        this.installTimer = new InstallTimer();
        this.deployTimer = new DeployTimer();
        this.downloadTimer = new DownloadTimer();

        this.metadataDownloadTimer = new MetadataDownloadTimer();
        this.metadataDeploymentTimer = new MetadataDeploymentTimer();
        this.metadataInstallTimer = new MetadataInstallTimer();
        this.forkTimer = new ForkTimer();
        this.forkProject = new ProjectTimer();

        String disabled = System.getProperty("maven-build-time-profiler.disabled", "unknown");
        LOGGER.debug("MBTP: maven-build-time-profiler.disabled {}", disabled);
        this.activated = !disabled.equals("true");
    }

    @Override
    public void init( Context context )
        throws Exception
    {
        super.init( context );

        LOGGER.info( "Maven Build Time Profiler started. (Version {})", BuildTimeProfilerVersion.getVersion() );
        if ( !activated ) {
            LOGGER.info("Maven Build Time Profiler deactivated.");
            return;
        }

        // Is this always in the context? Based on Maven Core yes.
        String workingDirectory = (String) context.getData().get( "workingDirectory" );
        LOGGER.debug( "MBTP: workingDirectory: {}", workingDirectory );

        String multiModuleProjectDirectory = (String) context.getData().get( "multiModuleProjectDirectory" );
        LOGGER.debug( "MBTP: multiModuleProjectDirectory: {}", multiModuleProjectDirectory );
    }

    @Override
    public void onEvent( Object event )
        throws Exception
    {
        try
        {
            if ( !activated ) {
                return;
            }

            if ( event instanceof ExecutionEvent)
            {
                executionEventHandler( (ExecutionEvent) event );
            }
            else if ( event instanceof org.eclipse.aether.RepositoryEvent )
            {
                repositoryEventHandler( (RepositoryEvent) event );
            }
            else if ( event instanceof MavenExecutionRequest)
            {
                executionRequestEventHandler( (MavenExecutionRequest) event );
            }
            else if ( event instanceof MavenExecutionResult)
            {
                executionResultEventHandler( (MavenExecutionResult) event );
            }
            else if ( event instanceof DependencyResolutionRequest)
            {
                dependencyResolutionRequest( (DependencyResolutionRequest) event );
            }
            else if ( event instanceof DependencyResolutionResult)
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
        if ( !activated ) {
            return;
        }
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

    private void repositoryEventHandler( org.eclipse.aether.RepositoryEvent repositoryEvent )
    {
        RepositoryEvent.EventType type = repositoryEvent.getType();
        switch ( type )
        {
            case ARTIFACT_DOWNLOADING:
                LOGGER.debug( "MBTP: repositoryEventHandler {}", type );
                downloadTimer.start( repositoryEvent );
                break;
            case ARTIFACT_DOWNLOADED:
                LOGGER.debug( "MBTP: repositoryEventHandler {}", type );
                downloadTimer.stop( repositoryEvent );
                break;

            case ARTIFACT_DEPLOYING:
                deployTimer.start( repositoryEvent );
                break;
            case ARTIFACT_DEPLOYED:
                deployTimer.stop( repositoryEvent );
                break;

            case ARTIFACT_INSTALLING:
                installTimer.start( repositoryEvent );
                break;
            case ARTIFACT_INSTALLED:
                installTimer.stop( repositoryEvent );
                break;

            case METADATA_DEPLOYING:
                metadataDeploymentTimer.start( repositoryEvent );
                break;
            case METADATA_DEPLOYED:
                metadataDeploymentTimer.stop( repositoryEvent );
                break;

            case METADATA_DOWNLOADING:
                LOGGER.debug( "MBTP: repositoryEventHandler {}", type );
                metadataDownloadTimer.start( repositoryEvent );
                break;
            case METADATA_DOWNLOADED:
                LOGGER.debug( "MBTP: repositoryEventHandler {}", type );
                metadataDownloadTimer.stop( repositoryEvent );
                break;

            case METADATA_INSTALLING:
                LOGGER.debug( "MBTP: repositoryEventHandler {}", type );
                metadataInstallTimer.start( repositoryEvent );
                break;
            case METADATA_INSTALLED:
                LOGGER.debug( "MBTP: repositoryEventHandler {}", type );
                metadataInstallTimer.stop( repositoryEvent );
                break;

            case ARTIFACT_RESOLVING:
            case ARTIFACT_RESOLVED:
            case ARTIFACT_DESCRIPTOR_INVALID:
            case ARTIFACT_DESCRIPTOR_MISSING:
            case METADATA_RESOLVED:
            case METADATA_RESOLVING:
            case METADATA_INVALID:
                // Those events are not recorded.
                break;

            default:
                LOGGER.error( "MBTP: repositoryEventHandler {}", type );
                break;
        }
    }

    private void executionEventHandler( ExecutionEvent executionEvent )
    {
        if ( !activated ) {
            return;
        }
        LOGGER.debug( "executionEventHandler: {}", executionEvent.getType() );
        ExecutionEvent.Type type = executionEvent.getType();
        switch ( type )
        {
            case ProjectDiscoveryStarted:
                // Start reading the pom files..
                discoveryTimer.discoveryStart();
                break;
            case SessionStarted:
                // Reading of pom files done and structure now there.
                discoveryTimer.discoveryStop();
                sessionTimer.sessionStart();
                break;
            case SessionEnded:
                // Everything is done.
                LOGGER.info( "MBTP: Session Ended");
                sessionTimer.sessionStop();
                break;

            case ForkStarted:
                forkTimer.start();
                break;
            case ForkFailed:
            case ForkSucceeded:
                forkTimer.stop();
                break;

            case ForkedProjectStarted:
                forkProject.projectStart( executionEvent );
                break;
            case ForkedProjectFailed:
            case ForkedProjectSucceeded:
                forkProject.projectStop( executionEvent );
                break;

            case MojoStarted:
                String phaseStart = executionEvent.getMojoExecution().getLifecyclePhase();
                // Key: phase, project, mojo
                if ( phaseStart == null )
                {
                    goalTimer.mojoStart( executionEvent );
                }
                else
                {
                    collectAllLifeCyclePhases( phaseStart );
                    mojoTimer.mojoStart( executionEvent );
                }
                break;

            case MojoFailed:
            case MojoSucceeded:
            case MojoSkipped:
                String phaseStop = executionEvent.getMojoExecution().getLifecyclePhase();
                if ( phaseStop == null )
                {
                    goalTimer.mojoStop( executionEvent );
                }
                else
                {
                    mojoTimer.mojoStop( executionEvent );
                }
                break;

            case ProjectStarted:
                projectTimer.projectStart( executionEvent );
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
        LOGGER.debug( "MBTP: executionRequestEventHandler: {}", event.getExecutionListener() );
    }

    private void executionResultEventHandler( MavenExecutionResult event )
    {
        LOGGER.debug( "MBTP: executionResultEventHandler: {}", event.getProject() );
        LOGGER.info( "--             Maven Build Time Profiler Summary                      --" );
        LOGGER.info(SEPARATION_LINE);

        discoveryTimer.report();

        if ( mojoTimer.hasEvents() )
        {
            LOGGER.info( "Project Build Time (reactor order):" );
            LOGGER.info( "" );
            for ( MavenProject mavenProject : event.getTopologicallySortedProjects() )
            {
                LOGGER.info( "{}:", mavenProject.getName() );

                for ( String phase : lifeCyclePhases )
                {
                    ProjectKey projectKey = mavenProjectToProjectKey( mavenProject );

                    if ( !mojoTimer.hasTimeForProjectAndPhase( projectKey, phase ) )
                    {
                        continue;
                    }

                    long timeForPhaseAndProjectInMillis =
                        mojoTimer.getTimeForProjectAndPhaseInMillis( projectKey, phase );
                    String formattedTime = String.format("%8d", timeForPhaseAndProjectInMillis);
                    LOGGER.info( "    {} ms : {}", formattedTime, phase );

                }

            }
            LOGGER.info(SEPARATION_LINE);
            LOGGER.info( "Lifecycle Phase summary:" );
            LOGGER.info( "" );
            for ( String phase : lifeCyclePhases )
            {
                long timeForPhaseInMillis = mojoTimer.getTimeForPhaseInMillis( phase );
                String formattedTime = String.format("%8d", timeForPhaseInMillis);
                LOGGER.info( "{} ms : {}", formattedTime, phase );
            }

            // List all plugins per phase
            LOGGER.info(SEPARATION_LINE);
            LOGGER.info( "Plugins in lifecycle Phases:" );
            LOGGER.info( "" );
            for ( String phase : lifeCyclePhases )
            {
                LOGGER.info( "{}:", phase );
                Map<ProjectMojo, SystemTime> plugisInPhase = mojoTimer.getPluginsInPhase( phase );
                for ( Entry<ProjectMojo, SystemTime> pluginInPhase : plugisInPhase.entrySet() )
                {
                    String formattedTime = String.format("%8d", pluginInPhase.getValue().getElapsedTime());
                    LOGGER.info( "{} ms: {}", formattedTime,
                                 pluginInPhase.getKey().getMojo().getFullId() );
                }

            }
            LOGGER.info(SEPARATION_LINE);

            LOGGER.info( "Plugins execution Summary:" );
            LOGGER.info( "" );
            long timeForPlugins = mojoTimer.getTimeForPlugins();
            String formattedTime = String.format("%8d", timeForPlugins);
            LOGGER.info("{} ms", formattedTime);
            LOGGER.info(SEPARATION_LINE);
        }

        if ( goalTimer.hasEvents() )
        {
            LOGGER.info( "Plugins directly called via goals:" );
            LOGGER.info( "" );
            goalTimer.report();
            LOGGER.info(SEPARATION_LINE);
        }

        installTimer.report();
        downloadTimer.report();
        deployTimer.report();
        metadataInstallTimer.report();
        metadataDownloadTimer.report();
        metadataDeploymentTimer.report();

        forkTimer.report();
        forkProject.report();
    }

    private ProjectKey mavenProjectToProjectKey( MavenProject project )
    {
        return new ProjectKey( project.getGroupId(), project.getArtifactId(), project.getVersion() );
    }

    private void collectAllLifeCyclePhases(String phase )
    {
        // Phase can be null if you call maven via:
        // mvn site:stage (no life cycle started.)
        if ( phase == null )
        {
            return;
        }
        LOGGER.debug( "collectAllLifeCyclePhases({})", phase );
        if ( !lifeCyclePhases.contains( phase ) )
        {
            lifeCyclePhases.add( phase );
        }
    }

}
