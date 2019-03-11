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


import CapacitySchedulerConfiguration.ROOT;
import java.util.List;
import org.apache.hadoop.yarn.api.records.Priority;
import org.junit.Test;

import static CapacitySchedulerConfiguration.ROOT;


public class TestApplicationPriorityACLConfiguration {
    private final int defaultPriorityQueueA = 3;

    private final int defaultPriorityQueueB = -1;

    private final int maxPriorityQueueA = 5;

    private final int maxPriorityQueueB = 10;

    private final int clusterMaxPriority = 10;

    private static final String QUEUE_A_USER = "queueA_user";

    private static final String QUEUE_B_USER = "queueB_user";

    private static final String QUEUE_A_GROUP = "queueA_group";

    private static final String QUEUEA = "queueA";

    private static final String QUEUEB = "queueB";

    private static final String QUEUEC = "queueC";

    @Test
    public void testSimpleACLConfiguration() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setQueues(ROOT, new String[]{ TestApplicationPriorityACLConfiguration.QUEUEA, TestApplicationPriorityACLConfiguration.QUEUEB, TestApplicationPriorityACLConfiguration.QUEUEC });
        csConf.setCapacity((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEA)), 50.0F);
        csConf.setCapacity((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEB)), 25.0F);
        csConf.setCapacity((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEC)), 25.0F);
        // Success case: Configure one user/group level priority acl for queue A.
        String[] aclsForA = new String[2];
        aclsForA[0] = TestApplicationPriorityACLConfiguration.QUEUE_A_USER;
        aclsForA[1] = TestApplicationPriorityACLConfiguration.QUEUE_A_GROUP;
        csConf.setPriorityAcls((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEA)), Priority.newInstance(maxPriorityQueueA), Priority.newInstance(defaultPriorityQueueA), aclsForA);
        // Try to get the ACL configs and make sure there are errors/exceptions
        List<AppPriorityACLGroup> pGroupA = csConf.getPriorityAcls((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEA)), Priority.newInstance(clusterMaxPriority));
        // Validate!
        verifyACLs(pGroupA, TestApplicationPriorityACLConfiguration.QUEUE_A_USER, TestApplicationPriorityACLConfiguration.QUEUE_A_GROUP, maxPriorityQueueA, defaultPriorityQueueA);
    }

    @Test
    public void testACLConfigurationForInvalidCases() throws Exception {
        CapacitySchedulerConfiguration csConf = new CapacitySchedulerConfiguration();
        csConf.setQueues(ROOT, new String[]{ TestApplicationPriorityACLConfiguration.QUEUEA, TestApplicationPriorityACLConfiguration.QUEUEB, TestApplicationPriorityACLConfiguration.QUEUEC });
        csConf.setCapacity((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEA)), 50.0F);
        csConf.setCapacity((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEB)), 25.0F);
        csConf.setCapacity((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEC)), 25.0F);
        // Success case: Configure one user/group level priority acl for queue A.
        String[] aclsForA = new String[2];
        aclsForA[0] = TestApplicationPriorityACLConfiguration.QUEUE_A_USER;
        aclsForA[1] = TestApplicationPriorityACLConfiguration.QUEUE_A_GROUP;
        csConf.setPriorityAcls((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEA)), Priority.newInstance(maxPriorityQueueA), Priority.newInstance(defaultPriorityQueueA), aclsForA);
        String[] aclsForB = new String[1];
        aclsForB[0] = TestApplicationPriorityACLConfiguration.QUEUE_B_USER;
        csConf.setPriorityAcls((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEB)), Priority.newInstance(maxPriorityQueueB), Priority.newInstance(defaultPriorityQueueB), aclsForB);
        // Try to get the ACL configs and make sure there are errors/exceptions
        List<AppPriorityACLGroup> pGroupA = csConf.getPriorityAcls((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEA)), Priority.newInstance(clusterMaxPriority));
        List<AppPriorityACLGroup> pGroupB = csConf.getPriorityAcls((((ROOT) + ".") + (TestApplicationPriorityACLConfiguration.QUEUEB)), Priority.newInstance(clusterMaxPriority));
        // Validate stored ACL values with configured ones.
        verifyACLs(pGroupA, TestApplicationPriorityACLConfiguration.QUEUE_A_USER, TestApplicationPriorityACLConfiguration.QUEUE_A_GROUP, maxPriorityQueueA, defaultPriorityQueueA);
        verifyACLs(pGroupB, TestApplicationPriorityACLConfiguration.QUEUE_B_USER, "", maxPriorityQueueB, 0);
    }
}

