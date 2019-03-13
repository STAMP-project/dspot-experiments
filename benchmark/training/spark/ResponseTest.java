package spark;


import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;


public class ResponseTest {
    private Response response;

    private HttpServletResponse httpServletResponse;

    private ArgumentCaptor<Cookie> cookieArgumentCaptor;

    @Test
    public void testConstructor_whenHttpServletResponseParameter() {
        HttpServletResponse returnResponse = Whitebox.getInternalState(response, "response");
        Assert.assertSame("Should be the same the HttpServletResponse object for httpServletResponse and returnResponse", httpServletResponse, returnResponse);
    }

    @Test
    public void testSetStatus() {
        final int finalStatusCode = HttpServletResponse.SC_OK;
        response.status(finalStatusCode);
        Mockito.verify(httpServletResponse).setStatus(finalStatusCode);
    }

    @Test
    public void testGetStatus() {
        response.status();
        Mockito.verify(httpServletResponse).getStatus();
    }

    @Test
    public void testSetType() {
        final String finalType = "text/html";
        response.type(finalType);
        Mockito.verify(httpServletResponse).setContentType(finalType);
    }

    @Test
    public void testGetType() {
        response.type();
        Mockito.verify(httpServletResponse).getContentType();
    }

    @Test
    public void testSetBody() {
        final String finalBody = "Hello world!";
        response.body(finalBody);
        String returnBody = Whitebox.getInternalState(response, "body");
        Assert.assertEquals("Should return body specified", finalBody, returnBody);
    }

    @Test
    public void testGetBody() {
        final String finalBody = "Hello world!";
        Whitebox.setInternalState(response, "body", finalBody);
        String returnBody = response.body();
        Assert.assertEquals("Should return body specified", finalBody, returnBody);
    }

    @Test
    public void testRaw() {
        HttpServletResponse returnResponse = response.raw();
        Assert.assertSame("Should be the same the HttpServletResponse object for httpServletResponse and returnResponse", httpServletResponse, returnResponse);
    }

    @Test
    public void testHeader() {
        final String finalHeaderKey = "Content-Length";
        final String finalHeaderValue = "32";
        response.header(finalHeaderKey, finalHeaderValue);
        Mockito.verify(httpServletResponse).addHeader(finalHeaderKey, finalHeaderValue);
    }

    @Test
    public void testCookie_whenNameAndValueParameters_shouldAddCookieSuccessfully() {
        final String finalDomain = "";
        final String finalPath = "";
        final String finalName = "cookie_name";
        final String finalValue = "Test Cookie";
        final int finalMaxAge = -1;
        final boolean finalSecured = false;
        final boolean finalHttpOnly = false;
        response.cookie(finalName, finalValue);
        Mockito.verify(httpServletResponse).addCookie(cookieArgumentCaptor.capture());
        validateCookieContent(cookieArgumentCaptor.getValue(), finalDomain, finalPath, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
    }

    @Test
    public void testCookie_whenNameValueAndMaxAgeParameters_shouldAddCookieSuccessfully() {
        final String finalDomain = "";
        final String finalPath = "";
        final String finalName = "cookie_name";
        final String finalValue = "Test Cookie";
        final int finalMaxAge = 86400;
        final boolean finalSecured = false;
        final boolean finalHttpOnly = false;
        response.cookie(finalName, finalValue, finalMaxAge);
        Mockito.verify(httpServletResponse).addCookie(cookieArgumentCaptor.capture());
        validateCookieContent(cookieArgumentCaptor.getValue(), finalDomain, finalPath, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
    }

    @Test
    public void testCookie_whenNameValueMaxAgeAndSecuredParameters_shouldAddCookieSuccessfully() {
        final String finalDomain = "";
        final String finalPath = "";
        final String finalName = "cookie_name";
        final String finalValue = "Test Cookie";
        final int finalMaxAge = 86400;
        final boolean finalSecured = true;
        final boolean finalHttpOnly = false;
        response.cookie(finalName, finalValue, finalMaxAge, finalSecured);
        Mockito.verify(httpServletResponse).addCookie(cookieArgumentCaptor.capture());
        validateCookieContent(cookieArgumentCaptor.getValue(), finalDomain, finalPath, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
    }

    @Test
    public void testCookie_whenNameValueMaxAgeSecuredAndHttpOnlyParameters_shouldAddCookieSuccessfully() {
        final String finalDomain = "";
        final String finalPath = "";
        final String finalName = "cookie_name";
        final String finalValue = "Test Cookie";
        final int finalMaxAge = 86400;
        final boolean finalSecured = true;
        final boolean finalHttpOnly = true;
        response.cookie(finalName, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
        Mockito.verify(httpServletResponse).addCookie(cookieArgumentCaptor.capture());
        validateCookieContent(cookieArgumentCaptor.getValue(), finalDomain, finalPath, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
    }

    @Test
    public void testCookie_whenPathNameValueMaxAgeAndSecuredParameters_shouldAddCookieSuccessfully() {
        final String finalDomain = "";
        final String finalPath = "/cookie/SetCookie";
        final String finalName = "cookie_name";
        final String finalValue = "Test Cookie";
        final int finalMaxAge = 86400;
        final boolean finalSecured = true;
        final boolean finalHttpOnly = false;
        response.cookie(finalPath, finalName, finalValue, finalMaxAge, finalSecured);
        Mockito.verify(httpServletResponse).addCookie(cookieArgumentCaptor.capture());
        validateCookieContent(cookieArgumentCaptor.getValue(), finalDomain, finalPath, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
    }

    @Test
    public void testCookie_whenPathNameValueMaxAgeSecuredAndHttpOnlyParameters_shouldAddCookieSuccessfully() {
        final String finalDomain = "";
        final String finalPath = "/cookie/SetCookie";
        final String finalName = "cookie_name";
        final String finalValue = "Test Cookie";
        final int finalMaxAge = 86400;
        final boolean finalSecured = true;
        final boolean finalHttpOnly = true;
        response.cookie(finalPath, finalName, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
        Mockito.verify(httpServletResponse).addCookie(cookieArgumentCaptor.capture());
        validateCookieContent(cookieArgumentCaptor.getValue(), finalDomain, finalPath, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
    }

    @Test
    public void testCookie_whenDomainPathNameValueMaxAgeSecuredAndHttpOnlyParameters_shouldAddCookieSuccessfully() {
        final String finalDomain = "example.com";
        final String finalPath = "/cookie/SetCookie";
        final String finalName = "cookie_name";
        final String finalValue = "Test Cookie";
        final int finalMaxAge = 86400;
        final boolean finalSecured = true;
        final boolean finalHttpOnly = true;
        response.cookie(finalDomain, finalPath, finalName, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
        Mockito.verify(httpServletResponse).addCookie(cookieArgumentCaptor.capture());
        validateCookieContent(cookieArgumentCaptor.getValue(), finalDomain, finalPath, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
    }

    @Test
    public void testRemoveCookie_shouldModifyPropertiesFromCookieSuccessfully() {
        final String finalPath = "/cookie/SetCookie";
        final String finalName = "cookie_name";
        final String finalValue = "Test Cookie";
        final int finalMaxAge = 86400;
        final boolean finalSecured = true;
        final boolean finalHttpOnly = true;
        response.cookie(finalPath, finalName, finalValue, finalMaxAge, finalSecured, finalHttpOnly);
        response.removeCookie(finalName);
        Mockito.verify(httpServletResponse, Mockito.times(2)).addCookie(cookieArgumentCaptor.capture());
        Assert.assertEquals("Should return empty value for the given cookie name", "", cookieArgumentCaptor.getValue().getValue());
        Assert.assertEquals("Should return an 0 for maximum cookie age", 0, cookieArgumentCaptor.getValue().getMaxAge());
    }

    @Test
    public void testRedirect_whenLocationParameter_shouldModifyStatusCodeSuccessfully() throws Exception {
        // NOSONAR
        final String finalLocation = "/test";
        response.redirect(finalLocation);
        Mockito.verify(httpServletResponse).sendRedirect(finalLocation);
    }

    @Test
    public void testRedirect_whenLocationAndHttpStatusCodeParameters_shouldModifyStatusCodeSuccessfully() throws Exception {
        // NOSONAR
        final String finalLocation = "/test";
        int finalStatusCode = HttpServletResponse.SC_BAD_GATEWAY;
        response.redirect(finalLocation, finalStatusCode);
        Mockito.verify(httpServletResponse).setStatus(finalStatusCode);
        Mockito.verify(httpServletResponse).setHeader("Location", finalLocation);
        Mockito.verify(httpServletResponse).setHeader("Connection", "close");
        Mockito.verify(httpServletResponse).sendError(finalStatusCode);
    }
}

