/**
 * *  Copyright 2010-2017 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.common.concur.executors;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Sitnikov
 */
public class SubExecutorRandomizedTest {
    private static final int TASKS = 3000;

    private static final int TIME = 1500;

    private static final int CORES = Runtime.getRuntime().availableProcessors();

    private ScheduledThreadPoolExecutor executor;

    private ScheduledExecutorService subExecutor;

    private Random random;

    @Test
    public void test() throws InterruptedException, ExecutionException {
        final AtomicLong runs = new AtomicLong(0);
        final AtomicLong delayedRuns = new AtomicLong(0);
        final AtomicLong periodicRuns = new AtomicLong(0);
        long expectedRuns = 0;
        long expectedDelayedRuns = 0;
        long expectedPeriodicRuns = 0;
        for (int i = 0; i < (SubExecutorRandomizedTest.TASKS); ++i)
            switch (random.nextInt(11)) {
                case 0 :
                    subExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            runs.incrementAndGet();
                        }
                    });
                    ++expectedRuns;
                    break;
                case 1 :
                    subExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            runs.incrementAndGet();
                        }
                    });
                    ++expectedRuns;
                    break;
                case 2 :
                    subExecutor.submit(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            runs.incrementAndGet();
                            return null;
                        }
                    });
                    ++expectedRuns;
                    break;
                case 3 :
                    subExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            runs.incrementAndGet();
                        }
                    }, null);
                    ++expectedRuns;
                    break;
                case 4 :
                    subExecutor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            delayedRuns.incrementAndGet();
                        }
                    }, random.nextInt(SubExecutorRandomizedTest.TIME), TimeUnit.MILLISECONDS);
                    ++expectedDelayedRuns;
                    break;
                case 5 :
                    subExecutor.schedule(new Callable<Object>() {
                        @Override
                        public Object call() throws Exception {
                            delayedRuns.incrementAndGet();
                            return null;
                        }
                    }, random.nextInt(SubExecutorRandomizedTest.TIME), TimeUnit.MILLISECONDS);
                    ++expectedDelayedRuns;
                    break;
                case 6 :
                    {
                        final long delay = random.nextInt(SubExecutorRandomizedTest.TIME);
                        final long period = 1 + (random.nextInt(SubExecutorRandomizedTest.TIME));
                        subExecutor.scheduleAtFixedRate(new Runnable() {
                            @Override
                            public void run() {
                                periodicRuns.incrementAndGet();
                            }
                        }, delay, period, TimeUnit.MILLISECONDS);
                        expectedPeriodicRuns += 1 + (((SubExecutorRandomizedTest.TIME) - delay) / period);
                    }
                    break;
                case 7 :
                    {
                        final long delay = random.nextInt(SubExecutorRandomizedTest.TIME);
                        final long period = 1 + (random.nextInt(SubExecutorRandomizedTest.TIME));
                        subExecutor.scheduleWithFixedDelay(new Runnable() {
                            @Override
                            public void run() {
                                periodicRuns.incrementAndGet();
                            }
                        }, delay, period, TimeUnit.MILLISECONDS);
                        expectedPeriodicRuns += 1 + (((SubExecutorRandomizedTest.TIME) - delay) / period);
                    }
                    break;
                case 8 :
                    subExecutor.submit(new Runnable() {
                        @Override
                        public void run() {
                            runs.incrementAndGet();
                            subExecutor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    runs.incrementAndGet();
                                }
                            });
                        }
                    });
                    expectedRuns += 2;
                    break;
                case 9 :
                    subExecutor.schedule(new Runnable() {
                        @Override
                        public void run() {
                            delayedRuns.incrementAndGet();
                            subExecutor.schedule(new Runnable() {
                                @Override
                                public void run() {
                                    delayedRuns.incrementAndGet();
                                }
                            }, random.nextInt(((SubExecutorRandomizedTest.TIME) / 2)), TimeUnit.MILLISECONDS);
                        }
                    }, random.nextInt(((SubExecutorRandomizedTest.TIME) / 2)), TimeUnit.MILLISECONDS);
                    expectedDelayedRuns += 2;
                    break;
                case 10 :
                    {
                        final long delay = random.nextInt(((SubExecutorRandomizedTest.TIME) / 2));
                        final long subDelay = random.nextInt(((SubExecutorRandomizedTest.TIME) / 2));
                        final long subPeriod = 1 + (random.nextInt(((SubExecutorRandomizedTest.TIME) / 2)));
                        subExecutor.schedule(new Runnable() {
                            @Override
                            public void run() {
                                delayedRuns.incrementAndGet();
                                subExecutor.scheduleAtFixedRate(new Runnable() {
                                    @Override
                                    public void run() {
                                        periodicRuns.incrementAndGet();
                                    }
                                }, subDelay, subPeriod, TimeUnit.MILLISECONDS);
                            }
                        }, delay, TimeUnit.MILLISECONDS);
                        ++expectedDelayedRuns;
                        expectedPeriodicRuns += 1 + ((((SubExecutorRandomizedTest.TIME) - delay) - subDelay) / subPeriod);
                    }
                    break;
            }

        Thread.sleep(SubExecutorRandomizedTest.TIME);
        final Set<Future<Boolean>> futures = new HashSet<Future<Boolean>>();
        // it's ok here
        for (int i = 0; i < (SubExecutorRandomizedTest.CORES); ++i)
            try {
                futures.add(subExecutor.submit(new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        subExecutor.shutdown();
                        return true;
                    }
                }));
            } catch (RejectedExecutionException e) {
            }

        subExecutor.shutdown();
        Assert.assertTrue(subExecutor.awaitTermination(SubExecutorRandomizedTest.TIME, TimeUnit.MILLISECONDS));
        Assert.assertTrue(subExecutor.isTerminated());
        for (Future<Boolean> future : futures)
            Assert.assertTrue(future.get());

        final long runsSnapshot = runs.get();
        final long delayedRunsSnapshot = delayedRuns.get();
        final long periodicRunsSnapshot = periodicRuns.get();
        Assert.assertEquals(expectedRuns, runsSnapshot);
        Assert.assertEquals(expectedDelayedRuns, delayedRunsSnapshot);
        Assert.assertTrue(SubExecutorRandomizedTest.fuzzyGreater(periodicRunsSnapshot, expectedPeriodicRuns));
        Assert.assertTrue(SubExecutorRandomizedTest.fuzzyLess(periodicRunsSnapshot, (expectedPeriodicRuns * 2)));
        Thread.sleep(((SubExecutorRandomizedTest.TIME) / 5));
        Assert.assertEquals(runsSnapshot, runs.get());
        Assert.assertEquals(delayedRunsSnapshot, delayedRuns.get());
        Assert.assertEquals(periodicRunsSnapshot, periodicRuns.get());
    }
}

