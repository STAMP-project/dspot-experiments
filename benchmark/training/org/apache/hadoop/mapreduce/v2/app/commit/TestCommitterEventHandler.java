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
package org.apache.hadoop.mapreduce.v2.app.commit;


import MRJobConfig.MR_AM_STAGING_DIR;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.TypeConverter;
import org.apache.hadoop.mapreduce.v2.api.records.JobId;
import org.apache.hadoop.mapreduce.v2.app.AppContext;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobCommitCompletedEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobCommitFailedEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobEvent;
import org.apache.hadoop.mapreduce.v2.app.job.event.JobEventType;
import org.apache.hadoop.mapreduce.v2.app.rm.RMHeartbeatHandler;
import org.apache.hadoop.mapreduce.v2.util.MRApps;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.util.Time;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.conf.YarnConfiguration;
import org.apache.hadoop.yarn.event.AsyncDispatcher;
import org.apache.hadoop.yarn.event.Event;
import org.apache.hadoop.yarn.event.EventHandler;
import org.apache.hadoop.yarn.exceptions.YarnRuntimeException;
import org.apache.hadoop.yarn.util.Clock;
import org.apache.hadoop.yarn.util.SystemClock;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestCommitterEventHandler {
    public static class WaitForItHandler implements EventHandler<Event> {
        private Event event = null;

        @Override
        public synchronized void handle(Event event) {
            this.event = event;
            notifyAll();
        }

        public synchronized Event getAndClearEvent() throws InterruptedException {
            if ((event) == null) {
                final long waitTime = 5000;
                long waitStartTime = Time.monotonicNow();
                while (((event) == null) && (((Time.monotonicNow()) - waitStartTime) < waitTime)) {
                    // Wait for at most 5 sec
                    wait(waitTime);
                } 
            }
            Event e = event;
            event = null;
            return e;
        }
    }

    static String stagingDir = "target/test-staging/";

    @Test
    public void testCommitWindow() throws Exception {
        Configuration conf = new Configuration();
        conf.set(MR_AM_STAGING_DIR, TestCommitterEventHandler.stagingDir);
        AsyncDispatcher dispatcher = new AsyncDispatcher();
        dispatcher.init(conf);
        dispatcher.start();
        TestCommitterEventHandler.TestingJobEventHandler jeh = new TestCommitterEventHandler.TestingJobEventHandler();
        dispatcher.register(JobEventType.class, jeh);
        SystemClock clock = SystemClock.getInstance();
        AppContext appContext = Mockito.mock(AppContext.class);
        ApplicationAttemptId attemptid = ApplicationAttemptId.fromString("appattempt_1234567890000_0001_0");
        Mockito.when(appContext.getApplicationID()).thenReturn(attemptid.getApplicationId());
        Mockito.when(appContext.getApplicationAttemptId()).thenReturn(attemptid);
        Mockito.when(appContext.getEventHandler()).thenReturn(dispatcher.getEventHandler());
        Mockito.when(appContext.getClock()).thenReturn(clock);
        OutputCommitter committer = Mockito.mock(OutputCommitter.class);
        TestCommitterEventHandler.TestingRMHeartbeatHandler rmhh = new TestCommitterEventHandler.TestingRMHeartbeatHandler();
        CommitterEventHandler ceh = new CommitterEventHandler(appContext, committer, rmhh);
        ceh.init(conf);
        ceh.start();
        // verify trying to commit when RM heartbeats are stale does not commit
        ceh.handle(new CommitterJobCommitEvent(null, null));
        long timeToWaitMs = 5000;
        while (((rmhh.getNumCallbacks()) != 1) && (timeToWaitMs > 0)) {
            Thread.sleep(10);
            timeToWaitMs -= 10;
        } 
        Assert.assertEquals("committer did not register a heartbeat callback", 1, rmhh.getNumCallbacks());
        Mockito.verify(committer, Mockito.never()).commitJob(ArgumentMatchers.any(JobContext.class));
        Assert.assertEquals("committer should not have committed", 0, jeh.numCommitCompletedEvents);
        // set a fresh heartbeat and verify commit completes
        rmhh.setLastHeartbeatTime(clock.getTime());
        timeToWaitMs = 5000;
        while (((jeh.numCommitCompletedEvents) != 1) && (timeToWaitMs > 0)) {
            Thread.sleep(10);
            timeToWaitMs -= 10;
        } 
        Assert.assertEquals("committer did not complete commit after RM hearbeat", 1, jeh.numCommitCompletedEvents);
        Mockito.verify(committer, Mockito.times(1)).commitJob(ArgumentMatchers.any());
        // Clean up so we can try to commit again (Don't do this at home)
        cleanup();
        // try to commit again and verify it goes through since the heartbeat
        // is still fresh
        ceh.handle(new CommitterJobCommitEvent(null, null));
        timeToWaitMs = 5000;
        while (((jeh.numCommitCompletedEvents) != 2) && (timeToWaitMs > 0)) {
            Thread.sleep(10);
            timeToWaitMs -= 10;
        } 
        Assert.assertEquals("committer did not commit", 2, jeh.numCommitCompletedEvents);
        Mockito.verify(committer, Mockito.times(2)).commitJob(ArgumentMatchers.any());
        ceh.stop();
        dispatcher.stop();
    }

    private static class TestingRMHeartbeatHandler implements RMHeartbeatHandler {
        private long lastHeartbeatTime = 0;

        private ConcurrentLinkedQueue<Runnable> callbacks = new ConcurrentLinkedQueue<Runnable>();

        @Override
        public long getLastHeartbeatTime() {
            return lastHeartbeatTime;
        }

        @Override
        public void runOnNextHeartbeat(Runnable callback) {
            callbacks.add(callback);
        }

        public void setLastHeartbeatTime(long timestamp) {
            lastHeartbeatTime = timestamp;
            Runnable callback = null;
            while ((callback = callbacks.poll()) != null) {
                callback.run();
            } 
        }

        public int getNumCallbacks() {
            return callbacks.size();
        }
    }

    private static class TestingJobEventHandler implements EventHandler<JobEvent> {
        int numCommitCompletedEvents = 0;

        @Override
        public void handle(JobEvent event) {
            if ((event.getType()) == (JobEventType.JOB_COMMIT_COMPLETED)) {
                ++(numCommitCompletedEvents);
            }
        }
    }

    @Test
    public void testBasic() throws Exception {
        AppContext mockContext = Mockito.mock(AppContext.class);
        OutputCommitter mockCommitter = Mockito.mock(OutputCommitter.class);
        Clock mockClock = Mockito.mock(Clock.class);
        CommitterEventHandler handler = new CommitterEventHandler(mockContext, mockCommitter, new TestCommitterEventHandler.TestingRMHeartbeatHandler());
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(MR_AM_STAGING_DIR, TestCommitterEventHandler.stagingDir);
        JobContext mockJobContext = Mockito.mock(JobContext.class);
        ApplicationAttemptId attemptid = ApplicationAttemptId.fromString("appattempt_1234567890000_0001_0");
        JobId jobId = TypeConverter.toYarn(TypeConverter.fromYarn(attemptid.getApplicationId()));
        TestCommitterEventHandler.WaitForItHandler waitForItHandler = new TestCommitterEventHandler.WaitForItHandler();
        Mockito.when(mockContext.getApplicationID()).thenReturn(attemptid.getApplicationId());
        Mockito.when(mockContext.getApplicationAttemptId()).thenReturn(attemptid);
        Mockito.when(mockContext.getEventHandler()).thenReturn(waitForItHandler);
        Mockito.when(mockContext.getClock()).thenReturn(mockClock);
        handler.init(conf);
        handler.start();
        try {
            handler.handle(new CommitterJobCommitEvent(jobId, mockJobContext));
            String user = UserGroupInformation.getCurrentUser().getShortUserName();
            Path startCommitFile = MRApps.getStartJobCommitFile(conf, user, jobId);
            Path endCommitSuccessFile = MRApps.getEndJobCommitSuccessFile(conf, user, jobId);
            Path endCommitFailureFile = MRApps.getEndJobCommitFailureFile(conf, user, jobId);
            Event e = waitForItHandler.getAndClearEvent();
            Assert.assertNotNull(e);
            Assert.assertTrue((e instanceof JobCommitCompletedEvent));
            FileSystem fs = FileSystem.get(conf);
            Assert.assertTrue(startCommitFile.toString(), fs.exists(startCommitFile));
            Assert.assertTrue(endCommitSuccessFile.toString(), fs.exists(endCommitSuccessFile));
            Assert.assertFalse(endCommitFailureFile.toString(), fs.exists(endCommitFailureFile));
            Mockito.verify(mockCommitter).commitJob(ArgumentMatchers.any(JobContext.class));
        } finally {
            handler.stop();
        }
    }

    @Test
    public void testFailure() throws Exception {
        AppContext mockContext = Mockito.mock(AppContext.class);
        OutputCommitter mockCommitter = Mockito.mock(OutputCommitter.class);
        Clock mockClock = Mockito.mock(Clock.class);
        CommitterEventHandler handler = new CommitterEventHandler(mockContext, mockCommitter, new TestCommitterEventHandler.TestingRMHeartbeatHandler());
        YarnConfiguration conf = new YarnConfiguration();
        conf.set(MR_AM_STAGING_DIR, TestCommitterEventHandler.stagingDir);
        JobContext mockJobContext = Mockito.mock(JobContext.class);
        ApplicationAttemptId attemptid = ApplicationAttemptId.fromString("appattempt_1234567890000_0001_0");
        JobId jobId = TypeConverter.toYarn(TypeConverter.fromYarn(attemptid.getApplicationId()));
        TestCommitterEventHandler.WaitForItHandler waitForItHandler = new TestCommitterEventHandler.WaitForItHandler();
        Mockito.when(mockContext.getApplicationID()).thenReturn(attemptid.getApplicationId());
        Mockito.when(mockContext.getApplicationAttemptId()).thenReturn(attemptid);
        Mockito.when(mockContext.getEventHandler()).thenReturn(waitForItHandler);
        Mockito.when(mockContext.getClock()).thenReturn(mockClock);
        Mockito.doThrow(new YarnRuntimeException("Intentional Failure")).when(mockCommitter).commitJob(ArgumentMatchers.any(JobContext.class));
        handler.init(conf);
        handler.start();
        try {
            handler.handle(new CommitterJobCommitEvent(jobId, mockJobContext));
            String user = UserGroupInformation.getCurrentUser().getShortUserName();
            Path startCommitFile = MRApps.getStartJobCommitFile(conf, user, jobId);
            Path endCommitSuccessFile = MRApps.getEndJobCommitSuccessFile(conf, user, jobId);
            Path endCommitFailureFile = MRApps.getEndJobCommitFailureFile(conf, user, jobId);
            Event e = waitForItHandler.getAndClearEvent();
            Assert.assertNotNull(e);
            Assert.assertTrue((e instanceof JobCommitFailedEvent));
            FileSystem fs = FileSystem.get(conf);
            Assert.assertTrue(fs.exists(startCommitFile));
            Assert.assertFalse(fs.exists(endCommitSuccessFile));
            Assert.assertTrue(fs.exists(endCommitFailureFile));
            Mockito.verify(mockCommitter).commitJob(ArgumentMatchers.any(JobContext.class));
        } finally {
            handler.stop();
        }
    }
}

