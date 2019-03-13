/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.common.utils;


import org.junit.Assert;
import org.junit.Test;


public class TimerTest {
    private final MockTime time = new MockTime();

    @Test
    public void testTimerUpdate() {
        Timer timer = timer(500);
        Assert.assertEquals(500, timer.remainingMs());
        Assert.assertEquals(0, timer.elapsedMs());
        time.sleep(100);
        timer.update();
        Assert.assertEquals(400, timer.remainingMs());
        Assert.assertEquals(100, timer.elapsedMs());
        time.sleep(400);
        timer.update(time.milliseconds());
        Assert.assertEquals(0, timer.remainingMs());
        Assert.assertEquals(500, timer.elapsedMs());
        Assert.assertTrue(timer.isExpired());
        // Going over the expiration is fine and the elapsed time can exceed
        // the initial timeout. However, remaining time should be stuck at 0.
        time.sleep(200);
        timer.update(time.milliseconds());
        Assert.assertTrue(timer.isExpired());
        Assert.assertEquals(0, timer.remainingMs());
        Assert.assertEquals(700, timer.elapsedMs());
    }

    @Test
    public void testTimerUpdateAndReset() {
        Timer timer = timer(500);
        timer.sleep(200);
        Assert.assertEquals(300, timer.remainingMs());
        Assert.assertEquals(200, timer.elapsedMs());
        timer.updateAndReset(400);
        Assert.assertEquals(400, timer.remainingMs());
        Assert.assertEquals(0, timer.elapsedMs());
        timer.sleep(400);
        Assert.assertTrue(timer.isExpired());
        timer.updateAndReset(200);
        Assert.assertEquals(200, timer.remainingMs());
        Assert.assertEquals(0, timer.elapsedMs());
        Assert.assertFalse(timer.isExpired());
    }

    @Test
    public void testTimerResetUsesCurrentTime() {
        Timer timer = timer(500);
        timer.sleep(200);
        Assert.assertEquals(300, timer.remainingMs());
        Assert.assertEquals(200, timer.elapsedMs());
        time.sleep(300);
        timer.reset(500);
        Assert.assertEquals(500, timer.remainingMs());
        timer.update();
        Assert.assertEquals(200, timer.remainingMs());
    }

    @Test
    public void testTimeoutOverflow() {
        Timer timer = time.timer(Long.MAX_VALUE);
        Assert.assertEquals(((Long.MAX_VALUE) - (timer.currentTimeMs())), timer.remainingMs());
        Assert.assertEquals(0, timer.elapsedMs());
    }

    @Test
    public void testNonMonotonicUpdate() {
        Timer timer = timer(100);
        long currentTimeMs = timer.currentTimeMs();
        timer.update((currentTimeMs - 1));
        Assert.assertEquals(currentTimeMs, timer.currentTimeMs());
        Assert.assertEquals(100, timer.remainingMs());
        Assert.assertEquals(0, timer.elapsedMs());
    }

    @Test
    public void testTimerSleep() {
        Timer timer = timer(500);
        long currentTimeMs = timer.currentTimeMs();
        timer.sleep(200);
        Assert.assertEquals(time.milliseconds(), timer.currentTimeMs());
        Assert.assertEquals((currentTimeMs + 200), timer.currentTimeMs());
        timer.sleep(1000);
        Assert.assertEquals(time.milliseconds(), timer.currentTimeMs());
        Assert.assertEquals((currentTimeMs + 500), timer.currentTimeMs());
        Assert.assertTrue(timer.isExpired());
    }
}

