package com.soebes.maven.extensions;

public class SystemTime
{

    private long startTime;

    private long stopTime;

    public SystemTime()
    {
    }

    public SystemTime start()
    {
        this.startTime = System.currentTimeMillis();
        return this;
    }

    public SystemTime stop()
    {
        this.stopTime = System.currentTimeMillis();
        return this;
    }
    
    public long getElapsedTime() {
        return this.stopTime - this.startTime;
    }
}
