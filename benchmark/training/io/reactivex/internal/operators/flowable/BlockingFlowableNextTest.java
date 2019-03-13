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
package io.reactivex.internal.operators.flowable;


import io.reactivex.TestHelper;
import io.reactivex.disposables.SerialDisposable;
import io.reactivex.exceptions.TestException;
import io.reactivex.internal.operators.flowable.BlockingFlowableNext.NextSubscriber;
import io.reactivex.internal.subscriptions.BooleanSubscription;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;


public class BlockingFlowableNextTest {
    @Test
    public void testNext() {
        FlowableProcessor<String> obs = PublishProcessor.create();
        Iterator<String> it = obs.blockingNext().iterator();
        fireOnNextInNewThread(obs, "one");
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("one", it.next());
        fireOnNextInNewThread(obs, "two");
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("two", it.next());
        fireOnNextInNewThread(obs, "three");
        try {
            Assert.assertEquals("three", it.next());
        } catch (NoSuchElementException e) {
            Assert.fail("Calling next() without hasNext() should wait for next fire");
        }
        obs.onComplete();
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("At the end of an iterator should throw a NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
        // If the observable is completed, hasNext always returns false and next always throw a NoSuchElementException.
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("At the end of an iterator should throw a NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void testNextWithError() {
        FlowableProcessor<String> obs = PublishProcessor.create();
        Iterator<String> it = obs.blockingNext().iterator();
        fireOnNextInNewThread(obs, "one");
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("one", it.next());
        fireOnErrorInNewThread(obs);
        try {
            it.hasNext();
            Assert.fail("Expected an TestException");
        } catch (TestException e) {
        }
        assertErrorAfterObservableFail(it);
    }

    @Test
    public void testNextWithEmpty() {
        Flowable<String> obs = Flowable.<String>empty().observeOn(Schedulers.newThread());
        Iterator<String> it = obs.blockingNext().iterator();
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("At the end of an iterator should throw a NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
        // If the observable is completed, hasNext always returns false and next always throw a NoSuchElementException.
        Assert.assertFalse(it.hasNext());
        try {
            it.next();
            Assert.fail("At the end of an iterator should throw a NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void testOnError() throws Throwable {
        FlowableProcessor<String> obs = PublishProcessor.create();
        Iterator<String> it = obs.blockingNext().iterator();
        obs.onError(new TestException());
        try {
            it.hasNext();
            Assert.fail("Expected an TestException");
        } catch (TestException e) {
            // successful
        }
        assertErrorAfterObservableFail(it);
    }

    @Test
    public void testOnErrorInNewThread() {
        FlowableProcessor<String> obs = PublishProcessor.create();
        Iterator<String> it = obs.blockingNext().iterator();
        fireOnErrorInNewThread(obs);
        try {
            it.hasNext();
            Assert.fail("Expected an TestException");
        } catch (TestException e) {
            // successful
        }
        assertErrorAfterObservableFail(it);
    }

    @Test
    public void testNextWithOnlyUsingNextMethod() {
        FlowableProcessor<String> obs = PublishProcessor.create();
        Iterator<String> it = obs.blockingNext().iterator();
        fireOnNextInNewThread(obs, "one");
        Assert.assertEquals("one", it.next());
        fireOnNextInNewThread(obs, "two");
        Assert.assertEquals("two", it.next());
        obs.onComplete();
        try {
            it.next();
            Assert.fail("At the end of an iterator should throw a NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
    }

    @Test
    public void testNextWithCallingHasNextMultipleTimes() {
        FlowableProcessor<String> obs = PublishProcessor.create();
        Iterator<String> it = obs.blockingNext().iterator();
        fireOnNextInNewThread(obs, "one");
        Assert.assertTrue(it.hasNext());
        Assert.assertTrue(it.hasNext());
        Assert.assertTrue(it.hasNext());
        Assert.assertTrue(it.hasNext());
        Assert.assertEquals("one", it.next());
        obs.onComplete();
        try {
            it.next();
            Assert.fail("At the end of an iterator should throw a NoSuchElementException");
        } catch (NoSuchElementException e) {
        }
    }

    /**
     * Confirm that no buffering or blocking of the Observable onNext calls occurs and it just grabs the next emitted value.
     * <p/>
     * This results in output such as => a: 1 b: 2 c: 89
     *
     * @throws Throwable
     * 		some method call is declared throws
     */
    @Test
    public void testNoBufferingOrBlockingOfSequence() throws Throwable {
        int repeat = 0;
        for (; ;) {
            final SerialDisposable task = new SerialDisposable();
            try {
                final CountDownLatch finished = new CountDownLatch(1);
                final int COUNT = 30;
                final CountDownLatch timeHasPassed = new CountDownLatch(COUNT);
                final AtomicBoolean running = new AtomicBoolean(true);
                final AtomicInteger count = new AtomicInteger(0);
                final Flowable<Integer> obs = Flowable.unsafeCreate(new Publisher<Integer>() {
                    @Override
                    public void subscribe(final Subscriber<? super Integer> subscriber) {
                        subscriber.onSubscribe(new BooleanSubscription());
                        task.replace(Schedulers.single().scheduleDirect(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while ((running.get()) && (!(task.isDisposed()))) {
                                        subscriber.onNext(count.incrementAndGet());
                                        timeHasPassed.countDown();
                                    } 
                                    subscriber.onComplete();
                                } catch (Throwable e) {
                                    subscriber.onError(e);
                                } finally {
                                    finished.countDown();
                                }
                            }
                        }));
                    }
                });
                Iterator<Integer> it = obs.blockingNext().iterator();
                Assert.assertTrue(it.hasNext());
                int a = it.next();
                Assert.assertTrue(it.hasNext());
                int b = it.next();
                // we should have a different value
                Assert.assertTrue("a and b should be different", (a != b));
                // wait for some time (if times out we are blocked somewhere so fail ... set very high for very slow, constrained machines)
                timeHasPassed.await(8000, TimeUnit.MILLISECONDS);
                Assert.assertTrue(it.hasNext());
                int c = it.next();
                Assert.assertTrue("c should not just be the next in sequence", (c != (b + 1)));
                Assert.assertTrue(((("expected that c [" + c) + "] is higher than or equal to ") + COUNT), (c >= COUNT));
                Assert.assertTrue(it.hasNext());
                int d = it.next();
                Assert.assertTrue((d > c));
                // shut down the thread
                running.set(false);
                finished.await();
                Assert.assertFalse(it.hasNext());
                System.out.println(((((("a: " + a) + " b: ") + b) + " c: ") + c));
                break;
            } catch (AssertionError ex) {
                if ((++repeat) == 3) {
                    throw ex;
                }
                Thread.sleep(((int) (1000 * (Math.pow(2, (repeat - 1))))));
            } finally {
                task.dispose();
            }
        }
    }

    /* (timeout = 8000) */
    @Test
    public void testSingleSourceManyIterators() throws InterruptedException {
        Flowable<Long> f = Flowable.interval(250, TimeUnit.MILLISECONDS);
        PublishProcessor<Integer> terminal = PublishProcessor.create();
        Flowable<Long> source = f.takeUntil(terminal);
        Iterable<Long> iter = source.blockingNext();
        for (int j = 0; j < 3; j++) {
            BlockingFlowableNext.NextIterator<Long> it = ((BlockingFlowableNext.NextIterator<Long>) (iter.iterator()));
            for (long i = 0; i < 10; i++) {
                Assert.assertEquals(true, it.hasNext());
                Assert.assertEquals((j + "th iteration next"), Long.valueOf(i), it.next());
            }
            terminal.onNext(1);
        }
    }

    @Test
    public void testSynchronousNext() {
        Assert.assertEquals(1, BehaviorProcessor.createDefault(1).take(1).blockingSingle().intValue());
        Assert.assertEquals(2, BehaviorProcessor.createDefault(2).blockingIterable().iterator().next().intValue());
        Assert.assertEquals(3, BehaviorProcessor.createDefault(3).blockingNext().iterator().next().intValue());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void remove() {
        Flowable.never().blockingNext().iterator().remove();
    }

    @Test
    public void interrupt() {
        Iterator<Object> it = Flowable.never().blockingNext().iterator();
        try {
            Thread.currentThread().interrupt();
            it.next();
        } catch (RuntimeException ex) {
            Assert.assertTrue(ex.toString(), ((ex.getCause()) instanceof InterruptedException));
        }
    }

    @Test
    public void nextObserverError() {
        NextSubscriber<Integer> no = new NextSubscriber<Integer>();
        List<Throwable> errors = TestHelper.trackPluginErrors();
        try {
            no.onError(new TestException());
            TestHelper.assertUndeliverable(errors, 0, TestException.class);
        } finally {
            RxJavaPlugins.reset();
        }
    }

    @Test
    public void nextObserverOnNext() throws Exception {
        NextSubscriber<Integer> no = new NextSubscriber<Integer>();
        no.setWaiting();
        no.onNext(Notification.createOnNext(1));
        no.setWaiting();
        no.onNext(Notification.createOnNext(1));
        Assert.assertEquals(1, no.takeNext().getValue().intValue());
    }

    @Test
    public void nextObserverOnCompleteOnNext() throws Exception {
        NextSubscriber<Integer> no = new NextSubscriber<Integer>();
        no.setWaiting();
        no.onNext(Notification.<Integer>createOnComplete());
        no.setWaiting();
        no.onNext(Notification.createOnNext(1));
        Assert.assertTrue(no.takeNext().isOnComplete());
    }
}

