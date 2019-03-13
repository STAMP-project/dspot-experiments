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
package com.hazelcast.cp.internal;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.cp.CPGroupId;
import com.hazelcast.cp.exception.CPGroupDestroyedException;
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
public class RaftInvocationManagerTest extends HazelcastRaftTestSupport {
    private HazelcastInstance[] instances;

    @Test
    public void when_raftGroupIsCreated_then_raftOperationsAreExecuted() throws InterruptedException, ExecutionException {
        int nodeCount = 5;
        instances = newInstances(nodeCount);
        RaftInvocationManager invocationService = getRaftInvocationManager(instances[0]);
        final CPGroupId groupId = invocationService.createRaftGroup("test", nodeCount).get();
        for (int i = 0; i < 100; i++) {
            invocationService.invoke(groupId, new RaftTestApplyOp(("val" + i))).get();
        }
    }

    @Test
    public void when_raftGroupIsCreated_then_raftOperationsAreExecutedOnNonCPNode() throws InterruptedException, ExecutionException {
        int cpNodeCount = 5;
        instances = newInstances(cpNodeCount, 3, 1);
        RaftInvocationManager invocationService = getRaftInvocationManager(instances[((instances.length) - 1)]);
        final CPGroupId groupId = invocationService.createRaftGroup("test", cpNodeCount).get();
        for (int i = 0; i < 100; i++) {
            invocationService.invoke(groupId, new RaftTestApplyOp(("val" + i))).get();
        }
    }

    @Test
    public void when_raftGroupIsDestroyed_then_operationsEventuallyFail() throws InterruptedException, ExecutionException {
        int nodeCount = 3;
        instances = newInstances(nodeCount);
        final RaftInvocationManager invocationManager = getRaftInvocationManager(instances[0]);
        final CPGroupId groupId = invocationManager.createRaftGroup("test", nodeCount).get();
        invocationManager.invoke(groupId, new RaftTestApplyOp("val")).get();
        invocationManager.invoke(HazelcastRaftTestSupport.getRaftService(instances[0]).getMetadataGroupId(), new com.hazelcast.cp.internal.raftop.metadata.TriggerDestroyRaftGroupOp(groupId)).get();
        HazelcastTestSupport.assertTrueEventually(new AssertTask() {
            @Override
            public void run() throws Exception {
                try {
                    invocationManager.invoke(groupId, new RaftTestApplyOp("val")).get();
                    Assert.fail();
                } catch (ExecutionException e) {
                    HazelcastTestSupport.assertInstanceOf(CPGroupDestroyedException.class, e.getCause());
                }
            }
        });
    }
}

