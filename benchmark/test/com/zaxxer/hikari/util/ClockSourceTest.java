/**
 * Copyright (C) 2016 Brett Wooldridge
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
package com.zaxxer.hikari.util;


import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Brett Wooldridge
 */
public class ClockSourceTest {
    @Test
    public void testClockSourceDisplay() {
        ClockSource msSource = new ClockSource.MillisecondClockSource();
        final long sTime = ((((TimeUnit.DAYS.toMillis(3)) + (TimeUnit.HOURS.toMillis(9))) + (TimeUnit.MINUTES.toMillis(24))) + (TimeUnit.SECONDS.toMillis(18))) + (TimeUnit.MILLISECONDS.toMillis(572));
        final long eTime = ((((TimeUnit.DAYS.toMillis(4)) + (TimeUnit.HOURS.toMillis(9))) + (TimeUnit.MINUTES.toMillis(55))) + (TimeUnit.SECONDS.toMillis(23))) + (TimeUnit.MILLISECONDS.toMillis(777));
        String ds1 = msSource.elapsedDisplayString0(sTime, eTime);
        Assert.assertEquals("1d31m5s205ms", ds1);
        final long eTime2 = ((((TimeUnit.DAYS.toMillis(3)) + (TimeUnit.HOURS.toMillis(8))) + (TimeUnit.MINUTES.toMillis(24))) + (TimeUnit.SECONDS.toMillis(23))) + (TimeUnit.MILLISECONDS.toMillis(777));
        String ds2 = msSource.elapsedDisplayString0(sTime, eTime2);
        Assert.assertEquals("-59m54s795ms", ds2);
        ClockSource nsSource = new ClockSource.NanosecondClockSource();
        final long sTime2 = ((((((TimeUnit.DAYS.toNanos(3)) + (TimeUnit.HOURS.toNanos(9))) + (TimeUnit.MINUTES.toNanos(24))) + (TimeUnit.SECONDS.toNanos(18))) + (TimeUnit.MILLISECONDS.toNanos(572))) + (TimeUnit.MICROSECONDS.toNanos(324))) + (TimeUnit.NANOSECONDS.toNanos(823));
        final long eTime3 = ((((((TimeUnit.DAYS.toNanos(4)) + (TimeUnit.HOURS.toNanos(19))) + (TimeUnit.MINUTES.toNanos(55))) + (TimeUnit.SECONDS.toNanos(23))) + (TimeUnit.MILLISECONDS.toNanos(777))) + (TimeUnit.MICROSECONDS.toNanos(0))) + (TimeUnit.NANOSECONDS.toNanos(982));
        String ds3 = nsSource.elapsedDisplayString0(sTime2, eTime3);
        Assert.assertEquals("1d10h31m5s204ms676?s159ns", ds3);
    }
}

