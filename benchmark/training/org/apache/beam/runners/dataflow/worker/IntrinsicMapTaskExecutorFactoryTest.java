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
package org.apache.beam.runners.dataflow.worker;


import BatchModeExecutionContext.StepContext;
import ExecutionLocation.UNKNOWN;
import Operation.InitializationState.UNSTARTED;
import com.google.api.services.dataflow.model.MapTask;
import com.google.api.services.dataflow.model.ParallelInstruction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.util.CloudObjects;
import org.apache.beam.runners.dataflow.worker.counters.Counter;
import org.apache.beam.runners.dataflow.worker.counters.Counter.CounterUpdateExtractor;
import org.apache.beam.runners.dataflow.worker.counters.CounterName;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.apache.beam.runners.dataflow.worker.graph.Edges.Edge;
import org.apache.beam.runners.dataflow.worker.graph.Edges.MultiOutputInfoEdge;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.InstructionOutputNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.Node;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.OperationNode;
import org.apache.beam.runners.dataflow.worker.graph.Nodes.ParallelInstructionNode;
import org.apache.beam.runners.dataflow.worker.util.common.worker.FlattenOperation;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ParDoOperation;
import org.apache.beam.runners.dataflow.worker.util.common.worker.ReadOperation;
import org.apache.beam.runners.dataflow.worker.util.common.worker.Sink;
import org.apache.beam.runners.dataflow.worker.util.common.worker.WriteOperation;
import org.apache.beam.sdk.coders.BigEndianIntegerCoder;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.CoderRegistry;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.fn.IdGenerator;
import org.apache.beam.sdk.fn.IdGenerators;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.util.AppliedCombineFn;
import org.apache.beam.sdk.util.Transport;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.MutableNetwork;
import org.apache.beam.vendor.guava.v20_0.com.google.common.graph.Network;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link IntrinsicMapTaskExecutorFactory}.
 */
@RunWith(JUnit4.class)
public class IntrinsicMapTaskExecutorFactoryTest {
    private static final String STAGE = "test";

    private static final IdGenerator idGenerator = IdGenerators.decrementingLongs();

    private static final Function<MapTask, MutableNetwork<Node, Edge>> mapTaskToNetwork = andThen(new org.apache.beam.runners.dataflow.worker.graph.MapTaskToNetworkFunction(IntrinsicMapTaskExecutorFactoryTest.idGenerator));

    private static final CloudObject windowedStringCoder = /* sdkComponents= */
    CloudObjects.asCloudObject(WindowedValue.getValueOnlyCoder(StringUtf8Coder.of()), null);

    private IntrinsicMapTaskExecutorFactory mapTaskExecutorFactory;

    private PipelineOptions options;

    private ReaderRegistry readerRegistry;

    private SinkRegistry sinkRegistry;

    private static final String PCOLLECTION_ID = "fakeId";

    @Mock
    private Network<Node, Edge> network;

    @Mock
    private CounterUpdateExtractor<?> updateExtractor;

    private final CounterSet counterSet = new CounterSet();

    @Test
    public void testCreateMapTaskExecutor() throws Exception {
        List<ParallelInstruction> instructions = Arrays.asList(IntrinsicMapTaskExecutorFactoryTest.createReadInstruction("Read"), IntrinsicMapTaskExecutorFactoryTest.createParDoInstruction(0, 0, "DoFn1"), IntrinsicMapTaskExecutorFactoryTest.createParDoInstruction(0, 0, "DoFnWithContext"), IntrinsicMapTaskExecutorFactoryTest.createFlattenInstruction(1, 0, 2, 0, "Flatten"), IntrinsicMapTaskExecutorFactoryTest.createWriteInstruction(3, 0, "Write"));
        MapTask mapTask = new MapTask();
        mapTask.setStageName(IntrinsicMapTaskExecutorFactoryTest.STAGE);
        mapTask.setSystemName("systemName");
        mapTask.setInstructions(instructions);
        mapTask.setFactory(Transport.getJsonFactory());
        try (DataflowMapTaskExecutor executor = /* beamFnControlClientHandler */
        /* GrpcFnServer<GrpcDataService> */
        /* ApiServiceDescriptor */
        /* GrpcFnServer<GrpcStateService> */
        mapTaskExecutorFactory.create(null, null, null, null, IntrinsicMapTaskExecutorFactoryTest.mapTaskToNetwork.apply(mapTask), options, IntrinsicMapTaskExecutorFactoryTest.STAGE, readerRegistry, sinkRegistry, BatchModeExecutionContext.forTesting(options, counterSet, "testStage"), counterSet, IntrinsicMapTaskExecutorFactoryTest.idGenerator)) {
            // Safe covariant cast not expressible without rawtypes.
            @SuppressWarnings({ "rawtypes", "unchecked" })
            List<Object> operations = ((List) (executor.operations));
            Assert.assertThat(operations, Matchers.hasItems(Matchers.instanceOf(ReadOperation.class), Matchers.instanceOf(ParDoOperation.class), Matchers.instanceOf(ParDoOperation.class), Matchers.instanceOf(FlattenOperation.class), Matchers.instanceOf(WriteOperation.class)));
            // Verify that the inputs are attached.
            ReadOperation readOperation = Iterables.getOnlyElement(Iterables.filter(operations, ReadOperation.class));
            Assert.assertEquals(2, readOperation.receivers[0].getReceiverCount());
            FlattenOperation flattenOperation = Iterables.getOnlyElement(Iterables.filter(operations, FlattenOperation.class));
            for (ParDoOperation operation : Iterables.filter(operations, ParDoOperation.class)) {
                Assert.assertSame(flattenOperation, operation.receivers[0].getOnlyReceiver());
            }
            WriteOperation writeOperation = Iterables.getOnlyElement(Iterables.filter(operations, WriteOperation.class));
            Assert.assertSame(writeOperation, flattenOperation.receivers[0].getOnlyReceiver());
        }
        @SuppressWarnings("unchecked")
        Counter<Long, ?> otherMsecCounter = ((Counter<Long, ?>) (counterSet.getExistingCounter("test-other-msecs")));
        // "other" state only got created upon MapTaskExecutor.execute().
        Assert.assertNull(otherMsecCounter);
        counterSet.extractUpdates(false, updateExtractor);
        IntrinsicMapTaskExecutorFactoryTest.verifyOutputCounters(updateExtractor, "read_output_name", "DoFn1_output", "DoFnWithContext_output", "flatten_output_name");
        Mockito.verify(updateExtractor).longSum(ArgumentMatchers.eq(CounterName.named("Read-ByteCount")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong());
        Mockito.verify(updateExtractor).longSum(ArgumentMatchers.eq(CounterName.named("Write-ByteCount")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong());
        Mockito.verifyNoMoreInteractions(updateExtractor);
    }

    @Test
    public void testExecutionContextPlumbing() throws Exception {
        List<ParallelInstruction> instructions = Arrays.asList(IntrinsicMapTaskExecutorFactoryTest.createReadInstruction("Read", ReaderFactoryTest.SingletonTestReaderFactory.class), IntrinsicMapTaskExecutorFactoryTest.createParDoInstruction(0, 0, "DoFn1", "DoFnUserName"), IntrinsicMapTaskExecutorFactoryTest.createParDoInstruction(1, 0, "DoFnWithContext", "DoFnWithContextUserName"));
        MapTask mapTask = new MapTask();
        mapTask.setStageName(IntrinsicMapTaskExecutorFactoryTest.STAGE);
        mapTask.setInstructions(instructions);
        mapTask.setFactory(Transport.getJsonFactory());
        BatchModeExecutionContext context = BatchModeExecutionContext.forTesting(options, counterSet, "testStage");
        try (DataflowMapTaskExecutor executor = /* beamFnControlClientHandler */
        /* beamFnDataService */
        /* beamFnStateService */
        mapTaskExecutorFactory.create(null, null, null, null, IntrinsicMapTaskExecutorFactoryTest.mapTaskToNetwork.apply(mapTask), options, IntrinsicMapTaskExecutorFactoryTest.STAGE, readerRegistry, sinkRegistry, context, counterSet, IntrinsicMapTaskExecutorFactoryTest.idGenerator)) {
            executor.execute();
        }
        List<String> stepNames = new ArrayList<>();
        for (BatchModeExecutionContext.StepContext stepContext : context.getAllStepContexts()) {
            stepNames.add(stepContext.getNameContext().systemName());
        }
        Assert.assertThat(stepNames, Matchers.hasItems("DoFn1", "DoFnWithContext"));
    }

    @Test
    public void testCreateReadOperation() throws Exception {
        ParallelInstructionNode instructionNode = ParallelInstructionNode.create(IntrinsicMapTaskExecutorFactoryTest.createReadInstruction("Read"), UNKNOWN);
        Mockito.when(network.successors(instructionNode)).thenReturn(ImmutableSet.<Node>of(IntrinsicMapTaskExecutorFactory.createOutputReceiversTransform(IntrinsicMapTaskExecutorFactoryTest.STAGE, counterSet).apply(InstructionOutputNode.create(instructionNode.getParallelInstruction().getOutputs().get(0), IntrinsicMapTaskExecutorFactoryTest.PCOLLECTION_ID))));
        Mockito.when(network.outDegree(instructionNode)).thenReturn(1);
        Node operationNode = mapTaskExecutorFactory.createOperationTransformForParallelInstructionNodes(IntrinsicMapTaskExecutorFactoryTest.STAGE, network, PipelineOptionsFactory.create(), readerRegistry, sinkRegistry, BatchModeExecutionContext.forTesting(options, counterSet, "testStage")).apply(instructionNode);
        Assert.assertThat(operationNode, Matchers.instanceOf(OperationNode.class));
        Assert.assertThat(getOperation(), Matchers.instanceOf(ReadOperation.class));
        ReadOperation readOperation = ((ReadOperation) (getOperation()));
        Assert.assertEquals(1, readOperation.receivers.length);
        Assert.assertEquals(0, readOperation.receivers[0].getReceiverCount());
        Assert.assertEquals(UNSTARTED, readOperation.initializationState);
        Assert.assertThat(readOperation.reader, Matchers.instanceOf(ReaderFactoryTest.TestReader.class));
        counterSet.extractUpdates(false, updateExtractor);
        IntrinsicMapTaskExecutorFactoryTest.verifyOutputCounters(updateExtractor, "read_output_name");
        Mockito.verify(updateExtractor).longSum(ArgumentMatchers.eq(CounterName.named("Read-ByteCount")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong());
        Mockito.verifyNoMoreInteractions(updateExtractor);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testCreateWriteOperation() throws Exception {
        int producerIndex = 1;
        int producerOutputNum = 2;
        ParallelInstructionNode instructionNode = ParallelInstructionNode.create(IntrinsicMapTaskExecutorFactoryTest.createWriteInstruction(producerIndex, producerOutputNum, "WriteOperation"), UNKNOWN);
        Node operationNode = mapTaskExecutorFactory.createOperationTransformForParallelInstructionNodes(IntrinsicMapTaskExecutorFactoryTest.STAGE, network, options, readerRegistry, sinkRegistry, BatchModeExecutionContext.forTesting(options, counterSet, "testStage")).apply(instructionNode);
        Assert.assertThat(operationNode, Matchers.instanceOf(OperationNode.class));
        Assert.assertThat(getOperation(), Matchers.instanceOf(WriteOperation.class));
        WriteOperation writeOperation = ((WriteOperation) (getOperation()));
        Assert.assertEquals(0, writeOperation.receivers.length);
        Assert.assertEquals(UNSTARTED, writeOperation.initializationState);
        Assert.assertThat(writeOperation.sink, Matchers.instanceOf(SizeReportingSinkWrapper.class));
        Assert.assertThat(((SizeReportingSinkWrapper<?>) (writeOperation.sink)).getUnderlyingSink(), Matchers.instanceOf(IntrinsicMapTaskExecutorFactoryTest.TestSink.class));
        counterSet.extractUpdates(false, updateExtractor);
        Mockito.verify(updateExtractor).longSum(ArgumentMatchers.eq(CounterName.named("WriteOperation-ByteCount")), ArgumentMatchers.anyBoolean(), ArgumentMatchers.anyLong());
        Mockito.verifyNoMoreInteractions(updateExtractor);
    }

    static class TestDoFn extends DoFn<String, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            c.output(c.element());
        }
    }

    static class TestSink extends Sink<Integer> {
        @Override
        public SinkWriter<Integer> writer() {
            return new IntrinsicMapTaskExecutorFactoryTest.TestSink.TestSinkWriter();
        }

        /**
         * A sink writer that drops its input values, for testing.
         */
        static class TestSinkWriter implements SinkWriter<Integer> {
            @Override
            public long add(Integer outputElem) {
                return 4;
            }

            @Override
            public void close() {
            }

            @Override
            public void abort() throws IOException {
                close();
            }
        }
    }

    static class TestSinkFactory implements SinkFactory {
        @Override
        public IntrinsicMapTaskExecutorFactoryTest.TestSink create(CloudObject o, @Nullable
        Coder<?> coder, @Nullable
        PipelineOptions options, @Nullable
        DataflowExecutionContext executionContext, DataflowOperationContext operationContext) {
            return new IntrinsicMapTaskExecutorFactoryTest.TestSink();
        }
    }

    @Test
    public void testCreateParDoOperation() throws Exception {
        int producerIndex = 1;
        int producerOutputNum = 2;
        BatchModeExecutionContext context = BatchModeExecutionContext.forTesting(options, counterSet, "testStage");
        ParallelInstructionNode instructionNode = ParallelInstructionNode.create(IntrinsicMapTaskExecutorFactoryTest.createParDoInstruction(producerIndex, producerOutputNum, "DoFn"), UNKNOWN);
        Node outputReceiverNode = IntrinsicMapTaskExecutorFactory.createOutputReceiversTransform(IntrinsicMapTaskExecutorFactoryTest.STAGE, counterSet).apply(InstructionOutputNode.create(instructionNode.getParallelInstruction().getOutputs().get(0), IntrinsicMapTaskExecutorFactoryTest.PCOLLECTION_ID));
        Mockito.when(network.successors(instructionNode)).thenReturn(ImmutableSet.of(outputReceiverNode));
        Mockito.when(network.outDegree(instructionNode)).thenReturn(1);
        Mockito.when(network.edgesConnecting(instructionNode, outputReceiverNode)).thenReturn(ImmutableSet.<Edge>of(MultiOutputInfoEdge.create(instructionNode.getParallelInstruction().getParDo().getMultiOutputInfos().get(0))));
        Node operationNode = mapTaskExecutorFactory.createOperationTransformForParallelInstructionNodes(IntrinsicMapTaskExecutorFactoryTest.STAGE, network, options, readerRegistry, sinkRegistry, context).apply(instructionNode);
        Assert.assertThat(operationNode, Matchers.instanceOf(OperationNode.class));
        Assert.assertThat(getOperation(), Matchers.instanceOf(ParDoOperation.class));
        ParDoOperation parDoOperation = ((ParDoOperation) (getOperation()));
        Assert.assertEquals(1, parDoOperation.receivers.length);
        Assert.assertEquals(0, parDoOperation.receivers[0].getReceiverCount());
        Assert.assertEquals(UNSTARTED, parDoOperation.initializationState);
    }

    @Test
    public void testCreatePartialGroupByKeyOperation() throws Exception {
        int producerIndex = 1;
        int producerOutputNum = 2;
        ParallelInstructionNode instructionNode = ParallelInstructionNode.create(IntrinsicMapTaskExecutorFactoryTest.createPartialGroupByKeyInstruction(producerIndex, producerOutputNum), UNKNOWN);
        Mockito.when(network.successors(instructionNode)).thenReturn(ImmutableSet.<Node>of(IntrinsicMapTaskExecutorFactory.createOutputReceiversTransform(IntrinsicMapTaskExecutorFactoryTest.STAGE, counterSet).apply(InstructionOutputNode.create(instructionNode.getParallelInstruction().getOutputs().get(0), IntrinsicMapTaskExecutorFactoryTest.PCOLLECTION_ID))));
        Mockito.when(network.outDegree(instructionNode)).thenReturn(1);
        Node operationNode = mapTaskExecutorFactory.createOperationTransformForParallelInstructionNodes(IntrinsicMapTaskExecutorFactoryTest.STAGE, network, PipelineOptionsFactory.create(), readerRegistry, sinkRegistry, BatchModeExecutionContext.forTesting(options, counterSet, "testStage")).apply(instructionNode);
        Assert.assertThat(operationNode, Matchers.instanceOf(OperationNode.class));
        Assert.assertThat(getOperation(), Matchers.instanceOf(ParDoOperation.class));
        ParDoOperation pgbkOperation = ((ParDoOperation) (getOperation()));
        Assert.assertEquals(1, pgbkOperation.receivers.length);
        Assert.assertEquals(0, pgbkOperation.receivers[0].getReceiverCount());
        Assert.assertEquals(UNSTARTED, pgbkOperation.initializationState);
    }

    @Test
    public void testCreatePartialGroupByKeyOperationWithCombine() throws Exception {
        int producerIndex = 1;
        int producerOutputNum = 2;
        ParallelInstruction instruction = IntrinsicMapTaskExecutorFactoryTest.createPartialGroupByKeyInstruction(producerIndex, producerOutputNum);
        AppliedCombineFn<?, ?, ?, ?> combineFn = AppliedCombineFn.withInputCoder(Sum.ofIntegers(), CoderRegistry.createDefault(), KvCoder.of(StringUtf8Coder.of(), BigEndianIntegerCoder.of()));
        CloudObject cloudCombineFn = CloudObject.forClassName("CombineFn");
        addString(cloudCombineFn, PropertyNames.SERIALIZED_FN, byteArrayToJsonString(serializeToByteArray(combineFn)));
        instruction.getPartialGroupByKey().setValueCombiningFn(cloudCombineFn);
        ParallelInstructionNode instructionNode = ParallelInstructionNode.create(instruction, UNKNOWN);
        Mockito.when(network.successors(instructionNode)).thenReturn(ImmutableSet.<Node>of(IntrinsicMapTaskExecutorFactory.createOutputReceiversTransform(IntrinsicMapTaskExecutorFactoryTest.STAGE, counterSet).apply(InstructionOutputNode.create(instructionNode.getParallelInstruction().getOutputs().get(0), IntrinsicMapTaskExecutorFactoryTest.PCOLLECTION_ID))));
        Mockito.when(network.outDegree(instructionNode)).thenReturn(1);
        Node operationNode = mapTaskExecutorFactory.createOperationTransformForParallelInstructionNodes(IntrinsicMapTaskExecutorFactoryTest.STAGE, network, options, readerRegistry, sinkRegistry, BatchModeExecutionContext.forTesting(options, counterSet, "testStage")).apply(instructionNode);
        Assert.assertThat(operationNode, Matchers.instanceOf(OperationNode.class));
        Assert.assertThat(getOperation(), Matchers.instanceOf(ParDoOperation.class));
        ParDoOperation pgbkOperation = ((ParDoOperation) (getOperation()));
        Assert.assertEquals(1, pgbkOperation.receivers.length);
        Assert.assertEquals(0, pgbkOperation.receivers[0].getReceiverCount());
        Assert.assertEquals(UNSTARTED, pgbkOperation.initializationState);
    }

    @Test
    public void testCreateFlattenOperation() throws Exception {
        int producerIndex1 = 1;
        int producerOutputNum1 = 2;
        int producerIndex2 = 0;
        int producerOutputNum2 = 1;
        ParallelInstructionNode instructionNode = ParallelInstructionNode.create(IntrinsicMapTaskExecutorFactoryTest.createFlattenInstruction(producerIndex1, producerOutputNum1, producerIndex2, producerOutputNum2, "Flatten"), UNKNOWN);
        Mockito.when(network.successors(instructionNode)).thenReturn(ImmutableSet.<Node>of(IntrinsicMapTaskExecutorFactory.createOutputReceiversTransform(IntrinsicMapTaskExecutorFactoryTest.STAGE, counterSet).apply(InstructionOutputNode.create(instructionNode.getParallelInstruction().getOutputs().get(0), IntrinsicMapTaskExecutorFactoryTest.PCOLLECTION_ID))));
        Mockito.when(network.outDegree(instructionNode)).thenReturn(1);
        Node operationNode = mapTaskExecutorFactory.createOperationTransformForParallelInstructionNodes(IntrinsicMapTaskExecutorFactoryTest.STAGE, network, options, readerRegistry, sinkRegistry, BatchModeExecutionContext.forTesting(options, counterSet, "testStage")).apply(instructionNode);
        Assert.assertThat(operationNode, Matchers.instanceOf(OperationNode.class));
        Assert.assertThat(getOperation(), Matchers.instanceOf(FlattenOperation.class));
        FlattenOperation flattenOperation = ((FlattenOperation) (getOperation()));
        Assert.assertEquals(1, flattenOperation.receivers.length);
        Assert.assertEquals(0, flattenOperation.receivers[0].getReceiverCount());
        Assert.assertEquals(UNSTARTED, flattenOperation.initializationState);
    }
}

