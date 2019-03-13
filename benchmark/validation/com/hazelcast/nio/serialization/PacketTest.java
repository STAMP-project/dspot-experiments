/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.nio.serialization;


import Packet.Type;
import Packet.Type.NULL;
import Packet.Type.OPERATION;
import com.hazelcast.nio.Packet;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class PacketTest {
    @Test
    public void raiseFlags() {
        Packet packet = new Packet();
        packet.raiseFlags(Packet.FLAG_URGENT);
        Assert.assertEquals(Packet.FLAG_URGENT, packet.getFlags());
    }

    @Test
    public void setPacketType() {
        Packet packet = new Packet();
        for (Packet.Type type : Type.values()) {
            packet.setPacketType(type);
            Assert.assertSame(type, packet.getPacketType());
        }
    }

    @Test
    public void isFlagSet() {
        Packet packet = new Packet();
        packet.setPacketType(OPERATION);
        packet.raiseFlags(Packet.FLAG_URGENT);
        Assert.assertSame(OPERATION, packet.getPacketType());
        Assert.assertTrue(packet.isFlagRaised(Packet.FLAG_URGENT));
        Assert.assertFalse(packet.isFlagRaised(Packet.FLAG_OP_CONTROL));
    }

    @Test
    public void resetFlagsTo() {
        Packet packet = new Packet().setPacketType(OPERATION);
        packet.resetFlagsTo(Packet.FLAG_URGENT);
        Assert.assertSame(NULL, packet.getPacketType());
        Assert.assertEquals(Packet.FLAG_URGENT, packet.getFlags());
    }
}

