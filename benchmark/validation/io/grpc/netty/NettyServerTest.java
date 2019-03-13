/**
 * Copyright 2016 The gRPC Authors
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
package io.grpc.netty;


import ChannelOption.WRITE_BUFFER_WATER_MARK;
import com.google.common.util.concurrent.SettableFuture;
import io.grpc.Attributes;
import io.grpc.InternalChannelz;
import io.grpc.InternalChannelz.SocketStats;
import io.grpc.InternalInstrumented;
import io.grpc.Metadata;
import io.grpc.ServerStreamTracer;
import io.grpc.internal.ServerListener;
import io.grpc.internal.ServerStream;
import io.grpc.internal.ServerTransport;
import io.grpc.internal.ServerTransportListener;
import io.grpc.internal.TransportTracer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.WriteBufferWaterMark;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public class NettyServerTest {
    private final InternalChannelz channelz = new InternalChannelz();

    @Test
    public void getPort() throws Exception {
        InetSocketAddress addr = new InetSocketAddress(0);
        NettyServer ns = // no boss group
        // no event group
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        new NettyServer(addr, NioServerSocketChannel.class, new HashMap<ChannelOption<?>, Object>(), null, null, ProtocolNegotiators.plaintext(), Collections.<ServerStreamTracer.Factory>emptyList(), TransportTracer.getDefaultFactory(), 1, 1, 1, 1, 1, 1, 1, 1, 1, true, 0, channelz);
        ns.start(new ServerListener() {
            @Override
            public ServerTransportListener transportCreated(ServerTransport transport) {
                return new NettyServerTest.NoopServerTransportListener();
            }

            @Override
            public void serverShutdown() {
            }
        });
        // Check that we got an actual port.
        assertThat(((InetSocketAddress) (ns.getListenSocketAddress())).getPort()).isGreaterThan(0);
        // Cleanup
        ns.shutdown();
    }

    @Test
    public void getPort_notStarted() throws Exception {
        InetSocketAddress addr = new InetSocketAddress(0);
        NettyServer ns = // no boss group
        // no event group
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        new NettyServer(addr, NioServerSocketChannel.class, new HashMap<ChannelOption<?>, Object>(), null, null, ProtocolNegotiators.plaintext(), Collections.<ServerStreamTracer.Factory>emptyList(), TransportTracer.getDefaultFactory(), 1, 1, 1, 1, 1, 1, 1, 1, 1, true, 0, channelz);
        assertThat(ns.getListenSocketAddress()).isEqualTo(addr);
    }

    @Test(timeout = 60000)
    public void childChannelOptions() throws Exception {
        final int originalLowWaterMark = 2097169;
        final int originalHighWaterMark = 2097211;
        Map<ChannelOption<?>, Object> channelOptions = new HashMap<>();
        channelOptions.put(WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(originalLowWaterMark, originalHighWaterMark));
        final AtomicInteger lowWaterMark = new AtomicInteger(0);
        final AtomicInteger highWaterMark = new AtomicInteger(0);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        InetSocketAddress addr = new InetSocketAddress(0);
        NettyServer ns = // no boss group
        // no event group
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        new NettyServer(addr, NioServerSocketChannel.class, channelOptions, null, null, ProtocolNegotiators.plaintext(), Collections.<ServerStreamTracer.Factory>emptyList(), TransportTracer.getDefaultFactory(), 1, 1, 1, 1, 1, 1, 1, 1, 1, true, 0, channelz);
        ns.start(new ServerListener() {
            @Override
            public ServerTransportListener transportCreated(ServerTransport transport) {
                Channel channel = channel();
                WriteBufferWaterMark writeBufferWaterMark = channel.config().getOption(WRITE_BUFFER_WATER_MARK);
                lowWaterMark.set(writeBufferWaterMark.low());
                highWaterMark.set(writeBufferWaterMark.high());
                countDownLatch.countDown();
                return new NettyServerTest.NoopServerTransportListener();
            }

            @Override
            public void serverShutdown() {
            }
        });
        Socket socket = new Socket();
        /* timeout= */
        socket.connect(ns.getListenSocketAddress(), 8000);
        countDownLatch.await();
        socket.close();
        assertThat(lowWaterMark.get()).isEqualTo(originalLowWaterMark);
        assertThat(highWaterMark.get()).isEqualTo(originalHighWaterMark);
        ns.shutdown();
    }

    @Test
    public void channelzListenSocket() throws Exception {
        InetSocketAddress addr = new InetSocketAddress(0);
        NettyServer ns = // no boss group
        // no event group
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        // ignore
        new NettyServer(addr, NioServerSocketChannel.class, new HashMap<ChannelOption<?>, Object>(), null, null, ProtocolNegotiators.plaintext(), Collections.<ServerStreamTracer.Factory>emptyList(), TransportTracer.getDefaultFactory(), 1, 1, 1, 1, 1, 1, 1, 1, 1, true, 0, channelz);
        final SettableFuture<Void> shutdownCompleted = SettableFuture.create();
        ns.start(new ServerListener() {
            @Override
            public ServerTransportListener transportCreated(ServerTransport transport) {
                return new NettyServerTest.NoopServerTransportListener();
            }

            @Override
            public void serverShutdown() {
                shutdownCompleted.set(null);
            }
        });
        assertThat(((InetSocketAddress) (ns.getListenSocketAddress())).getPort()).isGreaterThan(0);
        InternalInstrumented<SocketStats> listenSocket = ns.getListenSocketStats();
        Assert.assertSame(listenSocket, channelz.getSocket(InternalChannelz.id(listenSocket)));
        // very basic sanity check of the contents
        SocketStats socketStats = listenSocket.getStats().get();
        Assert.assertEquals(ns.getListenSocketAddress(), socketStats.local);
        Assert.assertNull(socketStats.remote);
        // TODO(zpencer): uncomment when sock options are exposed
        // by default, there are some socket options set on the listen socket
        // assertThat(socketStats.socketOptions.additional).isNotEmpty();
        // Cleanup
        ns.shutdown();
        shutdownCompleted.get();
        // listen socket is removed
        Assert.assertNull(channelz.getSocket(InternalChannelz.id(listenSocket)));
    }

    private static class NoopServerTransportListener implements ServerTransportListener {
        @Override
        public void streamCreated(ServerStream stream, String method, Metadata headers) {
        }

        @Override
        public Attributes transportReady(Attributes attributes) {
            return attributes;
        }

        @Override
        public void transportTerminated() {
        }
    }
}

