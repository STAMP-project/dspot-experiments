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
package io.reactivex.internal.subscribers;


import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class StrictSubscriberTest {
    @Test
    public void strictMode() {
        final List<Object> list = new ArrayList<Object>();
        Subscriber<Object> sub = new Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(10);
            }

            @Override
            public void onNext(Object t) {
                list.add(t);
            }

            @Override
            public void onError(Throwable t) {
                list.add(t);
            }

            @Override
            public void onComplete() {
                list.add("Done");
            }
        };
        new io.reactivex.Flowable<Object>() {
            @Override
            protected void subscribeActual(Subscriber<? super Object> s) {
                s.onSubscribe(new BooleanSubscription());
                s.onNext(s);
            }
        }.subscribe(sub);
        Assert.assertTrue(list.toString(), ((list.get(0)) instanceof StrictSubscriber));
    }

    static final class SubscriberWrapper<T> implements Subscriber<T> {
        final TestSubscriber<T> tester;

        SubscriberWrapper(TestSubscriber<T> tester) {
            this.tester = tester;
        }

        @Override
        public void onSubscribe(Subscription s) {
            tester.onSubscribe(s);
        }

        @Override
        public void onNext(T t) {
            tester.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            tester.onError(t);
        }

        @Override
        public void onComplete() {
            tester.onComplete();
        }
    }

    @Test
    public void normalOnNext() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        StrictSubscriberTest.SubscriberWrapper<Integer> wrapper = new StrictSubscriberTest.SubscriberWrapper<Integer>(ts);
        subscribe(wrapper);
        ts.assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void normalOnNextBackpressured() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>(0);
        StrictSubscriberTest.SubscriberWrapper<Integer> wrapper = new StrictSubscriberTest.SubscriberWrapper<Integer>(ts);
        subscribe(wrapper);
        ts.assertEmpty().requestMore(1).assertValue(1).requestMore(2).assertValues(1, 2, 3).requestMore(2).assertResult(1, 2, 3, 4, 5);
    }

    @Test
    public void normalOnError() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        StrictSubscriberTest.SubscriberWrapper<Integer> wrapper = new StrictSubscriberTest.SubscriberWrapper<Integer>(ts);
        subscribe(wrapper);
        ts.assertFailure(TestException.class, 1, 2, 3, 4, 5);
    }

    @Test
    public void deferredRequest() {
        final List<Object> list = new ArrayList<Object>();
        Subscriber<Object> sub = new Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(5);
                list.add(0);
            }

            @Override
            public void onNext(Object t) {
                list.add(t);
            }

            @Override
            public void onError(Throwable t) {
                list.add(t);
            }

            @Override
            public void onComplete() {
                list.add("Done");
            }
        };
        io.reactivex.Flowable.range(1, 5).subscribe(sub);
        Assert.assertEquals(Arrays.<Object>asList(0, 1, 2, 3, 4, 5, "Done"), list);
    }

    @Test
    public void requestZero() {
        final List<Object> list = new ArrayList<Object>();
        Subscriber<Object> sub = new Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request(0);
            }

            @Override
            public void onNext(Object t) {
                list.add(t);
            }

            @Override
            public void onError(Throwable t) {
                list.add(t);
            }

            @Override
            public void onComplete() {
                list.add("Done");
            }
        };
        io.reactivex.Flowable.range(1, 5).subscribe(sub);
        Assert.assertTrue(list.toString(), ((list.get(0)) instanceof IllegalArgumentException));
        Assert.assertTrue(list.toString(), list.get(0).toString().contains("3.9"));
    }

    @Test
    public void requestNegative() {
        final List<Object> list = new ArrayList<Object>();
        Subscriber<Object> sub = new Subscriber<Object>() {
            @Override
            public void onSubscribe(Subscription s) {
                s.request((-99));
            }

            @Override
            public void onNext(Object t) {
                list.add(t);
            }

            @Override
            public void onError(Throwable t) {
                list.add(t);
            }

            @Override
            public void onComplete() {
                list.add("Done");
            }
        };
        io.reactivex.Flowable.range(1, 5).subscribe(sub);
        Assert.assertTrue(list.toString(), ((list.get(0)) instanceof IllegalArgumentException));
        Assert.assertTrue(list.toString(), list.get(0).toString().contains("3.9"));
    }

    @Test
    public void cancelAfterOnComplete() {
        final List<Object> list = new ArrayList<Object>();
        Subscriber<Object> sub = new Subscriber<Object>() {
            Subscription upstream;

            @Override
            public void onSubscribe(Subscription s) {
                this.upstream = s;
            }

            @Override
            public void onNext(Object t) {
                list.add(t);
            }

            @Override
            public void onError(Throwable t) {
                upstream.cancel();
                list.add(t);
            }

            @Override
            public void onComplete() {
                upstream.cancel();
                list.add("Done");
            }
        };
        new io.reactivex.Flowable<Object>() {
            @Override
            protected void subscribeActual(Subscriber<? super Object> s) {
                BooleanSubscription b = new BooleanSubscription();
                s.onSubscribe(b);
                s.onComplete();
                list.add(b.isCancelled());
            }
        }.subscribe(sub);
        Assert.assertEquals(Arrays.<Object>asList("Done", false), list);
    }

    @Test
    public void cancelAfterOnError() {
        final List<Object> list = new ArrayList<Object>();
        Subscriber<Object> sub = new Subscriber<Object>() {
            Subscription upstream;

            @Override
            public void onSubscribe(Subscription s) {
                this.upstream = s;
            }

            @Override
            public void onNext(Object t) {
                list.add(t);
            }

            @Override
            public void onError(Throwable t) {
                upstream.cancel();
                list.add(t.getMessage());
            }

            @Override
            public void onComplete() {
                upstream.cancel();
                list.add("Done");
            }
        };
        new io.reactivex.Flowable<Object>() {
            @Override
            protected void subscribeActual(Subscriber<? super Object> s) {
                BooleanSubscription b = new BooleanSubscription();
                s.onSubscribe(b);
                s.onError(new TestException("Forced failure"));
                list.add(b.isCancelled());
            }
        }.subscribe(sub);
        Assert.assertEquals(Arrays.<Object>asList("Forced failure", false), list);
    }

    @Test
    public void doubleOnSubscribe() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        StrictSubscriberTest.SubscriberWrapper<Integer> wrapper = new StrictSubscriberTest.SubscriberWrapper<Integer>(ts);
        subscribe(wrapper);
        ts.assertFailure(IllegalStateException.class);
        Assert.assertTrue(ts.errors().toString(), ts.errors().get(0).getMessage().contains("2.12"));
    }
}

