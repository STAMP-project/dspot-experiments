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


import Duration.ZERO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.base.AbstractInterval;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.LenientChronology;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeZone.UTC;


/**
 * This class is a Junit unit test for Instant.
 *
 * @author Stephen Colebourne
 */
public class TestInterval_Basics extends TestCase {
    // Test in 2002/03 as time zones are more well known
    // (before the late 90's they were all over the place)
    private static final DateTimeZone MOSCOW = DateTimeZone.forID("Europe/Moscow");

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestInterval_Basics.PARIS);

    private Interval interval37;

    private Interval interval33;

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    long y2003days = (((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    // 2002-04-05
    private long TEST_TIME1 = ((((((((y2002days) + 31L) + 28L) + 31L) + 5L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    // 2003-05-06
    private long TEST_TIME2 = (((((((((y2003days) + 31L) + 28L) + 31L) + 30L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestInterval_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testTest() {
        TestCase.assertEquals("2002-06-09T00:00:00.000Z", new Instant(TEST_TIME_NOW).toString());
        TestCase.assertEquals("2002-04-05T12:24:00.000Z", new Instant(TEST_TIME1).toString());
        TestCase.assertEquals("2003-05-06T14:28:00.000Z", new Instant(TEST_TIME2).toString());
    }

    // -----------------------------------------------------------------------
    public void testGetMillis() {
        Interval test = new Interval(TEST_TIME1, TEST_TIME2);
        TestCase.assertEquals(TEST_TIME1, test.getStartMillis());
        TestCase.assertEquals(TEST_TIME1, test.getStart().getMillis());
        TestCase.assertEquals(TEST_TIME2, test.getEndMillis());
        TestCase.assertEquals(TEST_TIME2, test.getEnd().getMillis());
        TestCase.assertEquals(((TEST_TIME2) - (TEST_TIME1)), test.toDurationMillis());
        TestCase.assertEquals(((TEST_TIME2) - (TEST_TIME1)), test.toDuration().getMillis());
    }

    public void testGetDuration1() {
        Interval test = new Interval(TEST_TIME1, TEST_TIME2);
        TestCase.assertEquals(((TEST_TIME2) - (TEST_TIME1)), test.toDurationMillis());
        TestCase.assertEquals(((TEST_TIME2) - (TEST_TIME1)), test.toDuration().getMillis());
    }

    public void testGetDuration2() {
        Interval test = new Interval(TEST_TIME1, TEST_TIME1);
        TestCase.assertSame(ZERO, test.toDuration());
    }

    public void testGetDuration3() {
        Interval test = new Interval(Long.MIN_VALUE, (-2));
        TestCase.assertEquals(((-2L) - (Long.MIN_VALUE)), test.toDurationMillis());
    }

    public void testEqualsHashCode() {
        Interval test1 = new Interval(TEST_TIME1, TEST_TIME2);
        Interval test2 = new Interval(TEST_TIME1, TEST_TIME2);
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
        Interval test3 = new Interval(TEST_TIME_NOW, TEST_TIME2);
        TestCase.assertEquals(false, test1.equals(test3));
        TestCase.assertEquals(false, test2.equals(test3));
        TestCase.assertEquals(false, test3.equals(test1));
        TestCase.assertEquals(false, test3.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test3.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test3.hashCode())));
        Interval test4 = new Interval(TEST_TIME1, TEST_TIME2, GJChronology.getInstance());
        TestCase.assertEquals(true, test4.equals(test4));
        TestCase.assertEquals(false, test1.equals(test4));
        TestCase.assertEquals(false, test2.equals(test4));
        TestCase.assertEquals(false, test4.equals(test1));
        TestCase.assertEquals(false, test4.equals(test2));
        TestCase.assertEquals(false, ((test1.hashCode()) == (test4.hashCode())));
        TestCase.assertEquals(false, ((test2.hashCode()) == (test4.hashCode())));
        MutableInterval test5 = new MutableInterval(TEST_TIME1, TEST_TIME2);
        TestCase.assertEquals(true, test1.equals(test5));
        TestCase.assertEquals(true, test2.equals(test5));
        TestCase.assertEquals(false, test3.equals(test5));
        TestCase.assertEquals(true, test5.equals(test1));
        TestCase.assertEquals(true, test5.equals(test2));
        TestCase.assertEquals(false, test5.equals(test3));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test5.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test5.hashCode())));
        TestCase.assertEquals(false, ((test3.hashCode()) == (test5.hashCode())));
        TestCase.assertEquals(false, test1.equals("Hello"));
        TestCase.assertEquals(true, test1.equals(new TestInterval_Basics.MockInterval()));
        TestCase.assertEquals(false, test1.equals(new DateTime(TEST_TIME1)));
    }

    class MockInterval extends AbstractInterval {
        public MockInterval() {
            super();
        }

        public Chronology getChronology() {
            return ISOChronology.getInstance();
        }

        public long getStartMillis() {
            return TEST_TIME1;
        }

        public long getEndMillis() {
            return TEST_TIME2;
        }
    }

    public void testEqualsHashCodeLenient() {
        Interval test1 = new Interval(new DateTime(TEST_TIME1, LenientChronology.getInstance(TestInterval_Basics.COPTIC_PARIS)), new DateTime(TEST_TIME2, LenientChronology.getInstance(TestInterval_Basics.COPTIC_PARIS)));
        Interval test2 = new Interval(new DateTime(TEST_TIME1, LenientChronology.getInstance(TestInterval_Basics.COPTIC_PARIS)), new DateTime(TEST_TIME2, LenientChronology.getInstance(TestInterval_Basics.COPTIC_PARIS)));
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
    }

    public void testEqualsHashCodeStrict() {
        Interval test1 = new Interval(new DateTime(TEST_TIME1, LenientChronology.getInstance(TestInterval_Basics.COPTIC_PARIS)), new DateTime(TEST_TIME2, LenientChronology.getInstance(TestInterval_Basics.COPTIC_PARIS)));
        Interval test2 = new Interval(new DateTime(TEST_TIME1, LenientChronology.getInstance(TestInterval_Basics.COPTIC_PARIS)), new DateTime(TEST_TIME2, LenientChronology.getInstance(TestInterval_Basics.COPTIC_PARIS)));
        TestCase.assertEquals(true, test1.equals(test2));
        TestCase.assertEquals(true, test2.equals(test1));
        TestCase.assertEquals(true, test1.equals(test1));
        TestCase.assertEquals(true, test2.equals(test2));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test2.hashCode())));
        TestCase.assertEquals(true, ((test1.hashCode()) == (test1.hashCode())));
        TestCase.assertEquals(true, ((test2.hashCode()) == (test2.hashCode())));
    }

    // -----------------------------------------------------------------------
    public void test_useCase_ContainsOverlapAbutGap() {
        // this is a simple test to ensure that the use case of these methods is OK
        // when comparing any two intervals they can be in one and only one of these states
        // (a) have a gap between them, (b) abut or (c) overlap
        // contains is a subset of overlap
        Interval test1020 = new Interval(10, 20);
        // [4,8) [10,20) - gap
        Interval interval = new Interval(4, 8);
        TestCase.assertNotNull(test1020.gap(interval));
        TestCase.assertEquals(false, test1020.abuts(interval));
        TestCase.assertEquals(false, test1020.overlaps(interval));
        TestCase.assertEquals(false, test1020.contains(interval));
        TestCase.assertNotNull(interval.gap(test1020));
        TestCase.assertEquals(false, interval.abuts(test1020));
        TestCase.assertEquals(false, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
        // [6,10) [10,20) - abuts
        interval = new Interval(6, 10);
        TestCase.assertNull(test1020.gap(interval));
        TestCase.assertEquals(true, test1020.abuts(interval));
        TestCase.assertEquals(false, test1020.overlaps(interval));
        TestCase.assertEquals(false, test1020.contains(interval));
        TestCase.assertNull(interval.gap(test1020));
        TestCase.assertEquals(true, interval.abuts(test1020));
        TestCase.assertEquals(false, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
        // [8,12) [10,20) - overlaps
        interval = new Interval(8, 12);
        TestCase.assertNull(test1020.gap(interval));
        TestCase.assertEquals(false, test1020.abuts(interval));
        TestCase.assertEquals(true, test1020.overlaps(interval));
        TestCase.assertEquals(false, test1020.contains(interval));
        TestCase.assertNull(interval.gap(test1020));
        TestCase.assertEquals(false, interval.abuts(test1020));
        TestCase.assertEquals(true, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
        // [10,14) [10,20) - overlaps and contains-one-way
        interval = new Interval(10, 14);
        TestCase.assertNull(test1020.gap(interval));
        TestCase.assertEquals(false, test1020.abuts(interval));
        TestCase.assertEquals(true, test1020.overlaps(interval));
        TestCase.assertEquals(true, test1020.contains(interval));
        TestCase.assertNull(interval.gap(test1020));
        TestCase.assertEquals(false, interval.abuts(test1020));
        TestCase.assertEquals(true, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
        // [10,20) [10,20) - overlaps and contains-both-ways
        TestCase.assertNull(test1020.gap(interval));
        TestCase.assertEquals(false, test1020.abuts(test1020));
        TestCase.assertEquals(true, test1020.overlaps(test1020));
        TestCase.assertEquals(true, test1020.contains(test1020));
        // [10,20) [16,20) - overlaps and contains-one-way
        interval = new Interval(16, 20);
        TestCase.assertNull(test1020.gap(interval));
        TestCase.assertEquals(false, test1020.abuts(interval));
        TestCase.assertEquals(true, test1020.overlaps(interval));
        TestCase.assertEquals(true, test1020.contains(interval));
        TestCase.assertNull(interval.gap(test1020));
        TestCase.assertEquals(false, interval.abuts(test1020));
        TestCase.assertEquals(true, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
        // [10,20) [18,22) - overlaps
        interval = new Interval(18, 22);
        TestCase.assertNull(test1020.gap(interval));
        TestCase.assertEquals(false, test1020.abuts(interval));
        TestCase.assertEquals(true, test1020.overlaps(interval));
        TestCase.assertEquals(false, test1020.contains(interval));
        TestCase.assertNull(interval.gap(test1020));
        TestCase.assertEquals(false, interval.abuts(test1020));
        TestCase.assertEquals(true, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
        // [10,20) [20,24) - abuts
        interval = new Interval(20, 24);
        TestCase.assertNull(test1020.gap(interval));
        TestCase.assertEquals(true, test1020.abuts(interval));
        TestCase.assertEquals(false, test1020.overlaps(interval));
        TestCase.assertEquals(false, test1020.contains(interval));
        TestCase.assertNull(interval.gap(test1020));
        TestCase.assertEquals(true, interval.abuts(test1020));
        TestCase.assertEquals(false, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
        // [10,20) [22,26) - gap
        interval = new Interval(22, 26);
        TestCase.assertNotNull(test1020.gap(interval));
        TestCase.assertEquals(false, test1020.abuts(interval));
        TestCase.assertEquals(false, test1020.overlaps(interval));
        TestCase.assertEquals(false, test1020.contains(interval));
        TestCase.assertNotNull(interval.gap(test1020));
        TestCase.assertEquals(false, interval.abuts(test1020));
        TestCase.assertEquals(false, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
    }

    // -----------------------------------------------------------------------
    public void test_useCase_ContainsOverlapAbutGap_zeroDuration() {
        // this is a simple test to ensure that the use case of these methods
        // is OK when considering a zero duration inerval
        // when comparing any two intervals they can be in one and only one of these states
        // (a) have a gap between them, (b) abut or (c) overlap
        // contains is a subset of overlap
        Interval test1020 = new Interval(10, 20);
        // [8,8) [10,20) - gap
        Interval interval = new Interval(8, 8);
        TestCase.assertNotNull(test1020.gap(interval));
        TestCase.assertEquals(false, test1020.abuts(interval));
        TestCase.assertEquals(false, test1020.overlaps(interval));
        TestCase.assertEquals(false, test1020.contains(interval));
        TestCase.assertNotNull(interval.gap(test1020));
        TestCase.assertEquals(false, interval.abuts(test1020));
        TestCase.assertEquals(false, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
        // [10,10) [10,20) - abuts and contains-one-way
        interval = new Interval(10, 10);
        TestCase.assertNull(test1020.gap(interval));
        TestCase.assertEquals(true, test1020.abuts(interval));
        TestCase.assertEquals(false, test1020.overlaps(interval));// abuts, so can't overlap

        TestCase.assertEquals(true, test1020.contains(interval));// normal contains zero-duration

        TestCase.assertNull(interval.gap(test1020));
        TestCase.assertEquals(true, interval.abuts(test1020));
        TestCase.assertEquals(false, interval.overlaps(test1020));// abuts, so can't overlap

        TestCase.assertEquals(false, interval.contains(test1020));// zero-duration does not contain normal

        // [12,12) [10,20) - contains-one-way and overlaps
        interval = new Interval(12, 12);
        TestCase.assertNull(test1020.gap(interval));
        TestCase.assertEquals(false, test1020.abuts(interval));
        TestCase.assertEquals(true, test1020.overlaps(interval));
        TestCase.assertEquals(true, test1020.contains(interval));// normal contains zero-duration

        TestCase.assertNull(interval.gap(test1020));
        TestCase.assertEquals(false, interval.abuts(test1020));
        TestCase.assertEquals(true, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));// zero-duration does not contain normal

        // [10,20) [20,20) - abuts
        interval = new Interval(20, 20);
        TestCase.assertNull(test1020.gap(interval));
        TestCase.assertEquals(true, test1020.abuts(interval));
        TestCase.assertEquals(false, test1020.overlaps(interval));
        TestCase.assertEquals(false, test1020.contains(interval));
        TestCase.assertNull(interval.gap(test1020));
        TestCase.assertEquals(true, interval.abuts(test1020));
        TestCase.assertEquals(false, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
        // [10,20) [22,22) - gap
        interval = new Interval(22, 22);
        TestCase.assertNotNull(test1020.gap(interval));
        TestCase.assertEquals(false, test1020.abuts(interval));
        TestCase.assertEquals(false, test1020.overlaps(interval));
        TestCase.assertEquals(false, test1020.contains(interval));
        TestCase.assertNotNull(interval.gap(test1020));
        TestCase.assertEquals(false, interval.abuts(test1020));
        TestCase.assertEquals(false, interval.overlaps(test1020));
        TestCase.assertEquals(false, interval.contains(test1020));
    }

    // -----------------------------------------------------------------------
    public void test_useCase_ContainsOverlapAbutGap_bothZeroDuration() {
        // this is a simple test to ensure that the use case of these methods
        // is OK when considering two zero duration inervals
        // this is the simplest case, as the two intervals either have a gap or not
        // if not, then they are equal and abut
        Interval test0808 = new Interval(8, 8);
        Interval test1010 = new Interval(10, 10);
        // [8,8) [10,10) - gap
        TestCase.assertNotNull(test1010.gap(test0808));
        TestCase.assertEquals(false, test1010.abuts(test0808));
        TestCase.assertEquals(false, test1010.overlaps(test0808));
        TestCase.assertEquals(false, test1010.contains(test0808));
        TestCase.assertNotNull(test0808.gap(test1010));
        TestCase.assertEquals(false, test0808.abuts(test1010));
        TestCase.assertEquals(false, test0808.overlaps(test1010));
        TestCase.assertEquals(false, test0808.contains(test1010));
        // [10,10) [10,10) - abuts
        TestCase.assertNull(test1010.gap(test1010));
        TestCase.assertEquals(true, test1010.abuts(test1010));
        TestCase.assertEquals(false, test1010.overlaps(test1010));
        TestCase.assertEquals(false, test1010.contains(test1010));
    }

    // -----------------------------------------------------------------------
    public void testContains_long() {
        TestCase.assertEquals(false, interval37.contains(2));// value before

        TestCase.assertEquals(true, interval37.contains(3));
        TestCase.assertEquals(true, interval37.contains(4));
        TestCase.assertEquals(true, interval37.contains(5));
        TestCase.assertEquals(true, interval37.contains(6));
        TestCase.assertEquals(false, interval37.contains(7));// value after

        TestCase.assertEquals(false, interval37.contains(8));// value after

    }

    public void testContains_long_zeroDuration() {
        TestCase.assertEquals(false, interval33.contains(2));// value before

        TestCase.assertEquals(false, interval33.contains(3));// zero length duration contains nothing

        TestCase.assertEquals(false, interval33.contains(4));// value after

    }

    // -----------------------------------------------------------------------
    public void testContainsNow() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(false, interval37.containsNow());// value before

        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(true, interval37.containsNow());
        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(true, interval37.containsNow());
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(true, interval37.containsNow());
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(false, interval37.containsNow());// value after

        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(false, interval37.containsNow());// value after

        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(false, interval33.containsNow());// value before

        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(false, interval33.containsNow());// zero length duration contains nothing

        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(false, interval33.containsNow());// value after

    }

    // -----------------------------------------------------------------------
    public void testContains_RI() {
        TestCase.assertEquals(false, interval37.contains(new Instant(2)));// value before

        TestCase.assertEquals(true, interval37.contains(new Instant(3)));
        TestCase.assertEquals(true, interval37.contains(new Instant(4)));
        TestCase.assertEquals(true, interval37.contains(new Instant(5)));
        TestCase.assertEquals(true, interval37.contains(new Instant(6)));
        TestCase.assertEquals(false, interval37.contains(new Instant(7)));// value after

        TestCase.assertEquals(false, interval37.contains(new Instant(8)));// value after

    }

    public void testContains_RI_null() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(false, interval37.contains(((ReadableInstant) (null))));// value before

        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(true, interval37.contains(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(true, interval37.contains(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(true, interval37.contains(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(false, interval37.contains(((ReadableInstant) (null))));// value after

        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(false, interval37.contains(((ReadableInstant) (null))));// value after

    }

    public void testContains_RI_zeroDuration() {
        TestCase.assertEquals(false, interval33.contains(new Instant(2)));// value before

        TestCase.assertEquals(false, interval33.contains(new Instant(3)));// zero length duration contains nothing

        TestCase.assertEquals(false, interval33.contains(new Instant(4)));// value after

    }

    // -----------------------------------------------------------------------
    public void testContains_RInterval() {
        TestCase.assertEquals(false, interval37.contains(new Interval(1, 2)));// gap before

        TestCase.assertEquals(false, interval37.contains(new Interval(2, 2)));// gap before

        TestCase.assertEquals(false, interval37.contains(new Interval(2, 3)));// abuts before

        TestCase.assertEquals(true, interval37.contains(new Interval(3, 3)));
        TestCase.assertEquals(false, interval37.contains(new Interval(2, 4)));// starts before

        TestCase.assertEquals(true, interval37.contains(new Interval(3, 4)));
        TestCase.assertEquals(true, interval37.contains(new Interval(4, 4)));
        TestCase.assertEquals(false, interval37.contains(new Interval(2, 6)));// starts before

        TestCase.assertEquals(true, interval37.contains(new Interval(3, 6)));
        TestCase.assertEquals(true, interval37.contains(new Interval(4, 6)));
        TestCase.assertEquals(true, interval37.contains(new Interval(5, 6)));
        TestCase.assertEquals(true, interval37.contains(new Interval(6, 6)));
        TestCase.assertEquals(false, interval37.contains(new Interval(2, 7)));// starts before

        TestCase.assertEquals(true, interval37.contains(new Interval(3, 7)));
        TestCase.assertEquals(true, interval37.contains(new Interval(4, 7)));
        TestCase.assertEquals(true, interval37.contains(new Interval(5, 7)));
        TestCase.assertEquals(true, interval37.contains(new Interval(6, 7)));
        TestCase.assertEquals(false, interval37.contains(new Interval(7, 7)));// abuts after

        TestCase.assertEquals(false, interval37.contains(new Interval(2, 8)));// ends after

        TestCase.assertEquals(false, interval37.contains(new Interval(3, 8)));// ends after

        TestCase.assertEquals(false, interval37.contains(new Interval(4, 8)));// ends after

        TestCase.assertEquals(false, interval37.contains(new Interval(5, 8)));// ends after

        TestCase.assertEquals(false, interval37.contains(new Interval(6, 8)));// ends after

        TestCase.assertEquals(false, interval37.contains(new Interval(7, 8)));// abuts after

        TestCase.assertEquals(false, interval37.contains(new Interval(8, 8)));// gap after

        TestCase.assertEquals(false, interval37.contains(new Interval(8, 9)));// gap after

        TestCase.assertEquals(false, interval37.contains(new Interval(9, 9)));// gap after

    }

    public void testContains_RInterval_null() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(false, interval37.contains(((ReadableInterval) (null))));// gap before

        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(true, interval37.contains(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(true, interval37.contains(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(true, interval37.contains(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(false, interval37.contains(((ReadableInterval) (null))));// abuts after

        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(false, interval37.contains(((ReadableInterval) (null))));// gap after

    }

    public void testContains_RInterval_zeroDuration() {
        TestCase.assertEquals(false, interval33.contains(interval33));// zero length duration contains nothing

        TestCase.assertEquals(false, interval33.contains(interval37));// zero-duration cannot contain anything

        TestCase.assertEquals(true, interval37.contains(interval33));
        TestCase.assertEquals(false, interval33.contains(new Interval(1, 2)));// zero-duration cannot contain anything

        TestCase.assertEquals(false, interval33.contains(new Interval(8, 9)));// zero-duration cannot contain anything

        TestCase.assertEquals(false, interval33.contains(new Interval(1, 9)));// zero-duration cannot contain anything

        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(false, interval33.contains(((ReadableInterval) (null))));// gap before

        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(false, interval33.contains(((ReadableInterval) (null))));// zero length duration contains nothing

        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(false, interval33.contains(((ReadableInterval) (null))));// gap after

    }

    // -----------------------------------------------------------------------
    public void testOverlaps_RInterval() {
        TestCase.assertEquals(false, interval37.overlaps(new Interval(1, 2)));// gap before

        TestCase.assertEquals(false, interval37.overlaps(new Interval(2, 2)));// gap before

        TestCase.assertEquals(false, interval37.overlaps(new Interval(2, 3)));// abuts before

        TestCase.assertEquals(false, interval37.overlaps(new Interval(3, 3)));// abuts before

        TestCase.assertEquals(true, interval37.overlaps(new Interval(2, 4)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(3, 4)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(4, 4)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(2, 6)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(3, 6)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(4, 6)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(5, 6)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(6, 6)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(2, 7)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(3, 7)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(4, 7)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(5, 7)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(6, 7)));
        TestCase.assertEquals(false, interval37.overlaps(new Interval(7, 7)));// abuts after

        TestCase.assertEquals(true, interval37.overlaps(new Interval(2, 8)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(3, 8)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(4, 8)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(5, 8)));
        TestCase.assertEquals(true, interval37.overlaps(new Interval(6, 8)));
        TestCase.assertEquals(false, interval37.overlaps(new Interval(7, 8)));// abuts after

        TestCase.assertEquals(false, interval37.overlaps(new Interval(8, 8)));// gap after

        TestCase.assertEquals(false, interval37.overlaps(new Interval(8, 9)));// gap after

        TestCase.assertEquals(false, interval37.overlaps(new Interval(9, 9)));// gap after

    }

    public void testOverlaps_RInterval_null() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(false, interval37.overlaps(((ReadableInterval) (null))));// gap before

        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(false, interval37.overlaps(((ReadableInterval) (null))));// abuts before

        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(true, interval37.overlaps(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(true, interval37.overlaps(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(false, interval37.overlaps(((ReadableInterval) (null))));// abuts after

        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(false, interval37.overlaps(((ReadableInterval) (null))));// gap after

        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(false, interval33.overlaps(((ReadableInterval) (null))));// abuts before and after

    }

    public void testOverlaps_RInterval_zeroDuration() {
        TestCase.assertEquals(false, interval33.overlaps(interval33));// abuts before and after

        TestCase.assertEquals(false, interval33.overlaps(interval37));// abuts before

        TestCase.assertEquals(false, interval37.overlaps(interval33));// abuts before

        TestCase.assertEquals(false, interval33.overlaps(new Interval(1, 2)));
        TestCase.assertEquals(false, interval33.overlaps(new Interval(8, 9)));
        TestCase.assertEquals(true, interval33.overlaps(new Interval(1, 9)));
    }

    // -----------------------------------------------------------------------
    public void testOverlap_RInterval() {
        TestCase.assertEquals(null, interval37.overlap(new Interval(1, 2)));// gap before

        TestCase.assertEquals(null, interval37.overlap(new Interval(2, 2)));// gap before

        TestCase.assertEquals(null, interval37.overlap(new Interval(2, 3)));// abuts before

        TestCase.assertEquals(null, interval37.overlap(new Interval(3, 3)));// abuts before

        TestCase.assertEquals(new Interval(3, 4), interval37.overlap(new Interval(2, 4)));// truncated start

        TestCase.assertEquals(new Interval(3, 4), interval37.overlap(new Interval(3, 4)));
        TestCase.assertEquals(new Interval(4, 4), interval37.overlap(new Interval(4, 4)));
        TestCase.assertEquals(new Interval(3, 7), interval37.overlap(new Interval(2, 7)));// truncated start

        TestCase.assertEquals(new Interval(3, 7), interval37.overlap(new Interval(3, 7)));
        TestCase.assertEquals(new Interval(4, 7), interval37.overlap(new Interval(4, 7)));
        TestCase.assertEquals(new Interval(5, 7), interval37.overlap(new Interval(5, 7)));
        TestCase.assertEquals(new Interval(6, 7), interval37.overlap(new Interval(6, 7)));
        TestCase.assertEquals(null, interval37.overlap(new Interval(7, 7)));// abuts after

        TestCase.assertEquals(new Interval(3, 7), interval37.overlap(new Interval(2, 8)));// truncated start and end

        TestCase.assertEquals(new Interval(3, 7), interval37.overlap(new Interval(3, 8)));// truncated end

        TestCase.assertEquals(new Interval(4, 7), interval37.overlap(new Interval(4, 8)));// truncated end

        TestCase.assertEquals(new Interval(5, 7), interval37.overlap(new Interval(5, 8)));// truncated end

        TestCase.assertEquals(new Interval(6, 7), interval37.overlap(new Interval(6, 8)));// truncated end

        TestCase.assertEquals(null, interval37.overlap(new Interval(7, 8)));// abuts after

        TestCase.assertEquals(null, interval37.overlap(new Interval(8, 8)));// gap after

    }

    public void testOverlap_RInterval_null() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(null, interval37.overlap(((ReadableInterval) (null))));// gap before

        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(null, interval37.overlap(((ReadableInterval) (null))));// abuts before

        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(new Interval(4, 4), interval37.overlap(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(new Interval(6, 6), interval37.overlap(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(null, interval37.overlap(((ReadableInterval) (null))));// abuts after

        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(null, interval37.overlap(((ReadableInterval) (null))));// gap after

        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(null, interval33.overlap(((ReadableInterval) (null))));// abuts before and after

    }

    public void testOverlap_RInterval_zone() {
        Interval testA = new Interval(new DateTime(3, TestInterval_Basics.LONDON), new DateTime(7, TestInterval_Basics.LONDON));
        TestCase.assertEquals(ISOChronology.getInstance(TestInterval_Basics.LONDON), testA.getChronology());
        Interval testB = new Interval(new DateTime(4, TestInterval_Basics.MOSCOW), new DateTime(8, TestInterval_Basics.MOSCOW));
        TestCase.assertEquals(ISOChronology.getInstance(TestInterval_Basics.MOSCOW), testB.getChronology());
        Interval resultAB = testA.overlap(testB);
        TestCase.assertEquals(ISOChronology.getInstance(TestInterval_Basics.LONDON), resultAB.getChronology());
        Interval resultBA = testB.overlap(testA);
        TestCase.assertEquals(ISOChronology.getInstance(TestInterval_Basics.MOSCOW), resultBA.getChronology());
    }

    public void testOverlap_RInterval_zoneUTC() {
        Interval testA = new Interval(new Instant(3), new Instant(7));
        TestCase.assertEquals(ISOChronology.getInstanceUTC(), testA.getChronology());
        Interval testB = new Interval(new Instant(4), new Instant(8));
        TestCase.assertEquals(ISOChronology.getInstanceUTC(), testB.getChronology());
        Interval result = testA.overlap(testB);
        TestCase.assertEquals(ISOChronology.getInstanceUTC(), result.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testGap_RInterval() {
        TestCase.assertEquals(new Interval(1, 3), interval37.gap(new Interval(0, 1)));
        TestCase.assertEquals(new Interval(1, 3), interval37.gap(new Interval(1, 1)));
        TestCase.assertEquals(null, interval37.gap(new Interval(2, 3)));// abuts before

        TestCase.assertEquals(null, interval37.gap(new Interval(3, 3)));// abuts before

        TestCase.assertEquals(null, interval37.gap(new Interval(4, 6)));// overlaps

        TestCase.assertEquals(null, interval37.gap(new Interval(3, 7)));// overlaps

        TestCase.assertEquals(null, interval37.gap(new Interval(6, 7)));// overlaps

        TestCase.assertEquals(null, interval37.gap(new Interval(7, 7)));// abuts after

        TestCase.assertEquals(null, interval37.gap(new Interval(6, 8)));// overlaps

        TestCase.assertEquals(null, interval37.gap(new Interval(7, 8)));// abuts after

        TestCase.assertEquals(new Interval(7, 8), interval37.gap(new Interval(8, 8)));
        TestCase.assertEquals(null, interval37.gap(new Interval(6, 9)));// overlaps

        TestCase.assertEquals(null, interval37.gap(new Interval(7, 9)));// abuts after

        TestCase.assertEquals(new Interval(7, 8), interval37.gap(new Interval(8, 9)));
        TestCase.assertEquals(new Interval(7, 9), interval37.gap(new Interval(9, 9)));
    }

    public void testGap_RInterval_null() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(new Interval(2, 3), interval37.gap(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(null, interval37.gap(((ReadableInterval) (null))));// abuts before

        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(null, interval37.gap(((ReadableInterval) (null))));// overlaps

        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(null, interval37.gap(((ReadableInterval) (null))));// overlaps

        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(null, interval37.gap(((ReadableInterval) (null))));// abuts after

        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(new Interval(7, 8), interval37.gap(((ReadableInterval) (null))));
    }

    public void testGap_RInterval_zone() {
        Interval testA = new Interval(new DateTime(3, TestInterval_Basics.LONDON), new DateTime(7, TestInterval_Basics.LONDON));
        TestCase.assertEquals(ISOChronology.getInstance(TestInterval_Basics.LONDON), testA.getChronology());
        Interval testB = new Interval(new DateTime(1, TestInterval_Basics.MOSCOW), new DateTime(2, TestInterval_Basics.MOSCOW));
        TestCase.assertEquals(ISOChronology.getInstance(TestInterval_Basics.MOSCOW), testB.getChronology());
        Interval resultAB = testA.gap(testB);
        TestCase.assertEquals(ISOChronology.getInstance(TestInterval_Basics.LONDON), resultAB.getChronology());
        Interval resultBA = testB.gap(testA);
        TestCase.assertEquals(ISOChronology.getInstance(TestInterval_Basics.MOSCOW), resultBA.getChronology());
    }

    public void testGap_RInterval_zoneUTC() {
        Interval testA = new Interval(new Instant(3), new Instant(7));
        TestCase.assertEquals(ISOChronology.getInstanceUTC(), testA.getChronology());
        Interval testB = new Interval(new Instant(1), new Instant(2));
        TestCase.assertEquals(ISOChronology.getInstanceUTC(), testB.getChronology());
        Interval result = testA.gap(testB);
        TestCase.assertEquals(ISOChronology.getInstanceUTC(), result.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testAbuts_RInterval() {
        TestCase.assertEquals(false, interval37.abuts(new Interval(1, 2)));// gap before

        TestCase.assertEquals(false, interval37.abuts(new Interval(2, 2)));// gap before

        TestCase.assertEquals(true, interval37.abuts(new Interval(2, 3)));
        TestCase.assertEquals(true, interval37.abuts(new Interval(3, 3)));
        TestCase.assertEquals(false, interval37.abuts(new Interval(2, 4)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(3, 4)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(4, 4)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(2, 6)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(3, 6)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(4, 6)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(5, 6)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(6, 6)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(2, 7)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(3, 7)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(4, 7)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(5, 7)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(6, 7)));// overlaps

        TestCase.assertEquals(true, interval37.abuts(new Interval(7, 7)));
        TestCase.assertEquals(false, interval37.abuts(new Interval(2, 8)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(3, 8)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(4, 8)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(5, 8)));// overlaps

        TestCase.assertEquals(false, interval37.abuts(new Interval(6, 8)));// overlaps

        TestCase.assertEquals(true, interval37.abuts(new Interval(7, 8)));
        TestCase.assertEquals(false, interval37.abuts(new Interval(8, 8)));// gap after

        TestCase.assertEquals(false, interval37.abuts(new Interval(8, 9)));// gap after

        TestCase.assertEquals(false, interval37.abuts(new Interval(9, 9)));// gap after

    }

    public void testAbuts_RInterval_null() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(false, interval37.abuts(((ReadableInterval) (null))));// gap before

        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(true, interval37.abuts(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(false, interval37.abuts(((ReadableInterval) (null))));// overlaps

        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(false, interval37.abuts(((ReadableInterval) (null))));// overlaps

        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(true, interval37.abuts(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(false, interval37.abuts(((ReadableInterval) (null))));// gap after

    }

    // -----------------------------------------------------------------------
    public void testIsEqual_RI() {
        TestCase.assertEquals(false, interval37.isEqual(interval33));
        TestCase.assertEquals(true, interval37.isEqual(interval37));
    }

    // -----------------------------------------------------------------------
    public void testIsBefore_long() {
        TestCase.assertEquals(false, interval37.isBefore(2));
        TestCase.assertEquals(false, interval37.isBefore(3));
        TestCase.assertEquals(false, interval37.isBefore(4));
        TestCase.assertEquals(false, interval37.isBefore(5));
        TestCase.assertEquals(false, interval37.isBefore(6));
        TestCase.assertEquals(true, interval37.isBefore(7));
        TestCase.assertEquals(true, interval37.isBefore(8));
    }

    public void testIsBeforeNow() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(false, interval37.isBeforeNow());
        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(false, interval37.isBeforeNow());
        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(false, interval37.isBeforeNow());
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(false, interval37.isBeforeNow());
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(true, interval37.isBeforeNow());
        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(true, interval37.isBeforeNow());
    }

    public void testIsBefore_RI() {
        TestCase.assertEquals(false, interval37.isBefore(new Instant(2)));
        TestCase.assertEquals(false, interval37.isBefore(new Instant(3)));
        TestCase.assertEquals(false, interval37.isBefore(new Instant(4)));
        TestCase.assertEquals(false, interval37.isBefore(new Instant(5)));
        TestCase.assertEquals(false, interval37.isBefore(new Instant(6)));
        TestCase.assertEquals(true, interval37.isBefore(new Instant(7)));
        TestCase.assertEquals(true, interval37.isBefore(new Instant(8)));
    }

    public void testIsBefore_RI_null() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(false, interval37.isBefore(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(false, interval37.isBefore(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(false, interval37.isBefore(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(false, interval37.isBefore(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(true, interval37.isBefore(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(true, interval37.isBefore(((ReadableInstant) (null))));
    }

    public void testIsBefore_RInterval() {
        TestCase.assertEquals(false, interval37.isBefore(new Interval(Long.MIN_VALUE, 2)));
        TestCase.assertEquals(false, interval37.isBefore(new Interval(Long.MIN_VALUE, 3)));
        TestCase.assertEquals(false, interval37.isBefore(new Interval(Long.MIN_VALUE, 4)));
        TestCase.assertEquals(false, interval37.isBefore(new Interval(6, Long.MAX_VALUE)));
        TestCase.assertEquals(true, interval37.isBefore(new Interval(7, Long.MAX_VALUE)));
        TestCase.assertEquals(true, interval37.isBefore(new Interval(8, Long.MAX_VALUE)));
    }

    public void testIsBefore_RInterval_null() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(false, interval37.isBefore(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(false, interval37.isBefore(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(false, interval37.isBefore(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(false, interval37.isBefore(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(true, interval37.isBefore(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(true, interval37.isBefore(((ReadableInterval) (null))));
    }

    // -----------------------------------------------------------------------
    public void testIsAfter_long() {
        TestCase.assertEquals(true, interval37.isAfter(2));
        TestCase.assertEquals(false, interval37.isAfter(3));
        TestCase.assertEquals(false, interval37.isAfter(4));
        TestCase.assertEquals(false, interval37.isAfter(5));
        TestCase.assertEquals(false, interval37.isAfter(6));
        TestCase.assertEquals(false, interval37.isAfter(7));
        TestCase.assertEquals(false, interval37.isAfter(8));
    }

    public void testIsAfterNow() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(true, interval37.isAfterNow());
        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(false, interval37.isAfterNow());
        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(false, interval37.isAfterNow());
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(false, interval37.isAfterNow());
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(false, interval37.isAfterNow());
        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(false, interval37.isAfterNow());
    }

    public void testIsAfter_RI() {
        TestCase.assertEquals(true, interval37.isAfter(new Instant(2)));
        TestCase.assertEquals(false, interval37.isAfter(new Instant(3)));
        TestCase.assertEquals(false, interval37.isAfter(new Instant(4)));
        TestCase.assertEquals(false, interval37.isAfter(new Instant(5)));
        TestCase.assertEquals(false, interval37.isAfter(new Instant(6)));
        TestCase.assertEquals(false, interval37.isAfter(new Instant(7)));
        TestCase.assertEquals(false, interval37.isAfter(new Instant(8)));
    }

    public void testIsAfter_RI_null() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(true, interval37.isAfter(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(false, interval37.isAfter(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(false, interval37.isAfter(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(false, interval37.isAfter(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(false, interval37.isAfter(((ReadableInstant) (null))));
        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(false, interval37.isAfter(((ReadableInstant) (null))));
    }

    public void testIsAfter_RInterval() {
        TestCase.assertEquals(true, interval37.isAfter(new Interval(Long.MIN_VALUE, 2)));
        TestCase.assertEquals(true, interval37.isAfter(new Interval(Long.MIN_VALUE, 3)));
        TestCase.assertEquals(false, interval37.isAfter(new Interval(Long.MIN_VALUE, 4)));
        TestCase.assertEquals(false, interval37.isAfter(new Interval(6, Long.MAX_VALUE)));
        TestCase.assertEquals(false, interval37.isAfter(new Interval(7, Long.MAX_VALUE)));
        TestCase.assertEquals(false, interval37.isAfter(new Interval(8, Long.MAX_VALUE)));
    }

    public void testIsAfter_RInterval_null() {
        DateTimeUtils.setCurrentMillisFixed(2);
        TestCase.assertEquals(true, interval37.isAfter(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(3);
        TestCase.assertEquals(true, interval37.isAfter(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(4);
        TestCase.assertEquals(false, interval37.isAfter(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(6);
        TestCase.assertEquals(false, interval37.isAfter(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(7);
        TestCase.assertEquals(false, interval37.isAfter(((ReadableInterval) (null))));
        DateTimeUtils.setCurrentMillisFixed(8);
        TestCase.assertEquals(false, interval37.isAfter(((ReadableInterval) (null))));
    }

    // -----------------------------------------------------------------------
    public void testToInterval1() {
        Interval test = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval result = test.toInterval();
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToMutableInterval1() {
        Interval test = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        MutableInterval result = test.toMutableInterval();
        TestCase.assertEquals(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToPeriod() {
        DateTime dt1 = new DateTime(2004, 6, 9, 7, 8, 9, 10, TestInterval_Basics.COPTIC_PARIS);
        DateTime dt2 = new DateTime(2005, 8, 13, 12, 14, 16, 18, TestInterval_Basics.COPTIC_PARIS);
        Interval base = new Interval(dt1, dt2);
        Period test = base.toPeriod();
        Period expected = new Period(dt1, dt2, PeriodType.standard());
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testToPeriod_PeriodType1() {
        DateTime dt1 = new DateTime(2004, 6, 9, 7, 8, 9, 10, TestInterval_Basics.COPTIC_PARIS);
        DateTime dt2 = new DateTime(2005, 8, 13, 12, 14, 16, 18, TestInterval_Basics.COPTIC_PARIS);
        Interval base = new Interval(dt1, dt2);
        Period test = base.toPeriod(null);
        Period expected = new Period(dt1, dt2, PeriodType.standard());
        TestCase.assertEquals(expected, test);
    }

    public void testToPeriod_PeriodType2() {
        DateTime dt1 = new DateTime(2004, 6, 9, 7, 8, 9, 10);
        DateTime dt2 = new DateTime(2005, 8, 13, 12, 14, 16, 18);
        Interval base = new Interval(dt1, dt2);
        Period test = base.toPeriod(PeriodType.yearWeekDayTime());
        Period expected = new Period(dt1, dt2, PeriodType.yearWeekDayTime());
        TestCase.assertEquals(expected, test);
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Interval test = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Interval result = ((Interval) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        DateTime dt1 = new DateTime(2004, 6, 9, 7, 8, 9, 10, UTC);
        DateTime dt2 = new DateTime(2005, 8, 13, 12, 14, 16, 18, UTC);
        Interval test = new Interval(dt1, dt2);
        TestCase.assertEquals("2004-06-09T07:08:09.010Z/2005-08-13T12:14:16.018Z", test.toString());
    }

    public void testToString_reparse() {
        DateTime dt1 = new DateTime(2004, 6, 9, 7, 8, 9, 10, DateTimeZone.getDefault());
        DateTime dt2 = new DateTime(2005, 8, 13, 12, 14, 16, 18, DateTimeZone.getDefault());
        Interval test = new Interval(dt1, dt2);
        TestCase.assertEquals(test, new Interval(test.toString()));
    }

    // -----------------------------------------------------------------------
    public void testWithChronology1() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withChronology(BuddhistChronology.getInstance());
        TestCase.assertEquals(new Interval(TEST_TIME1, TEST_TIME2, BuddhistChronology.getInstance()), test);
    }

    public void testWithChronology2() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withChronology(null);
        TestCase.assertEquals(new Interval(TEST_TIME1, TEST_TIME2, ISOChronology.getInstance()), test);
    }

    public void testWithChronology3() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withChronology(TestInterval_Basics.COPTIC_PARIS);
        TestCase.assertSame(base, test);
    }

    // -----------------------------------------------------------------------
    public void testWithStartMillis_long1() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withStartMillis(((TEST_TIME1) - 1));
        TestCase.assertEquals(new Interval(((TEST_TIME1) - 1), TEST_TIME2, TestInterval_Basics.COPTIC_PARIS), test);
    }

    public void testWithStartMillis_long2() {
        Interval test = new Interval(TEST_TIME1, TEST_TIME2);
        try {
            test.withStartMillis(((TEST_TIME2) + 1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithStartMillis_long3() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withStartMillis(TEST_TIME1);
        TestCase.assertSame(base, test);
    }

    // -----------------------------------------------------------------------
    public void testWithStartInstant_RI1() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withStart(new Instant(((TEST_TIME1) - 1)));
        TestCase.assertEquals(new Interval(((TEST_TIME1) - 1), TEST_TIME2, TestInterval_Basics.COPTIC_PARIS), test);
    }

    public void testWithStartInstant_RI2() {
        Interval test = new Interval(TEST_TIME1, TEST_TIME2);
        try {
            test.withStart(new Instant(((TEST_TIME2) + 1)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithStartInstant_RI3() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withStart(null);
        TestCase.assertEquals(new Interval(TEST_TIME_NOW, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS), test);
    }

    // -----------------------------------------------------------------------
    public void testWithEndMillis_long1() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withEndMillis(((TEST_TIME2) - 1));
        TestCase.assertEquals(new Interval(TEST_TIME1, ((TEST_TIME2) - 1), TestInterval_Basics.COPTIC_PARIS), test);
    }

    public void testWithEndMillis_long2() {
        Interval test = new Interval(TEST_TIME1, TEST_TIME2);
        try {
            test.withEndMillis(((TEST_TIME1) - 1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithEndMillis_long3() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withEndMillis(TEST_TIME2);
        TestCase.assertSame(base, test);
    }

    // -----------------------------------------------------------------------
    public void testWithEndInstant_RI1() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withEnd(new Instant(((TEST_TIME2) - 1)));
        TestCase.assertEquals(new Interval(TEST_TIME1, ((TEST_TIME2) - 1), TestInterval_Basics.COPTIC_PARIS), test);
    }

    public void testWithEndInstant_RI2() {
        Interval test = new Interval(TEST_TIME1, TEST_TIME2);
        try {
            test.withEnd(new Instant(((TEST_TIME1) - 1)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithEndInstant_RI3() {
        Interval base = new Interval(TEST_TIME1, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withEnd(null);
        TestCase.assertEquals(new Interval(TEST_TIME1, TEST_TIME_NOW, TestInterval_Basics.COPTIC_PARIS), test);
    }

    // -----------------------------------------------------------------------
    public void testWithDurationAfterStart1() throws Throwable {
        Duration dur = new Duration(((TEST_TIME2) - (TEST_TIME_NOW)));
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME_NOW, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withDurationAfterStart(dur);
        TestCase.assertEquals(new Interval(TEST_TIME_NOW, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS), test);
    }

    public void testWithDurationAfterStart2() throws Throwable {
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withDurationAfterStart(null);
        TestCase.assertEquals(new Interval(TEST_TIME_NOW, TEST_TIME_NOW, TestInterval_Basics.COPTIC_PARIS), test);
    }

    public void testWithDurationAfterStart3() throws Throwable {
        Duration dur = new Duration((-1));
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME_NOW);
        try {
            base.withDurationAfterStart(dur);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithDurationAfterStart4() throws Throwable {
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withDurationAfterStart(base.toDuration());
        TestCase.assertSame(base, test);
    }

    // -----------------------------------------------------------------------
    public void testWithDurationBeforeEnd1() throws Throwable {
        Duration dur = new Duration(((TEST_TIME_NOW) - (TEST_TIME1)));
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME_NOW, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withDurationBeforeEnd(dur);
        TestCase.assertEquals(new Interval(TEST_TIME1, TEST_TIME_NOW, TestInterval_Basics.COPTIC_PARIS), test);
    }

    public void testWithDurationBeforeEnd2() throws Throwable {
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withDurationBeforeEnd(null);
        TestCase.assertEquals(new Interval(TEST_TIME2, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS), test);
    }

    public void testWithDurationBeforeEnd3() throws Throwable {
        Duration dur = new Duration((-1));
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME_NOW);
        try {
            base.withDurationBeforeEnd(dur);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testWithDurationBeforeEnd4() throws Throwable {
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withDurationBeforeEnd(base.toDuration());
        TestCase.assertSame(base, test);
    }

    // -----------------------------------------------------------------------
    public void testWithPeriodAfterStart1() throws Throwable {
        DateTime dt = new DateTime(TEST_TIME_NOW, TestInterval_Basics.COPTIC_PARIS);
        Period dur = new Period(0, 6, 0, 0, 1, 0, 0, 0);
        Interval base = new Interval(dt, dt);
        Interval test = base.withPeriodAfterStart(dur);
        TestCase.assertEquals(new Interval(dt, dur), test);
    }

    public void testWithPeriodAfterStart2() throws Throwable {
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withPeriodAfterStart(null);
        TestCase.assertEquals(new Interval(TEST_TIME_NOW, TEST_TIME_NOW, TestInterval_Basics.COPTIC_PARIS), test);
    }

    public void testWithPeriodAfterStart3() throws Throwable {
        Period per = new Period(0, 0, 0, 0, 0, 0, 0, (-1));
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME_NOW);
        try {
            base.withPeriodAfterStart(per);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithPeriodBeforeEnd1() throws Throwable {
        DateTime dt = new DateTime(TEST_TIME_NOW, TestInterval_Basics.COPTIC_PARIS);
        Period dur = new Period(0, 6, 0, 0, 1, 0, 0, 0);
        Interval base = new Interval(dt, dt);
        Interval test = base.withPeriodBeforeEnd(dur);
        TestCase.assertEquals(new Interval(dur, dt), test);
    }

    public void testWithPeriodBeforeEnd2() throws Throwable {
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS);
        Interval test = base.withPeriodBeforeEnd(null);
        TestCase.assertEquals(new Interval(TEST_TIME2, TEST_TIME2, TestInterval_Basics.COPTIC_PARIS), test);
    }

    public void testWithPeriodBeforeEnd3() throws Throwable {
        Period per = new Period(0, 0, 0, 0, 0, 0, 0, (-1));
        Interval base = new Interval(TEST_TIME_NOW, TEST_TIME_NOW);
        try {
            base.withPeriodBeforeEnd(per);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }
}

