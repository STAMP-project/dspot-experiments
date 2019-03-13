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
package org.apache.hadoop.yarn.server.resourcemanager.ahs;


import ContainerState.COMPLETE;
import FinalApplicationStatus.UNDEFINED;
import RMAppState.FINISHED;
import YarnConfiguration.APPLICATION_HISTORY_ENABLED;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.server.applicationhistoryservice.ApplicationHistoryStore;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationAttemptHistoryData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ApplicationHistoryData;
import org.apache.hadoop.yarn.server.applicationhistoryservice.records.ContainerHistoryData;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.attempt.RMAppAttempt;
import org.apache.hadoop.yarn.server.resourcemanager.rmcontainer.RMContainer;
import org.junit.Assert;
import org.junit.Test;


public class TestRMApplicationHistoryWriter {
    private static int MAX_RETRIES = 10;

    private RMApplicationHistoryWriter writer;

    private ApplicationHistoryStore store;

    private List<TestRMApplicationHistoryWriter.CounterDispatcher> dispatchers = new ArrayList<TestRMApplicationHistoryWriter.CounterDispatcher>();

    @Test
    public void testDefaultStoreSetup() throws Exception {
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(APPLICATION_HISTORY_ENABLED, true);
        RMApplicationHistoryWriter writer = new RMApplicationHistoryWriter();
        writer.init(conf);
        writer.start();
        try {
            Assert.assertFalse(writer.historyServiceEnabled);
            Assert.assertNull(writer.writer);
        } finally {
            writer.stop();
            writer.close();
        }
    }

    @Test
    public void testWriteApplication() throws Exception {
        RMApp app = TestRMApplicationHistoryWriter.createRMApp(ApplicationId.newInstance(0, 1));
        writer.applicationStarted(app);
        ApplicationHistoryData appHD = null;
        for (int i = 0; i < (TestRMApplicationHistoryWriter.MAX_RETRIES); ++i) {
            appHD = store.getApplication(ApplicationId.newInstance(0, 1));
            if (appHD != null) {
                break;
            } else {
                Thread.sleep(100);
            }
        }
        Assert.assertNotNull(appHD);
        Assert.assertEquals("test app", appHD.getApplicationName());
        Assert.assertEquals("test app type", appHD.getApplicationType());
        Assert.assertEquals("test user", appHD.getUser());
        Assert.assertEquals("test queue", appHD.getQueue());
        Assert.assertEquals(0L, appHD.getSubmitTime());
        Assert.assertEquals(1L, appHD.getStartTime());
        writer.applicationFinished(app, FINISHED);
        for (int i = 0; i < (TestRMApplicationHistoryWriter.MAX_RETRIES); ++i) {
            appHD = store.getApplication(ApplicationId.newInstance(0, 1));
            if ((appHD.getYarnApplicationState()) != null) {
                break;
            } else {
                Thread.sleep(100);
            }
        }
        Assert.assertEquals(2L, appHD.getFinishTime());
        Assert.assertEquals("test diagnostics info", appHD.getDiagnosticsInfo());
        Assert.assertEquals(UNDEFINED, appHD.getFinalApplicationStatus());
        Assert.assertEquals(YarnApplicationState.FINISHED, appHD.getYarnApplicationState());
    }

    @Test
    public void testWriteApplicationAttempt() throws Exception {
        RMAppAttempt appAttempt = TestRMApplicationHistoryWriter.createRMAppAttempt(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1));
        writer.applicationAttemptStarted(appAttempt);
        ApplicationAttemptHistoryData appAttemptHD = null;
        for (int i = 0; i < (TestRMApplicationHistoryWriter.MAX_RETRIES); ++i) {
            appAttemptHD = store.getApplicationAttempt(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1));
            if (appAttemptHD != null) {
                break;
            } else {
                Thread.sleep(100);
            }
        }
        Assert.assertNotNull(appAttemptHD);
        Assert.assertEquals("test host", appAttemptHD.getHost());
        Assert.assertEquals((-100), appAttemptHD.getRPCPort());
        Assert.assertEquals(ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1), 1), appAttemptHD.getMasterContainerId());
        writer.applicationAttemptFinished(appAttempt, RMAppAttemptState.FINISHED);
        for (int i = 0; i < (TestRMApplicationHistoryWriter.MAX_RETRIES); ++i) {
            appAttemptHD = store.getApplicationAttempt(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1));
            if ((appAttemptHD.getYarnApplicationAttemptState()) != null) {
                break;
            } else {
                Thread.sleep(100);
            }
        }
        Assert.assertEquals("test diagnostics info", appAttemptHD.getDiagnosticsInfo());
        Assert.assertEquals("test url", appAttemptHD.getTrackingURL());
        Assert.assertEquals(UNDEFINED, appAttemptHD.getFinalApplicationStatus());
        Assert.assertEquals(YarnApplicationAttemptState.FINISHED, appAttemptHD.getYarnApplicationAttemptState());
    }

    @Test
    public void testWriteContainer() throws Exception {
        RMContainer container = TestRMApplicationHistoryWriter.createRMContainer(ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1), 1));
        writer.containerStarted(container);
        ContainerHistoryData containerHD = null;
        for (int i = 0; i < (TestRMApplicationHistoryWriter.MAX_RETRIES); ++i) {
            containerHD = store.getContainer(ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1), 1));
            if (containerHD != null) {
                break;
            } else {
                Thread.sleep(100);
            }
        }
        Assert.assertNotNull(containerHD);
        Assert.assertEquals(NodeId.newInstance("test host", (-100)), containerHD.getAssignedNode());
        Assert.assertEquals(Resource.newInstance((-1), (-1)), containerHD.getAllocatedResource());
        Assert.assertEquals(Priority.UNDEFINED, containerHD.getPriority());
        Assert.assertEquals(0L, container.getCreationTime());
        writer.containerFinished(container);
        for (int i = 0; i < (TestRMApplicationHistoryWriter.MAX_RETRIES); ++i) {
            containerHD = store.getContainer(ContainerId.newContainerId(ApplicationAttemptId.newInstance(ApplicationId.newInstance(0, 1), 1), 1));
            if ((containerHD.getContainerState()) != null) {
                break;
            } else {
                Thread.sleep(100);
            }
        }
        Assert.assertEquals("test diagnostics info", containerHD.getDiagnosticsInfo());
        Assert.assertEquals((-1), containerHD.getContainerExitStatus());
        Assert.assertEquals(COMPLETE, containerHD.getContainerState());
    }

    @Test
    public void testParallelWrite() throws Exception {
        List<ApplicationId> appIds = new ArrayList<ApplicationId>();
        for (int i = 0; i < 10; ++i) {
            Random rand = new Random(i);
            ApplicationId appId = ApplicationId.newInstance(0, rand.nextInt());
            appIds.add(appId);
            RMApp app = TestRMApplicationHistoryWriter.createRMApp(appId);
            writer.applicationStarted(app);
            for (int j = 1; j <= 10; ++j) {
                ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, j);
                RMAppAttempt appAttempt = TestRMApplicationHistoryWriter.createRMAppAttempt(appAttemptId);
                writer.applicationAttemptStarted(appAttempt);
                for (int k = 1; k <= 10; ++k) {
                    ContainerId containerId = ContainerId.newContainerId(appAttemptId, k);
                    RMContainer container = TestRMApplicationHistoryWriter.createRMContainer(containerId);
                    writer.containerStarted(container);
                    writer.containerFinished(container);
                }
                writer.applicationAttemptFinished(appAttempt, RMAppAttemptState.FINISHED);
            }
            writer.applicationFinished(app, FINISHED);
        }
        for (int i = 0; i < (TestRMApplicationHistoryWriter.MAX_RETRIES); ++i) {
            if (allEventsHandled(((((20 * 10) * 10) + (20 * 10)) + 20))) {
                break;
            } else {
                Thread.sleep(500);
            }
        }
        Assert.assertTrue(allEventsHandled(((((20 * 10) * 10) + (20 * 10)) + 20)));
        // Validate all events of one application are handled by one dispatcher
        for (ApplicationId appId : appIds) {
            Assert.assertTrue(handledByOne(appId));
        }
    }

    @Test
    public void testRMWritingMassiveHistoryForFairSche() throws Exception {
        // test WritingMassiveHistory for Fair Scheduler.
        testRMWritingMassiveHistory(true);
    }

    @Test
    public void testRMWritingMassiveHistoryForCapacitySche() throws Exception {
        // test WritingMassiveHistory for Capacity Scheduler.
        testRMWritingMassiveHistory(false);
    }

    private static class CounterDispatcher extends AsyncDispatcher {
        private Map<ApplicationId, Integer> counts = new HashMap<ApplicationId, Integer>();

        @SuppressWarnings("rawtypes")
        @Override
        protected void dispatch(Event event) {
            if (event instanceof WritingApplicationHistoryEvent) {
                WritingApplicationHistoryEvent ashEvent = ((WritingApplicationHistoryEvent) (event));
                switch (ashEvent.getType()) {
                    case APP_START :
                        incrementCounts(getApplicationId());
                        break;
                    case APP_FINISH :
                        incrementCounts(getApplicationId());
                        break;
                    case APP_ATTEMPT_START :
                        incrementCounts(getApplicationId());
                        break;
                    case APP_ATTEMPT_FINISH :
                        incrementCounts(getApplicationId());
                        break;
                    case CONTAINER_START :
                        incrementCounts(getApplicationId());
                        break;
                    case CONTAINER_FINISH :
                        incrementCounts(getApplicationId());
                        break;
                }
            }
            super.dispatch(event);
        }

        private void incrementCounts(ApplicationId appId) {
            Integer val = counts.get(appId);
            if (val == null) {
                counts.put(appId, 1);
            } else {
                counts.put(appId, (val + 1));
            }
        }
    }
}

