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


import java.util.Locale;
import junit.framework.TestCase;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;


/**
 * This class is a Junit unit test for Partial.
 *
 * @author Stephen Colebourne
 */
public class TestPartial_Properties extends TestCase {
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private DateTimeZone zone = null;

    private static final DateTimeFieldType[] TYPES = new DateTimeFieldType[]{ DateTimeFieldType.hourOfDay(), DateTimeFieldType.minuteOfHour(), DateTimeFieldType.secondOfMinute(), DateTimeFieldType.millisOfSecond() };

    private static final int[] VALUES = new int[]{ 10, 20, 30, 40 };

    private static final int[] VALUES1 = new int[]{ 1, 2, 3, 4 };

    private static final int[] VALUES2 = new int[]{ 5, 6, 7, 8 };

    // private long TEST_TIME_NOW =
    // 10L * DateTimeConstants.MILLIS_PER_HOUR
    // + 20L * DateTimeConstants.MILLIS_PER_MINUTE
    // + 30L * DateTimeConstants.MILLIS_PER_SECOND
    // + 40L;
    // 
    private long TEST_TIME1 = (((1L * (MILLIS_PER_HOUR)) + (2L * (MILLIS_PER_MINUTE))) + (3L * (MILLIS_PER_SECOND))) + 4L;

    private long TEST_TIME2 = ((((1L * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;

    public TestPartial_Properties(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetHour() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        TestCase.assertSame(test.getChronology().hourOfDay(), test.property(DateTimeFieldType.hourOfDay()).getField());
        TestCase.assertEquals("hourOfDay", test.property(DateTimeFieldType.hourOfDay()).getName());
        TestCase.assertEquals("Property[hourOfDay]", test.property(DateTimeFieldType.hourOfDay()).toString());
        TestCase.assertSame(test, test.property(DateTimeFieldType.hourOfDay()).getReadablePartial());
        TestCase.assertSame(test, test.property(DateTimeFieldType.hourOfDay()).getPartial());
        TestCase.assertEquals(10, test.property(DateTimeFieldType.hourOfDay()).get());
        TestCase.assertEquals("10", test.property(DateTimeFieldType.hourOfDay()).getAsString());
        TestCase.assertEquals("10", test.property(DateTimeFieldType.hourOfDay()).getAsText());
        TestCase.assertEquals("10", test.property(DateTimeFieldType.hourOfDay()).getAsText(Locale.FRENCH));
        TestCase.assertEquals("10", test.property(DateTimeFieldType.hourOfDay()).getAsShortText());
        TestCase.assertEquals("10", test.property(DateTimeFieldType.hourOfDay()).getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().hours(), test.property(DateTimeFieldType.hourOfDay()).getDurationField());
        TestCase.assertEquals(test.getChronology().days(), test.property(DateTimeFieldType.hourOfDay()).getRangeDurationField());
        TestCase.assertEquals(2, test.property(DateTimeFieldType.hourOfDay()).getMaximumTextLength(null));
        TestCase.assertEquals(2, test.property(DateTimeFieldType.hourOfDay()).getMaximumShortTextLength(null));
    }

    public void testPropertyGetMaxMinValuesHour() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        TestCase.assertEquals(0, test.property(DateTimeFieldType.hourOfDay()).getMinimumValue());
        TestCase.assertEquals(0, test.property(DateTimeFieldType.hourOfDay()).getMinimumValueOverall());
        TestCase.assertEquals(23, test.property(DateTimeFieldType.hourOfDay()).getMaximumValue());
        TestCase.assertEquals(23, test.property(DateTimeFieldType.hourOfDay()).getMaximumValueOverall());
    }

    // public void testPropertyAddHour() {
    // Partial test = new Partial(TYPES, VALUES);
    // Partial copy = test.property(DateTimeFieldType.hourOfDay()).addToCopy(9);
    // check(test, 10, 20, 30, 40);
    // check(copy, 19, 20, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.hourOfDay()).addToCopy(0);
    // check(copy, 10, 20, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.hourOfDay()).addToCopy(13);
    // check(copy, 23, 20, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.hourOfDay()).addToCopy(14);
    // check(copy, 0, 20, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.hourOfDay()).addToCopy(-10);
    // check(copy, 0, 20, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.hourOfDay()).addToCopy(-11);
    // check(copy, 23, 20, 30, 40);
    // }
    // 
    public void testPropertyAddHour() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        Partial copy = test.property(DateTimeFieldType.hourOfDay()).addToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 19, 20, 30, 40);
        copy = test.property(DateTimeFieldType.hourOfDay()).addToCopy(0);
        check(copy, 10, 20, 30, 40);
        copy = test.property(DateTimeFieldType.hourOfDay()).addToCopy(13);
        check(copy, 23, 20, 30, 40);
        try {
            test.property(DateTimeFieldType.hourOfDay()).addToCopy(14);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
        copy = test.property(DateTimeFieldType.hourOfDay()).addToCopy((-10));
        check(copy, 0, 20, 30, 40);
        try {
            test.property(DateTimeFieldType.hourOfDay()).addToCopy((-11));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
    }

    public void testPropertyAddWrapFieldHour() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        Partial copy = test.property(DateTimeFieldType.hourOfDay()).addWrapFieldToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 19, 20, 30, 40);
        copy = test.property(DateTimeFieldType.hourOfDay()).addWrapFieldToCopy(0);
        check(copy, 10, 20, 30, 40);
        copy = test.property(DateTimeFieldType.hourOfDay()).addWrapFieldToCopy(18);
        check(copy, 4, 20, 30, 40);
        copy = test.property(DateTimeFieldType.hourOfDay()).addWrapFieldToCopy((-15));
        check(copy, 19, 20, 30, 40);
    }

    public void testPropertySetHour() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        Partial copy = test.property(DateTimeFieldType.hourOfDay()).setCopy(12);
        check(test, 10, 20, 30, 40);
        check(copy, 12, 20, 30, 40);
        try {
            test.property(DateTimeFieldType.hourOfDay()).setCopy(24);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.property(DateTimeFieldType.hourOfDay()).setCopy((-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextHour() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        Partial copy = test.property(DateTimeFieldType.hourOfDay()).setCopy("12");
        check(test, 10, 20, 30, 40);
        check(copy, 12, 20, 30, 40);
    }

    public void testPropertyWithMaximumValueHour() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        Partial copy = test.property(DateTimeFieldType.hourOfDay()).withMaximumValue();
        check(test, 10, 20, 30, 40);
        check(copy, 23, 20, 30, 40);
    }

    public void testPropertyWithMinimumValueHour() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        Partial copy = test.property(DateTimeFieldType.hourOfDay()).withMinimumValue();
        check(test, 10, 20, 30, 40);
        check(copy, 0, 20, 30, 40);
    }

    public void testPropertyCompareToHour() {
        Partial test1 = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES1);
        Partial test2 = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES2);
        TestCase.assertEquals(true, ((test1.property(DateTimeFieldType.hourOfDay()).compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.property(DateTimeFieldType.hourOfDay()).compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.property(DateTimeFieldType.hourOfDay()).compareTo(test1)) == 0));
        try {
            test1.property(DateTimeFieldType.hourOfDay()).compareTo(((ReadablePartial) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.property(DateTimeFieldType.hourOfDay()).compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.property(DateTimeFieldType.hourOfDay()).compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.property(DateTimeFieldType.hourOfDay()).compareTo(dt1)) == 0));
        try {
            test1.property(DateTimeFieldType.hourOfDay()).compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMinute() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        TestCase.assertSame(test.getChronology().minuteOfHour(), test.property(DateTimeFieldType.minuteOfHour()).getField());
        TestCase.assertEquals("minuteOfHour", test.property(DateTimeFieldType.minuteOfHour()).getName());
        TestCase.assertEquals("Property[minuteOfHour]", test.property(DateTimeFieldType.minuteOfHour()).toString());
        TestCase.assertSame(test, test.property(DateTimeFieldType.minuteOfHour()).getReadablePartial());
        TestCase.assertSame(test, test.property(DateTimeFieldType.minuteOfHour()).getPartial());
        TestCase.assertEquals(20, test.property(DateTimeFieldType.minuteOfHour()).get());
        TestCase.assertEquals("20", test.property(DateTimeFieldType.minuteOfHour()).getAsString());
        TestCase.assertEquals("20", test.property(DateTimeFieldType.minuteOfHour()).getAsText());
        TestCase.assertEquals("20", test.property(DateTimeFieldType.minuteOfHour()).getAsText(Locale.FRENCH));
        TestCase.assertEquals("20", test.property(DateTimeFieldType.minuteOfHour()).getAsShortText());
        TestCase.assertEquals("20", test.property(DateTimeFieldType.minuteOfHour()).getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().minutes(), test.property(DateTimeFieldType.minuteOfHour()).getDurationField());
        TestCase.assertEquals(test.getChronology().hours(), test.property(DateTimeFieldType.minuteOfHour()).getRangeDurationField());
        TestCase.assertEquals(2, test.property(DateTimeFieldType.minuteOfHour()).getMaximumTextLength(null));
        TestCase.assertEquals(2, test.property(DateTimeFieldType.minuteOfHour()).getMaximumShortTextLength(null));
    }

    public void testPropertyGetMaxMinValuesMinute() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        TestCase.assertEquals(0, test.property(DateTimeFieldType.minuteOfHour()).getMinimumValue());
        TestCase.assertEquals(0, test.property(DateTimeFieldType.minuteOfHour()).getMinimumValueOverall());
        TestCase.assertEquals(59, test.property(DateTimeFieldType.minuteOfHour()).getMaximumValue());
        TestCase.assertEquals(59, test.property(DateTimeFieldType.minuteOfHour()).getMaximumValueOverall());
    }

    // public void testPropertyAddMinute() {
    // Partial test = new Partial(TYPES, VALUES);
    // Partial copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(9);
    // check(test, 10, 20, 30, 40);
    // check(copy, 10, 29, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(39);
    // check(copy, 10, 59, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(40);
    // check(copy, 11, 0, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(1 * 60 + 45);
    // check(copy, 12, 5, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(13 * 60 + 39);
    // check(copy, 23, 59, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(13 * 60 + 40);
    // check(copy, 0, 0, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(-9);
    // check(copy, 10, 11, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(-19);
    // check(copy, 10, 1, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(-20);
    // check(copy, 10, 0, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(-21);
    // check(copy, 9, 59, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(-(10 * 60 + 20));
    // check(copy, 0, 0, 30, 40);
    // 
    // copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(-(10 * 60 + 21));
    // check(copy, 23, 59, 30, 40);
    // }
    public void testPropertyAddMinute() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        Partial copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 29, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(39);
        check(copy, 10, 59, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(40);
        check(copy, 11, 0, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(((1 * 60) + 45));
        check(copy, 12, 5, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy(((13 * 60) + 39));
        check(copy, 23, 59, 30, 40);
        try {
            test.property(DateTimeFieldType.minuteOfHour()).addToCopy(((13 * 60) + 40));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy((-9));
        check(copy, 10, 11, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy((-19));
        check(copy, 10, 1, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy((-20));
        check(copy, 10, 0, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy((-21));
        check(copy, 9, 59, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addToCopy((-((10 * 60) + 20)));
        check(copy, 0, 0, 30, 40);
        try {
            test.property(DateTimeFieldType.minuteOfHour()).addToCopy((-((10 * 60) + 21)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20, 30, 40);
    }

    public void testPropertyAddWrapFieldMinute() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        Partial copy = test.property(DateTimeFieldType.minuteOfHour()).addWrapFieldToCopy(9);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 29, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addWrapFieldToCopy(49);
        check(copy, 10, 9, 30, 40);
        copy = test.property(DateTimeFieldType.minuteOfHour()).addWrapFieldToCopy((-47));
        check(copy, 10, 33, 30, 40);
    }

    public void testPropertySetMinute() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        Partial copy = test.property(DateTimeFieldType.minuteOfHour()).setCopy(12);
        check(test, 10, 20, 30, 40);
        check(copy, 10, 12, 30, 40);
        try {
            test.property(DateTimeFieldType.minuteOfHour()).setCopy(60);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.property(DateTimeFieldType.minuteOfHour()).setCopy((-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testPropertySetTextMinute() {
        Partial test = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES);
        Partial copy = test.property(DateTimeFieldType.minuteOfHour()).setCopy("12");
        check(test, 10, 20, 30, 40);
        check(copy, 10, 12, 30, 40);
    }

    public void testPropertyCompareToMinute() {
        Partial test1 = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES1);
        Partial test2 = new Partial(TestPartial_Properties.TYPES, TestPartial_Properties.VALUES2);
        TestCase.assertEquals(true, ((test1.property(DateTimeFieldType.minuteOfHour()).compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.property(DateTimeFieldType.minuteOfHour()).compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.property(DateTimeFieldType.minuteOfHour()).compareTo(test1)) == 0));
        try {
            test1.property(DateTimeFieldType.minuteOfHour()).compareTo(((ReadablePartial) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.property(DateTimeFieldType.minuteOfHour()).compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.property(DateTimeFieldType.minuteOfHour()).compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.property(DateTimeFieldType.minuteOfHour()).compareTo(dt1)) == 0));
        try {
            test1.property(DateTimeFieldType.minuteOfHour()).compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }
}

