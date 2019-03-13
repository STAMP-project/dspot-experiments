package org.pac4j.core.matching;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * Tests {@link RequireAllMatchersChecker}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class RequireAllMatchersCheckerTests implements TestsConstants {
    private static final MatchingChecker checker = new RequireAllMatchersChecker();

    private static class NullContextMatcher implements Matcher {
        @Override
        public boolean matches(final WebContext context) {
            return context != null;
        }
    }

    private static class AlwaysFalseMatcher implements Matcher {
        @Override
        public boolean matches(final WebContext context) {
            return false;
        }
    }

    @Test
    public void testNoMatcherName() {
        Assert.assertTrue(RequireAllMatchersCheckerTests.checker.matches(null, null, new HashMap()));
    }

    @Test
    public void testNoMatchers() {
        TestsHelper.expectException(() -> RequireAllMatchersCheckerTests.checker.matches(null, TestsConstants.NAME, null), TechnicalException.class, "matchersMap cannot be null");
    }

    @Test
    public void testNoExistingMatcher() {
        TestsHelper.expectException(() -> RequireAllMatchersCheckerTests.checker.matches(null, TestsConstants.NAME, new HashMap()), TechnicalException.class, (("matchersMap['" + (TestsConstants.NAME)) + "'] cannot be null"));
    }

    @Test
    public void testMatch() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(TestsConstants.NAME, new RequireAllMatchersCheckerTests.NullContextMatcher());
        Assert.assertTrue(RequireAllMatchersCheckerTests.checker.matches(MockWebContext.create(), TestsConstants.NAME, matchers));
    }

    @Test
    public void testMatchCasTrim() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(TestsConstants.NAME, new RequireAllMatchersCheckerTests.NullContextMatcher());
        Assert.assertTrue(RequireAllMatchersCheckerTests.checker.matches(MockWebContext.create(), "  NAmE  ", matchers));
    }

    @Test
    public void testDontMatch() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(TestsConstants.NAME, new RequireAllMatchersCheckerTests.NullContextMatcher());
        Assert.assertFalse(RequireAllMatchersCheckerTests.checker.matches(null, TestsConstants.NAME, matchers));
    }

    @Test
    public void testMatchAll() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(TestsConstants.NAME, new RequireAllMatchersCheckerTests.NullContextMatcher());
        matchers.put(TestsConstants.VALUE, new RequireAllMatchersCheckerTests.NullContextMatcher());
        Assert.assertTrue(RequireAllMatchersCheckerTests.checker.matches(MockWebContext.create(), (((TestsConstants.NAME) + (Pac4jConstants.ELEMENT_SEPARATOR)) + (TestsConstants.VALUE)), matchers));
    }

    @Test
    public void testDontMatchOneOfThem() {
        final Map<String, Matcher> matchers = new HashMap<>();
        matchers.put(TestsConstants.NAME, new RequireAllMatchersCheckerTests.NullContextMatcher());
        matchers.put(TestsConstants.VALUE, new RequireAllMatchersCheckerTests.AlwaysFalseMatcher());
        Assert.assertFalse(RequireAllMatchersCheckerTests.checker.matches(MockWebContext.create(), (((TestsConstants.NAME) + (Pac4jConstants.ELEMENT_SEPARATOR)) + (TestsConstants.VALUE)), matchers));
    }
}

