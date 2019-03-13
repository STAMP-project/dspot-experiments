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
import io.reactivex.disposables.Disposables;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class ObservableMaterializeTest {
    @Test
    public void testMaterialize1() {
        // null will cause onError to be triggered before "three" can be
        // returned
        final ObservableMaterializeTest.TestAsyncErrorObservable o1 = new ObservableMaterializeTest.TestAsyncErrorObservable("one", "two", null, "three");
        ObservableMaterializeTest.TestLocalObserver observer = new ObservableMaterializeTest.TestLocalObserver();
        Observable<Notification<String>> m = unsafeCreate(o1).materialize();
        m.subscribe(observer);
        try {
            o1.t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertFalse(observer.onError);
        Assert.assertTrue(observer.onComplete);
        Assert.assertEquals(3, observer.notifications.size());
        Assert.assertTrue(observer.notifications.get(0).isOnNext());
        Assert.assertEquals("one", observer.notifications.get(0).getValue());
        Assert.assertTrue(observer.notifications.get(1).isOnNext());
        Assert.assertEquals("two", observer.notifications.get(1).getValue());
        Assert.assertTrue(observer.notifications.get(2).isOnError());
        Assert.assertEquals(NullPointerException.class, observer.notifications.get(2).getError().getClass());
    }

    @Test
    public void testMaterialize2() {
        final ObservableMaterializeTest.TestAsyncErrorObservable o1 = new ObservableMaterializeTest.TestAsyncErrorObservable("one", "two", "three");
        ObservableMaterializeTest.TestLocalObserver observer = new ObservableMaterializeTest.TestLocalObserver();
        Observable<Notification<String>> m = unsafeCreate(o1).materialize();
        m.subscribe(observer);
        try {
            o1.t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertFalse(observer.onError);
        Assert.assertTrue(observer.onComplete);
        Assert.assertEquals(4, observer.notifications.size());
        Assert.assertTrue(observer.notifications.get(0).isOnNext());
        Assert.assertEquals("one", observer.notifications.get(0).getValue());
        Assert.assertTrue(observer.notifications.get(1).isOnNext());
        Assert.assertEquals("two", observer.notifications.get(1).getValue());
        Assert.assertTrue(observer.notifications.get(2).isOnNext());
        Assert.assertEquals("three", observer.notifications.get(2).getValue());
        Assert.assertTrue(observer.notifications.get(3).isOnComplete());
    }

    @Test
    public void testMultipleSubscribes() throws InterruptedException, ExecutionException {
        final ObservableMaterializeTest.TestAsyncErrorObservable o = new ObservableMaterializeTest.TestAsyncErrorObservable("one", "two", null, "three");
        Observable<Notification<String>> m = unsafeCreate(o).materialize();
        Assert.assertEquals(3, m.toList().toFuture().get().size());
        Assert.assertEquals(3, m.toList().toFuture().get().size());
    }

    @Test
    public void testWithCompletionCausingError() {
        TestObserver<Notification<Integer>> to = new TestObserver<Notification<Integer>>();
        final RuntimeException ex = new RuntimeException("boo");
        <Integer>empty().materialize().doOnNext(new Consumer<Object>() {
            @Override
            public void accept(Object t) {
                throw ex;
            }
        }).subscribe(to);
        to.assertError(ex);
        to.assertNoValues();
        to.assertTerminated();
    }

    private static class TestLocalObserver extends DefaultObserver<Notification<String>> {
        boolean onComplete;

        boolean onError;

        List<Notification<String>> notifications = new Vector<Notification<String>>();

        @Override
        public void onComplete() {
            this.onComplete = true;
        }

        @Override
        public void onError(Throwable e) {
            this.onError = true;
        }

        @Override
        public void onNext(Notification<String> value) {
            this.notifications.add(value);
        }
    }

    private static class TestAsyncErrorObservable implements ObservableSource<String> {
        String[] valuesToReturn;

        TestAsyncErrorObservable(String... values) {
            valuesToReturn = values;
        }

        volatile Thread t;

        @Override
        public void subscribe(final Observer<? super String> observer) {
            observer.onSubscribe(Disposables.empty());
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (String s : valuesToReturn) {
                        if (s == null) {
                            System.out.println("throwing exception");
                            try {
                                Thread.sleep(100);
                            } catch (Throwable e) {
                            }
                            observer.onError(new NullPointerException());
                            return;
                        } else {
                            observer.onNext(s);
                        }
                    }
                    System.out.println("subscription complete");
                    observer.onComplete();
                }
            });
            t.start();
        }
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(just(1).materialize());
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeObservable(new Function<Observable<Object>, ObservableSource<Notification<Object>>>() {
            @Override
            public ObservableSource<Notification<Object>> apply(Observable<Object> o) throws Exception {
                return o.materialize();
            }
        });
    }
}

