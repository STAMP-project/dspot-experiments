/**
 * Copyright 2018 The gRPC Authors
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
package io.grpc.internal;


import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link Rescheduler}.
 */
@RunWith(JUnit4.class)
public class ReschedulerTest {
    private final ReschedulerTest.Runner runner = new ReschedulerTest.Runner();

    private final ReschedulerTest.Exec exec = new ReschedulerTest.Exec();

    private final FakeClock scheduler = new FakeClock();

    private final Rescheduler rescheduler = new Rescheduler(runner, exec, scheduler.getScheduledExecutorService(), scheduler.getStopwatchSupplier().get());

    @Test
    public void runs() {
        Assert.assertFalse(runner.ran);
        rescheduler.reschedule(1, TimeUnit.NANOSECONDS);
        Assert.assertFalse(runner.ran);
        scheduler.forwardNanos(1);
        Assert.assertTrue(runner.ran);
    }

    @Test
    public void cancels() {
        Assert.assertFalse(runner.ran);
        rescheduler.reschedule(1, TimeUnit.NANOSECONDS);
        Assert.assertFalse(runner.ran);
        /* permanent= */
        rescheduler.cancel(false);
        scheduler.forwardNanos(1);
        Assert.assertFalse(runner.ran);
        Assert.assertTrue(exec.executed);
    }

    @Test
    public void cancelPermanently() {
        Assert.assertFalse(runner.ran);
        rescheduler.reschedule(1, TimeUnit.NANOSECONDS);
        Assert.assertFalse(runner.ran);
        /* permanent= */
        rescheduler.cancel(true);
        scheduler.forwardNanos(1);
        Assert.assertFalse(runner.ran);
        Assert.assertFalse(exec.executed);
    }

    @Test
    public void reschedules() {
        Assert.assertFalse(runner.ran);
        rescheduler.reschedule(1, TimeUnit.NANOSECONDS);
        Assert.assertFalse(runner.ran);
        Assert.assertFalse(exec.executed);
        rescheduler.reschedule(50, TimeUnit.NANOSECONDS);
        Assert.assertFalse(runner.ran);
        Assert.assertFalse(exec.executed);
        scheduler.forwardNanos(1);
        Assert.assertFalse(runner.ran);
        Assert.assertTrue(exec.executed);
        scheduler.forwardNanos(50);
        Assert.assertTrue(runner.ran);
    }

    @Test
    public void reschedulesShortDelay() {
        Assert.assertFalse(runner.ran);
        rescheduler.reschedule(50, TimeUnit.NANOSECONDS);
        Assert.assertFalse(runner.ran);
        Assert.assertFalse(exec.executed);
        rescheduler.reschedule(1, TimeUnit.NANOSECONDS);
        Assert.assertFalse(runner.ran);
        Assert.assertFalse(exec.executed);
        scheduler.forwardNanos(1);
        Assert.assertTrue(runner.ran);
        Assert.assertTrue(exec.executed);
    }

    private static final class Exec implements Executor {
        boolean executed;

        @Override
        public void execute(Runnable command) {
            executed = true;
            command.run();
        }
    }

    private static final class Runner implements Runnable {
        boolean ran;

        @Override
        public void run() {
            ran = true;
        }
    }
}

