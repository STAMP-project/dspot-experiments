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
package org.apache.beam.runners.dataflow.worker;


import org.apache.beam.runners.dataflow.worker.counters.Counter;
import org.apache.beam.runners.dataflow.worker.counters.Counter.CounterUpdateExtractor;
import org.apache.beam.runners.dataflow.worker.counters.CounterName;
import org.apache.beam.runners.dataflow.worker.counters.CounterSet;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;


/**
 * Tests for ApplianceShuffleCounters.
 */
@RunWith(JUnit4.class)
public class ApplianceShuffleCountersTest {
    private static final String DATASET_ID = "dataset";

    private CounterSet counterSet = new CounterSet();

    private ApplianceShuffleCounters counters;

    private CounterUpdateExtractor<?> mockUpdateExtractor = Mockito.mock(CounterUpdateExtractor.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testSingleCounter() throws Exception {
        String[] names = new String[]{ "sum_counter" };
        String[] kinds = new String[]{ "sum" };
        long[] deltas = new long[]{ 122 };
        counters.importCounters(names, kinds, deltas);
        counterSet.extractUpdates(false, mockUpdateExtractor);
        Mockito.verify(mockUpdateExtractor).longSum(CounterName.named("stageName-systemName-dataset-sum_counter"), false, 122L);
        Mockito.verifyNoMoreInteractions(mockUpdateExtractor);
    }

    @Test
    public void testSingleCounterMultipleDeltas() throws Exception {
        String[] names = new String[]{ "sum_counter", "sum_counter" };
        String[] kinds = new String[]{ "sum", "sum" };
        long[] deltas = new long[]{ 122, 78 };
        counters.importCounters(names, kinds, deltas);
        counterSet.extractUpdates(false, mockUpdateExtractor);
        Mockito.verify(mockUpdateExtractor).longSum(CounterName.named("stageName-systemName-dataset-sum_counter"), false, 200L);
        Mockito.verifyNoMoreInteractions(mockUpdateExtractor);
    }

    @Test
    public void testMultipleCounters() throws Exception {
        String[] names = new String[]{ "sum_counter", "max_counter", "min_counter" };
        String[] kinds = new String[]{ "sum", "max", "min" };
        long[] deltas = new long[]{ 100, 200, 300 };
        counters.importCounters(names, kinds, deltas);
        Counter<Long, Long> sumCounter = getCounter("sum_counter");
        Assert.assertNotNull(sumCounter);
        counterSet.extractUpdates(false, mockUpdateExtractor);
        Mockito.verify(mockUpdateExtractor).longSum(CounterName.named("stageName-systemName-dataset-sum_counter"), false, 100L);
        Mockito.verify(mockUpdateExtractor).longMax(CounterName.named("stageName-systemName-dataset-max_counter"), false, 200L);
        Mockito.verify(mockUpdateExtractor).longMin(CounterName.named("stageName-systemName-dataset-min_counter"), false, 300L);
        Mockito.verifyNoMoreInteractions(mockUpdateExtractor);
    }

    @Test
    public void testSinglePreexistingCounter() throws Exception {
        Counter<Long, Long> sumCounter = counterSet.longSum(CounterName.named("stageName-systemName-dataset-sum_counter"));
        sumCounter.addValue(1000L);
        String[] names = new String[]{ "sum_counter" };
        String[] kinds = new String[]{ "sum" };
        long[] deltas = new long[]{ 122 };
        counters.importCounters(names, kinds, deltas);
        counterSet.extractUpdates(false, mockUpdateExtractor);
        Mockito.verify(mockUpdateExtractor).longSum(CounterName.named("stageName-systemName-dataset-sum_counter"), false, 1122L);
        Mockito.verifyNoMoreInteractions(mockUpdateExtractor);
    }

    @Test
    public void testArrayLengthMismatch() throws Exception {
        String[] names = new String[]{ "sum_counter" };
        String[] kinds = new String[]{ "sum", "max" };
        long[] deltas = new long[]{ 122 };
        try {
            counters.importCounters(names, kinds, deltas);
        } catch (AssertionError e) {
            // expected
        }
    }

    @Test
    public void testUnsupportedKind() throws Exception {
        String[] names = new String[]{ "sum_counter" };
        String[] kinds = new String[]{ "sum_int" };
        long[] deltas = new long[]{ 122 };
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("sum_int");
        counters.importCounters(names, kinds, deltas);
    }
}

