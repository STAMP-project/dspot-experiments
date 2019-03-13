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
package org.apache.beam.runners.dataflow.worker.util;


import PaneInfo.NO_FIRING;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.runners.core.InMemoryStateInternals;
import org.apache.beam.runners.core.StateInternals;
import org.apache.beam.runners.core.StateInternalsFactory;
import org.apache.beam.runners.core.StepContext;
import org.apache.beam.runners.core.TimerInternals;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.util.WindowedValue;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Unit tests for {@link BatchGroupAlsoByWindowReshuffleFn}.
 *
 * <p>Note the absence of tests for sessions, as merging window functions are not supported.
 */
@RunWith(JUnit4.class)
public class BatchGroupAlsoByWindowReshuffleDoFnTest {
    @Rule
    public final transient ExpectedException thrown = ExpectedException.none();

    private static final String STEP_NAME = "GABWStep";

    private class GABWReshuffleDoFnFactory implements GroupAlsoByWindowProperties.GroupAlsoByWindowDoFnFactory<String, String, Iterable<String>> {
        @Override
        public <W extends BoundedWindow> BatchGroupAlsoByWindowFn<String, String, Iterable<String>> forStrategy(WindowingStrategy<?, W> windowingStrategy, StateInternalsFactory<String> stateInternalsFactory) {
            // ignores windowing strategy.
            return new BatchGroupAlsoByWindowReshuffleFn<String, String, W>();
        }
    }

    @Test
    public void testEmptyInputEmptyOutput() throws Exception {
        GroupAlsoByWindowProperties.emptyInputEmptyOutput(new BatchGroupAlsoByWindowReshuffleDoFnTest.GABWReshuffleDoFnFactory());
    }

    /**
     * Tests that for a simple sequence of elements on the same key, {@link BatchGroupAlsoByWindowReshuffleFn} fires each element in a single pane.
     */
    @Test
    public void testReshuffleFiresEveryElement() throws Exception {
        BatchGroupAlsoByWindowReshuffleDoFnTest.GABWReshuffleDoFnFactory gabwFactory = new BatchGroupAlsoByWindowReshuffleDoFnTest.GABWReshuffleDoFnFactory();
        WindowingStrategy<?, IntervalWindow> windowingStrategy = WindowingStrategy.of(FixedWindows.of(Duration.millis(10)));
        List<WindowedValue<KV<String, Iterable<String>>>> result = BatchGroupAlsoByWindowReshuffleDoFnTest.runGABW(gabwFactory, windowingStrategy, "key", WindowedValue.of("v1", new Instant(1), Arrays.asList(BatchGroupAlsoByWindowReshuffleDoFnTest.window(0, 10)), NO_FIRING), WindowedValue.of("v2", new Instant(2), Arrays.asList(BatchGroupAlsoByWindowReshuffleDoFnTest.window(0, 10)), NO_FIRING), WindowedValue.of("v3", new Instant(13), Arrays.asList(BatchGroupAlsoByWindowReshuffleDoFnTest.window(10, 20)), NO_FIRING));
        Assert.assertThat(result.size(), Matchers.equalTo(3));
        WindowedValue<KV<String, Iterable<String>>> item0 = result.get(0);
        Assert.assertThat(item0.getValue().getValue(), Matchers.contains("v1"));
        Assert.assertThat(item0.getTimestamp(), Matchers.equalTo(new Instant(1)));
        Assert.assertThat(item0.getWindows(), Matchers.contains(BatchGroupAlsoByWindowReshuffleDoFnTest.window(0, 10)));
        WindowedValue<KV<String, Iterable<String>>> item1 = result.get(1);
        Assert.assertThat(item1.getValue().getValue(), Matchers.contains("v2"));
        Assert.assertThat(item1.getTimestamp(), Matchers.equalTo(new Instant(2)));
        Assert.assertThat(item1.getWindows(), Matchers.contains(BatchGroupAlsoByWindowReshuffleDoFnTest.window(0, 10)));
        WindowedValue<KV<String, Iterable<String>>> item2 = result.get(2);
        Assert.assertThat(item2.getValue().getValue(), Matchers.contains("v3"));
        Assert.assertThat(item2.getTimestamp(), Matchers.equalTo(new Instant(13)));
        Assert.assertThat(item2.getWindows(), Matchers.contains(BatchGroupAlsoByWindowReshuffleDoFnTest.window(10, 20)));
    }

    private static final class TestStepContext implements StepContext {
        private StateInternals stateInternals;

        private TestStepContext(String stepName) {
            this.stateInternals = InMemoryStateInternals.forKey(stepName);
        }

        @Override
        public TimerInternals timerInternals() {
            throw new UnsupportedOperationException();
        }

        @Override
        public StateInternals stateInternals() {
            return stateInternals;
        }
    }
}

