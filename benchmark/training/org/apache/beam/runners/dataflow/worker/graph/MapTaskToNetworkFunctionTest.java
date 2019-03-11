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


import com.google.api.services.dataflow.model.FlattenInstruction;
import com.google.api.services.dataflow.model.InstructionOutput;
import com.google.api.services.dataflow.model.MapTask;
import com.google.api.services.dataflow.model.MultiOutputInfo;
import com.google.api.services.dataflow.model.ParDoInstruction;
import com.google.api.services.dataflow.model.ParallelInstruction;
import com.google.api.services.dataflow.model.PartialGroupByKeyInstruction;
import com.google.api.services.dataflow.model.ReadInstruction;
import com.google.api.services.dataflow.model.WriteInstruction;
import org.apache.beam.runners.dataflow.worker.graph.Edges.Edge;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.InstructionOutputNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.Node;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.ParallelInstructionNode;
import org.apache.beam.sdk.fn.IdGenerators;
import org.apache.beam.sdk.util.Transport;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.Network;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link MapTaskToNetworkFunction}.
 */
@RunWith(JUnit4.class)
public class MapTaskToNetworkFunctionTest {
    @Test
    public void testEmptyMapTask() {
        Network<Node, Edge> network = new MapTaskToNetworkFunction(IdGenerators.decrementingLongs()).apply(new MapTask());
        Assert.assertTrue(network.isDirected());
        Assert.assertTrue(network.allowsParallelEdges());
        Assert.assertFalse(network.allowsSelfLoops());
        Assert.assertThat(network.nodes(), emptyCollectionOf(Node.class));
    }

    @Test
    public void testRead() {
        InstructionOutput readOutput = MapTaskToNetworkFunctionTest.createInstructionOutput("Read.out");
        ParallelInstruction read = MapTaskToNetworkFunctionTest.createParallelInstruction("Read", readOutput);
        read.setRead(new ReadInstruction());
        MapTask mapTask = new MapTask();
        mapTask.setInstructions(ImmutableList.of(read));
        mapTask.setFactory(Transport.getJsonFactory());
        Network<Node, Edge> network = new MapTaskToNetworkFunction(IdGenerators.decrementingLongs()).apply(mapTask);
        MapTaskToNetworkFunctionTest.assertNetworkProperties(network);
        Assert.assertEquals(2, network.nodes().size());
        Assert.assertEquals(1, network.edges().size());
        ParallelInstructionNode readNode = MapTaskToNetworkFunctionTest.get(network, read);
        InstructionOutputNode readOutputNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readNode);
        Assert.assertEquals(readOutput, readOutputNode.getInstructionOutput());
    }

    @Test
    public void testParDo() {
        InstructionOutput readOutput = MapTaskToNetworkFunctionTest.createInstructionOutput("Read.out");
        ParallelInstruction read = MapTaskToNetworkFunctionTest.createParallelInstruction("Read", readOutput);
        read.setRead(new ReadInstruction());
        MultiOutputInfo parDoMultiOutput = MapTaskToNetworkFunctionTest.createMultiOutputInfo("output");
        ParDoInstruction parDoInstruction = new ParDoInstruction();
        parDoInstruction.setInput(MapTaskToNetworkFunctionTest.createInstructionInput(0, 0));// Read.out

        parDoInstruction.setMultiOutputInfos(ImmutableList.of(parDoMultiOutput));
        InstructionOutput parDoOutput = MapTaskToNetworkFunctionTest.createInstructionOutput("ParDo.out");
        ParallelInstruction parDo = MapTaskToNetworkFunctionTest.createParallelInstruction("ParDo", parDoOutput);
        parDo.setParDo(parDoInstruction);
        MapTask mapTask = new MapTask();
        mapTask.setInstructions(ImmutableList.of(read, parDo));
        mapTask.setFactory(Transport.getJsonFactory());
        Network<Node, Edge> network = new MapTaskToNetworkFunction(IdGenerators.decrementingLongs()).apply(mapTask);
        MapTaskToNetworkFunctionTest.assertNetworkProperties(network);
        Assert.assertEquals(4, network.nodes().size());
        Assert.assertEquals(3, network.edges().size());
        ParallelInstructionNode readNode = MapTaskToNetworkFunctionTest.get(network, read);
        InstructionOutputNode readOutputNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readNode);
        Assert.assertEquals(readOutput, readOutputNode.getInstructionOutput());
        ParallelInstructionNode parDoNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readOutputNode);
        InstructionOutputNode parDoOutputNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, parDoNode);
        Assert.assertEquals(parDoOutput, parDoOutputNode.getInstructionOutput());
        Assert.assertEquals(parDoMultiOutput, getMultiOutputInfo());
    }

    @Test
    public void testFlatten() {
        // ReadA --\
        // |--> Flatten
        // ReadB --/
        InstructionOutput readOutputA = MapTaskToNetworkFunctionTest.createInstructionOutput("ReadA.out");
        ParallelInstruction readA = MapTaskToNetworkFunctionTest.createParallelInstruction("ReadA", readOutputA);
        readA.setRead(new ReadInstruction());
        InstructionOutput readOutputB = MapTaskToNetworkFunctionTest.createInstructionOutput("ReadB.out");
        ParallelInstruction readB = MapTaskToNetworkFunctionTest.createParallelInstruction("ReadB", readOutputB);
        readB.setRead(new ReadInstruction());
        FlattenInstruction flattenInstruction = new FlattenInstruction();
        flattenInstruction.setInputs(// ReadA.out
        ImmutableList.of(MapTaskToNetworkFunctionTest.createInstructionInput(0, 0), MapTaskToNetworkFunctionTest.createInstructionInput(1, 0)));// ReadB.out

        InstructionOutput flattenOutput = MapTaskToNetworkFunctionTest.createInstructionOutput("Flatten.out");
        ParallelInstruction flatten = MapTaskToNetworkFunctionTest.createParallelInstruction("Flatten", flattenOutput);
        flatten.setFlatten(flattenInstruction);
        MapTask mapTask = new MapTask();
        mapTask.setInstructions(ImmutableList.of(readA, readB, flatten));
        mapTask.setFactory(Transport.getJsonFactory());
        Network<Node, Edge> network = new MapTaskToNetworkFunction(IdGenerators.decrementingLongs()).apply(mapTask);
        MapTaskToNetworkFunctionTest.assertNetworkProperties(network);
        Assert.assertEquals(6, network.nodes().size());
        Assert.assertEquals(5, network.edges().size());
        ParallelInstructionNode readANode = MapTaskToNetworkFunctionTest.get(network, readA);
        InstructionOutputNode readOutputANode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readANode);
        Assert.assertEquals(readOutputA, readOutputANode.getInstructionOutput());
        ParallelInstructionNode readBNode = MapTaskToNetworkFunctionTest.get(network, readB);
        InstructionOutputNode readOutputBNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readBNode);
        Assert.assertEquals(readOutputB, readOutputBNode.getInstructionOutput());
        // Make sure the successors for both ReadA and ReadB output PCollections are the same
        Assert.assertEquals(network.successors(readOutputANode), network.successors(readOutputBNode));
        ParallelInstructionNode flattenNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readOutputANode);
        InstructionOutputNode flattenOutputNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, flattenNode);
        Assert.assertEquals(flattenOutput, flattenOutputNode.getInstructionOutput());
    }

    @Test
    public void testParallelEdgeFlatten() {
        // /---\
        // Read --> Read.out --> Flatten
        // \---/
        InstructionOutput readOutput = MapTaskToNetworkFunctionTest.createInstructionOutput("Read.out");
        ParallelInstruction read = MapTaskToNetworkFunctionTest.createParallelInstruction("Read", readOutput);
        read.setRead(new ReadInstruction());
        FlattenInstruction flattenInstruction = new FlattenInstruction();
        flattenInstruction.setInputs(// Read.out
        // Read.out
        ImmutableList.of(MapTaskToNetworkFunctionTest.createInstructionInput(0, 0), MapTaskToNetworkFunctionTest.createInstructionInput(0, 0), MapTaskToNetworkFunctionTest.createInstructionInput(0, 0)));// Read.out

        InstructionOutput flattenOutput = MapTaskToNetworkFunctionTest.createInstructionOutput("Flatten.out");
        ParallelInstruction flatten = MapTaskToNetworkFunctionTest.createParallelInstruction("Flatten", flattenOutput);
        flatten.setFlatten(flattenInstruction);
        MapTask mapTask = new MapTask();
        mapTask.setInstructions(ImmutableList.of(read, flatten));
        mapTask.setFactory(Transport.getJsonFactory());
        Network<Node, Edge> network = new MapTaskToNetworkFunction(IdGenerators.decrementingLongs()).apply(mapTask);
        MapTaskToNetworkFunctionTest.assertNetworkProperties(network);
        Assert.assertEquals(4, network.nodes().size());
        Assert.assertEquals(5, network.edges().size());
        ParallelInstructionNode readNode = MapTaskToNetworkFunctionTest.get(network, read);
        InstructionOutputNode readOutputNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readNode);
        Assert.assertEquals(readOutput, readOutputNode.getInstructionOutput());
        ParallelInstructionNode flattenNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readOutputNode);
        // Assert that the three parallel edges are maintained
        Assert.assertEquals(3, network.edgesConnecting(readOutputNode, flattenNode).size());
        InstructionOutputNode flattenOutputNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, flattenNode);
        Assert.assertEquals(flattenOutput, flattenOutputNode.getInstructionOutput());
    }

    @Test
    public void testWrite() {
        InstructionOutput readOutput = MapTaskToNetworkFunctionTest.createInstructionOutput("Read.out");
        ParallelInstruction read = MapTaskToNetworkFunctionTest.createParallelInstruction("Read", readOutput);
        read.setRead(new ReadInstruction());
        WriteInstruction writeInstruction = new WriteInstruction();
        writeInstruction.setInput(MapTaskToNetworkFunctionTest.createInstructionInput(0, 0));// Read.out

        ParallelInstruction write = MapTaskToNetworkFunctionTest.createParallelInstruction("Write");
        write.setWrite(writeInstruction);
        MapTask mapTask = new MapTask();
        mapTask.setInstructions(ImmutableList.of(read, write));
        mapTask.setFactory(Transport.getJsonFactory());
        Network<Node, Edge> network = new MapTaskToNetworkFunction(IdGenerators.decrementingLongs()).apply(mapTask);
        MapTaskToNetworkFunctionTest.assertNetworkProperties(network);
        Assert.assertEquals(3, network.nodes().size());
        Assert.assertEquals(2, network.edges().size());
        ParallelInstructionNode readNode = MapTaskToNetworkFunctionTest.get(network, read);
        InstructionOutputNode readOutputNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readNode);
        Assert.assertEquals(readOutput, readOutputNode.getInstructionOutput());
        ParallelInstructionNode writeNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readOutputNode);
        Assert.assertNotNull(writeNode);
    }

    @Test
    public void testPartialGroupByKey() {
        // Read --> PGBK --> Write
        InstructionOutput readOutput = MapTaskToNetworkFunctionTest.createInstructionOutput("Read.out");
        ParallelInstruction read = MapTaskToNetworkFunctionTest.createParallelInstruction("Read", readOutput);
        read.setRead(new ReadInstruction());
        PartialGroupByKeyInstruction pgbkInstruction = new PartialGroupByKeyInstruction();
        pgbkInstruction.setInput(MapTaskToNetworkFunctionTest.createInstructionInput(0, 0));// Read.out

        InstructionOutput pgbkOutput = MapTaskToNetworkFunctionTest.createInstructionOutput("PGBK.out");
        ParallelInstruction pgbk = MapTaskToNetworkFunctionTest.createParallelInstruction("PGBK", pgbkOutput);
        pgbk.setPartialGroupByKey(pgbkInstruction);
        WriteInstruction writeInstruction = new WriteInstruction();
        writeInstruction.setInput(MapTaskToNetworkFunctionTest.createInstructionInput(1, 0));// PGBK.out

        ParallelInstruction write = MapTaskToNetworkFunctionTest.createParallelInstruction("Write");
        write.setWrite(writeInstruction);
        MapTask mapTask = new MapTask();
        mapTask.setInstructions(ImmutableList.of(read, pgbk, write));
        mapTask.setFactory(Transport.getJsonFactory());
        Network<Node, Edge> network = new MapTaskToNetworkFunction(IdGenerators.decrementingLongs()).apply(mapTask);
        MapTaskToNetworkFunctionTest.assertNetworkProperties(network);
        Assert.assertEquals(5, network.nodes().size());
        Assert.assertEquals(4, network.edges().size());
        ParallelInstructionNode readNode = MapTaskToNetworkFunctionTest.get(network, read);
        InstructionOutputNode readOutputNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readNode);
        Assert.assertEquals(readOutput, readOutputNode.getInstructionOutput());
        ParallelInstructionNode pgbkNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, readOutputNode);
        InstructionOutputNode pgbkOutputNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, pgbkNode);
        Assert.assertEquals(pgbkOutput, pgbkOutputNode.getInstructionOutput());
        ParallelInstructionNode writeNode = MapTaskToNetworkFunctionTest.getOnlySuccessor(network, pgbkOutputNode);
        Assert.assertNotNull(write);
    }

    @Test
    public void testMultipleOutput() {
        // /---> WriteA
        // Read ---> ParDo
        // \---> WriteB
        InstructionOutput readOutput = MapTaskToNetworkFunctionTest.createInstructionOutput("Read.out");
        ParallelInstruction read = MapTaskToNetworkFunctionTest.createParallelInstruction("Read", readOutput);
        read.setRead(new ReadInstruction());
        MultiOutputInfo parDoMultiOutput1 = MapTaskToNetworkFunctionTest.createMultiOutputInfo("output1");
        MultiOutputInfo parDoMultiOutput2 = MapTaskToNetworkFunctionTest.createMultiOutputInfo("output2");
        ParDoInstruction parDoInstruction = new ParDoInstruction();
        parDoInstruction.setInput(MapTaskToNetworkFunctionTest.createInstructionInput(0, 0));// Read.out

        parDoInstruction.setMultiOutputInfos(ImmutableList.of(parDoMultiOutput1, parDoMultiOutput2));
        InstructionOutput parDoOutput1 = MapTaskToNetworkFunctionTest.createInstructionOutput("ParDo.out1");
        InstructionOutput parDoOutput2 = MapTaskToNetworkFunctionTest.createInstructionOutput("ParDo.out2");
        ParallelInstruction parDo = MapTaskToNetworkFunctionTest.createParallelInstruction("ParDo", parDoOutput1, parDoOutput2);
        parDo.setParDo(parDoInstruction);
        WriteInstruction writeAInstruction = new WriteInstruction();
        writeAInstruction.setInput(MapTaskToNetworkFunctionTest.createInstructionInput(1, 0));// ParDo.out1

        ParallelInstruction writeA = MapTaskToNetworkFunctionTest.createParallelInstruction("WriteA");
        writeA.setWrite(writeAInstruction);
        WriteInstruction writeBInstruction = new WriteInstruction();
        writeBInstruction.setInput(MapTaskToNetworkFunctionTest.createInstructionInput(1, 1));// ParDo.out2

        ParallelInstruction writeB = MapTaskToNetworkFunctionTest.createParallelInstruction("WriteB");
        writeB.setWrite(writeBInstruction);
        MapTask mapTask = new MapTask();
        mapTask.setInstructions(ImmutableList.of(read, parDo, writeA, writeB));
        mapTask.setFactory(Transport.getJsonFactory());
        Network<Node, Edge> network = new MapTaskToNetworkFunction(IdGenerators.decrementingLongs()).apply(mapTask);
        MapTaskToNetworkFunctionTest.assertNetworkProperties(network);
        Assert.assertEquals(7, network.nodes().size());
        Assert.assertEquals(6, network.edges().size());
        ParallelInstructionNode parDoNode = MapTaskToNetworkFunctionTest.get(network, parDo);
        ParallelInstructionNode writeANode = MapTaskToNetworkFunctionTest.get(network, writeA);
        ParallelInstructionNode writeBNode = MapTaskToNetworkFunctionTest.get(network, writeB);
        InstructionOutputNode parDoOutput1Node = MapTaskToNetworkFunctionTest.getOnlyPredecessor(network, writeANode);
        Assert.assertEquals(parDoOutput1, parDoOutput1Node.getInstructionOutput());
        InstructionOutputNode parDoOutput2Node = MapTaskToNetworkFunctionTest.getOnlyPredecessor(network, writeBNode);
        Assert.assertEquals(parDoOutput2, parDoOutput2Node.getInstructionOutput());
        Assert.assertThat(network.successors(parDoNode), Matchers.<Node>containsInAnyOrder(parDoOutput1Node, parDoOutput2Node));
        Assert.assertEquals(parDoMultiOutput1, getMultiOutputInfo());
        Assert.assertEquals(parDoMultiOutput2, getMultiOutputInfo());
    }
}

