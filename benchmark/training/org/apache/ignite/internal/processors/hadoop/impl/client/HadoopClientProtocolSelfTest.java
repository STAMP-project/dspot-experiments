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
package org.apache.ignite.internal.processors.hadoop.impl.client;


import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Counter;
import org.apache.hadoop.mapreduce.Counters;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputCommitter;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.ignite.IgniteFileSystem;
import org.apache.ignite.igfs.IgfsPath;
import org.apache.ignite.internal.processors.hadoop.impl.HadoopAbstractSelfTest;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.junit.Test;


/**
 * Hadoop client protocol tests in external process mode.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class HadoopClientProtocolSelfTest extends HadoopAbstractSelfTest {
    /**
     * Input path.
     */
    private static final String PATH_INPUT = "/input";

    /**
     * Output path.
     */
    private static final String PATH_OUTPUT = "/output";

    /**
     * Job name.
     */
    private static final String JOB_NAME = "myJob";

    /**
     * Setup lock file.
     */
    private static File setupLockFile = new File((U.isWindows() ? System.getProperty("java.io.tmpdir") : "/tmp"), "ignite-lock-setup.file");

    /**
     * Map lock file.
     */
    private static File mapLockFile = new File((U.isWindows() ? System.getProperty("java.io.tmpdir") : "/tmp"), "ignite-lock-map.file");

    /**
     * Reduce lock file.
     */
    private static File reduceLockFile = new File((U.isWindows() ? System.getProperty("java.io.tmpdir") : "/tmp"), "ignite-lock-reduce.file");

    /**
     * Tests job counters retrieval.
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testJobCounters() throws Exception {
        IgniteFileSystem igfs = grid(0).fileSystem(HadoopAbstractSelfTest.igfsName);
        igfs.mkdirs(new IgfsPath(HadoopClientProtocolSelfTest.PATH_INPUT));
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(igfs.create(new IgfsPath(((HadoopClientProtocolSelfTest.PATH_INPUT) + "/test.file")), true)))) {
            bw.write(("alpha\n" + ((((((("beta\n" + "gamma\n") + "alpha\n") + "beta\n") + "gamma\n") + "alpha\n") + "beta\n") + "gamma\n")));
        }
        Configuration conf = config(HadoopAbstractSelfTest.REST_PORT);
        final Job job = Job.getInstance(conf);
        try {
            job.setOutputKeyClass(Text.class);
            job.setOutputValueClass(IntWritable.class);
            job.setMapperClass(HadoopClientProtocolSelfTest.TestCountingMapper.class);
            job.setReducerClass(HadoopClientProtocolSelfTest.TestCountingReducer.class);
            job.setCombinerClass(HadoopClientProtocolSelfTest.TestCountingCombiner.class);
            FileInputFormat.setInputPaths(job, new Path(((("igfs://" + (HadoopAbstractSelfTest.igfsName)) + "@") + (HadoopClientProtocolSelfTest.PATH_INPUT))));
            FileOutputFormat.setOutputPath(job, new Path(((("igfs://" + (HadoopAbstractSelfTest.igfsName)) + "@") + (HadoopClientProtocolSelfTest.PATH_OUTPUT))));
            job.submit();
            final Counter cntr = job.getCounters().findCounter(HadoopClientProtocolSelfTest.TestCounter.COUNTER1);
            assertEquals(0, cntr.getValue());
            cntr.increment(10);
            assertEquals(10, cntr.getValue());
            // Transferring to map phase.
            HadoopClientProtocolSelfTest.setupLockFile.delete();
            // Transferring to reduce phase.
            HadoopClientProtocolSelfTest.mapLockFile.delete();
            job.waitForCompletion(false);
            assertEquals("job must end successfully", JobStatus.State.SUCCEEDED, job.getStatus().getState());
            final Counters counters = job.getCounters();
            assertNotNull("counters cannot be null", counters);
            assertEquals("wrong counters count", 3, counters.countCounters());
            assertEquals("wrong counter value", 15, counters.findCounter(HadoopClientProtocolSelfTest.TestCounter.COUNTER1).getValue());
            assertEquals("wrong counter value", 3, counters.findCounter(HadoopClientProtocolSelfTest.TestCounter.COUNTER2).getValue());
            assertEquals("wrong counter value", 3, counters.findCounter(HadoopClientProtocolSelfTest.TestCounter.COUNTER3).getValue());
        } catch (Throwable t) {
            log.error("Unexpected exception", t);
        } finally {
            job.getCluster().close();
        }
    }

    /**
     * Test mapper.
     */
    public static class TestMapper extends Mapper<Object, Text, Text, IntWritable> {
        /**
         * Writable container for writing word.
         */
        private Text word = new Text();

        /**
         * Writable integer constant of '1' is writing as count of found words.
         */
        private static final IntWritable one = new IntWritable(1);

        /**
         * {@inheritDoc }
         */
        @Override
        public void map(Object key, Text val, Context ctx) throws IOException, InterruptedException {
            while (HadoopClientProtocolSelfTest.mapLockFile.exists())
                Thread.sleep(50);

            StringTokenizer wordList = new StringTokenizer(val.toString());
            while (wordList.hasMoreTokens()) {
                word.set(wordList.nextToken());
                ctx.write(word, HadoopClientProtocolSelfTest.TestMapper.one);
            } 
        }
    }

    /**
     * Test Hadoop counters.
     */
    public enum TestCounter {

        /**
         *
         */
        COUNTER1,
        /**
         *
         */
        COUNTER2,
        /**
         *
         */
        COUNTER3;}

    /**
     * Test mapper that uses counters.
     */
    public static class TestCountingMapper extends HadoopClientProtocolSelfTest.TestMapper {
        /**
         * {@inheritDoc }
         */
        @Override
        public void map(Object key, Text val, Context ctx) throws IOException, InterruptedException {
            super.map(key, val, ctx);
            ctx.getCounter(HadoopClientProtocolSelfTest.TestCounter.COUNTER1).increment(1);
        }
    }

    /**
     * Test combiner that counts invocations.
     */
    public static class TestCountingCombiner extends HadoopClientProtocolSelfTest.TestReducer {
        /**
         * {@inheritDoc }
         */
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context ctx) throws IOException, InterruptedException {
            ctx.getCounter(HadoopClientProtocolSelfTest.TestCounter.COUNTER1).increment(1);
            ctx.getCounter(HadoopClientProtocolSelfTest.TestCounter.COUNTER2).increment(1);
            int sum = 0;
            for (IntWritable value : values)
                sum += value.get();

            ctx.write(key, new IntWritable(sum));
        }
    }

    /**
     * Test reducer that counts invocations.
     */
    public static class TestCountingReducer extends HadoopClientProtocolSelfTest.TestReducer {
        /**
         * {@inheritDoc }
         */
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context ctx) throws IOException, InterruptedException {
            ctx.getCounter(HadoopClientProtocolSelfTest.TestCounter.COUNTER1).increment(1);
            ctx.getCounter(HadoopClientProtocolSelfTest.TestCounter.COUNTER3).increment(1);
        }
    }

    /**
     * Test combiner.
     */
    // No-op.
    public static class TestCombiner extends Reducer<Text, IntWritable, Text, IntWritable> {}

    /**
     * Test output format.
     */
    public static class TestOutputFormat<K, V> extends TextOutputFormat<K, V> {
        /**
         * {@inheritDoc }
         */
        @Override
        public synchronized OutputCommitter getOutputCommitter(TaskAttemptContext ctx) throws IOException {
            return new HadoopClientProtocolSelfTest.TestOutputCommitter(ctx, ((FileOutputCommitter) (super.getOutputCommitter(ctx))));
        }
    }

    /**
     * Test output committer.
     */
    private static class TestOutputCommitter extends FileOutputCommitter {
        /**
         * Delegate.
         */
        private final FileOutputCommitter delegate;

        /**
         * Constructor.
         *
         * @param ctx
         * 		Task attempt context.
         * @param delegate
         * 		Delegate.
         * @throws IOException
         * 		If failed.
         */
        private TestOutputCommitter(TaskAttemptContext ctx, FileOutputCommitter delegate) throws IOException {
            super(FileOutputFormat.getOutputPath(ctx), ctx);
            this.delegate = delegate;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setupJob(JobContext jobCtx) throws IOException {
            try {
                while (HadoopClientProtocolSelfTest.setupLockFile.exists())
                    Thread.sleep(50);

            } catch (InterruptedException ignored) {
                throw new IOException("Interrupted.");
            }
            delegate.setupJob(jobCtx);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void setupTask(TaskAttemptContext taskCtx) throws IOException {
            delegate.setupTask(taskCtx);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public boolean needsTaskCommit(TaskAttemptContext taskCtx) throws IOException {
            return delegate.needsTaskCommit(taskCtx);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void commitTask(TaskAttemptContext taskCtx) throws IOException {
            delegate.commitTask(taskCtx);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void abortTask(TaskAttemptContext taskCtx) throws IOException {
            delegate.abortTask(taskCtx);
        }
    }

    /**
     * Test reducer.
     */
    public static class TestReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        /**
         * Writable container for writing sum of word counts.
         */
        private IntWritable totalWordCnt = new IntWritable();

        /**
         * {@inheritDoc }
         */
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context ctx) throws IOException, InterruptedException {
            while (HadoopClientProtocolSelfTest.reduceLockFile.exists())
                Thread.sleep(50);

            int wordCnt = 0;
            for (IntWritable value : values)
                wordCnt += value.get();

            totalWordCnt.set(wordCnt);
            ctx.write(key, totalWordCnt);
        }
    }
}

