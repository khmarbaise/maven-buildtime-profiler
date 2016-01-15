package com.soebes.maven.extensions;

import org.apache.maven.execution.ExecutionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DiscoveryTimer
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private SystemTime time;

    public DiscoveryTimer()
    {
        this.time = new SystemTime();
    }

    public void discoveryStart( ExecutionEvent event )
    {
        time.start();
    }

    public void discoveryStop( ExecutionEvent event )
    {
        time.stop();
    }

    public void report()
    {
        LOGGER.info( "Project discovery time: {} ms", String.format( "%8d", time.getElapsedTime() ) );
    }

}
