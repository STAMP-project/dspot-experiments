/**
 * Copyright (C)2009 - SSHJ Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.schmizz.sshj.common;


import Buffer.DEFAULT_SIZE;
import java.math.BigInteger;
import net.schmizz.sshj.common.Buffer.BufferException;
import net.schmizz.sshj.common.Buffer.PlainBuffer;
import org.junit.Assert;
import org.junit.Test;

import static Buffer.DEFAULT_SIZE;


public class BufferTest {
    // Issue 72: previously, it entered an infinite loop trying to establish the buffer size
    @Test
    public void shouldThrowOnTooLargeCapacity() {
        PlainBuffer buffer = new PlainBuffer();
        try {
            buffer.ensureCapacity(Integer.MAX_VALUE);
            Assert.fail(("Allegedly ensured buffer capacity of size " + (Integer.MAX_VALUE)));
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    // Issue 72: previously, it entered an infinite loop trying to establish the buffer size
    @Test
    public void shouldThrowOnTooLargeInitialCapacity() {
        try {
            new PlainBuffer(Integer.MAX_VALUE);
            Assert.fail(("Allegedly created buffer with size " + (Integer.MAX_VALUE)));
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void shouldThrowOnPutNegativeLongUInt64() {
        try {
            new PlainBuffer().putUInt64((-1L));
            Assert.fail("Added negative uint64 to buffer?");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void shouldThrowOnReadNegativeLongUInt64() {
        byte[] negativeLong = new byte[]{ ((byte) (128)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (1)) };
        Buffer<?> buff = new PlainBuffer(negativeLong);
        try {
            buff.readUInt64();
            Assert.fail("Read negative uint64 from buffer?");
        } catch (BufferException e) {
            // success
        }
    }

    @Test
    public void shouldThrowOnPutNegativeBigIntegerUInt64() {
        try {
            new PlainBuffer().putUInt64(new BigInteger("-1"));
            Assert.fail("Added negative uint64 to buffer?");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void shouldHaveCorrectValueForMaxUInt64() {
        byte[] maxUInt64InBytes = new byte[]{ ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)), ((byte) (255)) };
        BigInteger maxUInt64 = new BigInteger(1, maxUInt64InBytes);
        new PlainBuffer().putUInt64(maxUInt64);// no exception

        BigInteger tooBig = maxUInt64.add(BigInteger.ONE);
        try {
            new PlainBuffer().putUInt64(tooBig);
            Assert.fail("Added 2^64 (too big) as uint64 to buffer?");
        } catch (IllegalArgumentException e) {
            // success
        }
    }

    @Test
    public void shouldCorrectlyEncodeAndDecodeUInt64Types() throws BufferException {
        // This number fits into a unsigned 64 bit integer but not a signed one.
        BigInteger bigUint64 = BigInteger.valueOf(Long.MAX_VALUE).add(BigInteger.ONE).add(BigInteger.ONE);
        Assert.assertEquals(-9223372036854775807L, bigUint64.longValue());
        Buffer<PlainBuffer> buff = new PlainBuffer();
        buff.putUInt64(bigUint64);
        byte[] data = buff.getCompactData();
        Assert.assertEquals(8, data.length);
        Assert.assertEquals(((byte) (128)), data[0]);
        Assert.assertEquals(((byte) (0)), data[1]);
        Assert.assertEquals(((byte) (0)), data[2]);
        Assert.assertEquals(((byte) (0)), data[3]);
        Assert.assertEquals(((byte) (0)), data[4]);
        Assert.assertEquals(((byte) (0)), data[5]);
        Assert.assertEquals(((byte) (0)), data[6]);
        Assert.assertEquals(((byte) (1)), data[7]);
        byte[] asBinary = new byte[]{ ((byte) (128)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (0)), ((byte) (1)) };
        buff = new PlainBuffer(asBinary);
        Assert.assertEquals(bigUint64, buff.readUInt64AsBigInteger());
    }

    @Test
    public void shouldHaveSameUInt64EncodingForBigIntegerAndLong() {
        long[] values = new long[]{ 0L, 1L, 232634978082517765L, (Long.MAX_VALUE) - 1, Long.MAX_VALUE };
        for (long value : values) {
            byte[] bytesBigInt = new PlainBuffer().putUInt64(BigInteger.valueOf(value)).getCompactData();
            byte[] bytesLong = new PlainBuffer().putUInt64(value).getCompactData();
            Assert.assertArrayEquals(("Value: " + value), bytesLong, bytesBigInt);
        }
    }

    @Test
    public void shouldExpandCapacityOfUInt32() {
        PlainBuffer buf = new PlainBuffer();
        for (int i = 0; i < ((DEFAULT_SIZE) + 1); i += 4) {
            buf.putUInt32(1L);
        }
        /* Buffer should have been expanded at this point */
        Assert.assertEquals(((DEFAULT_SIZE) * 2), buf.data.length);
    }

    @Test
    public void shouldExpandCapacityOfUInt64() {
        BigInteger bigUint64 = BigInteger.valueOf(Long.MAX_VALUE);
        PlainBuffer buf = new PlainBuffer();
        Assert.assertEquals(DEFAULT_SIZE, buf.data.length);
        for (int i = 0; i < ((DEFAULT_SIZE) + 1); i += 8) {
            buf.putUInt64(bigUint64.longValue());
        }
        /* Buffer should have been expanded at this point */
        Assert.assertEquals(((DEFAULT_SIZE) * 2), buf.data.length);
    }
}

