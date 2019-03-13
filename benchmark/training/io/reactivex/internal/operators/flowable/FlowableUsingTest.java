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


import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class FlowableUsingTest {
    private interface Resource {
        String getTextFromWeb();

        void dispose();
    }

    private static class DisposeAction implements Consumer<FlowableUsingTest.Resource> {
        @Override
        public void accept(FlowableUsingTest.Resource r) {
            r.dispose();
        }
    }

    private final io.reactivex.Consumer<Disposable> disposeSubscription = new Consumer<Disposable>() {
        @Override
        public void accept(Disposable d) {
            d.dispose();
        }
    };

    @Test
    public void testUsing() {
        performTestUsing(false);
    }

    @Test
    public void testUsingEagerly() {
        performTestUsing(true);
    }

    @Test
    public void testUsingWithSubscribingTwice() {
        performTestUsingWithSubscribingTwice(false);
    }

    @Test
    public void testUsingWithSubscribingTwiceDisposeEagerly() {
        performTestUsingWithSubscribingTwice(true);
    }

    @Test(expected = TestException.class)
    public void testUsingWithResourceFactoryError() {
        performTestUsingWithResourceFactoryError(false);
    }

    @Test(expected = TestException.class)
    public void testUsingWithResourceFactoryErrorDisposeEagerly() {
        performTestUsingWithResourceFactoryError(true);
    }

    @Test
    public void testUsingWithFlowableFactoryError() {
        performTestUsingWithFlowableFactoryError(false);
    }

    @Test
    public void testUsingWithFlowableFactoryErrorDisposeEagerly() {
        performTestUsingWithFlowableFactoryError(true);
    }

    @Test
    public void testUsingDisposesEagerlyBeforeCompletion() {
        final List<String> events = new ArrayList<String>();
        Callable<FlowableUsingTest.Resource> resourceFactory = FlowableUsingTest.createResourceFactory(events);
        final Action completion = FlowableUsingTest.createOnCompletedAction(events);
        final Action unsub = FlowableUsingTest.createUnsubAction(events);
        Function<FlowableUsingTest.Resource, Flowable<String>> observableFactory = new Function<FlowableUsingTest.Resource, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(FlowableUsingTest.Resource resource) {
                return Flowable.fromArray(resource.getTextFromWeb().split(" "));
            }
        };
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> flowable = Flowable.using(resourceFactory, observableFactory, new FlowableUsingTest.DisposeAction(), true).doOnCancel(unsub).doOnComplete(completion);
        flowable.safeSubscribe(subscriber);
        Assert.assertEquals(Arrays.asList("disposed", "completed"), events);
    }

    @Test
    public void testUsingDoesNotDisposesEagerlyBeforeCompletion() {
        final List<String> events = new ArrayList<String>();
        Callable<FlowableUsingTest.Resource> resourceFactory = FlowableUsingTest.createResourceFactory(events);
        final Action completion = FlowableUsingTest.createOnCompletedAction(events);
        final Action unsub = FlowableUsingTest.createUnsubAction(events);
        Function<FlowableUsingTest.Resource, Flowable<String>> observableFactory = new Function<FlowableUsingTest.Resource, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(FlowableUsingTest.Resource resource) {
                return Flowable.fromArray(resource.getTextFromWeb().split(" "));
            }
        };
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> flowable = Flowable.using(resourceFactory, observableFactory, new FlowableUsingTest.DisposeAction(), false).doOnCancel(unsub).doOnComplete(completion);
        flowable.safeSubscribe(subscriber);
        Assert.assertEquals(Arrays.asList("completed", "disposed"), events);
    }

    @Test
    public void testUsingDisposesEagerlyBeforeError() {
        final List<String> events = new ArrayList<String>();
        Callable<FlowableUsingTest.Resource> resourceFactory = FlowableUsingTest.createResourceFactory(events);
        final Consumer<Throwable> onError = FlowableUsingTest.createOnErrorAction(events);
        final Action unsub = FlowableUsingTest.createUnsubAction(events);
        Function<FlowableUsingTest.Resource, Flowable<String>> observableFactory = new Function<FlowableUsingTest.Resource, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(FlowableUsingTest.Resource resource) {
                return Flowable.fromArray(resource.getTextFromWeb().split(" ")).concatWith(Flowable.<String>error(new RuntimeException()));
            }
        };
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> flowable = Flowable.using(resourceFactory, observableFactory, new FlowableUsingTest.DisposeAction(), true).doOnCancel(unsub).doOnError(onError);
        flowable.safeSubscribe(subscriber);
        Assert.assertEquals(Arrays.asList("disposed", "error"), events);
    }

    @Test
    public void testUsingDoesNotDisposesEagerlyBeforeError() {
        final List<String> events = new ArrayList<String>();
        final Callable<FlowableUsingTest.Resource> resourceFactory = FlowableUsingTest.createResourceFactory(events);
        final Consumer<Throwable> onError = FlowableUsingTest.createOnErrorAction(events);
        final Action unsub = FlowableUsingTest.createUnsubAction(events);
        Function<FlowableUsingTest.Resource, Flowable<String>> observableFactory = new Function<FlowableUsingTest.Resource, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(FlowableUsingTest.Resource resource) {
                return Flowable.fromArray(resource.getTextFromWeb().split(" ")).concatWith(Flowable.<String>error(new RuntimeException()));
            }
        };
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> flowable = Flowable.using(resourceFactory, observableFactory, new FlowableUsingTest.DisposeAction(), false).doOnCancel(unsub).doOnError(onError);
        flowable.safeSubscribe(subscriber);
        Assert.assertEquals(Arrays.asList("error", "disposed"), events);
    }

    @Test
    public void factoryThrows() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        final AtomicInteger count = new AtomicInteger();
        Flowable.<Integer, Integer>using(new Callable<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        }, new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) {
                throw new TestException("forced failure");
            }
        }, new Consumer<Integer>() {
            @Override
            public void accept(Integer c) {
                count.incrementAndGet();
            }
        }).subscribe(ts);
        ts.assertError(TestException.class);
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void nonEagerTermination() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        final AtomicInteger count = new AtomicInteger();
        Flowable.<Integer, Integer>using(new Callable<Integer>() {
            @Override
            public Integer call() {
                return 1;
            }
        }, new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer v) {
                return Flowable.just(v);
            }
        }, new Consumer<Integer>() {
            @Override
            public void accept(Integer c) {
                count.incrementAndGet();
            }
        }, false).subscribe(ts);
        ts.assertValue(1);
        ts.assertNoErrors();
        ts.assertComplete();
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.using(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 1;
            }
        }, new Function<Object, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Object v) throws Exception {
                return Flowable.never();
            }
        }, Functions.emptyConsumer()));
    }

    @Test
    public void supplierDisposerCrash() {
        TestSubscriber<Object> ts = Flowable.using(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 1;
            }
        }, new Function<Object, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Object v) throws Exception {
                throw new TestException("First");
            }
        }, new Consumer<Object>() {
            @Override
            public void accept(Object e) throws Exception {
                throw new TestException("Second");
            }
        }).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(ts.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "First");
        TestHelper.assertError(errors, 1, TestException.class, "Second");
    }

    @Test
    public void eagerOnErrorDisposerCrash() {
        TestSubscriber<Object> ts = Flowable.using(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 1;
            }
        }, new Function<Object, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Object v) throws Exception {
                return Flowable.error(new TestException("First"));
            }
        }, new Consumer<Object>() {
            @Override
            public void accept(Object e) throws Exception {
                throw new TestException("Second");
            }
        }).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(ts.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "First");
        TestHelper.assertError(errors, 1, TestException.class, "Second");
    }

    @Test
    public void eagerOnCompleteDisposerCrash() {
        Flowable.using(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return 1;
            }
        }, new Function<Object, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Object v) throws Exception {
                return Flowable.empty();
            }
        }, new Consumer<Object>() {
            @Override
            public void accept(Object e) throws Exception {
                throw new TestException("Second");
            }
        }).test().assertFailureAndMessage(TestException.class, "Second");
    }

    @Test
    public void nonEagerDisposerCrash() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.using(new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return 1;
                }
            }, new Function<Object, Flowable<Object>>() {
                @Override
                public io.reactivex.Flowable<Object> apply(Object v) throws Exception {
                    return Flowable.empty();
                }
            }, new Consumer<Object>() {
                @Override
                public void accept(Object e) throws Exception {
                    throw new TestException("Second");
                }
            }, false).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void sourceSupplierReturnsNull() {
        Flowable.using(Functions.justCallable(1), Functions.justFunction(((Publisher<Object>) (null))), Functions.emptyConsumer()).test().assertFailureAndMessage(NullPointerException.class, "The sourceSupplier returned a null Publisher");
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return Flowable.using(Functions.justCallable(1), Functions.justFunction(f), Functions.emptyConsumer());
            }
        });
    }

    @Test
    public void eagerDisposedOnComplete() {
        final TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.using(Functions.justCallable(1), Functions.justFunction(new Flowable<Integer>() {
            @Override
            protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                ts.cancel();
                subscriber.onComplete();
            }
        }), Functions.emptyConsumer(), true).subscribe(ts);
    }

    @Test
    public void eagerDisposedOnError() {
        final TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.using(Functions.justCallable(1), Functions.justFunction(new Flowable<Integer>() {
            @Override
            protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                ts.cancel();
                subscriber.onError(new TestException());
            }
        }), Functions.emptyConsumer(), true).subscribe(ts);
    }
}

