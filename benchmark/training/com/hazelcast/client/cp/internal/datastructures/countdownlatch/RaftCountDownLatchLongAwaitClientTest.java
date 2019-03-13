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
package com.hazelcast.client.cp.internal.datastructures.countdownlatch;


import RaftCountDownLatchService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.datastructures.countdownlatch.RaftCountDownLatchService;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftCountDownLatchLongAwaitClientTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    private String objectName = "latch";

    private String proxyName = (objectName) + "@group1";

    private int groupSize = 3;

    private CPGroupId groupId;

    private final long callTimeoutSeconds = 15;

    private HazelcastInstance client;

    @Test
    public void when_awaitDurationIsLongerThanOperationTimeout_then_invocationFromClientWaits() throws InterruptedException, ExecutionException {
        final ICountDownLatch latch = client.getCPSubsystem().getCountDownLatch(proxyName);
        final HazelcastInstance instance = getLeaderInstance(instances, groupId);
        latch.trySetCount(1);
        Future<Boolean> f = spawn(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                return latch.await(5, TimeUnit.MINUTES);
            }
        });
        assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftCountDownLatchService service = getNodeEngineImpl(instance).getService(SERVICE_NAME);
                Assert.assertFalse(service.getLiveOperations(groupId).isEmpty());
            }
        });
        assertTrueAllTheTime(new AssertTask() {
            @Override
            public void run() {
                RaftCountDownLatchService service = getNodeEngineImpl(instance).getService(SERVICE_NAME);
                Assert.assertFalse(service.getLiveOperations(groupId).isEmpty());
            }
        }, ((callTimeoutSeconds) + 5));
        latch.countDown();
        assertCompletesEventually(f);
        Assert.assertTrue(f.get());
    }
}

