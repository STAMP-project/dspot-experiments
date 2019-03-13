/**
 * Copyright 2018 Red Hat, Inc. and/or its affiliates
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
package org.keycloak.testsuite.adapter.servlet;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.adapter.AbstractServletsAdapterTest;
import org.keycloak.testsuite.adapter.page.MultiTenant;
import org.keycloak.testsuite.arquillian.annotation.AppServerContainer;
import org.keycloak.testsuite.arquillian.containers.ContainerConstants;
import org.keycloak.testsuite.util.URLAssert;
import org.keycloak.testsuite.util.WaitUtils;


/**
 * note: migrated from old testsuite
 *
 * @author Juraci Paix?o Kr?hling <juraci at kroehling.de>
 */
@AppServerContainer(ContainerConstants.APP_SERVER_UNDERTOW)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY)
@AppServerContainer(ContainerConstants.APP_SERVER_WILDFLY_DEPRECATED)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP6)
@AppServerContainer(ContainerConstants.APP_SERVER_EAP71)
public class MultiTenancyTest extends AbstractServletsAdapterTest {
    @Page
    private MultiTenant tenantPage;

    /**
     * Simplest scenario: one user, one realm. The user is not logged in at
     * any other realm
     */
    @Test
    public void testTenantsLoggingOut() {
        doTenantRequests("tenant1", true);
        doTenantRequests("tenant2", true);
    }

    /**
     * This tests the adapter's ability to deal with multiple sessions
     * from the same user, one for each realm. It should not mixup and return
     * a session from tenant1 to tenant2
     */
    @Test
    public void testTenantsWithoutLoggingOut() {
        doTenantRequests("tenant1", true);
        doTenantRequests("tenant2", true);
        doTenantRequests("tenant1", false);
        doTenantRequests("tenant2", true);
    }

    /**
     * This test simulates an user that is not logged in yet, and tries to login
     * into tenant1 using an account from tenant2.
     * On this scenario, the user should be shown the login page again.
     */
    @Test
    public void testUnauthorizedAccessNotLoggedIn() {
        String keycloakServerBaseUrl = authServerPage.toString();
        driver.navigate().to(tenantPage.getTenantRealmUrl("tenant1"));
        WaitUtils.waitForPageToLoad();
        URLAssert.assertCurrentUrlStartsWith(keycloakServerBaseUrl);
        testRealmLoginPage.form().login("user-tenant2", "user-tenant2");
        URLAssert.assertCurrentUrlStartsWith(keycloakServerBaseUrl);
    }

    /**
     * This test simulates an user which is already logged in into tenant1
     * and tries to access a resource on tenant2.
     * On this scenario, the user should be shown the login page again.
     */
    @Test
    public void testUnauthorizedAccessLoggedIn() {
        doTenantRequests("tenant1", false);
        driver.navigate().to(tenantPage.getTenantRealmUrl("tenant2"));
        URLAssert.assertCurrentUrlStartsWith(authServerPage.toString());
    }
}

