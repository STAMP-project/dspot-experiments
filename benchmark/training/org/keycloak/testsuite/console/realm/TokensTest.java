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


import VerifyEmailActionToken.TOKEN_TYPE;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.auth.page.account.Account;
import org.keycloak.testsuite.console.AbstractConsoleTest;
import org.keycloak.testsuite.console.page.realm.TokenSettings;
import org.keycloak.testsuite.console.page.users.UserAttributes;
import org.keycloak.testsuite.pages.VerifyEmailPage;


/**
 *
 *
 * @author Petr Mensik
 */
public class TokensTest extends AbstractRealmTest {
    @Page
    private TokenSettings tokenSettingsPage;

    @Page
    private UserAttributes userAttributesPage;

    @Page
    protected VerifyEmailPage verifyEmailPage;

    @Page
    private Account testRealmAccountPage;

    private static final int TIMEOUT = 1;

    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    @Test
    public void testTimeoutForRealmSession() throws InterruptedException {
        tokenSettingsPage.form().setSessionTimeout(TokensTest.TIMEOUT, TokensTest.TIME_UNIT);
        tokenSettingsPage.form().save();
        loginToTestRealmConsoleAs(testUser);
        waitForTimeout(((TokensTest.TIMEOUT) + 2));
        driver.navigate().refresh();
        log.debug(driver.getCurrentUrl());
        assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);
    }

    @Test
    public void testLifespanOfRealmSession() throws InterruptedException {
        tokenSettingsPage.form().setSessionTimeoutLifespan(TokensTest.TIMEOUT, TokensTest.TIME_UNIT);
        tokenSettingsPage.form().save();
        loginToTestRealmConsoleAs(testUser);
        waitForTimeout(((TokensTest.TIMEOUT) / 2));
        driver.navigate().refresh();
        assertCurrentUrlStartsWith(testRealmAdminConsolePage);// assert still logged in (within lifespan)

        waitForTimeout((((TokensTest.TIMEOUT) / 2) + 2));
        driver.navigate().refresh();
        log.debug(driver.getCurrentUrl());
        assertCurrentUrlStartsWithLoginUrlOf(testRealmPage);// assert logged out (lifespan exceeded)

    }

    @Test
    public void testLifespanOfVerifyEmailActionTokenPropagated() throws InterruptedException {
        tokenSettingsPage.form().setOperation(TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.DAYS);
        tokenSettingsPage.form().save();
        assertAlertSuccess();
        loginToTestRealmConsoleAs(testUser);
        tokenSettingsPage.navigateTo();
        tokenSettingsPage.form().selectOperation(TOKEN_TYPE);
        Assert.assertTrue("User action token for verify e-mail expected", tokenSettingsPage.form().isOperationEquals(TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.DAYS));
    }

    @Test
    public void testLifespanActionTokenPropagatedForVerifyEmailAndResetPassword() throws InterruptedException {
        tokenSettingsPage.form().setOperation(TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.DAYS);
        tokenSettingsPage.form().setOperation(ResetCredentialsActionToken.TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.HOURS);
        tokenSettingsPage.form().save();
        assertAlertSuccess();
        loginToTestRealmConsoleAs(testUser);
        tokenSettingsPage.navigateTo();
        Assert.assertTrue("User action token for verify e-mail expected", tokenSettingsPage.form().isOperationEquals(TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.DAYS));
        Assert.assertTrue("User action token for reset credentials expected", tokenSettingsPage.form().isOperationEquals(ResetCredentialsActionToken.TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.HOURS));
        // Verify if values were properly propagated
        Map<String, Integer> userActionTokens = getUserActionTokens();
        Assert.assertThat("Action Token attributes list should contain 2 items", userActionTokens.entrySet(), Matchers.hasSize(2));
        Assert.assertThat(userActionTokens, Matchers.hasEntry(TOKEN_TYPE, ((int) (TimeUnit.DAYS.toSeconds(TokensTest.TIMEOUT)))));
        Assert.assertThat(userActionTokens, Matchers.hasEntry(ResetCredentialsActionToken.TOKEN_TYPE, ((int) (TimeUnit.HOURS.toSeconds(TokensTest.TIMEOUT)))));
    }

    @Test
    public void testButtonDisabledForEmptyAttributes() throws InterruptedException {
        tokenSettingsPage.form().setOperation(TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.DAYS);
        tokenSettingsPage.form().save();
        assertAlertSuccess();
        loginToTestRealmConsoleAs(testUser);
        tokenSettingsPage.navigateTo();
        tokenSettingsPage.form().selectOperation(TOKEN_TYPE);
        tokenSettingsPage.form().selectOperation(ResetCredentialsActionToken.TOKEN_TYPE);
        Assert.assertFalse("Save button should be disabled", tokenSettingsPage.form().saveBtn().isEnabled());
        Assert.assertFalse("Cancel button should be disabled", tokenSettingsPage.form().cancelBtn().isEnabled());
    }

    @Test
    public void testLifespanActionTokenResetForVerifyEmail() throws InterruptedException {
        tokenSettingsPage.form().setOperation(TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.DAYS);
        tokenSettingsPage.form().setOperation(ResetCredentialsActionToken.TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.HOURS);
        tokenSettingsPage.form().save();
        assertAlertSuccess();
        loginToTestRealmConsoleAs(testUser);
        tokenSettingsPage.navigateTo();
        Assert.assertTrue("User action token for verify e-mail expected", tokenSettingsPage.form().isOperationEquals(TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.DAYS));
        Assert.assertTrue("User action token for reset credentials expected", tokenSettingsPage.form().isOperationEquals(ResetCredentialsActionToken.TOKEN_TYPE, TokensTest.TIMEOUT, TimeUnit.HOURS));
        // Remove VerifyEmailActionToken and reset attribute
        tokenSettingsPage.form().resetActionToken(TOKEN_TYPE);
        tokenSettingsPage.form().save();
        // Verify if values were properly propagated
        Map<String, Integer> userActionTokens = getUserActionTokens();
        Assert.assertTrue("Action Token attributes list should contain 1 item", ((userActionTokens.size()) == 1));
        Assert.assertNull("VerifyEmailActionToken should not exist", userActionTokens.get(TOKEN_TYPE));
        Assert.assertEquals("ResetCredentialsActionToken expected to be propagated", userActionTokens.get(ResetCredentialsActionToken.TOKEN_TYPE).longValue(), TimeUnit.HOURS.toSeconds(TokensTest.TIMEOUT));
    }
}

