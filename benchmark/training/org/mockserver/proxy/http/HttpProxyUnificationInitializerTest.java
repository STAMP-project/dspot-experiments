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
import io.netty.handler.ssl.SslHandler;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.apache.commons.codec.binary.Hex;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockserver.lifecycle.LifeCycle;
import org.mockserver.mock.HttpStateHandler;
import org.mockserver.proxy.socks.Socks5ProxyHandler;
import org.mockserver.scheduler.Scheduler;


public class HttpProxyUnificationInitializerTest {
    @Test
    public void shouldSwitchToSsl() {
        // given
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new org.mockserver.mockserver.MockServerUnificationInitializer(Mockito.mock(LifeCycle.class), new HttpStateHandler(Mockito.mock(Scheduler.class)), null));
        // and - no SSL handler
        Assert.assertThat(embeddedChannel.pipeline().get(SslHandler.class), Is.is(CoreMatchers.nullValue()));
        // when - first part of a 5-byte handshake message
        embeddedChannel.writeInbound(Unpooled.wrappedBuffer(new byte[]{ 22// handshake
        , 3// major version
        , 1, 0, 5// package length (5-byte)
         }));
        // then - should add SSL handlers first
        Assert.assertThat(String.valueOf(embeddedChannel.pipeline().names()), embeddedChannel.pipeline().names(), contains("SniHandler#0", "PortUnificationHandler#0", "DefaultChannelPipeline$TailContext#0"));
    }

    @Test
    public void shouldSwitchToSOCKS() throws IOException, InterruptedException {
        // given - embedded channel
        short localPort = 1234;
        EmbeddedChannel embeddedChannel = new EmbeddedChannel(new org.mockserver.mockserver.MockServerUnificationInitializer(Mockito.mock(LifeCycle.class), new HttpStateHandler(Mockito.mock(Scheduler.class)), null));
        // embeddedChannel.attr(HTTP_CONNECT_SOCKET).set(new InetSocketAddress(localPort));
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
        Assert.assertThat(String.valueOf(embeddedChannel.pipeline().names()), embeddedChannel.pipeline().names(), contains("Socks5CommandRequestDecoder#0", "Socks5ServerEncoder#0", "Socks5ProxyHandler#0", "PortUnificationHandler#0", "DefaultChannelPipeline$TailContext#0"));
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
        Assert.assertThat(String.valueOf(embeddedChannel.pipeline().names()), embeddedChannel.pipeline().names(), contains("HttpServerCodec#0", "HttpContentDecompressor#0", "HttpContentLengthRemover#0", "HttpObjectAggregator#0", "CallbackWebSocketServerHandler#0", "DashboardWebSocketServerHandler#0", "MockServerServerCodec#0", "MockServerHandler#0", "DefaultChannelPipeline$TailContext#0"));
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
        Assert.assertThat(String.valueOf(embeddedChannel.pipeline().names()), embeddedChannel.pipeline().names(), contains("DefaultChannelPipeline$TailContext#0"));
        // and - close channel
        Assert.assertThat(embeddedChannel.isOpen(), Is.is(false));
    }
}

