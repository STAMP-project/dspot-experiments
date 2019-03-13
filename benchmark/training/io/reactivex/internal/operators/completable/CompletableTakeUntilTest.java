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
package io.reactivex.internal.operators.completable;


import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposables;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.subjects.CompletableSubject;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.Assert;
import org.junit.Test;


public class CompletableTakeUntilTest {
    @Test
    public void consumerDisposes() {
        CompletableSubject cs1 = CompletableSubject.create();
        CompletableSubject cs2 = CompletableSubject.create();
        TestObserver<Void> to = cs1.takeUntil(cs2).test();
        to.assertEmpty();
        Assert.assertTrue(cs1.hasObservers());
        Assert.assertTrue(cs2.hasObservers());
        to.dispose();
        Assert.assertFalse(cs1.hasObservers());
        Assert.assertFalse(cs2.hasObservers());
    }

    @Test
    public void mainCompletes() {
        CompletableSubject cs1 = CompletableSubject.create();
        CompletableSubject cs2 = CompletableSubject.create();
        TestObserver<Void> to = cs1.takeUntil(cs2).test();
        to.assertEmpty();
        Assert.assertTrue(cs1.hasObservers());
        Assert.assertTrue(cs2.hasObservers());
        cs1.onComplete();
        Assert.assertFalse(cs1.hasObservers());
        Assert.assertFalse(cs2.hasObservers());
        to.assertResult();
    }

    @Test
    public void otherCompletes() {
        CompletableSubject cs1 = CompletableSubject.create();
        CompletableSubject cs2 = CompletableSubject.create();
        TestObserver<Void> to = cs1.takeUntil(cs2).test();
        to.assertEmpty();
        Assert.assertTrue(cs1.hasObservers());
        Assert.assertTrue(cs2.hasObservers());
        cs2.onComplete();
        Assert.assertFalse(cs1.hasObservers());
        Assert.assertFalse(cs2.hasObservers());
        to.assertResult();
    }

    @Test
    public void mainErrors() {
        CompletableSubject cs1 = CompletableSubject.create();
        CompletableSubject cs2 = CompletableSubject.create();
        TestObserver<Void> to = cs1.takeUntil(cs2).test();
        to.assertEmpty();
        Assert.assertTrue(cs1.hasObservers());
        Assert.assertTrue(cs2.hasObservers());
        cs1.onError(new TestException());
        Assert.assertFalse(cs1.hasObservers());
        Assert.assertFalse(cs2.hasObservers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void otherErrors() {
        CompletableSubject cs1 = CompletableSubject.create();
        CompletableSubject cs2 = CompletableSubject.create();
        TestObserver<Void> to = cs1.takeUntil(cs2).test();
        to.assertEmpty();
        Assert.assertTrue(cs1.hasObservers());
        Assert.assertTrue(cs2.hasObservers());
        cs2.onError(new TestException());
        Assert.assertFalse(cs1.hasObservers());
        Assert.assertFalse(cs2.hasObservers());
        to.assertFailure(TestException.class);
    }

    @Test
    public void isDisposed() {
        CompletableSubject cs1 = CompletableSubject.create();
        CompletableSubject cs2 = CompletableSubject.create();
        TestHelper.checkDisposed(cs1.takeUntil(cs2));
    }

    @Test
    public void mainErrorLate() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Completable() {
                @Override
                protected void subscribeActual(CompletableObserver observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onError(new TestException());
                }
            }.takeUntil(Completable.complete()).test().assertResult();
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void mainCompleteLate() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            new Completable() {
                @Override
                protected void subscribeActual(CompletableObserver observer) {
                    observer.onSubscribe(Disposables.empty());
                    observer.onComplete();
                }
            }.takeUntil(Completable.complete()).test().assertResult();
            Assert.assertTrue(errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void otherErrorLate() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final AtomicReference<CompletableObserver> ref = new AtomicReference<CompletableObserver>();
            Completable.complete().takeUntil(new Completable() {
                @Override
                protected void subscribeActual(CompletableObserver observer) {
                    observer.onSubscribe(Disposables.empty());
                    ref.set(observer);
                }
            }).test().assertResult();
            ref.get().onError(new TestException());
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void otherCompleteLate() {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final AtomicReference<CompletableObserver> ref = new AtomicReference<CompletableObserver>();
            Completable.complete().takeUntil(new Completable() {
                @Override
                protected void subscribeActual(CompletableObserver observer) {
                    observer.onSubscribe(Disposables.empty());
                    ref.set(observer);
                }
            }).test().assertResult();
            ref.get().onComplete();
            Assert.assertTrue(errors.isEmpty());
        } finally {
            RxJavaPlugins.reset();
        }
    }
}

