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


import SequenceFileInputFilter.MD5Filter;
import SequenceFileInputFilter.PercentFilter;
import SequenceFileInputFilter.RegexFilter;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMRSequenceFileInputFilter {
    private static final Logger LOG = LoggerFactory.getLogger(TestMRSequenceFileInputFilter.class);

    private static final int MAX_LENGTH = 15000;

    private static final Configuration conf = new Configuration();

    private static final Job job;

    private static final FileSystem fs;

    private static final Path inDir = new Path(((System.getProperty("test.build.data", ".")) + "/mapred"));

    private static final Path inFile = new Path(TestMRSequenceFileInputFilter.inDir, "test.seq");

    private static final Random random = new Random(1);

    static {
        try {
            job = Job.getInstance(TestMRSequenceFileInputFilter.conf);
            FileInputFormat.setInputPaths(TestMRSequenceFileInputFilter.job, TestMRSequenceFileInputFilter.inDir);
            fs = FileSystem.getLocal(TestMRSequenceFileInputFilter.conf);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRegexFilter() throws Exception {
        // set the filter class
        TestMRSequenceFileInputFilter.LOG.info("Testing Regex Filter with patter: \\A10*");
        SequenceFileInputFilter.setFilterClass(TestMRSequenceFileInputFilter.job, RegexFilter.class);
        RegexFilter.setPattern(TestMRSequenceFileInputFilter.job.getConfiguration(), "\\A10*");
        // clean input dir
        TestMRSequenceFileInputFilter.fs.delete(TestMRSequenceFileInputFilter.inDir, true);
        // for a variety of lengths
        for (int length = 1; length < (TestMRSequenceFileInputFilter.MAX_LENGTH); length += (TestMRSequenceFileInputFilter.random.nextInt(((TestMRSequenceFileInputFilter.MAX_LENGTH) / 10))) + 1) {
            TestMRSequenceFileInputFilter.LOG.info(("******Number of records: " + length));
            TestMRSequenceFileInputFilter.createSequenceFile(length);
            int count = countRecords(0);
            Assert.assertEquals(count, (length == 0 ? 0 : ((int) (Math.log10(length))) + 1));
        }
        // clean up
        TestMRSequenceFileInputFilter.fs.delete(TestMRSequenceFileInputFilter.inDir, true);
    }

    @Test
    public void testPercentFilter() throws Exception {
        TestMRSequenceFileInputFilter.LOG.info("Testing Percent Filter with frequency: 1000");
        // set the filter class
        SequenceFileInputFilter.setFilterClass(TestMRSequenceFileInputFilter.job, PercentFilter.class);
        PercentFilter.setFrequency(TestMRSequenceFileInputFilter.job.getConfiguration(), 1000);
        // clean input dir
        TestMRSequenceFileInputFilter.fs.delete(TestMRSequenceFileInputFilter.inDir, true);
        // for a variety of lengths
        for (int length = 0; length < (TestMRSequenceFileInputFilter.MAX_LENGTH); length += (TestMRSequenceFileInputFilter.random.nextInt(((TestMRSequenceFileInputFilter.MAX_LENGTH) / 10))) + 1) {
            TestMRSequenceFileInputFilter.LOG.info(("******Number of records: " + length));
            TestMRSequenceFileInputFilter.createSequenceFile(length);
            int count = countRecords(1);
            TestMRSequenceFileInputFilter.LOG.info((("Accepted " + count) + " records"));
            int expectedCount = length / 1000;
            if ((expectedCount * 1000) != length)
                expectedCount++;

            Assert.assertEquals(count, expectedCount);
        }
        // clean up
        TestMRSequenceFileInputFilter.fs.delete(TestMRSequenceFileInputFilter.inDir, true);
    }

    @Test
    public void testMD5Filter() throws Exception {
        // set the filter class
        TestMRSequenceFileInputFilter.LOG.info("Testing MD5 Filter with frequency: 1000");
        SequenceFileInputFilter.setFilterClass(TestMRSequenceFileInputFilter.job, MD5Filter.class);
        MD5Filter.setFrequency(TestMRSequenceFileInputFilter.job.getConfiguration(), 1000);
        // clean input dir
        TestMRSequenceFileInputFilter.fs.delete(TestMRSequenceFileInputFilter.inDir, true);
        // for a variety of lengths
        for (int length = 0; length < (TestMRSequenceFileInputFilter.MAX_LENGTH); length += (TestMRSequenceFileInputFilter.random.nextInt(((TestMRSequenceFileInputFilter.MAX_LENGTH) / 10))) + 1) {
            TestMRSequenceFileInputFilter.LOG.info(("******Number of records: " + length));
            TestMRSequenceFileInputFilter.createSequenceFile(length);
            TestMRSequenceFileInputFilter.LOG.info((("Accepted " + (countRecords(0))) + " records"));
        }
        // clean up
        TestMRSequenceFileInputFilter.fs.delete(TestMRSequenceFileInputFilter.inDir, true);
    }
}

