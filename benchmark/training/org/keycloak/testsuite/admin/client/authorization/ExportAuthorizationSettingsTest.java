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
package org.keycloak.testsuite.admin.client.authorization;


import Status.CREATED;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ResourceServerRepresentation;

import static org.keycloak.testsuite.util.ClientBuilder.create;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlasta Ramik</a>
 */
public class ExportAuthorizationSettingsTest extends AbstractAuthorizationTest {
    // KEYCLOAK-4341
    @Test
    public void testResourceBasedPermission() throws Exception {
        String permissionName = "resource-based-permission";
        ClientResource clientResource = getClientResource();
        AuthorizationResource authorizationResource = clientResource.authorization();
        // get Default Resource
        List<ResourceRepresentation> resources = authorizationResource.resources().findByName("Default Resource");
        org.keycloak.testsuite.Assert.assertTrue(((resources.size()) == 1));
        ResourceRepresentation resource = resources.get(0);
        // get Default Policy
        PolicyRepresentation policy = authorizationResource.policies().findByName("Default Policy");
        // create Resource-based permission and add default policy/resource
        ResourcePermissionRepresentation permission = new ResourcePermissionRepresentation();
        permission.setName(permissionName);
        permission.addPolicy(policy.getId());
        permission.addResource(resource.getId());
        Response create = authorizationResource.permissions().resource().create(permission);
        try {
            org.keycloak.testsuite.Assert.assertEquals(CREATED, create.getStatusInfo());
        } finally {
            create.close();
        }
        // export authorization settings
        ResourceServerRepresentation exportSettings = authorizationResource.exportSettings();
        // check exported settings contains both resources/applyPolicies
        boolean found = false;
        for (PolicyRepresentation p : exportSettings.getPolicies()) {
            if (p.getName().equals(permissionName)) {
                found = true;
                org.keycloak.testsuite.Assert.assertEquals("[\"Default Resource\"]", p.getConfig().get("resources"));
                org.keycloak.testsuite.Assert.assertEquals("[\"Default Policy\"]", p.getConfig().get("applyPolicies"));
            }
        }
        org.keycloak.testsuite.Assert.assertTrue("Permission \"role-based-permission\" was not found.", found);
    }

    // KEYCLOAK-4340
    @Test
    public void testRoleBasedPolicy() {
        ClientResource clientResource = getClientResource();
        AuthorizationResource authorizationResource = clientResource.authorization();
        ClientRepresentation account = testRealmResource().clients().findByClientId("account").get(0);
        RoleRepresentation role = testRealmResource().clients().get(account.getId()).roles().get("view-profile").toRepresentation();
        PolicyRepresentation policy = new PolicyRepresentation();
        policy.setName("role-based-policy");
        policy.setType("role");
        Map<String, String> config = new HashMap<>();
        config.put("roles", (("[{\"id\":\"" + (role.getId())) + "\"}]"));
        policy.setConfig(config);
        Response create = authorizationResource.policies().create(policy);
        try {
            org.keycloak.testsuite.Assert.assertEquals(CREATED, create.getStatusInfo());
        } finally {
            create.close();
        }
        // this call was messing up with DB, see KEYCLOAK-4340
        authorizationResource.exportSettings();
        // this call failed with NPE
        authorizationResource.exportSettings();
    }

    // KEYCLOAK-4983
    @Test
    public void testRoleBasedPolicyWithMultipleRoles() {
        ClientResource clientResource = getClientResource();
        AuthorizationResource authorizationResource = clientResource.authorization();
        testRealmResource().clients().create(create().clientId("test-client-1").defaultRoles("client-role").build()).close();
        testRealmResource().clients().create(create().clientId("test-client-2").defaultRoles("client-role").build()).close();
        ClientRepresentation client1 = getClientByClientId("test-client-1");
        ClientRepresentation client2 = getClientByClientId("test-client-2");
        RoleRepresentation role1 = testRealmResource().clients().get(client1.getId()).roles().get("client-role").toRepresentation();
        RoleRepresentation role2 = testRealmResource().clients().get(client2.getId()).roles().get("client-role").toRepresentation();
        PolicyRepresentation policy = new PolicyRepresentation();
        policy.setName("role-based-policy");
        policy.setType("role");
        Map<String, String> config = new HashMap<>();
        config.put("roles", (((("[{\"id\":\"" + (role1.getId())) + "\"},{\"id\":\"") + (role2.getId())) + "\"}]"));
        policy.setConfig(config);
        Response create = authorizationResource.policies().create(policy);
        try {
            org.keycloak.testsuite.Assert.assertEquals(CREATED, create.getStatusInfo());
        } finally {
            create.close();
        }
        // export authorization settings
        ResourceServerRepresentation exportSettings = authorizationResource.exportSettings();
        boolean found = false;
        for (PolicyRepresentation p : exportSettings.getPolicies()) {
            if (p.getName().equals("role-based-policy")) {
                found = true;
                org.keycloak.testsuite.Assert.assertTrue(((p.getConfig().get("roles").contains("test-client-1/client-role")) && (p.getConfig().get("roles").contains("test-client-2/client-role"))));
            }
        }
        if (!found) {
            org.keycloak.testsuite.Assert.fail("Policy \"role-based-policy\" was not found in exported settings.");
        }
    }
}

