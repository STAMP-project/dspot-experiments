package org.mockserver.codec;


import HttpResponseStatus.METHOD_NOT_ALLOWED;
import com.google.common.net.MediaType;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.Body;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.StringBody;

import static HttpResponseStatus.METHOD_NOT_ALLOWED;
import static HttpResponseStatus.OK;
import static HttpVersion.HTTP_1_1;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerResponseDecoderTest {
    private MockServerResponseDecoder mockServerResponseDecoder;

    private List<Object> output;

    private FullHttpResponse fullHttpResponse;

    @Test
    public void shouldDecodeStatusCode() {
        // given
        fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, METHOD_NOT_ALLOWED);
        // when
        mockServerResponseDecoder.decode(null, fullHttpResponse, output);
        // then
        HttpResponse httpResponse = ((HttpResponse) (output.get(0)));
        MatcherAssert.assertThat(httpResponse.getStatusCode(), Is.is(METHOD_NOT_ALLOWED.code()));
    }

    @Test
    public void shouldDecodeHeaders() {
        // given
        fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK);
        fullHttpResponse.headers().add("headerName1", "headerValue1_1");
        fullHttpResponse.headers().add("headerName1", "headerValue1_2");
        fullHttpResponse.headers().add("headerName2", "headerValue2");
        // when
        mockServerResponseDecoder.decode(null, fullHttpResponse, output);
        // then
        List<Header> headers = getHeaderList();
        MatcherAssert.assertThat(headers, Matchers.containsInAnyOrder(Header.header("headerName1", "headerValue1_1", "headerValue1_2"), Header.header("headerName2", "headerValue2")));
    }

    @Test
    public void shouldDecodeCookies() {
        // given
        fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK);
        fullHttpResponse.headers().add("Cookie", "cookieName1=cookieValue1  ; cookieName2=cookieValue2;   ");
        fullHttpResponse.headers().add("Cookie", "cookieName3  =cookieValue3_1; cookieName4=cookieValue3_2");
        // when
        mockServerResponseDecoder.decode(null, fullHttpResponse, output);
        // then
        List<Cookie> cookies = getCookieList();
        MatcherAssert.assertThat(cookies, Matchers.containsInAnyOrder(Cookie.cookie("cookieName1", "cookieValue1"), Cookie.cookie("cookieName2", "cookieValue2"), Cookie.cookie("cookieName3", "cookieValue3_1"), Cookie.cookie("cookieName4", "cookieValue3_2")));
    }

    @Test
    public void shouldDecodeCookiesWithEmbeddedEquals() {
        // given
        fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK);
        fullHttpResponse.headers().add("Cookie", "cookieName1=cookie=Value1  ; cookieName2=cookie==Value2;   ");
        // when
        mockServerResponseDecoder.decode(null, fullHttpResponse, output);
        // then
        List<Cookie> cookies = getCookieList();
        MatcherAssert.assertThat(cookies, Matchers.containsInAnyOrder(Cookie.cookie("cookieName1", "cookie=Value1"), Cookie.cookie("cookieName2", "cookie==Value2")));
    }

    @Test
    public void shouldDecodeUTF8Body() {
        // given
        fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("some_random_string".getBytes(StandardCharsets.UTF_8)));
        fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, MediaType.create("text", "plain").toString());
        // when
        mockServerResponseDecoder.decode(null, fullHttpResponse, output);
        // then
        Body body = getBody();
        MatcherAssert.assertThat(body, Is.<Body>is(StringBody.exact("some_random_string", MediaType.create("text", "plain"))));
    }

    @Test
    public void shouldDecodeUTF16Body() {
        // given
        fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("?????".getBytes(StandardCharsets.UTF_16)));
        fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16).toString());
        // when
        mockServerResponseDecoder.decode(null, fullHttpResponse, output);
        // then
        Body body = getBody();
        MatcherAssert.assertThat(body, Is.<Body>is(StringBody.exact("?????", MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16))));
    }

    @Test
    public void shouldDecodeBinaryBody() {
        // given
        fullHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer("some_random_bytes".getBytes(StandardCharsets.UTF_8)));
        fullHttpResponse.headers().add(HttpHeaderNames.CONTENT_TYPE, "image/jpeg");
        // when
        mockServerResponseDecoder.decode(null, fullHttpResponse, output);
        // then
        Body body = getBody();
        MatcherAssert.assertThat(body, Is.<Body>is(BinaryBody.binary("some_random_bytes".getBytes(StandardCharsets.UTF_8))));
    }
}

