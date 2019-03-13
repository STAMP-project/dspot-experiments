package org.opentripplanner.api.model;


import org.junit.Assert;
import org.junit.Test;


public class WalkStepTest {
    private static final String NO_PARENS_STREET_NAME = "a normal name";

    private static final String START_WITH_PARENS_STREET_NAME = "(start with paren)";

    private static final String PARENS_STREET_NAME = "a normal name (paren)";

    private WalkStep step;

    @Test
    public void testNameNoParensWithNoParensName() {
        step.streetName = WalkStepTest.NO_PARENS_STREET_NAME;
        Assert.assertEquals(WalkStepTest.NO_PARENS_STREET_NAME, step.streetNameNoParens());
    }

    @Test
    public void testNameNoParensWithNameStaringWithParens() {
        step.streetName = WalkStepTest.START_WITH_PARENS_STREET_NAME;
        Assert.assertEquals(WalkStepTest.START_WITH_PARENS_STREET_NAME, step.streetNameNoParens());
    }

    @Test
    public void testNameNoParensWithNameWithParens() {
        step.streetName = WalkStepTest.PARENS_STREET_NAME;
        Assert.assertEquals(WalkStepTest.NO_PARENS_STREET_NAME, step.streetNameNoParens());
    }
}

