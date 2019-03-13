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
package org.apache.hadoop.io.compress.zlib;


import CompressionLevel.BEST_COMPRESSION;
import CompressionStrategy.FILTERED;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.compress.CompressDecompressTester;
import org.apache.hadoop.io.compress.Compressor;
import org.apache.hadoop.io.compress.Decompressor;
import org.apache.hadoop.io.compress.zlib.ZlibCompressor.CompressionHeader;
import org.apache.hadoop.io.compress.zlib.ZlibCompressor.CompressionLevel;
import org.apache.hadoop.io.compress.zlib.ZlibCompressor.CompressionStrategy;
import org.apache.hadoop.test.MultithreadedTestUtil;
import org.apache.hadoop.util.NativeCodeLoader;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;

import static org.apache.hadoop.io.compress.CompressDecompressTester.CompressionTestStrategy.COMPRESS_DECOMPRESS_BLOCK;
import static org.apache.hadoop.io.compress.CompressDecompressTester.CompressionTestStrategy.COMPRESS_DECOMPRESS_ERRORS;
import static org.apache.hadoop.io.compress.CompressDecompressTester.CompressionTestStrategy.COMPRESS_DECOMPRESS_SINGLE_BLOCK;
import static org.apache.hadoop.io.compress.CompressDecompressTester.CompressionTestStrategy.COMPRESS_DECOMPRESS_WITH_EMPTY_STREAM;
import static org.apache.hadoop.io.compress.zlib.ZlibDecompressor.CompressionHeader.DEFAULT_HEADER;


public class TestZlibCompressorDecompressor {
    private static final Random random = new Random(12345L);

    @Test
    public void testZlibCompressorDecompressor() {
        try {
            int SIZE = 44 * 1024;
            byte[] rawData = TestZlibCompressorDecompressor.generate(SIZE);
            CompressDecompressTester.of(rawData).withCompressDecompressPair(new ZlibCompressor(), new ZlibDecompressor()).withTestCases(ImmutableSet.of(COMPRESS_DECOMPRESS_SINGLE_BLOCK, COMPRESS_DECOMPRESS_BLOCK, COMPRESS_DECOMPRESS_ERRORS, COMPRESS_DECOMPRESS_WITH_EMPTY_STREAM)).test();
        } catch (Exception ex) {
            Assert.fail(("testCompressorDecompressor error !!!" + ex));
        }
    }

    @Test
    public void testCompressorDecompressorWithExeedBufferLimit() {
        int BYTE_SIZE = 100 * 1024;
        byte[] rawData = TestZlibCompressorDecompressor.generate(BYTE_SIZE);
        try {
            CompressDecompressTester.of(rawData).withCompressDecompressPair(new ZlibCompressor(CompressionLevel.BEST_COMPRESSION, CompressionStrategy.DEFAULT_STRATEGY, CompressionHeader.DEFAULT_HEADER, BYTE_SIZE), new ZlibDecompressor(DEFAULT_HEADER, BYTE_SIZE)).withTestCases(ImmutableSet.of(COMPRESS_DECOMPRESS_SINGLE_BLOCK, COMPRESS_DECOMPRESS_BLOCK, COMPRESS_DECOMPRESS_ERRORS, COMPRESS_DECOMPRESS_WITH_EMPTY_STREAM)).test();
        } catch (Exception ex) {
            Assert.fail(("testCompressorDecompressorWithExeedBufferLimit error !!!" + ex));
        }
    }

    @Test
    public void testZlibCompressorDecompressorWithConfiguration() {
        Configuration conf = new Configuration();
        if (ZlibFactory.isNativeZlibLoaded(conf)) {
            byte[] rawData;
            int tryNumber = 5;
            int BYTE_SIZE = 10 * 1024;
            Compressor zlibCompressor = ZlibFactory.getZlibCompressor(conf);
            Decompressor zlibDecompressor = ZlibFactory.getZlibDecompressor(conf);
            rawData = TestZlibCompressorDecompressor.generate(BYTE_SIZE);
            try {
                for (int i = 0; i < tryNumber; i++)
                    compressDecompressZlib(rawData, ((ZlibCompressor) (zlibCompressor)), ((ZlibDecompressor) (zlibDecompressor)));

                zlibCompressor.reinit(conf);
            } catch (Exception ex) {
                Assert.fail(("testZlibCompressorDecompressorWithConfiguration ex error " + ex));
            }
        } else {
            Assert.assertTrue("ZlibFactory is using native libs against request", ZlibFactory.isNativeZlibLoaded(conf));
        }
    }

    @Test
    public void testZlibCompressorDecompressorWithCompressionLevels() {
        Configuration conf = new Configuration();
        conf.set("zlib.compress.level", "FOUR");
        if (ZlibFactory.isNativeZlibLoaded(conf)) {
            byte[] rawData;
            int tryNumber = 5;
            int BYTE_SIZE = 10 * 1024;
            Compressor zlibCompressor = ZlibFactory.getZlibCompressor(conf);
            Decompressor zlibDecompressor = ZlibFactory.getZlibDecompressor(conf);
            rawData = TestZlibCompressorDecompressor.generate(BYTE_SIZE);
            try {
                for (int i = 0; i < tryNumber; i++)
                    compressDecompressZlib(rawData, ((ZlibCompressor) (zlibCompressor)), ((ZlibDecompressor) (zlibDecompressor)));

                zlibCompressor.reinit(conf);
            } catch (Exception ex) {
                Assert.fail(("testZlibCompressorDecompressorWithConfiguration ex error " + ex));
            }
        } else {
            Assert.assertTrue("ZlibFactory is using native libs against request", ZlibFactory.isNativeZlibLoaded(conf));
        }
    }

    @Test
    public void testZlibCompressDecompress() {
        byte[] rawData = null;
        int rawDataSize = 0;
        rawDataSize = 1024 * 64;
        rawData = TestZlibCompressorDecompressor.generate(rawDataSize);
        try {
            ZlibCompressor compressor = new ZlibCompressor();
            ZlibDecompressor decompressor = new ZlibDecompressor();
            Assert.assertFalse("testZlibCompressDecompress finished error", compressor.finished());
            compressor.setInput(rawData, 0, rawData.length);
            Assert.assertTrue("testZlibCompressDecompress getBytesRead before error", ((compressor.getBytesRead()) == 0));
            compressor.finish();
            byte[] compressedResult = new byte[rawDataSize];
            int cSize = compressor.compress(compressedResult, 0, rawDataSize);
            Assert.assertTrue("testZlibCompressDecompress getBytesRead ather error", ((compressor.getBytesRead()) == rawDataSize));
            Assert.assertTrue("testZlibCompressDecompress compressed size no less then original size", (cSize < rawDataSize));
            decompressor.setInput(compressedResult, 0, cSize);
            byte[] decompressedBytes = new byte[rawDataSize];
            decompressor.decompress(decompressedBytes, 0, decompressedBytes.length);
            Assert.assertArrayEquals("testZlibCompressDecompress arrays not equals ", rawData, decompressedBytes);
            compressor.reset();
            decompressor.reset();
        } catch (IOException ex) {
            Assert.fail(("testZlibCompressDecompress ex !!!" + ex));
        }
    }

    @Test
    public void testZlibDirectCompressDecompress() {
        int[] size = new int[]{ 1, 4, 16, 4 * 1024, 64 * 1024, 128 * 1024, 1024 * 1024 };
        Assume.assumeTrue(NativeCodeLoader.isNativeCodeLoaded());
        try {
            for (int i = 0; i < (size.length); i++) {
                compressDecompressLoop(size[i]);
            }
        } catch (IOException ex) {
            Assert.fail(("testZlibDirectCompressDecompress ex !!!" + ex));
        }
    }

    @Test
    public void testZlibCompressorDecompressorSetDictionary() {
        Configuration conf = new Configuration();
        if (ZlibFactory.isNativeZlibLoaded(conf)) {
            Compressor zlibCompressor = ZlibFactory.getZlibCompressor(conf);
            Decompressor zlibDecompressor = ZlibFactory.getZlibDecompressor(conf);
            checkSetDictionaryNullPointerException(zlibCompressor);
            checkSetDictionaryNullPointerException(zlibDecompressor);
            checkSetDictionaryArrayIndexOutOfBoundsException(zlibDecompressor);
            checkSetDictionaryArrayIndexOutOfBoundsException(zlibCompressor);
        } else {
            Assert.assertTrue("ZlibFactory is using native libs against request", ZlibFactory.isNativeZlibLoaded(conf));
        }
    }

    @Test
    public void testZlibFactory() {
        Configuration cfg = new Configuration();
        Assert.assertTrue("testZlibFactory compression level error !!!", ((CompressionLevel.DEFAULT_COMPRESSION) == (ZlibFactory.getCompressionLevel(cfg))));
        Assert.assertTrue("testZlibFactory compression strategy error !!!", ((CompressionStrategy.DEFAULT_STRATEGY) == (ZlibFactory.getCompressionStrategy(cfg))));
        ZlibFactory.setCompressionLevel(cfg, BEST_COMPRESSION);
        Assert.assertTrue("testZlibFactory compression strategy error !!!", ((CompressionLevel.BEST_COMPRESSION) == (ZlibFactory.getCompressionLevel(cfg))));
        ZlibFactory.setCompressionStrategy(cfg, FILTERED);
        Assert.assertTrue("testZlibFactory compression strategy error !!!", ((CompressionStrategy.FILTERED) == (ZlibFactory.getCompressionStrategy(cfg))));
    }

    @Test
    public void testBuiltInGzipDecompressorExceptions() {
        BuiltInGzipDecompressor decompresser = new BuiltInGzipDecompressor();
        try {
            decompresser.setInput(null, 0, 1);
        } catch (NullPointerException ex) {
            // expected
        } catch (Exception ex) {
            Assert.fail(("testBuiltInGzipDecompressorExceptions npe error " + ex));
        }
        try {
            decompresser.setInput(new byte[]{ 0 }, 0, (-1));
        } catch (ArrayIndexOutOfBoundsException ex) {
            // expected
        } catch (Exception ex) {
            Assert.fail(("testBuiltInGzipDecompressorExceptions aioob error" + ex));
        }
        Assert.assertTrue("decompresser.getBytesRead error", ((decompresser.getBytesRead()) == 0));
        Assert.assertTrue("decompresser.getRemaining error", ((decompresser.getRemaining()) == 0));
        decompresser.reset();
        decompresser.end();
        InputStream decompStream = null;
        try {
            // invalid 0 and 1 bytes , must be 31, -117
            int buffSize = 1 * 1024;
            byte[] buffer = new byte[buffSize];
            Decompressor decompressor = new BuiltInGzipDecompressor();
            DataInputBuffer gzbuf = new DataInputBuffer();
            decompStream = new org.apache.hadoop.io.compress.DecompressorStream(gzbuf, decompressor);
            gzbuf.reset(new byte[]{ 0, 0, 1, 1, 1, 1, 11, 1, 1, 1, 1 }, 11);
            decompStream.read(buffer);
        } catch (IOException ioex) {
            // expected
        } catch (Exception ex) {
            Assert.fail(("invalid 0 and 1 byte in gzip stream" + ex));
        }
        // invalid 2 byte, must be 8
        try {
            int buffSize = 1 * 1024;
            byte[] buffer = new byte[buffSize];
            Decompressor decompressor = new BuiltInGzipDecompressor();
            DataInputBuffer gzbuf = new DataInputBuffer();
            decompStream = new org.apache.hadoop.io.compress.DecompressorStream(gzbuf, decompressor);
            gzbuf.reset(new byte[]{ 31, -117, 7, 1, 1, 1, 1, 11, 1, 1, 1, 1 }, 11);
            decompStream.read(buffer);
        } catch (IOException ioex) {
            // expected
        } catch (Exception ex) {
            Assert.fail(("invalid 2 byte in gzip stream" + ex));
        }
        try {
            int buffSize = 1 * 1024;
            byte[] buffer = new byte[buffSize];
            Decompressor decompressor = new BuiltInGzipDecompressor();
            DataInputBuffer gzbuf = new DataInputBuffer();
            decompStream = new org.apache.hadoop.io.compress.DecompressorStream(gzbuf, decompressor);
            gzbuf.reset(new byte[]{ 31, -117, 8, -32, 1, 1, 1, 11, 1, 1, 1, 1 }, 11);
            decompStream.read(buffer);
        } catch (IOException ioex) {
            // expected
        } catch (Exception ex) {
            Assert.fail(("invalid 3 byte in gzip stream" + ex));
        }
        try {
            int buffSize = 1 * 1024;
            byte[] buffer = new byte[buffSize];
            Decompressor decompressor = new BuiltInGzipDecompressor();
            DataInputBuffer gzbuf = new DataInputBuffer();
            decompStream = new org.apache.hadoop.io.compress.DecompressorStream(gzbuf, decompressor);
            gzbuf.reset(new byte[]{ 31, -117, 8, 4, 1, 1, 1, 11, 1, 1, 1, 1 }, 11);
            decompStream.read(buffer);
        } catch (IOException ioex) {
            // expected
        } catch (Exception ex) {
            Assert.fail(("invalid 3 byte make hasExtraField" + ex));
        }
    }

    @Test
    public void testZlibCompressDecompressInMultiThreads() throws Exception {
        MultithreadedTestUtil.TestContext ctx = new MultithreadedTestUtil.TestContext();
        for (int i = 0; i < 10; i++) {
            ctx.addThread(new MultithreadedTestUtil.TestingThread(ctx) {
                @Override
                public void doWork() throws Exception {
                    testZlibCompressDecompress();
                }
            });
        }
        ctx.startThreads();
        ctx.waitFor(60000);
    }
}

