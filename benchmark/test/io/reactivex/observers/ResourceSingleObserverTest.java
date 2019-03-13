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


import io.reactivex.Single;
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


public class ResourceSingleObserverTest {
    static final class TestResourceSingleObserver<T> extends ResourceSingleObserver<T> {
        T value;

        final List<Throwable> errors = new ArrayList<Throwable>();

        int start;

        @Override
        protected void onStart() {
            super.onStart();
            (start)++;
        }

        @Override
        public void onSuccess(final T value) {
            this.value = value;
            dispose();
        }

        @Override
        public void onError(Throwable e) {
            errors.add(e);
            dispose();
        }
    }

    @Test(expected = NullPointerException.class)
    public void nullResource() {
        ResourceSingleObserverTest.TestResourceSingleObserver<Integer> rso = new ResourceSingleObserverTest.TestResourceSingleObserver<Integer>();
        rso.add(null);
    }

    @Test
    public void addResources() {
        ResourceSingleObserverTest.TestResourceSingleObserver<Integer> rso = new ResourceSingleObserverTest.TestResourceSingleObserver<Integer>();
        Assert.assertFalse(rso.isDisposed());
        Disposable d = Disposables.empty();
        rso.add(d);
        Assert.assertFalse(d.isDisposed());
        rso.dispose();
        Assert.assertTrue(rso.isDisposed());
        Assert.assertTrue(d.isDisposed());
        rso.dispose();
        Assert.assertTrue(rso.isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void onSuccessCleansUp() {
        ResourceSingleObserverTest.TestResourceSingleObserver<Integer> rso = new ResourceSingleObserverTest.TestResourceSingleObserver<Integer>();
        Assert.assertFalse(rso.isDisposed());
        Disposable d = Disposables.empty();
        rso.add(d);
        Assert.assertFalse(d.isDisposed());
        rso.onSuccess(1);
        Assert.assertTrue(rso.isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void onErrorCleansUp() {
        ResourceSingleObserverTest.TestResourceSingleObserver<Integer> rso = new ResourceSingleObserverTest.TestResourceSingleObserver<Integer>();
        Assert.assertFalse(rso.isDisposed());
        Disposable d = Disposables.empty();
        rso.add(d);
        Assert.assertFalse(d.isDisposed());
        rso.onError(new TestException());
        Assert.assertTrue(rso.isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void normal() {
        ResourceSingleObserverTest.TestResourceSingleObserver<Integer> rso = new ResourceSingleObserverTest.TestResourceSingleObserver<Integer>();
        Assert.assertFalse(rso.isDisposed());
        Assert.assertEquals(0, rso.start);
        Assert.assertNull(rso.value);
        Assert.assertTrue(rso.errors.isEmpty());
        Single.just(1).subscribe(rso);
        Assert.assertTrue(rso.isDisposed());
        Assert.assertEquals(1, rso.start);
        Assert.assertEquals(Integer.valueOf(1), rso.value);
        Assert.assertTrue(rso.errors.isEmpty());
    }

    @Test
    public void error() {
        ResourceSingleObserverTest.TestResourceSingleObserver<Integer> rso = new ResourceSingleObserverTest.TestResourceSingleObserver<Integer>();
        Assert.assertFalse(rso.isDisposed());
        Assert.assertEquals(0, rso.start);
        Assert.assertNull(rso.value);
        Assert.assertTrue(rso.errors.isEmpty());
        final RuntimeException error = new RuntimeException("error");
        Single.<Integer>error(error).subscribe(rso);
        Assert.assertTrue(rso.isDisposed());
        Assert.assertEquals(1, rso.start);
        Assert.assertNull(rso.value);
        Assert.assertEquals(1, rso.errors.size());
        Assert.assertTrue(rso.errors.contains(error));
    }

    @Test
    public void startOnce() {
        List<Throwable> error = TestHelper.trackPluginErrors();
        try {
            ResourceSingleObserverTest.TestResourceSingleObserver<Integer> rso = new ResourceSingleObserverTest.TestResourceSingleObserver<Integer>();
            rso.onSubscribe(Disposables.empty());
            Disposable d = Disposables.empty();
            rso.onSubscribe(d);
            Assert.assertTrue(d.isDisposed());
            Assert.assertEquals(1, rso.start);
            TestHelper.assertError(error, 0, IllegalStateException.class, EndConsumerHelper.composeMessage(rso.getClass().getName()));
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void dispose() {
        ResourceSingleObserverTest.TestResourceSingleObserver<Integer> rso = new ResourceSingleObserverTest.TestResourceSingleObserver<Integer>();
        rso.dispose();
        Disposable d = Disposables.empty();
        rso.onSubscribe(d);
        Assert.assertTrue(d.isDisposed());
        Assert.assertEquals(0, rso.start);
    }
}

