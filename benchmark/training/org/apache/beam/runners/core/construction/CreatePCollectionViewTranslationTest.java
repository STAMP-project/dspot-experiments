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


import org.apache.beam.model.pipeline.v1.RunnerApi.FunctionSpec;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.View.CreatePCollectionView;
import org.apache.beam.sdk.util.SerializableUtils;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link CreatePCollectionViewTranslation}.
 */
@RunWith(Parameterized.class)
public class CreatePCollectionViewTranslationTest {
    @Parameterized.Parameter(0)
    public CreatePCollectionView<?, ?> createViewTransform;

    public static TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    private static final PCollection<KV<Void, String>> testPCollection = CreatePCollectionViewTranslationTest.p.apply(Create.of(KV.of(((Void) (null)), "one")));

    @Test
    public void testEncodedProto() throws Exception {
        SdkComponents components = SdkComponents.create();
        components.registerEnvironment(Environments.createDockerEnvironment("java"));
        components.registerPCollection(CreatePCollectionViewTranslationTest.testPCollection);
        AppliedPTransform<?, ?, ?> appliedPTransform = AppliedPTransform.of("foo", CreatePCollectionViewTranslationTest.testPCollection.expand(), createViewTransform.getView().expand(), createViewTransform, CreatePCollectionViewTranslationTest.p);
        FunctionSpec payload = PTransformTranslation.toProto(appliedPTransform, components).getSpec();
        // Checks that the payload is what it should be
        PCollectionView<?> deserializedView = ((PCollectionView<?>) (SerializableUtils.deserializeFromByteArray(payload.getPayload().toByteArray(), PCollectionView.class.getSimpleName())));
        Assert.assertThat(deserializedView, Matchers.equalTo(createViewTransform.getView()));
    }

    @Test
    public void testExtractionDirectFromTransform() throws Exception {
        SdkComponents components = SdkComponents.create();
        components.registerEnvironment(Environments.createDockerEnvironment("java"));
        components.registerPCollection(CreatePCollectionViewTranslationTest.testPCollection);
        AppliedPTransform<?, ?, ?> appliedPTransform = AppliedPTransform.of("foo", CreatePCollectionViewTranslationTest.testPCollection.expand(), createViewTransform.getView().expand(), createViewTransform, CreatePCollectionViewTranslationTest.p);
        CreatePCollectionViewTranslation.getView(((AppliedPTransform) (appliedPTransform)));
        FunctionSpec payload = PTransformTranslation.toProto(appliedPTransform, components).getSpec();
        // Checks that the payload is what it should be
        PCollectionView<?> deserializedView = ((PCollectionView<?>) (SerializableUtils.deserializeFromByteArray(payload.getPayload().toByteArray(), PCollectionView.class.getSimpleName())));
        Assert.assertThat(deserializedView, Matchers.equalTo(createViewTransform.getView()));
    }
}

