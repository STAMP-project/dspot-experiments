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
package org.apache.hadoop.mapred.lib;


import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.junit.Test;


public class TestMultipleOutputs extends HadoopTestCase {
    public TestMultipleOutputs() throws IOException {
        super(HadoopTestCase.LOCAL_MR, HadoopTestCase.LOCAL_FS, 1, 1);
    }

    @Test
    public void testWithoutCounters() throws Exception {
        _testMultipleOutputs(false);
        _testMOWithJavaSerialization(false);
    }

    @Test
    public void testWithCounters() throws Exception {
        _testMultipleOutputs(true);
        _testMOWithJavaSerialization(true);
    }

    private static final Path ROOT_DIR = new Path("testing/mo");

    private static final Path IN_DIR = new Path(TestMultipleOutputs.ROOT_DIR, "input");

    private static final Path OUT_DIR = new Path(TestMultipleOutputs.ROOT_DIR, "output");

    @SuppressWarnings({ "unchecked" })
    public static class MOMap implements Mapper<LongWritable, Text, LongWritable, Text> {
        private MultipleOutputs mos;

        public void configure(JobConf conf) {
            mos = new MultipleOutputs(conf);
        }

        public void map(LongWritable key, Text value, OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
            if (!(value.toString().equals("a"))) {
                output.collect(key, value);
            } else {
                mos.getCollector("text", reporter).collect(key, new Text("text"));
                mos.getCollector("sequence", "A", reporter).collect(key, new Text("sequence"));
                mos.getCollector("sequence", "B", reporter).collect(key, new Text("sequence"));
            }
        }

        public void close() throws IOException {
            mos.close();
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static class MOReduce implements Reducer<LongWritable, Text, LongWritable, Text> {
        private MultipleOutputs mos;

        public void configure(JobConf conf) {
            mos = new MultipleOutputs(conf);
        }

        public void reduce(LongWritable key, Iterator<Text> values, OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
            while (values.hasNext()) {
                Text value = values.next();
                if (!(value.toString().equals("b"))) {
                    output.collect(key, value);
                } else {
                    mos.getCollector("text", reporter).collect(key, new Text("text"));
                    mos.getCollector("sequence", "B", reporter).collect(key, new Text("sequence"));
                    mos.getCollector("sequence", "C", reporter).collect(key, new Text("sequence"));
                }
            } 
        }

        public void close() throws IOException {
            mos.close();
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static class MOJavaSerDeMap implements Mapper<LongWritable, Text, Long, String> {
        private MultipleOutputs mos;

        public void configure(JobConf conf) {
            mos = new MultipleOutputs(conf);
        }

        public void map(LongWritable key, Text value, OutputCollector<Long, String> output, Reporter reporter) throws IOException {
            if (!(value.toString().equals("a"))) {
                output.collect(key.get(), value.toString());
            } else {
                mos.getCollector("text", reporter).collect(key, "text");
            }
        }

        public void close() throws IOException {
            mos.close();
        }
    }

    @SuppressWarnings({ "unchecked" })
    public static class MOJavaSerDeReduce implements Reducer<Long, String, Long, String> {
        private MultipleOutputs mos;

        public void configure(JobConf conf) {
            mos = new MultipleOutputs(conf);
        }

        public void reduce(Long key, Iterator<String> values, OutputCollector<Long, String> output, Reporter reporter) throws IOException {
            while (values.hasNext()) {
                String value = values.next();
                if (!(value.equals("b"))) {
                    output.collect(key, value);
                } else {
                    mos.getCollector("text", reporter).collect(key, "text");
                }
            } 
        }

        public void close() throws IOException {
            mos.close();
        }
    }
}

