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


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.reactivestreams.Subscriber;


public class FlowableJoinTest {
    Subscriber<Object> subscriber = TestHelper.mockSubscriber();

    BiFunction<Integer, Integer, Integer> add = new BiFunction<Integer, Integer, Integer>() {
        @Override
        public Integer apply(Integer t1, Integer t2) {
            return t1 + t2;
        }
    };

    @Test
    public void normal1() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Flowable<Integer> m = source1.join(source2, just(Flowable.never()), just(Flowable.never()), add);
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
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void normal1WithDuration() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        PublishProcessor<Integer> duration1 = PublishProcessor.create();
        Flowable<Integer> m = source1.join(source2, just(duration1), just(Flowable.never()), add);
        m.subscribe(subscriber);
        source1.onNext(1);
        source1.onNext(2);
        source2.onNext(16);
        duration1.onNext(1);
        source1.onNext(4);
        source1.onNext(8);
        source1.onComplete();
        source2.onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext(17);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(18);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(20);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(24);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void normal2() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Flowable<Integer> m = source1.join(source2, just(Flowable.never()), just(Flowable.never()), add);
        m.subscribe(subscriber);
        source1.onNext(1);
        source1.onNext(2);
        source1.onComplete();
        source2.onNext(16);
        source2.onNext(32);
        source2.onNext(64);
        source2.onComplete();
        Mockito.verify(subscriber, Mockito.times(1)).onNext(17);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(18);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(33);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(34);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(65);
        Mockito.verify(subscriber, Mockito.times(1)).onNext(66);
        Mockito.verify(subscriber, Mockito.times(1)).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    @Test
    public void leftThrows() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Flowable<Integer> m = source1.join(source2, just(Flowable.never()), just(Flowable.never()), add);
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
        Flowable<Integer> m = source1.join(source2, just(Flowable.never()), just(Flowable.never()), add);
        m.subscribe(subscriber);
        source1.onNext(1);
        source2.onError(new RuntimeException("Forced failure"));
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void leftDurationThrows() {
        PublishProcessor<Integer> source1 = PublishProcessor.create();
        PublishProcessor<Integer> source2 = PublishProcessor.create();
        Flowable<Integer> duration1 = Flowable.<Integer>error(new RuntimeException("Forced failure"));
        Flowable<Integer> m = source1.join(source2, just(duration1), just(Flowable.never()), add);
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
        Flowable<Integer> m = source1.join(source2, just(Flowable.never()), just(duration1), add);
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
        Flowable<Integer> m = source1.join(source2, fail, just(Flowable.never()), add);
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
        Flowable<Integer> m = source1.join(source2, just(Flowable.never()), fail, add);
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
        BiFunction<Integer, Integer, Integer> fail = new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer t1, Integer t2) {
                throw new RuntimeException("Forced failure");
            }
        };
        Flowable<Integer> m = source1.join(source2, just(Flowable.never()), just(Flowable.never()), fail);
        m.subscribe(subscriber);
        source1.onNext(1);
        source2.onNext(2);
        Mockito.verify(subscriber, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(subscriber, Mockito.never()).onComplete();
        Mockito.verify(subscriber, Mockito.never()).onNext(ArgumentMatchers.any());
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(PublishProcessor.<Integer>create().join(Flowable.just(1), Functions.justFunction(Flowable.never()), Functions.justFunction(Flowable.never()), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }));
    }

    @Test
    public void take() {
        Flowable.just(1).join(Flowable.just(2), Functions.justFunction(Flowable.never()), Functions.justFunction(Flowable.never()), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }).take(1).test().assertResult(3);
    }

    @Test
    public void rightClose() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = pp.join(Flowable.just(2), Functions.justFunction(Flowable.never()), Functions.justFunction(Flowable.empty()), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }).test().assertEmpty();
        pp.onNext(1);
        ts.assertEmpty();
    }

    @Test
    public void resultSelectorThrows2() {
        PublishProcessor<Integer> pp = PublishProcessor.create();
        TestSubscriber<Integer> ts = pp.join(Flowable.just(2), Functions.justFunction(Flowable.never()), Functions.justFunction(Flowable.never()), new BiFunction<Integer, Integer, Integer>() {
            @Override
            public Integer apply(Integer a, Integer b) throws Exception {
                throw new TestException();
            }
        }).test();
        pp.onNext(1);
        pp.onComplete();
        ts.assertFailure(TestException.class);
    }

    @Test
    public void badOuterSource() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                    subscriber.onSubscribe(new BooleanSubscription());
                    subscriber.onError(new TestException("First"));
                    subscriber.onError(new TestException("Second"));
                }
            }.join(Flowable.just(2), Functions.justFunction(Flowable.never()), Functions.justFunction(Flowable.never()), new BiFunction<Integer, Integer, Integer>() {
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
            final Subscriber[] o = new Subscriber[]{ null };
            TestSubscriber<Integer> ts = Flowable.just(1).join(Flowable.just(2), Functions.justFunction(Flowable.never()), Functions.justFunction(new Flowable<Integer>() {
                @Override
                protected void subscribeActual(Subscriber<? super Integer> subscriber) {
                    o[0] = subscriber;
                    subscriber.onSubscribe(new BooleanSubscription());
                    subscriber.onError(new TestException("First"));
                }
            }), new BiFunction<Integer, Integer, Integer>() {
                @Override
                public Integer apply(Integer a, Integer b) throws Exception {
                    return a + b;
                }
            }).test();
            o[0].onError(new TestException("Second"));
            ts.assertFailureAndMessage(TestException.class, "First");
            TestHelper.assertUndeliverable(errors, 0, TestException.class, "Second");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void backpressureOverflowRight() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        TestSubscriber<Object> ts = pp1.join(pp2, Functions.justFunction(Flowable.never()), Functions.justFunction(Flowable.never()), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }).test(0L);
        pp1.onNext(1);
        pp2.onNext(2);
        ts.assertFailure(MissingBackpressureException.class);
    }

    @Test
    public void backpressureOverflowLeft() {
        PublishProcessor<Integer> pp1 = PublishProcessor.create();
        PublishProcessor<Integer> pp2 = PublishProcessor.create();
        TestSubscriber<Object> ts = pp1.join(pp2, Functions.justFunction(Flowable.never()), Functions.justFunction(Flowable.never()), new BiFunction<Integer, Integer, Object>() {
            @Override
            public Object apply(Integer a, Integer b) throws Exception {
                return a + b;
            }
        }).test(0L);
        pp2.onNext(2);
        pp1.onNext(1);
        ts.assertFailure(MissingBackpressureException.class);
    }
}

