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
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.junit.Test;


/**
 * This test tests the support for a merge operation in Hadoop.  The input files
 * are already sorted on the key.  This test implements an external
 * MapOutputCollector implementation that just copies the records to different
 * partitions while maintaining the sort order in each partition.  The Hadoop
 * framework's merge on the reduce side will merge the partitions created to
 * generate the final output which is sorted on the key.
 */
@SuppressWarnings({ "unchecked", "deprecation" })
public class TestMRIntermediateDataEncryption {
    // Where MR job's input will reside.
    private static final Path INPUT_DIR = new Path("/test/input");

    // Where output goes.
    private static final Path OUTPUT = new Path("/test/output");

    @Test
    public void testSingleReducer() throws Exception {
        doEncryptionTest(3, 1, 2, false);
    }

    @Test
    public void testUberMode() throws Exception {
        doEncryptionTest(3, 1, 2, true);
    }

    @Test
    public void testMultipleMapsPerNode() throws Exception {
        doEncryptionTest(8, 1, 2, false);
    }

    @Test
    public void testMultipleReducers() throws Exception {
        doEncryptionTest(2, 4, 2, false);
    }

    /**
     * A mapper implementation that assumes that key text contains valid integers
     * in displayable form.
     */
    public static class MyMapper extends MapReduceBase implements Mapper<LongWritable, Text, Text, Text> {
        private Text keyText;

        private Text valueText;

        public MyMapper() {
            keyText = new Text();
            valueText = new Text();
        }

        @Override
        public void map(LongWritable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            String record = value.toString();
            int blankPos = record.indexOf(" ");
            keyText.set(record.substring(0, blankPos));
            valueText.set(record.substring((blankPos + 1)));
            output.collect(keyText, valueText);
        }

        public void close() throws IOException {
        }
    }

    /**
     * Partitioner implementation to make sure that output is in total sorted
     * order.  We basically route key ranges to different reducers such that
     * key values monotonically increase with the partition number.  For example,
     * in this test, the keys are numbers from 1 to 1000 in the form "000000001"
     * to "000001000" in each input file.  The keys "000000001" to "000000250" are
     * routed to partition 0, "000000251" to "000000500" are routed to partition 1
     * and so on since we have 4 reducers.
     */
    static class MyPartitioner implements Partitioner<Text, Text> {
        private JobConf job;

        public MyPartitioner() {
        }

        public void configure(JobConf job) {
            this.job = job;
        }

        public int getPartition(Text key, Text value, int numPartitions) {
            int keyValue = 0;
            try {
                keyValue = Integer.parseInt(key.toString());
            } catch (NumberFormatException nfe) {
                keyValue = 0;
            }
            int partitionNumber = (numPartitions * (Math.max(0, (keyValue - 1)))) / (job.getInt("mapred.test.num_lines", 10000));
            return partitionNumber;
        }
    }
}

