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


import DateTimeConstants.MILLIS_PER_DAY;
import Period.ZERO;
import junit.framework.TestCase;
import org.joda.time.base.BaseSingleFieldPeriod;


/**
 * This class is a Junit unit test for BaseSingleFieldPeriod.
 *
 * @author Stephen Colebourne
 */
public class TestBaseSingleFieldPeriod extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    public TestBaseSingleFieldPeriod(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testFactory_between_RInstant() {
        // test using Days
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 0, 0, TestBaseSingleFieldPeriod.PARIS);
        DateTime end1 = new DateTime(2006, 6, 12, 12, 0, 0, 0, TestBaseSingleFieldPeriod.PARIS);
        DateTime end2 = new DateTime(2006, 6, 15, 18, 0, 0, 0, TestBaseSingleFieldPeriod.PARIS);
        TestCase.assertEquals(3, TestBaseSingleFieldPeriod.Single.between(start, end1, DurationFieldType.days()));
        TestCase.assertEquals(0, TestBaseSingleFieldPeriod.Single.between(start, start, DurationFieldType.days()));
        TestCase.assertEquals(0, TestBaseSingleFieldPeriod.Single.between(end1, end1, DurationFieldType.days()));
        TestCase.assertEquals((-3), TestBaseSingleFieldPeriod.Single.between(end1, start, DurationFieldType.days()));
        TestCase.assertEquals(6, TestBaseSingleFieldPeriod.Single.between(start, end2, DurationFieldType.days()));
        try {
            TestBaseSingleFieldPeriod.Single.between(start, ((ReadableInstant) (null)), DurationFieldType.days());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            TestBaseSingleFieldPeriod.Single.between(((ReadableInstant) (null)), end1, DurationFieldType.days());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            TestBaseSingleFieldPeriod.Single.between(((ReadableInstant) (null)), ((ReadableInstant) (null)), DurationFieldType.days());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testFactory_standardPeriodIn_RPeriod() {
        TestCase.assertEquals(0, TestBaseSingleFieldPeriod.Single.standardPeriodIn(((ReadablePeriod) (null)), MILLIS_PER_DAY));
        TestCase.assertEquals(0, TestBaseSingleFieldPeriod.Single.standardPeriodIn(ZERO, MILLIS_PER_DAY));
        TestCase.assertEquals(1, TestBaseSingleFieldPeriod.Single.standardPeriodIn(new Period(0, 0, 0, 1, 0, 0, 0, 0), MILLIS_PER_DAY));
        TestCase.assertEquals(123, TestBaseSingleFieldPeriod.Single.standardPeriodIn(Period.days(123), MILLIS_PER_DAY));
        TestCase.assertEquals((-987), TestBaseSingleFieldPeriod.Single.standardPeriodIn(Period.days((-987)), MILLIS_PER_DAY));
        TestCase.assertEquals(1, TestBaseSingleFieldPeriod.Single.standardPeriodIn(Period.hours(47), MILLIS_PER_DAY));
        TestCase.assertEquals(2, TestBaseSingleFieldPeriod.Single.standardPeriodIn(Period.hours(48), MILLIS_PER_DAY));
        TestCase.assertEquals(2, TestBaseSingleFieldPeriod.Single.standardPeriodIn(Period.hours(49), MILLIS_PER_DAY));
        TestCase.assertEquals(14, TestBaseSingleFieldPeriod.Single.standardPeriodIn(Period.weeks(2), MILLIS_PER_DAY));
        try {
            TestBaseSingleFieldPeriod.Single.standardPeriodIn(Period.months(1), MILLIS_PER_DAY);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testValueIndexMethods() {
        TestBaseSingleFieldPeriod.Single test = new TestBaseSingleFieldPeriod.Single(20);
        TestCase.assertEquals(1, size());
        TestCase.assertEquals(20, test.getValue(0));
        try {
            test.getValue(1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }

    public void testFieldTypeIndexMethods() {
        TestBaseSingleFieldPeriod.Single test = new TestBaseSingleFieldPeriod.Single(20);
        TestCase.assertEquals(1, size());
        TestCase.assertEquals(DurationFieldType.days(), test.getFieldType(0));
        try {
            test.getFieldType(1);
            TestCase.fail();
        } catch (IndexOutOfBoundsException ex) {
            // expected
        }
    }

    public void testIsSupported() {
        TestBaseSingleFieldPeriod.Single test = new TestBaseSingleFieldPeriod.Single(20);
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.years()));
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.months()));
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.weeks()));
        TestCase.assertEquals(true, test.isSupported(DurationFieldType.days()));
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.hours()));
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.minutes()));
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.seconds()));
        TestCase.assertEquals(false, test.isSupported(DurationFieldType.millis()));
    }

    public void testGet() {
        TestBaseSingleFieldPeriod.Single test = new TestBaseSingleFieldPeriod.Single(20);
        TestCase.assertEquals(0, test.get(DurationFieldType.years()));
        TestCase.assertEquals(0, test.get(DurationFieldType.months()));
        TestCase.assertEquals(0, test.get(DurationFieldType.weeks()));
        TestCase.assertEquals(20, test.get(DurationFieldType.days()));
        TestCase.assertEquals(0, test.get(DurationFieldType.hours()));
        TestCase.assertEquals(0, test.get(DurationFieldType.minutes()));
        TestCase.assertEquals(0, test.get(DurationFieldType.seconds()));
        TestCase.assertEquals(0, test.get(DurationFieldType.millis()));
    }

    // -----------------------------------------------------------------------
    public void testEqualsHashCode() {
        TestBaseSingleFieldPeriod.Single testA = new TestBaseSingleFieldPeriod.Single(20);
        TestBaseSingleFieldPeriod.Single testB = new TestBaseSingleFieldPeriod.Single(20);
        TestCase.assertEquals(true, testA.equals(testB));
        TestCase.assertEquals(true, testB.equals(testA));
        TestCase.assertEquals(true, testA.equals(testA));
        TestCase.assertEquals(true, testB.equals(testB));
        TestCase.assertEquals(true, ((testA.hashCode()) == (testB.hashCode())));
        TestCase.assertEquals(true, ((testA.hashCode()) == (testA.hashCode())));
        TestCase.assertEquals(true, ((testB.hashCode()) == (testB.hashCode())));
        TestBaseSingleFieldPeriod.Single testC = new TestBaseSingleFieldPeriod.Single(30);
        TestCase.assertEquals(false, testA.equals(testC));
        TestCase.assertEquals(false, testB.equals(testC));
        TestCase.assertEquals(false, testC.equals(testA));
        TestCase.assertEquals(false, testC.equals(testB));
        TestCase.assertEquals(false, ((testA.hashCode()) == (testC.hashCode())));
        TestCase.assertEquals(false, ((testB.hashCode()) == (testC.hashCode())));
        TestCase.assertEquals(true, testA.equals(Days.days(20)));
        TestCase.assertEquals(true, testA.equals(new Period(0, 0, 0, 20, 0, 0, 0, 0, PeriodType.days())));
        TestCase.assertEquals(false, testA.equals(Period.days(2)));
        TestCase.assertEquals(false, testA.equals("Hello"));
        TestCase.assertEquals(false, testA.equals(Hours.hours(2)));
        TestCase.assertEquals(false, testA.equals(null));
    }

    public void testCompareTo() {
        TestBaseSingleFieldPeriod.Single test1 = new TestBaseSingleFieldPeriod.Single(21);
        TestBaseSingleFieldPeriod.Single test2 = new TestBaseSingleFieldPeriod.Single(22);
        TestBaseSingleFieldPeriod.Single test3 = new TestBaseSingleFieldPeriod.Single(23);
        TestCase.assertEquals(true, ((test1.compareTo(test1)) == 0));
        TestCase.assertEquals(true, ((test1.compareTo(test2)) < 0));
        TestCase.assertEquals(true, ((test1.compareTo(test3)) < 0));
        TestCase.assertEquals(true, ((test2.compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test2.compareTo(test2)) == 0));
        TestCase.assertEquals(true, ((test2.compareTo(test3)) < 0));
        TestCase.assertEquals(true, ((test3.compareTo(test1)) > 0));
        TestCase.assertEquals(true, ((test3.compareTo(test2)) > 0));
        TestCase.assertEquals(true, ((test3.compareTo(test3)) == 0));
        // try {
        // test1.compareTo("Hello");
        // fail();
        // } catch (ClassCastException ex) {
        // // expected
        // }
        // try {
        // test1.compareTo(new Period(0, 0, 0, 21, 0, 0, 0, 0, PeriodType.days()));
        // fail();
        // } catch (ClassCastException ex) {
        // // expected
        // }
        try {
            compareTo(null);
            TestCase.fail();
        } catch (NullPointerException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testToPeriod() {
        TestBaseSingleFieldPeriod.Single test = new TestBaseSingleFieldPeriod.Single(20);
        Period expected = Period.days(20);
        TestCase.assertEquals(expected, toPeriod());
    }

    public void testToMutablePeriod() {
        TestBaseSingleFieldPeriod.Single test = new TestBaseSingleFieldPeriod.Single(20);
        MutablePeriod expected = new MutablePeriod(0, 0, 0, 20, 0, 0, 0, 0);
        TestCase.assertEquals(expected, toMutablePeriod());
    }

    // public void testToDurationFrom() {
    // Period test = new Period(123L);
    // assertEquals(new Duration(123L), test.toDurationFrom(new Instant(0L)));
    // }
    // 
    // public void testToDurationTo() {
    // Period test = new Period(123L);
    // assertEquals(new Duration(123L), test.toDurationTo(new Instant(123L)));
    // }
    // 
    // -----------------------------------------------------------------------
    public void testGetSetValue() {
        TestBaseSingleFieldPeriod.Single test = new TestBaseSingleFieldPeriod.Single(20);
        TestCase.assertEquals(20, test.getValue());
        test.setValue(10);
        TestCase.assertEquals(10, test.getValue());
    }

    // -----------------------------------------------------------------------
    /**
     * Test class.
     */
    static class Single extends BaseSingleFieldPeriod {
        public Single(int period) {
            super(period);
        }

        public static int between(ReadableInstant start, ReadableInstant end, DurationFieldType field) {
            return BaseSingleFieldPeriod.between(start, end, field);
        }

        public static int between(ReadablePartial start, ReadablePartial end, ReadablePeriod zeroInstance) {
            return BaseSingleFieldPeriod.between(start, end, zeroInstance);
        }

        public static int standardPeriodIn(ReadablePeriod period, long millisPerUnit) {
            return BaseSingleFieldPeriod.standardPeriodIn(period, millisPerUnit);
        }

        public DurationFieldType getFieldType() {
            return DurationFieldType.days();
        }

        public PeriodType getPeriodType() {
            return PeriodType.days();
        }

        public int getValue() {
            return super.getValue();
        }

        public void setValue(int value) {
            super.setValue(value);
        }
    }
}

