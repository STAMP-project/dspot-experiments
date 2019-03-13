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


import DecisionStrategy.AFFIRMATIVE;
import DecisionStrategy.CONSENSUS;
import Logic.NEGATIVE;
import Logic.POSITIVE;
import RolePolicyRepresentation.RoleDefinition;
import java.util.stream.Collectors;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.PolicyResource;
import org.keycloak.admin.client.resource.RolePoliciesResource;
import org.keycloak.admin.client.resource.RolePolicyResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class RolePolicyManagementTest extends AbstractPolicyManagementTest {
    @Test
    public void testCreateRealmRolePolicy() {
        AuthorizationResource authorization = getClient().authorization();
        RolePolicyRepresentation representation = new RolePolicyRepresentation();
        representation.setName("Realm Role Policy");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.addRole("Role A", false);
        representation.addRole("Role B", true);
        assertCreated(authorization, representation);
    }

    @Test
    public void testCreateClientRolePolicy() {
        ClientResource client = getClient();
        AuthorizationResource authorization = client.authorization();
        RolePolicyRepresentation representation = new RolePolicyRepresentation();
        representation.setName("Realm Client Role Policy");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        RolesResource roles = client.roles();
        roles.create(new RoleRepresentation("Client Role A", "desc", false));
        ClientRepresentation clientRep = client.toRepresentation();
        roles.create(new RoleRepresentation("Client Role B", "desc", false));
        representation.addRole("Client Role A");
        representation.addClientRole(clientRep.getClientId(), "Client Role B", true);
        assertCreated(authorization, representation);
    }

    @Test
    public void testUpdate() {
        AuthorizationResource authorization = getClient().authorization();
        RolePolicyRepresentation representation = new RolePolicyRepresentation();
        representation.setName("Update Test Role Policy");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.addRole("Role A", false);
        representation.addRole("Role B", true);
        representation.addRole("Role C", false);
        assertCreated(authorization, representation);
        representation.setName("changed");
        representation.setDescription("changed");
        representation.setDecisionStrategy(AFFIRMATIVE);
        representation.setLogic(POSITIVE);
        representation.setRoles(representation.getRoles().stream().filter(( roleDefinition) -> !(roleDefinition.getId().equals("Resource A"))).collect(Collectors.toSet()));
        RolePoliciesResource policies = authorization.policies().role();
        RolePolicyResource permission = policies.findById(representation.getId());
        permission.update(representation);
        assertRepresentation(representation, permission);
        for (RolePolicyRepresentation.RoleDefinition roleDefinition : representation.getRoles()) {
            if (roleDefinition.getId().equals("Role B")) {
                roleDefinition.setRequired(false);
            }
            if (roleDefinition.getId().equals("Role C")) {
                roleDefinition.setRequired(true);
            }
        }
        permission.update(representation);
        assertRepresentation(representation, permission);
    }

    @Test
    public void testDelete() {
        AuthorizationResource authorization = getClient().authorization();
        RolePolicyRepresentation representation = new RolePolicyRepresentation();
        representation.setName("Test Delete Permission");
        representation.addRole("Role A", false);
        RolePoliciesResource policies = authorization.policies().role();
        try (Response response = policies.create(representation)) {
            RolePolicyRepresentation created = response.readEntity(RolePolicyRepresentation.class);
            policies.findById(created.getId()).remove();
            RolePolicyResource removed = policies.findById(created.getId());
            try {
                removed.toRepresentation();
                Assert.fail("Permission not removed");
            } catch (NotFoundException ignore) {
            }
        }
    }

    @Test
    public void testGenericConfig() {
        AuthorizationResource authorization = getClient().authorization();
        RolePolicyRepresentation representation = new RolePolicyRepresentation();
        representation.setName("Test Generic Config  Permission");
        representation.addRole("Role A", false);
        RolePoliciesResource policies = authorization.policies().role();
        try (Response response = policies.create(representation)) {
            RolePolicyRepresentation created = response.readEntity(RolePolicyRepresentation.class);
            PolicyResource policy = authorization.policies().policy(created.getId());
            PolicyRepresentation genericConfig = policy.toRepresentation();
            Assert.assertNotNull(genericConfig.getConfig());
            Assert.assertNotNull(genericConfig.getConfig().get("roles"));
            RoleRepresentation role = getRealm().roles().get("Role A").toRepresentation();
            Assert.assertTrue(genericConfig.getConfig().get("roles").contains(role.getId()));
        }
    }
}

