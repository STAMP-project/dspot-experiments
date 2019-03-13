/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.netty4;


import CharsetUtil.UTF_8;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import org.junit.Test;


public class NettyUdpConnectionlessSendTest extends BaseNettyTest {
    private static final String SEND_STRING = "***<We all love camel>***";

    private static final int SEND_COUNT = 20;

    private volatile int receivedCount;

    private EventLoopGroup group;

    private Bootstrap bootstrap;

    @Test
    public void sendConnectionlessUdp() throws Exception {
        createNettyUdpReceiver();
        bind();
        for (int i = 0; i < (NettyUdpConnectionlessSendTest.SEND_COUNT); ++i) {
            template.sendBody("direct:in", NettyUdpConnectionlessSendTest.SEND_STRING);
        }
        stop();
        assertTrue("We should have received some datagrams", ((receivedCount) > 0));
    }

    @Test
    public void sendWithoutReceiver() throws Exception {
        int exceptionCount = 0;
        for (int i = 0; i < (NettyUdpConnectionlessSendTest.SEND_COUNT); ++i) {
            try {
                template.sendBody("direct:in", NettyUdpConnectionlessSendTest.SEND_STRING);
            } catch (Exception ex) {
                ++exceptionCount;
            }
        }
        assertEquals("No exception should occur", 0, exceptionCount);
    }

    public class UdpHandler extends MessageToMessageDecoder<DatagramPacket> {
        @Override
        protected void decode(ChannelHandlerContext channelHandlerContext, DatagramPacket datagramPacket, List<Object> objects) throws Exception {
            objects.add(datagramPacket.content().toString(UTF_8));
        }
    }

    public class ContentHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext channelHandlerContext, String s) throws Exception {
            ++(receivedCount);
        }
    }
}

