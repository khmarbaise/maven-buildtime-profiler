package com.soebes.maven.extensions;

public class MojoKey
    extends ProjectKey
{
    private String goal;

    private String executionId;

    private String phase;

    public MojoKey( String groupId, String artifactId, String version, String goal, String executionId,
                    String lifeCyclePhase )
    {
        super( groupId, artifactId, version );
        this.goal = goal;
        this.executionId = executionId;
        this.phase = lifeCyclePhase;
    }

    public String getGoal()
    {
        return goal;
    }

    public void setGoal( String goal )
    {
        this.goal = goal;
    }

    public String getExecutionId()
    {
        return executionId;
    }

    public void setExecutionId( String executionId )
    {
        this.executionId = executionId;
    }

    public String getPhase()
    {
        return phase;
    }

    public void setPhase( String phase )
    {
        this.phase = phase;
    }

    public String getFullId()
    {
        return super.getId() + ":" + getGoal() + ":" + getExecutionId();
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ( ( executionId == null ) ? 0 : executionId.hashCode() );
        result = prime * result + ( ( goal == null ) ? 0 : goal.hashCode() );
        result = prime * result + ( ( phase == null ) ? 0 : phase.hashCode() );
        return result;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( !super.equals( obj ) )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        MojoKey other = (MojoKey) obj;
        if ( executionId == null )
        {
            if ( other.executionId != null )
                return false;
        }
        else if ( !executionId.equals( other.executionId ) )
            return false;
        if ( goal == null )
        {
            if ( other.goal != null )
                return false;
        }
        else if ( !goal.equals( other.goal ) )
            return false;
        if ( phase == null )
        {
            if ( other.phase != null )
                return false;
        }
        else if ( !phase.equals( other.phase ) )
            return false;
        return true;
    }

}