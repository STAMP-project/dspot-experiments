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
package org.apache.hadoop.hdfs.server.namenode.ha;


import SafeModeAction.SAFEMODE_ENTER;
import SafeModeAction.SAFEMODE_LEAVE;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.ha.ServiceFailedException;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocol.Block;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.LocatedBlock;
import org.apache.hadoop.hdfs.qjournal.MiniQJMHACluster;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.namenode.NameNodeAdapter;
import org.apache.hadoop.hdfs.server.namenode.TestFsck;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Test main functionality of ObserverNode.
 */
public class TestObserverNode {
    public static final Logger LOG = LoggerFactory.getLogger(TestObserverNode.class.getName());

    private static Configuration conf;

    private static MiniQJMHACluster qjmhaCluster;

    private static MiniDFSCluster dfsCluster;

    private static DistributedFileSystem dfs;

    private final Path testPath = new Path("/TestObserverNode");

    @Test
    public void testNoActiveToObserver() throws Exception {
        try {
            TestObserverNode.dfsCluster.transitionToObserver(0);
        } catch (ServiceFailedException e) {
            return;
        }
        Assert.fail("active cannot be transitioned to observer");
    }

    @Test
    public void testNoObserverToActive() throws Exception {
        try {
            TestObserverNode.dfsCluster.transitionToActive(2);
        } catch (ServiceFailedException e) {
            return;
        }
        Assert.fail("observer cannot be transitioned to active");
    }

    @Test
    public void testSimpleRead() throws Exception {
        Path testPath2 = new Path(testPath, "test2");
        TestObserverNode.dfs.mkdir(testPath, FsPermission.getDefault());
        assertSentTo(0);
        TestObserverNode.dfsCluster.rollEditLogAndTail(0);
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(2);
        TestObserverNode.dfs.mkdir(testPath2, FsPermission.getDefault());
        assertSentTo(0);
    }

    @Test
    public void testFailover() throws Exception {
        Path testPath2 = new Path(testPath, "test2");
        TestObserverNode.setObserverRead(false);
        TestObserverNode.dfs.mkdir(testPath, FsPermission.getDefault());
        assertSentTo(0);
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(0);
        TestObserverNode.dfsCluster.transitionToStandby(0);
        TestObserverNode.dfsCluster.transitionToActive(1);
        TestObserverNode.dfsCluster.waitActive(1);
        TestObserverNode.dfs.mkdir(testPath2, FsPermission.getDefault());
        assertSentTo(1);
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(1);
        TestObserverNode.dfsCluster.transitionToStandby(1);
        TestObserverNode.dfsCluster.transitionToActive(0);
        TestObserverNode.dfsCluster.waitActive(0);
    }

    @Test
    public void testDoubleFailover() throws Exception {
        Path testPath2 = new Path(testPath, "test2");
        Path testPath3 = new Path(testPath, "test3");
        TestObserverNode.dfs.mkdir(testPath, FsPermission.getDefault());
        assertSentTo(0);
        TestObserverNode.dfsCluster.rollEditLogAndTail(0);
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(2);
        TestObserverNode.dfs.mkdir(testPath2, FsPermission.getDefault());
        assertSentTo(0);
        TestObserverNode.dfsCluster.transitionToStandby(0);
        TestObserverNode.dfsCluster.transitionToActive(1);
        TestObserverNode.dfsCluster.waitActive(1);
        TestObserverNode.dfsCluster.rollEditLogAndTail(1);
        TestObserverNode.dfs.getFileStatus(testPath2);
        assertSentTo(2);
        TestObserverNode.dfs.mkdir(testPath3, FsPermission.getDefault());
        assertSentTo(1);
        TestObserverNode.dfsCluster.transitionToStandby(1);
        TestObserverNode.dfsCluster.transitionToActive(0);
        TestObserverNode.dfsCluster.waitActive(0);
        TestObserverNode.dfsCluster.rollEditLogAndTail(0);
        TestObserverNode.dfs.getFileStatus(testPath3);
        assertSentTo(2);
        TestObserverNode.dfs.delete(testPath3, false);
        assertSentTo(0);
    }

    @Test
    public void testObserverShutdown() throws Exception {
        TestObserverNode.dfs.mkdir(testPath, FsPermission.getDefault());
        TestObserverNode.dfsCluster.rollEditLogAndTail(0);
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(2);
        // Shutdown the observer - requests should go to active
        TestObserverNode.dfsCluster.shutdownNameNode(2);
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(0);
        // Start the observer again - requests should go to observer
        TestObserverNode.dfsCluster.restartNameNode(2);
        TestObserverNode.dfsCluster.transitionToObserver(2);
        // The first request goes to the active because it has not refreshed yet;
        // the second will properly go to the observer
        TestObserverNode.dfs.getFileStatus(testPath);
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(2);
    }

    @Test
    public void testObserverFailOverAndShutdown() throws Exception {
        TestObserverNode.dfs.mkdir(testPath, FsPermission.getDefault());
        TestObserverNode.dfsCluster.rollEditLogAndTail(0);
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(2);
        TestObserverNode.dfsCluster.transitionToStandby(0);
        TestObserverNode.dfsCluster.transitionToActive(1);
        TestObserverNode.dfsCluster.waitActive(1);
        // Shutdown the observer - requests should go to active
        TestObserverNode.dfsCluster.shutdownNameNode(2);
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(1);
        // Start the observer again - requests should go to observer
        TestObserverNode.dfsCluster.restartNameNode(2);
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(1);
        TestObserverNode.dfsCluster.transitionToObserver(2);
        TestObserverNode.dfs.getFileStatus(testPath);
        // The first request goes to the active because it has not refreshed yet;
        // the second will properly go to the observer
        TestObserverNode.dfs.getFileStatus(testPath);
        assertSentTo(2);
        TestObserverNode.dfsCluster.transitionToStandby(1);
        TestObserverNode.dfsCluster.transitionToActive(0);
        TestObserverNode.dfsCluster.waitActive(0);
    }

    @Test
    public void testBootstrap() throws Exception {
        for (URI u : TestObserverNode.dfsCluster.getNameDirs(2)) {
            File dir = new File(u.getPath());
            Assert.assertTrue(FileUtil.fullyDelete(dir));
        }
        int rc = BootstrapStandby.run(new String[]{ "-nonInteractive" }, TestObserverNode.dfsCluster.getConfiguration(2));
        Assert.assertEquals(0, rc);
    }

    /**
     * Test the case where Observer should throw RetriableException, just like
     * active NN, for certain open() calls where block locations are not
     * available. See HDFS-13898 for details.
     */
    @Test
    public void testObserverNodeSafeModeWithBlockLocations() throws Exception {
        // Create a new file - the request should go to active.
        TestObserverNode.dfs.create(testPath, ((short) (1))).close();
        assertSentTo(0);
        TestObserverNode.dfsCluster.rollEditLogAndTail(0);
        TestObserverNode.dfs.open(testPath).close();
        assertSentTo(2);
        // Set observer to safe mode.
        TestObserverNode.dfsCluster.getFileSystem(2).setSafeMode(SAFEMODE_ENTER);
        // Mock block manager for observer to generate some fake blocks which
        // will trigger the (retriable) safe mode exception.
        BlockManager bmSpy = NameNodeAdapter.spyOnBlockManager(TestObserverNode.dfsCluster.getNameNode(2));
        Mockito.doAnswer(( invocation) -> {
            ExtendedBlock b = new ExtendedBlock("fake-pool", new Block(12345L));
            LocatedBlock fakeBlock = new LocatedBlock(b, DatanodeInfo.EMPTY_ARRAY);
            List<LocatedBlock> fakeBlocks = new ArrayList<>();
            fakeBlocks.add(fakeBlock);
            return new org.apache.hadoop.hdfs.protocol.LocatedBlocks(0, false, fakeBlocks, null, true, null, null);
        }).when(bmSpy).createLocatedBlocks(ArgumentMatchers.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any(), ArgumentMatchers.any());
        // Open the file again - it should throw retriable exception and then
        // failover to active.
        TestObserverNode.dfs.open(testPath).close();
        assertSentTo(0);
        Mockito.reset(bmSpy);
        // Remove safe mode on observer, request should still go to it.
        TestObserverNode.dfsCluster.getFileSystem(2).setSafeMode(SAFEMODE_LEAVE);
        TestObserverNode.dfs.open(testPath).close();
        assertSentTo(2);
    }

    @Test
    public void testObserverNodeBlockMissingRetry() throws Exception {
        TestObserverNode.setObserverRead(true);
        TestObserverNode.dfs.create(testPath, ((short) (1))).close();
        assertSentTo(0);
        TestObserverNode.dfsCluster.rollEditLogAndTail(0);
        // Mock block manager for observer to generate some fake blocks which
        // will trigger the block missing exception.
        BlockManager bmSpy = NameNodeAdapter.spyOnBlockManager(TestObserverNode.dfsCluster.getNameNode(2));
        Mockito.doAnswer(( invocation) -> {
            List<LocatedBlock> fakeBlocks = new ArrayList<>();
            // Remove the datanode info for the only block so it will throw
            // BlockMissingException and retry.
            ExtendedBlock b = new ExtendedBlock("fake-pool", new Block(12345L));
            LocatedBlock fakeBlock = new LocatedBlock(b, DatanodeInfo.EMPTY_ARRAY);
            fakeBlocks.add(fakeBlock);
            return new org.apache.hadoop.hdfs.protocol.LocatedBlocks(0, false, fakeBlocks, null, true, null, null);
        }).when(bmSpy).createLocatedBlocks(Mockito.any(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyBoolean(), Mockito.any(), Mockito.any());
        TestObserverNode.dfs.open(testPath);
        assertSentTo(0);
        Mockito.reset(bmSpy);
    }

    @Test
    public void testFsckWithObserver() throws Exception {
        TestObserverNode.setObserverRead(true);
        TestObserverNode.dfs.create(testPath, ((short) (1))).close();
        assertSentTo(0);
        final String result = TestFsck.runFsck(TestObserverNode.conf, 0, true, "/");
        TestObserverNode.LOG.info(("result=" + result));
        Assert.assertTrue(result.contains("Status: HEALTHY"));
    }
}

