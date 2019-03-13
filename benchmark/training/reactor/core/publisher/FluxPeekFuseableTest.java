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


import OnNextFailureStrategy.KEY_ON_NEXT_ERROR_STRATEGY;
import Scannable.Attr.ACTUAL;
import Scannable.Attr.PARENT;
import Scannable.Attr.TERMINATED;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.LongConsumer;
import javax.annotation.Nullable;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Exceptions;
import reactor.core.Fuseable;
import reactor.core.publisher.FluxPeekFuseable.PeekConditionalSubscriber;
import reactor.core.publisher.FluxPeekFuseable.PeekFuseableConditionalSubscriber;
import reactor.core.publisher.FluxPeekFuseable.PeekFuseableSubscriber;
import reactor.core.scheduler.Schedulers;
import reactor.test.MockUtils;
import reactor.test.StepVerifier;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.concurrent.Queues;
import reactor.util.context.Context;


public class FluxPeekFuseableTest {
    @Test(expected = NullPointerException.class)
    public void nullSource() {
        new FluxPeekFuseable(null, null, null, null, null, null, null, null);
    }

    @Test
    public void normal() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        AtomicReference<Subscription> onSubscribe = new AtomicReference<>();
        AtomicReference<Integer> onNext = new AtomicReference<>();
        AtomicReference<Throwable> onError = new AtomicReference<>();
        AtomicBoolean onComplete = new AtomicBoolean();
        AtomicLong onRequest = new AtomicLong();
        AtomicBoolean onAfterComplete = new AtomicBoolean();
        AtomicBoolean onCancel = new AtomicBoolean();
        new FluxPeekFuseable(Flux.just(1), onSubscribe::set, onNext::set, onError::set, () -> onComplete.set(true), () -> onAfterComplete.set(true), onRequest::set, () -> onCancel.set(true)).subscribe(ts);
        Assert.assertNotNull(onSubscribe.get());
        Assert.assertEquals(((Integer) (1)), onNext.get());
        Assert.assertNull(onError.get());
        Assert.assertTrue(onComplete.get());
        Assert.assertTrue(onAfterComplete.get());
        Assert.assertEquals(Long.MAX_VALUE, onRequest.get());
        Assert.assertFalse(onCancel.get());
    }

    @Test
    public void error() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        AtomicReference<Subscription> onSubscribe = new AtomicReference<>();
        AtomicReference<Integer> onNext = new AtomicReference<>();
        AtomicReference<Throwable> onError = new AtomicReference<>();
        AtomicBoolean onComplete = new AtomicBoolean();
        AtomicLong onRequest = new AtomicLong();
        AtomicBoolean onAfterComplete = new AtomicBoolean();
        AtomicBoolean onCancel = new AtomicBoolean();
        new FluxPeekFuseable(Flux.error(new RuntimeException("forced failure")), onSubscribe::set, onNext::set, onError::set, () -> onComplete.set(true), () -> onAfterComplete.set(true), onRequest::set, () -> onCancel.set(true)).subscribe(ts);
        Assert.assertNotNull(onSubscribe.get());
        Assert.assertNull(onNext.get());
        Assert.assertTrue(((onError.get()) instanceof RuntimeException));
        Assert.assertFalse(onComplete.get());
        Assert.assertTrue(onAfterComplete.get());
        Assert.assertEquals(Long.MAX_VALUE, onRequest.get());
        Assert.assertFalse(onCancel.get());
    }

    @Test
    public void empty() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        AtomicReference<Subscription> onSubscribe = new AtomicReference<>();
        AtomicReference<Integer> onNext = new AtomicReference<>();
        AtomicReference<Throwable> onError = new AtomicReference<>();
        AtomicBoolean onComplete = new AtomicBoolean();
        AtomicLong onRequest = new AtomicLong();
        AtomicBoolean onAfterComplete = new AtomicBoolean();
        AtomicBoolean onCancel = new AtomicBoolean();
        new FluxPeekFuseable(Flux.empty(), onSubscribe::set, onNext::set, onError::set, () -> onComplete.set(true), () -> onAfterComplete.set(true), onRequest::set, () -> onCancel.set(true)).subscribe(ts);
        Assert.assertNotNull(onSubscribe.get());
        Assert.assertNull(onNext.get());
        Assert.assertNull(onError.get());
        Assert.assertTrue(onComplete.get());
        Assert.assertTrue(onAfterComplete.get());
        Assert.assertEquals(Long.MAX_VALUE, onRequest.get());
        Assert.assertFalse(onCancel.get());
    }

    @Test
    public void never() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        AtomicReference<Subscription> onSubscribe = new AtomicReference<>();
        AtomicReference<Integer> onNext = new AtomicReference<>();
        AtomicReference<Throwable> onError = new AtomicReference<>();
        AtomicBoolean onComplete = new AtomicBoolean();
        AtomicLong onRequest = new AtomicLong();
        AtomicBoolean onAfterComplete = new AtomicBoolean();
        AtomicBoolean onCancel = new AtomicBoolean();
        new FluxPeekFuseable(Flux.never(), onSubscribe::set, onNext::set, onError::set, () -> onComplete.set(true), () -> onAfterComplete.set(true), onRequest::set, () -> onCancel.set(true)).subscribe(ts);
        Assert.assertNotNull(onSubscribe.get());
        Assert.assertNull(onNext.get());
        Assert.assertNull(onError.get());
        Assert.assertFalse(onComplete.get());
        Assert.assertFalse(onAfterComplete.get());
        Assert.assertEquals(Long.MAX_VALUE, onRequest.get());
        Assert.assertFalse(onCancel.get());
    }

    @Test
    public void neverCancel() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        AtomicReference<Subscription> onSubscribe = new AtomicReference<>();
        AtomicReference<Integer> onNext = new AtomicReference<>();
        AtomicReference<Throwable> onError = new AtomicReference<>();
        AtomicBoolean onComplete = new AtomicBoolean();
        AtomicLong onRequest = new AtomicLong();
        AtomicBoolean onAfterComplete = new AtomicBoolean();
        AtomicBoolean onCancel = new AtomicBoolean();
        new FluxPeekFuseable(Flux.never(), onSubscribe::set, onNext::set, onError::set, () -> onComplete.set(true), () -> onAfterComplete.set(true), onRequest::set, () -> onCancel.set(true)).subscribe(ts);
        Assert.assertNotNull(onSubscribe.get());
        Assert.assertNull(onNext.get());
        Assert.assertNull(onError.get());
        Assert.assertFalse(onComplete.get());
        Assert.assertFalse(onAfterComplete.get());
        Assert.assertEquals(Long.MAX_VALUE, onRequest.get());
        Assert.assertFalse(onCancel.get());
        ts.cancel();
        Assert.assertTrue(onCancel.get());
    }

    @Test
    public void callbackError() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Throwable err = new Exception("test");
        Flux.just(1).doOnNext(( d) -> {
            throw Exceptions.propagate(err);
        }).subscribe(ts);
        // nominal error path (DownstreamException)
        ts.assertErrorMessage("test");
        ts = AssertSubscriber.create();
        try {
            Flux.just(1).doOnNext(( d) -> {
                throw Exceptions.bubble(err);
            }).subscribe(ts);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(((Exceptions.unwrap(e)) == err));
        }
    }

    @Test
    public void completeCallbackError() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Throwable err = new Exception("test");
        Flux.just(1).doOnComplete(() -> {
            throw Exceptions.propagate(err);
        }).subscribe(ts);
        // nominal error path (DownstreamException)
        ts.assertErrorMessage("test");
        ts = AssertSubscriber.create();
        try {
            Flux.just(1).doOnComplete(() -> {
                throw Exceptions.bubble(err);
            }).subscribe(ts);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(((Exceptions.unwrap(e)) == err));
        }
    }

    @Test
    public void errorCallbackError() {
        IllegalStateException err = new IllegalStateException("test");
        FluxPeekFuseable<String> flux = new FluxPeekFuseable(Flux.error(new IllegalArgumentException("bar")), null, null, ( e) -> {
            throw err;
        }, null, null, null, null);
        AssertSubscriber<String> ts = AssertSubscriber.create();
        flux.subscribe(ts);
        ts.assertNoValues();
        ts.assertError(IllegalStateException.class);
        ts.assertErrorWith(( e) -> e.getSuppressed()[0].getMessage().equals("bar"));
    }

    // See https://github.com/reactor/reactor-core/issues/272
    @Test
    public void errorCallbackError2() {
        // test with alternate / wrapped error types
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Throwable err = new Exception("test");
        Flux.just(1).doOnNext(( d) -> {
            throw new RuntimeException();
        }).doOnError(( e) -> {
            throw Exceptions.propagate(err);
        }).subscribe(ts);
        // nominal error path (DownstreamException)
        ts.assertErrorMessage("test");
        ts = AssertSubscriber.create();
        try {
            Flux.just(1).doOnNext(( d) -> {
                throw new RuntimeException();
            }).doOnError(( d) -> {
                throw Exceptions.bubble(err);
            }).subscribe(ts);
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(((Exceptions.unwrap(e)) == err));
        }
    }

    // See https://github.com/reactor/reactor-core/issues/253
    @Test
    public void errorCallbackErrorWithParallel() {
        AssertSubscriber<Integer> assertSubscriber = new AssertSubscriber<>();
        Mono.just(1).publishOn(Schedulers.parallel()).doOnNext(( i) -> {
            throw new IllegalArgumentException();
        }).doOnError(( e) -> {
            throw new IllegalStateException(e);
        }).subscribe(assertSubscriber);
        assertSubscriber.await().assertError(IllegalStateException.class).assertNotComplete();
    }

    @Test
    public void afterTerminateCallbackErrorDoesNotInvokeOnError() {
        IllegalStateException err = new IllegalStateException("test");
        AtomicReference<Throwable> errorCallbackCapture = new AtomicReference<>();
        FluxPeekFuseable<String> flux = new FluxPeekFuseable(Flux.empty(), null, null, errorCallbackCapture::set, null, () -> {
            throw err;
        }, null, null);
        AssertSubscriber<String> ts = AssertSubscriber.create();
        try {
            flux.subscribe(ts);
            Assert.fail("expected thrown exception");
        } catch (Exception e) {
            Assert.assertThat(e).hasCause(err);
        }
        ts.assertNoValues();
        ts.assertComplete();
        // the onError wasn't invoked:
        Assert.assertThat(errorCallbackCapture.get()).isNull();
    }

    @Test
    public void afterTerminateCallbackFatalIsThrownDirectly() {
        AtomicReference<Throwable> errorCallbackCapture = new AtomicReference<>();
        Error fatal = new LinkageError();
        FluxPeekFuseable<String> flux = new FluxPeekFuseable(Flux.empty(), null, null, errorCallbackCapture::set, null, () -> {
            throw fatal;
        }, null, null);
        AssertSubscriber<String> ts = AssertSubscriber.create();
        try {
            flux.subscribe(ts);
            Assert.fail("expected thrown exception");
        } catch (Throwable e) {
            Assert.assertSame(fatal, e);
        }
        ts.assertNoValues();
        ts.assertComplete();
        Assert.assertThat(errorCallbackCapture.get(), CoreMatchers.is(CoreMatchers.nullValue()));
        // same with after error
        errorCallbackCapture.set(null);
        flux = new FluxPeekFuseable(Flux.error(new NullPointerException()), null, null, errorCallbackCapture::set, null, () -> {
            throw fatal;
        }, null, null);
        ts = AssertSubscriber.create();
        try {
            flux.subscribe(ts);
            Assert.fail("expected thrown exception");
        } catch (Throwable e) {
            Assert.assertSame(fatal, e);
        }
        ts.assertNoValues();
        ts.assertError(NullPointerException.class);
        Assert.assertThat(errorCallbackCapture.get(), CoreMatchers.is(CoreMatchers.instanceOf(NullPointerException.class)));
    }

    @Test
    public void afterTerminateCallbackErrorAndErrorCallbackError() {
        IllegalStateException err = new IllegalStateException("expected afterTerminate");
        IllegalArgumentException err2 = new IllegalArgumentException("error");
        FluxPeekFuseable<String> flux = new FluxPeekFuseable(Flux.empty(), null, null, ( e) -> {
            throw err2;
        }, null, () -> {
            throw err;
        }, null, null);
        AssertSubscriber<String> ts = AssertSubscriber.create();
        try {
            flux.subscribe(ts);
            Assert.fail("expected thrown exception");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertSame(e.toString(), err, e.getCause());
            Assert.assertEquals(0, err2.getSuppressed().length);
            // err2 is never thrown
        }
        ts.assertNoValues();
        ts.assertComplete();
    }

    @Test
    public void afterTerminateCallbackErrorAndErrorCallbackError2() {
        IllegalStateException afterTerminate = new IllegalStateException("afterTerminate");
        IllegalArgumentException error = new IllegalArgumentException("error");
        NullPointerException err = new NullPointerException();
        FluxPeekFuseable<String> flux = new FluxPeekFuseable(Flux.error(err), null, null, ( e) -> {
            throw error;
        }, null, () -> {
            throw afterTerminate;
        }, null, null);
        AssertSubscriber<String> ts = AssertSubscriber.create();
        try {
            flux.subscribe(ts);
            Assert.fail("expected thrown exception");
        } catch (Exception e) {
            Assert.assertSame(afterTerminate, e.getCause());
            // afterTerminate suppressed error which itself suppressed original err
            Assert.assertEquals(1, afterTerminate.getSuppressed().length);
            Assert.assertEquals(error, afterTerminate.getSuppressed()[0]);
            Assert.assertEquals(1, error.getSuppressed().length);
            Assert.assertEquals(err, error.getSuppressed()[0]);
        }
        ts.assertNoValues();
        // the subscriber still sees the 'error' message since actual.onError is called before the afterTerminate callback
        ts.assertErrorMessage("error");
    }

    @Test
    public void syncFusionAvailable() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        Flux.range(1, 2).doOnNext(( v) -> {
        }).subscribe(ts);
        Subscription s = ts.upstream();
        Assert.assertTrue(("Non-fuseable upstream: " + s), (s instanceof Fuseable.QueueSubscription));
    }

    @Test
    public void asyncFusionAvailable() {
        AssertSubscriber<Integer> ts = AssertSubscriber.create();
        UnicastProcessor.create(Queues.<Integer>get(2).get()).doOnNext(( v) -> {
        }).subscribe(ts);
        Subscription s = ts.upstream();
        Assert.assertTrue(("Non-fuseable upstream" + s), (s instanceof Fuseable.QueueSubscription));
    }

    @Test
    public void conditionalFusionAvailable() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.from(( u) -> {
            if (!(u instanceof Fuseable.ConditionalSubscriber)) {
                Operators.error(u, new IllegalArgumentException(("The subscriber is not conditional: " + u)));
            } else {
                Operators.complete(u);
            }
        }).doOnNext(( v) -> {
        }).filter(( v) -> true).subscribe(ts);
        ts.assertNoError().assertNoValues().assertComplete();
    }

    @Test
    public void conditionalFusionAvailableWithFuseable() {
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.wrap(( u) -> {
            if (!(u instanceof Fuseable.ConditionalSubscriber)) {
                Operators.error(u, new IllegalArgumentException(("The subscriber is not conditional: " + u)));
            } else {
                Operators.complete(u);
            }
        }).doOnNext(( v) -> {
        }).filter(( v) -> true).subscribe(ts);
        ts.assertNoError().assertNoValues().assertComplete();
    }

    // TODO was these 2 tests supposed to trigger sync fusion and go through poll() ?
    @Test
    public void noFusionCompleteCalled() {
        AtomicBoolean onComplete = new AtomicBoolean();
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(1, 2).doOnComplete(() -> onComplete.set(true)).subscribe(ts);
        ts.assertNoError().assertValues(1, 2).assertComplete();
        Assert.assertTrue("onComplete not called back", onComplete.get());
    }

    @Test
    public void noFusionAfterTerminateCalled() {
        AtomicBoolean onTerminate = new AtomicBoolean();
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(1, 2).doAfterTerminate(() -> onTerminate.set(true)).subscribe(ts);
        ts.assertNoError().assertValues(1, 2).assertComplete();
        Assert.assertTrue("onComplete not called back", onTerminate.get());
    }

    @Test
    public void syncPollCompleteCalled() {
        AtomicBoolean onComplete = new AtomicBoolean();
        ConnectableFlux<Integer> f = Flux.just(1).doOnComplete(() -> onComplete.set(true)).publish();
        StepVerifier.create(f).then(f::connect).expectNext(1).verifyComplete();
        Assert.assertThat(onComplete.get()).withFailMessage("onComplete not called back").isTrue();
    }

    @Test
    public void syncPollConditionalCompleteCalled() {
        AtomicBoolean onComplete = new AtomicBoolean();
        ConnectableFlux<Integer> f = Flux.just(1).doOnComplete(() -> onComplete.set(true)).filter(( v) -> true).publish();
        StepVerifier.create(f).then(f::connect).expectNext(1).verifyComplete();
        Assert.assertThat(onComplete.get()).withFailMessage("onComplete not called back").isTrue();
    }

    @Test
    public void syncPollAfterTerminateCalled() {
        AtomicBoolean onAfterTerminate = new AtomicBoolean();
        ConnectableFlux<Integer> f = Flux.just(1).doAfterTerminate(() -> onAfterTerminate.set(true)).publish();
        StepVerifier.create(f).then(f::connect).expectNext(1).verifyComplete();
        Assert.assertThat(onAfterTerminate.get()).withFailMessage("onAfterTerminate not called back").isTrue();
    }

    @Test
    public void syncPollConditionalAfterTerminateCalled() {
        AtomicBoolean onAfterTerminate = new AtomicBoolean();
        ConnectableFlux<Integer> f = Flux.just(1).doAfterTerminate(() -> onAfterTerminate.set(true)).filter(( v) -> true).publish();
        StepVerifier.create(f).then(f::connect).expectNext(1).verifyComplete();
        Assert.assertThat(onAfterTerminate.get()).withFailMessage("onAfterTerminate not called back").isTrue();
    }

    @Test
    public void fusedDoOnNextOnErrorBothFailing() {
        ConnectableFlux<Integer> f = Flux.just(1).doOnNext(( i) -> {
            throw new IllegalArgumentException("fromOnNext");
        }).doOnError(( e) -> {
            throw new IllegalStateException("fromOnError", e);
        }).publish();
        StepVerifier.create(f).then(f::connect).verifyErrorMatches(( e) -> (((e instanceof IllegalStateException) && ("fromOnError".equals(e.getMessage()))) && ((e.getCause()) instanceof IllegalArgumentException)) && ("fromOnNext".equals(e.getCause().getMessage())));
    }

    @Test
    public void fusedDoOnNextOnErrorDoOnErrorAllFailing() {
        ConnectableFlux<Integer> f = Flux.just(1).doOnNext(( i) -> {
            throw new IllegalArgumentException("fromOnNext");
        }).doOnError(( e) -> {
            throw new IllegalStateException("fromOnError", e);
        }).doOnError(( e) -> {
            throw new IllegalStateException("fromOnError2", e);
        }).publish();
        StepVerifier.create(f).then(f::connect).verifyErrorSatisfies(( e) -> {
            assertThat(e).isInstanceOf(.class).hasMessage("fromOnError2").hasCauseInstanceOf(.class);
            assertThat(e.getCause()).hasMessage("fromOnError").hasCauseInstanceOf(.class);
            assertThat(e.getCause().getCause()).hasMessage("fromOnNext");
        });
    }

    @Test
    public void fusedDoOnNextCallsOnErrorWhenFailing() {
        AtomicBoolean passedOnError = new AtomicBoolean();
        ConnectableFlux<Integer> f = Flux.just(1).doOnNext(( i) -> {
            throw new IllegalArgumentException("fromOnNext");
        }).doOnError(( e) -> passedOnError.set(true)).publish();
        StepVerifier.create(f).then(f::connect).verifyErrorMatches(( e) -> (e instanceof IllegalArgumentException) && ("fromOnNext".equals(e.getMessage())));
        Assert.assertThat(passedOnError.get()).isTrue();
    }

    @Test
    public void conditionalFusedDoOnNextOnErrorBothFailing() {
        ConnectableFlux<Integer> f = Flux.just(1).doOnNext(( i) -> {
            throw new IllegalArgumentException("fromOnNext");
        }).doOnError(( e) -> {
            throw new IllegalStateException("fromOnError", e);
        }).filter(( v) -> true).publish();
        StepVerifier.create(f).then(f::connect).verifyErrorMatches(( e) -> (((e instanceof IllegalStateException) && ("fromOnError".equals(e.getMessage()))) && ((e.getCause()) instanceof IllegalArgumentException)) && ("fromOnNext".equals(e.getCause().getMessage())));
    }

    @Test
    public void conditionalFusedDoOnNextOnErrorDoOnErrorAllFailing() {
        ConnectableFlux<Integer> f = Flux.just(1).doOnNext(( i) -> {
            throw new IllegalArgumentException("fromOnNext");
        }).doOnError(( e) -> {
            throw new IllegalStateException("fromOnError", e);
        }).doOnError(( e) -> {
            throw new IllegalStateException("fromOnError2", e);
        }).filter(( v) -> true).publish();
        StepVerifier.create(f).then(f::connect).verifyErrorSatisfies(( e) -> {
            assertThat(e).isInstanceOf(.class).hasMessage("fromOnError2").hasCauseInstanceOf(.class);
            assertThat(e.getCause()).hasMessage("fromOnError").hasCauseInstanceOf(.class);
            assertThat(e.getCause().getCause()).hasMessage("fromOnNext");
        });
    }

    @Test
    public void conditionalFusedDoOnNextCallsOnErrorWhenFailing() {
        AtomicBoolean passedOnError = new AtomicBoolean();
        ConnectableFlux<Integer> f = Flux.just(1).doOnNext(( i) -> {
            throw new IllegalArgumentException("fromOnNext");
        }).doOnError(( e) -> passedOnError.set(true)).filter(( v) -> true).publish();
        StepVerifier.create(f).then(f::connect).verifyErrorMatches(( e) -> (e instanceof IllegalArgumentException) && ("fromOnNext".equals(e.getMessage())));
        Assert.assertThat(passedOnError.get()).isTrue();
    }

    @Test
    public void should_reduce_to_10_events() {
        for (int i = 0; i < 20; i++) {
            int n = i;
            List<Integer> rs = Collections.synchronizedList(new ArrayList<>());
            AtomicInteger count = new AtomicInteger();
            Flux.range(0, 10).flatMap(( x) -> Flux.range(0, 2).doOnNext(rs::add).map(( y) -> blockingOp(x, y)).subscribeOn(Schedulers.parallel()).reduce(( l, r) -> ((((((l + "_") + r) + " (") + x) + ", it:") + n) + ")")).doOnNext(( s) -> {
                count.incrementAndGet();
            }).blockLast();
            Assert.assertEquals(10, count.get());
        }
    }

    @Test
    public void should_reduce_to_10_events_conditional() {
        for (int i = 0; i < 20; i++) {
            AtomicInteger count = new AtomicInteger();
            Flux.range(0, 10).flatMap(( x) -> Flux.range(0, 2).map(( y) -> blockingOp(x, y)).subscribeOn(Schedulers.parallel()).reduce(( l, r) -> (l + "_") + r).doOnSuccess(( s) -> {
                count.incrementAndGet();
            }).filter(( v) -> true)).blockLast();
            Assert.assertEquals(10, count.get());
        }
    }

    @Test
    public void scanFuseableSubscriber() {
        CoreSubscriber<Integer> actual = new LambdaSubscriber(null, ( e) -> {
        }, null, null);
        FluxPeek<Integer> peek = new FluxPeek(Flux.just(1), ( s) -> {
        }, ( s) -> {
        }, ( e) -> {
        }, () -> {
        }, () -> {
        }, ( r) -> {
        }, () -> {
        });
        PeekFuseableSubscriber<Integer> test = new PeekFuseableSubscriber(actual, peek);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assert.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assert.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assert.assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        Assert.assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void scanFuseableConditionalSubscriber() {
        @SuppressWarnings("unchecked")
        Fuseable.ConditionalSubscriber<Integer> actual = Mockito.mock(MockUtils.TestScannableConditionalSubscriber.class);
        FluxPeek<Integer> peek = new FluxPeek(Flux.just(1), ( s) -> {
        }, ( s) -> {
        }, ( e) -> {
        }, () -> {
        }, () -> {
        }, ( r) -> {
        }, () -> {
        });
        PeekFuseableConditionalSubscriber<Integer> test = new PeekFuseableConditionalSubscriber(actual, peek);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assert.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assert.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assert.assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        Assert.assertThat(test.scan(TERMINATED)).isTrue();
    }

    static final class SignalPeekThrowNext<T> implements SignalPeek<T> {
        private final RuntimeException exception;

        SignalPeekThrowNext(RuntimeException exception) {
            this.exception = exception;
        }

        @Override
        public Consumer<? super T> onNextCall() {
            return ( t) -> {
                throw exception;
            };
        }

        @Nullable
        @Override
        public Consumer<? super Subscription> onSubscribeCall() {
            return null;
        }

        @Nullable
        @Override
        public Consumer<? super Throwable> onErrorCall() {
            return null;
        }

        @Nullable
        @Override
        public Runnable onCompleteCall() {
            return null;
        }

        @Nullable
        @Override
        public Runnable onAfterTerminateCall() {
            return null;
        }

        @Nullable
        @Override
        public LongConsumer onRequestCall() {
            return null;
        }

        @Nullable
        @Override
        public Runnable onCancelCall() {
            return null;
        }

        @Nullable
        @Override
        public Object scanUnsafe(Attr key) {
            return null;
        }
    }

    static class ConditionalAssertSubscriber<T> implements Fuseable.ConditionalSubscriber<T> {
        List<T> next = new ArrayList<>();

        boolean subscribed;

        Throwable error;

        boolean completed;

        private final Context context;

        ConditionalAssertSubscriber(Context context) {
            this.context = context;
        }

        ConditionalAssertSubscriber() {
            this(Context.empty());
        }

        @Override
        public boolean tryOnNext(T v) {
            next.add(v);
            return true;
        }

        @Override
        public void onSubscribe(Subscription s) {
            subscribed = true;
        }

        @Override
        public void onNext(T v) {
            next.add(v);
        }

        @Override
        public void onError(Throwable throwable) {
            error = throwable;
        }

        @Override
        public void onComplete() {
            completed = true;
        }

        @Override
        public Context currentContext() {
            return context;
        }
    }

    static class AssertQueueSubscription<T> implements Fuseable.QueueSubscription<T> {
        boolean isCancelled;

        int requested;

        boolean completeWithError;

        private Queue<T> q = Queues.<T>small().get();

        public void setCompleteWithError(boolean completeWithError) {
            this.completeWithError = completeWithError;
        }

        @Override
        public int requestFusion(int requestedMode) {
            return requestedMode;
        }

        @Override
        public boolean add(T t) {
            return q.add(t);
        }

        @Override
        public boolean offer(T t) {
            return q.offer(t);
        }

        @Override
        public T remove() {
            return q.remove();
        }

        @Override
        public T poll() {
            T value = q.poll();
            if ((value == null) && (completeWithError)) {
                throw new IllegalStateException("AssertQueueSubscriber poll error");
            }
            return value;
        }

        @Override
        public T element() {
            return q.element();
        }

        @Nullable
        @Override
        public T peek() {
            return q.peek();
        }

        @Override
        public int size() {
            return q.size();
        }

        @Override
        public boolean isEmpty() {
            return q.isEmpty();
        }

        @Override
        public void clear() {
            q.clear();
        }

        @Override
        public void request(long l) {
            (requested)++;
        }

        @Override
        public void cancel() {
            isCancelled = true;
        }
    }

    @Test
    public void resumeConditional() {
        RuntimeException nextError = new IllegalStateException("next");
        List<Throwable> resumedErrors = new ArrayList<>();
        List<Object> resumedValues = new ArrayList<>();
        Context context = Context.of(KEY_ON_NEXT_ERROR_STRATEGY, OnNextFailureStrategy.resume(( t, s) -> {
            resumedErrors.add(t);
            resumedValues.add(s);
        }));
        FluxPeekFuseableTest.ConditionalAssertSubscriber<Integer> actual = new FluxPeekFuseableTest.ConditionalAssertSubscriber(context);
        FluxPeekFuseableTest.SignalPeekThrowNext<Integer> peekParent = new FluxPeekFuseableTest.SignalPeekThrowNext<>(nextError);
        FluxPeekFuseableTest.AssertQueueSubscription<Integer> qs = new FluxPeekFuseableTest.AssertQueueSubscription<>();
        PeekConditionalSubscriber<Integer> test = new PeekConditionalSubscriber(actual, peekParent);
        test.onSubscribe(qs);
        test.onNext(1);
        Assert.assertThat(actual.next).as("onNext skips").isEmpty();
        Assert.assertThat(qs.requested).as("onNext requested more").isEqualTo(1);
        boolean tryOnNext = test.tryOnNext(2);
        Assert.assertThat(tryOnNext).as("tryOnNext skips").isFalse();
        test.onComplete();
        Assert.assertThat(actual.error).isNull();
        Assert.assertThat(actual.completed).isTrue();
        Assert.assertThat(resumedErrors).containsExactly(nextError, nextError);
        Assert.assertThat(resumedValues).containsExactly(1, 2);
    }

    @Test
    public void resumeFuseable() {
        RuntimeException nextError = new IllegalStateException("next");
        List<Throwable> resumedErrors = new ArrayList<>();
        List<Object> resumedValues = new ArrayList<>();
        Context context = Context.of(KEY_ON_NEXT_ERROR_STRATEGY, OnNextFailureStrategy.resume(( t, s) -> {
            resumedErrors.add(t);
            resumedValues.add(s);
        }));
        AssertSubscriber<Integer> actual = new AssertSubscriber(context, 0);
        FluxPeekFuseableTest.SignalPeekThrowNext<Integer> peekParent = new FluxPeekFuseableTest.SignalPeekThrowNext<>(nextError);
        FluxPeekFuseableTest.AssertQueueSubscription<Integer> qs = new FluxPeekFuseableTest.AssertQueueSubscription<>();
        PeekFuseableSubscriber<Integer> test = new PeekFuseableSubscriber(actual, peekParent);
        test.onSubscribe(qs);
        test.onNext(1);
        actual.assertNoValues();
        Assert.assertThat(qs.requested).as("onNext requested more").isEqualTo(1);
        qs.offer(3);
        Integer polled = test.poll();
        Assert.assertThat(polled).as("poll skips").isNull();
        test.onComplete();
        actual.assertNoValues();
        actual.assertNoError();
        actual.assertComplete();
        Assert.assertThat(resumedErrors).containsExactly(nextError, nextError);
        Assert.assertThat(resumedValues).containsExactly(1, 3);
    }

    @Test
    public void resumeFuseableConditional() {
        RuntimeException nextError = new IllegalStateException("next");
        List<Throwable> resumedErrors = new ArrayList<>();
        List<Object> resumedValues = new ArrayList<>();
        Context context = Context.of(KEY_ON_NEXT_ERROR_STRATEGY, OnNextFailureStrategy.resume(( t, s) -> {
            resumedErrors.add(t);
            resumedValues.add(s);
        }));
        FluxPeekFuseableTest.ConditionalAssertSubscriber<Integer> actual = new FluxPeekFuseableTest.ConditionalAssertSubscriber(context);
        FluxPeekFuseableTest.SignalPeekThrowNext<Integer> peekParent = new FluxPeekFuseableTest.SignalPeekThrowNext<>(nextError);
        FluxPeekFuseableTest.AssertQueueSubscription<Integer> qs = new FluxPeekFuseableTest.AssertQueueSubscription<>();
        PeekFuseableConditionalSubscriber<Integer> test = new PeekFuseableConditionalSubscriber(actual, peekParent);
        test.onSubscribe(qs);
        test.onNext(1);
        Assert.assertThat(actual.next).as("onNext skips").isEmpty();
        Assert.assertThat(qs.requested).as("onNext requested more").isEqualTo(1);
        boolean tryOnNext = test.tryOnNext(2);
        Assert.assertThat(tryOnNext).as("tryOnNext skips").isFalse();
        qs.offer(3);
        Integer polled = test.poll();
        Assert.assertThat(polled).as("poll skips").isNull();
        test.onComplete();
        Assert.assertThat(actual.error).isNull();
        Assert.assertThat(actual.completed).isTrue();
        Assert.assertThat(resumedErrors).containsExactly(nextError, nextError, nextError);
        Assert.assertThat(resumedValues).containsExactly(1, 2, 3);
    }
}

