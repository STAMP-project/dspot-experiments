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
package org.apache.beam.sdk.extensions.euphoria.core.client.operator;


import AccumulationMode.DISCARDING_FIRED_PANES;
import org.apache.beam.sdk.transforms.windowing.DefaultTrigger;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.transforms.windowing.WindowDesc;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test operator CountByKey.
 */
public class CountByKeyTest {
    @Test
    public void testBuild() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final FixedWindows windowing = FixedWindows.of(Duration.standardHours(1));
        final DefaultTrigger trigger = DefaultTrigger.of();
        final PCollection<KV<String, Long>> counted = CountByKey.named("CountByKey1").of(dataset).keyBy(( s) -> s).windowBy(windowing).triggeredBy(trigger).discardingFiredPanes().withAllowedLateness(Duration.millis(1000)).output();
        final CountByKey count = ((CountByKey) (TestUtils.getProducer(counted)));
        Assert.assertTrue(count.getName().isPresent());
        Assert.assertEquals("CountByKey1", count.getName().get());
        Assert.assertNotNull(count.getKeyExtractor());
        Assert.assertTrue(count.getWindow().isPresent());
        final WindowDesc<?> desc = WindowDesc.of(((Window<?>) (count.getWindow().get())));
        Assert.assertEquals(windowing, desc.getWindowFn());
        Assert.assertEquals(trigger, desc.getTrigger());
        Assert.assertEquals(DISCARDING_FIRED_PANES, desc.getAccumulationMode());
        Assert.assertEquals(Duration.millis(1000), desc.getAllowedLateness());
    }

    @Test
    public void testBuild_ImplicitName() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<KV<String, Long>> counted = CountByKey.of(dataset).keyBy(( s) -> s).output();
        final CountByKey count = ((CountByKey) (TestUtils.getProducer(counted)));
        Assert.assertFalse(count.getName().isPresent());
    }

    @Test
    public void testBuild_Windowing() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<KV<String, Long>> counted = CountByKey.named("CountByKey1").of(dataset).keyBy(( s) -> s).windowBy(FixedWindows.of(Duration.standardHours(1))).triggeredBy(DefaultTrigger.of()).accumulationMode(DISCARDING_FIRED_PANES).output();
        final CountByKey count = ((CountByKey) (TestUtils.getProducer(counted)));
        Assert.assertTrue(count.getWindow().isPresent());
        final WindowDesc<?> desc = WindowDesc.of(((Window<?>) (count.getWindow().get())));
        Assert.assertEquals(FixedWindows.of(Duration.standardHours(1)), desc.getWindowFn());
        Assert.assertEquals(DefaultTrigger.of(), desc.getTrigger());
        Assert.assertEquals(DISCARDING_FIRED_PANES, desc.getAccumulationMode());
    }

    @Test
    public void testWindow_applyIf() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final FixedWindows windowing = FixedWindows.of(Duration.standardHours(1));
        final DefaultTrigger trigger = DefaultTrigger.of();
        final PCollection<KV<String, Long>> counted = CountByKey.named("CountByKey1").of(dataset).keyBy(( s) -> s).applyIf(true, ( b) -> b.windowBy(windowing).triggeredBy(trigger).discardingFiredPanes()).output();
        final CountByKey count = ((CountByKey) (TestUtils.getProducer(counted)));
        Assert.assertTrue(count.getWindow().isPresent());
        final WindowDesc<?> desc = WindowDesc.of(((Window<?>) (count.getWindow().get())));
        Assert.assertEquals(windowing, desc.getWindowFn());
        Assert.assertEquals(trigger, desc.getTrigger());
        Assert.assertEquals(DISCARDING_FIRED_PANES, desc.getAccumulationMode());
    }

    @Test
    public void testBuildTypePropagation() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final TypeDescriptor<String> keyType = TypeDescriptors.strings();
        final PCollection<KV<String, Long>> counted = CountByKey.named("CountByKey1").of(dataset).keyBy(( s) -> s, keyType).output();
        final CountByKey count = ((CountByKey) (TestUtils.getProducer(counted)));
        Assert.assertTrue(count.getKeyType().isPresent());
        Assert.assertEquals(count.getKeyType().get(), keyType);
        Assert.assertTrue(count.getOutputType().isPresent());
        Assert.assertEquals(TypeDescriptors.kvs(keyType, TypeDescriptors.longs()), count.getOutputType().get());
    }
}

