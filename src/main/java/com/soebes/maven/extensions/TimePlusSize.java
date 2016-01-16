package com.soebes.maven.extensions;

public class TimePlusSize
    extends SystemTime
{

    private long size;

    public TimePlusSize()
    {
        super();
    }

    public long getSize()
    {
        return size;
    }

    public void setSize( long size )
    {
        this.size = size;
    }

}
