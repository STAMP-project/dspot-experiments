package org.pac4j.core.http.callback;


import Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;
import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link QueryParameterCallbackUrlResolver}.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public final class QueryParameterCallbackUrlResolverTests implements TestsConstants {
    private static final QueryParameterCallbackUrlResolver resolver = new QueryParameterCallbackUrlResolver();

    @Test
    public void testParams() {
        final String url = new QueryParameterCallbackUrlResolver(ImmutableMap.of("param1", "value", "param2", "value2")).compute(new DefaultUrlResolver(), TestsConstants.CALLBACK_URL, TestsConstants.CLIENT_NAME, MockWebContext.create());
        Assert.assertEquals(((((((TestsConstants.CALLBACK_URL) + '?') + (Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER)) + '=') + (TestsConstants.CLIENT_NAME)) + "&param1=value&param2=value2"), url);
    }

    @Test
    public void testCompute() {
        final String url = QueryParameterCallbackUrlResolverTests.resolver.compute(new DefaultUrlResolver(), TestsConstants.CALLBACK_URL, TestsConstants.CLIENT_NAME, MockWebContext.create());
        Assert.assertEquals((((((TestsConstants.CALLBACK_URL) + '?') + (Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER)) + '=') + (TestsConstants.CLIENT_NAME)), url);
    }

    @Test
    public void testComputeSpecificParameter() {
        final QueryParameterCallbackUrlResolver resolver = new QueryParameterCallbackUrlResolver();
        resolver.setClientNameParameter(TestsConstants.KEY);
        final String url = resolver.compute(new DefaultUrlResolver(), TestsConstants.CALLBACK_URL, TestsConstants.CLIENT_NAME, MockWebContext.create());
        Assert.assertEquals((((((TestsConstants.CALLBACK_URL) + '?') + (TestsConstants.KEY)) + '=') + (TestsConstants.CLIENT_NAME)), url);
    }

    @Test
    public void testComputeCallbackUrlAlreadyDefined() {
        final String callbackUrl = (((TestsConstants.CALLBACK_URL) + '?') + (Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER)) + "=cn";
        final String url = QueryParameterCallbackUrlResolverTests.resolver.compute(new DefaultUrlResolver(), callbackUrl, TestsConstants.CLIENT_NAME, MockWebContext.create());
        Assert.assertEquals(callbackUrl, url);
    }

    @Test
    public void testMatchesNoClientName() {
        Assert.assertFalse(QueryParameterCallbackUrlResolverTests.resolver.matches(TestsConstants.CLIENT_NAME, MockWebContext.create()));
    }

    @Test
    public void testMatches() {
        final MockWebContext context = MockWebContext.create();
        context.addRequestParameter(DEFAULT_CLIENT_NAME_PARAMETER, TestsConstants.CLIENT_NAME);
        Assert.assertTrue(QueryParameterCallbackUrlResolverTests.resolver.matches(TestsConstants.CLIENT_NAME, context));
    }
}

