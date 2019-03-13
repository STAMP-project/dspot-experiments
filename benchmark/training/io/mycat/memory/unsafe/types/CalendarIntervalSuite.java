/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.mycat.memory.unsafe.types;


import org.junit.Assert;
import org.junit.Test;

import static CalendarInterval.MICROS_PER_DAY;
import static CalendarInterval.MICROS_PER_HOUR;
import static CalendarInterval.MICROS_PER_MILLI;
import static CalendarInterval.MICROS_PER_MINUTE;
import static CalendarInterval.MICROS_PER_SECOND;
import static CalendarInterval.MICROS_PER_WEEK;


public class CalendarIntervalSuite {
    @Test
    public void equalsTest() {
        CalendarInterval i1 = new CalendarInterval(3, 123);
        CalendarInterval i2 = new CalendarInterval(3, 321);
        CalendarInterval i3 = new CalendarInterval(1, 123);
        CalendarInterval i4 = new CalendarInterval(3, 123);
        Assert.assertNotSame(i1, i2);
        Assert.assertNotSame(i1, i3);
        Assert.assertNotSame(i2, i3);
        Assert.assertEquals(i1, i4);
    }

    @Test
    public void toStringTest() {
        CalendarInterval i;
        i = new CalendarInterval(34, 0);
        Assert.assertEquals("interval 2 years 10 months", i.toString());
        i = new CalendarInterval((-34), 0);
        Assert.assertEquals("interval -2 years -10 months", i.toString());
        i = new CalendarInterval(0, (((3 * (MICROS_PER_WEEK)) + (13 * (MICROS_PER_HOUR))) + 123));
        Assert.assertEquals("interval 3 weeks 13 hours 123 microseconds", i.toString());
        i = new CalendarInterval(0, ((((-3) * (MICROS_PER_WEEK)) - (13 * (MICROS_PER_HOUR))) - 123));
        Assert.assertEquals("interval -3 weeks -13 hours -123 microseconds", i.toString());
        i = new CalendarInterval(34, (((3 * (MICROS_PER_WEEK)) + (13 * (MICROS_PER_HOUR))) + 123));
        Assert.assertEquals("interval 2 years 10 months 3 weeks 13 hours 123 microseconds", i.toString());
    }

    @Test
    public void fromStringTest() {
        CalendarIntervalSuite.testSingleUnit("year", 3, 36, 0);
        CalendarIntervalSuite.testSingleUnit("month", 3, 3, 0);
        CalendarIntervalSuite.testSingleUnit("week", 3, 0, (3 * (MICROS_PER_WEEK)));
        CalendarIntervalSuite.testSingleUnit("day", 3, 0, (3 * (MICROS_PER_DAY)));
        CalendarIntervalSuite.testSingleUnit("hour", 3, 0, (3 * (MICROS_PER_HOUR)));
        CalendarIntervalSuite.testSingleUnit("minute", 3, 0, (3 * (MICROS_PER_MINUTE)));
        CalendarIntervalSuite.testSingleUnit("second", 3, 0, (3 * (MICROS_PER_SECOND)));
        CalendarIntervalSuite.testSingleUnit("millisecond", 3, 0, (3 * (MICROS_PER_MILLI)));
        CalendarIntervalSuite.testSingleUnit("microsecond", 3, 0, 3);
        String input;
        input = "interval   -5  years  23   month";
        CalendarInterval result = new CalendarInterval((((-5) * 12) + 23), 0);
        Assert.assertEquals(CalendarInterval.fromString(input), result);
        input = "interval   -5  years  23   month   ";
        Assert.assertEquals(CalendarInterval.fromString(input), result);
        input = "  interval   -5  years  23   month   ";
        Assert.assertEquals(CalendarInterval.fromString(input), result);
        // Error cases
        input = "interval   3month 1 hour";
        Assert.assertNull(CalendarInterval.fromString(input));
        input = "interval 3 moth 1 hour";
        Assert.assertNull(CalendarInterval.fromString(input));
        input = "interval";
        Assert.assertNull(CalendarInterval.fromString(input));
        input = "int";
        Assert.assertNull(CalendarInterval.fromString(input));
        input = "";
        Assert.assertNull(CalendarInterval.fromString(input));
        input = null;
        Assert.assertNull(CalendarInterval.fromString(input));
    }

    @Test
    public void fromYearMonthStringTest() {
        String input;
        CalendarInterval i;
        input = "99-10";
        i = new CalendarInterval(((99 * 12) + 10), 0L);
        Assert.assertEquals(CalendarInterval.fromYearMonthString(input), i);
        input = "-8-10";
        i = new CalendarInterval((((-8) * 12) - 10), 0L);
        Assert.assertEquals(CalendarInterval.fromYearMonthString(input), i);
        try {
            input = "99-15";
            CalendarInterval.fromYearMonthString(input);
            Assert.fail("Expected to throw an exception for the invalid input");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("month 15 outside range"));
        }
    }

    @Test
    public void fromDayTimeStringTest() {
        String input;
        CalendarInterval i;
        input = "5 12:40:30.999999999";
        i = new CalendarInterval(0, (((((5 * (MICROS_PER_DAY)) + (12 * (MICROS_PER_HOUR))) + (40 * (MICROS_PER_MINUTE))) + (30 * (MICROS_PER_SECOND))) + 999999L));
        Assert.assertEquals(CalendarInterval.fromDayTimeString(input), i);
        input = "10 0:12:0.888";
        i = new CalendarInterval(0, ((10 * (MICROS_PER_DAY)) + (12 * (MICROS_PER_MINUTE))));
        Assert.assertEquals(CalendarInterval.fromDayTimeString(input), i);
        input = "-3 0:0:0";
        i = new CalendarInterval(0, ((-3) * (MICROS_PER_DAY)));
        Assert.assertEquals(CalendarInterval.fromDayTimeString(input), i);
        try {
            input = "5 30:12:20";
            CalendarInterval.fromDayTimeString(input);
            Assert.fail("Expected to throw an exception for the invalid input");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("hour 30 outside range"));
        }
        try {
            input = "5 30-12";
            CalendarInterval.fromDayTimeString(input);
            Assert.fail("Expected to throw an exception for the invalid input");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("not match day-time format"));
        }
    }

    @Test
    public void fromSingleUnitStringTest() {
        String input;
        CalendarInterval i;
        input = "12";
        i = new CalendarInterval((12 * 12), 0L);
        Assert.assertEquals(CalendarInterval.fromSingleUnitString("year", input), i);
        input = "100";
        i = new CalendarInterval(0, (100 * (MICROS_PER_DAY)));
        Assert.assertEquals(CalendarInterval.fromSingleUnitString("day", input), i);
        input = "1999.38888";
        i = new CalendarInterval(0, ((1999 * (MICROS_PER_SECOND)) + 38));
        Assert.assertEquals(CalendarInterval.fromSingleUnitString("second", input), i);
        try {
            input = String.valueOf(Integer.MAX_VALUE);
            CalendarInterval.fromSingleUnitString("year", input);
            Assert.fail("Expected to throw an exception for the invalid input");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("outside range"));
        }
        try {
            input = String.valueOf((((Long.MAX_VALUE) / (MICROS_PER_HOUR)) + 1));
            CalendarInterval.fromSingleUnitString("hour", input);
            Assert.fail("Expected to throw an exception for the invalid input");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(e.getMessage().contains("outside range"));
        }
    }

    @Test
    public void addTest() {
        String input = "interval 3 month 1 hour";
        String input2 = "interval 2 month 100 hour";
        CalendarInterval interval = CalendarInterval.fromString(input);
        CalendarInterval interval2 = CalendarInterval.fromString(input2);
        Assert.assertEquals(interval.add(interval2), new CalendarInterval(5, (101 * (MICROS_PER_HOUR))));
        input = "interval -10 month -81 hour";
        input2 = "interval 75 month 200 hour";
        interval = CalendarInterval.fromString(input);
        interval2 = CalendarInterval.fromString(input2);
        Assert.assertEquals(interval.add(interval2), new CalendarInterval(65, (119 * (MICROS_PER_HOUR))));
    }

    @Test
    public void subtractTest() {
        String input = "interval 3 month 1 hour";
        String input2 = "interval 2 month 100 hour";
        CalendarInterval interval = CalendarInterval.fromString(input);
        CalendarInterval interval2 = CalendarInterval.fromString(input2);
        Assert.assertEquals(interval.subtract(interval2), new CalendarInterval(1, ((-99) * (MICROS_PER_HOUR))));
        input = "interval -10 month -81 hour";
        input2 = "interval 75 month 200 hour";
        interval = CalendarInterval.fromString(input);
        interval2 = CalendarInterval.fromString(input2);
        Assert.assertEquals(interval.subtract(interval2), new CalendarInterval((-85), ((-281) * (MICROS_PER_HOUR))));
    }
}

