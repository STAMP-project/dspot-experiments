/**
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.web.util;


import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;


/**
 * Unit tests for {@link UriComponentsBuilder}.
 *
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Phillip Webb
 * @author Oliver Gierke
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author David Eckel
 */
public class UriComponentsBuilderTests {
    @Test
    public void plain() throws URISyntaxException {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        UriComponents result = builder.scheme("http").host("example.com").path("foo").queryParam("bar").fragment("baz").build();
        Assert.assertEquals("http", result.getScheme());
        Assert.assertEquals("example.com", result.getHost());
        Assert.assertEquals("foo", result.getPath());
        Assert.assertEquals("bar", result.getQuery());
        Assert.assertEquals("baz", result.getFragment());
        URI expected = new URI("http://example.com/foo?bar#baz");
        Assert.assertEquals("Invalid result URI", expected, result.toUri());
    }

    @Test
    public void multipleFromSameBuilder() throws URISyntaxException {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance().scheme("http").host("example.com").pathSegment("foo");
        UriComponents result1 = builder.build();
        builder = builder.pathSegment("foo2").queryParam("bar").fragment("baz");
        UriComponents result2 = builder.build();
        Assert.assertEquals("http", result1.getScheme());
        Assert.assertEquals("example.com", result1.getHost());
        Assert.assertEquals("/foo", result1.getPath());
        URI expected = new URI("http://example.com/foo");
        Assert.assertEquals("Invalid result URI", expected, result1.toUri());
        Assert.assertEquals("http", result2.getScheme());
        Assert.assertEquals("example.com", result2.getHost());
        Assert.assertEquals("/foo/foo2", result2.getPath());
        Assert.assertEquals("bar", result2.getQuery());
        Assert.assertEquals("baz", result2.getFragment());
        expected = new URI("http://example.com/foo/foo2?bar#baz");
        Assert.assertEquals("Invalid result URI", expected, result2.toUri());
    }

    @Test
    public void fromPath() throws URISyntaxException {
        UriComponents result = UriComponentsBuilder.fromPath("foo").queryParam("bar").fragment("baz").build();
        Assert.assertEquals("foo", result.getPath());
        Assert.assertEquals("bar", result.getQuery());
        Assert.assertEquals("baz", result.getFragment());
        Assert.assertEquals("Invalid result URI String", "foo?bar#baz", result.toUriString());
        URI expected = new URI("foo?bar#baz");
        Assert.assertEquals("Invalid result URI", expected, result.toUri());
        result = UriComponentsBuilder.fromPath("/foo").build();
        Assert.assertEquals("/foo", result.getPath());
        expected = new URI("/foo");
        Assert.assertEquals("Invalid result URI", expected, result.toUri());
    }

    @Test
    public void fromHierarchicalUri() throws URISyntaxException {
        URI uri = new URI("http://example.com/foo?bar#baz");
        UriComponents result = UriComponentsBuilder.fromUri(uri).build();
        Assert.assertEquals("http", result.getScheme());
        Assert.assertEquals("example.com", result.getHost());
        Assert.assertEquals("/foo", result.getPath());
        Assert.assertEquals("bar", result.getQuery());
        Assert.assertEquals("baz", result.getFragment());
        Assert.assertEquals("Invalid result URI", uri, result.toUri());
    }

    @Test
    public void fromOpaqueUri() throws URISyntaxException {
        URI uri = new URI("mailto:foo@bar.com#baz");
        UriComponents result = UriComponentsBuilder.fromUri(uri).build();
        Assert.assertEquals("mailto", result.getScheme());
        Assert.assertEquals("foo@bar.com", result.getSchemeSpecificPart());
        Assert.assertEquals("baz", result.getFragment());
        Assert.assertEquals("Invalid result URI", uri, result.toUri());
    }

    // SPR-9317
    @Test
    public void fromUriEncodedQuery() throws URISyntaxException {
        URI uri = new URI("http://www.example.org/?param=aGVsbG9Xb3JsZA%3D%3D");
        String fromUri = UriComponentsBuilder.fromUri(uri).build().getQueryParams().get("param").get(0);
        String fromUriString = UriComponentsBuilder.fromUriString(uri.toString()).build().getQueryParams().get("param").get(0);
        Assert.assertEquals(fromUri, fromUriString);
    }

    @Test
    public void fromUriString() {
        UriComponents result = UriComponentsBuilder.fromUriString("http://www.ietf.org/rfc/rfc3986.txt").build();
        Assert.assertEquals("http", result.getScheme());
        Assert.assertNull(result.getUserInfo());
        Assert.assertEquals("www.ietf.org", result.getHost());
        Assert.assertEquals((-1), result.getPort());
        Assert.assertEquals("/rfc/rfc3986.txt", result.getPath());
        Assert.assertEquals(Arrays.asList("rfc", "rfc3986.txt"), result.getPathSegments());
        Assert.assertNull(result.getQuery());
        Assert.assertNull(result.getFragment());
        String url = "http://arjen:foobar@java.sun.com:80" + "/javase/6/docs/api/java/util/BitSet.html?foo=bar#and(java.util.BitSet)";
        result = UriComponentsBuilder.fromUriString(url).build();
        Assert.assertEquals("http", result.getScheme());
        Assert.assertEquals("arjen:foobar", result.getUserInfo());
        Assert.assertEquals("java.sun.com", result.getHost());
        Assert.assertEquals(80, result.getPort());
        Assert.assertEquals("/javase/6/docs/api/java/util/BitSet.html", result.getPath());
        Assert.assertEquals("foo=bar", result.getQuery());
        MultiValueMap<String, String> expectedQueryParams = new org.springframework.util.LinkedMultiValueMap(1);
        expectedQueryParams.add("foo", "bar");
        Assert.assertEquals(expectedQueryParams, result.getQueryParams());
        Assert.assertEquals("and(java.util.BitSet)", result.getFragment());
        result = UriComponentsBuilder.fromUriString("mailto:java-net@java.sun.com#baz").build();
        Assert.assertEquals("mailto", result.getScheme());
        Assert.assertNull(result.getUserInfo());
        Assert.assertNull(result.getHost());
        Assert.assertEquals((-1), result.getPort());
        Assert.assertEquals("java-net@java.sun.com", result.getSchemeSpecificPart());
        Assert.assertNull(result.getPath());
        Assert.assertNull(result.getQuery());
        Assert.assertEquals("baz", result.getFragment());
        result = UriComponentsBuilder.fromUriString("docs/guide/collections/designfaq.html#28").build();
        Assert.assertNull(result.getScheme());
        Assert.assertNull(result.getUserInfo());
        Assert.assertNull(result.getHost());
        Assert.assertEquals((-1), result.getPort());
        Assert.assertEquals("docs/guide/collections/designfaq.html", result.getPath());
        Assert.assertNull(result.getQuery());
        Assert.assertEquals("28", result.getFragment());
    }

    // SPR-9832
    @Test
    public void fromUriStringQueryParamWithReservedCharInValue() {
        String uri = "http://www.google.com/ig/calculator?q=1USD=?EUR";
        UriComponents result = UriComponentsBuilder.fromUriString(uri).build();
        Assert.assertEquals("q=1USD=?EUR", result.getQuery());
        Assert.assertEquals("1USD=?EUR", result.getQueryParams().getFirst("q"));
    }

    // SPR-14828
    @Test
    public void fromUriStringQueryParamEncodedAndContainingPlus() {
        String httpUrl = "http://localhost:8080/test/print?value=%EA%B0%80+%EB%82%98";
        URI uri = UriComponentsBuilder.fromHttpUrl(httpUrl).build(true).toUri();
        Assert.assertEquals(httpUrl, uri.toString());
    }

    // SPR-10779
    @Test
    public void fromHttpUrlStringCaseInsesitiveScheme() {
        Assert.assertEquals("http", UriComponentsBuilder.fromHttpUrl("HTTP://www.google.com").build().getScheme());
        Assert.assertEquals("https", UriComponentsBuilder.fromHttpUrl("HTTPS://www.google.com").build().getScheme());
    }

    // SPR-10539
    @Test(expected = IllegalArgumentException.class)
    public void fromHttpUrlStringInvalidIPv6Host() {
        UriComponentsBuilder.fromHttpUrl("http://[1abc:2abc:3abc::5ABC:6abc:8080/resource").build().encode();
    }

    // SPR-10539
    @Test
    public void fromUriStringIPv6Host() {
        UriComponents result = UriComponentsBuilder.fromUriString("http://[1abc:2abc:3abc::5ABC:6abc]:8080/resource").build().encode();
        Assert.assertEquals("[1abc:2abc:3abc::5ABC:6abc]", result.getHost());
        UriComponents resultWithScopeId = UriComponentsBuilder.fromUriString("http://[1abc:2abc:3abc::5ABC:6abc%eth0]:8080/resource").build().encode();
        Assert.assertEquals("[1abc:2abc:3abc::5ABC:6abc%25eth0]", resultWithScopeId.getHost());
        UriComponents resultIPv4compatible = UriComponentsBuilder.fromUriString("http://[::192.168.1.1]:8080/resource").build().encode();
        Assert.assertEquals("[::192.168.1.1]", resultIPv4compatible.getHost());
    }

    // SPR-11970
    @Test
    public void fromUriStringNoPathWithReservedCharInQuery() {
        UriComponents result = UriComponentsBuilder.fromUriString("http://example.com?foo=bar@baz").build();
        Assert.assertTrue(StringUtils.isEmpty(result.getUserInfo()));
        Assert.assertEquals("example.com", result.getHost());
        Assert.assertTrue(result.getQueryParams().containsKey("foo"));
        Assert.assertEquals("bar@baz", result.getQueryParams().getFirst("foo"));
    }

    @Test
    public void fromHttpRequest() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort((-1));
        request.setRequestURI("/path");
        request.setQueryString("a=1");
        UriComponents result = UriComponentsBuilder.fromHttpRequest(new ServletServerHttpRequest(request)).build();
        Assert.assertEquals("http", result.getScheme());
        Assert.assertEquals("localhost", result.getHost());
        Assert.assertEquals((-1), result.getPort());
        Assert.assertEquals("/path", result.getPath());
        Assert.assertEquals("a=1", result.getQuery());
    }

    // SPR-12771
    @Test
    public void fromHttpRequestResetsPortBeforeSettingIt() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-Proto", "https");
        request.addHeader("X-Forwarded-Host", "84.198.58.199");
        request.addHeader("X-Forwarded-Port", 443);
        request.setScheme("http");
        request.setServerName("example.com");
        request.setServerPort(80);
        request.setRequestURI("/rest/mobile/users/1");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("84.198.58.199", result.getHost());
        Assert.assertEquals((-1), result.getPort());
        Assert.assertEquals("/rest/mobile/users/1", result.getPath());
    }

    // SPR-14761
    @Test
    public void fromHttpRequestWithForwardedIPv4Host() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort((-1));
        request.setRequestURI("/mvc-showcase");
        request.addHeader("Forwarded", "host=192.168.0.1");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("http://192.168.0.1/mvc-showcase", result.toString());
    }

    // SPR-14761
    @Test
    public void fromHttpRequestWithForwardedIPv6() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort((-1));
        request.setRequestURI("/mvc-showcase");
        request.addHeader("Forwarded", "host=[1abc:2abc:3abc::5ABC:6abc]");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("http://[1abc:2abc:3abc::5ABC:6abc]/mvc-showcase", result.toString());
    }

    // SPR-14761
    @Test
    public void fromHttpRequestWithForwardedIPv6Host() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort((-1));
        request.setRequestURI("/mvc-showcase");
        request.addHeader("X-Forwarded-Host", "[1abc:2abc:3abc::5ABC:6abc]");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("http://[1abc:2abc:3abc::5ABC:6abc]/mvc-showcase", result.toString());
    }

    // SPR-14761
    @Test
    public void fromHttpRequestWithForwardedIPv6HostAndPort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort((-1));
        request.setRequestURI("/mvc-showcase");
        request.addHeader("X-Forwarded-Host", "[1abc:2abc:3abc::5ABC:6abc]:8080");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("http://[1abc:2abc:3abc::5ABC:6abc]:8080/mvc-showcase", result.toString());
    }

    @Test
    public void fromHttpRequestWithForwardedHost() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort((-1));
        request.setRequestURI("/mvc-showcase");
        request.addHeader("X-Forwarded-Host", "anotherHost");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("http://anotherHost/mvc-showcase", result.toString());
    }

    // SPR-10701
    @Test
    public void fromHttpRequestWithForwardedHostIncludingPort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort((-1));
        request.setRequestURI("/mvc-showcase");
        request.addHeader("X-Forwarded-Host", "webtest.foo.bar.com:443");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("webtest.foo.bar.com", result.getHost());
        Assert.assertEquals(443, result.getPort());
    }

    // SPR-11140
    @Test
    public void fromHttpRequestWithForwardedHostMultiValuedHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort((-1));
        request.addHeader("X-Forwarded-Host", "a.example.org, b.example.org, c.example.org");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("a.example.org", result.getHost());
        Assert.assertEquals((-1), result.getPort());
    }

    // SPR-11855
    @Test
    public void fromHttpRequestWithForwardedHostAndPort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.addHeader("X-Forwarded-Host", "foobarhost");
        request.addHeader("X-Forwarded-Port", "9090");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("foobarhost", result.getHost());
        Assert.assertEquals(9090, result.getPort());
    }

    // SPR-11872
    @Test
    public void fromHttpRequestWithForwardedHostWithDefaultPort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(10080);
        request.addHeader("X-Forwarded-Host", "example.org");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("example.org", result.getHost());
        Assert.assertEquals((-1), result.getPort());
    }

    // SPR-16262
    @Test
    public void fromHttpRequestWithForwardedProtoWithDefaultPort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("example.org");
        request.setServerPort(10080);
        request.addHeader("X-Forwarded-Proto", "https");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("example.org", result.getHost());
        Assert.assertEquals((-1), result.getPort());
    }

    // SPR-16863
    @Test
    public void fromHttpRequestWithForwardedSsl() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("example.org");
        request.setServerPort(10080);
        request.addHeader("X-Forwarded-Ssl", "on");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("example.org", result.getHost());
        Assert.assertEquals((-1), result.getPort());
    }

    @Test
    public void fromHttpRequestWithForwardedHostWithForwardedScheme() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(10080);
        request.addHeader("X-Forwarded-Host", "example.org");
        request.addHeader("X-Forwarded-Proto", "https");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("example.org", result.getHost());
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals((-1), result.getPort());
    }

    // SPR-12771
    @Test
    public void fromHttpRequestWithForwardedProtoAndDefaultPort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);
        request.setRequestURI("/mvc-showcase");
        request.addHeader("X-Forwarded-Proto", "https");
        request.addHeader("X-Forwarded-Host", "84.198.58.199");
        request.addHeader("X-Forwarded-Port", "443");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https://84.198.58.199/mvc-showcase", result.toString());
    }

    // SPR-12813
    @Test
    public void fromHttpRequestWithForwardedPortMultiValueHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(9090);
        request.setRequestURI("/mvc-showcase");
        request.addHeader("X-Forwarded-Host", "a.example.org");
        request.addHeader("X-Forwarded-Port", "80,52022");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("http://a.example.org/mvc-showcase", result.toString());
    }

    // SPR-12816
    @Test
    public void fromHttpRequestWithForwardedProtoMultiValueHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setRequestURI("/mvc-showcase");
        request.addHeader("X-Forwarded-Host", "a.example.org");
        request.addHeader("X-Forwarded-Port", "443");
        request.addHeader("X-Forwarded-Proto", "https,https");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https://a.example.org/mvc-showcase", result.toString());
    }

    // SPR-12742
    @Test
    public void fromHttpRequestWithTrailingSlash() {
        UriComponents before = UriComponentsBuilder.fromPath("/foo/").build();
        UriComponents after = UriComponentsBuilder.newInstance().uriComponents(before).build();
        Assert.assertEquals("/foo/", after.getPath());
    }

    // gh-19890
    @Test
    public void fromHttpRequestWithEmptyScheme() {
        HttpRequest request = new HttpRequest() {
            @Override
            public String getMethodValue() {
                return "GET";
            }

            @Override
            public URI getURI() {
                return UriComponentsBuilder.fromUriString("/").build().toUri();
            }

            @Override
            public org.springframework.http.HttpHeaders getHeaders() {
                return new org.springframework.http.HttpHeaders();
            }
        };
        UriComponents result = UriComponentsBuilder.fromHttpRequest(request).build();
        Assert.assertEquals("/", result.toString());
    }

    @Test
    public void path() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/foo/bar");
        UriComponents result = builder.build();
        Assert.assertEquals("/foo/bar", result.getPath());
        Assert.assertEquals(Arrays.asList("foo", "bar"), result.getPathSegments());
    }

    @Test
    public void pathSegments() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        UriComponents result = builder.pathSegment("foo").pathSegment("bar").build();
        Assert.assertEquals("/foo/bar", result.getPath());
        Assert.assertEquals(Arrays.asList("foo", "bar"), result.getPathSegments());
    }

    @Test
    public void pathThenPath() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/foo/bar").path("ba/z");
        UriComponents result = builder.build().encode();
        Assert.assertEquals("/foo/barba/z", result.getPath());
        Assert.assertEquals(Arrays.asList("foo", "barba", "z"), result.getPathSegments());
    }

    @Test
    public void pathThenPathSegments() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/foo/bar").pathSegment("ba/z");
        UriComponents result = builder.build().encode();
        Assert.assertEquals("/foo/bar/ba%2Fz", result.getPath());
        Assert.assertEquals(Arrays.asList("foo", "bar", "ba%2Fz"), result.getPathSegments());
    }

    @Test
    public void pathSegmentsThenPathSegments() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance().pathSegment("foo").pathSegment("bar");
        UriComponents result = builder.build();
        Assert.assertEquals("/foo/bar", result.getPath());
        Assert.assertEquals(Arrays.asList("foo", "bar"), result.getPathSegments());
    }

    @Test
    public void pathSegmentsThenPath() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance().pathSegment("foo").path("/");
        UriComponents result = builder.build();
        Assert.assertEquals("/foo/", result.getPath());
        Assert.assertEquals(Collections.singletonList("foo"), result.getPathSegments());
    }

    @Test
    public void pathSegmentsSomeEmpty() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance().pathSegment("", "foo", "", "bar");
        UriComponents result = builder.build();
        Assert.assertEquals("/foo/bar", result.getPath());
        Assert.assertEquals(Arrays.asList("foo", "bar"), result.getPathSegments());
    }

    // SPR-12398
    @Test
    public void pathWithDuplicateSlashes() {
        UriComponents uriComponents = UriComponentsBuilder.fromPath("/foo/////////bar").build();
        Assert.assertEquals("/foo/bar", uriComponents.getPath());
    }

    @Test
    public void replacePath() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://www.ietf.org/rfc/rfc2396.txt");
        builder.replacePath("/rfc/rfc3986.txt");
        UriComponents result = builder.build();
        Assert.assertEquals("http://www.ietf.org/rfc/rfc3986.txt", result.toUriString());
        builder = UriComponentsBuilder.fromUriString("http://www.ietf.org/rfc/rfc2396.txt");
        builder.replacePath(null);
        result = builder.build();
        Assert.assertEquals("http://www.ietf.org", result.toUriString());
    }

    @Test
    public void replaceQuery() {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://example.com/foo?foo=bar&baz=qux");
        builder.replaceQuery("baz=42");
        UriComponents result = builder.build();
        Assert.assertEquals("http://example.com/foo?baz=42", result.toUriString());
        builder = UriComponentsBuilder.fromUriString("http://example.com/foo?foo=bar&baz=qux");
        builder.replaceQuery(null);
        result = builder.build();
        Assert.assertEquals("http://example.com/foo", result.toUriString());
    }

    @Test
    public void queryParams() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        UriComponents result = builder.queryParam("baz", "qux", 42).build();
        Assert.assertEquals("baz=qux&baz=42", result.getQuery());
        MultiValueMap<String, String> expectedQueryParams = new org.springframework.util.LinkedMultiValueMap(2);
        expectedQueryParams.add("baz", "qux");
        expectedQueryParams.add("baz", "42");
        Assert.assertEquals(expectedQueryParams, result.getQueryParams());
    }

    @Test
    public void emptyQueryParam() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance();
        UriComponents result = builder.queryParam("baz").build();
        Assert.assertEquals("baz", result.getQuery());
        MultiValueMap<String, String> expectedQueryParams = new org.springframework.util.LinkedMultiValueMap(2);
        expectedQueryParams.add("baz", null);
        Assert.assertEquals(expectedQueryParams, result.getQueryParams());
    }

    @Test
    public void replaceQueryParam() {
        UriComponentsBuilder builder = UriComponentsBuilder.newInstance().queryParam("baz", "qux", 42);
        builder.replaceQueryParam("baz", "xuq", 24);
        UriComponents result = builder.build();
        Assert.assertEquals("baz=xuq&baz=24", result.getQuery());
        builder = UriComponentsBuilder.newInstance().queryParam("baz", "qux", 42);
        builder.replaceQueryParam("baz");
        result = builder.build();
        Assert.assertNull("Query param should have been deleted", result.getQuery());
    }

    @Test
    public void buildAndExpandHierarchical() {
        UriComponents result = UriComponentsBuilder.fromPath("/{foo}").buildAndExpand("fooValue");
        Assert.assertEquals("/fooValue", result.toUriString());
        Map<String, String> values = new HashMap<>();
        values.put("foo", "fooValue");
        values.put("bar", "barValue");
        result = UriComponentsBuilder.fromPath("/{foo}/{bar}").buildAndExpand(values);
        Assert.assertEquals("/fooValue/barValue", result.toUriString());
    }

    @Test
    public void buildAndExpandOpaque() {
        UriComponents result = UriComponentsBuilder.fromUriString("mailto:{user}@{domain}").buildAndExpand("foo", "example.com");
        Assert.assertEquals("mailto:foo@example.com", result.toUriString());
        Map<String, String> values = new HashMap<>();
        values.put("user", "foo");
        values.put("domain", "example.com");
        UriComponentsBuilder.fromUriString("mailto:{user}@{domain}").buildAndExpand(values);
        Assert.assertEquals("mailto:foo@example.com", result.toUriString());
    }

    @Test
    public void queryParamWithValueWithEquals() {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString("http://example.com/foo?bar=baz").build();
        Assert.assertThat(uriComponents.toUriString(), equalTo("http://example.com/foo?bar=baz"));
        Assert.assertThat(uriComponents.getQueryParams().get("bar").get(0), equalTo("baz"));
    }

    @Test
    public void queryParamWithoutValueWithEquals() {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString("http://example.com/foo?bar=").build();
        Assert.assertThat(uriComponents.toUriString(), equalTo("http://example.com/foo?bar="));
        Assert.assertThat(uriComponents.getQueryParams().get("bar").get(0), equalTo(""));
    }

    @Test
    public void queryParamWithoutValueWithoutEquals() {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString("http://example.com/foo?bar").build();
        Assert.assertThat(uriComponents.toUriString(), equalTo("http://example.com/foo?bar"));
        // TODO [SPR-13537] Change equalTo(null) to equalTo("").
        Assert.assertThat(uriComponents.getQueryParams().get("bar").get(0), equalTo(null));
    }

    @Test
    public void relativeUrls() {
        String baseUrl = "http://example.com";
        Assert.assertThat(UriComponentsBuilder.fromUriString((baseUrl + "/foo/../bar")).build().toString(), equalTo((baseUrl + "/foo/../bar")));
        Assert.assertThat(UriComponentsBuilder.fromUriString((baseUrl + "/foo/../bar")).build().toUriString(), equalTo((baseUrl + "/foo/../bar")));
        Assert.assertThat(UriComponentsBuilder.fromUriString((baseUrl + "/foo/../bar")).build().toUri().getPath(), equalTo("/foo/../bar"));
        Assert.assertThat(UriComponentsBuilder.fromUriString("../../").build().toString(), equalTo("../../"));
        Assert.assertThat(UriComponentsBuilder.fromUriString("../../").build().toUriString(), equalTo("../../"));
        Assert.assertThat(UriComponentsBuilder.fromUriString("../../").build().toUri().getPath(), equalTo("../../"));
        Assert.assertThat(UriComponentsBuilder.fromUriString(baseUrl).path("foo/../bar").build().toString(), equalTo((baseUrl + "/foo/../bar")));
        Assert.assertThat(UriComponentsBuilder.fromUriString(baseUrl).path("foo/../bar").build().toUriString(), equalTo((baseUrl + "/foo/../bar")));
        Assert.assertThat(UriComponentsBuilder.fromUriString(baseUrl).path("foo/../bar").build().toUri().getPath(), equalTo("/foo/../bar"));
    }

    @Test
    public void emptySegments() {
        String baseUrl = "http://example.com/abc/";
        Assert.assertThat(UriComponentsBuilder.fromUriString(baseUrl).path("/x/y/z").build().toString(), equalTo("http://example.com/abc/x/y/z"));
        Assert.assertThat(UriComponentsBuilder.fromUriString(baseUrl).pathSegment("x", "y", "z").build().toString(), equalTo("http://example.com/abc/x/y/z"));
        Assert.assertThat(UriComponentsBuilder.fromUriString(baseUrl).path("/x/").path("/y/z").build().toString(), equalTo("http://example.com/abc/x/y/z"));
        Assert.assertThat(UriComponentsBuilder.fromUriString(baseUrl).pathSegment("x").path("y").build().toString(), equalTo("http://example.com/abc/x/y"));
    }

    @Test
    public void parsesEmptyFragment() {
        UriComponents components = UriComponentsBuilder.fromUriString("/example#").build();
        Assert.assertThat(components.getFragment(), is(nullValue()));
        Assert.assertThat(components.toString(), equalTo("/example"));
    }

    // SPR-13257
    @Test
    public void parsesEmptyUri() {
        UriComponents components = UriComponentsBuilder.fromUriString("").build();
        Assert.assertThat(components.toString(), equalTo(""));
    }

    @Test
    public void testClone() {
        UriComponentsBuilder builder1 = UriComponentsBuilder.newInstance();
        builder1.scheme("http").host("e1.com").path("/p1").pathSegment("ps1").queryParam("q1").fragment("f1").encode();
        UriComponentsBuilder builder2 = ((UriComponentsBuilder) (builder1.clone()));
        builder2.scheme("https").host("e2.com").path("p2").pathSegment("{ps2}").queryParam("q2").fragment("f2");
        UriComponents result1 = builder1.build();
        Assert.assertEquals("http", result1.getScheme());
        Assert.assertEquals("e1.com", result1.getHost());
        Assert.assertEquals("/p1/ps1", result1.getPath());
        Assert.assertEquals("q1", result1.getQuery());
        Assert.assertEquals("f1", result1.getFragment());
        UriComponents result2 = builder2.buildAndExpand("ps2;a");
        Assert.assertEquals("https", result2.getScheme());
        Assert.assertEquals("e2.com", result2.getHost());
        Assert.assertEquals("/p1/ps1/p2/ps2%3Ba", result2.getPath());
        Assert.assertEquals("q1&q2", result2.getQuery());
        Assert.assertEquals("f2", result2.getFragment());
    }

    // SPR-11856
    @Test
    public void fromHttpRequestForwardedHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Forwarded", "proto=https; host=84.198.58.199");
        request.setScheme("http");
        request.setServerName("example.com");
        request.setRequestURI("/rest/mobile/users/1");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("84.198.58.199", result.getHost());
        Assert.assertEquals("/rest/mobile/users/1", result.getPath());
    }

    @Test
    public void fromHttpRequestForwardedHeaderQuoted() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Forwarded", "proto=\"https\"; host=\"84.198.58.199\"");
        request.setScheme("http");
        request.setServerName("example.com");
        request.setRequestURI("/rest/mobile/users/1");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("84.198.58.199", result.getHost());
        Assert.assertEquals("/rest/mobile/users/1", result.getPath());
    }

    @Test
    public void fromHttpRequestMultipleForwardedHeader() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Forwarded", "host=84.198.58.199;proto=https");
        request.addHeader("Forwarded", "proto=ftp; host=1.2.3.4");
        request.setScheme("http");
        request.setServerName("example.com");
        request.setRequestURI("/rest/mobile/users/1");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("84.198.58.199", result.getHost());
        Assert.assertEquals("/rest/mobile/users/1", result.getPath());
    }

    @Test
    public void fromHttpRequestMultipleForwardedHeaderComma() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Forwarded", "host=84.198.58.199 ;proto=https, proto=ftp; host=1.2.3.4");
        request.setScheme("http");
        request.setServerName("example.com");
        request.setRequestURI("/rest/mobile/users/1");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("84.198.58.199", result.getHost());
        Assert.assertEquals("/rest/mobile/users/1", result.getPath());
    }

    @Test
    public void fromHttpRequestForwardedHeaderWithHostPortAndWithoutServerPort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Forwarded", "proto=https; host=84.198.58.199:9090");
        request.setScheme("http");
        request.setServerName("example.com");
        request.setRequestURI("/rest/mobile/users/1");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("84.198.58.199", result.getHost());
        Assert.assertEquals("/rest/mobile/users/1", result.getPath());
        Assert.assertEquals(9090, result.getPort());
        Assert.assertEquals("https://84.198.58.199:9090/rest/mobile/users/1", result.toUriString());
    }

    @Test
    public void fromHttpRequestForwardedHeaderWithHostPortAndServerPort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Forwarded", "proto=https; host=84.198.58.199:9090");
        request.setScheme("http");
        request.setServerPort(8080);
        request.setServerName("example.com");
        request.setRequestURI("/rest/mobile/users/1");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("84.198.58.199", result.getHost());
        Assert.assertEquals("/rest/mobile/users/1", result.getPath());
        Assert.assertEquals(9090, result.getPort());
        Assert.assertEquals("https://84.198.58.199:9090/rest/mobile/users/1", result.toUriString());
    }

    @Test
    public void fromHttpRequestForwardedHeaderWithoutHostPortAndWithServerPort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Forwarded", "proto=https; host=84.198.58.199");
        request.setScheme("http");
        request.setServerPort(8080);
        request.setServerName("example.com");
        request.setRequestURI("/rest/mobile/users/1");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("84.198.58.199", result.getHost());
        Assert.assertEquals("/rest/mobile/users/1", result.getPath());
        Assert.assertEquals((-1), result.getPort());
        Assert.assertEquals("https://84.198.58.199/rest/mobile/users/1", result.toUriString());
    }

    // SPR-16262
    @Test
    public void fromHttpRequestForwardedHeaderWithProtoAndServerPort() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Forwarded", "proto=https");
        request.setScheme("http");
        request.setServerPort(8080);
        request.setServerName("example.com");
        request.setRequestURI("/rest/mobile/users/1");
        HttpRequest httpRequest = new ServletServerHttpRequest(request);
        UriComponents result = UriComponentsBuilder.fromHttpRequest(httpRequest).build();
        Assert.assertEquals("https", result.getScheme());
        Assert.assertEquals("example.com", result.getHost());
        Assert.assertEquals("/rest/mobile/users/1", result.getPath());
        Assert.assertEquals((-1), result.getPort());
        Assert.assertEquals("https://example.com/rest/mobile/users/1", result.toUriString());
    }

    // SPR-16364
    @Test
    public void uriComponentsNotEqualAfterNormalization() {
        UriComponents uri1 = UriComponentsBuilder.fromUriString("http://test.com").build().normalize();
        UriComponents uri2 = UriComponentsBuilder.fromUriString("http://test.com/").build();
        Assert.assertTrue(uri1.getPathSegments().isEmpty());
        Assert.assertTrue(uri2.getPathSegments().isEmpty());
        Assert.assertNotEquals(uri1, uri2);
    }

    // SPR-17256
    @Test
    public void uriComponentsWithMergedQueryParams() {
        String uri = UriComponentsBuilder.fromUriString("http://localhost:8081").uriComponents(UriComponentsBuilder.fromUriString("/{path}?sort={sort}").build()).queryParam("sort", "another_value").build().toString();
        Assert.assertEquals("http://localhost:8081/{path}?sort={sort}&sort=another_value", uri);
    }

    // SPR-17630
    @Test
    public void toUriStringWithCurlyBraces() {
        Assert.assertEquals("/path?q=%7Basa%7Dasa", UriComponentsBuilder.fromUriString("/path?q={asa}asa").toUriString());
    }
}

