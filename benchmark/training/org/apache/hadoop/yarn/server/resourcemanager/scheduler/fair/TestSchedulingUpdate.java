/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair;


import org.apache.hadoop.yarn.server.resourcemanager.MockNodes;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNode;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeAddedSchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.NodeRemovedSchedulerEvent;
import org.apache.hadoop.yarn.util.resource.Resources;
import org.junit.Test;


public class TestSchedulingUpdate extends FairSchedulerTestBase {
    @Test(timeout = 3000)
    public void testSchedulingUpdateOnNodeJoinLeave() throws InterruptedException {
        verifyNoCalls();
        // Add one node
        String host = "127.0.0.1";
        final int memory = 4096;
        final int cores = 4;
        RMNode node1 = MockNodes.newNodeInfo(1, Resources.createResource(memory, cores), 1, host);
        NodeAddedSchedulerEvent nodeEvent1 = new NodeAddedSchedulerEvent(node1);
        scheduler.handle(nodeEvent1);
        long expectedCalls = 1;
        verifyExpectedCalls(expectedCalls, memory, cores);
        // Remove the node
        NodeRemovedSchedulerEvent nodeEvent2 = new NodeRemovedSchedulerEvent(node1);
        scheduler.handle(nodeEvent2);
        expectedCalls = 2;
        verifyExpectedCalls(expectedCalls, 0, 0);
    }
}

