package org.hamcrest.core;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsNullTest {
    private final Matcher<Object> nullMatcher = IsNull.nullValue();

    private final Matcher<Object> notNullMatcher = IsNull.notNullValue();

    @Test
    public void copesWithNullsAndUnknownTypes() {
        AbstractMatcherTest.assertNullSafe(nullMatcher);
        AbstractMatcherTest.assertUnknownTypeSafe(nullMatcher);
        AbstractMatcherTest.assertNullSafe(notNullMatcher);
        AbstractMatcherTest.assertUnknownTypeSafe(notNullMatcher);
    }

    @Test
    public void evaluatesToTrueIfArgumentIsNull() {
        AbstractMatcherTest.assertMatches(nullMatcher, null);
        AbstractMatcherTest.assertDoesNotMatch(nullMatcher, new Object());
        AbstractMatcherTest.assertMatches(notNullMatcher, new Object());
        AbstractMatcherTest.assertDoesNotMatch(notNullMatcher, null);
    }

    @Test
    public void supportsStaticTyping() {
        requiresStringMatcher(IsNull.nullValue(String.class));
        requiresStringMatcher(IsNull.notNullValue(String.class));
    }
}

