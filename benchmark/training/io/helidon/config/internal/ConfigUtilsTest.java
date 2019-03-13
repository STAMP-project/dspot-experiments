/**
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.helidon.config.internal;


import io.helidon.config.internal.ConfigUtils.ScheduledTask;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Priority;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link ConfigUtils}.
 */
public class ConfigUtilsTest {
    @Test
    public void testAsStream() {
        testAsStream(Arrays.asList(20, 0, 30, 10));
        testAsStream(Arrays.asList(10, 30, 0, 20));
        testAsStream(Arrays.asList(0, 10, 20, 30));
    }

    @Test
    public void testAsPrioritizedStream() {
        testAsPrioritizedStream(Arrays.asList(new ConfigUtilsTest.Provider1(), new ConfigUtilsTest.Provider2(), new ConfigUtilsTest.Provider3(), new ConfigUtilsTest.Provider4()));
        testAsPrioritizedStream(Arrays.asList(new ConfigUtilsTest.Provider4(), new ConfigUtilsTest.Provider3(), new ConfigUtilsTest.Provider2(), new ConfigUtilsTest.Provider1()));
        testAsPrioritizedStream(Arrays.asList(new ConfigUtilsTest.Provider2(), new ConfigUtilsTest.Provider4(), new ConfigUtilsTest.Provider1(), new ConfigUtilsTest.Provider3()));
    }

    @Test
    public void testScheduledTaskInterruptedRepeatedly() throws InterruptedException {
        AtomicInteger counter = new AtomicInteger();
        ScheduledTask task = new ScheduledTask(Executors.newSingleThreadScheduledExecutor(), counter::incrementAndGet, Duration.ofMillis(80));
        task.schedule();
        task.schedule();
        task.schedule();
        task.schedule();
        task.schedule();
        // not yet finished
        MatcherAssert.assertThat(counter.get(), Matchers.is(0));
        TimeUnit.MILLISECONDS.sleep(120);
        MatcherAssert.assertThat(counter.get(), Matchers.is(1));
    }

    @Test
    public void testScheduledTaskExecutedRepeatedly() throws InterruptedException {
        CountDownLatch execLatch = new CountDownLatch(5);
        ScheduledTask task = new ScheduledTask(Executors.newSingleThreadScheduledExecutor(), execLatch::countDown, Duration.ZERO);
        /* Because invoking 'schedule' can cancel an existing action, keep track
        of cancelations in case the latch expires without reaching 0.
         */
        final long RESCHEDULE_DELAY_MS = 5;
        final int ACTIONS_TO_SCHEDULE = 5;
        int cancelations = 0;
        for (int i = 0; i < ACTIONS_TO_SCHEDULE; i++) {
            if (task.schedule()) {
                cancelations++;
            }
            TimeUnit.MILLISECONDS.sleep(RESCHEDULE_DELAY_MS);
        }
        /* The latch can either complete -- because all the scheduled actions finished --
        or it can expire at the timeout because at least one action did not finish, in
        which case the remaining latch value should not exceed the number of actions
        canceled. (Do not check for exact equality; some attempts to cancel
        an action might occur after the action was deemed to be not-yet-run or in-progress
        but actually runs to completion before the cancel is actually invoked.
         */
        MatcherAssert.assertThat((((("Current execLatch count: " + (execLatch.getCount())) + ", cancelations: ") + "") + cancelations), ((execLatch.await(3000, TimeUnit.MILLISECONDS)) || ((execLatch.getCount()) <= cancelations)), Matchers.is(true));
    }

    // 
    // providers ...
    // 
    interface Provider {}

    @Priority(20)
    static class Provider1 implements ConfigUtilsTest.Provider {}

    static class Provider2 implements ConfigUtilsTest.Provider {}

    @Priority(30)
    static class Provider3 implements ConfigUtilsTest.Provider {}

    @Priority(10)
    static class Provider4 implements ConfigUtilsTest.Provider {}
}

