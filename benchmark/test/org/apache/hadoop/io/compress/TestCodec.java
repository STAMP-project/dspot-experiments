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


import CommonConfigurationKeys.IO_COMPRESSION_CODEC_LZ4_USELZ4HC_KEY;
import CompressionLevel.BEST_COMPRESSION;
import CompressionStrategy.HUFFMAN_ONLY;
import CompressionType.BLOCK;
import GzipCodec.GzipZlibDecompressor;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.GZIPOutputStream;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DataInputBuffer;
import org.apache.hadoop.io.DataOutputBuffer;
import org.apache.hadoop.io.IOUtils;
import org.apache.hadoop.io.compress.bzip2.Bzip2Factory;
import org.apache.hadoop.io.compress.zlib.BuiltInGzipDecompressor;
import org.apache.hadoop.io.compress.zlib.BuiltInZlibInflater;
import org.apache.hadoop.io.compress.zlib.ZlibFactory;
import org.apache.hadoop.test.GenericTestUtils;
import org.apache.hadoop.util.NativeCodeLoader;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCodec {
    private static final Logger LOG = LoggerFactory.getLogger(TestCodec.class);

    private Configuration conf = new Configuration();

    private int count = 10000;

    private int seed = new Random().nextInt();

    @Test
    public void testDefaultCodec() throws IOException {
        TestCodec.codecTest(conf, seed, 0, "org.apache.hadoop.io.compress.DefaultCodec");
        TestCodec.codecTest(conf, seed, count, "org.apache.hadoop.io.compress.DefaultCodec");
    }

    @Test
    public void testGzipCodec() throws IOException {
        TestCodec.codecTest(conf, seed, 0, "org.apache.hadoop.io.compress.GzipCodec");
        TestCodec.codecTest(conf, seed, count, "org.apache.hadoop.io.compress.GzipCodec");
    }

    @Test(timeout = 20000)
    public void testBZip2Codec() throws IOException {
        Configuration conf = new Configuration();
        conf.set("io.compression.codec.bzip2.library", "java-builtin");
        TestCodec.codecTest(conf, seed, 0, "org.apache.hadoop.io.compress.BZip2Codec");
        TestCodec.codecTest(conf, seed, count, "org.apache.hadoop.io.compress.BZip2Codec");
    }

    @Test(timeout = 20000)
    public void testBZip2NativeCodec() throws IOException {
        Configuration conf = new Configuration();
        conf.set("io.compression.codec.bzip2.library", "system-native");
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            if (Bzip2Factory.isNativeBzip2Loaded(conf)) {
                TestCodec.codecTest(conf, seed, 0, "org.apache.hadoop.io.compress.BZip2Codec");
                TestCodec.codecTest(conf, seed, count, "org.apache.hadoop.io.compress.BZip2Codec");
                conf.set("io.compression.codec.bzip2.library", "java-builtin");
                TestCodec.codecTest(conf, seed, 0, "org.apache.hadoop.io.compress.BZip2Codec");
                TestCodec.codecTest(conf, seed, count, "org.apache.hadoop.io.compress.BZip2Codec");
            } else {
                TestCodec.LOG.warn("Native hadoop library available but native bzip2 is not");
            }
        }
    }

    @Test
    public void testSnappyCodec() throws IOException {
        if (SnappyCodec.isNativeCodeLoaded()) {
            TestCodec.codecTest(conf, seed, 0, "org.apache.hadoop.io.compress.SnappyCodec");
            TestCodec.codecTest(conf, seed, count, "org.apache.hadoop.io.compress.SnappyCodec");
        }
    }

    @Test
    public void testLz4Codec() throws IOException {
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            if (Lz4Codec.isNativeCodeLoaded()) {
                conf.setBoolean(IO_COMPRESSION_CODEC_LZ4_USELZ4HC_KEY, false);
                TestCodec.codecTest(conf, seed, 0, "org.apache.hadoop.io.compress.Lz4Codec");
                TestCodec.codecTest(conf, seed, count, "org.apache.hadoop.io.compress.Lz4Codec");
                conf.setBoolean(IO_COMPRESSION_CODEC_LZ4_USELZ4HC_KEY, true);
                TestCodec.codecTest(conf, seed, 0, "org.apache.hadoop.io.compress.Lz4Codec");
                TestCodec.codecTest(conf, seed, count, "org.apache.hadoop.io.compress.Lz4Codec");
            } else {
                Assert.fail("Native hadoop library available but lz4 not");
            }
        }
    }

    @Test
    public void testDeflateCodec() throws IOException {
        TestCodec.codecTest(conf, seed, 0, "org.apache.hadoop.io.compress.DeflateCodec");
        TestCodec.codecTest(conf, seed, count, "org.apache.hadoop.io.compress.DeflateCodec");
    }

    @Test
    public void testGzipCodecWithParam() throws IOException {
        Configuration conf = new Configuration(this.conf);
        ZlibFactory.setCompressionLevel(conf, BEST_COMPRESSION);
        ZlibFactory.setCompressionStrategy(conf, HUFFMAN_ONLY);
        TestCodec.codecTest(conf, seed, 0, "org.apache.hadoop.io.compress.GzipCodec");
        TestCodec.codecTest(conf, seed, count, "org.apache.hadoop.io.compress.GzipCodec");
    }

    @Test
    public void testSplitableCodecs() throws Exception {
        testSplitableCodec(BZip2Codec.class);
    }

    @Test
    public void testCodecPoolGzipReuse() throws Exception {
        Configuration conf = new Configuration();
        Assume.assumeTrue(ZlibFactory.isNativeZlibLoaded(conf));
        GzipCodec gzc = ReflectionUtils.newInstance(GzipCodec.class, conf);
        DefaultCodec dfc = ReflectionUtils.newInstance(DefaultCodec.class, conf);
        Compressor c1 = CodecPool.getCompressor(gzc);
        Compressor c2 = CodecPool.getCompressor(dfc);
        CodecPool.returnCompressor(c1);
        CodecPool.returnCompressor(c2);
        Assert.assertTrue("Got mismatched ZlibCompressor", (c2 != (CodecPool.getCompressor(gzc))));
    }

    @Test
    public void testCodecInitWithCompressionLevel() throws Exception {
        Configuration conf = new Configuration();
        if (ZlibFactory.isNativeZlibLoaded(conf)) {
            TestCodec.LOG.info("testCodecInitWithCompressionLevel with native");
            TestCodec.codecTestWithNOCompression(conf, "org.apache.hadoop.io.compress.GzipCodec");
            TestCodec.codecTestWithNOCompression(conf, "org.apache.hadoop.io.compress.DefaultCodec");
        } else {
            TestCodec.LOG.warn(("testCodecInitWithCompressionLevel for native skipped" + ": native libs not loaded"));
        }
        conf = new Configuration();
        // don't use native libs
        ZlibFactory.setNativeZlibLoaded(false);
        TestCodec.codecTestWithNOCompression(conf, "org.apache.hadoop.io.compress.DefaultCodec");
    }

    @Test
    public void testCodecPoolCompressorReinit() throws Exception {
        Configuration conf = new Configuration();
        if (ZlibFactory.isNativeZlibLoaded(conf)) {
            GzipCodec gzc = ReflectionUtils.newInstance(GzipCodec.class, conf);
            TestCodec.gzipReinitTest(conf, gzc);
        } else {
            TestCodec.LOG.warn("testCodecPoolCompressorReinit skipped: native libs not loaded");
        }
        // don't use native libs
        ZlibFactory.setNativeZlibLoaded(false);
        DefaultCodec dfc = ReflectionUtils.newInstance(DefaultCodec.class, conf);
        TestCodec.gzipReinitTest(conf, dfc);
    }

    @Test
    public void testSequenceFileDefaultCodec() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        TestCodec.sequenceFileCodecTest(conf, 100, "org.apache.hadoop.io.compress.DefaultCodec", 100);
        TestCodec.sequenceFileCodecTest(conf, 200000, "org.apache.hadoop.io.compress.DefaultCodec", 1000000);
    }

    @Test(timeout = 20000)
    public void testSequenceFileBZip2Codec() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Configuration conf = new Configuration();
        conf.set("io.compression.codec.bzip2.library", "java-builtin");
        TestCodec.sequenceFileCodecTest(conf, 0, "org.apache.hadoop.io.compress.BZip2Codec", 100);
        TestCodec.sequenceFileCodecTest(conf, 100, "org.apache.hadoop.io.compress.BZip2Codec", 100);
        TestCodec.sequenceFileCodecTest(conf, 200000, "org.apache.hadoop.io.compress.BZip2Codec", 1000000);
    }

    @Test(timeout = 20000)
    public void testSequenceFileZStandardCodec() throws Exception {
        Assume.assumeTrue(ZStandardCodec.isNativeCodeLoaded());
        Configuration conf = new Configuration();
        TestCodec.sequenceFileCodecTest(conf, 0, "org.apache.hadoop.io.compress.ZStandardCodec", 100);
        TestCodec.sequenceFileCodecTest(conf, 100, "org.apache.hadoop.io.compress.ZStandardCodec", 100);
        TestCodec.sequenceFileCodecTest(conf, 200000, "org.apache.hadoop.io.compress.ZStandardCodec", 1000000);
    }

    @Test(timeout = 20000)
    public void testSequenceFileBZip2NativeCodec() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Configuration conf = new Configuration();
        conf.set("io.compression.codec.bzip2.library", "system-native");
        if (NativeCodeLoader.isNativeCodeLoaded()) {
            if (Bzip2Factory.isNativeBzip2Loaded(conf)) {
                TestCodec.sequenceFileCodecTest(conf, 0, "org.apache.hadoop.io.compress.BZip2Codec", 100);
                TestCodec.sequenceFileCodecTest(conf, 100, "org.apache.hadoop.io.compress.BZip2Codec", 100);
                TestCodec.sequenceFileCodecTest(conf, 200000, "org.apache.hadoop.io.compress.BZip2Codec", 1000000);
            } else {
                TestCodec.LOG.warn("Native hadoop library available but native bzip2 is not");
            }
        }
    }

    @Test
    public void testSequenceFileDeflateCodec() throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        TestCodec.sequenceFileCodecTest(conf, 100, "org.apache.hadoop.io.compress.DeflateCodec", 100);
        TestCodec.sequenceFileCodecTest(conf, 200000, "org.apache.hadoop.io.compress.DeflateCodec", 1000000);
    }

    /**
     * Regression test for HADOOP-8423: seeking in a block-compressed
     * stream would not properly reset the block decompressor state.
     */
    @Test
    public void testSnappyMapFile() throws Exception {
        Assume.assumeTrue(SnappyCodec.isNativeCodeLoaded());
        codecTestMapFile(SnappyCodec.class, BLOCK, 100);
    }

    @Test
    public void testGzipCompatibility() throws IOException {
        Random r = new Random();
        long seed = r.nextLong();
        r.setSeed(seed);
        TestCodec.LOG.info(("seed: " + seed));
        DataOutputBuffer dflbuf = new DataOutputBuffer();
        GZIPOutputStream gzout = new GZIPOutputStream(dflbuf);
        byte[] b = new byte[r.nextInt(((128 * 1024) + 1))];
        r.nextBytes(b);
        gzout.write(b);
        gzout.close();
        DataInputBuffer gzbuf = new DataInputBuffer();
        gzbuf.reset(dflbuf.getData(), dflbuf.getLength());
        Configuration conf = new Configuration();
        // don't use native libs
        ZlibFactory.setNativeZlibLoaded(false);
        CompressionCodec codec = ReflectionUtils.newInstance(GzipCodec.class, conf);
        Decompressor decom = codec.createDecompressor();
        Assert.assertNotNull(decom);
        Assert.assertEquals(BuiltInGzipDecompressor.class, decom.getClass());
        InputStream gzin = codec.createInputStream(gzbuf, decom);
        dflbuf.reset();
        IOUtils.copyBytes(gzin, dflbuf, 4096);
        final byte[] dflchk = Arrays.copyOf(dflbuf.getData(), dflbuf.getLength());
        Assert.assertArrayEquals(b, dflchk);
    }

    @Test
    public void testBuiltInGzipConcat() throws IOException {
        Configuration conf = new Configuration();
        // don't use native libs
        ZlibFactory.setNativeZlibLoaded(false);
        GzipConcatTest(conf, BuiltInGzipDecompressor.class);
    }

    @Test
    public void testNativeGzipConcat() throws IOException {
        Configuration conf = new Configuration();
        Assume.assumeTrue(ZlibFactory.isNativeZlibLoaded(conf));
        GzipConcatTest(conf, GzipZlibDecompressor.class);
    }

    @Test
    public void testGzipCodecRead() throws IOException {
        // Create a gzipped file and try to read it back, using a decompressor
        // from the CodecPool.
        // Don't use native libs for this test.
        Configuration conf = new Configuration();
        ZlibFactory.setNativeZlibLoaded(false);
        // Ensure that the CodecPool has a BuiltInZlibInflater in it.
        Decompressor zlibDecompressor = ZlibFactory.getZlibDecompressor(conf);
        Assert.assertNotNull("zlibDecompressor is null!", zlibDecompressor);
        Assert.assertTrue("ZlibFactory returned unexpected inflator", (zlibDecompressor instanceof BuiltInZlibInflater));
        CodecPool.returnDecompressor(zlibDecompressor);
        // Now create a GZip text file.
        Path f = new Path(GenericTestUtils.getTempPath("testGzipCodecRead.txt.gz"));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(f.toString()))));
        final String msg = "This is the message in the file!";
        bw.write(msg);
        bw.close();
        // Now read it back, using the CodecPool to establish the
        // decompressor to use.
        CompressionCodecFactory ccf = new CompressionCodecFactory(conf);
        CompressionCodec codec = ccf.getCodec(f);
        Decompressor decompressor = CodecPool.getDecompressor(codec);
        FileSystem fs = FileSystem.getLocal(conf);
        InputStream is = fs.open(f);
        is = codec.createInputStream(is, decompressor);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = br.readLine();
        Assert.assertEquals("Didn't get the same message back!", msg, line);
        br.close();
    }

    @Test
    public void testGzipLongOverflow() throws IOException {
        TestCodec.LOG.info("testGzipLongOverflow");
        // Don't use native libs for this test.
        Configuration conf = new Configuration();
        ZlibFactory.setNativeZlibLoaded(false);
        Assert.assertFalse("ZlibFactory is using native libs against request", ZlibFactory.isNativeZlibLoaded(conf));
        // Ensure that the CodecPool has a BuiltInZlibInflater in it.
        Decompressor zlibDecompressor = ZlibFactory.getZlibDecompressor(conf);
        Assert.assertNotNull("zlibDecompressor is null!", zlibDecompressor);
        Assert.assertTrue("ZlibFactory returned unexpected inflator", (zlibDecompressor instanceof BuiltInZlibInflater));
        CodecPool.returnDecompressor(zlibDecompressor);
        // Now create a GZip text file.
        Path f = new Path(GenericTestUtils.getTempPath("testGzipLongOverflow.bin.gz"));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(f.toString()))));
        final int NBUF = (1024 * 4) + 1;
        final char[] buf = new char[1024 * 1024];
        for (int i = 0; i < (buf.length); i++)
            buf[i] = '\u0000';

        for (int i = 0; i < NBUF; i++) {
            bw.write(buf);
        }
        bw.close();
        // Now read it back, using the CodecPool to establish the
        // decompressor to use.
        CompressionCodecFactory ccf = new CompressionCodecFactory(conf);
        CompressionCodec codec = ccf.getCodec(f);
        Decompressor decompressor = CodecPool.getDecompressor(codec);
        FileSystem fs = FileSystem.getLocal(conf);
        InputStream is = fs.open(f);
        is = codec.createInputStream(is, decompressor);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        for (int j = 0; j < NBUF; j++) {
            int n = br.read(buf);
            Assert.assertEquals("got wrong read length!", n, buf.length);
            for (int i = 0; i < (buf.length); i++)
                Assert.assertEquals("got wrong byte!", buf[i], '\u0000');

        }
        br.close();
    }

    @Test
    public void testGzipCodecWriteJava() throws IOException {
        // don't use native libs
        ZlibFactory.setNativeZlibLoaded(false);
        testGzipCodecWrite(false);
    }

    @Test
    public void testGzipNativeCodecWrite() throws IOException {
        testGzipCodecWrite(true);
    }

    @Test
    public void testCodecPoolAndGzipDecompressor() {
        // BuiltInZlibInflater should not be used as the GzipCodec decompressor.
        // Assert that this is the case.
        // Don't use native libs for this test.
        Configuration conf = new Configuration();
        ZlibFactory.setNativeZlibLoaded(false);
        Assert.assertFalse("ZlibFactory is using native libs against request", ZlibFactory.isNativeZlibLoaded(conf));
        // This should give us a BuiltInZlibInflater.
        Decompressor zlibDecompressor = ZlibFactory.getZlibDecompressor(conf);
        Assert.assertNotNull("zlibDecompressor is null!", zlibDecompressor);
        Assert.assertTrue("ZlibFactory returned unexpected inflator", (zlibDecompressor instanceof BuiltInZlibInflater));
        // its createOutputStream() just wraps the existing stream in a
        // java.util.zip.GZIPOutputStream.
        CompressionCodecFactory ccf = new CompressionCodecFactory(conf);
        CompressionCodec codec = ccf.getCodec(new Path("foo.gz"));
        Assert.assertTrue("Codec for .gz file is not GzipCodec", (codec instanceof GzipCodec));
        // make sure we don't get a null decompressor
        Decompressor codecDecompressor = codec.createDecompressor();
        if (null == codecDecompressor) {
            Assert.fail("Got null codecDecompressor");
        }
        // Asking the CodecPool for a decompressor for GzipCodec
        // should not return null
        Decompressor poolDecompressor = CodecPool.getDecompressor(codec);
        if (null == poolDecompressor) {
            Assert.fail("Got null poolDecompressor");
        }
        // return a couple decompressors
        CodecPool.returnDecompressor(zlibDecompressor);
        CodecPool.returnDecompressor(poolDecompressor);
        Decompressor poolDecompressor2 = CodecPool.getDecompressor(codec);
        if ((poolDecompressor.getClass()) == (BuiltInGzipDecompressor.class)) {
            if (poolDecompressor == poolDecompressor2) {
                Assert.fail("Reused java gzip decompressor in pool");
            }
        } else {
            if (poolDecompressor != poolDecompressor2) {
                Assert.fail("Did not reuse native gzip decompressor in pool");
            }
        }
    }
}

