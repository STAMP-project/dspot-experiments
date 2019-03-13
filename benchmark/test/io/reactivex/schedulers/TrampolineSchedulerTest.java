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


import Functions.EMPTY_RUNNABLE;
import io.reactivex.Flowable;
import io.reactivex.Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subscribers.TestSubscriber;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;


public class TrampolineSchedulerTest extends AbstractSchedulerTests {
    @Test
    public final void testMergeWithCurrentThreadScheduler1() {
        final String currentThreadName = Thread.currentThread().getName();
        Flowable<Integer> f1 = Flowable.<Integer>just(1, 2, 3, 4, 5);
        Flowable<Integer> f2 = Flowable.<Integer>just(6, 7, 8, 9, 10);
        Flowable<String> f = Flowable.<Integer>merge(f1, f2).subscribeOn(Schedulers.trampoline()).map(new io.reactivex.functions.Function<Integer, String>() {
            @Override
            public String apply(Integer t) {
                Assert.assertTrue(Thread.currentThread().getName().equals(currentThreadName));
                return (("Value_" + t) + "_Thread_") + (Thread.currentThread().getName());
            }
        });
        f.blockingForEach(new io.reactivex.functions.Consumer<String>() {
            @Override
            public void accept(String t) {
                System.out.println(("t: " + t));
            }
        });
    }

    @Test
    public void testNestedTrampolineWithUnsubscribe() {
        final ArrayList<String> workDone = new ArrayList<String>();
        final CompositeDisposable workers = new CompositeDisposable();
        Worker worker = Schedulers.trampoline().createWorker();
        try {
            workers.add(worker);
            worker.schedule(new Runnable() {
                @Override
                public void run() {
                    workers.add(TrampolineSchedulerTest.doWorkOnNewTrampoline("A", workDone));
                }
            });
            final Worker worker2 = Schedulers.trampoline().createWorker();
            workers.add(worker2);
            worker2.schedule(new Runnable() {
                @Override
                public void run() {
                    workers.add(TrampolineSchedulerTest.doWorkOnNewTrampoline("B", workDone));
                    // we unsubscribe worker2 ... it should not affect work scheduled on a separate Trampline.Worker
                    worker2.dispose();
                }
            });
            Assert.assertEquals(6, workDone.size());
            Assert.assertEquals(Arrays.asList("A.1", "A.B.1", "A.B.2", "B.1", "B.B.1", "B.B.2"), workDone);
        } finally {
            workers.dispose();
        }
    }

    /**
     * This is a regression test for #1702. Concurrent work scheduling that is improperly synchronized can cause an
     * action to be added or removed onto the priority queue during a poll, which can result in NPEs during queue
     * sifting. While it is difficult to isolate the issue directly, we can easily trigger the behavior by spamming the
     * trampoline with enqueue requests from multiple threads concurrently.
     */
    @Test
    public void testTrampolineWorkerHandlesConcurrentScheduling() {
        final Worker trampolineWorker = Schedulers.trampoline().createWorker();
        final Subscriber<Object> subscriber = TestHelper.mockSubscriber();
        final TestSubscriber<Disposable> ts = new TestSubscriber<Disposable>(subscriber);
        // Spam the trampoline with actions.
        Flowable.range(0, 50).flatMap(new io.reactivex.functions.Function<Integer, Publisher<Disposable>>() {
            @Override
            public Publisher<Disposable> apply(Integer count) {
                return Flowable.interval(1, TimeUnit.MICROSECONDS).map(new io.reactivex.functions.Function<Long, Disposable>() {
                    @Override
                    public Disposable apply(Long ount1) {
                        return trampolineWorker.schedule(EMPTY_RUNNABLE);
                    }
                }).take(100);
            }
        }).subscribeOn(Schedulers.computation()).subscribe(ts);
        ts.awaitTerminalEvent();
        ts.assertNoErrors();
    }
}

