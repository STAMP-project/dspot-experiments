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


import CacheDirectiveInfo.Builder;
import CacheDirectiveInfo.Expiration;
import CacheFlag.FORCE;
import CachePoolInfo.RELATIVE_EXPIRY_NEVER;
import DFSConfigKeys.DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY;
import DatanodeReportType.LIVE;
import Expiration.NEVER;
import NativeIO.POSIX;
import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import com.google.common.base.Supplier;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CacheFlag;
import org.apache.hadoop.fs.FileSystemTestHelper;
import org.apache.hadoop.fs.FsTracer;
import org.apache.hadoop.fs.InvalidRequestException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveEntry;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo;
import org.apache.hadoop.hdfs.protocol.CachePoolEntry;
import org.apache.hadoop.hdfs.protocol.CachePoolInfo;
import org.apache.hadoop.hdfs.protocol.CachePoolStats;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.server.datanode.DataNode;
import org.apache.hadoop.hdfs.server.datanode.DataNodeTestUtils;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.io.nativeio.NativeIO.POSIX.CacheManipulator;
import org.apache.hadoop.io.nativeio.NativeIO.POSIX.NoMlockCacheManipulator;
import org.apache.hadoop.security.AccessControlException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCacheDirectives {
    static final Logger LOG = LoggerFactory.getLogger(TestCacheDirectives.class);

    private static final UserGroupInformation unprivilegedUser = UserGroupInformation.createRemoteUser("unprivilegedUser");

    private static Configuration conf;

    private static MiniDFSCluster cluster;

    private static DistributedFileSystem dfs;

    private static NamenodeProtocols proto;

    private static NameNode namenode;

    private static CacheManipulator prevCacheManipulator;

    static {
        POSIX.setCacheManipulator(new NoMlockCacheManipulator());
    }

    private static final long BLOCK_SIZE = 4096;

    private static final int NUM_DATANODES = 4;

    // Most Linux installs will allow non-root users to lock 64KB.
    // In this test though, we stub out mlock so this doesn't matter.
    private static final long CACHE_CAPACITY = (64 * 1024) / (TestCacheDirectives.NUM_DATANODES);

    @Test(timeout = 60000)
    public void testBasicPoolOperations() throws Exception {
        final String poolName = "pool1";
        CachePoolInfo info = new CachePoolInfo(poolName).setOwnerName("bob").setGroupName("bobgroup").setMode(new FsPermission(((short) (493)))).setLimit(150L);
        // Add a pool
        TestCacheDirectives.dfs.addCachePool(info);
        // Do some bad addCachePools
        try {
            TestCacheDirectives.dfs.addCachePool(info);
            Assert.fail("added the pool with the same name twice");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("pool1 already exists", ioe);
        }
        try {
            TestCacheDirectives.dfs.addCachePool(new CachePoolInfo(""));
            Assert.fail("added empty pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("invalid empty cache pool name", ioe);
        }
        try {
            TestCacheDirectives.dfs.addCachePool(null);
            Assert.fail("added null pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("CachePoolInfo is null", ioe);
        }
        try {
            TestCacheDirectives.proto.addCachePool(new CachePoolInfo(""));
            Assert.fail("added empty pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("invalid empty cache pool name", ioe);
        }
        try {
            TestCacheDirectives.proto.addCachePool(null);
            Assert.fail("added null pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("CachePoolInfo is null", ioe);
        }
        // Modify the pool
        info.setOwnerName("jane").setGroupName("janegroup").setMode(new FsPermission(((short) (448)))).setLimit(314L);
        TestCacheDirectives.dfs.modifyCachePool(info);
        // Do some invalid modify pools
        try {
            TestCacheDirectives.dfs.modifyCachePool(new CachePoolInfo("fool"));
            Assert.fail("modified non-existent cache pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("fool does not exist", ioe);
        }
        try {
            TestCacheDirectives.dfs.modifyCachePool(new CachePoolInfo(""));
            Assert.fail("modified empty pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("invalid empty cache pool name", ioe);
        }
        try {
            TestCacheDirectives.dfs.modifyCachePool(null);
            Assert.fail("modified null pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("CachePoolInfo is null", ioe);
        }
        try {
            TestCacheDirectives.proto.modifyCachePool(new CachePoolInfo(""));
            Assert.fail("modified empty pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("invalid empty cache pool name", ioe);
        }
        try {
            TestCacheDirectives.proto.modifyCachePool(null);
            Assert.fail("modified null pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("CachePoolInfo is null", ioe);
        }
        // Remove the pool
        TestCacheDirectives.dfs.removeCachePool(poolName);
        // Do some bad removePools
        try {
            TestCacheDirectives.dfs.removeCachePool("pool99");
            Assert.fail(("expected to get an exception when " + "removing a non-existent pool."));
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains(("Cannot remove " + "non-existent cache pool"), ioe);
        }
        try {
            TestCacheDirectives.dfs.removeCachePool(poolName);
            Assert.fail(("expected to get an exception when " + "removing a non-existent pool."));
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains(("Cannot remove " + "non-existent cache pool"), ioe);
        }
        try {
            TestCacheDirectives.dfs.removeCachePool("");
            Assert.fail("removed empty pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("invalid empty cache pool name", ioe);
        }
        try {
            TestCacheDirectives.dfs.removeCachePool(null);
            Assert.fail("removed null pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("invalid empty cache pool name", ioe);
        }
        try {
            TestCacheDirectives.proto.removeCachePool("");
            Assert.fail("removed empty pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("invalid empty cache pool name", ioe);
        }
        try {
            TestCacheDirectives.proto.removeCachePool(null);
            Assert.fail("removed null pool");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("invalid empty cache pool name", ioe);
        }
        info = new CachePoolInfo("pool2");
        TestCacheDirectives.dfs.addCachePool(info);
        // Perform cache pool operations using a closed file system.
        DistributedFileSystem dfs1 = ((DistributedFileSystem) (TestCacheDirectives.cluster.getNewFileSystemInstance(0)));
        dfs1.close();
        try {
            dfs1.listCachePools();
            Assert.fail("listCachePools using a closed filesystem!");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Filesystem closed", ioe);
        }
        try {
            dfs1.addCachePool(info);
            Assert.fail("addCachePool using a closed filesystem!");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Filesystem closed", ioe);
        }
        try {
            dfs1.modifyCachePool(info);
            Assert.fail("modifyCachePool using a closed filesystem!");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Filesystem closed", ioe);
        }
        try {
            dfs1.removeCachePool(poolName);
            Assert.fail("removeCachePool using a closed filesystem!");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Filesystem closed", ioe);
        }
    }

    @Test(timeout = 60000)
    public void testCreateAndModifyPools() throws Exception {
        String poolName = "pool1";
        String ownerName = "abc";
        String groupName = "123";
        FsPermission mode = new FsPermission(((short) (493)));
        long limit = 150;
        TestCacheDirectives.dfs.addCachePool(new CachePoolInfo(poolName).setOwnerName(ownerName).setGroupName(groupName).setMode(mode).setLimit(limit));
        RemoteIterator<CachePoolEntry> iter = TestCacheDirectives.dfs.listCachePools();
        CachePoolInfo info = iter.next().getInfo();
        Assert.assertEquals(poolName, info.getPoolName());
        Assert.assertEquals(ownerName, info.getOwnerName());
        Assert.assertEquals(groupName, info.getGroupName());
        ownerName = "def";
        groupName = "456";
        mode = new FsPermission(((short) (448)));
        limit = 151;
        TestCacheDirectives.dfs.modifyCachePool(new CachePoolInfo(poolName).setOwnerName(ownerName).setGroupName(groupName).setMode(mode).setLimit(limit));
        iter = TestCacheDirectives.dfs.listCachePools();
        info = iter.next().getInfo();
        Assert.assertEquals(poolName, info.getPoolName());
        Assert.assertEquals(ownerName, info.getOwnerName());
        Assert.assertEquals(groupName, info.getGroupName());
        Assert.assertEquals(mode, info.getMode());
        Assert.assertEquals(limit, ((long) (info.getLimit())));
        TestCacheDirectives.dfs.removeCachePool(poolName);
        iter = TestCacheDirectives.dfs.listCachePools();
        Assert.assertFalse("expected no cache pools after deleting pool", iter.hasNext());
        TestCacheDirectives.proto.listCachePools(null);
        try {
            TestCacheDirectives.proto.removeCachePool("pool99");
            Assert.fail(("expected to get an exception when " + "removing a non-existent pool."));
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Cannot remove non-existent", ioe);
        }
        try {
            TestCacheDirectives.proto.removeCachePool(poolName);
            Assert.fail(("expected to get an exception when " + "removing a non-existent pool."));
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Cannot remove non-existent", ioe);
        }
        iter = TestCacheDirectives.dfs.listCachePools();
        Assert.assertFalse("expected no cache pools after deleting pool", iter.hasNext());
    }

    @Test(timeout = 60000)
    public void testAddRemoveDirectives() throws Exception {
        TestCacheDirectives.proto.addCachePool(new CachePoolInfo("pool1").setMode(new FsPermission(((short) (511)))));
        TestCacheDirectives.proto.addCachePool(new CachePoolInfo("pool2").setMode(new FsPermission(((short) (511)))));
        TestCacheDirectives.proto.addCachePool(new CachePoolInfo("pool3").setMode(new FsPermission(((short) (511)))));
        TestCacheDirectives.proto.addCachePool(new CachePoolInfo("pool4").setMode(new FsPermission(((short) (0)))));
        TestCacheDirectives.proto.addCachePool(new CachePoolInfo("pool5").setMode(new FsPermission(((short) (7)))).setOwnerName(TestCacheDirectives.unprivilegedUser.getShortUserName()));
        CacheDirectiveInfo alpha = new CacheDirectiveInfo.Builder().setPath(new Path("/alpha")).setPool("pool1").build();
        CacheDirectiveInfo beta = new CacheDirectiveInfo.Builder().setPath(new Path("/beta")).setPool("pool2").build();
        CacheDirectiveInfo delta = new CacheDirectiveInfo.Builder().setPath(new Path("/delta")).setPool("pool1").build();
        long alphaId = TestCacheDirectives.addAsUnprivileged(alpha);
        long alphaId2 = TestCacheDirectives.addAsUnprivileged(alpha);
        Assert.assertFalse(("Expected to get unique directives when re-adding an " + "existing CacheDirectiveInfo"), (alphaId == alphaId2));
        long betaId = TestCacheDirectives.addAsUnprivileged(beta);
        try {
            TestCacheDirectives.addAsUnprivileged(new CacheDirectiveInfo.Builder().setPath(new Path("/unicorn")).setPool("no_such_pool").build());
            Assert.fail("expected an error when adding to a non-existent pool.");
        } catch (InvalidRequestException ioe) {
            GenericTestUtils.assertExceptionContains("Unknown pool", ioe);
        }
        try {
            TestCacheDirectives.addAsUnprivileged(new CacheDirectiveInfo.Builder().setPath(new Path("/blackhole")).setPool("pool4").build());
            Assert.fail(("expected an error when adding to a pool with " + "mode 0 (no permissions for anyone)."));
        } catch (AccessControlException e) {
            GenericTestUtils.assertExceptionContains("Permission denied while accessing pool", e);
        }
        try {
            TestCacheDirectives.addAsUnprivileged(new CacheDirectiveInfo.Builder().setPath(new Path("/illegal:path/")).setPool("pool1").build());
            Assert.fail(("expected an error when adding a malformed path " + "to the cache directives."));
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("is not a valid DFS filename", e);
        }
        try {
            TestCacheDirectives.addAsUnprivileged(new CacheDirectiveInfo.Builder().setPath(new Path("/emptypoolname")).setReplication(((short) (1))).setPool("").build());
            Assert.fail(("expected an error when adding a cache " + "directive with an empty pool name."));
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("Invalid empty pool name", e);
        }
        long deltaId = TestCacheDirectives.addAsUnprivileged(delta);
        try {
            TestCacheDirectives.addAsUnprivileged(new CacheDirectiveInfo.Builder().setPath(new Path("/epsilon")).setPool("pool5").build());
            Assert.fail(("expected an error when adding to a pool with " + "mode 007 (no permissions for pool owner)."));
        } catch (AccessControlException e) {
            GenericTestUtils.assertExceptionContains("Permission denied while accessing pool", e);
        }
        // We expect the following to succeed, because DistributedFileSystem
        // qualifies the path.
        long relativeId = TestCacheDirectives.addAsUnprivileged(new CacheDirectiveInfo.Builder().setPath(new Path("relative")).setPool("pool1").build());
        RemoteIterator<CacheDirectiveEntry> iter;
        iter = TestCacheDirectives.dfs.listCacheDirectives(null);
        TestCacheDirectives.validateListAll(iter, alphaId, alphaId2, betaId, deltaId, relativeId);
        iter = TestCacheDirectives.dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().setPool("pool3").build());
        Assert.assertFalse(iter.hasNext());
        iter = TestCacheDirectives.dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().setPool("pool1").build());
        TestCacheDirectives.validateListAll(iter, alphaId, alphaId2, deltaId, relativeId);
        iter = TestCacheDirectives.dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().setPool("pool2").build());
        TestCacheDirectives.validateListAll(iter, betaId);
        iter = TestCacheDirectives.dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().setId(alphaId2).build());
        TestCacheDirectives.validateListAll(iter, alphaId2);
        iter = TestCacheDirectives.dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().setId(relativeId).build());
        TestCacheDirectives.validateListAll(iter, relativeId);
        TestCacheDirectives.dfs.removeCacheDirective(betaId);
        iter = TestCacheDirectives.dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().setPool("pool2").build());
        Assert.assertFalse(iter.hasNext());
        try {
            TestCacheDirectives.dfs.removeCacheDirective(betaId);
            Assert.fail("expected an error when removing a non-existent ID");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("No directive with ID", e);
        }
        try {
            TestCacheDirectives.proto.removeCacheDirective((-42L));
            Assert.fail("expected an error when removing a negative ID");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("Invalid negative ID", e);
        }
        try {
            TestCacheDirectives.proto.removeCacheDirective(43L);
            Assert.fail("expected an error when removing a non-existent ID");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("No directive with ID", e);
        }
        TestCacheDirectives.dfs.removeCacheDirective(alphaId);
        TestCacheDirectives.dfs.removeCacheDirective(alphaId2);
        TestCacheDirectives.dfs.removeCacheDirective(deltaId);
        TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(relativeId).setReplication(((short) (555))).build());
        iter = TestCacheDirectives.dfs.listCacheDirectives(null);
        Assert.assertTrue(iter.hasNext());
        CacheDirectiveInfo modified = iter.next().getInfo();
        Assert.assertEquals(relativeId, modified.getId().longValue());
        Assert.assertEquals(((short) (555)), modified.getReplication().shortValue());
        TestCacheDirectives.dfs.removeCacheDirective(relativeId);
        iter = TestCacheDirectives.dfs.listCacheDirectives(null);
        Assert.assertFalse(iter.hasNext());
        // Verify that PBCDs with path "." work correctly
        CacheDirectiveInfo directive = new CacheDirectiveInfo.Builder().setPath(new Path(".")).setPool("pool1").build();
        long id = TestCacheDirectives.dfs.addCacheDirective(directive);
        TestCacheDirectives.dfs.modifyCacheDirective(setId(id).setReplication(((short) (2))).build());
        TestCacheDirectives.dfs.removeCacheDirective(id);
        // Perform cache directive operations using a closed file system.
        DistributedFileSystem dfs1 = ((DistributedFileSystem) (TestCacheDirectives.cluster.getNewFileSystemInstance(0)));
        dfs1.close();
        try {
            dfs1.listCacheDirectives(null);
            Assert.fail("listCacheDirectives using a closed filesystem!");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Filesystem closed", ioe);
        }
        try {
            dfs1.addCacheDirective(alpha);
            Assert.fail("addCacheDirective using a closed filesystem!");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Filesystem closed", ioe);
        }
        try {
            dfs1.modifyCacheDirective(alpha);
            Assert.fail("modifyCacheDirective using a closed filesystem!");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Filesystem closed", ioe);
        }
        try {
            dfs1.removeCacheDirective(alphaId);
            Assert.fail("removeCacheDirective using a closed filesystem!");
        } catch (IOException ioe) {
            GenericTestUtils.assertExceptionContains("Filesystem closed", ioe);
        }
    }

    @Test(timeout = 60000)
    public void testCacheManagerRestart() throws Exception {
        SecondaryNameNode secondary = null;
        try {
            // Start a secondary namenode
            TestCacheDirectives.conf.set(DFS_NAMENODE_SECONDARY_HTTP_ADDRESS_KEY, "0.0.0.0:0");
            secondary = new SecondaryNameNode(TestCacheDirectives.conf);
            // Create and validate a pool
            final String pool = "poolparty";
            String groupName = "partygroup";
            FsPermission mode = new FsPermission(((short) (511)));
            long limit = 747;
            TestCacheDirectives.dfs.addCachePool(new CachePoolInfo(pool).setGroupName(groupName).setMode(mode).setLimit(limit));
            RemoteIterator<CachePoolEntry> pit = TestCacheDirectives.dfs.listCachePools();
            Assert.assertTrue("No cache pools found", pit.hasNext());
            CachePoolInfo info = pit.next().getInfo();
            Assert.assertEquals(pool, info.getPoolName());
            Assert.assertEquals(groupName, info.getGroupName());
            Assert.assertEquals(mode, info.getMode());
            Assert.assertEquals(limit, ((long) (info.getLimit())));
            Assert.assertFalse("Unexpected # of cache pools found", pit.hasNext());
            // Create some cache entries
            int numEntries = 10;
            String entryPrefix = "/party-";
            long prevId = -1;
            final Date expiry = new Date();
            for (int i = 0; i < numEntries; i++) {
                prevId = TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPath(new Path((entryPrefix + i))).setPool(pool).setExpiration(Expiration.newAbsolute(expiry.getTime())).build());
            }
            RemoteIterator<CacheDirectiveEntry> dit = TestCacheDirectives.dfs.listCacheDirectives(null);
            for (int i = 0; i < numEntries; i++) {
                Assert.assertTrue(("Unexpected # of cache entries: " + i), dit.hasNext());
                CacheDirectiveInfo cd = dit.next().getInfo();
                Assert.assertEquals((i + 1), cd.getId().longValue());
                Assert.assertEquals((entryPrefix + i), cd.getPath().toUri().getPath());
                Assert.assertEquals(pool, cd.getPool());
            }
            Assert.assertFalse("Unexpected # of cache directives found", dit.hasNext());
            // Checkpoint once to set some cache pools and directives on 2NN side
            secondary.doCheckpoint();
            // Add some more CacheManager state
            final String imagePool = "imagePool";
            TestCacheDirectives.dfs.addCachePool(new CachePoolInfo(imagePool));
            prevId = TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPath(new Path("/image")).setPool(imagePool).build());
            // Save a new image to force a fresh fsimage download
            TestCacheDirectives.dfs.setSafeMode(SAFEMODE_ENTER);
            TestCacheDirectives.dfs.saveNamespace();
            TestCacheDirectives.dfs.setSafeMode(SAFEMODE_LEAVE);
            // Checkpoint again forcing a reload of FSN state
            boolean fetchImage = secondary.doCheckpoint();
            Assert.assertTrue("Secondary should have fetched a new fsimage from NameNode", fetchImage);
            // Remove temp pool and directive
            TestCacheDirectives.dfs.removeCachePool(imagePool);
            // Restart namenode
            TestCacheDirectives.cluster.restartNameNode();
            // Check that state came back up
            pit = TestCacheDirectives.dfs.listCachePools();
            Assert.assertTrue("No cache pools found", pit.hasNext());
            info = pit.next().getInfo();
            Assert.assertEquals(pool, info.getPoolName());
            Assert.assertEquals(pool, info.getPoolName());
            Assert.assertEquals(groupName, info.getGroupName());
            Assert.assertEquals(mode, info.getMode());
            Assert.assertEquals(limit, ((long) (info.getLimit())));
            Assert.assertFalse("Unexpected # of cache pools found", pit.hasNext());
            dit = TestCacheDirectives.dfs.listCacheDirectives(null);
            for (int i = 0; i < numEntries; i++) {
                Assert.assertTrue(("Unexpected # of cache entries: " + i), dit.hasNext());
                CacheDirectiveInfo cd = dit.next().getInfo();
                Assert.assertEquals((i + 1), cd.getId().longValue());
                Assert.assertEquals((entryPrefix + i), cd.getPath().toUri().getPath());
                Assert.assertEquals(pool, cd.getPool());
                Assert.assertEquals(expiry.getTime(), cd.getExpiration().getMillis());
            }
            Assert.assertFalse("Unexpected # of cache directives found", dit.hasNext());
            long nextId = TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPath(new Path("/foobar")).setPool(pool).build());
            Assert.assertEquals((prevId + 1), nextId);
        } finally {
            if (secondary != null) {
                secondary.shutdown();
            }
        }
    }

    @Test(timeout = 120000)
    public void testWaitForCachedReplicas() throws Exception {
        FileSystemTestHelper helper = new FileSystemTestHelper();
        GenericTestUtils.waitFor(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return ((TestCacheDirectives.namenode.getNamesystem().getCacheCapacity()) == ((TestCacheDirectives.NUM_DATANODES) * (TestCacheDirectives.CACHE_CAPACITY))) && ((TestCacheDirectives.namenode.getNamesystem().getCacheUsed()) == 0);
            }
        }, 500, 60000);
        // Send a cache report referring to a bogus block.  It is important that
        // the NameNode be robust against this.
        NamenodeProtocols nnRpc = TestCacheDirectives.namenode.getRpcServer();
        DataNode dn0 = TestCacheDirectives.cluster.getDataNodes().get(0);
        String bpid = TestCacheDirectives.cluster.getNamesystem().getBlockPoolId();
        LinkedList<Long> bogusBlockIds = new LinkedList<Long>();
        bogusBlockIds.add(999999L);
        nnRpc.cacheReport(dn0.getDNRegistrationForBP(bpid), bpid, bogusBlockIds);
        Path rootDir = helper.getDefaultWorkingDirectory(TestCacheDirectives.dfs);
        // Create the pool
        final String pool = "friendlyPool";
        nnRpc.addCachePool(new CachePoolInfo("friendlyPool"));
        // Create some test files
        final int numFiles = 2;
        final int numBlocksPerFile = 2;
        final List<String> paths = new ArrayList<String>(numFiles);
        for (int i = 0; i < numFiles; i++) {
            Path p = new Path(rootDir, ("testCachePaths-" + i));
            FileSystemTestHelper.createFile(TestCacheDirectives.dfs, p, numBlocksPerFile, ((int) (TestCacheDirectives.BLOCK_SIZE)));
            paths.add(p.toUri().getPath());
        }
        // Check the initial statistics at the namenode
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 0, 0, "testWaitForCachedReplicas:0");
        // Cache and check each path in sequence
        int expected = 0;
        for (int i = 0; i < numFiles; i++) {
            CacheDirectiveInfo directive = new CacheDirectiveInfo.Builder().setPath(new Path(paths.get(i))).setPool(pool).build();
            nnRpc.addCacheDirective(directive, EnumSet.noneOf(CacheFlag.class));
            expected += numBlocksPerFile;
            TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, expected, expected, "testWaitForCachedReplicas:1");
        }
        // Check that the datanodes have the right cache values
        DatanodeInfo[] live = TestCacheDirectives.dfs.getDataNodeStats(LIVE);
        Assert.assertEquals("Unexpected number of live nodes", TestCacheDirectives.NUM_DATANODES, live.length);
        long totalUsed = 0;
        for (DatanodeInfo dn : live) {
            final long cacheCapacity = dn.getCacheCapacity();
            final long cacheUsed = dn.getCacheUsed();
            final long cacheRemaining = dn.getCacheRemaining();
            Assert.assertEquals("Unexpected cache capacity", TestCacheDirectives.CACHE_CAPACITY, cacheCapacity);
            Assert.assertEquals("Capacity not equal to used + remaining", cacheCapacity, (cacheUsed + cacheRemaining));
            Assert.assertEquals("Remaining not equal to capacity - used", (cacheCapacity - cacheUsed), cacheRemaining);
            totalUsed += cacheUsed;
        }
        Assert.assertEquals((expected * (TestCacheDirectives.BLOCK_SIZE)), totalUsed);
        // Uncache and check each path in sequence
        RemoteIterator<CacheDirectiveEntry> entries = new org.apache.hadoop.hdfs.protocol.CacheDirectiveIterator(nnRpc, null, FsTracer.get(TestCacheDirectives.conf));
        for (int i = 0; i < numFiles; i++) {
            CacheDirectiveEntry entry = entries.next();
            nnRpc.removeCacheDirective(entry.getInfo().getId());
            expected -= numBlocksPerFile;
            TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, expected, expected, "testWaitForCachedReplicas:2");
        }
    }

    @Test(timeout = 120000)
    public void testWaitForCachedReplicasInDirectory() throws Exception {
        // Create the pool
        final String pool = "friendlyPool";
        final CachePoolInfo poolInfo = new CachePoolInfo(pool);
        TestCacheDirectives.dfs.addCachePool(poolInfo);
        // Create some test files
        final List<Path> paths = new LinkedList<Path>();
        paths.add(new Path("/foo/bar"));
        paths.add(new Path("/foo/baz"));
        paths.add(new Path("/foo2/bar2"));
        paths.add(new Path("/foo2/baz2"));
        TestCacheDirectives.dfs.mkdir(new Path("/foo"), FsPermission.getDirDefault());
        TestCacheDirectives.dfs.mkdir(new Path("/foo2"), FsPermission.getDirDefault());
        final int numBlocksPerFile = 2;
        for (Path path : paths) {
            FileSystemTestHelper.createFile(TestCacheDirectives.dfs, path, numBlocksPerFile, ((int) (TestCacheDirectives.BLOCK_SIZE)), ((short) (3)), false);
        }
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 0, 0, "testWaitForCachedReplicasInDirectory:0");
        // cache entire directory
        long id = TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPath(new Path("/foo")).setReplication(((short) (2))).setPool(pool).build());
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 4, 8, "testWaitForCachedReplicasInDirectory:1:blocks");
        // Verify that listDirectives gives the stats we want.
        TestCacheDirectives.waitForCacheDirectiveStats(TestCacheDirectives.dfs, ((4 * numBlocksPerFile) * (TestCacheDirectives.BLOCK_SIZE)), ((4 * numBlocksPerFile) * (TestCacheDirectives.BLOCK_SIZE)), 2, 2, new CacheDirectiveInfo.Builder().setPath(new Path("/foo")).build(), "testWaitForCachedReplicasInDirectory:1:directive");
        TestCacheDirectives.waitForCachePoolStats(TestCacheDirectives.dfs, ((4 * numBlocksPerFile) * (TestCacheDirectives.BLOCK_SIZE)), ((4 * numBlocksPerFile) * (TestCacheDirectives.BLOCK_SIZE)), 2, 2, poolInfo, "testWaitForCachedReplicasInDirectory:1:pool");
        long id2 = TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPath(new Path("/foo/bar")).setReplication(((short) (4))).setPool(pool).build());
        // wait for an additional 2 cached replicas to come up
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 4, 10, "testWaitForCachedReplicasInDirectory:2:blocks");
        // the directory directive's stats are unchanged
        TestCacheDirectives.waitForCacheDirectiveStats(TestCacheDirectives.dfs, ((4 * numBlocksPerFile) * (TestCacheDirectives.BLOCK_SIZE)), ((4 * numBlocksPerFile) * (TestCacheDirectives.BLOCK_SIZE)), 2, 2, new CacheDirectiveInfo.Builder().setPath(new Path("/foo")).build(), "testWaitForCachedReplicasInDirectory:2:directive-1");
        // verify /foo/bar's stats
        // only 3 because the file only has 3 replicas, not 4 as requested.
        // only 0 because the file can't be fully cached
        TestCacheDirectives.waitForCacheDirectiveStats(TestCacheDirectives.dfs, ((4 * numBlocksPerFile) * (TestCacheDirectives.BLOCK_SIZE)), ((3 * numBlocksPerFile) * (TestCacheDirectives.BLOCK_SIZE)), 1, 0, new CacheDirectiveInfo.Builder().setPath(new Path("/foo/bar")).build(), "testWaitForCachedReplicasInDirectory:2:directive-2");
        TestCacheDirectives.waitForCachePoolStats(TestCacheDirectives.dfs, (((4 + 4) * numBlocksPerFile) * (TestCacheDirectives.BLOCK_SIZE)), (((4 + 3) * numBlocksPerFile) * (TestCacheDirectives.BLOCK_SIZE)), 3, 2, poolInfo, "testWaitForCachedReplicasInDirectory:2:pool");
        // remove and watch numCached go to 0
        TestCacheDirectives.dfs.removeCacheDirective(id);
        TestCacheDirectives.dfs.removeCacheDirective(id2);
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 0, 0, "testWaitForCachedReplicasInDirectory:3:blocks");
        TestCacheDirectives.waitForCachePoolStats(TestCacheDirectives.dfs, 0, 0, 0, 0, poolInfo, "testWaitForCachedReplicasInDirectory:3:pool");
    }

    /**
     * Tests stepping the cache replication factor up and down, checking the
     * number of cached replicas and blocks as well as the advertised locations.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 120000)
    public void testReplicationFactor() throws Exception {
        // Create the pool
        final String pool = "friendlyPool";
        TestCacheDirectives.dfs.addCachePool(new CachePoolInfo(pool));
        // Create some test files
        final List<Path> paths = new LinkedList<Path>();
        paths.add(new Path("/foo/bar"));
        paths.add(new Path("/foo/baz"));
        paths.add(new Path("/foo2/bar2"));
        paths.add(new Path("/foo2/baz2"));
        TestCacheDirectives.dfs.mkdir(new Path("/foo"), FsPermission.getDirDefault());
        TestCacheDirectives.dfs.mkdir(new Path("/foo2"), FsPermission.getDirDefault());
        final int numBlocksPerFile = 2;
        for (Path path : paths) {
            FileSystemTestHelper.createFile(TestCacheDirectives.dfs, path, numBlocksPerFile, ((int) (TestCacheDirectives.BLOCK_SIZE)), ((short) (3)), false);
        }
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 0, 0, "testReplicationFactor:0");
        TestCacheDirectives.checkNumCachedReplicas(TestCacheDirectives.dfs, paths, 0, 0);
        // cache directory
        long id = TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPath(new Path("/foo")).setReplication(((short) (1))).setPool(pool).build());
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 4, 4, "testReplicationFactor:1");
        TestCacheDirectives.checkNumCachedReplicas(TestCacheDirectives.dfs, paths, 4, 4);
        // step up the replication factor
        for (int i = 2; i <= 3; i++) {
            TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(id).setReplication(((short) (i))).build());
            TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 4, (4 * i), "testReplicationFactor:2");
            TestCacheDirectives.checkNumCachedReplicas(TestCacheDirectives.dfs, paths, 4, (4 * i));
        }
        // step it down
        for (int i = 2; i >= 1; i--) {
            TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(id).setReplication(((short) (i))).build());
            TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 4, (4 * i), "testReplicationFactor:3");
            TestCacheDirectives.checkNumCachedReplicas(TestCacheDirectives.dfs, paths, 4, (4 * i));
        }
        // remove and watch numCached go to 0
        TestCacheDirectives.dfs.removeCacheDirective(id);
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 0, 0, "testReplicationFactor:4");
        TestCacheDirectives.checkNumCachedReplicas(TestCacheDirectives.dfs, paths, 0, 0);
    }

    @Test(timeout = 60000)
    public void testListCachePoolPermissions() throws Exception {
        final UserGroupInformation myUser = UserGroupInformation.createRemoteUser("myuser");
        final DistributedFileSystem myDfs = ((DistributedFileSystem) (DFSTestUtil.getFileSystemAs(myUser, TestCacheDirectives.conf)));
        final String poolName = "poolparty";
        TestCacheDirectives.dfs.addCachePool(new CachePoolInfo(poolName).setMode(new FsPermission(((short) (448)))));
        // Should only see partial info
        RemoteIterator<CachePoolEntry> it = myDfs.listCachePools();
        CachePoolInfo info = it.next().getInfo();
        Assert.assertFalse(it.hasNext());
        Assert.assertEquals("Expected pool name", poolName, info.getPoolName());
        Assert.assertNull("Unexpected owner name", info.getOwnerName());
        Assert.assertNull("Unexpected group name", info.getGroupName());
        Assert.assertNull("Unexpected mode", info.getMode());
        Assert.assertNull("Unexpected limit", info.getLimit());
        // Modify the pool so myuser is now the owner
        final long limit = 99;
        TestCacheDirectives.dfs.modifyCachePool(new CachePoolInfo(poolName).setOwnerName(myUser.getShortUserName()).setLimit(limit));
        // Should see full info
        it = myDfs.listCachePools();
        info = it.next().getInfo();
        Assert.assertFalse(it.hasNext());
        Assert.assertEquals("Expected pool name", poolName, info.getPoolName());
        Assert.assertEquals("Mismatched owner name", myUser.getShortUserName(), info.getOwnerName());
        Assert.assertNotNull("Expected group name", info.getGroupName());
        Assert.assertEquals("Mismatched mode", ((short) (448)), info.getMode().toShort());
        Assert.assertEquals("Mismatched limit", limit, ((long) (info.getLimit())));
    }

    @Test(timeout = 120000)
    public void testExpiry() throws Exception {
        String pool = "pool1";
        TestCacheDirectives.dfs.addCachePool(new CachePoolInfo(pool));
        Path p = new Path("/mypath");
        DFSTestUtil.createFile(TestCacheDirectives.dfs, p, ((TestCacheDirectives.BLOCK_SIZE) * 2), ((short) (2)), 2457);
        // Expire after test timeout
        Date start = new Date();
        Date expiry = DateUtils.addSeconds(start, 120);
        final long id = TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPath(p).setPool(pool).setExpiration(Expiration.newAbsolute(expiry)).setReplication(((short) (2))).build());
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.cluster.getNameNode(), 2, 4, "testExpiry:1");
        // Change it to expire sooner
        TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(id).setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newRelative(0)).build());
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.cluster.getNameNode(), 0, 0, "testExpiry:2");
        RemoteIterator<CacheDirectiveEntry> it = TestCacheDirectives.dfs.listCacheDirectives(null);
        CacheDirectiveEntry ent = it.next();
        Assert.assertFalse(it.hasNext());
        Date entryExpiry = new Date(ent.getInfo().getExpiration().getMillis());
        Assert.assertTrue("Directive should have expired", entryExpiry.before(new Date()));
        // Change it back to expire later
        TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(id).setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newRelative(120000)).build());
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.cluster.getNameNode(), 2, 4, "testExpiry:3");
        it = TestCacheDirectives.dfs.listCacheDirectives(null);
        ent = it.next();
        Assert.assertFalse(it.hasNext());
        entryExpiry = new Date(ent.getInfo().getExpiration().getMillis());
        Assert.assertTrue("Directive should not have expired", entryExpiry.after(new Date()));
        // Verify that setting a negative TTL throws an error
        try {
            TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(id).setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newRelative((-1))).build());
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("Cannot set a negative expiration", e);
        }
    }

    @Test(timeout = 120000)
    public void testLimit() throws Exception {
        try {
            TestCacheDirectives.dfs.addCachePool(new CachePoolInfo("poolofnegativity").setLimit((-99L)));
            Assert.fail("Should not be able to set a negative limit");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("negative", e);
        }
        final String destiny = "poolofdestiny";
        final Path path1 = new Path("/destiny");
        DFSTestUtil.createFile(TestCacheDirectives.dfs, path1, (2 * (TestCacheDirectives.BLOCK_SIZE)), ((short) (1)), 38036);
        // Start off with a limit that is too small
        final CachePoolInfo poolInfo = new CachePoolInfo(destiny).setLimit(((2 * (TestCacheDirectives.BLOCK_SIZE)) - 1));
        TestCacheDirectives.dfs.addCachePool(poolInfo);
        final CacheDirectiveInfo info1 = new CacheDirectiveInfo.Builder().setPool(destiny).setPath(path1).build();
        try {
            TestCacheDirectives.dfs.addCacheDirective(info1);
            Assert.fail("Should not be able to cache when there is no more limit");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("remaining capacity", e);
        }
        // Raise the limit up to fit and it should work this time
        poolInfo.setLimit((2 * (TestCacheDirectives.BLOCK_SIZE)));
        TestCacheDirectives.dfs.modifyCachePool(poolInfo);
        long id1 = TestCacheDirectives.dfs.addCacheDirective(info1);
        TestCacheDirectives.waitForCachePoolStats(TestCacheDirectives.dfs, (2 * (TestCacheDirectives.BLOCK_SIZE)), (2 * (TestCacheDirectives.BLOCK_SIZE)), 1, 1, poolInfo, "testLimit:1");
        // Adding another file, it shouldn't be cached
        final Path path2 = new Path("/failure");
        DFSTestUtil.createFile(TestCacheDirectives.dfs, path2, TestCacheDirectives.BLOCK_SIZE, ((short) (1)), 38037);
        try {
            TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPool(destiny).setPath(path2).build(), EnumSet.noneOf(CacheFlag.class));
            Assert.fail("Should not be able to add another cached file");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("remaining capacity", e);
        }
        // Bring the limit down, the first file should get uncached
        poolInfo.setLimit(TestCacheDirectives.BLOCK_SIZE);
        TestCacheDirectives.dfs.modifyCachePool(poolInfo);
        TestCacheDirectives.waitForCachePoolStats(TestCacheDirectives.dfs, (2 * (TestCacheDirectives.BLOCK_SIZE)), 0, 1, 0, poolInfo, "testLimit:2");
        RemoteIterator<CachePoolEntry> it = TestCacheDirectives.dfs.listCachePools();
        Assert.assertTrue("Expected a cache pool", it.hasNext());
        CachePoolStats stats = it.next().getStats();
        Assert.assertEquals("Overlimit bytes should be difference of needed and limit", TestCacheDirectives.BLOCK_SIZE, stats.getBytesOverlimit());
        // Moving a directive to a pool without enough limit should fail
        CachePoolInfo inadequate = new CachePoolInfo("poolofinadequacy").setLimit(TestCacheDirectives.BLOCK_SIZE);
        TestCacheDirectives.dfs.addCachePool(inadequate);
        try {
            TestCacheDirectives.dfs.modifyCacheDirective(setId(id1).setPool(inadequate.getPoolName()).build(), EnumSet.noneOf(CacheFlag.class));
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("remaining capacity", e);
        }
        // Succeeds when force=true
        TestCacheDirectives.dfs.modifyCacheDirective(setId(id1).setPool(inadequate.getPoolName()).build(), EnumSet.of(FORCE));
        // Also can add with force=true
        TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPool(inadequate.getPoolName()).setPath(path1).build(), EnumSet.of(FORCE));
    }

    @Test(timeout = 30000)
    public void testMaxRelativeExpiry() throws Exception {
        // Test that negative and really big max expirations can't be set during add
        try {
            TestCacheDirectives.dfs.addCachePool(new CachePoolInfo("failpool").setMaxRelativeExpiryMs((-1L)));
            Assert.fail("Added a pool with a negative max expiry.");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("negative", e);
        }
        try {
            TestCacheDirectives.dfs.addCachePool(new CachePoolInfo("failpool").setMaxRelativeExpiryMs(((Long.MAX_VALUE) - 1)));
            Assert.fail("Added a pool with too big of a max expiry.");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("too big", e);
        }
        // Test that setting a max relative expiry on a pool works
        CachePoolInfo coolPool = new CachePoolInfo("coolPool");
        final long poolExpiration = (1000 * 60) * 10L;
        TestCacheDirectives.dfs.addCachePool(coolPool.setMaxRelativeExpiryMs(poolExpiration));
        RemoteIterator<CachePoolEntry> poolIt = TestCacheDirectives.dfs.listCachePools();
        CachePoolInfo listPool = poolIt.next().getInfo();
        Assert.assertFalse("Should only be one pool", poolIt.hasNext());
        Assert.assertEquals("Expected max relative expiry to match set value", poolExpiration, listPool.getMaxRelativeExpiryMs().longValue());
        // Test that negative and really big max expirations can't be modified
        try {
            TestCacheDirectives.dfs.addCachePool(coolPool.setMaxRelativeExpiryMs((-1L)));
            Assert.fail("Added a pool with a negative max expiry.");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("negative", e);
        }
        try {
            TestCacheDirectives.dfs.modifyCachePool(coolPool.setMaxRelativeExpiryMs(((CachePoolInfo.RELATIVE_EXPIRY_NEVER) + 1)));
            Assert.fail("Added a pool with too big of a max expiry.");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("too big", e);
        }
        // Test that adding a directives without an expiration uses the pool's max
        CacheDirectiveInfo defaultExpiry = new CacheDirectiveInfo.Builder().setPath(new Path("/blah")).setPool(coolPool.getPoolName()).build();
        TestCacheDirectives.dfs.addCacheDirective(defaultExpiry);
        RemoteIterator<CacheDirectiveEntry> dirIt = TestCacheDirectives.dfs.listCacheDirectives(defaultExpiry);
        CacheDirectiveInfo listInfo = dirIt.next().getInfo();
        Assert.assertFalse("Should only have one entry in listing", dirIt.hasNext());
        long listExpiration = (listInfo.getExpiration().getAbsoluteMillis()) - (new Date().getTime());
        Assert.assertTrue("Directive expiry should be approximately the pool's max expiry", ((Math.abs((listExpiration - poolExpiration))) < (10 * 1000)));
        // Test that the max is enforced on add for relative and absolute
        CacheDirectiveInfo.Builder builder = new CacheDirectiveInfo.Builder().setPath(new Path("/lolcat")).setPool(coolPool.getPoolName());
        try {
            TestCacheDirectives.dfs.addCacheDirective(builder.setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newRelative((poolExpiration + 1))).build());
            Assert.fail("Added a directive that exceeds pool's max relative expiration");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("exceeds the max relative expiration", e);
        }
        try {
            TestCacheDirectives.dfs.addCacheDirective(builder.setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newAbsolute((((new Date().getTime()) + poolExpiration) + (10 * 1000)))).build());
            Assert.fail("Added a directive that exceeds pool's max relative expiration");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("exceeds the max relative expiration", e);
        }
        // Test that max is enforced on modify for relative and absolute Expirations
        try {
            TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder(defaultExpiry).setId(listInfo.getId()).setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newRelative((poolExpiration + 1))).build());
            Assert.fail("Modified a directive to exceed pool's max relative expiration");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("exceeds the max relative expiration", e);
        }
        try {
            TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder(defaultExpiry).setId(listInfo.getId()).setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newAbsolute((((new Date().getTime()) + poolExpiration) + (10 * 1000)))).build());
            Assert.fail("Modified a directive to exceed pool's max relative expiration");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("exceeds the max relative expiration", e);
        }
        // Test some giant limit values with add
        try {
            TestCacheDirectives.dfs.addCacheDirective(builder.setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newRelative(Long.MAX_VALUE)).build());
            Assert.fail("Added a directive with a gigantic max value");
        } catch (IllegalArgumentException e) {
            GenericTestUtils.assertExceptionContains("is too far in the future", e);
        }
        try {
            TestCacheDirectives.dfs.addCacheDirective(builder.setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newAbsolute(Long.MAX_VALUE)).build());
            Assert.fail("Added a directive with a gigantic max value");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("is too far in the future", e);
        }
        // Test some giant limit values with modify
        try {
            TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder(defaultExpiry).setId(listInfo.getId()).setExpiration(NEVER).build());
            Assert.fail("Modified a directive to exceed pool's max relative expiration");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("exceeds the max relative expiration", e);
        }
        try {
            TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder(defaultExpiry).setId(listInfo.getId()).setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newAbsolute(Long.MAX_VALUE)).build());
            Assert.fail("Modified a directive to exceed pool's max relative expiration");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("is too far in the future", e);
        }
        // Test that the max is enforced on modify correctly when changing pools
        CachePoolInfo destPool = new CachePoolInfo("destPool");
        TestCacheDirectives.dfs.addCachePool(destPool.setMaxRelativeExpiryMs((poolExpiration / 2)));
        try {
            TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder(defaultExpiry).setId(listInfo.getId()).setPool(destPool.getPoolName()).build());
            Assert.fail("Modified a directive to a pool with a lower max expiration");
        } catch (InvalidRequestException e) {
            GenericTestUtils.assertExceptionContains("exceeds the max relative expiration", e);
        }
        TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder(defaultExpiry).setId(listInfo.getId()).setPool(destPool.getPoolName()).setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newRelative((poolExpiration / 2))).build());
        dirIt = TestCacheDirectives.dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().setPool(destPool.getPoolName()).build());
        listInfo = dirIt.next().getInfo();
        listExpiration = (listInfo.getExpiration().getAbsoluteMillis()) - (new Date().getTime());
        Assert.assertTrue(((("Unexpected relative expiry " + listExpiration) + " expected approximately ") + (poolExpiration / 2)), ((Math.abs(((poolExpiration / 2) - listExpiration))) < (10 * 1000)));
        // Test that cache pool and directive expiry can be modified back to never
        TestCacheDirectives.dfs.modifyCachePool(destPool.setMaxRelativeExpiryMs(RELATIVE_EXPIRY_NEVER));
        poolIt = TestCacheDirectives.dfs.listCachePools();
        listPool = poolIt.next().getInfo();
        while (!(listPool.getPoolName().equals(destPool.getPoolName()))) {
            listPool = poolIt.next().getInfo();
        } 
        Assert.assertEquals("Expected max relative expiry to match set value", RELATIVE_EXPIRY_NEVER, listPool.getMaxRelativeExpiryMs().longValue());
        TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(listInfo.getId()).setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newRelative(CachePoolInfo.RELATIVE_EXPIRY_NEVER)).build());
        // Test modifying close to the limit
        TestCacheDirectives.dfs.modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(listInfo.getId()).setExpiration(org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo.Expiration.newRelative(((CachePoolInfo.RELATIVE_EXPIRY_NEVER) - 1))).build());
    }

    @Test(timeout = 60000)
    public void testExceedsCapacity() throws Exception {
        // Create a giant file
        final Path fileName = new Path("/exceeds");
        final long fileLen = (TestCacheDirectives.CACHE_CAPACITY) * ((TestCacheDirectives.NUM_DATANODES) * 2);
        int numCachedReplicas = ((int) (((TestCacheDirectives.CACHE_CAPACITY) * (TestCacheDirectives.NUM_DATANODES)) / (TestCacheDirectives.BLOCK_SIZE)));
        DFSTestUtil.createFile(TestCacheDirectives.dfs, fileName, fileLen, ((short) (TestCacheDirectives.NUM_DATANODES)), 1027565);
        TestCacheDirectives.dfs.addCachePool(new CachePoolInfo("pool"));
        TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPool("pool").setPath(fileName).setReplication(((short) (1))).build());
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, (-1), numCachedReplicas, "testExceeds:1");
        checkPendingCachedEmpty(TestCacheDirectives.cluster);
        checkPendingCachedEmpty(TestCacheDirectives.cluster);
        // Try creating a file with giant-sized blocks that exceed cache capacity
        TestCacheDirectives.dfs.delete(fileName, false);
        DFSTestUtil.createFile(TestCacheDirectives.dfs, fileName, 4096, fileLen, ((TestCacheDirectives.CACHE_CAPACITY) * 2), ((short) (1)), 1027565);
        checkPendingCachedEmpty(TestCacheDirectives.cluster);
        checkPendingCachedEmpty(TestCacheDirectives.cluster);
    }

    @Test(timeout = 60000)
    public void testNoBackingReplica() throws Exception {
        // Cache all three replicas for a file.
        final Path filename = new Path("/noback");
        final short replication = ((short) (3));
        DFSTestUtil.createFile(TestCacheDirectives.dfs, filename, 1, replication, 2988);
        TestCacheDirectives.dfs.addCachePool(new CachePoolInfo("pool"));
        TestCacheDirectives.dfs.addCacheDirective(new CacheDirectiveInfo.Builder().setPool("pool").setPath(filename).setReplication(replication).build());
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 1, replication, "testNoBackingReplica:1");
        // Pause cache reports while we change the replication factor.
        // This will orphan some cached replicas.
        DataNodeTestUtils.setCacheReportsDisabledForTests(TestCacheDirectives.cluster, true);
        try {
            TestCacheDirectives.dfs.setReplication(filename, ((short) (1)));
            DFSTestUtil.waitForReplication(TestCacheDirectives.dfs, filename, ((short) (1)), 30000);
            // The cache locations should drop down to 1 even without cache reports.
            TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 1, ((short) (1)), "testNoBackingReplica:2");
        } finally {
            DataNodeTestUtils.setCacheReportsDisabledForTests(TestCacheDirectives.cluster, false);
        }
    }

    @Test
    public void testNoLookupsWhenNotUsed() throws Exception {
        CacheManager cm = TestCacheDirectives.cluster.getNamesystem().getCacheManager();
        LocatedBlocks locations = Mockito.mock(LocatedBlocks.class);
        cm.setCachedLocations(locations);
        Mockito.verifyZeroInteractions(locations);
    }

    @Test(timeout = 120000)
    public void testAddingCacheDirectiveInfosWhenCachingIsDisabled() throws Exception {
        TestCacheDirectives.cluster.shutdown();
        HdfsConfiguration config = TestCacheDirectives.createCachingConf();
        config.setBoolean(DFSConfigKeys.DFS_NAMENODE_CACHING_ENABLED_KEY, false);
        TestCacheDirectives.cluster = new MiniDFSCluster.Builder(config).numDataNodes(TestCacheDirectives.NUM_DATANODES).build();
        TestCacheDirectives.cluster.waitActive();
        TestCacheDirectives.dfs = TestCacheDirectives.cluster.getFileSystem();
        TestCacheDirectives.namenode = TestCacheDirectives.cluster.getNameNode();
        CacheManager cacheManager = TestCacheDirectives.namenode.getNamesystem().getCacheManager();
        Assert.assertFalse(cacheManager.isEnabled());
        Assert.assertNull(cacheManager.getCacheReplicationMonitor());
        // Create the pool
        String pool = "pool1";
        TestCacheDirectives.namenode.getRpcServer().addCachePool(new CachePoolInfo(pool));
        // Create some test files
        final int numFiles = 2;
        final int numBlocksPerFile = 2;
        final List<String> paths = new ArrayList<String>(numFiles);
        for (int i = 0; i < numFiles; i++) {
            Path p = new Path(("/testCachePaths-" + i));
            FileSystemTestHelper.createFile(TestCacheDirectives.dfs, p, numBlocksPerFile, ((int) (TestCacheDirectives.BLOCK_SIZE)));
            paths.add(p.toUri().getPath());
        }
        // Check the initial statistics at the namenode
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, 0, 0, "testAddingCacheDirectiveInfosWhenCachingIsDisabled:0");
        // Cache and check each path in sequence
        int expected = 0;
        for (int i = 0; i < numFiles; i++) {
            CacheDirectiveInfo directive = new CacheDirectiveInfo.Builder().setPath(new Path(paths.get(i))).setPool(pool).build();
            TestCacheDirectives.dfs.addCacheDirective(directive);
            TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, expected, 0, "testAddingCacheDirectiveInfosWhenCachingIsDisabled:1");
        }
        Thread.sleep(20000);
        TestCacheDirectives.waitForCachedBlocks(TestCacheDirectives.namenode, expected, 0, "testAddingCacheDirectiveInfosWhenCachingIsDisabled:2");
    }
}

