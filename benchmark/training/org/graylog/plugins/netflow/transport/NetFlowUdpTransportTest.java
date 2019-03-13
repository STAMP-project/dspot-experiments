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
package org.graylog.plugins.netflow.transport;


import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoopGroup;
import java.util.LinkedHashMap;
import java.util.concurrent.Callable;
import org.graylog2.inputs.transports.netty.EventLoopGroupFactory;
import org.graylog2.plugin.inputs.MessageInput;
import org.junit.Test;
import org.mockito.Mockito;


public class NetFlowUdpTransportTest {
    private EventLoopGroup eventLoopGroup;

    private EventLoopGroupFactory eventLoopGroupFactory;

    private NetFlowUdpTransport transport;

    @Test
    public void getChildChannelHandlersContainsCustomCodecAggregator() throws Exception {
        final LinkedHashMap<String, Callable<? extends ChannelHandler>> handlers = transport.getChannelHandlers(Mockito.mock(MessageInput.class));
        assertThat(handlers).containsKey("codec-aggregator").doesNotContainKey("udp-datagram");
        final ChannelHandler channelHandler = handlers.get("codec-aggregator").call();
        assertThat(channelHandler).isInstanceOf(NetflowMessageAggregationHandler.class);
    }
}

