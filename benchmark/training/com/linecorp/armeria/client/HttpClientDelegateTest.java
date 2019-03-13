/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.client;


import HttpHeaderNames.AUTHORITY;
import HttpHeaders.EMPTY_HEADERS;
import HttpMethod.GET;
import com.linecorp.armeria.common.HttpHeaders;
import com.linecorp.armeria.common.HttpRequest;
import org.junit.Test;


public class HttpClientDelegateTest {
    @Test
    public void testExtractHost() {
        // additionalRequestHeaders has the highest precedence.
        assertThat(HttpClientDelegate.extractHost(HttpClientDelegateTest.context(HttpHeaders.of(AUTHORITY, "foo")), HttpRequest.of(HttpHeaders.of(GET, "/").set(AUTHORITY, "bar:8080")), Endpoint.of("baz", 8080))).isEqualTo("foo");
        // Request header
        assertThat(HttpClientDelegate.extractHost(HttpClientDelegateTest.context(EMPTY_HEADERS), HttpRequest.of(HttpHeaders.of(GET, "/").set(AUTHORITY, "bar:8080")), Endpoint.of("baz", 8080))).isEqualTo("bar");
        // Endpoint.host() has the lowest precedence.
        assertThat(HttpClientDelegate.extractHost(HttpClientDelegateTest.context(EMPTY_HEADERS), HttpRequest.of(GET, "/"), Endpoint.of("baz", 8080))).isEqualTo("baz");
        // IPv6 address authority
        assertThat(HttpClientDelegate.extractHost(HttpClientDelegateTest.context(HttpHeaders.of(AUTHORITY, "[::1]:8443")), HttpRequest.of(GET, "/"), Endpoint.of("baz", 8080))).isEqualTo("::1");
        // An invalid authority should be ignored.
        assertThat(HttpClientDelegate.extractHost(HttpClientDelegateTest.context(HttpHeaders.of(AUTHORITY, "[::1")), HttpRequest.of(GET, "/"), Endpoint.of("baz", 8080))).isEqualTo("baz");
        assertThat(HttpClientDelegate.extractHost(HttpClientDelegateTest.context(HttpHeaders.of(AUTHORITY, ":8080")), HttpRequest.of(GET, "/"), Endpoint.of("baz", 8080))).isEqualTo("baz");
        // If additionalRequestHeader's authority is invalid but req.authority() is valid,
        // use the authority from 'req'.
        assertThat(HttpClientDelegate.extractHost(HttpClientDelegateTest.context(HttpHeaders.of(AUTHORITY, "[::1")), HttpRequest.of(HttpHeaders.of(GET, "/").set(AUTHORITY, "bar")), Endpoint.of("baz", 8080))).isEqualTo("bar");
        assertThat(HttpClientDelegate.extractHost(HttpClientDelegateTest.context(HttpHeaders.of(AUTHORITY, ":8080")), HttpRequest.of(HttpHeaders.of(GET, "/").set(AUTHORITY, "bar")), Endpoint.of("baz", 8080))).isEqualTo("bar");
    }
}

