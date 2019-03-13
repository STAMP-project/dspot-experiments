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


import EmptyDisposable.INSTANCE;
import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import org.junit.Assert;
import org.junit.Test;


public class CompletableCacheTest implements Action , Consumer<Object> {
    volatile int count;

    @Test
    public void normal() {
        Completable c = Completable.complete().doOnSubscribe(this).cache();
        Assert.assertEquals(0, count);
        c.test().assertResult();
        Assert.assertEquals(1, count);
        c.test().assertResult();
        Assert.assertEquals(1, count);
        c.test().assertResult();
        Assert.assertEquals(1, count);
    }

    @Test
    public void error() {
        Completable c = Completable.error(new TestException()).doOnSubscribe(this).cache();
        Assert.assertEquals(0, count);
        c.test().assertFailure(TestException.class);
        Assert.assertEquals(1, count);
        c.test().assertFailure(TestException.class);
        Assert.assertEquals(1, count);
        c.test().assertFailure(TestException.class);
        Assert.assertEquals(1, count);
    }

    @Test
    public void crossDispose() {
        PublishSubject<Integer> ps = PublishSubject.create();
        final TestObserver<Void> to1 = new TestObserver<Void>();
        final TestObserver<Void> to2 = new TestObserver<Void>() {
            @Override
            public void onComplete() {
                super.onComplete();
                to1.cancel();
            }
        };
        Completable c = ps.ignoreElements().cache();
        c.subscribe(to2);
        c.subscribe(to1);
        ps.onComplete();
        to1.assertEmpty();
        to2.assertResult();
    }

    @Test
    public void crossDisposeOnError() {
        PublishSubject<Integer> ps = PublishSubject.create();
        final TestObserver<Void> to1 = new TestObserver<Void>();
        final TestObserver<Void> to2 = new TestObserver<Void>() {
            @Override
            public void onError(Throwable ex) {
                super.onError(ex);
                to1.cancel();
            }
        };
        Completable c = ps.ignoreElements().cache();
        c.subscribe(to2);
        c.subscribe(to1);
        ps.onError(new TestException());
        to1.assertEmpty();
        to2.assertFailure(TestException.class);
    }

    @Test
    public void dispose() {
        PublishSubject<Integer> ps = PublishSubject.create();
        Completable c = ps.ignoreElements().cache();
        Assert.assertFalse(ps.hasObservers());
        TestObserver<Void> to1 = c.test();
        Assert.assertTrue(ps.hasObservers());
        to1.cancel();
        Assert.assertTrue(ps.hasObservers());
        TestObserver<Void> to2 = c.test();
        TestObserver<Void> to3 = c.test();
        to3.cancel();
        TestObserver<Void> to4 = c.test(true);
        to3.cancel();
        ps.onComplete();
        to1.assertEmpty();
        to2.assertResult();
        to3.assertEmpty();
        to4.assertEmpty();
    }

    @Test
    public void subscribeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            PublishSubject<Integer> ps = PublishSubject.create();
            final Completable c = ps.ignoreElements().cache();
            final TestObserver<Void> to1 = new TestObserver<Void>();
            final TestObserver<Void> to2 = new TestObserver<Void>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    c.subscribe(to1);
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    c.subscribe(to2);
                }
            };
            TestHelper.race(r1, r2);
            ps.onComplete();
            to1.assertResult();
            to2.assertResult();
        }
    }

    @Test
    public void subscribeDisposeRace() {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            PublishSubject<Integer> ps = PublishSubject.create();
            final Completable c = ps.ignoreElements().cache();
            final TestObserver<Void> to1 = c.test();
            final TestObserver<Void> to2 = new TestObserver<Void>();
            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    to1.cancel();
                }
            };
            Runnable r2 = new Runnable() {
                @Override
                public void run() {
                    c.subscribe(to2);
                }
            };
            TestHelper.race(r1, r2);
            ps.onComplete();
            to1.assertEmpty();
            to2.assertResult();
        }
    }

    @Test
    public void doubleDispose() {
        PublishSubject<Integer> ps = PublishSubject.create();
        final TestObserver<Void> to = new TestObserver<Void>();
        ps.ignoreElements().cache().subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(Disposable d) {
                to.onSubscribe(INSTANCE);
                d.dispose();
                d.dispose();
            }

            @Override
            public void onComplete() {
                to.onComplete();
            }

            @Override
            public void onError(Throwable e) {
                to.onError(e);
            }
        });
        ps.onComplete();
        to.assertEmpty();
    }
}

