package com.soebes.maven.extensions;

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

    public void discoveryStart()
    {
        time.start();
    }

    public void discoveryStop()
    {
        time.stop();
    }

    public void report()
    {
        LOGGER.info( "Project discovery time: {} ms", String.format( "%8d", time.getElapsedTime() ) );
        LOGGER.info( "------------------------------------------------------------------------" );
    }

}
