/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import org.junit.Test;


/**
 * Tests various preemption cases on auto-created leaf queues. All
 * auto-created leaf queues will end up having same priority since they are set
 * from template. Priority on ManagedParent Queues can be set however and
 * priority based premption cases are based on that.
 */
public class TestCapacitySchedulerAutoCreatedQueuePreemption extends TestCapacitySchedulerSurgicalPreemption {
    @Test(timeout = 60000)
    public void testSimpleSurgicalPreemptionOnAutoCreatedLeafQueues() throws Exception {
        /**
         * Test case: Submit two application (app1/app2) to different queues, queue
         * structure:
         *
         * <pre>
         *                    C
         *            /       |     \
         *           USER1   USER2   USER3
         *          30      30        30
         * </pre>
         *
         * 1) Two nodes (n1/n2) in the cluster, each of them has 20G.
         *
         * 2) app1 submit to queue-USER1 first, it asked 32 * 1G containers
         * We will allocate 16 on n1 and 16 on n2.
         *
         * 3) app2 submit to queue-USER2, ask for one 1G container (for AM)
         *
         * 4) app2 asks for another 6G container, it will be reserved on n1
         *
         * Now: we have:
         * n1: 17 from app1, 1 from app2, and 1 reserved from app2
         * n2: 16 from app1.
         *
         * After preemption, we should expect:
         * Preempt 4 containers from app1 on n1.
         */
        TestCapacitySchedulerAutoCreatedQueuePreemption.setupQueueConfigurationForSimpleSurgicalPreemption(conf);
        testSimpleSurgicalPreemption(TestCapacitySchedulerAutoCreatedQueueBase.USER1, TestCapacitySchedulerAutoCreatedQueueBase.USER2, TestCapacitySchedulerAutoCreatedQueueBase.USER1, TestCapacitySchedulerAutoCreatedQueueBase.USER2);
    }

    @Test(timeout = 600000)
    public void testPreemptionFromHighestPriorityManagedParentQueueAndOldestContainer() throws Exception {
        /**
         * Test case: Submit two application (app1/app2) to different queues, queue
         * structure:
         *
         * <pre>
         *             Root
         *            /  |  \
         *           c   d   e
         *          45  45  10
         * </pre>
         *
         * Priority of queue_c = 1
         * Priority of queue_d = 2
         *
         * 1) 5 nodes (n0-n4) in the cluster, each of them has 4G.
         *
         * 2) app1 submit to queue-e first (AM=1G), it asked 4 * 1G containers
         *    We will allocate 1 container on each of n0-n4. AM on n4.
         *
         * 3) app2 submit to queue-c, AM container=0.5G, allocated on n0
         *    Ask for 2 * 3.5G containers. (Reserved on n0/n1)
         *
         * 4) app2 submit to queue-d, AM container=0.5G, allocated on n2
         *    Ask for 2 * 3.5G containers. (Reserved on n2/n3)
         *
         * First we will preempt container on n2 since it is the oldest container of
         * Highest priority queue (d)
         */
        // Total preemption = 1G per round, which is 5% of cluster resource (20G)
        setupQueueConfigurationForPriorityBasedPreemption(conf);
        testPriorityPreemptionFromHighestPriorityQueueAndOldestContainer(new String[]{ TestCapacitySchedulerAutoCreatedQueueBase.USER1, TestCapacitySchedulerAutoCreatedQueueBase.USER3, TestCapacitySchedulerAutoCreatedQueueBase.USER0 }, new String[]{ TestCapacitySchedulerAutoCreatedQueueBase.USER1, TestCapacitySchedulerAutoCreatedQueueBase.USER3, TestCapacitySchedulerAutoCreatedQueueBase.USER0 });
    }
}

