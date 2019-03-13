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
package org.apache.flink.cep.operator;


import java.util.List;
import java.util.Map;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.accumulators.Accumulator;
import org.apache.flink.api.common.cache.DistributedCache;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.BroadcastVariableInitializer;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.cep.Event;
import org.apache.flink.cep.functions.PatternProcessFunction;
import org.apache.flink.cep.nfa.NFA;
import org.apache.flink.cep.utils.CepOperatorBuilder;
import org.apache.flink.cep.utils.EventBuilder;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.util.Collector;
import org.apache.flink.util.TestLogger;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Test cases for {@link CepRuntimeContext}.
 */
public class CepRuntimeContextTest extends TestLogger {
    @Test
    public void testCepRuntimeContextIsSetInNFA() throws Exception {
        @SuppressWarnings("unchecked")
        final NFA<Event> mockNFA = Mockito.mock(NFA.class);
        try (OneInputStreamOperatorTestHarness<Event, Map<String, List<Event>>> harness = CepOperatorTestUtilities.getCepTestHarness(CepOperatorBuilder.createOperatorForNFA(mockNFA).build())) {
            harness.open();
            Mockito.verify(mockNFA).open(ArgumentMatchers.any(CepRuntimeContext.class), ArgumentMatchers.any(Configuration.class));
        }
    }

    @Test
    public void testCepRuntimeContextIsSetInProcessFunction() throws Exception {
        final CepRuntimeContextTest.VerifyRuntimeContextProcessFunction processFunction = new CepRuntimeContextTest.VerifyRuntimeContextProcessFunction();
        try (OneInputStreamOperatorTestHarness<Event, Event> harness = CepOperatorTestUtilities.getCepTestHarness(CepOperatorBuilder.createOperatorForNFA(getSingleElementAlwaysTrueNFA()).withFunction(processFunction).build())) {
            harness.open();
            Event record = EventBuilder.event().withName("A").build();
            harness.processElement(record, 0);
            CepRuntimeContextTest.MockProcessFunctionAsserter.assertFunction(processFunction).checkOpenCalled().checkCloseCalled().checkProcessMatchCalled();
        }
    }

    @Test
    public void testCepRuntimeContext() {
        final String taskName = "foobarTask";
        final MetricGroup metricGroup = new UnregisteredMetricsGroup();
        final int numberOfParallelSubtasks = 42;
        final int indexOfSubtask = 43;
        final int attemptNumber = 1337;
        final String taskNameWithSubtask = "barfoo";
        final ExecutionConfig executionConfig = Mockito.mock(ExecutionConfig.class);
        final ClassLoader userCodeClassLoader = Mockito.mock(ClassLoader.class);
        final DistributedCache distributedCache = Mockito.mock(DistributedCache.class);
        RuntimeContext mockedRuntimeContext = Mockito.mock(RuntimeContext.class);
        Mockito.when(mockedRuntimeContext.getTaskName()).thenReturn(taskName);
        Mockito.when(mockedRuntimeContext.getMetricGroup()).thenReturn(metricGroup);
        Mockito.when(mockedRuntimeContext.getNumberOfParallelSubtasks()).thenReturn(numberOfParallelSubtasks);
        Mockito.when(mockedRuntimeContext.getIndexOfThisSubtask()).thenReturn(indexOfSubtask);
        Mockito.when(mockedRuntimeContext.getAttemptNumber()).thenReturn(attemptNumber);
        Mockito.when(mockedRuntimeContext.getTaskNameWithSubtasks()).thenReturn(taskNameWithSubtask);
        Mockito.when(mockedRuntimeContext.getExecutionConfig()).thenReturn(executionConfig);
        Mockito.when(mockedRuntimeContext.getUserCodeClassLoader()).thenReturn(userCodeClassLoader);
        Mockito.when(mockedRuntimeContext.getDistributedCache()).thenReturn(distributedCache);
        RuntimeContext runtimeContext = new CepRuntimeContext(mockedRuntimeContext);
        Assert.assertEquals(taskName, runtimeContext.getTaskName());
        Assert.assertEquals(metricGroup, runtimeContext.getMetricGroup());
        Assert.assertEquals(numberOfParallelSubtasks, runtimeContext.getNumberOfParallelSubtasks());
        Assert.assertEquals(indexOfSubtask, runtimeContext.getIndexOfThisSubtask());
        Assert.assertEquals(attemptNumber, runtimeContext.getAttemptNumber());
        Assert.assertEquals(taskNameWithSubtask, runtimeContext.getTaskNameWithSubtasks());
        Assert.assertEquals(executionConfig, runtimeContext.getExecutionConfig());
        Assert.assertEquals(userCodeClassLoader, runtimeContext.getUserCodeClassLoader());
        Assert.assertEquals(distributedCache, runtimeContext.getDistributedCache());
        try {
            runtimeContext.getState(new org.apache.flink.api.common.state.ValueStateDescriptor("foobar", Integer.class, 42));
            Assert.fail("Expected getState to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getListState(new org.apache.flink.api.common.state.ListStateDescriptor("foobar", Integer.class));
            Assert.fail("Expected getListState to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getReducingState(new org.apache.flink.api.common.state.ReducingStateDescriptor("foobar", Mockito.mock(ReduceFunction.class), Integer.class));
            Assert.fail("Expected getReducingState to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getAggregatingState(new org.apache.flink.api.common.state.AggregatingStateDescriptor("foobar", Mockito.mock(AggregateFunction.class), Integer.class));
            Assert.fail("Expected getAggregatingState to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getFoldingState(new org.apache.flink.api.common.state.FoldingStateDescriptor("foobar", 0, Mockito.mock(FoldFunction.class), Integer.class));
            Assert.fail("Expected getFoldingState to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getMapState(new org.apache.flink.api.common.state.MapStateDescriptor("foobar", Integer.class, String.class));
            Assert.fail("Expected getMapState to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.addAccumulator("foobar", Mockito.mock(Accumulator.class));
            Assert.fail("Expected addAccumulator to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getAccumulator("foobar");
            Assert.fail("Expected getAccumulator to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getAllAccumulators();
            Assert.fail("Expected getAllAccumulators to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getIntCounter("foobar");
            Assert.fail("Expected getIntCounter to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getLongCounter("foobar");
            Assert.fail("Expected getLongCounter to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getDoubleCounter("foobar");
            Assert.fail("Expected getDoubleCounter to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getHistogram("foobar");
            Assert.fail("Expected getHistogram to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.hasBroadcastVariable("foobar");
            Assert.fail("Expected hasBroadcastVariable to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getBroadcastVariable("foobar");
            Assert.fail("Expected getBroadcastVariable to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getBroadcastVariableWithInitializer("foobar", Mockito.mock(BroadcastVariableInitializer.class));
            Assert.fail("Expected getBroadcastVariableWithInitializer to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    /* Test Utils */
    static class MockProcessFunctionAsserter {
        private final CepRuntimeContextTest.VerifyRuntimeContextProcessFunction function;

        static CepRuntimeContextTest.MockProcessFunctionAsserter assertFunction(CepRuntimeContextTest.VerifyRuntimeContextProcessFunction function) {
            return new CepRuntimeContextTest.MockProcessFunctionAsserter(function);
        }

        private MockProcessFunctionAsserter(CepRuntimeContextTest.VerifyRuntimeContextProcessFunction function) {
            this.function = function;
        }

        CepRuntimeContextTest.MockProcessFunctionAsserter checkOpenCalled() {
            Assert.assertThat(function.openCalled, Is.is(true));
            return this;
        }

        CepRuntimeContextTest.MockProcessFunctionAsserter checkCloseCalled() {
            Assert.assertThat(function.openCalled, Is.is(true));
            return this;
        }

        CepRuntimeContextTest.MockProcessFunctionAsserter checkProcessMatchCalled() {
            Assert.assertThat(function.processMatchCalled, Is.is(true));
            return this;
        }
    }

    private static class VerifyRuntimeContextProcessFunction extends PatternProcessFunction<Event, Event> {
        boolean openCalled = false;

        boolean closeCalled = false;

        boolean processMatchCalled = false;

        @Override
        public void open(Configuration parameters) throws Exception {
            super.open(parameters);
            verifyContext();
            openCalled = true;
        }

        private void verifyContext() {
            if (!((getRuntimeContext()) instanceof CepRuntimeContext)) {
                Assert.fail("Runtime context was not wrapped in CepRuntimeContext");
            }
        }

        @Override
        public void close() throws Exception {
            super.close();
            verifyContext();
            closeCalled = true;
        }

        @Override
        public void processMatch(Map<String, List<Event>> match, Context ctx, Collector<Event> out) throws Exception {
            verifyContext();
            processMatchCalled = true;
        }
    }
}

