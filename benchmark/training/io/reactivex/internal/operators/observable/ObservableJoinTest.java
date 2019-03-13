/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.reactivex.internal.operators.observable;


import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableJoinTest {
    Observer<Object> observer = mockObserver();

    BiFunction<Integer, Integer, Integer> add = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer t1, Integer t2) {
            return t1 + t2;
        }
    };

    @Test
    public void normal1() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Observable<Integer> m = source1.join(source2, just(Observable.never()), just(Observable.never()), add);
        m.subscribe(observer);
        source1.onNext(1);
        source1.onNext(2);
        source1.onNext(4);
        source2.onNext(16);
        source2.onNext(32);
        source2.onNext(64);
        source1.onComplete();
        source2.onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext(17);
        Mockito.verify(observer, Mockito.times(1)).onNext(18);
        Mockito.verify(observer, Mockito.times(1)).onNext(20);
        Mockito.verify(observer, Mockito.times(1)).onNext(33);
        Mockito.verify(observer, Mockito.times(1)).onNext(34);
        Mockito.verify(observer, Mockito.times(1)).onNext(36);
        Mockito.verify(observer, Mockito.times(1)).onNext(65);
        Mockito.verify(observer, Mockito.times(1)).onNext(66);
        Mockito.verify(observer, Mockito.times(1)).onNext(68);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void normal1WithDuration() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        PublishSubject<Integer> duration1 = PublishSubject.create();
        Observable<Integer> m = source1.join(source2, just(duration1), just(Observable.never()), add);
        m.subscribe(observer);
        source1.onNext(1);
        source1.onNext(2);
        source2.onNext(16);
        duration1.onNext(1);
        source1.onNext(4);
        source1.onNext(8);
        source1.onComplete();
        source2.onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext(17);
        Mockito.verify(observer, Mockito.times(1)).onNext(18);
        Mockito.verify(observer, Mockito.times(1)).onNext(20);
        Mockito.verify(observer, Mockito.times(1)).onNext(24);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void normal2() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Observable<Integer> m = source1.join(source2, just(Observable.never()), just(Observable.never()), add);
        m.subscribe(observer);
        source1.onNext(1);
        source1.onNext(2);
        source1.onComplete();
        source2.onNext(16);
        source2.onNext(32);
        source2.onNext(64);
        source2.onComplete();
        Mockito.verify(observer, Mockito.times(1)).onNext(17);
        Mockito.verify(observer, Mockito.times(1)).onNext(18);
        Mockito.verify(observer, Mockito.times(1)).onNext(33);
        Mockito.verify(observer, Mockito.times(1)).onNext(34);
        Mockito.verify(observer, Mockito.times(1)).onNext(65);
        Mockito.verify(observer, Mockito.times(1)).onNext(66);
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void leftThrows() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Observable<Integer> m = source1.join(source2, just(Observable.never()), just(Observable.never()), add);
        m.subscribe(observer);
        source2.onNext(1);
        source1.onError(new RuntimeException("Forced failure"));
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void rightThrows() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Observable<Integer> m = source1.join(source2, just(Observable.never()), just(Observable.never()), add);
        m.subscribe(observer);
        source1.onNext(1);
        source2.onError(new RuntimeException("Forced failure"));
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void leftDurationThrows() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Observable<Integer> duration1 = Observable.<Integer>error(new RuntimeException("Forced failure"));
        Observable<Integer> m = source1.join(source2, just(duration1), just(Observable.never()), add);
        m.subscribe(observer);
        source1.onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void rightDurationThrows() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Observable<Integer> duration1 = Observable.<Integer>error(new RuntimeException("Forced failure"));
        Observable<Integer> m = source1.join(source2, just(Observable.never()), just(duration1), add);
        m.subscribe(observer);
        source2.onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void leftDurationSelectorThrows() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Function<Integer, Observable<Integer>> fail = new Function<Integer, Observable<Integer>>() {
            @Override
            public io.reactivex.Observable<Integer> apply(Integer t1) {
                throw new RuntimeException("Forced failure");
            }
        };
        Observable<Integer> m = source1.join(source2, fail, just(Observable.never()), add);
        m.subscribe(observer);
        source1.onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void rightDurationSelectorThrows() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Function<Integer, Observable<Integer>> fail = new Function<Integer, Observable<Integer>>() {
            @Override
            public io.reactivex.Observable<Integer> apply(Integer t1) {
                throw new RuntimeException("Forced failure");
            }
        };
        Observable<Integer> m = source1.join(source2, just(Observable.never()), fail, add);
        m.subscribe(observer);
        source2.onNext(1);
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void resultSelectorThrows() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        BiFunction<Integer, Integer, Integer> fail = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                throw new RuntimeException("Forced failure");
            }
        };
        Observable<Integer> m = source1.join(source2, just(Observable.never()), just(Observable.never()), fail);
        m.subscribe(observer);
        source1.onNext(1);
        source2.onNext(2);
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishSubject.<Integer>create().join(Observable.just(1), Functions.justFunction(Observable.never()), Functions.justFunction(Observable.never()), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }));
    }

    @Test
    public void take() {
        Observable.just(1).join(Observable.just(2), Functions.justFunction(Observable.never()), Functions.justFunction(Observable.never()), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }).take(1).test().assertResult(3);
    }

    @Test
    public void rightClose() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.join(Observable.just(2), Functions.justFunction(Observable.never()), Functions.justFunction(Observable.empty()), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }).test().assertEmpty();
        ps.onNext(1);
        to.assertEmpty();
    }

    @Test
    public void resultSelectorThrows2() {
        PublishSubject<Integer> ps = PublishSubject.create();
        TestObserver<Integer> to = ps.join(Observable.just(2), Functions.justFunction(Observable.never()), Functions.justFunction(Observable.never()), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                throw new TestException();
            }
        }).test();
        ps.onNext(1);
        ps.onComplete();
        to.assertFailure(TestException.class);
    }

    @Test
    public void badOuterSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? super Integer> observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onError(new TestException("First"));
                    observer.onError(new TestException("Second"));
                }
            }.join(Observable.just(2), Functions.justFunction(Observable.never()), Functions.justFunction(Observable.never()), new BiFunction<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer a, Integer b) throws Exception {
                    return a + b;
                }
            }).test().assertFailureAndMessage(TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void badEndSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            @SuppressWarnings("rawtypes")
            final Observer[] o = new Observer[]{ null };
            TestObserver<Integer> to = Observable.just(1).join(Observable.just(2), Functions.justFunction(Observable.never()), Functions.justFunction(new Observable<Integer>() {
                @Override
                protected void subscribeActual(Observer<? super Integer> observer) {
                    o[0] = observer;
                    observer.onSubscribe(Disposables.empty());
                    observer.onError(new TestException("First"));
                }
            }), new BiFunction<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer a, Integer b) throws Exception {
                    return a + b;
                }
            }).test();
            o[0].onError(new TestException("Second"));
            to.assertFailureAndMessage(TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

