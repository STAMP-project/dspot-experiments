/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.scheduledexecutor;


import com.hazelcast.config.Config;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.SlowTest;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ SlowTest.class, ParallelTest.class })
public class ScheduledExecutorServiceSlowTest extends ScheduledExecutorServiceTestSupport {
    @Test
    public void schedule_withLongSleepingCallable_blockingOnGet() throws Exception {
        int delay = 0;
        double expectedResult = 169.4;
        HazelcastInstance[] instances = createClusterWithCount(2);
        ICountDownLatch runsCountLatch = instances[0].getCountDownLatch("runsCountLatchName");
        runsCountLatch.trySetCount(1);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> future = executorService.schedule(new ScheduledExecutorServiceTestSupport.ICountdownLatchCallableTask("runsCountLatchName", 15000), delay, TimeUnit.SECONDS);
        double result = future.get();
        Assert.assertEquals(expectedResult, result, 0);
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
    }

    @Test
    public void schedule_withStatefulRunnable_durable() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(4);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        int waitStateSyncPeriodToAvoidPassiveState = 2000;
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        IAtomicLong runC = instances[0].getAtomicLong("runC");
        IAtomicLong loadC = instances[0].getAtomicLong("loadC");
        latch.trySetCount(1);
        IScheduledFuture future = executorService.scheduleOnKeyOwnerAtFixedRate(new ScheduledExecutorServiceTestSupport.StatefulRunnableTask("latch", "runC", "loadC"), key, 10, 10, TimeUnit.SECONDS);
        // wait for task to get scheduled and start
        latch.await(11, TimeUnit.SECONDS);
        Thread.sleep(waitStateSyncPeriodToAvoidPassiveState);
        instances[1].getLifecycleService().shutdown();
        // reset latch - task should be running on a replica now
        latch.trySetCount(7);
        latch.await(70, TimeUnit.SECONDS);
        future.cancel(false);
        Assert.assertEquals(HazelcastTestSupport.getPartitionService(instances[0]).getPartitionId(key), future.getHandler().getPartitionId());
        Assert.assertEquals(8, runC.get(), 1);
        Assert.assertEquals(1, loadC.get());
    }

    @Test
    public void stats_longRunningTask_durable() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(4);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        ICountDownLatch firstLatch = instances[0].getCountDownLatch("firstLatch");
        firstLatch.trySetCount(2);
        ICountDownLatch lastLatch = instances[0].getCountDownLatch("lastLatch");
        lastLatch.trySetCount(6);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture future = executorService.scheduleOnKeyOwnerAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("firstLatch", "lastLatch"), key, 0, 10, TimeUnit.SECONDS);
        firstLatch.await(12, TimeUnit.SECONDS);
        instances[1].getLifecycleService().shutdown();
        lastLatch.await(70, TimeUnit.SECONDS);
        // wait for run-cycle to finish before cancelling, in order for stats to get updated
        HazelcastTestSupport.sleepSeconds(4);
        future.cancel(false);
        ScheduledTaskStatistics stats = future.getStats();
        Assert.assertEquals(6, stats.getTotalRuns(), 1);
    }

    @Test
    public void stats_manyRepetitionsTask() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(4);
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(6);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture future = executorService.scheduleAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("latch"), 0, 10, TimeUnit.SECONDS);
        latch.await(120, TimeUnit.SECONDS);
        future.cancel(false);
        ScheduledTaskStatistics stats = future.getStats();
        Assert.assertEquals(6, stats.getTotalRuns(), 1);
    }

    @Test
    public void scheduleRandomPartitions_getAllScheduled_durable() throws Exception {
        ScheduledExecutorConfig scheduledExecutorConfig = new ScheduledExecutorConfig().setName("s").setDurability(2);
        Config config = // keep the partition count low, makes test faster, and chances of partition loss, less
        new Config().setProperty("hazelcast.partition.count", "10").addScheduledExecutorConfig(scheduledExecutorConfig);
        HazelcastInstance[] instances = createClusterWithCount(4, config);
        IScheduledExecutorService s = getScheduledExecutor(instances, "s");
        int expectedTotal = 11;
        IScheduledFuture[] futures = new IScheduledFuture[expectedTotal];
        for (int i = 0; i < expectedTotal; i++) {
            futures[i] = s.schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(i), 0, TimeUnit.SECONDS);
        }
        instances[1].shutdown();
        Assert.assertEquals(expectedTotal, countScheduledTasksOn(s), 0);
        // verify all tasks
        for (int i = 0; i < expectedTotal; i++) {
            Assert.assertEquals((25.0 + i), futures[i].get());
        }
    }

    @Test
    public void scheduleRandomPartitions_periodicTask_getAllScheduled_durable() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(3);
        IScheduledExecutorService s = getScheduledExecutor(instances, "s");
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        String runsCounterName = "runs";
        ICountDownLatch runsLatch = instances[0].getCountDownLatch(runsCounterName);
        runsLatch.trySetCount(2);
        int expectedTotal = 11;
        for (int i = 0; i < expectedTotal; i++) {
            s.scheduleOnKeyOwnerAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask(runsCounterName), key, 0, 2, TimeUnit.SECONDS);
        }
        runsLatch.await(10, TimeUnit.SECONDS);
        instances[1].getLifecycleService().shutdown();
        Assert.assertEquals(expectedTotal, countScheduledTasksOn(s), 0);
    }

    @Test
    public void schedulePeriodicTask_withMultipleSchedulers_atRandomPartitions_thenGetAllScheduled() throws Exception {
        String runsCounterName = "runs";
        HazelcastInstance[] instances = createClusterWithCount(3);
        ICountDownLatch runsLatch = instances[0].getCountDownLatch(runsCounterName);
        int numOfSchedulers = 10;
        int numOfTasks = 10;
        int expectedTotal = numOfSchedulers * numOfTasks;
        runsLatch.trySetCount(expectedTotal);
        for (int i = 0; i < numOfSchedulers; i++) {
            IScheduledExecutorService s = getScheduledExecutor(instances, ("scheduler_" + i));
            String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
            for (int k = 0; k < numOfTasks; k++) {
                s.scheduleOnKeyOwnerAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask(runsCounterName), key, 0, 2, TimeUnit.SECONDS);
            }
        }
        runsLatch.await(10, TimeUnit.SECONDS);
        int actualTotal = 0;
        for (int i = 0; i < numOfSchedulers; i++) {
            actualTotal += countScheduledTasksOn(getScheduledExecutor(instances, ("scheduler_" + i)));
        }
        Assert.assertEquals(expectedTotal, actualTotal, 0);
    }

    @Test
    public void schedulePeriodicTask_withMultipleSchedulers_atRandomPartitions_shutdownOrDestroy_thenGetAllScheduled() throws Exception {
        String runsCounterName = "runs";
        HazelcastInstance[] instances = createClusterWithCount(3);
        ICountDownLatch runsLatch = instances[0].getCountDownLatch(runsCounterName);
        int numOfSchedulers = 10;
        int numOfTasks = 10;
        int expectedTotal = numOfSchedulers * numOfTasks;
        runsLatch.trySetCount(expectedTotal);
        for (int i = 0; i < numOfSchedulers; i++) {
            IScheduledExecutorService s = getScheduledExecutor(instances, ("scheduler_" + i));
            String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
            for (int k = 0; k < numOfTasks; k++) {
                s.scheduleOnKeyOwnerAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask(runsCounterName), key, 0, 2, TimeUnit.SECONDS);
            }
        }
        runsLatch.await(10, TimeUnit.SECONDS);
        getScheduledExecutor(instances, ("scheduler_" + 0)).shutdown();
        getScheduledExecutor(instances, ("scheduler_" + 1)).shutdown();
        getScheduledExecutor(instances, ("scheduler_" + 3)).destroy();
        int actualTotal = 0;
        for (int i = 0; i < numOfSchedulers; i++) {
            actualTotal += countScheduledTasksOn(getScheduledExecutor(instances, ("scheduler_" + i)));
        }
        Assert.assertEquals((expectedTotal - (3 * numOfTasks)), actualTotal, 0);
    }

    @Test
    public void schedulePeriodicTask_withMultipleSchedulers_atRandomPartitions_killMember_thenGetAllScheduled() throws Exception {
        String runsCounterName = "runs";
        HazelcastInstance[] instances = createClusterWithCount(10);
        ICountDownLatch runsLatch = instances[0].getCountDownLatch(runsCounterName);
        int numOfSchedulers = 20;
        int numOfTasks = 10;
        int expectedTotal = numOfSchedulers * numOfTasks;
        runsLatch.trySetCount(expectedTotal);
        for (int i = 0; i < numOfSchedulers; i++) {
            IScheduledExecutorService s = getScheduledExecutor(instances, ("scheduler_" + i));
            String key = HazelcastTestSupport.generateKeyOwnedBy(instances[(i % (instances.length))]);
            for (int k = 0; k < numOfTasks; k++) {
                s.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask(runsCounterName), key, 0, TimeUnit.SECONDS);
            }
        }
        runsLatch.await(10, TimeUnit.SECONDS);
        instances[1].getLifecycleService().terminate();
        int actualTotal = 0;
        for (int i = 0; i < numOfSchedulers; i++) {
            actualTotal += countScheduledTasksOn(getScheduledExecutor(instances, ("scheduler_" + i)));
        }
        Assert.assertEquals(expectedTotal, actualTotal, 0);
    }

    @Test
    public void cancelUninterruptedTask_waitUntilRunCompleted_checkStatusIsCancelled() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(1);
        String runFinishedLatchName = "runFinishedLatch";
        ICountDownLatch latch = instances[0].getCountDownLatch(runFinishedLatchName);
        latch.trySetCount(1);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture future = executorService.scheduleAtFixedRate(new ScheduledExecutorServiceTestSupport.HotLoopBusyTask(runFinishedLatchName), 0, 1, TimeUnit.SECONDS);
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        future.cancel(false);
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
        // wait till the task is actually done, since even though we cancelled the task is current task is still running
        latch.await(60, TimeUnit.SECONDS);
        // make sure SyncState goes through
        HazelcastTestSupport.sleepSeconds(10);
        // check once more that the task status is consistent
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void cancelUninterruptedTask_waitUntilRunCompleted_killMember_checkStatusIsCancelled() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(2);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        String runFinishedLatchName = "runFinishedLatch";
        ICountDownLatch latch = instances[0].getCountDownLatch(runFinishedLatchName);
        latch.trySetCount(1);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture future = executorService.scheduleOnKeyOwnerAtFixedRate(new ScheduledExecutorServiceTestSupport.HotLoopBusyTask(runFinishedLatchName), key, 0, 1, TimeUnit.SECONDS);
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        future.cancel(false);
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
        // wait till the task is actually done, since even though we cancelled the task is current task is still running
        latch.await(60, TimeUnit.SECONDS);
        // make sure SyncState goes through
        HazelcastTestSupport.sleepSeconds(10);
        instances[1].getLifecycleService().terminate();
        // check once more that the task status is consistent
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void reschedulingAfterMigration_whenCurrentNodePreviouslyOwnedTask() {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory();
        HazelcastInstance first = factory.newHazelcastInstance();
        int tasksCount = 1000;
        final IScheduledExecutorService scheduler = first.getScheduledExecutorService("scheduler");
        for (int i = 1; i <= tasksCount; i++) {
            scheduler.scheduleAtFixedRate(TaskUtils.named(String.valueOf(i), new ScheduledExecutorServiceTestSupport.EchoTask()), 5, 10, TimeUnit.SECONDS);
        }
        HazelcastTestSupport.assertTrueEventually(new ScheduledExecutorServiceTestSupport.AllTasksRunningWithinNumOfNodes(scheduler, 1));
        // start a second member
        HazelcastInstance second = factory.newHazelcastInstance();
        HazelcastTestSupport.waitAllForSafeState(first, second);
        HazelcastTestSupport.assertTrueEventually(new ScheduledExecutorServiceTestSupport.AllTasksRunningWithinNumOfNodes(scheduler, 2));
        // kill the second member, tasks should now get rescheduled back in first member
        second.getLifecycleService().terminate();
        HazelcastTestSupport.waitAllForSafeState(first);
        HazelcastTestSupport.assertTrueEventually(new ScheduledExecutorServiceTestSupport.AllTasksRunningWithinNumOfNodes(scheduler, 1));
    }

    @Test(timeout = 1800000)
    public void schedule_thenDisposeLeakTest() {
        Config config = new Config().addScheduledExecutorConfig(new ScheduledExecutorConfig().setName("s").setCapacity(10000));
        HazelcastInstance[] instances = createClusterWithCount(2, config);
        final IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        final AtomicBoolean running = new AtomicBoolean(true);
        long counter = 0;
        long limit = 2000000;
        Executors.newSingleThreadExecutor().submit(new Runnable() {
            @Override
            public void run() {
                while (running.get()) {
                    for (Collection<IScheduledFuture<Object>> collection : executorService.getAllScheduledFutures().values()) {
                        for (IScheduledFuture future : collection) {
                            if ((future.getStats().getTotalRuns()) >= 1) {
                                future.dispose();
                            }
                        }
                    }
                } 
            }
        });
        while (running.get()) {
            try {
                executorService.schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), 1, TimeUnit.SECONDS);
                Thread.yield();
                if (((counter++) % 1000) == 0) {
                    System.out.println(("Tasks: " + counter));
                }
                if (counter >= limit) {
                    running.set(false);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                running.set(false);
            }
        } 
        // wait for running tasks to finish, keeping log clean of PassiveMode exceptions
        HazelcastTestSupport.sleepSeconds(5);
    }
}

