package org.springframework.security.oauth.examples.tonr;


import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.DefaultAccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.password.ResourceOwnerPasswordResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;


/**
 *
 *
 * @author Dave Syer
 */
public class RefreshTokenGrantTests {
    @Rule
    public ServerRunning serverRunning = ServerRunning.isRunning();

    private OAuth2AccessToken existingToken;

    private ResourceOwnerPasswordResourceDetails resource;

    @Test
    public void testConnectDirectlyToResourceServer() throws Exception {
        Assert.assertNotNull(existingToken.getRefreshToken());
        // It won't be expired on the server, but we can force the client to refresh it
        Assert.assertTrue(existingToken.isExpired());
        AccessTokenRequest request = new DefaultAccessTokenRequest();
        request.setExistingToken(existingToken);
        OAuth2RestTemplate template = new OAuth2RestTemplate(resource, new org.springframework.security.oauth2.client.DefaultOAuth2ClientContext(request));
        String result = template.getForObject(serverRunning.getUrl("/sparklr2/photos/user/message"), String.class);
        Assert.assertEquals("Hello, Trusted User marissa", result);
        Assert.assertFalse("Tokens match so there was no refresh", existingToken.equals(template.getAccessToken()));
    }
}

