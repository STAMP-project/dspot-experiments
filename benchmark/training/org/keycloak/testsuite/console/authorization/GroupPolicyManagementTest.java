/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates
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
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.authorization.GroupPolicyRepresentation;
import org.keycloak.testsuite.console.page.clients.authorization.policy.GroupPolicy;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class GroupPolicyManagementTest extends AbstractAuthorizationSettingsTest {
    @Test
    public void testCreateWithoutGroupClaims() throws InterruptedException {
        authorizationPage.navigateTo();
        GroupPolicyRepresentation expected = new GroupPolicyRepresentation();
        expected.setName("Test Group Policy");
        expected.setDescription("description");
        expected.addGroupPath("/Group A", true);
        expected.addGroupPath("/Group A/Group B/Group D");
        expected.addGroupPath("Group F");
        createPolicy(expected);
    }

    @Test
    public void testUpdate() throws InterruptedException {
        authorizationPage.navigateTo();
        GroupPolicyRepresentation expected = new GroupPolicyRepresentation();
        expected.setName("Test Group Policy");
        expected.setDescription("description");
        expected.setGroupsClaim("groups");
        expected.addGroupPath("/Group A", true);
        expected.addGroupPath("/Group A/Group B/Group D");
        expected.addGroupPath("Group F");
        expected = createPolicy(expected);
        String previousName = expected.getName();
        expected.setName("Changed Test Group Policy");
        expected.setDescription("Changed description");
        expected.setLogic(NEGATIVE);
        expected.setGroupsClaim(null);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().update(previousName, expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        GroupPolicy actual = authorizationPage.authorizationTabs().policies().name(expected.getName());
        assertPolicy(expected, actual);
        expected.getGroups().clear();
        expected.addGroupPath("/Group A", false);
        expected.addGroupPath("/Group A/Group B/Group D");
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().update(expected.getName(), expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        actual = authorizationPage.authorizationTabs().policies().name(expected.getName());
        assertPolicy(expected, actual);
        expected.getGroups().clear();
        expected.addGroupPath("/Group E");
        expected.addGroupPath("/Group A/Group B", true);
        expected.addGroupPath("/Group A/Group C");
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().update(expected.getName(), expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        actual = authorizationPage.authorizationTabs().policies().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testDelete() throws InterruptedException {
        authorizationPage.navigateTo();
        GroupPolicyRepresentation expected = new GroupPolicyRepresentation();
        expected.setName("Test Delete Group Policy");
        expected.setDescription("description");
        expected.setGroupsClaim("groups");
        expected.addGroupPath("/Group A", true);
        expected.addGroupPath("/Group A/Group B/Group D");
        expected.addGroupPath("Group F");
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
        GroupPolicyRepresentation expected = new GroupPolicyRepresentation();
        expected.setName("Test Delete Group Policy");
        expected.setDescription("description");
        expected.setGroupsClaim("groups");
        expected.addGroupPath("/Group A", true);
        expected.addGroupPath("/Group A/Group B/Group D");
        expected.addGroupPath("Group F");
        expected = createPolicy(expected);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().deleteFromList(expected.getName());
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().policies().policies().findByName(expected.getName()));
    }
}

