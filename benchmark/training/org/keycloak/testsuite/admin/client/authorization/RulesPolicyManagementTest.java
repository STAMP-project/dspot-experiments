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
import Logic.POSITIVE;
import Profile.Feature.AUTHZ_DROOLS_POLICY;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.AuthorizationResource;
import org.keycloak.admin.client.resource.RulePoliciesResource;
import org.keycloak.admin.client.resource.RulePolicyResource;
import org.keycloak.representations.idm.authorization.RulePolicyRepresentation;
import org.keycloak.testsuite.ProfileAssume;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class RulesPolicyManagementTest extends AbstractPolicyManagementTest {
    @Test
    public void testCreate() {
        ProfileAssume.assumeFeatureEnabled(AUTHZ_DROOLS_POLICY);
        assertCreated(getClient().authorization(), createDefaultRepresentation("Rule Policy"));
    }

    @Test
    public void testUpdate() {
        ProfileAssume.assumeFeatureEnabled(AUTHZ_DROOLS_POLICY);
        AuthorizationResource authorization = getClient().authorization();
        RulePolicyRepresentation representation = createDefaultRepresentation("Update Rule Policy");
        assertCreated(authorization, representation);
        representation.setName("changed");
        representation.setDescription("changed");
        representation.setDecisionStrategy(AFFIRMATIVE);
        representation.setLogic(POSITIVE);
        representation.setScannerPeriod("12");
        representation.setScannerPeriodUnit("Days");
        representation.setModuleName("PhotozAuthzContextualPolicy");
        representation.setSessionName("MainContextualSession");
        RulePoliciesResource policies = authorization.policies().rule();
        RulePolicyResource policy = policies.findById(representation.getId());
        policy.update(representation);
        assertRepresentation(representation, policy);
    }

    @Test
    public void testDelete() {
        ProfileAssume.assumeFeatureEnabled(AUTHZ_DROOLS_POLICY);
        AuthorizationResource authorization = getClient().authorization();
        RulePolicyRepresentation representation = createDefaultRepresentation("Delete Rule Policy");
        RulePoliciesResource policies = authorization.policies().rule();
        try (Response response = policies.create(representation)) {
            RulePolicyRepresentation created = response.readEntity(RulePolicyRepresentation.class);
            policies.findById(created.getId()).remove();
            RulePolicyResource removed = policies.findById(created.getId());
            try {
                removed.toRepresentation();
                Assert.fail("Policy not removed");
            } catch (NotFoundException ignore) {
            }
        }
    }
}

