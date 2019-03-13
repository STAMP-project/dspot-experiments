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
package com.hazelcast.client.io;


import com.hazelcast.client.test.TestHazelcastFactory;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.SlowTest;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category(SlowTest.class)
public class ClientExecutionPoolSizeLowTest {
    static final int COUNT = 1000;

    private final TestHazelcastFactory hazelcastFactory = new TestHazelcastFactory();

    private HazelcastInstance server1;

    private HazelcastInstance server2;

    private IMap map;

    @Test
    public void testNodeTerminate() throws InterruptedException, ExecutionException {
        for (int i = 0; i < (ClientExecutionPoolSizeLowTest.COUNT); i++) {
            map.put(i, i);
            if (i == ((ClientExecutionPoolSizeLowTest.COUNT) / 2)) {
                server2.getLifecycleService().terminate();
            }
        }
        Assert.assertEquals(ClientExecutionPoolSizeLowTest.COUNT, map.size());
    }

    @Test
    public void testOwnerNodeTerminate() throws InterruptedException, ExecutionException {
        for (int i = 0; i < (ClientExecutionPoolSizeLowTest.COUNT); i++) {
            map.put(i, i);
            if (i == ((ClientExecutionPoolSizeLowTest.COUNT) / 2)) {
                server1.getLifecycleService().terminate();
            }
        }
        Assert.assertEquals(ClientExecutionPoolSizeLowTest.COUNT, map.size());
    }

    @Test
    public void testNodeTerminateWithAsyncOperations() throws InterruptedException, ExecutionException {
        for (int i = 0; i < (ClientExecutionPoolSizeLowTest.COUNT); i++) {
            map.putAsync(i, i);
            if (i == ((ClientExecutionPoolSizeLowTest.COUNT) / 2)) {
                server2.getLifecycleService().terminate();
            }
        }
        HazelcastTestSupport.assertSizeEventually(ClientExecutionPoolSizeLowTest.COUNT, map);
    }

    @Test
    public void testOwnerNodeTerminateWithAsyncOperations() throws InterruptedException, ExecutionException {
        for (int i = 0; i < (ClientExecutionPoolSizeLowTest.COUNT); i++) {
            map.putAsync(i, i);
            if (i == ((ClientExecutionPoolSizeLowTest.COUNT) / 2)) {
                server1.getLifecycleService().terminate();
            }
        }
        HazelcastTestSupport.assertSizeEventually(ClientExecutionPoolSizeLowTest.COUNT, map);
    }
}

