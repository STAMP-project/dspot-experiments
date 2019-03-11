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
package org.apache.flink.runtime.state.filesystem;


import CheckpointedStateScope.EXCLUSIVE;
import org.apache.flink.api.common.JobID;
import org.apache.flink.core.fs.EntropyInjectingFileSystem;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.core.fs.local.LocalFileSystem;
import org.apache.flink.runtime.state.CheckpointMetadataOutputStream;
import org.apache.flink.runtime.state.CheckpointStreamFactory.CheckpointStateOutputStream;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Tests verifying that the FsStateBackend passes the entropy injection option
 * to the FileSystem for state payload files, but not for metadata files.
 */
public class FsStateBackendEntropyTest {
    static final String ENTROPY_MARKER = "__ENTROPY__";

    static final String RESOLVED_MARKER = "+RESOLVED+";

    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    @Test
    public void testEntropyInjection() throws Exception {
        final FileSystem fs = new FsStateBackendEntropyTest.TestEntropyAwareFs();
        final Path checkpointDir = new Path(Path.fromLocalFile(tmp.newFolder()), ((FsStateBackendEntropyTest.ENTROPY_MARKER) + "/checkpoints"));
        final String checkpointDirStr = checkpointDir.toString();
        FsCheckpointStorage storage = new FsCheckpointStorage(fs, checkpointDir, null, new JobID(), 1024);
        FsCheckpointStorageLocation location = ((FsCheckpointStorageLocation) (storage.initializeLocationForCheckpoint(96562)));
        Assert.assertThat(location.getCheckpointDirectory().toString(), Matchers.startsWith(checkpointDirStr));
        Assert.assertThat(location.getSharedStateDirectory().toString(), Matchers.startsWith(checkpointDirStr));
        Assert.assertThat(location.getTaskOwnedStateDirectory().toString(), Matchers.startsWith(checkpointDirStr));
        Assert.assertThat(location.getMetadataFilePath().toString(), Matchers.not(Matchers.containsString(FsStateBackendEntropyTest.ENTROPY_MARKER)));
        // check entropy in task-owned state
        try (CheckpointStateOutputStream stream = storage.createTaskOwnedStateStream()) {
            stream.flush();
            FileStateHandle handle = ((FileStateHandle) (stream.closeAndGetHandle()));
            Assert.assertNotNull(handle);
            Assert.assertThat(handle.getFilePath().toString(), Matchers.not(Matchers.containsString(FsStateBackendEntropyTest.ENTROPY_MARKER)));
            Assert.assertThat(handle.getFilePath().toString(), Matchers.containsString(FsStateBackendEntropyTest.RESOLVED_MARKER));
        }
        // check entropy in the exclusive/shared state
        try (CheckpointStateOutputStream stream = location.createCheckpointStateOutputStream(EXCLUSIVE)) {
            stream.flush();
            FileStateHandle handle = ((FileStateHandle) (stream.closeAndGetHandle()));
            Assert.assertNotNull(handle);
            Assert.assertThat(handle.getFilePath().toString(), Matchers.not(Matchers.containsString(FsStateBackendEntropyTest.ENTROPY_MARKER)));
            Assert.assertThat(handle.getFilePath().toString(), Matchers.containsString(FsStateBackendEntropyTest.RESOLVED_MARKER));
        }
        // check that there is no entropy in the metadata
        // check entropy in the exclusive/shared state
        try (CheckpointMetadataOutputStream stream = location.createMetadataOutputStream()) {
            stream.flush();
            FsCompletedCheckpointStorageLocation handle = ((FsCompletedCheckpointStorageLocation) (stream.closeAndFinalizeCheckpoint()));
            Assert.assertNotNull(handle);
            // metadata files have no entropy
            Assert.assertThat(handle.getMetadataHandle().getFilePath().toString(), Matchers.not(Matchers.containsString(FsStateBackendEntropyTest.ENTROPY_MARKER)));
            Assert.assertThat(handle.getMetadataHandle().getFilePath().toString(), Matchers.not(Matchers.containsString(FsStateBackendEntropyTest.RESOLVED_MARKER)));
            // external location is the same as metadata, without the file name
            Assert.assertEquals(handle.getMetadataHandle().getFilePath().getParent().toString(), handle.getExternalPointer());
        }
    }

    private static class TestEntropyAwareFs extends LocalFileSystem implements EntropyInjectingFileSystem {
        @Override
        public String getEntropyInjectionKey() {
            return FsStateBackendEntropyTest.ENTROPY_MARKER;
        }

        @Override
        public String generateEntropy() {
            return FsStateBackendEntropyTest.RESOLVED_MARKER;
        }
    }
}

