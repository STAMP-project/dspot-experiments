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
public class TestDateTime_Properties extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestDateTime_Properties.PARIS);

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

    public TestDateTime_Properties(String name) {
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
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().era(), test.era().getField());
        TestCase.assertEquals("era", test.era().getName());
        TestCase.assertEquals("Property[era]", test.era().toString());
        TestCase.assertSame(test, test.era().getDateTime());
        TestCase.assertEquals(1, test.era().get());
        TestCase.assertEquals("1", test.era().getAsString());
        TestCase.assertEquals("AD", test.era().getAsText());
        TestCase.assertEquals("AD", test.era().getField().getAsText(1, Locale.ENGLISH));
        TestCase.assertEquals("ap. J.-C.", test.era().getAsText(Locale.FRENCH));
        TestCase.assertEquals("ap. J.-C.", test.era().getField().getAsText(1, Locale.FRENCH));
        TestCase.assertEquals("AD", test.era().getAsShortText());
        TestCase.assertEquals("AD", test.era().getField().getAsShortText(1, Locale.ENGLISH));
        TestCase.assertEquals("ap. J.-C.", test.era().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals("ap. J.-C.", test.era().getField().getAsShortText(1, Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().eras(), test.era().getDurationField());
        TestCase.assertEquals(null, test.era().getRangeDurationField());
        TestCase.assertEquals(2, test.era().getMaximumTextLength(null));
        TestCase.assertEquals(9, test.era().getMaximumTextLength(Locale.FRENCH));
        TestCase.assertEquals(2, test.era().getMaximumShortTextLength(null));
        TestCase.assertEquals(9, test.era().getMaximumShortTextLength(Locale.FRENCH));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetYearOfEra() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().yearOfEra(), test.yearOfEra().getField());
        TestCase.assertEquals("yearOfEra", test.yearOfEra().getName());
        TestCase.assertEquals("Property[yearOfEra]", test.yearOfEra().toString());
        TestCase.assertSame(test, test.yearOfEra().getDateTime());
        TestCase.assertEquals(2004, test.yearOfEra().get());
        TestCase.assertEquals("2004", test.yearOfEra().getAsString());
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
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().centuryOfEra(), test.centuryOfEra().getField());
        TestCase.assertEquals("centuryOfEra", test.centuryOfEra().getName());
        TestCase.assertEquals("Property[centuryOfEra]", test.centuryOfEra().toString());
        TestCase.assertSame(test, test.centuryOfEra().getDateTime());
        TestCase.assertEquals(20, test.centuryOfEra().get());
        TestCase.assertEquals("20", test.centuryOfEra().getAsString());
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
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().yearOfCentury(), test.yearOfCentury().getField());
        TestCase.assertEquals("yearOfCentury", test.yearOfCentury().getName());
        TestCase.assertEquals("Property[yearOfCentury]", test.yearOfCentury().toString());
        TestCase.assertSame(test, test.yearOfCentury().getDateTime());
        TestCase.assertEquals(4, test.yearOfCentury().get());
        TestCase.assertEquals("4", test.yearOfCentury().getAsString());
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
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().weekyear(), test.weekyear().getField());
        TestCase.assertEquals("weekyear", test.weekyear().getName());
        TestCase.assertEquals("Property[weekyear]", test.weekyear().toString());
        TestCase.assertSame(test, test.weekyear().getDateTime());
        TestCase.assertEquals(2004, test.weekyear().get());
        TestCase.assertEquals("2004", test.weekyear().getAsString());
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
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(year(), test.year().getField());
        TestCase.assertEquals("year", test.year().getName());
        TestCase.assertEquals("Property[year]", test.year().toString());
        TestCase.assertSame(test, test.year().getDateTime());
        TestCase.assertEquals(2004, test.year().get());
        TestCase.assertEquals("2004", test.year().getAsString());
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

    public void testPropertyLeapYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertEquals(true, test.year().isLeap());
        TestCase.assertEquals(1, test.year().getLeapAmount());
        TestCase.assertEquals(test.getChronology().days(), test.year().getLeapDurationField());
        test = new DateTime(2003, 6, 9, 0, 0, 0, 0);
        TestCase.assertEquals(false, test.year().isLeap());
        TestCase.assertEquals(0, test.year().getLeapAmount());
        TestCase.assertEquals(test.getChronology().days(), test.year().getLeapDurationField());
    }

    public void testPropertyAddYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.year().addToCopy(9);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2013-06-09T00:00:00.000+01:00", copy.toString());
        copy = test.year().addToCopy(0);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", copy.toString());
        copy = test.year().addToCopy((292277023 - 2004));
        TestCase.assertEquals(292277023, copy.getYear());
        try {
            test.year().addToCopy(((292278993 - 2004) + 1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        copy = test.year().addToCopy((-2004));
        TestCase.assertEquals(0, copy.getYear());
        copy = test.year().addToCopy((-2005));
        TestCase.assertEquals((-1), copy.getYear());
        try {
            test.year().addToCopy((((-292275054) - 2004) - 1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertyAddWrapFieldYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.year().addWrapFieldToCopy(9);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2013-06-09T00:00:00.000+01:00", copy.toString());
        copy = test.year().addWrapFieldToCopy(0);
        TestCase.assertEquals(2004, copy.getYear());
        copy = test.year().addWrapFieldToCopy(((292278993 - 2004) + 1));
        TestCase.assertEquals((-292275054), copy.getYear());
        copy = test.year().addWrapFieldToCopy((((-292275054) - 2004) - 1));
        TestCase.assertEquals(292278993, copy.getYear());
    }

    public void testPropertySetYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.year().setCopy(1960);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("1960-06-09T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertySetTextYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.year().setCopy("1960");
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("1960-06-09T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyCompareToYear() {
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.year().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.year().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.year().compareTo(test1)) == 0));
        try {
            test1.year().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertyEqualsHashCodeYear() {
        DateTime test1 = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertEquals(true, test1.year().equals(test1.year()));
        TestCase.assertEquals(true, test1.year().equals(new DateTime(2004, 6, 9, 0, 0, 0, 0).year()));
        TestCase.assertEquals(false, test1.year().equals(new DateTime(2004, 6, 9, 0, 0, 0, 0).monthOfYear()));
        TestCase.assertEquals(false, test1.year().equals(year()));
        TestCase.assertEquals(true, ((test1.year().hashCode()) == (test1.year().hashCode())));
        TestCase.assertEquals(true, ((test1.year().hashCode()) == (new DateTime(2004, 6, 9, 0, 0, 0, 0).year().hashCode())));
        TestCase.assertEquals(false, ((test1.year().hashCode()) == (new DateTime(2004, 6, 9, 0, 0, 0, 0).monthOfYear().hashCode())));
        TestCase.assertEquals(false, ((test1.year().hashCode()) == (year().hashCode())));
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMonthOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().monthOfYear(), test.monthOfYear().getField());
        TestCase.assertEquals("monthOfYear", test.monthOfYear().getName());
        TestCase.assertEquals("Property[monthOfYear]", test.monthOfYear().toString());
        TestCase.assertSame(test, test.monthOfYear().getDateTime());
        TestCase.assertEquals(6, test.monthOfYear().get());
        TestCase.assertEquals("6", test.monthOfYear().getAsString());
        TestCase.assertEquals("June", test.monthOfYear().getAsText());
        TestCase.assertEquals("June", test.monthOfYear().getField().getAsText(6, Locale.ENGLISH));
        TestCase.assertEquals("juin", test.monthOfYear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("juin", test.monthOfYear().getField().getAsText(6, Locale.FRENCH));
        TestCase.assertEquals("Jun", test.monthOfYear().getAsShortText());
        TestCase.assertEquals("Jun", test.monthOfYear().getField().getAsShortText(6, Locale.ENGLISH));
        TestCase.assertEquals("juin", test.monthOfYear().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals("juin", test.monthOfYear().getField().getAsShortText(6, Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().months(), test.monthOfYear().getDurationField());
        TestCase.assertEquals(test.getChronology().years(), test.monthOfYear().getRangeDurationField());
        TestCase.assertEquals(9, test.monthOfYear().getMaximumTextLength(null));
        TestCase.assertEquals(3, test.monthOfYear().getMaximumShortTextLength(null));
        test = new DateTime(2004, 7, 9, 0, 0, 0, 0);
        TestCase.assertEquals("juillet", test.monthOfYear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("juillet", test.monthOfYear().getField().getAsText(7, Locale.FRENCH));
        TestCase.assertEquals("juil.", test.monthOfYear().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals("juil.", test.monthOfYear().getField().getAsShortText(7, Locale.FRENCH));
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValue());
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValueOverall());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValue());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValueOverall());
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValue());
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValueOverall());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValue());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValueOverall());
    }

    public void testPropertyLeapMonthOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertEquals(false, test.monthOfYear().isLeap());
        TestCase.assertEquals(0, test.monthOfYear().getLeapAmount());
        TestCase.assertEquals(test.getChronology().days(), test.monthOfYear().getLeapDurationField());
        test = new DateTime(2004, 2, 9, 0, 0, 0, 0);
        TestCase.assertEquals(true, test.monthOfYear().isLeap());
        TestCase.assertEquals(1, test.monthOfYear().getLeapAmount());
        TestCase.assertEquals(test.getChronology().days(), test.monthOfYear().getLeapDurationField());
        test = new DateTime(2003, 6, 9, 0, 0, 0, 0);
        TestCase.assertEquals(false, test.monthOfYear().isLeap());
        TestCase.assertEquals(0, test.monthOfYear().getLeapAmount());
        TestCase.assertEquals(test.getChronology().days(), test.monthOfYear().getLeapDurationField());
        test = new DateTime(2003, 2, 9, 0, 0, 0, 0);
        TestCase.assertEquals(false, test.monthOfYear().isLeap());
        TestCase.assertEquals(0, test.monthOfYear().getLeapAmount());
        TestCase.assertEquals(test.getChronology().days(), test.monthOfYear().getLeapDurationField());
    }

    public void testPropertyAddMonthOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.monthOfYear().addToCopy(6);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-12-09T00:00:00.000Z", copy.toString());
        copy = test.monthOfYear().addToCopy(7);
        TestCase.assertEquals("2005-01-09T00:00:00.000Z", copy.toString());
        copy = test.monthOfYear().addToCopy((-5));
        TestCase.assertEquals("2004-01-09T00:00:00.000Z", copy.toString());
        copy = test.monthOfYear().addToCopy((-6));
        TestCase.assertEquals("2003-12-09T00:00:00.000Z", copy.toString());
        test = new DateTime(2004, 1, 31, 0, 0, 0, 0);
        copy = test.monthOfYear().addToCopy(1);
        TestCase.assertEquals("2004-01-31T00:00:00.000Z", test.toString());
        TestCase.assertEquals("2004-02-29T00:00:00.000Z", copy.toString());
        copy = test.monthOfYear().addToCopy(2);
        TestCase.assertEquals("2004-03-31T00:00:00.000+01:00", copy.toString());
        copy = test.monthOfYear().addToCopy(3);
        TestCase.assertEquals("2004-04-30T00:00:00.000+01:00", copy.toString());
        test = new DateTime(2003, 1, 31, 0, 0, 0, 0);
        copy = test.monthOfYear().addToCopy(1);
        TestCase.assertEquals("2003-02-28T00:00:00.000Z", copy.toString());
    }

    public void testPropertyAddWrapFieldMonthOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.monthOfYear().addWrapFieldToCopy(4);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-10-09T00:00:00.000+01:00", copy.toString());
        copy = test.monthOfYear().addWrapFieldToCopy(8);
        TestCase.assertEquals("2004-02-09T00:00:00.000Z", copy.toString());
        copy = test.monthOfYear().addWrapFieldToCopy((-8));
        TestCase.assertEquals("2004-10-09T00:00:00.000+01:00", copy.toString());
        test = new DateTime(2004, 1, 31, 0, 0, 0, 0);
        copy = test.monthOfYear().addWrapFieldToCopy(1);
        TestCase.assertEquals("2004-01-31T00:00:00.000Z", test.toString());
        TestCase.assertEquals("2004-02-29T00:00:00.000Z", copy.toString());
        copy = test.monthOfYear().addWrapFieldToCopy(2);
        TestCase.assertEquals("2004-03-31T00:00:00.000+01:00", copy.toString());
        copy = test.monthOfYear().addWrapFieldToCopy(3);
        TestCase.assertEquals("2004-04-30T00:00:00.000+01:00", copy.toString());
        test = new DateTime(2005, 1, 31, 0, 0, 0, 0);
        copy = test.monthOfYear().addWrapFieldToCopy(1);
        TestCase.assertEquals("2005-01-31T00:00:00.000Z", test.toString());
        TestCase.assertEquals("2005-02-28T00:00:00.000Z", copy.toString());
    }

    public void testPropertySetMonthOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.monthOfYear().setCopy(12);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-12-09T00:00:00.000Z", copy.toString());
        test = new DateTime(2004, 1, 31, 0, 0, 0, 0);
        copy = test.monthOfYear().setCopy(2);
        TestCase.assertEquals("2004-02-29T00:00:00.000Z", copy.toString());
        try {
            test.monthOfYear().setCopy(13);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.monthOfYear().setCopy(0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextMonthOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.monthOfYear().setCopy("12");
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-12-09T00:00:00.000Z", copy.toString());
        copy = test.monthOfYear().setCopy("December");
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-12-09T00:00:00.000Z", copy.toString());
        copy = test.monthOfYear().setCopy("Dec");
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-12-09T00:00:00.000Z", copy.toString());
    }

    public void testPropertyCompareToMonthOfYear() {
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.monthOfYear().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.monthOfYear().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.monthOfYear().compareTo(test1)) == 0));
        try {
            test1.monthOfYear().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.monthOfYear().compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.monthOfYear().compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.monthOfYear().compareTo(dt1)) == 0));
        try {
            test1.monthOfYear().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetDayOfMonth() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().dayOfMonth(), test.dayOfMonth().getField());
        TestCase.assertEquals("dayOfMonth", test.dayOfMonth().getName());
        TestCase.assertEquals("Property[dayOfMonth]", test.dayOfMonth().toString());
        TestCase.assertSame(test, test.dayOfMonth().getDateTime());
        TestCase.assertEquals(9, test.dayOfMonth().get());
        TestCase.assertEquals("9", test.dayOfMonth().getAsString());
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

    public void testPropertyGetMaxMinValuesDayOfMonth() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertEquals(1, test.dayOfMonth().getMinimumValue());
        TestCase.assertEquals(1, test.dayOfMonth().getMinimumValueOverall());
        TestCase.assertEquals(30, test.dayOfMonth().getMaximumValue());
        TestCase.assertEquals(31, test.dayOfMonth().getMaximumValueOverall());
        test = new DateTime(2004, 7, 9, 0, 0, 0, 0);
        TestCase.assertEquals(31, test.dayOfMonth().getMaximumValue());
        test = new DateTime(2004, 2, 9, 0, 0, 0, 0);
        TestCase.assertEquals(29, test.dayOfMonth().getMaximumValue());
        test = new DateTime(2003, 2, 9, 0, 0, 0, 0);
        TestCase.assertEquals(28, test.dayOfMonth().getMaximumValue());
    }

    public void testPropertyAddDayOfMonth() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfMonth().addToCopy(9);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-18T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addToCopy(21);
        TestCase.assertEquals("2004-06-30T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addToCopy(22);
        TestCase.assertEquals("2004-07-01T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addToCopy((22 + 30));
        TestCase.assertEquals("2004-07-31T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addToCopy((22 + 31));
        TestCase.assertEquals("2004-08-01T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addToCopy(((((((21 + 31) + 31) + 30) + 31) + 30) + 31));
        TestCase.assertEquals("2004-12-31T00:00:00.000Z", copy.toString());
        copy = test.dayOfMonth().addToCopy(((((((22 + 31) + 31) + 30) + 31) + 30) + 31));
        TestCase.assertEquals("2005-01-01T00:00:00.000Z", copy.toString());
        copy = test.dayOfMonth().addToCopy((-8));
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addToCopy((-9));
        TestCase.assertEquals("2004-05-31T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addToCopy(((((((-8) - 31) - 30) - 31) - 29) - 31));
        TestCase.assertEquals("2004-01-01T00:00:00.000Z", copy.toString());
        copy = test.dayOfMonth().addToCopy(((((((-9) - 31) - 30) - 31) - 29) - 31));
        TestCase.assertEquals("2003-12-31T00:00:00.000Z", copy.toString());
    }

    public void testPropertyAddWrapFieldDayOfMonth() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfMonth().addWrapFieldToCopy(21);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-30T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addWrapFieldToCopy(22);
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addWrapFieldToCopy((-12));
        TestCase.assertEquals("2004-06-27T00:00:00.000+01:00", copy.toString());
        test = new DateTime(2004, 7, 9, 0, 0, 0, 0);
        copy = test.dayOfMonth().addWrapFieldToCopy(21);
        TestCase.assertEquals("2004-07-30T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addWrapFieldToCopy(22);
        TestCase.assertEquals("2004-07-31T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addWrapFieldToCopy(23);
        TestCase.assertEquals("2004-07-01T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfMonth().addWrapFieldToCopy((-12));
        TestCase.assertEquals("2004-07-28T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertySetDayOfMonth() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfMonth().setCopy(12);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-12T00:00:00.000+01:00", copy.toString());
        try {
            test.dayOfMonth().setCopy(31);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.dayOfMonth().setCopy(0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextDayOfMonth() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfMonth().setCopy("12");
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-12T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyWithMaximumValueDayOfMonth() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfMonth().withMaximumValue();
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-30T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyWithMaximumValueMillisOfDayDSTGap() {
        DateTimeZone paris = DateTimeZone.forID("Europe/Paris");
        DateTime dt = new DateTime(1926, 4, 17, 18, 0, 0, 0, paris);// DST gap 23:00 to 00:00

        DateTime test = dt.millisOfDay().withMaximumValue();
        TestCase.assertEquals("1926-04-17T22:59:59.999Z", test.toString());
    }

    public void testPropertyWithMinimumValueDayOfMonth() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfMonth().withMinimumValue();
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyWithMinimumValueMillisOfDayDSTGap() {
        DateTimeZone gaza = DateTimeZone.forID("Asia/Gaza");
        DateTime dt = new DateTime(2001, 4, 20, 18, 0, 0, 0, gaza);// DST gap 00:00 to 01:00

        DateTime test = dt.millisOfDay().withMinimumValue();
        TestCase.assertEquals("2001-04-20T01:00:00.000+03:00", test.toString());
    }

    public void testPropertyCompareToDayOfMonth() {
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.dayOfMonth().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.dayOfMonth().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.dayOfMonth().compareTo(test1)) == 0));
        try {
            test1.dayOfMonth().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.dayOfMonth().compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.dayOfMonth().compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.dayOfMonth().compareTo(dt1)) == 0));
        try {
            test1.dayOfMonth().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetDayOfYear() {
        // 31+29+31+30+31+9 = 161
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().dayOfYear(), test.dayOfYear().getField());
        TestCase.assertEquals("dayOfYear", test.dayOfYear().getName());
        TestCase.assertEquals("Property[dayOfYear]", test.dayOfYear().toString());
        TestCase.assertSame(test, test.dayOfYear().getDateTime());
        TestCase.assertEquals(161, test.dayOfYear().get());
        TestCase.assertEquals("161", test.dayOfYear().getAsString());
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

    public void testPropertyGetMaxMinValuesDayOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertEquals(1, test.dayOfYear().getMinimumValue());
        TestCase.assertEquals(1, test.dayOfYear().getMinimumValueOverall());
        TestCase.assertEquals(366, test.dayOfYear().getMaximumValue());
        TestCase.assertEquals(366, test.dayOfYear().getMaximumValueOverall());
        test = new DateTime(2002, 6, 9, 0, 0, 0, 0);
        TestCase.assertEquals(365, test.dayOfYear().getMaximumValue());
        TestCase.assertEquals(366, test.dayOfYear().getMaximumValueOverall());
    }

    public void testPropertyAddDayOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfYear().addToCopy(9);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-18T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfYear().addToCopy(21);
        TestCase.assertEquals("2004-06-30T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfYear().addToCopy(22);
        TestCase.assertEquals("2004-07-01T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfYear().addToCopy(((((((21 + 31) + 31) + 30) + 31) + 30) + 31));
        TestCase.assertEquals("2004-12-31T00:00:00.000Z", copy.toString());
        copy = test.dayOfYear().addToCopy(((((((22 + 31) + 31) + 30) + 31) + 30) + 31));
        TestCase.assertEquals("2005-01-01T00:00:00.000Z", copy.toString());
        copy = test.dayOfYear().addToCopy((-8));
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfYear().addToCopy((-9));
        TestCase.assertEquals("2004-05-31T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfYear().addToCopy(((((((-8) - 31) - 30) - 31) - 29) - 31));
        TestCase.assertEquals("2004-01-01T00:00:00.000Z", copy.toString());
        copy = test.dayOfYear().addToCopy(((((((-9) - 31) - 30) - 31) - 29) - 31));
        TestCase.assertEquals("2003-12-31T00:00:00.000Z", copy.toString());
    }

    public void testPropertyAddWrapFieldDayOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfYear().addWrapFieldToCopy(21);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-30T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfYear().addWrapFieldToCopy(22);
        TestCase.assertEquals("2004-07-01T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfYear().addWrapFieldToCopy((-12));
        TestCase.assertEquals("2004-05-28T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfYear().addWrapFieldToCopy(205);
        TestCase.assertEquals("2004-12-31T00:00:00.000Z", copy.toString());
        copy = test.dayOfYear().addWrapFieldToCopy(206);
        TestCase.assertEquals("2004-01-01T00:00:00.000Z", copy.toString());
        copy = test.dayOfYear().addWrapFieldToCopy((-160));
        TestCase.assertEquals("2004-01-01T00:00:00.000Z", copy.toString());
        copy = test.dayOfYear().addWrapFieldToCopy((-161));
        TestCase.assertEquals("2004-12-31T00:00:00.000Z", copy.toString());
    }

    public void testPropertySetDayOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfYear().setCopy(12);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-01-12T00:00:00.000Z", copy.toString());
        try {
            test.dayOfYear().setCopy(367);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.dayOfYear().setCopy(0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextDayOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfYear().setCopy("12");
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-01-12T00:00:00.000Z", copy.toString());
    }

    public void testPropertyCompareToDayOfYear() {
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.dayOfYear().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.dayOfYear().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.dayOfYear().compareTo(test1)) == 0));
        try {
            test1.dayOfYear().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.dayOfYear().compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.dayOfYear().compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.dayOfYear().compareTo(dt1)) == 0));
        try {
            test1.dayOfYear().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
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
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().weekOfWeekyear(), test.weekOfWeekyear().getField());
        TestCase.assertEquals("weekOfWeekyear", test.weekOfWeekyear().getName());
        TestCase.assertEquals("Property[weekOfWeekyear]", test.weekOfWeekyear().toString());
        TestCase.assertSame(test, test.weekOfWeekyear().getDateTime());
        TestCase.assertEquals(24, test.weekOfWeekyear().get());
        TestCase.assertEquals("24", test.weekOfWeekyear().getAsString());
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

    public void testPropertyGetMaxMinValuesWeekOfWeekyear() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertEquals(1, test.weekOfWeekyear().getMinimumValue());
        TestCase.assertEquals(1, test.weekOfWeekyear().getMinimumValueOverall());
        TestCase.assertEquals(53, test.weekOfWeekyear().getMaximumValue());
        TestCase.assertEquals(53, test.weekOfWeekyear().getMaximumValueOverall());
        test = new DateTime(2005, 6, 9, 0, 0, 0, 0);
        TestCase.assertEquals(52, test.weekOfWeekyear().getMaximumValue());
        TestCase.assertEquals(53, test.weekOfWeekyear().getMaximumValueOverall());
    }

    public void testPropertyAddWeekOfWeekyear() {
        DateTime test = new DateTime(2004, 6, 7, 0, 0, 0, 0);
        DateTime copy = test.weekOfWeekyear().addToCopy(1);
        TestCase.assertEquals("2004-06-07T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-14T00:00:00.000+01:00", copy.toString());
        copy = test.weekOfWeekyear().addToCopy(29);
        TestCase.assertEquals("2004-12-27T00:00:00.000Z", copy.toString());
        copy = test.weekOfWeekyear().addToCopy(30);
        TestCase.assertEquals("2005-01-03T00:00:00.000Z", copy.toString());
        copy = test.weekOfWeekyear().addToCopy((-22));
        TestCase.assertEquals("2004-01-05T00:00:00.000Z", copy.toString());
        copy = test.weekOfWeekyear().addToCopy((-23));
        TestCase.assertEquals("2003-12-29T00:00:00.000Z", copy.toString());
    }

    public void testPropertyAddWrapFieldWeekOfWeekyear() {
        DateTime test = new DateTime(2004, 6, 7, 0, 0, 0, 0);
        DateTime copy = test.weekOfWeekyear().addWrapFieldToCopy(1);
        TestCase.assertEquals("2004-06-07T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-14T00:00:00.000+01:00", copy.toString());
        copy = test.weekOfWeekyear().addWrapFieldToCopy(29);
        TestCase.assertEquals("2004-12-27T00:00:00.000Z", copy.toString());
        copy = test.weekOfWeekyear().addWrapFieldToCopy(30);
        TestCase.assertEquals("2003-12-29T00:00:00.000Z", copy.toString());
        copy = test.weekOfWeekyear().addWrapFieldToCopy((-23));
        TestCase.assertEquals("2003-12-29T00:00:00.000Z", copy.toString());
    }

    public void testPropertySetWeekOfWeekyear() {
        DateTime test = new DateTime(2004, 6, 7, 0, 0, 0, 0);
        DateTime copy = test.weekOfWeekyear().setCopy(4);
        TestCase.assertEquals("2004-06-07T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-01-19T00:00:00.000Z", copy.toString());
        try {
            test.weekOfWeekyear().setCopy(54);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.weekOfWeekyear().setCopy(0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextWeekOfWeekyear() {
        DateTime test = new DateTime(2004, 6, 7, 0, 0, 0, 0);
        DateTime copy = test.weekOfWeekyear().setCopy("4");
        TestCase.assertEquals("2004-06-07T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-01-19T00:00:00.000Z", copy.toString());
    }

    public void testPropertyCompareToWeekOfWeekyear() {
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.weekOfWeekyear().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.weekOfWeekyear().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.weekOfWeekyear().compareTo(test1)) == 0));
        try {
            test1.weekOfWeekyear().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.weekOfWeekyear().compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.weekOfWeekyear().compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.weekOfWeekyear().compareTo(dt1)) == 0));
        try {
            test1.weekOfWeekyear().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetDayOfWeek() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        TestCase.assertSame(test.getChronology().dayOfWeek(), test.dayOfWeek().getField());
        TestCase.assertEquals("dayOfWeek", test.dayOfWeek().getName());
        TestCase.assertEquals("Property[dayOfWeek]", test.dayOfWeek().toString());
        TestCase.assertSame(test, test.dayOfWeek().getDateTime());
        TestCase.assertEquals(3, test.dayOfWeek().get());
        TestCase.assertEquals("3", test.dayOfWeek().getAsString());
        TestCase.assertEquals("Wednesday", test.dayOfWeek().getAsText());
        TestCase.assertEquals("Wednesday", test.dayOfWeek().getField().getAsText(3, Locale.ENGLISH));
        TestCase.assertEquals("mercredi", test.dayOfWeek().getAsText(Locale.FRENCH));
        TestCase.assertEquals("mercredi", test.dayOfWeek().getField().getAsText(3, Locale.FRENCH));
        TestCase.assertEquals("Wed", test.dayOfWeek().getAsShortText());
        TestCase.assertEquals("Wed", test.dayOfWeek().getField().getAsShortText(3, Locale.ENGLISH));
        TestCase.assertEquals("mer.", test.dayOfWeek().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals("mer.", test.dayOfWeek().getField().getAsShortText(3, Locale.FRENCH));
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
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfWeek().addToCopy(1);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-10T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfWeek().addToCopy(21);
        TestCase.assertEquals("2004-06-30T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfWeek().addToCopy(22);
        TestCase.assertEquals("2004-07-01T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfWeek().addToCopy(((((((21 + 31) + 31) + 30) + 31) + 30) + 31));
        TestCase.assertEquals("2004-12-31T00:00:00.000Z", copy.toString());
        copy = test.dayOfWeek().addToCopy(((((((22 + 31) + 31) + 30) + 31) + 30) + 31));
        TestCase.assertEquals("2005-01-01T00:00:00.000Z", copy.toString());
        copy = test.dayOfWeek().addToCopy((-8));
        TestCase.assertEquals("2004-06-01T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfWeek().addToCopy((-9));
        TestCase.assertEquals("2004-05-31T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfWeek().addToCopy(((((((-8) - 31) - 30) - 31) - 29) - 31));
        TestCase.assertEquals("2004-01-01T00:00:00.000Z", copy.toString());
        copy = test.dayOfWeek().addToCopy(((((((-9) - 31) - 30) - 31) - 29) - 31));
        TestCase.assertEquals("2003-12-31T00:00:00.000Z", copy.toString());
    }

    public void testPropertyAddLongDayOfWeek() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfWeek().addToCopy(1L);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-10T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyAddWrapFieldDayOfWeek() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);// Wed

        DateTime copy = test.dayOfWeek().addWrapFieldToCopy(1);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-10T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfWeek().addWrapFieldToCopy(5);
        TestCase.assertEquals("2004-06-07T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfWeek().addWrapFieldToCopy((-10));
        TestCase.assertEquals("2004-06-13T00:00:00.000+01:00", copy.toString());
        test = new DateTime(2004, 6, 2, 0, 0, 0, 0);
        copy = test.dayOfWeek().addWrapFieldToCopy(5);
        TestCase.assertEquals("2004-06-02T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-05-31T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertySetDayOfWeek() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfWeek().setCopy(4);
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-10T00:00:00.000+01:00", copy.toString());
        try {
            test.dayOfWeek().setCopy(8);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.dayOfWeek().setCopy(0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextDayOfWeek() {
        DateTime test = new DateTime(2004, 6, 9, 0, 0, 0, 0);
        DateTime copy = test.dayOfWeek().setCopy("4");
        TestCase.assertEquals("2004-06-09T00:00:00.000+01:00", test.toString());
        TestCase.assertEquals("2004-06-10T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfWeek().setCopy("Mon");
        TestCase.assertEquals("2004-06-07T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfWeek().setCopy("Tuesday");
        TestCase.assertEquals("2004-06-08T00:00:00.000+01:00", copy.toString());
        copy = test.dayOfWeek().setCopy("lundi", Locale.FRENCH);
        TestCase.assertEquals("2004-06-07T00:00:00.000+01:00", copy.toString());
    }

    public void testPropertyCompareToDayOfWeek() {
        DateTime test1 = new DateTime(TEST_TIME1);
        DateTime test2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test2.dayOfWeek().compareTo(test1)) < 0));
        TestCase.assertEquals(true, ((test1.dayOfWeek().compareTo(test2)) > 0));
        TestCase.assertEquals(true, ((test1.dayOfWeek().compareTo(test1)) == 0));
        try {
            test1.dayOfWeek().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test2.dayOfWeek().compareTo(dt1)) < 0));
        TestCase.assertEquals(true, ((test1.dayOfWeek().compareTo(dt2)) > 0));
        TestCase.assertEquals(true, ((test1.dayOfWeek().compareTo(dt1)) == 0));
        try {
            test1.dayOfWeek().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetHourOfDay() {
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().hourOfDay(), test.hourOfDay().getField());
        TestCase.assertEquals("hourOfDay", test.hourOfDay().getName());
        TestCase.assertEquals("Property[hourOfDay]", test.hourOfDay().toString());
        TestCase.assertSame(test, test.hourOfDay().getDateTime());
        TestCase.assertEquals(13, test.hourOfDay().get());
        TestCase.assertEquals("13", test.hourOfDay().getAsString());
        TestCase.assertEquals("13", test.hourOfDay().getAsText());
        TestCase.assertEquals("13", test.hourOfDay().getAsText(Locale.FRENCH));
        TestCase.assertEquals("13", test.hourOfDay().getAsShortText());
        TestCase.assertEquals("13", test.hourOfDay().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().hours(), test.hourOfDay().getDurationField());
        TestCase.assertEquals(test.getChronology().days(), test.hourOfDay().getRangeDurationField());
        TestCase.assertEquals(2, test.hourOfDay().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.hourOfDay().getMaximumShortTextLength(null));
    }

    public void testPropertyGetDifferenceHourOfDay() {
        DateTime test1 = new DateTime(2004, 6, 9, 13, 30, 0, 0);
        DateTime test2 = new DateTime(2004, 6, 9, 15, 30, 0, 0);
        TestCase.assertEquals((-2), test1.hourOfDay().getDifference(test2));
        TestCase.assertEquals(2, test2.hourOfDay().getDifference(test1));
        TestCase.assertEquals((-2L), test1.hourOfDay().getDifferenceAsLong(test2));
        TestCase.assertEquals(2L, test2.hourOfDay().getDifferenceAsLong(test1));
        DateTime test = new DateTime(((TEST_TIME_NOW) + (13L * (MILLIS_PER_HOUR))));
        TestCase.assertEquals(13, test.hourOfDay().getDifference(null));
        TestCase.assertEquals(13L, test.hourOfDay().getDifferenceAsLong(null));
    }

    public void testPropertyRoundFloorHourOfDay() {
        DateTime test = new DateTime(2004, 6, 9, 13, 30, 0, 0);
        DateTime copy = test.hourOfDay().roundFloorCopy();
        TestCase.assertEquals("2004-06-09T13:00:00.000+01:00", copy.toString());
    }

    public void testPropertyRoundCeilingHourOfDay() {
        DateTime test = new DateTime(2004, 6, 9, 13, 30, 0, 0);
        DateTime copy = test.hourOfDay().roundCeilingCopy();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", copy.toString());
    }

    public void testPropertyRoundHalfFloorHourOfDay() {
        DateTime test = new DateTime(2004, 6, 9, 13, 30, 0, 0);
        DateTime copy = test.hourOfDay().roundHalfFloorCopy();
        TestCase.assertEquals("2004-06-09T13:00:00.000+01:00", copy.toString());
        test = new DateTime(2004, 6, 9, 13, 30, 0, 1);
        copy = test.hourOfDay().roundHalfFloorCopy();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", copy.toString());
        test = new DateTime(2004, 6, 9, 13, 29, 59, 999);
        copy = test.hourOfDay().roundHalfFloorCopy();
        TestCase.assertEquals("2004-06-09T13:00:00.000+01:00", copy.toString());
    }

    public void testPropertyRoundHalfCeilingHourOfDay() {
        DateTime test = new DateTime(2004, 6, 9, 13, 30, 0, 0);
        DateTime copy = test.hourOfDay().roundHalfCeilingCopy();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", copy.toString());
        test = new DateTime(2004, 6, 9, 13, 30, 0, 1);
        copy = test.hourOfDay().roundHalfCeilingCopy();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", copy.toString());
        test = new DateTime(2004, 6, 9, 13, 29, 59, 999);
        copy = test.hourOfDay().roundHalfCeilingCopy();
        TestCase.assertEquals("2004-06-09T13:00:00.000+01:00", copy.toString());
    }

    public void testPropertyRoundHalfEvenHourOfDay() {
        DateTime test = new DateTime(2004, 6, 9, 13, 30, 0, 0);
        DateTime copy = test.hourOfDay().roundHalfEvenCopy();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", copy.toString());
        test = new DateTime(2004, 6, 9, 14, 30, 0, 0);
        copy = test.hourOfDay().roundHalfEvenCopy();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", copy.toString());
        test = new DateTime(2004, 6, 9, 13, 30, 0, 1);
        copy = test.hourOfDay().roundHalfEvenCopy();
        TestCase.assertEquals("2004-06-09T14:00:00.000+01:00", copy.toString());
        test = new DateTime(2004, 6, 9, 13, 29, 59, 999);
        copy = test.hourOfDay().roundHalfEvenCopy();
        TestCase.assertEquals("2004-06-09T13:00:00.000+01:00", copy.toString());
    }

    public void testPropertyRemainderHourOfDay() {
        DateTime test = new DateTime(2004, 6, 9, 13, 30, 0, 0);
        TestCase.assertEquals((30L * (MILLIS_PER_MINUTE)), test.hourOfDay().remainder());
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMinuteOfHour() {
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().minuteOfHour(), test.minuteOfHour().getField());
        TestCase.assertEquals("minuteOfHour", test.minuteOfHour().getName());
        TestCase.assertEquals("Property[minuteOfHour]", test.minuteOfHour().toString());
        TestCase.assertSame(test, test.minuteOfHour().getDateTime());
        TestCase.assertEquals(23, test.minuteOfHour().get());
        TestCase.assertEquals("23", test.minuteOfHour().getAsString());
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
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().minuteOfDay(), test.minuteOfDay().getField());
        TestCase.assertEquals("minuteOfDay", test.minuteOfDay().getName());
        TestCase.assertEquals("Property[minuteOfDay]", test.minuteOfDay().toString());
        TestCase.assertSame(test, test.minuteOfDay().getDateTime());
        TestCase.assertEquals(803, test.minuteOfDay().get());
        TestCase.assertEquals("803", test.minuteOfDay().getAsString());
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
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().secondOfMinute(), test.secondOfMinute().getField());
        TestCase.assertEquals("secondOfMinute", test.secondOfMinute().getName());
        TestCase.assertEquals("Property[secondOfMinute]", test.secondOfMinute().toString());
        TestCase.assertSame(test, test.secondOfMinute().getDateTime());
        TestCase.assertEquals(43, test.secondOfMinute().get());
        TestCase.assertEquals("43", test.secondOfMinute().getAsString());
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
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().secondOfDay(), test.secondOfDay().getField());
        TestCase.assertEquals("secondOfDay", test.secondOfDay().getName());
        TestCase.assertEquals("Property[secondOfDay]", test.secondOfDay().toString());
        TestCase.assertSame(test, test.secondOfDay().getDateTime());
        TestCase.assertEquals(48223, test.secondOfDay().get());
        TestCase.assertEquals("48223", test.secondOfDay().getAsString());
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
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().millisOfSecond(), test.millisOfSecond().getField());
        TestCase.assertEquals("millisOfSecond", test.millisOfSecond().getName());
        TestCase.assertEquals("Property[millisOfSecond]", test.millisOfSecond().toString());
        TestCase.assertSame(test, test.millisOfSecond().getDateTime());
        TestCase.assertEquals(53, test.millisOfSecond().get());
        TestCase.assertEquals("53", test.millisOfSecond().getAsString());
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
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        TestCase.assertSame(test.getChronology().millisOfDay(), test.millisOfDay().getField());
        TestCase.assertEquals("millisOfDay", test.millisOfDay().getName());
        TestCase.assertEquals("Property[millisOfDay]", test.millisOfDay().toString());
        TestCase.assertSame(test, test.millisOfDay().getDateTime());
        TestCase.assertEquals(48223053, test.millisOfDay().get());
        TestCase.assertEquals("48223053", test.millisOfDay().getAsString());
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
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.yearOfEra().toInterval();
        TestCase.assertEquals(new DateTime(2004, 1, 1, 0, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new DateTime(2005, 1, 1, 0, 0, 0, 0), testInterval.getEnd());
    }

    public void testPropertyToIntervalYearOfCentury() {
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.yearOfCentury().toInterval();
        TestCase.assertEquals(new DateTime(2004, 1, 1, 0, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new DateTime(2005, 1, 1, 0, 0, 0, 0), testInterval.getEnd());
    }

    public void testPropertyToIntervalYear() {
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.year().toInterval();
        TestCase.assertEquals(new DateTime(2004, 1, 1, 0, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new DateTime(2005, 1, 1, 0, 0, 0, 0), testInterval.getEnd());
    }

    public void testPropertyToIntervalMonthOfYear() {
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.monthOfYear().toInterval();
        TestCase.assertEquals(new DateTime(2004, 6, 1, 0, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new DateTime(2004, 7, 1, 0, 0, 0, 0), testInterval.getEnd());
    }

    public void testPropertyToIntervalDayOfMonth() {
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.dayOfMonth().toInterval();
        TestCase.assertEquals(new DateTime(2004, 6, 9, 0, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new DateTime(2004, 6, 10, 0, 0, 0, 0), testInterval.getEnd());
        DateTime febTest = new DateTime(2004, 2, 29, 13, 23, 43, 53);
        Interval febTestInterval = febTest.dayOfMonth().toInterval();
        TestCase.assertEquals(new DateTime(2004, 2, 29, 0, 0, 0, 0), febTestInterval.getStart());
        TestCase.assertEquals(new DateTime(2004, 3, 1, 0, 0, 0, 0), febTestInterval.getEnd());
    }

    public void testPropertyToIntervalHourOfDay() {
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.hourOfDay().toInterval();
        TestCase.assertEquals(new DateTime(2004, 6, 9, 13, 0, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new DateTime(2004, 6, 9, 14, 0, 0, 0), testInterval.getEnd());
        DateTime midnightTest = new DateTime(2004, 6, 9, 23, 23, 43, 53, TestDateTime_Properties.COPTIC_PARIS);
        Interval midnightTestInterval = midnightTest.hourOfDay().toInterval();
        TestCase.assertEquals(new DateTime(2004, 6, 9, 23, 0, 0, 0, TestDateTime_Properties.COPTIC_PARIS), midnightTestInterval.getStart());
        TestCase.assertEquals(new DateTime(2004, 6, 10, 0, 0, 0, 0, TestDateTime_Properties.COPTIC_PARIS), midnightTestInterval.getEnd());
    }

    public void testPropertyToIntervalMinuteOfHour() {
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.minuteOfHour().toInterval();
        TestCase.assertEquals(new DateTime(2004, 6, 9, 13, 23, 0, 0), testInterval.getStart());
        TestCase.assertEquals(new DateTime(2004, 6, 9, 13, 24, 0, 0), testInterval.getEnd());
    }

    public void testPropertyToIntervalSecondOfMinute() {
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.secondOfMinute().toInterval();
        TestCase.assertEquals(new DateTime(2004, 6, 9, 13, 23, 43, 0), testInterval.getStart());
        TestCase.assertEquals(new DateTime(2004, 6, 9, 13, 23, 44, 0), testInterval.getEnd());
    }

    public void testPropertyToIntervalMillisOfSecond() {
        DateTime test = new DateTime(2004, 6, 9, 13, 23, 43, 53);
        Interval testInterval = test.millisOfSecond().toInterval();
        TestCase.assertEquals(new DateTime(2004, 6, 9, 13, 23, 43, 53), testInterval.getStart());
        TestCase.assertEquals(new DateTime(2004, 6, 9, 13, 23, 43, 54), testInterval.getEnd());
    }

    public void testPropertyEqualsHashCodeLenient() {
        DateTime test1 = new DateTime(1970, 6, 9, 0, 0, 0, 0, LenientChronology.getInstance(TestDateTime_Properties.COPTIC_PARIS));
        DateTime test2 = new DateTime(1970, 6, 9, 0, 0, 0, 0, LenientChronology.getInstance(TestDateTime_Properties.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test1.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test2.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
    }

    public void testPropertyEqualsHashCodeStrict() {
        DateTime test1 = new DateTime(1970, 6, 9, 0, 0, 0, 0, StrictChronology.getInstance(TestDateTime_Properties.COPTIC_PARIS));
        DateTime test2 = new DateTime(1970, 6, 9, 0, 0, 0, 0, StrictChronology.getInstance(TestDateTime_Properties.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test1.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test2.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
    }
}

