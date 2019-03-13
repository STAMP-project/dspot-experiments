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


import BoundedWindow.TIMESTAMP_MAX_VALUE;
import BoundedWindow.TIMESTAMP_MIN_VALUE;
import GlobalWindow.Coder.INSTANCE;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.beam.runners.core.StateNamespace;
import org.apache.beam.runners.core.StateNamespaces;
import org.apache.beam.runners.core.TimerInternals.TimerData;
import org.apache.beam.sdk.coders.Coder;
import org.apache.beam.sdk.state.TimeDomain;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.hamcrest.Matchers;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link WindmillTimerInternals}.
 */
@RunWith(JUnit4.class)
public class WindmillTimerInternalsTest {
    private static final List<KV<Coder<? extends BoundedWindow>, StateNamespace>> TEST_NAMESPACES_WITH_CODERS = ImmutableList.of(KV.of(null, StateNamespaces.global()), KV.of(INSTANCE, StateNamespaces.window(INSTANCE, GlobalWindow.INSTANCE)), KV.of(IntervalWindow.getCoder(), StateNamespaces.window(IntervalWindow.getCoder(), new IntervalWindow(new Instant(13), new Instant(47)))));

    private static final List<Instant> TEST_TIMESTAMPS = ImmutableList.of(TIMESTAMP_MIN_VALUE, TIMESTAMP_MAX_VALUE, GlobalWindow.INSTANCE.maxTimestamp(), new Instant(0), new Instant(127));

    private static final List<String> TEST_STATE_FAMILIES = ImmutableList.of("", "F24");

    private static final List<String> TEST_TIMER_IDS = ImmutableList.of("", "foo", "this one has spaces", "this/one/has/slashes", "/");

    @Test
    public void testTimerDataToFromTimer() {
        for (String stateFamily : WindmillTimerInternalsTest.TEST_STATE_FAMILIES) {
            for (KV<Coder<? extends BoundedWindow>, StateNamespace> coderAndNamespace : WindmillTimerInternalsTest.TEST_NAMESPACES_WITH_CODERS) {
                @Nullable
                Coder<? extends BoundedWindow> coder = coderAndNamespace.getKey();
                StateNamespace namespace = coderAndNamespace.getValue();
                for (TimeDomain timeDomain : TimeDomain.values()) {
                    for (WindmillNamespacePrefix prefix : WindmillNamespacePrefix.values()) {
                        for (Instant timestamp : WindmillTimerInternalsTest.TEST_TIMESTAMPS) {
                            TimerData anonymousTimerData = TimerData.of(namespace, timestamp, timeDomain);
                            Assert.assertThat(WindmillTimerInternals.windmillTimerToTimerData(prefix, WindmillTimerInternals.timerDataToWindmillTimer(stateFamily, prefix, anonymousTimerData), coder), Matchers.equalTo(anonymousTimerData));
                            for (String timerId : WindmillTimerInternalsTest.TEST_TIMER_IDS) {
                                TimerData timerData = TimerData.of(timerId, namespace, timestamp, timeDomain);
                                Assert.assertThat(WindmillTimerInternals.windmillTimerToTimerData(prefix, WindmillTimerInternals.timerDataToWindmillTimer(stateFamily, prefix, timerData), coder), Matchers.equalTo(timerData));
                            }
                        }
                    }
                }
            }
        }
    }
}

