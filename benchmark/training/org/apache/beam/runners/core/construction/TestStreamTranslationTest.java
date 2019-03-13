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


import RunnerApi.FunctionSpec;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.model.pipeline.v1.RunnerApi.TestStreamPayload;
import org.apache.beam.sdk.runners.AppliedPTransform;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.testing.TestStream;
import org.apache.beam.sdk.values.PBegin;
import org.apache.beam.sdk.values.PCollection;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link TestStreamTranslation}.
 */
@RunWith(Parameterized.class)
public class TestStreamTranslationTest {
    @Parameterized.Parameter(0)
    public TestStream<String> testStream;

    public static TestPipeline p = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Test
    public void testEncodedProto() throws Exception {
        SdkComponents components = SdkComponents.create();
        components.registerEnvironment(Environments.createDockerEnvironment("java"));
        RunnerApi.TestStreamPayload payload = TestStreamTranslation.payloadForTestStream(testStream, components);
        TestStreamTranslationTest.verifyTestStreamEncoding(testStream, payload, RehydratedComponents.forComponents(components.toComponents()));
    }

    @Test
    public void testRegistrarEncodedProto() throws Exception {
        PCollection<String> output = TestStreamTranslationTest.p.apply(testStream);
        AppliedPTransform<PBegin, PCollection<String>, TestStream<String>> appliedTestStream = AppliedPTransform.of("fakeName", PBegin.in(TestStreamTranslationTest.p).expand(), output.expand(), testStream, TestStreamTranslationTest.p);
        SdkComponents components = SdkComponents.create();
        components.registerEnvironment(Environments.createDockerEnvironment("java"));
        RunnerApi.FunctionSpec spec = PTransformTranslation.toProto(appliedTestStream, components).getSpec();
        Assert.assertThat(spec.getUrn(), Matchers.equalTo(PTransformTranslation.TEST_STREAM_TRANSFORM_URN));
        RunnerApi.TestStreamPayload payload = TestStreamPayload.parseFrom(spec.getPayload());
        TestStreamTranslationTest.verifyTestStreamEncoding(testStream, payload, RehydratedComponents.forComponents(components.toComponents()));
    }
}

