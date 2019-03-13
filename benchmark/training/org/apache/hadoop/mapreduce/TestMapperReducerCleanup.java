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


import java.io.DataOutputStream;
import java.io.IOException;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.mapreduce.lib.reduce.IntSumReducer;
import org.junit.Assert;
import org.junit.Test;


public class TestMapperReducerCleanup {
    static boolean mapCleanup = false;

    static boolean reduceCleanup = false;

    static boolean recordReaderCleanup = false;

    static boolean recordWriterCleanup = false;

    private static class FailingMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
        /**
         * Map method with different behavior based on the thread id
         */
        public void map(LongWritable key, Text val, Context c) throws IOException, InterruptedException {
            throw new IOException("TestMapperReducerCleanup");
        }

        protected void cleanup(Context context) throws IOException, InterruptedException {
            TestMapperReducerCleanup.mapCleanup = true;
            super.cleanup(context);
        }
    }

    private static class TrackingTokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {
        private static final IntWritable one = new IntWritable(1);

        private Text word = new Text();

        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, TestMapperReducerCleanup.TrackingTokenizerMapper.one);
            } 
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        protected void cleanup(Mapper.Context context) throws IOException, InterruptedException {
            TestMapperReducerCleanup.mapCleanup = true;
            super.cleanup(context);
        }
    }

    private static class FailingReducer extends Reducer<LongWritable, Text, LongWritable, LongWritable> {
        public void reduce(LongWritable key, Iterable<Text> vals, Context context) throws IOException, InterruptedException {
            throw new IOException("TestMapperReducerCleanup");
        }

        protected void cleanup(Context context) throws IOException, InterruptedException {
            TestMapperReducerCleanup.reduceCleanup = true;
            super.cleanup(context);
        }
    }

    @SuppressWarnings("rawtypes")
    private static class TrackingIntSumReducer extends IntSumReducer {
        @SuppressWarnings("unchecked")
        protected void cleanup(Context context) throws IOException, InterruptedException {
            TestMapperReducerCleanup.reduceCleanup = true;
            super.cleanup(context);
        }
    }

    public static class TrackingTextInputFormat extends TextInputFormat {
        public static class TrackingRecordReader extends LineRecordReader {
            @Override
            public synchronized void close() throws IOException {
                TestMapperReducerCleanup.recordReaderCleanup = true;
                super.close();
            }
        }

        @Override
        public RecordReader<LongWritable, Text> createRecordReader(InputSplit split, TaskAttemptContext context) {
            return new TestMapperReducerCleanup.TrackingTextInputFormat.TrackingRecordReader();
        }
    }

    @SuppressWarnings("rawtypes")
    public static class TrackingTextOutputFormat extends TextOutputFormat {
        public static class TrackingRecordWriter extends LineRecordWriter {
            public TrackingRecordWriter(DataOutputStream out) {
                super(out);
            }

            @Override
            public synchronized void close(TaskAttemptContext context) throws IOException {
                TestMapperReducerCleanup.recordWriterCleanup = true;
                super.close(context);
            }
        }

        @Override
        public RecordWriter getRecordWriter(TaskAttemptContext job) throws IOException, InterruptedException {
            Configuration conf = job.getConfiguration();
            Path file = getDefaultWorkFile(job, "");
            FileSystem fs = file.getFileSystem(conf);
            FSDataOutputStream fileOut = fs.create(file, false);
            return new TestMapperReducerCleanup.TrackingTextOutputFormat.TrackingRecordWriter(fileOut);
        }
    }

    private final String INPUT_DIR = "input";

    private final String OUTPUT_DIR = "output";

    @Test
    public void testMapCleanup() throws Exception {
        TestMapperReducerCleanup.reset();
        Job job = Job.getInstance();
        Path inputPath = createInput();
        Path outputPath = getOutputPath();
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }
        job.setMapperClass(TestMapperReducerCleanup.FailingMapper.class);
        job.setInputFormatClass(TestMapperReducerCleanup.TrackingTextInputFormat.class);
        job.setOutputFormatClass(TestMapperReducerCleanup.TrackingTextOutputFormat.class);
        job.setNumReduceTasks(0);
        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        job.waitForCompletion(true);
        Assert.assertTrue(TestMapperReducerCleanup.mapCleanup);
        Assert.assertTrue(TestMapperReducerCleanup.recordReaderCleanup);
        Assert.assertTrue(TestMapperReducerCleanup.recordWriterCleanup);
    }

    @Test
    public void testReduceCleanup() throws Exception {
        TestMapperReducerCleanup.reset();
        Job job = Job.getInstance();
        Path inputPath = createInput();
        Path outputPath = getOutputPath();
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }
        job.setMapperClass(TestMapperReducerCleanup.TrackingTokenizerMapper.class);
        job.setReducerClass(TestMapperReducerCleanup.FailingReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(TestMapperReducerCleanup.TrackingTextInputFormat.class);
        job.setOutputFormatClass(TestMapperReducerCleanup.TrackingTextOutputFormat.class);
        job.setNumReduceTasks(1);
        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        job.waitForCompletion(true);
        Assert.assertTrue(TestMapperReducerCleanup.mapCleanup);
        Assert.assertTrue(TestMapperReducerCleanup.reduceCleanup);
        Assert.assertTrue(TestMapperReducerCleanup.recordReaderCleanup);
        Assert.assertTrue(TestMapperReducerCleanup.recordWriterCleanup);
    }

    @Test
    public void testJobSuccessCleanup() throws Exception {
        TestMapperReducerCleanup.reset();
        Job job = Job.getInstance();
        Path inputPath = createInput();
        Path outputPath = getOutputPath();
        Configuration conf = new Configuration();
        FileSystem fs = FileSystem.getLocal(conf);
        if (fs.exists(outputPath)) {
            fs.delete(outputPath, true);
        }
        job.setMapperClass(TestMapperReducerCleanup.TrackingTokenizerMapper.class);
        job.setReducerClass(TestMapperReducerCleanup.TrackingIntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        job.setInputFormatClass(TestMapperReducerCleanup.TrackingTextInputFormat.class);
        job.setOutputFormatClass(TestMapperReducerCleanup.TrackingTextOutputFormat.class);
        job.setNumReduceTasks(1);
        FileInputFormat.addInputPath(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);
        job.waitForCompletion(true);
        Assert.assertTrue(TestMapperReducerCleanup.mapCleanup);
        Assert.assertTrue(TestMapperReducerCleanup.reduceCleanup);
        Assert.assertTrue(TestMapperReducerCleanup.recordReaderCleanup);
        Assert.assertTrue(TestMapperReducerCleanup.recordWriterCleanup);
        Assert.assertNotNull(job.getCluster());
        job.close();
        Assert.assertNull(job.getCluster());
    }
}

