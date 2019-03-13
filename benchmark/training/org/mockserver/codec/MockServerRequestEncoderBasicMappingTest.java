package org.mockserver.codec;


import HttpMethod.OPTIONS;
import com.google.common.net.MediaType;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.Parameter;
import org.mockserver.model.StringBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerRequestEncoderBasicMappingTest {
    private MockServerRequestEncoder mockServerRequestEncoder;

    private List<Object> output;

    private HttpRequest httpRequest;

    @Test
    public void shouldEncodeMethod() {
        // given
        httpRequest.withMethod("OPTIONS");
        // when
        mockServerRequestEncoder.encode(null, httpRequest, output);
        // then
        HttpMethod method = method();
        MatcherAssert.assertThat(method, Is.is(OPTIONS));
    }

    @Test
    public void shouldEncodeQueryParameters() {
        // given
        httpRequest.withPath("/uri").withQueryStringParameters(Parameter.param("queryStringParameterNameOne", "queryStringParameterValueOne_One", "queryStringParameterValueOne_Two"), Parameter.param("queryStringParameterNameTwo", "queryStringParameterValueTwo_One"));
        // when
        mockServerRequestEncoder.encode(null, httpRequest, output);
        // then
        String uri = uri();
        MatcherAssert.assertThat(uri, Is.is(("/uri?" + (("queryStringParameterNameOne=queryStringParameterValueOne_One&" + "queryStringParameterNameOne=queryStringParameterValueOne_Two&") + "queryStringParameterNameTwo=queryStringParameterValueTwo_One"))));
    }

    @Test
    public void shouldEscapeQueryParameters() {
        // given
        httpRequest.withPath("/uri").withQueryStringParameters(Parameter.param("parameter name with spaces", "a value with double \"quotes\" and spaces"), Parameter.param("another parameter", "a value with single \'quotes\' and spaces"));
        // when
        mockServerRequestEncoder.encode(null, httpRequest, output);
        // then
        String uri = uri();
        MatcherAssert.assertThat(uri, Is.is(("/uri?" + ("parameter%20name%20with%20spaces=a%20value%20with%20double%20%22quotes%22%20and%20spaces&" + "another%20parameter=a%20value%20with%20single%20%27quotes%27%20and%20spaces"))));
    }

    @Test
    public void shouldEncodePath() {
        // given
        httpRequest.withPath("/other_path");
        // when
        mockServerRequestEncoder.encode(null, httpRequest, output);
        // then
        String uri = uri();
        MatcherAssert.assertThat(uri, Is.is("/other_path"));
    }

    @Test
    public void shouldEncodeHeaders() {
        // given
        httpRequest.withHeaders(new Header("headerName1", "headerValue1"), new Header("headerName2", "headerValue2_1", "headerValue2_2")).withHeader(HttpHeaderNames.HOST.toString(), "localhost");
        // when
        mockServerRequestEncoder.encode(null, httpRequest, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("headerName1"), Matchers.containsInAnyOrder("headerValue1"));
        MatcherAssert.assertThat(headers.getAll("headerName2"), Matchers.containsInAnyOrder("headerValue2_1", "headerValue2_2"));
        MatcherAssert.assertThat(headers.getAll(HttpHeaderNames.HOST.toString()), Matchers.containsInAnyOrder("localhost"));
    }

    @Test
    public void shouldEncodeNoHeaders() {
        // given
        httpRequest.withHeaders(((Header[]) (null)));
        // when
        mockServerRequestEncoder.encode(null, httpRequest, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.names(), Matchers.containsInAnyOrder("accept-encoding", "content-length", "connection"));
        MatcherAssert.assertThat(headers.getAll("Accept-Encoding"), Matchers.containsInAnyOrder("gzip,deflate"));
        MatcherAssert.assertThat(headers.getAll("Content-Length"), Matchers.containsInAnyOrder("0"));
        MatcherAssert.assertThat(headers.getAll("Connection"), Matchers.containsInAnyOrder("keep-alive"));
    }

    @Test
    public void shouldEncodeCookies() {
        // given
        httpRequest.withCookies(new Cookie("cookieName1", "cookieValue1"), new Cookie("cookieName2", "cookieValue2"));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("Cookie"), Is.is(Arrays.asList("cookieName1=cookieValue1; cookieName2=cookieValue2")));
    }

    @Test
    public void shouldEncodeNoCookies() {
        // given
        httpRequest.withCookies(((Cookie[]) (null)));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("Cookie"), emptyIterable());
    }

    @Test
    public void shouldEncodeStringBody() {
        // given
        httpRequest.withBody("somebody");
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(StandardCharsets.UTF_8), Is.is("somebody"));
        MatcherAssert.assertThat(fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE), Matchers.nullValue());
    }

    @Test
    public void shouldEncodeStringBodyWithContentType() {
        // given
        httpRequest.withBody(StringBody.exact("somebody", MediaType.HTML_UTF_8));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(StandardCharsets.UTF_8), Is.is("somebody"));
        MatcherAssert.assertThat(fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE), Is.is(MediaType.HTML_UTF_8.toString()));
    }

    @Test
    public void shouldEncodeBinaryBody() {
        // given
        httpRequest.withBody(BinaryBody.binary("somebody".getBytes(StandardCharsets.UTF_8)));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().array(), Is.is("somebody".getBytes(StandardCharsets.UTF_8)));
        MatcherAssert.assertThat(fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE), Matchers.nullValue());
    }

    @Test
    public void shouldEncodeBinaryBodyWithContentType() {
        // given
        httpRequest.withBody(BinaryBody.binary("somebody".getBytes(StandardCharsets.UTF_8), MediaType.QUICKTIME));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().array(), Is.is("somebody".getBytes(StandardCharsets.UTF_8)));
        MatcherAssert.assertThat(fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE), Is.is(MediaType.QUICKTIME.toString()));
    }

    @Test
    public void shouldEncodeNullBody() {
        // given
        httpRequest.withBody(((String) (null)));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(StandardCharsets.UTF_8), Is.is(""));
    }
}

