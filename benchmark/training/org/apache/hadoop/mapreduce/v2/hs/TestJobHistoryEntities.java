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


import JobState.FAILED;
import JobState.SUCCEEDED;
import TaskType.MAP;
import TaskType.REDUCE;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.JobACLsManager;
import org.apache.hadoop.mapred.TaskCompletionEvent;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.JobInfo;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.JobReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptReport;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskReport;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.hs.HistoryFileManager.HistoryFileInfo;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;


@RunWith(Parameterized.class)
public class TestJobHistoryEntities {
    private final String historyFileName = "job_1329348432655_0001-1329348443227-user-Sleep+job-1329348468601-10-1-SUCCEEDED-default.jhist";

    private final String historyFileNameZeroReduceTasks = "job_1416424547277_0002-1416424775281-root-TeraGen-1416424785433-2-0-SUCCEEDED-default-1416424779349.jhist";

    private final String confFileName = "job_1329348432655_0001_conf.xml";

    private final Configuration conf = new Configuration();

    private final JobACLsManager jobAclsManager = new JobACLsManager(conf);

    private boolean loadTasks;

    private JobId jobId = MRBuilderUtils.newJobId(1329348432655L, 1, 1);

    Path fullHistoryPath = new Path(this.getClass().getClassLoader().getResource(historyFileName).getFile());

    Path fullHistoryPathZeroReduces = new Path(this.getClass().getClassLoader().getResource(historyFileNameZeroReduceTasks).getFile());

    Path fullConfPath = new Path(this.getClass().getClassLoader().getResource(confFileName).getFile());

    private CompletedJob completedJob;

    public TestJobHistoryEntities(boolean loadTasks) throws Exception {
        this.loadTasks = loadTasks;
    }

    /* Verify some expected values based on the history file */
    @Test(timeout = 100000)
    public void testCompletedJob() throws Exception {
        HistoryFileInfo info = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(info.getConfFile()).thenReturn(fullConfPath);
        Mockito.when(info.getHistoryFile()).thenReturn(fullHistoryPath);
        // Re-initialize to verify the delayed load.
        completedJob = new CompletedJob(conf, jobId, fullHistoryPath, loadTasks, "user", info, jobAclsManager);
        // Verify tasks loaded based on loadTask parameter.
        Assert.assertEquals(loadTasks, completedJob.tasksLoaded.get());
        Assert.assertEquals(1, completedJob.getAMInfos().size());
        Assert.assertEquals(10, completedJob.getCompletedMaps());
        Assert.assertEquals(1, completedJob.getCompletedReduces());
        Assert.assertEquals(12, completedJob.getTasks().size());
        // Verify tasks loaded at this point.
        Assert.assertEquals(true, completedJob.tasksLoaded.get());
        Assert.assertEquals(10, completedJob.getTasks(MAP).size());
        Assert.assertEquals(2, completedJob.getTasks(REDUCE).size());
        Assert.assertEquals("user", completedJob.getUserName());
        Assert.assertEquals(SUCCEEDED, completedJob.getState());
        JobReport jobReport = completedJob.getReport();
        Assert.assertEquals("user", jobReport.getUser());
        Assert.assertEquals(SUCCEEDED, jobReport.getJobState());
        Assert.assertEquals(fullHistoryPath.toString(), jobReport.getHistoryFile());
    }

    @Test(timeout = 100000)
    public void testCopmletedJobReportWithZeroTasks() throws Exception {
        HistoryFileInfo info = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(info.getConfFile()).thenReturn(fullConfPath);
        Mockito.when(info.getHistoryFile()).thenReturn(fullHistoryPathZeroReduces);
        completedJob = new CompletedJob(conf, jobId, fullHistoryPathZeroReduces, loadTasks, "user", info, jobAclsManager);
        JobReport jobReport = completedJob.getReport();
        // Make sure that the number reduces (completed and total) are equal to zero.
        Assert.assertEquals(0, completedJob.getTotalReduces());
        Assert.assertEquals(0, completedJob.getCompletedReduces());
        // Verify that the reduce progress is 1.0 (not NaN)
        Assert.assertEquals(1.0, jobReport.getReduceProgress(), 0.001);
        Assert.assertEquals(fullHistoryPathZeroReduces.toString(), jobReport.getHistoryFile());
    }

    @Test(timeout = 10000)
    public void testCompletedTask() throws Exception {
        HistoryFileInfo info = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(info.getConfFile()).thenReturn(fullConfPath);
        completedJob = new CompletedJob(conf, jobId, fullHistoryPath, loadTasks, "user", info, jobAclsManager);
        TaskId mt1Id = MRBuilderUtils.newTaskId(jobId, 0, MAP);
        TaskId rt1Id = MRBuilderUtils.newTaskId(jobId, 0, REDUCE);
        Map<TaskId, Task> mapTasks = completedJob.getTasks(MAP);
        Map<TaskId, Task> reduceTasks = completedJob.getTasks(REDUCE);
        Assert.assertEquals(10, mapTasks.size());
        Assert.assertEquals(2, reduceTasks.size());
        Task mt1 = mapTasks.get(mt1Id);
        Assert.assertEquals(1, mt1.getAttempts().size());
        Assert.assertEquals(TaskState.SUCCEEDED, mt1.getState());
        TaskReport mt1Report = mt1.getReport();
        Assert.assertEquals(TaskState.SUCCEEDED, mt1Report.getTaskState());
        Assert.assertEquals(mt1Id, mt1Report.getTaskId());
        Task rt1 = reduceTasks.get(rt1Id);
        Assert.assertEquals(1, rt1.getAttempts().size());
        Assert.assertEquals(TaskState.SUCCEEDED, rt1.getState());
        TaskReport rt1Report = rt1.getReport();
        Assert.assertEquals(TaskState.SUCCEEDED, rt1Report.getTaskState());
        Assert.assertEquals(rt1Id, rt1Report.getTaskId());
    }

    @Test(timeout = 10000)
    public void testCompletedTaskAttempt() throws Exception {
        HistoryFileInfo info = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(info.getConfFile()).thenReturn(fullConfPath);
        completedJob = new CompletedJob(conf, jobId, fullHistoryPath, loadTasks, "user", info, jobAclsManager);
        TaskId mt1Id = MRBuilderUtils.newTaskId(jobId, 0, MAP);
        TaskId rt1Id = MRBuilderUtils.newTaskId(jobId, 0, REDUCE);
        TaskAttemptId mta1Id = MRBuilderUtils.newTaskAttemptId(mt1Id, 0);
        TaskAttemptId rta1Id = MRBuilderUtils.newTaskAttemptId(rt1Id, 0);
        Task mt1 = completedJob.getTask(mt1Id);
        Task rt1 = completedJob.getTask(rt1Id);
        TaskAttempt mta1 = mt1.getAttempt(mta1Id);
        Assert.assertEquals(TaskAttemptState.SUCCEEDED, mta1.getState());
        Assert.assertEquals("localhost:45454", mta1.getAssignedContainerMgrAddress());
        Assert.assertEquals("localhost:9999", mta1.getNodeHttpAddress());
        TaskAttemptReport mta1Report = mta1.getReport();
        Assert.assertEquals(TaskAttemptState.SUCCEEDED, mta1Report.getTaskAttemptState());
        Assert.assertEquals("localhost", mta1Report.getNodeManagerHost());
        Assert.assertEquals(45454, mta1Report.getNodeManagerPort());
        Assert.assertEquals(9999, mta1Report.getNodeManagerHttpPort());
        TaskAttempt rta1 = rt1.getAttempt(rta1Id);
        Assert.assertEquals(TaskAttemptState.SUCCEEDED, rta1.getState());
        Assert.assertEquals("localhost:45454", rta1.getAssignedContainerMgrAddress());
        Assert.assertEquals("localhost:9999", rta1.getNodeHttpAddress());
        TaskAttemptReport rta1Report = rta1.getReport();
        Assert.assertEquals(TaskAttemptState.SUCCEEDED, rta1Report.getTaskAttemptState());
        Assert.assertEquals("localhost", rta1Report.getNodeManagerHost());
        Assert.assertEquals(45454, rta1Report.getNodeManagerPort());
        Assert.assertEquals(9999, rta1Report.getNodeManagerHttpPort());
    }

    /**
     * Simple test of some methods of CompletedJob
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 30000)
    public void testGetTaskAttemptCompletionEvent() throws Exception {
        HistoryFileInfo info = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(info.getConfFile()).thenReturn(fullConfPath);
        completedJob = new CompletedJob(conf, jobId, fullHistoryPath, loadTasks, "user", info, jobAclsManager);
        TaskCompletionEvent[] events = completedJob.getMapAttemptCompletionEvents(0, 1000);
        Assert.assertEquals(10, completedJob.getMapAttemptCompletionEvents(0, 10).length);
        int currentEventId = 0;
        for (TaskCompletionEvent taskAttemptCompletionEvent : events) {
            int eventId = taskAttemptCompletionEvent.getEventId();
            Assert.assertTrue((eventId >= currentEventId));
            currentEventId = eventId;
        }
        Assert.assertNull(completedJob.loadConfFile());
        // job name
        Assert.assertEquals("Sleep job", completedJob.getName());
        // queue name
        Assert.assertEquals("default", completedJob.getQueueName());
        // progress
        Assert.assertEquals(1.0, completedJob.getProgress(), 0.001);
        // 12 rows in answer
        Assert.assertEquals(12, completedJob.getTaskAttemptCompletionEvents(0, 1000).length);
        // select first 10 rows
        Assert.assertEquals(10, completedJob.getTaskAttemptCompletionEvents(0, 10).length);
        // select 5-10 rows include 5th
        Assert.assertEquals(7, completedJob.getTaskAttemptCompletionEvents(5, 10).length);
        // without errors
        Assert.assertEquals(1, completedJob.getDiagnostics().size());
        Assert.assertEquals("", completedJob.getDiagnostics().get(0));
        Assert.assertEquals(0, completedJob.getJobACLs().size());
    }

    @Test(timeout = 30000)
    public void testCompletedJobWithDiagnostics() throws Exception {
        final String jobError = "Job Diagnostics";
        JobInfo jobInfo = Mockito.spy(new JobInfo());
        Mockito.when(jobInfo.getErrorInfo()).thenReturn(jobError);
        Mockito.when(jobInfo.getJobStatus()).thenReturn(FAILED.toString());
        Mockito.when(jobInfo.getAMInfos()).thenReturn(Collections.<JobHistoryParser.AMInfo>emptyList());
        final JobHistoryParser mockParser = Mockito.mock(JobHistoryParser.class);
        Mockito.when(mockParser.parse()).thenReturn(jobInfo);
        HistoryFileInfo info = Mockito.mock(HistoryFileInfo.class);
        Mockito.when(info.getConfFile()).thenReturn(fullConfPath);
        Mockito.when(info.getHistoryFile()).thenReturn(fullHistoryPath);
        CompletedJob job = new CompletedJob(conf, jobId, fullHistoryPath, loadTasks, "user", info, jobAclsManager) {
            @Override
            protected JobHistoryParser createJobHistoryParser(Path historyFileAbsolute) throws IOException {
                return mockParser;
            }
        };
        Assert.assertEquals(jobError, job.getReport().getDiagnostics());
    }
}

