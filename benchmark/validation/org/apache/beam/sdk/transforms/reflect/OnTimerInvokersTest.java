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
package org.apache.beam.sdk.transforms.reflect;


import DoFnInvoker.ArgumentProvider;
import TimeDomain.PROCESSING_TIME;
import org.apache.beam.sdk.state.TimerSpec;
import org.apache.beam.sdk.state.TimerSpecs;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;


/**
 * Tests for {@link DoFnInvokers}.
 */
@RunWith(JUnit4.class)
public class OnTimerInvokersTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    private BoundedWindow mockWindow;

    @Mock
    private ArgumentProvider<String, String> mockArgumentProvider;

    @Test
    public void testOnTimerHelloWord() throws Exception {
        final String timerId = "my-timer-id";
        class SimpleTimerDoFn extends DoFn<String, String> {
            public String status = "not yet";

            @TimerId(timerId)
            private final TimerSpec myTimer = TimerSpecs.timer(PROCESSING_TIME);

            @ProcessElement
            public void process(ProcessContext c) {
            }

            @OnTimer(timerId)
            public void onMyTimer() {
                status = "OK now";
            }
        }
        SimpleTimerDoFn fn = new SimpleTimerDoFn();
        invokeOnTimer(fn, timerId);
        Assert.assertThat(fn.status, Matchers.equalTo("OK now"));
    }

    @Test
    public void testOnTimerWithWindow() throws Exception {
        OnTimerInvokersTest.WindowedTimerDoFn fn = new OnTimerInvokersTest.WindowedTimerDoFn();
        invokeOnTimer(fn, OnTimerInvokersTest.WindowedTimerDoFn.TIMER_ID);
        Assert.assertThat(fn.window, Matchers.theInstance(mockWindow));
    }

    private static class WindowedTimerDoFn extends DoFn<String, String> {
        public static final String TIMER_ID = "my-timer-id";

        public BoundedWindow window = null;

        @TimerId(OnTimerInvokersTest.WindowedTimerDoFn.TIMER_ID)
        private final TimerSpec myTimer = TimerSpecs.timer(PROCESSING_TIME);

        @ProcessElement
        public void process(ProcessContext c) {
        }

        @OnTimer(OnTimerInvokersTest.WindowedTimerDoFn.TIMER_ID)
        public void onMyTimer(BoundedWindow window) {
            this.window = window;
        }
    }

    static class StableNameTestDoFn extends DoFn<Void, Void> {
        private static final String TIMER_ID = "timer-id.with specialChars{}";

        @TimerId(OnTimerInvokersTest.StableNameTestDoFn.TIMER_ID)
        private final TimerSpec myTimer = TimerSpecs.timer(PROCESSING_TIME);

        @ProcessElement
        public void process() {
        }

        @OnTimer(OnTimerInvokersTest.StableNameTestDoFn.TIMER_ID)
        public void onMyTimer() {
        }
    }

    /**
     * This is a change-detector test that the generated name is stable across runs.
     */
    @Test
    public void testStableName() {
        OnTimerInvoker<Void, Void> invoker = OnTimerInvokers.forTimer(new OnTimerInvokersTest.StableNameTestDoFn(), OnTimerInvokersTest.StableNameTestDoFn.TIMER_ID);
        Assert.assertThat(invoker.getClass().getName(), Matchers.equalTo(/* alphanum only; human readable but not unique */
        /* base64 encoding of UTF-8 timerId */
        String.format("%s$%s$%s$%s", OnTimerInvokersTest.StableNameTestDoFn.class.getName(), OnTimerInvoker.class.getSimpleName(), "timeridwithspecialChars", "dGltZXItaWQud2l0aCBzcGVjaWFsQ2hhcnN7fQ")));
    }
}

