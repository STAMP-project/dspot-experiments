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


public class DisposableMaybeObserverTest {
    static final class TestMaybe<T> extends DisposableMaybeObserver<T> {
        int start;

        final List<T> values = new ArrayList<T>();

        final List<Throwable> errors = new ArrayList<Throwable>();

        int complete;

        @Override
        protected void onStart() {
            super.onStart();
            (start)++;
        }

        @Override
        public void onSuccess(T value) {
            values.add(value);
        }

        @Override
        public void onError(Throwable e) {
            errors.add(e);
        }

        @Override
        public void onComplete() {
            (complete)++;
        }
    }

    @Test
    public void normal() {
        DisposableMaybeObserverTest.TestMaybe<Integer> tc = new DisposableMaybeObserverTest.TestMaybe<Integer>();
        Assert.assertFalse(tc.isDisposed());
        Assert.assertEquals(0, tc.start);
        Assert.assertTrue(tc.values.isEmpty());
        Assert.assertTrue(tc.errors.isEmpty());
        Assert.assertEquals(0, tc.complete);
        Maybe.just(1).subscribe(tc);
        Assert.assertFalse(tc.isDisposed());
        Assert.assertEquals(1, tc.start);
        Assert.assertEquals(1, tc.values.get(0).intValue());
        Assert.assertTrue(tc.errors.isEmpty());
        Assert.assertEquals(0, tc.complete);
    }

    @Test
    public void startOnce() {
        List<Throwable> error = TestHelper.trackPluginErrors();
        try {
            DisposableMaybeObserverTest.TestMaybe<Integer> tc = new DisposableMaybeObserverTest.TestMaybe<Integer>();
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
        DisposableMaybeObserverTest.TestMaybe<Integer> tc = new DisposableMaybeObserverTest.TestMaybe<Integer>();
        tc.dispose();
        Assert.assertTrue(tc.isDisposed());
        Disposable d = Disposables.empty();
        tc.onSubscribe(d);
        Assert.assertTrue(d.isDisposed());
        Assert.assertEquals(0, tc.start);
    }
}

