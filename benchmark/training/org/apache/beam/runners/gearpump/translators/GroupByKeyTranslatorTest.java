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
package org.apache.beam.runners.gearpump.translators;


import GroupByKeyTranslator.KeyedByTimestamp;
import GroupByKeyTranslator.Merge;
import PaneInfo.NO_FIRING;
import io.gearpump.streaming.dsl.window.api.WindowFunction;
import io.gearpump.streaming.dsl.window.impl.Window;
import java.time.org.joda.time.Instant;
import java.util.Collection;
import java.util.List;
import org.apache.beam.runners.gearpump.translators.GroupByKeyTranslator.GearpumpWindowFn;
import org.apache.beam.runners.gearpump.translators.utils.TranslatorUtils;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.Sessions;
import org.apache.beam.sdk.transforms.windowing.TimestampCombiner;
import org.apache.beam.sdk.transforms.windowing.WindowFn;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.Lists;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Tests for {@link GroupByKeyTranslator}.
 */
@RunWith(Parameterized.class)
public class GroupByKeyTranslatorTest {
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testGearpumpWindowFn() {
        GearpumpWindowFn windowFn = new GearpumpWindowFn(true);
        List<BoundedWindow> windows = Lists.newArrayList(new IntervalWindow(new Instant(0), new Instant(10)), new IntervalWindow(new Instant(5), new Instant(15)));
        WindowFunction.Context<WindowedValue<String>> context = new WindowFunction.Context<WindowedValue<String>>() {
            @Override
            public java.time.Instant timestamp() {
                return Instant.EPOCH;
            }

            @Override
            public WindowedValue<String> element() {
                return WindowedValue.of("v1", new Instant(6), windows, PaneInfo.NO_FIRING);
            }
        };
        Window[] result = windowFn.apply(context);
        List<Window> expected = Lists.newArrayList();
        for (BoundedWindow w : windows) {
            expected.add(TranslatorUtils.boundedWindowToGearpumpWindow(w));
        }
        Assert.assertThat(result, Matchers.equalTo(expected.toArray()));
    }

    @Parameterized.Parameter(0)
    public TimestampCombiner timestampCombiner;

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testKeyedByTimestamp() {
        WindowFn slidingWindows = Sessions.withGapDuration(Duration.millis(10));
        BoundedWindow window = new IntervalWindow(new Instant(0), new Instant(10));
        GroupByKeyTranslator.KeyedByTimestamp keyedByTimestamp = new GroupByKeyTranslator.KeyedByTimestamp(slidingWindows, timestampCombiner);
        WindowedValue<KV<String, String>> value = WindowedValue.of(KV.of("key", "val"), org.joda.time.Instant.now(), window, NO_FIRING);
        KV<Instant, WindowedValue<KV<String, String>>> result = keyedByTimestamp.map(value);
        Instant time = timestampCombiner.assign(window, slidingWindows.getOutputTime(value.getTimestamp(), window));
        Assert.assertThat(result, Matchers.equalTo(KV.of(time, value)));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testMerge() {
        WindowFn slidingWindows = Sessions.withGapDuration(Duration.millis(10));
        GroupByKeyTranslator.Merge merge = new GroupByKeyTranslator.Merge(slidingWindows, timestampCombiner);
        Instant key1 = new Instant(5);
        WindowedValue<KV<String, String>> value1 = WindowedValue.of(KV.of("key1", "value1"), key1, new IntervalWindow(new Instant(5), new Instant(10)), NO_FIRING);
        Instant key2 = new Instant(10);
        WindowedValue<KV<String, String>> value2 = WindowedValue.of(KV.of("key2", "value2"), key2, new IntervalWindow(new Instant(9), new Instant(14)), NO_FIRING);
        KV<Instant, WindowedValue<KV<String, List<String>>>> result1 = merge.fold(KV.<Instant, WindowedValue<KV<String, List<String>>>>of(null, null), KV.of(key1, value1));
        Assert.assertThat(result1.getKey(), Matchers.equalTo(key1));
        Assert.assertThat(result1.getValue().getValue().getValue(), Matchers.equalTo(Lists.newArrayList("value1")));
        KV<Instant, WindowedValue<KV<String, List<String>>>> result2 = merge.fold(result1, KV.of(key2, value2));
        Assert.assertThat(result2.getKey(), Matchers.equalTo(timestampCombiner.combine(key1, key2)));
        Collection<? extends BoundedWindow> resultWindows = result2.getValue().getWindows();
        Assert.assertThat(resultWindows.size(), Matchers.equalTo(1));
        IntervalWindow expectedWindow = new IntervalWindow(new Instant(5), new Instant(14));
        Assert.assertThat(resultWindows.toArray()[0], Matchers.equalTo(expectedWindow));
        Assert.assertThat(result2.getValue().getValue().getValue(), Matchers.equalTo(Lists.newArrayList("value1", "value2")));
    }
}

