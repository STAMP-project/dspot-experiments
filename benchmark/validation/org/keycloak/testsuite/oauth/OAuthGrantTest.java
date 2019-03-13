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


import AccessToken.Access;
import AccountApplicationsPage.AppEntry;
import ClientScopeModel.GUI_ORDER;
import Details.AUTH_METHOD;
import Details.CODE_ID;
import Details.CONSENT;
import Details.CONSENT_VALUE_CONSENT_GRANTED;
import Details.CONSENT_VALUE_PERSISTED_CONSENT;
import Details.REVOKED_CLIENT;
import Details.USERNAME;
import EventType.REVOKE_GRANT;
import OAuth2Constants.CODE;
import OAuth2Constants.ERROR;
import OAuth2Constants.GRANT_TYPE;
import OAuthClient.AccessTokenResponse;
import OAuthGrantPage.EMAIL_CONSENT_TEXT;
import OAuthGrantPage.PROFILE_CONSENT_TEXT;
import OAuthGrantPage.ROLES_CONSENT_TEXT;
import OIDCLoginProtocol.LOGIN_PROTOCOL;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientScopeResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.EventRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.pages.AccountApplicationsPage;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.OAuthGrantPage;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.ProtocolMapperUtil;
import org.openqa.selenium.By;


/**
 *
 *
 * @author <a href="mailto:vrockai@redhat.com">Viliam Rockai</a>
 */
public class OAuthGrantTest extends AbstractKeycloakTest {
    public static final String THIRD_PARTY_APP = "third-party";

    public static final String REALM_NAME = "test";

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected OAuthGrantPage grantPage;

    @Page
    protected AccountApplicationsPage accountAppsPage;

    @Page
    protected AppPage appPage;

    @Page
    protected ErrorPage errorPage;

    private static String ROLE_USER = "Have User privileges";

    private static String ROLE_CUSTOMER = "Have Customer User privileges";

    @Test
    public void oauthGrantAcceptTest() {
        oauth.clientId(OAuthGrantTest.THIRD_PARTY_APP);
        oauth.doLoginGrant("test-user@localhost", "password");
        grantPage.assertCurrent();
        grantPage.assertGrants(PROFILE_CONSENT_TEXT, EMAIL_CONSENT_TEXT, ROLES_CONSENT_TEXT);
        grantPage.accept();
        Assert.assertTrue(oauth.getCurrentQuery().containsKey(CODE));
        EventRepresentation loginEvent = events.expectLogin().client(OAuthGrantTest.THIRD_PARTY_APP).detail(CONSENT, CONSENT_VALUE_CONSENT_GRANTED).assertEvent();
        String codeId = loginEvent.getDetails().get(CODE_ID);
        String sessionId = loginEvent.getSessionId();
        OAuthClient.AccessTokenResponse accessToken = oauth.doAccessTokenRequest(oauth.getCurrentQuery().get(CODE), "password");
        String tokenString = accessToken.getAccessToken();
        Assert.assertNotNull(tokenString);
        AccessToken token = oauth.verifyToken(tokenString);
        Assert.assertEquals(sessionId, token.getSessionState());
        AccessToken.Access realmAccess = token.getRealmAccess();
        Assert.assertEquals(1, realmAccess.getRoles().size());
        Assert.assertTrue(realmAccess.isUserInRole("user"));
        Map<String, AccessToken.Access> resourceAccess = token.getResourceAccess();
        Assert.assertEquals(1, resourceAccess.size());
        Assert.assertEquals(1, resourceAccess.get("test-app").getRoles().size());
        Assert.assertTrue(resourceAccess.get("test-app").isUserInRole("customer-user"));
        events.expectCodeToToken(codeId, loginEvent.getSessionId()).client(OAuthGrantTest.THIRD_PARTY_APP).assertEvent();
        accountAppsPage.open();
        Assert.assertEquals(1, driver.findElements(By.id("revoke-third-party")).size());
        accountAppsPage.revokeGrant(OAuthGrantTest.THIRD_PARTY_APP);
        events.expect(REVOKE_GRANT).client("account").detail(REVOKED_CLIENT, OAuthGrantTest.THIRD_PARTY_APP).assertEvent();
        Assert.assertEquals(0, driver.findElements(By.id("revoke-third-party")).size());
    }

    @Test
    public void oauthGrantCancelTest() {
        oauth.clientId(OAuthGrantTest.THIRD_PARTY_APP);
        oauth.doLoginGrant("test-user@localhost", "password");
        grantPage.assertCurrent();
        grantPage.assertGrants(PROFILE_CONSENT_TEXT, EMAIL_CONSENT_TEXT, ROLES_CONSENT_TEXT);
        grantPage.cancel();
        Assert.assertTrue(oauth.getCurrentQuery().containsKey(ERROR));
        Assert.assertEquals("access_denied", oauth.getCurrentQuery().get(ERROR));
        events.expectLogin().client(OAuthGrantTest.THIRD_PARTY_APP).error("rejected_by_user").removeDetail(CONSENT).session(Matchers.nullValue(String.class)).assertEvent();
    }

    @Test
    public void oauthGrantNotShownWhenAlreadyGranted() {
        // Grant permissions on grant screen
        oauth.clientId(OAuthGrantTest.THIRD_PARTY_APP);
        oauth.doLoginGrant("test-user@localhost", "password");
        grantPage.assertCurrent();
        grantPage.accept();
        events.expectLogin().client(OAuthGrantTest.THIRD_PARTY_APP).detail(CONSENT, CONSENT_VALUE_CONSENT_GRANTED).assertEvent();
        // Assert permissions granted on Account mgmt. applications page
        accountAppsPage.open();
        AccountApplicationsPage.AppEntry thirdPartyEntry = accountAppsPage.getApplications().get(OAuthGrantTest.THIRD_PARTY_APP);
        thirdPartyEntry.getClientScopesGranted().contains(PROFILE_CONSENT_TEXT);
        thirdPartyEntry.getClientScopesGranted().contains(EMAIL_CONSENT_TEXT);
        // Open login form and assert grantPage not shown
        oauth.openLoginForm();
        appPage.assertCurrent();
        events.expectLogin().detail(AUTH_METHOD, LOGIN_PROTOCOL).detail(CONSENT, CONSENT_VALUE_PERSISTED_CONSENT).removeDetail(USERNAME).client(OAuthGrantTest.THIRD_PARTY_APP).assertEvent();
        // Revoke grant in account mgmt.
        accountAppsPage.open();
        accountAppsPage.revokeGrant(OAuthGrantTest.THIRD_PARTY_APP);
        events.expect(REVOKE_GRANT).client("account").detail(REVOKED_CLIENT, OAuthGrantTest.THIRD_PARTY_APP).assertEvent();
        // Open login form again and assert grant Page is shown
        oauth.openLoginForm();
        grantPage.assertCurrent();
        grantPage.assertGrants(PROFILE_CONSENT_TEXT, EMAIL_CONSENT_TEXT, ROLES_CONSENT_TEXT);
    }

    @Test
    public void oauthGrantAddAnotherScope() {
        // Grant permissions on grant screen
        oauth.clientId(OAuthGrantTest.THIRD_PARTY_APP);
        oauth.doLoginGrant("test-user@localhost", "password");
        oauth.scope(GRANT_TYPE);
        // Create new clientScope and add to client
        RealmResource appRealm = adminClient.realm(OAuthGrantTest.REALM_NAME);
        ClientScopeRepresentation scope1 = new ClientScopeRepresentation();
        scope1.setName("foo-scope");
        scope1.setProtocol(LOGIN_PROTOCOL);
        Response response = appRealm.clientScopes().create(scope1);
        String fooScopeId = ApiUtil.getCreatedId(response);
        response.close();
        getCleanup().addClientScopeId(fooScopeId);
        // Add clientScope to client
        ClientResource thirdParty = ApiUtil.findClientByClientId(appRealm, OAuthGrantTest.THIRD_PARTY_APP);
        thirdParty.addDefaultClientScope(fooScopeId);
        // Confirm grant page
        grantPage.assertCurrent();
        grantPage.accept();
        events.expectLogin().client(OAuthGrantTest.THIRD_PARTY_APP).detail(CONSENT, CONSENT_VALUE_CONSENT_GRANTED).assertEvent();
        // Assert new clientScope not yet in account mgmt
        accountAppsPage.open();
        AccountApplicationsPage.AppEntry appEntry = accountAppsPage.getApplications().get(OAuthGrantTest.THIRD_PARTY_APP);
        Assert.assertFalse(appEntry.getClientScopesGranted().contains("foo-scope"));
        // Show grant page another time. Just new clientScope is on the page
        oauth.openLoginForm();
        grantPage.assertCurrent();
        grantPage.assertGrants("foo-scope");
        grantPage.accept();
        events.expectLogin().client(OAuthGrantTest.THIRD_PARTY_APP).detail(CONSENT, CONSENT_VALUE_CONSENT_GRANTED).assertEvent();
        // Go to account mgmt. Everything is granted now
        accountAppsPage.open();
        appEntry = accountAppsPage.getApplications().get(OAuthGrantTest.THIRD_PARTY_APP);
        Assert.assertTrue(appEntry.getClientScopesGranted().contains("foo-scope"));
        // Revoke
        accountAppsPage.revokeGrant(OAuthGrantTest.THIRD_PARTY_APP);
        events.expect(REVOKE_GRANT).client("account").detail(REVOKED_CLIENT, OAuthGrantTest.THIRD_PARTY_APP).assertEvent();
        // Cleanup
        thirdParty.removeDefaultClientScope(fooScopeId);
    }

    @Test
    public void oauthGrantScopeParamRequired() throws Exception {
        RealmResource appRealm = adminClient.realm(OAuthGrantTest.REALM_NAME);
        ClientResource thirdParty = ApiUtil.findClientByClientId(appRealm, OAuthGrantTest.THIRD_PARTY_APP);
        // Create clientScope
        ClientScopeRepresentation scope1 = new ClientScopeRepresentation();
        scope1.setName("foo-scope");
        scope1.setProtocol(LOGIN_PROTOCOL);
        Response response = appRealm.clientScopes().create(scope1);
        String fooScopeId = ApiUtil.getCreatedId(response);
        response.close();
        getCleanup().addClientScopeId(fooScopeId);
        // Add clientScope as optional to client
        thirdParty.addOptionalClientScope(fooScopeId);
        // Assert clientScope not on grant screen when not requested
        oauth.clientId(OAuthGrantTest.THIRD_PARTY_APP);
        oauth.doLoginGrant("test-user@localhost", "password");
        grantPage.assertCurrent();
        List<String> grants = grantPage.getDisplayedGrants();
        Assert.assertFalse(grants.contains("foo-scope"));
        grantPage.cancel();
        events.expectLogin().client(OAuthGrantTest.THIRD_PARTY_APP).error("rejected_by_user").removeDetail(CONSENT).session(Matchers.nullValue(String.class)).assertEvent();
        oauth.scope("foo-scope");
        oauth.doLoginGrant("test-user@localhost", "password");
        grantPage.assertCurrent();
        grants = grantPage.getDisplayedGrants();
        Assert.assertTrue(grants.contains("foo-scope"));
        grantPage.accept();
        events.expectLogin().client(OAuthGrantTest.THIRD_PARTY_APP).detail(CONSENT, CONSENT_VALUE_CONSENT_GRANTED).assertEvent();
        // Revoke
        accountAppsPage.open();
        accountAppsPage.revokeGrant(OAuthGrantTest.THIRD_PARTY_APP);
        events.expect(REVOKE_GRANT).client("account").detail(REVOKED_CLIENT, OAuthGrantTest.THIRD_PARTY_APP).assertEvent();
        // cleanup
        oauth.scope(null);
        thirdParty.removeOptionalClientScope(fooScopeId);
    }

    // KEYCLOAK-4326
    @Test
    public void oauthGrantClientScopeMappers() throws Exception {
        // Add client scope with some protocol mapper
        RealmResource appRealm = adminClient.realm(OAuthGrantTest.REALM_NAME);
        ClientScopeRepresentation scope1 = new ClientScopeRepresentation();
        scope1.setName("foo-addr");
        scope1.setProtocol(LOGIN_PROTOCOL);
        Response response = appRealm.clientScopes().create(scope1);
        String fooScopeId = ApiUtil.getCreatedId(response);
        response.close();
        ProtocolMapperRepresentation protocolMapper = ProtocolMapperUtil.createAddressMapper(true, true, true);
        response = appRealm.clientScopes().get(fooScopeId).getProtocolMappers().createMapper(protocolMapper);
        response.close();
        // Add clientScope to client
        ClientResource thirdParty = ApiUtil.findClientByClientId(appRealm, OAuthGrantTest.THIRD_PARTY_APP);
        thirdParty.addDefaultClientScope(fooScopeId);
        getCleanup().addClientScopeId(fooScopeId);
        // Login
        oauth.clientId(OAuthGrantTest.THIRD_PARTY_APP);
        oauth.doLoginGrant("test-user@localhost", "password");
        grantPage.assertCurrent();
        grantPage.assertGrants(EMAIL_CONSENT_TEXT, PROFILE_CONSENT_TEXT, ROLES_CONSENT_TEXT, "foo-addr");
        grantPage.accept();
        events.expectLogin().client(OAuthGrantTest.THIRD_PARTY_APP).detail(CONSENT, CONSENT_VALUE_CONSENT_GRANTED).assertEvent();
        // Go to user's application screen
        accountAppsPage.open();
        Assert.assertTrue(accountAppsPage.isCurrent());
        Map<String, AccountApplicationsPage.AppEntry> apps = accountAppsPage.getApplications();
        Assert.assertTrue(apps.containsKey("third-party"));
        Assert.assertTrue(apps.get("third-party").getClientScopesGranted().contains("foo-addr"));
        // Login as admin and see the consent screen of particular user
        UserResource user = ApiUtil.findUserByUsernameId(appRealm, "test-user@localhost");
        List<Map<String, Object>> consents = user.getConsents();
        Assert.assertEquals(1, consents.size());
        // Assert automatically logged another time
        oauth.openLoginForm();
        appPage.assertCurrent();
        events.expectLogin().detail(AUTH_METHOD, LOGIN_PROTOCOL).detail(CONSENT, CONSENT_VALUE_PERSISTED_CONSENT).removeDetail(USERNAME).client(OAuthGrantTest.THIRD_PARTY_APP).assertEvent();
        // Revoke
        accountAppsPage.open();
        accountAppsPage.revokeGrant(OAuthGrantTest.THIRD_PARTY_APP);
        events.expect(REVOKE_GRANT).client("account").detail(REVOKED_CLIENT, OAuthGrantTest.THIRD_PARTY_APP).assertEvent();
        // Cleanup
        thirdParty.removeDefaultClientScope(fooScopeId);
    }

    @Test
    public void oauthGrantExpiredAuthSession() throws Exception {
        oauth.clientId(OAuthGrantTest.THIRD_PARTY_APP);
        oauth.doLoginGrant("test-user@localhost", "password");
        grantPage.assertCurrent();
        // Expire cookies
        driver.manage().deleteAllCookies();
        grantPage.accept();
        // Assert link "back to application" present
        errorPage.assertCurrent();
        String backToAppLink = errorPage.getBackToApplicationLink();
        ClientRepresentation thirdParty = ApiUtil.findClientByClientId(adminClient.realm(OAuthGrantTest.REALM_NAME), OAuthGrantTest.THIRD_PARTY_APP).toRepresentation();
        Assert.assertEquals(backToAppLink, thirdParty.getBaseUrl());
    }

    // KEYCLOAK-7470
    @Test
    public void oauthGrantOrderedClientScopes() throws Exception {
        // Add GUI Order to client scopes --- email=1, profile=2
        RealmResource appRealm = adminClient.realm(OAuthGrantTest.REALM_NAME);
        ClientScopeResource emailScope = ApiUtil.findClientScopeByName(appRealm, "email");
        ClientScopeRepresentation emailRep = emailScope.toRepresentation();
        emailRep.getAttributes().put(GUI_ORDER, "1");
        emailScope.update(emailRep);
        ClientScopeResource profileScope = ApiUtil.findClientScopeByName(appRealm, "profile");
        ClientScopeRepresentation profileRep = profileScope.toRepresentation();
        profileRep.getAttributes().put(GUI_ORDER, "2");
        profileScope.update(profileRep);
        // Display consent screen --- assert email, then profile
        oauth.clientId(OAuthGrantTest.THIRD_PARTY_APP);
        oauth.doLoginGrant("test-user@localhost", "password");
        grantPage.assertCurrent();
        List<String> displayedScopes = grantPage.getDisplayedGrants();
        Assert.assertEquals("Email address", displayedScopes.get(0));
        Assert.assertEquals("User profile", displayedScopes.get(1));
        grantPage.accept();
        // Display account mgmt --- assert email, then profile
        accountAppsPage.open();
        displayedScopes = accountAppsPage.getApplications().get(OAuthGrantTest.THIRD_PARTY_APP).getClientScopesGranted();
        Assert.assertEquals("Email address", displayedScopes.get(0));
        Assert.assertEquals("User profile", displayedScopes.get(1));
        // Update GUI Order --- email=3
        emailRep = emailScope.toRepresentation();
        emailRep.getAttributes().put(GUI_ORDER, "3");
        emailScope.update(emailRep);
        // Display account mgmt --- assert profile, then email
        accountAppsPage.open();
        displayedScopes = accountAppsPage.getApplications().get(OAuthGrantTest.THIRD_PARTY_APP).getClientScopesGranted();
        Assert.assertEquals("User profile", displayedScopes.get(0));
        Assert.assertEquals("Email address", displayedScopes.get(1));
        // Revoke grant and display consent screen --- assert profile, then email
        accountAppsPage.revokeGrant(OAuthGrantTest.THIRD_PARTY_APP);
        oauth.openLoginForm();
        grantPage.assertCurrent();
        displayedScopes = grantPage.getDisplayedGrants();
        Assert.assertEquals("User profile", displayedScopes.get(0));
        Assert.assertEquals("Email address", displayedScopes.get(1));
    }
}

