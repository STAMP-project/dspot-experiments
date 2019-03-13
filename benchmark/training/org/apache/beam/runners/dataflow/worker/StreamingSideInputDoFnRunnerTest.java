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


import PaneInfo.NO_FIRING;
import SideInputState.KNOWN_READY;
import SideInputState.UNKNOWN;
import StreamingModeExecutionContext.StepContext;
import Windmill.GlobalDataId;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.beam.runners.core.InMemoryStateInternals;
import org.apache.beam.runners.core.SideInputReader;
import org.apache.beam.runners.core.StateNamespaces;
import org.apache.beam.runners.dataflow.worker.StateFetcher.SideInputState;
import org.apache.beam.runners.dataflow.worker.util.ListOutputManager;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill;
import org.apache.beam.runners.dataflow.worker.windmill.Windmill.GlobalDataRequest;
import org.apache.beam.sdk.state.ValueState;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.SlidingWindows;
import org.apache.beam.sdk.util.CoderUtils;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.vendor.grpc.v1p13p1.com.google.protobuf.ByteString;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Iterables;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


/**
 * Unit tests for {@link StreamingSideInputDoFnRunner}.
 */
@RunWith(JUnit4.class)
public class StreamingSideInputDoFnRunnerTest {
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
    public void testSideInputReady() throws Exception {
        PCollectionView<String> view = createView();
        Mockito.when(stepContext.getSideInputNotifications()).thenReturn(Arrays.<Windmill.GlobalDataId>asList());
        Mockito.when(stepContext.issueSideInputFetch(ArgumentMatchers.eq(view), ArgumentMatchers.any(BoundedWindow.class), ArgumentMatchers.eq(UNKNOWN))).thenReturn(true);
        Mockito.when(execContext.getSideInputReaderForViews(Mockito.<Iterable<? extends PCollectionView<?>>>any())).thenReturn(mockSideInputReader);
        Mockito.when(mockSideInputReader.contains(ArgumentMatchers.eq(view))).thenReturn(true);
        Mockito.when(mockSideInputReader.get(ArgumentMatchers.eq(view), ArgumentMatchers.any(BoundedWindow.class))).thenReturn("data");
        ListOutputManager outputManager = new ListOutputManager();
        List<PCollectionView<String>> views = Arrays.asList(view);
        StreamingSideInputFetcher<String, IntervalWindow> sideInputFetcher = createFetcher(views);
        StreamingSideInputDoFnRunner<String, String, IntervalWindow> runner = createRunner(outputManager, views, sideInputFetcher);
        runner.startBundle();
        runner.processElement(createDatum("e", 0));
        runner.finishBundle();
        Assert.assertThat(outputManager.getOutput(StreamingSideInputDoFnRunnerTest.mainOutputTag), Matchers.contains(createDatum("e:data", 0)));
    }

    @Test
    public void testSideInputNotReady() throws Exception {
        PCollectionView<String> view = createView();
        Mockito.when(stepContext.getSideInputNotifications()).thenReturn(Arrays.<Windmill.GlobalDataId>asList());
        Mockito.when(stepContext.issueSideInputFetch(ArgumentMatchers.eq(view), ArgumentMatchers.any(BoundedWindow.class), ArgumentMatchers.eq(UNKNOWN))).thenReturn(false);
        ListOutputManager outputManager = new ListOutputManager();
        List<PCollectionView<String>> views = Arrays.asList(view);
        StreamingSideInputFetcher<String, IntervalWindow> sideInputFetcher = createFetcher(views);
        StreamingSideInputDoFnRunner<String, String, IntervalWindow> runner = createRunner(outputManager, views, sideInputFetcher);
        runner.startBundle();
        runner.processElement(createDatum("e", 0));
        runner.finishBundle();
        Assert.assertTrue(outputManager.getOutput(StreamingSideInputDoFnRunnerTest.mainOutputTag).isEmpty());
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant(10));
        // Verify that we added the element to an appropriate tag list, and that we buffered the element
        ValueState<Map<IntervalWindow, Set<GlobalDataRequest>>> blockedMapState = state.state(StateNamespaces.global(), StreamingSideInputFetcher.blockedMapAddr(StreamingSideInputDoFnRunnerTest.WINDOW_FN.windowCoder()));
        Assert.assertEquals(blockedMapState.read(), Collections.singletonMap(window, Collections.singleton(Windmill.GlobalDataRequest.newBuilder().setDataId(GlobalDataId.newBuilder().setTag(view.getTagInternal().getId()).setVersion(ByteString.copyFrom(CoderUtils.encodeToByteArray(IntervalWindow.getCoder(), window))).build()).setExistenceWatermarkDeadline(9000).build())));
        Assert.assertThat(sideInputFetcher.elementBag(createWindow(0)).read(), Matchers.contains(createDatum("e", 0)));
        Assert.assertEquals(sideInputFetcher.watermarkHold(createWindow(0)).read(), new Instant(0));
    }

    @Test
    public void testMultipleWindowsNotReady() throws Exception {
        PCollectionView<String> view = createView();
        Mockito.when(stepContext.getSideInputNotifications()).thenReturn(Arrays.<Windmill.GlobalDataId>asList());
        Mockito.when(stepContext.issueSideInputFetch(ArgumentMatchers.eq(view), ArgumentMatchers.any(BoundedWindow.class), ArgumentMatchers.eq(UNKNOWN))).thenReturn(false);
        ListOutputManager outputManager = new ListOutputManager();
        List<PCollectionView<String>> views = Arrays.asList(view);
        StreamingSideInputFetcher<String, IntervalWindow> sideInputFetcher = createFetcher(views);
        StreamingSideInputDoFnRunner<String, String, IntervalWindow> runner = createRunner(SlidingWindows.of(Duration.millis(10)).every(Duration.millis(10)), outputManager, views, sideInputFetcher);
        IntervalWindow window1 = new IntervalWindow(new Instant(0), new Instant(10));
        IntervalWindow window2 = new IntervalWindow(new Instant((-5)), new Instant(5));
        long timestamp = 1L;
        WindowedValue<String> elem = WindowedValue.of("e", new Instant(timestamp), Arrays.asList(window1, window2), NO_FIRING);
        runner.startBundle();
        runner.processElement(elem);
        runner.finishBundle();
        Assert.assertTrue(outputManager.getOutput(StreamingSideInputDoFnRunnerTest.mainOutputTag).isEmpty());
        // Verify that we added the element to an appropriate tag list, and that we buffered the element
        // in both windows separately
        ValueState<Map<IntervalWindow, Set<GlobalDataRequest>>> blockedMapState = state.state(StateNamespaces.global(), StreamingSideInputFetcher.blockedMapAddr(StreamingSideInputDoFnRunnerTest.WINDOW_FN.windowCoder()));
        Map<IntervalWindow, Set<GlobalDataRequest>> blockedMap = blockedMapState.read();
        Assert.assertThat(blockedMap.get(window1), Matchers.equalTo(Collections.singleton(Windmill.GlobalDataRequest.newBuilder().setDataId(GlobalDataId.newBuilder().setTag(view.getTagInternal().getId()).setVersion(ByteString.copyFrom(CoderUtils.encodeToByteArray(IntervalWindow.getCoder(), window1))).build()).setExistenceWatermarkDeadline(9000).build())));
        Assert.assertThat(blockedMap.get(window2), Matchers.equalTo(Collections.singleton(Windmill.GlobalDataRequest.newBuilder().setDataId(GlobalDataId.newBuilder().setTag(view.getTagInternal().getId()).setVersion(ByteString.copyFrom(CoderUtils.encodeToByteArray(IntervalWindow.getCoder(), window1))).build()).setExistenceWatermarkDeadline(9000).build())));
        Assert.assertThat(sideInputFetcher.elementBag(window1).read(), Matchers.contains(Iterables.get(elem.explodeWindows(), 0)));
        Assert.assertThat(sideInputFetcher.elementBag(window2).read(), Matchers.contains(Iterables.get(elem.explodeWindows(), 1)));
        Assert.assertEquals(sideInputFetcher.watermarkHold(window1).read(), new Instant(timestamp));
        Assert.assertEquals(sideInputFetcher.watermarkHold(window2).read(), new Instant(timestamp));
    }

    @Test
    public void testSideInputNotification() throws Exception {
        PCollectionView<String> view = createView();
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant(10));
        Windmill.GlobalDataId id = GlobalDataId.newBuilder().setTag(view.getTagInternal().getId()).setVersion(ByteString.copyFrom(CoderUtils.encodeToByteArray(IntervalWindow.getCoder(), window))).build();
        Set<Windmill.GlobalDataRequest> requestSet = new HashSet<>();
        requestSet.add(Windmill.GlobalDataRequest.newBuilder().setDataId(id).build());
        Map<IntervalWindow, Set<Windmill.GlobalDataRequest>> blockedMap = new HashMap<>();
        blockedMap.put(window, requestSet);
        ValueState<Map<IntervalWindow, Set<GlobalDataRequest>>> blockedMapState = state.state(StateNamespaces.global(), StreamingSideInputFetcher.blockedMapAddr(StreamingSideInputDoFnRunnerTest.WINDOW_FN.windowCoder()));
        blockedMapState.write(blockedMap);
        ListOutputManager outputManager = new ListOutputManager();
        List<PCollectionView<String>> views = Arrays.asList(view);
        StreamingSideInputFetcher<String, IntervalWindow> sideInputFetcher = createFetcher(views);
        StreamingSideInputDoFnRunner<String, String, IntervalWindow> runner = createRunner(outputManager, views, sideInputFetcher);
        sideInputFetcher.watermarkHold(createWindow(0)).add(new Instant(0));
        sideInputFetcher.elementBag(createWindow(0)).add(createDatum("e", 0));
        Mockito.when(stepContext.getSideInputNotifications()).thenReturn(Arrays.asList(id));
        Mockito.when(stepContext.issueSideInputFetch(ArgumentMatchers.eq(view), ArgumentMatchers.any(BoundedWindow.class), ArgumentMatchers.eq(UNKNOWN))).thenReturn(false);
        Mockito.when(stepContext.issueSideInputFetch(ArgumentMatchers.eq(view), ArgumentMatchers.any(BoundedWindow.class), ArgumentMatchers.eq(KNOWN_READY))).thenReturn(true);
        Mockito.when(execContext.getSideInputReaderForViews(Mockito.<Iterable<? extends PCollectionView<?>>>any())).thenReturn(mockSideInputReader);
        Mockito.when(mockSideInputReader.contains(ArgumentMatchers.eq(view))).thenReturn(true);
        Mockito.when(mockSideInputReader.get(ArgumentMatchers.eq(view), ArgumentMatchers.any(BoundedWindow.class))).thenReturn("data");
        runner.startBundle();
        runner.finishBundle();
        Assert.assertThat(outputManager.getOutput(StreamingSideInputDoFnRunnerTest.mainOutputTag), Matchers.contains(createDatum("e:data", 0)));
        Assert.assertThat(blockedMapState.read(), Matchers.nullValue());
        Assert.assertThat(sideInputFetcher.watermarkHold(createWindow(0)).read(), Matchers.nullValue());
        Assert.assertThat(sideInputFetcher.elementBag(createWindow(0)).read(), Matchers.emptyIterable());
    }

    @Test
    public void testMultipleSideInputs() throws Exception {
        PCollectionView<String> view1 = createView();
        PCollectionView<String> view2 = createView();
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant(10));
        Windmill.GlobalDataId id = GlobalDataId.newBuilder().setTag(view1.getTagInternal().getId()).setVersion(ByteString.copyFrom(CoderUtils.encodeToByteArray(IntervalWindow.getCoder(), window))).build();
        Set<Windmill.GlobalDataRequest> requestSet = new HashSet<>();
        requestSet.add(Windmill.GlobalDataRequest.newBuilder().setDataId(id).build());
        Map<IntervalWindow, Set<Windmill.GlobalDataRequest>> blockedMap = new HashMap<>();
        blockedMap.put(window, requestSet);
        ValueState<Map<IntervalWindow, Set<GlobalDataRequest>>> blockedMapState = state.state(StateNamespaces.global(), StreamingSideInputFetcher.blockedMapAddr(StreamingSideInputDoFnRunnerTest.WINDOW_FN.windowCoder()));
        blockedMapState.write(blockedMap);
        Mockito.when(stepContext.getSideInputNotifications()).thenReturn(Arrays.asList(id));
        Mockito.when(stepContext.issueSideInputFetch(ArgumentMatchers.any(PCollectionView.class), ArgumentMatchers.any(BoundedWindow.class), ArgumentMatchers.any(SideInputState.class))).thenReturn(true);
        Mockito.when(execContext.getSideInputReaderForViews(Mockito.<Iterable<? extends PCollectionView<?>>>any())).thenReturn(mockSideInputReader);
        Mockito.when(mockSideInputReader.contains(ArgumentMatchers.eq(view1))).thenReturn(true);
        Mockito.when(mockSideInputReader.contains(ArgumentMatchers.eq(view2))).thenReturn(true);
        Mockito.when(mockSideInputReader.get(ArgumentMatchers.eq(view1), ArgumentMatchers.any(BoundedWindow.class))).thenReturn("data1");
        Mockito.when(mockSideInputReader.get(ArgumentMatchers.eq(view2), ArgumentMatchers.any(BoundedWindow.class))).thenReturn("data2");
        ListOutputManager outputManager = new ListOutputManager();
        List<PCollectionView<String>> views = Arrays.asList(view1, view2);
        StreamingSideInputFetcher<String, IntervalWindow> sideInputFetcher = createFetcher(views);
        StreamingSideInputDoFnRunner<String, String, IntervalWindow> runner = createRunner(outputManager, views, sideInputFetcher);
        sideInputFetcher.watermarkHold(createWindow(0)).add(new Instant(0));
        sideInputFetcher.elementBag(createWindow(0)).add(createDatum("e1", 0));
        runner.startBundle();
        runner.processElement(createDatum("e2", 2));
        runner.finishBundle();
        Assert.assertThat(outputManager.getOutput(StreamingSideInputDoFnRunnerTest.mainOutputTag), Matchers.contains(createDatum("e1:data1:data2", 0), createDatum("e2:data1:data2", 2)));
        Assert.assertThat(blockedMapState.read(), Matchers.nullValue());
        Assert.assertThat(sideInputFetcher.watermarkHold(createWindow(0)).read(), Matchers.nullValue());
        Assert.assertThat(sideInputFetcher.elementBag(createWindow(0)).read(), Matchers.emptyIterable());
    }

    private static class SideInputFn extends DoFn<String, String> {
        private List<PCollectionView<String>> views;

        public SideInputFn(List<PCollectionView<String>> views) {
            this.views = views;
        }

        @ProcessElement
        public void processElement(ProcessContext c) {
            String output = c.element();
            for (PCollectionView<String> view : views) {
                output += ":" + (c.sideInput(view));
            }
            c.output(output);
        }
    }
}

