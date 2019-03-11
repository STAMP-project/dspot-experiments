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
package org.apache.hadoop.mapreduce.v2.hs;


import CommonConfigurationKeysPublic.NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY;
import ExitUtil.ExitException;
import JobState.SUCCEEDED;
import STATE.INITED;
import STATE.STARTED;
import STATE.STOPPED;
import TaskCounter.PHYSICAL_MEMORY_BYTES;
import TaskType.REDUCE;
import java.io.IOException;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.v2.api.MRClientProtocol;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetDiagnosticsRequest;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetDiagnosticsResponse;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskAttemptCompletionEventsRequest;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskAttemptCompletionEventsResponse;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskAttemptReportRequest;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskAttemptReportResponse;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskReportRequest;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskReportResponse;
import org.apache.hadoop.mapreduce.v2.api.protocolrecords.GetTaskReportsRequest;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.app.MRApp;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.net.DNSToSwitchMapping;
import org.apache.hadoop.service.Service;
import org.apache.hadoop.util.ExitUtil;
import org.apache.hadoop.yarn.factories.RecordFactory;
import org.apache.hadoop.yarn.factory.providers.RecordFactoryProvider;
import org.apache.hadoop.yarn.util.RackResolver;
import org.junit.Assert;
import org.junit.Test;


/* test JobHistoryServer protocols.... */
public class TestJobHistoryServer {
    private static RecordFactory recordFactory = RecordFactoryProvider.getRecordFactory(null);

    JobHistoryServer historyServer = null;

    // simple test init/start/stop   JobHistoryServer. Status should change.
    @Test(timeout = 50000)
    public void testStartStopServer() throws Exception {
        historyServer = new JobHistoryServer();
        Configuration config = new Configuration();
        historyServer.init(config);
        Assert.assertEquals(INITED, historyServer.getServiceState());
        HistoryClientService historyService = historyServer.getClientService();
        Assert.assertNotNull(historyServer.getClientService());
        Assert.assertEquals(INITED, historyService.getServiceState());
        historyServer.start();
        Assert.assertEquals(STARTED, historyServer.getServiceState());
        Assert.assertEquals(STARTED, historyService.getServiceState());
        historyServer.stop();
        Assert.assertEquals(STOPPED, historyServer.getServiceState());
        Assert.assertNotNull(historyService.getClientHandler().getConnectAddress());
    }

    // Test reports of  JobHistoryServer. History server should get log files from  MRApp and read them
    @Test(timeout = 50000)
    public void testReports() throws Exception {
        Configuration config = new Configuration();
        config.setClass(NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY, TestJobHistoryParsing.MyResolver.class, DNSToSwitchMapping.class);
        RackResolver.init(config);
        MRApp app = new TestJobHistoryEvents.MRAppWithHistory(1, 1, true, this.getClass().getName(), true);
        app.submit(config);
        Job job = app.getContext().getAllJobs().values().iterator().next();
        app.waitForState(job, SUCCEEDED);
        historyServer = new JobHistoryServer();
        historyServer.init(config);
        historyServer.start();
        // search JobHistory  service
        JobHistory jobHistory = null;
        for (Service service : historyServer.getServices()) {
            if (service instanceof JobHistory) {
                jobHistory = ((JobHistory) (service));
            }
        }
        Map<JobId, Job> jobs = jobHistory.getAllJobs();
        Assert.assertEquals(1, jobs.size());
        Assert.assertEquals("job_0_0000", jobs.keySet().iterator().next().toString());
        Task task = job.getTasks().values().iterator().next();
        TaskAttempt attempt = task.getAttempts().values().iterator().next();
        HistoryClientService historyService = historyServer.getClientService();
        MRClientProtocol protocol = historyService.getClientHandler();
        GetTaskAttemptReportRequest gtarRequest = TestJobHistoryServer.recordFactory.newRecordInstance(GetTaskAttemptReportRequest.class);
        // test getTaskAttemptReport
        TaskAttemptId taId = attempt.getID();
        taId.setTaskId(task.getID());
        taId.getTaskId().setJobId(job.getID());
        gtarRequest.setTaskAttemptId(taId);
        GetTaskAttemptReportResponse response = protocol.getTaskAttemptReport(gtarRequest);
        Assert.assertEquals("container_0_0000_01_000000", response.getTaskAttemptReport().getContainerId().toString());
        Assert.assertTrue(response.getTaskAttemptReport().getDiagnosticInfo().isEmpty());
        // counters
        Assert.assertNotNull(response.getTaskAttemptReport().getCounters().getCounter(PHYSICAL_MEMORY_BYTES));
        Assert.assertEquals(taId.toString(), response.getTaskAttemptReport().getTaskAttemptId().toString());
        // test getTaskReport
        GetTaskReportRequest request = TestJobHistoryServer.recordFactory.newRecordInstance(GetTaskReportRequest.class);
        TaskId taskId = task.getID();
        taskId.setJobId(job.getID());
        request.setTaskId(taskId);
        GetTaskReportResponse reportResponse = protocol.getTaskReport(request);
        Assert.assertEquals("", reportResponse.getTaskReport().getDiagnosticsList().iterator().next());
        // progress
        Assert.assertEquals(1.0F, reportResponse.getTaskReport().getProgress(), 0.01);
        // report has corrected taskId
        Assert.assertEquals(taskId.toString(), reportResponse.getTaskReport().getTaskId().toString());
        // Task state should be SUCCEEDED
        Assert.assertEquals(TaskState.SUCCEEDED, reportResponse.getTaskReport().getTaskState());
        // For invalid jobid, throw IOException
        GetTaskReportsRequest gtreportsRequest = TestJobHistoryServer.recordFactory.newRecordInstance(GetTaskReportsRequest.class);
        gtreportsRequest.setJobId(TypeConverter.toYarn(JobID.forName("job_1415730144495_0001")));
        gtreportsRequest.setTaskType(REDUCE);
        try {
            protocol.getTaskReports(gtreportsRequest);
            Assert.fail("IOException not thrown for invalid job id");
        } catch (IOException e) {
            // Expected
        }
        // test getTaskAttemptCompletionEvents
        GetTaskAttemptCompletionEventsRequest taskAttemptRequest = TestJobHistoryServer.recordFactory.newRecordInstance(GetTaskAttemptCompletionEventsRequest.class);
        taskAttemptRequest.setJobId(job.getID());
        GetTaskAttemptCompletionEventsResponse taskAttemptCompletionEventsResponse = protocol.getTaskAttemptCompletionEvents(taskAttemptRequest);
        Assert.assertEquals(0, taskAttemptCompletionEventsResponse.getCompletionEventCount());
        // test getDiagnostics
        GetDiagnosticsRequest diagnosticRequest = TestJobHistoryServer.recordFactory.newRecordInstance(GetDiagnosticsRequest.class);
        diagnosticRequest.setTaskAttemptId(taId);
        GetDiagnosticsResponse diagnosticResponse = protocol.getDiagnostics(diagnosticRequest);
        // it is strange : why one empty string ?
        Assert.assertEquals(1, diagnosticResponse.getDiagnosticsCount());
        Assert.assertEquals("", diagnosticResponse.getDiagnostics(0));
    }

    // test launch method
    @Test(timeout = 60000)
    public void testLaunch() throws Exception {
        ExitUtil.disableSystemExit();
        try {
            historyServer = JobHistoryServer.launchJobHistoryServer(new String[0]);
        } catch (ExitUtil e) {
            Assert.assertEquals(0, e.status);
            ExitUtil.resetFirstExitException();
            Assert.fail();
        }
    }
}

