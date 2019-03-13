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
package org.apache.hadoop.yarn.server.resourcemanager.logaggregationstatus;


import LogAggregationStatus.DISABLED;
import LogAggregationStatus.FAILED;
import LogAggregationStatus.NOT_START;
import LogAggregationStatus.RUNNING;
import LogAggregationStatus.RUNNING_WITH_FAILURE;
import LogAggregationStatus.SUCCEEDED;
import LogAggregationStatus.TIME_OUT;
import RMAppState.KILLED;
import YarnConfiguration.LOG_AGGREGATION_ENABLED;
import YarnConfiguration.LOG_AGGREGATION_STATUS_TIME_OUT_MS;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.Resource;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.server.api.protocolrecords.LogAggregationReport;
import org.apache.hadoop.yarn.server.api.records.NodeHealthStatus;
import org.apache.hadoop.yarn.server.api.records.NodeStatus;
import org.apache.hadoop.yarn.server.resourcemanager.RMContext;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMApp;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEventType;
import org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppImpl;
import org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeImpl;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.YarnScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEvent;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.event.SchedulerEventType;
import org.junit.Assert;
import org.junit.Test;


public class TestRMAppLogAggregationStatus {
    private RMContext rmContext;

    private YarnScheduler scheduler;

    private SchedulerEventType eventType;

    private ApplicationId appId;

    private final class TestSchedulerEventDispatcher implements EventHandler<SchedulerEvent> {
        @Override
        public void handle(SchedulerEvent event) {
            scheduler.handle(event);
        }
    }

    @Test
    public void testLogAggregationStatus() throws Exception {
        YarnConfiguration conf = new YarnConfiguration();
        conf.setBoolean(LOG_AGGREGATION_ENABLED, true);
        conf.setLong(LOG_AGGREGATION_STATUS_TIME_OUT_MS, 1500);
        RMApp rmApp = createRMApp(conf);
        this.rmContext.getRMApps().put(appId, rmApp);
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent(this.appId, RMAppEventType.START));
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent(this.appId, RMAppEventType.APP_NEW_SAVED));
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent(this.appId, RMAppEventType.APP_ACCEPTED));
        // This application will be running on two nodes
        NodeId nodeId1 = NodeId.newInstance("localhost", 1234);
        Resource capability = Resource.newInstance(4096, 4);
        RMNodeImpl node1 = new RMNodeImpl(nodeId1, rmContext, null, 0, 0, null, capability, null);
        node1.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStartedEvent(nodeId1, null, null));
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppRunningOnNodeEvent(this.appId, nodeId1));
        NodeId nodeId2 = NodeId.newInstance("localhost", 2345);
        RMNodeImpl node2 = new RMNodeImpl(nodeId2, rmContext, null, 0, 0, null, capability, null);
        node2.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStartedEvent(node2.getNodeID(), null, null));
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppRunningOnNodeEvent(this.appId, nodeId2));
        // The initial log aggregation status for these two nodes
        // should be NOT_STARTED
        Map<NodeId, LogAggregationReport> logAggregationStatus = rmApp.getLogAggregationReportsForApp();
        Assert.assertEquals(2, logAggregationStatus.size());
        Assert.assertTrue(logAggregationStatus.containsKey(nodeId1));
        Assert.assertTrue(logAggregationStatus.containsKey(nodeId2));
        for (Map.Entry<NodeId, LogAggregationReport> report : logAggregationStatus.entrySet()) {
            Assert.assertEquals(NOT_START, report.getValue().getLogAggregationStatus());
        }
        List<LogAggregationReport> node1ReportForApp = new ArrayList<LogAggregationReport>();
        String messageForNode1_1 = "node1 logAggregation status updated at " + (System.currentTimeMillis());
        LogAggregationReport report1 = LogAggregationReport.newInstance(appId, RUNNING, messageForNode1_1);
        node1ReportForApp.add(report1);
        NodeStatus nodeStatus1 = NodeStatus.newInstance(node1.getNodeID(), 0, new ArrayList<org.apache.hadoop.yarn.api.records.ContainerStatus>(), null, NodeHealthStatus.newInstance(true, null, 0), null, null, null);
        node1.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent(node1.getNodeID(), nodeStatus1, node1ReportForApp));
        List<LogAggregationReport> node2ReportForApp = new ArrayList<LogAggregationReport>();
        String messageForNode2_1 = "node2 logAggregation status updated at " + (System.currentTimeMillis());
        LogAggregationReport report2 = LogAggregationReport.newInstance(appId, RUNNING, messageForNode2_1);
        node2ReportForApp.add(report2);
        NodeStatus nodeStatus2 = NodeStatus.newInstance(node2.getNodeID(), 0, new ArrayList<org.apache.hadoop.yarn.api.records.ContainerStatus>(), null, NodeHealthStatus.newInstance(true, null, 0), null, null, null);
        node2.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent(node2.getNodeID(), nodeStatus2, node2ReportForApp));
        // node1 and node2 has updated its log aggregation status
        // verify that the log aggregation status for node1, node2
        // has been changed
        logAggregationStatus = rmApp.getLogAggregationReportsForApp();
        Assert.assertEquals(2, logAggregationStatus.size());
        Assert.assertTrue(logAggregationStatus.containsKey(nodeId1));
        Assert.assertTrue(logAggregationStatus.containsKey(nodeId2));
        for (Map.Entry<NodeId, LogAggregationReport> report : logAggregationStatus.entrySet()) {
            if (report.getKey().equals(node1.getNodeID())) {
                Assert.assertEquals(RUNNING, report.getValue().getLogAggregationStatus());
                Assert.assertEquals(messageForNode1_1, report.getValue().getDiagnosticMessage());
            } else
                if (report.getKey().equals(node2.getNodeID())) {
                    Assert.assertEquals(RUNNING, report.getValue().getLogAggregationStatus());
                    Assert.assertEquals(messageForNode2_1, report.getValue().getDiagnosticMessage());
                } else {
                    // should not contain log aggregation report for other nodes
                    Assert.fail("should not contain log aggregation report for other nodes");
                }

        }
        // node1 updates its log aggregation status again
        List<LogAggregationReport> node1ReportForApp2 = new ArrayList<LogAggregationReport>();
        String messageForNode1_2 = "node1 logAggregation status updated at " + (System.currentTimeMillis());
        LogAggregationReport report1_2 = LogAggregationReport.newInstance(appId, RUNNING, messageForNode1_2);
        node1ReportForApp2.add(report1_2);
        node1.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent(node1.getNodeID(), nodeStatus1, node1ReportForApp2));
        // verify that the log aggregation status for node1
        // has been changed
        // verify that the log aggregation status for node2
        // does not change
        logAggregationStatus = rmApp.getLogAggregationReportsForApp();
        Assert.assertEquals(2, logAggregationStatus.size());
        Assert.assertTrue(logAggregationStatus.containsKey(nodeId1));
        Assert.assertTrue(logAggregationStatus.containsKey(nodeId2));
        for (Map.Entry<NodeId, LogAggregationReport> report : logAggregationStatus.entrySet()) {
            if (report.getKey().equals(node1.getNodeID())) {
                Assert.assertEquals(RUNNING, report.getValue().getLogAggregationStatus());
                Assert.assertEquals(((messageForNode1_1 + "\n") + messageForNode1_2), report.getValue().getDiagnosticMessage());
            } else
                if (report.getKey().equals(node2.getNodeID())) {
                    Assert.assertEquals(RUNNING, report.getValue().getLogAggregationStatus());
                    Assert.assertEquals(messageForNode2_1, report.getValue().getDiagnosticMessage());
                } else {
                    // should not contain log aggregation report for other nodes
                    Assert.fail("should not contain log aggregation report for other nodes");
                }

        }
        // kill the application
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent(appId, RMAppEventType.KILL));
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent(appId, RMAppEventType.ATTEMPT_KILLED));
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent(appId, RMAppEventType.APP_UPDATE_SAVED));
        Assert.assertEquals(KILLED, rmApp.getState());
        // wait for 1500 ms
        Thread.sleep(1500);
        // the log aggregation status for both nodes should be changed
        // to TIME_OUT
        logAggregationStatus = rmApp.getLogAggregationReportsForApp();
        Assert.assertEquals(2, logAggregationStatus.size());
        Assert.assertTrue(logAggregationStatus.containsKey(nodeId1));
        Assert.assertTrue(logAggregationStatus.containsKey(nodeId2));
        for (Map.Entry<NodeId, LogAggregationReport> report : logAggregationStatus.entrySet()) {
            Assert.assertEquals(TIME_OUT, report.getValue().getLogAggregationStatus());
        }
        // Finally, node1 finished its log aggregation and sent out its final
        // log aggregation status. The log aggregation status for node1 should
        // be changed from TIME_OUT to SUCCEEDED
        List<LogAggregationReport> node1ReportForApp3 = new ArrayList<LogAggregationReport>();
        LogAggregationReport report1_3;
        for (int i = 0; i < 10; i++) {
            report1_3 = LogAggregationReport.newInstance(appId, RUNNING, ("test_message_" + i));
            node1ReportForApp3.add(report1_3);
        }
        node1ReportForApp3.add(LogAggregationReport.newInstance(appId, SUCCEEDED, ""));
        // For every logAggregationReport cached in memory, we can only save at most
        // 10 diagnostic messages/failure messages
        node1.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent(node1.getNodeID(), nodeStatus1, node1ReportForApp3));
        logAggregationStatus = rmApp.getLogAggregationReportsForApp();
        Assert.assertEquals(2, logAggregationStatus.size());
        Assert.assertTrue(logAggregationStatus.containsKey(nodeId1));
        Assert.assertTrue(logAggregationStatus.containsKey(nodeId2));
        for (Map.Entry<NodeId, LogAggregationReport> report : logAggregationStatus.entrySet()) {
            if (report.getKey().equals(node1.getNodeID())) {
                Assert.assertEquals(SUCCEEDED, report.getValue().getLogAggregationStatus());
                StringBuilder builder = new StringBuilder();
                for (int i = 0; i < 9; i++) {
                    builder.append(("test_message_" + i));
                    builder.append("\n");
                }
                builder.append(("test_message_" + 9));
                Assert.assertEquals(builder.toString(), report.getValue().getDiagnosticMessage());
            } else
                if (report.getKey().equals(node2.getNodeID())) {
                    Assert.assertEquals(TIME_OUT, report.getValue().getLogAggregationStatus());
                } else {
                    // should not contain log aggregation report for other nodes
                    Assert.fail("should not contain log aggregation report for other nodes");
                }

        }
        // update log aggregationStatus for node2 as FAILED,
        // so the log aggregation status for the App will become FAILED,
        // and we only keep the log aggregation reports whose status is FAILED,
        // so the log aggregation report for node1 will be removed.
        List<LogAggregationReport> node2ReportForApp2 = new ArrayList<LogAggregationReport>();
        LogAggregationReport report2_2 = LogAggregationReport.newInstance(appId, RUNNING_WITH_FAILURE, "Fail_Message");
        LogAggregationReport report2_3 = LogAggregationReport.newInstance(appId, FAILED, "");
        node2ReportForApp2.add(report2_2);
        node2ReportForApp2.add(report2_3);
        node2.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmnode.RMNodeStatusEvent(node2.getNodeID(), nodeStatus2, node2ReportForApp2));
        Assert.assertEquals(FAILED, rmApp.getLogAggregationStatusForAppReport());
        logAggregationStatus = rmApp.getLogAggregationReportsForApp();
        Assert.assertTrue(((logAggregationStatus.size()) == 1));
        Assert.assertTrue(logAggregationStatus.containsKey(node2.getNodeID()));
        Assert.assertTrue((!(logAggregationStatus.containsKey(node1.getNodeID()))));
        Assert.assertEquals("Fail_Message", ((RMAppImpl) (rmApp)).getLogAggregationFailureMessagesForNM(nodeId2));
    }

    @Test(timeout = 10000)
    public void testGetLogAggregationStatusForAppReport() {
        YarnConfiguration conf = new YarnConfiguration();
        // Disable the log aggregation
        conf.setBoolean(LOG_AGGREGATION_ENABLED, false);
        RMAppImpl rmApp = ((RMAppImpl) (createRMApp(conf)));
        // The log aggregation status should be DISABLED.
        Assert.assertEquals(DISABLED, rmApp.getLogAggregationStatusForAppReport());
        // Enable the log aggregation
        conf.setBoolean(LOG_AGGREGATION_ENABLED, true);
        rmApp = ((RMAppImpl) (createRMApp(conf)));
        // If we do not know any NodeManagers for this application , and
        // the log aggregation is enabled, the log aggregation status will
        // return NOT_START
        Assert.assertEquals(NOT_START, rmApp.getLogAggregationStatusForAppReport());
        NodeId nodeId1 = NodeId.newInstance("localhost", 1111);
        NodeId nodeId2 = NodeId.newInstance("localhost", 2222);
        NodeId nodeId3 = NodeId.newInstance("localhost", 3333);
        NodeId nodeId4 = NodeId.newInstance("localhost", 4444);
        // If the log aggregation status for all NMs are NOT_START,
        // the log aggregation status for this app will return NOT_START
        rmApp.aggregateLogReport(nodeId1, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        rmApp.aggregateLogReport(nodeId2, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        rmApp.aggregateLogReport(nodeId3, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        rmApp.aggregateLogReport(nodeId4, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        Assert.assertEquals(NOT_START, rmApp.getLogAggregationStatusForAppReport());
        rmApp.aggregateLogReport(nodeId1, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        rmApp.aggregateLogReport(nodeId2, LogAggregationReport.newInstance(rmApp.getApplicationId(), RUNNING, ""));
        rmApp.aggregateLogReport(nodeId3, LogAggregationReport.newInstance(rmApp.getApplicationId(), SUCCEEDED, ""));
        rmApp.aggregateLogReport(nodeId4, LogAggregationReport.newInstance(rmApp.getApplicationId(), SUCCEEDED, ""));
        Assert.assertEquals(RUNNING, rmApp.getLogAggregationStatusForAppReport());
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent(rmApp.getApplicationId(), RMAppEventType.KILL));
        Assert.assertTrue(RMAppImpl.isAppInFinalState(rmApp));
        // If at least of one log aggregation status for one NM is TIME_OUT,
        // others are SUCCEEDED, the log aggregation status for this app will
        // return TIME_OUT
        rmApp.aggregateLogReport(nodeId1, LogAggregationReport.newInstance(rmApp.getApplicationId(), SUCCEEDED, ""));
        rmApp.aggregateLogReport(nodeId2, LogAggregationReport.newInstance(rmApp.getApplicationId(), TIME_OUT, ""));
        rmApp.aggregateLogReport(nodeId3, LogAggregationReport.newInstance(rmApp.getApplicationId(), SUCCEEDED, ""));
        rmApp.aggregateLogReport(nodeId4, LogAggregationReport.newInstance(rmApp.getApplicationId(), SUCCEEDED, ""));
        Assert.assertEquals(TIME_OUT, rmApp.getLogAggregationStatusForAppReport());
        rmApp = ((RMAppImpl) (createRMApp(conf)));
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent(rmApp.getApplicationId(), RMAppEventType.KILL));
        // If the log aggregation status for all NMs are SUCCEEDED and Application
        // is at the final state, the log aggregation status for this app will
        // return SUCCEEDED
        rmApp.aggregateLogReport(nodeId1, LogAggregationReport.newInstance(rmApp.getApplicationId(), SUCCEEDED, ""));
        rmApp.aggregateLogReport(nodeId2, LogAggregationReport.newInstance(rmApp.getApplicationId(), SUCCEEDED, ""));
        rmApp.aggregateLogReport(nodeId3, LogAggregationReport.newInstance(rmApp.getApplicationId(), SUCCEEDED, ""));
        rmApp.aggregateLogReport(nodeId4, LogAggregationReport.newInstance(rmApp.getApplicationId(), SUCCEEDED, ""));
        Assert.assertEquals(SUCCEEDED, rmApp.getLogAggregationStatusForAppReport());
        rmApp = ((RMAppImpl) (createRMApp(conf)));
        // If the log aggregation status for at least one of NMs are RUNNING,
        // the log aggregation status for this app will return RUNNING
        rmApp.aggregateLogReport(nodeId1, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        rmApp.aggregateLogReport(nodeId2, LogAggregationReport.newInstance(rmApp.getApplicationId(), RUNNING, ""));
        rmApp.aggregateLogReport(nodeId3, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        rmApp.aggregateLogReport(nodeId4, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        Assert.assertEquals(RUNNING, rmApp.getLogAggregationStatusForAppReport());
        // If the log aggregation status for at least one of NMs
        // are RUNNING_WITH_FAILURE, the log aggregation status
        // for this app will return RUNNING_WITH_FAILURE
        rmApp.aggregateLogReport(nodeId1, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        rmApp.aggregateLogReport(nodeId2, LogAggregationReport.newInstance(rmApp.getApplicationId(), RUNNING, ""));
        rmApp.aggregateLogReport(nodeId3, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        rmApp.aggregateLogReport(nodeId4, LogAggregationReport.newInstance(rmApp.getApplicationId(), RUNNING_WITH_FAILURE, ""));
        Assert.assertEquals(RUNNING_WITH_FAILURE, rmApp.getLogAggregationStatusForAppReport());
        // For node4, the previous log aggregation status is RUNNING_WITH_FAILURE,
        // it will not be changed even it get a new log aggregation status
        // as RUNNING
        rmApp.aggregateLogReport(nodeId1, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        rmApp.aggregateLogReport(nodeId2, LogAggregationReport.newInstance(rmApp.getApplicationId(), RUNNING, ""));
        rmApp.aggregateLogReport(nodeId3, LogAggregationReport.newInstance(rmApp.getApplicationId(), NOT_START, ""));
        rmApp.aggregateLogReport(nodeId4, LogAggregationReport.newInstance(rmApp.getApplicationId(), RUNNING, ""));
        Assert.assertEquals(RUNNING_WITH_FAILURE, rmApp.getLogAggregationStatusForAppReport());
        rmApp.handle(new org.apache.hadoop.yarn.server.resourcemanager.rmapp.RMAppEvent(rmApp.getApplicationId(), RMAppEventType.KILL));
        Assert.assertTrue(RMAppImpl.isAppInFinalState(rmApp));
        // If at least of one log aggregation status for one NM is FAILED,
        // others are either SUCCEEDED or TIME_OUT, and this application is
        // at the final state, the log aggregation status for this app
        // will return FAILED
        rmApp.aggregateLogReport(nodeId1, LogAggregationReport.newInstance(rmApp.getApplicationId(), SUCCEEDED, ""));
        rmApp.aggregateLogReport(nodeId2, LogAggregationReport.newInstance(rmApp.getApplicationId(), TIME_OUT, ""));
        rmApp.aggregateLogReport(nodeId3, LogAggregationReport.newInstance(rmApp.getApplicationId(), FAILED, ""));
        rmApp.aggregateLogReport(nodeId4, LogAggregationReport.newInstance(rmApp.getApplicationId(), FAILED, ""));
        Assert.assertEquals(FAILED, rmApp.getLogAggregationStatusForAppReport());
    }
}

