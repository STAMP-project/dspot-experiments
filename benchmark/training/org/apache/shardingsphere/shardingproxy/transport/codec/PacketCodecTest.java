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
package org.apache.shardingsphere.shardingproxy.transport.codec;


import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.Collections;
import org.apache.shardingsphere.shardingproxy.transport.packet.DatabasePacket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public final class PacketCodecTest {
    @Mock
    private DatabasePacketCodecEngine databasePacketCodecEngine;

    @Mock
    private ChannelHandlerContext context;

    @Mock
    private ByteBuf byteBuf;

    private PacketCodec packetCodec;

    @Test
    public void assertDecodeWithValidHeader() {
        Mockito.when(byteBuf.readableBytes()).thenReturn(1);
        Mockito.when(databasePacketCodecEngine.isValidHeader(1)).thenReturn(true);
        packetCodec.decode(context, byteBuf, Collections.emptyList());
        Mockito.verify(databasePacketCodecEngine).decode(context, byteBuf, Collections.emptyList(), 1);
    }

    @Test
    public void assertDecodeWithInvalidHeader() {
        Mockito.when(byteBuf.readableBytes()).thenReturn(1);
        Mockito.when(databasePacketCodecEngine.isValidHeader(1)).thenReturn(false);
        packetCodec.decode(context, byteBuf, Collections.emptyList());
        Mockito.verify(databasePacketCodecEngine, Mockito.times(0)).decode(context, byteBuf, Collections.emptyList(), 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void assertEncode() {
        DatabasePacket databasePacket = Mockito.mock(DatabasePacket.class);
        packetCodec.encode(context, databasePacket, byteBuf);
        Mockito.verify(databasePacketCodecEngine).encode(context, databasePacket, byteBuf);
    }
}

