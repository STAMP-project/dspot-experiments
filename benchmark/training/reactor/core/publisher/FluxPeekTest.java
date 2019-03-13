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
import Scannable.Attr.PARENT;
import Scannable.Attr.TERMINATED;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Subscription;
import reactor.core.CoreSubscriber;
import reactor.core.Exceptions;
import reactor.core.Fuseable;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.publisher.FluxOperatorTest;
import reactor.test.subscriber.AssertSubscriber;
import reactor.util.concurrent.Queues;


public class FluxPeekTest extends FluxOperatorTest<String, String> {
    @Test(expected = NullPointerException.class)
    public void nullSource() {
        new FluxPeek(null, null, null, null, null, null, null, null);
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
        new FluxPeek(Flux.just(1).hide(), onSubscribe::set, onNext::set, onError::set, () -> onComplete.set(true), () -> onAfterComplete.set(true), onRequest::set, () -> onCancel.set(true)).subscribe(ts);
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
        new FluxPeek(new FluxError(new RuntimeException("forced failure")), onSubscribe::set, onNext::set, onError::set, () -> onComplete.set(true), () -> onAfterComplete.set(true), onRequest::set, () -> onCancel.set(true)).subscribe(ts);
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
        new FluxPeek(Flux.empty(), onSubscribe::set, onNext::set, onError::set, () -> onComplete.set(true), () -> onAfterComplete.set(true), onRequest::set, () -> onCancel.set(true)).subscribe(ts);
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
        new FluxPeek(Flux.never(), onSubscribe::set, onNext::set, onError::set, () -> onComplete.set(true), () -> onAfterComplete.set(true), onRequest::set, () -> onCancel.set(true)).subscribe(ts);
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
        new FluxPeek(Flux.never(), onSubscribe::set, onNext::set, onError::set, () -> onComplete.set(true), () -> onAfterComplete.set(true), onRequest::set, () -> onCancel.set(true)).subscribe(ts);
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
        Flux.just(1).hide().doOnNext(( d) -> {
            throw Exceptions.propagate(err);
        }).subscribe(ts);
        // nominal error path (DownstreamException)
        ts.assertErrorMessage("test");
        ts = AssertSubscriber.create();
        try {
            Flux.just(1).hide().doOnNext(( d) -> {
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
        Flux.just(1).hide().doOnComplete(() -> {
            throw Exceptions.propagate(err);
        }).subscribe(ts);
        // nominal error path (DownstreamException)
        ts.assertErrorMessage("test");
        ts = AssertSubscriber.create();
        try {
            Flux.just(1).hide().doOnComplete(() -> {
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
        FluxPeek<String> flux = new FluxPeek(Flux.error(new IllegalArgumentException("bar")), null, null, ( e) -> {
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
        Flux.just(1).hide().doOnNext(( d) -> {
            throw new RuntimeException();
        }).doOnError(( e) -> {
            throw Exceptions.propagate(err);
        }).subscribe(ts);
        // nominal error path (DownstreamException)
        ts.assertErrorMessage("test");
        ts = AssertSubscriber.create();
        try {
            Flux.just(1).hide().doOnNext(( d) -> {
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
        Mono.just(1).hide().publishOn(Schedulers.parallel()).doOnNext(( i) -> {
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
        FluxPeek<String> flux = new FluxPeek(Flux.empty(), null, null, errorCallbackCapture::set, null, () -> {
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
        Assert.assertThat(errorCallbackCapture.get()).isNull();
    }

    @Test
    public void afterTerminateCallbackFatalIsThrownDirectly() {
        AtomicReference<Throwable> errorCallbackCapture = new AtomicReference<>();
        Error fatal = new LinkageError();
        FluxPeek<String> flux = new FluxPeek(Flux.empty(), null, null, errorCallbackCapture::set, null, () -> {
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
        flux = new FluxPeek(Flux.error(new NullPointerException()), null, null, errorCallbackCapture::set, null, () -> {
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
        IllegalStateException err = new IllegalStateException("afterTerminate");
        IllegalArgumentException err2 = new IllegalArgumentException("error");
        FluxPeek<String> flux = new FluxPeek(Flux.empty(), null, null, ( e) -> {
            throw err2;
        }, null, () -> {
            throw err;
        }, null, null);
        AssertSubscriber<String> ts = AssertSubscriber.create();
        try {
            flux.subscribe(ts);
            Assert.fail("expected thrown exception");
        } catch (Exception e) {
            Assert.assertSame(err, e.getCause());
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
        FluxPeek<String> flux = new FluxPeek(Flux.error(err), null, null, ( e) -> {
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

    @Test
    public void syncCompleteCalled() {
        AtomicBoolean onComplete = new AtomicBoolean();
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(1, 2).hide().doOnComplete(() -> onComplete.set(true)).subscribe(ts);
        ts.assertNoError().assertValues(1, 2).assertComplete();
        Assert.assertTrue("onComplete not called back", onComplete.get());
    }

    @Test
    public void syncdoAfterTerminateCalled() {
        AtomicBoolean onTerminate = new AtomicBoolean();
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(1, 2).hide().doAfterTerminate(() -> onTerminate.set(true)).subscribe(ts);
        ts.assertNoError().assertValues(1, 2).assertComplete();
        Assert.assertTrue("onComplete not called back", onTerminate.get());
    }

    @Test
    public void syncdoOnTerminateCalled() {
        AtomicBoolean onTerminate = new AtomicBoolean();
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.range(1, 2).hide().doOnTerminate(() -> onTerminate.set(true)).subscribe(ts);
        ts.assertNoError().assertValues(1, 2).assertComplete();
        Assert.assertTrue("onComplete not called back", onTerminate.get());
    }

    @Test
    public void syncdoOnTerminateCalled2() {
        AtomicBoolean onTerminate = new AtomicBoolean();
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.empty().hide().doOnTerminate(() -> onTerminate.set(true)).subscribe(ts);
        ts.assertNoError().assertComplete();
        Assert.assertTrue("onComplete not called back", onTerminate.get());
    }

    @Test
    public void syncdoOnTerminateCalled3() {
        AtomicBoolean onTerminate = new AtomicBoolean();
        AssertSubscriber<Object> ts = AssertSubscriber.create();
        Flux.error(new Exception("test")).hide().doOnTerminate(() -> onTerminate.set(true)).subscribe(ts);
        ts.assertErrorMessage("test").assertNotComplete();
        Assert.assertTrue("onComplete not called back", onTerminate.get());
    }

    @Test
    public void should_reduce_to_10_events() {
        for (int i = 0; i < 20; i++) {
            AtomicInteger count = new AtomicInteger();
            Flux.range(0, 10).flatMap(( x) -> Flux.range(0, 2).map(( y) -> blockingOp(x, y)).subscribeOn(parallel()).reduce(( l, r) -> (l + "_") + r).hide().doOnSuccess(( s) -> {
                count.incrementAndGet();
            })).blockLast();
            Assert.assertEquals(10, count.get());
        }
    }

    @Test
    public void should_reduce_to_10_events_conditional() {
        for (int i = 0; i < 20; i++) {
            AtomicInteger count = new AtomicInteger();
            Flux.range(0, 10).flatMap(( x) -> Flux.range(0, 2).map(( y) -> blockingOp(x, y)).subscribeOn(parallel()).reduce(( l, r) -> (l + "_") + r).hide().doOnSuccess(( s) -> {
                count.incrementAndGet();
            }).filter(( v) -> true)).blockLast();
            Assert.assertEquals(10, count.get());
        }
    }

    static final class TestException extends Exception {}

    @Test
    public void doOnErrorPredicate() {
        AtomicReference<Throwable> ref = new AtomicReference<>();
        StepVerifier.create(Flux.error(new FluxPeekTest.TestException()).doOnError(FluxPeekTest.TestException.class::isInstance, ref::set)).thenAwait().then(() -> assertThat(ref.get()).isInstanceOf(.class)).verifyError(FluxPeekTest.TestException.class);
    }

    @Test
    public void doOnErrorPredicateNot() {
        AtomicReference<Throwable> ref = new AtomicReference<>();
        StepVerifier.create(Flux.error(new FluxPeekTest.TestException()).doOnError(RuntimeException.class::isInstance, ref::set)).thenAwait().then(() -> assertThat(ref.get()).isNull()).verifyError(FluxPeekTest.TestException.class);
    }

    @Test
    public void scanSubscriber() {
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
        FluxPeek.PeekSubscriber<Integer> test = new FluxPeek.PeekSubscriber<>(actual, peek);
        Subscription parent = Operators.emptySubscription();
        test.onSubscribe(parent);
        Assert.assertThat(test.scan(PARENT)).isSameAs(parent);
        Assert.assertThat(test.scan(ACTUAL)).isSameAs(actual);
        Assert.assertThat(test.scan(TERMINATED)).isFalse();
        test.onError(new IllegalStateException("boom"));
        Assert.assertThat(test.scan(TERMINATED)).isTrue();
    }

    @Test
    public void errorStrategyResumeDropsNext() {
        RuntimeException nextError = new IllegalStateException("next");
        List<Throwable> resumedErrors = new ArrayList<>();
        List<Object> resumedValues = new ArrayList<>();
        Flux<Integer> source = Flux.just(1, 2).hide();
        Flux<Integer> test = new FluxPeek(source, null, ( v) -> {
            throw nextError;
        }, null, null, null, null, null).hide().onErrorContinue(( t, s) -> {
            resumedErrors.add(t);
            resumedValues.add(s);
        });
        // classically dropped in all error modes:
        StepVerifier.create(test).expectNoFusionSupport().expectComplete().verifyThenAssertThat().hasNotDroppedElements().hasNotDroppedErrors();
        Assert.assertThat(resumedValues).containsExactly(1, 2);
        Assert.assertThat(resumedErrors).containsExactly(nextError, nextError);
    }
}

