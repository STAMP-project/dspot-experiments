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
package org.apache.hadoop.hdfs.protocol;


import Capability.STORAGE_BLOCK_REPORT_BUFFERS;
import Capability.UNKNOWN;
import ReplicaState.RBW;
import ReplicaState.RWR;
import com.google.protobuf.ServiceException;
import java.io.IOException;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.hadoop.hdfs.DFSTestUtil;
import org.apache.hadoop.hdfs.protocol.proto.DatanodeProtocolProtos.BlockReportRequestProto;
import org.apache.hadoop.hdfs.protocol.proto.DatanodeProtocolProtos.BlockReportResponseProto;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolClientSideTranslatorPB;
import org.apache.hadoop.hdfs.protocolPB.DatanodeProtocolPB;
import org.apache.hadoop.hdfs.server.datanode.Replica;
import org.apache.hadoop.hdfs.server.protocol.BlockReportContext;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorage;
import org.apache.hadoop.hdfs.server.protocol.NamespaceInfo;
import org.apache.hadoop.hdfs.server.protocol.StorageBlockReport;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


public class TestBlockListAsLongs {
    static Block b1 = new Block(1, 11, 111);

    static Block b2 = new Block(2, 22, 222);

    static Block b3 = new Block(3, 33, 333);

    static Block b4 = new Block(4, 44, 444);

    @Test
    public void testEmptyReport() {
        BlockListAsLongs blocks = checkReport();
        Assert.assertArrayEquals(new long[]{ 0, 0, -1, -1, -1 }, blocks.getBlockListAsLongs());
    }

    @Test
    public void testFinalized() {
        BlockListAsLongs blocks = checkReport(new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(TestBlockListAsLongs.b1, null, null));
        Assert.assertArrayEquals(new long[]{ 1, 0, 1, 11, 111, -1, -1, -1 }, blocks.getBlockListAsLongs());
    }

    @Test
    public void testUc() {
        BlockListAsLongs blocks = checkReport(new org.apache.hadoop.hdfs.server.datanode.ReplicaBeingWritten(TestBlockListAsLongs.b1, null, null, null));
        Assert.assertArrayEquals(new long[]{ 0, 1, -1, -1, -1, 1, 11, 111, RBW.getValue() }, blocks.getBlockListAsLongs());
    }

    @Test
    public void testMix() {
        BlockListAsLongs blocks = checkReport(new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(TestBlockListAsLongs.b1, null, null), new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(TestBlockListAsLongs.b2, null, null), new org.apache.hadoop.hdfs.server.datanode.ReplicaBeingWritten(TestBlockListAsLongs.b3, null, null, null), new org.apache.hadoop.hdfs.server.datanode.ReplicaWaitingToBeRecovered(TestBlockListAsLongs.b4, null, null));
        Assert.assertArrayEquals(new long[]{ 2, 2, 1, 11, 111, 2, 22, 222, -1, -1, -1, 3, 33, 333, RBW.getValue(), 4, 44, 444, RWR.getValue() }, blocks.getBlockListAsLongs());
    }

    @Test
    public void testFuzz() throws InterruptedException {
        Replica[] replicas = new Replica[100000];
        Random rand = new Random(0);
        for (int i = 0; i < (replicas.length); i++) {
            Block b = new Block(rand.nextLong(), i, (i << 4));
            switch (rand.nextInt(2)) {
                case 0 :
                    replicas[i] = new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(b, null, null);
                    break;
                case 1 :
                    replicas[i] = new org.apache.hadoop.hdfs.server.datanode.ReplicaBeingWritten(b, null, null, null);
                    break;
                case 2 :
                    replicas[i] = new org.apache.hadoop.hdfs.server.datanode.ReplicaWaitingToBeRecovered(b, null, null);
                    break;
            }
        }
        checkReport(replicas);
    }

    @Test
    public void testCapabilitiesInited() {
        NamespaceInfo nsInfo = new NamespaceInfo();
        Assert.assertTrue(nsInfo.isCapabilitySupported(STORAGE_BLOCK_REPORT_BUFFERS));
    }

    @Test
    public void testDatanodeDetect() throws ServiceException, IOException {
        final AtomicReference<BlockReportRequestProto> request = new AtomicReference<>();
        // just capture the outgoing PB
        DatanodeProtocolPB mockProxy = Mockito.mock(DatanodeProtocolPB.class);
        Mockito.doAnswer(new Answer<BlockReportResponseProto>() {
            public BlockReportResponseProto answer(InvocationOnMock invocation) {
                Object[] args = invocation.getArguments();
                request.set(((BlockReportRequestProto) (args[1])));
                return BlockReportResponseProto.newBuilder().build();
            }
        }).when(mockProxy).blockReport(ArgumentMatchers.any(), ArgumentMatchers.any(BlockReportRequestProto.class));
        @SuppressWarnings("resource")
        DatanodeProtocolClientSideTranslatorPB nn = new DatanodeProtocolClientSideTranslatorPB(mockProxy);
        DatanodeRegistration reg = DFSTestUtil.getLocalDatanodeRegistration();
        NamespaceInfo nsInfo = new NamespaceInfo(1, "cluster", "bp", 1);
        reg.setNamespaceInfo(nsInfo);
        Replica r = new org.apache.hadoop.hdfs.server.datanode.FinalizedReplica(new Block(1, 2, 3), null, null);
        BlockListAsLongs bbl = BlockListAsLongs.encode(Collections.singleton(r));
        DatanodeStorage storage = new DatanodeStorage("s1");
        StorageBlockReport[] sbr = new StorageBlockReport[]{ new StorageBlockReport(storage, bbl) };
        // check DN sends new-style BR
        request.set(null);
        nsInfo.setCapabilities(STORAGE_BLOCK_REPORT_BUFFERS.getMask());
        nn.blockReport(reg, "pool", sbr, new BlockReportContext(1, 0, System.nanoTime(), 0L, true));
        BlockReportRequestProto proto = request.get();
        Assert.assertNotNull(proto);
        Assert.assertTrue(proto.getReports(0).getBlocksList().isEmpty());
        Assert.assertFalse(proto.getReports(0).getBlocksBuffersList().isEmpty());
        // back up to prior version and check DN sends old-style BR
        request.set(null);
        nsInfo.setCapabilities(UNKNOWN.getMask());
        BlockListAsLongs blockList = getBlockList(r);
        StorageBlockReport[] obp = new StorageBlockReport[]{ new StorageBlockReport(new DatanodeStorage("s1"), blockList) };
        nn.blockReport(reg, "pool", obp, new BlockReportContext(1, 0, System.nanoTime(), 0L, true));
        proto = request.get();
        Assert.assertNotNull(proto);
        Assert.assertFalse(proto.getReports(0).getBlocksList().isEmpty());
        Assert.assertTrue(proto.getReports(0).getBlocksBuffersList().isEmpty());
    }
}

