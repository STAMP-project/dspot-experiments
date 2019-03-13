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
package com.hazelcast.client.protocol;


import com.hazelcast.client.impl.protocol.ClientMessage;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


/**
 * ClientMessageAccumulator Tests
 */
@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class ClientMessageAccumulatorTest {
    private static final byte[] BYTE_DATA = new byte[]{ 0, 0, 24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

    private static final int OFFSET = 2;

    @Test
    public void shouldAccumulateClientMessageCorrectly() {
        ClientMessage accumulator = ClientMessage.create();
        final ByteBuffer inBuffer = ByteBuffer.wrap(ClientMessageAccumulatorTest.BYTE_DATA);
        inBuffer.position(ClientMessageAccumulatorTest.OFFSET);
        accumulator.readFrom(inBuffer);
        final ByteBuffer byteBuffer = ClientMessageAccumulatorTest.accumulatedByteBuffer(accumulator.buffer(), accumulator.index());
        Assert.assertEquals(0, byteBuffer.position());
        Assert.assertEquals(accumulator.getFrameLength(), byteBuffer.limit());
        for (int i = ClientMessageAccumulatorTest.OFFSET; i < (byteBuffer.limit()); i++) {
            Assert.assertEquals(ClientMessageAccumulatorTest.BYTE_DATA[i], byteBuffer.get());
        }
        Assert.assertTrue(accumulator.isComplete());
    }

    @Test
    public void shouldNotAccumulateInCompleteFrameSize() {
        ClientMessage accumulator = ClientMessage.create();
        final byte[] array = new byte[]{ 1, 2, 3 };
        final ByteBuffer inBuffer = ByteBuffer.wrap(array);
        Assert.assertFalse(accumulator.readFrom(inBuffer));
        Assert.assertFalse(accumulator.isComplete());
    }
}

