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


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.Flowable;
import io.reactivex.Subscriber;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.SubscriberFusion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static QueueFuseable.ANY;
import static QueueFuseable.BOUNDARY;


public class FlowableMapTest {
    Subscriber<String> stringSubscriber;

    Subscriber<String> stringSubscriber2;

    static final BiFunction<String, Integer, String> APPEND_INDEX = new BiFunction<String, Integer, String>() {
        @Override
        public String apply(String value, Integer index) {
            return value + index;
        }
    };

    @Test
    public void testMap() {
        Map<String, String> m1 = FlowableMapTest.getMap("One");
        Map<String, String> m2 = FlowableMapTest.getMap("Two");
        Flowable<Map<String, String>> flowable = Flowable.just(m1, m2);
        Flowable<String> m = flowable.map(new Function<Map<String, String>, String>() {
            @Override
            public String apply(Map<String, String> map) {
                return map.get("firstName");
            }
        });
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("OneFirst");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("TwoFirst");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testMapMany() {
        /* simulate a top-level async call which returns IDs */
        Flowable<Integer> ids = Flowable.just(1, 2);
        /* now simulate the behavior to take those IDs and perform nested async calls based on them */
        Flowable<String> m = ids.flatMap(new Function<Integer, Flowable<String>>() {
            @Override
            public Flowable<String> apply(Integer id) {
                /* simulate making a nested async call which creates another Flowable */
                Flowable<Map<String, String>> subFlowable = null;
                if (id == 1) {
                    Map<String, String> m1 = FlowableMapTest.getMap("One");
                    Map<String, String> m2 = FlowableMapTest.getMap("Two");
                    subFlowable = Flowable.just(m1, m2);
                } else {
                    Map<String, String> m3 = FlowableMapTest.getMap("Three");
                    Map<String, String> m4 = FlowableMapTest.getMap("Four");
                    subFlowable = Flowable.just(m3, m4);
                }
                /* simulate kicking off the async call and performing a select on it to transform the data */
                return subFlowable.map(new Function<Map<String, String>, String>() {
                    @Override
                    public String apply(Map<String, String> map) {
                        return map.get("firstName");
                    }
                });
            }
        });
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("OneFirst");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("TwoFirst");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("ThreeFirst");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("FourFirst");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testMapMany2() {
        Map<String, String> m1 = FlowableMapTest.getMap("One");
        Map<String, String> m2 = FlowableMapTest.getMap("Two");
        Flowable<Map<String, String>> flowable1 = Flowable.just(m1, m2);
        Map<String, String> m3 = FlowableMapTest.getMap("Three");
        Map<String, String> m4 = FlowableMapTest.getMap("Four");
        Flowable<Map<String, String>> flowable2 = Flowable.just(m3, m4);
        Flowable<Flowable<Map<String, String>>> f = Flowable.just(flowable1, flowable2);
        Flowable<String> m = f.flatMap(new Function<Flowable<Map<String, String>>, Flowable<String>>() {
            @Override
            public Flowable<String> apply(Flowable<Map<String, String>> f) {
                return f.map(new Function<Map<String, String>, String>() {
                    @Override
                    public String apply(Map<String, String> map) {
                        return map.get("firstName");
                    }
                });
            }
        });
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("OneFirst");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("TwoFirst");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("ThreeFirst");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("FourFirst");
        Mockito.verify(stringSubscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testMapWithError() {
        final List<Throwable> errors = new ArrayList<Throwable>();
        Flowable<String> w = Flowable.just("one", "fail", "two", "three", "fail");
        Flowable<String> m = w.map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                if ("fail".equals(s)) {
                    throw new TestException("Forced Failure");
                }
                return s;
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t1) {
                errors.add(t1);
            }
        });
        m.subscribe(stringSubscriber);
        Mockito.verify(stringSubscriber, Mockito.times(1)).onNext("one");
        Mockito.verify(stringSubscriber, Mockito.never()).onNext("two");
        Mockito.verify(stringSubscriber, Mockito.never()).onNext("three");
        Mockito.verify(stringSubscriber, Mockito.never()).onComplete();
        Mockito.verify(stringSubscriber, Mockito.times(1)).onError(ArgumentMatchers.any(TestException.class));
        TestHelper.assertError(errors, 0, TestException.class, "Forced Failure");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapWithIssue417() {
        Flowable.just(1).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer arg0) {
                throw new IllegalArgumentException("any error");
            }
        }).blockingSingle();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapWithErrorInFuncAndThreadPoolScheduler() throws InterruptedException {
        // The error will throw in one of threads in the thread pool.
        // If map does not handle it, the error will disappear.
        // so map needs to handle the error by itself.
        Flowable<String> m = Flowable.just("one").observeOn(Schedulers.computation()).map(new Function<String, String>() {
            @Override
            public String apply(String arg0) {
                throw new IllegalArgumentException("any error");
            }
        });
        // block for response, expecting exception thrown
        m.blockingLast();
    }

    /**
     * While mapping over range(1,0).last() we expect NoSuchElementException since the sequence is empty.
     */
    @Test
    public void testErrorPassesThruMap() {
        Assert.assertNull(Flowable.range(1, 0).lastElement().map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return i;
            }
        }).blockingGet());
    }

    /**
     * We expect IllegalStateException to pass thru map.
     */
    @Test(expected = IllegalStateException.class)
    public void testErrorPassesThruMap2() {
        Flowable.error(new IllegalStateException()).map(new Function<Object, Object>() {
            @Override
            public Object apply(Object i) {
                return i;
            }
        }).blockingSingle();
    }

    /**
     * We expect an ArithmeticException exception here because last() emits a single value
     * but then we divide by 0.
     */
    @Test(expected = ArithmeticException.class)
    public void testMapWithErrorInFunc() {
        Flowable.range(1, 1).lastElement().map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return i / 0;
            }
        }).blockingGet();
    }

    @Test
    public void functionCrashUnsubscribes() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        pp.map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                throw new TestException();
            }
        }).subscribe(ts);
        Assert.assertTrue("Not subscribed?", pp.hasSubscribers());
        pp.onNext(1);
        Assert.assertFalse("Subscribed?", pp.hasSubscribers());
        ts.assertError(TestException.class);
    }

    @Test
    public void mapFilter() {
        Flowable.range(1, 2).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                return v + 1;
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return true;
            }
        }).test().assertResult(2, 3);
    }

    @Test
    public void mapFilterMapperCrash() {
        Flowable.range(1, 2).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return true;
            }
        }).test().assertFailure(TestException.class);
    }

    @Test
    public void mapFilterHidden() {
        Flowable.range(1, 2).hide().map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                return v + 1;
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return true;
            }
        }).test().assertResult(2, 3);
    }

    @Test
    public void mapFilterFused() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        Flowable.range(1, 2).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                return v + 1;
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return true;
            }
        }).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(SYNC)).assertResult(2, 3);
    }

    @Test
    public void mapFilterFusedHidden() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        Flowable.range(1, 2).hide().map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                return v + 1;
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return true;
            }
        }).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(NONE)).assertResult(2, 3);
    }

    @Test
    public void sourceIgnoresCancel() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.fromPublisher(new Publisher<Integer>() {
                @Override
                public void subscribe(Subscriber<? super Integer> s) {
                    s.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    s.onNext(1);
                    s.onNext(2);
                    s.onError(new IOException());
                    s.onComplete();
                }
            }).map(new Function<Integer, Object>() {
                @Override
                public Object apply(Integer v) throws Exception {
                    throw new TestException();
                }
            }).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void mapFilterMapperCrashFused() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        Flowable.range(1, 2).hide().map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                throw new TestException();
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return true;
            }
        }).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(NONE)).assertFailure(TestException.class);
    }

    @Test
    public void sourceIgnoresCancelFilter() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.fromPublisher(new Publisher<Integer>() {
                @Override
                public void subscribe(Subscriber<? super Integer> s) {
                    s.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    s.onNext(1);
                    s.onNext(2);
                    s.onError(new IOException());
                    s.onComplete();
                }
            }).map(new Function<Integer, Integer>() {
                @Override
                public Integer apply(Integer v) throws Exception {
                    throw new TestException();
                }
            }).filter(new Predicate<Integer>() {
                @Override
                public boolean test(Integer v) throws Exception {
                    return true;
                }
            }).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void mapFilterFused2() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        UnicastProcessor<Integer> up = UnicastProcessor.create();
        up.map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) throws Exception {
                return v + 1;
            }
        }).filter(new Predicate<Integer>() {
            @Override
            public boolean test(Integer v) throws Exception {
                return true;
            }
        }).subscribe(ts);
        up.onNext(1);
        up.onNext(2);
        up.onComplete();
        ts.assertOf(SubscriberFusion.<Integer>assertFuseable()).assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertResult(2, 3);
    }

    @Test
    public void sourceIgnoresCancelConditional() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.fromPublisher(new Publisher<Integer>() {
                @Override
                public void subscribe(Subscriber<? super Integer> s) {
                    ConditionalSubscriber<? super Integer> cs = ((ConditionalSubscriber<? super Integer>) (s));
                    cs.onSubscribe(new io.reactivex.internal.subscriptions.BooleanSubscription());
                    cs.tryOnNext(1);
                    cs.tryOnNext(2);
                    cs.onError(new IOException());
                    cs.onComplete();
                }
            }).map(new Function<Integer, Integer>() {
                @Override
                public Integer apply(Integer v) throws Exception {
                    throw new TestException();
                }
            }).filter(new Predicate<Integer>() {
                @Override
                public boolean test(Integer v) throws Exception {
                    return true;
                }
            }).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.range(1, 5).map(Functions.identity()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Object>>() {
            @Override
            public Flowable<Object> apply(Flowable<Object> f) throws Exception {
                return f.map(Functions.identity());
            }
        });
    }

    @Test
    public void fusedSync() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        Flowable.range(1, 5).map(Functions.<Integer>identity()).subscribe(ts);
        SubscriberFusion.assertFusion(ts, SYNC).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void fusedAsync() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ANY);
        UnicastProcessor<Integer> us = UnicastProcessor.create();
        us.map(Functions.<Integer>identity()).subscribe(ts);
        TestHelper.emit(us, 1, 2, 3, 4, 5);
        SubscriberFusion.assertFusion(ts, ASYNC).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void fusedReject() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(((ANY) | (BOUNDARY)));
        Flowable.range(1, 5).map(Functions.<Integer>identity()).subscribe(ts);
        SubscriberFusion.assertFusion(ts, NONE).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void badSource() {
        TestHelper.checkBadSourceFlowable(new Function<Flowable<Object>, Object>() {
            @Override
            public Object apply(Flowable<Object> f) throws Exception {
                return f.map(Functions.identity());
            }
        }, false, 1, 1, 1);
    }
}

