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
package io.reactivex.internal.operators.observable;


import io.reactivex.Function;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observables.GroupedObservable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


public class ObservableGroupByTest {
    final Function<String, Integer> length = new Function<String, Integer>() {
        @Override
        public Integer apply(String s) {
            return s.length();
        }
    };

    @Test
    public void testGroupBy() {
        Observable<String> source = Observable.just("one", "two", "three", "four", "five", "six");
        Observable<GroupedObservable<Integer, String>> grouped = source.groupBy(length);
        Map<Integer, Collection<String>> map = toMap(grouped);
        Assert.assertEquals(3, map.size());
        Assert.assertArrayEquals(Arrays.asList("one", "two", "six").toArray(), map.get(3).toArray());
        Assert.assertArrayEquals(Arrays.asList("four", "five").toArray(), map.get(4).toArray());
        Assert.assertArrayEquals(Arrays.asList("three").toArray(), map.get(5).toArray());
    }

    @Test
    public void testGroupByWithElementSelector() {
        Observable<String> source = Observable.just("one", "two", "three", "four", "five", "six");
        Observable<GroupedObservable<Integer, Integer>> grouped = source.groupBy(length, length);
        Map<Integer, Collection<Integer>> map = toMap(grouped);
        Assert.assertEquals(3, map.size());
        Assert.assertArrayEquals(Arrays.asList(3, 3, 3).toArray(), map.get(3).toArray());
        Assert.assertArrayEquals(Arrays.asList(4, 4).toArray(), map.get(4).toArray());
        Assert.assertArrayEquals(Arrays.asList(5).toArray(), map.get(5).toArray());
    }

    @Test
    public void testGroupByWithElementSelector2() {
        Observable<String> source = Observable.just("one", "two", "three", "four", "five", "six");
        Observable<GroupedObservable<Integer, Integer>> grouped = source.groupBy(length, length);
        Map<Integer, Collection<Integer>> map = toMap(grouped);
        Assert.assertEquals(3, map.size());
        Assert.assertArrayEquals(Arrays.asList(3, 3, 3).toArray(), map.get(3).toArray());
        Assert.assertArrayEquals(Arrays.asList(4, 4).toArray(), map.get(4).toArray());
        Assert.assertArrayEquals(Arrays.asList(5).toArray(), map.get(5).toArray());
    }

    @Test
    public void testEmpty() {
        Observable<String> source = empty();
        Observable<GroupedObservable<Integer, String>> grouped = source.groupBy(length);
        Map<Integer, Collection<String>> map = toMap(grouped);
        Assert.assertTrue(map.isEmpty());
    }

    @Test
    public void testError() {
        Observable<String> sourceStrings = Observable.just("one", "two", "three", "four", "five", "six");
        Observable<String> errorSource = Observable.error(new RuntimeException("forced failure"));
        Observable<String> source = Observable.concat(sourceStrings, errorSource);
        Observable<GroupedObservable<Integer, String>> grouped = source.groupBy(length);
        final AtomicInteger groupCounter = new AtomicInteger();
        final AtomicInteger eventCounter = new AtomicInteger();
        final AtomicReference<Throwable> error = new AtomicReference<Throwable>();
        grouped.flatMap(new Function<GroupedObservable<Integer, String>, Observable<String>>() {
            @Override
            public Observable<String> apply(final GroupedObservable<Integer, String> o) {
                groupCounter.incrementAndGet();
                return o.map(new Function<String, String>() {
                    @Override
                    public String apply(String v) {
                        return (("Event => key: " + (o.getKey())) + " value: ") + v;
                    }
                });
            }
        }).subscribe(new DefaultObserver<String>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
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
    }

    /**
     * Assert that only a single subscription to a stream occurs and that all events are received.
     *
     * @throws Throwable
     * 		some method may throw
     */
    @Test
    public void testGroupedEventStream() throws Throwable {
        final AtomicInteger eventCounter = new AtomicInteger();
        final AtomicInteger subscribeCounter = new AtomicInteger();
        final AtomicInteger groupCounter = new AtomicInteger();
        final CountDownLatch latch = new CountDownLatch(1);
        final int count = 100;
        final int groupCount = 2;
        Observable<ObservableGroupByTest.Event> es = Observable.unsafeCreate(new ObservableSource<ObservableGroupByTest.Event>() {
            @Override
            public void subscribe(final Observer<? super ObservableGroupByTest.Event> observer) {
                observer.onSubscribe(Disposables.empty());
                System.out.println("*** Subscribing to EventStream ***");
                subscribeCounter.incrementAndGet();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < count; i++) {
                            ObservableGroupByTest.Event e = new ObservableGroupByTest.Event();
                            e.source = i % groupCount;
                            e.message = "Event-" + i;
                            observer.onNext(e);
                        }
                        observer.onComplete();
                    }
                }).start();
            }
        });
        es.groupBy(new Function<ObservableGroupByTest.Event, Integer>() {
            @Override
            public Integer apply(ObservableGroupByTest.Event e) {
                return e.source;
            }
        }).flatMap(new Function<GroupedObservable<Integer, ObservableGroupByTest.Event>, Observable<String>>() {
            @Override
            public Observable<String> apply(GroupedObservable<Integer, io.reactivex.internal.operators.observable.Event> eventGroupedObservable) {
                System.out.println(("GroupedObservable Key: " + (eventGroupedObservable.getKey())));
                groupCounter.incrementAndGet();
                return eventGroupedObservable.map(new Function<io.reactivex.internal.operators.observable.Event, String>() {
                    @Override
                    public String apply(io.reactivex.internal.operators.observable.Event event) {
                        return (("Source: " + event.source) + "  Message: ") + event.message;
                    }
                });
            }
        }).subscribe(new DefaultObserver<String>() {
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
        SYNC_INFINITE_OBSERVABLE_OF_EVENT(4, subscribeCounter, sentEventCounter).groupBy(new Function<ObservableGroupByTest.Event, Integer>() {
            @Override
            public Integer apply(ObservableGroupByTest.Event e) {
                return e.source;
            }
        }).take(2).flatMap(new Function<GroupedObservable<Integer, ObservableGroupByTest.Event>, Observable<String>>() {
            @Override
            public Observable<String> apply(GroupedObservable<Integer, io.reactivex.internal.operators.observable.Event> eventGroupedObservable) {
                return eventGroupedObservable.map(new Function<io.reactivex.internal.operators.observable.Event, String>() {
                    @Override
                    public String apply(io.reactivex.internal.operators.observable.Event event) {
                        return (("testUnsubscribe => Source: " + event.source) + "  Message: ") + event.message;
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
        SYNC_INFINITE_OBSERVABLE_OF_EVENT(4, subscribeCounter, sentEventCounter).groupBy(new Function<ObservableGroupByTest.Event, Integer>() {
            @Override
            public Integer apply(ObservableGroupByTest.Event e) {
                return e.source;
            }
        }).take(2).flatMap(new Function<GroupedObservable<Integer, ObservableGroupByTest.Event>, Observable<String>>() {
            @Override
            public Observable<String> apply(GroupedObservable<Integer, io.reactivex.internal.operators.observable.Event> eventGroupedObservable) {
                int numToTake = 0;
                if ((eventGroupedObservable.getKey()) == 1) {
                    numToTake = 10;
                } else
                    if ((eventGroupedObservable.getKey()) == 2) {
                        numToTake = 5;
                    }

                return eventGroupedObservable.take(numToTake).map(new Function<io.reactivex.internal.operators.observable.Event, String>() {
                    @Override
                    public String apply(io.reactivex.internal.operators.observable.Event event) {
                        return (("testUnsubscribe => Source: " + event.source) + "  Message: ") + event.message;
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
        range(0, 100).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return i % 2;
            }
        }).flatMap(new Function<GroupedObservable<Integer, Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(GroupedObservable<Integer, Integer> group) {
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
        }).subscribe(new DefaultObserver<Integer>() {
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
        range(0, 100).groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return i % 2;
            }
        }).subscribe(new DefaultObserver<GroupedObservable<Integer, Integer>>() {
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
            public void onNext(GroupedObservable<Integer, Integer> s) {
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
        SYNC_INFINITE_OBSERVABLE_OF_EVENT(4, subscribeCounter, sentEventCounter).groupBy(new Function<ObservableGroupByTest.Event, Integer>() {
            @Override
            public Integer apply(ObservableGroupByTest.Event e) {
                return e.source;
            }
        }).flatMap(new Function<GroupedObservable<Integer, ObservableGroupByTest.Event>, Observable<String>>() {
            @Override
            public Observable<String> apply(GroupedObservable<Integer, io.reactivex.internal.operators.observable.Event> eventGroupedObservable) {
                Observable<io.reactivex.internal.operators.observable.Event> eventStream = eventGroupedObservable;
                if ((eventGroupedObservable.getKey()) >= 2) {
                    // filter these
                    eventStream = eventGroupedObservable.filter(new Predicate<io.reactivex.internal.operators.observable.Event>() {
                        @Override
                        public boolean test(io.reactivex.internal.operators.observable.Event t1) {
                            return false;
                        }
                    });
                }
                return eventStream.map(new Function<io.reactivex.internal.operators.observable.Event, String>() {
                    @Override
                    public String apply(io.reactivex.internal.operators.observable.Event event) {
                        return (("testUnsubscribe => Source: " + event.source) + "  Message: ") + event.message;
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
        Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> sub) {
                sub.onSubscribe(Disposables.empty());
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
        }).flatMap(new Function<GroupedObservable<Integer, Integer>, Observable<String>>() {
            @Override
            public Observable<String> apply(final GroupedObservable<Integer, Integer> group) {
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
        Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> sub) {
                sub.onSubscribe(Disposables.empty());
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
        }).flatMap(new Function<GroupedObservable<Integer, Integer>, Observable<String>>() {
            @Override
            public Observable<String> apply(final GroupedObservable<Integer, Integer> group) {
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
        Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> sub) {
                sub.onSubscribe(Disposables.empty());
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
        }).flatMap(new Function<GroupedObservable<Integer, Integer>, Observable<String>>() {
            @Override
            public Observable<String> apply(final GroupedObservable<Integer, Integer> group) {
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
        Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> sub) {
                sub.onSubscribe(Disposables.empty());
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
        }).flatMap(new Function<GroupedObservable<Integer, Integer>, Observable<String>>() {
            @Override
            public Observable<String> apply(final GroupedObservable<Integer, Integer> group) {
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
        Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> sub) {
                sub.onSubscribe(Disposables.empty());
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
        }).flatMap(new Function<GroupedObservable<Integer, Integer>, Observable<String>>() {
            @Override
            public Observable<String> apply(final GroupedObservable<Integer, Integer> group) {
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
        Observable<Long> source = interval(10, TimeUnit.MILLISECONDS).take(1);
        // apply groupBy to the source
        Observable<GroupedObservable<Boolean, Long>> stream = source.groupBy(ObservableGroupByTest.IS_EVEN);
        // create two observers
        Observer<GroupedObservable<Boolean, Long>> o1 = mockObserver();
        Observer<GroupedObservable<Boolean, Long>> o2 = mockObserver();
        // subscribe with the observers
        stream.subscribe(o1);
        stream.subscribe(o2);
        // check that subscriptions were successful
        Mockito.verify(o1, never()).onError(Mockito.<Throwable>any());
        Mockito.verify(o2, never()).onError(Mockito.<Throwable>any());
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
        TestObserver<String> to = new TestObserver<String>();
        range(1, 4000).groupBy(ObservableGroupByTest.IS_EVEN2).flatMap(new Function<GroupedObservable<Boolean, Integer>, Observable<String>>() {
            @Override
            public Observable<String> apply(final GroupedObservable<Boolean, Integer> g) {
                return g.observeOn(Schedulers.computation()).map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer l) {
                        if (g.getKey()) {
                            try {
                                Thread.sleep(1);
                            } catch ( e) {
                            }
                            return l + " is even.";
                        } else {
                            return l + " is odd.";
                        }
                    }
                });
            }
        }).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
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
        Observable<String> source = fromIterable(Arrays.asList("  foo", " FoO ", "baR  ", "foO ", " Baz   ", "  qux ", "   bar", " BAR  ", "FOO ", "baz  ", " bAZ ", "    fOo    "));
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
        Observable<String> m = source.groupBy(keysel, valuesel).flatMap(new Function<GroupedObservable<String, String>, Observable<String>>() {
            @Override
            public Observable<String> apply(final GroupedObservable<String, String> g) {
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
        TestObserver<String> to = new TestObserver<String>();
        m.subscribe(to);
        to.awaitTerminalEvent();
        System.out.println(("ts .get " + (to.values())));
        to.assertNoErrors();
        Assert.assertEquals(to.values(), Arrays.asList("foo-0", "foo-1", "bar-0", "foo-0", "baz-0", "qux-0", "bar-1", "bar-0", "foo-1", "baz-1", "baz-0", "foo-0"));
    }

    @Test
    public void keySelectorThrows() {
        Observable<Integer> source = Observable.just(0, 1, 2, 3, 4, 5, 6);
        Observable<Integer> m = source.groupBy(fail(0), dbl).flatMap(io.reactivex.internal.operators.observable.FLATTEN_INTEGER);
        TestObserver<Integer> to = new TestObserver<Integer>();
        m.subscribe(to);
        to.awaitTerminalEvent();
        Assert.assertEquals(1, to.errorCount());
        to.assertNoValues();
    }

    @Test
    public void valueSelectorThrows() {
        Observable<Integer> source = Observable.just(0, 1, 2, 3, 4, 5, 6);
        Observable<Integer> m = source.groupBy(identity, fail(0)).flatMap(io.reactivex.internal.operators.observable.FLATTEN_INTEGER);
        TestObserver<Integer> to = new TestObserver<Integer>();
        m.subscribe(to);
        to.awaitTerminalEvent();
        Assert.assertEquals(1, to.errorCount());
        to.assertNoValues();
    }

    @Test
    public void innerEscapeCompleted() {
        Observable<Integer> source = just(0);
        Observable<Integer> m = source.groupBy(identity, dbl).flatMap(io.reactivex.internal.operators.observable.FLATTEN_INTEGER);
        TestObserver<Object> to = new TestObserver<Object>();
        m.subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
        System.out.println(to.values());
    }

    /**
     * Assert we get an IllegalStateException if trying to subscribe to an inner GroupedObservable more than once.
     */
    @Test
    public void testExceptionIfSubscribeToChildMoreThanOnce() {
        Observable<Integer> source = just(0);
        final AtomicReference<GroupedObservable<Integer, Integer>> inner = new AtomicReference<GroupedObservable<Integer, Integer>>();
        Observable<GroupedObservable<Integer, Integer>> m = source.groupBy(identity, dbl);
        m.subscribe(new Consumer<GroupedObservable<Integer, Integer>>() {
            @Override
            public void accept(GroupedObservable<Integer, Integer> t1) {
                inner.set(t1);
            }
        });
        inner.get().subscribe();
        Observer<Integer> o2 = mockObserver();
        inner.get().subscribe(o2);
        Mockito.verify(o2, never()).onComplete();
        Mockito.verify(o2, never()).onNext(anyInt());
        verify(o2).onError(any(IllegalStateException.class));
    }

    @Test
    public void testError2() {
        Observable<Integer> source = Observable.concat(just(0), <Integer>error(new TestException("Forced failure")));
        Observable<Integer> m = source.groupBy(identity, dbl).flatMap(io.reactivex.internal.operators.observable.FLATTEN_INTEGER);
        TestObserver<Object> to = new TestObserver<Object>();
        m.subscribe(to);
        to.awaitTerminalEvent();
        Assert.assertEquals(1, to.errorCount());
        to.assertValueCount(1);
    }

    @Test
    public void testgroupByBackpressure() throws InterruptedException {
        TestObserver<String> to = new TestObserver<String>();
        range(1, 4000).groupBy(ObservableGroupByTest.IS_EVEN2).flatMap(new Function<GroupedObservable<Boolean, Integer>, Observable<String>>() {
            @Override
            public Observable<String> apply(final GroupedObservable<Boolean, Integer> g) {
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
                                } catch ( e) {
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
        }).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
    }

    @Test
    public void testgroupByBackpressure2() throws InterruptedException {
        TestObserver<String> to = new TestObserver<String>();
        range(1, 4000).groupBy(ObservableGroupByTest.IS_EVEN2).flatMap(new Function<GroupedObservable<Boolean, Integer>, Observable<String>>() {
            @Override
            public Observable<String> apply(final GroupedObservable<Boolean, Integer> g) {
                return g.take(2).observeOn(Schedulers.computation()).map(new Function<Integer, String>() {
                    @Override
                    public String apply(Integer l) {
                        if (g.getKey()) {
                            try {
                                Thread.sleep(1);
                            } catch ( e) {
                            }
                            return l + " is even.";
                        } else {
                            return l + " is odd.";
                        }
                    }
                });
            }
        }).subscribe(to);
        to.awaitTerminalEvent();
        to.assertNoErrors();
    }

    static Function<GroupedObservable<Integer, Integer>, Observable<Integer>> FLATTEN_INTEGER = new Function<GroupedObservable<Integer, Integer>, Observable<Integer>>() {
        @Override
        public Observable<Integer> apply(GroupedObservable<Integer, Integer> t) {
            return t;
        }
    };

    @Test
    public void testGroupByWithNullKey() {
        final String[] key = new String[]{ "uninitialized" };
        final List<String> values = new ArrayList<String>();
        Observable.just("a", "b", "c").groupBy(new Function<String, String>() {
            @Override
            public String apply(String value) {
                return null;
            }
        }).subscribe(new Consumer<GroupedObservable<String, String>>() {
            @Override
            public void accept(GroupedObservable<String, String> groupedObservable) {
                key[0] = groupedObservable.getKey();
                groupedObservable.subscribe(new Consumer<String>() {
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
        final Disposable upstream = Mockito.mock(io.reactivex.disposables.Disposable.class);
        Observable<Integer> o = Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observer) {
                observer.onSubscribe(upstream);
            }
        });
        TestObserver<Object> to = new TestObserver<Object>();
        o.groupBy(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return null;
            }
        }).subscribe(to);
        to.dispose();
        verify(upstream).dispose();
    }

    @Test
    public void testGroupByShouldPropagateError() {
        final Throwable e = new RuntimeException("Oops");
        final TestObserver<Integer> inner1 = new TestObserver<Integer>();
        final TestObserver<Integer> inner2 = new TestObserver<Integer>();
        final TestObserver<GroupedObservable<Integer, Integer>> outer = new TestObserver<GroupedObservable<Integer, Integer>>(new DefaultObserver<GroupedObservable<Integer, Integer>>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(GroupedObservable<Integer, Integer> o) {
                if ((o.getKey()) == 0) {
                    o.subscribe(inner1);
                } else {
                    o.subscribe(inner2);
                }
            }
        });
        unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> observer) {
                observer.onSubscribe(Disposables.empty());
                observer.onNext(0);
                observer.onNext(1);
                observer.onError(e);
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
    public void keySelectorAndDelayError() {
        just(1).concatWith(<Integer>error(new TestException())).groupBy(Functions.<Integer>identity(), true).flatMap(new Function<GroupedObservable<Integer, Integer>, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(GroupedObservable<Integer, Integer> g) throws Exception {
                return g;
            }
        }).test().assertFailure(TestException.class, 1);
    }

    @Test
    public void keyAndValueSelectorAndDelayError() {
        just(1).concatWith(<Integer>error(new TestException())).groupBy(Functions.<Integer>identity(), Functions.<Integer>identity(), true).flatMap(new Function<GroupedObservable<Integer, Integer>, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(GroupedObservable<Integer, Integer> g) throws Exception {
                return g;
            }
        }).test().assertFailure(TestException.class, 1);
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(just(1).groupBy(Functions.justFunction(1)));
        just(1).groupBy(Functions.justFunction(1)).doOnNext(new Consumer<GroupedObservable<Integer, Integer>>() {
            @Override
            public void accept(GroupedObservable<Integer, Integer> g) throws Exception {
                TestHelper.checkDisposed(g);
            }
        }).test();
    }

    @Test
    public void reentrantComplete() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onComplete();
                }
            }
        };
        Observable.merge(ps.groupBy(Functions.justFunction(1))).subscribe(to);
        ps.onNext(1);
        to.assertResult(1);
    }

    @Test
    public void reentrantCompleteCancel() {
        final PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = new TestObserver<Integer>() {
            @Override
            public void onNext(Integer t) {
                super.onNext(t);
                if (t == 1) {
                    ps.onComplete();
                    dispose();
                }
            }
        };
        Observable.merge(ps.groupBy(Functions.justFunction(1))).subscribe(to);
        ps.onNext(1);
        to.assertSubscribed().assertValue(1).assertNoErrors().assertNotComplete();
    }

    @Test
    public void delayErrorSimpleComplete() {
        just(1).groupBy(Functions.justFunction(1), true).flatMap(Functions.<Observable<Integer>>identity()).test().assertResult(1);
    }
}

