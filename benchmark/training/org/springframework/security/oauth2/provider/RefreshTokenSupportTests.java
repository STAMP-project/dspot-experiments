package org.springframework.security.oauth2.provider;


import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.security.oauth2.common.OAuth2AccessToken;


/**
 *
 *
 * @author Ryan Heaton
 * @author Dave Syer
 */
public class RefreshTokenSupportTests {
    @Rule
    public ServerRunning serverRunning = ServerRunning.isRunning();

    /**
     * tests a happy-day flow of the refresh token provider.
     */
    @Test
    public void testHappyDay() throws Exception {
        OAuth2AccessToken accessToken = getAccessToken("read", "my-trusted-client");
        // now use the refresh token to get a new access token.
        Assert.assertNotNull(accessToken.getRefreshToken());
        OAuth2AccessToken newAccessToken = refreshAccessToken(accessToken.getRefreshToken().getValue());
        Assert.assertFalse(newAccessToken.getValue().equals(accessToken.getValue()));
        // make sure the new access token can be used.
        verifyTokenResponse(newAccessToken.getValue(), OK);
        // make sure the old access token isn't valid anymore.
        verifyTokenResponse(accessToken.getValue(), UNAUTHORIZED);
    }
}

