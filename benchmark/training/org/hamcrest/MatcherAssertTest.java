package org.hamcrest;


import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public final class MatcherAssertTest {
    @Test
    public void includesDescriptionOfTestedValueInErrorMessage() {
        String expected = "expected";
        String actual = "actual";
        String expectedMessage = "identifier\nExpected: \"expected\"\n     but: was \"actual\"";
        try {
            MatcherAssert.assertThat("identifier", actual, IsEqual.equalTo(expected));
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().startsWith(expectedMessage));
            return;
        }
        Assert.fail("should have failed");
    }

    @Test
    public void descriptionCanBeElided() {
        String expected = "expected";
        String actual = "actual";
        String expectedMessage = "\nExpected: \"expected\"\n     but: was \"actual\"";
        try {
            MatcherAssert.assertThat(actual, IsEqual.equalTo(expected));
        } catch (AssertionError e) {
            Assert.assertTrue(e.getMessage().startsWith(expectedMessage));
            return;
        }
        Assert.fail("should have failed");
    }

    @Test
    public void canTestBooleanDirectly() {
        MatcherAssert.assertThat("success reason message", true);
        try {
            MatcherAssert.assertThat("failing reason message", false);
        } catch (AssertionError e) {
            Assert.assertEquals("failing reason message", e.getMessage());
            return;
        }
        Assert.fail("should have failed");
    }

    @Test
    public void includesMismatchDescription() {
        Matcher<String> matcherWithCustomMismatchDescription = new BaseMatcher<String>() {
            @Override
            public boolean matches(Object item) {
                return false;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Something cool");
            }

            @Override
            public void describeMismatch(Object item, Description mismatchDescription) {
                mismatchDescription.appendText("Not cool");
            }
        };
        String expectedMessage = "\nExpected: Something cool\n     but: Not cool";
        try {
            MatcherAssert.assertThat("Value", matcherWithCustomMismatchDescription);
            Assert.fail("should have failed");
        } catch (AssertionError e) {
            Assert.assertEquals(expectedMessage, e.getMessage());
        }
    }

    @Test
    public void canAssertSubtypes() {
        MatcherAssert.assertThat(1, IsEqual.equalTo(((Number) (1))));
    }
}

