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
package org.apache.beam.runners.direct;


import GlobalWindow.INSTANCE;
import PaneInfo.ON_TIME_AND_ONLY_FIRING;
import Timing.EARLY;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.apache.beam.runners.core.ReadyCheckingSideInputReader;
import org.apache.beam.sdk.testing.TestPipeline;
import org.apache.beam.sdk.transforms.Create;
import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link SideInputContainer}.
 */
@RunWith(JUnit4.class)
public class SideInputContainerTest {
    private static final BoundedWindow FIRST_WINDOW = new BoundedWindow() {
        @Override
        public Instant maxTimestamp() {
            return new Instant(789541L);
        }

        @Override
        public String toString() {
            return "firstWindow";
        }
    };

    private static final BoundedWindow SECOND_WINDOW = new BoundedWindow() {
        @Override
        public Instant maxTimestamp() {
            return new Instant(14564786L);
        }

        @Override
        public String toString() {
            return "secondWindow";
        }
    };

    @Rule
    public TestPipeline pipeline = TestPipeline.create().enableAbandonedNodeEnforcement(false);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private EvaluationContext context;

    private SideInputContainer container;

    private PCollectionView<Map<String, Integer>> mapView;

    private PCollectionView<Double> singletonView;

    // Not present in container.
    private PCollectionView<Iterable<Integer>> iterableView;

    @Test
    public void getAfterWriteReturnsPaneInWindow() throws Exception {
        ImmutableList.Builder<WindowedValue<?>> valuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asMap(), KV.of("one", 1))) {
            valuesBuilder.add(WindowedValue.of(materializedValue, new Instant(1L), SideInputContainerTest.FIRST_WINDOW, ON_TIME_AND_ONLY_FIRING));
        }
        for (Object materializedValue : materializeValuesFor(View.asMap(), KV.of("two", 2))) {
            valuesBuilder.add(WindowedValue.of(materializedValue, new Instant(20L), SideInputContainerTest.FIRST_WINDOW, ON_TIME_AND_ONLY_FIRING));
        }
        container.write(mapView, valuesBuilder.build());
        Map<String, Integer> viewContents = container.createReaderForViews(ImmutableList.of(mapView)).get(mapView, SideInputContainerTest.FIRST_WINDOW);
        Assert.assertThat(viewContents, Matchers.hasEntry("one", 1));
        Assert.assertThat(viewContents, Matchers.hasEntry("two", 2));
        Assert.assertThat(viewContents.size(), Matchers.is(2));
    }

    @Test
    public void getReturnsLatestPaneInWindow() throws Exception {
        ImmutableList.Builder<WindowedValue<?>> valuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asMap(), KV.of("one", 1))) {
            valuesBuilder.add(WindowedValue.of(materializedValue, new Instant(1L), SideInputContainerTest.SECOND_WINDOW, PaneInfo.createPane(true, false, EARLY)));
        }
        for (Object materializedValue : materializeValuesFor(View.asMap(), KV.of("two", 2))) {
            valuesBuilder.add(WindowedValue.of(materializedValue, new Instant(20L), SideInputContainerTest.SECOND_WINDOW, PaneInfo.createPane(true, false, EARLY)));
        }
        container.write(mapView, valuesBuilder.build());
        Map<String, Integer> viewContents = container.createReaderForViews(ImmutableList.of(mapView)).get(mapView, SideInputContainerTest.SECOND_WINDOW);
        Assert.assertThat(viewContents, Matchers.hasEntry("one", 1));
        Assert.assertThat(viewContents, Matchers.hasEntry("two", 2));
        Assert.assertThat(viewContents.size(), Matchers.is(2));
        ImmutableList.Builder<WindowedValue<?>> overwriteValuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asMap(), KV.of("three", 3))) {
            overwriteValuesBuilder.add(WindowedValue.of(materializedValue, new Instant(300L), SideInputContainerTest.SECOND_WINDOW, PaneInfo.createPane(false, false, EARLY, 1, (-1))));
        }
        container.write(mapView, overwriteValuesBuilder.build());
        Map<String, Integer> overwrittenViewContents = container.createReaderForViews(ImmutableList.of(mapView)).get(mapView, SideInputContainerTest.SECOND_WINDOW);
        Assert.assertThat(overwrittenViewContents, Matchers.hasEntry("three", 3));
        Assert.assertThat(overwrittenViewContents.size(), Matchers.is(1));
    }

    /**
     * Demonstrates that calling get() on a window that currently has no data does not return until
     * there is data in the pane.
     */
    @Test
    public void getNotReadyThrows() throws Exception {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("not ready");
        container.createReaderForViews(ImmutableList.of(mapView)).get(mapView, INSTANCE);
    }

    @Test
    public void withViewsForViewNotInContainerFails() {
        PCollection<KV<String, String>> input = pipeline.apply(Create.empty(new org.apache.beam.sdk.values.TypeDescriptor<KV<String, String>>() {}));
        PCollectionView<Map<String, Iterable<String>>> newView = input.apply(View.asMultimap());
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("unknown views");
        thrown.expectMessage(newView.toString());
        container.createReaderForViews(ImmutableList.of(newView));
    }

    @Test
    public void getOnReaderForViewNotInReaderFails() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage(("unknown view: " + (iterableView.toString())));
        container.createReaderForViews(ImmutableList.of(mapView)).get(iterableView, INSTANCE);
    }

    @Test
    public void writeForMultipleElementsInDifferentWindowsSucceeds() throws Exception {
        ImmutableList.Builder<WindowedValue<?>> valuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asSingleton(), 2.875)) {
            valuesBuilder.add(WindowedValue.of(materializedValue, SideInputContainerTest.FIRST_WINDOW.maxTimestamp().minus(200L), SideInputContainerTest.FIRST_WINDOW, ON_TIME_AND_ONLY_FIRING));
        }
        for (Object materializedValue : materializeValuesFor(View.asSingleton(), 4.125)) {
            valuesBuilder.add(WindowedValue.of(materializedValue, SideInputContainerTest.SECOND_WINDOW.maxTimestamp().minus(2000000L), SideInputContainerTest.SECOND_WINDOW, ON_TIME_AND_ONLY_FIRING));
        }
        container.write(singletonView, valuesBuilder.build());
        Assert.assertThat(container.createReaderForViews(ImmutableList.of(singletonView)).get(singletonView, SideInputContainerTest.FIRST_WINDOW), Matchers.equalTo(2.875));
        Assert.assertThat(container.createReaderForViews(ImmutableList.of(singletonView)).get(singletonView, SideInputContainerTest.SECOND_WINDOW), Matchers.equalTo(4.125));
    }

    @Test
    public void writeForMultipleIdenticalElementsInSameWindowSucceeds() throws Exception {
        ImmutableList.Builder<WindowedValue<?>> valuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asIterable(), 44, 44)) {
            valuesBuilder.add(WindowedValue.of(materializedValue, SideInputContainerTest.FIRST_WINDOW.maxTimestamp().minus(200L), SideInputContainerTest.FIRST_WINDOW, ON_TIME_AND_ONLY_FIRING));
        }
        container.write(iterableView, valuesBuilder.build());
        Assert.assertThat(container.createReaderForViews(ImmutableList.of(iterableView)).get(iterableView, SideInputContainerTest.FIRST_WINDOW), Matchers.contains(44, 44));
    }

    @Test
    public void writeForElementInMultipleWindowsSucceeds() throws Exception {
        ImmutableList.Builder<WindowedValue<?>> valuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asSingleton(), 2.875)) {
            valuesBuilder.add(WindowedValue.of(materializedValue, SideInputContainerTest.FIRST_WINDOW.maxTimestamp().minus(200L), ImmutableList.of(SideInputContainerTest.FIRST_WINDOW, SideInputContainerTest.SECOND_WINDOW), ON_TIME_AND_ONLY_FIRING));
        }
        container.write(singletonView, valuesBuilder.build());
        Assert.assertThat(container.createReaderForViews(ImmutableList.of(singletonView)).get(singletonView, SideInputContainerTest.FIRST_WINDOW), Matchers.equalTo(2.875));
        Assert.assertThat(container.createReaderForViews(ImmutableList.of(singletonView)).get(singletonView, SideInputContainerTest.SECOND_WINDOW), Matchers.equalTo(2.875));
    }

    @Test
    public void finishDoesNotOverwriteWrittenElements() throws Exception {
        ImmutableList.Builder<WindowedValue<?>> valuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asMap(), KV.of("one", 1))) {
            valuesBuilder.add(WindowedValue.of(materializedValue, new Instant(1L), SideInputContainerTest.SECOND_WINDOW, PaneInfo.createPane(true, false, EARLY)));
        }
        for (Object materializedValue : materializeValuesFor(View.asMap(), KV.of("two", 2))) {
            valuesBuilder.add(WindowedValue.of(materializedValue, new Instant(20L), SideInputContainerTest.SECOND_WINDOW, PaneInfo.createPane(true, false, EARLY)));
        }
        container.write(mapView, valuesBuilder.build());
        immediatelyInvokeCallback(mapView, SideInputContainerTest.SECOND_WINDOW);
        Map<String, Integer> viewContents = container.createReaderForViews(ImmutableList.of(mapView)).get(mapView, SideInputContainerTest.SECOND_WINDOW);
        Assert.assertThat(viewContents, Matchers.hasEntry("one", 1));
        Assert.assertThat(viewContents, Matchers.hasEntry("two", 2));
        Assert.assertThat(viewContents.size(), Matchers.is(2));
    }

    @Test
    public void finishOnPendingViewsSetsEmptyElements() throws Exception {
        immediatelyInvokeCallback(mapView, SideInputContainerTest.SECOND_WINDOW);
        Future<Map<String, Integer>> mapFuture = getFutureOfView(container.createReaderForViews(ImmutableList.of(mapView)), mapView, SideInputContainerTest.SECOND_WINDOW);
        Assert.assertThat(mapFuture.get().isEmpty(), Matchers.is(true));
    }

    /**
     * Demonstrates that calling isReady on an empty container throws an {@link IllegalArgumentException}.
     */
    @Test
    public void isReadyInEmptyReaderThrows() {
        ReadyCheckingSideInputReader reader = container.createReaderForViews(ImmutableList.of());
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("does not contain");
        thrown.expectMessage(ImmutableList.of().toString());
        reader.isReady(mapView, INSTANCE);
    }

    /**
     * Demonstrates that calling isReady returns false until elements are written to the {@link PCollectionView}, {@link BoundedWindow} pair, at which point it returns true.
     */
    @Test
    public void isReadyForSomeNotReadyViewsFalseUntilElements() {
        ImmutableList.Builder<WindowedValue<?>> mapValuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asMap(), KV.of("one", 1))) {
            mapValuesBuilder.add(WindowedValue.of(materializedValue, SideInputContainerTest.SECOND_WINDOW.maxTimestamp().minus(100L), SideInputContainerTest.SECOND_WINDOW, ON_TIME_AND_ONLY_FIRING));
        }
        container.write(mapView, mapValuesBuilder.build());
        ReadyCheckingSideInputReader reader = container.createReaderForViews(ImmutableList.of(mapView, singletonView));
        Assert.assertThat(reader.isReady(mapView, SideInputContainerTest.FIRST_WINDOW), Matchers.is(false));
        Assert.assertThat(reader.isReady(mapView, SideInputContainerTest.SECOND_WINDOW), Matchers.is(true));
        Assert.assertThat(reader.isReady(singletonView, SideInputContainerTest.SECOND_WINDOW), Matchers.is(false));
        ImmutableList.Builder<WindowedValue<?>> newMapValuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asMap(), KV.of("too", 2))) {
            newMapValuesBuilder.add(WindowedValue.of(materializedValue, SideInputContainerTest.FIRST_WINDOW.maxTimestamp().minus(100L), SideInputContainerTest.FIRST_WINDOW, ON_TIME_AND_ONLY_FIRING));
        }
        container.write(mapView, newMapValuesBuilder.build());
        // Cached value is false
        Assert.assertThat(reader.isReady(mapView, SideInputContainerTest.FIRST_WINDOW), Matchers.is(false));
        ImmutableList.Builder<WindowedValue<?>> singletonValuesBuilder = ImmutableList.builder();
        for (Object materializedValue : materializeValuesFor(View.asSingleton(), 1.25)) {
            singletonValuesBuilder.add(WindowedValue.of(materializedValue, SideInputContainerTest.SECOND_WINDOW.maxTimestamp().minus(100L), SideInputContainerTest.SECOND_WINDOW, ON_TIME_AND_ONLY_FIRING));
        }
        container.write(singletonView, singletonValuesBuilder.build());
        Assert.assertThat(reader.isReady(mapView, SideInputContainerTest.SECOND_WINDOW), Matchers.is(true));
        Assert.assertThat(reader.isReady(singletonView, SideInputContainerTest.SECOND_WINDOW), Matchers.is(false));
        Assert.assertThat(reader.isReady(mapView, INSTANCE), Matchers.is(false));
        Assert.assertThat(reader.isReady(singletonView, INSTANCE), Matchers.is(false));
        reader = container.createReaderForViews(ImmutableList.of(mapView, singletonView));
        Assert.assertThat(reader.isReady(mapView, SideInputContainerTest.SECOND_WINDOW), Matchers.is(true));
        Assert.assertThat(reader.isReady(singletonView, SideInputContainerTest.SECOND_WINDOW), Matchers.is(true));
        Assert.assertThat(reader.isReady(mapView, SideInputContainerTest.FIRST_WINDOW), Matchers.is(true));
    }

    @Test
    public void isReadyForEmptyWindowTrue() throws Exception {
        CountDownLatch onComplete = new CountDownLatch(1);
        immediatelyInvokeCallback(mapView, INSTANCE);
        CountDownLatch latch = invokeLatchedCallback(singletonView, INSTANCE, onComplete);
        ReadyCheckingSideInputReader reader = container.createReaderForViews(ImmutableList.of(mapView, singletonView));
        Assert.assertThat(reader.isReady(mapView, INSTANCE), Matchers.is(true));
        Assert.assertThat(reader.isReady(singletonView, INSTANCE), Matchers.is(false));
        latch.countDown();
        if (!(onComplete.await(1500L, TimeUnit.MILLISECONDS))) {
            Assert.fail("Callback to set empty values did not complete!");
        }
        // The cached value was false, so it continues to be true
        Assert.assertThat(reader.isReady(singletonView, INSTANCE), Matchers.is(false));
        // A new reader for the same container gets a fresh look
        reader = container.createReaderForViews(ImmutableList.of(mapView, singletonView));
        Assert.assertThat(reader.isReady(singletonView, INSTANCE), Matchers.is(true));
    }
}

