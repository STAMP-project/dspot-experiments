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
package org.apache.beam.runners.core.construction.graph;


import java.nio.charset.StandardCharsets;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.model.pipeline.v1.RunnerApi.Coder;
import org.apache.beam.model.pipeline.v1.RunnerApi.Components;
import org.apache.beam.model.pipeline.v1.RunnerApi.ComponentsOrBuilder;
import org.apache.beam.model.pipeline.v1.RunnerApi.FunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.MessageWithComponents;
import org.apache.beam.model.pipeline.v1.RunnerApi.PCollection;
import org.apache.beam.model.pipeline.v1.RunnerApi.PTransform;
import org.apache.beam.model.pipeline.v1.RunnerApi.Pipeline;
import org.apache.beam.model.pipeline.v1.RunnerApi.SdkFunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi.WindowingStrategy;
import org.apache.beam.runners.core.construction.graph.ProtoOverrides.TransformReplacement;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link ProtoOverrides}.
 */
@RunWith(JUnit4.class)
public class ProtoOverridesTest {
    @Test
    public void replacesOnlyMatching() {
        RunnerApi.Pipeline p = Pipeline.newBuilder().addAllRootTransformIds(ImmutableList.of("first", "second")).setComponents(Components.newBuilder().putTransforms("first", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn("beam:first")).build()).putTransforms("second", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn("beam:second")).build()).putPcollections("intermediatePc", PCollection.newBuilder().setUniqueName("intermediate").build()).putCoders("coder", Coder.newBuilder().setSpec(SdkFunctionSpec.getDefaultInstance()).build())).build();
        PTransform secondReplacement = PTransform.newBuilder().addSubtransforms("second_sub").setSpec(FunctionSpec.newBuilder().setUrn("beam:second:replacement").setPayload(ByteString.copyFrom("foo-bar-baz".getBytes(StandardCharsets.UTF_8)))).build();
        WindowingStrategy introducedWS = WindowingStrategy.newBuilder().setAccumulationMode(ACCUMULATING).build();
        RunnerApi.Components extraComponents = Components.newBuilder().putPcollections("intermediatePc", PCollection.newBuilder().setUniqueName("intermediate_replacement").build()).putWindowingStrategies("new_ws", introducedWS).putTransforms("second_sub", PTransform.getDefaultInstance()).build();
        Pipeline updated = ProtoOverrides.updateTransform("beam:second", p, new ProtoOverridesTest.TestReplacer(secondReplacement, extraComponents));
        PTransform updatedSecond = updated.getComponents().getTransformsOrThrow("second");
        Assert.assertThat(updatedSecond, Matchers.equalTo(secondReplacement));
        Assert.assertThat(updated.getComponents().getWindowingStrategiesOrThrow("new_ws"), Matchers.equalTo(introducedWS));
        Assert.assertThat(updated.getComponents().getTransformsOrThrow("second_sub"), Matchers.equalTo(PTransform.getDefaultInstance()));
        // TODO: This might not be appropriate. Merging in the other direction might force that callers
        // are well behaved.
        Assert.assertThat(updated.getComponents().getPcollectionsOrThrow("intermediatePc").getUniqueName(), Matchers.equalTo("intermediate_replacement"));
        // Assert that the untouched components are unchanged.
        Assert.assertThat(updated.getComponents().getTransformsOrThrow("first"), Matchers.equalTo(p.getComponents().getTransformsOrThrow("first")));
        Assert.assertThat(updated.getComponents().getCodersOrThrow("coder"), Matchers.equalTo(p.getComponents().getCodersOrThrow("coder")));
        Assert.assertThat(updated.getRootTransformIdsList(), Matchers.equalTo(p.getRootTransformIdsList()));
    }

    @Test
    public void replacesMultiple() {
        RunnerApi.Pipeline p = Pipeline.newBuilder().addAllRootTransformIds(ImmutableList.of("first", "second")).setComponents(Components.newBuilder().putTransforms("first", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn("beam:first")).build()).putTransforms("second", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn("beam:repeated")).build()).putTransforms("third", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn("beam:repeated")).build()).putPcollections("intermediatePc", PCollection.newBuilder().setUniqueName("intermediate").build()).putCoders("coder", Coder.newBuilder().setSpec(SdkFunctionSpec.getDefaultInstance()).build())).build();
        ByteString newPayload = ByteString.copyFrom("foo-bar-baz".getBytes(StandardCharsets.UTF_8));
        Pipeline updated = ProtoOverrides.updateTransform("beam:repeated", p, ( transformId, existingComponents) -> {
            String subtransform = String.format("%s_sub", transformId);
            return MessageWithComponents.newBuilder().setPtransform(PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn("beam:repeated:replacement").setPayload(newPayload)).addSubtransforms(subtransform)).setComponents(Components.newBuilder().putTransforms(subtransform, PTransform.newBuilder().setUniqueName(subtransform).build())).build();
        });
        PTransform updatedSecond = updated.getComponents().getTransformsOrThrow("second");
        PTransform updatedThird = updated.getComponents().getTransformsOrThrow("third");
        Assert.assertThat(updatedSecond, Matchers.not(Matchers.equalTo(p.getComponents().getTransformsOrThrow("second"))));
        Assert.assertThat(updatedThird, Matchers.not(Matchers.equalTo(p.getComponents().getTransformsOrThrow("third"))));
        Assert.assertThat(updatedSecond.getSubtransformsList(), Matchers.contains("second_sub"));
        Assert.assertThat(updatedSecond.getSpec().getPayload(), Matchers.equalTo(newPayload));
        Assert.assertThat(updatedThird.getSubtransformsList(), Matchers.contains("third_sub"));
        Assert.assertThat(updatedThird.getSpec().getPayload(), Matchers.equalTo(newPayload));
        Assert.assertThat(updated.getComponents().getTransformsMap(), Matchers.hasKey("second_sub"));
        Assert.assertThat(updated.getComponents().getTransformsMap(), Matchers.hasKey("third_sub"));
        Assert.assertThat(updated.getComponents().getTransformsOrThrow("second_sub").getUniqueName(), Matchers.equalTo("second_sub"));
        Assert.assertThat(updated.getComponents().getTransformsOrThrow("third_sub").getUniqueName(), Matchers.equalTo("third_sub"));
    }

    @Test
    public void replaceExistingCompositeSucceeds() {
        Pipeline p = Pipeline.newBuilder().addRootTransformIds("root").setComponents(Components.newBuilder().putTransforms("root", PTransform.newBuilder().addSubtransforms("sub_first").setSpec(FunctionSpec.newBuilder().setUrn("beam:composite")).build()).putTransforms("sub_first", PTransform.newBuilder().setSpec(FunctionSpec.newBuilder().setUrn("beam:inner")).build())).build();
        Pipeline pipeline = ProtoOverrides.updateTransform("beam:composite", p, new ProtoOverridesTest.TestReplacer(PTransform.newBuilder().addSubtransforms("foo").addSubtransforms("bar").setSpec(FunctionSpec.getDefaultInstance().newBuilderForType().setUrn("beam:composite")).build(), Components.getDefaultInstance()));
        Assert.assertThat(pipeline.getComponents().getTransformsOrThrow("root").getSpec().getUrn(), Matchers.equalTo("beam:composite"));
        Assert.assertThat(pipeline.getComponents().getTransformsOrThrow("root").getSubtransformsList(), Matchers.contains("foo", "bar"));
    }

    private static class TestReplacer implements TransformReplacement {
        private final PTransform extraTransform;

        private final Components extraComponents;

        private TestReplacer(PTransform extraTransform, Components extraComponents) {
            this.extraTransform = extraTransform;
            this.extraComponents = extraComponents;
        }

        @Override
        public MessageWithComponents getReplacement(String transformId, ComponentsOrBuilder existingComponents) {
            return MessageWithComponents.newBuilder().setPtransform(extraTransform).setComponents(extraComponents).build();
        }
    }
}

