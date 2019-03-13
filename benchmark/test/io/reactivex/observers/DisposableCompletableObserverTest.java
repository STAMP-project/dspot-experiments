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
import io.reactivex.internal.util.EndConsumerHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DisposableCompletableObserverTest {
    static final class TestCompletable extends DisposableCompletableObserver {
        int start;

        int complete;

        final List<Throwable> errors = new ArrayList<Throwable>();

        @Override
        protected void onStart() {
            super.onStart();
            (start)++;
        }

        @Override
        public void onComplete() {
            (complete)++;
        }

        @Override
        public void onError(Throwable e) {
            errors.add(e);
        }
    }

    @Test
    public void normal() {
        DisposableCompletableObserverTest.TestCompletable tc = new DisposableCompletableObserverTest.TestCompletable();
        Assert.assertFalse(isDisposed());
        Assert.assertEquals(0, tc.start);
        Assert.assertEquals(0, tc.complete);
        Assert.assertTrue(tc.errors.isEmpty());
        Completable.complete().subscribe(tc);
        Assert.assertFalse(isDisposed());
        Assert.assertEquals(1, tc.start);
        Assert.assertEquals(1, tc.complete);
        Assert.assertTrue(tc.errors.isEmpty());
    }

    @Test
    public void startOnce() {
        List<Throwable> error = TestHelper.trackPluginErrors();
        try {
            DisposableCompletableObserverTest.TestCompletable tc = new DisposableCompletableObserverTest.TestCompletable();
            tc.onSubscribe(Disposables.empty());
            Disposable d = Disposables.empty();
            tc.onSubscribe(d);
            Assert.assertTrue(d.isDisposed());
            Assert.assertEquals(1, tc.start);
            TestHelper.assertError(error, 0, IllegalStateException.class, EndConsumerHelper.composeMessage(tc.getClass().getName()));
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void dispose() {
        DisposableCompletableObserverTest.TestCompletable tc = new DisposableCompletableObserverTest.TestCompletable();
        tc.dispose();
        Assert.assertTrue(isDisposed());
        Disposable d = Disposables.empty();
        tc.onSubscribe(d);
        Assert.assertTrue(d.isDisposed());
        Assert.assertEquals(0, tc.start);
    }
}

