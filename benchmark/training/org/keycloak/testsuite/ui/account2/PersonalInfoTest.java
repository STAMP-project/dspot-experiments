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
package org.keycloak.testsuite.ui.account2;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.admin.ApiUtil;
import org.keycloak.testsuite.auth.page.account2.PersonalInfoPage;


/**
 *
 *
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
public class PersonalInfoTest extends BaseAccountPageTest {
    private UserRepresentation testUser2;

    @Page
    private PersonalInfoPage personalInfoPage;

    @Test
    public void updateUserInfo() {
        setEditUsernameAllowed(true);
        Assert.assertTrue(personalInfoPage.personalInfo().valuesEqual(testUser));
        Assert.assertFalse(personalInfoPage.personalInfo().isUsernameDisabled());
        Assert.assertTrue(personalInfoPage.personalInfo().isSaveDisabled());
        personalInfoPage.personalInfo().setValues(testUser2);
        Assert.assertTrue(personalInfoPage.personalInfo().valuesEqual(testUser2));
        Assert.assertFalse(personalInfoPage.personalInfo().isSaveDisabled());
        personalInfoPage.personalInfo().clickSave();
        personalInfoPage.alert().assertSuccess();
        personalInfoPage.navigateTo();
        personalInfoPage.personalInfo().valuesEqual(testUser2);
        // change just first and last name
        testUser2.setFirstName("Another");
        testUser2.setLastName("Name");
        personalInfoPage.personalInfo().setValues(testUser2);
        personalInfoPage.personalInfo().clickSave();
        personalInfoPage.alert().assertSuccess();
        personalInfoPage.navigateTo();
        personalInfoPage.personalInfo().valuesEqual(testUser2);
    }

    @Test
    public void formValidationTest() {
        setEditUsernameAllowed(true);
        // clear username
        personalInfoPage.personalInfo().setUsername("");
        Assert.assertTrue(personalInfoPage.personalInfo().isSaveDisabled());
        personalInfoPage.personalInfo().setUsername("abc");
        Assert.assertFalse(personalInfoPage.personalInfo().isSaveDisabled());
        // clear email
        personalInfoPage.personalInfo().setEmail("");
        Assert.assertTrue(personalInfoPage.personalInfo().isSaveDisabled());
        personalInfoPage.personalInfo().setEmail("vmuzikar@redhat.com");
        Assert.assertFalse(personalInfoPage.personalInfo().isSaveDisabled());
        // TODO test email validation (blocked by KEYCLOAK-8098)
        // clear first name
        personalInfoPage.personalInfo().setFirstName("");
        Assert.assertTrue(personalInfoPage.personalInfo().isSaveDisabled());
        personalInfoPage.personalInfo().setFirstName("abc");
        Assert.assertFalse(personalInfoPage.personalInfo().isSaveDisabled());
        // clear last name
        personalInfoPage.personalInfo().setLastName("");
        Assert.assertTrue(personalInfoPage.personalInfo().isSaveDisabled());
        personalInfoPage.personalInfo().setLastName("abc");
        Assert.assertFalse(personalInfoPage.personalInfo().isSaveDisabled());
        // duplicity tests
        ApiUtil.createUserWithAdminClient(testRealmResource(), testUser2);
        // duplicate username
        personalInfoPage.personalInfo().setUsername(testUser2.getUsername());
        personalInfoPage.personalInfo().clickSave();
        personalInfoPage.alert().assertDanger("Username already exists.");
        personalInfoPage.personalInfo().setUsername(testUser.getUsername());
        // duplicate email
        personalInfoPage.personalInfo().setEmail(testUser2.getEmail());
        personalInfoPage.personalInfo().clickSave();
        personalInfoPage.alert().assertDanger("Email already exists.");
        // check no changes were saved
        personalInfoPage.navigateTo();
        personalInfoPage.personalInfo().valuesEqual(testUser);
    }

    @Test
    public void disabledEditUsername() {
        setEditUsernameAllowed(false);
        Assert.assertTrue(personalInfoPage.personalInfo().isUsernameDisabled());
        personalInfoPage.personalInfo().setValues(testUser2);
        personalInfoPage.personalInfo().clickSave();
        personalInfoPage.alert().assertSuccess();
        testUser2.setUsername(testUser.getUsername());// the username should remain the same

        personalInfoPage.navigateTo();
        personalInfoPage.personalInfo().valuesEqual(testUser2);
    }
}

