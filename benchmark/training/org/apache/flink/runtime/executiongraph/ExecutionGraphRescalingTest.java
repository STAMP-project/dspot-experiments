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
package org.apache.flink.runtime.executiongraph;


import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.apache.flink.runtime.JobException;
import org.apache.flink.runtime.akka.AkkaUtils;
import org.apache.flink.runtime.blob.VoidBlobWriter;
import org.apache.flink.runtime.checkpoint.StandaloneCheckpointRecoveryFactory;
import org.apache.flink.runtime.executiongraph.restart.NoRestartStrategy;
import org.apache.flink.runtime.jobgraph.JobGraph;
import org.apache.flink.runtime.jobgraph.JobVertex;
import org.apache.flink.runtime.testingUtils.TestingUtils;
import org.apache.flink.util.TestLogger;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class contains tests that verify when rescaling a {@link JobGraph},
 * constructed {@link ExecutionGraph}s are correct.
 */
public class ExecutionGraphRescalingTest extends TestLogger {
    private static final Logger TEST_LOGGER = LoggerFactory.getLogger(ExecutionGraphRescalingTest.class);

    @Test
    public void testExecutionGraphArbitraryDopConstructionTest() throws Exception {
        final Configuration config = new Configuration();
        final int initialParallelism = 5;
        final int maxParallelism = 10;
        final JobVertex[] jobVertices = ExecutionGraphRescalingTest.createVerticesForSimpleBipartiteJobGraph(initialParallelism, maxParallelism);
        final JobGraph jobGraph = new JobGraph(jobVertices);
        ExecutionGraph eg = ExecutionGraphBuilder.buildGraph(null, jobGraph, config, TestingUtils.defaultExecutor(), TestingUtils.defaultExecutor(), new TestingSlotProvider(( ignore) -> new CompletableFuture<>()), Thread.currentThread().getContextClassLoader(), new StandaloneCheckpointRecoveryFactory(), AkkaUtils.getDefaultTimeout(), new NoRestartStrategy(), new UnregisteredMetricsGroup(), VoidBlobWriter.getInstance(), AkkaUtils.getDefaultTimeout(), ExecutionGraphRescalingTest.TEST_LOGGER);
        for (JobVertex jv : jobVertices) {
            Assert.assertThat(jv.getParallelism(), CoreMatchers.is(initialParallelism));
        }
        ExecutionGraphRescalingTest.verifyGeneratedExecutionGraphOfSimpleBitartiteJobGraph(eg, jobVertices);
        // --- verify scaling down works correctly ---
        final int scaleDownParallelism = 1;
        for (JobVertex jv : jobVertices) {
            jv.setParallelism(scaleDownParallelism);
        }
        eg = ExecutionGraphBuilder.buildGraph(null, jobGraph, config, TestingUtils.defaultExecutor(), TestingUtils.defaultExecutor(), new TestingSlotProvider(( ignore) -> new CompletableFuture<>()), Thread.currentThread().getContextClassLoader(), new StandaloneCheckpointRecoveryFactory(), AkkaUtils.getDefaultTimeout(), new NoRestartStrategy(), new UnregisteredMetricsGroup(), VoidBlobWriter.getInstance(), AkkaUtils.getDefaultTimeout(), ExecutionGraphRescalingTest.TEST_LOGGER);
        for (JobVertex jv : jobVertices) {
            Assert.assertThat(jv.getParallelism(), CoreMatchers.is(1));
        }
        ExecutionGraphRescalingTest.verifyGeneratedExecutionGraphOfSimpleBitartiteJobGraph(eg, jobVertices);
        // --- verify scaling up works correctly ---
        final int scaleUpParallelism = 10;
        for (JobVertex jv : jobVertices) {
            jv.setParallelism(scaleUpParallelism);
        }
        eg = ExecutionGraphBuilder.buildGraph(null, jobGraph, config, TestingUtils.defaultExecutor(), TestingUtils.defaultExecutor(), new TestingSlotProvider(( ignore) -> new CompletableFuture<>()), Thread.currentThread().getContextClassLoader(), new StandaloneCheckpointRecoveryFactory(), AkkaUtils.getDefaultTimeout(), new NoRestartStrategy(), new UnregisteredMetricsGroup(), VoidBlobWriter.getInstance(), AkkaUtils.getDefaultTimeout(), ExecutionGraphRescalingTest.TEST_LOGGER);
        for (JobVertex jv : jobVertices) {
            Assert.assertThat(jv.getParallelism(), CoreMatchers.is(scaleUpParallelism));
        }
        ExecutionGraphRescalingTest.verifyGeneratedExecutionGraphOfSimpleBitartiteJobGraph(eg, jobVertices);
    }

    /**
     * Verifies that building an {@link ExecutionGraph} from a {@link JobGraph} with
     * parallelism higher than the maximum parallelism fails.
     */
    @Test
    public void testExecutionGraphConstructionFailsRescaleDopExceedMaxParallelism() throws Exception {
        final Configuration config = new Configuration();
        final int initialParallelism = 1;
        final int maxParallelism = 10;
        final JobVertex[] jobVertices = ExecutionGraphRescalingTest.createVerticesForSimpleBipartiteJobGraph(initialParallelism, maxParallelism);
        final JobGraph jobGraph = new JobGraph(jobVertices);
        for (JobVertex jv : jobVertices) {
            jv.setParallelism((maxParallelism + 1));
        }
        try {
            // this should fail since we set the parallelism to maxParallelism + 1
            ExecutionGraphBuilder.buildGraph(null, jobGraph, config, TestingUtils.defaultExecutor(), TestingUtils.defaultExecutor(), new TestingSlotProvider(( ignore) -> new CompletableFuture<>()), Thread.currentThread().getContextClassLoader(), new StandaloneCheckpointRecoveryFactory(), AkkaUtils.getDefaultTimeout(), new NoRestartStrategy(), new UnregisteredMetricsGroup(), VoidBlobWriter.getInstance(), AkkaUtils.getDefaultTimeout(), ExecutionGraphRescalingTest.TEST_LOGGER);
            Assert.fail("Building the ExecutionGraph with a parallelism higher than the max parallelism should fail.");
        } catch (JobException e) {
            // expected, ignore
        }
    }
}

