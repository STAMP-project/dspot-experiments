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
package com.hazelcast.cp.internal.datastructures.countdownlatch;


import RaftCountDownLatchService.SERVICE_NAME;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICountDownLatch;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.internal.HazelcastRaftTestSupport;
import com.hazelcast.cp.internal.datastructures.spi.blocking.ResourceRegistry;
import com.hazelcast.cp.internal.raft.impl.RaftNodeImpl;
import com.hazelcast.cp.internal.raft.impl.RaftUtil;
import com.hazelcast.cp.internal.raft.impl.log.LogEntry;
import com.hazelcast.cp.internal.raftop.snapshot.RestoreSnapshotOp;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class RaftCountDownLatchAdvancedTest extends HazelcastRaftTestSupport {
    private static final int LOG_ENTRY_COUNT_TO_SNAPSHOT = 10;

    private HazelcastInstance[] instances;

    private ICountDownLatch latch;

    private String objectName = "latch";

    private String proxyName = (objectName) + "@group1";

    private int groupSize = 3;

    @Test
    public void testSuccessfulAwaitClearsWaitTimeouts() {
        latch.trySetCount(1);
        CPGroupId groupId = getGroupId(latch);
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        final RaftCountDownLatchService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        final RaftCountDownLatchRegistry registry = service.getRegistryOrNull(groupId);
        final CountDownLatch threadLatch = new CountDownLatch(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await(10, TimeUnit.MINUTES);
                    threadLatch.countDown();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
                Assert.assertFalse(registry.getLiveOperations().isEmpty());
            }
        });
        latch.countDown();
        HazelcastTestSupport.assertOpenEventually(threadLatch);
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testFailedAwaitClearsWaitTimeouts() throws InterruptedException {
        latch.trySetCount(1);
        CPGroupId groupId = getGroupId(latch);
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        RaftCountDownLatchService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        final RaftCountDownLatchRegistry registry = service.getRegistryOrNull(groupId);
        boolean success = latch.await(1, TimeUnit.SECONDS);
        Assert.assertFalse(success);
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testDestroyClearsWaitTimeouts() {
        latch.trySetCount(1);
        CPGroupId groupId = getGroupId(latch);
        HazelcastInstance leader = getLeaderInstance(instances, groupId);
        RaftCountDownLatchService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
        final RaftCountDownLatchRegistry registry = service.getRegistryOrNull(groupId);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await(10, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
            }
        });
        latch.destroy();
        Assert.assertTrue(registry.getWaitTimeouts().isEmpty());
        Assert.assertTrue(registry.getLiveOperations().isEmpty());
    }

    @Test
    public void testNewRaftGroupMemberSchedulesTimeoutsWithSnapshot() throws InterruptedException, ExecutionException {
        latch.trySetCount(1);
        HazelcastTestSupport.spawn(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await(10, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        for (int i = 0; i < (RaftCountDownLatchAdvancedTest.LOG_ENTRY_COUNT_TO_SNAPSHOT); i++) {
            latch.trySetCount(1);
        }
        final CPGroupId groupId = getGroupId(latch);
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                HazelcastInstance leader = getLeaderInstance(instances, groupId);
                RaftCountDownLatchService service = HazelcastTestSupport.getNodeEngineImpl(leader).getService(SERVICE_NAME);
                ResourceRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
            }
        });
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                for (HazelcastInstance instance : instances) {
                    RaftNodeImpl raftNode = HazelcastRaftTestSupport.getRaftNode(instance, groupId);
                    Assert.assertNotNull(raftNode);
                    LogEntry snapshotEntry = RaftUtil.getSnapshotEntry(raftNode);
                    Assert.assertTrue(((snapshotEntry.index()) > 0));
                    List<RestoreSnapshotOp> ops = ((List<RestoreSnapshotOp>) (snapshotEntry.operation()));
                    for (RestoreSnapshotOp op : ops) {
                        if (op.getServiceName().equals(SERVICE_NAME)) {
                            ResourceRegistry registry = ((ResourceRegistry) (op.getSnapshot()));
                            Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
                            return;
                        }
                    }
                    Assert.fail();
                }
            }
        });
        instances[1].shutdown();
        final HazelcastInstance newInstance = factory.newHazelcastInstance(createConfig(groupSize, groupSize));
        newInstance.getCPSubsystem().getCPSubsystemManagementService().promoteToCPMember().get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() {
                RaftCountDownLatchService service = HazelcastTestSupport.getNodeEngineImpl(newInstance).getService(SERVICE_NAME);
                RaftCountDownLatchRegistry registry = service.getRegistryOrNull(groupId);
                Assert.assertNotNull(registry);
                Assert.assertFalse(registry.getWaitTimeouts().isEmpty());
                Assert.assertEquals(1, registry.getRemainingCount(objectName));
            }
        });
    }
}

