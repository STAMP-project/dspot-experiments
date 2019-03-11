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
package org.apache.hadoop.yarn.sls.appmaster;


import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ReservationId;
import org.apache.hadoop.yarn.client.cli.RMAdminCLI;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnException;
import org.apache.hadoop.yarn.server.resourcemanager.ResourceManager;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.sls.conf.SLSConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestAMSimulator {
    private ResourceManager rm;

    private YarnConfiguration conf;

    private Path metricOutputDir;

    private Class<?> slsScheduler;

    private Class<?> scheduler;

    public TestAMSimulator(Class<?> slsScheduler, Class<?> scheduler) {
        this.slsScheduler = slsScheduler;
        this.scheduler = scheduler;
    }

    class MockAMSimulator extends AMSimulator {
        @Override
        protected void processResponseQueue() throws IOException, InterruptedException, YarnException {
        }

        @Override
        protected void sendContainerRequest() throws IOException, InterruptedException, YarnException {
        }

        @Override
        public void initReservation(ReservationId id, long deadline, long now) {
        }

        @Override
        protected void checkStop() {
        }
    }

    @Test
    public void testAMSimulator() throws Exception {
        // Register one app
        TestAMSimulator.MockAMSimulator app = new TestAMSimulator.MockAMSimulator();
        String appId = "app1";
        String queue = "default";
        List<ContainerSimulator> containers = new ArrayList<>();
        HashMap<ApplicationId, AMSimulator> map = new HashMap<>();
        app.init(1000, containers, rm, null, 0, 1000000L, "user1", queue, true, appId, 0, SLSConfiguration.getAMContainerResource(conf), null, null, map);
        firstStep();
        verifySchedulerMetrics(appId);
        Assert.assertEquals(1, rm.getRMContext().getRMApps().size());
        Assert.assertNotNull(rm.getRMContext().getRMApps().get(app.appId));
        // Finish this app
        lastStep();
    }

    @Test
    public void testAMSimulatorWithNodeLabels() throws Exception {
        if (scheduler.equals(CapacityScheduler.class)) {
            // add label to the cluster
            RMAdminCLI rmAdminCLI = new RMAdminCLI(conf);
            String[] args = new String[]{ "-addToClusterNodeLabels", "label1" };
            rmAdminCLI.run(args);
            TestAMSimulator.MockAMSimulator app = new TestAMSimulator.MockAMSimulator();
            String appId = "app1";
            String queue = "default";
            List<ContainerSimulator> containers = new ArrayList<>();
            HashMap<ApplicationId, AMSimulator> map = new HashMap<>();
            app.init(1000, containers, rm, null, 0, 1000000L, "user1", queue, true, appId, 0, SLSConfiguration.getAMContainerResource(conf), "label1", null, map);
            firstStep();
            verifySchedulerMetrics(appId);
            ConcurrentMap<ApplicationId, RMApp> rmApps = rm.getRMContext().getRMApps();
            Assert.assertEquals(1, rmApps.size());
            RMApp rmApp = rmApps.get(app.appId);
            Assert.assertNotNull(rmApp);
            Assert.assertEquals("label1", rmApp.getAmNodeLabelExpression());
        }
    }
}

