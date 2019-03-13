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
package org.keycloak.testsuite.composites;


import OAuth2Constants.CODE;
import java.util.Collections;
import java.util.List;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.OAuthClient.AccessTokenResponse;

import static org.keycloak.testsuite.Assert.assertNames;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class CompositeRoleTest extends AbstractCompositeKeycloakTest {
    @Page
    protected LoginPage loginPage;

    @Test
    public void testAppCompositeUser() throws Exception {
        oauth.realm("test");
        oauth.clientId("APP_COMPOSITE_APPLICATION");
        oauth.doLogin("APP_COMPOSITE_USER", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        org.keycloak.testsuite.Assert.assertEquals(200, response.getStatusCode());
        org.keycloak.testsuite.Assert.assertEquals("bearer", response.getTokenType());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        org.keycloak.testsuite.Assert.assertEquals(getUserId("APP_COMPOSITE_USER"), token.getSubject());
        org.keycloak.testsuite.Assert.assertEquals(1, token.getResourceAccess("APP_ROLE_APPLICATION").getRoles().size());
        org.keycloak.testsuite.Assert.assertEquals(1, token.getRealmAccess().getRoles().size());
        org.keycloak.testsuite.Assert.assertTrue(token.getResourceAccess("APP_ROLE_APPLICATION").isUserInRole("APP_ROLE_1"));
        org.keycloak.testsuite.Assert.assertTrue(token.getRealmAccess().isUserInRole("REALM_ROLE_1"));
        AccessTokenResponse refreshResponse = oauth.doRefreshTokenRequest(response.getRefreshToken(), "password");
        org.keycloak.testsuite.Assert.assertEquals(200, refreshResponse.getStatusCode());
    }

    @Test
    public void testRealmAppCompositeUser() throws Exception {
        oauth.realm("test");
        oauth.clientId("APP_ROLE_APPLICATION");
        oauth.doLogin("REALM_APP_COMPOSITE_USER", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        org.keycloak.testsuite.Assert.assertEquals(200, response.getStatusCode());
        org.keycloak.testsuite.Assert.assertEquals("bearer", response.getTokenType());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        org.keycloak.testsuite.Assert.assertEquals(getUserId("REALM_APP_COMPOSITE_USER"), token.getSubject());
        org.keycloak.testsuite.Assert.assertEquals(1, token.getResourceAccess("APP_ROLE_APPLICATION").getRoles().size());
        org.keycloak.testsuite.Assert.assertTrue(token.getResourceAccess("APP_ROLE_APPLICATION").isUserInRole("APP_ROLE_1"));
        AccessTokenResponse refreshResponse = oauth.doRefreshTokenRequest(response.getRefreshToken(), "password");
        org.keycloak.testsuite.Assert.assertEquals(200, refreshResponse.getStatusCode());
    }

    @Test
    public void testRealmOnlyWithUserCompositeAppComposite() throws Exception {
        oauth.realm("test");
        oauth.clientId("REALM_COMPOSITE_1_APPLICATION");
        oauth.doLogin("REALM_COMPOSITE_1_USER", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        org.keycloak.testsuite.Assert.assertEquals(200, response.getStatusCode());
        org.keycloak.testsuite.Assert.assertEquals("bearer", response.getTokenType());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        org.keycloak.testsuite.Assert.assertEquals(getUserId("REALM_COMPOSITE_1_USER"), token.getSubject());
        org.keycloak.testsuite.Assert.assertEquals(2, token.getRealmAccess().getRoles().size());
        org.keycloak.testsuite.Assert.assertTrue(token.getRealmAccess().isUserInRole("REALM_COMPOSITE_1"));
        org.keycloak.testsuite.Assert.assertTrue(token.getRealmAccess().isUserInRole("REALM_ROLE_1"));
        AccessTokenResponse refreshResponse = oauth.doRefreshTokenRequest(response.getRefreshToken(), "password");
        org.keycloak.testsuite.Assert.assertEquals(200, refreshResponse.getStatusCode());
    }

    @Test
    public void testRealmOnlyWithUserCompositeAppRole() throws Exception {
        oauth.realm("test");
        oauth.clientId("REALM_ROLE_1_APPLICATION");
        oauth.doLogin("REALM_COMPOSITE_1_USER", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        org.keycloak.testsuite.Assert.assertEquals(200, response.getStatusCode());
        org.keycloak.testsuite.Assert.assertEquals("bearer", response.getTokenType());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        org.keycloak.testsuite.Assert.assertEquals(getUserId("REALM_COMPOSITE_1_USER"), token.getSubject());
        org.keycloak.testsuite.Assert.assertEquals(1, token.getRealmAccess().getRoles().size());
        org.keycloak.testsuite.Assert.assertTrue(token.getRealmAccess().isUserInRole("REALM_ROLE_1"));
        AccessTokenResponse refreshResponse = oauth.doRefreshTokenRequest(response.getRefreshToken(), "password");
        org.keycloak.testsuite.Assert.assertEquals(200, refreshResponse.getStatusCode());
    }

    @Test
    public void testRealmOnlyWithUserRoleAppComposite() throws Exception {
        oauth.realm("test");
        oauth.clientId("REALM_COMPOSITE_1_APPLICATION");
        oauth.doLogin("REALM_ROLE_1_USER", "password");
        String code = oauth.getCurrentQuery().get(CODE);
        AccessTokenResponse response = oauth.doAccessTokenRequest(code, "password");
        org.keycloak.testsuite.Assert.assertEquals(200, response.getStatusCode());
        org.keycloak.testsuite.Assert.assertEquals("bearer", response.getTokenType());
        AccessToken token = oauth.verifyToken(response.getAccessToken());
        org.keycloak.testsuite.Assert.assertEquals(getUserId("REALM_ROLE_1_USER"), token.getSubject());
        org.keycloak.testsuite.Assert.assertEquals(1, token.getRealmAccess().getRoles().size());
        org.keycloak.testsuite.Assert.assertTrue(token.getRealmAccess().isUserInRole("REALM_ROLE_1"));
        AccessTokenResponse refreshResponse = oauth.doRefreshTokenRequest(response.getRefreshToken(), "password");
        org.keycloak.testsuite.Assert.assertEquals(200, refreshResponse.getStatusCode());
    }

    // KEYCLOAK-4274
    @Test
    public void testRecursiveComposites() throws Exception {
        // This will create recursive composite mappings between "REALM_COMPOSITE_1" and "REALM_ROLE_1"
        RoleRepresentation realmComposite1 = testRealm().roles().get("REALM_COMPOSITE_1").toRepresentation();
        testRealm().roles().get("REALM_ROLE_1").addComposites(Collections.singletonList(realmComposite1));
        UserResource userResource = ApiUtil.findUserByUsernameId(testRealm(), "REALM_COMPOSITE_1_USER");
        List<RoleRepresentation> realmRoles = userResource.roles().realmLevel().listEffective();
        assertNames(realmRoles, "REALM_COMPOSITE_1", "REALM_ROLE_1");
        userResource = ApiUtil.findUserByUsernameId(testRealm(), "REALM_ROLE_1_USER");
        realmRoles = userResource.roles().realmLevel().listEffective();
        assertNames(realmRoles, "REALM_COMPOSITE_1", "REALM_ROLE_1");
        // Revert
        testRealm().roles().get("REALM_ROLE_1").deleteComposites(Collections.singletonList(realmComposite1));
    }
}

