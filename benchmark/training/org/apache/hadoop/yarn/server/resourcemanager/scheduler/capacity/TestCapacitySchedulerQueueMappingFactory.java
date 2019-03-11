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


import YarnConfiguration.RM_SCHEDULER;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.yarn.server.resourcemanager.MockRM;
import org.apache.hadoop.yarn.server.resourcemanager.placement.PlacementRule;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class TestCapacitySchedulerQueueMappingFactory {
    private static final String QUEUE_MAPPING_NAME = "app-name";

    private static final String QUEUE_MAPPING_RULE_APP_NAME = "org.apache.hadoop.yarn.server.resourcemanager.placement.AppNameMappingPlacementRule";

    private static final String QUEUE_MAPPING_RULE_USER_GROUP = "org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule";

    public static final String USER = "user_";

    public static final String PARENT_QUEUE = "c";

    private MockRM mockRM = null;

    @Test
    public void testUpdatePlacementRulesFactory() throws Exception {
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        TestCapacitySchedulerAutoCreatedQueueBase.setupQueueConfiguration(conf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        // init queue mapping for UserGroupMappingRule and AppNameMappingRule
        TestCapacitySchedulerQueueMappingFactory.setupQueueMappingsForRules(conf, TestCapacitySchedulerQueueMappingFactory.PARENT_QUEUE, true, new int[]{ 1, 2, 3 });
        mockRM = new MockRM(conf);
        CapacityScheduler cs = ((CapacityScheduler) (getResourceScheduler()));
        cs.updatePlacementRules();
        start();
        cs.start();
        List<PlacementRule> rules = cs.getRMContext().getQueuePlacementManager().getPlacementRules();
        List<String> placementRuleNames = new ArrayList<>();
        for (PlacementRule pr : rules) {
            placementRuleNames.add(pr.getName());
        }
        // verify both placement rules were added successfully
        Assert.assertThat(placementRuleNames, CoreMatchers.hasItems(TestCapacitySchedulerQueueMappingFactory.QUEUE_MAPPING_RULE_USER_GROUP));
        Assert.assertThat(placementRuleNames, CoreMatchers.hasItems(TestCapacitySchedulerQueueMappingFactory.QUEUE_MAPPING_RULE_APP_NAME));
    }
}

