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


import QueueFuseable.ASYNC;
import io.reactivex.Publisher;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.SubscriberFusion;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;

import static io.reactivex.internal.operators.flowable.FlowableZipTest.ArgsToString.INSTANCE;


public class FlowableCombineLatestTest {
    @Test
    public void testCombineLatestWithFunctionThatThrowsAnException() {
        Subscriber<String> w = TestHelper.mockSubscriber();
        PublishProcessor<String> w1 = PublishProcessor.create();
        PublishProcessor<String> w2 = PublishProcessor.create();
        Flowable<String> combined = Flowable.combineLatest(w1, w2, new BiFunction<String, String, String>() {
            @Override
            public String apply(String v1, String v2) {
                throw new RuntimeException("I don't work.");
            }
        });
        combined.subscribe(w);
        w1.onNext("first value of w1");
        w2.onNext("first value of w2");
        Mockito.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
        Mockito.verify(w, Mockito.times(1)).onError(Mockito.<RuntimeException>any());
    }

    @Test
    public void testCombineLatestDifferentLengthFlowableSequences1() {
        Subscriber<String> w = TestHelper.mockSubscriber();
        PublishProcessor<String> w1 = PublishProcessor.create();
        PublishProcessor<String> w2 = PublishProcessor.create();
        PublishProcessor<String> w3 = PublishProcessor.create();
        Flowable<String> combineLatestW = Flowable.combineLatest(w1, w2, w3, getConcat3StringsCombineLatestFunction());
        combineLatestW.subscribe(w);
        /* simulate sending data */
        // once for w1
        w1.onNext("1a");
        w2.onNext("2a");
        w3.onNext("3a");
        w1.onComplete();
        // twice for w2
        w2.onNext("2b");
        w2.onComplete();
        // 4 times for w3
        w3.onNext("3b");
        w3.onNext("3c");
        w3.onNext("3d");
        w3.onComplete();
        /* we should have been called 4 times on the Observer */
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w).onNext("1a2a3a");
        inOrder.verify(w).onNext("1a2b3a");
        inOrder.verify(w).onNext("1a2b3b");
        inOrder.verify(w).onNext("1a2b3c");
        inOrder.verify(w).onNext("1a2b3d");
        inOrder.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testCombineLatestDifferentLengthFlowableSequences2() {
        Subscriber<String> w = TestHelper.mockSubscriber();
        PublishProcessor<String> w1 = PublishProcessor.create();
        PublishProcessor<String> w2 = PublishProcessor.create();
        PublishProcessor<String> w3 = PublishProcessor.create();
        Flowable<String> combineLatestW = Flowable.combineLatest(w1, w2, w3, getConcat3StringsCombineLatestFunction());
        combineLatestW.subscribe(w);
        /* simulate sending data */
        // 4 times for w1
        w1.onNext("1a");
        w1.onNext("1b");
        w1.onNext("1c");
        w1.onNext("1d");
        w1.onComplete();
        // twice for w2
        w2.onNext("2a");
        w2.onNext("2b");
        w2.onComplete();
        // 1 times for w3
        w3.onNext("3a");
        w3.onComplete();
        /* we should have been called 1 time only on the Observer since we only combine the "latest" we don't go back and loop through others once completed */
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w, Mockito.times(1)).onNext("1d2b3a");
        inOrder.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testCombineLatestWithInterleavingSequences() {
        Subscriber<String> w = TestHelper.mockSubscriber();
        PublishProcessor<String> w1 = PublishProcessor.create();
        PublishProcessor<String> w2 = PublishProcessor.create();
        PublishProcessor<String> w3 = PublishProcessor.create();
        Flowable<String> combineLatestW = Flowable.combineLatest(w1, w2, w3, getConcat3StringsCombineLatestFunction());
        combineLatestW.subscribe(w);
        /* simulate sending data */
        w1.onNext("1a");
        w2.onNext("2a");
        w2.onNext("2b");
        w3.onNext("3a");
        w1.onNext("1b");
        w2.onNext("2c");
        w2.onNext("2d");
        w3.onNext("3b");
        w1.onComplete();
        w2.onComplete();
        w3.onComplete();
        /* we should have been called 5 times on the Observer */
        InOrder inOrder = Mockito.inOrder(w);
        inOrder.verify(w).onNext("1a2b3a");
        inOrder.verify(w).onNext("1b2b3a");
        inOrder.verify(w).onNext("1b2c3a");
        inOrder.verify(w).onNext("1b2d3a");
        inOrder.verify(w).onNext("1b2d3b");
        inOrder.verify(w, Mockito.never()).onNext(ArgumentMatchers.anyString());
        onComplete();
    }

    @Test
    public void testCombineLatest2Types() {
        BiFunction<String, Integer, String> combineLatestFunction = getConcatStringIntegerCombineLatestFunction();
        /* define an Observer to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> w = Flowable.combineLatest(Flowable.just("one", "two"), Flowable.just(2, 3, 4), combineLatestFunction);
        w.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two2");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two3");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two4");
    }

    @Test
    public void testCombineLatest3TypesA() {
        Function3<String, Integer, int[], String> combineLatestFunction = getConcatStringIntegerIntArrayCombineLatestFunction();
        /* define an Observer to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> w = Flowable.combineLatest(Flowable.just("one", "two"), Flowable.just(2), Flowable.just(new int[]{ 4, 5, 6 }), combineLatestFunction);
        w.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext("two2[4, 5, 6]");
    }

    @Test
    public void testCombineLatest3TypesB() {
        Function3<String, Integer, int[], String> combineLatestFunction = getConcatStringIntegerIntArrayCombineLatestFunction();
        /* define an Observer to receive aggregated events */
        Subscriber<String> subscriber = TestHelper.mockSubscriber();
        Flowable<String> w = Flowable.combineLatest(Flowable.just("one"), Flowable.just(2), Flowable.just(new int[]{ 4, 5, 6 }, new int[]{ 7, 8 }), combineLatestFunction);
        w.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one2[4, 5, 6]");
        Mockito.verify(subscriber, Mockito.times(1)).onNext("one2[7, 8]");
    }

    BiFunction<Integer, Integer, Integer> or = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer t1, Integer t2) {
            return t1 | t2;
        }
    };

    @Test
    public void combineSimple() {
        PublishProcessor<Integer> a = PublishProcessor.create();
        PublishProcessor<Integer> b = PublishProcessor.create();
        Flowable<Integer> source = Flowable.combineLatest(a, b, or);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.subscribe(subscriber);
        a.onNext(1);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        a.onNext(2);
        inOrder.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        b.onNext(16);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(18);
        b.onNext(32);
        inOrder.verify(subscriber, Mockito.times(1)).onNext(34);
        b.onComplete();
        onComplete();
        a.onComplete();
        onComplete();
        a.onNext(3);
        b.onNext(48);
        a.onComplete();
        b.onComplete();
        inOrder.verifyNoMoreInteractions();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void combineMultipleObservers() {
        PublishProcessor<Integer> a = PublishProcessor.create();
        PublishProcessor<Integer> b = PublishProcessor.create();
        Flowable<Integer> source = Flowable.combineLatest(a, b, or);
        Subscriber<Object> subscriber1 = TestHelper.mockSubscriber();
        Subscriber<Object> subscriber2 = TestHelper.mockSubscriber();
        InOrder inOrder1 = Mockito.inOrder(subscriber1);
        InOrder inOrder2 = Mockito.inOrder(subscriber2);
        source.subscribe(subscriber1);
        source.subscribe(subscriber2);
        a.onNext(1);
        inOrder1.verify(subscriber1, Mockito.never()).onNext(ArgumentMatchers.any());
        inOrder2.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.any());
        a.onNext(2);
        inOrder1.verify(subscriber1, Mockito.never()).onNext(ArgumentMatchers.any());
        inOrder2.verify(subscriber2, Mockito.never()).onNext(ArgumentMatchers.any());
        b.onNext(16);
        inOrder1.verify(subscriber1, Mockito.times(1)).onNext(18);
        inOrder2.verify(subscriber2, Mockito.times(1)).onNext(18);
        b.onNext(32);
        inOrder1.verify(subscriber1, Mockito.times(1)).onNext(34);
        inOrder2.verify(subscriber2, Mockito.times(1)).onNext(34);
        b.onComplete();
        onComplete();
        onComplete();
        a.onComplete();
        onComplete();
        onComplete();
        a.onNext(3);
        b.onNext(48);
        a.onComplete();
        b.onComplete();
        inOrder1.verifyNoMoreInteractions();
        inOrder2.verifyNoMoreInteractions();
        Mockito.verify(subscriber1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testFirstNeverProduces() {
        PublishProcessor<Integer> a = PublishProcessor.create();
        PublishProcessor<Integer> b = PublishProcessor.create();
        Flowable<Integer> source = Flowable.combineLatest(a, b, or);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.subscribe(subscriber);
        b.onNext(16);
        b.onNext(32);
        a.onComplete();
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testSecondNeverProduces() {
        PublishProcessor<Integer> a = PublishProcessor.create();
        PublishProcessor<Integer> b = PublishProcessor.create();
        Flowable<Integer> source = Flowable.combineLatest(a, b, or);
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        InOrder inOrder = Mockito.inOrder(subscriber);
        source.subscribe(subscriber);
        a.onNext(1);
        a.onNext(2);
        b.onComplete();
        a.onComplete();
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test1ToNSources() {
        int n = 30;
        Function<Object[], List<Object>> func = new Function<Object[], List<Object>>() {
            @Override
            public List<Object> apply(Object[] args) {
                return Arrays.asList(args);
            }
        };
        for (int i = 1; i <= n; i++) {
            System.out.println((("test1ToNSources: " + i) + " sources"));
            List<Flowable<Integer>> sources = new ArrayList<Flowable<Integer>>();
            List<Object> values = new ArrayList<Object>();
            for (int j = 0; j < i; j++) {
                sources.add(Flowable.just(j));
                values.add(j);
            }
            Flowable<List<Object>> result = Flowable.combineLatest(sources, func);
            Subscriber<List<Object>> subscriber = TestHelper.mockSubscriber();
            result.subscribe(subscriber);
            Mockito.verify(subscriber).onNext(values);
            onComplete();
            Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test(timeout = 5000)
    public void test1ToNSourcesScheduled() throws InterruptedException {
        int n = 10;
        Function<Object[], List<Object>> func = new Function<Object[], List<Object>>() {
            @Override
            public List<Object> apply(Object[] args) {
                return Arrays.asList(args);
            }
        };
        for (int i = 1; i <= n; i++) {
            System.out.println((("test1ToNSourcesScheduled: " + i) + " sources"));
            List<Flowable<Integer>> sources = new ArrayList<Flowable<Integer>>();
            List<Object> values = new ArrayList<Object>();
            for (int j = 0; j < i; j++) {
                sources.add(Flowable.just(j).subscribeOn(Schedulers.io()));
                values.add(j);
            }
            Flowable<List<Object>> result = Flowable.combineLatest(sources, func);
            final Subscriber<List<Object>> subscriber = TestHelper.mockSubscriber();
            final CountDownLatch cdl = new CountDownLatch(1);
            Subscriber<List<Object>> s = new DefaultSubscriber<List<Object>>() {
                @Override
                public void onNext(List<Object> t) {
                    subscriber.onNext(t);
                }

                @Override
                public void onError(Throwable e) {
                    subscriber.onError(e);
                    cdl.countDown();
                }

                @Override
                public void onComplete() {
                    subscriber.onComplete();
                    cdl.countDown();
                }
            };
            result.subscribe(s);
            cdl.await();
            Mockito.verify(subscriber).onNext(values);
            onComplete();
            Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        }
    }

    @Test
    public void test2SourcesOverload() {
        Flowable<Integer> s1 = Flowable.just(1);
        Flowable<Integer> s2 = Flowable.just(2);
        Flowable<List<Integer>> result = Flowable.combineLatest(s1, s2, new BiFunction<Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2) {
                return Arrays.asList(t1, t2);
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber).onNext(Arrays.asList(1, 2));
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test3SourcesOverload() {
        Flowable<Integer> s1 = Flowable.just(1);
        Flowable<Integer> s2 = Flowable.just(2);
        Flowable<Integer> s3 = Flowable.just(3);
        Flowable<List<Integer>> result = Flowable.combineLatest(s1, s2, s3, new Function3<Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3) {
                return Arrays.asList(t1, t2, t3);
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber).onNext(Arrays.asList(1, 2, 3));
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test4SourcesOverload() {
        Flowable<Integer> s1 = Flowable.just(1);
        Flowable<Integer> s2 = Flowable.just(2);
        Flowable<Integer> s3 = Flowable.just(3);
        Flowable<Integer> s4 = Flowable.just(4);
        Flowable<List<Integer>> result = Flowable.combineLatest(s1, s2, s3, s4, new Function4<Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4) {
                return Arrays.asList(t1, t2, t3, t4);
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber).onNext(Arrays.asList(1, 2, 3, 4));
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test5SourcesOverload() {
        Flowable<Integer> s1 = Flowable.just(1);
        Flowable<Integer> s2 = Flowable.just(2);
        Flowable<Integer> s3 = Flowable.just(3);
        Flowable<Integer> s4 = Flowable.just(4);
        Flowable<Integer> s5 = Flowable.just(5);
        Flowable<List<Integer>> result = Flowable.combineLatest(s1, s2, s3, s4, s5, new Function5<Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4, Integer t5) {
                return Arrays.asList(t1, t2, t3, t4, t5);
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber).onNext(Arrays.asList(1, 2, 3, 4, 5));
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test6SourcesOverload() {
        Flowable<Integer> s1 = Flowable.just(1);
        Flowable<Integer> s2 = Flowable.just(2);
        Flowable<Integer> s3 = Flowable.just(3);
        Flowable<Integer> s4 = Flowable.just(4);
        Flowable<Integer> s5 = Flowable.just(5);
        Flowable<Integer> s6 = Flowable.just(6);
        Flowable<List<Integer>> result = Flowable.combineLatest(s1, s2, s3, s4, s5, s6, new Function6<Integer, Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4, Integer t5, Integer t6) {
                return Arrays.asList(t1, t2, t3, t4, t5, t6);
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber).onNext(Arrays.asList(1, 2, 3, 4, 5, 6));
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test7SourcesOverload() {
        Flowable<Integer> s1 = Flowable.just(1);
        Flowable<Integer> s2 = Flowable.just(2);
        Flowable<Integer> s3 = Flowable.just(3);
        Flowable<Integer> s4 = Flowable.just(4);
        Flowable<Integer> s5 = Flowable.just(5);
        Flowable<Integer> s6 = Flowable.just(6);
        Flowable<Integer> s7 = Flowable.just(7);
        Flowable<List<Integer>> result = Flowable.combineLatest(s1, s2, s3, s4, s5, s6, s7, new Function7<Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4, Integer t5, Integer t6, Integer t7) {
                return Arrays.asList(t1, t2, t3, t4, t5, t6, t7);
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber).onNext(Arrays.asList(1, 2, 3, 4, 5, 6, 7));
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test8SourcesOverload() {
        Flowable<Integer> s1 = Flowable.just(1);
        Flowable<Integer> s2 = Flowable.just(2);
        Flowable<Integer> s3 = Flowable.just(3);
        Flowable<Integer> s4 = Flowable.just(4);
        Flowable<Integer> s5 = Flowable.just(5);
        Flowable<Integer> s6 = Flowable.just(6);
        Flowable<Integer> s7 = Flowable.just(7);
        Flowable<Integer> s8 = Flowable.just(8);
        Flowable<List<Integer>> result = Flowable.combineLatest(s1, s2, s3, s4, s5, s6, s7, s8, new Function8<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4, Integer t5, Integer t6, Integer t7, Integer t8) {
                return Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8);
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber).onNext(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void test9SourcesOverload() {
        Flowable<Integer> s1 = Flowable.just(1);
        Flowable<Integer> s2 = Flowable.just(2);
        Flowable<Integer> s3 = Flowable.just(3);
        Flowable<Integer> s4 = Flowable.just(4);
        Flowable<Integer> s5 = Flowable.just(5);
        Flowable<Integer> s6 = Flowable.just(6);
        Flowable<Integer> s7 = Flowable.just(7);
        Flowable<Integer> s8 = Flowable.just(8);
        Flowable<Integer> s9 = Flowable.just(9);
        Flowable<List<Integer>> result = Flowable.combineLatest(s1, s2, s3, s4, s5, s6, s7, s8, s9, new Function9<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, Integer, List<Integer>>() {
            @Override
            public List<Integer> apply(Integer t1, Integer t2, Integer t3, Integer t4, Integer t5, Integer t6, Integer t7, Integer t8, Integer t9) {
                return Arrays.asList(t1, t2, t3, t4, t5, t6, t7, t8, t9);
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        Mockito.verify(subscriber).onNext(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testZeroSources() {
        Flowable<Object> result = Flowable.combineLatest(Collections.<Flowable<Object>>emptyList(), new Function<Object[], Object>() {
            @Override
            public Object apply(Object[] args) {
                return args;
            }
        });
        Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        result.subscribe(subscriber);
        onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testBackpressureLoop() {
        for (int i = 0; i < 5000; i++) {
            testBackpressure();
        }
    }

    // (timeout = 2000)
    @Test
    public void testBackpressure() {
        BiFunction<String, Integer, String> combineLatestFunction = getConcatStringIntegerCombineLatestFunction();
        int num = (Flowable.bufferSize()) * 4;
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Flowable.combineLatest(Flowable.just("one", "two"), Flowable.range(2, num), combineLatestFunction).observeOn(Schedulers.computation()).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        List<String> events = ts.values();
        Assert.assertEquals("two2", events.get(0));
        Assert.assertEquals("two3", events.get(1));
        Assert.assertEquals("two4", events.get(2));
        Assert.assertEquals(num, events.size());
    }

    @Test
    public void testWithCombineLatestIssue1717() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger count = new AtomicInteger();
        final int SIZE = 2000;
        Flowable<Long> timer = Flowable.interval(0, 1, TimeUnit.MILLISECONDS).observeOn(Schedulers.newThread()).doOnEach(new Consumer<Notification<Long>>() {
            @Override
            public void accept(Notification<Long> n) {
                // System.out.println(n);
                if ((count.incrementAndGet()) >= SIZE) {
                    latch.countDown();
                }
            }
        }).take(SIZE);
        TestSubscriber<Long> ts = new TestSubscriber<Long>();
        Flowable.combineLatest(timer, Flowable.<Integer>never(), new BiFunction<Long, Integer, Long>() {
            @Override
            public Long apply(Long t1, Integer t2) {
                return t1;
            }
        }).subscribe(ts);
        if (!(latch.await((SIZE + 2000), TimeUnit.MILLISECONDS))) {
            Assert.fail("timed out");
        }
        Assert.assertEquals(SIZE, count.get());
    }

    @Test(timeout = 10000)
    public void testCombineLatestRequestOverflow() throws InterruptedException {
        @SuppressWarnings("unchecked")
        List<Flowable<Integer>> sources = Arrays.asList(Flowable.fromArray(1, 2, 3, 4), Flowable.fromArray(5, 6, 7, 8));
        Flowable<Integer> f = Flowable.combineLatest(sources, new Function<Object[], Integer>() {
            @Override
            public Integer apply(Object[] args) {
                return ((Integer) (args[0]));
            }
        });
        // should get at least 4
        final CountDownLatch latch = new CountDownLatch(4);
        f.subscribeOn(Schedulers.computation()).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(2);
            }

            @Override
            public void onComplete() {
                // ignore
            }

            @Override
            public void onError(Throwable e) {
                throw new RuntimeException(e);
            }

            @Override
            public void onNext(Integer t) {
                latch.countDown();
                request(((Long.MAX_VALUE) - 1));
            }
        });
        Assert.assertTrue(latch.await(10, TimeUnit.SECONDS));
    }

    private static final io.reactivex.Function<Object[], Integer> THROW_NON_FATAL = new Function<Object[], Integer>() {
        @Override
        public Integer apply(Object[] args) {
            throw new RuntimeException();
        }
    };

    @Test
    public void testNonFatalExceptionThrownByCombinatorForSingleSourceIsNotReportedByUpstreamOperator() {
        final AtomicBoolean errorOccurred = new AtomicBoolean(false);
        TestSubscriber<Integer> ts = TestSubscriber.create(1);
        Flowable<Integer> source = // if haven't caught exception in combineLatest operator then would incorrectly
        // be picked up by this call to doOnError
        Flowable.just(1).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t) {
                errorOccurred.set(true);
            }
        });
        Flowable.combineLatest(Collections.singletonList(source), FlowableCombineLatestTest.THROW_NON_FATAL).subscribe(ts);
        Assert.assertFalse(errorOccurred.get());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combineLatestIterable() {
        Flowable<Integer> source = Flowable.just(1);
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.combineLatest(Arrays.asList(source, source), new Function<Object[], Integer>() {
            @Override
            public Integer apply(Object[] args) {
                return ((Integer) (args[0])) + ((Integer) (args[1]));
            }
        }).subscribe(ts);
        ts.assertValue(2);
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @Test
    public void testCombineMany() {
        int n = (Flowable.bufferSize()) * 3;
        List<Flowable<Integer>> sources = new ArrayList<Flowable<Integer>>();
        StringBuilder expected = new StringBuilder((n * 2));
        for (int i = 0; i < n; i++) {
            sources.add(Flowable.just(i));
            expected.append(i);
        }
        TestSubscriber<String> ts = TestSubscriber.create();
        Flowable.combineLatest(sources, new Function<Object[], String>() {
            @Override
            public String apply(Object[] args) {
                StringBuilder b = new StringBuilder();
                for (Object o : args) {
                    b.append(o);
                }
                return b.toString();
            }
        }).subscribe(ts);
        ts.assertNoErrors();
        ts.assertValue(expected.toString());
        ts.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void firstJustError() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.combineLatestDelayError(Arrays.asList(Flowable.just(1), Flowable.<Integer>error(new TestException())), new Function<Object[], Integer>() {
            @Override
            public Integer apply(Object[] args) {
                return ((Integer) (args[0])) + ((Integer) (args[1]));
            }
        }).subscribe(ts);
        ts.assertNoValues();
        ts.assertError(TestException.class);
        ts.assertNotComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void secondJustError() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.combineLatestDelayError(Arrays.asList(Flowable.<Integer>error(new TestException()), Flowable.just(1)), new Function<Object[], Integer>() {
            @Override
            public Integer apply(Object[] args) {
                return ((Integer) (args[0])) + ((Integer) (args[1]));
            }
        }).subscribe(ts);
        ts.assertNoValues();
        ts.assertError(TestException.class);
        ts.assertNotComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void oneErrors() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.combineLatestDelayError(Arrays.asList(Flowable.just(10).concatWith(Flowable.<Integer>error(new TestException())), Flowable.just(1)), new Function<Object[], Integer>() {
            @Override
            public Integer apply(Object[] args) {
                return ((Integer) (args[0])) + ((Integer) (args[1]));
            }
        }).subscribe(ts);
        ts.assertValues(11);
        ts.assertError(TestException.class);
        ts.assertNotComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void twoErrors() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.combineLatestDelayError(Arrays.asList(Flowable.just(1), Flowable.just(10).concatWith(Flowable.<Integer>error(new TestException()))), new Function<Object[], Integer>() {
            @Override
            public Integer apply(Object[] args) {
                return ((Integer) (args[0])) + ((Integer) (args[1]));
            }
        }).subscribe(ts);
        ts.assertValues(11);
        ts.assertError(TestException.class);
        ts.assertNotComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void bothError() {
        TestSubscriber<Integer> ts = TestSubscriber.create();
        Flowable.combineLatestDelayError(Arrays.asList(Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())), Flowable.just(10).concatWith(Flowable.<Integer>error(new TestException()))), new Function<Object[], Integer>() {
            @Override
            public Integer apply(Object[] args) {
                return ((Integer) (args[0])) + ((Integer) (args[1]));
            }
        }).subscribe(ts);
        ts.assertValues(11);
        ts.assertError(CompositeException.class);
        ts.assertNotComplete();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void combineLatestNArguments() throws Exception {
        Flowable source = Flowable.just(1);
        for (int i = 2; i < 10; i++) {
            Class<?>[] types = new Class[i + 1];
            Arrays.fill(types, Publisher.class);
            types[i] = (i == 2) ? BiFunction.class : Class.forName(("io.reactivex.functions.Function" + i));
            Method m = io.reactivex.Flowable.class.getMethod("combineLatest", types);
            Object[] params = new Object[i + 1];
            Arrays.fill(params, source);
            params[i] = INSTANCE;
            StringBuilder b = new StringBuilder();
            for (int j = 0; j < i; j++) {
                b.append('1');
            }
            test().assertResult(b.toString());
            for (int j = 0; j < (params.length); j++) {
                Object[] params0 = params.clone();
                params0[j] = null;
                try {
                    m.invoke(null, params0);
                    Assert.fail(("Should have thrown @ " + m));
                } catch (InvocationTargetException ex) {
                    Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof NullPointerException));
                    if (j < i) {
                        Assert.assertEquals((("source" + (j + 1)) + " is null"), ex.getCause().getMessage());
                    } else {
                        Assert.assertEquals("f is null", ex.getCause().getMessage());
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combineLatestNSources() {
        for (int i = 1; i < 100; i++) {
            io.reactivex.Flowable<Integer>[] sources = new Flowable[i];
            Arrays.fill(sources, Flowable.just(1));
            List<Object> expected = new ArrayList<Object>(i);
            for (int j = 1; j <= i; j++) {
                expected.add(1);
            }
            test().assertResult(expected);
            test().assertResult(expected);
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combineLatestArrayOfSources() {
        test().assertResult("[1, 2]");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void combineLatestDelayErrorArrayOfSources() {
        test().assertResult("[1, 2]");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void combineLatestDelayErrorArrayOfSourcesWithError() {
        test().assertFailure(TestException.class, "[1, 2]");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void combineLatestDelayErrorIterableOfSources() {
        test().assertResult("[1, 2]");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void combineLatestDelayErrorIterableOfSourcesWithError() {
        test().assertFailure(TestException.class, "[1, 2]");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combineLatestEmpty() {
        Assert.assertSame(Flowable.empty(), Flowable.combineLatest(new Flowable[0], Functions.<Object[]>identity(), 16));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combineLatestDelayErrorEmpty() {
        Assert.assertSame(Flowable.empty(), Flowable.combineLatestDelayError(new Flowable[0], Functions.<Object[]>identity(), 16));
    }

    @Test
    public void error() {
        test().assertFailure(TestException.class);
    }

    @Test
    public void disposed() {
        TestHelper.checkDisposed(Flowable.combineLatest(Flowable.never(), Flowable.never(), new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object a, Object b) throws Exception {
                return a;
            }
        }));
    }

    @Test
    public void cancelWhileSubscribing() {
        final TestSubscriber<Object> ts = new TestSubscriber<Object>();
        Flowable.combineLatest(Flowable.just(1).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                ts.cancel();
            }
        }), Flowable.never(), new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object a, Object b) throws Exception {
                return a;
            }
        }).subscribe(ts);
    }

    @Test
    public void onErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                final PublishProcessor<Integer> pp1 = PublishProcessor.create();
                final PublishProcessor<Integer> pp2 = PublishProcessor.create();
                TestSubscriber<Integer> ts = Flowable.combineLatest(pp1, pp2, new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer a, Integer b) throws Exception {
                        return a;
                    }
                }).test();
                final TestException ex1 = new TestException();
                final TestException ex2 = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        pp1.onError(ex1);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        pp2.onError(ex2);
                    }
                };
                TestHelper.race(r1, r2);
                if ((ts.errorCount()) != 0) {
                    if ((ts.errors().get(0)) instanceof CompositeException) {
                        ts.assertSubscribed().assertNotComplete().assertNoValues();
                        for (Throwable e : TestHelper.errorList(ts)) {
                            Assert.assertTrue(e.toString(), (e instanceof TestException));
                        }
                    } else {
                        ts.assertFailure(TestException.class);
                    }
                }
                for (Throwable e : errors) {
                    Assert.assertTrue(e.toString(), ((e.getCause()) instanceof TestException));
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void combineAsync() {
        Flowable<Integer> source = Flowable.range(1, 1000).subscribeOn(Schedulers.computation());
        test().awaitDone(5, TimeUnit.SECONDS).assertNoErrors().assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorDelayed() {
        test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorDelayed2() {
        test().assertFailure(TestException.class);
    }

    @Test
    public void dontSubscribeIfDone() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final int[] count = new int[]{ 0 };
            test().assertResult();
            Assert.assertEquals(0, count[0]);
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void dontSubscribeIfDone2() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final int[] count = new int[]{ 0 };
            test().assertResult();
            Assert.assertEquals(0, count[0]);
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void combine2Flowable2Errors() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            TestSubscriber<Object> testObserver = TestSubscriber.create();
            TestScheduler testScheduler = new TestScheduler();
            Flowable<Integer> emptyFlowable = Flowable.timer(10, TimeUnit.MILLISECONDS, testScheduler).flatMap(new Function<Long, Publisher<Integer>>() {
                @Override
                public Publisher<Integer> apply(Long aLong) throws Exception {
                    return Flowable.error(new Exception());
                }
            });
            Flowable<Object> errorFlowable = Flowable.timer(100, TimeUnit.MILLISECONDS, testScheduler).map(new Function<Long, Object>() {
                @Override
                public Object apply(Long aLong) throws Exception {
                    throw new Exception();
                }
            });
            Flowable.combineLatestDelayError(Arrays.asList(emptyFlowable.doOnEach(new Consumer<Notification<Integer>>() {
                @Override
                public void accept(Notification<Integer> integerNotification) throws Exception {
                    System.out.println(("emptyFlowable: " + integerNotification));
                }
            }).doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    System.out.println("emptyFlowable: doFinally");
                }
            }), errorFlowable.doOnEach(new Consumer<Notification<Object>>() {
                @Override
                public void accept(Notification<Object> integerNotification) throws Exception {
                    System.out.println(("errorFlowable: " + integerNotification));
                }
            }).doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    System.out.println("errorFlowable: doFinally");
                }
            })), new Function<Object[], Object>() {
                @Override
                public Object apply(Object[] objects) throws Exception {
                    return 0;
                }
            }).doOnEach(new Consumer<Notification<Object>>() {
                @Override
                public void accept(Notification<Object> integerNotification) throws Exception {
                    System.out.println(("combineLatestDelayError: " + integerNotification));
                }
            }).doFinally(new Action() {
                @Override
                public void run() throws Exception {
                    System.out.println("combineLatestDelayError: doFinally");
                }
            }).subscribe(testObserver);
            testScheduler.advanceTimeBy(100, TimeUnit.MILLISECONDS);
            testObserver.awaitTerminalEvent();
            Assert.assertTrue(errors.toString(), errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void eagerDispose() {
        final PublishProcessor<Integer> pp1 = PublishProcessor.create();
        final PublishProcessor<Integer> pp2 = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                cancel();
                if (pp1.hasSubscribers()) {
                    onError(new IllegalStateException("pp1 not disposed"));
                } else
                    if (pp2.hasSubscribers()) {
                        onError(new IllegalStateException("pp2 not disposed"));
                    } else {
                        onComplete();
                    }

            }
        };
        Flowable.combineLatest(pp1, pp2, new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) throws Exception {
                return t1 + t2;
            }
        }).subscribe(ts);
        pp1.onNext(1);
        pp2.onNext(2);
        ts.assertResult(3);
    }

    @Test
    public void fusedNullCheck() {
        TestSubscriber<Integer> ts = SubscriberFusion.newTest(ASYNC);
        Flowable.combineLatest(Flowable.just(1), Flowable.just(2), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) throws Exception {
                return null;
            }
        }).subscribe(ts);
        ts.assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertFailureAndMessage(NullPointerException.class, "The combiner returned a null value");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void syncFirstErrorsAfterItemDelayError() {
        test().awaitDone(5, TimeUnit.SECONDS).assertFailure(TestException.class, 42);
    }
}

