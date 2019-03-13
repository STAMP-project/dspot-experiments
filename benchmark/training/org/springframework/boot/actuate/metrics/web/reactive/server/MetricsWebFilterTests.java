/**
 * Copyright 2012-2018 the original author or authors.
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
package org.springframework.boot.actuate.metrics.web.reactive.server;


import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.Duration;
import org.junit.Test;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;


/**
 * Tests for {@link MetricsWebFilter}
 *
 * @author Brian Clozel
 */
public class MetricsWebFilterTests {
    private static final String REQUEST_METRICS_NAME = "http.server.requests";

    private SimpleMeterRegistry registry;

    private MetricsWebFilter webFilter;

    @Test
    public void filterAddsTagsToRegistry() {
        MockServerWebExchange exchange = createExchange("/projects/spring-boot", "/projects/{project}");
        this.webFilter.filter(exchange, ( serverWebExchange) -> exchange.getResponse().setComplete()).block(Duration.ofSeconds(30));
        assertMetricsContainsTag("uri", "/projects/{project}");
        assertMetricsContainsTag("status", "200");
    }

    @Test
    public void filterAddsTagsToRegistryForExceptions() {
        MockServerWebExchange exchange = createExchange("/projects/spring-boot", "/projects/{project}");
        this.webFilter.filter(exchange, ( serverWebExchange) -> Mono.error(new IllegalStateException("test error"))).onErrorResume(( t) -> {
            exchange.getResponse().setStatusCodeValue(500);
            return exchange.getResponse().setComplete();
        }).block(Duration.ofSeconds(30));
        assertMetricsContainsTag("uri", "/projects/{project}");
        assertMetricsContainsTag("status", "500");
        assertMetricsContainsTag("exception", "IllegalStateException");
    }

    @Test
    public void filterAddsNonEmptyTagsToRegistryForAnonymousExceptions() {
        final Exception anonymous = new Exception("test error") {};
        MockServerWebExchange exchange = createExchange("/projects/spring-boot", "/projects/{project}");
        this.webFilter.filter(exchange, ( serverWebExchange) -> Mono.error(anonymous)).onErrorResume(( t) -> {
            exchange.getResponse().setStatusCodeValue(500);
            return exchange.getResponse().setComplete();
        }).block(Duration.ofSeconds(30));
        assertMetricsContainsTag("uri", "/projects/{project}");
        assertMetricsContainsTag("status", "500");
        assertMetricsContainsTag("exception", anonymous.getClass().getName());
    }

    @Test
    public void filterAddsTagsToRegistryForExceptionsAndCommittedResponse() {
        MockServerWebExchange exchange = createExchange("/projects/spring-boot", "/projects/{project}");
        this.webFilter.filter(exchange, ( serverWebExchange) -> {
            exchange.getResponse().setStatusCodeValue(500);
            return exchange.getResponse().setComplete().then(Mono.error(new IllegalStateException("test error")));
        }).onErrorResume(( t) -> Mono.empty()).block(Duration.ofSeconds(30));
        assertMetricsContainsTag("uri", "/projects/{project}");
        assertMetricsContainsTag("status", "500");
    }
}

