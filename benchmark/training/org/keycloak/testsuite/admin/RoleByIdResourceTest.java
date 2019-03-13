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


import OperationType.CREATE;
import OperationType.DELETE;
import OperationType.UPDATE;
import ResourceType.REALM_ROLE;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.NotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.RoleByIdResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.testsuite.util.AdminEventPaths;
import org.keycloak.testsuite.util.RoleBuilder;

import static org.keycloak.testsuite.Assert.assertNames;
import static org.keycloak.testsuite.Assert.assertRoleAttributes;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class RoleByIdResourceTest extends AbstractAdminTest {
    private RoleByIdResource resource;

    private Map<String, String> ids = new HashMap<>();

    private String clientUuid;

    @Test
    public void getRole() {
        RoleRepresentation role = resource.getRole(ids.get("role-a"));
        Assert.assertNotNull(role);
        Assert.assertEquals("role-a", role.getName());
        Assert.assertEquals("Role A", role.getDescription());
        Assert.assertFalse(role.isComposite());
    }

    @Test
    public void updateRole() {
        RoleRepresentation role = resource.getRole(ids.get("role-a"));
        role.setName("role-a-new");
        role.setDescription("Role A New");
        resource.updateRole(ids.get("role-a"), role);
        assertAdminEvents.assertEvent(realmId, UPDATE, AdminEventPaths.roleByIdResourcePath(ids.get("role-a")), role, REALM_ROLE);
        role = resource.getRole(ids.get("role-a"));
        Assert.assertNotNull(role);
        Assert.assertEquals("role-a-new", role.getName());
        Assert.assertEquals("Role A New", role.getDescription());
        Assert.assertFalse(role.isComposite());
    }

    @Test
    public void deleteRole() {
        Assert.assertNotNull(resource.getRole(ids.get("role-a")));
        resource.deleteRole(ids.get("role-a"));
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.roleByIdResourcePath(ids.get("role-a")), REALM_ROLE);
        try {
            resource.getRole(ids.get("role-a"));
            Assert.fail("Expected 404");
        } catch (NotFoundException e) {
        }
    }

    @Test
    public void composites() {
        Assert.assertFalse(resource.getRole(ids.get("role-a")).isComposite());
        Assert.assertEquals(0, resource.getRoleComposites(ids.get("role-a")).size());
        List<RoleRepresentation> l = new LinkedList<>();
        l.add(RoleBuilder.create().id(ids.get("role-b")).build());
        l.add(RoleBuilder.create().id(ids.get("role-c")).build());
        resource.addComposites(ids.get("role-a"), l);
        assertAdminEvents.assertEvent(realmId, CREATE, AdminEventPaths.roleByIdResourceCompositesPath(ids.get("role-a")), l, REALM_ROLE);
        Set<RoleRepresentation> composites = resource.getRoleComposites(ids.get("role-a"));
        Assert.assertTrue(resource.getRole(ids.get("role-a")).isComposite());
        assertNames(composites, "role-b", "role-c");
        Set<RoleRepresentation> realmComposites = resource.getRealmRoleComposites(ids.get("role-a"));
        assertNames(realmComposites, "role-b");
        Set<RoleRepresentation> clientComposites = resource.getClientRoleComposites(ids.get("role-a"), clientUuid);
        assertNames(clientComposites, "role-c");
        resource.deleteComposites(ids.get("role-a"), l);
        assertAdminEvents.assertEvent(realmId, DELETE, AdminEventPaths.roleByIdResourceCompositesPath(ids.get("role-a")), l, REALM_ROLE);
        Assert.assertFalse(resource.getRole(ids.get("role-a")).isComposite());
        Assert.assertEquals(0, resource.getRoleComposites(ids.get("role-a")).size());
    }

    @Test
    public void attributes() {
        for (String id : ids.values()) {
            RoleRepresentation role = resource.getRole(id);
            Assert.assertNotNull(role.getAttributes());
            Assert.assertTrue(role.getAttributes().isEmpty());
            // update the role with attributes
            Map<String, List<String>> attributes = new HashMap<>();
            List<String> attributeValues = new ArrayList<>();
            attributeValues.add("value1");
            attributes.put("key1", attributeValues);
            attributeValues = new ArrayList<>();
            attributeValues.add("value2.1");
            attributeValues.add("value2.2");
            attributes.put("key2", attributeValues);
            role.setAttributes(attributes);
            resource.updateRole(id, role);
            role = resource.getRole(id);
            Assert.assertNotNull(role);
            Map<String, List<String>> roleAttributes = role.getAttributes();
            Assert.assertNotNull(roleAttributes);
            assertRoleAttributes(attributes, roleAttributes);
            // delete an attribute
            attributes.remove("key2");
            role.setAttributes(attributes);
            resource.updateRole(id, role);
            role = resource.getRole(id);
            Assert.assertNotNull(role);
            roleAttributes = role.getAttributes();
            Assert.assertNotNull(roleAttributes);
            assertRoleAttributes(attributes, roleAttributes);
        }
    }
}

