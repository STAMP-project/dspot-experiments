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


import MRConfig.FRAMEWORK_NAME;
import MRConfig.LOCAL_FRAMEWORK_NAME;
import SequenceFile.CompressionType.NONE;
import SequenceFile.Reader;
import SequenceFile.Writer;
import java.io.File;
import java.io.IOException;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.SequenceFile.CompressionType;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapred.lib.IdentityReducer;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.Tool;
import org.junit.Assert;
import org.junit.Test;


/**
 * ********************************************************
 * MapredLoadTest generates a bunch of work that exercises
 * a Hadoop Map-Reduce system (and DFS, too).  It goes through
 * the following steps:
 *
 * 1) Take inputs 'range' and 'counts'.
 * 2) Generate 'counts' random integers between 0 and range-1.
 * 3) Create a file that lists each integer between 0 and range-1,
 *    and lists the number of times that integer was generated.
 * 4) Emit a (very large) file that contains all the integers
 *    in the order generated.
 * 5) After the file has been generated, read it back and count
 *    how many times each int was generated.
 * 6) Compare this big count-map against the original one.  If
 *    they match, then SUCCESS!  Otherwise, FAILURE!
 *
 * OK, that's how we can think about it.  What are the map-reduce
 * steps that get the job done?
 *
 * 1) In a non-mapred thread, take the inputs 'range' and 'counts'.
 * 2) In a non-mapread thread, generate the answer-key and write to disk.
 * 3) In a mapred job, divide the answer key into K jobs.
 * 4) A mapred 'generator' task consists of K map jobs.  Each reads
 *    an individual "sub-key", and generates integers according to
 *    to it (though with a random ordering).
 * 5) The generator's reduce task agglomerates all of those files
 *    into a single one.
 * 6) A mapred 'reader' task consists of M map jobs.  The output
 *    file is cut into M pieces. Each of the M jobs counts the
 *    individual ints in its chunk and creates a map of all seen ints.
 * 7) A mapred job integrates all the count files into a single one.
 *
 * ********************************************************
 */
public class TestMapRed extends Configured implements Tool {
    /**
     * Modified to make it a junit test.
     * The RandomGen Job does the actual work of creating
     * a huge file of assorted numbers.  It receives instructions
     * as to how many times each number should be counted.  Then
     * it emits those numbers in a crazy order.
     *
     * The map() function takes a key/val pair that describes
     * a value-to-be-emitted (the key) and how many times it
     * should be emitted (the value), aka "numtimes".  map() then
     * emits a series of intermediate key/val pairs.  It emits
     * 'numtimes' of these.  The key is a random number and the
     * value is the 'value-to-be-emitted'.
     *
     * The system collates and merges these pairs according to
     * the random number.  reduce() function takes in a key/value
     * pair that consists of a crazy random number and a series
     * of values that should be emitted.  The random number key
     * is now dropped, and reduce() emits a pair for every intermediate value.
     * The emitted key is an intermediate value.  The emitted value
     * is just a blank string.  Thus, we've created a huge file
     * of numbers in random order, but where each number appears
     * as many times as we were instructed.
     */
    private static final File TEST_DIR = new File(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), "TestMapRed-mapred");

    static class RandomGenMapper implements Mapper<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void configure(JobConf job) {
        }

        public void map(IntWritable key, IntWritable val, OutputCollector<IntWritable, IntWritable> out, Reporter reporter) throws IOException {
            int randomVal = key.get();
            int randomCount = val.get();
            for (int i = 0; i < randomCount; i++) {
                out.collect(new IntWritable(Math.abs(TestMapRed.r.nextInt())), new IntWritable(randomVal));
            }
        }

        public void close() {
        }
    }

    /**
     *
     */
    static class RandomGenReducer implements Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void configure(JobConf job) {
        }

        public void reduce(IntWritable key, Iterator<IntWritable> it, OutputCollector<IntWritable, IntWritable> out, Reporter reporter) throws IOException {
            while (it.hasNext()) {
                out.collect(it.next(), null);
            } 
        }

        public void close() {
        }
    }

    /**
     * The RandomCheck Job does a lot of our work.  It takes
     * in a num/string keyspace, and transforms it into a
     * key/count(int) keyspace.
     *
     * The map() function just emits a num/1 pair for every
     * num/string input pair.
     *
     * The reduce() function sums up all the 1s that were
     * emitted for a single key.  It then emits the key/total
     * pair.
     *
     * This is used to regenerate the random number "answer key".
     * Each key here is a random number, and the count is the
     * number of times the number was emitted.
     */
    static class RandomCheckMapper implements Mapper<WritableComparable, Text, IntWritable, IntWritable> {
        public void configure(JobConf job) {
        }

        public void map(WritableComparable key, Text val, OutputCollector<IntWritable, IntWritable> out, Reporter reporter) throws IOException {
            out.collect(new IntWritable(Integer.parseInt(val.toString().trim())), new IntWritable(1));
        }

        public void close() {
        }
    }

    /**
     *
     */
    static class RandomCheckReducer implements Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void configure(JobConf job) {
        }

        public void reduce(IntWritable key, Iterator<IntWritable> it, OutputCollector<IntWritable, IntWritable> out, Reporter reporter) throws IOException {
            int keyint = key.get();
            int count = 0;
            while (it.hasNext()) {
                it.next();
                count++;
            } 
            out.collect(new IntWritable(keyint), new IntWritable(count));
        }

        public void close() {
        }
    }

    /**
     * The Merge Job is a really simple one.  It takes in
     * an int/int key-value set, and emits the same set.
     * But it merges identical keys by adding their values.
     *
     * Thus, the map() function is just the identity function
     * and reduce() just sums.  Nothing to see here!
     */
    static class MergeMapper implements Mapper<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void configure(JobConf job) {
        }

        public void map(IntWritable key, IntWritable val, OutputCollector<IntWritable, IntWritable> out, Reporter reporter) throws IOException {
            int keyint = key.get();
            int valint = val.get();
            out.collect(new IntWritable(keyint), new IntWritable(valint));
        }

        public void close() {
        }
    }

    static class MergeReducer implements Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void configure(JobConf job) {
        }

        public void reduce(IntWritable key, Iterator<IntWritable> it, OutputCollector<IntWritable, IntWritable> out, Reporter reporter) throws IOException {
            int keyint = key.get();
            int total = 0;
            while (it.hasNext()) {
                total += it.next().get();
            } 
            out.collect(new IntWritable(keyint), new IntWritable(total));
        }

        public void close() {
        }
    }

    private static int range = 10;

    private static int counts = 100;

    private static Random r = new Random();

    /**
     * public TestMapRed(int range, int counts, Configuration conf) throws IOException {
     * this.range = range;
     * this.counts = counts;
     * this.conf = conf;
     * }
     */
    @Test
    public void testMapred() throws Exception {
        launch();
    }

    private static class MyMap implements Mapper<WritableComparable, Text, Text, Text> {
        public void configure(JobConf conf) {
        }

        public void map(WritableComparable key, Text value, OutputCollector<Text, Text> output, Reporter reporter) throws IOException {
            String str = StringUtils.toLowerCase(value.toString());
            output.collect(new Text(str), value);
        }

        public void close() throws IOException {
        }
    }

    private static class MyReduce extends IdentityReducer {
        private JobConf conf;

        private boolean compressInput;

        private boolean first = true;

        @Override
        public void configure(JobConf conf) {
            this.conf = conf;
            compressInput = conf.getCompressMapOutput();
        }

        public void reduce(WritableComparable key, Iterator values, OutputCollector output, Reporter reporter) throws IOException {
            if (first) {
                first = false;
                MapOutputFile mapOutputFile = new MROutputFiles();
                mapOutputFile.setConf(conf);
                Path input = mapOutputFile.getInputFile(0);
                FileSystem fs = FileSystem.get(conf);
                Assert.assertTrue(("reduce input exists " + input), fs.exists(input));
                SequenceFile.Reader rdr = new SequenceFile.Reader(fs, input, conf);
                Assert.assertEquals(("is reduce input compressed " + input), compressInput, rdr.isCompressed());
                rdr.close();
            }
        }
    }

    public static class NullMapper implements Mapper<NullWritable, Text, NullWritable, Text> {
        public void map(NullWritable key, Text val, OutputCollector<NullWritable, Text> output, Reporter reporter) throws IOException {
            output.collect(NullWritable.get(), val);
        }

        public void configure(JobConf conf) {
        }

        public void close() {
        }
    }

    @Test
    public void testNullKeys() throws Exception {
        JobConf conf = new JobConf(TestMapRed.class);
        FileSystem fs = FileSystem.getLocal(conf);
        HashSet<String> values = new HashSet<String>();
        String m = "AAAAAAAAAAAAAA";
        for (int i = 1; i < 11; ++i) {
            values.add(m);
            m = m.replace(((char) (('A' + i) - 1)), ((char) ('A' + i)));
        }
        Path testdir = fs.makeQualified(new Path(System.getProperty("test.build.data", "/tmp")));
        fs.delete(testdir, true);
        Path inFile = new Path(testdir, "nullin/blah");
        SequenceFile.Writer w = SequenceFile.createWriter(fs, conf, inFile, NullWritable.class, Text.class, NONE);
        Text t = new Text();
        for (String s : values) {
            t.set(s);
            w.append(NullWritable.get(), t);
        }
        w.close();
        FileInputFormat.setInputPaths(conf, inFile);
        FileOutputFormat.setOutputPath(conf, new Path(testdir, "nullout"));
        conf.setMapperClass(TestMapRed.NullMapper.class);
        conf.setReducerClass(IdentityReducer.class);
        conf.setOutputKeyClass(NullWritable.class);
        conf.setOutputValueClass(Text.class);
        conf.setInputFormat(SequenceFileInputFormat.class);
        conf.setOutputFormat(SequenceFileOutputFormat.class);
        conf.setNumReduceTasks(1);
        conf.set(FRAMEWORK_NAME, LOCAL_FRAMEWORK_NAME);
        JobClient.runJob(conf);
        // Since null keys all equal, allow any ordering
        SequenceFile.Reader r = new SequenceFile.Reader(fs, new Path(testdir, "nullout/part-00000"), conf);
        m = "AAAAAAAAAAAAAA";
        for (int i = 1; r.next(NullWritable.get(), t); ++i) {
            Assert.assertTrue(("Unexpected value: " + t), values.remove(t.toString()));
            m = m.replace(((char) (('A' + i) - 1)), ((char) ('A' + i)));
        }
        Assert.assertTrue(("Missing values: " + (values.toString())), values.isEmpty());
    }

    @Test
    public void testCompression() throws Exception {
        EnumSet<SequenceFile.CompressionType> seq = EnumSet.allOf(CompressionType.class);
        for (CompressionType redCompression : seq) {
            for (int combine = 0; combine < 2; ++combine) {
                checkCompression(false, redCompression, (combine == 1));
                checkCompression(true, redCompression, (combine == 1));
            }
        }
    }

    @Test
    public void testSmallInput() {
        runJob(100);
    }

    @Test
    public void testBiggerInput() {
        runJob(1000);
    }
}

