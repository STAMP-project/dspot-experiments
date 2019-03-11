/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.namenode;


import DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY;
import DFSConfigKeys.DFS_STORAGE_POLICY_SATISFIER_RECHECK_TIMEOUT_MILLIS_KEY;
import StoragePolicySatisfierMode.NONE;
import StorageType.ARCHIVE;
import StorageType.DISK;
import StorageType.SSD;
import com.google.common.base.Supplier;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.fs.XAttr;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.balancer.NameNodeConnector;
import org.apache.hadoop.hdfs.server.common.HdfsServerConstants;
import org.apache.hadoop.hdfs.server.namenode.sps.StoragePolicySatisfier;
import org.apache.hadoop.hdfs.server.sps.ExternalSPSContext;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test persistence of satisfying files/directories.
 */
public class TestPersistentStoragePolicySatisfier {
    private static Configuration conf;

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem fs;

    private NameNodeConnector nnc;

    private StoragePolicySatisfier sps;

    private ExternalSPSContext ctxt;

    private static Path testFile = new Path("/testFile");

    private static String testFileName = TestPersistentStoragePolicySatisfier.testFile.toString();

    private static Path parentDir = new Path("/parentDir");

    private static Path parentFile = new Path(TestPersistentStoragePolicySatisfier.parentDir, "parentFile");

    private static String parentFileName = TestPersistentStoragePolicySatisfier.parentFile.toString();

    private static Path childDir = new Path(TestPersistentStoragePolicySatisfier.parentDir, "childDir");

    private static Path childFile = new Path(TestPersistentStoragePolicySatisfier.childDir, "childFile");

    private static String childFileName = TestPersistentStoragePolicySatisfier.childFile.toString();

    private static final String COLD = "COLD";

    private static final String WARM = "WARM";

    private static final String ONE_SSD = "ONE_SSD";

    private static StorageType[][] storageTypes = new StorageType[][]{ new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE, StorageType.SSD }, new StorageType[]{ StorageType.DISK, StorageType.ARCHIVE, StorageType.SSD } };

    private final int timeout = 90000;

    /**
     * While satisfying file/directory, trigger the cluster's checkpoint to
     * make sure satisfier persistence work as expected. This test case runs
     * as below:
     * 1. use satisfyStoragePolicy and add xAttr to the file.
     * 2. do the checkpoint by secondary NameNode.
     * 3. restart the cluster immediately.
     * 4. make sure all the storage policies are satisfied.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testWithCheckpoint() throws Exception {
        SecondaryNameNode secondary = null;
        try {
            clusterSetUp();
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(TestPersistentStoragePolicySatisfier.testFile, TestPersistentStoragePolicySatisfier.WARM);
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.testFile);
            // Start the checkpoint.
            TestPersistentStoragePolicySatisfier.conf.set(DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, "0.0.0.0:0");
            secondary = new SecondaryNameNode(TestPersistentStoragePolicySatisfier.conf);
            secondary.doCheckpoint();
            restartCluster();
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.testFileName, DISK, 1, timeout, TestPersistentStoragePolicySatisfier.fs);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.testFileName, ARCHIVE, 2, timeout, TestPersistentStoragePolicySatisfier.fs);
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(TestPersistentStoragePolicySatisfier.parentDir, TestPersistentStoragePolicySatisfier.COLD);
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.parentDir);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.parentFileName, ARCHIVE, 3, timeout, TestPersistentStoragePolicySatisfier.fs);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.childFileName, ARCHIVE, 3, timeout, TestPersistentStoragePolicySatisfier.fs);
        } finally {
            if (secondary != null) {
                secondary.shutdown();
            }
            clusterShutdown();
        }
    }

    /**
     * Tests to verify satisfier persistence working well with multiple
     * restarts operations. This test case runs as below:
     * 1. satisfy the storage policy of file1.
     * 2. restart the cluster.
     * 3. check whether all the blocks are satisfied.
     * 4. satisfy the storage policy of file2.
     * 5. restart the cluster.
     * 6. check whether all the blocks are satisfied.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testWithRestarts() throws Exception {
        try {
            clusterSetUp();
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(TestPersistentStoragePolicySatisfier.testFile, TestPersistentStoragePolicySatisfier.ONE_SSD);
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.testFile);
            restartCluster();
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.testFileName, SSD, 1, timeout, TestPersistentStoragePolicySatisfier.fs);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.testFileName, DISK, 2, timeout, TestPersistentStoragePolicySatisfier.fs);
            // test directory
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(TestPersistentStoragePolicySatisfier.parentDir, TestPersistentStoragePolicySatisfier.COLD);
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.parentDir);
            restartCluster();
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.parentFileName, ARCHIVE, 3, timeout, TestPersistentStoragePolicySatisfier.fs);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.childFileName, ARCHIVE, 3, timeout, TestPersistentStoragePolicySatisfier.fs);
        } finally {
            clusterShutdown();
        }
    }

    /**
     * Tests to verify SPS xattr will be removed if the satisfy work has
     * been finished, expect that the method satisfyStoragePolicy can be
     * invoked on the same file again after the block movement has been
     * finished:
     * 1. satisfy storage policy of file1.
     * 2. wait until storage policy is satisfied.
     * 3. satisfy storage policy of file1 again
     * 4. make sure step 3 works as expected.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testMultipleSatisfyStoragePolicy() throws Exception {
        try {
            // Lower block movement check for testing.
            TestPersistentStoragePolicySatisfier.conf = new HdfsConfiguration();
            final long minCheckTimeout = 500;// minimum value

            TestPersistentStoragePolicySatisfier.conf.setLong(DFS_STORAGE_POLICY_SATISFIER_RECHECK_TIMEOUT_MILLIS_KEY, minCheckTimeout);
            clusterSetUp(TestPersistentStoragePolicySatisfier.conf);
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(TestPersistentStoragePolicySatisfier.testFile, TestPersistentStoragePolicySatisfier.ONE_SSD);
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.testFile);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.testFileName, SSD, 1, timeout, TestPersistentStoragePolicySatisfier.fs);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.testFileName, DISK, 2, timeout, TestPersistentStoragePolicySatisfier.fs);
            // Make sure satisfy xattr has been removed.
            DFSTestUtil.waitForXattrRemoved(TestPersistentStoragePolicySatisfier.testFileName, HdfsServerConstants.XATTR_SATISFY_STORAGE_POLICY, TestPersistentStoragePolicySatisfier.cluster.getNamesystem(), 30000);
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(TestPersistentStoragePolicySatisfier.testFile, TestPersistentStoragePolicySatisfier.COLD);
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.testFile);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.testFileName, ARCHIVE, 3, timeout, TestPersistentStoragePolicySatisfier.fs);
        } finally {
            clusterShutdown();
        }
    }

    /**
     * Tests to verify SPS xattr is removed after SPS is dropped,
     * expect that if the SPS is disabled/dropped, the SPS
     * xattr should be removed accordingly:
     * 1. satisfy storage policy of file1.
     * 2. drop SPS thread in block manager.
     * 3. make sure sps xattr is removed.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000000)
    public void testDropSPS() throws Exception {
        try {
            clusterSetUp();
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(TestPersistentStoragePolicySatisfier.testFile, TestPersistentStoragePolicySatisfier.ONE_SSD);
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.testFile);
            TestPersistentStoragePolicySatisfier.cluster.getNamesystem().getBlockManager().getSPSManager().changeModeEvent(NONE);
            // Make sure satisfy xattr has been removed.
            DFSTestUtil.waitForXattrRemoved(TestPersistentStoragePolicySatisfier.testFileName, HdfsServerConstants.XATTR_SATISFY_STORAGE_POLICY, TestPersistentStoragePolicySatisfier.cluster.getNamesystem(), 30000);
        } finally {
            clusterShutdown();
        }
    }

    /**
     * Tests that Xattrs should be cleaned if all blocks already satisfied.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 300000)
    public void testSPSShouldNotLeakXattrIfStorageAlreadySatisfied() throws Exception {
        try {
            clusterSetUp();
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.testFileName, DISK, 3, timeout, TestPersistentStoragePolicySatisfier.fs);
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.testFile);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.testFileName, DISK, 3, timeout, TestPersistentStoragePolicySatisfier.fs);
            // Make sure satisfy xattr has been removed.
            DFSTestUtil.waitForXattrRemoved(TestPersistentStoragePolicySatisfier.testFileName, HdfsServerConstants.XATTR_SATISFY_STORAGE_POLICY, TestPersistentStoragePolicySatisfier.cluster.getNamesystem(), 30000);
        } finally {
            clusterShutdown();
        }
    }

    /**
     * Test loading of SPS xAttrs from the edits log when satisfyStoragePolicy
     * called on child file and parent directory.
     * 1. Create one directory and create one child file.
     * 2. Set storage policy for child file and call
     * satisfyStoragePolicy.
     * 3. wait for SPS to remove xAttr for file child file.
     * 4. Set storage policy for parent directory and call
     * satisfyStoragePolicy.
     * 5. restart the namenode.
     * NameNode should be started successfully.
     */
    @Test(timeout = 300000)
    public void testNameNodeRestartWhenSPSCalledOnChildFileAndParentDir() throws Exception {
        try {
            clusterSetUp();
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(TestPersistentStoragePolicySatisfier.childFile, "COLD");
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.childFile);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.childFile.toUri().getPath(), ARCHIVE, 3, 30000, TestPersistentStoragePolicySatisfier.cluster.getFileSystem());
            // wait for SPS to remove Xattr from file
            Thread.sleep(30000);
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(TestPersistentStoragePolicySatisfier.childDir, "COLD");
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.childDir);
            try {
                TestPersistentStoragePolicySatisfier.cluster.restartNameNodes();
            } catch (Exception e) {
                Assert.assertFalse(e.getMessage().contains("Cannot request to call satisfy storage policy"));
            }
        } finally {
            clusterShutdown();
        }
    }

    /**
     * Test SPS when satisfyStoragePolicy called on child file and
     * parent directory.
     * 1. Create one parent directory and child directory.
     * 2. Create some file in both the directory.
     * 3. Set storage policy for parent directory and call
     * satisfyStoragePolicy.
     * 4. Set storage policy for child directory and call
     * satisfyStoragePolicy.
     * 5. restart the namenode.
     * All the file blocks should satisfy the policy.
     */
    @Test(timeout = 300000)
    public void testSPSOnChildAndParentDirectory() throws Exception {
        try {
            clusterSetUp();
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(TestPersistentStoragePolicySatisfier.parentDir, "COLD");
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.childDir);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.childFileName, ARCHIVE, 3, 30000, TestPersistentStoragePolicySatisfier.cluster.getFileSystem());
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(TestPersistentStoragePolicySatisfier.parentDir);
            DFSTestUtil.waitExpectedStorageType(TestPersistentStoragePolicySatisfier.parentFileName, ARCHIVE, 3, 30000, TestPersistentStoragePolicySatisfier.cluster.getFileSystem());
        } finally {
            clusterShutdown();
        }
    }

    /**
     * Test SPS xAttr on directory. xAttr should be removed from the directory
     * once all the files blocks moved to specific storage.
     */
    @Test(timeout = 300000)
    public void testSPSxAttrWhenSpsCalledForDir() throws Exception {
        try {
            clusterSetUp();
            Path parent = new Path("/parent");
            // create parent dir
            TestPersistentStoragePolicySatisfier.fs.mkdirs(parent);
            // create 10 child files
            for (int i = 0; i < 5; i++) {
                DFSTestUtil.createFile(TestPersistentStoragePolicySatisfier.fs, new Path(parent, ("f" + i)), 1024, ((short) (3)), 0);
            }
            // Set storage policy for parent directory
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(parent, "COLD");
            // Stop one DN so we can check the SPS xAttr for directory.
            MiniDFSCluster.DataNodeProperties stopDataNode = TestPersistentStoragePolicySatisfier.cluster.stopDataNode(0);
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(parent);
            // Check xAttr for parent directory
            FSNamesystem namesystem = TestPersistentStoragePolicySatisfier.cluster.getNamesystem();
            INode inode = namesystem.getFSDirectory().getINode("/parent");
            XAttrFeature f = inode.getXAttrFeature();
            Assert.assertTrue("SPS xAttr should be exist", ((f.getXAttr(HdfsServerConstants.XATTR_SATISFY_STORAGE_POLICY)) != null));
            // check for the child, SPS xAttr should not be there
            for (int i = 0; i < 5; i++) {
                inode = namesystem.getFSDirectory().getINode(("/parent/f" + i));
                f = inode.getXAttrFeature();
                Assert.assertTrue((f == null));
            }
            TestPersistentStoragePolicySatisfier.cluster.restartDataNode(stopDataNode, false);
            // wait and check all the file block moved in ARCHIVE
            for (int i = 0; i < 5; i++) {
                DFSTestUtil.waitExpectedStorageType(("/parent/f" + i), ARCHIVE, 3, 30000, TestPersistentStoragePolicySatisfier.cluster.getFileSystem());
            }
            DFSTestUtil.waitForXattrRemoved("/parent", HdfsServerConstants.XATTR_SATISFY_STORAGE_POLICY, namesystem, 10000);
        } finally {
            clusterShutdown();
        }
    }

    /**
     * Test SPS xAttr on file. xAttr should be removed from the file
     * once all the blocks moved to specific storage.
     */
    @Test(timeout = 300000)
    public void testSPSxAttrWhenSpsCalledForFile() throws Exception {
        try {
            clusterSetUp();
            Path file = new Path("/file");
            DFSTestUtil.createFile(TestPersistentStoragePolicySatisfier.fs, file, 1024, ((short) (3)), 0);
            // Set storage policy for file
            TestPersistentStoragePolicySatisfier.fs.setStoragePolicy(file, "COLD");
            // Stop one DN so we can check the SPS xAttr for file.
            MiniDFSCluster.DataNodeProperties stopDataNode = TestPersistentStoragePolicySatisfier.cluster.stopDataNode(0);
            TestPersistentStoragePolicySatisfier.fs.satisfyStoragePolicy(file);
            // Check xAttr for parent directory
            FSNamesystem namesystem = TestPersistentStoragePolicySatisfier.cluster.getNamesystem();
            INode inode = namesystem.getFSDirectory().getINode("/file");
            XAttrFeature f = inode.getXAttrFeature();
            Assert.assertTrue("SPS xAttr should be exist", ((f.getXAttr(HdfsServerConstants.XATTR_SATISFY_STORAGE_POLICY)) != null));
            TestPersistentStoragePolicySatisfier.cluster.restartDataNode(stopDataNode, false);
            // wait and check all the file block moved in ARCHIVE
            DFSTestUtil.waitExpectedStorageType("/file", ARCHIVE, 3, 30000, TestPersistentStoragePolicySatisfier.cluster.getFileSystem());
            GenericTestUtils.waitFor(new Supplier<Boolean>() {
                @Override
                public Boolean get() {
                    List<XAttr> existingXAttrs = XAttrStorage.readINodeXAttrs(inode);
                    return !(existingXAttrs.contains(HdfsServerConstants.XATTR_SATISFY_STORAGE_POLICY));
                }
            }, 100, 10000);
        } finally {
            clusterShutdown();
        }
    }
}

