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


import Seconds.MAX_VALUE;
import Seconds.MIN_VALUE;
import Seconds.ONE;
import Seconds.THREE;
import Seconds.TWO;
import Seconds.ZERO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;

import static DateTimeConstants.MILLIS_PER_SECOND;
import static Seconds.THREE;


/**
 * This class is a Junit unit test for Seconds.
 *
 * @author Stephen Colebourne
 */
public class TestSeconds extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    public TestSeconds(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testConstants() {
        TestCase.assertEquals(0, ZERO.getSeconds());
        TestCase.assertEquals(1, ONE.getSeconds());
        TestCase.assertEquals(2, TWO.getSeconds());
        TestCase.assertEquals(3, THREE.getSeconds());
        TestCase.assertEquals(Integer.MAX_VALUE, MAX_VALUE.getSeconds());
        TestCase.assertEquals(Integer.MIN_VALUE, MIN_VALUE.getSeconds());
    }

    // -----------------------------------------------------------------------
    public void testFactory_seconds_int() {
        TestCase.assertSame(ZERO, Seconds.seconds(0));
        TestCase.assertSame(ONE, Seconds.seconds(1));
        TestCase.assertSame(TWO, Seconds.seconds(2));
        TestCase.assertSame(THREE, Seconds.seconds(3));
        TestCase.assertSame(MAX_VALUE, Seconds.seconds(Integer.MAX_VALUE));
        TestCase.assertSame(MIN_VALUE, Seconds.seconds(Integer.MIN_VALUE));
        TestCase.assertEquals((-1), Seconds.seconds((-1)).getSeconds());
        TestCase.assertEquals(4, Seconds.seconds(4).getSeconds());
    }

    // -----------------------------------------------------------------------
    public void testFactory_secondsBetween_RInstant() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 3, 0, TestSeconds.PARIS);
        DateTime end1 = new DateTime(2006, 6, 9, 12, 0, 6, 0, TestSeconds.PARIS);
        DateTime end2 = new DateTime(2006, 6, 9, 12, 0, 9, 0, TestSeconds.PARIS);
        TestCase.assertEquals(3, Seconds.secondsBetween(start, end1).getSeconds());
        TestCase.assertEquals(0, Seconds.secondsBetween(start, start).getSeconds());
        TestCase.assertEquals(0, Seconds.secondsBetween(end1, end1).getSeconds());
        TestCase.assertEquals((-3), Seconds.secondsBetween(end1, start).getSeconds());
        TestCase.assertEquals(6, Seconds.secondsBetween(start, end2).getSeconds());
    }

    public void testFactory_secondsBetween_RPartial() {
        LocalTime start = new LocalTime(12, 0, 3);
        LocalTime end1 = new LocalTime(12, 0, 6);
        @SuppressWarnings("deprecation")
        TimeOfDay end2 = new TimeOfDay(12, 0, 9);
        TestCase.assertEquals(3, Seconds.secondsBetween(start, end1).getSeconds());
        TestCase.assertEquals(0, Seconds.secondsBetween(start, start).getSeconds());
        TestCase.assertEquals(0, Seconds.secondsBetween(end1, end1).getSeconds());
        TestCase.assertEquals((-3), Seconds.secondsBetween(end1, start).getSeconds());
        TestCase.assertEquals(6, Seconds.secondsBetween(start, end2).getSeconds());
    }

    public void testFactory_secondsIn_RInterval() {
        DateTime start = new DateTime(2006, 6, 9, 12, 0, 3, 0, TestSeconds.PARIS);
        DateTime end1 = new DateTime(2006, 6, 9, 12, 0, 6, 0, TestSeconds.PARIS);
        DateTime end2 = new DateTime(2006, 6, 9, 12, 0, 9, 0, TestSeconds.PARIS);
        TestCase.assertEquals(0, Seconds.secondsIn(((ReadableInterval) (null))).getSeconds());
        TestCase.assertEquals(3, Seconds.secondsIn(new Interval(start, end1)).getSeconds());
        TestCase.assertEquals(0, Seconds.secondsIn(new Interval(start, start)).getSeconds());
        TestCase.assertEquals(0, Seconds.secondsIn(new Interval(end1, end1)).getSeconds());
        TestCase.assertEquals(6, Seconds.secondsIn(new Interval(start, end2)).getSeconds());
    }

    public void testFactory_standardSecondsIn_RPeriod() {
        TestCase.assertEquals(0, Seconds.standardSecondsIn(((ReadablePeriod) (null))).getSeconds());
        TestCase.assertEquals(0, Seconds.standardSecondsIn(Period.ZERO).getSeconds());
        TestCase.assertEquals(1, Seconds.standardSecondsIn(new Period(0, 0, 0, 0, 0, 0, 1, 0)).getSeconds());
        TestCase.assertEquals(123, Seconds.standardSecondsIn(Period.seconds(123)).getSeconds());
        TestCase.assertEquals((-987), Seconds.standardSecondsIn(Period.seconds((-987))).getSeconds());
        TestCase.assertEquals((((2 * 24) * 60) * 60), Seconds.standardSecondsIn(Period.days(2)).getSeconds());
        try {
            Seconds.standardSecondsIn(Period.months(1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    public void testFactory_parseSeconds_String() {
        TestCase.assertEquals(0, Seconds.parseSeconds(((String) (null))).getSeconds());
        TestCase.assertEquals(0, Seconds.parseSeconds("PT0S").getSeconds());
        TestCase.assertEquals(1, Seconds.parseSeconds("PT1S").getSeconds());
        TestCase.assertEquals((-3), Seconds.parseSeconds("PT-3S").getSeconds());
        TestCase.assertEquals(2, Seconds.parseSeconds("P0Y0M0DT2S").getSeconds());
        TestCase.assertEquals(2, Seconds.parseSeconds("PT0H2S").getSeconds());
        try {
            Seconds.parseSeconds("P1Y1D");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        try {
            Seconds.parseSeconds("P1DT1S");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testGetMethods() {
        Seconds test = Seconds.seconds(20);
        TestCase.assertEquals(20, test.getSeconds());
    }

    public void testGetFieldType() {
        Seconds test = Seconds.seconds(20);
        TestCase.assertEquals(DurationFieldType.seconds(), test.getFieldType());
    }

    public void testGetPeriodType() {
        Seconds test = Seconds.seconds(20);
        TestCase.assertEquals(PeriodType.seconds(), test.getPeriodType());
    }

    // -----------------------------------------------------------------------
    public void testIsGreaterThan() {
        TestCase.assertEquals(true, THREE.isGreaterThan(TWO));
        TestCase.assertEquals(false, THREE.isGreaterThan(THREE));
        TestCase.assertEquals(false, TWO.isGreaterThan(THREE));
        TestCase.assertEquals(true, ONE.isGreaterThan(null));
        TestCase.assertEquals(false, Seconds.seconds((-1)).isGreaterThan(null));
    }

    public void testIsLessThan() {
        TestCase.assertEquals(false, THREE.isLessThan(TWO));
        TestCase.assertEquals(false, THREE.isLessThan(THREE));
        TestCase.assertEquals(true, TWO.isLessThan(THREE));
        TestCase.assertEquals(false, ONE.isLessThan(null));
        TestCase.assertEquals(true, Seconds.seconds((-1)).isLessThan(null));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        Seconds test = Seconds.seconds(20);
        TestCase.assertEquals("PT20S", test.toString());
        test = Seconds.seconds((-20));
        TestCase.assertEquals("PT-20S", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Seconds test = THREE;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Seconds result = ((Seconds) (ois.readObject()));
        ois.close();
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToStandardWeeks() {
        Seconds test = Seconds.seconds(((((60 * 60) * 24) * 7) * 2));
        Weeks expected = Weeks.weeks(2);
        TestCase.assertEquals(expected, test.toStandardWeeks());
    }

    public void testToStandardDays() {
        Seconds test = Seconds.seconds((((60 * 60) * 24) * 2));
        Days expected = Days.days(2);
        TestCase.assertEquals(expected, test.toStandardDays());
    }

    public void testToStandardHours() {
        Seconds test = Seconds.seconds(((60 * 60) * 2));
        Hours expected = Hours.hours(2);
        TestCase.assertEquals(expected, test.toStandardHours());
    }

    public void testToStandardMinutes() {
        Seconds test = Seconds.seconds((60 * 2));
        Minutes expected = Minutes.minutes(2);
        TestCase.assertEquals(expected, test.toStandardMinutes());
    }

    public void testToStandardDuration() {
        Seconds test = Seconds.seconds(20);
        Duration expected = new Duration((20L * (MILLIS_PER_SECOND)));
        TestCase.assertEquals(expected, test.toStandardDuration());
        expected = new Duration((((long) (Integer.MAX_VALUE)) * (MILLIS_PER_SECOND)));
        TestCase.assertEquals(expected, MAX_VALUE.toStandardDuration());
    }

    // -----------------------------------------------------------------------
    public void testPlus_int() {
        Seconds test2 = Seconds.seconds(2);
        Seconds result = test2.plus(3);
        TestCase.assertEquals(2, test2.getSeconds());
        TestCase.assertEquals(5, result.getSeconds());
        TestCase.assertEquals(1, ONE.plus(0).getSeconds());
        try {
            MAX_VALUE.plus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testPlus_Seconds() {
        Seconds test2 = Seconds.seconds(2);
        Seconds test3 = Seconds.seconds(3);
        Seconds result = test2.plus(test3);
        TestCase.assertEquals(2, test2.getSeconds());
        TestCase.assertEquals(3, test3.getSeconds());
        TestCase.assertEquals(5, result.getSeconds());
        TestCase.assertEquals(1, ONE.plus(ZERO).getSeconds());
        TestCase.assertEquals(1, ONE.plus(((Seconds) (null))).getSeconds());
        try {
            MAX_VALUE.plus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_int() {
        Seconds test2 = Seconds.seconds(2);
        Seconds result = test2.minus(3);
        TestCase.assertEquals(2, test2.getSeconds());
        TestCase.assertEquals((-1), result.getSeconds());
        TestCase.assertEquals(1, ONE.minus(0).getSeconds());
        try {
            MIN_VALUE.minus(1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMinus_Seconds() {
        Seconds test2 = Seconds.seconds(2);
        Seconds test3 = Seconds.seconds(3);
        Seconds result = test2.minus(test3);
        TestCase.assertEquals(2, test2.getSeconds());
        TestCase.assertEquals(3, test3.getSeconds());
        TestCase.assertEquals((-1), result.getSeconds());
        TestCase.assertEquals(1, ONE.minus(ZERO).getSeconds());
        TestCase.assertEquals(1, ONE.minus(((Seconds) (null))).getSeconds());
        try {
            MIN_VALUE.minus(ONE);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testMultipliedBy_int() {
        Seconds test = Seconds.seconds(2);
        TestCase.assertEquals(6, test.multipliedBy(3).getSeconds());
        TestCase.assertEquals(2, test.getSeconds());
        TestCase.assertEquals((-6), test.multipliedBy((-3)).getSeconds());
        TestCase.assertSame(test, test.multipliedBy(1));
        Seconds halfMax = Seconds.seconds((((Integer.MAX_VALUE) / 2) + 1));
        try {
            halfMax.multipliedBy(2);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testDividedBy_int() {
        Seconds test = Seconds.seconds(12);
        TestCase.assertEquals(6, test.dividedBy(2).getSeconds());
        TestCase.assertEquals(12, test.getSeconds());
        TestCase.assertEquals(4, test.dividedBy(3).getSeconds());
        TestCase.assertEquals(3, test.dividedBy(4).getSeconds());
        TestCase.assertEquals(2, test.dividedBy(5).getSeconds());
        TestCase.assertEquals(2, test.dividedBy(6).getSeconds());
        TestCase.assertSame(test, test.dividedBy(1));
        try {
            ONE.dividedBy(0);
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    public void testNegated() {
        Seconds test = Seconds.seconds(12);
        TestCase.assertEquals((-12), test.negated().getSeconds());
        TestCase.assertEquals(12, test.getSeconds());
        try {
            MIN_VALUE.negated();
            TestCase.fail();
        } catch (ArithmeticException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void testAddToLocalDate() {
        Seconds test = Seconds.seconds(26);
        LocalDateTime date = new LocalDateTime(2006, 6, 1, 0, 0, 0, 0);
        LocalDateTime expected = new LocalDateTime(2006, 6, 1, 0, 0, 26, 0);
        TestCase.assertEquals(expected, date.plus(test));
    }
}

