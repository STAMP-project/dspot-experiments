/**
 * Copyright 2014 The gRPC Authors
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


import com.google.common.base.Charsets;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for the array-backed {@link ReadableBuffer} returned by {@link ReadableBuffers#wrap(byte[],
 * int, int)}.
 */
public class ReadableBuffersArrayTest extends ReadableBufferTestBase {
    @Test
    public void bufferShouldExposeArray() {
        byte[] array = ReadableBufferTestBase.msg.getBytes(Charsets.UTF_8);
        ReadableBuffer buffer = ReadableBuffers.wrap(array, 1, ((ReadableBufferTestBase.msg.length()) - 1));
        Assert.assertTrue(buffer.hasArray());
        Assert.assertSame(array, buffer.array());
        Assert.assertEquals(1, buffer.arrayOffset());
        // Now read a byte and verify that the offset changes.
        buffer.readUnsignedByte();
        Assert.assertEquals(2, buffer.arrayOffset());
    }
}

