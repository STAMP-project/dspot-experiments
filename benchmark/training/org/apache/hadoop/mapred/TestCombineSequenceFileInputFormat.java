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
import java.util.BitSet;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.mapred.lib.CombineFileSplit;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static Reporter.NULL;


public class TestCombineSequenceFileInputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestCombineSequenceFileInputFormat.class);

    private static Configuration conf = new Configuration();

    private static FileSystem localFs = null;

    static {
        try {
            TestCombineSequenceFileInputFormat.conf.set("fs.defaultFS", "file:///");
            TestCombineSequenceFileInputFormat.localFs = FileSystem.getLocal(TestCombineSequenceFileInputFormat.conf);
        } catch (IOException e) {
            throw new RuntimeException("init failure", e);
        }
    }

    private static Path workDir = TestCombineSequenceFileInputFormat.localFs.makeQualified(new Path(System.getProperty("test.build.data", "/tmp"), "TestCombineSequenceFileInputFormat"));

    @Test(timeout = 10000)
    public void testFormat() throws Exception {
        JobConf job = new JobConf(TestCombineSequenceFileInputFormat.conf);
        Reporter reporter = NULL;
        Random random = new Random();
        long seed = random.nextLong();
        TestCombineSequenceFileInputFormat.LOG.info(("seed = " + seed));
        random.setSeed(seed);
        TestCombineSequenceFileInputFormat.localFs.delete(TestCombineSequenceFileInputFormat.workDir, true);
        FileInputFormat.setInputPaths(job, TestCombineSequenceFileInputFormat.workDir);
        final int length = 10000;
        final int numFiles = 10;
        // create a file with various lengths
        TestCombineSequenceFileInputFormat.createFiles(length, numFiles, random);
        // create a combine split for the files
        InputFormat<IntWritable, BytesWritable> format = new org.apache.hadoop.mapred.lib.CombineSequenceFileInputFormat<IntWritable, BytesWritable>();
        IntWritable key = new IntWritable();
        BytesWritable value = new BytesWritable();
        for (int i = 0; i < 3; i++) {
            int numSplits = (random.nextInt((length / ((SequenceFile.SYNC_INTERVAL) / 20)))) + 1;
            TestCombineSequenceFileInputFormat.LOG.info(("splitting: requesting = " + numSplits));
            InputSplit[] splits = format.getSplits(job, numSplits);
            TestCombineSequenceFileInputFormat.LOG.info(("splitting: got =        " + (splits.length)));
            // we should have a single split as the length is comfortably smaller than
            // the block size
            Assert.assertEquals("We got more than one splits!", 1, splits.length);
            InputSplit split = splits[0];
            Assert.assertEquals("It should be CombineFileSplit", CombineFileSplit.class, split.getClass());
            // check each split
            BitSet bits = new BitSet(length);
            RecordReader<IntWritable, BytesWritable> reader = format.getRecordReader(split, job, reporter);
            try {
                while (reader.next(key, value)) {
                    Assert.assertFalse("Key in multiple partitions.", bits.get(key.get()));
                    bits.set(key.get());
                } 
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
}

