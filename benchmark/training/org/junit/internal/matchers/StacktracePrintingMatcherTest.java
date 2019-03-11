package org.junit.internal.matchers;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class StacktracePrintingMatcherTest {
    @Test
    public void succeedsWhenInnerMatcherSucceeds() throws Exception {
        Assert.assertTrue(StacktracePrintingMatcher.isThrowable(CoreMatchers.any(Throwable.class)).matches(new Exception()));
    }

    @Test
    public void failsWhenInnerMatcherFails() throws Exception {
        Assert.assertFalse(StacktracePrintingMatcher.isException(CoreMatchers.notNullValue(Exception.class)).matches(null));
    }

    @Test
    public void assertThatIncludesStacktrace() {
        Exception actual = new IllegalArgumentException("my message");
        Exception expected = new NullPointerException();
        try {
            Assert.assertThat(actual, StacktracePrintingMatcher.isThrowable(CoreMatchers.equalTo(expected)));
        } catch (AssertionError e) {
            Assert.assertThat(e.getMessage(), CoreMatchers.containsString("Stacktrace was: java.lang.IllegalArgumentException: my message"));
        }
    }
}

