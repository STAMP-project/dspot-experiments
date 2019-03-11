package org.keycloak.testsuite.cookies;


import java.io.IOException;
import java.util.Calendar;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.hamcrest.Matchers;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.services.managers.AuthenticationSessionManager;
import org.keycloak.testsuite.AbstractKeycloakTest;
import org.keycloak.testsuite.pages.LoginPage;
import org.keycloak.testsuite.util.OAuthClient;
import org.keycloak.testsuite.util.URLUtils;
import org.openqa.selenium.Cookie;


/**
 *
 *
 * @author <a href="mailto:mkanis@redhat.com">Martin Kanis</a>
 */
public class CookiesPathTest extends AbstractKeycloakTest {
    @Page
    protected LoginPage loginPage;

    public static final String AUTH_SESSION_VALUE = "1869c345-2f90-4724-936d-a1a1ef41dea7";

    public static final String AUTH_SESSION_VALUE_NODE = "1869c345-2f90-4724-936d-a1a1ef41dea7.host";

    public static final String OLD_COOKIE_PATH = "/auth/realms/foo";

    public static final String KC_RESTART = "KC_RESTART";

    private CloseableHttpClient httpClient = null;

    @Test
    public void testCookiesPath() {
        // navigate to "/realms/foo/account" and remove cookies in the browser for the current path
        // first access to the path means there are no cookies being sent
        // we are redirected to login page and Keycloak sets cookie's path to "/auth/realms/foo/"
        deleteAllCookiesForRealm("foo");
        Assert.assertTrue("There shouldn't be any cookies sent!", driver.manage().getCookies().isEmpty());
        // refresh the page and cookies are sent within the request
        driver.navigate().refresh();
        Set<Cookie> cookies = driver.manage().getCookies();
        Assert.assertTrue("There should be cookies sent!", ((cookies.size()) > 0));
        // check cookie's path, for some reason IE adds extra slash to the beginning of the path
        cookies.stream().forEach(( cookie) -> Assert.assertThat(cookie.getPath(), Matchers.endsWith("/auth/realms/foo/")));
        // now navigate to realm which name overlaps the first realm and delete cookies for that realm (foobar)
        deleteAllCookiesForRealm("foobar");
        // cookies shouldn't be sent for the first access to /realms/foobar/account
        // At this moment IE would sent cookies for /auth/realms/foo without the fix
        cookies = driver.manage().getCookies();
        Assert.assertTrue("There shouldn't be any cookies sent!", cookies.isEmpty());
        // refresh the page and check if correct cookies were sent
        driver.navigate().refresh();
        cookies = driver.manage().getCookies();
        Assert.assertTrue("There should be cookies sent!", ((cookies.size()) > 0));
        // check cookie's path, for some reason IE adds extra slash to the beginning of the path
        cookies.stream().forEach(( cookie) -> Assert.assertThat(cookie.getPath(), Matchers.endsWith("/auth/realms/foobar/")));
        // lets back to "/realms/foo/account" to test the cookies for "foo" realm are still there and haven't been (correctly) sent to "foobar"
        URLUtils.navigateToUri(((OAuthClient.AUTH_SERVER_ROOT) + "/realms/foo/account"));
        cookies = driver.manage().getCookies();
        Assert.assertTrue("There should be cookies sent!", ((cookies.size()) > 0));
        cookies.stream().forEach(( cookie) -> Assert.assertThat(cookie.getPath(), Matchers.endsWith("/auth/realms/foo/")));
    }

    @Test
    public void testMultipleCookies() throws IOException {
        String requestURI = (OAuthClient.AUTH_SERVER_ROOT) + "/realms/foo/account";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        // create old cookie with wrong path
        BasicClientCookie wrongCookie = new BasicClientCookie(AuthenticationSessionManager.AUTH_SESSION_ID, CookiesPathTest.AUTH_SESSION_VALUE);
        wrongCookie.setDomain("localhost");
        wrongCookie.setPath(CookiesPathTest.OLD_COOKIE_PATH);
        wrongCookie.setExpiryDate(calendar.getTime());
        // obtain new cookies
        CookieStore cookieStore = getCorrectCookies(requestURI);
        cookieStore.addCookie(wrongCookie);
        Assert.assertThat(cookieStore.getCookies(), Matchers.hasSize(3));
        login(requestURI, cookieStore);
        // old cookie has been removed
        // now we have AUTH_SESSION_ID, KEYCLOAK_IDENTITY, KEYCLOAK_SESSION
        Assert.assertThat(cookieStore.getCookies().stream().map(org.apache.http.cookie.Cookie::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("AUTH_SESSION_ID", "KEYCLOAK_IDENTITY", "KEYCLOAK_SESSION", "OAuth_Token_Request_State"));
        // does each cookie's path end with "/"
        cookieStore.getCookies().stream().filter(( c) -> !("OAuth_Token_Request_State".equals(c.getName()))).map(org.apache.http.cookie.Cookie::getPath).forEach(( path) -> Assert.assertThat(path, Matchers.endsWith("/")));
        // KEYCLOAK_SESSION should end by AUTH_SESSION_ID value
        String authSessionId = cookieStore.getCookies().stream().filter(( c) -> "AUTH_SESSION_ID".equals(c.getName())).findFirst().get().getValue();
        String KCSessionId = cookieStore.getCookies().stream().filter(( c) -> "KEYCLOAK_SESSION".equals(c.getName())).findFirst().get().getValue();
        String KCSessionSuffix = KCSessionId.split("/")[2];
        Assert.assertThat(authSessionId, Matchers.containsString(KCSessionSuffix));
    }

    @Test
    public void testOldCookieWithWrongPath() {
        Cookie wrongCookie = new Cookie(AuthenticationSessionManager.AUTH_SESSION_ID, CookiesPathTest.AUTH_SESSION_VALUE, null, CookiesPathTest.OLD_COOKIE_PATH, null, false, true);
        deleteAllCookiesForRealm("foo");
        // add old cookie with wrong path
        driver.manage().addCookie(wrongCookie);
        Set<Cookie> cookies = driver.manage().getCookies();
        Assert.assertThat(cookies, Matchers.hasSize(1));
        oauth.realm("foo").redirectUri(((OAuthClient.AUTH_SERVER_ROOT) + "/realms/foo/account")).clientId("account").openLoginForm();
        loginPage.login("foo", "password");
        // old cookie has been removed and new cookies have been added
        cookies = driver.manage().getCookies();
        Assert.assertThat(cookies, Matchers.hasSize(3));
        // does each cookie's path end with "/"
        cookies.stream().map(Cookie::getPath).forEach(( path) -> Assert.assertThat(path, Matchers.endsWith("/")));
        // KEYCLOAK_SESSION should end by AUTH_SESSION_ID value
        String authSessionId = cookies.stream().filter(( c) -> "AUTH_SESSION_ID".equals(c.getName())).findFirst().get().getValue();
        String KCSessionId = cookies.stream().filter(( c) -> "KEYCLOAK_SESSION".equals(c.getName())).findFirst().get().getValue();
        String KCSessionSuffix = KCSessionId.split("/")[2];
        Assert.assertThat(authSessionId, Matchers.containsString(KCSessionSuffix));
    }

    @Test
    public void testOldCookieWithNodeInValue() throws IOException {
        String requestURI = (OAuthClient.AUTH_SERVER_ROOT) + "/realms/foo/account";
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        // create old cookie with wrong path
        BasicClientCookie wrongCookie = new BasicClientCookie(AuthenticationSessionManager.AUTH_SESSION_ID, CookiesPathTest.AUTH_SESSION_VALUE_NODE);
        wrongCookie.setDomain("localhost");
        wrongCookie.setPath(CookiesPathTest.OLD_COOKIE_PATH);
        wrongCookie.setExpiryDate(calendar.getTime());
        // obtain new cookies
        CookieStore cookieStore = getCorrectCookies(requestURI);
        cookieStore.addCookie(wrongCookie);
        Assert.assertThat(cookieStore.getCookies(), Matchers.hasSize(3));
        login(requestURI, cookieStore);
        // old cookie has been removed
        // now we have AUTH_SESSION_ID, KEYCLOAK_IDENTITY, KEYCLOAK_SESSION, OAuth_Token_Request_State
        Assert.assertThat(cookieStore.getCookies().stream().map(org.apache.http.cookie.Cookie::getName).collect(Collectors.toList()), Matchers.containsInAnyOrder("AUTH_SESSION_ID", "KEYCLOAK_IDENTITY", "KEYCLOAK_SESSION", "OAuth_Token_Request_State"));
        // does each cookie's path end with "/"
        cookieStore.getCookies().stream().filter(( c) -> !("OAuth_Token_Request_State".equals(c.getName()))).map(org.apache.http.cookie.Cookie::getPath).forEach(( path) -> Assert.assertThat(path, Matchers.endsWith("/")));
        // KEYCLOAK_SESSION should end by AUTH_SESSION_ID value
        String authSessionId = cookieStore.getCookies().stream().filter(( c) -> "AUTH_SESSION_ID".equals(c.getName())).findFirst().get().getValue();
        String KCSessionId = cookieStore.getCookies().stream().filter(( c) -> "KEYCLOAK_SESSION".equals(c.getName())).findFirst().get().getValue();
        String KCSessionSuffix = KCSessionId.split("/")[2];
        Assert.assertThat(authSessionId, Matchers.containsString(KCSessionSuffix));
    }
}

