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
package io.reactivex.observable;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.PublishSubject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class ObservableSubscriberTest {
    @Test
    public void testOnStartCalledOnceViaSubscribe() {
        final AtomicInteger c = new AtomicInteger();
        Observable.just(1, 2, 3, 4).take(2).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onStart() {
                c.incrementAndGet();
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer t) {
            }
        });
        Assert.assertEquals(1, c.get());
    }

    @Test
    public void testOnStartCalledOnceViaUnsafeSubscribe() {
        final AtomicInteger c = new AtomicInteger();
        Observable.just(1, 2, 3, 4).take(2).subscribe(new DefaultObserver<Integer>() {
            @Override
            public void onStart() {
                c.incrementAndGet();
            }

            @Override
            public void onComplete() {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onNext(Integer t) {
            }
        });
        Assert.assertEquals(1, c.get());
    }

    @Test
    public void testOnStartCalledOnceViaLift() {
        final AtomicInteger c = new AtomicInteger();
        Observable.just(1, 2, 3, 4).lift(new ObservableOperator<Integer, Integer>() {
            @Override
            public Observer<? super Integer> apply(final Observer<? super Integer> child) {
                return new DefaultObserver<Integer>() {
                    @Override
                    public void onStart() {
                        c.incrementAndGet();
                    }

                    @Override
                    public void onComplete() {
                        child.onComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        child.onError(e);
                    }

                    @Override
                    public void onNext(Integer t) {
                        child.onNext(t);
                    }
                };
            }
        }).subscribe();
        Assert.assertEquals(1, c.get());
    }

    @Test
    public void subscribeConsumerConsumer() {
        final List<Integer> list = new ArrayList<Integer>();
        just(1).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                list.add(v);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                list.add(100);
            }
        });
        Assert.assertEquals(Arrays.asList(1), list);
    }

    @Test
    public void subscribeConsumerConsumerWithError() {
        final List<Integer> list = new ArrayList<Integer>();
        <Integer>error(new TestException()).subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                list.add(v);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable e) throws Exception {
                list.add(100);
            }
        });
        Assert.assertEquals(Arrays.asList(100), list);
    }

    @Test
    public void methodTestCancelled() {
        PublishSubject<Integer> ps = PublishSubject.create();
        ps.test(true);
        Assert.assertFalse(ps.hasObservers());
    }

    @Test
    public void safeSubscriberAlreadySafe() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        just(1).safeSubscribe(new SafeObserver<Integer>(to));
        to.assertResult(1);
    }

    @Test
    public void methodTestNoCancel() {
        PublishSubject<Integer> ps = PublishSubject.create();
        ps.test(false);
        Assert.assertTrue(ps.hasObservers());
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void pluginNull() {
        RxJavaPlugins.setOnObservableSubscribe(new BiFunction<Observable, Observer, Observer>() {
            @Override
            public Observer apply(Observable a, Observer b) throws Exception {
                return null;
            }
        });
        try {
            try {
                test();
                Assert.fail("Should have thrown");
            } catch (NullPointerException ex) {
                Assert.assertEquals("The RxJavaPlugins.onSubscribe hook returned a null Observer. Please change the handler provided to RxJavaPlugins.setOnObservableSubscribe for invalid null returns. Further reading: https://github.com/ReactiveX/RxJava/wiki/Plugins", ex.getMessage());
            }
        } finally {
            RxJavaPlugins.reset();
        }
    }

    static final class BadObservable extends Observable<Integer> {
        @Override
        protected void subscribeActual(Observer<? super Integer> observer) {
            throw new IllegalArgumentException();
        }
    }

    @Test
    public void subscribeActualThrows() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            try {
                test();
                Assert.fail("Should have thrown!");
            } catch (NullPointerException ex) {
                if (!((ex.getCause()) instanceof IllegalArgumentException)) {
                    Assert.fail(((ex.toString()) + ": Should be NPE(IAE)"));
                }
            }
            TestHelper.assertError(list, 0, IllegalArgumentException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

