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
package org.apache.hadoop.yarn.server.resourcemanager.placement;


import UserGroupMappingPlacementRule.QueueMapping;
import UserGroupMappingPlacementRule.QueueMapping.MappingType;
import YarnConfiguration.DEFAULT_QUEUE_NAME;
import YarnConfiguration.RM_SCHEDULER;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.hadoop.yarn.api.records.ApplicationSubmissionContext;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.TestCapacitySchedulerAutoCreatedQueueBase;
import org.apache.hadoop.yarn.util.Records;
import org.junit.Assert;
import org.junit.Test;


public class TestPlacementManager {
    public static final String USER = "user_";

    public static final String APP_NAME = "DistributedShell";

    public static final String APP_ID1 = "1";

    public static final String USER1 = (TestPlacementManager.USER) + (TestPlacementManager.APP_ID1);

    public static final String APP_ID2 = "2";

    public static final String USER2 = (TestPlacementManager.USER) + (TestPlacementManager.APP_ID2);

    public static final String PARENT_QUEUE = "c";

    private MockRM mockRM = null;

    private static final long CLUSTER_TIMESTAMP = System.currentTimeMillis();

    @Test
    public void testPlaceApplicationWithPlacementRuleChain() throws Exception {
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        TestCapacitySchedulerAutoCreatedQueueBase.setupQueueConfiguration(conf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        mockRM = new MockRM(conf);
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        start();
        cs.start();
        PlacementManager pm = cs.getRMContext().getQueuePlacementManager();
        List<PlacementRule> queuePlacementRules = new ArrayList<>();
        UserGroupMappingPlacementRule.QueueMapping userQueueMapping = new UserGroupMappingPlacementRule.QueueMapping(MappingType.USER, TestPlacementManager.USER1, getQueueMapping(TestPlacementManager.PARENT_QUEUE, TestPlacementManager.USER1));
        UserGroupMappingPlacementRule ugRule = new UserGroupMappingPlacementRule(false, Arrays.asList(userQueueMapping), null);
        queuePlacementRules.add(ugRule);
        pm.updateRules(queuePlacementRules);
        ApplicationSubmissionContext asc = Records.newRecord(ApplicationSubmissionContext.class);
        asc.setQueue(DEFAULT_QUEUE_NAME);
        asc.setApplicationName(TestPlacementManager.APP_NAME);
        Assert.assertNull("Placement should be null", pm.placeApplication(asc, TestPlacementManager.USER2));
        QueueMappingEntity queueMappingEntity = new QueueMappingEntity(TestPlacementManager.APP_NAME, TestPlacementManager.USER1, TestPlacementManager.PARENT_QUEUE);
        AppNameMappingPlacementRule anRule = new AppNameMappingPlacementRule(false, Arrays.asList(queueMappingEntity));
        queuePlacementRules.add(anRule);
        pm.updateRules(queuePlacementRules);
        try {
            ApplicationPlacementContext pc = pm.placeApplication(asc, TestPlacementManager.USER2);
            Assert.assertNotNull(pc);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail("Exception not expected");
        }
    }
}

