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
package org.apache.hadoop.hdfs.server.namenode;


import HdfsConstants.BYTES_IN_INTEGER;
import HdfsConstants.DatanodeReportType.ALL;
import HdfsConstants.QUOTA_DONT_SET;
import HdfsConstants.QUOTA_RESET;
import HdfsConstants.SafeModeAction.SAFEMODE_ENTER;
import HdfsConstants.SafeModeAction.SAFEMODE_FORCE_EXIT;
import HdfsConstants.SafeModeAction.SAFEMODE_GET;
import HdfsConstants.SafeModeAction.SAFEMODE_LEAVE;
import RPC.RpcKind;
import Server.Call;
import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo;
import org.apache.hadoop.hdfs.protocol.CachePoolInfo;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorageReport;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.test.GenericTestUtils.LogCapturer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestAuditLoggerWithCommands {
    static final int NUM_DATA_NODES = 2;

    static final long seed = 3735928559L;

    static final int blockSize = 8192;

    private static MiniDFSCluster cluster = null;

    private static FileSystem fileSys = null;

    private static FileSystem fs2 = null;

    private static FileSystem fs = null;

    private static LogCapturer auditlog;

    static Configuration conf;

    static UserGroupInformation user1;

    static UserGroupInformation user2;

    private static NamenodeProtocols proto;

    @Test
    public void testGetContentSummary() throws IOException {
        Path dir1 = new Path("/dir1");
        Path dir2 = new Path("/dir2");
        String acePattern = ".*allowed=false.*ugi=theEngineer.*cmd=contentSummary.*";
        TestAuditLoggerWithCommands.fs.mkdirs(dir1, new FsPermission(((short) (384))));
        TestAuditLoggerWithCommands.fs.mkdirs(dir2, new FsPermission(((short) (384))));
        TestAuditLoggerWithCommands.fs.setOwner(dir1, TestAuditLoggerWithCommands.user1.getUserName(), TestAuditLoggerWithCommands.user1.getPrimaryGroupName());
        TestAuditLoggerWithCommands.fs.setOwner(dir2, TestAuditLoggerWithCommands.user2.getUserName(), TestAuditLoggerWithCommands.user2.getPrimaryGroupName());
        try {
            TestAuditLoggerWithCommands.fs2.getContentSummary(new Path("/"));
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        int length = verifyAuditLogs(acePattern);
        try {
            TestAuditLoggerWithCommands.fs2.getContentSummary(new Path("/dir3"));
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log from getContentSummary", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testSetQuota() throws Exception {
        Path path = new Path("/testdir/testdir1");
        TestAuditLoggerWithCommands.fs.mkdirs(path);
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        verifySetQuota(path, QUOTA_RESET, QUOTA_DONT_SET);
        verifySetQuota(path, QUOTA_DONT_SET, QUOTA_RESET);
        verifySetQuota(path, QUOTA_DONT_SET, BYTES_IN_INTEGER);
        verifySetQuota(path, BYTES_IN_INTEGER, BYTES_IN_INTEGER);
        TestAuditLoggerWithCommands.fileSys.close();
    }

    @Test
    public void testConcat() throws Exception {
        Path file1 = new Path("/file1");
        Path file2 = new Path("/file2");
        Path targetDir = new Path("/target");
        String acePattern = ".*allowed=false.*ugi=theDoctor.*cmd=concat.*";
        TestAuditLoggerWithCommands.fs.createNewFile(file1);
        TestAuditLoggerWithCommands.fs.createNewFile(file2);
        TestAuditLoggerWithCommands.fs.mkdirs(targetDir);
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        try {
            TestAuditLoggerWithCommands.fileSys.concat(targetDir, new Path[]{ file1, file2 });
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        int length = verifyAuditLogs(acePattern);
        TestAuditLoggerWithCommands.fileSys.close();
        try {
            TestAuditLoggerWithCommands.fileSys.concat(targetDir, new Path[]{ file1, file2 });
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log from Concat", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testCreateRenameSnapShot() throws Exception {
        Path srcDir = new Path("/src");
        String aceCreatePattern = ".*allowed=false.*ugi=theDoctor.*cmd=createSnapshot.*";
        String aceRenamePattern = ".*allowed=false.*ugi=theDoctor.*cmd=renameSnapshot.*";
        TestAuditLoggerWithCommands.fs.mkdirs(srcDir);
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        ((DistributedFileSystem) (TestAuditLoggerWithCommands.fs)).allowSnapshot(srcDir);
        try {
            TestAuditLoggerWithCommands.fileSys.createSnapshot(srcDir);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        verifyAuditLogs(aceCreatePattern);
        try {
            Path s1 = TestAuditLoggerWithCommands.fs.createSnapshot(srcDir);
            TestAuditLoggerWithCommands.fileSys.renameSnapshot(srcDir, s1.getName(), "test");
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        int length = TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length;
        verifyAuditLogs(aceRenamePattern);
        try {
            TestAuditLoggerWithCommands.fs.createSnapshot(new Path("/test1"));
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        try {
            TestAuditLoggerWithCommands.fs.renameSnapshot(new Path("/test1"), "abc", "test2");
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testDeleteSnapshot() throws Exception {
        Path srcDir = new Path("/src");
        Path s1;
        TestAuditLoggerWithCommands.fs.mkdirs(srcDir);
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        ((DistributedFileSystem) (TestAuditLoggerWithCommands.fs)).allowSnapshot(srcDir);
        try {
            s1 = TestAuditLoggerWithCommands.fs.createSnapshot(srcDir);
            TestAuditLoggerWithCommands.fileSys.deleteSnapshot(srcDir, s1.getName());
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        String aceDeletePattern = ".*allowed=false.*ugi=theDoctor.*cmd=deleteSnapshot.*";
        int length = verifyAuditLogs(aceDeletePattern);
        TestAuditLoggerWithCommands.fileSys.close();
        try {
            s1 = TestAuditLoggerWithCommands.fs.createSnapshot(srcDir);
            TestAuditLoggerWithCommands.fileSys.deleteSnapshot(srcDir, s1.getName());
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", ((length + 1) == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testAllowSnapshot() throws Exception {
        Path srcDir = new Path(System.getProperty("user.dir"), "/src");
        TestAuditLoggerWithCommands.fs.mkdirs(srcDir);
        String pattern = (".*allowed=true.*ugi=" + (System.getProperty("user.name"))) + ".*cmd=allowSnapshot.*";
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fs)).allowSnapshot(srcDir);
            verifyAuditLogs(pattern);
        } catch (Exception e) {
            Assert.fail("The operation should not have failed with Exception");
        }
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).allowSnapshot(srcDir);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        pattern = ".*allowed=false.*ugi=theDoctor.*cmd=allowSnapshot.*";
        verifyAuditLogs(pattern);
        TestAuditLoggerWithCommands.fs.delete(srcDir, true);
        TestAuditLoggerWithCommands.fileSys.close();
    }

    @Test
    public void testDisallowSnapshot() throws Exception {
        Path srcDir = new Path(System.getProperty("user.dir"), "/src");
        TestAuditLoggerWithCommands.fs.mkdirs(srcDir);
        TestAuditLoggerWithCommands.cluster.getNamesystem().allowSnapshot(srcDir.toString());
        String pattern = (".*allowed=true.*ugi=" + (System.getProperty("user.name"))) + ".*cmd=disallowSnapshot.*";
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fs)).disallowSnapshot(srcDir);
            verifyAuditLogs(pattern);
        } catch (Exception e) {
            Assert.fail("The operation should not have failed with Exception");
        }
        TestAuditLoggerWithCommands.cluster.getNamesystem().allowSnapshot(srcDir.toString());
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).disallowSnapshot(srcDir);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
            pattern = ".*allowed=false.*ugi=theDoctor.*cmd=disallowSnapshot.*";
            verifyAuditLogs(pattern);
        }
        TestAuditLoggerWithCommands.fileSys.close();
    }

    @Test
    public void testAddCacheDirective() throws Exception {
        removeExistingCachePools(null);
        TestAuditLoggerWithCommands.proto.addCachePool(new CachePoolInfo("pool1").setMode(new FsPermission(((short) (0)))));
        CacheDirectiveInfo alpha = new CacheDirectiveInfo.Builder().setPath(new Path(System.getProperty("user.dir"), "/alpha")).setPool("pool1").build();
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).addCacheDirective(alpha);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        String aceAddCachePattern = ".*allowed=false.*ugi=theDoctor.*cmd=addCache.*";
        int length = verifyAuditLogs(aceAddCachePattern);
        try {
            TestAuditLoggerWithCommands.fileSys.close();
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).addCacheDirective(alpha);
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testModifyCacheDirective() throws Exception {
        removeExistingCachePools(null);
        TestAuditLoggerWithCommands.proto.addCachePool(new CachePoolInfo("pool1").setMode(new FsPermission(((short) (0)))));
        CacheDirectiveInfo alpha = new CacheDirectiveInfo.Builder().setPath(new Path("/alpha")).setPool("pool1").build();
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        Long id = ((DistributedFileSystem) (TestAuditLoggerWithCommands.fs)).addCacheDirective(alpha);
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(id).setReplication(((short) (1))).build());
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        String aceModifyCachePattern = ".*allowed=false.*ugi=theDoctor.*cmd=modifyCache.*";
        verifyAuditLogs(aceModifyCachePattern);
        TestAuditLoggerWithCommands.fileSys.close();
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(id).setReplication(((short) (1))).build());
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
    }

    @Test
    public void testRemoveCacheDirective() throws Exception {
        removeExistingCachePools(null);
        TestAuditLoggerWithCommands.proto.addCachePool(new CachePoolInfo("pool1").setMode(new FsPermission(((short) (0)))));
        CacheDirectiveInfo alpha = new CacheDirectiveInfo.Builder().setPath(new Path("/alpha")).setPool("pool1").build();
        String aceRemoveCachePattern = ".*allowed=false.*ugi=theDoctor.*cmd=removeCache.*";
        int length = -1;
        Long id = ((DistributedFileSystem) (TestAuditLoggerWithCommands.fs)).addCacheDirective(alpha);
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        try {
            removeCacheDirective(id);
            Assert.fail("It should have failed with an AccessControlException");
        } catch (AccessControlException ace) {
            length = verifyAuditLogs(aceRemoveCachePattern);
        }
        try {
            TestAuditLoggerWithCommands.fileSys.close();
            removeCacheDirective(id);
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testGetSnapshotDiffReport() throws Exception {
        Path snapshotDirPath = new Path("/test");
        TestAuditLoggerWithCommands.fs.mkdirs(snapshotDirPath, new FsPermission(((short) (0))));
        TestAuditLoggerWithCommands.cluster.getNamesystem().allowSnapshot(snapshotDirPath.toString());
        Path s1 = TestAuditLoggerWithCommands.fs.createSnapshot(snapshotDirPath);
        Path s2 = TestAuditLoggerWithCommands.fs.createSnapshot(snapshotDirPath);
        int length;
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).getSnapshotDiffReport(snapshotDirPath, s1.getName(), s2.getName());
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        String aceSnapshotDiffPattern = ".*allowed=false.*ugi=theDoctor.*cmd=computeSnapshotDiff.*";
        length = verifyAuditLogs(aceSnapshotDiffPattern);
        try {
            TestAuditLoggerWithCommands.fileSys.close();
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).getSnapshotDiffReport(snapshotDirPath, s1.getName(), s2.getName());
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testGetQuotaUsage() throws Exception {
        Path path = new Path("/test");
        TestAuditLoggerWithCommands.fs.mkdirs(path, new FsPermission(((short) (0))));
        String aceGetQuotaUsagePattern = ".*allowed=false.*ugi=theDoctor.*cmd=quotaUsage.*";
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        try {
            TestAuditLoggerWithCommands.fileSys.getQuotaUsage(path);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        int length = verifyAuditLogs(aceGetQuotaUsagePattern);
        TestAuditLoggerWithCommands.fileSys.close();
        try {
            TestAuditLoggerWithCommands.fileSys.getQuotaUsage(path);
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testAddCachePool() throws Exception {
        removeExistingCachePools(null);
        CachePoolInfo cacheInfo = new CachePoolInfo("pool1").setMode(new FsPermission(((short) (0))));
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).addCachePool(cacheInfo);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        String aceAddCachePoolPattern = ".*allowed=false.*ugi=theDoctor.*cmd=addCachePool.*";
        int length = verifyAuditLogs(aceAddCachePoolPattern);
        try {
            TestAuditLoggerWithCommands.fileSys.close();
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).addCachePool(cacheInfo);
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testModifyCachePool() throws Exception {
        removeExistingCachePools(null);
        CachePoolInfo cacheInfo = new CachePoolInfo("pool1").setMode(new FsPermission(((short) (0))));
        ((DistributedFileSystem) (TestAuditLoggerWithCommands.fs)).addCachePool(cacheInfo);
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).modifyCachePool(cacheInfo);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        String aceModifyCachePoolPattern = ".*allowed=false.*ugi=theDoctor.*cmd=modifyCachePool.*";
        int length = verifyAuditLogs(aceModifyCachePoolPattern);
        try {
            TestAuditLoggerWithCommands.fileSys.close();
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).modifyCachePool(cacheInfo);
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testRemoveCachePool() throws Exception {
        removeExistingCachePools(null);
        CachePoolInfo cacheInfo = new CachePoolInfo("pool1").setMode(new FsPermission(((short) (0))));
        ((DistributedFileSystem) (TestAuditLoggerWithCommands.fs)).addCachePool(cacheInfo);
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        try {
            removeCachePool("pool1");
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        String aceRemoveCachePoolPattern = ".*allowed=false.*ugi=theDoctor.*cmd=removeCachePool.*";
        int length = verifyAuditLogs(aceRemoveCachePoolPattern);
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
        try {
            TestAuditLoggerWithCommands.fileSys.close();
            removeCachePool("pool1");
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testGetEZForPath() throws Exception {
        Path path = new Path("/test");
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        TestAuditLoggerWithCommands.fs.mkdirs(path, new FsPermission(((short) (0))));
        String aceGetEzForPathPattern = ".*allowed=false.*ugi=theDoctor.*cmd=getEZForPath.*";
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).getEZForPath(path);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        int length = verifyAuditLogs(aceGetEzForPathPattern);
        TestAuditLoggerWithCommands.fileSys.close();
        try {
            ((DistributedFileSystem) (TestAuditLoggerWithCommands.fileSys)).getEZForPath(path);
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testRenameTo() throws Exception {
        Path path = new Path("/test");
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        TestAuditLoggerWithCommands.fs.mkdirs(path, new FsPermission(((short) (0))));
        String aceRenameToPattern = ".*allowed=false.*ugi=theDoctor.*cmd=rename.*";
        try {
            TestAuditLoggerWithCommands.fileSys.rename(path, path);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        int length = verifyAuditLogs(aceRenameToPattern);
        TestAuditLoggerWithCommands.fileSys.close();
        try {
            TestAuditLoggerWithCommands.fileSys.rename(path, path);
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testGetXattrs() throws Exception {
        Path path = new Path("/test");
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        TestAuditLoggerWithCommands.fs.mkdirs(path, new FsPermission(((short) (0))));
        String aceGetXattrsPattern = ".*allowed=false.*ugi=theDoctor.*cmd=getXAttrs.*";
        try {
            TestAuditLoggerWithCommands.fileSys.getXAttrs(path);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        int length = verifyAuditLogs(aceGetXattrsPattern);
        TestAuditLoggerWithCommands.fileSys.close();
        try {
            TestAuditLoggerWithCommands.fileSys.getXAttrs(path);
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException e) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
    }

    @Test
    public void testListXattrs() throws Exception {
        Path path = new Path("/test");
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        TestAuditLoggerWithCommands.fs.mkdirs(path);
        TestAuditLoggerWithCommands.fs.setOwner(path, TestAuditLoggerWithCommands.user1.getUserName(), TestAuditLoggerWithCommands.user1.getPrimaryGroupName());
        String aceListXattrsPattern = ".*allowed=true.*ugi=theDoctor.*cmd=listXAttrs.*";
        TestAuditLoggerWithCommands.fileSys.listXAttrs(path);
        verifyAuditLogs(aceListXattrsPattern);
    }

    @Test
    public void testGetAclStatus() throws Exception {
        Path path = new Path("/test");
        TestAuditLoggerWithCommands.fs.mkdirs(path);
        TestAuditLoggerWithCommands.fileSys = DFSTestUtil.getFileSystemAs(TestAuditLoggerWithCommands.user1, TestAuditLoggerWithCommands.conf);
        TestAuditLoggerWithCommands.fs.setOwner(path, TestAuditLoggerWithCommands.user1.getUserName(), TestAuditLoggerWithCommands.user1.getPrimaryGroupName());
        final FSDirectory dir = TestAuditLoggerWithCommands.cluster.getNamesystem().getFSDirectory();
        final FSDirectory mockedDir = Mockito.spy(dir);
        AccessControlException ex = new AccessControlException();
        Mockito.doThrow(ex).when(mockedDir).checkTraverse(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        TestAuditLoggerWithCommands.cluster.getNamesystem().setFSDirectory(mockedDir);
        String aceGetAclStatus = ".*allowed=false.*ugi=theDoctor.*cmd=getAclStatus.*";
        try {
            TestAuditLoggerWithCommands.fileSys.getAclStatus(path);
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (AccessControlException ace) {
        }
        int length = verifyAuditLogs(aceGetAclStatus);
        TestAuditLoggerWithCommands.fileSys.close();
        try {
            TestAuditLoggerWithCommands.fileSys.getAclStatus(path);
            verifyAuditLogs(aceGetAclStatus);
            Assert.fail("The operation should have failed with IOException");
        } catch (IOException ace) {
        }
        Assert.assertTrue("Unexpected log!", (length == (TestAuditLoggerWithCommands.auditlog.getOutput().split("\n").length)));
        TestAuditLoggerWithCommands.cluster.getNamesystem().setFSDirectory(dir);
    }

    @Test
    public void testDelegationTokens() throws Exception {
        Token dt = TestAuditLoggerWithCommands.fs.getDelegationToken("foo");
        final String getDT = ".*src=HDFS_DELEGATION_TOKEN token 1.*with renewer foo.*";
        verifyAuditLogs(true, (".*cmd=getDelegationToken" + getDT));
        // renew
        final UserGroupInformation foo = UserGroupInformation.createUserForTesting("foo", new String[]{  });
        foo.doAs(new PrivilegedExceptionAction<Void>() {
            @Override
            public Void run() throws Exception {
                dt.renew(TestAuditLoggerWithCommands.conf);
                return null;
            }
        });
        verifyAuditLogs(true, (".*cmd=renewDelegationToken" + getDT));
        try {
            dt.renew(TestAuditLoggerWithCommands.conf);
            Assert.fail("Renewing a token with non-renewer should fail");
        } catch (AccessControlException expected) {
        }
        verifyAuditLogs(false, (".*cmd=renewDelegationToken" + getDT));
        // cancel
        final UserGroupInformation bar = UserGroupInformation.createUserForTesting("bar", new String[]{  });
        try {
            bar.doAs(new PrivilegedExceptionAction<Void>() {
                @Override
                public Void run() throws Exception {
                    dt.cancel(TestAuditLoggerWithCommands.conf);
                    return null;
                }
            });
            Assert.fail("Canceling a token with non-renewer should fail");
        } catch (AccessControlException expected) {
        }
        verifyAuditLogs(false, (".*cmd=cancelDelegationToken" + getDT));
        dt.cancel(TestAuditLoggerWithCommands.conf);
        verifyAuditLogs(true, (".*cmd=cancelDelegationToken" + getDT));
    }

    @Test
    public void testMetaSave() throws Exception {
        String aceMetaSave = ".*allowed=true.*cmd=metaSave.*";
        try {
            metaSave("test.log");
            verifyAuditLogs(aceMetaSave);
        } catch (Exception e) {
            Assert.fail("The operation should not have failed with Exception");
        }
        try {
            metaSave("test.log");
            Assert.fail("The operation should have failed with AccessControlException");
        } catch (IOException ace) {
            GenericTestUtils.assertExceptionContains("Access denied", ace);
            aceMetaSave = ".*allowed=false.*cmd=metaSave.*";
            verifyAuditLogs(aceMetaSave);
        }
    }

    @Test
    public void testStartReconfiguration() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=startNamenodeReconfiguration.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            startReconfiguration();
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail("StartConfiguration should have passed!");
        }
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            startReconfiguration();
            Assert.fail("startNameNodeReconfiguration should throw AccessControlException!");
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=startNamenodeReconfiguration.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testGetReconfigurationStatus() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=getNamenodeReconfigurationStatus.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            getReconfigurationStatus();
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail(("getNamenodeReconfigurationStatus " + " threw Exception!"));
        }
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            getReconfigurationStatus();
            Assert.fail(("getNamenodeReconfigurationStatus " + " did not throw AccessControlException!"));
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=getNamenodeReconfigurationStatus.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testListReconfigurableProperties() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=listNamenodeReconfigurableProperties.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            listReconfigurableProperties();
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail(("listReconfigurableProperties " + " threw Exception!"));
        }
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            listReconfigurableProperties();
            Assert.fail(("getNamenodeReconfigurationStatus " + " did not throw AccessControlException!"));
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=listNamenodeReconfigurableProperties.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testRefreshUserToGroupsMappings() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=refreshUserToGroupsMappings.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        refreshUserToGroupsMappings();
        verifyAuditLogs(auditLogString);
    }

    @Test
    public void testRefreshSuperUserGroupsConfiguration() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=refreshSuperUserGroupsConfiguration.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            refreshSuperUserGroupsConfiguration();
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail(" The operation threw an exception");
        }
    }

    @Test
    public void testRefreshQueue() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=refreshCallQueue.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            refreshCallQueue();
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail(" The operation threw an exception");
        }
    }

    @Test
    public void testRefreshServiceAcl() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=refreshServiceAcl.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            refreshServiceAcl();
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail((" The operation threw an exception" + e));
        }
    }

    @Test
    public void testFinalizeRollingUpgrade() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=finalizeRollingUpgrade.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        fsNamesystem.setRollingUpgradeInfo(false, System.currentTimeMillis());
        try {
            fsNamesystem.finalizeRollingUpgrade();
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail("finalizeRollingUpgrade threw Exception");
        }
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            fsNamesystem.finalizeRollingUpgrade();
            Assert.fail("finalizeRollingUpgrade should throw AccessControlException!");
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=finalizeRollingUpgrade.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testQueryRollingUpgrade() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=queryRollingUpgrade.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        fsNamesystem.setRollingUpgradeInfo(false, System.currentTimeMillis());
        try {
            fsNamesystem.queryRollingUpgrade();
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail("queryRollingUpgrade threw Exception");
        }
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            fsNamesystem.queryRollingUpgrade();
            Assert.fail("queryRollingUpgrade should have thrown an AccessControlException!");
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=queryRollingUpgrade.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testRollEditLog() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=rollEditLog.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            fsNamesystem.rollEditLog();
        } catch (Exception e) {
            Assert.fail("rollEditLog threw Exception");
        }
        verifyAuditLogs(auditLogString);
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            fsNamesystem.rollEditLog();
            Assert.fail("rollEditLog should have thrown an AccessControlException!");
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=rollEditLog.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testSetSafeMode() throws Exception {
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        verifySuccessfulSetSafeMode(fsNamesystem, SAFEMODE_ENTER);
        verifySuccessfulSetSafeMode(fsNamesystem, SAFEMODE_GET);
        verifySuccessfulSetSafeMode(fsNamesystem, SAFEMODE_LEAVE);
        verifySuccessfulSetSafeMode(fsNamesystem, SAFEMODE_FORCE_EXIT);
        String auditLogString;
        auditLogString = ".*allowed=true.*cmd=safemode_get.*";
        fsNamesystem.setSafeMode(SAFEMODE_GET);
        verifyAuditLogs(auditLogString);
        auditLogString = ".*allowed=true.*cmd=safemode_leave.*";
        fsNamesystem.setSafeMode(SAFEMODE_LEAVE);
        verifyAuditLogs(auditLogString);
        auditLogString = ".*allowed=true.*cmd=safemode_force_exit.*";
        fsNamesystem.setSafeMode(SAFEMODE_FORCE_EXIT);
        verifyAuditLogs(auditLogString);
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        verifySafeModeAction(fsNamesystem, SAFEMODE_ENTER);
        verifySafeModeAction(fsNamesystem, SAFEMODE_LEAVE);
        verifySafeModeAction(fsNamesystem, SAFEMODE_FORCE_EXIT);
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
    }

    @Test
    public void testSetBalancerBandwidth() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=setBalancerBandwidth.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            fsNamesystem.setBalancerBandwidth(10);
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail("setBalancerBandwidth threw exception!");
        }
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            fsNamesystem.setBalancerBandwidth(10);
            Assert.fail("setBalancerBandwidth should have thrown AccessControlException!");
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=setBalancerBandwidth.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testRefreshNodes() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=refreshNodes.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            fsNamesystem.refreshNodes();
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail("refreshNodes threw exception!");
        }
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            fsNamesystem.refreshNodes();
            Assert.fail("refreshNodes should have thrown an AccessControlException!");
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=refreshNodes.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testFinalizeUpgrade() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=finalizeUpgrade.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            fsNamesystem.finalizeUpgrade();
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail("finalizeUpgrade threw Exception");
        }
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            fsNamesystem.finalizeUpgrade();
            Assert.fail("finalizeUpgrade should have thrown an AccessControlException!");
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=finalizeUpgrade.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testSaveNamespace() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=saveNamespace.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        fsNamesystem.setSafeMode(SAFEMODE_ENTER);
        try {
            fsNamesystem.saveNamespace(10, 10);
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail("saveNamespace threw Exception");
        }
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            fsNamesystem.saveNamespace(10, 10);
            Assert.fail("saveNamespace should have thrown an AccessControlException!");
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=saveNamespace.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testDatanodeReport() throws Exception {
        String auditLogString = ".*allowed=true.*cmd=datanodeReport.*";
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        try {
            fsNamesystem.datanodeReport(ALL);
            verifyAuditLogs(auditLogString);
        } catch (Exception e) {
            Assert.fail("datanodeReport threw Exception");
        }
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        try {
            fsNamesystem.datanodeReport(ALL);
            Assert.fail("datanodeReport should have thrown an AccessControlException!");
        } catch (AccessControlException ace) {
            auditLogString = ".*allowed=false.*cmd=datanodeReport.*";
            verifyAuditLogs(auditLogString);
        }
    }

    @Test
    public void testRestoreFailedStorage() throws Exception {
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        verifyAuditRestoreFailedStorage(fsNamesystem, "check");
        verifyAuditRestoreFailedStorage(fsNamesystem, "true");
        verifyAuditRestoreFailedStorage(fsNamesystem, "false");
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        verifyAuditRestoreFailedStorageACE(fsNamesystem, "check");
        verifyAuditRestoreFailedStorageACE(fsNamesystem, "true");
        verifyAuditRestoreFailedStorageACE(fsNamesystem, "false");
    }

    @Test
    public void testGetDatanodeStorageReport() throws Exception {
        FSNamesystem fsNamesystem = Mockito.spy(TestAuditLoggerWithCommands.cluster.getNamesystem());
        Mockito.when(fsNamesystem.isExternalInvocation()).thenReturn(true);
        Server.Call call = Mockito.spy(new Server.Call(1, 1, null, null, RpcKind.RPC_BUILTIN, new byte[]{ 1, 2, 3 }));
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser(System.getProperty("user.name")));
        Server.getCurCall().set(call);
        DatanodeStorageReport[] reports = fsNamesystem.getDatanodeStorageReport(ALL);
        String auditLogString = ".*allowed=true.*cmd=" + ("getDatanodeStorageReport" + ".*");
        verifyAuditLogs(auditLogString);
        Mockito.when(call.getRemoteUser()).thenReturn(UserGroupInformation.createRemoteUser("theDoctor"));
        auditLogString = ".*allowed=false.*cmd=" + ("getDatanodeStorageReport" + ".*");
        try {
            fsNamesystem.getDatanodeStorageReport(ALL);
            Assert.fail("Should have thrown an AccessControlException!");
        } catch (AccessControlException ace) {
            verifyAuditLogs(auditLogString);
        }
    }
}

