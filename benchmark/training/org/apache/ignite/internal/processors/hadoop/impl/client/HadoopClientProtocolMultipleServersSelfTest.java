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


import java.io.IOException;
import java.util.concurrent.Callable;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.OutputCommitter;
import org.apache.hadoop.mapreduce.OutputFormat;
import org.apache.hadoop.mapreduce.RecordWriter;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.ignite.internal.client.GridServerUnreachableException;
import org.apache.ignite.internal.processors.hadoop.impl.HadoopAbstractSelfTest;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Hadoop client protocol configured with multiple ignite servers tests.
 */
public class HadoopClientProtocolMultipleServersSelfTest extends HadoopAbstractSelfTest {
    /**
     * Input path.
     */
    private static final String PATH_INPUT = "/input";

    /**
     * Job name.
     */
    private static final String JOB_NAME = "myJob";

    /**
     * Rest port.
     */
    private static int restPort;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleAddresses() throws Exception {
        HadoopClientProtocolMultipleServersSelfTest.restPort = HadoopAbstractSelfTest.REST_PORT;
        startGrids(gridCount());
        beforeJob();
        U.sleep(5000);
        checkJobSubmit(configMultipleAddrs(gridCount()));
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testSingleAddress() throws Exception {
        try {
            // Don't use REST_PORT to test connection fails if the only this port is configured
            HadoopClientProtocolMultipleServersSelfTest.restPort = (HadoopAbstractSelfTest.REST_PORT) + 1;
            startGrids(gridCount());
            GridTestUtils.assertThrowsAnyCause(log, new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    checkJobSubmit(configSingleAddress());
                    return null;
                }
            }, GridServerUnreachableException.class, "Failed to connect to any of the servers in list");
        } finally {
            FileSystem fs = FileSystem.get(configSingleAddress());
            fs.close();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMixedAddrs() throws Exception {
        HadoopClientProtocolMultipleServersSelfTest.restPort = HadoopAbstractSelfTest.REST_PORT;
        startGrids(gridCount());
        beforeJob();
        stopGrid(1);
        U.sleep(5000);
        checkJobSubmit(configMixed());
        startGrid(1);
        awaitPartitionMapExchange();
    }

    /**
     * Test mapper.
     */
    public static class TestMapper extends Mapper<Object, Text, Text, IntWritable> {
        /**
         * {@inheritDoc }
         */
        @Override
        public void map(Object key, Text val, Context ctx) throws IOException, InterruptedException {
            // No-op.
        }
    }

    /**
     * Test reducer.
     */
    public static class TestReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        /**
         * {@inheritDoc }
         */
        @Override
        public void reduce(Text key, Iterable<IntWritable> values, Context ctx) throws IOException, InterruptedException {
            // No-op.
        }
    }

    /**
     * Test output formatter.
     */
    public static class OutFormat extends OutputFormat {
        /**
         * {@inheritDoc }
         */
        @Override
        public RecordWriter getRecordWriter(TaskAttemptContext ctx) throws IOException, InterruptedException {
            return null;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void checkOutputSpecs(JobContext ctx) throws IOException, InterruptedException {
            // No-op.
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public OutputCommitter getOutputCommitter(TaskAttemptContext ctx) throws IOException, InterruptedException {
            return null;
        }
    }
}

