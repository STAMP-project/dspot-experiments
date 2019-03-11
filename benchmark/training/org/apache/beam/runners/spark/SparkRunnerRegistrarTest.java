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
package org.apache.beam.runners.spark;


import SparkRunnerRegistrar.Options;
import SparkRunnerRegistrar.Runner;
import java.util.ServiceLoader;
import org.apache.beam.sdk.options.PipelineOptionsRegistrar;
import org.apache.beam.sdk.runners.PipelineRunnerRegistrar;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Test {@link SparkRunnerRegistrar}.
 */
@RunWith(JUnit4.class)
public class SparkRunnerRegistrarTest {
    @Test
    public void testOptions() {
        Assert.assertEquals(ImmutableList.of(SparkPipelineOptions.class), new SparkRunnerRegistrar.Options().getPipelineOptions());
    }

    @Test
    public void testRunners() {
        Assert.assertEquals(ImmutableList.of(SparkRunner.class, TestSparkRunner.class), new SparkRunnerRegistrar.Runner().getPipelineRunners());
    }

    @Test
    public void testServiceLoaderForOptions() {
        for (PipelineOptionsRegistrar registrar : Lists.newArrayList(ServiceLoader.load(PipelineOptionsRegistrar.class).iterator())) {
            if (registrar instanceof SparkRunnerRegistrar.Options) {
                return;
            }
        }
        Assert.fail(("Expected to find " + (Options.class)));
    }

    @Test
    public void testServiceLoaderForRunner() {
        for (PipelineRunnerRegistrar registrar : Lists.newArrayList(ServiceLoader.load(PipelineRunnerRegistrar.class).iterator())) {
            if (registrar instanceof SparkRunnerRegistrar.Runner) {
                return;
            }
        }
        Assert.fail(("Expected to find " + (Runner.class)));
    }
}

