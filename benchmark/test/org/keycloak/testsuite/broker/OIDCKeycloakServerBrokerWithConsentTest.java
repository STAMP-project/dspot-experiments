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


import IdentityProviderRepresentation.UPFLM_OFF;
import java.util.List;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.common.util.Time;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.testsuite.KeycloakServer;
import org.keycloak.testsuite.rule.AbstractKeycloakRule;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OIDCKeycloakServerBrokerWithConsentTest extends AbstractIdentityProviderTest {
    private static final int PORT = 8082;

    private static Keycloak keycloak1;

    private static Keycloak keycloak2;

    @ClassRule
    public static AbstractKeycloakRule oidcServerRule = new AbstractKeycloakRule() {
        @Override
        protected void configureServer(KeycloakServer server) {
            server.getConfig().setPort(OIDCKeycloakServerBrokerWithConsentTest.PORT);
        }

        @Override
        protected void configure(KeycloakSession session, RealmManager manager, RealmModel adminRealm) {
            server.importRealm(getClass().getResourceAsStream("/broker-test/test-broker-realm-with-kc-oidc.json"));
            // Disable update profile
            RealmModel realm = AbstractIdentityProviderTest.getRealm(session);
            AbstractIdentityProviderTest.setUpdateProfileFirstLogin(realm, UPFLM_OFF);
        }

        @Override
        protected String[] getTestRealms() {
            return new String[]{ "realm-with-oidc-identity-provider" };
        }
    };

    // KEYCLOAK-2769
    @Test
    public void testConsentDeniedWithExpiredClientSession() throws Exception {
        // Login to broker
        loginIDP("test-user");
        // Set time offset
        Time.setOffset(60);
        try {
            // User rejected consent
            grantPage.assertCurrent();
            grantPage.cancel();
            // Assert login page with "You took too long to login..." message
            Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/auth/realms/realm-with-broker/login-actions/authenticate"));
            Assert.assertEquals("You took too long to login. Login process starting from beginning.", loginPage.getError());
        } finally {
            Time.setOffset(0);
        }
    }

    // KEYCLOAK-2769
    @Test
    public void testConsentDeniedWithExpiredAndClearedClientSession() throws Exception {
        // Login to broker again
        loginIDP("test-user");
        // Set time offset
        Time.setOffset(60);
        try {
            // Manually remove expiredSessions TODO: Will require custom endpoint when migrate to integration-arquillian
            AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
            this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
            session.sessions().removeExpired(getRealm());
            session.authenticationSessions().removeExpired(getRealm());
            AbstractIdentityProviderTest.brokerServerRule.stopSession(this.session, true);
            this.session = AbstractIdentityProviderTest.brokerServerRule.startSession();
            // User rejected consent
            grantPage.assertCurrent();
            grantPage.cancel();
            // Assert login page with "You took too long to login..." message
            Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/auth/realms/realm-with-broker/login-actions/authenticate"));
            Assert.assertEquals("You took too long to login. Login process starting from beginning.", loginPage.getError());
        } finally {
            Time.setOffset(0);
        }
    }

    // KEYCLOAK-2801
    @Test
    public void testAccountManagementLinkingAndExpiredClientSession() throws Exception {
        // Login as pedroigor to account management
        loginToAccountManagement("pedroigor");
        // Link my "pedroigor" identity with "test-user" from brokered Keycloak
        accountFederatedIdentityPage.clickAddProvider(getProviderId());
        Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8082/auth/"));
        this.loginPage.login("test-user", "password");
        // Set time offset
        Time.setOffset(60);
        try {
            // User rejected consent
            grantPage.assertCurrent();
            grantPage.cancel();
            // Assert account error page with "staleCodeAccount" error displayed
            accountFederatedIdentityPage.assertCurrent();
            Assert.assertEquals("The page expired. Please try one more time.", accountFederatedIdentityPage.getError());
            // Try to link one more time
            accountFederatedIdentityPage.clickAddProvider(getProviderId());
            Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8082/auth/"));
            this.loginPage.login("test-user", "password");
            Time.setOffset(120);
            // User granted consent
            grantPage.assertCurrent();
            grantPage.accept();
            // Assert account error page with "staleCodeAccount" error displayed
            accountFederatedIdentityPage.assertCurrent();
            Assert.assertEquals("The page expired. Please try one more time.", accountFederatedIdentityPage.getError());
        } finally {
            Time.setOffset(0);
        }
        // Revoke consent
        RealmResource brokeredRealm = OIDCKeycloakServerBrokerWithConsentTest.keycloak2.realm("realm-with-oidc-identity-provider");
        List<UserRepresentation> users = brokeredRealm.users().search("test-user", 0, 1);
        brokeredRealm.users().get(users.get(0).getId()).revokeConsent("broker-app");
    }

    @Test
    public void testLoginCancelConsent() throws Exception {
        // Try to login
        loginIDP("test-user");
        // User rejected consent
        grantPage.assertCurrent();
        grantPage.cancel();
        // Assert back on login page
        Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8081/auth/"));
        Assert.assertTrue(driver.getTitle().equals("Log in to realm-with-broker"));
    }

    // KEYCLOAK-2802
    @Test
    public void testAccountManagementLinkingCancelConsent() throws Exception {
        // Login as pedroigor to account management
        loginToAccountManagement("pedroigor");
        // Link my "pedroigor" identity with "test-user" from brokered Keycloak
        accountFederatedIdentityPage.clickAddProvider(getProviderId());
        Assert.assertTrue(this.driver.getCurrentUrl().startsWith("http://localhost:8082/auth/"));
        this.loginPage.login("test-user", "password");
        // User rejected consent
        grantPage.assertCurrent();
        grantPage.cancel();
        // Assert account error page with "consentDenied" error displayed
        accountFederatedIdentityPage.assertCurrent();
        Assert.assertEquals("Consent denied.", accountFederatedIdentityPage.getError());
    }
}

