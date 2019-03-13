package org.pac4j.http.credentials.authenticator;


import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.http.profile.IpProfile;


/**
 * This class tests the {@link IpRegexpAuthenticator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class IpRegexpAuthenticatorTests implements TestsConstants {
    private static final String GOOD_IP = "goodIp";

    private static final String BAD_IP = "badIp";

    private static final IpRegexpAuthenticator authenticator = new IpRegexpAuthenticator(IpRegexpAuthenticatorTests.GOOD_IP);

    @Test(expected = TechnicalException.class)
    public void testNoPattern() {
        final TokenCredentials credentials = new TokenCredentials(IpRegexpAuthenticatorTests.GOOD_IP);
        IpRegexpAuthenticator authenticator = new IpRegexpAuthenticator();
        authenticator.validate(credentials, null);
    }

    @Test
    public void testValidateGoodIP() {
        final TokenCredentials credentials = new TokenCredentials(IpRegexpAuthenticatorTests.GOOD_IP);
        IpRegexpAuthenticatorTests.authenticator.validate(credentials, null);
        final IpProfile profile = ((IpProfile) (credentials.getUserProfile()));
        Assert.assertEquals(IpRegexpAuthenticatorTests.GOOD_IP, profile.getId());
    }

    @Test
    public void testValidateBadIP() {
        final TokenCredentials credentials = new TokenCredentials(IpRegexpAuthenticatorTests.BAD_IP);
        TestsHelper.expectException(() -> authenticator.validate(credentials, null), CredentialsException.class, ("Unauthorized IP address: " + (IpRegexpAuthenticatorTests.BAD_IP)));
    }
}

