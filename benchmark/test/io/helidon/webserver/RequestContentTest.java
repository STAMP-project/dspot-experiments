/**
 * Copyright (c) 2017, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.webserver;


import io.helidon.common.http.DataChunk;
import io.helidon.common.reactive.Flow;
import io.helidon.common.reactive.ReactiveStreamsAdapter;
import io.helidon.common.reactive.SubmissionPublisher;
import io.helidon.media.common.ContentReaders;
import io.helidon.webserver.utils.TestUtils;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.AllOf;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsInstanceOf;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


/**
 * The RequestContentTest.
 */
public class RequestContentTest {
    @Test
    public void directSubscriptionTest() throws Exception {
        StringBuilder sb = new StringBuilder();
        Flux<DataChunk> flux = Flux.just("first", "second", "third").map(( s) -> DataChunk.create(s.getBytes()));
        Request request = RequestContentTest.requestTestStub(flux);
        ReactiveStreamsAdapter.publisherFromFlow(request.content()).subscribe(( chunk) -> sb.append(TestUtils.requestChunkAsString(chunk)).append("-"));
        MatcherAssert.assertThat(sb.toString(), Is.is("first-second-third-"));
    }

    @Test
    public void upperCaseFilterTest() throws Exception {
        StringBuilder sb = new StringBuilder();
        Flux<DataChunk> flux = Flux.just("first", "second", "third").map(( s) -> DataChunk.create(s.getBytes()));
        Request request = RequestContentTest.requestTestStub(flux);
        request.content().registerFilter(( publisher) -> {
            sb.append("apply_filter-");
            Flux<DataChunk> byteBufferFlux = ReactiveStreamsAdapter.publisherFromFlow(publisher);
            Flux<DataChunk> stringFlux = byteBufferFlux.map(TestUtils::requestChunkAsString).map(String::toUpperCase).map(( s) -> DataChunk.create(s.getBytes()));
            return ReactiveStreamsAdapter.publisherToFlow(stringFlux);
        });
        MatcherAssert.assertThat("Apply filter is expected to be called after a subscription!", sb.toString(), Is.is(""));
        ReactiveStreamsAdapter.publisherFromFlow(request.content()).subscribe(( chunk) -> sb.append(TestUtils.requestChunkAsString(chunk)).append("-"));
        MatcherAssert.assertThat(sb.toString(), Is.is("apply_filter-FIRST-SECOND-THIRD-"));
    }

    @Test
    public void multiThreadingFilterAndReaderTest() throws Exception {
        CountDownLatch subscribedLatch = new CountDownLatch(1);
        SubmissionPublisher<DataChunk> publisher = new SubmissionPublisher(Runnable::run, 10);
        ForkJoinPool.commonPool().submit(() -> {
            try {
                if (!(subscribedLatch.await(10, TimeUnit.SECONDS))) {
                    Assertions.fail("Subscriber didn't subscribe in timely manner!");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Interrupted!", e);
            }
            publisher.submit(DataChunk.create("first".getBytes()));
            publisher.submit(DataChunk.create("second".getBytes()));
            publisher.submit(DataChunk.create("third".getBytes()));
            publisher.close();
        });
        Request request = RequestContentTest.requestTestStub(ReactiveStreamsAdapter.publisherFromFlow(publisher));
        request.content().registerFilter(( originalPublisher) -> ( subscriberDelegate) -> originalPublisher.subscribe(new Flow.Subscriber<DataChunk>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscriberDelegate.onSubscribe(subscription);
                subscribedLatch.countDown();
            }

            @Override
            public void onNext(DataChunk item) {
                // mapping the on next call only
                subscriberDelegate.onNext(DataChunk.create(TestUtils.requestChunkAsString(item).toUpperCase().getBytes()));
            }

            @Override
            public void onError(Throwable throwable) {
                subscriberDelegate.onError(throwable);
            }

            @Override
            public void onComplete() {
                subscriberDelegate.onComplete();
            }
        }));
        request.content().registerReader(Iterable.class, ( publisher1, clazz) -> {
            fail("Iterable reader should have not been used!");
            throw new IllegalStateException("unreachable code");
        });
        request.content().registerReader(ArrayList.class, ( publisher1, clazz) -> {
            fail("ArrayList reader should have not been used!");
            throw new IllegalStateException("unreachable code");
        });
        request.content().registerReader(List.class, ( publisher1, clazz) -> {
            CompletableFuture<List<String>> future = new CompletableFuture<>();
            List<String> list = new CopyOnWriteArrayList<>();
            publisher1.subscribe(new Flow.Subscriber<DataChunk>() {
                @Override
                public void onSubscribe(Flow.Subscription subscription) {
                    subscription.request(Long.MAX_VALUE);
                    subscribedLatch.countDown();
                }

                @Override
                public void onNext(DataChunk item) {
                    list.add(TestUtils.requestChunkAsString(item));
                }

                @Override
                public void onError(Throwable throwable) {
                    fail(("Received an exception: " + (throwable.getMessage())));
                }

                @Override
                public void onComplete() {
                    future.complete(list);
                }
            });
            return future;
        });
        List result = request.content().as(List.class).toCompletableFuture().get(10, TimeUnit.SECONDS);
        MatcherAssert.assertThat(((List<String>) (result)), IsCollectionContaining.hasItems(Is.is("FIRST"), Is.is("SECOND"), Is.is("THIRD")));
    }

    @Test
    public void failingFilter() throws Exception {
        Request request = RequestContentTest.requestTestStub(Mono.never());
        request.content().registerFilter(( publisher) -> {
            throw new IllegalStateException("failed-publisher-transformation");
        });
        request.content().registerReader(Duration.class, ( publisher, clazz) -> {
            fail("Should not be called");
            throw new IllegalStateException("unreachable code");
        });
        java.util.concurrent.CompletableFuture<?> future = request.content().as(Duration.class).toCompletableFuture();
        try {
            future.get(10, TimeUnit.SECONDS);
            Assertions.fail("Should have thrown an exception");
        } catch (ExecutionException e) {
            MatcherAssert.assertThat(e.getCause(), AllOf.allOf(IsInstanceOf.instanceOf(IllegalArgumentException.class), hasProperty("message", StringContains.containsString("Transformation failed!"))));
            MatcherAssert.assertThat(e.getCause().getCause(), hasProperty("message", StringContains.containsString("failed-publisher-transformation")));
        }
    }

    @Test
    public void failingReader() throws Exception {
        Request request = RequestContentTest.requestTestStub(Mono.never());
        request.content().registerReader(Duration.class, ( publisher, clazz) -> {
            throw new IllegalStateException("failed-read");
        });
        java.util.concurrent.CompletableFuture<?> future = request.content().as(Duration.class).toCompletableFuture();
        try {
            future.get(10, TimeUnit.SECONDS);
            Assertions.fail("Should have thrown an exception");
        } catch (ExecutionException e) {
            MatcherAssert.assertThat(e.getCause(), AllOf.allOf(IsInstanceOf.instanceOf(IllegalArgumentException.class), hasProperty("message", StringContains.containsString("Transformation failed!"))));
            MatcherAssert.assertThat(e.getCause().getCause(), hasProperty("message", StringContains.containsString("failed-read")));
        }
    }

    @Test
    public void missingReaderTest() throws Exception {
        Request request = RequestContentTest.requestTestStub(Mono.just(DataChunk.create("hello".getBytes())));
        request.content().registerReader(LocalDate.class, ( publisher, clazz) -> {
            throw new IllegalStateException("Should not be called");
        });
        java.util.concurrent.CompletableFuture<?> future = request.content().as(Duration.class).toCompletableFuture();
        try {
            future.get(10, TimeUnit.SECONDS);
            Assertions.fail("Should have thrown an exception");
        } catch (ExecutionException e) {
            MatcherAssert.assertThat(e.getCause(), IsInstanceOf.instanceOf(IllegalArgumentException.class));
        }
    }

    @Test
    public void nullFilter() throws Exception {
        Request request = RequestContentTest.requestTestStub(Mono.never());
        Assertions.assertThrows(NullPointerException.class, () -> {
            request.content().registerFilter(null);
        });
    }

    @Test
    public void failingSubscribe() throws Exception {
        Request request = RequestContentTest.requestTestStub(Flux.just(DataChunk.create("data".getBytes())));
        request.content().registerFilter(( publisher) -> {
            throw new IllegalStateException("failed-publisher-transformation");
        });
        AtomicReference<Throwable> receivedThrowable = new AtomicReference<>();
        ReactiveStreamsAdapter.publisherFromFlow(request.content()).subscribe(( byteBuffer) -> {
            fail("Should not have been called!");
        }, receivedThrowable::set);
        Throwable throwable = receivedThrowable.get();
        MatcherAssert.assertThat(throwable, AllOf.allOf(IsInstanceOf.instanceOf(IllegalArgumentException.class), hasProperty("message", StringContains.containsString("Unexpected exception occurred during publishers chaining"))));
        MatcherAssert.assertThat(throwable.getCause(), hasProperty("message", StringContains.containsString("failed-publisher-transformation")));
    }

    @Test
    public void readerTest() throws Exception {
        Flux<DataChunk> flux = Flux.just("2010-01-02").map(( s) -> DataChunk.create(s.getBytes()));
        Request request = RequestContentTest.requestTestStub(flux);
        request.content().registerReader(LocalDate.class, ( publisher, clazz) -> ContentReaders.stringReader(Request.requestContentCharset(request)).apply(publisher).thenApply(LocalDate::parse));
        CompletionStage<String> complete = request.content().as(LocalDate.class).thenApply(( o) -> ((((o.getDayOfMonth()) + "/") + (o.getMonthValue())) + "/") + (o.getYear()));
        String result = complete.toCompletableFuture().get(10, TimeUnit.SECONDS);
        MatcherAssert.assertThat(result, Is.is("2/1/2010"));
    }

    @Test
    public void implicitByteArrayContentReader() throws Exception {
        Flux<DataChunk> flux = Flux.just("test-string").map(( s) -> DataChunk.create(s.getBytes()));
        Request request = RequestContentTest.requestTestStub(flux);
        CompletionStage<String> complete = request.content().as(byte[].class).thenApply(String::new);
        MatcherAssert.assertThat(complete.toCompletableFuture().get(10, TimeUnit.SECONDS), Is.is("test-string"));
    }

    @Test
    public void implicitStringContentReader() throws Exception {
        Flux<DataChunk> flux = Flux.just("test-string").map(( s) -> DataChunk.create(s.getBytes()));
        Request request = RequestContentTest.requestTestStub(flux);
        CompletionStage<? extends String> complete = request.content().as(String.class);
        MatcherAssert.assertThat(complete.toCompletableFuture().get(10, TimeUnit.SECONDS), Is.is("test-string"));
    }

    @Test
    public void overridingStringContentReader() throws Exception {
        Flux<DataChunk> flux = Flux.just("test-string").map(( s) -> DataChunk.create(s.getBytes()));
        Request request = RequestContentTest.requestTestStub(flux);
        request.content().registerReader(String.class, ( publisher, clazz) -> {
            fail("Should not be called");
            throw new IllegalStateException("unreachable code");
        });
        request.content().registerReader(String.class, ( publisher, clazz) -> {
            Flux<DataChunk> byteBufferFlux = ReactiveStreamsAdapter.publisherFromFlow(publisher);
            return byteBufferFlux.map(TestUtils::requestChunkAsString).map(String::toUpperCase).collect(Collectors.joining()).toFuture();
        });
        CompletionStage<? extends String> complete = request.content().as(String.class);
        MatcherAssert.assertThat(complete.toCompletableFuture().get(10, TimeUnit.SECONDS), Is.is("TEST-STRING"));
    }
}

