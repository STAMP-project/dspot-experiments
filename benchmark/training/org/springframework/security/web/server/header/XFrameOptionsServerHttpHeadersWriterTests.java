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


import XFrameOptionsServerHttpHeadersWriter.Mode.DENY;
import XFrameOptionsServerHttpHeadersWriter.Mode.SAMEORIGIN;
import XFrameOptionsServerHttpHeadersWriter.X_FRAME_OPTIONS;
import org.junit.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;


/**
 *
 *
 * @author Rob Winch
 * @since 5.0
 */
public class XFrameOptionsServerHttpHeadersWriterTests {
    ServerWebExchange exchange = exchange(org.springframework.mock.http.server.reactive.MockServerHttpRequest.get("/"));

    XFrameOptionsServerHttpHeadersWriter writer;

    @Test
    public void writeHeadersWhenUsingDefaultsThenWritesDeny() {
        writer.writeHttpHeaders(exchange);
        HttpHeaders headers = exchange.getResponse().getHeaders();
        assertThat(headers).hasSize(1);
        assertThat(headers.get(X_FRAME_OPTIONS)).containsOnly("DENY");
    }

    @Test
    public void writeHeadersWhenUsingExplicitDenyThenWritesDeny() {
        writer.setMode(DENY);
        writer.writeHttpHeaders(exchange);
        HttpHeaders headers = exchange.getResponse().getHeaders();
        assertThat(headers).hasSize(1);
        assertThat(headers.get(X_FRAME_OPTIONS)).containsOnly("DENY");
    }

    @Test
    public void writeHeadersWhenUsingSameOriginThenWritesSameOrigin() {
        writer.setMode(SAMEORIGIN);
        writer.writeHttpHeaders(exchange);
        HttpHeaders headers = exchange.getResponse().getHeaders();
        assertThat(headers).hasSize(1);
        assertThat(headers.get(X_FRAME_OPTIONS)).containsOnly("SAMEORIGIN");
    }

    @Test
    public void writeHeadersWhenAlreadyWrittenThenWritesHeader() {
        String headerValue = "other";
        exchange.getResponse().getHeaders().set(X_FRAME_OPTIONS, headerValue);
        writer.writeHttpHeaders(exchange);
        HttpHeaders headers = exchange.getResponse().getHeaders();
        assertThat(headers).hasSize(1);
        assertThat(headers.get(X_FRAME_OPTIONS)).containsOnly(headerValue);
    }
}

