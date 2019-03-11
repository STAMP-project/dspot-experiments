/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.file;


import java.io.IOException;
import java.nio.ByteBuffer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TestAllCodecs {
    @Parameterized.Parameter(0)
    public String codec;

    @Parameterized.Parameter(1)
    public Class<? extends Codec> codecClass;

    @Test
    public void testCodec() throws IOException {
        int inputSize = 500000;
        byte[] input = TestAllCodecs.generateTestData(inputSize);
        Codec codecInstance = CodecFactory.fromString(codec).createInstance();
        Assert.assertTrue(codecClass.isInstance(codecInstance));
        Assert.assertTrue(codecInstance.getName().equals(codec));
        ByteBuffer inputByteBuffer = ByteBuffer.wrap(input);
        ByteBuffer compressedBuffer = codecInstance.compress(inputByteBuffer);
        int compressedSize = compressedBuffer.remaining();
        // Make sure something returned
        Assert.assertTrue((compressedSize > 0));
        // While the compressed size could in many real cases
        // *increase* compared to the input size, our input data
        // is extremely easy to compress and all Avro's compression algorithms
        // should have a compression ratio greater than 1 (except 'null').
        Assert.assertTrue(((compressedSize < inputSize) || (codec.equals("null"))));
        // Decompress the data
        ByteBuffer decompressedBuffer = codecInstance.decompress(compressedBuffer);
        // Validate the the input and output are equal.
        inputByteBuffer.rewind();
        Assert.assertEquals(decompressedBuffer, inputByteBuffer);
    }

    @Test
    public void testCodecSlice() throws IOException {
        int inputSize = 500000;
        byte[] input = TestAllCodecs.generateTestData(inputSize);
        Codec codecInstance = CodecFactory.fromString(codec).createInstance();
        ByteBuffer partialBuffer = ByteBuffer.wrap(input);
        partialBuffer.position(17);
        ByteBuffer inputByteBuffer = partialBuffer.slice();
        ByteBuffer compressedBuffer = codecInstance.compress(inputByteBuffer);
        int compressedSize = compressedBuffer.remaining();
        // Make sure something returned
        Assert.assertTrue((compressedSize > 0));
        // Create a slice from the compressed buffer
        ByteBuffer sliceBuffer = ByteBuffer.allocate((compressedSize + 100));
        sliceBuffer.position(50);
        sliceBuffer.put(compressedBuffer);
        sliceBuffer.limit((compressedSize + 50));
        sliceBuffer.position(50);
        // Decompress the data
        ByteBuffer decompressedBuffer = codecInstance.decompress(sliceBuffer.slice());
        // Validate the the input and output are equal.
        inputByteBuffer.rewind();
        Assert.assertEquals(decompressedBuffer, inputByteBuffer);
    }
}

