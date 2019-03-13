/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.plugins;


import ImmediateThinScheduler.INSTANCE;
import io.reactivex.TestHelper;
import io.reactivex.disposables.RxJavaPlugins;
import io.reactivex.exceptions.TestException;
import io.reactivex.flowables.ConnectableFlowable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.operators.completable.CompletableError;
import io.reactivex.internal.operators.flowable.FlowableRange;
import io.reactivex.internal.operators.maybe.MaybeError;
import io.reactivex.internal.operators.observable.ObservableRange;
import io.reactivex.internal.schedulers.ImmediateThinScheduler;
import io.reactivex.observables.ConnectableObservable;
import io.reactivex.parallel.ParallelFlowable;
import io.reactivex.schedulers.Schedulers;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class RxJavaPluginsTest {
    // static Observable<Integer> createObservable() {
    // return Observable.range(1, 5).map(new Function<Integer, Integer>() {
    // @Override
    // public Integer apply(Integer t) {
    // throw new TestException();
    // }
    // });
    // }
    // 
    // static Flowable<Integer> createFlowable() {
    // return Flowable.range(1, 5).map(new Function<Integer, Integer>() {
    // @Override
    // public Integer apply(Integer t) {
    // throw new TestException();
    // }
    // });
    // }
    @Test
    public void constructorShouldBePrivate() {
        TestHelper.checkUtilityClass(RxJavaPlugins.class);
    }

    @SuppressWarnings({ "rawtypes" })
    @Test
    public void lockdown() throws Exception {
        RxJavaPlugins.reset();
        RxJavaPlugins.lockdown();
        try {
            Assert.assertTrue(RxJavaPlugins.isLockdown());
            Consumer a1 = Functions.emptyConsumer();
            Callable f0 = new Callable() {
                @Override
                public Object call() {
                    return null;
                }
            };
            Function f1 = Functions.identity();
            BiFunction f2 = new BiFunction() {
                @Override
                public Object apply(Object t1, Object t2) {
                    return t2;
                }
            };
            BooleanSupplier bs = new BooleanSupplier() {
                @Override
                public boolean getAsBoolean() throws Exception {
                    return true;
                }
            };
            for (Method m : RxJavaPlugins.class.getMethods()) {
                if (m.getName().startsWith("set")) {
                    Method getter;
                    Class<?> paramType = m.getParameterTypes()[0];
                    if (paramType == (Boolean.TYPE)) {
                        getter = RxJavaPlugins.class.getMethod(("is" + (m.getName().substring(3))));
                    } else {
                        getter = RxJavaPlugins.class.getMethod(("get" + (m.getName().substring(3))));
                    }
                    Object before = getter.invoke(null);
                    try {
                        if (paramType.isAssignableFrom(Boolean.TYPE)) {
                            m.invoke(null, true);
                        } else
                            if (paramType.isAssignableFrom(Callable.class)) {
                                m.invoke(null, f0);
                            } else
                                if (paramType.isAssignableFrom(io.reactivex.Function.class)) {
                                    m.invoke(null, f1);
                                } else
                                    if (paramType.isAssignableFrom(io.reactivex.Consumer.class)) {
                                        m.invoke(null, a1);
                                    } else
                                        if (paramType.isAssignableFrom(BooleanSupplier.class)) {
                                            m.invoke(null, bs);
                                        } else {
                                            m.invoke(null, f2);
                                        }




                        Assert.fail("Should have thrown InvocationTargetException(IllegalStateException)");
                    } catch (InvocationTargetException ex) {
                        if ((ex.getCause()) instanceof IllegalStateException) {
                            Assert.assertEquals("Plugins can't be changed anymore", ex.getCause().getMessage());
                        } else {
                            Assert.fail("Should have thrown InvocationTargetException(IllegalStateException)");
                        }
                    }
                    Object after = getter.invoke(null);
                    if (paramType.isPrimitive()) {
                        Assert.assertEquals(m.toString(), before, after);
                    } else {
                        Assert.assertSame(m.toString(), before, after);
                    }
                }
            }
            // Object o1 = RxJavaPlugins.getOnObservableCreate();
            // Object o2 = RxJavaPlugins.getOnSingleCreate();
            // Object o3 = RxJavaPlugins.getOnCompletableCreate();
            // 
            // RxJavaPlugins.enableAssemblyTracking();
            // RxJavaPlugins.clearAssemblyTracking();
            // RxJavaPlugins.resetAssemblyTracking();
            // 
            // 
            // assertSame(o1, RxJavaPlugins.getOnObservableCreate());
            // assertSame(o2, RxJavaPlugins.getOnSingleCreate());
            // assertSame(o3, RxJavaPlugins.getOnCompletableCreate());
        } finally {
            RxJavaPlugins.unlock();
            RxJavaPlugins.reset();
            Assert.assertFalse(RxJavaPlugins.isLockdown());
        }
    }

    io.reactivex.Function<Scheduler, Scheduler> replaceWithImmediate = new Function<Scheduler, Scheduler>() {
        @Override
        public io.reactivex.Scheduler apply(Scheduler t) {
            return ImmediateThinScheduler.INSTANCE;
        }
    };

    @Test
    public void overrideSingleScheduler() {
        try {
            RxJavaPlugins.setSingleSchedulerHandler(replaceWithImmediate);
            Assert.assertSame(INSTANCE, Schedulers.single());
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertNotSame(INSTANCE, Schedulers.single());
    }

    @Test
    public void overrideComputationScheduler() {
        try {
            RxJavaPlugins.setComputationSchedulerHandler(replaceWithImmediate);
            Assert.assertSame(INSTANCE, Schedulers.computation());
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertNotSame(INSTANCE, Schedulers.computation());
    }

    @Test
    public void overrideIoScheduler() {
        try {
            RxJavaPlugins.setIoSchedulerHandler(replaceWithImmediate);
            Assert.assertSame(INSTANCE, Schedulers.io());
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertNotSame(INSTANCE, Schedulers.io());
    }

    @Test
    public void overrideNewThreadScheduler() {
        try {
            RxJavaPlugins.setNewThreadSchedulerHandler(replaceWithImmediate);
            Assert.assertSame(INSTANCE, Schedulers.newThread());
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertNotSame(INSTANCE, Schedulers.newThread());
    }

    io.reactivex.Function<Callable<Scheduler>, Scheduler> initReplaceWithImmediate = new Function<Callable<Scheduler>, Scheduler>() {
        @Override
        public io.reactivex.Scheduler apply(Callable<Scheduler> t) {
            return ImmediateThinScheduler.INSTANCE;
        }
    };

    @Test
    public void overrideInitSingleScheduler() {
        final Scheduler s = Schedulers.single();// make sure the Schedulers is initialized

        Callable<Scheduler> c = new Callable<Scheduler>() {
            @Override
            public io.reactivex.Scheduler call() throws Exception {
                return s;
            }
        };
        try {
            RxJavaPlugins.setInitSingleSchedulerHandler(initReplaceWithImmediate);
            Assert.assertSame(INSTANCE, RxJavaPlugins.initSingleScheduler(c));
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertSame(s, RxJavaPlugins.initSingleScheduler(c));
    }

    @Test
    public void overrideInitComputationScheduler() {
        final Scheduler s = Schedulers.computation();// make sure the Schedulers is initialized

        Callable<Scheduler> c = new Callable<Scheduler>() {
            @Override
            public io.reactivex.Scheduler call() throws Exception {
                return s;
            }
        };
        try {
            RxJavaPlugins.setInitComputationSchedulerHandler(initReplaceWithImmediate);
            Assert.assertSame(INSTANCE, RxJavaPlugins.initComputationScheduler(c));
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertSame(s, RxJavaPlugins.initComputationScheduler(c));
    }

    @Test
    public void overrideInitIoScheduler() {
        final Scheduler s = Schedulers.io();// make sure the Schedulers is initialized;

        Callable<Scheduler> c = new Callable<Scheduler>() {
            @Override
            public io.reactivex.Scheduler call() throws Exception {
                return s;
            }
        };
        try {
            RxJavaPlugins.setInitIoSchedulerHandler(initReplaceWithImmediate);
            Assert.assertSame(INSTANCE, RxJavaPlugins.initIoScheduler(c));
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertSame(s, RxJavaPlugins.initIoScheduler(c));
    }

    @Test
    public void overrideInitNewThreadScheduler() {
        final Scheduler s = Schedulers.newThread();// make sure the Schedulers is initialized;

        Callable<Scheduler> c = new Callable<Scheduler>() {
            @Override
            public io.reactivex.Scheduler call() throws Exception {
                return s;
            }
        };
        try {
            RxJavaPlugins.setInitNewThreadSchedulerHandler(initReplaceWithImmediate);
            Assert.assertSame(INSTANCE, RxJavaPlugins.initNewThreadScheduler(c));
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertSame(s, RxJavaPlugins.initNewThreadScheduler(c));
    }

    Callable<Scheduler> nullResultCallable = new Callable<Scheduler>() {
        @Override
        public io.reactivex.Scheduler call() throws Exception {
            return null;
        }
    };

    @Test
    public void overrideInitSingleSchedulerCrashes() {
        // fail when Callable is null
        try {
            RxJavaPlugins.initSingleScheduler(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Scheduler Callable can't be null", npe.getMessage());
        }
        // fail when Callable result is null
        try {
            RxJavaPlugins.initSingleScheduler(nullResultCallable);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Scheduler Callable result can't be null", npe.getMessage());
        }
    }

    @Test
    public void overrideInitComputationSchedulerCrashes() {
        // fail when Callable is null
        try {
            RxJavaPlugins.initComputationScheduler(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Scheduler Callable can't be null", npe.getMessage());
        }
        // fail when Callable result is null
        try {
            RxJavaPlugins.initComputationScheduler(nullResultCallable);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Scheduler Callable result can't be null", npe.getMessage());
        }
    }

    @Test
    public void overrideInitIoSchedulerCrashes() {
        // fail when Callable is null
        try {
            RxJavaPlugins.initIoScheduler(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Scheduler Callable can't be null", npe.getMessage());
        }
        // fail when Callable result is null
        try {
            RxJavaPlugins.initIoScheduler(nullResultCallable);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Scheduler Callable result can't be null", npe.getMessage());
        }
    }

    @Test
    public void overrideInitNewThreadSchedulerCrashes() {
        // fail when Callable is null
        try {
            RxJavaPlugins.initNewThreadScheduler(null);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            // expected
            Assert.assertEquals("Scheduler Callable can't be null", npe.getMessage());
        }
        // fail when Callable result is null
        try {
            RxJavaPlugins.initNewThreadScheduler(nullResultCallable);
            Assert.fail("Should have thrown NullPointerException");
        } catch (NullPointerException npe) {
            Assert.assertEquals("Scheduler Callable result can't be null", npe.getMessage());
        }
    }

    Callable<Scheduler> unsafeDefault = new Callable<Scheduler>() {
        @Override
        public io.reactivex.Scheduler call() throws Exception {
            throw new AssertionError("Default Scheduler instance should not have been evaluated");
        }
    };

    @Test
    public void testDefaultSingleSchedulerIsInitializedLazily() {
        // unsafe default Scheduler Callable should not be evaluated
        try {
            RxJavaPlugins.setInitSingleSchedulerHandler(initReplaceWithImmediate);
            RxJavaPlugins.initSingleScheduler(unsafeDefault);
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertNotSame(INSTANCE, Schedulers.single());
    }

    @Test
    public void testDefaultIoSchedulerIsInitializedLazily() {
        // unsafe default Scheduler Callable should not be evaluated
        try {
            RxJavaPlugins.setInitIoSchedulerHandler(initReplaceWithImmediate);
            RxJavaPlugins.initIoScheduler(unsafeDefault);
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertNotSame(INSTANCE, Schedulers.io());
    }

    @Test
    public void testDefaultComputationSchedulerIsInitializedLazily() {
        // unsafe default Scheduler Callable should not be evaluated
        try {
            RxJavaPlugins.setInitComputationSchedulerHandler(initReplaceWithImmediate);
            RxJavaPlugins.initComputationScheduler(unsafeDefault);
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertNotSame(INSTANCE, Schedulers.computation());
    }

    @Test
    public void testDefaultNewThreadSchedulerIsInitializedLazily() {
        // unsafe default Scheduler Callable should not be evaluated
        try {
            RxJavaPlugins.setInitNewThreadSchedulerHandler(initReplaceWithImmediate);
            RxJavaPlugins.initNewThreadScheduler(unsafeDefault);
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Assert.assertNotSame(INSTANCE, Schedulers.newThread());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void observableCreate() {
        try {
            RxJavaPlugins.setOnObservableAssembly(new Function<Observable, Observable>() {
                @Override
                public Observable apply(Observable t) {
                    return new ObservableRange(1, 2);
                }
            });
            range(10, 3).test().assertValues(1, 2).assertNoErrors().assertComplete();
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        range(10, 3).test().assertValues(10, 11, 12).assertNoErrors().assertComplete();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void flowableCreate() {
        try {
            RxJavaPlugins.setOnFlowableAssembly(new Function<Flowable, Flowable>() {
                @Override
                public io.reactivex.Flowable apply(Flowable t) {
                    return new FlowableRange(1, 2);
                }
            });
            Flowable.range(10, 3).test().assertValues(1, 2).assertNoErrors().assertComplete();
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Flowable.range(10, 3).test().assertValues(10, 11, 12).assertNoErrors().assertComplete();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void observableStart() {
        try {
            RxJavaPlugins.setOnObservableSubscribe(new BiFunction<Observable, Observer, Observer>() {
                @Override
                public Observer apply(Observable o, final Observer t) {
                    return new Observer() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            t.onSubscribe(d);
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        public void onNext(Object value) {
                            onNext((((Integer) (value)) - 9));
                        }

                        @Override
                        public void onError(Throwable e) {
                            t.onError(e);
                        }

                        @Override
                        public void onComplete() {
                            onComplete();
                        }
                    };
                }
            });
            range(10, 3).test().assertValues(1, 2, 3).assertNoErrors().assertComplete();
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        range(10, 3).test().assertValues(10, 11, 12).assertNoErrors().assertComplete();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void flowableStart() {
        try {
            RxJavaPlugins.setOnFlowableSubscribe(new BiFunction<Flowable, Subscriber, Subscriber>() {
                @Override
                public io.reactivex.Subscriber apply(Flowable f, final Subscriber t) {
                    return new Subscriber() {
                        @Override
                        public void onSubscribe(Subscription s) {
                            t.onSubscribe(s);
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        public void onNext(Object value) {
                            t.onNext((((Integer) (value)) - 9));
                        }

                        @Override
                        public void onError(Throwable e) {
                            t.onError(e);
                        }

                        @Override
                        public void onComplete() {
                            t.onComplete();
                        }
                    };
                }
            });
            Flowable.range(10, 3).test().assertValues(1, 2, 3).assertNoErrors().assertComplete();
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Flowable.range(10, 3).test().assertValues(10, 11, 12).assertNoErrors().assertComplete();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void singleCreate() {
        try {
            RxJavaPlugins.setOnSingleAssembly(new Function<Single, Single>() {
                @Override
                public io.reactivex.Single apply(Single t) {
                    return new io.reactivex.internal.operators.single.SingleJust<Integer>(10);
                }
            });
            Single.just(1).test().assertValue(10).assertNoErrors().assertComplete();
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Single.just(1).test().assertValue(1).assertNoErrors().assertComplete();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void singleStart() {
        try {
            RxJavaPlugins.setOnSingleSubscribe(new BiFunction<Single, SingleObserver, SingleObserver>() {
                @Override
                public io.reactivex.SingleObserver apply(Single o, final SingleObserver t) {
                    return new SingleObserver<Object>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            t.onSubscribe(d);
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        public void onSuccess(Object value) {
                            t.onSuccess(10);
                        }

                        @Override
                        public void onError(Throwable e) {
                            t.onError(e);
                        }
                    };
                }
            });
            Single.just(1).test().assertValue(10).assertNoErrors().assertComplete();
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Single.just(1).test().assertValue(1).assertNoErrors().assertComplete();
    }

    @Test
    public void completableCreate() {
        try {
            RxJavaPlugins.setOnCompletableAssembly(new Function<Completable, Completable>() {
                @Override
                public io.reactivex.Completable apply(Completable t) {
                    return new CompletableError(new TestException());
                }
            });
            Completable.complete().test().assertNoValues().assertNotComplete().assertError(TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Completable.complete().test().assertNoValues().assertNoErrors().assertComplete();
    }

    @Test
    public void completableStart() {
        try {
            RxJavaPlugins.setOnCompletableSubscribe(new BiFunction<Completable, CompletableObserver, CompletableObserver>() {
                @Override
                public io.reactivex.CompletableObserver apply(Completable o, final CompletableObserver t) {
                    return new CompletableObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            t.onSubscribe(d);
                        }

                        @Override
                        public void onError(Throwable e) {
                            t.onError(e);
                        }

                        @Override
                        public void onComplete() {
                            t.onError(new TestException());
                        }
                    };
                }
            });
            Completable.complete().test().assertNoValues().assertNotComplete().assertError(TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Completable.complete().test().assertNoValues().assertNoErrors().assertComplete();
    }

    @Test
    public void onScheduleComputation() throws InterruptedException {
        onSchedule(Schedulers.computation().createWorker());
    }

    @Test
    public void onScheduleIO() throws InterruptedException {
        onSchedule(Schedulers.io().createWorker());
    }

    @Test
    public void onScheduleNewThread() throws InterruptedException {
        onSchedule(Schedulers.newThread().createWorker());
    }

    @Test
    public void onError() {
        try {
            final List<Throwable> list = new ArrayList<Throwable>();
            RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t) {
                    list.add(t);
                }
            });
            RxJavaPlugins.onError(new TestException("Forced failure"));
            Assert.assertEquals(1, list.size());
            RxJavaPluginsTest.assertUndeliverableTestException(list, 0, "Forced failure");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onErrorNoHandler() {
        try {
            final List<Throwable> list = new ArrayList<Throwable>();
            RxJavaPlugins.setErrorHandler(null);
            Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    list.add(e);
                }
            });
            RxJavaPlugins.onError(new TestException("Forced failure"));
            Thread.currentThread().setUncaughtExceptionHandler(null);
            // this will be printed on the console and should not crash
            RxJavaPlugins.onError(new TestException("Forced failure 3"));
            Assert.assertEquals(1, list.size());
            RxJavaPluginsTest.assertUndeliverableTestException(list, 0, "Forced failure");
        } finally {
            RxJavaPlugins.reset();
            Thread.currentThread().setUncaughtExceptionHandler(null);
        }
    }

    @Test
    public void onErrorCrashes() {
        try {
            final List<Throwable> list = new ArrayList<Throwable>();
            RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t) {
                    throw new TestException("Forced failure 2");
                }
            });
            Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    list.add(e);
                }
            });
            RxJavaPlugins.onError(new TestException("Forced failure"));
            Assert.assertEquals(2, list.size());
            RxJavaPluginsTest.assertTestException(list, 0, "Forced failure 2");
            RxJavaPluginsTest.assertUndeliverableTestException(list, 1, "Forced failure");
            Thread.currentThread().setUncaughtExceptionHandler(null);
        } finally {
            RxJavaPlugins.reset();
            Thread.currentThread().setUncaughtExceptionHandler(null);
        }
    }

    @Test
    public void onErrorWithNull() {
        try {
            final List<Throwable> list = new ArrayList<Throwable>();
            RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t) {
                    throw new TestException("Forced failure 2");
                }
            });
            Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    list.add(e);
                }
            });
            RxJavaPlugins.onError(null);
            Assert.assertEquals(2, list.size());
            RxJavaPluginsTest.assertTestException(list, 0, "Forced failure 2");
            RxJavaPluginsTest.assertNPE(list, 1);
            RxJavaPlugins.reset();
            RxJavaPlugins.onError(null);
            RxJavaPluginsTest.assertNPE(list, 2);
        } finally {
            RxJavaPlugins.reset();
            Thread.currentThread().setUncaughtExceptionHandler(null);
        }
    }

    /**
     * Ensure set*() accepts a consumers/functions with wider bounds.
     *
     * @throws Exception
     * 		on error
     */
    @Test
    @SuppressWarnings("rawtypes")
    public void onErrorWithSuper() throws Exception {
        try {
            Consumer<? super Throwable> errorHandler = new Consumer<Throwable>() {
                @Override
                public void accept(Throwable t) {
                    throw new TestException("Forced failure 2");
                }
            };
            RxJavaPlugins.setErrorHandler(errorHandler);
            Consumer<? super Throwable> errorHandler1 = RxJavaPlugins.getErrorHandler();
            Assert.assertSame(errorHandler, errorHandler1);
            Function<? super Scheduler, ? extends Scheduler> scheduler2scheduler = new Function<Scheduler, Scheduler>() {
                @Override
                public io.reactivex.Scheduler apply(Scheduler scheduler) throws Exception {
                    return scheduler;
                }
            };
            Function<? super Callable<Scheduler>, ? extends Scheduler> callable2scheduler = new Function<Callable<Scheduler>, Scheduler>() {
                @Override
                public io.reactivex.Scheduler apply(Callable<Scheduler> schedulerCallable) throws Exception {
                    return schedulerCallable.call();
                }
            };
            Function<? super ConnectableFlowable, ? extends ConnectableFlowable> connectableFlowable2ConnectableFlowable = new Function<ConnectableFlowable, ConnectableFlowable>() {
                @Override
                public ConnectableFlowable apply(ConnectableFlowable connectableFlowable) throws Exception {
                    return connectableFlowable;
                }
            };
            Function<? super ConnectableObservable, ? extends ConnectableObservable> connectableObservable2ConnectableObservable = new Function<ConnectableObservable, ConnectableObservable>() {
                @Override
                public ConnectableObservable apply(ConnectableObservable connectableObservable) throws Exception {
                    return connectableObservable;
                }
            };
            Function<? super Flowable, ? extends Flowable> flowable2Flowable = new Function<Flowable, Flowable>() {
                @Override
                public io.reactivex.Flowable apply(Flowable flowable) throws Exception {
                    return flowable;
                }
            };
            BiFunction<? super Flowable, ? super Subscriber, ? extends Subscriber> flowable2subscriber = new BiFunction<Flowable, Subscriber, Subscriber>() {
                @Override
                public io.reactivex.Subscriber apply(Flowable flowable, Subscriber subscriber) throws Exception {
                    return subscriber;
                }
            };
            Function<Maybe, Maybe> maybe2maybe = new Function<Maybe, Maybe>() {
                @Override
                public io.reactivex.Maybe apply(Maybe maybe) throws Exception {
                    return maybe;
                }
            };
            BiFunction<Maybe, MaybeObserver, MaybeObserver> maybe2observer = new BiFunction<Maybe, MaybeObserver, MaybeObserver>() {
                @Override
                public io.reactivex.MaybeObserver apply(Maybe maybe, MaybeObserver maybeObserver) throws Exception {
                    return maybeObserver;
                }
            };
            Function<Observable, Observable> observable2observable = new Function<Observable, Observable>() {
                @Override
                public Observable apply(Observable observable) throws Exception {
                    return observable;
                }
            };
            BiFunction<? super Observable, ? super Observer, ? extends Observer> observable2observer = new BiFunction<Observable, Observer, Observer>() {
                @Override
                public Observer apply(Observable observable, Observer observer) throws Exception {
                    return observer;
                }
            };
            Function<? super ParallelFlowable, ? extends ParallelFlowable> parallelFlowable2parallelFlowable = new Function<ParallelFlowable, ParallelFlowable>() {
                @Override
                public ParallelFlowable apply(ParallelFlowable parallelFlowable) throws Exception {
                    return parallelFlowable;
                }
            };
            Function<Single, Single> single2single = new Function<Single, Single>() {
                @Override
                public io.reactivex.Single apply(Single single) throws Exception {
                    return single;
                }
            };
            BiFunction<? super Single, ? super SingleObserver, ? extends SingleObserver> single2observer = new BiFunction<Single, SingleObserver, SingleObserver>() {
                @Override
                public io.reactivex.SingleObserver apply(Single single, SingleObserver singleObserver) throws Exception {
                    return singleObserver;
                }
            };
            Function<? super Runnable, ? extends Runnable> runnable2runnable = new Function<Runnable, Runnable>() {
                @Override
                public Runnable apply(Runnable runnable) throws Exception {
                    return runnable;
                }
            };
            BiFunction<? super Completable, ? super CompletableObserver, ? extends CompletableObserver> completableObserver2completableObserver = new BiFunction<Completable, CompletableObserver, CompletableObserver>() {
                @Override
                public io.reactivex.CompletableObserver apply(Completable completable, CompletableObserver completableObserver) throws Exception {
                    return completableObserver;
                }
            };
            Function<? super Completable, ? extends Completable> completable2completable = new Function<Completable, Completable>() {
                @Override
                public io.reactivex.Completable apply(Completable completable) throws Exception {
                    return completable;
                }
            };
            RxJavaPlugins.setInitComputationSchedulerHandler(callable2scheduler);
            RxJavaPlugins.setComputationSchedulerHandler(scheduler2scheduler);
            RxJavaPlugins.setIoSchedulerHandler(scheduler2scheduler);
            RxJavaPlugins.setNewThreadSchedulerHandler(scheduler2scheduler);
            RxJavaPlugins.setOnConnectableFlowableAssembly(connectableFlowable2ConnectableFlowable);
            RxJavaPlugins.setOnConnectableObservableAssembly(connectableObservable2ConnectableObservable);
            RxJavaPlugins.setOnFlowableAssembly(flowable2Flowable);
            RxJavaPlugins.setOnFlowableSubscribe(flowable2subscriber);
            RxJavaPlugins.setOnMaybeAssembly(maybe2maybe);
            RxJavaPlugins.setOnMaybeSubscribe(maybe2observer);
            RxJavaPlugins.setOnObservableAssembly(observable2observable);
            RxJavaPlugins.setOnObservableSubscribe(observable2observer);
            RxJavaPlugins.setOnParallelAssembly(parallelFlowable2parallelFlowable);
            RxJavaPlugins.setOnSingleAssembly(single2single);
            RxJavaPlugins.setOnSingleSubscribe(single2observer);
            RxJavaPlugins.setScheduleHandler(runnable2runnable);
            RxJavaPlugins.setSingleSchedulerHandler(scheduler2scheduler);
            RxJavaPlugins.setOnCompletableSubscribe(completableObserver2completableObserver);
            RxJavaPlugins.setOnCompletableAssembly(completable2completable);
            RxJavaPlugins.setInitSingleSchedulerHandler(callable2scheduler);
            RxJavaPlugins.setInitNewThreadSchedulerHandler(callable2scheduler);
            RxJavaPlugins.setInitIoSchedulerHandler(callable2scheduler);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void clearIsPassthrough() {
        try {
            RxJavaPlugins.reset();
            Assert.assertNull(RxJavaPlugins.onAssembly(((Observable) (null))));
            Assert.assertNull(RxJavaPlugins.onAssembly(((ConnectableObservable) (null))));
            Assert.assertNull(RxJavaPlugins.onAssembly(((Flowable) (null))));
            Assert.assertNull(RxJavaPlugins.onAssembly(((ConnectableFlowable) (null))));
            Observable oos = new Observable() {
                @Override
                public void subscribeActual(Observer t) {
                }
            };
            Flowable fos = new Flowable() {
                @Override
                public void subscribeActual(Subscriber t) {
                }
            };
            Assert.assertSame(oos, RxJavaPlugins.onAssembly(oos));
            Assert.assertSame(fos, RxJavaPlugins.onAssembly(fos));
            Assert.assertNull(RxJavaPlugins.onAssembly(((Single) (null))));
            Single sos = new Single() {
                @Override
                public void subscribeActual(SingleObserver t) {
                }
            };
            Assert.assertSame(sos, RxJavaPlugins.onAssembly(sos));
            Assert.assertNull(RxJavaPlugins.onAssembly(((Completable) (null))));
            Completable cos = new Completable() {
                @Override
                public void subscribeActual(CompletableObserver t) {
                }
            };
            Assert.assertSame(cos, RxJavaPlugins.onAssembly(cos));
            Assert.assertNull(RxJavaPlugins.onAssembly(((Maybe) (null))));
            Maybe myb = new Maybe() {
                @Override
                public void subscribeActual(MaybeObserver t) {
                }
            };
            Assert.assertSame(myb, RxJavaPlugins.onAssembly(myb));
            Runnable action = Functions.EMPTY_RUNNABLE;
            Assert.assertSame(action, RxJavaPlugins.onSchedule(action));
            class AllSubscriber implements CompletableObserver , MaybeObserver , SingleObserver , Subscriber , Observer {
                @Override
                public void onSuccess(Object value) {
                }

                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onSubscribe(Subscription s) {
                }

                @Override
                public void onNext(Object t) {
                }

                @Override
                public void onError(Throwable t) {
                }

                @Override
                public void onComplete() {
                }
            }
            AllSubscriber all = new AllSubscriber();
            Assert.assertNull(RxJavaPlugins.onSubscribe(never(), null));
            Assert.assertSame(all, RxJavaPlugins.onSubscribe(never(), all));
            Assert.assertNull(RxJavaPlugins.onSubscribe(Flowable.never(), null));
            Assert.assertSame(all, RxJavaPlugins.onSubscribe(Flowable.never(), all));
            Assert.assertNull(RxJavaPlugins.onSubscribe(Single.just(1), null));
            Assert.assertSame(all, RxJavaPlugins.onSubscribe(Single.just(1), all));
            Assert.assertNull(RxJavaPlugins.onSubscribe(Completable.never(), null));
            Assert.assertSame(all, RxJavaPlugins.onSubscribe(Completable.never(), all));
            Assert.assertNull(RxJavaPlugins.onSubscribe(Maybe.never(), null));
            Assert.assertSame(all, RxJavaPlugins.onSubscribe(Maybe.never(), all));
            // These hooks don't exist in 2.0
            // Subscription subscription = Subscriptions.empty();
            // 
            // assertNull(RxJavaPlugins.onObservableReturn(null));
            // 
            // assertSame(subscription, RxJavaPlugins.onObservableReturn(subscription));
            // 
            // assertNull(RxJavaPlugins.onSingleReturn(null));
            // 
            // assertSame(subscription, RxJavaPlugins.onSingleReturn(subscription));
            // 
            // TestException ex = new TestException();
            // 
            // assertNull(RxJavaPlugins.onObservableError(null));
            // 
            // assertSame(ex, RxJavaPlugins.onObservableError(ex));
            // 
            // assertNull(RxJavaPlugins.onSingleError(null));
            // 
            // assertSame(ex, RxJavaPlugins.onSingleError(ex));
            // 
            // assertNull(RxJavaPlugins.onCompletableError(null));
            // 
            // assertSame(ex, RxJavaPlugins.onCompletableError(ex));
            // 
            // Observable.Operator oop = new Observable.Operator() {
            // @Override
            // public Object call(Object t) {
            // return t;
            // }
            // };
            // 
            // assertNull(RxJavaPlugins.onObservableLift(null));
            // 
            // assertSame(oop, RxJavaPlugins.onObservableLift(oop));
            // 
            // assertNull(RxJavaPlugins.onSingleLift(null));
            // 
            // assertSame(oop, RxJavaPlugins.onSingleLift(oop));
            // 
            // Completable.CompletableOperator cop = new Completable.CompletableOperator() {
            // @Override
            // public CompletableSubscriber call(CompletableSubscriber t) {
            // return t;
            // }
            // };
            // 
            // assertNull(RxJavaPlugins.onCompletableLift(null));
            // 
            // assertSame(cop, RxJavaPlugins.onCompletableLift(cop));
            final Scheduler s = ImmediateThinScheduler.INSTANCE;
            Callable<Scheduler> c = new Callable<Scheduler>() {
                @Override
                public io.reactivex.Scheduler call() throws Exception {
                    return s;
                }
            };
            Assert.assertSame(s, RxJavaPlugins.onComputationScheduler(s));
            Assert.assertSame(s, RxJavaPlugins.onIoScheduler(s));
            Assert.assertSame(s, RxJavaPlugins.onNewThreadScheduler(s));
            Assert.assertSame(s, RxJavaPlugins.onSingleScheduler(s));
            Assert.assertSame(s, RxJavaPlugins.initComputationScheduler(c));
            Assert.assertSame(s, RxJavaPlugins.initIoScheduler(c));
            Assert.assertSame(s, RxJavaPlugins.initNewThreadScheduler(c));
            Assert.assertSame(s, RxJavaPlugins.initSingleScheduler(c));
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void overrideConnectableObservable() {
        try {
            RxJavaPlugins.setOnConnectableObservableAssembly(new Function<ConnectableObservable, ConnectableObservable>() {
                @Override
                public ConnectableObservable apply(ConnectableObservable co) throws Exception {
                    return new ConnectableObservable() {
                        @Override
                        public void connect(Consumer connection) {
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        protected void subscribeActual(Observer observer) {
                            observer.onSubscribe(Disposables.empty());
                            onNext(10);
                            onComplete();
                        }
                    };
                }
            });
            just(1).publish().autoConnect().test().assertResult(10);
        } finally {
            RxJavaPlugins.reset();
        }
        just(1).publish().autoConnect().test().assertResult(1);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void overrideConnectableFlowable() {
        try {
            RxJavaPlugins.setOnConnectableFlowableAssembly(new Function<ConnectableFlowable, ConnectableFlowable>() {
                @Override
                public ConnectableFlowable apply(ConnectableFlowable co) throws Exception {
                    return new ConnectableFlowable() {
                        @Override
                        public void connect(Consumer connection) {
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        protected void subscribeActual(Subscriber subscriber) {
                            subscriber.onSubscribe(new io.reactivex.internal.subscriptions.ScalarSubscription(subscriber, 10));
                        }
                    };
                }
            });
            Flowable.just(1).publish().autoConnect().test().assertResult(10);
        } finally {
            RxJavaPlugins.reset();
        }
        Flowable.just(1).publish().autoConnect().test().assertResult(1);
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void assemblyHookCrashes() {
        try {
            RxJavaPlugins.setOnFlowableAssembly(new Function<Flowable, Flowable>() {
                @Override
                public io.reactivex.Flowable apply(Flowable f) throws Exception {
                    throw new IllegalArgumentException();
                }
            });
            try {
                Flowable.empty();
                Assert.fail("Should have thrown!");
            } catch (IllegalArgumentException ex) {
                // expected
            }
            RxJavaPlugins.setOnFlowableAssembly(new Function<Flowable, Flowable>() {
                @Override
                public io.reactivex.Flowable apply(Flowable f) throws Exception {
                    throw new InternalError();
                }
            });
            try {
                Flowable.empty();
                Assert.fail("Should have thrown!");
            } catch (InternalError ex) {
                // expected
            }
            RxJavaPlugins.setOnFlowableAssembly(new Function<Flowable, Flowable>() {
                @Override
                public io.reactivex.Flowable apply(Flowable f) throws Exception {
                    throw new IOException();
                }
            });
            try {
                Flowable.empty();
                Assert.fail("Should have thrown!");
            } catch (RuntimeException ex) {
                if (!((ex.getCause()) instanceof IOException)) {
                    Assert.fail(((ex.getCause().toString()) + ": Should have thrown RuntimeException(IOException)"));
                }
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void subscribeHookCrashes() {
        try {
            RxJavaPlugins.setOnFlowableSubscribe(new BiFunction<Flowable, Subscriber, Subscriber>() {
                @Override
                public io.reactivex.Subscriber apply(Flowable f, Subscriber s) throws Exception {
                    throw new IllegalArgumentException();
                }
            });
            try {
                Flowable.empty().test();
                Assert.fail("Should have thrown!");
            } catch (NullPointerException ex) {
                if (!((ex.getCause()) instanceof IllegalArgumentException)) {
                    Assert.fail(((ex.getCause().toString()) + ": Should have thrown NullPointerException(IllegalArgumentException)"));
                }
            }
            RxJavaPlugins.setOnFlowableSubscribe(new BiFunction<Flowable, Subscriber, Subscriber>() {
                @Override
                public io.reactivex.Subscriber apply(Flowable f, Subscriber s) throws Exception {
                    throw new InternalError();
                }
            });
            try {
                Flowable.empty().test();
                Assert.fail("Should have thrown!");
            } catch (InternalError ex) {
                // expected
            }
            RxJavaPlugins.setOnFlowableSubscribe(new BiFunction<Flowable, Subscriber, Subscriber>() {
                @Override
                public io.reactivex.Subscriber apply(Flowable f, Subscriber s) throws Exception {
                    throw new IOException();
                }
            });
            try {
                Flowable.empty().test();
                Assert.fail("Should have thrown!");
            } catch (NullPointerException ex) {
                if (!((ex.getCause()) instanceof RuntimeException)) {
                    Assert.fail(((ex.getCause().toString()) + ": Should have thrown NullPointerException(RuntimeException(IOException))"));
                }
                if (!((ex.getCause().getCause()) instanceof IOException)) {
                    Assert.fail(((ex.getCause().toString()) + ": Should have thrown NullPointerException(RuntimeException(IOException))"));
                }
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void maybeCreate() {
        try {
            RxJavaPlugins.setOnMaybeAssembly(new Function<Maybe, Maybe>() {
                @Override
                public io.reactivex.Maybe apply(Maybe t) {
                    return new MaybeError(new TestException());
                }
            });
            Maybe.empty().test().assertNoValues().assertNotComplete().assertError(TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Maybe.empty().test().assertNoValues().assertNoErrors().assertComplete();
    }

    @Test
    @SuppressWarnings("rawtypes")
    public void maybeStart() {
        try {
            RxJavaPlugins.setOnMaybeSubscribe(new BiFunction<Maybe, MaybeObserver, MaybeObserver>() {
                @Override
                public io.reactivex.MaybeObserver apply(Maybe o, final MaybeObserver t) {
                    return new MaybeObserver() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            t.onSubscribe(d);
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        public void onSuccess(Object value) {
                            t.onSuccess(value);
                        }

                        @Override
                        public void onError(Throwable e) {
                            t.onError(e);
                        }

                        @Override
                        public void onComplete() {
                            t.onError(new TestException());
                        }
                    };
                }
            });
            Maybe.empty().test().assertNoValues().assertNotComplete().assertError(TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
        // make sure the reset worked
        Maybe.empty().test().assertNoValues().assertNoErrors().assertComplete();
    }

    @Test
    public void onErrorNull() {
        try {
            final AtomicReference<Throwable> t = new AtomicReference<Throwable>();
            RxJavaPlugins.setErrorHandler(new Consumer<Throwable>() {
                @Override
                public void accept(final Throwable throwable) throws Exception {
                    t.set(throwable);
                }
            });
            RxJavaPlugins.onError(null);
            final Throwable throwable = t.get();
            Assert.assertEquals("onError called with null. Null values are generally not allowed in 2.x operators and sources.", throwable.getMessage());
            Assert.assertTrue((throwable instanceof NullPointerException));
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createComputationScheduler() {
        final String name = "ComputationSchedulerTest";
        ThreadFactory factory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, name);
            }
        };
        final Scheduler customScheduler = RxJavaPlugins.createComputationScheduler(factory);
        RxJavaPlugins.setComputationSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public io.reactivex.Scheduler apply(Scheduler scheduler) throws Exception {
                return customScheduler;
            }
        });
        try {
            RxJavaPluginsTest.verifyThread(Schedulers.computation(), name);
        } finally {
            customScheduler.shutdown();
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createIoScheduler() {
        final String name = "IoSchedulerTest";
        ThreadFactory factory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, name);
            }
        };
        final Scheduler customScheduler = RxJavaPlugins.createIoScheduler(factory);
        RxJavaPlugins.setIoSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public io.reactivex.Scheduler apply(Scheduler scheduler) throws Exception {
                return customScheduler;
            }
        });
        try {
            RxJavaPluginsTest.verifyThread(Schedulers.io(), name);
        } finally {
            customScheduler.shutdown();
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createNewThreadScheduler() {
        final String name = "NewThreadSchedulerTest";
        ThreadFactory factory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, name);
            }
        };
        final Scheduler customScheduler = RxJavaPlugins.createNewThreadScheduler(factory);
        RxJavaPlugins.setNewThreadSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public io.reactivex.Scheduler apply(Scheduler scheduler) throws Exception {
                return customScheduler;
            }
        });
        try {
            RxJavaPluginsTest.verifyThread(Schedulers.newThread(), name);
        } finally {
            customScheduler.shutdown();
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void createSingleScheduler() {
        final String name = "SingleSchedulerTest";
        ThreadFactory factory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, name);
            }
        };
        final Scheduler customScheduler = RxJavaPlugins.createSingleScheduler(factory);
        RxJavaPlugins.setSingleSchedulerHandler(new Function<Scheduler, Scheduler>() {
            @Override
            public io.reactivex.Scheduler apply(Scheduler scheduler) throws Exception {
                return customScheduler;
            }
        });
        try {
            RxJavaPluginsTest.verifyThread(Schedulers.single(), name);
        } finally {
            customScheduler.shutdown();
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onBeforeBlocking() {
        try {
            RxJavaPlugins.setOnBeforeBlocking(new BooleanSupplier() {
                @Override
                public boolean getAsBoolean() throws Exception {
                    throw new IllegalArgumentException();
                }
            });
            try {
                RxJavaPlugins.onBeforeBlocking();
                Assert.fail("Should have thrown");
            } catch (IllegalArgumentException ex) {
                // expected
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void onParallelAssembly() {
        try {
            RxJavaPlugins.setOnParallelAssembly(new Function<ParallelFlowable, ParallelFlowable>() {
                @Override
                public ParallelFlowable apply(ParallelFlowable pf) throws Exception {
                    return new io.reactivex.internal.operators.parallel.ParallelFromPublisher<Integer>(Flowable.just(2), 2, 2);
                }
            });
            Flowable.just(1).parallel().sequential().test().assertResult(2);
        } finally {
            RxJavaPlugins.reset();
        }
        Flowable.just(1).parallel().sequential().test().assertResult(1);
    }

    @Test
    public void isBug() {
        Assert.assertFalse(RxJavaPlugins.isBug(new RuntimeException()));
        Assert.assertFalse(RxJavaPlugins.isBug(new IOException()));
        Assert.assertFalse(RxJavaPlugins.isBug(new InterruptedException()));
        Assert.assertFalse(RxJavaPlugins.isBug(new InterruptedIOException()));
        Assert.assertTrue(RxJavaPlugins.isBug(new NullPointerException()));
        Assert.assertTrue(RxJavaPlugins.isBug(new IllegalArgumentException()));
        Assert.assertTrue(RxJavaPlugins.isBug(new IllegalStateException()));
        Assert.assertTrue(RxJavaPlugins.isBug(new MissingBackpressureException()));
        Assert.assertTrue(RxJavaPlugins.isBug(new ProtocolViolationException("")));
        Assert.assertTrue(RxJavaPlugins.isBug(new UndeliverableException(new TestException())));
        Assert.assertTrue(RxJavaPlugins.isBug(new CompositeException(new TestException())));
        Assert.assertTrue(RxJavaPlugins.isBug(new OnErrorNotImplementedException(new TestException())));
    }
}

