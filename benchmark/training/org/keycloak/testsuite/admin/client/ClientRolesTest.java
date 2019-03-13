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
package org.keycloak.testsuite.admin.client;


import OperationType.CREATE;
import OperationType.DELETE;
import ResourceType.CLIENT_ROLE;
import ResourceType.REALM_ROLE;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.util.AdminEventPaths;

import static org.keycloak.testsuite.Assert.assertNames;


/**
 *
 *
 * @author Stan Silvert ssilvert@redhat.com (C) 2016 Red Hat Inc.
 */
public class ClientRolesTest extends AbstractClientTest {
    private ClientResource clientRsc;

    private String clientDbId;

    private RolesResource rolesRsc;

    @Test
    public void testAddRole() {
        RoleRepresentation role1 = makeRole("role1");
        rolesRsc.create(role1);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientRoleResourcePath(clientDbId, "role1"), role1, CLIENT_ROLE);
        Assert.assertTrue(hasRole(rolesRsc, "role1"));
    }

    @Test
    public void testRemoveRole() {
        RoleRepresentation role2 = makeRole("role2");
        rolesRsc.create(role2);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientRoleResourcePath(clientDbId, "role2"), role2, CLIENT_ROLE);
        rolesRsc.deleteRole("role2");
        assertAdminEvents.assertEvent(getRealmId(), DELETE, AdminEventPaths.clientRoleResourcePath(clientDbId, "role2"), CLIENT_ROLE);
        Assert.assertFalse(hasRole(rolesRsc, "role2"));
    }

    @Test
    public void testComposites() {
        RoleRepresentation roleA = makeRole("role-a");
        rolesRsc.create(roleA);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientRoleResourcePath(clientDbId, "role-a"), roleA, CLIENT_ROLE);
        Assert.assertFalse(rolesRsc.get("role-a").toRepresentation().isComposite());
        Assert.assertEquals(0, rolesRsc.get("role-a").getRoleComposites().size());
        RoleRepresentation roleB = makeRole("role-b");
        rolesRsc.create(roleB);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientRoleResourcePath(clientDbId, "role-b"), roleB, CLIENT_ROLE);
        RoleRepresentation roleC = makeRole("role-c");
        testRealmResource().roles().create(roleC);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.roleResourcePath("role-c"), roleC, REALM_ROLE);
        List<RoleRepresentation> l = new LinkedList<>();
        l.add(rolesRsc.get("role-b").toRepresentation());
        l.add(testRealmResource().roles().get("role-c").toRepresentation());
        rolesRsc.get("role-a").addComposites(l);
        assertAdminEvents.assertEvent(getRealmId(), CREATE, AdminEventPaths.clientRoleResourceCompositesPath(clientDbId, "role-a"), l, CLIENT_ROLE);
        Set<RoleRepresentation> composites = rolesRsc.get("role-a").getRoleComposites();
        Assert.assertTrue(rolesRsc.get("role-a").toRepresentation().isComposite());
        assertNames(composites, "role-b", "role-c");
        Set<RoleRepresentation> realmComposites = rolesRsc.get("role-a").getRealmRoleComposites();
        assertNames(realmComposites, "role-c");
        Set<RoleRepresentation> clientComposites = rolesRsc.get("role-a").getClientRoleComposites(clientRsc.toRepresentation().getId());
        assertNames(clientComposites, "role-b");
        rolesRsc.get("role-a").deleteComposites(l);
        assertAdminEvents.assertEvent(getRealmId(), DELETE, AdminEventPaths.clientRoleResourceCompositesPath(clientDbId, "role-a"), l, CLIENT_ROLE);
        Assert.assertFalse(rolesRsc.get("role-a").toRepresentation().isComposite());
        Assert.assertEquals(0, rolesRsc.get("role-a").getRoleComposites().size());
    }
}

