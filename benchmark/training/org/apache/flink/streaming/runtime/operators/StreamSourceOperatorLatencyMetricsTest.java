/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.runtime.operators;


import MetricOptions.LATENCY_INTERVAL;
import java.util.List;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.execution.Environment;
import org.apache.flink.runtime.operators.testutils.MockEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.operators.StreamSource;
import org.apache.flink.streaming.runtime.tasks.TestProcessingTimeService;
import org.apache.flink.util.TestLogger;
import org.junit.Test;


/**
 * Tests for the emission of latency markers by {@link StreamSource} operators.
 */
public class StreamSourceOperatorLatencyMetricsTest extends TestLogger {
    private static final long maxProcessingTime = 100L;

    private static final long latencyMarkInterval = 10L;

    /**
     * Verifies that by default no latency metrics are emitted.
     */
    @Test
    public void testLatencyMarkEmissionDisabled() throws Exception {
        testLatencyMarkEmission(0, ( operator, timeProvider) -> {
            StreamSourceOperatorLatencyMetricsTest.setupSourceOperator(operator, new ExecutionConfig(), MockEnvironment.builder().build(), timeProvider);
        });
    }

    /**
     * Verifies that latency metrics can be enabled via the {@link ExecutionConfig}.
     */
    @Test
    public void testLatencyMarkEmissionEnabledViaExecutionConfig() throws Exception {
        testLatencyMarkEmission((((int) ((StreamSourceOperatorLatencyMetricsTest.maxProcessingTime) / (StreamSourceOperatorLatencyMetricsTest.latencyMarkInterval))) + 1), ( operator, timeProvider) -> {
            ExecutionConfig executionConfig = new ExecutionConfig();
            executionConfig.setLatencyTrackingInterval(StreamSourceOperatorLatencyMetricsTest.latencyMarkInterval);
            StreamSourceOperatorLatencyMetricsTest.setupSourceOperator(operator, executionConfig, MockEnvironment.builder().build(), timeProvider);
        });
    }

    /**
     * Verifies that latency metrics can be enabled via the configuration.
     */
    @Test
    public void testLatencyMarkEmissionEnabledViaFlinkConfig() throws Exception {
        testLatencyMarkEmission((((int) ((StreamSourceOperatorLatencyMetricsTest.maxProcessingTime) / (StreamSourceOperatorLatencyMetricsTest.latencyMarkInterval))) + 1), ( operator, timeProvider) -> {
            Configuration tmConfig = new Configuration();
            tmConfig.setLong(LATENCY_INTERVAL, StreamSourceOperatorLatencyMetricsTest.latencyMarkInterval);
            Environment env = MockEnvironment.builder().setTaskManagerRuntimeInfo(new org.apache.flink.runtime.util.TestingTaskManagerRuntimeInfo(tmConfig)).build();
            StreamSourceOperatorLatencyMetricsTest.setupSourceOperator(operator, new ExecutionConfig(), env, timeProvider);
        });
    }

    /**
     * Verifies that latency metrics can be enabled via the {@link ExecutionConfig} even if they are disabled via
     * the configuration.
     */
    @Test
    public void testLatencyMarkEmissionEnabledOverrideViaExecutionConfig() throws Exception {
        testLatencyMarkEmission((((int) ((StreamSourceOperatorLatencyMetricsTest.maxProcessingTime) / (StreamSourceOperatorLatencyMetricsTest.latencyMarkInterval))) + 1), ( operator, timeProvider) -> {
            ExecutionConfig executionConfig = new ExecutionConfig();
            executionConfig.setLatencyTrackingInterval(StreamSourceOperatorLatencyMetricsTest.latencyMarkInterval);
            Configuration tmConfig = new Configuration();
            tmConfig.setLong(LATENCY_INTERVAL, 0L);
            Environment env = MockEnvironment.builder().setTaskManagerRuntimeInfo(new org.apache.flink.runtime.util.TestingTaskManagerRuntimeInfo(tmConfig)).build();
            StreamSourceOperatorLatencyMetricsTest.setupSourceOperator(operator, executionConfig, env, timeProvider);
        });
    }

    /**
     * Verifies that latency metrics can be disabled via the {@link ExecutionConfig} even if they are enabled via
     * the configuration.
     */
    @Test
    public void testLatencyMarkEmissionDisabledOverrideViaExecutionConfig() throws Exception {
        testLatencyMarkEmission(0, ( operator, timeProvider) -> {
            Configuration tmConfig = new Configuration();
            tmConfig.setLong(LATENCY_INTERVAL, StreamSourceOperatorLatencyMetricsTest.latencyMarkInterval);
            Environment env = MockEnvironment.builder().setTaskManagerRuntimeInfo(new org.apache.flink.runtime.util.TestingTaskManagerRuntimeInfo(tmConfig)).build();
            ExecutionConfig executionConfig = new ExecutionConfig();
            executionConfig.setLatencyTrackingInterval(0);
            StreamSourceOperatorLatencyMetricsTest.setupSourceOperator(operator, executionConfig, env, timeProvider);
        });
    }

    private interface OperatorSetupOperation {
        void setupSourceOperator(StreamSource<Long, ?> operator, TestProcessingTimeService testProcessingTimeService);
    }

    // ------------------------------------------------------------------------
    private static final class ProcessingTimeServiceSource implements SourceFunction<Long> {
        private final TestProcessingTimeService processingTimeService;

        private final List<Long> processingTimes;

        private boolean cancelled = false;

        private ProcessingTimeServiceSource(TestProcessingTimeService processingTimeService, List<Long> processingTimes) {
            this.processingTimeService = processingTimeService;
            this.processingTimes = processingTimes;
        }

        @Override
        public void run(SourceContext<Long> ctx) throws Exception {
            for (Long processingTime : processingTimes) {
                if (cancelled) {
                    break;
                }
                processingTimeService.setCurrentTime(processingTime);
            }
        }

        @Override
        public void cancel() {
            cancelled = true;
        }
    }
}

