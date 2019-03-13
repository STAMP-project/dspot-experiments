/**
 * Copyright 2012-2019 the original author or authors.
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
package org.springframework.boot.devtools.tunnel.server;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import org.junit.Test;


/**
 * Tests for {@link SocketTargetServerConnection}.
 *
 * @author Phillip Webb
 */
public class SocketTargetServerConnectionTests {
    private static final int DEFAULT_TIMEOUT = 1000;

    private SocketTargetServerConnectionTests.MockServer server;

    private SocketTargetServerConnection connection;

    @Test
    public void readData() throws Exception {
        this.server.willSend("hello".getBytes());
        this.server.start();
        ByteChannel channel = this.connection.open(SocketTargetServerConnectionTests.DEFAULT_TIMEOUT);
        ByteBuffer buffer = ByteBuffer.allocate(5);
        channel.read(buffer);
        assertThat(buffer.array()).isEqualTo("hello".getBytes());
    }

    @Test
    public void writeData() throws Exception {
        this.server.expect("hello".getBytes());
        this.server.start();
        ByteChannel channel = this.connection.open(SocketTargetServerConnectionTests.DEFAULT_TIMEOUT);
        ByteBuffer buffer = ByteBuffer.wrap("hello".getBytes());
        channel.write(buffer);
        this.server.closeAndVerify();
    }

    @Test
    public void timeout() throws Exception {
        this.server.delay(1000);
        this.server.start();
        ByteChannel channel = this.connection.open(10);
        long startTime = System.currentTimeMillis();
        assertThatExceptionOfType(SocketTimeoutException.class).isThrownBy(() -> channel.read(ByteBuffer.allocate(5))).satisfies(( ex) -> {
            long runTime = (System.currentTimeMillis()) - startTime;
            assertThat(runTime).isGreaterThanOrEqualTo(10L);
            assertThat(runTime).isLessThan(10000L);
        });
    }

    private static class MockServer {
        private ServerSocketChannel serverSocket;

        private byte[] send;

        private byte[] expect;

        private int delay;

        private ByteBuffer actualRead;

        private SocketTargetServerConnectionTests.MockServer.ServerThread thread;

        MockServer() throws IOException {
            this.serverSocket = ServerSocketChannel.open();
            this.serverSocket.bind(new InetSocketAddress(0));
        }

        int getPort() {
            return this.serverSocket.socket().getLocalPort();
        }

        public void delay(int delay) {
            this.delay = delay;
        }

        public void willSend(byte[] send) {
            this.send = send;
        }

        public void expect(byte[] expect) {
            this.expect = expect;
        }

        public void start() {
            this.thread = new SocketTargetServerConnectionTests.MockServer.ServerThread();
            this.thread.start();
        }

        public void closeAndVerify() throws InterruptedException {
            close();
            assertThat(this.actualRead.array()).isEqualTo(this.expect);
        }

        public void close() throws InterruptedException {
            while (this.thread.isAlive()) {
                Thread.sleep(10);
            } 
        }

        private class ServerThread extends Thread {
            @Override
            public void run() {
                try {
                    SocketChannel channel = SocketTargetServerConnectionTests.MockServer.this.serverSocket.accept();
                    Thread.sleep(SocketTargetServerConnectionTests.MockServer.this.delay);
                    if ((SocketTargetServerConnectionTests.MockServer.this.send) != null) {
                        ByteBuffer buffer = ByteBuffer.wrap(SocketTargetServerConnectionTests.MockServer.this.send);
                        while (buffer.hasRemaining()) {
                            channel.write(buffer);
                        } 
                    }
                    if ((SocketTargetServerConnectionTests.MockServer.this.expect) != null) {
                        ByteBuffer buffer = ByteBuffer.allocate(SocketTargetServerConnectionTests.MockServer.this.expect.length);
                        while (buffer.hasRemaining()) {
                            channel.read(buffer);
                        } 
                        SocketTargetServerConnectionTests.MockServer.this.actualRead = buffer;
                    }
                    channel.close();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}

