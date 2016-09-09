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

import com.google.inject.Singleton;
import com.soebes.maven.extensions.artifact.DeployTimer;
import com.soebes.maven.extensions.artifact.DownloadTimer;
import com.soebes.maven.extensions.artifact.InstallTimer;
import com.soebes.maven.extensions.metadata.MetadataDeploymentTimer;
import com.soebes.maven.extensions.metadata.MetadataDownloadTimer;
import com.soebes.maven.extensions.metadata.MetadataInstallTimer;

/**
 * @author Karl Heinz Marbaise <a href="mailto:khmarbaise@apache.org">khmarbaise@apache.org</a>
 */
@Named
@Singleton
public class BuildTimeProfiler
    extends AbstractEventSpy
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private final List<String> lifeCyclePhases;

    private final DiscoveryTimer discoveryTimer;

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

    @Inject
    public BuildTimeProfiler()
    {
        LOGGER.debug( "LifeCycleProfiler ctor called." );
        this.lifeCyclePhases = Collections.<String>synchronizedList( new LinkedList<String>() );
        this.discoveryTimer = new DiscoveryTimer();
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
    }

    @Override
    public void init( Context context )
        throws Exception
    {
        super.init( context );

        LOGGER.info( "MBTP: Maven Build Time Profiler Version {} started.", BuildTimeProfilerVersion.getVersion() );
        // Is this always in the context? Based on Maven Core yes.
        String workingDirectory = (String) context.getData().get( "workingDirectory" );
        LOGGER.info( "MBTP: workingDirectory: " + workingDirectory );

        String multiModuleProjectDirectory = (String) context.getData().get( "multiModuleProjectDirectory" );
        LOGGER.info( "MBTP: multiModuleProjectDirectory: " + multiModuleProjectDirectory );

        // Properties systemProperties = (Properties) context.getData().get( "systemProperties" );
        // for ( String propName : systemProperties.stringPropertyNames() )
        // {
        // String propValue = systemProperties.getProperty( propName );
        // LOGGER.info( " systemProperty " + propName + ": '" + propValue + "'" );
        // }
        //
        // Properties userProperties = (Properties) context.getData().get( "userProperties" );
        // for ( String propName : userProperties.stringPropertyNames() )
        // {
        // String propValue = userProperties.getProperty( propName );
        // LOGGER.info( " userProperty " + propName + ": '" + propValue + "'" );
        // }
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
            else if ( event instanceof org.eclipse.aether.RepositoryEvent )
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

    private void repositoryEventHandler( org.eclipse.aether.RepositoryEvent repositoryEvent )
    {
        EventType type = repositoryEvent.getType();
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
        Type type = executionEvent.getType();
        switch ( type )
        {
            case ProjectDiscoveryStarted:
                // Start reading the pom files..
                discoveryTimer.discoveryStart();
                break;
            case SessionStarted:
                // Reading of pom files done and structure now there.
                discoveryTimer.discoveryStop();
                // executionEvent.getSession().getProjectDependencyGraph().getSortedProjects();
                sessionTimer.sessionStart();
                break;
            case SessionEnded:
                // Everything is done.
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
                String phase = executionEvent.getMojoExecution().getLifecyclePhase();
                collectAllLifeCylcePhases( phase );

                // Key: phase, project, mojo
                mojoTimer.mojoStart( executionEvent );
                break;

            case MojoFailed:
            case MojoSucceeded:
            case MojoSkipped:
                mojoTimer.mojoStop( executionEvent );
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
        // event.getUserProperties().put( "revision", "1.2.3-SNAPSHOT" );
        // event.getSystemProperties().put( "revision", "1.2.3-SNAPSHOT" );
        // Can we do something more useful here?
        LOGGER.debug( "MBTP: executionRequestEventHandler: {}", event.getExecutionListener() );
    }

    private void executionResultEventHandler( MavenExecutionResult event )
    {
    	// when no phase was built, do not create any statistics
    	if (lifeCyclePhases.isEmpty() || lifeCyclePhases.get(0) == null) {
    		return;
    	}

    	// sort the information for Maven phases according to their order in the lifecycle 
    	Collections.sort(lifeCyclePhases, new LifeCyclePhaseComparator());

    	LOGGER.debug( "MBTP: executionResultEventHandler: {}", event.getProject() );

        // TODO: Use better formatting
        LOGGER.info( "" );
        LOGGER.info( "" );
        LOGGER.info( "------------------------------------------------------------------------" );
        LOGGER.info( "--             Maven Build Time Profiler Summary                      --" );
        LOGGER.info( "------------------------------------------------------------------------" );

        discoveryTimer.report();
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

        // LifecyclePhase.CLEAN.ordinal();
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

    private void collectAllLifeCylcePhases( String phase )
    {
        if ( !lifeCyclePhases.contains( phase ) )
        {
            lifeCyclePhases.add( phase );
        }
    }

}
