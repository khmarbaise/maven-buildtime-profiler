package com.soebes.maven.extensions;

import org.testng.annotations.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

public class ProjectGoalTest
{
    @Test
    public void checkEqualsAndHashCodeContract()
    {
        EqualsVerifier.forClass( ProjectGoal.class ).verify();
    }
}
