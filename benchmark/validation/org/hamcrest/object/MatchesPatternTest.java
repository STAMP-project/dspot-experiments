package org.hamcrest.object;


import java.util.regex.Pattern;
import org.hamcrest.AbstractMatcherTest;
import org.hamcrest.Matcher;
import org.hamcrest.text.MatchesPattern;
import org.junit.Assert;
import org.junit.Test;


public class MatchesPatternTest {
    @Test
    public void copesWithNullsAndUnknownTypes() {
        Matcher<String> matcher = new MatchesPattern(Pattern.compile("."));
        AbstractMatcherTest.assertNullSafe(matcher);
        AbstractMatcherTest.assertUnknownTypeSafe(matcher);
    }

    @Test
    public void matchesExactString() {
        Assert.assertThat("a", new MatchesPattern(Pattern.compile("a")));
    }

    @Test
    public void doesNotMatchADifferentString() {
        AbstractMatcherTest.assertDoesNotMatch("A different string does not match", new MatchesPattern(Pattern.compile("a")), "b");
    }

    @Test
    public void doesNotMatchSubstring() {
        AbstractMatcherTest.assertDoesNotMatch("A substring does not match", new MatchesPattern(Pattern.compile("a")), "ab");
    }

    @Test
    public void hasAReadableDescription() {
        Matcher<?> m = new MatchesPattern(Pattern.compile("a[bc](d|e)"));
        AbstractMatcherTest.assertDescription("a string matching the pattern 'a[bc](d|e)'", m);
    }

    @Test
    public void describesAMismatch() {
        final Matcher<String> matcher = new MatchesPattern(Pattern.compile("a"));
        AbstractMatcherTest.assertMismatchDescription("was \"Cheese\"", matcher, "Cheese");
    }

    @Test
    public void factoryMethodAllowsCreationWithPattern() {
        Matcher<?> m = MatchesPattern.matchesPattern(Pattern.compile("a[bc](d|e)"));
        AbstractMatcherTest.assertDescription("a string matching the pattern 'a[bc](d|e)'", m);
    }

    @Test
    public void factoryMethodAllowsCreationWithString() {
        Matcher<?> m = MatchesPattern.matchesPattern("a[bc](d|e)");
        AbstractMatcherTest.assertDescription("a string matching the pattern 'a[bc](d|e)'", m);
    }
}

