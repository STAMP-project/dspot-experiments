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


import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.FairSchedulerConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.fair.QueueManager;
import org.junit.Test;


/**
 * Simple tests for FS specific parts of the PlacementRule.
 */
public class TestPlacementRuleFS {
    // List of rules that are configurable (reject rule is not!)
    private static final List<Class<? extends PlacementRule>> CONFIG_RULES = new ArrayList<Class<? extends PlacementRule>>() {
        {
            add(DefaultPlacementRule.class);
            add(PrimaryGroupPlacementRule.class);
            add(SecondaryGroupExistingPlacementRule.class);
            add(SpecifiedPlacementRule.class);
            add(UserPlacementRule.class);
        }
    };

    // List of rules that are not configurable
    private static final List<Class<? extends PlacementRule>> NO_CONFIG_RULES = new ArrayList<Class<? extends PlacementRule>>() {
        {
            add(RejectPlacementRule.class);
        }
    };

    private static final FairSchedulerConfiguration CONF = new FairSchedulerConfiguration();

    private FairScheduler scheduler;

    private QueueManager queueManager;

    /**
     * Check the create and setting the config on the rule.
     * This walks over all known rules and check the behaviour:
     * - no config (null object)
     * - unknown object type
     * - boolean object
     * - xml config ({@link Element})
     * - calling initialize on the rule
     */
    @Test
    public void testRuleSetups() {
        // test for config(s) and init
        for (Class<? extends PlacementRule> ruleClass : TestPlacementRuleFS.CONFIG_RULES) {
            ruleCreateNoConfig(ruleClass);
            ruleCreateWrongObject(ruleClass);
            ruleCreateBoolean(ruleClass);
            ruleCreateElement(ruleClass);
            ruleInit(ruleClass);
        }
    }

    /**
     * Check the init of rules that do not use a config.
     */
    @Test
    public void testRuleInitOnly() {
        // test for init
        for (Class<? extends PlacementRule> ruleClass : TestPlacementRuleFS.NO_CONFIG_RULES) {
            ruleInit(ruleClass);
        }
    }
}

