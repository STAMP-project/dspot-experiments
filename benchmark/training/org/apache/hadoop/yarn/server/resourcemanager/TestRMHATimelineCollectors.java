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
package org.apache.hadoop.yarn.server.resourcemanager;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.server.api.records.AppCollectorData;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test if the new active RM could recover collector status on a state
 * transition.
 */
public class TestRMHATimelineCollectors extends RMHATestBase {
    @Test
    public void testRebuildCollectorDataOnFailover() throws Exception {
        startRMs();
        MockNM nm1 = new MockNM("127.0.0.1:1234", 15120, getResourceTrackerService());
        MockNM nm2 = new MockNM("127.0.0.1:5678", 15121, getResourceTrackerService());
        RMApp app1 = RMHATestBase.rm1.submitApp(1024);
        String collectorAddr1 = "1.2.3.4:5";
        AppCollectorData data1 = AppCollectorData.newInstance(app1.getApplicationId(), collectorAddr1);
        nm1.addRegisteringCollector(app1.getApplicationId(), data1);
        String collectorAddr2 = "5.4.3.2:1";
        RMApp app2 = RMHATestBase.rm1.submitApp(1024);
        AppCollectorData data2 = AppCollectorData.newInstance(app2.getApplicationId(), collectorAddr2, getStartTime(), 1);
        nm1.addRegisteringCollector(app2.getApplicationId(), data2);
        explicitFailover();
        List<ApplicationId> runningApps = new ArrayList<>();
        runningApps.add(app1.getApplicationId());
        runningApps.add(app2.getApplicationId());
        nm1.registerNode(runningApps);
        nm2.registerNode(runningApps);
        String collectorAddr12 = "1.2.3.4:56";
        AppCollectorData data12 = AppCollectorData.newInstance(app1.getApplicationId(), collectorAddr12, getStartTime(), 0);
        nm2.addRegisteringCollector(app1.getApplicationId(), data12);
        String collectorAddr22 = "5.4.3.2:10";
        AppCollectorData data22 = AppCollectorData.newInstance(app2.getApplicationId(), collectorAddr22, getStartTime(), 2);
        nm2.addRegisteringCollector(app2.getApplicationId(), data22);
        Map<ApplicationId, AppCollectorData> results1 = nm1.nodeHeartbeat(true).getAppCollectors();
        Assert.assertEquals(collectorAddr1, results1.get(app1.getApplicationId()).getCollectorAddr());
        Assert.assertEquals(collectorAddr2, results1.get(app2.getApplicationId()).getCollectorAddr());
        Map<ApplicationId, AppCollectorData> results2 = nm2.nodeHeartbeat(true).getAppCollectors();
        // addr of app1 should be collectorAddr1 since it's registering (no time
        // stamp).
        Assert.assertEquals(collectorAddr1, results2.get(app1.getApplicationId()).getCollectorAddr());
        // addr of app2 should be collectorAddr22 since its version number is
        // greater.
        Assert.assertEquals(collectorAddr22, results2.get(app2.getApplicationId()).getCollectorAddr());
        // Now nm1 should get updated collector list
        nm1.getRegisteringCollectors().clear();
        Map<ApplicationId, AppCollectorData> results12 = nm1.nodeHeartbeat(true).getAppCollectors();
        Assert.assertEquals(collectorAddr1, results12.get(app1.getApplicationId()).getCollectorAddr());
        Assert.assertEquals(collectorAddr22, results12.get(app2.getApplicationId()).getCollectorAddr());
    }
}

