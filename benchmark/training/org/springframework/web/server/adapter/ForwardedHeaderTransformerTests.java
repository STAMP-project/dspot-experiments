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
package org.springframework.web.server.adapter;


import java.net.URI;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;

import static org.springframework.mock.http.server.reactive.test.MockServerHttpRequest.method;


/**
 * Unit tests for {@link ForwardedHeaderTransformer}.
 *
 * @author Rossen Stoyanchev
 */
public class ForwardedHeaderTransformerTests {
    private static final String BASE_URL = "http://example.com/path";

    private final ForwardedHeaderTransformer requestMutator = new ForwardedHeaderTransformer();

    @Test
    public void removeOnly() {
        this.requestMutator.setRemoveOnly(true);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Forwarded", "for=192.0.2.60;proto=http;by=203.0.113.43");
        headers.add("X-Forwarded-Host", "example.com");
        headers.add("X-Forwarded-Port", "8080");
        headers.add("X-Forwarded-Proto", "http");
        headers.add("X-Forwarded-Prefix", "prefix");
        headers.add("X-Forwarded-Ssl", "on");
        ServerHttpRequest request = this.requestMutator.apply(getRequest(headers));
        assertForwardedHeadersRemoved(request);
    }

    @Test
    public void xForwardedHeaders() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("X-Forwarded-Host", "84.198.58.199");
        headers.add("X-Forwarded-Port", "443");
        headers.add("X-Forwarded-Proto", "https");
        headers.add("foo", "bar");
        ServerHttpRequest request = this.requestMutator.apply(getRequest(headers));
        Assert.assertEquals(new URI("https://84.198.58.199/path"), request.getURI());
        assertForwardedHeadersRemoved(request);
    }

    @Test
    public void forwardedHeader() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Forwarded", "host=84.198.58.199;proto=https");
        ServerHttpRequest request = this.requestMutator.apply(getRequest(headers));
        Assert.assertEquals(new URI("https://84.198.58.199/path"), request.getURI());
        assertForwardedHeadersRemoved(request);
    }

    @Test
    public void xForwardedPrefix() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("X-Forwarded-Prefix", "/prefix");
        ServerHttpRequest request = this.requestMutator.apply(getRequest(headers));
        Assert.assertEquals(new URI("http://example.com/prefix/path"), request.getURI());
        Assert.assertEquals("/prefix/path", request.getPath().value());
        assertForwardedHeadersRemoved(request);
    }

    @Test
    public void xForwardedPrefixTrailingSlash() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("X-Forwarded-Prefix", "/prefix////");
        ServerHttpRequest request = this.requestMutator.apply(getRequest(headers));
        Assert.assertEquals(new URI("http://example.com/prefix/path"), request.getURI());
        Assert.assertEquals("/prefix/path", request.getPath().value());
        assertForwardedHeadersRemoved(request);
    }

    // SPR-17525
    @Test
    public void shouldNotDoubleEncode() throws Exception {
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Forwarded", "host=84.198.58.199;proto=https");
        ServerHttpRequest request = method(HttpMethod.GET, new URI("http://example.com/a%20b?q=a%2Bb")).headers(headers).build();
        request = this.requestMutator.apply(request);
        Assert.assertEquals(new URI("https://84.198.58.199/a%20b?q=a%2Bb"), request.getURI());
        assertForwardedHeadersRemoved(request);
    }
}

