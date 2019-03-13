/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.devtools.tunnel.client;


import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.channels.WritableByteChannel;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link TunnelClient}.
 *
 * @author Phillip Webb
 */
public class TunnelClientTests {
    private TunnelClientTests.MockTunnelConnection tunnelConnection = new TunnelClientTests.MockTunnelConnection();

    @Test
    public void listenPortMustNotBeNegative() {
        assertThatIllegalArgumentException().isThrownBy(() -> new TunnelClient((-5), this.tunnelConnection)).withMessageContaining("ListenPort must be greater than or equal to 0");
    }

    @Test
    public void tunnelConnectionMustNotBeNull() {
        assertThatIllegalArgumentException().isThrownBy(() -> new TunnelClient(1, null)).withMessageContaining("TunnelConnection must not be null");
    }

    @Test
    public void typicalTraffic() throws Exception {
        TunnelClient client = new TunnelClient(0, this.tunnelConnection);
        int port = client.start();
        SocketChannel channel = SocketChannel.open(new InetSocketAddress(port));
        channel.write(ByteBuffer.wrap("hello".getBytes()));
        ByteBuffer buffer = ByteBuffer.allocate(5);
        channel.read(buffer);
        channel.close();
        this.tunnelConnection.verifyWritten("hello");
        assertThat(new String(buffer.array())).isEqualTo("olleh");
    }

    @Test
    public void socketChannelClosedTriggersTunnelClose() throws Exception {
        TunnelClient client = new TunnelClient(0, this.tunnelConnection);
        int port = client.start();
        SocketChannel channel = SocketChannel.open(new InetSocketAddress(port));
        Thread.sleep(200);
        channel.close();
        client.getServerThread().stopAcceptingConnections();
        client.getServerThread().join(2000);
        assertThat(this.tunnelConnection.getOpenedTimes()).isEqualTo(1);
        assertThat(this.tunnelConnection.isOpen()).isFalse();
    }

    @Test
    public void stopTriggersTunnelClose() throws Exception {
        TunnelClient client = new TunnelClient(0, this.tunnelConnection);
        int port = client.start();
        SocketChannel channel = SocketChannel.open(new InetSocketAddress(port));
        Thread.sleep(200);
        client.stop();
        assertThat(this.tunnelConnection.getOpenedTimes()).isEqualTo(1);
        assertThat(this.tunnelConnection.isOpen()).isFalse();
        assertThat(channel.read(ByteBuffer.allocate(1))).isEqualTo((-1));
    }

    @Test
    public void addListener() throws Exception {
        TunnelClient client = new TunnelClient(0, this.tunnelConnection);
        TunnelClientListener listener = Mockito.mock(TunnelClientListener.class);
        client.addListener(listener);
        int port = client.start();
        SocketChannel channel = SocketChannel.open(new InetSocketAddress(port));
        Thread.sleep(200);
        channel.close();
        client.getServerThread().stopAcceptingConnections();
        client.getServerThread().join(2000);
        Mockito.verify(listener).onOpen(ArgumentMatchers.any(SocketChannel.class));
        Mockito.verify(listener).onClose(ArgumentMatchers.any(SocketChannel.class));
    }

    private static class MockTunnelConnection implements TunnelConnection {
        private final ByteArrayOutputStream written = new ByteArrayOutputStream();

        private boolean open;

        private int openedTimes;

        @Override
        public WritableByteChannel open(WritableByteChannel incomingChannel, Closeable closeable) {
            (this.openedTimes)++;
            this.open = true;
            return new TunnelClientTests.MockTunnelConnection.TunnelChannel(incomingChannel, closeable);
        }

        public void verifyWritten(String expected) {
            verifyWritten(expected.getBytes());
        }

        public void verifyWritten(byte[] expected) {
            synchronized(this.written) {
                assertThat(this.written.toByteArray()).isEqualTo(expected);
                this.written.reset();
            }
        }

        public boolean isOpen() {
            return this.open;
        }

        public int getOpenedTimes() {
            return this.openedTimes;
        }

        private class TunnelChannel implements WritableByteChannel {
            private final WritableByteChannel incomingChannel;

            private final Closeable closeable;

            TunnelChannel(WritableByteChannel incomingChannel, Closeable closeable) {
                this.incomingChannel = incomingChannel;
                this.closeable = closeable;
            }

            @Override
            public boolean isOpen() {
                return TunnelClientTests.MockTunnelConnection.this.open;
            }

            @Override
            public void close() throws IOException {
                TunnelClientTests.MockTunnelConnection.this.open = false;
                this.closeable.close();
            }

            @Override
            public int write(ByteBuffer src) throws IOException {
                int remaining = src.remaining();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                Channels.newChannel(stream).write(src);
                byte[] bytes = stream.toByteArray();
                synchronized(TunnelClientTests.MockTunnelConnection.this.written) {
                    TunnelClientTests.MockTunnelConnection.this.written.write(bytes);
                }
                byte[] reversed = new byte[bytes.length];
                for (int i = 0; i < (reversed.length); i++) {
                    reversed[i] = bytes[(((bytes.length) - 1) - i)];
                }
                this.incomingChannel.write(ByteBuffer.wrap(reversed));
                return remaining;
            }
        }
    }
}

