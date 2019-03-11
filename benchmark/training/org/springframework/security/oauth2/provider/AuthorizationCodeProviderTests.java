/**
 * Copyright 2006-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.security.oauth2.provider;


import HttpStatus.BAD_REQUEST;
import HttpStatus.FOUND;
import HttpStatus.OK;
import HttpStatus.UNAUTHORIZED;
import MediaType.TEXT_HTML;
import OAuth2AccessToken.BEARER_TYPE;
import OAuth2Utils.USER_OAUTH_APPROVAL;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.test.OAuth2ContextConfiguration;
import org.springframework.security.oauth2.client.test.OAuth2ContextSetup;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.exceptions.InsufficientScopeException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;


/**
 *
 *
 * @author Dave Syer
 * @author Luke Taylor
 */
public class AuthorizationCodeProviderTests {
    @Rule
    public ServerRunning serverRunning = ServerRunning.isRunning();

    @Rule
    public OAuth2ContextSetup context = OAuth2ContextSetup.standard(serverRunning);

    private AuthorizationCodeAccessTokenProvider accessTokenProvider;

    private ClientHttpResponse tokenEndpointResponse;

    @Test
    public void testResourceIsProtected() throws Exception {
        // first make sure the resource is actually protected.
        Assert.assertEquals(UNAUTHORIZED, serverRunning.getStatusCode("/sparklr2/photos?format=json"));
    }

    @Test
    @OAuth2ContextConfiguration(resource = AuthorizationCodeProviderTests.MyLessTrustedClient.class, initialize = false)
    public void testUnauthenticatedAuthorizationRequestRedirectsToLogin() throws Exception {
        AccessTokenRequest request = context.getAccessTokenRequest();
        request.setCurrentUri("http://anywhere");
        request.add(USER_OAUTH_APPROVAL, "true");
        String location = null;
        try {
            String code = accessTokenProvider.obtainAuthorizationCode(context.getResource(), request);
            Assert.assertNotNull(code);
            Assert.fail("Expected UserRedirectRequiredException");
        } catch (UserRedirectRequiredException e) {
            location = e.getRedirectUri();
        }
        Assert.assertNotNull(location);
        Assert.assertEquals(serverRunning.getUrl("/sparklr2/login.jsp"), location);
    }

    @Test
    @OAuth2ContextConfiguration(resource = AuthorizationCodeProviderTests.MyLessTrustedClient.class, initialize = false)
    public void testSuccessfulAuthorizationCodeFlow() throws Exception {
        // Once the request is ready and approved, we can continue with the access token
        approveAccessTokenGrant("http://anywhere", true);
        // Finally everything is in place for the grant to happen...
        Assert.assertNotNull(context.getAccessToken());
        AccessTokenRequest request = context.getAccessTokenRequest();
        Assert.assertNotNull(request.getAuthorizationCode());
        Assert.assertEquals(OK, serverRunning.getStatusCode("/sparklr2/photos?format=json"));
    }

    @Test
    @OAuth2ContextConfiguration(resource = AuthorizationCodeProviderTests.MyLessTrustedClient.class, initialize = false)
    public void testWrongRedirectUri() throws Exception {
        approveAccessTokenGrant("http://anywhere", true);
        AccessTokenRequest request = context.getAccessTokenRequest();
        // The redirect is stored in the preserved state...
        context.getOAuth2ClientContext().setPreservedState(request.getStateKey(), "http://nowhere");
        // Finally everything is in place for the grant to happen...
        try {
            Assert.assertNotNull(context.getAccessToken());
            Assert.fail("Expected RedirectMismatchException");
        } catch (RedirectMismatchException e) {
            // expected
        }
        Assert.assertEquals(BAD_REQUEST, tokenEndpointResponse.getStatusCode());
    }

    @Test
    @OAuth2ContextConfiguration(resource = AuthorizationCodeProviderTests.MyLessTrustedClient.class, initialize = false)
    public void testUserDeniesConfirmation() throws Exception {
        approveAccessTokenGrant("http://anywhere", false);
        String location = null;
        try {
            Assert.assertNotNull(context.getAccessToken());
            Assert.fail("Expected UserRedirectRequiredException");
        } catch (UserRedirectRequiredException e) {
            location = e.getRedirectUri();
        }
        Assert.assertTrue(("Wrong location: " + location), location.contains("state="));
        Assert.assertTrue(location.startsWith("http://anywhere"));
        Assert.assertTrue(location.substring(location.indexOf('?')).contains("error=access_denied"));
        // It was a redirect that triggered our client redirect exception:
        Assert.assertEquals(FOUND, tokenEndpointResponse.getStatusCode());
    }

    @Test
    public void testNoClientIdProvided() throws Exception {
        ResponseEntity<String> response = attemptToGetConfirmationPage(null, "http://anywhere");
        // With no client id you get an InvalidClientException on the server which is forwarded to /oauth/error
        Assert.assertEquals(UNAUTHORIZED, response.getStatusCode());
        String body = response.getBody();
        Assert.assertTrue(("Wrong body: " + body), body.contains("<html"));
        Assert.assertTrue(("Wrong body: " + body), body.contains("OAuth2 Error"));
    }

    @Test
    public void testNoRedirect() throws Exception {
        ResponseEntity<String> response = attemptToGetConfirmationPage("my-less-trusted-client", null);
        // With no redirect uri you get an UnapprovedClientAuthenticationException on the server which is redirected to
        // /oauth/error.
        Assert.assertEquals(BAD_REQUEST, response.getStatusCode());
        String body = response.getBody();
        Assert.assertTrue(("Wrong body: " + body), body.contains("<html"));
        Assert.assertTrue(("Wrong body: " + body), body.contains("OAuth2 Error"));
    }

    @Test
    public void testIllegalAttemptToApproveWithoutUsingAuthorizationRequest() throws Exception {
        String cookie = loginAndGrabCookie();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        headers.set("Cookie", cookie);
        String authorizeUrl = getAuthorizeUrl("my-less-trusted-client", "http://anywhere.com", "read");
        authorizeUrl = authorizeUrl + "&user_oauth_approval=true";
        ResponseEntity<Void> response = serverRunning.postForStatus(authorizeUrl, headers, new org.springframework.util.LinkedMultiValueMap<String, String>());
        Assert.assertEquals(BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @OAuth2ContextConfiguration(resource = AuthorizationCodeProviderTests.MyClientWithRegisteredRedirect.class, initialize = false)
    public void testSuccessfulFlowWithRegisteredRedirect() throws Exception {
        // Once the request is ready and approved, we can continue with the access token
        approveAccessTokenGrant(null, true);
        // Finally everything is in place for the grant to happen...
        Assert.assertNotNull(context.getAccessToken());
        AccessTokenRequest request = context.getAccessTokenRequest();
        Assert.assertNotNull(request.getAuthorizationCode());
        Assert.assertEquals(OK, serverRunning.getStatusCode("/sparklr2/photos?format=json"));
    }

    @Test
    public void testInvalidScopeInAuthorizationRequest() throws Exception {
        // Need to use the client with a redirect because "my-less-trusted-client" has no registered scopes
        String cookie = loginAndGrabCookie();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(TEXT_HTML));
        headers.set("Cookie", cookie);
        String scope = "bogus";
        String redirectUri = "http://anywhere?key=value";
        String clientId = "my-client-with-registered-redirect";
        ServerRunning.UriBuilder uri = serverRunning.buildUri("/sparklr2/oauth/authorize").queryParam("response_type", "code").queryParam("state", "mystateid").queryParam("scope", scope);
        if (clientId != null) {
            uri.queryParam("client_id", clientId);
        }
        if (redirectUri != null) {
            uri.queryParam("redirect_uri", redirectUri);
        }
        ResponseEntity<String> response = serverRunning.getForString(uri.pattern(), headers, uri.params());
        Assert.assertEquals(FOUND, response.getStatusCode());
        String location = response.getHeaders().getLocation().toString();
        Assert.assertTrue(location.startsWith("http://anywhere"));
        Assert.assertTrue(location.contains("error=invalid_scope"));
        Assert.assertFalse(location.contains("redirect_uri="));
    }

    @Test
    @OAuth2ContextConfiguration(resource = AuthorizationCodeProviderTests.MyClientWithRegisteredRedirect.class, initialize = false)
    public void testInsufficientScopeInResourceRequest() throws Exception {
        AuthorizationCodeResourceDetails resource = ((AuthorizationCodeResourceDetails) (context.getResource()));
        resource.setScope(Arrays.asList("trust"));
        approveAccessTokenGrant("http://anywhere?key=value", true);
        Assert.assertNotNull(context.getAccessToken());
        try {
            serverRunning.getForString("/sparklr2/photos?format=json");
            Assert.fail("Should have thrown exception");
        } catch (InsufficientScopeException ex) {
            // ignore / all good
        }
    }

    @Test
    public void testInvalidAccessToken() throws Exception {
        // now make sure an unauthorized request fails the right way.
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", String.format("%s %s", BEARER_TYPE, "FOO"));
        ResponseEntity<String> response = serverRunning.getForString("/sparklr2/photos?format=json", headers);
        Assert.assertEquals(UNAUTHORIZED, response.getStatusCode());
        String authenticate = response.getHeaders().getFirst("WWW-Authenticate");
        Assert.assertNotNull(authenticate);
        Assert.assertTrue(authenticate.startsWith("Bearer"));
        // Resource Server doesn't know what scopes are required until the token can be validated
        Assert.assertFalse(authenticate.contains("scope=\""));
    }

    @Test
    @OAuth2ContextConfiguration(resource = AuthorizationCodeProviderTests.MyClientWithRegisteredRedirect.class, initialize = false)
    public void testRegisteredRedirectWithWrongRequestedRedirect() throws Exception {
        try {
            approveAccessTokenGrant("http://nowhere", true);
            Assert.fail("Expected RedirectMismatchException");
        } catch (RedirectMismatchException e) {
        }
        Assert.assertEquals(BAD_REQUEST, tokenEndpointResponse.getStatusCode());
    }

    @Test
    @OAuth2ContextConfiguration(resource = AuthorizationCodeProviderTests.MyClientWithRegisteredRedirect.class, initialize = false)
    public void testRegisteredRedirectWithWrongOneInTokenEndpoint() throws Exception {
        approveAccessTokenGrant("http://anywhere?key=value", true);
        // Setting the redirect uri directly in the request shoiuld override the saved value
        context.getAccessTokenRequest().set("redirect_uri", "http://nowhere.com");
        try {
            Assert.assertNotNull(context.getAccessToken());
            Assert.fail("Expected RedirectMismatchException");
        } catch (RedirectMismatchException e) {
        }
        Assert.assertEquals(BAD_REQUEST, tokenEndpointResponse.getStatusCode());
    }

    static class MyLessTrustedClient extends AuthorizationCodeResourceDetails {
        public MyLessTrustedClient(Object target) {
            super();
            setClientId("my-less-trusted-client");
            setScope(Arrays.asList("read"));
            setId(getClientId());
            AuthorizationCodeProviderTests test = ((AuthorizationCodeProviderTests) (target));
            setAccessTokenUri(test.serverRunning.getUrl("/sparklr2/oauth/token"));
            setUserAuthorizationUri(test.serverRunning.getUrl("/sparklr2/oauth/authorize"));
        }
    }

    static class MyClientWithRegisteredRedirect extends AuthorizationCodeProviderTests.MyLessTrustedClient {
        public MyClientWithRegisteredRedirect(Object target) {
            super(target);
            setClientId("my-client-with-registered-redirect");
            setPreEstablishedRedirectUri("http://anywhere?key=value");
        }
    }
}

