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
package org.apache.hadoop.mapreduce.jobhistory;


import CommonConfigurationKeysPublic.FS_DEFAULT_NAME_KEY;
import EventType.AM_STARTED;
import EventType.JOB_FINISHED;
import EventType.JOB_KILLED;
import EventType.JOB_QUEUE_CHANGED;
import EventType.JOB_SUBMITTED;
import EventType.TASK_STARTED;
import JHAdminConfig.MR_HISTORY_INTERMEDIATE_DONE_DIR;
import JobHistoryEventHandler.MetaInfo;
import JobHistoryEventHandler.fileMap;
import JobStateInternal.ERROR;
import JobStateInternal.FAILED;
import JobStateInternal.KILLED;
import MRJobConfUtil.REDACTION_REPLACEMENT_VAL;
import MRJobConfig.MAPREDUCE_JOB_EMIT_TIMELINE_DATA;
import MRJobConfig.MR_AM_HISTORY_COMPLETE_EVENT_FLUSH_TIMEOUT_MS;
import MRJobConfig.MR_AM_HISTORY_JOB_COMPLETE_UNFLUSHED_MULTIPLIER;
import MRJobConfig.MR_AM_HISTORY_MAX_UNFLUSHED_COMPLETE_EVENTS;
import MRJobConfig.MR_AM_HISTORY_USE_BATCHED_FLUSH_QUEUE_SIZE_THRESHOLD;
import MRJobConfig.MR_AM_STAGING_DIR;
import MRJobConfig.MR_JOB_REDACTED_PROPERTIES;
import MRJobConfig.USER_NAME;
import TaskType.MAP;
import TaskType.REDUCE;
import YarnConfiguration.TIMELINE_SERVICE_ENABLED;
import YarnConfiguration.TIMELINE_SERVICE_WEBAPP_ADDRESS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileContext;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.mapreduce.CounterGroup;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskID;
import org.apache.hadoop.mapreduce.TaskType;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.util.JobHistoryEventUtils;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.MRAppMaster.RunningAppContext;
import org.apache.hadoop.mapreduce.v2.app.job.Job;
import org.apache.hadoop.mapreduce.v2.jobhistory.JobHistoryUtils;
import org.apache.hadoop.mapreduce.v2.util.MRBuilderUtils;
import org.apache.hadoop.mapreduce.v2.util.MRWebAppUtil;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntities;
import org.apache.hadoop.yarn.api.records.timeline.TimelineEntity;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.server.MiniYARNCluster;
import org.apache.hadoop.yarn.server.timeline.TimelineStore;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestJobHistoryEventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(TestJobHistoryEventHandler.class);

    private static MiniDFSCluster dfsCluster = null;

    private static String coreSitePath;

    @Test(timeout = 50000)
    public void testFirstFlushOnCompletionEvent() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams();
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, t.workDir);
        conf.setLong(MR_AM_HISTORY_COMPLETE_EVENT_FLUSH_TIMEOUT_MS, (60 * 1000L));
        conf.setInt(MR_AM_HISTORY_JOB_COMPLETE_UNFLUSHED_MULTIPLIER, 10);
        conf.setInt(MR_AM_HISTORY_MAX_UNFLUSHED_COMPLETE_EVENTS, 10);
        conf.setInt(MR_AM_HISTORY_USE_BATCHED_FLUSH_QUEUE_SIZE_THRESHOLD, 200);
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        EventWriter mockWriter = null;
        try {
            start();
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1))));
            mockWriter = jheh.getEventWriter();
            Mockito.verify(mockWriter).write(ArgumentMatchers.any(HistoryEvent.class));
            for (int i = 0; i < 100; i++) {
                queueEvent(jheh, new JobHistoryEvent(t.jobId, new TaskStartedEvent(t.taskID, 0, TaskType.MAP, "")));
            }
            handleNextNEvents(jheh, 100);
            Mockito.verify(mockWriter, Mockito.times(0)).flush();
            // First completion event, but min-queue-size for batching flushes is 10
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new TaskFinishedEvent(t.taskID, t.taskAttemptID, 0, TaskType.MAP, "", null, 0)));
            Mockito.verify(mockWriter).flush();
        } finally {
            stop();
            Mockito.verify(mockWriter).close();
        }
    }

    @Test(timeout = 50000)
    public void testMaxUnflushedCompletionEvents() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams();
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, t.workDir);
        conf.setLong(MR_AM_HISTORY_COMPLETE_EVENT_FLUSH_TIMEOUT_MS, (60 * 1000L));
        conf.setInt(MR_AM_HISTORY_JOB_COMPLETE_UNFLUSHED_MULTIPLIER, 10);
        conf.setInt(MR_AM_HISTORY_MAX_UNFLUSHED_COMPLETE_EVENTS, 10);
        conf.setInt(MR_AM_HISTORY_USE_BATCHED_FLUSH_QUEUE_SIZE_THRESHOLD, 5);
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        EventWriter mockWriter = null;
        try {
            start();
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1))));
            mockWriter = jheh.getEventWriter();
            Mockito.verify(mockWriter).write(ArgumentMatchers.any(HistoryEvent.class));
            for (int i = 0; i < 100; i++) {
                queueEvent(jheh, new JobHistoryEvent(t.jobId, new TaskFinishedEvent(t.taskID, t.taskAttemptID, 0, TaskType.MAP, "", null, 0)));
            }
            handleNextNEvents(jheh, 9);
            Mockito.verify(mockWriter, Mockito.times(0)).flush();
            handleNextNEvents(jheh, 1);
            Mockito.verify(mockWriter).flush();
            handleNextNEvents(jheh, 50);
            Mockito.verify(mockWriter, Mockito.times(6)).flush();
        } finally {
            stop();
            Mockito.verify(mockWriter).close();
        }
    }

    @Test(timeout = 50000)
    public void testUnflushedTimer() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams();
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, t.workDir);
        conf.setLong(MR_AM_HISTORY_COMPLETE_EVENT_FLUSH_TIMEOUT_MS, (2 * 1000L));// 2 seconds.

        conf.setInt(MR_AM_HISTORY_JOB_COMPLETE_UNFLUSHED_MULTIPLIER, 10);
        conf.setInt(MR_AM_HISTORY_MAX_UNFLUSHED_COMPLETE_EVENTS, 100);
        conf.setInt(MR_AM_HISTORY_USE_BATCHED_FLUSH_QUEUE_SIZE_THRESHOLD, 5);
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        EventWriter mockWriter = null;
        try {
            start();
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1))));
            mockWriter = jheh.getEventWriter();
            Mockito.verify(mockWriter).write(ArgumentMatchers.any(HistoryEvent.class));
            for (int i = 0; i < 100; i++) {
                queueEvent(jheh, new JobHistoryEvent(t.jobId, new TaskFinishedEvent(t.taskID, t.taskAttemptID, 0, TaskType.MAP, "", null, 0)));
            }
            handleNextNEvents(jheh, 9);
            Assert.assertTrue(getFlushTimerStatus());
            Mockito.verify(mockWriter, Mockito.times(0)).flush();
            Thread.sleep(((2 * 4) * 1000L));// 4 seconds should be enough. Just be safe.

            Mockito.verify(mockWriter).flush();
            Assert.assertFalse(getFlushTimerStatus());
        } finally {
            stop();
            Mockito.verify(mockWriter).close();
        }
    }

    @Test(timeout = 50000)
    public void testBatchedFlushJobEndMultiplier() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams();
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, t.workDir);
        conf.setLong(MR_AM_HISTORY_COMPLETE_EVENT_FLUSH_TIMEOUT_MS, (60 * 1000L));// 2 seconds.

        conf.setInt(MR_AM_HISTORY_JOB_COMPLETE_UNFLUSHED_MULTIPLIER, 3);
        conf.setInt(MR_AM_HISTORY_MAX_UNFLUSHED_COMPLETE_EVENTS, 10);
        conf.setInt(MR_AM_HISTORY_USE_BATCHED_FLUSH_QUEUE_SIZE_THRESHOLD, 0);
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        EventWriter mockWriter = null;
        try {
            start();
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1))));
            mockWriter = jheh.getEventWriter();
            Mockito.verify(mockWriter).write(ArgumentMatchers.any(HistoryEvent.class));
            for (int i = 0; i < 100; i++) {
                queueEvent(jheh, new JobHistoryEvent(t.jobId, new TaskFinishedEvent(t.taskID, t.taskAttemptID, 0, TaskType.MAP, "", null, 0)));
            }
            queueEvent(jheh, new JobHistoryEvent(t.jobId, new JobFinishedEvent(TypeConverter.fromYarn(t.jobId), 0, 10, 10, 0, 0, 0, 0, null, null, new Counters())));
            handleNextNEvents(jheh, 29);
            Mockito.verify(mockWriter, Mockito.times(0)).flush();
            handleNextNEvents(jheh, 72);
            Mockito.verify(mockWriter, Mockito.times(4)).flush();// 3 * 30 + 1 for JobFinished

        } finally {
            stop();
            Mockito.verify(mockWriter).close();
        }
    }

    // In case of all types of events, process Done files if it's last AM retry
    @Test(timeout = 50000)
    public void testProcessDoneFilesOnLastAMRetry() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams(true);
        Configuration conf = new Configuration();
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        EventWriter mockWriter = null;
        try {
            start();
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1))));
            Mockito.verify(jheh, Mockito.times(0)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobUnsuccessfulCompletionEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, ERROR.toString())));
            Mockito.verify(jheh, Mockito.times(1)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobFinishedEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, new Counters(), new Counters(), new Counters())));
            Mockito.verify(jheh, Mockito.times(2)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobUnsuccessfulCompletionEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, FAILED.toString())));
            Mockito.verify(jheh, Mockito.times(3)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobUnsuccessfulCompletionEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, KILLED.toString())));
            Mockito.verify(jheh, Mockito.times(4)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            mockWriter = jheh.getEventWriter();
            Mockito.verify(mockWriter, Mockito.times(5)).write(ArgumentMatchers.any(HistoryEvent.class));
        } finally {
            stop();
            Mockito.verify(mockWriter).close();
        }
    }

    // Skip processing Done files in case of ERROR, if it's not last AM retry
    @Test(timeout = 50000)
    public void testProcessDoneFilesNotLastAMRetry() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams(false);
        Configuration conf = new Configuration();
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        EventWriter mockWriter = null;
        try {
            start();
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1))));
            Mockito.verify(jheh, Mockito.times(0)).processDoneFiles(t.jobId);
            // skip processing done files
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobUnsuccessfulCompletionEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, ERROR.toString())));
            Mockito.verify(jheh, Mockito.times(0)).processDoneFiles(t.jobId);
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobFinishedEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, new Counters(), new Counters(), new Counters())));
            Mockito.verify(jheh, Mockito.times(1)).processDoneFiles(t.jobId);
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobUnsuccessfulCompletionEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, FAILED.toString())));
            Mockito.verify(jheh, Mockito.times(2)).processDoneFiles(t.jobId);
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobUnsuccessfulCompletionEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, KILLED.toString())));
            Mockito.verify(jheh, Mockito.times(3)).processDoneFiles(t.jobId);
            mockWriter = jheh.getEventWriter();
            Mockito.verify(mockWriter, Mockito.times(5)).write(ArgumentMatchers.any(HistoryEvent.class));
        } finally {
            stop();
            Mockito.verify(mockWriter).close();
        }
    }

    @Test
    public void testPropertyRedactionForJHS() throws Exception {
        final Configuration conf = new Configuration();
        String sensitivePropertyName = "aws.fake.credentials.name";
        String sensitivePropertyValue = "aws.fake.credentials.val";
        conf.set(sensitivePropertyName, sensitivePropertyValue);
        conf.set(MR_JOB_REDACTED_PROPERTIES, sensitivePropertyName);
        conf.set(FS_DEFAULT_NAME_KEY, TestJobHistoryEventHandler.dfsCluster.getURI().toString());
        final TestJobHistoryEventHandler.TestParams params = new TestJobHistoryEventHandler.TestParams();
        conf.set(MR_AM_STAGING_DIR, params.dfsWorkDir);
        final JHEvenHandlerForTest jheh = new JHEvenHandlerForTest(params.mockAppContext, 0, false);
        try {
            jheh.init(conf);
            start();
            handleEvent(jheh, new JobHistoryEvent(params.jobId, new AMStartedEvent(params.appAttemptId, 200, params.containerId, "nmhost", 3000, 4000, (-1))));
            handleEvent(jheh, new JobHistoryEvent(params.jobId, new JobUnsuccessfulCompletionEvent(TypeConverter.fromYarn(params.jobId), 0, 0, 0, 0, 0, 0, 0, FAILED.toString())));
            // verify the value of the sensitive property in job.xml is restored.
            Assert.assertEquals((sensitivePropertyName + " is modified."), conf.get(sensitivePropertyName), sensitivePropertyValue);
            // load the job_conf.xml in JHS directory and verify property redaction.
            Path jhsJobConfFile = TestJobHistoryEventHandler.getJobConfInIntermediateDoneDir(conf, params.jobId);
            Assert.assertTrue("The job_conf.xml file is not in the JHS directory", FileContext.getFileContext(conf).util().exists(jhsJobConfFile));
            Configuration jhsJobConf = new Configuration();
            try (InputStream input = FileSystem.get(conf).open(jhsJobConfFile)) {
                jhsJobConf.addResource(input);
                Assert.assertEquals((sensitivePropertyName + " is not redacted in HDFS."), REDACTION_REPLACEMENT_VAL, jhsJobConf.get(sensitivePropertyName));
            }
        } finally {
            stop();
            purgeHdfsHistoryIntermediateDoneDirectory(conf);
        }
    }

    @Test(timeout = 50000)
    public void testDefaultFsIsUsedForHistory() throws Exception {
        // Create default configuration pointing to the minicluster
        Configuration conf = new Configuration();
        conf.set(FS_DEFAULT_NAME_KEY, TestJobHistoryEventHandler.dfsCluster.getURI().toString());
        FileOutputStream os = new FileOutputStream(TestJobHistoryEventHandler.coreSitePath);
        conf.writeXml(os);
        os.close();
        // simulate execution under a non-default namenode
        conf.set(FS_DEFAULT_NAME_KEY, "file:///");
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams();
        conf.set(MR_AM_STAGING_DIR, t.dfsWorkDir);
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0, false);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        try {
            start();
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1))));
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobFinishedEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, new Counters(), new Counters(), new Counters())));
            // If we got here then event handler worked but we don't know with which
            // file system. Now we check that history stuff was written to minicluster
            FileSystem dfsFileSystem = TestJobHistoryEventHandler.dfsCluster.getFileSystem();
            Assert.assertTrue("Minicluster contains some history files", ((dfsFileSystem.globStatus(new Path(((t.dfsWorkDir) + "/*"))).length) != 0));
            FileSystem localFileSystem = LocalFileSystem.get(conf);
            Assert.assertFalse("No history directory on non-default file system", localFileSystem.exists(new Path(t.dfsWorkDir)));
        } finally {
            stop();
            purgeHdfsHistoryIntermediateDoneDirectory(conf);
        }
    }

    @Test
    public void testGetHistoryIntermediateDoneDirForUser() throws IOException {
        // Test relative path
        Configuration conf = new Configuration();
        conf.set(MR_HISTORY_INTERMEDIATE_DONE_DIR, "/mapred/history/done_intermediate");
        conf.set(USER_NAME, System.getProperty("user.name"));
        String pathStr = JobHistoryUtils.getHistoryIntermediateDoneDirForUser(conf);
        Assert.assertEquals(("/mapred/history/done_intermediate/" + (System.getProperty("user.name"))), pathStr);
        // Test fully qualified path
        // Create default configuration pointing to the minicluster
        conf.set(FS_DEFAULT_NAME_KEY, TestJobHistoryEventHandler.dfsCluster.getURI().toString());
        FileOutputStream os = new FileOutputStream(TestJobHistoryEventHandler.coreSitePath);
        conf.writeXml(os);
        os.close();
        // Simulate execution under a non-default namenode
        conf.set(FS_DEFAULT_NAME_KEY, "file:///");
        pathStr = JobHistoryUtils.getHistoryIntermediateDoneDirForUser(conf);
        Assert.assertEquals((((TestJobHistoryEventHandler.dfsCluster.getURI().toString()) + "/mapred/history/done_intermediate/") + (System.getProperty("user.name"))), pathStr);
    }

    // test AMStartedEvent for submitTime and startTime
    @Test(timeout = 50000)
    public void testAMStartedEvent() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams();
        Configuration conf = new Configuration();
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        EventWriter mockWriter = null;
        try {
            start();
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, 100)));
            JobHistoryEventHandler.MetaInfo mi = fileMap.get(t.jobId);
            Assert.assertEquals(mi.getJobIndexInfo().getSubmitTime(), 100);
            Assert.assertEquals(mi.getJobIndexInfo().getJobStartTime(), 200);
            Assert.assertEquals(mi.getJobSummary().getJobSubmitTime(), 100);
            Assert.assertEquals(mi.getJobSummary().getJobLaunchTime(), 200);
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobUnsuccessfulCompletionEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, FAILED.toString())));
            Assert.assertEquals(mi.getJobIndexInfo().getSubmitTime(), 100);
            Assert.assertEquals(mi.getJobIndexInfo().getJobStartTime(), 200);
            Assert.assertEquals(mi.getJobSummary().getJobSubmitTime(), 100);
            Assert.assertEquals(mi.getJobSummary().getJobLaunchTime(), 200);
            Mockito.verify(jheh, Mockito.times(1)).processDoneFiles(t.jobId);
            mockWriter = jheh.getEventWriter();
            Mockito.verify(mockWriter, Mockito.times(2)).write(ArgumentMatchers.any(HistoryEvent.class));
        } finally {
            stop();
        }
    }

    // Have JobHistoryEventHandler handle some events and make sure they get
    // stored to the Timeline store
    @Test(timeout = 50000)
    public void testTimelineEventHandling() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams(RunningAppContext.class, false);
        Configuration conf = new YarnConfiguration();
        conf.setBoolean(TIMELINE_SERVICE_ENABLED, true);
        long currentTime = System.currentTimeMillis();
        try (MiniYARNCluster yarnCluster = new MiniYARNCluster(TestJobHistoryEventHandler.class.getSimpleName(), 1, 1, 1, 1)) {
            yarnCluster.init(conf);
            yarnCluster.start();
            Configuration confJHEH = new YarnConfiguration(conf);
            confJHEH.setBoolean(MAPREDUCE_JOB_EMIT_TIMELINE_DATA, true);
            confJHEH.set(TIMELINE_SERVICE_WEBAPP_ADDRESS, (((MiniYARNCluster.getHostname()) + ":") + (yarnCluster.getApplicationHistoryServer().getPort())));
            JHEvenHandlerForTest jheh = new JHEvenHandlerForTest(t.mockAppContext, 0);
            jheh.init(confJHEH);
            start();
            TimelineStore ts = yarnCluster.getApplicationHistoryServer().getTimelineStore();
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1)), (currentTime - 10)));
            jheh.getDispatcher().await();
            TimelineEntities entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(1, entities.getEntities().size());
            TimelineEntity tEntity = entities.getEntities().get(0);
            Assert.assertEquals(t.jobId.toString(), tEntity.getEntityId());
            Assert.assertEquals(1, tEntity.getEvents().size());
            Assert.assertEquals(AM_STARTED.toString(), tEntity.getEvents().get(0).getEventType());
            Assert.assertEquals((currentTime - 10), tEntity.getEvents().get(0).getTimestamp());
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobSubmittedEvent(TypeConverter.fromYarn(t.jobId), "name", "user", 200, "/foo/job.xml", new HashMap<org.apache.hadoop.mapreduce.JobACL, org.apache.hadoop.security.authorize.AccessControlList>(), "default"), (currentTime + 10)));
            jheh.getDispatcher().await();
            entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(1, entities.getEntities().size());
            tEntity = entities.getEntities().get(0);
            Assert.assertEquals(t.jobId.toString(), tEntity.getEntityId());
            Assert.assertEquals(2, tEntity.getEvents().size());
            Assert.assertEquals(JOB_SUBMITTED.toString(), tEntity.getEvents().get(0).getEventType());
            Assert.assertEquals(AM_STARTED.toString(), tEntity.getEvents().get(1).getEventType());
            Assert.assertEquals((currentTime + 10), tEntity.getEvents().get(0).getTimestamp());
            Assert.assertEquals((currentTime - 10), tEntity.getEvents().get(1).getTimestamp());
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobQueueChangeEvent(TypeConverter.fromYarn(t.jobId), "q2"), (currentTime - 20)));
            jheh.getDispatcher().await();
            entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(1, entities.getEntities().size());
            tEntity = entities.getEntities().get(0);
            Assert.assertEquals(t.jobId.toString(), tEntity.getEntityId());
            Assert.assertEquals(3, tEntity.getEvents().size());
            Assert.assertEquals(JOB_SUBMITTED.toString(), tEntity.getEvents().get(0).getEventType());
            Assert.assertEquals(AM_STARTED.toString(), tEntity.getEvents().get(1).getEventType());
            Assert.assertEquals(JOB_QUEUE_CHANGED.toString(), tEntity.getEvents().get(2).getEventType());
            Assert.assertEquals((currentTime + 10), tEntity.getEvents().get(0).getTimestamp());
            Assert.assertEquals((currentTime - 10), tEntity.getEvents().get(1).getTimestamp());
            Assert.assertEquals((currentTime - 20), tEntity.getEvents().get(2).getTimestamp());
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobFinishedEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, new Counters(), new Counters(), new Counters()), currentTime));
            jheh.getDispatcher().await();
            entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(1, entities.getEntities().size());
            tEntity = entities.getEntities().get(0);
            Assert.assertEquals(t.jobId.toString(), tEntity.getEntityId());
            Assert.assertEquals(4, tEntity.getEvents().size());
            Assert.assertEquals(JOB_SUBMITTED.toString(), tEntity.getEvents().get(0).getEventType());
            Assert.assertEquals(JOB_FINISHED.toString(), tEntity.getEvents().get(1).getEventType());
            Assert.assertEquals(AM_STARTED.toString(), tEntity.getEvents().get(2).getEventType());
            Assert.assertEquals(JOB_QUEUE_CHANGED.toString(), tEntity.getEvents().get(3).getEventType());
            Assert.assertEquals((currentTime + 10), tEntity.getEvents().get(0).getTimestamp());
            Assert.assertEquals(currentTime, tEntity.getEvents().get(1).getTimestamp());
            Assert.assertEquals((currentTime - 10), tEntity.getEvents().get(2).getTimestamp());
            Assert.assertEquals((currentTime - 20), tEntity.getEvents().get(3).getTimestamp());
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobUnsuccessfulCompletionEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, KILLED.toString()), (currentTime + 20)));
            jheh.getDispatcher().await();
            entities = ts.getEntities("MAPREDUCE_JOB", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(1, entities.getEntities().size());
            tEntity = entities.getEntities().get(0);
            Assert.assertEquals(t.jobId.toString(), tEntity.getEntityId());
            Assert.assertEquals(5, tEntity.getEvents().size());
            Assert.assertEquals(JOB_KILLED.toString(), tEntity.getEvents().get(0).getEventType());
            Assert.assertEquals(JOB_SUBMITTED.toString(), tEntity.getEvents().get(1).getEventType());
            Assert.assertEquals(JOB_FINISHED.toString(), tEntity.getEvents().get(2).getEventType());
            Assert.assertEquals(AM_STARTED.toString(), tEntity.getEvents().get(3).getEventType());
            Assert.assertEquals(JOB_QUEUE_CHANGED.toString(), tEntity.getEvents().get(4).getEventType());
            Assert.assertEquals((currentTime + 20), tEntity.getEvents().get(0).getTimestamp());
            Assert.assertEquals((currentTime + 10), tEntity.getEvents().get(1).getTimestamp());
            Assert.assertEquals(currentTime, tEntity.getEvents().get(2).getTimestamp());
            Assert.assertEquals((currentTime - 10), tEntity.getEvents().get(3).getTimestamp());
            Assert.assertEquals((currentTime - 20), tEntity.getEvents().get(4).getTimestamp());
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new TaskStartedEvent(t.taskID, 0, TaskType.MAP, "")));
            jheh.getDispatcher().await();
            entities = ts.getEntities("MAPREDUCE_TASK", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(1, entities.getEntities().size());
            tEntity = entities.getEntities().get(0);
            Assert.assertEquals(t.taskID.toString(), tEntity.getEntityId());
            Assert.assertEquals(1, tEntity.getEvents().size());
            Assert.assertEquals(TASK_STARTED.toString(), tEntity.getEvents().get(0).getEventType());
            Assert.assertEquals(MAP.toString(), tEntity.getEvents().get(0).getEventInfo().get("TASK_TYPE"));
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new TaskStartedEvent(t.taskID, 0, TaskType.REDUCE, "")));
            jheh.getDispatcher().await();
            entities = ts.getEntities("MAPREDUCE_TASK", null, null, null, null, null, null, null, null, null);
            Assert.assertEquals(1, entities.getEntities().size());
            tEntity = entities.getEntities().get(0);
            Assert.assertEquals(t.taskID.toString(), tEntity.getEntityId());
            Assert.assertEquals(2, tEntity.getEvents().size());
            Assert.assertEquals(TASK_STARTED.toString(), tEntity.getEvents().get(1).getEventType());
            Assert.assertEquals(REDUCE.toString(), tEntity.getEvents().get(0).getEventInfo().get("TASK_TYPE"));
            Assert.assertEquals(MAP.toString(), tEntity.getEvents().get(1).getEventInfo().get("TASK_TYPE"));
        }
    }

    @Test(timeout = 50000)
    public void testCountersToJSON() throws Exception {
        JobHistoryEventHandler jheh = new JobHistoryEventHandler(null, 0);
        Counters counters = new Counters();
        CounterGroup group1 = counters.addGroup("DOCTORS", "Incarnations of the Doctor");
        group1.addCounter("PETER_CAPALDI", "Peter Capaldi", 12);
        group1.addCounter("MATT_SMITH", "Matt Smith", 11);
        group1.addCounter("DAVID_TENNANT", "David Tennant", 10);
        CounterGroup group2 = counters.addGroup("COMPANIONS", "Companions of the Doctor");
        group2.addCounter("CLARA_OSWALD", "Clara Oswald", 6);
        group2.addCounter("RORY_WILLIAMS", "Rory Williams", 5);
        group2.addCounter("AMY_POND", "Amy Pond", 4);
        group2.addCounter("MARTHA_JONES", "Martha Jones", 3);
        group2.addCounter("DONNA_NOBLE", "Donna Noble", 2);
        group2.addCounter("ROSE_TYLER", "Rose Tyler", 1);
        JsonNode jsonNode = JobHistoryEventUtils.countersToJSON(counters);
        String jsonStr = new ObjectMapper().writeValueAsString(jsonNode);
        String expected = "[{\"NAME\":\"COMPANIONS\",\"DISPLAY_NAME\":\"Companions " + (((((((((((("of the Doctor\",\"COUNTERS\":[{\"NAME\":\"AMY_POND\",\"DISPLAY_NAME\"" + ":\"Amy Pond\",\"VALUE\":4},{\"NAME\":\"CLARA_OSWALD\",") + "\"DISPLAY_NAME\":\"Clara Oswald\",\"VALUE\":6},{\"NAME\":") + "\"DONNA_NOBLE\",\"DISPLAY_NAME\":\"Donna Noble\",\"VALUE\":2},") + "{\"NAME\":\"MARTHA_JONES\",\"DISPLAY_NAME\":\"Martha Jones\",") + "\"VALUE\":3},{\"NAME\":\"RORY_WILLIAMS\",\"DISPLAY_NAME\":\"Rory ") + "Williams\",\"VALUE\":5},{\"NAME\":\"ROSE_TYLER\",\"DISPLAY_NAME\":") + "\"Rose Tyler\",\"VALUE\":1}]},{\"NAME\":\"DOCTORS\",\"DISPLAY_NAME\"") + ":\"Incarnations of the Doctor\",\"COUNTERS\":[{\"NAME\":") + "\"DAVID_TENNANT\",\"DISPLAY_NAME\":\"David Tennant\",\"VALUE\":10},") + "{\"NAME\":\"MATT_SMITH\",\"DISPLAY_NAME\":\"Matt Smith\",\"VALUE\":") + "11},{\"NAME\":\"PETER_CAPALDI\",\"DISPLAY_NAME\":\"Peter Capaldi\",") + "\"VALUE\":12}]}]");
        Assert.assertEquals(expected, jsonStr);
    }

    @Test(timeout = 50000)
    public void testCountersToJSONEmpty() throws Exception {
        JobHistoryEventHandler jheh = new JobHistoryEventHandler(null, 0);
        Counters counters = null;
        JsonNode jsonNode = JobHistoryEventUtils.countersToJSON(counters);
        String jsonStr = new ObjectMapper().writeValueAsString(jsonNode);
        String expected = "[]";
        Assert.assertEquals(expected, jsonStr);
        counters = new Counters();
        jsonNode = JobHistoryEventUtils.countersToJSON(counters);
        jsonStr = new ObjectMapper().writeValueAsString(jsonNode);
        expected = "[]";
        Assert.assertEquals(expected, jsonStr);
        counters.addGroup("DOCTORS", "Incarnations of the Doctor");
        jsonNode = JobHistoryEventUtils.countersToJSON(counters);
        jsonStr = new ObjectMapper().writeValueAsString(jsonNode);
        expected = "[{\"NAME\":\"DOCTORS\",\"DISPLAY_NAME\":\"Incarnations of the " + "Doctor\",\"COUNTERS\":[]}]";
        Assert.assertEquals(expected, jsonStr);
    }

    private class TestParams {
        boolean isLastAMRetry;

        String workDir = setupTestWorkDir();

        String dfsWorkDir = "/" + (this.getClass().getCanonicalName());

        ApplicationId appId = ApplicationId.newInstance(200, 1);

        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);

        ContainerId containerId = ContainerId.newContainerId(appAttemptId, 1);

        TaskID taskID = TaskID.forName("task_200707121733_0003_m_000005");

        TaskAttemptID taskAttemptID = new TaskAttemptID(taskID, 0);

        JobId jobId = MRBuilderUtils.newJobId(appId, 1);

        AppContext mockAppContext;

        public TestParams() {
            this(AppContext.class, false);
        }

        public TestParams(boolean isLastAMRetry) {
            this(AppContext.class, isLastAMRetry);
        }

        public TestParams(Class<? extends AppContext> contextClass, boolean isLastAMRetry) {
            this.isLastAMRetry = isLastAMRetry;
            mockAppContext = mockAppContext(contextClass, appId, this.isLastAMRetry);
        }
    }

    /**
     * Tests that in case of SIGTERM, the JHEH stops without processing its event
     * queue (because we must stop quickly lest we get SIGKILLed) and processes
     * a JobUnsuccessfulEvent for jobs which were still running (so that they may
     * show up in the JobHistoryServer)
     */
    @Test
    public void testSigTermedFunctionality() throws IOException {
        AppContext mockedContext = Mockito.mock(AppContext.class);
        JHEventHandlerForSigtermTest jheh = new JHEventHandlerForSigtermTest(mockedContext, 0);
        JobId jobId = Mockito.mock(JobId.class);
        jheh.addToFileMap(jobId);
        // Submit 4 events and check that they're handled in the absence of a signal
        final int numEvents = 4;
        JobHistoryEvent[] events = new JobHistoryEvent[numEvents];
        for (int i = 0; i < numEvents; ++i) {
            events[i] = getEventToEnqueue(jobId);
            jheh.handle(events[i]);
        }
        stop();
        // Make sure events were handled
        Assert.assertTrue(("handleEvent should've been called only 4 times but was " + (jheh.eventsHandled)), ((jheh.eventsHandled) == 4));
        // Create a new jheh because the last stop closed the eventWriter etc.
        jheh = new JHEventHandlerForSigtermTest(mockedContext, 0);
        // Make constructor of JobUnsuccessfulCompletionEvent pass
        Job job = Mockito.mock(Job.class);
        Mockito.when(mockedContext.getJob(jobId)).thenReturn(job);
        // Make TypeConverter(JobID) pass
        ApplicationId mockAppId = Mockito.mock(ApplicationId.class);
        Mockito.when(mockAppId.getClusterTimestamp()).thenReturn(1000L);
        Mockito.when(jobId.getAppId()).thenReturn(mockAppId);
        jheh.addToFileMap(jobId);
        setForcejobCompletion(true);
        for (int i = 0; i < numEvents; ++i) {
            events[i] = getEventToEnqueue(jobId);
            jheh.handle(events[i]);
        }
        stop();
        // Make sure events were handled, 4 + 1 finish event
        Assert.assertTrue(("handleEvent should've been called only 5 times but was " + (jheh.eventsHandled)), ((jheh.eventsHandled) == 5));
        Assert.assertTrue("Last event handled wasn't JobUnsuccessfulCompletionEvent", ((jheh.lastEventHandled.getHistoryEvent()) instanceof JobUnsuccessfulCompletionEvent));
    }

    @Test(timeout = 50000)
    public void testSetTrackingURLAfterHistoryIsWritten() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams(true);
        Configuration conf = new Configuration();
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0, false);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        try {
            start();
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1))));
            Mockito.verify(jheh, Mockito.times(0)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            Mockito.verify(t.mockAppContext, Mockito.times(0)).setHistoryUrl(ArgumentMatchers.any(String.class));
            // Job finishes and successfully writes history
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobFinishedEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, new Counters(), new Counters(), new Counters())));
            Mockito.verify(jheh, Mockito.times(1)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            String historyUrl = MRWebAppUtil.getApplicationWebURLOnJHSWithScheme(conf, t.mockAppContext.getApplicationID());
            Mockito.verify(t.mockAppContext, Mockito.times(1)).setHistoryUrl(historyUrl);
        } finally {
            stop();
        }
    }

    @Test(timeout = 50000)
    public void testDontSetTrackingURLIfHistoryWriteFailed() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams(true);
        Configuration conf = new Configuration();
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0, false);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        try {
            start();
            moveToDoneNow(ArgumentMatchers.any(Path.class), ArgumentMatchers.any(Path.class));
            moveTmpToDone(ArgumentMatchers.any(Path.class));
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1))));
            Mockito.verify(jheh, Mockito.times(0)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            Mockito.verify(t.mockAppContext, Mockito.times(0)).setHistoryUrl(ArgumentMatchers.any(String.class));
            // Job finishes, but doesn't successfully write history
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobFinishedEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, new Counters(), new Counters(), new Counters())));
            Mockito.verify(jheh, Mockito.times(1)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            Mockito.verify(t.mockAppContext, Mockito.times(0)).setHistoryUrl(ArgumentMatchers.any(String.class));
        } finally {
            stop();
        }
    }

    @Test(timeout = 50000)
    public void testDontSetTrackingURLIfHistoryWriteThrows() throws Exception {
        TestJobHistoryEventHandler.TestParams t = new TestJobHistoryEventHandler.TestParams(true);
        Configuration conf = new Configuration();
        JHEvenHandlerForTest realJheh = new JHEvenHandlerForTest(t.mockAppContext, 0, false);
        JHEvenHandlerForTest jheh = Mockito.spy(realJheh);
        jheh.init(conf);
        try {
            start();
            Mockito.doThrow(new YarnRuntimeException(new IOException())).when(jheh).processDoneFiles(ArgumentMatchers.any(JobId.class));
            handleEvent(jheh, new JobHistoryEvent(t.jobId, new AMStartedEvent(t.appAttemptId, 200, t.containerId, "nmhost", 3000, 4000, (-1))));
            Mockito.verify(jheh, Mockito.times(0)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            Mockito.verify(t.mockAppContext, Mockito.times(0)).setHistoryUrl(ArgumentMatchers.any(String.class));
            // Job finishes, but doesn't successfully write history
            try {
                handleEvent(jheh, new JobHistoryEvent(t.jobId, new JobFinishedEvent(TypeConverter.fromYarn(t.jobId), 0, 0, 0, 0, 0, 0, 0, new Counters(), new Counters(), new Counters())));
                throw new RuntimeException("processDoneFiles didn't throw, but should have");
            } catch (YarnRuntimeException yre) {
                // Exception expected, do nothing
            }
            Mockito.verify(jheh, Mockito.times(1)).processDoneFiles(ArgumentMatchers.any(JobId.class));
            Mockito.verify(t.mockAppContext, Mockito.times(0)).setHistoryUrl(ArgumentMatchers.any(String.class));
        } finally {
            stop();
        }
    }
}

