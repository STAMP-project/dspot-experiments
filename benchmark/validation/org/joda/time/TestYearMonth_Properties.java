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
 * This class is a Junit unit test for YearMonth.
 *
 * @author Stephen Colebourne
 */
public class TestYearMonth_Properties extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestYearMonth_Properties.PARIS);

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private long TEST_TIME1 = ((((((31L + 28L) + 31L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2 = ((((((((365L + 31L) + 28L) + 31L) + 30L) + 7L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone zone = null;

    private Locale systemDefaultLocale = null;

    public TestYearMonth_Properties(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testPropertyGetYear() {
        YearMonth test = new YearMonth(1972, 6);
        TestCase.assertSame(test.getChronology().year(), test.year().getField());
        TestCase.assertEquals("year", test.year().getName());
        TestCase.assertEquals("Property[year]", test.year().toString());
        TestCase.assertSame(test, test.year().getReadablePartial());
        TestCase.assertSame(test, test.year().getYearMonth());
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
        YearMonth test = new YearMonth(1972, 6);
        TestCase.assertEquals((-292275054), test.year().getMinimumValue());
        TestCase.assertEquals((-292275054), test.year().getMinimumValueOverall());
        TestCase.assertEquals(292278993, test.year().getMaximumValue());
        TestCase.assertEquals(292278993, test.year().getMaximumValueOverall());
    }

    public void testPropertyAddYear() {
        YearMonth test = new YearMonth(1972, 6);
        YearMonth copy = test.year().addToCopy(9);
        check(test, 1972, 6);
        check(copy, 1981, 6);
        copy = test.year().addToCopy(0);
        check(copy, 1972, 6);
        copy = test.year().addToCopy((292277023 - 1972));
        check(copy, 292277023, 6);
        try {
            test.year().addToCopy(((292278993 - 1972) + 1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 1972, 6);
        copy = test.year().addToCopy((-1972));
        check(copy, 0, 6);
        copy = test.year().addToCopy((-1973));
        check(copy, (-1), 6);
        try {
            test.year().addToCopy((((-292275054) - 1972) - 1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 1972, 6);
    }

    public void testPropertyAddWrapFieldYear() {
        YearMonth test = new YearMonth(1972, 6);
        YearMonth copy = test.year().addWrapFieldToCopy(9);
        check(test, 1972, 6);
        check(copy, 1981, 6);
        copy = test.year().addWrapFieldToCopy(0);
        check(copy, 1972, 6);
        copy = test.year().addWrapFieldToCopy(((292278993 - 1972) + 1));
        check(copy, (-292275054), 6);
        copy = test.year().addWrapFieldToCopy((((-292275054) - 1972) - 1));
        check(copy, 292278993, 6);
    }

    public void testPropertySetYear() {
        YearMonth test = new YearMonth(1972, 6);
        YearMonth copy = test.year().setCopy(12);
        check(test, 1972, 6);
        check(copy, 12, 6);
    }

    public void testPropertySetTextYear() {
        YearMonth test = new YearMonth(1972, 6);
        YearMonth copy = test.year().setCopy("12");
        check(test, 1972, 6);
        check(copy, 12, 6);
    }

    public void testPropertyCompareToYear() {
        YearMonth test1 = new YearMonth(TEST_TIME1);
        YearMonth test2 = new YearMonth(TEST_TIME2);
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
        YearMonth test = new YearMonth(1972, 6);
        TestCase.assertSame(test.getChronology().monthOfYear(), test.monthOfYear().getField());
        TestCase.assertEquals("monthOfYear", test.monthOfYear().getName());
        TestCase.assertEquals("Property[monthOfYear]", test.monthOfYear().toString());
        TestCase.assertSame(test, test.monthOfYear().getReadablePartial());
        TestCase.assertSame(test, test.monthOfYear().getYearMonth());
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
        test = new YearMonth(1972, 7);
        TestCase.assertEquals("juillet", test.monthOfYear().getAsText(Locale.FRENCH));
        TestCase.assertEquals("juil.", test.monthOfYear().getAsShortText(Locale.FRENCH));
    }

    public void testPropertyGetMaxMinValuesMonth() {
        YearMonth test = new YearMonth(1972, 6);
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValue());
        TestCase.assertEquals(1, test.monthOfYear().getMinimumValueOverall());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValue());
        TestCase.assertEquals(12, test.monthOfYear().getMaximumValueOverall());
    }

    public void testPropertyAddMonth() {
        YearMonth test = new YearMonth(1972, 6);
        YearMonth copy = test.monthOfYear().addToCopy(6);
        check(test, 1972, 6);
        check(copy, 1972, 12);
        copy = test.monthOfYear().addToCopy(7);
        check(copy, 1973, 1);
        copy = test.monthOfYear().addToCopy((-5));
        check(copy, 1972, 1);
        copy = test.monthOfYear().addToCopy((-6));
        check(copy, 1971, 12);
    }

    public void testPropertyAddWrapFieldMonth() {
        YearMonth test = new YearMonth(1972, 6);
        YearMonth copy = test.monthOfYear().addWrapFieldToCopy(4);
        check(test, 1972, 6);
        check(copy, 1972, 10);
        copy = test.monthOfYear().addWrapFieldToCopy(8);
        check(copy, 1972, 2);
        copy = test.monthOfYear().addWrapFieldToCopy((-8));
        check(copy, 1972, 10);
    }

    public void testPropertySetMonth() {
        YearMonth test = new YearMonth(1972, 6);
        YearMonth copy = test.monthOfYear().setCopy(12);
        check(test, 1972, 6);
        check(copy, 1972, 12);
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

    public void testPropertySetTextMonth() {
        YearMonth test = new YearMonth(1972, 6);
        YearMonth copy = test.monthOfYear().setCopy("12");
        check(test, 1972, 6);
        check(copy, 1972, 12);
        copy = test.monthOfYear().setCopy("December");
        check(test, 1972, 6);
        check(copy, 1972, 12);
        copy = test.monthOfYear().setCopy("Dec");
        check(test, 1972, 6);
        check(copy, 1972, 12);
    }

    public void testPropertyCompareToMonth() {
        YearMonth test1 = new YearMonth(TEST_TIME1);
        YearMonth test2 = new YearMonth(TEST_TIME2);
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
    public void testPropertyEquals() {
        YearMonth test1 = new YearMonth(11, 11);
        YearMonth test2 = new YearMonth(11, 12);
        YearMonth test3 = new YearMonth(11, 11, CopticChronology.getInstanceUTC());
        TestCase.assertEquals(true, test1.monthOfYear().equals(test1.monthOfYear()));
        TestCase.assertEquals(false, test1.monthOfYear().equals(test1.year()));
        TestCase.assertEquals(false, test1.monthOfYear().equals(test2.monthOfYear()));
        TestCase.assertEquals(false, test1.monthOfYear().equals(test2.year()));
        TestCase.assertEquals(false, test1.year().equals(test1.monthOfYear()));
        TestCase.assertEquals(true, test1.year().equals(test1.year()));
        TestCase.assertEquals(false, test1.year().equals(test2.monthOfYear()));
        TestCase.assertEquals(true, test1.year().equals(test2.year()));
        TestCase.assertEquals(false, test1.monthOfYear().equals(null));
        TestCase.assertEquals(false, test1.monthOfYear().equals("any"));
        // chrono
        TestCase.assertEquals(false, test1.monthOfYear().equals(test3.monthOfYear()));
    }

    public void testPropertyHashCode() {
        YearMonth test1 = new YearMonth(2005, 11);
        YearMonth test2 = new YearMonth(2005, 12);
        TestCase.assertEquals(true, ((test1.monthOfYear().hashCode()) == (test1.monthOfYear().hashCode())));
        TestCase.assertEquals(false, ((test1.monthOfYear().hashCode()) == (test2.monthOfYear().hashCode())));
        TestCase.assertEquals(true, ((test1.year().hashCode()) == (test1.year().hashCode())));
        TestCase.assertEquals(true, ((test1.year().hashCode()) == (test2.year().hashCode())));
    }

    public void testPropertyEqualsHashCodeLenient() {
        YearMonth test1 = new YearMonth(1970, 6, LenientChronology.getInstance(TestYearMonth_Properties.COPTIC_PARIS));
        YearMonth test2 = new YearMonth(1970, 6, LenientChronology.getInstance(TestYearMonth_Properties.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.monthOfYear().equals(test2.monthOfYear()));
        TestCase.assertEquals(true, test2.monthOfYear().equals(test1.monthOfYear()));
        TestCase.assertEquals(true, test1.monthOfYear().equals(test1.monthOfYear()));
        TestCase.assertEquals(true, test2.monthOfYear().equals(test2.monthOfYear()));
        TestCase.assertEquals(true, ((test1.monthOfYear().hashCode()) == (test2.monthOfYear().hashCode())));
        TestCase.assertEquals(true, ((test1.monthOfYear().hashCode()) == (test1.monthOfYear().hashCode())));
        TestCase.assertEquals(true, ((test2.monthOfYear().hashCode()) == (test2.monthOfYear().hashCode())));
    }

    public void testPropertyEqualsHashCodeStrict() {
        YearMonth test1 = new YearMonth(1970, 6, StrictChronology.getInstance(TestYearMonth_Properties.COPTIC_PARIS));
        YearMonth test2 = new YearMonth(1970, 6, StrictChronology.getInstance(TestYearMonth_Properties.COPTIC_PARIS));
        TestCase.assertEquals(true, test1.monthOfYear().equals(test2.monthOfYear()));
        TestCase.assertEquals(true, test2.monthOfYear().equals(test1.monthOfYear()));
        TestCase.assertEquals(true, test1.monthOfYear().equals(test1.monthOfYear()));
        TestCase.assertEquals(true, test2.monthOfYear().equals(test2.monthOfYear()));
        TestCase.assertEquals(true, ((test1.monthOfYear().hashCode()) == (test2.monthOfYear().hashCode())));
        TestCase.assertEquals(true, ((test1.monthOfYear().hashCode()) == (test1.monthOfYear().hashCode())));
        TestCase.assertEquals(true, ((test2.monthOfYear().hashCode()) == (test2.monthOfYear().hashCode())));
    }
}

