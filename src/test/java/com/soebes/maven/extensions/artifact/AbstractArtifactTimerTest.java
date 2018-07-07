package com.soebes.maven.extensions.artifact;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryEvent.EventType;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.soebes.maven.extensions.TimePlusSize;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
public class AbstractArtifactTimerTest
{
    class XAbstractArtifactTimer
        extends AbstractArtifactTimer
    {
        public XAbstractArtifactTimer()
        {
            super();
        }
    }

    private AbstractArtifactTimer aat;

    @BeforeMethod
    public void beforeMethod()
    {
        aat = new XAbstractArtifactTimer();
    }

    private Artifact createMockArtifactWithLength( String groupId, String artifactId, String version, String extensions,
                                                   String classifier )
    {
        Artifact artifact = createMockArtifact( groupId, artifactId, version, extensions, classifier );

        File f = mock( File.class );

        when( f.length() ).thenReturn( 1000L );
        when( artifact.getFile() ).thenReturn( f );

        return artifact;
    }

    private Artifact createMockArtifact( String groupId, String artifactId, String version, String extensions,
                                         String classifier )
    {
        Artifact artifact = mock( Artifact.class );

        when( artifact.getGroupId() ).thenReturn( groupId );
        when( artifact.getArtifactId() ).thenReturn( artifactId );
        when( artifact.getVersion() ).thenReturn( version );
        when( artifact.getExtension() ).thenReturn( extensions );
        when( artifact.getClassifier() ).thenReturn( classifier );
        when( artifact.getFile() ).thenReturn( null );
        return artifact;
    }

    @Test
    public void shouldResultWithoutClassifier()
    {
        Artifact artifact = createMockArtifact( "groupId", "artifactId", "version", "jar", "" );

        String result = aat.getArtifactId( artifact );

        assertThat( result ).isEqualTo( "groupId:artifactId:version:jar" );
    }

    @Test
    public void shouldResultWitClassifier()
    {
        Artifact artifact = createMockArtifact( "groupId", "artifactId", "version", "jar", "classifier" );

        String result = aat.getArtifactId( artifact );

        assertThat( result ).isEqualTo( "groupId:artifactId:version:classifier:jar" );

    }

    @Test
    public void shouldResultInSingleEntryInTimerEvents()
        throws InterruptedException
    {
        Artifact artifact = createMockArtifact( "groupId", "artifactId", "version", "jar", "" ); // as per javadoc, extension is never null

        RepositoryEvent build =
            new RepositoryEvent.Builder( mock( RepositorySystemSession.class ),
                                         EventType.ARTIFACT_DEPLOYED ).setArtifact( artifact ).build();
        aat.start( build );

        Thread.sleep( 10L );

        aat.stop( build );

        String key = aat.getArtifactId( artifact );

        assertThat( aat.getTimerEvents() ).hasSize( 1 ).containsKey( key );

        TimePlusSize timePlusSize = aat.getTimerEvents().get( key );
        assertThat( timePlusSize.getElapsedTime() ).isGreaterThanOrEqualTo( 10L );
    }

    @Test
    public void shouldResultInSingleEntryInTimerEventsWithLengthEntry()
        throws InterruptedException
    {
        Artifact artifact = createMockArtifactWithLength( "groupId", "artifactId", "version", "jar", "" );

        RepositoryEvent build =
            new RepositoryEvent.Builder( mock( RepositorySystemSession.class ),
                                         EventType.ARTIFACT_DEPLOYED ).setArtifact( artifact ).build();
        aat.start( build );

        Thread.sleep( 10L );

        aat.stop( build );

        String key = aat.getArtifactId( artifact );

        assertThat( aat.getTimerEvents() ).hasSize( 1 ).containsKey( key );

        TimePlusSize timePlusSize = aat.getTimerEvents().get( key );
        assertThat( timePlusSize.getElapsedTime() ).isGreaterThanOrEqualTo( 10L );
        assertThat( timePlusSize.getSize() ).isEqualTo( 1000L );
    }

    @Test( expectedExceptions = {
        IllegalArgumentException.class }, expectedExceptionsMessageRegExp = "Unknown artifactId \\(groupId:artifactId:version:classifier:jar\\)" )
    public void stopShouldFailWithIllegalArgumentExceptionBasedOnWrongArtifact()
    {
        Artifact artifact = createMockArtifact( "groupId", "artifactId", "version", "jar", "" );
        Artifact unKnownArtifact = createMockArtifact( "groupId", "artifactId", "version", "jar", "classifier" );

        RepositoryEvent build =
            new RepositoryEvent.Builder( mock( RepositorySystemSession.class ),
                                         EventType.ARTIFACT_DEPLOYED ).setArtifact( artifact ).build();
        aat.start( build );

        RepositoryEvent buildUnknown =
            new RepositoryEvent.Builder( mock( RepositorySystemSession.class ),
                                         EventType.ARTIFACT_DEPLOYED ).setArtifact( unKnownArtifact ).build();

        aat.stop( buildUnknown );

        // Intentionally no assertThat () cause we expect to get an IllegalArgumentException
    }

    @Test
    public void calculateMegabytesPerSecondsShouldReturnOneMegabytePerSecond()
        throws InterruptedException
    {
        long timeInMilliseconds = 1000;
        long sizeInBytes = 1 * 1024 * 1024;
        assertThat( aat.calculateMegabytesPerSeconds( timeInMilliseconds, sizeInBytes ) ).isEqualTo( 1.0,
                                                                                                     offset( 0.0002 ) );

    }

}
