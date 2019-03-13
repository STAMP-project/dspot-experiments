package org.hamcrest;


import org.junit.Test;


/**
 *
 *
 * @author Steve Freeman 2016 http://www.hamcrest.com
 */
@SuppressWarnings("WeakerAccess")
public class TypeSafeDiagnosingMatcherTest {
    @Test
    public void describesMismatches() {
        AbstractMatcherTest.assertMismatchDescription("was null", TypeSafeDiagnosingMatcherTest.STRING_MATCHER, null);
        AbstractMatcherTest.assertMismatchDescription("was Character \"c\"", TypeSafeDiagnosingMatcherTest.STRING_MATCHER, 'c');
        AbstractMatcherTest.assertMismatchDescription("mismatching", TypeSafeDiagnosingMatcherTest.STRING_MATCHER, "other");
    }

    @Test
    public void detects_non_builtin_types() {
        final Matcher<TypeSafeDiagnosingMatcherTest.NotBuiltIn> matcher = new TypeSafeDiagnosingMatcher<TypeSafeDiagnosingMatcherTest.NotBuiltIn>() {
            @Override
            protected boolean matchesSafely(TypeSafeDiagnosingMatcherTest.NotBuiltIn item, Description mismatchDescription) {
                return true;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("a not builtin");
            }
        };
        AbstractMatcherTest.assertMatches("not built in", matcher, new TypeSafeDiagnosingMatcherTest.NotBuiltIn());
        AbstractMatcherTest.assertDoesNotMatch("other not built in", ((Matcher) (matcher)), new TypeSafeDiagnosingMatcherTest.OtherNotBuiltIn());
    }

    @Test
    public void filters_type_for_subclassed_matcher_when_expected_type_passed_in() {
        final Matcher<TypeSafeDiagnosingMatcherTest.NotBuiltIn> matcher = new TypeSafeDiagnosingMatcherTest.SubMatcher<>(new TypeSafeDiagnosingMatcherTest.NotBuiltIn());
        AbstractMatcherTest.assertMatches("not built in", matcher, new TypeSafeDiagnosingMatcherTest.NotBuiltIn());
        AbstractMatcherTest.assertDoesNotMatch("other not built in", ((Matcher) (matcher)), new TypeSafeDiagnosingMatcherTest.OtherNotBuiltIn());
    }

    @Test
    public void but_cannot_detect_generic_type_in_subclassed_matcher_using_reflection() {
        final Matcher<TypeSafeDiagnosingMatcherTest.NotBuiltIn> matcher = new TypeSafeDiagnosingMatcherTest.SubMatcher<>();
        AbstractMatcherTest.assertMatches("not built in", matcher, new TypeSafeDiagnosingMatcherTest.NotBuiltIn());
        AbstractMatcherTest.assertMatches("other not built in", ((Matcher) (matcher)), new TypeSafeDiagnosingMatcherTest.OtherNotBuiltIn());
    }

    private static final TypeSafeDiagnosingMatcher STRING_MATCHER = new TypeSafeDiagnosingMatcher<String>() {
        @Override
        protected boolean matchesSafely(String item, Description mismatchDescription) {
            mismatchDescription.appendText("mismatching");
            return false;
        }

        @Override
        public void describeTo(Description description) {
        }
    };

    public static class SubMatcher<T> extends TypeSafeDiagnosingMatcher<T> {
        public SubMatcher() {
            super();
        }

        public SubMatcher(T expectedObject) {
            super(expectedObject.getClass());
        }

        @Override
        protected boolean matchesSafely(T item, Description mismatchDescription) {
            return true;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("sub type");
        }
    }

    public static class NotBuiltIn {
        public final String value = "not built in";

        @Override
        public String toString() {
            return "NotBuiltIn";
        }
    }

    // empty
    public static class OtherNotBuiltIn {}
}

