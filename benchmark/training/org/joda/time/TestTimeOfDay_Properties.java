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
import static DateTimeConstants.MILLIS_PER_SECOND;


/**
 * This class is a Junit unit test for TimeOfDay.
 *
 * @author Stephen Colebourne
 */
@SuppressWarnings("deprecation")
public class TestTimeOfDay_Properties extends TestCase {
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private long TEST_TIME_NOW = (((10L * (MILLIS_PER_HOUR)) + (20L * (MILLIS_PER_MINUTE))) + (30L * (MILLIS_PER_SECOND))) + 40L;

    private long TEST_TIME1 = (((1L * (MILLIS_PER_HOUR)) + (2L * (MILLIS_PER_MINUTE))) + (3L * (MILLIS_PER_SECOND))) + 4L;

    private long TEST_TIME2 = ((((1L * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;

    private DateTimeZone zone = null;

    public TestTimeOfDay_Properties(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetHour() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertSame(test.getChronology().hourOfDay(), test.hourOfDay().getField());
        TestCase.assertEquals("hourOfDay", test.hourOfDay().getName());
        TestCase.assertEquals("Property[hourOfDay]", test.hourOfDay().toString());
        TestCase.assertSame(test, test.hourOfDay().getReadablePartial());
        TestCase.assertSame(test, test.hourOfDay().getTimeOfDay());
        TestCase.assertEquals(10, test.hourOfDay().get());
        TestCase.assertEquals("10", test.hourOfDay().getAsString());
        TestCase.assertEquals("10", test.hourOfDay().getAsText());
        TestCase.assertEquals("10", test.hourOfDay().getAsText(Locale.FRENCH));
        TestCase.assertEquals("10", test.hourOfDay().getAsShortText());
        TestCase.assertEquals("10", test.hourOfDay().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().hours(), test.hourOfDay().getDurationField());
        TestCase.assertEquals(test.getChronology().days(), test.hourOfDay().getRangeDurationField());
        TestCase.assertEquals(2, test.hourOfDay().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.hourOfDay().getMaximumShortTextLength(null));
    }

    public void testPropertyGetMaxMinValuesHour() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals(0, test.hourOfDay().getMinimumValue());
        TestCase.assertEquals(0, test.hourOfDay().getMinimumValueOverall());
        TestCase.assertEquals(23, test.hourOfDay().getMaximumValue());
        TestCase.assertEquals(23, test.hourOfDay().getMaximumValueOverall());
    }

    public void testPropertyAddHour() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.hourOfDay().addToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 19, 20, 30, 40);
        copy = test.hourOfDay().addToCopy(0);
        check(copy, 10, 20, 30, 40);
        copy = test.hourOfDay().addToCopy(13);
        check(copy, 23, 20, 30, 40);
        copy = test.hourOfDay().addToCopy(14);
        check(copy, 0, 20, 30, 40);
        copy = test.hourOfDay().addToCopy((-10));
        check(copy, 0, 20, 30, 40);
        copy = test.hourOfDay().addToCopy((-11));
        check(copy, 23, 20, 30, 40);
    }

    public void testPropertyAddNoWrapHour() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.hourOfDay().addNoWrapToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 19, 20, 30, 40);
        copy = test.hourOfDay().addNoWrapToCopy(0);
        check(copy, 10, 20, 30, 40);
        copy = test.hourOfDay().addNoWrapToCopy(13);
        check(copy, 23, 20, 30, 40);
        try {
            test.hourOfDay().addNoWrapToCopy(14);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
        copy = test.hourOfDay().addNoWrapToCopy((-10));
        check(copy, 0, 20, 30, 40);
        try {
            test.hourOfDay().addNoWrapToCopy((-11));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
    }

    public void testPropertyAddWrapFieldHour() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.hourOfDay().addWrapFieldToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 19, 20, 30, 40);
        copy = test.hourOfDay().addWrapFieldToCopy(0);
        check(copy, 10, 20, 30, 40);
        copy = test.hourOfDay().addWrapFieldToCopy(18);
        check(copy, 4, 20, 30, 40);
        copy = test.hourOfDay().addWrapFieldToCopy((-15));
        check(copy, 19, 20, 30, 40);
    }

    public void testPropertySetHour() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.hourOfDay().setCopy(12);
        check(test, 10, 20, 30, 40);
        check(copy, 12, 20, 30, 40);
        try {
            test.hourOfDay().setCopy(24);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.hourOfDay().setCopy((-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextHour() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.hourOfDay().setCopy("12");
        check(test, 10, 20, 30, 40);
        check(copy, 12, 20, 30, 40);
    }

    public void testPropertyWithMaximumValueHour() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.hourOfDay().withMaximumValue();
        check(test, 10, 20, 30, 40);
        check(copy, 23, 20, 30, 40);
    }

    public void testPropertyWithMinimumValueHour() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.hourOfDay().withMinimumValue();
        check(test, 10, 20, 30, 40);
        check(copy, 0, 20, 30, 40);
    }

    public void testPropertyCompareToHour() {
        TimeOfDay test1 = new TimeOfDay(TEST_TIME1);
        TimeOfDay test2 = new TimeOfDay(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.hourOfDay().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.hourOfDay().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.hourOfDay().compareTo(test1)) == 0));
        try {
            test1.hourOfDay().compareTo(((ReadablePartial) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.hourOfDay().compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.hourOfDay().compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.hourOfDay().compareTo(dt1)) == 0));
        try {
            test1.hourOfDay().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMinute() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertSame(test.getChronology().minuteOfHour(), test.minuteOfHour().getField());
        TestCase.assertEquals("minuteOfHour", test.minuteOfHour().getName());
        TestCase.assertEquals("Property[minuteOfHour]", test.minuteOfHour().toString());
        TestCase.assertSame(test, test.minuteOfHour().getReadablePartial());
        TestCase.assertSame(test, test.minuteOfHour().getTimeOfDay());
        TestCase.assertEquals(20, test.minuteOfHour().get());
        TestCase.assertEquals("20", test.minuteOfHour().getAsString());
        TestCase.assertEquals("20", test.minuteOfHour().getAsText());
        TestCase.assertEquals("20", test.minuteOfHour().getAsText(Locale.FRENCH));
        TestCase.assertEquals("20", test.minuteOfHour().getAsShortText());
        TestCase.assertEquals("20", test.minuteOfHour().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().minutes(), test.minuteOfHour().getDurationField());
        TestCase.assertEquals(test.getChronology().hours(), test.minuteOfHour().getRangeDurationField());
        TestCase.assertEquals(2, test.minuteOfHour().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.minuteOfHour().getMaximumShortTextLength(null));
    }

    public void testPropertyGetMaxMinValuesMinute() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals(0, test.minuteOfHour().getMinimumValue());
        TestCase.assertEquals(0, test.minuteOfHour().getMinimumValueOverall());
        TestCase.assertEquals(59, test.minuteOfHour().getMaximumValue());
        TestCase.assertEquals(59, test.minuteOfHour().getMaximumValueOverall());
    }

    public void testPropertyAddMinute() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.minuteOfHour().addToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 29, 30, 40);
        copy = test.minuteOfHour().addToCopy(39);
        check(copy, 10, 59, 30, 40);
        copy = test.minuteOfHour().addToCopy(40);
        check(copy, 11, 0, 30, 40);
        copy = test.minuteOfHour().addToCopy(((1 * 60) + 45));
        check(copy, 12, 5, 30, 40);
        copy = test.minuteOfHour().addToCopy(((13 * 60) + 39));
        check(copy, 23, 59, 30, 40);
        copy = test.minuteOfHour().addToCopy(((13 * 60) + 40));
        check(copy, 0, 0, 30, 40);
        copy = test.minuteOfHour().addToCopy((-9));
        check(copy, 10, 11, 30, 40);
        copy = test.minuteOfHour().addToCopy((-19));
        check(copy, 10, 1, 30, 40);
        copy = test.minuteOfHour().addToCopy((-20));
        check(copy, 10, 0, 30, 40);
        copy = test.minuteOfHour().addToCopy((-21));
        check(copy, 9, 59, 30, 40);
        copy = test.minuteOfHour().addToCopy((-((10 * 60) + 20)));
        check(copy, 0, 0, 30, 40);
        copy = test.minuteOfHour().addToCopy((-((10 * 60) + 21)));
        check(copy, 23, 59, 30, 40);
    }

    public void testPropertyAddNoWrapMinute() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.minuteOfHour().addNoWrapToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 29, 30, 40);
        copy = test.minuteOfHour().addNoWrapToCopy(39);
        check(copy, 10, 59, 30, 40);
        copy = test.minuteOfHour().addNoWrapToCopy(40);
        check(copy, 11, 0, 30, 40);
        copy = test.minuteOfHour().addNoWrapToCopy(((1 * 60) + 45));
        check(copy, 12, 5, 30, 40);
        copy = test.minuteOfHour().addNoWrapToCopy(((13 * 60) + 39));
        check(copy, 23, 59, 30, 40);
        try {
            test.minuteOfHour().addNoWrapToCopy(((13 * 60) + 40));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
        copy = test.minuteOfHour().addNoWrapToCopy((-9));
        check(copy, 10, 11, 30, 40);
        copy = test.minuteOfHour().addNoWrapToCopy((-19));
        check(copy, 10, 1, 30, 40);
        copy = test.minuteOfHour().addNoWrapToCopy((-20));
        check(copy, 10, 0, 30, 40);
        copy = test.minuteOfHour().addNoWrapToCopy((-21));
        check(copy, 9, 59, 30, 40);
        copy = test.minuteOfHour().addNoWrapToCopy((-((10 * 60) + 20)));
        check(copy, 0, 0, 30, 40);
        try {
            test.minuteOfHour().addNoWrapToCopy((-((10 * 60) + 21)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
    }

    public void testPropertyAddWrapFieldMinute() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.minuteOfHour().addWrapFieldToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 29, 30, 40);
        copy = test.minuteOfHour().addWrapFieldToCopy(49);
        check(copy, 10, 9, 30, 40);
        copy = test.minuteOfHour().addWrapFieldToCopy((-47));
        check(copy, 10, 33, 30, 40);
    }

    public void testPropertySetMinute() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.minuteOfHour().setCopy(12);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 12, 30, 40);
        try {
            test.minuteOfHour().setCopy(60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.minuteOfHour().setCopy((-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextMinute() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.minuteOfHour().setCopy("12");
        check(test, 10, 20, 30, 40);
        check(copy, 10, 12, 30, 40);
    }

    public void testPropertyCompareToMinute() {
        TimeOfDay test1 = new TimeOfDay(TEST_TIME1);
        TimeOfDay test2 = new TimeOfDay(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.minuteOfHour().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.minuteOfHour().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.minuteOfHour().compareTo(test1)) == 0));
        try {
            test1.minuteOfHour().compareTo(((ReadablePartial) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.minuteOfHour().compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.minuteOfHour().compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.minuteOfHour().compareTo(dt1)) == 0));
        try {
            test1.minuteOfHour().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetSecond() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertSame(test.getChronology().secondOfMinute(), test.secondOfMinute().getField());
        TestCase.assertEquals("secondOfMinute", test.secondOfMinute().getName());
        TestCase.assertEquals("Property[secondOfMinute]", test.secondOfMinute().toString());
        TestCase.assertSame(test, test.secondOfMinute().getReadablePartial());
        TestCase.assertSame(test, test.secondOfMinute().getTimeOfDay());
        TestCase.assertEquals(30, test.secondOfMinute().get());
        TestCase.assertEquals("30", test.secondOfMinute().getAsString());
        TestCase.assertEquals("30", test.secondOfMinute().getAsText());
        TestCase.assertEquals("30", test.secondOfMinute().getAsText(Locale.FRENCH));
        TestCase.assertEquals("30", test.secondOfMinute().getAsShortText());
        TestCase.assertEquals("30", test.secondOfMinute().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().seconds(), test.secondOfMinute().getDurationField());
        TestCase.assertEquals(test.getChronology().minutes(), test.secondOfMinute().getRangeDurationField());
        TestCase.assertEquals(2, test.secondOfMinute().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.secondOfMinute().getMaximumShortTextLength(null));
    }

    public void testPropertyGetMaxMinValuesSecond() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals(0, test.secondOfMinute().getMinimumValue());
        TestCase.assertEquals(0, test.secondOfMinute().getMinimumValueOverall());
        TestCase.assertEquals(59, test.secondOfMinute().getMaximumValue());
        TestCase.assertEquals(59, test.secondOfMinute().getMaximumValueOverall());
    }

    public void testPropertyAddSecond() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.secondOfMinute().addToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 20, 39, 40);
        copy = test.secondOfMinute().addToCopy(29);
        check(copy, 10, 20, 59, 40);
        copy = test.secondOfMinute().addToCopy(30);
        check(copy, 10, 21, 0, 40);
        copy = test.secondOfMinute().addToCopy(((39 * 60) + 29));
        check(copy, 10, 59, 59, 40);
        copy = test.secondOfMinute().addToCopy(((39 * 60) + 30));
        check(copy, 11, 0, 0, 40);
        copy = test.secondOfMinute().addToCopy(((((13 * 60) * 60) + (39 * 60)) + 30));
        check(copy, 0, 0, 0, 40);
        copy = test.secondOfMinute().addToCopy((-9));
        check(copy, 10, 20, 21, 40);
        copy = test.secondOfMinute().addToCopy((-30));
        check(copy, 10, 20, 0, 40);
        copy = test.secondOfMinute().addToCopy((-31));
        check(copy, 10, 19, 59, 40);
        copy = test.secondOfMinute().addToCopy((-((((10 * 60) * 60) + (20 * 60)) + 30)));
        check(copy, 0, 0, 0, 40);
        copy = test.secondOfMinute().addToCopy((-((((10 * 60) * 60) + (20 * 60)) + 31)));
        check(copy, 23, 59, 59, 40);
    }

    public void testPropertyAddNoWrapSecond() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.secondOfMinute().addNoWrapToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 20, 39, 40);
        copy = test.secondOfMinute().addNoWrapToCopy(29);
        check(copy, 10, 20, 59, 40);
        copy = test.secondOfMinute().addNoWrapToCopy(30);
        check(copy, 10, 21, 0, 40);
        copy = test.secondOfMinute().addNoWrapToCopy(((39 * 60) + 29));
        check(copy, 10, 59, 59, 40);
        copy = test.secondOfMinute().addNoWrapToCopy(((39 * 60) + 30));
        check(copy, 11, 0, 0, 40);
        try {
            test.secondOfMinute().addNoWrapToCopy(((((13 * 60) * 60) + (39 * 60)) + 30));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
        copy = test.secondOfMinute().addNoWrapToCopy((-9));
        check(copy, 10, 20, 21, 40);
        copy = test.secondOfMinute().addNoWrapToCopy((-30));
        check(copy, 10, 20, 0, 40);
        copy = test.secondOfMinute().addNoWrapToCopy((-31));
        check(copy, 10, 19, 59, 40);
        copy = test.secondOfMinute().addNoWrapToCopy((-((((10 * 60) * 60) + (20 * 60)) + 30)));
        check(copy, 0, 0, 0, 40);
        try {
            test.secondOfMinute().addNoWrapToCopy((-((((10 * 60) * 60) + (20 * 60)) + 31)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
    }

    public void testPropertyAddWrapFieldSecond() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.secondOfMinute().addWrapFieldToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 20, 39, 40);
        copy = test.secondOfMinute().addWrapFieldToCopy(49);
        check(copy, 10, 20, 19, 40);
        copy = test.secondOfMinute().addWrapFieldToCopy((-47));
        check(copy, 10, 20, 43, 40);
    }

    public void testPropertySetSecond() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.secondOfMinute().setCopy(12);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 20, 12, 40);
        try {
            test.secondOfMinute().setCopy(60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.secondOfMinute().setCopy((-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextSecond() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.secondOfMinute().setCopy("12");
        check(test, 10, 20, 30, 40);
        check(copy, 10, 20, 12, 40);
    }

    public void testPropertyCompareToSecond() {
        TimeOfDay test1 = new TimeOfDay(TEST_TIME1);
        TimeOfDay test2 = new TimeOfDay(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.secondOfMinute().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.secondOfMinute().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.secondOfMinute().compareTo(test1)) == 0));
        try {
            test1.secondOfMinute().compareTo(((ReadablePartial) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.secondOfMinute().compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.secondOfMinute().compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.secondOfMinute().compareTo(dt1)) == 0));
        try {
            test1.secondOfMinute().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMilli() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertSame(test.getChronology().millisOfSecond(), test.millisOfSecond().getField());
        TestCase.assertEquals("millisOfSecond", test.millisOfSecond().getName());
        TestCase.assertEquals("Property[millisOfSecond]", test.millisOfSecond().toString());
        TestCase.assertSame(test, test.millisOfSecond().getReadablePartial());
        TestCase.assertSame(test, test.millisOfSecond().getTimeOfDay());
        TestCase.assertEquals(40, test.millisOfSecond().get());
        TestCase.assertEquals("40", test.millisOfSecond().getAsString());
        TestCase.assertEquals("40", test.millisOfSecond().getAsText());
        TestCase.assertEquals("40", test.millisOfSecond().getAsText(Locale.FRENCH));
        TestCase.assertEquals("40", test.millisOfSecond().getAsShortText());
        TestCase.assertEquals("40", test.millisOfSecond().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().millis(), test.millisOfSecond().getDurationField());
        TestCase.assertEquals(test.getChronology().seconds(), test.millisOfSecond().getRangeDurationField());
        TestCase.assertEquals(3, test.millisOfSecond().getMaximumTextLength(null));
        TestCase.assertEquals(3, test.millisOfSecond().getMaximumShortTextLength(null));
    }

    public void testPropertyGetMaxMinValuesMilli() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TestCase.assertEquals(0, test.millisOfSecond().getMinimumValue());
        TestCase.assertEquals(0, test.millisOfSecond().getMinimumValueOverall());
        TestCase.assertEquals(999, test.millisOfSecond().getMaximumValue());
        TestCase.assertEquals(999, test.millisOfSecond().getMaximumValueOverall());
    }

    public void testPropertyAddMilli() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.millisOfSecond().addToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 20, 30, 49);
        copy = test.millisOfSecond().addToCopy(959);
        check(copy, 10, 20, 30, 999);
        copy = test.millisOfSecond().addToCopy(960);
        check(copy, 10, 20, 31, 0);
        copy = test.millisOfSecond().addToCopy(((((((13 * 60) * 60) * 1000) + ((39 * 60) * 1000)) + (29 * 1000)) + 959));
        check(copy, 23, 59, 59, 999);
        copy = test.millisOfSecond().addToCopy(((((((13 * 60) * 60) * 1000) + ((39 * 60) * 1000)) + (29 * 1000)) + 960));
        check(copy, 0, 0, 0, 0);
        copy = test.millisOfSecond().addToCopy((-9));
        check(copy, 10, 20, 30, 31);
        copy = test.millisOfSecond().addToCopy((-40));
        check(copy, 10, 20, 30, 0);
        copy = test.millisOfSecond().addToCopy((-41));
        check(copy, 10, 20, 29, 999);
        copy = test.millisOfSecond().addToCopy((-((((((10 * 60) * 60) * 1000) + ((20 * 60) * 1000)) + (30 * 1000)) + 40)));
        check(copy, 0, 0, 0, 0);
        copy = test.millisOfSecond().addToCopy((-((((((10 * 60) * 60) * 1000) + ((20 * 60) * 1000)) + (30 * 1000)) + 41)));
        check(copy, 23, 59, 59, 999);
    }

    public void testPropertyAddNoWrapMilli() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.millisOfSecond().addNoWrapToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 20, 30, 49);
        copy = test.millisOfSecond().addNoWrapToCopy(959);
        check(copy, 10, 20, 30, 999);
        copy = test.millisOfSecond().addNoWrapToCopy(960);
        check(copy, 10, 20, 31, 0);
        copy = test.millisOfSecond().addNoWrapToCopy(((((((13 * 60) * 60) * 1000) + ((39 * 60) * 1000)) + (29 * 1000)) + 959));
        check(copy, 23, 59, 59, 999);
        try {
            test.millisOfSecond().addNoWrapToCopy(((((((13 * 60) * 60) * 1000) + ((39 * 60) * 1000)) + (29 * 1000)) + 960));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
        copy = test.millisOfSecond().addNoWrapToCopy((-9));
        check(copy, 10, 20, 30, 31);
        copy = test.millisOfSecond().addNoWrapToCopy((-40));
        check(copy, 10, 20, 30, 0);
        copy = test.millisOfSecond().addNoWrapToCopy((-41));
        check(copy, 10, 20, 29, 999);
        copy = test.millisOfSecond().addNoWrapToCopy((-((((((10 * 60) * 60) * 1000) + ((20 * 60) * 1000)) + (30 * 1000)) + 40)));
        check(copy, 0, 0, 0, 0);
        try {
            test.millisOfSecond().addNoWrapToCopy((-((((((10 * 60) * 60) * 1000) + ((20 * 60) * 1000)) + (30 * 1000)) + 41)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
    }

    public void testPropertyAddWrapFieldMilli() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.millisOfSecond().addWrapFieldToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 20, 30, 49);
        copy = test.millisOfSecond().addWrapFieldToCopy(995);
        check(copy, 10, 20, 30, 35);
        copy = test.millisOfSecond().addWrapFieldToCopy((-47));
        check(copy, 10, 20, 30, 993);
    }

    public void testPropertySetMilli() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.millisOfSecond().setCopy(12);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 20, 30, 12);
        try {
            test.millisOfSecond().setCopy(1000);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.millisOfSecond().setCopy((-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextMilli() {
        TimeOfDay test = new TimeOfDay(10, 20, 30, 40);
        TimeOfDay copy = test.millisOfSecond().setCopy("12");
        check(test, 10, 20, 30, 40);
        check(copy, 10, 20, 30, 12);
    }

    public void testPropertyCompareToMilli() {
        TimeOfDay test1 = new TimeOfDay(TEST_TIME1);
        TimeOfDay test2 = new TimeOfDay(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.millisOfSecond().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.millisOfSecond().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.millisOfSecond().compareTo(test1)) == 0));
        try {
            test1.millisOfSecond().compareTo(((ReadablePartial) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.millisOfSecond().compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.millisOfSecond().compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.millisOfSecond().compareTo(dt1)) == 0));
        try {
            test1.millisOfSecond().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }
}

