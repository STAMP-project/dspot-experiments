/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.hamcrest;


import java.io.Serializable;
import java.util.HashMap;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.Test;
import org.mockitoutil.TestBase;


public class MatcherGenericTypeExtractorTest extends TestBase {
    // traditional inner class for matcher
    private class IntMatcher extends BaseMatcher<Integer> {
        public boolean matches(Object o) {
            return true;
        }

        public void describeTo(Description description) {
        }
    }

    // static class with matcher
    private static class StaticIntMatcher extends BaseMatcher<Integer> {
        public boolean matches(Object o) {
            return true;
        }

        public void describeTo(Description description) {
        }
    }

    // static subclass
    private static class StaticIntMatcherSubclass extends MatcherGenericTypeExtractorTest.StaticIntMatcher {
        public boolean matches(Object o) {
            return true;
        }

        public void describeTo(Description description) {
        }
    }

    // non-generic
    @SuppressWarnings("rawtypes")
    private static class NonGenericMatcher extends BaseMatcher {
        public boolean matches(Object o) {
            return true;
        }

        public void describeTo(Description description) {
        }
    }

    // Matcher interface implementation (instead of the BaseMatcher)
    private class IntMatcherFromInterface extends BaseMatcher<Integer> {
        public boolean matches(Object o) {
            return true;
        }

        public void describeMismatch(Object item, Description mismatchDescription) {
        }

        public void describeTo(Description description) {
        }
    }

    // Static Matcher interface implementation (instead of the BaseMatcher)
    private static class StaticIntMatcherFromInterface extends BaseMatcher<Integer> {
        public boolean matches(Object o) {
            return true;
        }

        public void describeMismatch(Object item, Description mismatchDescription) {
        }

        public void describeTo(Description description) {
        }
    }

    // non-generic matcher implementing the interface
    @SuppressWarnings("rawtypes")
    private static class NonGenericMatcherFromInterface extends BaseMatcher {
        public boolean matches(Object o) {
            return true;
        }

        public void describeMismatch(Object item, Description mismatchDescription) {
        }

        public void describeTo(Description description) {
        }
    }

    private interface IMatcher extends Matcher<Integer> {}

    // non-generic matcher implementing the interface
    private static class SubclassGenericMatcherFromInterface extends BaseMatcher<Integer> implements Serializable , Cloneable , MatcherGenericTypeExtractorTest.IMatcher {
        public boolean matches(Object o) {
            return true;
        }

        public void describeMismatch(Object item, Description mismatchDescription) {
        }

        public void describeTo(Description description) {
        }
    }

    // I refuse to comment on the sanity of this case
    private static class InsaneEdgeCase extends MatcherGenericTypeExtractorTest.SubclassGenericMatcherFromInterface {}

    @Test
    public void findsGenericType() {
        Assert.assertEquals(Integer.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(MatcherGenericTypeExtractorTest.IntMatcher.class));
        Assert.assertEquals(Integer.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(MatcherGenericTypeExtractorTest.StaticIntMatcher.class));
        Assert.assertEquals(Integer.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(MatcherGenericTypeExtractorTest.IntMatcherFromInterface.class));
        Assert.assertEquals(Integer.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(MatcherGenericTypeExtractorTest.StaticIntMatcherSubclass.class));
        Assert.assertEquals(Integer.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(MatcherGenericTypeExtractorTest.IntMatcherFromInterface.class));
        Assert.assertEquals(Integer.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(MatcherGenericTypeExtractorTest.StaticIntMatcherFromInterface.class));
        Assert.assertEquals(Integer.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(MatcherGenericTypeExtractorTest.SubclassGenericMatcherFromInterface.class));
        Assert.assertEquals(Integer.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(MatcherGenericTypeExtractorTest.InsaneEdgeCase.class));
        Assert.assertEquals(Integer.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(new BaseMatcher<Integer>() {
            public void describeTo(Description description) {
            }

            public boolean matches(Object o) {
                return false;
            }
        }.getClass()));
        Assert.assertEquals(Integer.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(new BaseMatcher<Integer>() {
            public void describeTo(Description description) {
            }

            public boolean matches(Object o) {
                return false;
            }

            public void describeMismatch(Object item, Description mismatchDescription) {
            }
        }.getClass()));
        Assert.assertEquals(Object.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(Object.class));
        Assert.assertEquals(Object.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(String.class));
        Assert.assertEquals(Object.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(HashMap.class));
        Assert.assertEquals(Object.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(new HashMap<String, String>() {}.getClass()));
        Assert.assertEquals(Object.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(MatcherGenericTypeExtractorTest.NonGenericMatcher.class));
        Assert.assertEquals(Object.class, MatcherGenericTypeExtractor.genericTypeOfMatcher(MatcherGenericTypeExtractorTest.NonGenericMatcherFromInterface.class));
    }
}

