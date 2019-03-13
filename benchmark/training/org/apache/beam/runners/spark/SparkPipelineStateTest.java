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
package org.apache.beam.runners.spark;


import java.io.Serializable;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;


/**
 * This suite tests that various scenarios result in proper states of the pipeline.
 */
public class SparkPipelineStateTest implements Serializable {
    private static class MyCustomException extends RuntimeException {
        MyCustomException(final String message) {
            super(message);
        }
    }

    private transient SparkPipelineOptions options = PipelineOptionsFactory.as(SparkPipelineOptions.class);

    @Rule
    public transient TestName testName = new TestName();

    private static final String FAILED_THE_BATCH_INTENTIONALLY = "Failed the batch intentionally";

    @Test
    public void testStreamingPipelineRunningState() throws Exception {
        testRunningPipeline(getStreamingOptions());
    }

    @Test
    public void testBatchPipelineRunningState() throws Exception {
        testRunningPipeline(getBatchOptions());
    }

    @Test
    public void testStreamingPipelineCanceledState() throws Exception {
        testCanceledPipeline(getStreamingOptions());
    }

    @Test
    public void testBatchPipelineCanceledState() throws Exception {
        testCanceledPipeline(getBatchOptions());
    }

    @Test
    public void testStreamingPipelineFailedState() throws Exception {
        testFailedPipeline(getStreamingOptions());
    }

    @Test
    public void testBatchPipelineFailedState() throws Exception {
        testFailedPipeline(getBatchOptions());
    }

    @Test
    public void testStreamingPipelineTimeoutState() throws Exception {
        testTimeoutPipeline(getStreamingOptions());
    }

    @Test
    public void testBatchPipelineTimeoutState() throws Exception {
        testTimeoutPipeline(getBatchOptions());
    }
}

