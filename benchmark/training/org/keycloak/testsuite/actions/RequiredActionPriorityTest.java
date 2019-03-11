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
package org.keycloak.testsuite.actions;


import Details.PREVIOUS_EMAIL;
import Details.REDIRECT_URI;
import Details.UPDATED_EMAIL;
import EventType.CUSTOM_REQUIRED_ACTION;
import EventType.UPDATE_EMAIL;
import EventType.UPDATE_PASSWORD;
import EventType.UPDATE_PROFILE;
import RequestType.AUTH_RESPONSE;
import TermsAndConditions.PROVIDER_ID;
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
import org.keycloak.testsuite.pages.TermsAndConditionsPage;


/**
 *
 *
 * @author <a href="mailto:wadahiro@gmail.com">Hiroyuki Wada</a>
 */
public class RequiredActionPriorityTest extends AbstractTestRealmKeycloakTest {
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

    @Page
    protected TermsAndConditionsPage termsPage;

    @Test
    public void executeRequiredActionsWithDefaultPriority() throws Exception {
        // Default priority is alphabetical order:
        // TermsAndConditions -> UpdatePassword -> UpdateProfile
        // Login
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        // First, accept terms
        termsPage.assertCurrent();
        termsPage.acceptTerms();
        events.expectRequiredAction(CUSTOM_REQUIRED_ACTION).removeDetail(REDIRECT_URI).detail(Details.CUSTOM_REQUIRED_ACTION, PROVIDER_ID).assertEvent();
        // Second, change password
        changePasswordPage.assertCurrent();
        changePasswordPage.changePassword("new-password", "new-password");
        events.expectRequiredAction(UPDATE_PASSWORD).assertEvent();
        // Finally, update profile
        updateProfilePage.assertCurrent();
        updateProfilePage.update("New first", "New last", "new@email.com", "test-user@localhost");
        events.expectRequiredAction(UPDATE_EMAIL).detail(PREVIOUS_EMAIL, "test-user@localhost").detail(UPDATED_EMAIL, "new@email.com").assertEvent();
        events.expectRequiredAction(UPDATE_PROFILE).assertEvent();
        // Logined
        appPage.assertCurrent();
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().assertEvent();
    }

    @Test
    public void executeRequiredActionsWithCustomPriority() throws Exception {
        // Default priority is alphabetical order:
        // TermsAndConditions -> UpdatePassword -> UpdateProfile
        // After Changing the priority, the order will be:
        // UpdatePassword -> UpdateProfile -> TermsAndConditions
        testRealm().flows().raiseRequiredActionPriority(UserModel.RequiredAction.UPDATE_PASSWORD.name());
        testRealm().flows().lowerRequiredActionPriority("terms_and_conditions");
        // Login
        loginPage.open();
        loginPage.login("test-user@localhost", "password");
        // First, change password
        changePasswordPage.assertCurrent();
        changePasswordPage.changePassword("new-password", "new-password");
        events.expectRequiredAction(UPDATE_PASSWORD).assertEvent();
        // Second, update profile
        updateProfilePage.assertCurrent();
        updateProfilePage.update("New first", "New last", "new@email.com", "test-user@localhost");
        events.expectRequiredAction(UPDATE_EMAIL).detail(PREVIOUS_EMAIL, "test-user@localhost").detail(UPDATED_EMAIL, "new@email.com").assertEvent();
        events.expectRequiredAction(UPDATE_PROFILE).assertEvent();
        // Finally, accept terms
        termsPage.assertCurrent();
        termsPage.acceptTerms();
        events.expectRequiredAction(CUSTOM_REQUIRED_ACTION).removeDetail(REDIRECT_URI).detail(Details.CUSTOM_REQUIRED_ACTION, PROVIDER_ID).assertEvent();
        // Logined
        appPage.assertCurrent();
        Assert.assertEquals(AUTH_RESPONSE, appPage.getRequestType());
        events.expectLogin().assertEvent();
    }
}

