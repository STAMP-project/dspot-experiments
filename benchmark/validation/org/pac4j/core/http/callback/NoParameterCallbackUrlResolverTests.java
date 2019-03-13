package org.pac4j.core.http.callback;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.http.url.DefaultUrlResolver;
import org.pac4j.core.util.TestsConstants;


/**
 * Tests {@link NoParameterCallbackUrlResolver}.
 *
 * @author Jerome Leleu
 * @since 3.0.0
 */
public final class NoParameterCallbackUrlResolverTests implements TestsConstants {
    private static final NoParameterCallbackUrlResolver resolver = new NoParameterCallbackUrlResolver();

    @Test
    public void testCompute() {
        Assert.assertEquals(TestsConstants.CALLBACK_URL, NoParameterCallbackUrlResolverTests.resolver.compute(new DefaultUrlResolver(), TestsConstants.CALLBACK_URL, TestsConstants.CLIENT_NAME, MockWebContext.create()));
    }

    @Test
    public void testMatches() {
        Assert.assertFalse(NoParameterCallbackUrlResolverTests.resolver.matches(TestsConstants.CLIENT_NAME, MockWebContext.create()));
    }
}

