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
package org.springframework.core;


import io.reactivex.Flowable;
import io.reactivex.Maybe;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;
import rx.Completable;
import rx.Observable;
import rx.Single;


/**
 * Unit tests for {@link ReactiveAdapterRegistry}.
 *
 * @author Rossen Stoyanchev
 */
@SuppressWarnings("unchecked")
public class ReactiveAdapterRegistryTests {
    private final ReactiveAdapterRegistry registry = ReactiveAdapterRegistry.getSharedInstance();

    @Test
    public void defaultAdapterRegistrations() {
        // Reactor
        Assert.assertNotNull(getAdapter(Mono.class));
        Assert.assertNotNull(getAdapter(Flux.class));
        // Publisher
        Assert.assertNotNull(getAdapter(Publisher.class));
        // Completable
        Assert.assertNotNull(getAdapter(CompletableFuture.class));
        // RxJava 1
        Assert.assertNotNull(getAdapter(Observable.class));
        Assert.assertNotNull(getAdapter(Single.class));
        Assert.assertNotNull(getAdapter(Completable.class));
        // RxJava 2
        Assert.assertNotNull(getAdapter(Flowable.class));
        Assert.assertNotNull(getAdapter(Observable.class));
        Assert.assertNotNull(getAdapter(Single.class));
        Assert.assertNotNull(getAdapter(Maybe.class));
        Assert.assertNotNull(getAdapter(Completable.class));
    }

    @Test
    public void getAdapterForReactiveSubType() {
        ReactiveAdapter adapter1 = getAdapter(Flux.class);
        ReactiveAdapter adapter2 = getAdapter(FluxProcessor.class);
        Assert.assertSame(adapter1, adapter2);
        this.registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(FluxProcessor.class, FluxProcessor::empty), ( o) -> ((FluxProcessor<?, ?>) (o)), FluxProcessor::from);
        ReactiveAdapter adapter3 = getAdapter(FluxProcessor.class);
        Assert.assertNotNull(adapter3);
        Assert.assertNotSame(adapter1, adapter3);
    }

    @Test
    public void publisherToFlux() {
        List<Integer> sequence = Arrays.asList(1, 2, 3);
        Publisher<Integer> source = Flowable.fromIterable(sequence);
        Object target = getAdapter(Flux.class).fromPublisher(source);
        Assert.assertTrue((target instanceof Flux));
        Assert.assertEquals(sequence, ((Flux<Integer>) (target)).collectList().block(Duration.ofMillis(1000)));
    }

    // TODO: publisherToMono/CompletableFuture vs Single (ISE on multiple elements)?
    @Test
    public void publisherToMono() {
        Publisher<Integer> source = Flowable.fromArray(1, 2, 3);
        Object target = getAdapter(Mono.class).fromPublisher(source);
        Assert.assertTrue((target instanceof Mono));
        Assert.assertEquals(Integer.valueOf(1), ((Mono<Integer>) (target)).block(Duration.ofMillis(1000)));
    }

    @Test
    public void publisherToCompletableFuture() throws Exception {
        Publisher<Integer> source = Flowable.fromArray(1, 2, 3);
        Object target = getAdapter(CompletableFuture.class).fromPublisher(source);
        Assert.assertTrue((target instanceof CompletableFuture));
        Assert.assertEquals(Integer.valueOf(1), ((CompletableFuture<Integer>) (target)).get());
    }

    @Test
    public void publisherToRxObservable() {
        List<Integer> sequence = Arrays.asList(1, 2, 3);
        Publisher<Integer> source = Flowable.fromIterable(sequence);
        Object target = getAdapter(Observable.class).fromPublisher(source);
        Assert.assertTrue((target instanceof Observable));
        Assert.assertEquals(sequence, ((Observable<?>) (target)).toList().toBlocking().first());
    }

    @Test
    public void publisherToRxSingle() {
        Publisher<Integer> source = Flowable.fromArray(1);
        Object target = getAdapter(Single.class).fromPublisher(source);
        Assert.assertTrue((target instanceof Single));
        Assert.assertEquals(Integer.valueOf(1), ((Single<Integer>) (target)).toBlocking().value());
    }

    @Test
    public void publisherToRxCompletable() {
        Publisher<Integer> source = Flowable.fromArray(1, 2, 3);
        Object target = getAdapter(Completable.class).fromPublisher(source);
        Assert.assertTrue((target instanceof Completable));
        Assert.assertNull(get());
    }

    @Test
    public void publisherToReactivexFlowable() {
        List<Integer> sequence = Arrays.asList(1, 2, 3);
        Publisher<Integer> source = Flux.fromIterable(sequence);
        Object target = getAdapter(Flowable.class).fromPublisher(source);
        Assert.assertTrue((target instanceof Flowable));
        Assert.assertEquals(sequence, blockingGet());
    }

    @Test
    public void publisherToReactivexObservable() {
        List<Integer> sequence = Arrays.asList(1, 2, 3);
        Publisher<Integer> source = Flowable.fromIterable(sequence);
        Object target = getAdapter(Observable.class).fromPublisher(source);
        Assert.assertTrue((target instanceof io.reactivex.Observable));
        Assert.assertEquals(sequence, blockingGet());
    }

    @Test
    public void publisherToReactivexSingle() {
        Publisher<Integer> source = Flowable.fromArray(1);
        Object target = getAdapter(Single.class).fromPublisher(source);
        Assert.assertTrue((target instanceof io.reactivex.Single));
        Assert.assertEquals(Integer.valueOf(1), blockingGet());
    }

    @Test
    public void publisherToReactivexCompletable() {
        Publisher<Integer> source = Flowable.fromArray(1, 2, 3);
        Object target = getAdapter(Completable.class).fromPublisher(source);
        Assert.assertTrue((target instanceof io.reactivex.Completable));
        Assert.assertNull(blockingGet());
    }

    @Test
    public void rxObservableToPublisher() {
        List<Integer> sequence = Arrays.asList(1, 2, 3);
        Object source = Observable.from(sequence);
        Object target = getAdapter(Observable.class).toPublisher(source);
        Assert.assertTrue(("Expected Flux Publisher: " + (target.getClass().getName())), (target instanceof Flux));
        Assert.assertEquals(sequence, ((Flux<Integer>) (target)).collectList().block(Duration.ofMillis(1000)));
    }

    @Test
    public void rxSingleToPublisher() {
        Object source = Single.just(1);
        Object target = getAdapter(Single.class).toPublisher(source);
        Assert.assertTrue(("Expected Mono Publisher: " + (target.getClass().getName())), (target instanceof Mono));
        Assert.assertEquals(Integer.valueOf(1), ((Mono<Integer>) (target)).block(Duration.ofMillis(1000)));
    }

    @Test
    public void rxCompletableToPublisher() {
        Object source = Completable.complete();
        Object target = getAdapter(Completable.class).toPublisher(source);
        Assert.assertTrue(("Expected Mono Publisher: " + (target.getClass().getName())), (target instanceof Mono));
        ((Mono<Void>) (target)).block(Duration.ofMillis(1000));
    }

    @Test
    public void reactivexFlowableToPublisher() {
        List<Integer> sequence = Arrays.asList(1, 2, 3);
        Object source = Flowable.fromIterable(sequence);
        Object target = getAdapter(Flowable.class).toPublisher(source);
        Assert.assertTrue(("Expected Flux Publisher: " + (target.getClass().getName())), (target instanceof Flux));
        Assert.assertEquals(sequence, ((Flux<Integer>) (target)).collectList().block(Duration.ofMillis(1000)));
    }

    @Test
    public void reactivexObservableToPublisher() {
        List<Integer> sequence = Arrays.asList(1, 2, 3);
        Object source = io.reactivex.Observable.fromIterable(sequence);
        Object target = getAdapter(Observable.class).toPublisher(source);
        Assert.assertTrue(("Expected Flux Publisher: " + (target.getClass().getName())), (target instanceof Flux));
        Assert.assertEquals(sequence, ((Flux<Integer>) (target)).collectList().block(Duration.ofMillis(1000)));
    }

    @Test
    public void reactivexSingleToPublisher() {
        Object source = io.reactivex.Single.just(1);
        Object target = getAdapter(Single.class).toPublisher(source);
        Assert.assertTrue(("Expected Mono Publisher: " + (target.getClass().getName())), (target instanceof Mono));
        Assert.assertEquals(Integer.valueOf(1), ((Mono<Integer>) (target)).block(Duration.ofMillis(1000)));
    }

    @Test
    public void reactivexCompletableToPublisher() {
        Object source = io.reactivex.Completable.complete();
        Object target = getAdapter(Completable.class).toPublisher(source);
        Assert.assertTrue(("Expected Mono Publisher: " + (target.getClass().getName())), (target instanceof Mono));
        ((Mono<Void>) (target)).block(Duration.ofMillis(1000));
    }

    @Test
    public void CompletableFutureToPublisher() {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        future.complete(1);
        Object target = getAdapter(CompletableFuture.class).toPublisher(future);
        Assert.assertTrue(("Expected Mono Publisher: " + (target.getClass().getName())), (target instanceof Mono));
        Assert.assertEquals(Integer.valueOf(1), ((Mono<Integer>) (target)).block(Duration.ofMillis(1000)));
    }
}

