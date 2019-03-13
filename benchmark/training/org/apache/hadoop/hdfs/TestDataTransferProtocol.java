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


import BlockConstructionStage.DATA_STREAMING;
import BlockConstructionStage.PIPELINE_CLOSE_RECOVERY;
import BlockConstructionStage.PIPELINE_SETUP_APPEND;
import BlockConstructionStage.PIPELINE_SETUP_APPEND_RECOVERY;
import BlockConstructionStage.PIPELINE_SETUP_CREATE;
import BlockConstructionStage.PIPELINE_SETUP_STREAMING_RECOVERY;
import BlockTokenSecretManager.DUMMY_TOKEN;
import DFSConfigKeys.DFS_BLOCK_SIZE_KEY;
import DFSConfigKeys.DFS_REPLICATION_KEY;
import DataChecksum.Type.CRC32C;
import DataTransferProtocol.DATA_TRANSFER_VERSION;
import DataTransferProtos.PipelineAckProto;
import DatanodeReportType.LIVE;
import Op.WRITE_BLOCK;
import PipelineAck.ECN.DISABLED;
import PipelineAck.ECN.SUPPORTED;
import Status.CHECKSUM_OK;
import Status.ERROR;
import Status.SUCCESS;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hdfs.protocol.DatanodeID;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.datatransfer.DataTransferProtoUtil;
import org.apache.hadoop.hdfs.protocol.datatransfer.DataTransferProtocol;
import org.apache.hadoop.hdfs.protocol.datatransfer.PacketHeader;
import org.apache.hadoop.hdfs.protocol.datatransfer.PipelineAck;
import org.apache.hadoop.hdfs.protocol.datatransfer.Sender;
import org.apache.hadoop.hdfs.protocol.proto.DataTransferProtos;
import org.apache.hadoop.hdfs.protocol.proto.DataTransferProtos.BlockOpResponseProto;
import org.apache.hadoop.hdfs.protocol.proto.DataTransferProtos.ReadOpChecksumInfoProto;
import org.apache.hadoop.hdfs.server.datanode.CachingStrategy;
import org.apache.hadoop.hdfs.server.datanode.InternalDataNodeTestUtils;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.util.DataChecksum;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This tests data transfer protocol handling in the Datanode. It sends
 * various forms of wrong data and verifies that Datanode handles it well.
 */
public class TestDataTransferProtocol {
    private static final Logger LOG = LoggerFactory.getLogger("org.apache.hadoop.hdfs.TestDataTransferProtocol");

    private static final DataChecksum DEFAULT_CHECKSUM = DataChecksum.newDataChecksum(CRC32C, 512);

    DatanodeID datanode;

    InetSocketAddress dnAddr;

    final ByteArrayOutputStream sendBuf = new ByteArrayOutputStream(128);

    final DataOutputStream sendOut = new DataOutputStream(sendBuf);

    final Sender sender = new Sender(sendOut);

    final ByteArrayOutputStream recvBuf = new ByteArrayOutputStream(128);

    final DataOutputStream recvOut = new DataOutputStream(recvBuf);

    @Test
    public void testOpWrite() throws IOException {
        int numDataNodes = 1;
        final long BLOCK_ID_FUDGE = 128;
        Configuration conf = new HdfsConfiguration();
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDataNodes).build();
        try {
            cluster.waitActive();
            String poolId = cluster.getNamesystem().getBlockPoolId();
            datanode = InternalDataNodeTestUtils.getDNRegistrationForBP(cluster.getDataNodes().get(0), poolId);
            dnAddr = NetUtils.createSocketAddr(datanode.getXferAddr());
            FileSystem fileSys = cluster.getFileSystem();
            /* Test writing to finalized replicas */
            Path file = new Path("dataprotocol.dat");
            DFSTestUtil.createFile(fileSys, file, 1L, ((short) (numDataNodes)), 0L);
            // get the first blockid for the file
            ExtendedBlock firstBlock = DFSTestUtil.getFirstBlock(fileSys, file);
            // test PIPELINE_SETUP_CREATE on a finalized block
            testWrite(firstBlock, PIPELINE_SETUP_CREATE, 0L, "Cannot create an existing block", true);
            // test PIPELINE_DATA_STREAMING on a finalized block
            testWrite(firstBlock, DATA_STREAMING, 0L, "Unexpected stage", true);
            // test PIPELINE_SETUP_STREAMING_RECOVERY on an existing block
            long newGS = (firstBlock.getGenerationStamp()) + 1;
            testWrite(firstBlock, PIPELINE_SETUP_STREAMING_RECOVERY, newGS, "Cannot recover data streaming to a finalized replica", true);
            // test PIPELINE_SETUP_APPEND on an existing block
            newGS = (firstBlock.getGenerationStamp()) + 1;
            testWrite(firstBlock, PIPELINE_SETUP_APPEND, newGS, "Append to a finalized replica", false);
            firstBlock.setGenerationStamp(newGS);
            // test PIPELINE_SETUP_APPEND_RECOVERY on an existing block
            file = new Path("dataprotocol1.dat");
            DFSTestUtil.createFile(fileSys, file, 1L, ((short) (numDataNodes)), 0L);
            firstBlock = DFSTestUtil.getFirstBlock(fileSys, file);
            newGS = (firstBlock.getGenerationStamp()) + 1;
            testWrite(firstBlock, PIPELINE_SETUP_APPEND_RECOVERY, newGS, "Recover appending to a finalized replica", false);
            // test PIPELINE_CLOSE_RECOVERY on an existing block
            file = new Path("dataprotocol2.dat");
            DFSTestUtil.createFile(fileSys, file, 1L, ((short) (numDataNodes)), 0L);
            firstBlock = DFSTestUtil.getFirstBlock(fileSys, file);
            newGS = (firstBlock.getGenerationStamp()) + 1;
            testWrite(firstBlock, PIPELINE_CLOSE_RECOVERY, newGS, "Recover failed close to a finalized replica", false);
            firstBlock.setGenerationStamp(newGS);
            // Test writing to a new block. Don't choose the next sequential
            // block ID to avoid conflicting with IDs chosen by the NN.
            long newBlockId = (firstBlock.getBlockId()) + BLOCK_ID_FUDGE;
            ExtendedBlock newBlock = new ExtendedBlock(firstBlock.getBlockPoolId(), newBlockId, 0, firstBlock.getGenerationStamp());
            // test PIPELINE_SETUP_CREATE on a new block
            testWrite(newBlock, PIPELINE_SETUP_CREATE, 0L, "Create a new block", false);
            // test PIPELINE_SETUP_STREAMING_RECOVERY on a new block
            newGS = (newBlock.getGenerationStamp()) + 1;
            newBlock.setBlockId(((newBlock.getBlockId()) + 1));
            testWrite(newBlock, PIPELINE_SETUP_STREAMING_RECOVERY, newGS, "Recover a new block", true);
            // test PIPELINE_SETUP_APPEND on a new block
            newGS = (newBlock.getGenerationStamp()) + 1;
            testWrite(newBlock, PIPELINE_SETUP_APPEND, newGS, "Cannot append to a new block", true);
            // test PIPELINE_SETUP_APPEND_RECOVERY on a new block
            newBlock.setBlockId(((newBlock.getBlockId()) + 1));
            newGS = (newBlock.getGenerationStamp()) + 1;
            testWrite(newBlock, PIPELINE_SETUP_APPEND_RECOVERY, newGS, "Cannot append to a new block", true);
            /* Test writing to RBW replicas */
            Path file1 = new Path("dataprotocol1.dat");
            DFSTestUtil.createFile(fileSys, file1, 1L, ((short) (numDataNodes)), 0L);
            DFSOutputStream out = ((DFSOutputStream) (fileSys.append(file1).getWrappedStream()));
            out.write(1);
            out.hflush();
            FSDataInputStream in = fileSys.open(file1);
            firstBlock = DFSTestUtil.getAllBlocks(in).get(0).getBlock();
            firstBlock.setNumBytes(2L);
            try {
                // test PIPELINE_SETUP_CREATE on a RBW block
                testWrite(firstBlock, PIPELINE_SETUP_CREATE, 0L, "Cannot create a RBW block", true);
                // test PIPELINE_SETUP_APPEND on an existing block
                newGS = (firstBlock.getGenerationStamp()) + 1;
                testWrite(firstBlock, PIPELINE_SETUP_APPEND, newGS, "Cannot append to a RBW replica", true);
                // test PIPELINE_SETUP_APPEND on an existing block
                testWrite(firstBlock, PIPELINE_SETUP_APPEND_RECOVERY, newGS, "Recover append to a RBW replica", false);
                firstBlock.setGenerationStamp(newGS);
                // test PIPELINE_SETUP_STREAMING_RECOVERY on a RBW block
                file = new Path("dataprotocol2.dat");
                DFSTestUtil.createFile(fileSys, file, 1L, ((short) (numDataNodes)), 0L);
                out = ((DFSOutputStream) (fileSys.append(file).getWrappedStream()));
                out.write(1);
                out.hflush();
                in = fileSys.open(file);
                firstBlock = DFSTestUtil.getAllBlocks(in).get(0).getBlock();
                firstBlock.setNumBytes(2L);
                newGS = (firstBlock.getGenerationStamp()) + 1;
                testWrite(firstBlock, PIPELINE_SETUP_STREAMING_RECOVERY, newGS, "Recover a RBW replica", false);
            } finally {
                IOUtils.closeStream(in);
                IOUtils.closeStream(out);
            }
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testDataTransferProtocol() throws IOException {
        Random random = new Random();
        int oneMil = 1024 * 1024;
        Path file = new Path("dataprotocol.dat");
        int numDataNodes = 1;
        Configuration conf = new HdfsConfiguration();
        conf.setInt(DFS_REPLICATION_KEY, numDataNodes);
        MiniDFSCluster cluster = new MiniDFSCluster.Builder(conf).numDataNodes(numDataNodes).build();
        try {
            cluster.waitActive();
            datanode = cluster.getFileSystem().getDataNodeStats(LIVE)[0];
            dnAddr = NetUtils.createSocketAddr(datanode.getXferAddr());
            FileSystem fileSys = cluster.getFileSystem();
            int fileLen = Math.min(conf.getInt(DFS_BLOCK_SIZE_KEY, 4096), 4096);
            DFSTestUtil.createFile(fileSys, file, fileLen, fileLen, fileSys.getDefaultBlockSize(file), fileSys.getDefaultReplication(file), 0L);
            // get the first blockid for the file
            final ExtendedBlock firstBlock = DFSTestUtil.getFirstBlock(fileSys, file);
            final String poolId = firstBlock.getBlockPoolId();
            long newBlockId = (firstBlock.getBlockId()) + 1;
            recvBuf.reset();
            sendBuf.reset();
            // bad version
            recvOut.writeShort(((short) ((DataTransferProtocol.DATA_TRANSFER_VERSION) - 1)));
            sendOut.writeShort(((short) ((DataTransferProtocol.DATA_TRANSFER_VERSION) - 1)));
            sendRecvData("Wrong Version", true);
            // bad ops
            sendBuf.reset();
            sendOut.writeShort(((short) (DATA_TRANSFER_VERSION)));
            sendOut.writeByte(((WRITE_BLOCK.code) - 1));
            sendRecvData("Wrong Op Code", true);
            /* Test OP_WRITE_BLOCK */
            sendBuf.reset();
            DataChecksum badChecksum = Mockito.spy(TestDataTransferProtocol.DEFAULT_CHECKSUM);
            Mockito.doReturn((-1)).when(badChecksum).getBytesPerChecksum();
            writeBlock(poolId, newBlockId, badChecksum);
            recvBuf.reset();
            sendResponse(ERROR, null, null, recvOut);
            sendRecvData("wrong bytesPerChecksum while writing", true);
            sendBuf.reset();
            recvBuf.reset();
            writeBlock(poolId, (++newBlockId), TestDataTransferProtocol.DEFAULT_CHECKSUM);
            PacketHeader hdr = // size of packet
            // offset in block,
            // seqno
            // last packet
            // bad datalen
            new PacketHeader(4, 0, 100, false, ((-1) - (random.nextInt(oneMil))), false);
            hdr.write(sendOut);
            sendResponse(SUCCESS, "", null, recvOut);
            new PipelineAck(100, new int[]{ PipelineAck.combineHeader(DISABLED, ERROR) }).write(recvOut);
            sendRecvData(("negative DATA_CHUNK len while writing block " + newBlockId), true);
            // test for writing a valid zero size block
            sendBuf.reset();
            recvBuf.reset();
            writeBlock(poolId, (++newBlockId), TestDataTransferProtocol.DEFAULT_CHECKSUM);
            hdr = // size of packet
            // OffsetInBlock
            // sequencenumber
            // lastPacketInBlock
            // chunk length
            new PacketHeader(8, 0, 100, true, 0, false);
            hdr.write(sendOut);
            sendOut.writeInt(0);
            // zero checksum
            sendOut.flush();
            // ok finally write a block with 0 len
            sendResponse(SUCCESS, "", null, recvOut);
            new PipelineAck(100, new int[]{ PipelineAck.combineHeader(DISABLED, SUCCESS) }).write(recvOut);
            sendRecvData(("Writing a zero len block blockid " + newBlockId), false);
            /* Test OP_READ_BLOCK */
            String bpid = cluster.getNamesystem().getBlockPoolId();
            ExtendedBlock blk = new ExtendedBlock(bpid, firstBlock.getLocalBlock());
            long blkid = blk.getBlockId();
            // bad block id
            sendBuf.reset();
            recvBuf.reset();
            blk.setBlockId((blkid - 1));
            sender.readBlock(blk, DUMMY_TOKEN, "cl", 0L, fileLen, true, CachingStrategy.newDefaultStrategy());
            sendRecvData((("Wrong block ID " + newBlockId) + " for read"), false);
            // negative block start offset -1L
            sendBuf.reset();
            blk.setBlockId(blkid);
            sender.readBlock(blk, DUMMY_TOKEN, "cl", (-1L), fileLen, true, CachingStrategy.newDefaultStrategy());
            sendRecvData(("Negative start-offset for read for block " + (firstBlock.getBlockId())), false);
            // bad block start offset
            sendBuf.reset();
            sender.readBlock(blk, DUMMY_TOKEN, "cl", fileLen, fileLen, true, CachingStrategy.newDefaultStrategy());
            sendRecvData(("Wrong start-offset for reading block " + (firstBlock.getBlockId())), false);
            // negative length is ok. Datanode assumes we want to read the whole block.
            recvBuf.reset();
            BlockOpResponseProto.newBuilder().setStatus(SUCCESS).setReadOpChecksumInfo(ReadOpChecksumInfoProto.newBuilder().setChecksum(DataTransferProtoUtil.toProto(TestDataTransferProtocol.DEFAULT_CHECKSUM)).setChunkOffset(0L)).build().writeDelimitedTo(recvOut);
            sendBuf.reset();
            sender.readBlock(blk, DUMMY_TOKEN, "cl", 0L, ((-1L) - (random.nextInt(oneMil))), true, CachingStrategy.newDefaultStrategy());
            sendRecvData(("Negative length for reading block " + (firstBlock.getBlockId())), false);
            // length is more than size of block.
            recvBuf.reset();
            sendResponse(ERROR, null, ((((("opReadBlock " + firstBlock) + " received exception java.io.IOException:  ") + "Offset 0 and length 4097 don't match block ") + firstBlock) + " ( blockLen 4096 )"), recvOut);
            sendBuf.reset();
            sender.readBlock(blk, DUMMY_TOKEN, "cl", 0L, (fileLen + 1), true, CachingStrategy.newDefaultStrategy());
            sendRecvData(("Wrong length for reading block " + (firstBlock.getBlockId())), false);
            // At the end of all this, read the file to make sure that succeeds finally.
            sendBuf.reset();
            sender.readBlock(blk, DUMMY_TOKEN, "cl", 0L, fileLen, true, CachingStrategy.newDefaultStrategy());
            readFile(fileSys, file, fileLen);
        } finally {
            cluster.shutdown();
        }
    }

    @Test
    public void testPacketHeader() throws IOException {
        PacketHeader hdr = // size of packet
        // OffsetInBlock
        // sequencenumber
        // lastPacketInBlock
        // chunk length
        new PacketHeader(4, 1024, 100, false, 4096, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        hdr.write(new DataOutputStream(baos));
        // Read back using DataInput
        PacketHeader readBack = new PacketHeader();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        readBack.readFields(new DataInputStream(bais));
        Assert.assertEquals(hdr, readBack);
        // Read back using ByteBuffer
        readBack = new PacketHeader();
        readBack.readFields(ByteBuffer.wrap(baos.toByteArray()));
        Assert.assertEquals(hdr, readBack);
        Assert.assertTrue(hdr.sanityCheck(99));
        Assert.assertFalse(hdr.sanityCheck(100));
    }

    @Test
    public void TestPipeLineAckCompatibility() throws IOException {
        DataTransferProtos.PipelineAckProto proto = PipelineAckProto.newBuilder().setSeqno(0).addReply(CHECKSUM_OK).build();
        DataTransferProtos.PipelineAckProto newProto = PipelineAckProto.newBuilder().mergeFrom(proto).addFlag(PipelineAck.combineHeader(SUPPORTED, CHECKSUM_OK)).build();
        ByteArrayOutputStream oldAckBytes = new ByteArrayOutputStream();
        proto.writeDelimitedTo(oldAckBytes);
        PipelineAck oldAck = new PipelineAck();
        oldAck.readFields(new ByteArrayInputStream(oldAckBytes.toByteArray()));
        Assert.assertEquals(PipelineAck.combineHeader(DISABLED, CHECKSUM_OK), oldAck.getHeaderFlag(0));
        PipelineAck newAck = new PipelineAck();
        ByteArrayOutputStream newAckBytes = new ByteArrayOutputStream();
        newProto.writeDelimitedTo(newAckBytes);
        newAck.readFields(new ByteArrayInputStream(newAckBytes.toByteArray()));
        Assert.assertEquals(PipelineAck.combineHeader(SUPPORTED, CHECKSUM_OK), newAck.getHeaderFlag(0));
    }
}

