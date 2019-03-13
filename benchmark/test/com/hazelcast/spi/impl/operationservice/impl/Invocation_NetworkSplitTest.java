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
package com.hazelcast.spi.impl.operationservice.impl;


import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spi.AbstractWaitNotifyKey;
import com.hazelcast.spi.BlockingOperation;
import com.hazelcast.spi.ExceptionAction;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.WaitNotifyKey;
import com.hazelcast.test.AssertTask;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.SplitBrainTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.util.concurrent.TimeoutException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class Invocation_NetworkSplitTest extends HazelcastTestSupport {
    @Test
    public void testWaitingInvocations_whenNodeSplitFromCluster() {
        Invocation_NetworkSplitTest.SplitAction action = new Invocation_NetworkSplitTest.FullSplitAction();
        testWaitingInvocations_whenNodeSplitFromCluster(action);
    }

    @Test
    public void testWaitingInvocations_whenNodePartiallySplitFromCluster() {
        Invocation_NetworkSplitTest.SplitAction action = new Invocation_NetworkSplitTest.PartialSplitAction();
        testWaitingInvocations_whenNodeSplitFromCluster(action);
    }

    @Test
    public void testWaitNotifyService_whenNodeSplitFromCluster() {
        Invocation_NetworkSplitTest.SplitAction action = new Invocation_NetworkSplitTest.FullSplitAction();
        testWaitNotifyService_whenNodeSplitFromCluster(action);
    }

    @Test
    public void testWaitNotifyService_whenNodePartiallySplitFromCluster() {
        Invocation_NetworkSplitTest.SplitAction action = new Invocation_NetworkSplitTest.PartialSplitAction();
        testWaitNotifyService_whenNodeSplitFromCluster(action);
    }

    private static class AlwaysBlockingOperation extends Operation implements BlockingOperation {
        @Override
        public void run() throws Exception {
        }

        @Override
        public WaitNotifyKey getWaitKey() {
            return new AbstractWaitNotifyKey(getServiceName(), "test") {};
        }

        @Override
        public boolean shouldWait() {
            return true;
        }

        @Override
        public void onWaitExpire() {
            sendResponse(new TimeoutException());
        }

        @Override
        public String getServiceName() {
            return "AlwaysBlockingOperationService";
        }

        @Override
        public ExceptionAction onInvocationException(Throwable throwable) {
            return ExceptionAction.THROW_EXCEPTION;
        }
    }

    private interface SplitAction {
        void run(HazelcastInstance instance1, HazelcastInstance instance2, HazelcastInstance instance3);
    }

    private static class FullSplitAction implements Invocation_NetworkSplitTest.SplitAction {
        @Override
        public void run(final HazelcastInstance instance1, final HazelcastInstance instance2, final HazelcastInstance instance3) {
            // Artificially create a network-split
            SplitBrainTestSupport.blockCommunicationBetween(instance1, instance3);
            SplitBrainTestSupport.blockCommunicationBetween(instance2, instance3);
            HazelcastTestSupport.closeConnectionBetween(instance1, instance3);
            HazelcastTestSupport.closeConnectionBetween(instance2, instance3);
            HazelcastTestSupport.assertTrueEventually(new AssertTask() {
                @Override
                public void run() {
                    Assert.assertEquals(2, HazelcastTestSupport.getNodeEngineImpl(instance1).getClusterService().getSize());
                    Assert.assertEquals(2, HazelcastTestSupport.getNodeEngineImpl(instance2).getClusterService().getSize());
                    Assert.assertEquals(1, HazelcastTestSupport.getNodeEngineImpl(instance3).getClusterService().getSize());
                }
            });
        }
    }

    private static class PartialSplitAction implements Invocation_NetworkSplitTest.SplitAction {
        @Override
        public void run(final HazelcastInstance instance1, final HazelcastInstance instance2, final HazelcastInstance instance3) {
            // Artificially create a partial network-split;
            // node1 and node2 will be split from node3
            // but node 3 will not be able to detect that.
            SplitBrainTestSupport.blockCommunicationBetween(instance1, instance3);
            SplitBrainTestSupport.blockCommunicationBetween(instance2, instance3);
            HazelcastTestSupport.suspectMember(HazelcastTestSupport.getNode(instance1), HazelcastTestSupport.getNode(instance3));
            HazelcastTestSupport.assertTrueEventually(new AssertTask() {
                @Override
                public void run() {
                    Assert.assertEquals(2, HazelcastTestSupport.getNodeEngineImpl(instance1).getClusterService().getSize());
                    Assert.assertEquals(2, HazelcastTestSupport.getNodeEngineImpl(instance2).getClusterService().getSize());
                    Assert.assertEquals(3, HazelcastTestSupport.getNodeEngineImpl(instance3).getClusterService().getSize());
                }
            });
        }
    }
}

