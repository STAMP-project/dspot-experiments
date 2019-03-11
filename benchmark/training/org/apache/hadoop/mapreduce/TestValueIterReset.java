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
package org.apache.hadoop.mapreduce;


import MRJobConfig.REDUCE_MARKRESET_BUFFER_SIZE;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A JUnit test to test the Map-Reduce framework's support for the
 * "mark-reset" functionality in Reduce Values Iterator
 */
public class TestValueIterReset {
    private static final int NUM_MAPS = 1;

    private static final int NUM_TESTS = 4;

    private static final int NUM_VALUES = 40;

    private static Path TEST_ROOT_DIR = new Path(System.getProperty("test.build.data", "/tmp"));

    private static Configuration conf = new Configuration();

    private static FileSystem localFs;

    static {
        try {
            TestValueIterReset.localFs = FileSystem.getLocal(TestValueIterReset.conf);
        } catch (IOException io) {
            throw new RuntimeException("problem getting local fs", io);
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger(TestValueIterReset.class);

    public static class TestMapper extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            IntWritable outKey = new IntWritable();
            IntWritable outValue = new IntWritable();
            for (int j = 0; j < (TestValueIterReset.NUM_TESTS); j++) {
                for (int i = 0; i < (TestValueIterReset.NUM_VALUES); i++) {
                    outKey.set(j);
                    outValue.set(i);
                    context.write(outKey, outValue);
                }
            }
        }
    }

    public static class TestReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int errors = 0;
            MarkableIterator<IntWritable> mitr = new MarkableIterator<IntWritable>(values.iterator());
            switch (key.get()) {
                case 0 :
                    errors += TestValueIterReset.test0(key, mitr);
                    break;
                case 1 :
                    errors += TestValueIterReset.test1(key, mitr);
                    break;
                case 2 :
                    errors += TestValueIterReset.test2(key, mitr);
                    break;
                case 3 :
                    errors += TestValueIterReset.test3(key, mitr);
                    break;
                default :
                    break;
            }
            context.write(key, new IntWritable(errors));
        }
    }

    @Test
    public void testValueIterReset() {
        try {
            Configuration conf = new Configuration();
            Job job = Job.getInstance(conf, "TestValueIterReset");
            job.setJarByClass(TestValueIterReset.class);
            job.setMapperClass(TestValueIterReset.TestMapper.class);
            job.setReducerClass(TestValueIterReset.TestReducer.class);
            job.setNumReduceTasks(TestValueIterReset.NUM_TESTS);
            job.setMapOutputKeyClass(IntWritable.class);
            job.setMapOutputValueClass(IntWritable.class);
            job.setOutputKeyClass(IntWritable.class);
            job.setOutputValueClass(IntWritable.class);
            job.getConfiguration().setInt(REDUCE_MARKRESET_BUFFER_SIZE, 128);
            job.setInputFormatClass(TextInputFormat.class);
            job.setOutputFormatClass(TextOutputFormat.class);
            FileInputFormat.addInputPath(job, new Path(((TestValueIterReset.TEST_ROOT_DIR) + "/in")));
            Path output = new Path(((TestValueIterReset.TEST_ROOT_DIR) + "/out"));
            TestValueIterReset.localFs.delete(output, true);
            FileOutputFormat.setOutputPath(job, output);
            createInput();
            Assert.assertTrue(job.waitForCompletion(true));
            validateOutput();
        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertTrue(false);
        }
    }
}

