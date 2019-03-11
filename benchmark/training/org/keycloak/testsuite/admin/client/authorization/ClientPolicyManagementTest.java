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
import java.util.stream.Collectors;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.ClientPoliciesResource;
import org.keycloak.admin.client.resource.ClientPolicyResource;
import org.keycloak.admin.client.resource.ClientsResource;
import org.keycloak.admin.client.resource.PolicyResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.authorization.ClientPolicyRepresentation;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ClientPolicyManagementTest extends AbstractPolicyManagementTest {
    @Test
    public void testCreate() {
        AuthorizationResource authorization = getClient().authorization();
        ClientPolicyRepresentation representation = new ClientPolicyRepresentation();
        representation.setName("Realm Client Policy");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.addClient("Client A");
        representation.addClient("Client B");
        assertCreated(authorization, representation);
    }

    @Test
    public void testUpdate() {
        AuthorizationResource authorization = getClient().authorization();
        ClientPolicyRepresentation representation = new ClientPolicyRepresentation();
        representation.setName("Update Test Client Policy");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.addClient("Client A");
        representation.addClient("Client B");
        representation.addClient("Client C");
        assertCreated(authorization, representation);
        representation.setName("changed");
        representation.setDescription("changed");
        representation.setDecisionStrategy(AFFIRMATIVE);
        representation.setLogic(POSITIVE);
        representation.setClients(representation.getClients().stream().filter(( userName) -> !(userName.equals("Client A"))).collect(Collectors.toSet()));
        ClientPoliciesResource policies = authorization.policies().client();
        ClientPolicyResource permission = policies.findById(representation.getId());
        permission.update(representation);
        assertRepresentation(representation, permission);
        representation.setClients(representation.getClients().stream().filter(( userName) -> !(userName.equals("Client C"))).collect(Collectors.toSet()));
        permission.update(representation);
        assertRepresentation(representation, permission);
    }

    @Test
    public void testDelete() {
        AuthorizationResource authorization = getClient().authorization();
        ClientPolicyRepresentation representation = new ClientPolicyRepresentation();
        representation.setName("Test Delete Permission");
        representation.addClient("Client A");
        ClientPoliciesResource policies = authorization.policies().client();
        try (Response response = policies.create(representation)) {
            ClientPolicyRepresentation created = response.readEntity(ClientPolicyRepresentation.class);
            policies.findById(created.getId()).remove();
            ClientPolicyResource removed = policies.findById(created.getId());
            try {
                removed.toRepresentation();
                Assert.fail("Permission not removed");
            } catch (NotFoundException ignore) {
            }
        }
    }

    @Test
    public void testDeleteClient() {
        AuthorizationResource authorization = getClient().authorization();
        ClientPolicyRepresentation representation = new ClientPolicyRepresentation();
        representation.setName("Update Test Client Policy");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.addClient("Client D");
        representation.addClient("Client E");
        representation.addClient("Client F");
        assertCreated(authorization, representation);
        ClientsResource clients = getRealm().clients();
        ClientRepresentation client = clients.findByClientId("Client D").get(0);
        clients.get(client.getId()).remove();
        representation = authorization.policies().client().findById(representation.getId()).toRepresentation();
        Assert.assertEquals(2, representation.getClients().size());
        Assert.assertFalse(representation.getClients().contains(client.getId()));
        client = clients.findByClientId("Client E").get(0);
        clients.get(client.getId()).remove();
        representation = authorization.policies().client().findById(representation.getId()).toRepresentation();
        Assert.assertEquals(1, representation.getClients().size());
        Assert.assertFalse(representation.getClients().contains(client.getId()));
        client = clients.findByClientId("Client F").get(0);
        clients.get(client.getId()).remove();
        try {
            authorization.policies().client().findById(representation.getId()).toRepresentation();
            Assert.fail("Client policy should be removed");
        } catch (NotFoundException nfe) {
            // ignore
        }
    }

    @Test
    public void testGenericConfig() {
        AuthorizationResource authorization = getClient().authorization();
        ClientPolicyRepresentation representation = new ClientPolicyRepresentation();
        representation.setName("Test Generic Config Permission");
        representation.addClient("Client A");
        ClientPoliciesResource policies = authorization.policies().client();
        try (Response response = policies.create(representation)) {
            ClientPolicyRepresentation created = response.readEntity(ClientPolicyRepresentation.class);
            PolicyResource policy = authorization.policies().policy(created.getId());
            PolicyRepresentation genericConfig = policy.toRepresentation();
            Assert.assertNotNull(genericConfig.getConfig());
            Assert.assertNotNull(genericConfig.getConfig().get("clients"));
            ClientRepresentation user = getRealm().clients().findByClientId("Client A").get(0);
            Assert.assertTrue(genericConfig.getConfig().get("clients").contains(user.getId()));
        }
    }
}

