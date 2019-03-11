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
package org.apache.hadoop.mapreduce.lib.input;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.MapReduceTestUtil;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.util.LineReader;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMRKeyValueTextInputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestMRKeyValueTextInputFormat.class);

    private static Configuration defaultConf = new Configuration();

    private static FileSystem localFs = null;

    static {
        try {
            TestMRKeyValueTextInputFormat.defaultConf.set("fs.defaultFS", "file:///");
            TestMRKeyValueTextInputFormat.localFs = FileSystem.getLocal(TestMRKeyValueTextInputFormat.defaultConf);
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    private static Path workDir = new Path(new Path(System.getProperty("test.build.data", "."), "data"), "TestKeyValueTextInputFormat");

    @Test
    public void testFormat() throws Exception {
        Job job = Job.getInstance(new Configuration(TestMRKeyValueTextInputFormat.defaultConf));
        Path file = new Path(TestMRKeyValueTextInputFormat.workDir, "test.txt");
        int seed = new Random().nextInt();
        TestMRKeyValueTextInputFormat.LOG.info(("seed = " + seed));
        Random random = new Random(seed);
        TestMRKeyValueTextInputFormat.localFs.delete(TestMRKeyValueTextInputFormat.workDir, true);
        FileInputFormat.setInputPaths(job, TestMRKeyValueTextInputFormat.workDir);
        final int MAX_LENGTH = 10000;
        // for a variety of lengths
        for (int length = 0; length < MAX_LENGTH; length += (random.nextInt((MAX_LENGTH / 10))) + 1) {
            TestMRKeyValueTextInputFormat.LOG.debug(("creating; entries = " + length));
            // create a file with length entries
            Writer writer = new OutputStreamWriter(TestMRKeyValueTextInputFormat.localFs.create(file));
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
            for (int i = 0; i < 3; i++) {
                int numSplits = (random.nextInt((MAX_LENGTH / 20))) + 1;
                TestMRKeyValueTextInputFormat.LOG.debug(("splitting: requesting = " + numSplits));
                List<InputSplit> splits = format.getSplits(job);
                TestMRKeyValueTextInputFormat.LOG.debug(("splitting: got =        " + (splits.size())));
                // check each split
                BitSet bits = new BitSet(length);
                for (int j = 0; j < (splits.size()); j++) {
                    TestMRKeyValueTextInputFormat.LOG.debug(((("split[" + j) + "]= ") + (splits.get(j))));
                    TaskAttemptContext context = MapReduceTestUtil.createDummyMapTaskAttemptContext(job.getConfiguration());
                    RecordReader<Text, Text> reader = format.createRecordReader(splits.get(j), context);
                    Class<?> clazz = reader.getClass();
                    Assert.assertEquals("reader class is KeyValueLineRecordReader.", KeyValueLineRecordReader.class, clazz);
                    MapContext<Text, Text, Text, Text> mcontext = new org.apache.hadoop.mapreduce.task.MapContextImpl<Text, Text, Text, Text>(job.getConfiguration(), context.getTaskAttemptID(), reader, null, null, MapReduceTestUtil.createDummyReporter(), splits.get(j));
                    reader.initialize(splits.get(j), mcontext);
                    Text key = null;
                    Text value = null;
                    try {
                        int count = 0;
                        while (reader.nextKeyValue()) {
                            key = reader.getCurrentKey();
                            clazz = key.getClass();
                            Assert.assertEquals("Key class is Text.", Text.class, clazz);
                            value = reader.getCurrentValue();
                            clazz = value.getClass();
                            Assert.assertEquals("Value class is Text.", Text.class, clazz);
                            final int k = Integer.parseInt(key.toString());
                            final int v = Integer.parseInt(value.toString());
                            Assert.assertEquals("Bad key", 0, (k % 2));
                            Assert.assertEquals("Mismatched key/value", (k / 2), v);
                            TestMRKeyValueTextInputFormat.LOG.debug(("read " + v));
                            Assert.assertFalse("Key in multiple partitions.", bits.get(v));
                            bits.set(v);
                            count++;
                        } 
                        TestMRKeyValueTextInputFormat.LOG.debug(((((("splits[" + j) + "]=") + (splits.get(j))) + " count=") + count));
                    } finally {
                        reader.close();
                    }
                }
                Assert.assertEquals("Some keys in no partition.", length, bits.cardinality());
            }
        }
    }

    @Test
    public void testSplitableCodecs() throws Exception {
        final Job job = Job.getInstance(TestMRKeyValueTextInputFormat.defaultConf);
        final Configuration conf = job.getConfiguration();
        // Create the codec
        CompressionCodec codec = null;
        try {
            codec = ((CompressionCodec) (ReflectionUtils.newInstance(conf.getClassByName("org.apache.hadoop.io.compress.BZip2Codec"), conf)));
        } catch (ClassNotFoundException cnfe) {
            throw new IOException("Illegal codec!");
        }
        Path file = new Path(TestMRKeyValueTextInputFormat.workDir, ("test" + (codec.getDefaultExtension())));
        int seed = new Random().nextInt();
        TestMRKeyValueTextInputFormat.LOG.info(("seed = " + seed));
        Random random = new Random(seed);
        TestMRKeyValueTextInputFormat.localFs.delete(TestMRKeyValueTextInputFormat.workDir, true);
        FileInputFormat.setInputPaths(job, TestMRKeyValueTextInputFormat.workDir);
        final int MAX_LENGTH = 500000;
        FileInputFormat.setMaxInputSplitSize(job, (MAX_LENGTH / 20));
        // for a variety of lengths
        for (int length = 0; length < MAX_LENGTH; length += (random.nextInt((MAX_LENGTH / 4))) + 1) {
            TestMRKeyValueTextInputFormat.LOG.info(("creating; entries = " + length));
            // create a file with length entries
            Writer writer = new OutputStreamWriter(codec.createOutputStream(TestMRKeyValueTextInputFormat.localFs.create(file)));
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
            Assert.assertTrue("KVTIF claims not splittable", format.isSplitable(job, file));
            for (int i = 0; i < 3; i++) {
                int numSplits = (random.nextInt((MAX_LENGTH / 2000))) + 1;
                TestMRKeyValueTextInputFormat.LOG.info(("splitting: requesting = " + numSplits));
                List<InputSplit> splits = format.getSplits(job);
                TestMRKeyValueTextInputFormat.LOG.info(("splitting: got =        " + (splits.size())));
                // check each split
                BitSet bits = new BitSet(length);
                for (int j = 0; j < (splits.size()); j++) {
                    TestMRKeyValueTextInputFormat.LOG.debug(((("split[" + j) + "]= ") + (splits.get(j))));
                    TaskAttemptContext context = MapReduceTestUtil.createDummyMapTaskAttemptContext(job.getConfiguration());
                    RecordReader<Text, Text> reader = format.createRecordReader(splits.get(j), context);
                    Class<?> clazz = reader.getClass();
                    MapContext<Text, Text, Text, Text> mcontext = new org.apache.hadoop.mapreduce.task.MapContextImpl<Text, Text, Text, Text>(job.getConfiguration(), context.getTaskAttemptID(), reader, null, null, MapReduceTestUtil.createDummyReporter(), splits.get(j));
                    reader.initialize(splits.get(j), mcontext);
                    Text key = null;
                    Text value = null;
                    try {
                        int count = 0;
                        while (reader.nextKeyValue()) {
                            key = reader.getCurrentKey();
                            value = reader.getCurrentValue();
                            final int k = Integer.parseInt(key.toString());
                            final int v = Integer.parseInt(value.toString());
                            Assert.assertEquals("Bad key", 0, (k % 2));
                            Assert.assertEquals("Mismatched key/value", (k / 2), v);
                            TestMRKeyValueTextInputFormat.LOG.debug(((("read " + k) + ",") + v));
                            Assert.assertFalse((((k + ",") + v) + " in multiple partitions."), bits.get(v));
                            bits.set(v);
                            count++;
                        } 
                        if (count > 0) {
                            TestMRKeyValueTextInputFormat.LOG.info(((((("splits[" + j) + "]=") + (splits.get(j))) + " count=") + count));
                        } else {
                            TestMRKeyValueTextInputFormat.LOG.debug(((((("splits[" + j) + "]=") + (splits.get(j))) + " count=") + count));
                        }
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
        LineReader in = makeStream("abcd\u20acbdcd\u20ac");
        Text line = new Text();
        in.readLine(line);
        Assert.assertEquals("readLine changed utf8 characters", "abcd\u20acbdcd\u20ac", line.toString());
        in = makeStream("abc\u200axyz");
        in.readLine(line);
        Assert.assertEquals("split on fake newline", "abc\u200axyz", line.toString());
    }

    @Test
    public void testNewLines() throws Exception {
        LineReader in = makeStream("a\nbb\n\nccc\rdddd\r\neeeee");
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
    }

    /**
     * Test using the gzip codec for reading
     */
    @Test
    public void testGzip() throws IOException, InterruptedException {
        Configuration conf = new Configuration(TestMRKeyValueTextInputFormat.defaultConf);
        CompressionCodec gzip = new GzipCodec();
        ReflectionUtils.setConf(gzip, conf);
        TestMRKeyValueTextInputFormat.localFs.delete(TestMRKeyValueTextInputFormat.workDir, true);
        TestMRKeyValueTextInputFormat.writeFile(TestMRKeyValueTextInputFormat.localFs, new Path(TestMRKeyValueTextInputFormat.workDir, "part1.txt.gz"), gzip, ("line-1\tthe quick\nline-2\tbrown\nline-3\t" + "fox jumped\nline-4\tover\nline-5\t the lazy\nline-6\t dog\n"));
        TestMRKeyValueTextInputFormat.writeFile(TestMRKeyValueTextInputFormat.localFs, new Path(TestMRKeyValueTextInputFormat.workDir, "part2.txt.gz"), gzip, "line-1\tthis is a test\nline-1\tof gzip\n");
        Job job = Job.getInstance(conf);
        FileInputFormat.setInputPaths(job, TestMRKeyValueTextInputFormat.workDir);
        KeyValueTextInputFormat format = new KeyValueTextInputFormat();
        List<InputSplit> splits = format.getSplits(job);
        Assert.assertEquals("compressed splits == 2", 2, splits.size());
        FileSplit tmp = ((FileSplit) (splits.get(0)));
        if (tmp.getPath().getName().equals("part2.txt.gz")) {
            splits.set(0, splits.get(1));
            splits.set(1, tmp);
        }
        List<Text> results = TestMRKeyValueTextInputFormat.readSplit(format, splits.get(0), job);
        Assert.assertEquals("splits[0] length", 6, results.size());
        Assert.assertEquals("splits[0][0]", "the quick", results.get(0).toString());
        Assert.assertEquals("splits[0][1]", "brown", results.get(1).toString());
        Assert.assertEquals("splits[0][2]", "fox jumped", results.get(2).toString());
        Assert.assertEquals("splits[0][3]", "over", results.get(3).toString());
        Assert.assertEquals("splits[0][4]", " the lazy", results.get(4).toString());
        Assert.assertEquals("splits[0][5]", " dog", results.get(5).toString());
        results = TestMRKeyValueTextInputFormat.readSplit(format, splits.get(1), job);
        Assert.assertEquals("splits[1] length", 2, results.size());
        Assert.assertEquals("splits[1][0]", "this is a test", results.get(0).toString());
        Assert.assertEquals("splits[1][1]", "of gzip", results.get(1).toString());
    }
}

