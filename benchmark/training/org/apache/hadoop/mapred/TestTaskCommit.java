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


import JobStatus.FAILED;
import Reporter.NULL;
import TaskType.MAP;
import TaskType.REDUCE;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.ipc.ProtocolSignature;
import org.apache.hadoop.mapred.SortedRanges.Range;
import org.apache.hadoop.mapreduce.checkpoint.TaskCheckpointID;
import org.junit.Assert;
import org.junit.Test;


public class TestTaskCommit extends HadoopTestCase {
    Path rootDir = new Path(System.getProperty("test.build.data", "/tmp"), "test");

    static class CommitterWithCommitFail extends FileOutputCommitter {
        public void commitTask(TaskAttemptContext context) throws IOException {
            Path taskOutputPath = getTaskAttemptPath(context);
            TaskAttemptID attemptId = context.getTaskAttemptID();
            JobConf job = context.getJobConf();
            if (taskOutputPath != null) {
                FileSystem fs = taskOutputPath.getFileSystem(job);
                if (fs.exists(taskOutputPath)) {
                    throw new IOException();
                }
            }
        }
    }

    /**
     * Special Committer that does not cleanup temporary files in
     * abortTask
     *
     * The framework's FileOutputCommitter cleans up any temporary
     * files left behind in abortTask. We want the test case to
     * find these files and hence short-circuit abortTask.
     */
    static class CommitterWithoutCleanup extends FileOutputCommitter {
        @Override
        public void abortTask(TaskAttemptContext context) throws IOException {
            // does nothing
        }
    }

    /**
     * Special committer that always requires commit.
     */
    static class CommitterThatAlwaysRequiresCommit extends FileOutputCommitter {
        @Override
        public boolean needsTaskCommit(TaskAttemptContext context) throws IOException {
            return true;
        }
    }

    public TestTaskCommit() throws IOException {
        super(HadoopTestCase.LOCAL_MR, HadoopTestCase.LOCAL_FS, 1, 1);
    }

    @Test
    public void testCommitFail() throws IOException {
        final Path inDir = new Path(rootDir, "./input");
        final Path outDir = new Path(rootDir, "./output");
        JobConf jobConf = createJobConf();
        jobConf.setMaxMapAttempts(1);
        jobConf.setOutputCommitter(TestTaskCommit.CommitterWithCommitFail.class);
        RunningJob rJob = UtilsForTests.runJob(jobConf, inDir, outDir, 1, 0);
        rJob.waitForCompletion();
        Assert.assertEquals(FAILED, rJob.getJobState());
    }

    private class MyUmbilical implements TaskUmbilicalProtocol {
        boolean taskDone = false;

        @Override
        public boolean canCommit(TaskAttemptID taskid) throws IOException {
            return false;
        }

        @Override
        public void commitPending(TaskAttemptID taskId, TaskStatus taskStatus) throws IOException, InterruptedException {
            Assert.fail("Task should not go to commit-pending");
        }

        @Override
        public void done(TaskAttemptID taskid) throws IOException {
            taskDone = true;
        }

        @Override
        public void fatalError(TaskAttemptID taskId, String message, boolean fastFail) throws IOException {
        }

        @Override
        public void fsError(TaskAttemptID taskId, String message) throws IOException {
        }

        @Override
        public MapTaskCompletionEventsUpdate getMapCompletionEvents(JobID jobId, int fromIndex, int maxLocs, TaskAttemptID id) throws IOException {
            return null;
        }

        @Override
        public JvmTask getTask(JvmContext context) throws IOException {
            return null;
        }

        @Override
        public void reportDiagnosticInfo(TaskAttemptID taskid, String trace) throws IOException {
        }

        @Override
        public void reportNextRecordRange(TaskAttemptID taskid, Range range) throws IOException {
        }

        @Override
        public void shuffleError(TaskAttemptID taskId, String message) throws IOException {
        }

        @Override
        public AMFeedback statusUpdate(TaskAttemptID taskId, TaskStatus taskStatus) throws IOException, InterruptedException {
            AMFeedback a = new AMFeedback();
            a.setTaskFound(true);
            return a;
        }

        @Override
        public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
            return 0;
        }

        @Override
        public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash) throws IOException {
            return null;
        }

        @Override
        public void preempted(TaskAttemptID taskId, TaskStatus taskStatus) throws IOException, InterruptedException {
            Assert.fail("Task should not go to commit-pending");
        }

        @Override
        public TaskCheckpointID getCheckpointID(TaskID taskId) {
            return null;
        }

        @Override
        public void setCheckpointID(TaskID downgrade, TaskCheckpointID cid) {
            // ignore
        }
    }

    /**
     * A test that mimics a failed task to ensure that it does
     * not get into the COMMIT_PENDING state, by using a fake
     * UmbilicalProtocol's implementation that fails if the commit.
     * protocol is played.
     *
     * The test mocks the various steps in a failed task's
     * life-cycle using a special OutputCommitter and UmbilicalProtocol
     * implementation.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testTaskCleanupDoesNotCommit() throws Exception {
        // Mimic a job with a special committer that does not cleanup
        // files when a task fails.
        JobConf job = new JobConf();
        job.setOutputCommitter(TestTaskCommit.CommitterWithoutCleanup.class);
        Path outDir = new Path(rootDir, "output");
        FileOutputFormat.setOutputPath(job, outDir);
        // Mimic job setup
        String dummyAttemptID = "attempt_200707121733_0001_m_000000_0";
        TaskAttemptID attemptID = TaskAttemptID.forName(dummyAttemptID);
        OutputCommitter committer = new TestTaskCommit.CommitterWithoutCleanup();
        JobContext jContext = new JobContextImpl(job, attemptID.getJobID());
        committer.setupJob(jContext);
        // Mimic a map task
        dummyAttemptID = "attempt_200707121733_0001_m_000001_0";
        attemptID = TaskAttemptID.forName(dummyAttemptID);
        Task task = new MapTask(null, attemptID, 0, null, 1);
        task.setConf(job);
        task.localizeConfiguration(job);
        task.initialize(job, attemptID.getJobID(), NULL, false);
        // Mimic the map task writing some output.
        String file = "test.txt";
        FileSystem localFs = FileSystem.getLocal(job);
        TextOutputFormat<Text, Text> theOutputFormat = new TextOutputFormat<Text, Text>();
        RecordWriter<Text, Text> theRecordWriter = theOutputFormat.getRecordWriter(localFs, job, file, NULL);
        theRecordWriter.write(new Text("key"), new Text("value"));
        theRecordWriter.close(NULL);
        // Mimic a task failure; setting up the task for cleanup simulates
        // the abort protocol to be played.
        // Without checks in the framework, this will fail
        // as the committer will cause a COMMIT to happen for
        // the cleanup task.
        task.setTaskCleanupTask();
        TestTaskCommit.MyUmbilical umbilical = new TestTaskCommit.MyUmbilical();
        task.run(job, umbilical);
        Assert.assertTrue("Task did not succeed", umbilical.taskDone);
    }

    @Test
    public void testCommitRequiredForMapTask() throws Exception {
        Task testTask = createDummyTask(MAP);
        Assert.assertTrue("MapTask should need commit", testTask.isCommitRequired());
    }

    @Test
    public void testCommitRequiredForReduceTask() throws Exception {
        Task testTask = createDummyTask(REDUCE);
        Assert.assertTrue("ReduceTask should need commit", testTask.isCommitRequired());
    }

    @Test
    public void testCommitNotRequiredForJobSetup() throws Exception {
        Task testTask = createDummyTask(MAP);
        testTask.setJobSetupTask();
        Assert.assertFalse("Job setup task should not need commit", testTask.isCommitRequired());
    }

    @Test
    public void testCommitNotRequiredForJobCleanup() throws Exception {
        Task testTask = createDummyTask(MAP);
        testTask.setJobCleanupTask();
        Assert.assertFalse("Job cleanup task should not need commit", testTask.isCommitRequired());
    }

    @Test
    public void testCommitNotRequiredForTaskCleanup() throws Exception {
        Task testTask = createDummyTask(REDUCE);
        testTask.setTaskCleanupTask();
        Assert.assertFalse("Task cleanup task should not need commit", testTask.isCommitRequired());
    }
}

