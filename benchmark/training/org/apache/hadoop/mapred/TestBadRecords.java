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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Ignore
public class TestBadRecords extends ClusterMapReduceTestCase {
    private static final Logger LOG = LoggerFactory.getLogger(TestBadRecords.class);

    private static final List<String> MAPPER_BAD_RECORDS = Arrays.asList("hello01", "hello04", "hello05");

    private static final List<String> REDUCER_BAD_RECORDS = Arrays.asList("hello08", "hello10");

    private List<String> input;

    public TestBadRecords() {
        input = new ArrayList<String>();
        for (int i = 1; i <= 10; i++) {
            String str = "" + i;
            int zerosToPrepend = 2 - (str.length());
            for (int j = 0; j < zerosToPrepend; j++) {
                str = "0" + str;
            }
            input.add(("hello" + str));
        }
    }

    @Test
    public void testBadMapRed() throws Exception {
        JobConf conf = createJobConf();
        conf.setMapperClass(TestBadRecords.BadMapper.class);
        conf.setReducerClass(TestBadRecords.BadReducer.class);
        runMapReduce(conf, TestBadRecords.MAPPER_BAD_RECORDS, TestBadRecords.REDUCER_BAD_RECORDS);
    }

    static class BadMapper extends MapReduceBase implements Mapper<LongWritable, Text, LongWritable, Text> {
        public void map(LongWritable key, Text val, OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
            String str = val.toString();
            TestBadRecords.LOG.debug(((("MAP key:" + key) + "  value:") + str));
            if (TestBadRecords.MAPPER_BAD_RECORDS.get(0).equals(str)) {
                TestBadRecords.LOG.warn("MAP Encountered BAD record");
                System.exit((-1));
            } else
                if (TestBadRecords.MAPPER_BAD_RECORDS.get(1).equals(str)) {
                    TestBadRecords.LOG.warn("MAP Encountered BAD record");
                    throw new RuntimeException(("Bad record " + str));
                } else
                    if (TestBadRecords.MAPPER_BAD_RECORDS.get(2).equals(str)) {
                        try {
                            TestBadRecords.LOG.warn("MAP Encountered BAD record");
                            Thread.sleep(((15 * 60) * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


            output.collect(key, val);
        }
    }

    static class BadReducer extends MapReduceBase implements Reducer<LongWritable, Text, LongWritable, Text> {
        public void reduce(LongWritable key, Iterator<Text> values, OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
            while (values.hasNext()) {
                Text value = values.next();
                TestBadRecords.LOG.debug(((("REDUCE key:" + key) + "  value:") + value));
                if (TestBadRecords.REDUCER_BAD_RECORDS.get(0).equals(value.toString())) {
                    TestBadRecords.LOG.warn("REDUCE Encountered BAD record");
                    System.exit((-1));
                } else
                    if (TestBadRecords.REDUCER_BAD_RECORDS.get(1).equals(value.toString())) {
                        try {
                            TestBadRecords.LOG.warn("REDUCE Encountered BAD record");
                            Thread.sleep(((15 * 60) * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                output.collect(key, value);
            } 
        }
    }
}

