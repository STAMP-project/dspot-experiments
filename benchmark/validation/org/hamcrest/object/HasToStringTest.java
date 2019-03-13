package org.hamcrest.object;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.junit.Test;


public final class HasToStringTest {
    private static final String TO_STRING_RESULT = "toString result";

    private static final Object TEST_OBJECT = new Object() {
        @Override
        public String toString() {
            return HasToStringTest.TO_STRING_RESULT;
        }
    };

    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<Object> matcher = HasToString.hasToString(IsEqual.equalTo("irrelevant"));
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void matchesWhenUtilisingANestedMatcher() {
        final Matcher<Object> matcher = HasToString.hasToString(IsEqual.equalTo(HasToStringTest.TO_STRING_RESULT));
        AbstractMatcherTest.assertMatches(matcher, HasToStringTest.TEST_OBJECT);
        AbstractMatcherTest.assertDoesNotMatch(matcher, new Object());
    }

    @Test
    public void matchesWhenUsingShortcutForHasToStringEqualTo() {
        final Matcher<Object> matcher = HasToString.hasToString(HasToStringTest.TO_STRING_RESULT);
        AbstractMatcherTest.assertMatches(matcher, HasToStringTest.TEST_OBJECT);
        AbstractMatcherTest.assertDoesNotMatch(matcher, new Object());
    }

    @Test
    public void describesItself() {
        final Matcher<Object> matcher = HasToString.hasToString(IsEqual.equalTo(HasToStringTest.TO_STRING_RESULT));
        AbstractMatcherTest.assertDescription("with toString() \"toString result\"", matcher);
    }

    @Test
    public void describesAMismatch() {
        final Matcher<Object> matcher = HasToString.hasToString(IsEqual.equalTo(HasToStringTest.TO_STRING_RESULT));
        String expectedMismatchString = "toString() was \"Cheese\"";
        AbstractMatcherTest.assertMismatchDescription(expectedMismatchString, matcher, "Cheese");
    }
}

