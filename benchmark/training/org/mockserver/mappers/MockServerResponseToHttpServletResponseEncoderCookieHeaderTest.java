package org.mockserver.mappers;


import HttpStatusCode.OK_200;
import java.io.UnsupportedEncodingException;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;
import org.springframework.mock.web.MockHttpServletResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerResponseToHttpServletResponseEncoderCookieHeaderTest {
    @Test
    public void shouldOnlyMapACookieIfThereIsNoSetCookieHeader() throws UnsupportedEncodingException {
        // given
        // - an HttpResponse
        HttpResponse httpResponse = new HttpResponse();
        String cookieOne = "cookieName1=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/";
        String cookieTwo = "cookieName2=\"cookie==Value2\"; Version=1; Comment=\"Anonymous cookie for site\"; Max-Age=15552000; Expires=Sat, 19-Mar-2016 18:43:26 GMT; Path=/";
        httpResponse.withHeaders(new Header("Set-Cookie", cookieOne), new Header("Set-Cookie", cookieTwo));
        httpResponse.withCookies(new Cookie("cookieName1", ""), new Cookie("cookieName2", "cookie==Value2"), new Cookie("cookieName3", "cookie==Value3"));
        // - an HttpServletResponse
        MockHttpServletResponse httpServletResponse = new MockHttpServletResponse();
        // when
        new MockServerResponseToHttpServletResponseEncoder().mapMockServerResponseToHttpServletResponse(httpResponse, httpServletResponse);
        // then
        Assert.assertEquals(OK_200.code(), httpServletResponse.getStatus());
        Assert.assertThat(httpServletResponse.getHeaders("Set-Cookie"), Matchers.containsInAnyOrder(cookieOne, cookieTwo, "cookieName3=cookie==Value3"));
        Assert.assertThat(httpServletResponse.getHeaderNames(), Matchers.contains("Set-Cookie"));
    }
}

