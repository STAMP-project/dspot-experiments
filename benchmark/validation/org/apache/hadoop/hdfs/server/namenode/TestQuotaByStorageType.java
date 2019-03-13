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


import HdfsConstants.ALLSSD_STORAGE_POLICY_NAME;
import HdfsConstants.HOT_STORAGE_POLICY_NAME;
import HdfsConstants.ONESSD_STORAGE_POLICY_NAME;
import HdfsConstants.QUOTA_DONT_SET;
import HdfsConstants.QUOTA_RESET;
import HdfsConstants.SafeModeAction.SAFEMODE_ENTER;
import HdfsConstants.SafeModeAction.SAFEMODE_LEAVE;
import HdfsConstants.WARM_STORAGE_POLICY_NAME;
import StorageType.DISK;
import StorageType.SSD;
import java.io.IOException;
import org.apache.hadoop.fs.ContentSummary;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.DSQuotaExceededException;
import org.apache.hadoop.hdfs.protocol.QuotaByStorageTypeExceededException;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotTestHelper;
import org.apache.hadoop.test.GenericTestUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestQuotaByStorageType {
    private static final int BLOCKSIZE = 1024;

    private static final short REPLICATION = 3;

    private static final long seed = 0L;

    private static final Path dir = new Path("/TestQuotaByStorageType");

    private MiniDFSCluster cluster;

    private FSDirectory fsdir;

    private DistributedFileSystem dfs;

    private FSNamesystem fsn;

    protected static final Logger LOG = LoggerFactory.getLogger(TestQuotaByStorageType.class);

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeWithFileCreateOneSSD() throws Exception {
        testQuotaByStorageTypeWithFileCreateCase(ONESSD_STORAGE_POLICY_NAME, SSD, ((short) (1)));
    }

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeWithFileCreateAllSSD() throws Exception {
        testQuotaByStorageTypeWithFileCreateCase(ALLSSD_STORAGE_POLICY_NAME, SSD, ((short) (3)));
    }

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeWithFileCreateAppend() throws Exception {
        final Path foo = new Path(TestQuotaByStorageType.dir, "foo");
        Path createdFile1 = new Path(foo, "created_file1.data");
        dfs.mkdirs(foo);
        // set storage policy on directory "foo" to ONESSD
        dfs.setStoragePolicy(foo, ONESSD_STORAGE_POLICY_NAME);
        // set quota by storage type on directory "foo"
        dfs.setQuotaByStorageType(foo, SSD, ((TestQuotaByStorageType.BLOCKSIZE) * 4));
        INode fnode = fsdir.getINode4Write(foo.toString());
        Assert.assertTrue(fnode.isDirectory());
        Assert.assertTrue(fnode.isQuotaSet());
        // Create file of size 2 * BLOCKSIZE under directory "foo"
        long file1Len = (TestQuotaByStorageType.BLOCKSIZE) * 2;
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        // Verify space consumed and remaining quota
        long ssdConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumed);
        // append several blocks
        int appendLen = (TestQuotaByStorageType.BLOCKSIZE) * 2;
        DFSTestUtil.appendFile(dfs, createdFile1, appendLen);
        file1Len += appendLen;
        ssdConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumed);
        ContentSummary cs = dfs.getContentSummary(foo);
        Assert.assertEquals(cs.getSpaceConsumed(), (file1Len * (TestQuotaByStorageType.REPLICATION)));
        Assert.assertEquals(cs.getTypeConsumed(SSD), file1Len);
        Assert.assertEquals(cs.getTypeConsumed(DISK), (file1Len * 2));
    }

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeWithFileCreateDelete() throws Exception {
        final Path foo = new Path(TestQuotaByStorageType.dir, "foo");
        Path createdFile1 = new Path(foo, "created_file1.data");
        dfs.mkdirs(foo);
        dfs.setStoragePolicy(foo, ONESSD_STORAGE_POLICY_NAME);
        // set quota by storage type on directory "foo"
        dfs.setQuotaByStorageType(foo, SSD, ((TestQuotaByStorageType.BLOCKSIZE) * 10));
        INode fnode = fsdir.getINode4Write(foo.toString());
        Assert.assertTrue(fnode.isDirectory());
        Assert.assertTrue(fnode.isQuotaSet());
        // Create file of size 2.5 * BLOCKSIZE under directory "foo"
        long file1Len = ((TestQuotaByStorageType.BLOCKSIZE) * 2) + ((TestQuotaByStorageType.BLOCKSIZE) / 2);
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        // Verify space consumed and remaining quota
        long storageTypeConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, storageTypeConsumed);
        // Delete file and verify the consumed space of the storage type is updated
        dfs.delete(createdFile1, false);
        storageTypeConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(0, storageTypeConsumed);
        QuotaCounts counts = fnode.computeQuotaUsage(fsn.getBlockManager().getStoragePolicySuite(), true);
        Assert.assertEquals(fnode.dumpTreeRecursively().toString(), 0, counts.getTypeSpaces().get(SSD));
        ContentSummary cs = dfs.getContentSummary(foo);
        Assert.assertEquals(cs.getSpaceConsumed(), 0);
        Assert.assertEquals(cs.getTypeConsumed(SSD), 0);
        Assert.assertEquals(cs.getTypeConsumed(DISK), 0);
    }

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeWithFileCreateRename() throws Exception {
        final Path foo = new Path(TestQuotaByStorageType.dir, "foo");
        dfs.mkdirs(foo);
        Path createdFile1foo = new Path(foo, "created_file1.data");
        final Path bar = new Path(TestQuotaByStorageType.dir, "bar");
        dfs.mkdirs(bar);
        Path createdFile1bar = new Path(bar, "created_file1.data");
        // set storage policy on directory "foo" and "bar" to ONESSD
        dfs.setStoragePolicy(foo, ONESSD_STORAGE_POLICY_NAME);
        dfs.setStoragePolicy(bar, ONESSD_STORAGE_POLICY_NAME);
        // set quota by storage type on directory "foo"
        dfs.setQuotaByStorageType(foo, SSD, ((TestQuotaByStorageType.BLOCKSIZE) * 4));
        dfs.setQuotaByStorageType(bar, SSD, ((TestQuotaByStorageType.BLOCKSIZE) * 2));
        INode fnode = fsdir.getINode4Write(foo.toString());
        Assert.assertTrue(fnode.isDirectory());
        Assert.assertTrue(fnode.isQuotaSet());
        // Create file of size 3 * BLOCKSIZE under directory "foo"
        long file1Len = (TestQuotaByStorageType.BLOCKSIZE) * 3;
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1foo, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        // Verify space consumed and remaining quota
        long ssdConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumed);
        // move file from foo to bar
        try {
            dfs.rename(createdFile1foo, createdFile1bar);
            Assert.fail("Should have failed with QuotaByStorageTypeExceededException ");
        } catch (Throwable t) {
            TestQuotaByStorageType.LOG.info("Got expected exception ", t);
        }
        ContentSummary cs = dfs.getContentSummary(foo);
        Assert.assertEquals(cs.getSpaceConsumed(), (file1Len * (TestQuotaByStorageType.REPLICATION)));
        Assert.assertEquals(cs.getTypeConsumed(SSD), file1Len);
        Assert.assertEquals(cs.getTypeConsumed(DISK), (file1Len * 2));
    }

    /**
     * Test if the quota can be correctly updated for create file even
     * QuotaByStorageTypeExceededException is thrown
     */
    @Test(timeout = 60000)
    public void testQuotaByStorageTypeExceptionWithFileCreate() throws Exception {
        final Path foo = new Path(TestQuotaByStorageType.dir, "foo");
        Path createdFile1 = new Path(foo, "created_file1.data");
        dfs.mkdirs(foo);
        dfs.setStoragePolicy(foo, ONESSD_STORAGE_POLICY_NAME);
        dfs.setQuotaByStorageType(foo, SSD, ((TestQuotaByStorageType.BLOCKSIZE) * 4));
        INode fnode = fsdir.getINode4Write(foo.toString());
        Assert.assertTrue(fnode.isDirectory());
        Assert.assertTrue(fnode.isQuotaSet());
        // Create the 1st file of size 2 * BLOCKSIZE under directory "foo" and expect no exception
        long file1Len = (TestQuotaByStorageType.BLOCKSIZE) * 2;
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        long currentSSDConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, currentSSDConsumed);
        // Create the 2nd file of size 1.5 * BLOCKSIZE under directory "foo" and expect no exception
        Path createdFile2 = new Path(foo, "created_file2.data");
        long file2Len = (TestQuotaByStorageType.BLOCKSIZE) + ((TestQuotaByStorageType.BLOCKSIZE) / 2);
        DFSTestUtil.createFile(dfs, createdFile2, bufLen, file2Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        currentSSDConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals((file1Len + file2Len), currentSSDConsumed);
        // Create the 3rd file of size BLOCKSIZE under directory "foo" and expect quota exceeded exception
        Path createdFile3 = new Path(foo, "created_file3.data");
        long file3Len = TestQuotaByStorageType.BLOCKSIZE;
        try {
            DFSTestUtil.createFile(dfs, createdFile3, bufLen, file3Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
            Assert.fail("Should have failed with QuotaByStorageTypeExceededException ");
        } catch (Throwable t) {
            TestQuotaByStorageType.LOG.info("Got expected exception ", t);
            currentSSDConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
            Assert.assertEquals((file1Len + file2Len), currentSSDConsumed);
        }
    }

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeParentOffChildOff() throws Exception {
        final Path parent = new Path(TestQuotaByStorageType.dir, "parent");
        final Path child = new Path(parent, "child");
        dfs.mkdirs(parent);
        dfs.mkdirs(child);
        dfs.setStoragePolicy(parent, ONESSD_STORAGE_POLICY_NAME);
        // Create file of size 2.5 * BLOCKSIZE under child directory.
        // Since both parent and child directory do not have SSD quota set,
        // expect succeed without exception
        Path createdFile1 = new Path(child, "created_file1.data");
        long file1Len = ((TestQuotaByStorageType.BLOCKSIZE) * 2) + ((TestQuotaByStorageType.BLOCKSIZE) / 2);
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        // Verify SSD usage at the root level as both parent/child don't have DirectoryWithQuotaFeature
        INode fnode = fsdir.getINode4Write("/");
        long ssdConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumed);
    }

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeParentOffChildOn() throws Exception {
        final Path parent = new Path(TestQuotaByStorageType.dir, "parent");
        final Path child = new Path(parent, "child");
        dfs.mkdirs(parent);
        dfs.mkdirs(child);
        dfs.setStoragePolicy(parent, ONESSD_STORAGE_POLICY_NAME);
        dfs.setQuotaByStorageType(child, SSD, (2 * (TestQuotaByStorageType.BLOCKSIZE)));
        // Create file of size 2.5 * BLOCKSIZE under child directory
        // Since child directory have SSD quota of 2 * BLOCKSIZE,
        // expect an exception when creating files under child directory.
        Path createdFile1 = new Path(child, "created_file1.data");
        long file1Len = ((TestQuotaByStorageType.BLOCKSIZE) * 2) + ((TestQuotaByStorageType.BLOCKSIZE) / 2);
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        try {
            DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
            Assert.fail("Should have failed with QuotaByStorageTypeExceededException ");
        } catch (Throwable t) {
            TestQuotaByStorageType.LOG.info("Got expected exception ", t);
        }
    }

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeParentOnChildOff() throws Exception {
        short replication = 1;
        final Path parent = new Path(TestQuotaByStorageType.dir, "parent");
        final Path child = new Path(parent, "child");
        dfs.mkdirs(parent);
        dfs.mkdirs(child);
        dfs.setStoragePolicy(parent, ONESSD_STORAGE_POLICY_NAME);
        dfs.setQuotaByStorageType(parent, SSD, (3 * (TestQuotaByStorageType.BLOCKSIZE)));
        // Create file of size 2.5 * BLOCKSIZE under child directory
        // Verify parent Quota applies
        Path createdFile1 = new Path(child, "created_file1.data");
        long file1Len = ((TestQuotaByStorageType.BLOCKSIZE) * 2) + ((TestQuotaByStorageType.BLOCKSIZE) / 2);
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, replication, TestQuotaByStorageType.seed);
        INode fnode = fsdir.getINode4Write(parent.toString());
        Assert.assertTrue(fnode.isDirectory());
        Assert.assertTrue(fnode.isQuotaSet());
        long currentSSDConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, currentSSDConsumed);
        // Create the 2nd file of size BLOCKSIZE under child directory and expect quota exceeded exception
        Path createdFile2 = new Path(child, "created_file2.data");
        long file2Len = TestQuotaByStorageType.BLOCKSIZE;
        try {
            DFSTestUtil.createFile(dfs, createdFile2, bufLen, file2Len, TestQuotaByStorageType.BLOCKSIZE, replication, TestQuotaByStorageType.seed);
            Assert.fail("Should have failed with QuotaByStorageTypeExceededException ");
        } catch (Throwable t) {
            TestQuotaByStorageType.LOG.info("Got expected exception ", t);
            currentSSDConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
            Assert.assertEquals(file1Len, currentSSDConsumed);
        }
    }

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeParentOnChildOn() throws Exception {
        final Path parent = new Path(TestQuotaByStorageType.dir, "parent");
        final Path child = new Path(parent, "child");
        dfs.mkdirs(parent);
        dfs.mkdirs(child);
        dfs.setStoragePolicy(parent, ONESSD_STORAGE_POLICY_NAME);
        dfs.setQuotaByStorageType(parent, SSD, (2 * (TestQuotaByStorageType.BLOCKSIZE)));
        dfs.setQuotaByStorageType(child, SSD, (3 * (TestQuotaByStorageType.BLOCKSIZE)));
        // Create file of size 2.5 * BLOCKSIZE under child directory
        // Verify parent Quota applies
        Path createdFile1 = new Path(child, "created_file1.data");
        long file1Len = ((TestQuotaByStorageType.BLOCKSIZE) * 2) + ((TestQuotaByStorageType.BLOCKSIZE) / 2);
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        try {
            DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
            Assert.fail("Should have failed with QuotaByStorageTypeExceededException ");
        } catch (Throwable t) {
            TestQuotaByStorageType.LOG.info("Got expected exception ", t);
        }
    }

    /**
     * Both traditional space quota and the storage type quota for SSD are set and
     * not exceeded.
     */
    @Test(timeout = 60000)
    public void testQuotaByStorageTypeWithTraditionalQuota() throws Exception {
        final Path foo = new Path(TestQuotaByStorageType.dir, "foo");
        dfs.mkdirs(foo);
        dfs.setStoragePolicy(foo, ONESSD_STORAGE_POLICY_NAME);
        dfs.setQuotaByStorageType(foo, SSD, ((TestQuotaByStorageType.BLOCKSIZE) * 10));
        dfs.setQuota(foo, ((Long.MAX_VALUE) - 1), (((TestQuotaByStorageType.REPLICATION) * (TestQuotaByStorageType.BLOCKSIZE)) * 10));
        INode fnode = fsdir.getINode4Write(foo.toString());
        Assert.assertTrue(fnode.isDirectory());
        Assert.assertTrue(fnode.isQuotaSet());
        Path createdFile = new Path(foo, "created_file.data");
        long fileLen = ((TestQuotaByStorageType.BLOCKSIZE) * 2) + ((TestQuotaByStorageType.BLOCKSIZE) / 2);
        DFSTestUtil.createFile(dfs, createdFile, ((TestQuotaByStorageType.BLOCKSIZE) / 16), fileLen, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        QuotaCounts cnt = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed();
        Assert.assertEquals(2, cnt.getNameSpace());
        Assert.assertEquals((fileLen * (TestQuotaByStorageType.REPLICATION)), cnt.getStorageSpace());
        dfs.delete(createdFile, true);
        QuotaCounts cntAfterDelete = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed();
        Assert.assertEquals(1, cntAfterDelete.getNameSpace());
        Assert.assertEquals(0, cntAfterDelete.getStorageSpace());
        // Validate the computeQuotaUsage()
        QuotaCounts counts = fnode.computeQuotaUsage(fsn.getBlockManager().getStoragePolicySuite(), true);
        Assert.assertEquals(fnode.dumpTreeRecursively().toString(), 1, counts.getNameSpace());
        Assert.assertEquals(fnode.dumpTreeRecursively().toString(), 0, counts.getStorageSpace());
    }

    /**
     * Both traditional space quota and the storage type quota for SSD are set and
     * exceeded. expect DSQuotaExceededException is thrown as we check traditional
     * space quota first and then storage type quota.
     */
    @Test(timeout = 60000)
    public void testQuotaByStorageTypeAndTraditionalQuotaException1() throws Exception {
        testQuotaByStorageTypeOrTraditionalQuotaExceededCase((4 * (TestQuotaByStorageType.REPLICATION)), 4, 5, TestQuotaByStorageType.REPLICATION);
    }

    /**
     * Both traditional space quota and the storage type quota for SSD are set and
     * SSD quota is exceeded but traditional space quota is not exceeded.
     */
    @Test(timeout = 60000)
    public void testQuotaByStorageTypeAndTraditionalQuotaException2() throws Exception {
        testQuotaByStorageTypeOrTraditionalQuotaExceededCase((5 * (TestQuotaByStorageType.REPLICATION)), 4, 5, TestQuotaByStorageType.REPLICATION);
    }

    /**
     * Both traditional space quota and the storage type quota for SSD are set and
     * traditional space quota is exceeded but SSD quota is not exceeded.
     */
    @Test(timeout = 60000)
    public void testQuotaByStorageTypeAndTraditionalQuotaException3() throws Exception {
        testQuotaByStorageTypeOrTraditionalQuotaExceededCase((4 * (TestQuotaByStorageType.REPLICATION)), 5, 5, TestQuotaByStorageType.REPLICATION);
    }

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeWithSnapshot() throws Exception {
        final Path sub1 = new Path(TestQuotaByStorageType.dir, "Sub1");
        dfs.mkdirs(sub1);
        // Setup ONE_SSD policy and SSD quota of 4 * BLOCKSIZE on sub1
        dfs.setStoragePolicy(sub1, ONESSD_STORAGE_POLICY_NAME);
        dfs.setQuotaByStorageType(sub1, SSD, (4 * (TestQuotaByStorageType.BLOCKSIZE)));
        INode sub1Node = fsdir.getINode4Write(sub1.toString());
        Assert.assertTrue(sub1Node.isDirectory());
        Assert.assertTrue(sub1Node.isQuotaSet());
        // Create file1 of size 2 * BLOCKSIZE under sub1
        Path file1 = new Path(sub1, "file1");
        long file1Len = 2 * (TestQuotaByStorageType.BLOCKSIZE);
        DFSTestUtil.createFile(dfs, file1, file1Len, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        // Create snapshot on sub1 named s1
        SnapshotTestHelper.createSnapshot(dfs, sub1, "s1");
        // Verify sub1 SSD usage is unchanged after creating snapshot s1
        long ssdConsumed = sub1Node.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumed);
        // Delete file1
        dfs.delete(file1, false);
        // Verify sub1 SSD usage is unchanged due to the existence of snapshot s1
        ssdConsumed = sub1Node.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumed);
        QuotaCounts counts1 = sub1Node.computeQuotaUsage(fsn.getBlockManager().getStoragePolicySuite(), true);
        Assert.assertEquals(sub1Node.dumpTreeRecursively().toString(), file1Len, counts1.getTypeSpaces().get(SSD));
        ContentSummary cs1 = dfs.getContentSummary(sub1);
        Assert.assertEquals(cs1.getSpaceConsumed(), (file1Len * (TestQuotaByStorageType.REPLICATION)));
        Assert.assertEquals(cs1.getTypeConsumed(SSD), file1Len);
        Assert.assertEquals(cs1.getTypeConsumed(DISK), (file1Len * 2));
        // Delete the snapshot s1
        dfs.deleteSnapshot(sub1, "s1");
        // Verify sub1 SSD usage is fully reclaimed and changed to 0
        ssdConsumed = sub1Node.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(0, ssdConsumed);
        QuotaCounts counts2 = sub1Node.computeQuotaUsage(fsn.getBlockManager().getStoragePolicySuite(), true);
        Assert.assertEquals(sub1Node.dumpTreeRecursively().toString(), 0, counts2.getTypeSpaces().get(SSD));
        ContentSummary cs2 = dfs.getContentSummary(sub1);
        Assert.assertEquals(cs2.getSpaceConsumed(), 0);
        Assert.assertEquals(cs2.getTypeConsumed(SSD), 0);
        Assert.assertEquals(cs2.getTypeConsumed(DISK), 0);
    }

    @Test(timeout = 60000)
    public void testQuotaByStorageTypeWithFileCreateTruncate() throws Exception {
        final Path foo = new Path(TestQuotaByStorageType.dir, "foo");
        Path createdFile1 = new Path(foo, "created_file1.data");
        dfs.mkdirs(foo);
        // set storage policy on directory "foo" to ONESSD
        dfs.setStoragePolicy(foo, ONESSD_STORAGE_POLICY_NAME);
        // set quota by storage type on directory "foo"
        dfs.setQuotaByStorageType(foo, SSD, ((TestQuotaByStorageType.BLOCKSIZE) * 4));
        INode fnode = fsdir.getINode4Write(foo.toString());
        Assert.assertTrue(fnode.isDirectory());
        Assert.assertTrue(fnode.isQuotaSet());
        // Create file of size 2 * BLOCKSIZE under directory "foo"
        long file1Len = (TestQuotaByStorageType.BLOCKSIZE) * 2;
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        // Verify SSD consumed before truncate
        long ssdConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumed);
        // Truncate file to 1 * BLOCKSIZE
        int newFile1Len = TestQuotaByStorageType.BLOCKSIZE;
        dfs.truncate(createdFile1, newFile1Len);
        // Verify SSD consumed after truncate
        ssdConsumed = fnode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(newFile1Len, ssdConsumed);
        ContentSummary cs = dfs.getContentSummary(foo);
        Assert.assertEquals(cs.getSpaceConsumed(), (newFile1Len * (TestQuotaByStorageType.REPLICATION)));
        Assert.assertEquals(cs.getTypeConsumed(SSD), newFile1Len);
        Assert.assertEquals(cs.getTypeConsumed(DISK), (newFile1Len * 2));
    }

    @Test
    public void testQuotaByStorageTypePersistenceInEditLog() throws IOException {
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        final Path testDir = new Path(TestQuotaByStorageType.dir, METHOD_NAME);
        Path createdFile1 = new Path(testDir, "created_file1.data");
        dfs.mkdirs(testDir);
        // set storage policy on testDir to ONESSD
        dfs.setStoragePolicy(testDir, ONESSD_STORAGE_POLICY_NAME);
        // set quota by storage type on testDir
        final long SSD_QUOTA = (TestQuotaByStorageType.BLOCKSIZE) * 4;
        dfs.setQuotaByStorageType(testDir, SSD, SSD_QUOTA);
        INode testDirNode = fsdir.getINode4Write(testDir.toString());
        Assert.assertTrue(testDirNode.isDirectory());
        Assert.assertTrue(testDirNode.isQuotaSet());
        // Create file of size 2 * BLOCKSIZE under testDir
        long file1Len = (TestQuotaByStorageType.BLOCKSIZE) * 2;
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        // Verify SSD consumed before namenode restart
        long ssdConsumed = testDirNode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumed);
        // Restart namenode to make sure the editlog is correct
        cluster.restartNameNode(true);
        refreshClusterState();
        INode testDirNodeAfterNNRestart = fsdir.getINode4Write(testDir.toString());
        // Verify quota is still set
        Assert.assertTrue(testDirNode.isDirectory());
        Assert.assertTrue(testDirNode.isQuotaSet());
        QuotaCounts qc = testDirNodeAfterNNRestart.getQuotaCounts();
        Assert.assertEquals(SSD_QUOTA, qc.getTypeSpace(SSD));
        for (StorageType t : StorageType.getTypesSupportingQuota()) {
            if (t != (StorageType.SSD)) {
                Assert.assertEquals(QUOTA_RESET, qc.getTypeSpace(t));
            }
        }
        long ssdConsumedAfterNNRestart = testDirNodeAfterNNRestart.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumedAfterNNRestart);
    }

    @Test
    public void testQuotaByStorageTypePersistenceInFsImage() throws IOException {
        final String METHOD_NAME = GenericTestUtils.getMethodName();
        final Path testDir = new Path(TestQuotaByStorageType.dir, METHOD_NAME);
        Path createdFile1 = new Path(testDir, "created_file1.data");
        dfs.mkdirs(testDir);
        // set storage policy on testDir to ONESSD
        dfs.setStoragePolicy(testDir, ONESSD_STORAGE_POLICY_NAME);
        // set quota by storage type on testDir
        final long SSD_QUOTA = (TestQuotaByStorageType.BLOCKSIZE) * 4;
        dfs.setQuotaByStorageType(testDir, SSD, SSD_QUOTA);
        INode testDirNode = fsdir.getINode4Write(testDir.toString());
        Assert.assertTrue(testDirNode.isDirectory());
        Assert.assertTrue(testDirNode.isQuotaSet());
        // Create file of size 2 * BLOCKSIZE under testDir
        long file1Len = (TestQuotaByStorageType.BLOCKSIZE) * 2;
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        // Verify SSD consumed before namenode restart
        long ssdConsumed = testDirNode.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumed);
        // Restart the namenode with checkpoint to make sure fsImage is correct
        dfs.setSafeMode(SAFEMODE_ENTER);
        dfs.saveNamespace();
        dfs.setSafeMode(SAFEMODE_LEAVE);
        cluster.restartNameNode(true);
        refreshClusterState();
        INode testDirNodeAfterNNRestart = fsdir.getINode4Write(testDir.toString());
        Assert.assertTrue(testDirNode.isDirectory());
        Assert.assertTrue(testDirNode.isQuotaSet());
        QuotaCounts qc = testDirNodeAfterNNRestart.getQuotaCounts();
        Assert.assertEquals(SSD_QUOTA, qc.getTypeSpace(SSD));
        for (StorageType t : StorageType.getTypesSupportingQuota()) {
            if (t != (StorageType.SSD)) {
                Assert.assertEquals(QUOTA_RESET, qc.getTypeSpace(t));
            }
        }
        long ssdConsumedAfterNNRestart = testDirNodeAfterNNRestart.asDirectory().getDirectoryWithQuotaFeature().getSpaceConsumed().getTypeSpaces().get(SSD);
        Assert.assertEquals(file1Len, ssdConsumedAfterNNRestart);
    }

    @Test(timeout = 60000)
    public void testContentSummaryWithoutQuotaByStorageType() throws Exception {
        final Path foo = new Path(TestQuotaByStorageType.dir, "foo");
        Path createdFile1 = new Path(foo, "created_file1.data");
        dfs.mkdirs(foo);
        // set storage policy on directory "foo" to ONESSD
        dfs.setStoragePolicy(foo, ONESSD_STORAGE_POLICY_NAME);
        INode fnode = fsdir.getINode4Write(foo.toString());
        Assert.assertTrue(fnode.isDirectory());
        Assert.assertTrue((!(fnode.isQuotaSet())));
        // Create file of size 2 * BLOCKSIZE under directory "foo"
        long file1Len = (TestQuotaByStorageType.BLOCKSIZE) * 2;
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        // Verify getContentSummary without any quota set
        ContentSummary cs = dfs.getContentSummary(foo);
        Assert.assertEquals(cs.getSpaceConsumed(), (file1Len * (TestQuotaByStorageType.REPLICATION)));
        Assert.assertEquals(cs.getTypeConsumed(SSD), file1Len);
        Assert.assertEquals(cs.getTypeConsumed(DISK), (file1Len * 2));
    }

    @Test(timeout = 60000)
    public void testContentSummaryWithoutStoragePolicy() throws Exception {
        final Path foo = new Path(TestQuotaByStorageType.dir, "foo");
        Path createdFile1 = new Path(foo, "created_file1.data");
        dfs.mkdirs(foo);
        INode fnode = fsdir.getINode4Write(foo.toString());
        Assert.assertTrue(fnode.isDirectory());
        Assert.assertTrue((!(fnode.isQuotaSet())));
        // Create file of size 2 * BLOCKSIZE under directory "foo"
        long file1Len = (TestQuotaByStorageType.BLOCKSIZE) * 2;
        int bufLen = (TestQuotaByStorageType.BLOCKSIZE) / 16;
        DFSTestUtil.createFile(dfs, createdFile1, bufLen, file1Len, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        // Verify getContentSummary without any quota set
        // Expect no type quota and usage information available
        ContentSummary cs = dfs.getContentSummary(foo);
        Assert.assertEquals(cs.getSpaceConsumed(), (file1Len * (TestQuotaByStorageType.REPLICATION)));
        for (StorageType t : StorageType.values()) {
            Assert.assertEquals(cs.getTypeConsumed(t), 0);
            Assert.assertEquals(cs.getTypeQuota(t), (-1));
        }
    }

    /**
     * Tests space quota for storage policy = WARM.
     */
    @Test
    public void testStorageSpaceQuotaWithWarmPolicy() throws IOException {
        final Path testDir = new Path(TestQuotaByStorageType.dir, GenericTestUtils.getMethodName());
        Assert.assertTrue(dfs.mkdirs(testDir));
        /* set policy to HOT */
        dfs.setStoragePolicy(testDir, HOT_STORAGE_POLICY_NAME);
        /* init space quota */
        final long storageSpaceQuota = (TestQuotaByStorageType.BLOCKSIZE) * 6;
        final long storageTypeSpaceQuota = (TestQuotaByStorageType.BLOCKSIZE) * 1;
        /* set space quota */
        dfs.setQuota(testDir, QUOTA_DONT_SET, storageSpaceQuota);
        /* init vars */
        Path createdFile;
        final long fileLen = TestQuotaByStorageType.BLOCKSIZE;
        /**
         * create one file with 3 replicas, REPLICATION * BLOCKSIZE go to DISK due
         * to HOT policy
         */
        createdFile = new Path(testDir, "file1.data");
        DFSTestUtil.createFile(dfs, createdFile, ((TestQuotaByStorageType.BLOCKSIZE) / 16), fileLen, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
        Assert.assertTrue(dfs.exists(createdFile));
        Assert.assertTrue(dfs.isFile(createdFile));
        /* set space quota for DISK */
        dfs.setQuotaByStorageType(testDir, DISK, storageTypeSpaceQuota);
        /* set policy to WARM */
        dfs.setStoragePolicy(testDir, WARM_STORAGE_POLICY_NAME);
        /* create another file with 3 replicas */
        try {
            createdFile = new Path(testDir, "file2.data");
            /**
             * This will fail since quota on DISK is 1 block but space consumed on
             * DISK is already 3 blocks due to the first file creation.
             */
            DFSTestUtil.createFile(dfs, createdFile, ((TestQuotaByStorageType.BLOCKSIZE) / 16), fileLen, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
            Assert.fail("should fail on QuotaByStorageTypeExceededException");
        } catch (QuotaByStorageTypeExceededException e) {
            TestQuotaByStorageType.LOG.info("Got expected exception ", e);
            Assert.assertThat(e.toString(), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("Quota by storage type"), CoreMatchers.containsString("DISK on path"), CoreMatchers.containsString(testDir.toString()))));
        }
    }

    /**
     * Tests if changing replication factor results in copying file as quota
     * doesn't exceed.
     */
    @Test(timeout = 30000)
    public void testStorageSpaceQuotaWithRepFactor() throws IOException {
        final Path testDir = new Path(TestQuotaByStorageType.dir, GenericTestUtils.getMethodName());
        Assert.assertTrue(dfs.mkdirs(testDir));
        final long storageSpaceQuota = (TestQuotaByStorageType.BLOCKSIZE) * 2;
        /* set policy to HOT */
        dfs.setStoragePolicy(testDir, HOT_STORAGE_POLICY_NAME);
        /* set space quota */
        dfs.setQuota(testDir, QUOTA_DONT_SET, storageSpaceQuota);
        /* init vars */
        Path createdFile = null;
        final long fileLen = TestQuotaByStorageType.BLOCKSIZE;
        try {
            /* create one file with 3 replicas */
            createdFile = new Path(testDir, "file1.data");
            DFSTestUtil.createFile(dfs, createdFile, ((TestQuotaByStorageType.BLOCKSIZE) / 16), fileLen, TestQuotaByStorageType.BLOCKSIZE, TestQuotaByStorageType.REPLICATION, TestQuotaByStorageType.seed);
            Assert.fail("should fail on DSQuotaExceededException");
        } catch (DSQuotaExceededException e) {
            TestQuotaByStorageType.LOG.info("Got expected exception ", e);
            Assert.assertThat(e.toString(), CoreMatchers.is(CoreMatchers.allOf(CoreMatchers.containsString("DiskSpace quota"), CoreMatchers.containsString(testDir.toString()))));
        }
        /* try creating file again with 2 replicas */
        createdFile = new Path(testDir, "file2.data");
        DFSTestUtil.createFile(dfs, createdFile, ((TestQuotaByStorageType.BLOCKSIZE) / 16), fileLen, TestQuotaByStorageType.BLOCKSIZE, ((short) (2)), TestQuotaByStorageType.seed);
        Assert.assertTrue(dfs.exists(createdFile));
        Assert.assertTrue(dfs.isFile(createdFile));
    }

    /**
     * Tests if clearing quota per heterogeneous storage doesn't result in
     * clearing quota for another storage.
     *
     * @throws IOException
     * 		
     */
    @Test(timeout = 30000)
    public void testStorageSpaceQuotaPerQuotaClear() throws IOException {
        final Path testDir = new Path(TestQuotaByStorageType.dir, GenericTestUtils.getMethodName());
        Assert.assertTrue(dfs.mkdirs(testDir));
        final long diskSpaceQuota = (TestQuotaByStorageType.BLOCKSIZE) * 1;
        final long ssdSpaceQuota = (TestQuotaByStorageType.BLOCKSIZE) * 2;
        /* set space quota */
        dfs.setQuotaByStorageType(testDir, DISK, diskSpaceQuota);
        dfs.setQuotaByStorageType(testDir, SSD, ssdSpaceQuota);
        final INode testDirNode = fsdir.getINode4Write(testDir.toString());
        Assert.assertTrue(testDirNode.isDirectory());
        Assert.assertTrue(testDirNode.isQuotaSet());
        /* verify space quota by storage type */
        Assert.assertEquals(diskSpaceQuota, testDirNode.asDirectory().getDirectoryWithQuotaFeature().getQuota().getTypeSpace(DISK));
        Assert.assertEquals(ssdSpaceQuota, testDirNode.asDirectory().getDirectoryWithQuotaFeature().getQuota().getTypeSpace(SSD));
        /* clear DISK space quota */
        dfs.setQuotaByStorageType(testDir, DISK, QUOTA_RESET);
        /* verify space quota by storage type after clearing DISK's */
        Assert.assertEquals((-1), testDirNode.asDirectory().getDirectoryWithQuotaFeature().getQuota().getTypeSpace(DISK));
        Assert.assertEquals(ssdSpaceQuota, testDirNode.asDirectory().getDirectoryWithQuotaFeature().getQuota().getTypeSpace(SSD));
    }
}

