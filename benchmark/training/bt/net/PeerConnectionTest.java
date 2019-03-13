/**
 * Copyright (c) 2016?2017 Andrei Tomashpolskiy and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package bt.net;


import bt.metainfo.TorrentId;
import bt.net.pipeline.IChannelPipelineFactory;
import bt.protocol.Bitfield;
import bt.protocol.EncodingContext;
import bt.protocol.Handshake;
import bt.protocol.InvalidMessageException;
import bt.protocol.Message;
import bt.protocol.Request;
import bt.test.protocol.ProtocolTest;
import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


// TODO: rewrite or delete
@Ignore
public class PeerConnectionTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(PeerConnectionTest.class);

    private static final ProtocolTest TEST = ProtocolTest.forBittorrentProtocol().build();

    private static final int BUFFER_SIZE = 2 << 6;

    private IChannelPipelineFactory channelPipelineFactory;

    private PeerConnectionTest.Server server;

    private SocketChannel clientChannel;

    @Test
    public void testConnection() throws InvalidMessageException, IOException {
        Peer peer = Mockito.mock(Peer.class);
        PeerConnection connection = createConnection(peer, clientChannel, PeerConnectionTest.TEST.getProtocol());
        Message message;
        server.writeMessage(new Handshake(new byte[8], TorrentId.fromBytes(new byte[20]), PeerId.fromBytes(new byte[20])));
        message = connection.readMessageNow();
        Assert.assertNotNull(message);
        Assert.assertEquals(Handshake.class, message.getClass());
        server.writeMessage(new Bitfield(new byte[2 << 3]));
        message = connection.readMessageNow();
        Assert.assertNotNull(message);
        Assert.assertEquals(Bitfield.class, message.getClass());
        Assert.assertEquals((2 << 3), getBitfield().length);
        server.writeMessage(new Request(1, 2, 3));
        message = connection.readMessageNow();
        Assert.assertNotNull(message);
        Assert.assertEquals(Request.class, message.getClass());
        Assert.assertEquals(1, getPieceIndex());
        Assert.assertEquals(2, getOffset());
        Assert.assertEquals(3, getLength());
    }

    private class Server implements Closeable , Runnable {
        private ServerSocketChannel channel;

        private volatile SocketChannel clientSocket;

        private final Object lock;

        private volatile boolean connected;

        Server(ServerSocketChannel channel) {
            this.channel = channel;
            lock = new Object();
        }

        @Override
        public void run() {
            try {
                synchronized(lock) {
                    clientSocket = channel.accept();
                    connected = true;
                }
            } catch (IOException e) {
                throw new RuntimeException("Unexpected I/O error", e);
            }
        }

        public void waitUntilConnected() {
            while (!(connected)) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException("interrupted");
                }
            } 
        }

        public void writeMessage(Message message) throws InvalidMessageException, IOException {
            ByteBuffer buffer = ByteBuffer.allocate(PeerConnectionTest.BUFFER_SIZE);
            Assert.assertTrue("Protocol failed to serialize message", PeerConnectionTest.TEST.getProtocol().encode(new EncodingContext(null), message, buffer));
            buffer.flip();
            synchronized(lock) {
                clientSocket.write(buffer);
            }
        }

        @Override
        public void close() {
            if ((clientSocket) != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    PeerConnectionTest.LOGGER.warn("Failed to close client channel", e);
                }
            }
            try {
                channel.close();
            } catch (IOException e) {
                PeerConnectionTest.LOGGER.warn("Failed to close server channel", e);
            }
        }
    }
}

