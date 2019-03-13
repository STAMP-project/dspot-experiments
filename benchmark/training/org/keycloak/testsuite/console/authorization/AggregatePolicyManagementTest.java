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
package org.keycloak.testsuite.console.authorization;


import Logic.NEGATIVE;
import Profile.Feature.AUTHZ_DROOLS_POLICY;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.common.Profile;
import org.keycloak.representations.idm.authorization.AggregatePolicyRepresentation;
import org.keycloak.representations.idm.authorization.ClientPolicyRepresentation;
import org.keycloak.representations.idm.authorization.GroupPolicyRepresentation;
import org.keycloak.representations.idm.authorization.JSPolicyRepresentation;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;
import org.keycloak.representations.idm.authorization.RulePolicyRepresentation;
import org.keycloak.representations.idm.authorization.TimePolicyRepresentation;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;
import org.keycloak.testsuite.console.page.clients.authorization.policy.AggregatePolicy;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class AggregatePolicyManagementTest extends AbstractAuthorizationSettingsTest {
    @Test
    public void testUpdate() throws InterruptedException {
        authorizationPage.navigateTo();
        AggregatePolicyRepresentation expected = new AggregatePolicyRepresentation();
        expected.setName("Test Update Aggregate Policy");
        expected.setDescription("description");
        expected.addPolicy("Policy A");
        expected.addPolicy("Policy B");
        expected.addPolicy("Policy C");
        expected = createPolicy(expected);
        String previousName = expected.getName();
        expected.setName("Changed Test Update Aggregate Policy");
        expected.setDescription("Changed description");
        expected.setLogic(NEGATIVE);
        expected.getPolicies().clear();
        expected.addPolicy("Policy A", "Policy C");
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().update(previousName, expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        AggregatePolicy actual = authorizationPage.authorizationTabs().policies().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testDelete() throws InterruptedException {
        authorizationPage.navigateTo();
        AggregatePolicyRepresentation expected = new AggregatePolicyRepresentation();
        expected.setName("Test Delete Aggregate Policy");
        expected.setDescription("description");
        expected.addPolicy("Policy C");
        expected = createPolicy(expected);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().delete(expected.getName());
        assertAlertSuccess();
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().policies().policies().findByName(expected.getName()));
    }

    @Test
    public void testDeleteFromList() throws InterruptedException {
        authorizationPage.navigateTo();
        AggregatePolicyRepresentation expected = new AggregatePolicyRepresentation();
        expected.setName("Test Delete Aggregate Policy");
        expected.setDescription("description");
        expected.addPolicy("Policy C");
        expected = createPolicy(expected);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().deleteFromList(expected.getName());
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().policies().policies().findByName(expected.getName()));
    }

    @Test
    public void testCreateWithChild() {
        AggregatePolicyRepresentation expected = new AggregatePolicyRepresentation();
        expected.setName("Test Child Create Aggregate Policy");
        expected.setDescription("description");
        AggregatePolicy policy = authorizationPage.authorizationTabs().policies().create(expected, false);
        RolePolicyRepresentation childPolicy = new RolePolicyRepresentation();
        childPolicy.setName(UUID.randomUUID().toString());
        childPolicy.addRole("Role A");
        policy.createPolicy(childPolicy);
        policy.form().save();
        assertAlertSuccess();
        expected.addPolicy(childPolicy.getName());
        authorizationPage.navigateTo();
        AggregatePolicy actual = authorizationPage.authorizationTabs().policies().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testCreateWithChildAndSelectedPolicy() {
        AggregatePolicyRepresentation expected = new AggregatePolicyRepresentation();
        expected.setName("Test Child Create And Select Aggregate Policy");
        expected.setDescription("description");
        expected.addPolicy("Policy C");
        AggregatePolicy policy = authorizationPage.authorizationTabs().policies().create(expected, false);
        RolePolicyRepresentation childRolePolicy = new RolePolicyRepresentation();
        childRolePolicy.setName(UUID.randomUUID().toString());
        childRolePolicy.addRole("Role A");
        policy.createPolicy(childRolePolicy);
        expected.addPolicy(childRolePolicy.getName());
        UserPolicyRepresentation childUserPolicy = new UserPolicyRepresentation();
        childUserPolicy.setName(UUID.randomUUID().toString());
        childUserPolicy.setDescription("description");
        childUserPolicy.addUser("user a");
        policy.createPolicy(childUserPolicy);
        expected.addPolicy(childUserPolicy.getName());
        ClientPolicyRepresentation childClientPolicy = new ClientPolicyRepresentation();
        childClientPolicy.setName(UUID.randomUUID().toString());
        childClientPolicy.setDescription("description");
        childClientPolicy.addClient("client a");
        policy.createPolicy(childClientPolicy);
        expected.addPolicy(childClientPolicy.getName());
        JSPolicyRepresentation childJSPolicy = new JSPolicyRepresentation();
        childJSPolicy.setName(UUID.randomUUID().toString());
        childJSPolicy.setDescription("description");
        childJSPolicy.setCode("$evaluation.grant();");
        policy.createPolicy(childJSPolicy);
        expected.addPolicy(childJSPolicy.getName());
        TimePolicyRepresentation childTimePolicy = new TimePolicyRepresentation();
        childTimePolicy.setName(UUID.randomUUID().toString());
        childTimePolicy.setDescription("description");
        childTimePolicy.setNotBefore("2017-01-01 00:00:00");
        childTimePolicy.setNotBefore("2018-01-01 00:00:00");
        policy.createPolicy(childTimePolicy);
        expected.addPolicy(childTimePolicy.getName());
        if (Profile.isFeatureEnabled(AUTHZ_DROOLS_POLICY)) {
            RulePolicyRepresentation rulePolicy = new RulePolicyRepresentation();
            rulePolicy.setName(UUID.randomUUID().toString());
            rulePolicy.setDescription("description");
            rulePolicy.setArtifactGroupId("org.keycloak.testsuite");
            rulePolicy.setArtifactId("photoz-authz-policy");
            rulePolicy.setArtifactVersion(System.getProperty("project.version"));
            rulePolicy.setModuleName("PhotozAuthzOwnerPolicy");
            rulePolicy.setSessionName("MainOwnerSession");
            rulePolicy.setScannerPeriod("1");
            rulePolicy.setScannerPeriodUnit("Minutes");
            policy.createPolicy(rulePolicy);
            expected.addPolicy(rulePolicy.getName());
        }
        GroupPolicyRepresentation childGroupPolicy = new GroupPolicyRepresentation();
        childGroupPolicy.setName(UUID.randomUUID().toString());
        childGroupPolicy.setDescription("description");
        childGroupPolicy.setGroupsClaim("groups");
        childGroupPolicy.addGroupPath("/Group A", true);
        policy.createPolicy(childGroupPolicy);
        expected.addPolicy(childGroupPolicy.getName());
        policy.form().save();
        assertAlertSuccess();
        authorizationPage.navigateTo();
        AggregatePolicy actual = authorizationPage.authorizationTabs().policies().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testUpdateWithChild() {
        AggregatePolicyRepresentation expected = new AggregatePolicyRepresentation();
        expected.setName("Test Child Update Aggregate Policy");
        expected.setDescription("description");
        expected.addPolicy("Policy C");
        AggregatePolicy policy = authorizationPage.authorizationTabs().policies().create(expected);
        assertAlertSuccess();
        assertPolicy(expected, policy);
        RolePolicyRepresentation childPolicy = new RolePolicyRepresentation();
        childPolicy.setName(UUID.randomUUID().toString());
        childPolicy.addRole("Role A");
        policy.createPolicy(childPolicy);
        policy.form().save();
        expected.addPolicy(childPolicy.getName());
        authorizationPage.navigateTo();
        AggregatePolicy actual = authorizationPage.authorizationTabs().policies().name(expected.getName());
        assertPolicy(expected, actual);
    }
}

