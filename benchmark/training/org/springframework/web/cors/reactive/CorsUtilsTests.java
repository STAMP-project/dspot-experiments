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
package org.springframework.web.cors.reactive;


import HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD;
import HttpHeaders.ORIGIN;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;


/**
 * Test case for reactive {@link CorsUtils}.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 */
public class CorsUtilsTests {
    @Test
    public void isCorsRequest() {
        ServerHttpRequest request = MockServerHttpRequest.get("/").header(ORIGIN, "http://domain.com").build();
        Assert.assertTrue(CorsUtils.isCorsRequest(request));
    }

    @Test
    public void isNotCorsRequest() {
        ServerHttpRequest request = MockServerHttpRequest.get("/").build();
        Assert.assertFalse(CorsUtils.isCorsRequest(request));
    }

    @Test
    public void isPreFlightRequest() {
        ServerHttpRequest request = MockServerHttpRequest.options("/").header(ORIGIN, "http://domain.com").header(ACCESS_CONTROL_REQUEST_METHOD, "GET").build();
        Assert.assertTrue(CorsUtils.isPreFlightRequest(request));
    }

    @Test
    public void isNotPreFlightRequest() {
        ServerHttpRequest request = MockServerHttpRequest.get("/").build();
        Assert.assertFalse(CorsUtils.isPreFlightRequest(request));
        request = MockServerHttpRequest.options("/").header(ORIGIN, "http://domain.com").build();
        Assert.assertFalse(CorsUtils.isPreFlightRequest(request));
        request = MockServerHttpRequest.options("/").header(ACCESS_CONTROL_REQUEST_METHOD, "GET").build();
        Assert.assertFalse(CorsUtils.isPreFlightRequest(request));
    }

    // SPR-16262
    @Test
    public void isSameOriginWithXForwardedHeaders() {
        String server = "mydomain1.com";
        testWithXForwardedHeaders(server, (-1), "https", null, (-1), "https://mydomain1.com");
        testWithXForwardedHeaders(server, 123, "https", null, (-1), "https://mydomain1.com");
        testWithXForwardedHeaders(server, (-1), "https", "mydomain2.com", (-1), "https://mydomain2.com");
        testWithXForwardedHeaders(server, 123, "https", "mydomain2.com", (-1), "https://mydomain2.com");
        testWithXForwardedHeaders(server, (-1), "https", "mydomain2.com", 456, "https://mydomain2.com:456");
        testWithXForwardedHeaders(server, 123, "https", "mydomain2.com", 456, "https://mydomain2.com:456");
    }

    // SPR-16262
    @Test
    public void isSameOriginWithForwardedHeader() {
        String server = "mydomain1.com";
        testWithForwardedHeader(server, (-1), "proto=https", "https://mydomain1.com");
        testWithForwardedHeader(server, 123, "proto=https", "https://mydomain1.com");
        testWithForwardedHeader(server, (-1), "proto=https; host=mydomain2.com", "https://mydomain2.com");
        testWithForwardedHeader(server, 123, "proto=https; host=mydomain2.com", "https://mydomain2.com");
        testWithForwardedHeader(server, (-1), "proto=https; host=mydomain2.com:456", "https://mydomain2.com:456");
        testWithForwardedHeader(server, 123, "proto=https; host=mydomain2.com:456", "https://mydomain2.com:456");
    }

    // SPR-16362
    @Test
    public void isSameOriginWithDifferentSchemes() {
        MockServerHttpRequest request = MockServerHttpRequest.get("http://mydomain1.com").header(ORIGIN, "https://mydomain1.com").build();
        Assert.assertFalse(CorsUtils.isSameOrigin(request));
    }
}

