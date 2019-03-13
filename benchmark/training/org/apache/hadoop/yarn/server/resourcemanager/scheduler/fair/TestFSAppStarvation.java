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


import FairSchedulerConfiguration.PREEMPTION;
import FairSchedulerConfiguration.PREEMPTION_THRESHOLD;
import java.io.File;
import org.apache.hadoop.yarn.util.ControlledClock;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test class to verify identification of app starvation
 */
public class TestFSAppStarvation extends FairSchedulerTestBase {
    private static final File ALLOC_FILE = new File(FairSchedulerTestBase.TEST_DIR, "test-QUEUES");

    private final ControlledClock clock = new ControlledClock();

    // Node Capacity = NODE_CAPACITY_MULTIPLE * (1 GB or 1 vcore)
    private static final int NODE_CAPACITY_MULTIPLE = 4;

    private static final String[] QUEUES = new String[]{ "no-preemption", "minshare", "fairshare.child", "drf.child" };

    private FairSchedulerWithMockPreemption.MockPreemptionThread preemptionThread;

    /* Test to verify application starvation is computed only when preemption
    is enabled.
     */
    @Test
    public void testPreemptionDisabled() throws Exception {
        conf.setBoolean(PREEMPTION, false);
        setupClusterAndSubmitJobs();
        Assert.assertNull("Found starved apps even when preemption is turned off", scheduler.getContext().getStarvedApps());
    }

    /* Test to verify application starvation is computed correctly when
    preemption is turned on.
     */
    @Test
    public void testPreemptionEnabled() throws Exception {
        setupClusterAndSubmitJobs();
        // Wait for apps to be processed by MockPreemptionThread
        for (int i = 0; i < 6000; ++i) {
            if ((preemptionThread.uniqueAppsAdded()) >= 3) {
                break;
            }
            Thread.sleep(10);
        }
        Assert.assertNotNull("FSContext does not have an FSStarvedApps instance", scheduler.getContext().getStarvedApps());
        Assert.assertEquals(("Expecting 3 starved applications, one each for the " + "minshare and fairshare queues"), 3, preemptionThread.uniqueAppsAdded());
        // Verify apps are added again only after the set delay for starvation has
        // passed.
        clock.tickSec(1);
        scheduler.update();
        Assert.assertEquals("Apps re-added even before starvation delay passed", preemptionThread.totalAppsAdded(), preemptionThread.uniqueAppsAdded());
        verifyLeafQueueStarvation();
        clock.tickMsec(FairSchedulerWithMockPreemption.DELAY_FOR_NEXT_STARVATION_CHECK_MS);
        scheduler.update();
        // Wait for apps to be processed by MockPreemptionThread
        for (int i = 0; i < 6000; ++i) {
            if ((preemptionThread.totalAppsAdded()) >= ((preemptionThread.uniqueAppsAdded()) * 2)) {
                break;
            }
            Thread.sleep(10);
        }
        Assert.assertEquals(("Each app should be marked as starved once" + " at each scheduler update above"), preemptionThread.totalAppsAdded(), ((preemptionThread.uniqueAppsAdded()) * 2));
    }

    /* Test to verify app starvation is computed only when the cluster
    utilization threshold is over the preemption threshold.
     */
    @Test
    public void testClusterUtilizationThreshold() throws Exception {
        // Set preemption threshold to 1.1, so the utilization is always lower
        conf.setFloat(PREEMPTION_THRESHOLD, 1.1F);
        setupClusterAndSubmitJobs();
        Assert.assertNotNull("FSContext does not have an FSStarvedApps instance", scheduler.getContext().getStarvedApps());
        Assert.assertEquals("Found starved apps when preemption threshold is over 100%", 0, preemptionThread.totalAppsAdded());
    }
}

