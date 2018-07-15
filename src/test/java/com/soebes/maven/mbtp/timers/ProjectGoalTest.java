package com.soebes.maven.mbtp.timers;

import org.testng.annotations.Test;

import com.soebes.maven.mbtp.timers.ProjectGoal;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ProjectGoalTest
{
    @Test
    public void checkEqualsAndHashCodeContract()
    {
        EqualsVerifier.forClass( ProjectGoal.class ).verify();
    }
}
