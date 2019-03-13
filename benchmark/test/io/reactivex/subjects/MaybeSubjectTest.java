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
package io.reactivex.subjects;


import io.reactivex.TestHelper;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import java.io.IOException;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class MaybeSubjectTest {
    @Test
    public void success() {
        MaybeSubject<Integer> ms = MaybeSubject.create();
        Assert.assertFalse(ms.hasValue());
        Assert.assertNull(ms.getValue());
        Assert.assertFalse(ms.hasComplete());
        Assert.assertFalse(ms.hasThrowable());
        Assert.assertNull(ms.getThrowable());
        Assert.assertFalse(ms.hasObservers());
        Assert.assertEquals(0, ms.observerCount());
        TestObserver<Integer> to = ms.test();
        to.assertEmpty();
        Assert.assertTrue(ms.hasObservers());
        Assert.assertEquals(1, ms.observerCount());
        ms.onSuccess(1);
        Assert.assertTrue(ms.hasValue());
        Assert.assertEquals(1, ms.getValue().intValue());
        Assert.assertFalse(ms.hasComplete());
        Assert.assertFalse(ms.hasThrowable());
        Assert.assertNull(ms.getThrowable());
        Assert.assertFalse(ms.hasObservers());
        Assert.assertEquals(0, ms.observerCount());
        to.assertResult(1);
        ms.test().assertResult(1);
        Assert.assertTrue(ms.hasValue());
        Assert.assertEquals(1, ms.getValue().intValue());
        Assert.assertFalse(ms.hasComplete());
        Assert.assertFalse(ms.hasThrowable());
        Assert.assertNull(ms.getThrowable());
        Assert.assertFalse(ms.hasObservers());
        Assert.assertEquals(0, ms.observerCount());
    }

    @Test
    public void once() {
        MaybeSubject<Integer> ms = MaybeSubject.create();
        TestObserver<Integer> to = ms.test();
        ms.onSuccess(1);
        ms.onSuccess(2);
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            ms.onError(new IOException());
            TestHelper.assertUndeliverable(errors, 0, IOException.class);
        } finally {
            RxJavaPlugins.reset();
        }
        ms.onComplete();
        to.assertResult(1);
    }

    @Test
    public void error() {
        MaybeSubject<Integer> ms = MaybeSubject.create();
        Assert.assertFalse(ms.hasValue());
        Assert.assertNull(ms.getValue());
        Assert.assertFalse(ms.hasComplete());
        Assert.assertFalse(ms.hasThrowable());
        Assert.assertNull(ms.getThrowable());
        Assert.assertFalse(ms.hasObservers());
        Assert.assertEquals(0, ms.observerCount());
        TestObserver<Integer> to = ms.test();
        to.assertEmpty();
        Assert.assertTrue(ms.hasObservers());
        Assert.assertEquals(1, ms.observerCount());
        ms.onError(new IOException());
        Assert.assertFalse(ms.hasValue());
        Assert.assertNull(ms.getValue());
        Assert.assertFalse(ms.hasComplete());
        Assert.assertTrue(ms.hasThrowable());
        Assert.assertTrue(ms.getThrowable().toString(), ((ms.getThrowable()) instanceof IOException));
        Assert.assertFalse(ms.hasObservers());
        Assert.assertEquals(0, ms.observerCount());
        to.assertFailure(IOException.class);
        ms.test().assertFailure(IOException.class);
        Assert.assertFalse(ms.hasValue());
        Assert.assertNull(ms.getValue());
        Assert.assertFalse(ms.hasComplete());
        Assert.assertTrue(ms.hasThrowable());
        Assert.assertTrue(ms.getThrowable().toString(), ((ms.getThrowable()) instanceof IOException));
        Assert.assertFalse(ms.hasObservers());
        Assert.assertEquals(0, ms.observerCount());
    }

    @Test
    public void complete() {
        MaybeSubject<Integer> ms = MaybeSubject.create();
        Assert.assertFalse(ms.hasValue());
        Assert.assertNull(ms.getValue());
        Assert.assertFalse(ms.hasComplete());
        Assert.assertFalse(ms.hasThrowable());
        Assert.assertNull(ms.getThrowable());
        Assert.assertFalse(ms.hasObservers());
        Assert.assertEquals(0, ms.observerCount());
        TestObserver<Integer> to = ms.test();
        to.assertEmpty();
        Assert.assertTrue(ms.hasObservers());
        Assert.assertEquals(1, ms.observerCount());
        ms.onComplete();
        Assert.assertFalse(ms.hasValue());
        Assert.assertNull(ms.getValue());
        Assert.assertTrue(ms.hasComplete());
        Assert.assertFalse(ms.hasThrowable());
        Assert.assertNull(ms.getThrowable());
        Assert.assertFalse(ms.hasObservers());
        Assert.assertEquals(0, ms.observerCount());
        to.assertResult();
        ms.test().assertResult();
        Assert.assertFalse(ms.hasValue());
        Assert.assertNull(ms.getValue());
        Assert.assertTrue(ms.hasComplete());
        Assert.assertFalse(ms.hasThrowable());
        Assert.assertNull(ms.getThrowable());
        Assert.assertFalse(ms.hasObservers());
        Assert.assertEquals(0, ms.observerCount());
    }

    @Test
    public void cancelOnArrival() {
        MaybeSubject.create().test(true).assertEmpty();
    }

    @Test
    public void cancelOnArrival2() {
        MaybeSubject<Integer> ms = MaybeSubject.create();
        ms.test();
        ms.test(true).assertEmpty();
    }

    @Test
    public void dispose() {
        TestHelper.checkDisposed(MaybeSubject.create());
    }

    @Test
    public void disposeTwice() {
        MaybeSubject.create().subscribe(new MaybeObserver<Object>() {
            @Override
            public void onSubscribe(Disposable d) {
                Assert.assertFalse(d.isDisposed());
                d.dispose();
                d.dispose();
                Assert.assertTrue(d.isDisposed());
            }

            @Override
            public void onSuccess(Object value) {
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
            }
        });
    }

    @Test
    public void onSubscribeDispose() {
        MaybeSubject<Integer> ms = MaybeSubject.create();
        Disposable d = Disposables.empty();
        ms.onSubscribe(d);
        Assert.assertFalse(d.isDisposed());
        ms.onComplete();
        d = Disposables.empty();
        ms.onSubscribe(d);
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void addRemoveRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final MaybeSubject<Integer> ms = MaybeSubject.create();
            final TestObserver<Integer> to = ms.test();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    ms.test();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    to.cancel();
                }
            };
            TestHelper.race(r1, r2);
        }
    }
}

