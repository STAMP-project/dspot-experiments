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
package org.apache.flink.streaming.api.operators.windowing.functions;


import InternalWindowFunction.InternalWindowContext;
import ProcessWindowFunction.Context;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.RichAllWindowFunction;
import org.apache.flink.streaming.api.functions.windowing.RichWindowFunction;
import org.apache.flink.streaming.api.operators.OutputTypeConfigurable;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalAggregateProcessAllWindowFunction;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalAggregateProcessWindowFunction;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalIterableAllWindowFunction;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalIterableProcessAllWindowFunction;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalIterableProcessWindowFunction;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalIterableWindowFunction;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalSingleValueAllWindowFunction;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalSingleValueProcessAllWindowFunction;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalSingleValueProcessWindowFunction;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalSingleValueWindowFunction;
import org.apache.flink.streaming.runtime.operators.windowing.functions.InternalWindowFunction;
import org.apache.flink.streaming.util.functions.StreamingFunctionUtils;
import org.apache.flink.util.Collector;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.hamcrest.core.AllOf;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.hamcrest.MockitoHamcrest;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Tests for {@link InternalWindowFunction}.
 */
public class InternalWindowFunctionTest {
    @SuppressWarnings("unchecked")
    @Test
    public void testInternalIterableAllWindowFunction() throws Exception {
        InternalWindowFunctionTest.AllWindowFunctionMock mock = Mockito.mock(InternalWindowFunctionTest.AllWindowFunctionMock.class);
        InternalIterableAllWindowFunction<Long, String, TimeWindow> windowFunction = new InternalIterableAllWindowFunction(mock);
        // check setOutputType
        TypeInformation<String> stringType = BasicTypeInfo.STRING_TYPE_INFO;
        ExecutionConfig execConf = new ExecutionConfig();
        execConf.setParallelism(42);
        StreamingFunctionUtils.setOutputType(windowFunction, stringType, execConf);
        Mockito.verify(mock).setOutputType(stringType, execConf);
        // check open
        Configuration config = new Configuration();
        windowFunction.open(config);
        Mockito.verify(mock).open(config);
        // check setRuntimeContext
        RuntimeContext rCtx = Mockito.mock(RuntimeContext.class);
        windowFunction.setRuntimeContext(rCtx);
        Mockito.verify(mock).setRuntimeContext(rCtx);
        // check apply
        TimeWindow w = Mockito.mock(TimeWindow.class);
        Iterable<Long> i = ((Iterable<Long>) (Mockito.mock(Iterable.class)));
        Collector<String> c = ((Collector<String>) (Mockito.mock(Collector.class)));
        InternalWindowFunction.InternalWindowContext ctx = Mockito.mock(InternalWindowContext.class);
        windowFunction.process(((byte) (0)), w, ctx, i, c);
        Mockito.verify(mock).apply(w, i, c);
        // check close
        windowFunction.close();
        close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInternalIterableProcessAllWindowFunction() throws Exception {
        InternalWindowFunctionTest.ProcessAllWindowFunctionMock mock = Mockito.mock(InternalWindowFunctionTest.ProcessAllWindowFunctionMock.class);
        InternalIterableProcessAllWindowFunction<Long, String, TimeWindow> windowFunction = new InternalIterableProcessAllWindowFunction(mock);
        // check setOutputType
        TypeInformation<String> stringType = BasicTypeInfo.STRING_TYPE_INFO;
        ExecutionConfig execConf = new ExecutionConfig();
        execConf.setParallelism(42);
        StreamingFunctionUtils.setOutputType(windowFunction, stringType, execConf);
        Mockito.verify(mock).setOutputType(stringType, execConf);
        // check open
        Configuration config = new Configuration();
        windowFunction.open(config);
        Mockito.verify(mock).open(config);
        // check setRuntimeContext
        RuntimeContext rCtx = Mockito.mock(RuntimeContext.class);
        windowFunction.setRuntimeContext(rCtx);
        Mockito.verify(mock).setRuntimeContext(rCtx);
        // check apply
        TimeWindow w = Mockito.mock(TimeWindow.class);
        Iterable<Long> i = ((Iterable<Long>) (Mockito.mock(Iterable.class)));
        Collector<String> c = ((Collector<String>) (Mockito.mock(Collector.class)));
        InternalWindowFunction.InternalWindowContext ctx = Mockito.mock(InternalWindowContext.class);
        windowFunction.process(((byte) (0)), w, ctx, i, c);
        Mockito.verify(mock).process(((ProcessAllWindowFunctionMock.Context) (ArgumentMatchers.anyObject())), ArgumentMatchers.eq(i), ArgumentMatchers.eq(c));
        // check close
        windowFunction.close();
        close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInternalIterableWindowFunction() throws Exception {
        InternalWindowFunctionTest.WindowFunctionMock mock = Mockito.mock(InternalWindowFunctionTest.WindowFunctionMock.class);
        InternalIterableWindowFunction<Long, String, Long, TimeWindow> windowFunction = new InternalIterableWindowFunction(mock);
        // check setOutputType
        TypeInformation<String> stringType = BasicTypeInfo.STRING_TYPE_INFO;
        ExecutionConfig execConf = new ExecutionConfig();
        execConf.setParallelism(42);
        StreamingFunctionUtils.setOutputType(windowFunction, stringType, execConf);
        Mockito.verify(mock).setOutputType(stringType, execConf);
        // check open
        Configuration config = new Configuration();
        windowFunction.open(config);
        Mockito.verify(mock).open(config);
        // check setRuntimeContext
        RuntimeContext rCtx = Mockito.mock(RuntimeContext.class);
        windowFunction.setRuntimeContext(rCtx);
        Mockito.verify(mock).setRuntimeContext(rCtx);
        // check apply
        TimeWindow w = Mockito.mock(TimeWindow.class);
        Iterable<Long> i = ((Iterable<Long>) (Mockito.mock(Iterable.class)));
        Collector<String> c = ((Collector<String>) (Mockito.mock(Collector.class)));
        InternalWindowFunction.InternalWindowContext ctx = Mockito.mock(InternalWindowContext.class);
        windowFunction.process(42L, w, ctx, i, c);
        Mockito.verify(mock).apply(ArgumentMatchers.eq(42L), ArgumentMatchers.eq(w), ArgumentMatchers.eq(i), ArgumentMatchers.eq(c));
        // check close
        windowFunction.close();
        close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInternalIterableProcessWindowFunction() throws Exception {
        InternalWindowFunctionTest.ProcessWindowFunctionMock mock = Mockito.mock(InternalWindowFunctionTest.ProcessWindowFunctionMock.class);
        InternalIterableProcessWindowFunction<Long, String, Long, TimeWindow> windowFunction = new InternalIterableProcessWindowFunction(mock);
        // check setOutputType
        TypeInformation<String> stringType = BasicTypeInfo.STRING_TYPE_INFO;
        ExecutionConfig execConf = new ExecutionConfig();
        execConf.setParallelism(42);
        StreamingFunctionUtils.setOutputType(windowFunction, stringType, execConf);
        Mockito.verify(mock).setOutputType(stringType, execConf);
        // check open
        Configuration config = new Configuration();
        windowFunction.open(config);
        Mockito.verify(mock).open(config);
        // check setRuntimeContext
        RuntimeContext rCtx = Mockito.mock(RuntimeContext.class);
        windowFunction.setRuntimeContext(rCtx);
        Mockito.verify(mock).setRuntimeContext(rCtx);
        // check apply
        TimeWindow w = Mockito.mock(TimeWindow.class);
        Iterable<Long> i = ((Iterable<Long>) (Mockito.mock(Iterable.class)));
        Collector<String> c = ((Collector<String>) (Mockito.mock(Collector.class)));
        InternalWindowFunction.InternalWindowContext ctx = Mockito.mock(InternalWindowContext.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ProcessWindowFunctionMock.Context c = ((ProcessWindowFunction.Context) (invocationOnMock.getArguments()[1]));
                c.currentProcessingTime();
                c.currentWatermark();
                c.windowState();
                c.globalState();
                return null;
            }
        }).when(mock).process(ArgumentMatchers.eq(42L), ((ProcessWindowFunctionMock.Context) (ArgumentMatchers.anyObject())), ArgumentMatchers.eq(i), ArgumentMatchers.eq(c));
        windowFunction.process(42L, w, ctx, i, c);
        Mockito.verify(ctx).currentProcessingTime();
        Mockito.verify(ctx).currentWatermark();
        Mockito.verify(ctx).windowState();
        Mockito.verify(ctx).globalState();
        // check close
        windowFunction.close();
        close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInternalSingleValueWindowFunction() throws Exception {
        InternalWindowFunctionTest.WindowFunctionMock mock = Mockito.mock(InternalWindowFunctionTest.WindowFunctionMock.class);
        InternalSingleValueWindowFunction<Long, String, Long, TimeWindow> windowFunction = new InternalSingleValueWindowFunction(mock);
        // check setOutputType
        TypeInformation<String> stringType = BasicTypeInfo.STRING_TYPE_INFO;
        ExecutionConfig execConf = new ExecutionConfig();
        execConf.setParallelism(42);
        StreamingFunctionUtils.setOutputType(windowFunction, stringType, execConf);
        Mockito.verify(mock).setOutputType(stringType, execConf);
        // check open
        Configuration config = new Configuration();
        windowFunction.open(config);
        Mockito.verify(mock).open(config);
        // check setRuntimeContext
        RuntimeContext rCtx = Mockito.mock(RuntimeContext.class);
        windowFunction.setRuntimeContext(rCtx);
        Mockito.verify(mock).setRuntimeContext(rCtx);
        // check apply
        TimeWindow w = Mockito.mock(TimeWindow.class);
        Collector<String> c = ((Collector<String>) (Mockito.mock(Collector.class)));
        InternalWindowFunction.InternalWindowContext ctx = Mockito.mock(InternalWindowContext.class);
        windowFunction.process(42L, w, ctx, 23L, c);
        Mockito.verify(mock).apply(ArgumentMatchers.eq(42L), ArgumentMatchers.eq(w), ((Iterable<Long>) (MockitoHamcrest.argThat(IsIterableContainingInOrder.contains(23L)))), ArgumentMatchers.eq(c));
        // check close
        windowFunction.close();
        close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInternalSingleValueAllWindowFunction() throws Exception {
        InternalWindowFunctionTest.AllWindowFunctionMock mock = Mockito.mock(InternalWindowFunctionTest.AllWindowFunctionMock.class);
        InternalSingleValueAllWindowFunction<Long, String, TimeWindow> windowFunction = new InternalSingleValueAllWindowFunction(mock);
        // check setOutputType
        TypeInformation<String> stringType = BasicTypeInfo.STRING_TYPE_INFO;
        ExecutionConfig execConf = new ExecutionConfig();
        execConf.setParallelism(42);
        StreamingFunctionUtils.setOutputType(windowFunction, stringType, execConf);
        Mockito.verify(mock).setOutputType(stringType, execConf);
        // check open
        Configuration config = new Configuration();
        windowFunction.open(config);
        Mockito.verify(mock).open(config);
        // check setRuntimeContext
        RuntimeContext rCtx = Mockito.mock(RuntimeContext.class);
        windowFunction.setRuntimeContext(rCtx);
        Mockito.verify(mock).setRuntimeContext(rCtx);
        // check apply
        TimeWindow w = Mockito.mock(TimeWindow.class);
        Collector<String> c = ((Collector<String>) (Mockito.mock(Collector.class)));
        InternalWindowFunction.InternalWindowContext ctx = Mockito.mock(InternalWindowContext.class);
        windowFunction.process(((byte) (0)), w, ctx, 23L, c);
        Mockito.verify(mock).apply(ArgumentMatchers.eq(w), ((Iterable<Long>) (MockitoHamcrest.argThat(IsIterableContainingInOrder.contains(23L)))), ArgumentMatchers.eq(c));
        // check close
        windowFunction.close();
        close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInternalSingleValueProcessAllWindowFunction() throws Exception {
        InternalWindowFunctionTest.ProcessAllWindowFunctionMock mock = Mockito.mock(InternalWindowFunctionTest.ProcessAllWindowFunctionMock.class);
        InternalSingleValueProcessAllWindowFunction<Long, String, TimeWindow> windowFunction = new InternalSingleValueProcessAllWindowFunction(mock);
        // check setOutputType
        TypeInformation<String> stringType = BasicTypeInfo.STRING_TYPE_INFO;
        ExecutionConfig execConf = new ExecutionConfig();
        execConf.setParallelism(42);
        StreamingFunctionUtils.setOutputType(windowFunction, stringType, execConf);
        Mockito.verify(mock).setOutputType(stringType, execConf);
        // check open
        Configuration config = new Configuration();
        windowFunction.open(config);
        Mockito.verify(mock).open(config);
        // check setRuntimeContext
        RuntimeContext rCtx = Mockito.mock(RuntimeContext.class);
        windowFunction.setRuntimeContext(rCtx);
        Mockito.verify(mock).setRuntimeContext(rCtx);
        // check apply
        TimeWindow w = Mockito.mock(TimeWindow.class);
        Collector<String> c = ((Collector<String>) (Mockito.mock(Collector.class)));
        InternalWindowFunction.InternalWindowContext ctx = Mockito.mock(InternalWindowContext.class);
        windowFunction.process(((byte) (0)), w, ctx, 23L, c);
        Mockito.verify(mock).process(((ProcessAllWindowFunctionMock.Context) (ArgumentMatchers.anyObject())), ((Iterable<Long>) (MockitoHamcrest.argThat(IsIterableContainingInOrder.contains(23L)))), ArgumentMatchers.eq(c));
        // check close
        windowFunction.close();
        close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInternalSingleValueProcessWindowFunction() throws Exception {
        InternalWindowFunctionTest.ProcessWindowFunctionMock mock = Mockito.mock(InternalWindowFunctionTest.ProcessWindowFunctionMock.class);
        InternalSingleValueProcessWindowFunction<Long, String, Long, TimeWindow> windowFunction = new InternalSingleValueProcessWindowFunction(mock);
        // check setOutputType
        TypeInformation<String> stringType = BasicTypeInfo.STRING_TYPE_INFO;
        ExecutionConfig execConf = new ExecutionConfig();
        execConf.setParallelism(42);
        StreamingFunctionUtils.setOutputType(windowFunction, stringType, execConf);
        Mockito.verify(mock).setOutputType(stringType, execConf);
        // check open
        Configuration config = new Configuration();
        windowFunction.open(config);
        Mockito.verify(mock).open(config);
        // check setRuntimeContext
        RuntimeContext rCtx = Mockito.mock(RuntimeContext.class);
        windowFunction.setRuntimeContext(rCtx);
        Mockito.verify(mock).setRuntimeContext(rCtx);
        // check apply
        TimeWindow w = Mockito.mock(TimeWindow.class);
        Collector<String> c = ((Collector<String>) (Mockito.mock(Collector.class)));
        InternalWindowFunction.InternalWindowContext ctx = Mockito.mock(InternalWindowContext.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ProcessWindowFunctionMock.Context c = ((ProcessWindowFunction.Context) (invocationOnMock.getArguments()[1]));
                c.currentProcessingTime();
                c.currentWatermark();
                c.windowState();
                c.globalState();
                return null;
            }
        }).when(mock).process(ArgumentMatchers.eq(42L), ((ProcessWindowFunctionMock.Context) (ArgumentMatchers.anyObject())), ((Iterable<Long>) (MockitoHamcrest.argThat(IsIterableContainingInOrder.contains(23L)))), ArgumentMatchers.eq(c));
        windowFunction.process(42L, w, ctx, 23L, c);
        Mockito.verify(ctx).currentProcessingTime();
        Mockito.verify(ctx).currentWatermark();
        Mockito.verify(ctx).windowState();
        Mockito.verify(ctx).globalState();
        // check close
        windowFunction.close();
        close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInternalAggregateProcessWindowFunction() throws Exception {
        InternalWindowFunctionTest.AggregateProcessWindowFunctionMock mock = Mockito.mock(InternalWindowFunctionTest.AggregateProcessWindowFunctionMock.class);
        InternalAggregateProcessWindowFunction<Long, Set<Long>, Map<Long, Long>, String, Long, TimeWindow> windowFunction = new InternalAggregateProcessWindowFunction(new org.apache.flink.api.common.functions.AggregateFunction<Long, Set<Long>, Map<Long, Long>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Set<Long> createAccumulator() {
                return new HashSet<>();
            }

            @Override
            public Set<Long> add(Long value, Set<Long> accumulator) {
                accumulator.add(value);
                return accumulator;
            }

            @Override
            public Map<Long, Long> getResult(Set<Long> accumulator) {
                Map<Long, Long> result = new HashMap<>();
                for (Long in : accumulator) {
                    result.put(in, in);
                }
                return result;
            }

            @Override
            public Set<Long> merge(Set<Long> a, Set<Long> b) {
                a.addAll(b);
                return a;
            }
        }, mock);
        // check setOutputType
        TypeInformation<String> stringType = BasicTypeInfo.STRING_TYPE_INFO;
        ExecutionConfig execConf = new ExecutionConfig();
        execConf.setParallelism(42);
        StreamingFunctionUtils.setOutputType(windowFunction, stringType, execConf);
        Mockito.verify(mock).setOutputType(stringType, execConf);
        // check open
        Configuration config = new Configuration();
        windowFunction.open(config);
        Mockito.verify(mock).open(config);
        // check setRuntimeContext
        RuntimeContext rCtx = Mockito.mock(RuntimeContext.class);
        windowFunction.setRuntimeContext(rCtx);
        Mockito.verify(mock).setRuntimeContext(rCtx);
        // check apply
        TimeWindow w = Mockito.mock(TimeWindow.class);
        Collector<String> c = ((Collector<String>) (Mockito.mock(Collector.class)));
        List<Long> args = new LinkedList<>();
        args.add(23L);
        args.add(24L);
        InternalWindowFunction.InternalWindowContext ctx = Mockito.mock(InternalWindowContext.class);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                ProcessWindowFunctionMock.Context c = ((ProcessWindowFunction.Context) (invocationOnMock.getArguments()[1]));
                c.currentProcessingTime();
                c.currentWatermark();
                c.windowState();
                c.globalState();
                return null;
            }
        }).when(mock).process(ArgumentMatchers.eq(42L), ((AggregateProcessWindowFunctionMock.Context) (ArgumentMatchers.anyObject())), ((Iterable) (MockitoHamcrest.argThat(Matchers.containsInAnyOrder(AllOf.allOf(hasEntry(CoreMatchers.is(23L), CoreMatchers.is(23L)), hasEntry(CoreMatchers.is(24L), CoreMatchers.is(24L))))))), ArgumentMatchers.eq(c));
        windowFunction.process(42L, w, ctx, args, c);
        Mockito.verify(ctx).currentProcessingTime();
        Mockito.verify(ctx).currentWatermark();
        Mockito.verify(ctx).windowState();
        Mockito.verify(ctx).globalState();
        // check close
        windowFunction.close();
        close();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testInternalAggregateProcessAllWindowFunction() throws Exception {
        InternalWindowFunctionTest.AggregateProcessAllWindowFunctionMock mock = Mockito.mock(InternalWindowFunctionTest.AggregateProcessAllWindowFunctionMock.class);
        InternalAggregateProcessAllWindowFunction<Long, Set<Long>, Map<Long, Long>, String, TimeWindow> windowFunction = new InternalAggregateProcessAllWindowFunction(new org.apache.flink.api.common.functions.AggregateFunction<Long, Set<Long>, Map<Long, Long>>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Set<Long> createAccumulator() {
                return new HashSet<>();
            }

            @Override
            public Set<Long> add(Long value, Set<Long> accumulator) {
                accumulator.add(value);
                return accumulator;
            }

            @Override
            public Map<Long, Long> getResult(Set<Long> accumulator) {
                Map<Long, Long> result = new HashMap<>();
                for (Long in : accumulator) {
                    result.put(in, in);
                }
                return result;
            }

            @Override
            public Set<Long> merge(Set<Long> a, Set<Long> b) {
                a.addAll(b);
                return a;
            }
        }, mock);
        // check setOutputType
        TypeInformation<String> stringType = BasicTypeInfo.STRING_TYPE_INFO;
        ExecutionConfig execConf = new ExecutionConfig();
        execConf.setParallelism(42);
        StreamingFunctionUtils.setOutputType(windowFunction, stringType, execConf);
        Mockito.verify(mock).setOutputType(stringType, execConf);
        // check open
        Configuration config = new Configuration();
        windowFunction.open(config);
        Mockito.verify(mock).open(config);
        // check setRuntimeContext
        RuntimeContext rCtx = Mockito.mock(RuntimeContext.class);
        windowFunction.setRuntimeContext(rCtx);
        Mockito.verify(mock).setRuntimeContext(rCtx);
        // check apply
        TimeWindow w = Mockito.mock(TimeWindow.class);
        Collector<String> c = ((Collector<String>) (Mockito.mock(Collector.class)));
        List<Long> args = new LinkedList<>();
        args.add(23L);
        args.add(24L);
        InternalWindowFunction.InternalWindowContext ctx = Mockito.mock(InternalWindowContext.class);
        windowFunction.process(((byte) (0)), w, ctx, args, c);
        Mockito.verify(mock).process(((AggregateProcessAllWindowFunctionMock.Context) (ArgumentMatchers.anyObject())), ((Iterable) (MockitoHamcrest.argThat(Matchers.containsInAnyOrder(AllOf.allOf(hasEntry(CoreMatchers.is(23L), CoreMatchers.is(23L)), hasEntry(CoreMatchers.is(24L), CoreMatchers.is(24L))))))), ArgumentMatchers.eq(c));
        // check close
        windowFunction.close();
        close();
    }

    private static class ProcessWindowFunctionMock extends ProcessWindowFunction<Long, String, Long, TimeWindow> implements OutputTypeConfigurable<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public void setOutputType(TypeInformation<String> outTypeInfo, ExecutionConfig executionConfig) {
        }

        @Override
        public void process(Long aLong, Context context, Iterable<Long> elements, Collector<String> out) throws Exception {
        }
    }

    private static class AggregateProcessWindowFunctionMock extends ProcessWindowFunction<Map<Long, Long>, String, Long, TimeWindow> implements OutputTypeConfigurable<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public void setOutputType(TypeInformation<String> outTypeInfo, ExecutionConfig executionConfig) {
        }

        @Override
        public void process(Long aLong, Context context, Iterable<Map<Long, Long>> elements, Collector<String> out) throws Exception {
        }
    }

    private static class AggregateProcessAllWindowFunctionMock extends ProcessAllWindowFunction<Map<Long, Long>, String, TimeWindow> implements OutputTypeConfigurable<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public void setOutputType(TypeInformation<String> outTypeInfo, ExecutionConfig executionConfig) {
        }

        @Override
        public void process(Context context, Iterable<Map<Long, Long>> input, Collector<String> out) throws Exception {
        }
    }

    private static class WindowFunctionMock extends RichWindowFunction<Long, String, Long, TimeWindow> implements OutputTypeConfigurable<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public void setOutputType(TypeInformation<String> outTypeInfo, ExecutionConfig executionConfig) {
        }

        @Override
        public void apply(Long aLong, TimeWindow w, Iterable<Long> input, Collector<String> out) throws Exception {
        }
    }

    private static class AllWindowFunctionMock extends RichAllWindowFunction<Long, String, TimeWindow> implements OutputTypeConfigurable<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public void setOutputType(TypeInformation<String> outTypeInfo, ExecutionConfig executionConfig) {
        }

        @Override
        public void apply(TimeWindow window, Iterable<Long> values, Collector<String> out) throws Exception {
        }
    }

    private static class ProcessAllWindowFunctionMock extends ProcessAllWindowFunction<Long, String, TimeWindow> implements OutputTypeConfigurable<String> {
        private static final long serialVersionUID = 1L;

        @Override
        public void setOutputType(TypeInformation<String> outTypeInfo, ExecutionConfig executionConfig) {
        }

        @Override
        public void process(Context context, Iterable<Long> input, Collector<String> out) throws Exception {
        }
    }
}

