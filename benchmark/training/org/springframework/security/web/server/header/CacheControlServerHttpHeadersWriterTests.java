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
import HttpHeaders.CACHE_CONTROL;
import HttpHeaders.EXPIRES;
import HttpHeaders.PRAGMA;
import HttpStatus.NOT_MODIFIED;
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
public class CacheControlServerHttpHeadersWriterTests {
    CacheControlServerHttpHeadersWriter writer = new CacheControlServerHttpHeadersWriter();

    ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/").build());

    HttpHeaders headers = exchange.getResponse().getHeaders();

    @Test
    public void writeHeadersWhenCacheHeadersThenWritesAllCacheControl() {
        writer.writeHttpHeaders(exchange);
        assertThat(headers).hasSize(3);
        assertThat(headers.get(CACHE_CONTROL)).containsOnly(CACHE_CONTRTOL_VALUE);
        assertThat(headers.get(EXPIRES)).containsOnly(EXPIRES_VALUE);
        assertThat(headers.get(PRAGMA)).containsOnly(PRAGMA_VALUE);
    }

    @Test
    public void writeHeadersWhenCacheControlThenNoCacheControlHeaders() {
        String cacheControl = "max-age=1234";
        headers.set(CACHE_CONTROL, cacheControl);
        writer.writeHttpHeaders(exchange);
        assertThat(headers.get(CACHE_CONTROL)).containsOnly(cacheControl);
    }

    @Test
    public void writeHeadersWhenPragmaThenNoCacheControlHeaders() {
        String pragma = "1";
        headers.set(PRAGMA, pragma);
        writer.writeHttpHeaders(exchange);
        assertThat(headers).hasSize(1);
        assertThat(headers.get(PRAGMA)).containsOnly(pragma);
    }

    @Test
    public void writeHeadersWhenExpiresThenNoCacheControlHeaders() {
        String expires = "1";
        headers.set(EXPIRES, expires);
        writer.writeHttpHeaders(exchange);
        assertThat(headers).hasSize(1);
        assertThat(headers.get(EXPIRES)).containsOnly(expires);
    }

    // gh-5534
    @Test
    public void writeHeadersWhenNotModifiedThenNoCacheControlHeaders() {
        exchange.getResponse().setStatusCode(NOT_MODIFIED);
        writer.writeHttpHeaders(exchange);
        assertThat(headers).isEmpty();
    }
}

