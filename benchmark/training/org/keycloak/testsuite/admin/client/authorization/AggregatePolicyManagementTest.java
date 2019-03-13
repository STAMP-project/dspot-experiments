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
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AggregatePoliciesResource;
import org.keycloak.admin.client.resource.AggregatePolicyResource;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.representations.idm.authorization.AggregatePolicyRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class AggregatePolicyManagementTest extends AbstractPolicyManagementTest {
    @Test
    public void testCreate() {
        AuthorizationResource authorization = getClient().authorization();
        AggregatePolicyRepresentation representation = new AggregatePolicyRepresentation();
        representation.setName("Aggregate Policy");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.addPolicy("Only Marta Policy", "Only Kolo Policy");
        assertCreated(authorization, representation);
    }

    @Test
    public void testUpdate() {
        AuthorizationResource authorization = getClient().authorization();
        AggregatePolicyRepresentation representation = new AggregatePolicyRepresentation();
        representation.setName("Update Aggregate Policy");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.addPolicy("Only Marta Policy", "Only Kolo Policy");
        assertCreated(authorization, representation);
        representation.setName("changed");
        representation.setDescription("changed");
        representation.setDecisionStrategy(AFFIRMATIVE);
        representation.setLogic(POSITIVE);
        representation.getPolicies().clear();
        representation.addPolicy("Only Kolo Policy");
        AggregatePoliciesResource policies = authorization.policies().aggregate();
        AggregatePolicyResource policy = policies.findById(representation.getId());
        policy.update(representation);
        assertRepresentation(representation, policy);
    }

    @Test
    public void testDelete() {
        AuthorizationResource authorization = getClient().authorization();
        AggregatePolicyRepresentation representation = new AggregatePolicyRepresentation();
        representation.setName("Test Delete Policy");
        representation.addPolicy("Only Marta Policy");
        AggregatePoliciesResource policies = authorization.policies().aggregate();
        try (Response response = policies.create(representation)) {
            AggregatePolicyRepresentation created = response.readEntity(AggregatePolicyRepresentation.class);
            policies.findById(created.getId()).remove();
            AggregatePolicyResource removed = policies.findById(created.getId());
            try {
                removed.toRepresentation();
                Assert.fail("Policy not removed");
            } catch (NotFoundException ignore) {
            }
        }
    }
}

