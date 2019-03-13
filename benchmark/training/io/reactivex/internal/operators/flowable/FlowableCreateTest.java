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
package io.reactivex.internal.operators.flowable;


import BackpressureStrategy.BUFFER;
import BackpressureStrategy.DROP;
import BackpressureStrategy.ERROR;
import BackpressureStrategy.LATEST;
import BackpressureStrategy.MISSING;
import FlowableCreate.BufferAsyncEmitter;
import FlowableCreate.DropAsyncEmitter;
import FlowableCreate.ErrorAsyncEmitter;
import FlowableCreate.LatestAsyncEmitter;
import FlowableCreate.MissingEmitter;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.functions.Cancellable;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.TestSubscriber;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class FlowableCreateTest {
    @Test(expected = NullPointerException.class)
    public void sourceNull() {
        Flowable.create(null, BUFFER);
    }

    @Test(expected = NullPointerException.class)
    public void modeNull() {
        Flowable.create(new FlowableOnSubscribe<Object>() {
            @Override
            public void subscribe(FlowableEmitter<Object> s) throws Exception {
            }
        }, null);
    }

    @Test
    public void basic() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Disposable d = Disposables.empty();
            Flowable.<Integer>create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    e.setDisposable(d);
                    e.onNext(1);
                    e.onNext(2);
                    e.onNext(3);
                    e.onComplete();
                    e.onError(new TestException("first"));
                    e.onNext(4);
                    e.onError(new TestException("second"));
                    e.onComplete();
                }
            }, BUFFER).test().assertResult(1, 2, 3);
            Assert.assertTrue(d.isDisposed());
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "first");
            TestHelper.assertUndeliverable(errors, 1, TestException.class, "second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void basicWithCancellable() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Disposable d1 = Disposables.empty();
            final Disposable d2 = Disposables.empty();
            Flowable.<Integer>create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    e.setDisposable(d1);
                    e.setCancellable(new Cancellable() {
                        @Override
                        public void cancel() throws Exception {
                            d2.dispose();
                        }
                    });
                    e.onNext(1);
                    e.onNext(2);
                    e.onNext(3);
                    e.onComplete();
                    e.onError(new TestException("first"));
                    e.onNext(4);
                    e.onError(new TestException("second"));
                    e.onComplete();
                }
            }, BUFFER).test().assertResult(1, 2, 3);
            Assert.assertTrue(d1.isDisposed());
            Assert.assertTrue(d2.isDisposed());
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "first");
            TestHelper.assertUndeliverable(errors, 1, TestException.class, "second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void basicWithError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Disposable d = Disposables.empty();
            Flowable.<Integer>create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    e.setDisposable(d);
                    e.onNext(1);
                    e.onNext(2);
                    e.onNext(3);
                    e.onError(new TestException());
                    e.onComplete();
                    e.onNext(4);
                    e.onError(new TestException("second"));
                }
            }, BUFFER).test().assertFailure(TestException.class, 1, 2, 3);
            Assert.assertTrue(d.isDisposed());
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void basicSerialized() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Disposable d = Disposables.empty();
            Flowable.<Integer>create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    e = e.serialize();
                    e.setDisposable(d);
                    e.onNext(1);
                    e.onNext(2);
                    e.onNext(3);
                    e.onComplete();
                    e.onError(new TestException("first"));
                    e.onNext(4);
                    e.onError(new TestException("second"));
                    e.onComplete();
                }
            }, BUFFER).test().assertResult(1, 2, 3);
            Assert.assertTrue(d.isDisposed());
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "first");
            TestHelper.assertUndeliverable(errors, 1, TestException.class, "second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void basicWithErrorSerialized() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Disposable d = Disposables.empty();
            Flowable.<Integer>create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    e = e.serialize();
                    e.setDisposable(d);
                    e.onNext(1);
                    e.onNext(2);
                    e.onNext(3);
                    e.onError(new TestException());
                    e.onComplete();
                    e.onNext(4);
                    e.onError(new TestException("second"));
                }
            }, BUFFER).test().assertFailure(TestException.class, 1, 2, 3);
            Assert.assertTrue(d.isDisposed());
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void wrap() {
        Flowable.fromPublisher(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onNext(4);
                subscriber.onNext(5);
                subscriber.onComplete();
            }
        }).test().assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void unsafe() {
        Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onNext(4);
                subscriber.onNext(5);
                subscriber.onComplete();
            }
        }).test().assertResult(1, 2, 3, 4, 5);
    }

    @Test(expected = IllegalArgumentException.class)
    public void unsafeWithFlowable() {
        Flowable.unsafeCreate(Flowable.just(1));
    }

    @Test
    public void createNullValueBuffer() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Throwable[] error = new Throwable[]{ null };
            Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    try {
                        e.onNext(null);
                        e.onNext(1);
                        e.onError(new TestException());
                        e.onComplete();
                    } catch (Throwable ex) {
                        error[0] = ex;
                    }
                }
            }, BUFFER).test().assertFailure(NullPointerException.class);
            Assert.assertNull(error[0]);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createNullValueLatest() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Throwable[] error = new Throwable[]{ null };
            Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    try {
                        e.onNext(null);
                        e.onNext(1);
                        e.onError(new TestException());
                        e.onComplete();
                    } catch (Throwable ex) {
                        error[0] = ex;
                    }
                }
            }, LATEST).test().assertFailure(NullPointerException.class);
            Assert.assertNull(error[0]);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createNullValueError() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Throwable[] error = new Throwable[]{ null };
            Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    try {
                        e.onNext(null);
                        e.onNext(1);
                        e.onError(new TestException());
                        e.onComplete();
                    } catch (Throwable ex) {
                        error[0] = ex;
                    }
                }
            }, ERROR).test().assertFailure(NullPointerException.class);
            Assert.assertNull(error[0]);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createNullValueDrop() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Throwable[] error = new Throwable[]{ null };
            Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    try {
                        e.onNext(null);
                        e.onNext(1);
                        e.onError(new TestException());
                        e.onComplete();
                    } catch (Throwable ex) {
                        error[0] = ex;
                    }
                }
            }, DROP).test().assertFailure(NullPointerException.class);
            Assert.assertNull(error[0]);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createNullValueMissing() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Throwable[] error = new Throwable[]{ null };
            Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    try {
                        e.onNext(null);
                        e.onNext(1);
                        e.onError(new TestException());
                        e.onComplete();
                    } catch (Throwable ex) {
                        error[0] = ex;
                    }
                }
            }, MISSING).test().assertFailure(NullPointerException.class);
            Assert.assertNull(error[0]);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createNullValueBufferSerialized() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Throwable[] error = new Throwable[]{ null };
            Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    e = e.serialize();
                    try {
                        e.onNext(null);
                        e.onNext(1);
                        e.onError(new TestException());
                        e.onComplete();
                    } catch (Throwable ex) {
                        error[0] = ex;
                    }
                }
            }, BUFFER).test().assertFailure(NullPointerException.class);
            Assert.assertNull(error[0]);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createNullValueLatestSerialized() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Throwable[] error = new Throwable[]{ null };
            Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    e = e.serialize();
                    try {
                        e.onNext(null);
                        e.onNext(1);
                        e.onError(new TestException());
                        e.onComplete();
                    } catch (Throwable ex) {
                        error[0] = ex;
                    }
                }
            }, LATEST).test().assertFailure(NullPointerException.class);
            Assert.assertNull(error[0]);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createNullValueErrorSerialized() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Throwable[] error = new Throwable[]{ null };
            Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    e = e.serialize();
                    try {
                        e.onNext(null);
                        e.onNext(1);
                        e.onError(new TestException());
                        e.onComplete();
                    } catch (Throwable ex) {
                        error[0] = ex;
                    }
                }
            }, ERROR).test().assertFailure(NullPointerException.class);
            Assert.assertNull(error[0]);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createNullValueDropSerialized() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Throwable[] error = new Throwable[]{ null };
            Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    e = e.serialize();
                    try {
                        e.onNext(null);
                        e.onNext(1);
                        e.onError(new TestException());
                        e.onComplete();
                    } catch (Throwable ex) {
                        error[0] = ex;
                    }
                }
            }, DROP).test().assertFailure(NullPointerException.class);
            Assert.assertNull(error[0]);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createNullValueMissingSerialized() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final Throwable[] error = new Throwable[]{ null };
            Flowable.create(new FlowableOnSubscribe<Integer>() {
                @Override
                public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                    e = e.serialize();
                    try {
                        e.onNext(null);
                        e.onNext(1);
                        e.onError(new TestException());
                        e.onComplete();
                    } catch (Throwable ex) {
                        error[0] = ex;
                    }
                }
            }, MISSING).test().assertFailure(NullPointerException.class);
            Assert.assertNull(error[0]);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onErrorRace() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            Flowable<Object> source = Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    final FlowableEmitter<Object> f = e.serialize();
                    final TestException ex = new TestException();
                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            f.onError(null);
                        }
                    };
                    Runnable r2 = new Runnable() {
                        @Override
                        public void run() {
                            f.onError(ex);
                        }
                    };
                    TestHelper.race(r1, r2);
                }
            }, m);
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                    source.test().assertFailure(Throwable.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
            Assert.assertFalse(errors.isEmpty());
        }
    }

    @Test
    public void onCompleteRace() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            Flowable<Object> source = Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    final FlowableEmitter<Object> f = e.serialize();
                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            f.onComplete();
                        }
                    };
                    Runnable r2 = new Runnable() {
                        @Override
                        public void run() {
                            f.onComplete();
                        }
                    };
                    TestHelper.race(r1, r2);
                }
            }, m);
            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                source.test().assertResult();
            }
        }
    }

    @Test
    public void nullValue() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    e.onNext(null);
                }
            }, m).test().assertFailure(NullPointerException.class);
        }
    }

    @Test
    public void nullThrowable() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            System.out.println(m);
            Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    e.onError(null);
                }
            }, m).test().assertFailure(NullPointerException.class);
        }
    }

    @Test
    public void serializedConcurrentOnNextOnError() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    final FlowableEmitter<Object> f = e.serialize();
                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 1000; i++) {
                                f.onNext(1);
                            }
                        }
                    };
                    Runnable r2 = new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 100; i++) {
                                f.onNext(1);
                            }
                            f.onError(new TestException());
                        }
                    };
                    TestHelper.race(r1, r2);
                }
            }, m).test().assertSubscribed().assertNotComplete().assertError(TestException.class);
        }
    }

    @Test
    public void callbackThrows() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    throw new TestException();
                }
            }, m).test().assertFailure(TestException.class);
        }
    }

    @Test
    public void nullValueSync() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    e.serialize().onNext(null);
                }
            }, m).test().assertFailure(NullPointerException.class);
        }
    }

    @Test
    public void createNullValue() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final Throwable[] error = new Throwable[]{ null };
                Flowable.create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                        try {
                            e.onNext(null);
                            e.onNext(1);
                            e.onError(new TestException());
                            e.onComplete();
                        } catch (Throwable ex) {
                            error[0] = ex;
                        }
                    }
                }, m).test().assertFailure(NullPointerException.class);
                Assert.assertNull(error[0]);
                TestHelper.assertUndeliverable(errors, 0, TestException.class);
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test(expected = NullPointerException.class)
    public void nullArgument() {
        Flowable.create(null, MISSING);
    }

    @Test
    public void onErrorCrash() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    Disposable d = Disposables.empty();
                    e.setDisposable(d);
                    try {
                        e.onError(new java.io.IOException());
                        Assert.fail("Should have thrown");
                    } catch (TestException ex) {
                        // expected
                    }
                    Assert.assertTrue(d.isDisposed());
                }
            }, m).subscribe(new FlowableSubscriber<Object>() {
                @Override
                public void onSubscribe(Subscription s) {
                }

                @Override
                public void onNext(Object value) {
                }

                @Override
                public void onError(Throwable e) {
                    throw new TestException();
                }

                @Override
                public void onComplete() {
                }
            });
        }
    }

    @Test
    public void onCompleteCrash() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    Disposable d = Disposables.empty();
                    e.setDisposable(d);
                    try {
                        e.onComplete();
                        Assert.fail("Should have thrown");
                    } catch (TestException ex) {
                        // expected
                    }
                    Assert.assertTrue(d.isDisposed());
                }
            }, m).subscribe(new FlowableSubscriber<Object>() {
                @Override
                public void onSubscribe(Subscription s) {
                }

                @Override
                public void onNext(Object value) {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                    throw new TestException();
                }
            });
        }
    }

    @Test
    public void createNullValueSerialized() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final Throwable[] error = new Throwable[]{ null };
                Flowable.create(new FlowableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                        e = e.serialize();
                        try {
                            e.onNext(null);
                            e.onNext(1);
                            e.onError(new TestException());
                            e.onComplete();
                        } catch (Throwable ex) {
                            error[0] = ex;
                        }
                    }
                }, m).test().assertFailure(NullPointerException.class);
                Assert.assertNull(error[0]);
                TestHelper.assertUndeliverable(errors, 0, TestException.class);
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void nullThrowableSync() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    e.serialize().onError(null);
                }
            }, m).test().assertFailure(NullPointerException.class);
        }
    }

    @Test
    public void serializedConcurrentOnNext() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    final FlowableEmitter<Object> f = e.serialize();
                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
                                f.onNext(1);
                            }
                        }
                    };
                    TestHelper.race(r1, r1);
                }
            }, m).take(TestHelper.RACE_DEFAULT_LOOPS).test().assertSubscribed().assertValueCount(TestHelper.RACE_DEFAULT_LOOPS).assertComplete().assertNoErrors();
        }
    }

    @Test
    public void serializedConcurrentOnNextOnComplete() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            TestSubscriber<Object> ts = Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> e) throws Exception {
                    final FlowableEmitter<Object> f = e.serialize();
                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 1000; i++) {
                                f.onNext(1);
                            }
                        }
                    };
                    Runnable r2 = new Runnable() {
                        @Override
                        public void run() {
                            for (int i = 0; i < 100; i++) {
                                f.onNext(1);
                            }
                            f.onComplete();
                        }
                    };
                    TestHelper.race(r1, r2);
                }
            }, m).test().assertSubscribed().assertComplete().assertNoErrors();
            int c = ts.valueCount();
            Assert.assertTrue(("" + c), (c >= 100));
        }
    }

    @Test
    public void serialized() {
        for (BackpressureStrategy m : BackpressureStrategy.values()) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                Flowable.create(new FlowableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(FlowableEmitter<Object> e) throws Exception {
                        FlowableEmitter<Object> f = e.serialize();
                        Assert.assertSame(f, f.serialize());
                        Assert.assertFalse(f.isCancelled());
                        final int[] calls = new int[]{ 0 };
                        f.setCancellable(new Cancellable() {
                            @Override
                            public void cancel() throws Exception {
                                (calls[0])++;
                            }
                        });
                        e.onComplete();
                        Assert.assertTrue(f.isCancelled());
                        Assert.assertEquals(1, calls[0]);
                    }
                }, m).test().assertResult();
                Assert.assertTrue(errors.toString(), errors.isEmpty());
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void tryOnError() {
        for (BackpressureStrategy strategy : BackpressureStrategy.values()) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final Boolean[] response = new Boolean[]{ null };
                Flowable.create(new FlowableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(FlowableEmitter<Object> e) throws Exception {
                        e.onNext(1);
                        response[0] = e.tryOnError(new TestException());
                    }
                }, strategy).take(1).test().withTag(strategy.toString()).assertResult(1);
                Assert.assertFalse(response[0]);
                Assert.assertTrue(((strategy + ": ") + (errors.toString())), errors.isEmpty());
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void tryOnErrorSerialized() {
        for (BackpressureStrategy strategy : BackpressureStrategy.values()) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final Boolean[] response = new Boolean[]{ null };
                Flowable.create(new FlowableOnSubscribe<Object>() {
                    @Override
                    public void subscribe(FlowableEmitter<Object> e) throws Exception {
                        e = e.serialize();
                        e.onNext(1);
                        response[0] = e.tryOnError(new TestException());
                    }
                }, strategy).take(1).test().withTag(strategy.toString()).assertResult(1);
                Assert.assertFalse(response[0]);
                Assert.assertTrue(((strategy + ": ") + (errors.toString())), errors.isEmpty());
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void emittersHasToString() {
        Map<BackpressureStrategy, Class<? extends FlowableEmitter>> emitterMap = new HashMap<BackpressureStrategy, Class<? extends FlowableEmitter>>();
        emitterMap.put(MISSING, MissingEmitter.class);
        emitterMap.put(ERROR, ErrorAsyncEmitter.class);
        emitterMap.put(DROP, DropAsyncEmitter.class);
        emitterMap.put(LATEST, LatestAsyncEmitter.class);
        emitterMap.put(BUFFER, BufferAsyncEmitter.class);
        for (final Map.Entry<BackpressureStrategy, Class<? extends FlowableEmitter>> entry : emitterMap.entrySet()) {
            Flowable.create(new FlowableOnSubscribe<Object>() {
                @Override
                public void subscribe(FlowableEmitter<Object> emitter) throws Exception {
                    Assert.assertTrue(emitter.toString().contains(entry.getValue().getSimpleName()));
                    Assert.assertTrue(emitter.serialize().toString().contains(entry.getValue().getSimpleName()));
                }
            }, entry.getKey()).test().assertEmpty();
        }
    }
}

