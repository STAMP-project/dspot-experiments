package org.keycloak.testsuite.oauth;


import Details.CODE_ID;
import Errors.CODE_VERIFIER_MISSING;
import Errors.INVALID_CODE_VERIFIER;
import Errors.PKCE_VERIFICATION_FAILED;
import OAuth2Constants.CODE;
import OAuth2Constants.PKCE_METHOD_PLAIN;
import OAuth2Constants.PKCE_METHOD_S256;
import OAuthClient.AccessTokenResponse;
import OAuthClient.AuthorizationEndpointResponse;
import OAuthErrorException.INVALID_GRANT;
import OAuthErrorException.INVALID_REQUEST;
import javax.ws.rs.core.UriBuilder;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.util.OAuthClient;


// https://tools.ietf.org/html/rfc7636
/**
 *
 *
 * @author <a href="takashi.norimatsu.ws@hitachi.com">Takashi Norimatsu</a>
 */
public class OAuthProofKeyForCodeExchangeTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void accessTokenRequestWithoutPKCE() throws Exception {
        // test case : success : A-1-1
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        expectSuccessfulResponseFromTokenEndpoint(codeId, sessionId, code);
    }

    @Test
    public void accessTokenRequestInPKCEValidS256CodeChallengeMethod() throws Exception {
        // test case : success : A-1-2
        String codeVerifier = "1234567890123456789012345678901234567890123";// 43

        String codeChallenge = generateS256CodeChallenge(codeVerifier);
        oauth.codeChallenge(codeChallenge);
        oauth.codeChallengeMethod(PKCE_METHOD_S256);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.codeVerifier(codeVerifier);
        expectSuccessfulResponseFromTokenEndpoint(codeId, sessionId, code);
    }

    @Test
    public void accessTokenRequestInPKCEUnmatchedCodeVerifierWithS256CodeChallengeMethod() throws Exception {
        // test case : failure : A-1-5
        String codeVerifier = "1234567890123456789012345678901234567890123";
        String codeChallenge = codeVerifier;
        oauth.codeChallenge(codeChallenge);
        oauth.codeChallengeMethod(PKCE_METHOD_S256);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.codeVerifier(codeVerifier);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(INVALID_GRANT, response.getError());
        Assert.assertEquals("PKCE verification failed", response.getErrorDescription());
        events.expectCodeToToken(codeId, sessionId).error(PKCE_VERIFICATION_FAILED).clearDetails().assertEvent();
    }

    @Test
    public void accessTokenRequestInPKCEValidPlainCodeChallengeMethod() throws Exception {
        // test case : success : A-1-3
        oauth.codeChallenge(".234567890-234567890~234567890_234567890123");
        oauth.codeChallengeMethod(PKCE_METHOD_PLAIN);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.codeVerifier(".234567890-234567890~234567890_234567890123");
        expectSuccessfulResponseFromTokenEndpoint(codeId, sessionId, code);
    }

    @Test
    public void accessTokenRequestInPKCEUnmachedCodeVerifierWithPlainCodeChallengeMethod() throws Exception {
        // test case : failure : A-1-6
        oauth.codeChallenge("1234567890123456789012345678901234567890123");
        oauth.codeChallengeMethod(PKCE_METHOD_PLAIN);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.codeVerifier("aZ_-.~1234567890123456789012345678901234567890123Za");
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(INVALID_GRANT, response.getError());
        Assert.assertEquals("PKCE verification failed", response.getErrorDescription());
        events.expectCodeToToken(codeId, sessionId).error(PKCE_VERIFICATION_FAILED).clearDetails().assertEvent();
    }

    @Test
    public void accessTokenRequestInPKCEValidDefaultCodeChallengeMethod() throws Exception {
        // test case : success : A-1-4
        oauth.codeChallenge("1234567890123456789012345678901234567890123");
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.codeVerifier("1234567890123456789012345678901234567890123");
        expectSuccessfulResponseFromTokenEndpoint(codeId, sessionId, code);
    }

    @Test
    public void accessTokenRequestInPKCEWithoutCodeChallengeWithValidCodeChallengeMethod() throws Exception {
        // test case : failure : A-1-7
        oauth.codeChallengeMethod(PKCE_METHOD_PLAIN);
        UriBuilder b = UriBuilder.fromUri(oauth.getLoginFormUrl());
        driver.navigate().to(b.build().toURL());
        OAuthClient.AuthorizationEndpointResponse errorResponse = new OAuthClient.AuthorizationEndpointResponse(oauth);
        Assert.assertTrue(errorResponse.isRedirected());
        Assert.assertEquals(errorResponse.getError(), INVALID_REQUEST);
        Assert.assertEquals(errorResponse.getErrorDescription(), "Missing parameter: code_challenge");
        events.expectLogin().error(Errors.INVALID_REQUEST).user(((String) (null))).session(((String) (null))).clearDetails().assertEvent();
    }

    @Test
    public void accessTokenRequestInPKCEInvalidUnderCodeChallengeWithS256CodeChallengeMethod() throws Exception {
        // test case : failure : A-1-8
        oauth.codeChallengeMethod(PKCE_METHOD_S256);
        oauth.codeChallenge("ABCDEFGabcdefg1234567ABCDEFGabcdefg1234567");// 42

        UriBuilder b = UriBuilder.fromUri(oauth.getLoginFormUrl());
        driver.navigate().to(b.build().toURL());
        OAuthClient.AuthorizationEndpointResponse errorResponse = new OAuthClient.AuthorizationEndpointResponse(oauth);
        Assert.assertTrue(errorResponse.isRedirected());
        Assert.assertEquals(errorResponse.getError(), INVALID_REQUEST);
        Assert.assertEquals(errorResponse.getErrorDescription(), "Invalid parameter: code_challenge");
        events.expectLogin().error(Errors.INVALID_REQUEST).user(((String) (null))).session(((String) (null))).clearDetails().assertEvent();
    }

    @Test
    public void accessTokenRequestInPKCEInvalidOverCodeChallengeWithPlainCodeChallengeMethod() throws Exception {
        // test case : failure : A-1-9
        oauth.codeChallengeMethod(PKCE_METHOD_PLAIN);
        oauth.codeChallenge("3fRc92kac_keic8c7al-3ncbdoaie.DDeizlck3~3fRc92kac_keic8c7al-3ncbdoaie.DDeizlck3~3fRc92kac_keic8c7al-3ncbdoaie.DDeizlck3~123456789");// 129

        UriBuilder b = UriBuilder.fromUri(oauth.getLoginFormUrl());
        driver.navigate().to(b.build().toURL());
        OAuthClient.AuthorizationEndpointResponse errorResponse = new OAuthClient.AuthorizationEndpointResponse(oauth);
        Assert.assertTrue(errorResponse.isRedirected());
        Assert.assertEquals(errorResponse.getError(), INVALID_REQUEST);
        Assert.assertEquals(errorResponse.getErrorDescription(), "Invalid parameter: code_challenge");
        events.expectLogin().error(Errors.INVALID_REQUEST).user(((String) (null))).session(((String) (null))).clearDetails().assertEvent();
    }

    @Test
    public void accessTokenRequestInPKCEInvalidUnderCodeVerifierWithS256CodeChallengeMethod() throws Exception {
        // test case : success : A-1-10
        String codeVerifier = "ABCDEFGabcdefg1234567ABCDEFGabcdefg1234567";// 42

        String codeChallenge = generateS256CodeChallenge(codeVerifier);
        oauth.codeChallenge(codeChallenge);
        oauth.codeChallengeMethod(PKCE_METHOD_S256);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.codeVerifier(codeVerifier);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(INVALID_GRANT, response.getError());
        Assert.assertEquals("PKCE invalid code verifier", response.getErrorDescription());
        events.expectCodeToToken(codeId, sessionId).error(INVALID_CODE_VERIFIER).clearDetails().assertEvent();
    }

    @Test
    public void accessTokenRequestInPKCEInvalidOverCodeVerifierWithS256CodeChallengeMethod() throws Exception {
        // test case : success : A-1-11
        String codeVerifier = "3fRc92kac_keic8c7al-3ncbdoaie.DDeizlck3~3fRc92kac_keic8c7al-3ncbdoaie.DDeizlck3~3fRc92kac_keic8c7al-3ncbdoaie.DDeizlck3~123456789";// 129

        String codeChallenge = generateS256CodeChallenge(codeVerifier);
        oauth.codeChallenge(codeChallenge);
        oauth.codeChallengeMethod(PKCE_METHOD_S256);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.codeVerifier(codeVerifier);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(INVALID_GRANT, response.getError());
        Assert.assertEquals("PKCE invalid code verifier", response.getErrorDescription());
        events.expectCodeToToken(codeId, sessionId).error(INVALID_CODE_VERIFIER).clearDetails().assertEvent();
    }

    @Test
    public void accessTokenRequestInPKCEWIthoutCodeVerifierWithS256CodeChallengeMethod() throws Exception {
        // test case : failure : A-1-12
        String codeVerifier = "1234567890123456789012345678901234567890123";
        String codeChallenge = codeVerifier;
        oauth.codeChallenge(codeChallenge);
        oauth.codeChallengeMethod(PKCE_METHOD_S256);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(INVALID_GRANT, response.getError());
        Assert.assertEquals("PKCE code verifier not specified", response.getErrorDescription());
        events.expectCodeToToken(codeId, sessionId).error(CODE_VERIFIER_MISSING).clearDetails().assertEvent();
    }

    @Test
    public void accessTokenRequestInPKCEInvalidCodeChallengeWithS256CodeChallengeMethod() throws Exception {
        // test case : failure : A-1-13
        String codeVerifier = "1234567890123456789=12345678901234567890123";
        String codeChallenge = codeVerifier;
        oauth.codeChallenge(codeChallenge);
        oauth.codeChallengeMethod(PKCE_METHOD_S256);
        UriBuilder b = UriBuilder.fromUri(oauth.getLoginFormUrl());
        driver.navigate().to(b.build().toURL());
        OAuthClient.AuthorizationEndpointResponse errorResponse = new OAuthClient.AuthorizationEndpointResponse(oauth);
        Assert.assertTrue(errorResponse.isRedirected());
        Assert.assertEquals(errorResponse.getError(), INVALID_REQUEST);
        Assert.assertEquals(errorResponse.getErrorDescription(), "Invalid parameter: code_challenge");
        events.expectLogin().error(Errors.INVALID_REQUEST).user(((String) (null))).session(((String) (null))).clearDetails().assertEvent();
    }

    @Test
    public void accessTokenRequestInPKCEInvalidCodeVerifierWithS256CodeChallengeMethod() throws Exception {
        // test case : failure : A-1-14
        String codeVerifier = "123456789.123456789-123456789~1234$6789_123";
        String codeChallenge = generateS256CodeChallenge(codeVerifier);
        oauth.codeChallenge(codeChallenge);
        oauth.codeChallengeMethod(PKCE_METHOD_S256);
        oauth.doLogin("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String sessionId = loginEvent.getSessionId();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String code = oauth.getCurrentQuery().get(CODE);
        oauth.codeVerifier(codeVerifier);
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        Assert.assertEquals(400, response.getStatusCode());
        Assert.assertEquals(INVALID_GRANT, response.getError());
        Assert.assertEquals("PKCE invalid code verifier", response.getErrorDescription());
        events.expectCodeToToken(codeId, sessionId).error(INVALID_CODE_VERIFIER).clearDetails().assertEvent();
    }
}

