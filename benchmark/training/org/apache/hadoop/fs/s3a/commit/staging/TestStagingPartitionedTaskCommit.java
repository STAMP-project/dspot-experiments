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


import ConflictResolution.FAIL;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.PathExistsException;
import org.apache.hadoop.mapreduce.JobContext;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Mocking test of the partitioned committer.
 */
public class TestStagingPartitionedTaskCommit extends StagingTestBase.TaskCommitterTest<PartitionedStagingCommitter> {
    // The set of files used by this test
    private static List<String> relativeFiles = Lists.newArrayList();

    @Test
    public void testBadConflictMode() throws Throwable {
        getJob().getConfiguration().set(FS_S3A_COMMITTER_STAGING_CONFLICT_MODE, "merge");
        intercept(IllegalArgumentException.class, "MERGE", "committer conflict", this::newJobCommitter);
    }

    @Test
    public void testDefault() throws Exception {
        FileSystem mockS3 = getMockS3A();
        JobContext job = getJob();
        job.getConfiguration().unset(FS_S3A_COMMITTER_STAGING_CONFLICT_MODE);
        final PartitionedStagingCommitter committer = newTaskCommitter();
        committer.setupTask(getTAC());
        StagingTestBase.assertConflictResolution(committer, job, FAIL);
        StagingTestBase.createTestOutputFiles(TestStagingPartitionedTaskCommit.relativeFiles, committer.getTaskAttemptPath(getTAC()), getTAC().getConfiguration());
        // test failure when one partition already exists
        Mockito.reset(mockS3);
        Path exists = getParent();
        StagingTestBase.pathExists(mockS3, exists);
        intercept(PathExistsException.class, InternalCommitterConstants.E_DEST_EXISTS, (("Expected a PathExistsException as a partition" + " already exists:") + exists), () -> {
            committer.commitTask(getTAC());
            mockS3.getFileStatus(exists);
        });
        // test success
        Mockito.reset(mockS3);
        committer.commitTask(getTAC());
        Set<String> files = Sets.newHashSet();
        for (InitiateMultipartUploadRequest request : getMockResults().getRequests().values()) {
            assertEquals(StagingTestBase.BUCKET, request.getBucketName());
            files.add(request.getKey());
        }
        assertEquals("Should have the right number of uploads", TestStagingPartitionedTaskCommit.relativeFiles.size(), files.size());
        Set<String> expected = buildExpectedList(committer);
        assertEquals("Should have correct paths", expected, files);
    }

    @Test
    public void testFail() throws Exception {
        FileSystem mockS3 = getMockS3A();
        getTAC().getConfiguration().set(FS_S3A_COMMITTER_STAGING_CONFLICT_MODE, CONFLICT_MODE_FAIL);
        final PartitionedStagingCommitter committer = newTaskCommitter();
        committer.setupTask(getTAC());
        StagingTestBase.createTestOutputFiles(TestStagingPartitionedTaskCommit.relativeFiles, committer.getTaskAttemptPath(getTAC()), getTAC().getConfiguration());
        // test failure when one partition already exists
        Mockito.reset(mockS3);
        Path existsPath = getParent();
        StagingTestBase.pathExists(mockS3, existsPath);
        intercept(PathExistsException.class, "", ("Should complain because a partition already exists: " + existsPath), () -> committer.commitTask(getTAC()));
        // test success
        Mockito.reset(mockS3);
        committer.commitTask(getTAC());
        Set<String> files = Sets.newHashSet();
        for (InitiateMultipartUploadRequest request : getMockResults().getRequests().values()) {
            assertEquals(StagingTestBase.BUCKET, request.getBucketName());
            files.add(request.getKey());
        }
        assertEquals("Should have the right number of uploads", TestStagingPartitionedTaskCommit.relativeFiles.size(), files.size());
        Set<String> expected = buildExpectedList(committer);
        assertEquals("Should have correct paths", expected, files);
    }

    @Test
    public void testAppend() throws Exception {
        FileSystem mockS3 = getMockS3A();
        getTAC().getConfiguration().set(FS_S3A_COMMITTER_STAGING_CONFLICT_MODE, CONFLICT_MODE_APPEND);
        PartitionedStagingCommitter committer = newTaskCommitter();
        committer.setupTask(getTAC());
        StagingTestBase.createTestOutputFiles(TestStagingPartitionedTaskCommit.relativeFiles, committer.getTaskAttemptPath(getTAC()), getTAC().getConfiguration());
        // test success when one partition already exists
        Mockito.reset(mockS3);
        StagingTestBase.pathExists(mockS3, new Path(StagingTestBase.outputPath, TestStagingPartitionedTaskCommit.relativeFiles.get(2)).getParent());
        committer.commitTask(getTAC());
        Set<String> files = Sets.newHashSet();
        for (InitiateMultipartUploadRequest request : getMockResults().getRequests().values()) {
            assertEquals(StagingTestBase.BUCKET, request.getBucketName());
            files.add(request.getKey());
        }
        assertEquals("Should have the right number of uploads", TestStagingPartitionedTaskCommit.relativeFiles.size(), files.size());
        Set<String> expected = buildExpectedList(committer);
        assertEquals("Should have correct paths", expected, files);
    }

    @Test
    public void testReplace() throws Exception {
        // TODO: this committer needs to delete the data that already exists
        // This test should assert that the delete was done
        FileSystem mockS3 = getMockS3A();
        getTAC().getConfiguration().set(FS_S3A_COMMITTER_STAGING_CONFLICT_MODE, CONFLICT_MODE_REPLACE);
        PartitionedStagingCommitter committer = newTaskCommitter();
        committer.setupTask(getTAC());
        StagingTestBase.createTestOutputFiles(TestStagingPartitionedTaskCommit.relativeFiles, committer.getTaskAttemptPath(getTAC()), getTAC().getConfiguration());
        // test success when one partition already exists
        Mockito.reset(mockS3);
        StagingTestBase.pathExists(mockS3, new Path(StagingTestBase.outputPath, TestStagingPartitionedTaskCommit.relativeFiles.get(3)).getParent());
        committer.commitTask(getTAC());
        Set<String> files = Sets.newHashSet();
        for (InitiateMultipartUploadRequest request : getMockResults().getRequests().values()) {
            assertEquals(StagingTestBase.BUCKET, request.getBucketName());
            files.add(request.getKey());
        }
        assertEquals("Should have the right number of uploads", TestStagingPartitionedTaskCommit.relativeFiles.size(), files.size());
        Set<String> expected = buildExpectedList(committer);
        assertEquals("Should have correct paths", expected, files);
    }
}

