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
package org.apache.beam.sdk.extensions.euphoria.core.testkit;


import Distinct.SelectionPolicy.NEWEST;
import Distinct.SelectionPolicy.OLDEST;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.extensions.euphoria.core.client.operator.AssignEventTime;
import org.apache.beam.sdk.extensions.euphoria.core.client.operator.Distinct;
import org.apache.beam.sdk.extensions.euphoria.core.client.operator.MapElements;
import org.apache.beam.sdk.transforms.windowing.DefaultTrigger;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptor;
import org.apache.beam.sdk.values.TypeDescriptors;
import org.joda.time.Duration;
import org.junit.Test;


/**
 * Test for the {@link Distinct} operator.
 */
public class DistinctTest extends AbstractOperatorTest {
    /**
     * Test simple duplicates.
     */
    @Test
    public void testSimpleDuplicatesWithNoWindowing() {
        execute(new AbstractOperatorTest.AbstractTestCase<Integer, Integer>() {
            @Override
            public List<Integer> getUnorderedOutput() {
                return Arrays.asList(1, 2, 3);
            }

            @Override
            protected PCollection<Integer> getOutput(PCollection<Integer> input) {
                return Distinct.of(input).output();
            }

            @Override
            protected List<Integer> getInput() {
                return Arrays.asList(1, 2, 3, 3, 2, 1);
            }

            @Override
            protected TypeDescriptor<Integer> getInputType() {
                return TypeDescriptors.integers();
            }
        });
    }

    /**
     * Test simple duplicates with unbounded input with count window.
     */
    @Test
    public void testSimpleDuplicatesWithTimeWindowing() {
        execute(new AbstractOperatorTest.AbstractTestCase<KV<Integer, Long>, Integer>() {
            @Override
            public List<Integer> getUnorderedOutput() {
                return Arrays.asList(1, 2, 3, 2, 1);
            }

            @Override
            protected PCollection<Integer> getOutput(PCollection<KV<Integer, Long>> input) {
                input = AssignEventTime.of(input).using(KV::getValue).output();
                PCollection<KV<Integer, Long>> distinct = Distinct.of(input).projected(KV::getKey).windowBy(FixedWindows.of(Duration.standardSeconds(1))).triggeredBy(DefaultTrigger.of()).discardingFiredPanes().output();
                return MapElements.of(distinct).using(KV::getKey).output();
            }

            @Override
            protected List<KV<Integer, Long>> getInput() {
                return // first window
                // second window
                Arrays.asList(KV.of(1, 100L), KV.of(2, 300L), KV.of(3, 1200L), KV.of(3, 1500L), KV.of(2, 2200L), KV.of(1, 2700L));
            }

            @Override
            protected TypeDescriptor<KV<Integer, Long>> getInputType() {
                return TypeDescriptors.kvs(TypeDescriptors.integers(), TypeDescriptors.longs());
            }
        });
    }

    @Test
    public void testSimpleDuplicatesWithStream() {
        execute(new AbstractOperatorTest.AbstractTestCase<KV<Integer, Long>, Integer>() {
            @Override
            public List<Integer> getUnorderedOutput() {
                return Arrays.asList(2, 1, 3);
            }

            @Override
            protected PCollection<Integer> getOutput(PCollection<KV<Integer, Long>> input) {
                input = AssignEventTime.of(input).using(KV::getValue).output();
                PCollection<KV<Integer, Long>> distinct = Distinct.of(input).projected(KV::getKey).windowBy(FixedWindows.of(Duration.standardSeconds(1))).triggeredBy(DefaultTrigger.of()).discardingFiredPanes().output();
                return MapElements.of(distinct).using(KV::getKey).output();
            }

            @Override
            protected List<KV<Integer, Long>> getInput() {
                List<KV<Integer, Long>> first = asTimedList(100, 1, 2, 3, 3, 2, 1);
                first.addAll(asTimedList(100, 1, 2, 3, 3, 2, 1));
                return first;
            }

            @Override
            protected TypeDescriptor<KV<Integer, Long>> getInputType() {
                return TypeDescriptors.kvs(TypeDescriptors.integers(), TypeDescriptors.longs());
            }
        });
    }

    @Test
    public void testSimpleDuplicatesWithStreamStrategyOldest() {
        execute(new AbstractOperatorTest.AbstractTestCase<KV<String, Long>, String>() {
            @Override
            public List<String> getUnorderedOutput() {
                return Arrays.asList("2", "1", "3");
            }

            @Override
            protected PCollection<String> getOutput(PCollection<KV<String, Long>> input) {
                input = AssignEventTime.of(input).using(KV::getValue).output();
                PCollection<KV<String, Long>> distinct = Distinct.of(input).projected(( in) -> in.getKey().substring(0, 1), OLDEST, TypeDescriptors.strings()).windowBy(FixedWindows.of(Duration.standardSeconds(1))).triggeredBy(DefaultTrigger.of()).discardingFiredPanes().output();
                return MapElements.of(distinct).using(KV::getKey).output();
            }

            @Override
            protected List<KV<String, Long>> getInput() {
                return asTimedList(100, "1", "2", "3", "3.", "2.", "1.");
            }

            @Override
            protected TypeDescriptor<KV<String, Long>> getInputType() {
                return TypeDescriptors.kvs(TypeDescriptors.strings(), TypeDescriptors.longs());
            }
        });
    }

    @Test
    public void testSimpleDuplicatesWithStreamStrategyNewest() {
        execute(new AbstractOperatorTest.AbstractTestCase<KV<String, Long>, String>() {
            @Override
            public List<String> getUnorderedOutput() {
                return Arrays.asList("2.", "1.", "3.");
            }

            @Override
            protected PCollection<String> getOutput(PCollection<KV<String, Long>> input) {
                input = AssignEventTime.of(input).using(KV::getValue).output();
                PCollection<KV<String, Long>> distinct = Distinct.of(input).projected(( in) -> in.getKey().substring(0, 1), NEWEST, TypeDescriptors.strings()).windowBy(FixedWindows.of(Duration.standardSeconds(1))).triggeredBy(DefaultTrigger.of()).discardingFiredPanes().output();
                return MapElements.of(distinct).using(KV::getKey).output();
            }

            @Override
            protected List<KV<String, Long>> getInput() {
                return asTimedList(100, "1", "2", "3", "3.", "2.", "1.");
            }

            @Override
            protected TypeDescriptor<KV<String, Long>> getInputType() {
                return TypeDescriptors.kvs(TypeDescriptors.strings(), TypeDescriptors.longs());
            }
        });
    }
}

