package com.soebes.maven.extensions.metadata;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryEvent.EventType;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.metadata.Metadata.Nature;

import com.soebes.maven.extensions.TimePlusSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AbstractMetadataTimerTest
{
    static class XAbstractMetadataTimer
        extends AbstractMetadataTimer
    {
        public XAbstractMetadataTimer()
        {
            super();
        }
    }

    private AbstractMetadataTimer aat;

    @BeforeEach
    public void beforeMethod()
    {
        aat = new XAbstractMetadataTimer();
    }

    private Metadata createMockMetadataWithLength( String groupId, String artifactId, String version, String type,
                                                   Nature nature )
    {
        Metadata artifact = createMockMetadata( groupId, artifactId, version, type, nature );

        File f = mock( File.class );

        when( f.length() ).thenReturn( 1000L );
        when( artifact.getFile() ).thenReturn( f );

        return artifact;
    }

    private Metadata createMockMetadata( String groupId, String artifactId, String version, String type,
                                         Metadata.Nature nature )
    {
        Metadata artifact = mock( Metadata.class );

        when( artifact.getGroupId() ).thenReturn( groupId );
        when( artifact.getArtifactId() ).thenReturn( artifactId );
        when( artifact.getVersion() ).thenReturn( version );
        when( artifact.getType() ).thenReturn( type );
        when( artifact.getNature() ).thenReturn( nature );
        when( artifact.getFile() ).thenReturn( null );
        return artifact;
    }

    @Test
    void shouldResultWithoutClassifier()
    {
        Metadata artifact = createMockMetadata( "groupId", "artifactId", "version", "type", Nature.RELEASE );

        String result = aat.getArtifactId( artifact );

        assertThat( result ).isEqualTo( "groupId:artifactId:version:type:RELEASE" );
    }

    @Test
    void shouldResultWitClassifier()
    {
        Metadata artifact = createMockMetadata( "groupId", "artifactId", "version", "type", Nature.RELEASE );

        String result = aat.getArtifactId( artifact );

        assertThat( result ).isEqualTo( "groupId:artifactId:version:type:RELEASE" );

    }

    @Test
    void shouldResultInSingleEntryInTimerEvents() {
        Metadata artifact = createMockMetadata( "groupId", "artifactId", "version", "jar", null );

        RepositoryEvent build =
            new RepositoryEvent.Builder( mock( RepositorySystemSession.class ),
                                         EventType.ARTIFACT_DEPLOYED ).setMetadata( artifact ).build();
        aat.start( build );

        await()
            .pollInterval(Duration.ofMillis(10))
            .atLeast(10L, TimeUnit.MILLISECONDS).until(() -> true);

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
        Metadata metadata = createMockMetadataWithLength( "groupId", "artifactId", "version", "type", Nature.RELEASE );

        RepositoryEvent build =
            new RepositoryEvent.Builder( mock( RepositorySystemSession.class ),
                                         EventType.ARTIFACT_DEPLOYED ).setMetadata( metadata ).build();
        aat.start( build );

        await()
            .pollInterval(Duration.ofMillis(10))
            .atLeast(10L, TimeUnit.MILLISECONDS).until(() -> true);

        aat.stop( build );

        String key = aat.getArtifactId( metadata );

        assertThat( aat.getTimerEvents() ).hasSize( 1 ).containsKey( key );

        TimePlusSize timePlusSize = aat.getTimerEvents().get( key );
        assertThat( timePlusSize.getElapsedTime() ).isGreaterThanOrEqualTo( 10L );
        assertThat( timePlusSize.getSize() ).isEqualTo( 1000L );
    }

    @Test
    void stopShouldFailWithIllegalArgumentExceptionBasedOnWrongMetadata()
    {
        Metadata metadata = createMockMetadata( "groupId", "artifactId", "version", "type", Nature.RELEASE );
        Metadata unKnownMetadata = createMockMetadata( "groupId", "artifactId", "version", "xtype", Nature.RELEASE );

        RepositoryEvent build =
            new RepositoryEvent.Builder( mock( RepositorySystemSession.class ),
                                         EventType.ARTIFACT_DEPLOYED ).setMetadata( metadata ).build();
        aat.start( build );

        RepositoryEvent buildUnknown =
            new RepositoryEvent.Builder( mock( RepositorySystemSession.class ),
                                         EventType.ARTIFACT_DEPLOYED ).setMetadata( unKnownMetadata ).build();

        assertThatIllegalArgumentException().isThrownBy(() -> aat.stop(buildUnknown))
            .withMessage("Unknown metadataId (groupId:artifactId:version:xtype:RELEASE)");

    }

}
