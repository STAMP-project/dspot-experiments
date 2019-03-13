/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.flink.streaming.api.operators;


import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.TaskInfo;
import org.apache.flink.api.common.accumulators.Accumulator;
import org.apache.flink.api.common.functions.AggregateFunction;
import org.apache.flink.api.common.functions.FoldFunction;
import org.apache.flink.api.common.functions.ReduceFunction;
import org.apache.flink.api.common.state.AggregatingStateDescriptor;
import org.apache.flink.api.common.state.FoldingStateDescriptor;
import org.apache.flink.api.common.state.ListState;
import org.apache.flink.api.common.state.ListStateDescriptor;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ReducingStateDescriptor;
import org.apache.flink.api.common.state.StateDescriptor;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeutils.TypeSerializer;
import org.apache.flink.api.common.typeutils.base.ListSerializer;
import org.apache.flink.api.java.typeutils.runtime.kryo.KryoSerializer;
import org.apache.flink.core.fs.Path;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link StreamingRuntimeContext}.
 */
public class StreamingRuntimeContextTest {
    @Test
    public void testValueStateInstantiation() throws Exception {
        final ExecutionConfig config = new ExecutionConfig();
        config.registerKryoType(Path.class);
        final AtomicReference<Object> descriptorCapture = new AtomicReference<>();
        StreamingRuntimeContext context = new StreamingRuntimeContext(StreamingRuntimeContextTest.createDescriptorCapturingMockOp(descriptorCapture, config), StreamingRuntimeContextTest.createMockEnvironment(), Collections.<String, Accumulator<?, ?>>emptyMap());
        ValueStateDescriptor<TaskInfo> descr = new ValueStateDescriptor("name", TaskInfo.class);
        context.getState(descr);
        StateDescriptor<?, ?> descrIntercepted = ((StateDescriptor<?, ?>) (descriptorCapture.get()));
        TypeSerializer<?> serializer = descrIntercepted.getSerializer();
        // check that the Path class is really registered, i.e., the execution config was applied
        Assert.assertTrue((serializer instanceof KryoSerializer));
        Assert.assertTrue(((((KryoSerializer<?>) (serializer)).getKryo().getRegistration(Path.class).getId()) > 0));
    }

    @Test
    public void testReducingStateInstantiation() throws Exception {
        final ExecutionConfig config = new ExecutionConfig();
        config.registerKryoType(Path.class);
        final AtomicReference<Object> descriptorCapture = new AtomicReference<>();
        StreamingRuntimeContext context = new StreamingRuntimeContext(StreamingRuntimeContextTest.createDescriptorCapturingMockOp(descriptorCapture, config), StreamingRuntimeContextTest.createMockEnvironment(), Collections.<String, Accumulator<?, ?>>emptyMap());
        @SuppressWarnings("unchecked")
        ReduceFunction<TaskInfo> reducer = ((ReduceFunction<TaskInfo>) (Mockito.mock(ReduceFunction.class)));
        ReducingStateDescriptor<TaskInfo> descr = new ReducingStateDescriptor("name", reducer, TaskInfo.class);
        context.getReducingState(descr);
        StateDescriptor<?, ?> descrIntercepted = ((StateDescriptor<?, ?>) (descriptorCapture.get()));
        TypeSerializer<?> serializer = descrIntercepted.getSerializer();
        // check that the Path class is really registered, i.e., the execution config was applied
        Assert.assertTrue((serializer instanceof KryoSerializer));
        Assert.assertTrue(((((KryoSerializer<?>) (serializer)).getKryo().getRegistration(Path.class).getId()) > 0));
    }

    @Test
    public void testAggregatingStateInstantiation() throws Exception {
        final ExecutionConfig config = new ExecutionConfig();
        config.registerKryoType(Path.class);
        final AtomicReference<Object> descriptorCapture = new AtomicReference<>();
        StreamingRuntimeContext context = new StreamingRuntimeContext(StreamingRuntimeContextTest.createDescriptorCapturingMockOp(descriptorCapture, config), StreamingRuntimeContextTest.createMockEnvironment(), Collections.<String, Accumulator<?, ?>>emptyMap());
        @SuppressWarnings("unchecked")
        AggregateFunction<String, TaskInfo, String> aggregate = ((AggregateFunction<String, TaskInfo, String>) (Mockito.mock(AggregateFunction.class)));
        AggregatingStateDescriptor<String, TaskInfo, String> descr = new AggregatingStateDescriptor("name", aggregate, TaskInfo.class);
        context.getAggregatingState(descr);
        AggregatingStateDescriptor<?, ?, ?> descrIntercepted = ((AggregatingStateDescriptor<?, ?, ?>) (descriptorCapture.get()));
        TypeSerializer<?> serializer = descrIntercepted.getSerializer();
        // check that the Path class is really registered, i.e., the execution config was applied
        Assert.assertTrue((serializer instanceof KryoSerializer));
        Assert.assertTrue(((((KryoSerializer<?>) (serializer)).getKryo().getRegistration(Path.class).getId()) > 0));
    }

    @Test
    public void testFoldingStateInstantiation() throws Exception {
        final ExecutionConfig config = new ExecutionConfig();
        config.registerKryoType(Path.class);
        final AtomicReference<Object> descriptorCapture = new AtomicReference<>();
        StreamingRuntimeContext context = new StreamingRuntimeContext(StreamingRuntimeContextTest.createDescriptorCapturingMockOp(descriptorCapture, config), StreamingRuntimeContextTest.createMockEnvironment(), Collections.<String, Accumulator<?, ?>>emptyMap());
        @SuppressWarnings("unchecked")
        FoldFunction<String, TaskInfo> folder = ((FoldFunction<String, TaskInfo>) (Mockito.mock(FoldFunction.class)));
        FoldingStateDescriptor<String, TaskInfo> descr = new FoldingStateDescriptor("name", null, folder, TaskInfo.class);
        context.getFoldingState(descr);
        FoldingStateDescriptor<?, ?> descrIntercepted = ((FoldingStateDescriptor<?, ?>) (descriptorCapture.get()));
        TypeSerializer<?> serializer = descrIntercepted.getSerializer();
        // check that the Path class is really registered, i.e., the execution config was applied
        Assert.assertTrue((serializer instanceof KryoSerializer));
        Assert.assertTrue(((((KryoSerializer<?>) (serializer)).getKryo().getRegistration(Path.class).getId()) > 0));
    }

    @Test
    public void testListStateInstantiation() throws Exception {
        final ExecutionConfig config = new ExecutionConfig();
        config.registerKryoType(Path.class);
        final AtomicReference<Object> descriptorCapture = new AtomicReference<>();
        StreamingRuntimeContext context = new StreamingRuntimeContext(StreamingRuntimeContextTest.createDescriptorCapturingMockOp(descriptorCapture, config), StreamingRuntimeContextTest.createMockEnvironment(), Collections.<String, Accumulator<?, ?>>emptyMap());
        ListStateDescriptor<TaskInfo> descr = new ListStateDescriptor("name", TaskInfo.class);
        context.getListState(descr);
        ListStateDescriptor<?> descrIntercepted = ((ListStateDescriptor<?>) (descriptorCapture.get()));
        TypeSerializer<?> serializer = descrIntercepted.getSerializer();
        // check that the Path class is really registered, i.e., the execution config was applied
        Assert.assertTrue((serializer instanceof ListSerializer));
        TypeSerializer<?> elementSerializer = descrIntercepted.getElementSerializer();
        Assert.assertTrue((elementSerializer instanceof KryoSerializer));
        Assert.assertTrue(((((KryoSerializer<?>) (elementSerializer)).getKryo().getRegistration(Path.class).getId()) > 0));
    }

    @Test
    public void testListStateReturnsEmptyListByDefault() throws Exception {
        StreamingRuntimeContext context = new StreamingRuntimeContext(StreamingRuntimeContextTest.createListPlainMockOp(), StreamingRuntimeContextTest.createMockEnvironment(), Collections.<String, Accumulator<?, ?>>emptyMap());
        ListStateDescriptor<String> descr = new ListStateDescriptor("name", String.class);
        ListState<String> state = context.getListState(descr);
        Iterable<String> value = state.get();
        Assert.assertNotNull(value);
        Assert.assertFalse(value.iterator().hasNext());
    }

    @Test
    public void testMapStateInstantiation() throws Exception {
        final ExecutionConfig config = new ExecutionConfig();
        config.registerKryoType(Path.class);
        final AtomicReference<Object> descriptorCapture = new AtomicReference<>();
        StreamingRuntimeContext context = new StreamingRuntimeContext(StreamingRuntimeContextTest.createDescriptorCapturingMockOp(descriptorCapture, config), StreamingRuntimeContextTest.createMockEnvironment(), Collections.<String, Accumulator<?, ?>>emptyMap());
        MapStateDescriptor<String, TaskInfo> descr = new MapStateDescriptor("name", String.class, TaskInfo.class);
        context.getMapState(descr);
        MapStateDescriptor<?, ?> descrIntercepted = ((MapStateDescriptor<?, ?>) (descriptorCapture.get()));
        TypeSerializer<?> valueSerializer = descrIntercepted.getValueSerializer();
        // check that the Path class is really registered, i.e., the execution config was applied
        Assert.assertTrue((valueSerializer instanceof KryoSerializer));
        Assert.assertTrue(((((KryoSerializer<?>) (valueSerializer)).getKryo().getRegistration(Path.class).getId()) > 0));
    }

    @Test
    public void testMapStateReturnsEmptyMapByDefault() throws Exception {
        StreamingRuntimeContext context = new StreamingRuntimeContext(StreamingRuntimeContextTest.createMapPlainMockOp(), StreamingRuntimeContextTest.createMockEnvironment(), Collections.<String, Accumulator<?, ?>>emptyMap());
        MapStateDescriptor<Integer, String> descr = new MapStateDescriptor("name", Integer.class, String.class);
        MapState<Integer, String> state = context.getMapState(descr);
        Iterable<Map.Entry<Integer, String>> value = state.entries();
        Assert.assertNotNull(value);
        Assert.assertFalse(value.iterator().hasNext());
    }
}

