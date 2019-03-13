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
package org.apache.beam.runners.core.construction.expansion;


import ExpansionApi.ExpansionRequest;
import ExpansionApi.ExpansionResponse;
import ExpansionService.ExpansionServiceRegistrar;
import RunnerApi.FunctionSpec;
import RunnerApi.PTransform;
import com.google.auto.service.AutoService;
import java.util.Map;
import java.util.Set;
import org.apache.beam.model.expansion.v1.ExpansionApi;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.runners.core.construction.PipelineTranslation;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.Impulse;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableMap;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests for {@link ExpansionService}.
 */
public class ExpansionServiceTest {
    private static final String TEST_URN = "test:beam:transforms:count";

    private static final String TEST_NAME = "TestName";

    private static final String TEST_NAMESPACE = "namespace";

    private ExpansionService expansionService = new ExpansionService();

    /**
     * Registers a single test transformation.
     */
    @AutoService(ExpansionServiceRegistrar.class)
    public static class TestTransforms implements ExpansionService.ExpansionServiceRegistrar {
        @Override
        public Map<String, ExpansionService.TransformProvider> knownTransforms() {
            return ImmutableMap.of(ExpansionServiceTest.TEST_URN, ( spec) -> Count.perElement());
        }
    }

    @Test
    public void testConstruct() {
        Pipeline p = Pipeline.create();
        p.apply(Impulse.create());
        RunnerApi.Pipeline pipelineProto = PipelineTranslation.toProto(p);
        String inputPcollId = Iterables.getOnlyElement(Iterables.getOnlyElement(pipelineProto.getComponents().getTransformsMap().values()).getOutputsMap().values());
        ExpansionApi.ExpansionRequest request = ExpansionRequest.newBuilder().setComponents(pipelineProto.getComponents()).setTransform(PTransform.newBuilder().setUniqueName(ExpansionServiceTest.TEST_NAME).setSpec(FunctionSpec.newBuilder().setUrn(ExpansionServiceTest.TEST_URN)).putInputs("input", inputPcollId)).setNamespace(ExpansionServiceTest.TEST_NAMESPACE).build();
        ExpansionApi.ExpansionResponse response = expansionService.expand(request);
        RunnerApi.PTransform expandedTransform = response.getTransform();
        Assert.assertEquals(ExpansionServiceTest.TEST_NAME, expandedTransform.getUniqueName());
        // Verify it has the right input.
        Assert.assertEquals(inputPcollId, Iterables.getOnlyElement(expandedTransform.getInputsMap().values()));
        // Loose check that it's composite, and its children are represented.
        Assert.assertNotEquals(expandedTransform.getSubtransformsCount(), 0);
        for (String subtransform : expandedTransform.getSubtransformsList()) {
            Assert.assertTrue(response.getComponents().containsTransforms(subtransform));
        }
        // Check that any newly generated components are properly namespaced.
        Set<String> originalIds = allIds(request.getComponents());
        for (String id : allIds(response.getComponents())) {
            Assert.assertTrue(id, ((id.startsWith(ExpansionServiceTest.TEST_NAMESPACE)) || (originalIds.contains(id))));
        }
    }
}

