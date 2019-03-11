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
package org.kaaproject.kaa.server.transports.tcp.transport.netty;


import ChannelFutureListener.CLOSE;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.junit.Test;
import org.kaaproject.kaa.common.channels.protocols.kaatcp.messages.MqttFrame;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class KaaTcpEncoderTest {
    private KaaTcpEncoder encoder = new KaaTcpEncoder();

    private ChannelHandlerContext ctx;

    private ChannelPromise promise;

    private ChannelFuture future;

    @Test
    public void incorrectMessageWriteTest() throws Exception {
        Object msg = new Object();
        encoder.write(ctx, msg, promise);
        Mockito.verify(ctx, Mockito.never()).writeAndFlush(ArgumentMatchers.any(Object.class), ArgumentMatchers.any(ChannelPromise.class));
    }

    @Test
    public void writeMessageNoCloseConnectionTest() throws Exception {
        MqttFrame msg = createMqttFrameMock(false);
        encoder.write(ctx, msg, promise);
        Mockito.verify(future, Mockito.never()).addListener(CLOSE);
    }

    @Test
    public void writeMessageAndCloseConnectionTest() throws Exception {
        MqttFrame msg = createMqttFrameMock(true);
        encoder.write(ctx, msg, promise);
        Mockito.verify(future).addListener(CLOSE);
    }
}

