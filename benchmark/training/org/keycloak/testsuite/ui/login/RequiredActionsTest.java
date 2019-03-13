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
package org.keycloak.testsuite.ui.login;


import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.models.utils.TimeBasedOTP;
import org.keycloak.representations.idm.RequiredActionProviderRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.auth.page.login.LoginError;
import org.keycloak.testsuite.auth.page.login.OAuthGrant;
import org.keycloak.testsuite.auth.page.login.OTPSetup;
import org.keycloak.testsuite.auth.page.login.OneTimeCode;
import org.keycloak.testsuite.auth.page.login.TermsAndConditions;
import org.keycloak.testsuite.auth.page.login.UpdateAccount;
import org.keycloak.testsuite.auth.page.login.UpdatePassword;
import org.keycloak.testsuite.auth.page.login.VerifyEmail;
import org.keycloak.testsuite.ui.AbstractUiTest;


/**
 *
 *
 * @author Vaclav Muzikar <vmuzikar@redhat.com>
 */
public class RequiredActionsTest extends AbstractLoginTest {
    public static final String GRANT_REALM = "grant-realm";

    public static final String CONSENT_TEXT = "P??li? ?lu?ou?k? k?? ?p?l ??belsk? ?dy";

    private UserRepresentation grantRealmUser = createUserRepresentation("test", PASSWORD);

    public static final String TOTP = "totp";

    public static final String HOTP = "hotp";

    @Page
    private TermsAndConditions termsAndConditionsPage;

    @Page
    private UpdatePassword updatePasswordPage;

    @Page
    private UpdateAccount updateAccountPage;

    @Page
    private VerifyEmail verifyEmailPage;

    @Page
    private OTPSetup otpSetupPage;

    @Page
    private OneTimeCode oneTimeCodePage;

    @Page
    private OAuthGrant oAuthGrantPage;

    @Page
    private LoginError loginErrorPage;

    private TimeBasedOTP otpGenerator = new TimeBasedOTP();

    @Test
    public void termsAndConditions() {
        RequiredActionProviderRepresentation termsAndCondRep = testRealmResource().flows().getRequiredAction(termsAndConditionsPage.getActionId());
        termsAndCondRep.setEnabled(true);
        testRealmResource().flows().updateRequiredAction(termsAndConditionsPage.getActionId(), termsAndCondRep);
        initiateRequiredAction(termsAndConditionsPage);
        termsAndConditionsPage.localeDropdown().selectAndAssert(AbstractUiTest.CUSTOM_LOCALE_NAME);
        termsAndConditionsPage.acceptTerms();
        assertLoginSuccessful();
        deleteAllSessionsInTestRealm();
        initiateRequiredAction(termsAndConditionsPage);
        Assert.assertEquals("[TEST LOCALE] souhlas s podm?nkami", termsAndConditionsPage.getText());
        termsAndConditionsPage.declineTerms();
        loginErrorPage.assertCurrent();
        assertNoAccess();
    }

    @Test
    public void updatePassword() {
        initiateRequiredAction(updatePasswordPage);
        updatePasswordPage.localeDropdown().selectAndAssert(AbstractUiTest.CUSTOM_LOCALE_NAME);
        Assert.assertTrue(updatePasswordPage.feedbackMessage().isWarning());
        Assert.assertEquals("You need to change your password to activate your account.", updatePasswordPage.feedbackMessage().getText());
        Assert.assertEquals("New Password", updatePasswordPage.fields().getNewPasswordLabel());
        Assert.assertEquals("Confirm password", updatePasswordPage.fields().getConfirmPasswordLabel());
        updatePasswordPage.updatePasswords("some wrong", "password");
        Assert.assertTrue(updatePasswordPage.feedbackMessage().isError());
        Assert.assertEquals("[TEST LOCALE] hesla se neshoduj?", updatePasswordPage.feedbackMessage().getText());
        updatePasswordPage.localeDropdown().selectAndAssert(ENGLISH_LOCALE_NAME);
        updatePasswordPage.updatePasswords("matchingPassword", "matchingPassword");
        assertLoginSuccessful();
    }

    @Test
    public void updateProfile() {
        initiateRequiredAction(updateAccountPage);
        // prefilled profile
        Assert.assertTrue(updateAccountPage.feedbackMessage().isWarning());
        updateAccountPage.localeDropdown().selectAndAssert(AbstractUiTest.CUSTOM_LOCALE_NAME);
        Assert.assertEquals("[TEST LOCALE] aktualizovat profil", updateAccountPage.feedbackMessage().getText());
        updateAccountPage.localeDropdown().selectAndAssert(ENGLISH_LOCALE_NAME);
        Assert.assertFalse(updateAccountPage.fields().isUsernamePresent());
        Assert.assertEquals("Email", updateAccountPage.fields().getEmailLabel());
        Assert.assertEquals("First name", updateAccountPage.fields().getFirstNameLabel());
        Assert.assertEquals("Last name", updateAccountPage.fields().getLastNameLabel());
        Assert.assertFalse(updateAccountPage.fields().hasEmailError());
        Assert.assertFalse(updateAccountPage.fields().hasFirstNameError());
        Assert.assertFalse(updateAccountPage.fields().hasLastNameError());
        Assert.assertEquals(testUser.getEmail(), updateAccountPage.fields().getEmail());
        Assert.assertEquals(testUser.getFirstName(), updateAccountPage.fields().getFirstName());
        Assert.assertEquals(testUser.getLastName(), updateAccountPage.fields().getLastName());
        updateAccountPage.localeDropdown().selectAndAssert(AbstractUiTest.CUSTOM_LOCALE_NAME);
        // empty form
        updateAccountPage.updateAccount(null, null, null);
        Assert.assertTrue(updateAccountPage.feedbackMessage().isError());
        String errorMsg = updateAccountPage.feedbackMessage().getText();
        Assert.assertTrue((((errorMsg.contains("first name")) && (errorMsg.contains("last name"))) && (errorMsg.contains("email"))));
        Assert.assertTrue(updateAccountPage.fields().hasEmailError());
        Assert.assertTrue(updateAccountPage.fields().hasFirstNameError());
        Assert.assertTrue(updateAccountPage.fields().hasLastNameError());
        final String email = "vmuzikar@redhat.com";
        final String firstName = "Vaclav";
        final String lastName = "Muzikar";
        // email filled in
        updateAccountPage.fields().setEmail(email);
        updateAccountPage.submit();
        Assert.assertTrue(updateAccountPage.feedbackMessage().isError());
        errorMsg = updateAccountPage.feedbackMessage().getText();
        Assert.assertTrue((((errorMsg.contains("first name")) && (errorMsg.contains("last name"))) && (!(errorMsg.contains("email")))));
        Assert.assertFalse(updateAccountPage.fields().hasEmailError());
        Assert.assertTrue(updateAccountPage.fields().hasFirstNameError());
        Assert.assertTrue(updateAccountPage.fields().hasLastNameError());
        Assert.assertEquals(email, updateAccountPage.fields().getEmail());
        // first name filled in
        updateAccountPage.fields().setFirstName(firstName);
        updateAccountPage.submit();
        Assert.assertTrue(updateAccountPage.feedbackMessage().isError());
        errorMsg = updateAccountPage.feedbackMessage().getText();
        Assert.assertTrue((((!(errorMsg.contains("first name"))) && (errorMsg.contains("last name"))) && (!(errorMsg.contains("email")))));
        Assert.assertFalse(updateAccountPage.fields().hasEmailError());
        Assert.assertFalse(updateAccountPage.fields().hasFirstNameError());
        Assert.assertTrue(updateAccountPage.fields().hasLastNameError());
        Assert.assertEquals(email, updateAccountPage.fields().getEmail());
        Assert.assertEquals(firstName, updateAccountPage.fields().getFirstName());
        // last name filled in
        updateAccountPage.fields().setFirstName(null);
        updateAccountPage.fields().setLastName(lastName);
        updateAccountPage.submit();
        Assert.assertTrue(updateAccountPage.feedbackMessage().isError());
        errorMsg = updateAccountPage.feedbackMessage().getText();
        Assert.assertTrue((((errorMsg.contains("first name")) && (!(errorMsg.contains("last name")))) && (!(errorMsg.contains("email")))));
        Assert.assertFalse(updateAccountPage.fields().hasEmailError());
        Assert.assertTrue(updateAccountPage.fields().hasFirstNameError());
        Assert.assertFalse(updateAccountPage.fields().hasLastNameError());
        Assert.assertEquals(email, updateAccountPage.fields().getEmail());
        Assert.assertEquals(lastName, updateAccountPage.fields().getLastName());
        // success
        Assert.assertEquals("[TEST LOCALE] k?estn? jm?no", updateAccountPage.fields().getFirstNameLabel());
        updateAccountPage.updateAccount(email, firstName, lastName);
        assertLoginSuccessful();
    }

    @Test
    public void verifyEmail() {
        initiateRequiredAction(verifyEmailPage);
        verifyEmailPage.localeDropdown().selectAndAssert(AbstractUiTest.CUSTOM_LOCALE_NAME);
        boolean firstAttempt = true;
        while (true) {
            Assert.assertTrue(verifyEmailPage.feedbackMessage().isWarning());
            Assert.assertEquals("[TEST LOCALE] je t?eba ov??it emailovou adresu", verifyEmailPage.feedbackMessage().getText());
            Assert.assertEquals("An email with instructions to verify your email address has been sent to you.", verifyEmailPage.getInstructionMessage());
            if (firstAttempt) {
                verifyEmailPage.clickResend();
                firstAttempt = false;
            } else {
                break;
            }
        } 
    }

    @Test
    public void configureManualTotp() {
        setRealmOtpType(RequiredActionsTest.TOTP);
        testManualOtp();
    }

    @Test
    public void configureManualHotp() {
        setRealmOtpType(RequiredActionsTest.HOTP);
        testManualOtp();
    }

    @Test
    public void configureBarcodeTotp() throws Exception {
        setRealmOtpType(RequiredActionsTest.TOTP);
        testBarcodeOtp();
    }

    @Test
    public void configureBarcodeHotp() throws Exception {
        setRealmOtpType(RequiredActionsTest.HOTP);
        testBarcodeOtp();
    }

    @Test
    public void clientConsent() {
        testRealmPage.setAuthRealm(RequiredActionsTest.GRANT_REALM);
        testRealmAccountPage.setAuthRealm(RequiredActionsTest.GRANT_REALM);
        testRealmLoginPage.setAuthRealm(RequiredActionsTest.GRANT_REALM);
        final List<String> defaultClientScopesToApprove = Arrays.asList("Email address", "User profile");
        // custom consent text
        initiateClientScopesConsent(true, RequiredActionsTest.CONSENT_TEXT);
        oAuthGrantPage.localeDropdown().selectAndAssert(AbstractUiTest.CUSTOM_LOCALE_NAME);
        List<String> clientScopesToApprove = new LinkedList<>(defaultClientScopesToApprove);
        clientScopesToApprove.add(RequiredActionsTest.CONSENT_TEXT);
        oAuthGrantPage.assertClientScopes(clientScopesToApprove);
        // default consent text
        initiateClientScopesConsent(true, null);
        clientScopesToApprove = new LinkedList<>(defaultClientScopesToApprove);
        clientScopesToApprove.add("Account");
        oAuthGrantPage.assertClientScopes(clientScopesToApprove);
        // consent with missing client
        initiateClientScopesConsent(false, RequiredActionsTest.CONSENT_TEXT);
        oAuthGrantPage.assertClientScopes(defaultClientScopesToApprove);
        // test buttons
        oAuthGrantPage.cancel();
        assertNoAccess();
        testRealmLoginPage.form().login(grantRealmUser);
        Assert.assertEquals("[TEST LOCALE] Ud?lit p??stup Account", oAuthGrantPage.getTitleText());
        oAuthGrantPage.accept();
        assertLoginSuccessful();
    }
}

