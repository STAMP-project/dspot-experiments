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


import ExecutionLocation.RUNNER_HARNESS;
import ExecutionLocation.SDK_HARNESS;
import Nodes.ExecutionLocation.UNKNOWN;
import com.google.api.services.dataflow.model.FlattenInstruction;
import com.google.api.services.dataflow.model.InstructionOutput;
import com.google.api.services.dataflow.model.ParallelInstruction;
import org.apache.beam.runners.dataflow.worker.graph.Edges.DefaultEdge;
import org.apache.beam.runners.dataflow.worker.graph.Edges.Edge;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.InstructionOutputNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.Node;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.ParallelInstructionNode;
import org.apache.beam.vendor.guava.v20_0.com.google.common.base.Equivalence;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.Graphs;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.ImmutableNetwork;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.MutableNetwork;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.Network;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DeduceNodeLocationsFunction}.
 */
@RunWith(JUnit4.class)
public final class DeduceNodeLocationsFunctionTest {
    private static final String CUSTOM_SOURCE = "org.apache.beam.runners.dataflow.internal.CustomSources";

    private static final String RUNNER_SOURCE = "GroupingShuffleSource";

    private static final String DO_FN = "DoFn";

    private static final Equivalence<Node> NODE_EQUIVALENCE = DeduceNodeLocationsFunctionTest.NodeEquivalence.INSTANCE;

    private static final class NodeEquivalence extends Equivalence<Node> {
        static final DeduceNodeLocationsFunctionTest.NodeEquivalence INSTANCE = new DeduceNodeLocationsFunctionTest.NodeEquivalence();

        @Override
        protected boolean doEquivalent(Node a, Node b) {
            if ((a instanceof ParallelInstructionNode) && (b instanceof ParallelInstructionNode)) {
                ParallelInstruction contentsA = getParallelInstruction();
                ParallelInstruction contentsB = getParallelInstruction();
                return contentsA.equals(contentsB);
            } else {
                return a.equals(b);// Make sure non-deducible nodes haven't been modified.

            }
        }

        @Override
        protected int doHash(Node n) {
            return n.hashCode();
        }
    }

    @Test
    public void testEmptyNetwork() {
        Assert.assertTrue(Graphs.equivalent(DeduceNodeLocationsFunctionTest.createEmptyNetwork(), new DeduceNodeLocationsFunction().apply(DeduceNodeLocationsFunctionTest.createEmptyNetwork())));
    }

    @Test
    public void testSingleNodeWithSdkRead() throws Exception {
        Node unknown = DeduceNodeLocationsFunctionTest.createReadNode("Unknown", DeduceNodeLocationsFunctionTest.CUSTOM_SOURCE);
        MutableNetwork<Node, Edge> network = DeduceNodeLocationsFunctionTest.createEmptyNetwork();
        network.addNode(unknown);
        Network<Node, Edge> inputNetwork = ImmutableNetwork.copyOf(network);
        network = new DeduceNodeLocationsFunction().apply(network);
        assertThatNetworksAreIdentical(inputNetwork, network);
        for (Node node : ImmutableList.copyOf(network.nodes())) {
            assertNodesIdenticalExceptForExecutionLocation(unknown, node);
            assertThatLocationIsProperlyDeduced(node, SDK_HARNESS);
        }
    }

    @Test
    public void testSingleNodeWithRunnerRead() throws Exception {
        Node unknown = DeduceNodeLocationsFunctionTest.createReadNode("Unknown", DeduceNodeLocationsFunctionTest.RUNNER_SOURCE);
        MutableNetwork<Node, Edge> network = DeduceNodeLocationsFunctionTest.createEmptyNetwork();
        network.addNode(unknown);
        Network<Node, Edge> inputNetwork = ImmutableNetwork.copyOf(network);
        network = new DeduceNodeLocationsFunction().apply(network);
        assertThatNetworksAreIdentical(inputNetwork, network);
        for (Node node : ImmutableList.copyOf(network.nodes())) {
            assertNodesIdenticalExceptForExecutionLocation(unknown, node);
            assertThatLocationIsProperlyDeduced(node, RUNNER_HARNESS);
        }
    }

    @Test
    public void testSingleNodeWithSdkParDo() throws Exception {
        Node unknown = DeduceNodeLocationsFunctionTest.createParDoNode("Unknown", DeduceNodeLocationsFunctionTest.DO_FN);
        MutableNetwork<Node, Edge> network = DeduceNodeLocationsFunctionTest.createEmptyNetwork();
        network.addNode(unknown);
        Network<Node, Edge> inputNetwork = ImmutableNetwork.copyOf(network);
        network = new DeduceNodeLocationsFunction().apply(network);
        assertThatNetworksAreIdentical(inputNetwork, network);
        for (Node node : ImmutableList.copyOf(network.nodes())) {
            assertNodesIdenticalExceptForExecutionLocation(unknown, node);
            assertThatLocationIsProperlyDeduced(node, SDK_HARNESS);
        }
    }

    @Test
    public void testSingleNodeWithRunnerParDo() throws Exception {
        Node unknown = DeduceNodeLocationsFunctionTest.createParDoNode("Unknown", "RunnerDoFn");
        MutableNetwork<Node, Edge> network = DeduceNodeLocationsFunctionTest.createEmptyNetwork();
        network.addNode(unknown);
        Network<Node, Edge> inputNetwork = ImmutableNetwork.copyOf(network);
        network = new DeduceNodeLocationsFunction().apply(network);
        assertThatNetworksAreIdentical(inputNetwork, network);
        for (Node node : ImmutableList.copyOf(network.nodes())) {
            assertNodesIdenticalExceptForExecutionLocation(unknown, node);
            assertThatLocationIsProperlyDeduced(node, RUNNER_HARNESS);
        }
    }

    /**
     * Tests that multiple deduced nodes with connecting edges are maintained correctly.
     */
    @Test
    public void testMultipleNodesDeduced() throws Exception {
        // A --\     /--> C
        // -> E
        // B --/     \--> D
        Node a = DeduceNodeLocationsFunctionTest.createReadNode("A", DeduceNodeLocationsFunctionTest.CUSTOM_SOURCE);
        Node b = DeduceNodeLocationsFunctionTest.createReadNode("B", DeduceNodeLocationsFunctionTest.RUNNER_SOURCE);
        Node c = DeduceNodeLocationsFunctionTest.createParDoNode("C", "RunnerDoFn");
        Node d = DeduceNodeLocationsFunctionTest.createParDoNode("D", DeduceNodeLocationsFunctionTest.DO_FN);
        Node e = DeduceNodeLocationsFunctionTest.createParDoNode("E", DeduceNodeLocationsFunctionTest.DO_FN);
        MutableNetwork<Node, Edge> network = DeduceNodeLocationsFunctionTest.createEmptyNetwork();
        network.addNode(a);
        network.addNode(b);
        network.addNode(c);
        network.addNode(d);
        network.addNode(e);
        network.addEdge(a, e, DefaultEdge.create());
        network.addEdge(b, e, DefaultEdge.create());
        network.addEdge(e, c, DefaultEdge.create());
        network.addEdge(e, d, DefaultEdge.create());
        Network<Node, Edge> inputNetwork = ImmutableNetwork.copyOf(network);
        network = new DeduceNodeLocationsFunction().apply(network);
        assertThatNetworksAreIdentical(inputNetwork, network);
        assertAllNodesDeducedExceptFlattens(network);
    }

    /**
     * Tests that graphs with deducible and non-deducible nodes are maintained correctly.
     */
    @Test
    public void testGraphWithNonDeducibleNodes() throws Exception {
        // A --> out1 --\
        // --> Flatten --> D
        // B --> out2 --/-->C
        Node a = DeduceNodeLocationsFunctionTest.createReadNode("A", DeduceNodeLocationsFunctionTest.CUSTOM_SOURCE);
        Node out1 = InstructionOutputNode.create(new InstructionOutput(), "fakeId");
        Node b = DeduceNodeLocationsFunctionTest.createReadNode("B", DeduceNodeLocationsFunctionTest.RUNNER_SOURCE);
        Node out2 = InstructionOutputNode.create(new InstructionOutput(), "fakeId");
        Node c = DeduceNodeLocationsFunctionTest.createParDoNode("C", "RunnerDoFn");
        Node flatten = ParallelInstructionNode.create(new ParallelInstruction().setName("Flatten").setFlatten(new FlattenInstruction()), UNKNOWN);
        Node d = DeduceNodeLocationsFunctionTest.createParDoNode("D", DeduceNodeLocationsFunctionTest.DO_FN);
        MutableNetwork<Node, Edge> network = DeduceNodeLocationsFunctionTest.createEmptyNetwork();
        network.addNode(a);
        network.addNode(out1);
        network.addNode(b);
        network.addNode(out2);
        network.addNode(c);
        network.addNode(flatten);
        network.addNode(d);
        network.addEdge(a, out1, DefaultEdge.create());
        network.addEdge(b, out2, DefaultEdge.create());
        network.addEdge(out1, flatten, DefaultEdge.create());
        network.addEdge(out2, flatten, DefaultEdge.create());
        network.addEdge(out2, c, DefaultEdge.create());
        network.addEdge(flatten, d, DefaultEdge.create());
        Network<Node, Edge> inputNetwork = ImmutableNetwork.copyOf(network);
        network = new DeduceNodeLocationsFunction().apply(network);
        assertThatNetworksAreIdentical(inputNetwork, network);
        assertAllNodesDeducedExceptFlattens(network);
    }
}

