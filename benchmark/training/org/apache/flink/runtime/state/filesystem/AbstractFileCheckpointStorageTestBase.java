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


import WriteMode.NO_OVERWRITE;
import java.io.IOException;
import java.util.Random;
import org.apache.flink.core.fs.FSDataOutputStream;
import org.apache.flink.core.fs.FileStatus;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.CheckpointMetadataOutputStream;
import org.apache.flink.runtime.state.CheckpointStorage;
import org.apache.flink.runtime.state.CheckpointStorageLocation;
import org.apache.flink.runtime.state.CompletedCheckpointStorageLocation;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static AbstractFsCheckpointStorage.METADATA_FILE_NAME;


/**
 * Test base for file-system-based checkoint storage, such as the
 * {@link org.apache.flink.runtime.state.memory.MemoryBackendCheckpointStorage} and the
 * {@link FsCheckpointStorage}.
 */
public abstract class AbstractFileCheckpointStorageTestBase {
    @Rule
    public final TemporaryFolder tmp = new TemporaryFolder();

    // ------------------------------------------------------------------------
    // pointers
    // ------------------------------------------------------------------------
    @Test
    public void testPointerPathResolution() throws Exception {
        final FileSystem fs = FileSystem.getLocalFileSystem();
        final Path metadataFile = new Path(Path.fromLocalFile(tmp.newFolder()), METADATA_FILE_NAME);
        final String basePointer = metadataFile.getParent().toString();
        final String pointer1 = metadataFile.toString();
        final String pointer2 = metadataFile.getParent().toString();
        final String pointer3 = (metadataFile.getParent().toString()) + '/';
        // create the storage for some random checkpoint directory
        final CheckpointStorage storage = createCheckpointStorage(randomTempPath());
        final byte[] data = new byte[23686];
        new Random().nextBytes(data);
        try (FSDataOutputStream out = fs.create(metadataFile, NO_OVERWRITE)) {
            out.write(data);
        }
        CompletedCheckpointStorageLocation completed1 = storage.resolveCheckpoint(pointer1);
        CompletedCheckpointStorageLocation completed2 = storage.resolveCheckpoint(pointer2);
        CompletedCheckpointStorageLocation completed3 = storage.resolveCheckpoint(pointer3);
        Assert.assertEquals(basePointer, completed1.getExternalPointer());
        Assert.assertEquals(basePointer, completed2.getExternalPointer());
        Assert.assertEquals(basePointer, completed3.getExternalPointer());
        StreamStateHandle handle1 = completed1.getMetadataHandle();
        StreamStateHandle handle2 = completed2.getMetadataHandle();
        StreamStateHandle handle3 = completed3.getMetadataHandle();
        Assert.assertNotNull(handle1);
        Assert.assertNotNull(handle2);
        Assert.assertNotNull(handle3);
        AbstractFileCheckpointStorageTestBase.validateContents(handle1, data);
        AbstractFileCheckpointStorageTestBase.validateContents(handle2, data);
        AbstractFileCheckpointStorageTestBase.validateContents(handle3, data);
    }

    @Test
    public void testFailingPointerPathResolution() throws Exception {
        // create the storage for some random checkpoint directory
        final CheckpointStorage storage = createCheckpointStorage(randomTempPath());
        // null value
        try {
            storage.resolveCheckpoint(null);
            Assert.fail("expected exception");
        } catch (NullPointerException ignored) {
        }
        // empty string
        try {
            storage.resolveCheckpoint("");
            Assert.fail("expected exception");
        } catch (IllegalArgumentException ignored) {
        }
        // not a file path at all
        try {
            storage.resolveCheckpoint("this-is_not/a#filepath.at.all");
            Assert.fail("expected exception");
        } catch (IOException ignored) {
        }
        // non-existing file
        try {
            storage.resolveCheckpoint(((tmp.newFile().toURI().toString()) + "_not_existing"));
            Assert.fail("expected exception");
        } catch (IOException ignored) {
        }
    }

    // ------------------------------------------------------------------------
    // checkpoints
    // ------------------------------------------------------------------------
    /**
     * Validates that multiple checkpoints from different jobs with the same checkpoint ID do not
     * interfere with each other.
     */
    @Test
    public void testPersistMultipleMetadataOnlyCheckpoints() throws Exception {
        final FileSystem fs = FileSystem.getLocalFileSystem();
        final Path checkpointDir = new Path(tmp.newFolder().toURI());
        final long checkpointId = 177;
        final CheckpointStorage storage1 = createCheckpointStorage(checkpointDir);
        final CheckpointStorage storage2 = createCheckpointStorage(checkpointDir);
        final CheckpointStorageLocation loc1 = storage1.initializeLocationForCheckpoint(checkpointId);
        final CheckpointStorageLocation loc2 = storage2.initializeLocationForCheckpoint(checkpointId);
        final byte[] data1 = new byte[]{ 77, 66, 55, 99, 88 };
        final byte[] data2 = new byte[]{ 1, 3, 2, 5, 4 };
        final CompletedCheckpointStorageLocation completedLocation1;
        try (CheckpointMetadataOutputStream out = loc1.createMetadataOutputStream()) {
            out.write(data1);
            completedLocation1 = out.closeAndFinalizeCheckpoint();
        }
        final String result1 = completedLocation1.getExternalPointer();
        final CompletedCheckpointStorageLocation completedLocation2;
        try (CheckpointMetadataOutputStream out = loc2.createMetadataOutputStream()) {
            out.write(data2);
            completedLocation2 = out.closeAndFinalizeCheckpoint();
        }
        final String result2 = completedLocation2.getExternalPointer();
        // check that this went to a file, but in a nested directory structure
        // one directory per storage
        FileStatus[] files = fs.listStatus(checkpointDir);
        Assert.assertEquals(2, files.length);
        // in each per-storage directory, one for the checkpoint
        FileStatus[] job1Files = fs.listStatus(files[0].getPath());
        FileStatus[] job2Files = fs.listStatus(files[1].getPath());
        Assert.assertTrue(((job1Files.length) >= 1));
        Assert.assertTrue(((job2Files.length) >= 1));
        Assert.assertTrue(fs.exists(new Path(result1, METADATA_FILE_NAME)));
        Assert.assertTrue(fs.exists(new Path(result2, METADATA_FILE_NAME)));
        // check that both storages can resolve each others contents
        AbstractFileCheckpointStorageTestBase.validateContents(storage1.resolveCheckpoint(result1).getMetadataHandle(), data1);
        AbstractFileCheckpointStorageTestBase.validateContents(storage1.resolveCheckpoint(result2).getMetadataHandle(), data2);
        AbstractFileCheckpointStorageTestBase.validateContents(storage2.resolveCheckpoint(result1).getMetadataHandle(), data1);
        AbstractFileCheckpointStorageTestBase.validateContents(storage2.resolveCheckpoint(result2).getMetadataHandle(), data2);
    }

    @Test
    public void writeToAlreadyExistingCheckpointFails() throws Exception {
        final byte[] data = new byte[]{ 8, 8, 4, 5, 2, 6, 3 };
        final long checkpointId = 177;
        final CheckpointStorage storage = createCheckpointStorage(randomTempPath());
        final CheckpointStorageLocation loc = storage.initializeLocationForCheckpoint(checkpointId);
        // write to the metadata file for the checkpoint
        try (CheckpointMetadataOutputStream out = loc.createMetadataOutputStream()) {
            out.write(data);
            out.closeAndFinalizeCheckpoint();
        }
        // create another writer to the metadata file for the checkpoint
        try {
            loc.createMetadataOutputStream();
            Assert.fail("this should fail with an exception");
        } catch (IOException ignored) {
        }
    }

    // ------------------------------------------------------------------------
    // savepoints
    // ------------------------------------------------------------------------
    @Test
    public void testSavepointPathConfiguredAndTarget() throws Exception {
        final Path savepointDir = randomTempPath();
        final Path customDir = randomTempPath();
        testSavepoint(savepointDir, customDir, customDir);
    }

    @Test
    public void testSavepointPathConfiguredNoTarget() throws Exception {
        final Path savepointDir = randomTempPath();
        testSavepoint(savepointDir, null, savepointDir);
    }

    @Test
    public void testNoSavepointPathConfiguredAndTarget() throws Exception {
        final Path customDir = Path.fromLocalFile(tmp.newFolder());
        testSavepoint(null, customDir, customDir);
    }

    @Test
    public void testNoSavepointPathConfiguredNoTarget() throws Exception {
        final CheckpointStorage storage = createCheckpointStorage(randomTempPath());
        try {
            storage.initializeLocationForSavepoint(1337, null);
            Assert.fail("this should fail with an exception");
        } catch (IllegalArgumentException ignored) {
        }
    }
}

