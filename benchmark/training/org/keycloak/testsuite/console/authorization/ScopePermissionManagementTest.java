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


import DecisionStrategy.CONSENSUS;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;
import org.keycloak.representations.idm.authorization.ScopePermissionRepresentation;
import org.keycloak.testsuite.console.page.clients.authorization.permission.ScopePermission;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ScopePermissionManagementTest extends AbstractAuthorizationSettingsTest {
    @Test
    public void testCreateWithoutPolicies() {
        authorizationPage.navigateTo();
        ScopePermissionRepresentation expected = new ScopePermissionRepresentation();
        expected.setName("testCreateWithoutPolicies Permission");
        expected.setDescription("description");
        expected.addResource("Resource A");
        expected.addScope("Scope A");
        expected = createPermission(expected);
        authorizationPage.navigateTo();
        ScopePermission actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testUpdateResourceScope() {
        authorizationPage.navigateTo();
        ScopePermissionRepresentation expected = new ScopePermissionRepresentation();
        expected.setName("testUpdateResourceScope Permission");
        expected.setDescription("description");
        expected.addResource("Resource A");
        expected.addScope("Scope A");
        expected.addPolicy("Policy C", "Policy A", "Policy B");
        expected = createPermission(expected);
        String previousName = expected.getName();
        expected.setName((previousName + "Changed"));
        expected.setDescription("changed");
        expected.setDecisionStrategy(CONSENSUS);
        expected.getResources().clear();
        expected.addResource("Resource B");
        expected.getScopes().clear();
        expected.addScope("Scope B", "Scope C");
        expected.getPolicies().clear();
        expected.addPolicy("Policy C");
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().permissions().update(previousName, expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        ScopePermission actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
        expected.getPolicies().clear();
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().permissions().update(expected.getName(), expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testUpdateWithoutResource() {
        authorizationPage.navigateTo();
        ScopePermissionRepresentation expected = new ScopePermissionRepresentation();
        expected.setName("testUpdateWithoutResource Permission");
        expected.setDescription("description");
        expected.addResource("Resource A");
        expected.addScope("Scope A");
        expected.addPolicy("Policy C");
        expected = createPermission(expected);
        expected.getResources().clear();
        expected.addScope("Scope B");
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().permissions().update(expected.getName(), expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        ScopePermission actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testUpdateScopeOnly() {
        authorizationPage.navigateTo();
        ScopePermissionRepresentation expected = new ScopePermissionRepresentation();
        expected.setName("testUpdateScopeOnly Permission");
        expected.setDescription("description");
        expected.addScope("Scope C", "Scope A", "Scope B");
        expected.addPolicy("Policy C", "Policy A", "Policy B");
        expected = createPermission(expected);
        String previousName = expected.getName();
        expected.setName((previousName + "Changed"));
        expected.setDescription("changed");
        expected.setDecisionStrategy(CONSENSUS);
        expected.getScopes().clear();
        expected.addScope("Scope B");
        expected.getPolicies().clear();
        expected.addPolicy("Policy C");
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().permissions().update(previousName, expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        ScopePermission actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testDelete() {
        authorizationPage.navigateTo();
        ScopePermissionRepresentation expected = new ScopePermissionRepresentation();
        expected.setName("testDelete Permission");
        expected.setDescription("description");
        expected.addScope("Scope C");
        expected.addPolicy("Policy C");
        expected = createPermission(expected);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().permissions().delete(expected.getName());
        assertAlertSuccess();
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().permissions().permissions().findByName(expected.getName()));
    }

    @Test
    public void testDeleteFromList() {
        authorizationPage.navigateTo();
        ScopePermissionRepresentation expected = new ScopePermissionRepresentation();
        expected.setName("testDeleteFromList Permission");
        expected.setDescription("description");
        expected.addScope("Scope C");
        expected.addPolicy("Policy C");
        expected = createPermission(expected);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().permissions().deleteFromList(expected.getName());
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().permissions().permissions().findByName(expected.getName()));
    }

    @Test
    public void testCreateUpdateWithChild() {
        ScopePermissionRepresentation expected = new ScopePermissionRepresentation();
        expected.setName(UUID.randomUUID().toString());
        expected.setDescription("description");
        expected.addScope("Scope C");
        expected.addPolicy("Policy C");
        ScopePermission policy = authorizationPage.authorizationTabs().permissions().create(expected, false);
        RolePolicyRepresentation childPolicy = new RolePolicyRepresentation();
        childPolicy.setName(UUID.randomUUID().toString());
        childPolicy.addRole("Role A");
        policy.createPolicy(childPolicy);
        policy.form().save();
        assertAlertSuccess();
        expected.addPolicy(childPolicy.getName());
        authorizationPage.navigateTo();
        ScopePermission actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
        RolePolicyRepresentation childPolicy2 = new RolePolicyRepresentation();
        childPolicy2.setName(UUID.randomUUID().toString());
        childPolicy2.addRole("Role A");
        policy.createPolicy(childPolicy2);
        policy.form().save();
        assertAlertSuccess();
        expected.addPolicy(childPolicy2.getName());
        authorizationPage.navigateTo();
        actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
        expected.addResource("Resource B");
        expected.getScopes().clear();
        expected.addScope("Scope B", "Scope C");
        expected.getScopes().remove("Policy C");
        RolePolicyRepresentation childPolicy3 = new RolePolicyRepresentation();
        childPolicy3.setName(UUID.randomUUID().toString());
        childPolicy3.addRole("Role A");
        policy.update(expected, false);
        policy.createPolicy(childPolicy3);
        policy.form().save();
        assertAlertSuccess();
        expected.addPolicy(childPolicy3.getName());
        authorizationPage.navigateTo();
        actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
    }
}

