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
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.hive.hcatalog.mapreduce;


import SequenceFile.Reader;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MiniMRCluster;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.SequenceFileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hive.hcatalog.mapreduce.MultiOutputFormat.JobConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMultiOutputFormat {
    private static final Logger LOG = LoggerFactory.getLogger(TestMultiOutputFormat.class);

    private static File workDir;

    private static JobConf mrConf = null;

    private static FileSystem fs = null;

    private static MiniMRCluster mrCluster = null;

    /**
     * A test job that reads a input file and outputs each word and the index of
     * the word encountered to a text file and sequence file with different key
     * values.
     */
    @Test
    public void testMultiOutputFormatWithoutReduce() throws Throwable {
        Job job = new Job(TestMultiOutputFormat.mrConf, "MultiOutNoReduce");
        job.setMapperClass(TestMultiOutputFormat.MultiOutWordIndexMapper.class);
        job.setJarByClass(this.getClass());
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(MultiOutputFormat.class);
        job.setNumReduceTasks(0);
        JobConfigurer configurer = MultiOutputFormat.createConfigurer(job);
        configurer.addOutputFormat("out1", TextOutputFormat.class, IntWritable.class, Text.class);
        configurer.addOutputFormat("out2", SequenceFileOutputFormat.class, Text.class, IntWritable.class);
        Path outDir = new Path(TestMultiOutputFormat.workDir.getPath(), job.getJobName());
        FileOutputFormat.setOutputPath(configurer.getJob("out1"), new Path(outDir, "out1"));
        FileOutputFormat.setOutputPath(configurer.getJob("out2"), new Path(outDir, "out2"));
        String fileContent = "Hello World";
        String inputFile = createInputFile(fileContent);
        FileInputFormat.setInputPaths(job, new Path(inputFile));
        // Test for merging of configs
        DistributedCache.addFileToClassPath(new Path(inputFile), job.getConfiguration(), TestMultiOutputFormat.fs);
        String dummyFile = createInputFile("dummy file");
        DistributedCache.addFileToClassPath(new Path(dummyFile), configurer.getJob("out1").getConfiguration(), TestMultiOutputFormat.fs);
        // duplicate of the value. Merging should remove duplicates
        DistributedCache.addFileToClassPath(new Path(inputFile), configurer.getJob("out2").getConfiguration(), TestMultiOutputFormat.fs);
        configurer.configure();
        // Verify if the configs are merged
        Path[] fileClassPaths = DistributedCache.getFileClassPaths(job.getConfiguration());
        List<Path> fileClassPathsList = Arrays.asList(fileClassPaths);
        Assert.assertTrue(((("Cannot find " + (new Path(inputFile))) + " in ") + fileClassPathsList), fileClassPathsList.contains(new Path(inputFile)));
        Assert.assertTrue(((("Cannot find " + (new Path(dummyFile))) + " in ") + fileClassPathsList), fileClassPathsList.contains(new Path(dummyFile)));
        URI[] cacheFiles = DistributedCache.getCacheFiles(job.getConfiguration());
        List<URI> cacheFilesList = Arrays.asList(cacheFiles);
        URI inputFileURI = new Path(inputFile).makeQualified(TestMultiOutputFormat.fs).toUri();
        Assert.assertTrue(((("Cannot find " + inputFileURI) + " in ") + cacheFilesList), cacheFilesList.contains(inputFileURI));
        URI dummyFileURI = new Path(dummyFile).makeQualified(TestMultiOutputFormat.fs).toUri();
        Assert.assertTrue(((("Cannot find " + dummyFileURI) + " in ") + cacheFilesList), cacheFilesList.contains(dummyFileURI));
        Assert.assertTrue(job.waitForCompletion(true));
        Path textOutPath = new Path(outDir, "out1/part-m-00000");
        String[] textOutput = readFully(textOutPath).split("\n");
        Path seqOutPath = new Path(outDir, "out2/part-m-00000");
        SequenceFile.Reader reader = new SequenceFile.Reader(TestMultiOutputFormat.fs, seqOutPath, TestMultiOutputFormat.mrConf);
        Text key = new Text();
        IntWritable value = new IntWritable();
        String[] words = fileContent.split(" ");
        Assert.assertEquals(words.length, textOutput.length);
        TestMultiOutputFormat.LOG.info("Verifying file contents");
        for (int i = 0; i < (words.length); i++) {
            Assert.assertEquals((((i + 1) + "\t") + (words[i])), textOutput[i]);
            reader.next(key, value);
            Assert.assertEquals(words[i], key.toString());
            Assert.assertEquals((i + 1), value.get());
        }
        Assert.assertFalse(reader.next(key, value));
    }

    /**
     * A word count test job that reads a input file and outputs the count of
     * words to a text file and sequence file with different key values.
     */
    @Test
    public void testMultiOutputFormatWithReduce() throws Throwable {
        Job job = new Job(TestMultiOutputFormat.mrConf, "MultiOutWithReduce");
        job.setMapperClass(TestMultiOutputFormat.WordCountMapper.class);
        job.setReducerClass(TestMultiOutputFormat.MultiOutWordCountReducer.class);
        job.setJarByClass(this.getClass());
        job.setInputFormatClass(TextInputFormat.class);
        job.setOutputFormatClass(MultiOutputFormat.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        JobConfigurer configurer = MultiOutputFormat.createConfigurer(job);
        configurer.addOutputFormat("out1", TextOutputFormat.class, IntWritable.class, Text.class);
        configurer.addOutputFormat("out2", SequenceFileOutputFormat.class, Text.class, IntWritable.class);
        configurer.addOutputFormat("out3", TestMultiOutputFormat.NullOutputFormat.class, Text.class, IntWritable.class);
        Path outDir = new Path(TestMultiOutputFormat.workDir.getPath(), job.getJobName());
        FileOutputFormat.setOutputPath(configurer.getJob("out1"), new Path(outDir, "out1"));
        FileOutputFormat.setOutputPath(configurer.getJob("out2"), new Path(outDir, "out2"));
        configurer.configure();
        String fileContent = "Hello World Hello World World";
        String inputFile = createInputFile(fileContent);
        FileInputFormat.setInputPaths(job, new Path(inputFile));
        Assert.assertTrue(job.waitForCompletion(true));
        Path textOutPath = new Path(outDir, "out1/part-r-00000");
        String[] textOutput = readFully(textOutPath).split("\n");
        Path seqOutPath = new Path(outDir, "out2/part-r-00000");
        SequenceFile.Reader reader = new SequenceFile.Reader(TestMultiOutputFormat.fs, seqOutPath, TestMultiOutputFormat.mrConf);
        Text key = new Text();
        IntWritable value = new IntWritable();
        String[] words = "Hello World".split(" ");
        Assert.assertEquals(words.length, textOutput.length);
        for (int i = 0; i < (words.length); i++) {
            Assert.assertEquals((((i + 2) + "\t") + (words[i])), textOutput[i]);
            reader.next(key, value);
            Assert.assertEquals(words[i], key.toString());
            Assert.assertEquals((i + 2), value.get());
        }
        Assert.assertFalse(reader.next(key, value));
    }

    private static class MultiOutWordIndexMapper extends Mapper<LongWritable, Text, Writable, Writable> {
        private IntWritable index = new IntWritable(1);

        private Text word = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                MultiOutputFormat.write("out1", index, word, context);
                MultiOutputFormat.write("out2", word, index, context);
                index.set(((index.get()) + 1));
            } 
        }
    }

    private static class WordCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private static final IntWritable one = new IntWritable(1);

        private Text word = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            StringTokenizer itr = new StringTokenizer(value.toString());
            while (itr.hasMoreTokens()) {
                word.set(itr.nextToken());
                context.write(word, TestMultiOutputFormat.WordCountMapper.one);
            } 
        }
    }

    private static class MultiOutWordCountReducer extends Reducer<Text, IntWritable, Writable, Writable> {
        private IntWritable count = new IntWritable();

        @Override
        protected void reduce(Text word, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            count.set(sum);
            MultiOutputFormat.write("out1", count, word, context);
            MultiOutputFormat.write("out2", word, count, context);
            MultiOutputFormat.write("out3", word, count, context);
        }
    }

    private static class NullOutputFormat<K, V> extends org.apache.hadoop.mapreduce.lib.output.NullOutputFormat<K, V> {
        @Override
        public OutputCommitter getOutputCommitter(TaskAttemptContext context) {
            return new OutputCommitter() {
                public void abortTask(TaskAttemptContext taskContext) {
                }

                public void cleanupJob(JobContext jobContext) {
                }

                public void commitJob(JobContext jobContext) {
                }

                public void commitTask(TaskAttemptContext taskContext) {
                    Assert.fail("needsTaskCommit is false but commitTask was called");
                }

                public boolean needsTaskCommit(TaskAttemptContext taskContext) {
                    return false;
                }

                public void setupJob(JobContext jobContext) {
                }

                public void setupTask(TaskAttemptContext taskContext) {
                }
            };
        }
    }
}

