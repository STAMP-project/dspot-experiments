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
package org.apache.hadoop.fs.azurebfs;


import ConfigurationKeys.FS_AZURE_READ_AHEAD_QUEUE_DEPTH;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.azurebfs.constants.ConfigurationKeys;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test end to end between ABFS client and ABFS server.
 */
public class ITestAzureBlobFileSystemE2E extends AbstractAbfsIntegrationTest {
    private static final int TEST_BYTE = 100;

    private static final int TEST_OFFSET = 100;

    private static final int TEST_DEFAULT_BUFFER_SIZE = (4 * 1024) * 1024;

    private static final int TEST_DEFAULT_READ_BUFFER_SIZE = 1023900;

    public ITestAzureBlobFileSystemE2E() throws Exception {
        super();
        AbfsConfiguration configuration = this.getConfiguration();
        configuration.set(FS_AZURE_READ_AHEAD_QUEUE_DEPTH, "0");
    }

    @Test
    public void testWriteOneByteToFile() throws Exception {
        final Path testFilePath = new Path(methodName.getMethodName());
        testWriteOneByteToFile(testFilePath);
    }

    @Test
    public void testReadWriteBytesToFile() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Path testFilePath = new Path(methodName.getMethodName());
        testWriteOneByteToFile(testFilePath);
        try (FSDataInputStream inputStream = fs.open(testFilePath, ITestAzureBlobFileSystemE2E.TEST_DEFAULT_BUFFER_SIZE)) {
            Assert.assertEquals(ITestAzureBlobFileSystemE2E.TEST_BYTE, inputStream.read());
        }
    }

    @Test(expected = IOException.class)
    public void testOOBWritesAndReadFail() throws Exception {
        Configuration conf = this.getRawConfiguration();
        conf.setBoolean(ConfigurationKeys.AZURE_TOLERATE_CONCURRENT_APPEND, false);
        final AzureBlobFileSystem fs = getFileSystem();
        int readBufferSize = fs.getAbfsStore().getAbfsConfiguration().getReadBufferSize();
        byte[] bytesToRead = new byte[readBufferSize];
        final byte[] b = new byte[2 * readBufferSize];
        new Random().nextBytes(b);
        final Path testFilePath = new Path(methodName.getMethodName());
        try (FSDataOutputStream writeStream = fs.create(testFilePath)) {
            writeStream.write(b);
            writeStream.flush();
        }
        try (FSDataInputStream readStream = fs.open(testFilePath)) {
            Assert.assertEquals(readBufferSize, readStream.read(bytesToRead, 0, readBufferSize));
            try (FSDataOutputStream writeStream = fs.create(testFilePath)) {
                writeStream.write(b);
                writeStream.flush();
            }
            Assert.assertEquals(readBufferSize, readStream.read(bytesToRead, 0, readBufferSize));
        }
    }

    @Test
    public void testOOBWritesAndReadSucceed() throws Exception {
        Configuration conf = this.getRawConfiguration();
        conf.setBoolean(ConfigurationKeys.AZURE_TOLERATE_CONCURRENT_APPEND, true);
        final AzureBlobFileSystem fs = getFileSystem(conf);
        int readBufferSize = fs.getAbfsStore().getAbfsConfiguration().getReadBufferSize();
        byte[] bytesToRead = new byte[readBufferSize];
        final byte[] b = new byte[2 * readBufferSize];
        new Random().nextBytes(b);
        final Path testFilePath = new Path(methodName.getMethodName());
        try (FSDataOutputStream writeStream = fs.create(testFilePath)) {
            writeStream.write(b);
            writeStream.flush();
        }
        try (FSDataInputStream readStream = fs.open(testFilePath)) {
            // Read
            Assert.assertEquals(readBufferSize, readStream.read(bytesToRead, 0, readBufferSize));
            // Concurrent write
            try (FSDataOutputStream writeStream = fs.create(testFilePath)) {
                writeStream.write(b);
                writeStream.flush();
            }
            Assert.assertEquals(readBufferSize, readStream.read(bytesToRead, 0, readBufferSize));
        }
    }

    @Test
    public void testWriteWithBufferOffset() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Path testFilePath = new Path(methodName.getMethodName());
        final byte[] b = new byte[1024 * 1000];
        new Random().nextBytes(b);
        try (FSDataOutputStream stream = fs.create(testFilePath)) {
            stream.write(b, ITestAzureBlobFileSystemE2E.TEST_OFFSET, ((b.length) - (ITestAzureBlobFileSystemE2E.TEST_OFFSET)));
        }
        final byte[] r = new byte[ITestAzureBlobFileSystemE2E.TEST_DEFAULT_READ_BUFFER_SIZE];
        FSDataInputStream inputStream = fs.open(testFilePath, ITestAzureBlobFileSystemE2E.TEST_DEFAULT_BUFFER_SIZE);
        int result = inputStream.read(r);
        Assert.assertNotEquals((-1), result);
        Assert.assertArrayEquals(r, Arrays.copyOfRange(b, ITestAzureBlobFileSystemE2E.TEST_OFFSET, b.length));
        inputStream.close();
    }

    @Test
    public void testReadWriteHeavyBytesToFileWithSmallerChunks() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Path testFilePath = new Path(methodName.getMethodName());
        final byte[] writeBuffer = new byte[(5 * 1000) * 1024];
        new Random().nextBytes(writeBuffer);
        write(testFilePath, writeBuffer);
        final byte[] readBuffer = new byte[(5 * 1000) * 1024];
        FSDataInputStream inputStream = fs.open(testFilePath, ITestAzureBlobFileSystemE2E.TEST_DEFAULT_BUFFER_SIZE);
        int offset = 0;
        while ((inputStream.read(readBuffer, offset, ITestAzureBlobFileSystemE2E.TEST_OFFSET)) > 0) {
            offset += ITestAzureBlobFileSystemE2E.TEST_OFFSET;
        } 
        Assert.assertArrayEquals(readBuffer, writeBuffer);
        inputStream.close();
    }

    @Test
    public void testReadWithFileNotFoundException() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Path testFilePath = new Path(methodName.getMethodName());
        testWriteOneByteToFile(testFilePath);
        FSDataInputStream inputStream = fs.open(testFilePath, ITestAzureBlobFileSystemE2E.TEST_DEFAULT_BUFFER_SIZE);
        fs.delete(testFilePath, true);
        Assert.assertFalse(fs.exists(testFilePath));
        intercept(FileNotFoundException.class, () -> inputStream.read(new byte[1]));
    }

    @Test
    public void testWriteWithFileNotFoundException() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Path testFilePath = new Path(methodName.getMethodName());
        FSDataOutputStream stream = fs.create(testFilePath);
        Assert.assertTrue(fs.exists(testFilePath));
        stream.write(ITestAzureBlobFileSystemE2E.TEST_BYTE);
        fs.delete(testFilePath, true);
        Assert.assertFalse(fs.exists(testFilePath));
        // trigger append call
        intercept(FileNotFoundException.class, () -> stream.close());
    }

    @Test
    public void testFlushWithFileNotFoundException() throws Exception {
        final AzureBlobFileSystem fs = getFileSystem();
        final Path testFilePath = new Path(methodName.getMethodName());
        FSDataOutputStream stream = fs.create(testFilePath);
        Assert.assertTrue(fs.exists(testFilePath));
        fs.delete(testFilePath, true);
        Assert.assertFalse(fs.exists(testFilePath));
        intercept(FileNotFoundException.class, () -> stream.close());
    }
}

