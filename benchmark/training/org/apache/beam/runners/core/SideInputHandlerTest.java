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


import org.apache.beam.sdk.transforms.View;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link SideInputHandler}.
 */
@RunWith(JUnit4.class)
public class SideInputHandlerTest {
    private static final long WINDOW_MSECS_1 = 100;

    private static final long WINDOW_MSECS_2 = 500;

    private PCollectionView<Iterable<String>> view1;

    private PCollectionView<Iterable<String>> view2;

    @Test
    public void testIsEmpty() {
        SideInputHandler sideInputHandler = new SideInputHandler(ImmutableList.of(view1), InMemoryStateInternals.<Void>forKey(null));
        Assert.assertFalse(sideInputHandler.isEmpty());
        // create an empty handler
        SideInputHandler emptySideInputHandler = new SideInputHandler(ImmutableList.of(), InMemoryStateInternals.<Void>forKey(null));
        Assert.assertTrue(emptySideInputHandler.isEmpty());
    }

    @Test
    public void testContains() {
        SideInputHandler sideInputHandler = new SideInputHandler(ImmutableList.of(view1), InMemoryStateInternals.<Void>forKey(null));
        Assert.assertTrue(sideInputHandler.contains(view1));
        Assert.assertFalse(sideInputHandler.contains(view2));
    }

    @Test
    public void testIsReady() {
        SideInputHandler sideInputHandler = new SideInputHandler(ImmutableList.of(view1, view2), InMemoryStateInternals.<Void>forKey(null));
        IntervalWindow firstWindow = new IntervalWindow(new Instant(0), new Instant(SideInputHandlerTest.WINDOW_MSECS_1));
        IntervalWindow secondWindow = new IntervalWindow(new Instant(0), new Instant(SideInputHandlerTest.WINDOW_MSECS_2));
        // side input should not yet be ready
        Assert.assertFalse(sideInputHandler.isReady(view1, firstWindow));
        // add a value for view1
        sideInputHandler.addSideInputValue(view1, valuesInWindow(materializeValuesFor(View.asIterable(), "Hello"), new Instant(0), firstWindow));
        // now side input should be ready
        Assert.assertTrue(sideInputHandler.isReady(view1, firstWindow));
        // second window input should still not be ready
        Assert.assertFalse(sideInputHandler.isReady(view1, secondWindow));
    }

    @Test
    public void testNewInputReplacesPreviousInput() {
        // new input should completely replace old input
        // the creation of the Iterable that has the side input
        // contents happens upstream. this is also where
        // accumulation/discarding is decided.
        SideInputHandler sideInputHandler = new SideInputHandler(ImmutableList.of(view1), InMemoryStateInternals.<Void>forKey(null));
        IntervalWindow window = new IntervalWindow(new Instant(0), new Instant(SideInputHandlerTest.WINDOW_MSECS_1));
        // add a first value for view1
        sideInputHandler.addSideInputValue(view1, valuesInWindow(materializeValuesFor(View.asIterable(), "Hello"), new Instant(0), window));
        Assert.assertThat(sideInputHandler.get(view1, window), Matchers.contains("Hello"));
        // subsequent values should replace existing values
        sideInputHandler.addSideInputValue(view1, valuesInWindow(materializeValuesFor(View.asIterable(), "Ciao", "Buongiorno"), new Instant(0), window));
        Assert.assertThat(sideInputHandler.get(view1, window), Matchers.contains("Ciao", "Buongiorno"));
    }

    @Test
    public void testMultipleWindows() {
        SideInputHandler sideInputHandler = new SideInputHandler(ImmutableList.of(view1), InMemoryStateInternals.<Void>forKey(null));
        // two windows that we'll later use for adding elements/retrieving side input
        IntervalWindow firstWindow = new IntervalWindow(new Instant(0), new Instant(SideInputHandlerTest.WINDOW_MSECS_1));
        IntervalWindow secondWindow = new IntervalWindow(new Instant(1000), new Instant((1000 + (SideInputHandlerTest.WINDOW_MSECS_2))));
        // add a first value for view1 in the first window
        sideInputHandler.addSideInputValue(view1, valuesInWindow(materializeValuesFor(View.asIterable(), "Hello"), new Instant(0), firstWindow));
        Assert.assertThat(sideInputHandler.get(view1, firstWindow), Matchers.contains("Hello"));
        // add something for second window of view1
        sideInputHandler.addSideInputValue(view1, valuesInWindow(materializeValuesFor(View.asIterable(), "Arrivederci"), new Instant(0), secondWindow));
        Assert.assertThat(sideInputHandler.get(view1, secondWindow), Matchers.contains("Arrivederci"));
        // contents for first window should be unaffected
        Assert.assertThat(sideInputHandler.get(view1, firstWindow), Matchers.contains("Hello"));
    }

    @Test
    public void testMultipleSideInputs() {
        SideInputHandler sideInputHandler = new SideInputHandler(ImmutableList.of(view1, view2), InMemoryStateInternals.<Void>forKey(null));
        // two windows that we'll later use for adding elements/retrieving side input
        IntervalWindow firstWindow = new IntervalWindow(new Instant(0), new Instant(SideInputHandlerTest.WINDOW_MSECS_1));
        // add value for view1 in the first window
        sideInputHandler.addSideInputValue(view1, valuesInWindow(materializeValuesFor(View.asIterable(), "Hello"), new Instant(0), firstWindow));
        Assert.assertThat(sideInputHandler.get(view1, firstWindow), Matchers.contains("Hello"));
        // view2 should not have any data
        Assert.assertFalse(sideInputHandler.isReady(view2, firstWindow));
        // also add some data for view2
        sideInputHandler.addSideInputValue(view2, valuesInWindow(materializeValuesFor(View.asIterable(), "Salut"), new Instant(0), firstWindow));
        Assert.assertTrue(sideInputHandler.isReady(view2, firstWindow));
        Assert.assertThat(sideInputHandler.get(view2, firstWindow), Matchers.contains("Salut"));
        // view1 should not be affected by that
        Assert.assertThat(sideInputHandler.get(view1, firstWindow), Matchers.contains("Hello"));
    }
}

