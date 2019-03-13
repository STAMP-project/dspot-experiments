/**
 * Copyright 2015-present Open Networking Foundation
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
package io.atomix.storage.buffer;


import java.io.File;
import java.nio.file.Files;
import org.junit.Assert;
import org.junit.Test;


/**
 * File buffer test.
 *
 * @author <a href="http://github.com/kuujo">Jordan Halterman</a>
 */
public class FileBufferTest extends BufferTest {
    @Test
    public void testFileToHeapBuffer() {
        File file = FileTesting.createFile();
        try (FileBuffer buffer = FileBuffer.allocate(file, 16)) {
            buffer.writeLong(10).writeLong(11).flip();
            byte[] bytes = new byte[16];
            buffer.read(bytes).rewind();
            HeapBuffer heapBuffer = HeapBuffer.wrap(bytes);
            Assert.assertEquals(buffer.readLong(), heapBuffer.readLong());
            Assert.assertEquals(buffer.readLong(), heapBuffer.readLong());
        }
    }

    /**
     * Rests reopening a file that has been closed.
     */
    @Test
    public void testPersist() {
        File file = FileTesting.createFile();
        try (FileBuffer buffer = FileBuffer.allocate(file, 16)) {
            buffer.writeLong(10).writeLong(11).flip();
            Assert.assertEquals(10, buffer.readLong());
            Assert.assertEquals(11, buffer.readLong());
        }
        try (FileBuffer buffer = FileBuffer.allocate(file, 16)) {
            Assert.assertEquals(10, buffer.readLong());
            Assert.assertEquals(11, buffer.readLong());
        }
    }

    /**
     * Tests deleting a file.
     */
    @Test
    public void testDelete() {
        File file = FileTesting.createFile();
        FileBuffer buffer = FileBuffer.allocate(file, 16);
        buffer.writeLong(10).writeLong(11).flip();
        Assert.assertEquals(10, buffer.readLong());
        Assert.assertEquals(11, buffer.readLong());
        Assert.assertTrue(Files.exists(file.toPath()));
        buffer.delete();
        Assert.assertFalse(Files.exists(file.toPath()));
    }
}

