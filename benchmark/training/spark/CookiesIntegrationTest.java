package spark;


import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;


/**
 * System tests for the Cookies support.
 *
 * @author dreambrother
 */
public class CookiesIntegrationTest {
    private static final String DEFAULT_HOST_URL = "http://localhost:4567";

    private HttpClient httpClient = HttpClientBuilder.create().build();

    @Test
    public void testEmptyCookies() {
        httpPost("/assertNoCookies");
    }

    @Test
    public void testCreateCookie() {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost(((("/setCookie?cookieName=" + cookieName) + "&cookieValue=") + cookieValue));
        httpPost(((("/assertHasCookie?cookieName=" + cookieName) + "&cookieValue=") + cookieValue));
    }

    @Test
    public void testRemoveCookie() {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost(((("/setCookie?cookieName=" + cookieName) + "&cookieValue=") + cookieValue));
        httpPost(((("/removeCookie?cookieName=" + cookieName) + "&cookieValue=") + cookieValue));
        httpPost("/assertNoCookies");
    }

    @Test
    public void testRemoveCookieWithPath() {
        String cookieName = "testCookie";
        String cookieValue = "testCookieValue";
        httpPost(((("/path/setCookieWithPath?cookieName=" + cookieName) + "&cookieValue=") + cookieValue));
        // for sanity, check that cookie is not sent with request if path doesn't match
        httpPost("/assertNoCookies");
        // now remove cookie with matching path
        httpPost(((("/path/removeCookieWithPath?cookieName=" + cookieName) + "&cookieValue=") + cookieValue));
        httpPost("/path/assertNoCookies");
    }
}

