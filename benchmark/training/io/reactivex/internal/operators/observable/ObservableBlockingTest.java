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
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.functions.Functions;
import io.reactivex.internal.observers.BlockingFirstObserver;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class ObservableBlockingTest {
    @Test
    public void blockingFirst() {
        Assert.assertEquals(1, range(1, 10).subscribeOn(Schedulers.computation()).blockingFirst().intValue());
    }

    @Test
    public void blockingFirstDefault() {
        Assert.assertEquals(1, <Integer>empty().subscribeOn(Schedulers.computation()).blockingFirst(1).intValue());
    }

    @Test
    public void blockingSubscribeConsumer() {
        final List<Integer> list = new ArrayList<Integer>();
        range(1, 5).subscribeOn(Schedulers.computation()).blockingSubscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                list.add(v);
            }
        });
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void blockingSubscribeConsumerConsumer() {
        final List<Object> list = new ArrayList<Object>();
        range(1, 5).subscribeOn(Schedulers.computation()).blockingSubscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer v) throws Exception {
                list.add(v);
            }
        }, Functions.emptyConsumer());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), list);
    }

    @Test
    public void blockingSubscribeConsumerConsumerError() {
        final List<Object> list = new ArrayList<Object>();
        TestException ex = new TestException();
        Consumer<Object> cons = new Consumer<Object>() {
            @Override
            public void accept(Object v) throws Exception {
                list.add(v);
            }
        };
        range(1, 5).concatWith(<Integer>error(ex)).subscribeOn(Schedulers.computation()).blockingSubscribe(cons, cons);
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, ex), list);
    }

    @Test
    public void blockingSubscribeConsumerConsumerAction() {
        final List<Object> list = new ArrayList<Object>();
        Consumer<Object> cons = new Consumer<Object>() {
            @Override
            public void accept(Object v) throws Exception {
                list.add(v);
            }
        };
        range(1, 5).subscribeOn(Schedulers.computation()).blockingSubscribe(cons, cons, new Action() {
            @Override
            public void run() throws Exception {
                list.add(100);
            }
        });
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 100), list);
    }

    @Test
    public void blockingSubscribeObserver() {
        final List<Object> list = new ArrayList<Object>();
        range(1, 5).subscribeOn(Schedulers.computation()).blockingSubscribe(new java.util.Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Object value) {
                list.add(value);
            }

            @Override
            public void onError(Throwable e) {
                list.add(e);
            }

            @Override
            public void onComplete() {
                list.add(100);
            }
        });
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, 100), list);
    }

    @Test
    public void blockingSubscribeObserverError() {
        final List<Object> list = new ArrayList<Object>();
        final TestException ex = new TestException();
        range(1, 5).concatWith(<Integer>error(ex)).subscribeOn(Schedulers.computation()).blockingSubscribe(new java.util.Observer<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Object value) {
                list.add(value);
            }

            @Override
            public void onError(Throwable e) {
                list.add(e);
            }

            @Override
            public void onComplete() {
                list.add(100);
            }
        });
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5, ex), list);
    }

    @Test(expected = TestException.class)
    public void blockingForEachThrows() {
        just(1).blockingForEach(new Consumer<Integer>() {
            @Override
            public void accept(Integer e) throws Exception {
                throw new TestException();
            }
        });
    }

    @Test(expected = NoSuchElementException.class)
    public void blockingFirstEmpty() {
        empty().blockingFirst();
    }

    @Test(expected = NoSuchElementException.class)
    public void blockingLastEmpty() {
        empty().blockingLast();
    }

    @Test
    public void blockingFirstNormal() {
        Assert.assertEquals(1, java.util.Observable.just(1, 2).blockingFirst(3).intValue());
    }

    @Test
    public void blockingLastNormal() {
        Assert.assertEquals(2, java.util.Observable.just(1, 2).blockingLast(3).intValue());
    }

    @Test(expected = NoSuchElementException.class)
    public void blockingSingleEmpty() {
        empty().blockingSingle();
    }

    @Test
    public void utilityClass() {
        TestHelper.checkUtilityClass(ObservableBlockingSubscribe.class);
    }

    @Test
    public void disposeUpFront() {
        TestObserver<Object> to = new TestObserver<Object>();
        to.dispose();
        just(1).blockingSubscribe(to);
        to.assertEmpty();
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void delayed() throws Exception {
        final TestObserver<Object> to = new TestObserver<Object>();
        final java.util.Observer[] s = new java.util.Observer[]{ null };
        Schedulers.single().scheduleDirect(new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                to.dispose();
                onNext(1);
            }
        }, 200, TimeUnit.MILLISECONDS);
        new java.util.Observable<Integer>() {
            @Override
            protected void subscribeActual(Observer<? extends Integer> observer) {
                observer.onSubscribe(Disposables.empty());
                s[0] = observer;
            }
        }.blockingSubscribe(to);
        while (!(to.isDisposed())) {
            Thread.sleep(100);
        } 
        to.assertEmpty();
    }

    @Test
    public void interrupt() {
        TestObserver<Object> to = new TestObserver<Object>();
        Thread.currentThread().interrupt();
        never().blockingSubscribe(to);
    }

    @Test
    public void onCompleteDelayed() {
        TestObserver<Object> to = new TestObserver<Object>();
        empty().delay(100, TimeUnit.MILLISECONDS).blockingSubscribe(to);
        to.assertResult();
    }

    @Test
    public void blockingCancelUpfront() {
        BlockingFirstObserver<Integer> o = new BlockingFirstObserver<Integer>();
        Assert.assertFalse(o.isDisposed());
        o.dispose();
        Assert.assertTrue(o.isDisposed());
        Disposable d = Disposables.empty();
        o.onSubscribe(d);
        Assert.assertTrue(d.isDisposed());
        Thread.currentThread().interrupt();
        try {
            o.blockingGet();
            Assert.fail("Should have thrown");
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof InterruptedException));
        }
        Thread.interrupted();
        o.onError(new TestException());
        try {
            o.blockingGet();
            Assert.fail("Should have thrown");
        } catch (TestException ex) {
            // expected
        }
    }
}

