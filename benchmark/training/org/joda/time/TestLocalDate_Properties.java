/**
 * Copyright 2001-2010 Stephen Colebourne
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
 * This class is a Junit unit test for YearMonthDay.
 *
 * @author Stephen Colebourne
 */
public class TestLocalDate_Properties extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestLocalDate_Properties.PARIS);

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private long TEST_TIME1 = ((((((31L + 28L) + 31L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2 = ((((((((365L + 31L) + 28L) + 31L) + 30L) + 7L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone zone = null;

    private Locale systemDefaultLocale = null;

    public TestLocalDate_Properties(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetYear() {
        LocalDate test = new LocalDate(1972, 6, 9);
        TestCase.assertSame(test.getChronology().year(), test.year().getField());
        TestCase.assertEquals("year", test.year().getName());
        TestCase.assertEquals("Property[year]", test.year().toString());
        TestCase.assertSame(test, test.year().getLocalDate());
        TestCase.assertEquals(1972, test.year().get());
        TestCase.assertEquals("1972", test.year().getAsString());
        TestCase.assertEquals("1972", test.year().getAsText());
        TestCase.assertEquals("1972", test.year().getAsText(Locale.FRENCH));
        TestCase.assertEquals("1972", test.year().getAsShortText());
        TestCase.assertEquals("1972", test.year().getAsShortText(Locale.FRENCH));
        TestCase.assertEquals(test.getChronology().years(), test.year().getDurationField());
        TestCase.assertEquals(null, test.year().getRangeDurationField());
        TestCase.assertEquals(9, test.year().getMaximumTextLength(null));
        TestCase.assertEquals(9, test.year().getMaximumShortTextLength(null));
    }

    public void testPropertyGetMaxMinValuesYear() {
        LocalDate test = new LocalDate(1972, 6, 9);
        TestCase.assertEquals((-292275054), test.year().getMinimumValue());
        TestCase.assertEquals((-292275054), test.year().getMinimumValueOverall());
        TestCase.assertEquals(292278993, test.year().getMaximumValue());
        TestCase.assertEquals(292278993, test.year().getMaximumValueOverall());
    }

    public void testPropertyAddToCopyYear() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.year().addToCopy(9);
        check(test, 1972, 6, 9);
        check(copy, 1981, 6, 9);
        copy = test.year().addToCopy(0);
        check(copy, 1972, 6, 9);
        copy = test.year().addToCopy((292278993 - 1972));
        check(copy, 292278993, 6, 9);
        try {
            test.year().addToCopy(((292278993 - 1972) + 1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 1972, 6, 9);
        copy = test.year().addToCopy((-1972));
        check(copy, 0, 6, 9);
        copy = test.year().addToCopy((-1973));
        check(copy, (-1), 6, 9);
        try {
            test.year().addToCopy((((-292275054) - 1972) - 1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 1972, 6, 9);
    }

    public void testPropertyAddWrapFieldToCopyYear() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.year().addWrapFieldToCopy(9);
        check(test, 1972, 6, 9);
        check(copy, 1981, 6, 9);
        copy = test.year().addWrapFieldToCopy(0);
        check(copy, 1972, 6, 9);
        copy = test.year().addWrapFieldToCopy(((292278993 - 1972) + 1));
        check(copy, (-292275054), 6, 9);
        copy = test.year().addWrapFieldToCopy((((-292275054) - 1972) - 1));
        check(copy, 292278993, 6, 9);
    }

    public void testPropertySetCopyYear() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.year().setCopy(12);
        check(test, 1972, 6, 9);
        check(copy, 12, 6, 9);
    }

    public void testPropertySetCopyTextYear() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.year().setCopy("12");
        check(test, 1972, 6, 9);
        check(copy, 12, 6, 9);
    }

    public void testPropertyCompareToYear() {
        LocalDate test1 = new LocalDate(TEST_TIME1);
        LocalDate test2 = new LocalDate(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.year().compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test2.year().compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test1.year().compareTo(test1)) == 0));
        try {
            test1.year().compareTo(((ReadablePartial) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        DateTime dt1 = new DateTime(TEST_TIME1);
        DateTime dt2 = new DateTime(TEST_TIME2);
        TestCase.assertEquals(true, ((test1.year().compareTo(dt2)) < 0));
        TestCase.assertEquals(true, ((test2.year().compareTo(dt1)) > 0));
        TestCase.assertEquals(true, ((test1.year().compareTo(dt1)) == 0));
        try {
            test1.year().compareTo(((ReadableInstant) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetMonth() {
        LocalDate test = new LocalDate(1972, 6, 9);
        TestCase.assertSame(test.getChronology().monthOfYear(), test.monthOfYear().getField());
        TestCase.assertEquals("monthOfYear", test.monthOfYear().getName());
        TestCase.assertEquals("Property[monthOfYear]", test.monthOfYear().toString());
        TestCase.assertSame(test, test.monthOfYear().getLocalDate());
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
        test = new LocalDate(1972, 7, 9);
        TestCase.assertEquals("juillet", test.monthOfYear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("juil.", test.monthOfYear().getAsShortText(Locale.FRENCH));
    }

    public void testPropertyGetMaxMinValuesMonth() {
        LocalDate test = new LocalDate(1972, 6, 9);
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValue());
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValueOverall());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValue());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValueOverall());
    }

    public void testPropertyAddToCopyMonth() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.monthOfYear().addToCopy(6);
        check(test, 1972, 6, 9);
        check(copy, 1972, 12, 9);
        copy = test.monthOfYear().addToCopy(7);
        check(copy, 1973, 1, 9);
        copy = test.monthOfYear().addToCopy((-5));
        check(copy, 1972, 1, 9);
        copy = test.monthOfYear().addToCopy((-6));
        check(copy, 1971, 12, 9);
        test = new LocalDate(1972, 1, 31);
        copy = test.monthOfYear().addToCopy(1);
        check(copy, 1972, 2, 29);
        copy = test.monthOfYear().addToCopy(2);
        check(copy, 1972, 3, 31);
        copy = test.monthOfYear().addToCopy(3);
        check(copy, 1972, 4, 30);
        test = new LocalDate(1971, 1, 31);
        copy = test.monthOfYear().addToCopy(1);
        check(copy, 1971, 2, 28);
    }

    public void testPropertyAddWrapFieldToCopyMonth() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.monthOfYear().addWrapFieldToCopy(4);
        check(test, 1972, 6, 9);
        check(copy, 1972, 10, 9);
        copy = test.monthOfYear().addWrapFieldToCopy(8);
        check(copy, 1972, 2, 9);
        copy = test.monthOfYear().addWrapFieldToCopy((-8));
        check(copy, 1972, 10, 9);
        test = new LocalDate(1972, 1, 31);
        copy = test.monthOfYear().addWrapFieldToCopy(1);
        check(copy, 1972, 2, 29);
        copy = test.monthOfYear().addWrapFieldToCopy(2);
        check(copy, 1972, 3, 31);
        copy = test.monthOfYear().addWrapFieldToCopy(3);
        check(copy, 1972, 4, 30);
        test = new LocalDate(1971, 1, 31);
        copy = test.monthOfYear().addWrapFieldToCopy(1);
        check(copy, 1971, 2, 28);
    }

    public void testPropertySetCopyMonth() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.monthOfYear().setCopy(12);
        check(test, 1972, 6, 9);
        check(copy, 1972, 12, 9);
        test = new LocalDate(1972, 1, 31);
        copy = test.monthOfYear().setCopy(2);
        check(copy, 1972, 2, 29);
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

    public void testPropertySetCopyTextMonth() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.monthOfYear().setCopy("12");
        check(test, 1972, 6, 9);
        check(copy, 1972, 12, 9);
        copy = test.monthOfYear().setCopy("December");
        check(test, 1972, 6, 9);
        check(copy, 1972, 12, 9);
        copy = test.monthOfYear().setCopy("Dec");
        check(test, 1972, 6, 9);
        check(copy, 1972, 12, 9);
    }

    public void testPropertyCompareToMonth() {
        LocalDate test1 = new LocalDate(TEST_TIME1);
        LocalDate test2 = new LocalDate(TEST_TIME2);
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
    public void testPropertyGetDay() {
        LocalDate test = new LocalDate(1972, 6, 9);
        TestCase.assertSame(test.getChronology().dayOfMonth(), test.dayOfMonth().getField());
        TestCase.assertEquals("dayOfMonth", test.dayOfMonth().getName());
        TestCase.assertEquals("Property[dayOfMonth]", test.dayOfMonth().toString());
        TestCase.assertSame(test, test.dayOfMonth().getLocalDate());
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
    }

    public void testPropertyGetMaxMinValuesDay() {
        LocalDate test = new LocalDate(1972, 6, 9);
        TestCase.assertEquals(1, test.dayOfMonth().getMinimumValue());
        TestCase.assertEquals(1, test.dayOfMonth().getMinimumValueOverall());
        TestCase.assertEquals(30, test.dayOfMonth().getMaximumValue());
        TestCase.assertEquals(31, test.dayOfMonth().getMaximumValueOverall());
        test = new LocalDate(1972, 7, 9);
        TestCase.assertEquals(31, test.dayOfMonth().getMaximumValue());
        test = new LocalDate(1972, 2, 9);
        TestCase.assertEquals(29, test.dayOfMonth().getMaximumValue());
        test = new LocalDate(1971, 2, 9);
        TestCase.assertEquals(28, test.dayOfMonth().getMaximumValue());
    }

    public void testPropertyAddToCopyDay() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.dayOfMonth().addToCopy(9);
        check(test, 1972, 6, 9);
        check(copy, 1972, 6, 18);
        copy = test.dayOfMonth().addToCopy(21);
        check(copy, 1972, 6, 30);
        copy = test.dayOfMonth().addToCopy(22);
        check(copy, 1972, 7, 1);
        copy = test.dayOfMonth().addToCopy((22 + 30));
        check(copy, 1972, 7, 31);
        copy = test.dayOfMonth().addToCopy((22 + 31));
        check(copy, 1972, 8, 1);
        copy = test.dayOfMonth().addToCopy(((((((21 + 31) + 31) + 30) + 31) + 30) + 31));
        check(copy, 1972, 12, 31);
        copy = test.dayOfMonth().addToCopy(((((((22 + 31) + 31) + 30) + 31) + 30) + 31));
        check(copy, 1973, 1, 1);
        copy = test.dayOfMonth().addToCopy((-8));
        check(copy, 1972, 6, 1);
        copy = test.dayOfMonth().addToCopy((-9));
        check(copy, 1972, 5, 31);
        copy = test.dayOfMonth().addToCopy(((((((-8) - 31) - 30) - 31) - 29) - 31));
        check(copy, 1972, 1, 1);
        copy = test.dayOfMonth().addToCopy(((((((-9) - 31) - 30) - 31) - 29) - 31));
        check(copy, 1971, 12, 31);
    }

    public void testPropertyAddWrapFieldToCopyDay() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.dayOfMonth().addWrapFieldToCopy(21);
        check(test, 1972, 6, 9);
        check(copy, 1972, 6, 30);
        copy = test.dayOfMonth().addWrapFieldToCopy(22);
        check(copy, 1972, 6, 1);
        copy = test.dayOfMonth().addWrapFieldToCopy((-12));
        check(copy, 1972, 6, 27);
        test = new LocalDate(1972, 7, 9);
        copy = test.dayOfMonth().addWrapFieldToCopy(21);
        check(copy, 1972, 7, 30);
        copy = test.dayOfMonth().addWrapFieldToCopy(22);
        check(copy, 1972, 7, 31);
        copy = test.dayOfMonth().addWrapFieldToCopy(23);
        check(copy, 1972, 7, 1);
        copy = test.dayOfMonth().addWrapFieldToCopy((-12));
        check(copy, 1972, 7, 28);
    }

    public void testPropertySetCopyDay() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.dayOfMonth().setCopy(12);
        check(test, 1972, 6, 9);
        check(copy, 1972, 6, 12);
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

    public void testPropertySetCopyTextDay() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.dayOfMonth().setCopy("12");
        check(test, 1972, 6, 9);
        check(copy, 1972, 6, 12);
    }

    public void testPropertyWithMaximumValueDayOfMonth() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.dayOfMonth().withMaximumValue();
        check(test, 1972, 6, 9);
        check(copy, 1972, 6, 30);
    }

    public void testPropertyWithMinimumValueDayOfMonth() {
        LocalDate test = new LocalDate(1972, 6, 9);
        LocalDate copy = test.dayOfMonth().withMinimumValue();
        check(test, 1972, 6, 9);
        check(copy, 1972, 6, 1);
    }

    public void testPropertyCompareToDay() {
        LocalDate test1 = new LocalDate(TEST_TIME1);
        LocalDate test2 = new LocalDate(TEST_TIME2);
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

    public void testPropertyEquals() {
        LocalDate test1 = new LocalDate(2005, 11, 8);
        LocalDate test2 = new LocalDate(2005, 11, 9);
        LocalDate test3 = new LocalDate(2005, 11, 8, CopticChronology.getInstanceUTC());
        TestCase.assertEquals(false, test1.dayOfMonth().equals(test1.year()));
        TestCase.assertEquals(false, test1.dayOfMonth().equals(test1.monthOfYear()));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(false, test1.dayOfMonth().equals(test2.year()));
        TestCase.assertEquals(false, test1.dayOfMonth().equals(test2.monthOfYear()));
        TestCase.assertEquals(false, test1.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(false, test1.monthOfYear().equals(test1.year()));
        TestCase.assertEquals(true, test1.monthOfYear().equals(test1.monthOfYear()));
        TestCase.assertEquals(false, test1.monthOfYear().equals(test1.dayOfMonth()));
        TestCase.assertEquals(false, test1.monthOfYear().equals(test2.year()));
        TestCase.assertEquals(true, test1.monthOfYear().equals(test2.monthOfYear()));
        TestCase.assertEquals(false, test1.monthOfYear().equals(test2.dayOfMonth()));
        TestCase.assertEquals(false, test1.dayOfMonth().equals(null));
        TestCase.assertEquals(false, test1.dayOfMonth().equals("any"));
        // chrono
        TestCase.assertEquals(false, test1.dayOfMonth().equals(test3.dayOfMonth()));
    }

    public void testPropertyHashCode() {
        LocalDate test1 = new LocalDate(2005, 11, 8);
        LocalDate test2 = new LocalDate(2005, 11, 9);
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test1.dayOfMonth().hashCode())));
        TestCase.assertEquals(false, ((test1.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test1.monthOfYear().hashCode()) == (test1.monthOfYear().hashCode())));
        TestCase.assertEquals(true, ((test1.monthOfYear().hashCode()) == (test2.monthOfYear().hashCode())));
    }

    public void testPropertyEqualsHashCodeLenient() {
        LocalDate test1 = new LocalDate(1970, 6, 9, LenientChronology.getInstance(TestLocalDate_Properties.COPTIC_PARIS));
        LocalDate test2 = new LocalDate(1970, 6, 9, LenientChronology.getInstance(TestLocalDate_Properties.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test1.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test2.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
    }

    public void testPropertyEqualsHashCodeStrict() {
        LocalDate test1 = new LocalDate(1970, 6, 9, StrictChronology.getInstance(TestLocalDate_Properties.COPTIC_PARIS));
        LocalDate test2 = new LocalDate(1970, 6, 9, StrictChronology.getInstance(TestLocalDate_Properties.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test1.dayOfMonth().equals(test1.dayOfMonth()));
        TestCase.assertEquals(true, test2.dayOfMonth().equals(test2.dayOfMonth()));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test1.dayOfMonth().hashCode()) == (test1.dayOfMonth().hashCode())));
        TestCase.assertEquals(true, ((test2.dayOfMonth().hashCode()) == (test2.dayOfMonth().hashCode())));
    }
}

