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
package org.apache.beam.runners.direct.portable;


import BoundedWindow.TIMESTAMP_MAX_VALUE;
import GlobalWindow.INSTANCE;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.beam.runners.core.construction.graph.PipelineNode.PTransformNode;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.IntervalWindow;
import org.apache.beam.sdk.transforms.windowing.WindowFn;
import org.apache.beam.sdk.values.WindowingStrategy;
import org.hamcrest.Matchers;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link WatermarkCallbackExecutor}.
 */
@RunWith(JUnit4.class)
public class WatermarkCallbackExecutorTest {
    private WatermarkCallbackExecutor executor = WatermarkCallbackExecutor.create(Executors.newSingleThreadExecutor());

    private PTransformNode create;

    private PTransformNode sum;

    @Test
    public void onGuaranteedFiringFiresAfterTrigger() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        executor.callOnGuaranteedFiring(create, INSTANCE, WindowingStrategy.globalDefault(), new WatermarkCallbackExecutorTest.CountDownLatchCallback(latch));
        executor.fireForWatermark(create, TIMESTAMP_MAX_VALUE);
        Assert.assertThat(latch.await(500, TimeUnit.MILLISECONDS), Matchers.equalTo(true));
    }

    @Test
    public void multipleCallbacksShouldFireFires() throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        WindowFn<Object, IntervalWindow> windowFn = FixedWindows.of(Duration.standardMinutes(10));
        IntervalWindow window = new IntervalWindow(new Instant(0L), new Instant(0L).plus(Duration.standardMinutes(10)));
        executor.callOnGuaranteedFiring(create, window, WindowingStrategy.of(windowFn), new WatermarkCallbackExecutorTest.CountDownLatchCallback(latch));
        executor.callOnGuaranteedFiring(create, window, WindowingStrategy.of(windowFn), new WatermarkCallbackExecutorTest.CountDownLatchCallback(latch));
        executor.fireForWatermark(create, new Instant(0L).plus(Duration.standardMinutes(10)));
        Assert.assertThat(latch.await(500, TimeUnit.MILLISECONDS), Matchers.equalTo(true));
    }

    @Test
    public void noCallbacksShouldFire() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        WindowFn<Object, IntervalWindow> windowFn = FixedWindows.of(Duration.standardMinutes(10));
        IntervalWindow window = new IntervalWindow(new Instant(0L), new Instant(0L).plus(Duration.standardMinutes(10)));
        executor.callOnGuaranteedFiring(create, window, WindowingStrategy.of(windowFn), new WatermarkCallbackExecutorTest.CountDownLatchCallback(latch));
        executor.fireForWatermark(create, new Instant(0L).plus(Duration.standardMinutes(5)));
        Assert.assertThat(latch.await(500, TimeUnit.MILLISECONDS), Matchers.equalTo(false));
    }

    @Test
    public void unrelatedStepShouldNotFire() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        WindowFn<Object, IntervalWindow> windowFn = FixedWindows.of(Duration.standardMinutes(10));
        IntervalWindow window = new IntervalWindow(new Instant(0L), new Instant(0L).plus(Duration.standardMinutes(10)));
        executor.callOnGuaranteedFiring(sum, window, WindowingStrategy.of(windowFn), new WatermarkCallbackExecutorTest.CountDownLatchCallback(latch));
        executor.fireForWatermark(create, new Instant(0L).plus(Duration.standardMinutes(20)));
        Assert.assertThat(latch.await(500, TimeUnit.MILLISECONDS), Matchers.equalTo(false));
    }

    private static class CountDownLatchCallback implements Runnable {
        private final CountDownLatch latch;

        public CountDownLatchCallback(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            latch.countDown();
        }
    }
}

