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
package org.apache.beam.runners.dataflow.worker.graph;


import BeamFnApi.RegisterRequest;
import GlobalWindow.Coder.INSTANCE;
import Nodes.ExecutionLocation;
import Nodes.ExecutionLocation.UNKNOWN;
import com.google.api.services.dataflow.model.InstructionOutput;
import com.google.api.services.dataflow.model.ParallelInstruction;
import com.google.api.services.dataflow.model.SideInputInfo;
import java.util.Map;
import org.apache.beam.model.fnexecution.v1.BeamFnApi;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.model.pipeline.v1.RunnerApi.FunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.SdkFunctionSpec;
import org.apache.beam.runners.dataflow.worker.DataflowPortabilityPCollectionView;
import org.apache.beam.runners.dataflow.worker.NameContextsForTests;
import org.apache.beam.runners.dataflow.worker.counters.NameContext;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.FetchAndFilterStreamingSideInputsNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.InstructionOutputNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.OperationNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.OutputReceiverNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.ParallelInstructionNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.RegisterRequestNode;
import org.apache.beam.runners.dataflow.worker.util.common.worker.Operation;
import org.apache.beam.runners.dataflow.worker.util.common.worker.OutputReceiver;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.util.WindowedValue.FullWindowedValueCoder;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for {@link Nodes}.
 */
@RunWith(JUnit4.class)
public class NodesTest {
    private static final String PCOLLECTION_ID = "fakeId";

    @Test
    public void testParallelInstructionNode() {
        ParallelInstruction param = new ParallelInstruction();
        Nodes.ExecutionLocation location = ExecutionLocation.UNKNOWN;
        Assert.assertSame(param, ParallelInstructionNode.create(param, location).getParallelInstruction());
        Assert.assertSame(UNKNOWN, ParallelInstructionNode.create(param, location).getExecutionLocation());
        Assert.assertNotEquals(ParallelInstructionNode.create(param, location), ParallelInstructionNode.create(param, location));
    }

    @Test
    public void testInstructionOutputNode() {
        InstructionOutput param = new InstructionOutput();
        Assert.assertSame(param, InstructionOutputNode.create(param, NodesTest.PCOLLECTION_ID).getInstructionOutput());
        Assert.assertNotEquals(InstructionOutputNode.create(param, NodesTest.PCOLLECTION_ID), InstructionOutputNode.create(param, NodesTest.PCOLLECTION_ID));
    }

    @Test
    public void testOutputReceiverNode() {
        OutputReceiver receiver = new OutputReceiver();
        Coder<?> coder = StringUtf8Coder.of();
        Assert.assertSame(receiver, OutputReceiverNode.create(receiver, coder, NodesTest.PCOLLECTION_ID).getOutputReceiver());
        Assert.assertSame(coder, OutputReceiverNode.create(receiver, coder, NodesTest.PCOLLECTION_ID).getCoder());
        Assert.assertNotEquals(OutputReceiverNode.create(receiver, coder, NodesTest.PCOLLECTION_ID), OutputReceiverNode.create(receiver, coder, NodesTest.PCOLLECTION_ID));
    }

    @Test
    public void testOperationNode() {
        Operation param = Mockito.mock(Operation.class);
        Assert.assertSame(param, OperationNode.create(param).getOperation());
        Assert.assertNotEquals(OperationNode.create(param), OperationNode.create(param));
    }

    @Test
    public void testRegisterRequestNode() {
        BeamFnApi.RegisterRequest param = RegisterRequest.getDefaultInstance();
        Map<String, NameContext> nameContexts = ImmutableMap.of("ABC", NameContext.create(null, "originalName", "systemName", "userName"));
        Map<String, Iterable<SideInputInfo>> sideInputInfos = ImmutableMap.of("DEF", ImmutableList.of(new SideInputInfo()));
        Map<String, Iterable<PCollectionView<?>>> pcollectionViews = ImmutableMap.of("GHI", ImmutableList.of(DataflowPortabilityPCollectionView.with(new org.apache.beam.sdk.values.TupleTag("JKL"), FullWindowedValueCoder.of(KvCoder.of(StringUtf8Coder.of(), StringUtf8Coder.of()), INSTANCE))));
        Assert.assertSame(param, RegisterRequestNode.create(param, nameContexts, sideInputInfos, pcollectionViews).getRegisterRequest());
        Assert.assertSame(nameContexts, RegisterRequestNode.create(param, nameContexts, sideInputInfos, pcollectionViews).getPTransformIdToPartialNameContextMap());
        Assert.assertSame(sideInputInfos, RegisterRequestNode.create(param, nameContexts, sideInputInfos, pcollectionViews).getPTransformIdToSideInputInfoMap());
        Assert.assertSame(pcollectionViews, RegisterRequestNode.create(param, nameContexts, sideInputInfos, pcollectionViews).getPTransformIdToPCollectionViewMap());
        Assert.assertNotEquals(RegisterRequestNode.create(param, nameContexts, sideInputInfos, pcollectionViews), RegisterRequestNode.create(param, nameContexts, sideInputInfos, pcollectionViews));
    }

    @Test
    public void testFetchReadySideInputsAndFilterBlockedStreamingSideInputsNode() {
        WindowingStrategy windowingStrategy = WindowingStrategy.globalDefault();
        Map<PCollectionView<?>, RunnerApi.SdkFunctionSpec> pcollectionViewsToWindowMappingFns = ImmutableMap.of(Mockito.mock(PCollectionView.class), SdkFunctionSpec.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn("beam:test:urn:1.0")).build());
        NameContext nameContext = NameContextsForTests.nameContextForTest();
        Assert.assertSame(FetchAndFilterStreamingSideInputsNode.create(windowingStrategy, pcollectionViewsToWindowMappingFns, nameContext).getWindowingStrategy(), windowingStrategy);
        Assert.assertSame(FetchAndFilterStreamingSideInputsNode.create(windowingStrategy, pcollectionViewsToWindowMappingFns, nameContext).getPCollectionViewsToWindowMappingFns(), pcollectionViewsToWindowMappingFns);
        Assert.assertSame(FetchAndFilterStreamingSideInputsNode.create(windowingStrategy, pcollectionViewsToWindowMappingFns, nameContext).getNameContext(), nameContext);
    }
}

