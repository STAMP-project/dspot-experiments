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
package org.apache.flink.runtime.executiongraph.restart;


import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.core.testutils.OneShotLatch;
import org.apache.flink.runtime.concurrent.ScheduledExecutor;
import org.apache.flink.runtime.concurrent.ScheduledExecutorServiceAdapter;
import org.junit.Assert;
import org.junit.Test;


/**
 * Unit test for the {@link FailureRateRestartStrategy}.
 */
public class FailureRateRestartStrategyTest {
    public final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(4);

    public final ScheduledExecutor executor = new ScheduledExecutorServiceAdapter(executorService);

    // ------------------------------------------------------------------------
    @Test
    public void testManyFailuresWithinRate() throws Exception {
        final int numAttempts = 10;
        final int intervalMillis = 1;
        final FailureRateRestartStrategy restartStrategy = new FailureRateRestartStrategy(1, Time.milliseconds(intervalMillis), Time.milliseconds(0));
        for (int attempsLeft = numAttempts; attempsLeft > 0; --attempsLeft) {
            Assert.assertTrue(restartStrategy.canRestart());
            restartStrategy.restart(new NoOpRestarter(), executor);
            FailureRateRestartStrategyTest.sleepGuaranteed((2 * intervalMillis));
        }
        Assert.assertTrue(restartStrategy.canRestart());
    }

    @Test
    public void testFailuresExceedingRate() throws Exception {
        final int numFailures = 3;
        final int intervalMillis = 10000;
        final FailureRateRestartStrategy restartStrategy = new FailureRateRestartStrategy(numFailures, Time.milliseconds(intervalMillis), Time.milliseconds(0));
        for (int failuresLeft = numFailures; failuresLeft > 0; --failuresLeft) {
            Assert.assertTrue(restartStrategy.canRestart());
            restartStrategy.restart(new NoOpRestarter(), executor);
        }
        // now the rate should be exceeded
        Assert.assertFalse(restartStrategy.canRestart());
    }

    @Test
    public void testDelay() throws Exception {
        final long restartDelay = 2;
        final int numberRestarts = 10;
        final FailureRateRestartStrategy strategy = new FailureRateRestartStrategy((numberRestarts + 1), Time.milliseconds(1), Time.milliseconds(restartDelay));
        for (int restartsLeft = numberRestarts; restartsLeft > 0; --restartsLeft) {
            Assert.assertTrue(strategy.canRestart());
            final OneShotLatch sync = new OneShotLatch();
            final RestartCallback restarter = new LatchedRestarter(sync);
            final long time = System.nanoTime();
            strategy.restart(restarter, executor);
            sync.await();
            final long elapsed = (System.nanoTime()) - time;
            Assert.assertTrue("Not enough delay", (elapsed >= (restartDelay * 1000000)));
        }
    }
}

