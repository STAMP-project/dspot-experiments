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


import StreamingModeExecutionContext.StepContext;
import Timer.Type.WATERMARK;
import java.util.List;
import java.util.Set;
import org.apache.beam.runners.core.InMemoryStateInternals;
import org.apache.beam.runners.core.KeyedWorkItem;
import org.apache.beam.runners.core.KeyedWorkItems;
import org.apache.beam.runners.core.SideInputReader;
import org.apache.beam.runners.core.TimerInternals;
import org.apache.beam.runners.core.TimerInternals.TimerData;
import org.apache.beam.runners.dataflow.worker.util.ListOutputManager;
import org.apache.beam.sdk.state.BagState;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link StreamingKeyedWorkItemSideInputDoFnRunner}.
 */
@RunWith(JUnit4.class)
public class StreamingKeyedWorkItemSideInputDoFnRunnerTest {
    private static final FixedWindows WINDOW_FN = FixedWindows.of(Duration.millis(10));

    private static TupleTag<KV<String, Integer>> mainOutputTag = new TupleTag();

    private final InMemoryStateInternals<String> state = InMemoryStateInternals.forKey("dummyKey");

    @Mock
    private StepContext stepContext;

    @Mock
    private StreamingSideInputFetcher<Integer, IntervalWindow> sideInputFetcher;

    @Mock
    private SideInputReader mockSideInputReader;

    @Mock
    private TimerInternals mockTimerInternals;

    @Mock
    private BagState<WindowedValue<Integer>> elemsBag;

    @Mock
    private BagState<TimerData> timersBag;

    @Test
    public void testInvokeProcessElement() throws Exception {
        Mockito.when(sideInputFetcher.storeIfBlocked(Matchers.<WindowedValue<Integer>>any())).thenReturn(false, true, false).thenThrow(new RuntimeException("Does not expect more calls"));
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(15L));
        ListOutputManager outputManager = new ListOutputManager();
        StreamingKeyedWorkItemSideInputDoFnRunner<String, Integer, KV<String, Integer>, IntervalWindow> runner = createRunner(outputManager);
        KeyedWorkItem<String, Integer> elemsWorkItem = KeyedWorkItems.elementsWorkItem("a", // side inputs non-ready element
        ImmutableList.of(createDatum(13, 13L), createDatum(16, 16L), createDatum(18, 18L)));
        runner.processElement(new org.apache.beam.runners.dataflow.worker.util.ValueInEmptyWindows(elemsWorkItem));
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(20));
        runner.processElement(new org.apache.beam.runners.dataflow.worker.util.ValueInEmptyWindows(KeyedWorkItems.<String, Integer>timersWorkItem("a", ImmutableList.of(timerData(window(10, 20), new Instant(19), WATERMARK)))));
        List<WindowedValue<KV<String, Integer>>> result = outputManager.getOutput(StreamingKeyedWorkItemSideInputDoFnRunnerTest.mainOutputTag);
        Assert.assertEquals(1, result.size());
        WindowedValue<KV<String, Integer>> item0 = result.get(0);
        Assert.assertEquals("a", item0.getValue().getKey());
        Assert.assertEquals(31, item0.getValue().getValue().intValue());
        Assert.assertEquals("a", runner.keyValue().read());
    }

    @Test
    public void testStartBundle() throws Exception {
        ListOutputManager outputManager = new ListOutputManager();
        StreamingKeyedWorkItemSideInputDoFnRunner<String, Integer, KV<String, Integer>, IntervalWindow> runner = createRunner(outputManager);
        runner.keyValue().write("a");
        Set<IntervalWindow> readyWindows = ImmutableSet.of(window(10, 20));
        Mockito.when(sideInputFetcher.getReadyWindows()).thenReturn(readyWindows);
        Mockito.when(sideInputFetcher.prefetchElements(readyWindows)).thenReturn(ImmutableList.of(elemsBag));
        Mockito.when(sideInputFetcher.prefetchTimers(readyWindows)).thenReturn(ImmutableList.of(timersBag));
        Mockito.when(elemsBag.read()).thenReturn(ImmutableList.of(createDatum(13, 13L), createDatum(18, 18L)));
        Mockito.when(timersBag.read()).thenReturn(ImmutableList.of(timerData(window(10, 20), new Instant(19), WATERMARK)));
        Mockito.when(mockTimerInternals.currentInputWatermarkTime()).thenReturn(new Instant(20));
        runner.startBundle();
        List<WindowedValue<KV<String, Integer>>> result = outputManager.getOutput(StreamingKeyedWorkItemSideInputDoFnRunnerTest.mainOutputTag);
        Assert.assertEquals(1, result.size());
        WindowedValue<KV<String, Integer>> item0 = result.get(0);
        Assert.assertEquals("a", item0.getValue().getKey());
        Assert.assertEquals(31, item0.getValue().getValue().intValue());
    }
}

