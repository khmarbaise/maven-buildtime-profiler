package com.soebes.maven.extensions;

public class ProjectMojo
{
    private ProjectKey project;

    private MojoKey mojo;

    public ProjectMojo( ProjectKey project, MojoKey mojo )
    {
        super();
        this.project = project;
        this.mojo = mojo;
    }

    public ProjectKey getProject()
    {
        return project;
    }

    public void setProject( ProjectKey project )
    {
        this.project = project;
    }

    public MojoKey getMojo()
    {
        return mojo;
    }

    public void setMojo( MojoKey mojo )
    {
        this.mojo = mojo;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( mojo == null ) ? 0 : mojo.hashCode() );
        result = prime * result + ( ( project == null ) ? 0 : project.hashCode() );
        return result;
    }

    public String getId()
    {
        String s1 = getMojo().getGroupId() + ":" + getMojo().getArtifactId() + ":" + getMojo().getVersion() + ":"
            + getMojo().getGoal() + " (" + getMojo().getExecutionId() + ":" + getMojo().getPhase() + ")";
        String s2 = getProject().getGroupId() + ":" + getProject().getArtifactId() + ":" + getProject().getVersion();
        return s1 + " @ " + s2;
    }

    @Override
    public boolean equals( Object obj )
    {
        if ( this == obj )
            return true;
        if ( obj == null )
            return false;
        if ( getClass() != obj.getClass() )
            return false;
        ProjectMojo other = (ProjectMojo) obj;
        if ( mojo == null )
        {
            if ( other.mojo != null )
                return false;
        }
        else if ( !mojo.equals( other.mojo ) )
            return false;
        if ( project == null )
        {
            if ( other.project != null )
                return false;
        }
        else if ( !project.equals( other.project ) )
            return false;
        return true;
    }

}