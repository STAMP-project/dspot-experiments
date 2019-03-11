package org.mockserver.proxy.http;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.socks.SocksInitRequestDecoder;
import io.netty.handler.codec.socks.SocksMessageEncoder;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Hex;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.lifecycle.LifeCycle;
import org.mockserver.logging.MockServerLogger;
import org.mockserver.mock.HttpStateHandler;
import org.mockserver.proxy.socks.Socks5ProxyHandler;
import org.mockserver.scheduler.Scheduler;
import org.slf4j.event.Level;


public class HttpProxyUnificationInitializerSOCKSErrorTest {
    @Test
    public void shouldHandleErrorsDuringSOCKSConnection() {
        // given - embedded channel
        short localPort = 1234;
        final LifeCycle lifeCycle = Mockito.mock(LifeCycle.class);
        Mockito.when(lifeCycle.getScheduler()).thenReturn(Mockito.mock(Scheduler.class));
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new org.mockserver.mockserver.MockServerUnificationInitializer(lifeCycle, new HttpStateHandler(Mockito.mock(Scheduler.class)), null));
        // and - no SOCKS handlers
        Assert.assertThat(embeddedChannel.pipeline().get(Socks5ProxyHandler.class), Is.is(CoreMatchers.nullValue()));
        Assert.assertThat(embeddedChannel.pipeline().get(SocksMessageEncoder.class), Is.is(CoreMatchers.nullValue()));
        Assert.assertThat(embeddedChannel.pipeline().get(SocksInitRequestDecoder.class), Is.is(CoreMatchers.nullValue()));
        // when - SOCKS INIT message
        embeddedChannel.writeInbound(Unpooled.wrappedBuffer(new byte[]{ ((byte) (5)), // SOCKS5
        ((byte) (2)), // 1 authentication method
        ((byte) (0)), // NO_AUTH
        ((byte) (2))// AUTH_PASSWORD
         }));
        // then - INIT response
        Assert.assertThat(ByteBufUtil.hexDump(((ByteBuf) (embeddedChannel.readOutbound()))), Is.is(Hex.encodeHexString(new byte[]{ ((byte) (5)), // SOCKS5
        ((byte) (0))// NO_AUTH
         })));
        // and then - should add SOCKS handlers first
        if (new MockServerLogger().isEnabled(Level.TRACE)) {
            Assert.assertThat(String.valueOf(embeddedChannel.pipeline().names()), embeddedChannel.pipeline().names(), contains("LoggingHandler#0", "Socks5CommandRequestDecoder#0", "Socks5ServerEncoder#0", "Socks5ProxyHandler#0", "PortUnificationHandler#0", "DefaultChannelPipeline$TailContext#0"));
        } else {
            Assert.assertThat(String.valueOf(embeddedChannel.pipeline().names()), embeddedChannel.pipeline().names(), contains("Socks5CommandRequestDecoder#0", "Socks5ServerEncoder#0", "Socks5ProxyHandler#0", "PortUnificationHandler#0", "DefaultChannelPipeline$TailContext#0"));
        }
        // and when - SOCKS CONNECT command
        embeddedChannel.writeInbound(Unpooled.wrappedBuffer(new byte[]{ ((byte) (5)), // SOCKS5
        ((byte) (1)), // command type CONNECT
        ((byte) (0)), // reserved (must be 0x00)
        ((byte) (1)), // address type IPv4
        ((byte) (127)), ((byte) (0)), ((byte) (0)), ((byte) (1))// ip address
        , ((byte) (localPort & 65280)), ((byte) (localPort))// port
         }));
        // then - CONNECT response
        Assert.assertThat(ByteBufUtil.hexDump(((ByteBuf) (embeddedChannel.readOutbound()))), Is.is(Hex.encodeHexString(new byte[]{ ((byte) (5)), // SOCKS5
        ((byte) (1)), // general failure (caused by connection failure)
        ((byte) (0)), // reserved (must be 0x00)
        ((byte) (3)), // address type domain
        ((byte) (0)), // domain of length 0
        ((byte) (0)), ((byte) (0))// port 0
         })));
        // then - channel is closed after error
        Assert.assertThat(embeddedChannel.isOpen(), Is.is(false));
    }

    @Test
    public void shouldSwitchToHttp() {
        // given
        EmbeddedChannel embeddedChannel = new EmbeddedChannel();
        embeddedChannel.pipeline().addLast(new org.mockserver.mockserver.MockServerUnificationInitializer(Mockito.mock(LifeCycle.class), new HttpStateHandler(Mockito.mock(Scheduler.class)), null));
        // and - no HTTP handlers
        Assert.assertThat(embeddedChannel.pipeline().get(HttpServerCodec.class), Is.is(CoreMatchers.nullValue()));
        Assert.assertThat(embeddedChannel.pipeline().get(HttpContentDecompressor.class), Is.is(CoreMatchers.nullValue()));
        Assert.assertThat(embeddedChannel.pipeline().get(HttpObjectAggregator.class), Is.is(CoreMatchers.nullValue()));
        // when - basic HTTP request
        embeddedChannel.writeInbound(Unpooled.wrappedBuffer("GET /somePath HTTP/1.1\r\nHost: some.random.host\r\n\r\n".getBytes(StandardCharsets.UTF_8)));
        // then - should add HTTP handlers last
        if (new MockServerLogger().isEnabled(Level.TRACE)) {
            Assert.assertThat(String.valueOf(embeddedChannel.pipeline().names()), embeddedChannel.pipeline().names(), contains("LoggingHandler#0", "HttpServerCodec#0", "HttpContentDecompressor#0", "HttpContentLengthRemover#0", "HttpObjectAggregator#0", "CallbackWebSocketServerHandler#0", "DashboardWebSocketServerHandler#0", "MockServerServerCodec#0", "MockServerHandler#0", "DefaultChannelPipeline$TailContext#0"));
        } else {
            Assert.assertThat(String.valueOf(embeddedChannel.pipeline().names()), embeddedChannel.pipeline().names(), contains("HttpServerCodec#0", "HttpContentDecompressor#0", "HttpContentLengthRemover#0", "HttpObjectAggregator#0", "CallbackWebSocketServerHandler#0", "DashboardWebSocketServerHandler#0", "MockServerServerCodec#0", "MockServerHandler#0", "DefaultChannelPipeline$TailContext#0"));
        }
    }

    @Test
    public void shouldSupportUnknownProtocol() {
        // given
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new org.mockserver.mockserver.MockServerUnificationInitializer(Mockito.mock(LifeCycle.class), new HttpStateHandler(Mockito.mock(Scheduler.class)), null));
        // and - channel open
        Assert.assertThat(embeddedChannel.isOpen(), Is.is(true));
        // when - basic HTTP request
        embeddedChannel.writeInbound(Unpooled.wrappedBuffer("UNKNOWN_PROTOCOL".getBytes(StandardCharsets.UTF_8)));
        // then - should add no handlers
        Assert.assertThat(embeddedChannel.pipeline().names(), contains("DefaultChannelPipeline$TailContext#0"));
        // and - close channel
        Assert.assertThat(embeddedChannel.isOpen(), Is.is(false));
    }
}

