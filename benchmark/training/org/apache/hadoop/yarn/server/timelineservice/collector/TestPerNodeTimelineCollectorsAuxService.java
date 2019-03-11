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
package org.apache.hadoop.yarn.server.timelineservice.collector;


import ContainerType.APPLICATION_MASTER;
import ExitUtil.ExitException;
import YarnConfiguration.ATS_APP_COLLECTOR_LINGER_PERIOD_IN_MS;
import YarnConfiguration.TIMELINE_SERVICE_ENABLED;
import YarnConfiguration.TIMELINE_SERVICE_VERSION;
import YarnConfiguration.TIMELINE_SERVICE_WRITER_CLASS;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.server.api.ContainerInitializationContext;
import org.apache.hadoop.yarn.server.api.ContainerTerminationContext;
import org.apache.hadoop.yarn.server.timelineservice.storage.FileSystemTimelineWriterImpl;
import org.apache.hadoop.yarn.server.timelineservice.storage.TimelineWriter;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class TestPerNodeTimelineCollectorsAuxService {
    private ApplicationAttemptId appAttemptId;

    private PerNodeTimelineCollectorsAuxService auxService;

    private Configuration conf;

    private ApplicationId appId;

    public TestPerNodeTimelineCollectorsAuxService() {
        appId = ApplicationId.newInstance(System.currentTimeMillis(), 1);
        appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        conf = new YarnConfiguration();
        // enable timeline service v.2
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        conf.setFloat(TIMELINE_SERVICE_VERSION, 2.0F);
        conf.setClass(TIMELINE_SERVICE_WRITER_CLASS, FileSystemTimelineWriterImpl.class, TimelineWriter.class);
        conf.setLong(ATS_APP_COLLECTOR_LINGER_PERIOD_IN_MS, 1000L);
    }

    @Test
    public void testAddApplication() throws Exception {
        auxService = createCollectorAndAddApplication();
        // auxService should have a single app
        Assert.assertTrue(auxService.hasApplication(appAttemptId.getApplicationId()));
        auxService.close();
    }

    @Test
    public void testAddApplicationNonAMContainer() throws Exception {
        auxService = createCollector();
        ContainerId containerId = getContainerId(2L);// not an AM

        ContainerInitializationContext context = Mockito.mock(ContainerInitializationContext.class);
        Mockito.when(context.getContainerId()).thenReturn(containerId);
        auxService.initializeContainer(context);
        // auxService should not have that app
        Assert.assertFalse(auxService.hasApplication(appAttemptId.getApplicationId()));
    }

    @Test
    public void testRemoveApplication() throws Exception {
        auxService = createCollectorAndAddApplication();
        // auxService should have a single app
        Assert.assertTrue(auxService.hasApplication(appAttemptId.getApplicationId()));
        ContainerId containerId = getAMContainerId();
        ContainerTerminationContext context = Mockito.mock(ContainerTerminationContext.class);
        Mockito.when(context.getContainerId()).thenReturn(containerId);
        Mockito.when(context.getContainerType()).thenReturn(APPLICATION_MASTER);
        auxService.stopContainer(context);
        // auxService should not have that app
        Assert.assertFalse(auxService.hasApplication(appAttemptId.getApplicationId()));
        auxService.close();
    }

    @Test
    public void testRemoveApplicationNonAMContainer() throws Exception {
        auxService = createCollectorAndAddApplication();
        // auxService should have a single app
        Assert.assertTrue(auxService.hasApplication(appAttemptId.getApplicationId()));
        ContainerId containerId = getContainerId(2L);// not an AM

        ContainerTerminationContext context = Mockito.mock(ContainerTerminationContext.class);
        Mockito.when(context.getContainerId()).thenReturn(containerId);
        auxService.stopContainer(context);
        // auxService should still have that app
        Assert.assertTrue(auxService.hasApplication(appAttemptId.getApplicationId()));
        auxService.close();
    }

    @Test(timeout = 60000)
    public void testLaunch() throws Exception {
        ExitUtil.disableSystemExit();
        try {
            auxService = PerNodeTimelineCollectorsAuxService.launchServer(new String[0], createCollectorManager(), conf);
        } catch (ExitUtil e) {
            Assert.assertEquals(0, e.status);
            ExitUtil.resetFirstExitException();
            Assert.fail();
        }
    }

    @Test(timeout = 60000)
    public void testRemoveAppWhenSecondAttemptAMCotainerIsLaunchedSameNode() throws Exception {
        // add first attempt collector
        auxService = createCollectorAndAddApplication();
        // auxService should have a single app
        Assert.assertTrue(auxService.hasApplication(appAttemptId.getApplicationId()));
        // add second attempt collector before first attempt master container stop
        ContainerInitializationContext containerInitalizationContext = createContainerInitalizationContext(2);
        auxService.initializeContainer(containerInitalizationContext);
        Assert.assertTrue("Applicatin not found in collectors.", auxService.hasApplication(appAttemptId.getApplicationId()));
        // first attempt stop container
        ContainerTerminationContext context = createContainerTerminationContext(1);
        auxService.stopContainer(context);
        // 2nd attempt container removed, still collector should hold application id
        Assert.assertTrue(("collector has removed application though 2nd attempt" + " is running this node"), auxService.hasApplication(appAttemptId.getApplicationId()));
        // second attempt stop container
        context = createContainerTerminationContext(2);
        auxService.stopContainer(context);
        // auxService should not have that app
        Assert.assertFalse("Application is not removed from collector", auxService.hasApplication(appAttemptId.getApplicationId()));
        auxService.close();
    }
}

