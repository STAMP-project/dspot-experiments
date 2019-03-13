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


import IntWritable.Comparator;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.junit.Assert;
import org.junit.Test;


/**
 * Two different types of comparators can be used in MapReduce. One is used
 * during the Map and Reduce phases, to sort/merge key-value pairs. Another
 * is used to group values for a particular key, when calling the user's
 * reducer. A user can override both of these two.
 * This class has tests for making sure we use the right comparators at the
 * right places. See Hadoop issues 485 and 1535. Our tests:
 * 1. Test that the same comparator is used for all sort/merge operations
 * during the Map and Reduce phases.
 * 2. Test the common use case where values are grouped by keys but values
 * within each key are grouped by a secondary key (a timestamp, for example).
 */
public class TestComparators {
    private static final File TEST_DIR = new File(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), "TestComparators-mapred");

    JobConf conf = new JobConf(TestMapOutputType.class);

    JobClient jc;

    static Random rng = new Random();

    /**
     * RandomGen is a mapper that generates 5 random values for each key
     * in the input. The values are in the range [0-4]. The mapper also
     * generates a composite key. If the input key is x and the generated
     * value is y, the composite key is x0y (x-zero-y). Therefore, the inter-
     * mediate key value pairs are ordered by {input key, value}.
     * Think of the random value as a timestamp associated with the record.
     */
    static class RandomGenMapper implements Mapper<IntWritable, Writable, IntWritable, IntWritable> {
        public void configure(JobConf job) {
        }

        public void map(IntWritable key, Writable value, OutputCollector<IntWritable, IntWritable> out, Reporter reporter) throws IOException {
            int num_values = 5;
            for (int i = 0; i < num_values; ++i) {
                int val = TestComparators.rng.nextInt(num_values);
                int compositeKey = ((key.get()) * 100) + val;
                out.collect(new IntWritable(compositeKey), new IntWritable(val));
            }
        }

        public void close() {
        }
    }

    /**
     * Your basic identity mapper.
     */
    static class IdentityMapper implements Mapper<WritableComparable, Writable, WritableComparable, Writable> {
        public void configure(JobConf job) {
        }

        public void map(WritableComparable key, Writable value, OutputCollector<WritableComparable, Writable> out, Reporter reporter) throws IOException {
            out.collect(key, value);
        }

        public void close() {
        }
    }

    /**
     * Checks whether keys are in ascending order.
     */
    static class AscendingKeysReducer implements Reducer<IntWritable, Writable, IntWritable, Text> {
        public void configure(JobConf job) {
        }

        // keep track of the last key we've seen
        private int lastKey = Integer.MIN_VALUE;

        public void reduce(IntWritable key, Iterator<Writable> values, OutputCollector<IntWritable, Text> out, Reporter reporter) throws IOException {
            int currentKey = key.get();
            // keys should be in ascending order
            if (currentKey < (lastKey)) {
                Assert.fail("Keys not in sorted ascending order");
            }
            lastKey = currentKey;
            out.collect(key, new Text("success"));
        }

        public void close() {
        }
    }

    /**
     * Checks whether keys are in ascending order.
     */
    static class DescendingKeysReducer implements Reducer<IntWritable, Writable, IntWritable, Text> {
        public void configure(JobConf job) {
        }

        // keep track of the last key we've seen
        private int lastKey = Integer.MAX_VALUE;

        public void reduce(IntWritable key, Iterator<Writable> values, OutputCollector<IntWritable, Text> out, Reporter reporter) throws IOException {
            int currentKey = key.get();
            // keys should be in descending order
            if (currentKey > (lastKey)) {
                Assert.fail("Keys not in sorted descending order");
            }
            lastKey = currentKey;
            out.collect(key, new Text("success"));
        }

        public void close() {
        }
    }

    /**
     * The reducer checks whether the input values are in ascending order and
     * whether they are correctly grouped by key (i.e. each call to reduce
     * should have 5 values if the grouping is correct). It also checks whether
     * the keys themselves are in ascending order.
     */
    static class AscendingGroupReducer implements Reducer<IntWritable, IntWritable, IntWritable, Text> {
        public void configure(JobConf job) {
        }

        // keep track of the last key we've seen
        private int lastKey = Integer.MIN_VALUE;

        public void reduce(IntWritable key, Iterator<IntWritable> values, OutputCollector<IntWritable, Text> out, Reporter reporter) throws IOException {
            // check key order
            int currentKey = key.get();
            if (currentKey < (lastKey)) {
                Assert.fail("Keys not in sorted ascending order");
            }
            lastKey = currentKey;
            // check order of values
            IntWritable previous = new IntWritable(Integer.MIN_VALUE);
            int valueCount = 0;
            while (values.hasNext()) {
                IntWritable current = values.next();
                // Check that the values are sorted
                if ((current.compareTo(previous)) < 0)
                    Assert.fail("Values generated by Mapper not in order");

                previous = current;
                ++valueCount;
            } 
            if (valueCount != 5) {
                Assert.fail("Values not grouped by primary key");
            }
            out.collect(key, new Text("success"));
        }

        public void close() {
        }
    }

    /**
     * The reducer checks whether the input values are in descending order and
     * whether they are correctly grouped by key (i.e. each call to reduce
     * should have 5 values if the grouping is correct).
     */
    static class DescendingGroupReducer implements Reducer<IntWritable, IntWritable, IntWritable, Text> {
        public void configure(JobConf job) {
        }

        // keep track of the last key we've seen
        private int lastKey = Integer.MAX_VALUE;

        public void reduce(IntWritable key, Iterator<IntWritable> values, OutputCollector<IntWritable, Text> out, Reporter reporter) throws IOException {
            // check key order
            int currentKey = key.get();
            if (currentKey > (lastKey)) {
                Assert.fail("Keys not in sorted descending order");
            }
            lastKey = currentKey;
            // check order of values
            IntWritable previous = new IntWritable(Integer.MAX_VALUE);
            int valueCount = 0;
            while (values.hasNext()) {
                IntWritable current = values.next();
                // Check that the values are sorted
                if ((current.compareTo(previous)) > 0)
                    Assert.fail("Values generated by Mapper not in order");

                previous = current;
                ++valueCount;
            } 
            if (valueCount != 5) {
                Assert.fail("Values not grouped by primary key");
            }
            out.collect(key, new Text("success"));
        }

        public void close() {
        }
    }

    /**
     * A decreasing Comparator for IntWritable
     */
    public static class DecreasingIntComparator extends IntWritable.Comparator {
        public int compare(byte[] b1, int s1, int l1, byte[] b2, int s2, int l2) {
            return -(super.compare(b1, s1, l1, b2, s2, l2));
        }

        static {
            // register this comparator
            WritableComparator.define(TestComparators.DecreasingIntComparator.class, new IntWritable.Comparator());
        }
    }

    /**
     * Grouping function for values based on the composite key. This
     * comparator strips off the secondary key part from the x0y composite
     * and only compares the primary key value (x).
     */
    public static class CompositeIntGroupFn extends WritableComparator {
        public CompositeIntGroupFn() {
            super(IntWritable.class);
        }

        public int compare(WritableComparable v1, WritableComparable v2) {
            int val1 = (get()) / 100;
            int val2 = (get()) / 100;
            if (val1 < val2)
                return 1;
            else
                if (val1 > val2)
                    return -1;
                else
                    return 0;


        }

        public boolean equals(IntWritable v1, IntWritable v2) {
            int val1 = v1.get();
            int val2 = v2.get();
            return (val1 / 100) == (val2 / 100);
        }

        static {
            WritableComparator.define(TestComparators.CompositeIntGroupFn.class, new IntWritable.Comparator());
        }
    }

    /**
     * Reverse grouping function for values based on the composite key.
     */
    public static class CompositeIntReverseGroupFn extends TestComparators.CompositeIntGroupFn {
        public int compare(WritableComparable v1, WritableComparable v2) {
            return -(super.compare(v1, v2));
        }

        public boolean equals(IntWritable v1, IntWritable v2) {
            return !(super.equals(v1, v2));
        }

        static {
            WritableComparator.define(TestComparators.CompositeIntReverseGroupFn.class, new IntWritable.Comparator());
        }
    }

    /**
     * Test the default comparator for Map/Reduce.
     * Use the identity mapper and see if the keys are sorted at the end
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testDefaultMRComparator() throws Exception {
        conf.setMapperClass(TestComparators.IdentityMapper.class);
        conf.setReducerClass(TestComparators.AscendingKeysReducer.class);
        RunningJob r_job = jc.submitJob(conf);
        while (!(r_job.isComplete())) {
            Thread.sleep(1000);
        } 
        if (!(r_job.isSuccessful())) {
            Assert.fail("Oops! The job broke due to an unexpected error");
        }
    }

    /**
     * Test user-defined comparator for Map/Reduce.
     * We provide our own comparator that is the reverse of the default int
     * comparator. Keys should be sorted in reverse order in the reducer.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUserMRComparator() throws Exception {
        conf.setMapperClass(TestComparators.IdentityMapper.class);
        conf.setReducerClass(TestComparators.DescendingKeysReducer.class);
        conf.setOutputKeyComparatorClass(TestComparators.DecreasingIntComparator.class);
        RunningJob r_job = jc.submitJob(conf);
        while (!(r_job.isComplete())) {
            Thread.sleep(1000);
        } 
        if (!(r_job.isSuccessful())) {
            Assert.fail("Oops! The job broke due to an unexpected error");
        }
    }

    /**
     * Test user-defined grouping comparator for grouping values in Reduce.
     * We generate composite keys that contain a random number, which acts
     * as a timestamp associated with the record. In our Reduce function,
     * values for a key should be sorted by the 'timestamp'.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testUserValueGroupingComparator() throws Exception {
        conf.setMapperClass(TestComparators.RandomGenMapper.class);
        conf.setReducerClass(TestComparators.AscendingGroupReducer.class);
        conf.setOutputValueGroupingComparator(TestComparators.CompositeIntGroupFn.class);
        RunningJob r_job = jc.submitJob(conf);
        while (!(r_job.isComplete())) {
            Thread.sleep(1000);
        } 
        if (!(r_job.isSuccessful())) {
            Assert.fail("Oops! The job broke due to an unexpected error");
        }
    }

    /**
     * Test all user comparators. Super-test of all tests here.
     * We generate composite keys that contain a random number, which acts
     * as a timestamp associated with the record. In our Reduce function,
     * values for a key should be sorted by the 'timestamp'.
     * We also provide our own comparators that reverse the default sorting
     * order. This lets us make sure that the right comparators are used.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testAllUserComparators() throws Exception {
        conf.setMapperClass(TestComparators.RandomGenMapper.class);
        // use a decreasing comparator so keys are sorted in reverse order
        conf.setOutputKeyComparatorClass(TestComparators.DecreasingIntComparator.class);
        conf.setReducerClass(TestComparators.DescendingGroupReducer.class);
        conf.setOutputValueGroupingComparator(TestComparators.CompositeIntReverseGroupFn.class);
        RunningJob r_job = jc.submitJob(conf);
        while (!(r_job.isComplete())) {
            Thread.sleep(1000);
        } 
        if (!(r_job.isSuccessful())) {
            Assert.fail("Oops! The job broke due to an unexpected error");
        }
    }

    /**
     * Test a user comparator that relies on deserializing both arguments
     * for each compare.
     */
    @Test
    public void testBakedUserComparator() throws Exception {
        TestComparators.MyWritable a = new TestComparators.MyWritable(8, 8);
        TestComparators.MyWritable b = new TestComparators.MyWritable(7, 9);
        Assert.assertTrue(((a.compareTo(b)) > 0));
        Assert.assertTrue(((WritableComparator.get(TestComparators.MyWritable.class).compare(a, b)) < 0));
    }

    public static class MyWritable implements WritableComparable<TestComparators.MyWritable> {
        int i;

        int j;

        public MyWritable() {
        }

        public MyWritable(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public void readFields(DataInput in) throws IOException {
            i = in.readInt();
            j = in.readInt();
        }

        public void write(DataOutput out) throws IOException {
            out.writeInt(i);
            out.writeInt(j);
        }

        public int compareTo(TestComparators.MyWritable b) {
            return (this.i) - (b.i);
        }

        static {
            WritableComparator.define(TestComparators.MyWritable.class, new TestComparators.MyCmp());
        }
    }

    public static class MyCmp extends WritableComparator {
        public MyCmp() {
            super(TestComparators.MyWritable.class, true);
        }

        public int compare(WritableComparable a, WritableComparable b) {
            TestComparators.MyWritable aa = ((TestComparators.MyWritable) (a));
            TestComparators.MyWritable bb = ((TestComparators.MyWritable) (b));
            return (aa.j) - (bb.j);
        }
    }
}

