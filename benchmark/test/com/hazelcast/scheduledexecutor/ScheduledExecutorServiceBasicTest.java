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


import DistributedScheduledExecutorService.SERVICE_NAME;
import com.hazelcast.config.Config;
import com.hazelcast.config.ScheduledExecutorConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IAtomicLong;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.core.IMap;
import com.hazelcast.core.Member;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.scheduledexecutor.impl.DistributedScheduledExecutorService;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.partition.IPartition;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.RootCauseMatcher;
import com.hazelcast.util.executor.ManagedExecutorService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ScheduledExecutorServiceBasicTest extends ScheduledExecutorServiceTestSupport {
    @Rule
    public ExpectedException expected = ExpectedException.none();

    @Test
    public void config() {
        String schedulerName = "foobar";
        ScheduledExecutorConfig sec = new ScheduledExecutorConfig().setName(schedulerName).setDurability(5).setPoolSize(24);
        Config config = new Config().addScheduledExecutorConfig(sec);
        HazelcastInstance[] instances = createClusterWithCount(1, config);
        IScheduledFuture future = instances[0].getScheduledExecutorService(schedulerName).schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), 0, TimeUnit.SECONDS);
        NodeEngineImpl nodeEngine = HazelcastTestSupport.getNodeEngineImpl(instances[0]);
        ManagedExecutorService mes = ((ManagedExecutorService) (nodeEngine.getExecutionService().getScheduledDurable(sec.getName())));
        DistributedScheduledExecutorService dses = nodeEngine.getService(SERVICE_NAME);
        Assert.assertNotNull(mes);
        Assert.assertEquals(24, mes.getMaximumPoolSize());
        Assert.assertEquals(5, dses.getPartition(future.getHandler().getPartitionId()).getOrCreateContainer(schedulerName).getDurability());
        Assert.assertEquals(1, dses.getPartition(future.getHandler().getPartitionId()).getOrCreateContainer("other").getDurability());
    }

    @Test
    public void exception_suppressesFutureExecutions() throws InterruptedException, ExecutionException {
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService service = instances[0].getScheduledExecutorService("test");
        final IScheduledFuture f = service.scheduleAtFixedRate(new ScheduledExecutorServiceTestSupport.ErroneousRunnableTask(), 1, 1, TimeUnit.SECONDS);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(f.isDone());
            }
        });
        Assert.assertEquals(1L, f.getStats().getTotalRuns());
        expected.expect(ExecutionException.class);
        expected.expectCause(new RootCauseMatcher(IllegalStateException.class, "Erroneous task"));
        f.get();
    }

    @Test
    public void capacity_whenNoLimit() {
        String schedulerName = "foobar";
        ScheduledExecutorConfig sec = new ScheduledExecutorConfig().setName(schedulerName).setDurability(1).setPoolSize(1).setCapacity(0);
        Config config = new Config().addScheduledExecutorConfig(sec);
        HazelcastInstance[] instances = createClusterWithCount(1, config);
        IScheduledExecutorService service = instances[0].getScheduledExecutorService(schedulerName);
        String keyOwner = "hitSamePartitionToCheckCapacity";
        for (int i = 0; i < 101; i++) {
            service.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), keyOwner, 0, TimeUnit.SECONDS);
        }
    }

    @Test
    public void capacity_whenDefault() {
        String schedulerName = "foobar";
        HazelcastInstance[] instances = createClusterWithCount(1, null);
        IScheduledExecutorService service = instances[0].getScheduledExecutorService(schedulerName);
        String keyOwner = "hitSamePartitionToCheckCapacity";
        for (int i = 0; i < 100; i++) {
            service.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), keyOwner, 0, TimeUnit.SECONDS);
        }
        try {
            service.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), keyOwner, 0, TimeUnit.SECONDS);
            Assert.fail("Should have been rejected.");
        } catch (RejectedExecutionException ex) {
            Assert.assertEquals("Got wrong RejectedExecutionException", ("Maximum capacity (100) of tasks reached, for scheduled executor (foobar). " + "Reminder that tasks must be disposed if not needed."), ex.getMessage());
        }
    }

    @Test
    public void capacity_whenPositiveLimit() {
        String schedulerName = "foobar";
        ScheduledExecutorConfig sec = new ScheduledExecutorConfig().setName(schedulerName).setDurability(1).setPoolSize(1).setCapacity(10);
        Config config = new Config().addScheduledExecutorConfig(sec);
        HazelcastInstance[] instances = createClusterWithCount(1, config);
        IScheduledExecutorService service = instances[0].getScheduledExecutorService(schedulerName);
        String keyOwner = "hitSamePartitionToCheckCapacity";
        for (int i = 0; i < 10; i++) {
            service.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), keyOwner, 0, TimeUnit.SECONDS);
        }
        try {
            service.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), keyOwner, 0, TimeUnit.SECONDS);
            Assert.fail("Should have been rejected.");
        } catch (RejectedExecutionException ex) {
            Assert.assertEquals("Got wrong RejectedExecutionException", ("Maximum capacity (10) of tasks reached, for scheduled executor (foobar). " + "Reminder that tasks must be disposed if not needed."), ex.getMessage());
        }
    }

    @Test
    public void capacity_onMember_whenPositiveLimit() {
        String schedulerName = "foobar";
        ScheduledExecutorConfig sec = new ScheduledExecutorConfig().setName(schedulerName).setDurability(1).setPoolSize(1).setCapacity(10);
        Config config = new Config().addScheduledExecutorConfig(sec);
        HazelcastInstance[] instances = createClusterWithCount(1, config);
        IScheduledExecutorService service = instances[0].getScheduledExecutorService(schedulerName);
        Member member = instances[0].getCluster().getLocalMember();
        for (int i = 0; i < 10; i++) {
            service.scheduleOnMember(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), member, 0, TimeUnit.SECONDS);
        }
        try {
            service.scheduleOnMember(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), member, 0, TimeUnit.SECONDS);
            Assert.fail("Should have been rejected.");
        } catch (RejectedExecutionException ex) {
            Assert.assertEquals("Got wrong RejectedExecutionException", ("Maximum capacity (10) of tasks reached, for scheduled executor (foobar). " + "Reminder that tasks must be disposed if not needed."), ex.getMessage());
        }
    }

    @Test
    public void handlerTaskAndSchedulerNames_withCallable() throws Exception {
        int delay = 0;
        String schedulerName = "s";
        String taskName = "TestCallable";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = instances[0].getScheduledExecutorService(schedulerName);
        IScheduledFuture<Double> future = executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), delay, TimeUnit.SECONDS);
        future.get();
        ScheduledTaskHandler handler = future.getHandler();
        Assert.assertEquals(schedulerName, handler.getSchedulerName());
        Assert.assertEquals(taskName, handler.getTaskName());
    }

    @Test
    public void handlerTaskAndSchedulerNames_withRunnable() throws Exception {
        int delay = 0;
        String schedulerName = "s";
        String taskName = "TestRunnable";
        HazelcastInstance[] instances = createClusterWithCount(2);
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(1);
        IScheduledExecutorService executorService = instances[0].getScheduledExecutorService(schedulerName);
        IScheduledFuture future = executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("latch")), delay, TimeUnit.SECONDS);
        latch.await(10, TimeUnit.SECONDS);
        ScheduledTaskHandler handler = future.getHandler();
        Assert.assertEquals(schedulerName, handler.getSchedulerName());
        Assert.assertEquals(taskName, handler.getTaskName());
    }

    @Test
    public void stats() throws Exception {
        double delay = 2.0;
        HazelcastInstance[] instances = createClusterWithCount(2);
        Object key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> future = executorService.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), key, ((int) (delay)), TimeUnit.SECONDS);
        future.get();
        ScheduledTaskStatistics stats = future.getStats();
        Assert.assertEquals(1, stats.getTotalRuns());
        Assert.assertEquals(0, stats.getLastRunDuration(TimeUnit.SECONDS));
        Assert.assertEquals(0, stats.getTotalRunTime(TimeUnit.SECONDS));
        Assert.assertNotEquals(0, stats.getLastIdleTime(TimeUnit.SECONDS));
        Assert.assertNotEquals(0, stats.getTotalIdleTime(TimeUnit.SECONDS));
    }

    @Test
    public void stats_whenMemberOwned() throws Exception {
        double delay = 2.0;
        HazelcastInstance[] instances = createClusterWithCount(2);
        Member localMember = instances[0].getCluster().getLocalMember();
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> future = executorService.scheduleOnMember(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), localMember, ((int) (delay)), TimeUnit.SECONDS);
        future.get();
        ScheduledTaskStatistics stats = future.getStats();
        Assert.assertEquals(1, stats.getTotalRuns());
        Assert.assertEquals(0, stats.getLastRunDuration(TimeUnit.SECONDS));
        Assert.assertEquals(0, stats.getTotalRunTime(TimeUnit.SECONDS));
        Assert.assertNotEquals(0, stats.getLastIdleTime(TimeUnit.SECONDS));
        Assert.assertNotEquals(0, stats.getTotalIdleTime(TimeUnit.SECONDS));
    }

    @Test
    public void scheduleAndGet_withCallable() throws Exception {
        int delay = 5;
        double expectedResult = 25.0;
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> future = executorService.schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), delay, TimeUnit.SECONDS);
        double result = future.get();
        Assert.assertEquals(expectedResult, result, 0);
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
    }

    @Test
    public void scheduleAndGet_withCallable_durableAfterTaskCompletion() throws Exception {
        int delay = 5;
        double expectedResult = 25.0;
        HazelcastInstance[] instances = createClusterWithCount(2);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> future = executorService.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), key, delay, TimeUnit.SECONDS);
        double resultFromOriginalTask = future.get();
        instances[1].getLifecycleService().shutdown();
        double resultFromMigratedTask = future.get();
        Assert.assertEquals(expectedResult, resultFromOriginalTask, 0);
        Assert.assertEquals(expectedResult, resultFromMigratedTask, 0);
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
    }

    @Test
    public void schedule_withMapChanges_durable() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(2);
        IMap<String, Integer> map = instances[1].getMap("map");
        for (int i = 0; i < (ScheduledExecutorServiceTestSupport.MAP_INCREMENT_TASK_MAX_ENTRIES); i++) {
            map.put(String.valueOf(i), i);
        }
        Object key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        ICountDownLatch startedLatch = instances[1].getCountDownLatch("startedLatch");
        ICountDownLatch finishedLatch = instances[1].getCountDownLatch("finishedLatch");
        startedLatch.trySetCount(1);
        finishedLatch.trySetCount(1);
        IAtomicLong runEntryCounter = instances[1].getAtomicLong("runEntryCounterName");
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        executorService.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.ICountdownLatchMapIncrementCallableTask("map", "runEntryCounterName", "startedLatch", "finishedLatch"), key, 0, TimeUnit.SECONDS);
        HazelcastTestSupport.assertOpenEventually(startedLatch);
        instances[0].getLifecycleService().shutdown();
        HazelcastTestSupport.assertOpenEventually(finishedLatch);
        for (int i = 0; i < 10000; i++) {
            Assert.assertEquals((i + 1), ((int) (map.get(String.valueOf(i)))));
        }
        Assert.assertEquals(2, runEntryCounter.get());
    }

    @Test
    public void schedule_withLongSleepingCallable_cancelledAndGet() throws InterruptedException {
        int delay = 0;
        HazelcastInstance[] instances = createClusterWithCount(2);
        ICountDownLatch runsCountLatch = instances[0].getCountDownLatch("runsCountLatchName");
        runsCountLatch.trySetCount(1);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> future = executorService.schedule(new ScheduledExecutorServiceTestSupport.ICountdownLatchCallableTask("runsCountLatchName", 15000), delay, TimeUnit.SECONDS);
        HazelcastTestSupport.sleepSeconds(4);
        future.cancel(false);
        runsCountLatch.await(15, TimeUnit.SECONDS);
        Assert.assertTrue(future.isDone());
        Assert.assertTrue(future.isCancelled());
    }

    @Test
    public void schedule_withNegativeDelay() throws Exception {
        int delay = -2;
        double expectedResult = 25.0;
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> future = executorService.schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), delay, TimeUnit.SECONDS);
        double result = future.get();
        Assert.assertEquals(expectedResult, result, 0);
        Assert.assertTrue(future.isDone());
        Assert.assertFalse(future.isCancelled());
    }

    @Test(expected = DuplicateTaskException.class)
    public void schedule_duplicate() {
        int delay = 1;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), delay, TimeUnit.SECONDS);
        executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), delay, TimeUnit.SECONDS);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void schedule_thenCancelInterrupted() {
        int delay = 1;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), delay, TimeUnit.MINUTES);
        first.cancel(true);
    }

    @Test(expected = CancellationException.class)
    public void schedule_thenCancelAndGet() throws Exception {
        int delay = 1;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), delay, TimeUnit.MINUTES);
        first.cancel(false);
        first.get();
    }

    @Test(expected = TimeoutException.class)
    public void schedule_thenGetWithTimeout() throws Exception {
        int delay = 5;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), delay, TimeUnit.MINUTES);
        first.get(2, TimeUnit.SECONDS);
    }

    @Test
    public void schedule_getDelay() {
        int delay = 20;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), delay, TimeUnit.MINUTES);
        Assert.assertEquals(19, first.getDelay(TimeUnit.MINUTES));
    }

    @Test
    public void scheduleOnKeyOwner_getDelay() {
        int delay = 20;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        Object key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.scheduleOnKeyOwner(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), key, delay, TimeUnit.MINUTES);
        Assert.assertEquals(19, first.getDelay(TimeUnit.MINUTES));
    }

    @Test
    public void scheduleOnMember_getDelay() {
        int delay = 20;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        Member localMember = instances[0].getCluster().getLocalMember();
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.scheduleOnMember(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), localMember, delay, TimeUnit.MINUTES);
        Assert.assertEquals(19, first.getDelay(TimeUnit.MINUTES));
    }

    @Test
    public void schedule_andCancel() {
        HazelcastInstance[] instances = createClusterWithCount(2);
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(1);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture future = executorService.scheduleAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("latch"), 1, 1, TimeUnit.SECONDS);
        HazelcastTestSupport.sleepSeconds(5);
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        future.cancel(false);
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void schedule_andCancel_onMember() {
        HazelcastInstance[] instances = createClusterWithCount(2);
        Member localMember = instances[0].getCluster().getLocalMember();
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(1);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture future = executorService.scheduleOnMemberAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("latch"), localMember, 1, 1, TimeUnit.SECONDS);
        HazelcastTestSupport.sleepSeconds(5);
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        future.cancel(false);
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
    }

    @Test
    public void cancelledAndDone_durable() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(3);
        Object key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(1);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture future = executorService.scheduleOnKeyOwnerAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("latch"), key, 0, 1, TimeUnit.SECONDS);
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertFalse(future.isCancelled());
        Assert.assertFalse(future.isDone());
        future.cancel(false);
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
        instances[1].getLifecycleService().shutdown();
        Assert.assertTrue(future.isCancelled());
        Assert.assertTrue(future.isDone());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void schedule_compareTo() {
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), 1, TimeUnit.MINUTES);
        IScheduledFuture<Double> second = executorService.schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), 2, TimeUnit.MINUTES);
        Assert.assertEquals((-1), first.compareTo(second));
    }

    @Test(expected = StaleTaskException.class)
    public void schedule_thenDisposeThenGet() throws Exception {
        int delay = 1;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), delay, TimeUnit.SECONDS);
        first.dispose();
        first.get();
    }

    @Test(expected = StaleTaskException.class)
    public void schedule_thenDisposeThenGet_onMember() throws Exception {
        int delay = 1;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        Member localMember = instances[0].getCluster().getLocalMember();
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.scheduleOnMember(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), localMember, delay, TimeUnit.SECONDS);
        first.dispose();
        first.get();
    }

    @Test(expected = RejectedExecutionException.class)
    public void schedule_whenShutdown() {
        int delay = 1;
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        executorService.schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), delay, TimeUnit.SECONDS);
        executorService.shutdown();
        executorService.schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), delay, TimeUnit.SECONDS);
    }

    @Test
    public void schedule_testPartitionLostEvent_withMaxBackupCount() {
        schedule_testPartitionLostEvent(IPartition.MAX_BACKUP_COUNT);
    }

    @Test
    public void schedule_testPartitionLostEvent_withDurabilityCount() {
        schedule_testPartitionLostEvent(1);
    }

    @Test
    public void scheduleOnMember_testMemberLostEvent() {
        int delay = 1;
        HazelcastInstance[] instances = createClusterWithCount(2);
        Member member = instances[1].getCluster().getLocalMember();
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        final IScheduledFuture future = executorService.scheduleOnMember(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), member, delay, TimeUnit.SECONDS);
        instances[1].getLifecycleService().terminate();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                try {
                    future.get(0, TimeUnit.SECONDS);
                    Assert.fail();
                } catch (IllegalStateException ex) {
                    System.err.println(ex.getMessage());
                    Assert.assertEquals(String.format("Member with address: %s,  holding this scheduled task is not part of this cluster.", future.getHandler().getAddress()), ex.getMessage());
                } catch (TimeoutException ex) {
                    HazelcastTestSupport.ignore(ex);
                }
            }
        });
    }

    @Test
    public void schedule_getHandlerDisposeThenRecreateFutureAndGet() throws Exception {
        int delay = 1;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), delay, TimeUnit.SECONDS);
        ScheduledTaskHandler handler = first.getHandler();
        first.dispose();
        expected.expect(ExecutionException.class);
        expected.expectCause(new RootCauseMatcher(StaleTaskException.class));
        executorService.getScheduledFuture(handler).get();
    }

    @Test
    public void schedule_partitionAware() {
        int delay = 1;
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        Callable<Double> task = new ScheduledExecutorServiceTestSupport.PlainPartitionAwareCallableTask();
        IScheduledFuture<Double> first = executorService.schedule(task, delay, TimeUnit.SECONDS);
        ScheduledTaskHandler handler = first.getHandler();
        int expectedPartition = getPartitionIdFromPartitionAwareTask(instances[0], ((PartitionAware) (task)));
        Assert.assertEquals(expectedPartition, handler.getPartitionId());
    }

    @Test
    public void schedule_partitionAware_runnable() throws Exception {
        int delay = 1;
        String completionLatchName = "completionLatch";
        HazelcastInstance[] instances = createClusterWithCount(2);
        ICountDownLatch completionLatch = instances[0].getCountDownLatch(completionLatchName);
        completionLatch.trySetCount(1);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        Runnable task = new ScheduledExecutorServiceTestSupport.PlainPartitionAwareRunnableTask(completionLatchName);
        IScheduledFuture first = executorService.schedule(task, delay, TimeUnit.SECONDS);
        completionLatch.await(10, TimeUnit.SECONDS);
        ScheduledTaskHandler handler = first.getHandler();
        int expectedPartition = getPartitionIdFromPartitionAwareTask(instances[0], ((PartitionAware) (task)));
        Assert.assertEquals(expectedPartition, handler.getPartitionId());
    }

    @Test
    public void schedule_withStatefulRunnable() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(4);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(1);
        executorService.schedule(new ScheduledExecutorServiceTestSupport.StatefulRunnableTask("latch", "runC", "loadC"), 2, TimeUnit.SECONDS);
        latch.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void scheduleWithRepetition() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService s = getScheduledExecutor(instances, "s");
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(3);
        IScheduledFuture future = s.scheduleAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("latch"), 0, 1, TimeUnit.SECONDS);
        latch.await(10, TimeUnit.SECONDS);
        future.cancel(false);
        Assert.assertEquals(0, latch.getCount());
    }

    @Test
    public void scheduleOnMember() throws Exception {
        int delay = 1;
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        MemberImpl member = HazelcastTestSupport.getNodeEngineImpl(instances[0]).getLocalMember();
        IScheduledFuture<Double> future = executorService.scheduleOnMember(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), member, delay, TimeUnit.SECONDS);
        Assert.assertTrue(future.getHandler().isAssignedToMember());
        Assert.assertEquals(25.0, future.get(), 0);
    }

    @Test
    public void scheduleOnMemberWithRepetition() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(4);
        IScheduledExecutorService s = getScheduledExecutor(instances, "s");
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(4);
        Map<Member, IScheduledFuture<?>> futures = s.scheduleOnAllMembersAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("latch"), 0, 3, TimeUnit.SECONDS);
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
        Assert.assertEquals(4, futures.size());
    }

    @Test
    public void scheduleOnKeyOwner_thenGet() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        IScheduledFuture<Double> future = executorService.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), key, 2, TimeUnit.SECONDS);
        Assert.assertEquals(25.0, future.get(), 0.0);
    }

    @Test
    public void scheduleOnKeyOwner_withNotPeriodicRunnable() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(2);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[0]);
        IScheduledExecutorService s = getScheduledExecutor(instances, "s");
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(1);
        s.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("latch"), key, 2, TimeUnit.SECONDS).get();
        Assert.assertEquals(0, latch.getCount());
    }

    @Test
    public void scheduleOnKeyOwner_withNotPeriodicRunnableDurable() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(2);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        IScheduledExecutorService s = getScheduledExecutor(instances, "s");
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(1);
        IScheduledFuture future = s.scheduleOnKeyOwner(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("latch"), key, 2, TimeUnit.SECONDS);
        instances[1].getLifecycleService().shutdown();
        future.get();
        Assert.assertEquals(0, latch.getCount());
    }

    @Test
    public void scheduleOnKeyOwner_withCallable() throws Exception {
        int delay = 1;
        String key = "TestKey";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        Callable<Double> task = new ScheduledExecutorServiceTestSupport.PlainPartitionAwareCallableTask();
        IScheduledFuture<Double> first = executorService.scheduleOnKeyOwner(task, key, delay, TimeUnit.SECONDS);
        ScheduledTaskHandler handler = first.getHandler();
        int expectedPartition = instances[0].getPartitionService().getPartition(key).getPartitionId();
        Assert.assertEquals(expectedPartition, handler.getPartitionId());
        Assert.assertEquals(25, first.get(), 0);
    }

    @Test
    public void scheduleOnKeyOwnerWithRepetition() throws Exception {
        String key = "TestKey";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        ICountDownLatch latch = instances[0].getCountDownLatch("latch");
        latch.trySetCount(5);
        IScheduledFuture future = executorService.scheduleOnKeyOwnerAtFixedRate(new ScheduledExecutorServiceTestSupport.ICountdownLatchRunnableTask("latch"), key, 0, 1, TimeUnit.SECONDS);
        ScheduledTaskHandler handler = future.getHandler();
        int expectedPartition = instances[0].getPartitionService().getPartition(key).getPartitionId();
        Assert.assertEquals(expectedPartition, handler.getPartitionId());
        latch.await(60, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());
    }

    @Test
    public void getScheduled() {
        int delay = 1;
        String taskName = "Test";
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        IScheduledFuture<Double> first = executorService.schedule(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.PlainCallableTask()), delay, TimeUnit.SECONDS);
        ScheduledTaskHandler handler = first.getHandler();
        IScheduledFuture<Double> copy = executorService.getScheduledFuture(handler);
        Assert.assertEquals(first.getHandler(), copy.getHandler());
    }

    @Test
    public void scheduleOnAllMembers_getAllScheduled() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(3);
        IScheduledExecutorService s = getScheduledExecutor(instances, "s");
        s.scheduleOnAllMembers(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), 0, TimeUnit.SECONDS);
        Set<Member> members = instances[0].getCluster().getMembers();
        Map<Member, List<IScheduledFuture<Double>>> allScheduled = s.getAllScheduledFutures();
        Assert.assertEquals(members.size(), allScheduled.size());
        for (Member member : members) {
            Assert.assertEquals(1, allScheduled.get(member).size());
            Assert.assertEquals(25.0, allScheduled.get(member).get(0).get(), 0);
        }
    }

    @Test
    public void scheduleRandomPartitions_getAllScheduled() throws Exception {
        HazelcastInstance[] instances = createClusterWithCount(2);
        IScheduledExecutorService s = getScheduledExecutor(instances, "s");
        int expectedTotal = 11;
        IScheduledFuture[] futures = new IScheduledFuture[expectedTotal];
        for (int i = 0; i < expectedTotal; i++) {
            futures[i] = s.schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(i), 0, TimeUnit.SECONDS);
        }
        Assert.assertEquals(expectedTotal, countScheduledTasksOn(s), 0);
        // dispose 1 task
        futures[0].dispose();
        // recount
        Assert.assertEquals((expectedTotal - 1), countScheduledTasksOn(s), 0);
        // verify all tasks
        for (int i = 1; i < expectedTotal; i++) {
            Assert.assertEquals((25.0 + i), futures[i].get());
        }
    }

    @Test
    public void getErroneous() throws Exception {
        int delay = 2;
        String taskName = "Test";
        String completionLatchName = "completionLatch";
        HazelcastInstance[] instances = createClusterWithCount(2);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        ICountDownLatch latch = instances[1].getCountDownLatch(completionLatchName);
        latch.trySetCount(1);
        IScheduledFuture<Double> future = executorService.scheduleOnKeyOwner(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.ErroneousCallableTask(completionLatchName)), key, delay, TimeUnit.SECONDS);
        latch.await(10, TimeUnit.SECONDS);
        expected.expect(ExecutionException.class);
        expected.expectCause(new RootCauseMatcher(IllegalStateException.class, "Erroneous task"));
        future.get();
    }

    @Test
    public void getErroneous_durable() throws Exception {
        int delay = 2;
        String taskName = "Test";
        String completionLatchName = "completionLatch";
        HazelcastInstance[] instances = createClusterWithCount(2);
        String key = HazelcastTestSupport.generateKeyOwnedBy(instances[1]);
        IScheduledExecutorService executorService = getScheduledExecutor(instances, "s");
        ICountDownLatch latch = instances[1].getCountDownLatch(completionLatchName);
        latch.trySetCount(1);
        IScheduledFuture<Double> future = executorService.scheduleOnKeyOwner(TaskUtils.named(taskName, new ScheduledExecutorServiceTestSupport.ErroneousCallableTask(completionLatchName)), key, delay, TimeUnit.SECONDS);
        latch.await(10, TimeUnit.SECONDS);
        instances[1].getLifecycleService().shutdown();
        Thread.sleep(2000);
        expected.expect(ExecutionException.class);
        expected.expectCause(new RootCauseMatcher(IllegalStateException.class, "Erroneous task"));
        future.get();
    }

    @Test
    public void managedContext_whenLocalExecution() {
        HazelcastInstance instance = createHazelcastInstance();
        IScheduledExecutorService s = instance.getScheduledExecutorService("s");
        s.schedule(new ScheduledExecutorServiceTestSupport.PlainCallableTask(), 0, TimeUnit.SECONDS);
    }
}

