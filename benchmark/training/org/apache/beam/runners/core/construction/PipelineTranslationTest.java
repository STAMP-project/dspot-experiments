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
package org.apache.beam.runners.core.construction;


import CoderTranslation.KNOWN_CODER_URNS;
import PTransformTranslation.COMBINE_PER_KEY_TRANSFORM_URN;
import PTransformTranslation.CREATE_VIEW_TRANSFORM_URN;
import PipelineVisitor.Defaults;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.Pipeline.PipelineVisitor;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.StructuredCoder;
import org.apache.beam.sdk.runners.TransformHierarchy.Node;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PValue;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link PipelineTranslation}.
 */
@RunWith(Parameterized.class)
public class PipelineTranslationTest {
    @Parameterized.Parameter(0)
    public Pipeline pipeline;

    @Test
    public void testProtoDirectly() {
        final RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(pipeline, false);
        pipeline.traverseTopologically(new PipelineTranslationTest.PipelineProtoVerificationVisitor(pipelineProto, false));
    }

    @Test
    public void testProtoDirectlyWithViewTransform() {
        final RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(pipeline, true);
        pipeline.traverseTopologically(new PipelineTranslationTest.PipelineProtoVerificationVisitor(pipelineProto, true));
    }

    private static class PipelineProtoVerificationVisitor extends PipelineVisitor.Defaults {
        private final Pipeline pipelineProto;

        private boolean useDeprecatedViewTransforms;

        Set<Node> transforms;

        Set<PCollection<?>> pcollections;

        Set<Coder<?>> coders;

        Set<WindowingStrategy<?, ?>> windowingStrategies;

        int missingViewTransforms = 0;

        public PipelineProtoVerificationVisitor(RunnerApi.Pipeline pipelineProto, boolean useDeprecatedViewTransforms) {
            this.pipelineProto = pipelineProto;
            this.useDeprecatedViewTransforms = useDeprecatedViewTransforms;
            transforms = new HashSet();
            pcollections = new HashSet();
            coders = new HashSet();
            windowingStrategies = new HashSet();
        }

        @Override
        public void leaveCompositeTransform(Node node) {
            if (node.isRootNode()) {
                Assert.assertThat("Unexpected number of PTransforms", pipelineProto.getComponents().getTransformsCount(), Matchers.equalTo(((transforms.size()) - (missingViewTransforms))));
                Assert.assertThat("Unexpected number of PCollections", pipelineProto.getComponents().getPcollectionsCount(), Matchers.equalTo(((pcollections.size()) - (missingViewTransforms))));
                Assert.assertThat("Unexpected number of Coders", pipelineProto.getComponents().getCodersCount(), Matchers.equalTo(coders.size()));
                Assert.assertThat("Unexpected number of Windowing Strategies", pipelineProto.getComponents().getWindowingStrategiesCount(), Matchers.equalTo(windowingStrategies.size()));
            } else {
                transforms.add(node);
                if (COMBINE_PER_KEY_TRANSFORM_URN.equals(PTransformTranslation.urnForTransformOrNull(node.getTransform()))) {
                    // Combine translation introduces a coder that is not assigned to any PCollection
                    // in the default expansion, and must be explicitly added here.
                    try {
                        addCoders(PipelineTranslationTest.getAccumulatorCoder(node.toAppliedPTransform(getPipeline())));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        @Override
        public void visitPrimitiveTransform(Node node) {
            transforms.add(node);
            if ((!(useDeprecatedViewTransforms)) && (CREATE_VIEW_TRANSFORM_URN.equals(PTransformTranslation.urnForTransformOrNull(node.getTransform())))) {
                missingViewTransforms += 1;
            }
        }

        @Override
        public void visitValue(PValue value, Node producer) {
            if (value instanceof PCollection) {
                PCollection pc = ((PCollection) (value));
                pcollections.add(pc);
                addCoders(pc.getCoder());
                windowingStrategies.add(pc.getWindowingStrategy());
                addCoders(pc.getWindowingStrategy().getWindowFn().windowCoder());
            }
        }

        private void addCoders(Coder<?> coder) {
            coders.add(coder);
            if (KNOWN_CODER_URNS.containsKey(coder.getClass())) {
                for (Coder<?> component : ((StructuredCoder<?>) (coder)).getComponents()) {
                    addCoders(component);
                }
            }
        }
    }
}

