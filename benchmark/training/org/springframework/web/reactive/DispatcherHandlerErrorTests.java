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
package org.springframework.web.reactive;


import HttpStatus.INTERNAL_SERVER_ERROR;
import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.mock.http.server.reactive.test.MockServerHttpRequest;
import org.springframework.mock.web.test.server.MockServerWebExchange;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.accept.HeaderContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import org.springframework.web.server.WebHandler;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


/**
 * Test the effect of exceptions at different stages of request processing by
 * checking the error signals on the completion publisher.
 *
 * @author Rossen Stoyanchev
 */
@SuppressWarnings({ "ThrowableResultOfMethodCallIgnored", "ThrowableInstanceNeverThrown" })
public class DispatcherHandlerErrorTests {
    private static final IllegalStateException EXCEPTION = new IllegalStateException("boo");

    private DispatcherHandler dispatcherHandler;

    @Test
    public void noHandler() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/does-not-exist"));
        Mono<Void> mono = this.dispatcherHandler.handle(exchange);
        StepVerifier.create(mono).consumeErrorWith(( ex) -> {
            assertThat(ex, instanceOf(.class));
            assertThat(ex.getMessage(), is("404 NOT_FOUND \"No matching handler\""));
        }).verify();
        // SPR-17475
        AtomicReference<Throwable> exceptionRef = new AtomicReference<>();
        StepVerifier.create(mono).consumeErrorWith(exceptionRef::set).verify();
        StepVerifier.create(mono).consumeErrorWith(( ex) -> assertNotSame(exceptionRef.get(), ex)).verify();
    }

    @Test
    public void controllerReturnsMonoError() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/error-signal"));
        Mono<Void> publisher = this.dispatcherHandler.handle(exchange);
        StepVerifier.create(publisher).consumeErrorWith(( error) -> assertSame(EXCEPTION, error)).verify();
    }

    @Test
    public void controllerThrowsException() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/raise-exception"));
        Mono<Void> publisher = this.dispatcherHandler.handle(exchange);
        StepVerifier.create(publisher).consumeErrorWith(( error) -> assertSame(EXCEPTION, error)).verify();
    }

    @Test
    public void unknownReturnType() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/unknown-return-type"));
        Mono<Void> publisher = this.dispatcherHandler.handle(exchange);
        StepVerifier.create(publisher).consumeErrorWith(( error) -> {
            assertThat(error, instanceOf(.class));
            assertThat(error.getMessage(), startsWith("No HandlerResultHandler"));
        }).verify();
    }

    @Test
    public void responseBodyMessageConversionError() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/request-body").accept(APPLICATION_JSON).body("body"));
        Mono<Void> publisher = this.dispatcherHandler.handle(exchange);
        StepVerifier.create(publisher).consumeErrorWith(( error) -> assertThat(error, instanceOf(.class))).verify();
    }

    @Test
    public void requestBodyError() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/request-body").body(Mono.error(DispatcherHandlerErrorTests.EXCEPTION)));
        Mono<Void> publisher = this.dispatcherHandler.handle(exchange);
        StepVerifier.create(publisher).consumeErrorWith(( error) -> assertSame(EXCEPTION, error)).verify();
    }

    @Test
    public void webExceptionHandler() {
        ServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/unknown-argument-type"));
        List<WebExceptionHandler> handlers = Collections.singletonList(new DispatcherHandlerErrorTests.ServerError500ExceptionHandler());
        WebHandler webHandler = new org.springframework.web.server.handler.ExceptionHandlingWebHandler(this.dispatcherHandler, handlers);
        webHandler.handle(exchange).block(Duration.ofSeconds(5));
        Assert.assertEquals(INTERNAL_SERVER_ERROR, exchange.getResponse().getStatusCode());
    }

    @Configuration
    @SuppressWarnings({ "unused", "WeakerAccess" })
    static class TestConfig {
        @Bean
        public RequestMappingHandlerMapping handlerMapping() {
            return new RequestMappingHandlerMapping();
        }

        @Bean
        public RequestMappingHandlerAdapter handlerAdapter() {
            return new RequestMappingHandlerAdapter();
        }

        @Bean
        public ResponseBodyResultHandler resultHandler() {
            return new ResponseBodyResultHandler(Collections.singletonList(new org.springframework.http.codec.EncoderHttpMessageWriter(CharSequenceEncoder.textPlainOnly())), new HeaderContentTypeResolver());
        }

        @Bean
        public DispatcherHandlerErrorTests.TestController testController() {
            return new DispatcherHandlerErrorTests.TestController();
        }
    }

    @Controller
    @SuppressWarnings("unused")
    private static class TestController {
        @RequestMapping("/error-signal")
        @ResponseBody
        public Publisher<String> errorSignal() {
            return Mono.error(DispatcherHandlerErrorTests.EXCEPTION);
        }

        @RequestMapping("/raise-exception")
        public void raiseException() {
            throw DispatcherHandlerErrorTests.EXCEPTION;
        }

        @RequestMapping("/unknown-return-type")
        public DispatcherHandlerErrorTests.Foo unknownReturnType() {
            return new DispatcherHandlerErrorTests.Foo();
        }

        @RequestMapping("/request-body")
        @ResponseBody
        public Publisher<String> requestBody(@RequestBody
        Publisher<String> body) {
            return Mono.from(body).map(( s) -> "hello " + s);
        }
    }

    private static class Foo {}

    private static class ServerError500ExceptionHandler implements WebExceptionHandler {
        @Override
        public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
            exchange.getResponse().setStatusCode(INTERNAL_SERVER_ERROR);
            return Mono.empty();
        }
    }
}

