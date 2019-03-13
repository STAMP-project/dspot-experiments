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
package org.apache.flink.streaming.api.functions.sink.filesystem;


import PartFileWriter.PartFileFactory;
import RecoverableFsDataOutputStream.Committer;
import java.io.File;
import java.io.IOException;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.fs.RecoverableFsDataOutputStream;
import org.apache.flink.core.fs.local.LocalFileSystem;
import org.apache.flink.core.fs.local.LocalRecoverableWriter;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.api.functions.sink.filesystem.utils.NoOpCommitter;
import org.apache.flink.streaming.api.functions.sink.filesystem.utils.NoOpRecoverable;
import org.apache.flink.streaming.api.functions.sink.filesystem.utils.NoOpRecoverableFsDataOutputStream;
import org.apache.flink.streaming.api.functions.sink.filesystem.utils.NoOpRecoverableWriter;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests for the {@code Bucket}.
 */
public class BucketTest {
    @ClassRule
    public static final TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

    @Test
    public void shouldNotCleanupResumablesThatArePartOfTheAckedCheckpoint() throws IOException {
        final File outDir = BucketTest.TEMP_FOLDER.newFolder();
        final Path path = new Path(outDir.toURI());
        final BucketTest.TestRecoverableWriter recoverableWriter = BucketTest.getRecoverableWriter(path);
        final Bucket<String, String> bucketUnderTest = BucketTest.createBucket(recoverableWriter, path, 0, 0);
        bucketUnderTest.write("test-element", 0L);
        final BucketState<String> state = bucketUnderTest.onReceptionOfCheckpoint(0L);
        Assert.assertThat(state, BucketTest.hasActiveInProgressFile());
        bucketUnderTest.onSuccessfulCompletionOfCheckpoint(0L);
        Assert.assertThat(recoverableWriter, BucketTest.hasCalledDiscard(0));// it did not discard as this is still valid.

    }

    @Test
    public void shouldCleanupOutdatedResumablesOnCheckpointAck() throws IOException {
        final File outDir = BucketTest.TEMP_FOLDER.newFolder();
        final Path path = new Path(outDir.toURI());
        final BucketTest.TestRecoverableWriter recoverableWriter = BucketTest.getRecoverableWriter(path);
        final Bucket<String, String> bucketUnderTest = BucketTest.createBucket(recoverableWriter, path, 0, 0);
        bucketUnderTest.write("test-element", 0L);
        final BucketState<String> state = bucketUnderTest.onReceptionOfCheckpoint(0L);
        Assert.assertThat(state, BucketTest.hasActiveInProgressFile());
        bucketUnderTest.onSuccessfulCompletionOfCheckpoint(0L);
        bucketUnderTest.onReceptionOfCheckpoint(1L);
        bucketUnderTest.onReceptionOfCheckpoint(2L);
        bucketUnderTest.onSuccessfulCompletionOfCheckpoint(2L);
        Assert.assertThat(recoverableWriter, BucketTest.hasCalledDiscard(2));// that is for checkpoints 0 and 1

    }

    @Test
    public void shouldCleanupResumableAfterRestoring() throws Exception {
        final File outDir = BucketTest.TEMP_FOLDER.newFolder();
        final Path path = new Path(outDir.toURI());
        final BucketTest.TestRecoverableWriter recoverableWriter = BucketTest.getRecoverableWriter(path);
        final Bucket<String, String> bucketUnderTest = BucketTest.createBucket(recoverableWriter, path, 0, 0);
        bucketUnderTest.write("test-element", 0L);
        final BucketState<String> state = bucketUnderTest.onReceptionOfCheckpoint(0L);
        Assert.assertThat(state, BucketTest.hasActiveInProgressFile());
        bucketUnderTest.onSuccessfulCompletionOfCheckpoint(0L);
        final BucketTest.TestRecoverableWriter newRecoverableWriter = BucketTest.getRecoverableWriter(path);
        BucketTest.restoreBucket(newRecoverableWriter, 0, 1, state);
        Assert.assertThat(newRecoverableWriter, BucketTest.hasCalledDiscard(1));// that is for checkpoints 0 and 1

    }

    @Test
    public void shouldNotCallCleanupWithoutInProgressPartFiles() throws Exception {
        final File outDir = BucketTest.TEMP_FOLDER.newFolder();
        final Path path = new Path(outDir.toURI());
        final BucketTest.TestRecoverableWriter recoverableWriter = BucketTest.getRecoverableWriter(path);
        final Bucket<String, String> bucketUnderTest = BucketTest.createBucket(recoverableWriter, path, 0, 0);
        final BucketState<String> state = bucketUnderTest.onReceptionOfCheckpoint(0L);
        Assert.assertThat(state, BucketTest.hasNoActiveInProgressFile());
        bucketUnderTest.onReceptionOfCheckpoint(1L);
        bucketUnderTest.onReceptionOfCheckpoint(2L);
        bucketUnderTest.onSuccessfulCompletionOfCheckpoint(2L);
        Assert.assertThat(recoverableWriter, BucketTest.hasCalledDiscard(0));// we have no in-progress file.

    }

    // --------------------------- Checking Restore ---------------------------
    @Test
    public void inProgressFileShouldBeCommittedIfWriterDoesNotSupportResume() throws IOException {
        final BucketTest.StubNonResumableWriter nonResumableWriter = new BucketTest.StubNonResumableWriter();
        final Bucket<String, String> bucket = getRestoredBucketWithOnlyInProgressPart(nonResumableWriter);
        Assert.assertThat(nonResumableWriter, BucketTest.hasMethodCallCountersEqualTo(1, 0, 1));
        Assert.assertThat(bucket, BucketTest.hasNullInProgressFile(true));
    }

    @Test
    public void inProgressFileShouldBeRestoredIfWriterSupportsResume() throws IOException {
        final BucketTest.StubResumableWriter resumableWriter = new BucketTest.StubResumableWriter();
        final Bucket<String, String> bucket = getRestoredBucketWithOnlyInProgressPart(resumableWriter);
        Assert.assertThat(resumableWriter, BucketTest.hasMethodCallCountersEqualTo(1, 1, 0));
        Assert.assertThat(bucket, BucketTest.hasNullInProgressFile(false));
    }

    @Test
    public void pendingFilesShouldBeRestored() throws IOException {
        final int expectedRecoverForCommitCounter = 10;
        final BucketTest.StubNonResumableWriter writer = new BucketTest.StubNonResumableWriter();
        final Bucket<String, String> bucket = getRestoredBucketWithOnlyPendingParts(writer, expectedRecoverForCommitCounter);
        Assert.assertThat(writer, BucketTest.hasMethodCallCountersEqualTo(0, 0, expectedRecoverForCommitCounter));
        Assert.assertThat(bucket, BucketTest.hasNullInProgressFile(true));
    }

    // ------------------------------- Mock Classes --------------------------------
    private static class TestRecoverableWriter extends LocalRecoverableWriter {
        private int cleanupCallCounter = 0;

        TestRecoverableWriter(LocalFileSystem fs) {
            super(fs);
        }

        int getCleanupCallCounter() {
            return cleanupCallCounter;
        }

        @Override
        public boolean requiresCleanupOfRecoverableState() {
            // here we return true so that the cleanupRecoverableState() is called.
            return true;
        }

        @Override
        public boolean cleanupRecoverableState(ResumeRecoverable resumable) throws IOException {
            (cleanupCallCounter)++;
            return false;
        }

        @Override
        public String toString() {
            return ("TestRecoverableWriter has called discardRecoverableState() " + (cleanupCallCounter)) + " times.";
        }
    }

    /**
     * A test implementation of a {@link RecoverableWriter} that does not support
     * resuming, i.e. keep on writing to the in-progress file at the point we were
     * before the failure.
     */
    private static class StubResumableWriter extends BucketTest.BaseStubWriter {
        StubResumableWriter() {
            super(true);
        }
    }

    /**
     * A test implementation of a {@link RecoverableWriter} that does not support
     * resuming, i.e. keep on writing to the in-progress file at the point we were
     * before the failure.
     */
    private static class StubNonResumableWriter extends BucketTest.BaseStubWriter {
        StubNonResumableWriter() {
            super(false);
        }
    }

    /**
     * A test implementation of a {@link RecoverableWriter} that does not support
     * resuming, i.e. keep on writing to the in-progress file at the point we were
     * before the failure.
     */
    private static class BaseStubWriter extends NoOpRecoverableWriter {
        private final boolean supportsResume;

        private int supportsResumeCallCounter = 0;

        private int recoverCallCounter = 0;

        private int recoverForCommitCallCounter = 0;

        private BaseStubWriter(final boolean supportsResume) {
            this.supportsResume = supportsResume;
        }

        int getSupportsResumeCallCounter() {
            return supportsResumeCallCounter;
        }

        int getRecoverCallCounter() {
            return recoverCallCounter;
        }

        int getRecoverForCommitCallCounter() {
            return recoverForCommitCallCounter;
        }

        @Override
        public RecoverableFsDataOutputStream recover(ResumeRecoverable resumable) throws IOException {
            (recoverCallCounter)++;
            return new NoOpRecoverableFsDataOutputStream();
        }

        @Override
        public Committer recoverForCommit(CommitRecoverable resumable) throws IOException {
            checkArgument((resumable instanceof NoOpRecoverable));
            (recoverForCommitCallCounter)++;
            return new NoOpCommitter();
        }

        @Override
        public boolean supportsResume() {
            (supportsResumeCallCounter)++;
            return supportsResume;
        }
    }

    // ------------------------------- Utility Methods --------------------------------
    private static final String bucketId = "testing-bucket";

    private static final RollingPolicy<String, String> rollingPolicy = DefaultRollingPolicy.create().build();

    private static final PartFileFactory<String, String> partFileFactory = new RowWisePartWriter.Factory<>(new SimpleStringEncoder<>());
}

