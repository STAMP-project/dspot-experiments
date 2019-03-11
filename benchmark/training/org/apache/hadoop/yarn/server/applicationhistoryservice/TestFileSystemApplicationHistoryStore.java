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
package org.apache.hadoop.yarn.server.applicationhistoryservice;


import java.io.IOException;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RawLocalFileSystem;
import org.apache.hadoop.yarn.api.records.ApplicationAttemptId;
import org.apache.hadoop.yarn.api.records.ApplicationId;
import org.apache.hadoop.yarn.api.records.ContainerId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestFileSystemApplicationHistoryStore extends ApplicationHistoryStoreTestUtils {
    private static final Logger LOG = LoggerFactory.getLogger(TestFileSystemApplicationHistoryStore.class.getName());

    private FileSystem fs;

    private Path fsWorkingPath;

    @Test
    public void testReadWriteHistoryData() throws IOException {
        TestFileSystemApplicationHistoryStore.LOG.info("Starting testReadWriteHistoryData");
        testWriteHistoryData(5);
        testReadHistoryData(5);
    }

    @Test
    public void testWriteAfterApplicationFinish() throws IOException {
        TestFileSystemApplicationHistoryStore.LOG.info("Starting testWriteAfterApplicationFinish");
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        writeApplicationStartData(appId);
        writeApplicationFinishData(appId);
        // write application attempt history data
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        try {
            writeApplicationAttemptStartData(appAttemptId);
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("is not opened"));
        }
        try {
            writeApplicationAttemptFinishData(appAttemptId);
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("is not opened"));
        }
        // write container history data
        ContainerId containerId = ContainerId.newContainerId(appAttemptId, 1);
        try {
            writeContainerStartData(containerId);
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("is not opened"));
        }
        try {
            writeContainerFinishData(containerId);
            Assert.fail();
        } catch (IOException e) {
            Assert.assertTrue(e.getMessage().contains("is not opened"));
        }
    }

    @Test
    public void testMassiveWriteContainerHistoryData() throws IOException {
        TestFileSystemApplicationHistoryStore.LOG.info("Starting testMassiveWriteContainerHistoryData");
        long mb = 1024 * 1024;
        long usedDiskBefore = (fs.getContentSummary(fsWorkingPath).getLength()) / mb;
        ApplicationId appId = ApplicationId.newInstance(0, 1);
        writeApplicationStartData(appId);
        ApplicationAttemptId appAttemptId = ApplicationAttemptId.newInstance(appId, 1);
        for (int i = 1; i <= 100000; ++i) {
            ContainerId containerId = ContainerId.newContainerId(appAttemptId, i);
            writeContainerStartData(containerId);
            writeContainerFinishData(containerId);
        }
        writeApplicationFinishData(appId);
        long usedDiskAfter = (fs.getContentSummary(fsWorkingPath).getLength()) / mb;
        Assert.assertTrue(((usedDiskAfter - usedDiskBefore) < 20));
    }

    @Test
    public void testMissingContainerHistoryData() throws IOException {
        TestFileSystemApplicationHistoryStore.LOG.info("Starting testMissingContainerHistoryData");
        testWriteHistoryData(3, true, false);
        testReadHistoryData(3, true, false);
    }

    @Test
    public void testMissingApplicationAttemptHistoryData() throws IOException {
        TestFileSystemApplicationHistoryStore.LOG.info("Starting testMissingApplicationAttemptHistoryData");
        testWriteHistoryData(3, false, true);
        testReadHistoryData(3, false, true);
    }

    @Test
    public void testInitExistingWorkingDirectoryInSafeMode() throws Exception {
        TestFileSystemApplicationHistoryStore.LOG.info("Starting testInitExistingWorkingDirectoryInSafeMode");
        tearDown();
        // Setup file system to inject startup conditions
        FileSystem fileSystem = Mockito.spy(new RawLocalFileSystem());
        FileStatus fileStatus = Mockito.mock(FileStatus.class);
        Mockito.doReturn(true).when(fileStatus).isDirectory();
        Mockito.doReturn(fileStatus).when(fileSystem).getFileStatus(ArgumentMatchers.any(Path.class));
        try {
            initAndStartStore(fileSystem);
        } catch (Exception e) {
            Assert.fail(("Exception should not be thrown: " + e));
        }
        // Make sure that directory creation was not attempted
        Mockito.verify(fileStatus, Mockito.never()).isDirectory();
        Mockito.verify(fileSystem, Mockito.times(1)).mkdirs(ArgumentMatchers.any(Path.class));
    }

    @Test
    public void testInitNonExistingWorkingDirectoryInSafeMode() throws Exception {
        TestFileSystemApplicationHistoryStore.LOG.info("Starting testInitNonExistingWorkingDirectoryInSafeMode");
        tearDown();
        // Setup file system to inject startup conditions
        FileSystem fileSystem = Mockito.spy(new RawLocalFileSystem());
        FileStatus fileStatus = Mockito.mock(FileStatus.class);
        Mockito.doReturn(false).when(fileStatus).isDirectory();
        Mockito.doReturn(fileStatus).when(fileSystem).getFileStatus(ArgumentMatchers.any(Path.class));
        Mockito.doThrow(new IOException()).when(fileSystem).mkdirs(ArgumentMatchers.any(Path.class));
        try {
            initAndStartStore(fileSystem);
            Assert.fail("Exception should have been thrown");
        } catch (Exception e) {
            // Expected failure
        }
        // Make sure that directory creation was attempted
        Mockito.verify(fileStatus, Mockito.never()).isDirectory();
        Mockito.verify(fileSystem, Mockito.times(1)).mkdirs(ArgumentMatchers.any(Path.class));
    }
}

