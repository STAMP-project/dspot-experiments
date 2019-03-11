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


import FileOutputCommitter.SUCCEEDED_FILE_NAME;
import JobStatus.State;
import JobStatus.State.FAILED;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.JobStatus;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.junit.Test;

import static FileOutputCommitter.SUCCEEDED_FILE_NAME;


/**
 * A JUnit test to test Map-Reduce job committer.
 */
public class TestJobOutputCommitter extends HadoopTestCase {
    public TestJobOutputCommitter() throws IOException {
        super(HadoopTestCase.CLUSTER_MR, HadoopTestCase.LOCAL_FS, 1, 1);
    }

    private static String TEST_ROOT_DIR = new File((((System.getProperty("test.build.data", "/tmp")) + "/") + "test-job-output-committer")).toString();

    private static final String CUSTOM_CLEANUP_FILE_NAME = "_custom_cleanup";

    private static final String ABORT_KILLED_FILE_NAME = "_custom_abort_killed";

    private static final String ABORT_FAILED_FILE_NAME = "_custom_abort_failed";

    private static Path inDir = new Path(TestJobOutputCommitter.TEST_ROOT_DIR, "test-input");

    private static int outDirs = 0;

    private FileSystem fs;

    private Configuration conf = null;

    /**
     * Committer with deprecated {@link FileOutputCommitter#cleanupJob(JobContext)}
     * making a _failed/_killed in the output folder
     */
    static class CommitterWithCustomDeprecatedCleanup extends FileOutputCommitter {
        public CommitterWithCustomDeprecatedCleanup(Path outputPath, TaskAttemptContext context) throws IOException {
            super(outputPath, context);
        }

        @Override
        public void cleanupJob(JobContext context) throws IOException {
            System.err.println("---- HERE ----");
            Path outputPath = FileOutputFormat.getOutputPath(context);
            FileSystem fs = outputPath.getFileSystem(context.getConfiguration());
            fs.create(new Path(outputPath, TestJobOutputCommitter.CUSTOM_CLEANUP_FILE_NAME)).close();
        }
    }

    /**
     * Committer with abort making a _failed/_killed in the output folder
     */
    static class CommitterWithCustomAbort extends FileOutputCommitter {
        public CommitterWithCustomAbort(Path outputPath, TaskAttemptContext context) throws IOException {
            super(outputPath, context);
        }

        @Override
        public void abortJob(JobContext context, JobStatus.State state) throws IOException {
            Path outputPath = FileOutputFormat.getOutputPath(context);
            FileSystem fs = outputPath.getFileSystem(context.getConfiguration());
            String fileName = (state.equals(FAILED)) ? TestJobOutputCommitter.ABORT_FAILED_FILE_NAME : TestJobOutputCommitter.ABORT_KILLED_FILE_NAME;
            fs.create(new Path(outputPath, fileName)).close();
        }
    }

    static class MyOutputFormatWithCustomAbort<K, V> extends TextOutputFormat<K, V> {
        private OutputCommitter committer = null;

        public synchronized OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException {
            if ((committer) == null) {
                Path output = getOutputPath(context);
                committer = new TestJobOutputCommitter.CommitterWithCustomAbort(output, context);
            }
            return committer;
        }
    }

    static class MyOutputFormatWithCustomCleanup<K, V> extends TextOutputFormat<K, V> {
        private OutputCommitter committer = null;

        public synchronized OutputCommitter getOutputCommitter(TaskAttemptContext context) throws IOException {
            if ((committer) == null) {
                Path output = getOutputPath(context);
                committer = new TestJobOutputCommitter.CommitterWithCustomDeprecatedCleanup(output, context);
            }
            return committer;
        }
    }

    /**
     * Test default cleanup/abort behavior
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDefaultCleanupAndAbort() throws Exception {
        // check with a successful job
        testSuccessfulJob(SUCCEEDED_FILE_NAME, TextOutputFormat.class, new String[]{  });
        // check with a failed job
        testFailedJob(null, TextOutputFormat.class, new String[]{ SUCCEEDED_FILE_NAME });
        // check default abort job kill
        testKilledJob(null, TextOutputFormat.class, new String[]{ SUCCEEDED_FILE_NAME });
    }

    /**
     * Test if a failed job with custom committer runs the abort code.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCustomAbort() throws Exception {
        // check with a successful job
        testSuccessfulJob(SUCCEEDED_FILE_NAME, TestJobOutputCommitter.MyOutputFormatWithCustomAbort.class, new String[]{ TestJobOutputCommitter.ABORT_FAILED_FILE_NAME, TestJobOutputCommitter.ABORT_KILLED_FILE_NAME });
        // check with a failed job
        testFailedJob(TestJobOutputCommitter.ABORT_FAILED_FILE_NAME, TestJobOutputCommitter.MyOutputFormatWithCustomAbort.class, new String[]{ SUCCEEDED_FILE_NAME, TestJobOutputCommitter.ABORT_KILLED_FILE_NAME });
        // check with a killed job
        testKilledJob(TestJobOutputCommitter.ABORT_KILLED_FILE_NAME, TestJobOutputCommitter.MyOutputFormatWithCustomAbort.class, new String[]{ SUCCEEDED_FILE_NAME, TestJobOutputCommitter.ABORT_FAILED_FILE_NAME });
    }

    /**
     * Test if a failed job with custom committer runs the deprecated
     * {@link FileOutputCommitter#cleanupJob(JobContext)} code for api
     * compatibility testing.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testCustomCleanup() throws Exception {
        // check with a successful job
        testSuccessfulJob(TestJobOutputCommitter.CUSTOM_CLEANUP_FILE_NAME, TestJobOutputCommitter.MyOutputFormatWithCustomCleanup.class, new String[]{  });
        // check with a failed job
        testFailedJob(TestJobOutputCommitter.CUSTOM_CLEANUP_FILE_NAME, TestJobOutputCommitter.MyOutputFormatWithCustomCleanup.class, new String[]{ SUCCEEDED_FILE_NAME });
        // check with a killed job
        testKilledJob(TestJobOutputCommitter.CUSTOM_CLEANUP_FILE_NAME, TestJobOutputCommitter.MyOutputFormatWithCustomCleanup.class, new String[]{ SUCCEEDED_FILE_NAME });
    }
}

