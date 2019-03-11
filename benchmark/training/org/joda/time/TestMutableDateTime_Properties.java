/**
 * Copyright 2001-2013 Stephen Colebourne
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


import java.util.Locale;
import junit.framework.TestCase;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for DateTime.
 *
 * @author Stephen Colebourne
 * @author Mike Schrag
 */
public class TestMutableDateTime_Properties extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    // private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    long y2003days = (((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    // 2002-04-05 Fri
    private long TEST_TIME1 = ((((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    // 2003-05-06 Tue
    private long TEST_TIME2 = (((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone zone = null;

    private Locale locale = null;

    public TestMutableDateTime_Properties(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2).toString());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetEra() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().era(), test.era().getField());
        TestCase.assertEquals("era", test.era().getName());
        TestCase.assertEquals("Property[era]", test.era().toString());
        TestCase.assertSame(test, test.era().getMutableDateTime());
        TestCase.assertEquals(1, test.era().get());
        TestCase.assertEquals("AD", test.era().getAsText());
        TestCase.assertEquals("ap. J.-C.", test.era().getAsText(Locale.FRENCH));
        TestCase.assertEquals("AD", test.era().getAsShortText());
        TestCase.assertEquals("ap. J.-C.", test.era().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().eras(), test.era().getDurationField());
        TestCase.assertEquals(null, test.era().getRangeDurationField());
        TestCase.assertEquals(2, test.era().getMaximumTextLength(null));
        TestCase.assertEquals(9, test.era().getMaximumTextLength(Locale.FRENCH));
        TestCase.assertEquals(2, test.era().getMaximumShortTextLength(null));
        TestCase.assertEquals(9, test.era().getMaximumShortTextLength(Locale.FRENCH));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetYearOfEra() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().yearOfEra(), test.yearOfEra().getField());
        TestCase.assertEquals("yearOfEra", test.yearOfEra().getName());
        TestCase.assertEquals("Property[yearOfEra]", test.yearOfEra().toString());
        TestCase.assertEquals(2004, test.yearOfEra().get());
        TestCase.assertEquals("2004", test.yearOfEra().getAsText());
        TestCase.assertEquals("2004", test.yearOfEra().getAsText(Locale.FRENCH));
        TestCase.assertEquals("2004", test.yearOfEra().getAsShortText());
        TestCase.assertEquals("2004", test.yearOfEra().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().years(), test.yearOfEra().getDurationField());
        TestCase.assertEquals(test.getChronology().eras(), test.yearOfEra().getRangeDurationField());
        TestCase.assertEquals(9, test.yearOfEra().getMaximumTextLength(null));
        TestCase.assertEquals(9, test.yearOfEra().getMaximumShortTextLength(null));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetCenturyOfEra() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().centuryOfEra(), test.centuryOfEra().getField());
        TestCase.assertEquals("centuryOfEra", test.centuryOfEra().getName());
        TestCase.assertEquals("Property[centuryOfEra]", test.centuryOfEra().toString());
        TestCase.assertEquals(20, test.centuryOfEra().get());
        TestCase.assertEquals("20", test.centuryOfEra().getAsText());
        TestCase.assertEquals("20", test.centuryOfEra().getAsText(Locale.FRENCH));
        TestCase.assertEquals("20", test.centuryOfEra().getAsShortText());
        TestCase.assertEquals("20", test.centuryOfEra().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().centuries(), test.centuryOfEra().getDurationField());
        TestCase.assertEquals(test.getChronology().eras(), test.centuryOfEra().getRangeDurationField());
        TestCase.assertEquals(7, test.centuryOfEra().getMaximumTextLength(null));
        TestCase.assertEquals(7, test.centuryOfEra().getMaximumShortTextLength(null));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetYearOfCentury() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().yearOfCentury(), test.yearOfCentury().getField());
        TestCase.assertEquals("yearOfCentury", test.yearOfCentury().getName());
        TestCase.assertEquals("Property[yearOfCentury]", test.yearOfCentury().toString());
        TestCase.assertEquals(4, test.yearOfCentury().get());
        TestCase.assertEquals("4", test.yearOfCentury().getAsText());
        TestCase.assertEquals("4", test.yearOfCentury().getAsText(Locale.FRENCH));
        TestCase.assertEquals("4", test.yearOfCentury().getAsShortText());
        TestCase.assertEquals("4", test.yearOfCentury().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().years(), test.yearOfCentury().getDurationField());
        TestCase.assertEquals(test.getChronology().centuries(), test.yearOfCentury().getRangeDurationField());
        TestCase.assertEquals(2, test.yearOfCentury().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.yearOfCentury().getMaximumShortTextLength(null));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetWeekyear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().weekyear(), test.weekyear().getField());
        TestCase.assertEquals("weekyear", test.weekyear().getName());
        TestCase.assertEquals("Property[weekyear]", test.weekyear().toString());
        TestCase.assertEquals(2004, test.weekyear().get());
        TestCase.assertEquals("2004", test.weekyear().getAsText());
        TestCase.assertEquals("2004", test.weekyear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("2004", test.weekyear().getAsShortText());
        TestCase.assertEquals("2004", test.weekyear().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().weekyears(), test.weekyear().getDurationField());
        TestCase.assertEquals(null, test.weekyear().getRangeDurationField());
        TestCase.assertEquals(9, test.weekyear().getMaximumTextLength(null));
        TestCase.assertEquals(9, test.weekyear().getMaximumShortTextLength(null));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().year(), test.year().getField());
        TestCase.assertEquals("year", test.year().getName());
        TestCase.assertEquals("Property[year]", test.year().toString());
        TestCase.assertEquals(2004, test.year().get());
        TestCase.assertEquals("2004", test.year().getAsText());
        TestCase.assertEquals("2004", test.year().getAsText(Locale.FRENCH));
        TestCase.assertEquals("2004", test.year().getAsShortText());
        TestCase.assertEquals("2004", test.year().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().years(), test.year().getDurationField());
        TestCase.assertEquals(null, test.year().getRangeDurationField());
        TestCase.assertEquals(9, test.year().getMaximumTextLength(null));
        TestCase.assertEquals(9, test.year().getMaximumShortTextLength(null));
        TestCase.assertEquals((-292275054), test.year().getMinimumValue());
        TestCase.assertEquals((-292275054), test.year().getMinimumValueOverall());
        TestCase.assertEquals(292278993, test.year().getMaximumValue());
        TestCase.assertEquals(292278993, test.year().getMaximumValueOverall());
    }

    public void testPropertyAddYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.year().add(9);
        TestCase.assertEquals("2013-06-09T00:00:00.000+01:00", test.toString());
    }

    public void testPropertyAddWrapFieldYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.year().addWrapField(9);
        TestCase.assertEquals("2013-06-09T00:00:00.000+01:00", test.toString());
    }

    public void testPropertySetYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.year().set(1960);
        TestCase.assertEquals("1960-06-09T00:00:00.000+01:00", test.toString());
    }

    public void testPropertySetTextYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.year().set("1960");
        TestCase.assertEquals("1960-06-09T00:00:00.000+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMonthOfYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().monthOfYear(), test.monthOfYear().getField());
        TestCase.assertEquals("monthOfYear", test.monthOfYear().getName());
        TestCase.assertEquals("Property[monthOfYear]", test.monthOfYear().toString());
        TestCase.assertEquals(6, test.monthOfYear().get());
        TestCase.assertEquals("June", test.monthOfYear().getAsText());
        TestCase.assertEquals("juin", test.monthOfYear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("Jun", test.monthOfYear().getAsShortText());
        TestCase.assertEquals("juin", test.monthOfYear().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().months(), test.monthOfYear().getDurationField());
        TestCase.assertEquals(test.getChronology().years(), test.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(9, test.monthOfYear().getMaximumTextLength(null));
        TestCase.assertEquals(3, test.monthOfYear().getMaximumShortTextLength(null));
        test = new MutableDateTime(2004, 7, 9, 0, 0, 0, 0);
        TestCase.assertEquals("juillet", test.monthOfYear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("juil.", test.monthOfYear().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValue());
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValueOverall());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValue());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValueOverall());
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValue());
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValueOverall());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValue());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValueOverall());
    }

    public void testPropertyAddMonthOfYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.monthOfYear().add(6);
        TestCase.assertEquals("2004-12-09T00:00:00.000Z", test.toString());
    }

    public void testPropertyAddWrapFieldMonthOfYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.monthOfYear().addWrapField(8);
        TestCase.assertEquals("2004-02-09T00:00:00.000Z", test.toString());
    }

    public void testPropertySetMonthOfYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.monthOfYear().set(12);
        TestCase.assertEquals("2004-12-09T00:00:00.000Z", test.toString());
    }

    public void testPropertySetTextMonthOfYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.monthOfYear().set("12");
        TestCase.assertEquals("2004-12-09T00:00:00.000Z", test.toString());
        test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.monthOfYear().set("December");
        TestCase.assertEquals("2004-12-09T00:00:00.000Z", test.toString());
        test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.monthOfYear().set("Dec");
        TestCase.assertEquals("2004-12-09T00:00:00.000Z", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetDayOfMonth() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().dayOfMonth(), test.dayOfMonth().getField());
        TestCase.assertEquals("dayOfMonth", test.dayOfMonth().getName());
        TestCase.assertEquals("Property[dayOfMonth]", test.dayOfMonth().toString());
        TestCase.assertEquals(9, test.dayOfMonth().get());
        TestCase.assertEquals("9", test.dayOfMonth().getAsText());
        TestCase.assertEquals("9", test.dayOfMonth().getAsText(Locale.FRENCH));
        TestCase.assertEquals("9", test.dayOfMonth().getAsShortText());
        TestCase.assertEquals("9", test.dayOfMonth().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().days(), test.dayOfMonth().getDurationField());
        TestCase.assertEquals(test.getChronology().months(), test.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(2, test.dayOfMonth().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.dayOfMonth().getMaximumShortTextLength(null));
        TestCase.assertEquals(1, test.dayOfMonth().getMinimumValue());
        TestCase.assertEquals(1, test.dayOfMonth().getMinimumValueOverall());
        TestCase.assertEquals(30, test.dayOfMonth().getMaximumValue());
        TestCase.assertEquals(31, test.dayOfMonth().getMaximumValueOverall());
        TestCase.assertEquals(false, test.dayOfMonth().isLeap());
        TestCase.assertEquals(0, test.dayOfMonth().getLeapAmount());
        TestCase.assertEquals(null, test.dayOfMonth().getLeapDurationField());
    }

    public void testPropertyAddDayOfMonth() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfMonth().add(9);
        TestCase.assertEquals("2004-06-18T00:00:00.000+01:00", test.toString());
    }

    public void testPropertyAddWrapFieldDayOfMonth() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfMonth().addWrapField(22);
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", test.toString());
    }

    public void testPropertySetDayOfMonth() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfMonth().set(12);
        TestCase.assertEquals("2004-06-12T00:00:00.000+01:00", test.toString());
    }

    public void testPropertySetTextDayOfMonth() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfMonth().set("12");
        TestCase.assertEquals("2004-06-12T00:00:00.000+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetDayOfYear() {
        // 31+29+31+30+31+9 = 161
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().dayOfYear(), test.dayOfYear().getField());
        TestCase.assertEquals("dayOfYear", test.dayOfYear().getName());
        TestCase.assertEquals("Property[dayOfYear]", test.dayOfYear().toString());
        TestCase.assertEquals(161, test.dayOfYear().get());
        TestCase.assertEquals("161", test.dayOfYear().getAsText());
        TestCase.assertEquals("161", test.dayOfYear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("161", test.dayOfYear().getAsShortText());
        TestCase.assertEquals("161", test.dayOfYear().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().days(), test.dayOfYear().getDurationField());
        TestCase.assertEquals(test.getChronology().years(), test.dayOfYear().getRangeDurationField());
        TestCase.assertEquals(3, test.dayOfYear().getMaximumTextLength(null));
        TestCase.assertEquals(3, test.dayOfYear().getMaximumShortTextLength(null));
        TestCase.assertEquals(false, test.dayOfYear().isLeap());
        TestCase.assertEquals(0, test.dayOfYear().getLeapAmount());
        TestCase.assertEquals(null, test.dayOfYear().getLeapDurationField());
    }

    public void testPropertyAddDayOfYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfYear().add(9);
        TestCase.assertEquals("2004-06-18T00:00:00.000+01:00", test.toString());
    }

    public void testPropertyAddWrapFieldDayOfYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfYear().addWrapField(206);
        TestCase.assertEquals("2004-01-01T00:00:00.000Z", test.toString());
    }

    public void testPropertySetDayOfYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfYear().set(12);
        TestCase.assertEquals("2004-01-12T00:00:00.000Z", test.toString());
    }

    public void testPropertySetTextDayOfYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfYear().set("12");
        TestCase.assertEquals("2004-01-12T00:00:00.000Z", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetWeekOfWeekyear() {
        // 2002-01-01 = Thu
        // 2002-12-31 = Thu (+364 days)
        // 2003-12-30 = Thu (+364 days)
        // 2004-01-03 = Mon             W1
        // 2004-01-31 = Mon (+28 days)  W5
        // 2004-02-28 = Mon (+28 days)  W9
        // 2004-03-27 = Mon (+28 days)  W13
        // 2004-04-24 = Mon (+28 days)  W17
        // 2004-05-23 = Mon (+28 days)  W21
        // 2004-06-05 = Mon (+14 days)  W23
        // 2004-06-09 = Fri
        // 2004-12-25 = Mon             W52
        // 2005-01-01 = Mon             W1
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().weekOfWeekyear(), test.weekOfWeekyear().getField());
        TestCase.assertEquals("weekOfWeekyear", test.weekOfWeekyear().getName());
        TestCase.assertEquals("Property[weekOfWeekyear]", test.weekOfWeekyear().toString());
        TestCase.assertEquals(24, test.weekOfWeekyear().get());
        TestCase.assertEquals("24", test.weekOfWeekyear().getAsText());
        TestCase.assertEquals("24", test.weekOfWeekyear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("24", test.weekOfWeekyear().getAsShortText());
        TestCase.assertEquals("24", test.weekOfWeekyear().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().weeks(), test.weekOfWeekyear().getDurationField());
        TestCase.assertEquals(test.getChronology().weekyears(), test.weekOfWeekyear().getRangeDurationField());
        TestCase.assertEquals(2, test.weekOfWeekyear().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.weekOfWeekyear().getMaximumShortTextLength(null));
        TestCase.assertEquals(false, test.weekOfWeekyear().isLeap());
        TestCase.assertEquals(0, test.weekOfWeekyear().getLeapAmount());
        TestCase.assertEquals(null, test.weekOfWeekyear().getLeapDurationField());
    }

    public void testPropertyAddWeekOfWeekyear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 7, 0, 0, 0, 0);
        test.weekOfWeekyear().add(1);
        TestCase.assertEquals("2004-06-14T00:00:00.000+01:00", test.toString());
    }

    public void testPropertyAddWrapFieldWeekOfWeekyear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 7, 0, 0, 0, 0);
        test.weekOfWeekyear().addWrapField(30);
        TestCase.assertEquals("2003-12-29T00:00:00.000Z", test.toString());
    }

    public void testPropertySetWeekOfWeekyear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 7, 0, 0, 0, 0);
        test.weekOfWeekyear().set(4);
        TestCase.assertEquals("2004-01-19T00:00:00.000Z", test.toString());
    }

    public void testPropertySetTextWeekOfWeekyear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 7, 0, 0, 0, 0);
        test.weekOfWeekyear().set("4");
        TestCase.assertEquals("2004-01-19T00:00:00.000Z", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetDayOfWeek() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().dayOfWeek(), test.dayOfWeek().getField());
        TestCase.assertEquals("dayOfWeek", test.dayOfWeek().getName());
        TestCase.assertEquals("Property[dayOfWeek]", test.dayOfWeek().toString());
        TestCase.assertEquals(3, test.dayOfWeek().get());
        TestCase.assertEquals("Wednesday", test.dayOfWeek().getAsText());
        TestCase.assertEquals("mercredi", test.dayOfWeek().getAsText(Locale.FRENCH));
        TestCase.assertEquals("Wed", test.dayOfWeek().getAsShortText());
        TestCase.assertEquals("mer.", test.dayOfWeek().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().days(), test.dayOfWeek().getDurationField());
        TestCase.assertEquals(test.getChronology().weeks(), test.dayOfWeek().getRangeDurationField());
        TestCase.assertEquals(9, test.dayOfWeek().getMaximumTextLength(null));
        TestCase.assertEquals(8, test.dayOfWeek().getMaximumTextLength(Locale.FRENCH));
        TestCase.assertEquals(3, test.dayOfWeek().getMaximumShortTextLength(null));
        TestCase.assertEquals(4, test.dayOfWeek().getMaximumShortTextLength(Locale.FRENCH));
        TestCase.assertEquals(1, test.dayOfWeek().getMinimumValue());
        TestCase.assertEquals(1, test.dayOfWeek().getMinimumValueOverall());
        TestCase.assertEquals(7, test.dayOfWeek().getMaximumValue());
        TestCase.assertEquals(7, test.dayOfWeek().getMaximumValueOverall());
        TestCase.assertEquals(false, test.dayOfWeek().isLeap());
        TestCase.assertEquals(0, test.dayOfWeek().getLeapAmount());
        TestCase.assertEquals(null, test.dayOfWeek().getLeapDurationField());
    }

    public void testPropertyAddDayOfWeek() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfWeek().add(1);
        TestCase.assertEquals("2004-06-10T00:00:00.000+01:00", test.toString());
    }

    public void testPropertyAddLongDayOfWeek() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfWeek().add(1L);
        TestCase.assertEquals("2004-06-10T00:00:00.000+01:00", test.toString());
    }

    public void testPropertyAddWrapFieldDayOfWeek() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);// Wed

        test.dayOfWeek().addWrapField(5);
        TestCase.assertEquals("2004-06-07T00:00:00.000+01:00", test.toString());
    }

    public void testPropertySetDayOfWeek() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfWeek().set(4);
        TestCase.assertEquals("2004-06-10T00:00:00.000+01:00", test.toString());
    }

    public void testPropertySetTextDayOfWeek() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfWeek().set("4");
        TestCase.assertEquals("2004-06-10T00:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfWeek().set("Mon");
        TestCase.assertEquals("2004-06-07T00:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfWeek().set("Tuesday");
        TestCase.assertEquals("2004-06-08T00:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2004, 6, 9, 0, 0, 0, 0);
        test.dayOfWeek().set("lundi", Locale.FRENCH);
        TestCase.assertEquals("2004-06-07T00:00:00.000+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetHourOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().hourOfDay(), test.hourOfDay().getField());
        TestCase.assertEquals("hourOfDay", test.hourOfDay().getName());
        TestCase.assertEquals("Property[hourOfDay]", test.hourOfDay().toString());
        TestCase.assertEquals(13, test.hourOfDay().get());
        TestCase.assertEquals("13", test.hourOfDay().getAsText());
        TestCase.assertEquals("13", test.hourOfDay().getAsText(Locale.FRENCH));
        TestCase.assertEquals("13", test.hourOfDay().getAsShortText());
        TestCase.assertEquals("13", test.hourOfDay().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().hours(), test.hourOfDay().getDurationField());
        TestCase.assertEquals(test.getChronology().days(), test.hourOfDay().getRangeDurationField());
        TestCase.assertEquals(2, test.hourOfDay().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.hourOfDay().getMaximumShortTextLength(null));
    }

    public void testPropertyRoundFloorHourOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 30, 0, 0);
        test.hourOfDay().roundFloor();
        TestCase.assertEquals("2004-06-09T13:00:00.000+01:00", test.toString());
    }

    public void testPropertyRoundCeilingHourOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 30, 0, 0);
        test.hourOfDay().roundCeiling();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", test.toString());
    }

    public void testPropertyRoundHalfFloorHourOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 30, 0, 0);
        test.hourOfDay().roundHalfFloor();
        TestCase.assertEquals("2004-06-09T13:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2004, 6, 9, 13, 30, 0, 1);
        test.hourOfDay().roundHalfFloor();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2004, 6, 9, 13, 29, 59, 999);
        test.hourOfDay().roundHalfFloor();
        TestCase.assertEquals("2004-06-09T13:00:00.000+01:00", test.toString());
    }

    public void testPropertyRoundHalfCeilingHourOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 30, 0, 0);
        test.hourOfDay().roundHalfCeiling();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2004, 6, 9, 13, 30, 0, 1);
        test.hourOfDay().roundHalfCeiling();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2004, 6, 9, 13, 29, 59, 999);
        test.hourOfDay().roundHalfCeiling();
        TestCase.assertEquals("2004-06-09T13:00:00.000+01:00", test.toString());
    }

    public void testPropertyRoundHalfEvenHourOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 30, 0, 0);
        test.hourOfDay().roundHalfEven();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2004, 6, 9, 14, 30, 0, 0);
        test.hourOfDay().roundHalfEven();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2004, 6, 9, 13, 30, 0, 1);
        test.hourOfDay().roundHalfEven();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", test.toString());
        test = new MutableDateTime(2004, 6, 9, 13, 29, 59, 999);
        test.hourOfDay().roundHalfEven();
        TestCase.assertEquals("2004-06-09T13:00:00.000+01:00", test.toString());
    }

    public void testPropertyRemainderHourOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 30, 0, 0);
        TestCase.assertEquals((30L * (MILLIS_PER_MINUTE)), test.hourOfDay().remainder());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMinuteOfHour() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().minuteOfHour(), test.minuteOfHour().getField());
        TestCase.assertEquals("minuteOfHour", test.minuteOfHour().getName());
        TestCase.assertEquals("Property[minuteOfHour]", test.minuteOfHour().toString());
        TestCase.assertEquals(23, test.minuteOfHour().get());
        TestCase.assertEquals("23", test.minuteOfHour().getAsText());
        TestCase.assertEquals("23", test.minuteOfHour().getAsText(Locale.FRENCH));
        TestCase.assertEquals("23", test.minuteOfHour().getAsShortText());
        TestCase.assertEquals("23", test.minuteOfHour().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().minutes(), test.minuteOfHour().getDurationField());
        TestCase.assertEquals(test.getChronology().hours(), test.minuteOfHour().getRangeDurationField());
        TestCase.assertEquals(2, test.minuteOfHour().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.minuteOfHour().getMaximumShortTextLength(null));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMinuteOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().minuteOfDay(), test.minuteOfDay().getField());
        TestCase.assertEquals("minuteOfDay", test.minuteOfDay().getName());
        TestCase.assertEquals("Property[minuteOfDay]", test.minuteOfDay().toString());
        TestCase.assertEquals(803, test.minuteOfDay().get());
        TestCase.assertEquals("803", test.minuteOfDay().getAsText());
        TestCase.assertEquals("803", test.minuteOfDay().getAsText(Locale.FRENCH));
        TestCase.assertEquals("803", test.minuteOfDay().getAsShortText());
        TestCase.assertEquals("803", test.minuteOfDay().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().minutes(), test.minuteOfDay().getDurationField());
        TestCase.assertEquals(test.getChronology().days(), test.minuteOfDay().getRangeDurationField());
        TestCase.assertEquals(4, test.minuteOfDay().getMaximumTextLength(null));
        TestCase.assertEquals(4, test.minuteOfDay().getMaximumShortTextLength(null));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetSecondOfMinute() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().secondOfMinute(), test.secondOfMinute().getField());
        TestCase.assertEquals("secondOfMinute", test.secondOfMinute().getName());
        TestCase.assertEquals("Property[secondOfMinute]", test.secondOfMinute().toString());
        TestCase.assertEquals(43, test.secondOfMinute().get());
        TestCase.assertEquals("43", test.secondOfMinute().getAsText());
        TestCase.assertEquals("43", test.secondOfMinute().getAsText(Locale.FRENCH));
        TestCase.assertEquals("43", test.secondOfMinute().getAsShortText());
        TestCase.assertEquals("43", test.secondOfMinute().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().seconds(), test.secondOfMinute().getDurationField());
        TestCase.assertEquals(test.getChronology().minutes(), test.secondOfMinute().getRangeDurationField());
        TestCase.assertEquals(2, test.secondOfMinute().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.secondOfMinute().getMaximumShortTextLength(null));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetSecondOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().secondOfDay(), test.secondOfDay().getField());
        TestCase.assertEquals("secondOfDay", test.secondOfDay().getName());
        TestCase.assertEquals("Property[secondOfDay]", test.secondOfDay().toString());
        TestCase.assertEquals(48223, test.secondOfDay().get());
        TestCase.assertEquals("48223", test.secondOfDay().getAsText());
        TestCase.assertEquals("48223", test.secondOfDay().getAsText(Locale.FRENCH));
        TestCase.assertEquals("48223", test.secondOfDay().getAsShortText());
        TestCase.assertEquals("48223", test.secondOfDay().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().seconds(), test.secondOfDay().getDurationField());
        TestCase.assertEquals(test.getChronology().days(), test.secondOfDay().getRangeDurationField());
        TestCase.assertEquals(5, test.secondOfDay().getMaximumTextLength(null));
        TestCase.assertEquals(5, test.secondOfDay().getMaximumShortTextLength(null));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMillisOfSecond() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().millisOfSecond(), test.millisOfSecond().getField());
        TestCase.assertEquals("millisOfSecond", test.millisOfSecond().getName());
        TestCase.assertEquals("Property[millisOfSecond]", test.millisOfSecond().toString());
        TestCase.assertEquals(53, test.millisOfSecond().get());
        TestCase.assertEquals("53", test.millisOfSecond().getAsText());
        TestCase.assertEquals("53", test.millisOfSecond().getAsText(Locale.FRENCH));
        TestCase.assertEquals("53", test.millisOfSecond().getAsShortText());
        TestCase.assertEquals("53", test.millisOfSecond().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().millis(), test.millisOfSecond().getDurationField());
        TestCase.assertEquals(test.getChronology().seconds(), test.millisOfSecond().getRangeDurationField());
        TestCase.assertEquals(3, test.millisOfSecond().getMaximumTextLength(null));
        TestCase.assertEquals(3, test.millisOfSecond().getMaximumShortTextLength(null));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMillisOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().millisOfDay(), test.millisOfDay().getField());
        TestCase.assertEquals("millisOfDay", test.millisOfDay().getName());
        TestCase.assertEquals("Property[millisOfDay]", test.millisOfDay().toString());
        TestCase.assertEquals(48223053, test.millisOfDay().get());
        TestCase.assertEquals("48223053", test.millisOfDay().getAsText());
        TestCase.assertEquals("48223053", test.millisOfDay().getAsText(Locale.FRENCH));
        TestCase.assertEquals("48223053", test.millisOfDay().getAsShortText());
        TestCase.assertEquals("48223053", test.millisOfDay().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().millis(), test.millisOfDay().getDurationField());
        TestCase.assertEquals(test.getChronology().days(), test.millisOfDay().getRangeDurationField());
        TestCase.assertEquals(8, test.millisOfDay().getMaximumTextLength(null));
        TestCase.assertEquals(8, test.millisOfDay().getMaximumShortTextLength(null));
    }

    // -----------------------------------------------------------------------
    public void testPropertyToIntervalYearOfEra() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.yearOfEra().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 1, 1, 0, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2005, 1, 1, 0, 0, 0, 0), testInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 53), test);
    }

    public void testPropertyToIntervalYearOfCentury() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.yearOfCentury().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 1, 1, 0, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2005, 1, 1, 0, 0, 0, 0), testInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 53), test);
    }

    public void testPropertyToIntervalYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.year().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 1, 1, 0, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2005, 1, 1, 0, 0, 0, 0), testInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 53), test);
    }

    public void testPropertyToIntervalMonthOfYear() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.monthOfYear().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 6, 1, 0, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2004, 7, 1, 0, 0, 0, 0), testInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 53), test);
    }

    public void testPropertyToIntervalDayOfMonth() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.dayOfMonth().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 0, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 10, 0, 0, 0, 0), testInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 53), test);
        MutableDateTime febTest = new MutableDateTime(2004, 2, 29, 13, 23, 43, 53);
        Interval febTestInterval = febTest.dayOfMonth().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 0, 0, 0, 0), febTestInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2004, 3, 1, 0, 0, 0, 0), febTestInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 2, 29, 13, 23, 43, 53), febTest);
    }

    public void testPropertyToIntervalHourOfDay() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.hourOfDay().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 14, 0, 0, 0), testInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 53), test);
        MutableDateTime midnightTest = new MutableDateTime(2004, 6, 9, 23, 23, 43, 53);
        Interval midnightTestInterval = midnightTest.hourOfDay().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 23, 0, 0, 0), midnightTestInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 10, 0, 0, 0, 0), midnightTestInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 23, 23, 43, 53), midnightTest);
    }

    public void testPropertyToIntervalMinuteOfHour() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.minuteOfHour().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 24, 0, 0), testInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 53), test);
    }

    public void testPropertyToIntervalSecondOfMinute() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.secondOfMinute().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 0), testInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 44, 0), testInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 53), test);
    }

    public void testPropertyToIntervalMillisOfSecond() {
        MutableDateTime test = new MutableDateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.millisOfSecond().toInterval();
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 53), testInterval.getStart());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 54), testInterval.getEnd());
        TestCase.assertEquals(new MutableDateTime(2004, 6, 9, 13, 23, 43, 53), test);
    }
}

