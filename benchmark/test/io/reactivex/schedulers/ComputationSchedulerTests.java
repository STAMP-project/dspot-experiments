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
package io.reactivex.schedulers;


import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposables;
import io.reactivex.internal.schedulers.ComputationScheduler;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class ComputationSchedulerTests extends AbstractSchedulerConcurrencyTests {
    @Test
    public void testThreadSafetyWhenSchedulerIsHoppingBetweenThreads() {
        final int NUM = 1000000;
        final CountDownLatch latch = new CountDownLatch(1);
        final HashMap<String, Integer> map = new HashMap<String, Integer>();
        final Scheduler.Worker inner = Schedulers.computation().createWorker();
        try {
            inner.schedule(new Runnable() {
                private HashMap<String, Integer> statefulMap = map;

                int nonThreadSafeCounter;

                @Override
                public void run() {
                    Integer i = statefulMap.get("a");
                    if (i == null) {
                        i = 1;
                        statefulMap.put("a", i);
                        statefulMap.put("b", i);
                    } else {
                        i++;
                        statefulMap.put("a", i);
                        statefulMap.put("b", i);
                    }
                    (nonThreadSafeCounter)++;
                    statefulMap.put("nonThreadSafeCounter", nonThreadSafeCounter);
                    if (i < NUM) {
                        inner.schedule(this);
                    } else {
                        latch.countDown();
                    }
                }
            });
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(("Count A: " + (map.get("a"))));
            System.out.println(("Count B: " + (map.get("b"))));
            System.out.println(("nonThreadSafeCounter: " + (map.get("nonThreadSafeCounter"))));
            Assert.assertEquals(NUM, map.get("a").intValue());
            Assert.assertEquals(NUM, map.get("b").intValue());
            Assert.assertEquals(NUM, map.get("nonThreadSafeCounter").intValue());
        } finally {
            inner.dispose();
        }
    }

    @Test
    public final void testComputationThreadPool1() {
        Flowable<Integer> f1 = Flowable.<Integer>just(1, 2, 3, 4, 5);
        Flowable<Integer> f2 = Flowable.<Integer>just(6, 7, 8, 9, 10);
        Flowable<String> f = Flowable.<Integer>merge(f1, f2).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer t) {
                Assert.assertTrue(Thread.currentThread().getName().startsWith("RxComputationThreadPool"));
                return (("Value_" + t) + "_Thread_") + (Thread.currentThread().getName());
            }
        });
        f.subscribeOn(Schedulers.computation()).blockingForEach(new Consumer<String>() {
            @Override
            public void accept(String t) {
                System.out.println(("t: " + t));
            }
        });
    }

    @Test
    public final void testMergeWithExecutorScheduler() {
        final String currentThreadName = Thread.currentThread().getName();
        Flowable<Integer> f1 = Flowable.<Integer>just(1, 2, 3, 4, 5);
        Flowable<Integer> f2 = Flowable.<Integer>just(6, 7, 8, 9, 10);
        Flowable<String> f = Flowable.<Integer>merge(f1, f2).subscribeOn(Schedulers.computation()).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer t) {
                Assert.assertFalse(Thread.currentThread().getName().equals(currentThreadName));
                Assert.assertTrue(Thread.currentThread().getName().startsWith("RxComputationThreadPool"));
                return (("Value_" + t) + "_Thread_") + (Thread.currentThread().getName());
            }
        });
        f.blockingForEach(new Consumer<String>() {
            @Override
            public void accept(String t) {
                System.out.println(("t: " + t));
            }
        });
    }

    @Test
    public final void testHandledErrorIsNotDeliveredToThreadHandler() throws InterruptedException {
        SchedulerTestHelper.testHandledErrorIsNotDeliveredToThreadHandler(getScheduler());
    }

    @Test(timeout = 60000)
    public void testCancelledTaskRetention() throws InterruptedException {
        Worker w = Schedulers.computation().createWorker();
        try {
            ExecutorSchedulerTest.testCancelledRetention(w, false);
        } finally {
            w.dispose();
        }
        w = Schedulers.computation().createWorker();
        try {
            ExecutorSchedulerTest.testCancelledRetention(w, true);
        } finally {
            w.dispose();
        }
    }

    @Test
    public void shutdownRejects() {
        final int[] calls = new int[]{ 0 };
        Runnable r = new Runnable() {
            @Override
            public void run() {
                (calls[0])++;
            }
        };
        Scheduler s = new ComputationScheduler();
        s.shutdown();
        s.shutdown();
        Assert.assertEquals(Disposables.disposed(), s.scheduleDirect(r));
        Assert.assertEquals(Disposables.disposed(), s.scheduleDirect(r, 1, TimeUnit.SECONDS));
        Assert.assertEquals(Disposables.disposed(), s.schedulePeriodicallyDirect(r, 1, 1, TimeUnit.SECONDS));
        Worker w = s.createWorker();
        w.dispose();
        Assert.assertTrue(w.isDisposed());
        Assert.assertEquals(Disposables.disposed(), w.schedule(r));
        Assert.assertEquals(Disposables.disposed(), w.schedule(r, 1, TimeUnit.SECONDS));
        Assert.assertEquals(Disposables.disposed(), w.schedulePeriodically(r, 1, 1, TimeUnit.SECONDS));
        Assert.assertEquals(0, calls[0]);
    }
}

