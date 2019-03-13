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
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.MapContext;
import org.apache.hadoop.mapreduce.MapReduceTestUtil;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.util.ReflectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestCombineTextInputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestCombineTextInputFormat.class);

    private static Configuration defaultConf = new Configuration();

    private static FileSystem localFs = null;

    static {
        try {
            TestCombineTextInputFormat.defaultConf.set("fs.defaultFS", "file:///");
            TestCombineTextInputFormat.localFs = FileSystem.getLocal(TestCombineTextInputFormat.defaultConf);
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    private static Path workDir = new Path(new Path(System.getProperty("test.build.data", "."), "data"), "TestCombineTextInputFormat");

    @Test(timeout = 10000)
    public void testFormat() throws Exception {
        Job job = Job.getInstance(new Configuration(TestCombineTextInputFormat.defaultConf));
        Random random = new Random();
        long seed = random.nextLong();
        TestCombineTextInputFormat.LOG.info(("seed = " + seed));
        random.setSeed(seed);
        TestCombineTextInputFormat.localFs.delete(TestCombineTextInputFormat.workDir, true);
        FileInputFormat.setInputPaths(job, TestCombineTextInputFormat.workDir);
        final int length = 10000;
        final int numFiles = 10;
        // create files with various lengths
        TestCombineTextInputFormat.createFiles(length, numFiles, random);
        // create a combined split for the files
        CombineTextInputFormat format = new CombineTextInputFormat();
        for (int i = 0; i < 3; i++) {
            int numSplits = (random.nextInt((length / 20))) + 1;
            TestCombineTextInputFormat.LOG.info(("splitting: requesting = " + numSplits));
            List<InputSplit> splits = format.getSplits(job);
            TestCombineTextInputFormat.LOG.info(("splitting: got =        " + (splits.size())));
            // we should have a single split as the length is comfortably smaller than
            // the block size
            Assert.assertEquals("We got more than one splits!", 1, splits.size());
            InputSplit split = splits.get(0);
            Assert.assertEquals("It should be CombineFileSplit", CombineFileSplit.class, split.getClass());
            // check the split
            BitSet bits = new BitSet(length);
            TestCombineTextInputFormat.LOG.debug(("split= " + split));
            TaskAttemptContext context = MapReduceTestUtil.createDummyMapTaskAttemptContext(job.getConfiguration());
            RecordReader<LongWritable, Text> reader = format.createRecordReader(split, context);
            Assert.assertEquals("reader class is CombineFileRecordReader.", CombineFileRecordReader.class, reader.getClass());
            MapContext<LongWritable, Text, LongWritable, Text> mcontext = new org.apache.hadoop.mapreduce.task.MapContextImpl<LongWritable, Text, LongWritable, Text>(job.getConfiguration(), context.getTaskAttemptID(), reader, null, null, MapReduceTestUtil.createDummyReporter(), split);
            reader.initialize(split, mcontext);
            try {
                int count = 0;
                while (reader.nextKeyValue()) {
                    LongWritable key = reader.getCurrentKey();
                    Assert.assertNotNull("Key should not be null.", key);
                    Text value = reader.getCurrentValue();
                    final int v = Integer.parseInt(value.toString());
                    TestCombineTextInputFormat.LOG.debug(("read " + v));
                    Assert.assertFalse("Key in multiple partitions.", bits.get(v));
                    bits.set(v);
                    count++;
                } 
                TestCombineTextInputFormat.LOG.debug(((("split=" + split) + " count=") + count));
            } finally {
                reader.close();
            }
            Assert.assertEquals("Some keys in no partition.", length, bits.cardinality());
        }
    }

    private static class Range {
        private final int start;

        private final int end;

        Range(int start, int end) {
            this.start = start;
            this.end = end;
        }

        @Override
        public String toString() {
            return ((("(" + (start)) + ", ") + (end)) + ")";
        }
    }

    /**
     * Test using the gzip codec for reading
     */
    @Test(timeout = 10000)
    public void testGzip() throws IOException, InterruptedException {
        Configuration conf = new Configuration(TestCombineTextInputFormat.defaultConf);
        CompressionCodec gzip = new GzipCodec();
        ReflectionUtils.setConf(gzip, conf);
        TestCombineTextInputFormat.localFs.delete(TestCombineTextInputFormat.workDir, true);
        TestCombineTextInputFormat.writeFile(TestCombineTextInputFormat.localFs, new Path(TestCombineTextInputFormat.workDir, "part1.txt.gz"), gzip, "the quick\nbrown\nfox jumped\nover\n the lazy\n dog\n");
        TestCombineTextInputFormat.writeFile(TestCombineTextInputFormat.localFs, new Path(TestCombineTextInputFormat.workDir, "part2.txt.gz"), gzip, "this is a test\nof gzip\n");
        Job job = Job.getInstance(conf);
        FileInputFormat.setInputPaths(job, TestCombineTextInputFormat.workDir);
        CombineTextInputFormat format = new CombineTextInputFormat();
        List<InputSplit> splits = format.getSplits(job);
        Assert.assertEquals("compressed splits == 1", 1, splits.size());
        List<Text> results = TestCombineTextInputFormat.readSplit(format, splits.get(0), job);
        Assert.assertEquals("splits[0] length", 8, results.size());
        final String[] firstList = new String[]{ "the quick", "brown", "fox jumped", "over", " the lazy", " dog" };
        final String[] secondList = new String[]{ "this is a test", "of gzip" };
        String first = results.get(0).toString();
        if (first.equals(firstList[0])) {
            TestCombineTextInputFormat.testResults(results, firstList, secondList);
        } else
            if (first.equals(secondList[0])) {
                TestCombineTextInputFormat.testResults(results, secondList, firstList);
            } else {
                Assert.fail("unexpected first token!");
            }

    }
}

