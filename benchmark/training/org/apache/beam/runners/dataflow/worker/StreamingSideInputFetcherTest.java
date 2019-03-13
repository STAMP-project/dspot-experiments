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


import SideInputState.KNOWN_READY;
import SideInputState.UNKNOWN;
import StreamingModeExecutionContext.StepContext;
import Windmill.GlobalDataId;
import java.util.Arrays;
import java.util.Set;
import org.apache.beam.runners.core.InMemoryStateInternals;
import org.apache.beam.runners.core.SideInputReader;
import org.apache.beam.runners.core.TimerInternals.TimerData;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill;
import org.apache.beam.sdk.state.BagState;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Tests for {@link StreamingSideInputFetcher}.
 */
@RunWith(JUnit4.class)
public class StreamingSideInputFetcherTest {
    private static final FixedWindows WINDOW_FN = FixedWindows.of(Duration.millis(10));

    static TupleTag<String> mainOutputTag = new TupleTag();

    @Mock
    StreamingModeExecutionContext execContext;

    @Mock
    StepContext stepContext;

    @Mock
    SideInputReader mockSideInputReader;

    private final InMemoryStateInternals<String> state = InMemoryStateInternals.forKey("dummyKey");

    @Test
    public void testStoreIfBlocked() throws Exception {
        PCollectionView<String> view = createView();
        IntervalWindow readyWindow = createWindow(0);
        Windmill.GlobalDataId id = GlobalDataId.newBuilder().setTag(view.getTagInternal().getId()).setVersion(ByteString.copyFrom(CoderUtils.encodeToByteArray(IntervalWindow.getCoder(), readyWindow))).build();
        Mockito.when(stepContext.getSideInputNotifications()).thenReturn(Arrays.<Windmill.GlobalDataId>asList(id));
        Mockito.when(stepContext.issueSideInputFetch(ArgumentMatchers.eq(view), ArgumentMatchers.any(BoundedWindow.class), ArgumentMatchers.eq(UNKNOWN))).thenReturn(false);
        Mockito.when(stepContext.issueSideInputFetch(ArgumentMatchers.eq(view), ArgumentMatchers.any(BoundedWindow.class), ArgumentMatchers.eq(KNOWN_READY))).thenReturn(true);
        StreamingSideInputFetcher<String, IntervalWindow> fetcher = createFetcher(Arrays.asList(view));
        // Verify storeIfBlocked
        WindowedValue<String> datum1 = createDatum("e1", 0);
        Assert.assertTrue(fetcher.storeIfBlocked(datum1));
        Assert.assertThat(fetcher.getBlockedWindows(), Matchers.contains(createWindow(0)));
        WindowedValue<String> datum2 = createDatum("e2", 0);
        Assert.assertTrue(fetcher.storeIfBlocked(datum2));
        Assert.assertThat(fetcher.getBlockedWindows(), Matchers.contains(createWindow(0)));
        WindowedValue<String> datum3 = createDatum("e3", 10);
        Assert.assertTrue(fetcher.storeIfBlocked(datum3));
        Assert.assertThat(fetcher.getBlockedWindows(), Matchers.containsInAnyOrder(createWindow(0), createWindow(10)));
        TimerData timer1 = createTimer(0);
        Assert.assertTrue(fetcher.storeIfBlocked(timer1));
        TimerData timer2 = createTimer(15);
        Assert.assertTrue(fetcher.storeIfBlocked(timer2));
        // Verify ready windows
        Assert.assertThat(fetcher.getReadyWindows(), Matchers.contains(readyWindow));
        Set<WindowedValue<String>> actualElements = Sets.newHashSet();
        for (BagState<WindowedValue<String>> bag : fetcher.prefetchElements(ImmutableList.of(readyWindow))) {
            for (WindowedValue<String> elem : bag.read()) {
                actualElements.add(elem);
            }
            bag.clear();
        }
        Assert.assertThat(actualElements, Matchers.containsInAnyOrder(datum1, datum2));
        Set<TimerData> actualTimers = Sets.newHashSet();
        for (BagState<TimerData> bag : fetcher.prefetchTimers(ImmutableList.of(readyWindow))) {
            for (TimerData timer : bag.read()) {
                actualTimers.add(timer);
            }
            bag.clear();
        }
        Assert.assertThat(actualTimers, Matchers.contains(timer1));
        // Verify releaseBlockedWindows
        fetcher.releaseBlockedWindows(ImmutableList.of(readyWindow));
        Assert.assertThat(fetcher.getBlockedWindows(), Matchers.contains(createWindow(10)));
        // Verify rest elements and timers
        Set<WindowedValue<String>> restElements = Sets.newHashSet();
        for (BagState<WindowedValue<String>> bag : fetcher.prefetchElements(ImmutableList.of(createWindow(10), createWindow(15)))) {
            for (WindowedValue<String> elem : bag.read()) {
                restElements.add(elem);
            }
        }
        Assert.assertThat(restElements, Matchers.contains(datum3));
        Set<TimerData> restTimers = Sets.newHashSet();
        for (BagState<TimerData> bag : fetcher.prefetchTimers(ImmutableList.of(createWindow(10), createWindow(15)))) {
            for (TimerData timer : bag.read()) {
                restTimers.add(timer);
            }
        }
        Assert.assertThat(restTimers, Matchers.contains(timer2));
    }
}

