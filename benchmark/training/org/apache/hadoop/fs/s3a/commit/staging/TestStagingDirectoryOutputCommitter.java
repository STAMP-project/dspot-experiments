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
package org.apache.hadoop.fs.s3a.commit.staging;


import org.apache.hadoop.fs.FileSystem;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Mocking test of directory committer.
 */
public class TestStagingDirectoryOutputCommitter extends StagingTestBase.JobCommitterTest<DirectoryStagingCommitter> {
    @Test
    public void testBadConflictMode() throws Throwable {
        getJob().getConfiguration().set(FS_S3A_COMMITTER_STAGING_CONFLICT_MODE, "merge");
        intercept(IllegalArgumentException.class, "MERGE", "committer conflict", this::newJobCommitter);
    }

    @Test
    public void testDefaultConflictResolution() throws Exception {
        getJob().getConfiguration().unset(FS_S3A_COMMITTER_STAGING_CONFLICT_MODE);
        verifyFailureConflictOutcome();
    }

    @Test
    public void testFailConflictResolution() throws Exception {
        getJob().getConfiguration().set(FS_S3A_COMMITTER_STAGING_CONFLICT_MODE, CONFLICT_MODE_FAIL);
        verifyFailureConflictOutcome();
    }

    @Test
    public void testAppendConflictResolution() throws Exception {
        FileSystem mockS3 = getMockS3A();
        StagingTestBase.pathExists(mockS3, StagingTestBase.outputPath);
        getJob().getConfiguration().set(FS_S3A_COMMITTER_STAGING_CONFLICT_MODE, CONFLICT_MODE_APPEND);
        final DirectoryStagingCommitter committer = newJobCommitter();
        committer.setupJob(getJob());
        Mockito.verifyNoMoreInteractions(mockS3);
        Mockito.reset(mockS3);
        StagingTestBase.pathExists(mockS3, StagingTestBase.outputPath);
        committer.commitJob(getJob());
        StagingTestBase.verifyCompletion(mockS3);
    }

    @Test
    public void testReplaceConflictResolution() throws Exception {
        FileSystem mockS3 = getMockS3A();
        StagingTestBase.pathExists(mockS3, StagingTestBase.outputPath);
        getJob().getConfiguration().set(FS_S3A_COMMITTER_STAGING_CONFLICT_MODE, CONFLICT_MODE_REPLACE);
        final DirectoryStagingCommitter committer = newJobCommitter();
        committer.setupJob(getJob());
        Mockito.verifyNoMoreInteractions(mockS3);
        Mockito.reset(mockS3);
        StagingTestBase.pathExists(mockS3, StagingTestBase.outputPath);
        StagingTestBase.canDelete(mockS3, StagingTestBase.outputPath);
        committer.commitJob(getJob());
        StagingTestBase.verifyDeleted(mockS3, StagingTestBase.outputPath);
        StagingTestBase.verifyCompletion(mockS3);
    }
}

