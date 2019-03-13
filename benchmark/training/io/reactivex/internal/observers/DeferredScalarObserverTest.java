/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.internal.observers;


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.QueueDisposable;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.ObserverFusion;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DeferredScalarObserverTest {
    static final class TakeFirst extends DeferredScalarObserver<Integer, Integer> {
        private static final long serialVersionUID = -2793723002312330530L;

        TakeFirst(Observer<? super Integer> downstream) {
            super(downstream);
        }

        @Override
        public void onNext(Integer value) {
            upstream.dispose();
            complete(value);
            complete(value);
        }
    }

    @Test
    public void normal() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = new TestObserver<Integer>();
            DeferredScalarObserverTest.TakeFirst source = new DeferredScalarObserverTest.TakeFirst(to);
            source.onSubscribe(Disposables.empty());
            Disposable d = Disposables.empty();
            source.onSubscribe(d);
            Assert.assertTrue(d.isDisposed());
            source.onNext(1);
            to.assertResult(1);
            TestHelper.assertError(errors, 0, ProtocolViolationException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void error() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        DeferredScalarObserverTest.TakeFirst source = new DeferredScalarObserverTest.TakeFirst(to);
        source.onSubscribe(Disposables.empty());
        onError(new TestException());
        to.assertFailure(TestException.class);
    }

    @Test
    public void complete() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        DeferredScalarObserverTest.TakeFirst source = new DeferredScalarObserverTest.TakeFirst(to);
        source.onSubscribe(Disposables.empty());
        onComplete();
        to.assertResult();
    }

    @Test
    public void dispose() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        DeferredScalarObserverTest.TakeFirst source = new DeferredScalarObserverTest.TakeFirst(to);
        Disposable d = Disposables.empty();
        source.onSubscribe(d);
        Assert.assertFalse(d.isDisposed());
        to.cancel();
        Assert.assertTrue(d.isDisposed());
        Assert.assertTrue(isDisposed());
    }

    @Test
    public void fused() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = ObserverFusion.newTest(ANY);
            DeferredScalarObserverTest.TakeFirst source = new DeferredScalarObserverTest.TakeFirst(to);
            Disposable d = Disposables.empty();
            source.onSubscribe(d);
            to.assertOf(ObserverFusion.<Integer>assertFuseable());
            to.assertOf(ObserverFusion.<Integer>assertFusionMode(ASYNC));
            source.onNext(1);
            source.onNext(1);
            onError(new TestException());
            onComplete();
            Assert.assertTrue(d.isDisposed());
            to.assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void fusedReject() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = ObserverFusion.newTest(SYNC);
            DeferredScalarObserverTest.TakeFirst source = new DeferredScalarObserverTest.TakeFirst(to);
            Disposable d = Disposables.empty();
            source.onSubscribe(d);
            to.assertOf(ObserverFusion.<Integer>assertFuseable());
            to.assertOf(ObserverFusion.<Integer>assertFusionMode(NONE));
            source.onNext(1);
            source.onNext(1);
            onError(new TestException());
            onComplete();
            Assert.assertTrue(d.isDisposed());
            to.assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    static final class TakeLast extends DeferredScalarObserver<Integer, Integer> {
        private static final long serialVersionUID = -2793723002312330530L;

        TakeLast(Observer<? super Integer> downstream) {
            super(downstream);
        }

        @Override
        public void onNext(Integer value) {
            this.value = value;
        }
    }

    @Test
    public void nonfusedTerminateMore() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = ObserverFusion.newTest(NONE);
            DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(to);
            Disposable d = Disposables.empty();
            source.onSubscribe(d);
            source.onNext(1);
            onComplete();
            onComplete();
            onError(new TestException());
            to.assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void nonfusedError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = ObserverFusion.newTest(NONE);
            DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(to);
            Disposable d = Disposables.empty();
            source.onSubscribe(d);
            source.onNext(1);
            onError(new TestException());
            onError(new TestException("second"));
            onComplete();
            to.assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void fusedTerminateMore() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = ObserverFusion.newTest(ANY);
            DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(to);
            Disposable d = Disposables.empty();
            source.onSubscribe(d);
            source.onNext(1);
            onComplete();
            onComplete();
            onError(new TestException());
            to.assertResult(1);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void fusedError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestObserver<Integer> to = ObserverFusion.newTest(ANY);
            DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(to);
            Disposable d = Disposables.empty();
            source.onSubscribe(d);
            source.onNext(1);
            onError(new TestException());
            onError(new TestException("second"));
            onComplete();
            to.assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void disposed() {
        TestObserver<Integer> to = ObserverFusion.newTest(NONE);
        DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(to);
        Disposable d = Disposables.empty();
        source.onSubscribe(d);
        to.cancel();
        source.onNext(1);
        onComplete();
        to.assertNoValues().assertNoErrors().assertNotComplete();
    }

    @Test
    public void disposedAfterOnNext() {
        final TestObserver<Integer> to = new TestObserver<Integer>();
        DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(new Observer<Integer>() {
            io.reactivex.disposables.Disposable upstream;

            @Override
            public void onSubscribe(Disposable d) {
                this.upstream = d;
                to.onSubscribe(d);
            }

            @Override
            public void onNext(Integer value) {
                to.onNext(value);
                upstream.dispose();
            }

            @Override
            public void onError(Throwable e) {
                to.onError(e);
            }

            @Override
            public void onComplete() {
                to.onComplete();
            }
        });
        source.onSubscribe(Disposables.empty());
        source.onNext(1);
        onComplete();
        to.assertValue(1).assertNoErrors().assertNotComplete();
    }

    @Test
    public void fusedEmpty() {
        TestObserver<Integer> to = ObserverFusion.newTest(ANY);
        DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(to);
        Disposable d = Disposables.empty();
        source.onSubscribe(d);
        onComplete();
        to.assertResult();
    }

    @Test
    public void nonfusedEmpty() {
        TestObserver<Integer> to = ObserverFusion.newTest(NONE);
        DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(to);
        Disposable d = Disposables.empty();
        source.onSubscribe(d);
        onComplete();
        to.assertResult();
    }

    @Test
    public void customFusion() {
        final TestObserver<Integer> to = new TestObserver<Integer>();
        DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(new Observer<Integer>() {
            QueueDisposable<Integer> d;

            @SuppressWarnings("unchecked")
            @Override
            public void onSubscribe(Disposable d) {
                this.d = ((QueueDisposable<Integer>) (d));
                to.onSubscribe(d);
                this.d.requestFusion(ANY);
            }

            @Override
            public void onNext(Integer value) {
                if (!(d.isEmpty())) {
                    Integer v = null;
                    try {
                        to.onNext(d.poll());
                        v = d.poll();
                    } catch (Throwable ex) {
                        to.onError(ex);
                    }
                    Assert.assertNull(v);
                    Assert.assertTrue(d.isEmpty());
                }
            }

            @Override
            public void onError(Throwable e) {
                to.onError(e);
            }

            @Override
            public void onComplete() {
                to.onComplete();
            }
        });
        source.onSubscribe(Disposables.empty());
        source.onNext(1);
        onComplete();
        to.assertResult(1);
    }

    @Test
    public void customFusionClear() {
        final TestObserver<Integer> to = new TestObserver<Integer>();
        DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(new Observer<Integer>() {
            QueueDisposable<Integer> d;

            @SuppressWarnings("unchecked")
            @Override
            public void onSubscribe(Disposable d) {
                this.d = ((QueueDisposable<Integer>) (d));
                to.onSubscribe(d);
                this.d.requestFusion(ANY);
            }

            @Override
            public void onNext(Integer value) {
                d.clear();
                Assert.assertTrue(d.isEmpty());
            }

            @Override
            public void onError(Throwable e) {
                to.onError(e);
            }

            @Override
            public void onComplete() {
                to.onComplete();
            }
        });
        source.onSubscribe(Disposables.empty());
        source.onNext(1);
        onComplete();
        to.assertNoValues().assertNoErrors().assertComplete();
    }

    @Test
    public void offerThrow() {
        TestObserver<Integer> to = ObserverFusion.newTest(NONE);
        DeferredScalarObserverTest.TakeLast source = new DeferredScalarObserverTest.TakeLast(to);
        TestHelper.assertNoOffer(source);
    }

    @Test
    public void customFusionDontConsume() {
        final TestObserver<Integer> to = new TestObserver<Integer>();
        DeferredScalarObserverTest.TakeFirst source = new DeferredScalarObserverTest.TakeFirst(new Observer<Integer>() {
            QueueDisposable<Integer> d;

            @SuppressWarnings("unchecked")
            @Override
            public void onSubscribe(Disposable d) {
                this.d = ((QueueDisposable<Integer>) (d));
                to.onSubscribe(d);
                this.d.requestFusion(ANY);
            }

            @Override
            public void onNext(Integer value) {
                // not consuming
            }

            @Override
            public void onError(Throwable e) {
                to.onError(e);
            }

            @Override
            public void onComplete() {
                to.onComplete();
            }
        });
        source.onSubscribe(Disposables.empty());
        source.onNext(1);
        to.assertNoValues().assertNoErrors().assertComplete();
    }
}

