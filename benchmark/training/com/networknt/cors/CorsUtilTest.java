/**
 * Copyright (C) 2015 Red Hat, inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.networknt.cors;


import HttpString.EMPTY;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 * Testing CORS utility class.
 *
 * @author <a href="mailto:ehugonne@redhat.com">Emmanuel Hugonnet</a> (c) 2015 Red Hat, inc.
 */
public class CorsUtilTest {
    public CorsUtilTest() {
    }

    /**
     * Test of isCoreRequest method, of class CorsUtil.
     */
    @Test
    public void testIsCoreRequest() {
        HeaderMap headers = new HeaderMap();
        Assert.assertThat(CorsUtil.isCoreRequest(headers), CoreMatchers.is(false));
        headers = new HeaderMap();
        headers.add(CorsHeaders.ORIGIN, "");
        Assert.assertThat(CorsUtil.isCoreRequest(headers), CoreMatchers.is(true));
        headers = new HeaderMap();
        headers.add(CorsHeaders.ACCESS_CONTROL_REQUEST_HEADERS, "");
        Assert.assertThat(CorsUtil.isCoreRequest(headers), CoreMatchers.is(true));
        headers = new HeaderMap();
        headers.add(CorsHeaders.ACCESS_CONTROL_REQUEST_METHOD, "");
        Assert.assertThat(CorsUtil.isCoreRequest(headers), CoreMatchers.is(true));
    }

    /**
     * Test of matchOrigin method, of class CorsUtil.
     */
    @Test
    public void testMatchOrigin() throws Exception {
        HeaderMap headerMap = new HeaderMap();
        headerMap.add(HOST, "localhost:80");
        headerMap.add(CorsHeaders.ORIGIN, "http://localhost");
        HttpServerExchange exchange = new HttpServerExchange(null, headerMap, new HeaderMap(), 10);
        exchange.setRequestScheme("http");
        exchange.setRequestMethod(EMPTY);
        Collection<String> allowedOrigins = null;
        Assert.assertThat(CorsUtil.matchOrigin(exchange, allowedOrigins), CoreMatchers.is("http://localhost"));
        allowedOrigins = Collections.singletonList("http://www.example.com:9990");
        // Default origin
        Assert.assertThat(CorsUtil.matchOrigin(exchange, allowedOrigins), CoreMatchers.is("http://localhost"));
        headerMap.clear();
        headerMap.add(HOST, "localhost:80");
        headerMap.add(CorsHeaders.ORIGIN, "http://www.example.com:9990");
        Assert.assertThat(CorsUtil.matchOrigin(exchange, allowedOrigins), CoreMatchers.is("http://www.example.com:9990"));
        headerMap.clear();
        headerMap.add(HOST, "localhost:80");
        headerMap.add(CorsHeaders.ORIGIN, "http://www.example.com");
        Assert.assertThat(CorsUtil.matchOrigin(exchange, allowedOrigins), CoreMatchers.is(CoreMatchers.nullValue()));
        headerMap.addAll(CorsHeaders.ORIGIN, Arrays.asList("http://localhost:8080", "http://www.example.com:9990", "http://localhost"));
        allowedOrigins = Arrays.asList("http://localhost", "http://www.example.com:9990");
        Assert.assertThat(CorsUtil.matchOrigin(exchange, allowedOrigins), CoreMatchers.is("http://localhost"));
    }

    /**
     * Test of sanitizeDefaultPort method, of class CorsUtil.
     */
    @Test
    public void testSanitizeDefaultPort() {
        String url = "http://127.0.0.1:80";
        Assert.assertThat(CorsUtil.sanitizeDefaultPort(url), CoreMatchers.is("http://127.0.0.1"));
        url = "http://127.0.0.1";
        Assert.assertThat(CorsUtil.sanitizeDefaultPort(url), CoreMatchers.is("http://127.0.0.1"));
        url = "http://127.0.0.1:443";
        Assert.assertThat(CorsUtil.sanitizeDefaultPort(url), CoreMatchers.is("http://127.0.0.1:443"));
        url = "http://127.0.0.1:8080";
        Assert.assertThat(CorsUtil.sanitizeDefaultPort(url), CoreMatchers.is("http://127.0.0.1:8080"));
        url = "https://127.0.0.1:80";
        Assert.assertThat(CorsUtil.sanitizeDefaultPort(url), CoreMatchers.is("https://127.0.0.1:80"));
        url = "https://127.0.0.1:443";
        Assert.assertThat(CorsUtil.sanitizeDefaultPort(url), CoreMatchers.is("https://127.0.0.1"));
        url = "https://127.0.0.1";
        Assert.assertThat(CorsUtil.sanitizeDefaultPort(url), CoreMatchers.is("https://127.0.0.1"));
        url = "http://[::FFFF:129.144.52.38]:8080";
        Assert.assertThat(CorsUtil.sanitizeDefaultPort(url), CoreMatchers.is("http://[::FFFF:129.144.52.38]:8080"));
        url = "http://[::FFFF:129.144.52.38]:80";
        Assert.assertThat(CorsUtil.sanitizeDefaultPort(url), CoreMatchers.is("http://[::FFFF:129.144.52.38]"));
    }

    /**
     * Test of defaultOrigin method, of class CorsUtil.
     */
    @Test
    public void testDefaultOrigin() {
        HeaderMap headerMap = new HeaderMap();
        headerMap.add(HOST, "localhost:80");
        HttpServerExchange exchange = new HttpServerExchange(null, headerMap, new HeaderMap(), 10);
        exchange.setRequestScheme("http");
        Assert.assertThat(CorsUtil.defaultOrigin(exchange), CoreMatchers.is("http://localhost"));
        headerMap.clear();
        headerMap.add(HOST, "www.example.com:8080");
        Assert.assertThat(CorsUtil.defaultOrigin(exchange), CoreMatchers.is("http://www.example.com:8080"));
        headerMap.clear();
        headerMap.add(HOST, "www.example.com:443");
        exchange.setRequestScheme("https");
        Assert.assertThat(CorsUtil.defaultOrigin(exchange), CoreMatchers.is("https://www.example.com"));
        headerMap.clear();
        exchange.setRequestScheme("http");
        headerMap.add(HOST, "[::1]:80");
        Assert.assertThat(CorsUtil.defaultOrigin(exchange), CoreMatchers.is("http://[::1]"));
    }
}

