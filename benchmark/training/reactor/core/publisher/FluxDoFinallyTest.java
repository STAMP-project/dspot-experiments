/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core.publisher;


import Scannable.Attr.ACTUAL;
import Scannable.Attr.CANCELLED;
import Scannable.Attr.PARENT;
import Scannable.Attr.TERMINATED;
import SignalType.CANCEL;
import SignalType.ON_COMPLETE;
import SignalType.ON_ERROR;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;
import org.assertj.core.api.Assertions;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Exceptions;
import reactor.test.StepVerifier;


public class FluxDoFinallyTest implements Consumer<SignalType> {
    volatile SignalType signalType;

    volatile int calls;

    @Test
    public void normalJust() {
        StepVerifier.create(Flux.just(1).hide().doFinally(this)).expectNoFusionSupport().expectNext(1).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void normalEmpty() {
        StepVerifier.create(Flux.empty().doFinally(this)).expectNoFusionSupport().expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void normalError() {
        StepVerifier.create(Flux.error(new IllegalArgumentException()).doFinally(this)).expectNoFusionSupport().expectError(IllegalArgumentException.class).verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_ERROR, signalType);
    }

    @Test
    public void normalCancel() {
        StepVerifier.create(Flux.range(1, 10).hide().doFinally(this).take(5)).expectNoFusionSupport().expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(CANCEL, signalType);
    }

    @Test
    public void normalTake() {
        StepVerifier.create(Flux.range(1, 5).hide().doFinally(this)).expectNoFusionSupport().expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void syncFused() {
        StepVerifier.create(Flux.range(1, 5).doFinally(this)).expectFusion(SYNC).expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void syncFusedThreadBarrier() {
        StepVerifier.create(Flux.range(1, 5).doFinally(this)).expectFusion(((SYNC) | (THREAD_BARRIER)), NONE).expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void asyncFused() {
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        up.onNext(1);
        up.onNext(2);
        up.onNext(3);
        up.onNext(4);
        up.onNext(5);
        up.onComplete();
        StepVerifier.create(up.doFinally(this)).expectFusion(ASYNC).expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void asyncFusedThreadBarrier() {
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        up.onNext(1);
        up.onNext(2);
        up.onNext(3);
        up.onNext(4);
        up.onNext(5);
        up.onComplete();
        StepVerifier.create(up.doFinally(this)).expectFusion(((ASYNC) | (THREAD_BARRIER)), NONE).expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void normalJustConditional() {
        StepVerifier.create(Flux.just(1).hide().doFinally(this).filter(( i) -> true)).expectNoFusionSupport().expectNext(1).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void normalEmptyConditional() {
        StepVerifier.create(Flux.empty().hide().doFinally(this).filter(( i) -> true)).expectNoFusionSupport().expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void normalErrorConditional() {
        StepVerifier.create(Flux.error(new IllegalArgumentException()).hide().doFinally(this).filter(( i) -> true)).expectNoFusionSupport().expectError(IllegalArgumentException.class).verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_ERROR, signalType);
    }

    @Test
    public void normalCancelConditional() {
        StepVerifier.create(Flux.range(1, 10).hide().doFinally(this).filter(( i) -> true).take(5)).expectNoFusionSupport().expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(CANCEL, signalType);
    }

    @Test
    public void normalTakeConditional() {
        StepVerifier.create(Flux.range(1, 5).hide().doFinally(this).filter(( i) -> true)).expectNoFusionSupport().expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void syncFusedConditional() {
        StepVerifier.create(Flux.range(1, 5).doFinally(this).filter(( i) -> true)).expectFusion(SYNC).expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void syncFusedThreadBarrierConditional() {
        StepVerifier.create(Flux.range(1, 5).doFinally(this).filter(( i) -> true)).expectFusion(((SYNC) | (THREAD_BARRIER)), NONE).expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void asyncFusedConditional() {
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        up.onNext(1);
        up.onNext(2);
        up.onNext(3);
        up.onNext(4);
        up.onNext(5);
        up.onComplete();
        StepVerifier.create(up.doFinally(this).filter(( i) -> true)).expectFusion(ASYNC).expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test
    public void asyncFusedThreadBarrierConditional() {
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        up.onNext(1);
        up.onNext(2);
        up.onNext(3);
        up.onNext(4);
        up.onNext(5);
        up.onComplete();
        StepVerifier.create(up.doFinally(this).filter(( i) -> true)).expectFusion(((ASYNC) | (THREAD_BARRIER)), NONE).expectNext(1, 2, 3, 4, 5).expectComplete().verify();
        Assert.assertEquals(1, calls);
        Assert.assertEquals(ON_COMPLETE, signalType);
    }

    @Test(expected = NullPointerException.class)
    public void nullCallback() {
        Flux.just(1).doFinally(null);
    }

    @Test
    public void callbackThrows() {
        try {
            StepVerifier.create(Flux.just(1).doFinally(( signal) -> {
                throw new IllegalStateException();
            })).expectNext(1).expectComplete().verify();
        } catch (Throwable e) {
            Throwable _e = Exceptions.unwrap(e);
            Assert.assertNotSame(e, _e);
            Assert.assertThat(_e, CoreMatchers.is(CoreMatchers.instanceOf(IllegalStateException.class)));
        }
    }

    @Test
    public void callbackThrowsConditional() {
        try {
            StepVerifier.create(Flux.just(1).doFinally(( signal) -> {
                throw new IllegalStateException();
            }).filter(( i) -> true)).expectNext(1).expectComplete().verify();
        } catch (Throwable e) {
            Throwable _e = Exceptions.unwrap(e);
            Assert.assertNotSame(e, _e);
            Assert.assertThat(_e, CoreMatchers.is(CoreMatchers.instanceOf(IllegalStateException.class)));
        }
    }

    @Test
    public void severalInARowExecutedInReverseOrder() {
        Queue<String> finallyOrder = new ConcurrentLinkedDeque<>();
        Flux.just("b").hide().doFinally(( s) -> finallyOrder.offer("FIRST")).doFinally(( s) -> finallyOrder.offer("SECOND")).blockLast();
        Assertions.assertThat(finallyOrder).containsExactly("SECOND", "FIRST");
    }

    @Test
    public void scanSubscriber() {
        CoreSubscriber<String> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxDoFinally.DoFinallySubscriber<String> test = new FluxDoFinally.DoFinallySubscriber<>(actual, ( st) -> {
        });
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assertions.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assertions.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assertions.assertThat(test.scan(CANCELLED)).isFalse();
        Assertions.assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        Assertions.assertThat(test.scan(TERMINATED)).isTrue();
        Assertions.assertThat(test.scan(CANCELLED)).isTrue();
    }

    // TODO test multiple subscriptions?
    // see https://github.com/reactor/reactor-core/issues/951
    @Test
    public void gh951_withoutConsumerInSubscribe() {
        List<String> events = new ArrayList<>();
        Mono.just(true).map(this::throwError).doOnError(( e) -> events.add("doOnError")).doFinally(( any) -> events.add(("doFinally " + (any.toString())))).subscribe();
        Assertions.assertThat(events).as("subscribe without consumer: map_doOnError_doFinally").containsExactly("doOnError", "doFinally onError");
        events.clear();
        Mono.just(true).doFinally(( any) -> events.add(("doFinally " + (any.toString())))).map(this::throwError).doOnError(( e) -> events.add("doOnError")).subscribe();
        Assertions.assertThat(events).as("subscribe without consumer: doFinally_map_doOnError").containsExactly("doFinally cancel", "doOnError");
        events.clear();
        Mono.just(true).map(this::throwError).doFinally(( any) -> events.add(("doFinally " + (any.toString())))).doOnError(( e) -> events.add("doOnError")).subscribe();
        Assertions.assertThat(events).as("subscribe without consumer:  map_doFinally_doOnError").containsExactly("doOnError", "doFinally onError");
    }

    // see https://github.com/reactor/reactor-core/issues/951
    @Test
    public void gh951_withConsumerInSubscribe() {
        List<String> events = new ArrayList<>();
        Mono.just(true).map(this::throwError).doOnError(( e) -> events.add("doOnError")).doFinally(( any) -> events.add(("doFinally " + (any.toString())))).subscribe(( v) -> {
        }, ( e) -> {
        });
        Assertions.assertThat(events).as("subscribe with consumer: map_doOnError_doFinally").containsExactly("doOnError", "doFinally onError");
        events.clear();
        Mono.just(true).doFinally(( any) -> events.add(("doFinally " + (any.toString())))).map(this::throwError).doOnError(( e) -> events.add("doOnError")).subscribe(( v) -> {
        }, ( e) -> {
        });
        Assertions.assertThat(events).as("subscribe with consumer: doFinally_map_doOnError").containsExactly("doFinally cancel", "doOnError");
        events.clear();
        Mono.just(true).map(this::throwError).doFinally(( any) -> events.add(("doFinally " + (any.toString())))).doOnError(( e) -> events.add("doOnError")).subscribe(( v) -> {
        }, ( e) -> {
        });
        Assertions.assertThat(events).as("subscribe with consumer: map_doFinally_doOnError").containsExactly("doOnError", "doFinally onError");
    }

    // see https://github.com/reactor/reactor-core/issues/951
    @Test
    public void gh951_withoutDoOnError() {
        List<String> events = new ArrayList<>();
        Assertions.assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(Mono.just(true).map(this::throwError).doFinally(( any) -> events.add(("doFinally " + (any.toString()))))::subscribe).withMessage("java.lang.IllegalStateException: boom");
        Assertions.assertThat(events).as("withoutDoOnError").containsExactly("doFinally onError");
    }
}

