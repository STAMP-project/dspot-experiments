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


import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
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
public class TestMapReduce {
    private static final File TEST_DIR = new File(System.getProperty("test.build.data", System.getProperty("java.io.tmpdir")), "TestMapReduce-mapreduce");

    private static FileSystem fs;

    static {
        try {
            TestMapReduce.fs = FileSystem.getLocal(new Configuration());
        } catch (IOException ioe) {
            TestMapReduce.fs = null;
        }
    }

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
    static class RandomGenMapper extends Mapper<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void map(IntWritable key, IntWritable val, Context context) throws IOException, InterruptedException {
            int randomVal = key.get();
            int randomCount = val.get();
            for (int i = 0; i < randomCount; i++) {
                context.write(new IntWritable(Math.abs(TestMapReduce.r.nextInt())), new IntWritable(randomVal));
            }
        }
    }

    /**
     *
     */
    static class RandomGenReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void reduce(IntWritable key, Iterable<IntWritable> it, Context context) throws IOException, InterruptedException {
            for (IntWritable iw : it) {
                context.write(iw, null);
            }
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
    static class RandomCheckMapper extends Mapper<WritableComparable<?>, Text, IntWritable, IntWritable> {
        public void map(WritableComparable<?> key, Text val, Context context) throws IOException, InterruptedException {
            context.write(new IntWritable(Integer.parseInt(val.toString().trim())), new IntWritable(1));
        }
    }

    /**
     *
     */
    static class RandomCheckReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void reduce(IntWritable key, Iterable<IntWritable> it, Context context) throws IOException, InterruptedException {
            int keyint = key.get();
            int count = 0;
            for (IntWritable iw : it) {
                count++;
            }
            context.write(new IntWritable(keyint), new IntWritable(count));
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
    static class MergeMapper extends Mapper<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void map(IntWritable key, IntWritable val, Context context) throws IOException, InterruptedException {
            int keyint = key.get();
            int valint = val.get();
            context.write(new IntWritable(keyint), new IntWritable(valint));
        }
    }

    static class MergeReducer extends Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        public void reduce(IntWritable key, Iterator<IntWritable> it, Context context) throws IOException, InterruptedException {
            int keyint = key.get();
            int total = 0;
            while (it.hasNext()) {
                total += it.next().get();
            } 
            context.write(new IntWritable(keyint), new IntWritable(total));
        }
    }

    private static int range = 10;

    private static int counts = 100;

    private static Random r = new Random();

    @Test
    public void testMapred() throws Exception {
        TestMapReduce.launch();
    }
}

