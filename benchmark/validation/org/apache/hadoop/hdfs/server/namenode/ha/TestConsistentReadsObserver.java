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


import HdfsConstants.DatanodeReportType.ALL;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.qjournal.MiniQJMHACluster;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.ipc.RpcScheduler;
import org.apache.hadoop.ipc.Schedulable;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.Time;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Thread.State.WAITING;


/**
 * Test consistency of reads while accessing an ObserverNode.
 * The tests are based on traditional (non fast path) edits tailing.
 */
public class TestConsistentReadsObserver {
    public static final Logger LOG = LoggerFactory.getLogger(TestConsistentReadsObserver.class.getName());

    private static Configuration conf;

    private static MiniQJMHACluster qjmhaCluster;

    private static MiniDFSCluster dfsCluster;

    private static DistributedFileSystem dfs;

    private final Path testPath = new Path("/TestConsistentReadsObserver");

    @Test
    public void testRequeueCall() throws Exception {
        TestConsistentReadsObserver.setObserverRead(true);
        // Update the configuration just for the observer, by enabling
        // IPC backoff and using the test scheduler class, which starts to backoff
        // after certain number of calls.
        final int observerIdx = 2;
        NameNode nn = TestConsistentReadsObserver.dfsCluster.getNameNode(observerIdx);
        int port = nn.getNameNodeAddress().getPort();
        Configuration configuration = TestConsistentReadsObserver.dfsCluster.getConfiguration(observerIdx);
        String prefix = (((CommonConfigurationKeys.IPC_NAMESPACE) + ".") + port) + ".";
        configuration.set((prefix + (CommonConfigurationKeys.IPC_SCHEDULER_IMPL_KEY)), TestConsistentReadsObserver.TestRpcScheduler.class.getName());
        configuration.setBoolean((prefix + (CommonConfigurationKeys.IPC_BACKOFF_ENABLE)), true);
        TestConsistentReadsObserver.dfsCluster.restartNameNode(observerIdx);
        TestConsistentReadsObserver.dfsCluster.transitionToObserver(observerIdx);
        TestConsistentReadsObserver.dfs.create(testPath, ((short) (1))).close();
        assertSentTo(0);
        // Since we haven't tailed edit logs on the observer, it will fall behind
        // and keep re-queueing the incoming request. Eventually, RPC backoff will
        // be triggered and client should retry active NN.
        TestConsistentReadsObserver.dfs.getFileStatus(testPath);
        assertSentTo(0);
    }

    @Test
    public void testMsyncSimple() throws Exception {
        // 0 == not completed, 1 == succeeded, -1 == failed
        AtomicInteger readStatus = new AtomicInteger(0);
        // Making an uncoordinated call, which initialize the proxy
        // to Observer node.
        TestConsistentReadsObserver.dfs.getClient().getHAServiceState();
        TestConsistentReadsObserver.dfs.mkdir(testPath, FsPermission.getDefault());
        assertSentTo(0);
        Thread reader = new Thread(() -> {
            try {
                // this read will block until roll and tail edits happen.
                TestConsistentReadsObserver.dfs.getFileStatus(testPath);
                readStatus.set(1);
            } catch (IOException e) {
                e.printStackTrace();
                readStatus.set((-1));
            }
        });
        reader.start();
        // the reader is still blocking, not succeeded yet.
        Assert.assertEquals(0, readStatus.get());
        TestConsistentReadsObserver.dfsCluster.rollEditLogAndTail(0);
        // wait a while for all the change to be done
        GenericTestUtils.waitFor(() -> (readStatus.get()) != 0, 100, 10000);
        // the reader should have succeed.
        Assert.assertEquals(1, readStatus.get());
    }

    @Test
    public void testMsync() throws Exception {
        // 0 == not completed, 1 == succeeded, -1 == failed
        AtomicInteger readStatus = new AtomicInteger(0);
        Configuration conf2 = new Configuration(TestConsistentReadsObserver.conf);
        // Disable FS cache so two different DFS clients will be used.
        conf2.setBoolean("fs.hdfs.impl.disable.cache", true);
        DistributedFileSystem dfs2 = ((DistributedFileSystem) (FileSystem.get(conf2)));
        // Initialize the proxies for Observer Node.
        TestConsistentReadsObserver.dfs.getClient().getHAServiceState();
        dfs2.getClient().getHAServiceState();
        // Advance Observer's state ID so it is ahead of client's.
        TestConsistentReadsObserver.dfs.mkdir(new Path("/test"), FsPermission.getDefault());
        TestConsistentReadsObserver.dfsCluster.rollEditLogAndTail(0);
        TestConsistentReadsObserver.dfs.mkdir(testPath, FsPermission.getDefault());
        assertSentTo(0);
        Thread reader = new Thread(() -> {
            try {
                // After msync, client should have the latest state ID from active.
                // Therefore, the subsequent getFileStatus call should succeed.
                dfs2.getClient().msync();
                dfs2.getFileStatus(testPath);
                if (HATestUtil.isSentToAnyOfNameNodes(dfs2, TestConsistentReadsObserver.dfsCluster, 2)) {
                    readStatus.set(1);
                } else {
                    readStatus.set((-1));
                }
            } catch (Exception e) {
                e.printStackTrace();
                readStatus.set((-1));
            }
        });
        reader.start();
        Thread.sleep(100);
        Assert.assertEquals(0, readStatus.get());
        TestConsistentReadsObserver.dfsCluster.rollEditLogAndTail(0);
        GenericTestUtils.waitFor(() -> (readStatus.get()) != 0, 100, 10000);
        Assert.assertEquals(1, readStatus.get());
    }

    // A new client should first contact the active, before using an observer,
    // to ensure that it is up-to-date with the current state
    @Test
    public void testCallFromNewClient() throws Exception {
        // Set the order of nodes: Observer, Standby, Active
        // This is to ensure that test doesn't pass trivially because the active is
        // the first node contacted
        TestConsistentReadsObserver.dfsCluster.transitionToStandby(0);
        TestConsistentReadsObserver.dfsCluster.transitionToObserver(0);
        TestConsistentReadsObserver.dfsCluster.transitionToStandby(2);
        TestConsistentReadsObserver.dfsCluster.transitionToActive(2);
        try {
            // 0 == not completed, 1 == succeeded, -1 == failed
            AtomicInteger readStatus = new AtomicInteger(0);
            // Initialize the proxies for Observer Node.
            TestConsistentReadsObserver.dfs.getClient().getHAServiceState();
            // Advance Observer's state ID so it is ahead of client's.
            TestConsistentReadsObserver.dfs.mkdir(new Path("/test"), FsPermission.getDefault());
            TestConsistentReadsObserver.dfsCluster.getNameNode(2).getRpcServer().rollEditLog();
            TestConsistentReadsObserver.dfsCluster.getNameNode(0).getNamesystem().getEditLogTailer().doTailEdits();
            TestConsistentReadsObserver.dfs.mkdir(testPath, FsPermission.getDefault());
            assertSentTo(2);
            Configuration conf2 = new Configuration(TestConsistentReadsObserver.conf);
            // Disable FS cache so two different DFS clients will be used.
            conf2.setBoolean("fs.hdfs.impl.disable.cache", true);
            DistributedFileSystem dfs2 = ((DistributedFileSystem) (FileSystem.get(conf2)));
            dfs2.getClient().getHAServiceState();
            Thread reader = new Thread(() -> {
                try {
                    dfs2.getFileStatus(testPath);
                    readStatus.set(1);
                } catch (Exception e) {
                    e.printStackTrace();
                    readStatus.set((-1));
                }
            });
            reader.start();
            Thread.sleep(100);
            Assert.assertEquals(0, readStatus.get());
            TestConsistentReadsObserver.dfsCluster.getNameNode(2).getRpcServer().rollEditLog();
            TestConsistentReadsObserver.dfsCluster.getNameNode(0).getNamesystem().getEditLogTailer().doTailEdits();
            GenericTestUtils.waitFor(() -> (readStatus.get()) != 0, 100, 10000);
            Assert.assertEquals(1, readStatus.get());
        } finally {
            // Put the cluster back the way it was when the test started
            TestConsistentReadsObserver.dfsCluster.transitionToStandby(2);
            TestConsistentReadsObserver.dfsCluster.transitionToObserver(2);
            TestConsistentReadsObserver.dfsCluster.transitionToStandby(0);
            TestConsistentReadsObserver.dfsCluster.transitionToActive(0);
        }
    }

    @Test
    public void testUncoordinatedCall() throws Exception {
        // make a write call so that client will be ahead of
        // observer for now.
        TestConsistentReadsObserver.dfs.mkdir(testPath, FsPermission.getDefault());
        // a status flag, initialized to 0, after reader finished, this will be
        // updated to 1, -1 on error
        AtomicInteger readStatus = new AtomicInteger(0);
        // create a separate thread to make a blocking read.
        Thread reader = new Thread(() -> {
            try {
                // this read call will block until server state catches up. But due to
                // configuration, this will take a very long time.
                TestConsistentReadsObserver.dfs.getClient().getFileInfo("/");
                readStatus.set(1);
                Assert.fail("Should have been interrupted before getting here.");
            } catch (IOException e) {
                e.printStackTrace();
                readStatus.set((-1));
            }
        });
        reader.start();
        long before = Time.now();
        TestConsistentReadsObserver.dfs.getClient().datanodeReport(ALL);
        long after = Time.now();
        // should succeed immediately, because datanodeReport is marked an
        // uncoordinated call, and will not be waiting for server to catch up.
        Assert.assertTrue(((after - before) < 200));
        // by this time, reader thread should still be blocking, so the status not
        // updated
        Assert.assertEquals(0, readStatus.get());
        Thread.sleep(5000);
        // reader thread status should still be unchanged after 5 sec...
        Assert.assertEquals(0, readStatus.get());
        // and the reader thread is not dead, so it must be still waiting
        Assert.assertEquals(WAITING, reader.getState());
        reader.interrupt();
    }

    /**
     * A dummy test scheduler that starts backoff after a fixed number
     * of requests.
     */
    public static class TestRpcScheduler implements RpcScheduler {
        // Allow a number of RPCs to pass in order for the NN restart to succeed.
        private int allowed = 10;

        public TestRpcScheduler() {
        }

        @Override
        public int getPriorityLevel(Schedulable obj) {
            return 0;
        }

        @Override
        public boolean shouldBackOff(Schedulable obj) {
            return (--(allowed)) < 0;
        }

        @Override
        public void addResponseTime(String name, int priorityLevel, int queueTime, int processingTime) {
        }

        @Override
        public void stop() {
        }
    }
}

