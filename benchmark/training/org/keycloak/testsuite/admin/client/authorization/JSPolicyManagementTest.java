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
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.JSPoliciesResource;
import org.keycloak.admin.client.resource.JSPolicyResource;
import org.keycloak.representations.idm.authorization.JSPolicyRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class JSPolicyManagementTest extends AbstractPolicyManagementTest {
    @Test
    public void testCreate() {
        AuthorizationResource authorization = getClient().authorization();
        JSPolicyRepresentation representation = new JSPolicyRepresentation();
        representation.setName("JS Policy");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.setCode("$evaluation.grant();");
        assertCreated(authorization, representation);
    }

    @Test
    public void testUpdate() {
        AuthorizationResource authorization = getClient().authorization();
        JSPolicyRepresentation representation = new JSPolicyRepresentation();
        representation.setName("Update JS Policy");
        representation.setDescription("description");
        representation.setDecisionStrategy(CONSENSUS);
        representation.setLogic(NEGATIVE);
        representation.setCode("$evaluation.grant();");
        assertCreated(authorization, representation);
        representation.setName("changed");
        representation.setDescription("changed");
        representation.setDecisionStrategy(AFFIRMATIVE);
        representation.setLogic(POSITIVE);
        representation.setCode("$evaluation.deny()");
        JSPoliciesResource policies = authorization.policies().js();
        JSPolicyResource permission = policies.findById(representation.getId());
        permission.update(representation);
        assertRepresentation(representation, permission);
    }

    @Test
    public void testDelete() {
        AuthorizationResource authorization = getClient().authorization();
        JSPolicyRepresentation representation = new JSPolicyRepresentation();
        representation.setName("Test Delete Policy");
        representation.setCode("$evaluation.grant()");
        JSPoliciesResource policies = authorization.policies().js();
        try (Response response = policies.create(representation)) {
            JSPolicyRepresentation created = response.readEntity(JSPolicyRepresentation.class);
            policies.findById(created.getId()).remove();
            JSPolicyResource removed = policies.findById(created.getId());
            try {
                removed.toRepresentation();
                Assert.fail("Permission not removed");
            } catch (NotFoundException ignore) {
            }
        }
    }
}

