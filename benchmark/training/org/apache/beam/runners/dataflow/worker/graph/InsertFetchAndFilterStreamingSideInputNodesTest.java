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


import com.google.api.services.dataflow.model.InstructionOutput;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.model.pipeline.v1.RunnerApi.SdkFunctionSpec;
import org.apache.beam.runners.core.construction.ParDoTranslation;
import org.apache.beam.runners.core.construction.PipelineTranslation;
import org.apache.beam.runners.core.construction.SdkComponents;
import org.apache.beam.runners.dataflow.worker.NameContextsForTests;
import org.apache.beam.runners.dataflow.worker.graph.Edges.DefaultEdge;
import org.apache.beam.runners.dataflow.worker.graph.Edges.Edge;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.FetchAndFilterStreamingSideInputsNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.InstructionOutputNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.Node;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Equivalence;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.ImmutableNetwork;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.MutableNetwork;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.Network;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link InsertFetchAndFilterStreamingSideInputNodes}.
 */
@RunWith(JUnit4.class)
public class InsertFetchAndFilterStreamingSideInputNodesTest {
    @Test
    public void testWithoutPipeline() throws Exception {
        Node unknown = InsertFetchAndFilterStreamingSideInputNodesTest.createParDoNode("parDoId");
        MutableNetwork<Node, Edge> network = InsertFetchAndFilterStreamingSideInputNodesTest.createEmptyNetwork();
        network.addNode(unknown);
        Network<Node, Edge> inputNetwork = ImmutableNetwork.copyOf(network);
        network = InsertFetchAndFilterStreamingSideInputNodes.with(null).forNetwork(network);
        assertThatNetworksAreIdentical(inputNetwork, network);
    }

    @Test
    public void testSdkParDoWithSideInput() throws Exception {
        Pipeline p = Pipeline.create();
        PCollection<String> pc = p.apply(Create.of("a", "b", "c"));
        PCollectionView<Iterable<String>> pcView = pc.apply(View.asIterable());
        pc.apply(ParDo.of(new InsertFetchAndFilterStreamingSideInputNodesTest.TestDoFn(pcView)).withSideInputs(pcView));
        RunnerApi.Pipeline pipeline = PipelineTranslation.toProto(p);
        Node predecessor = InsertFetchAndFilterStreamingSideInputNodesTest.createParDoNode("predecessor");
        InstructionOutputNode mainInput = InstructionOutputNode.create(new InstructionOutput(), "fakeId");
        Node sideInputParDo = InsertFetchAndFilterStreamingSideInputNodesTest.createParDoNode(findParDoWithSideInput(pipeline));
        MutableNetwork<Node, Edge> network = InsertFetchAndFilterStreamingSideInputNodesTest.createEmptyNetwork();
        network.addNode(predecessor);
        network.addNode(mainInput);
        network.addNode(sideInputParDo);
        network.addEdge(predecessor, mainInput, DefaultEdge.create());
        network.addEdge(mainInput, sideInputParDo, DefaultEdge.create());
        Network<Node, Edge> inputNetwork = ImmutableNetwork.copyOf(network);
        network = InsertFetchAndFilterStreamingSideInputNodes.with(pipeline).forNetwork(network);
        Node mainInputClone = InstructionOutputNode.create(mainInput.getInstructionOutput(), "fakeId");
        Node fetchAndFilter = FetchAndFilterStreamingSideInputsNode.create(pcView.getWindowingStrategyInternal(), ImmutableMap.of(pcView, ParDoTranslation.translateWindowMappingFn(pcView.getWindowMappingFn(), SdkComponents.create(PipelineOptionsFactory.create()))), NameContextsForTests.nameContextForTest());
        MutableNetwork<Node, Edge> expectedNetwork = InsertFetchAndFilterStreamingSideInputNodesTest.createEmptyNetwork();
        expectedNetwork.addNode(predecessor);
        expectedNetwork.addNode(mainInputClone);
        expectedNetwork.addNode(fetchAndFilter);
        expectedNetwork.addNode(mainInput);
        expectedNetwork.addNode(sideInputParDo);
        expectedNetwork.addEdge(predecessor, mainInputClone, DefaultEdge.create());
        expectedNetwork.addEdge(mainInputClone, fetchAndFilter, DefaultEdge.create());
        expectedNetwork.addEdge(fetchAndFilter, mainInput, DefaultEdge.create());
        expectedNetwork.addEdge(mainInput, sideInputParDo, DefaultEdge.create());
        assertThatNetworksAreIdentical(expectedNetwork, network);
    }

    @Test
    public void testSdkParDoWithoutSideInput() throws Exception {
        Pipeline p = Pipeline.create();
        PCollection<String> pc = p.apply(Create.of("a", "b", "c"));
        pc.apply(ParDo.of(new InsertFetchAndFilterStreamingSideInputNodesTest.TestDoFn(null)));
        RunnerApi.Pipeline pipeline = PipelineTranslation.toProto(p);
        Node predecessor = InsertFetchAndFilterStreamingSideInputNodesTest.createParDoNode("predecessor");
        Node mainInput = InstructionOutputNode.create(new InstructionOutput(), "fakeId");
        Node sideInputParDo = InsertFetchAndFilterStreamingSideInputNodesTest.createParDoNode("noSideInput");
        MutableNetwork<Node, Edge> network = InsertFetchAndFilterStreamingSideInputNodesTest.createEmptyNetwork();
        network.addNode(predecessor);
        network.addNode(mainInput);
        network.addNode(sideInputParDo);
        network.addEdge(predecessor, mainInput, DefaultEdge.create());
        network.addEdge(mainInput, sideInputParDo, DefaultEdge.create());
        Network<Node, Edge> inputNetwork = ImmutableNetwork.copyOf(network);
        network = InsertFetchAndFilterStreamingSideInputNodes.with(pipeline).forNetwork(network);
        assertThatNetworksAreIdentical(inputNetwork, network);
    }

    private static class TestDoFn extends DoFn<String, Iterable<String>> {
        @Nullable
        private final PCollectionView<Iterable<String>> pCollectionView;

        private TestDoFn(@Nullable
        PCollectionView<Iterable<String>> pCollectionView) {
            this.pCollectionView = pCollectionView;
        }

        @ProcessElement
        public void processElement(ProcessContext context) {
        }
    }

    private static final class NodeEquivalence extends Equivalence<Node> {
        static final InsertFetchAndFilterStreamingSideInputNodesTest.NodeEquivalence INSTANCE = new InsertFetchAndFilterStreamingSideInputNodesTest.NodeEquivalence();

        @Override
        protected boolean doEquivalent(Node a, Node b) {
            if ((a instanceof FetchAndFilterStreamingSideInputsNode) && (b instanceof FetchAndFilterStreamingSideInputsNode)) {
                FetchAndFilterStreamingSideInputsNode nodeA = ((FetchAndFilterStreamingSideInputsNode) (a));
                FetchAndFilterStreamingSideInputsNode nodeB = ((FetchAndFilterStreamingSideInputsNode) (b));
                Map.Entry<PCollectionView<?>, SdkFunctionSpec> nodeAEntry = Iterables.getOnlyElement(nodeA.getPCollectionViewsToWindowMappingFns().entrySet());
                Map.Entry<PCollectionView<?>, SdkFunctionSpec> nodeBEntry = Iterables.getOnlyElement(nodeB.getPCollectionViewsToWindowMappingFns().entrySet());
                return (Objects.equals(nodeAEntry.getKey().getTagInternal(), nodeBEntry.getKey().getTagInternal())) && (Objects.equals(nodeAEntry.getValue(), nodeBEntry.getValue()));
            } else
                if ((a instanceof InstructionOutputNode) && (b instanceof InstructionOutputNode)) {
                    return Objects.equals(getInstructionOutput(), getInstructionOutput());
                } else {
                    return a.equals(b);// Make sure that other nodes haven't been modified

                }

        }

        @Override
        protected int doHash(Node n) {
            return n.hashCode();
        }
    }
}

