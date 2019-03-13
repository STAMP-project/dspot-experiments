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
package org.springframework.cloud.gateway.handler.predicate;


import CloudFoundryRouteServiceRoutePredicateFactory.X_CF_FORWARDED_URL;
import CloudFoundryRouteServiceRoutePredicateFactory.X_CF_PROXY_METADATA;
import CloudFoundryRouteServiceRoutePredicateFactory.X_CF_PROXY_SIGNATURE;
import java.util.function.Predicate;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;


/**
 *
 *
 * @author Andrew Fitzgerald
 */
public class CloudFoundryRouteServiceRoutePredicateFactoryTest {
    private Predicate<ServerWebExchange> predicate;

    @Test
    public void itReturnsTrueWithAllHeadersPresent() {
        MockServerHttpRequest request = MockServerHttpRequest.get("someurl").header(X_CF_FORWARDED_URL, "url").header(X_CF_PROXY_METADATA, "metadata").header(X_CF_PROXY_SIGNATURE, "signature").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        assertThat(predicate.test(exchange)).isTrue();
    }

    @Test
    public void itReturnsFalseWithAHeadersMissing() {
        MockServerHttpRequest request = MockServerHttpRequest.get("someurl").header(X_CF_FORWARDED_URL, "url").header(X_CF_PROXY_METADATA, "metadata").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        assertThat(predicate.test(exchange)).isFalse();
    }

    @Test
    public void itReturnsFalseWithNoHeaders() {
        MockServerHttpRequest request = MockServerHttpRequest.get("someurl").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);
        assertThat(predicate.test(exchange)).isFalse();
    }
}

