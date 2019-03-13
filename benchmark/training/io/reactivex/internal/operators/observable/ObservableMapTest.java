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


import QueueFuseable.ANY;
import QueueFuseable.ASYNC;
import QueueFuseable.NONE;
import QueueFuseable.SYNC;
import io.reactivex.TestHelper;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.ObserverFusion;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.UnicastSubject;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static QueueFuseable.ANY;
import static QueueFuseable.BOUNDARY;


public class ObservableMapTest {
    Observer<String> stringObserver;

    Observer<String> stringObserver2;

    static final BiFunction<String, Integer, String> APPEND_INDEX = new BiFunction<String, Integer, String>() {
        @Override
        public String apply(String value, Integer index) {
            return value + index;
        }
    };

    @Test
    public void testMap() {
        Map<String, String> m1 = ObservableMapTest.getMap("One");
        Map<String, String> m2 = ObservableMapTest.getMap("Two");
        Observable<Map<String, String>> o = Observable.just(m1, m2);
        Observable<String> m = o.map(new Function<Map<String, String>, String>() {
            @Override
            public String apply(Map<String, String> map) {
                return map.get("firstName");
            }
        });
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("OneFirst");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("TwoFirst");
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testMapMany() {
        /* simulate a top-level async call which returns IDs */
        Observable<Integer> ids = Observable.just(1, 2);
        /* now simulate the behavior to take those IDs and perform nested async calls based on them */
        Observable<String> m = ids.flatMap(new Function<Integer, Observable<String>>() {
            @Override
            public Observable<String> apply(Integer id) {
                /* simulate making a nested async call which creates another Observable */
                Observable<Map<String, String>> subObservable = null;
                if (id == 1) {
                    Map<String, String> m1 = getMap("One");
                    Map<String, String> m2 = getMap("Two");
                    subObservable = io.reactivex.Observable.just(m1, m2);
                } else {
                    Map<String, String> m3 = getMap("Three");
                    Map<String, String> m4 = getMap("Four");
                    subObservable = io.reactivex.Observable.just(m3, m4);
                }
                /* simulate kicking off the async call and performing a select on it to transform the data */
                return subObservable.map(new Function<Map<String, String>, String>() {
                    @Override
                    public String apply(Map<String, String> map) {
                        return map.get("firstName");
                    }
                });
            }
        });
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("OneFirst");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("TwoFirst");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("ThreeFirst");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("FourFirst");
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testMapMany2() {
        Map<String, String> m1 = ObservableMapTest.getMap("One");
        Map<String, String> m2 = ObservableMapTest.getMap("Two");
        Observable<Map<String, String>> observable1 = Observable.just(m1, m2);
        Map<String, String> m3 = ObservableMapTest.getMap("Three");
        Map<String, String> m4 = ObservableMapTest.getMap("Four");
        Observable<Map<String, String>> observable2 = Observable.just(m3, m4);
        Observable<Observable<Map<String, String>>> o = Observable.just(observable1, observable2);
        Observable<String> m = o.flatMap(new Function<Observable<Map<String, String>>, Observable<String>>() {
            @Override
            public Observable<String> apply(Observable<Map<String, String>> o) {
                return o.map(new Function<Map<String, String>, String>() {
                    @Override
                    public String apply(Map<String, String> map) {
                        return map.get("firstName");
                    }
                });
            }
        });
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("OneFirst");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("TwoFirst");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("ThreeFirst");
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("FourFirst");
        Mockito.verify(stringObserver, Mockito.times(1)).onComplete();
    }

    @Test
    public void testMapWithError() {
        Observable<String> w = Observable.just("one", "fail", "two", "three", "fail");
        Observable<String> m = w.map(new Function<String, String>() {
            @Override
            public String apply(String s) {
                if ("fail".equals(s)) {
                    throw new RuntimeException("Forced Failure");
                }
                return s;
            }
        }).doOnError(new Consumer<Throwable>() {
            @Override
            public void accept(Throwable t1) {
                t1.printStackTrace();
            }
        });
        m.subscribe(stringObserver);
        Mockito.verify(stringObserver, Mockito.times(1)).onNext("one");
        Mockito.verify(stringObserver, Mockito.never()).onNext("two");
        Mockito.verify(stringObserver, Mockito.never()).onNext("three");
        Mockito.verify(stringObserver, Mockito.never()).onComplete();
        Mockito.verify(stringObserver, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMapWithIssue417() {
        Observable.just(1).observeOn(Schedulers.computation()).map(new Function<Integer, Integer>() {
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
        Observable<String> m = just("one").observeOn(Schedulers.computation()).map(new Function<String, String>() {
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
        Assert.assertNull(range(1, 0).lastElement().map(new Function<Integer, Integer>() {
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
        error(new IllegalStateException()).map(new Function<Object, Object>() {
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
        range(1, 1).lastElement().map(new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer i) {
                return i / 0;
            }
        }).blockingGet();
    }

    // FIXME RS subscribers can't throw
    // @Test(expected = OnErrorNotImplementedException.class)
    // public void testShouldNotSwallowOnErrorNotImplementedException() {
    // Observable.just("a", "b").flatMap(new Function<String, Observable<String>>() {
    // @Override
    // public Observable<String> apply(String s) {
    // return Observable.just(s + "1", s + "2");
    // }
    // }).flatMap(new Function<String, Observable<String>>() {
    // @Override
    // public Observable<String> apply(String s) {
    // return Observable.error(new Exception("test"));
    // }
    // }).forEach(new Consumer<String>() {
    // @Override
    // public void accept(String s) {
    // System.out.println(s);
    // }
    // });
    // }
    @Test
    public void dispose() {
        TestHelper.checkDisposed(range(1, 5).map(Functions.identity()));
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Object>>() {
            @Override
            public ObservableSource<Object> apply(Observable<Object> o) throws Exception {
                return o.map(Functions.identity());
            }
        });
    }

    @Test
    public void fusedSync() {
        TestObserver<Integer> to = ObserverFusion.newTest(ANY);
        range(1, 5).map(Functions.<Integer>identity()).subscribe(to);
        ObserverFusion.assertFusion(to, SYNC).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void fusedAsync() {
        TestObserver<Integer> to = ObserverFusion.newTest(ANY);
        UnicastSubject<Integer> us = UnicastSubject.create();
        us.map(Functions.<Integer>identity()).subscribe(to);
        TestHelper.emit(us, 1, 2, 3, 4, 5);
        ObserverFusion.assertFusion(to, ASYNC).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void fusedReject() {
        TestObserver<Integer> to = ObserverFusion.newTest(((ANY) | (BOUNDARY)));
        range(1, 5).map(Functions.<Integer>identity()).subscribe(to);
        ObserverFusion.assertFusion(to, NONE).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void badSource() {
        TestHelper.checkBadSourceObservable(new Function<Observable<Object>, Object>() {
            @Override
            public Object apply(Observable<Object> o) throws Exception {
                return o.map(Functions.identity());
            }
        }, false, 1, 1, 1);
    }
}

