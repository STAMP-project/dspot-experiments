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


import HdfsConstants.ONESSD_STORAGE_POLICY_NAME;
import HdfsDataOutputStream.SyncFlag.UPDATE_LENGTH;
import StorageType.SSD;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.DSQuotaExceededException;
import org.apache.hadoop.hdfs.protocol.QuotaByStorageTypeExceededException;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.datanode.InternalDataNodeTestUtils;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestDiskspaceQuotaUpdate {
    private static final int BLOCKSIZE = 1024;

    private static final short REPLICATION = 4;

    static final long seed = 0L;

    private static final Path BASE_DIR = new Path("/TestQuotaUpdate");

    private static Configuration conf;

    private static MiniDFSCluster cluster;

    /**
     * Test if the quota can be correctly updated for create file
     */
    @Test(timeout = 60000)
    public void testQuotaUpdateWithFileCreate() throws Exception {
        final Path foo = new Path(getParent(GenericTestUtils.getMethodName()), "foo");
        Path createdFile = new Path(foo, "created_file.data");
        getDFS().mkdirs(foo);
        getDFS().setQuota(foo, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        long fileLen = ((TestDiskspaceQuotaUpdate.BLOCKSIZE) * 2) + ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 2);
        DFSTestUtil.createFile(getDFS(), createdFile, ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 16), fileLen, TestDiskspaceQuotaUpdate.BLOCKSIZE, TestDiskspaceQuotaUpdate.REPLICATION, TestDiskspaceQuotaUpdate.seed);
        INode fnode = getFSDirectory().getINode4Write(foo.toString());
        Assert.assertTrue(fnode.isDirectory());
        Assert.assertTrue(fnode.isQuotaSet());
        QuotaCounts cnt = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed();
        Assert.assertEquals(2, cnt.getNameSpace());
        Assert.assertEquals((fileLen * (TestDiskspaceQuotaUpdate.REPLICATION)), cnt.getStorageSpace());
    }

    /**
     * Test if the quota can be correctly updated for append
     */
    @Test(timeout = 60000)
    public void testUpdateQuotaForAppend() throws Exception {
        final Path foo = new Path(getParent(GenericTestUtils.getMethodName()), "foo");
        final Path bar = new Path(foo, "bar");
        long currentFileLen = TestDiskspaceQuotaUpdate.BLOCKSIZE;
        DFSTestUtil.createFile(getDFS(), bar, currentFileLen, TestDiskspaceQuotaUpdate.REPLICATION, TestDiskspaceQuotaUpdate.seed);
        getDFS().setQuota(foo, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        // append half of the block data, the previous file length is at block
        // boundary
        DFSTestUtil.appendFile(getDFS(), bar, ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 2));
        currentFileLen += (TestDiskspaceQuotaUpdate.BLOCKSIZE) / 2;
        INodeDirectory fooNode = getFSDirectory().getINode4Write(foo.toString()).asDirectory();
        Assert.assertTrue(fooNode.isQuotaSet());
        QuotaCounts quota = fooNode.getDirectoryWithQuotaFeature().getSpaceConsumed();
        long ns = quota.getNameSpace();
        long ds = quota.getStorageSpace();
        Assert.assertEquals(2, ns);// foo and bar

        Assert.assertEquals((currentFileLen * (TestDiskspaceQuotaUpdate.REPLICATION)), ds);
        ContentSummary c = getDFS().getContentSummary(foo);
        Assert.assertEquals(c.getSpaceConsumed(), ds);
        // append another block, the previous file length is not at block boundary
        DFSTestUtil.appendFile(getDFS(), bar, TestDiskspaceQuotaUpdate.BLOCKSIZE);
        currentFileLen += TestDiskspaceQuotaUpdate.BLOCKSIZE;
        quota = fooNode.getDirectoryWithQuotaFeature().getSpaceConsumed();
        ns = quota.getNameSpace();
        ds = quota.getStorageSpace();
        Assert.assertEquals(2, ns);// foo and bar

        Assert.assertEquals((currentFileLen * (TestDiskspaceQuotaUpdate.REPLICATION)), ds);
        c = getDFS().getContentSummary(foo);
        Assert.assertEquals(c.getSpaceConsumed(), ds);
        // append several blocks
        DFSTestUtil.appendFile(getDFS(), bar, (((TestDiskspaceQuotaUpdate.BLOCKSIZE) * 3) + ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 8)));
        currentFileLen += ((TestDiskspaceQuotaUpdate.BLOCKSIZE) * 3) + ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 8);
        quota = fooNode.getDirectoryWithQuotaFeature().getSpaceConsumed();
        ns = quota.getNameSpace();
        ds = quota.getStorageSpace();
        Assert.assertEquals(2, ns);// foo and bar

        Assert.assertEquals((currentFileLen * (TestDiskspaceQuotaUpdate.REPLICATION)), ds);
        c = getDFS().getContentSummary(foo);
        Assert.assertEquals(c.getSpaceConsumed(), ds);
    }

    /**
     * Test if the quota can be correctly updated when file length is updated
     * through fsync
     */
    @Test(timeout = 60000)
    public void testUpdateQuotaForFSync() throws Exception {
        final Path foo = new Path(getParent(GenericTestUtils.getMethodName()), "foo");
        final Path bar = new Path(foo, "bar");
        DFSTestUtil.createFile(getDFS(), bar, TestDiskspaceQuotaUpdate.BLOCKSIZE, TestDiskspaceQuotaUpdate.REPLICATION, 0L);
        getDFS().setQuota(foo, ((Long.MAX_VALUE) - 1), ((Long.MAX_VALUE) - 1));
        FSDataOutputStream out = getDFS().append(bar);
        out.write(new byte[(TestDiskspaceQuotaUpdate.BLOCKSIZE) / 4]);
        ((DFSOutputStream) (out.getWrappedStream())).hsync(EnumSet.of(UPDATE_LENGTH));
        INodeDirectory fooNode = getFSDirectory().getINode4Write(foo.toString()).asDirectory();
        QuotaCounts quota = fooNode.getDirectoryWithQuotaFeature().getSpaceConsumed();
        long ns = quota.getNameSpace();
        long ds = quota.getStorageSpace();
        Assert.assertEquals(2, ns);// foo and bar

        Assert.assertEquals((((TestDiskspaceQuotaUpdate.BLOCKSIZE) * 2) * (TestDiskspaceQuotaUpdate.REPLICATION)), ds);// file is under construction

        out.write(new byte[(TestDiskspaceQuotaUpdate.BLOCKSIZE) / 4]);
        out.close();
        fooNode = getFSDirectory().getINode4Write(foo.toString()).asDirectory();
        quota = fooNode.getDirectoryWithQuotaFeature().getSpaceConsumed();
        ns = quota.getNameSpace();
        ds = quota.getStorageSpace();
        Assert.assertEquals(2, ns);
        Assert.assertEquals((((TestDiskspaceQuotaUpdate.BLOCKSIZE) + ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 2)) * (TestDiskspaceQuotaUpdate.REPLICATION)), ds);
        // append another block
        DFSTestUtil.appendFile(getDFS(), bar, TestDiskspaceQuotaUpdate.BLOCKSIZE);
        quota = fooNode.getDirectoryWithQuotaFeature().getSpaceConsumed();
        ns = quota.getNameSpace();
        ds = quota.getStorageSpace();
        Assert.assertEquals(2, ns);// foo and bar

        Assert.assertEquals(((((TestDiskspaceQuotaUpdate.BLOCKSIZE) * 2) + ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 2)) * (TestDiskspaceQuotaUpdate.REPLICATION)), ds);
    }

    /**
     * Test append over storage quota does not mark file as UC or create lease
     */
    @Test(timeout = 60000)
    public void testAppendOverStorageQuota() throws Exception {
        final Path dir = getParent(GenericTestUtils.getMethodName());
        final Path file = new Path(dir, "file");
        // create partial block file
        getDFS().mkdirs(dir);
        DFSTestUtil.createFile(getDFS(), file, ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 2), TestDiskspaceQuotaUpdate.REPLICATION, TestDiskspaceQuotaUpdate.seed);
        // lower quota to cause exception when appending to partial block
        getDFS().setQuota(dir, ((Long.MAX_VALUE) - 1), 1);
        final INodeDirectory dirNode = getFSDirectory().getINode4Write(dir.toString()).asDirectory();
        final long spaceUsed = dirNode.getDirectoryWithQuotaFeature().getSpaceConsumed().getStorageSpace();
        try {
            DFSTestUtil.appendFile(getDFS(), file, TestDiskspaceQuotaUpdate.BLOCKSIZE);
            Assert.fail("append didn't fail");
        } catch (DSQuotaExceededException e) {
            // ignore
        }
        LeaseManager lm = TestDiskspaceQuotaUpdate.cluster.getNamesystem().getLeaseManager();
        // check that the file exists, isn't UC, and has no dangling lease
        INodeFile inode = getFSDirectory().getINode(file.toString()).asFile();
        Assert.assertNotNull(inode);
        Assert.assertFalse("should not be UC", inode.isUnderConstruction());
        Assert.assertNull("should not have a lease", lm.getLease(inode));
        // make sure the quota usage is unchanged
        final long newSpaceUsed = dirNode.getDirectoryWithQuotaFeature().getSpaceConsumed().getStorageSpace();
        Assert.assertEquals(spaceUsed, newSpaceUsed);
        // make sure edits aren't corrupted
        getDFS().recoverLease(file);
        TestDiskspaceQuotaUpdate.cluster.restartNameNode(true);
    }

    /**
     * Test append over a specific type of storage quota does not mark file as
     * UC or create a lease
     */
    @Test(timeout = 60000)
    public void testAppendOverTypeQuota() throws Exception {
        final Path dir = getParent(GenericTestUtils.getMethodName());
        final Path file = new Path(dir, "file");
        // create partial block file
        getDFS().mkdirs(dir);
        // set the storage policy on dir
        getDFS().setStoragePolicy(dir, ONESSD_STORAGE_POLICY_NAME);
        DFSTestUtil.createFile(getDFS(), file, ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 2), TestDiskspaceQuotaUpdate.REPLICATION, TestDiskspaceQuotaUpdate.seed);
        // set quota of SSD to 1L
        getDFS().setQuotaByStorageType(dir, SSD, 1L);
        final INodeDirectory dirNode = getFSDirectory().getINode4Write(dir.toString()).asDirectory();
        final long spaceUsed = dirNode.getDirectoryWithQuotaFeature().getSpaceConsumed().getStorageSpace();
        try {
            DFSTestUtil.appendFile(getDFS(), file, TestDiskspaceQuotaUpdate.BLOCKSIZE);
            Assert.fail("append didn't fail");
        } catch (QuotaByStorageTypeExceededException e) {
            // ignore
        }
        // check that the file exists, isn't UC, and has no dangling lease
        LeaseManager lm = TestDiskspaceQuotaUpdate.cluster.getNamesystem().getLeaseManager();
        INodeFile inode = getFSDirectory().getINode(file.toString()).asFile();
        Assert.assertNotNull(inode);
        Assert.assertFalse("should not be UC", inode.isUnderConstruction());
        Assert.assertNull("should not have a lease", lm.getLease(inode));
        // make sure the quota usage is unchanged
        final long newSpaceUsed = dirNode.getDirectoryWithQuotaFeature().getSpaceConsumed().getStorageSpace();
        Assert.assertEquals(spaceUsed, newSpaceUsed);
        // make sure edits aren't corrupted
        getDFS().recoverLease(file);
        TestDiskspaceQuotaUpdate.cluster.restartNameNode(true);
    }

    /**
     * Test truncate over quota does not mark file as UC or create a lease
     */
    @Test(timeout = 60000)
    public void testTruncateOverQuota() throws Exception {
        final Path dir = getParent(GenericTestUtils.getMethodName());
        final Path file = new Path(dir, "file");
        // create partial block file
        getDFS().mkdirs(dir);
        DFSTestUtil.createFile(getDFS(), file, ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 2), TestDiskspaceQuotaUpdate.REPLICATION, TestDiskspaceQuotaUpdate.seed);
        // lower quota to cause exception when appending to partial block
        getDFS().setQuota(dir, ((Long.MAX_VALUE) - 1), 1);
        final INodeDirectory dirNode = getFSDirectory().getINode4Write(dir.toString()).asDirectory();
        final long spaceUsed = dirNode.getDirectoryWithQuotaFeature().getSpaceConsumed().getStorageSpace();
        try {
            getDFS().truncate(file, (((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 2) - 1));
            Assert.fail("truncate didn't fail");
        } catch (RemoteException e) {
            Assert.assertTrue(e.getClassName().contains("DSQuotaExceededException"));
        }
        // check that the file exists, isn't UC, and has no dangling lease
        LeaseManager lm = TestDiskspaceQuotaUpdate.cluster.getNamesystem().getLeaseManager();
        INodeFile inode = getFSDirectory().getINode(file.toString()).asFile();
        Assert.assertNotNull(inode);
        Assert.assertFalse("should not be UC", inode.isUnderConstruction());
        Assert.assertNull("should not have a lease", lm.getLease(inode));
        // make sure the quota usage is unchanged
        final long newSpaceUsed = dirNode.getDirectoryWithQuotaFeature().getSpaceConsumed().getStorageSpace();
        Assert.assertEquals(spaceUsed, newSpaceUsed);
        // make sure edits aren't corrupted
        getDFS().recoverLease(file);
        TestDiskspaceQuotaUpdate.cluster.restartNameNode(true);
    }

    /**
     * Check whether the quota is initialized correctly.
     */
    @Test
    public void testQuotaInitialization() throws Exception {
        final int size = 500;
        Path testDir = new Path(getParent(GenericTestUtils.getMethodName()), "testDir");
        long expectedSize = (3 * (TestDiskspaceQuotaUpdate.BLOCKSIZE)) + ((TestDiskspaceQuotaUpdate.BLOCKSIZE) / 2);
        getDFS().mkdirs(testDir);
        getDFS().setQuota(testDir, (size * 4), ((expectedSize * size) * 2));
        Path[] testDirs = new Path[size];
        for (int i = 0; i < size; i++) {
            testDirs[i] = new Path(testDir, ("sub" + i));
            getDFS().mkdirs(testDirs[i]);
            getDFS().setQuota(testDirs[i], 100, 1000000);
            DFSTestUtil.createFile(getDFS(), new Path(testDirs[i], "a"), expectedSize, ((short) (1)), 1L);
        }
        // Directly access the name system to obtain the current cached usage.
        INodeDirectory root = getFSDirectory().getRoot();
        HashMap<String, Long> nsMap = new HashMap<String, Long>();
        HashMap<String, Long> dsMap = new HashMap<String, Long>();
        scanDirsWithQuota(root, nsMap, dsMap, false);
        getFSDirectory().updateCountForQuota(1);
        scanDirsWithQuota(root, nsMap, dsMap, true);
        getFSDirectory().updateCountForQuota(2);
        scanDirsWithQuota(root, nsMap, dsMap, true);
        getFSDirectory().updateCountForQuota(4);
        scanDirsWithQuota(root, nsMap, dsMap, true);
    }

    /**
     * Test that the cached quota stays correct between the COMMIT
     * and COMPLETE block steps, even if the replication factor is
     * changed during this time.
     */
    @Test(timeout = 60000)
    public void testQuotaIssuesWhileCommitting() throws Exception {
        // We want a one-DN cluster so that we can force a lack of
        // commit by only instrumenting a single DN; we kill the other 3
        List<MiniDFSCluster.DataNodeProperties> dnprops = new ArrayList<>();
        try {
            for (int i = (TestDiskspaceQuotaUpdate.REPLICATION) - 1; i > 0; i--) {
                dnprops.add(TestDiskspaceQuotaUpdate.cluster.stopDataNode(i));
            }
            DatanodeProtocolClientSideTranslatorPB nnSpy = InternalDataNodeTestUtils.spyOnBposToNN(TestDiskspaceQuotaUpdate.cluster.getDataNodes().get(0), TestDiskspaceQuotaUpdate.cluster.getNameNode());
            testQuotaIssuesWhileCommittingHelper(nnSpy, ((short) (1)), ((short) (4)));
            testQuotaIssuesWhileCommittingHelper(nnSpy, ((short) (4)), ((short) (1)));
            // Don't actually change replication; just check that the sizes
            // agree during the commit period
            testQuotaIssuesWhileCommittingHelper(nnSpy, ((short) (1)), ((short) (1)));
        } finally {
            for (MiniDFSCluster.DataNodeProperties dnprop : dnprops) {
                TestDiskspaceQuotaUpdate.cluster.restartDataNode(dnprop);
            }
            TestDiskspaceQuotaUpdate.cluster.waitActive();
        }
    }

    @Test(timeout = 60000)
    public void testCachedComputedSizesAgreeBeforeCommitting() throws Exception {
        // Don't actually change replication; just check that the sizes
        // agree before the commit period
        testQuotaIssuesBeforeCommitting(((short) (1)), ((short) (1)));
    }

    @Test(timeout = 60000)
    public void testDecreaseReplicationBeforeCommitting() throws Exception {
        testQuotaIssuesBeforeCommitting(((short) (4)), ((short) (1)));
    }

    @Test(timeout = 60000)
    public void testIncreaseReplicationBeforeCommitting() throws Exception {
        testQuotaIssuesBeforeCommitting(((short) (1)), ((short) (4)));
    }
}

