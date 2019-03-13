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
package io.reactivex.flowable;


import io.reactivex.Publisher;
import io.reactivex.internal.util.ExceptionHelper;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class FlowableConversionTest {
    public static class Cylon {}

    public static class Jail {
        Object cylon;

        Jail(Object cylon) {
            this.cylon = cylon;
        }
    }

    public static class CylonDetectorObservable<T> {
        protected Publisher<T> onSubscribe;

        public static <T> FlowableConversionTest.CylonDetectorObservable<T> create(Publisher<T> onSubscribe) {
            return new FlowableConversionTest.CylonDetectorObservable<T>(onSubscribe);
        }

        protected CylonDetectorObservable(Publisher<T> onSubscribe) {
            this.onSubscribe = onSubscribe;
        }

        public void subscribe(Subscriber<T> subscriber) {
            onSubscribe.subscribe(subscriber);
        }

        public <R> FlowableConversionTest.CylonDetectorObservable<R> lift(FlowableOperator<? extends R, ? super T> operator) {
            return x(new FlowableConversionTest.RobotConversionFunc<T, R>(operator));
        }

        public <R, O> O x(Function<Publisher<T>, O> operator) {
            try {
                return operator.apply(onSubscribe);
            } catch (Throwable ex) {
                throw ExceptionHelper.wrapOrThrow(ex);
            }
        }

        public <R> FlowableConversionTest.CylonDetectorObservable<? extends R> compose(Function<FlowableConversionTest.CylonDetectorObservable<? super T>, FlowableConversionTest.CylonDetectorObservable<? extends R>> transformer) {
            try {
                return transformer.apply(this);
            } catch (Throwable ex) {
                throw ExceptionHelper.wrapOrThrow(ex);
            }
        }

        public final FlowableConversionTest.CylonDetectorObservable<T> beep(Predicate<? super T> predicate) {
            return new FlowableConversionTest.CylonDetectorObservable<T>(new FlowableFilter<T>(Flowable.fromPublisher(onSubscribe), predicate));
        }

        public final <R> FlowableConversionTest.CylonDetectorObservable<R> boop(Function<? super T, ? extends R> func) {
            return new FlowableConversionTest.CylonDetectorObservable<R>(new FlowableMap<T, R>(Flowable.fromPublisher(onSubscribe), func));
        }

        public FlowableConversionTest.CylonDetectorObservable<String> DESTROY() {
            return boop(new Function<T, String>() {
                @Override
                public String apply(T t) {
                    Object cylon = ((FlowableConversionTest.Jail) (t)).cylon;
                    FlowableConversionTest.CylonDetectorObservable.throwOutTheAirlock(cylon);
                    if (t instanceof FlowableConversionTest.Jail) {
                        String name = cylon.toString();
                        return ("Cylon '" + name) + "' has been destroyed";
                    } else {
                        return "Cylon 'anonymous' has been destroyed";
                    }
                }
            });
        }

        private static void throwOutTheAirlock(Object cylon) {
            // ...
        }
    }

    public static class RobotConversionFunc<T, R> implements Function<Publisher<T>, FlowableConversionTest.CylonDetectorObservable<R>> {
        private FlowableOperator<? extends R, ? super T> operator;

        public RobotConversionFunc(FlowableOperator<? extends R, ? super T> operator) {
            this.operator = operator;
        }

        @Override
        public FlowableConversionTest.CylonDetectorObservable<R> apply(final Publisher<T> onSubscribe) {
            return FlowableConversionTest.CylonDetectorObservable.create(new Publisher<R>() {
                @Override
                public void subscribe(Subscriber<? super R> subscriber) {
                    try {
                        Subscriber<? super T> st = operator.apply(subscriber);
                        try {
                            onSubscribe.subscribe(st);
                        } catch (Throwable e) {
                            st.onError(e);
                        }
                    } catch (Throwable e) {
                        subscriber.onError(e);
                    }
                }
            });
        }
    }

    public static class ConvertToCylonDetector<T> implements Function<Publisher<T>, FlowableConversionTest.CylonDetectorObservable<T>> {
        @Override
        public FlowableConversionTest.CylonDetectorObservable<T> apply(final Publisher<T> onSubscribe) {
            return FlowableConversionTest.CylonDetectorObservable.create(onSubscribe);
        }
    }

    public static class ConvertToObservable<T> implements Function<Publisher<T>, Flowable<T>> {
        @Override
        public io.reactivex.Flowable<T> apply(final Publisher<T> onSubscribe) {
            return Flowable.fromPublisher(onSubscribe);
        }
    }

    @Test
    public void testConversionBetweenObservableClasses() {
        final TestObserver<String> to = new TestObserver<String>(new DefaultObserver<String>() {
            @Override
            public void onComplete() {
                System.out.println("Complete");
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(("error: " + (e.getMessage())));
                e.printStackTrace();
            }

            @Override
            public void onNext(String t) {
                System.out.println(t);
            }
        });
        List<Object> crewOfBattlestarGalactica = Arrays.asList(new Object[]{ "William Adama", "Laura Roslin", "Lee Adama", new FlowableConversionTest.Cylon() });
        Flowable.fromIterable(crewOfBattlestarGalactica).doOnNext(new Consumer<Object>() {
            @Override
            public void accept(Object pv) {
                System.out.println(pv);
            }
        }).to(new FlowableConversionTest.ConvertToCylonDetector<Object>()).beep(new Predicate<Object>() {
            @Override
            public boolean test(Object t) {
                return t instanceof FlowableConversionTest.Cylon;
            }
        }).boop(new Function<Object, Object>() {
            @Override
            public Object apply(Object cylon) {
                return new FlowableConversionTest.Jail(cylon);
            }
        }).DESTROY().x(new FlowableConversionTest.ConvertToObservable<String>()).reduce("Cylon Detector finished. Report:\n", new BiFunction<String, String, String>() {
            @Override
            public String apply(String a, String n) {
                return (a + n) + "\n";
            }
        }).subscribe(to);
        to.assertNoErrors();
        to.assertComplete();
    }

    @Test
    public void testConvertToConcurrentQueue() {
        final AtomicReference<Throwable> thrown = new AtomicReference<Throwable>(null);
        final AtomicBoolean isFinished = new AtomicBoolean(false);
        ConcurrentLinkedQueue<? extends Integer> queue = Flowable.range(0, 5).flatMap(new Function<Integer, Publisher<Integer>>() {
            @Override
            public Publisher<Integer> apply(final Integer i) {
                return Flowable.range(0, 5).observeOn(Schedulers.io()).map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(Integer k) {
                        try {
                            Thread.sleep(((System.currentTimeMillis()) % 100));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return i + k;
                    }
                });
            }
        }).to(new Function<Flowable<Integer>, ConcurrentLinkedQueue<Integer>>() {
            @Override
            public ConcurrentLinkedQueue<Integer> apply(Flowable<Integer> onSubscribe) {
                final ConcurrentLinkedQueue<Integer> q = new ConcurrentLinkedQueue<Integer>();
                onSubscribe.subscribe(new io.reactivex.subscribers.DefaultSubscriber<Integer>() {
                    @Override
                    public void onComplete() {
                        isFinished.set(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        thrown.set(e);
                    }

                    @Override
                    public void onNext(Integer t) {
                        q.add(t);
                    }
                });
                return q;
            }
        });
        int x = 0;
        while (!(isFinished.get())) {
            Integer i = queue.poll();
            if (i != null) {
                x++;
                System.out.println(((x + " item: ") + i));
            }
        } 
        Assert.assertNull(thrown.get());
    }
}

