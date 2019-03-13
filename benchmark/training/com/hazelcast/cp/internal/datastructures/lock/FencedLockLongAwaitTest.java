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
package com.hazelcast.cp.internal.datastructures.lock;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.OperationTimeoutException;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.lock.FencedLock;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class FencedLockLongAwaitTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    private String objectName = "lock";

    private String proxyName = (objectName) + "@group1";

    private int groupSize = 3;

    private CPGroupId groupId;

    private final long callTimeoutSeconds = 15;

    @Test(timeout = 300000)
    public void when_awaitDurationIsLongerThanOperationTimeout_then_invocationFromLeaderInstanceWaits() throws InterruptedException, ExecutionException {
        testLongAwait(getLeaderInstance(instances, groupId));
    }

    @Test(timeout = 300000)
    public void when_awaitDurationIsLongerThanOperationTimeout_then_invocationFromNonLeaderInstanceWaits() throws InterruptedException, ExecutionException {
        testLongAwait(getRandomFollowerInstance(instances, groupId));
    }

    @Test(timeout = 300000)
    public void when_longWaitOperationIsNotCommitted_then_itFailsWithOperationTimeoutException() {
        HazelcastInstance apInstance = factory.newHazelcastInstance(createConfig(groupSize, groupSize));
        final FencedLock lock = apInstance.getCPSubsystem().getLock(proxyName);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                lock.lock();
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertTrue(lock.isLocked());
            }
        });
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        for (int i = 0; i < (groupSize); i++) {
            HazelcastInstance instance = instances[i];
            if (instance != leader) {
                instance.getLifecycleService().terminate();
            }
        }
        try {
            lock.lock();
            Assert.fail();
        } catch (OperationTimeoutException ignored) {
        }
    }
}

