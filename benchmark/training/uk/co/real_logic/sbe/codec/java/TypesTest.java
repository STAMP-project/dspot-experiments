/**
 * Copyright 2013-2019 Real Logic Ltd.
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
package uk.co.real_logic.sbe.codec.java;


import java.nio.ByteOrder;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class TypesTest {
    private static final ByteOrder BYTE_ORDER = ByteOrder.nativeOrder();

    private static final int BUFFER_CAPACITY = 64;

    private final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[TypesTest.BUFFER_CAPACITY]);

    @Test
    public void shouldTestBitInByte() {
        final byte bits = ((byte) (128));
        final int bufferIndex = 8;
        final int bitIndex = 7;
        buffer.putByte(bufferIndex, bits);
        for (int i = 0; i < 8; i++) {
            final boolean result = 0 != ((buffer.getByte(bufferIndex)) & (1 << i));
            if (bitIndex == i) {
                Assert.assertTrue(result);
            } else {
                Assert.assertFalse(("bit set i = " + i), result);
            }
        }
    }

    @Test
    public void shouldSetBitInByte() {
        final int bufferIndex = 8;
        short total = 0;
        for (int i = 0; i < 8; i++) {
            byte bits = buffer.getByte(bufferIndex);
            bits = ((byte) (bits | (1 << i)));
            buffer.putByte(bufferIndex, bits);
            total += 1 << i;
            Assert.assertThat(buffer.getByte(bufferIndex), Matchers.is(((byte) (total))));
        }
    }

    @Test
    public void shouldTestBitInShort() {
        final short bits = ((short) (4));
        final int bufferIndex = 8;
        final int bitIndex = 2;
        buffer.putShort(bufferIndex, bits, TypesTest.BYTE_ORDER);
        for (int i = 0; i < 16; i++) {
            final boolean result = 0 != ((buffer.getShort(bufferIndex, TypesTest.BYTE_ORDER)) & (1 << i));
            if (bitIndex == i) {
                Assert.assertTrue(result);
            } else {
                Assert.assertFalse(("bit set i = " + i), result);
            }
        }
    }

    @Test
    public void shouldSetBitInShort() {
        final int bufferIndex = 8;
        int total = 0;
        for (int i = 0; i < 16; i++) {
            short bits = buffer.getShort(bufferIndex, TypesTest.BYTE_ORDER);
            bits = ((short) (bits | (1 << i)));
            buffer.putShort(bufferIndex, bits, TypesTest.BYTE_ORDER);
            total += 1 << i;
            Assert.assertThat(buffer.getShort(bufferIndex, TypesTest.BYTE_ORDER), Matchers.is(((short) (total))));
        }
    }

    @Test
    public void shouldTestBitInInt() {
        final int bits = 4;
        final int bufferIndex = 8;
        final int bitIndex = 2;
        buffer.putInt(bufferIndex, bits, TypesTest.BYTE_ORDER);
        for (int i = 0; i < 32; i++) {
            final boolean result = 0 != ((buffer.getInt(bufferIndex, TypesTest.BYTE_ORDER)) & (1 << i));
            if (bitIndex == i) {
                Assert.assertTrue(result);
            } else {
                Assert.assertFalse(("bit set i = " + i), result);
            }
        }
    }

    @Test
    public void shouldSetBitInInt() {
        final int bufferIndex = 8;
        long total = 0;
        for (int i = 0; i < 32; i++) {
            int bits = buffer.getInt(bufferIndex, TypesTest.BYTE_ORDER);
            bits = bits | (1 << i);
            buffer.putInt(bufferIndex, bits, TypesTest.BYTE_ORDER);
            total += 1 << i;
            Assert.assertThat(buffer.getInt(bufferIndex, TypesTest.BYTE_ORDER), Matchers.is(((int) (total))));
        }
    }
}

