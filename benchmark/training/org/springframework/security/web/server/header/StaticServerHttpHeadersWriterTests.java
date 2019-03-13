/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.web.server.header;


import CacheControlServerHttpHeadersWriter.CACHE_CONTRTOL_VALUE;
import CacheControlServerHttpHeadersWriter.EXPIRES_VALUE;
import CacheControlServerHttpHeadersWriter.PRAGMA_VALUE;
import ContentTypeOptionsServerHttpHeadersWriter.NOSNIFF;
import ContentTypeOptionsServerHttpHeadersWriter.X_CONTENT_OPTIONS;
import HttpHeaders.CACHE_CONTROL;
import HttpHeaders.EXPIRES;
import HttpHeaders.PRAGMA;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
public class StaticServerHttpHeadersWriterTests {
    StaticServerHttpHeadersWriter writer = StaticServerHttpHeadersWriter.builder().header(X_CONTENT_OPTIONS, NOSNIFF).build();

    ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());

    HttpHeaders headers = exchange.getResponse().getHeaders();

    @Test
    public void writeHeadersWhenSingleHeaderThenWritesHeader() {
        writer.writeHttpHeaders(exchange);
        assertThat(headers.get(X_CONTENT_OPTIONS)).containsOnly(NOSNIFF);
    }

    @Test
    public void writeHeadersWhenSingleHeaderAndHeaderWrittenThenSuccess() {
        String headerValue = "other";
        headers.set(X_CONTENT_OPTIONS, headerValue);
        writer.writeHttpHeaders(exchange);
        assertThat(headers.get(X_CONTENT_OPTIONS)).containsOnly(headerValue);
    }

    @Test
    public void writeHeadersWhenMultiHeaderThenWritesAllHeaders() {
        writer = StaticServerHttpHeadersWriter.builder().header(CACHE_CONTROL, CACHE_CONTRTOL_VALUE).header(PRAGMA, PRAGMA_VALUE).header(EXPIRES, EXPIRES_VALUE).build();
        writer.writeHttpHeaders(exchange);
        assertThat(headers.get(CACHE_CONTROL)).containsOnly(CACHE_CONTRTOL_VALUE);
        assertThat(headers.get(PRAGMA)).containsOnly(PRAGMA_VALUE);
        assertThat(headers.get(EXPIRES)).containsOnly(EXPIRES_VALUE);
    }

    @Test
    public void writeHeadersWhenMultiHeaderAndSingleWrittenThenNoHeadersOverridden() {
        String headerValue = "other";
        headers.set(CACHE_CONTROL, headerValue);
        writer = StaticServerHttpHeadersWriter.builder().header(CACHE_CONTROL, CACHE_CONTRTOL_VALUE).header(PRAGMA, PRAGMA_VALUE).header(EXPIRES, EXPIRES_VALUE).build();
        writer.writeHttpHeaders(exchange);
        assertThat(headers).hasSize(1);
        assertThat(headers.get(CACHE_CONTROL)).containsOnly(headerValue);
    }
}

