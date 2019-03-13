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
package io.reactivex.internal.operators.maybe;


import io.reactivex.TestHelper;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class MaybeFromCallableTest {
    @Test(expected = NullPointerException.class)
    public void fromCallableNull() {
        Maybe.fromCallable(null);
    }

    @Test
    public void fromCallable() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Maybe.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                atomicInteger.incrementAndGet();
                return null;
            }
        }).test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
    }

    @Test
    public void fromCallableTwice() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Callable<Object> callable = new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                atomicInteger.incrementAndGet();
                return null;
            }
        };
        Maybe.fromCallable(callable).test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
        Maybe.fromCallable(callable).test().assertResult();
        Assert.assertEquals(2, atomicInteger.get());
    }

    @Test
    public void fromCallableInvokesLazy() {
        final AtomicInteger atomicInteger = new AtomicInteger();
        Maybe<Object> completable = Maybe.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                atomicInteger.incrementAndGet();
                return null;
            }
        });
        Assert.assertEquals(0, atomicInteger.get());
        completable.test().assertResult();
        Assert.assertEquals(1, atomicInteger.get());
    }

    @Test
    public void fromCallableThrows() {
        Maybe.fromCallable(new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                throw new UnsupportedOperationException();
            }
        }).test().assertFailure(UnsupportedOperationException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void callable() throws Exception {
        final int[] counter = new int[]{ 0 };
        Maybe<Integer> m = Maybe.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                (counter[0])++;
                return 0;
            }
        });
        Assert.assertTrue(m.getClass().toString(), (m instanceof Callable));
        Assert.assertEquals(0, ((Callable<Void>) (m)).call());
        Assert.assertEquals(1, counter[0]);
    }

    @Test
    public void noErrorLoss() throws Exception {
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            final CountDownLatch cdl1 = new CountDownLatch(1);
            final CountDownLatch cdl2 = new CountDownLatch(1);
            TestObserver<Integer> to = Maybe.fromCallable(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    cdl1.countDown();
                    cdl2.await(5, TimeUnit.SECONDS);
                    return 1;
                }
            }).subscribeOn(Schedulers.single()).test();
            Assert.assertTrue(cdl1.await(5, TimeUnit.SECONDS));
            to.cancel();
            int timeout = 10;
            while (((timeout--) > 0) && (errors.isEmpty())) {
                Thread.sleep(100);
            } 
            TestHelper.assertUndeliverable(errors, 0, InterruptedException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldNotDeliverResultIfSubscriberUnsubscribedBeforeEmission() throws Exception {
        Callable<String> func = Mockito.mock(Callable.class);
        final CountDownLatch funcLatch = new CountDownLatch(1);
        final CountDownLatch observerLatch = new CountDownLatch(1);
        Mockito.when(func.call()).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                observerLatch.countDown();
                try {
                    funcLatch.await();
                } catch (InterruptedException e) {
                    // It's okay, unsubscription causes Thread interruption
                    // Restoring interruption status of the Thread
                    Thread.currentThread().interrupt();
                }
                return "should_not_be_delivered";
            }
        });
        Maybe<String> fromCallableObservable = Maybe.fromCallable(func);
        Observer<Object> observer = mockObserver();
        TestObserver<String> outer = new TestObserver<String>(observer);
        fromCallableObservable.subscribeOn(Schedulers.computation()).subscribe(outer);
        // Wait until func will be invoked
        observerLatch.await();
        // Unsubscribing before emission
        outer.cancel();
        // Emitting result
        funcLatch.countDown();
        // func must be invoked
        Mockito.verify(func).call();
        // Observer must not be notified at all
        Mockito.verify(observer).onSubscribe(ArgumentMatchers.any(Disposable.class));
        Mockito.verifyNoMoreInteractions(observer);
    }
}

