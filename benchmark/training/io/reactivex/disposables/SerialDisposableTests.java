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
package io.reactivex.disposables;


import DisposableHelper.DISPOSED;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class SerialDisposableTests {
    private SerialDisposable serialDisposable;

    @Test
    public void unsubscribingWithoutUnderlyingDoesNothing() {
        serialDisposable.dispose();
    }

    @Test
    public void getDisposableShouldReturnset() {
        final Disposable underlying = Mockito.mock(Disposable.class);
        serialDisposable.set(underlying);
        Assert.assertSame(underlying, serialDisposable.get());
        final Disposable another = Mockito.mock(Disposable.class);
        serialDisposable.set(another);
        Assert.assertSame(another, serialDisposable.get());
    }

    @Test
    public void notDisposedWhenReplaced() {
        final Disposable underlying = Mockito.mock(Disposable.class);
        serialDisposable.set(underlying);
        serialDisposable.replace(Disposables.empty());
        serialDisposable.dispose();
        Mockito.verify(underlying, Mockito.never()).dispose();
    }

    @Test
    public void unsubscribingTwiceDoesUnsubscribeOnce() {
        Disposable underlying = Mockito.mock(Disposable.class);
        serialDisposable.set(underlying);
        serialDisposable.dispose();
        Mockito.verify(underlying).dispose();
        serialDisposable.dispose();
        Mockito.verifyNoMoreInteractions(underlying);
    }

    @Test
    public void settingSameDisposableTwiceDoesUnsubscribeIt() {
        Disposable underlying = Mockito.mock(Disposable.class);
        serialDisposable.set(underlying);
        Mockito.verifyZeroInteractions(underlying);
        serialDisposable.set(underlying);
        Mockito.verify(underlying).dispose();
    }

    @Test
    public void unsubscribingWithSingleUnderlyingUnsubscribes() {
        Disposable underlying = Mockito.mock(Disposable.class);
        serialDisposable.set(underlying);
        underlying.dispose();
        Mockito.verify(underlying).dispose();
    }

    @Test
    public void replacingFirstUnderlyingCausesUnsubscription() {
        Disposable first = Mockito.mock(Disposable.class);
        serialDisposable.set(first);
        Disposable second = Mockito.mock(Disposable.class);
        serialDisposable.set(second);
        Mockito.verify(first).dispose();
    }

    @Test
    public void whenUnsubscribingSecondUnderlyingUnsubscribed() {
        Disposable first = Mockito.mock(Disposable.class);
        serialDisposable.set(first);
        Disposable second = Mockito.mock(Disposable.class);
        serialDisposable.set(second);
        serialDisposable.dispose();
        Mockito.verify(second).dispose();
    }

    @Test
    public void settingUnderlyingWhenUnsubscribedCausesImmediateUnsubscription() {
        serialDisposable.dispose();
        Disposable underlying = Mockito.mock(Disposable.class);
        serialDisposable.set(underlying);
        Mockito.verify(underlying).dispose();
    }

    @Test(timeout = 1000)
    public void settingUnderlyingWhenUnsubscribedCausesImmediateUnsubscriptionConcurrently() throws InterruptedException {
        final Disposable firstSet = Mockito.mock(Disposable.class);
        serialDisposable.set(firstSet);
        final CountDownLatch start = new CountDownLatch(1);
        final int count = 10;
        final CountDownLatch end = new CountDownLatch(count);
        final List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < count; i++) {
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        start.await();
                        serialDisposable.dispose();
                    } catch (InterruptedException e) {
                        Assert.fail(e.getMessage());
                    } finally {
                        end.countDown();
                    }
                }
            };
            t.start();
            threads.add(t);
        }
        final Disposable underlying = Mockito.mock(Disposable.class);
        start.countDown();
        serialDisposable.set(underlying);
        end.await();
        Mockito.verify(firstSet).dispose();
        Mockito.verify(underlying).dispose();
        for (final Thread t : threads) {
            t.join();
        }
    }

    @Test
    public void concurrentSetDisposableShouldNotInterleave() throws InterruptedException {
        final int count = 10;
        final List<Disposable> subscriptions = new ArrayList<Disposable>();
        final CountDownLatch start = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(count);
        final List<Thread> threads = new ArrayList<Thread>();
        for (int i = 0; i < count; i++) {
            final Disposable subscription = Mockito.mock(Disposable.class);
            subscriptions.add(subscription);
            final Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        start.await();
                        serialDisposable.set(subscription);
                    } catch (InterruptedException e) {
                        Assert.fail(e.getMessage());
                    } finally {
                        end.countDown();
                    }
                }
            };
            t.start();
            threads.add(t);
        }
        start.countDown();
        end.await();
        serialDisposable.dispose();
        for (final Disposable subscription : subscriptions) {
            Mockito.verify(subscription).dispose();
        }
        for (final Thread t : threads) {
            t.join();
        }
    }

    @Test
    public void disposeState() {
        Disposable empty = Disposables.empty();
        SerialDisposable d = new SerialDisposable(empty);
        Assert.assertFalse(d.isDisposed());
        Assert.assertSame(empty, d.get());
        d.dispose();
        Assert.assertTrue(d.isDisposed());
        Assert.assertNotSame(empty, d.get());
        Assert.assertNotSame(DISPOSED, d.get());
    }
}

