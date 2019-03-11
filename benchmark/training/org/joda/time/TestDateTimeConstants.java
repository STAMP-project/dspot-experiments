/**
 * Copyright 2001-2005 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time;


import DateTimeConstants.AD;
import DateTimeConstants.AM;
import DateTimeConstants.APRIL;
import DateTimeConstants.AUGUST;
import DateTimeConstants.BC;
import DateTimeConstants.BCE;
import DateTimeConstants.CE;
import DateTimeConstants.DAYS_PER_WEEK;
import DateTimeConstants.DECEMBER;
import DateTimeConstants.FEBRUARY;
import DateTimeConstants.FRIDAY;
import DateTimeConstants.HOURS_PER_DAY;
import DateTimeConstants.HOURS_PER_WEEK;
import DateTimeConstants.JANUARY;
import DateTimeConstants.JULY;
import DateTimeConstants.JUNE;
import DateTimeConstants.MARCH;
import DateTimeConstants.MAY;
import DateTimeConstants.MILLIS_PER_DAY;
import DateTimeConstants.MILLIS_PER_HOUR;
import DateTimeConstants.MILLIS_PER_MINUTE;
import DateTimeConstants.MILLIS_PER_SECOND;
import DateTimeConstants.MILLIS_PER_WEEK;
import DateTimeConstants.MINUTES_PER_DAY;
import DateTimeConstants.MINUTES_PER_HOUR;
import DateTimeConstants.MINUTES_PER_WEEK;
import DateTimeConstants.MONDAY;
import DateTimeConstants.NOVEMBER;
import DateTimeConstants.OCTOBER;
import DateTimeConstants.PM;
import DateTimeConstants.SATURDAY;
import DateTimeConstants.SECONDS_PER_DAY;
import DateTimeConstants.SECONDS_PER_HOUR;
import DateTimeConstants.SECONDS_PER_MINUTE;
import DateTimeConstants.SECONDS_PER_WEEK;
import DateTimeConstants.SEPTEMBER;
import DateTimeConstants.SUNDAY;
import DateTimeConstants.THURSDAY;
import DateTimeConstants.TUESDAY;
import DateTimeConstants.WEDNESDAY;
import junit.framework.TestCase;


/**
 * Test case.
 *
 * @author Stephen Colebourne
 */
public class TestDateTimeConstants extends TestCase {
    /**
     * TestDateTimeComparator constructor.
     *
     * @param name
     * 		
     */
    public TestDateTimeConstants(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testConstructor() {
        DateTimeConstants c = new DateTimeConstants() {};
        c.toString();
    }

    public void testHalfdaysOfDay() {
        TestCase.assertEquals(0, AM);
        TestCase.assertEquals(1, PM);
    }

    public void testDaysOfWeek() {
        TestCase.assertEquals(1, MONDAY);
        TestCase.assertEquals(2, TUESDAY);
        TestCase.assertEquals(3, WEDNESDAY);
        TestCase.assertEquals(4, THURSDAY);
        TestCase.assertEquals(5, FRIDAY);
        TestCase.assertEquals(6, SATURDAY);
        TestCase.assertEquals(7, SUNDAY);
    }

    public void testMonthsOfYear() {
        TestCase.assertEquals(1, JANUARY);
        TestCase.assertEquals(2, FEBRUARY);
        TestCase.assertEquals(3, MARCH);
        TestCase.assertEquals(4, APRIL);
        TestCase.assertEquals(5, MAY);
        TestCase.assertEquals(6, JUNE);
        TestCase.assertEquals(7, JULY);
        TestCase.assertEquals(8, AUGUST);
        TestCase.assertEquals(9, SEPTEMBER);
        TestCase.assertEquals(10, OCTOBER);
        TestCase.assertEquals(11, NOVEMBER);
        TestCase.assertEquals(12, DECEMBER);
    }

    public void testEras() {
        TestCase.assertEquals(0, BC);
        TestCase.assertEquals(0, BCE);
        TestCase.assertEquals(1, AD);
        TestCase.assertEquals(1, CE);
    }

    public void testMaths() {
        TestCase.assertEquals(1000, MILLIS_PER_SECOND);
        TestCase.assertEquals((60 * 1000), MILLIS_PER_MINUTE);
        TestCase.assertEquals(((60 * 60) * 1000), MILLIS_PER_HOUR);
        TestCase.assertEquals((((24 * 60) * 60) * 1000), MILLIS_PER_DAY);
        TestCase.assertEquals(((((7 * 24) * 60) * 60) * 1000), MILLIS_PER_WEEK);
        TestCase.assertEquals(60, SECONDS_PER_MINUTE);
        TestCase.assertEquals((60 * 60), SECONDS_PER_HOUR);
        TestCase.assertEquals(((24 * 60) * 60), SECONDS_PER_DAY);
        TestCase.assertEquals((((7 * 24) * 60) * 60), SECONDS_PER_WEEK);
        TestCase.assertEquals(60, MINUTES_PER_HOUR);
        TestCase.assertEquals((24 * 60), MINUTES_PER_DAY);
        TestCase.assertEquals(((7 * 24) * 60), MINUTES_PER_WEEK);
        TestCase.assertEquals(24, HOURS_PER_DAY);
        TestCase.assertEquals((7 * 24), HOURS_PER_WEEK);
        TestCase.assertEquals(7, DAYS_PER_WEEK);
    }
}

