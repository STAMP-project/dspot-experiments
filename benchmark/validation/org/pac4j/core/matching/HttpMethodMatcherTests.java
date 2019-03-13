package org.pac4j.core.matching;


import HTTP_METHOD.POST;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.Executable;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

import static HTTP_METHOD.DELETE;
import static HTTP_METHOD.GET;
import static HTTP_METHOD.POST;
import static HTTP_METHOD.PUT;


/**
 * Tests {@link HttpMethodMatcher}.
 *
 * @author Jerome Leleu
 * @since 1.9.3
 */
public final class HttpMethodMatcherTests implements TestsConstants {
    @Test
    public void testNullMethods() {
        final HttpMethodMatcher matcher = new HttpMethodMatcher();
        TestsHelper.expectException(() -> matcher.matches(MockWebContext.create()), TechnicalException.class, "methods cannot be null");
    }

    @Test
    public void testBadMethod() {
        final HttpMethodMatcher matcher = new HttpMethodMatcher(GET);
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name());
        Assert.assertFalse(matcher.matches(context));
    }

    @Test
    public void testGoodMethod() {
        final HttpMethodMatcher matcher = new HttpMethodMatcher(POST);
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name());
        Assert.assertTrue(matcher.matches(context));
    }

    @Test
    public void testBadMethod2() {
        final HttpMethodMatcher matcher = new HttpMethodMatcher(GET, PUT);
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name());
        Assert.assertFalse(matcher.matches(context));
    }

    @Test
    public void testGoodMethod2() {
        final HttpMethodMatcher matcher = new HttpMethodMatcher(DELETE, POST);
        final MockWebContext context = MockWebContext.create().setRequestMethod(POST.name());
        Assert.assertTrue(matcher.matches(context));
    }
}

