/**
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak.testsuite.console.realm;


import BruteForceDetection.TimeSelectValues.MINUTES;
import BruteForceDetection.TimeSelectValues.SECONDS;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.auth.page.account.Account;
import org.keycloak.testsuite.console.page.realm.BruteForceDetection;
import org.keycloak.testsuite.console.page.users.UserAttributes;
import org.keycloak.testsuite.console.page.users.Users;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


/**
 *
 *
 * @author Filip Kiss
 * @author mhajas
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
public class SecurityDefensesTest extends AbstractRealmTest {
    public static final String INVALID_PWD_MSG = "Invalid username or password.";

    public static final String ACC_DISABLED_MSG = "Invalid username or password.";

    public static final short ATTEMPTS_BAD_PWD = 2;

    public static final short ATTEMPTS_GOOD_PWD = 1;

    @Page
    private BruteForceDetection bruteForceDetectionPage;

    @Page
    private Account testRealmAccountPage;

    @Page
    private Users usersPage;

    @Page
    private UserAttributes userAttributesPage;

    @FindBy(className = "kc-feedback-text")
    private WebElement feedbackTextElement;

    @Test
    public void maxLoginFailuresTest() throws InterruptedException {
        final short secondsToWait = 10;// For slower browsers/webdrivers (like IE) we need higher value

        final short maxLoginFailures = 2;
        bruteForceDetectionPage.form().setProtectionEnabled(true);
        bruteForceDetectionPage.form().setMaxLoginFailures(String.valueOf(maxLoginFailures));
        bruteForceDetectionPage.form().setWaitIncrementSelect(SECONDS);
        bruteForceDetectionPage.form().setWaitIncrementInput(String.valueOf(secondsToWait));
        bruteForceDetectionPage.form().setQuickLoginCheckInput("1");
        bruteForceDetectionPage.form().save();
        assertAlertSuccess();
        tryToLogin((secondsToWait * ((SecurityDefensesTest.ATTEMPTS_BAD_PWD) / maxLoginFailures)));
    }

    @Test
    public void quickLoginCheck() throws InterruptedException {
        final short secondsToWait = 10;
        bruteForceDetectionPage.form().setProtectionEnabled(true);
        bruteForceDetectionPage.form().setMaxLoginFailures("100");
        bruteForceDetectionPage.form().setQuickLoginCheckInput("10000");
        bruteForceDetectionPage.form().setMinQuickLoginWaitSelect(SECONDS);
        bruteForceDetectionPage.form().setMinQuickLoginWaitInput(String.valueOf(secondsToWait));
        bruteForceDetectionPage.form().save();
        assertAlertSuccess();
        tryToLogin(secondsToWait);
    }

    @Test
    public void maxWaitLoginFailures() throws InterruptedException {
        final short secondsToWait = 15;
        bruteForceDetectionPage.form().setProtectionEnabled(true);
        bruteForceDetectionPage.form().setMaxLoginFailures("1");
        bruteForceDetectionPage.form().setWaitIncrementSelect(MINUTES);
        bruteForceDetectionPage.form().setWaitIncrementInput("10");
        bruteForceDetectionPage.form().setMaxWaitSelect(SECONDS);
        bruteForceDetectionPage.form().setMaxWaitInput(String.valueOf(secondsToWait));
        bruteForceDetectionPage.form().save();
        tryToLogin(secondsToWait);
    }

    @Test
    public void failureResetTime() {
        final short failureResetTime = 10;
        final short waitIncrement = 30;
        final short maxFailures = 2;
        bruteForceDetectionPage.form().setProtectionEnabled(true);
        bruteForceDetectionPage.form().setMaxLoginFailures(String.valueOf(maxFailures));
        bruteForceDetectionPage.form().setWaitIncrementSelect(SECONDS);
        bruteForceDetectionPage.form().setWaitIncrementInput(String.valueOf(waitIncrement));
        bruteForceDetectionPage.form().setFailureResetTimeSelect(SECONDS);
        bruteForceDetectionPage.form().setFailureResetTimeInput(String.valueOf(failureResetTime));
        bruteForceDetectionPage.form().save();
        assertAlertSuccess();
        testRealmAccountPage.navigateTo();
        testRealmLoginPage.form().login(testUser.getUsername(), ((PASSWORD) + "-mismatch"));
        pause((failureResetTime * 1000));
        testRealmLoginPage.form().login(testUser.getUsername(), ((PASSWORD) + "-mismatch"));
        testRealmLoginPage.form().login(testUser);
        assertCurrentUrlStartsWith(testRealmAccountPage);
    }

    @Test
    public void userUnlockTest() {
        bruteForceDetectionPage.form().setProtectionEnabled(true);
        bruteForceDetectionPage.form().setMaxLoginFailures("1");
        bruteForceDetectionPage.form().setWaitIncrementSelect(MINUTES);
        bruteForceDetectionPage.form().setWaitIncrementInput("10");
        bruteForceDetectionPage.form().save();
        assertAlertSuccess();
        testRealmAccountPage.navigateTo();
        setPasswordFor(testUser, ((PASSWORD) + "-mismatch"));
        testRealmLoginPage.form().login(testUser);
        usersPage.navigateTo();
        usersPage.table().searchUsers(testUser.getUsername());
        usersPage.table().editUser(testUser.getUsername());
        Assert.assertFalse(userAttributesPage.form().isEnabled());
        userAttributesPage.form().unlockUser();
        testRealmAccountPage.navigateTo();
        setPasswordFor(testUser, PASSWORD);
        testRealmLoginPage.form().login(testUser);
        assertCurrentUrlStartsWith(testRealmAccountPage);
    }
}

