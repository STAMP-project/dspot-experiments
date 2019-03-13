package org.mockserver.mock.action;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpServerCodec;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockserver.model.HttpError;


/**
 *
 *
 * @author jamesdbloom
 */
public class HttpErrorActionHandlerTest {
    @Test
    public void shouldDropConnection() {
        // given
        ChannelHandlerContext mockChannelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        HttpError httpError = HttpError.error().withDropConnection(true);
        // when
        new HttpErrorActionHandler().handle(httpError, mockChannelHandlerContext);
        // then
        Mockito.verify(mockChannelHandlerContext).close();
    }

    @Test
    public void shouldReturnBytes() {
        // given
        ChannelHandlerContext mockChannelHandlerContext = Mockito.mock(ChannelHandlerContext.class);
        ChannelPipeline mockChannelPipeline = Mockito.mock(ChannelPipeline.class);
        ChannelFuture mockChannelFuture = Mockito.mock(ChannelFuture.class);
        Mockito.when(mockChannelHandlerContext.pipeline()).thenReturn(mockChannelPipeline);
        Mockito.when(mockChannelPipeline.context(HttpServerCodec.class)).thenReturn(mockChannelHandlerContext);
        Mockito.when(mockChannelHandlerContext.writeAndFlush(ArgumentMatchers.any(ByteBuf.class))).thenReturn(mockChannelFuture);
        // when
        new HttpErrorActionHandler().handle(HttpError.error().withResponseBytes("some_bytes".getBytes()), mockChannelHandlerContext);
        // then
        Mockito.verify(mockChannelHandlerContext).pipeline();
        Mockito.verify(mockChannelPipeline).context(HttpServerCodec.class);
        Mockito.verify(mockChannelHandlerContext).writeAndFlush(Unpooled.wrappedBuffer("some_bytes".getBytes()));
        Mockito.verify(mockChannelFuture).awaitUninterruptibly();
    }
}

