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
import java.util.Iterator;
import org.junit.Test;


/**
 * TestCollect checks if the collect can handle simultaneous invocations.
 */
public class TestCollect {
    static final Path OUTPUT_DIR = new Path("build/test/test.collect.output");

    static final int NUM_FEEDERS = 10;

    static final int NUM_COLLECTS_PER_THREAD = 1000;

    /**
     * Map is a Mapper that spawns threads which simultaneously call collect.
     * Each thread has a specific range to write to the buffer and is unique to
     * the thread. This is a synchronization test for the map's collect.
     */
    static class Map implements Mapper<Text, Text, IntWritable, IntWritable> {
        public void configure(JobConf job) {
        }

        public void map(Text key, Text val, final OutputCollector<IntWritable, IntWritable> out, Reporter reporter) throws IOException {
            // Class for calling collect in separate threads
            class CollectFeeder extends Thread {
                int id;// id for the thread


                public CollectFeeder(int id) {
                    this.id = id;
                }

                public void run() {
                    for (int j = 1; j <= (TestCollect.NUM_COLLECTS_PER_THREAD); j++) {
                        try {
                            out.collect(new IntWritable((((id) * (TestCollect.NUM_COLLECTS_PER_THREAD)) + j)), new IntWritable(0));
                        } catch (IOException ioe) {
                        }
                    }
                }
            }
            CollectFeeder[] feeders = new CollectFeeder[TestCollect.NUM_FEEDERS];
            // start the feeders
            for (int i = 0; i < (TestCollect.NUM_FEEDERS); i++) {
                feeders[i] = new CollectFeeder(i);
                feeders[i].start();
            }
            // wait for them to finish
            for (int i = 0; i < (TestCollect.NUM_FEEDERS); i++) {
                try {
                    feeders[i].join();
                } catch (InterruptedException ie) {
                    throw new IOException(ie.toString());
                }
            }
        }

        public void close() {
        }
    }

    static class Reduce implements Reducer<IntWritable, IntWritable, IntWritable, IntWritable> {
        static int numSeen;

        static int actualSum;

        public void configure(JobConf job) {
        }

        public void reduce(IntWritable key, Iterator<IntWritable> val, OutputCollector<IntWritable, IntWritable> out, Reporter reporter) throws IOException {
            TestCollect.Reduce.actualSum += key.get();// keep the running count of the seen values

            (TestCollect.Reduce.numSeen)++;// number of values seen so far

            // using '1+2+3+...n =  n*(n+1)/2' to validate
            int expectedSum = ((TestCollect.Reduce.numSeen) * ((TestCollect.Reduce.numSeen) + 1)) / 2;
            if (expectedSum != (TestCollect.Reduce.actualSum)) {
                throw new IOException("Collect test failed!! Ordering mismatch.");
            }
        }

        public void close() {
        }
    }

    @Test
    public void testCollect() throws IOException {
        JobConf conf = new JobConf();
        configure(conf);
        try {
            JobClient.runJob(conf);
            // check if all the values were seen by the reducer
            if ((TestCollect.Reduce.numSeen) != ((TestCollect.NUM_COLLECTS_PER_THREAD) * (TestCollect.NUM_FEEDERS))) {
                throw new IOException("Collect test failed!! Total does not match.");
            }
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            FileSystem fs = FileSystem.get(conf);
            fs.delete(TestCollect.OUTPUT_DIR, true);
        }
    }
}

