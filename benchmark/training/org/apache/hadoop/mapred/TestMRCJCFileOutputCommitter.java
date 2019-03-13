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


import FileSystem.FS_DEFAULT_NAME_KEY;
import JobStatus.State.FAILED;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;

import static FileOutputCommitter.TEMP_DIR_NAME;
import static Reporter.NULL;


public class TestMRCJCFileOutputCommitter {
    private static Path outDir = new Path(GenericTestUtils.getTempPath("output"));

    // A random task attempt id for testing.
    private static String attempt = "attempt_200707121733_0001_m_000000_0";

    private static TaskAttemptID taskID = TaskAttemptID.forName(TestMRCJCFileOutputCommitter.attempt);

    private Text key1 = new Text("key1");

    private Text key2 = new Text("key2");

    private Text val1 = new Text("val1");

    private Text val2 = new Text("val2");

    @SuppressWarnings("unchecked")
    @Test
    public void testCommitter() throws Exception {
        JobConf job = new JobConf();
        setConfForFileOutputCommitter(job);
        JobContext jContext = new JobContextImpl(job, TestMRCJCFileOutputCommitter.taskID.getJobID());
        TaskAttemptContext tContext = new TaskAttemptContextImpl(job, TestMRCJCFileOutputCommitter.taskID);
        FileOutputCommitter committer = new FileOutputCommitter();
        FileOutputFormat.setWorkOutputPath(job, committer.getTaskAttemptPath(tContext));
        committer.setupJob(jContext);
        committer.setupTask(tContext);
        String file = "test.txt";
        // A reporter that does nothing
        Reporter reporter = NULL;
        // write output
        FileSystem localFs = FileSystem.getLocal(job);
        TextOutputFormat theOutputFormat = new TextOutputFormat();
        RecordWriter theRecordWriter = theOutputFormat.getRecordWriter(localFs, job, file, reporter);
        writeOutput(theRecordWriter, reporter);
        // do commit
        committer.commitTask(tContext);
        committer.commitJob(jContext);
        // validate output
        File expectedFile = new File(new Path(TestMRCJCFileOutputCommitter.outDir, file).toString());
        StringBuffer expectedOutput = new StringBuffer();
        expectedOutput.append(key1).append('\t').append(val1).append("\n");
        expectedOutput.append(val1).append("\n");
        expectedOutput.append(val2).append("\n");
        expectedOutput.append(key2).append("\n");
        expectedOutput.append(key1).append("\n");
        expectedOutput.append(key2).append('\t').append(val2).append("\n");
        String output = UtilsForTests.slurp(expectedFile);
        Assert.assertEquals(output, expectedOutput.toString());
    }

    @Test
    public void testAbort() throws IOException {
        FileUtil.fullyDelete(new File(TestMRCJCFileOutputCommitter.outDir.toString()));
        JobConf job = new JobConf();
        setConfForFileOutputCommitter(job);
        JobContext jContext = new JobContextImpl(job, TestMRCJCFileOutputCommitter.taskID.getJobID());
        TaskAttemptContext tContext = new TaskAttemptContextImpl(job, TestMRCJCFileOutputCommitter.taskID);
        FileOutputCommitter committer = new FileOutputCommitter();
        FileOutputFormat.setWorkOutputPath(job, committer.getTaskAttemptPath(tContext));
        // do setup
        committer.setupJob(jContext);
        committer.setupTask(tContext);
        String file = "test.txt";
        // A reporter that does nothing
        Reporter reporter = NULL;
        // write output
        FileSystem localFs = FileSystem.getLocal(job);
        TextOutputFormat theOutputFormat = new TextOutputFormat();
        RecordWriter theRecordWriter = theOutputFormat.getRecordWriter(localFs, job, file, reporter);
        writeOutput(theRecordWriter, reporter);
        // do abort
        committer.abortTask(tContext);
        File expectedFile = new File(new Path(committer.getTaskAttemptPath(tContext), file).toString());
        Assert.assertFalse("task temp dir still exists", expectedFile.exists());
        committer.abortJob(jContext, FAILED);
        expectedFile = new File(new Path(TestMRCJCFileOutputCommitter.outDir, TEMP_DIR_NAME).toString());
        Assert.assertFalse((("job temp dir " + expectedFile) + " still exists"), expectedFile.exists());
        Assert.assertEquals("Output directory not empty", 0, new File(TestMRCJCFileOutputCommitter.outDir.toString()).listFiles().length);
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
    public void testFailAbort() throws IOException {
        JobConf job = new JobConf();
        job.set(FS_DEFAULT_NAME_KEY, "faildel:///");
        job.setClass("fs.faildel.impl", TestMRCJCFileOutputCommitter.FakeFileSystem.class, FileSystem.class);
        setConfForFileOutputCommitter(job);
        JobContext jContext = new JobContextImpl(job, TestMRCJCFileOutputCommitter.taskID.getJobID());
        TaskAttemptContext tContext = new TaskAttemptContextImpl(job, TestMRCJCFileOutputCommitter.taskID);
        FileOutputCommitter committer = new FileOutputCommitter();
        FileOutputFormat.setWorkOutputPath(job, committer.getTaskAttemptPath(tContext));
        // do setup
        committer.setupJob(jContext);
        committer.setupTask(tContext);
        String file = "test.txt";
        File jobTmpDir = new File(committer.getJobAttemptPath(jContext).toUri().getPath());
        File taskTmpDir = new File(committer.getTaskAttemptPath(tContext).toUri().getPath());
        File expectedFile = new File(taskTmpDir, file);
        // A reporter that does nothing
        Reporter reporter = NULL;
        // write output
        FileSystem localFs = new TestMRCJCFileOutputCommitter.FakeFileSystem();
        TextOutputFormat theOutputFormat = new TextOutputFormat();
        RecordWriter theRecordWriter = theOutputFormat.getRecordWriter(localFs, job, expectedFile.getAbsolutePath(), reporter);
        writeOutput(theRecordWriter, reporter);
        // do abort
        Throwable th = null;
        try {
            committer.abortTask(tContext);
        } catch (IOException ie) {
            th = ie;
        }
        Assert.assertNotNull(th);
        Assert.assertTrue((th instanceof IOException));
        Assert.assertTrue(th.getMessage().contains("fake delete failed"));
        Assert.assertTrue((expectedFile + " does not exists"), expectedFile.exists());
        th = null;
        try {
            committer.abortJob(jContext, FAILED);
        } catch (IOException ie) {
            th = ie;
        }
        Assert.assertNotNull(th);
        Assert.assertTrue((th instanceof IOException));
        Assert.assertTrue(th.getMessage().contains("fake delete failed"));
        Assert.assertTrue("job temp dir does not exists", jobTmpDir.exists());
    }
}

