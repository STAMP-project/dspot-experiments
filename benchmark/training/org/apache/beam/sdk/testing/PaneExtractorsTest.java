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
package org.apache.beam.sdk.testing;


import BoundedWindow.TIMESTAMP_MIN_VALUE;
import GlobalWindow.INSTANCE;
import PAssert.PAssertionSite;
import PaneInfo.NO_FIRING;
import PaneInfo.ON_TIME_AND_ONLY_FIRING;
import Timing.EARLY;
import Timing.LATE;
import Timing.ON_TIME;
import org.apache.beam.sdk.transforms.SerializableFunction;
import org.apache.beam.sdk.transforms.windowing.PaneInfo;
import org.apache.beam.sdk.values.ValueInSingleWindow;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link PaneExtractors}.
 */
@RunWith(JUnit4.class)
public class PaneExtractorsTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void onlyPaneNoFiring() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.onlyPane(PAssertionSite.capture(""));
        Iterable<ValueInSingleWindow<Integer>> noFiring = ImmutableList.of(ValueInSingleWindow.of(9, TIMESTAMP_MIN_VALUE, INSTANCE, NO_FIRING), ValueInSingleWindow.of(19, TIMESTAMP_MIN_VALUE, INSTANCE, NO_FIRING));
        Assert.assertThat(extractor.apply(noFiring), Matchers.containsInAnyOrder(9, 19));
    }

    @Test
    public void onlyPaneOnlyOneFiring() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.onlyPane(PAssertionSite.capture(""));
        Iterable<ValueInSingleWindow<Integer>> onlyFiring = ImmutableList.of(ValueInSingleWindow.of(2, new Instant(0L), INSTANCE, ON_TIME_AND_ONLY_FIRING), ValueInSingleWindow.of(1, new Instant(0L), INSTANCE, ON_TIME_AND_ONLY_FIRING));
        Assert.assertThat(extractor.apply(onlyFiring), Matchers.containsInAnyOrder(2, 1));
    }

    @Test
    public void onlyPaneMultiplePanesFails() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.onlyPane(PAssertionSite.capture(""));
        Iterable<ValueInSingleWindow<Integer>> multipleFiring = ImmutableList.of(ValueInSingleWindow.of(4, new Instant(0L), INSTANCE, PaneInfo.createPane(true, false, EARLY)), ValueInSingleWindow.of(2, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, ON_TIME, 1L, 0L)), ValueInSingleWindow.of(1, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, LATE, 2L, 1L)));
        thrown.expectMessage("trigger that fires at most once");
        extractor.apply(multipleFiring);
    }

    @Test
    public void onTimePane() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.onTimePane();
        Iterable<ValueInSingleWindow<Integer>> onlyOnTime = ImmutableList.of(ValueInSingleWindow.of(4, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, ON_TIME, 1L, 0L)), ValueInSingleWindow.of(2, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, ON_TIME, 1L, 0L)));
        Assert.assertThat(extractor.apply(onlyOnTime), Matchers.containsInAnyOrder(2, 4));
    }

    @Test
    public void onTimePaneOnlyEarlyAndLate() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.onTimePane();
        Iterable<ValueInSingleWindow<Integer>> onlyOnTime = ImmutableList.of(ValueInSingleWindow.of(8, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, LATE, 2L, 1L)), ValueInSingleWindow.of(4, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, ON_TIME, 1L, 0L)), ValueInSingleWindow.of(2, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, ON_TIME, 1L, 0L)), ValueInSingleWindow.of(1, new Instant(0L), INSTANCE, PaneInfo.createPane(true, false, EARLY)));
        Assert.assertThat(extractor.apply(onlyOnTime), Matchers.containsInAnyOrder(2, 4));
    }

    @Test
    public void finalPane() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.finalPane();
        Iterable<ValueInSingleWindow<Integer>> onlyOnTime = ImmutableList.of(ValueInSingleWindow.of(8, new Instant(0L), INSTANCE, PaneInfo.createPane(false, true, LATE, 2L, 1L)), ValueInSingleWindow.of(4, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, ON_TIME, 1L, 0L)), ValueInSingleWindow.of(1, new Instant(0L), INSTANCE, PaneInfo.createPane(true, false, EARLY)));
        Assert.assertThat(extractor.apply(onlyOnTime), Matchers.containsInAnyOrder(8));
    }

    @Test
    public void finalPaneNoExplicitFinalEmpty() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.finalPane();
        Iterable<ValueInSingleWindow<Integer>> onlyOnTime = ImmutableList.of(ValueInSingleWindow.of(8, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, LATE, 2L, 1L)), ValueInSingleWindow.of(4, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, ON_TIME, 1L, 0L)), ValueInSingleWindow.of(1, new Instant(0L), INSTANCE, PaneInfo.createPane(true, false, EARLY)));
        Assert.assertThat(extractor.apply(onlyOnTime), Matchers.emptyIterable());
    }

    @Test
    public void nonLatePanesSingleOnTime() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.nonLatePanes();
        Iterable<ValueInSingleWindow<Integer>> onlyOnTime = ImmutableList.of(ValueInSingleWindow.of(8, new Instant(0L), INSTANCE, ON_TIME_AND_ONLY_FIRING), ValueInSingleWindow.of(4, new Instant(0L), INSTANCE, ON_TIME_AND_ONLY_FIRING), ValueInSingleWindow.of(2, new Instant(0L), INSTANCE, ON_TIME_AND_ONLY_FIRING));
        Assert.assertThat(extractor.apply(onlyOnTime), Matchers.containsInAnyOrder(2, 4, 8));
    }

    @Test
    public void nonLatePanesSingleEarly() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.nonLatePanes();
        Iterable<ValueInSingleWindow<Integer>> onlyOnTime = ImmutableList.of(ValueInSingleWindow.of(8, new Instant(0L), INSTANCE, PaneInfo.createPane(true, false, EARLY)), ValueInSingleWindow.of(4, new Instant(0L), INSTANCE, PaneInfo.createPane(true, false, EARLY)));
        Assert.assertThat(extractor.apply(onlyOnTime), Matchers.containsInAnyOrder(4, 8));
    }

    @Test
    public void allPanesSingleLate() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.nonLatePanes();
        Iterable<ValueInSingleWindow<Integer>> onlyOnTime = ImmutableList.of(ValueInSingleWindow.of(8, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, LATE, 0L, 0L)));
        Assert.assertThat(extractor.apply(onlyOnTime), Matchers.emptyIterable());
    }

    @Test
    public void nonLatePanesMultiplePanes() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.nonLatePanes();
        Iterable<ValueInSingleWindow<Integer>> onlyOnTime = ImmutableList.of(ValueInSingleWindow.of(8, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, LATE, 2L, 1L)), ValueInSingleWindow.of(7, new Instant(0L), INSTANCE, NO_FIRING), ValueInSingleWindow.of(4, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, ON_TIME, 1L, 0L)), ValueInSingleWindow.of(1, new Instant(0L), INSTANCE, PaneInfo.createPane(true, false, EARLY)));
        Assert.assertThat(extractor.apply(onlyOnTime), Matchers.containsInAnyOrder(4, 1, 7));
    }

    @Test
    public void allPanesSinglePane() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.allPanes();
        Iterable<ValueInSingleWindow<Integer>> onlyOnTime = ImmutableList.of(ValueInSingleWindow.of(8, new Instant(0L), INSTANCE, ON_TIME_AND_ONLY_FIRING), ValueInSingleWindow.of(4, new Instant(0L), INSTANCE, ON_TIME_AND_ONLY_FIRING), ValueInSingleWindow.of(2, new Instant(0L), INSTANCE, ON_TIME_AND_ONLY_FIRING));
        Assert.assertThat(extractor.apply(onlyOnTime), Matchers.containsInAnyOrder(2, 4, 8));
    }

    @Test
    public void allPanesMultiplePanes() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.allPanes();
        Iterable<ValueInSingleWindow<Integer>> onlyOnTime = ImmutableList.of(ValueInSingleWindow.of(8, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, LATE, 2L, 1L)), ValueInSingleWindow.of(4, new Instant(0L), INSTANCE, PaneInfo.createPane(false, false, ON_TIME, 1L, 0L)), ValueInSingleWindow.of(1, new Instant(0L), INSTANCE, PaneInfo.createPane(true, false, EARLY)));
        Assert.assertThat(extractor.apply(onlyOnTime), Matchers.containsInAnyOrder(4, 8, 1));
    }

    @Test
    public void allPanesEmpty() {
        SerializableFunction<Iterable<ValueInSingleWindow<Integer>>, Iterable<Integer>> extractor = PaneExtractors.allPanes();
        Iterable<ValueInSingleWindow<Integer>> noPanes = ImmutableList.of();
        Assert.assertThat(extractor.apply(noPanes), Matchers.emptyIterable());
    }
}

