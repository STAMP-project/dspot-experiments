package org.mockserver.matchers;


import com.google.common.net.MediaType;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpVersion;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.codec.MockServerRequestDecoder;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.mappers.ContentTypeMapper;


/**
 *
 *
 * @author jamesdbloom
 */
public class MatcherBuilderTest {
    private HttpRequest httpRequest = new HttpRequest().withMethod("GET").withPath("some_path").withQueryStringParameter(new Parameter("queryStringParameterName", "queryStringParameterValue")).withBody(new StringBody("some_body")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "value"));

    private MockServerLogger mockLogFormatter;

    @Test
    public void shouldCreateMatcherThatMatchesAllFields() {
        // when
        HttpRequestMatcher httpRequestMapper = new MatcherBuilder(mockLogFormatter).transformsToMatcher(httpRequest);
        // then
        Assert.assertTrue(httpRequestMapper.matches(null, httpRequest));
    }

    @Test
    public void shouldSupportSpecialCharactersWhenCharsetSpecified() {
        String bodyTestString = "UTF_8 characters: Bj\u00f6rk";
        // given
        MockServerRequestDecoder mockServerRequestDecoder = new MockServerRequestDecoder(new MockServerLogger(), false);
        FullHttpRequest fullHttpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/uri", wrappedBuffer(bodyTestString.getBytes(ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET)));
        fullHttpRequest.headers().add(HttpHeaderNames.CONTENT_TYPE, MediaType.PLAIN_TEXT_UTF_8.withCharset(ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET).toString());
        // when
        HttpRequest httpRequest = mockServerRequestDecoder.decode(fullHttpRequest);
        // and
        HttpRequestMatcher httpRequestMapper = new MatcherBuilder(new MockServerLogger()).transformsToMatcher(new HttpRequest().withMethod(HttpMethod.GET.name()).withPath("/uri").withBody(new StringBody(bodyTestString)));
        // then
        MatcherAssert.assertThat(httpRequest.getBody().getCharset(null), CoreMatchers.is(ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET));
        Assert.assertTrue(httpRequestMapper.matches(null, httpRequest));
    }

    @Test
    public void shouldSupportSpecialCharactersWithDefaultCharset() {
        String bodyTestString = "UTF_8 characters: Bj\u00f6rk";
        // given
        MockServerRequestDecoder mockServerRequestDecoder = new MockServerRequestDecoder(new MockServerLogger(), false);
        FullHttpRequest fullHttpRequest = new io.netty.handler.codec.http.DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/uri", wrappedBuffer(bodyTestString.getBytes(ContentTypeMapper.DEFAULT_HTTP_CHARACTER_SET)));
        // when
        HttpRequest httpRequest = mockServerRequestDecoder.decode(fullHttpRequest);
        // and
        HttpRequestMatcher httpRequestMapper = new MatcherBuilder(new MockServerLogger()).transformsToMatcher(new HttpRequest().withMethod(HttpMethod.GET.name()).withPath("/uri").withBody(new StringBody(bodyTestString)));
        // then - request used default charset, then body charset is NULL
        Assert.assertNull(httpRequest.getBody().getCharset(null));
        Assert.assertTrue(httpRequestMapper.matches(null, httpRequest));
    }

    @Test
    public void shouldCreateMatcherThatIgnoresMethod() {
        // when
        HttpRequestMatcher httpRequestMapper = new MatcherBuilder(mockLogFormatter).transformsToMatcher(new HttpRequest().withMethod("").withPath("some_path").withQueryStringParameter(new Parameter("queryStringParameterName", "queryStringParameterValue")).withBody(new StringBody("some_body")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "value")));
        // then
        Assert.assertTrue(httpRequestMapper.matches(null, httpRequest));
    }

    @Test
    public void shouldCreateMatcherThatIgnoresPath() {
        // when
        HttpRequestMatcher httpRequestMapper = new MatcherBuilder(mockLogFormatter).transformsToMatcher(new HttpRequest().withMethod("GET").withPath("").withQueryStringParameter(new Parameter("queryStringParameterName", "queryStringParameterValue")).withBody(new StringBody("some_body")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "value")));
        // then
        Assert.assertTrue(httpRequestMapper.matches(null, httpRequest));
    }

    @Test
    public void shouldCreateMatcherThatIgnoresQueryString() {
        // when
        HttpRequestMatcher httpRequestMapper = new MatcherBuilder(mockLogFormatter).transformsToMatcher(new HttpRequest().withMethod("GET").withPath("some_path").withQueryStringParameters().withBody(new StringBody("some_body")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "value")));
        // then
        Assert.assertTrue(httpRequestMapper.matches(null, httpRequest));
    }

    @Test
    public void shouldCreateMatcherThatIgnoresBodyParameters() {
        // when
        HttpRequestMatcher httpRequestMapper = new MatcherBuilder(mockLogFormatter).transformsToMatcher(new HttpRequest().withMethod("GET").withPath("some_path").withQueryStringParameter(new Parameter("queryStringParameterName", "queryStringParameterValue")).withBody(new ParameterBody()).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "value")));
        // then
        Assert.assertTrue(httpRequestMapper.matches(null, httpRequest));
    }

    @Test
    public void shouldCreateMatcherThatIgnoresBody() {
        // when
        HttpRequestMatcher httpRequestMapper = new MatcherBuilder(mockLogFormatter).transformsToMatcher(new HttpRequest().withMethod("GET").withPath("some_path").withQueryStringParameter(new Parameter("queryStringParameterName", "queryStringParameterValue")).withBody(new StringBody("")).withHeaders(new Header("name", "value")).withCookies(new Cookie("name", "value")));
        // then
        Assert.assertTrue(httpRequestMapper.matches(null, httpRequest));
    }

    @Test
    public void shouldCreateMatcherThatIgnoresHeaders() {
        // when
        HttpRequestMatcher httpRequestMapper = new MatcherBuilder(mockLogFormatter).transformsToMatcher(new HttpRequest().withMethod("GET").withPath("some_path").withQueryStringParameter(new Parameter("queryStringParameterName", "queryStringParameterValue")).withBody(new StringBody("some_body")).withHeaders().withCookies(new Cookie("name", "value")));
        // then
        Assert.assertTrue(httpRequestMapper.matches(null, httpRequest));
    }

    @Test
    public void shouldCreateMatcherThatIgnoresCookies() {
        // when
        HttpRequestMatcher httpRequestMapper = new MatcherBuilder(mockLogFormatter).transformsToMatcher(new HttpRequest().withMethod("GET").withPath("some_path").withQueryStringParameter(new Parameter("queryStringParameterName", "queryStringParameterValue")).withBody(new StringBody("some_body")).withHeaders(new Header("name", "value")).withCookies());
        // then
        Assert.assertTrue(httpRequestMapper.matches(null, httpRequest));
    }
}

