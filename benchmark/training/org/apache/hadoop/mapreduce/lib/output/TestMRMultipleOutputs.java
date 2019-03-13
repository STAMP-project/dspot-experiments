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
package org.apache.hadoop.mapreduce.lib.output;


import java.io.IOException;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.junit.Test;


public class TestMRMultipleOutputs extends HadoopTestCase {
    public TestMRMultipleOutputs() throws IOException {
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

    private static String localPathRoot = System.getProperty("test.build.data", "/tmp");

    private static final Path ROOT_DIR = new Path(TestMRMultipleOutputs.localPathRoot, "testing/mo");

    private static final Path IN_DIR = new Path(TestMRMultipleOutputs.ROOT_DIR, "input");

    private static final Path OUT_DIR = new Path(TestMRMultipleOutputs.ROOT_DIR, "output");

    private static String TEXT = "text";

    private static String SEQUENCE = "sequence";

    @SuppressWarnings("unchecked")
    public static class MOMap extends Mapper<LongWritable, Text, LongWritable, Text> {
        private MultipleOutputs mos;

        public void setup(Context context) {
            mos = new MultipleOutputs(context);
        }

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            context.write(key, value);
            if (value.toString().equals("a")) {
                mos.write(TestMRMultipleOutputs.TEXT, key, new Text(TestMRMultipleOutputs.TEXT));
                mos.write(TestMRMultipleOutputs.SEQUENCE, new IntWritable(1), new Text(TestMRMultipleOutputs.SEQUENCE), ((TestMRMultipleOutputs.SEQUENCE) + "_A"));
                mos.write(TestMRMultipleOutputs.SEQUENCE, new IntWritable(2), new Text(TestMRMultipleOutputs.SEQUENCE), ((TestMRMultipleOutputs.SEQUENCE) + "_B"));
            }
        }

        public void cleanup(Context context) throws IOException, InterruptedException {
            mos.close();
        }
    }

    @SuppressWarnings("unchecked")
    public static class MOReduce extends Reducer<LongWritable, Text, LongWritable, Text> {
        private MultipleOutputs mos;

        public void setup(Context context) {
            mos = new MultipleOutputs(context);
        }

        public void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            for (Text value : values) {
                mos.write(key, value, value.toString());
                if (!(value.toString().equals("b"))) {
                    context.write(key, value);
                } else {
                    mos.write(TestMRMultipleOutputs.TEXT, key, new Text(TestMRMultipleOutputs.TEXT));
                    mos.write(TestMRMultipleOutputs.SEQUENCE, new IntWritable(2), new Text(TestMRMultipleOutputs.SEQUENCE), ((TestMRMultipleOutputs.SEQUENCE) + "_B"));
                    mos.write(TestMRMultipleOutputs.SEQUENCE, new IntWritable(3), new Text(TestMRMultipleOutputs.SEQUENCE), ((TestMRMultipleOutputs.SEQUENCE) + "_C"));
                }
            }
        }

        public void cleanup(Context context) throws IOException, InterruptedException {
            mos.close();
        }
    }

    public static class MOJavaSerDeMap extends Mapper<LongWritable, Text, Long, String> {
        private MultipleOutputs<Long, String> mos;

        public void setup(Context context) {
            mos = new MultipleOutputs<Long, String>(context);
        }

        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            context.write(key.get(), value.toString());
            if (value.toString().equals("a")) {
                mos.write(TestMRMultipleOutputs.TEXT, key.get(), TestMRMultipleOutputs.TEXT);
            }
        }

        public void cleanup(Context context) throws IOException, InterruptedException {
            mos.close();
        }
    }

    public static class MOJavaSerDeReduce extends Reducer<Long, String, Long, String> {
        private MultipleOutputs<Long, String> mos;

        public void setup(Context context) {
            mos = new MultipleOutputs<Long, String>(context);
        }

        public void reduce(Long key, Iterable<String> values, Context context) throws IOException, InterruptedException {
            for (String value : values) {
                mos.write(key, value, value.toString());
                if (!(value.toString().equals("b"))) {
                    context.write(key, value);
                } else {
                    mos.write(TestMRMultipleOutputs.TEXT, key, new Text(TestMRMultipleOutputs.TEXT));
                }
            }
        }

        public void cleanup(Context context) throws IOException, InterruptedException {
            mos.close();
        }
    }
}

