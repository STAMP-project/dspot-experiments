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
package org.apache.hadoop.mapreduce.lib.output;


import FileOutputFormat.OUTDIR;
import java.io.IOException;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.TaskAttemptID;
import org.apache.hadoop.mapreduce.TaskType;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test the committer factory logic, looking at the override
 * and fallback behavior.
 */
@SuppressWarnings("unchecked")
public class TestPathOutputCommitterFactory extends Assert {
    private static final String HTTP_COMMITTER_FACTORY = String.format(COMMITTER_FACTORY_SCHEME_PATTERN, "http");

    private static final Path HTTP_PATH = new Path("http://hadoop.apache.org/");

    private static final Path HDFS_PATH = new Path("hdfs://localhost:8081/");

    private TaskAttemptID taskAttemptID = new TaskAttemptID("local", 0, TaskType.MAP, 1, 2);

    /**
     * Set a factory for a schema, verify it works.
     *
     * @throws Throwable
     * 		failure
     */
    @Test
    public void testCommitterFactoryForSchema() throws Throwable {
        createCommitterFactory(TestPathOutputCommitterFactory.SimpleCommitterFactory.class, TestPathOutputCommitterFactory.HTTP_PATH, newBondedConfiguration());
    }

    /**
     * A schema factory only affects that filesystem.
     *
     * @throws Throwable
     * 		failure
     */
    @Test
    public void testCommitterFactoryFallbackDefault() throws Throwable {
        createCommitterFactory(FileOutputCommitterFactory.class, TestPathOutputCommitterFactory.HDFS_PATH, newBondedConfiguration());
    }

    /**
     * A schema factory only affects that filesystem; test through
     * {@link PathOutputCommitterFactory#createCommitter(Path, TaskAttemptContext)}.
     *
     * @throws Throwable
     * 		failure
     */
    @Test
    public void testCommitterFallbackDefault() throws Throwable {
        createCommitter(FileOutputCommitter.class, TestPathOutputCommitterFactory.HDFS_PATH, taskAttempt(newBondedConfiguration()));
    }

    /**
     * Verify that you can override any schema with an explicit name.
     */
    @Test
    public void testCommitterFactoryOverride() throws Throwable {
        Configuration conf = newBondedConfiguration();
        // set up for the schema factory
        // and then set a global one which overrides the others.
        conf.set(COMMITTER_FACTORY_CLASS, TestPathOutputCommitterFactory.OtherFactory.class.getName());
        createCommitterFactory(TestPathOutputCommitterFactory.OtherFactory.class, TestPathOutputCommitterFactory.HDFS_PATH, conf);
        createCommitterFactory(TestPathOutputCommitterFactory.OtherFactory.class, TestPathOutputCommitterFactory.HTTP_PATH, conf);
    }

    /**
     * Verify that if the factory class option is "", schema factory
     * resolution still works.
     */
    @Test
    public void testCommitterFactoryEmptyOption() throws Throwable {
        Configuration conf = newBondedConfiguration();
        // set up for the schema factory
        // and then set a global one which overrides the others.
        conf.set(COMMITTER_FACTORY_CLASS, "");
        createCommitterFactory(TestPathOutputCommitterFactory.SimpleCommitterFactory.class, TestPathOutputCommitterFactory.HTTP_PATH, conf);
        // and HDFS, with no schema, falls back to the default
        createCommitterFactory(FileOutputCommitterFactory.class, TestPathOutputCommitterFactory.HDFS_PATH, conf);
    }

    /**
     * Verify that if the committer factory class is unknown, you cannot
     * create committers.
     */
    @Test
    public void testCommitterFactoryUnknown() throws Throwable {
        Configuration conf = new Configuration();
        // set the factory to an unknown class
        conf.set(COMMITTER_FACTORY_CLASS, "unknown");
        intercept(RuntimeException.class, () -> getCommitterFactory(HDFS_PATH, conf));
    }

    /**
     * Verify that if the committer output path is null, you get back
     * a FileOutputCommitter with null output & work paths.
     */
    @Test
    public void testCommitterNullOutputPath() throws Throwable {
        // bind http to schema
        Configuration conf = newBondedConfiguration();
        // then ask committers for a null path
        FileOutputCommitter committer = createCommitter(FileOutputCommitterFactory.class, FileOutputCommitter.class, null, conf);
        Assert.assertNull(committer.getOutputPath());
        Assert.assertNull(committer.getWorkPath());
    }

    /**
     * Verify that if you explicitly name a committer, that takes priority
     * over any filesystem committer.
     */
    @Test
    public void testNamedCommitterFactory() throws Throwable {
        Configuration conf = new Configuration();
        // set up for the schema factory
        conf.set(COMMITTER_FACTORY_CLASS, NAMED_COMMITTER_FACTORY);
        conf.set(NAMED_COMMITTER_CLASS, TestPathOutputCommitterFactory.SimpleCommitter.class.getName());
        TestPathOutputCommitterFactory.SimpleCommitter sc = createCommitter(NamedCommitterFactory.class, TestPathOutputCommitterFactory.SimpleCommitter.class, TestPathOutputCommitterFactory.HDFS_PATH, conf);
        Assert.assertEquals(("Wrong output path from " + sc), TestPathOutputCommitterFactory.HDFS_PATH, sc.getOutputPath());
    }

    /**
     * Verify that if you explicitly name a committer and there's no
     * path, the committer is picked up.
     */
    @Test
    public void testNamedCommitterFactoryNullPath() throws Throwable {
        Configuration conf = new Configuration();
        // set up for the schema factory
        conf.set(COMMITTER_FACTORY_CLASS, NAMED_COMMITTER_FACTORY);
        conf.set(NAMED_COMMITTER_CLASS, TestPathOutputCommitterFactory.SimpleCommitter.class.getName());
        TestPathOutputCommitterFactory.SimpleCommitter sc = createCommitter(NamedCommitterFactory.class, TestPathOutputCommitterFactory.SimpleCommitter.class, null, conf);
        Assert.assertNull(sc.getOutputPath());
    }

    /**
     * Verify that if you explicitly name a committer and there's no
     * path, the committer is picked up.
     */
    @Test
    public void testNamedCommitterNullPath() throws Throwable {
        Configuration conf = new Configuration();
        // set up for the schema factory
        conf.set(COMMITTER_FACTORY_CLASS, NAMED_COMMITTER_FACTORY);
        conf.set(NAMED_COMMITTER_CLASS, TestPathOutputCommitterFactory.SimpleCommitter.class.getName());
        TestPathOutputCommitterFactory.SimpleCommitter sc = createCommitter(TestPathOutputCommitterFactory.SimpleCommitter.class, null, taskAttempt(conf));
        Assert.assertNull(sc.getOutputPath());
    }

    /**
     * Verify that if you explicitly name a committer, that takes priority
     * over any filesystem committer.
     */
    @Test
    public void testFileOutputCommitterFactory() throws Throwable {
        Configuration conf = new Configuration();
        // set up for the schema factory
        conf.set(COMMITTER_FACTORY_CLASS, FILE_COMMITTER_FACTORY);
        conf.set(NAMED_COMMITTER_CLASS, TestPathOutputCommitterFactory.SimpleCommitter.class.getName());
        getCommitterFactory(TestPathOutputCommitterFactory.HDFS_PATH, conf);
        createCommitter(FileOutputCommitterFactory.class, FileOutputCommitter.class, null, conf);
    }

    /**
     * Follow the entire committer chain down and create a new committer from
     * the output format.
     *
     * @throws Throwable
     * 		on a failure.
     */
    @Test
    public void testFileOutputFormatBinding() throws Throwable {
        Configuration conf = newBondedConfiguration();
        conf.set(OUTDIR, TestPathOutputCommitterFactory.HTTP_PATH.toUri().toString());
        TextOutputFormat<String, String> off = new TextOutputFormat();
        TestPathOutputCommitterFactory.SimpleCommitter committer = ((TestPathOutputCommitterFactory.SimpleCommitter) (off.getOutputCommitter(taskAttempt(conf))));
        Assert.assertEquals(("Wrong output path from " + committer), TestPathOutputCommitterFactory.HTTP_PATH, committer.getOutputPath());
    }

    /**
     * Follow the entire committer chain down and create a new committer from
     * the output format.
     *
     * @throws Throwable
     * 		on a failure.
     */
    @Test
    public void testFileOutputFormatBindingNoPath() throws Throwable {
        Configuration conf = new Configuration();
        conf.unset(OUTDIR);
        // set up for the schema factory
        conf.set(COMMITTER_FACTORY_CLASS, NAMED_COMMITTER_FACTORY);
        conf.set(NAMED_COMMITTER_CLASS, TestPathOutputCommitterFactory.SimpleCommitter.class.getName());
        httpToSimpleFactory(conf);
        TextOutputFormat<String, String> off = new TextOutputFormat();
        TestPathOutputCommitterFactory.SimpleCommitter committer = ((TestPathOutputCommitterFactory.SimpleCommitter) (off.getOutputCommitter(taskAttempt(conf))));
        Assert.assertNull(("Output path from " + committer), committer.getOutputPath());
    }

    @Test
    public void testBadCommitterFactory() throws Throwable {
        expectFactoryConstructionFailure(TestPathOutputCommitterFactory.HTTP_COMMITTER_FACTORY);
    }

    @Test
    public void testBoundCommitterWithSchema() throws Throwable {
        // this verifies that a bound committer relays to the underlying committer
        Configuration conf = newBondedConfiguration();
        TestPathOutputCommitter.TaskContext tac = new TestPathOutputCommitter.TaskContext(conf);
        BindingPathOutputCommitter committer = new BindingPathOutputCommitter(TestPathOutputCommitterFactory.HTTP_PATH, tac);
        intercept(IOException.class, "setupJob", () -> committer.setupJob(tac));
    }

    @Test
    public void testBoundCommitterWithDefault() throws Throwable {
        // this verifies that a bound committer relays to the underlying committer
        Configuration conf = newBondedConfiguration();
        TestPathOutputCommitter.TaskContext tac = new TestPathOutputCommitter.TaskContext(conf);
        BindingPathOutputCommitter committer = new BindingPathOutputCommitter(TestPathOutputCommitterFactory.HDFS_PATH, tac);
        Assert.assertEquals(FileOutputCommitter.class, committer.getCommitter().getClass());
    }

    /**
     * A simple committer.
     */
    public static final class SimpleCommitter extends PathOutputCommitter {
        private final Path outputPath;

        public SimpleCommitter(Path outputPath, TaskAttemptContext context) throws IOException {
            super(outputPath, context);
            this.outputPath = outputPath;
        }

        @Override
        public Path getWorkPath() throws IOException {
            return null;
        }

        /**
         * Job setup throws an exception.
         *
         * @param jobContext
         * 		Context of the job
         * @throws IOException
         * 		always
         */
        @Override
        public void setupJob(JobContext jobContext) throws IOException {
            throw new IOException("setupJob");
        }

        @Override
        public void setupTask(TaskAttemptContext taskContext) throws IOException {
        }

        @Override
        public boolean needsTaskCommit(TaskAttemptContext taskContext) throws IOException {
            return false;
        }

        @Override
        public void commitTask(TaskAttemptContext taskContext) throws IOException {
        }

        @Override
        public void abortTask(TaskAttemptContext taskContext) throws IOException {
        }

        @Override
        public Path getOutputPath() {
            return outputPath;
        }
    }

    /**
     * The simple committer factory.
     */
    private static class SimpleCommitterFactory extends PathOutputCommitterFactory.PathOutputCommitterFactory {
        @Override
        public PathOutputCommitter createOutputCommitter(Path outputPath, TaskAttemptContext context) throws IOException {
            return new TestPathOutputCommitterFactory.SimpleCommitter(outputPath, context);
        }
    }

    /**
     * Some other factory.
     */
    private static class OtherFactory extends PathOutputCommitterFactory.PathOutputCommitterFactory {
        /**
         * {@inheritDoc }
         *
         * @param outputPath
         * 		output path. This may be null.
         * @param context
         * 		context
         * @return 
         * @throws IOException
         * 		
         */
        @Override
        public PathOutputCommitter createOutputCommitter(Path outputPath, TaskAttemptContext context) throws IOException {
            return new TestPathOutputCommitterFactory.SimpleCommitter(outputPath, context);
        }
    }
}

