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
package org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity;


import org.apache.hadoop.yarn.server.resourcemanager.ACLsTestBase;
import org.junit.Test;


public class TestApplicationPriorityACLs extends ACLsTestBase {
    private final int defaultPriorityQueueA = 3;

    private final int defaultPriorityQueueB = 10;

    private final int maxPriorityQueueA = 5;

    private final int maxPriorityQueueB = 11;

    private final int clusterMaxPriority = 10;

    @Test
    public void testApplicationACLs() throws Exception {
        /* Cluster Max-priority is 10. User 'queueA_user' has permission to submit
        apps only at priority 5. Default priority for this user is 3.
         */
        // Case 1: App will be submitted with priority 5.
        verifyAppSubmitWithPrioritySuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUEA, 5);
        // Case 2: App will be rejected as submitted priority was 6.
        verifyAppSubmitWithPriorityFailure(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUEA, 6);
        // Case 3: App will be submitted w/o priority, hence consider default 3.
        verifyAppSubmitWithPrioritySuccess(ACLsTestBase.QUEUE_A_USER, ACLsTestBase.QUEUEA, (-1));
        // Case 4: App will be submitted with priority 11.
        verifyAppSubmitWithPrioritySuccess(ACLsTestBase.QUEUE_B_USER, ACLsTestBase.QUEUEB, 11);
    }
}

