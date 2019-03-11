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
package org.apache.hadoop.hdfs.server.namenode.ha;


import CacheFlag.FORCE;
import CreateFlag.APPEND;
import CreateFlag.CREATE;
import Rename.OVERWRITE;
import SyncFlag.UPDATE_LENGTH;
import SystemErasureCodingPolicies.RS_6_3_POLICY_ID;
import java.io.FileNotFoundException;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.crypto.CryptoProtocolVersion;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.RemoteIterator;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DFSClient;
import org.apache.hadoop.hdfs.DFSOutputStream;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.client.HdfsDataOutputStream;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveEntry;
import org.apache.hadoop.hdfs.protocol.CacheDirectiveInfo;
import org.apache.hadoop.hdfs.protocol.CachePoolEntry;
import org.apache.hadoop.hdfs.protocol.CachePoolInfo;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ErasureCodingPolicy;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.LastBlockWithStatus;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlocks;
import org.apache.hadoop.hdfs.protocol.SystemErasureCodingPolicies;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockInfo;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeManager;
import org.apache.hadoop.hdfs.server.namenode.FSNamesystem;
import org.apache.hadoop.hdfs.server.namenode.INodeFile;
import org.apache.hadoop.hdfs.server.namenode.snapshot.SnapshotTestHelper;
import org.apache.hadoop.io.retry.FailoverProxyProvider;
import org.apache.hadoop.io.retry.RetryInvocationHandler;
import org.apache.hadoop.io.retry.RetryPolicy;
import org.apache.hadoop.ipc.RetryCache.CacheEntry;
import org.apache.hadoop.util.LightWeightCache;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestRetryCacheWithHA {
    private static final Logger LOG = LoggerFactory.getLogger(TestRetryCacheWithHA.class);

    private static final int BlockSize = 1024;

    private static ErasureCodingPolicy defaultEcPolicy = SystemErasureCodingPolicies.getByID(RS_6_3_POLICY_ID);

    private static final short DataNodes = ((short) (((TestRetryCacheWithHA.defaultEcPolicy.getNumDataUnits()) + (TestRetryCacheWithHA.defaultEcPolicy.getNumParityUnits())) + 1));

    private static final int CHECKTIMES = 10;

    private static final int ResponseSize = 3;

    private MiniDFSCluster cluster;

    private DistributedFileSystem dfs;

    private final Configuration conf = new HdfsConfiguration();

    /**
     * A dummy invocation handler extending RetryInvocationHandler. We can use
     * a boolean flag to control whether the method invocation succeeds or not.
     */
    private static class DummyRetryInvocationHandler extends RetryInvocationHandler<ClientProtocol> {
        static final AtomicBoolean block = new AtomicBoolean(false);

        DummyRetryInvocationHandler(FailoverProxyProvider<ClientProtocol> proxyProvider, RetryPolicy retryPolicy) {
            super(proxyProvider, retryPolicy);
        }

        @Override
        protected Object invokeMethod(Method method, Object[] args) throws Throwable {
            Object result = super.invokeMethod(method, args);
            if (TestRetryCacheWithHA.DummyRetryInvocationHandler.block.get()) {
                throw new UnknownHostException("Fake Exception");
            } else {
                return result;
            }
        }
    }

    /**
     * 1. Run a set of operations
     * 2. Trigger the NN failover
     * 3. Check the retry cache on the original standby NN
     */
    @Test(timeout = 60000)
    public void testRetryCacheOnStandbyNN() throws Exception {
        // 1. run operations
        DFSTestUtil.runOperations(cluster, dfs, conf, TestRetryCacheWithHA.BlockSize, 0);
        // check retry cache in NN1
        FSNamesystem fsn0 = cluster.getNamesystem(0);
        LightWeightCache<CacheEntry, CacheEntry> cacheSet = ((LightWeightCache<CacheEntry, CacheEntry>) (fsn0.getRetryCache().getCacheSet()));
        Assert.assertEquals("Retry cache size is wrong", 39, cacheSet.size());
        Map<CacheEntry, CacheEntry> oldEntries = new HashMap<CacheEntry, CacheEntry>();
        Iterator<CacheEntry> iter = cacheSet.iterator();
        while (iter.hasNext()) {
            CacheEntry entry = iter.next();
            oldEntries.put(entry, entry);
        } 
        // 2. Failover the current standby to active.
        cluster.getNameNode(0).getRpcServer().rollEditLog();
        cluster.getNameNode(1).getNamesystem().getEditLogTailer().doTailEdits();
        cluster.shutdownNameNode(0);
        cluster.transitionToActive(1);
        // 3. check the retry cache on the new active NN
        FSNamesystem fsn1 = cluster.getNamesystem(1);
        cacheSet = ((LightWeightCache<CacheEntry, CacheEntry>) (fsn1.getRetryCache().getCacheSet()));
        Assert.assertEquals("Retry cache size is wrong", 39, cacheSet.size());
        iter = cacheSet.iterator();
        while (iter.hasNext()) {
            CacheEntry entry = iter.next();
            Assert.assertTrue(oldEntries.containsKey(entry));
        } 
    }

    abstract class AtMostOnceOp {
        private final String name;

        final DFSClient client;

        int expectedUpdateCount = 0;

        AtMostOnceOp(String name, DFSClient client) {
            this.name = name;
            this.client = client;
        }

        abstract void prepare() throws Exception;

        abstract void invoke() throws Exception;

        abstract boolean checkNamenodeBeforeReturn() throws Exception;

        abstract Object getResult();

        int getExpectedCacheUpdateCount() {
            return expectedUpdateCount;
        }
    }

    /**
     * createSnapshot operaiton
     */
    class CreateSnapshotOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private String snapshotPath;

        private final String dir;

        private final String snapshotName;

        CreateSnapshotOp(DFSClient client, String dir, String snapshotName) {
            super("createSnapshot", client);
            this.dir = dir;
            this.snapshotName = snapshotName;
        }

        @Override
        void prepare() throws Exception {
            final Path dirPath = new Path(dir);
            if (!(dfs.exists(dirPath))) {
                dfs.mkdirs(dirPath);
                dfs.allowSnapshot(dirPath);
            }
        }

        @Override
        void invoke() throws Exception {
            this.snapshotPath = client.createSnapshot(dir, snapshotName);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            final Path sPath = SnapshotTestHelper.getSnapshotRoot(new Path(dir), snapshotName);
            boolean snapshotCreated = dfs.exists(sPath);
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && (!snapshotCreated); i++) {
                Thread.sleep(1000);
                snapshotCreated = dfs.exists(sPath);
            }
            return snapshotCreated;
        }

        @Override
        Object getResult() {
            return snapshotPath;
        }
    }

    /**
     * deleteSnapshot
     */
    class DeleteSnapshotOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String dir;

        private final String snapshotName;

        DeleteSnapshotOp(DFSClient client, String dir, String snapshotName) {
            super("deleteSnapshot", client);
            this.dir = dir;
            this.snapshotName = snapshotName;
        }

        @Override
        void prepare() throws Exception {
            final Path dirPath = new Path(dir);
            if (!(dfs.exists(dirPath))) {
                dfs.mkdirs(dirPath);
            }
            Path sPath = SnapshotTestHelper.getSnapshotRoot(dirPath, snapshotName);
            if (!(dfs.exists(sPath))) {
                dfs.allowSnapshot(dirPath);
                dfs.createSnapshot(dirPath, snapshotName);
            }
        }

        @Override
        void invoke() throws Exception {
            client.deleteSnapshot(dir, snapshotName);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            final Path sPath = SnapshotTestHelper.getSnapshotRoot(new Path(dir), snapshotName);
            boolean snapshotNotDeleted = dfs.exists(sPath);
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && snapshotNotDeleted; i++) {
                Thread.sleep(1000);
                snapshotNotDeleted = dfs.exists(sPath);
            }
            return !snapshotNotDeleted;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * renameSnapshot
     */
    class RenameSnapshotOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String dir;

        private final String oldName;

        private final String newName;

        RenameSnapshotOp(DFSClient client, String dir, String oldName, String newName) {
            super("renameSnapshot", client);
            this.dir = dir;
            this.oldName = oldName;
            this.newName = newName;
        }

        @Override
        void prepare() throws Exception {
            final Path dirPath = new Path(dir);
            if (!(dfs.exists(dirPath))) {
                dfs.mkdirs(dirPath);
            }
            Path sPath = SnapshotTestHelper.getSnapshotRoot(dirPath, oldName);
            if (!(dfs.exists(sPath))) {
                dfs.allowSnapshot(dirPath);
                dfs.createSnapshot(dirPath, oldName);
            }
        }

        @Override
        void invoke() throws Exception {
            client.renameSnapshot(dir, oldName, newName);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            final Path sPath = SnapshotTestHelper.getSnapshotRoot(new Path(dir), newName);
            boolean snapshotRenamed = dfs.exists(sPath);
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && (!snapshotRenamed); i++) {
                Thread.sleep(1000);
                snapshotRenamed = dfs.exists(sPath);
            }
            return snapshotRenamed;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * create file operation (without OverWrite)
     */
    class CreateOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String fileName;

        private HdfsFileStatus status;

        CreateOp(DFSClient client, String fileName) {
            super("create", client);
            this.fileName = fileName;
        }

        @Override
        void prepare() throws Exception {
            final Path filePath = new Path(fileName);
            if (dfs.exists(filePath)) {
                dfs.delete(filePath, true);
            }
            final Path fileParent = filePath.getParent();
            if (!(dfs.exists(fileParent))) {
                dfs.mkdirs(fileParent);
            }
        }

        @Override
        void invoke() throws Exception {
            EnumSet<CreateFlag> createFlag = EnumSet.of(CREATE);
            this.status = client.getNamenode().create(fileName, FsPermission.getFileDefault(), client.getClientName(), new org.apache.hadoop.io.EnumSetWritable<CreateFlag>(createFlag), false, TestRetryCacheWithHA.DataNodes, TestRetryCacheWithHA.BlockSize, new CryptoProtocolVersion[]{ CryptoProtocolVersion.ENCRYPTION_ZONES }, null, null);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            final Path filePath = new Path(fileName);
            boolean fileCreated = dfs.exists(filePath);
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && (!fileCreated); i++) {
                Thread.sleep(1000);
                fileCreated = dfs.exists(filePath);
            }
            return fileCreated;
        }

        @Override
        Object getResult() {
            return status;
        }
    }

    /**
     * append operation
     */
    class AppendOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String fileName;

        private LastBlockWithStatus lbk;

        AppendOp(DFSClient client, String fileName) {
            super("append", client);
            this.fileName = fileName;
        }

        @Override
        void prepare() throws Exception {
            final Path filePath = new Path(fileName);
            if (!(dfs.exists(filePath))) {
                DFSTestUtil.createFile(dfs, filePath, ((TestRetryCacheWithHA.BlockSize) / 2), TestRetryCacheWithHA.DataNodes, 0);
            }
        }

        @Override
        void invoke() throws Exception {
            lbk = client.getNamenode().append(fileName, client.getClientName(), new org.apache.hadoop.io.EnumSetWritable(EnumSet.of(APPEND)));
        }

        // check if the inode of the file is under construction
        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            INodeFile fileNode = cluster.getNameNode(0).getNamesystem().getFSDirectory().getINode4Write(fileName).asFile();
            boolean fileIsUC = fileNode.isUnderConstruction();
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && (!fileIsUC); i++) {
                Thread.sleep(1000);
                fileNode = cluster.getNameNode(0).getNamesystem().getFSDirectory().getINode4Write(fileName).asFile();
                fileIsUC = fileNode.isUnderConstruction();
            }
            return fileIsUC;
        }

        @Override
        Object getResult() {
            return lbk;
        }
    }

    /**
     * rename
     */
    class RenameOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String oldName;

        private final String newName;

        private boolean renamed;

        RenameOp(DFSClient client, String oldName, String newName) {
            super("rename", client);
            this.oldName = oldName;
            this.newName = newName;
        }

        @Override
        void prepare() throws Exception {
            final Path filePath = new Path(oldName);
            if (!(dfs.exists(filePath))) {
                DFSTestUtil.createFile(dfs, filePath, TestRetryCacheWithHA.BlockSize, TestRetryCacheWithHA.DataNodes, 0);
            }
        }

        @SuppressWarnings("deprecation")
        @Override
        void invoke() throws Exception {
            this.renamed = client.rename(oldName, newName);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            Path targetPath = new Path(newName);
            boolean renamed = dfs.exists(targetPath);
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && (!renamed); i++) {
                Thread.sleep(1000);
                renamed = dfs.exists(targetPath);
            }
            return renamed;
        }

        @Override
        Object getResult() {
            return new Boolean(renamed);
        }
    }

    /**
     * rename2
     */
    class Rename2Op extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String oldName;

        private final String newName;

        Rename2Op(DFSClient client, String oldName, String newName) {
            super("rename2", client);
            this.oldName = oldName;
            this.newName = newName;
        }

        @Override
        void prepare() throws Exception {
            final Path filePath = new Path(oldName);
            if (!(dfs.exists(filePath))) {
                DFSTestUtil.createFile(dfs, filePath, TestRetryCacheWithHA.BlockSize, TestRetryCacheWithHA.DataNodes, 0);
            }
        }

        @Override
        void invoke() throws Exception {
            client.rename(oldName, newName, OVERWRITE);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            Path targetPath = new Path(newName);
            boolean renamed = dfs.exists(targetPath);
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && (!renamed); i++) {
                Thread.sleep(1000);
                renamed = dfs.exists(targetPath);
            }
            return renamed;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * concat
     */
    class ConcatOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String target;

        private final String[] srcs;

        private final Path[] srcPaths;

        ConcatOp(DFSClient client, Path target, int numSrc) {
            super("concat", client);
            this.target = target.toString();
            this.srcs = new String[numSrc];
            this.srcPaths = new Path[numSrc];
            Path parent = target.getParent();
            for (int i = 0; i < numSrc; i++) {
                srcPaths[i] = new Path(parent, ("srcfile" + i));
                srcs[i] = srcPaths[i].toString();
            }
        }

        @Override
        void prepare() throws Exception {
            final Path targetPath = new Path(target);
            DFSTestUtil.createFile(dfs, targetPath, TestRetryCacheWithHA.BlockSize, TestRetryCacheWithHA.DataNodes, 0);
            for (int i = 0; i < (srcPaths.length); i++) {
                DFSTestUtil.createFile(dfs, srcPaths[i], TestRetryCacheWithHA.BlockSize, TestRetryCacheWithHA.DataNodes, 0);
            }
            Assert.assertEquals(TestRetryCacheWithHA.BlockSize, dfs.getFileStatus(targetPath).getLen());
        }

        @Override
        void invoke() throws Exception {
            client.concat(target, srcs);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            Path targetPath = new Path(target);
            boolean done = (dfs.getFileStatus(targetPath).getLen()) == ((TestRetryCacheWithHA.BlockSize) * ((srcs.length) + 1));
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && (!done); i++) {
                Thread.sleep(1000);
                done = (dfs.getFileStatus(targetPath).getLen()) == ((TestRetryCacheWithHA.BlockSize) * ((srcs.length) + 1));
            }
            return done;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * delete
     */
    class DeleteOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String target;

        private boolean deleted;

        DeleteOp(DFSClient client, String target) {
            super("delete", client);
            this.target = target;
        }

        @Override
        void prepare() throws Exception {
            Path p = new Path(target);
            if (!(dfs.exists(p))) {
                (expectedUpdateCount)++;
                DFSTestUtil.createFile(dfs, p, TestRetryCacheWithHA.BlockSize, TestRetryCacheWithHA.DataNodes, 0);
            }
        }

        @Override
        void invoke() throws Exception {
            (expectedUpdateCount)++;
            deleted = client.delete(target, true);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            Path targetPath = new Path(target);
            boolean del = !(dfs.exists(targetPath));
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && (!del); i++) {
                Thread.sleep(1000);
                del = !(dfs.exists(targetPath));
            }
            return del;
        }

        @Override
        Object getResult() {
            return new Boolean(deleted);
        }
    }

    /**
     * createSymlink
     */
    class CreateSymlinkOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String target;

        private final String link;

        public CreateSymlinkOp(DFSClient client, String target, String link) {
            super("createSymlink", client);
            this.target = target;
            this.link = link;
        }

        @Override
        void prepare() throws Exception {
            Path p = new Path(target);
            if (!(dfs.exists(p))) {
                (expectedUpdateCount)++;
                DFSTestUtil.createFile(dfs, p, TestRetryCacheWithHA.BlockSize, TestRetryCacheWithHA.DataNodes, 0);
            }
        }

        @Override
        void invoke() throws Exception {
            (expectedUpdateCount)++;
            client.createSymlink(target, link, false);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            Path linkPath = new Path(link);
            FileStatus linkStatus = null;
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && (linkStatus == null); i++) {
                try {
                    linkStatus = dfs.getFileLinkStatus(linkPath);
                } catch (FileNotFoundException fnf) {
                    // Ignoring, this can be legitimate.
                    Thread.sleep(1000);
                }
            }
            return linkStatus != null;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * updatePipeline
     */
    class UpdatePipelineOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String file;

        private ExtendedBlock oldBlock;

        private ExtendedBlock newBlock;

        private DatanodeInfo[] nodes;

        private FSDataOutputStream out;

        public UpdatePipelineOp(DFSClient client, String file) {
            super("updatePipeline", client);
            this.file = file;
        }

        @Override
        void prepare() throws Exception {
            final Path filePath = new Path(file);
            DFSTestUtil.createFile(dfs, filePath, TestRetryCacheWithHA.BlockSize, TestRetryCacheWithHA.DataNodes, 0);
            // append to the file and leave the last block under construction
            out = this.client.append(file, TestRetryCacheWithHA.BlockSize, EnumSet.of(APPEND), null, null);
            byte[] appendContent = new byte[100];
            new Random().nextBytes(appendContent);
            out.write(appendContent);
            ((HdfsDataOutputStream) (out)).hsync(EnumSet.of(UPDATE_LENGTH));
            LocatedBlocks blks = dfs.getClient().getLocatedBlocks(file, ((TestRetryCacheWithHA.BlockSize) + 1));
            Assert.assertEquals(1, blks.getLocatedBlocks().size());
            nodes = blks.get(0).getLocations();
            oldBlock = blks.get(0).getBlock();
            LocatedBlock newLbk = client.getNamenode().updateBlockForPipeline(oldBlock, client.getClientName());
            newBlock = new ExtendedBlock(oldBlock.getBlockPoolId(), oldBlock.getBlockId(), oldBlock.getNumBytes(), newLbk.getBlock().getGenerationStamp());
        }

        @Override
        void invoke() throws Exception {
            DatanodeInfo[] newNodes = new DatanodeInfo[2];
            newNodes[0] = nodes[0];
            newNodes[1] = nodes[1];
            final DatanodeManager dm = cluster.getNamesystem(0).getBlockManager().getDatanodeManager();
            final String storageID1 = dm.getDatanode(newNodes[0]).getStorageInfos()[0].getStorageID();
            final String storageID2 = dm.getDatanode(newNodes[1]).getStorageInfos()[0].getStorageID();
            String[] storageIDs = new String[]{ storageID1, storageID2 };
            client.getNamenode().updatePipeline(client.getClientName(), oldBlock, newBlock, newNodes, storageIDs);
            // close can fail if the out.close() commit the block after block received
            // notifications from Datanode.
            // Since datanodes and output stream have still old genstamps, these
            // blocks will be marked as corrupt after HDFS-5723 if RECEIVED
            // notifications reaches namenode first and close() will fail.
            DFSTestUtil.abortStream(((DFSOutputStream) (out.getWrappedStream())));
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            INodeFile fileNode = cluster.getNamesystem(0).getFSDirectory().getINode4Write(file).asFile();
            BlockInfo blkUC = fileNode.getBlocks()[1];
            int datanodeNum = blkUC.getUnderConstructionFeature().getExpectedStorageLocations().length;
            for (int i = 0; (i < (TestRetryCacheWithHA.CHECKTIMES)) && (datanodeNum != 2); i++) {
                Thread.sleep(1000);
                datanodeNum = blkUC.getUnderConstructionFeature().getExpectedStorageLocations().length;
            }
            return datanodeNum == 2;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * addCacheDirective
     */
    class AddCacheDirectiveInfoOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final CacheDirectiveInfo directive;

        private Long result;

        AddCacheDirectiveInfoOp(DFSClient client, CacheDirectiveInfo directive) {
            super("addCacheDirective", client);
            this.directive = directive;
        }

        @Override
        void prepare() throws Exception {
            (expectedUpdateCount)++;
            dfs.addCachePool(new CachePoolInfo(directive.getPool()));
        }

        @Override
        void invoke() throws Exception {
            (expectedUpdateCount)++;
            result = client.addCacheDirective(directive, EnumSet.of(FORCE));
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            for (int i = 0; i < (TestRetryCacheWithHA.CHECKTIMES); i++) {
                RemoteIterator<CacheDirectiveEntry> iter = dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().setPool(directive.getPool()).setPath(directive.getPath()).build());
                if (iter.hasNext()) {
                    return true;
                }
                Thread.sleep(1000);
            }
            return false;
        }

        @Override
        Object getResult() {
            return result;
        }
    }

    /**
     * modifyCacheDirective
     */
    class ModifyCacheDirectiveInfoOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final CacheDirectiveInfo directive;

        private final short newReplication;

        private long id;

        ModifyCacheDirectiveInfoOp(DFSClient client, CacheDirectiveInfo directive, short newReplication) {
            super("modifyCacheDirective", client);
            this.directive = directive;
            this.newReplication = newReplication;
        }

        @Override
        void prepare() throws Exception {
            (expectedUpdateCount)++;
            dfs.addCachePool(new CachePoolInfo(directive.getPool()));
            (expectedUpdateCount)++;
            id = client.addCacheDirective(directive, EnumSet.of(FORCE));
        }

        @Override
        void invoke() throws Exception {
            (expectedUpdateCount)++;
            client.modifyCacheDirective(new CacheDirectiveInfo.Builder().setId(id).setReplication(newReplication).build(), EnumSet.of(FORCE));
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            for (int i = 0; i < (TestRetryCacheWithHA.CHECKTIMES); i++) {
                RemoteIterator<CacheDirectiveEntry> iter = dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().setPool(directive.getPool()).setPath(directive.getPath()).build());
                while (iter.hasNext()) {
                    CacheDirectiveInfo result = iter.next().getInfo();
                    if (((result.getId()) == (id)) && ((result.getReplication().shortValue()) == (newReplication))) {
                        return true;
                    }
                } 
                Thread.sleep(1000);
            }
            return false;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * removeCacheDirective
     */
    class RemoveCacheDirectiveInfoOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final CacheDirectiveInfo directive;

        private long id;

        RemoveCacheDirectiveInfoOp(DFSClient client, String pool, String path) {
            super("removeCacheDirective", client);
            this.directive = new CacheDirectiveInfo.Builder().setPool(pool).setPath(new Path(path)).build();
        }

        @Override
        void prepare() throws Exception {
            (expectedUpdateCount)++;
            dfs.addCachePool(new CachePoolInfo(directive.getPool()));
            (expectedUpdateCount)++;
            id = dfs.addCacheDirective(directive, EnumSet.of(FORCE));
        }

        @Override
        void invoke() throws Exception {
            (expectedUpdateCount)++;
            client.removeCacheDirective(id);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            for (int i = 0; i < (TestRetryCacheWithHA.CHECKTIMES); i++) {
                RemoteIterator<CacheDirectiveEntry> iter = dfs.listCacheDirectives(new CacheDirectiveInfo.Builder().setPool(directive.getPool()).setPath(directive.getPath()).build());
                if (!(iter.hasNext())) {
                    return true;
                }
                Thread.sleep(1000);
            }
            return false;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * addCachePool
     */
    class AddCachePoolOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String pool;

        AddCachePoolOp(DFSClient client, String pool) {
            super("addCachePool", client);
            this.pool = pool;
        }

        @Override
        void prepare() throws Exception {
        }

        @Override
        void invoke() throws Exception {
            (expectedUpdateCount)++;
            client.addCachePool(new CachePoolInfo(pool));
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            for (int i = 0; i < (TestRetryCacheWithHA.CHECKTIMES); i++) {
                RemoteIterator<CachePoolEntry> iter = dfs.listCachePools();
                if (iter.hasNext()) {
                    return true;
                }
                Thread.sleep(1000);
            }
            return false;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * modifyCachePool
     */
    class ModifyCachePoolOp extends TestRetryCacheWithHA.AtMostOnceOp {
        final String pool;

        ModifyCachePoolOp(DFSClient client, String pool) {
            super("modifyCachePool", client);
            this.pool = pool;
        }

        @Override
        void prepare() throws Exception {
            (expectedUpdateCount)++;
            client.addCachePool(new CachePoolInfo(pool).setLimit(10L));
        }

        @Override
        void invoke() throws Exception {
            (expectedUpdateCount)++;
            client.modifyCachePool(new CachePoolInfo(pool).setLimit(99L));
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            for (int i = 0; i < (TestRetryCacheWithHA.CHECKTIMES); i++) {
                RemoteIterator<CachePoolEntry> iter = dfs.listCachePools();
                if ((iter.hasNext()) && ((iter.next().getInfo().getLimit()) == 99)) {
                    return true;
                }
                Thread.sleep(1000);
            }
            return false;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * removeCachePool
     */
    class RemoveCachePoolOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String pool;

        RemoveCachePoolOp(DFSClient client, String pool) {
            super("removeCachePool", client);
            this.pool = pool;
        }

        @Override
        void prepare() throws Exception {
            (expectedUpdateCount)++;
            client.addCachePool(new CachePoolInfo(pool));
        }

        @Override
        void invoke() throws Exception {
            (expectedUpdateCount)++;
            client.removeCachePool(pool);
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            for (int i = 0; i < (TestRetryCacheWithHA.CHECKTIMES); i++) {
                RemoteIterator<CachePoolEntry> iter = dfs.listCachePools();
                if (!(iter.hasNext())) {
                    return true;
                }
                Thread.sleep(1000);
            }
            return false;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * setXAttr
     */
    class SetXAttrOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String src;

        SetXAttrOp(DFSClient client, String src) {
            super("setXAttr", client);
            this.src = src;
        }

        @Override
        void prepare() throws Exception {
            Path p = new Path(src);
            if (!(dfs.exists(p))) {
                (expectedUpdateCount)++;
                DFSTestUtil.createFile(dfs, p, TestRetryCacheWithHA.BlockSize, TestRetryCacheWithHA.DataNodes, 0);
            }
        }

        @Override
        void invoke() throws Exception {
            (expectedUpdateCount)++;
            client.setXAttr(src, "user.key", "value".getBytes(), EnumSet.of(XAttrSetFlag.CREATE));
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            for (int i = 0; i < (TestRetryCacheWithHA.CHECKTIMES); i++) {
                Map<String, byte[]> iter = dfs.getXAttrs(new Path(src));
                Set<String> keySet = iter.keySet();
                if (keySet.contains("user.key")) {
                    return true;
                }
                Thread.sleep(1000);
            }
            return false;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    /**
     * removeXAttr
     */
    class RemoveXAttrOp extends TestRetryCacheWithHA.AtMostOnceOp {
        private final String src;

        RemoveXAttrOp(DFSClient client, String src) {
            super("removeXAttr", client);
            this.src = src;
        }

        @Override
        void prepare() throws Exception {
            Path p = new Path(src);
            if (!(dfs.exists(p))) {
                (expectedUpdateCount)++;
                DFSTestUtil.createFile(dfs, p, TestRetryCacheWithHA.BlockSize, TestRetryCacheWithHA.DataNodes, 0);
                (expectedUpdateCount)++;
                client.setXAttr(src, "user.key", "value".getBytes(), EnumSet.of(XAttrSetFlag.CREATE));
            }
        }

        @Override
        void invoke() throws Exception {
            (expectedUpdateCount)++;
            client.removeXAttr(src, "user.key");
        }

        @Override
        boolean checkNamenodeBeforeReturn() throws Exception {
            for (int i = 0; i < (TestRetryCacheWithHA.CHECKTIMES); i++) {
                Map<String, byte[]> iter = dfs.getXAttrs(new Path(src));
                Set<String> keySet = iter.keySet();
                if (!(keySet.contains("user.key"))) {
                    return true;
                }
                Thread.sleep(1000);
            }
            return false;
        }

        @Override
        Object getResult() {
            return null;
        }
    }

    @Test(timeout = 60000)
    public void testCreateSnapshot() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.CreateSnapshotOp(client, "/test", "s1");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testDeleteSnapshot() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.DeleteSnapshotOp(client, "/test", "s1");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testRenameSnapshot() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.RenameSnapshotOp(client, "/test", "s1", "s2");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testCreate() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.CreateOp(client, "/testfile");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testAppend() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.AppendOp(client, "/testfile");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testRename() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.RenameOp(client, "/file1", "/file2");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testRename2() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.Rename2Op(client, "/file1", "/file2");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testConcat() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.ConcatOp(client, new Path("/test/file"), 5);
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testDelete() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.DeleteOp(client, "/testfile");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testCreateSymlink() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.CreateSymlinkOp(client, "/testfile", "/testlink");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testUpdatePipeline() throws Exception {
        final DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.UpdatePipelineOp(client, "/testfile");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testAddCacheDirectiveInfo() throws Exception {
        DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.AddCacheDirectiveInfoOp(client, new CacheDirectiveInfo.Builder().setPool("pool").setPath(new Path("/path")).build());
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testModifyCacheDirectiveInfo() throws Exception {
        DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.ModifyCacheDirectiveInfoOp(client, new CacheDirectiveInfo.Builder().setPool("pool").setPath(new Path("/path")).setReplication(((short) (1))).build(), ((short) (555)));
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testRemoveCacheDescriptor() throws Exception {
        DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.RemoveCacheDirectiveInfoOp(client, "pool", "/path");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testAddCachePool() throws Exception {
        DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.AddCachePoolOp(client, "pool");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testModifyCachePool() throws Exception {
        DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.ModifyCachePoolOp(client, "pool");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testRemoveCachePool() throws Exception {
        DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.RemoveCachePoolOp(client, "pool");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testSetXAttr() throws Exception {
        DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.SetXAttrOp(client, "/setxattr");
        testClientRetryWithFailover(op);
    }

    @Test(timeout = 60000)
    public void testRemoveXAttr() throws Exception {
        DFSClient client = genClientWithDummyHandler();
        TestRetryCacheWithHA.AtMostOnceOp op = new TestRetryCacheWithHA.RemoveXAttrOp(client, "/removexattr");
        testClientRetryWithFailover(op);
    }

    /**
     * Add a list of cache pools, list cache pools,
     * switch active NN, and list cache pools again.
     */
    @Test(timeout = 60000)
    public void testListCachePools() throws Exception {
        final int poolCount = 7;
        HashSet<String> poolNames = new HashSet<String>(poolCount);
        for (int i = 0; i < poolCount; i++) {
            String poolName = "testListCachePools-" + i;
            dfs.addCachePool(new CachePoolInfo(poolName));
            poolNames.add(poolName);
        }
        listCachePools(poolNames, 0);
        cluster.transitionToStandby(0);
        cluster.transitionToActive(1);
        cluster.waitActive(1);
        listCachePools(poolNames, 1);
    }

    /**
     * Add a list of cache directives, list cache directives,
     * switch active NN, and list cache directives again.
     */
    @Test(timeout = 60000)
    public void testListCacheDirectives() throws Exception {
        final int poolCount = 7;
        HashSet<String> poolNames = new HashSet<String>(poolCount);
        Path path = new Path("/p");
        for (int i = 0; i < poolCount; i++) {
            String poolName = "testListCacheDirectives-" + i;
            CacheDirectiveInfo directiveInfo = new CacheDirectiveInfo.Builder().setPool(poolName).setPath(path).build();
            dfs.addCachePool(new CachePoolInfo(poolName));
            dfs.addCacheDirective(directiveInfo, EnumSet.of(FORCE));
            poolNames.add(poolName);
        }
        listCacheDirectives(poolNames, 0);
        cluster.transitionToStandby(0);
        cluster.transitionToActive(1);
        cluster.waitActive(1);
        listCacheDirectives(poolNames, 1);
    }
}

