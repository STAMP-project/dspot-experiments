/**
 * Copyright 2015 The gRPC Authors
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
package io.grpc.internal;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Abstract base class for tests of {@link WritableBuffer} subclasses.
 */
@RunWith(JUnit4.class)
public abstract class WritableBufferTestBase {
    @Test(expected = RuntimeException.class)
    public void testWriteNegativeLength() {
        buffer().write(new byte[1], 0, (-1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testWriteNegativeSrcIndex() {
        buffer().write(new byte[1], (-1), 0);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testWriteSrcIndexAndLengthExceedSrcLength() {
        buffer().write(new byte[10], 1, 10);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testWriteSrcIndexAndLengthExceedWritableBytes() {
        buffer().write(new byte[buffer().writableBytes()], 1, buffer().writableBytes());
    }

    @Test
    public void testWritableAndReadableBytes() {
        int before = buffer().writableBytes();
        buffer().write(new byte[10], 0, 5);
        Assert.assertEquals(5, (before - (buffer().writableBytes())));
        Assert.assertEquals(5, buffer().readableBytes());
    }

    @Test
    public void testWriteSrcIndex() {
        byte[] b = new byte[10];
        for (byte i = 5; i < 10; i++) {
            b[i] = i;
        }
        buffer().write(b, 5, 5);
        Assert.assertEquals(5, buffer().readableBytes());
        byte[] writtenBytes = writtenBytes();
        Assert.assertEquals(5, writtenBytes.length);
        for (int i = 0; i < (writtenBytes.length); i++) {
            Assert.assertEquals((5 + i), writtenBytes[i]);
        }
    }

    @Test
    public void testMultipleWrites() {
        byte[] b = new byte[100];
        for (byte i = 0; i < (b.length); i++) {
            b[i] = i;
        }
        // Write in chunks of 10 bytes
        for (int i = 0; i < 10; i++) {
            buffer().write(b, (10 * i), 10);
            Assert.assertEquals((10 * (i + 1)), buffer().readableBytes());
        }
        Assert.assertArrayEquals(b, writtenBytes());
    }
}

