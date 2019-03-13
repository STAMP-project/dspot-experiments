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


import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.subjects.PublishSubject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class ObservableRetryWithPredicateTest {
    BiPredicate<Integer, Throwable> retryTwice = new BiPredicate<Integer, Throwable>() {
        @Override
        public boolean test(Integer t1, Throwable t2) {
            return t1 <= 2;
        }
    };

    BiPredicate<Integer, Throwable> retry5 = new BiPredicate<Integer, Throwable>() {
        @Override
        public boolean test(Integer t1, Throwable t2) {
            return t1 <= 5;
        }
    };

    BiPredicate<Integer, Throwable> retryOnTestException = new BiPredicate<Integer, Throwable>() {
        @Override
        public boolean test(Integer t1, Throwable t2) {
            return t2 instanceof IOException;
        }
    };

    @Test
    public void testWithNothingToRetry() {
        Observable<Integer> source = range(0, 3);
        Observer<Integer> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.retry(retryTwice).subscribe(o);
        inOrder.verify(o).onNext(0);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(2);
        inOrder.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testRetryTwice() {
        Observable<Integer> source = Observable.unsafeCreate(new ObservableSource<Integer>() {
            int count;

            @Override
            public void subscribe(Observer<? super Integer> t1) {
                t1.onSubscribe(Disposables.empty());
                (count)++;
                t1.onNext(0);
                t1.onNext(1);
                if ((count) == 1) {
                    t1.onError(new TestException());
                    return;
                }
                t1.onNext(2);
                t1.onNext(3);
                t1.onComplete();
            }
        });
        Observer<Integer> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.retry(retryTwice).subscribe(o);
        inOrder.verify(o).onNext(0);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(0);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(2);
        inOrder.verify(o).onNext(3);
        inOrder.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testRetryTwiceAndGiveUp() {
        Observable<Integer> source = Observable.unsafeCreate(new ObservableSource<Integer>() {
            @Override
            public void subscribe(Observer<? super Integer> t1) {
                t1.onSubscribe(Disposables.empty());
                t1.onNext(0);
                t1.onNext(1);
                t1.onError(new TestException());
            }
        });
        Observer<Integer> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.retry(retryTwice).subscribe(o);
        inOrder.verify(o).onNext(0);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(0);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(0);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onError(ArgumentMatchers.any(TestException.class));
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testRetryOnSpecificException() {
        Observable<Integer> source = Observable.unsafeCreate(new ObservableSource<Integer>() {
            int count;

            @Override
            public void subscribe(Observer<? super Integer> t1) {
                t1.onSubscribe(Disposables.empty());
                (count)++;
                t1.onNext(0);
                t1.onNext(1);
                if ((count) == 1) {
                    t1.onError(new IOException());
                    return;
                }
                t1.onNext(2);
                t1.onNext(3);
                t1.onComplete();
            }
        });
        Observer<Integer> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.retry(retryOnTestException).subscribe(o);
        inOrder.verify(o).onNext(0);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(0);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(2);
        inOrder.verify(o).onNext(3);
        inOrder.verify(o).onComplete();
        Mockito.verify(o, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void testRetryOnSpecificExceptionAndNotOther() {
        final IOException ioe = new IOException();
        final TestException te = new TestException();
        Observable<Integer> source = Observable.unsafeCreate(new ObservableSource<Integer>() {
            int count;

            @Override
            public void subscribe(Observer<? super Integer> t1) {
                t1.onSubscribe(Disposables.empty());
                (count)++;
                t1.onNext(0);
                t1.onNext(1);
                if ((count) == 1) {
                    t1.onError(ioe);
                    return;
                }
                t1.onNext(2);
                t1.onNext(3);
                t1.onError(te);
            }
        });
        Observer<Integer> o = mockObserver();
        InOrder inOrder = Mockito.inOrder(o);
        source.retry(retryOnTestException).subscribe(o);
        inOrder.verify(o).onNext(0);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(0);
        inOrder.verify(o).onNext(1);
        inOrder.verify(o).onNext(2);
        inOrder.verify(o).onNext(3);
        inOrder.verify(o).onError(te);
        Mockito.verify(o, Mockito.never()).onError(ioe);
        Mockito.verify(o, Mockito.never()).onComplete();
    }

    @Test
    public void testUnsubscribeFromRetry() {
        PublishSubject<Integer> subject = PublishSubject.create();
        final AtomicInteger count = new AtomicInteger(0);
        Disposable sub = subject.retry(retryTwice).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer n) {
                count.incrementAndGet();
            }
        });
        subject.onNext(1);
        sub.dispose();
        subject.onNext(2);
        Assert.assertEquals(1, count.get());
    }

    @Test(timeout = 10000)
    public void testUnsubscribeAfterError() {
        Observer<Long> observer = mockObserver();
        // Observable that always fails after 100ms
        ObservableRetryTest.SlowObservable so = new ObservableRetryTest.SlowObservable(100, 0, "testUnsubscribeAfterError");
        Observable<Long> o = unsafeCreate(so).retry(retry5);
        ObservableRetryTest.AsyncObserver<Long> async = new ObservableRetryTest.AsyncObserver<Long>(observer);
        o.subscribe(async);
        async.await();
        InOrder inOrder = Mockito.inOrder(observer);
        // Should fail once
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(observer, Mockito.never()).onComplete();
        Assert.assertEquals("Start 6 threads, retry 5 then fail on 6", 6, so.efforts.get());
        Assert.assertEquals("Only 1 active subscription", 1, so.maxActive.get());
    }

    @Test(timeout = 10000)
    public void testTimeoutWithRetry() {
        Observer<Long> observer = mockObserver();
        // Observable that sends every 100ms (timeout fails instead)
        ObservableRetryTest.SlowObservable so = new ObservableRetryTest.SlowObservable(100, 10, "testTimeoutWithRetry");
        Observable<Long> o = unsafeCreate(so).timeout(80, TimeUnit.MILLISECONDS).retry(retry5);
        ObservableRetryTest.AsyncObserver<Long> async = new ObservableRetryTest.AsyncObserver<Long>(observer);
        o.subscribe(async);
        async.await();
        InOrder inOrder = Mockito.inOrder(observer);
        // Should fail once
        inOrder.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        inOrder.verify(observer, Mockito.never()).onComplete();
        Assert.assertEquals("Start 6 threads, retry 5 then fail on 6", 6, so.efforts.get());
    }

    @Test
    public void testIssue2826() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        final RuntimeException e = new RuntimeException("You shall not pass");
        final AtomicInteger c = new AtomicInteger();
        Observable.just(1).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                c.incrementAndGet();
                throw e;
            }
        }).retry(retry5).subscribe(to);
        to.assertTerminated();
        Assert.assertEquals(6, c.get());
        Assert.assertEquals(Collections.singletonList(e), to.errors());
    }

    @Test
    public void testJustAndRetry() throws Exception {
        final AtomicBoolean throwException = new AtomicBoolean(true);
        int value = Observable.just(1).map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer t1) {
                if (throwException.compareAndSet(true, false)) {
                    throw new TestException();
                }
                return t1;
            }
        }).retry(1).blockingSingle();
        Assert.assertEquals(1, value);
    }

    @Test
    public void testIssue3008RetryWithPredicate() {
        final List<Long> list = new CopyOnWriteArrayList<Long>();
        final AtomicBoolean isFirst = new AtomicBoolean(true);
        <Long>just(1L, 2L, 3L).map(new Function<Long, Long>() {
            @Override
            public Long apply(Long x) {
                System.out.println(("map " + x));
                if ((x == 2) && (isFirst.getAndSet(false))) {
                    throw new RuntimeException("retryable error");
                }
                return x;
            }
        }).retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer t1, Throwable t2) {
                return true;
            }
        }).forEach(new Consumer<Long>() {
            @Override
            public void accept(Long t) {
                System.out.println(t);
                list.add(t);
            }
        });
        Assert.assertEquals(Arrays.asList(1L, 1L, 2L, 3L), list);
    }

    @Test
    public void testIssue3008RetryInfinite() {
        final List<Long> list = new CopyOnWriteArrayList<Long>();
        final AtomicBoolean isFirst = new AtomicBoolean(true);
        <Long>just(1L, 2L, 3L).map(new Function<Long, Long>() {
            @Override
            public Long apply(Long x) {
                System.out.println(("map " + x));
                if ((x == 2) && (isFirst.getAndSet(false))) {
                    throw new RuntimeException("retryable error");
                }
                return x;
            }
        }).retry().forEach(new Consumer<Long>() {
            @Override
            public void accept(Long t) {
                System.out.println(t);
                list.add(t);
            }
        });
        Assert.assertEquals(Arrays.asList(1L, 1L, 2L, 3L), list);
    }

    @Test
    public void predicateThrows() {
        TestObserver<Object> to = error(new TestException("Outer")).retry(new Predicate<Throwable>() {
            @Override
            public boolean test(Throwable e) throws Exception {
                throw new TestException("Inner");
            }
        }).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Outer");
        TestHelper.assertError(errors, 1, TestException.class, "Inner");
    }

    @Test
    public void dontRetry() {
        error(new TestException("Outer")).retry(io.reactivex.internal.functions.Functions.alwaysFalse()).test().assertFailureAndMessage(TestException.class, "Outer");
    }

    @Test
    public void retryDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final TestObserver<Integer> to = ps.retry(io.reactivex.internal.functions.Functions.alwaysTrue()).test();
            final TestException ex = new TestException();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.onError(ex);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
            to.assertEmpty();
        }
    }

    @Test
    public void bipredicateThrows() {
        TestObserver<Object> to = error(new TestException("Outer")).retry(new BiPredicate<Integer, Throwable>() {
            @Override
            public boolean test(Integer n, Throwable e) throws Exception {
                throw new TestException("Inner");
            }
        }).test().assertFailure(CompositeException.class);
        List<Throwable> errors = TestHelper.compositeList(to.errors().get(0));
        TestHelper.assertError(errors, 0, TestException.class, "Outer");
        TestHelper.assertError(errors, 1, TestException.class, "Inner");
    }

    @Test
    public void retryBiPredicateDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Integer> ps = PublishSubject.create();
            final TestObserver<Integer> to = ps.retry(new BiPredicate<Object, Object>() {
                @Override
                public boolean test(Object t1, Object t2) throws Exception {
                    return true;
                }
            }).test();
            final TestException ex = new TestException();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ps.onError(ex);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
            to.assertEmpty();
        }
    }
}

