package org.keycloak.testsuite.oauth;


import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author <a href="mailto:mkanis@redhat.com">Martin Kanis</a>
 */
public class TokenEndpointCorsTest extends AbstractKeycloakTest {
    private static final String VALID_CORS_URL = "http://localtest.me:8180";

    private static final String INVALID_CORS_URL = "http://invalid.localtest.me:8180";

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void preflightRequest() throws Exception {
        CloseableHttpResponse response = oauth.doPreflightRequest();
        String[] methods = response.getHeaders("Access-Control-Allow-Methods")[0].getValue().split(", ");
        Set allowedMethods = new HashSet(Arrays.asList(methods));
        Assert.assertEquals(2, allowedMethods.size());
        Assert.assertTrue(allowedMethods.containsAll(Arrays.asList("POST", "OPTIONS")));
    }

    @Test
    public void accessTokenCorsRequest() throws Exception {
        oauth.realm("test");
        oauth.clientId("test-app2");
        oauth.redirectUri(((TokenEndpointCorsTest.VALID_CORS_URL) + "/realms/master/app"));
        oauth.doLogin("test-user@localhost", "password");
        // Token request
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.origin(TokenEndpointCorsTest.VALID_CORS_URL);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(200, response.getStatusCode());
        TokenEndpointCorsTest.assertCors(response);
        // Refresh request
        response = oauth.doRefreshTokenRequest(response.getRefreshToken(), null);
        Assert.assertEquals(200, response.getStatusCode());
        TokenEndpointCorsTest.assertCors(response);
        // Invalid origin
        oauth.origin(TokenEndpointCorsTest.INVALID_CORS_URL);
        response = oauth.doRefreshTokenRequest(response.getRefreshToken(), "password");
        Assert.assertEquals(200, response.getStatusCode());
        TokenEndpointCorsTest.assertNotCors(response);
        oauth.origin(TokenEndpointCorsTest.VALID_CORS_URL);
        // No session
        oauth.openLogout();
        response = oauth.doRefreshTokenRequest(response.getRefreshToken(), null);
        Assert.assertEquals(400, response.getStatusCode());
        TokenEndpointCorsTest.assertCors(response);
        Assert.assertEquals("invalid_grant", response.getError());
        Assert.assertEquals("Session not active", response.getErrorDescription());
    }

    @Test
    public void accessTokenResourceOwnerCorsRequest() throws Exception {
        oauth.realm("test");
        oauth.clientId("test-app2");
        oauth.origin(TokenEndpointCorsTest.VALID_CORS_URL);
        // Token request
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("password", "test-user@localhost", "password");
        Assert.assertEquals(200, response.getStatusCode());
        TokenEndpointCorsTest.assertCors(response);
        // Invalid password
        response = oauth.doGrantAccessTokenRequest("password", "test-user@localhost", "invalid");
        Assert.assertEquals(401, response.getStatusCode());
        TokenEndpointCorsTest.assertCors(response);
    }
}

