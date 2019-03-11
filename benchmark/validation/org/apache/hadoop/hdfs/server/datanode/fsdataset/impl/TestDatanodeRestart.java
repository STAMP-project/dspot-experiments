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
package org.apache.hadoop.hdfs.server.datanode.fsdataset.impl;


import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_DATANODE_BP_READY_TIMEOUT_KEY;
import HdfsClientConfigKeys.DFS_CLIENT_SOCKET_TIMEOUT_KEY;
import HdfsClientConfigKeys.DFS_CLIENT_WRITE_PACKET_SIZE_KEY;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.HdfsConfiguration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.server.datanode.DataNodeFaultInjector;
import org.apache.hadoop.ipc.RemoteException;
import org.apache.hadoop.util.Time;
import org.junit.Test;


/**
 * Test if a datanode can correctly upgrade itself
 */
public class TestDatanodeRestart {
    // test finalized replicas persist across DataNode restarts
    @Test
    public void testFinalizedReplicas() throws Exception {
        // bring up a cluster of 3
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_BLOCK_SIZE_KEY, 1024L);
        conf.setInt(DFS_CLIENT_WRITE_PACKET_SIZE_KEY, 512);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(3).build();
        cluster.waitActive();
        FileSystem fs = cluster.getFileSystem();
        try {
            // test finalized replicas
            final String TopDir = "/test";
            DFSTestUtil util = new DFSTestUtil.Builder().setName("TestDatanodeRestart").setNumFiles(2).build();
            util.createFiles(fs, TopDir, ((short) (3)));
            util.waitReplication(fs, TopDir, ((short) (3)));
            util.checkFiles(fs, TopDir);
            cluster.restartDataNodes();
            cluster.waitActive();
            util.checkFiles(fs, TopDir);
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testWaitForRegistrationOnRestart() throws Exception {
        Configuration conf = new HdfsConfiguration();
        conf.setLong(DFS_DATANODE_BP_READY_TIMEOUT_KEY, 5);
        conf.setInt(DFS_CLIENT_SOCKET_TIMEOUT_KEY, 5000);
        // This makes the datanode appear registered to the NN, but it won't be
        // able to get to the saved dn reg internally.
        DataNodeFaultInjector dnFaultInjector = new DataNodeFaultInjector() {
            @Override
            public void noRegistration() throws IOException {
                throw new IOException("no reg found for testing");
            }
        };
        DataNodeFaultInjector oldDnInjector = DataNodeFaultInjector.get();
        DataNodeFaultInjector.set(dnFaultInjector);
        MiniDFSCluster cluster = null;
        long start = 0;
        Path file = new Path("/reg");
        try {
            int numDNs = 1;
            cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDNs).build();
            cluster.waitActive();
            start = Time.monotonicNow();
            FileSystem fileSys = cluster.getFileSystem();
            try {
                DFSTestUtil.createFile(fileSys, file, 10240L, ((short) (1)), 0L);
                // It is a bug if this does not fail.
                throw new IOException("Did not fail!");
            } catch (org.apache.hadoop.ipc e) {
                long elapsed = (Time.monotonicNow()) - start;
                // timers have at-least semantics, so it should be at least 5 seconds.
                if ((elapsed < 5000) || (elapsed > 10000)) {
                    throw new IOException((elapsed + " milliseconds passed."), e);
                }
            }
            DataNodeFaultInjector.set(oldDnInjector);
            // this should succeed now.
            DFSTestUtil.createFile(fileSys, file, 10240L, ((short) (1)), 0L);
            // turn it back to under-construction, so that the client calls
            // getReplicaVisibleLength() rpc method against the datanode.
            fileSys.append(file);
            // back to simulating unregistered node.
            DataNodeFaultInjector.set(dnFaultInjector);
            byte[] buffer = new byte[8];
            start = Time.monotonicNow();
            try {
                fileSys.open(file).read(0L, buffer, 0, 1);
                throw new IOException("Did not fail!");
            } catch (IOException e) {
                long elapsed = (Time.monotonicNow()) - start;
                if (e.getMessage().contains("readBlockLength")) {
                    throw new IOException("Failed, but with unexpected exception:", e);
                }
                // timers have at-least semantics, so it should be at least 5 seconds.
                if ((elapsed < 5000) || (elapsed > 10000)) {
                    throw new IOException((elapsed + " milliseconds passed."), e);
                }
            }
            DataNodeFaultInjector.set(oldDnInjector);
            fileSys.open(file).read(0L, buffer, 0, 1);
        } finally {
            DataNodeFaultInjector.set(oldDnInjector);
            if (cluster != null) {
                cluster.shutdown();
            }
        }
    }
}

