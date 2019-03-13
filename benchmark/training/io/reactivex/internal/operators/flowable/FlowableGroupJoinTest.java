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
package io.reactivex.internal.operators.flowable;


import io.reactivex.Subscriber;
import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class FlowableGroupJoinTest {
    Subscriber<Object> subscriber = TestHelper.mockSubscriber();

    BiFunction<Integer, Integer, Integer> add = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer t1, Integer t2) {
            return t1 + t2;
        }
    };

    BiFunction<Integer, Flowable<Integer>, Flowable<Integer>> add2 = new BiFunction<Integer, Flowable<Integer>, Flowable<Integer>>() {
        @Override
        public io.reactivex.Flowable<Integer> apply(final Integer leftValue, Flowable<Integer> rightValues) {
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
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Flowable<Integer> m = Flowable.merge(source1.groupJoin(source2, just(Flowable.never()), just(Flowable.never()), add2));
        m.subscribe(subscriber);
        source1.onNext(1);
        source1.onNext(2);
        source1.onNext(4);
        source2.onNext(16);
        source2.onNext(32);
        source2.onNext(64);
        source1.onComplete();
        source2.onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext(17);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(18);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(20);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(33);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(34);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(36);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(65);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(66);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(68);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();// Never emitted?

        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
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
        final FlowableGroupJoinTest.Person person;

        final io.reactivex.Flowable<FlowableGroupJoinTest.PersonFruit> fruits;

        PPF(FlowableGroupJoinTest.Person person, Flowable<FlowableGroupJoinTest.PersonFruit> fruits) {
            this.person = person;
            this.fruits = fruits;
        }
    }

    @Test
    public void normal1() {
        Flowable<FlowableGroupJoinTest.Person> source1 = Flowable.fromIterable(Arrays.asList(new FlowableGroupJoinTest.Person(1, "Joe"), new FlowableGroupJoinTest.Person(2, "Mike"), new FlowableGroupJoinTest.Person(3, "Charlie")));
        Flowable<FlowableGroupJoinTest.PersonFruit> source2 = Flowable.fromIterable(Arrays.asList(new FlowableGroupJoinTest.PersonFruit(1, "Strawberry"), new FlowableGroupJoinTest.PersonFruit(1, "Apple"), new FlowableGroupJoinTest.PersonFruit(3, "Peach")));
        Flowable<FlowableGroupJoinTest.PPF> q = source1.groupJoin(source2, just2(Flowable.<Object>never()), just2(Flowable.<Object>never()), new BiFunction<FlowableGroupJoinTest.Person, Flowable<FlowableGroupJoinTest.PersonFruit>, FlowableGroupJoinTest.PPF>() {
            @Override
            public FlowableGroupJoinTest.PPF apply(FlowableGroupJoinTest.Person t1, Flowable<FlowableGroupJoinTest.PersonFruit> t2) {
                return new FlowableGroupJoinTest.PPF(t1, t2);
            }
        });
        q.subscribe(new FlowableSubscriber<FlowableGroupJoinTest.PPF>() {
            @Override
            public void onNext(final FlowableGroupJoinTest.PPF ppf) {
                ppf.fruits.filter(new Predicate<FlowableGroupJoinTest.PersonFruit>() {
                    @Override
                    public boolean test(FlowableGroupJoinTest.PersonFruit t1) {
                        return (ppf.person.id) == (t1.personId);
                    }
                }).subscribe(new Consumer<FlowableGroupJoinTest.PersonFruit>() {
                    @Override
                    public void accept(FlowableGroupJoinTest.PersonFruit t1) {
                        subscriber.onNext(Arrays.asList(ppf.person.name, t1.fruit));
                    }
                });
            }

            @Override
            public void onError(Throwable e) {
                subscriber.onError(e);
            }

            @Override
            public void onComplete() {
                subscriber.onComplete();
            }

            @Override
            public void onSubscribe(Subscription s) {
                s.request(Long.MAX_VALUE);
            }
        });
        Mockito.verify(subscriber, Mockito.times(1)).onNext(Arrays.asList("Joe", "Strawberry"));
        Mockito.verify(subscriber, Mockito.times(1)).onNext(Arrays.asList("Joe", "Apple"));
        Mockito.verify(subscriber, Mockito.times(1)).onNext(Arrays.asList("Charlie", "Peach"));
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void leftThrows() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Flowable<Flowable<Integer>> m = source1.groupJoin(source2, just(Flowable.never()), just(Flowable.never()), add2);
        m.subscribe(subscriber);
        source2.onNext(1);
        source1.onError(new RuntimeException("Forced failure"));
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void rightThrows() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Flowable<Flowable<Integer>> m = source1.groupJoin(source2, just(Flowable.never()), just(Flowable.never()), add2);
        m.subscribe(subscriber);
        source1.onNext(1);
        source2.onError(new RuntimeException("Forced failure"));
        Mockito.verify(subscriber, Mockito.times(1)).onNext(ArgumentMatchers.any(io.reactivex.Flowable.class));
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
    }

    @Test
    public void leftDurationThrows() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Flowable<Integer> duration1 = Flowable.<Integer>error(new RuntimeException("Forced failure"));
        Flowable<Flowable<Integer>> m = source1.groupJoin(source2, just(duration1), just(Flowable.never()), add2);
        m.subscribe(subscriber);
        source1.onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void rightDurationThrows() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Flowable<Integer> duration1 = Flowable.<Integer>error(new RuntimeException("Forced failure"));
        Flowable<Flowable<Integer>> m = source1.groupJoin(source2, just(Flowable.never()), just(duration1), add2);
        m.subscribe(subscriber);
        source2.onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void leftDurationSelectorThrows() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Function<Integer, Flowable<Integer>> fail = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                throw new RuntimeException("Forced failure");
            }
        };
        Flowable<Flowable<Integer>> m = source1.groupJoin(source2, fail, just(Flowable.never()), add2);
        m.subscribe(subscriber);
        source1.onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void rightDurationSelectorThrows() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Function<Integer, Flowable<Integer>> fail = new Function<Integer, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer t1) {
                throw new RuntimeException("Forced failure");
            }
        };
        Flowable<Flowable<Integer>> m = source1.groupJoin(source2, just(Flowable.never()), fail, add2);
        m.subscribe(subscriber);
        source2.onNext(1);
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void resultSelectorThrows() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        BiFunction<Integer, Flowable<Integer>, Integer> fail = new BiFunction<Integer, Flowable<Integer>, Integer>() {
            @Override
            public Integer apply(Integer t1, Flowable<Integer> t2) {
                throw new RuntimeException("Forced failure");
            }
        };
        Flowable<Integer> m = source1.groupJoin(source2, just(Flowable.never()), just(Flowable.never()), fail);
        m.subscribe(subscriber);
        source1.onNext(1);
        source2.onNext(2);
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).groupJoin(Flowable.just(2), new Function<Integer, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Integer left) throws Exception {
                return Flowable.never();
            }
        }, new Function<Integer, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Integer right) throws Exception {
                return Flowable.never();
            }
        }, new BiFunction<Integer, Flowable<Integer>, Object>() {
            @Override
            public Object apply(Integer r, Flowable<Integer> l) throws Exception {
                return l;
            }
        }));
    }

    @Test
    public void innerCompleteLeft() {
        Flowable.just(1).groupJoin(Flowable.just(2), new Function<Integer, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Integer left) throws Exception {
                return Flowable.empty();
            }
        }, new Function<Integer, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Integer right) throws Exception {
                return Flowable.never();
            }
        }, new BiFunction<Integer, Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer r, Flowable<Integer> l) throws Exception {
                return l;
            }
        }).flatMap(io.reactivex.internal.functions.Functions.<Flowable<Integer>>identity()).test().assertResult();
    }

    @Test
    public void innerErrorLeft() {
        Flowable.just(1).groupJoin(Flowable.just(2), new Function<Integer, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Integer left) throws Exception {
                return Flowable.error(new TestException());
            }
        }, new Function<Integer, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Integer right) throws Exception {
                return Flowable.never();
            }
        }, new BiFunction<Integer, Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer r, Flowable<Integer> l) throws Exception {
                return l;
            }
        }).flatMap(io.reactivex.internal.functions.Functions.<Flowable<Integer>>identity()).test().assertFailure(TestException.class);
    }

    @Test
    public void innerCompleteRight() {
        Flowable.just(1).groupJoin(Flowable.just(2), new Function<Integer, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Integer left) throws Exception {
                return Flowable.never();
            }
        }, new Function<Integer, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Integer right) throws Exception {
                return Flowable.empty();
            }
        }, new BiFunction<Integer, Flowable<Integer>, Flowable<Integer>>() {
            @Override
            public io.reactivex.Flowable<Integer> apply(Integer r, Flowable<Integer> l) throws Exception {
                return l;
            }
        }).flatMap(io.reactivex.internal.functions.Functions.<Flowable<Integer>>identity()).test().assertResult(2);
    }

    @Test
    public void innerErrorRight() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            Flowable.just(1).groupJoin(Flowable.just(2), new Function<Integer, Flowable<Object>>() {
                @Override
                public io.reactivex.Flowable<Object> apply(Integer left) throws Exception {
                    return Flowable.never();
                }
            }, new Function<Integer, Flowable<Object>>() {
                @Override
                public io.reactivex.Flowable<Object> apply(Integer right) throws Exception {
                    return Flowable.error(new TestException());
                }
            }, new BiFunction<Integer, Flowable<Integer>, Flowable<Integer>>() {
                @Override
                public io.reactivex.Flowable<Integer> apply(Integer r, Flowable<Integer> l) throws Exception {
                    return l;
                }
            }).flatMap(io.reactivex.internal.functions.Functions.<Flowable<Integer>>identity()).test().assertFailure(TestException.class);
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void innerErrorRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final PublishProcessor<Object> pp1 = PublishProcessor.create();
            final PublishProcessor<Object> pp2 = PublishProcessor.create();
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                TestSubscriber<Flowable<Integer>> ts = Flowable.just(1).groupJoin(Flowable.just(2).concatWith(Flowable.<Integer>never()), new Function<Integer, Flowable<Object>>() {
                    @Override
                    public io.reactivex.Flowable<Object> apply(Integer left) throws Exception {
                        return pp1;
                    }
                }, new Function<Integer, Flowable<Object>>() {
                    @Override
                    public io.reactivex.Flowable<Object> apply(Integer right) throws Exception {
                        return pp2;
                    }
                }, new BiFunction<Integer, Flowable<Integer>, Flowable<Integer>>() {
                    @Override
                    public io.reactivex.Flowable<Integer> apply(Integer r, Flowable<Integer> l) throws Exception {
                        return l;
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
                ts.assertError(Throwable.class).assertSubscribed().assertNotComplete().assertValueCount(1);
                Throwable exc = ts.errors().get(0);
                if (exc instanceof CompositeException) {
                    List<Throwable> es = TestHelper.compositeList(exc);
                    TestHelper.assertError(es, 0, TestException.class);
                    TestHelper.assertError(es, 1, TestException.class);
                } else {
                    ts.assertError(TestException.class);
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
            final PublishProcessor<Object> pp1 = PublishProcessor.create();
            final PublishProcessor<Object> pp2 = PublishProcessor.create();
            List<Throwable> errors = TestHelper.trackPluginErrors();
            try {
                TestSubscriber<Object> ts = pp1.groupJoin(pp2, new Function<Object, Flowable<Object>>() {
                    @Override
                    public io.reactivex.Flowable<Object> apply(Object left) throws Exception {
                        return Flowable.never();
                    }
                }, new Function<Object, Flowable<Object>>() {
                    @Override
                    public io.reactivex.Flowable<Object> apply(Object right) throws Exception {
                        return Flowable.never();
                    }
                }, new BiFunction<Object, Flowable<Object>, Flowable<Object>>() {
                    @Override
                    public io.reactivex.Flowable<Object> apply(Object r, Flowable<Object> l) throws Exception {
                        return l;
                    }
                }).flatMap(io.reactivex.internal.functions.Functions.<Flowable<Object>>identity()).test();
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
                ts.assertError(Throwable.class).assertSubscribed().assertNotComplete().assertNoValues();
                Throwable exc = ts.errors().get(0);
                if (exc instanceof CompositeException) {
                    List<Throwable> es = TestHelper.compositeList(exc);
                    TestHelper.assertError(es, 0, TestException.class);
                    TestHelper.assertError(es, 1, TestException.class);
                } else {
                    ts.assertError(TestException.class);
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
        final PublishProcessor<Object> pp1 = PublishProcessor.create();
        final PublishProcessor<Object> pp2 = PublishProcessor.create();
        TestSubscriber<Object> ts = pp1.groupJoin(pp2, new Function<Object, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Object left) throws Exception {
                return Flowable.never();
            }
        }, new Function<Object, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Object right) throws Exception {
                return Flowable.never();
            }
        }, new BiFunction<Object, Flowable<Object>, Flowable<Object>>() {
            @Override
            public io.reactivex.Flowable<Object> apply(Object r, Flowable<Object> l) throws Exception {
                return l;
            }
        }).flatMap(io.reactivex.internal.functions.Functions.<Flowable<Object>>identity()).test();
        pp2.onNext(2);
        pp1.onNext(1);
        pp1.onComplete();
        pp2.onComplete();
        ts.assertResult(2);
    }

    @Test
    public void leftRightState() {
        JoinSupport js = Mockito.mock(JoinSupport.class);
        LeftRightSubscriber o = new LeftRightSubscriber(js, false);
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
        LeftRightEndSubscriber o = new LeftRightEndSubscriber(js, false, 0);
        Assert.assertFalse(o.isDisposed());
        o.onNext(1);
        o.onNext(2);
        Assert.assertTrue(o.isDisposed());
        Mockito.verify(js).innerClose(false, o);
    }
}

