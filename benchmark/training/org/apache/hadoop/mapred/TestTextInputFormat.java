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


import Reporter.NULL;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader.MAX_LINE_LENGTH;
import org.apache.hadoop.util.LineReader;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Reporter.NULL;


public class TestTextInputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestTextInputFormat.class);

    private static int MAX_LENGTH = 10000;

    private static JobConf defaultConf = new JobConf();

    private static FileSystem localFs = null;

    static {
        try {
            TestTextInputFormat.defaultConf.set("fs.defaultFS", "file:///");
            TestTextInputFormat.localFs = FileSystem.getLocal(TestTextInputFormat.defaultConf);
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    private static Path workDir = TestTextInputFormat.localFs.makeQualified(new Path(System.getProperty("test.build.data", "/tmp"), "TestTextInputFormat"));

    @Test(timeout = 500000)
    public void testFormat() throws Exception {
        JobConf job = new JobConf(TestTextInputFormat.defaultConf);
        Path file = new Path(TestTextInputFormat.workDir, "test.txt");
        // A reporter that does nothing
        Reporter reporter = NULL;
        int seed = new Random().nextInt();
        TestTextInputFormat.LOG.info(("seed = " + seed));
        Random random = new Random(seed);
        TestTextInputFormat.localFs.delete(TestTextInputFormat.workDir, true);
        FileInputFormat.setInputPaths(job, TestTextInputFormat.workDir);
        // for a variety of lengths
        for (int length = 0; length < (TestTextInputFormat.MAX_LENGTH); length += (random.nextInt(((TestTextInputFormat.MAX_LENGTH) / 10))) + 1) {
            TestTextInputFormat.LOG.debug(("creating; entries = " + length));
            // create a file with length entries
            Writer writer = new OutputStreamWriter(TestTextInputFormat.localFs.create(file));
            try {
                for (int i = 0; i < length; i++) {
                    writer.write(Integer.toString(i));
                    writer.write("\n");
                }
            } finally {
                writer.close();
            }
            // try splitting the file in a variety of sizes
            TextInputFormat format = new TextInputFormat();
            format.configure(job);
            LongWritable key = new LongWritable();
            Text value = new Text();
            for (int i = 0; i < 3; i++) {
                int numSplits = (random.nextInt(((TestTextInputFormat.MAX_LENGTH) / 20))) + 1;
                TestTextInputFormat.LOG.debug(("splitting: requesting = " + numSplits));
                InputSplit[] splits = format.getSplits(job, numSplits);
                TestTextInputFormat.LOG.debug(("splitting: got =        " + (splits.length)));
                if (length == 0) {
                    Assert.assertEquals("Files of length 0 are not returned from FileInputFormat.getSplits().", 1, splits.length);
                    Assert.assertEquals("Empty file length == 0", 0, splits[0].getLength());
                }
                // check each split
                BitSet bits = new BitSet(length);
                for (int j = 0; j < (splits.length); j++) {
                    TestTextInputFormat.LOG.debug(((("split[" + j) + "]= ") + (splits[j])));
                    RecordReader<LongWritable, Text> reader = format.getRecordReader(splits[j], job, reporter);
                    try {
                        int count = 0;
                        while (reader.next(key, value)) {
                            int v = Integer.parseInt(value.toString());
                            TestTextInputFormat.LOG.debug(("read " + v));
                            if (bits.get(v)) {
                                TestTextInputFormat.LOG.warn(((((("conflict with " + v) + " in split ") + j) + " at position ") + (reader.getPos())));
                            }
                            Assert.assertFalse("Key in multiple partitions.", bits.get(v));
                            bits.set(v);
                            count++;
                        } 
                        TestTextInputFormat.LOG.debug(((((("splits[" + j) + "]=") + (splits[j])) + " count=") + count));
                    } finally {
                        reader.close();
                    }
                }
                Assert.assertEquals("Some keys in no partition.", length, bits.cardinality());
            }
        }
    }

    @Test(timeout = 900000)
    public void testSplitableCodecs() throws IOException {
        JobConf conf = new JobConf(TestTextInputFormat.defaultConf);
        int seed = new Random().nextInt();
        // Create the codec
        CompressionCodec codec = null;
        try {
            codec = ((CompressionCodec) (ReflectionUtils.newInstance(conf.getClassByName("org.apache.hadoop.io.compress.BZip2Codec"), conf)));
        } catch (ClassNotFoundException cnfe) {
            throw new IOException("Illegal codec!");
        }
        Path file = new Path(TestTextInputFormat.workDir, ("test" + (codec.getDefaultExtension())));
        // A reporter that does nothing
        Reporter reporter = NULL;
        TestTextInputFormat.LOG.info(("seed = " + seed));
        Random random = new Random(seed);
        FileSystem localFs = FileSystem.getLocal(conf);
        localFs.delete(TestTextInputFormat.workDir, true);
        FileInputFormat.setInputPaths(conf, TestTextInputFormat.workDir);
        final int MAX_LENGTH = 500000;
        // for a variety of lengths
        for (int length = MAX_LENGTH / 2; length < MAX_LENGTH; length += (random.nextInt((MAX_LENGTH / 4))) + 1) {
            for (int i = 0; i < 3; i++) {
                int numSplits = (random.nextInt((MAX_LENGTH / 2000))) + 1;
                verifyPartitions(length, numSplits, file, codec, conf);
            }
        }
        // corner case when we have byte alignment and position of stream are same
        verifyPartitions(471507, 218, file, codec, conf);
        verifyPartitions(473608, 110, file, codec, conf);
        // corner case when split size is small and position of stream is before
        // the first BZip2 block
        verifyPartitions(100, 20, file, codec, conf);
        verifyPartitions(100, 25, file, codec, conf);
        verifyPartitions(100, 30, file, codec, conf);
        verifyPartitions(100, 50, file, codec, conf);
        verifyPartitions(100, 100, file, codec, conf);
    }

    // Test a corner case when position of stream is right after BZip2 marker
    @Test(timeout = 900000)
    public void testSplitableCodecs2() throws IOException {
        JobConf conf = new JobConf(TestTextInputFormat.defaultConf);
        // Create the codec
        CompressionCodec codec = null;
        try {
            codec = ((CompressionCodec) (ReflectionUtils.newInstance(conf.getClassByName("org.apache.hadoop.io.compress.BZip2Codec"), conf)));
        } catch (ClassNotFoundException cnfe) {
            throw new IOException("Illegal codec!");
        }
        Path file = new Path(TestTextInputFormat.workDir, ("test" + (codec.getDefaultExtension())));
        FileSystem localFs = FileSystem.getLocal(conf);
        localFs.delete(TestTextInputFormat.workDir, true);
        FileInputFormat.setInputPaths(conf, TestTextInputFormat.workDir);
        int length = 250000;
        TestTextInputFormat.LOG.info(("creating; entries = " + length));
        // create a file with length entries
        Writer writer = new OutputStreamWriter(codec.createOutputStream(localFs.create(file)));
        try {
            for (int i = 0; i < length; i++) {
                writer.write(Integer.toString(i));
                writer.write("\n");
            }
        } finally {
            writer.close();
        }
        // Test split positions around a block boundary where the block does
        // not start on a byte boundary.
        for (long splitpos = 203418; splitpos < 203430; ++splitpos) {
            TextInputFormat format = new TextInputFormat();
            format.configure(conf);
            TestTextInputFormat.LOG.info(("setting block size of the input file to " + splitpos));
            conf.setLong("mapreduce.input.fileinputformat.split.minsize", splitpos);
            LongWritable key = new LongWritable();
            Text value = new Text();
            InputSplit[] splits = format.getSplits(conf, 2);
            TestTextInputFormat.LOG.info(("splitting: got =        " + (splits.length)));
            // check each split
            BitSet bits = new BitSet(length);
            for (int j = 0; j < (splits.length); j++) {
                TestTextInputFormat.LOG.debug(((("split[" + j) + "]= ") + (splits[j])));
                RecordReader<LongWritable, Text> reader = format.getRecordReader(splits[j], conf, NULL);
                try {
                    int counter = 0;
                    while (reader.next(key, value)) {
                        int v = Integer.parseInt(value.toString());
                        TestTextInputFormat.LOG.debug(("read " + v));
                        if (bits.get(v)) {
                            TestTextInputFormat.LOG.warn(((((("conflict with " + v) + " in split ") + j) + " at position ") + (reader.getPos())));
                        }
                        Assert.assertFalse("Key in multiple partitions.", bits.get(v));
                        bits.set(v);
                        counter++;
                    } 
                    if (counter > 0) {
                        TestTextInputFormat.LOG.info(((((("splits[" + j) + "]=") + (splits[j])) + " count=") + counter));
                    } else {
                        TestTextInputFormat.LOG.debug(((((("splits[" + j) + "]=") + (splits[j])) + " count=") + counter));
                    }
                } finally {
                    reader.close();
                }
            }
            Assert.assertEquals("Some keys in no partition.", length, bits.cardinality());
        }
    }

    @Test(timeout = 5000)
    public void testUTF8() throws Exception {
        LineReader in = TestTextInputFormat.makeStream("abcd\u20acbdcd\u20ac");
        Text line = new Text();
        in.readLine(line);
        Assert.assertEquals("readLine changed utf8 characters", "abcd\u20acbdcd\u20ac", line.toString());
        in = TestTextInputFormat.makeStream("abc\u200axyz");
        in.readLine(line);
        Assert.assertEquals("split on fake newline", "abc\u200axyz", line.toString());
    }

    /**
     * Test readLine for various kinds of line termination sequneces.
     * Varies buffer size to stress test.  Also check that returned
     * value matches the string length.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 5000)
    public void testNewLines() throws Exception {
        final String STR = "a\nbb\n\nccc\rdddd\r\r\r\n\r\neeeee";
        final int STRLENBYTES = STR.getBytes().length;
        Text out = new Text();
        for (int bufsz = 1; bufsz < (STRLENBYTES + 1); ++bufsz) {
            LineReader in = TestTextInputFormat.makeStream(STR, bufsz);
            int c = 0;
            c += in.readLine(out);// "a"\n

            Assert.assertEquals(("line1 length, bufsz:" + bufsz), 1, out.getLength());
            c += in.readLine(out);// "bb"\n

            Assert.assertEquals(("line2 length, bufsz:" + bufsz), 2, out.getLength());
            c += in.readLine(out);// ""\n

            Assert.assertEquals(("line3 length, bufsz:" + bufsz), 0, out.getLength());
            c += in.readLine(out);// "ccc"\r

            Assert.assertEquals(("line4 length, bufsz:" + bufsz), 3, out.getLength());
            c += in.readLine(out);// dddd\r

            Assert.assertEquals(("line5 length, bufsz:" + bufsz), 4, out.getLength());
            c += in.readLine(out);// ""\r

            Assert.assertEquals(("line6 length, bufsz:" + bufsz), 0, out.getLength());
            c += in.readLine(out);// ""\r\n

            Assert.assertEquals(("line7 length, bufsz:" + bufsz), 0, out.getLength());
            c += in.readLine(out);// ""\r\n

            Assert.assertEquals(("line8 length, bufsz:" + bufsz), 0, out.getLength());
            c += in.readLine(out);// "eeeee"EOF

            Assert.assertEquals(("line9 length, bufsz:" + bufsz), 5, out.getLength());
            Assert.assertEquals(("end of file, bufsz: " + bufsz), 0, in.readLine(out));
            Assert.assertEquals(("total bytes, bufsz: " + bufsz), c, STRLENBYTES);
        }
    }

    /**
     * Test readLine for correct interpretation of maxLineLength
     * (returned string should be clipped at maxLineLength, and the
     * remaining bytes on the same line should be thrown out).
     * Also check that returned value matches the string length.
     * Varies buffer size to stress test.
     *
     * @throws Exception
     * 		
     */
    @Test(timeout = 5000)
    public void testMaxLineLength() throws Exception {
        final String STR = "a\nbb\n\nccc\rdddd\r\neeeee";
        final int STRLENBYTES = STR.getBytes().length;
        Text out = new Text();
        for (int bufsz = 1; bufsz < (STRLENBYTES + 1); ++bufsz) {
            LineReader in = TestTextInputFormat.makeStream(STR, bufsz);
            int c = 0;
            c += in.readLine(out, 1);
            Assert.assertEquals(("line1 length, bufsz: " + bufsz), 1, out.getLength());
            c += in.readLine(out, 1);
            Assert.assertEquals(("line2 length, bufsz: " + bufsz), 1, out.getLength());
            c += in.readLine(out, 1);
            Assert.assertEquals(("line3 length, bufsz: " + bufsz), 0, out.getLength());
            c += in.readLine(out, 3);
            Assert.assertEquals(("line4 length, bufsz: " + bufsz), 3, out.getLength());
            c += in.readLine(out, 10);
            Assert.assertEquals(("line5 length, bufsz: " + bufsz), 4, out.getLength());
            c += in.readLine(out, 8);
            Assert.assertEquals(("line5 length, bufsz: " + bufsz), 5, out.getLength());
            Assert.assertEquals(("end of file, bufsz: " + bufsz), 0, in.readLine(out));
            Assert.assertEquals(("total bytes, bufsz: " + bufsz), c, STRLENBYTES);
        }
    }

    @Test(timeout = 5000)
    public void testMRMaxLine() throws Exception {
        final int MAXPOS = 1024 * 1024;
        final int MAXLINE = 10 * 1024;
        final int BUF = 64 * 1024;
        final InputStream infNull = new InputStream() {
            int position = 0;

            final int MAXPOSBUF = (1024 * 1024) + BUF;// max LRR pos + LineReader buf


            @Override
            public int read() {
                ++(position);
                return 0;
            }

            @Override
            public int read(byte[] b) {
                Assert.assertTrue("Read too many bytes from the stream", ((position) < (MAXPOSBUF)));
                Arrays.fill(b, ((byte) (0)));
                position += b.length;
                return b.length;
            }

            public void reset() {
                position = 0;
            }
        };
        final LongWritable key = new LongWritable();
        final Text val = new Text();
        TestTextInputFormat.LOG.info("Reading a line from /dev/null");
        final Configuration conf = new Configuration(false);
        conf.setInt(MAX_LINE_LENGTH, MAXLINE);
        conf.setInt("io.file.buffer.size", BUF);// used by LRR

        // test another constructor
        LineRecordReader lrr = new LineRecordReader(infNull, 0, MAXPOS, conf);
        Assert.assertFalse("Read a line from null", lrr.next(key, val));
        infNull.reset();
        lrr = new LineRecordReader(infNull, 0L, MAXLINE, MAXPOS);
        Assert.assertFalse("Read a line from null", lrr.next(key, val));
    }

    private static final Reporter voidReporter = NULL;

    /**
     * Test using the gzip codec for reading
     */
    @Test(timeout = 5000)
    public void testGzip() throws IOException {
        JobConf job = new JobConf(TestTextInputFormat.defaultConf);
        CompressionCodec gzip = new GzipCodec();
        ReflectionUtils.setConf(gzip, job);
        TestTextInputFormat.localFs.delete(TestTextInputFormat.workDir, true);
        TestTextInputFormat.writeFile(TestTextInputFormat.localFs, new Path(TestTextInputFormat.workDir, "part1.txt.gz"), gzip, "the quick\nbrown\nfox jumped\nover\n the lazy\n dog\n");
        TestTextInputFormat.writeFile(TestTextInputFormat.localFs, new Path(TestTextInputFormat.workDir, "part2.txt.gz"), gzip, "this is a test\nof gzip\n");
        FileInputFormat.setInputPaths(job, TestTextInputFormat.workDir);
        TextInputFormat format = new TextInputFormat();
        format.configure(job);
        InputSplit[] splits = format.getSplits(job, 100);
        Assert.assertEquals("compressed splits == 2", 2, splits.length);
        FileSplit tmp = ((FileSplit) (splits[0]));
        if (tmp.getPath().getName().equals("part2.txt.gz")) {
            splits[0] = splits[1];
            splits[1] = tmp;
        }
        List<Text> results = TestTextInputFormat.readSplit(format, splits[0], job);
        Assert.assertEquals("splits[0] length", 6, results.size());
        Assert.assertEquals("splits[0][5]", " dog", results.get(5).toString());
        results = TestTextInputFormat.readSplit(format, splits[1], job);
        Assert.assertEquals("splits[1] length", 2, results.size());
        Assert.assertEquals("splits[1][0]", "this is a test", results.get(0).toString());
        Assert.assertEquals("splits[1][1]", "of gzip", results.get(1).toString());
    }

    /**
     * Test using the gzip codec and an empty input file
     */
    @Test(timeout = 5000)
    public void testGzipEmpty() throws IOException {
        JobConf job = new JobConf(TestTextInputFormat.defaultConf);
        CompressionCodec gzip = new GzipCodec();
        ReflectionUtils.setConf(gzip, job);
        TestTextInputFormat.localFs.delete(TestTextInputFormat.workDir, true);
        TestTextInputFormat.writeFile(TestTextInputFormat.localFs, new Path(TestTextInputFormat.workDir, "empty.gz"), gzip, "");
        FileInputFormat.setInputPaths(job, TestTextInputFormat.workDir);
        TextInputFormat format = new TextInputFormat();
        format.configure(job);
        InputSplit[] splits = format.getSplits(job, 100);
        Assert.assertEquals("Compressed files of length 0 are not returned from FileInputFormat.getSplits().", 1, splits.length);
        List<Text> results = TestTextInputFormat.readSplit(format, splits[0], job);
        Assert.assertEquals("Compressed empty file length == 0", 0, results.size());
    }
}

