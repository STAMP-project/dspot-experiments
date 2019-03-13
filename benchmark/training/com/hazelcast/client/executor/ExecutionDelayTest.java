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
package com.hazelcast.client.executor;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.NightlyTest;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(NightlyTest.class)
public class ExecutionDelayTest extends HazelcastTestSupport {
    private static final int CLUSTER_SIZE = 3;

    private static final AtomicInteger COUNTER = new AtomicInteger();

    private final List<HazelcastInstance> instances = new ArrayList<HazelcastInstance>(ExecutionDelayTest.CLUSTER_SIZE);

    private TestHazelcastFactory hazelcastFactory;

    @Test
    public void testExecutorRetriesTask_whenOneNodeTerminates() throws IOException, InterruptedException, ExecutionException {
        final int taskCount = 20;
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
        try {
            ex.schedule(new Runnable() {
                @Override
                public void run() {
                    instances.get(1).getLifecycleService().terminate();
                }
            }, 1000, TimeUnit.MILLISECONDS);
            ExecutionDelayTest.Task task = new ExecutionDelayTest.Task();
            runClient(task, taskCount);
            assertTrueEventually(new AssertTask() {
                @Override
                public void run() {
                    final int taskExecutions = ExecutionDelayTest.COUNTER.get();
                    Assert.assertTrue((taskExecutions >= taskCount));
                }
            });
        } finally {
            ex.shutdown();
        }
    }

    @Test
    public void testExecutorRetriesTask_whenOneNodeShutdowns() throws IOException, InterruptedException, ExecutionException {
        final int taskCount = 20;
        ScheduledExecutorService ex = Executors.newSingleThreadScheduledExecutor();
        try {
            ex.schedule(new Runnable() {
                @Override
                public void run() {
                    instances.get(1).shutdown();
                }
            }, 1000, TimeUnit.MILLISECONDS);
            ExecutionDelayTest.Task task = new ExecutionDelayTest.Task();
            runClient(task, taskCount);
            assertTrueEventually(new AssertTask() {
                @Override
                public void run() {
                    final int taskExecutions = ExecutionDelayTest.COUNTER.get();
                    Assert.assertTrue((taskExecutions >= taskCount));
                }
            });
        } finally {
            ex.shutdown();
        }
    }

    public static class Task implements Serializable , Callable {
        public Task() {
        }

        @Override
        public Object call() {
            ExecutionDelayTest.COUNTER.incrementAndGet();
            return null;
        }

        @Override
        public String toString() {
            return "Task{}";
        }
    }
}

