/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.hdfs.server.datanode;


import BlockConstructionStage.PIPELINE_SETUP_CREATE;
import DataChecksum.Type.NULL;
import DatanodeInfo.AdminStates.NORMAL;
import StorageType.RAM_DISK;
import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.StorageType;
import org.apache.hadoop.hdfs.protocol.datatransfer.sasl.DataEncryptionKeyFactory;
import org.apache.hadoop.hdfs.protocol.datatransfer.sasl.SaslDataTransferClient;
import org.apache.hadoop.hdfs.security.token.block.BlockTokenIdentifier;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsDatasetSpi;
import org.apache.hadoop.hdfs.server.datanode.fsdataset.FsVolumeSpi;
import org.apache.hadoop.hdfs.server.protocol.DatanodeRegistration;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.net.ServerSocketUtil;
import org.apache.hadoop.security.token.Token;
import org.apache.hadoop.util.DataChecksum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Mock-based unit test to verify that DataXceiver does not fail when no
 * storageId or targetStorageTypes are passed - as is the case in Hadoop 2.x.
 */
public class TestDataXceiverBackwardsCompat {
    @Rule
    public Timeout timeout = new Timeout(60000);

    /**
     * Used for mocking DataNode. Mockito does not provide a way to mock
     * properties (like data or saslClient) so we have to manually set up mocks
     * of those properties inside our own class.
     */
    public class NullDataNode extends DataNode {
        public NullDataNode(Configuration conf, OutputStream out, int port) throws Exception {
            super(conf);
            data = ((FsDatasetSpi<FsVolumeSpi>) (Mockito.mock(FsDatasetSpi.class)));
            saslClient = Mockito.mock(SaslDataTransferClient.class);
            IOStreamPair pair = new IOStreamPair(null, out);
            Mockito.doReturn(pair).when(saslClient).socketSend(ArgumentMatchers.any(Socket.class), ArgumentMatchers.any(OutputStream.class), ArgumentMatchers.any(InputStream.class), ArgumentMatchers.any(DataEncryptionKeyFactory.class), ArgumentMatchers.any(), ArgumentMatchers.any(DatanodeID.class));
            Mockito.doReturn(Mockito.mock(ReplicaHandler.class)).when(data).createTemporary(ArgumentMatchers.any(StorageType.class), ArgumentMatchers.any(String.class), ArgumentMatchers.any(ExtendedBlock.class), ArgumentMatchers.anyBoolean());
            new Thread(new TestDataXceiverBackwardsCompat.NullDataNode.NullServer(port)).start();
        }

        @Override
        public DatanodeRegistration getDNRegistrationForBP(String bpid) throws IOException {
            return null;
        }

        @Override
        public Socket newSocket() throws IOException {
            return new Socket();
        }

        /**
         * Class for accepting incoming an incoming connection. Does not read
         * data or repeat in any way: simply allows a single client to connect to
         * a local URL.
         */
        private class NullServer implements Runnable {
            private ServerSocket serverSocket;

            NullServer(int port) throws IOException {
                serverSocket = new ServerSocket(port);
            }

            @Override
            public void run() {
                try {
                    serverSocket.accept();
                    serverSocket.close();
                    LOG.info("Client connection accepted by NullServer");
                } catch (Exception e) {
                    LOG.info(((("Exception in NullServer: " + e) + "; ") + (e.getMessage())));
                }
            }
        }
    }

    @Test
    public void testBackwardsCompat() throws Exception {
        Peer peer = Mockito.mock(Peer.class);
        Mockito.doReturn("").when(peer).getRemoteAddressString();
        Configuration conf = new Configuration();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int port = ServerSocketUtil.getPort(1234, 10);
        DataNode dataNode = new TestDataXceiverBackwardsCompat.NullDataNode(conf, out, port);
        DataXceiverServer server = new DataXceiverServer(Mockito.mock(PeerServer.class), conf, dataNode);
        DataXceiver xceiver = Mockito.spy(DataXceiver.create(peer, dataNode, server));
        BlockReceiver mockBlockReceiver = Mockito.mock(BlockReceiver.class);
        Mockito.doReturn(Mockito.mock(Replica.class)).when(mockBlockReceiver).getReplica();
        DatanodeInfo[] targets = new DatanodeInfo[]{ Mockito.mock(DatanodeInfo.class) };
        Mockito.doReturn(("localhost:" + port)).when(targets[0]).getXferAddr(true);
        Mockito.doReturn(("127.0.0.1:" + port)).when(targets[0]).getXferAddr(false);
        StorageType[] storageTypes = new StorageType[]{ StorageType.RAM_DISK };
        Mockito.doReturn(mockBlockReceiver).when(xceiver).getBlockReceiver(ArgumentMatchers.any(ExtendedBlock.class), ArgumentMatchers.any(StorageType.class), ArgumentMatchers.any(), ArgumentMatchers.anyString(), ArgumentMatchers.any(), ArgumentMatchers.any(BlockConstructionStage.class), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyLong(), ArgumentMatchers.anyString(), ArgumentMatchers.any(DatanodeInfo.class), ArgumentMatchers.any(DataNode.class), ArgumentMatchers.any(DataChecksum.class), ArgumentMatchers.any(CachingStrategy.class), ArgumentCaptor.forClass(Boolean.class).capture(), ArgumentMatchers.anyBoolean(), ArgumentMatchers.any());
        Token<BlockTokenIdentifier> token = ((Token<BlockTokenIdentifier>) (Mockito.mock(Token.class)));
        Mockito.doReturn("".getBytes()).when(token).getIdentifier();
        Mockito.doReturn("".getBytes()).when(token).getPassword();
        Mockito.doReturn(new Text("")).when(token).getKind();
        Mockito.doReturn(new Text("")).when(token).getService();
        DataChecksum checksum = Mockito.mock(DataChecksum.class);
        Mockito.doReturn(NULL).when(checksum).getChecksumType();
        DatanodeInfo datanodeInfo = Mockito.mock(DatanodeInfo.class);
        Mockito.doReturn("localhost").when(datanodeInfo).getHostName();
        Mockito.doReturn(ByteString.copyFromUtf8("localhost")).when(datanodeInfo).getHostNameBytes();
        Mockito.doReturn("127.0.0.1").when(datanodeInfo).getIpAddr();
        Mockito.doReturn(ByteString.copyFromUtf8("127.0.0.1")).when(datanodeInfo).getIpAddrBytes();
        Mockito.doReturn(NORMAL).when(datanodeInfo).getAdminState();
        final String uuid = UUID.randomUUID().toString();
        Mockito.doReturn(uuid).when(datanodeInfo).getDatanodeUuid();
        Mockito.doReturn(ByteString.copyFromUtf8(uuid)).when(datanodeInfo).getDatanodeUuidBytes();
        Exception storedException = null;
        try {
            xceiver.writeBlock(new ExtendedBlock("Dummy-pool", 0L), RAM_DISK, token, "Dummy-Client", targets, storageTypes, datanodeInfo, PIPELINE_SETUP_CREATE, 0, 0, 0, 0, checksum, CachingStrategy.newDefaultStrategy(), false, false, new boolean[0], null, new String[0]);
        } catch (Exception e) {
            // Not enough things have been mocked for this to complete without
            // exceptions, but we want to make sure we can at least get as far as
            // sending data to the server with null values for storageId and
            // targetStorageTypes.
            storedException = e;
        }
        byte[] output = out.toByteArray();
        if ((output.length) == 0) {
            if (storedException == null) {
                failWithException(("No output written, but no exception either (this " + "shouldn't happen"), storedException);
            } else {
                failWithException("Exception occurred before anything was written", storedException);
            }
        }
    }
}

