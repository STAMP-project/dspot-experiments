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
package org.apache.hadoop.mapred;


import MRConfig.LOCAL_DIR;
import MRJobConfig.JOB_SINGLE_DISK_LIMIT_BYTES;
import MRJobConfig.JOB_SINGLE_DISK_LIMIT_KILL_LIMIT_EXCEED;
import MRJobConfig.TASK_PROGRESS_REPORT_INTERVAL;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.mapred.SortedRanges.Range;
import org.apache.hadoop.mapreduce.checkpoint.TaskCheckpointID;
import org.junit.Assert;
import org.junit.Test;


public class TestTaskProgressReporter {
    private static int statusUpdateTimes = 0;

    // set to true if the thread is existed with ExitUtil.terminate
    volatile boolean threadExited = false;

    static final int LOCAL_BYTES_WRITTEN = 1024;

    private TestTaskProgressReporter.FakeUmbilical fakeUmbilical = new TestTaskProgressReporter.FakeUmbilical();

    private static final String TEST_DIR = ((System.getProperty("test.build.data", System.getProperty("java.io.tmpdir"))) + "/") + (TestTaskProgressReporter.class.getName());

    private static class DummyTask extends Task {
        @Override
        public void run(JobConf job, TaskUmbilicalProtocol umbilical) throws IOException, ClassNotFoundException, InterruptedException {
        }

        @Override
        public boolean isMapTask() {
            return true;
        }

        @Override
        public boolean isCommitRequired() {
            return false;
        }
    }

    private static class FakeUmbilical implements TaskUmbilicalProtocol {
        @Override
        public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
            return 0;
        }

        @Override
        public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash) throws IOException {
            return null;
        }

        @Override
        public JvmTask getTask(JvmContext context) throws IOException {
            return null;
        }

        @Override
        public AMFeedback statusUpdate(TaskAttemptID taskId, TaskStatus taskStatus) throws IOException, InterruptedException {
            (TestTaskProgressReporter.statusUpdateTimes)++;
            AMFeedback feedback = new AMFeedback();
            feedback.setTaskFound(true);
            feedback.setPreemption(true);
            return feedback;
        }

        @Override
        public void reportDiagnosticInfo(TaskAttemptID taskid, String trace) throws IOException {
        }

        @Override
        public void reportNextRecordRange(TaskAttemptID taskid, Range range) throws IOException {
        }

        @Override
        public void done(TaskAttemptID taskid) throws IOException {
        }

        @Override
        public void commitPending(TaskAttemptID taskId, TaskStatus taskStatus) throws IOException, InterruptedException {
        }

        @Override
        public boolean canCommit(TaskAttemptID taskid) throws IOException {
            return false;
        }

        @Override
        public void shuffleError(TaskAttemptID taskId, String message) throws IOException {
        }

        @Override
        public void fsError(TaskAttemptID taskId, String message) throws IOException {
        }

        @Override
        public void fatalError(TaskAttemptID taskId, String message, boolean fastFail) throws IOException {
        }

        @Override
        public MapTaskCompletionEventsUpdate getMapCompletionEvents(JobID jobId, int fromIndex, int maxLocs, TaskAttemptID id) throws IOException {
            return null;
        }

        @Override
        public void preempted(TaskAttemptID taskId, TaskStatus taskStatus) throws IOException, InterruptedException {
        }

        @Override
        public TaskCheckpointID getCheckpointID(TaskID taskID) {
            return null;
        }

        @Override
        public void setCheckpointID(TaskID tid, TaskCheckpointID cid) {
        }
    }

    private class DummyTaskReporter extends Task.TaskReporter {
        volatile boolean taskLimitIsChecked = false;

        public DummyTaskReporter(Task task) {
            task.super(task.getProgress(), fakeUmbilical);
        }

        @Override
        public void setProgress(float progress) {
            super.setProgress(progress);
        }

        @Override
        protected void checkTaskLimits() throws TaskLimitException {
            taskLimitIsChecked = true;
            super.checkTaskLimits();
        }
    }

    @Test(timeout = 60000)
    public void testScratchDirSize() throws Exception {
        String tmpPath = ((TestTaskProgressReporter.TEST_DIR) + "/testBytesWrittenLimit-tmpFile-") + (new Random(System.currentTimeMillis()).nextInt());
        File data = new File((tmpPath + "/out"));
        File testDir = new File(tmpPath);
        testDir.mkdirs();
        testDir.deleteOnExit();
        JobConf conf = new JobConf();
        conf.setStrings(LOCAL_DIR, ("file://" + tmpPath));
        conf.setLong(JOB_SINGLE_DISK_LIMIT_BYTES, 1024L);
        conf.setBoolean(JOB_SINGLE_DISK_LIMIT_KILL_LIMIT_EXCEED, true);
        getBaseConfAndWriteToFile((-1), data);
        testScratchDirLimit(false, conf);
        data.delete();
        getBaseConfAndWriteToFile(100, data);
        testScratchDirLimit(false, conf);
        data.delete();
        getBaseConfAndWriteToFile(1536, data);
        testScratchDirLimit(true, conf);
        conf.setBoolean(JOB_SINGLE_DISK_LIMIT_KILL_LIMIT_EXCEED, false);
        testScratchDirLimit(false, conf);
        conf.setBoolean(JOB_SINGLE_DISK_LIMIT_KILL_LIMIT_EXCEED, true);
        conf.setLong(JOB_SINGLE_DISK_LIMIT_BYTES, (-1L));
        testScratchDirLimit(false, conf);
        data.delete();
        FileUtil.fullyDelete(testDir);
    }

    @Test(timeout = 10000)
    public void testTaskProgress() throws Exception {
        JobConf job = new JobConf();
        job.setLong(TASK_PROGRESS_REPORT_INTERVAL, 1000);
        Task task = new TestTaskProgressReporter.DummyTask();
        task.setConf(job);
        TestTaskProgressReporter.DummyTaskReporter reporter = new TestTaskProgressReporter.DummyTaskReporter(task);
        Thread t = new Thread(reporter);
        t.start();
        Thread.sleep(2100);
        task.setTaskDone();
        resetDoneFlag();
        t.join();
        Assert.assertEquals(TestTaskProgressReporter.statusUpdateTimes, 2);
    }

    @Test(timeout = 10000)
    public void testBytesWrittenRespectingLimit() throws Exception {
        // add 1024 to the limit to account for writes not controlled by the test
        testBytesWrittenLimit(((TestTaskProgressReporter.LOCAL_BYTES_WRITTEN) + 1024), false);
    }

    @Test(timeout = 10000)
    public void testBytesWrittenExceedingLimit() throws Exception {
        testBytesWrittenLimit(((TestTaskProgressReporter.LOCAL_BYTES_WRITTEN) - 1), true);
    }
}

