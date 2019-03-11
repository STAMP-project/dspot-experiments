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
package org.apache.flink.runtime.io.network.netty;


import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.runtime.io.network.ConnectionID;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelHandler;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelHandlerContext;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelOutboundHandlerAdapter;
import org.apache.flink.shaded.netty4.io.netty.channel.ChannelPromise;
import org.apache.flink.util.NetUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


@Ignore
public class PartitionRequestClientFactoryTest {
    private static final int SERVER_PORT = NetUtils.getAvailablePort();

    @Test
    public void testResourceReleaseAfterInterruptedConnect() throws Exception {
        // Latch to synchronize on the connect call.
        final CountDownLatch syncOnConnect = new CountDownLatch(1);
        final Tuple2<NettyServer, NettyClient> netty = PartitionRequestClientFactoryTest.createNettyServerAndClient(new NettyProtocol(null, null, true) {
            @Override
            public ChannelHandler[] getServerChannelHandlers() {
                return new ChannelHandler[0];
            }

            @Override
            public ChannelHandler[] getClientChannelHandlers() {
                return new ChannelHandler[]{ new PartitionRequestClientFactoryTest.CountDownLatchOnConnectHandler(syncOnConnect) };
            }
        });
        final NettyServer server = netty.f0;
        final NettyClient client = netty.f1;
        final PartitionRequestClientFactoryTest.UncaughtTestExceptionHandler exceptionHandler = new PartitionRequestClientFactoryTest.UncaughtTestExceptionHandler();
        try {
            final PartitionRequestClientFactory factory = new PartitionRequestClientFactory(client);
            final Thread connect = new Thread(new Runnable() {
                @Override
                public void run() {
                    ConnectionID serverAddress = null;
                    try {
                        serverAddress = PartitionRequestClientFactoryTest.createServerConnectionID(0);
                        // This triggers a connect
                        factory.createPartitionRequestClient(serverAddress);
                    } catch (Throwable t) {
                        if (serverAddress != null) {
                            factory.closeOpenChannelConnections(serverAddress);
                            Thread.getDefaultUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), t);
                        } else {
                            t.printStackTrace();
                            Assert.fail("Could not create RemoteAddress for server.");
                        }
                    }
                }
            });
            connect.setUncaughtExceptionHandler(exceptionHandler);
            connect.start();
            // Wait on the connect
            syncOnConnect.await();
            connect.interrupt();
            connect.join();
            // Make sure that after a failed connect all resources are cleared.
            Assert.assertEquals(0, factory.getNumberOfActiveClients());
            // Make sure that the interrupt exception is not swallowed
            Assert.assertTrue(((exceptionHandler.getErrors().size()) > 0));
        } finally {
            if (server != null) {
                server.shutdown();
            }
            if (client != null) {
                client.shutdown();
            }
        }
    }

    private static class CountDownLatchOnConnectHandler extends ChannelOutboundHandlerAdapter {
        private final CountDownLatch syncOnConnect;

        public CountDownLatchOnConnectHandler(CountDownLatch syncOnConnect) {
            this.syncOnConnect = syncOnConnect;
        }

        @Override
        public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
            syncOnConnect.countDown();
        }
    }

    private static class UncaughtTestExceptionHandler implements Thread.UncaughtExceptionHandler {
        private final List<Throwable> errors = new ArrayList<Throwable>(1);

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            errors.add(e);
        }

        private List<Throwable> getErrors() {
            return errors;
        }
    }
}

