package org.pac4j.http.authorization.authorizer;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;


/**
 * This class tests the {@link IpRegexpAuthorizer}.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public final class IpRegexpAuthorizerTests {
    private static final String GOOD_IP = "goodIp";

    private static final String BAD_IP = "badIp";

    private static final IpRegexpAuthorizer authorizer = new IpRegexpAuthorizer(IpRegexpAuthorizerTests.GOOD_IP);

    @Test(expected = TechnicalException.class)
    public void testNoPattern() {
        final IpRegexpAuthorizer authorizer = new IpRegexpAuthorizer();
        authorizer.isAuthorized(MockWebContext.create(), null);
    }

    @Test
    public void testValidateGoodIP() {
        Assert.assertTrue(IpRegexpAuthorizerTests.authorizer.isAuthorized(MockWebContext.create().setRemoteAddress(IpRegexpAuthorizerTests.GOOD_IP), null));
    }

    @Test
    public void testValidateBadIP() {
        Assert.assertFalse(IpRegexpAuthorizerTests.authorizer.isAuthorized(MockWebContext.create().setRemoteAddress(IpRegexpAuthorizerTests.BAD_IP), null));
    }
}

