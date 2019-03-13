/**
 * Copyright 2001-2011 Stephen Colebourne
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
 * This class is a Junit unit test for MonthDay. Based on {@link TestYearMonth_Propeties}
 */
public class TestMonthDay_Properties extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestMonthDay_Properties.PARIS);

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private long TEST_TIME1 = ((((((31L + 28L) + 31L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2 = ((((((((365L + 31L) + 28L) + 31L) + 30L) + 7L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone zone = null;

    private Locale locale = null;

    public TestMonthDay_Properties(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMonthOfYear() {
        MonthDay test = new MonthDay(9, 6);
        TestCase.assertSame(test.getChronology().monthOfYear(), test.monthOfYear().getField());
        TestCase.assertEquals("monthOfYear", test.monthOfYear().getName());
        TestCase.assertEquals("Property[monthOfYear]", test.monthOfYear().toString());
        TestCase.assertSame(test, test.monthOfYear().getReadablePartial());
        TestCase.assertSame(test, test.monthOfYear().getMonthDay());
        TestCase.assertEquals(9, test.monthOfYear().get());
        TestCase.assertEquals("9", test.monthOfYear().getAsString());
        TestCase.assertEquals("September", test.monthOfYear().getAsText());
        TestCase.assertEquals("septembre", test.monthOfYear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("Sep", test.monthOfYear().getAsShortText());
        TestCase.assertEquals("sept.", test.monthOfYear().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().months(), test.monthOfYear().getDurationField());
        // assertEquals(test.getChronology().days(), test.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(9, test.monthOfYear().getMaximumTextLength(null));
        TestCase.assertEquals(3, test.monthOfYear().getMaximumShortTextLength(null));
    }

    public void testPropertyGetMaxMinValuesMonthOfYear() {
        MonthDay test = new MonthDay(10, 6);
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValue());
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValueOverall());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValue());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValueOverall());
    }

    public void testPropertyAddMonthOfYear() {
        MonthDay test = new MonthDay(3, 6);
        MonthDay copy = test.monthOfYear().addToCopy(9);
        check(test, 3, 6);
        check(copy, 12, 6);
        copy = test.monthOfYear().addToCopy(0);
        check(copy, 3, 6);
        check(test, 3, 6);
        copy = test.monthOfYear().addToCopy((-3));
        check(copy, 12, 6);
        check(test, 3, 6);
    }

    public void testPropertyAddWrapFieldMonthOfYear() {
        MonthDay test = new MonthDay(5, 6);
        MonthDay copy = test.monthOfYear().addWrapFieldToCopy(2);
        check(test, 5, 6);
        check(copy, 7, 6);
        copy = test.monthOfYear().addWrapFieldToCopy(2);
        check(copy, 7, 6);
        copy = test.monthOfYear().addWrapFieldToCopy(((292278993 - 4) + 1));
        check(copy, 11, 6);
        copy = test.monthOfYear().addWrapFieldToCopy((((-292275054) - 4) - 1));
        check(copy, 6, 6);
    }

    public void testPropertySetMonthOfYear() {
        MonthDay test = new MonthDay(10, 6);
        MonthDay copy = test.monthOfYear().setCopy(12);
        check(test, 10, 6);
        check(copy, 12, 6);
    }

    public void testPropertySetTextMonthOfYear() {
        MonthDay test = new MonthDay(10, 6);
        MonthDay copy = test.monthOfYear().setCopy("12");
        check(test, 10, 6);
        check(copy, 12, 6);
    }

    public void testPropertyCompareToMonthOfYear() {
        MonthDay test1 = new MonthDay(TEST_TIME1);
        MonthDay test2 = new MonthDay(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.monthOfYear().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.monthOfYear().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.monthOfYear().compareTo(test1)) == 0));
        try {
            test1.monthOfYear().compareTo(((ReadablePartial) (null)));
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
        MonthDay test = new MonthDay(4, 6);
        TestCase.assertSame(test.getChronology().dayOfMonth(), test.dayOfMonth().getField());
        TestCase.assertEquals("dayOfMonth", test.dayOfMonth().getName());
        TestCase.assertEquals("Property[dayOfMonth]", test.dayOfMonth().toString());
        TestCase.assertSame(test, test.dayOfMonth().getReadablePartial());
        TestCase.assertSame(test, test.dayOfMonth().getMonthDay());
        TestCase.assertEquals(6, test.dayOfMonth().get());
        TestCase.assertEquals("6", test.dayOfMonth().getAsString());
        TestCase.assertEquals("6", test.dayOfMonth().getAsText());
        TestCase.assertEquals("6", test.dayOfMonth().getAsText(Locale.FRENCH));
        TestCase.assertEquals("6", test.dayOfMonth().getAsShortText());
        TestCase.assertEquals("6", test.dayOfMonth().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().days(), test.dayOfMonth().getDurationField());
        TestCase.assertEquals(test.getChronology().months(), test.dayOfMonth().getRangeDurationField());
        TestCase.assertEquals(2, test.dayOfMonth().getMaximumTextLength(null));
        TestCase.assertEquals(2, test.dayOfMonth().getMaximumShortTextLength(null));
        test = new MonthDay(4, 7);
        TestCase.assertEquals("7", test.dayOfMonth().getAsText(Locale.FRENCH));
        TestCase.assertEquals("7", test.dayOfMonth().getAsShortText(Locale.FRENCH));
    }

    public void testPropertyGetMaxMinValuesDayOfMonth() {
        MonthDay test = new MonthDay(4, 6);
        TestCase.assertEquals(1, test.dayOfMonth().getMinimumValue());
        TestCase.assertEquals(1, test.dayOfMonth().getMinimumValueOverall());
        TestCase.assertEquals(30, test.dayOfMonth().getMaximumValue());
        TestCase.assertEquals(31, test.dayOfMonth().getMaximumValueOverall());
    }

    public void testPropertyAddDayOfMonth() {
        MonthDay test = new MonthDay(4, 6);
        MonthDay copy = test.dayOfMonth().addToCopy(6);
        check(test, 4, 6);
        check(copy, 4, 12);
        copy = test.dayOfMonth().addToCopy(7);
        check(copy, 4, 13);
        copy = test.dayOfMonth().addToCopy((-5));
        check(copy, 4, 1);
        copy = test.dayOfMonth().addToCopy((-6));
        check(copy, 3, 31);
    }

    public void testPropertyAddWrapFieldDayOfMonth() {
        MonthDay test = new MonthDay(4, 6);
        MonthDay copy = test.dayOfMonth().addWrapFieldToCopy(4);
        check(test, 4, 6);
        check(copy, 4, 10);
        copy = test.dayOfMonth().addWrapFieldToCopy(8);
        check(copy, 4, 14);
        copy = test.dayOfMonth().addWrapFieldToCopy((-8));
        check(copy, 4, 28);
    }

    public void testPropertySetDayOfMonth() {
        MonthDay test = new MonthDay(4, 6);
        MonthDay copy = test.dayOfMonth().setCopy(12);
        check(test, 4, 6);
        check(copy, 4, 12);
        try {
            test.dayOfMonth().setCopy(33);
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
        MonthDay test = new MonthDay(4, 6);
        MonthDay copy = test.dayOfMonth().setCopy("12");
        check(test, 4, 6);
        check(copy, 4, 12);
        copy = test.dayOfMonth().setCopy("2");
        check(test, 4, 6);
        check(copy, 4, 2);
        copy = test.dayOfMonth().setCopy("4");
        check(test, 4, 6);
        check(copy, 4, 4);
    }

    public void testPropertyCompareToDayOfMonth() {
        MonthDay test1 = new MonthDay(TEST_TIME1);
        MonthDay test2 = new MonthDay(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.dayOfMonth().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.dayOfMonth().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.dayOfMonth().compareTo(test1)) == 0));
        try {
            test1.dayOfMonth().compareTo(((ReadablePartial) (null)));
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
    public void testPropertyEquals() {
        MonthDay test1 = new MonthDay(11, 11);
        MonthDay test2 = new MonthDay(11, 12);
        MonthDay test3 = new MonthDay(11, 11, CopticChronology.getInstanceUTC());
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(false, test1.dayOfMonth().equals(test1.monthOfYear()));
        TestCase.assertEquals(false, test1.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(false, test1.dayOfMonth().equals(test2.monthOfYear()));
        TestCase.assertEquals(false, test1.monthOfYear().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test1.monthOfYear().equals(test1.monthOfYear()));
        TestCase.assertEquals(false, test1.monthOfYear().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, test1.monthOfYear().equals(test2.monthOfYear()));
        TestCase.assertEquals(false, test1.dayOfMonth().equals(null));
        TestCase.assertEquals(false, test1.dayOfMonth().equals("any"));
        // chrono
        TestCase.assertEquals(false, test1.dayOfMonth().equals(test3.dayOfMonth()));
    }

    public void testPropertyHashCode() {
        MonthDay test1 = new MonthDay(5, 11);
        MonthDay test2 = new MonthDay(5, 12);
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test1.dayOfMonth().hashCode())));
        TestCase.assertEquals(false, ((test1.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test1.monthOfYear().hashCode()) == (test1.monthOfYear().hashCode())));
        TestCase.assertEquals(true, ((test1.monthOfYear().hashCode()) == (test2.monthOfYear().hashCode())));
    }

    public void testPropertyEqualsHashCodeLenient() {
        MonthDay test1 = new MonthDay(5, 6, LenientChronology.getInstance(TestMonthDay_Properties.COPTIC_PARIS));
        MonthDay test2 = new MonthDay(5, 6, LenientChronology.getInstance(TestMonthDay_Properties.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test1.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test2.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
    }

    public void testPropertyEqualsHashCodeStrict() {
        MonthDay test1 = new MonthDay(5, 6, StrictChronology.getInstance(TestMonthDay_Properties.COPTIC_PARIS));
        MonthDay test2 = new MonthDay(5, 6, StrictChronology.getInstance(TestMonthDay_Properties.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test1.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test2.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
    }
}

