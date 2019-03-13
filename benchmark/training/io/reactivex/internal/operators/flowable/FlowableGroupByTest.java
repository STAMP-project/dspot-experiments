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
import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import io.reactivex.Function;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.flowables.GroupedFlowable;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subscribers.SubscriberFusion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


public class FlowableGroupByTest {
    final Function<String, Integer> length = new Function<String, Integer>() {
        @Override
        public Integer apply(String s) {
            return s.length();
        }
    };

    @Test
    public void testGroupBy() {
        Flowable<String> source = Flowable.just("one", "two", "three", "four", "five", "six");
        Flowable<GroupedFlowable<Integer, String>> grouped = source.groupBy(length);
        Map<Integer, Collection<String>> map = FlowableGroupByTest.toMap(grouped);
        Assert.assertEquals(3, map.size());
        Assert.assertArrayEquals(Arrays.asList("one", "two", "six").toArray(), map.get(3).toArray());
        Assert.assertArrayEquals(Arrays.asList("four", "five").toArray(), map.get(4).toArray());
        Assert.assertArrayEquals(Arrays.asList("three").toArray(), map.get(5).toArray());
    }

    @Test
    public void testGroupByWithElementSelector() {
        Flowable<String> source = Flowable.just("one", "two", "three", "four", "five", "six");
        Flowable<GroupedFlowable<Integer, Integer>> grouped = source.groupBy(length, length);
        Map<Integer, Collection<Integer>> map = FlowableGroupByTest.toMap(grouped);
        Assert.assertEquals(3, map.size());
        Assert.assertArrayEquals(Arrays.asList(3, 3, 3).toArray(), map.get(3).toArray());
        Assert.assertArrayEquals(Arrays.asList(4, 4).toArray(), map.get(4).toArray());
        Assert.assertArrayEquals(Arrays.asList(5).toArray(), map.get(5).toArray());
    }

    @Test
    public void testGroupByWithElementSelector2() {
        Flowable<String> source = Flowable.just("one", "two", "three", "four", "five", "six");
        Flowable<GroupedFlowable<Integer, Integer>> grouped = source.groupBy(length, length);
        Map<Integer, Collection<Integer>> map = FlowableGroupByTest.toMap(grouped);
        Assert.assertEquals(3, map.size());
        Assert.assertArrayEquals(Arrays.asList(3, 3, 3).toArray(), map.get(3).toArray());
        Assert.assertArrayEquals(Arrays.asList(4, 4).toArray(), map.get(4).toArray());
        Assert.assertArrayEquals(Arrays.asList(5).toArray(), map.get(5).toArray());
    }

    @Test
    public void testEmpty() {
        Flowable<String> source = Flowable.empty();
        Flowable<GroupedFlowable<Integer, String>> grouped = source.groupBy(length);
        Map<Integer, Collection<String>> map = FlowableGroupByTest.toMap(grouped);
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testError() {
        Flowable<String> sourceStrings = Flowable.just("one", "two", "three", "four", "five", "six");
        Flowable<String> errorSource = Flowable.error(new TestException("forced failure"));
        Flowable<String> source = Flowable.concat(sourceStrings, errorSource);
        Flowable<GroupedFlowable<Integer, String>> grouped = source.groupBy(length);
        final AtomicInteger groupCounter = new AtomicInteger();
        final AtomicInteger eventCounter = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        grouped.flatMap(new Function<GroupedFlowable<Integer, String>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final GroupedFlowable<Integer, String> f) {
                groupCounter.incrementAndGet();
                return f.map(new Function<String, String>() {
                    @Override
                    public String apply(String v) {
                        return (("Event => key: " + (f.getKey())) + " value: ") + v;
                    }
                });
            }
        }).subscribe(new DefaultSubscriber<String>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                // e.printStackTrace();
                error.set(e);
            }

            @Override
            public void onNext(String v) {
                eventCounter.incrementAndGet();
                System.out.println(v);
            }
        });
        Assert.assertEquals(3, groupCounter.get());
        Assert.assertEquals(6, eventCounter.get());
        Assert.assertNotNull(error.get());
        Assert.assertTrue(("" + (error.get())), ((error.get()) instanceof TestException));
        Assert.assertEquals(error.get().getMessage(), "forced failure");
    }

    /**
     * Assert that only a single subscription to a stream occurs and that all events are received.
     *
     * @throws Throwable
     * 		some method call is declared throws
     */
    @Test
    public void testGroupedEventStream() throws Throwable {
        final AtomicInteger eventCounter = new AtomicInteger();
        final AtomicInteger subscribeCounter = new AtomicInteger();
        final AtomicInteger groupCounter = new AtomicInteger();
        final CountDownLatch latch = new CountDownLatch(1);
        final int count = 100;
        final int groupCount = 2;
        Flowable<FlowableGroupByTest.Event> es = Flowable.unsafeCreate(new Publisher<FlowableGroupByTest.Event>() {
            @Override
            public void subscribe(final Subscriber<? super FlowableGroupByTest.Event> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                System.out.println("*** Subscribing to EventStream ***");
                subscribeCounter.incrementAndGet();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < count; i++) {
                            FlowableGroupByTest.Event e = new FlowableGroupByTest.Event();
                            e.source = i % groupCount;
                            e.message = "Event-" + i;
                            subscriber.onNext(e);
                        }
                        subscriber.onComplete();
                    }
                }).start();
            }
        });
        es.groupBy(new Function<FlowableGroupByTest.Event, Integer>() {
            @Override
            public Integer apply(FlowableGroupByTest.Event e) {
                return e.source;
            }
        }).flatMap(new Function<GroupedFlowable<Integer, FlowableGroupByTest.Event>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(GroupedFlowable<Integer, FlowableGroupByTest.Event> eventGroupedFlowable) {
                System.out.println(("GroupedFlowable Key: " + (eventGroupedFlowable.getKey())));
                groupCounter.incrementAndGet();
                return eventGroupedFlowable.map(new Function<FlowableGroupByTest.Event, String>() {
                    @Override
                    public String apply(FlowableGroupByTest.Event event) {
                        return (("Source: " + (event.source)) + "  Message: ") + (event.message);
                    }
                });
            }
        }).subscribe(new DefaultSubscriber<String>() {
            @Override
            public void onComplete() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onNext(String outputMessage) {
                System.out.println(outputMessage);
                eventCounter.incrementAndGet();
            }
        });
        latch.await(5000, TimeUnit.MILLISECONDS);
        Assert.assertEquals(1, subscribeCounter.get());
        Assert.assertEquals(groupCount, groupCounter.get());
        Assert.assertEquals(count, eventCounter.get());
    }

    /* We will only take 1 group with 20 events from it and then unsubscribe. */
    @Test
    public void testUnsubscribeOnNestedTakeAndSyncInfiniteStream() throws InterruptedException {
        final AtomicInteger subscribeCounter = new AtomicInteger();
        final AtomicInteger sentEventCounter = new AtomicInteger();
        doTestUnsubscribeOnNestedTakeAndAsyncInfiniteStream(SYNC_INFINITE_OBSERVABLE_OF_EVENT(2, subscribeCounter, sentEventCounter), subscribeCounter);
        Thread.sleep(500);
        Assert.assertEquals(39, sentEventCounter.get());
    }

    /* We will only take 1 group with 20 events from it and then unsubscribe. */
    @Test
    public void testUnsubscribeOnNestedTakeAndAsyncInfiniteStream() throws InterruptedException {
        final AtomicInteger subscribeCounter = new AtomicInteger();
        final AtomicInteger sentEventCounter = new AtomicInteger();
        doTestUnsubscribeOnNestedTakeAndAsyncInfiniteStream(ASYNC_INFINITE_OBSERVABLE_OF_EVENT(2, subscribeCounter, sentEventCounter), subscribeCounter);
        Thread.sleep(500);
        Assert.assertEquals(39, sentEventCounter.get());
    }

    @Test
    public void testUnsubscribeViaTakeOnGroupThenMergeAndTake() {
        final AtomicInteger subscribeCounter = new AtomicInteger();
        final AtomicInteger sentEventCounter = new AtomicInteger();
        final AtomicInteger eventCounter = new AtomicInteger();
        // take 2 of the 4 groups
        SYNC_INFINITE_OBSERVABLE_OF_EVENT(4, subscribeCounter, sentEventCounter).groupBy(new Function<FlowableGroupByTest.Event, Integer>() {
            @Override
            public Integer apply(FlowableGroupByTest.Event e) {
                return e.source;
            }
        }).take(2).flatMap(new Function<GroupedFlowable<Integer, FlowableGroupByTest.Event>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(GroupedFlowable<Integer, FlowableGroupByTest.Event> eventGroupedFlowable) {
                return eventGroupedFlowable.map(new Function<FlowableGroupByTest.Event, String>() {
                    @Override
                    public String apply(FlowableGroupByTest.Event event) {
                        return (("testUnsubscribe => Source: " + (event.source)) + "  Message: ") + (event.message);
                    }
                });
            }
        }).take(30).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                eventCounter.incrementAndGet();
                System.out.println(("=> " + s));
            }
        });
        Assert.assertEquals(30, eventCounter.get());
        // we should send 28 additional events that are filtered out as they are in the groups we skip
        Assert.assertEquals(58, sentEventCounter.get());
    }

    @Test
    public void testUnsubscribeViaTakeOnGroupThenTakeOnInner() {
        final AtomicInteger subscribeCounter = new AtomicInteger();
        final AtomicInteger sentEventCounter = new AtomicInteger();
        final AtomicInteger eventCounter = new AtomicInteger();
        // take 2 of the 4 groups
        SYNC_INFINITE_OBSERVABLE_OF_EVENT(4, subscribeCounter, sentEventCounter).groupBy(new Function<FlowableGroupByTest.Event, Integer>() {
            @Override
            public Integer apply(FlowableGroupByTest.Event e) {
                return e.source;
            }
        }).take(2).flatMap(new Function<GroupedFlowable<Integer, FlowableGroupByTest.Event>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(GroupedFlowable<Integer, FlowableGroupByTest.Event> eventGroupedFlowable) {
                int numToTake = 0;
                if ((eventGroupedFlowable.getKey()) == 1) {
                    numToTake = 10;
                } else
                    if ((eventGroupedFlowable.getKey()) == 2) {
                        numToTake = 5;
                    }

                return eventGroupedFlowable.take(numToTake).map(new Function<FlowableGroupByTest.Event, String>() {
                    @Override
                    public String apply(FlowableGroupByTest.Event event) {
                        return (("testUnsubscribe => Source: " + (event.source)) + "  Message: ") + (event.message);
                    }
                });
            }
        }).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                eventCounter.incrementAndGet();
                System.out.println(("=> " + s));
            }
        });
        Assert.assertEquals(15, eventCounter.get());
        // we should send 22 additional events that are filtered out as they are skipped while taking the 15 we want
        Assert.assertEquals(37, sentEventCounter.get());
    }

    @Test
    public void testStaggeredCompletion() throws InterruptedException {
        final AtomicInteger eventCounter = new AtomicInteger();
        final CountDownLatch latch = new CountDownLatch(1);
        Flowable.range(0, 100).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return i % 2;
            }
        }).flatMap(new Function<GroupedFlowable<Integer, Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(GroupedFlowable<Integer, Integer> group) {
                if ((group.getKey()) == 0) {
                    return group.delay(100, TimeUnit.MILLISECONDS).map(new Function<Integer, Integer>() {
                        @Override
                        public Integer apply(Integer t) {
                            return t * 10;
                        }
                    });
                } else {
                    return group;
                }
            }
        }).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onComplete() {
                System.out.println("=> onComplete");
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onNext(Integer s) {
                eventCounter.incrementAndGet();
                System.out.println(("=> " + s));
            }
        });
        if (!(latch.await(3000, TimeUnit.MILLISECONDS))) {
            fail("timed out");
        }
        Assert.assertEquals(100, eventCounter.get());
    }

    @Test(timeout = 1000)
    public void testCompletionIfInnerNotSubscribed() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicInteger eventCounter = new AtomicInteger();
        Flowable.range(0, 100).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return i % 2;
            }
        }).subscribe(new DefaultSubscriber<GroupedFlowable<Integer, Integer>>() {
            @Override
            public void onComplete() {
                latch.countDown();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onNext(GroupedFlowable<Integer, Integer> s) {
                eventCounter.incrementAndGet();
                System.out.println(("=> " + s));
            }
        });
        if (!(latch.await(500, TimeUnit.MILLISECONDS))) {
            fail("timed out - never got completion");
        }
        Assert.assertEquals(2, eventCounter.get());
    }

    @Test
    public void testIgnoringGroups() {
        final AtomicInteger subscribeCounter = new AtomicInteger();
        final AtomicInteger sentEventCounter = new AtomicInteger();
        final AtomicInteger eventCounter = new AtomicInteger();
        SYNC_INFINITE_OBSERVABLE_OF_EVENT(4, subscribeCounter, sentEventCounter).groupBy(new Function<FlowableGroupByTest.Event, Integer>() {
            @Override
            public Integer apply(FlowableGroupByTest.Event e) {
                return e.source;
            }
        }).flatMap(new Function<GroupedFlowable<Integer, FlowableGroupByTest.Event>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(GroupedFlowable<Integer, FlowableGroupByTest.Event> eventGroupedFlowable) {
                Flowable<FlowableGroupByTest.Event> eventStream = eventGroupedFlowable;
                if ((eventGroupedFlowable.getKey()) >= 2) {
                    // filter these
                    eventStream = eventGroupedFlowable.filter(new Predicate<FlowableGroupByTest.Event>() {
                        @Override
                        public boolean test(FlowableGroupByTest.Event t1) {
                            return false;
                        }
                    });
                }
                return eventStream.map(new Function<FlowableGroupByTest.Event, String>() {
                    @Override
                    public String apply(FlowableGroupByTest.Event event) {
                        return (("testUnsubscribe => Source: " + (event.source)) + "  Message: ") + (event.message);
                    }
                });
            }
        }).take(30).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) {
                eventCounter.incrementAndGet();
                System.out.println(("=> " + s));
            }
        });
        Assert.assertEquals(30, eventCounter.get());
        // we should send 30 additional events that are filtered out as they are in the groups we skip
        Assert.assertEquals(60, sentEventCounter.get());
    }

    @Test
    public void testFirstGroupsCompleteAndParentSlowToThenEmitFinalGroupsAndThenComplete() throws InterruptedException {
        final CountDownLatch first = new CountDownLatch(2);// there are two groups to first complete

        final ArrayList<String> results = new ArrayList<String>();
        Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new BooleanSubscription());
                sub.onNext(1);
                sub.onNext(2);
                sub.onNext(1);
                sub.onNext(2);
                try {
                    first.await();
                } catch (InterruptedException e) {
                    sub.onError(e);
                    return;
                }
                sub.onNext(3);
                sub.onNext(3);
                sub.onComplete();
            }
        }).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t) {
                return t;
            }
        }).flatMap(new Function<GroupedFlowable<Integer, Integer>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final GroupedFlowable<Integer, Integer> group) {
                if ((group.getKey()) < 3) {
                    return // must take(2) so an onComplete + unsubscribe happens on these first 2 groups
                    group.map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer t1) {
                            return "first groups: " + t1;
                        }
                    }).take(2).doOnComplete(new Action() {
                        @Override
                        public void run() {
                            first.countDown();
                        }
                    });
                } else {
                    return group.map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer t1) {
                            return "last group: " + t1;
                        }
                    });
                }
            }
        }).blockingForEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                results.add(s);
            }
        });
        System.out.println(("Results: " + results));
        Assert.assertEquals(6, results.size());
    }

    @Test
    public void testFirstGroupsCompleteAndParentSlowToThenEmitFinalGroupsWhichThenSubscribesOnAndDelaysAndThenCompletes() throws InterruptedException {
        System.err.println("----------------------------------------------------------------------------------------------");
        final CountDownLatch first = new CountDownLatch(2);// there are two groups to first complete

        final ArrayList<String> results = new ArrayList<String>();
        Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new BooleanSubscription());
                sub.onNext(1);
                sub.onNext(2);
                sub.onNext(1);
                sub.onNext(2);
                try {
                    first.await();
                } catch (InterruptedException e) {
                    sub.onError(e);
                    return;
                }
                sub.onNext(3);
                sub.onNext(3);
                sub.onComplete();
            }
        }).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t) {
                return t;
            }
        }).flatMap(new Function<GroupedFlowable<Integer, Integer>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final GroupedFlowable<Integer, Integer> group) {
                if ((group.getKey()) < 3) {
                    return // must take(2) so an onComplete + unsubscribe happens on these first 2 groups
                    group.map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer t1) {
                            return "first groups: " + t1;
                        }
                    }).take(2).doOnComplete(new Action() {
                        @Override
                        public void run() {
                            first.countDown();
                        }
                    });
                } else {
                    return group.subscribeOn(Schedulers.newThread()).delay(400, TimeUnit.MILLISECONDS).map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer t1) {
                            return "last group: " + t1;
                        }
                    }).doOnEach(new Consumer<Notification<String>>() {
                        @Override
                        public void accept(Notification<String> t1) {
                            System.err.println(("subscribeOn notification => " + t1));
                        }
                    });
                }
            }
        }).doOnEach(new Consumer<Notification<String>>() {
            @Override
            public void accept(Notification<String> t1) {
                System.err.println(("outer notification => " + t1));
            }
        }).blockingForEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                results.add(s);
            }
        });
        System.out.println(("Results: " + results));
        Assert.assertEquals(6, results.size());
    }

    @Test
    public void testFirstGroupsCompleteAndParentSlowToThenEmitFinalGroupsWhichThenObservesOnAndDelaysAndThenCompletes() throws InterruptedException {
        final CountDownLatch first = new CountDownLatch(2);// there are two groups to first complete

        final ArrayList<String> results = new ArrayList<String>();
        Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new BooleanSubscription());
                sub.onNext(1);
                sub.onNext(2);
                sub.onNext(1);
                sub.onNext(2);
                try {
                    first.await();
                } catch (InterruptedException e) {
                    sub.onError(e);
                    return;
                }
                sub.onNext(3);
                sub.onNext(3);
                sub.onComplete();
            }
        }).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t) {
                return t;
            }
        }).flatMap(new Function<GroupedFlowable<Integer, Integer>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final GroupedFlowable<Integer, Integer> group) {
                if ((group.getKey()) < 3) {
                    return // must take(2) so an onComplete + unsubscribe happens on these first 2 groups
                    group.map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer t1) {
                            return "first groups: " + t1;
                        }
                    }).take(2).doOnComplete(new Action() {
                        @Override
                        public void run() {
                            first.countDown();
                        }
                    });
                } else {
                    return group.observeOn(Schedulers.newThread()).delay(400, TimeUnit.MILLISECONDS).map(new Function<Integer, String>() {
                        @Override
                        public String apply(Integer t1) {
                            return "last group: " + t1;
                        }
                    });
                }
            }
        }).blockingForEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                results.add(s);
            }
        });
        System.out.println(("Results: " + results));
        Assert.assertEquals(6, results.size());
    }

    @Test
    public void testGroupsWithNestedSubscribeOn() throws InterruptedException {
        final ArrayList<String> results = new ArrayList<String>();
        Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new BooleanSubscription());
                sub.onNext(1);
                sub.onNext(2);
                sub.onNext(1);
                sub.onNext(2);
                sub.onComplete();
            }
        }).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t) {
                return t;
            }
        }).flatMap(new Function<GroupedFlowable<Integer, Integer>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final GroupedFlowable<Integer, Integer> group) {
                return group.subscribeOn(Schedulers.newThread()).map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer t1) {
                        System.out.println(((("Received: " + t1) + " on group : ") + (group.getKey())));
                        return "first groups: " + t1;
                    }
                });
            }
        }).doOnEach(new Consumer<Notification<String>>() {
            @Override
            public void accept(Notification<String> t1) {
                System.out.println(("notification => " + t1));
            }
        }).blockingForEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                results.add(s);
            }
        });
        System.out.println(("Results: " + results));
        Assert.assertEquals(4, results.size());
    }

    @Test
    public void testGroupsWithNestedObserveOn() throws InterruptedException {
        final ArrayList<String> results = new ArrayList<String>();
        Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> sub) {
                sub.onSubscribe(new BooleanSubscription());
                sub.onNext(1);
                sub.onNext(2);
                sub.onNext(1);
                sub.onNext(2);
                sub.onComplete();
            }
        }).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t) {
                return t;
            }
        }).flatMap(new Function<GroupedFlowable<Integer, Integer>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final GroupedFlowable<Integer, Integer> group) {
                return group.observeOn(Schedulers.newThread()).delay(400, TimeUnit.MILLISECONDS).map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer t1) {
                        return "first groups: " + t1;
                    }
                });
            }
        }).blockingForEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                results.add(s);
            }
        });
        System.out.println(("Results: " + results));
        Assert.assertEquals(4, results.size());
    }

    private static class Event {
        int source;

        String message;

        @Override
        public String toString() {
            return (("Event => source: " + (source)) + " message: ") + (message);
        }
    }

    @Test
    public void testGroupByOnAsynchronousSourceAcceptsMultipleSubscriptions() throws InterruptedException {
        // choose an asynchronous source
        Flowable<Long> source = Flowable.interval(10, TimeUnit.MILLISECONDS).take(1);
        // apply groupBy to the source
        Flowable<GroupedFlowable<Boolean, Long>> stream = source.groupBy(FlowableGroupByTest.IS_EVEN);
        // create two observers
        Subscriber<GroupedFlowable<Boolean, Long>> f1 = TestHelper.mockSubscriber();
        Subscriber<GroupedFlowable<Boolean, Long>> f2 = TestHelper.mockSubscriber();
        // subscribe with the observers
        stream.subscribe(f1);
        stream.subscribe(f2);
        // check that subscriptions were successful
        Mockito.verify(f1, never()).onError(Mockito.<Throwable>any());
        Mockito.verify(f2, never()).onError(Mockito.<Throwable>any());
    }

    private static Function<Long, Boolean> IS_EVEN = new Function<Long, Boolean>() {
        @Override
        public Boolean apply(Long n) {
            return (n % 2) == 0;
        }
    };

    private static Function<Integer, Boolean> IS_EVEN2 = new Function<Integer, Boolean>() {
        @Override
        public Boolean apply(Integer n) {
            return (n % 2) == 0;
        }
    };

    @Test
    public void testGroupByBackpressure() throws InterruptedException {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Flowable.range(1, 4000).groupBy(FlowableGroupByTest.IS_EVEN2).flatMap(new Function<GroupedFlowable<Boolean, Integer>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final GroupedFlowable<Boolean, Integer> g) {
                return g.observeOn(Schedulers.computation()).map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer l) {
                        if (g.getKey()) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                            }
                            return l + " is even.";
                        } else {
                            return l + " is odd.";
                        }
                    }
                });
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
    }

    Function<Integer, Integer> dbl = new Function<Integer, Integer>() {
        @Override
        public Integer apply(Integer t1) {
            return t1 * 2;
        }
    };

    Function<Integer, Integer> identity = new Function<Integer, Integer>() {
        @Override
        public Integer apply(Integer v) {
            return v;
        }
    };

    @Test
    public void normalBehavior() {
        Flowable<String> source = Flowable.fromIterable(Arrays.asList("  foo", " FoO ", "baR  ", "foO ", " Baz   ", "  qux ", "   bar", " BAR  ", "FOO ", "baz  ", " bAZ ", "    fOo    "));
        /* foo FoO foO FOO fOo
        baR bar BAR
        Baz baz bAZ
        qux
         */
        Function<String, String> keysel = new Function<String, String>() {
            @Override
            public String apply(String t1) {
                return t1.trim().toLowerCase();
            }
        };
        Function<String, String> valuesel = new Function<String, String>() {
            @Override
            public String apply(String t1) {
                return t1 + t1;
            }
        };
        Flowable<String> m = source.groupBy(keysel, valuesel).flatMap(new Function<GroupedFlowable<String, String>, Publisher<String>>() {
            @Override
            public io.reactivex.Publisher<String> apply(final GroupedFlowable<String, String> g) {
                System.out.println(("-----------> NEXT: " + (g.getKey())));
                return g.take(2).map(new Function<String, String>() {
                    int count;

                    @Override
                    public String apply(String v) {
                        System.out.println(v);
                        return ((g.getKey()) + "-") + ((count)++);
                    }
                });
            }
        });
        TestSubscriber<String> ts = new TestSubscriber<String>();
        m.subscribe(ts);
        ts.awaitTerminalEvent();
        System.out.println(("ts .get " + (ts.values())));
        ts.assertNoErrors();
        Assert.assertEquals(ts.values(), Arrays.asList("foo-0", "foo-1", "bar-0", "foo-0", "baz-0", "qux-0", "bar-1", "bar-0", "foo-1", "baz-1", "baz-0", "foo-0"));
    }

    @Test
    public void keySelectorThrows() {
        Flowable<Integer> source = Flowable.just(0, 1, 2, 3, 4, 5, 6);
        Flowable<Integer> m = source.groupBy(fail(0), dbl).flatMap(FlowableGroupByTest.FLATTEN_INTEGER);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        m.subscribe(ts);
        ts.awaitTerminalEvent();
        Assert.assertEquals(1, ts.errorCount());
        ts.assertNoValues();
    }

    @Test
    public void valueSelectorThrows() {
        Flowable<Integer> source = Flowable.just(0, 1, 2, 3, 4, 5, 6);
        Flowable<Integer> m = source.groupBy(identity, fail(0)).flatMap(FlowableGroupByTest.FLATTEN_INTEGER);
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        m.subscribe(ts);
        ts.awaitTerminalEvent();
        Assert.assertEquals(1, ts.errorCount());
        ts.assertNoValues();
    }

    @Test
    public void innerEscapeCompleted() {
        Flowable<Integer> source = Flowable.just(0);
        Flowable<Integer> m = source.groupBy(identity, dbl).flatMap(FlowableGroupByTest.FLATTEN_INTEGER);
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        m.subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
        System.out.println(ts.values());
    }

    /**
     * Assert we get an IllegalStateException if trying to subscribe to an inner GroupedFlowable more than once.
     */
    @Test
    public void testExceptionIfSubscribeToChildMoreThanOnce() {
        Flowable<Integer> source = Flowable.just(0);
        final AtomicReference<GroupedFlowable<Integer, Integer>> inner = new AtomicReference<GroupedFlowable<Integer, Integer>>();
        Flowable<GroupedFlowable<Integer, Integer>> m = source.groupBy(identity, dbl);
        m.subscribe(new Consumer<GroupedFlowable<Integer, Integer>>() {
            @Override
            public void accept(GroupedFlowable<Integer, Integer> t1) {
                inner.set(t1);
            }
        });
        inner.get().subscribe();
        Subscriber<Integer> subscriber2 = TestHelper.mockSubscriber();
        inner.get().subscribe(subscriber2);
        Mockito.verify(subscriber2, never()).onComplete();
        Mockito.verify(subscriber2, never()).onNext(anyInt());
        verify(subscriber2).onError(any(IllegalStateException.class));
    }

    @Test
    public void testError2() {
        Flowable<Integer> source = Flowable.concat(Flowable.just(0), Flowable.<Integer>error(new TestException("Forced failure")));
        Flowable<Integer> m = source.groupBy(identity, dbl).flatMap(FlowableGroupByTest.FLATTEN_INTEGER);
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        m.subscribe(ts);
        ts.awaitTerminalEvent();
        Assert.assertEquals(1, ts.errorCount());
        ts.assertValueCount(1);
    }

    @Test
    public void testgroupByBackpressure() throws InterruptedException {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Flowable.range(1, 4000).groupBy(FlowableGroupByTest.IS_EVEN2).flatMap(new Function<GroupedFlowable<Boolean, Integer>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final GroupedFlowable<Boolean, Integer> g) {
                return g.doOnComplete(new Action() {
                    @Override
                    public void run() {
                        System.out.println("//////////////////// COMPLETED-A");
                    }
                }).observeOn(Schedulers.computation()).map(new Function<Integer, String>() {
                    int c;

                    @Override
                    public String apply(Integer l) {
                        if (g.getKey()) {
                            if (((c)++) < 400) {
                                try {
                                    Thread.sleep(1);
                                } catch (InterruptedException e) {
                                }
                            }
                            return l + " is even.";
                        } else {
                            return l + " is odd.";
                        }
                    }
                }).doOnComplete(new Action() {
                    @Override
                    public void run() {
                        System.out.println("//////////////////// COMPLETED-B");
                    }
                });
            }
        }).doOnEach(new Consumer<Notification<String>>() {
            @Override
            public void accept(Notification<String> t1) {
                System.out.println(("NEXT: " + t1));
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
    }

    @Test
    public void testgroupByBackpressure2() throws InterruptedException {
        TestSubscriber<String> ts = new TestSubscriber<String>();
        Flowable.range(1, 4000).doOnNext(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) {
                System.out.println(("testgroupByBackpressure2 >> " + v));
            }
        }).groupBy(FlowableGroupByTest.IS_EVEN2).flatMap(new Function<GroupedFlowable<Boolean, Integer>, Flowable<String>>() {
            @Override
            public io.reactivex.Flowable<String> apply(final GroupedFlowable<Boolean, Integer> g) {
                return g.take(2).observeOn(Schedulers.computation()).map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer l) {
                        if (g.getKey()) {
                            try {
                                Thread.sleep(1);
                            } catch (InterruptedException e) {
                            }
                            return l + " is even.";
                        } else {
                            return l + " is odd.";
                        }
                    }
                });
            }
        }).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
    }

    static Function<GroupedFlowable<Integer, Integer>, Flowable<Integer>> FLATTEN_INTEGER = new Function<GroupedFlowable<Integer, Integer>, Flowable<Integer>>() {
        @Override
        public io.reactivex.Flowable<Integer> apply(GroupedFlowable<Integer, Integer> t) {
            return t;
        }
    };

    @Test
    public void testGroupByWithNullKey() {
        final String[] key = new String[]{ "uninitialized" };
        final List<String> values = new ArrayList<String>();
        Flowable.just("a", "b", "c").groupBy(new Function<String, String>() {
            @Override
            public String apply(String value) {
                return null;
            }
        }).subscribe(new Consumer<GroupedFlowable<String, String>>() {
            @Override
            public void accept(GroupedFlowable<String, String> groupedFlowable) {
                key[0] = groupedFlowable.getKey();
                groupedFlowable.subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        values.add(s);
                    }
                });
            }
        });
        Assert.assertEquals(null, key[0]);
        Assert.assertEquals(Arrays.asList("a", "b", "c"), values);
    }

    @Test
    public void testGroupByUnsubscribe() {
        final Subscription s = Mockito.mock(io.reactivex.subscribers.Subscription.class);
        Flowable<Integer> f = Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(s);
            }
        });
        TestSubscriber<Object> ts = new TestSubscriber<Object>();
        f.groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return null;
            }
        }).subscribe(ts);
        ts.dispose();
        verify(s).cancel();
    }

    @Test
    public void testGroupByShouldPropagateError() {
        final Throwable e = new RuntimeException("Oops");
        final TestSubscriber<Integer> inner1 = new TestSubscriber<Integer>();
        final TestSubscriber<Integer> inner2 = new TestSubscriber<Integer>();
        final TestSubscriber<GroupedFlowable<Integer, Integer>> outer = new TestSubscriber<GroupedFlowable<Integer, Integer>>(new DefaultSubscriber<GroupedFlowable<Integer, Integer>>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(GroupedFlowable<Integer, Integer> f) {
                if ((f.getKey()) == 0) {
                    f.subscribe(inner1);
                } else {
                    f.subscribe(inner2);
                }
            }
        });
        Flowable.unsafeCreate(new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new BooleanSubscription());
                subscriber.onNext(0);
                subscriber.onNext(1);
                subscriber.onError(e);
            }
        }).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return i % 2;
            }
        }).subscribe(outer);
        Assert.assertEquals(Arrays.asList(e), outer.errors());
        Assert.assertEquals(Arrays.asList(e), inner1.errors());
        Assert.assertEquals(Arrays.asList(e), inner2.errors());
    }

    @Test
    public void testRequestOverflow() {
        final AtomicBoolean completed = new AtomicBoolean(false);
        // flatten
        // group into one group
        Flowable.just(1, 2, 3).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t) {
                return 1;
            }
        }).concatMap(new Function<GroupedFlowable<Integer, Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(GroupedFlowable<Integer, Integer> g) {
                return g;
            }
        }).subscribe(new DefaultSubscriber<Integer>() {
            @Override
            public void onStart() {
                request(2);
            }

            @Override
            public void onComplete() {
                completed.set(true);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer t) {
                System.out.println(t);
                // provoke possible request overflow
                request(((Long.MAX_VALUE) - 1));
            }
        });
        Assert.assertTrue(completed.get());
    }

    /**
     * Issue #3425.
     *
     * The problem is that a request of 1 may create a new group, emit to the desired group
     * or emit to a completely different group. In this test, the merge requests N which
     * must be produced by the range, however it will create a bunch of groups before the actual
     * group receives a value.
     */
    @Test
    public void testBackpressureObserveOnOuter() {
        for (int j = 0; j < 1000; j++) {
            Flowable.merge(Flowable.range(0, 500).groupBy(new Function<Integer, Object>() {
                @Override
                public Object apply(Integer i) {
                    return i % ((Flowable.bufferSize()) + 2);
                }
            }).observeOn(Schedulers.computation())).blockingLast();
        }
    }

    /**
     * Synchronous verification of issue #3425.
     */
    @Test
    public void testBackpressureInnerDoesntOverflowOuter() {
        TestSubscriber<GroupedFlowable<Integer, Integer>> ts = new TestSubscriber<GroupedFlowable<Integer, Integer>>(0L);
        // this will request Long.MAX_VALUE
        Flowable.fromArray(1, 2).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v;
            }
        }).doOnNext(new Consumer<GroupedFlowable<Integer, Integer>>() {
            @Override
            public void accept(GroupedFlowable<Integer, Integer> g) {
                g.subscribe();
            }
        }).subscribe(ts);
        ts.request(1);
        ts.assertNotComplete();
        ts.assertNoErrors();
        ts.assertValueCount(1);
    }

    @Test
    public void testOneGroupInnerRequestsTwiceBuffer() {
        TestSubscriber<Object> ts1 = new TestSubscriber<Object>(0L);
        final TestSubscriber<Object> ts2 = new TestSubscriber<Object>(0L);
        Flowable.range(1, ((Flowable.bufferSize()) * 2)).groupBy(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer v) {
                return 1;
            }
        }).doOnNext(new Consumer<GroupedFlowable<Object, Integer>>() {
            @Override
            public void accept(GroupedFlowable<Object, Integer> g) {
                g.subscribe(ts2);
            }
        }).subscribe(ts1);
        ts1.assertNoValues();
        ts1.assertNoErrors();
        ts1.assertNotComplete();
        ts2.assertNoValues();
        ts2.assertNoErrors();
        ts2.assertNotComplete();
        ts1.request(1);
        ts1.assertValueCount(1);
        ts1.assertNoErrors();
        ts1.assertNotComplete();
        ts2.assertNoValues();
        ts2.assertNoErrors();
        ts2.assertNotComplete();
        ts2.request(((Flowable.bufferSize()) * 2));
        ts2.assertValueCount(((Flowable.bufferSize()) * 2));
        ts2.assertNoErrors();
        ts2.assertComplete();
    }

    @Test
    public void outerInnerFusion() {
        final TestSubscriber<Integer> ts1 = SubscriberFusion.newTest(ANY);
        final TestSubscriber<GroupedFlowable<Integer, Integer>> ts2 = SubscriberFusion.newTest(ANY);
        Flowable.range(1, 10).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return 1;
            }
        }, new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer v) {
                return v + 1;
            }
        }).doOnNext(new Consumer<GroupedFlowable<Integer, Integer>>() {
            @Override
            public void accept(GroupedFlowable<Integer, Integer> g) {
                g.subscribe(ts1);
            }
        }).subscribe(ts2);
        ts1.assertOf(SubscriberFusion.<Integer>assertFusionMode(ASYNC)).assertValues(2, 3, 4, 5, 6, 7, 8, 9, 10, 11).assertNoErrors().assertComplete();
        ts2.assertOf(SubscriberFusion.<GroupedFlowable<Integer, Integer>>assertFusionMode(ASYNC)).assertValueCount(1).assertNoErrors().assertComplete();
    }

    @Test
    public void keySelectorAndDelayError() {
        Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).groupBy(Functions.<Integer>identity(), true).flatMap(new Function<GroupedFlowable<Integer, Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(GroupedFlowable<Integer, Integer> g) throws Exception {
                return g;
            }
        }).test().assertFailure(TestException.class, 1);
    }

    @Test
    public void keyAndValueSelectorAndDelayError() {
        Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).groupBy(Functions.<Integer>identity(), Functions.<Integer>identity(), true).flatMap(new Function<GroupedFlowable<Integer, Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(GroupedFlowable<Integer, Integer> g) throws Exception {
                return g;
            }
        }).test().assertFailure(TestException.class, 1);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).groupBy(Functions.justFunction(1)));
        Flowable.just(1).groupBy(Functions.justFunction(1)).doOnNext(new Consumer<GroupedFlowable<Integer, Integer>>() {
            @Override
            public void accept(GroupedFlowable<Integer, Integer> g) throws Exception {
                TestHelper.checkDisposed(g);
            }
        }).test();
    }

    @Test
    public void reentrantComplete() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    pp.onComplete();
                }
            }
        };
        Flowable.merge(pp.groupBy(Functions.justFunction(1))).subscribe(ts);
        pp.onNext(1);
        ts.assertResult(1);
    }

    @Test
    public void reentrantCompleteCancel() {
        final PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    pp.onComplete();
                    dispose();
                }
            }
        };
        Flowable.merge(pp.groupBy(Functions.justFunction(1))).subscribe(ts);
        pp.onNext(1);
        ts.assertSubscribed().assertValue(1).assertNoErrors().assertNotComplete();
    }

    @Test
    public void delayErrorSimpleComplete() {
        Flowable.just(1).groupBy(Functions.justFunction(1), true).flatMap(Functions.<Flowable<Integer>>identity()).test().assertResult(1);
    }

    @Test
    public void mainFusionRejected() {
        TestSubscriber<Flowable<Integer>> ts = SubscriberFusion.newTest(SYNC);
        Flowable.just(1).groupBy(Functions.justFunction(1)).subscribe(ts);
        SubscriberFusion.assertFusion(ts, NONE).assertValueCount(1).assertComplete().assertNoErrors();
    }

    @Test
    public void badSource() {
        TestHelper.checkBadSourceFlowable(new Function<Flowable<Object>, Object>() {
            @Override
            public Object apply(Flowable<Object> f) throws Exception {
                return f.groupBy(Functions.justFunction(1));
            }
        }, false, 1, 1, ((Object[]) (null)));
    }

    @Test
    public void badRequest() {
        TestHelper.assertBadRequestReported(Flowable.just(1).groupBy(Functions.justFunction(1)));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Publisher<GroupedFlowable<Integer, Object>>>() {
            @Override
            public io.reactivex.Publisher<GroupedFlowable<Integer, Object>> apply(Flowable<Object> f) throws Exception {
                return f.groupBy(Functions.justFunction(1));
            }
        });
    }

    @Test
    public void nullKeyTakeInner() {
        Flowable.just(1).groupBy(new Function<Integer, Object>() {
            @Override
            public Object apply(Integer v) throws Exception {
                return null;
            }
        }).flatMap(new Function<GroupedFlowable<Object, Integer>, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(GroupedFlowable<Object, Integer> g) throws Exception {
                return g.take(1);
            }
        }).test().assertResult(1);
    }

    @Test
    public void errorFused() {
        TestSubscriber<Object> ts = SubscriberFusion.newTest(ANY);
        Flowable.error(new TestException()).groupBy(Functions.justFunction(1)).subscribe(ts);
        SubscriberFusion.assertFusion(ts, ASYNC).assertFailure(TestException.class);
    }

    @Test
    public void errorFusedDelayed() {
        TestSubscriber<Object> ts = SubscriberFusion.newTest(ANY);
        Flowable.error(new TestException()).groupBy(Functions.justFunction(1), true).subscribe(ts);
        SubscriberFusion.assertFusion(ts, ASYNC).assertFailure(TestException.class);
    }

    @Test
    public void groupError() {
        Flowable.just(1).concatWith(Flowable.<Integer>error(new TestException())).groupBy(Functions.justFunction(1), true).flatMap(new Function<GroupedFlowable<Integer, Integer>, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(GroupedFlowable<Integer, Integer> g) throws Exception {
                return g.hide();
            }
        }).test().assertFailure(TestException.class, 1);
    }

    @Test
    public void groupComplete() {
        Flowable.just(1).groupBy(Functions.justFunction(1), true).flatMap(new Function<GroupedFlowable<Integer, Integer>, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(GroupedFlowable<Integer, Integer> g) throws Exception {
                return g.hide();
            }
        }).test().assertResult(1);
    }

    @Test
    public void mapFactoryThrows() {
        final IOException ex = new IOException("boo");
        // 
        Function<Consumer<Object>, Map<Integer, Object>> evictingMapFactory = new Function<Consumer<Object>, Map<Integer, Object>>() {
            @Override
            public Map<Integer, Object> apply(final Consumer<Object> notify) throws Exception {
                throw ex;
            }
        };
        Flowable.just(1).groupBy(Functions.<Integer>identity(), Functions.identity(), true, 16, evictingMapFactory).test().assertNoValues().assertError(ex);
    }

    @Test
    public void mapFactoryExpiryCompletesGroupedFlowable() {
        final List<Integer> completed = new CopyOnWriteArrayList<Integer>();
        Function<Consumer<Object>, Map<Integer, Object>> evictingMapFactory = FlowableGroupByTest.createEvictingMapFactorySynchronousOnly(1);
        PublishSubject<Integer> subject = PublishSubject.create();
        TestSubscriber<Integer> ts = subject.toFlowable(BackpressureStrategy.BUFFER).groupBy(Functions.<Integer>identity(), Functions.<Integer>identity(), true, 16, evictingMapFactory).flatMap(FlowableGroupByTest.addCompletedKey(completed)).test();
        subject.onNext(1);
        subject.onNext(2);
        subject.onNext(3);
        ts.assertValues(1, 2, 3).assertNotTerminated();
        Assert.assertEquals(Arrays.asList(1, 2), completed);
        // ensure coverage of the code that clears the evicted queue
        subject.onComplete();
        ts.assertComplete();
        ts.assertValueCount(3);
    }

    private static final Function<Integer, Integer> mod5 = new Function<Integer, Integer>() {
        @Override
        public Integer apply(Integer n) throws Exception {
            return n % 5;
        }
    };

    @Test
    public void mapFactoryWithExpiringGuavaCacheDemonstrationCodeForUseInJavadoc() {
        // javadoc will be a version of this using lambdas and without assertions
        final List<Integer> completed = new CopyOnWriteArrayList<Integer>();
        // size should be less than 5 to notice the effect
        Function<Consumer<Object>, Map<Integer, Object>> evictingMapFactory = FlowableGroupByTest.createEvictingMapFactoryGuava(3);
        int numValues = 1000;
        TestSubscriber<Integer> ts = Flowable.range(1, numValues).groupBy(FlowableGroupByTest.mod5, Functions.<Integer>identity(), true, 16, evictingMapFactory).flatMap(FlowableGroupByTest.addCompletedKey(completed)).test().assertComplete();
        ts.assertValueCount(numValues);
        // the exact eviction behaviour of the guava cache is not specified so we make some approximate tests
        Assert.assertTrue(((completed.size()) > (numValues * 0.9)));
    }

    @Test
    public void mapFactoryEvictionQueueClearedOnErrorCoverageOnly() {
        Function<Consumer<Object>, Map<Integer, Object>> evictingMapFactory = FlowableGroupByTest.createEvictingMapFactorySynchronousOnly(1);
        PublishSubject<Integer> subject = PublishSubject.create();
        TestSubscriber<Integer> ts = subject.toFlowable(BackpressureStrategy.BUFFER).groupBy(Functions.<Integer>identity(), Functions.<Integer>identity(), true, 16, evictingMapFactory).flatMap(new Function<GroupedFlowable<Integer, Integer>, Publisher<Integer>>() {
            @Override
            public io.reactivex.Publisher<Integer> apply(GroupedFlowable<Integer, Integer> g) throws Exception {
                return g;
            }
        }).test();
        RuntimeException ex = new RuntimeException();
        // ensure coverage of the code that clears the evicted queue
        subject.onError(ex);
        ts.assertNoValues().assertError(ex);
    }

    private static final class TestTicker extends Ticker {
        long tick;

        @Override
        public long read() {
            return tick;
        }
    }

    @Test
    public void testGroupByEvictionCancellationOfSource5933() {
        PublishProcessor<Integer> source = PublishProcessor.create();
        final FlowableGroupByTest.TestTicker testTicker = new FlowableGroupByTest.TestTicker();
        Function<Consumer<Object>, Map<Integer, Object>> mapFactory = new Function<Consumer<Object>, Map<Integer, Object>>() {
            @Override
            public Map<Integer, Object> apply(final Consumer<Object> action) throws Exception {
                return // 
                // 
                CacheBuilder.newBuilder().expireAfterAccess(5, TimeUnit.SECONDS).removalListener(new RemovalListener<Object, Object>() {
                    @Override
                    public void onRemoval(RemovalNotification<Object, Object> notification) {
                        try {
                            action.accept(notification.getValue());
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }).ticker(testTicker).<Integer, Object>build().asMap();
            }
        };
        final List<String> list = new CopyOnWriteArrayList<String>();
        Flowable<Integer> stream = // 
        // 
        source.doOnCancel(new Action() {
            @Override
            public void run() throws Exception {
                list.add("Source canceled");
            }
        }).<Integer, Integer>groupBy(Functions.<Integer>identity(), Functions.<Integer>identity(), false, Flowable.bufferSize(), mapFactory).flatMap(new Function<GroupedFlowable<Integer, Integer>, Publisher<? extends Integer>>() {
            @Override
            public io.reactivex.Publisher<? extends Integer> apply(GroupedFlowable<Integer, Integer> group) throws Exception {
                return // 
                group.doOnComplete(new Action() {
                    @Override
                    public void run() throws Exception {
                        list.add("Group completed");
                    }
                }).doOnCancel(new Action() {
                    @Override
                    public void run() throws Exception {
                        list.add("Group canceled");
                    }
                });
            }
        });
        TestSubscriber<Integer> ts = // 
        stream.doOnCancel(new Action() {
            @Override
            public void run() throws Exception {
                list.add("Outer group by canceled");
            }
        }).test();
        // Send 3 in the same group and wait for them to be seen
        source.onNext(1);
        source.onNext(1);
        source.onNext(1);
        ts.awaitCount(3);
        // Advance time far enough to evict the group.
        // NOTE -- Comment this line out to make the test "pass".
        testTicker.tick = TimeUnit.SECONDS.toNanos(6);
        // Send more data in the group (triggering eviction and recreation)
        source.onNext(1);
        // Wait for the last 2 and then cancel the subscription
        ts.awaitCount(4);
        ts.cancel();
        // Observe the result.  Note that right now the result differs depending on whether eviction occurred or
        // not.  The observed sequence in that case is:  Group completed, Outer group by canceled., Group canceled.
        // The addition of the "Group completed" is actually fine, but the fact that the cancel doesn't reach the
        // source seems like a bug.  Commenting out the setting of "tick" above will produce the "expected" sequence.
        System.out.println(list);
        Assert.assertTrue(list.contains("Source canceled"));
        Assert.assertEquals(// this is here when eviction occurs
        // This is *not* here when eviction occurs
        Arrays.asList("Group completed", "Outer group by canceled", "Group canceled", "Source canceled"), list);
    }

    @Test
    public void testCancellationOfUpstreamWhenGroupedFlowableCompletes() {
        final AtomicBoolean cancelled = new AtomicBoolean();
        // 
        // 
        // 
        Flowable.just(1).repeat().doOnCancel(new Action() {
            @Override
            public void run() throws Exception {
                cancelled.set(true);
            }
        }).groupBy(Functions.<Integer>identity(), Functions.<Integer>identity()).flatMap(new Function<GroupedFlowable<Integer, Integer>, Publisher<? extends Object>>() {
            @Override
            public io.reactivex.Publisher<? extends Object> apply(GroupedFlowable<Integer, Integer> g) throws Exception {
                return g.first(0).toFlowable();
            }
        }).take(4).test().assertComplete();
        Assert.assertTrue(cancelled.get());
    }

    // not thread safe
    private static final class SingleThreadEvictingHashMap<K, V> implements Map<K, V> {
        private final List<K> list = new ArrayList<K>();

        private final Map<K, V> map = new HashMap<K, V>();

        private final int maxSize;

        private final io.reactivex.Consumer<V> evictedListener;

        SingleThreadEvictingHashMap(int maxSize, Consumer<V> evictedListener) {
            this.maxSize = maxSize;
            this.evictedListener = evictedListener;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return map.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return map.containsValue(value);
        }

        @Override
        public V get(Object key) {
            return map.get(key);
        }

        @Override
        public V put(K key, V value) {
            list.remove(key);
            V v;
            if (((maxSize) > 0) && ((list.size()) == (maxSize))) {
                // remove first
                K k = list.get(0);
                list.remove(0);
                v = map.remove(k);
            } else {
                v = null;
            }
            list.add(key);
            V result = map.put(key, value);
            if (v != null) {
                try {
                    evictedListener.accept(v);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return result;
        }

        @Override
        public V remove(Object key) {
            list.remove(key);
            return map.remove(key);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> m) {
            for (Map.Entry<? extends K, ? extends V> entry : m.entrySet()) {
                put(entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void clear() {
            list.clear();
            map.clear();
        }

        @Override
        public Set<K> keySet() {
            return map.keySet();
        }

        @Override
        public Collection<V> values() {
            return map.values();
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return map.entrySet();
        }
    }
}

