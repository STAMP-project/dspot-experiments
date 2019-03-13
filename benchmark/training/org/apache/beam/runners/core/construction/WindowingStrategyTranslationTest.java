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


import com.google.auto.value.AutoValue;
import org.apache.beam.model.pipeline.v1.RunnerApi;
import org.apache.beam.sdk.transforms.windowing.AfterWatermark;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Trigger;
import org.apache.beam.sdk.transforms.windowing.WindowFn;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit tests for {@link WindowingStrategy}.
 */
@RunWith(Parameterized.class)
public class WindowingStrategyTranslationTest {
    // Each spec activates tests of all subsets of its fields
    @AutoValue
    abstract static class ToProtoAndBackSpec {
        abstract WindowingStrategy getWindowingStrategy();
    }

    private static final WindowFn<?, ?> REPRESENTATIVE_WINDOW_FN = FixedWindows.of(Duration.millis(12));

    private static final Trigger REPRESENTATIVE_TRIGGER = AfterWatermark.pastEndOfWindow();

    @Parameterized.Parameter(0)
    public WindowingStrategyTranslationTest.ToProtoAndBackSpec toProtoAndBackSpec;

    @Test
    public void testToProtoAndBack() throws Exception {
        WindowingStrategy<?, ?> windowingStrategy = toProtoAndBackSpec.getWindowingStrategy();
        SdkComponents components = SdkComponents.create();
        components.registerEnvironment(Environments.createDockerEnvironment("java"));
        WindowingStrategy<?, ?> toProtoAndBackWindowingStrategy = WindowingStrategyTranslation.fromProto(WindowingStrategyTranslation.toMessageProto(windowingStrategy, components));
        Assert.assertThat(toProtoAndBackWindowingStrategy, Matchers.equalTo(((WindowingStrategy) (windowingStrategy.fixDefaults()))));
    }

    @Test
    public void testToProtoAndBackWithComponents() throws Exception {
        WindowingStrategy<?, ?> windowingStrategy = toProtoAndBackSpec.getWindowingStrategy();
        SdkComponents components = SdkComponents.create();
        components.registerEnvironment(Environments.createDockerEnvironment("java"));
        RunnerApi.WindowingStrategy proto = WindowingStrategyTranslation.toProto(windowingStrategy, components);
        RehydratedComponents protoComponents = RehydratedComponents.forComponents(components.toComponents());
        Assert.assertThat(WindowingStrategyTranslation.fromProto(proto, protoComponents).fixDefaults(), Matchers.equalTo(windowingStrategy.fixDefaults()));
        protoComponents.getCoder(components.registerCoder(windowingStrategy.getWindowFn().windowCoder()));
        Assert.assertThat(proto.getAssignsToOneWindow(), Matchers.equalTo(windowingStrategy.getWindowFn().assignsToOneWindow()));
    }
}

