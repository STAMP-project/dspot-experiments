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
import java.util.HashMap;
import java.util.Random;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMultiFileInputFormat {
    private static JobConf job = new JobConf();

    private static final Logger LOG = LoggerFactory.getLogger(TestMultiFileInputFormat.class);

    private static final int MAX_SPLIT_COUNT = 10000;

    private static final int SPLIT_COUNT_INCR = 6000;

    private static final int MAX_BYTES = 1024;

    private static final int MAX_NUM_FILES = 10000;

    private static final int NUM_FILES_INCR = 8000;

    private Random rand = new Random(System.currentTimeMillis());

    private HashMap<String, Long> lengths = new HashMap<String, Long>();

    /**
     * Dummy class to extend MultiFileInputFormat
     */
    private class DummyMultiFileInputFormat extends MultiFileInputFormat<Text, Text> {
        @Override
        public RecordReader<Text, Text> getRecordReader(InputSplit split, JobConf job, Reporter reporter) throws IOException {
            return null;
        }
    }

    @Test
    public void testFormat() throws IOException {
        TestMultiFileInputFormat.LOG.info("Test started");
        TestMultiFileInputFormat.LOG.info(("Max split count           = " + (TestMultiFileInputFormat.MAX_SPLIT_COUNT)));
        TestMultiFileInputFormat.LOG.info(("Split count increment     = " + (TestMultiFileInputFormat.SPLIT_COUNT_INCR)));
        TestMultiFileInputFormat.LOG.info(("Max bytes per file        = " + (TestMultiFileInputFormat.MAX_BYTES)));
        TestMultiFileInputFormat.LOG.info(("Max number of files       = " + (TestMultiFileInputFormat.MAX_NUM_FILES)));
        TestMultiFileInputFormat.LOG.info(("Number of files increment = " + (TestMultiFileInputFormat.NUM_FILES_INCR)));
        MultiFileInputFormat<Text, Text> format = new TestMultiFileInputFormat.DummyMultiFileInputFormat();
        FileSystem fs = FileSystem.getLocal(TestMultiFileInputFormat.job);
        for (int numFiles = 1; numFiles < (TestMultiFileInputFormat.MAX_NUM_FILES); numFiles += ((TestMultiFileInputFormat.NUM_FILES_INCR) / 2) + (rand.nextInt(((TestMultiFileInputFormat.NUM_FILES_INCR) / 2)))) {
            Path dir = initFiles(fs, numFiles, (-1));
            BitSet bits = new BitSet(numFiles);
            for (int i = 1; i < (TestMultiFileInputFormat.MAX_SPLIT_COUNT); i += (rand.nextInt(TestMultiFileInputFormat.SPLIT_COUNT_INCR)) + 1) {
                TestMultiFileInputFormat.LOG.info(((("Running for Num Files=" + numFiles) + ", split count=") + i));
                MultiFileSplit[] splits = ((MultiFileSplit[]) (format.getSplits(TestMultiFileInputFormat.job, i)));
                bits.clear();
                for (MultiFileSplit split : splits) {
                    long splitLength = 0;
                    for (Path p : split.getPaths()) {
                        long length = fs.getContentSummary(p).getLength();
                        Assert.assertEquals(length, lengths.get(p.getName()).longValue());
                        splitLength += length;
                        String name = p.getName();
                        int index = Integer.parseInt(name.substring(((name.lastIndexOf("file_")) + 5)));
                        Assert.assertFalse(bits.get(index));
                        bits.set(index);
                    }
                    Assert.assertEquals(splitLength, split.getLength());
                }
            }
            Assert.assertEquals(bits.cardinality(), numFiles);
            fs.delete(dir, true);
        }
        TestMultiFileInputFormat.LOG.info("Test Finished");
    }

    @Test
    public void testFormatWithLessPathsThanSplits() throws Exception {
        MultiFileInputFormat<Text, Text> format = new TestMultiFileInputFormat.DummyMultiFileInputFormat();
        FileSystem fs = FileSystem.getLocal(TestMultiFileInputFormat.job);
        // Test with no path
        initFiles(fs, 0, (-1));
        Assert.assertEquals(0, format.getSplits(TestMultiFileInputFormat.job, 2).length);
        // Test with 2 path and 4 splits
        initFiles(fs, 2, 500);
        Assert.assertEquals(2, format.getSplits(TestMultiFileInputFormat.job, 4).length);
    }
}

