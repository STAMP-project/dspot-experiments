/**
 * Copyright (C) 2014 Markus Junginger, greenrobot (http://greenrobot.de)
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
package org.greenrobot.essentials.hash;


import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class Murmur3FTest extends AbstractChecksumTest {
    private final Murmur3F murmur3F;

    public Murmur3FTest() {
        super(new Murmur3F());
        murmur3F = ((Murmur3F) (checksum));
    }

    @Test
    public void testExpectedHash() {
        // 0 MurmurHash3_x64_128 hash lo: cfa0f7ddd84c76bc hash hi: 589623161cf526f1
        // I4 MurmurHash3_x64_128 hash lo: 14885fe730885297 hash hi: 1e5a73f96044885e
        // I16 MurmurHash3_x64_128 hash lo: edb199d42d778ebb hash hi: c6dec4069552440b
        super.testExpectedHash(-3485513579396041028L, 1479537924147532439L, -1319104079267918149L);
    }

    @Test
    public void testCompareWithGuava() {
        byte[] bytes = new byte[1024];
        new Random(42).nextBytes(bytes);
        for (int i = 0; i <= (bytes.length); i++) {
            HashCode hashCode = Hashing.murmur3_128().hashBytes(bytes, 0, i);
            long expected = hashCode.asLong();// 64 bit is enough

            checksum.reset();
            checksum.update(bytes, 0, i);
            Assert.assertEquals(("Iteration " + i), expected, checksum.getValue());
        }
        for (int i = 0; i < (bytes.length); i++) {
            HashCode hashCode = Hashing.murmur3_128().hashBytes(bytes, i, ((bytes.length) - i));
            long expected = hashCode.asLong();// 64 bit is enough

            checksum.reset();
            checksum.update(bytes, i, ((bytes.length) - i));
            Assert.assertEquals(("Iteration " + i), expected, checksum.getValue());
        }
    }

    @Test
    public void testGetValueBytesAgainstGuava() {
        byte[] expected = Hashing.murmur3_128().hashBytes(AbstractChecksumTest.INPUT4).asBytes();
        murmur3F.update(AbstractChecksumTest.INPUT4, 0, Murmur3FTest.INPUT4.length);
        byte[] bytes = murmur3F.getValueBytesLittleEndian();
        Assert.assertArrayEquals(expected, bytes);
    }

    @Test
    public void testGetValueBytesEndian() {
        murmur3F.update(AbstractChecksumTest.INPUT4, 0, Murmur3FTest.INPUT4.length);
        byte[] bytesLE = murmur3F.getValueBytesLittleEndian();
        byte[] bytesBE = murmur3F.getValueBytesBigEndian();
        for (int i = 0; i < (bytesLE.length); i++) {
            Assert.assertEquals(bytesLE[i], bytesBE[(((bytesBE.length) - 1) - i)]);
        }
    }

    @Test
    public void testGetBigValue() {
        murmur3F.update(42);
        String expected = (Long.toHexString(murmur3F.getValueHigh())) + (Long.toHexString(murmur3F.getValue()));
        Assert.assertEquals(32, expected.length());// For this particular hash OK

        String big = murmur3F.getValueBigInteger().toString(16);
        Assert.assertEquals(expected, big);
    }

    @Test
    public void testGetValueHexString() {
        murmur3F.update(42);
        String expected = (Long.toHexString(murmur3F.getValueHigh())) + (Long.toHexString(murmur3F.getValue()));
        Assert.assertEquals(32, expected.length());// For this particular hash OK

        Assert.assertEquals(expected, murmur3F.getValueHexString());
    }

    @Test
    public void testGetValueHexStringPadded() {
        while (true) {
            murmur3F.update(42);
            String nonPadded = Long.toHexString(murmur3F.getValueHigh());
            int delta = 16 - (nonPadded.length());
            if (delta > 0) {
                String padded = murmur3F.getValueHexString();
                for (int i = 0; i < delta; i++) {
                    Assert.assertEquals('0', padded.charAt(i));
                }
                Assert.assertNotEquals('0', padded.charAt(delta));
                if (delta > 2) {
                    break;
                }
            }
        } 
    }

    @Test
    public void testSeedsAgainsGuava() {
        byte[] bytes = new byte[32];
        new Random(42).nextBytes(bytes);
        // TODO Negative seeds are interpreted differently than Guava, double check with reference implementation
        // int[] seeds = {0, 1, -1, 42, -1000, Integer.MIN_VALUE, Integer.MAX_VALUE};
        int[] seeds = new int[]{ 0, 1, 42, Integer.MAX_VALUE };
        for (int i = 0; i < (seeds.length); i++) {
            HashCode hashCode = Hashing.murmur3_128(seeds[i]).hashBytes(bytes, 0, bytes.length);
            long expected = hashCode.asLong();// 64 bit is enough

            Murmur3F murmur3FSeeded = new Murmur3F(seeds[i]);
            murmur3FSeeded.update(bytes, 0, bytes.length);
            Assert.assertEquals(("Iteration " + i), expected, murmur3FSeeded.getValue());
            murmur3FSeeded.reset();
            murmur3FSeeded.update(bytes, 0, bytes.length);
            Assert.assertEquals(("Iteration " + i), expected, murmur3FSeeded.getValue());
        }
        for (int i = 0; i < (bytes.length); i++) {
            HashCode hashCode = Hashing.murmur3_128().hashBytes(bytes, i, ((bytes.length) - i));
            long expected = hashCode.asLong();// 64 bit is enough

            checksum.reset();
            checksum.update(bytes, i, ((bytes.length) - i));
            Assert.assertEquals(("Iteration " + i), expected, checksum.getValue());
        }
    }

    @Test
    public void testUpdateLongBE() throws IOException {
        doTestUpdateLong(ByteOrder.BIG_ENDIAN);
    }

    @Test
    public void testUpdateLongLE() throws IOException {
        doTestUpdateLong(ByteOrder.LITTLE_ENDIAN);
    }
}

