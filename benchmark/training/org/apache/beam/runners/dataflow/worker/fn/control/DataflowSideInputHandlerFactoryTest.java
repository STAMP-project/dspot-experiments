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
package org.apache.beam.runners.dataflow.worker.fn.control;


import GlobalWindow.Coder.INSTANCE;
import PTransformTranslation.MULTIMAP_SIDE_INPUT;
import RunnerApi.ExecutableStagePayload.SideInputId;
import RunnerApi.FunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.runners.core.SideInputReader;
import org.apache.beam.runners.dataflow.worker.DataflowPortabilityPCollectionView;
import org.apache.beam.runners.fnexecution.state.StateRequestHandlers;
import org.apache.beam.runners.fnexecution.state.StateRequestHandlers.SideInputHandler;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.transforms.windowing.GlobalWindow;
import org.apache.beam.sdk.util.WindowedValue.FullWindowedValueCoder;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test for {@link DataflowSideInputHandlerFactory}
 */
@RunWith(JUnit4.class)
public final class DataflowSideInputHandlerFactoryTest {
    private static final String TRANSFORM_ID = "transformId";

    private static final String SIDE_INPUT_NAME = "testSideInputId";

    private static final FunctionSpec MULTIMAP_ACCESS = FunctionSpec.newBuilder().setUrn(MULTIMAP_SIDE_INPUT).build();

    private static final byte[] ENCODED_FOO = DataflowSideInputHandlerFactoryTest.encode("foo", StringUtf8Coder.of());

    private static final byte[] ENCODED_FOO2 = DataflowSideInputHandlerFactoryTest.encode("foo2", StringUtf8Coder.of());

    private static final PCollectionView view = DataflowPortabilityPCollectionView.with(new org.apache.beam.sdk.values.TupleTag(DataflowSideInputHandlerFactoryTest.SIDE_INPUT_NAME), FullWindowedValueCoder.of(KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of()), INSTANCE));

    private static final SideInputId sideInputId = SideInputId.newBuilder().setTransformId(DataflowSideInputHandlerFactoryTest.TRANSFORM_ID).setLocalName(DataflowSideInputHandlerFactoryTest.SIDE_INPUT_NAME).build();

    private static SideInputReader fakeSideInputReader;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void invalidSideInputThrowsException() {
        ImmutableMap<String, SideInputReader> sideInputReadersMap = ImmutableMap.<String, SideInputReader>builder().build();
        ImmutableMap<RunnerApi.ExecutableStagePayload.SideInputId, PCollectionView<?>> sideInputIdToPCollectionViewMap = ImmutableMap.<RunnerApi.ExecutableStagePayload.SideInputId, PCollectionView<?>>builder().build();
        DataflowSideInputHandlerFactory factory = DataflowSideInputHandlerFactory.of(sideInputReadersMap, sideInputIdToPCollectionViewMap);
        thrown.expect(Matchers.instanceOf(IllegalStateException.class));
        factory.forSideInput(DataflowSideInputHandlerFactoryTest.TRANSFORM_ID, DataflowSideInputHandlerFactoryTest.SIDE_INPUT_NAME, DataflowSideInputHandlerFactoryTest.MULTIMAP_ACCESS, KvCoder.of(VoidCoder.of(), VoidCoder.of()), INSTANCE);
    }

    @Test
    public void emptyResultForEmptyCollection() {
        ImmutableMap<String, SideInputReader> sideInputReadersMap = ImmutableMap.<String, SideInputReader>builder().put(DataflowSideInputHandlerFactoryTest.TRANSFORM_ID, DataflowSideInputHandlerFactoryTest.fakeSideInputReader).build();
        ImmutableMap<RunnerApi.ExecutableStagePayload.SideInputId, PCollectionView<?>> sideInputIdToPCollectionViewMap = ImmutableMap.<RunnerApi.ExecutableStagePayload.SideInputId, PCollectionView<?>>builder().put(DataflowSideInputHandlerFactoryTest.sideInputId, DataflowSideInputHandlerFactoryTest.view).build();
        DataflowSideInputHandlerFactory factory = DataflowSideInputHandlerFactory.of(sideInputReadersMap, sideInputIdToPCollectionViewMap);
        SideInputHandler<Integer, GlobalWindow> handler = factory.forSideInput(DataflowSideInputHandlerFactoryTest.TRANSFORM_ID, DataflowSideInputHandlerFactoryTest.SIDE_INPUT_NAME, DataflowSideInputHandlerFactoryTest.MULTIMAP_ACCESS, KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of()), INSTANCE);
        Iterable<Integer> result = handler.get(DataflowSideInputHandlerFactoryTest.ENCODED_FOO2, GlobalWindow.INSTANCE);
        MatcherAssert.assertThat(result, Matchers.emptyIterable());
    }

    @Test
    public void multimapSideInputAsIterable() {
        ImmutableMap<String, SideInputReader> sideInputReadersMap = ImmutableMap.<String, SideInputReader>builder().put(DataflowSideInputHandlerFactoryTest.TRANSFORM_ID, DataflowSideInputHandlerFactoryTest.fakeSideInputReader).build();
        ImmutableMap<RunnerApi.ExecutableStagePayload.SideInputId, PCollectionView<?>> sideInputIdToPCollectionViewMap = ImmutableMap.<RunnerApi.ExecutableStagePayload.SideInputId, PCollectionView<?>>builder().put(DataflowSideInputHandlerFactoryTest.sideInputId, DataflowSideInputHandlerFactoryTest.view).build();
        DataflowSideInputHandlerFactory factory = DataflowSideInputHandlerFactory.of(sideInputReadersMap, sideInputIdToPCollectionViewMap);
        StateRequestHandlers.SideInputHandler handler = factory.forSideInput(DataflowSideInputHandlerFactoryTest.TRANSFORM_ID, DataflowSideInputHandlerFactoryTest.SIDE_INPUT_NAME, DataflowSideInputHandlerFactoryTest.MULTIMAP_ACCESS, KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of()), INSTANCE);
        Iterable<String> result = handler.get(DataflowSideInputHandlerFactoryTest.ENCODED_FOO, GlobalWindow.INSTANCE);
        MatcherAssert.assertThat(result, Matchers.containsInAnyOrder(1, 4, 3));
    }
}

