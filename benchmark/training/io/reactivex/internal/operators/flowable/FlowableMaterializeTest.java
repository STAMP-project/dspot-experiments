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
package io.reactivex.internal.operators.flowable;


import io.reactivex.TestHelper;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.schedulers.Schedulers;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;


public class FlowableMaterializeTest {
    @Test
    public void testMaterialize1() {
        // null will cause onError to be triggered before "three" can be
        // returned
        final FlowableMaterializeTest.TestAsyncErrorObservable o1 = new FlowableMaterializeTest.TestAsyncErrorObservable("one", "two", null, "three");
        FlowableMaterializeTest.TestNotificationSubscriber observer = new FlowableMaterializeTest.TestNotificationSubscriber();
        Flowable<Notification<String>> m = Flowable.unsafeCreate(o1).materialize();
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
        final FlowableMaterializeTest.TestAsyncErrorObservable o1 = new FlowableMaterializeTest.TestAsyncErrorObservable("one", "two", "three");
        FlowableMaterializeTest.TestNotificationSubscriber subscriber = new FlowableMaterializeTest.TestNotificationSubscriber();
        Flowable<Notification<String>> m = Flowable.unsafeCreate(o1).materialize();
        m.subscribe(subscriber);
        try {
            o1.t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assert.assertFalse(subscriber.onError);
        Assert.assertTrue(subscriber.onComplete);
        Assert.assertEquals(4, subscriber.notifications.size());
        Assert.assertTrue(subscriber.notifications.get(0).isOnNext());
        Assert.assertEquals("one", subscriber.notifications.get(0).getValue());
        Assert.assertTrue(subscriber.notifications.get(1).isOnNext());
        Assert.assertEquals("two", subscriber.notifications.get(1).getValue());
        Assert.assertTrue(subscriber.notifications.get(2).isOnNext());
        Assert.assertEquals("three", subscriber.notifications.get(2).getValue());
        Assert.assertTrue(subscriber.notifications.get(3).isOnComplete());
    }

    @Test
    public void testMultipleSubscribes() throws InterruptedException, ExecutionException {
        final FlowableMaterializeTest.TestAsyncErrorObservable o = new FlowableMaterializeTest.TestAsyncErrorObservable("one", "two", null, "three");
        Flowable<Notification<String>> m = Flowable.unsafeCreate(o).materialize();
        Assert.assertEquals(3, m.toList().toFuture().get().size());
        Assert.assertEquals(3, m.toList().toFuture().get().size());
    }

    @Test
    public void testBackpressureOnEmptyStream() {
        TestSubscriber<Notification<Integer>> ts = new TestSubscriber<Notification<Integer>>(0L);
        Flowable.<Integer>empty().materialize().subscribe(ts);
        ts.assertNoValues();
        ts.request(1);
        ts.assertValueCount(1);
        Assert.assertTrue(ts.values().get(0).isOnComplete());
        ts.assertComplete();
    }

    @Test
    public void testBackpressureNoError() {
        TestSubscriber<Notification<Integer>> ts = new TestSubscriber<Notification<Integer>>(0L);
        Flowable.just(1, 2, 3).materialize().subscribe(ts);
        ts.assertNoValues();
        ts.request(1);
        ts.assertValueCount(1);
        ts.request(2);
        ts.assertValueCount(3);
        ts.request(1);
        ts.assertValueCount(4);
        ts.assertComplete();
    }

    @Test
    public void testBackpressureNoErrorAsync() throws InterruptedException {
        TestSubscriber<Notification<Integer>> ts = new TestSubscriber<Notification<Integer>>(0L);
        Flowable.just(1, 2, 3).materialize().subscribeOn(Schedulers.computation()).subscribe(ts);
        Thread.sleep(100);
        ts.assertNoValues();
        ts.request(1);
        Thread.sleep(100);
        ts.assertValueCount(1);
        ts.request(2);
        Thread.sleep(100);
        ts.assertValueCount(3);
        ts.request(1);
        Thread.sleep(100);
        ts.assertValueCount(4);
        ts.assertComplete();
    }

    @Test
    public void testBackpressureWithError() {
        TestSubscriber<Notification<Integer>> ts = new TestSubscriber<Notification<Integer>>(0L);
        Flowable.<Integer>error(new IllegalArgumentException()).materialize().subscribe(ts);
        ts.assertNoValues();
        ts.request(1);
        ts.assertValueCount(1);
        ts.assertComplete();
    }

    @Test
    public void testBackpressureWithEmissionThenError() {
        TestSubscriber<Notification<Integer>> ts = new TestSubscriber<Notification<Integer>>(0L);
        IllegalArgumentException ex = new IllegalArgumentException();
        Flowable.fromIterable(Arrays.asList(1)).concatWith(Flowable.<Integer>error(ex)).materialize().subscribe(ts);
        ts.assertNoValues();
        ts.request(1);
        ts.assertValueCount(1);
        Assert.assertTrue(ts.values().get(0).isOnNext());
        ts.request(1);
        ts.assertValueCount(2);
        Assert.assertTrue(ts.values().get(1).isOnError());
        Assert.assertEquals(ex, ts.values().get(1).getError());
        ts.assertComplete();
    }

    @Test
    public void testWithCompletionCausingError() {
        TestSubscriber<Notification<Integer>> ts = new TestSubscriber<Notification<Integer>>();
        final RuntimeException ex = new RuntimeException("boo");
        Flowable.<Integer>empty().materialize().doOnNext(new Consumer<Object>() {
            @Override
            public void accept(Object t) {
                throw ex;
            }
        }).subscribe(ts);
        ts.assertError(ex);
        ts.assertNoValues();
        ts.assertTerminated();
    }

    @Test
    public void testUnsubscribeJustBeforeCompletionNotificationShouldPreventThatNotificationArriving() {
        TestSubscriber<Notification<Integer>> ts = new TestSubscriber<Notification<Integer>>(0L);
        Flowable.<Integer>empty().materialize().subscribe(ts);
        ts.assertNoValues();
        ts.dispose();
        ts.request(1);
        ts.assertNoValues();
    }

    private static class TestNotificationSubscriber extends DefaultSubscriber<Notification<String>> {
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

    private static class TestAsyncErrorObservable implements Publisher<String> {
        String[] valuesToReturn;

        TestAsyncErrorObservable(String... values) {
            valuesToReturn = values;
        }

        volatile Thread t;

        @Override
        public void subscribe(final Subscriber<? super String> subscriber) {
            subscriber.onSubscribe(new BooleanSubscription());
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
                            subscriber.onError(new NullPointerException());
                            return;
                        } else {
                            subscriber.onNext(s);
                        }
                    }
                    System.out.println("subscription complete");
                    subscriber.onComplete();
                }
            });
            t.start();
        }
    }

    @Test
    public void backpressure() {
        TestSubscriber<Notification<Integer>> ts = Flowable.range(1, 5).materialize().test(0);
        ts.assertEmpty();
        ts.request(5);
        ts.assertValueCount(5).assertNoErrors().assertNotComplete();
        ts.request(1);
        ts.assertValueCount(6).assertNoErrors().assertComplete();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(Flowable.just(1).materialize());
    }

    @Test
    public void doubleOnSubscribe() {
        TestHelper.checkDoubleOnSubscribeFlowable(new Function<Flowable<Object>, Flowable<Notification<Object>>>() {
            @Override
            public io.reactivex.Flowable<Notification<Object>> apply(Flowable<Object> f) throws Exception {
                return f.materialize();
            }
        });
    }

    @Test
    public void badSource() {
        TestHelper.checkBadSourceFlowable(new Function<Flowable<Object>, Object>() {
            @Override
            public Object apply(Flowable<Object> f) throws Exception {
                return f.materialize();
            }
        }, false, null, null, Notification.createOnComplete());
    }

    @Test
    public void badRequest() {
        TestHelper.assertBadRequestReported(Flowable.just(1).materialize());
    }
}

