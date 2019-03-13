package org.hamcrest;


import org.junit.Test;


public final class CustomTypeSafeMatcherTest {
    private static final String STATIC_DESCRIPTION = "I match non empty strings";

    private final Matcher<String> customMatcher = new CustomTypeSafeMatcher<String>(CustomTypeSafeMatcherTest.STATIC_DESCRIPTION) {
        @Override
        public boolean matchesSafely(String item) {
            return false;
        }

        @Override
        public void describeMismatchSafely(String item, Description mismatchDescription) {
            mismatchDescription.appendText(("an " + item));
        }
    };

    @Test
    public void usesStaticDescription() throws Exception {
        AbstractMatcherTest.assertDescription(CustomTypeSafeMatcherTest.STATIC_DESCRIPTION, customMatcher);
    }

    @Test
    public void reportsMismatch() {
        AbstractMatcherTest.assertMismatchDescription("an item", customMatcher, "item");
    }

    @Test
    public void isNullSafe() {
        AbstractMatcherTest.assertNullSafe(customMatcher);
    }

    @Test
    public void copesWithUnknownTypes() {
        AbstractMatcherTest.assertUnknownTypeSafe(customMatcher);
    }
}

