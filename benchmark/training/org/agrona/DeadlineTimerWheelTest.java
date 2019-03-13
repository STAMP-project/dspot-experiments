/**
 * Copyright 2014-2019 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.agrona;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;
import org.agrona.collections.MutableLong;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;


public class DeadlineTimerWheelTest {
    private static final TimeUnit TIME_UNIT = TimeUnit.NANOSECONDS;

    private static final int RESOLUTION = BitUtil.findNextPositivePowerOfTwo(((int) (TimeUnit.MILLISECONDS.toNanos(1))));

    @Test(expected = IllegalArgumentException.class)
    public void shouldExceptionOnNonPowerOfTwoTicksPerWheel() {
        new DeadlineTimerWheel(TimeUnit.NANOSECONDS, 0, 16, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldExceptionOnNonPowerOfTwoResolution() {
        new DeadlineTimerWheel(TimeUnit.NANOSECONDS, 0, 17, 8);
    }

    @Test(timeout = 1000)
    public void shouldBeAbleToScheduleTimerOnEdgeOfTick() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 1024);
        final long id = wheel.scheduleTimer((5 * (wheel.tickResolution())));
        do {
            wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                assertThat(timerId, is(id));
                firedTimestamp.value = now;
                return true;
            }, Integer.MAX_VALUE);
            controlTimestamp += wheel.tickResolution();
        } while ((-1) == (firedTimestamp.value) );
        // this is the first tick after the timer, so it should be on this edge
        MatcherAssert.assertThat(firedTimestamp.value, Matchers.is((6 * (wheel.tickResolution()))));
    }

    @Test(timeout = 1000)
    public void shouldHandleNonZeroStartTime() {
        long controlTimestamp = 100 * (DeadlineTimerWheelTest.RESOLUTION);
        final MutableLong firedTimestamp = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 1024);
        final long id = wheel.scheduleTimer((controlTimestamp + (5 * (wheel.tickResolution()))));
        do {
            wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                assertThat(timerId, is(id));
                firedTimestamp.value = now;
                return true;
            }, Integer.MAX_VALUE);
            controlTimestamp += wheel.tickResolution();
        } while ((-1) == (firedTimestamp.value) );
        // this is the first tick after the timer, so it should be on this edge
        MatcherAssert.assertThat(firedTimestamp.value, Matchers.is((106 * (wheel.tickResolution()))));
    }

    @Test(timeout = 1000)
    public void shouldHandleNanoTimeUnitTimers() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 1024);
        final long id = wheel.scheduleTimer(((controlTimestamp + (5 * (wheel.tickResolution()))) + 1));
        do {
            wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                assertThat(timerId, is(id));
                firedTimestamp.value = now;
                return true;
            }, Integer.MAX_VALUE);
            controlTimestamp += wheel.tickResolution();
        } while ((-1) == (firedTimestamp.value) );
        // this is the first tick after the timer, so it should be on this edge
        MatcherAssert.assertThat(firedTimestamp.value, Matchers.is((6 * (wheel.tickResolution()))));
    }

    @Test(timeout = 1000)
    public void shouldHandleMultipleRounds() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 16);
        final long id = wheel.scheduleTimer((controlTimestamp + (63 * (wheel.tickResolution()))));
        do {
            wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                assertThat(timerId, is(id));
                firedTimestamp.value = now;
                return true;
            }, Integer.MAX_VALUE);
            controlTimestamp += wheel.tickResolution();
        } while ((-1) == (firedTimestamp.value) );
        // this is the first tick after the timer, so it should be on this edge
        MatcherAssert.assertThat(firedTimestamp.value, Matchers.is((64 * (wheel.tickResolution()))));
    }

    @Test(timeout = 1000)
    public void shouldBeAbleToCancelTimer() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 256);
        final long id = wheel.scheduleTimer((controlTimestamp + (63 * (wheel.tickResolution()))));
        do {
            wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                assertThat(timerId, is(id));
                firedTimestamp.value = now;
                return true;
            }, Integer.MAX_VALUE);
            controlTimestamp += wheel.tickResolution();
        } while (((-1) == (firedTimestamp.value)) && (controlTimestamp < (16 * (wheel.tickResolution()))) );
        TestCase.assertTrue(wheel.cancelTimer(id));
        TestCase.assertFalse(wheel.cancelTimer(id));
        do {
            wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                firedTimestamp.value = now;
                return true;
            }, Integer.MAX_VALUE);
            controlTimestamp += wheel.tickResolution();
        } while (((-1) == (firedTimestamp.value)) && (controlTimestamp < (128 * (wheel.tickResolution()))) );
        MatcherAssert.assertThat(firedTimestamp.value, Matchers.is((-1L)));
    }

    @Test(timeout = 1000)
    public void shouldHandleExpiringTimersInPreviousTicks() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 256);
        final long id = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        final long pollStartTimeNs = 32 * (wheel.tickResolution());
        controlTimestamp += pollStartTimeNs;
        do {
            wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                assertThat(timerId, is(id));
                firedTimestamp.value = now;
                return true;
            }, Integer.MAX_VALUE);
            if ((wheel.currentTickTime()) > pollStartTimeNs) {
                controlTimestamp += wheel.tickResolution();
            }
        } while (((-1) == (firedTimestamp.value)) && (controlTimestamp < (128 * (wheel.tickResolution()))) );
        MatcherAssert.assertThat(firedTimestamp.value, Matchers.is(pollStartTimeNs));
    }

    @Test(timeout = 1000)
    public void shouldHandleMultipleTimersInDifferentTicks() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp1 = new MutableLong((-1));
        final MutableLong firedTimestamp2 = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 256);
        final long id1 = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        final long id2 = wheel.scheduleTimer((controlTimestamp + (23 * (wheel.tickResolution()))));
        do {
            wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                if (timerId == id1) {
                    firedTimestamp1.value = now;
                } else
                    if (timerId == id2) {
                        firedTimestamp2.value = now;
                    }

                return true;
            }, Integer.MAX_VALUE);
            controlTimestamp += wheel.tickResolution();
        } while (((-1) == (firedTimestamp1.value)) || ((-1) == (firedTimestamp2.value)) );
        MatcherAssert.assertThat(firedTimestamp1.value, Matchers.is((16 * (wheel.tickResolution()))));
        MatcherAssert.assertThat(firedTimestamp2.value, Matchers.is((24 * (wheel.tickResolution()))));
    }

    @Test(timeout = 1000)
    public void shouldHandleMultipleTimersInSameTickSameRound() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp1 = new MutableLong((-1));
        final MutableLong firedTimestamp2 = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 8);
        final long id1 = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        final long id2 = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        do {
            wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                if (timerId == id1) {
                    firedTimestamp1.value = now;
                } else
                    if (timerId == id2) {
                        firedTimestamp2.value = now;
                    }

                return true;
            }, Integer.MAX_VALUE);
            controlTimestamp += wheel.tickResolution();
        } while (((-1) == (firedTimestamp1.value)) || ((-1) == (firedTimestamp2.value)) );
        MatcherAssert.assertThat(firedTimestamp1.value, Matchers.is((16 * (wheel.tickResolution()))));
        MatcherAssert.assertThat(firedTimestamp2.value, Matchers.is((16 * (wheel.tickResolution()))));
    }

    @Test(timeout = 1000)
    public void shouldHandleMultipleTimersInSameTickDifferentRound() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp1 = new MutableLong((-1));
        final MutableLong firedTimestamp2 = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 8);
        final long id1 = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        final long id2 = wheel.scheduleTimer((controlTimestamp + (23 * (wheel.tickResolution()))));
        do {
            wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                if (timerId == id1) {
                    firedTimestamp1.value = now;
                } else
                    if (timerId == id2) {
                        firedTimestamp2.value = now;
                    }

                return true;
            }, Integer.MAX_VALUE);
            controlTimestamp += wheel.tickResolution();
        } while (((-1) == (firedTimestamp1.value)) || ((-1) == (firedTimestamp2.value)) );
        MatcherAssert.assertThat(firedTimestamp1.value, Matchers.is((16 * (wheel.tickResolution()))));
        MatcherAssert.assertThat(firedTimestamp2.value, Matchers.is((24 * (wheel.tickResolution()))));
    }

    @Test(timeout = 1000)
    public void shouldLimitExpiringTimers() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp1 = new MutableLong((-1));
        final MutableLong firedTimestamp2 = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 8);
        final long id1 = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        final long id2 = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        int numExpired = 0;
        do {
            numExpired += wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                assertThat(timerId, is(id1));
                firedTimestamp1.value = now;
                return true;
            }, 1);
            controlTimestamp += wheel.tickResolution();
        } while (((-1) == (firedTimestamp1.value)) && ((-1) == (firedTimestamp2.value)) );
        MatcherAssert.assertThat(numExpired, Matchers.is(1));
        do {
            numExpired += wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                assertThat(timerId, is(id2));
                firedTimestamp2.value = now;
                return true;
            }, 1);
            controlTimestamp += wheel.tickResolution();
        } while (((-1) == (firedTimestamp1.value)) && ((-1) == (firedTimestamp2.value)) );
        MatcherAssert.assertThat(numExpired, Matchers.is(2));
        MatcherAssert.assertThat(firedTimestamp1.value, Matchers.is((16 * (wheel.tickResolution()))));
        MatcherAssert.assertThat(firedTimestamp2.value, Matchers.is((17 * (wheel.tickResolution()))));
    }

    @Test(timeout = 1000)
    public void shouldHandleFalseReturnToExpireTimerAgain() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp1 = new MutableLong((-1));
        final MutableLong firedTimestamp2 = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 8);
        final long id1 = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        final long id2 = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        int numExpired = 0;
        do {
            numExpired += wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                if (timerId == id1) {
                    if ((-1) == firedTimestamp1.value) {
                        firedTimestamp1.value = now;
                        return false;
                    }
                    firedTimestamp1.value = now;
                } else
                    if (timerId == id2) {
                        firedTimestamp2.value = now;
                    }

                return true;
            }, Integer.MAX_VALUE);
            controlTimestamp += wheel.tickResolution();
        } while (((-1) == (firedTimestamp1.value)) || ((-1) == (firedTimestamp2.value)) );
        MatcherAssert.assertThat(firedTimestamp1.value, Matchers.is((17 * (wheel.tickResolution()))));
        MatcherAssert.assertThat(firedTimestamp2.value, Matchers.is((17 * (wheel.tickResolution()))));
        MatcherAssert.assertThat(numExpired, Matchers.is(3));
    }

    @Test(timeout = 1000)
    public void shouldCopeWithExceptionFromHandler() {
        long controlTimestamp = 0;
        final MutableLong firedTimestamp1 = new MutableLong((-1));
        final MutableLong firedTimestamp2 = new MutableLong((-1));
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 8);
        final long id1 = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        final long id2 = wheel.scheduleTimer((controlTimestamp + (15 * (wheel.tickResolution()))));
        int numExpired = 0;
        Exception e = null;
        do {
            try {
                numExpired += wheel.poll(controlTimestamp, ( timeUnit, now, timerId) -> {
                    if (timerId == id1) {
                        firedTimestamp1.value = now;
                        throw new IllegalStateException();
                    } else
                        if (timerId == id2) {
                            firedTimestamp2.value = now;
                        }

                    return true;
                }, Integer.MAX_VALUE);
                controlTimestamp += wheel.tickResolution();
            } catch (final Exception ex) {
                e = ex;
            }
        } while (((-1) == (firedTimestamp1.value)) || ((-1) == (firedTimestamp2.value)) );
        MatcherAssert.assertThat(firedTimestamp1.value, Matchers.is((16 * (wheel.tickResolution()))));
        MatcherAssert.assertThat(firedTimestamp2.value, Matchers.is((16 * (wheel.tickResolution()))));
        MatcherAssert.assertThat(numExpired, Matchers.is(1));
        MatcherAssert.assertThat(wheel.timerCount(), Matchers.is(0L));
        Assert.assertNotNull(e);
    }

    @Test(timeout = 1000)
    public void shouldBeAbleToIterateOverTimers() {
        final long controlTimestamp = 0;
        final DeadlineTimerWheel wheel = new DeadlineTimerWheel(DeadlineTimerWheelTest.TIME_UNIT, controlTimestamp, DeadlineTimerWheelTest.RESOLUTION, 8);
        final long deadline1 = controlTimestamp + (15 * (wheel.tickResolution()));
        final long deadline2 = controlTimestamp + ((15 + 7) * (wheel.tickResolution()));
        final long id1 = wheel.scheduleTimer(deadline1);
        final long id2 = wheel.scheduleTimer(deadline2);
        final Map<Long, Long> timerIdByDeadlineMap = new HashMap<>();
        wheel.forEach(timerIdByDeadlineMap::put);
        MatcherAssert.assertThat(timerIdByDeadlineMap.size(), Matchers.is(2));
        MatcherAssert.assertThat(timerIdByDeadlineMap.get(deadline1), Matchers.is(id1));
        MatcherAssert.assertThat(timerIdByDeadlineMap.get(deadline2), Matchers.is(id2));
    }
}

