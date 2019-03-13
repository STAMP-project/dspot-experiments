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


import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.models.Constants.ADMIN_CLI_CLIENT_ID;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.services.managers.RealmManager;
import org.keycloak.testsuite.KeycloakServer;
import org.keycloak.testsuite.pages.AccountApplicationsPage;
import org.keycloak.testsuite.rule.AbstractKeycloakRule;
import org.keycloak.testsuite.rule.WebResource;


/**
 *
 *
 * @author pedroigor
 */
public class OIDCKeyCloakServerBrokerBasicTest extends AbstractKeycloakIdentityProviderTest {
    private static final int PORT = 8082;

    @ClassRule
    public static AbstractKeycloakRule samlServerRule = new AbstractKeycloakRule() {
        @Override
        protected void configureServer(KeycloakServer server) {
            server.getConfig().setPort(OIDCKeyCloakServerBrokerBasicTest.PORT);
        }

        @Override
        protected void configure(KeycloakSession session, RealmManager manager, RealmModel adminRealm) {
            server.importRealm(getClass().getResourceAsStream("/broker-test/test-broker-realm-with-kc-oidc.json"));
        }

        @Override
        protected String[] getTestRealms() {
            return new String[]{ "realm-with-oidc-identity-provider" };
        }
    };

    @WebResource
    protected AccountApplicationsPage accountApplicationsPage;

    @Test
    public void testDisabledUser() throws Exception {
        super.testDisabledUser();
    }

    @Test
    public void testLogoutWorksWithTokenTimeout() {
        try (Keycloak keycloak = Keycloak.getInstance("http://localhost:8081/auth", "master", "admin", "admin", ADMIN_CLI_CLIENT_ID)) {
            RealmRepresentation realm = keycloak.realm("realm-with-oidc-identity-provider").toRepresentation();
            Assert.assertNotNull(realm);
            int oldLifespan = realm.getAccessTokenLifespan();
            realm.setAccessTokenLifespan(1);
            keycloak.realm("realm-with-oidc-identity-provider").update(realm);
            IdentityProviderRepresentation idp = keycloak.realm("realm-with-broker").identityProviders().get("kc-oidc-idp").toRepresentation();
            idp.getConfig().put("backchannelSupported", "false");
            keycloak.realm("realm-with-broker").identityProviders().get("kc-oidc-idp").update(idp);
            logoutTimeOffset = 2;
            super.testSuccessfulAuthentication();
            logoutTimeOffset = 0;
            realm.setAccessTokenLifespan(oldLifespan);
            keycloak.realm("realm-with-oidc-identity-provider").update(realm);
            idp.getConfig().put("backchannelSupported", "true");
            keycloak.realm("realm-with-broker").identityProviders().get("kc-oidc-idp").update(idp);
        }
    }

    @Test
    public void testSuccessfulAuthenticationWithoutUpdateProfile() {
        super.testSuccessfulAuthenticationWithoutUpdateProfile();
    }

    @Test
    public void testSuccessfulAuthenticationWithoutUpdateProfile_emailNotProvided_emailVerifyEnabled() throws Exception {
        super.testSuccessfulAuthenticationWithoutUpdateProfile_emailNotProvided_emailVerifyEnabled();
    }

    @Test
    public void testSuccessfulAuthenticationWithoutUpdateProfile_newUser_emailAsUsername() throws Exception {
        super.testSuccessfulAuthenticationWithoutUpdateProfile_newUser_emailAsUsername();
    }

    @Test
    public void testTokenStorageAndRetrievalByApplication() {
        super.testTokenStorageAndRetrievalByApplication();
    }

    @Test
    public void testAccountManagementLinkIdentity() {
        super.testAccountManagementLinkIdentity();
    }

    @Test
    public void testWithLinkedFederationProvider() throws Exception {
        super.testWithLinkedFederationProvider();
    }

    @Test
    public void testAccountManagementLinkedIdentityAlreadyExists() {
        super.testAccountManagementLinkedIdentityAlreadyExists();
    }
}

