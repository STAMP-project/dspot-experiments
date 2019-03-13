package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsNotTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<String> matcher = IsNot.not("something");
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void evaluatesToTheTheLogicalNegationOfAnotherMatcher() {
        final Matcher<String> matcher = IsNot.not(IsEqual.equalTo("A"));
        AbstractMatcherTest.assertMatches(matcher, "B");
        AbstractMatcherTest.assertDoesNotMatch(matcher, "A");
    }

    @Test
    public void providesConvenientShortcutForNotEqualTo() {
        final Matcher<String> matcher = IsNot.not("A");
        AbstractMatcherTest.assertMatches(matcher, "B");
        AbstractMatcherTest.assertDoesNotMatch(matcher, "A");
    }

    @Test
    public void usesDescriptionOfNegatedMatcherWithPrefix() {
        AbstractMatcherTest.assertDescription("not an instance of java.lang.String", IsNot.not(IsInstanceOf.instanceOf(String.class)));
        AbstractMatcherTest.assertDescription("not \"A\"", IsNot.not("A"));
    }
}

