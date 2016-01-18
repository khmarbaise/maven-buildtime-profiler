package com.soebes.maven.extensions.artifact;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.artifact.Artifact;

import com.soebes.maven.extensions.TimePlusSize;

public abstract class AbstractArtifactTimer
{
    private Map<String, TimePlusSize> timerEvents;

    public AbstractArtifactTimer()
    {
        this.timerEvents = new ConcurrentHashMap<>();
    }
    
    protected String getArtifactId( Artifact artifact )
    {
        StringBuilder sb = new StringBuilder( 128 );
        sb.append( artifact.getGroupId() );
        sb.append( ":" ).append( artifact.getArtifactId() ).append( ":" ).append( artifact.getVersion() );
        if ( artifact.getClassifier() != null )
        {
            sb.append( ':' ).append( artifact.getClassifier() );
        }
        sb.append( ':' ).append( artifact.getExtension() );
        return sb.toString();
    }

    protected Map<String, TimePlusSize> getTimerEvents()
    {
        return timerEvents;
    }

    public void start( RepositoryEvent event )
    {
        String artifactId = getArtifactId( event.getArtifact() );
        TimePlusSize systemTime = new TimePlusSize();
        systemTime.start();
        getTimerEvents().put( artifactId, systemTime );
    }

    public void stop( RepositoryEvent event )
    {
        String artifactId = getArtifactId( event.getArtifact() );
        if ( !getTimerEvents().containsKey( artifactId ) )
        {
            throw new IllegalArgumentException( "Unknown artifactId (" + artifactId + ")" );
        }
        getTimerEvents().get( artifactId ).stop();
        getTimerEvents().get( artifactId ).setSize( event.getArtifact().getFile().length() );
    }

}
