package org.hamcrest;


import org.hamcrest.core.IsEqual;
import org.junit.Assert;
import org.junit.Test;


public final class FeatureMatcherTest {
    private final FeatureMatcher<FeatureMatcherTest.Thingy, String> resultMatcher = FeatureMatcherTest.resultMatcher();

    @Test
    public void matchesPartOfAnObject() {
        AbstractMatcherTest.assertMatches("feature", resultMatcher, new FeatureMatcherTest.Thingy("bar"));
        AbstractMatcherTest.assertDescription("Thingy with result \"bar\"", resultMatcher);
    }

    @Test
    public void mismatchesPartOfAnObject() {
        AbstractMatcherTest.assertMismatchDescription("result mismatch-description", resultMatcher, new FeatureMatcherTest.Thingy("foo"));
    }

    @Test
    public void doesNotThrowNullPointerException() {
        AbstractMatcherTest.assertMismatchDescription("was null", resultMatcher, null);
    }

    @Test
    public void doesNotThrowClassCastException() {
        resultMatcher.matches(new FeatureMatcherTest.ShouldNotMatch());
        StringDescription mismatchDescription = new StringDescription();
        resultMatcher.describeMismatch(new FeatureMatcherTest.ShouldNotMatch(), mismatchDescription);
        Assert.assertEquals("was ShouldNotMatch <ShouldNotMatch>", mismatchDescription.toString());
    }

    public static class Match extends IsEqual<String> {
        public Match(String equalArg) {
            super(equalArg);
        }

        @Override
        public void describeMismatch(Object item, Description description) {
            description.appendText("mismatch-description");
        }
    }

    public static class Thingy {
        private final String result;

        public Thingy(String result) {
            this.result = result;
        }

        public String getResult() {
            return result;
        }
    }

    public static class ShouldNotMatch {
        @Override
        public String toString() {
            return "ShouldNotMatch";
        }
    }
}

