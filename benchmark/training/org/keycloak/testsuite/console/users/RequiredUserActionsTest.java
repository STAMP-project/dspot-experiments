package org.keycloak.testsuite.console.users;


import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.testsuite.auth.page.account.Account;
import org.keycloak.testsuite.auth.page.login.TermsAndConditions;
import org.keycloak.testsuite.auth.page.login.UpdateAccount;
import org.keycloak.testsuite.auth.page.login.UpdatePassword;
import org.keycloak.testsuite.console.page.authentication.RequiredActions;
import org.keycloak.testsuite.console.page.users.UserAttributes;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;


/**
 *
 *
 * @author tkyjovsk
 * @author mhajas
 */
public class RequiredUserActionsTest extends AbstractUserTest {
    @Page
    private UserAttributes userAttributesPage;

    @Page
    private Account testRealmAccountPage;

    @Page
    private UpdateAccount testRealmUpdateAccountPage;

    @Page
    private UpdatePassword testRealmUpdatePasswordPage;

    @Page
    private RequiredActions requiredActionsPage;

    @Page
    private TermsAndConditions termsAndConditionsPage;

    @FindBy(css = "kc-feedback-text")
    protected WebElement feedbackText;

    @Test
    public void updatePassword() {
        userAttributesPage.form().addRequiredAction(UPDATE_PASSWORD.getActionName());
        userAttributesPage.form().save();
        assertAlertSuccess();
        testRealmAccountPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        waitForFeedbackText("You need to change your password to activate your account.");
        testRealmUpdatePasswordPage.updatePasswords(null, null);
        waitForFeedbackText("Please specify password.");
        testRealmUpdatePasswordPage.updatePasswords(PASSWORD, null);
        waitForFeedbackText("Passwords don't match.");
        testRealmUpdatePasswordPage.updatePasswords(PASSWORD, ((PASSWORD) + "-mismatch"));
        waitForFeedbackText("Passwords don't match.");
        testRealmUpdatePasswordPage.updatePasswords(PASSWORD, PASSWORD);
        assertCurrentUrlStartsWith(testRealmAccountPage);
    }

    @Test
    public void updateProfile() {
        userAttributesPage.form().addRequiredAction(UPDATE_PROFILE.getActionName());
        userAttributesPage.form().save();
        assertAlertSuccess();
        testRealmAccountPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        waitForFeedbackText("You need to update your user profile to activate your account.");
        testUser.setEmail(null);
        testUser.setFirstName(null);
        testUser.setLastName(null);
        testRealmUpdateAccountPage.updateAccount(testUser);
        waitForFeedbackText("Please specify email.");
        testUser.setEmail("test@email.test");
        testRealmUpdateAccountPage.updateAccount(testUser);
        waitForFeedbackText("Please specify first name.");
        testUser.setFirstName("test");
        testRealmUpdateAccountPage.updateAccount(testUser);
        waitForFeedbackText("Please specify last name.");
        testUser.setLastName("user");
        testRealmUpdateAccountPage.updateAccount(testUser);
        assertCurrentUrlStartsWith(testRealmAccountPage);
    }

    @Test
    public void termsAndConditions() {
        requiredActionsPage.navigateTo();
        requiredActionsPage.setTermsAndConditionEnabled(true);
        manage().users();
        usersPage.table().viewAllUsers();
        usersPage.table().clickUser(testUser.getUsername());
        userAttributesPage.form().addRequiredAction(TERMS_AND_CONDITIONS.getActionName());
        userAttributesPage.form().save();
        assertAlertSuccess();
        testRealmAccountPage.navigateTo();
        testRealmLoginPage.form().login(testUser);
        Assert.assertTrue(termsAndConditionsPage.isCurrent());
    }
}

