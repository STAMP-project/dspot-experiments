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
package org.apache.hadoop.fs.viewfs;


import StorageType.DISK;
import StorageType.SSD;
import java.io.IOException;
import java.net.URISyntaxException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.FsServerDefaults;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.QuotaUsage;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.HdfsClientConfigKeys;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for viewfs implementation of default fs level values.
 * This tests for both passing in a path (based on mount point)
 * to obtain the default value of the fs that the path is mounted on
 * or just passing in no arguments.
 */
public class TestViewFsDefaultValue {
    static final String testFileDir = "/tmp/test/";

    static final String testFileName = (TestViewFsDefaultValue.testFileDir) + "testFileStatusSerialziation";

    static final String NOT_IN_MOUNTPOINT_FILENAME = "/NotInMountpointFile";

    private static MiniDFSCluster cluster;

    private static final FileSystemTestHelper fileSystemTestHelper = new FileSystemTestHelper();

    private static final Configuration CONF = new Configuration();

    private static FileSystem fHdfs;

    private static FileSystem vfs;

    private static Path testFilePath;

    private static Path testFileDirPath;

    // Use NotInMountpoint path to trigger the exception
    private static Path notInMountpointPath;

    /**
     * Test that default blocksize values can be retrieved on the client side.
     */
    @Test
    public void testGetDefaultBlockSize() throws IOException, URISyntaxException {
        // createFile does not use defaultBlockSize to create the file,
        // but we are only looking at the defaultBlockSize, so this
        // test should still pass
        try {
            TestViewFsDefaultValue.vfs.getDefaultBlockSize(TestViewFsDefaultValue.notInMountpointPath);
            Assert.fail("getServerDefaults on viewFs did not throw excetion!");
        } catch (NotInMountpointException e) {
            Assert.assertEquals(TestViewFsDefaultValue.vfs.getDefaultBlockSize(TestViewFsDefaultValue.testFilePath), DFSConfigKeys.DFS_BLOCK_SIZE_DEFAULT);
        }
    }

    /**
     * Test that default replication values can be retrieved on the client side.
     */
    @Test
    public void testGetDefaultReplication() throws IOException, URISyntaxException {
        try {
            TestViewFsDefaultValue.vfs.getDefaultReplication(TestViewFsDefaultValue.notInMountpointPath);
            Assert.fail("getDefaultReplication on viewFs did not throw excetion!");
        } catch (NotInMountpointException e) {
            Assert.assertEquals(TestViewFsDefaultValue.vfs.getDefaultReplication(TestViewFsDefaultValue.testFilePath), ((DFSConfigKeys.DFS_REPLICATION_DEFAULT) + 1));
        }
    }

    /**
     * Test that server default values can be retrieved on the client side.
     */
    @Test
    public void testServerDefaults() throws IOException {
        try {
            TestViewFsDefaultValue.vfs.getServerDefaults(TestViewFsDefaultValue.notInMountpointPath);
            Assert.fail("getServerDefaults on viewFs did not throw excetion!");
        } catch (NotInMountpointException e) {
            FsServerDefaults serverDefaults = TestViewFsDefaultValue.vfs.getServerDefaults(TestViewFsDefaultValue.testFilePath);
            Assert.assertEquals(DFSConfigKeys.DFS_BLOCK_SIZE_DEFAULT, serverDefaults.getBlockSize());
            Assert.assertEquals(HdfsClientConfigKeys.DFS_BYTES_PER_CHECKSUM_DEFAULT, serverDefaults.getBytesPerChecksum());
            Assert.assertEquals(HdfsClientConfigKeys.DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT, serverDefaults.getWritePacketSize());
            Assert.assertEquals(CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT, serverDefaults.getFileBufferSize());
            Assert.assertEquals(((DFSConfigKeys.DFS_REPLICATION_DEFAULT) + 1), serverDefaults.getReplication());
        }
    }

    /**
     * Test that getContentSummary can be retrieved on the client side.
     */
    @Test
    public void testGetContentSummary() throws IOException {
        FileSystem hFs = TestViewFsDefaultValue.cluster.getFileSystem(0);
        final DistributedFileSystem dfs = ((DistributedFileSystem) (hFs));
        dfs.setQuota(TestViewFsDefaultValue.testFileDirPath, 100, 500);
        ContentSummary cs = TestViewFsDefaultValue.vfs.getContentSummary(TestViewFsDefaultValue.testFileDirPath);
        Assert.assertEquals(100, cs.getQuota());
        Assert.assertEquals(500, cs.getSpaceQuota());
    }

    /**
     * Test that getQuotaUsage can be retrieved on the client side.
     */
    @Test
    public void testGetQuotaUsage() throws IOException {
        FileSystem hFs = TestViewFsDefaultValue.cluster.getFileSystem(0);
        final DistributedFileSystem dfs = ((DistributedFileSystem) (hFs));
        dfs.setQuota(TestViewFsDefaultValue.testFileDirPath, 100, 500);
        QuotaUsage qu = TestViewFsDefaultValue.vfs.getQuotaUsage(TestViewFsDefaultValue.testFileDirPath);
        Assert.assertEquals(100, qu.getQuota());
        Assert.assertEquals(500, qu.getSpaceQuota());
    }

    /**
     * Test that getQuotaUsage can be retrieved on the client side if
     * storage types are defined.
     */
    @Test
    public void testGetQuotaUsageWithStorageTypes() throws IOException {
        FileSystem hFs = TestViewFsDefaultValue.cluster.getFileSystem(0);
        final DistributedFileSystem dfs = ((DistributedFileSystem) (hFs));
        dfs.setQuotaByStorageType(TestViewFsDefaultValue.testFileDirPath, SSD, 500);
        dfs.setQuotaByStorageType(TestViewFsDefaultValue.testFileDirPath, DISK, 600);
        QuotaUsage qu = TestViewFsDefaultValue.vfs.getQuotaUsage(TestViewFsDefaultValue.testFileDirPath);
        Assert.assertEquals(500, qu.getTypeQuota(SSD));
        Assert.assertEquals(600, qu.getTypeQuota(DISK));
    }

    /**
     * Test that getQuotaUsage can be retrieved on the client side if
     * quota isn't defined.
     */
    @Test
    public void testGetQuotaUsageWithQuotaDefined() throws IOException {
        FileSystem hFs = TestViewFsDefaultValue.cluster.getFileSystem(0);
        final DistributedFileSystem dfs = ((DistributedFileSystem) (hFs));
        dfs.setQuota(TestViewFsDefaultValue.testFileDirPath, (-1), (-1));
        dfs.setQuotaByStorageType(TestViewFsDefaultValue.testFileDirPath, SSD, (-1));
        dfs.setQuotaByStorageType(TestViewFsDefaultValue.testFileDirPath, DISK, (-1));
        QuotaUsage qu = TestViewFsDefaultValue.vfs.getQuotaUsage(TestViewFsDefaultValue.testFileDirPath);
        Assert.assertEquals((-1), qu.getTypeQuota(SSD));
        Assert.assertEquals((-1), qu.getQuota());
        Assert.assertEquals((-1), qu.getSpaceQuota());
        Assert.assertEquals(2, qu.getFileAndDirectoryCount());
        Assert.assertEquals(0, qu.getTypeConsumed(SSD));
        Assert.assertTrue(((qu.getSpaceConsumed()) > 0));
    }
}

