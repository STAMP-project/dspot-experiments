/**
 * Copyright 2013-2019 the original author or authors.
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
package org.springframework.cloud.gateway.filter.headers;


import HttpHeaders.CONNECTION;
import HttpHeaders.UPGRADE;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;


/**
 *
 *
 * @author Spencer Gibb
 */
public class RemoveHopByHopHeadersFilterTests {
    @Test
    public void happyPath() {
        MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.get("http://localhost/get");
        RemoveHopByHopHeadersFilter.HEADERS_REMOVED_ON_REQUEST.forEach(( header) -> builder.header(header, (header + "1")));
        testFilter(MockServerWebExchange.from(builder));
    }

    @Test
    public void caseInsensitive() {
        MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.get("http://localhost/get");
        RemoveHopByHopHeadersFilter.HEADERS_REMOVED_ON_REQUEST.forEach(( header) -> builder.header(header.toLowerCase(), (header + "1")));
        testFilter(MockServerWebExchange.from(builder));
    }

    @Test
    public void removesHeadersListedInConnectionHeader() {
        MockServerHttpRequest.BaseBuilder<?> builder = MockServerHttpRequest.get("http://localhost/get");
        builder.header(CONNECTION, "upgrade", "keep-alive");
        builder.header(UPGRADE, "WebSocket");
        builder.header("Keep-Alive", "timeout:5");
        testFilter(MockServerWebExchange.from(builder), "upgrade", "keep-alive");
    }
}

