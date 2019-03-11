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
package org.apache.hadoop.mapred;


import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.BZip2Codec;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionInputStream;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.io.compress.zlib.BuiltInGzipDecompressor;
import org.apache.hadoop.io.compress.zlib.ZlibFactory;
import org.apache.hadoop.util.LineReader;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Reporter.NULL;


/**
 * Test class for concatenated {@link CompressionInputStream}.
 */
public class TestConcatenatedCompressedInput {
    private static final Logger LOG = LoggerFactory.getLogger(TestConcatenatedCompressedInput.class);

    private static JobConf defaultConf = new JobConf();

    private static FileSystem localFs = null;

    // from ~roelofs/ss30b-colors.hh
    static final String COLOR_RED = "[0;31m";// background doesn't matter...  "[0m"


    static final String COLOR_GREEN = "[0;32m";// background doesn't matter...  "[0m"


    static final String COLOR_YELLOW = "[0;33;40m";// DO force black background     "[0m"


    static final String COLOR_BLUE = "[0;34m";// do NOT force black background "[0m"


    static final String COLOR_MAGENTA = "[0;35m";// background doesn't matter...  "[0m"


    static final String COLOR_CYAN = "[0;36m";// background doesn't matter...  "[0m"


    static final String COLOR_WHITE = "[0;37;40m";// DO force black background     "[0m"


    static final String COLOR_BR_RED = "[1;31m";// background doesn't matter...  "[0m"


    static final String COLOR_BR_GREEN = "[1;32m";// background doesn't matter...  "[0m"


    static final String COLOR_BR_YELLOW = "[1;33;40m";// DO force black background     "[0m"


    static final String COLOR_BR_BLUE = "[1;34m";// do NOT force black background "[0m"


    static final String COLOR_BR_MAGENTA = "[1;35m";// background doesn't matter...  "[0m"


    static final String COLOR_BR_CYAN = "[1;36m";// background doesn't matter...  "[0m"


    static final String COLOR_BR_WHITE = "[1;37;40m";// DO force black background     "[0m"


    static final String COLOR_NORMAL = "[0m";

    static {
        try {
            TestConcatenatedCompressedInput.defaultConf.set("fs.defaultFS", "file:///");
            TestConcatenatedCompressedInput.localFs = FileSystem.getLocal(TestConcatenatedCompressedInput.defaultConf);
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    private static final String DEFAULT_WORK_DIR = "target/test-classes/testdata";

    private static Path workDir = TestConcatenatedCompressedInput.localFs.makeQualified(new Path(System.getProperty("test.build.data", TestConcatenatedCompressedInput.DEFAULT_WORK_DIR), "TestConcatenatedCompressedInput"));

    private static final Reporter voidReporter = NULL;

    /**
     * Test using Hadoop's original, native-zlib gzip codec for reading.
     */
    @Test
    public void testGzip() throws IOException {
        JobConf jobConf = new JobConf(TestConcatenatedCompressedInput.defaultConf);
        CompressionCodec gzip = new GzipCodec();
        ReflectionUtils.setConf(gzip, jobConf);
        TestConcatenatedCompressedInput.localFs.delete(TestConcatenatedCompressedInput.workDir, true);
        // preferred, but not compatible with Apache/trunk instance of Hudson:
        /* assertFalse("[native (C/C++) codec]",
        (org.apache.hadoop.io.compress.zlib.BuiltInGzipDecompressor.class ==
        gzip.getDecompressorType()) );
        System.out.println(COLOR_BR_RED +
        "testGzip() using native-zlib Decompressor (" +
        gzip.getDecompressorType() + ")" + COLOR_NORMAL);
         */
        // alternative:
        if ((BuiltInGzipDecompressor.class) == (gzip.getDecompressorType())) {
            System.out.println((((((TestConcatenatedCompressedInput.COLOR_BR_RED) + "testGzip() using native-zlib Decompressor (") + (gzip.getDecompressorType())) + ")") + (TestConcatenatedCompressedInput.COLOR_NORMAL)));
        } else {
            TestConcatenatedCompressedInput.LOG.warn("testGzip() skipped:  native (C/C++) libs not loaded");
            return;
        }
        /* // THIS IS BUGGY: omits 2nd/3rd gzip headers; screws up 2nd/3rd CRCs--
             //                see https://issues.apache.org/jira/browse/HADOOP-6799
         Path fnHDFS = new Path(workDir, "concat" + gzip.getDefaultExtension());
         //OutputStream out = localFs.create(fnHDFS);
         //GzipCodec.GzipOutputStream gzOStm = new GzipCodec.GzipOutputStream(out);
             // can just combine those two lines, probably
         //GzipCodec.GzipOutputStream gzOStm =
         //  new GzipCodec.GzipOutputStream(localFs.create(fnHDFS));
             // oops, no:  this is a protected helper class; need to access
             //   it via createOutputStream() instead:
         OutputStream out = localFs.create(fnHDFS);
         Compressor gzCmp = gzip.createCompressor();
         CompressionOutputStream gzOStm = gzip.createOutputStream(out, gzCmp);
             // this SHOULD be going to HDFS:  got out from localFs == HDFS
             //   ...yup, works
         gzOStm.write("first gzip concat\n member\nwith three lines\n".getBytes());
         gzOStm.finish();
         gzOStm.resetState();
         gzOStm.write("2nd gzip concat member\n".getBytes());
         gzOStm.finish();
         gzOStm.resetState();
         gzOStm.write("gzip concat\nmember #3\n".getBytes());
         gzOStm.close();
             //
         String fn = "hdfs-to-local-concat" + gzip.getDefaultExtension();
         Path fnLocal = new Path(System.getProperty("test.concat.data","/tmp"), fn);
         localFs.copyToLocalFile(fnHDFS, fnLocal);
         */
        // copy prebuilt (correct!) version of concat.gz to HDFS
        final String fn = "concat" + (gzip.getDefaultExtension());
        Path fnLocal = new Path(System.getProperty("test.concat.data", TestConcatenatedCompressedInput.DEFAULT_WORK_DIR), fn);
        Path fnHDFS = new Path(TestConcatenatedCompressedInput.workDir, fn);
        TestConcatenatedCompressedInput.localFs.copyFromLocalFile(fnLocal, fnHDFS);
        TestConcatenatedCompressedInput.writeFile(TestConcatenatedCompressedInput.localFs, new Path(TestConcatenatedCompressedInput.workDir, "part2.txt.gz"), gzip, "this is a test\nof gzip\n");
        FileInputFormat.setInputPaths(jobConf, TestConcatenatedCompressedInput.workDir);
        TextInputFormat format = new TextInputFormat();
        format.configure(jobConf);
        InputSplit[] splits = format.getSplits(jobConf, 100);
        Assert.assertEquals("compressed splits == 2", 2, splits.length);
        FileSplit tmp = ((FileSplit) (splits[0]));
        if (tmp.getPath().getName().equals("part2.txt.gz")) {
            splits[0] = splits[1];
            splits[1] = tmp;
        }
        List<Text> results = TestConcatenatedCompressedInput.readSplit(format, splits[0], jobConf);
        Assert.assertEquals("splits[0] num lines", 6, results.size());
        Assert.assertEquals("splits[0][5]", "member #3", results.get(5).toString());
        results = TestConcatenatedCompressedInput.readSplit(format, splits[1], jobConf);
        Assert.assertEquals("splits[1] num lines", 2, results.size());
        Assert.assertEquals("splits[1][0]", "this is a test", results.get(0).toString());
        Assert.assertEquals("splits[1][1]", "of gzip", results.get(1).toString());
    }

    /**
     * Test using the raw Inflater codec for reading gzip files.
     */
    @Test
    public void testPrototypeInflaterGzip() throws IOException {
        CompressionCodec gzip = new GzipCodec();// used only for file extension

        TestConcatenatedCompressedInput.localFs.delete(TestConcatenatedCompressedInput.workDir, true);// localFs = FileSystem instance

        System.out.println(((((TestConcatenatedCompressedInput.COLOR_BR_BLUE) + "testPrototypeInflaterGzip() using ") + "non-native/Java Inflater and manual gzip header/trailer parsing") + (TestConcatenatedCompressedInput.COLOR_NORMAL)));
        // copy prebuilt (correct!) version of concat.gz to HDFS
        final String fn = "concat" + (gzip.getDefaultExtension());
        Path fnLocal = new Path(System.getProperty("test.concat.data", TestConcatenatedCompressedInput.DEFAULT_WORK_DIR), fn);
        Path fnHDFS = new Path(TestConcatenatedCompressedInput.workDir, fn);
        TestConcatenatedCompressedInput.localFs.copyFromLocalFile(fnLocal, fnHDFS);
        final FileInputStream in = new FileInputStream(fnLocal.toString());
        Assert.assertEquals("concat bytes available", 148, in.available());
        // should wrap all of this header-reading stuff in a running-CRC wrapper
        // (did so in BuiltInGzipDecompressor; see below)
        byte[] compressedBuf = new byte[256];
        int numBytesRead = in.read(compressedBuf, 0, 10);
        Assert.assertEquals("header bytes read", 10, numBytesRead);
        Assert.assertEquals("1st byte", 31, ((compressedBuf[0]) & 255));
        Assert.assertEquals("2nd byte", 139, ((compressedBuf[1]) & 255));
        Assert.assertEquals("3rd byte (compression method)", 8, ((compressedBuf[2]) & 255));
        byte flags = ((byte) ((compressedBuf[3]) & 255));
        if ((flags & 4) != 0) {
            // FEXTRA
            numBytesRead = in.read(compressedBuf, 0, 2);
            Assert.assertEquals("XLEN bytes read", 2, numBytesRead);
            int xlen = (((compressedBuf[1]) << 8) | (compressedBuf[0])) & 65535;
            in.skip(xlen);
        }
        if ((flags & 8) != 0) {
            // FNAME
            while ((numBytesRead = in.read()) != 0) {
                Assert.assertFalse("unexpected end-of-file while reading filename", (numBytesRead == (-1)));
            } 
        }
        if ((flags & 16) != 0) {
            // FCOMMENT
            while ((numBytesRead = in.read()) != 0) {
                Assert.assertFalse("unexpected end-of-file while reading comment", (numBytesRead == (-1)));
            } 
        }
        if ((flags & 224) != 0) {
            // reserved
            Assert.assertTrue("reserved bits are set??", ((flags & 224) == 0));
        }
        if ((flags & 2) != 0) {
            // FHCRC
            numBytesRead = in.read(compressedBuf, 0, 2);
            Assert.assertEquals("CRC16 bytes read", 2, numBytesRead);
            int crc16 = (((compressedBuf[1]) << 8) | (compressedBuf[0])) & 65535;
        }
        // ready to go!  next bytes should be start of deflated stream, suitable
        // for Inflater
        numBytesRead = in.read(compressedBuf);
        // Inflater docs refer to a "dummy byte":  no clue what that's about;
        // appears to work fine without one
        byte[] uncompressedBuf = new byte[256];
        Inflater inflater = new Inflater(true);
        inflater.setInput(compressedBuf, 0, numBytesRead);
        try {
            int numBytesUncompressed = inflater.inflate(uncompressedBuf);
            String outString = new String(uncompressedBuf, 0, numBytesUncompressed, "UTF-8");
            System.out.println((("uncompressed data of first gzip member = [" + outString) + "]"));
        } catch (DataFormatException ex) {
            throw new IOException(ex.getMessage());
        }
        in.close();
    }

    /**
     * Test using the new BuiltInGzipDecompressor codec for reading gzip files.
     */
    // NOTE:  This fails on RHEL4 with "java.io.IOException: header crc mismatch"
    // due to buggy version of zlib (1.2.1.2) included.
    @Test
    public void testBuiltInGzipDecompressor() throws IOException {
        JobConf jobConf = new JobConf(TestConcatenatedCompressedInput.defaultConf);
        CompressionCodec gzip = new GzipCodec();
        ReflectionUtils.setConf(gzip, jobConf);
        TestConcatenatedCompressedInput.localFs.delete(TestConcatenatedCompressedInput.workDir, true);
        // Don't use native libs for this test
        ZlibFactory.setNativeZlibLoaded(false);
        Assert.assertEquals("[non-native (Java) codec]", BuiltInGzipDecompressor.class, gzip.getDecompressorType());
        System.out.println(((((((TestConcatenatedCompressedInput.COLOR_BR_YELLOW) + "testBuiltInGzipDecompressor() using") + " non-native (Java Inflater) Decompressor (") + (gzip.getDecompressorType())) + ")") + (TestConcatenatedCompressedInput.COLOR_NORMAL)));
        // copy single-member test file to HDFS
        String fn1 = "testConcatThenCompress.txt" + (gzip.getDefaultExtension());
        Path fnLocal1 = new Path(System.getProperty("test.concat.data", TestConcatenatedCompressedInput.DEFAULT_WORK_DIR), fn1);
        Path fnHDFS1 = new Path(TestConcatenatedCompressedInput.workDir, fn1);
        TestConcatenatedCompressedInput.localFs.copyFromLocalFile(fnLocal1, fnHDFS1);
        // copy multiple-member test file to HDFS
        // (actually in "seekable gzip" format, a la JIRA PIG-42)
        String fn2 = "testCompressThenConcat.txt" + (gzip.getDefaultExtension());
        Path fnLocal2 = new Path(System.getProperty("test.concat.data", TestConcatenatedCompressedInput.DEFAULT_WORK_DIR), fn2);
        Path fnHDFS2 = new Path(TestConcatenatedCompressedInput.workDir, fn2);
        TestConcatenatedCompressedInput.localFs.copyFromLocalFile(fnLocal2, fnHDFS2);
        FileInputFormat.setInputPaths(jobConf, TestConcatenatedCompressedInput.workDir);
        // here's first pair of DecompressorStreams:
        final FileInputStream in1 = new FileInputStream(fnLocal1.toString());
        final FileInputStream in2 = new FileInputStream(fnLocal2.toString());
        Assert.assertEquals("concat bytes available", 2734, in1.available());
        Assert.assertEquals("concat bytes available", 3413, in2.available());// w/hdr CRC

        CompressionInputStream cin2 = gzip.createInputStream(in2);
        LineReader in = new LineReader(cin2);
        Text out = new Text();
        int numBytes;
        int totalBytes = 0;
        int lineNum = 0;
        while ((numBytes = in.readLine(out)) > 0) {
            ++lineNum;
            totalBytes += numBytes;
        } 
        in.close();
        Assert.assertEquals("total uncompressed bytes in concatenated test file", 5346, totalBytes);
        Assert.assertEquals("total uncompressed lines in concatenated test file", 84, lineNum);
        ZlibFactory.loadNativeZLib();
        // test GzipZlibDecompressor (native), just to be sure
        // (FIXME?  could move this call to testGzip(), but would need filename
        // setup above) (alternatively, maybe just nuke testGzip() and extend this?)
        TestConcatenatedCompressedInput.doMultipleGzipBufferSizes(jobConf, true);
    }

    /**
     * Test using the bzip2 codec for reading
     */
    @Test
    public void testBzip2() throws IOException {
        JobConf jobConf = new JobConf(TestConcatenatedCompressedInput.defaultConf);
        CompressionCodec bzip2 = new BZip2Codec();
        ReflectionUtils.setConf(bzip2, jobConf);
        TestConcatenatedCompressedInput.localFs.delete(TestConcatenatedCompressedInput.workDir, true);
        System.out.println((((TestConcatenatedCompressedInput.COLOR_BR_CYAN) + "testBzip2() using non-native CBZip2InputStream (presumably)") + (TestConcatenatedCompressedInput.COLOR_NORMAL)));
        // copy prebuilt (correct!) version of concat.bz2 to HDFS
        final String fn = "concat" + (bzip2.getDefaultExtension());
        Path fnLocal = new Path(System.getProperty("test.concat.data", TestConcatenatedCompressedInput.DEFAULT_WORK_DIR), fn);
        Path fnHDFS = new Path(TestConcatenatedCompressedInput.workDir, fn);
        TestConcatenatedCompressedInput.localFs.copyFromLocalFile(fnLocal, fnHDFS);
        TestConcatenatedCompressedInput.writeFile(TestConcatenatedCompressedInput.localFs, new Path(TestConcatenatedCompressedInput.workDir, "part2.txt.bz2"), bzip2, "this is a test\nof bzip2\n");
        FileInputFormat.setInputPaths(jobConf, TestConcatenatedCompressedInput.workDir);
        TextInputFormat format = new TextInputFormat();// extends FileInputFormat

        format.configure(jobConf);
        format.setMinSplitSize(256);// work around 2-byte splits issue

        // [135 splits for a 208-byte file and a 62-byte file(!)]
        InputSplit[] splits = format.getSplits(jobConf, 100);
        Assert.assertEquals("compressed splits == 2", 2, splits.length);
        FileSplit tmp = ((FileSplit) (splits[0]));
        if (tmp.getPath().getName().equals("part2.txt.bz2")) {
            splits[0] = splits[1];
            splits[1] = tmp;
        }
        List<Text> results = TestConcatenatedCompressedInput.readSplit(format, splits[0], jobConf);
        Assert.assertEquals("splits[0] num lines", 6, results.size());
        Assert.assertEquals("splits[0][5]", "member #3", results.get(5).toString());
        results = TestConcatenatedCompressedInput.readSplit(format, splits[1], jobConf);
        Assert.assertEquals("splits[1] num lines", 2, results.size());
        Assert.assertEquals("splits[1][0]", "this is a test", results.get(0).toString());
        Assert.assertEquals("splits[1][1]", "of bzip2", results.get(1).toString());
    }

    /**
     * Extended bzip2 test, similar to BuiltInGzipDecompressor test above.
     */
    @Test
    public void testMoreBzip2() throws IOException {
        JobConf jobConf = new JobConf(TestConcatenatedCompressedInput.defaultConf);
        CompressionCodec bzip2 = new BZip2Codec();
        ReflectionUtils.setConf(bzip2, jobConf);
        TestConcatenatedCompressedInput.localFs.delete(TestConcatenatedCompressedInput.workDir, true);
        System.out.println((((TestConcatenatedCompressedInput.COLOR_BR_MAGENTA) + "testMoreBzip2() using non-native CBZip2InputStream (presumably)") + (TestConcatenatedCompressedInput.COLOR_NORMAL)));
        // copy single-member test file to HDFS
        String fn1 = "testConcatThenCompress.txt" + (bzip2.getDefaultExtension());
        Path fnLocal1 = new Path(System.getProperty("test.concat.data", TestConcatenatedCompressedInput.DEFAULT_WORK_DIR), fn1);
        Path fnHDFS1 = new Path(TestConcatenatedCompressedInput.workDir, fn1);
        TestConcatenatedCompressedInput.localFs.copyFromLocalFile(fnLocal1, fnHDFS1);
        // copy multiple-member test file to HDFS
        String fn2 = "testCompressThenConcat.txt" + (bzip2.getDefaultExtension());
        Path fnLocal2 = new Path(System.getProperty("test.concat.data", TestConcatenatedCompressedInput.DEFAULT_WORK_DIR), fn2);
        Path fnHDFS2 = new Path(TestConcatenatedCompressedInput.workDir, fn2);
        TestConcatenatedCompressedInput.localFs.copyFromLocalFile(fnLocal2, fnHDFS2);
        FileInputFormat.setInputPaths(jobConf, TestConcatenatedCompressedInput.workDir);
        // here's first pair of BlockDecompressorStreams:
        final FileInputStream in1 = new FileInputStream(fnLocal1.toString());
        final FileInputStream in2 = new FileInputStream(fnLocal2.toString());
        Assert.assertEquals("concat bytes available", 2567, in1.available());
        Assert.assertEquals("concat bytes available", 3056, in2.available());
        CompressionInputStream cin2 = bzip2.createInputStream(in2);
        LineReader in = new LineReader(cin2);
        Text out = new Text();
        int numBytes;
        int totalBytes = 0;
        int lineNum = 0;
        while ((numBytes = in.readLine(out)) > 0) {
            ++lineNum;
            totalBytes += numBytes;
        } 
        in.close();
        Assert.assertEquals("total uncompressed bytes in concatenated test file", 5346, totalBytes);
        Assert.assertEquals("total uncompressed lines in concatenated test file", 84, lineNum);
        // test CBZip2InputStream with lots of different input-buffer sizes
        TestConcatenatedCompressedInput.doMultipleBzip2BufferSizes(jobConf);
    }
}

