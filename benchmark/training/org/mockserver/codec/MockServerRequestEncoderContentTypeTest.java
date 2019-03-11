package org.mockserver.codec;


import ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET;
import com.google.common.net.MediaType;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockserver.mappers.ContentTypeMapper;
import org.mockserver.model.BinaryBody;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.JsonBody;
import org.mockserver.model.JsonSchemaBody;
import org.mockserver.model.Parameter;
import org.mockserver.model.ParameterBody;
import org.mockserver.model.RegexBody;
import org.mockserver.model.StringBody;
import org.mockserver.model.XmlBody;


/**
 *
 *
 * @author jamesdbloom
 */
public class MockServerRequestEncoderContentTypeTest {
    private List<Object> output;

    private HttpRequest httpRequest;

    @Test
    public void shouldDecodeBodyWithContentTypeAndNoCharset() {
        // given
        httpRequest.withBody("A normal string with ASCII characters");
        httpRequest.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.create("text", "plain").toString()));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(DEFAULT_HTTP_CHARACTER_SET), Is.is("A normal string with ASCII characters"));
    }

    @Test
    public void shouldDecodeBodyWithNoContentType() {
        // given
        httpRequest.withBody("A normal string with ASCII characters");
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(DEFAULT_HTTP_CHARACTER_SET), Is.is("A normal string with ASCII characters"));
    }

    @Test
    public void shouldTransmitUnencodableCharacters() {
        // given
        httpRequest.withBody("Euro sign: \u20ac", DEFAULT_HTTP_CHARACTER_SET);
        httpRequest.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.create("text", "plain").toString()));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(DEFAULT_HTTP_CHARACTER_SET), Is.is(new String("Euro sign: \u20ac".getBytes(DEFAULT_HTTP_CHARACTER_SET), ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET)));
    }

    @Test
    public void shouldUseDefaultCharsetIfCharsetNotSupported() {
        // given
        httpRequest.withBody("A normal string with ASCII characters");
        httpRequest.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), "text/plain; charset=invalid-charset"));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(DEFAULT_HTTP_CHARACTER_SET), Is.is("A normal string with ASCII characters"));
    }

    @Test
    public void shouldDecodeBodyWithUTF8ContentType() {
        // given
        httpRequest.withBody("avro i\u015far\u0259si: \u20ac", StandardCharsets.UTF_8);
        httpRequest.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_8).toString()));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(StandardCharsets.UTF_8), Is.is("avro i\u015far\u0259si: \u20ac"));
    }

    @Test
    public void shouldDecodeBodyWithUTF16ContentType() {
        // given
        httpRequest.withBody("?????", StandardCharsets.UTF_16);
        httpRequest.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16).toString()));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(StandardCharsets.UTF_16), Is.is("?????"));
    }

    @Test
    public void shouldEncodeStringBodyWithCharset() {
        // given
        httpRequest.withBody("?????", StandardCharsets.UTF_16);
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(StandardCharsets.UTF_16), Is.is("?????"));
        MatcherAssert.assertThat(fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE), Is.is(MediaType.create("text", "plain").withCharset(StandardCharsets.UTF_16).toString()));
    }

    @Test
    public void shouldEncodeUTF8JsonBodyWithContentType() {
        // given
        httpRequest.withBody("{ \"some_field\": \"\u6211\u8bf4\u4e2d\u56fd\u8bdd\" }").withHeader(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.JSON_UTF_8.toString());
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(new String(fullHttpResponse.content().array(), StandardCharsets.UTF_8), Is.is("{ \"some_field\": \"\u6211\u8bf4\u4e2d\u56fd\u8bdd\" }"));
        MatcherAssert.assertThat(fullHttpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE), Is.is(MediaType.JSON_UTF_8.toString()));
    }

    @Test
    public void shouldEncodeUTF8JsonBodyWithCharset() {
        // given
        httpRequest.withBody(JsonBody.json("{ \"some_field\": \"\u6211\u8bf4\u4e2d\u56fd\u8bdd\" }", StandardCharsets.UTF_8));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(new String(fullHttpResponse.content().array(), StandardCharsets.UTF_8), Is.is("{ \"some_field\": \"\u6211\u8bf4\u4e2d\u56fd\u8bdd\" }"));
        MatcherAssert.assertThat(fullHttpResponse.headers().get(HttpHeaderNames.CONTENT_TYPE), Is.is(MediaType.JSON_UTF_8.toString()));
    }

    @Test
    public void shouldPreferStringBodyCharacterSet() {
        // given
        httpRequest.withBody("avro i\u015far\u0259si: \u20ac", StandardCharsets.UTF_16);
        httpRequest.withHeader(new Header(HttpHeaderNames.CONTENT_TYPE.toString(), MediaType.create("text", "plain").withCharset(StandardCharsets.US_ASCII).toString()));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpRequest = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpRequest.content().toString(StandardCharsets.UTF_16), Is.is("avro i\u015far\u0259si: \u20ac"));
    }

    @Test
    public void shouldReturnNoDefaultContentTypeWhenNoBodySpecified() {
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), empty());
    }

    @Test
    public void shouldReturnContentTypeForStringBody() {
        // given - a request & response
        httpRequest.withBody("somebody");
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), empty());
    }

    @Test
    public void shouldReturnContentTypeForStringBodyWithContentType() {
        // given - a request & response
        httpRequest.withBody(StringBody.exact("somebody", MediaType.PLAIN_TEXT_UTF_8));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), Matchers.containsInAnyOrder("text/plain; charset=utf-8"));
    }

    @Test
    public void shouldReturnContentTypeForStringBodyWithCharset() {
        // given - a request & response
        httpRequest.withBody(StringBody.exact("somebody", StandardCharsets.UTF_16));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), Matchers.containsInAnyOrder("text/plain; charset=utf-16"));
    }

    @Test
    public void shouldReturnContentTypeForJsonBody() {
        // given
        httpRequest.withBody(JsonBody.json("somebody"));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), Matchers.containsInAnyOrder("application/json"));
    }

    @Test
    public void shouldReturnContentTypeForJsonBodyWithContentType() {
        // given - a request & response
        httpRequest.withBody(JsonBody.json("somebody", MediaType.JSON_UTF_8));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), Matchers.containsInAnyOrder("application/json; charset=utf-8"));
    }

    @Test
    public void shouldReturnContentTypeForBinaryBody() {
        // given
        httpRequest.withBody(BinaryBody.binary("somebody".getBytes(StandardCharsets.UTF_8)));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), empty());
    }

    @Test
    public void shouldReturnContentTypeForBinaryBodyWithContentType() {
        // given - a request & response
        httpRequest.withBody(BinaryBody.binary("somebody".getBytes(StandardCharsets.UTF_8), MediaType.QUICKTIME));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), Matchers.containsInAnyOrder(MediaType.QUICKTIME.toString()));
    }

    @Test
    public void shouldReturnContentTypeForXmlBody() {
        // given
        httpRequest.withBody(XmlBody.xml("somebody"));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), Matchers.containsInAnyOrder("application/xml"));
    }

    @Test
    public void shouldReturnContentTypeForXmlBodyWithContentType() {
        // given - a request & response
        httpRequest.withBody(XmlBody.xml("somebody", MediaType.XML_UTF_8));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), Matchers.containsInAnyOrder("text/xml; charset=utf-8"));
    }

    @Test
    public void shouldNotReturnContentTypeForJsonSchemaBody() {
        // given
        httpRequest.withBody(JsonSchemaBody.jsonSchema("somebody"));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), empty());
    }

    @Test
    public void shouldReturnContentTypeForParameterBody() {
        // given
        httpRequest.withBody(ParameterBody.params(Parameter.param("key", "value")));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), Matchers.containsInAnyOrder("application/x-www-form-urlencoded"));
    }

    @Test
    public void shouldReturnNoContentTypeForBodyWithNoAssociatedContentType() {
        // given
        httpRequest.withBody(RegexBody.regex("some_value"));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), empty());
    }

    @Test
    public void shouldNotSetDefaultContentTypeWhenContentTypeExplicitlySpecified() {
        // given
        httpRequest.withBody(JsonBody.json("somebody")).withHeaders(new Header("Content-Type", "some/value"));
        // when
        new MockServerRequestEncoder().encode(null, httpRequest, output);
        // then
        FullHttpRequest fullHttpResponse = ((FullHttpRequest) (output.get(0)));
        MatcherAssert.assertThat(fullHttpResponse.headers().getAll("Content-Type"), Matchers.containsInAnyOrder("some/value"));
    }
}

