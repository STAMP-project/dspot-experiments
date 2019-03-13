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


import org.apache.hadoop.conf.Configurable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.serializer.WritableSerialization;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.JobConfigurable;
import org.apache.hadoop.mapred.lib.HashPartitioner;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.ignite.internal.processors.hadoop.state.HadoopMapReduceEmbeddedSelfTestState;
import org.junit.Test;

import static org.apache.hadoop.mapred.TextOutputFormat.<init>;


/**
 * Tests map-reduce execution with embedded mode.
 */
public class HadoopMapReduceEmbeddedSelfTest extends HadoopMapReduceTest {
    /**
     *
     *
     * @throws Exception
     * 		If fails.
     */
    @Test
    public void testMultiReducerWholeMapReduceExecution() throws Exception {
        checkMultiReducerWholeMapReduceExecution(false);
    }

    /**
     *
     *
     * @throws Exception
     * 		If fails.
     */
    @Test
    public void testMultiReducerWholeMapReduceExecutionStriped() throws Exception {
        checkMultiReducerWholeMapReduceExecution(true);
    }

    /**
     * Custom serialization class that inherits behaviour of native {@link WritableSerialization}.
     */
    protected static class CustomSerialization extends WritableSerialization {
        @Override
        public void setConf(Configuration conf) {
            super.setConf(conf);
            HadoopMapReduceEmbeddedSelfTestState.flags.put("serializationWasConfigured", true);
        }
    }

    /**
     * Custom implementation of Partitioner in v1 API.
     */
    private static class CustomV1Partitioner extends HashPartitioner {
        /**
         * {@inheritDoc }
         */
        @Override
        public void configure(JobConf job) {
            HadoopMapReduceEmbeddedSelfTestState.flags.put("partitionerWasConfigured", true);
        }
    }

    /**
     * Custom implementation of Partitioner in v2 API.
     */
    private static class CustomV2Partitioner extends org.apache.hadoop.mapreduce.lib.partition.HashPartitioner implements Configurable {
        /**
         * {@inheritDoc }
         */
        @Override
        public void setConf(Configuration conf) {
            HadoopMapReduceEmbeddedSelfTestState.flags.put("partitionerWasConfigured", true);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Configuration getConf() {
            return null;
        }
    }

    /**
     * Custom implementation of InputFormat in v2 API.
     */
    private static class CustomV2InputFormat extends TextInputFormat implements Configurable {
        /**
         * {@inheritDoc }
         */
        @Override
        public void setConf(Configuration conf) {
            HadoopMapReduceEmbeddedSelfTestState.flags.put("inputFormatWasConfigured", true);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Configuration getConf() {
            return null;
        }
    }

    /**
     * Custom implementation of OutputFormat in v2 API.
     */
    private static class CustomV2OutputFormat extends TextOutputFormat implements Configurable {
        /**
         * {@inheritDoc }
         */
        @Override
        public void setConf(Configuration conf) {
            HadoopMapReduceEmbeddedSelfTestState.flags.put("outputFormatWasConfigured", true);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Configuration getConf() {
            return null;
        }
    }

    /**
     * Custom implementation of InputFormat in v1 API.
     */
    private static class CustomV1InputFormat extends org.apache.hadoop.mapred.TextInputFormat {
        /**
         * {@inheritDoc }
         */
        @Override
        public void configure(JobConf job) {
            super.configure(job);
            HadoopMapReduceEmbeddedSelfTestState.flags.put("inputFormatWasConfigured", true);
        }
    }

    /**
     * Custom implementation of OutputFormat in v1 API.
     */
    private static class CustomV1OutputFormat extends org.apache.hadoop.mapred.TextOutputFormat implements JobConfigurable {
        /**
         * {@inheritDoc }
         */
        @Override
        public void configure(JobConf job) {
            HadoopMapReduceEmbeddedSelfTestState.flags.put("outputFormatWasConfigured", true);
        }
    }
}

