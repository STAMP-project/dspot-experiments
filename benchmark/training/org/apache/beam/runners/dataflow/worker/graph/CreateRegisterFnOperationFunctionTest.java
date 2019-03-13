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


import Nodes.ExecutionLocation.RUNNER_HARNESS;
import Nodes.ExecutionLocation.SDK_HARNESS;
import com.google.auto.value.AutoValue;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.beam.runners.dataflow.worker.graph.Edges.DefaultEdge;
import org.apache.beam.runners.dataflow.worker.graph.Edges.Edge;
import org.apache.beam.runners.dataflow.worker.graph.Edges.HappensBeforeEdge;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.Node;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.Graphs;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.MutableNetwork;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link CreateRegisterFnOperationFunction}.
 */
@RunWith(JUnit4.class)
public class CreateRegisterFnOperationFunctionTest {
    @Mock
    private BiFunction<String, String, Node> portSupplier;

    @Mock
    private Function<MutableNetwork<Node, Edge>, Node> registerFnOperationFunction;

    private Function<MutableNetwork<Node, Edge>, MutableNetwork<Node, Edge>> createRegisterFnOperation;

    @Test
    public void testEmptyGraph() {
        MutableNetwork<Node, Edge> appliedNetwork = createRegisterFnOperation.apply(CreateRegisterFnOperationFunctionTest.createEmptyNetwork());
        MutableNetwork<Node, Edge> expectedNetwork = CreateRegisterFnOperationFunctionTest.createEmptyNetwork();
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(appliedNetwork);
        Assert.assertTrue(String.format("Expected network %s but got network %s", expectedNetwork, appliedNetwork), Graphs.equivalent(expectedNetwork, appliedNetwork));
    }

    @Test
    public void testAllRunnerGraph() {
        Node readNode = CreateRegisterFnOperationFunctionTest.createReadNode("Read", RUNNER_HARNESS);
        Edge readNodeEdge = DefaultEdge.create();
        Node readNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("Read.out");
        Edge readNodeOutEdge = DefaultEdge.create();
        Node parDoNode = CreateRegisterFnOperationFunctionTest.createParDoNode("ParDo", RUNNER_HARNESS);
        Edge parDoNodeEdge = DefaultEdge.create();
        Node parDoNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("ParDo.out");
        // Read -out-> ParDo
        MutableNetwork<Node, Edge> expectedNetwork = CreateRegisterFnOperationFunctionTest.createEmptyNetwork();
        expectedNetwork.addNode(readNode);
        expectedNetwork.addNode(readNodeOut);
        expectedNetwork.addNode(parDoNode);
        expectedNetwork.addNode(parDoNodeOut);
        expectedNetwork.addEdge(readNode, readNodeOut, readNodeEdge);
        expectedNetwork.addEdge(readNodeOut, parDoNode, readNodeOutEdge);
        expectedNetwork.addEdge(parDoNode, parDoNodeOut, parDoNodeEdge);
        MutableNetwork<Node, Edge> appliedNetwork = createRegisterFnOperation.apply(Graphs.copyOf(expectedNetwork));
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(appliedNetwork);
        Assert.assertTrue(String.format("Expected network %s but got network %s", expectedNetwork, appliedNetwork), Graphs.equivalent(expectedNetwork, appliedNetwork));
    }

    @Test
    public void testAllSdkGraph() {
        Node sdkPortionNode = CreateRegisterFnOperationFunctionTest.TestNode.create("SdkPortion");
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ArgumentCaptor<MutableNetwork<Node, Edge>> networkCapture = ArgumentCaptor.forClass(((Class) (MutableNetwork.class)));
        Mockito.when(registerFnOperationFunction.apply(networkCapture.capture())).thenReturn(sdkPortionNode);
        // Read -out-> ParDo
        Node readNode = CreateRegisterFnOperationFunctionTest.createReadNode("Read", SDK_HARNESS);
        Edge readNodeEdge = DefaultEdge.create();
        Node readNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("Read.out");
        Edge readNodeOutEdge = DefaultEdge.create();
        Node parDoNode = CreateRegisterFnOperationFunctionTest.createParDoNode("ParDo", SDK_HARNESS);
        Edge parDoNodeEdge = DefaultEdge.create();
        Node parDoNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("ParDo.out");
        MutableNetwork<Node, Edge> network = CreateRegisterFnOperationFunctionTest.createEmptyNetwork();
        network.addNode(readNode);
        network.addNode(readNodeOut);
        network.addNode(parDoNode);
        network.addNode(parDoNodeOut);
        network.addEdge(readNode, readNodeOut, readNodeEdge);
        network.addEdge(readNodeOut, parDoNode, readNodeOutEdge);
        network.addEdge(parDoNode, parDoNodeOut, parDoNodeEdge);
        MutableNetwork<Node, Edge> expectedNetwork = CreateRegisterFnOperationFunctionTest.createEmptyNetwork();
        expectedNetwork.addNode(sdkPortionNode);
        MutableNetwork<Node, Edge> appliedNetwork = createRegisterFnOperation.apply(Graphs.copyOf(network));
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(appliedNetwork);
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(networkCapture.getValue());
        Assert.assertTrue(String.format("Expected network %s but got network %s", expectedNetwork, appliedNetwork), Graphs.equivalent(expectedNetwork, appliedNetwork));
        Assert.assertTrue(String.format("Expected network %s but got network %s", network, networkCapture.getValue()), Graphs.equivalent(network, networkCapture.getValue()));
    }

    @Test
    public void testRunnerToSdkToRunnerGraph() {
        Node sdkPortion = CreateRegisterFnOperationFunctionTest.TestNode.create("SdkPortion");
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ArgumentCaptor<MutableNetwork<Node, Edge>> networkCapture = ArgumentCaptor.forClass(((Class) (MutableNetwork.class)));
        Mockito.when(registerFnOperationFunction.apply(networkCapture.capture())).thenReturn(sdkPortion);
        Node firstPort = CreateRegisterFnOperationFunctionTest.TestNode.create("FirstPort");
        Node secondPort = CreateRegisterFnOperationFunctionTest.TestNode.create("SecondPort");
        Mockito.when(portSupplier.apply(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(firstPort, secondPort);
        Node readNode = CreateRegisterFnOperationFunctionTest.createReadNode("Read", RUNNER_HARNESS);
        Edge readNodeEdge = DefaultEdge.create();
        Node readNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("Read.out");
        Edge readNodeOutEdge = DefaultEdge.create();
        Node sdkParDoNode = CreateRegisterFnOperationFunctionTest.createParDoNode("SdkParDo", SDK_HARNESS);
        Edge sdkParDoNodeEdge = DefaultEdge.create();
        Node sdkParDoNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("SdkParDo.out");
        Edge sdkParDoNodeOutEdge = DefaultEdge.create();
        Node runnerParDoNode = CreateRegisterFnOperationFunctionTest.createParDoNode("RunnerParDo", RUNNER_HARNESS);
        Edge runnerParDoNodeEdge = DefaultEdge.create();
        Node runnerParDoNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("RunnerParDo.out");
        // Read -out-> SdkParDo -out-> RunnerParDo
        MutableNetwork<Node, Edge> network = CreateRegisterFnOperationFunctionTest.createEmptyNetwork();
        network.addNode(readNode);
        network.addNode(readNodeOut);
        network.addNode(sdkParDoNodeOut);
        network.addNode(sdkParDoNodeOut);
        network.addNode(runnerParDoNode);
        network.addNode(runnerParDoNodeOut);
        network.addEdge(readNode, readNodeOut, readNodeEdge);
        network.addEdge(readNodeOut, sdkParDoNode, readNodeOutEdge);
        network.addEdge(sdkParDoNode, sdkParDoNodeOut, sdkParDoNodeEdge);
        network.addEdge(sdkParDoNodeOut, runnerParDoNode, sdkParDoNodeOutEdge);
        network.addEdge(runnerParDoNode, runnerParDoNodeOut, runnerParDoNodeEdge);
        MutableNetwork<Node, Edge> appliedNetwork = createRegisterFnOperation.apply(Graphs.copyOf(network));
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(appliedNetwork);
        // On each rewire between runner and SDK and vice versa, we use a new output node
        Node newOutA = Iterables.getOnlyElement(appliedNetwork.predecessors(firstPort));
        Node newOutB = Iterables.getOnlyElement(appliedNetwork.successors(secondPort));
        // readNode -newOutA-> firstPort --> sdkPortion --> secondPort -newOutB-> runnerParDoNode
        Assert.assertThat(appliedNetwork.nodes(), Matchers.containsInAnyOrder(readNode, newOutA, firstPort, sdkPortion, secondPort, newOutB, runnerParDoNode, runnerParDoNodeOut));
        Assert.assertThat(appliedNetwork.successors(readNode), Matchers.containsInAnyOrder(newOutA));
        Assert.assertThat(appliedNetwork.successors(newOutA), Matchers.containsInAnyOrder(firstPort));
        Assert.assertThat(appliedNetwork.successors(firstPort), Matchers.containsInAnyOrder(sdkPortion));
        Assert.assertThat(appliedNetwork.successors(sdkPortion), Matchers.containsInAnyOrder(secondPort));
        Assert.assertThat(appliedNetwork.successors(secondPort), Matchers.containsInAnyOrder(newOutB));
        Assert.assertThat(appliedNetwork.successors(newOutB), Matchers.containsInAnyOrder(runnerParDoNode));
        Assert.assertThat(appliedNetwork.successors(runnerParDoNode), Matchers.containsInAnyOrder(runnerParDoNodeOut));
        Assert.assertThat(appliedNetwork.edgesConnecting(firstPort, sdkPortion), Matchers.everyItem(Matchers.<Edges.Edge>instanceOf(HappensBeforeEdge.class)));
        Assert.assertThat(appliedNetwork.edgesConnecting(sdkPortion, secondPort), Matchers.everyItem(Matchers.<Edges.Edge>instanceOf(HappensBeforeEdge.class)));
        MutableNetwork<Node, Edge> sdkSubnetwork = networkCapture.getValue();
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(sdkSubnetwork);
        Node sdkNewOutA = Iterables.getOnlyElement(sdkSubnetwork.successors(firstPort));
        Node sdkNewOutB = Iterables.getOnlyElement(sdkSubnetwork.predecessors(secondPort));
        // firstPort -sdkNewOutA-> sdkParDoNode -sdkNewOutB-> secondPort
        Assert.assertThat(sdkSubnetwork.nodes(), Matchers.containsInAnyOrder(firstPort, sdkNewOutA, sdkParDoNode, sdkNewOutB, secondPort));
        Assert.assertThat(sdkSubnetwork.successors(firstPort), Matchers.containsInAnyOrder(sdkNewOutA));
        Assert.assertThat(sdkSubnetwork.successors(sdkNewOutA), Matchers.containsInAnyOrder(sdkParDoNode));
        Assert.assertThat(sdkSubnetwork.successors(sdkParDoNode), Matchers.containsInAnyOrder(sdkNewOutB));
        Assert.assertThat(sdkSubnetwork.successors(sdkNewOutB), Matchers.containsInAnyOrder(secondPort));
    }

    @Test
    public void testSdkToRunnerToSdkGraph() {
        Node firstSdkPortion = CreateRegisterFnOperationFunctionTest.TestNode.create("FirstSdkPortion");
        Node secondSdkPortion = CreateRegisterFnOperationFunctionTest.TestNode.create("SecondSdkPortion");
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ArgumentCaptor<MutableNetwork<Node, Edge>> networkCapture = ArgumentCaptor.forClass(((Class) (MutableNetwork.class)));
        Mockito.when(registerFnOperationFunction.apply(networkCapture.capture())).thenReturn(firstSdkPortion, secondSdkPortion);
        Node firstPort = CreateRegisterFnOperationFunctionTest.TestNode.create("FirstPort");
        Node secondPort = CreateRegisterFnOperationFunctionTest.TestNode.create("SecondPort");
        Mockito.when(portSupplier.apply(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(firstPort, secondPort);
        Node readNode = CreateRegisterFnOperationFunctionTest.createReadNode("Read", SDK_HARNESS);
        Edge readNodeEdge = DefaultEdge.create();
        Node readNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("Read.out");
        Edge readNodeOutEdge = DefaultEdge.create();
        Node runnerParDoNode = CreateRegisterFnOperationFunctionTest.createParDoNode("RunnerParDo", RUNNER_HARNESS);
        Edge runnerParDoNodeEdge = DefaultEdge.create();
        Node runnerParDoNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("RunnerParDo.out");
        Edge runnerParDoNodeOutEdge = DefaultEdge.create();
        Node sdkParDoNode = CreateRegisterFnOperationFunctionTest.createParDoNode("SdkParDo", SDK_HARNESS);
        Edge sdkParDoNodeEdge = DefaultEdge.create();
        Node sdkParDoNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("SdkParDo.out");
        // Read -out-> RunnerParDo -out-> SdkParDo
        MutableNetwork<Node, Edge> network = CreateRegisterFnOperationFunctionTest.createEmptyNetwork();
        network.addNode(readNode);
        network.addNode(readNodeOut);
        network.addNode(runnerParDoNode);
        network.addNode(runnerParDoNodeOut);
        network.addNode(sdkParDoNodeOut);
        network.addNode(sdkParDoNodeOut);
        network.addEdge(readNode, readNodeOut, readNodeEdge);
        network.addEdge(readNodeOut, runnerParDoNode, readNodeOutEdge);
        network.addEdge(runnerParDoNode, runnerParDoNodeOut, runnerParDoNodeEdge);
        network.addEdge(runnerParDoNodeOut, sdkParDoNode, runnerParDoNodeOutEdge);
        network.addEdge(sdkParDoNode, sdkParDoNodeOut, sdkParDoNodeEdge);
        MutableNetwork<Node, Edge> appliedNetwork = createRegisterFnOperation.apply(Graphs.copyOf(network));
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(appliedNetwork);
        // On each rewire between runner and SDK, we use a new output node
        Node newOutA = Iterables.getOnlyElement(appliedNetwork.successors(firstPort));
        Node newOutB = Iterables.getOnlyElement(appliedNetwork.predecessors(secondPort));
        // firstSdkPortion -> firstPort -newOutA-> RunnerParDo -newOutB-> secondPort -> secondSdkPortion
        Assert.assertThat(appliedNetwork.nodes(), Matchers.containsInAnyOrder(firstSdkPortion, firstPort, newOutA, runnerParDoNode, newOutB, secondPort, secondSdkPortion));
        Assert.assertThat(appliedNetwork.successors(firstSdkPortion), Matchers.containsInAnyOrder(firstPort));
        Assert.assertThat(appliedNetwork.successors(firstPort), Matchers.containsInAnyOrder(newOutA));
        Assert.assertThat(appliedNetwork.successors(newOutA), Matchers.containsInAnyOrder(runnerParDoNode));
        Assert.assertThat(appliedNetwork.successors(runnerParDoNode), Matchers.containsInAnyOrder(newOutB));
        Assert.assertThat(appliedNetwork.successors(newOutB), Matchers.containsInAnyOrder(secondPort));
        Assert.assertThat(appliedNetwork.successors(secondPort), Matchers.containsInAnyOrder(secondSdkPortion));
        Assert.assertThat(appliedNetwork.edgesConnecting(firstSdkPortion, firstPort), Matchers.everyItem(Matchers.<Edges.Edge>instanceOf(HappensBeforeEdge.class)));
        Assert.assertThat(appliedNetwork.edgesConnecting(secondPort, secondSdkPortion), Matchers.everyItem(Matchers.<Edges.Edge>instanceOf(HappensBeforeEdge.class)));
        // The order of the calls to create the SDK subnetworks is indeterminate
        List<MutableNetwork<Node, Edge>> sdkSubnetworks = networkCapture.getAllValues();
        MutableNetwork<Node, Edge> firstSdkSubnetwork;
        MutableNetwork<Node, Edge> secondSdkSubnetwork;
        if (sdkSubnetworks.get(0).nodes().contains(readNode)) {
            firstSdkSubnetwork = sdkSubnetworks.get(0);
            secondSdkSubnetwork = sdkSubnetworks.get(1);
        } else {
            firstSdkSubnetwork = sdkSubnetworks.get(1);
            secondSdkSubnetwork = sdkSubnetworks.get(0);
        }
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(firstSdkSubnetwork);
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(secondSdkSubnetwork);
        Node sdkNewOutA = Iterables.getOnlyElement(firstSdkSubnetwork.predecessors(firstPort));
        // readNode -sdkNewOutA-> firstPort
        Assert.assertThat(firstSdkSubnetwork.nodes(), Matchers.containsInAnyOrder(readNode, sdkNewOutA, firstPort));
        Assert.assertThat(firstSdkSubnetwork.successors(readNode), Matchers.containsInAnyOrder(sdkNewOutA));
        Assert.assertThat(firstSdkSubnetwork.successors(sdkNewOutA), Matchers.containsInAnyOrder(firstPort));
        Node sdkNewOutB = Iterables.getOnlyElement(secondSdkSubnetwork.successors(secondPort));
        // secondPort -sdkNewOutB-> sdkParDoNode -> sdkParDoNodeOut
        Assert.assertThat(secondSdkSubnetwork.nodes(), Matchers.containsInAnyOrder(secondPort, sdkNewOutB, sdkParDoNode, sdkParDoNodeOut));
        Assert.assertThat(secondSdkSubnetwork.successors(secondPort), Matchers.containsInAnyOrder(sdkNewOutB));
        Assert.assertThat(secondSdkSubnetwork.successors(sdkNewOutB), Matchers.containsInAnyOrder(sdkParDoNode));
        Assert.assertThat(secondSdkSubnetwork.successors(sdkParDoNode), Matchers.containsInAnyOrder(sdkParDoNodeOut));
    }

    @Test
    public void testRunnerAndSdkToRunnerAndSdkGraph() {
        // RunnerSource --\   /--> RunnerParDo
        // out
        // CustomSource --/   \--> SdkParDo
        // 
        // Should produce:
        // PortB --> out --\
        // RunnerSource --> out --> RunnerParDo
        // \--> PortA
        // PortA --> out --\
        // CustomSource --> out --> SdkParDo
        // \--> PortB
        Node firstSdkPortion = CreateRegisterFnOperationFunctionTest.TestNode.create("FirstSdkPortion");
        Node secondSdkPortion = CreateRegisterFnOperationFunctionTest.TestNode.create("SecondSdkPortion");
        @SuppressWarnings({ "unchecked", "rawtypes" })
        ArgumentCaptor<MutableNetwork<Node, Edge>> networkCapture = ArgumentCaptor.forClass(((Class) (MutableNetwork.class)));
        Mockito.when(registerFnOperationFunction.apply(networkCapture.capture())).thenReturn(firstSdkPortion, secondSdkPortion);
        Node firstPort = CreateRegisterFnOperationFunctionTest.TestNode.create("FirstPort");
        Node secondPort = CreateRegisterFnOperationFunctionTest.TestNode.create("SecondPort");
        Mockito.when(portSupplier.apply(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(firstPort, secondPort);
        Node runnerReadNode = CreateRegisterFnOperationFunctionTest.createReadNode("RunnerRead", RUNNER_HARNESS);
        Edge runnerReadNodeEdge = DefaultEdge.create();
        Node sdkReadNode = CreateRegisterFnOperationFunctionTest.createReadNode("SdkRead", SDK_HARNESS);
        Edge sdkReadNodeEdge = DefaultEdge.create();
        Node readNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("Read.out");
        Edge readNodeOutToRunnerEdge = DefaultEdge.create();
        Edge readNodeOutToSdkEdge = DefaultEdge.create();
        Node runnerParDoNode = CreateRegisterFnOperationFunctionTest.createParDoNode("RunnerParDo", RUNNER_HARNESS);
        Edge runnerParDoNodeEdge = DefaultEdge.create();
        Node runnerParDoNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("RunnerParDo.out");
        Node sdkParDoNode = CreateRegisterFnOperationFunctionTest.createParDoNode("SdkParDo", SDK_HARNESS);
        Edge sdkParDoNodeEdge = DefaultEdge.create();
        Node sdkParDoNodeOut = CreateRegisterFnOperationFunctionTest.createInstructionOutputNode("SdkParDo.out");
        // Read -out-> RunnerParDo -out-> SdkParDo
        MutableNetwork<Node, Edge> network = CreateRegisterFnOperationFunctionTest.createEmptyNetwork();
        network.addNode(sdkReadNode);
        network.addNode(runnerReadNode);
        network.addNode(readNodeOut);
        network.addNode(runnerParDoNode);
        network.addNode(runnerParDoNodeOut);
        network.addNode(sdkParDoNodeOut);
        network.addNode(sdkParDoNodeOut);
        network.addEdge(sdkReadNode, readNodeOut, sdkReadNodeEdge);
        network.addEdge(runnerReadNode, readNodeOut, runnerReadNodeEdge);
        network.addEdge(readNodeOut, runnerParDoNode, readNodeOutToRunnerEdge);
        network.addEdge(readNodeOut, sdkParDoNode, readNodeOutToSdkEdge);
        network.addEdge(runnerParDoNode, runnerParDoNodeOut, runnerParDoNodeEdge);
        network.addEdge(sdkParDoNode, sdkParDoNodeOut, sdkParDoNodeEdge);
        MutableNetwork<Node, Edge> appliedNetwork = createRegisterFnOperation.apply(Graphs.copyOf(network));
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(appliedNetwork);
        // Node wiring is indeterministic, must be detected from generated graph.
        Node sdkPortionA;
        Node sdkPortionB;
        if ((appliedNetwork.inDegree(firstSdkPortion)) == 0) {
            sdkPortionA = firstSdkPortion;
            sdkPortionB = secondSdkPortion;
        } else {
            sdkPortionA = secondSdkPortion;
            sdkPortionB = firstSdkPortion;
        }
        Node portA = Iterables.getOnlyElement(appliedNetwork.successors(sdkPortionA));
        Node portB = Iterables.getOnlyElement(appliedNetwork.predecessors(sdkPortionB));
        // On each rewire between runner and SDK, we use a new output node
        Node newOutA = Iterables.getOnlyElement(appliedNetwork.successors(portA));
        Node newOutB = Iterables.getOnlyElement(appliedNetwork.predecessors(portB));
        // sdkPortionA -> portA -newOutA-> runnerParDoNode -> runnerParDoNodeOut
        // runnerReadNode -newOutB-/
        // \--> portB -> sdkPortionB
        Assert.assertThat(appliedNetwork.nodes(), Matchers.containsInAnyOrder(runnerReadNode, firstSdkPortion, secondSdkPortion, portA, newOutA, portB, newOutB, runnerParDoNode, runnerParDoNodeOut));
        Assert.assertThat(appliedNetwork.successors(runnerReadNode), Matchers.containsInAnyOrder(newOutB));
        Assert.assertThat(appliedNetwork.successors(newOutB), Matchers.containsInAnyOrder(runnerParDoNode, portB));
        Assert.assertThat(appliedNetwork.successors(portB), Matchers.containsInAnyOrder(sdkPortionB));
        Assert.assertThat(appliedNetwork.successors(sdkPortionA), Matchers.containsInAnyOrder(portA));
        Assert.assertThat(appliedNetwork.successors(portA), Matchers.containsInAnyOrder(newOutA));
        Assert.assertThat(appliedNetwork.successors(newOutA), Matchers.containsInAnyOrder(runnerParDoNode));
        Assert.assertThat(appliedNetwork.successors(runnerParDoNode), Matchers.containsInAnyOrder(runnerParDoNodeOut));
        Assert.assertThat(appliedNetwork.edgesConnecting(sdkPortionA, portA), Matchers.everyItem(Matchers.<Edges.Edge>instanceOf(HappensBeforeEdge.class)));
        Assert.assertThat(appliedNetwork.edgesConnecting(portB, sdkPortionB), Matchers.everyItem(Matchers.<Edges.Edge>instanceOf(HappensBeforeEdge.class)));
        // Argument captor call order can be indeterministic
        List<MutableNetwork<Node, Edge>> sdkSubnetworks = networkCapture.getAllValues();
        MutableNetwork<Node, Edge> sdkSubnetworkA;
        MutableNetwork<Node, Edge> sdkSubnetworkB;
        if (sdkSubnetworks.get(0).nodes().contains(sdkReadNode)) {
            sdkSubnetworkA = sdkSubnetworks.get(0);
            sdkSubnetworkB = sdkSubnetworks.get(1);
        } else {
            sdkSubnetworkA = sdkSubnetworks.get(1);
            sdkSubnetworkB = sdkSubnetworks.get(0);
        }
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(sdkSubnetworkA);
        CreateRegisterFnOperationFunctionTest.assertNetworkMaintainsBipartiteStructure(sdkSubnetworkB);
        // /-> portA
        // sdkReadNode -sdkNewOutA-> sdkParDoNode -> sdkParDoNodeOut
        Node sdkNewOutA = Iterables.getOnlyElement(sdkSubnetworkA.predecessors(portA));
        Assert.assertThat(sdkSubnetworkA.nodes(), Matchers.containsInAnyOrder(sdkReadNode, portA, sdkNewOutA, sdkParDoNode, sdkParDoNodeOut));
        Assert.assertThat(sdkSubnetworkA.successors(sdkReadNode), Matchers.containsInAnyOrder(sdkNewOutA));
        Assert.assertThat(sdkSubnetworkA.successors(sdkNewOutA), Matchers.containsInAnyOrder(portA, sdkParDoNode));
        Assert.assertThat(sdkSubnetworkA.successors(sdkParDoNode), Matchers.containsInAnyOrder(sdkParDoNodeOut));
        // portB -sdkNewOutB-> sdkParDoNode -> sdkParDoNodeOut
        Node sdkNewOutB = Iterables.getOnlyElement(sdkSubnetworkB.successors(portB));
        Assert.assertThat(sdkSubnetworkB.nodes(), Matchers.containsInAnyOrder(portB, sdkNewOutB, sdkParDoNode, sdkParDoNodeOut));
        Assert.assertThat(sdkSubnetworkB.successors(portB), Matchers.containsInAnyOrder(sdkNewOutB));
        Assert.assertThat(sdkSubnetworkB.successors(sdkNewOutB), Matchers.containsInAnyOrder(sdkParDoNode));
        Assert.assertThat(sdkSubnetworkB.successors(sdkParDoNode), Matchers.containsInAnyOrder(sdkParDoNodeOut));
    }

    /**
     * A named node to easily differentiate graph construction problems during testing.
     */
    @AutoValue
    public abstract static class TestNode extends Node {
        public static CreateRegisterFnOperationFunctionTest.TestNode create(String value) {
            return new AutoValue_CreateRegisterFnOperationFunctionTest_TestNode(value);
        }

        public abstract String getName();

        @Override
        public String toString() {
            return ((hashCode()) + " ") + (getName());
        }
    }
}

