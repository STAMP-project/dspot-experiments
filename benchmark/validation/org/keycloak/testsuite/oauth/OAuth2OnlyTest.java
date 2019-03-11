/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.oauth;


import Details.CODE_ID;
import Details.CONSENT;
import Errors.INVALID_REDIRECT_URI;
import OAuth2Constants.REDIRECT_URI;
import OAuth2Constants.SCOPE;
import OAuthClient.AccessTokenResponse;
import OAuthClient.AuthorizationEndpointResponse;
import org.hamcrest.Matchers;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.ActionURIUtils;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AccountUpdateProfilePage;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.OAuthGrantPage;
import org.keycloak.testsuite.util.OAuthClient;


/**
 * Test for scenarios when 'scope=openid' is missing. Which means we have pure OAuth2 request (not OpenID Connect)
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OAuth2OnlyTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected AccountUpdateProfilePage profilePage;

    @Page
    protected OAuthGrantPage grantPage;

    @Page
    protected ErrorPage errorPage;

    // If scope=openid is missing, IDToken won't be present
    @Test
    public void testMissingIDToken() {
        String loginFormUrl = oauth.getLoginFormUrl();
        loginFormUrl = ActionURIUtils.removeQueryParamFromURI(loginFormUrl, SCOPE);
        driver.navigate().to(loginFormUrl);
        oauth.fillLoginForm("test-user@localhost", "password");
        EventRepresentation loginEvent = events.expectLogin().assertEvent();
        String code = getCode();
        OAuthClient.AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        // IDToken is not there
        org.keycloak.testsuite.Assert.assertEquals(200, response.getStatusCode());
        org.keycloak.testsuite.Assert.assertNull(response.getIdToken());
        org.keycloak.testsuite.Assert.assertNotNull(response.getRefreshToken());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        org.keycloak.testsuite.Assert.assertEquals(token.getSubject(), loginEvent.getUserId());
        // Refresh and assert idToken still not present
        response = oauth.doRefreshTokenRequest(response.getRefreshToken(), "password");
        org.keycloak.testsuite.Assert.assertEquals(200, response.getStatusCode());
        org.keycloak.testsuite.Assert.assertNull(response.getIdToken());
        token = oauth.verifyToken(response.getAccessToken());
        org.keycloak.testsuite.Assert.assertEquals(token.getSubject(), loginEvent.getUserId());
    }

    // If scope=openid is missing, IDToken won't be present
    @Test
    public void testMissingScopeOpenidInResourceOwnerPasswordCredentialRequest() throws Exception {
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("password", "test-user@localhost", "password");
        Assert.assertEquals(200, response.getStatusCode());
        // idToken not present
        org.keycloak.testsuite.Assert.assertNull(response.getIdToken());
        org.keycloak.testsuite.Assert.assertNotNull(response.getRefreshToken());
        AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
        org.keycloak.testsuite.Assert.assertEquals(accessToken.getPreferredUsername(), "test-user@localhost");
    }

    // In OAuth2, it is allowed that redirect_uri is not mandatory as long as client has just 1 redirect_uri configured without wildcard
    @Test
    public void testMissingRedirectUri() throws Exception {
        // OAuth2 login without redirect_uri. It will be allowed.
        String loginFormUrl = oauth.getLoginFormUrl();
        loginFormUrl = ActionURIUtils.removeQueryParamFromURI(loginFormUrl, SCOPE);
        loginFormUrl = ActionURIUtils.removeQueryParamFromURI(loginFormUrl, REDIRECT_URI);
        driver.navigate().to(loginFormUrl);
        loginPage.assertCurrent();
        oauth.fillLoginForm("test-user@localhost", "password");
        events.expectLogin().assertEvent();
        // Client 'more-uris-client' has 2 redirect uris. OAuth2 login without redirect_uri won't be allowed
        oauth.clientId("more-uris-client");
        loginFormUrl = oauth.getLoginFormUrl();
        loginFormUrl = ActionURIUtils.removeQueryParamFromURI(loginFormUrl, SCOPE);
        loginFormUrl = ActionURIUtils.removeQueryParamFromURI(loginFormUrl, REDIRECT_URI);
        driver.navigate().to(loginFormUrl);
        errorPage.assertCurrent();
        org.keycloak.testsuite.Assert.assertEquals("Invalid parameter: redirect_uri", errorPage.getError());
        events.expectLogin().error(INVALID_REDIRECT_URI).client("more-uris-client").user(Matchers.nullValue(String.class)).session(Matchers.nullValue(String.class)).removeDetail(Details.REDIRECT_URI).removeDetail(CODE_ID).removeDetail(CONSENT).assertEvent();
    }

    // In OAuth2 (when response_type=token and no scope=openid) we don't treat nonce parameter mandatory
    @Test
    public void testMissingNonceInOAuth2ImplicitFlow() throws Exception {
        oauth.responseType("token");
        oauth.nonce(null);
        String loginFormUrl = oauth.getLoginFormUrl();
        loginFormUrl = ActionURIUtils.removeQueryParamFromURI(loginFormUrl, SCOPE);
        driver.navigate().to(loginFormUrl);
        loginPage.assertCurrent();
        oauth.fillLoginForm("test-user@localhost", "password");
        events.expectLogin().assertEvent();
        OAuthClient.AuthorizationEndpointResponse response = new OAuthClient.AuthorizationEndpointResponse(oauth);
        org.keycloak.testsuite.Assert.assertNull(response.getError());
        org.keycloak.testsuite.Assert.assertNull(response.getCode());
        org.keycloak.testsuite.Assert.assertNull(response.getIdToken());
        org.keycloak.testsuite.Assert.assertNotNull(response.getAccessToken());
    }
}

