package org.hamcrest.text;


import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.junit.Test;


public final class IsEqualIgnoringCaseTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<String> matcher = IsEqualIgnoringCase.equalToIgnoringCase("irrelevant");
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void ignoresCaseOfCharsInString() {
        final Matcher<String> matcher = IsEqualIgnoringCase.equalToIgnoringCase("heLLo");
        AbstractMatcherTest.assertMatches(matcher, "HELLO");
        AbstractMatcherTest.assertMatches(matcher, "hello");
        AbstractMatcherTest.assertMatches(matcher, "HelLo");
        AbstractMatcherTest.assertDoesNotMatch(matcher, "bye");
    }

    @Test
    public void mismatchesIfAdditionalWhitespaceIsPresent() {
        final Matcher<String> matcher = IsEqualIgnoringCase.equalToIgnoringCase("heLLo");
        AbstractMatcherTest.assertDoesNotMatch(matcher, "hello ");
        AbstractMatcherTest.assertDoesNotMatch(matcher, " hello");
    }

    @Test
    public void mismatchesNull() {
        final Matcher<String> matcher = IsEqualIgnoringCase.equalToIgnoringCase("heLLo");
        AbstractMatcherTest.assertDoesNotMatch(matcher, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void canOnlyBeConstructedAboutANonNullString() {
        IsEqualIgnoringCase.equalToIgnoringCase(null);
    }

    @Test
    public void describesItself() {
        final Matcher<String> matcher = IsEqualIgnoringCase.equalToIgnoringCase("heLLo");
        AbstractMatcherTest.assertDescription("a string equal to \"heLLo\" ignoring case", matcher);
    }

    @Test
    public void describesAMismatch() {
        final Matcher<String> matcher = IsEqualIgnoringCase.equalToIgnoringCase("heLLo");
        String expectedMismatchString = "was \"Cheese\"";
        AbstractMatcherTest.assertMismatchDescription(expectedMismatchString, matcher, "Cheese");
    }
}

