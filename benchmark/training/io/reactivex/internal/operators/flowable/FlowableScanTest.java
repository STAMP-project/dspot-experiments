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


import io.reactivex.Flowable;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.flowable.FlowableEventStream;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static io.reactivex.flowable.Burst.item;
import static io.reactivex.flowable.Burst.items;


public class FlowableScanTest {
    @Test
    public void testScanIntegersWithInitialValue() {
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<Integer> flowable = Flowable.just(1, 2, 3);
        Flowable<String> m = flowable.scan("", new BiFunction<String, Integer, String>() {
            @Override
            public String apply(String s, Integer n) {
                return s + (n.toString());
            }
        });
        m.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onNext("");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("1");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("12");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("123");
        Mockito.verify(subscriber, Mockito.times(4)).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testScanIntegersWithoutInitialValue() {
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        Flowable<Integer> flowable = Flowable.just(1, 2, 3);
        Flowable<Integer> m = flowable.scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });
        m.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(0);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(3);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(6);
        Mockito.verify(subscriber, Mockito.times(3)).onNext(ArgumentMatchers.anyInt());
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testScanIntegersWithoutInitialValueAndOnlyOneValue() {
        Subscriber<Integer> subscriber = TestHelper.mockSubscriber();
        Flowable<Integer> flowable = Flowable.just(1);
        Flowable<Integer> m = flowable.scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });
        m.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onNext(0);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(ArgumentMatchers.anyInt());
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void shouldNotEmitUntilAfterSubscription() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        Flowable.range(1, 100).scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer t1) {
                // this will cause request(1) when 0 is emitted
                return t1 > 0;
            }
        }).subscribe(ts);
        Assert.assertEquals(100, ts.values().size());
    }

    @Test
    public void testBackpressureWithInitialValue() {
        final AtomicInteger count = new AtomicInteger();
        Flowable.range(1, 100).scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(10);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                Assert.fail(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer t) {
                count.incrementAndGet();
            }
        });
        // we only expect to receive 10 since we request(10)
        Assert.assertEquals(10, count.get());
    }

    @Test
    public void testBackpressureWithoutInitialValue() {
        final AtomicInteger count = new AtomicInteger();
        Flowable.range(1, 100).scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(10);
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                Assert.fail(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer t) {
                count.incrementAndGet();
            }
        });
        // we only expect to receive 10 since we request(10)
        Assert.assertEquals(10, count.get());
    }

    @Test
    public void testNoBackpressureWithInitialValue() {
        final AtomicInteger count = new AtomicInteger();
        Flowable.range(1, 100).scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                Assert.fail(e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer t) {
                count.incrementAndGet();
            }
        });
        // we only expect to receive 101 as we'll receive all 100 + the initial value
        Assert.assertEquals(101, count.get());
    }

    /**
     * This uses the public API collect which uses scan under the covers.
     */
    @Test
    public void testSeedFactory() {
        Single<List<Integer>> o = Flowable.range(1, 10).collect(new Callable<List<Integer>>() {
            @Override
            public List<Integer> call() {
                return new ArrayList<Integer>();
            }
        }, new BiConsumer<List<Integer>, Integer>() {
            @Override
            public void accept(List<Integer> list, Integer t2) {
                list.add(t2);
            }
        });
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), o.blockingGet());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), o.blockingGet());
    }

    /**
     * This uses the public API collect which uses scan under the covers.
     */
    @Test
    public void testSeedFactoryFlowable() {
        Flowable<List<Integer>> f = Flowable.range(1, 10).collect(new Callable<List<Integer>>() {
            @Override
            public List<Integer> call() {
                return new ArrayList<Integer>();
            }
        }, new BiConsumer<List<Integer>, Integer>() {
            @Override
            public void accept(List<Integer> list, Integer t2) {
                list.add(t2);
            }
        }).toFlowable().takeLast(1);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), f.blockingSingle());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), f.blockingSingle());
    }

    @Test
    public void testScanWithRequestOne() {
        Flowable<Integer> f = Flowable.just(1, 2).scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        }).take(1);
        TestSubscriber<Integer> subscriber = new TestSubscriber<Integer>();
        f.subscribe(subscriber);
        subscriber.assertValue(0);
        subscriber.assertTerminated();
        subscriber.assertNoErrors();
    }

    @Test
    public void testScanShouldNotRequestZero() {
        final AtomicReference<Subscription> producer = new AtomicReference<Subscription>();
        Flowable<Integer> f = Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(final Subscriber<? super Integer> subscriber) {
                Subscription p = Mockito.spy(new Subscription() {
                    private AtomicBoolean requested = new AtomicBoolean(false);

                    @Override
                    public void request(long n) {
                        if (requested.compareAndSet(false, true)) {
                            subscriber.onNext(1);
                            subscriber.onComplete();
                        }
                    }

                    @Override
                    public void cancel() {
                    }
                });
                producer.set(p);
                subscriber.onSubscribe(p);
            }
        }).scan(100, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                return t1 + t2;
            }
        });
        f.subscribe(new TestSubscriber<Integer>(1L) {
            @Override
            public void onNext(Integer integer) {
                request(1);
            }
        });
        request(0);
        Mockito.verify(producer.get(), Mockito.times(1)).request(((Flowable.bufferSize()) - 1));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.create().scan(new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object a, Object b) throws Exception {
                return a;
            }
        }));
        TestHelper.checkDisposed(PublishProcessor.<Integer>create().scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.scan(new BiFunction<Object, Object, Object>() {
                    @Override
                    public Object apply(Object a, Object b) throws Exception {
                        return a;
                    }
                });
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.scan(0, new BiFunction<Object, Object, Object>() {
                    @Override
                    public Object apply(Object a, Object b) throws Exception {
                        return a;
                    }
                });
            }
        });
    }

    @Test
    public void error() {
        Flowable.error(new TestException()).scan(new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object a, Object b) throws Exception {
                return a;
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void neverSource() {
        Flowable.<Integer>never().scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }).test().assertValue(0).assertNoErrors().assertNotComplete();
    }

    @Test
    public void testUnsubscribeScan() {
        FlowableEventStream.getEventStream("HTTP-ClusterB", 20).scan(new HashMap<String, String>(), new BiFunction<HashMap<String, String>, FlowableEventStream.Event, HashMap<String, String>>() {
            @Override
            public HashMap<String, String> apply(HashMap<String, String> accum, FlowableEventStream.Event perInstanceEvent) {
                accum.put("instance", perInstanceEvent.instanceId);
                return accum;
            }
        }).take(10).blockingForEach(new Consumer<HashMap<String, String>>() {
            @Override
            public void accept(HashMap<String, String> v) {
                System.out.println(v);
            }
        });
    }

    @Test
    public void testScanWithSeedDoesNotEmitErrorTwiceIfScanFunctionThrows() {
        final List<Throwable> list = new CopyOnWriteArrayList<Throwable>();
        Consumer<Throwable> errorConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) throws Exception {
                list.add(t);
            }
        };
        try {
            RxJavaPlugins.setErrorHandler(errorConsumer);
            final RuntimeException e = new RuntimeException();
            final RuntimeException e2 = new RuntimeException();
            items(1).error(e2).scan(0, FlowableScanTest.throwingBiFunction(e)).test().assertValues(0).assertError(e);
            Assert.assertEquals(("" + list), 1, list.size());
            Assert.assertTrue(("" + list), ((list.get(0)) instanceof UndeliverableException));
            Assert.assertEquals(e2, list.get(0).getCause());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void testScanWithSeedDoesNotEmitTerminalEventTwiceIfScanFunctionThrows() {
        final RuntimeException e = new RuntimeException();
        item(1).create().scan(0, FlowableScanTest.throwingBiFunction(e)).test().assertValue(0).assertError(e);
    }

    @Test
    public void testScanWithSeedDoesNotProcessOnNextAfterTerminalEventIfScanFunctionThrows() {
        final RuntimeException e = new RuntimeException();
        final AtomicInteger count = new AtomicInteger();
        items(1, 2).create().scan(0, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer n1, Integer n2) throws Exception {
                count.incrementAndGet();
                throw e;
            }
        }).test().assertValues(0).assertError(e);
        Assert.assertEquals(1, count.get());
    }

    @Test
    public void testScanWithSeedCompletesNormally() {
        Flowable.just(1, 2, 3).scan(0, FlowableScanTest.SUM).test().assertValues(0, 1, 3, 6).assertComplete();
    }

    @Test
    public void testScanWithSeedWhenScanSeedProviderThrows() {
        final RuntimeException e = new RuntimeException();
        Flowable.just(1, 2, 3).scanWith(FlowableScanTest.throwingCallable(e), FlowableScanTest.SUM).test().assertError(e).assertNoValues();
    }

    @Test
    public void testScanNoSeed() {
        Flowable.just(1, 2, 3).scan(FlowableScanTest.SUM).test().assertValues(1, 3, 6).assertComplete();
    }

    @Test
    public void testScanNoSeedDoesNotEmitErrorTwiceIfScanFunctionThrows() {
        final List<Throwable> list = new CopyOnWriteArrayList<Throwable>();
        Consumer<Throwable> errorConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) throws Exception {
                list.add(t);
            }
        };
        try {
            RxJavaPlugins.setErrorHandler(errorConsumer);
            final RuntimeException e = new RuntimeException();
            final RuntimeException e2 = new RuntimeException();
            items(1, 2).error(e2).scan(FlowableScanTest.throwingBiFunction(e)).test().assertValue(1).assertError(e);
            Assert.assertEquals(("" + list), 1, list.size());
            Assert.assertTrue(("" + list), ((list.get(0)) instanceof UndeliverableException));
            Assert.assertEquals(e2, list.get(0).getCause());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void testScanNoSeedDoesNotEmitTerminalEventTwiceIfScanFunctionThrows() {
        final RuntimeException e = new RuntimeException();
        items(1, 2).create().scan(FlowableScanTest.throwingBiFunction(e)).test().assertValue(1).assertError(e);
    }

    @Test
    public void testScanNoSeedDoesNotProcessOnNextAfterTerminalEventIfScanFunctionThrows() {
        final RuntimeException e = new RuntimeException();
        final AtomicInteger count = new AtomicInteger();
        items(1, 2, 3).create().scan(new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer n1, Integer n2) throws Exception {
                count.incrementAndGet();
                throw e;
            }
        }).test().assertValue(1).assertError(e);
        Assert.assertEquals(1, count.get());
    }

    private static final io.reactivex.flowable.BiFunction<Integer, Integer, Integer> SUM = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer t1, Integer t2) throws Exception {
            return t1 + t2;
        }
    };

    @Test
    public void scanEmptyBackpressured() {
        Flowable.<Integer>empty().scan(0, FlowableScanTest.SUM).test(1).assertResult(0);
    }

    @Test
    public void scanErrorBackpressured() {
        Flowable.<Integer>error(new TestException()).scan(0, FlowableScanTest.SUM).test(0).assertFailure(TestException.class);
    }

    @Test
    public void scanTake() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                onComplete();
                cancel();
            }
        };
        Flowable.range(1, 10).scan(0, FlowableScanTest.SUM).subscribe(ts);
        ts.assertResult(0);
    }

    @Test
    public void scanLong() {
        int n = 2 * (Flowable.bufferSize());
        for (int b = 1; b <= n; b *= 2) {
            List<Integer> list = Flowable.range(1, n).scan(0, new BiFunction<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer a, Integer b) throws Exception {
                    return b;
                }
            }).rebatchRequests(b).toList().blockingGet();
            for (int i = 0; i <= n; i++) {
                Assert.assertEquals(i, list.get(i).intValue());
            }
        }
    }
}

