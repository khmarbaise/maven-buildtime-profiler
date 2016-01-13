package com.soebes.maven.extensions;

import org.apache.maven.execution.ExecutionEvent;
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

    public void sessionStart( ExecutionEvent event )
    {
        time.start();
    }

    public void sessionStop( ExecutionEvent event )
    {
        time.stop();
    }

    public void report()
    {
        LOGGER.info( "SessionTime: {}", time.getElapsedTime() );
    }

}
