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


import io.reactivex.Observable;
import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.util.EndConsumerHelper;
import io.reactivex.plugins.RxJavaPlugins;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class ResourceObserverTest {
    static final class TestResourceObserver<T> extends ResourceObserver<T> {
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
    }

    @Test(expected = NullPointerException.class)
    public void nullResource() {
        ResourceObserverTest.TestResourceObserver<Integer> ro = new ResourceObserverTest.TestResourceObserver<Integer>();
        ro.add(null);
    }

    @Test
    public void addResources() {
        ResourceObserverTest.TestResourceObserver<Integer> ro = new ResourceObserverTest.TestResourceObserver<Integer>();
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
        ResourceObserverTest.TestResourceObserver<Integer> ro = new ResourceObserverTest.TestResourceObserver<Integer>();
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
        ResourceObserverTest.TestResourceObserver<Integer> ro = new ResourceObserverTest.TestResourceObserver<Integer>();
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
        ResourceObserverTest.TestResourceObserver<Integer> tc = new ResourceObserverTest.TestResourceObserver<Integer>();
        Assert.assertFalse(tc.isDisposed());
        Assert.assertEquals(0, tc.start);
        Assert.assertTrue(tc.values.isEmpty());
        Assert.assertTrue(tc.errors.isEmpty());
        Observable.just(1).subscribe(tc);
        Assert.assertTrue(tc.isDisposed());
        Assert.assertEquals(1, tc.start);
        Assert.assertEquals(1, tc.values.get(0).intValue());
        Assert.assertTrue(tc.errors.isEmpty());
    }

    @Test
    public void error() {
        ResourceObserverTest.TestResourceObserver<Integer> tc = new ResourceObserverTest.TestResourceObserver<Integer>();
        Assert.assertFalse(tc.isDisposed());
        Assert.assertEquals(0, tc.start);
        Assert.assertTrue(tc.values.isEmpty());
        Assert.assertTrue(tc.errors.isEmpty());
        final RuntimeException error = new RuntimeException("error");
        Observable.<Integer>error(error).subscribe(tc);
        Assert.assertTrue(tc.isDisposed());
        Assert.assertEquals(1, tc.start);
        Assert.assertTrue(tc.values.isEmpty());
        Assert.assertEquals(1, tc.errors.size());
        Assert.assertTrue(tc.errors.contains(error));
    }

    @Test
    public void startOnce() {
        List<Throwable> error = TestHelper.trackPluginErrors();
        try {
            ResourceObserverTest.TestResourceObserver<Integer> tc = new ResourceObserverTest.TestResourceObserver<Integer>();
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
        ResourceObserverTest.TestResourceObserver<Integer> tc = new ResourceObserverTest.TestResourceObserver<Integer>();
        tc.dispose();
        Disposable d = Disposables.empty();
        tc.onSubscribe(d);
        Assert.assertTrue(d.isDisposed());
        Assert.assertEquals(0, tc.start);
    }
}

