/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.connectors.fs.bucketing;


import java.io.File;
import java.util.List;
import java.util.Map;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.OperatorStateStore;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.state.FunctionInitializationContext;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.streaming.util.OperatorSnapshotUtil;
import org.apache.flink.testutils.migration.MigrationVersion;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for checking whether {@link BucketingSink} can restore from snapshots that were done
 * using previous Flink versions' {@link BucketingSink}.
 *
 * <p>For regenerating the binary snapshot file you have to run the {@code write*()} method on
 * the corresponding Flink release-* branch.
 */
@RunWith(Parameterized.class)
public class BucketingSinkMigrationTest {
    /**
     * TODO change this to the corresponding savepoint version to be written (e.g. {@link MigrationVersion#v1_3} for 1.3)
     * TODO and remove all @Ignore annotations on write*Snapshot() methods to generate savepoints
     * TODO Note: You should generate the savepoint based on the release branch instead of the master.
     */
    private final MigrationVersion flinkGenerateSavepointVersion = null;

    @ClassRule
    public static TemporaryFolder tempFolder = new TemporaryFolder();

    private final MigrationVersion testMigrateVersion;

    private final String expectedBucketFilesPrefix;

    public BucketingSinkMigrationTest(Tuple2<MigrationVersion, String> migrateVersionAndExpectedBucketFilesPrefix) {
        this.testMigrateVersion = migrateVersionAndExpectedBucketFilesPrefix.f0;
        this.expectedBucketFilesPrefix = migrateVersionAndExpectedBucketFilesPrefix.f1;
    }

    @Test
    public void testRestore() throws Exception {
        final File outDir = BucketingSinkMigrationTest.tempFolder.newFolder();
        BucketingSinkMigrationTest.ValidatingBucketingSink<String> sink = ((BucketingSinkMigrationTest.ValidatingBucketingSink<String>) (new BucketingSinkMigrationTest.ValidatingBucketingSink<String>(outDir.getAbsolutePath(), expectedBucketFilesPrefix).setWriter(new org.apache.flink.streaming.connectors.fs.StringWriter<String>()).setBatchSize(5).setPartPrefix(BucketingSinkTestUtils.PART_PREFIX).setInProgressPrefix("").setPendingPrefix("").setValidLengthPrefix("").setInProgressSuffix(BucketingSinkTestUtils.IN_PROGRESS_SUFFIX).setPendingSuffix(BucketingSinkTestUtils.PENDING_SUFFIX).setValidLengthSuffix(BucketingSinkTestUtils.VALID_LENGTH_SUFFIX).setUseTruncate(false)));// don't use truncate because files do not exist

        OneInputStreamOperatorTestHarness<String, Object> testHarness = new OneInputStreamOperatorTestHarness(new org.apache.flink.streaming.api.operators.StreamSink(sink), 10, 1, 0);
        testHarness.setup();
        testHarness.initializeState(OperatorSnapshotUtil.getResourceFilename((("bucketing-sink-migration-test-flink" + (testMigrateVersion)) + "-snapshot")));
        testHarness.open();
        Assert.assertTrue(sink.initializeCalled);
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord("test1", 0L));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord("test2", 0L));
        BucketingSinkTestUtils.checkLocalFs(outDir, 1, 1, 0, 0);
        testHarness.close();
    }

    static class ValidatingBucketingSink<T> extends BucketingSink<T> {
        private static final long serialVersionUID = -4263974081712009141L;

        public boolean initializeCalled = false;

        private final String expectedBucketFilesPrefix;

        ValidatingBucketingSink(String basePath, String expectedBucketFilesPrefix) {
            super(basePath);
            this.expectedBucketFilesPrefix = expectedBucketFilesPrefix;
        }

        /**
         * The actual paths in this depend on the binary checkpoint so it you update this the paths
         * here have to be updated as well.
         */
        @Override
        public void initializeState(FunctionInitializationContext context) throws Exception {
            OperatorStateStore stateStore = context.getOperatorStateStore();
            ListState<State<T>> restoredBucketStates = stateStore.getSerializableListState("bucket-states");
            if (context.isRestored()) {
                for (State<T> states : restoredBucketStates.get()) {
                    for (String bucketPath : states.bucketStates.keySet()) {
                        BucketState state = states.getBucketState(new Path(bucketPath));
                        String current = state.currentFile;
                        long validLength = state.currentFileValidLength;
                        Assert.assertEquals(((expectedBucketFilesPrefix) + "4"), current);
                        Assert.assertEquals(6, validLength);
                        List<String> pendingFiles = state.pendingFiles;
                        Assert.assertTrue(pendingFiles.isEmpty());
                        final Map<Long, List<String>> pendingFilesPerCheckpoint = state.pendingFilesPerCheckpoint;
                        Assert.assertEquals(1, pendingFilesPerCheckpoint.size());
                        for (Map.Entry<Long, List<String>> entry : pendingFilesPerCheckpoint.entrySet()) {
                            long checkpoint = entry.getKey();
                            List<String> files = entry.getValue();
                            Assert.assertEquals(0L, checkpoint);
                            Assert.assertEquals(4, files.size());
                            for (int i = 0; i < 4; i++) {
                                Assert.assertEquals(((expectedBucketFilesPrefix) + i), files.get(i));
                            }
                        }
                    }
                }
            }
            initializeCalled = true;
            super.initializeState(context);
        }
    }
}

