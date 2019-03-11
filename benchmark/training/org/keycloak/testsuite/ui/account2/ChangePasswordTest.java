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


import java.time.LocalDateTime;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.auth.page.account2.ChangePasswordPage;


/**
 *
 *
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
// TODO test the last update timestamp when the password was never updated (blocked by KEYCLOAK-8193)
// TODO test internationalization for last update timestamp (blocked by KEYCLOAK-8194)
public class ChangePasswordTest extends BaseAccountPageTest {
    @Page
    private ChangePasswordPage changePasswordPage;

    @Test
    public void changePassword() {
        final LocalDateTime testStartTime = LocalDateTime.now();
        final String oldPwd = getPasswordOf(testUser);
        final String newPwd = "n?jak? nov? heslo s h??ky a ??rkami";
        setPasswordFor(testUser, newPwd);
        Assert.assertTrue("The current password should be older than the start time of this test", testStartTime.isAfter(changePasswordPage.passwordLastUpdate().getDateTime()));
        changePasswordPage.updatePassword().setPasswords(oldPwd, newPwd);
        changePasswordPage.updatePassword().clickSave();
        changePasswordPage.alert().assertSuccess();
        // try the new password
        deleteAllSessionsInTestRealm();// logout

        changePasswordPage.navigateTo();
        loginToAccount();
        changePasswordPage.assertCurrent();
        Assert.assertTrue("The new password should be newer than the start time of this test", testStartTime.isBefore(changePasswordPage.passwordLastUpdate().getDateTime()));
    }

    @Test
    public void formValidationTest() {
        Assert.assertTrue(changePasswordPage.updatePassword().isSaveDisabled());
        changePasswordPage.updatePassword().setPasswords("abc", "def");
        Assert.assertFalse(changePasswordPage.updatePassword().isSaveDisabled());
        // clear current password
        changePasswordPage.updatePassword().setCurrentPassword("");
        Assert.assertTrue(changePasswordPage.updatePassword().isSaveDisabled());
        changePasswordPage.updatePassword().setCurrentPassword("abc");
        Assert.assertFalse(changePasswordPage.updatePassword().isSaveDisabled());
        // clear new password
        changePasswordPage.updatePassword().setNewPassword("");
        Assert.assertTrue(changePasswordPage.updatePassword().isSaveDisabled());
        changePasswordPage.updatePassword().setNewPassword("def");
        Assert.assertFalse(changePasswordPage.updatePassword().isSaveDisabled());
        // clear confirm password
        changePasswordPage.updatePassword().setConfirmPassword("");
        Assert.assertTrue(changePasswordPage.updatePassword().isSaveDisabled());
        changePasswordPage.updatePassword().setConfirmPassword("def");
        Assert.assertFalse(changePasswordPage.updatePassword().isSaveDisabled());
        // invalid current password
        changePasswordPage.updatePassword().setPasswords("invalid", "ab");
        changePasswordPage.updatePassword().clickSave();
        changePasswordPage.alert().assertDanger("Invalid existing password.");
        // non-matching passwords
        changePasswordPage.updatePassword().setPasswords(getPasswordOf(testUser), "ab");
        changePasswordPage.updatePassword().setConfirmPassword("no match");
        changePasswordPage.updatePassword().clickSave();
        changePasswordPage.alert().assertDanger("Passwords don't match.");
        // password policy
        changePasswordPage.updatePassword().setPasswords(getPasswordOf(testUser), "ab");
        changePasswordPage.updatePassword().clickSave();
        changePasswordPage.alert().assertDanger("Invalid password: minimum length 3.");
        // check the password is not changed
        deleteAllSessionsInTestRealm();
        changePasswordPage.navigateTo();
        loginToAccount();
        changePasswordPage.assertCurrent();
    }
}

