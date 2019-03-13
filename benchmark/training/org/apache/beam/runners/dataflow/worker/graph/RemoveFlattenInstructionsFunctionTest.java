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


import Nodes.ExecutionLocation.UNKNOWN;
import com.google.api.services.dataflow.model.FlattenInstruction;
import com.google.api.services.dataflow.model.InstructionOutput;
import com.google.api.services.dataflow.model.MultiOutputInfo;
import com.google.api.services.dataflow.model.ParallelInstruction;
import org.apache.beam.runners.dataflow.worker.graph.Edges.DefaultEdge;
import org.apache.beam.runners.dataflow.worker.graph.Edges.Edge;
import org.apache.beam.runners.dataflow.worker.graph.Edges.MultiOutputInfoEdge;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.InstructionOutputNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.Node;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.ParallelInstructionNode;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.Graphs;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.MutableNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link RemoveFlattenInstructionsFunction}.
 */
@RunWith(JUnit4.class)
public class RemoveFlattenInstructionsFunctionTest {
    private static final String PCOLLECTION_ID = "fakeId";

    @Test
    public void testEmptyNetwork() {
        Assert.assertTrue(Graphs.equivalent(RemoveFlattenInstructionsFunctionTest.createEmptyNetwork(), new RemoveFlattenInstructionsFunction().apply(RemoveFlattenInstructionsFunctionTest.createEmptyNetwork())));
    }

    @Test
    public void testRemoveFlatten() {
        Node a = ParallelInstructionNode.create(new ParallelInstruction().setName("A"), UNKNOWN);
        Node aPCollection = InstructionOutputNode.create(new InstructionOutput().setName("A.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Edge aOutput = DefaultEdge.create();
        Node b = ParallelInstructionNode.create(new ParallelInstruction().setName("B"), UNKNOWN);
        Edge bOutput = DefaultEdge.create();
        Node bPCollection = InstructionOutputNode.create(new InstructionOutput().setName("B.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node flatten = ParallelInstructionNode.create(new ParallelInstruction().setName("Flatten").setFlatten(new FlattenInstruction()), UNKNOWN);
        Node flattenPCollection = InstructionOutputNode.create(new InstructionOutput().setName("Flatten.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node c = ParallelInstructionNode.create(new ParallelInstruction().setName("C"), UNKNOWN);
        Edge cOutput = DefaultEdge.create();
        Node cPCollection = InstructionOutputNode.create(new InstructionOutput().setName("C.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        // A --\
        // Flatten --> C
        // B --/
        MutableNetwork<Node, Edge> network = RemoveFlattenInstructionsFunctionTest.createEmptyNetwork();
        network.addNode(a);
        network.addNode(aPCollection);
        network.addNode(b);
        network.addNode(bPCollection);
        network.addNode(flatten);
        network.addNode(flattenPCollection);
        network.addNode(c);
        network.addNode(cPCollection);
        network.addEdge(a, aPCollection, aOutput);
        network.addEdge(aPCollection, flatten, DefaultEdge.create());
        network.addEdge(b, bPCollection, bOutput);
        network.addEdge(bPCollection, flatten, DefaultEdge.create());
        network.addEdge(flatten, flattenPCollection, DefaultEdge.create());
        network.addEdge(flattenPCollection, c, DefaultEdge.create());
        network.addEdge(c, cPCollection, cOutput);
        // A --\
        // C
        // B --/
        assertThatFlattenIsProperlyRemoved(network);
    }

    @Test
    public void testRemoveFlattenOnMultiOutputInstruction() {
        Node a = ParallelInstructionNode.create(new ParallelInstruction().setName("A"), UNKNOWN);
        Node aOut1PCollection = InstructionOutputNode.create(new InstructionOutput().setName("A.out1"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node aOut2PCollection = InstructionOutputNode.create(new InstructionOutput().setName("A.out2"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node aOut3PCollection = InstructionOutputNode.create(new InstructionOutput().setName("A.out3"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Edge aOut1 = MultiOutputInfoEdge.create(new MultiOutputInfo().setTag("out1"));
        Edge aOut2 = MultiOutputInfoEdge.create(new MultiOutputInfo().setTag("out2"));
        Edge aOut3 = MultiOutputInfoEdge.create(new MultiOutputInfo().setTag("out3"));
        Edge aOut1PCollectionEdge = DefaultEdge.create();
        Node b = ParallelInstructionNode.create(new ParallelInstruction().setName("B"), UNKNOWN);
        Node bOut1PCollection = InstructionOutputNode.create(new InstructionOutput().setName("B.out1"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node bOut2PCollection = InstructionOutputNode.create(new InstructionOutput().setName("B.out1"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Edge bOut1 = MultiOutputInfoEdge.create(new MultiOutputInfo().setTag("out1"));
        Edge bOut2 = MultiOutputInfoEdge.create(new MultiOutputInfo().setTag("out2"));
        Edge bOut1PCollectionEdge = DefaultEdge.create();
        Node flatten = ParallelInstructionNode.create(new ParallelInstruction().setName("Flatten").setFlatten(new FlattenInstruction()), UNKNOWN);
        Node flattenPCollection = InstructionOutputNode.create(new InstructionOutput().setName("Flatten.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node c = ParallelInstructionNode.create(new ParallelInstruction().setName("C"), UNKNOWN);
        Edge cOutput = DefaultEdge.create();
        Node cPCollection = InstructionOutputNode.create(new InstructionOutput().setName("C.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node d = ParallelInstructionNode.create(new ParallelInstruction().setName("D"), UNKNOWN);
        Edge dOutput = DefaultEdge.create();
        Node dPCollection = InstructionOutputNode.create(new InstructionOutput().setName("D.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node e = ParallelInstructionNode.create(new ParallelInstruction().setName("E"), UNKNOWN);
        Edge eOutput = DefaultEdge.create();
        Node ePCollection = InstructionOutputNode.create(new InstructionOutput().setName("E.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        // /-out1-> C
        // A -out2-\
        // \-out3--> Flatten --> D
        // B -out2-/
        // \-out1-> E
        MutableNetwork<Node, Edge> network = RemoveFlattenInstructionsFunctionTest.createEmptyNetwork();
        network.addNode(a);
        network.addNode(aOut1PCollection);
        network.addNode(aOut2PCollection);
        network.addNode(aOut3PCollection);
        network.addNode(b);
        network.addNode(bOut1PCollection);
        network.addNode(bOut2PCollection);
        network.addNode(flatten);
        network.addNode(flattenPCollection);
        network.addNode(c);
        network.addNode(cPCollection);
        network.addNode(d);
        network.addNode(dPCollection);
        network.addNode(e);
        network.addNode(ePCollection);
        network.addEdge(a, aOut1PCollection, aOut1);
        network.addEdge(a, aOut2PCollection, aOut2);
        network.addEdge(a, aOut3PCollection, aOut3);
        network.addEdge(aOut1PCollection, c, aOut1PCollectionEdge);
        network.addEdge(aOut2PCollection, flatten, DefaultEdge.create());
        network.addEdge(aOut3PCollection, flatten, DefaultEdge.create());
        network.addEdge(b, bOut1PCollection, bOut1);
        network.addEdge(b, bOut2PCollection, bOut2);
        network.addEdge(bOut1PCollection, e, bOut1PCollectionEdge);
        network.addEdge(bOut2PCollection, flatten, DefaultEdge.create());
        network.addEdge(flatten, flattenPCollection, DefaultEdge.create());
        network.addEdge(flattenPCollection, d, DefaultEdge.create());
        network.addEdge(c, cPCollection, cOutput);
        network.addEdge(d, dPCollection, dOutput);
        network.addEdge(e, ePCollection, eOutput);
        // /-out1-> C
        // A -out2-\
        // \-out3--> D
        // B -out2-/
        // \-out1-> E
        assertThatFlattenIsProperlyRemoved(network);
    }

    @Test
    public void testMultiLevelFlattenResultingInParallelEdges() {
        Node a = ParallelInstructionNode.create(new ParallelInstruction().setName("A"), UNKNOWN);
        Node aPCollection = InstructionOutputNode.create(new InstructionOutput().setName("A.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Edge aOutput = DefaultEdge.create();
        Node b = ParallelInstructionNode.create(new ParallelInstruction().setName("B"), UNKNOWN);
        Node bOut1PCollection = InstructionOutputNode.create(new InstructionOutput().setName("B.out1"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node bOut2PCollection = InstructionOutputNode.create(new InstructionOutput().setName("B.out1"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Edge bOut1 = MultiOutputInfoEdge.create(new MultiOutputInfo().setTag("out1"));
        Edge bOut2 = MultiOutputInfoEdge.create(new MultiOutputInfo().setTag("out2"));
        Node flatten1 = ParallelInstructionNode.create(new ParallelInstruction().setName("Flatten1").setFlatten(new FlattenInstruction()), UNKNOWN);
        Node flatten1PCollection = InstructionOutputNode.create(new InstructionOutput().setName("Flatten1.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node flatten2 = ParallelInstructionNode.create(new ParallelInstruction().setName("Flatten2").setFlatten(new FlattenInstruction()), UNKNOWN);
        Node flatten2PCollection = InstructionOutputNode.create(new InstructionOutput().setName("Flatten2.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node c = ParallelInstructionNode.create(new ParallelInstruction().setName("C"), UNKNOWN);
        Edge cOutput = DefaultEdge.create();
        Node cPCollection = InstructionOutputNode.create(new InstructionOutput().setName("C.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        // A ------\
        // Flatten1 --\
        // B -out1-/            Flatten2 --> C
        // \-out2-------------/
        MutableNetwork<Node, Edge> network = RemoveFlattenInstructionsFunctionTest.createEmptyNetwork();
        network.addNode(a);
        network.addNode(aPCollection);
        network.addNode(b);
        network.addNode(bOut1PCollection);
        network.addNode(bOut2PCollection);
        network.addNode(flatten1);
        network.addNode(flatten1PCollection);
        network.addNode(flatten2);
        network.addNode(flatten2PCollection);
        network.addNode(c);
        network.addNode(cPCollection);
        network.addEdge(a, aPCollection, aOutput);
        network.addEdge(aPCollection, flatten1, DefaultEdge.create());
        network.addEdge(b, bOut1PCollection, bOut1);
        network.addEdge(b, bOut2PCollection, bOut2);
        network.addEdge(bOut1PCollection, flatten1, DefaultEdge.create());
        network.addEdge(bOut2PCollection, flatten2, DefaultEdge.create());
        network.addEdge(flatten1, flatten1PCollection, DefaultEdge.create());
        network.addEdge(flatten1PCollection, flatten2, DefaultEdge.create());
        network.addEdge(flatten2, flatten2PCollection, DefaultEdge.create());
        network.addEdge(flatten2PCollection, c, DefaultEdge.create());
        network.addEdge(c, cPCollection, cOutput);
        // A ------\
        // B -out1--> C
        // \-out2-/
        assertThatFlattenIsProperlyRemoved(network);
    }

    @Test
    public void testFlattenMultiplePCollectionsHavingMultipleConsumers() {
        Node a = ParallelInstructionNode.create(new ParallelInstruction().setName("A"), UNKNOWN);
        Node aPCollection = InstructionOutputNode.create(new InstructionOutput().setName("A.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Edge aOutput = DefaultEdge.create();
        Node b = ParallelInstructionNode.create(new ParallelInstruction().setName("B"), UNKNOWN);
        Edge bOutput = DefaultEdge.create();
        Node bPCollection = InstructionOutputNode.create(new InstructionOutput().setName("B.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node flatten = ParallelInstructionNode.create(new ParallelInstruction().setName("Flatten").setFlatten(new FlattenInstruction()), UNKNOWN);
        Node flattenPCollection = InstructionOutputNode.create(new InstructionOutput().setName("Flatten.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node c = ParallelInstructionNode.create(new ParallelInstruction().setName("C"), UNKNOWN);
        Edge cOutput = DefaultEdge.create();
        Node cPCollection = InstructionOutputNode.create(new InstructionOutput().setName("C.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        Node d = ParallelInstructionNode.create(new ParallelInstruction().setName("D"), UNKNOWN);
        Edge dOutput = DefaultEdge.create();
        Node dPCollection = InstructionOutputNode.create(new InstructionOutput().setName("D.out"), RemoveFlattenInstructionsFunctionTest.PCOLLECTION_ID);
        // A --\
        // -> Flatten --> C
        // B --/-------------> D
        MutableNetwork<Node, Edge> network = RemoveFlattenInstructionsFunctionTest.createEmptyNetwork();
        network.addNode(a);
        network.addNode(aPCollection);
        network.addNode(b);
        network.addNode(bPCollection);
        network.addNode(flatten);
        network.addNode(flattenPCollection);
        network.addNode(c);
        network.addNode(cPCollection);
        network.addEdge(a, aPCollection, aOutput);
        network.addEdge(aPCollection, flatten, DefaultEdge.create());
        network.addEdge(b, bPCollection, bOutput);
        network.addEdge(bPCollection, flatten, DefaultEdge.create());
        network.addEdge(bPCollection, d, DefaultEdge.create());
        network.addEdge(flatten, flattenPCollection, DefaultEdge.create());
        network.addEdge(flattenPCollection, c, DefaultEdge.create());
        network.addEdge(c, cPCollection, cOutput);
        network.addEdge(d, dPCollection, dOutput);
        // A --\
        // -> C
        // B --/-> D
        assertThatFlattenIsProperlyRemoved(network);
    }
}

