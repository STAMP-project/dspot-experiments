/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processor;


import java.util.concurrent.TimeUnit;
import org.apache.nifi.util.FormatUtils;
import org.junit.Assert;
import org.junit.Test;


public class TestFormatUtils {
    @Test
    public void testParse() {
        Assert.assertEquals(3, FormatUtils.getTimeDuration("3000 ms", TimeUnit.SECONDS));
        Assert.assertEquals(3000, FormatUtils.getTimeDuration("3000 s", TimeUnit.SECONDS));
        Assert.assertEquals(0, FormatUtils.getTimeDuration("999 millis", TimeUnit.SECONDS));
        Assert.assertEquals(((((4L * 24L) * 60L) * 60L) * 1000000000L), FormatUtils.getTimeDuration("4 days", TimeUnit.NANOSECONDS));
        Assert.assertEquals(24, FormatUtils.getTimeDuration("1 DAY", TimeUnit.HOURS));
        Assert.assertEquals(60, FormatUtils.getTimeDuration("1 hr", TimeUnit.MINUTES));
        Assert.assertEquals(60, FormatUtils.getTimeDuration("1 Hrs", TimeUnit.MINUTES));
    }

    @Test
    public void testFormatTime() throws Exception {
        Assert.assertEquals("00:00:00.000", FormatUtils.formatHoursMinutesSeconds(0, TimeUnit.DAYS));
        Assert.assertEquals("01:00:00.000", FormatUtils.formatHoursMinutesSeconds(1, TimeUnit.HOURS));
        Assert.assertEquals("02:00:00.000", FormatUtils.formatHoursMinutesSeconds(2, TimeUnit.HOURS));
        Assert.assertEquals("00:01:00.000", FormatUtils.formatHoursMinutesSeconds(1, TimeUnit.MINUTES));
        Assert.assertEquals("00:00:10.000", FormatUtils.formatHoursMinutesSeconds(10, TimeUnit.SECONDS));
        Assert.assertEquals("00:00:00.777", FormatUtils.formatHoursMinutesSeconds(777, TimeUnit.MILLISECONDS));
        Assert.assertEquals("00:00:07.777", FormatUtils.formatHoursMinutesSeconds(7777, TimeUnit.MILLISECONDS));
        Assert.assertEquals("20:11:36.897", FormatUtils.formatHoursMinutesSeconds(((((TimeUnit.MILLISECONDS.convert(20, TimeUnit.HOURS)) + (TimeUnit.MILLISECONDS.convert(11, TimeUnit.MINUTES))) + (TimeUnit.MILLISECONDS.convert(36, TimeUnit.SECONDS))) + (TimeUnit.MILLISECONDS.convert(897, TimeUnit.MILLISECONDS))), TimeUnit.MILLISECONDS));
        Assert.assertEquals("1000:01:01.001", FormatUtils.formatHoursMinutesSeconds(((((TimeUnit.MILLISECONDS.convert(999, TimeUnit.HOURS)) + (TimeUnit.MILLISECONDS.convert(60, TimeUnit.MINUTES))) + (TimeUnit.MILLISECONDS.convert(60, TimeUnit.SECONDS))) + (TimeUnit.MILLISECONDS.convert(1001, TimeUnit.MILLISECONDS))), TimeUnit.MILLISECONDS));
    }

    @Test
    public void testFormatNanos() {
        Assert.assertEquals("0 nanos", FormatUtils.formatNanos(0L, false));
        Assert.assertEquals("0 nanos (0 nanos)", FormatUtils.formatNanos(0L, true));
        Assert.assertEquals("1 millis, 0 nanos", FormatUtils.formatNanos(1000000L, false));
        Assert.assertEquals("1 millis, 0 nanos (1000000 nanos)", FormatUtils.formatNanos(1000000L, true));
        Assert.assertEquals("1 millis, 1 nanos", FormatUtils.formatNanos(1000001L, false));
        Assert.assertEquals("1 millis, 1 nanos (1000001 nanos)", FormatUtils.formatNanos(1000001L, true));
        Assert.assertEquals("1 seconds, 0 millis, 0 nanos", FormatUtils.formatNanos(1000000000L, false));
        Assert.assertEquals("1 seconds, 0 millis, 0 nanos (1000000000 nanos)", FormatUtils.formatNanos(1000000000L, true));
        Assert.assertEquals("1 seconds, 1 millis, 0 nanos", FormatUtils.formatNanos(1001000000L, false));
        Assert.assertEquals("1 seconds, 1 millis, 0 nanos (1001000000 nanos)", FormatUtils.formatNanos(1001000000L, true));
        Assert.assertEquals("1 seconds, 1 millis, 1 nanos", FormatUtils.formatNanos(1001000001L, false));
        Assert.assertEquals("1 seconds, 1 millis, 1 nanos (1001000001 nanos)", FormatUtils.formatNanos(1001000001L, true));
    }

    @Test
    public void testFormatDataSize() {
        Assert.assertEquals("0 bytes", FormatUtils.formatDataSize(0.0));
        Assert.assertEquals("10.4 bytes", FormatUtils.formatDataSize(10.4));
        Assert.assertEquals("1,024 bytes", FormatUtils.formatDataSize(1024.0));
        Assert.assertEquals("1 KB", FormatUtils.formatDataSize(1025.0));
        Assert.assertEquals("1.95 KB", FormatUtils.formatDataSize(2000.0));
        Assert.assertEquals("195.31 KB", FormatUtils.formatDataSize(200000.0));
        Assert.assertEquals("190.73 MB", FormatUtils.formatDataSize(2.0E8));
        Assert.assertEquals("186.26 GB", FormatUtils.formatDataSize(2.0E11));
        Assert.assertEquals("181.9 TB", FormatUtils.formatDataSize(2.0E14));
    }
}

