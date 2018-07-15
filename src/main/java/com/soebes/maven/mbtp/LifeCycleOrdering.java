package com.soebes.maven.mbtp;

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

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.eventspy.AbstractEventSpy;
import org.apache.maven.plugins.annotations.LifecyclePhase;

/**
 * @author Karl Heinz Marbaise <a href="mailto:kama@soebes.de">kama@soebes.de</a>
 */
class LifeCycleOrdering
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
        initPredefinedFromTo( LifecyclePhase.VALIDATE, LifecyclePhase.DEPLOY );
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
