package org.hamcrest;


import org.junit.Assert;
import org.junit.Test;


public final class BaseMatcherTest {
    @Test
    public void describesItselfWithToStringMethod() {
        Matcher<Object> someMatcher = new BaseMatcher<Object>() {
            @Override
            public boolean matches(Object item) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("SOME DESCRIPTION");
            }
        };
        Assert.assertEquals("SOME DESCRIPTION", someMatcher.toString());
    }
}

