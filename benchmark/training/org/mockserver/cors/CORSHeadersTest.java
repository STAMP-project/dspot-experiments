package org.mockserver.cors;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;


/**
 *
 *
 * @author jamesdbloom
 */
public class CORSHeadersTest {
    @Test
    public void shouldDetectPreflightRequest() {
        Assert.assertThat(CORSHeaders.isPreflightRequest(HttpRequest.request().withMethod("OPTIONS").withHeader("origin", "some_origin_header").withHeader("access-control-request-method", "true")), CoreMatchers.is(true));
        Assert.assertThat(CORSHeaders.isPreflightRequest(HttpRequest.request().withMethod("GET").withHeader("origin", "some_origin_header").withHeader("access-control-request-method", "true")), CoreMatchers.is(false));
        Assert.assertThat(CORSHeaders.isPreflightRequest(HttpRequest.request().withMethod("OPTIONS").withHeader("not_origin", "some_origin_header").withHeader("access-control-request-method", "true")), CoreMatchers.is(false));
        Assert.assertThat(CORSHeaders.isPreflightRequest(HttpRequest.request().withMethod("OPTIONS").withHeader("origin", "some_origin_header").withHeader("not_access-control-request-method", "true")), CoreMatchers.is(false));
    }

    @Test
    public void shouldAddCORSHeader() {
        // given
        HttpRequest request = HttpRequest.request();
        HttpResponse response = HttpResponse.response();
        // when
        new CORSHeaders().addCORSHeaders(request, response);
        // then
        Assert.assertThat(response.getFirstHeader("access-control-allow-origin"), CoreMatchers.is("*"));
        Assert.assertThat(response.getFirstHeader("access-control-allow-methods"), CoreMatchers.is("CONNECT, DELETE, GET, HEAD, OPTIONS, POST, PUT, PATCH, TRACE"));
        Assert.assertThat(response.getFirstHeader("access-control-allow-headers"), CoreMatchers.is("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization"));
        Assert.assertThat(response.getFirstHeader("access-control-expose-headers"), CoreMatchers.is("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization"));
        Assert.assertThat(response.getFirstHeader("access-control-max-age"), CoreMatchers.is("300"));
        Assert.assertThat(response.getFirstHeader("x-cors"), CoreMatchers.is("MockServer CORS support enabled by default, to disable ConfigurationProperties.enableCORSForAPI(false) or -Dmockserver.enableCORSForAPI=false"));
    }

    @Test
    public void shouldAddCORSHeaderForNullOrigin() {
        // given
        HttpRequest request = HttpRequest.request().withHeader("origin", "null");
        HttpResponse response = HttpResponse.response();
        // when
        new CORSHeaders().addCORSHeaders(request, response);
        // then
        Assert.assertThat(response.getFirstHeader("access-control-allow-origin"), CoreMatchers.is("null"));
        Assert.assertThat(response.getFirstHeader("access-control-allow-methods"), CoreMatchers.is("CONNECT, DELETE, GET, HEAD, OPTIONS, POST, PUT, PATCH, TRACE"));
        Assert.assertThat(response.getFirstHeader("access-control-allow-headers"), CoreMatchers.is("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization"));
        Assert.assertThat(response.getFirstHeader("access-control-expose-headers"), CoreMatchers.is("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization"));
        Assert.assertThat(response.getFirstHeader("access-control-max-age"), CoreMatchers.is("300"));
        Assert.assertThat(response.getFirstHeader("x-cors"), CoreMatchers.is("MockServer CORS support enabled by default, to disable ConfigurationProperties.enableCORSForAPI(false) or -Dmockserver.enableCORSForAPI=false"));
    }

    @Test
    public void shouldAddCORSHeaderForAllowCredentials() {
        // given
        HttpRequest request = HttpRequest.request().withHeader("origin", "some_origin_value").withHeader("access-control-allow-credentials", "true");
        HttpResponse response = HttpResponse.response();
        // when
        new CORSHeaders().addCORSHeaders(request, response);
        // then
        Assert.assertThat(response.getFirstHeader("access-control-allow-origin"), CoreMatchers.is("some_origin_value"));
        Assert.assertThat(response.getFirstHeader("access-control-allow-methods"), CoreMatchers.is("CONNECT, DELETE, GET, HEAD, OPTIONS, POST, PUT, PATCH, TRACE"));
        Assert.assertThat(response.getFirstHeader("access-control-allow-headers"), CoreMatchers.is("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization"));
        Assert.assertThat(response.getFirstHeader("access-control-expose-headers"), CoreMatchers.is("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization"));
        Assert.assertThat(response.getFirstHeader("access-control-max-age"), CoreMatchers.is("300"));
        Assert.assertThat(response.getFirstHeader("x-cors"), CoreMatchers.is("MockServer CORS support enabled by default, to disable ConfigurationProperties.enableCORSForAPI(false) or -Dmockserver.enableCORSForAPI=false"));
    }

    @Test
    public void shouldAddCORSHeaderForAllowCredentialsWithoutOrigin() {
        // given
        HttpRequest request = HttpRequest.request().withHeader("access-control-allow-credentials", "true");
        HttpResponse response = HttpResponse.response();
        // when
        new CORSHeaders().addCORSHeaders(request, response);
        // then
        Assert.assertThat(response.getFirstHeader("access-control-allow-origin"), CoreMatchers.is("*"));
        Assert.assertThat(response.getFirstHeader("access-control-allow-methods"), CoreMatchers.is("CONNECT, DELETE, GET, HEAD, OPTIONS, POST, PUT, PATCH, TRACE"));
        Assert.assertThat(response.getFirstHeader("access-control-allow-headers"), CoreMatchers.is("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization"));
        Assert.assertThat(response.getFirstHeader("access-control-expose-headers"), CoreMatchers.is("Allow, Content-Encoding, Content-Length, Content-Type, ETag, Expires, Last-Modified, Location, Server, Vary, Authorization"));
        Assert.assertThat(response.getFirstHeader("access-control-max-age"), CoreMatchers.is("300"));
        Assert.assertThat(response.getFirstHeader("x-cors"), CoreMatchers.is("MockServer CORS support enabled by default, to disable ConfigurationProperties.enableCORSForAPI(false) or -Dmockserver.enableCORSForAPI=false"));
    }
}

