package org.mockserver.codec;


import ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET;
import com.google.common.net.MediaType;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.mappers.ContentTypeMapper;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.Cookie;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.StringBody;

import static HttpMethod.GET;
import static HttpMethod.OPTIONS;
import static HttpVersion.HTTP_1_1;
import static org.mockserver.model.Header.header;
import static org.mockserver.model.NottableString.string;
import static org.mockserver.model.Parameter.param;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerRequestDecoderTest {
    private MockServerRequestDecoder mockServerRequestDecoder;

    private List<Object> output;

    private FullHttpRequest fullHttpRequest;

    @Test
    public void shouldDecodeMethod() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, OPTIONS, "/uri");
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        NottableString method = getMethod();
        MatcherAssert.assertThat(method, Is.is(string("OPTIONS")));
    }

    @Test
    public void shouldDecodeQueryParameters() {
        // given
        String uri = "/uri?" + (("queryStringParameterNameOne=queryStringParameterValueOne_One&" + "queryStringParameterNameOne=queryStringParameterValueOne_Two&") + "queryStringParameterNameTwo=queryStringParameterValueTwo_One");
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, uri);
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        List<Parameter> queryStringParameters = getQueryStringParameterList();
        MatcherAssert.assertThat(queryStringParameters, Matchers.containsInAnyOrder(org.mockserver.model.Parameter.param("queryStringParameterNameOne", "queryStringParameterValueOne_One", "queryStringParameterValueOne_Two"), param("queryStringParameterNameTwo", "queryStringParameterValueTwo_One")));
    }

    @Test
    public void shouldDecodePath() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri");
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        HttpRequest httpRequest = ((HttpRequest) (output.get(0)));
        MatcherAssert.assertThat(httpRequest.getPath(), Is.is(string("/uri")));
    }

    @Test
    public void shouldDecodeHeaders() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri");
        fullHttpRequest.headers().add("headerName1", "headerValue1_1");
        fullHttpRequest.headers().add("headerName1", "headerValue1_2");
        fullHttpRequest.headers().add("headerName2", "headerValue2");
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        List<Header> headers = getHeaderList();
        MatcherAssert.assertThat(headers, Matchers.containsInAnyOrder(org.mockserver.model.Header.header("headerName1", "headerValue1_1", "headerValue1_2"), header("headerName2", "headerValue2")));
    }

    @Test
    public void shouldDecodeIsKeepAlive() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri");
        fullHttpRequest.headers().add("Connection", "keep-alive");
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        HttpRequest httpRequest = ((HttpRequest) (output.get(0)));
        MatcherAssert.assertThat(httpRequest.isKeepAlive(), Is.is(true));
    }

    @Test
    public void shouldDecodeIsNotKeepAlive() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri");
        fullHttpRequest.headers().add("Connection", "close");
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        HttpRequest httpRequest = ((HttpRequest) (output.get(0)));
        MatcherAssert.assertThat(httpRequest.isKeepAlive(), Is.is(false));
    }

    @Test
    public void shouldDecodeCookies() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri");
        fullHttpRequest.headers().add("Cookie", "cookieName1=cookieValue1  ; cookieName2=cookieValue2;   ");
        fullHttpRequest.headers().add("Cookie", "cookieName3  =cookieValue3        ;");
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        List<Cookie> cookies = getCookieList();
        MatcherAssert.assertThat(cookies, Matchers.containsInAnyOrder(Cookie.cookie("cookieName1", "cookieValue1  "), Cookie.cookie("cookieName2", "cookieValue2"), Cookie.cookie("cookieName3", "cookieValue3        ")));
    }

    @Test
    public void shouldDecodeCookiesWithEmbeddedEquals() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri");
        fullHttpRequest.headers().add("Cookie", "cookieName1=cookie=Value1  ; cookieName2=cookie==Value2;   ");
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        List<Cookie> cookies = getCookieList();
        MatcherAssert.assertThat(cookies, Matchers.containsInAnyOrder(Cookie.cookie("cookieName1", "cookie=Value1  "), Cookie.cookie("cookieName2", "cookie==Value2")));
    }

    /* Test is significant because popular Java REST library Jersey adds $Version=1 to all cookies
    in line with RFC2965's recommendation (even though RFC2965 is now marked "Obsolete" by
    RFC6265, this is still common and not hard to handle).
     */
    @Test
    public void shouldDecodeCookiesWithRFC2965StyleAttributes() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri");
        fullHttpRequest.headers().add("Cookie", "$Version=1; Customer=WILE_E_COYOTE; $Path=/acme");
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        List<Cookie> cookies = getCookieList();
        MatcherAssert.assertThat(cookies, Matchers.containsInAnyOrder(Cookie.cookie("Customer", "WILE_E_COYOTE")));
    }

    @Test
    public void shouldDecodeBodyWithContentTypeAndNoCharset() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri", Unpooled.wrappedBuffer("A normal string with ASCII characters".getBytes(DEFAULT_HTTP_CHARACTER_SET)));
        fullHttpRequest.headers().add(HttpHeaderNames.CONTENT_TYPE, MediaType.create("text", "plain").toString());
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        Body body = getBody();
        MatcherAssert.assertThat(body, Is.<Body>is(StringBody.exact("A normal string with ASCII characters", MediaType.create("text", "plain"))));
    }

    @Test
    public void shouldDecodeBodyWithNoContentType() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri", Unpooled.wrappedBuffer("A normal string with ASCII characters".getBytes(DEFAULT_HTTP_CHARACTER_SET)));
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        Body body = getBody();
        MatcherAssert.assertThat(body, Is.<Body>is(StringBody.exact("A normal string with ASCII characters")));
    }

    @Test
    public void shouldTransmitUnencodableCharacters() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri", Unpooled.wrappedBuffer("Euro sign: \u20ac".getBytes(DEFAULT_HTTP_CHARACTER_SET)));
        fullHttpRequest.headers().add(HttpHeaderNames.CONTENT_TYPE, MediaType.create("text", "plain").toString());
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        Body body = getBody();
        MatcherAssert.assertThat(body.getRawBytes(), Is.is("Euro sign: \u20ac".getBytes(DEFAULT_HTTP_CHARACTER_SET)));
        MatcherAssert.assertThat(((String) (body.getValue())), Is.is(new String("Euro sign: \u20ac".getBytes(DEFAULT_HTTP_CHARACTER_SET), ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET)));
    }

    @Test
    public void shouldUseDefaultCharsetIfCharsetNotSupported() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri", Unpooled.wrappedBuffer("A normal string with ASCII characters".getBytes(DEFAULT_HTTP_CHARACTER_SET)));
        fullHttpRequest.headers().add(HttpHeaderNames.CONTENT_TYPE, "plain/text; charset=invalid-charset");
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        Body body = getBody();
        MatcherAssert.assertThat(body, Is.<Body>is(new StringBody("A normal string with ASCII characters", "A normal string with ASCII characters".getBytes(StandardCharsets.UTF_8), false, MediaType.parse("plain/text; charset=invalid-charset"))));
    }

    @Test
    public void shouldDecodeBodyWithUTF8ContentType() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri", Unpooled.wrappedBuffer("avro i\u015far\u0259si: \u20ac".getBytes(StandardCharsets.UTF_8)));
        fullHttpRequest.headers().add(HttpHeaderNames.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.toString());
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        Body body = getBody();
        MatcherAssert.assertThat(body, Is.<Body>is(StringBody.exact("avro i\u015far\u0259si: \u20ac", StandardCharsets.UTF_8)));
    }

    @Test
    public void shouldDecodeBodyWithUTF16ContentType() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri", Unpooled.wrappedBuffer("?????".getBytes(StandardCharsets.UTF_16)));
        fullHttpRequest.headers().add(HttpHeaderNames.CONTENT_TYPE, MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16).toString());
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        Body body = getBody();
        MatcherAssert.assertThat(body, Is.<Body>is(StringBody.exact("?????", StandardCharsets.UTF_16)));
    }

    @Test
    public void shouldDecodeBinaryBody() {
        // given
        fullHttpRequest = new DefaultFullHttpRequest(HTTP_1_1, GET, "/uri", Unpooled.wrappedBuffer("some_random_bytes".getBytes(StandardCharsets.UTF_8)));
        fullHttpRequest.headers().add(HttpHeaderNames.CONTENT_TYPE, MediaType.JPEG);
        // when
        mockServerRequestDecoder.decode(null, fullHttpRequest, output);
        // then
        Body body = getBody();
        MatcherAssert.assertThat(body, Is.<Body>is(BinaryBody.binary("some_random_bytes".getBytes(StandardCharsets.UTF_8))));
    }
}

