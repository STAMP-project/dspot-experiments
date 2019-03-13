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
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ObservableGroupJoinTest {
    Observer<Object> observer = mockObserver();

    BiFunction<Integer, Integer, Integer> add = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer t1, Integer t2) {
            return t1 + t2;
        }
    };

    BiFunction<Integer, Observable<Integer>, Observable<Integer>> add2 = new BiFunction<Integer, Observable<Integer>, Observable<Integer>>() {
        @Override
        public Observable<Integer> apply(final Integer leftValue, Observable<Integer> rightValues) {
            return rightValues.map(new Function<Integer, Integer>() {
                @Override
                public Integer apply(Integer rightValue) throws Exception {
                    return add.apply(leftValue, rightValue);
                }
            });
        }
    };

    @Test
    public void behaveAsJoin() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Observable<Integer> m = Observable.merge(source1.groupJoin(source2, just(never()), just(never()), add2));
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
        Mockito.verify(observer, Mockito.times(1)).onComplete();// Never emitted?

        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    class Person {
        final int id;

        final String name;

        Person(int id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    class PersonFruit {
        final int personId;

        final String fruit;

        PersonFruit(int personId, String fruit) {
            this.personId = personId;
            this.fruit = fruit;
        }
    }

    class PPF {
        final ObservableGroupJoinTest.Person person;

        final Observable<ObservableGroupJoinTest.PersonFruit> fruits;

        PPF(ObservableGroupJoinTest.Person person, Observable<ObservableGroupJoinTest.PersonFruit> fruits) {
            this.person = person;
            this.fruits = fruits;
        }
    }

    @Test
    public void normal1() {
        Observable<ObservableGroupJoinTest.Person> source1 = fromIterable(Arrays.asList(new ObservableGroupJoinTest.Person(1, "Joe"), new ObservableGroupJoinTest.Person(2, "Mike"), new ObservableGroupJoinTest.Person(3, "Charlie")));
        Observable<ObservableGroupJoinTest.PersonFruit> source2 = fromIterable(Arrays.asList(new ObservableGroupJoinTest.PersonFruit(1, "Strawberry"), new ObservableGroupJoinTest.PersonFruit(1, "Apple"), new ObservableGroupJoinTest.PersonFruit(3, "Peach")));
        Observable<ObservableGroupJoinTest.PPF> q = source1.groupJoin(source2, just2(<Object>never()), just2(<Object>never()), new BiFunction<ObservableGroupJoinTest.Person, Observable<ObservableGroupJoinTest.PersonFruit>, ObservableGroupJoinTest.PPF>() {
            @Override
            public io.reactivex.internal.operators.observable.PPF apply(io.reactivex.internal.operators.observable.Person t1, Observable<io.reactivex.internal.operators.observable.PersonFruit> t2) {
                return new io.reactivex.internal.operators.observable.PPF(t1, t2);
            }
        });
        q.subscribe(new Observer<ObservableGroupJoinTest.PPF>() {
            @Override
            public void onNext(final io.reactivex.internal.operators.observable.PPF ppf) {
                ppf.fruits.filter(new Predicate<io.reactivex.internal.operators.observable.PersonFruit>() {
                    @Override
                    public boolean test(io.reactivex.internal.operators.observable.PersonFruit t1) {
                        return ppf.person.id == t1.personId;
                    }
                }).subscribe(new Consumer<io.reactivex.internal.operators.observable.PersonFruit>() {
                    @Override
                    public void accept(io.reactivex.internal.operators.observable.PersonFruit t1) {
                        observer.onNext(Arrays.asList(ppf.person.name, t1.fruit));
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                observer.onError(e);
            }

            @Override
            public void onComplete() {
                observer.onComplete();
            }

            @Override
            public void onSubscribe(Disposable d) {
            }
        });
        Mockito.verify(observer, Mockito.times(1)).onNext(Arrays.asList("Joe", "Strawberry"));
        Mockito.verify(observer, Mockito.times(1)).onNext(Arrays.asList("Joe", "Apple"));
        Mockito.verify(observer, Mockito.times(1)).onNext(Arrays.asList("Charlie", "Peach"));
        Mockito.verify(observer, Mockito.times(1)).onComplete();
        Mockito.verify(observer, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void leftThrows() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Observable<Observable<Integer>> m = source1.groupJoin(source2, just(never()), just(never()), add2);
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
        Observable<Observable<Integer>> m = source1.groupJoin(source2, just(never()), just(never()), add2);
        m.subscribe(observer);
        source1.onNext(1);
        source2.onError(new RuntimeException("Forced failure"));
        Mockito.verify(observer, Mockito.times(1)).onNext(ArgumentMatchers.any(Observable.class));
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
    }

    @Test
    public void leftDurationThrows() {
        PublishSubject<Integer> source1 = PublishSubject.create();
        PublishSubject<Integer> source2 = PublishSubject.create();
        Observable<Integer> duration1 = Observable.<Integer>error(new RuntimeException("Forced failure"));
        Observable<Observable<Integer>> m = source1.groupJoin(source2, just(duration1), just(never()), add2);
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
        Observable<Observable<Integer>> m = source1.groupJoin(source2, just(never()), just(duration1), add2);
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
            public Observable<Integer> apply(Integer t1) {
                throw new RuntimeException("Forced failure");
            }
        };
        Observable<Observable<Integer>> m = source1.groupJoin(source2, fail, just(never()), add2);
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
            public Observable<Integer> apply(Integer t1) {
                throw new RuntimeException("Forced failure");
            }
        };
        Observable<Observable<Integer>> m = source1.groupJoin(source2, just(never()), fail, add2);
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
        BiFunction<Integer, Observable<Integer>, Integer> fail = new BiFunction<Integer, Observable<Integer>, Integer>() {
            @Override
            public Integer apply(Integer t1, Observable<Integer> t2) {
                throw new RuntimeException("Forced failure");
            }
        };
        Observable<Integer> m = source1.groupJoin(source2, just(never()), just(never()), fail);
        m.subscribe(observer);
        source1.onNext(1);
        source2.onNext(2);
        Mockito.verify(observer, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(observer, Mockito.never()).onComplete();
        Mockito.verify(observer, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(just(1).groupJoin(just(2), new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer left) throws Exception {
                return Observable.never();
            }
        }, new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer right) throws Exception {
                return Observable.never();
            }
        }, new BiFunction<Integer, Observable<Integer>, Object>() {
            @Override
            public Object apply(Integer r, Observable<Integer> l) throws Exception {
                return l;
            }
        }));
    }

    @Test
    public void innerCompleteLeft() {
        just(1).groupJoin(just(2), new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer left) throws Exception {
                return empty();
            }
        }, new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer right) throws Exception {
                return Observable.never();
            }
        }, new BiFunction<Integer, Observable<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer r, Observable<Integer> l) throws Exception {
                return l;
            }
        }).flatMap(io.reactivex.internal.functions.Functions.<Observable<Integer>>identity()).test().assertResult();
    }

    @Test
    public void innerErrorLeft() {
        just(1).groupJoin(just(2), new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer left) throws Exception {
                return error(new TestException());
            }
        }, new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer right) throws Exception {
                return Observable.never();
            }
        }, new BiFunction<Integer, Observable<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer r, Observable<Integer> l) throws Exception {
                return l;
            }
        }).flatMap(io.reactivex.internal.functions.Functions.<Observable<Integer>>identity()).test().assertFailure(TestException.class);
    }

    @Test
    public void innerCompleteRight() {
        just(1).groupJoin(just(2), new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer left) throws Exception {
                return Observable.never();
            }
        }, new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer right) throws Exception {
                return empty();
            }
        }, new BiFunction<Integer, Observable<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer r, Observable<Integer> l) throws Exception {
                return l;
            }
        }).flatMap(io.reactivex.internal.functions.Functions.<Observable<Integer>>identity()).test().assertResult(2);
    }

    @Test
    public void innerErrorRight() {
        just(1).groupJoin(just(2), new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer left) throws Exception {
                return Observable.never();
            }
        }, new Function<Integer, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Integer right) throws Exception {
                return error(new TestException());
            }
        }, new BiFunction<Integer, Observable<Integer>, Observable<Integer>>() {
            @Override
            public Observable<Integer> apply(Integer r, Observable<Integer> l) throws Exception {
                return l;
            }
        }).flatMap(io.reactivex.internal.functions.Functions.<Observable<Integer>>identity()).test().assertFailure(TestException.class);
    }

    @Test
    public void innerErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Object> ps1 = PublishSubject.create();
            final PublishSubject<Object> ps2 = PublishSubject.create();
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                TestObserver<Observable<Integer>> to = just(1).groupJoin(just(2).concatWith(<Integer>never()), new Function<Integer, ObservableSource<Object>>() {
                    @Override
                    public io.reactivex.ObservableSource<Object> apply(Integer left) throws Exception {
                        return ps1;
                    }
                }, new Function<Integer, ObservableSource<Object>>() {
                    @Override
                    public io.reactivex.ObservableSource<Object> apply(Integer right) throws Exception {
                        return ps2;
                    }
                }, new BiFunction<Integer, Observable<Integer>, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> apply(Integer r, Observable<Integer> l) throws Exception {
                        return l;
                    }
                }).test();
                final TestException ex1 = new TestException();
                final TestException ex2 = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ps1.onError(ex1);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps2.onError(ex2);
                    }
                };
                TestHelper.race(r1, r2);
                to.assertError(Throwable.class).assertSubscribed().assertNotComplete().assertValueCount(1);
                Throwable exc = to.errors().get(0);
                if (exc instanceof CompositeException) {
                    List<Throwable> es = TestHelper.compositeList(exc);
                    TestHelper.assertError(es, 0, TestException.class);
                    TestHelper.assertError(es, 1, TestException.class);
                } else {
                    to.assertError(TestException.class);
                }
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void outerErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishSubject<Object> ps1 = PublishSubject.create();
            final PublishSubject<Object> ps2 = PublishSubject.create();
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                TestObserver<Object> to = ps1.groupJoin(ps2, new Function<Object, ObservableSource<Object>>() {
                    @Override
                    public io.reactivex.ObservableSource<Object> apply(Object left) throws Exception {
                        return Observable.never();
                    }
                }, new Function<Object, ObservableSource<Object>>() {
                    @Override
                    public io.reactivex.ObservableSource<Object> apply(Object right) throws Exception {
                        return Observable.never();
                    }
                }, new BiFunction<Object, Observable<Object>, Observable<Object>>() {
                    @Override
                    public Observable<Object> apply(Object r, Observable<Object> l) throws Exception {
                        return l;
                    }
                }).flatMap(io.reactivex.internal.functions.Functions.<Observable<Object>>identity()).test();
                final TestException ex1 = new TestException();
                final TestException ex2 = new TestException();
                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        ps1.onError(ex1);
                    }
                };
                Runnable r2 = new Runnable() {
                    @Override
                    public void run() {
                        ps2.onError(ex2);
                    }
                };
                TestHelper.race(r1, r2);
                to.assertError(Throwable.class).assertSubscribed().assertNotComplete().assertNoValues();
                Throwable exc = to.errors().get(0);
                if (exc instanceof CompositeException) {
                    List<Throwable> es = TestHelper.compositeList(exc);
                    TestHelper.assertError(es, 0, TestException.class);
                    TestHelper.assertError(es, 1, TestException.class);
                } else {
                    to.assertError(TestException.class);
                }
                if (!(errors.isEmpty())) {
                    TestHelper.assertUndeliverable(errors, 0, TestException.class);
                }
            } finally {
                RxJavaPlugins.reset();
            }
        }
    }

    @Test
    public void rightEmission() {
        final PublishSubject<Object> ps1 = PublishSubject.create();
        final PublishSubject<Object> ps2 = PublishSubject.create();
        TestObserver<Object> to = ps1.groupJoin(ps2, new Function<Object, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Object left) throws Exception {
                return Observable.never();
            }
        }, new Function<Object, ObservableSource<Object>>() {
            @Override
            public io.reactivex.ObservableSource<Object> apply(Object right) throws Exception {
                return Observable.never();
            }
        }, new BiFunction<Object, Observable<Object>, Observable<Object>>() {
            @Override
            public Observable<Object> apply(Object r, Observable<Object> l) throws Exception {
                return l;
            }
        }).flatMap(io.reactivex.internal.functions.Functions.<Observable<Object>>identity()).test();
        ps2.onNext(2);
        ps1.onNext(1);
        ps1.onComplete();
        ps2.onComplete();
        to.assertResult(2);
    }

    @Test
    public void leftRightState() {
        JoinSupport js = Mockito.mock(JoinSupport.class);
        LeftRightObserver o = new LeftRightObserver(js, false);
        Assert.assertFalse(o.isDisposed());
        o.onNext(1);
        o.onNext(2);
        o.dispose();
        Assert.assertTrue(o.isDisposed());
        Mockito.verify(js).innerValue(false, 1);
        Mockito.verify(js).innerValue(false, 2);
    }

    @Test
    public void leftRightEndState() {
        JoinSupport js = Mockito.mock(JoinSupport.class);
        LeftRightEndObserver o = new LeftRightEndObserver(js, false, 0);
        Assert.assertFalse(o.isDisposed());
        o.onNext(1);
        o.onNext(2);
        Assert.assertTrue(o.isDisposed());
        Mockito.verify(js).innerClose(false, o);
    }
}

