package org.hamcrest.text;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsEmptyStringTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<String> matcher = IsEmptyString.emptyString();
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void matchesEmptyString() {
        AbstractMatcherTest.assertMatches(IsEmptyString.emptyOrNullString(), "");
        AbstractMatcherTest.assertMatches(IsEmptyString.emptyString(), "");
    }

    @Test
    public void matchesNullAppropriately() {
        AbstractMatcherTest.assertMatches(IsEmptyString.emptyOrNullString(), null);
        AbstractMatcherTest.assertDoesNotMatch(IsEmptyString.emptyString(), null);
    }

    @Test
    public void matchesBlankStringAppropriately() {
        AbstractMatcherTest.assertDoesNotMatch(IsEmptyString.emptyString(), "  ");
        AbstractMatcherTest.assertDoesNotMatch(IsEmptyString.emptyOrNullString(), "  ");
    }

    @Test
    public void doesNotMatchFilledString() {
        AbstractMatcherTest.assertDoesNotMatch(IsEmptyString.emptyString(), "a");
        AbstractMatcherTest.assertDoesNotMatch(IsEmptyString.emptyOrNullString(), "a");
    }

    @Test
    public void describesItself() {
        AbstractMatcherTest.assertDescription("an empty string", IsEmptyString.emptyString());
        AbstractMatcherTest.assertDescription("(null or an empty string)", IsEmptyString.emptyOrNullString());
    }

    @Test
    public void describesAMismatch() {
        AbstractMatcherTest.assertMismatchDescription("was \"a\"", IsEmptyString.emptyString(), "a");
        AbstractMatcherTest.assertMismatchDescription("was \"a\"", IsEmptyString.emptyOrNullString(), "a");
    }
}

