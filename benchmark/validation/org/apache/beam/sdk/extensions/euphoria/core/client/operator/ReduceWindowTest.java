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
import java.util.stream.Stream;
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
 * Test behavior of operator {@code ReduceWindow}.
 */
public class ReduceWindowTest {
    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleBuild() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<Long> output = ReduceWindow.of(dataset).valueBy(( e) -> "").reduceBy(( e) -> 1L).output();
        final ReduceWindow rw = ((ReduceWindow) (TestUtils.getProducer(output)));
        Assert.assertEquals(1L, ((long) (collectSingle(rw.getReducer(), Stream.of("blah")))));
        Assert.assertEquals("", rw.getValueExtractor().apply("blah"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleBuildWithoutValue() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<Long> output = ReduceWindow.of(dataset).reduceBy(( e) -> 1L).windowBy(FixedWindows.of(Duration.standardHours(1))).triggeredBy(DefaultTrigger.of()).discardingFiredPanes().withAllowedLateness(Duration.millis(1000)).output();
        final ReduceWindow rw = ((ReduceWindow) (TestUtils.getProducer(output)));
        Assert.assertEquals(1L, ((long) (collectSingle(rw.getReducer(), Stream.of("blah")))));
        Assert.assertEquals("blah", rw.getValueExtractor().apply("blah"));
        Assert.assertTrue(rw.getWindow().isPresent());
        @SuppressWarnings("unchecked")
        final WindowDesc<?> windowDesc = WindowDesc.of(((Window) (rw.getWindow().get())));
        Assert.assertNotNull(windowDesc);
        Assert.assertEquals(FixedWindows.of(Duration.standardHours(1)), windowDesc.getWindowFn());
        Assert.assertEquals(DefaultTrigger.of(), windowDesc.getTrigger());
        Assert.assertEquals(Duration.millis(1000), windowDesc.getAllowedLateness());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSimpleBuildWithValueSorted() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<Long> output = ReduceWindow.of(dataset).reduceBy(( e) -> 1L).withSortedValues(String::compareTo).windowBy(FixedWindows.of(Duration.standardHours(1))).triggeredBy(DefaultTrigger.of()).accumulationMode(DISCARDING_FIRED_PANES).output();
        final ReduceWindow rw = ((ReduceWindow) (TestUtils.getProducer(output)));
        Assert.assertTrue(rw.getValueComparator().isPresent());
    }

    @Test
    public void testWindow_applyIf() {
        final PCollection<String> dataset = TestUtils.createMockDataset(TypeDescriptors.strings());
        final PCollection<Long> output = ReduceWindow.of(dataset).reduceBy(( e) -> 1L).withSortedValues(String::compareTo).applyIf(true, ( b) -> b.windowBy(FixedWindows.of(org.joda.time.Duration.standardHours(1))).triggeredBy(DefaultTrigger.of()).discardingFiredPanes()).output();
        final ReduceWindow rw = ((ReduceWindow) (TestUtils.getProducer(output)));
        Assert.assertTrue(rw.getWindow().isPresent());
        @SuppressWarnings("unchecked")
        final WindowDesc<?> windowDesc = WindowDesc.of(((Window) (rw.getWindow().get())));
        Assert.assertEquals(FixedWindows.of(Duration.standardHours(1)), windowDesc.getWindowFn());
        Assert.assertEquals(DefaultTrigger.of(), windowDesc.getTrigger());
        Assert.assertEquals(DISCARDING_FIRED_PANES, windowDesc.getAccumulationMode());
    }
}

