package org.keycloak.testsuite.oauth;


import Details.CLIENT_AUTH_METHOD;
import Details.CODE_ID;
import JWTClientSecretAuthenticator.PROVIDER_ID;
import OAuth2Constants.CODE;
import OAuthClient.AccessTokenResponse;
import org.jboss.logging.Logger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.util.OAuthClient;


/**
 *
 *
 * @author Takashi Norimatsu <takashi.norimatsu.ws@hitachi.com>
 */
public class ClientAuthSecretSignedJWTTest extends AbstractKeycloakTest {
    private static final Logger logger = Logger.getLogger(ClientAuthSecretSignedJWTTest.class);

    @Rule
    public AssertEvents events = new AssertEvents(this);

    // TEST SUCCESS
    @Test
    public void testCodeToTokenRequestSuccess() throws Exception {
        oauth.clientId("test-app");
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().client("test-app").assertEvent();
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = doAccessTokenRequest(code, getClientSignedJWT("password", 20));
        Assert.assertEquals(200, response.getStatusCode());
        oauth.verifyToken(response.getAccessToken());
        oauth.parseRefreshToken(response.getRefreshToken());
        events.expectCodeToToken(loginEvent.getDetails().get(CODE_ID), loginEvent.getSessionId()).client(oauth.getClientId()).detail(CLIENT_AUTH_METHOD, PROVIDER_ID).assertEvent();
    }

    // TEST ERRORS
    @Test
    public void testAssertionInvalidSignature() throws Exception {
        oauth.clientId("test-app");
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().client("test-app").assertEvent();
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = doAccessTokenRequest(code, getClientSignedJWT("ppassswordd", 20));
        // https://tools.ietf.org/html/rfc6749#section-5.2
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("unauthorized_client", response.getError());
    }

    @Test
    public void testAssertionReuse() throws Exception {
        oauth.clientId("test-app");
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().client("test-app").assertEvent();
        String code = oauth.getCurrentQuery().get(CODE);
        String clientSignedJWT = getClientSignedJWT("password", 20);
        OAuthClient.AccessTokenResponse response = doAccessTokenRequest(code, clientSignedJWT);
        Assert.assertEquals(200, response.getStatusCode());
        events.expectCodeToToken(loginEvent.getDetails().get(CODE_ID), loginEvent.getSessionId()).client(oauth.getClientId()).detail(CLIENT_AUTH_METHOD, PROVIDER_ID).assertEvent();
        // 2nd attempt to use same clientSignedJWT should fail
        oauth.openLoginForm();
        loginEvent = events.expectLogin().client("test-app").assertEvent();
        String code2 = oauth.getCurrentQuery().get(CODE);
        response = doAccessTokenRequest(code2, clientSignedJWT);
        events.expectCodeToToken(loginEvent.getDetails().get(CODE_ID), loginEvent.getSessionId()).error("invalid_client_credentials").clearDetails().user(((String) (null))).session(((String) (null))).assertEvent();
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals("unauthorized_client", response.getError());
    }
}

