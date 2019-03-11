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
import DecisionStrategy.UNANIMOUS;
import Logic.NEGATIVE;
import Logic.POSITIVE;
import Profile.Feature.AUTHZ_DROOLS_POLICY;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.PolicyResource;
import org.keycloak.common.Profile;
import org.keycloak.representations.idm.authorization.PolicyRepresentation;
import org.keycloak.representations.idm.authorization.ResourceRepresentation;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class GenericPolicyManagementTest extends AbstractAuthorizationTest {
    private static final String[] EXPECTED_BUILTIN_POLICY_PROVIDERS = new String[]{ "test", "user", "role", "rules", "js", "time", "aggregate", "scope", "resource" };

    @Test
    public void testCreate() {
        PolicyRepresentation newPolicy = createTestingPolicy().toRepresentation();
        Assert.assertEquals("Test Generic Policy", newPolicy.getName());
        Assert.assertEquals("scope", newPolicy.getType());
        Assert.assertEquals(POSITIVE, newPolicy.getLogic());
        Assert.assertEquals(UNANIMOUS, newPolicy.getDecisionStrategy());
        Assert.assertEquals("configuration for A", newPolicy.getConfig().get("configA"));
        Assert.assertEquals("configuration for B", newPolicy.getConfig().get("configB"));
        Assert.assertEquals("configuration for C", newPolicy.getConfig().get("configC"));
        List<PolicyRepresentation> policies = getClientResource().authorization().policies().policies();
        Assert.assertEquals(6, policies.size());
        assertAssociatedPolicy("Test Associated A", newPolicy);
        assertAssociatedPolicy("Test Associated B", newPolicy);
        assertAssociatedPolicy("Test Associated C", newPolicy);
        assertAssociatedResource("Test Resource A", newPolicy);
        assertAssociatedResource("Test Resource B", newPolicy);
        assertAssociatedResource("Test Resource C", newPolicy);
        assertAssociatedScope("Test Scope A", newPolicy);
        assertAssociatedScope("Test Scope B", newPolicy);
        assertAssociatedScope("Test Scope C", newPolicy);
    }

    @Test
    public void testUpdate() {
        PolicyResource policyResource = createTestingPolicy();
        PolicyRepresentation policy = policyResource.toRepresentation();
        policy.setName("changed");
        policy.setLogic(NEGATIVE);
        policy.setDecisionStrategy(AFFIRMATIVE);
        policy.getConfig().put("configA", "changed configuration for A");
        policy.getConfig().remove("configB");
        policy.getConfig().put("configC", "changed configuration for C");
        policyResource.update(policy);
        policy = policyResource.toRepresentation();
        Assert.assertEquals("changed", policy.getName());
        Assert.assertEquals(NEGATIVE, policy.getLogic());
        Assert.assertEquals(AFFIRMATIVE, policy.getDecisionStrategy());
        Assert.assertEquals("changed configuration for A", policy.getConfig().get("configA"));
        Assert.assertNull(policy.getConfig().get("configB"));
        Assert.assertEquals("changed configuration for C", policy.getConfig().get("configC"));
        Map<String, String> config = policy.getConfig();
        config.put("applyPolicies", buildConfigOption(findPolicyByName("Test Associated C").getId()));
        config.put("resources", buildConfigOption(findResourceByName("Test Resource B").getId()));
        config.put("scopes", buildConfigOption(findScopeByName("Test Scope A").getId()));
        policyResource.update(policy);
        policy = policyResource.toRepresentation();
        config = policy.getConfig();
        assertAssociatedPolicy("Test Associated C", policy);
        List<PolicyRepresentation> associatedPolicies = getClientResource().authorization().policies().policy(policy.getId()).associatedPolicies();
        Assert.assertFalse(associatedPolicies.stream().filter(( associated) -> associated.getId().equals(findPolicyByName("Test Associated A").getId())).findFirst().isPresent());
        Assert.assertFalse(associatedPolicies.stream().filter(( associated) -> associated.getId().equals(findPolicyByName("Test Associated B").getId())).findFirst().isPresent());
        assertAssociatedResource("Test Resource B", policy);
        List<ResourceRepresentation> resources = policyResource.resources();
        Assert.assertFalse(resources.contains(findResourceByName("Test Resource A")));
        Assert.assertFalse(resources.contains(findResourceByName("Test Resource C")));
        assertAssociatedScope("Test Scope A", policy);
        List<ScopeRepresentation> scopes = getClientResource().authorization().policies().policy(policy.getId()).scopes();
        Assert.assertFalse(scopes.contains(findScopeByName("Test Scope B").getId()));
        Assert.assertFalse(scopes.contains(findScopeByName("Test Scope C").getId()));
    }

    @Test
    public void testDefaultPolicyProviders() {
        List<String> providers = getClientResource().authorization().policies().policyProviders().stream().map(PolicyProviderRepresentation::getType).collect(Collectors.toList());
        Assert.assertFalse(providers.isEmpty());
        List expected = new ArrayList(Arrays.asList(GenericPolicyManagementTest.EXPECTED_BUILTIN_POLICY_PROVIDERS));
        if (!(Profile.isFeatureEnabled(AUTHZ_DROOLS_POLICY))) {
            expected.remove("rules");
        }
        Assert.assertTrue(providers.containsAll(expected));
    }
}

