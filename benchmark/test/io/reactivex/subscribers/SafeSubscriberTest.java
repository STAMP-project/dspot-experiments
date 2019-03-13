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
import io.reactivex.exceptions.CompositeException;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class SafeSubscriberTest {
    /**
     * Ensure onNext can not be called after onError.
     */
    @Test
    public void testOnNextAfterOnError() {
        SafeSubscriberTest.TestObservable t = new SafeSubscriberTest.TestObservable();
        Flowable<String> st = Flowable.unsafeCreate(t);
        Subscriber<String> w = TestHelper.mockSubscriber();
        st.subscribe(new SafeSubscriber<String>(new TestSubscriber<String>(w)));
        t.sendOnNext("one");
        t.sendOnError(new RuntimeException("bad"));
        t.sendOnNext("two");
        Mockito.verify(w, Mockito.times(1)).onNext("one");
        Mockito.verify(w, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.never()).onNext("two");
    }

    /**
     * Ensure onComplete can not be called after onError.
     */
    @Test
    public void testOnCompletedAfterOnError() {
        SafeSubscriberTest.TestObservable t = new SafeSubscriberTest.TestObservable();
        Flowable<String> st = Flowable.unsafeCreate(t);
        Subscriber<String> w = TestHelper.mockSubscriber();
        st.subscribe(new SafeSubscriber<String>(new TestSubscriber<String>(w)));
        t.sendOnNext("one");
        t.sendOnError(new RuntimeException("bad"));
        t.sendOnCompleted();
        Mockito.verify(w, Mockito.times(1)).onNext("one");
        Mockito.verify(w, Mockito.times(1)).onError(ArgumentMatchers.any(Throwable.class));
        Mockito.verify(w, Mockito.never()).onComplete();
    }

    /**
     * Ensure onNext can not be called after onComplete.
     */
    @Test
    public void testOnNextAfterOnCompleted() {
        SafeSubscriberTest.TestObservable t = new SafeSubscriberTest.TestObservable();
        Flowable<String> st = Flowable.unsafeCreate(t);
        Subscriber<String> w = TestHelper.mockSubscriber();
        st.subscribe(new SafeSubscriber<String>(new TestSubscriber<String>(w)));
        t.sendOnNext("one");
        t.sendOnCompleted();
        t.sendOnNext("two");
        Mockito.verify(w, Mockito.times(1)).onNext("one");
        Mockito.verify(w, Mockito.never()).onNext("two");
        Mockito.verify(w, Mockito.times(1)).onComplete();
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    /**
     * Ensure onError can not be called after onComplete.
     */
    @Test
    public void testOnErrorAfterOnCompleted() {
        SafeSubscriberTest.TestObservable t = new SafeSubscriberTest.TestObservable();
        Flowable<String> st = Flowable.unsafeCreate(t);
        Subscriber<String> w = TestHelper.mockSubscriber();
        st.subscribe(new SafeSubscriber<String>(new TestSubscriber<String>(w)));
        t.sendOnNext("one");
        t.sendOnCompleted();
        t.sendOnError(new RuntimeException("bad"));
        Mockito.verify(w, Mockito.times(1)).onNext("one");
        Mockito.verify(w, Mockito.times(1)).onComplete();
        Mockito.verify(w, Mockito.never()).onError(ArgumentMatchers.any(Throwable.class));
    }

    /**
     * An Observable that doesn't do the right thing on UnSubscribe/Error/etc in that it will keep sending events down the pipe regardless of what happens.
     */
    private static class TestObservable implements Publisher<String> {
        io.reactivex.Subscriber<? super String> subscriber;

        /* used to simulate subscription */
        public void sendOnCompleted() {
            subscriber.onComplete();
        }

        /* used to simulate subscription */
        public void sendOnNext(String value) {
            subscriber.onNext(value);
        }

        /* used to simulate subscription */
        public void sendOnError(Throwable e) {
            subscriber.onError(e);
        }

        @Override
        public void subscribe(Subscriber<? super String> subscriber) {
            this.subscriber = subscriber;
            subscriber.onSubscribe(new Subscription() {
                @Override
                public void cancel() {
                    // going to do nothing to pretend I'm a bad Observable that keeps allowing events to be sent
                    System.out.println("==> SynchronizeTest unsubscribe that does nothing!");
                }

                @Override
                public void request(long n) {
                }
            });
        }
    }

    @Test
    public void onNextFailure() {
        AtomicReference<Throwable> onError = new AtomicReference<Throwable>();
        try {
            SafeSubscriberTest.OBSERVER_ONNEXT_FAIL(onError).onNext("one");
            Assert.fail("expects exception to be thrown");
        } catch (Exception e) {
            Assert.assertNull(onError.get());
            Assert.assertTrue((e instanceof SafeSubscriberTest.SafeSubscriberTestException));
            Assert.assertEquals("onNextFail", e.getMessage());
        }
    }

    @Test
    public void onNextFailureSafe() {
        AtomicReference<Throwable> onError = new AtomicReference<Throwable>();
        try {
            SafeSubscriber<String> safeObserver = new SafeSubscriber<String>(SafeSubscriberTest.OBSERVER_ONNEXT_FAIL(onError));
            safeObserver.onSubscribe(new BooleanSubscription());
            safeObserver.onNext("one");
            Assert.assertNotNull(onError.get());
            Assert.assertTrue(((onError.get()) instanceof SafeSubscriberTest.SafeSubscriberTestException));
            Assert.assertEquals("onNextFail", onError.get().getMessage());
        } catch (Exception e) {
            Assert.fail("expects exception to be passed to onError");
        }
    }

    @Test
    public void onCompleteFailure() {
        AtomicReference<Throwable> onError = new AtomicReference<Throwable>();
        try {
            SafeSubscriberTest.OBSERVER_ONCOMPLETED_FAIL(onError).onComplete();
            Assert.fail("expects exception to be thrown");
        } catch (Exception e) {
            Assert.assertNull(onError.get());
            Assert.assertTrue((e instanceof SafeSubscriberTest.SafeSubscriberTestException));
            Assert.assertEquals("onCompleteFail", e.getMessage());
        }
    }

    @Test
    public void onErrorFailure() {
        try {
            SafeSubscriberTest.subscriberOnErrorFail().onError(new SafeSubscriberTest.SafeSubscriberTestException("error!"));
            Assert.fail("expects exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof SafeSubscriberTest.SafeSubscriberTestException));
            Assert.assertEquals("onErrorFail", e.getMessage());
        }
    }

    @Test
    public void onNextOnErrorFailure() {
        try {
            SafeSubscriberTest.OBSERVER_ONNEXT_ONERROR_FAIL().onNext("one");
            Assert.fail("expects exception to be thrown");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue((e instanceof SafeSubscriberTest.SafeSubscriberTestException));
            Assert.assertEquals("onNextFail", e.getMessage());
        }
    }

    static final io.reactivex.exceptions.Subscription THROWING_DISPOSABLE = new Subscription() {
        @Override
        public void cancel() {
            // break contract by throwing exception
            throw new SafeSubscriberTest.SafeSubscriberTestException("failure from unsubscribe");
        }

        @Override
        public void request(long n) {
            // ignored
        }
    };

    @SuppressWarnings("serial")
    private static class SafeSubscriberTestException extends RuntimeException {
        SafeSubscriberTestException(String message) {
            super(message);
        }
    }

    @Test
    public void testActual() {
        Subscriber<Integer> actual = new DefaultSubscriber<Integer>() {
            @Override
            public void onNext(Integer t) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        };
        SafeSubscriber<Integer> s = new SafeSubscriber<Integer>(actual);
        Assert.assertSame(actual, s.downstream);
    }

    @Test
    public void dispose() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        SafeSubscriber<Integer> so = new SafeSubscriber<Integer>(ts);
        BooleanSubscription bs = new BooleanSubscription();
        so.onSubscribe(bs);
        ts.dispose();
        Assert.assertTrue(bs.isCancelled());
        // assertTrue(so.isDisposed());
    }

    @Test
    public void onNextAfterComplete() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        SafeSubscriber<Integer> so = new SafeSubscriber<Integer>(ts);
        BooleanSubscription bs = new BooleanSubscription();
        so.onSubscribe(bs);
        so.onComplete();
        so.onNext(1);
        so.onError(new TestException());
        so.onComplete();
        ts.assertResult();
    }

    @Test
    public void onNextNull() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        SafeSubscriber<Integer> so = new SafeSubscriber<Integer>(ts);
        BooleanSubscription bs = new BooleanSubscription();
        so.onSubscribe(bs);
        so.onNext(null);
        ts.assertFailure(NullPointerException.class);
    }

    @Test
    public void onNextWithoutOnSubscribe() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        SafeSubscriber<Integer> so = new SafeSubscriber<Integer>(ts);
        so.onNext(1);
        ts.assertFailureAndMessage(NullPointerException.class, "Subscription not set!");
    }

    @Test
    public void onErrorWithoutOnSubscribe() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        SafeSubscriber<Integer> so = new SafeSubscriber<Integer>(ts);
        so.onError(new TestException());
        ts.assertFailure(CompositeException.class);
        TestHelper.assertError(ts, 0, TestException.class);
        TestHelper.assertError(ts, 1, NullPointerException.class, "Subscription not set!");
    }

    @Test
    public void onCompleteWithoutOnSubscribe() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        SafeSubscriber<Integer> so = new SafeSubscriber<Integer>(ts);
        so.onComplete();
        ts.assertFailureAndMessage(NullPointerException.class, "Subscription not set!");
    }

    @Test
    public void onNextNormal() {
        TestSubscriber<Integer> ts = new TestSubscriber<Integer>();
        SafeSubscriber<Integer> so = new SafeSubscriber<Integer>(ts);
        BooleanSubscription bs = new BooleanSubscription();
        so.onSubscribe(bs);
        so.onNext(1);
        so.onComplete();
        ts.assertResult(1);
    }

    static final class CrashDummy implements FlowableSubscriber<Object> , Subscription {
        final boolean crashOnSubscribe;

        int crashOnNext;

        final boolean crashOnError;

        final boolean crashOnComplete;

        final boolean crashDispose;

        final boolean crashRequest;

        Throwable error;

        CrashDummy(boolean crashOnSubscribe, int crashOnNext, boolean crashOnError, boolean crashOnComplete, boolean crashDispose, boolean crashRequest) {
            this.crashOnSubscribe = crashOnSubscribe;
            this.crashOnNext = crashOnNext;
            this.crashOnError = crashOnError;
            this.crashOnComplete = crashOnComplete;
            this.crashDispose = crashDispose;
            this.crashRequest = crashRequest;
        }

        @Override
        public void cancel() {
            if (crashDispose) {
                throw new TestException("cancel()");
            }
        }

        @Override
        public void request(long n) {
            if (crashRequest) {
                throw new TestException("request()");
            }
        }

        @Override
        public void onSubscribe(Subscription s) {
            if (crashOnSubscribe) {
                throw new TestException("onSubscribe()");
            }
        }

        @Override
        public void onNext(Object value) {
            if ((--(crashOnNext)) == 0) {
                throw new TestException((("onNext(" + value) + ")"));
            }
        }

        @Override
        public void onError(Throwable e) {
            if (crashOnError) {
                throw new TestException((("onError(" + e) + ")"));
            }
            error = e;
        }

        @Override
        public void onComplete() {
            if (crashOnComplete) {
                throw new TestException("onComplete()");
            }
        }

        public SafeSubscriber<Object> toSafe() {
            return new SafeSubscriber<Object>(this);
        }

        public SafeSubscriberTest.CrashDummy assertError(Class<? extends Throwable> clazz) {
            if (!(clazz.isInstance(error))) {
                throw new AssertionError(("Different error: " + (error)));
            }
            return this;
        }

        public SafeSubscriberTest.CrashDummy assertInnerError(int index, Class<? extends Throwable> clazz) {
            List<Throwable> cel = TestHelper.compositeList(error);
            TestHelper.assertError(cel, index, clazz);
            return this;
        }

        public SafeSubscriberTest.CrashDummy assertInnerError(int index, Class<? extends Throwable> clazz, String message) {
            List<Throwable> cel = TestHelper.compositeList(error);
            TestHelper.assertError(cel, index, clazz, message);
            return this;
        }
    }

    @Test
    public void onNextOnErrorCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, true, false, false, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onSubscribe(cd);
            so.onNext(1);
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, TestException.class, "onNext(1)");
            TestHelper.assertError(ce, 1, TestException.class, "onError(io.reactivex.exceptions.TestException: onNext(1))");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onNextDisposeCrash() {
        SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, false, false, true, false);
        SafeSubscriber<Object> so = cd.toSafe();
        so.onSubscribe(cd);
        so.onNext(1);
        cd.assertError(CompositeException.class);
        cd.assertInnerError(0, TestException.class, "onNext(1)");
        cd.assertInnerError(1, TestException.class, "cancel()");
    }

    @Test
    public void onSubscribeTwice() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, false, false, false, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onSubscribe(cd);
            so.onSubscribe(cd);
            TestHelper.assertError(list, 0, IllegalStateException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onSubscribeCrashes() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(true, 1, false, false, false, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onSubscribe(cd);
            TestHelper.assertUndeliverable(list, 0, TestException.class, "onSubscribe()");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onSubscribeAndDisposeCrashes() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(true, 1, false, false, true, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onSubscribe(cd);
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, TestException.class, "onSubscribe()");
            TestHelper.assertError(ce, 1, TestException.class, "cancel()");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onNextOnSubscribeCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(true, 1, false, false, false, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onNext(1);
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, NullPointerException.class, "Subscription not set!");
            TestHelper.assertError(ce, 1, TestException.class, "onSubscribe()");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onNextNullDisposeCrashes() {
        SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, false, false, true, false);
        SafeSubscriber<Object> so = cd.toSafe();
        so.onSubscribe(cd);
        so.onNext(null);
        cd.assertInnerError(0, NullPointerException.class);
        cd.assertInnerError(1, TestException.class, "cancel()");
    }

    @Test
    public void noSubscribeOnErrorCrashes() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, true, false, false, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onNext(1);
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, NullPointerException.class, "Subscription not set!");
            TestHelper.assertError(ce, 1, TestException.class, "onError(java.lang.NullPointerException: Subscription not set!)");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onErrorNull() {
        SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, false, false, false, false);
        SafeSubscriber<Object> so = cd.toSafe();
        so.onSubscribe(cd);
        so.onError(null);
        cd.assertError(NullPointerException.class);
    }

    @Test
    public void onErrorNoSubscribeCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(true, 1, false, false, false, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onError(new TestException());
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, TestException.class);
            TestHelper.assertError(ce, 1, NullPointerException.class, "Subscription not set!");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onErrorNoSubscribeOnErrorCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, true, false, false, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onError(new TestException());
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, TestException.class);
            TestHelper.assertError(ce, 1, NullPointerException.class, "Subscription not set!");
            TestHelper.assertError(ce, 2, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteteCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, false, true, false, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onSubscribe(cd);
            so.onComplete();
            TestHelper.assertUndeliverable(list, 0, TestException.class, "onComplete()");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteteNoSubscribeCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(true, 1, false, true, false, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onComplete();
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, NullPointerException.class, "Subscription not set!");
            TestHelper.assertError(ce, 1, TestException.class, "onSubscribe()");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onCompleteteNoSubscribeOnErrorCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, true, true, false, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onComplete();
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, NullPointerException.class, "Subscription not set!");
            TestHelper.assertError(ce, 1, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void requestCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, false, false, false, true);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onSubscribe(cd);
            so.request(1);
            TestHelper.assertUndeliverable(list, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void cancelCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, false, false, true, false);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onSubscribe(cd);
            so.cancel();
            TestHelper.assertUndeliverable(list, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void requestCancelCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeSubscriberTest.CrashDummy cd = new SafeSubscriberTest.CrashDummy(false, 1, false, false, true, true);
            SafeSubscriber<Object> so = cd.toSafe();
            so.onSubscribe(cd);
            so.request(1);
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, TestException.class, "request()");
            TestHelper.assertError(ce, 1, TestException.class, "cancel()");
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

