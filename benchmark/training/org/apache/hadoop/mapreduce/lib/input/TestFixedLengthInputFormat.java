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
import java.util.List;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
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


public class TestFixedLengthInputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestFixedLengthInputFormat.class);

    private static Configuration defaultConf;

    private static FileSystem localFs;

    private static Path workDir;

    // some chars for the record data
    private static char[] chars;

    private static Random charRand;

    /**
     * 20 random tests of various record, file, and split sizes.  All tests have
     * uncompressed file as input.
     */
    @Test(timeout = 500000)
    public void testFormat() throws Exception {
        runRandomTests(null);
    }

    /**
     * 20 random tests of various record, file, and split sizes.  All tests have
     * compressed file as input.
     */
    @Test(timeout = 500000)
    public void testFormatCompressedIn() throws Exception {
        runRandomTests(new GzipCodec());
    }

    /**
     * Test with no record length set.
     */
    @Test(timeout = 5000)
    public void testNoRecordLength() throws Exception {
        TestFixedLengthInputFormat.localFs.delete(TestFixedLengthInputFormat.workDir, true);
        Path file = new Path(TestFixedLengthInputFormat.workDir, new String("testFormat.txt"));
        createFile(file, null, 10, 10);
        // Create the job and do not set fixed record length
        Job job = Job.getInstance(TestFixedLengthInputFormat.defaultConf);
        FileInputFormat.setInputPaths(job, TestFixedLengthInputFormat.workDir);
        FixedLengthInputFormat format = new FixedLengthInputFormat();
        List<InputSplit> splits = format.getSplits(job);
        boolean exceptionThrown = false;
        for (InputSplit split : splits) {
            try {
                TaskAttemptContext context = MapReduceTestUtil.createDummyMapTaskAttemptContext(job.getConfiguration());
                RecordReader<LongWritable, BytesWritable> reader = format.createRecordReader(split, context);
                MapContext<LongWritable, BytesWritable, LongWritable, BytesWritable> mcontext = new org.apache.hadoop.mapreduce.task.MapContextImpl<LongWritable, BytesWritable, LongWritable, BytesWritable>(job.getConfiguration(), context.getTaskAttemptID(), reader, null, null, MapReduceTestUtil.createDummyReporter(), split);
                reader.initialize(split, mcontext);
            } catch (IOException ioe) {
                exceptionThrown = true;
                TestFixedLengthInputFormat.LOG.info(("Exception message:" + (ioe.getMessage())));
            }
        }
        Assert.assertTrue("Exception for not setting record length:", exceptionThrown);
    }

    /**
     * Test with record length set to 0
     */
    @Test(timeout = 5000)
    public void testZeroRecordLength() throws Exception {
        TestFixedLengthInputFormat.localFs.delete(TestFixedLengthInputFormat.workDir, true);
        Path file = new Path(TestFixedLengthInputFormat.workDir, new String("testFormat.txt"));
        createFile(file, null, 10, 10);
        Job job = Job.getInstance(TestFixedLengthInputFormat.defaultConf);
        // Set the fixed length record length config property
        FixedLengthInputFormat format = new FixedLengthInputFormat();
        format.setRecordLength(job.getConfiguration(), 0);
        FileInputFormat.setInputPaths(job, TestFixedLengthInputFormat.workDir);
        List<InputSplit> splits = format.getSplits(job);
        boolean exceptionThrown = false;
        for (InputSplit split : splits) {
            try {
                TaskAttemptContext context = MapReduceTestUtil.createDummyMapTaskAttemptContext(job.getConfiguration());
                RecordReader<LongWritable, BytesWritable> reader = format.createRecordReader(split, context);
                MapContext<LongWritable, BytesWritable, LongWritable, BytesWritable> mcontext = new org.apache.hadoop.mapreduce.task.MapContextImpl<LongWritable, BytesWritable, LongWritable, BytesWritable>(job.getConfiguration(), context.getTaskAttemptID(), reader, null, null, MapReduceTestUtil.createDummyReporter(), split);
                reader.initialize(split, mcontext);
            } catch (IOException ioe) {
                exceptionThrown = true;
                TestFixedLengthInputFormat.LOG.info(("Exception message:" + (ioe.getMessage())));
            }
        }
        Assert.assertTrue("Exception for zero record length:", exceptionThrown);
    }

    /**
     * Test with record length set to a negative value
     */
    @Test(timeout = 5000)
    public void testNegativeRecordLength() throws Exception {
        TestFixedLengthInputFormat.localFs.delete(TestFixedLengthInputFormat.workDir, true);
        Path file = new Path(TestFixedLengthInputFormat.workDir, new String("testFormat.txt"));
        createFile(file, null, 10, 10);
        // Set the fixed length record length config property
        Job job = Job.getInstance(TestFixedLengthInputFormat.defaultConf);
        FixedLengthInputFormat format = new FixedLengthInputFormat();
        format.setRecordLength(job.getConfiguration(), (-10));
        FileInputFormat.setInputPaths(job, TestFixedLengthInputFormat.workDir);
        List<InputSplit> splits = format.getSplits(job);
        boolean exceptionThrown = false;
        for (InputSplit split : splits) {
            try {
                TaskAttemptContext context = MapReduceTestUtil.createDummyMapTaskAttemptContext(job.getConfiguration());
                RecordReader<LongWritable, BytesWritable> reader = format.createRecordReader(split, context);
                MapContext<LongWritable, BytesWritable, LongWritable, BytesWritable> mcontext = new org.apache.hadoop.mapreduce.task.MapContextImpl<LongWritable, BytesWritable, LongWritable, BytesWritable>(job.getConfiguration(), context.getTaskAttemptID(), reader, null, null, MapReduceTestUtil.createDummyReporter(), split);
                reader.initialize(split, mcontext);
            } catch (IOException ioe) {
                exceptionThrown = true;
                TestFixedLengthInputFormat.LOG.info(("Exception message:" + (ioe.getMessage())));
            }
        }
        Assert.assertTrue("Exception for negative record length:", exceptionThrown);
    }

    /**
     * Test with partial record at the end of a compressed input file.
     */
    @Test(timeout = 5000)
    public void testPartialRecordCompressedIn() throws Exception {
        CompressionCodec gzip = new GzipCodec();
        runPartialRecordTest(gzip);
    }

    /**
     * Test with partial record at the end of an uncompressed input file.
     */
    @Test(timeout = 5000)
    public void testPartialRecordUncompressedIn() throws Exception {
        runPartialRecordTest(null);
    }

    /**
     * Test using the gzip codec with two input files.
     */
    @Test(timeout = 5000)
    public void testGzipWithTwoInputs() throws Exception {
        CompressionCodec gzip = new GzipCodec();
        TestFixedLengthInputFormat.localFs.delete(TestFixedLengthInputFormat.workDir, true);
        Job job = Job.getInstance(TestFixedLengthInputFormat.defaultConf);
        FixedLengthInputFormat format = new FixedLengthInputFormat();
        format.setRecordLength(job.getConfiguration(), 5);
        ReflectionUtils.setConf(gzip, job.getConfiguration());
        FileInputFormat.setInputPaths(job, TestFixedLengthInputFormat.workDir);
        // Create files with fixed length records with 5 byte long records.
        TestFixedLengthInputFormat.writeFile(TestFixedLengthInputFormat.localFs, new Path(TestFixedLengthInputFormat.workDir, "part1.txt.gz"), gzip, "one  two  threefour five six  seveneightnine ten  ");
        TestFixedLengthInputFormat.writeFile(TestFixedLengthInputFormat.localFs, new Path(TestFixedLengthInputFormat.workDir, "part2.txt.gz"), gzip, "ten  nine eightsevensix  five four threetwo  one  ");
        List<InputSplit> splits = format.getSplits(job);
        Assert.assertEquals("compressed splits == 2", 2, splits.size());
        FileSplit tmp = ((FileSplit) (splits.get(0)));
        if (tmp.getPath().getName().equals("part2.txt.gz")) {
            splits.set(0, splits.get(1));
            splits.set(1, tmp);
        }
        List<String> results = TestFixedLengthInputFormat.readSplit(format, splits.get(0), job);
        Assert.assertEquals("splits[0] length", 10, results.size());
        Assert.assertEquals("splits[0][5]", "six  ", results.get(5));
        results = TestFixedLengthInputFormat.readSplit(format, splits.get(1), job);
        Assert.assertEquals("splits[1] length", 10, results.size());
        Assert.assertEquals("splits[1][0]", "ten  ", results.get(0));
        Assert.assertEquals("splits[1][1]", "nine ", results.get(1));
    }
}

