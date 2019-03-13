/**
 * Copyright (c) 2012-2019 Nikita Koksharov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.corundumstudio.socketio.parser;


import com.corundumstudio.socketio.protocol.Packet;
import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


public class DecoderJsonPacketTest extends DecoderBaseTest {
    @Test
    public void testUTF8Decode() throws IOException {
        Packet packet = decoder.decodePacket("4:::\"\u041f\u0440\u0438\u0432\u0435\u0442\"", null);
        // Assert.assertEquals(PacketType.JSON, packet.getType());
        Assert.assertEquals("??????", packet.getData());
    }

    @Test
    public void testDecode() throws IOException {
        Packet packet = decoder.decodePacket("4:::\"2\"", null);
        // Assert.assertEquals(PacketType.JSON, packet.getType());
        Assert.assertEquals("2", packet.getData());
    }

    @Test
    public void testDecodeWithMessageIdAndAckData() throws IOException {
        Packet packet = decoder.decodePacket("4:1+::{\"a\":\"b\"}", null);
        // Assert.assertEquals(PacketType.JSON, packet.getType());
        // Assert.assertEquals(1, (long)packet.getId());
        // Assert.assertEquals(Packet.ACK_DATA, packet.getAck());
        Map obj = ((Map) (packet.getData()));
        Assert.assertEquals("b", obj.get("a"));
        Assert.assertEquals(1, obj.size());
    }
}

