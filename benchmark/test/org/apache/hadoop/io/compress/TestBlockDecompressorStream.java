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
package org.apache.hadoop.io.compress;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;


public class TestBlockDecompressorStream {
    private byte[] buf;

    private ByteArrayInputStream bytesIn;

    private ByteArrayOutputStream bytesOut;

    @Test
    public void testRead1() throws IOException {
        testRead(0);
    }

    @Test
    public void testRead2() throws IOException {
        // Test eof after getting non-zero block size info
        testRead(4);
    }

    @Test
    public void testReadWhenIoExceptionOccure() throws IOException {
        File file = new File("testReadWhenIOException");
        try {
            file.createNewFile();
            InputStream io = new FileInputStream(file) {
                @Override
                public int read() throws IOException {
                    throw new IOException("File blocks missing");
                }
            };
            try (BlockDecompressorStream blockDecompressorStream = new BlockDecompressorStream(io, new FakeDecompressor(), 1024)) {
                int byteRead = blockDecompressorStream.read();
                Assert.fail(("Should not return -1 in case of IOException. Byte read " + byteRead));
            } catch (IOException e) {
                Assert.assertTrue(e.getMessage().contains("File blocks missing"));
            }
        } finally {
            file.delete();
        }
    }
}

