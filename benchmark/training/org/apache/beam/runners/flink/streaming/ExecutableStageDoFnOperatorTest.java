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
package org.apache.beam.runners.flink.streaming;


import ProcessBundleDescriptors.ExecutableProcessBundleDescriptor;
import java.util.Collections;
import java.util.Map;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.model.pipeline.v1.RunnerApi.ExecutableStagePayload;
import org.apache.beam.model.pipeline.v1.RunnerApi.PCollection;
import org.apache.beam.runners.flink.FlinkPipelineOptions;
import org.apache.beam.runners.flink.translation.functions.FlinkExecutableStageContext;
import org.apache.beam.runners.flink.translation.wrappers.streaming.DoFnOperator;
import org.apache.beam.runners.flink.translation.wrappers.streaming.ExecutableStageDoFnOperator;
import org.apache.beam.runners.fnexecution.control.BundleProgressHandler;
import org.apache.beam.runners.fnexecution.control.OutputReceiverFactory;
import org.apache.beam.runners.fnexecution.control.RemoteBundle;
import org.apache.beam.runners.fnexecution.control.StageBundleFactory;
import org.apache.beam.runners.fnexecution.provisioning.JobInfo;
import org.apache.beam.runners.fnexecution.state.StateRequestHandler;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.fn.data.FnDataReceiver;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.Struct;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.flink.api.common.cache.DistributedCache;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.util.OneInputStreamOperatorTestHarness;
import org.apache.flink.util.OutputTag;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link ExecutableStageDoFnOperator}.
 */
@RunWith(JUnit4.class)
public class ExecutableStageDoFnOperatorTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private RuntimeContext runtimeContext;

    @Mock
    private DistributedCache distributedCache;

    @Mock
    private FlinkExecutableStageContext stageContext;

    @Mock
    private StageBundleFactory stageBundleFactory;

    @Mock
    private StateRequestHandler stateRequestHandler;

    @Mock
    private ExecutableProcessBundleDescriptor processBundleDescriptor;

    // NOTE: ExecutableStage.fromPayload expects exactly one input, so we provide one here. These unit
    // tests in general ignore the executable stage itself and mock around it.
    private final ExecutableStagePayload stagePayload = ExecutableStagePayload.newBuilder().setInput("input").setComponents(Components.newBuilder().putPcollections("input", PCollection.getDefaultInstance()).build()).build();

    private final JobInfo jobInfo = JobInfo.create("job-id", "job-name", "retrieval-token", Struct.getDefaultInstance());

    @Test
    public void sdkErrorsSurfaceOnClose() throws Exception {
        TupleTag<Integer> mainOutput = new TupleTag("main-output");
        DoFnOperator.MultiOutputOutputManagerFactory<Integer> outputManagerFactory = new DoFnOperator.MultiOutputOutputManagerFactory(mainOutput, VoidCoder.of());
        ExecutableStageDoFnOperator<Integer, Integer> operator = getOperator(mainOutput, Collections.emptyList(), outputManagerFactory);
        OneInputStreamOperatorTestHarness<WindowedValue<Integer>, WindowedValue<Integer>> testHarness = new OneInputStreamOperatorTestHarness(operator);
        testHarness.open();
        @SuppressWarnings("unchecked")
        RemoteBundle bundle = Mockito.mock(RemoteBundle.class);
        Mockito.when(stageBundleFactory.getBundle(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(bundle);
        @SuppressWarnings("unchecked")
        FnDataReceiver<WindowedValue<?>> receiver = Mockito.mock(FnDataReceiver.class);
        Mockito.when(bundle.getInputReceivers()).thenReturn(ImmutableMap.of("input", receiver));
        Exception expected = new RuntimeException(new Exception());
        Mockito.doThrow(expected).when(bundle).close();
        thrown.expectCause(Matchers.is(expected));
        operator.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(WindowedValue.valueInGlobalWindow(0)));
        testHarness.close();
    }

    @Test
    public void expectedInputsAreSent() throws Exception {
        TupleTag<Integer> mainOutput = new TupleTag("main-output");
        DoFnOperator.MultiOutputOutputManagerFactory<Integer> outputManagerFactory = new DoFnOperator.MultiOutputOutputManagerFactory(mainOutput, VoidCoder.of());
        ExecutableStageDoFnOperator<Integer, Integer> operator = getOperator(mainOutput, Collections.emptyList(), outputManagerFactory);
        @SuppressWarnings("unchecked")
        RemoteBundle bundle = Mockito.mock(RemoteBundle.class);
        Mockito.when(stageBundleFactory.getBundle(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(bundle);
        @SuppressWarnings("unchecked")
        FnDataReceiver<WindowedValue<?>> receiver = Mockito.mock(FnDataReceiver.class);
        Mockito.when(bundle.getInputReceivers()).thenReturn(ImmutableMap.of("input", receiver));
        WindowedValue<Integer> one = WindowedValue.valueInGlobalWindow(1);
        WindowedValue<Integer> two = WindowedValue.valueInGlobalWindow(2);
        WindowedValue<Integer> three = WindowedValue.valueInGlobalWindow(3);
        OneInputStreamOperatorTestHarness<WindowedValue<Integer>, WindowedValue<Integer>> testHarness = new OneInputStreamOperatorTestHarness(operator);
        testHarness.open();
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(one));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(two));
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(three));
        Mockito.verify(receiver).accept(one);
        Mockito.verify(receiver).accept(two);
        Mockito.verify(receiver).accept(three);
        Mockito.verifyNoMoreInteractions(receiver);
        testHarness.close();
    }

    @Test
    public void outputsAreTaggedCorrectly() throws Exception {
        WindowedValue.ValueOnlyWindowedValueCoder<Integer> coder = WindowedValue.getValueOnlyCoder(VarIntCoder.of());
        TupleTag<Integer> mainOutput = new TupleTag("main-output");
        TupleTag<Integer> additionalOutput1 = new TupleTag("output-1");
        TupleTag<Integer> additionalOutput2 = new TupleTag("output-2");
        ImmutableMap<TupleTag<?>, OutputTag<?>> tagsToOutputTags = ImmutableMap.<TupleTag<?>, OutputTag<?>>builder().put(additionalOutput1, new OutputTag<String>(additionalOutput1.getId()) {}).put(additionalOutput2, new OutputTag<String>(additionalOutput2.getId()) {}).build();
        ImmutableMap<TupleTag<?>, Coder<WindowedValue<?>>> tagsToCoders = ImmutableMap.<TupleTag<?>, Coder<WindowedValue<?>>>builder().put(mainOutput, ((Coder) (coder))).put(additionalOutput1, coder).put(additionalOutput2, coder).build();
        ImmutableMap<TupleTag<?>, Integer> tagsToIds = ImmutableMap.<TupleTag<?>, Integer>builder().put(mainOutput, 0).put(additionalOutput1, 1).put(additionalOutput2, 2).build();
        DoFnOperator.MultiOutputOutputManagerFactory<Integer> outputManagerFactory = new DoFnOperator.MultiOutputOutputManagerFactory(mainOutput, tagsToOutputTags, tagsToCoders, tagsToIds);
        WindowedValue<Integer> zero = WindowedValue.valueInGlobalWindow(0);
        WindowedValue<Integer> three = WindowedValue.valueInGlobalWindow(3);
        WindowedValue<Integer> four = WindowedValue.valueInGlobalWindow(4);
        WindowedValue<Integer> five = WindowedValue.valueInGlobalWindow(5);
        // We use a real StageBundleFactory here in order to exercise the output receiver factory.
        StageBundleFactory stageBundleFactory = new StageBundleFactory() {
            private boolean onceEmitted;

            @Override
            public RemoteBundle getBundle(OutputReceiverFactory receiverFactory, StateRequestHandler stateRequestHandler, BundleProgressHandler progressHandler) {
                return new RemoteBundle() {
                    @Override
                    public String getId() {
                        return "bundle-id";
                    }

                    @Override
                    public Map<String, FnDataReceiver<WindowedValue<?>>> getInputReceivers() {
                        return ImmutableMap.of("input", ( input) -> {
                            /* Ignore input */
                        });
                    }

                    @Override
                    public void close() throws Exception {
                        if (onceEmitted) {
                            return;
                        }
                        // Emit all values to the runner when the bundle is closed.
                        receiverFactory.create(mainOutput.getId()).accept(three);
                        receiverFactory.create(additionalOutput1.getId()).accept(four);
                        receiverFactory.create(additionalOutput2.getId()).accept(five);
                        onceEmitted = true;
                    }
                };
            }

            @Override
            public ExecutableProcessBundleDescriptor getProcessBundleDescriptor() {
                return processBundleDescriptor;
            }

            @Override
            public void close() {
            }
        };
        // Wire the stage bundle factory into our context.
        Mockito.when(stageContext.getStageBundleFactory(ArgumentMatchers.any())).thenReturn(stageBundleFactory);
        ExecutableStageDoFnOperator<Integer, Integer> operator = getOperator(mainOutput, ImmutableList.of(additionalOutput1, additionalOutput2), outputManagerFactory);
        OneInputStreamOperatorTestHarness<WindowedValue<Integer>, WindowedValue<Integer>> testHarness = new OneInputStreamOperatorTestHarness(operator);
        long watermark = (testHarness.getCurrentWatermark()) + 1;
        testHarness.open();
        testHarness.processElement(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(zero));
        testHarness.processWatermark(watermark);
        watermark++;
        testHarness.processWatermark(watermark);
        Assert.assertEquals(watermark, testHarness.getCurrentWatermark());
        // watermark hold until bundle complete
        Assert.assertEquals(0, testHarness.getOutput().size());
        testHarness.close();// triggers finish bundle

        Assert.assertThat(testHarness.getOutput(), contains(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(three), new Watermark(watermark), new Watermark(Long.MAX_VALUE)));
        Assert.assertThat(testHarness.getSideOutput(tagsToOutputTags.get(additionalOutput1)), contains(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(four)));
        Assert.assertThat(testHarness.getSideOutput(tagsToOutputTags.get(additionalOutput2)), contains(new org.apache.flink.streaming.runtime.streamrecord.StreamRecord(five)));
    }

    @Test
    public void testStageBundleClosed() throws Exception {
        TupleTag<Integer> mainOutput = new TupleTag("main-output");
        DoFnOperator.MultiOutputOutputManagerFactory<Integer> outputManagerFactory = new DoFnOperator.MultiOutputOutputManagerFactory(mainOutput, VoidCoder.of());
        ExecutableStageDoFnOperator<Integer, Integer> operator = getOperator(mainOutput, Collections.emptyList(), outputManagerFactory);
        OneInputStreamOperatorTestHarness<WindowedValue<Integer>, WindowedValue<Integer>> testHarness = new OneInputStreamOperatorTestHarness(operator);
        RemoteBundle bundle = Mockito.mock(RemoteBundle.class);
        Mockito.when(bundle.getInputReceivers()).thenReturn(ImmutableMap.<String, FnDataReceiver<WindowedValue>>builder().put("input", Mockito.mock(FnDataReceiver.class)).build());
        Mockito.when(stageBundleFactory.getBundle(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(bundle);
        testHarness.open();
        testHarness.close();
        Mockito.verify(stageBundleFactory).getProcessBundleDescriptor();
        Mockito.verify(stageBundleFactory).close();
        Mockito.verify(stageContext).close();
        // DoFnOperator generates a final watermark, which triggers a new bundle..
        Mockito.verify(stageBundleFactory).getBundle(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
        Mockito.verify(bundle).getInputReceivers();
        Mockito.verify(bundle).close();
        Mockito.verifyNoMoreInteractions(stageBundleFactory);
        // close() will also call dispose(), but call again to verify no new bundle
        // is created afterwards
        operator.dispose();
        Mockito.verifyNoMoreInteractions(bundle);
    }

    @Test
    public void testSerialization() {
        WindowedValue.ValueOnlyWindowedValueCoder<Integer> coder = WindowedValue.getValueOnlyCoder(VarIntCoder.of());
        TupleTag<Integer> mainOutput = new TupleTag("main-output");
        TupleTag<Integer> additionalOutput = new TupleTag("additional-output");
        ImmutableMap<TupleTag<?>, OutputTag<?>> tagsToOutputTags = ImmutableMap.<TupleTag<?>, OutputTag<?>>builder().put(additionalOutput, new OutputTag(additionalOutput.getId(), TypeInformation.of(Integer.class))).build();
        ImmutableMap<TupleTag<?>, Coder<WindowedValue<?>>> tagsToCoders = ImmutableMap.<TupleTag<?>, Coder<WindowedValue<?>>>builder().put(mainOutput, ((Coder) (coder))).put(additionalOutput, coder).build();
        ImmutableMap<TupleTag<?>, Integer> tagsToIds = ImmutableMap.<TupleTag<?>, Integer>builder().put(mainOutput, 0).put(additionalOutput, 1).build();
        DoFnOperator.MultiOutputOutputManagerFactory<Integer> outputManagerFactory = new DoFnOperator.MultiOutputOutputManagerFactory(mainOutput, tagsToOutputTags, tagsToCoders, tagsToIds);
        FlinkPipelineOptions options = PipelineOptionsFactory.as(FlinkPipelineOptions.class);
        ExecutableStageDoFnOperator<Integer, Integer> operator = /* sideInputTagMapping */
        /* sideInputs */
        /* sideInputId mapping */
        new ExecutableStageDoFnOperator("transform", null, null, Collections.emptyMap(), mainOutput, ImmutableList.of(additionalOutput), outputManagerFactory, Collections.emptyMap(), Collections.emptyList(), Collections.emptyMap(), options, stagePayload, jobInfo, FlinkExecutableStageContext.factory(options), ExecutableStageDoFnOperatorTest.createOutputMap(mainOutput, ImmutableList.of(additionalOutput)), WindowingStrategy.globalDefault(), null, null);
        ExecutableStageDoFnOperator<Integer, Integer> clone = SerializationUtils.clone(operator);
        Assert.assertNotNull(clone);
        Assert.assertNotEquals(operator, clone);
    }
}

