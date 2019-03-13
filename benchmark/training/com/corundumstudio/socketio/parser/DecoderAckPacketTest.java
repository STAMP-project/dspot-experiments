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


import PacketType.ACK;
import com.corundumstudio.socketio.protocol.Packet;
import com.fasterxml.jackson.core.JsonParseException;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;


public class DecoderAckPacketTest extends DecoderBaseTest {
    @Test
    public void testDecode() throws IOException {
        Packet packet = decoder.decodePacket("6:::140", null);
        Assert.assertEquals(ACK, packet.getType());
        Assert.assertEquals(140, ((long) (packet.getAckId())));
        // Assert.assertTrue(packet.getArgs().isEmpty());
    }

    @Test
    public void testDecodeWithArgs() throws IOException {
        initExpectations();
        Packet packet = decoder.decodePacket("6:::12+[\"woot\",\"wa\"]", null);
        Assert.assertEquals(ACK, packet.getType());
        Assert.assertEquals(12, ((long) (packet.getAckId())));
        // Assert.assertEquals(Arrays.<Object>asList("woot", "wa"), packet.getArgs());
    }

    @Test(expected = JsonParseException.class)
    public void testDecodeWithBadJson() throws IOException {
        initExpectations();
        decoder.decodePacket("6:::1+{\"++]", null);
    }
}

