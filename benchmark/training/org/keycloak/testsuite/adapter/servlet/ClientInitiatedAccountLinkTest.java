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
package org.keycloak.testsuite.adapter.servlet;


import Constants.CLIENT_ID;
import Constants.TAB_ID;
import LoginActionsService.SESSION_CODE;
import OIDCLoginProtocol.PROMPT_PARAM;
import OIDCLoginProtocol.PROMPT_VALUE_LOGIN;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.graphene.page.Page;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.util.Base64Url;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.FederatedIdentityRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.ActionURIUtils;
import org.keycloak.testsuite.adapter.AbstractServletsAdapterTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.arquillian.AuthServerTestEnricher;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.page.AbstractPageWithInjectedUrl;
import org.keycloak.testsuite.pages.AccountUpdateProfilePage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginUpdateProfilePage;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.WaitUtils;


/**
 *
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@AppServerContainer(ContainerConstants.APP_SERVER_UNDERTOW)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY_DEPRECATED)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
public class ClientInitiatedAccountLinkTest extends AbstractServletsAdapterTest {
    public static final String CHILD_IDP = "child";

    public static final String PARENT_IDP = "parent-idp";

    public static final String PARENT_USERNAME = "parent";

    @Page
    protected LoginUpdateProfilePage loginUpdateProfilePage;

    @Page
    protected AccountUpdateProfilePage profilePage;

    @Page
    private LoginPage loginPage;

    @Page
    protected ErrorPage errorPage;

    public static class ClientApp extends AbstractPageWithInjectedUrl {
        public static final String DEPLOYMENT_NAME = "client-linking";

        @ArquillianResource
        @OperateOnDeployment(ClientInitiatedAccountLinkTest.ClientApp.DEPLOYMENT_NAME)
        private URL url;

        @Override
        public URL getInjectedUrl() {
            return url;
        }
    }

    @Page
    private ClientInitiatedAccountLinkTest.ClientApp appPage;

    private String childUserId = null;

    @Test
    public void testErrorConditions() throws Exception {
        String helloUrl = getUriBuilder().clone().path("hello").build().toASCIIString();
        RealmResource realm = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP);
        List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        ClientRepresentation client = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP).clients().findByClientId("client-linking").get(0);
        UriBuilder redirectUri = UriBuilder.fromUri(appPage.getInjectedUrl().toString()).path("link").queryParam("response", "true");
        UriBuilder directLinking = UriBuilder.fromUri(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth")).path("realms/child/broker/{provider}/link").queryParam("client_id", "client-linking").queryParam("redirect_uri", redirectUri.build()).queryParam("hash", Base64Url.encode("crap".getBytes())).queryParam("nonce", UUID.randomUUID().toString());
        String linkUrl = directLinking.build(ClientInitiatedAccountLinkTest.PARENT_IDP).toString();
        // test not logged in
        navigateTo(linkUrl);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        loginPage.login("child", "password");
        Assert.assertTrue(driver.getCurrentUrl().contains("link_error=not_logged_in"));
        logoutAll();
        // now log in
        navigateTo(helloUrl);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        loginPage.login("child", "password");
        Assert.assertTrue(driver.getCurrentUrl().startsWith(helloUrl));
        Assert.assertTrue(driver.getPageSource().contains("Unknown request:"));
        // now test CSRF with bad hash.
        navigateTo(linkUrl);
        Assert.assertTrue(driver.getPageSource().contains("We're sorry..."));
        logoutAll();
        // now log in again with client that does not have scope
        String accountId = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP).clients().findByClientId(ACCOUNT_MANAGEMENT_CLIENT_ID).get(0).getId();
        RoleRepresentation manageAccount = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP).clients().get(accountId).roles().get(MANAGE_ACCOUNT).toRepresentation();
        RoleRepresentation manageLinks = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP).clients().get(accountId).roles().get(MANAGE_ACCOUNT_LINKS).toRepresentation();
        RoleRepresentation userRole = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP).roles().get("user").toRepresentation();
        client.setFullScopeAllowed(false);
        ClientResource clientResource = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP).clients().get(client.getId());
        clientResource.update(client);
        List<RoleRepresentation> roles = new LinkedList<>();
        roles.add(userRole);
        clientResource.getScopeMappings().realmLevel().add(roles);
        navigateTo(helloUrl);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        loginPage.login("child", "password");
        Assert.assertTrue(driver.getCurrentUrl().startsWith(helloUrl));
        Assert.assertTrue(driver.getPageSource().contains("Unknown request:"));
        UriBuilder linkBuilder = UriBuilder.fromUri(appPage.getInjectedUrl().toString()).path("link");
        String clientLinkUrl = linkBuilder.clone().queryParam("realm", ClientInitiatedAccountLinkTest.CHILD_IDP).queryParam("provider", ClientInitiatedAccountLinkTest.PARENT_IDP).build().toString();
        navigateTo(clientLinkUrl);
        Assert.assertTrue(driver.getCurrentUrl().contains("error=not_allowed"));
        logoutAll();
        // add MANAGE_ACCOUNT_LINKS scope should pass.
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        roles = new LinkedList();
        roles.add(manageLinks);
        clientResource.getScopeMappings().clientLevel(accountId).add(roles);
        navigateTo(clientLinkUrl);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        loginPage.login("child", "password");
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.PARENT_IDP));
        loginPage.login(ClientInitiatedAccountLinkTest.PARENT_USERNAME, "password");
        Assert.assertTrue(driver.getCurrentUrl().startsWith(linkBuilder.toTemplate()));
        Assert.assertTrue(driver.getPageSource().contains("Account Linked"));
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertFalse(links.isEmpty());
        realm.users().get(childUserId).removeFederatedIdentity(ClientInitiatedAccountLinkTest.PARENT_IDP);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        clientResource.getScopeMappings().clientLevel(accountId).remove(roles);
        logoutAll();
        navigateTo(clientLinkUrl);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        loginPage.login("child", "password");
        Assert.assertTrue(driver.getCurrentUrl().contains("link_error=not_allowed"));
        logoutAll();
        // add MANAGE_ACCOUNT scope should pass
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        roles = new LinkedList();
        roles.add(manageAccount);
        clientResource.getScopeMappings().clientLevel(accountId).add(roles);
        navigateTo(clientLinkUrl);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        loginPage.login("child", "password");
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.PARENT_IDP));
        loginPage.login(ClientInitiatedAccountLinkTest.PARENT_USERNAME, "password");
        Assert.assertTrue(driver.getCurrentUrl().startsWith(linkBuilder.toTemplate()));
        Assert.assertTrue(driver.getPageSource().contains("Account Linked"));
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertFalse(links.isEmpty());
        realm.users().get(childUserId).removeFederatedIdentity(ClientInitiatedAccountLinkTest.PARENT_IDP);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        clientResource.getScopeMappings().clientLevel(accountId).remove(roles);
        logoutAll();
        navigateTo(clientLinkUrl);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        loginPage.login("child", "password");
        Assert.assertTrue(driver.getCurrentUrl().contains("link_error=not_allowed"));
        logoutAll();
        // undo fullScopeAllowed
        client = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP).clients().findByClientId("client-linking").get(0);
        client.setFullScopeAllowed(true);
        clientResource.update(client);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        logoutAll();
    }

    @Test
    public void testAccountLink() throws Exception {
        RealmResource realm = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP);
        List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        UriBuilder linkBuilder = UriBuilder.fromUri(appPage.getInjectedUrl().toString()).path("link");
        String linkUrl = linkBuilder.clone().queryParam("realm", ClientInitiatedAccountLinkTest.CHILD_IDP).queryParam("provider", ClientInitiatedAccountLinkTest.PARENT_IDP).build().toString();
        System.out.println(("linkUrl: " + linkUrl));
        navigateTo(linkUrl);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        Assert.assertTrue(driver.getPageSource().contains(ClientInitiatedAccountLinkTest.PARENT_IDP));
        loginPage.login("child", "password");
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.PARENT_IDP));
        loginPage.login(ClientInitiatedAccountLinkTest.PARENT_USERNAME, "password");
        System.out.println(("After linking: " + (driver.getCurrentUrl())));
        System.out.println(driver.getPageSource());
        Assert.assertTrue(driver.getCurrentUrl().startsWith(linkBuilder.toTemplate()));
        Assert.assertTrue(driver.getPageSource().contains("Account Linked"));
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest(ClientInitiatedAccountLinkTest.CHILD_IDP, "child", "password", null, "client-linking", "password");
        Assert.assertNotNull(response.getAccessToken());
        Assert.assertNull(response.getError());
        Client httpClient = ClientBuilder.newClient();
        String firstToken = getToken(response, httpClient);
        Assert.assertNotNull(firstToken);
        navigateTo(linkUrl);
        Assert.assertTrue(driver.getPageSource().contains("Account Linked"));
        String nextToken = getToken(response, httpClient);
        Assert.assertNotNull(nextToken);
        Assert.assertNotEquals(firstToken, nextToken);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertFalse(links.isEmpty());
        realm.users().get(childUserId).removeFederatedIdentity(ClientInitiatedAccountLinkTest.PARENT_IDP);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        logoutAll();
    }

    @Test
    public void testLinkOnlyProvider() throws Exception {
        RealmResource realm = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP);
        IdentityProviderRepresentation rep = realm.identityProviders().get(ClientInitiatedAccountLinkTest.PARENT_IDP).toRepresentation();
        rep.setLinkOnly(true);
        realm.identityProviders().get(ClientInitiatedAccountLinkTest.PARENT_IDP).update(rep);
        try {
            List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
            Assert.assertTrue(links.isEmpty());
            UriBuilder linkBuilder = UriBuilder.fromUri(appPage.getInjectedUrl().toString()).path("link");
            String linkUrl = linkBuilder.clone().queryParam("realm", ClientInitiatedAccountLinkTest.CHILD_IDP).queryParam("provider", ClientInitiatedAccountLinkTest.PARENT_IDP).build().toString();
            navigateTo(linkUrl);
            Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
            // should not be on login page.  This is what we are testing
            Assert.assertFalse(driver.getPageSource().contains(ClientInitiatedAccountLinkTest.PARENT_IDP));
            // now test that we can still link.
            loginPage.login("child", "password");
            Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.PARENT_IDP));
            loginPage.login(ClientInitiatedAccountLinkTest.PARENT_USERNAME, "password");
            System.out.println(("After linking: " + (driver.getCurrentUrl())));
            System.out.println(driver.getPageSource());
            Assert.assertTrue(driver.getCurrentUrl().startsWith(linkBuilder.toTemplate()));
            Assert.assertTrue(driver.getPageSource().contains("Account Linked"));
            links = realm.users().get(childUserId).getFederatedIdentity();
            Assert.assertFalse(links.isEmpty());
            realm.users().get(childUserId).removeFederatedIdentity(ClientInitiatedAccountLinkTest.PARENT_IDP);
            links = realm.users().get(childUserId).getFederatedIdentity();
            Assert.assertTrue(links.isEmpty());
            logoutAll();
            System.out.println("testing link-only attack");
            navigateTo(linkUrl);
            Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
            System.out.println(("login page uri is: " + (driver.getCurrentUrl())));
            // ok, now scrape the code from page
            String pageSource = driver.getPageSource();
            String action = ActionURIUtils.getActionURIFromPageSource(pageSource);
            System.out.println(("action uri: " + action));
            Map<String, String> queryParams = ActionURIUtils.parseQueryParamsFromActionURI(action);
            System.out.println(("query params: " + queryParams));
            // now try and use the code to login to remote link-only idp
            String uri = "/auth/realms/child/broker/parent-idp/login";
            uri = UriBuilder.fromUri(AuthServerTestEnricher.getAuthServerContextRoot()).path(uri).queryParam(SESSION_CODE, queryParams.get(SESSION_CODE)).queryParam(CLIENT_ID, queryParams.get(CLIENT_ID)).queryParam(TAB_ID, queryParams.get(TAB_ID)).build().toString();
            System.out.println(("hack uri: " + uri));
            navigateTo(uri);
            Assert.assertTrue(driver.getPageSource().contains("Could not send authentication request to identity provider."));
        } finally {
            rep.setLinkOnly(false);
            realm.identityProviders().get(ClientInitiatedAccountLinkTest.PARENT_IDP).update(rep);
        }
    }

    @Test
    public void testAccountNotLinkedAutomatically() throws Exception {
        RealmResource realm = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP);
        List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        // Login to account mgmt first
        profilePage.open(ClientInitiatedAccountLinkTest.CHILD_IDP);
        WaitUtils.waitForPageToLoad();
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        loginPage.login("child", "password");
        profilePage.assertCurrent();
        // Now in another tab, open login screen with "prompt=login" . Login screen will be displayed even if I have SSO cookie
        UriBuilder linkBuilder = UriBuilder.fromUri(appPage.getInjectedUrl().toString()).path("nosuch");
        String linkUrl = linkBuilder.clone().queryParam(PROMPT_PARAM, PROMPT_VALUE_LOGIN).build().toString();
        navigateTo(linkUrl);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        loginPage.clickSocial(ClientInitiatedAccountLinkTest.PARENT_IDP);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.PARENT_IDP));
        loginPage.login(ClientInitiatedAccountLinkTest.PARENT_USERNAME, "password");
        // Test I was not automatically linked.
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        loginUpdateProfilePage.assertCurrent();
        loginUpdateProfilePage.update("Joe", "Doe", "joe@parent.com");
        errorPage.assertCurrent();
        Assert.assertEquals("You are already authenticated as different user 'child' in this session. Please logout first.", errorPage.getError());
        logoutAll();
        // Remove newly created user
        String newUserId = ApiUtil.findUserByUsername(realm, "parent").getId();
        getCleanup("child").addUserId(newUserId);
    }

    @Test
    public void testAccountLinkingExpired() throws Exception {
        RealmResource realm = adminClient.realms().realm(ClientInitiatedAccountLinkTest.CHILD_IDP);
        List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        // Login to account mgmt first
        profilePage.open(ClientInitiatedAccountLinkTest.CHILD_IDP);
        WaitUtils.waitForPageToLoad();
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.CHILD_IDP));
        loginPage.login("child", "password");
        profilePage.assertCurrent();
        // Now in another tab, request account linking
        UriBuilder linkBuilder = UriBuilder.fromUri(appPage.getInjectedUrl().toString()).path("link");
        String linkUrl = linkBuilder.clone().queryParam("realm", ClientInitiatedAccountLinkTest.CHILD_IDP).queryParam("provider", ClientInitiatedAccountLinkTest.PARENT_IDP).build().toString();
        navigateTo(linkUrl);
        Assert.assertTrue(loginPage.isCurrent(ClientInitiatedAccountLinkTest.PARENT_IDP));
        // Logout "child" userSession in the meantime (for example through admin request)
        realm.logoutAll();
        // Finish login on parent.
        loginPage.login(ClientInitiatedAccountLinkTest.PARENT_USERNAME, "password");
        // Test I was not automatically linked
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        errorPage.assertCurrent();
        Assert.assertEquals("Requested broker account linking, but current session is no longer valid.", errorPage.getError());
        logoutAll();
    }
}

