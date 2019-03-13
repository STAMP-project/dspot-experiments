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


import io.reactivex.Maybe;
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


public class ResourceMaybeObserverTest {
    static final class TestResourceMaybeObserver<T> extends ResourceMaybeObserver<T> {
        T value;

        final List<Throwable> errors = new ArrayList<Throwable>();

        int complete;

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
        public void onComplete() {
            (complete)++;
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
        ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer> rmo = new ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer>();
        rmo.add(null);
    }

    @Test
    public void addResources() {
        ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer> rmo = new ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer>();
        Assert.assertFalse(rmo.isDisposed());
        Disposable d = Disposables.empty();
        rmo.add(d);
        Assert.assertFalse(d.isDisposed());
        rmo.dispose();
        Assert.assertTrue(rmo.isDisposed());
        Assert.assertTrue(d.isDisposed());
        rmo.dispose();
        Assert.assertTrue(rmo.isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void onCompleteCleansUp() {
        ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer> rmo = new ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer>();
        Assert.assertFalse(rmo.isDisposed());
        Disposable d = Disposables.empty();
        rmo.add(d);
        Assert.assertFalse(d.isDisposed());
        rmo.onComplete();
        Assert.assertTrue(rmo.isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void onSuccessCleansUp() {
        ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer> rmo = new ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer>();
        Assert.assertFalse(rmo.isDisposed());
        Disposable d = Disposables.empty();
        rmo.add(d);
        Assert.assertFalse(d.isDisposed());
        rmo.onSuccess(1);
        Assert.assertTrue(rmo.isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void onErrorCleansUp() {
        ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer> rmo = new ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer>();
        Assert.assertFalse(rmo.isDisposed());
        Disposable d = Disposables.empty();
        rmo.add(d);
        Assert.assertFalse(d.isDisposed());
        rmo.onError(new TestException());
        Assert.assertTrue(rmo.isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void normal() {
        ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer> rmo = new ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer>();
        Assert.assertFalse(rmo.isDisposed());
        Assert.assertEquals(0, rmo.start);
        Assert.assertNull(rmo.value);
        Assert.assertTrue(rmo.errors.isEmpty());
        Maybe.just(1).subscribe(rmo);
        Assert.assertTrue(rmo.isDisposed());
        Assert.assertEquals(1, rmo.start);
        Assert.assertEquals(Integer.valueOf(1), rmo.value);
        Assert.assertEquals(0, rmo.complete);
        Assert.assertTrue(rmo.errors.isEmpty());
    }

    @Test
    public void empty() {
        ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer> rmo = new ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer>();
        Assert.assertFalse(rmo.isDisposed());
        Assert.assertEquals(0, rmo.start);
        Assert.assertNull(rmo.value);
        Assert.assertTrue(rmo.errors.isEmpty());
        Maybe.<Integer>empty().subscribe(rmo);
        Assert.assertTrue(rmo.isDisposed());
        Assert.assertEquals(1, rmo.start);
        Assert.assertNull(rmo.value);
        Assert.assertEquals(1, rmo.complete);
        Assert.assertTrue(rmo.errors.isEmpty());
    }

    @Test
    public void error() {
        ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer> rmo = new ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer>();
        Assert.assertFalse(rmo.isDisposed());
        Assert.assertEquals(0, rmo.start);
        Assert.assertNull(rmo.value);
        Assert.assertTrue(rmo.errors.isEmpty());
        final RuntimeException error = new RuntimeException("error");
        Maybe.<Integer>error(error).subscribe(rmo);
        Assert.assertTrue(rmo.isDisposed());
        Assert.assertEquals(1, rmo.start);
        Assert.assertNull(rmo.value);
        Assert.assertEquals(0, rmo.complete);
        Assert.assertEquals(1, rmo.errors.size());
        Assert.assertTrue(rmo.errors.contains(error));
    }

    @Test
    public void startOnce() {
        List<Throwable> error = TestHelper.trackPluginErrors();
        try {
            ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer> rmo = new ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer>();
            rmo.onSubscribe(Disposables.empty());
            Disposable d = Disposables.empty();
            rmo.onSubscribe(d);
            Assert.assertTrue(d.isDisposed());
            Assert.assertEquals(1, rmo.start);
            TestHelper.assertError(error, 0, IllegalStateException.class, EndConsumerHelper.composeMessage(rmo.getClass().getName()));
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void dispose() {
        ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer> rmo = new ResourceMaybeObserverTest.TestResourceMaybeObserver<Integer>();
        rmo.dispose();
        Disposable d = Disposables.empty();
        rmo.onSubscribe(d);
        Assert.assertTrue(d.isDisposed());
        Assert.assertEquals(0, rmo.start);
    }
}

