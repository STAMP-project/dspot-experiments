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


import HttpStatus.BAD_REQUEST;
import HttpStatus.CREATED;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Unit tests for {@link ResponseStatusExceptionHandler}.
 *
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 */
public class ResponseStatusExceptionHandlerTests {
    protected final MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/"));

    protected ResponseStatusExceptionHandler handler;

    @Test
    public void handleResponseStatusException() {
        Throwable ex = new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        this.handler.handle(this.exchange, ex).block(Duration.ofSeconds(5));
        Assert.assertEquals(BAD_REQUEST, getStatusCode());
    }

    @Test
    public void handleNestedResponseStatusException() {
        Throwable ex = new Exception(new org.springframework.web.server.ResponseStatusException(HttpStatus.BAD_REQUEST, ""));
        this.handler.handle(this.exchange, ex).block(Duration.ofSeconds(5));
        Assert.assertEquals(BAD_REQUEST, getStatusCode());
    }

    @Test
    public void unresolvedException() {
        Throwable expected = new IllegalStateException();
        Mono<Void> mono = this.handler.handle(this.exchange, expected);
        StepVerifier.create(mono).consumeErrorWith(( actual) -> assertSame(expected, actual)).verify();
    }

    // SPR-16231
    @Test
    public void responseCommitted() {
        Throwable ex = new org.springframework.web.server.ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops");
        this.exchange.getResponse().setStatusCode(CREATED);
        Mono<Void> mono = this.exchange.getResponse().setComplete().then(Mono.defer(() -> this.handler.handle(this.exchange, ex)));
        StepVerifier.create(mono).consumeErrorWith(( actual) -> assertSame(ex, actual)).verify();
    }
}

