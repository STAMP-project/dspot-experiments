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
package org.apache.flink.runtime.jobmanager;


import java.io.File;
import org.apache.flink.configuration.UnmodifiableConfiguration;
import org.apache.flink.runtime.testutils.MiniClusterResource;
import org.apache.flink.util.TestLogger;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Small test to check that the {@link org.apache.flink.runtime.blob.BlobServer} cleanup is executed
 * after job termination.
 */
public class BlobsCleanupITCase extends TestLogger {
    private static final long RETRY_INTERVAL = 100L;

    @ClassRule
    public static final TemporaryFolder TEMPORARY_FOLDER = new TemporaryFolder();

    private static MiniClusterResource miniClusterResource;

    private static UnmodifiableConfiguration configuration;

    private static File blobBaseDir;

    /**
     * Specifies which test case to run in {@link #testBlobServerCleanup(TestCase)}.
     */
    private enum TestCase {

        JOB_FINISHES_SUCESSFULLY,
        JOB_IS_CANCELLED,
        JOB_FAILS,
        JOB_SUBMISSION_FAILS;}

    /**
     * Test cleanup for a job that finishes ordinarily.
     */
    @Test
    public void testBlobServerCleanupFinishedJob() throws Exception {
        testBlobServerCleanup(BlobsCleanupITCase.TestCase.JOB_FINISHES_SUCESSFULLY);
    }

    /**
     * Test cleanup for a job which is cancelled after submission.
     */
    @Test
    public void testBlobServerCleanupCancelledJob() throws Exception {
        testBlobServerCleanup(BlobsCleanupITCase.TestCase.JOB_IS_CANCELLED);
    }

    /**
     * Test cleanup for a job that fails (first a task fails, then the job recovers, then the whole
     * job fails due to a limited restart policy).
     */
    @Test
    public void testBlobServerCleanupFailedJob() throws Exception {
        testBlobServerCleanup(BlobsCleanupITCase.TestCase.JOB_FAILS);
    }

    /**
     * Test cleanup for a job that fails job submission (emulated by an additional BLOB not being
     * present).
     */
    @Test
    public void testBlobServerCleanupFailedSubmission() throws Exception {
        testBlobServerCleanup(BlobsCleanupITCase.TestCase.JOB_SUBMISSION_FAILS);
    }
}

