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
package org.apache.flink.contrib.streaming.state;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.flink.core.fs.CloseableRegistry;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.CheckpointStreamFactory;
import org.apache.flink.runtime.state.CheckpointedStateScope;
import org.apache.flink.runtime.state.StateHandleID;
import org.apache.flink.runtime.state.StreamStateHandle;
import org.apache.flink.runtime.state.filesystem.FsCheckpointStreamFactory;
import org.apache.flink.util.TestLogger;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;


/**
 * Test class for {@link RocksDBStateUploader}.
 */
public class RocksDBStateUploaderTest extends TestLogger {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    /**
     * Test that the exception arose in the thread pool will rethrow to the main thread.
     */
    @Test
    public void testMultiThreadUploadThreadPoolExceptionRethrow() throws IOException {
        RocksDBStateUploaderTest.SpecifiedException expectedException = new RocksDBStateUploaderTest.SpecifiedException("throw exception while multi thread upload states.");
        CheckpointStreamFactory.CheckpointStateOutputStream outputStream = createFailingCheckpointStateOutputStream(expectedException);
        CheckpointStreamFactory checkpointStreamFactory = (CheckpointedStateScope scope) -> outputStream;
        File file = temporaryFolder.newFile(String.valueOf(UUID.randomUUID()));
        generateRandomFileContent(file.getPath(), 20);
        Map<StateHandleID, Path> filePaths = new HashMap<>(1);
        filePaths.put(new StateHandleID("mockHandleID"), new Path(file.getPath()));
        try (RocksDBStateUploader rocksDBStateUploader = new RocksDBStateUploader(5)) {
            rocksDBStateUploader.uploadFilesToCheckpointFs(filePaths, checkpointStreamFactory, new CloseableRegistry());
            Assert.fail();
        } catch (Exception e) {
            Assert.assertEquals(expectedException, e);
        }
    }

    /**
     * Test that upload files with multi-thread correctly.
     */
    @Test
    public void testMultiThreadUploadCorrectly() throws Exception {
        File checkpointPrivateFolder = temporaryFolder.newFolder("private");
        Path checkpointPrivateDirectory = new Path(checkpointPrivateFolder.getPath());
        File checkpointSharedFolder = temporaryFolder.newFolder("shared");
        Path checkpointSharedDirectory = new Path(checkpointSharedFolder.getPath());
        FileSystem fileSystem = checkpointPrivateDirectory.getFileSystem();
        int fileStateSizeThreshold = 1024;
        FsCheckpointStreamFactory checkpointStreamFactory = new FsCheckpointStreamFactory(fileSystem, checkpointPrivateDirectory, checkpointSharedDirectory, fileStateSizeThreshold);
        String localFolder = "local";
        temporaryFolder.newFolder(localFolder);
        int sstFileCount = 6;
        Map<StateHandleID, Path> sstFilePaths = generateRandomSstFiles(localFolder, sstFileCount, fileStateSizeThreshold);
        try (RocksDBStateUploader rocksDBStateUploader = new RocksDBStateUploader(5)) {
            Map<StateHandleID, StreamStateHandle> sstFiles = rocksDBStateUploader.uploadFilesToCheckpointFs(sstFilePaths, checkpointStreamFactory, new CloseableRegistry());
            for (Map.Entry<StateHandleID, Path> entry : sstFilePaths.entrySet()) {
                assertStateContentEqual(entry.getValue(), sstFiles.get(entry.getKey()).openInputStream());
            }
        }
    }

    private static class SpecifiedException extends IOException {
        SpecifiedException(String message) {
            super(message);
        }
    }
}

