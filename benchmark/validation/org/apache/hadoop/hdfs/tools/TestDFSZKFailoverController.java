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
package org.apache.hadoop.hdfs.tools;


import HAServiceState.ACTIVE;
import HAServiceState.OBSERVER;
import HAServiceState.STANDBY;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.ha.ClientBaseWithFixes;
import org.apache.hadoop.ha.TestNodeFencer.AlwaysSucceedFencer;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.EditLogFileOutputStream;
import org.apache.hadoop.hdfs.server.namenode.MockNameNodeResourceChecker;
import org.apache.hadoop.test.MultithreadedTestUtil.TestContext;
import org.apache.hadoop.test.MultithreadedTestUtil.TestingThread;
import org.junit.Assert;
import org.junit.Test;


public class TestDFSZKFailoverController extends ClientBaseWithFixes {
    private Configuration conf;

    private MiniDFSCluster cluster;

    private TestContext ctx;

    private TestDFSZKFailoverController.ZKFCThread thr1;

    private TestDFSZKFailoverController.ZKFCThread thr2;

    private FileSystem fs;

    static {
        // Make tests run faster by avoiding fsync()
        EditLogFileOutputStream.setShouldSkipFsyncForTesting(true);
    }

    /**
     * Test that thread dump is captured after NN state changes.
     */
    @Test(timeout = 60000)
    public void testThreadDumpCaptureAfterNNStateChange() throws Exception {
        MockNameNodeResourceChecker mockResourceChecker = new MockNameNodeResourceChecker(conf);
        mockResourceChecker.setResourcesAvailable(false);
        cluster.getNameNode(0).getNamesystem().setNNResourceChecker(mockResourceChecker);
        waitForHAState(0, STANDBY);
        while (!(thr1.zkfc.isThreadDumpCaptured())) {
            Thread.sleep(1000);
        } 
    }

    /**
     * Test that automatic failover is triggered by shutting the
     * active NN down.
     */
    @Test(timeout = 60000)
    public void testFailoverAndBackOnNNShutdown() throws Exception {
        Path p1 = new Path("/dir1");
        Path p2 = new Path("/dir2");
        // Write some data on the first NN
        fs.mkdirs(p1);
        // Shut it down, causing automatic failover
        cluster.shutdownNameNode(0);
        // Data should still exist. Write some on the new NN
        Assert.assertTrue(fs.exists(p1));
        fs.mkdirs(p2);
        Assert.assertEquals(AlwaysSucceedFencer.getLastFencedService().getAddress(), thr1.zkfc.getLocalTarget().getAddress());
        // Start the first node back up
        cluster.restartNameNode(0);
        // This should have no effect -- the new node should be STANDBY.
        waitForHAState(0, STANDBY);
        Assert.assertTrue(fs.exists(p1));
        Assert.assertTrue(fs.exists(p2));
        // Shut down the second node, which should failback to the first
        cluster.shutdownNameNode(1);
        waitForHAState(0, ACTIVE);
        // First node should see what was written on the second node while it was down.
        Assert.assertTrue(fs.exists(p1));
        Assert.assertTrue(fs.exists(p2));
        Assert.assertEquals(AlwaysSucceedFencer.getLastFencedService().getAddress(), thr2.zkfc.getLocalTarget().getAddress());
    }

    @Test(timeout = 30000)
    public void testManualFailover() throws Exception {
        thr2.zkfc.getLocalTarget().getZKFCProxy(conf, 15000).gracefulFailover();
        waitForHAState(0, STANDBY);
        waitForHAState(1, ACTIVE);
        thr1.zkfc.getLocalTarget().getZKFCProxy(conf, 15000).gracefulFailover();
        waitForHAState(0, ACTIVE);
        waitForHAState(1, STANDBY);
    }

    @Test(timeout = 30000)
    public void testManualFailoverWithDFSHAAdmin() throws Exception {
        DFSHAAdmin tool = new DFSHAAdmin();
        tool.setConf(conf);
        Assert.assertEquals(0, tool.run(new String[]{ "-failover", "nn1", "nn2" }));
        waitForHAState(0, STANDBY);
        waitForHAState(1, ACTIVE);
        Assert.assertEquals(0, tool.run(new String[]{ "-failover", "nn2", "nn1" }));
        waitForHAState(0, ACTIVE);
        waitForHAState(1, STANDBY);
        // Answer "yes" to the prompt for --forcemanual
        InputStream inOriginial = System.in;
        System.setIn(new ByteArrayInputStream("yes\n".getBytes()));
        int result = tool.run(new String[]{ "-transitionToObserver", "-forcemanual", "nn2" });
        Assert.assertEquals(("State transition returned: " + result), 0, result);
        waitForHAState(1, OBSERVER);
        // Answer "yes" to the prompt for --forcemanual
        System.setIn(new ByteArrayInputStream("yes\n".getBytes()));
        result = tool.run(new String[]{ "-transitionToStandby", "-forcemanual", "nn2" });
        System.setIn(inOriginial);
        Assert.assertEquals(("State transition returned: " + result), 0, result);
        waitForHAState(1, STANDBY);
    }

    /**
     * Test-thread which runs a ZK Failover Controller corresponding
     * to a given NameNode in the minicluster.
     */
    private class ZKFCThread extends TestingThread {
        private final DFSZKFailoverController zkfc;

        public ZKFCThread(TestContext ctx, int idx) {
            super(ctx);
            this.zkfc = DFSZKFailoverController.create(cluster.getConfiguration(idx));
        }

        @Override
        public void doWork() throws Exception {
            try {
                Assert.assertEquals(0, zkfc.run(new String[0]));
            } catch (InterruptedException ie) {
                // Interrupted by main thread, that's OK.
            }
        }
    }
}

