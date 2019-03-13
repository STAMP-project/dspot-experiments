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


import RMAppState.KILLED;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.queuemanagement.GuaranteedOrZeroCapacityOverTimePolicy;
import org.junit.Assert;
import org.junit.Test;


public class TestQueueManagementDynamicEditPolicy extends TestCapacitySchedulerAutoCreatedQueueBase {
    private QueueManagementDynamicEditPolicy policy = new QueueManagementDynamicEditPolicy();

    @Test
    public void testEditSchedule() throws Exception {
        try {
            policy.editSchedule();
            Assert.assertEquals(2, policy.getManagedParentQueues().size());
            CSQueue parentQueue = cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.PARENT_QUEUE);
            GuaranteedOrZeroCapacityOverTimePolicy autoCreatedQueueManagementPolicy = ((GuaranteedOrZeroCapacityOverTimePolicy) (getAutoCreatedQueueManagementPolicy()));
            Assert.assertEquals(0.0F, autoCreatedQueueManagementPolicy.getAbsoluteActivatedChildQueueCapacity(NO_LABEL), CSQueueUtils.EPSILON);
            // submit app1 as USER1
            ApplicationId user1AppId = submitApp(mockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER1, TestCapacitySchedulerAutoCreatedQueueBase.USER1, 1, 1);
            Map<String, Float> expectedAbsChildQueueCapacity = populateExpectedAbsCapacityByLabelForParentQueue(1);
            validateInitialQueueEntitlement(parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER1, expectedAbsChildQueueCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            // submit another app2 as USER2
            ApplicationId user2AppId = submitApp(mockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER2, TestCapacitySchedulerAutoCreatedQueueBase.USER2, 2, 1);
            expectedAbsChildQueueCapacity = populateExpectedAbsCapacityByLabelForParentQueue(2);
            validateInitialQueueEntitlement(parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER2, expectedAbsChildQueueCapacity, TestCapacitySchedulerAutoCreatedQueueBase.accessibleNodeLabelsOnC);
            // validate total activated abs capacity
            Assert.assertEquals(0.2F, autoCreatedQueueManagementPolicy.getAbsoluteActivatedChildQueueCapacity(NO_LABEL), CSQueueUtils.EPSILON);
            // submit user_3 app. This cant be scheduled since there is no capacity
            submitApp(mockRM, parentQueue, TestCapacitySchedulerAutoCreatedQueueBase.USER3, TestCapacitySchedulerAutoCreatedQueueBase.USER3, 3, 1);
            final CSQueue user3LeafQueue = cs.getQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER3);
            validateCapacities(((AutoCreatedLeafQueue) (user3LeafQueue)), 0.0F, 0.0F, 1.0F, 1.0F);
            Assert.assertEquals(autoCreatedQueueManagementPolicy.getAbsoluteActivatedChildQueueCapacity(NO_LABEL), 0.2F, CSQueueUtils.EPSILON);
            // deactivate USER2 queue
            cs.killAllAppsInQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER2);
            mockRM.waitForState(user2AppId, KILLED);
            // deactivate USER1 queue
            cs.killAllAppsInQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER1);
            mockRM.waitForState(user1AppId, KILLED);
            policy.editSchedule();
            waitForPolicyState(0.1F, autoCreatedQueueManagementPolicy, NO_LABEL, 1000);
            validateCapacities(((AutoCreatedLeafQueue) (user3LeafQueue)), 0.5F, 0.1F, 1.0F, 1.0F);
            validateCapacitiesByLabel(((ManagedParentQueue) (parentQueue)), ((AutoCreatedLeafQueue) (user3LeafQueue)), TestCapacitySchedulerAutoCreatedQueueBase.NODEL_LABEL_GPU);
        } finally {
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER1);
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER2);
            cleanupQueue(TestCapacitySchedulerAutoCreatedQueueBase.USER3);
        }
    }
}

