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
import Join.Type.FULL;
import Join.Type.INNER;
import Join.Type.LEFT;
import Join.Type.RIGHT;
import java.util.Optional;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.extensions.euphoria.core.client.io.Collector;
import org.apache.beam.sdk.extensions.euphoria.core.client.type.TypePropagationAssert;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.windowing.AfterWatermark;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.beam.sdk.transforms.windowing.WindowDesc;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * Test operator Join.
 */
public class JoinTest {
    @Rule
    public TestPipeline pipeline = TestPipeline.create();

    @Test
    public void testBuild() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<KV<Integer, String>> joined = Join.named("Join1").of(left, right).by(String::length, String::length).using((String l,String r,Collector<String> c) -> {
            // no-op
        }).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        Assert.assertTrue(join.getName().isPresent());
        Assert.assertEquals("Join1", join.getName().get());
        Assert.assertNotNull(join.getLeftKeyExtractor());
        Assert.assertNotNull(join.getRightKeyExtractor());
        Assert.assertFalse(join.getWindow().isPresent());
        Assert.assertEquals(INNER, join.getType());
    }

    @Test
    public void testBuild_OutputValues() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> joined = Join.named("JoinValues").of(left, right).by(String::length, String::length).using((String l,String r,Collector<String> c) -> {
            // no-op
        }).outputValues();
        final OutputValues outputValues = ((OutputValues) (TestUtils.getProducer(joined)));
        Assert.assertTrue(outputValues.getName().isPresent());
        Assert.assertEquals("JoinValues", outputValues.getName().get());
    }

    @Test
    public void testBuild_WithCounters() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<KV<Integer, String>> joined = Join.named("Join1").of(left, right).by(String::length, String::length).using((String l,String r,Collector<String> c) -> {
            c.getCounter("my-counter").increment();
            c.collect((l + r));
        }).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        Assert.assertTrue(join.getName().isPresent());
        Assert.assertEquals("Join1", join.getName().get());
        Assert.assertNotNull(join.getLeftKeyExtractor());
        Assert.assertNotNull(join.getRightKeyExtractor());
        Assert.assertFalse(join.getWindow().isPresent());
        Assert.assertEquals(INNER, join.getType());
    }

    @Test
    public void testBuild_ImplicitName() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<KV<Integer, String>> joined = Join.of(left, right).by(String::length, String::length).using((String l,String r,Collector<String> c) -> {
            // no-op
        }).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        Assert.assertFalse(join.getName().isPresent());
    }

    @Test
    public void testBuild_LeftJoin() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<KV<Integer, String>> joined = LeftJoin.named("Join1").of(left, right).by(String::length, String::length).using((String l,Optional<String> r,Collector<String> c) -> {
            // no-op
        }).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        Assert.assertEquals(LEFT, join.getType());
    }

    @Test
    public void testBuild_RightJoin() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<KV<Integer, String>> joined = RightJoin.named("Join1").of(left, right).by(String::length, String::length).using((Optional<String> l,String r,Collector<String> c) -> {
            // no-op
        }).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        Assert.assertEquals(RIGHT, join.getType());
    }

    @Test
    public void testBuild_FullJoin() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<KV<Integer, String>> joined = FullJoin.named("Join1").of(left, right).by(String::length, String::length).using((Optional<String> l,Optional<String> r,Collector<String> c) -> c.collect(((l.orElse(null)) + (r.orElse(null))))).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        Assert.assertEquals(FULL, join.getType());
    }

    @Test
    public void testBuild_Windowing() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<KV<Integer, String>> joined = Join.named("Join1").of(left, right).by(String::length, String::length).using((String l,String r,Collector<String> c) -> c.collect((l + r))).windowBy(FixedWindows.of(Duration.standardHours(1))).triggeredBy(AfterWatermark.pastEndOfWindow()).discardingFiredPanes().withAllowedLateness(Duration.millis(1000)).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        Assert.assertTrue(join.getWindow().isPresent());
        @SuppressWarnings("unchecked")
        final WindowDesc<?> windowDesc = WindowDesc.of(((Window) (join.getWindow().get())));
        Assert.assertEquals(FixedWindows.of(Duration.standardHours(1)), windowDesc.getWindowFn());
        Assert.assertEquals(AfterWatermark.pastEndOfWindow(), windowDesc.getTrigger());
        Assert.assertEquals(DISCARDING_FIRED_PANES, windowDesc.getAccumulationMode());
        Assert.assertEquals(Duration.millis(1000), windowDesc.getAllowedLateness());
    }

    @Test
    public void testBuild_OptionalWindowing() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<KV<Integer, String>> joined = Join.named("Join1").of(left, right).by(String::length, String::length).using((String l,String r,Collector<String> c) -> c.collect((l + r))).applyIf(true, ( b) -> b.windowBy(FixedWindows.of(org.joda.time.Duration.standardHours(1))).triggeredBy(AfterWatermark.pastEndOfWindow()).accumulationMode(AccumulationMode.DISCARDING_FIRED_PANES)).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        Assert.assertTrue(join.getWindow().isPresent());
        final Window<?> window = ((Window) (join.getWindow().get()));
        Assert.assertEquals(FixedWindows.of(Duration.standardHours(1)), window.getWindowFn());
        Assert.assertEquals(AfterWatermark.pastEndOfWindow(), WindowDesc.of(window).getTrigger());
        Assert.assertEquals(DISCARDING_FIRED_PANES, WindowDesc.of(window).getAccumulationMode());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuildTypePropagation() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final TypeDescriptor<Integer> keyType = TypeDescriptors.integers();
        final TypeDescriptor<String> outputType = TypeDescriptors.strings();
        final PCollection<KV<Integer, String>> joined = Join.named("Join1").of(left, right).by(String::length, String::length, keyType).using((String l,String r,Collector<String> c) -> {
            // no-op
        }, outputType).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        TypePropagationAssert.assertOperatorTypeAwareness(join, keyType, outputType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild_LeftJoinTypePropagation() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        TypeDescriptor<Integer> keyType = TypeDescriptors.integers();
        TypeDescriptor<String> outputType = TypeDescriptors.strings();
        final PCollection<KV<Integer, String>> joined = LeftJoin.named("Join1").of(left, right).by(String::length, String::length, keyType).using((String l,Optional<String> r,Collector<String> c) -> {
            // no-op
        }, outputType).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        TypePropagationAssert.assertOperatorTypeAwareness(join, keyType, outputType);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBuild_RightJoinTypePropagation() {
        final Pipeline pipeline = TestUtils.createTestPipeline();
        final PCollection<String> left = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final PCollection<String> right = TestUtils.createMockDataset(pipeline, TypeDescriptors.strings());
        final TypeDescriptor<Integer> keyType = TypeDescriptors.integers();
        final TypeDescriptor<String> outputType = TypeDescriptors.strings();
        final PCollection<KV<Integer, String>> joined = RightJoin.named("Join1").of(left, right).by(String::length, String::length, keyType).using((Optional<String> l,String r,Collector<String> c) -> {
            // no-op
        }, outputType).output();
        final Join join = ((Join) (TestUtils.getProducer(joined)));
        TypePropagationAssert.assertOperatorTypeAwareness(join, keyType, outputType);
    }
}

