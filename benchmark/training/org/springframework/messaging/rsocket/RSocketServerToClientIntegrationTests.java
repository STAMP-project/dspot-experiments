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
import io.rsocket.Closeable;
import java.time.Duration;
import java.util.Collections;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoProcessor;
import reactor.core.publisher.ReplayProcessor;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;


/**
 * Client-side handling of requests initiated from the server side.
 *
 * @author Rossen Stoyanchev
 */
public class RSocketServerToClientIntegrationTests {
    private static AnnotationConfigApplicationContext context;

    private static Closeable server;

    @Test
    public void echo() {
        RSocketServerToClientIntegrationTests.connectAndVerify("connect.echo");
    }

    @Test
    public void echoAsync() {
        RSocketServerToClientIntegrationTests.connectAndVerify("connect.echo-async");
    }

    @Test
    public void echoStream() {
        RSocketServerToClientIntegrationTests.connectAndVerify("connect.echo-stream");
    }

    @Test
    public void echoChannel() {
        RSocketServerToClientIntegrationTests.connectAndVerify("connect.echo-channel");
    }

    @Controller
    @SuppressWarnings({ "unused", "NullableProblems" })
    static class ServerController {
        // Must be initialized by @Test method...
        volatile MonoProcessor<Void> result;

        public void reset() {
            this.result = MonoProcessor.create();
        }

        public void await(Duration duration) {
            this.result.block(duration);
        }

        @MessageMapping("connect.echo")
        void echo(RSocketRequester requester) {
            runTest(() -> {
                Flux<String> flux = Flux.range(1, 3).concatMap(( i) -> requester.route("echo").data(("Hello " + i)).retrieveMono(.class));
                StepVerifier.create(flux).expectNext("Hello 1").expectNext("Hello 2").expectNext("Hello 3").verifyComplete();
            });
        }

        @MessageMapping("connect.echo-async")
        void echoAsync(RSocketRequester requester) {
            runTest(() -> {
                Flux<String> flux = Flux.range(1, 3).concatMap(( i) -> requester.route("echo-async").data(("Hello " + i)).retrieveMono(.class));
                StepVerifier.create(flux).expectNext("Hello 1 async").expectNext("Hello 2 async").expectNext("Hello 3 async").verifyComplete();
            });
        }

        @MessageMapping("connect.echo-stream")
        void echoStream(RSocketRequester requester) {
            runTest(() -> {
                Flux<String> flux = requester.route("echo-stream").data("Hello").retrieveFlux(String.class);
                StepVerifier.create(flux).expectNext("Hello 0").expectNextCount(5).expectNext("Hello 6").expectNext("Hello 7").thenCancel().verify();
            });
        }

        @MessageMapping("connect.echo-channel")
        void echoChannel(RSocketRequester requester) {
            runTest(() -> {
                Flux<String> flux = requester.route("echo-channel").data(Flux.range(1, 10).map(( i) -> "Hello " + i), String.class).retrieveFlux(String.class);
                StepVerifier.create(flux).expectNext("Hello 1 async").expectNextCount(7).expectNext("Hello 9 async").expectNext("Hello 10 async").verifyComplete();
            });
        }

        private void runTest(Runnable testEcho) {
            // StepVerifier will block
            Mono.fromRunnable(testEcho).doOnError(( ex) -> result.onError(ex)).doOnSuccess(( o) -> result.onComplete()).subscribeOn(Schedulers.elastic()).subscribe();
        }
    }

    private static class ClientHandler {
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
    }

    @Configuration
    static class RSocketConfig {
        @Bean
        public RSocketServerToClientIntegrationTests.ClientHandler clientHandler() {
            return new RSocketServerToClientIntegrationTests.ClientHandler();
        }

        @Bean
        public RSocketServerToClientIntegrationTests.ServerController serverController() {
            return new RSocketServerToClientIntegrationTests.ServerController();
        }

        @Bean
        public MessageHandlerAcceptor clientAcceptor() {
            MessageHandlerAcceptor acceptor = new MessageHandlerAcceptor();
            acceptor.setHandlers(Collections.singletonList(clientHandler()));
            acceptor.setAutoDetectDisabled();
            acceptor.setRSocketStrategies(rsocketStrategies());
            return acceptor;
        }

        @Bean
        public MessageHandlerAcceptor serverAcceptor() {
            MessageHandlerAcceptor handler = new MessageHandlerAcceptor();
            handler.setRSocketStrategies(rsocketStrategies());
            return handler;
        }

        @Bean
        public RSocketStrategies rsocketStrategies() {
            return RSocketStrategies.builder().decoder(StringDecoder.allMimeTypes()).encoder(CharSequenceEncoder.allMimeTypes()).dataBufferFactory(new org.springframework.core.io.buffer.NettyDataBufferFactory(PooledByteBufAllocator.DEFAULT)).build();
        }
    }
}

