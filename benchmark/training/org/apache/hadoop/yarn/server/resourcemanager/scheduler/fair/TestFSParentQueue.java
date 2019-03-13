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


import org.junit.Assert;
import org.junit.Test;


public class TestFSParentQueue {
    private FairSchedulerConfiguration conf;

    private QueueManager queueManager;

    @Test
    public void testConcurrentChangeToGetChildQueue() {
        queueManager.getLeafQueue("parent.child", true);
        queueManager.getLeafQueue("parent.child2", true);
        FSParentQueue test = queueManager.getParentQueue("parent", false);
        Assert.assertEquals(2, test.getChildQueues().size());
        boolean first = true;
        int childQueuesFound = 0;
        for (FSQueue childQueue : test.getChildQueues()) {
            if (first) {
                first = false;
                queueManager.getLeafQueue("parent.child3", true);
            }
            childQueuesFound++;
        }
        Assert.assertEquals(2, childQueuesFound);
        Assert.assertEquals(3, test.getChildQueues().size());
    }
}

