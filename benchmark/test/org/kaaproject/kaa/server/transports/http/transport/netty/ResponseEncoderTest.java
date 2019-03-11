/**
 * Copyright 2014-2016 CyberVision, Inc.
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
package org.kaaproject.kaa.server.transports.http.transport.netty;


import ChannelFutureListener.CLOSE;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpResponse;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class ResponseEncoderTest {
    public static final String CONNECTION = "close";

    private ResponseEncoder responseEncoder = new ResponseEncoder();

    private ChannelHandlerContext channelHandlerContext = Mockito.mock(ChannelHandlerContext.class);

    private AbstractCommand abstractCommand = Mockito.mock(AbstractCommand.class);

    private ChannelPromise promise = Mockito.mock(ChannelPromise.class);

    private HttpResponse response = Mockito.mock(HttpResponse.class, Mockito.RETURNS_DEEP_STUBS);

    private ChannelFuture future = Mockito.mock(ChannelFuture.class);

    @Test
    public void validHttpResponseWriteTest() throws Exception {
        Mockito.when(response.headers().get(ArgumentMatchers.isA(CharSequence.class))).thenReturn(null);
        responseEncoder.write(channelHandlerContext, abstractCommand, promise);
        Mockito.verify(channelHandlerContext).writeAndFlush(response, promise);
        Mockito.verify(future, Mockito.never()).addListener(CLOSE);
    }

    @Test
    public void validHttpResponseAddListenerTest() throws Exception {
        Mockito.when(response.headers().get(ArgumentMatchers.isA(CharSequence.class))).thenReturn(ResponseEncoderTest.CONNECTION);
        Mockito.when(channelHandlerContext.writeAndFlush(response, promise)).thenReturn(future);
        responseEncoder.write(channelHandlerContext, abstractCommand, promise);
        Mockito.verify(channelHandlerContext).writeAndFlush(response, promise);
        Mockito.verify(future).addListener(CLOSE);
    }

    @Test
    public void invalidHttpResponseWriteTest() throws Exception {
        responseEncoder.write(channelHandlerContext, new Object(), promise);
        Mockito.verify(channelHandlerContext, Mockito.never()).writeAndFlush(response, promise);
    }
}

