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


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class QosProcessHandlerTest {
    @Test
    public void testDecodeHttp() throws Exception {
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{ 'G' });
        ChannelHandlerContext context = Mockito.mock(ChannelHandlerContext.class);
        ChannelPipeline pipeline = Mockito.mock(ChannelPipeline.class);
        Mockito.when(context.pipeline()).thenReturn(pipeline);
        QosProcessHandler handler = new QosProcessHandler("welcome", false);
        handler.decode(context, buf, Collections.emptyList());
        Mockito.verify(pipeline).addLast(ArgumentMatchers.any(HttpServerCodec.class));
        Mockito.verify(pipeline).addLast(ArgumentMatchers.any(HttpObjectAggregator.class));
        Mockito.verify(pipeline).addLast(ArgumentMatchers.any(HttpProcessHandler.class));
        Mockito.verify(pipeline).remove(handler);
    }

    @Test
    public void testDecodeTelnet() throws Exception {
        ByteBuf buf = Unpooled.wrappedBuffer(new byte[]{ 'A' });
        ChannelHandlerContext context = Mockito.mock(ChannelHandlerContext.class);
        ChannelPipeline pipeline = Mockito.mock(ChannelPipeline.class);
        Mockito.when(context.pipeline()).thenReturn(pipeline);
        QosProcessHandler handler = new QosProcessHandler("welcome", false);
        handler.decode(context, buf, Collections.emptyList());
        Mockito.verify(pipeline).addLast(ArgumentMatchers.any(LineBasedFrameDecoder.class));
        Mockito.verify(pipeline).addLast(ArgumentMatchers.any(StringDecoder.class));
        Mockito.verify(pipeline).addLast(ArgumentMatchers.any(StringEncoder.class));
        Mockito.verify(pipeline).addLast(ArgumentMatchers.any(TelnetProcessHandler.class));
        Mockito.verify(pipeline).remove(handler);
    }
}

