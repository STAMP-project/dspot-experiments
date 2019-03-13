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
import org.apache.hadoop.hdfs.DataNode;
import org.apache.hadoop.hdfs.MiniDFSCluster;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.server.namenode.NameNode;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.hdfs.server.protocol.StorageReceivedDeletedBlocks;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Verify that incremental block reports are generated in response to
 * block additions/deletions.
 */
public class TestIncrementalBlockReports {
    public static final Logger LOG = LoggerFactory.getLogger(TestIncrementalBlockReports.class);

    private static final short DN_COUNT = 1;

    private static final long DUMMY_BLOCK_ID = 5678;

    private static final long DUMMY_BLOCK_LENGTH = 1024 * 1024;

    private static final long DUMMY_BLOCK_GENSTAMP = 1000;

    private MiniDFSCluster cluster = null;

    private Configuration conf;

    private NameNode singletonNn;

    private DataNode singletonDn;

    private BPOfferService bpos;// BPOS to use for block injection.


    private BPServiceActor actor;// BPSA to use for block injection.


    private String storageUuid;// DatanodeStorage to use for block injection.


    /**
     * Ensure that an IBR is generated immediately for a block received by
     * the DN.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test(timeout = 60000)
    public void testReportBlockReceived() throws IOException, InterruptedException {
        try {
            DatanodeProtocolClientSideTranslatorPB nnSpy = spyOnDnCallsToNn();
            injectBlockReceived();
            // Sleep for a very short time, this is necessary since the IBR is
            // generated asynchronously.
            Thread.sleep(2000);
            // Ensure that the received block was reported immediately.
            Mockito.verify(nnSpy, Mockito.times(1)).blockReceivedAndDeleted(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(StorageReceivedDeletedBlocks[].class));
        } finally {
            cluster.shutdown();
            cluster = null;
        }
    }

    /**
     * Ensure that a delayed IBR is generated for a block deleted on the DN.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test(timeout = 60000)
    public void testReportBlockDeleted() throws IOException, InterruptedException {
        try {
            // Trigger a block report to reset the IBR timer.
            DataNodeTestUtils.triggerBlockReport(singletonDn);
            // Spy on calls from the DN to the NN
            DatanodeProtocolClientSideTranslatorPB nnSpy = spyOnDnCallsToNn();
            injectBlockDeleted();
            // Sleep for a very short time since IBR is generated
            // asynchronously.
            Thread.sleep(2000);
            // Ensure that no block report was generated immediately.
            // Deleted blocks are reported when the IBR timer elapses.
            Mockito.verify(nnSpy, Mockito.times(0)).blockReceivedAndDeleted(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(StorageReceivedDeletedBlocks[].class));
            // Trigger a heartbeat, this also triggers an IBR.
            DataNodeTestUtils.triggerHeartbeat(singletonDn);
            Thread.sleep(2000);
            // Ensure that the deleted block is reported.
            Mockito.verify(nnSpy, Mockito.times(1)).blockReceivedAndDeleted(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(StorageReceivedDeletedBlocks[].class));
        } finally {
            cluster.shutdown();
            cluster = null;
        }
    }

    /**
     * Add a received block entry and then replace it. Ensure that a single
     * IBR is generated and that pending receive request state is cleared.
     * This test case verifies the failure in HDFS-5922.
     *
     * @throws InterruptedException
     * 		
     * @throws IOException
     * 		
     */
    @Test(timeout = 60000)
    public void testReplaceReceivedBlock() throws IOException, InterruptedException {
        try {
            // Spy on calls from the DN to the NN
            DatanodeProtocolClientSideTranslatorPB nnSpy = spyOnDnCallsToNn();
            injectBlockReceived();
            injectBlockReceived();// Overwrite the existing entry.

            // Sleep for a very short time since IBR is generated
            // asynchronously.
            Thread.sleep(2000);
            // Ensure that the received block is reported.
            Mockito.verify(nnSpy, Mockito.atLeastOnce()).blockReceivedAndDeleted(ArgumentMatchers.any(DatanodeRegistration.class), ArgumentMatchers.anyString(), ArgumentMatchers.any(StorageReceivedDeletedBlocks[].class));
            // Ensure that no more IBRs are pending.
            Assert.assertFalse(actor.getIbrManager().sendImmediately());
        } finally {
            cluster.shutdown();
            cluster = null;
        }
    }
}

