package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


public final class IsAnythingTest {
    private final Matcher<Object> matcher = IsAnything.anything();

    private static class CustomThing {}

    @Test
    public void alwaysEvaluatesToTrue() {
        AbstractMatcherTest.assertMatches("didn't match null", matcher, null);
        AbstractMatcherTest.assertMatches("didn't match Object", matcher, new Object());
        AbstractMatcherTest.assertMatches("didn't match custom object", matcher, new IsAnythingTest.CustomThing());
        AbstractMatcherTest.assertMatches("didn't match String", matcher, "hi");
    }

    @Test
    public void compilesWithoutTypeWarnings() {
        MatcherAssert.assertThat(new IsAnythingTest.CustomThing(), Is.is(IsAnything.anything()));
    }

    @Test
    public void hasUsefulDefaultDescription() {
        AbstractMatcherTest.assertDescription("ANYTHING", matcher);
    }

    @Test
    public void canOverrideDescription() {
        String description = "description";
        AbstractMatcherTest.assertDescription(description, IsAnything.anything(description));
    }
}

