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
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test operator Distinct.
 */
public class DistinctTest {
    @Test
    public void testBuild() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final FixedWindows windowing = FixedWindows.of(Duration.standardHours(1));
        final DefaultTrigger trigger = DefaultTrigger.of();
        final PCollection<String> uniq = Distinct.named("Distinct1").of(dataset).windowBy(windowing).triggeredBy(trigger).discardingFiredPanes().withAllowedLateness(Duration.millis(1000)).output();
        final Distinct distinct = ((Distinct) (TestUtils.getProducer(uniq)));
        Assert.assertTrue(distinct.getName().isPresent());
        Assert.assertEquals("Distinct1", distinct.getName().get());
        Assert.assertTrue(distinct.getWindow().isPresent());
        @SuppressWarnings("unchecked")
        final WindowDesc<?> windowDesc = WindowDesc.of(((Window) (distinct.getWindow().get())));
        Assert.assertEquals(windowing, windowDesc.getWindowFn());
        Assert.assertEquals(trigger, windowDesc.getTrigger());
        Assert.assertEquals(DISCARDING_FIRED_PANES, windowDesc.getAccumulationMode());
        Assert.assertEquals(Duration.millis(1000), windowDesc.getAllowedLateness());
    }

    @Test
    public void testBuild_ImplicitName() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<String> uniq = Distinct.of(dataset).output();
        final Distinct distinct = ((Distinct) (TestUtils.getProducer(uniq)));
        Assert.assertFalse(distinct.getName().isPresent());
    }

    @Test
    public void testBuild_Windowing() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<String> uniq = Distinct.of(dataset).windowBy(FixedWindows.of(Duration.standardHours(1))).triggeredBy(DefaultTrigger.of()).accumulationMode(DISCARDING_FIRED_PANES).output();
        final Distinct distinct = ((Distinct) (TestUtils.getProducer(uniq)));
        Assert.assertTrue(distinct.getWindow().isPresent());
        @SuppressWarnings("unchecked")
        final WindowDesc<?> windowDesc = WindowDesc.of(((Window) (distinct.getWindow().get())));
        Assert.assertEquals(FixedWindows.of(Duration.standardHours(1)), windowDesc.getWindowFn());
        Assert.assertEquals(DefaultTrigger.of(), windowDesc.getTrigger());
    }

    @Test
    public void testWindow_applyIf() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<String> uniq = Distinct.of(dataset).applyIf(true, ( b) -> b.windowBy(FixedWindows.of(Duration.standardHours(1))).triggeredBy(DefaultTrigger.of()).discardingFiredPanes()).output();
        final Distinct distinct = ((Distinct) (TestUtils.getProducer(uniq)));
        Assert.assertTrue(distinct.getWindow().isPresent());
        @SuppressWarnings("unchecked")
        final WindowDesc<?> windowDesc = WindowDesc.of(((Window) (distinct.getWindow().get())));
        Assert.assertEquals(FixedWindows.of(Duration.standardHours(1)), windowDesc.getWindowFn());
        Assert.assertEquals(DefaultTrigger.of(), windowDesc.getTrigger());
        Assert.assertEquals(DISCARDING_FIRED_PANES, windowDesc.getAccumulationMode());
    }
}

