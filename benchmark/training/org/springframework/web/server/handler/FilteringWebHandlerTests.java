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
package org.springframework.web.server.handler;


import HttpStatus.INTERNAL_SERVER_ERROR;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link FilteringWebHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class FilteringWebHandlerTests {
    private static Log logger = LogFactory.getLog(FilteringWebHandlerTests.class);

    @Test
    public void multipleFilters() throws Exception {
        FilteringWebHandlerTests.TestFilter filter1 = new FilteringWebHandlerTests.TestFilter();
        FilteringWebHandlerTests.TestFilter filter2 = new FilteringWebHandlerTests.TestFilter();
        FilteringWebHandlerTests.TestFilter filter3 = new FilteringWebHandlerTests.TestFilter();
        FilteringWebHandlerTests.StubWebHandler targetHandler = new FilteringWebHandlerTests.StubWebHandler();
        new FilteringWebHandler(targetHandler, Arrays.asList(filter1, filter2, filter3)).handle(MockServerWebExchange.from(MockServerHttpRequest.get("/"))).block(Duration.ZERO);
        Assert.assertTrue(filter1.invoked());
        Assert.assertTrue(filter2.invoked());
        Assert.assertTrue(filter3.invoked());
        Assert.assertTrue(targetHandler.invoked());
    }

    @Test
    public void zeroFilters() throws Exception {
        FilteringWebHandlerTests.StubWebHandler targetHandler = new FilteringWebHandlerTests.StubWebHandler();
        new FilteringWebHandler(targetHandler, Collections.emptyList()).handle(MockServerWebExchange.from(MockServerHttpRequest.get("/"))).block(Duration.ZERO);
        Assert.assertTrue(targetHandler.invoked());
    }

    @Test
    public void shortcircuitFilter() throws Exception {
        FilteringWebHandlerTests.TestFilter filter1 = new FilteringWebHandlerTests.TestFilter();
        FilteringWebHandlerTests.ShortcircuitingFilter filter2 = new FilteringWebHandlerTests.ShortcircuitingFilter();
        FilteringWebHandlerTests.TestFilter filter3 = new FilteringWebHandlerTests.TestFilter();
        FilteringWebHandlerTests.StubWebHandler targetHandler = new FilteringWebHandlerTests.StubWebHandler();
        new FilteringWebHandler(targetHandler, Arrays.asList(filter1, filter2, filter3)).handle(MockServerWebExchange.from(MockServerHttpRequest.get("/"))).block(Duration.ZERO);
        Assert.assertTrue(filter1.invoked());
        Assert.assertTrue(filter2.invoked());
        Assert.assertFalse(filter3.invoked());
        Assert.assertFalse(targetHandler.invoked());
    }

    @Test
    public void asyncFilter() throws Exception {
        FilteringWebHandlerTests.AsyncFilter filter = new FilteringWebHandlerTests.AsyncFilter();
        FilteringWebHandlerTests.StubWebHandler targetHandler = new FilteringWebHandlerTests.StubWebHandler();
        new FilteringWebHandler(targetHandler, Collections.singletonList(filter)).handle(MockServerWebExchange.from(MockServerHttpRequest.get("/"))).block(Duration.ofSeconds(5));
        Assert.assertTrue(filter.invoked());
        Assert.assertTrue(targetHandler.invoked());
    }

    @Test
    public void handleErrorFromFilter() throws Exception {
        MockServerHttpRequest request = MockServerHttpRequest.get("/").build();
        MockServerHttpResponse response = new MockServerHttpResponse();
        FilteringWebHandlerTests.TestExceptionHandler exceptionHandler = new FilteringWebHandlerTests.TestExceptionHandler();
        org.springframework.web.server.adapter.WebHttpHandlerBuilder.webHandler(new FilteringWebHandlerTests.StubWebHandler()).filter(new FilteringWebHandlerTests.ExceptionFilter()).exceptionHandler(exceptionHandler).build().handle(request, response).block();
        Assert.assertEquals(INTERNAL_SERVER_ERROR, getStatusCode());
        Assert.assertNotNull(exceptionHandler.ex);
        Assert.assertEquals("boo", exceptionHandler.ex.getMessage());
    }

    private static class TestFilter implements WebFilter {
        private volatile boolean invoked;

        public boolean invoked() {
            return this.invoked;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            this.invoked = true;
            return doFilter(exchange, chain);
        }

        public Mono<Void> doFilter(ServerWebExchange exchange, WebFilterChain chain) {
            return chain.filter(exchange);
        }
    }

    private static class ShortcircuitingFilter extends FilteringWebHandlerTests.TestFilter {
        @Override
        public Mono<Void> doFilter(ServerWebExchange exchange, WebFilterChain chain) {
            return Mono.empty();
        }
    }

    private static class AsyncFilter extends FilteringWebHandlerTests.TestFilter {
        @Override
        public Mono<Void> doFilter(ServerWebExchange exchange, WebFilterChain chain) {
            return doAsyncWork().flatMap(( asyncResult) -> {
                org.springframework.web.server.handler.logger.debug(("Async result: " + asyncResult));
                return chain.filter(exchange);
            });
        }

        private Mono<String> doAsyncWork() {
            return Mono.delay(Duration.ofMillis(100L)).map(( l) -> "123");
        }
    }

    private static class ExceptionFilter implements WebFilter {
        @Override
        public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
            return Mono.error(new IllegalStateException("boo"));
        }
    }

    private static class TestExceptionHandler implements WebExceptionHandler {
        private Throwable ex;

        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
            this.ex = ex;
            return Mono.error(ex);
        }
    }

    private static class StubWebHandler implements WebHandler {
        private volatile boolean invoked;

        public boolean invoked() {
            return this.invoked;
        }

        @Override
        public Mono<Void> handle(ServerWebExchange exchange) {
            FilteringWebHandlerTests.logger.trace("StubHandler invoked.");
            this.invoked = true;
            return Mono.empty();
        }
    }
}

