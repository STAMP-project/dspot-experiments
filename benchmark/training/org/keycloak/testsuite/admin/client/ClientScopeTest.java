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
package org.keycloak.testsuite.admin.client;


import AccountRoles.VIEW_PROFILE;
import Constants.ACCOUNT_MANAGEMENT_CLIENT_ID;
import OIDCLoginProtocol.LOGIN_PROTOCOL;
import OperationType.CREATE;
import OperationType.DELETE;
import OperationType.UPDATE;
import ResourceType.CLIENT_SCOPE;
import ResourceType.CLIENT_SCOPE_MAPPING;
import ResourceType.REALM_ROLE;
import ResourceType.REALM_SCOPE_MAPPING;
import Status.CONFLICT;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ProtocolMappersResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.common.util.ObjectUtil;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.ClientScopeRepresentation;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.MappingsRepresentation;
import org.keycloak.representations.idm.ProtocolMapperRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.util.AdminEventPaths;
import org.keycloak.testsuite.util.Matchers;


/**
 *
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class ClientScopeTest extends AbstractClientTest {
    @Test
    public void testAddDuplicatedClientScope() {
        ClientScopeRepresentation scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("scope1");
        String scopeId = createClientScope(scopeRep);
        scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("scope1");
        Response response = clientScopes().create(scopeRep);
        Assert.assertEquals(409, response.getStatus());
        ErrorRepresentation error = response.readEntity(ErrorRepresentation.class);
        Assert.assertEquals("Client Scope scope1 already exists", error.getErrorMessage());
        // Cleanup
        removeClientScope(scopeId);
    }

    @Test(expected = NotFoundException.class)
    public void testGetUnknownScope() {
        clientScopes().get("unknown-id").toRepresentation();
    }

    @Test
    public void testRemoveClientScope() {
        // Create scope1
        ClientScopeRepresentation scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("scope1");
        String scope1Id = createClientScope(scopeRep);
        List<ClientScopeRepresentation> clientScopes = clientScopes().findAll();
        Assert.assertTrue(getClientScopeNames(clientScopes).contains("scope1"));
        // Create scope2
        scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("scope2");
        String scope2Id = createClientScope(scopeRep);
        clientScopes = clientScopes().findAll();
        Assert.assertTrue(getClientScopeNames(clientScopes).contains("scope2"));
        // Remove scope1
        removeClientScope(scope1Id);
        clientScopes = clientScopes().findAll();
        Assert.assertFalse(getClientScopeNames(clientScopes).contains("scope1"));
        Assert.assertTrue(getClientScopeNames(clientScopes).contains("scope2"));
        // Remove scope2
        removeClientScope(scope2Id);
        clientScopes = clientScopes().findAll();
        Assert.assertFalse(getClientScopeNames(clientScopes).contains("scope1"));
        Assert.assertFalse(getClientScopeNames(clientScopes).contains("scope2"));
    }

    @Test
    public void testUpdateScopeScope() {
        // Test creating
        ClientScopeRepresentation scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("scope1");
        scopeRep.setDescription("scope1-desc");
        scopeRep.setProtocol(LOGIN_PROTOCOL);
        Map<String, String> attrs = new HashMap<>();
        attrs.put("someAttr", "someAttrValue");
        attrs.put("emptyAttr", "");
        scopeRep.setAttributes(attrs);
        String scope1Id = createClientScope(scopeRep);
        // Assert created attributes
        scopeRep = clientScopes().get(scope1Id).toRepresentation();
        Assert.assertEquals("scope1", scopeRep.getName());
        Assert.assertEquals("scope1-desc", scopeRep.getDescription());
        Assert.assertEquals("someAttrValue", scopeRep.getAttributes().get("someAttr"));
        Assert.assertTrue(ObjectUtil.isBlank(scopeRep.getAttributes().get("emptyAttr")));
        Assert.assertEquals(LOGIN_PROTOCOL, scopeRep.getProtocol());
        // Test updating
        scopeRep.setName("scope1-updated");
        scopeRep.setDescription("scope1-desc-updated");
        scopeRep.setProtocol(SamlProtocol.LOGIN_PROTOCOL);
        // Test update attribute to some non-blank value
        scopeRep.getAttributes().put("emptyAttr", "someValue");
        clientScopes().get(scope1Id).update(scopeRep);
        assertAdminEvents.assertEvent(getRealmId(), UPDATE, AdminEventPaths.clientScopeResourcePath(scope1Id), scopeRep, CLIENT_SCOPE);
        // Assert updated attributes
        scopeRep = clientScopes().get(scope1Id).toRepresentation();
        Assert.assertEquals("scope1-updated", scopeRep.getName());
        Assert.assertEquals("scope1-desc-updated", scopeRep.getDescription());
        Assert.assertEquals(SamlProtocol.LOGIN_PROTOCOL, scopeRep.getProtocol());
        Assert.assertEquals("someAttrValue", scopeRep.getAttributes().get("someAttr"));
        Assert.assertEquals("someValue", scopeRep.getAttributes().get("emptyAttr"));
        // Remove scope1
        clientScopes().get(scope1Id).remove();
    }

    @Test
    public void testRenameScope() {
        // Create two scopes
        ClientScopeRepresentation scope1Rep = new ClientScopeRepresentation();
        scope1Rep.setName("scope1");
        scope1Rep.setDescription("scope1-desc");
        scope1Rep.setProtocol(LOGIN_PROTOCOL);
        createClientScope(scope1Rep);
        ClientScopeRepresentation scope2Rep = new ClientScopeRepresentation();
        scope2Rep.setName("scope2");
        scope2Rep.setDescription("scope2-desc");
        scope2Rep.setProtocol(LOGIN_PROTOCOL);
        String scope2Id = createClientScope(scope2Rep);
        // Test updating
        scope2Rep.setName("scope1");
        try {
            clientScopes().get(scope2Id).update(scope2Rep);
        } catch (ClientErrorException ex) {
            Assert.assertThat(ex.getResponse(), Matchers.statusCodeIs(CONFLICT));
        }
    }

    @Test
    public void testScopes() {
        // Add realm role1
        RoleRepresentation roleRep1 = createRealmRole("role1");
        // Add realm role2
        RoleRepresentation roleRep2 = createRealmRole("role2");
        // Add role2 as composite to role1
        testRealmResource().roles().get("role1").addComposites(Collections.singletonList(roleRep2));
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.roleResourceCompositesPath("role1"), Collections.singletonList(roleRep2), REALM_ROLE);
        // create client scope
        ClientScopeRepresentation scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("bar-scope");
        String scopeId = createClientScope(scopeRep);
        // update with some scopes
        String accountMgmtId = testRealmResource().clients().findByClientId(ACCOUNT_MANAGEMENT_CLIENT_ID).get(0).getId();
        RoleRepresentation viewAccountRoleRep = testRealmResource().clients().get(accountMgmtId).roles().get(VIEW_PROFILE).toRepresentation();
        RoleMappingResource scopesResource = clientScopes().get(scopeId).getScopeMappings();
        scopesResource.realmLevel().add(Collections.singletonList(roleRep1));
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientScopeRoleMappingsRealmLevelPath(scopeId), Collections.singletonList(roleRep1), REALM_SCOPE_MAPPING);
        scopesResource.clientLevel(accountMgmtId).add(Collections.singletonList(viewAccountRoleRep));
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientScopeRoleMappingsClientLevelPath(scopeId, accountMgmtId), Collections.singletonList(viewAccountRoleRep), CLIENT_SCOPE_MAPPING);
        // test that scopes are available (also through composite role)
        List<RoleRepresentation> allRealm = scopesResource.realmLevel().listAll();
        List<RoleRepresentation> availableRealm = scopesResource.realmLevel().listAvailable();
        List<RoleRepresentation> effectiveRealm = scopesResource.realmLevel().listEffective();
        List<RoleRepresentation> accountRoles = scopesResource.clientLevel(accountMgmtId).listAll();
        assertRolesPresent(allRealm, "role1");
        assertRolesNotPresent(availableRealm, "role1", "role2");
        assertRolesPresent(effectiveRealm, "role1", "role2");
        assertRolesPresent(accountRoles, VIEW_PROFILE);
        MappingsRepresentation mappingsRep = clientScopes().get(scopeId).getScopeMappings().getAll();
        assertRolesPresent(mappingsRep.getRealmMappings(), "role1");
        assertRolesPresent(mappingsRep.getClientMappings().get(ACCOUNT_MANAGEMENT_CLIENT_ID).getMappings(), VIEW_PROFILE);
        // remove scopes
        scopesResource.realmLevel().remove(Collections.singletonList(roleRep1));
        assertAdminEvents.assertEvent(getRealmId(), DELETE, AdminEventPaths.clientScopeRoleMappingsRealmLevelPath(scopeId), Collections.singletonList(roleRep1), REALM_SCOPE_MAPPING);
        scopesResource.clientLevel(accountMgmtId).remove(Collections.singletonList(viewAccountRoleRep));
        assertAdminEvents.assertEvent(getRealmId(), DELETE, AdminEventPaths.clientScopeRoleMappingsClientLevelPath(scopeId, accountMgmtId), Collections.singletonList(viewAccountRoleRep), CLIENT_SCOPE_MAPPING);
        // assert scopes are removed
        allRealm = scopesResource.realmLevel().listAll();
        availableRealm = scopesResource.realmLevel().listAvailable();
        effectiveRealm = scopesResource.realmLevel().listEffective();
        accountRoles = scopesResource.clientLevel(accountMgmtId).listAll();
        assertRolesNotPresent(allRealm, "role1");
        assertRolesPresent(availableRealm, "role1", "role2");
        assertRolesNotPresent(effectiveRealm, "role1", "role2");
        assertRolesNotPresent(accountRoles, VIEW_PROFILE);
        // remove scope
        removeClientScope(scopeId);
    }

    // KEYCLOAK-2809
    @Test
    public void testRemoveScopedRole() {
        // Add realm role
        RoleRepresentation roleRep = createRealmRole("foo-role");
        // Add client scope
        ClientScopeRepresentation scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("bar-scope");
        String scopeId = createClientScope(scopeRep);
        // Add realm role to scopes of clientScope
        clientScopes().get(scopeId).getScopeMappings().realmLevel().add(Collections.singletonList(roleRep));
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientScopeRoleMappingsRealmLevelPath(scopeId), Collections.singletonList(roleRep), REALM_SCOPE_MAPPING);
        List<RoleRepresentation> roleReps = clientScopes().get(scopeId).getScopeMappings().realmLevel().listAll();
        Assert.assertEquals(1, roleReps.size());
        Assert.assertEquals("foo-role", roleReps.get(0).getName());
        // Remove realm role
        testRealmResource().roles().deleteRole("foo-role");
        assertAdminEvents.assertEvent(getRealmId(), DELETE, AdminEventPaths.roleResourcePath("foo-role"), REALM_ROLE);
        // Get scope mappings
        roleReps = clientScopes().get(scopeId).getScopeMappings().realmLevel().listAll();
        Assert.assertEquals(0, roleReps.size());
        // Cleanup
        removeClientScope(scopeId);
    }

    // KEYCLOAK-2844
    @Test
    public void testRemoveClientScopeInUse() {
        // Add client scope
        ClientScopeRepresentation scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("foo-scope");
        String scopeId = createClientScope(scopeRep);
        // Add client with the clientScope
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setClientId("bar-client");
        clientRep.setName("bar-client");
        clientRep.setRootUrl("foo");
        clientRep.setProtocol("openid-connect");
        clientRep.setDefaultClientScopes(Collections.singletonList("foo-scope"));
        String clientDbId = createClient(clientRep);
        // Can't remove clientScope
        try {
            clientScopes().get(scopeId).remove();
            Assert.fail("Not expected to successfully remove clientScope in use");
        } catch (BadRequestException bre) {
            ErrorRepresentation error = bre.getResponse().readEntity(ErrorRepresentation.class);
            Assert.assertEquals("Cannot remove client scope, it is currently in use", error.getErrorMessage());
            assertAdminEvents.assertEmpty();
        }
        // Remove client
        removeClient(clientDbId);
        // Can remove clientScope now
        removeClientScope(scopeId);
    }

    @Test
    public void testRealmDefaultClientScopes() {
        // Create 2 client scopes
        ClientScopeRepresentation scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("scope-def");
        scopeRep.setProtocol("openid-connect");
        String scopeDefId = createClientScope(scopeRep);
        getCleanup().addClientScopeId(scopeDefId);
        scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("scope-opt");
        scopeRep.setProtocol("openid-connect");
        String scopeOptId = createClientScope(scopeRep);
        getCleanup().addClientScopeId(scopeOptId);
        // Add scope-def as default and scope-opt as optional client scope
        testRealmResource().addDefaultDefaultClientScope(scopeDefId);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.defaultDefaultClientScopePath(scopeDefId), CLIENT_SCOPE);
        testRealmResource().addDefaultOptionalClientScope(scopeOptId);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.defaultOptionalClientScopePath(scopeOptId), CLIENT_SCOPE);
        // Ensure defaults and optional scopes are here
        List<String> realmDefaultScopes = getClientScopeNames(testRealmResource().getDefaultDefaultClientScopes());
        List<String> realmOptionalScopes = getClientScopeNames(testRealmResource().getDefaultOptionalClientScopes());
        Assert.assertTrue(realmDefaultScopes.contains("scope-def"));
        Assert.assertFalse(realmOptionalScopes.contains("scope-def"));
        Assert.assertFalse(realmDefaultScopes.contains("scope-opt"));
        Assert.assertTrue(realmOptionalScopes.contains("scope-opt"));
        // create client. Ensure that it has scope-def and scope-opt scopes assigned
        ClientRepresentation clientRep = new ClientRepresentation();
        clientRep.setClientId("bar-client");
        clientRep.setProtocol("openid-connect");
        String clientUuid = createClient(clientRep);
        getCleanup().addClientUuid(clientUuid);
        List<String> clientDefaultScopes = getClientScopeNames(testRealmResource().clients().get(clientUuid).getDefaultClientScopes());
        List<String> clientOptionalScopes = getClientScopeNames(testRealmResource().clients().get(clientUuid).getOptionalClientScopes());
        Assert.assertTrue(clientDefaultScopes.contains("scope-def"));
        Assert.assertFalse(clientOptionalScopes.contains("scope-def"));
        Assert.assertFalse(clientDefaultScopes.contains("scope-opt"));
        Assert.assertTrue(clientOptionalScopes.contains("scope-opt"));
        // Unassign scope-def and scope-opt from realm
        testRealmResource().removeDefaultDefaultClientScope(scopeDefId);
        assertAdminEvents.assertEvent(getRealmId(), DELETE, AdminEventPaths.defaultDefaultClientScopePath(scopeDefId), CLIENT_SCOPE);
        testRealmResource().removeDefaultOptionalClientScope(scopeOptId);
        assertAdminEvents.assertEvent(getRealmId(), DELETE, AdminEventPaths.defaultOptionalClientScopePath(scopeOptId), CLIENT_SCOPE);
        realmDefaultScopes = getClientScopeNames(testRealmResource().getDefaultDefaultClientScopes());
        realmOptionalScopes = getClientScopeNames(testRealmResource().getDefaultOptionalClientScopes());
        Assert.assertFalse(realmDefaultScopes.contains("scope-def"));
        Assert.assertFalse(realmOptionalScopes.contains("scope-def"));
        Assert.assertFalse(realmDefaultScopes.contains("scope-opt"));
        Assert.assertFalse(realmOptionalScopes.contains("scope-opt"));
        // Create another client. Check it doesn't have scope-def and scope-opt scopes assigned
        clientRep = new ClientRepresentation();
        clientRep.setClientId("bar-client-2");
        clientRep.setProtocol("openid-connect");
        clientUuid = createClient(clientRep);
        getCleanup().addClientUuid(clientUuid);
        clientDefaultScopes = getClientScopeNames(testRealmResource().clients().get(clientUuid).getDefaultClientScopes());
        clientOptionalScopes = getClientScopeNames(testRealmResource().clients().get(clientUuid).getOptionalClientScopes());
        Assert.assertFalse(clientDefaultScopes.contains("scope-def"));
        Assert.assertFalse(clientOptionalScopes.contains("scope-def"));
        Assert.assertFalse(clientDefaultScopes.contains("scope-opt"));
        Assert.assertFalse(clientOptionalScopes.contains("scope-opt"));
    }

    // KEYCLOAK-5863
    @Test
    public void testUpdateProtocolMappers() {
        ClientScopeRepresentation scopeRep = new ClientScopeRepresentation();
        scopeRep.setName("testUpdateProtocolMappers");
        scopeRep.setProtocol("openid-connect");
        String scopeId = createClientScope(scopeRep);
        ProtocolMapperRepresentation mapper = new ProtocolMapperRepresentation();
        mapper.setName("test");
        mapper.setProtocol("openid-connect");
        mapper.setProtocolMapper("oidc-usermodel-attribute-mapper");
        Map<String, String> m = new HashMap<>();
        m.put("user.attribute", "test");
        m.put("claim.name", "");
        m.put("jsonType.label", "");
        mapper.setConfig(m);
        ProtocolMappersResource protocolMappers = clientScopes().get(scopeId).getProtocolMappers();
        Response response = protocolMappers.createMapper(mapper);
        String mapperId = ApiUtil.getCreatedId(response);
        mapper = protocolMappers.getMapperById(mapperId);
        mapper.getConfig().put("claim.name", "claim");
        protocolMappers.update(mapperId, mapper);
        List<ProtocolMapperRepresentation> mappers = protocolMappers.getMappers();
        Assert.assertEquals(1, mappers.size());
        Assert.assertEquals(2, mappers.get(0).getConfig().size());
        Assert.assertEquals("test", mappers.get(0).getConfig().get("user.attribute"));
        Assert.assertEquals("claim", mappers.get(0).getConfig().get("claim.name"));
        clientScopes().get(scopeId).remove();
    }
}

