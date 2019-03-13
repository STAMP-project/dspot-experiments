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
package org.apache.shardingsphere.shardingproxy.transport.mysql.codec;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import java.util.LinkedList;
import java.util.List;
import org.apache.shardingsphere.shardingproxy.transport.mysql.packet.MySQLPacket;
import org.apache.shardingsphere.shardingproxy.transport.mysql.payload.MySQLPacketPayload;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class MySQLPacketCodecEngineTest {
    @Mock
    private ChannelHandlerContext context;

    @Mock
    private ByteBuf byteBuf;

    @Test
    public void assertIsValidHeader() {
        Assert.assertTrue(new MySQLPacketCodecEngine().isValidHeader(50));
    }

    @Test
    public void assertIsInvalidHeader() {
        Assert.assertFalse(new MySQLPacketCodecEngine().isValidHeader(3));
    }

    @Test
    public void assertDecode() {
        Mockito.when(byteBuf.markReaderIndex()).thenReturn(byteBuf);
        Mockito.when(byteBuf.readMediumLE()).thenReturn(50);
        Mockito.when(byteBuf.readRetainedSlice(51)).thenReturn(byteBuf);
        List<Object> out = new LinkedList<>();
        new MySQLPacketCodecEngine().decode(context, byteBuf, out, 54);
        Assert.assertThat(out.size(), CoreMatchers.is(1));
    }

    @Test
    public void assertDecodeWithStickyPacket() {
        Mockito.when(byteBuf.markReaderIndex()).thenReturn(byteBuf);
        Mockito.when(byteBuf.readMediumLE()).thenReturn(50);
        List<Object> out = new LinkedList<>();
        new MySQLPacketCodecEngine().decode(context, byteBuf, out, 40);
        Assert.assertTrue(out.isEmpty());
    }

    @Test
    public void assertEncode() {
        ByteBufAllocator byteBufAllocator = Mockito.mock(ByteBufAllocator.class);
        Mockito.when(context.alloc()).thenReturn(byteBufAllocator);
        ByteBuf payloadByteBuf = Mockito.mock(ByteBuf.class);
        Mockito.when(byteBufAllocator.buffer()).thenReturn(payloadByteBuf);
        Mockito.when(payloadByteBuf.readableBytes()).thenReturn(50);
        MySQLPacket actualMessage = Mockito.mock(MySQLPacket.class);
        Mockito.when(actualMessage.getSequenceId()).thenReturn(1);
        new MySQLPacketCodecEngine().encode(context, actualMessage, byteBuf);
        Mockito.verify(actualMessage).write(ArgumentMatchers.<MySQLPacketPayload>any());
        Mockito.verify(byteBuf).writeMediumLE(50);
        Mockito.verify(byteBuf).writeByte(1);
        Mockito.verify(byteBuf).writeBytes(payloadByteBuf);
    }

    @Test
    public void assertCreatePacketPayload() {
        Assert.assertThat(new MySQLPacketCodecEngine().createPacketPayload(byteBuf).getByteBuf(), CoreMatchers.is(byteBuf));
    }
}

