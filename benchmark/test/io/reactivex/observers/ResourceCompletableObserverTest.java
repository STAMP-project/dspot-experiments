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


import io.reactivex.Completable;
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


public class ResourceCompletableObserverTest {
    static final class TestResourceCompletableObserver extends ResourceCompletableObserver {
        final List<Throwable> errors = new ArrayList<Throwable>();

        int complete;

        int start;

        @Override
        protected void onStart() {
            super.onStart();
            (start)++;
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
        ResourceCompletableObserverTest.TestResourceCompletableObserver rco = new ResourceCompletableObserverTest.TestResourceCompletableObserver();
        add(null);
    }

    @Test
    public void addResources() {
        ResourceCompletableObserverTest.TestResourceCompletableObserver rco = new ResourceCompletableObserverTest.TestResourceCompletableObserver();
        Assert.assertFalse(isDisposed());
        Disposable d = Disposables.empty();
        rco.add(d);
        Assert.assertFalse(d.isDisposed());
        rco.dispose();
        Assert.assertTrue(isDisposed());
        Assert.assertTrue(d.isDisposed());
        rco.dispose();
        Assert.assertTrue(isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void onCompleteCleansUp() {
        ResourceCompletableObserverTest.TestResourceCompletableObserver rco = new ResourceCompletableObserverTest.TestResourceCompletableObserver();
        Assert.assertFalse(isDisposed());
        Disposable d = Disposables.empty();
        rco.add(d);
        Assert.assertFalse(d.isDisposed());
        rco.onComplete();
        Assert.assertTrue(isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void onErrorCleansUp() {
        ResourceCompletableObserverTest.TestResourceCompletableObserver rco = new ResourceCompletableObserverTest.TestResourceCompletableObserver();
        Assert.assertFalse(isDisposed());
        Disposable d = Disposables.empty();
        rco.add(d);
        Assert.assertFalse(d.isDisposed());
        rco.onError(new TestException());
        Assert.assertTrue(isDisposed());
        Assert.assertTrue(d.isDisposed());
    }

    @Test
    public void normal() {
        ResourceCompletableObserverTest.TestResourceCompletableObserver rco = new ResourceCompletableObserverTest.TestResourceCompletableObserver();
        Assert.assertFalse(isDisposed());
        Assert.assertEquals(0, rco.start);
        Assert.assertTrue(rco.errors.isEmpty());
        Completable.complete().subscribe(rco);
        Assert.assertTrue(isDisposed());
        Assert.assertEquals(1, rco.start);
        Assert.assertEquals(1, rco.complete);
        Assert.assertTrue(rco.errors.isEmpty());
    }

    @Test
    public void error() {
        ResourceCompletableObserverTest.TestResourceCompletableObserver rco = new ResourceCompletableObserverTest.TestResourceCompletableObserver();
        Assert.assertFalse(isDisposed());
        Assert.assertEquals(0, rco.start);
        Assert.assertTrue(rco.errors.isEmpty());
        final RuntimeException error = new RuntimeException("error");
        Completable.error(error).subscribe(rco);
        Assert.assertTrue(isDisposed());
        Assert.assertEquals(1, rco.start);
        Assert.assertEquals(0, rco.complete);
        Assert.assertEquals(1, rco.errors.size());
        Assert.assertTrue(rco.errors.contains(error));
    }

    @Test
    public void startOnce() {
        List<Throwable> error = TestHelper.trackPluginErrors();
        try {
            ResourceCompletableObserverTest.TestResourceCompletableObserver rco = new ResourceCompletableObserverTest.TestResourceCompletableObserver();
            rco.onSubscribe(Disposables.empty());
            Disposable d = Disposables.empty();
            rco.onSubscribe(d);
            Assert.assertTrue(d.isDisposed());
            Assert.assertEquals(1, rco.start);
            TestHelper.assertError(error, 0, IllegalStateException.class, EndConsumerHelper.composeMessage(rco.getClass().getName()));
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void dispose() {
        ResourceCompletableObserverTest.TestResourceCompletableObserver rco = new ResourceCompletableObserverTest.TestResourceCompletableObserver();
        rco.dispose();
        Disposable d = Disposables.empty();
        rco.onSubscribe(d);
        Assert.assertTrue(d.isDisposed());
        Assert.assertEquals(0, rco.start);
    }
}

