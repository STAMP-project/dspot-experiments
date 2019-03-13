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
package com.hazelcast.nio.tcp;


import com.hazelcast.internal.networking.HandlerStatus;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.Packet;
import com.hazelcast.nio.PacketIOHelper;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import com.hazelcast.util.function.Supplier;
import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class PacketEncoderTest extends HazelcastTestSupport {
    private InternalSerializationService serializationService;

    private PacketEncoder encoder;

    @Test
    public void whenPacketFullyWritten() {
        final Packet packet = new Packet(serializationService.toBytes("foobar"));
        ByteBuffer dst = ByteBuffer.allocate(1000);
        dst.flip();
        PacketEncoderTest.PacketSupplier src = new PacketEncoderTest.PacketSupplier();
        src.queue.add(packet);
        encoder.dst(dst);
        encoder.src(src);
        HandlerStatus result = encoder.onWrite();
        Assert.assertEquals(HandlerStatus.CLEAN, result);
        // now we read out the dst and check if we can find the written packet.
        Packet resultPacket = new PacketIOHelper().readFrom(dst);
        Assert.assertEquals(packet, resultPacket);
    }

    @Test
    public void whenNotEnoughSpace() {
        final Packet packet = new Packet(serializationService.toBytes(new byte[2000]));
        ByteBuffer dst = ByteBuffer.allocate(1000);
        dst.flip();
        PacketEncoderTest.PacketSupplier src = new PacketEncoderTest.PacketSupplier();
        src.queue.add(packet);
        encoder.dst(dst);
        encoder.src(src);
        HandlerStatus result = encoder.onWrite();
        Assert.assertEquals(HandlerStatus.DIRTY, result);
    }

    static class PacketSupplier implements Supplier<Packet> {
        Queue<Packet> queue = new LinkedBlockingQueue<Packet>();

        @Override
        public Packet get() {
            return queue.poll();
        }
    }
}

