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
package org.apache.beam.runners.flink.streaming;


import TimestampCombiner.EARLIEST;
import java.nio.ByteBuffer;
import org.apache.beam.runners.core.StateInternalsTest;
import org.apache.beam.runners.core.StateNamespaces;
import org.apache.beam.runners.core.StateTag;
import org.apache.beam.runners.core.StateTags;
import org.apache.beam.runners.flink.translation.wrappers.streaming.state.FlinkStateInternals;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.state.WatermarkHoldState;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.flink.runtime.state.KeyedStateBackend;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.joda.time.Instant;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link FlinkStateInternals}. This is based on {@link StateInternalsTest}.
 */
@RunWith(JUnit4.class)
public class FlinkStateInternalsTest extends StateInternalsTest {
    @Test
    public void testWatermarkHoldsPersistence() throws Exception {
        KeyedStateBackend<ByteBuffer> keyedStateBackend = createStateBackend();
        FlinkStateInternals stateInternals = new FlinkStateInternals(keyedStateBackend, StringUtf8Coder.of());
        StateTag<WatermarkHoldState> stateTag = StateTags.watermarkStateInternal("hold", EARLIEST);
        WatermarkHoldState globalWindow = stateInternals.state(StateNamespaces.global(), stateTag);
        WatermarkHoldState fixedWindow = stateInternals.state(StateNamespaces.window(IntervalWindow.getCoder(), new IntervalWindow(new Instant(0), new Instant(10))), stateTag);
        Instant noHold = new Instant(Long.MAX_VALUE);
        MatcherAssert.assertThat(stateInternals.watermarkHold(), Is.is(noHold));
        Instant high = new Instant(10);
        globalWindow.add(high);
        MatcherAssert.assertThat(stateInternals.watermarkHold(), Is.is(high));
        Instant middle = new Instant(5);
        fixedWindow.add(middle);
        MatcherAssert.assertThat(stateInternals.watermarkHold(), Is.is(middle));
        Instant low = new Instant(1);
        globalWindow.add(low);
        MatcherAssert.assertThat(stateInternals.watermarkHold(), Is.is(low));
        // Try to overwrite with later hold (should not succeed)
        globalWindow.add(high);
        MatcherAssert.assertThat(stateInternals.watermarkHold(), Is.is(low));
        fixedWindow.add(high);
        MatcherAssert.assertThat(stateInternals.watermarkHold(), Is.is(low));
        changeKey(keyedStateBackend);
        // Discard watermark view and recover it
        stateInternals = new FlinkStateInternals(keyedStateBackend, StringUtf8Coder.of());
        globalWindow = stateInternals.state(StateNamespaces.global(), stateTag);
        fixedWindow = stateInternals.state(StateNamespaces.window(IntervalWindow.getCoder(), new IntervalWindow(new Instant(0), new Instant(10))), stateTag);
        MatcherAssert.assertThat(stateInternals.watermarkHold(), Is.is(low));
        fixedWindow.clear();
        MatcherAssert.assertThat(stateInternals.watermarkHold(), Is.is(low));
        globalWindow.clear();
        MatcherAssert.assertThat(stateInternals.watermarkHold(), Is.is(noHold));
    }
}

