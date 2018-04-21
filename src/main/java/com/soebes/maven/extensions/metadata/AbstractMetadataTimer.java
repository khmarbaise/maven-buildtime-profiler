package com.soebes.maven.extensions.metadata;

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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.metadata.Metadata;

import com.soebes.maven.extensions.TimePlusSize;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
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
        sb.append( artifact.getGroupId() ) //
          .append( ":" ).append( artifact.getArtifactId() ) //
          .append( ":" ).append( artifact.getVersion() ) //
          .append( ':' ).append( artifact.getType() ) //
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

        long size = 0;
        if ( event.getMetadata().getFile() != null )
        {
            size = event.getMetadata().getFile().length();
        }
        getTimerEvents().get( metadataId ).setSize( size );
    }

}
