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


import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


/**
 * QueueManager tests that require a real scheduler
 */
public class TestQueueManagerRealScheduler extends FairSchedulerTestBase {
    private static final File ALLOC_FILE = new File(FairSchedulerTestBase.TEST_DIR, "test-queue-mgr");

    @Test
    public void testBackwardsCompatiblePreemptionConfiguration() throws IOException {
        // Check the min/fair share preemption timeout for each queue
        QueueManager queueMgr = scheduler.getQueueManager();
        Assert.assertEquals(30000, queueMgr.getQueue("root").getFairSharePreemptionTimeout());
        Assert.assertEquals(30000, queueMgr.getQueue("default").getFairSharePreemptionTimeout());
        Assert.assertEquals(30000, queueMgr.getQueue("queueA").getFairSharePreemptionTimeout());
        Assert.assertEquals(30000, queueMgr.getQueue("queueB").getFairSharePreemptionTimeout());
        Assert.assertEquals(30000, queueMgr.getQueue("queueB.queueB1").getFairSharePreemptionTimeout());
        Assert.assertEquals(30000, queueMgr.getQueue("queueB.queueB2").getFairSharePreemptionTimeout());
        Assert.assertEquals(30000, queueMgr.getQueue("queueC").getFairSharePreemptionTimeout());
        Assert.assertEquals(15000, queueMgr.getQueue("root").getMinSharePreemptionTimeout());
        Assert.assertEquals(15000, queueMgr.getQueue("default").getMinSharePreemptionTimeout());
        Assert.assertEquals(15000, queueMgr.getQueue("queueA").getMinSharePreemptionTimeout());
        Assert.assertEquals(15000, queueMgr.getQueue("queueB").getMinSharePreemptionTimeout());
        Assert.assertEquals(5000, queueMgr.getQueue("queueB.queueB1").getMinSharePreemptionTimeout());
        Assert.assertEquals(15000, queueMgr.getQueue("queueB.queueB2").getMinSharePreemptionTimeout());
        Assert.assertEquals(15000, queueMgr.getQueue("queueC").getMinSharePreemptionTimeout());
        // Lower the fairshare preemption timeouts and verify it is picked
        // correctly.
        writeAllocFile(25, 30);
        scheduler.reinitialize(conf, resourceManager.getRMContext());
        Assert.assertEquals(25000, queueMgr.getQueue("root").getFairSharePreemptionTimeout());
    }
}

