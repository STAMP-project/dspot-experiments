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


import JobStatus.State.KILLED;
import com.amazonaws.services.s3.AmazonS3;
import com.google.common.collect.Sets;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.s3a.AWSClientIOException;
import org.apache.hadoop.fs.s3a.MockS3AFileSystem;
import org.apache.hadoop.fs.s3a.S3AFileSystem;
import org.apache.hadoop.fs.s3a.commit.files.PendingSet;
import org.apache.hadoop.fs.s3a.commit.files.SinglePendingCommit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.JobID;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskType;
import org.hamcrest.core.StringStartsWith;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The main unit test suite of the staging committer.
 * Parameterized on thread count and unique filename policy.
 */
@RunWith(Parameterized.class)
public class TestStagingCommitter extends StagingTestBase.MiniDFSTest {
    private static final JobID JOB_ID = new JobID("job", 1);

    private static final TaskAttemptID AID = new TaskAttemptID(new org.apache.hadoop.mapreduce.TaskID(TestStagingCommitter.JOB_ID, TaskType.REDUCE, 2), 3);

    private static final Logger LOG = LoggerFactory.getLogger(TestStagingCommitter.class);

    private final int numThreads;

    private final boolean uniqueFilenames;

    private JobContext job = null;

    private TaskAttemptContext tac = null;

    private Configuration conf = null;

    private MockedStagingCommitter jobCommitter = null;

    private MockedStagingCommitter committer = null;

    // created in Before
    private S3AFileSystem mockFS = null;

    private MockS3AFileSystem wrapperFS = null;

    // created in Before
    private StagingTestBase.ClientResults results = null;

    private StagingTestBase.ClientErrors errors = null;

    private AmazonS3 mockClient = null;

    private File tmpDir;

    public TestStagingCommitter(int numThreads, boolean uniqueFilenames) {
        this.numThreads = numThreads;
        this.uniqueFilenames = uniqueFilenames;
    }

    @Test
    public void testUUIDPropagation() throws Exception {
        Configuration config = new Configuration();
        String jobUUID = addUUID(config);
        assertEquals("Upload UUID", jobUUID, StagingCommitter.getUploadUUID(config, TestStagingCommitter.JOB_ID));
    }

    @Test
    public void testAttemptPathConstructionNoSchema() throws Exception {
        Configuration config = new Configuration();
        final String jobUUID = addUUID(config);
        config.set(BUFFER_DIR, "/tmp/mr-local-0,/tmp/mr-local-1");
        String commonPath = "file:/tmp/mr-local-";
        assertThat("Missing scheme should produce local file paths", getLocalTaskAttemptTempDir(config, jobUUID, tac.getTaskAttemptID()).toString(), StringStartsWith.startsWith(commonPath));
    }

    @Test
    public void testAttemptPathConstructionWithSchema() throws Exception {
        Configuration config = new Configuration();
        final String jobUUID = addUUID(config);
        String commonPath = "file:/tmp/mr-local-";
        config.set(BUFFER_DIR, "file:/tmp/mr-local-0,file:/tmp/mr-local-1");
        assertThat("Path should be the same with file scheme", getLocalTaskAttemptTempDir(config, jobUUID, tac.getTaskAttemptID()).toString(), StringStartsWith.startsWith(commonPath));
    }

    @Test
    public void testAttemptPathConstructionWrongSchema() throws Exception {
        Configuration config = new Configuration();
        final String jobUUID = addUUID(config);
        config.set(BUFFER_DIR, "hdfs://nn:8020/tmp/mr-local-0,hdfs://nn:8020/tmp/mr-local-1");
        intercept(IllegalArgumentException.class, "Wrong FS", () -> getLocalTaskAttemptTempDir(config, jobUUID, tac.getTaskAttemptID()));
    }

    @Test
    public void testCommitPathConstruction() throws Exception {
        Path committedTaskPath = committer.getCommittedTaskPath(tac);
        assertEquals(("Path should be in HDFS: " + committedTaskPath), "hdfs", committedTaskPath.toUri().getScheme());
        String ending = (STAGING_UPLOADS) + "/_temporary/0/task_job_0001_r_000002";
        assertTrue(((("Did not end with \"" + ending) + "\" :") + committedTaskPath), committedTaskPath.toString().endsWith(ending));
    }

    @Test
    public void testSingleTaskCommit() throws Exception {
        Path file = new Path(commitTask(committer, tac, 1).iterator().next());
        List<String> uploads = results.getUploads();
        assertEquals(("Should initiate one upload: " + (results)), 1, uploads.size());
        Path committedPath = committer.getCommittedTaskPath(tac);
        FileSystem dfs = committedPath.getFileSystem(conf);
        assertEquals(("Should commit to HDFS: " + (committer)), StagingTestBase.MiniDFSTest.getDFS(), dfs);
        FileStatus[] stats = dfs.listStatus(committedPath);
        assertEquals(("Should produce one commit file: " + (results)), 1, stats.length);
        assertEquals(("Should name the commits file with the task ID: " + (results)), "task_job_0001_r_000002", stats[0].getPath().getName());
        PendingSet pending = PendingSet.load(dfs, stats[0].getPath());
        assertEquals("Should have one pending commit", 1, pending.size());
        SinglePendingCommit commit = pending.getCommits().get(0);
        assertEquals(("Should write to the correct bucket:" + (results)), StagingTestBase.BUCKET, commit.getBucket());
        assertEquals(("Should write to the correct key: " + (results)), (((StagingTestBase.OUTPUT_PREFIX) + "/") + (file.getName())), commit.getDestinationKey());
        TestStagingCommitter.assertValidUpload(results.getTagsByUpload(), commit);
    }

    /**
     * This originally verified that empty files weren't PUT. They are now.
     *
     * @throws Exception
     * 		on a failure
     */
    @Test
    public void testSingleTaskEmptyFileCommit() throws Exception {
        committer.setupTask(tac);
        Path attemptPath = committer.getTaskAttemptPath(tac);
        String rand = UUID.randomUUID().toString();
        TestStagingCommitter.writeOutputFile(tac.getTaskAttemptID(), attemptPath, rand, 0);
        committer.commitTask(tac);
        List<String> uploads = results.getUploads();
        assertEquals("Should initiate one upload", 1, uploads.size());
        Path committedPath = committer.getCommittedTaskPath(tac);
        FileSystem dfs = committedPath.getFileSystem(conf);
        assertEquals("Should commit to HDFS", StagingTestBase.MiniDFSTest.getDFS(), dfs);
        assertIsFile(dfs, committedPath);
        FileStatus[] stats = dfs.listStatus(committedPath);
        assertEquals("Should produce one commit file", 1, stats.length);
        assertEquals("Should name the commits file with the task ID", "task_job_0001_r_000002", stats[0].getPath().getName());
        PendingSet pending = PendingSet.load(dfs, stats[0].getPath());
        assertEquals("Should have one pending commit", 1, pending.size());
    }

    @Test
    public void testSingleTaskMultiFileCommit() throws Exception {
        int numFiles = 3;
        Set<String> files = commitTask(committer, tac, numFiles);
        List<String> uploads = results.getUploads();
        assertEquals("Should initiate multiple uploads", numFiles, uploads.size());
        Path committedPath = committer.getCommittedTaskPath(tac);
        FileSystem dfs = committedPath.getFileSystem(conf);
        assertEquals("Should commit to HDFS", StagingTestBase.MiniDFSTest.getDFS(), dfs);
        assertIsFile(dfs, committedPath);
        FileStatus[] stats = dfs.listStatus(committedPath);
        assertEquals("Should produce one commit file", 1, stats.length);
        assertEquals("Should name the commits file with the task ID", "task_job_0001_r_000002", stats[0].getPath().getName());
        List<SinglePendingCommit> pending = PendingSet.load(dfs, stats[0].getPath()).getCommits();
        assertEquals("Should have correct number of pending commits", files.size(), pending.size());
        Set<String> keys = Sets.newHashSet();
        for (SinglePendingCommit commit : pending) {
            assertEquals(("Should write to the correct bucket: " + commit), StagingTestBase.BUCKET, commit.getBucket());
            TestStagingCommitter.assertValidUpload(results.getTagsByUpload(), commit);
            keys.add(commit.getDestinationKey());
        }
        assertEquals("Should write to the correct key", files, keys);
    }

    @Test
    public void testTaskInitializeFailure() throws Exception {
        committer.setupTask(tac);
        errors.failOnInit(1);
        Path attemptPath = committer.getTaskAttemptPath(tac);
        FileSystem fs = attemptPath.getFileSystem(conf);
        TestStagingCommitter.writeOutputFile(tac.getTaskAttemptID(), attemptPath, UUID.randomUUID().toString(), 10);
        TestStagingCommitter.writeOutputFile(tac.getTaskAttemptID(), attemptPath, UUID.randomUUID().toString(), 10);
        intercept(AWSClientIOException.class, "Fail on init 1", "Should fail during init", () -> committer.commitTask(tac));
        assertEquals("Should have initialized one file upload", 1, results.getUploads().size());
        assertEquals("Should abort the upload", new HashSet(results.getUploads()), TestStagingCommitter.getAbortedIds(results.getAborts()));
        assertPathDoesNotExist(fs, "Should remove the attempt path", attemptPath);
    }

    @Test
    public void testTaskSingleFileUploadFailure() throws Exception {
        describe("Set up a single file upload to fail on upload 2");
        committer.setupTask(tac);
        errors.failOnUpload(2);
        Path attemptPath = committer.getTaskAttemptPath(tac);
        FileSystem fs = attemptPath.getFileSystem(conf);
        TestStagingCommitter.writeOutputFile(tac.getTaskAttemptID(), attemptPath, UUID.randomUUID().toString(), 10);
        intercept(((Class<? extends Exception>) (AWSClientIOException.class)), "Fail on upload 2", "Should fail during upload", () -> {
            committer.commitTask(tac);
            return committer.toString();
        });
        assertEquals("Should have attempted one file upload", 1, results.getUploads().size());
        assertEquals("Should abort the upload", results.getUploads().get(0), results.getAborts().get(0).getUploadId());
        assertPathDoesNotExist(fs, "Should remove the attempt path", attemptPath);
    }

    @Test
    public void testTaskMultiFileUploadFailure() throws Exception {
        committer.setupTask(tac);
        errors.failOnUpload(5);
        Path attemptPath = committer.getTaskAttemptPath(tac);
        FileSystem fs = attemptPath.getFileSystem(conf);
        TestStagingCommitter.writeOutputFile(tac.getTaskAttemptID(), attemptPath, UUID.randomUUID().toString(), 10);
        TestStagingCommitter.writeOutputFile(tac.getTaskAttemptID(), attemptPath, UUID.randomUUID().toString(), 10);
        intercept(((Class<? extends Exception>) (AWSClientIOException.class)), "Fail on upload 5", "Should fail during upload", () -> {
            committer.commitTask(tac);
            return committer.toString();
        });
        assertEquals("Should have attempted two file uploads", 2, results.getUploads().size());
        assertEquals("Should abort the upload", new HashSet(results.getUploads()), TestStagingCommitter.getAbortedIds(results.getAborts()));
        assertPathDoesNotExist(fs, "Should remove the attempt path", attemptPath);
    }

    @Test
    public void testTaskUploadAndAbortFailure() throws Exception {
        committer.setupTask(tac);
        errors.failOnUpload(5);
        errors.failOnAbort(0);
        Path attemptPath = committer.getTaskAttemptPath(tac);
        FileSystem fs = attemptPath.getFileSystem(conf);
        TestStagingCommitter.writeOutputFile(tac.getTaskAttemptID(), attemptPath, UUID.randomUUID().toString(), 10);
        TestStagingCommitter.writeOutputFile(tac.getTaskAttemptID(), attemptPath, UUID.randomUUID().toString(), 10);
        intercept(((Class<? extends Exception>) (AWSClientIOException.class)), "Fail on upload 5", "Should suppress abort failure, propagate upload failure", () -> {
            committer.commitTask(tac);
            return committer.toString();
        });
        assertEquals("Should have attempted two file uploads", 2, results.getUploads().size());
        assertEquals("Should not have succeeded with any aborts", new HashSet(), TestStagingCommitter.getAbortedIds(results.getAborts()));
        assertPathDoesNotExist(fs, "Should remove the attempt path", attemptPath);
    }

    @Test
    public void testSingleTaskAbort() throws Exception {
        committer.setupTask(tac);
        Path attemptPath = committer.getTaskAttemptPath(tac);
        FileSystem fs = attemptPath.getFileSystem(conf);
        Path outPath = TestStagingCommitter.writeOutputFile(tac.getTaskAttemptID(), attemptPath, UUID.randomUUID().toString(), 10);
        committer.abortTask(tac);
        assertEquals("Should not upload anything", 0, results.getUploads().size());
        assertEquals("Should not upload anything", 0, results.getParts().size());
        assertPathDoesNotExist(fs, "Should remove all attempt data", outPath);
        assertPathDoesNotExist(fs, "Should remove the attempt path", attemptPath);
    }

    @Test
    public void testJobCommit() throws Exception {
        Path jobAttemptPath = jobCommitter.getJobAttemptPath(job);
        FileSystem fs = jobAttemptPath.getFileSystem(conf);
        Set<String> uploads = runTasks(job, 4, 3);
        assertNotEquals(0, uploads.size());
        assertPathExists(fs, "No job attempt path", jobAttemptPath);
        jobCommitter.commitJob(job);
        assertEquals("Should have aborted no uploads", 0, results.getAborts().size());
        assertEquals("Should have deleted no uploads", 0, results.getDeletes().size());
        assertEquals("Should have committed all uploads", uploads, TestStagingCommitter.getCommittedIds(results.getCommits()));
        assertPathDoesNotExist(fs, "jobAttemptPath not deleted", jobAttemptPath);
    }

    @Test
    public void testJobCommitFailure() throws Exception {
        Path jobAttemptPath = jobCommitter.getJobAttemptPath(job);
        FileSystem fs = jobAttemptPath.getFileSystem(conf);
        Set<String> uploads = runTasks(job, 4, 3);
        assertPathExists(fs, "No job attempt path", jobAttemptPath);
        errors.failOnCommit(5);
        setMockLogLevel(MockS3AFileSystem.LOG_NAME);
        intercept(AWSClientIOException.class, "Fail on commit 5", "Should propagate the commit failure", () -> {
            jobCommitter.commitJob(job);
            return jobCommitter.toString();
        });
        assertEquals("Should have succeeded to commit some uploads", 5, results.getCommits().size());
        assertEquals("Should have deleted the files that succeeded", 5, results.getDeletes().size());
        Set<String> commits = results.getCommits().stream().map(( commit) -> (commit.getBucketName()) + (commit.getKey())).collect(Collectors.toSet());
        Set<String> deletes = results.getDeletes().stream().map(( delete) -> (delete.getBucketName()) + (delete.getKey())).collect(Collectors.toSet());
        assertEquals("Committed and deleted objects should match", commits, deletes);
        assertEquals("Mismatch in aborted upload count", 7, results.getAborts().size());
        Set<String> uploadIds = TestStagingCommitter.getCommittedIds(results.getCommits());
        uploadIds.addAll(TestStagingCommitter.getAbortedIds(results.getAborts()));
        assertEquals("Should have committed/deleted or aborted all uploads", uploads, uploadIds);
        assertPathDoesNotExist(fs, "jobAttemptPath not deleted", jobAttemptPath);
    }

    @Test
    public void testJobAbort() throws Exception {
        Path jobAttemptPath = jobCommitter.getJobAttemptPath(job);
        FileSystem fs = jobAttemptPath.getFileSystem(conf);
        Set<String> uploads = runTasks(job, 4, 3);
        assertPathExists(fs, "No job attempt path", jobAttemptPath);
        jobCommitter.abortJob(job, KILLED);
        assertEquals(("Should have committed no uploads: " + (jobCommitter)), 0, results.getCommits().size());
        assertEquals(("Should have deleted no uploads: " + (jobCommitter)), 0, results.getDeletes().size());
        assertEquals(("Should have aborted all uploads: " + (jobCommitter)), uploads, TestStagingCommitter.getAbortedIds(results.getAborts()));
        assertPathDoesNotExist(fs, "jobAttemptPath not deleted", jobAttemptPath);
    }
}

