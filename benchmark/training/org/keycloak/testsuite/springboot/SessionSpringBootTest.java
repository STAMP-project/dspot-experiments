package org.keycloak.testsuite.springboot;


import SessionPage.PAGE_TITLE;
import org.jboss.arquillian.drone.api.annotation.Drone;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.admin.client.resource.ClientResource;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.idm.ClientRepresentation;
import org.keycloak.representations.idm.RealmRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.testsuite.auth.page.account.Sessions;
import org.keycloak.testsuite.util.SecondBrowser;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


public class SessionSpringBootTest extends AbstractSpringBootTest {
    private static final String SERVLET_URL = (AbstractSpringBootTest.BASE_URL) + "/SessionServlet";

    static final String USER_LOGIN_CORRECT_2 = "testcorrectuser2";

    static final String USER_EMAIL_CORRECT_2 = "usercorrect2@email.test";

    static final String USER_PASSWORD_CORRECT_2 = "testcorrectpassword2";

    @Page
    private SessionPage sessionPage;

    @Drone
    @SecondBrowser
    private WebDriver driver2;

    @Page
    private Sessions realmSessions;

    @Test
    public void testSingleSessionInvalidated() {
        loginAndCheckSession();
        // cannot pass to loginAndCheckSession becayse loginPage is not working together with driver2, therefore copypasta
        driver2.navigate().to(SessionSpringBootTest.SERVLET_URL);
        log.info(("current title is " + (driver2.getTitle())));
        Assert.assertTrue("Must be on login page", driver2.getTitle().toLowerCase().startsWith("log in to"));
        driver2.findElement(By.id("username")).sendKeys(AbstractSpringBootTest.USER_LOGIN);
        driver2.findElement(By.id("password")).sendKeys(AbstractSpringBootTest.USER_PASSWORD);
        driver2.findElement(By.id("password")).submit();
        Assert.assertTrue("Must be on session page", driver2.getTitle().equals(PAGE_TITLE));
        Assert.assertTrue("Counter must be 0", checkCounterInSource(driver2, 0));
        // Counter increased now
        driver2.navigate().to(SessionSpringBootTest.SERVLET_URL);
        Assert.assertTrue("Counter must be 1", checkCounterInSource(driver2, 1));
        // Logout in browser1
        driver.navigate().to(logoutPage(SessionSpringBootTest.SERVLET_URL));
        // Assert that I am logged out in browser1
        driver.navigate().to(SessionSpringBootTest.SERVLET_URL);
        Assert.assertTrue("Must be on login page", loginPage.isCurrent());
        // Assert that I am still logged in browser2 and same session is still preserved
        driver2.navigate().to(SessionSpringBootTest.SERVLET_URL);
        Assert.assertTrue("Must be on session page", driver2.getTitle().equals(PAGE_TITLE));
        Assert.assertTrue("Counter must be 2", checkCounterInSource(driver2, 2));
        driver2.navigate().to(logoutPage(SessionSpringBootTest.SERVLET_URL));
        Assert.assertTrue("Must be on login page", driver2.getTitle().toLowerCase().startsWith("log in to"));
    }

    @Test
    public void testSessionInvalidatedAfterFailedRefresh() {
        RealmResource realmResource = adminClient.realm(AbstractSpringBootTest.REALM_NAME);
        RealmRepresentation realmRep = realmResource.toRepresentation();
        ClientResource clientResource = null;
        for (ClientRepresentation clientRep : realmResource.clients().findAll()) {
            if (AbstractSpringBootTest.CLIENT_ID.equals(clientRep.getClientId())) {
                clientResource = realmResource.clients().get(clientRep.getId());
            }
        }
        Assert.assertNotNull(clientResource);
        clientResource.toRepresentation().setAdminUrl("");
        int origTokenLifespan = realmRep.getAccessCodeLifespan();
        realmRep.setAccessCodeLifespan(1);
        realmResource.update(realmRep);
        // Login
        loginAndCheckSession();
        // Logout
        String logoutUri = logoutPage(SessionSpringBootTest.SERVLET_URL);
        driver.navigate().to(logoutUri);
        // Assert that http session was invalidated
        driver.navigate().to(SessionSpringBootTest.SERVLET_URL);
        Assert.assertTrue("Must be on login page", loginPage.isCurrent());
        loginPage.login(AbstractSpringBootTest.USER_LOGIN, AbstractSpringBootTest.USER_PASSWORD);
        Assert.assertTrue("Must be on session page", sessionPage.isCurrent());
        Assert.assertEquals("Counter must be 0", 0, sessionPage.getCounter());
        clientResource.toRepresentation().setAdminUrl(AbstractSpringBootTest.BASE_URL);
        realmRep.setAccessCodeLifespan(origTokenLifespan);
        realmResource.update(realmRep);
    }

    @Test
    public void testAdminApplicationLogout() {
        loginAndCheckSession();
        // logout user2 with admin client
        UserRepresentation correct2 = realmsResouce().realm(AbstractSpringBootTest.REALM_NAME).users().search(SessionSpringBootTest.USER_LOGIN_CORRECT_2, null, null, null, null, null).get(0);
        realmsResouce().realm(AbstractSpringBootTest.REALM_NAME).users().get(correct2.getId()).logout();
        // user1 should be still logged with original httpSession in our browser window
        driver.navigate().to(SessionSpringBootTest.SERVLET_URL);
        Assert.assertTrue("Must be on session page", sessionPage.isCurrent());
        Assert.assertEquals("Counter must be 2", 2, sessionPage.getCounter());
        driver.navigate().to(logoutPage(SessionSpringBootTest.SERVLET_URL));
    }

    @Test
    public void testAccountManagementSessionsLogout() {
        loginAndCheckSession();
        realmSessions.navigateTo();
        realmSessions.logoutAll();
        // Assert I need to login again (logout was propagated to the app)
        loginAndCheckSession();
    }
}

