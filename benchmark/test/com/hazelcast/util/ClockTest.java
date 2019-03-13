/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.util;


import Clock.ClockImpl;
import Clock.SystemClock;
import Clock.SystemOffsetClock;
import com.hazelcast.test.HazelcastSerialClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastSerialClassRunner.class)
@Category(QuickTest.class)
public class ClockTest extends AbstractClockTest {
    @Test
    public void testConstructor() {
        HazelcastTestSupport.assertUtilityConstructor(Clock.class);
    }

    @Test
    public void testCurrentTimeMillis() {
        Assert.assertTrue(((Clock.currentTimeMillis()) > 0));
    }

    @Test
    public void testCreateClock_withDefaults() {
        Clock.ClockImpl clock = Clock.createClock();
        HazelcastTestSupport.assertInstanceOf(SystemClock.class, clock);
    }

    @Test
    public void testCreateClock_withClockImpl() {
        AbstractClockTest.setJumpingClock(30);
        Clock.ClockImpl clock = Clock.createClock();
        HazelcastTestSupport.assertInstanceOf(JumpingSystemClock.class, clock);
    }

    @Test
    public void testCreateClock_withClockOffset() {
        AbstractClockTest.setClockOffset(30);
        Clock.ClockImpl clock = Clock.createClock();
        HazelcastTestSupport.assertInstanceOf(SystemOffsetClock.class, clock);
    }

    @Test
    public void testSystemClock() {
        Clock.SystemClock clock = new Clock.SystemClock();
        Assert.assertTrue(((clock.currentTimeMillis()) > 0));
    }

    @Test
    public void testSystemOffsetClock() {
        Clock.SystemOffsetClock clock = new Clock.SystemOffsetClock((-999999999));
        long systemMillis = System.currentTimeMillis();
        HazelcastTestSupport.sleepSeconds(1);
        long offsetMillis = clock.currentTimeMillis();
        Assert.assertTrue(String.format("SystemOffsetClock should be far behind the normal clock! %d < %d", offsetMillis, systemMillis), (offsetMillis < systemMillis));
    }
}

