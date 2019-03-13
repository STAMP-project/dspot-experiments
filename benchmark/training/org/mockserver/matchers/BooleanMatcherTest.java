package org.mockserver.matchers;


import org.junit.Assert;
import org.junit.Test;


public class BooleanMatcherTest {
    @Test
    public void shouldMatchMatchingExpectations() {
        Assert.assertTrue(new BooleanMatcher(new org.mockserver.logging.MockServerLogger(), true).matches(null, true));
        Assert.assertTrue(new BooleanMatcher(new org.mockserver.logging.MockServerLogger(), false).matches(null, false));
    }

    @Test
    public void shouldMatchNullExpectations() {
        Assert.assertTrue(matches(null, null));
        Assert.assertTrue(new BooleanMatcher(new org.mockserver.logging.MockServerLogger(), null).matches(null, false));
    }

    @Test
    public void shouldNotMatchNonMatchingExpectations() {
        Assert.assertFalse(new BooleanMatcher(new org.mockserver.logging.MockServerLogger(), true).matches(null, false));
        Assert.assertFalse(new BooleanMatcher(new org.mockserver.logging.MockServerLogger(), false).matches(null, true));
    }

    @Test
    public void shouldNotMatchNullAgainstNonMatchingExpectations() {
        Assert.assertFalse(matches(null, null));
        Assert.assertFalse(matches(null, null));
    }
}

