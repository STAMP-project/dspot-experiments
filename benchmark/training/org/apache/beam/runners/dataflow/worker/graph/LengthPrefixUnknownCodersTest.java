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


import GlobalWindow.Coder.INSTANCE;
import WorkerPropertyNames.INPUT_CODER;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.dataflow.model.InstructionOutput;
import com.google.api.services.dataflow.model.ParDoInstruction;
import com.google.api.services.dataflow.model.ParallelInstruction;
import com.google.api.services.dataflow.model.ReadInstruction;
import com.google.api.services.dataflow.model.SideInputInfo;
import com.google.api.services.dataflow.model.Sink;
import com.google.api.services.dataflow.model.Source;
import com.google.api.services.dataflow.model.WriteInstruction;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.util.CloudObjects;
import org.apache.beam.runners.dataflow.worker.graph.Edges.DefaultEdge;
import org.apache.beam.runners.dataflow.worker.graph.Edges.Edge;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.InstructionOutputNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.Node;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.ParallelInstructionNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.RemoteGrpcPortNode;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.LengthPrefixCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.MutableNetwork;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link LengthPrefixUnknownCoders}.
 */
@RunWith(JUnit4.class)
public class LengthPrefixUnknownCodersTest {
    private static final Coder<WindowedValue<KV<String, Integer>>> windowedValueCoder = WindowedValue.getFullCoder(KvCoder.of(StringUtf8Coder.of(), VarIntCoder.of()), INSTANCE);

    private static final Coder<WindowedValue<KV<String, Integer>>> prefixedWindowedValueCoder = WindowedValue.getFullCoder(KvCoder.of(LengthPrefixCoder.of(StringUtf8Coder.of()), LengthPrefixCoder.of(VarIntCoder.of())), INSTANCE);

    private static final Coder<WindowedValue<KV<byte[], byte[]>>> prefixedAndReplacedWindowedValueCoder = WindowedValue.getFullCoder(KvCoder.of(LengthPrefixUnknownCoders.LENGTH_PREFIXED_BYTE_ARRAY_CODER, LengthPrefixUnknownCoders.LENGTH_PREFIXED_BYTE_ARRAY_CODER), INSTANCE);

    private static final String MERGE_BUCKETS_DO_FN = "MergeBucketsDoFn";

    private ParallelInstruction instruction;

    private InstructionOutputNode instructionOutputNode;

    private MutableNetwork<Node, Edge> network;

    @Mock
    private RemoteGrpcPortNode grpcPortNode;

    /**
     * Test wrapping unknown coders with {@code LengthPrefixCoder}
     */
    @Test
    public void testLengthPrefixUnknownCoders() throws Exception {
        Map<String, Object> lengthPrefixedCoderCloudObject = LengthPrefixUnknownCoders.forCodec(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.windowedValueCoder, null), false);
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder, null), lengthPrefixedCoderCloudObject);
    }

    /**
     * Test bypassing unknown coders that are already wrapped with {@code LengthPrefixCoder}
     */
    @Test
    public void testLengthPrefixForLengthPrefixCoder() throws Exception {
        Coder<WindowedValue<KV<String, Integer>>> windowedValueCoder = WindowedValue.getFullCoder(KvCoder.of(StringUtf8Coder.of(), LengthPrefixCoder.of(VarIntCoder.of())), INSTANCE);
        Map<String, Object> lengthPrefixedCoderCloudObject = LengthPrefixUnknownCoders.forCodec(/* sdkComponents= */
        CloudObjects.asCloudObject(windowedValueCoder, null), false);
        Coder<WindowedValue<KV<String, Integer>>> expectedCoder = WindowedValue.getFullCoder(KvCoder.of(LengthPrefixCoder.of(StringUtf8Coder.of()), LengthPrefixCoder.of(VarIntCoder.of())), INSTANCE);
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(expectedCoder, null), lengthPrefixedCoderCloudObject);
    }

    /**
     * Test replacing unknown coders with {@code LengthPrefixCoder<ByteArray>}
     */
    @Test
    public void testLengthPrefixAndReplaceUnknownCoder() throws Exception {
        Coder<WindowedValue<KV<String, Integer>>> windowedValueCoder = WindowedValue.getFullCoder(KvCoder.of(LengthPrefixCoder.of(StringUtf8Coder.of()), VarIntCoder.of()), INSTANCE);
        Map<String, Object> lengthPrefixedCoderCloudObject = LengthPrefixUnknownCoders.forCodec(/* sdkComponents= */
        CloudObjects.asCloudObject(windowedValueCoder, null), true);
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.prefixedAndReplacedWindowedValueCoder, null), lengthPrefixedCoderCloudObject);
    }

    @Test
    public void testLengthPrefixInstructionOutputCoder() throws Exception {
        InstructionOutput output = new InstructionOutput();
        output.setCodec(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.windowedValueCoder, null));
        output.setFactory(new JacksonFactory());
        InstructionOutput prefixedOutput = LengthPrefixUnknownCoders.forInstructionOutput(output, false);
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder, null), prefixedOutput.getCodec());
        // Should not mutate the instruction.
        Assert.assertEquals(output.getCodec(), /* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.windowedValueCoder, null));
    }

    @Test
    public void testLengthPrefixReadInstructionCoder() throws Exception {
        ReadInstruction readInstruction = new ReadInstruction();
        readInstruction.setSource(new Source().setCodec(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.windowedValueCoder, null)));
        instruction.setRead(readInstruction);
        ParallelInstruction prefixedInstruction = LengthPrefixUnknownCoders.forParallelInstruction(instruction, false);
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder, null), prefixedInstruction.getRead().getSource().getCodec());
        // Should not mutate the instruction.
        Assert.assertEquals(readInstruction.getSource().getCodec(), /* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.windowedValueCoder, null));
    }

    @Test
    public void testLengthPrefixWriteInstructionCoder() throws Exception {
        WriteInstruction writeInstruction = new WriteInstruction();
        writeInstruction.setSink(new Sink().setCodec(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.windowedValueCoder, null)));
        instruction.setWrite(writeInstruction);
        ParallelInstruction prefixedInstruction = LengthPrefixUnknownCoders.forParallelInstruction(instruction, false);
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder, null), prefixedInstruction.getWrite().getSink().getCodec());
        // Should not mutate the instruction.
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.windowedValueCoder, null), writeInstruction.getSink().getCodec());
    }

    @Test
    public void testLengthPrefixParDoInstructionCoder() throws Exception {
        ParDoInstruction parDo = new ParDoInstruction();
        CloudObject spec = CloudObject.forClassName(LengthPrefixUnknownCodersTest.MERGE_BUCKETS_DO_FN);
        spec.put(INPUT_CODER, /* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.windowedValueCoder, null));
        parDo.setUserFn(spec);
        instruction.setParDo(parDo);
        ParallelInstruction prefixedInstruction = LengthPrefixUnknownCoders.forParallelInstruction(instruction, false);
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder, null), prefixedInstruction.getParDo().getUserFn().get(INPUT_CODER));
        // Should not mutate the instruction.
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.windowedValueCoder, null), parDo.getUserFn().get(INPUT_CODER));
    }

    @Test
    public void testClone() throws Exception {
        ParallelInstruction copy = LengthPrefixUnknownCoders.clone(instruction, ParallelInstruction.class);
        Assert.assertNotSame(instruction, copy);
        Assert.assertEquals(instruction, copy);
    }

    @Test
    public void testLengthPrefixAndReplaceForRunnerNetwork() {
        Node readNode = LengthPrefixUnknownCodersTest.createReadNode("Read", "Source", LengthPrefixUnknownCodersTest.windowedValueCoder);
        Edge readNodeEdge = DefaultEdge.create();
        Node readNodeOut = LengthPrefixUnknownCodersTest.createInstructionOutputNode("Read.out", LengthPrefixUnknownCodersTest.windowedValueCoder);
        MutableNetwork<Node, Edge> network = LengthPrefixUnknownCodersTest.createEmptyNetwork();
        network.addNode(readNode);
        network.addNode(readNodeOut);
        network.addEdge(readNode, readNodeOut, readNodeEdge);
        ParallelInstructionNode prefixedReadNode = LengthPrefixUnknownCodersTest.createReadNode("Read", "Source", LengthPrefixUnknownCodersTest.prefixedAndReplacedWindowedValueCoder);
        InstructionOutputNode prefixedReadNodeOut = LengthPrefixUnknownCodersTest.createInstructionOutputNode("Read.out", LengthPrefixUnknownCodersTest.prefixedAndReplacedWindowedValueCoder);
        MutableNetwork<Node, Edge> prefixedNetwork = LengthPrefixUnknownCoders.andReplaceForRunnerNetwork(network);
        Set prefixedInstructions = new HashSet<>();
        for (Node node : prefixedNetwork.nodes()) {
            if (node instanceof ParallelInstructionNode) {
                prefixedInstructions.add(getParallelInstruction());
            } else
                if (node instanceof InstructionOutputNode) {
                    prefixedInstructions.add(getInstructionOutput());
                }

        }
        Set expectedInstructions = ImmutableSet.of(prefixedReadNode.getParallelInstruction(), prefixedReadNodeOut.getInstructionOutput());
        Assert.assertEquals(expectedInstructions, prefixedInstructions);
    }

    @Test
    public void testLengthPrefixForInstructionOutputNodeWithGrpcNodeSuccessor() {
        MutableNetwork<Node, Edge> network = LengthPrefixUnknownCodersTest.createEmptyNetwork();
        network.addNode(instructionOutputNode);
        network.addNode(grpcPortNode);
        network.addEdge(grpcPortNode, instructionOutputNode, DefaultEdge.create());
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder, null), getInstructionOutput().getCodec());
    }

    @Test
    public void testLengthPrefixForInstructionOutputNodeWithGrpcNodePredecessor() {
        MutableNetwork<Node, Edge> network = LengthPrefixUnknownCodersTest.createEmptyNetwork();
        network.addNode(instructionOutputNode);
        network.addNode(grpcPortNode);
        network.addEdge(instructionOutputNode, grpcPortNode, DefaultEdge.create());
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder, null), getInstructionOutput().getCodec());
    }

    @Test
    public void testLengthPrefixForInstructionOutputNodeWithNonGrpcNodeNeighbor() {
        MutableNetwork<Node, Edge> network = LengthPrefixUnknownCodersTest.createEmptyNetwork();
        ParallelInstructionNode readNode = LengthPrefixUnknownCodersTest.createReadNode("read", "source", LengthPrefixUnknownCodersTest.windowedValueCoder);
        network.addNode(instructionOutputNode);
        network.addNode(readNode);
        network.addEdge(readNode, instructionOutputNode, DefaultEdge.create());
        Assert.assertEquals(/* sdkComponents= */
        CloudObjects.asCloudObject(LengthPrefixUnknownCodersTest.windowedValueCoder, null), getInstructionOutput().getCodec());
    }

    @Test
    public void testLengthPrefixForSideInputInfos() {
        List<SideInputInfo> prefixedSideInputInfos = LengthPrefixUnknownCoders.forSideInputInfos(ImmutableList.of(LengthPrefixUnknownCodersTest.createSideInputInfosWithCoders(LengthPrefixUnknownCodersTest.windowedValueCoder, LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder)), false);
        Assert.assertEquals(ImmutableList.of(LengthPrefixUnknownCodersTest.createSideInputInfosWithCoders(LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder, LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder)), prefixedSideInputInfos);
        List<SideInputInfo> prefixedAndReplacedSideInputInfos = LengthPrefixUnknownCoders.forSideInputInfos(ImmutableList.of(LengthPrefixUnknownCodersTest.createSideInputInfosWithCoders(LengthPrefixUnknownCodersTest.windowedValueCoder, LengthPrefixUnknownCodersTest.prefixedWindowedValueCoder)), true);
        Assert.assertEquals(ImmutableList.of(LengthPrefixUnknownCodersTest.createSideInputInfosWithCoders(LengthPrefixUnknownCodersTest.prefixedAndReplacedWindowedValueCoder, LengthPrefixUnknownCodersTest.prefixedAndReplacedWindowedValueCoder)), prefixedAndReplacedSideInputInfos);
    }
}

