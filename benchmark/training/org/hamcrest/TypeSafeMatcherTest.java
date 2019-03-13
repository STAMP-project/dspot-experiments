package org.hamcrest;


import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("WeakerAccess")
public final class TypeSafeMatcherTest {
    private final Matcher<String> matcher = new TypeSafeMatcherTest.TypeSafeMatcherSubclass();

    public static class TypeSafeMatcherSubclass extends TypeSafeMatcher<String> {
        @Override
        public boolean matchesSafely(String item) {
            return false;
        }

        @Override
        public void describeMismatchSafely(String item, Description mismatchDescription) {
            mismatchDescription.appendText("The mismatch");
        }

        @Override
        public void describeTo(Description description) {
        }
    }

    @Test
    public void canDetermineMatcherTypeFromProtectedMatchesSafelyMethod() {
        Assert.assertFalse(matcher.matches(null));
        Assert.assertFalse(matcher.matches(10));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void describesMismatches() {
        AbstractMatcherTest.assertMismatchDescription("was null", matcher, null);
        AbstractMatcherTest.assertMismatchDescription("was a java.lang.Integer (<3>)", ((Matcher) (matcher)), 3);
        AbstractMatcherTest.assertMismatchDescription("The mismatch", matcher, "a string");
    }
}

