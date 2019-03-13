package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<String> matcher = Is.is("something");
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void matchesTheSameWayTheUnderlyingMatcherDoes() {
        final Matcher<Boolean> matcher = Is.is(IsEqual.equalTo(true));
        AbstractMatcherTest.assertMatches(matcher, true);
        AbstractMatcherTest.assertDoesNotMatch(matcher, false);
    }

    @Test
    public void generatesIsPrefixInDescription() {
        AbstractMatcherTest.assertDescription("is <true>", Is.is(IsEqual.equalTo(true)));
        AbstractMatcherTest.assertDescription("is \"A\"", Is.is("A"));
    }

    @Test
    public void providesConvenientShortcutForIsEqualTo() {
        final Matcher<String> matcher = Is.is("A");
        AbstractMatcherTest.assertMatches(matcher, "A");
        AbstractMatcherTest.assertDoesNotMatch(Is.is("A"), "B");
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void providesConvenientShortcutForIsInstanceOf() {
        final Matcher matcher = Is.isA(Number.class);
        AbstractMatcherTest.assertMatches(matcher, 1);
        AbstractMatcherTest.assertDoesNotMatch(matcher, new Object());
        AbstractMatcherTest.assertDoesNotMatch(matcher, null);
    }
}

