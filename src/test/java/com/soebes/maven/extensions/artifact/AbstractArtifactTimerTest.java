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

import com.soebes.maven.extensions.TimePlusSize;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryEvent.EventType;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.offset;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
class AbstractArtifactTimerTest
{
    static class XAbstractArtifactTimer
        extends AbstractArtifactTimer
    {
        public XAbstractArtifactTimer()
        {
            super();
        }
    }

    private AbstractArtifactTimer aat;

    @BeforeEach
    void beforeMethod()
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
    void shouldResultWithoutClassifier()
    {
        Artifact artifact = createMockArtifact( "groupId", "artifactId", "version", "jar", "" );

        String result = aat.getArtifactId( artifact );

        assertThat( result ).isEqualTo( "groupId:artifactId:version:jar" );
    }

    @Test
    void shouldResultWitClassifier()
    {
        Artifact artifact = createMockArtifact( "groupId", "artifactId", "version", "jar", "classifier" );

        String result = aat.getArtifactId( artifact );

        assertThat( result ).isEqualTo( "groupId:artifactId:version:classifier:jar" );

    }

    @Test
    void shouldResultInSingleEntryInTimerEvents() {
        Artifact artifact = createMockArtifact( "groupId", "artifactId", "version", "jar", "" ); // as per javadoc, extension is never null

        RepositoryEvent build =
            new RepositoryEvent.Builder( mock( RepositorySystemSession.class ),
                                         EventType.ARTIFACT_DEPLOYED ).setArtifact( artifact ).build();
        aat.start( build );

        await()
            .pollInterval(Duration.ofMillis(10))
            .atLeast(Duration.ofMillis(10)).until(() -> true);

        aat.stop( build );

        String key = aat.getArtifactId( artifact );

        assertThat( aat.getTimerEvents() ).hasSize( 1 ).containsKey( key );

        TimePlusSize timePlusSize = aat.getTimerEvents().get( key );
        assertThat( timePlusSize.getElapsedTime() ).isGreaterThanOrEqualTo( 10L );
    }

    @Test
    void shouldResultInSingleEntryInTimerEventsWithLengthEntry()
        throws InterruptedException
    {
        Artifact artifact = createMockArtifactWithLength( "groupId", "artifactId", "version", "jar", "" );

        RepositoryEvent build =
            new RepositoryEvent.Builder( mock( RepositorySystemSession.class ),
                                         EventType.ARTIFACT_DEPLOYED ).setArtifact( artifact ).build();
        aat.start( build );

        await()
            .pollInterval(Duration.ofMillis(10))
            .atLeast(Duration.ofMillis(10)).until(() -> true);

        aat.stop( build );

        String key = aat.getArtifactId( artifact );

        assertThat( aat.getTimerEvents() ).hasSize( 1 ).containsKey( key );

        TimePlusSize timePlusSize = aat.getTimerEvents().get( key );
        assertThat( timePlusSize.getElapsedTime() ).isGreaterThanOrEqualTo( 10L );
        assertThat( timePlusSize.getSize() ).isEqualTo( 1000L );
    }

    @Test
    void stopShouldFailWithIllegalArgumentExceptionBasedOnWrongArtifact()
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

        assertThatIllegalArgumentException().isThrownBy(() -> aat.stop( buildUnknown )).withMessage("Unknown artifactId (groupId:artifactId:version:classifier:jar)");
    }

    @Test
    void calculateMegabytesPerSecondsShouldReturnOneMegabytePerSecond()
    {
        long timeInMilliseconds = 1000;
        long sizeInBytes = 1 * 1024 * 1024;
        assertThat( aat. calculateMegabytesPerSeconds( timeInMilliseconds, sizeInBytes ) ).isEqualTo( 1.0,
                                                                                                     offset( 0.0002 ) );

    }

}
