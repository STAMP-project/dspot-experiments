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


import BlockConstructionStage.PIPELINE_CLOSE;
import CreateFlag.CREATE;
import CreateFlag.NO_LOCAL_WRITE;
import HdfsConstants.DatanodeReportType.LIVE;
import StreamCapability.HFLUSH;
import StreamCapability.HSYNC;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CreateFlag;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsTracer;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.hadoop.hdfs.DataStreamer.LastExceptionInStreamer;
import org.apache.hadoop.hdfs.client.HdfsClientConfigKeys;
import org.apache.hadoop.hdfs.client.impl.DfsClientConf;
import org.apache.hadoop.hdfs.protocol.BlockListAsLongs;
import org.apache.hadoop.hdfs.protocol.DatanodeInfo;
import org.apache.hadoop.hdfs.protocol.ExtendedBlock;
import org.apache.hadoop.hdfs.protocol.HdfsFileStatus;
import org.apache.hadoop.hdfs.protocol.datatransfer.PacketReceiver;
import org.apache.hadoop.hdfs.server.blockmanagement.BlockManager;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeDescriptor;
import org.apache.hadoop.hdfs.server.blockmanagement.DatanodeManager;
import org.apache.hadoop.hdfs.server.protocol.DatanodeStorage;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.test.Whitebox;
import org.apache.htrace.core.SpanId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TestDFSOutputStream {
    static MiniDFSCluster cluster;

    /**
     * The close() method of DFSOutputStream should never throw the same exception
     * twice. See HDFS-5335 for details.
     */
    @Test
    public void testCloseTwice() throws IOException {
        DistributedFileSystem fs = TestDFSOutputStream.cluster.getFileSystem();
        FSDataOutputStream os = fs.create(new Path("/test"));
        DFSOutputStream dos = ((DFSOutputStream) (Whitebox.getInternalState(os, "wrappedStream")));
        DataStreamer streamer = ((DataStreamer) (Whitebox.getInternalState(dos, "streamer")));
        @SuppressWarnings("unchecked")
        LastExceptionInStreamer ex = ((LastExceptionInStreamer) (Whitebox.getInternalState(streamer, "lastException")));
        Throwable thrown = ((Throwable) (Whitebox.getInternalState(ex, "thrown")));
        Assert.assertNull(thrown);
        dos.close();
        IOException dummy = new IOException("dummy");
        ex.set(dummy);
        try {
            dos.close();
        } catch (IOException e) {
            Assert.assertEquals(e, dummy);
        }
        thrown = ((Throwable) (Whitebox.getInternalState(ex, "thrown")));
        Assert.assertNull(thrown);
        dos.close();
    }

    /**
     * The computePacketChunkSize() method of DFSOutputStream should set the actual
     * packet size < 64kB. See HDFS-7308 for details.
     */
    @Test
    public void testComputePacketChunkSize() throws Exception {
        DistributedFileSystem fs = TestDFSOutputStream.cluster.getFileSystem();
        FSDataOutputStream os = fs.create(new Path("/test"));
        DFSOutputStream dos = ((DFSOutputStream) (Whitebox.getInternalState(os, "wrappedStream")));
        final int packetSize = 64 * 1024;
        final int bytesPerChecksum = 512;
        Method method = dos.getClass().getDeclaredMethod("computePacketChunkSize", int.class, int.class);
        method.setAccessible(true);
        method.invoke(dos, packetSize, bytesPerChecksum);
        Field field = dos.getClass().getDeclaredField("packetSize");
        field.setAccessible(true);
        Assert.assertTrue(((((Integer) (field.get(dos))) + 33) < packetSize));
        // If PKT_MAX_HEADER_LEN is 257, actual packet size come to over 64KB
        // without a fix on HDFS-7308.
        Assert.assertTrue(((((Integer) (field.get(dos))) + 257) < packetSize));
    }

    /**
     * This tests preventing overflows of package size and bodySize.
     * <p>
     * See also https://issues.apache.org/jira/browse/HDFS-11608.
     * </p>
     *
     * @throws IOException
     * 		
     * @throws SecurityException
     * 		
     * @throws NoSuchFieldException
     * 		
     * @throws InvocationTargetException
     * 		
     * @throws IllegalArgumentException
     * 		
     * @throws IllegalAccessException
     * 		
     * @throws NoSuchMethodException
     * 		
     */
    @Test(timeout = 60000)
    public void testPreventOverflow() throws IOException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, NoSuchMethodException, SecurityException, InvocationTargetException {
        final int defaultWritePacketSize = HdfsClientConfigKeys.DFS_CLIENT_WRITE_PACKET_SIZE_DEFAULT;
        int configuredWritePacketSize = defaultWritePacketSize;
        int finalWritePacketSize = defaultWritePacketSize;
        /* test default WritePacketSize, e.g. 64*1024 */
        runAdjustChunkBoundary(configuredWritePacketSize, finalWritePacketSize);
        /* test large WritePacketSize, e.g. 1G */
        configuredWritePacketSize = (1000 * 1024) * 1024;
        finalWritePacketSize = PacketReceiver.MAX_PACKET_SIZE;
        runAdjustChunkBoundary(configuredWritePacketSize, finalWritePacketSize);
    }

    @Test
    public void testCongestionBackoff() throws IOException {
        DfsClientConf dfsClientConf = Mockito.mock(DfsClientConf.class);
        DFSClient client = Mockito.mock(DFSClient.class);
        Mockito.when(client.getConf()).thenReturn(dfsClientConf);
        Mockito.when(client.getTracer()).thenReturn(FsTracer.get(new Configuration()));
        client.clientRunning = true;
        DataStreamer stream = new DataStreamer(Mockito.mock(HdfsFileStatus.class), Mockito.mock(ExtendedBlock.class), client, "foo", null, null, null, null, null, null);
        DataOutputStream blockStream = Mockito.mock(DataOutputStream.class);
        Mockito.doThrow(new IOException()).when(blockStream).flush();
        Whitebox.setInternalState(stream, "blockStream", blockStream);
        Whitebox.setInternalState(stream, "stage", PIPELINE_CLOSE);
        @SuppressWarnings("unchecked")
        LinkedList<DFSPacket> dataQueue = ((LinkedList<DFSPacket>) (Whitebox.getInternalState(stream, "dataQueue")));
        @SuppressWarnings("unchecked")
        ArrayList<DatanodeInfo> congestedNodes = ((ArrayList<DatanodeInfo>) (Whitebox.getInternalState(stream, "congestedNodes")));
        congestedNodes.add(Mockito.mock(DatanodeInfo.class));
        DFSPacket packet = Mockito.mock(DFSPacket.class);
        Mockito.when(packet.getTraceParents()).thenReturn(new SpanId[]{  });
        dataQueue.add(packet);
        stream.run();
        Assert.assertTrue(congestedNodes.isEmpty());
    }

    @Test
    public void testNoLocalWriteFlag() throws IOException {
        DistributedFileSystem fs = TestDFSOutputStream.cluster.getFileSystem();
        EnumSet<CreateFlag> flags = EnumSet.of(NO_LOCAL_WRITE, CREATE);
        BlockManager bm = TestDFSOutputStream.cluster.getNameNode().getNamesystem().getBlockManager();
        DatanodeManager dm = bm.getDatanodeManager();
        try (FSDataOutputStream os = fs.create(new Path("/test-no-local"), FsPermission.getDefault(), flags, 512, ((short) (2)), 512, null)) {
            // Inject a DatanodeManager that returns one DataNode as local node for
            // the client.
            DatanodeManager spyDm = Mockito.spy(dm);
            DatanodeDescriptor dn1 = dm.getDatanodeListForReport(LIVE).get(0);
            Mockito.doReturn(dn1).when(spyDm).getDatanodeByHost("127.0.0.1");
            Whitebox.setInternalState(bm, "datanodeManager", spyDm);
            byte[] buf = new byte[512 * 16];
            new Random().nextBytes(buf);
            os.write(buf);
        } finally {
            Whitebox.setInternalState(bm, "datanodeManager", dm);
        }
        TestDFSOutputStream.cluster.triggerBlockReports();
        final String bpid = TestDFSOutputStream.cluster.getNamesystem().getBlockPoolId();
        // Total number of DataNodes is 3.
        Assert.assertEquals(3, TestDFSOutputStream.cluster.getAllBlockReports(bpid).size());
        int numDataNodesWithData = 0;
        for (Map<DatanodeStorage, BlockListAsLongs> dnBlocks : TestDFSOutputStream.cluster.getAllBlockReports(bpid)) {
            for (BlockListAsLongs blocks : dnBlocks.values()) {
                if ((blocks.getNumberOfBlocks()) > 0) {
                    numDataNodesWithData++;
                    break;
                }
            }
        }
        // Verify that only one DN has no data.
        Assert.assertEquals(1, (3 - numDataNodesWithData));
    }

    @Test
    public void testEndLeaseCall() throws Exception {
        Configuration conf = new Configuration();
        DFSClient client = new DFSClient(TestDFSOutputStream.cluster.getNameNode(0).getNameNodeAddress(), conf);
        DFSClient spyClient = Mockito.spy(client);
        DFSOutputStream dfsOutputStream = spyClient.create("/file2", FsPermission.getFileDefault(), EnumSet.of(CREATE), ((short) (3)), 1024, null, 1024, null);
        DFSOutputStream spyDFSOutputStream = Mockito.spy(dfsOutputStream);
        spyDFSOutputStream.closeThreads(ArgumentMatchers.anyBoolean());
        Mockito.verify(spyClient, Mockito.times(1)).endFileLease(ArgumentMatchers.anyLong());
    }

    @Test
    public void testStreamFlush() throws Exception {
        FileSystem fs = TestDFSOutputStream.cluster.getFileSystem();
        FSDataOutputStream os = fs.create(new Path("/normal-file"));
        // Verify output stream supports hsync() and hflush().
        Assert.assertTrue("DFSOutputStream should support hflush()!", os.hasCapability(HFLUSH.getValue()));
        Assert.assertTrue("DFSOutputStream should support hsync()!", os.hasCapability(HSYNC.getValue()));
        byte[] bytes = new byte[1024];
        InputStream is = new ByteArrayInputStream(bytes);
        IOUtils.copyBytes(is, os, bytes.length);
        os.hflush();
        IOUtils.copyBytes(is, os, bytes.length);
        os.hsync();
        os.close();
    }
}

