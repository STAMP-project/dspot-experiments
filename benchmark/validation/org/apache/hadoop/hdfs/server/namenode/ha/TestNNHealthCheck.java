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


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DFSConfigKeys;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.MiniDFSNNTopology;
import org.junit.Test;


public class TestNNHealthCheck {
    private MiniDFSCluster cluster;

    private Configuration conf;

    @Test
    public void testNNHealthCheck() throws IOException {
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).nnTopology(MiniDFSNNTopology.simpleHATopology()).build();
        doNNHealthCheckTest();
    }

    @Test
    public void testNNHealthCheckWithLifelineAddress() throws IOException {
        conf.set(DFSConfigKeys.DFS_NAMENODE_LIFELINE_RPC_ADDRESS_KEY, "0.0.0.0:0");
        cluster = new MiniDFSCluster.Builder(conf).numDataNodes(0).nnTopology(MiniDFSNNTopology.simpleHATopology()).build();
        doNNHealthCheckTest();
    }
}

