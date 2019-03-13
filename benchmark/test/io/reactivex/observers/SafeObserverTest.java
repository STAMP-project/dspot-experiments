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
package io.reactivex.observers;


import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeException;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class SafeObserverTest {
    @Test
    public void onNextFailure() {
        AtomicReference<Throwable> onError = new AtomicReference<Throwable>();
        try {
            SafeObserverTest.OBSERVER_ONNEXT_FAIL(onError).onNext("one");
            Assert.fail("expects exception to be thrown");
        } catch (Exception e) {
            Assert.assertNull(onError.get());
            Assert.assertTrue((e instanceof SafeObserverTest.SafeObserverTestException));
            Assert.assertEquals("onNextFail", e.getMessage());
        }
    }

    @Test
    public void onNextFailureSafe() {
        AtomicReference<Throwable> onError = new AtomicReference<Throwable>();
        try {
            SafeObserver<String> safeObserver = new SafeObserver<String>(SafeObserverTest.OBSERVER_ONNEXT_FAIL(onError));
            safeObserver.onSubscribe(Disposables.empty());
            safeObserver.onNext("one");
            Assert.assertNotNull(onError.get());
            Assert.assertTrue(((onError.get()) instanceof SafeObserverTest.SafeObserverTestException));
            Assert.assertEquals("onNextFail", onError.get().getMessage());
        } catch (Exception e) {
            Assert.fail("expects exception to be passed to onError");
        }
    }

    @Test
    public void onCompleteFailure() {
        AtomicReference<Throwable> onError = new AtomicReference<Throwable>();
        try {
            SafeObserverTest.OBSERVER_ONCOMPLETED_FAIL(onError).onComplete();
            Assert.fail("expects exception to be thrown");
        } catch (Exception e) {
            Assert.assertNull(onError.get());
            Assert.assertTrue((e instanceof SafeObserverTest.SafeObserverTestException));
            Assert.assertEquals("onCompleteFail", e.getMessage());
        }
    }

    @Test
    public void onErrorFailure() {
        try {
            SafeObserverTest.OBSERVER_ONERROR_FAIL().onError(new SafeObserverTest.SafeObserverTestException("error!"));
            Assert.fail("expects exception to be thrown");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof SafeObserverTest.SafeObserverTestException));
            Assert.assertEquals("onErrorFail", e.getMessage());
        }
    }

    @Test
    public void onNextOnErrorFailure() {
        try {
            SafeObserverTest.OBSERVER_ONNEXT_ONERROR_FAIL().onNext("one");
            Assert.fail("expects exception to be thrown");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue((e instanceof SafeObserverTest.SafeObserverTestException));
            Assert.assertEquals("onNextFail", e.getMessage());
        }
    }

    static final Disposable THROWING_DISPOSABLE = new Disposable() {
        @Override
        public boolean isDisposed() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void dispose() {
            // break contract by throwing exception
            throw new SafeObserverTest.SafeObserverTestException("failure from unsubscribe");
        }
    };

    @SuppressWarnings("serial")
    static class SafeObserverTestException extends RuntimeException {
        SafeObserverTestException(String message) {
            super(message);
        }
    }

    @Test
    public void testActual() {
        Observer<Integer> actual = new DefaultObserver<Integer>() {
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
        SafeObserver<Integer> observer = new SafeObserver<Integer>(actual);
        Assert.assertSame(actual, observer.downstream);
    }

    @Test
    public void dispose() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        SafeObserver<Integer> so = new SafeObserver<Integer>(to);
        Disposable d = Disposables.empty();
        so.onSubscribe(d);
        to.dispose();
        Assert.assertTrue(d.isDisposed());
        Assert.assertTrue(so.isDisposed());
    }

    @Test
    public void onNextAfterComplete() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        SafeObserver<Integer> so = new SafeObserver<Integer>(to);
        Disposable d = Disposables.empty();
        so.onSubscribe(d);
        so.onComplete();
        so.onNext(1);
        so.onError(new TestException());
        so.onComplete();
        to.assertResult();
    }

    @Test
    public void onNextNull() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        SafeObserver<Integer> so = new SafeObserver<Integer>(to);
        Disposable d = Disposables.empty();
        so.onSubscribe(d);
        so.onNext(null);
        to.assertFailure(NullPointerException.class);
    }

    @Test
    public void onNextWithoutOnSubscribe() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        SafeObserver<Integer> so = new SafeObserver<Integer>(to);
        so.onNext(1);
        to.assertFailureAndMessage(NullPointerException.class, "Subscription not set!");
    }

    @Test
    public void onErrorWithoutOnSubscribe() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        SafeObserver<Integer> so = new SafeObserver<Integer>(to);
        so.onError(new TestException());
        to.assertFailure(CompositeException.class);
        TestHelper.assertError(to, 0, TestException.class);
        TestHelper.assertError(to, 1, NullPointerException.class, "Subscription not set!");
    }

    @Test
    public void onCompleteWithoutOnSubscribe() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        SafeObserver<Integer> so = new SafeObserver<Integer>(to);
        so.onComplete();
        to.assertFailureAndMessage(NullPointerException.class, "Subscription not set!");
    }

    @Test
    public void onNextNormal() {
        TestObserver<Integer> to = new TestObserver<Integer>();
        SafeObserver<Integer> so = new SafeObserver<Integer>(to);
        Disposable d = Disposables.empty();
        so.onSubscribe(d);
        so.onNext(1);
        so.onComplete();
        to.assertResult(1);
    }

    static final class CrashDummy implements Disposable , Observer<Object> {
        boolean crashOnSubscribe;

        int crashOnNext;

        boolean crashOnError;

        boolean crashOnComplete;

        boolean crashDispose;

        Throwable error;

        CrashDummy(boolean crashOnSubscribe, int crashOnNext, boolean crashOnError, boolean crashOnComplete, boolean crashDispose) {
            this.crashOnSubscribe = crashOnSubscribe;
            this.crashOnNext = crashOnNext;
            this.crashOnError = crashOnError;
            this.crashOnComplete = crashOnComplete;
            this.crashDispose = crashDispose;
        }

        @Override
        public void dispose() {
            if (crashDispose) {
                throw new TestException("dispose()");
            }
        }

        @Override
        public boolean isDisposed() {
            return false;
        }

        @Override
        public void onSubscribe(Disposable d) {
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

        public SafeObserver<Object> toSafe() {
            return new SafeObserver<Object>(this);
        }

        public SafeObserverTest.CrashDummy assertError(Class<? extends Throwable> clazz) {
            if (!(clazz.isInstance(error))) {
                throw new AssertionError(("Different error: " + (error)));
            }
            return this;
        }

        public SafeObserverTest.CrashDummy assertInnerError(int index, Class<? extends Throwable> clazz) {
            List<Throwable> cel = TestHelper.compositeList(error);
            TestHelper.assertError(cel, index, clazz);
            return this;
        }

        public SafeObserverTest.CrashDummy assertInnerError(int index, Class<? extends Throwable> clazz, String message) {
            List<Throwable> cel = TestHelper.compositeList(error);
            TestHelper.assertError(cel, index, clazz, message);
            return this;
        }
    }

    @Test
    public void onNextOnErrorCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(false, 1, true, false, false);
            SafeObserver<Object> so = cd.toSafe();
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
        SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(false, 1, false, false, true);
        SafeObserver<Object> so = cd.toSafe();
        so.onSubscribe(cd);
        so.onNext(1);
        cd.assertError(CompositeException.class);
        cd.assertInnerError(0, TestException.class, "onNext(1)");
        cd.assertInnerError(1, TestException.class, "dispose()");
    }

    @Test
    public void onSubscribeTwice() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(false, 1, false, false, false);
            SafeObserver<Object> so = cd.toSafe();
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
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(true, 1, false, false, false);
            SafeObserver<Object> so = cd.toSafe();
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
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(true, 1, false, false, true);
            SafeObserver<Object> so = cd.toSafe();
            so.onSubscribe(cd);
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, TestException.class, "onSubscribe()");
            TestHelper.assertError(ce, 1, TestException.class, "dispose()");
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void onNextOnSubscribeCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(true, 1, false, false, false);
            SafeObserver<Object> so = cd.toSafe();
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
        SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(false, 1, false, false, true);
        SafeObserver<Object> so = cd.toSafe();
        so.onSubscribe(cd);
        so.onNext(null);
        cd.assertInnerError(0, NullPointerException.class);
        cd.assertInnerError(1, TestException.class, "dispose()");
    }

    @Test
    public void noSubscribeOnErrorCrashes() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(false, 1, true, false, false);
            SafeObserver<Object> so = cd.toSafe();
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
        SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(false, 1, false, false, false);
        SafeObserver<Object> so = cd.toSafe();
        so.onSubscribe(cd);
        so.onError(null);
        cd.assertError(NullPointerException.class);
    }

    @Test
    public void onErrorNoSubscribeCrash() {
        List<Throwable> list = TestHelper.trackPluginErrors();
        try {
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(true, 1, false, false, false);
            SafeObserver<Object> so = cd.toSafe();
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
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(false, 1, true, false, false);
            SafeObserver<Object> so = cd.toSafe();
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
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(false, 1, false, true, false);
            SafeObserver<Object> so = cd.toSafe();
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
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(true, 1, false, true, false);
            SafeObserver<Object> so = cd.toSafe();
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
            SafeObserverTest.CrashDummy cd = new SafeObserverTest.CrashDummy(false, 1, true, true, false);
            SafeObserver<Object> so = cd.toSafe();
            so.onComplete();
            TestHelper.assertError(list, 0, CompositeException.class);
            List<Throwable> ce = TestHelper.compositeList(list.get(0));
            TestHelper.assertError(ce, 0, NullPointerException.class, "Subscription not set!");
            TestHelper.assertError(ce, 1, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

