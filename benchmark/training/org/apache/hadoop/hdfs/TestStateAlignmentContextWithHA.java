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
package org.apache.hadoop.hdfs;


import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.ClientProtocol;
import org.apache.hadoop.hdfs.qjournal.MiniQJMHACluster;
import org.apache.hadoop.hdfs.server.namenode.ha.HAProxyFactory;
import org.apache.hadoop.hdfs.server.namenode.ha.HATestUtil;
import org.apache.hadoop.hdfs.server.namenode.ha.ObserverReadProxyProvider;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class is used to test server sending state alignment information to clients
 * via RPC and likewise clients receiving and updating their last known
 * state alignment info.
 * These tests check that after a single RPC call a client will have caught up
 * to the most recent alignment state of the server.
 */
public class TestStateAlignmentContextWithHA {
    public static final Logger LOG = LoggerFactory.getLogger(TestStateAlignmentContextWithHA.class.getName());

    private static final int NUMDATANODES = 1;

    private static final int NUMCLIENTS = 10;

    private static final int NUMFILES = 120;

    private static final Configuration CONF = new HdfsConfiguration();

    private static final List<ClientGSIContext> AC_LIST = new ArrayList<>();

    private static MiniQJMHACluster qjmhaCluster;

    private static MiniDFSCluster cluster;

    private static List<TestStateAlignmentContextWithHA.Worker> clients;

    private DistributedFileSystem dfs;

    private int active = 0;

    private int standby = 1;

    static class ORPPwithAlignmentContexts<T extends ClientProtocol> extends ObserverReadProxyProvider<T> {
        public ORPPwithAlignmentContexts(Configuration conf, URI uri, Class<T> xface, HAProxyFactory<T> factory) throws IOException {
            super(conf, uri, xface, factory);
            TestStateAlignmentContextWithHA.AC_LIST.add(((ClientGSIContext) (TestStateAlignmentContextWithHA.ORPPwithAlignmentContexts.getAlignmentContext())));
        }
    }

    /**
     * This test checks if after a client writes we can see the state id in
     * updated via the response.
     */
    @Test
    public void testStateTransferOnWrite() throws Exception {
        long preWriteState = TestStateAlignmentContextWithHA.cluster.getNamesystem(active).getLastWrittenTransactionId();
        DFSTestUtil.writeFile(dfs, new Path("/testFile1"), "abc");
        long clientState = getContext(0).getLastSeenStateId();
        long postWriteState = TestStateAlignmentContextWithHA.cluster.getNamesystem(active).getLastWrittenTransactionId();
        // Write(s) should have increased state. Check for greater than.
        Assert.assertTrue((clientState > preWriteState));
        // Client and server state should be equal.
        Assert.assertEquals(clientState, postWriteState);
    }

    /**
     * This test checks if after a client reads we can see the state id in
     * updated via the response.
     */
    @Test
    public void testStateTransferOnRead() throws Exception {
        DFSTestUtil.writeFile(dfs, new Path("/testFile2"), "123");
        long lastWrittenId = TestStateAlignmentContextWithHA.cluster.getNamesystem(active).getLastWrittenTransactionId();
        DFSTestUtil.readFile(dfs, new Path("/testFile2"));
        // Read should catch client up to last written state.
        long clientState = getContext(0).getLastSeenStateId();
        Assert.assertEquals(clientState, lastWrittenId);
    }

    /**
     * This test checks that a fresh client starts with no state and becomes
     * updated of state from RPC call.
     */
    @Test
    public void testStateTransferOnFreshClient() throws Exception {
        DFSTestUtil.writeFile(dfs, new Path("/testFile3"), "ezpz");
        long lastWrittenId = TestStateAlignmentContextWithHA.cluster.getNamesystem(active).getLastWrittenTransactionId();
        try (DistributedFileSystem clearDfs = HATestUtil.configureObserverReadFs(TestStateAlignmentContextWithHA.cluster, TestStateAlignmentContextWithHA.CONF, TestStateAlignmentContextWithHA.ORPPwithAlignmentContexts.class, true)) {
            ClientGSIContext clientState = getContext(1);
            Assert.assertEquals(clientState.getLastSeenStateId(), Long.MIN_VALUE);
            DFSTestUtil.readFile(clearDfs, new Path("/testFile3"));
            Assert.assertEquals(clientState.getLastSeenStateId(), lastWrittenId);
        }
    }

    /**
     * This test checks if after a client writes we can see the state id in
     * updated via the response.
     */
    @Test
    public void testStateTransferOnWriteWithFailover() throws Exception {
        long preWriteState = TestStateAlignmentContextWithHA.cluster.getNamesystem(active).getLastWrittenTransactionId();
        // Write using HA client.
        DFSTestUtil.writeFile(dfs, new Path("/testFile1FO"), "123");
        long clientState = getContext(0).getLastSeenStateId();
        long postWriteState = TestStateAlignmentContextWithHA.cluster.getNamesystem(active).getLastWrittenTransactionId();
        // Write(s) should have increased state. Check for greater than.
        Assert.assertTrue((clientState > preWriteState));
        // Client and server state should be equal.
        Assert.assertEquals(clientState, postWriteState);
        // Failover NameNode.
        failOver();
        // Write using HA client.
        DFSTestUtil.writeFile(dfs, new Path("/testFile2FO"), "456");
        long clientStateFO = getContext(0).getLastSeenStateId();
        long writeStateFO = TestStateAlignmentContextWithHA.cluster.getNamesystem(active).getLastWrittenTransactionId();
        // Write(s) should have increased state. Check for greater than.
        Assert.assertTrue((clientStateFO > postWriteState));
        // Client and server state should be equal.
        Assert.assertEquals(clientStateFO, writeStateFO);
    }

    @Test(timeout = 300000)
    public void testMultiClientStatesWithRandomFailovers() throws Exception {
        // First run, half the load, with one failover.
        runClientsWithFailover(1, ((TestStateAlignmentContextWithHA.NUMCLIENTS) / 2), ((TestStateAlignmentContextWithHA.NUMFILES) / 2));
        // Second half, with fail back.
        runClientsWithFailover((((TestStateAlignmentContextWithHA.NUMCLIENTS) / 2) + 1), TestStateAlignmentContextWithHA.NUMCLIENTS, ((TestStateAlignmentContextWithHA.NUMFILES) / 2));
    }

    private enum STATE {

        SUCCESS,
        FAIL,
        ERROR;}

    private class Worker implements Callable<TestStateAlignmentContextWithHA.STATE> {
        private final DistributedFileSystem client;

        private final int filesToMake;

        private String filePath;

        private final int nonce;

        Worker(DistributedFileSystem client, int filesToMake, String filePath, int nonce) {
            this.client = client;
            this.filesToMake = filesToMake;
            this.filePath = filePath;
            this.nonce = nonce;
        }

        @Override
        public TestStateAlignmentContextWithHA.STATE call() {
            int i = -1;
            try {
                for (i = 0; i < (filesToMake); i++) {
                    ClientGSIContext gsiContext = getContext(nonce);
                    long preClientStateFO = gsiContext.getLastSeenStateId();
                    // Write using HA client.
                    Path path = new Path(((((filePath) + (nonce)) + "_") + i));
                    DFSTestUtil.writeFile(client, path, "erk");
                    long postClientStateFO = gsiContext.getLastSeenStateId();
                    // Write(s) should have increased state. Check for greater than.
                    if ((postClientStateFO < 0) || (postClientStateFO <= preClientStateFO)) {
                        TestStateAlignmentContextWithHA.LOG.error("FAIL: Worker started with: {} , but finished with: {}", preClientStateFO, postClientStateFO);
                        return TestStateAlignmentContextWithHA.STATE.FAIL;
                    }
                    if ((i % ((TestStateAlignmentContextWithHA.NUMFILES) / 10)) == 0) {
                        TestStateAlignmentContextWithHA.LOG.info("Worker {} created {} files", nonce, i);
                        TestStateAlignmentContextWithHA.LOG.info("LastSeenStateId = {}", postClientStateFO);
                    }
                }
                return TestStateAlignmentContextWithHA.STATE.SUCCESS;
            } catch (Exception e) {
                TestStateAlignmentContextWithHA.LOG.error("ERROR: Worker failed with: ", e);
                return TestStateAlignmentContextWithHA.STATE.ERROR;
            } finally {
                TestStateAlignmentContextWithHA.LOG.info("Worker {} created {} files", nonce, i);
            }
        }

        public void kill() throws IOException {
            client.dfs.closeAllFilesBeingWritten(true);
            client.dfs.closeOutputStreams(true);
            client.dfs.closeConnectionToNamenode();
            client.dfs.close();
            client.close();
        }
    }
}

