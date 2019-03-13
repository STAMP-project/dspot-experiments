/**
 * Copyright 2002-2019 the original author or authors.
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
package org.springframework.messaging.rsocket;


import io.netty.buffer.PooledByteBufAllocator;
import io.rsocket.RSocket;
import io.rsocket.transport.netty.server.CloseableChannel;
import java.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.test.StepVerifier;


/**
 * Server-side handling of RSocket requests.
 *
 * @author Rossen Stoyanchev
 */
public class RSocketClientToServerIntegrationTests {
    private static AnnotationConfigApplicationContext context;

    private static CloseableChannel server;

    private static FireAndForgetCountingInterceptor interceptor = new FireAndForgetCountingInterceptor();

    private static RSocket client;

    private static RSocketRequester requester;

    @Test
    public void fireAndForget() {
        Flux.range(1, 3).concatMap(( i) -> RSocketClientToServerIntegrationTests.requester.route("receive").data(("Hello " + i)).send()).blockLast();
        StepVerifier.create(RSocketClientToServerIntegrationTests.context.getBean(RSocketClientToServerIntegrationTests.ServerController.class).fireForgetPayloads).expectNext("Hello 1").expectNext("Hello 2").expectNext("Hello 3").thenCancel().verify(Duration.ofSeconds(5));
        Assert.assertEquals(1, RSocketClientToServerIntegrationTests.interceptor.getRSocketCount());
        Assert.assertEquals("Fire and forget requests did not actually complete handling on the server side", 3, RSocketClientToServerIntegrationTests.interceptor.getFireAndForgetCount(0));
    }

    @Test
    public void echo() {
        Flux<String> result = Flux.range(1, 3).concatMap(( i) -> RSocketClientToServerIntegrationTests.requester.route("echo").data(("Hello " + i)).retrieveMono(.class));
        StepVerifier.create(result).expectNext("Hello 1").expectNext("Hello 2").expectNext("Hello 3").verifyComplete();
    }

    @Test
    public void echoAsync() {
        Flux<String> result = Flux.range(1, 3).concatMap(( i) -> RSocketClientToServerIntegrationTests.requester.route("echo-async").data(("Hello " + i)).retrieveMono(.class));
        StepVerifier.create(result).expectNext("Hello 1 async").expectNext("Hello 2 async").expectNext("Hello 3 async").verifyComplete();
    }

    @Test
    public void echoStream() {
        Flux<String> result = RSocketClientToServerIntegrationTests.requester.route("echo-stream").data("Hello").retrieveFlux(String.class);
        StepVerifier.create(result).expectNext("Hello 0").expectNextCount(6).expectNext("Hello 7").thenCancel().verify();
    }

    @Test
    public void echoChannel() {
        Flux<String> result = RSocketClientToServerIntegrationTests.requester.route("echo-channel").data(Flux.range(1, 10).map(( i) -> "Hello " + i), String.class).retrieveFlux(String.class);
        StepVerifier.create(result).expectNext("Hello 1 async").expectNextCount(8).expectNext("Hello 10 async").verifyComplete();
    }

    @Test
    public void voidReturnValue() {
        Flux<String> result = RSocketClientToServerIntegrationTests.requester.route("void-return-value").data("Hello").retrieveFlux(String.class);
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    public void voidReturnValueFromExceptionHandler() {
        Flux<String> result = RSocketClientToServerIntegrationTests.requester.route("void-return-value").data("bad").retrieveFlux(String.class);
        StepVerifier.create(result).verifyComplete();
    }

    @Test
    public void handleWithThrownException() {
        Mono<String> result = RSocketClientToServerIntegrationTests.requester.route("thrown-exception").data("a").retrieveMono(String.class);
        StepVerifier.create(result).expectNext("Invalid input error handled").verifyComplete();
    }

    @Test
    public void handleWithErrorSignal() {
        Mono<String> result = RSocketClientToServerIntegrationTests.requester.route("error-signal").data("a").retrieveMono(String.class);
        StepVerifier.create(result).expectNext("Invalid input error handled").verifyComplete();
    }

    @Test
    public void noMatchingRoute() {
        Mono<String> result = RSocketClientToServerIntegrationTests.requester.route("invalid").data("anything").retrieveMono(String.class);
        StepVerifier.create(result).verifyErrorMessage("No handler for destination 'invalid'");
    }

    @Controller
    static class ServerController {
        final ReplayProcessor<String> fireForgetPayloads = ReplayProcessor.create();

        @MessageMapping("receive")
        void receive(String payload) {
            this.fireForgetPayloads.onNext(payload);
        }

        @MessageMapping("echo")
        String echo(String payload) {
            return payload;
        }

        @MessageMapping("echo-async")
        Mono<String> echoAsync(String payload) {
            return Mono.delay(Duration.ofMillis(10)).map(( aLong) -> payload + " async");
        }

        @MessageMapping("echo-stream")
        Flux<String> echoStream(String payload) {
            return Flux.interval(Duration.ofMillis(10)).map(( aLong) -> (payload + " ") + aLong);
        }

        @MessageMapping("echo-channel")
        Flux<String> echoChannel(Flux<String> payloads) {
            return payloads.delayElements(Duration.ofMillis(10)).map(( payload) -> payload + " async");
        }

        @MessageMapping("thrown-exception")
        Mono<String> handleAndThrow(String payload) {
            throw new IllegalArgumentException("Invalid input error");
        }

        @MessageMapping("error-signal")
        Mono<String> handleAndReturnError(String payload) {
            return Mono.error(new IllegalArgumentException("Invalid input error"));
        }

        @MessageMapping("void-return-value")
        Mono<Void> voidReturnValue(String payload) {
            return !(payload.equals("bad")) ? Mono.delay(Duration.ofMillis(10)).then(Mono.empty()) : Mono.error(new IllegalStateException("bad"));
        }

        @MessageExceptionHandler
        Mono<String> handleException(IllegalArgumentException ex) {
            return Mono.delay(Duration.ofMillis(10)).map(( aLong) -> (ex.getMessage()) + " handled");
        }

        @MessageExceptionHandler
        Mono<Void> handleExceptionWithVoidReturnValue(IllegalStateException ex) {
            return Mono.delay(Duration.ofMillis(10)).then(Mono.empty());
        }
    }

    @Configuration
    static class ServerConfig {
        @Bean
        public RSocketClientToServerIntegrationTests.ServerController controller() {
            return new RSocketClientToServerIntegrationTests.ServerController();
        }

        @Bean
        public MessageHandlerAcceptor messageHandlerAcceptor() {
            MessageHandlerAcceptor acceptor = new MessageHandlerAcceptor();
            acceptor.setRSocketStrategies(rsocketStrategies());
            return acceptor;
        }

        @Bean
        public RSocketStrategies rsocketStrategies() {
            return RSocketStrategies.builder().decoder(StringDecoder.allMimeTypes()).encoder(CharSequenceEncoder.allMimeTypes()).dataBufferFactory(new org.springframework.core.io.buffer.NettyDataBufferFactory(PooledByteBufAllocator.DEFAULT)).build();
        }
    }
}

