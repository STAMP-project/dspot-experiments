package org.mockserver.codec;


import ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.BinaryBody;

import static org.mockserver.model.HttpResponse.response;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerResponseEncoderBasicMappingTest {
    private MockServerResponseEncoder mockServerResponseEncoder;

    private List<Object> output;

    private HttpResponse httpResponse;

    @Test
    public void shouldEncodeHeaders() {
        // given
        httpResponse = response().withHeaders(new Header("headerName1", "headerValue1"), new Header("headerName2", "headerValue2_1", "headerValue2_2"));
        // when
        mockServerResponseEncoder.encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("headerName1"), Matchers.containsInAnyOrder("headerValue1"));
        MatcherAssert.assertThat(headers.getAll("headerName2"), Matchers.containsInAnyOrder("headerValue2_1", "headerValue2_2"));
    }

    @Test
    public void shouldEncodeNoHeaders() {
        // given
        httpResponse = response().withHeaders(((Header[]) (null)));
        // when
        mockServerResponseEncoder.encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.names(), Matchers.containsInAnyOrder(HttpHeaderNames.CONTENT_LENGTH.toString()));
        MatcherAssert.assertThat(headers.get("Content-Length"), Is.is("0"));
    }

    @Test
    public void shouldEncodeCookies() {
        // given
        httpResponse.withCookies(new Cookie("cookieName1", "cookieValue1"), new Cookie("cookieName2", "cookieValue2"));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.getAll("Set-Cookie"), Matchers.containsInAnyOrder("cookieName1=cookieValue1", "cookieName2=cookieValue2"));
    }

    @Test
    public void shouldEncodeNoCookies() {
        // given
        httpResponse.withCookies(((Cookie[]) (null)));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        HttpHeaders headers = headers();
        MatcherAssert.assertThat(headers.names(), Matchers.containsInAnyOrder(HttpHeaderNames.CONTENT_LENGTH.toString()));
        MatcherAssert.assertThat(headers.get("Content-Length"), Is.is("0"));
    }

    @Test
    public void shouldEncodeStatusCode() {
        // given
        httpResponse.withStatusCode(10);
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.status().code(), Is.is(10));
    }

    @Test
    public void shouldEncodeNoStatusCode() {
        // given
        httpResponse.withStatusCode(null);
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.status().code(), Is.is(200));
    }

    @Test
    public void shouldEncodeReasonPhrase() {
        // given
        httpResponse.withReasonPhrase("someReasonPhrase");
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.status().reasonPhrase(), Is.is("someReasonPhrase"));
    }

    @Test
    public void shouldEncodeNoReasonPhrase() {
        // given
        httpResponse.withReasonPhrase(null);
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.status().reasonPhrase(), Is.is("OK"));
    }

    @Test
    public void shouldEncodeNoReasonPhraseAndStatusCode() {
        // given
        httpResponse.withStatusCode(404);
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.status().reasonPhrase(), Is.is("Not Found"));
    }

    @Test
    public void shouldEncodeStringBody() {
        // given
        httpResponse.withBody("somebody");
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.content().toString(DEFAULT_HTTP_CHARACTER_SET), Is.is("somebody"));
    }

    @Test
    public void shouldEncodeBinaryBody() {
        // given
        httpResponse.withBody(BinaryBody.binary("somebody".getBytes(StandardCharsets.UTF_8)));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(new String(fullHttpResponse.content().array()), Is.is("somebody"));
    }

    @Test
    public void shouldEncodeNullBody() {
        // given
        httpResponse.withBody(((String) (null)));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.content().toString(DEFAULT_HTTP_CHARACTER_SET), Is.is(""));
    }
}

