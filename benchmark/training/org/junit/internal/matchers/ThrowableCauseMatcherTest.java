package org.junit.internal.matchers;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class ThrowableCauseMatcherTest {
    @Test
    public void shouldAllowCauseOfDifferentClassFromRoot() throws Exception {
        NullPointerException expectedCause = new NullPointerException("expected");
        Exception actual = new Exception(expectedCause);
        Assert.assertThat(actual, ThrowableCauseMatcher.hasCause(CoreMatchers.is(expectedCause)));
    }
}

