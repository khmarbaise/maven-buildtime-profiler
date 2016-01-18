package com.soebes.maven.extensions.metadata;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.metadata.Metadata;

import com.soebes.maven.extensions.TimePlusSize;

public abstract class AbstractMetadataTimer
{
    private Map<String, TimePlusSize> timerEvents;

    public AbstractMetadataTimer()
    {
        this.timerEvents = new ConcurrentHashMap<>();
    }

    protected String getArtifactId( Metadata artifact )
    {
        StringBuilder sb = new StringBuilder( 128 );
        sb.append( artifact.getGroupId() ).append( ":" )
            .append( artifact.getArtifactId() )
            .append( ":" ).append( artifact.getVersion() )
            .append( ':' ).append( artifact.getType() )
            .append( ':' ).append( artifact.getNature() );

        return sb.toString();
    }

    protected Map<String, TimePlusSize> getTimerEvents()
    {
        return timerEvents;
    }

    public void start( RepositoryEvent event )
    {
        String metadataId = getArtifactId( event.getMetadata() );
        TimePlusSize systemTime = new TimePlusSize();
        systemTime.start();
        getTimerEvents().put( metadataId, systemTime );
    }

    public void stop( RepositoryEvent event )
    {
        String metadataId = getArtifactId( event.getMetadata() );
        if ( !getTimerEvents().containsKey( metadataId ) )
        {
            throw new IllegalArgumentException( "Unknown metadataId (" + metadataId + ")" );
        }
        getTimerEvents().get( metadataId ).stop();
        if ( event.getMetadata().getFile() != null )
        {
            getTimerEvents().get( metadataId ).setSize( event.getMetadata().getFile().length() );
        }
    }

}
