/**
 * Copyright 2002-2018 the original author or authors.
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
package org.springframework.web.servlet.support;


import javax.servlet.http.HttpServletRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.util.UriComponents;


/**
 * Unit tests for
 * {@link org.springframework.web.servlet.support.ServletUriComponentsBuilder}.
 *
 * @author Rossen Stoyanchev
 */
public class ServletUriComponentsBuilderTests {
    private MockHttpServletRequest request;

    @Test
    public void fromRequest() {
        this.request.setRequestURI("/mvc-showcase/data/param");
        this.request.setQueryString("foo=123");
        String result = ServletUriComponentsBuilder.fromRequest(this.request).build().toUriString();
        Assert.assertEquals("http://localhost/mvc-showcase/data/param?foo=123", result);
    }

    @Test
    public void fromRequestEncodedPath() {
        this.request.setRequestURI("/mvc-showcase/data/foo%20bar");
        String result = ServletUriComponentsBuilder.fromRequest(this.request).build().toUriString();
        Assert.assertEquals("http://localhost/mvc-showcase/data/foo%20bar", result);
    }

    @Test
    public void fromRequestAtypicalHttpPort() {
        this.request.setServerPort(8080);
        String result = ServletUriComponentsBuilder.fromRequest(this.request).build().toUriString();
        Assert.assertEquals("http://localhost:8080/mvc-showcase", result);
    }

    @Test
    public void fromRequestAtypicalHttpsPort() {
        this.request.setScheme("https");
        this.request.setServerPort(9043);
        String result = ServletUriComponentsBuilder.fromRequest(this.request).build().toUriString();
        Assert.assertEquals("https://localhost:9043/mvc-showcase", result);
    }

    // Some X-Forwarded-* tests in addition to the ones in UriComponentsBuilderTests
    @Test
    public void fromRequestWithForwardedHostAndPort() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(80);
        request.setRequestURI("/mvc-showcase");
        request.addHeader("X-Forwarded-Proto", "https");
        request.addHeader("X-Forwarded-Host", "84.198.58.199");
        request.addHeader("X-Forwarded-Port", "443");
        HttpServletRequest requestToUse = adaptFromForwardedHeaders(request);
        UriComponents result = ServletUriComponentsBuilder.fromRequest(requestToUse).build();
        Assert.assertEquals("https://84.198.58.199/mvc-showcase", result.toString());
    }

    @Test
    public void fromRequestUri() {
        this.request.setRequestURI("/mvc-showcase/data/param");
        this.request.setQueryString("foo=123");
        String result = ServletUriComponentsBuilder.fromRequestUri(this.request).build().toUriString();
        Assert.assertEquals("http://localhost/mvc-showcase/data/param", result);
    }

    // SPR-16650
    @Test
    public void fromRequestWithForwardedPrefix() throws Exception {
        this.request.addHeader("X-Forwarded-Prefix", "/prefix");
        this.request.setContextPath("/mvc-showcase");
        this.request.setRequestURI("/mvc-showcase/bar");
        HttpServletRequest requestToUse = adaptFromForwardedHeaders(this.request);
        UriComponents result = ServletUriComponentsBuilder.fromRequest(requestToUse).build();
        Assert.assertEquals("http://localhost/prefix/bar", result.toUriString());
    }

    // SPR-16650
    @Test
    public void fromRequestWithForwardedPrefixTrailingSlash() throws Exception {
        this.request.addHeader("X-Forwarded-Prefix", "/foo/");
        this.request.setContextPath("/spring-mvc-showcase");
        this.request.setRequestURI("/spring-mvc-showcase/bar");
        HttpServletRequest requestToUse = adaptFromForwardedHeaders(this.request);
        UriComponents result = ServletUriComponentsBuilder.fromRequest(requestToUse).build();
        Assert.assertEquals("http://localhost/foo/bar", result.toUriString());
    }

    // SPR-16650
    @Test
    public void fromRequestWithForwardedPrefixRoot() throws Exception {
        this.request.addHeader("X-Forwarded-Prefix", "/");
        this.request.setContextPath("/mvc-showcase");
        this.request.setRequestURI("/mvc-showcase/bar");
        HttpServletRequest requestToUse = adaptFromForwardedHeaders(this.request);
        UriComponents result = ServletUriComponentsBuilder.fromRequest(requestToUse).build();
        Assert.assertEquals("http://localhost/bar", result.toUriString());
    }

    @Test
    public void fromContextPath() {
        this.request.setRequestURI("/mvc-showcase/data/param");
        this.request.setQueryString("foo=123");
        String result = ServletUriComponentsBuilder.fromContextPath(this.request).build().toUriString();
        Assert.assertEquals("http://localhost/mvc-showcase", result);
    }

    // SPR-16650
    @Test
    public void fromContextPathWithForwardedPrefix() throws Exception {
        this.request.addHeader("X-Forwarded-Prefix", "/prefix");
        this.request.setContextPath("/mvc-showcase");
        this.request.setRequestURI("/mvc-showcase/simple");
        HttpServletRequest requestToUse = adaptFromForwardedHeaders(this.request);
        String result = ServletUriComponentsBuilder.fromContextPath(requestToUse).build().toUriString();
        Assert.assertEquals("http://localhost/prefix", result);
    }

    @Test
    public void fromServletMapping() {
        this.request.setRequestURI("/mvc-showcase/app/simple");
        this.request.setServletPath("/app");
        this.request.setQueryString("foo=123");
        String result = ServletUriComponentsBuilder.fromServletMapping(this.request).build().toUriString();
        Assert.assertEquals("http://localhost/mvc-showcase/app", result);
    }

    // SPR-16650
    @Test
    public void fromServletMappingWithForwardedPrefix() throws Exception {
        this.request.addHeader("X-Forwarded-Prefix", "/prefix");
        this.request.setContextPath("/mvc-showcase");
        this.request.setServletPath("/app");
        this.request.setRequestURI("/mvc-showcase/app/simple");
        HttpServletRequest requestToUse = adaptFromForwardedHeaders(this.request);
        String result = ServletUriComponentsBuilder.fromServletMapping(requestToUse).build().toUriString();
        Assert.assertEquals("http://localhost/prefix/app", result);
    }

    @Test
    public void fromCurrentRequest() {
        this.request.setRequestURI("/mvc-showcase/data/param");
        this.request.setQueryString("foo=123");
        RequestContextHolder.setRequestAttributes(new org.springframework.web.context.request.ServletRequestAttributes(this.request));
        try {
            String result = ServletUriComponentsBuilder.fromCurrentRequest().build().toUriString();
            Assert.assertEquals("http://localhost/mvc-showcase/data/param?foo=123", result);
        } finally {
            RequestContextHolder.resetRequestAttributes();
        }
    }

    // SPR-10272
    @Test
    public void pathExtension() {
        this.request.setRequestURI("/rest/books/6.json");
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromRequestUri(this.request);
        String extension = builder.removePathExtension();
        String result = builder.path("/pages/1.{ext}").buildAndExpand(extension).toUriString();
        Assert.assertEquals("http://localhost/rest/books/6/pages/1.json", result);
    }

    @Test
    public void pathExtensionNone() {
        this.request.setRequestURI("/rest/books/6");
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromRequestUri(this.request);
        Assert.assertNull(builder.removePathExtension());
    }
}

