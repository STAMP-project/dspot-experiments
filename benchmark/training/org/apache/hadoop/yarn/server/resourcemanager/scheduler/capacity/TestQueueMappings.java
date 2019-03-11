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


import CapacitySchedulerConfiguration.QUEUE_MAPPING;
import java.io.IOException;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping.MappingType;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static CapacitySchedulerConfiguration.ROOT;


public class TestQueueMappings {
    private static final Logger LOG = LoggerFactory.getLogger(TestQueueMappings.class);

    private static final String Q1 = "q1";

    private static final String Q2 = "q2";

    private static final String Q1_PATH = ((ROOT) + ".") + (TestQueueMappings.Q1);

    private static final String Q2_PATH = ((ROOT) + ".") + (TestQueueMappings.Q2);

    private CapacityScheduler cs;

    private YarnConfiguration conf;

    @Test
    public void testQueueMappingSpecifyingNotExistedQueue() {
        // if the mapping specifies a queue that does not exist, reinitialize will
        // be failed
        conf.set(QUEUE_MAPPING, "u:user:non_existent_queue");
        boolean fail = false;
        try {
            cs.reinitialize(conf, null);
        } catch (IOException ioex) {
            fail = true;
        }
        Assert.assertTrue("queue initialization failed for non-existent q", fail);
    }

    @Test
    public void testQueueMappingTrimSpaces() throws IOException {
        // space trimming
        conf.set(QUEUE_MAPPING, ("    u : a : " + (TestQueueMappings.Q1)));
        cs.reinitialize(conf, null);
        checkQMapping(new org.apache.hadoop.yarn.server.resourcemanager.placement.UserGroupMappingPlacementRule.QueueMapping(MappingType.USER, "a", TestQueueMappings.Q1));
    }

    @Test(timeout = 60000)
    public void testQueueMappingParsingInvalidCases() throws Exception {
        // configuration parsing tests - negative test cases
        checkInvalidQMapping(conf, cs, "x:a:b", "invalid specifier");
        checkInvalidQMapping(conf, cs, "u:a", "no queue specified");
        checkInvalidQMapping(conf, cs, "g:a", "no queue specified");
        checkInvalidQMapping(conf, cs, "u:a:b,g:a", "multiple mappings with invalid mapping");
        checkInvalidQMapping(conf, cs, "u:a:b,g:a:d:e", "too many path segments");
        checkInvalidQMapping(conf, cs, "u::", "empty source and queue");
        checkInvalidQMapping(conf, cs, "u:", "missing source missing queue");
        checkInvalidQMapping(conf, cs, "u:a:", "empty source missing q");
    }
}

