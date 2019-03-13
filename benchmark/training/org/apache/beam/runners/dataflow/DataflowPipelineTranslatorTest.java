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
package org.apache.beam.runners.dataflow;


import DataflowPipelineWorkerPoolOptions.AutoscalingAlgorithmType;
import DisplayData.Builder;
import PTransformTranslation.SPLITTABLE_PROCESS_KEYED_URN;
import ParDo.SingleOutput;
import PropertyNames.OUTPUT_INFO;
import PropertyNames.RESTRICTION_CODER;
import PropertyNames.SERIALIZED_FN;
import PropertyNames.USER_NAME;
import com.google.api.services.dataflow.model.Job;
import com.google.api.services.dataflow.model.Step;
import com.google.api.services.dataflow.model.WorkerPool;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.model.pipeline.v1.RunnerApi.ParDoPayload;
import org.apache.beam.runners.core.construction.ParDoTranslation;
import org.apache.beam.runners.core.construction.RehydratedComponents;
import org.apache.beam.runners.dataflow.DataflowPipelineTranslator.JobSpecification;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.runners.dataflow.options.DataflowPipelineWorkerPoolOptions;
import org.apache.beam.runners.dataflow.util.CloudObject;
import org.apache.beam.runners.dataflow.util.CloudObjects;
import org.apache.beam.runners.dataflow.util.Structs;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.coders.VoidCoder;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.range.OffsetRange;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.display.DisplayData;
import org.apache.beam.sdk.transforms.splittabledofn.OffsetRangeTracker;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.transforms.windowing.WindowFn;
import org.apache.beam.sdk.util.DoFnInfo;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.PDone;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatcher;

import static org.apache.beam.sdk.transforms.DoFn.<init>;


/**
 * Tests for DataflowPipelineTranslator.
 */
@RunWith(JUnit4.class)
public class DataflowPipelineTranslatorTest implements Serializable {
    @Rule
    public transient ExpectedException thrown = ExpectedException.none();

    // A Custom Mockito matcher for an initial Job that checks that all
    // expected fields are set.
    private static class IsValidCreateRequest extends ArgumentMatcher<Job> {
        @Override
        public boolean matches(Object o) {
            Job job = ((Job) (o));
            return ((((((((((job.getId()) == null) && ((job.getProjectId()) == null)) && ((job.getName()) != null)) && ((job.getType()) != null)) && ((job.getEnvironment()) != null)) && ((job.getSteps()) != null)) && ((job.getCurrentState()) == null)) && ((job.getCurrentStateTime()) == null)) && ((job.getExecutionInfo()) == null)) && ((job.getCreateTime()) == null);
        }
    }

    @Test
    public void testNetworkConfig() throws IOException {
        final String testNetwork = "test-network";
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        options.setNetwork(testNetwork);
        Pipeline p = buildPipeline(options);
        p.traverseTopologically(new RecordingPipelineVisitor());
        Job job = DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList()).getJob();
        Assert.assertEquals(1, job.getEnvironment().getWorkerPools().size());
        Assert.assertEquals(testNetwork, job.getEnvironment().getWorkerPools().get(0).getNetwork());
    }

    @Test
    public void testNetworkConfigMissing() throws IOException {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        Pipeline p = buildPipeline(options);
        p.traverseTopologically(new RecordingPipelineVisitor());
        Job job = DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList()).getJob();
        Assert.assertEquals(1, job.getEnvironment().getWorkerPools().size());
        Assert.assertNull(job.getEnvironment().getWorkerPools().get(0).getNetwork());
    }

    @Test
    public void testSubnetworkConfig() throws IOException {
        final String testSubnetwork = "regions/REGION/subnetworks/SUBNETWORK";
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        options.setSubnetwork(testSubnetwork);
        Pipeline p = buildPipeline(options);
        p.traverseTopologically(new RecordingPipelineVisitor());
        Job job = DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList()).getJob();
        Assert.assertEquals(1, job.getEnvironment().getWorkerPools().size());
        Assert.assertEquals(testSubnetwork, job.getEnvironment().getWorkerPools().get(0).getSubnetwork());
    }

    @Test
    public void testSubnetworkConfigMissing() throws IOException {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        Pipeline p = buildPipeline(options);
        p.traverseTopologically(new RecordingPipelineVisitor());
        Job job = DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList()).getJob();
        Assert.assertEquals(1, job.getEnvironment().getWorkerPools().size());
        Assert.assertNull(job.getEnvironment().getWorkerPools().get(0).getSubnetwork());
    }

    @Test
    public void testScalingAlgorithmMissing() throws IOException {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        Pipeline p = buildPipeline(options);
        p.traverseTopologically(new RecordingPipelineVisitor());
        Job job = DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList()).getJob();
        Assert.assertEquals(1, job.getEnvironment().getWorkerPools().size());
        // Autoscaling settings are always set.
        Assert.assertNull(job.getEnvironment().getWorkerPools().get(0).getAutoscalingSettings().getAlgorithm());
        Assert.assertEquals(0, job.getEnvironment().getWorkerPools().get(0).getAutoscalingSettings().getMaxNumWorkers().intValue());
    }

    @Test
    public void testScalingAlgorithmNone() throws IOException {
        final DataflowPipelineWorkerPoolOptions.AutoscalingAlgorithmType noScaling = AutoscalingAlgorithmType.NONE;
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        options.setAutoscalingAlgorithm(noScaling);
        Pipeline p = buildPipeline(options);
        p.traverseTopologically(new RecordingPipelineVisitor());
        Job job = DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList()).getJob();
        Assert.assertEquals(1, job.getEnvironment().getWorkerPools().size());
        Assert.assertEquals("AUTOSCALING_ALGORITHM_NONE", job.getEnvironment().getWorkerPools().get(0).getAutoscalingSettings().getAlgorithm());
        Assert.assertEquals(0, job.getEnvironment().getWorkerPools().get(0).getAutoscalingSettings().getMaxNumWorkers().intValue());
    }

    @Test
    public void testMaxNumWorkersIsPassedWhenNoAlgorithmIsSet() throws IOException {
        final DataflowPipelineWorkerPoolOptions.AutoscalingAlgorithmType noScaling = null;
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        options.setMaxNumWorkers(42);
        options.setAutoscalingAlgorithm(noScaling);
        Pipeline p = buildPipeline(options);
        p.traverseTopologically(new RecordingPipelineVisitor());
        Job job = DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList()).getJob();
        Assert.assertEquals(1, job.getEnvironment().getWorkerPools().size());
        Assert.assertNull(job.getEnvironment().getWorkerPools().get(0).getAutoscalingSettings().getAlgorithm());
        Assert.assertEquals(42, job.getEnvironment().getWorkerPools().get(0).getAutoscalingSettings().getMaxNumWorkers().intValue());
    }

    @Test
    public void testZoneConfig() throws IOException {
        final String testZone = "test-zone-1";
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        options.setZone(testZone);
        Pipeline p = buildPipeline(options);
        p.traverseTopologically(new RecordingPipelineVisitor());
        Job job = DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList()).getJob();
        Assert.assertEquals(1, job.getEnvironment().getWorkerPools().size());
        Assert.assertEquals(testZone, job.getEnvironment().getWorkerPools().get(0).getZone());
    }

    @Test
    public void testWorkerMachineTypeConfig() throws IOException {
        final String testMachineType = "test-machine-type";
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        options.setWorkerMachineType(testMachineType);
        Pipeline p = buildPipeline(options);
        p.traverseTopologically(new RecordingPipelineVisitor());
        Job job = DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList()).getJob();
        Assert.assertEquals(1, job.getEnvironment().getWorkerPools().size());
        WorkerPool workerPool = job.getEnvironment().getWorkerPools().get(0);
        Assert.assertEquals(testMachineType, workerPool.getMachineType());
    }

    @Test
    public void testDiskSizeGbConfig() throws IOException {
        final Integer diskSizeGb = 1234;
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        options.setDiskSizeGb(diskSizeGb);
        Pipeline p = buildPipeline(options);
        p.traverseTopologically(new RecordingPipelineVisitor());
        Job job = DataflowPipelineTranslator.fromOptions(options).translate(p, DataflowRunner.fromOptions(options), Collections.emptyList()).getJob();
        Assert.assertEquals(1, job.getEnvironment().getWorkerPools().size());
        Assert.assertEquals(diskSizeGb, job.getEnvironment().getWorkerPools().get(0).getDiskSizeGb());
    }

    /**
     * A composite transform that returns an output that is unrelated to the input.
     */
    private static class UnrelatedOutputCreator extends PTransform<PCollection<Integer>, PCollection<Integer>> {
        @Override
        public PCollection<Integer> expand(PCollection<Integer> input) {
            // Apply an operation so that this is a composite transform.
            input.apply(Count.perElement());
            // Return a value unrelated to the input.
            return input.getPipeline().apply(Create.of(1, 2, 3, 4));
        }
    }

    /**
     * A composite transform that returns an output that is unbound.
     */
    private static class UnboundOutputCreator extends PTransform<PCollection<Integer>, PDone> {
        @Override
        public PDone expand(PCollection<Integer> input) {
            // Apply an operation so that this is a composite transform.
            input.apply(Count.perElement());
            return PDone.in(input.getPipeline());
        }
    }

    /**
     * A composite transform that returns a partially bound output.
     *
     * <p>This is not allowed and will result in a failure.
     */
    private static class PartiallyBoundOutputCreator extends PTransform<PCollection<Integer>, PCollectionTuple> {
        public final TupleTag<Integer> sumTag = new TupleTag("sum");

        public final TupleTag<Void> doneTag = new TupleTag("done");

        @Override
        public PCollectionTuple expand(PCollection<Integer> input) {
            PCollection<Integer> sum = input.apply(Sum.integersGlobally());
            // Fails here when attempting to construct a tuple with an unbound object.
            return PCollectionTuple.of(sumTag, sum).and(doneTag, PCollection.createPrimitiveOutputInternal(input.getPipeline(), WindowingStrategy.globalDefault(), input.isBounded(), VoidCoder.of()));
        }
    }

    @Test
    public void testMultiGraphPipelineSerialization() throws Exception {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        Pipeline p = Pipeline.create(options);
        PCollection<Integer> input = p.begin().apply(Create.of(1, 2, 3));
        input.apply(new DataflowPipelineTranslatorTest.UnrelatedOutputCreator());
        input.apply(new DataflowPipelineTranslatorTest.UnboundOutputCreator());
        DataflowPipelineTranslator t = DataflowPipelineTranslator.fromOptions(PipelineOptionsFactory.as(DataflowPipelineOptions.class));
        // Check that translation doesn't fail.
        JobSpecification jobSpecification = t.translate(p, DataflowRunner.fromOptions(options), Collections.emptyList());
        DataflowPipelineTranslatorTest.assertAllStepOutputsHaveUniqueIds(jobSpecification.getJob());
    }

    @Test
    public void testPartiallyBoundFailure() throws IOException {
        Pipeline p = Pipeline.create(DataflowPipelineTranslatorTest.buildPipelineOptions());
        PCollection<Integer> input = p.begin().apply(Create.of(1, 2, 3));
        thrown.expect(IllegalArgumentException.class);
        input.apply(new DataflowPipelineTranslatorTest.PartiallyBoundOutputCreator());
        Assert.fail("Failure expected from use of partially bound output");
    }

    /**
     * This tests a few corner cases that should not crash.
     */
    @Test
    public void testGoodWildcards() throws Exception {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        Pipeline pipeline = Pipeline.create(options);
        DataflowPipelineTranslator t = DataflowPipelineTranslator.fromOptions(options);
        applyRead(pipeline, "gs://bucket/foo");
        applyRead(pipeline, "gs://bucket/foo/");
        applyRead(pipeline, "gs://bucket/foo/*");
        applyRead(pipeline, "gs://bucket/foo/?");
        applyRead(pipeline, "gs://bucket/foo/[0-9]");
        applyRead(pipeline, "gs://bucket/foo/*baz*");
        applyRead(pipeline, "gs://bucket/foo/*baz?");
        applyRead(pipeline, "gs://bucket/foo/[0-9]baz?");
        applyRead(pipeline, "gs://bucket/foo/baz/*");
        applyRead(pipeline, "gs://bucket/foo/baz/*wonka*");
        applyRead(pipeline, "gs://bucket/foo/*baz/wonka*");
        applyRead(pipeline, "gs://bucket/foo*/baz");
        applyRead(pipeline, "gs://bucket/foo?/baz");
        applyRead(pipeline, "gs://bucket/foo[0-9]/baz");
        // Check that translation doesn't fail.
        JobSpecification jobSpecification = t.translate(pipeline, DataflowRunner.fromOptions(options), Collections.emptyList());
        DataflowPipelineTranslatorTest.assertAllStepOutputsHaveUniqueIds(jobSpecification.getJob());
    }

    private static class TestValueProvider implements Serializable , ValueProvider<String> {
        @Override
        public boolean isAccessible() {
            return false;
        }

        @Override
        public String get() {
            throw new RuntimeException("Should not be called.");
        }
    }

    @Test
    public void testInaccessibleProvider() throws Exception {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        Pipeline pipeline = Pipeline.create(options);
        DataflowPipelineTranslator t = DataflowPipelineTranslator.fromOptions(options);
        pipeline.apply(TextIO.read().from(new DataflowPipelineTranslatorTest.TestValueProvider()));
        // Check that translation does not fail.
        t.translate(pipeline, DataflowRunner.fromOptions(options), Collections.emptyList());
    }

    /**
     * Test that in translation the name for a collection (in this case just a Create output) is
     * overridden to be what the Dataflow service expects.
     */
    @Test
    public void testNamesOverridden() throws Exception {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        options.setStreaming(false);
        DataflowPipelineTranslator translator = DataflowPipelineTranslator.fromOptions(options);
        Pipeline pipeline = Pipeline.create(options);
        pipeline.apply("Jazzy", Create.of(3)).setName("foobizzle");
        runner.replaceTransforms(pipeline);
        Job job = translator.translate(pipeline, runner, Collections.emptyList()).getJob();
        // The Create step
        Step step = job.getSteps().get(0);
        // This is the name that is "set by the user" that the Dataflow translator must override
        String userSpecifiedName = Structs.getString(Structs.getListOfMaps(step.getProperties(), OUTPUT_INFO, null).get(0), USER_NAME);
        // This is the calculated name that must actually be used
        String calculatedName = (Structs.getString(step.getProperties(), USER_NAME)) + ".out0";
        Assert.assertThat(userSpecifiedName, Matchers.equalTo(calculatedName));
    }

    /**
     * Test that in translation the name for collections of a multi-output ParDo - a special case
     * because the user can name tags - are overridden to be what the Dataflow service expects.
     */
    @Test
    public void testTaggedNamesOverridden() throws Exception {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        options.setStreaming(false);
        DataflowPipelineTranslator translator = DataflowPipelineTranslator.fromOptions(options);
        Pipeline pipeline = Pipeline.create(options);
        TupleTag<Integer> tag1 = new TupleTag<Integer>("frazzle") {};
        TupleTag<Integer> tag2 = new TupleTag<Integer>("bazzle") {};
        TupleTag<Integer> tag3 = new TupleTag<Integer>() {};
        PCollectionTuple outputs = pipeline.apply(Create.of(3)).apply(ParDo.of(new org.apache.beam.sdk.transforms.DoFn<Integer, Integer>() {
            @ProcessElement
            public void drop() {
            }
        }).withOutputTags(tag1, TupleTagList.of(tag2).and(tag3)));
        outputs.get(tag1).setName("bizbazzle");
        outputs.get(tag2).setName("gonzaggle");
        outputs.get(tag3).setName("froonazzle");
        runner.replaceTransforms(pipeline);
        Job job = translator.translate(pipeline, runner, Collections.emptyList()).getJob();
        // The ParDo step
        Step step = job.getSteps().get(1);
        String stepName = Structs.getString(step.getProperties(), USER_NAME);
        List<Map<String, Object>> outputInfos = Structs.getListOfMaps(step.getProperties(), OUTPUT_INFO, null);
        Assert.assertThat(outputInfos.size(), Matchers.equalTo(3));
        // The names set by the user _and_ the tags _must_ be ignored, or metrics will not show up.
        for (int i = 0; i < (outputInfos.size()); ++i) {
            Assert.assertThat(Structs.getString(outputInfos.get(i), USER_NAME), Matchers.equalTo(String.format("%s.out%s", stepName, i)));
        }
    }

    /**
     * Smoke test to fail fast if translation of a stateful ParDo in batch breaks.
     */
    @Test
    public void testBatchStatefulParDoTranslation() throws Exception {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        options.setStreaming(false);
        DataflowPipelineTranslator translator = DataflowPipelineTranslator.fromOptions(options);
        Pipeline pipeline = Pipeline.create(options);
        TupleTag<Integer> mainOutputTag = new TupleTag<Integer>() {};
        pipeline.apply(Create.of(KV.of(1, 1))).apply(ParDo.of(new org.apache.beam.sdk.transforms.DoFn<KV<Integer, Integer>, Integer>() {
            @StateId("unused")
            final StateSpec<ValueState<Integer>> stateSpec = StateSpecs.value(VarIntCoder.of());

            @ProcessElement
            public void process(ProcessContext c) {
                // noop
            }
        }).withOutputTags(mainOutputTag, TupleTagList.empty()));
        runner.replaceTransforms(pipeline);
        Job job = translator.translate(pipeline, runner, Collections.emptyList()).getJob();
        // The job should look like:
        // 0. ParallelRead (Create)
        // 1. ParDo(ReifyWVs)
        // 2. GroupByKeyAndSortValuesONly
        // 3. A ParDo over grouped and sorted KVs that is executed via ungrouping service-side
        List<Step> steps = job.getSteps();
        Assert.assertEquals(4, steps.size());
        Step createStep = steps.get(0);
        Assert.assertEquals("ParallelRead", createStep.getKind());
        Step reifyWindowedValueStep = steps.get(1);
        Assert.assertEquals("ParallelDo", reifyWindowedValueStep.getKind());
        Step gbkStep = steps.get(2);
        Assert.assertEquals("GroupByKey", gbkStep.getKind());
        Step statefulParDoStep = steps.get(3);
        Assert.assertEquals("ParallelDo", statefulParDoStep.getKind());
        Assert.assertThat(((String) (statefulParDoStep.getProperties().get(PropertyNames.USES_KEYED_STATE))), Matchers.not(Matchers.equalTo("true")));
    }

    /**
     * Smoke test to fail fast if translation of a splittable ParDo in streaming breaks.
     */
    @Test
    public void testStreamingSplittableParDoTranslation() throws Exception {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        options.setStreaming(true);
        DataflowPipelineTranslator translator = DataflowPipelineTranslator.fromOptions(options);
        Pipeline pipeline = Pipeline.create(options);
        PCollection<String> windowedInput = pipeline.apply(Create.of("a")).apply(Window.into(FixedWindows.of(Duration.standardMinutes(1))));
        windowedInput.apply(ParDo.of(new DataflowPipelineTranslatorTest.TestSplittableFn()));
        runner.replaceTransforms(pipeline);
        Job job = translator.translate(pipeline, runner, Collections.emptyList()).getJob();
        // The job should contain a SplittableParDo.ProcessKeyedElements step, translated as
        // "SplittableProcessKeyed".
        List<Step> steps = job.getSteps();
        Step processKeyedStep = null;
        for (Step step : steps) {
            if ("SplittableProcessKeyed".equals(step.getKind())) {
                Assert.assertNull(processKeyedStep);
                processKeyedStep = step;
            }
        }
        Assert.assertNotNull(processKeyedStep);
        @SuppressWarnings({ "unchecked", "rawtypes" })
        DoFnInfo<String, Integer> fnInfo = ((DoFnInfo<String, Integer>) (SerializableUtils.deserializeFromByteArray(jsonStringToByteArray(Structs.getString(processKeyedStep.getProperties(), SERIALIZED_FN)), "DoFnInfo")));
        Assert.assertThat(fnInfo.getDoFn(), Matchers.instanceOf(DataflowPipelineTranslatorTest.TestSplittableFn.class));
        Assert.assertThat(fnInfo.getWindowingStrategy().getWindowFn(), Matchers.<WindowFn>equalTo(FixedWindows.of(Duration.standardMinutes(1))));
        Assert.assertThat(fnInfo.getInputCoder(), Matchers.instanceOf(StringUtf8Coder.class));
        Coder<?> restrictionCoder = CloudObjects.coderFromCloudObject(((CloudObject) (Structs.getObject(processKeyedStep.getProperties(), RESTRICTION_CODER))));
        Assert.assertEquals(SerializableCoder.of(OffsetRange.class), restrictionCoder);
    }

    /**
     * Smoke test to fail fast if translation of a splittable ParDo in streaming breaks.
     */
    @Test
    public void testStreamingSplittableParDoTranslationFnApi() throws Exception {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        options.setStreaming(true);
        options.setExperiments(Arrays.asList("beam_fn_api"));
        DataflowPipelineTranslator translator = DataflowPipelineTranslator.fromOptions(options);
        Pipeline pipeline = Pipeline.create(options);
        PCollection<String> windowedInput = pipeline.apply(Create.of("a")).apply(Window.into(FixedWindows.of(Duration.standardMinutes(1))));
        windowedInput.apply(ParDo.of(new DataflowPipelineTranslatorTest.TestSplittableFn()));
        runner.replaceTransforms(pipeline);
        JobSpecification result = translator.translate(pipeline, runner, Collections.emptyList());
        Job job = result.getJob();
        // The job should contain a SplittableParDo.ProcessKeyedElements step, translated as
        // "SplittableProcessKeyed".
        List<Step> steps = job.getSteps();
        Step processKeyedStep = null;
        for (Step step : steps) {
            if ("SplittableProcessKeyed".equals(step.getKind())) {
                Assert.assertNull(processKeyedStep);
                processKeyedStep = step;
            }
        }
        Assert.assertNotNull(processKeyedStep);
        String fn = Structs.getString(processKeyedStep.getProperties(), SERIALIZED_FN);
        Components componentsProto = result.getPipelineProto().getComponents();
        RehydratedComponents components = RehydratedComponents.forComponents(componentsProto);
        RunnerApi.PTransform spkTransform = componentsProto.getTransformsOrThrow(fn);
        Assert.assertEquals(SPLITTABLE_PROCESS_KEYED_URN, spkTransform.getSpec().getUrn());
        ParDoPayload payload = ParDoPayload.parseFrom(spkTransform.getSpec().getPayload());
        Assert.assertThat(ParDoTranslation.doFnWithExecutionInformationFromProto(payload.getDoFn()).getDoFn(), Matchers.instanceOf(DataflowPipelineTranslatorTest.TestSplittableFn.class));
        Assert.assertThat(components.getCoder(payload.getRestrictionCoderId()), Matchers.instanceOf(SerializableCoder.class));
        // In the Fn API case, we still translate the restriction coder into the RESTRICTION_CODER
        // property as a CloudObject, and it gets passed through the Dataflow backend, but in the end
        // the Dataflow worker will end up fetching it from the SPK transform payload instead.
        Coder<?> restrictionCoder = CloudObjects.coderFromCloudObject(((CloudObject) (Structs.getObject(processKeyedStep.getProperties(), RESTRICTION_CODER))));
        Assert.assertEquals(SerializableCoder.of(OffsetRange.class), restrictionCoder);
    }

    @Test
    public void testToSingletonTranslationWithIsmSideInput() throws Exception {
        // A "change detector" test that makes sure the translation
        // of getting a PCollectionView<T> does not change
        // in bad ways during refactor
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        DataflowPipelineTranslator translator = DataflowPipelineTranslator.fromOptions(options);
        Pipeline pipeline = Pipeline.create(options);
        pipeline.apply(Create.of(1)).apply(View.asSingleton());
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        runner.replaceTransforms(pipeline);
        Job job = translator.translate(pipeline, runner, Collections.emptyList()).getJob();
        DataflowPipelineTranslatorTest.assertAllStepOutputsHaveUniqueIds(job);
        List<Step> steps = job.getSteps();
        Assert.assertEquals(9, steps.size());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> toIsmRecordOutputs = ((List<Map<String, Object>>) (steps.get(7).getProperties().get(OUTPUT_INFO)));
        Assert.assertTrue(Structs.getBoolean(Iterables.getOnlyElement(toIsmRecordOutputs), "use_indexed_format"));
        Step collectionToSingletonStep = steps.get(8);
        Assert.assertEquals("CollectionToSingleton", collectionToSingletonStep.getKind());
    }

    @Test
    public void testToIterableTranslationWithIsmSideInput() throws Exception {
        // A "change detector" test that makes sure the translation
        // of getting a PCollectionView<Iterable<T>> does not change
        // in bad ways during refactor
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        DataflowPipelineTranslator translator = DataflowPipelineTranslator.fromOptions(options);
        Pipeline pipeline = Pipeline.create(options);
        pipeline.apply(Create.of(1, 2, 3)).apply(View.asIterable());
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        runner.replaceTransforms(pipeline);
        Job job = translator.translate(pipeline, runner, Collections.emptyList()).getJob();
        DataflowPipelineTranslatorTest.assertAllStepOutputsHaveUniqueIds(job);
        List<Step> steps = job.getSteps();
        Assert.assertEquals(3, steps.size());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> toIsmRecordOutputs = ((List<Map<String, Object>>) (steps.get(1).getProperties().get(OUTPUT_INFO)));
        Assert.assertTrue(Structs.getBoolean(Iterables.getOnlyElement(toIsmRecordOutputs), "use_indexed_format"));
        Step collectionToSingletonStep = steps.get(2);
        Assert.assertEquals("CollectionToSingleton", collectionToSingletonStep.getKind());
    }

    @Test
    public void testStepDisplayData() throws Exception {
        DataflowPipelineOptions options = DataflowPipelineTranslatorTest.buildPipelineOptions();
        DataflowPipelineTranslator translator = DataflowPipelineTranslator.fromOptions(options);
        Pipeline pipeline = Pipeline.create(options);
        org.apache.beam.sdk.transforms.DoFn<Integer, Integer> fn1 = new org.apache.beam.sdk.transforms.DoFn<Integer, Integer>() {
            @ProcessElement
            public void processElement(ProcessContext c) throws Exception {
                c.output(c.element());
            }

            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo", "bar")).add(DisplayData.item("foo2", DataflowPipelineTranslatorTest.class).withLabel("Test Class").withLinkUrl("http://www.google.com"));
            }
        };
        org.apache.beam.sdk.transforms.DoFn<Integer, Integer> fn2 = new org.apache.beam.sdk.transforms.DoFn<Integer, Integer>() {
            @ProcessElement
            public void processElement(ProcessContext c) throws Exception {
                c.output(c.element());
            }

            @Override
            public void populateDisplayData(DisplayData.Builder builder) {
                builder.add(DisplayData.item("foo3", 1234));
            }
        };
        SingleOutput<Integer, Integer> parDo1 = ParDo.of(fn1);
        SingleOutput<Integer, Integer> parDo2 = ParDo.of(fn2);
        pipeline.apply(Create.of(1, 2, 3)).apply(parDo1).apply(parDo2);
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        runner.replaceTransforms(pipeline);
        Job job = translator.translate(pipeline, runner, Collections.emptyList()).getJob();
        DataflowPipelineTranslatorTest.assertAllStepOutputsHaveUniqueIds(job);
        List<Step> steps = job.getSteps();
        Assert.assertEquals(3, steps.size());
        Map<String, Object> parDo1Properties = steps.get(1).getProperties();
        Map<String, Object> parDo2Properties = steps.get(2).getProperties();
        Assert.assertThat(parDo1Properties, Matchers.hasKey("display_data"));
        @SuppressWarnings("unchecked")
        Collection<Map<String, String>> fn1displayData = ((Collection<Map<String, String>>) (parDo1Properties.get("display_data")));
        @SuppressWarnings("unchecked")
        Collection<Map<String, String>> fn2displayData = ((Collection<Map<String, String>>) (parDo2Properties.get("display_data")));
        ImmutableSet<ImmutableMap<String, Object>> expectedFn1DisplayData = ImmutableSet.of(ImmutableMap.<String, Object>builder().put("key", "foo").put("type", "STRING").put("value", "bar").put("namespace", fn1.getClass().getName()).build(), ImmutableMap.<String, Object>builder().put("key", "fn").put("label", "Transform Function").put("type", "JAVA_CLASS").put("value", fn1.getClass().getName()).put("shortValue", fn1.getClass().getSimpleName()).put("namespace", parDo1.getClass().getName()).build(), ImmutableMap.<String, Object>builder().put("key", "foo2").put("type", "JAVA_CLASS").put("value", DataflowPipelineTranslatorTest.class.getName()).put("shortValue", DataflowPipelineTranslatorTest.class.getSimpleName()).put("namespace", fn1.getClass().getName()).put("label", "Test Class").put("linkUrl", "http://www.google.com").build());
        ImmutableSet<ImmutableMap<String, Object>> expectedFn2DisplayData = ImmutableSet.of(ImmutableMap.<String, Object>builder().put("key", "fn").put("label", "Transform Function").put("type", "JAVA_CLASS").put("value", fn2.getClass().getName()).put("shortValue", fn2.getClass().getSimpleName()).put("namespace", parDo2.getClass().getName()).build(), ImmutableMap.<String, Object>builder().put("key", "foo3").put("type", "INTEGER").put("value", 1234L).put("namespace", fn2.getClass().getName()).build());
        Assert.assertEquals(expectedFn1DisplayData, ImmutableSet.copyOf(fn1displayData));
        Assert.assertEquals(expectedFn2DisplayData, ImmutableSet.copyOf(fn2displayData));
    }

    private static class TestSplittableFn extends org.apache.beam.sdk.transforms.DoFn<String, Integer> {
        @ProcessElement
        public void process(ProcessContext c, OffsetRangeTracker tracker) {
            // noop
        }

        @GetInitialRestriction
        public OffsetRange getInitialRange(String element) {
            return null;
        }
    }
}

