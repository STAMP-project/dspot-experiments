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


import JHAdminConfig.MR_HS_JHIST_FORMAT;
import JobImpl.JOB_KILLED_DIAG;
import JobState.FAILED;
import JobState.KILLED;
import JobState.SUCCEEDED;
import MRJobConfig.MAP_FAILURES_MAX_PERCENT;
import MRJobConfig.REDUCE_FAILURES_MAXPERCENT;
import Service.STATE.STOPPED;
import TaskType.MAP;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.jobhistory.EventReader;
import org.apache.hadoop.mapreduce.jobhistory.HistoryEvent;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.JobInfo;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.TaskAttemptInfo;
import org.apache.hadoop.mapreduce.jobhistory.JobHistoryParser.TaskInfo;
import org.apache.hadoop.mapreduce.jobhistory.JobUnsuccessfulCompletionEvent;
import org.apache.hadoop.mapreduce.jobhistory.TaskFailedEvent;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskAttemptId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskId;
import org.apache.hadoop.mapreduce.v2.api.records.TaskType;
import org.apache.hadoop.mapreduce.v2.api.records.impl.pb.JobIdPBImpl;
import org.apache.hadoop.mapreduce.v2.api.records.impl.pb.TaskIdPBImpl;
import org.apache.hadoop.mapreduce.v2.api.records.org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.v2.app.MRApp;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.app.job.Task;
import org.apache.hadoop.mapreduce.v2.app.job.TaskAttempt;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobEventType;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEventType;
import org.apache.hadoop.mapreduce.v2.app.job.event.TaskEventType;
import org.apache.hadoop.mapreduce.v2.hs.HistoryFileManager.HistoryFileInfo;
import org.apache.hadoop.mapreduce.v2.hs.webapp.dao.JobsInfo;
import org.apache.hadoop.mapreduce.v2.jobhistory.JobIndexInfo;
import org.apache.hadoop.net.DNSToSwitchMapping;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.yarn.util.RackResolver;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestJobHistoryParsing {
    private static final Logger LOG = LoggerFactory.getLogger(TestJobHistoryParsing.class);

    private static final String RACK_NAME = "/MyRackName";

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    public static class MyResolver implements DNSToSwitchMapping {
        @Override
        public List<String> resolve(List<String> names) {
            return Arrays.asList(new String[]{ TestJobHistoryParsing.RACK_NAME });
        }

        @Override
        public void reloadCachedMappings() {
        }

        @Override
        public void reloadCachedMappings(List<String> names) {
        }
    }

    @Test(timeout = 50000)
    public void testJobInfo() throws Exception {
        JobInfo info = new JobInfo();
        Assert.assertEquals("NORMAL", info.getPriority());
        info.printAll();
    }

    @Test(timeout = 300000)
    public void testHistoryParsing() throws Exception {
        TestJobHistoryParsing.LOG.info("STARTING testHistoryParsing()");
        try {
            checkHistoryParsing(2, 1, 2);
        } finally {
            TestJobHistoryParsing.LOG.info("FINISHED testHistoryParsing()");
        }
    }

    @Test(timeout = 50000)
    public void testHistoryParsingWithParseErrors() throws Exception {
        TestJobHistoryParsing.LOG.info("STARTING testHistoryParsingWithParseErrors()");
        try {
            checkHistoryParsing(3, 0, 2);
        } finally {
            TestJobHistoryParsing.LOG.info("FINISHED testHistoryParsingWithParseErrors()");
        }
    }

    @Test(timeout = 30000)
    public void testHistoryParsingForFailedAttempts() throws Exception {
        TestJobHistoryParsing.LOG.info("STARTING testHistoryParsingForFailedAttempts");
        try {
            Configuration conf = new Configuration();
            conf.setClass(NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY, TestJobHistoryParsing.MyResolver.class, DNSToSwitchMapping.class);
            RackResolver.init(conf);
            MRApp app = new TestJobHistoryParsing.MRAppWithHistoryWithFailedAttempt(2, 1, true, this.getClass().getName(), true);
            app.submit(conf);
            Job job = app.getContext().getAllJobs().values().iterator().next();
            JobId jobId = job.getID();
            app.waitForState(job, SUCCEEDED);
            // make sure all events are flushed
            app.waitForState(STOPPED);
            JobHistory jobHistory = new JobHistory();
            jobHistory.init(conf);
            HistoryFileInfo fileInfo = jobHistory.getJobFileInfo(jobId);
            JobHistoryParser parser;
            JobInfo jobInfo;
            synchronized(fileInfo) {
                Path historyFilePath = fileInfo.getHistoryFile();
                FSDataInputStream in = null;
                FileContext fc = null;
                try {
                    fc = FileContext.getFileContext(conf);
                    in = fc.open(fc.makeQualified(historyFilePath));
                } catch (IOException ioe) {
                    TestJobHistoryParsing.LOG.info(("Can not open history file: " + historyFilePath), ioe);
                    throw new Exception("Can not open History File");
                }
                parser = new JobHistoryParser(in);
                jobInfo = parser.parse();
            }
            Exception parseException = parser.getParseException();
            Assert.assertNull(("Caught an expected exception " + parseException), parseException);
            int noOffailedAttempts = 0;
            Map<TaskID, TaskInfo> allTasks = jobInfo.getAllTasks();
            for (Task task : job.getTasks().values()) {
                TaskInfo taskInfo = allTasks.get(TypeConverter.fromYarn(task.getID()));
                for (TaskAttempt taskAttempt : task.getAttempts().values()) {
                    TaskAttemptInfo taskAttemptInfo = taskInfo.getAllTaskAttempts().get(TypeConverter.fromYarn(taskAttempt.getID()));
                    // Verify rack-name for all task attempts
                    Assert.assertEquals("rack-name is incorrect", taskAttemptInfo.getRackname(), TestJobHistoryParsing.RACK_NAME);
                    if (taskAttemptInfo.getTaskStatus().equals("FAILED")) {
                        noOffailedAttempts++;
                    }
                }
            }
            Assert.assertEquals("No of Failed tasks doesn't match.", 2, noOffailedAttempts);
        } finally {
            TestJobHistoryParsing.LOG.info("FINISHED testHistoryParsingForFailedAttempts");
        }
    }

    @Test(timeout = 30000)
    public void testHistoryParsingForKilledAndFailedAttempts() throws Exception {
        MRApp app = null;
        JobHistory jobHistory = null;
        TestJobHistoryParsing.LOG.info("STARTING testHistoryParsingForKilledAndFailedAttempts");
        try {
            Configuration conf = new Configuration();
            conf.setClass(NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY, TestJobHistoryParsing.MyResolver.class, DNSToSwitchMapping.class);
            conf.set(MR_HS_JHIST_FORMAT, "json");
            // "CommitterEventHandler" thread could be slower in some cases,
            // which might cause a failed map/reduce task to fail the job
            // immediately (see JobImpl.checkJobAfterTaskCompletion()). If there are
            // killed events in progress, those will not be counted. Instead,
            // we allow a 50% failure rate, so the job will always succeed and kill
            // events will not be ignored.
            conf.setInt(MAP_FAILURES_MAX_PERCENT, 50);
            conf.setInt(REDUCE_FAILURES_MAXPERCENT, 50);
            RackResolver.init(conf);
            app = new TestJobHistoryParsing.MRAppWithHistoryWithFailedAndKilledTask(3, 3, true, this.getClass().getName(), true);
            app.submit(conf);
            Job job = app.getContext().getAllJobs().values().iterator().next();
            JobId jobId = job.getID();
            app.waitForState(job, SUCCEEDED);
            // make sure all events are flushed
            app.waitForState(STOPPED);
            jobHistory = new JobHistory();
            jobHistory.init(conf);
            HistoryFileInfo fileInfo = jobHistory.getJobFileInfo(jobId);
            JobHistoryParser parser;
            JobInfo jobInfo;
            synchronized(fileInfo) {
                Path historyFilePath = fileInfo.getHistoryFile();
                FSDataInputStream in = null;
                FileContext fc = null;
                try {
                    fc = FileContext.getFileContext(conf);
                    in = fc.open(fc.makeQualified(historyFilePath));
                } catch (IOException ioe) {
                    TestJobHistoryParsing.LOG.info(("Can not open history file: " + historyFilePath), ioe);
                    throw new Exception("Can not open History File");
                }
                parser = new JobHistoryParser(in);
                jobInfo = parser.parse();
            }
            Exception parseException = parser.getParseException();
            Assert.assertNull(("Caught an expected exception " + parseException), parseException);
            Assert.assertEquals("FailedMaps", 1, jobInfo.getFailedMaps());
            Assert.assertEquals("KilledMaps", 1, jobInfo.getKilledMaps());
            Assert.assertEquals("FailedReduces", 1, jobInfo.getFailedReduces());
            Assert.assertEquals("KilledReduces", 1, jobInfo.getKilledReduces());
        } finally {
            TestJobHistoryParsing.LOG.info("FINISHED testHistoryParsingForKilledAndFailedAttempts");
            if (app != null) {
                app.close();
            }
            if (jobHistory != null) {
                jobHistory.close();
            }
        }
    }

    @Test(timeout = 60000)
    public void testCountersForFailedTask() throws Exception {
        TestJobHistoryParsing.LOG.info("STARTING testCountersForFailedTask");
        try {
            Configuration conf = new Configuration();
            conf.setClass(NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY, TestJobHistoryParsing.MyResolver.class, DNSToSwitchMapping.class);
            RackResolver.init(conf);
            MRApp app = new TestJobHistoryParsing.MRAppWithHistoryWithFailedTask(2, 1, true, this.getClass().getName(), true);
            app.submit(conf);
            Job job = app.getContext().getAllJobs().values().iterator().next();
            JobId jobId = job.getID();
            app.waitForState(job, FAILED);
            // make sure all events are flushed
            app.waitForState(STOPPED);
            JobHistory jobHistory = new JobHistory();
            jobHistory.init(conf);
            HistoryFileInfo fileInfo = jobHistory.getJobFileInfo(jobId);
            JobHistoryParser parser;
            JobInfo jobInfo;
            synchronized(fileInfo) {
                Path historyFilePath = fileInfo.getHistoryFile();
                FSDataInputStream in = null;
                FileContext fc = null;
                try {
                    fc = FileContext.getFileContext(conf);
                    in = fc.open(fc.makeQualified(historyFilePath));
                } catch (IOException ioe) {
                    TestJobHistoryParsing.LOG.info(("Can not open history file: " + historyFilePath), ioe);
                    throw new Exception("Can not open History File");
                }
                parser = new JobHistoryParser(in);
                jobInfo = parser.parse();
            }
            Exception parseException = parser.getParseException();
            Assert.assertNull(("Caught an expected exception " + parseException), parseException);
            for (Map.Entry<TaskID, TaskInfo> entry : jobInfo.getAllTasks().entrySet()) {
                TaskId yarnTaskID = TypeConverter.toYarn(entry.getKey());
                CompletedTask ct = new CompletedTask(yarnTaskID, entry.getValue());
                Assert.assertNotNull("completed task report has null counters", ct.getReport().getCounters());
            }
            final List<String> originalDiagnostics = job.getDiagnostics();
            final String historyError = jobInfo.getErrorInfo();
            Assert.assertTrue("No original diagnostics for a failed job", ((originalDiagnostics != null) && (!(originalDiagnostics.isEmpty()))));
            Assert.assertNotNull("No history error info for a failed job ", historyError);
            for (String diagString : originalDiagnostics) {
                Assert.assertTrue(historyError.contains(diagString));
            }
        } finally {
            TestJobHistoryParsing.LOG.info("FINISHED testCountersForFailedTask");
        }
    }

    @Test(timeout = 60000)
    public void testDiagnosticsForKilledJob() throws Exception {
        TestJobHistoryParsing.LOG.info("STARTING testDiagnosticsForKilledJob");
        try {
            final Configuration conf = new Configuration();
            conf.setClass(NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY, TestJobHistoryParsing.MyResolver.class, DNSToSwitchMapping.class);
            RackResolver.init(conf);
            MRApp app = new TestJobHistoryParsing.MRAppWithHistoryWithJobKilled(2, 1, true, this.getClass().getName(), true);
            app.submit(conf);
            Job job = app.getContext().getAllJobs().values().iterator().next();
            JobId jobId = job.getID();
            app.waitForState(job, KILLED);
            // make sure all events are flushed
            app.waitForState(STOPPED);
            JobHistory jobHistory = new JobHistory();
            jobHistory.init(conf);
            HistoryFileInfo fileInfo = jobHistory.getJobFileInfo(jobId);
            JobHistoryParser parser;
            JobInfo jobInfo;
            synchronized(fileInfo) {
                Path historyFilePath = fileInfo.getHistoryFile();
                FSDataInputStream in = null;
                FileContext fc = null;
                try {
                    fc = FileContext.getFileContext(conf);
                    in = fc.open(fc.makeQualified(historyFilePath));
                } catch (IOException ioe) {
                    TestJobHistoryParsing.LOG.info(("Can not open history file: " + historyFilePath), ioe);
                    throw new Exception("Can not open History File");
                }
                parser = new JobHistoryParser(in);
                jobInfo = parser.parse();
            }
            Exception parseException = parser.getParseException();
            Assert.assertNull(("Caught an expected exception " + parseException), parseException);
            final List<String> originalDiagnostics = job.getDiagnostics();
            final String historyError = jobInfo.getErrorInfo();
            Assert.assertTrue("No original diagnostics for a failed job", ((originalDiagnostics != null) && (!(originalDiagnostics.isEmpty()))));
            Assert.assertNotNull("No history error info for a failed job ", historyError);
            for (String diagString : originalDiagnostics) {
                Assert.assertTrue(historyError.contains(diagString));
            }
            Assert.assertTrue("No killed message in diagnostics", historyError.contains(JOB_KILLED_DIAG));
        } finally {
            TestJobHistoryParsing.LOG.info("FINISHED testDiagnosticsForKilledJob");
        }
    }

    @Test(timeout = 50000)
    public void testScanningOldDirs() throws Exception {
        TestJobHistoryParsing.LOG.info("STARTING testScanningOldDirs");
        try {
            Configuration conf = new Configuration();
            conf.setClass(NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY, TestJobHistoryParsing.MyResolver.class, DNSToSwitchMapping.class);
            RackResolver.init(conf);
            MRApp app = new TestJobHistoryEvents.MRAppWithHistory(1, 1, true, this.getClass().getName(), true);
            app.submit(conf);
            Job job = app.getContext().getAllJobs().values().iterator().next();
            JobId jobId = job.getID();
            TestJobHistoryParsing.LOG.info(("JOBID is " + (TypeConverter.fromYarn(jobId).toString())));
            app.waitForState(job, SUCCEEDED);
            // make sure all events are flushed
            app.waitForState(STOPPED);
            TestJobHistoryParsing.HistoryFileManagerForTest hfm = new TestJobHistoryParsing.HistoryFileManagerForTest();
            hfm.init(conf);
            HistoryFileInfo fileInfo = hfm.getFileInfo(jobId);
            Assert.assertNotNull("Unable to locate job history", fileInfo);
            // force the manager to "forget" the job
            hfm.deleteJobFromJobListCache(fileInfo);
            final int msecPerSleep = 10;
            int msecToSleep = 10 * 1000;
            while ((fileInfo.isMovePending()) && (msecToSleep > 0)) {
                Assert.assertTrue((!(fileInfo.didMoveFail())));
                msecToSleep -= msecPerSleep;
                Thread.sleep(msecPerSleep);
            } 
            Assert.assertTrue("Timeout waiting for history move", (msecToSleep > 0));
            fileInfo = hfm.getFileInfo(jobId);
            stop();
            Assert.assertNotNull("Unable to locate old job history", fileInfo);
            Assert.assertTrue("HistoryFileManager not shutdown properly", hfm.moveToDoneExecutor.isTerminated());
        } finally {
            TestJobHistoryParsing.LOG.info("FINISHED testScanningOldDirs");
        }
    }

    static class MRAppWithHistoryWithFailedAttempt extends TestJobHistoryEvents.MRAppWithHistory {
        public MRAppWithHistoryWithFailedAttempt(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart) {
            super(maps, reduces, autoComplete, testName, cleanOnStart);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void attemptLaunched(TaskAttemptId attemptID) {
            if (((attemptID.getTaskId().getId()) == 0) && ((attemptID.getId()) == 0)) {
                getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptFailEvent(attemptID));
            } else {
                getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(attemptID, TaskAttemptEventType.TA_DONE));
            }
        }
    }

    static class MRAppWithHistoryWithFailedTask extends TestJobHistoryEvents.MRAppWithHistory {
        public MRAppWithHistoryWithFailedTask(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart) {
            super(maps, reduces, autoComplete, testName, cleanOnStart);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void attemptLaunched(TaskAttemptId attemptID) {
            if ((attemptID.getTaskId().getId()) == 0) {
                getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptFailEvent(attemptID));
            } else {
                getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(attemptID, TaskAttemptEventType.TA_DONE));
            }
        }
    }

    static class MRAppWithHistoryWithFailedAndKilledTask extends TestJobHistoryEvents.MRAppWithHistory {
        MRAppWithHistoryWithFailedAndKilledTask(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart) {
            super(maps, reduces, autoComplete, testName, cleanOnStart);
        }

        @Override
        protected void attemptLaunched(TaskAttemptId attemptID) {
            final int taskId = attemptID.getTaskId().getId();
            final TaskType taskType = attemptID.getTaskId().getTaskType();
            // map #0 --> kill
            // reduce #0 --> fail
            if ((taskType == (TaskType.MAP)) && (taskId == 0)) {
                getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskEvent(attemptID.getTaskId(), TaskEventType.T_KILL));
            } else
                if ((taskType == (TaskType.MAP)) && (taskId == 1)) {
                    getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptFailEvent(attemptID));
                } else
                    if ((taskType == (TaskType.REDUCE)) && (taskId == 0)) {
                        getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptFailEvent(attemptID));
                    } else
                        if ((taskType == (TaskType.REDUCE)) && (taskId == 1)) {
                            getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskEvent(attemptID.getTaskId(), TaskEventType.T_KILL));
                        } else {
                            getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(attemptID, TaskAttemptEventType.TA_DONE));
                        }



        }
    }

    static class MRAppWithHistoryWithJobKilled extends TestJobHistoryEvents.MRAppWithHistory {
        public MRAppWithHistoryWithJobKilled(int maps, int reduces, boolean autoComplete, String testName, boolean cleanOnStart) {
            super(maps, reduces, autoComplete, testName, cleanOnStart);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void attemptLaunched(TaskAttemptId attemptID) {
            if ((attemptID.getTaskId().getId()) == 0) {
                getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.JobEvent(attemptID.getTaskId().getJobId(), JobEventType.JOB_KILL));
            } else {
                getContext().getEventHandler().handle(new org.apache.hadoop.mapreduce.v2.app.job.event.TaskAttemptEvent(attemptID, TaskAttemptEventType.TA_DONE));
            }
        }
    }

    static class HistoryFileManagerForTest extends HistoryFileManager {
        void deleteJobFromJobListCache(HistoryFileInfo fileInfo) {
            jobListCache.delete(fileInfo);
        }
    }

    /**
     * Test clean old history files. Files should be deleted after 1 week by
     * default.
     */
    @Test(timeout = 15000)
    public void testDeleteFileInfo() throws Exception {
        TestJobHistoryParsing.LOG.info("STARTING testDeleteFileInfo");
        try {
            Configuration conf = new Configuration();
            conf.setClass(NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY, TestJobHistoryParsing.MyResolver.class, DNSToSwitchMapping.class);
            RackResolver.init(conf);
            MRApp app = new TestJobHistoryEvents.MRAppWithHistory(1, 1, true, this.getClass().getName(), true);
            app.submit(conf);
            Job job = app.getContext().getAllJobs().values().iterator().next();
            JobId jobId = job.getID();
            app.waitForState(job, SUCCEEDED);
            // make sure all events are flushed
            app.waitForState(STOPPED);
            HistoryFileManager hfm = new HistoryFileManager();
            hfm.init(conf);
            HistoryFileInfo fileInfo = hfm.getFileInfo(jobId);
            hfm.initExisting();
            // wait for move files form the done_intermediate directory to the gone
            // directory
            while (fileInfo.isMovePending()) {
                Thread.sleep(300);
            } 
            Assert.assertNotNull(hfm.jobListCache.values());
            // try to remove fileInfo
            hfm.clean();
            // check that fileInfo does not deleted
            Assert.assertFalse(fileInfo.isDeleted());
            // correct live time
            hfm.setMaxHistoryAge((-1));
            hfm.clean();
            hfm.stop();
            Assert.assertTrue("Thread pool shutdown", hfm.moveToDoneExecutor.isTerminated());
            // should be deleted !
            Assert.assertTrue("file should be deleted ", fileInfo.isDeleted());
        } finally {
            TestJobHistoryParsing.LOG.info("FINISHED testDeleteFileInfo");
        }
    }

    /**
     * Simple test some methods of JobHistory
     */
    @Test(timeout = 20000)
    public void testJobHistoryMethods() throws Exception {
        TestJobHistoryParsing.LOG.info("STARTING testJobHistoryMethods");
        try {
            Configuration configuration = new Configuration();
            configuration.setClass(NET_TOPOLOGY_NODE_SWITCH_MAPPING_IMPL_KEY, TestJobHistoryParsing.MyResolver.class, DNSToSwitchMapping.class);
            RackResolver.init(configuration);
            MRApp app = new TestJobHistoryEvents.MRAppWithHistory(1, 1, true, this.getClass().getName(), true);
            app.submit(configuration);
            Job job = app.getContext().getAllJobs().values().iterator().next();
            JobId jobId = job.getID();
            TestJobHistoryParsing.LOG.info(("JOBID is " + (TypeConverter.fromYarn(jobId).toString())));
            app.waitForState(job, SUCCEEDED);
            // make sure job history events are handled
            app.waitForState(STOPPED);
            JobHistory jobHistory = new JobHistory();
            jobHistory.init(configuration);
            // Method getAllJobs
            Assert.assertEquals(1, jobHistory.getAllJobs().size());
            // and with ApplicationId
            Assert.assertEquals(1, jobHistory.getAllJobs(app.getAppID()).size());
            JobsInfo jobsinfo = jobHistory.getPartialJobs(0L, 10L, null, "default", 0L, ((System.currentTimeMillis()) + 1), 0L, ((System.currentTimeMillis()) + 1), SUCCEEDED);
            Assert.assertEquals(1, jobsinfo.getJobs().size());
            Assert.assertNotNull(jobHistory.getApplicationAttemptId());
            // test Application Id
            Assert.assertEquals("application_0_0000", jobHistory.getApplicationID().toString());
            Assert.assertEquals("Job History Server", jobHistory.getApplicationName());
            // method does not work
            Assert.assertNull(jobHistory.getEventHandler());
            // method does not work
            Assert.assertNull(jobHistory.getClock());
            // method does not work
            Assert.assertNull(jobHistory.getClusterInfo());
        } finally {
            TestJobHistoryParsing.LOG.info("FINISHED testJobHistoryMethods");
        }
    }

    /**
     * Simple test PartialJob
     */
    @Test(timeout = 3000)
    public void testPartialJob() throws Exception {
        JobId jobId = new JobIdPBImpl();
        jobId.setId(0);
        JobIndexInfo jii = new JobIndexInfo(0L, System.currentTimeMillis(), "user", "jobName", jobId, 3, 2, "JobStatus");
        PartialJob test = new PartialJob(jii, jobId);
        Assert.assertEquals(1.0F, test.getProgress(), 0.001F);
        Assert.assertNull(test.getAllCounters());
        Assert.assertNull(test.getTasks());
        Assert.assertNull(test.getTasks(MAP));
        Assert.assertNull(test.getTask(new TaskIdPBImpl()));
        Assert.assertNull(test.getTaskAttemptCompletionEvents(0, 100));
        Assert.assertNull(test.getMapAttemptCompletionEvents(0, 100));
        Assert.assertTrue(test.checkAccess(UserGroupInformation.getCurrentUser(), null));
        Assert.assertNull(test.getAMInfos());
    }

    @Test
    public void testMultipleFailedTasks() throws Exception {
        JobHistoryParser parser = new JobHistoryParser(Mockito.mock(FSDataInputStream.class));
        EventReader reader = Mockito.mock(EventReader.class);
        final AtomicInteger numEventsRead = new AtomicInteger(0);// Hack!

        final org.apache.hadoop.mapreduce.TaskType taskType = TaskType.MAP;
        final TaskID[] tids = new TaskID[2];
        final JobID jid = new JobID("1", 1);
        tids[0] = new TaskID(jid, taskType, 0);
        tids[1] = new TaskID(jid, taskType, 1);
        Mockito.when(reader.getNextEvent()).thenAnswer(new Answer<HistoryEvent>() {
            public HistoryEvent answer(InvocationOnMock invocation) throws IOException {
                // send two task start and two task fail events for tasks 0 and 1
                int eventId = numEventsRead.getAndIncrement();
                TaskID tid = tids[(eventId & 1)];
                if (eventId < 2) {
                    return new org.apache.hadoop.mapreduce.jobhistory.TaskStartedEvent(tid, 0, taskType, "");
                }
                if (eventId < 4) {
                    TaskFailedEvent tfe = new TaskFailedEvent(tid, 0, taskType, "failed", "FAILED", null, new Counters());
                    tfe.setDatum(tfe.getDatum());
                    return tfe;
                }
                if (eventId < 5) {
                    JobUnsuccessfulCompletionEvent juce = new JobUnsuccessfulCompletionEvent(jid, 100L, 2, 0, 0, 0, 0, 0, "JOB_FAILED", Collections.singletonList(("Task failed: " + (tids[0].toString()))));
                    return juce;
                }
                return null;
            }
        });
        JobInfo info = parser.parse(reader);
        Assert.assertTrue("Task 0 not implicated", info.getErrorInfo().contains(tids[0].toString()));
    }

    @Test
    public void testFailedJobHistoryWithoutDiagnostics() throws Exception {
        final Path histPath = new Path(getClass().getClassLoader().getResource("job_1393307629410_0001-1393307687476-user-Sleep+job-1393307723835-0-0-FAILED-default-1393307693920.jhist").getFile());
        final FileSystem lfs = FileSystem.getLocal(new Configuration());
        final FSDataInputStream fsdis = lfs.open(histPath);
        try {
            JobHistoryParser parser = new JobHistoryParser(fsdis);
            JobInfo info = parser.parse();
            Assert.assertEquals("History parsed jobId incorrectly", info.getJobId(), JobID.forName("job_1393307629410_0001"));
            Assert.assertEquals("Default diagnostics incorrect ", "", info.getErrorInfo());
        } finally {
            fsdis.close();
        }
    }

    /**
     * Test compatibility of JobHistoryParser with 2.0.3-alpha history files
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testTaskAttemptUnsuccessfulCompletionWithoutCounters203() throws IOException {
        Path histPath = new Path(getClass().getClassLoader().getResource("job_2.0.3-alpha-FAILED.jhist").getFile());
        JobHistoryParser parser = new JobHistoryParser(FileSystem.getLocal(new Configuration()), histPath);
        JobInfo jobInfo = parser.parse();
        TestJobHistoryParsing.LOG.info((((((((" job info: " + (jobInfo.getJobname())) + " ") + (jobInfo.getSucceededMaps())) + " ") + (jobInfo.getTotalMaps())) + " ") + (jobInfo.getJobId())));
    }

    /**
     * Test compatibility of JobHistoryParser with 2.4.0 history files
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testTaskAttemptUnsuccessfulCompletionWithoutCounters240() throws IOException {
        Path histPath = new Path(getClass().getClassLoader().getResource("job_2.4.0-FAILED.jhist").getFile());
        JobHistoryParser parser = new JobHistoryParser(FileSystem.getLocal(new Configuration()), histPath);
        JobInfo jobInfo = parser.parse();
        TestJobHistoryParsing.LOG.info((((((((" job info: " + (jobInfo.getJobname())) + " ") + (jobInfo.getSucceededMaps())) + " ") + (jobInfo.getTotalMaps())) + " ") + (jobInfo.getJobId())));
    }

    /**
     * Test compatibility of JobHistoryParser with 0.23.9 history files
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testTaskAttemptUnsuccessfulCompletionWithoutCounters0239() throws IOException {
        Path histPath = new Path(getClass().getClassLoader().getResource("job_0.23.9-FAILED.jhist").getFile());
        JobHistoryParser parser = new JobHistoryParser(FileSystem.getLocal(new Configuration()), histPath);
        JobInfo jobInfo = parser.parse();
        TestJobHistoryParsing.LOG.info((((((((" job info: " + (jobInfo.getJobname())) + " ") + (jobInfo.getSucceededMaps())) + " ") + (jobInfo.getTotalMaps())) + " ") + (jobInfo.getJobId())));
    }
}

