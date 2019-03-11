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
package org.apache.beam.runners.flink.translation.functions;


import GlobalWindow.Coder.INSTANCE;
import PTransformTranslation.ITERABLE_SIDE_INPUT;
import PTransformTranslation.MULTIMAP_SIDE_INPUT;
import PaneInfo.NO_FIRING;
import RunnerApi.FunctionSpec;
import RunnerApi.PCollection;
import RunnerApi.PTransform;
import java.util.Arrays;
import java.util.Collections;
import org.apache.beam.runners.core.construction.graph.ExecutableStage;
import org.apache.beam.runners.core.construction.graph.PipelineNode;
import org.apache.beam.runners.core.construction.graph.SideInputReference;
import org.apache.beam.runners.fnexecution.state.StateRequestHandlers.SideInputHandler;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.transforms.windowing.GlobalWindow;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow.IntervalWindowCoder;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link FlinkBatchSideInputHandlerFactory}.
 */
@RunWith(JUnit4.class)
public class FlinkBatchSideInputHandlerFactoryTest {
    private static final String TRANSFORM_ID = "transform-id";

    private static final String SIDE_INPUT_NAME = "side-input";

    private static final String COLLECTION_ID = "collection";

    private static final FunctionSpec MULTIMAP_ACCESS = FunctionSpec.newBuilder().setUrn(MULTIMAP_SIDE_INPUT).build();

    private static final FunctionSpec ITERABLE_ACCESS = FunctionSpec.newBuilder().setUrn(ITERABLE_SIDE_INPUT).build();

    private static final ExecutableStage EXECUTABLE_STAGE = FlinkBatchSideInputHandlerFactoryTest.createExecutableStage(Arrays.asList(SideInputReference.of(PipelineNode.pTransform(FlinkBatchSideInputHandlerFactoryTest.TRANSFORM_ID, PTransform.getDefaultInstance()), FlinkBatchSideInputHandlerFactoryTest.SIDE_INPUT_NAME, PipelineNode.pCollection(FlinkBatchSideInputHandlerFactoryTest.COLLECTION_ID, PCollection.getDefaultInstance()))));

    private static final byte[] ENCODED_NULL = FlinkBatchSideInputHandlerFactoryTest.encode(null, VoidCoder.of());

    private static final byte[] ENCODED_FOO = FlinkBatchSideInputHandlerFactoryTest.encode("foo", StringUtf8Coder.of());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private RuntimeContext context;

    @Test
    public void invalidSideInputThrowsException() {
        ExecutableStage stage = FlinkBatchSideInputHandlerFactoryTest.createExecutableStage(Collections.emptyList());
        FlinkBatchSideInputHandlerFactory factory = FlinkBatchSideInputHandlerFactory.forStage(stage, context);
        thrown.expect(Matchers.instanceOf(IllegalArgumentException.class));
        factory.forSideInput("transform-id", "side-input", FlinkBatchSideInputHandlerFactoryTest.MULTIMAP_ACCESS, KvCoder.of(VoidCoder.of(), VoidCoder.of()), INSTANCE);
    }

    @Test
    public void emptyResultForEmptyCollection() {
        FlinkBatchSideInputHandlerFactory factory = FlinkBatchSideInputHandlerFactory.forStage(FlinkBatchSideInputHandlerFactoryTest.EXECUTABLE_STAGE, context);
        SideInputHandler<Integer, GlobalWindow> handler = factory.forSideInput(FlinkBatchSideInputHandlerFactoryTest.TRANSFORM_ID, FlinkBatchSideInputHandlerFactoryTest.SIDE_INPUT_NAME, FlinkBatchSideInputHandlerFactoryTest.MULTIMAP_ACCESS, KvCoder.of(VoidCoder.of(), VarIntCoder.of()), INSTANCE);
        // We never populated the broadcast variable for "side-input", so the mock will return an empty
        // list.
        Iterable<Integer> result = handler.get(FlinkBatchSideInputHandlerFactoryTest.ENCODED_NULL, GlobalWindow.INSTANCE);
        MatcherAssert.assertThat(result, Matchers.emptyIterable());
    }

    @Test
    public void singleElementForCollection() {
        Mockito.when(context.getBroadcastVariable(FlinkBatchSideInputHandlerFactoryTest.COLLECTION_ID)).thenReturn(Arrays.asList(WindowedValue.valueInGlobalWindow(KV.<Void, Integer>of(null, 3))));
        FlinkBatchSideInputHandlerFactory factory = FlinkBatchSideInputHandlerFactory.forStage(FlinkBatchSideInputHandlerFactoryTest.EXECUTABLE_STAGE, context);
        SideInputHandler<Integer, GlobalWindow> handler = factory.forSideInput(FlinkBatchSideInputHandlerFactoryTest.TRANSFORM_ID, FlinkBatchSideInputHandlerFactoryTest.SIDE_INPUT_NAME, FlinkBatchSideInputHandlerFactoryTest.MULTIMAP_ACCESS, KvCoder.of(VoidCoder.of(), VarIntCoder.of()), INSTANCE);
        Iterable<Integer> result = handler.get(FlinkBatchSideInputHandlerFactoryTest.ENCODED_NULL, GlobalWindow.INSTANCE);
        MatcherAssert.assertThat(result, Matchers.contains(3));
    }

    @Test
    public void groupsValuesByKey() {
        Mockito.when(context.getBroadcastVariable(FlinkBatchSideInputHandlerFactoryTest.COLLECTION_ID)).thenReturn(Arrays.asList(WindowedValue.valueInGlobalWindow(KV.of("foo", 2)), WindowedValue.valueInGlobalWindow(KV.of("bar", 3)), WindowedValue.valueInGlobalWindow(KV.of("foo", 5))));
        FlinkBatchSideInputHandlerFactory factory = FlinkBatchSideInputHandlerFactory.forStage(FlinkBatchSideInputHandlerFactoryTest.EXECUTABLE_STAGE, context);
        SideInputHandler<Integer, GlobalWindow> handler = factory.forSideInput(FlinkBatchSideInputHandlerFactoryTest.TRANSFORM_ID, FlinkBatchSideInputHandlerFactoryTest.SIDE_INPUT_NAME, FlinkBatchSideInputHandlerFactoryTest.MULTIMAP_ACCESS, KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of()), INSTANCE);
        Iterable<Integer> result = handler.get(FlinkBatchSideInputHandlerFactoryTest.ENCODED_FOO, GlobalWindow.INSTANCE);
        MatcherAssert.assertThat(result, Matchers.containsInAnyOrder(2, 5));
    }

    @Test
    public void groupsValuesByWindowAndKey() {
        Instant instantA = toInstant();
        Instant instantB = toInstant();
        Instant instantC = toInstant();
        IntervalWindow windowA = new IntervalWindow(instantA, instantB);
        IntervalWindow windowB = new IntervalWindow(instantB, instantC);
        Mockito.when(context.getBroadcastVariable(FlinkBatchSideInputHandlerFactoryTest.COLLECTION_ID)).thenReturn(Arrays.asList(WindowedValue.of(KV.of("foo", 1), instantA, windowA, NO_FIRING), WindowedValue.of(KV.of("bar", 2), instantA, windowA, NO_FIRING), WindowedValue.of(KV.of("foo", 3), instantA, windowA, NO_FIRING), WindowedValue.of(KV.of("foo", 4), instantB, windowB, NO_FIRING), WindowedValue.of(KV.of("bar", 5), instantB, windowB, NO_FIRING), WindowedValue.of(KV.of("foo", 6), instantB, windowB, NO_FIRING)));
        FlinkBatchSideInputHandlerFactory factory = FlinkBatchSideInputHandlerFactory.forStage(FlinkBatchSideInputHandlerFactoryTest.EXECUTABLE_STAGE, context);
        SideInputHandler<Integer, IntervalWindow> handler = factory.forSideInput(FlinkBatchSideInputHandlerFactoryTest.TRANSFORM_ID, FlinkBatchSideInputHandlerFactoryTest.SIDE_INPUT_NAME, FlinkBatchSideInputHandlerFactoryTest.MULTIMAP_ACCESS, KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of()), IntervalWindowCoder.of());
        Iterable<Integer> resultA = handler.get(FlinkBatchSideInputHandlerFactoryTest.ENCODED_FOO, windowA);
        Iterable<Integer> resultB = handler.get(FlinkBatchSideInputHandlerFactoryTest.ENCODED_FOO, windowB);
        MatcherAssert.assertThat(resultA, Matchers.containsInAnyOrder(1, 3));
        MatcherAssert.assertThat(resultB, Matchers.containsInAnyOrder(4, 6));
    }

    @Test
    public void iterableAccessPattern() {
        Instant instantA = toInstant();
        Instant instantB = toInstant();
        Instant instantC = toInstant();
        IntervalWindow windowA = new IntervalWindow(instantA, instantB);
        IntervalWindow windowB = new IntervalWindow(instantB, instantC);
        Mockito.when(context.getBroadcastVariable(FlinkBatchSideInputHandlerFactoryTest.COLLECTION_ID)).thenReturn(Arrays.asList(WindowedValue.of(1, instantA, windowA, NO_FIRING), WindowedValue.of(2, instantA, windowA, NO_FIRING), WindowedValue.of(3, instantB, windowB, NO_FIRING), WindowedValue.of(4, instantB, windowB, NO_FIRING)));
        FlinkBatchSideInputHandlerFactory factory = FlinkBatchSideInputHandlerFactory.forStage(FlinkBatchSideInputHandlerFactoryTest.EXECUTABLE_STAGE, context);
        SideInputHandler<Integer, IntervalWindow> handler = factory.forSideInput(FlinkBatchSideInputHandlerFactoryTest.TRANSFORM_ID, FlinkBatchSideInputHandlerFactoryTest.SIDE_INPUT_NAME, FlinkBatchSideInputHandlerFactoryTest.ITERABLE_ACCESS, VarIntCoder.of(), IntervalWindowCoder.of());
        Iterable<Integer> resultA = handler.get(null, windowA);
        Iterable<Integer> resultB = handler.get(null, windowB);
        MatcherAssert.assertThat(resultA, Matchers.containsInAnyOrder(1, 2));
        MatcherAssert.assertThat(resultB, Matchers.containsInAnyOrder(3, 4));
    }
}

