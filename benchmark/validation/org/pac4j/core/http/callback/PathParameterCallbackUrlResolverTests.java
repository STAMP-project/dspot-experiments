package org.pac4j.core.http.callback;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link PathParameterCallbackUrlResolver}.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public final class PathParameterCallbackUrlResolverTests implements TestsConstants {
    private static final PathParameterCallbackUrlResolver resolver = new PathParameterCallbackUrlResolver();

    @Test
    public void testCompute() {
        final String url = PathParameterCallbackUrlResolverTests.resolver.compute(new DefaultUrlResolver(), TestsConstants.CALLBACK_URL, TestsConstants.CLIENT_NAME, MockWebContext.create());
        Assert.assertEquals((((TestsConstants.CALLBACK_URL) + "/") + (TestsConstants.CLIENT_NAME)), url);
    }

    @Test
    public void testMatchesNoClientName() {
        Assert.assertFalse(PathParameterCallbackUrlResolverTests.resolver.matches(TestsConstants.CLIENT_NAME, MockWebContext.create()));
    }

    @Test
    public void testMatchesSimplePath() {
        final MockWebContext context = MockWebContext.create();
        context.setPath(TestsConstants.CLIENT_NAME);
        Assert.assertTrue(PathParameterCallbackUrlResolverTests.resolver.matches(TestsConstants.CLIENT_NAME, context));
    }

    @Test
    public void testMatchesComplexPath() {
        final MockWebContext context = MockWebContext.create();
        context.setPath((((TestsConstants.VALUE) + "/") + (TestsConstants.CLIENT_NAME)));
        Assert.assertTrue(PathParameterCallbackUrlResolverTests.resolver.matches(TestsConstants.CLIENT_NAME, context));
    }
}

