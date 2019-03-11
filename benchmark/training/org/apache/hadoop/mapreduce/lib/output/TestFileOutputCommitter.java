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
package org.apache.hadoop.mapreduce.lib.output;


import FileOutputCommitter.FILEOUTPUTCOMMITTER_ALGORITHM_VERSION;
import MRJobConfig.TASK_ATTEMPT_ID;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@SuppressWarnings("unchecked")
public class TestFileOutputCommitter {
    private static final Path outDir = new Path(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), TestFileOutputCommitter.class.getName());

    private static final String SUB_DIR = "SUB_DIR";

    private static final Path OUT_SUB_DIR = new Path(TestFileOutputCommitter.outDir, TestFileOutputCommitter.SUB_DIR);

    private static final Logger LOG = LoggerFactory.getLogger(TestFileOutputCommitter.class);

    // A random task attempt id for testing.
    private static final String attempt = "attempt_200707121733_0001_m_000000_0";

    private static final String partFile = "part-m-00000";

    private static final TaskAttemptID taskID = TaskAttemptID.forName(TestFileOutputCommitter.attempt);

    private static final String attempt1 = "attempt_200707121733_0001_m_000001_0";

    private static final TaskAttemptID taskID1 = TaskAttemptID.forName(TestFileOutputCommitter.attempt1);

    private Text key1 = new Text("key1");

    private Text key2 = new Text("key2");

    private Text val1 = new Text("val1");

    private Text val2 = new Text("val2");

    @Test
    public void testRecoveryV1() throws Exception {
        testRecoveryInternal(1, 1);
    }

    @Test
    public void testRecoveryV2() throws Exception {
        testRecoveryInternal(2, 2);
    }

    @Test
    public void testRecoveryUpgradeV1V2() throws Exception {
        testRecoveryInternal(1, 2);
    }

    @Test
    public void testCommitterV1() throws Exception {
        testCommitterInternal(1, false);
    }

    @Test
    public void testCommitterV2() throws Exception {
        testCommitterInternal(2, false);
    }

    @Test
    public void testCommitterV2TaskCleanupEnabled() throws Exception {
        testCommitterInternal(2, true);
    }

    @Test
    public void testCommitterWithDuplicatedCommitV1() throws Exception {
        testCommitterWithDuplicatedCommitInternal(1);
    }

    @Test
    public void testCommitterWithDuplicatedCommitV2() throws Exception {
        testCommitterWithDuplicatedCommitInternal(2);
    }

    @Test
    public void testCommitterWithFailureV1() throws Exception {
        testCommitterWithFailureInternal(1, 1);
        testCommitterWithFailureInternal(1, 2);
    }

    @Test
    public void testCommitterWithFailureV2() throws Exception {
        testCommitterWithFailureInternal(2, 1);
        testCommitterWithFailureInternal(2, 2);
    }

    @Test
    public void testProgressDuringMerge() throws Exception {
        Job job = Job.getInstance();
        FileOutputFormat.setOutputPath(job, TestFileOutputCommitter.outDir);
        Configuration conf = job.getConfiguration();
        conf.set(TASK_ATTEMPT_ID, TestFileOutputCommitter.attempt);
        conf.setInt(FILEOUTPUTCOMMITTER_ALGORITHM_VERSION, 2);
        JobContext jContext = new org.apache.hadoop.mapreduce.task.JobContextImpl(conf, TestFileOutputCommitter.taskID.getJobID());
        TaskAttemptContext tContext = Mockito.spy(new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(conf, TestFileOutputCommitter.taskID));
        FileOutputCommitter committer = new FileOutputCommitter(TestFileOutputCommitter.outDir, tContext);
        // setup
        committer.setupJob(jContext);
        committer.setupTask(tContext);
        // write output
        MapFileOutputFormat theOutputFormat = new MapFileOutputFormat();
        RecordWriter theRecordWriter = theOutputFormat.getRecordWriter(tContext);
        writeMapFileOutput(theRecordWriter, tContext);
        // do commit
        committer.commitTask(tContext);
        // make sure progress flag was set.
        // The first time it is set is during commit but ensure that
        // mergePaths call makes it go again.
        Mockito.verify(tContext, Mockito.atLeast(2)).progress();
    }

    @Test
    public void testCommitterRepeatableV1() throws Exception {
        testCommitterRetryInternal(1);
    }

    @Test
    public void testCommitterRepeatableV2() throws Exception {
        testCommitterRetryInternal(2);
    }

    @Test
    public void testMapFileOutputCommitterV1() throws Exception {
        testMapFileOutputCommitterInternal(1);
    }

    @Test
    public void testMapFileOutputCommitterV2() throws Exception {
        testMapFileOutputCommitterInternal(2);
    }

    @Test
    public void testInvalidVersionNumber() throws IOException {
        Job job = Job.getInstance();
        FileOutputFormat.setOutputPath(job, TestFileOutputCommitter.outDir);
        Configuration conf = job.getConfiguration();
        conf.set(TASK_ATTEMPT_ID, TestFileOutputCommitter.attempt);
        conf.setInt(FILEOUTPUTCOMMITTER_ALGORITHM_VERSION, 3);
        TaskAttemptContext tContext = new org.apache.hadoop.mapreduce.task.TaskAttemptContextImpl(conf, TestFileOutputCommitter.taskID);
        try {
            new FileOutputCommitter(TestFileOutputCommitter.outDir, tContext);
            Assert.fail("should've thrown an exception!");
        } catch (IOException e) {
            // test passed
        }
    }

    @Test
    public void testAbortV1() throws IOException, InterruptedException {
        testAbortInternal(1);
    }

    @Test
    public void testAbortV2() throws IOException, InterruptedException {
        testAbortInternal(2);
    }

    public static class FakeFileSystem extends RawLocalFileSystem {
        public FakeFileSystem() {
            super();
        }

        public URI getUri() {
            return URI.create("faildel:///");
        }

        @Override
        public boolean delete(Path p, boolean recursive) throws IOException {
            throw new IOException("fake delete failed");
        }
    }

    @Test
    public void testFailAbortV1() throws Exception {
        testFailAbortInternal(1);
    }

    @Test
    public void testFailAbortV2() throws Exception {
        testFailAbortInternal(2);
    }

    static class RLFS extends RawLocalFileSystem {
        private final ThreadLocal<Boolean> needNull = new ThreadLocal<Boolean>() {
            @Override
            protected Boolean initialValue() {
                return true;
            }
        };

        public RLFS() {
        }

        @Override
        public FileStatus getFileStatus(Path f) throws IOException {
            if ((needNull.get()) && (TestFileOutputCommitter.OUT_SUB_DIR.toUri().getPath().equals(f.toUri().getPath()))) {
                needNull.set(false);// lie once per thread

                return null;
            }
            return super.getFileStatus(f);
        }
    }

    @Test
    public void testConcurrentCommitTaskWithSubDirV1() throws Exception {
        testConcurrentCommitTaskWithSubDir(1);
    }

    @Test
    public void testConcurrentCommitTaskWithSubDirV2() throws Exception {
        testConcurrentCommitTaskWithSubDir(2);
    }

    /**
     * The class provides a overrided implementation of commitJobInternal which
     * causes the commit failed for the first time then succeed.
     */
    public static class CommitterWithFailedThenSucceed extends FileOutputCommitter {
        boolean firstTimeFail = true;

        public CommitterWithFailedThenSucceed(Path outputPath, JobContext context) throws IOException {
            super(outputPath, context);
        }

        @Override
        protected void commitJobInternal(JobContext context) throws IOException {
            super.commitJobInternal(context);
            if (firstTimeFail) {
                firstTimeFail = false;
                throw new IOException();
            } else {
                // succeed then, nothing to do
            }
        }
    }
}

