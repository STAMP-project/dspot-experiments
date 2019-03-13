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
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.authorization.ResourcePermissionRepresentation;
import org.keycloak.representations.idm.authorization.RolePolicyRepresentation;
import org.keycloak.testsuite.console.page.clients.authorization.permission.ResourcePermission;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ResourcePermissionManagementTest extends AbstractAuthorizationSettingsTest {
    @Test
    public void testCreateWithoutPolicies() throws InterruptedException {
        authorizationPage.navigateTo();
        ResourcePermissionRepresentation expected = new ResourcePermissionRepresentation();
        expected.setName("testCreateWithoutPolicies Permission");
        expected.setDescription("description");
        expected.addResource("Resource A");
        expected = createPermission(expected);
        authorizationPage.navigateTo();
        ResourcePermission actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testUpdateResource() throws InterruptedException {
        authorizationPage.navigateTo();
        ResourcePermissionRepresentation expected = new ResourcePermissionRepresentation();
        expected.setName("testUpdateResource Permission");
        expected.setDescription("description");
        expected.addResource("Resource A");
        expected.addPolicy("Policy A");
        expected.addPolicy("Policy B");
        expected.addPolicy("Policy C");
        expected = createPermission(expected);
        String previousName = expected.getName();
        expected.setName(((expected.getName()) + " Changed"));
        expected.setDescription("Changed description");
        expected.setDecisionStrategy(CONSENSUS);
        expected.getResources().clear();
        expected.addResource("Resource B");
        expected.getPolicies().clear();
        expected.addPolicy("Policy A", "Policy C");
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().permissions().update(previousName, expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        ResourcePermission actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
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
    public void testUpdateResourceType() throws InterruptedException {
        authorizationPage.navigateTo();
        ResourcePermissionRepresentation expected = new ResourcePermissionRepresentation();
        expected.setName("testUpdateResourceType Permission");
        expected.setDescription("description");
        expected.setResourceType("test-resource-type");
        expected.addPolicy("Policy A");
        expected.addPolicy("Policy B");
        expected.addPolicy("Policy C");
        expected = createPermission(expected);
        String previousName = expected.getName();
        expected.setName(((expected.getName()) + " Changed"));
        expected.setDescription("Changed description");
        expected.setDecisionStrategy(CONSENSUS);
        expected.setResourceType("changed-resource-type");
        expected.setPolicies(expected.getPolicies().stream().filter(( policy) -> !(policy.equals("Policy B"))).collect(Collectors.toSet()));
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().permissions().update(previousName, expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        ResourcePermission actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testDelete() throws InterruptedException {
        authorizationPage.navigateTo();
        ResourcePermissionRepresentation expected = new ResourcePermissionRepresentation();
        expected.setName("testDelete Permission");
        expected.setDescription("description");
        expected.addResource("Resource B");
        expected.addPolicy("Policy C");
        expected = createPermission(expected);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().permissions().delete(expected.getName());
        assertAlertSuccess();
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().permissions().permissions().findByName(expected.getName()));
    }

    @Test
    public void testDeleteFromList() throws InterruptedException {
        authorizationPage.navigateTo();
        ResourcePermissionRepresentation expected = new ResourcePermissionRepresentation();
        expected.setName("testDeleteFromList Permission");
        expected.setDescription("description");
        expected.addResource("Resource B");
        expected.addPolicy("Policy C");
        expected = createPermission(expected);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().permissions().deleteFromList(expected.getName());
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().permissions().permissions().findByName(expected.getName()));
    }

    @Test
    public void testCreateWithChild() {
        ResourcePermissionRepresentation expected = new ResourcePermissionRepresentation();
        expected.setName(UUID.randomUUID().toString());
        expected.setDescription("description");
        expected.addResource("Resource B");
        expected.addPolicy("Policy C");
        ResourcePermission policy = authorizationPage.authorizationTabs().permissions().create(expected, false);
        RolePolicyRepresentation childPolicy = new RolePolicyRepresentation();
        childPolicy.setName(UUID.randomUUID().toString());
        childPolicy.addRole("Role A");
        policy.createPolicy(childPolicy);
        policy.form().save();
        assertAlertSuccess();
        expected.addPolicy(childPolicy.getName());
        authorizationPage.navigateTo();
        ResourcePermission actual = authorizationPage.authorizationTabs().permissions().name(expected.getName());
        assertPolicy(expected, actual);
    }
}

