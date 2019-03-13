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


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.util.LineReader;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Reporter.NULL;


public class TestKeyValueTextInputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestKeyValueTextInputFormat.class);

    private static int MAX_LENGTH = 10000;

    private static JobConf defaultConf = new JobConf();

    private static java.io.FileSystem localFs = null;

    static {
        try {
            TestKeyValueTextInputFormat.localFs = FileSystem.getLocal(TestKeyValueTextInputFormat.defaultConf);
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    private static Path workDir = new Path(new Path(System.getProperty("test.build.data", "."), "data"), "TestKeyValueTextInputFormat");

    @Test
    public void testFormat() throws Exception {
        JobConf job = new JobConf();
        Path file = new Path(TestKeyValueTextInputFormat.workDir, "test.txt");
        // A reporter that does nothing
        Reporter reporter = NULL;
        int seed = new Random().nextInt();
        TestKeyValueTextInputFormat.LOG.info(("seed = " + seed));
        Random random = new Random(seed);
        TestKeyValueTextInputFormat.localFs.delete(TestKeyValueTextInputFormat.workDir, true);
        FileInputFormat.setInputPaths(job, TestKeyValueTextInputFormat.workDir);
        // for a variety of lengths
        for (int length = 0; length < (TestKeyValueTextInputFormat.MAX_LENGTH); length += (random.nextInt(((TestKeyValueTextInputFormat.MAX_LENGTH) / 10))) + 1) {
            TestKeyValueTextInputFormat.LOG.debug(("creating; entries = " + length));
            // create a file with length entries
            Writer writer = new OutputStreamWriter(TestKeyValueTextInputFormat.localFs.create(file));
            try {
                for (int i = 0; i < length; i++) {
                    writer.write(Integer.toString((i * 2)));
                    writer.write("\t");
                    writer.write(Integer.toString(i));
                    writer.write("\n");
                }
            } finally {
                writer.close();
            }
            // try splitting the file in a variety of sizes
            KeyValueTextInputFormat format = new KeyValueTextInputFormat();
            format.configure(job);
            for (int i = 0; i < 3; i++) {
                int numSplits = (random.nextInt(((TestKeyValueTextInputFormat.MAX_LENGTH) / 20))) + 1;
                TestKeyValueTextInputFormat.LOG.debug(("splitting: requesting = " + numSplits));
                InputSplit[] splits = format.getSplits(job, numSplits);
                TestKeyValueTextInputFormat.LOG.debug(("splitting: got =        " + (splits.length)));
                // check each split
                BitSet bits = new BitSet(length);
                for (int j = 0; j < (splits.length); j++) {
                    TestKeyValueTextInputFormat.LOG.debug(((("split[" + j) + "]= ") + (splits[j])));
                    RecordReader<Text, Text> reader = format.getRecordReader(splits[j], job, reporter);
                    Class readerClass = reader.getClass();
                    Assert.assertEquals("reader class is KeyValueLineRecordReader.", KeyValueLineRecordReader.class, readerClass);
                    Text key = reader.createKey();
                    Class keyClass = key.getClass();
                    Text value = reader.createValue();
                    Class valueClass = value.getClass();
                    Assert.assertEquals("Key class is Text.", Text.class, keyClass);
                    Assert.assertEquals("Value class is Text.", Text.class, valueClass);
                    try {
                        int count = 0;
                        while (reader.next(key, value)) {
                            int v = Integer.parseInt(value.toString());
                            TestKeyValueTextInputFormat.LOG.debug(("read " + v));
                            if (bits.get(v)) {
                                TestKeyValueTextInputFormat.LOG.warn(((((("conflict with " + v) + " in split ") + j) + " at position ") + (reader.getPos())));
                            }
                            Assert.assertFalse("Key in multiple partitions.", bits.get(v));
                            bits.set(v);
                            count++;
                        } 
                        TestKeyValueTextInputFormat.LOG.debug(((((("splits[" + j) + "]=") + (splits[j])) + " count=") + count));
                    } finally {
                        reader.close();
                    }
                }
                Assert.assertEquals("Some keys in no partition.", length, bits.cardinality());
            }
        }
    }

    @Test
    public void testUTF8() throws Exception {
        LineReader in = null;
        try {
            in = makeStream("abcd\u20acbdcd\u20ac");
            Text line = new Text();
            in.readLine(line);
            Assert.assertEquals("readLine changed utf8 characters", "abcd\u20acbdcd\u20ac", line.toString());
            in = makeStream("abc\u200axyz");
            in.readLine(line);
            Assert.assertEquals("split on fake newline", "abc\u200axyz", line.toString());
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    @Test
    public void testNewLines() throws Exception {
        LineReader in = null;
        try {
            in = makeStream("a\nbb\n\nccc\rdddd\r\neeeee");
            Text out = new Text();
            in.readLine(out);
            Assert.assertEquals("line1 length", 1, out.getLength());
            in.readLine(out);
            Assert.assertEquals("line2 length", 2, out.getLength());
            in.readLine(out);
            Assert.assertEquals("line3 length", 0, out.getLength());
            in.readLine(out);
            Assert.assertEquals("line4 length", 3, out.getLength());
            in.readLine(out);
            Assert.assertEquals("line5 length", 4, out.getLength());
            in.readLine(out);
            Assert.assertEquals("line5 length", 5, out.getLength());
            Assert.assertEquals("end of file", 0, in.readLine(out));
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    private static final Reporter voidReporter = NULL;

    /**
     * Test using the gzip codec for reading
     */
    @Test
    public void testGzip() throws IOException {
        JobConf job = new JobConf();
        CompressionCodec gzip = new GzipCodec();
        ReflectionUtils.setConf(gzip, job);
        TestKeyValueTextInputFormat.localFs.delete(TestKeyValueTextInputFormat.workDir, true);
        TestKeyValueTextInputFormat.writeFile(TestKeyValueTextInputFormat.localFs, new Path(TestKeyValueTextInputFormat.workDir, "part1.txt.gz"), gzip, "line-1\tthe quick\nline-2\tbrown\nline-3\tfox jumped\nline-4\tover\nline-5\t the lazy\nline-6\t dog\n");
        TestKeyValueTextInputFormat.writeFile(TestKeyValueTextInputFormat.localFs, new Path(TestKeyValueTextInputFormat.workDir, "part2.txt.gz"), gzip, "line-1\tthis is a test\nline-1\tof gzip\n");
        FileInputFormat.setInputPaths(job, TestKeyValueTextInputFormat.workDir);
        KeyValueTextInputFormat format = new KeyValueTextInputFormat();
        format.configure(job);
        InputSplit[] splits = format.getSplits(job, 100);
        Assert.assertEquals("compressed splits == 2", 2, splits.length);
        FileSplit tmp = ((FileSplit) (splits[0]));
        if (tmp.getPath().getName().equals("part2.txt.gz")) {
            splits[0] = splits[1];
            splits[1] = tmp;
        }
        List<Text> results = TestKeyValueTextInputFormat.readSplit(format, splits[0], job);
        Assert.assertEquals("splits[0] length", 6, results.size());
        Assert.assertEquals("splits[0][5]", " dog", results.get(5).toString());
        results = TestKeyValueTextInputFormat.readSplit(format, splits[1], job);
        Assert.assertEquals("splits[1] length", 2, results.size());
        Assert.assertEquals("splits[1][0]", "this is a test", results.get(0).toString());
        Assert.assertEquals("splits[1][1]", "of gzip", results.get(1).toString());
    }
}

