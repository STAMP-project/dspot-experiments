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
package org.apache.hadoop.hdfs.server.datanode;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests if DataNode process exits if all Block Pool services exit.
 */
public class TestDataNodeExit {
    private static final long WAIT_TIME_IN_MILLIS = 10;

    Configuration conf;

    MiniDFSCluster cluster = null;

    /**
     * Test BPService Thread Exit
     */
    @Test
    public void testBPServiceExit() throws Exception {
        DataNode dn = cluster.getDataNodes().get(0);
        stopBPServiceThreads(1, dn);
        Assert.assertTrue("DataNode should not exit", dn.isDatanodeUp());
        stopBPServiceThreads(2, dn);
        Assert.assertFalse("DataNode should exit", dn.isDatanodeUp());
    }

    @Test
    public void testSendOOBToPeers() throws Exception {
        DataNode dn = cluster.getDataNodes().get(0);
        DataXceiverServer spyXserver = Mockito.spy(dn.getXferServer());
        NullPointerException npe = new NullPointerException();
        Mockito.doThrow(npe).when(spyXserver).sendOOBToPeers();
        dn.xserver = spyXserver;
        try {
            dn.shutdown();
        } catch (Exception e) {
            Assert.fail(("DataNode shutdown should not have thrown exception " + e));
        }
    }
}

