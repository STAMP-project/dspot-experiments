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
package org.apache.hadoop.yarn.client.cli;


import ApplicationCLI.CONTAINER_PATTERN;
import ApplicationTimeoutType.LIFETIME;
import CapacitySchedulerConfiguration.INTRAQUEUE_PREEMPTION_ENABLED;
import ContainerState.COMPLETE;
import ContainerState.RUNNING;
import FinalApplicationStatus.KILLED;
import FinalApplicationStatus.SUCCEEDED;
import NodeState.DECOMMISSIONED;
import NodeState.LOST;
import NodeState.NEW;
import NodeState.REBOOTED;
import NodeState.UNHEALTHY;
import Priority.UNDEFINED;
import ResourceInformation.MEMORY_MB;
import ResourceInformation.VCORES;
import YarnApplicationAttemptState.SCHEDULED;
import YarnApplicationState.ACCEPTED;
import YarnApplicationState.FAILED;
import YarnApplicationState.FINISHED;
import YarnApplicationState.SUBMITTED;
import YarnConfiguration.RM_SCHEDULER;
import YarnConfiguration.RM_SCHEDULER_ENABLE_MONITORS;
import YarnConfiguration.RM_SCHEDULER_MONITOR_POLICIES;
import com.google.common.collect.Sets;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.cli.Options;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.yarn.api.protocolrecords.UpdateApplicationTimeoutsRequest;
import org.apache.hadoop.yarn.api.protocolrecords.UpdateApplicationTimeoutsResponse;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptReport;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ApplicationReport;
import org.apache.hadoop.yarn.api.records.ApplicationResourceUsageReport;
import org.apache.hadoop.yarn.api.records.ApplicationTimeout;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.ContainerReport;
import org.apache.hadoop.yarn.api.records.NodeId;
import org.apache.hadoop.yarn.api.records.NodeLabel;
import org.apache.hadoop.yarn.api.records.NodeReport;
import org.apache.hadoop.yarn.api.records.NodeState;
import org.apache.hadoop.yarn.api.records.Priority;
import org.apache.hadoop.yarn.api.records.QueueInfo;
import org.apache.hadoop.yarn.api.records.YarnApplicationState;
import org.apache.hadoop.yarn.client.api.YarnClient;
import org.apache.hadoop.yarn.exceptions.ApplicationNotFoundException;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.resourcemanager.reservation.ReservationSystemTestUtil;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.ResourceScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacityScheduler;
import org.apache.hadoop.yarn.server.resourcemanager.scheduler.capacity.CapacitySchedulerConfiguration;
import org.apache.hadoop.yarn.util.Times;
import org.eclipse.jetty.util.log.Log;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestYarnCLI {
    private static final Logger LOG = LoggerFactory.getLogger(TestYarnCLI.class);

    private YarnClient client = Mockito.mock(YarnClient.class);

    ByteArrayOutputStream sysOutStream;

    private PrintStream sysOut;

    ByteArrayOutputStream sysErrStream;

    private PrintStream sysErr;

    private static final Pattern SPACES_PATTERN = Pattern.compile("\\s+|\\n+|\\t+");

    @Test
    public void testGetApplicationReport() throws Exception {
        for (int i = 0; i < 2; ++i) {
            ApplicationCLI cli = createAndGetAppCLI();
            ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
            Map<String, Long> resourceSecondsMap = new HashMap<>();
            Map<String, Long> preemptedResoureSecondsMap = new HashMap<>();
            resourceSecondsMap.put(MEMORY_MB.getName(), 123456L);
            resourceSecondsMap.put(VCORES.getName(), 4567L);
            preemptedResoureSecondsMap.put(MEMORY_MB.getName(), 1111L);
            preemptedResoureSecondsMap.put(VCORES.getName(), 2222L);
            ApplicationResourceUsageReport usageReport = (i == 0) ? null : ApplicationResourceUsageReport.newInstance(2, 0, null, null, null, resourceSecondsMap, 0, 0, preemptedResoureSecondsMap);
            ApplicationReport newApplicationReport = ApplicationReport.newInstance(applicationId, ApplicationAttemptId.newInstance(applicationId, 1), "user", "queue", "appname", "host", 124, null, FINISHED, "diagnostics", "url", 0, 0, 0, SUCCEEDED, usageReport, "N/A", 0.53789F, "YARN", null, null, false, Priority.newInstance(0), "high-mem", "high-mem");
            newApplicationReport.setLogAggregationStatus(LogAggregationStatus.SUCCEEDED);
            newApplicationReport.setPriority(Priority.newInstance(0));
            ApplicationTimeout timeout = ApplicationTimeout.newInstance(LIFETIME, "UNLIMITED", (-1));
            newApplicationReport.setApplicationTimeouts(Collections.singletonMap(timeout.getTimeoutType(), timeout));
            Mockito.when(client.getApplicationReport(ArgumentMatchers.any(ApplicationId.class))).thenReturn(newApplicationReport);
            int result = cli.run(new String[]{ "application", "-status", applicationId.toString() });
            Assert.assertEquals(0, result);
            Mockito.verify(client, Mockito.times((1 + i))).getApplicationReport(applicationId);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintWriter pw = new PrintWriter(baos);
            pw.println("Application Report : ");
            pw.println("\tApplication-Id : application_1234_0005");
            pw.println("\tApplication-Name : appname");
            pw.println("\tApplication-Type : YARN");
            pw.println("\tUser : user");
            pw.println("\tQueue : queue");
            pw.println("\tApplication Priority : 0");
            pw.println("\tStart-Time : 0");
            pw.println("\tFinish-Time : 0");
            pw.println("\tProgress : 53.79%");
            pw.println("\tState : FINISHED");
            pw.println("\tFinal-State : SUCCEEDED");
            pw.println("\tTracking-URL : N/A");
            pw.println("\tRPC Port : 124");
            pw.println("\tAM Host : host");
            pw.println(("\tAggregate Resource Allocation : " + (i == 0 ? "N/A" : "123456 MB-seconds, 4567 vcore-seconds")));
            pw.println(("\tAggregate Resource Preempted : " + (i == 0 ? "N/A" : "1111 MB-seconds, 2222 vcore-seconds")));
            pw.println("\tLog Aggregation Status : SUCCEEDED");
            pw.println("\tDiagnostics : diagnostics");
            pw.println("\tUnmanaged Application : false");
            pw.println("\tApplication Node Label Expression : high-mem");
            pw.println("\tAM container Node Label Expression : high-mem");
            pw.print("\tTimeoutType : LIFETIME");
            pw.print("\tExpiryTime : UNLIMITED");
            pw.println("\tRemainingTime : -1seconds");
            pw.println();
            pw.close();
            String appReportStr = baos.toString("UTF-8");
            Assert.assertEquals(appReportStr, sysOutStream.toString());
            sysOutStream.reset();
            Mockito.verify(sysOut, Mockito.times((1 + i))).println(ArgumentMatchers.isA(String.class));
        }
    }

    @Test
    public void testGetApplicationAttemptReport() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(applicationId, 1);
        ApplicationAttemptReport attemptReport = ApplicationAttemptReport.newInstance(attemptId, "host", 124, "url", "oUrl", "diagnostics", YarnApplicationAttemptState.FINISHED, ContainerId.newContainerId(attemptId, 1), 1000L, 2000L);
        Mockito.when(client.getApplicationAttemptReport(ArgumentMatchers.any(ApplicationAttemptId.class))).thenReturn(attemptReport);
        int result = cli.run(new String[]{ "applicationattempt", "-status", attemptId.toString() });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplicationAttemptReport(attemptId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Application Attempt Report : ");
        pw.println("\tApplicationAttempt-Id : appattempt_1234_0005_000001");
        pw.println("\tState : FINISHED");
        pw.println("\tAMContainer : container_1234_0005_01_000001");
        pw.println("\tTracking-URL : url");
        pw.println("\tRPC Port : 124");
        pw.println("\tAM Host : host");
        pw.println("\tDiagnostics : diagnostics");
        pw.close();
        String appReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(1)).println(ArgumentMatchers.isA(String.class));
    }

    @Test
    public void testGetApplicationAttempts() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(applicationId, 1);
        ApplicationAttemptId attemptId1 = ApplicationAttemptId.newInstance(applicationId, 2);
        ApplicationAttemptReport attemptReport = ApplicationAttemptReport.newInstance(attemptId, "host", 124, "url", "oUrl", "diagnostics", YarnApplicationAttemptState.FINISHED, ContainerId.newContainerId(attemptId, 1));
        ApplicationAttemptReport attemptReport1 = ApplicationAttemptReport.newInstance(attemptId1, "host", 124, "url", "oUrl", "diagnostics", YarnApplicationAttemptState.FINISHED, ContainerId.newContainerId(attemptId1, 1));
        List<ApplicationAttemptReport> reports = new ArrayList<ApplicationAttemptReport>();
        reports.add(attemptReport);
        reports.add(attemptReport1);
        Mockito.when(client.getApplicationAttempts(ArgumentMatchers.any(ApplicationId.class))).thenReturn(reports);
        int result = cli.run(new String[]{ "applicationattempt", "-list", applicationId.toString() });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplicationAttempts(applicationId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Total number of application attempts :2");
        pw.print("         ApplicationAttempt-Id");
        pw.print("\t               State");
        pw.print("\t                    AM-Container-Id");
        pw.println("\t                       Tracking-URL");
        pw.print("   appattempt_1234_0005_000001");
        pw.print("\t            FINISHED");
        pw.print("\t      container_1234_0005_01_000001");
        pw.println("\t                                url");
        pw.print("   appattempt_1234_0005_000002");
        pw.print("\t            FINISHED");
        pw.print("\t      container_1234_0005_02_000001");
        pw.println("\t                                url");
        pw.close();
        String appReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appReportStr, sysOutStream.toString());
    }

    @Test
    public void testGetContainerReport() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(applicationId, 1);
        ContainerId containerId = ContainerId.newContainerId(attemptId, 1);
        Map<String, List<Map<String, String>>> ports = new HashMap<>();
        ArrayList<Map<String, String>> list = new ArrayList();
        HashMap<String, String> map = new HashMap();
        map.put("abc", "123");
        list.add(map);
        ports.put("192.168.0.1", list);
        ContainerReport container = ContainerReport.newInstance(containerId, null, NodeId.newInstance("host", 1234), UNDEFINED, 1234, 5678, "diagnosticInfo", "logURL", 0, COMPLETE, ("http://" + (NodeId.newInstance("host", 2345).toString())));
        container.setExposedPorts(ports);
        Mockito.when(client.getContainerReport(ArgumentMatchers.any(ContainerId.class))).thenReturn(container);
        int result = cli.run(new String[]{ "container", "-status", containerId.toString() });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getContainerReport(containerId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Container Report : ");
        pw.println("\tContainer-Id : container_1234_0005_01_000001");
        pw.println("\tStart-Time : 1234");
        pw.println("\tFinish-Time : 5678");
        pw.println("\tState : COMPLETE");
        pw.println("\tExecution-Type : GUARANTEED");
        pw.println("\tLOG-URL : logURL");
        pw.println("\tHost : host:1234");
        pw.println("\tNodeHttpAddress : http://host:2345");
        pw.println("\tExposedPorts : {\"192.168.0.1\":[{\"abc\":\"123\"}]}");
        pw.println("\tDiagnostics : diagnosticInfo");
        pw.close();
        String appReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(1)).println(ArgumentMatchers.isA(String.class));
    }

    @Test
    public void testGetContainers() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(applicationId, 1);
        ContainerId containerId = ContainerId.newContainerId(attemptId, 1);
        ContainerId containerId1 = ContainerId.newContainerId(attemptId, 2);
        ContainerId containerId2 = ContainerId.newContainerId(attemptId, 3);
        long time1 = 1234;
        long time2 = 5678;
        Map<String, List<Map<String, String>>> ports = new HashMap<>();
        ContainerReport container = ContainerReport.newInstance(containerId, null, NodeId.newInstance("host", 1234), UNDEFINED, time1, time2, "diagnosticInfo", "logURL", 0, COMPLETE, ("http://" + (NodeId.newInstance("host", 2345).toString())));
        container.setExposedPorts(ports);
        ContainerReport container1 = ContainerReport.newInstance(containerId1, null, NodeId.newInstance("host", 1234), UNDEFINED, time1, time2, "diagnosticInfo", "logURL", 0, COMPLETE, ("http://" + (NodeId.newInstance("host", 2345).toString())));
        container1.setExposedPorts(ports);
        ContainerReport container2 = ContainerReport.newInstance(containerId2, null, NodeId.newInstance("host", 1234), UNDEFINED, time1, 0, "diagnosticInfo", "", 0, RUNNING, ("http://" + (NodeId.newInstance("host", 2345).toString())));
        container2.setExposedPorts(ports);
        List<ContainerReport> reports = new ArrayList<ContainerReport>();
        reports.add(container);
        reports.add(container1);
        reports.add(container2);
        Mockito.when(client.getContainers(ArgumentMatchers.any(ApplicationAttemptId.class))).thenReturn(reports);
        sysOutStream.reset();
        int result = cli.run(new String[]{ "container", "-list", attemptId.toString() });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getContainers(attemptId);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStreamWriter stream = new OutputStreamWriter(baos, "UTF-8");
        PrintWriter pw = new PrintWriter(stream);
        pw.println("Total number of containers :3");
        pw.printf(CONTAINER_PATTERN, "Container-Id", "Start Time", "Finish Time", "State", "Host", "Node Http Address", "LOG-URL");
        pw.printf(CONTAINER_PATTERN, "container_1234_0005_01_000001", Times.format(time1), Times.format(time2), "COMPLETE", "host:1234", "http://host:2345", "logURL");
        pw.printf(CONTAINER_PATTERN, "container_1234_0005_01_000002", Times.format(time1), Times.format(time2), "COMPLETE", "host:1234", "http://host:2345", "logURL");
        pw.printf(CONTAINER_PATTERN, "container_1234_0005_01_000003", Times.format(time1), "N/A", "RUNNING", "host:1234", "http://host:2345", "");
        pw.close();
        String appReportStr = baos.toString("UTF-8");
        Log.getLog().info("ExpectedOutput");
        Log.getLog().info((("[" + appReportStr) + "]"));
        Log.getLog().info("OutputFrom command");
        String actualOutput = sysOutStream.toString("UTF-8");
        Log.getLog().info((("[" + actualOutput) + "]"));
        Assert.assertEquals(appReportStr, actualOutput);
    }

    @Test
    public void testGetApplicationReportException() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        Mockito.when(client.getApplicationReport(ArgumentMatchers.any(ApplicationId.class))).thenThrow(new ApplicationNotFoundException((("History file for application" + applicationId) + " is not found")));
        int exitCode = cli.run(new String[]{ "application", "-status", applicationId.toString() });
        Mockito.verify(sysOut).println((("Application with id '" + applicationId) + "' doesn't exist in RM or Timeline Server."));
        Assert.assertNotSame("should return non-zero exit code.", 0, exitCode);
    }

    @Test
    public void testGetApplications() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationReport newApplicationReport = ApplicationReport.newInstance(applicationId, ApplicationAttemptId.newInstance(applicationId, 1), "user", "queue", "appname", "host", 124, null, YarnApplicationState.RUNNING, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53789F, "YARN", null, Sets.newHashSet("tag1", "tag3"), false, UNDEFINED, "", "");
        List<ApplicationReport> applicationReports = new ArrayList<ApplicationReport>();
        applicationReports.add(newApplicationReport);
        ApplicationId applicationId2 = ApplicationId.newInstance(1234, 6);
        ApplicationReport newApplicationReport2 = ApplicationReport.newInstance(applicationId2, ApplicationAttemptId.newInstance(applicationId2, 2), "user2", "queue2", "appname2", "host2", 125, null, FINISHED, "diagnostics2", "url2", 2, 2, 2, SUCCEEDED, null, "N/A", 0.63789F, "NON-YARN", null, Sets.newHashSet("tag2", "tag3"), false, UNDEFINED, "", "");
        applicationReports.add(newApplicationReport2);
        ApplicationId applicationId3 = ApplicationId.newInstance(1234, 7);
        ApplicationReport newApplicationReport3 = ApplicationReport.newInstance(applicationId3, ApplicationAttemptId.newInstance(applicationId3, 3), "user3", "queue3", "appname3", "host3", 126, null, YarnApplicationState.RUNNING, "diagnostics3", "url3", 3, 3, 3, SUCCEEDED, null, "N/A", 0.73789F, "MAPREDUCE", null, Sets.newHashSet("tag1", "tag4"), false, UNDEFINED, "", "");
        applicationReports.add(newApplicationReport3);
        ApplicationId applicationId4 = ApplicationId.newInstance(1234, 8);
        ApplicationReport newApplicationReport4 = ApplicationReport.newInstance(applicationId4, ApplicationAttemptId.newInstance(applicationId4, 4), "user4", "queue4", "appname4", "host4", 127, null, FAILED, "diagnostics4", "url4", 4, 4, 4, SUCCEEDED, null, "N/A", 0.83789F, "NON-MAPREDUCE", null, Sets.newHashSet("tag1"), false, UNDEFINED, "", "");
        applicationReports.add(newApplicationReport4);
        ApplicationId applicationId5 = ApplicationId.newInstance(1234, 9);
        ApplicationReport newApplicationReport5 = ApplicationReport.newInstance(applicationId5, ApplicationAttemptId.newInstance(applicationId5, 5), "user5", "queue5", "appname5", "host5", 128, null, ACCEPTED, "diagnostics5", "url5", 5, 5, 5, KILLED, null, "N/A", 0.93789F, "HIVE", null, Sets.newHashSet("tag2", "tag4"), false, UNDEFINED, "", "");
        applicationReports.add(newApplicationReport5);
        ApplicationId applicationId6 = ApplicationId.newInstance(1234, 10);
        ApplicationReport newApplicationReport6 = ApplicationReport.newInstance(applicationId6, ApplicationAttemptId.newInstance(applicationId6, 6), "user6", "queue6", "appname6", "host6", 129, null, SUBMITTED, "diagnostics6", "url6", 6, 6, 6, KILLED, null, "N/A", 0.99789F, "PIG", null, new HashSet<String>(), false, UNDEFINED, "", "");
        applicationReports.add(newApplicationReport6);
        // Test command yarn application -list
        // if the set appStates is empty, RUNNING state will be automatically added
        // to the appStates list
        // the output of yarn application -list should be the same as
        // equals to yarn application -list --appStates RUNNING,ACCEPTED,SUBMITTED
        Set<String> appType1 = new HashSet<String>();
        EnumSet<YarnApplicationState> appState1 = EnumSet.noneOf(YarnApplicationState.class);
        appState1.add(YarnApplicationState.RUNNING);
        appState1.add(ACCEPTED);
        appState1.add(SUBMITTED);
        Set<String> appTag = new HashSet<String>();
        Mockito.when(client.getApplications(appType1, appState1, appTag)).thenReturn(getApplicationReports(applicationReports, appType1, appState1, appTag, false));
        int result = cli.run(new String[]{ "application", "-list" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplications(appType1, appState1, appTag);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println((((((((("Total number of applications (application-types: " + appType1) + ", states: ") + appState1) + " and tags: ") + appTag) + ")") + ":") + 4));
        pw.print("                Application-Id\t    Application-Name");
        pw.print("\t    Application-Type");
        pw.print("\t      User\t     Queue\t             State\t       ");
        pw.print("Final-State\t       Progress");
        pw.println("\t                       Tracking-URL");
        pw.print("         application_1234_0005\t             ");
        pw.print("appname\t                YARN\t      user\t     ");
        pw.print("queue\t           RUNNING\t         ");
        pw.print("SUCCEEDED\t         53.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0007\t            ");
        pw.print("appname3\t           MAPREDUCE\t     user3\t    ");
        pw.print("queue3\t           RUNNING\t         ");
        pw.print("SUCCEEDED\t         73.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0009\t            ");
        pw.print("appname5\t                HIVE\t     user5\t    ");
        pw.print("queue5\t          ACCEPTED\t            ");
        pw.print("KILLED\t         93.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0010\t            ");
        pw.print("appname6\t                 PIG\t     user6\t    ");
        pw.print("queue6\t         SUBMITTED\t            ");
        pw.print("KILLED\t         99.79%");
        pw.println("\t                                N/A");
        pw.close();
        String appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(1)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Test command yarn application -list --appTypes apptype1,apptype2
        // the output should be the same as
        // yarn application -list --appTypes apptyp1, apptype2 --appStates
        // RUNNING,ACCEPTED,SUBMITTED
        sysOutStream.reset();
        Set<String> appType2 = new HashSet<String>();
        appType2.add("YARN");
        appType2.add("NON-YARN");
        EnumSet<YarnApplicationState> appState2 = EnumSet.noneOf(YarnApplicationState.class);
        appState2.add(YarnApplicationState.RUNNING);
        appState2.add(ACCEPTED);
        appState2.add(SUBMITTED);
        Mockito.when(client.getApplications(appType2, appState2, appTag)).thenReturn(getApplicationReports(applicationReports, appType2, appState2, appTag, false));
        result = cli.run(new String[]{ "application", "-list", "-appTypes", "YARN, ,,  NON-YARN", "   ,, ,," });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplications(appType2, appState2, appTag);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println((((((((("Total number of applications (application-types: " + appType2) + ", states: ") + appState2) + " and tags: ") + appTag) + ")") + ":") + 1));
        pw.print("                Application-Id\t    Application-Name");
        pw.print("\t    Application-Type");
        pw.print("\t      User\t     Queue\t             State\t       ");
        pw.print("Final-State\t       Progress");
        pw.println("\t                       Tracking-URL");
        pw.print("         application_1234_0005\t             ");
        pw.print("appname\t                YARN\t      user\t     ");
        pw.print("queue\t           RUNNING\t         ");
        pw.print("SUCCEEDED\t         53.79%");
        pw.println("\t                                N/A");
        pw.close();
        appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(2)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Test command yarn application -list --appStates appState1,appState2
        sysOutStream.reset();
        Set<String> appType3 = new HashSet<String>();
        EnumSet<YarnApplicationState> appState3 = EnumSet.noneOf(YarnApplicationState.class);
        appState3.add(FINISHED);
        appState3.add(FAILED);
        Mockito.when(client.getApplications(appType3, appState3, appTag)).thenReturn(getApplicationReports(applicationReports, appType3, appState3, appTag, false));
        result = cli.run(new String[]{ "application", "-list", "--appStates", "FINISHED ,, , FAILED", ",,FINISHED" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplications(appType3, appState3, appTag);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println((((((((("Total number of applications (application-types: " + appType3) + ", states: ") + appState3) + " and tags: ") + appTag) + ")") + ":") + 2));
        pw.print("                Application-Id\t    Application-Name");
        pw.print("\t    Application-Type");
        pw.print("\t      User\t     Queue\t             State\t       ");
        pw.print("Final-State\t       Progress");
        pw.println("\t                       Tracking-URL");
        pw.print("         application_1234_0006\t            ");
        pw.print("appname2\t            NON-YARN\t     user2\t    ");
        pw.print("queue2\t          FINISHED\t         ");
        pw.print("SUCCEEDED\t         63.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0008\t            ");
        pw.print("appname4\t       NON-MAPREDUCE\t     user4\t    ");
        pw.print("queue4\t            FAILED\t         ");
        pw.print("SUCCEEDED\t         83.79%");
        pw.println("\t                                N/A");
        pw.close();
        appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(3)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Test command yarn application -list --appTypes apptype1,apptype2
        // --appStates appstate1,appstate2
        sysOutStream.reset();
        Set<String> appType4 = new HashSet<String>();
        appType4.add("YARN");
        appType4.add("NON-YARN");
        EnumSet<YarnApplicationState> appState4 = EnumSet.noneOf(YarnApplicationState.class);
        appState4.add(FINISHED);
        appState4.add(FAILED);
        Mockito.when(client.getApplications(appType4, appState4, appTag)).thenReturn(getApplicationReports(applicationReports, appType4, appState4, appTag, false));
        result = cli.run(new String[]{ "application", "-list", "--appTypes", "YARN,NON-YARN", "--appStates", "FINISHED ,, , FAILED" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplications(appType2, appState2, appTag);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println((((((((("Total number of applications (application-types: " + appType4) + ", states: ") + appState4) + " and tags: ") + appTag) + ")") + ":") + 1));
        pw.print("                Application-Id\t    Application-Name");
        pw.print("\t    Application-Type");
        pw.print("\t      User\t     Queue\t             State\t       ");
        pw.print("Final-State\t       Progress");
        pw.println("\t                       Tracking-URL");
        pw.print("         application_1234_0006\t            ");
        pw.print("appname2\t            NON-YARN\t     user2\t    ");
        pw.print("queue2\t          FINISHED\t         ");
        pw.print("SUCCEEDED\t         63.79%");
        pw.println("\t                                N/A");
        pw.close();
        appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(4)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Test command yarn application -list --appStates with invalid appStates
        sysOutStream.reset();
        result = cli.run(new String[]{ "application", "-list", "--appStates", "FINISHED ,, , INVALID" });
        Assert.assertEquals((-1), result);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println("The application state  INVALID is invalid.");
        pw.print("The valid application state can be one of the following: ");
        StringBuilder sb = new StringBuilder();
        sb.append("ALL,");
        for (YarnApplicationState state : YarnApplicationState.values()) {
            sb.append((state + ","));
        }
        String output = sb.toString();
        pw.println(output.substring(0, ((output.length()) - 1)));
        pw.close();
        appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(4)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Test command yarn application -list --appStates all
        sysOutStream.reset();
        Set<String> appType5 = new HashSet<String>();
        EnumSet<YarnApplicationState> appState5 = EnumSet.noneOf(YarnApplicationState.class);
        appState5.add(FINISHED);
        Mockito.when(client.getApplications(appType5, appState5, appTag)).thenReturn(getApplicationReports(applicationReports, appType5, appState5, appTag, true));
        result = cli.run(new String[]{ "application", "-list", "--appStates", "FINISHED ,, , ALL" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplications(appType5, appState5, appTag);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println((((((((("Total number of applications (application-types: " + appType5) + ", states: ") + appState5) + " and tags: ") + appTag) + ")") + ":") + 6));
        pw.print("                Application-Id\t    Application-Name");
        pw.print("\t    Application-Type");
        pw.print("\t      User\t     Queue\t             State\t       ");
        pw.print("Final-State\t       Progress");
        pw.println("\t                       Tracking-URL");
        pw.print("         application_1234_0005\t             ");
        pw.print("appname\t                YARN\t      user\t     ");
        pw.print("queue\t           RUNNING\t         ");
        pw.print("SUCCEEDED\t         53.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0006\t            ");
        pw.print("appname2\t            NON-YARN\t     user2\t    ");
        pw.print("queue2\t          FINISHED\t         ");
        pw.print("SUCCEEDED\t         63.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0007\t            ");
        pw.print("appname3\t           MAPREDUCE\t     user3\t    ");
        pw.print("queue3\t           RUNNING\t         ");
        pw.print("SUCCEEDED\t         73.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0008\t            ");
        pw.print("appname4\t       NON-MAPREDUCE\t     user4\t    ");
        pw.print("queue4\t            FAILED\t         ");
        pw.print("SUCCEEDED\t         83.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0009\t            ");
        pw.print("appname5\t                HIVE\t     user5\t    ");
        pw.print("queue5\t          ACCEPTED\t            ");
        pw.print("KILLED\t         93.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0010\t            ");
        pw.print("appname6\t                 PIG\t     user6\t    ");
        pw.print("queue6\t         SUBMITTED\t            ");
        pw.print("KILLED\t         99.79%");
        pw.println("\t                                N/A");
        pw.close();
        appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(5)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Test command yarn application user case insensitive
        sysOutStream.reset();
        Set<String> appType6 = new HashSet<String>();
        appType6.add("YARN");
        appType6.add("NON-YARN");
        EnumSet<YarnApplicationState> appState6 = EnumSet.noneOf(YarnApplicationState.class);
        appState6.add(FINISHED);
        Mockito.when(client.getApplications(appType6, appState6, appTag)).thenReturn(getApplicationReports(applicationReports, appType6, appState6, appTag, false));
        result = cli.run(new String[]{ "application", "-list", "-appTypes", "YARN, ,,  NON-YARN", "--appStates", "finished" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplications(appType6, appState6, appTag);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println((((((((("Total number of applications (application-types: " + appType6) + ", states: ") + appState6) + " and tags: ") + appTag) + ")") + ":") + 1));
        pw.print("                Application-Id\t    Application-Name");
        pw.print("\t    Application-Type");
        pw.print("\t      User\t     Queue\t             State\t       ");
        pw.print("Final-State\t       Progress");
        pw.println("\t                       Tracking-URL");
        pw.print("         application_1234_0006\t            ");
        pw.print("appname2\t            NON-YARN\t     user2\t    ");
        pw.print("queue2\t          FINISHED\t         ");
        pw.print("SUCCEEDED\t         63.79%");
        pw.println("\t                                N/A");
        pw.close();
        appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(6)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        // Test command yarn application with tags.
        sysOutStream.reset();
        Set<String> appTag1 = Sets.newHashSet("tag1");
        Mockito.when(client.getApplications(appType1, appState1, appTag1)).thenReturn(getApplicationReports(applicationReports, appType1, appState1, appTag1, false));
        result = cli.run(new String[]{ "application", "-list", "-appTags", "tag1" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplications(appType1, appState1, appTag1);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println((((((((("Total number of applications (application-types: " + appType1) + ", states: ") + appState1) + " and tags: ") + appTag1) + ")") + ":") + 2));
        pw.print("                Application-Id\t    Application-Name");
        pw.print("\t    Application-Type");
        pw.print("\t      User\t     Queue\t             State\t       ");
        pw.print("Final-State\t       Progress");
        pw.println("\t                       Tracking-URL");
        pw.print("         application_1234_0005\t             ");
        pw.print("appname\t                YARN\t      user\t     ");
        pw.print("queue\t           RUNNING\t         ");
        pw.print("SUCCEEDED\t         53.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0007\t            ");
        pw.print("appname3\t           MAPREDUCE\t     user3\t    ");
        pw.print("queue3\t           RUNNING\t         ");
        pw.print("SUCCEEDED\t         73.79%");
        pw.println("\t                                N/A");
        pw.close();
        appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(7)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        EnumSet<YarnApplicationState> appState7 = EnumSet.of(YarnApplicationState.RUNNING, FAILED);
        Mockito.when(client.getApplications(appType1, appState7, appTag1)).thenReturn(getApplicationReports(applicationReports, appType1, appState7, appTag1, false));
        result = cli.run(new String[]{ "application", "-list", "-appStates", "RUNNING,FAILED", "-appTags", "tag1" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplications(appType1, appState7, appTag1);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println((((((((("Total number of applications (application-types: " + appType1) + ", states: ") + appState7) + " and tags: ") + appTag1) + ")") + ":") + 3));
        pw.print("                Application-Id\t    Application-Name");
        pw.print("\t    Application-Type");
        pw.print("\t      User\t     Queue\t             State\t       ");
        pw.print("Final-State\t       Progress");
        pw.println("\t                       Tracking-URL");
        pw.print("         application_1234_0005\t             ");
        pw.print("appname\t                YARN\t      user\t     ");
        pw.print("queue\t           RUNNING\t         ");
        pw.print("SUCCEEDED\t         53.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0007\t            ");
        pw.print("appname3\t           MAPREDUCE\t     user3\t    ");
        pw.print("queue3\t           RUNNING\t         ");
        pw.print("SUCCEEDED\t         73.79%");
        pw.println("\t                                N/A");
        pw.print("         application_1234_0008\t            ");
        pw.print("appname4\t       NON-MAPREDUCE\t     user4\t    ");
        pw.print("queue4\t            FAILED\t         ");
        pw.print("SUCCEEDED\t         83.79%");
        pw.println("\t                                N/A");
        pw.close();
        appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(8)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        Set<String> appType9 = Sets.newHashSet("YARN");
        Set<String> appTag2 = Sets.newHashSet("tag3");
        Mockito.when(client.getApplications(appType9, appState1, appTag2)).thenReturn(getApplicationReports(applicationReports, appType9, appState1, appTag2, false));
        result = cli.run(new String[]{ "application", "-list", "-appTypes", "YARN", "-appTags", "tag3" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplications(appType9, appState1, appTag2);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println((((((((("Total number of applications (application-types: " + appType9) + ", states: ") + appState1) + " and tags: ") + appTag2) + ")") + ":") + 1));
        pw.print("                Application-Id\t    Application-Name");
        pw.print("\t    Application-Type");
        pw.print("\t      User\t     Queue\t             State\t       ");
        pw.print("Final-State\t       Progress");
        pw.println("\t                       Tracking-URL");
        pw.print("         application_1234_0005\t             ");
        pw.print("appname\t                YARN\t      user\t     ");
        pw.print("queue\t           RUNNING\t         ");
        pw.print("SUCCEEDED\t         53.79%");
        pw.println("\t                                N/A");
        pw.close();
        appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(9)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        Set<String> appType10 = Sets.newHashSet("HIVE");
        Set<String> appTag3 = Sets.newHashSet("tag4");
        EnumSet<YarnApplicationState> appState10 = EnumSet.of(ACCEPTED);
        Mockito.when(client.getApplications(appType10, appState10, appTag3)).thenReturn(getApplicationReports(applicationReports, appType10, appState10, appTag3, false));
        result = cli.run(new String[]{ "application", "-list", "-appTypes", "HIVE", "-appStates", "ACCEPTED", "-appTags", "tag4" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getApplications(appType10, appState10, appTag3);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println((((((((("Total number of applications (application-types: " + appType10) + ", states: ") + appState10) + " and tags: ") + appTag3) + ")") + ":") + 1));
        pw.print("                Application-Id\t    Application-Name");
        pw.print("\t    Application-Type");
        pw.print("\t      User\t     Queue\t             State\t       ");
        pw.print("Final-State\t       Progress");
        pw.println("\t                       Tracking-URL");
        pw.print("         application_1234_0009\t            ");
        pw.print("appname5\t                HIVE\t     user5\t    ");
        pw.print("queue5\t          ACCEPTED\t            ");
        pw.print("KILLED\t         93.79%");
        pw.println("\t                                N/A");
        pw.close();
        appsReportStr = baos.toString("UTF-8");
        Assert.assertEquals(appsReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(10)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
    }

    @Test(timeout = 10000)
    public void testAppsHelpCommand() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationCLI spyCli = Mockito.spy(cli);
        int result = spyCli.run(new String[]{ "application", "-help" });
        Assert.assertTrue((result == 0));
        Mockito.verify(spyCli).printUsage(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Options.class));
        Assert.assertEquals(createApplicationCLIHelpMessage(), sysOutStream.toString());
        sysOutStream.reset();
        NodeId nodeId = NodeId.newInstance("host0", 0);
        result = cli.run(new String[]{ "application", "-status", nodeId.toString(), "args" });
        Mockito.verify(spyCli).printUsage(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Options.class));
        Assert.assertEquals(createApplicationCLIHelpMessage(), sysOutStream.toString());
    }

    @Test(timeout = 10000)
    public void testAppAttemptsHelpCommand() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationCLI spyCli = Mockito.spy(cli);
        int result = spyCli.run(new String[]{ "applicationattempt", "-help" });
        Assert.assertTrue((result == 0));
        Mockito.verify(spyCli).printUsage(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Options.class));
        Assert.assertEquals(createApplicationAttemptCLIHelpMessage(), sysOutStream.toString());
        sysOutStream.reset();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        result = cli.run(new String[]{ "applicationattempt", "-list", applicationId.toString(), "args" });
        Mockito.verify(spyCli).printUsage(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Options.class));
        Assert.assertEquals(createApplicationAttemptCLIHelpMessage(), sysOutStream.toString());
        sysOutStream.reset();
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(applicationId, 6);
        result = cli.run(new String[]{ "applicationattempt", "-status", appAttemptId.toString(), "args" });
        Mockito.verify(spyCli).printUsage(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Options.class));
        Assert.assertEquals(createApplicationAttemptCLIHelpMessage(), sysOutStream.toString());
    }

    @Test(timeout = 10000)
    public void testContainersHelpCommand() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationCLI spyCli = Mockito.spy(cli);
        int result = spyCli.run(new String[]{ "container", "-help" });
        Assert.assertTrue((result == 0));
        Mockito.verify(spyCli).printUsage(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Options.class));
        Assert.assertEquals(createContainerCLIHelpMessage(), TestYarnCLI.normalize(sysOutStream.toString()));
        sysOutStream.reset();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(applicationId, 6);
        result = cli.run(new String[]{ "container", "-list", appAttemptId.toString(), "args" });
        Mockito.verify(spyCli).printUsage(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Options.class));
        Assert.assertEquals(createContainerCLIHelpMessage(), TestYarnCLI.normalize(sysOutStream.toString()));
        sysOutStream.reset();
        ContainerId containerId = ContainerId.newContainerId(appAttemptId, 7);
        result = cli.run(new String[]{ "container", "-status", containerId.toString(), "args" });
        Mockito.verify(spyCli).printUsage(ArgumentMatchers.any(String.class), ArgumentMatchers.any(Options.class));
        Assert.assertEquals(createContainerCLIHelpMessage(), TestYarnCLI.normalize(sysOutStream.toString()));
    }

    @Test(timeout = 5000)
    public void testNodesHelpCommand() throws Exception {
        NodeCLI nodeCLI = createAndGetNodeCLI();
        nodeCLI.run(new String[]{  });
        Assert.assertEquals(createNodeCLIHelpMessage(), sysOutStream.toString());
    }

    @Test
    public void testKillApplication() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationReport newApplicationReport2 = ApplicationReport.newInstance(applicationId, ApplicationAttemptId.newInstance(applicationId, 1), "user", "queue", "appname", "host", 124, null, FINISHED, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53789F, "YARN", null);
        Mockito.when(client.getApplicationReport(ArgumentMatchers.any(ApplicationId.class))).thenReturn(newApplicationReport2);
        int result = cli.run(new String[]{ "application", "-kill", applicationId.toString() });
        Assert.assertEquals(0, result);
        Mockito.verify(client, Mockito.times(0)).killApplication(ArgumentMatchers.any(ApplicationId.class));
        Mockito.verify(sysOut).println((("Application " + applicationId) + " has already finished "));
        ApplicationReport newApplicationReport = ApplicationReport.newInstance(applicationId, ApplicationAttemptId.newInstance(applicationId, 1), "user", "queue", "appname", "host", 124, null, YarnApplicationState.RUNNING, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53789F, "YARN", null);
        Mockito.when(client.getApplicationReport(ArgumentMatchers.any(ApplicationId.class))).thenReturn(newApplicationReport);
        result = cli.run(new String[]{ "application", "-kill", applicationId.toString() });
        Assert.assertEquals(0, result);
        Mockito.verify(client).killApplication(ArgumentMatchers.any(ApplicationId.class));
        Mockito.verify(sysOut).println("Killing application application_1234_0005");
        Mockito.doThrow(new ApplicationNotFoundException((("Application with id '" + applicationId) + "' doesn't exist in RM."))).when(client).getApplicationReport(applicationId);
        cli = createAndGetAppCLI();
        try {
            int exitCode = cli.run(new String[]{ "application", "-kill", applicationId.toString() });
            Mockito.verify(sysOut).println((("Application with id '" + applicationId) + "' doesn't exist in RM."));
            Assert.assertNotSame("should return non-zero exit code.", 0, exitCode);
        } catch (ApplicationNotFoundException appEx) {
            Assert.fail((("application -kill should not throw" + "ApplicationNotFoundException. ") + appEx));
        } catch (Exception e) {
            Assert.fail(("Unexpected exception: " + e));
        }
    }

    @Test
    public void testKillApplications() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId1 = ApplicationId.newInstance(1234, 5);
        ApplicationId applicationId2 = ApplicationId.newInstance(1234, 6);
        ApplicationId applicationId3 = ApplicationId.newInstance(1234, 7);
        ApplicationId applicationId4 = ApplicationId.newInstance(1234, 8);
        // Test Scenario 1: Both applications are FINISHED.
        ApplicationReport newApplicationReport1 = ApplicationReport.newInstance(applicationId1, ApplicationAttemptId.newInstance(applicationId1, 1), "user", "queue", "appname", "host", 124, null, FINISHED, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53789F, "YARN", null);
        ApplicationReport newApplicationReport2 = ApplicationReport.newInstance(applicationId2, ApplicationAttemptId.newInstance(applicationId2, 1), "user", "queue", "appname", "host", 124, null, FINISHED, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.34344F, "YARN", null);
        Mockito.when(client.getApplicationReport(applicationId1)).thenReturn(newApplicationReport1);
        Mockito.when(client.getApplicationReport(applicationId2)).thenReturn(newApplicationReport2);
        int result = cli.run(new String[]{ "application", "-kill", ((applicationId1.toString()) + " ") + (applicationId2.toString()) });
        Assert.assertEquals(0, result);
        Mockito.verify(client, Mockito.times(0)).killApplication(applicationId1);
        Mockito.verify(client, Mockito.times(0)).killApplication(applicationId2);
        Mockito.verify(sysOut).println((("Application " + applicationId1) + " has already finished "));
        Mockito.verify(sysOut).println((("Application " + applicationId2) + " has already finished "));
        // Test Scenario 2: Both applications are RUNNING.
        ApplicationReport newApplicationReport3 = ApplicationReport.newInstance(applicationId1, ApplicationAttemptId.newInstance(applicationId1, 1), "user", "queue", "appname", "host", 124, null, YarnApplicationState.RUNNING, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53789F, "YARN", null);
        ApplicationReport newApplicationReport4 = ApplicationReport.newInstance(applicationId2, ApplicationAttemptId.newInstance(applicationId2, 1), "user", "queue", "appname", "host", 124, null, YarnApplicationState.RUNNING, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53345F, "YARN", null);
        Mockito.when(client.getApplicationReport(applicationId1)).thenReturn(newApplicationReport3);
        Mockito.when(client.getApplicationReport(applicationId2)).thenReturn(newApplicationReport4);
        result = cli.run(new String[]{ "application", "-kill", ((applicationId1.toString()) + " ") + (applicationId2.toString()) });
        Assert.assertEquals(0, result);
        Mockito.verify(client).killApplication(applicationId1);
        Mockito.verify(client).killApplication(applicationId2);
        Mockito.verify(sysOut).println("Killing application application_1234_0005");
        Mockito.verify(sysOut).println("Killing application application_1234_0006");
        // Test Scenario 3: Both applications are not present.
        Mockito.doThrow(new ApplicationNotFoundException((("Application with id '" + applicationId3) + "' doesn't exist in RM."))).when(client).getApplicationReport(applicationId3);
        Mockito.doThrow(new ApplicationNotFoundException((("Application with id '" + applicationId4) + "' doesn't exist in RM."))).when(client).getApplicationReport(applicationId4);
        result = cli.run(new String[]{ "application", "-kill", ((applicationId3.toString()) + " ") + (applicationId4.toString()) });
        Assert.assertNotEquals(0, result);
        Mockito.verify(sysOut).println("Application with id 'application_1234_0007' doesn't exist in RM.");
        Mockito.verify(sysOut).println("Application with id 'application_1234_0008' doesn't exist in RM.");
        // Test Scenario 4: one application is not present and other RUNNING
        Mockito.doThrow(new ApplicationNotFoundException((("Application with id '" + applicationId3) + "' doesn't exist in RM."))).when(client).getApplicationReport(applicationId3);
        ApplicationReport newApplicationReport5 = ApplicationReport.newInstance(applicationId1, ApplicationAttemptId.newInstance(applicationId1, 1), "user", "queue", "appname", "host", 124, null, YarnApplicationState.RUNNING, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53345F, "YARN", null);
        Mockito.when(client.getApplicationReport(applicationId1)).thenReturn(newApplicationReport5);
        result = cli.run(new String[]{ "application", "-kill", ((applicationId3.toString()) + " ") + (applicationId1.toString()) });
        Assert.assertEquals(0, result);
        // Test Scenario 5: kill operation with some other command.
        sysOutStream.reset();
        result = cli.run(new String[]{ "application", "--appStates", "RUNNING", "-kill", ((applicationId3.toString()) + " ") + (applicationId1.toString()) });
        Assert.assertEquals((-1), result);
        Assert.assertEquals(createApplicationCLIHelpMessage(), sysOutStream.toString());
    }

    @Test
    public void testKillApplicationsOfDifferentEndStates() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId1 = ApplicationId.newInstance(1234, 5);
        ApplicationId applicationId2 = ApplicationId.newInstance(1234, 6);
        // Scenario: One application is FINISHED and other is RUNNING.
        ApplicationReport newApplicationReport5 = ApplicationReport.newInstance(applicationId1, ApplicationAttemptId.newInstance(applicationId1, 1), "user", "queue", "appname", "host", 124, null, FINISHED, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53789F, "YARN", null);
        ApplicationReport newApplicationReport6 = ApplicationReport.newInstance(applicationId2, ApplicationAttemptId.newInstance(applicationId2, 1), "user", "queue", "appname", "host", 124, null, YarnApplicationState.RUNNING, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53345F, "YARN", null);
        Mockito.when(client.getApplicationReport(applicationId1)).thenReturn(newApplicationReport5);
        Mockito.when(client.getApplicationReport(applicationId2)).thenReturn(newApplicationReport6);
        int result = cli.run(new String[]{ "application", "-kill", ((applicationId1.toString()) + " ") + (applicationId2.toString()) });
        Assert.assertEquals(0, result);
        Mockito.verify(client, Mockito.times(1)).killApplication(applicationId2);
        Mockito.verify(sysOut).println((("Application " + applicationId1) + " has already finished "));
        Mockito.verify(sysOut).println("Killing application application_1234_0006");
    }

    @Test
    public void testMoveApplicationAcrossQueues() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationReport newApplicationReport2 = ApplicationReport.newInstance(applicationId, ApplicationAttemptId.newInstance(applicationId, 1), "user", "queue", "appname", "host", 124, null, FINISHED, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53789F, "YARN", null);
        Mockito.when(client.getApplicationReport(ArgumentMatchers.any(ApplicationId.class))).thenReturn(newApplicationReport2);
        int result = cli.run(new String[]{ "application", "-movetoqueue", applicationId.toString(), "-queue", "targetqueue" });
        Assert.assertEquals(0, result);
        Mockito.verify(client, Mockito.times(0)).moveApplicationAcrossQueues(ArgumentMatchers.any(ApplicationId.class), ArgumentMatchers.any(String.class));
        Mockito.verify(sysOut).println((("Application " + applicationId) + " has already finished "));
        ApplicationReport newApplicationReport = ApplicationReport.newInstance(applicationId, ApplicationAttemptId.newInstance(applicationId, 1), "user", "queue", "appname", "host", 124, null, YarnApplicationState.RUNNING, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53789F, "YARN", null);
        Mockito.when(client.getApplicationReport(ArgumentMatchers.any(ApplicationId.class))).thenReturn(newApplicationReport);
        result = cli.run(new String[]{ "application", "-movetoqueue", applicationId.toString(), "-queue", "targetqueue" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).moveApplicationAcrossQueues(ArgumentMatchers.any(ApplicationId.class), ArgumentMatchers.any(String.class));
        Mockito.verify(sysOut).println("Moving application application_1234_0005 to queue targetqueue");
        Mockito.verify(sysOut).println("Successfully completed move.");
        Mockito.doThrow(new ApplicationNotFoundException((("Application with id '" + applicationId) + "' doesn't exist in RM."))).when(client).moveApplicationAcrossQueues(applicationId, "targetqueue");
        cli = createAndGetAppCLI();
        try {
            result = cli.run(new String[]{ "application", "-movetoqueue", applicationId.toString(), "-queue", "targetqueue" });
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof ApplicationNotFoundException));
            Assert.assertEquals((("Application with id '" + applicationId) + "' doesn't exist in RM."), ex.getMessage());
        }
    }

    @Test
    public void testMoveApplicationAcrossQueuesWithNewCommand() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationReport newApplicationReport2 = ApplicationReport.newInstance(applicationId, ApplicationAttemptId.newInstance(applicationId, 1), "user", "queue", "appname", "host", 124, null, FINISHED, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53789F, "YARN", null);
        Mockito.when(client.getApplicationReport(ArgumentMatchers.any(ApplicationId.class))).thenReturn(newApplicationReport2);
        int result = cli.run(new String[]{ "application", "-appId", applicationId.toString(), "-changeQueue", "targetqueue" });
        Assert.assertEquals(0, result);
        Mockito.verify(client, Mockito.times(0)).moveApplicationAcrossQueues(ArgumentMatchers.any(ApplicationId.class), ArgumentMatchers.any(String.class));
        Mockito.verify(sysOut).println((("Application " + applicationId) + " has already finished "));
        ApplicationReport newApplicationReport = ApplicationReport.newInstance(applicationId, ApplicationAttemptId.newInstance(applicationId, 1), "user", "queue", "appname", "host", 124, null, YarnApplicationState.RUNNING, "diagnostics", "url", 0, 0, 0, SUCCEEDED, null, "N/A", 0.53789F, "YARN", null);
        Mockito.when(client.getApplicationReport(ArgumentMatchers.any(ApplicationId.class))).thenReturn(newApplicationReport);
        result = cli.run(new String[]{ "application", "-appId", applicationId.toString(), "-changeQueue", "targetqueue" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).moveApplicationAcrossQueues(ArgumentMatchers.any(ApplicationId.class), ArgumentMatchers.any(String.class));
        Mockito.verify(sysOut).println("Moving application application_1234_0005 to queue targetqueue");
        Mockito.verify(sysOut).println("Successfully completed move.");
        Mockito.doThrow(new ApplicationNotFoundException((("Application with id '" + applicationId) + "' doesn't exist in RM."))).when(client).moveApplicationAcrossQueues(applicationId, "targetqueue");
        cli = createAndGetAppCLI();
        try {
            result = cli.run(new String[]{ "application", "-appId", applicationId.toString(), "-changeQueue", "targetqueue" });
            Assert.fail();
        } catch (Exception ex) {
            Assert.assertTrue((ex instanceof ApplicationNotFoundException));
            Assert.assertEquals((("Application with id '" + applicationId) + "' doesn't exist in RM."), ex.getMessage());
        }
    }

    @Test
    public void testListClusterNodes() throws Exception {
        List<NodeReport> nodeReports = new ArrayList<NodeReport>();
        nodeReports.addAll(getNodeReports(1, NEW));
        nodeReports.addAll(getNodeReports(2, NodeState.RUNNING));
        nodeReports.addAll(getNodeReports(1, UNHEALTHY));
        nodeReports.addAll(getNodeReports(1, DECOMMISSIONED));
        nodeReports.addAll(getNodeReports(1, REBOOTED));
        nodeReports.addAll(getNodeReports(1, LOST));
        NodeCLI cli = createAndGetNodeCLI();
        Set<NodeState> nodeStates = new HashSet<NodeState>();
        nodeStates.add(NEW);
        NodeState[] states = nodeStates.toArray(new NodeState[0]);
        Mockito.when(client.getNodeReports(states)).thenReturn(getNodeReports(nodeReports, nodeStates));
        int result = cli.run(new String[]{ "-list", "-states", "NEW" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports(states);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Total Nodes:1");
        pw.print("         Node-Id\t     Node-State\tNode-Http-Address\t");
        pw.println("Number-of-Running-Containers");
        pw.print("         host0:0\t            NEW\t       host1:8888\t");
        pw.println("                           0");
        pw.close();
        String nodesReportStr = baos.toString("UTF-8");
        Assert.assertEquals(nodesReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(1)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        nodeStates.clear();
        nodeStates.add(NodeState.RUNNING);
        states = nodeStates.toArray(new NodeState[0]);
        Mockito.when(client.getNodeReports(states)).thenReturn(getNodeReports(nodeReports, nodeStates));
        result = cli.run(new String[]{ "-list", "-states", "RUNNING" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports(states);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println("Total Nodes:2");
        pw.print("         Node-Id\t     Node-State\tNode-Http-Address\t");
        pw.println("Number-of-Running-Containers");
        pw.print("         host0:0\t        RUNNING\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host1:0\t        RUNNING\t       host1:8888\t");
        pw.println("                           0");
        pw.close();
        nodesReportStr = baos.toString("UTF-8");
        Assert.assertEquals(nodesReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(2)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        result = cli.run(new String[]{ "-list" });
        Assert.assertEquals(0, result);
        Assert.assertEquals(nodesReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(3)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        result = cli.run(new String[]{ "-list", "-showDetails" });
        Assert.assertEquals(0, result);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println("Total Nodes:2");
        pw.print("         Node-Id\t     Node-State\tNode-Http-Address\t");
        pw.println("Number-of-Running-Containers");
        pw.print("         host0:0\t        RUNNING\t       host1:8888\t");
        pw.println("                           0");
        pw.println("Detailed Node Information :");
        pw.println("\tConfigured Resources : <memory:0, vCores:0>");
        pw.println("\tAllocated Resources : <memory:0, vCores:0>");
        pw.println("\tResource Utilization by Node : PMem:2048 MB, VMem:4096 MB, VCores:8.0");
        pw.println("\tResource Utilization by Containers : PMem:1024 MB, VMem:2048 MB, VCores:4.0");
        pw.println("\tNode-Labels : ");
        pw.print("         host1:0\t        RUNNING\t       host1:8888\t");
        pw.println("                           0");
        pw.println("Detailed Node Information :");
        pw.println("\tConfigured Resources : <memory:0, vCores:0>");
        pw.println("\tAllocated Resources : <memory:0, vCores:0>");
        pw.println("\tResource Utilization by Node : PMem:2048 MB, VMem:4096 MB, VCores:8.0");
        pw.println("\tResource Utilization by Containers : PMem:1024 MB, VMem:2048 MB, VCores:4.0");
        pw.println("\tNode-Labels : ");
        pw.close();
        nodesReportStr = baos.toString("UTF-8");
        Assert.assertEquals(nodesReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(4)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        nodeStates.clear();
        nodeStates.add(UNHEALTHY);
        states = nodeStates.toArray(new NodeState[0]);
        Mockito.when(client.getNodeReports(states)).thenReturn(getNodeReports(nodeReports, nodeStates));
        result = cli.run(new String[]{ "-list", "-states", "UNHEALTHY" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports(states);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println("Total Nodes:1");
        pw.print("         Node-Id\t     Node-State\tNode-Http-Address\t");
        pw.println("Number-of-Running-Containers");
        pw.print("         host0:0\t      UNHEALTHY\t       host1:8888\t");
        pw.println("                           0");
        pw.close();
        nodesReportStr = baos.toString("UTF-8");
        Assert.assertEquals(nodesReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(5)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        nodeStates.clear();
        nodeStates.add(DECOMMISSIONED);
        states = nodeStates.toArray(new NodeState[0]);
        Mockito.when(client.getNodeReports(states)).thenReturn(getNodeReports(nodeReports, nodeStates));
        result = cli.run(new String[]{ "-list", "-states", "DECOMMISSIONED" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports(states);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println("Total Nodes:1");
        pw.print("         Node-Id\t     Node-State\tNode-Http-Address\t");
        pw.println("Number-of-Running-Containers");
        pw.print("         host0:0\t DECOMMISSIONED\t       host1:8888\t");
        pw.println("                           0");
        pw.close();
        nodesReportStr = baos.toString("UTF-8");
        Assert.assertEquals(nodesReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(6)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        nodeStates.clear();
        nodeStates.add(REBOOTED);
        states = nodeStates.toArray(new NodeState[0]);
        Mockito.when(client.getNodeReports(states)).thenReturn(getNodeReports(nodeReports, nodeStates));
        result = cli.run(new String[]{ "-list", "-states", "REBOOTED" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports(states);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println("Total Nodes:1");
        pw.print("         Node-Id\t     Node-State\tNode-Http-Address\t");
        pw.println("Number-of-Running-Containers");
        pw.print("         host0:0\t       REBOOTED\t       host1:8888\t");
        pw.println("                           0");
        pw.close();
        nodesReportStr = baos.toString("UTF-8");
        Assert.assertEquals(nodesReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(7)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        nodeStates.clear();
        nodeStates.add(LOST);
        states = nodeStates.toArray(new NodeState[0]);
        Mockito.when(client.getNodeReports(states)).thenReturn(getNodeReports(nodeReports, nodeStates));
        result = cli.run(new String[]{ "-list", "-states", "LOST" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports(states);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println("Total Nodes:1");
        pw.print("         Node-Id\t     Node-State\tNode-Http-Address\t");
        pw.println("Number-of-Running-Containers");
        pw.print("         host0:0\t           LOST\t       host1:8888\t");
        pw.println("                           0");
        pw.close();
        nodesReportStr = baos.toString("UTF-8");
        Assert.assertEquals(nodesReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(8)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        nodeStates.clear();
        nodeStates.add(NEW);
        nodeStates.add(NodeState.RUNNING);
        nodeStates.add(LOST);
        nodeStates.add(REBOOTED);
        states = nodeStates.toArray(new NodeState[0]);
        Mockito.when(client.getNodeReports(states)).thenReturn(getNodeReports(nodeReports, nodeStates));
        result = cli.run(new String[]{ "-list", "-states", "NEW,RUNNING,LOST,REBOOTED" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports(states);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println("Total Nodes:5");
        pw.print("         Node-Id\t     Node-State\tNode-Http-Address\t");
        pw.println("Number-of-Running-Containers");
        pw.print("         host0:0\t            NEW\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host0:0\t        RUNNING\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host1:0\t        RUNNING\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host0:0\t       REBOOTED\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host0:0\t           LOST\t       host1:8888\t");
        pw.println("                           0");
        pw.close();
        nodesReportStr = baos.toString("UTF-8");
        Assert.assertEquals(nodesReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(9)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        nodeStates.clear();
        for (NodeState s : NodeState.values()) {
            nodeStates.add(s);
        }
        states = nodeStates.toArray(new NodeState[0]);
        Mockito.when(client.getNodeReports(states)).thenReturn(getNodeReports(nodeReports, nodeStates));
        result = cli.run(new String[]{ "-list", "-All" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports(states);
        baos = new ByteArrayOutputStream();
        pw = new PrintWriter(baos);
        pw.println("Total Nodes:7");
        pw.print("         Node-Id\t     Node-State\tNode-Http-Address\t");
        pw.println("Number-of-Running-Containers");
        pw.print("         host0:0\t            NEW\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host0:0\t        RUNNING\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host1:0\t        RUNNING\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host0:0\t      UNHEALTHY\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host0:0\t DECOMMISSIONED\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host0:0\t       REBOOTED\t       host1:8888\t");
        pw.println("                           0");
        pw.print("         host0:0\t           LOST\t       host1:8888\t");
        pw.println("                           0");
        pw.close();
        nodesReportStr = baos.toString("UTF-8");
        Assert.assertEquals(nodesReportStr, sysOutStream.toString());
        Mockito.verify(sysOut, Mockito.times(10)).write(ArgumentMatchers.any(byte[].class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        sysOutStream.reset();
        result = cli.run(new String[]{ "-list", "-states", "InvalidState" });
        Assert.assertEquals((-1), result);
    }

    @Test
    public void testNodeStatus() throws Exception {
        NodeId nodeId = NodeId.newInstance("host0", 0);
        Mockito.when(client.getNodeReports()).thenReturn(getNodeReports(3, NodeState.RUNNING, false, false, false));
        NodeCLI cli = createAndGetNodeCLI();
        int result = cli.run(new String[]{ "-status", nodeId.toString() });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Node Report : ");
        pw.println("\tNode-Id : host0:0");
        pw.println("\tRack : rack1");
        pw.println("\tNode-State : RUNNING");
        pw.println("\tNode-Http-Address : host1:8888");
        pw.println(("\tLast-Health-Update : " + (DateFormatUtils.format(new Date(0), "E dd/MMM/yy hh:mm:ss:SSzz"))));
        pw.println("\tHealth-Report : ");
        pw.println("\tContainers : 0");
        pw.println("\tMemory-Used : 0MB");
        pw.println("\tMemory-Capacity : 0MB");
        pw.println("\tCPU-Used : 0 vcores");
        pw.println("\tCPU-Capacity : 0 vcores");
        pw.println("\tNode-Labels : a,b,c,x,y,z");
        pw.println("\tNode Attributes : rm.yarn.io/GPU(STRING)=ARM");
        pw.println("\t                  rm.yarn.io/CPU(STRING)=ARM");
        pw.println("\tResource Utilization by Node : PMem:2048 MB, VMem:4096 MB, VCores:8.0");
        pw.println("\tResource Utilization by Containers : PMem:1024 MB, VMem:2048 MB, VCores:4.0");
        pw.close();
        String nodeStatusStr = baos.toString("UTF-8");
        Mockito.verify(sysOut, Mockito.times(1)).println(ArgumentMatchers.isA(String.class));
        Mockito.verify(sysOut).println(nodeStatusStr);
    }

    @Test
    public void testNodeStatusWithEmptyNodeLabels() throws Exception {
        NodeId nodeId = NodeId.newInstance("host0", 0);
        Mockito.when(client.getNodeReports()).thenReturn(getNodeReports(3, NodeState.RUNNING));
        NodeCLI cli = createAndGetNodeCLI();
        int result = cli.run(new String[]{ "-status", nodeId.toString() });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Node Report : ");
        pw.println("\tNode-Id : host0:0");
        pw.println("\tRack : rack1");
        pw.println("\tNode-State : RUNNING");
        pw.println("\tNode-Http-Address : host1:8888");
        pw.println(("\tLast-Health-Update : " + (DateFormatUtils.format(new Date(0), "E dd/MMM/yy hh:mm:ss:SSzz"))));
        pw.println("\tHealth-Report : ");
        pw.println("\tContainers : 0");
        pw.println("\tMemory-Used : 0MB");
        pw.println("\tMemory-Capacity : 0MB");
        pw.println("\tCPU-Used : 0 vcores");
        pw.println("\tCPU-Capacity : 0 vcores");
        pw.println("\tNode-Labels : ");
        pw.println("\tNode Attributes : ");
        pw.println("\tResource Utilization by Node : PMem:2048 MB, VMem:4096 MB, VCores:8.0");
        pw.println("\tResource Utilization by Containers : PMem:1024 MB, VMem:2048 MB, VCores:4.0");
        pw.close();
        String nodeStatusStr = baos.toString("UTF-8");
        Mockito.verify(sysOut, Mockito.times(1)).println(ArgumentMatchers.isA(String.class));
        Mockito.verify(sysOut).println(nodeStatusStr);
    }

    @Test
    public void testNodeStatusWithEmptyResourceUtilization() throws Exception {
        NodeId nodeId = NodeId.newInstance("host0", 0);
        Mockito.when(client.getNodeReports()).thenReturn(getNodeReports(3, NodeState.RUNNING, false, true, true));
        NodeCLI cli = createAndGetNodeCLI();
        int result = cli.run(new String[]{ "-status", nodeId.toString() });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Node Report : ");
        pw.println("\tNode-Id : host0:0");
        pw.println("\tRack : rack1");
        pw.println("\tNode-State : RUNNING");
        pw.println("\tNode-Http-Address : host1:8888");
        pw.println(("\tLast-Health-Update : " + (DateFormatUtils.format(new Date(0), "E dd/MMM/yy hh:mm:ss:SSzz"))));
        pw.println("\tHealth-Report : ");
        pw.println("\tContainers : 0");
        pw.println("\tMemory-Used : 0MB");
        pw.println("\tMemory-Capacity : 0MB");
        pw.println("\tCPU-Used : 0 vcores");
        pw.println("\tCPU-Capacity : 0 vcores");
        pw.println("\tNode-Labels : a,b,c,x,y,z");
        pw.println("\tNode Attributes : ");
        pw.println("\tResource Utilization by Node : ");
        pw.println("\tResource Utilization by Containers : ");
        pw.close();
        String nodeStatusStr = baos.toString("UTF-8");
        Mockito.verify(sysOut, Mockito.times(1)).println(ArgumentMatchers.isA(String.class));
        Mockito.verify(sysOut).println(nodeStatusStr);
    }

    @Test
    public void testAbsentNodeStatus() throws Exception {
        NodeId nodeId = NodeId.newInstance("Absenthost0", 0);
        Mockito.when(client.getNodeReports()).thenReturn(getNodeReports(0, NodeState.RUNNING));
        NodeCLI cli = createAndGetNodeCLI();
        int result = cli.run(new String[]{ "-status", nodeId.toString() });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getNodeReports();
        Mockito.verify(sysOut, Mockito.times(1)).println(ArgumentMatchers.isA(String.class));
        Mockito.verify(sysOut).println(("Could not find the node report for node id : " + (nodeId.toString())));
    }

    @Test
    public void testAppCLIUsageInfo() throws Exception {
        verifyUsageInfo(new ApplicationCLI());
    }

    @Test
    public void testNodeCLIUsageInfo() throws Exception {
        verifyUsageInfo(new NodeCLI());
    }

    @Test
    public void testMissingArguments() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        int result = cli.run(new String[]{ "application", "-status" });
        Assert.assertEquals(result, (-1));
        Assert.assertEquals(String.format("Missing argument for options%n%1s", createApplicationCLIHelpMessage()), sysOutStream.toString());
        sysOutStream.reset();
        result = cli.run(new String[]{ "applicationattempt", "-status" });
        Assert.assertEquals(result, (-1));
        Assert.assertEquals(String.format("Missing argument for options%n%1s", createApplicationAttemptCLIHelpMessage()), sysOutStream.toString());
        sysOutStream.reset();
        result = cli.run(new String[]{ "container", "-status" });
        Assert.assertEquals(result, (-1));
        Assert.assertEquals(String.format("Missing argument for options %1s", createContainerCLIHelpMessage()), TestYarnCLI.normalize(sysOutStream.toString()));
        sysOutStream.reset();
        NodeCLI nodeCLI = createAndGetNodeCLI();
        result = nodeCLI.run(new String[]{ "-status" });
        Assert.assertEquals(result, (-1));
        Assert.assertEquals(String.format("Missing argument for options%n%1s", createNodeCLIHelpMessage()), sysOutStream.toString());
    }

    @Test
    public void testGetQueueInfo() throws Exception {
        QueueCLI cli = createAndGetQueueCLI();
        Set<String> nodeLabels = new HashSet<String>();
        nodeLabels.add("GPU");
        nodeLabels.add("JDK_7");
        QueueInfo queueInfo = QueueInfo.newInstance("queueA", 0.4F, 0.8F, 0.5F, null, null, QueueState.RUNNING, nodeLabels, "GPU", null, false, null, false);
        Mockito.when(client.getQueueInfo(ArgumentMatchers.any(String.class))).thenReturn(queueInfo);
        int result = cli.run(new String[]{ "-status", "queueA" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getQueueInfo("queueA");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Queue Information : ");
        pw.println(("Queue Name : " + "queueA"));
        pw.println(("\tState : " + "RUNNING"));
        pw.println(("\tCapacity : " + "40.0%"));
        pw.println(("\tCurrent Capacity : " + "50.0%"));
        pw.println(("\tMaximum Capacity : " + "80.0%"));
        pw.println(("\tDefault Node Label expression : " + "GPU"));
        pw.println(("\tAccessible Node Labels : " + "JDK_7,GPU"));
        pw.println(("\tPreemption : " + "enabled"));
        pw.println(("\tIntra-queue Preemption : " + "enabled"));
        pw.close();
        String queueInfoStr = baos.toString("UTF-8");
        Assert.assertEquals(queueInfoStr, sysOutStream.toString());
    }

    @Test
    public void testGetQueueInfoOverrideIntraQueuePreemption() throws Exception {
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        ReservationSystemTestUtil.setupQueueConfiguration(conf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        conf.setBoolean(RM_SCHEDULER_ENABLE_MONITORS, true);
        conf.set(RM_SCHEDULER_MONITOR_POLICIES, ("org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity." + "ProportionalCapacityPreemptionPolicy"));
        // Turn on cluster-wide intra-queue preemption
        conf.setBoolean(INTRAQUEUE_PREEMPTION_ENABLED, true);
        // Disable intra-queue preemption for all queues
        conf.setBoolean(((CapacitySchedulerConfiguration.PREFIX) + "root.intra-queue-preemption.disable_preemption"), true);
        // Enable intra-queue preemption for the a1 queue
        conf.setBoolean(((CapacitySchedulerConfiguration.PREFIX) + "root.a.a1.intra-queue-preemption.disable_preemption"), false);
        MiniYARNCluster cluster = new MiniYARNCluster("testGetQueueInfoOverrideIntraQueuePreemption", 2, 1, 1);
        YarnClient yarnClient = null;
        try {
            cluster.init(conf);
            cluster.start();
            final Configuration yarnConf = cluster.getConfig();
            yarnClient = YarnClient.createYarnClient();
            yarnClient.init(yarnConf);
            yarnClient.start();
            QueueCLI cli = createAndGetQueueCLI(yarnClient);
            sysOutStream.reset();
            // Get status for the root.a queue
            int result = cli.run(new String[]{ "-status", "a" });
            Assert.assertEquals(0, result);
            String queueStatusOut = sysOutStream.toString();
            Assert.assertTrue(queueStatusOut.contains("\tPreemption : enabled"));
            // In-queue preemption is disabled at the "root.a" queue level
            Assert.assertTrue(queueStatusOut.contains("Intra-queue Preemption : disabled"));
            cli = createAndGetQueueCLI(yarnClient);
            sysOutStream.reset();
            // Get status for the root.a.a1 queue
            result = cli.run(new String[]{ "-status", "a1" });
            Assert.assertEquals(0, result);
            queueStatusOut = sysOutStream.toString();
            Assert.assertTrue(queueStatusOut.contains("\tPreemption : enabled"));
            // In-queue preemption is enabled at the "root.a.a1" queue level
            Assert.assertTrue(queueStatusOut.contains("Intra-queue Preemption : enabled"));
        } finally {
            // clean-up
            if (yarnClient != null) {
                yarnClient.stop();
            }
            cluster.stop();
            cluster.close();
        }
    }

    @Test
    public void testGetQueueInfoPreemptionEnabled() throws Exception {
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        ReservationSystemTestUtil.setupQueueConfiguration(conf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        conf.setBoolean(RM_SCHEDULER_ENABLE_MONITORS, true);
        conf.set(RM_SCHEDULER_MONITOR_POLICIES, ("org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity." + "ProportionalCapacityPreemptionPolicy"));
        conf.setBoolean(INTRAQUEUE_PREEMPTION_ENABLED, true);
        MiniYARNCluster cluster = new MiniYARNCluster("testGetQueueInfoPreemptionEnabled", 2, 1, 1);
        YarnClient yarnClient = null;
        try {
            cluster.init(conf);
            cluster.start();
            final Configuration yarnConf = cluster.getConfig();
            yarnClient = YarnClient.createYarnClient();
            yarnClient.init(yarnConf);
            yarnClient.start();
            QueueCLI cli = createAndGetQueueCLI(yarnClient);
            sysOutStream.reset();
            int result = cli.run(new String[]{ "-status", "a1" });
            Assert.assertEquals(0, result);
            String queueStatusOut = sysOutStream.toString();
            Assert.assertTrue(queueStatusOut.contains("\tPreemption : enabled"));
            Assert.assertTrue(queueStatusOut.contains("Intra-queue Preemption : enabled"));
        } finally {
            // clean-up
            if (yarnClient != null) {
                yarnClient.stop();
            }
            cluster.stop();
            cluster.close();
        }
    }

    @Test
    public void testGetQueueInfoPreemptionDisabled() throws Exception {
        CapacitySchedulerConfiguration conf = new CapacitySchedulerConfiguration();
        ReservationSystemTestUtil.setupQueueConfiguration(conf);
        conf.setClass(RM_SCHEDULER, CapacityScheduler.class, ResourceScheduler.class);
        conf.setBoolean(RM_SCHEDULER_ENABLE_MONITORS, true);
        conf.set(RM_SCHEDULER_MONITOR_POLICIES, ("org.apache.hadoop.yarn.server.resourcemanager.monitor.capacity." + "ProportionalCapacityPreemptionPolicy"));
        conf.setBoolean(RM_SCHEDULER_ENABLE_MONITORS, true);
        conf.setBoolean(((PREFIX) + "root.a.a1.disable_preemption"), true);
        try (MiniYARNCluster cluster = new MiniYARNCluster("testReservationAPIs", 2, 1, 1);YarnClient yarnClient = YarnClient.createYarnClient()) {
            cluster.init(conf);
            cluster.start();
            final Configuration yarnConf = cluster.getConfig();
            yarnClient.init(yarnConf);
            yarnClient.start();
            QueueCLI cli = createAndGetQueueCLI(yarnClient);
            sysOutStream.reset();
            int result = cli.run(new String[]{ "-status", "a1" });
            Assert.assertEquals(0, result);
            String queueStatusOut = sysOutStream.toString();
            Assert.assertTrue(queueStatusOut.contains("\tPreemption : disabled"));
            Assert.assertTrue(queueStatusOut.contains("Intra-queue Preemption : disabled"));
        }
    }

    @Test
    public void testGetQueueInfoWithEmptyNodeLabel() throws Exception {
        QueueCLI cli = createAndGetQueueCLI();
        QueueInfo queueInfo = QueueInfo.newInstance("queueA", 0.4F, 0.8F, 0.5F, null, null, QueueState.RUNNING, null, null, null, true, null, true);
        Mockito.when(client.getQueueInfo(ArgumentMatchers.any(String.class))).thenReturn(queueInfo);
        int result = cli.run(new String[]{ "-status", "queueA" });
        Assert.assertEquals(0, result);
        Mockito.verify(client).getQueueInfo("queueA");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println("Queue Information : ");
        pw.println(("Queue Name : " + "queueA"));
        pw.println(("\tState : " + "RUNNING"));
        pw.println(("\tCapacity : " + "40.0%"));
        pw.println(("\tCurrent Capacity : " + "50.0%"));
        pw.println(("\tMaximum Capacity : " + "80.0%"));
        pw.println(("\tDefault Node Label expression : " + (NodeLabel.DEFAULT_NODE_LABEL_PARTITION)));
        pw.println("\tAccessible Node Labels : ");
        pw.println(("\tPreemption : " + "disabled"));
        pw.println(("\tIntra-queue Preemption : " + "disabled"));
        pw.close();
        String queueInfoStr = baos.toString("UTF-8");
        Assert.assertEquals(queueInfoStr, sysOutStream.toString());
    }

    @Test
    public void testGetQueueInfoWithNonExistedQueue() throws Exception {
        String queueName = "non-existed-queue";
        QueueCLI cli = createAndGetQueueCLI();
        Mockito.when(client.getQueueInfo(ArgumentMatchers.any(String.class))).thenReturn(null);
        int result = cli.run(new String[]{ "-status", queueName });
        Assert.assertEquals((-1), result);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        pw.println((("Cannot get queue from RM by queueName = " + queueName) + ", please check."));
        pw.close();
        String queueInfoStr = baos.toString("UTF-8");
        Assert.assertEquals(queueInfoStr, sysOutStream.toString());
    }

    @Test
    public void testGetApplicationAttemptReportException() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationAttemptId attemptId1 = ApplicationAttemptId.newInstance(applicationId, 1);
        Mockito.when(client.getApplicationAttemptReport(attemptId1)).thenThrow(new ApplicationNotFoundException((("History file for application" + applicationId) + " is not found")));
        int exitCode = cli.run(new String[]{ "applicationattempt", "-status", attemptId1.toString() });
        Mockito.verify(sysOut).println((("Application for AppAttempt with id '" + attemptId1) + "' doesn't exist in RM or Timeline Server."));
        Assert.assertNotSame("should return non-zero exit code.", 0, exitCode);
        ApplicationAttemptId attemptId2 = ApplicationAttemptId.newInstance(applicationId, 2);
        Mockito.when(client.getApplicationAttemptReport(attemptId2)).thenThrow(new org.apache.hadoop.yarn.exceptions.ApplicationAttemptNotFoundException((("History file for application attempt" + attemptId2) + " is not found")));
        exitCode = cli.run(new String[]{ "applicationattempt", "-status", attemptId2.toString() });
        Mockito.verify(sysOut).println((("Application Attempt with id '" + attemptId2) + "' doesn't exist in RM or Timeline Server."));
        Assert.assertNotSame("should return non-zero exit code.", 0, exitCode);
    }

    @Test
    public void testGetContainerReportException() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(applicationId, 1);
        long cntId = 1;
        ContainerId containerId1 = ContainerId.newContainerId(attemptId, (cntId++));
        Mockito.when(client.getContainerReport(containerId1)).thenThrow(new ApplicationNotFoundException((("History file for application" + applicationId) + " is not found")));
        int exitCode = cli.run(new String[]{ "container", "-status", containerId1.toString() });
        Mockito.verify(sysOut).println((("Application for Container with id '" + containerId1) + "' doesn't exist in RM or Timeline Server."));
        Assert.assertNotSame("should return non-zero exit code.", 0, exitCode);
        ContainerId containerId2 = ContainerId.newContainerId(attemptId, (cntId++));
        Mockito.when(client.getContainerReport(containerId2)).thenThrow(new org.apache.hadoop.yarn.exceptions.ApplicationAttemptNotFoundException((("History file for application attempt" + attemptId) + " is not found")));
        exitCode = cli.run(new String[]{ "container", "-status", containerId2.toString() });
        Mockito.verify(sysOut).println((("Application Attempt for Container with id '" + containerId2) + "' doesn't exist in RM or Timeline Server."));
        Assert.assertNotSame("should return non-zero exit code.", 0, exitCode);
        ContainerId containerId3 = ContainerId.newContainerId(attemptId, (cntId++));
        Mockito.when(client.getContainerReport(containerId3)).thenThrow(new org.apache.hadoop.yarn.exceptions.ContainerNotFoundException((("History file for container" + containerId3) + " is not found")));
        exitCode = cli.run(new String[]{ "container", "-status", containerId3.toString() });
        Mockito.verify(sysOut).println((("Container with id '" + containerId3) + "' doesn't exist in RM or Timeline Server."));
        Assert.assertNotSame("should return non-zero exit code.", 0, exitCode);
    }

    @Test(timeout = 60000)
    public void testUpdateApplicationPriority() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 6);
        ApplicationReport appReport = ApplicationReport.newInstance(applicationId, ApplicationAttemptId.newInstance(applicationId, 1), "user", "queue", "appname", "host", 124, null, YarnApplicationState.RUNNING, "diagnostics", "url", 0, 0, 0, FinalApplicationStatus.UNDEFINED, null, "N/A", 0.53789F, "YARN", null);
        Mockito.when(client.getApplicationReport(ArgumentMatchers.any(ApplicationId.class))).thenReturn(appReport);
        int result = cli.run(new String[]{ "application", "-appId", applicationId.toString(), "-updatePriority", "1" });
        Assert.assertEquals(result, 0);
        Mockito.verify(client).updateApplicationPriority(ArgumentMatchers.any(ApplicationId.class), ArgumentMatchers.any(Priority.class));
    }

    @Test
    public void testFailApplicationAttempt() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        int exitCode = cli.run(new String[]{ "applicationattempt", "-fail", "appattempt_1444199730803_0003_000001" });
        Assert.assertEquals(0, exitCode);
        Mockito.verify(client).failApplicationAttempt(ArgumentMatchers.any(ApplicationAttemptId.class));
        Mockito.verifyNoMoreInteractions(client);
    }

    @Test
    public void testAppAttemptReportWhileContainerIsNotAssigned() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 5);
        ApplicationAttemptId attemptId = ApplicationAttemptId.newInstance(applicationId, 1);
        ApplicationAttemptReport attemptReport = ApplicationAttemptReport.newInstance(attemptId, "host", 124, "url", "oUrl", "diagnostics", SCHEDULED, null, 1000L, 2000L);
        Mockito.when(client.getApplicationAttemptReport(ArgumentMatchers.any(ApplicationAttemptId.class))).thenReturn(attemptReport);
        int result = cli.run(new String[]{ "applicationattempt", "-status", attemptId.toString() });
        Assert.assertEquals(0, result);
        result = cli.run(new String[]{ "applicationattempt", "-list", applicationId.toString() });
        Assert.assertEquals(0, result);
    }

    @Test(timeout = 60000)
    public void testUpdateApplicationTimeout() throws Exception {
        ApplicationCLI cli = createAndGetAppCLI();
        ApplicationId applicationId = ApplicationId.newInstance(1234, 6);
        UpdateApplicationTimeoutsResponse response = Mockito.mock(UpdateApplicationTimeoutsResponse.class);
        String formatISO8601 = Times.formatISO8601(((System.currentTimeMillis()) + (5 * 1000)));
        Mockito.when(response.getApplicationTimeouts()).thenReturn(Collections.singletonMap(LIFETIME, formatISO8601));
        Mockito.when(client.updateApplicationTimeouts(ArgumentMatchers.any(UpdateApplicationTimeoutsRequest.class))).thenReturn(response);
        int result = cli.run(new String[]{ "application", "-appId", applicationId.toString(), "-updateLifetime", "10" });
        Assert.assertEquals(result, 0);
        Mockito.verify(client).updateApplicationTimeouts(ArgumentMatchers.any(UpdateApplicationTimeoutsRequest.class));
    }
}

