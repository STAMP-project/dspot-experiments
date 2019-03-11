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
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.authorization.UserPolicyRepresentation;
import org.keycloak.testsuite.console.page.clients.authorization.policy.UserPolicy;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class UserPolicyManagementTest extends AbstractAuthorizationSettingsTest {
    @Test
    public void testUpdate() throws InterruptedException {
        authorizationPage.navigateTo();
        UserPolicyRepresentation expected = new UserPolicyRepresentation();
        expected.setName("Test User Policy");
        expected.setDescription("description");
        expected.addUser("user a");
        expected.addUser("user b");
        expected.addUser("user c");
        expected = createPolicy(expected);
        String previousName = expected.getName();
        expected.setName("Changed Test User Policy");
        expected.setDescription("Changed description");
        expected.setLogic(NEGATIVE);
        expected.setUsers(expected.getUsers().stream().filter(( user) -> !(user.equals("user b"))).collect(Collectors.toSet()));
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().update(previousName, expected);
        assertAlertSuccess();
        authorizationPage.navigateTo();
        UserPolicy actual = authorizationPage.authorizationTabs().policies().name(expected.getName());
        assertPolicy(expected, actual);
    }

    @Test
    public void testDelete() throws InterruptedException {
        authorizationPage.navigateTo();
        UserPolicyRepresentation expected = new UserPolicyRepresentation();
        expected.setName("Test User Policy");
        expected.setDescription("description");
        expected.addUser("user c");
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
        UserPolicyRepresentation expected = new UserPolicyRepresentation();
        expected.setName("Test User Policy");
        expected.setDescription("description");
        expected.addUser("user c");
        expected = createPolicy(expected);
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().policies().deleteFromList(expected.getName());
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().policies().policies().findByName(expected.getName()));
    }
}

