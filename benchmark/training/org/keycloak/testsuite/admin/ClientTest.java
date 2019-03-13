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


import AccountRoles.MANAGE_ACCOUNT;
import AccountRoles.MANAGE_ACCOUNT_LINKS;
import AccountRoles.VIEW_PROFILE;
import Constants.ACCOUNT_MANAGEMENT_CLIENT_ID;
import Constants.ADMIN_CLI_CLIENT_ID;
import Constants.AUTHZ_UMA_AUTHORIZATION;
import OAuth2Constants.OFFLINE_ACCESS;
import OAuthClient.AuthorizationEndpointResponse;
import OperationType.ACTION;
import OperationType.CREATE;
import OperationType.DELETE;
import OperationType.UPDATE;
import ResourceType.CLIENT;
import ResourceType.CLIENT_ROLE;
import ResourceType.CLIENT_SCOPE_MAPPING;
import ResourceType.CLUSTER_NODE;
import ResourceType.REALM_ROLE;
import ResourceType.REALM_SCOPE_MAPPING;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.ProtocolMappersResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.representations.adapters.action.GlobalRequestResult;
import org.keycloak.representations.adapters.action.PushNotBeforeAction;
import org.keycloak.representations.adapters.action.TestAvailabilityAction;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.UserSessionRepresentation;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.Assert;
import org.keycloak.testsuite.util.AdminEventPaths;
import org.keycloak.testsuite.util.ClientBuilder;
import org.keycloak.testsuite.util.CredentialBuilder;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.OAuthClient.AccessTokenResponse;
import org.keycloak.testsuite.util.RoleBuilder;
import org.keycloak.testsuite.util.UserBuilder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class ClientTest extends AbstractAdminTest {
    @Test
    public void getClients() {
        Assert.assertNames(realm.clients().findAll(), "account", "realm-management", "security-admin-console", "broker", ADMIN_CLI_CLIENT_ID);
    }

    @Test
    public void createClientVerify() {
        String id = createClient().getId();
        assertNotNull(realm.clients().get(id));
        Assert.assertNames(realm.clients().findAll(), "account", "realm-management", "security-admin-console", "broker", "my-app", ADMIN_CLI_CLIENT_ID);
    }

    @Test
    public void removeClient() {
        String id = createClient().getId();
        assertNotNull(ApiUtil.findClientByClientId(realm, "my-app"));
        realm.clients().get(id).remove();
        assertNull(ApiUtil.findClientResourceByClientId(realm, "my-app"));
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.clientResourcePath(id), CLIENT);
    }

    @Test
    public void getClientRepresentation() {
        String id = createClient().getId();
        ClientRepresentation rep = realm.clients().get(id).toRepresentation();
        org.junit.Assert.assertEquals(id, rep.getId());
        org.junit.Assert.assertEquals("my-app", rep.getClientId());
        assertTrue(rep.isEnabled());
    }

    /**
     * See <a href="https://issues.jboss.org/browse/KEYCLOAK-1918">KEYCLOAK-1918</a>
     */
    @Test
    public void getClientDescription() {
        String id = createClient().getId();
        ClientRepresentation rep = realm.clients().get(id).toRepresentation();
        org.junit.Assert.assertEquals(id, rep.getId());
        org.junit.Assert.assertEquals("my-app description", rep.getDescription());
    }

    @Test
    public void getClientSessions() throws Exception {
        OAuthClient.AccessTokenResponse response = oauth.doGrantAccessTokenRequest("password", "test-user@localhost", "password");
        org.junit.Assert.assertEquals(200, response.getStatusCode());
        OAuthClient.AuthorizationEndpointResponse codeResponse = oauth.doLogin("test-user@localhost", "password");
        OAuthClient.AccessTokenResponse response2 = oauth.doAccessTokenRequest(codeResponse.getCode(), "password");
        org.junit.Assert.assertEquals(200, response2.getStatusCode());
        ClientResource app = ApiUtil.findClientByClientId(adminClient.realm("test"), "test-app");
        org.junit.Assert.assertEquals(2, ((long) (app.getApplicationSessionCount().get("count"))));
        List<UserSessionRepresentation> userSessions = app.getUserSessions(0, 100);
        org.junit.Assert.assertEquals(2, userSessions.size());
        org.junit.Assert.assertEquals(1, userSessions.get(0).getClients().size());
    }

    @Test
    public void getAllClients() {
        List<ClientRepresentation> allClients = realm.clients().findAll();
        assertNotNull(allClients);
        assertFalse(allClients.isEmpty());
    }

    @Test
    public void getClientById() {
        createClient();
        ClientRepresentation rep = ApiUtil.findClientResourceByClientId(realm, "my-app").toRepresentation();
        ClientRepresentation gotById = realm.clients().get(rep.getId()).toRepresentation();
        ClientTest.assertClient(rep, gotById);
    }

    // KEYCLOAK-1110
    @Test
    public void deleteDefaultRole() {
        ClientRepresentation rep = createClient();
        String id = rep.getId();
        RoleRepresentation role = new RoleRepresentation("test", "test", false);
        realm.clients().get(id).roles().create(role);
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.clientRoleResourcePath(id, "test"), role, CLIENT_ROLE);
        ClientRepresentation foundClientRep = realm.clients().get(id).toRepresentation();
        foundClientRep.setDefaultRoles(new String[]{ "test" });
        realm.clients().get(id).update(foundClientRep);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.clientResourcePath(id), rep, CLIENT);
        assertArrayEquals(new String[]{ "test" }, realm.clients().get(id).toRepresentation().getDefaultRoles());
        realm.clients().get(id).roles().deleteRole("test");
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.clientRoleResourcePath(id, "test"), CLIENT_ROLE);
        assertNull(realm.clients().get(id).toRepresentation().getDefaultRoles());
    }

    @Test
    public void testProtocolMappers() {
        String clientDbId = createClient().getId();
        ProtocolMappersResource mappersResource = ApiUtil.findClientByClientId(realm, "my-app").getProtocolMappers();
        protocolMappersTest(clientDbId, mappersResource);
    }

    @Test
    public void updateClient() {
        ClientRepresentation client = createClient();
        ClientRepresentation newClient = new ClientRepresentation();
        newClient.setId(client.getId());
        newClient.setClientId(client.getClientId());
        newClient.setBaseUrl("http://baseurl");
        realm.clients().get(client.getId()).update(newClient);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.clientResourcePath(client.getId()), newClient, CLIENT);
        ClientRepresentation storedClient = realm.clients().get(client.getId()).toRepresentation();
        ClientTest.assertClient(client, storedClient);
        newClient.setSecret("new-secret");
        realm.clients().get(client.getId()).update(newClient);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.clientResourcePath(client.getId()), newClient, CLIENT);
        storedClient = realm.clients().get(client.getId()).toRepresentation();
        ClientTest.assertClient(client, storedClient);
    }

    @Test
    public void serviceAccount() {
        Response response = realm.clients().create(ClientBuilder.create().clientId("serviceClient").serviceAccount().build());
        String id = ApiUtil.getCreatedId(response);
        getCleanup().addClientUuid(id);
        response.close();
        UserRepresentation userRep = realm.clients().get(id).getServiceAccountUser();
        org.junit.Assert.assertEquals("service-account-serviceclient", userRep.getUsername());
    }

    // KEYCLOAK-3421
    @Test
    public void createClientWithFragments() {
        ClientRepresentation client = ClientBuilder.create().clientId("client-with-fragment").rootUrl("http://localhost/base#someFragment").redirectUris("http://localhost/auth", "http://localhost/auth#fragment", "http://localhost/auth*", "/relative").build();
        Response response = realm.clients().create(client);
        assertUriFragmentError(response);
    }

    // KEYCLOAK-3421
    @Test
    public void updateClientWithFragments() {
        ClientRepresentation client = ClientBuilder.create().clientId("client-with-fragment").redirectUris("http://localhost/auth", "http://localhost/auth*").build();
        Response response = realm.clients().create(client);
        String clientUuid = ApiUtil.getCreatedId(response);
        ClientResource clientResource = realm.clients().get(clientUuid);
        getCleanup().addClientUuid(clientUuid);
        response.close();
        client = clientResource.toRepresentation();
        client.setRootUrl("http://localhost/base#someFragment");
        List<String> redirectUris = client.getRedirectUris();
        redirectUris.add("http://localhost/auth#fragment");
        redirectUris.add("/relative");
        client.setRedirectUris(redirectUris);
        try {
            clientResource.update(client);
            fail("Should fail");
        } catch (BadRequestException e) {
            assertUriFragmentError(e.getResponse());
        }
    }

    @Test
    public void pushRevocation() {
        testingClient.testApp().clearAdminActions();
        ClientRepresentation client = createAppClient();
        String id = client.getId();
        realm.clients().get(id).pushRevocation();
        PushNotBeforeAction pushNotBefore = testingClient.testApp().getAdminPushNotBefore();
        org.junit.Assert.assertEquals(client.getNotBefore().intValue(), pushNotBefore.getNotBefore());
        assertAdminEvents.assertEvent(realmId, ACTION, AdminEventPaths.clientPushRevocationPath(id), CLIENT);
    }

    @Test
    public void nodes() {
        testingClient.testApp().clearAdminActions();
        ClientRepresentation client = createAppClient();
        String id = client.getId();
        String myhost = suiteContext.getAuthServerInfo().getContextRoot().getHost();
        realm.clients().get(id).registerNode(Collections.singletonMap("node", myhost));
        realm.clients().get(id).registerNode(Collections.singletonMap("node", "invalid"));
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.clientNodePath(id, myhost), CLUSTER_NODE);
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.clientNodePath(id, "invalid"), CLUSTER_NODE);
        GlobalRequestResult result = realm.clients().get(id).testNodesAvailable();
        org.junit.Assert.assertEquals(1, result.getSuccessRequests().size());
        org.junit.Assert.assertEquals(1, result.getFailedRequests().size());
        assertAdminEvents.assertEvent(realmId, ACTION, AdminEventPaths.clientTestNodesAvailablePath(id), result, CLUSTER_NODE);
        TestAvailabilityAction testAvailable = testingClient.testApp().getTestAvailable();
        org.junit.Assert.assertEquals("test-app", testAvailable.getResource());
        org.junit.Assert.assertEquals(2, realm.clients().get(id).toRepresentation().getRegisteredNodes().size());
        realm.clients().get(id).unregisterNode("invalid");
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.clientNodePath(id, "invalid"), CLUSTER_NODE);
        org.junit.Assert.assertEquals(1, realm.clients().get(id).toRepresentation().getRegisteredNodes().size());
    }

    @Test
    public void offlineUserSessions() throws IOException {
        ClientRepresentation client = createAppClient();
        String id = client.getId();
        Response response = realm.users().create(UserBuilder.create().username("testuser").build());
        String userId = ApiUtil.getCreatedId(response);
        response.close();
        realm.users().get(userId).resetPassword(CredentialBuilder.create().password("password").build());
        Map<String, Long> offlineSessionCount = realm.clients().get(id).getOfflineSessionCount();
        org.junit.Assert.assertEquals(new Long(0), offlineSessionCount.get("count"));
        List<UserSessionRepresentation> userSessions = realm.users().get(userId).getOfflineSessions(id);
        assertEquals("There should be no offline sessions", 0, userSessions.size());
        oauth.realm(AbstractAdminTest.REALM_NAME);
        oauth.redirectUri(client.getRedirectUris().get(0));
        oauth.scope(OFFLINE_ACCESS);
        oauth.doLogin("testuser", "password");
        AccessTokenResponse accessTokenResponse = oauth.doAccessTokenRequest(oauth.getCurrentQuery().get("code"), "secret");
        org.junit.Assert.assertEquals(200, accessTokenResponse.getStatusCode());
        offlineSessionCount = realm.clients().get(id).getOfflineSessionCount();
        org.junit.Assert.assertEquals(new Long(1), offlineSessionCount.get("count"));
        List<UserSessionRepresentation> offlineUserSessions = realm.clients().get(id).getOfflineUserSessions(0, 100);
        org.junit.Assert.assertEquals(1, offlineUserSessions.size());
        org.junit.Assert.assertEquals("testuser", offlineUserSessions.get(0).getUsername());
        userSessions = realm.users().get(userId).getOfflineSessions(id);
        assertEquals("There should be one offline session", 1, userSessions.size());
        assertOfflineSession(offlineUserSessions.get(0), userSessions.get(0));
    }

    @Test
    public void scopes() {
        Response response = realm.clients().create(ClientBuilder.create().clientId("client").fullScopeEnabled(false).build());
        String id = ApiUtil.getCreatedId(response);
        getCleanup().addClientUuid(id);
        response.close();
        assertAdminEvents.poll();
        RoleMappingResource scopesResource = realm.clients().get(id).getScopeMappings();
        RoleRepresentation roleRep1 = RoleBuilder.create().name("role1").build();
        RoleRepresentation roleRep2 = RoleBuilder.create().name("role2").build();
        realm.roles().create(roleRep1);
        realm.roles().create(roleRep2);
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.roleResourcePath("role1"), roleRep1, REALM_ROLE);
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.roleResourcePath("role2"), roleRep2, REALM_ROLE);
        roleRep1 = realm.roles().get("role1").toRepresentation();
        roleRep2 = realm.roles().get("role2").toRepresentation();
        realm.roles().get("role1").addComposites(Collections.singletonList(roleRep2));
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.roleResourceCompositesPath("role1"), Collections.singletonList(roleRep2), REALM_ROLE);
        String accountMgmtId = realm.clients().findByClientId(ACCOUNT_MANAGEMENT_CLIENT_ID).get(0).getId();
        RoleRepresentation viewAccountRoleRep = realm.clients().get(accountMgmtId).roles().get(VIEW_PROFILE).toRepresentation();
        scopesResource.realmLevel().add(Collections.singletonList(roleRep1));
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.clientScopeMappingsRealmLevelPath(id), Collections.singletonList(roleRep1), REALM_SCOPE_MAPPING);
        scopesResource.clientLevel(accountMgmtId).add(Collections.singletonList(viewAccountRoleRep));
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.clientScopeMappingsClientLevelPath(id, accountMgmtId), Collections.singletonList(viewAccountRoleRep), CLIENT_SCOPE_MAPPING);
        Assert.assertNames(scopesResource.realmLevel().listAll(), "role1");
        Assert.assertNames(scopesResource.realmLevel().listEffective(), "role1", "role2");
        Assert.assertNames(scopesResource.realmLevel().listAvailable(), "offline_access", AUTHZ_UMA_AUTHORIZATION);
        Assert.assertNames(scopesResource.clientLevel(accountMgmtId).listAll(), VIEW_PROFILE);
        Assert.assertNames(scopesResource.clientLevel(accountMgmtId).listEffective(), VIEW_PROFILE);
        Assert.assertNames(scopesResource.clientLevel(accountMgmtId).listAvailable(), MANAGE_ACCOUNT, MANAGE_ACCOUNT_LINKS);
        Assert.assertNames(scopesResource.getAll().getRealmMappings(), "role1");
        Assert.assertNames(scopesResource.getAll().getClientMappings().get(ACCOUNT_MANAGEMENT_CLIENT_ID).getMappings(), VIEW_PROFILE);
        scopesResource.realmLevel().remove(Collections.singletonList(roleRep1));
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.clientScopeMappingsRealmLevelPath(id), Collections.singletonList(roleRep1), REALM_SCOPE_MAPPING);
        scopesResource.clientLevel(accountMgmtId).remove(Collections.singletonList(viewAccountRoleRep));
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.clientScopeMappingsClientLevelPath(id, accountMgmtId), Collections.singletonList(viewAccountRoleRep), CLIENT_SCOPE_MAPPING);
        Assert.assertNames(scopesResource.realmLevel().listAll());
        Assert.assertNames(scopesResource.realmLevel().listEffective());
        Assert.assertNames(scopesResource.realmLevel().listAvailable(), "offline_access", AUTHZ_UMA_AUTHORIZATION, "role1", "role2");
        Assert.assertNames(scopesResource.clientLevel(accountMgmtId).listAll());
        Assert.assertNames(scopesResource.clientLevel(accountMgmtId).listAvailable(), VIEW_PROFILE, MANAGE_ACCOUNT, MANAGE_ACCOUNT_LINKS);
        Assert.assertNames(scopesResource.clientLevel(accountMgmtId).listEffective());
    }
}

