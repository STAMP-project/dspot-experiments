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


import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hdfs.DistributedFileSystem;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.hdfs.server.protocol.StorageBlockReport;
import org.apache.hadoop.test.GenericTestUtils;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests that the DataNode respects
 * {@link DFSConfigKeys#DFS_BLOCKREPORT_SPLIT_THRESHOLD_KEY}
 */
public class TestDnRespectsBlockReportSplitThreshold {
    public static final Logger LOG = LoggerFactory.getLogger(TestStorageReport.class);

    private static final int BLOCK_SIZE = 1024;

    private static final short REPL_FACTOR = 1;

    private static final long seed = -17958194;

    private static final int BLOCKS_IN_FILE = 5;

    private static Configuration conf;

    private MiniDFSCluster cluster;

    private DistributedFileSystem fs;

    static String bpid;

    /**
     * Test that if splitThreshold is zero, then we always get a separate
     * call per storage.
     */
    @Test(timeout = 300000)
    public void testAlwaysSplit() throws IOException, InterruptedException {
        startUpCluster(0);
        NameNode nn = cluster.getNameNode();
        DataNode dn = cluster.getDataNodes().get(0);
        // Create a file with a few blocks.
        createFile(GenericTestUtils.getMethodName(), TestDnRespectsBlockReportSplitThreshold.BLOCKS_IN_FILE);
        // Insert a spy object for the NN RPC.
        DatanodeProtocolClientSideTranslatorPB nnSpy = InternalDataNodeTestUtils.spyOnBposToNN(dn, nn);
        // Trigger a block report so there is an interaction with the spy
        // object.
        DataNodeTestUtils.triggerBlockReport(dn);
        ArgumentCaptor<StorageBlockReport[]> captor = ArgumentCaptor.forClass(StorageBlockReport[].class);
        Mockito.verify(nnSpy, Mockito.times(cluster.getStoragesPerDatanode())).blockReport(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.anyString(), captor.capture(), ArgumentMatchers.any());
        verifyCapturedArguments(captor, 1, TestDnRespectsBlockReportSplitThreshold.BLOCKS_IN_FILE);
    }

    /**
     * Tests the behavior when the count of blocks is exactly one less than
     * the threshold.
     */
    @Test(timeout = 300000)
    public void testCornerCaseUnderThreshold() throws IOException, InterruptedException {
        startUpCluster(((TestDnRespectsBlockReportSplitThreshold.BLOCKS_IN_FILE) + 1));
        NameNode nn = cluster.getNameNode();
        DataNode dn = cluster.getDataNodes().get(0);
        // Create a file with a few blocks.
        createFile(GenericTestUtils.getMethodName(), TestDnRespectsBlockReportSplitThreshold.BLOCKS_IN_FILE);
        // Insert a spy object for the NN RPC.
        DatanodeProtocolClientSideTranslatorPB nnSpy = InternalDataNodeTestUtils.spyOnBposToNN(dn, nn);
        // Trigger a block report so there is an interaction with the spy
        // object.
        DataNodeTestUtils.triggerBlockReport(dn);
        ArgumentCaptor<StorageBlockReport[]> captor = ArgumentCaptor.forClass(StorageBlockReport[].class);
        Mockito.verify(nnSpy, Mockito.times(1)).blockReport(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.anyString(), captor.capture(), ArgumentMatchers.any());
        verifyCapturedArguments(captor, cluster.getStoragesPerDatanode(), TestDnRespectsBlockReportSplitThreshold.BLOCKS_IN_FILE);
    }

    /**
     * Tests the behavior when the count of blocks is exactly equal to the
     * threshold.
     */
    @Test(timeout = 300000)
    public void testCornerCaseAtThreshold() throws IOException, InterruptedException {
        startUpCluster(TestDnRespectsBlockReportSplitThreshold.BLOCKS_IN_FILE);
        NameNode nn = cluster.getNameNode();
        DataNode dn = cluster.getDataNodes().get(0);
        // Create a file with a few blocks.
        createFile(GenericTestUtils.getMethodName(), TestDnRespectsBlockReportSplitThreshold.BLOCKS_IN_FILE);
        // Insert a spy object for the NN RPC.
        DatanodeProtocolClientSideTranslatorPB nnSpy = InternalDataNodeTestUtils.spyOnBposToNN(dn, nn);
        // Trigger a block report so there is an interaction with the spy
        // object.
        DataNodeTestUtils.triggerBlockReport(dn);
        ArgumentCaptor<StorageBlockReport[]> captor = ArgumentCaptor.forClass(StorageBlockReport[].class);
        Mockito.verify(nnSpy, Mockito.times(cluster.getStoragesPerDatanode())).blockReport(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.anyString(), captor.capture(), ArgumentMatchers.any());
        verifyCapturedArguments(captor, 1, TestDnRespectsBlockReportSplitThreshold.BLOCKS_IN_FILE);
    }
}

