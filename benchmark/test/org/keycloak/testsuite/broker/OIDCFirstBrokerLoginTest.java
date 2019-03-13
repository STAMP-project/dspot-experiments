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


import AuthenticationExecutionModel.Requirement.ALTERNATIVE;
import AuthenticationExecutionModel.Requirement.DISABLED;
import DefaultAuthenticationFlows.FIRST_BROKER_LOGIN_HANDLE_EXISTING_SUBFLOW;
import IdentityProviderRepresentation.UPFLM_OFF;
import IdpEmailVerificationAuthenticatorFactory.PROVIDER_ID;
import java.util.Set;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.keycloak.models.AuthenticationExecutionModel;
import org.keycloak.models.AuthenticationFlowModel;
import org.keycloak.models.FederatedIdentityModel;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserModel;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.testsuite.KeycloakServer;
import org.keycloak.testsuite.rule.AbstractKeycloakRule;
import org.keycloak.testsuite.rule.KeycloakRule;
import org.openqa.selenium.NoSuchElementException;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OIDCFirstBrokerLoginTest extends AbstractFirstBrokerLoginTest {
    private static final int PORT = 8082;

    @ClassRule
    public static AbstractKeycloakRule samlServerRule = new AbstractKeycloakRule() {
        @Override
        protected void configureServer(KeycloakServer server) {
            server.getConfig().setPort(OIDCFirstBrokerLoginTest.PORT);
        }

        @Override
        protected void configure(KeycloakSession session, RealmManager manager, RealmModel adminRealm) {
            server.importRealm(getClass().getResourceAsStream("/broker-test/test-broker-realm-with-kc-oidc.json"));
            server.importRealm(getClass().getResourceAsStream("/broker-test/test-broker-realm-with-saml.json"));
        }

        @Override
        protected String[] getTestRealms() {
            return new String[]{ "realm-with-oidc-identity-provider", "realm-with-saml-idp-basic" };
        }
    };

    /**
     * Tests that duplication is detected and user wants to link federatedIdentity with existing account. He will confirm link by reauthentication
     * with different broker already linked to his account
     */
    @Test
    public void testLinkAccountByReauthenticationWithDifferentBroker() throws Exception {
        AbstractIdentityProviderTest.brokerServerRule.update(new KeycloakRule.KeycloakSetup() {
            @Override
            public void config(RealmManager manager, RealmModel adminstrationRealm, RealmModel realmWithBroker) {
                AbstractFirstBrokerLoginTest.setExecutionRequirement(realmWithBroker, FIRST_BROKER_LOGIN_HANDLE_EXISTING_SUBFLOW, PROVIDER_ID, DISABLED);
                AbstractIdentityProviderTest.setUpdateProfileFirstLogin(realmWithBroker, UPFLM_OFF);
            }
        }, AbstractFirstBrokerLoginTest.APP_REALM_ID);
        // First link "pedroigor" user with SAML broker and logout
        linkUserWithSamlBroker("pedroigor", "psilva@redhat.com");
        // login through OIDC broker now
        loginIDP("pedroigor");
        this.idpConfirmLinkPage.assertCurrent();
        Assert.assertEquals("User with email psilva@redhat.com already exists. How do you want to continue?", this.idpConfirmLinkPage.getMessage());
        this.idpConfirmLinkPage.clickLinkAccount();
        // assert reauthentication with login page. On login page is link to kc-saml-idp-basic as user has it linked already
        Assert.assertEquals(("Log in to " + (AbstractFirstBrokerLoginTest.APP_REALM_ID)), this.driver.getTitle());
        Assert.assertEquals(("Authenticate as pedroigor to link your account with " + (getProviderId())), this.loginPage.getInfoMessage());
        try {
            this.loginPage.findSocialButton(getProviderId());
            Assert.fail(("Not expected to see social button with " + (getProviderId())));
        } catch (NoSuchElementException expected) {
        }
        // reauthenticate with SAML broker
        this.loginPage.clickSocial("kc-saml-idp-basic");
        Assert.assertEquals("Log in to realm-with-saml-idp-basic", this.driver.getTitle());
        this.loginPage.login("pedroigor", "password");
        // authenticated and redirected to app. User is linked with identity provider
        Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/test-app"));
        UserModel federatedUser = getFederatedUser();
        Assert.assertNotNull(federatedUser);
        Assert.assertEquals("pedroigor", federatedUser.getUsername());
        Assert.assertEquals("psilva@redhat.com", federatedUser.getEmail());
        RealmModel realmWithBroker = getRealm();
        Set<FederatedIdentityModel> federatedIdentities = this.session.users().getFederatedIdentities(federatedUser, realmWithBroker);
        Assert.assertEquals(2, federatedIdentities.size());
        for (FederatedIdentityModel link : federatedIdentities) {
            Assert.assertEquals("pedroigor", link.getUserName());
            Assert.assertTrue(((link.getIdentityProvider().equals(getProviderId())) || (link.getIdentityProvider().equals("kc-saml-idp-basic"))));
        }
        AbstractIdentityProviderTest.brokerServerRule.update(new KeycloakRule.KeycloakSetup() {
            @Override
            public void config(RealmManager manager, RealmModel adminstrationRealm, RealmModel realmWithBroker) {
                AbstractFirstBrokerLoginTest.setExecutionRequirement(realmWithBroker, FIRST_BROKER_LOGIN_HANDLE_EXISTING_SUBFLOW, PROVIDER_ID, ALTERNATIVE);
            }
        }, AbstractFirstBrokerLoginTest.APP_REALM_ID);
    }

    /**
     * Tests that user can link federated identity with existing brokered
     * account without prompt (KEYCLOAK-7270).
     */
    @Test
    public void testAutoLinkAccountWithBroker() throws Exception {
        final String originalFirstBrokerLoginFlowId = getRealm().getIdentityProviderByAlias(getProviderId()).getFirstBrokerLoginFlowId();
        AbstractIdentityProviderTest.brokerServerRule.update(new KeycloakRule.KeycloakSetup() {
            @Override
            public void config(RealmManager manager, RealmModel adminstrationRealm, RealmModel appRealm) {
                AuthenticationFlowModel newFlow = new AuthenticationFlowModel();
                newFlow.setAlias("AutoLink");
                newFlow.setDescription("AutoLink");
                newFlow.setProviderId("basic-flow");
                newFlow.setBuiltIn(false);
                newFlow.setTopLevel(true);
                newFlow = appRealm.addAuthenticationFlow(newFlow);
                AuthenticationExecutionModel execution = new AuthenticationExecutionModel();
                execution.setRequirement(ALTERNATIVE);
                execution.setAuthenticatorFlow(false);
                execution.setAuthenticator("idp-create-user-if-unique");
                execution.setPriority(1);
                execution.setParentFlow(newFlow.getId());
                execution = appRealm.addAuthenticatorExecution(execution);
                AuthenticationExecutionModel execution2 = new AuthenticationExecutionModel();
                execution2.setRequirement(ALTERNATIVE);
                execution2.setAuthenticatorFlow(false);
                execution2.setAuthenticator("idp-auto-link");
                execution2.setPriority(2);
                execution2.setParentFlow(newFlow.getId());
                execution2 = appRealm.addAuthenticatorExecution(execution2);
                IdentityProviderModel idp = appRealm.getIdentityProviderByAlias(getProviderId());
                idp.setFirstBrokerLoginFlowId(newFlow.getId());
                appRealm.updateIdentityProvider(idp);
            }
        }, AbstractFirstBrokerLoginTest.APP_REALM_ID);
        // login through OIDC broker
        loginIDP("pedroigor");
        // authenticated and redirected to app. User is linked with identity provider
        Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/test-app"));
        UserModel federatedUser = getFederatedUser();
        Assert.assertNotNull(federatedUser);
        Assert.assertEquals("pedroigor", federatedUser.getUsername());
        Assert.assertEquals("psilva@redhat.com", federatedUser.getEmail());
        RealmModel realmWithBroker = getRealm();
        Set<FederatedIdentityModel> federatedIdentities = this.session.users().getFederatedIdentities(federatedUser, realmWithBroker);
        Assert.assertEquals(1, federatedIdentities.size());
        for (FederatedIdentityModel link : federatedIdentities) {
            Assert.assertEquals("pedroigor", link.getUserName());
            Assert.assertTrue(link.getIdentityProvider().equals(getProviderId()));
        }
        AbstractIdentityProviderTest.brokerServerRule.update(new KeycloakRule.KeycloakSetup() {
            @Override
            public void config(RealmManager manager, RealmModel adminstrationRealm, RealmModel appRealm) {
                IdentityProviderModel idp = appRealm.getIdentityProviderByAlias(getProviderId());
                idp.setFirstBrokerLoginFlowId(originalFirstBrokerLoginFlowId);
                appRealm.updateIdentityProvider(idp);
            }
        }, AbstractFirstBrokerLoginTest.APP_REALM_ID);
    }

    // KEYCLOAK-5936
    @Test
    public void testMoreIdpAndBackButtonWhenLinkingAccount() throws Exception {
        AbstractIdentityProviderTest.brokerServerRule.update(new KeycloakRule.KeycloakSetup() {
            @Override
            public void config(RealmManager manager, RealmModel adminstrationRealm, RealmModel realmWithBroker) {
                AbstractFirstBrokerLoginTest.setExecutionRequirement(realmWithBroker, FIRST_BROKER_LOGIN_HANDLE_EXISTING_SUBFLOW, PROVIDER_ID, DISABLED);
                // setUpdateProfileFirstLogin(realmWithBroker, IdentityProviderRepresentation.UPFLM_ON);
            }
        }, AbstractFirstBrokerLoginTest.APP_REALM_ID);
        // First link user with SAML broker and logout
        linkUserWithSamlBroker("pedroigor", "psilva@redhat.com");
        // Try to login through OIDC broker now
        loginIDP("pedroigor");
        this.updateProfilePage.assertCurrent();
        // User doesn't want to continue linking account. He rather wants to revert and try the other broker. Cick browser "back" 2 times now
        driver.navigate().back();
        loginExpiredPage.assertCurrent();
        driver.navigate().back();
        // I am back on the base login screen. Click login with SAML now and login with SAML broker instead
        Assert.assertEquals("Log in to realm-with-broker", driver.getTitle());
        this.loginPage.clickSocial("kc-saml-idp-basic");
        // Login inside SAML broker
        this.loginPage.login("pedroigor", "password");
        // Assert logged successfully
        Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/test-app"));
        UserModel federatedUser = getFederatedUser();
        Assert.assertNotNull(federatedUser);
        Assert.assertEquals("pedroigor", federatedUser.getUsername());
        // Logout
        driver.navigate().to("http://localhost:8081/test-app/logout");
        AbstractIdentityProviderTest.brokerServerRule.update(new KeycloakRule.KeycloakSetup() {
            @Override
            public void config(RealmManager manager, RealmModel adminstrationRealm, RealmModel realmWithBroker) {
                AbstractFirstBrokerLoginTest.setExecutionRequirement(realmWithBroker, FIRST_BROKER_LOGIN_HANDLE_EXISTING_SUBFLOW, PROVIDER_ID, ALTERNATIVE);
            }
        }, AbstractFirstBrokerLoginTest.APP_REALM_ID);
    }
}

