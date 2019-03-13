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


import ExecutionLocation.AMBIGUOUS;
import ExecutionLocation.RUNNER_HARNESS;
import ExecutionLocation.SDK_HARNESS;
import ExecutionLocation.UNKNOWN;
import org.apache.beam.runners.dataflow.worker.graph.Edges.DefaultEdge;
import org.apache.beam.runners.dataflow.worker.graph.Edges.Edge;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.ExecutionLocation;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.Node;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.Graphs;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.MutableNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link DeduceFlattenLocationsFunction}. Certain tests are based on the table described
 * in {@link DeduceFlattenLocationsFunction}.
 */
@RunWith(JUnit4.class)
public final class DeduceFlattenLocationsFunctionTest {
    @Test
    public void testEmptyNetwork() throws Exception {
        Assert.assertTrue(Graphs.equivalent(DeduceFlattenLocationsFunctionTest.createEmptyNetwork(), new DeduceFlattenLocationsFunction().apply(DeduceFlattenLocationsFunctionTest.createEmptyNetwork())));
    }

    /* In the following tests, the desired results should match the table described in {@link
    DeduceFlattenLocationsFunction}.
     */
    @Test
    public void testDeductionFromSdkToSdk() throws Exception {
        // sdk_predecessor --> flatten --> pcollection --> sdk_successor
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(SDK_HARNESS, SDK_HARNESS, SDK_HARNESS);
    }

    @Test
    public void testDeductionFromSdkToRunner() throws Exception {
        // sdk_predecessor --> flatten --> pcollection --> runner_successor
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(SDK_HARNESS, RUNNER_HARNESS, RUNNER_HARNESS);
    }

    @Test
    public void testDeductionFromSdkToBoth() throws Exception {
        // sdk_predecessor --> flatten --> pcollection --> sdk_successor
        // \-> runner_successor
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(SDK_HARNESS, AMBIGUOUS, SDK_HARNESS);
    }

    @Test
    public void testDeductionFromSdkToNeither() throws Exception {
        // sdk_predecessor --> flatten --> pcollection
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(SDK_HARNESS, UNKNOWN, SDK_HARNESS);
    }

    @Test
    public void testDeductionFromRunnerToSdk() throws Exception {
        // runner_predecessor --> flatten --> pcollection --> sdk_successor
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(RUNNER_HARNESS, SDK_HARNESS, RUNNER_HARNESS);
    }

    @Test
    public void testDeductionFromRunnerToRunner() throws Exception {
        // sdk_predecessor --> flatten --> pcollection --> runner_successor
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(RUNNER_HARNESS, RUNNER_HARNESS, RUNNER_HARNESS);
    }

    @Test
    public void testDeductionFromRunnerToBoth() throws Exception {
        // runner_predecessor --> flatten --> pcollection --> sdk_successor
        // \-> runner_successor
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(RUNNER_HARNESS, AMBIGUOUS, RUNNER_HARNESS);
    }

    @Test
    public void testDeductionFromRunnerToNeither() throws Exception {
        // runner_predecessor --> flatten --> pcollection
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(RUNNER_HARNESS, UNKNOWN, RUNNER_HARNESS);
    }

    @Test
    public void testDeductionFromBothToSdk() throws Exception {
        // sdk_predecessor ----> flatten --> pcollection --> sdk_successor
        // runner_predecessor -/
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(AMBIGUOUS, SDK_HARNESS, SDK_HARNESS);
    }

    @Test
    public void testDeductionFromBothToRunner() throws Exception {
        // sdk_predecessor ----> flatten --> pcollection --> runner_successor
        // runner_predecessor -/
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(AMBIGUOUS, RUNNER_HARNESS, RUNNER_HARNESS);
    }

    @Test
    public void testDeductionFromBothToBoth() throws Exception {
        // sdk_predecessor ----> flatten --> pcollection --> sdk_successor
        // runner_predecessor -/                         \-> runner_successor
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(AMBIGUOUS, AMBIGUOUS, AMBIGUOUS);
    }

    @Test
    public void testDeductionFromBothToNeither() throws Exception {
        // sdk_predecessor ----> flatten --> pcollection
        // runner_predecessor -/
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(AMBIGUOUS, UNKNOWN, RUNNER_HARNESS);
    }

    @Test
    public void testDeductionFromNeitherToSdk() throws Exception {
        // flatten --> pcollection --> sdk_successor
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(UNKNOWN, SDK_HARNESS, SDK_HARNESS);
    }

    @Test
    public void testDeductionFromNeitherToRunner() throws Exception {
        // flatten --> pcollection --> runner_successor
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(UNKNOWN, RUNNER_HARNESS, RUNNER_HARNESS);
    }

    @Test
    public void testDeductionFromNeitherToBoth() throws Exception {
        // flatten --> pcollection --> sdk_successor
        // \-> runner_successor
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(UNKNOWN, AMBIGUOUS, RUNNER_HARNESS);
    }

    @Test
    public void testDeductionFromNeitherToNeither() throws Exception {
        // flatten --> pcollection
        // 
        DeduceFlattenLocationsFunctionTest.assertSingleFlattenLocationDeduction(UNKNOWN, UNKNOWN, RUNNER_HARNESS);
    }

    /**
     * Test that when multiple flattens with PCollections are connected, they are deduced.
     */
    @Test
    public void testDeductionOfChainedFlattens() throws Exception {
        // sdk_node1 --> out --\
        // sdk_node2 --> out --> flatten1 --> out ----\                /-> sdk_node3 --> out
        // flatten3 --> out
        // runner_node1 --> out --> flatten2 --> out -/                \-> runner_node3 --> out
        // runner_node2 --> out --/
        MutableNetwork<Node, Edge> network = DeduceFlattenLocationsFunctionTest.createEmptyNetwork();
        Node sdkNode1 = DeduceFlattenLocationsFunctionTest.createSdkNode("sdk_node1");
        Node sdkNode1Output = DeduceFlattenLocationsFunctionTest.createPCollection("sdk_node1.out");
        Node sdkNode2 = DeduceFlattenLocationsFunctionTest.createSdkNode("sdk_node2");
        Node sdkNode2Output = DeduceFlattenLocationsFunctionTest.createPCollection("sdk_node2.out");
        Node sdkNode3 = DeduceFlattenLocationsFunctionTest.createSdkNode("sdk_node3");
        Node sdkNode3Output = DeduceFlattenLocationsFunctionTest.createPCollection("sdk_node3.out");
        Node runnerNode1 = DeduceFlattenLocationsFunctionTest.createRunnerNode("runner_node1");
        Node runnerNode1Output = DeduceFlattenLocationsFunctionTest.createPCollection("runner_node1.out");
        Node runnerNode2 = DeduceFlattenLocationsFunctionTest.createRunnerNode("runner_node2");
        Node runnerNode2Output = DeduceFlattenLocationsFunctionTest.createPCollection("runner_node2.out");
        Node runnerNode3 = DeduceFlattenLocationsFunctionTest.createRunnerNode("runner_node3");
        Node runnerNode3Output = DeduceFlattenLocationsFunctionTest.createPCollection("runner_node3.out");
        Node flatten1 = DeduceFlattenLocationsFunctionTest.createFlatten("flatten1");
        Node flatten1Output = DeduceFlattenLocationsFunctionTest.createPCollection("flatten1.out");
        Node flatten2 = DeduceFlattenLocationsFunctionTest.createFlatten("flatten2");
        Node flatten2Output = DeduceFlattenLocationsFunctionTest.createPCollection("flatten2.out");
        Node flatten3 = DeduceFlattenLocationsFunctionTest.createFlatten("flatten3");
        Node flatten3Output = DeduceFlattenLocationsFunctionTest.createPCollection("flatten3.out");
        network.addNode(sdkNode1);
        network.addNode(sdkNode2);
        network.addNode(sdkNode3);
        network.addNode(runnerNode1);
        network.addNode(runnerNode2);
        network.addNode(runnerNode3);
        network.addNode(flatten1);
        network.addNode(flatten1Output);
        network.addNode(flatten2);
        network.addNode(flatten2Output);
        network.addNode(flatten3);
        network.addNode(flatten3Output);
        network.addEdge(sdkNode1, sdkNode1Output, DefaultEdge.create());
        network.addEdge(sdkNode2, sdkNode2Output, DefaultEdge.create());
        network.addEdge(runnerNode1, runnerNode1Output, DefaultEdge.create());
        network.addEdge(runnerNode2, runnerNode2Output, DefaultEdge.create());
        network.addEdge(sdkNode1Output, flatten1, DefaultEdge.create());
        network.addEdge(sdkNode2Output, flatten1, DefaultEdge.create());
        network.addEdge(runnerNode1Output, flatten2, DefaultEdge.create());
        network.addEdge(runnerNode2Output, flatten2, DefaultEdge.create());
        network.addEdge(flatten1, flatten1Output, DefaultEdge.create());
        network.addEdge(flatten2, flatten2Output, DefaultEdge.create());
        network.addEdge(flatten1Output, flatten3, DefaultEdge.create());
        network.addEdge(flatten2Output, flatten3, DefaultEdge.create());
        network.addEdge(flatten3, flatten3Output, DefaultEdge.create());
        network.addEdge(flatten3Output, sdkNode3, DefaultEdge.create());
        network.addEdge(flatten3Output, runnerNode3, DefaultEdge.create());
        network.addEdge(sdkNode3, sdkNode3Output, DefaultEdge.create());
        network.addEdge(runnerNode3, runnerNode3Output, DefaultEdge.create());
        network = new DeduceFlattenLocationsFunction().apply(network);
        ExecutionLocation flatten1Location = DeduceFlattenLocationsFunctionTest.getExecutionLocationOf("flatten1", network);
        Assert.assertEquals(flatten1Location, SDK_HARNESS);
        ExecutionLocation flatten2Location = DeduceFlattenLocationsFunctionTest.getExecutionLocationOf("flatten2", network);
        Assert.assertEquals(flatten2Location, RUNNER_HARNESS);
        ExecutionLocation flatten3Location = DeduceFlattenLocationsFunctionTest.getExecutionLocationOf("flatten3", network);
        Assert.assertEquals(flatten3Location, AMBIGUOUS);
    }
}

