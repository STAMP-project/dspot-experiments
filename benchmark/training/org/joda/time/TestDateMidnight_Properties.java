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
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.LenientChronology;
import org.joda.time.chrono.StrictChronology;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for DateTime.
 *
 * @author Stephen Colebourne
 * @author Mike Schrag
 */
@SuppressWarnings("deprecation")
public class TestDateMidnight_Properties extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestDateMidnight_Properties.PARIS);

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

    public TestDateMidnight_Properties(String name) {
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
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().era(), test.era().getField());
        TestCase.assertEquals("era", test.era().getName());
        TestCase.assertEquals("Property[era]", test.era().toString());
        TestCase.assertSame(test, test.era().getDateMidnight());
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
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().yearOfEra(), test.yearOfEra().getField());
        TestCase.assertEquals("yearOfEra", test.yearOfEra().getName());
        TestCase.assertEquals("Property[yearOfEra]", test.yearOfEra().toString());
        TestCase.assertSame(test, test.yearOfEra().getDateMidnight());
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
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().centuryOfEra(), test.centuryOfEra().getField());
        TestCase.assertEquals("centuryOfEra", test.centuryOfEra().getName());
        TestCase.assertEquals("Property[centuryOfEra]", test.centuryOfEra().toString());
        TestCase.assertSame(test, test.centuryOfEra().getDateMidnight());
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
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().yearOfCentury(), test.yearOfCentury().getField());
        TestCase.assertEquals("yearOfCentury", test.yearOfCentury().getName());
        TestCase.assertEquals("Property[yearOfCentury]", test.yearOfCentury().toString());
        TestCase.assertSame(test, test.yearOfCentury().getDateMidnight());
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
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().weekyear(), test.weekyear().getField());
        TestCase.assertEquals("weekyear", test.weekyear().getName());
        TestCase.assertEquals("Property[weekyear]", test.weekyear().toString());
        TestCase.assertSame(test, test.weekyear().getDateMidnight());
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
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().year(), test.year().getField());
        TestCase.assertEquals("year", test.year().getName());
        TestCase.assertEquals("Property[year]", test.year().toString());
        TestCase.assertSame(test, test.year().getDateMidnight());
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

    // -----------------------------------------------------------------------
    public void testPropertyGetMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().monthOfYear(), test.monthOfYear().getField());
        TestCase.assertEquals("monthOfYear", test.monthOfYear().getName());
        TestCase.assertEquals("Property[monthOfYear]", test.monthOfYear().toString());
        TestCase.assertSame(test, test.monthOfYear().getDateMidnight());
        TestCase.assertEquals(6, test.monthOfYear().get());
        TestCase.assertEquals("6", test.monthOfYear().getAsString());
        TestCase.assertEquals("June", test.monthOfYear().getAsText());
        TestCase.assertEquals("juin", test.monthOfYear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("Jun", test.monthOfYear().getAsShortText());
        TestCase.assertEquals("juin", test.monthOfYear().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().months(), test.monthOfYear().getDurationField());
        TestCase.assertEquals(test.getChronology().years(), test.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(9, test.monthOfYear().getMaximumTextLength(null));
        TestCase.assertEquals(3, test.monthOfYear().getMaximumShortTextLength(null));
        test = new DateMidnight(2004, 7, 9);
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

    public void testPropertySetMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight copy = test.monthOfYear().setCopy(8);
        TestCase.assertEquals(2004, copy.getYear());
        TestCase.assertEquals(8, copy.getMonthOfYear());
        TestCase.assertEquals(9, copy.getDayOfMonth());
    }

    public void testPropertySetTextMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight copy = test.monthOfYear().setCopy("8");
        TestCase.assertEquals(2004, copy.getYear());
        TestCase.assertEquals(8, copy.getMonthOfYear());
        TestCase.assertEquals(9, copy.getDayOfMonth());
    }

    public void testPropertySetTextLocaleMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight copy = test.monthOfYear().setCopy("mars", Locale.FRENCH);
        TestCase.assertEquals(2004, copy.getYear());
        TestCase.assertEquals(3, copy.getMonthOfYear());
        TestCase.assertEquals(9, copy.getDayOfMonth());
    }

    public void testPropertyAddMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight copy = test.monthOfYear().addToCopy(8);
        TestCase.assertEquals(2005, copy.getYear());
        TestCase.assertEquals(2, copy.getMonthOfYear());
        TestCase.assertEquals(9, copy.getDayOfMonth());
    }

    public void testPropertyAddLongMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight copy = test.monthOfYear().addToCopy(8L);
        TestCase.assertEquals(2005, copy.getYear());
        TestCase.assertEquals(2, copy.getMonthOfYear());
        TestCase.assertEquals(9, copy.getDayOfMonth());
    }

    public void testPropertyAddWrapFieldMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight copy = test.monthOfYear().addWrapFieldToCopy(8);
        TestCase.assertEquals(2004, copy.getYear());
        TestCase.assertEquals(2, copy.getMonthOfYear());
        TestCase.assertEquals(9, copy.getDayOfMonth());
    }

    public void testPropertyGetDifferenceMonthOfYear() {
        DateMidnight test1 = new DateMidnight(2004, 6, 9);
        DateMidnight test2 = new DateMidnight(2004, 8, 9);
        TestCase.assertEquals((-2), test1.monthOfYear().getDifference(test2));
        TestCase.assertEquals(2, test2.monthOfYear().getDifference(test1));
        TestCase.assertEquals((-2L), test1.monthOfYear().getDifferenceAsLong(test2));
        TestCase.assertEquals(2L, test2.monthOfYear().getDifferenceAsLong(test1));
    }

    public void testPropertyRoundFloorMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 16);
        DateMidnight copy = test.monthOfYear().roundFloorCopy();
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyRoundCeilingMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 16);
        DateMidnight copy = test.monthOfYear().roundCeilingCopy();
        TestCase.assertEquals("2004-07-01T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyRoundHalfFloorMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 16);
        DateMidnight copy = test.monthOfYear().roundHalfFloorCopy();
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
        test = new DateMidnight(2004, 6, 17);
        copy = test.monthOfYear().roundHalfFloorCopy();
        TestCase.assertEquals("2004-07-01T00:00:00.000+01:00", copy.toString());
        test = new DateMidnight(2004, 6, 15);
        copy = test.monthOfYear().roundHalfFloorCopy();
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyRoundHalfCeilingMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 16);
        DateMidnight copy = test.monthOfYear().roundHalfCeilingCopy();
        TestCase.assertEquals("2004-07-01T00:00:00.000+01:00", copy.toString());
        test = new DateMidnight(2004, 6, 17);
        copy = test.monthOfYear().roundHalfCeilingCopy();
        TestCase.assertEquals("2004-07-01T00:00:00.000+01:00", copy.toString());
        test = new DateMidnight(2004, 6, 15);
        copy = test.monthOfYear().roundHalfCeilingCopy();
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyRoundHalfEvenMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 16);
        DateMidnight copy = test.monthOfYear().roundHalfEvenCopy();
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
        test = new DateMidnight(2004, 9, 16);
        copy = test.monthOfYear().roundHalfEvenCopy();
        TestCase.assertEquals("2004-10-01T00:00:00.000+01:00", copy.toString());
        test = new DateMidnight(2004, 6, 17);
        copy = test.monthOfYear().roundHalfEvenCopy();
        TestCase.assertEquals("2004-07-01T00:00:00.000+01:00", copy.toString());
        test = new DateMidnight(2004, 6, 15);
        copy = test.monthOfYear().roundHalfEvenCopy();
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyRemainderMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertEquals(((9L - 1L) * (MILLIS_PER_DAY)), test.monthOfYear().remainder());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetDayOfMonth() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().dayOfMonth(), test.dayOfMonth().getField());
        TestCase.assertEquals("dayOfMonth", test.dayOfMonth().getName());
        TestCase.assertEquals("Property[dayOfMonth]", test.dayOfMonth().toString());
        TestCase.assertSame(test, test.dayOfMonth().getDateMidnight());
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

    public void testPropertyWithMaximumValueDayOfMonth() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight copy = test.dayOfMonth().withMaximumValue();
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-30T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyWithMinimumValueDayOfMonth() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        DateMidnight copy = test.dayOfMonth().withMinimumValue();
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetDayOfYear() {
        // 31+29+31+30+31+9 = 161
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().dayOfYear(), test.dayOfYear().getField());
        TestCase.assertEquals("dayOfYear", test.dayOfYear().getName());
        TestCase.assertEquals("Property[dayOfYear]", test.dayOfYear().toString());
        TestCase.assertSame(test, test.dayOfYear().getDateMidnight());
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

    // -----------------------------------------------------------------------
    public void testPropertyGetWeekOfWeekyear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().weekOfWeekyear(), test.weekOfWeekyear().getField());
        TestCase.assertEquals("weekOfWeekyear", test.weekOfWeekyear().getName());
        TestCase.assertEquals("Property[weekOfWeekyear]", test.weekOfWeekyear().toString());
        TestCase.assertSame(test, test.weekOfWeekyear().getDateMidnight());
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

    // -----------------------------------------------------------------------
    public void testPropertyGetDayOfWeek() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        TestCase.assertSame(test.getChronology().dayOfWeek(), test.dayOfWeek().getField());
        TestCase.assertEquals("dayOfWeek", test.dayOfWeek().getName());
        TestCase.assertEquals("Property[dayOfWeek]", test.dayOfWeek().toString());
        TestCase.assertSame(test, test.dayOfWeek().getDateMidnight());
        TestCase.assertEquals(3, test.dayOfWeek().get());
        TestCase.assertEquals("3", test.dayOfWeek().getAsString());
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

    // -----------------------------------------------------------------------
    public void testPropertyToIntervalYearOfEra() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        Interval testInterval = test.yearOfEra().toInterval();
        TestCase.assertEquals(new DateMidnight(2004, 1, 1), testInterval.getStart());
        TestCase.assertEquals(new DateMidnight(2005, 1, 1), testInterval.getEnd());
    }

    public void testPropertyToIntervalYearOfCentury() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        Interval testInterval = test.yearOfCentury().toInterval();
        TestCase.assertEquals(new DateMidnight(2004, 1, 1), testInterval.getStart());
        TestCase.assertEquals(new DateMidnight(2005, 1, 1), testInterval.getEnd());
    }

    public void testPropertyToIntervalYear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        Interval testInterval = test.year().toInterval();
        TestCase.assertEquals(new DateMidnight(2004, 1, 1), testInterval.getStart());
        TestCase.assertEquals(new DateMidnight(2005, 1, 1), testInterval.getEnd());
    }

    public void testPropertyToIntervalMonthOfYear() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        Interval testInterval = test.monthOfYear().toInterval();
        TestCase.assertEquals(new DateMidnight(2004, 6, 1), testInterval.getStart());
        TestCase.assertEquals(new DateMidnight(2004, 7, 1), testInterval.getEnd());
    }

    public void testPropertyToIntervalDayOfMonth() {
        DateMidnight test = new DateMidnight(2004, 6, 9);
        Interval testInterval = test.dayOfMonth().toInterval();
        TestCase.assertEquals(new DateMidnight(2004, 6, 9), testInterval.getStart());
        TestCase.assertEquals(new DateMidnight(2004, 6, 10), testInterval.getEnd());
        DateMidnight febTest = new DateMidnight(2004, 2, 29);
        Interval febTestInterval = febTest.dayOfMonth().toInterval();
        TestCase.assertEquals(new DateMidnight(2004, 2, 29), febTestInterval.getStart());
        TestCase.assertEquals(new DateMidnight(2004, 3, 1), febTestInterval.getEnd());
    }

    public void testPropertyEqualsHashCodeLenient() {
        DateMidnight test1 = new DateMidnight(1970, 6, 9, LenientChronology.getInstance(TestDateMidnight_Properties.COPTIC_PARIS));
        DateMidnight test2 = new DateMidnight(1970, 6, 9, LenientChronology.getInstance(TestDateMidnight_Properties.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test1.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test2.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
    }

    public void testPropertyEqualsHashCodeStrict() {
        DateMidnight test1 = new DateMidnight(1970, 6, 9, StrictChronology.getInstance(TestDateMidnight_Properties.COPTIC_PARIS));
        DateMidnight test2 = new DateMidnight(1970, 6, 9, StrictChronology.getInstance(TestDateMidnight_Properties.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test1.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test2.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
    }
}

