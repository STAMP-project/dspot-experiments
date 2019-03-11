/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io.compress.zstd;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Random;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.CompressionOutputStream;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.hadoop.io.compress.ZStandardCodec;
import org.apache.hadoop.test.MultithreadedTestUtil;
import org.junit.Assert;
import org.junit.Test;


public class TestZStandardCompressorDecompressor {
    private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

    private static final Random RANDOM = new Random(12345L);

    private static final Configuration CONFIGURATION = new Configuration();

    private static File compressedFile;

    private static File uncompressedFile;

    @Test
    public void testCompressionCompressesCorrectly() throws Exception {
        int uncompressedSize = ((int) (FileUtils.sizeOf(TestZStandardCompressorDecompressor.uncompressedFile)));
        byte[] bytes = FileUtils.readFileToByteArray(TestZStandardCompressorDecompressor.uncompressedFile);
        Assert.assertEquals(uncompressedSize, bytes.length);
        Configuration conf = new Configuration();
        ZStandardCodec codec = new ZStandardCodec();
        codec.setConf(conf);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Compressor compressor = codec.createCompressor();
        CompressionOutputStream outputStream = codec.createOutputStream(baos, compressor);
        for (byte aByte : bytes) {
            outputStream.write(aByte);
        }
        outputStream.finish();
        outputStream.close();
        Assert.assertEquals(uncompressedSize, compressor.getBytesRead());
        Assert.assertTrue(compressor.finished());
        // just make sure we can decompress the file
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Decompressor decompressor = codec.createDecompressor();
        CompressionInputStream inputStream = codec.createInputStream(bais, decompressor);
        byte[] buffer = new byte[100];
        int n = buffer.length;
        while ((n = inputStream.read(buffer, 0, n)) != (-1)) {
            byteArrayOutputStream.write(buffer, 0, n);
        } 
        Assert.assertArrayEquals(bytes, byteArrayOutputStream.toByteArray());
    }

    @Test(expected = NullPointerException.class)
    public void testCompressorSetInputNullPointerException() {
        ZStandardCompressor compressor = new ZStandardCompressor();
        compressor.setInput(null, 0, 10);
    }

    // test on NullPointerException in {@code decompressor.setInput()}
    @Test(expected = NullPointerException.class)
    public void testDecompressorSetInputNullPointerException() {
        ZStandardDecompressor decompressor = new ZStandardDecompressor(CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT);
        decompressor.setInput(null, 0, 10);
    }

    // test on ArrayIndexOutOfBoundsException in {@code compressor.setInput()}
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testCompressorSetInputAIOBException() {
        ZStandardCompressor compressor = new ZStandardCompressor();
        compressor.setInput(new byte[]{  }, (-5), 10);
    }

    // test on ArrayIndexOutOfBoundsException in {@code decompressor.setInput()}
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testDecompressorSetInputAIOUBException() {
        ZStandardDecompressor decompressor = new ZStandardDecompressor(CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT);
        decompressor.setInput(new byte[]{  }, (-5), 10);
    }

    // test on NullPointerException in {@code compressor.compress()}
    @Test(expected = NullPointerException.class)
    public void testCompressorCompressNullPointerException() throws Exception {
        ZStandardCompressor compressor = new ZStandardCompressor();
        byte[] bytes = TestZStandardCompressorDecompressor.generate((1024 * 6));
        compressor.setInput(bytes, 0, bytes.length);
        compressor.compress(null, 0, 0);
    }

    // test on NullPointerException in {@code decompressor.decompress()}
    @Test(expected = NullPointerException.class)
    public void testDecompressorCompressNullPointerException() throws Exception {
        ZStandardDecompressor decompressor = new ZStandardDecompressor(CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT);
        byte[] bytes = TestZStandardCompressorDecompressor.generate((1024 * 6));
        decompressor.setInput(bytes, 0, bytes.length);
        decompressor.decompress(null, 0, 0);
    }

    // test on ArrayIndexOutOfBoundsException in {@code compressor.compress()}
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testCompressorCompressAIOBException() throws Exception {
        ZStandardCompressor compressor = new ZStandardCompressor();
        byte[] bytes = TestZStandardCompressorDecompressor.generate((1024 * 6));
        compressor.setInput(bytes, 0, bytes.length);
        compressor.compress(new byte[]{  }, 0, (-1));
    }

    // test on ArrayIndexOutOfBoundsException in decompressor.decompress()
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testDecompressorCompressAIOBException() throws Exception {
        ZStandardDecompressor decompressor = new ZStandardDecompressor(CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT);
        byte[] bytes = TestZStandardCompressorDecompressor.generate((1024 * 6));
        decompressor.setInput(bytes, 0, bytes.length);
        decompressor.decompress(new byte[]{  }, 0, (-1));
    }

    // test ZStandardCompressor compressor.compress()
    @Test
    public void testSetInputWithBytesSizeMoreThenDefaultZStandardBufferSize() throws Exception {
        int bytesSize = (1024 * 2056) + 1;
        ZStandardCompressor compressor = new ZStandardCompressor();
        byte[] bytes = TestZStandardCompressorDecompressor.generate(bytesSize);
        Assert.assertTrue("needsInput error !!!", compressor.needsInput());
        compressor.setInput(bytes, 0, bytes.length);
        byte[] emptyBytes = new byte[bytesSize];
        int cSize = compressor.compress(emptyBytes, 0, bytes.length);
        Assert.assertTrue((cSize > 0));
    }

    // test compress/decompress process through
    // CompressionOutputStream/CompressionInputStream api
    @Test
    public void testCompressorDecompressorLogicWithCompressionStreams() throws Exception {
        DataOutputStream deflateOut = null;
        DataInputStream inflateIn = null;
        int byteSize = 1024 * 100;
        byte[] bytes = TestZStandardCompressorDecompressor.generate(byteSize);
        int bufferSize = CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT;
        try {
            DataOutputBuffer compressedDataBuffer = new DataOutputBuffer();
            CompressionOutputStream deflateFilter = new org.apache.hadoop.io.compress.CompressorStream(compressedDataBuffer, new ZStandardCompressor(), bufferSize);
            deflateOut = new DataOutputStream(new BufferedOutputStream(deflateFilter));
            deflateOut.write(bytes, 0, bytes.length);
            deflateOut.flush();
            deflateFilter.finish();
            DataInputBuffer deCompressedDataBuffer = new DataInputBuffer();
            deCompressedDataBuffer.reset(compressedDataBuffer.getData(), 0, compressedDataBuffer.getLength());
            CompressionInputStream inflateFilter = new org.apache.hadoop.io.compress.DecompressorStream(deCompressedDataBuffer, new ZStandardDecompressor(bufferSize), bufferSize);
            inflateIn = new DataInputStream(new BufferedInputStream(inflateFilter));
            byte[] result = new byte[byteSize];
            inflateIn.read(result);
            Assert.assertArrayEquals("original array not equals compress/decompressed array", result, bytes);
        } finally {
            IOUtils.closeQuietly(deflateOut);
            IOUtils.closeQuietly(inflateIn);
        }
    }

    @Test
    public void testZStandardCompressDecompressInMultiThreads() throws Exception {
        MultithreadedTestUtil.TestContext ctx = new MultithreadedTestUtil.TestContext();
        for (int i = 0; i < 10; i++) {
            ctx.addThread(new MultithreadedTestUtil.TestingThread(ctx) {
                @Override
                public void doWork() throws Exception {
                    testCompressDecompress();
                }
            });
        }
        ctx.startThreads();
        ctx.waitFor(60000);
    }

    @Test
    public void testCompressDecompress() throws Exception {
        byte[] rawData;
        int rawDataSize;
        rawDataSize = CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT;
        rawData = TestZStandardCompressorDecompressor.generate(rawDataSize);
        ZStandardCompressor compressor = new ZStandardCompressor();
        ZStandardDecompressor decompressor = new ZStandardDecompressor(CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT);
        Assert.assertFalse(compressor.finished());
        compressor.setInput(rawData, 0, rawData.length);
        Assert.assertEquals(0, compressor.getBytesRead());
        compressor.finish();
        byte[] compressedResult = new byte[rawDataSize];
        int cSize = compressor.compress(compressedResult, 0, rawDataSize);
        Assert.assertEquals(rawDataSize, compressor.getBytesRead());
        Assert.assertTrue((cSize < rawDataSize));
        decompressor.setInput(compressedResult, 0, cSize);
        byte[] decompressedBytes = new byte[rawDataSize];
        decompressor.decompress(decompressedBytes, 0, decompressedBytes.length);
        Assert.assertEquals(TestZStandardCompressorDecompressor.bytesToHex(rawData), TestZStandardCompressorDecompressor.bytesToHex(decompressedBytes));
        compressor.reset();
        decompressor.reset();
    }

    @Test
    public void testCompressingWithOneByteOutputBuffer() throws Exception {
        int uncompressedSize = ((int) (FileUtils.sizeOf(TestZStandardCompressorDecompressor.uncompressedFile)));
        byte[] bytes = FileUtils.readFileToByteArray(TestZStandardCompressorDecompressor.uncompressedFile);
        Assert.assertEquals(uncompressedSize, bytes.length);
        Configuration conf = new Configuration();
        ZStandardCodec codec = new ZStandardCodec();
        codec.setConf(conf);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Compressor compressor = new ZStandardCompressor(3, CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT, 1);
        CompressionOutputStream outputStream = codec.createOutputStream(baos, compressor);
        for (byte aByte : bytes) {
            outputStream.write(aByte);
        }
        outputStream.finish();
        outputStream.close();
        Assert.assertEquals(uncompressedSize, compressor.getBytesRead());
        Assert.assertTrue(compressor.finished());
        // just make sure we can decompress the file
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        Decompressor decompressor = codec.createDecompressor();
        CompressionInputStream inputStream = codec.createInputStream(bais, decompressor);
        byte[] buffer = new byte[100];
        int n = buffer.length;
        while ((n = inputStream.read(buffer, 0, n)) != (-1)) {
            byteArrayOutputStream.write(buffer, 0, n);
        } 
        Assert.assertArrayEquals(bytes, byteArrayOutputStream.toByteArray());
    }

    @Test
    public void testZStandardCompressDecompress() throws Exception {
        byte[] rawData = null;
        int rawDataSize = 0;
        rawDataSize = CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT;
        rawData = TestZStandardCompressorDecompressor.generate(rawDataSize);
        ZStandardCompressor compressor = new ZStandardCompressor();
        ZStandardDecompressor decompressor = new ZStandardDecompressor(rawDataSize);
        Assert.assertTrue(compressor.needsInput());
        Assert.assertFalse("testZStandardCompressDecompress finished error", compressor.finished());
        compressor.setInput(rawData, 0, rawData.length);
        compressor.finish();
        byte[] compressedResult = new byte[rawDataSize];
        int cSize = compressor.compress(compressedResult, 0, rawDataSize);
        Assert.assertEquals(rawDataSize, compressor.getBytesRead());
        Assert.assertTrue("compressed size no less then original size", (cSize < rawDataSize));
        decompressor.setInput(compressedResult, 0, cSize);
        byte[] decompressedBytes = new byte[rawDataSize];
        decompressor.decompress(decompressedBytes, 0, decompressedBytes.length);
        String decompressed = TestZStandardCompressorDecompressor.bytesToHex(decompressedBytes);
        String original = TestZStandardCompressorDecompressor.bytesToHex(rawData);
        Assert.assertEquals(original, decompressed);
        compressor.reset();
        decompressor.reset();
    }

    @Test
    public void testDecompressingOutput() throws Exception {
        byte[] expectedDecompressedResult = FileUtils.readFileToByteArray(TestZStandardCompressorDecompressor.uncompressedFile);
        ZStandardCodec codec = new ZStandardCodec();
        codec.setConf(TestZStandardCompressorDecompressor.CONFIGURATION);
        CompressionInputStream inputStream = codec.createInputStream(FileUtils.openInputStream(TestZStandardCompressorDecompressor.compressedFile), codec.createDecompressor());
        byte[] toDecompress = new byte[100];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] decompressedResult;
        int totalFileSize = 0;
        int result = toDecompress.length;
        try {
            while ((result = inputStream.read(toDecompress, 0, result)) != (-1)) {
                baos.write(toDecompress, 0, result);
                totalFileSize += result;
            } 
            decompressedResult = baos.toByteArray();
        } finally {
            IOUtils.closeQuietly(baos);
        }
        Assert.assertEquals(decompressedResult.length, totalFileSize);
        Assert.assertEquals(TestZStandardCompressorDecompressor.bytesToHex(expectedDecompressedResult), TestZStandardCompressorDecompressor.bytesToHex(decompressedResult));
    }

    @Test
    public void testZStandardDirectCompressDecompress() throws Exception {
        int[] size = new int[]{ 1, 4, 16, 4 * 1024, 64 * 1024, 128 * 1024, 1024 * 1024 };
        for (int aSize : size) {
            System.out.println(("aSize = " + aSize));
            compressDecompressLoop(aSize);
        }
    }

    @Test
    public void testReadingWithAStream() throws Exception {
        FileInputStream inputStream = FileUtils.openInputStream(TestZStandardCompressorDecompressor.compressedFile);
        ZStandardCodec codec = new ZStandardCodec();
        codec.setConf(TestZStandardCompressorDecompressor.CONFIGURATION);
        Decompressor decompressor = codec.createDecompressor();
        CompressionInputStream cis = codec.createInputStream(inputStream, decompressor);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] resultOfDecompression;
        try {
            byte[] buffer = new byte[100];
            int n;
            while ((n = cis.read(buffer, 0, buffer.length)) != (-1)) {
                baos.write(buffer, 0, n);
            } 
            resultOfDecompression = baos.toByteArray();
        } finally {
            IOUtils.closeQuietly(baos);
            IOUtils.closeQuietly(cis);
        }
        byte[] expected = FileUtils.readFileToByteArray(TestZStandardCompressorDecompressor.uncompressedFile);
        Assert.assertEquals(TestZStandardCompressorDecompressor.bytesToHex(expected), TestZStandardCompressorDecompressor.bytesToHex(resultOfDecompression));
    }

    @Test
    public void testDecompressReturnsWhenNothingToDecompress() throws Exception {
        ZStandardDecompressor decompressor = new ZStandardDecompressor(CommonConfigurationKeysPublic.IO_FILE_BUFFER_SIZE_DEFAULT);
        int result = decompressor.decompress(new byte[10], 0, 10);
        Assert.assertEquals(0, result);
    }
}

