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
package org.apache.beam.runners.core;


import LateDataDroppingDoFnRunner.DROPPED_DUE_TO_LATENESS;
import org.apache.beam.runners.core.LateDataDroppingDoFnRunner.LateDataFilter;
import org.apache.beam.runners.core.metrics.MetricsContainerImpl;
import org.apache.beam.sdk.metrics.MetricName;
import org.apache.beam.sdk.metrics.MetricsEnvironment;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link LateDataDroppingDoFnRunner}.
 */
@RunWith(JUnit4.class)
public class LateDataDroppingDoFnRunnerTest {
    private static final FixedWindows WINDOW_FN = FixedWindows.of(Duration.millis(10));

    @Mock
    private TimerInternals mockTimerInternals;

    @Test
    public void testLateDataFilter() throws Exception {
        MetricsContainerImpl container = new MetricsContainerImpl("any");
        MetricsEnvironment.setCurrentContainer(container);
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(15L));
        LateDataFilter lateDataFilter = new LateDataFilter(WindowingStrategy.of(LateDataDroppingDoFnRunnerTest.WINDOW_FN), mockTimerInternals);
        Iterable<WindowedValue<Integer>> actual = lateDataFilter.filter("a", // late element, earlier than 4L.
        ImmutableList.of(createDatum(13, 13L), createDatum(5, 5L), createDatum(16, 16L), createDatum(18, 18L)));
        Iterable<WindowedValue<Integer>> expected = ImmutableList.of(createDatum(13, 13L), createDatum(16, 16L), createDatum(18, 18L));
        Assert.assertThat(expected, Matchers.containsInAnyOrder(Iterables.toArray(actual, WindowedValue.class)));
        long droppedValues = container.getCounter(MetricName.named(LateDataDroppingDoFnRunner.class, DROPPED_DUE_TO_LATENESS)).getCumulative();
        Assert.assertEquals(1, droppedValues);
        // Ensure that reiterating returns the same results and doesn't increment the counter again.
        Assert.assertThat(expected, Matchers.containsInAnyOrder(Iterables.toArray(actual, WindowedValue.class)));
        droppedValues = container.getCounter(MetricName.named(LateDataDroppingDoFnRunner.class, DROPPED_DUE_TO_LATENESS)).getCumulative();
        Assert.assertEquals(1, droppedValues);
    }
}

