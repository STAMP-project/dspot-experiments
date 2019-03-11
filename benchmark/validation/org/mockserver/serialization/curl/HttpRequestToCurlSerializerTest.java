package org.mockserver.serialization.curl;


import io.netty.handler.codec.http.HttpHeaderNames;
import java.net.InetSocketAddress;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.Cookie;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.Parameter;


public class HttpRequestToCurlSerializerTest {
    @Test
    public void shouldGenerateCurlForSimpleRequest() {
        // given
        HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();
        // when
        String curl = httpRequestToCurlSerializer.toCurl(HttpRequest.request(), new InetSocketAddress("localhost", 80));
        // then
        Assert.assertThat(curl, CoreMatchers.is("curl -v 'http://localhost:80/'"));
    }

    @Test
    public void shouldGenerateCurlForRequestWithPOST() {
        // given
        HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();
        // when
        String curl = httpRequestToCurlSerializer.toCurl(HttpRequest.request().withMethod("POST"), new InetSocketAddress("localhost", 80));
        // then
        Assert.assertThat(curl, CoreMatchers.is("curl -v 'http://localhost:80/' -X POST"));
    }

    @Test
    public void shouldGenerateCurlForRequestWithGETAndSocketAddress() {
        // given
        HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();
        // when
        String curl = httpRequestToCurlSerializer.toCurl(HttpRequest.request().withMethod("GET"), new InetSocketAddress("localhost", 80));
        // then
        Assert.assertThat(curl, CoreMatchers.is("curl -v 'http://localhost:80/'"));
    }

    @Test
    public void shouldGenerateCurlForRequestWithGETAndNullSocketAddress() {
        // given
        HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();
        // when
        String curl = httpRequestToCurlSerializer.toCurl(HttpRequest.request().withHeader(HttpHeaderNames.HOST.toString(), ("localhost:" + 80)).withMethod("GET"), null);
        // then
        Assert.assertThat(curl, CoreMatchers.is("curl -v 'http://localhost:80/' -H 'host: localhost:80'"));
    }

    @Test
    public void shouldGenerateCurlForRequestWithGETAndNullSocketAddressAndNoHostHeader() {
        // given
        HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();
        // when
        String curl = httpRequestToCurlSerializer.toCurl(HttpRequest.request().withMethod("GET"), null);
        // then
        Assert.assertThat(curl, CoreMatchers.is("no host header or remote address specified"));
    }

    @Test
    public void shouldGenerateCurlForRequestWithParameter() {
        // given
        HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();
        // when
        String curl = httpRequestToCurlSerializer.toCurl(HttpRequest.request().withQueryStringParameters(Parameter.param("parameterName1", "parameterValue1_1", "parameterValue1_2"), Parameter.param("another parameter with spaces", "a value with single \'quotes\', double \"quotes\" and spaces")), new InetSocketAddress("localhost", 80));
        // then
        Assert.assertThat(curl, CoreMatchers.is("curl -v 'http://localhost:80/?parameterName1=parameterValue1_1&parameterName1=parameterValue1_2&another%20parameter%20with%20spaces=a%20value%20with%20single%20%27quotes%27%2C%20double%20%22quotes%22%20and%20spaces'"));
    }

    @Test
    public void shouldGenerateCurlForRequestWithHeaders() {
        // given
        HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();
        // when
        String curl = httpRequestToCurlSerializer.toCurl(HttpRequest.request().withHeaders(new Header("headerName1", "headerValue1"), new Header("headerName2", "headerValue2_1", "headerValue2_2")), new InetSocketAddress("localhost", 80));
        // then
        Assert.assertThat(curl, CoreMatchers.is("curl -v 'http://localhost:80/' -H 'headerName1: headerValue1' -H 'headerName2: headerValue2_1' -H 'headerName2: headerValue2_2'"));
    }

    @Test
    public void shouldGenerateCurlForRequestWithCookies() {
        // given
        HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();
        // when
        String curl = httpRequestToCurlSerializer.toCurl(HttpRequest.request().withCookies(new Cookie("cookieName1", "cookieValue1"), new Cookie("cookieName2", "cookieValue2")), new InetSocketAddress("localhost", 80));
        // then
        Assert.assertThat(curl, CoreMatchers.is(("curl -v 'http://localhost:80/' " + "-H 'cookie: cookieName1=cookieValue1; cookieName2=cookieValue2'")));
    }

    @Test
    public void shouldGenerateCurlForRequestWithPOSTParameterHeadersAndCookies() {
        // given
        HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();
        // when
        String curl = httpRequestToCurlSerializer.toCurl(HttpRequest.request().withPath("/somePath").withMethod("POST").withQueryStringParameters(Parameter.param("parameterName1", "parameterValue1_1", "parameterValue1_2"), Parameter.param("another parameter with spaces", "a value with single \'quotes\', double \"quotes\" and spaces")).withHeaders(new Header("headerName1", "headerValue1"), new Header("headerName2", "headerValue2_1", "headerValue2_2")).withCookies(new Cookie("cookieName1", "cookieValue1"), new Cookie("cookieName2", "cookieValue2")), new InetSocketAddress("localhost", 80));
        // then
        Assert.assertThat(curl, CoreMatchers.is(("curl -v " + (((((("'http://localhost:80/somePath" + "?parameterName1=parameterValue1_1&parameterName1=parameterValue1_2&another%20parameter%20with%20spaces=a%20value%20with%20single%20%27quotes%27%2C%20double%20%22quotes%22%20and%20spaces'") + " -X POST") + " -H 'headerName1: headerValue1'") + " -H 'headerName2: headerValue2_1'") + " -H 'headerName2: headerValue2_2'") + " -H 'cookie: cookieName1=cookieValue1; cookieName2=cookieValue2'"))));
    }

    @Test
    public void shouldHandleNullWhenGeneratingCurl() {
        // given
        HttpRequestToCurlSerializer httpRequestToCurlSerializer = new HttpRequestToCurlSerializer();
        // when
        String curl = httpRequestToCurlSerializer.toCurl(null, null);
        // then
        Assert.assertThat(curl, CoreMatchers.is("null HttpRequest"));
    }
}

