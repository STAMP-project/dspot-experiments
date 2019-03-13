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
package org.apache.flink.runtime.io.disk.iomanager;


import java.io.IOException;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.flink.runtime.io.network.buffer.Buffer;
import org.apache.flink.runtime.io.network.buffer.BufferRecycler;
import org.apache.flink.runtime.io.network.buffer.FreeingBufferRecycler;
import org.junit.Assert;
import org.junit.Test;


public class BufferFileWriterReaderTest {
    private static final int BUFFER_SIZE = 32 * 1024;

    private static final BufferRecycler BUFFER_RECYCLER = FreeingBufferRecycler.INSTANCE;

    private static final Random random = new Random();

    private static final IOManager ioManager = new IOManagerAsync();

    private BufferFileWriter writer;

    private BufferFileReader reader;

    private LinkedBlockingQueue<Buffer> returnedBuffers = new LinkedBlockingQueue<>();

    @Test
    public void testWriteRead() throws IOException {
        int numBuffers = 1024;
        int currentNumber = 0;
        final int minBufferSize = (BufferFileWriterReaderTest.BUFFER_SIZE) / 4;
        // Write buffers filled with ascending numbers...
        for (int i = 0; i < numBuffers; i++) {
            final Buffer buffer = createBuffer();
            int size = getNextMultipleOf(getRandomNumberInRange(minBufferSize, BufferFileWriterReaderTest.BUFFER_SIZE), 4);
            currentNumber = BufferFileWriterReaderTest.fillBufferWithAscendingNumbers(buffer, currentNumber, size);
            writer.writeBlock(buffer);
        }
        // Make sure that the writes are finished
        writer.close();
        // Read buffers back in...
        for (int i = 0; i < numBuffers; i++) {
            Assert.assertFalse(reader.hasReachedEndOfFile());
            reader.readInto(createBuffer());
        }
        reader.close();
        Assert.assertTrue(reader.hasReachedEndOfFile());
        // Verify that the content is the same
        Assert.assertEquals("Read less buffers than written.", numBuffers, returnedBuffers.size());
        currentNumber = 0;
        Buffer buffer;
        while ((buffer = returnedBuffers.poll()) != null) {
            currentNumber = BufferFileWriterReaderTest.verifyBufferFilledWithAscendingNumbers(buffer, currentNumber);
        } 
    }

    @Test
    public void testWriteSkipRead() throws IOException {
        int numBuffers = 1024;
        int currentNumber = 0;
        // Write buffers filled with ascending numbers...
        for (int i = 0; i < numBuffers; i++) {
            final Buffer buffer = createBuffer();
            currentNumber = BufferFileWriterReaderTest.fillBufferWithAscendingNumbers(buffer, currentNumber, buffer.getMaxCapacity());
            writer.writeBlock(buffer);
        }
        // Make sure that the writes are finished
        writer.close();
        final int toSkip = 32;
        // Skip first buffers...
        reader.seekToPosition(((8 + (BufferFileWriterReaderTest.BUFFER_SIZE)) * toSkip));
        numBuffers -= toSkip;
        // Read buffers back in...
        for (int i = 0; i < numBuffers; i++) {
            Assert.assertFalse(reader.hasReachedEndOfFile());
            reader.readInto(createBuffer());
        }
        reader.close();
        Assert.assertTrue(reader.hasReachedEndOfFile());
        // Verify that the content is the same
        Assert.assertEquals("Read less buffers than written.", numBuffers, returnedBuffers.size());
        // Start number after skipped buffers...
        currentNumber = ((BufferFileWriterReaderTest.BUFFER_SIZE) / 4) * toSkip;
        Buffer buffer;
        while ((buffer = returnedBuffers.poll()) != null) {
            currentNumber = BufferFileWriterReaderTest.verifyBufferFilledWithAscendingNumbers(buffer, currentNumber);
        } 
    }
}

