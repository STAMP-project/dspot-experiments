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


import FileOutputCommitter.SUCCEEDED_FILE_NAME;
import java.io.File;
import java.io.IOException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static FileOutputCommitter.SUCCEEDED_FILE_NAME;
import static JobStatus.FAILED;


/**
 * A JUnit test to test Map-Reduce job cleanup.
 */
@SuppressWarnings("deprecation")
public class TestJobCleanup {
    private static String TEST_ROOT_DIR = new File((((System.getProperty("test.build.data", "/tmp")) + "/") + "test-job-cleanup")).toString();

    private static final String CUSTOM_CLEANUP_FILE_NAME = "_custom_cleanup";

    private static final String ABORT_KILLED_FILE_NAME = "_custom_abort_killed";

    private static final String ABORT_FAILED_FILE_NAME = "_custom_abort_failed";

    private static FileSystem fileSys = null;

    private static MiniMRCluster mr = null;

    private static Path inDir = null;

    private static Path emptyInDir = null;

    private static int outDirs = 0;

    private static final Logger LOG = LoggerFactory.getLogger(TestJobCleanup.class);

    /**
     * Committer with deprecated
     * {@link FileOutputCommitter#cleanupJob(JobContext)} making a _failed/_killed
     * in the output folder
     */
    static class CommitterWithCustomDeprecatedCleanup extends FileOutputCommitter {
        @Override
        public void cleanupJob(JobContext context) throws IOException {
            System.err.println("---- HERE ----");
            JobConf conf = context.getJobConf();
            Path outputPath = FileOutputFormat.getOutputPath(conf);
            FileSystem fs = outputPath.getFileSystem(conf);
            fs.create(new Path(outputPath, TestJobCleanup.CUSTOM_CLEANUP_FILE_NAME)).close();
        }

        @Override
        public void commitJob(JobContext context) throws IOException {
            cleanupJob(context);
        }

        @Override
        public void abortJob(JobContext context, int i) throws IOException {
            cleanupJob(context);
        }
    }

    /**
     * Committer with abort making a _failed/_killed in the output folder
     */
    static class CommitterWithCustomAbort extends FileOutputCommitter {
        @Override
        public void abortJob(JobContext context, int state) throws IOException {
            JobConf conf = context.getJobConf();
            Path outputPath = FileOutputFormat.getOutputPath(conf);
            FileSystem fs = outputPath.getFileSystem(conf);
            String fileName = (state == (FAILED)) ? TestJobCleanup.ABORT_FAILED_FILE_NAME : TestJobCleanup.ABORT_KILLED_FILE_NAME;
            fs.create(new Path(outputPath, fileName)).close();
        }
    }

    /**
     * Test default cleanup/abort behavior
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testDefaultCleanupAndAbort() throws IOException {
        // check with a successful job
        testSuccessfulJob(SUCCEEDED_FILE_NAME, FileOutputCommitter.class, new String[]{  });
        // check with a failed job
        testFailedJob(null, FileOutputCommitter.class, new String[]{ SUCCEEDED_FILE_NAME });
        // check default abort job kill
        testKilledJob(null, FileOutputCommitter.class, new String[]{ SUCCEEDED_FILE_NAME });
    }

    /**
     * Test if a failed job with custom committer runs the abort code.
     *
     * @throws IOException
     * 		
     */
    @Test
    public void testCustomAbort() throws IOException {
        // check with a successful job
        testSuccessfulJob(SUCCEEDED_FILE_NAME, TestJobCleanup.CommitterWithCustomAbort.class, new String[]{ TestJobCleanup.ABORT_FAILED_FILE_NAME, TestJobCleanup.ABORT_KILLED_FILE_NAME });
        // check with a failed job
        testFailedJob(TestJobCleanup.ABORT_FAILED_FILE_NAME, TestJobCleanup.CommitterWithCustomAbort.class, new String[]{ SUCCEEDED_FILE_NAME, TestJobCleanup.ABORT_KILLED_FILE_NAME });
        // check with a killed job
        testKilledJob(TestJobCleanup.ABORT_KILLED_FILE_NAME, TestJobCleanup.CommitterWithCustomAbort.class, new String[]{ SUCCEEDED_FILE_NAME, TestJobCleanup.ABORT_FAILED_FILE_NAME });
    }

    /**
     * Test if a failed job with custom committer runs the deprecated
     * {@link FileOutputCommitter#cleanupJob(JobContext)} code for api
     * compatibility testing.
     */
    @Test
    public void testCustomCleanup() throws IOException {
        // check with a successful job
        testSuccessfulJob(TestJobCleanup.CUSTOM_CLEANUP_FILE_NAME, TestJobCleanup.CommitterWithCustomDeprecatedCleanup.class, new String[]{  });
        // check with a failed job
        testFailedJob(TestJobCleanup.CUSTOM_CLEANUP_FILE_NAME, TestJobCleanup.CommitterWithCustomDeprecatedCleanup.class, new String[]{ SUCCEEDED_FILE_NAME });
        // check with a killed job
        testKilledJob(TestJobCleanup.CUSTOM_CLEANUP_FILE_NAME, TestJobCleanup.CommitterWithCustomDeprecatedCleanup.class, new String[]{ SUCCEEDED_FILE_NAME });
    }
}

