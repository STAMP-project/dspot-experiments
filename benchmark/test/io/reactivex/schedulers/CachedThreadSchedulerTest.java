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
import io.reactivex.internal.schedulers.IoScheduler;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


public class CachedThreadSchedulerTest extends AbstractSchedulerConcurrencyTests {
    /**
     * IO scheduler defaults to using CachedThreadScheduler.
     */
    @Test
    public final void testIOScheduler() {
        Flowable<Integer> f1 = Flowable.just(1, 2, 3, 4, 5);
        Flowable<Integer> f2 = Flowable.just(6, 7, 8, 9, 10);
        Flowable<String> f = Flowable.merge(f1, f2).map(new Function<Integer, String>() {
            @Override
            public String apply(Integer t) {
                Assert.assertTrue(Thread.currentThread().getName().startsWith("RxCachedThreadScheduler"));
                return (("Value_" + t) + "_Thread_") + (Thread.currentThread().getName());
            }
        });
        f.subscribeOn(Schedulers.io()).blockingForEach(new Consumer<String>() {
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
        Worker w = Schedulers.io().createWorker();
        try {
            ExecutorSchedulerTest.testCancelledRetention(w, false);
        } finally {
            w.dispose();
        }
        w = Schedulers.io().createWorker();
        try {
            ExecutorSchedulerTest.testCancelledRetention(w, true);
        } finally {
            w.dispose();
        }
    }

    @Test
    public void workerDisposed() {
        Worker w = Schedulers.io().createWorker();
        Assert.assertFalse(isDisposed());
        w.dispose();
        Assert.assertTrue(isDisposed());
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
        IoScheduler s = new IoScheduler();
        s.shutdown();
        s.shutdown();
        s.scheduleDirect(r);
        s.scheduleDirect(r, 1, TimeUnit.SECONDS);
        s.schedulePeriodicallyDirect(r, 1, 1, TimeUnit.SECONDS);
        Worker w = s.createWorker();
        w.dispose();
        Assert.assertEquals(Disposables.disposed(), w.schedule(r));
        Assert.assertEquals(Disposables.disposed(), w.schedule(r, 1, TimeUnit.SECONDS));
        Assert.assertEquals(Disposables.disposed(), w.schedulePeriodically(r, 1, 1, TimeUnit.SECONDS));
        Assert.assertEquals(0, calls[0]);
    }
}

