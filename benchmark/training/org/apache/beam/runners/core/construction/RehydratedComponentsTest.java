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


import org.apache.beam.model.pipeline.v1.RunnerApi.Environment;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.coders.NullableCoder;
import org.apache.beam.sdk.coders.VarIntCoder;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link RehydratedComponents}.
 *
 * <p>These are basic sanity checks. The most thorough testing of this is by extensive use in all
 * other rehydration. The two are tightly coupled, as they recursively invoke each other.
 */
@RunWith(JUnit4.class)
public class RehydratedComponentsTest {
    @Test
    public void testSimpleCoder() throws Exception {
        SdkComponents sdkComponents = SdkComponents.create();
        sdkComponents.registerEnvironment(Environments.createDockerEnvironment("java"));
        Coder<?> coder = VarIntCoder.of();
        String id = sdkComponents.registerCoder(coder);
        RehydratedComponents rehydratedComponents = RehydratedComponents.forComponents(sdkComponents.toComponents());
        Coder<?> rehydratedCoder = rehydratedComponents.getCoder(id);
        Assert.assertThat(rehydratedCoder, Matchers.equalTo(((Coder) (coder))));
        Assert.assertThat(rehydratedComponents.getCoder(id), Matchers.theInstance(((Coder) (rehydratedCoder))));
    }

    @Test
    public void testCompoundCoder() throws Exception {
        SdkComponents sdkComponents = SdkComponents.create();
        sdkComponents.registerEnvironment(Environments.createDockerEnvironment("java"));
        Coder<?> coder = VarIntCoder.of();
        Coder<?> compoundCoder = NullableCoder.of(coder);
        String compoundCoderId = sdkComponents.registerCoder(compoundCoder);
        String coderId = sdkComponents.registerCoder(coder);
        RehydratedComponents rehydratedComponents = RehydratedComponents.forComponents(sdkComponents.toComponents());
        Coder<?> rehydratedCoder = rehydratedComponents.getCoder(coderId);
        Coder<?> rehydratedCompoundCoder = rehydratedComponents.getCoder(compoundCoderId);
        Assert.assertThat(rehydratedCoder, Matchers.equalTo(((Coder) (coder))));
        Assert.assertThat(rehydratedCompoundCoder, Matchers.equalTo(((Coder) (compoundCoder))));
        Assert.assertThat(rehydratedComponents.getCoder(coderId), Matchers.theInstance(((Coder) (rehydratedCoder))));
        Assert.assertThat(rehydratedComponents.getCoder(compoundCoderId), Matchers.theInstance(((Coder) (rehydratedCompoundCoder))));
    }

    @Test
    public void testWindowingStrategy() throws Exception {
        SdkComponents sdkComponents = SdkComponents.create();
        sdkComponents.registerEnvironment(Environments.createDockerEnvironment("java"));
        WindowingStrategy windowingStrategy = WindowingStrategy.of(FixedWindows.of(Duration.millis(1))).withAllowedLateness(Duration.standardSeconds(4));
        String id = sdkComponents.registerWindowingStrategy(windowingStrategy);
        RehydratedComponents rehydratedComponents = RehydratedComponents.forComponents(sdkComponents.toComponents());
        WindowingStrategy<?, ?> rehydratedStrategy = rehydratedComponents.getWindowingStrategy(id);
        Assert.assertThat(rehydratedStrategy, Matchers.equalTo(((WindowingStrategy) (windowingStrategy.fixDefaults()))));
        Assert.assertThat(rehydratedComponents.getWindowingStrategy(id), Matchers.theInstance(((WindowingStrategy) (rehydratedStrategy))));
    }

    @Test
    public void testEnvironment() {
        SdkComponents sdkComponents = SdkComponents.create();
        sdkComponents.registerEnvironment(Environments.createDockerEnvironment("java"));
        Environment env = Environments.createDockerEnvironment("java_test");
        String id = sdkComponents.registerEnvironment(env);
        RehydratedComponents rehydratedComponents = RehydratedComponents.forComponents(sdkComponents.toComponents());
        Environment rehydratedEnv = rehydratedComponents.getEnvironment(id);
        Assert.assertThat(rehydratedEnv, Matchers.equalTo(env));
        Assert.assertThat(rehydratedComponents.getEnvironment(id), Matchers.theInstance(rehydratedEnv));
    }
}

