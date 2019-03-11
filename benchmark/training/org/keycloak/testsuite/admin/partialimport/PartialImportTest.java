/**
 * Copyright 2016 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @author tags. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.keycloak.testsuite.admin.partialimport;


import OperationType.CREATE;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.IdentityProviderResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.partialimport.PartialImportResult;
import org.keycloak.partialimport.PartialImportResults;
import org.keycloak.representations.idm.AdminEventRepresentation;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.IdentityProviderRepresentation;
import org.keycloak.representations.idm.PartialImportRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.RolesRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.util.AssertAdminEvents;


/**
 * Tests for the partial import endpoint in admin client.  Also tests the
 * server side functionality of each resource along with "fail, skip, overwrite"
 * functions.
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class PartialImportTest extends AbstractAuthTest {
    @Rule
    public AssertAdminEvents assertAdminEvents = new AssertAdminEvents(this);

    private static final int NUM_RESOURCE_TYPES = 6;

    private static final String CLIENT_ROLES_CLIENT = "clientRolesClient";

    private static final String USER_PREFIX = "user";

    private static final String GROUP_PREFIX = "group";

    private static final String CLIENT_PREFIX = "client";

    private static final String REALM_ROLE_PREFIX = "realmRole";

    private static final String CLIENT_ROLE_PREFIX = "clientRole";

    private static final String[] IDP_ALIASES = new String[]{ "twitter", "github", "facebook", "google", "linkedin", "microsoft", "stackoverflow" };

    private static final int NUM_ENTITIES = PartialImportTest.IDP_ALIASES.length;

    private PartialImportRepresentation piRep;

    private String realmId;

    @Test
    public void testAddUsers() {
        assertAdminEvents.clear();
        setFail();
        addUsers();
        PartialImportResults results = doImport();
        Assert.assertEquals(PartialImportTest.NUM_ENTITIES, results.getAdded());
        // Need to do this way as admin events from partial import are unsorted
        Set<String> userIds = new HashSet<>();
        for (int i = 0; i < (PartialImportTest.NUM_ENTITIES); i++) {
            AdminEventRepresentation adminEvent = assertAdminEvents.poll();
            org.keycloak.testsuite.Assert.assertEquals(realmId, adminEvent.getRealmId());
            org.keycloak.testsuite.Assert.assertEquals(CREATE.name(), adminEvent.getOperationType());
            org.keycloak.testsuite.Assert.assertTrue(adminEvent.getResourcePath().startsWith("users/"));
            String userId = adminEvent.getResourcePath().substring(6);
            userIds.add(userId);
        }
        assertAdminEvents.assertEmpty();
        for (PartialImportResult result : results.getResults()) {
            String id = result.getId();
            UserResource userRsc = testRealmResource().users().get(id);
            UserRepresentation user = userRsc.toRepresentation();
            Assert.assertTrue(user.getUsername().startsWith(PartialImportTest.USER_PREFIX));
            org.keycloak.testsuite.Assert.assertTrue(userIds.contains(id));
        }
    }

    @Test
    public void testAddUsersWithDuplicateEmailsForbidden() {
        assertAdminEvents.clear();
        setFail();
        addUsers();
        UserRepresentation user = AbstractAuthTest.createUserRepresentation(((PartialImportTest.USER_PREFIX) + 999), (((PartialImportTest.USER_PREFIX) + 1) + "@foo.com"), "foo", "bar", true);
        piRep.getUsers().add(user);
        Response response = testRealmResource().partialImport(piRep);
        Assert.assertEquals(409, response.getStatus());
    }

    @Test
    public void testAddUsersWithDuplicateEmailsAllowed() {
        RealmRepresentation realmRep = testRealmResource().toRepresentation();
        realmRep.setDuplicateEmailsAllowed(true);
        testRealmResource().update(realmRep);
        assertAdminEvents.clear();
        setFail();
        addUsers();
        doImport();
        UserRepresentation user = AbstractAuthTest.createUserRepresentation(((PartialImportTest.USER_PREFIX) + 999), (((PartialImportTest.USER_PREFIX) + 1) + "@foo.com"), "foo", "bar", true);
        piRep.setUsers(Arrays.asList(user));
        PartialImportResults results = doImport();
        Assert.assertEquals(1, results.getAdded());
    }

    @Test
    public void testAddUsersWithTermsAndConditions() {
        assertAdminEvents.clear();
        setFail();
        addUsersWithTermsAndConditions();
        PartialImportResults results = doImport();
        Assert.assertEquals(PartialImportTest.NUM_ENTITIES, results.getAdded());
        // Need to do this way as admin events from partial import are unsorted
        Set<String> userIds = new HashSet<>();
        for (int i = 0; i < (PartialImportTest.NUM_ENTITIES); i++) {
            AdminEventRepresentation adminEvent = assertAdminEvents.poll();
            org.keycloak.testsuite.Assert.assertEquals(realmId, adminEvent.getRealmId());
            org.keycloak.testsuite.Assert.assertEquals(CREATE.name(), adminEvent.getOperationType());
            org.keycloak.testsuite.Assert.assertTrue(adminEvent.getResourcePath().startsWith("users/"));
            String userId = adminEvent.getResourcePath().substring(6);
            userIds.add(userId);
        }
        assertAdminEvents.assertEmpty();
        for (PartialImportResult result : results.getResults()) {
            String id = result.getId();
            UserResource userRsc = testRealmResource().users().get(id);
            UserRepresentation user = userRsc.toRepresentation();
            Assert.assertTrue(user.getUsername().startsWith(PartialImportTest.USER_PREFIX));
            org.keycloak.testsuite.Assert.assertTrue(userIds.contains(id));
        }
    }

    @Test
    public void testAddClients() {
        setFail();
        addClients();
        PartialImportResults results = doImport();
        Assert.assertEquals(PartialImportTest.NUM_ENTITIES, results.getAdded());
        for (PartialImportResult result : results.getResults()) {
            String id = result.getId();
            ClientResource clientRsc = testRealmResource().clients().get(id);
            ClientRepresentation client = clientRsc.toRepresentation();
            Assert.assertTrue(client.getName().startsWith(PartialImportTest.CLIENT_PREFIX));
        }
    }

    @Test
    public void testAddProviders() {
        setFail();
        addProviders();
        PartialImportResults results = doImport();
        Assert.assertEquals(PartialImportTest.IDP_ALIASES.length, results.getAdded());
        for (PartialImportResult result : results.getResults()) {
            String id = result.getId();
            IdentityProviderResource idpRsc = testRealmResource().identityProviders().get(id);
            IdentityProviderRepresentation idp = idpRsc.toRepresentation();
            Map<String, String> config = idp.getConfig();
            Assert.assertTrue(Arrays.asList(PartialImportTest.IDP_ALIASES).contains(config.get("clientId")));
        }
    }

    @Test
    public void testAddRealmRoles() {
        setFail();
        addRealmRoles();
        PartialImportResults results = doImport();
        Assert.assertEquals(PartialImportTest.NUM_ENTITIES, results.getAdded());
        for (PartialImportResult result : results.getResults()) {
            String name = result.getResourceName();
            RoleResource roleRsc = testRealmResource().roles().get(name);
            RoleRepresentation role = roleRsc.toRepresentation();
            Assert.assertTrue(role.getName().startsWith(PartialImportTest.REALM_ROLE_PREFIX));
        }
    }

    @Test
    public void testAddClientRoles() {
        setFail();
        addClientRoles();
        PartialImportResults results = doImport();
        Assert.assertEquals(PartialImportTest.NUM_ENTITIES, results.getAdded());
        List<RoleRepresentation> clientRoles = clientRolesClient().roles().list();
        Assert.assertEquals(PartialImportTest.NUM_ENTITIES, clientRoles.size());
        for (RoleRepresentation roleRep : clientRoles) {
            Assert.assertTrue(roleRep.getName().startsWith(PartialImportTest.CLIENT_ROLE_PREFIX));
        }
    }

    @Test
    public void testAddUsersFail() {
        addUsers();
        testFail();
    }

    @Test
    public void testAddGroupsFail() {
        addGroups();
        testFail();
    }

    @Test
    public void testAddClientsFail() {
        addClients();
        testFail();
    }

    @Test
    public void testAddProvidersFail() {
        addProviders();
        testFail();
    }

    @Test
    public void testAddRealmRolesFail() {
        addRealmRoles();
        testFail();
    }

    @Test
    public void testAddClientRolesFail() {
        addClientRoles();
        testFail();
    }

    @Test
    public void testAddUsersSkip() {
        addUsers();
        testSkip();
    }

    @Test
    public void testAddGroupsSkip() {
        addGroups();
        testSkip();
    }

    @Test
    public void testAddClientsSkip() {
        addClients();
        testSkip();
    }

    @Test
    public void testAddProvidersSkip() {
        addProviders();
        testSkip();
    }

    @Test
    public void testAddRealmRolesSkip() {
        addRealmRoles();
        testSkip();
    }

    @Test
    public void testAddClientRolesSkip() {
        addClientRoles();
        testSkip();
    }

    @Test
    public void testAddUsersOverwrite() {
        addUsers();
        testOverwrite();
    }

    @Test
    public void testAddGroupsOverwrite() {
        addGroups();
        testOverwrite();
    }

    @Test
    public void testAddClientsOverwrite() {
        addClients();
        testOverwrite();
    }

    @Test
    public void testAddProvidersOverwrite() {
        addProviders();
        testOverwrite();
    }

    @Test
    public void testAddRealmRolesOverwrite() {
        addRealmRoles();
        testOverwrite();
    }

    @Test
    public void testAddClientRolesOverwrite() {
        addClientRoles();
        testOverwrite();
    }

    @Test
    public void testEverythingFail() {
        setFail();
        importEverything();
        PartialImportResults results = doImport();// second import will fail because not allowed to skip or overwrite

        Assert.assertNotNull(results.getErrorMessage());
    }

    @Test
    public void testEverythingSkip() {
        setSkip();
        importEverything();
        PartialImportResults results = doImport();
        Assert.assertEquals(((PartialImportTest.NUM_ENTITIES) * (PartialImportTest.NUM_RESOURCE_TYPES)), results.getSkipped());
    }

    @Test
    public void testEverythingOverwrite() {
        setOverwrite();
        importEverything();
        PartialImportResults results = doImport();
        Assert.assertEquals(((PartialImportTest.NUM_ENTITIES) * (PartialImportTest.NUM_RESOURCE_TYPES)), results.getOverwritten());
    }

    // KEYCLOAK-3042
    @Test
    public void testOverwriteExistingClientWithRoles() {
        setOverwrite();
        ClientRepresentation client = adminClient.realm(MASTER).clients().findByClientId("broker").get(0);
        List<RoleRepresentation> clientRoles = adminClient.realm(MASTER).clients().get(client.getId()).roles().list();
        Map<String, List<RoleRepresentation>> clients = new HashMap<>();
        clients.put(client.getClientId(), clientRoles);
        RolesRepresentation roles = new RolesRepresentation();
        roles.setClient(clients);
        piRep.setClients(Arrays.asList(client));
        piRep.setRoles(roles);
        doImport();
    }

    // KEYCLOAK-6058
    @Test
    public void testOverwriteExistingInternalClient() {
        setOverwrite();
        ClientRepresentation client = adminClient.realm(MASTER).clients().findByClientId("security-admin-console").get(0);
        ClientRepresentation client2 = adminClient.realm(MASTER).clients().findByClientId("master-realm").get(0);
        piRep.setClients(Arrays.asList(client, client2));
        PartialImportResults result = doImport();
        org.keycloak.testsuite.Assert.assertEquals(0, result.getOverwritten());
    }
}

