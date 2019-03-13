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


import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.authorization.ScopeRepresentation;


/**
 *
 *
 * @author <a href="mailto:psilva@redhat.com">Pedro Igor</a>
 */
public class ScopeManagementTest extends AbstractAuthorizationSettingsTest {
    @Test
    public void testUpdate() {
        ScopeRepresentation expected = createScope();
        String previousName = expected.getName();
        expected.setName("changed");
        expected.setDisplayName("changed");
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().scopes().update(previousName, expected);
        assertAlertSuccess();
        assertScope(expected);
    }

    @Test
    public void testDelete() {
        ScopeRepresentation expected = createScope();
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().scopes().delete(expected.getName());
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().scopes().scopes().findByName(expected.getName()));
    }

    @Test
    public void testDeleteFromList() {
        ScopeRepresentation expected = createScope();
        authorizationPage.navigateTo();
        authorizationPage.authorizationTabs().scopes().deleteFromList(expected.getName());
        authorizationPage.navigateTo();
        Assert.assertNull(authorizationPage.authorizationTabs().scopes().scopes().findByName(expected.getName()));
    }
}

