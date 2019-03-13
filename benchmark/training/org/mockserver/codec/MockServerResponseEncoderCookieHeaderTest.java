package org.mockserver.codec;


import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerResponseEncoderCookieHeaderTest {
    private MockServerResponseEncoder mockServerResponseEncoder;

    private List<Object> output;

    private HttpResponse httpResponse;

    @Test
    public void shouldOnlyMapACookieIfThereIsNoSetCookieHeader() throws UnsupportedEncodingException {
        // given
        // - an HttpResponse
        String cookieOne = "cookieName1=\"\"; Expires=Thu, 01-Jan-1970 00:00:10 GMT; Path=/";
        String cookieTwo = "cookieName2=\"cookie==Value2\"; Version=1; Comment=\"Anonymous cookie for site\"; Max-Age=15552000; Expires=Sat, 19-Mar-2016 18:43:26 GMT; Path=/";
        httpResponse.withHeaders(new Header("Set-Cookie", cookieOne), new Header("set-cookie", cookieTwo));
        httpResponse.withCookies(new Cookie("cookieName1", ""), new Cookie("cookieName2", "cookie==Value2"), new Cookie("cookieName3", "cookie==Value3"));
        // when
        mockServerResponseEncoder.encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.names(), Matchers.containsInAnyOrder("Set-Cookie", HttpHeaderNames.SET_COOKIE.toString(), HttpHeaderNames.CONTENT_LENGTH.toString(), HttpHeaderNames.SET_COOKIE.toString()));
        MatcherAssert.assertThat(headers.getAll("Set-Cookie"), Matchers.containsInAnyOrder(cookieOne, cookieTwo, "cookieName3=cookie==Value3"));
    }
}

