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
package org.keycloak.testsuite.welcomepage;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.auth.page.WelcomePage;
import org.keycloak.testsuite.auth.page.login.OIDCLogin;


/**
 *
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WelcomePageTest extends AbstractKeycloakTest {
    @Page
    private WelcomePage welcomePage;

    @Page
    protected OIDCLogin loginPage;

    @Test
    public void test_1_LocalAccessNoAdmin() throws Exception {
        welcomePage.navigateTo();
        Assert.assertFalse("Welcome page did not ask to create a new admin user.", welcomePage.isPasswordSet());
    }

    @Test
    public void test_2_RemoteAccessNoAdmin() throws Exception {
        navigateToUri(getPublicServerUrl().toString());
        Assert.assertFalse("Welcome page did not ask to create a new admin user.", welcomePage.isPasswordSet());
    }

    @Test
    public void test_3_LocalAccessWithAdmin() throws Exception {
        welcomePage.navigateTo();
        welcomePage.setPassword("admin", "admin");
        Assert.assertTrue(driver.getPageSource().contains("User created"));
        welcomePage.navigateTo();
        Assert.assertTrue("Welcome page asked to set admin password.", welcomePage.isPasswordSet());
    }

    @Test
    public void test_4_RemoteAccessWithAdmin() throws Exception {
        navigateToUri(getPublicServerUrl().toString());
        Assert.assertTrue("Welcome page asked to set admin password.", welcomePage.isPasswordSet());
    }

    @Test
    public void test_5_AccessCreatedAdminAccount() throws Exception {
        welcomePage.navigateToAdminConsole();
        loginPage.form().login("admin", "admin");
        Assert.assertFalse("Login with 'admin:admin' failed", driver.getPageSource().contains("Invalid username or password."));
    }
}

