/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.qos.server.handler;


import ChannelFutureListener.CLOSE;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class LocalHostPermitHandlerTest {
    @Test
    public void testHandlerAdded() throws Exception {
        ChannelHandlerContext context = Mockito.mock(ChannelHandlerContext.class);
        Channel channel = Mockito.mock(Channel.class);
        Mockito.when(context.channel()).thenReturn(channel);
        InetAddress addr = Mockito.mock(InetAddress.class);
        Mockito.when(addr.isLoopbackAddress()).thenReturn(false);
        InetSocketAddress address = new InetSocketAddress(addr, 12345);
        Mockito.when(channel.remoteAddress()).thenReturn(address);
        ChannelFuture future = Mockito.mock(ChannelFuture.class);
        Mockito.when(context.writeAndFlush(ArgumentMatchers.any(ByteBuf.class))).thenReturn(future);
        LocalHostPermitHandler handler = new LocalHostPermitHandler(false);
        handler.handlerAdded(context);
        ArgumentCaptor<ByteBuf> captor = ArgumentCaptor.forClass(ByteBuf.class);
        Mockito.verify(context).writeAndFlush(captor.capture());
        MatcherAssert.assertThat(new String(captor.getValue().array()), Matchers.containsString("Foreign Ip Not Permitted"));
        Mockito.verify(future).addListener(CLOSE);
    }
}

