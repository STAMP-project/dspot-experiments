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


import java.io.IOException;
import java.util.Iterator;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.HadoopTestCase;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Tests context api and {@link StatusReporter#getProgress()} via
 * {@link TaskAttemptContext#getProgress()} API .
 */
@Ignore
public class TestTaskContext extends HadoopTestCase {
    private static final Path rootTempDir = new Path(System.getProperty("test.build.data", "/tmp"));

    private static final Path testRootTempDir = new Path(TestTaskContext.rootTempDir, "TestTaskContext");

    private static FileSystem fs = null;

    public TestTaskContext() throws IOException {
        super(HadoopTestCase.CLUSTER_MR, HadoopTestCase.LOCAL_FS, 1, 1);
    }

    static String myStatus = "my status";

    static class MyMapper extends Mapper<LongWritable, Text, LongWritable, Text> {
        @Override
        protected void setup(Context context) throws IOException {
            context.setStatus(TestTaskContext.myStatus);
            Assert.assertEquals(TestTaskContext.myStatus, context.getStatus());
        }
    }

    // an input with 4 lines
    private static final String INPUT = "Hi\nHi\nHi\nHi\n";

    private static final int INPUT_LINES = TestTaskContext.INPUT.split("\n").length;

    @SuppressWarnings("unchecked")
    static class ProgressCheckerMapper extends Mapper<LongWritable, Text, Text, Text> {
        private int recordCount = 0;

        private float progressRange = 0;

        @Override
        protected void setup(Context context) throws IOException {
            // check if the map task attempt progress is 0
            Assert.assertEquals("Invalid progress in map setup", 0.0F, context.getProgress(), 0.0F);
            // define the progress boundaries
            if ((context.getNumReduceTasks()) == 0) {
                progressRange = 1.0F;
            } else {
                progressRange = 0.667F;
            }
        }

        @Override
        protected void map(LongWritable key, Text value, Mapper.Context context) throws IOException, InterruptedException {
            // get the map phase progress
            float mapPhaseProgress = ((float) (++(recordCount))) / (TestTaskContext.INPUT_LINES);
            // get the weighted map phase progress
            float weightedMapProgress = (progressRange) * mapPhaseProgress;
            // check the map progress
            Assert.assertEquals("Invalid progress in map", weightedMapProgress, context.getProgress(), 0.0F);
            context.write(new Text(((value.toString()) + (recordCount))), value);
        }

        protected void cleanup(Mapper.Context context) throws IOException, InterruptedException {
            // check if the attempt progress is at the progress boundary
            Assert.assertEquals("Invalid progress in map cleanup", progressRange, context.getProgress(), 0.0F);
        }
    }

    @SuppressWarnings("unchecked")
    static class ProgressCheckerReducer extends Reducer<Text, Text, Text, Text> {
        private int recordCount = 0;

        private final float REDUCE_PROGRESS_RANGE = 1.0F / 3;

        private final float SHUFFLE_PROGRESS_RANGE = 1 - (REDUCE_PROGRESS_RANGE);

        protected void setup(final Reducer.Context context) throws IOException, InterruptedException {
            // Note that the reduce will read some segments before calling setup()
            float reducePhaseProgress = ((float) (++(recordCount))) / (TestTaskContext.INPUT_LINES);
            float weightedReducePhaseProgress = (REDUCE_PROGRESS_RANGE) * reducePhaseProgress;
            // check that the shuffle phase progress is accounted for
            Assert.assertEquals("Invalid progress in reduce setup", ((SHUFFLE_PROGRESS_RANGE) + weightedReducePhaseProgress), context.getProgress(), 0.01F);
        }

        public void reduce(Text key, Iterator<Text> values, Context context) throws IOException, InterruptedException {
            float reducePhaseProgress = ((float) (++(recordCount))) / (TestTaskContext.INPUT_LINES);
            float weightedReducePhaseProgress = (REDUCE_PROGRESS_RANGE) * reducePhaseProgress;
            Assert.assertEquals("Invalid progress in reduce", ((SHUFFLE_PROGRESS_RANGE) + weightedReducePhaseProgress), context.getProgress(), 0.01F);
        }

        protected void cleanup(Reducer.Context context) throws IOException, InterruptedException {
            // check if the reduce task has progress of 1 in the end
            Assert.assertEquals("Invalid progress in reduce cleanup", 1.0F, context.getProgress(), 0.0F);
        }
    }

    /**
     * Tests new MapReduce reduce task's context.getProgress() method.
     *
     * @throws IOException
     * 		
     * @throws InterruptedException
     * 		
     * @throws ClassNotFoundException
     * 		
     */
    @Test
    public void testReduceContextProgress() throws IOException, ClassNotFoundException, InterruptedException {
        int numTasks = 1;
        Path test = new Path(TestTaskContext.testRootTempDir, "testReduceContextProgress");
        Job job = MapReduceTestUtil.createJob(createJobConf(), new Path(test, "in"), new Path(test, "out"), numTasks, numTasks, TestTaskContext.INPUT);
        job.setMapperClass(TestTaskContext.ProgressCheckerMapper.class);
        job.setReducerClass(TestTaskContext.ProgressCheckerReducer.class);
        job.setMapOutputKeyClass(Text.class);
        // fail early
        job.setMaxMapAttempts(1);
        job.setMaxReduceAttempts(1);
        job.waitForCompletion(true);
        Assert.assertTrue("Job failed", job.isSuccessful());
    }
}

