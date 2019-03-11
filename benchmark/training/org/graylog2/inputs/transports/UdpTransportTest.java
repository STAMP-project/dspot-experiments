/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.inputs.transports;


import ChannelOption.RCVBUF_ALLOCATOR;
import ChannelOption.SO_RCVBUF;
import NettyTransport.CK_BIND_ADDRESS;
import NettyTransport.CK_NUMBER_WORKER_THREADS;
import NettyTransport.CK_PORT;
import NettyTransport.CK_RECV_BUFFER_SIZE;
import ThroughputCounter.READ_BYTES_TOTAL;
import UdpTransport.Config;
import com.codahale.metrics.Gauge;
import com.github.joschi.jadconfig.util.Size;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;
import com.google.common.util.concurrent.Uninterruptibles;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.SystemUtils;
import org.graylog2.inputs.transports.netty.EventLoopGroupFactory;
import org.graylog2.plugin.LocalMetricRegistry;
import org.graylog2.plugin.configuration.Configuration;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.inputs.MessageInput;
import org.graylog2.plugin.inputs.util.ThroughputCounter;
import org.junit.Assume;
import org.junit.Test;
import org.mockito.Mockito;


public class UdpTransportTest {
    private static final String BIND_ADDRESS = "127.0.0.1";

    private static final int PORT = 0;

    private static final int RECV_BUFFER_SIZE = 1024;

    private static final ImmutableMap<String, Object> CONFIG_SOURCE = ImmutableMap.of(CK_BIND_ADDRESS, UdpTransportTest.BIND_ADDRESS, CK_PORT, UdpTransportTest.PORT, CK_RECV_BUFFER_SIZE, UdpTransportTest.RECV_BUFFER_SIZE, CK_NUMBER_WORKER_THREADS, 1);

    private static final Configuration CONFIGURATION = new Configuration(UdpTransportTest.CONFIG_SOURCE);

    private final NettyTransportConfiguration nettyTransportConfiguration = new NettyTransportConfiguration("nio", "jdk", 1);

    private UdpTransport udpTransport;

    private EventLoopGroup eventLoopGroup;

    private EventLoopGroupFactory eventLoopGroupFactory;

    private ThroughputCounter throughputCounter;

    private LocalMetricRegistry localMetricRegistry;

    @Test
    public void transportReceivesDataSmallerThanRecvBufferSize() throws Exception {
        final UdpTransportTest.CountingChannelUpstreamHandler handler = new UdpTransportTest.CountingChannelUpstreamHandler();
        final UdpTransport transport = launchTransportForBootStrapTest(handler);
        await().atMost(5, TimeUnit.SECONDS).until(() -> (transport.getLocalAddress()) != null);
        final InetSocketAddress localAddress = ((InetSocketAddress) (transport.getLocalAddress()));
        sendUdpDatagram(UdpTransportTest.BIND_ADDRESS, localAddress.getPort(), 100);
        await().atMost(5, TimeUnit.SECONDS).until(() -> !(handler.getBytesWritten().isEmpty()));
        transport.stop();
        assertThat(handler.getBytesWritten()).containsOnly(100);
    }

    @Test
    public void transportReceivesDataExactlyRecvBufferSize() throws Exception {
        final UdpTransportTest.CountingChannelUpstreamHandler handler = new UdpTransportTest.CountingChannelUpstreamHandler();
        final UdpTransport transport = launchTransportForBootStrapTest(handler);
        await().atMost(5, TimeUnit.SECONDS).until(() -> (transport.getLocalAddress()) != null);
        final InetSocketAddress localAddress = ((InetSocketAddress) (transport.getLocalAddress()));
        // This will be variable depending on the version of the IP protocol and the UDP packet size.
        final int udpOverhead = 16;
        final int maxPacketSize = (UdpTransportTest.RECV_BUFFER_SIZE) - udpOverhead;
        sendUdpDatagram(UdpTransportTest.BIND_ADDRESS, localAddress.getPort(), maxPacketSize);
        await().atMost(5, TimeUnit.SECONDS).until(() -> !(handler.getBytesWritten().isEmpty()));
        transport.stop();
        assertThat(handler.getBytesWritten()).containsOnly(maxPacketSize);
    }

    @Test
    public void transportDiscardsDataLargerRecvBufferSizeOnMacOsX() throws Exception {
        Assume.assumeTrue("Skipping test intended for MacOS X systems", SystemUtils.IS_OS_MAC_OSX);
        final UdpTransportTest.CountingChannelUpstreamHandler handler = new UdpTransportTest.CountingChannelUpstreamHandler();
        final UdpTransport transport = launchTransportForBootStrapTest(handler);
        await().atMost(5, TimeUnit.SECONDS).until(() -> (transport.getLocalAddress()) != null);
        final InetSocketAddress localAddress = ((InetSocketAddress) (transport.getLocalAddress()));
        sendUdpDatagram(UdpTransportTest.BIND_ADDRESS, localAddress.getPort(), (2 * (UdpTransportTest.RECV_BUFFER_SIZE)));
        Uninterruptibles.sleepUninterruptibly(2, TimeUnit.SECONDS);
        transport.stop();
        assertThat(handler.getBytesWritten()).isEmpty();
    }

    @Test
    public void transportTruncatesDataLargerRecvBufferSizeOnLinux() throws Exception {
        Assume.assumeTrue("Skipping test intended for Linux systems", SystemUtils.IS_OS_LINUX);
        final UdpTransportTest.CountingChannelUpstreamHandler handler = new UdpTransportTest.CountingChannelUpstreamHandler();
        final UdpTransport transport = launchTransportForBootStrapTest(handler);
        await().atMost(5, TimeUnit.SECONDS).until(() -> (transport.getLocalAddress()) != null);
        final InetSocketAddress localAddress = ((InetSocketAddress) (transport.getLocalAddress()));
        sendUdpDatagram(UdpTransportTest.BIND_ADDRESS, localAddress.getPort(), (2 * (UdpTransportTest.RECV_BUFFER_SIZE)));
        await().atMost(5, TimeUnit.SECONDS).until(() -> !(handler.getBytesWritten().isEmpty()));
        transport.stop();
        assertThat(handler.getBytesWritten()).containsExactly(UdpTransportTest.RECV_BUFFER_SIZE);
    }

    @Test
    public void receiveBufferSizeIsDefaultSize() {
        assertThat(udpTransport.getBootstrap(Mockito.mock(MessageInput.class)).config().options().get(SO_RCVBUF)).isEqualTo(UdpTransportTest.RECV_BUFFER_SIZE);
    }

    @Test
    public void receiveBufferSizeIsNotLimited() {
        final int recvBufferSize = Ints.saturatedCast(Size.megabytes(1L).toBytes());
        ImmutableMap<String, Object> source = ImmutableMap.of(CK_BIND_ADDRESS, UdpTransportTest.BIND_ADDRESS, CK_PORT, UdpTransportTest.PORT, CK_RECV_BUFFER_SIZE, recvBufferSize);
        Configuration config = new Configuration(source);
        UdpTransport udpTransport = new UdpTransport(config, eventLoopGroupFactory, nettyTransportConfiguration, throughputCounter, new LocalMetricRegistry());
        assertThat(udpTransport.getBootstrap(Mockito.mock(MessageInput.class)).config().options().get(SO_RCVBUF)).isEqualTo(recvBufferSize);
    }

    @Test
    public void receiveBufferSizePredictorIsUsingDefaultSize() {
        FixedRecvByteBufAllocator recvByteBufAllocator = ((FixedRecvByteBufAllocator) (udpTransport.getBootstrap(Mockito.mock(MessageInput.class)).config().options().get(RCVBUF_ALLOCATOR)));
        assertThat(recvByteBufAllocator.newHandle().guess()).isEqualTo(UdpTransportTest.RECV_BUFFER_SIZE);
    }

    @Test
    public void getMetricSetReturnsLocalMetricRegistry() {
        assertThat(udpTransport.getMetricSet()).isSameAs(localMetricRegistry);
    }

    @Test
    public void testDefaultReceiveBufferSize() {
        final UdpTransport.Config config = new UdpTransport.Config();
        final ConfigurationRequest requestedConfiguration = config.getRequestedConfiguration();
        assertThat(requestedConfiguration.getField(CK_RECV_BUFFER_SIZE).getDefaultValue()).isEqualTo(262144);
    }

    @Test
    public void testTrafficCounter() throws Exception {
        final UdpTransportTest.CountingChannelUpstreamHandler handler = new UdpTransportTest.CountingChannelUpstreamHandler();
        final UdpTransport transport = launchTransportForBootStrapTest(handler);
        try {
            await().atMost(5, TimeUnit.SECONDS).until(() -> (transport.getLocalAddress()) != null);
            final InetSocketAddress localAddress = ((InetSocketAddress) (transport.getLocalAddress()));
            sendUdpDatagram(UdpTransportTest.BIND_ADDRESS, localAddress.getPort(), 512);
            await().atMost(5, TimeUnit.SECONDS).until(() -> (handler.getBytesWritten().size()) == 1);
            assertThat(handler.getBytesWritten()).containsExactly(512);
            sendUdpDatagram(UdpTransportTest.BIND_ADDRESS, localAddress.getPort(), 512);
            await().atMost(5, TimeUnit.SECONDS).until(() -> (handler.getBytesWritten().size()) == 2);
            assertThat(handler.getBytesWritten()).containsExactly(512, 512);
        } finally {
            transport.stop();
        }
        final Map<String, Gauge<Long>> gauges = throughputCounter.gauges();
        assertThat(gauges.get(READ_BYTES_TOTAL).getValue()).isEqualTo(1024L);
    }

    public static class CountingChannelUpstreamHandler extends SimpleChannelInboundHandler<DatagramPacket> {
        private final List<Integer> bytesWritten = new CopyOnWriteArrayList<>();

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
            bytesWritten.add(msg.content().readableBytes());
            ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
        }

        List<Integer> getBytesWritten() {
            return bytesWritten;
        }
    }
}

