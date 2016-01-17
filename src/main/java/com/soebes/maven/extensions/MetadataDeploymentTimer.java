package com.soebes.maven.extensions;

import java.text.NumberFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.metadata.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataDeploymentTimer
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private Map<String, TimePlusSize> timerEvents;

    public MetadataDeploymentTimer()
    {
        this.timerEvents = new ConcurrentHashMap<>();
    }

    private String getArtifactId( Metadata artifact )
    {
        StringBuilder sb = new StringBuilder( 128 );
        sb.append( artifact.getGroupId() ).append( ":" )
            .append( artifact.getArtifactId() )
            .append( ":" ).append( artifact.getVersion() )
            .append( ':' ).append( artifact.getType() )
            .append( ':' ).append( artifact.getNature() );

        return sb.toString();
    }

    public void start( RepositoryEvent event )
    {
        String metadataId = getArtifactId( event.getMetadata() );
        TimePlusSize systemTime = new TimePlusSize();
        systemTime.start();
        Metadata md = event.getMetadata();
        LOGGER.info( "Start Metadata: {} {} {} {}", md.getArtifactId(), md.getGroupId(), md.getVersion(), md.getFile());
        timerEvents.put( metadataId, systemTime );
    }

    public void stop( RepositoryEvent event )
    {
        String metadataId = getArtifactId( event.getMetadata() );
        if ( !timerEvents.containsKey( metadataId ) )
        {
            throw new IllegalArgumentException( "Unknown metadataId (" + metadataId + ")" );
        }
        timerEvents.get( metadataId ).stop();
        
        Metadata md = event.getMetadata();
        LOGGER.info( "Metadata: {} {} {} {}", md.getArtifactId(), md.getGroupId(), md.getVersion(), md.getFile());
        if (event.getMetadata().getFile() != null) {
            timerEvents.get( metadataId ).setSize( event.getMetadata().getFile().length() );
        }
    }

    public void report()
    {
        LOGGER.info( "Metadata Deployment summary:" );
        long totalInstallationTime = 0;
        long totalInstallationSize = 0;
        for ( Entry<String, TimePlusSize> item : this.timerEvents.entrySet() )
        {
            totalInstallationTime += item.getValue().getElapsedTime();
            totalInstallationSize += item.getValue().getSize();
            LOGGER.info( "{} ms : {}", String.format( "%8d", item.getValue().getElapsedTime() ), item.getKey() );
        }
        LOGGER.info( "{} ms  {} bytes.", NumberFormat.getIntegerInstance().format( totalInstallationTime ),
                     NumberFormat.getIntegerInstance().format( totalInstallationSize ) );
        LOGGER.info( "------------------------------------------------------------------------" );
    }

}
