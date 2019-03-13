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


import PipelineVisitor.Defaults;
import java.io.Serializable;
import javax.annotation.Nullable;
import org.apache.beam.runners.dataflow.BatchStatefulParDoOverrides.StatefulMultiOutputParDo;
import org.apache.beam.runners.dataflow.BatchStatefulParDoOverrides.StatefulSingleOutputParDo;
import org.apache.beam.runners.dataflow.options.DataflowPipelineOptions;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.Pipeline.PipelineVisitor;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.runners.TransformHierarchy.Node;
import org.apache.beam.sdk.state.StateSpec;
import org.apache.beam.sdk.state.StateSpecs;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static CompositeBehavior.DO_NOT_ENTER_TRANSFORM;
import static CompositeBehavior.ENTER_TRANSFORM;


/**
 * Tests for {@link BatchStatefulParDoOverrides}.
 */
@RunWith(JUnit4.class)
public class BatchStatefulParDoOverridesTest implements Serializable {
    @Test
    public void testSingleOutputOverrideNonCrashing() throws Exception {
        DataflowPipelineOptions options = BatchStatefulParDoOverridesTest.buildPipelineOptions();
        options.setRunner(DataflowRunner.class);
        Pipeline pipeline = Pipeline.create(options);
        BatchStatefulParDoOverridesTest.DummyStatefulDoFn fn = new BatchStatefulParDoOverridesTest.DummyStatefulDoFn();
        pipeline.apply(Create.of(KV.of(1, 2))).apply(ParDo.of(fn));
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        runner.replaceTransforms(pipeline);
        Assert.assertThat(BatchStatefulParDoOverridesTest.findBatchStatefulDoFn(pipeline), Matchers.equalTo(((DoFn) (fn))));
    }

    @Test
    public void testFnApiSingleOutputOverrideNonCrashing() throws Exception {
        DataflowPipelineOptions options = BatchStatefulParDoOverridesTest.buildPipelineOptions("--experiments=beam_fn_api");
        options.setRunner(DataflowRunner.class);
        Pipeline pipeline = Pipeline.create(options);
        BatchStatefulParDoOverridesTest.DummyStatefulDoFn fn = new BatchStatefulParDoOverridesTest.DummyStatefulDoFn();
        pipeline.apply(Create.of(KV.of(1, 2))).apply(ParDo.of(fn));
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        runner.replaceTransforms(pipeline);
        Assert.assertThat(BatchStatefulParDoOverridesTest.findBatchStatefulDoFn(pipeline), Matchers.equalTo(((DoFn) (fn))));
    }

    @Test
    public void testMultiOutputOverrideNonCrashing() throws Exception {
        DataflowPipelineOptions options = BatchStatefulParDoOverridesTest.buildPipelineOptions();
        options.setRunner(DataflowRunner.class);
        Pipeline pipeline = Pipeline.create(options);
        TupleTag<Integer> mainOutputTag = new TupleTag<Integer>() {};
        TupleTag<Integer> sideOutputTag = new TupleTag<Integer>() {};
        BatchStatefulParDoOverridesTest.DummyStatefulDoFn fn = new BatchStatefulParDoOverridesTest.DummyStatefulDoFn();
        pipeline.apply(Create.of(KV.of(1, 2))).apply(ParDo.of(fn).withOutputTags(mainOutputTag, TupleTagList.of(sideOutputTag)));
        DataflowRunner runner = DataflowRunner.fromOptions(options);
        runner.replaceTransforms(pipeline);
        Assert.assertThat(BatchStatefulParDoOverridesTest.findBatchStatefulDoFn(pipeline), Matchers.equalTo(((DoFn) (fn))));
    }

    private static class DummyStatefulDoFn extends DoFn<KV<Integer, Integer>, Integer> {
        @StateId("foo")
        private final StateSpec<ValueState<Integer>> spec = StateSpecs.value(VarIntCoder.of());

        @ProcessElement
        public void processElem(ProcessContext c) {
            // noop
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof BatchStatefulParDoOverridesTest.DummyStatefulDoFn;
        }

        @Override
        public int hashCode() {
            return getClass().hashCode();
        }
    }

    private static class FindBatchStatefulDoFnVisitor extends PipelineVisitor.Defaults {
        @Nullable
        private DoFn<?, ?> batchStatefulDoFn;

        public DoFn<?, ?> getStatefulDoFn() {
            Assert.assertThat(batchStatefulDoFn, Matchers.not(Matchers.nullValue()));
            return batchStatefulDoFn;
        }

        @Override
        public CompositeBehavior enterCompositeTransform(Node node) {
            if ((node.getTransform()) instanceof StatefulSingleOutputParDo) {
                batchStatefulDoFn = getOriginalParDo().getFn();
                return DO_NOT_ENTER_TRANSFORM;
            } else
                if ((node.getTransform()) instanceof StatefulMultiOutputParDo) {
                    batchStatefulDoFn = getOriginalParDo().getFn();
                    return DO_NOT_ENTER_TRANSFORM;
                } else {
                    return ENTER_TRANSFORM;
                }

        }
    }
}

