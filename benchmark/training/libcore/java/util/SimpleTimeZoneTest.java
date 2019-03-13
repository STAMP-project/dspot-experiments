/**
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.java.util;


import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import junit.framework.TestCase;


/**
 * Tests for {@link SimpleTimeZone}.
 *
 * <p>The methods starting {@code testDstParis2014_...} and {@code testDstNewYork2014} check
 * various different ways to specify the same instants when DST starts and ends in the associated
 * real world time zone in 2014, i.e. Europe/Paris and America/New_York respectively.
 */
public class SimpleTimeZoneTest extends TestCase {
    private static final int NEW_YORK_RAW_OFFSET = -18000000;

    private static final int PARIS_RAW_OFFSET = 3600000;

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    /**
     * Sanity check to ensure that the standard TimeZone for Europe/Paris has the correct DST
     * transition times.
     */
    public void testStandardParis2014() {
        TimeZone timeZone = TimeZone.getTimeZone("Europe/Paris");
        checkDstParis2014(timeZone);
    }

    public void testDstParis2014_LastSundayMarch_LastSundayOctober_UtcTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.PARIS_RAW_OFFSET, "Europe/Paris", Calendar.MARCH, (-1), Calendar.SUNDAY, 3600000, SimpleTimeZone.UTC_TIME, Calendar.OCTOBER, (-1), Calendar.SUNDAY, 3600000, SimpleTimeZone.UTC_TIME, 3600000);
        checkDstParis2014(timeZone);
    }

    public void testDstParis2014_SundayAfter25thMarch_SundayAfter25thOctober_UtcTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.PARIS_RAW_OFFSET, "Europe/Paris", Calendar.MARCH, 25, (-(Calendar.SUNDAY)), 3600000, SimpleTimeZone.UTC_TIME, Calendar.OCTOBER, 25, (-(Calendar.SUNDAY)), 3600000, SimpleTimeZone.UTC_TIME, 3600000);
        checkDstParis2014(timeZone);
    }

    public void testDstParis2014_30thMarch_26thOctober_UtcTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.PARIS_RAW_OFFSET, "Europe/Paris", Calendar.MARCH, 30, 0, 3600000, SimpleTimeZone.UTC_TIME, Calendar.OCTOBER, 26, 0, 3600000, SimpleTimeZone.UTC_TIME, 3600000);
        checkDstParis2014(timeZone);
    }

    public void testDst_1stSundayApril_1stSundayOctober_DefaultTime() {
        TimeZone timeZone = new SimpleTimeZone((-18000000), "EST", Calendar.APRIL, 1, (-(Calendar.SUNDAY)), 7200000, Calendar.OCTOBER, (-1), Calendar.SUNDAY, 7200000, 3600000);
        checkDstTransitionTimes(timeZone, 1998, "1998-04-05T07:00:00.000+0000", "1998-10-25T06:00:00.000+0000");
        checkDstTransitionTimes(timeZone, 2014, "2014-04-06T07:00:00.000+0000", "2014-10-26T06:00:00.000+0000");
    }

    /**
     * Sanity check to ensure that the standard TimeZone for America/New_York has the correct DST
     * transition times.
     */
    public void testStandardNewYork2014() {
        TimeZone timeZone = TimeZone.getTimeZone("America/New_York");
        checkDstNewYork2014(timeZone);
    }

    public void testDstNewYork2014_2ndSundayMarch_1stSundayNovember_StandardTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.NEW_YORK_RAW_OFFSET, "EST", Calendar.MARCH, 2, Calendar.SUNDAY, 7200000, SimpleTimeZone.STANDARD_TIME, Calendar.NOVEMBER, 1, Calendar.SUNDAY, 3600000, SimpleTimeZone.STANDARD_TIME, 3600000);
        checkDstNewYork2014(timeZone);
    }

    public void testDstNewYork2014_2ndSundayMarch_1stSundayNovember_UtcTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.NEW_YORK_RAW_OFFSET, "EST", Calendar.MARCH, 2, Calendar.SUNDAY, 25200000, SimpleTimeZone.UTC_TIME, Calendar.NOVEMBER, 1, Calendar.SUNDAY, 21600000, SimpleTimeZone.UTC_TIME, 3600000);
        checkDstNewYork2014(timeZone);
    }

    public void testDstNewYork2014_2ndSundayMarch_1stSundayNovember_WallTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.NEW_YORK_RAW_OFFSET, "EST", Calendar.MARCH, 2, Calendar.SUNDAY, 7200000, SimpleTimeZone.WALL_TIME, Calendar.NOVEMBER, 1, Calendar.SUNDAY, 7200000, SimpleTimeZone.WALL_TIME, 3600000);
        checkDstNewYork2014(timeZone);
    }

    public void testDstNewYork2014_2ndSundayMarch_1stSundayNovember_DefaultTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.NEW_YORK_RAW_OFFSET, "EST", Calendar.MARCH, 2, Calendar.SUNDAY, 7200000, Calendar.NOVEMBER, 1, Calendar.SUNDAY, 7200000, 3600000);
        checkDstNewYork2014(timeZone);
    }

    public void testDstNewYork2014_9thMarch_2ndNovember_StandardTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.NEW_YORK_RAW_OFFSET, "EST", Calendar.MARCH, 9, 0, 7200000, SimpleTimeZone.STANDARD_TIME, Calendar.NOVEMBER, 2, 0, 3600000, SimpleTimeZone.STANDARD_TIME, 3600000);
        checkDstNewYork2014(timeZone);
    }

    public void testDstNewYork2014_9thMarch_2ndNovember_UtcTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.NEW_YORK_RAW_OFFSET, "EST", Calendar.MARCH, 9, 0, 25200000, SimpleTimeZone.UTC_TIME, Calendar.NOVEMBER, 2, 0, 21600000, SimpleTimeZone.UTC_TIME, 3600000);
        checkDstNewYork2014(timeZone);
    }

    public void testDstNewYork2014_9thMarch_2ndNovember_WallTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.NEW_YORK_RAW_OFFSET, "EST", Calendar.MARCH, 9, 0, 7200000, SimpleTimeZone.WALL_TIME, Calendar.NOVEMBER, 2, 0, 7200000, SimpleTimeZone.WALL_TIME, 3600000);
        checkDstNewYork2014(timeZone);
    }

    public void testDstNewYork2014_9thMarch_2ndNovember_DefaultTime() {
        TimeZone timeZone = new SimpleTimeZone(SimpleTimeZoneTest.NEW_YORK_RAW_OFFSET, "EST", Calendar.MARCH, 9, 0, 7200000, Calendar.NOVEMBER, 2, 0, 7200000, 3600000);
        checkDstNewYork2014(timeZone);
    }
}

