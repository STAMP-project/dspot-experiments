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
package org.apache.hadoop.fs.s3a.commit;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.commit.magic.MagicS3GuardCommitter;
import org.apache.hadoop.fs.s3a.commit.staging.DirectoryStagingCommitter;
import org.apache.hadoop.fs.s3a.commit.staging.PartitionedStagingCommitter;
import org.apache.hadoop.fs.s3a.commit.staging.StagingCommitter;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.junit.Test;

import static InternalCommitterConstants.COMMITTER_NAME_STAGING;


/**
 * Tests for some aspects of the committer factory.
 * All tests are grouped into one single test so that only one
 * S3A FS client is set up and used for the entire run.
 * Saves time and money.
 */
public class ITestS3ACommitterFactory extends AbstractCommitITest {
    protected static final String INVALID_NAME = "invalid-name";

    /**
     * Counter to guarantee that even in parallel test runs, no job has the same
     * ID.
     */
    private String jobId;

    // A random task attempt id for testing.
    private String attempt0;

    private TaskAttemptID taskAttempt0;

    private Path outDir;

    private S3ACommitterFactory factory;

    private TaskAttemptContext tContext;

    /**
     * Parameterized list of bindings of committer name in config file to
     * expected class instantiated.
     */
    private static final Object[][] bindings = new Object[][]{ new Object[]{ COMMITTER_NAME_FILE, FileOutputCommitter.class }, new Object[]{ COMMITTER_NAME_DIRECTORY, DirectoryStagingCommitter.class }, new Object[]{ COMMITTER_NAME_PARTITIONED, PartitionedStagingCommitter.class }, new Object[]{ COMMITTER_NAME_STAGING, StagingCommitter.class }, new Object[]{ COMMITTER_NAME_MAGIC, MagicS3GuardCommitter.class } };

    /**
     * This is a ref to the FS conf, so changes here are visible
     * to callers querying the FS config.
     */
    private Configuration filesystemConfRef;

    private Configuration taskConfRef;

    @Test
    public void testEverything() throws Throwable {
        testImplicitFileBinding();
        testBindingsInTask();
        testBindingsInFSConfig();
        testInvalidFileBinding();
        testInvalidTaskBinding();
    }
}

