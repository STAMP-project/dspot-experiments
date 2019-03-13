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
package com.hazelcast.executor;


import com.hazelcast.config.Config;
import com.hazelcast.config.ExecutorConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IExecutorService;
import com.hazelcast.core.ManagedContext;
import com.hazelcast.core.PartitionAware;
import com.hazelcast.monitor.LocalExecutorStats;
import com.hazelcast.spi.properties.GroupProperty;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.TestHazelcastInstanceFactory;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class SpecificSetupTest extends ExecutorServiceTestSupport {
    @Test
    public void managedContext_mustInitializeRunnable() throws Exception {
        final AtomicBoolean initialized = new AtomicBoolean();
        Config config = new Config().addExecutorConfig(new ExecutorConfig("test", 1)).setManagedContext(new ManagedContext() {
            @Override
            public Object initialize(Object obj) {
                if (obj instanceof SpecificSetupTest.RunnableWithManagedContext) {
                    initialized.set(true);
                }
                return obj;
            }
        });
        IExecutorService executor = createHazelcastInstance(config).getExecutorService("test");
        executor.submit(new SpecificSetupTest.RunnableWithManagedContext()).get();
        Assert.assertTrue("The task should have been initialized by the ManagedContext", initialized.get());
    }

    @Test
    public void statsIssue2039() throws Exception {
        Config config = new Config();
        String name = "testStatsIssue2039";
        config.addExecutorConfig(new ExecutorConfig(name).setQueueCapacity(1).setPoolSize(1));
        HazelcastInstance instance = createHazelcastInstance(config);
        IExecutorService executorService = instance.getExecutorService(name);
        SpecificSetupTest.SleepLatchRunnable runnable = new SpecificSetupTest.SleepLatchRunnable();
        executorService.execute(runnable);
        Assert.assertTrue(SpecificSetupTest.SleepLatchRunnable.startLatch.await(30, TimeUnit.SECONDS));
        Future waitingInQueue = executorService.submit(new SpecificSetupTest.EmptyRunnable());
        Future rejected = executorService.submit(new SpecificSetupTest.EmptyRunnable());
        try {
            rejected.get(1, TimeUnit.MINUTES);
        } catch (Exception e) {
            if (!((e.getCause()) instanceof RejectedExecutionException)) {
                Assert.fail(e.toString());
            }
        } finally {
            SpecificSetupTest.SleepLatchRunnable.sleepLatch.countDown();
        }
        waitingInQueue.get(1, TimeUnit.MINUTES);
        LocalExecutorStats stats = executorService.getLocalExecutorStats();
        Assert.assertEquals(2, stats.getStartedTaskCount());
        Assert.assertEquals(0, stats.getPendingTaskCount());
    }

    @Test
    public void operationTimeoutConfigProp() throws Exception {
        TestHazelcastInstanceFactory factory = createHazelcastInstanceFactory(2);
        Config config = new Config();
        int timeoutSeconds = 3;
        config.setProperty(GroupProperty.OPERATION_CALL_TIMEOUT_MILLIS.getName(), String.valueOf(TimeUnit.SECONDS.toMillis(timeoutSeconds)));
        HazelcastInstance hz1 = factory.newHazelcastInstance(config);
        HazelcastInstance hz2 = factory.newHazelcastInstance(config);
        IExecutorService executor = hz1.getExecutorService(HazelcastTestSupport.randomString());
        Future<Boolean> future = executor.submitToMember(new ExecutorServiceTestSupport.SleepingTask((3 * timeoutSeconds)), hz2.getCluster().getLocalMember());
        Boolean result = future.get(1, TimeUnit.MINUTES);
        Assert.assertTrue(result);
    }

    private static class SleepLatchRunnable implements Serializable , Runnable {
        static CountDownLatch startLatch;

        static CountDownLatch sleepLatch;

        SleepLatchRunnable() {
            SpecificSetupTest.SleepLatchRunnable.startLatch = new CountDownLatch(1);
            SpecificSetupTest.SleepLatchRunnable.sleepLatch = new CountDownLatch(1);
        }

        @Override
        public void run() {
            SpecificSetupTest.SleepLatchRunnable.startLatch.countDown();
            HazelcastTestSupport.assertOpenEventually(SpecificSetupTest.SleepLatchRunnable.sleepLatch);
        }
    }

    static class RunnableWithManagedContext implements Serializable , Runnable {
        @Override
        public void run() {
        }
    }

    static class EmptyRunnable implements PartitionAware , Serializable , Runnable {
        @Override
        public void run() {
        }

        @Override
        public Object getPartitionKey() {
            return "key";
        }
    }
}

