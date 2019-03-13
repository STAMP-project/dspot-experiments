package org.keycloak.testsuite.springboot;


import Constants.ACCOUNT_MANAGEMENT_CLIENT_ID;
import Constants.CLIENT_ID;
import Constants.TAB_ID;
import LoginActionsService.SESSION_CODE;
import OIDCLoginProtocol.PROMPT_PARAM;
import OIDCLoginProtocol.PROMPT_VALUE_LOGIN;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.UriBuilder;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.util.Base64Url;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.testsuite.ActionURIUtils;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.arquillian.AuthServerTestEnricher;
import org.keycloak.testsuite.pages.AccountUpdateProfilePage;
import org.keycloak.testsuite.pages.ErrorPage;
import org.keycloak.testsuite.pages.LoginUpdateProfilePage;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.WaitUtils;


public class AccountLinkSpringBootTest extends AbstractSpringBootTest {
    private static final String PARENT_REALM = "parent-realm";

    private static final String LINKING_URL = (AbstractSpringBootTest.BASE_URL) + "/LinkServlet";

    private static final String PARENT_USERNAME = "parent-username";

    private static final String PARENT_PASSWORD = "parent-password";

    private static final String CHILD_USERNAME_1 = "child-username-1";

    private static final String CHILD_PASSWORD_1 = "child-password-1";

    private static final String CHILD_USERNAME_2 = "child-username-2";

    private static final String CHILD_PASSWORD_2 = "child-password-2";

    @Page
    private LinkingPage linkingPage;

    @Page
    private AccountUpdateProfilePage profilePage;

    @Page
    private LoginUpdateProfilePage loginUpdateProfilePage;

    @Page
    private ErrorPage errorPage;

    private String childUserId = null;

    @Test
    public void testErrorConditions() throws Exception {
        RealmResource realm = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME);
        List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        ClientRepresentation client = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME).clients().findByClientId(AbstractSpringBootTest.CLIENT_ID).get(0);
        UriBuilder redirectUri = UriBuilder.fromUri(AccountLinkSpringBootTest.LINKING_URL).queryParam("response", "true");
        UriBuilder directLinking = UriBuilder.fromUri(((AuthServerTestEnricher.getAuthServerContextRoot()) + "/auth")).path("realms/{child-realm}/broker/{provider}/link").queryParam("client_id", AbstractSpringBootTest.CLIENT_ID).queryParam("redirect_uri", redirectUri.build()).queryParam("hash", Base64Url.encode("crap".getBytes())).queryParam("nonce", UUID.randomUUID().toString());
        String linkUrl = directLinking.build(AbstractSpringBootTest.REALM_NAME, AccountLinkSpringBootTest.PARENT_REALM).toString();
        // test that child user cannot log into parent realm
        navigateTo(linkUrl);
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
        Assert.assertTrue(driver.getCurrentUrl().contains("link_error=not_logged_in"));
        logoutAll();
        // now log in
        navigateTo(((AccountLinkSpringBootTest.LINKING_URL) + "?response=true"));
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
        Assert.assertTrue("Must be on linking page", linkingPage.isCurrent());
        Assert.assertEquals("account linked", linkingPage.getErrorMessage().toLowerCase());
        // now test CSRF with bad hash.
        navigateTo(linkUrl);
        Assert.assertTrue(driver.getPageSource().contains("We're sorry..."));
        logoutAll();
        // now log in again with client that does not have scope
        String accountId = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME).clients().findByClientId(ACCOUNT_MANAGEMENT_CLIENT_ID).get(0).getId();
        RoleRepresentation manageAccount = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME).clients().get(accountId).roles().get(MANAGE_ACCOUNT).toRepresentation();
        RoleRepresentation manageLinks = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME).clients().get(accountId).roles().get(MANAGE_ACCOUNT_LINKS).toRepresentation();
        RoleRepresentation userRole = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME).roles().get(AbstractSpringBootTest.CORRECT_ROLE).toRepresentation();
        client.setFullScopeAllowed(false);
        ClientResource clientResource = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME).clients().get(client.getId());
        clientResource.update(client);
        List<RoleRepresentation> roles = new LinkedList<>();
        roles.add(userRole);
        clientResource.getScopeMappings().realmLevel().add(roles);
        navigateTo(((AccountLinkSpringBootTest.LINKING_URL) + "?response=true"));
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
        Assert.assertTrue(linkingPage.isCurrent());
        Assert.assertEquals("account linked", linkingPage.getErrorMessage().toLowerCase());
        UriBuilder linkBuilder = UriBuilder.fromUri(AccountLinkSpringBootTest.LINKING_URL);
        String clientLinkUrl = linkBuilder.clone().queryParam("realm", AbstractSpringBootTest.REALM_NAME).queryParam("provider", AccountLinkSpringBootTest.PARENT_REALM).build().toString();
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
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
        Assert.assertTrue(loginPage.isCurrent(AccountLinkSpringBootTest.PARENT_REALM));
        loginPage.login(AccountLinkSpringBootTest.PARENT_USERNAME, AccountLinkSpringBootTest.PARENT_PASSWORD);
        Assert.assertTrue(driver.getCurrentUrl().startsWith(linkBuilder.toTemplate()));
        Assert.assertTrue(driver.getPageSource().contains("Account linked"));
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertFalse(links.isEmpty());
        realm.users().get(childUserId).removeFederatedIdentity(AccountLinkSpringBootTest.PARENT_REALM);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        clientResource.getScopeMappings().clientLevel(accountId).remove(roles);
        logoutAll();
        navigateTo(clientLinkUrl);
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
        Assert.assertTrue(driver.getCurrentUrl().contains("link_error=not_allowed"));
        logoutAll();
        // add MANAGE_ACCOUNT scope should pass
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        roles = new LinkedList();
        roles.add(manageAccount);
        clientResource.getScopeMappings().clientLevel(accountId).add(roles);
        navigateTo(clientLinkUrl);
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
        Assert.assertTrue(loginPage.isCurrent(AccountLinkSpringBootTest.PARENT_REALM));
        loginPage.login(AccountLinkSpringBootTest.PARENT_USERNAME, AccountLinkSpringBootTest.PARENT_PASSWORD);
        Assert.assertTrue(driver.getCurrentUrl().startsWith(linkBuilder.toTemplate()));
        Assert.assertTrue(driver.getPageSource().contains("Account linked"));
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertFalse(links.isEmpty());
        realm.users().get(childUserId).removeFederatedIdentity(AccountLinkSpringBootTest.PARENT_REALM);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        clientResource.getScopeMappings().clientLevel(accountId).remove(roles);
        logoutAll();
        navigateTo(clientLinkUrl);
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
        Assert.assertTrue(driver.getCurrentUrl().contains("link_error=not_allowed"));
        logoutAll();
        // undo fullScopeAllowed
        client = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME).clients().findByClientId(AbstractSpringBootTest.CLIENT_ID).get(0);
        client.setFullScopeAllowed(true);
        clientResource.update(client);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        logoutAll();
    }

    @Test
    public void testAccountLink() throws Exception {
        RealmResource realm = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME);
        List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        UriBuilder linkBuilder = UriBuilder.fromUri(AccountLinkSpringBootTest.LINKING_URL);
        String linkUrl = linkBuilder.clone().queryParam("realm", AbstractSpringBootTest.REALM_NAME).queryParam("provider", AccountLinkSpringBootTest.PARENT_REALM).build().toString();
        log.info(("linkUrl: " + linkUrl));
        navigateTo(linkUrl);
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        Assert.assertTrue(driver.getPageSource().contains(AccountLinkSpringBootTest.PARENT_REALM));
        loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
        Assert.assertTrue(loginPage.isCurrent(AccountLinkSpringBootTest.PARENT_REALM));
        loginPage.login(AccountLinkSpringBootTest.PARENT_USERNAME, AccountLinkSpringBootTest.PARENT_PASSWORD);
        log.info(("After linking: " + (driver.getCurrentUrl())));
        log.info(driver.getPageSource());
        Assert.assertTrue(driver.getCurrentUrl().startsWith(linkBuilder.toTemplate()));
        Assert.assertTrue(driver.getPageSource().contains("Account linked"));
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest(AbstractSpringBootTest.REALM_NAME, AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1, null, AbstractSpringBootTest.CLIENT_ID, AbstractSpringBootTest.SECRET);
        Assert.assertNotNull(response.getAccessToken());
        Assert.assertNull(response.getError());
        Client httpClient = ClientBuilder.newClient();
        String firstToken = getToken(response, httpClient);
        Assert.assertNotNull(firstToken);
        navigateTo(linkUrl);
        Assert.assertTrue(driver.getPageSource().contains("Account linked"));
        String nextToken = getToken(response, httpClient);
        Assert.assertNotNull(nextToken);
        Assert.assertNotEquals(firstToken, nextToken);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertFalse(links.isEmpty());
        realm.users().get(childUserId).removeFederatedIdentity(AccountLinkSpringBootTest.PARENT_REALM);
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        logoutAll();
    }

    @Test
    public void testLinkOnlyProvider() throws Exception {
        RealmResource realm = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME);
        IdentityProviderRepresentation rep = realm.identityProviders().get(AccountLinkSpringBootTest.PARENT_REALM).toRepresentation();
        rep.setLinkOnly(true);
        realm.identityProviders().get(AccountLinkSpringBootTest.PARENT_REALM).update(rep);
        try {
            List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
            Assert.assertTrue(links.isEmpty());
            UriBuilder linkBuilder = UriBuilder.fromUri(AccountLinkSpringBootTest.LINKING_URL);
            String linkUrl = linkBuilder.clone().queryParam("realm", AbstractSpringBootTest.REALM_NAME).queryParam("provider", AccountLinkSpringBootTest.PARENT_REALM).build().toString();
            navigateTo(linkUrl);
            Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
            // should not be on login page.  This is what we are testing
            Assert.assertFalse(driver.getPageSource().contains(AccountLinkSpringBootTest.PARENT_REALM));
            // now test that we can still link.
            loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
            Assert.assertTrue(loginPage.isCurrent(AccountLinkSpringBootTest.PARENT_REALM));
            loginPage.login(AccountLinkSpringBootTest.PARENT_USERNAME, AccountLinkSpringBootTest.PARENT_PASSWORD);
            log.info(("After linking: " + (driver.getCurrentUrl())));
            log.info(driver.getPageSource());
            Assert.assertTrue(driver.getCurrentUrl().startsWith(linkBuilder.toTemplate()));
            Assert.assertTrue(driver.getPageSource().contains("Account linked"));
            links = realm.users().get(childUserId).getFederatedIdentity();
            Assert.assertFalse(links.isEmpty());
            realm.users().get(childUserId).removeFederatedIdentity(AccountLinkSpringBootTest.PARENT_REALM);
            links = realm.users().get(childUserId).getFederatedIdentity();
            Assert.assertTrue(links.isEmpty());
            logoutAll();
            log.info("testing link-only attack");
            navigateTo(linkUrl);
            Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
            log.info(("login page uri is: " + (driver.getCurrentUrl())));
            // ok, now scrape the code from page
            String pageSource = driver.getPageSource();
            String action = ActionURIUtils.getActionURIFromPageSource(pageSource);
            System.out.println(("action uri: " + action));
            Map<String, String> queryParams = ActionURIUtils.parseQueryParamsFromActionURI(action);
            System.out.println(("query params: " + queryParams));
            // now try and use the code to login to remote link-only idp
            String uri = ((("/auth/realms/" + (AbstractSpringBootTest.REALM_NAME)) + "/broker/") + (AccountLinkSpringBootTest.PARENT_REALM)) + "/login";
            uri = UriBuilder.fromUri(AuthServerTestEnricher.getAuthServerContextRoot()).path(uri).queryParam(SESSION_CODE, queryParams.get(SESSION_CODE)).queryParam(Constants.CLIENT_ID, queryParams.get(Constants.CLIENT_ID)).queryParam(TAB_ID, queryParams.get(TAB_ID)).build().toString();
            log.info(("hack uri: " + uri));
            navigateTo(uri);
            Assert.assertTrue(driver.getPageSource().contains("Could not send authentication request to identity provider."));
        } finally {
            rep.setLinkOnly(false);
            realm.identityProviders().get(AccountLinkSpringBootTest.PARENT_REALM).update(rep);
        }
    }

    @Test
    public void testAccountNotLinkedAutomatically() throws Exception {
        RealmResource realm = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME);
        List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        // Login to account mgmt first
        profilePage.open(AbstractSpringBootTest.REALM_NAME);
        WaitUtils.waitForPageToLoad();
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
        profilePage.assertCurrent();
        // Now in another tab, open login screen with "prompt=login" . Login screen will be displayed even if I have SSO cookie
        UriBuilder linkBuilder = UriBuilder.fromUri(AccountLinkSpringBootTest.LINKING_URL);
        String linkUrl = linkBuilder.clone().queryParam(PROMPT_PARAM, PROMPT_VALUE_LOGIN).build().toString();
        navigateTo(linkUrl);
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        loginPage.clickSocial(AccountLinkSpringBootTest.PARENT_REALM);
        Assert.assertTrue(loginPage.isCurrent(AccountLinkSpringBootTest.PARENT_REALM));
        loginPage.login(AccountLinkSpringBootTest.PARENT_USERNAME, AccountLinkSpringBootTest.PARENT_PASSWORD);
        // Test I was not automatically linked.
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        loginUpdateProfilePage.assertCurrent();
        loginUpdateProfilePage.update("Joe", "Doe", "joe@parent.com");
        errorPage.assertCurrent();
        Assert.assertEquals((("You are already authenticated as different user '" + (AccountLinkSpringBootTest.CHILD_USERNAME_1)) + "' in this session. Please logout first."), errorPage.getError());
        logoutAll();
        // Remove newly created user
        String newUserId = ApiUtil.findUserByUsername(realm, AccountLinkSpringBootTest.PARENT_USERNAME).getId();
        getCleanup(AbstractSpringBootTest.REALM_NAME).addUserId(newUserId);
    }

    @Test
    public void testAccountLinkingExpired() throws Exception {
        RealmResource realm = adminClient.realms().realm(AbstractSpringBootTest.REALM_NAME);
        List<FederatedIdentityRepresentation> links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        // Login to account mgmt first
        profilePage.open(AbstractSpringBootTest.REALM_NAME);
        WaitUtils.waitForPageToLoad();
        Assert.assertTrue(loginPage.isCurrent(AbstractSpringBootTest.REALM_NAME));
        loginPage.login(AccountLinkSpringBootTest.CHILD_USERNAME_1, AccountLinkSpringBootTest.CHILD_PASSWORD_1);
        profilePage.assertCurrent();
        // Now in another tab, request account linking
        UriBuilder linkBuilder = UriBuilder.fromUri(AccountLinkSpringBootTest.LINKING_URL);
        String linkUrl = linkBuilder.clone().queryParam("realm", AbstractSpringBootTest.REALM_NAME).queryParam("provider", AccountLinkSpringBootTest.PARENT_REALM).build().toString();
        navigateTo(linkUrl);
        Assert.assertTrue(loginPage.isCurrent(AccountLinkSpringBootTest.PARENT_REALM));
        // Logout "child" userSession in the meantime (for example through admin request)
        realm.logoutAll();
        // Finish login on parent.
        loginPage.login(AccountLinkSpringBootTest.PARENT_USERNAME, AccountLinkSpringBootTest.PARENT_PASSWORD);
        // Test I was not automatically linked
        links = realm.users().get(childUserId).getFederatedIdentity();
        Assert.assertTrue(links.isEmpty());
        errorPage.assertCurrent();
        Assert.assertEquals("Requested broker account linking, but current session is no longer valid.", errorPage.getError());
        logoutAll();
    }
}

