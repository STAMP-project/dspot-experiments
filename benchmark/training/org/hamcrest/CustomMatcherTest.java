package org.hamcrest;


import org.junit.Test;


public final class CustomMatcherTest {
    @Test
    public void usesStaticDescription() throws Exception {
        Matcher<String> matcher = new CustomMatcher<String>("I match strings") {
            @Override
            public boolean matches(Object item) {
                return item instanceof String;
            }
        };
        AbstractMatcherTest.assertDescription("I match strings", matcher);
    }
}

