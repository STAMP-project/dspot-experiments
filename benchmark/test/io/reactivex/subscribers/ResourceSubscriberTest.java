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
package io.reactivex.subscribers;


import io.reactivex.TestHelper;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.internal.util.EndConsumerHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ResourceSubscriberTest {
    static class TestResourceSubscriber<T> extends ResourceSubscriber<T> {
        final List<T> values = new ArrayList<T>();

        final List<Throwable> errors = new ArrayList<Throwable>();

        int complete;

        int start;

        @Override
        protected void onStart() {
            super.onStart();
            (start)++;
        }

        @Override
        public void onNext(T value) {
            values.add(value);
        }

        @Override
        public void onError(Throwable e) {
            errors.add(e);
            dispose();
        }

        @Override
        public void onComplete() {
            (complete)++;
            dispose();
        }

        void requestMore(long n) {
            request(n);
        }
    }

    @Test(expected = NullPointerException.class)
    public void nullResource() {
        ResourceSubscriberTest.TestResourceSubscriber<Integer> ro = new ResourceSubscriberTest.TestResourceSubscriber<Integer>();
        ro.add(null);
    }

    @Test
    public void addResources() {
        ResourceSubscriberTest.TestResourceSubscriber<Integer> ro = new ResourceSubscriberTest.TestResourceSubscriber<Integer>();
        Assert.assertFalse(ro.isDisposed());
        Disposable d = Disposables.empty();
        ro.add(d);
        Assert.assertFalse(d.isDisposed());
        ro.dispose();
        Assert.assertTrue(ro.isDisposed());
        Assert.assertTrue(d.isDisposed());
        ro.dispose();
        Assert.assertTrue(ro.isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void onCompleteCleansUp() {
        ResourceSubscriberTest.TestResourceSubscriber<Integer> ro = new ResourceSubscriberTest.TestResourceSubscriber<Integer>();
        Assert.assertFalse(ro.isDisposed());
        Disposable d = Disposables.empty();
        ro.add(d);
        Assert.assertFalse(d.isDisposed());
        ro.onComplete();
        Assert.assertTrue(ro.isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void onErrorCleansUp() {
        ResourceSubscriberTest.TestResourceSubscriber<Integer> ro = new ResourceSubscriberTest.TestResourceSubscriber<Integer>();
        Assert.assertFalse(ro.isDisposed());
        Disposable d = Disposables.empty();
        ro.add(d);
        Assert.assertFalse(d.isDisposed());
        ro.onError(new TestException());
        Assert.assertTrue(ro.isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void normal() {
        ResourceSubscriberTest.TestResourceSubscriber<Integer> tc = new ResourceSubscriberTest.TestResourceSubscriber<Integer>();
        Assert.assertFalse(tc.isDisposed());
        Assert.assertEquals(0, tc.start);
        Assert.assertTrue(tc.values.isEmpty());
        Assert.assertTrue(tc.errors.isEmpty());
        Flowable.just(1).subscribe(tc);
        Assert.assertTrue(tc.isDisposed());
        Assert.assertEquals(1, tc.start);
        Assert.assertEquals(1, tc.values.get(0).intValue());
        Assert.assertTrue(tc.errors.isEmpty());
    }

    @Test
    public void startOnce() {
        List<Throwable> error = TestHelper.trackPluginErrors();
        try {
            ResourceSubscriberTest.TestResourceSubscriber<Integer> tc = new ResourceSubscriberTest.TestResourceSubscriber<Integer>();
            tc.onSubscribe(new BooleanSubscription());
            BooleanSubscription bs = new BooleanSubscription();
            tc.onSubscribe(bs);
            Assert.assertTrue(bs.isCancelled());
            Assert.assertEquals(1, tc.start);
            TestHelper.assertError(error, 0, IllegalStateException.class, EndConsumerHelper.composeMessage(tc.getClass().getName()));
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void dispose() {
        ResourceSubscriberTest.TestResourceSubscriber<Integer> tc = new ResourceSubscriberTest.TestResourceSubscriber<Integer>();
        tc.dispose();
        BooleanSubscription bs = new BooleanSubscription();
        tc.onSubscribe(bs);
        Assert.assertTrue(bs.isCancelled());
        Assert.assertEquals(0, tc.start);
    }

    @Test
    public void request() {
        ResourceSubscriberTest.TestResourceSubscriber<Integer> tc = new ResourceSubscriberTest.TestResourceSubscriber<Integer>() {
            @Override
            protected void onStart() {
                (start)++;
            }
        };
        Flowable.just(1).subscribe(tc);
        Assert.assertEquals(1, tc.start);
        Assert.assertEquals(Collections.emptyList(), tc.values);
        Assert.assertTrue(tc.errors.isEmpty());
        Assert.assertEquals(0, tc.complete);
        tc.requestMore(1);
        Assert.assertEquals(1, tc.start);
        Assert.assertEquals(1, tc.values.get(0).intValue());
        Assert.assertTrue(tc.errors.isEmpty());
        Assert.assertEquals(1, tc.complete);
    }

    static final class RequestEarly extends ResourceSubscriber<Integer> {
        final List<Object> events = new ArrayList<Object>();

        RequestEarly() {
            request(5);
        }

        @Override
        protected void onStart() {
        }

        @Override
        public void onNext(Integer t) {
            events.add(t);
        }

        @Override
        public void onError(Throwable t) {
            events.add(t);
        }

        @Override
        public void onComplete() {
            events.add("Done");
        }
    }

    @Test
    public void requestUpfront() {
        ResourceSubscriberTest.RequestEarly sub = new ResourceSubscriberTest.RequestEarly();
        Flowable.range(1, 10).subscribe(sub);
        Assert.assertEquals(Arrays.<Object>asList(1, 2, 3, 4, 5), sub.events);
    }
}

