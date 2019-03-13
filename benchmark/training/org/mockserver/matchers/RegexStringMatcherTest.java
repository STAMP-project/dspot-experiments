package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.model.NottableString;


/**
 *
 *
 * @author jamesdbloom
 */
public class RegexStringMatcherTest {
    @Test
    public void shouldMatchMatchingString() {
        Assert.assertTrue(matches("some_value"));
    }

    @Test
    public void shouldMatchUnMatchingNottedString() {
        Assert.assertTrue(new RegexStringMatcher(new MockServerLogger(), "some_value").matches(null, NottableString.not("not_value")));
    }

    @Test
    public void shouldMatchUnMatchingNottedMatcher() {
        Assert.assertTrue(matches("some_value"));
    }

    @Test
    public void shouldMatchUnMatchingNottedMatcherAndNottedString() {
        Assert.assertFalse(new RegexStringMatcher(new MockServerLogger(), NottableString.not("not_matcher")).matches(null, NottableString.not("not_value")));
    }

    @Test
    public void shouldNotMatchMatchingNottedString() {
        Assert.assertFalse(new RegexStringMatcher(new MockServerLogger(), "some_value").matches(null, NottableString.not("some_value")));
    }

    @Test
    public void shouldNotMatchMatchingNottedMatcher() {
        Assert.assertFalse(matches("some_value"));
    }

    @Test
    public void shouldNotMatchMatchingNottedMatcherAndNottedString() {
        Assert.assertTrue(new RegexStringMatcher(new MockServerLogger(), NottableString.not("some_value")).matches(null, NottableString.not("some_value")));
    }

    @Test
    public void shouldNotMatchMatchingString() {
        Assert.assertFalse(matches("some_value"));
    }

    @Test
    public void shouldMatchMatchingStringWithRegexSymbols() {
        Assert.assertTrue(matches("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
    }

    @Test
    public void shouldMatchMatchingRegex() {
        Assert.assertTrue(matches("some_value"));
    }

    @Test
    public void shouldMatchNullExpectation() {
        Assert.assertTrue(matches("some_value"));
    }

    @Test
    public void shouldMatchEmptyExpectation() {
        Assert.assertTrue(matches("some_value"));
    }

    @Test
    public void shouldNotMatchIncorrectString() {
        Assert.assertFalse(matches("not_matching"));
    }

    @Test
    public void shouldMatchIncorrectString() {
        Assert.assertTrue(matches("not_matching"));
    }

    @Test
    public void shouldNotMatchIncorrectStringWithRegexSymbols() {
        Assert.assertFalse(matches("text/html,application/xhtml+xml,application/xml;q=0.9;q=0.8"));
    }

    @Test
    public void shouldNotMatchIncorrectRegex() {
        Assert.assertFalse(matches("some_value"));
    }

    @Test
    public void shouldNotMatchNullTest() {
        Assert.assertFalse(new RegexStringMatcher(new MockServerLogger(), "some_value").matches(null, NottableString.string(null)));
    }

    @Test
    public void shouldNotMatchEmptyTest() {
        Assert.assertFalse(matches(""));
    }

    @Test
    public void shouldHandleIllegalRegexPatternForExpectationAndTest() {
        Assert.assertFalse(matches("/{{}"));
        Assert.assertFalse(matches("some_value"));
        Assert.assertFalse(matches("/{}"));
    }

    @Test
    public void shouldHandleIllegalRegexPatternForExpectation() {
        Assert.assertFalse(matches("some_value"));
    }

    @Test
    public void shouldHandleIllegalRegexPatternForTest() {
        Assert.assertFalse(matches("/{}"));
    }
}

