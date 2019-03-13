package org.hamcrest.text;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsBlankStringTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<String> matcher = IsBlankString.blankString();
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void matchesEmptyString() {
        AbstractMatcherTest.assertMatches(IsBlankString.blankOrNullString(), "");
        AbstractMatcherTest.assertMatches(IsBlankString.blankString(), "");
    }

    @Test
    public void matchesNullAppropriately() {
        AbstractMatcherTest.assertMatches(IsBlankString.blankOrNullString(), null);
        AbstractMatcherTest.assertDoesNotMatch(IsBlankString.blankString(), null);
    }

    @Test
    public void matchesBlankStringAppropriately() {
        AbstractMatcherTest.assertMatches(IsBlankString.blankString(), " \t");
        AbstractMatcherTest.assertMatches(IsBlankString.blankOrNullString(), " \t");
    }

    @Test
    public void doesNotMatchFilledString() {
        AbstractMatcherTest.assertDoesNotMatch(IsBlankString.blankString(), "a");
        AbstractMatcherTest.assertDoesNotMatch(IsBlankString.blankOrNullString(), "a");
    }

    @Test
    public void describesItself() {
        AbstractMatcherTest.assertDescription("a blank string", IsBlankString.blankString());
        AbstractMatcherTest.assertDescription("(null or a blank string)", IsBlankString.blankOrNullString());
    }

    @Test
    public void describesAMismatch() {
        AbstractMatcherTest.assertMismatchDescription("was \"a\"", IsBlankString.blankString(), "a");
        AbstractMatcherTest.assertMismatchDescription("was \"a\"", IsBlankString.blankOrNullString(), "a");
    }
}

