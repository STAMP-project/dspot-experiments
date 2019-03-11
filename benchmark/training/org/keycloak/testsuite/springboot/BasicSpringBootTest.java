package org.keycloak.testsuite.springboot;


import org.junit.Assert;
import org.junit.Test;


public class BasicSpringBootTest extends AbstractSpringBootTest {
    private static final String USER_LOGIN_2 = "testuser2";

    private static final String USER_EMAIL_2 = "user2@email.test";

    private static final String USER_PASSWORD_2 = "user2-password";

    private static final String INCORRECT_ROLE = "wrong-admin";

    @Test
    public void testCorrectUser() {
        driver.navigate().to(((AbstractSpringBootTest.APPLICATION_URL) + "/index.html"));
        Assert.assertTrue("Must be on application page", applicationPage.isCurrent());
        applicationPage.goAdmin();
        Assert.assertTrue("Must be on login page", loginPage.isCurrent());
        loginPage.login(AbstractSpringBootTest.USER_LOGIN, AbstractSpringBootTest.USER_PASSWORD);
        Assert.assertTrue("Must be on admin page", adminPage.isCurrent());
        Assert.assertTrue("Admin page must contain correct div", driver.getPageSource().contains("You are now admin"));
        driver.navigate().to(logoutPage(AbstractSpringBootTest.BASE_URL));
        Assert.assertTrue("Must be on login page", loginPage.isCurrent());
    }

    @Test
    public void testIncorrectUser() {
        driver.navigate().to(((AbstractSpringBootTest.APPLICATION_URL) + "/index.html"));
        Assert.assertTrue("Must be on application page", applicationPage.isCurrent());
        applicationPage.goAdmin();
        Assert.assertTrue("Must be on login page", loginPage.isCurrent());
        loginPage.login(BasicSpringBootTest.USER_LOGIN_2, BasicSpringBootTest.USER_PASSWORD_2);
        Assert.assertTrue("Must return 403 because of incorrect role", driver.getPageSource().contains("Forbidden"));
    }

    @Test
    public void testIncorrectCredentials() {
        driver.navigate().to(((AbstractSpringBootTest.APPLICATION_URL) + "/index.html"));
        Assert.assertTrue("Must be on application page", applicationPage.isCurrent());
        applicationPage.goAdmin();
        Assert.assertTrue("Must be on login page", loginPage.isCurrent());
        loginPage.login(AbstractSpringBootTest.USER_LOGIN, BasicSpringBootTest.USER_PASSWORD_2);
        Assert.assertEquals("Error message about password", "Invalid username or password.", loginPage.getError());
    }
}

