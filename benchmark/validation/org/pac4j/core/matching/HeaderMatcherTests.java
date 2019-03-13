package org.pac4j.core.matching;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;


/**
 * Tests {@link HeaderMatcher}.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
public final class HeaderMatcherTests implements TestsConstants {
    @Test
    public void testNullHeaderName() {
        final HeaderMatcher matcher = new HeaderMatcher();
        TestsHelper.expectException(() -> matcher.matches(MockWebContext.create()), TechnicalException.class, "headerName cannot be blank");
    }

    @Test
    public void testNullExpectedValueHeader() {
        final HeaderMatcher matcher = new HeaderMatcher(TestsConstants.NAME, null);
        final MockWebContext context = MockWebContext.create().addRequestHeader(TestsConstants.NAME, TestsConstants.VALUE);
        Assert.assertFalse(matcher.matches(context));
    }

    @Test
    public void testNullExpectedValueNull() {
        final HeaderMatcher matcher = new HeaderMatcher(TestsConstants.NAME, null);
        final MockWebContext context = MockWebContext.create();
        Assert.assertTrue(matcher.matches(context));
    }

    @Test
    public void testRegexExpectedRightValueHeader() {
        final HeaderMatcher matcher = new HeaderMatcher(TestsConstants.NAME, ".*A.*");
        final MockWebContext context = MockWebContext.create().addRequestHeader(TestsConstants.NAME, "BAC");
        Assert.assertTrue(matcher.matches(context));
    }

    @Test
    public void testRegexExpectedBadValueHeader() {
        final HeaderMatcher matcher = new HeaderMatcher(TestsConstants.NAME, ".*A.*");
        final MockWebContext context = MockWebContext.create().addRequestHeader(TestsConstants.NAME, "BOC");
        Assert.assertFalse(matcher.matches(context));
    }

    @Test
    public void testRegexExpectedNullHeader() {
        final HeaderMatcher matcher = new HeaderMatcher(TestsConstants.NAME, ".*A.*");
        final MockWebContext context = MockWebContext.create();
        Assert.assertFalse(matcher.matches(context));
    }
}

