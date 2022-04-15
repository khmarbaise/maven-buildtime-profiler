package com.soebes.maven.extensions;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

class ProjectGoalTest
{
    @Test
    void checkEqualsAndHashCodeContract()
    {
        EqualsVerifier.forClass( ProjectGoal.class ).verify();
    }
}
