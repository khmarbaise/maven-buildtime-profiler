package com.soebes.maven.extensions;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.plugins.annotations.LifecyclePhase;

public class LifeCycleOrdering
    extends AbstractEventSpy
{
    private final List<String> predefinedPhases = new LinkedList<>();

    private void initializePredefinedPhases()
    {
        // We do that explicitly here, cause otherwise
        // the clean life cycle and site life cycle are positioned
        // at the end. Looks a bit strange. Technically it's
        // not a problem.
        initPredefinedFromTo( LifecyclePhase.PRE_CLEAN, LifecyclePhase.POST_CLEAN );
        initPredefinedFromTo( LifecyclePhase.INITIALIZE, LifecyclePhase.DEPLOY );
        initPredefinedFromTo( LifecyclePhase.PRE_SITE, LifecyclePhase.SITE_DEPLOY );
    }

    private void initPredefinedFromTo( LifecyclePhase from, LifecyclePhase to )
    {
        LifecyclePhase[] values = LifecyclePhase.values();

        for ( int item = from.ordinal(); item <= to.ordinal(); item++ )
        {
            predefinedPhases.add( values[item].id() );
        }
    }

    public LifeCycleOrdering()
    {
        initializePredefinedPhases();
    }

    public List<String> getPredefinedPhases()
    {
        return predefinedPhases;
    }

    protected void orderLifeCycleOnPreparedOrder( List<String> lifeCyclePhases )
    {
        // Sort the lifeCyclePhases based on the given in predefinedPhases.
        Collections.sort( lifeCyclePhases, new Comparator<String>()
        {
            public int compare( String left, String right )
            {
                return Integer.compare( predefinedPhases.indexOf( left ), predefinedPhases.indexOf( right ) );
            }
        } );
    }

}
