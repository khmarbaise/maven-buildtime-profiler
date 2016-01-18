package com.soebes.maven.extensions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionTimer
{
    private final Logger LOGGER = LoggerFactory.getLogger( getClass() );

    private SystemTime time;

    public SessionTimer()
    {
        this.time = new SystemTime();
    }

    public void sessionStart( )
    {
        time.start();
    }

    public void sessionStop( )
    {
        time.stop();
    }

    public void report()
    {
        LOGGER.info( "SessionTime: {}", time.getElapsedTime() );
    }

}
