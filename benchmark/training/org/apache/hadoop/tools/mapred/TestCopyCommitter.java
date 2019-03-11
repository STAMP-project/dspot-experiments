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
package org.apache.hadoop.tools.mapred;


import DistCpConstants.CONF_LABEL_ATOMIC_COPY;
import DistCpConstants.CONF_LABEL_DELETE_MISSING;
import DistCpConstants.CONF_LABEL_PRESERVE_STATUS;
import DistCpConstants.CONF_LABEL_TARGET_FINAL_PATH;
import DistCpConstants.CONF_LABEL_TARGET_WORK_PATH;
import FileAttribute.PERMISSION;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.security.Credentials;
import org.apache.hadoop.tools.CopyListing;
import org.apache.hadoop.tools.DistCpContext;
import org.apache.hadoop.tools.DistCpOptions;
import org.apache.hadoop.tools.util.TestDistCpUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCopyCommitter {
    private static final Logger LOG = LoggerFactory.getLogger(TestCopyCommitter.class);

    private static final Random rand = new Random();

    private static final Credentials CREDENTIALS = new Credentials();

    public static final int PORT = 39737;

    private static Configuration config;

    private static MiniDFSCluster cluster;

    @Test
    public void testNoCommitAction() throws IOException {
        TaskAttemptContext taskAttemptContext = getTaskAttemptContext(TestCopyCommitter.config);
        JobContext jobContext = new org.apache.hadoop.mapreduce.task.JobContextImpl(taskAttemptContext.getConfiguration(), taskAttemptContext.getTaskAttemptID().getJobID());
        OutputCommitter committer = new CopyCommitter(null, taskAttemptContext);
        committer.commitJob(jobContext);
        Assert.assertEquals("Commit Successful", taskAttemptContext.getStatus());
        // Test for idempotent commit
        committer.commitJob(jobContext);
        Assert.assertEquals("Commit Successful", taskAttemptContext.getStatus());
    }

    @Test
    public void testPreserveStatus() throws IOException {
        TaskAttemptContext taskAttemptContext = getTaskAttemptContext(TestCopyCommitter.config);
        JobContext jobContext = new org.apache.hadoop.mapreduce.task.JobContextImpl(taskAttemptContext.getConfiguration(), taskAttemptContext.getTaskAttemptID().getJobID());
        Configuration conf = jobContext.getConfiguration();
        String sourceBase;
        String targetBase;
        FileSystem fs = null;
        try {
            OutputCommitter committer = new CopyCommitter(null, taskAttemptContext);
            fs = FileSystem.get(conf);
            FsPermission sourcePerm = new FsPermission(((short) (511)));
            FsPermission initialPerm = new FsPermission(((short) (448)));
            sourceBase = TestDistCpUtils.createTestSetup(fs, sourcePerm);
            targetBase = TestDistCpUtils.createTestSetup(fs, initialPerm);
            final DistCpOptions options = new DistCpOptions.Builder(Collections.singletonList(new Path(sourceBase)), new Path("/out")).preserve(PERMISSION).build();
            options.appendToConf(conf);
            final DistCpContext context = new DistCpContext(options);
            context.setTargetPathExists(false);
            CopyListing listing = new org.apache.hadoop.tools.GlobbedCopyListing(conf, TestCopyCommitter.CREDENTIALS);
            Path listingFile = new Path(("/tmp1/" + (String.valueOf(TestCopyCommitter.rand.nextLong()))));
            listing.buildListing(listingFile, context);
            conf.set(CONF_LABEL_TARGET_WORK_PATH, targetBase);
            committer.commitJob(jobContext);
            checkDirectoryPermissions(fs, targetBase, sourcePerm);
            // Test for idempotent commit
            committer.commitJob(jobContext);
            checkDirectoryPermissions(fs, targetBase, sourcePerm);
        } finally {
            TestDistCpUtils.delete(fs, "/tmp1");
            conf.unset(CONF_LABEL_PRESERVE_STATUS);
        }
    }

    @Test
    public void testDeleteMissing() throws IOException {
        TaskAttemptContext taskAttemptContext = getTaskAttemptContext(TestCopyCommitter.config);
        JobContext jobContext = new org.apache.hadoop.mapreduce.task.JobContextImpl(taskAttemptContext.getConfiguration(), taskAttemptContext.getTaskAttemptID().getJobID());
        Configuration conf = jobContext.getConfiguration();
        String sourceBase;
        String targetBase;
        FileSystem fs = null;
        try {
            OutputCommitter committer = new CopyCommitter(null, taskAttemptContext);
            fs = FileSystem.get(conf);
            sourceBase = TestDistCpUtils.createTestSetup(fs, FsPermission.getDefault());
            targetBase = TestDistCpUtils.createTestSetup(fs, FsPermission.getDefault());
            String targetBaseAdd = TestDistCpUtils.createTestSetup(fs, FsPermission.getDefault());
            fs.rename(new Path(targetBaseAdd), new Path(targetBase));
            final DistCpOptions options = withSyncFolder(true).withDeleteMissing(true).build();
            options.appendToConf(conf);
            final DistCpContext context = new DistCpContext(options);
            CopyListing listing = new org.apache.hadoop.tools.GlobbedCopyListing(conf, TestCopyCommitter.CREDENTIALS);
            Path listingFile = new Path(("/tmp1/" + (String.valueOf(TestCopyCommitter.rand.nextLong()))));
            listing.buildListing(listingFile, context);
            conf.set(CONF_LABEL_TARGET_WORK_PATH, targetBase);
            conf.set(CONF_LABEL_TARGET_FINAL_PATH, targetBase);
            committer.commitJob(jobContext);
            TestDistCpUtils.verifyFoldersAreInSync(fs, targetBase, sourceBase);
            TestDistCpUtils.verifyFoldersAreInSync(fs, sourceBase, targetBase);
            // Test for idempotent commit
            committer.commitJob(jobContext);
            TestDistCpUtils.verifyFoldersAreInSync(fs, targetBase, sourceBase);
            TestDistCpUtils.verifyFoldersAreInSync(fs, sourceBase, targetBase);
        } finally {
            TestDistCpUtils.delete(fs, "/tmp1");
            conf.set(CONF_LABEL_DELETE_MISSING, "false");
        }
    }

    @Test
    public void testDeleteMissingFlatInterleavedFiles() throws IOException {
        TaskAttemptContext taskAttemptContext = getTaskAttemptContext(TestCopyCommitter.config);
        JobContext jobContext = new org.apache.hadoop.mapreduce.task.JobContextImpl(taskAttemptContext.getConfiguration(), taskAttemptContext.getTaskAttemptID().getJobID());
        Configuration conf = jobContext.getConfiguration();
        String sourceBase;
        String targetBase;
        FileSystem fs = null;
        try {
            OutputCommitter committer = new CopyCommitter(null, taskAttemptContext);
            fs = FileSystem.get(conf);
            sourceBase = "/tmp1/" + (String.valueOf(TestCopyCommitter.rand.nextLong()));
            targetBase = "/tmp1/" + (String.valueOf(TestCopyCommitter.rand.nextLong()));
            TestDistCpUtils.createFile(fs, (sourceBase + "/1"));
            TestDistCpUtils.createFile(fs, (sourceBase + "/3"));
            TestDistCpUtils.createFile(fs, (sourceBase + "/4"));
            TestDistCpUtils.createFile(fs, (sourceBase + "/5"));
            TestDistCpUtils.createFile(fs, (sourceBase + "/7"));
            TestDistCpUtils.createFile(fs, (sourceBase + "/8"));
            TestDistCpUtils.createFile(fs, (sourceBase + "/9"));
            TestDistCpUtils.createFile(fs, (targetBase + "/2"));
            TestDistCpUtils.createFile(fs, (targetBase + "/4"));
            TestDistCpUtils.createFile(fs, (targetBase + "/5"));
            TestDistCpUtils.createFile(fs, (targetBase + "/7"));
            TestDistCpUtils.createFile(fs, (targetBase + "/9"));
            TestDistCpUtils.createFile(fs, (targetBase + "/A"));
            final DistCpOptions options = withSyncFolder(true).withDeleteMissing(true).build();
            options.appendToConf(conf);
            final DistCpContext context = new DistCpContext(options);
            CopyListing listing = new org.apache.hadoop.tools.GlobbedCopyListing(conf, TestCopyCommitter.CREDENTIALS);
            Path listingFile = new Path(("/tmp1/" + (String.valueOf(TestCopyCommitter.rand.nextLong()))));
            listing.buildListing(listingFile, context);
            conf.set(CONF_LABEL_TARGET_WORK_PATH, targetBase);
            conf.set(CONF_LABEL_TARGET_FINAL_PATH, targetBase);
            committer.commitJob(jobContext);
            TestDistCpUtils.verifyFoldersAreInSync(fs, targetBase, sourceBase);
            Assert.assertEquals(4, fs.listStatus(new Path(targetBase)).length);
            // Test for idempotent commit
            committer.commitJob(jobContext);
            TestDistCpUtils.verifyFoldersAreInSync(fs, targetBase, sourceBase);
            Assert.assertEquals(4, fs.listStatus(new Path(targetBase)).length);
        } finally {
            TestDistCpUtils.delete(fs, "/tmp1");
            conf.set(CONF_LABEL_DELETE_MISSING, "false");
        }
    }

    @Test
    public void testAtomicCommitMissingFinal() throws IOException {
        TaskAttemptContext taskAttemptContext = getTaskAttemptContext(TestCopyCommitter.config);
        JobContext jobContext = new org.apache.hadoop.mapreduce.task.JobContextImpl(taskAttemptContext.getConfiguration(), taskAttemptContext.getTaskAttemptID().getJobID());
        Configuration conf = jobContext.getConfiguration();
        String workPath = "/tmp1/" + (String.valueOf(TestCopyCommitter.rand.nextLong()));
        String finalPath = "/tmp1/" + (String.valueOf(TestCopyCommitter.rand.nextLong()));
        FileSystem fs = null;
        try {
            OutputCommitter committer = new CopyCommitter(null, taskAttemptContext);
            fs = FileSystem.get(conf);
            fs.mkdirs(new Path(workPath));
            conf.set(CONF_LABEL_TARGET_WORK_PATH, workPath);
            conf.set(CONF_LABEL_TARGET_FINAL_PATH, finalPath);
            conf.setBoolean(CONF_LABEL_ATOMIC_COPY, true);
            assertPathExists(fs, "Work path", new Path(workPath));
            assertPathDoesNotExist(fs, "Final path", new Path(finalPath));
            committer.commitJob(jobContext);
            assertPathDoesNotExist(fs, "Work path", new Path(workPath));
            assertPathExists(fs, "Final path", new Path(finalPath));
            // Test for idempotent commit
            committer.commitJob(jobContext);
            assertPathDoesNotExist(fs, "Work path", new Path(workPath));
            assertPathExists(fs, "Final path", new Path(finalPath));
        } finally {
            TestDistCpUtils.delete(fs, workPath);
            TestDistCpUtils.delete(fs, finalPath);
            conf.setBoolean(CONF_LABEL_ATOMIC_COPY, false);
        }
    }

    @Test
    public void testAtomicCommitExistingFinal() throws IOException {
        TaskAttemptContext taskAttemptContext = getTaskAttemptContext(TestCopyCommitter.config);
        JobContext jobContext = new org.apache.hadoop.mapreduce.task.JobContextImpl(taskAttemptContext.getConfiguration(), taskAttemptContext.getTaskAttemptID().getJobID());
        Configuration conf = jobContext.getConfiguration();
        String workPath = "/tmp1/" + (String.valueOf(TestCopyCommitter.rand.nextLong()));
        String finalPath = "/tmp1/" + (String.valueOf(TestCopyCommitter.rand.nextLong()));
        FileSystem fs = null;
        try {
            OutputCommitter committer = new CopyCommitter(null, taskAttemptContext);
            fs = FileSystem.get(conf);
            fs.mkdirs(new Path(workPath));
            fs.mkdirs(new Path(finalPath));
            conf.set(CONF_LABEL_TARGET_WORK_PATH, workPath);
            conf.set(CONF_LABEL_TARGET_FINAL_PATH, finalPath);
            conf.setBoolean(CONF_LABEL_ATOMIC_COPY, true);
            assertPathExists(fs, "Work path", new Path(workPath));
            assertPathExists(fs, "Final path", new Path(finalPath));
            try {
                committer.commitJob(jobContext);
                Assert.fail("Should not be able to atomic-commit to pre-existing path.");
            } catch (Exception exception) {
                assertPathExists(fs, "Work path", new Path(workPath));
                assertPathExists(fs, "Final path", new Path(finalPath));
                TestCopyCommitter.LOG.info("Atomic-commit Test pass.");
            }
        } finally {
            TestDistCpUtils.delete(fs, workPath);
            TestDistCpUtils.delete(fs, finalPath);
            conf.setBoolean(CONF_LABEL_ATOMIC_COPY, false);
        }
    }

    private static class NullInputFormat extends InputFormat {
        @Override
        public List getSplits(JobContext context) throws IOException, InterruptedException {
            return Collections.emptyList();
        }

        @Override
        public RecordReader createRecordReader(InputSplit split, TaskAttemptContext context) throws IOException, InterruptedException {
            return null;
        }
    }
}

