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
package org.keycloak.testsuite.broker;


import DummyUserFederationProviderFactory.PROVIDER_NAME;
import HttpHeaders.AUTHORIZATION;
import IdentityProviderRepresentation.UPFLM_MISSING;
import IdentityProviderRepresentation.UPFLM_OFF;
import IdentityProviderRepresentation.UPFLM_ON;
import Response.Status.FORBIDDEN;
import Response.Status.OK;
import UserModel.RequiredAction.VERIFY_EMAIL;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import javax.mail.MessagingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.models.FederatedIdentityModel;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.Urls;
import org.keycloak.storage.UserStorageProviderModel;
import org.keycloak.testsuite.broker.util.UserSessionStatusServlet;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;


/**
 *
 *
 * @author pedroigor
 */
public abstract class AbstractKeycloakIdentityProviderTest extends AbstractIdentityProviderTest {
    @Test
    public void testSuccessfulAuthentication() {
        IdentityProviderModel identityProviderModel = getIdentityProviderModel();
        setUpdateProfileFirstLogin(UPFLM_ON);
        UserModel user = assertSuccessfulAuthentication(identityProviderModel, "test-user", "new@email.com", true);
        Assert.assertEquals("617-666-7777", user.getFirstAttribute("mobile"));
    }

    @Test
    public void testDisabledUser() throws Exception {
        KeycloakSession session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        AbstractIdentityProviderTest.setUpdateProfileFirstLogin(session.realms().getRealmByName("realm-with-broker"), UPFLM_OFF);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(session, true);
        driver.navigate().to("http://localhost:8081/test-app");
        loginPage.clickSocial(getProviderId());
        loginPage.login("test-user", "password");
        System.out.println(driver.getPageSource());
        driver.navigate().to("http://localhost:8081/test-app/logout");
        try {
            session = AbstractIdentityProviderTest.brokerServerRule.startSession();
            session.users().getUserByUsername("test-user", session.realms().getRealmByName("realm-with-broker")).setEnabled(false);
            AbstractIdentityProviderTest.brokerServerRule.stopSession(session, true);
            driver.navigate().to("http://localhost:8081/test-app");
            loginPage.clickSocial(getProviderId());
            loginPage.login("test-user", "password");
            Assert.assertTrue(errorPage.isCurrent());
            Assert.assertEquals("Account is disabled, contact admin.", errorPage.getError());
        } finally {
            session = AbstractIdentityProviderTest.brokerServerRule.startSession();
            session.users().getUserByUsername("test-user", session.realms().getRealmByName("realm-with-broker")).setEnabled(true);
            AbstractIdentityProviderTest.brokerServerRule.stopSession(session, true);
        }
    }

    @Test
    public void testSuccessfulAuthenticationUpdateProfileOnMissing_nothingMissing() {
        IdentityProviderModel identityProviderModel = getIdentityProviderModel();
        setUpdateProfileFirstLogin(UPFLM_MISSING);
        assertSuccessfulAuthentication(identityProviderModel, "test-user", "test-user@localhost", false);
    }

    @Test
    public void testSuccessfulAuthenticationUpdateProfileOnMissing_missingEmail() {
        IdentityProviderModel identityProviderModel = getIdentityProviderModel();
        setUpdateProfileFirstLogin(UPFLM_MISSING);
        assertSuccessfulAuthentication(identityProviderModel, "test-user-noemail", "new@email.com", true);
    }

    @Test
    public void testSuccessfulAuthenticationWithoutUpdateProfile() {
        IdentityProviderModel identityProviderModel = getIdentityProviderModel();
        setUpdateProfileFirstLogin(UPFLM_OFF);
        assertSuccessfulAuthentication(identityProviderModel, "test-user", "test-user@localhost", false);
    }

    /**
     * Test that verify email action is performed if email is provided and email trust is not enabled for the provider
     *
     * @throws MessagingException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testSuccessfulAuthenticationWithoutUpdateProfile_emailProvided_emailVerifyEnabled() throws IOException, MessagingException {
        RealmModel realm = getRealm();
        realm.setVerifyEmail(true);
        AbstractIdentityProviderTest.setUpdateProfileFirstLogin(realm, UPFLM_OFF);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        IdentityProviderModel identityProviderModel = getIdentityProviderModel();
        try {
            identityProviderModel.setTrustEmail(false);
            UserModel federatedUser = assertSuccessfulAuthenticationWithEmailVerification(identityProviderModel, "test-user", "test-user@localhost", false);
            // email is verified now
            Assert.assertFalse(federatedUser.getRequiredActions().contains(VERIFY_EMAIL.name()));
        } finally {
            getRealm().setVerifyEmail(false);
        }
    }

    /**
     * Test for KEYCLOAK-1053 - verify email action is not performed if email is not provided, login is normal, but action stays in set to be performed later
     */
    @Test
    public void testSuccessfulAuthenticationWithoutUpdateProfile_emailNotProvided_emailVerifyEnabled() throws Exception {
        RealmModel realm = getRealm();
        realm.setVerifyEmail(true);
        AbstractIdentityProviderTest.setUpdateProfileFirstLogin(realm, UPFLM_OFF);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        try {
            IdentityProviderModel identityProviderModel = getIdentityProviderModel();
            UserModel federatedUser = assertSuccessfulAuthentication(identityProviderModel, "test-user-noemail", null, false);
            Assert.assertTrue(federatedUser.getRequiredActions().contains(VERIFY_EMAIL.name()));
        } finally {
            getRealm().setVerifyEmail(false);
        }
    }

    /**
     * Test for KEYCLOAK-1372 - verify email action is not performed if email is provided but email trust is enabled for the provider
     */
    @Test
    public void testSuccessfulAuthenticationWithoutUpdateProfile_emailProvided_emailVerifyEnabled_emailTrustEnabled() {
        RealmModel realmWithBroker = getRealm();
        realmWithBroker.setVerifyEmail(true);
        AbstractIdentityProviderTest.setUpdateProfileFirstLogin(realmWithBroker, UPFLM_OFF);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        IdentityProviderModel identityProviderModel = getIdentityProviderModel();
        try {
            identityProviderModel.setTrustEmail(true);
            UserModel federatedUser = assertSuccessfulAuthentication(identityProviderModel, "test-user", "test-user@localhost", false);
            Assert.assertFalse(federatedUser.getRequiredActions().contains(VERIFY_EMAIL.name()));
        } finally {
            identityProviderModel.setTrustEmail(false);
            getRealm().setVerifyEmail(false);
        }
    }

    /**
     * Test for KEYCLOAK-1372 - verify email action is performed if email is provided and email trust is enabled for the provider, but email is changed on First login update profile page
     *
     * @throws MessagingException
     * 		
     * @throws IOException
     * 		
     */
    @Test
    public void testSuccessfulAuthentication_emailTrustEnabled_emailVerifyEnabled_emailUpdatedOnFirstLogin() throws IOException, MessagingException {
        RealmModel realm = getRealm();
        realm.setVerifyEmail(true);
        AbstractIdentityProviderTest.setUpdateProfileFirstLogin(realm, UPFLM_ON);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        IdentityProviderModel identityProviderModel = getIdentityProviderModel();
        try {
            identityProviderModel.setTrustEmail(true);
            UserModel user = assertSuccessfulAuthenticationWithEmailVerification(identityProviderModel, "test-user", "new@email.com", true);
            Assert.assertEquals("617-666-7777", user.getFirstAttribute("mobile"));
        } finally {
            identityProviderModel.setTrustEmail(false);
            getRealm().setVerifyEmail(false);
        }
    }

    @Test
    public void testSuccessfulAuthenticationWithoutUpdateProfile_newUser_emailAsUsername() throws Exception {
        RealmModel realm = getRealm();
        realm.setRegistrationEmailAsUsername(true);
        AbstractIdentityProviderTest.setUpdateProfileFirstLogin(realm, UPFLM_OFF);
        AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
        this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        try {
            IdentityProviderModel identityProviderModel = getIdentityProviderModel();
            authenticateWithIdentityProvider(identityProviderModel, "test-user", false);
            // authenticated and redirected to app
            Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/test-app"));
            AbstractIdentityProviderTest.brokerServerRule.stopSession(session, true);
            session = AbstractIdentityProviderTest.brokerServerRule.startSession();
            // check correct user is created with email as username and bound to correct federated identity
            realm = getRealm();
            UserModel federatedUser = session.users().getUserByUsername("test-user@localhost", realm);
            Assert.assertNotNull(federatedUser);
            Assert.assertEquals("test-user@localhost", federatedUser.getUsername());
            doAssertFederatedUser(federatedUser, identityProviderModel, "test-user@localhost", false);
            Set<FederatedIdentityModel> federatedIdentities = this.session.users().getFederatedIdentities(federatedUser, realm);
            Assert.assertEquals(1, federatedIdentities.size());
            FederatedIdentityModel federatedIdentityModel = federatedIdentities.iterator().next();
            Assert.assertEquals(getProviderId(), federatedIdentityModel.getIdentityProvider());
            driver.navigate().to("http://localhost:8081/test-app/logout");
            driver.navigate().to("http://localhost:8081/test-app");
            Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/auth/realms/realm-with-broker/protocol/openid-connect/auth"));
        } finally {
            getRealm().setRegistrationEmailAsUsername(false);
        }
    }

    @Test
    public void testDisabled() {
        IdentityProviderModel identityProviderModel = getIdentityProviderModel();
        identityProviderModel.setEnabled(false);
        this.driver.navigate().to("http://localhost:8081/test-app/");
        Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/auth/realms/realm-with-broker/protocol/openid-connect/auth"));
        try {
            this.driver.findElement(By.className(getProviderId()));
            Assert.fail((("Provider [" + (getProviderId())) + "] not disabled."));
        } catch (NoSuchElementException nsee) {
        }
    }

    @Test
    public void testAccountManagementLinkIdentity() {
        // Login as pedroigor to account management
        accountFederatedIdentityPage.realm("realm-with-broker");
        accountFederatedIdentityPage.open();
        Assert.assertTrue(driver.getTitle().equals("Log in to realm-with-broker"));
        loginPage.login("pedroigor", "password");
        Assert.assertTrue(accountFederatedIdentityPage.isCurrent());
        // Link my "pedroigor" identity with "test-user" from brokered Keycloak
        IdentityProviderModel identityProviderModel = getIdentityProviderModel();
        setUpdateProfileFirstLogin(UPFLM_ON);
        accountFederatedIdentityPage.clickAddProvider(identityProviderModel.getAlias());
        Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8082/auth/"));
        this.loginPage.login("test-user", "password");
        doAfterProviderAuthentication();
        // Assert identity linked in account management
        Assert.assertTrue(accountFederatedIdentityPage.isCurrent());
        Assert.assertTrue(driver.getPageSource().contains((("id=\"remove-link-" + (identityProviderModel.getAlias())) + "\"")));
        // Revoke grant in account mgmt
        revokeGrant();
        // Logout from account management
        accountFederatedIdentityPage.logout();
        Assert.assertTrue(driver.getTitle().equals("Log in to realm-with-broker"));
        // Assert I am logged immediately to account management due to previously linked "test-user" identity
        loginPage.clickSocial(identityProviderModel.getAlias());
        this.loginPage.login("test-user", "password");
        doAfterProviderAuthentication();
        Assert.assertTrue(accountFederatedIdentityPage.isCurrent());
        Assert.assertTrue(driver.getPageSource().contains((("id=\"remove-link-" + (identityProviderModel.getAlias())) + "\"")));
        // Unlink my "test-user"
        accountFederatedIdentityPage.clickRemoveProvider(identityProviderModel.getAlias());
        Assert.assertTrue(driver.getPageSource().contains((("id=\"add-link-" + (identityProviderModel.getAlias())) + "\"")));
        // Revoke grant in account mgmt
        revokeGrant();
        // Logout from account management
        accountFederatedIdentityPage.logout();
        Assert.assertTrue(driver.getTitle().equals("Log in to realm-with-broker"));
        Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/auth/realms/realm-with-broker/protocol/openid-connect/auth"));
        // Try to login. Previous link is not valid anymore, so now it should try to register new user
        this.loginPage.clickSocial(identityProviderModel.getAlias());
        this.loginPage.login("test-user", "password");
        doAfterProviderAuthentication();
        this.updateProfilePage.assertCurrent();
    }

    // KEYCLOAK-1822
    @Test
    public void testAccountManagementLinkedIdentityAlreadyExists() {
        // Login as "test-user" through broker
        IdentityProviderModel identityProvider = getIdentityProviderModel();
        setUpdateProfileFirstLogin(UPFLM_OFF);
        assertSuccessfulAuthentication(identityProvider, "test-user", "test-user@localhost", false);
        // Login as pedroigor to account management
        accountFederatedIdentityPage.realm("realm-with-broker");
        accountFederatedIdentityPage.open();
        Assert.assertThat(driver.getTitle(), Matchers.is("Log in to realm-with-broker"));
        loginPage.login("pedroigor", "password");
        accountFederatedIdentityPage.assertCurrent();
        // Try to link my "pedroigor" identity with "test-user" from brokered Keycloak.
        accountFederatedIdentityPage.clickAddProvider(identityProvider.getAlias());
        Assert.assertThat(this.driver.getCurrentUrl(), Matchers.startsWith("http://localhost:8082/auth/"));
        this.loginPage.login("test-user", "password");
        doAfterProviderAuthentication();
        // Error is displayed in account management because federated identity"test-user" already linked to local account "test-user"
        accountFederatedIdentityPage.assertCurrent();
        Assert.assertEquals((("Federated identity returned by " + (getProviderId())) + " is already linked to another user."), accountFederatedIdentityPage.getError());
    }

    @Test
    public void testTokenStorageAndRetrievalByApplication() {
        setUpdateProfileFirstLogin(UPFLM_ON);
        IdentityProviderModel identityProviderModel = getIdentityProviderModel();
        setStoreToken(identityProviderModel, true);
        try {
            authenticateWithIdentityProvider(identityProviderModel, "test-user", true);
            AbstractIdentityProviderTest.brokerServerRule.stopSession(session, true);
            session = AbstractIdentityProviderTest.brokerServerRule.startSession();
            UserModel federatedUser = getFederatedUser();
            RealmModel realm = getRealm();
            Set<FederatedIdentityModel> federatedIdentities = this.session.users().getFederatedIdentities(federatedUser, realm);
            Assert.assertFalse(federatedIdentities.isEmpty());
            Assert.assertEquals(1, federatedIdentities.size());
            FederatedIdentityModel identityModel = federatedIdentities.iterator().next();
            Assert.assertNotNull(identityModel.getToken());
            UserSessionStatusServlet.UserSessionStatus userSessionStatus = retrieveSessionStatus();
            String accessToken = userSessionStatus.getAccessTokenString();
            URI tokenEndpointUrl = Urls.identityProviderRetrieveToken(AbstractIdentityProviderTest.BASE_URI, getProviderId(), realm.getName());
            final String authHeader = "Bearer " + accessToken;
            ClientRequestFilter authFilter = new ClientRequestFilter() {
                @Override
                public void filter(ClientRequestContext requestContext) throws IOException {
                    requestContext.getHeaders().add(AUTHORIZATION, authHeader);
                }
            };
            Client client = ClientBuilder.newBuilder().register(authFilter).build();
            WebTarget tokenEndpoint = client.target(tokenEndpointUrl);
            Response response = tokenEndpoint.request().get();
            Assert.assertEquals(OK.getStatusCode(), response.getStatus());
            Assert.assertNotNull(response.readEntity(String.class));
            revokeGrant();
            driver.navigate().to("http://localhost:8081/test-app/logout");
            String currentUrl = this.driver.getCurrentUrl();
            // System.out.println("after logout currentUrl: " + currentUrl);
            Assert.assertTrue(currentUrl.startsWith("http://localhost:8081/auth/realms/realm-with-broker/protocol/openid-connect/auth"));
            unconfigureUserRetrieveToken("test-user");
            loginIDP("test-user");
            // authenticateWithIdentityProvider(identityProviderModel, "test-user");
            Assert.assertEquals("http://localhost:8081/test-app", driver.getCurrentUrl());
            userSessionStatus = retrieveSessionStatus();
            accessToken = userSessionStatus.getAccessTokenString();
            final String authHeader2 = "Bearer " + accessToken;
            ClientRequestFilter authFilter2 = new ClientRequestFilter() {
                @Override
                public void filter(ClientRequestContext requestContext) throws IOException {
                    requestContext.getHeaders().add(AUTHORIZATION, authHeader2);
                }
            };
            client = ClientBuilder.newBuilder().register(authFilter2).build();
            tokenEndpoint = client.target(tokenEndpointUrl);
            response = tokenEndpoint.request().get();
            Assert.assertEquals(FORBIDDEN.getStatusCode(), response.getStatus());
            revokeGrant();
            driver.navigate().to("http://localhost:8081/test-app/logout");
            driver.navigate().to("http://localhost:8081/test-app");
            Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/auth/realms/realm-with-broker/protocol/openid-connect/auth"));
        } finally {
            setStoreToken(identityProviderModel, false);
        }
    }

    @Test
    public void testWithLinkedFederationProvider() throws Exception {
        setUpdateProfileFirstLogin(UPFLM_OFF);
        // Add federationProvider to realm. It's configured with sync registrations
        RealmModel realm = getRealm();
        UserStorageProviderModel model = new UserStorageProviderModel();
        model.setProviderId(PROVIDER_NAME);
        model.setPriority(1);
        model.setName("test-sync-dummy");
        model.setFullSyncPeriod((-1));
        model.setChangedSyncPeriod((-1));
        model.setLastSync(0);
        UserStorageProviderModel dummyModel = new UserStorageProviderModel(realm.addComponentModel(model));
        AbstractIdentityProviderTest.brokerServerRule.stopSession(session, true);
        session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        try {
            // Login as user "test-user" to account management.
            authenticateWithIdentityProvider(getIdentityProviderModel(), "test-user", false);
            changePasswordPage.realm("realm-with-broker");
            changePasswordPage.open();
            Assert.assertTrue(changePasswordPage.isCurrent());
            // Assert changing password with old password "secret" as this is the password from federationProvider (See DummyUserFederationProvider)
            changePasswordPage.changePassword("new-password", "new-password");
            Assert.assertEquals("Please specify password.", accountUpdateProfilePage.getError());
            changePasswordPage.changePassword("bad", "new-password", "new-password");
            Assert.assertEquals("Invalid existing password.", accountUpdateProfilePage.getError());
            changePasswordPage.changePassword("secret", "new-password", "new-password");
            Assert.assertEquals("Your password has been updated.", accountUpdateProfilePage.getSuccess());
            // Logout
            driver.navigate().to("http://localhost:8081/test-app/logout");
            // Login as user "test-user-noemail" .
            authenticateWithIdentityProvider(getIdentityProviderModel(), "test-user-noemail", false);
            changePasswordPage.open();
            Assert.assertTrue(changePasswordPage.isCurrent());
            // Assert old password is not required as federationProvider doesn't have it for this user
            changePasswordPage.changePassword("new-password", "new-password");
            Assert.assertEquals("Your password has been updated.", accountUpdateProfilePage.getSuccess());
            // Now it is required as it's set on model
            changePasswordPage.changePassword("new-password2", "new-password2");
            Assert.assertEquals("Please specify password.", accountUpdateProfilePage.getError());
            changePasswordPage.changePassword("new-password", "new-password2", "new-password2");
            Assert.assertEquals("Your password has been updated.", accountUpdateProfilePage.getSuccess());
            // Logout
            driver.navigate().to("http://localhost:8081/test-app/logout");
        } finally {
            // remove dummy federation provider for this realm
            realm = getRealm();
            realm.removeComponent(dummyModel);
            AbstractIdentityProviderTest.brokerServerRule.stopSession(session, true);
            session = AbstractIdentityProviderTest.brokerServerRule.startSession();
        }
    }
}

