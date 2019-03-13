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
package org.apache.hadoop;


import CommonConfigurationKeys.IPC_SERVER_HANDLER_QUEUE_SIZE_KEY;
import DFSConfigKeys.DFS_NAMENODE_SERVICE_HANDLER_COUNT_DEFAULT;
import DFSConfigKeys.DFS_NAMENODE_SERVICE_HANDLER_COUNT_KEY;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeys;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.namenode.NameNodeRpcServer;
import org.apache.hadoop.hdfs.tools.DFSAdmin;
import org.apache.hadoop.ipc.FairCallQueue;
import org.apache.hadoop.metrics2.MetricsException;
import org.apache.hadoop.metrics2.lib.DefaultMetricsSystem;
import org.junit.Assert;
import org.junit.Test;


public class TestRefreshCallQueue {
    private MiniDFSCluster cluster;

    private Configuration config;

    static int mockQueueConstructions;

    static int mockQueuePuts;

    private int nnPort = 0;

    @SuppressWarnings("serial")
    public static class MockCallQueue<E> extends LinkedBlockingQueue<E> {
        public MockCallQueue(int levels, int cap, String ns, Configuration conf) {
            super(cap);
            (TestRefreshCallQueue.mockQueueConstructions)++;
        }

        public void put(E e) throws InterruptedException {
            super.put(e);
            (TestRefreshCallQueue.mockQueuePuts)++;
        }
    }

    @Test
    public void testRefresh() throws Exception {
        // We want to count additional events, so we reset here
        TestRefreshCallQueue.mockQueueConstructions = 0;
        TestRefreshCallQueue.mockQueuePuts = 0;
        setUp(TestRefreshCallQueue.MockCallQueue.class);
        Assert.assertTrue("Mock queue should have been constructed", ((TestRefreshCallQueue.mockQueueConstructions) > 0));
        Assert.assertTrue("Puts are routed through MockQueue", canPutInMockQueue());
        int lastMockQueueConstructions = TestRefreshCallQueue.mockQueueConstructions;
        // Replace queue with the queue specified in core-site.xml, which would be
        // the LinkedBlockingQueue
        DFSAdmin admin = new DFSAdmin(config);
        String[] args = new String[]{ "-refreshCallQueue" };
        int exitCode = admin.run(args);
        Assert.assertEquals("DFSAdmin should return 0", 0, exitCode);
        Assert.assertEquals("Mock queue should have no additional constructions", lastMockQueueConstructions, TestRefreshCallQueue.mockQueueConstructions);
        try {
            Assert.assertFalse("Puts are routed through LBQ instead of MockQueue", canPutInMockQueue());
        } catch (IOException ioe) {
            Assert.fail("Could not put into queue at all");
        }
    }

    @Test
    public void testRefreshCallQueueWithFairCallQueue() throws Exception {
        setUp(FairCallQueue.class);
        boolean oldValue = DefaultMetricsSystem.inMiniClusterMode();
        // throw an error when we double-initialize JvmMetrics
        DefaultMetricsSystem.setMiniClusterMode(false);
        int serviceHandlerCount = config.getInt(DFS_NAMENODE_SERVICE_HANDLER_COUNT_KEY, DFS_NAMENODE_SERVICE_HANDLER_COUNT_DEFAULT);
        NameNodeRpcServer rpcServer = ((NameNodeRpcServer) (cluster.getNameNodeRpc()));
        // check callqueue size
        Assert.assertEquals(((CommonConfigurationKeys.IPC_SERVER_HANDLER_QUEUE_SIZE_DEFAULT) * serviceHandlerCount), rpcServer.getClientRpcServer().getMaxQueueSize());
        // Replace queue and update queue size
        config.setInt(IPC_SERVER_HANDLER_QUEUE_SIZE_KEY, 150);
        try {
            rpcServer.getClientRpcServer().refreshCallQueue(config);
        } catch (Exception e) {
            Throwable cause = e.getCause();
            if ((cause instanceof MetricsException) && (cause.getMessage().contains((("Metrics source DecayRpcSchedulerMetrics2.ipc." + (nnPort)) + " already exists!")))) {
                Assert.fail(("DecayRpcScheduler metrics should be unregistered before" + " reregister"));
            }
            throw e;
        } finally {
            DefaultMetricsSystem.setMiniClusterMode(oldValue);
        }
        // check callQueueSize has changed
        Assert.assertEquals((150 * serviceHandlerCount), rpcServer.getClientRpcServer().getMaxQueueSize());
    }
}

