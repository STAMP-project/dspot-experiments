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
package org.apache.flink.streaming.api.functions.async;


import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.IterationRuntimeContext;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.metrics.MetricGroup;
import org.apache.flink.metrics.groups.UnregisteredMetricsGroup;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Test cases for {@link RichAsyncFunction}.
 */
public class RichAsyncFunctionTest {
    /**
     * Test the set of iteration runtime context methods in the context of a
     * {@link RichAsyncFunction}.
     */
    @Test
    public void testIterationRuntimeContext() throws Exception {
        RichAsyncFunction<Integer, Integer> function = new RichAsyncFunction<Integer, Integer>() {
            private static final long serialVersionUID = -2023923961609455894L;

            @Override
            public void asyncInvoke(Integer input, ResultFuture<Integer> resultFuture) throws Exception {
                // no op
            }
        };
        int superstepNumber = 42;
        IterationRuntimeContext mockedIterationRuntimeContext = Mockito.mock(IterationRuntimeContext.class);
        Mockito.when(mockedIterationRuntimeContext.getSuperstepNumber()).thenReturn(superstepNumber);
        function.setRuntimeContext(mockedIterationRuntimeContext);
        IterationRuntimeContext iterationRuntimeContext = function.getIterationRuntimeContext();
        Assert.assertEquals(superstepNumber, iterationRuntimeContext.getSuperstepNumber());
        try {
            iterationRuntimeContext.getIterationAggregator("foobar");
            Assert.fail("Expected getIterationAggregator to fail with unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            iterationRuntimeContext.getPreviousIterationAggregate("foobar");
            Assert.fail("Expected getPreviousIterationAggregator to fail with unsupported operation exception");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }

    /**
     * Test the set of runtime context methods in the context of a {@link RichAsyncFunction}.
     */
    @Test
    public void testRuntimeContext() throws Exception {
        RichAsyncFunction<Integer, Integer> function = new RichAsyncFunction<Integer, Integer>() {
            private static final long serialVersionUID = 1707630162838967972L;

            @Override
            public void asyncInvoke(Integer input, ResultFuture<Integer> resultFuture) throws Exception {
                // no op
            }
        };
        final String taskName = "foobarTask";
        final MetricGroup metricGroup = new UnregisteredMetricsGroup();
        final int numberOfParallelSubtasks = 42;
        final int indexOfSubtask = 43;
        final int attemptNumber = 1337;
        final String taskNameWithSubtask = "barfoo";
        final ExecutionConfig executionConfig = Mockito.mock(ExecutionConfig.class);
        final ClassLoader userCodeClassLoader = Mockito.mock(ClassLoader.class);
        RuntimeContext mockedRuntimeContext = Mockito.mock(RuntimeContext.class);
        Mockito.when(mockedRuntimeContext.getTaskName()).thenReturn(taskName);
        Mockito.when(mockedRuntimeContext.getMetricGroup()).thenReturn(metricGroup);
        Mockito.when(mockedRuntimeContext.getNumberOfParallelSubtasks()).thenReturn(numberOfParallelSubtasks);
        Mockito.when(mockedRuntimeContext.getIndexOfThisSubtask()).thenReturn(indexOfSubtask);
        Mockito.when(mockedRuntimeContext.getAttemptNumber()).thenReturn(attemptNumber);
        Mockito.when(mockedRuntimeContext.getTaskNameWithSubtasks()).thenReturn(taskNameWithSubtask);
        Mockito.when(mockedRuntimeContext.getExecutionConfig()).thenReturn(executionConfig);
        Mockito.when(mockedRuntimeContext.getUserCodeClassLoader()).thenReturn(userCodeClassLoader);
        function.setRuntimeContext(mockedRuntimeContext);
        RuntimeContext runtimeContext = function.getRuntimeContext();
        Assert.assertEquals(taskName, runtimeContext.getTaskName());
        Assert.assertEquals(metricGroup, runtimeContext.getMetricGroup());
        Assert.assertEquals(numberOfParallelSubtasks, runtimeContext.getNumberOfParallelSubtasks());
        Assert.assertEquals(indexOfSubtask, runtimeContext.getIndexOfThisSubtask());
        Assert.assertEquals(attemptNumber, runtimeContext.getAttemptNumber());
        Assert.assertEquals(taskNameWithSubtask, runtimeContext.getTaskNameWithSubtasks());
        Assert.assertEquals(executionConfig, runtimeContext.getExecutionConfig());
        Assert.assertEquals(userCodeClassLoader, runtimeContext.getUserCodeClassLoader());
        try {
            runtimeContext.getDistributedCache();
            Assert.fail("Expected getDistributedCached to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
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
            runtimeContext.getReducingState(new org.apache.flink.api.common.state.ReducingStateDescriptor("foobar", new org.apache.flink.api.common.functions.ReduceFunction<Integer>() {
                private static final long serialVersionUID = 2136425961884441050L;

                @Override
                public Integer reduce(Integer value1, Integer value2) throws Exception {
                    return value1;
                }
            }, Integer.class));
            Assert.fail("Expected getReducingState to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getAggregatingState(new org.apache.flink.api.common.state.AggregatingStateDescriptor("foobar", new org.apache.flink.api.common.functions.AggregateFunction<Integer, Integer, Integer>() {
                @Override
                public Integer createAccumulator() {
                    return null;
                }

                @Override
                public Integer add(Integer value, Integer accumulator) {
                    return null;
                }

                @Override
                public Integer getResult(Integer accumulator) {
                    return null;
                }

                @Override
                public Integer merge(Integer a, Integer b) {
                    return null;
                }
            }, Integer.class));
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getFoldingState(new org.apache.flink.api.common.state.FoldingStateDescriptor("foobar", 0, new org.apache.flink.api.common.functions.FoldFunction<Integer, Integer>() {
                @Override
                public Integer fold(Integer accumulator, Integer value) throws Exception {
                    return accumulator;
                }
            }, Integer.class));
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.getMapState(new org.apache.flink.api.common.state.MapStateDescriptor("foobar", Integer.class, String.class));
        } catch (UnsupportedOperationException e) {
            // expected
        }
        try {
            runtimeContext.addAccumulator("foobar", new org.apache.flink.api.common.accumulators.Accumulator<Integer, Integer>() {
                private static final long serialVersionUID = -4673320336846482358L;

                @Override
                public void add(Integer value) {
                    // no op
                }

                @Override
                public Integer getLocalValue() {
                    return null;
                }

                @Override
                public void resetLocal() {
                }

                @Override
                public void merge(org.apache.flink.api.common.accumulators.Accumulator<Integer, Integer> other) {
                }

                @Override
                public org.apache.flink.api.common.accumulators.Accumulator<Integer, Integer> clone() {
                    return null;
                }
            });
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
            runtimeContext.getBroadcastVariableWithInitializer("foobar", new org.apache.flink.api.common.functions.BroadcastVariableInitializer<Object, Object>() {
                @Override
                public Object initializeBroadcastVariable(Iterable<Object> data) {
                    return null;
                }
            });
            Assert.fail("Expected getBroadcastVariableWithInitializer to fail with unsupported operation exception.");
        } catch (UnsupportedOperationException e) {
            // expected
        }
    }
}

