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


import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.util.CrashingIterable;
import io.reactivex.observers.ObserverFusion;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableFromIterableTest {
    @Test(expected = NullPointerException.class)
    public void testNull() {
        Observable.fromIterable(null);
    }

    @Test
    public void testListIterable() {
        Observable<String> o = Observable.fromIterable(Arrays.<String>asList("one", "two", "three"));
        Observer<String> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    /**
     * This tests the path that can not optimize based on size so must use setProducer.
     */
    @Test
    public void testRawIterable() {
        Iterable<String> it = new Iterable<String>() {
            @Override
            public Iterator<String> iterator() {
                return new Iterator<String>() {
                    int i;

                    @Override
                    public boolean hasNext() {
                        return (i) < 3;
                    }

                    @Override
                    public String next() {
                        return String.valueOf((++(i)));
                    }

                    @Override
                    public void remove() {
                    }
                };
            }
        };
        Observable<String> o = Observable.fromIterable(it);
        Observer<String> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext("1");
        Mockito.verify(observer, Mockito.times(1)).onNext("2");
        Mockito.verify(observer, Mockito.times(1)).onNext("3");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testObservableFromIterable() {
        Observable<String> o = Observable.fromIterable(Arrays.<String>asList("one", "two", "three"));
        Observer<String> observer = mockObserver();
        o.subscribe(observer);
        Mockito.verify(observer, Mockito.times(1)).onNext("one");
        Mockito.verify(observer, Mockito.times(1)).onNext("two");
        Mockito.verify(observer, Mockito.times(1)).onNext("three");
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
    }

    @Test
    public void testNoBackpressure() {
        Observable<Integer> o = fromIterable(Arrays.asList(1, 2, 3, 4, 5));
        TestObserver<Integer> to = new TestObserver<Integer>();
        o.subscribe(to);
        to.assertValues(1, 2, 3, 4, 5);
        to.assertTerminated();
    }

    @Test
    public void testSubscribeMultipleTimes() {
        Observable<Integer> o = fromIterable(Arrays.asList(1, 2, 3));
        for (int i = 0; i < 10; i++) {
            TestObserver<Integer> to = new TestObserver<Integer>();
            o.subscribe(to);
            to.assertValues(1, 2, 3);
            to.assertNoErrors();
            to.assertComplete();
        }
    }

    @Test
    public void testDoesNotCallIteratorHasNextMoreThanRequiredWithBackpressure() {
        final AtomicBoolean called = new AtomicBoolean(false);
        Iterable<Integer> iterable = new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    int count = 1;

                    @Override
                    public void remove() {
                        // ignore
                    }

                    @Override
                    public boolean hasNext() {
                        if ((count) > 1) {
                            called.set(true);
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public Integer next() {
                        return (count)++;
                    }
                };
            }
        };
        Observable.fromIterable(iterable).take(1).subscribe();
        Assert.assertFalse(called.get());
    }

    @Test
    public void testDoesNotCallIteratorHasNextMoreThanRequiredFastPath() {
        final AtomicBoolean called = new AtomicBoolean(false);
        Iterable<Integer> iterable = new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    @Override
                    public void remove() {
                        // ignore
                    }

                    int count = 1;

                    @Override
                    public boolean hasNext() {
                        if ((count) > 1) {
                            called.set(true);
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public Integer next() {
                        return (count)++;
                    }
                };
            }
        };
        Observable.fromIterable(iterable).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer t) {
                // unsubscribe on first emission
                cancel();
            }
        });
        Assert.assertFalse(called.get());
    }

    @Test
    public void fusionWithConcatMap() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        fromIterable(Arrays.asList(1, 2, 3, 4)).concatMap(new io.reactivex.functions.Function<Integer, ObservableSource<Integer>>() {
            @Override
            public io.reactivex.ObservableSource<Integer> apply(Integer v) {
                return range(v, 2);
            }
        }).subscribe(to);
        to.assertValues(1, 2, 2, 3, 3, 4, 4, 5);
        to.assertNoErrors();
        to.assertComplete();
    }

    @Test
    public void iteratorThrows() {
        Observable.fromIterable(new CrashingIterable(1, 100, 100)).test().assertFailureAndMessage(TestException.class, "iterator()");
    }

    @Test
    public void hasNext2Throws() {
        Observable.fromIterable(new CrashingIterable(100, 2, 100)).test().assertFailureAndMessage(TestException.class, "hasNext()", 0);
    }

    @Test
    public void hasNextCancels() {
        final TestObserver<Integer> to = new TestObserver<Integer>();
        Observable.fromIterable(new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    int count;

                    @Override
                    public boolean hasNext() {
                        if ((++(count)) == 2) {
                            to.cancel();
                        }
                        return true;
                    }

                    @Override
                    public Integer next() {
                        return 1;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        }).subscribe(to);
        to.assertValue(1).assertNoErrors().assertNotComplete();
    }

    @Test
    public void fusionRejected() {
        TestObserver<Integer> to = ObserverFusion.newTest(ASYNC);
        fromIterable(Arrays.asList(1, 2, 3)).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1, 2, 3);
    }

    @Test
    public void fusionClear() {
        fromIterable(Arrays.asList(1, 2, 3)).subscribe(new Observer<Integer>() {
            @Override
            public void onSubscribe(Disposable d) {
                @SuppressWarnings("unchecked")
                QueueDisposable<Integer> qd = ((QueueDisposable<Integer>) (d));
                qd.requestFusion(QueueFuseable.ANY);
                try {
                    assertEquals(1, qd.poll().intValue());
                } catch ( ex) {
                    fail(ex.toString());
                }
                qd.clear();
                try {
                    assertNull(qd.poll());
                } catch ( ex) {
                    fail(ex.toString());
                }
            }

            @Override
            public void onNext(Integer value) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }
}

