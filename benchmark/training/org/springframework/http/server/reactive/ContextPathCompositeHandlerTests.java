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
package org.springframework.http.server.reactive;


import HttpStatus.NOT_FOUND;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.http.server.reactive.test.MockServerHttpResponse;
import reactor.core.publisher.Mono;


/**
 * Unit tests for {@link ContextPathCompositeHandler}.
 *
 * @author Rossen Stoyanchev
 */
public class ContextPathCompositeHandlerTests {
    @Test
    public void invalidContextPath() {
        testInvalid("  ", "Context path must not be empty");
        testInvalid("path", "Context path must begin with '/'");
        testInvalid("/path/", "Context path must not end with '/'");
    }

    @Test
    public void match() {
        ContextPathCompositeHandlerTests.TestHttpHandler handler1 = new ContextPathCompositeHandlerTests.TestHttpHandler();
        ContextPathCompositeHandlerTests.TestHttpHandler handler2 = new ContextPathCompositeHandlerTests.TestHttpHandler();
        ContextPathCompositeHandlerTests.TestHttpHandler handler3 = new ContextPathCompositeHandlerTests.TestHttpHandler();
        Map<String, HttpHandler> map = new HashMap<>();
        map.put("/path", handler1);
        map.put("/another/path", handler2);
        map.put("/yet/another/path", handler3);
        testHandle("/another/path/and/more", map);
        assertInvoked(handler2, "/another/path");
        assertNotInvoked(handler1, handler3);
    }

    @Test
    public void matchWithContextPathEqualToPath() {
        ContextPathCompositeHandlerTests.TestHttpHandler handler1 = new ContextPathCompositeHandlerTests.TestHttpHandler();
        ContextPathCompositeHandlerTests.TestHttpHandler handler2 = new ContextPathCompositeHandlerTests.TestHttpHandler();
        ContextPathCompositeHandlerTests.TestHttpHandler handler3 = new ContextPathCompositeHandlerTests.TestHttpHandler();
        Map<String, HttpHandler> map = new HashMap<>();
        map.put("/path", handler1);
        map.put("/another/path", handler2);
        map.put("/yet/another/path", handler3);
        testHandle("/path", map);
        assertInvoked(handler1, "/path");
        assertNotInvoked(handler2, handler3);
    }

    @Test
    public void matchWithNativeContextPath() {
        MockServerHttpRequest request = // contextPath in underlying request
        MockServerHttpRequest.get("/yet/another/path").contextPath("/yet").build();
        ContextPathCompositeHandlerTests.TestHttpHandler handler = new ContextPathCompositeHandlerTests.TestHttpHandler();
        Map<String, HttpHandler> map = Collections.singletonMap("/another/path", handler);
        handle(request, new MockServerHttpResponse());
        Assert.assertTrue(handler.wasInvoked());
        Assert.assertEquals("/yet/another/path", handler.getRequest().getPath().contextPath().value());
    }

    @Test
    public void notFound() {
        ContextPathCompositeHandlerTests.TestHttpHandler handler1 = new ContextPathCompositeHandlerTests.TestHttpHandler();
        ContextPathCompositeHandlerTests.TestHttpHandler handler2 = new ContextPathCompositeHandlerTests.TestHttpHandler();
        Map<String, HttpHandler> map = new HashMap<>();
        map.put("/path", handler1);
        map.put("/another/path", handler2);
        ServerHttpResponse response = testHandle("/yet/another/path", map);
        assertNotInvoked(handler1, handler2);
        Assert.assertEquals(NOT_FOUND, response.getStatusCode());
    }

    // SPR-17144
    @Test
    public void notFoundWithCommitAction() {
        AtomicBoolean commitInvoked = new AtomicBoolean(false);
        ServerHttpRequest request = MockServerHttpRequest.get("/unknown/path").build();
        ServerHttpResponse response = new MockServerHttpResponse();
        response.beforeCommit(() -> {
            commitInvoked.set(true);
            return Mono.empty();
        });
        Map<String, HttpHandler> map = new HashMap<>();
        ContextPathCompositeHandlerTests.TestHttpHandler handler = new ContextPathCompositeHandlerTests.TestHttpHandler();
        map.put("/path", handler);
        new ContextPathCompositeHandler(map).handle(request, response).block(Duration.ofSeconds(5));
        assertNotInvoked(handler);
        Assert.assertEquals(NOT_FOUND, response.getStatusCode());
        Assert.assertTrue(commitInvoked.get());
    }

    @SuppressWarnings("WeakerAccess")
    private static class TestHttpHandler implements HttpHandler {
        private ServerHttpRequest request;

        public boolean wasInvoked() {
            return (this.request) != null;
        }

        public ServerHttpRequest getRequest() {
            return this.request;
        }

        @Override
        public Mono<Void> handle(ServerHttpRequest request, ServerHttpResponse response) {
            this.request = request;
            return Mono.empty();
        }
    }
}

