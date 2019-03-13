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
package io.reactivex.internal.schedulers;


import ComputationScheduler.NONE;
import ComputationScheduler.SHUTDOWN_WORKER;
import io.reactivex.Scheduler.Worker;
import io.reactivex.TestHelper;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.internal.schedulers.SchedulerMultiWorkerSupport.WorkerCallback;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;

import static ComputationScheduler.MAX_THREADS;


public class SchedulerMultiWorkerSupportTest {
    final int max = MAX_THREADS;

    @Test
    public void moreThanMaxWorkers() {
        final List<Worker> list = new ArrayList<Worker>();
        SchedulerMultiWorkerSupport mws = ((SchedulerMultiWorkerSupport) (Schedulers.computation()));
        mws.createWorkers(((max) * 2), new WorkerCallback() {
            @Override
            public void onWorker(int i, Worker w) {
                list.add(w);
            }
        });
        Assert.assertEquals(((max) * 2), list.size());
    }

    @Test
    public void getShutdownWorkers() {
        final List<Worker> list = new ArrayList<Worker>();
        NONE.createWorkers(((max) * 2), new WorkerCallback() {
            @Override
            public void onWorker(int i, Worker w) {
                list.add(w);
            }
        });
        Assert.assertEquals(((max) * 2), list.size());
        for (Worker w : list) {
            Assert.assertEquals(SHUTDOWN_WORKER, w);
        }
    }

    @Test
    public void distinctThreads() throws Exception {
        for (int i = 0; i < (TestHelper.RACE_DEFAULT_LOOPS); i++) {
            final CompositeDisposable composite = new CompositeDisposable();
            try {
                final CountDownLatch cdl = new CountDownLatch(((max) * 2));
                final Set<String> threads1 = Collections.synchronizedSet(new HashSet<String>());
                final Set<String> threads2 = Collections.synchronizedSet(new HashSet<String>());
                Runnable parallel1 = new Runnable() {
                    @Override
                    public void run() {
                        final List<Worker> list1 = new ArrayList<Worker>();
                        SchedulerMultiWorkerSupport mws = ((SchedulerMultiWorkerSupport) (Schedulers.computation()));
                        mws.createWorkers(max, new WorkerCallback() {
                            @Override
                            public void onWorker(int i, Worker w) {
                                list1.add(w);
                                composite.add(w);
                            }
                        });
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                threads1.add(Thread.currentThread().getName());
                                cdl.countDown();
                            }
                        };
                        for (Worker w : list1) {
                            w.schedule(run);
                        }
                    }
                };
                Runnable parallel2 = new Runnable() {
                    @Override
                    public void run() {
                        final List<Worker> list2 = new ArrayList<Worker>();
                        SchedulerMultiWorkerSupport mws = ((SchedulerMultiWorkerSupport) (Schedulers.computation()));
                        mws.createWorkers(max, new WorkerCallback() {
                            @Override
                            public void onWorker(int i, Worker w) {
                                list2.add(w);
                                composite.add(w);
                            }
                        });
                        Runnable run = new Runnable() {
                            @Override
                            public void run() {
                                threads2.add(Thread.currentThread().getName());
                                cdl.countDown();
                            }
                        };
                        for (Worker w : list2) {
                            w.schedule(run);
                        }
                    }
                };
                TestHelper.race(parallel1, parallel2);
                Assert.assertTrue(cdl.await(5, TimeUnit.SECONDS));
                Assert.assertEquals(threads1.toString(), max, threads1.size());
                Assert.assertEquals(threads2.toString(), max, threads2.size());
            } finally {
                composite.dispose();
            }
        }
    }
}

