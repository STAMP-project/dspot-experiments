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
package org.keycloak.testsuite.admin;


import AdminRoles.VIEW_USERS;
import ImpersonationConstants.IMPERSONATION_ROLE;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.Config;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.ClientBuilder;
import org.keycloak.testsuite.util.CredentialBuilder;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.UserBuilder;


/**
 * Tests Undertow Adapter
 *
 * @author <a href="mailto:bburke@redhat.com">Bill Burke</a>
 */
public class ImpersonationTest extends AbstractKeycloakTest {
    static class UserSessionNotesHolder {
        private Map<String, String> notes = new HashMap<>();

        public UserSessionNotesHolder() {
        }

        public UserSessionNotesHolder(final Map<String, String> notes) {
            this.notes = notes;
        }

        public void setNotes(final Map<String, String> notes) {
            this.notes = notes;
        }

        public Map<String, String> getNotes() {
            return notes;
        }
    }

    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    private String impersonatedUserId;

    @Test
    public void testImpersonateByMasterAdmin() {
        // test that composite is set up right for impersonation role
        testSuccessfulImpersonation("admin", Config.getAdminRealm());
    }

    @Test
    public void testImpersonateByMasterImpersonator() {
        String userId;
        try (Response response = adminClient.realm("master").users().create(UserBuilder.create().username("master-impersonator").build())) {
            userId = ApiUtil.getCreatedId(response);
        }
        UserResource user = adminClient.realm("master").users().get(userId);
        user.resetPassword(CredentialBuilder.create().password("password").build());
        ClientResource testRealmClient = ApiUtil.findClientResourceByClientId(adminClient.realm("master"), "test-realm");
        List<RoleRepresentation> roles = new LinkedList<>();
        roles.add(ApiUtil.findClientRoleByName(testRealmClient, VIEW_USERS).toRepresentation());
        roles.add(ApiUtil.findClientRoleByName(testRealmClient, IMPERSONATION_ROLE).toRepresentation());
        user.roles().clientLevel(testRealmClient.toRepresentation().getId()).add(roles);
        testSuccessfulImpersonation("master-impersonator", Config.getAdminRealm());
        adminClient.realm("master").users().get(userId).remove();
    }

    @Test
    public void testImpersonateByTestImpersonator() {
        testSuccessfulImpersonation("impersonator", "test");
    }

    @Test
    public void testImpersonateByTestAdmin() {
        // test that composite is set up right for impersonation role
        testSuccessfulImpersonation("realm-admin", "test");
    }

    @Test
    public void testImpersonateByTestBadImpersonator() {
        testForbiddenImpersonation("bad-impersonator", "test");
    }

    @Test
    public void testImpersonateByMastertBadImpersonator() {
        String userId;
        try (Response response = adminClient.realm("master").users().create(UserBuilder.create().username("master-bad-impersonator").build())) {
            userId = ApiUtil.getCreatedId(response);
        }
        adminClient.realm("master").users().get(userId).resetPassword(CredentialBuilder.create().password("password").build());
        testForbiddenImpersonation("master-bad-impersonator", Config.getAdminRealm());
        adminClient.realm("master").users().get(userId).remove();
    }

    // KEYCLOAK-5981
    @Test
    public void testImpersonationWorksWhenAuthenticationSessionExists() throws Exception {
        // Create test client
        RealmResource realm = adminClient.realms().realm("test");
        Response resp = realm.clients().create(ClientBuilder.create().clientId("test-app").addRedirectUri(((OAuthClient.APP_ROOT) + "/*")).build());
        resp.close();
        // Open the URL for the client (will redirect to Keycloak server AuthorizationEndpoint and create authenticationSession)
        String loginFormUrl = oauth.getLoginFormUrl();
        driver.navigate().to(loginFormUrl);
        loginPage.assertCurrent();
        // Impersonate and get SSO cookie. Setup that cookie for webDriver
        driver.manage().addCookie(testSuccessfulImpersonation("realm-admin", "test"));
        // Open the URL again - should be directly redirected to the app due the SSO login
        driver.navigate().to(loginFormUrl);
        appPage.assertCurrent();
        // Remove test client
        ApiUtil.findClientByClientId(realm, "test-app").remove();
    }
}

