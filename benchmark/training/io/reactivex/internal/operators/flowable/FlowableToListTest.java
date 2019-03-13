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
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableToListTest {
    @Test
    public void testListFlowable() {
        Flowable<String> w = Flowable.fromIterable(Arrays.asList("one", "two", "three"));
        Flowable<List<String>> flowable = w.toList().toFlowable();
        Subscriber<List<String>> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(Arrays.asList("one", "two", "three"));
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testListViaFlowableFlowable() {
        Flowable<String> w = Flowable.fromIterable(Arrays.asList("one", "two", "three"));
        Flowable<List<String>> flowable = w.toList().toFlowable();
        Subscriber<List<String>> subscriber = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(Arrays.asList("one", "two", "three"));
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
    }

    @Test
    public void testListMultipleSubscribersFlowable() {
        Flowable<String> w = Flowable.fromIterable(Arrays.asList("one", "two", "three"));
        Flowable<List<String>> flowable = w.toList().toFlowable();
        Subscriber<List<String>> subscriber1 = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber1);
        Subscriber<List<String>> subscriber2 = TestHelper.mockSubscriber();
        flowable.subscribe(subscriber2);
        List<String> expected = Arrays.asList("one", "two", "three");
        Mockito.verify(subscriber1, Mockito.times(1)).onNext(expected);
        Mockito.verify(subscriber1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber1, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber2, Mockito.times(1)).onNext(expected);
        Mockito.verify(subscriber2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber2, Mockito.times(1)).onComplete();
    }

    @Test
    public void testListWithBlockingFirstFlowable() {
        Flowable<String> f = Flowable.fromIterable(Arrays.asList("one", "two", "three"));
        List<String> actual = f.toList().toFlowable().blockingFirst();
        Assert.assertEquals(Arrays.asList("one", "two", "three"), actual);
    }

    @Test
    public void testBackpressureHonoredFlowable() {
        Flowable<List<Integer>> w = Flowable.just(1, 2, 3, 4, 5).toList().toFlowable();
        TestSubscriber<List<Integer>> ts = new TestSubscriber<List<Integer>>(0L);
        w.subscribe(ts);
        ts.assertNoValues();
        ts.assertNoErrors();
        ts.assertNotComplete();
        ts.request(1);
        ts.assertValue(Arrays.asList(1, 2, 3, 4, 5));
        ts.assertNoErrors();
        ts.assertComplete();
        ts.request(1);
        ts.assertValue(Arrays.asList(1, 2, 3, 4, 5));
        ts.assertNoErrors();
        ts.assertComplete();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void capacityHintFlowable() {
        Flowable.range(1, 10).toList(4).toFlowable().test().assertResult(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void testList() {
        Flowable<String> w = Flowable.fromIterable(Arrays.asList("one", "two", "three"));
        Single<List<String>> single = w.toList();
        SingleObserver<List<String>> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(Arrays.asList("one", "two", "three"));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testListViaFlowable() {
        Flowable<String> w = Flowable.fromIterable(Arrays.asList("one", "two", "three"));
        Single<List<String>> single = w.toList();
        SingleObserver<List<String>> observer = TestHelper.mockSingleObserver();
        single.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onSuccess(Arrays.asList("one", "two", "three"));
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testListMultipleSubscribers() {
        Flowable<String> w = Flowable.fromIterable(Arrays.asList("one", "two", "three"));
        Single<List<String>> single = w.toList();
        SingleObserver<List<String>> o1 = TestHelper.mockSingleObserver();
        single.subscribe(o1);
        SingleObserver<List<String>> o2 = TestHelper.mockSingleObserver();
        single.subscribe(o2);
        List<String> expected = Arrays.asList("one", "two", "three");
        Mockito.verify(o1, Mockito.times(1)).onSuccess(expected);
        Mockito.verify(o1, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(o2, Mockito.times(1)).onSuccess(expected);
        Mockito.verify(o2, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testListWithBlockingFirst() {
        Flowable<String> f = Flowable.fromIterable(Arrays.asList("one", "two", "three"));
        List<String> actual = f.toList().blockingGet();
        Assert.assertEquals(Arrays.asList("one", "two", "three"), actual);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void capacityHint() {
        Flowable.range(1, 10).toList(4).test().assertResult(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10));
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).toList().toFlowable());
        TestHelper.checkDisposed(Flowable.just(1).toList());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void error() {
        Flowable.error(new TestException()).toList().toFlowable().test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void errorSingle() {
        Flowable.error(new TestException()).toList().test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void collectionSupplierThrows() {
        Flowable.just(1).toList(new Callable<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() throws Exception {
                throw new TestException();
            }
        }).toFlowable().test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void collectionSupplierReturnsNull() {
        Flowable.just(1).toList(new Callable<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() throws Exception {
                return null;
            }
        }).toFlowable().test().assertFailure(NullPointerException.class).assertErrorMessage("The collectionSupplier returned a null collection. Null values are generally not allowed in 2.x operators and sources.");
    }

    @SuppressWarnings("unchecked")
    @Test
    public void singleCollectionSupplierThrows() {
        Flowable.just(1).toList(new Callable<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() throws Exception {
                throw new TestException();
            }
        }).test().assertFailure(TestException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void singleCollectionSupplierReturnsNull() {
        Flowable.just(1).toList(new Callable<Collection<Integer>>() {
            @Override
            public Collection<Integer> call() throws Exception {
                return null;
            }
        }).test().assertFailure(NullPointerException.class).assertErrorMessage("The collectionSupplier returned a null collection. Null values are generally not allowed in 2.x operators and sources.");
    }

    @Test
    public void onNextCancelRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            final TestObserver<List<Integer>> to = pp.toList().test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void onNextCancelRaceFlowable() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            final TestSubscriber<List<Integer>> ts = pp.toList().toFlowable().test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onNext(1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }

    @Test
    public void onCompleteCancelRaceFlowable() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Integer> pp = PublishProcessor.create();
            final TestSubscriber<List<Integer>> ts = pp.toList().toFlowable().test();
            pp.onNext(1);
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    pp.onComplete();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    ts.cancel();
                }
            };
            TestHelper.race(r1, r2);
            if ((ts.valueCount()) != 0) {
                ts.assertValue(Arrays.asList(1)).assertNoErrors();
            }
        }
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new io.reactivex.functions.Function<Flowable<Object>, Flowable<List<Object>>>() {
            @Override
            public io.reactivex.Flowable<List<Object>> apply(Flowable<Object> f) throws Exception {
                return f.toList().toFlowable();
            }
        });
        TestHelper.checkDoubleOnSubscribeFlowableToSingle(new io.reactivex.functions.Function<Flowable<Object>, Single<List<Object>>>() {
            @Override
            public io.reactivex.Single<List<Object>> apply(Flowable<Object> f) throws Exception {
                return f.toList();
            }
        });
    }
}

