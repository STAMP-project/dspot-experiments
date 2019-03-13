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
package org.keycloak.testsuite.account.custom;


import Requirement.DISABLED;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.AbstractAuthTest;
import org.keycloak.testsuite.AbstractKeycloakTest;


/**
 *
 *
 * @author <a href="mailto:vramik@redhat.com">Vlastislav Ramik</a>
 */
public class CustomAuthFlowCookieTest extends AbstractCustomAccountManagementTest {
    @Test
    public void cookieAlternative() {
        // test default setting of cookie provider
        // login to account management
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        // check SSO is working
        // navigate to different client of the same realm and verify user is logged in
        oauth.openLoginForm();
        Assert.assertEquals("AUTH_RESPONSE", driver.getTitle());
    }

    @Test
    public void disabledCookie() {
        // disable cookie
        updateRequirement("browser", "auth-cookie", DISABLED);
        // login to account management
        testRealmAccountManagementPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        // SSO shouln't work
        // navigate to different client of the same realm and verify user is not logged in
        oauth.openLoginForm();
        Assert.assertEquals("Log in to test", driver.getTitle());
    }
}

