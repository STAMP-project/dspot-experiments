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


import AddressClaimSet.COUNTRY;
import AddressClaimSet.POSTAL_CODE;
import AddressClaimSet.REGION;
import OAuthClient.AccessTokenResponse;
import OIDCAttributeMapperHelper.TOKEN_CLAIM_NAME;
import OIDCLoginProtocolFactory.CLIENT_ROLES;
import OIDCLoginProtocolFactory.REALM_ROLES;
import OIDCLoginProtocolFactory.ROLES_SCOPE;
import OIDCLoginProtocolFactory.WEB_ORIGINS_SCOPE;
import Response.Status.Family.CLIENT_ERROR;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ClientScopeResource;
import org.keycloak.admin.client.resource.ProtocolMappersResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.common.util.UriUtils;
import org.keycloak.protocol.oidc.mappers.AddressMapper;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.ProtocolMapperUtil;

import static org.keycloak.testsuite.Assert.assertNames;
import static org.keycloak.testsuite.util.ClientManager.realm;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class OIDCProtocolMappersTest extends AbstractKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Test
    public void testTokenMapping() throws Exception {
        {
            UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
            UserRepresentation user = userResource.toRepresentation();
            user.singleAttribute("street", "5 Yawkey Way");
            user.singleAttribute("locality", "Boston");
            user.singleAttribute("region_some", "MA");// Custom name for userAttribute name, which will be mapped to region

            user.singleAttribute("postal_code", "02115");
            user.singleAttribute("country", "USA");
            user.singleAttribute("formatted", "6 Foo Street");
            user.singleAttribute("phone", "617-777-6666");
            List<String> departments = Arrays.asList("finance", "development");
            user.getAttributes().put("departments", departments);
            userResource.update(user);
            ClientResource app = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app");
            ProtocolMapperRepresentation mapper = ProtocolMapperUtil.createAddressMapper(true, true, true);
            mapper.getConfig().put(AddressMapper.getModelPropertyName(REGION), "region_some");
            mapper.getConfig().put(AddressMapper.getModelPropertyName(COUNTRY), "country_some");
            mapper.getConfig().remove(AddressMapper.getModelPropertyName(POSTAL_CODE));// Even if we remove protocolMapper config property, it should still default to postal_code

            app.getProtocolMappers().createMapper(mapper).close();
            ProtocolMapperRepresentation hard = ProtocolMapperUtil.createHardcodedClaim("hard", "hard", "coded", "String", true, true);
            app.getProtocolMappers().createMapper(hard).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createHardcodedClaim("hard-nested", "nested.hard", "coded-nested", "String", true, true)).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createClaimMapper("custom phone", "phone", "home_phone", "String", true, true, true)).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createClaimMapper("nested phone", "phone", "home.phone", "String", true, true, true)).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createClaimMapper("dotted phone", "phone", "home\\.phone", "String", true, true, true)).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createClaimMapper("departments", "departments", "department", "String", true, true, true)).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createClaimMapper("firstDepartment", "departments", "firstDepartment", "String", true, true, false)).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createHardcodedRole("hard-realm", "hardcoded")).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createHardcodedRole("hard-app", "app.hardcoded")).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createRoleNameMapper("rename-app-role", "test-app.customer-user", "realm-user")).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createScriptMapper("test-script-mapper1", "computed-via-script", "computed-via-script", "String", true, true, "'hello_' + user.username", false)).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createScriptMapper("test-script-mapper2", "multiValued-via-script", "multiValued-via-script", "String", true, true, "new java.util.ArrayList(['A','B'])", true)).close();
            Response response = app.getProtocolMappers().createMapper(ProtocolMapperUtil.createScriptMapper("test-script-mapper3", "syntax-error-script", "syntax-error-script", "String", true, true, "func_tion foo(){ return 'fail';} foo()", false));
            Assert.assertThat(response.getStatusInfo().getFamily(), Matchers.is(CLIENT_ERROR));
            response.close();
        }
        {
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNotNull(idToken.getAddress());
            Assert.assertEquals(idToken.getName(), "Tom Brady");
            Assert.assertEquals(idToken.getAddress().getStreetAddress(), "5 Yawkey Way");
            Assert.assertEquals(idToken.getAddress().getLocality(), "Boston");
            Assert.assertEquals(idToken.getAddress().getRegion(), "MA");
            Assert.assertEquals(idToken.getAddress().getPostalCode(), "02115");
            Assert.assertNull(idToken.getAddress().getCountry());// Null because we changed userAttribute name to "country_some", but user contains "country"

            Assert.assertEquals(idToken.getAddress().getFormattedAddress(), "6 Foo Street");
            Assert.assertNotNull(idToken.getOtherClaims().get("home_phone"));
            Assert.assertThat(((List<String>) (idToken.getOtherClaims().get("home_phone"))), Matchers.hasItems("617-777-6666"));
            Assert.assertNotNull(idToken.getOtherClaims().get("home.phone"));
            Assert.assertThat(((List<String>) (idToken.getOtherClaims().get("home.phone"))), Matchers.hasItems("617-777-6666"));
            Assert.assertEquals("coded", idToken.getOtherClaims().get("hard"));
            Map nested = ((Map) (idToken.getOtherClaims().get("nested")));
            Assert.assertEquals("coded-nested", nested.get("hard"));
            nested = ((Map) (idToken.getOtherClaims().get("home")));
            Assert.assertThat(((List<String>) (nested.get("phone"))), Matchers.hasItems("617-777-6666"));
            List<String> departments = ((List<String>) (idToken.getOtherClaims().get("department")));
            Assert.assertThat(departments, Matchers.containsInAnyOrder("finance", "development"));
            Object firstDepartment = idToken.getOtherClaims().get("firstDepartment");
            Assert.assertThat(firstDepartment, Matchers.instanceOf(String.class));
            Assert.assertThat(firstDepartment, Matchers.anyOf(Matchers.is("finance"), Matchers.is("development")));// Has to be the first item

            AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
            Assert.assertEquals(accessToken.getName(), "Tom Brady");
            Assert.assertNotNull(accessToken.getAddress());
            Assert.assertEquals(accessToken.getAddress().getStreetAddress(), "5 Yawkey Way");
            Assert.assertEquals(accessToken.getAddress().getLocality(), "Boston");
            Assert.assertEquals(accessToken.getAddress().getRegion(), "MA");
            Assert.assertEquals(accessToken.getAddress().getPostalCode(), "02115");
            Assert.assertNull(idToken.getAddress().getCountry());// Null because we changed userAttribute name to "country_some", but user contains "country"

            Assert.assertEquals(idToken.getAddress().getFormattedAddress(), "6 Foo Street");
            Assert.assertNotNull(accessToken.getOtherClaims().get("home_phone"));
            Assert.assertThat(((List<String>) (accessToken.getOtherClaims().get("home_phone"))), Matchers.hasItems("617-777-6666"));
            Assert.assertEquals("coded", accessToken.getOtherClaims().get("hard"));
            nested = ((Map) (accessToken.getOtherClaims().get("nested")));
            Assert.assertEquals("coded-nested", nested.get("hard"));
            nested = ((Map) (accessToken.getOtherClaims().get("home")));
            Assert.assertThat(((List<String>) (nested.get("phone"))), Matchers.hasItems("617-777-6666"));
            departments = ((List<String>) (idToken.getOtherClaims().get("department")));
            Assert.assertEquals(2, departments.size());
            Assert.assertTrue(((departments.contains("finance")) && (departments.contains("development"))));
            Assert.assertTrue(accessToken.getRealmAccess().getRoles().contains("hardcoded"));
            Assert.assertTrue(accessToken.getRealmAccess().getRoles().contains("realm-user"));
            org.keycloak.testsuite.Assert.assertNull(accessToken.getResourceAccess("test-app"));
            Assert.assertTrue(accessToken.getResourceAccess("app").getRoles().contains("hardcoded"));
            Assert.assertEquals("hello_test-user@localhost", accessToken.getOtherClaims().get("computed-via-script"));
            Assert.assertEquals(Arrays.asList("A", "B"), accessToken.getOtherClaims().get("multiValued-via-script"));
            // Assert audiences added through AudienceResolve mapper
            org.keycloak.testsuite.Assert.assertThat(accessToken.getAudience(), Matchers.arrayContainingInAnyOrder("app", "account"));
            // Assert allowed origins
            assertNames(accessToken.getAllowedOrigins(), "http://localhost:8180", "https://localhost:8543");
            oauth.openLogout();
        }
        // undo mappers
        {
            ClientResource app = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
            ClientRepresentation clientRepresentation = app.toRepresentation();
            for (ProtocolMapperRepresentation model : clientRepresentation.getProtocolMappers()) {
                if ((((((((((((model.getName().equals("address")) || (model.getName().equals("hard"))) || (model.getName().equals("hard-nested"))) || (model.getName().equals("custom phone"))) || (model.getName().equals("dotted phone"))) || (model.getName().equals("departments"))) || (model.getName().equals("firstDepartment"))) || (model.getName().equals("nested phone"))) || (model.getName().equals("rename-app-role"))) || (model.getName().equals("hard-realm"))) || (model.getName().equals("hard-app"))) || (model.getName().equals("test-script-mapper"))) {
                    app.getProtocolMappers().delete(model.getId());
                }
            }
        }
        events.clear();
        {
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNull(idToken.getAddress());
            Assert.assertNull(idToken.getOtherClaims().get("home_phone"));
            Assert.assertNull(idToken.getOtherClaims().get("hard"));
            Assert.assertNull(idToken.getOtherClaims().get("nested"));
            Assert.assertNull(idToken.getOtherClaims().get("department"));
            oauth.openLogout();
        }
        events.clear();
    }

    @Test
    public void testNullOrEmptyTokenMapping() throws Exception {
        {
            UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
            UserRepresentation user = userResource.toRepresentation();
            user.singleAttribute("empty", "");
            user.singleAttribute("null", null);
            userResource.update(user);
            ClientResource app = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app");
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createClaimMapper("empty", "empty", "empty", "String", true, true, false)).close();
            app.getProtocolMappers().createMapper(ProtocolMapperUtil.createClaimMapper("null", "null", "null", "String", true, true, false)).close();
        }
        {
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Object empty = idToken.getOtherClaims().get("empty");
            Assert.assertThat((empty == null ? null : ((String) (empty))), Matchers.isEmptyOrNullString());
            Object nulll = idToken.getOtherClaims().get("null");
            Assert.assertNull(nulll);
            oauth.verifyToken(response.getAccessToken());
            oauth.openLogout();
        }
        // undo mappers
        {
            ClientResource app = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
            ClientRepresentation clientRepresentation = app.toRepresentation();
            for (ProtocolMapperRepresentation model : clientRepresentation.getProtocolMappers()) {
                if ((model.getName().equals("empty")) || (model.getName().equals("null"))) {
                    app.getProtocolMappers().delete(model.getId());
                }
            }
        }
        events.clear();
        {
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNull(idToken.getAddress());
            Assert.assertNull(idToken.getOtherClaims().get("empty"));
            Assert.assertNull(idToken.getOtherClaims().get("null"));
            oauth.openLogout();
        }
        events.clear();
    }

    @Test
    public void testUserRoleToAttributeMappers() throws Exception {
        // Add mapper for realm roles
        ProtocolMapperRepresentation realmMapper = ProtocolMapperUtil.createUserRealmRoleMappingMapper("pref.", "Realm roles mapper", "roles-custom.realm", true, true);
        ProtocolMapperRepresentation clientMapper = ProtocolMapperUtil.createUserClientRoleMappingMapper("test-app", null, "Client roles mapper", "roles-custom.test-app", true, true);
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(Arrays.asList(realmMapper, clientMapper));
        // Login user
        OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
        IDToken idToken = oauth.verifyIDToken(response.getIdToken());
        // Verify attribute is filled
        Map<String, Object> roleMappings = ((Map<String, Object>) (idToken.getOtherClaims().get("roles-custom")));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.keySet(), Matchers.containsInAnyOrder("realm", "test-app"));
        String realmRoleMappings = ((String) (roleMappings.get("realm")));
        String testAppMappings = ((String) (roleMappings.get("test-app")));
        // from direct assignment in user definition
        // from direct assignment in user definition
        assertRolesString(realmRoleMappings, "pref.user", "pref.offline_access");
        // from direct assignment in user definition
        assertRolesString(testAppMappings, "customer-user");
        // Revert
        deleteMappers(protocolMappers);
    }

    // Test to update protocolMappers to not have roles on the default position (realm_access and resource_access properties)
    @Test
    public void testUserRolesMovedFromAccessTokenProperties() throws Exception {
        RealmResource realm = adminClient.realm("test");
        ClientScopeResource rolesScope = ApiUtil.findClientScopeByName(realm, ROLES_SCOPE);
        // Update builtin protocolMappers to put roles to different position (claim "custom.roles") for both realm and client roles
        ProtocolMapperRepresentation realmRolesMapper = null;
        ProtocolMapperRepresentation clientRolesMapper = null;
        for (ProtocolMapperRepresentation rep : rolesScope.getProtocolMappers().getMappers()) {
            if (REALM_ROLES.equals(rep.getName())) {
                realmRolesMapper = rep;
            } else
                if (CLIENT_ROLES.equals(rep.getName())) {
                    clientRolesMapper = rep;
                }

        }
        String realmRolesTokenClaimOrig = realmRolesMapper.getConfig().get(TOKEN_CLAIM_NAME);
        String clientRolesTokenClaimOrig = clientRolesMapper.getConfig().get(TOKEN_CLAIM_NAME);
        realmRolesMapper.getConfig().put(TOKEN_CLAIM_NAME, "custom.roles");
        rolesScope.getProtocolMappers().update(realmRolesMapper.getId(), realmRolesMapper);
        clientRolesMapper.getConfig().put(TOKEN_CLAIM_NAME, "custom.roles");
        rolesScope.getProtocolMappers().update(clientRolesMapper.getId(), clientRolesMapper);
        // Create some hardcoded role mapper
        Response resp = rolesScope.getProtocolMappers().createMapper(ProtocolMapperUtil.createHardcodedRole("hard-realm", "hardcoded"));
        String hardcodedMapperId = ApiUtil.getCreatedId(resp);
        resp.close();
        try {
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
            // Assert roles are not on their original positions
            org.keycloak.testsuite.Assert.assertNull(accessToken.getRealmAccess());
            org.keycloak.testsuite.Assert.assertTrue(accessToken.getResourceAccess().isEmpty());
            // KEYCLOAK-8481 Assert that accessToken JSON doesn't have "realm_access" or "resource_access" fields in it
            String accessTokenJson = new String(getContent(), StandardCharsets.UTF_8);
            org.keycloak.testsuite.Assert.assertFalse(accessTokenJson.contains("realm_access"));
            org.keycloak.testsuite.Assert.assertFalse(accessTokenJson.contains("resource_access"));
            // Assert both realm and client roles on the new position. Hardcoded role should be here as well
            Map<String, Object> cst1 = ((Map<String, Object>) (accessToken.getOtherClaims().get("custom")));
            List<String> roles = ((List<String>) (cst1.get("roles")));
            assertNames(roles, "offline_access", "user", "customer-user", "hardcoded", AccountRoles.VIEW_PROFILE, AccountRoles.MANAGE_ACCOUNT, AccountRoles.MANAGE_ACCOUNT_LINKS);
            // Assert audience
            assertNames(Arrays.asList(accessToken.getAudience()), "account");
        } finally {
            // Revert
            rolesScope.getProtocolMappers().delete(hardcodedMapperId);
            realmRolesMapper.getConfig().put(TOKEN_CLAIM_NAME, realmRolesTokenClaimOrig);
            rolesScope.getProtocolMappers().update(realmRolesMapper.getId(), realmRolesMapper);
            clientRolesMapper.getConfig().put(TOKEN_CLAIM_NAME, clientRolesTokenClaimOrig);
            rolesScope.getProtocolMappers().update(clientRolesMapper.getId(), clientRolesMapper);
        }
    }

    @Test
    public void testRolesAndAllowedOriginsRemovedFromAccessToken() throws Exception {
        RealmResource realm = adminClient.realm("test");
        ClientScopeRepresentation allowedOriginsScope = ApiUtil.findClientScopeByName(realm, WEB_ORIGINS_SCOPE).toRepresentation();
        ClientScopeRepresentation rolesScope = ApiUtil.findClientScopeByName(realm, ROLES_SCOPE).toRepresentation();
        // Remove 'roles' and 'web-origins' scope from the client
        ClientResource testApp = ApiUtil.findClientByClientId(realm, "test-app");
        testApp.removeDefaultClientScope(allowedOriginsScope.getId());
        testApp.removeDefaultClientScope(rolesScope.getId());
        try {
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            AccessToken accessToken = oauth.verifyToken(response.getAccessToken());
            // Assert web origins are not in the token
            org.keycloak.testsuite.Assert.assertNull(accessToken.getAllowedOrigins());
            // Assert roles are not in the token
            org.keycloak.testsuite.Assert.assertNull(accessToken.getRealmAccess());
            org.keycloak.testsuite.Assert.assertTrue(accessToken.getResourceAccess().isEmpty());
            // Assert client not in the token audience. Just in "issuedFor"
            org.keycloak.testsuite.Assert.assertEquals("test-app", accessToken.getIssuedFor());
            org.keycloak.testsuite.Assert.assertFalse(accessToken.hasAudience("test-app"));
            // Assert IDToken still has "test-app" as an audience
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            org.keycloak.testsuite.Assert.assertEquals("test-app", idToken.getIssuedFor());
            org.keycloak.testsuite.Assert.assertTrue(idToken.hasAudience("test-app"));
        } finally {
            // Revert
            testApp.addDefaultClientScope(allowedOriginsScope.getId());
            testApp.addDefaultClientScope(rolesScope.getId());
        }
    }

    /**
     * KEYCLOAK-4205
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUserRoleToAttributeMappersWithMultiValuedRoles() throws Exception {
        // Add mapper for realm roles
        ProtocolMapperRepresentation realmMapper = ProtocolMapperUtil.createUserRealmRoleMappingMapper("pref.", "Realm roles mapper", "roles-custom.realm", true, true, true);
        ProtocolMapperRepresentation clientMapper = ProtocolMapperUtil.createUserClientRoleMappingMapper("test-app", null, "Client roles mapper", "roles-custom.test-app", true, true, true);
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(Arrays.asList(realmMapper, clientMapper));
        // Login user
        OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
        IDToken idToken = oauth.verifyIDToken(response.getIdToken());
        // Verify attribute is filled
        Map<String, Object> roleMappings = ((Map<String, Object>) (idToken.getOtherClaims().get("roles-custom")));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.keySet(), Matchers.containsInAnyOrder("realm", "test-app"));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.get("realm"), CoreMatchers.instanceOf(List.class));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.get("test-app"), CoreMatchers.instanceOf(List.class));
        List<String> realmRoleMappings = ((List<String>) (roleMappings.get("realm")));
        List<String> testAppMappings = ((List<String>) (roleMappings.get("test-app")));
        // from direct assignment in user definition
        // from direct assignment in user definition
        assertRoles(realmRoleMappings, "pref.user", "pref.offline_access");
        // from direct assignment in user definition
        assertRoles(testAppMappings, "customer-user");
        // Revert
        deleteMappers(protocolMappers);
    }

    /**
     * KEYCLOAK-5259
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUserRoleToAttributeMappersWithFullScopeDisabled() throws Exception {
        // Add mapper for realm roles
        ProtocolMapperRepresentation realmMapper = ProtocolMapperUtil.createUserRealmRoleMappingMapper("pref.", "Realm roles mapper", "roles-custom.realm", true, true, true);
        ProtocolMapperRepresentation clientMapper = ProtocolMapperUtil.createUserClientRoleMappingMapper("test-app", null, "Client roles mapper", "roles-custom.test-app", true, true, true);
        ClientResource client = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app");
        // Disable full-scope-allowed
        ClientRepresentation rep = client.toRepresentation();
        rep.setFullScopeAllowed(false);
        client.update(rep);
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(Arrays.asList(realmMapper, clientMapper));
        // Login user
        OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
        IDToken idToken = oauth.verifyIDToken(response.getIdToken());
        // Verify attribute is filled
        Map<String, Object> roleMappings = ((Map<String, Object>) (idToken.getOtherClaims().get("roles-custom")));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.keySet(), Matchers.containsInAnyOrder("realm", "test-app"));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.get("realm"), CoreMatchers.instanceOf(List.class));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.get("test-app"), CoreMatchers.instanceOf(List.class));
        List<String> realmRoleMappings = ((List<String>) (roleMappings.get("realm")));
        List<String> testAppMappings = ((List<String>) (roleMappings.get("test-app")));
        // from direct assignment in user definition
        assertRoles(realmRoleMappings, "pref.user");
        // from direct assignment in user definition
        assertRoles(testAppMappings, "customer-user");
        // Revert
        deleteMappers(protocolMappers);
        rep = client.toRepresentation();
        rep.setFullScopeAllowed(true);
        client.update(rep);
    }

    // KEYCLOAK-8148 -- Test the scenario where:
    // -- user is member of 2 groups
    // -- both groups have same role "customer-user" assigned
    // -- User login. Role will appear just once in the token (not twice)
    @Test
    public void testRoleMapperWithRoleInheritedFromMoreGroups() throws Exception {
        // Create client-mapper
        String clientId = "test-app";
        ProtocolMapperRepresentation clientMapper = ProtocolMapperUtil.createUserClientRoleMappingMapper(clientId, null, "Client roles mapper", "roles-custom.test-app", true, true);
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), clientId).getProtocolMappers();
        protocolMappers.createMapper(Arrays.asList(clientMapper));
        // Add user 'level2GroupUser' to the group 'level2Group2'
        GroupRepresentation level2Group2 = adminClient.realm("test").getGroupByPath("/topGroup/level2group2");
        UserResource level2GroupUser = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "level2GroupUser");
        level2GroupUser.joinGroup(level2Group2.getId());
        oauth.clientId(clientId);
        OAuthClient.AccessTokenResponse response = browserLogin("password", "level2GroupUser", "password");
        IDToken idToken = oauth.verifyIDToken(response.getIdToken());
        // Verify attribute is filled AND it is filled only once
        Map<String, Object> roleMappings = ((Map<String, Object>) (idToken.getOtherClaims().get("roles-custom")));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.keySet(), Matchers.containsInAnyOrder(clientId));
        String testAppScopeMappings = ((String) (roleMappings.get(clientId)));
        // from assignment to level2group or level2group2. It is filled just once
        assertRolesString(testAppScopeMappings, "customer-user");
        // Revert
        level2GroupUser.leaveGroup(level2Group2.getId());
        deleteMappers(protocolMappers);
    }

    @Test
    public void testUserGroupRoleToAttributeMappers() throws Exception {
        // Add mapper for realm roles
        String clientId = "test-app";
        ProtocolMapperRepresentation realmMapper = ProtocolMapperUtil.createUserRealmRoleMappingMapper("pref.", "Realm roles mapper", "roles-custom.realm", true, true);
        ProtocolMapperRepresentation clientMapper = ProtocolMapperUtil.createUserClientRoleMappingMapper(clientId, "ta.", "Client roles mapper", "roles-custom.test-app", true, true);
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), clientId).getProtocolMappers();
        protocolMappers.createMapper(Arrays.asList(realmMapper, clientMapper));
        // Login user
        OAuthClient.AccessTokenResponse response = browserLogin("password", "rich.roles@redhat.com", "password");
        IDToken idToken = oauth.verifyIDToken(response.getIdToken());
        // Verify attribute is filled
        Map<String, Object> roleMappings = ((Map<String, Object>) (idToken.getOtherClaims().get("roles-custom")));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.keySet(), Matchers.containsInAnyOrder("realm", clientId));
        String realmRoleMappings = ((String) (roleMappings.get("realm")));
        String testAppMappings = ((String) (roleMappings.get(clientId)));
        // from direct assignment to /roleRichGroup/level2group
        // from parent group of /roleRichGroup/level2group, i.e. from /roleRichGroup
        // from client role customer-admin-composite-role - realm role for test-app
        // from parent group of /roleRichGroup/level2group, i.e. from /roleRichGroup
        // from realm role realm-composite-role
        assertRolesString(realmRoleMappings, "pref.admin", "pref.user", "pref.customer-user-premium", "pref.realm-composite-role", "pref.sample-realm-role");
        // from direct assignment to /roleRichGroup/level2group
        // from direct assignment to /roleRichGroup/level2group
        // from client role customer-admin-composite-role - client role for test-app
        // from realm role realm-composite-role - client role for test-app
        assertRolesString(testAppMappings, "ta.customer-user", "ta.customer-admin-composite-role", "ta.customer-admin", "ta.sample-client-role");
        // Revert
        deleteMappers(protocolMappers);
    }

    @Test
    public void testUserGroupRoleToAttributeMappersNotScopedOtherApp() throws Exception {
        String clientId = "test-app-authz";
        ProtocolMapperRepresentation realmMapper = ProtocolMapperUtil.createUserRealmRoleMappingMapper("pref.", "Realm roles mapper", "roles-custom.realm", true, true);
        ProtocolMapperRepresentation clientMapper = ProtocolMapperUtil.createUserClientRoleMappingMapper(clientId, null, "Client roles mapper", ("roles-custom." + clientId), true, true);
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), clientId).getProtocolMappers();
        protocolMappers.createMapper(Arrays.asList(realmMapper, clientMapper));
        // Login user
        realm(adminClient.realm("test")).clientId(clientId).directAccessGrant(true);
        oauth.clientId(clientId);
        String oldRedirectUri = oauth.getRedirectUri();
        oauth.redirectUri(((UriUtils.getOrigin(oldRedirectUri)) + "/test-app-authz"));
        OAuthClient.AccessTokenResponse response = browserLogin("secret", "rich.roles@redhat.com", "password");
        IDToken idToken = oauth.verifyIDToken(response.getIdToken());
        // revert redirect_uri
        oauth.redirectUri(oldRedirectUri);
        // Verify attribute is filled
        Map<String, Object> roleMappings = ((Map<String, Object>) (idToken.getOtherClaims().get("roles-custom")));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.keySet(), Matchers.containsInAnyOrder("realm"));
        String realmRoleMappings = ((String) (roleMappings.get("realm")));
        String testAppAuthzMappings = ((String) (roleMappings.get(clientId)));
        // from direct assignment to /roleRichGroup/level2group
        // from parent group of /roleRichGroup/level2group, i.e. from /roleRichGroup
        // from client role customer-admin-composite-role - realm role for test-app
        // from parent group of /roleRichGroup/level2group, i.e. from /roleRichGroup
        // from realm role realm-composite-role
        assertRolesString(realmRoleMappings, "pref.admin", "pref.user", "pref.customer-user-premium", "pref.realm-composite-role", "pref.sample-realm-role");
        Assert.assertNull(testAppAuthzMappings);// There is no client role defined for test-app-authz

        // Revert
        deleteMappers(protocolMappers);
    }

    @Test
    public void testUserGroupRoleToAttributeMappersScoped() throws Exception {
        String clientId = "test-app-scope";
        ProtocolMapperRepresentation realmMapper = ProtocolMapperUtil.createUserRealmRoleMappingMapper("pref.", "Realm roles mapper", "roles-custom.realm", true, true);
        ProtocolMapperRepresentation clientMapper = ProtocolMapperUtil.createUserClientRoleMappingMapper(clientId, null, "Client roles mapper", "roles-custom.test-app-scope", true, true);
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), clientId).getProtocolMappers();
        protocolMappers.createMapper(Arrays.asList(realmMapper, clientMapper));
        // Login user
        realm(adminClient.realm("test")).clientId(clientId).directAccessGrant(true);
        oauth.clientId(clientId);
        OAuthClient.AccessTokenResponse response = browserLogin("password", "rich.roles@redhat.com", "password");
        IDToken idToken = oauth.verifyIDToken(response.getIdToken());
        // Verify attribute is filled
        Map<String, Object> roleMappings = ((Map<String, Object>) (idToken.getOtherClaims().get("roles-custom")));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.keySet(), Matchers.containsInAnyOrder("realm", clientId));
        String realmRoleMappings = ((String) (roleMappings.get("realm")));
        String testAppScopeMappings = ((String) (roleMappings.get(clientId)));
        // from direct assignment to /roleRichGroup/level2group
        // from parent group of /roleRichGroup/level2group, i.e. from /roleRichGroup
        assertRolesString(realmRoleMappings, "pref.admin", "pref.user", "pref.customer-user-premium");
        // from direct assignment to roleRichUser, present as scope allows it
        assertRolesString(testAppScopeMappings, "test-app-allowed-by-scope", "test-app-disallowed-by-scope");
        // Revert
        deleteMappers(protocolMappers);
    }

    @Test
    public void testUserGroupRoleToAttributeMappersScopedClientNotSet() throws Exception {
        String clientId = "test-app-scope";
        ProtocolMapperRepresentation realmMapper = ProtocolMapperUtil.createUserRealmRoleMappingMapper("pref.", "Realm roles mapper", "roles-custom.realm", true, true);
        ProtocolMapperRepresentation clientMapper = ProtocolMapperUtil.createUserClientRoleMappingMapper(null, null, "Client roles mapper", "roles-custom.test-app-scope", true, true);
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), clientId).getProtocolMappers();
        protocolMappers.createMapper(Arrays.asList(realmMapper, clientMapper));
        // Login user
        realm(adminClient.realm("test")).clientId(clientId).directAccessGrant(true);
        oauth.clientId(clientId);
        OAuthClient.AccessTokenResponse response = browserLogin("password", "rich.roles@redhat.com", "password");
        IDToken idToken = oauth.verifyIDToken(response.getIdToken());
        // Verify attribute is filled
        Map<String, Object> roleMappings = ((Map<String, Object>) (idToken.getOtherClaims().get("roles-custom")));
        org.keycloak.testsuite.Assert.assertThat(roleMappings.keySet(), Matchers.containsInAnyOrder("realm", clientId));
        String realmRoleMappings = ((String) (roleMappings.get("realm")));
        String testAppScopeMappings = ((String) (roleMappings.get(clientId)));
        // from direct assignment to /roleRichGroup/level2group
        // from parent group of /roleRichGroup/level2group, i.e. from /roleRichGroup
        assertRolesString(realmRoleMappings, "pref.admin", "pref.user", "pref.customer-user-premium");
        // from direct assignment to roleRichUser, present as scope allows it
        // from direct assignment to /roleRichGroup/level2group, present as scope allows it
        assertRolesString(testAppScopeMappings, "test-app-allowed-by-scope", "test-app-disallowed-by-scope");
        // Revert
        deleteMappers(protocolMappers);
    }

    @Test
    public void testGroupAttributeUserOneGroupNoMultivalueNoAggregate() throws Exception {
        // get the user
        UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
        UserRepresentation user = userResource.toRepresentation();
        user.setAttributes(new HashMap());
        user.getAttributes().put("group-value", Arrays.asList("user-value1", "user-value2"));
        userResource.update(user);
        // create a group1 with two values
        GroupRepresentation group1 = new GroupRepresentation();
        group1.setName("group1");
        group1.setAttributes(new HashMap());
        group1.getAttributes().put("group-value", Arrays.asList("value1", "value2"));
        adminClient.realm("test").groups().add(group1);
        group1 = adminClient.realm("test").getGroupByPath("/group1");
        userResource.joinGroup(group1.getId());
        // create the attribute mapper
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(ProtocolMapperUtil.createClaimMapper("group-value", "group-value", "group-value", "String", true, true, false, false)).close();
        try {
            // test it
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNotNull(idToken.getOtherClaims());
            Assert.assertNotNull(idToken.getOtherClaims().get("group-value"));
            Assert.assertTrue(((idToken.getOtherClaims().get("group-value")) instanceof String));
            Assert.assertTrue((("user-value1".equals(idToken.getOtherClaims().get("group-value"))) || ("user-value2".equals(idToken.getOtherClaims().get("group-value")))));
        } finally {
            // revert
            user.getAttributes().remove("group-value");
            userResource.update(user);
            userResource.leaveGroup(group1.getId());
            adminClient.realm("test").groups().group(group1.getId()).remove();
            deleteMappers(protocolMappers);
        }
    }

    @Test
    public void testGroupAttributeUserOneGroupMultivalueNoAggregate() throws Exception {
        // get the user
        UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
        UserRepresentation user = userResource.toRepresentation();
        user.setAttributes(new HashMap());
        user.getAttributes().put("group-value", Arrays.asList("user-value1", "user-value2"));
        userResource.update(user);
        // create a group1 with two values
        GroupRepresentation group1 = new GroupRepresentation();
        group1.setName("group1");
        group1.setAttributes(new HashMap());
        group1.getAttributes().put("group-value", Arrays.asList("value1", "value2"));
        adminClient.realm("test").groups().add(group1);
        group1 = adminClient.realm("test").getGroupByPath("/group1");
        userResource.joinGroup(group1.getId());
        // create the attribute mapper
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(ProtocolMapperUtil.createClaimMapper("group-value", "group-value", "group-value", "String", true, true, true, false)).close();
        try {
            // test it
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNotNull(idToken.getOtherClaims());
            Assert.assertNotNull(idToken.getOtherClaims().get("group-value"));
            Assert.assertTrue(((idToken.getOtherClaims().get("group-value")) instanceof List));
            Assert.assertEquals(2, ((List) (idToken.getOtherClaims().get("group-value"))).size());
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("user-value1"));
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("user-value2"));
        } finally {
            // revert
            user.getAttributes().remove("group-value");
            userResource.update(user);
            userResource.leaveGroup(group1.getId());
            adminClient.realm("test").groups().group(group1.getId()).remove();
            deleteMappers(protocolMappers);
        }
    }

    @Test
    public void testGroupAttributeUserOneGroupMultivalueAggregate() throws Exception {
        // get the user
        UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
        UserRepresentation user = userResource.toRepresentation();
        user.setAttributes(new HashMap());
        user.getAttributes().put("group-value", Arrays.asList("user-value1", "user-value2"));
        userResource.update(user);
        // create a group1 with two values
        GroupRepresentation group1 = new GroupRepresentation();
        group1.setName("group1");
        group1.setAttributes(new HashMap());
        group1.getAttributes().put("group-value", Arrays.asList("value1", "value2"));
        adminClient.realm("test").groups().add(group1);
        group1 = adminClient.realm("test").getGroupByPath("/group1");
        userResource.joinGroup(group1.getId());
        // create the attribute mapper
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(ProtocolMapperUtil.createClaimMapper("group-value", "group-value", "group-value", "String", true, true, true, true)).close();
        try {
            // test it
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNotNull(idToken.getOtherClaims());
            Assert.assertNotNull(idToken.getOtherClaims().get("group-value"));
            Assert.assertTrue(((idToken.getOtherClaims().get("group-value")) instanceof List));
            Assert.assertEquals(4, ((List) (idToken.getOtherClaims().get("group-value"))).size());
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("user-value1"));
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("user-value2"));
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("value1"));
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("value2"));
        } finally {
            // revert
            user.getAttributes().remove("group-value");
            userResource.update(user);
            userResource.leaveGroup(group1.getId());
            adminClient.realm("test").groups().group(group1.getId()).remove();
            deleteMappers(protocolMappers);
        }
    }

    @Test
    public void testGroupAttributeOneGroupNoMultivalueNoAggregate() throws Exception {
        // get the user
        UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
        // create a group1 with two values
        GroupRepresentation group1 = new GroupRepresentation();
        group1.setName("group1");
        group1.setAttributes(new HashMap());
        group1.getAttributes().put("group-value", Arrays.asList("value1", "value2"));
        adminClient.realm("test").groups().add(group1);
        group1 = adminClient.realm("test").getGroupByPath("/group1");
        userResource.joinGroup(group1.getId());
        // create the attribute mapper
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(ProtocolMapperUtil.createClaimMapper("group-value", "group-value", "group-value", "String", true, true, false, false)).close();
        try {
            // test it
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNotNull(idToken.getOtherClaims());
            Assert.assertNotNull(idToken.getOtherClaims().get("group-value"));
            Assert.assertTrue(((idToken.getOtherClaims().get("group-value")) instanceof String));
            Assert.assertTrue((("value1".equals(idToken.getOtherClaims().get("group-value"))) || ("value2".equals(idToken.getOtherClaims().get("group-value")))));
        } finally {
            // revert
            userResource.leaveGroup(group1.getId());
            adminClient.realm("test").groups().group(group1.getId()).remove();
            deleteMappers(protocolMappers);
        }
    }

    @Test
    public void testGroupAttributeOneGroupMultiValueNoAggregate() throws Exception {
        // get the user
        UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
        // create a group1 with two values
        GroupRepresentation group1 = new GroupRepresentation();
        group1.setName("group1");
        group1.setAttributes(new HashMap());
        group1.getAttributes().put("group-value", Arrays.asList("value1", "value2"));
        adminClient.realm("test").groups().add(group1);
        group1 = adminClient.realm("test").getGroupByPath("/group1");
        userResource.joinGroup(group1.getId());
        // create the attribute mapper
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(ProtocolMapperUtil.createClaimMapper("group-value", "group-value", "group-value", "String", true, true, true, false)).close();
        try {
            // test it
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNotNull(idToken.getOtherClaims());
            Assert.assertNotNull(idToken.getOtherClaims().get("group-value"));
            Assert.assertTrue(((idToken.getOtherClaims().get("group-value")) instanceof List));
            Assert.assertEquals(2, ((List) (idToken.getOtherClaims().get("group-value"))).size());
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("value1"));
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("value2"));
        } finally {
            // revert
            userResource.leaveGroup(group1.getId());
            adminClient.realm("test").groups().group(group1.getId()).remove();
            deleteMappers(protocolMappers);
        }
    }

    @Test
    public void testGroupAttributeOneGroupMultiValueAggregate() throws Exception {
        // get the user
        UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
        // create a group1 with two values
        GroupRepresentation group1 = new GroupRepresentation();
        group1.setName("group1");
        group1.setAttributes(new HashMap());
        group1.getAttributes().put("group-value", Arrays.asList("value1", "value2"));
        adminClient.realm("test").groups().add(group1);
        group1 = adminClient.realm("test").getGroupByPath("/group1");
        userResource.joinGroup(group1.getId());
        // create the attribute mapper
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(ProtocolMapperUtil.createClaimMapper("group-value", "group-value", "group-value", "String", true, true, true, true)).close();
        try {
            // test it
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNotNull(idToken.getOtherClaims());
            Assert.assertNotNull(idToken.getOtherClaims().get("group-value"));
            Assert.assertTrue(((idToken.getOtherClaims().get("group-value")) instanceof List));
            Assert.assertEquals(2, ((List) (idToken.getOtherClaims().get("group-value"))).size());
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("value1"));
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("value2"));
        } finally {
            // revert
            userResource.leaveGroup(group1.getId());
            adminClient.realm("test").groups().group(group1.getId()).remove();
            deleteMappers(protocolMappers);
        }
    }

    @Test
    public void testGroupAttributeTwoGroupNoMultivalueNoAggregate() throws Exception {
        // get the user
        UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
        // create two groups with two values (one is the same value)
        GroupRepresentation group1 = new GroupRepresentation();
        group1.setName("group1");
        group1.setAttributes(new HashMap());
        group1.getAttributes().put("group-value", Arrays.asList("value1", "value2"));
        adminClient.realm("test").groups().add(group1);
        group1 = adminClient.realm("test").getGroupByPath("/group1");
        userResource.joinGroup(group1.getId());
        GroupRepresentation group2 = new GroupRepresentation();
        group2.setName("group2");
        group2.setAttributes(new HashMap());
        group2.getAttributes().put("group-value", Arrays.asList("value2", "value3"));
        adminClient.realm("test").groups().add(group2);
        group2 = adminClient.realm("test").getGroupByPath("/group2");
        userResource.joinGroup(group2.getId());
        // create the attribute mapper
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(ProtocolMapperUtil.createClaimMapper("group-value", "group-value", "group-value", "String", true, true, false, false)).close();
        try {
            // test it
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNotNull(idToken.getOtherClaims());
            Assert.assertNotNull(idToken.getOtherClaims().get("group-value"));
            Assert.assertTrue(((idToken.getOtherClaims().get("group-value")) instanceof String));
            Assert.assertTrue(((("value1".equals(idToken.getOtherClaims().get("group-value"))) || ("value2".equals(idToken.getOtherClaims().get("group-value")))) || ("value3".equals(idToken.getOtherClaims().get("group-value")))));
        } finally {
            // revert
            userResource.leaveGroup(group1.getId());
            adminClient.realm("test").groups().group(group1.getId()).remove();
            userResource.leaveGroup(group2.getId());
            adminClient.realm("test").groups().group(group2.getId()).remove();
            deleteMappers(protocolMappers);
        }
    }

    @Test
    public void testGroupAttributeTwoGroupMultiValueNoAggregate() throws Exception {
        // get the user
        UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
        // create two groups with two values (one is the same value)
        GroupRepresentation group1 = new GroupRepresentation();
        group1.setName("group1");
        group1.setAttributes(new HashMap());
        group1.getAttributes().put("group-value", Arrays.asList("value1", "value2"));
        adminClient.realm("test").groups().add(group1);
        group1 = adminClient.realm("test").getGroupByPath("/group1");
        userResource.joinGroup(group1.getId());
        GroupRepresentation group2 = new GroupRepresentation();
        group2.setName("group2");
        group2.setAttributes(new HashMap());
        group2.getAttributes().put("group-value", Arrays.asList("value2", "value3"));
        adminClient.realm("test").groups().add(group2);
        group2 = adminClient.realm("test").getGroupByPath("/group2");
        userResource.joinGroup(group2.getId());
        // create the attribute mapper
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(ProtocolMapperUtil.createClaimMapper("group-value", "group-value", "group-value", "String", true, true, true, false)).close();
        try {
            // test it
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNotNull(idToken.getOtherClaims());
            Assert.assertNotNull(idToken.getOtherClaims().get("group-value"));
            Assert.assertTrue(((idToken.getOtherClaims().get("group-value")) instanceof List));
            Assert.assertEquals(2, ((List) (idToken.getOtherClaims().get("group-value"))).size());
            Assert.assertTrue((((((List) (idToken.getOtherClaims().get("group-value"))).contains("value1")) && (((List) (idToken.getOtherClaims().get("group-value"))).contains("value2"))) || ((((List) (idToken.getOtherClaims().get("group-value"))).contains("value2")) && (((List) (idToken.getOtherClaims().get("group-value"))).contains("value3")))));
        } finally {
            // revert
            userResource.leaveGroup(group1.getId());
            adminClient.realm("test").groups().group(group1.getId()).remove();
            userResource.leaveGroup(group2.getId());
            adminClient.realm("test").groups().group(group2.getId()).remove();
            deleteMappers(protocolMappers);
        }
    }

    @Test
    public void testGroupAttributeTwoGroupMultiValueAggregate() throws Exception {
        // get the user
        UserResource userResource = ApiUtil.findUserByUsernameId(adminClient.realm("test"), "test-user@localhost");
        // create two groups with two values (one is the same value)
        GroupRepresentation group1 = new GroupRepresentation();
        group1.setName("group1");
        group1.setAttributes(new HashMap());
        group1.getAttributes().put("group-value", Arrays.asList("value1", "value2"));
        adminClient.realm("test").groups().add(group1);
        group1 = adminClient.realm("test").getGroupByPath("/group1");
        userResource.joinGroup(group1.getId());
        GroupRepresentation group2 = new GroupRepresentation();
        group2.setName("group2");
        group2.setAttributes(new HashMap());
        group2.getAttributes().put("group-value", Arrays.asList("value2", "value3"));
        adminClient.realm("test").groups().add(group2);
        group2 = adminClient.realm("test").getGroupByPath("/group2");
        userResource.joinGroup(group2.getId());
        // create the attribute mapper
        ProtocolMappersResource protocolMappers = ApiUtil.findClientResourceByClientId(adminClient.realm("test"), "test-app").getProtocolMappers();
        protocolMappers.createMapper(ProtocolMapperUtil.createClaimMapper("group-value", "group-value", "group-value", "String", true, true, true, true)).close();
        try {
            // test it
            OAuthClient.AccessTokenResponse response = browserLogin("password", "test-user@localhost", "password");
            IDToken idToken = oauth.verifyIDToken(response.getIdToken());
            Assert.assertNotNull(idToken.getOtherClaims());
            Assert.assertNotNull(idToken.getOtherClaims().get("group-value"));
            Assert.assertTrue(((idToken.getOtherClaims().get("group-value")) instanceof List));
            Assert.assertEquals(3, ((List) (idToken.getOtherClaims().get("group-value"))).size());
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("value1"));
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("value2"));
            Assert.assertTrue(((List) (idToken.getOtherClaims().get("group-value"))).contains("value3"));
        } finally {
            // revert
            userResource.leaveGroup(group1.getId());
            adminClient.realm("test").groups().group(group1.getId()).remove();
            userResource.leaveGroup(group2.getId());
            adminClient.realm("test").groups().group(group2.getId()).remove();
            deleteMappers(protocolMappers);
        }
    }
}

