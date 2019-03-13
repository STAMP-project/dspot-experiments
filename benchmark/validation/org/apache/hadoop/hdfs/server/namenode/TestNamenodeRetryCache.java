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


import CreateFlag.APPEND;
import CreateFlag.CREATE;
import DFSConfigKeys.DFS_NAMENODE_ENABLE_RETRY_CACHE_KEY;
import HAServiceState.STANDBY;
import Rename.NONE;
import RpcKind.RPC_PROTOCOL_BUFFER;
import SystemErasureCodingPolicies.RS_6_3_POLICY_ID;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.LastBlockWithStatus;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.apache.hadoop.hdfs.server.protocol.NamenodeProtocols;
import org.apache.hadoop.ipc.ClientId;
import org.apache.hadoop.ipc.RetryCache.CacheEntry;
import org.apache.hadoop.ipc.Server;
import org.apache.hadoop.ipc.StandbyException;
import org.apache.hadoop.security.UserGroupInformation;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.LightWeightCache;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for ensuring the namenode retry cache works correctly for
 * non-idempotent requests.
 *
 * Retry cache works based on tracking previously received request based on the
 * ClientId and CallId received in RPC requests and storing the response. The
 * response is replayed on retry when the same request is received again.
 *
 * The test works by manipulating the Rpc {@link Server} current RPC call. For
 * testing retried requests, an Rpc callId is generated only once using
 * {@link #newCall()} and reused for many method calls. For testing non-retried
 * request, a new callId is generated using {@link #newCall()}.
 */
public class TestNamenodeRetryCache {
    private static final byte[] CLIENT_ID = ClientId.getClientId();

    private static MiniDFSCluster cluster;

    private static ErasureCodingPolicy defaultEcPolicy = SystemErasureCodingPolicies.getByID(RS_6_3_POLICY_ID);

    private static int numDataNodes = ((TestNamenodeRetryCache.defaultEcPolicy.getNumDataUnits()) + (TestNamenodeRetryCache.defaultEcPolicy.getNumParityUnits())) + 1;

    private static NamenodeProtocols nnRpc;

    private static final FsPermission perm = FsPermission.getDefault();

    private static DistributedFileSystem filesystem;

    private static int callId = 100;

    private static Configuration conf;

    private static final int BlockSize = 512;

    static class DummyCall extends Server.Call {
        private UserGroupInformation ugi;

        DummyCall(int callId, byte[] clientId) {
            super(callId, 1, null, null, RPC_PROTOCOL_BUFFER, clientId);
            try {
                ugi = UserGroupInformation.getCurrentUser();
            } catch (IOException ioe) {
            }
        }

        @Override
        public UserGroupInformation getRemoteUser() {
            return ugi;
        }
    }

    /**
     * Tests for concat call
     */
    @Test
    public void testConcat() throws Exception {
        TestNamenodeRetryCache.resetCall();
        String file1 = "/testNamenodeRetryCache/testConcat/file1";
        String file2 = "/testNamenodeRetryCache/testConcat/file2";
        // Two retried concat calls succeed
        concatSetup(file1, file2);
        TestNamenodeRetryCache.newCall();
        TestNamenodeRetryCache.nnRpc.concat(file1, new String[]{ file2 });
        TestNamenodeRetryCache.nnRpc.concat(file1, new String[]{ file2 });
        TestNamenodeRetryCache.nnRpc.concat(file1, new String[]{ file2 });
        // A non-retried concat request fails
        TestNamenodeRetryCache.newCall();
        try {
            // Second non-retry call should fail with an exception
            TestNamenodeRetryCache.nnRpc.concat(file1, new String[]{ file2 });
            Assert.fail("testConcat - expected exception is not thrown");
        } catch (IOException e) {
            // Expected
        }
    }

    /**
     * Tests for delete call
     */
    @Test
    public void testDelete() throws Exception {
        String dir = "/testNamenodeRetryCache/testDelete";
        // Two retried calls to create a non existent file
        TestNamenodeRetryCache.newCall();
        TestNamenodeRetryCache.nnRpc.mkdirs(dir, TestNamenodeRetryCache.perm, true);
        TestNamenodeRetryCache.newCall();
        Assert.assertTrue(TestNamenodeRetryCache.nnRpc.delete(dir, false));
        Assert.assertTrue(TestNamenodeRetryCache.nnRpc.delete(dir, false));
        Assert.assertTrue(TestNamenodeRetryCache.nnRpc.delete(dir, false));
        // non-retried call fails and gets false as return
        TestNamenodeRetryCache.newCall();
        Assert.assertFalse(TestNamenodeRetryCache.nnRpc.delete(dir, false));
    }

    /**
     * Test for createSymlink
     */
    @Test
    public void testCreateSymlink() throws Exception {
        String target = "/testNamenodeRetryCache/testCreateSymlink/target";
        // Two retried symlink calls succeed
        TestNamenodeRetryCache.newCall();
        TestNamenodeRetryCache.nnRpc.createSymlink(target, "/a/b", TestNamenodeRetryCache.perm, true);
        TestNamenodeRetryCache.nnRpc.createSymlink(target, "/a/b", TestNamenodeRetryCache.perm, true);
        TestNamenodeRetryCache.nnRpc.createSymlink(target, "/a/b", TestNamenodeRetryCache.perm, true);
        // non-retried call fails
        TestNamenodeRetryCache.newCall();
        try {
            // Second non-retry call should fail with an exception
            TestNamenodeRetryCache.nnRpc.createSymlink(target, "/a/b", TestNamenodeRetryCache.perm, true);
            Assert.fail("testCreateSymlink - expected exception is not thrown");
        } catch (IOException e) {
            // Expected
        }
    }

    /**
     * Test for create file
     */
    @Test
    public void testCreate() throws Exception {
        String src = "/testNamenodeRetryCache/testCreate/file";
        // Two retried calls succeed
        TestNamenodeRetryCache.newCall();
        HdfsFileStatus status = TestNamenodeRetryCache.nnRpc.create(src, TestNamenodeRetryCache.perm, "holder", new org.apache.hadoop.io.EnumSetWritable<org.apache.hadoop.fs.CreateFlag>(EnumSet.of(CREATE)), true, ((short) (1)), TestNamenodeRetryCache.BlockSize, null, null, null);
        Assert.assertEquals(status, TestNamenodeRetryCache.nnRpc.create(src, TestNamenodeRetryCache.perm, "holder", new org.apache.hadoop.io.EnumSetWritable<org.apache.hadoop.fs.CreateFlag>(EnumSet.of(CREATE)), true, ((short) (1)), TestNamenodeRetryCache.BlockSize, null, null, null));
        Assert.assertEquals(status, TestNamenodeRetryCache.nnRpc.create(src, TestNamenodeRetryCache.perm, "holder", new org.apache.hadoop.io.EnumSetWritable<org.apache.hadoop.fs.CreateFlag>(EnumSet.of(CREATE)), true, ((short) (1)), TestNamenodeRetryCache.BlockSize, null, null, null));
        // A non-retried call fails
        TestNamenodeRetryCache.newCall();
        try {
            TestNamenodeRetryCache.nnRpc.create(src, TestNamenodeRetryCache.perm, "holder", new org.apache.hadoop.io.EnumSetWritable<org.apache.hadoop.fs.CreateFlag>(EnumSet.of(CREATE)), true, ((short) (1)), TestNamenodeRetryCache.BlockSize, null, null, null);
            Assert.fail("testCreate - expected exception is not thrown");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * Test for rename1
     */
    @Test
    public void testAppend() throws Exception {
        String src = "/testNamenodeRetryCache/testAppend/src";
        TestNamenodeRetryCache.resetCall();
        // Create a file with partial block
        DFSTestUtil.createFile(TestNamenodeRetryCache.filesystem, new Path(src), 128, ((short) (1)), 0L);
        // Retried append requests succeed
        TestNamenodeRetryCache.newCall();
        LastBlockWithStatus b = TestNamenodeRetryCache.nnRpc.append(src, "holder", new org.apache.hadoop.io.EnumSetWritable(EnumSet.of(APPEND)));
        Assert.assertEquals(b, TestNamenodeRetryCache.nnRpc.append(src, "holder", new org.apache.hadoop.io.EnumSetWritable(EnumSet.of(APPEND))));
        Assert.assertEquals(b, TestNamenodeRetryCache.nnRpc.append(src, "holder", new org.apache.hadoop.io.EnumSetWritable(EnumSet.of(APPEND))));
        // non-retried call fails
        TestNamenodeRetryCache.newCall();
        try {
            TestNamenodeRetryCache.nnRpc.append(src, "holder", new org.apache.hadoop.io.EnumSetWritable(EnumSet.of(APPEND)));
            Assert.fail("testAppend - expected exception is not thrown");
        } catch (Exception e) {
            // Expected
        }
    }

    /**
     * Test for rename1
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testRename1() throws Exception {
        String src = "/testNamenodeRetryCache/testRename1/src";
        String target = "/testNamenodeRetryCache/testRename1/target";
        TestNamenodeRetryCache.resetCall();
        TestNamenodeRetryCache.nnRpc.mkdirs(src, TestNamenodeRetryCache.perm, true);
        // Retried renames succeed
        TestNamenodeRetryCache.newCall();
        Assert.assertTrue(TestNamenodeRetryCache.nnRpc.rename(src, target));
        Assert.assertTrue(TestNamenodeRetryCache.nnRpc.rename(src, target));
        Assert.assertTrue(TestNamenodeRetryCache.nnRpc.rename(src, target));
        // A non-retried request fails
        TestNamenodeRetryCache.newCall();
        Assert.assertFalse(TestNamenodeRetryCache.nnRpc.rename(src, target));
    }

    /**
     * Test for rename2
     */
    @Test
    public void testRename2() throws Exception {
        String src = "/testNamenodeRetryCache/testRename2/src";
        String target = "/testNamenodeRetryCache/testRename2/target";
        TestNamenodeRetryCache.resetCall();
        TestNamenodeRetryCache.nnRpc.mkdirs(src, TestNamenodeRetryCache.perm, true);
        // Retried renames succeed
        TestNamenodeRetryCache.newCall();
        TestNamenodeRetryCache.nnRpc.rename2(src, target, NONE);
        TestNamenodeRetryCache.nnRpc.rename2(src, target, NONE);
        TestNamenodeRetryCache.nnRpc.rename2(src, target, NONE);
        // A non-retried request fails
        TestNamenodeRetryCache.newCall();
        try {
            TestNamenodeRetryCache.nnRpc.rename2(src, target, NONE);
            Assert.fail("testRename 2 expected exception is not thrown");
        } catch (IOException e) {
            // expected
        }
    }

    /**
     * Make sure a retry call does not hang because of the exception thrown in the
     * first call.
     */
    @Test(timeout = 60000)
    public void testUpdatePipelineWithFailOver() throws Exception {
        TestNamenodeRetryCache.cluster.shutdown();
        TestNamenodeRetryCache.nnRpc = null;
        TestNamenodeRetryCache.filesystem = null;
        TestNamenodeRetryCache.cluster = new MiniDFSCluster.Builder(TestNamenodeRetryCache.conf).nnTopology(MiniDFSNNTopology.simpleHATopology()).numDataNodes(1).build();
        TestNamenodeRetryCache.cluster.waitActive();
        NamenodeProtocols ns0 = TestNamenodeRetryCache.cluster.getNameNodeRpc(0);
        ExtendedBlock oldBlock = new ExtendedBlock();
        ExtendedBlock newBlock = new ExtendedBlock();
        DatanodeID[] newNodes = new DatanodeID[2];
        String[] newStorages = new String[2];
        TestNamenodeRetryCache.newCall();
        try {
            ns0.updatePipeline("testClient", oldBlock, newBlock, newNodes, newStorages);
            Assert.fail("Expect StandbyException from the updatePipeline call");
        } catch (StandbyException e) {
            // expected, since in the beginning both nn are in standby state
            GenericTestUtils.assertExceptionContains(STANDBY.toString(), e);
        }
        TestNamenodeRetryCache.cluster.transitionToActive(0);
        try {
            ns0.updatePipeline("testClient", oldBlock, newBlock, newNodes, newStorages);
        } catch (IOException e) {
            // ignore call should not hang.
        }
    }

    /**
     * Test for crateSnapshot
     */
    @Test
    public void testSnapshotMethods() throws Exception {
        String dir = "/testNamenodeRetryCache/testCreateSnapshot/src";
        TestNamenodeRetryCache.resetCall();
        TestNamenodeRetryCache.nnRpc.mkdirs(dir, TestNamenodeRetryCache.perm, true);
        TestNamenodeRetryCache.nnRpc.allowSnapshot(dir);
        // Test retry of create snapshot
        TestNamenodeRetryCache.newCall();
        String name = TestNamenodeRetryCache.nnRpc.createSnapshot(dir, "snap1");
        Assert.assertEquals(name, TestNamenodeRetryCache.nnRpc.createSnapshot(dir, "snap1"));
        Assert.assertEquals(name, TestNamenodeRetryCache.nnRpc.createSnapshot(dir, "snap1"));
        Assert.assertEquals(name, TestNamenodeRetryCache.nnRpc.createSnapshot(dir, "snap1"));
        // Non retried calls should fail
        TestNamenodeRetryCache.newCall();
        try {
            TestNamenodeRetryCache.nnRpc.createSnapshot(dir, "snap1");
            Assert.fail("testSnapshotMethods expected exception is not thrown");
        } catch (IOException e) {
            // exptected
        }
        // Test retry of rename snapshot
        TestNamenodeRetryCache.newCall();
        TestNamenodeRetryCache.nnRpc.renameSnapshot(dir, "snap1", "snap2");
        TestNamenodeRetryCache.nnRpc.renameSnapshot(dir, "snap1", "snap2");
        TestNamenodeRetryCache.nnRpc.renameSnapshot(dir, "snap1", "snap2");
        // Non retried calls should fail
        TestNamenodeRetryCache.newCall();
        try {
            TestNamenodeRetryCache.nnRpc.renameSnapshot(dir, "snap1", "snap2");
            Assert.fail("testSnapshotMethods expected exception is not thrown");
        } catch (IOException e) {
            // expected
        }
        // Test retry of delete snapshot
        TestNamenodeRetryCache.newCall();
        TestNamenodeRetryCache.nnRpc.deleteSnapshot(dir, "snap2");
        TestNamenodeRetryCache.nnRpc.deleteSnapshot(dir, "snap2");
        TestNamenodeRetryCache.nnRpc.deleteSnapshot(dir, "snap2");
        // Non retried calls should fail
        TestNamenodeRetryCache.newCall();
        try {
            TestNamenodeRetryCache.nnRpc.deleteSnapshot(dir, "snap2");
            Assert.fail("testSnapshotMethods expected exception is not thrown");
        } catch (IOException e) {
            // expected
        }
    }

    @Test
    public void testRetryCacheConfig() {
        // By default retry configuration should be enabled
        Configuration conf = new HdfsConfiguration();
        Assert.assertNotNull(FSNamesystem.initRetryCache(conf));
        // If retry cache is disabled, it should not be created
        conf.setBoolean(DFS_NAMENODE_ENABLE_RETRY_CACHE_KEY, false);
        Assert.assertNull(FSNamesystem.initRetryCache(conf));
    }

    /**
     * After run a set of operations, restart NN and check if the retry cache has
     * been rebuilt based on the editlog.
     */
    @Test
    public void testRetryCacheRebuild() throws Exception {
        DFSTestUtil.runOperations(TestNamenodeRetryCache.cluster, TestNamenodeRetryCache.filesystem, TestNamenodeRetryCache.conf, TestNamenodeRetryCache.BlockSize, 0);
        FSNamesystem namesystem = TestNamenodeRetryCache.cluster.getNamesystem();
        LightWeightCache<CacheEntry, CacheEntry> cacheSet = ((LightWeightCache<CacheEntry, CacheEntry>) (namesystem.getRetryCache().getCacheSet()));
        Assert.assertEquals("Retry cache size is wrong", 39, cacheSet.size());
        Map<CacheEntry, CacheEntry> oldEntries = new HashMap<CacheEntry, CacheEntry>();
        Iterator<CacheEntry> iter = cacheSet.iterator();
        while (iter.hasNext()) {
            CacheEntry entry = iter.next();
            oldEntries.put(entry, entry);
        } 
        // restart NameNode
        TestNamenodeRetryCache.cluster.restartNameNode();
        TestNamenodeRetryCache.cluster.waitActive();
        namesystem = TestNamenodeRetryCache.cluster.getNamesystem();
        // check retry cache
        Assert.assertTrue(namesystem.hasRetryCache());
        cacheSet = ((LightWeightCache<CacheEntry, CacheEntry>) (namesystem.getRetryCache().getCacheSet()));
        Assert.assertEquals("Retry cache size is wrong", 39, cacheSet.size());
        iter = cacheSet.iterator();
        while (iter.hasNext()) {
            CacheEntry entry = iter.next();
            Assert.assertTrue(oldEntries.containsKey(entry));
        } 
    }
}

