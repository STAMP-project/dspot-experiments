/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ignite.internal.processors.hadoop.impl;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.InputFormat;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.ignite.internal.processors.hadoop.HadoopJobId;
import org.apache.ignite.internal.processors.hadoop.state.HadoopJobTrackerSelfTestState;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.junit.Test;


/**
 * Job tracker self test.
 */
public class HadoopJobTrackerSelfTest extends HadoopAbstractSelfTest {
    /**
     *
     */
    private static final String PATH_OUTPUT = "/test-out";

    /**
     * Test block count parameter name.
     */
    private static final int BLOCK_CNT = 10;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSimpleTaskSubmit() throws Exception {
        try {
            UUID globalId = UUID.randomUUID();
            Job job = Job.getInstance();
            setupFileSystems(job.getConfiguration());
            job.setMapperClass(HadoopJobTrackerSelfTest.TestMapper.class);
            job.setReducerClass(HadoopJobTrackerSelfTest.TestReducer.class);
            job.setInputFormatClass(HadoopJobTrackerSelfTest.InFormat.class);
            FileOutputFormat.setOutputPath(job, new Path((((igfsScheme()) + (HadoopJobTrackerSelfTest.PATH_OUTPUT)) + "1")));
            HadoopJobId jobId = new HadoopJobId(globalId, 1);
            grid(0).hadoop().submit(jobId, HadoopUtils.createJobInfo(job.getConfiguration(), null));
            checkStatus(jobId, false);
            info("Releasing map latch.");
            HadoopJobTrackerSelfTestState.latch.get("mapAwaitLatch").countDown();
            checkStatus(jobId, false);
            info("Releasing reduce latch.");
            HadoopJobTrackerSelfTestState.latch.get("reduceAwaitLatch").countDown();
            checkStatus(jobId, true);
            assertEquals(10, HadoopJobTrackerSelfTestState.mapExecCnt.get());
            assertEquals(0, HadoopJobTrackerSelfTestState.combineExecCnt.get());
            assertEquals(1, HadoopJobTrackerSelfTestState.reduceExecCnt.get());
        } finally {
            // Safety.
            HadoopJobTrackerSelfTestState.latch.get("mapAwaitLatch").countDown();
            HadoopJobTrackerSelfTestState.latch.get("combineAwaitLatch").countDown();
            HadoopJobTrackerSelfTestState.latch.get("reduceAwaitLatch").countDown();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testTaskWithCombinerPerMap() throws Exception {
        try {
            UUID globalId = UUID.randomUUID();
            Job job = Job.getInstance();
            setupFileSystems(job.getConfiguration());
            job.setMapperClass(HadoopJobTrackerSelfTest.TestMapper.class);
            job.setReducerClass(HadoopJobTrackerSelfTest.TestReducer.class);
            job.setCombinerClass(HadoopJobTrackerSelfTest.TestCombiner.class);
            job.setInputFormatClass(HadoopJobTrackerSelfTest.InFormat.class);
            FileOutputFormat.setOutputPath(job, new Path((((igfsScheme()) + (HadoopJobTrackerSelfTest.PATH_OUTPUT)) + "2")));
            HadoopJobId jobId = new HadoopJobId(globalId, 1);
            grid(0).hadoop().submit(jobId, HadoopUtils.createJobInfo(job.getConfiguration(), null));
            checkStatus(jobId, false);
            info("Releasing map latch.");
            HadoopJobTrackerSelfTestState.latch.get("mapAwaitLatch").countDown();
            checkStatus(jobId, false);
            // All maps are completed. We have a combiner, so no reducers should be executed
            // before combiner latch is released.
            U.sleep(50);
            assertEquals(0, HadoopJobTrackerSelfTestState.reduceExecCnt.get());
            info("Releasing combiner latch.");
            HadoopJobTrackerSelfTestState.latch.get("combineAwaitLatch").countDown();
            checkStatus(jobId, false);
            info("Releasing reduce latch.");
            HadoopJobTrackerSelfTestState.latch.get("reduceAwaitLatch").countDown();
            checkStatus(jobId, true);
            assertEquals(10, HadoopJobTrackerSelfTestState.mapExecCnt.get());
            assertEquals(10, HadoopJobTrackerSelfTestState.combineExecCnt.get());
            assertEquals(1, HadoopJobTrackerSelfTestState.reduceExecCnt.get());
        } finally {
            // Safety.
            HadoopJobTrackerSelfTestState.latch.get("mapAwaitLatch").countDown();
            HadoopJobTrackerSelfTestState.latch.get("combineAwaitLatch").countDown();
            HadoopJobTrackerSelfTestState.latch.get("reduceAwaitLatch").countDown();
        }
    }

    /**
     * Test input format
     */
    public static class InFormat extends InputFormat {
        @Override
        public List<InputSplit> getSplits(JobContext ctx) throws IOException, InterruptedException {
            List<InputSplit> res = new ArrayList<>(HadoopJobTrackerSelfTest.BLOCK_CNT);
            for (int i = 0; i < (HadoopJobTrackerSelfTest.BLOCK_CNT); i++)
                try {
                    res.add(new org.apache.hadoop.mapreduce.lib.input.FileSplit(new Path(new URI("someFile")), i, (i + 1), new String[]{ "localhost" }));
                } catch (URISyntaxException e) {
                    throw new IOException(e);
                }

            return res;
        }

        @Override
        public RecordReader createRecordReader(InputSplit split, TaskAttemptContext ctx) throws IOException, InterruptedException {
            return new RecordReader() {
                @Override
                public void initialize(InputSplit split, TaskAttemptContext ctx) {
                }

                @Override
                public boolean nextKeyValue() {
                    return false;
                }

                @Override
                public Object getCurrentKey() {
                    return null;
                }

                @Override
                public Object getCurrentValue() {
                    return null;
                }

                @Override
                public float getProgress() {
                    return 0;
                }

                @Override
                public void close() {
                }
            };
        }
    }

    /**
     * Test mapper.
     */
    private static class TestMapper extends Mapper {
        @Override
        public void run(Context ctx) throws IOException, InterruptedException {
            System.out.println(("Running task: " + (ctx.getTaskAttemptID().getTaskID().getId())));
            HadoopJobTrackerSelfTestState.latch.get("mapAwaitLatch").await();
            HadoopJobTrackerSelfTestState.mapExecCnt.incrementAndGet();
            System.out.println(("Completed task: " + (ctx.getTaskAttemptID().getTaskID().getId())));
        }
    }

    /**
     * Test reducer.
     */
    private static class TestReducer extends Reducer {
        @Override
        public void run(Context ctx) throws IOException, InterruptedException {
            System.out.println(("Running task: " + (ctx.getTaskAttemptID().getTaskID().getId())));
            HadoopJobTrackerSelfTestState.latch.get("reduceAwaitLatch").await();
            HadoopJobTrackerSelfTestState.reduceExecCnt.incrementAndGet();
            System.out.println(("Completed task: " + (ctx.getTaskAttemptID().getTaskID().getId())));
        }
    }

    /**
     * Test combiner.
     */
    private static class TestCombiner extends Reducer {
        @Override
        public void run(Context ctx) throws IOException, InterruptedException {
            System.out.println(("Running task: " + (ctx.getTaskAttemptID().getTaskID().getId())));
            HadoopJobTrackerSelfTestState.latch.get("combineAwaitLatch").await();
            HadoopJobTrackerSelfTestState.combineExecCnt.incrementAndGet();
            System.out.println(("Completed task: " + (ctx.getTaskAttemptID().getTaskID().getId())));
        }
    }
}

