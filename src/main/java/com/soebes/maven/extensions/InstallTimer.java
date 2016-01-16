package com.soebes.maven.extensions;

import java.text.NumberFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.artifact.Artifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InstallTimer
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private Map<String, TimePlusSize> timerEvents;

    public InstallTimer()
    {
        this.timerEvents = new ConcurrentHashMap<>();
    }

    private String getArtifactId( Artifact artifact )
    {
        StringBuilder sb = new StringBuilder( artifact.getGroupId() );
        sb.append( ":" ).append( artifact.getArtifactId() ).append( ":" ).append( artifact.getVersion() );
        if ( artifact.getClassifier() != null )
        {
            sb.append( ':' ).append( artifact.getClassifier() );
        }
        sb.append( ':' ).append( artifact.getExtension() );
        return sb.toString();
    }

    public void start( RepositoryEvent event )
    {
        String artifactId = getArtifactId( event.getArtifact() );
        TimePlusSize time = new TimePlusSize();
        time.start();
        timerEvents.put( artifactId, time );
    }

    public void stop( RepositoryEvent event )
    {
        String artifactId = getArtifactId( event.getArtifact() );
        if ( !timerEvents.containsKey( artifactId ) )
        {
            throw new IllegalArgumentException( "Unknown artifactId (" + artifactId + ")" );
        }
        timerEvents.get( artifactId ).stop();
        timerEvents.get( artifactId ).setSize( event.getArtifact().getFile().length() );
    }

    public void report()
    {
        LOGGER.info( "Installation summary:" );
        long totalInstallationTime = 0;
        long totalInstallationSize = 0;
        for ( Entry<String, TimePlusSize> item : this.timerEvents.entrySet() )
        {
            totalInstallationTime += item.getValue().getElapsedTime();
            totalInstallationSize += item.getValue().getSize();
            LOGGER.info( "{} ms : {}", String.format( "%8d", item.getValue().getElapsedTime() ), item.getKey() );
        }

        LOGGER.info( "{} ms {} bytes.", NumberFormat.getIntegerInstance().format( totalInstallationTime ),
                     NumberFormat.getIntegerInstance().format( totalInstallationSize ) );

    }

}
