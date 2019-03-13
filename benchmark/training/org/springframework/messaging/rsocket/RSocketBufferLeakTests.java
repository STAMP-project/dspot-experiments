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


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.ReferenceCounted;
import io.rsocket.AbstractRSocket;
import io.rsocket.RSocket;
import io.rsocket.plugins.RSocketInterceptor;
import io.rsocket.transport.netty.server.CloseableChannel;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.codec.CharSequenceEncoder;
import org.springframework.core.codec.StringDecoder;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ReplayProcessor;
import reactor.test.StepVerifier;


/**
 * Tests for scenarios that could lead to Payload and/or DataBuffer leaks.
 *
 * @author Rossen Stoyanchev
 */
public class RSocketBufferLeakTests {
    private static AnnotationConfigApplicationContext context;

    private static final RSocketBufferLeakTests.PayloadInterceptor payloadInterceptor = new RSocketBufferLeakTests.PayloadInterceptor();

    private static CloseableChannel server;

    private static RSocket client;

    private static RSocketRequester requester;

    @Test
    public void assemblyTimeErrorForHandleAndReply() {
        Mono<String> result = RSocketBufferLeakTests.requester.route("A.B").data("foo").retrieveMono(String.class);
        StepVerifier.create(result).expectErrorMatches(( ex) -> {
            String prefix = "Ambiguous handler methods mapped for destination 'A.B':";
            return ex.getMessage().startsWith(prefix);
        }).verify();
    }

    @Test
    public void subscriptionTimeErrorForHandleAndReply() {
        Mono<String> result = RSocketBufferLeakTests.requester.route("not-decodable").data("foo").retrieveMono(String.class);
        StepVerifier.create(result).expectErrorMatches(( ex) -> {
            String prefix = "Cannot decode to [org.springframework.core.io.Resource]";
            return ex.getMessage().contains(prefix);
        }).verify();
    }

    @Test
    public void errorSignalWithExceptionHandler() {
        Mono<String> result = RSocketBufferLeakTests.requester.route("error-signal").data("foo").retrieveMono(String.class);
        StepVerifier.create(result).expectNext("Handled 'bad input'").verifyComplete();
    }

    @Test
    public void ignoreInput() {
        Flux<String> result = RSocketBufferLeakTests.requester.route("ignore-input").data("a").retrieveFlux(String.class);
        StepVerifier.create(result).expectNext("bar").verifyComplete();
    }

    @Test
    public void retrieveMonoFromFluxResponderMethod() {
        Mono<String> result = RSocketBufferLeakTests.requester.route("request-stream").data("foo").retrieveMono(String.class);
        StepVerifier.create(result).expectNext("foo-1").verifyComplete();
    }

    @Controller
    static class ServerController {
        @MessageMapping("A.*")
        void ambiguousMatchA(String payload) {
            throw new IllegalStateException("Unexpected call");
        }

        @MessageMapping("*.B")
        void ambiguousMatchB(String payload) {
            throw new IllegalStateException("Unexpected call");
        }

        @MessageMapping("not-decodable")
        void notDecodable(@Payload
        Resource resource) {
            throw new IllegalStateException("Unexpected call");
        }

        @MessageMapping("error-signal")
        public Flux<String> errorSignal(String payload) {
            return Flux.error(new IllegalArgumentException("bad input")).delayElements(Duration.ofMillis(10)).cast(String.class);
        }

        @MessageExceptionHandler
        public String handleIllegalArgument(IllegalArgumentException ex) {
            return ("Handled '" + (ex.getMessage())) + "'";
        }

        @MessageMapping("ignore-input")
        Mono<String> ignoreInput() {
            return Mono.delay(Duration.ofMillis(10)).map(( l) -> "bar");
        }

        @MessageMapping("request-stream")
        Flux<String> stream(String payload) {
            return Flux.range(1, 100).delayElements(Duration.ofMillis(10)).map(( idx) -> (payload + "-") + idx);
        }
    }

    @Configuration
    static class ServerConfig {
        @Bean
        public RSocketBufferLeakTests.ServerController controller() {
            return new RSocketBufferLeakTests.ServerController();
        }

        @Bean
        public MessageHandlerAcceptor messageHandlerAcceptor() {
            MessageHandlerAcceptor acceptor = new MessageHandlerAcceptor();
            acceptor.setRSocketStrategies(rsocketStrategies());
            return acceptor;
        }

        @Bean
        public RSocketStrategies rsocketStrategies() {
            return RSocketStrategies.builder().decoder(StringDecoder.allMimeTypes()).encoder(CharSequenceEncoder.allMimeTypes()).dataBufferFactory(new RSocketBufferLeakTests.LeakAwareNettyDataBufferFactory(PooledByteBufAllocator.DEFAULT)).build();
        }
    }

    /**
     * Similar {@link org.springframework.core.io.buffer.LeakAwareDataBufferFactory}
     * but extends {@link NettyDataBufferFactory} rather than rely on
     * decoration, since {@link PayloadUtils} does instanceof checks.
     */
    private static class LeakAwareNettyDataBufferFactory extends NettyDataBufferFactory {
        private final List<RSocketBufferLeakTests.DataBufferLeakInfo> created = new ArrayList<>();

        LeakAwareNettyDataBufferFactory(ByteBufAllocator byteBufAllocator) {
            super(byteBufAllocator);
        }

        void checkForLeaks(Duration duration) throws InterruptedException {
            Instant start = Instant.now();
            while (true) {
                try {
                    this.created.forEach(( info) -> {
                        if (isAllocated()) {
                            throw info.getError();
                        }
                    });
                    break;
                } catch (AssertionError ex) {
                    if (Instant.now().isAfter(start.plus(duration))) {
                        throw ex;
                    }
                }
                Thread.sleep(50);
            } 
        }

        void reset() {
            this.created.clear();
        }

        @Override
        public NettyDataBuffer allocateBuffer() {
            return ((NettyDataBuffer) (record(super.allocateBuffer())));
        }

        @Override
        public NettyDataBuffer allocateBuffer(int initialCapacity) {
            return ((NettyDataBuffer) (record(super.allocateBuffer(initialCapacity))));
        }

        @Override
        public NettyDataBuffer wrap(ByteBuf byteBuf) {
            NettyDataBuffer dataBuffer = super.wrap(byteBuf);
            if (byteBuf != (Unpooled.EMPTY_BUFFER)) {
                record(dataBuffer);
            }
            return dataBuffer;
        }

        @Override
        public DataBuffer join(List<? extends DataBuffer> dataBuffers) {
            return record(super.join(dataBuffers));
        }

        private DataBuffer record(DataBuffer buffer) {
            this.created.add(new RSocketBufferLeakTests.DataBufferLeakInfo(buffer, new AssertionError(String.format("DataBuffer leak: {%s} {%s} not released.%nStacktrace at buffer creation: ", buffer, ObjectUtils.getIdentityHexString(getNativeBuffer())))));
            return buffer;
        }
    }

    private static class DataBufferLeakInfo {
        private final DataBuffer dataBuffer;

        private final AssertionError error;

        DataBufferLeakInfo(DataBuffer dataBuffer, AssertionError error) {
            this.dataBuffer = dataBuffer;
            this.error = error;
        }

        DataBuffer getDataBuffer() {
            return this.dataBuffer;
        }

        AssertionError getError() {
            return this.error;
        }
    }

    /**
     * Store all intercepted incoming and outgoing payloads and then use
     * {@link #checkForLeaks()} at the end to check reference counts.
     */
    private static class PayloadInterceptor extends AbstractRSocket implements RSocketInterceptor {
        private final List<RSocketBufferLeakTests.PayloadInterceptor.PayloadSavingDecorator> rsockets = new CopyOnWriteArrayList<>();

        void checkForLeaks() {
            this.rsockets.stream().map(RSocketBufferLeakTests.PayloadInterceptor.PayloadSavingDecorator::getPayloads).forEach(( payloadInfoProcessor) -> {
                payloadInfoProcessor.onComplete();
                payloadInfoProcessor.doOnNext(this::checkForLeak).blockLast();
            });
        }

        private void checkForLeak(RSocketBufferLeakTests.PayloadLeakInfo info) {
            Instant start = Instant.now();
            while (true) {
                try {
                    int count = info.getReferenceCount();
                    Assert.assertTrue(((("Leaked payload (refCnt=" + count) + "): ") + info), (count == 0));
                    break;
                } catch (AssertionError ex) {
                    if (Instant.now().isAfter(start.plus(Duration.ofSeconds(5)))) {
                        throw ex;
                    }
                }
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    // ignore
                }
            } 
        }

        public void reset() {
            this.rsockets.forEach(RSocketBufferLeakTests.PayloadInterceptor.PayloadSavingDecorator::reset);
        }

        @Override
        public RSocket apply(RSocket rsocket) {
            RSocketBufferLeakTests.PayloadInterceptor.PayloadSavingDecorator decorator = new RSocketBufferLeakTests.PayloadInterceptor.PayloadSavingDecorator(rsocket);
            this.rsockets.add(decorator);
            return decorator;
        }

        private static class PayloadSavingDecorator extends AbstractRSocket {
            private final RSocket delegate;

            private ReplayProcessor<RSocketBufferLeakTests.PayloadLeakInfo> payloads = ReplayProcessor.create();

            PayloadSavingDecorator(RSocket delegate) {
                this.delegate = delegate;
            }

            ReplayProcessor<RSocketBufferLeakTests.PayloadLeakInfo> getPayloads() {
                return this.payloads;
            }

            void reset() {
                this.payloads = ReplayProcessor.create();
            }

            @Override
            public Mono<Void> fireAndForget(io.rsocket.Payload payload) {
                return this.delegate.fireAndForget(addPayload(payload));
            }

            @Override
            public Mono<io.rsocket.Payload> requestResponse(io.rsocket.Payload payload) {
                return this.delegate.requestResponse(addPayload(payload)).doOnSuccess(this::addPayload);
            }

            @Override
            public Flux<io.rsocket.Payload> requestStream(io.rsocket.Payload payload) {
                return this.delegate.requestStream(addPayload(payload)).doOnNext(this::addPayload);
            }

            @Override
            public Flux<io.rsocket.Payload> requestChannel(Publisher<io.rsocket.Payload> payloads) {
                return this.delegate.requestChannel(Flux.from(payloads).doOnNext(this::addPayload)).doOnNext(this::addPayload);
            }

            private Payload addPayload(io.rsocket.Payload payload) {
                this.payloads.onNext(new RSocketBufferLeakTests.PayloadLeakInfo(payload));
                return payload;
            }

            @Override
            public Mono<Void> metadataPush(io.rsocket.Payload payload) {
                return this.delegate.metadataPush(addPayload(payload));
            }
        }
    }

    private static class PayloadLeakInfo {
        private final String description;

        private final ReferenceCounted referenceCounted;

        PayloadLeakInfo(io.rsocket.Payload payload) {
            this.description = payload.toString();
            this.referenceCounted = payload;
        }

        int getReferenceCount() {
            return this.referenceCounted.refCnt();
        }

        @Override
        public String toString() {
            return this.description;
        }
    }
}

