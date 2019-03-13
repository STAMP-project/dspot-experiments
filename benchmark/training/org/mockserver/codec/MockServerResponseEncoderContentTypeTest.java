package org.mockserver.codec;


import CharsetUtil.UTF_8;
import ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET;
import com.google.common.net.MediaType;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.Header;
import org.mockserver.model.HttpResponse;
import org.mockserver.model.JsonBody;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;
import org.mockserver.model.StringBody;
import org.mockserver.model.XmlBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerResponseEncoderContentTypeTest {
    private List<Object> output;

    private HttpResponse httpResponse;

    @Test
    public void shouldDecodeBodyWithContentTypeAndNoCharset() {
        // given
        httpResponse.withBody("avro i\u015far\u0259si: \u20ac");
        httpResponse.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.create("text", "plain").toString()));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.content().array(), Is.is("avro i\u015far\u0259si: \u20ac".getBytes(UTF_8)));
    }

    @Test
    public void shouldDecodeBodyWithNoContentType() {
        // given
        httpResponse.withBody("avro i\u015far\u0259si: \u20ac");
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.content().array(), Is.is("avro i\u015far\u0259si: \u20ac".getBytes(DEFAULT_HTTP_CHARACTER_SET)));
    }

    @Test
    public void shouldTransmitUnencodableCharacters() {
        // given
        httpResponse.withBody("avro i\u015far\u0259si: \u20ac", DEFAULT_HTTP_CHARACTER_SET);
        httpResponse.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.create("text", "plain").toString()));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.content().array(), Is.is("avro i\u015far\u0259si: \u20ac".getBytes(DEFAULT_HTTP_CHARACTER_SET)));
    }

    @Test
    public void shouldUseDefaultCharsetIfCharsetNotSupported() {
        // given
        httpResponse.withBody("avro i\u015far\u0259si: \u20ac");
        httpResponse.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), "text/plain; charset=invalid-charset"));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.content().array(), Is.is("avro i\u015far\u0259si: \u20ac".getBytes(DEFAULT_HTTP_CHARACTER_SET)));
    }

    @Test
    public void shouldDecodeBodyWithUTF8ContentType() {
        // given
        httpResponse.withBody("avro i\u015far\u0259si: \u20ac", StandardCharsets.UTF_8);
        httpResponse.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_8).toString()));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(new String(fullHttpResponse.content().array(), StandardCharsets.UTF_8), Is.is("avro i\u015far\u0259si: \u20ac"));
    }

    @Test
    public void shouldDecodeBodyWithUTF16ContentType() {
        // given
        httpResponse.withBody("?????", StandardCharsets.UTF_16);
        httpResponse.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16).toString()));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(new String(fullHttpResponse.content().array(), StandardCharsets.UTF_16), Is.is("?????"));
    }

    @Test
    public void shouldEncodeStringBodyWithCharset() {
        // given
        httpResponse.withBody("?????", StandardCharsets.UTF_16);
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpRequest = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(new String(fullHttpRequest.content().array(), StandardCharsets.UTF_16), Is.is("?????"));
        MatcherAssert.assertThat(fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE), Is.is(MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16).toString()));
    }

    @Test
    public void shouldEncodeUTF8JsonBodyWithContentType() {
        // given
        httpResponse.withBody("{ \"some_field\": \"\u6211\u8bf4\u4e2d\u56fd\u8bdd\" }").withHeader(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.JSON_UTF_8.toString());
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(new String(fullHttpResponse.content().array(), StandardCharsets.UTF_8), Is.is("{ \"some_field\": \"\u6211\u8bf4\u4e2d\u56fd\u8bdd\" }"));
        MatcherAssert.assertThat(fullHttpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE), Is.is(MediaType.JSON_UTF_8.toString()));
    }

    @Test
    public void shouldEncodeUTF8JsonBodyWithCharset() {
        // given
        httpResponse.withBody(JsonBody.json("{ \"some_field\": \"\u6211\u8bf4\u4e2d\u56fd\u8bdd\" }", StandardCharsets.UTF_8));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(new String(fullHttpResponse.content().array(), StandardCharsets.UTF_8), Is.is("{ \"some_field\": \"\u6211\u8bf4\u4e2d\u56fd\u8bdd\" }"));
        MatcherAssert.assertThat(fullHttpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE), Is.is(MediaType.JSON_UTF_8.toString()));
    }

    @Test
    public void shouldPreferStringBodyCharacterSet() {
        // given
        httpResponse.withBody("avro i\u015far\u0259si: \u20ac", StandardCharsets.UTF_16);
        httpResponse.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.create("text", "plain").withCharset(StandardCharsets.US_ASCII).toString()));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(new String(fullHttpResponse.content().array(), StandardCharsets.UTF_16), Is.is("avro i\u015far\u0259si: \u20ac"));
    }

    @Test
    public void shouldReturnNoDefaultContentTypeWhenNoBodySpecified() {
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), empty());
    }

    @Test
    public void shouldReturnContentTypeForStringBody() {
        // given - a request & response
        httpResponse.withBody("somebody");
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), empty());
    }

    @Test
    public void shouldReturnContentTypeForStringBodyWithContentType() {
        // given - a request & response
        httpResponse.withBody(StringBody.exact("somebody", MediaType.PLAIN_TEXT_UTF_8));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), containsInAnyOrder("text/plain; charset=utf-8"));
    }

    @Test
    public void shouldReturnContentTypeForStringBodyWithCharset() {
        // given - a request & response
        httpResponse.withBody(StringBody.exact("somebody", StandardCharsets.UTF_16));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), containsInAnyOrder("text/plain; charset=utf-16"));
    }

    @Test
    public void shouldReturnContentTypeForJsonBody() {
        // given
        httpResponse.withBody(JsonBody.json("somebody"));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), containsInAnyOrder("application/json"));
    }

    @Test
    public void shouldReturnContentTypeForJsonBodyWithContentType() {
        // given - a request & response
        httpResponse.withBody(JsonBody.json("somebody", MediaType.JSON_UTF_8));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), containsInAnyOrder("application/json; charset=utf-8"));
    }

    @Test
    public void shouldReturnContentTypeForBinaryBody() {
        // given
        httpResponse.withBody(BinaryBody.binary("somebody".getBytes(StandardCharsets.UTF_8)));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), empty());
    }

    @Test
    public void shouldReturnContentTypeForBinaryBodyWithContentType() {
        // given - a request & response
        httpResponse.withBody(BinaryBody.binary("somebody".getBytes(StandardCharsets.UTF_8), MediaType.QUICKTIME));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), containsInAnyOrder(MediaType.QUICKTIME.toString()));
    }

    @Test
    public void shouldReturnContentTypeForXmlBody() {
        // given
        httpResponse.withBody(XmlBody.xml("somebody"));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), containsInAnyOrder("application/xml"));
    }

    @Test
    public void shouldReturnContentTypeForXmlBodyWithContentType() {
        // given - a request & response
        httpResponse.withBody(XmlBody.xml("somebody", MediaType.XML_UTF_8));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), containsInAnyOrder("text/xml; charset=utf-8"));
    }

    @Test
    public void shouldReturnContentTypeForParameterBody() {
        // given
        httpResponse.withBody(ParameterBody.params(Parameter.param("key", "value")));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), containsInAnyOrder("application/x-www-form-urlencoded"));
    }

    @Test
    public void shouldReturnNoContentTypeForBodyWithNoAssociatedContentType() {
        // given
        httpResponse.withBody(XmlBody.xml("some_value", ((MediaType) (null))));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), empty());
    }

    @Test
    public void shouldNotSetDefaultContentTypeWhenContentTypeExplicitlySpecified() {
        // given
        httpResponse.withBody(JsonBody.json("somebody")).withHeaders(new Header("Content-Type", "some/value"));
        // when
        new MockServerResponseEncoder().encode(null, httpResponse, output);
        // then
        FullHttpResponse fullHttpResponse = ((FullHttpResponse) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), containsInAnyOrder("some/value"));
    }
}

