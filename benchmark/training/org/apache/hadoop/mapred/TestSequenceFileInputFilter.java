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


import SequenceFileInputFilter.MD5Filter;
import SequenceFileInputFilter.PercentFilter;
import SequenceFileInputFilter.RegexFilter;
import java.io.IOException;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;

import static FileInputFormat.LOG;
import static Reporter.NULL;


public class TestSequenceFileInputFilter {
    private static final Logger LOG = LOG;

    private static final int MAX_LENGTH = 15000;

    private static final Configuration conf = new Configuration();

    private static final JobConf job = new JobConf(TestSequenceFileInputFilter.conf);

    private static final FileSystem fs;

    private static final Path inDir = new Path(((System.getProperty("test.build.data", ".")) + "/mapred"));

    private static final Path inFile = new Path(TestSequenceFileInputFilter.inDir, "test.seq");

    private static final Random random = new Random(1);

    private static final Reporter reporter = NULL;

    static {
        FileInputFormat.setInputPaths(TestSequenceFileInputFilter.job, TestSequenceFileInputFilter.inDir);
        try {
            fs = FileSystem.getLocal(TestSequenceFileInputFilter.conf);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testRegexFilter() throws Exception {
        // set the filter class
        TestSequenceFileInputFilter.LOG.info("Testing Regex Filter with patter: \\A10*");
        SequenceFileInputFilter.setFilterClass(TestSequenceFileInputFilter.job, RegexFilter.class);
        RegexFilter.setPattern(TestSequenceFileInputFilter.job, "\\A10*");
        // clean input dir
        TestSequenceFileInputFilter.fs.delete(TestSequenceFileInputFilter.inDir, true);
        // for a variety of lengths
        for (int length = 1; length < (TestSequenceFileInputFilter.MAX_LENGTH); length += (TestSequenceFileInputFilter.random.nextInt(((TestSequenceFileInputFilter.MAX_LENGTH) / 10))) + 1) {
            TestSequenceFileInputFilter.LOG.info(("******Number of records: " + length));
            TestSequenceFileInputFilter.createSequenceFile(length);
            int count = countRecords(0);
            Assert.assertEquals(count, (length == 0 ? 0 : ((int) (Math.log10(length))) + 1));
        }
        // clean up
        TestSequenceFileInputFilter.fs.delete(TestSequenceFileInputFilter.inDir, true);
    }

    @Test
    public void testPercentFilter() throws Exception {
        TestSequenceFileInputFilter.LOG.info("Testing Percent Filter with frequency: 1000");
        // set the filter class
        SequenceFileInputFilter.setFilterClass(TestSequenceFileInputFilter.job, PercentFilter.class);
        PercentFilter.setFrequency(TestSequenceFileInputFilter.job, 1000);
        // clean input dir
        TestSequenceFileInputFilter.fs.delete(TestSequenceFileInputFilter.inDir, true);
        // for a variety of lengths
        for (int length = 0; length < (TestSequenceFileInputFilter.MAX_LENGTH); length += (TestSequenceFileInputFilter.random.nextInt(((TestSequenceFileInputFilter.MAX_LENGTH) / 10))) + 1) {
            TestSequenceFileInputFilter.LOG.info(("******Number of records: " + length));
            TestSequenceFileInputFilter.createSequenceFile(length);
            int count = countRecords(1);
            TestSequenceFileInputFilter.LOG.info((("Accepted " + count) + " records"));
            int expectedCount = length / 1000;
            if ((expectedCount * 1000) != length)
                expectedCount++;

            Assert.assertEquals(count, expectedCount);
        }
        // clean up
        TestSequenceFileInputFilter.fs.delete(TestSequenceFileInputFilter.inDir, true);
    }

    @Test
    public void testMD5Filter() throws Exception {
        // set the filter class
        TestSequenceFileInputFilter.LOG.info("Testing MD5 Filter with frequency: 1000");
        SequenceFileInputFilter.setFilterClass(TestSequenceFileInputFilter.job, MD5Filter.class);
        MD5Filter.setFrequency(TestSequenceFileInputFilter.job, 1000);
        // clean input dir
        TestSequenceFileInputFilter.fs.delete(TestSequenceFileInputFilter.inDir, true);
        // for a variety of lengths
        for (int length = 0; length < (TestSequenceFileInputFilter.MAX_LENGTH); length += (TestSequenceFileInputFilter.random.nextInt(((TestSequenceFileInputFilter.MAX_LENGTH) / 10))) + 1) {
            TestSequenceFileInputFilter.LOG.info(("******Number of records: " + length));
            TestSequenceFileInputFilter.createSequenceFile(length);
            TestSequenceFileInputFilter.LOG.info((("Accepted " + (countRecords(0))) + " records"));
        }
        // clean up
        TestSequenceFileInputFilter.fs.delete(TestSequenceFileInputFilter.inDir, true);
    }
}

