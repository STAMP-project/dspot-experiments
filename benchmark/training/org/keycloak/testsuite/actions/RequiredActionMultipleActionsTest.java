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
package org.keycloak.testsuite.actions;


import RequestType.AUTH_RESPONSE;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.keycloak.testsuite.AbstractTestRealmKeycloakTest;
import org.keycloak.testsuite.AssertEvents;
import org.keycloak.testsuite.pages.AppPage;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.pages.LoginPasswordUpdatePage;
import org.keycloak.testsuite.pages.LoginUpdateProfileEditUsernameAllowedPage;


/**
 *
 *
 * @author <a href="mailto:sthorger@redhat.com">Stian Thorgersen</a>
 */
public class RequiredActionMultipleActionsTest extends AbstractTestRealmKeycloakTest {
    @Rule
    public AssertEvents events = new AssertEvents(this);

    @Page
    protected AppPage appPage;

    @Page
    protected LoginPage loginPage;

    @Page
    protected LoginPasswordUpdatePage changePasswordPage;

    @Page
    protected LoginUpdateProfileEditUsernameAllowedPage updateProfilePage;

    @Test
    public void updateProfileAndPassword() throws Exception {
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        String codeId = null;
        if (changePasswordPage.isCurrent()) {
            codeId = updatePassword(codeId);
            updateProfilePage.assertCurrent();
            updateProfile(codeId);
        } else
            if (updateProfilePage.isCurrent()) {
                codeId = updateProfile(codeId);
                changePasswordPage.assertCurrent();
                updatePassword(codeId);
            } else {
                Assert.fail("Expected to update password and profile before login");
            }

        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().session(codeId).assertEvent();
    }
}

