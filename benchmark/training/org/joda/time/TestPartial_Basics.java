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


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Locale;
import junit.framework.TestCase;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;


/**
 * This class is a Junit unit test for Partial.
 *
 * @author Stephen Colebourne
 */
public class TestPartial_Basics extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final Chronology COPTIC_PARIS = CopticChronology.getInstance(TestPartial_Basics.PARIS);

    private static final Chronology COPTIC_TOKYO = CopticChronology.getInstance(TestPartial_Basics.TOKYO);

    private static final Chronology COPTIC_UTC = CopticChronology.getInstanceUTC();

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final Chronology BUDDHIST_LONDON = BuddhistChronology.getInstance(TestPartial_Basics.LONDON);

    private static final Chronology BUDDHIST_TOKYO = BuddhistChronology.getInstance(TestPartial_Basics.TOKYO);

    private static final Chronology BUDDHIST_UTC = BuddhistChronology.getInstanceUTC();

    private long TEST_TIME_NOW = (((10L * (MILLIS_PER_HOUR)) + (20L * (MILLIS_PER_MINUTE))) + (30L * (MILLIS_PER_SECOND))) + 40L;

    private long TEST_TIME2 = ((((1L * (MILLIS_PER_DAY)) + (5L * (MILLIS_PER_HOUR))) + (6L * (MILLIS_PER_MINUTE))) + (7L * (MILLIS_PER_SECOND))) + 8L;

    private DateTimeZone zone = null;

    public TestPartial_Basics(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testGet() {
        Partial test = createHourMinPartial();
        TestCase.assertEquals(10, test.get(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(20, test.get(DateTimeFieldType.minuteOfHour()));
        try {
            test.get(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.get(DateTimeFieldType.secondOfMinute());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testSize() {
        Partial test = createHourMinPartial();
        TestCase.assertEquals(2, test.size());
    }

    public void testGetFieldType() {
        Partial test = createHourMinPartial();
        TestCase.assertSame(DateTimeFieldType.hourOfDay(), test.getFieldType(0));
        TestCase.assertSame(DateTimeFieldType.minuteOfHour(), test.getFieldType(1));
        try {
            test.getFieldType((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getFieldType(2);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetFieldTypes() {
        Partial test = createHourMinPartial();
        DateTimeFieldType[] fields = test.getFieldTypes();
        TestCase.assertEquals(2, fields.length);
        TestCase.assertSame(DateTimeFieldType.hourOfDay(), fields[0]);
        TestCase.assertSame(DateTimeFieldType.minuteOfHour(), fields[1]);
        TestCase.assertNotSame(test.getFieldTypes(), test.getFieldTypes());
    }

    public void testGetField() {
        Partial test = createHourMinPartial(TestPartial_Basics.COPTIC_PARIS);
        TestCase.assertSame(CopticChronology.getInstanceUTC().hourOfDay(), test.getField(0));
        TestCase.assertSame(CopticChronology.getInstanceUTC().minuteOfHour(), test.getField(1));
        try {
            test.getField((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getField(5);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetFields() {
        Partial test = createHourMinPartial(TestPartial_Basics.COPTIC_PARIS);
        DateTimeField[] fields = test.getFields();
        TestCase.assertEquals(2, fields.length);
        TestCase.assertSame(CopticChronology.getInstanceUTC().hourOfDay(), fields[0]);
        TestCase.assertSame(CopticChronology.getInstanceUTC().minuteOfHour(), fields[1]);
        TestCase.assertNotSame(test.getFields(), test.getFields());
    }

    public void testGetValue() {
        Partial test = createHourMinPartial(TestPartial_Basics.COPTIC_PARIS);
        TestCase.assertEquals(10, test.getValue(0));
        TestCase.assertEquals(20, test.getValue(1));
        try {
            test.getValue((-1));
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            test.getValue(2);
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetValues() {
        Partial test = createHourMinPartial(TestPartial_Basics.COPTIC_PARIS);
        int[] values = test.getValues();
        TestCase.assertEquals(2, values.length);
        TestCase.assertEquals(10, values[0]);
        TestCase.assertEquals(20, values[1]);
        TestCase.assertNotSame(test.getValues(), test.getValues());
    }

    public void testIsSupported() {
        Partial test = createHourMinPartial(TestPartial_Basics.COPTIC_PARIS);
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(false, test.isSupported(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(false, test.isSupported(DateTimeFieldType.millisOfSecond()));
        TestCase.assertEquals(false, test.isSupported(DateTimeFieldType.dayOfMonth()));
    }

    // -----------------------------------------------------------------------
    public void testIsEqual_TOD() {
        Partial test1 = createHourMinPartial();
        Partial test1a = createHourMinPartial();
        TestCase.assertEquals(true, test1.isEqual(test1a));
        TestCase.assertEquals(true, test1a.isEqual(test1));
        TestCase.assertEquals(true, test1.isEqual(test1));
        TestCase.assertEquals(true, test1a.isEqual(test1a));
        Partial test2 = createHourMinPartial2(TestPartial_Basics.ISO_UTC);
        TestCase.assertEquals(false, test1.isEqual(test2));
        TestCase.assertEquals(false, test2.isEqual(test1));
        Partial test3 = createHourMinPartial2(TestPartial_Basics.COPTIC_UTC);
        TestCase.assertEquals(false, test1.isEqual(test3));
        TestCase.assertEquals(false, test3.isEqual(test1));
        TestCase.assertEquals(true, test3.isEqual(test2));
        try {
            createHourMinPartial().isEqual(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsBefore_TOD() {
        Partial test1 = createHourMinPartial();
        Partial test1a = createHourMinPartial();
        TestCase.assertEquals(false, test1.isBefore(test1a));
        TestCase.assertEquals(false, test1a.isBefore(test1));
        TestCase.assertEquals(false, test1.isBefore(test1));
        TestCase.assertEquals(false, test1a.isBefore(test1a));
        Partial test2 = createHourMinPartial2(TestPartial_Basics.ISO_UTC);
        TestCase.assertEquals(true, test1.isBefore(test2));
        TestCase.assertEquals(false, test2.isBefore(test1));
        Partial test3 = createHourMinPartial2(TestPartial_Basics.COPTIC_UTC);
        TestCase.assertEquals(true, test1.isBefore(test3));
        TestCase.assertEquals(false, test3.isBefore(test1));
        TestCase.assertEquals(false, test3.isBefore(test2));
        try {
            createHourMinPartial().isBefore(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testIsAfter_TOD() {
        Partial test1 = createHourMinPartial();
        Partial test1a = createHourMinPartial();
        TestCase.assertEquals(false, test1.isAfter(test1a));
        TestCase.assertEquals(false, test1a.isAfter(test1));
        TestCase.assertEquals(false, test1.isAfter(test1));
        TestCase.assertEquals(false, test1a.isAfter(test1a));
        Partial test2 = createHourMinPartial2(TestPartial_Basics.ISO_UTC);
        TestCase.assertEquals(false, test1.isAfter(test2));
        TestCase.assertEquals(true, test2.isAfter(test1));
        Partial test3 = createHourMinPartial2(TestPartial_Basics.COPTIC_UTC);
        TestCase.assertEquals(false, test1.isAfter(test3));
        TestCase.assertEquals(true, test3.isAfter(test1));
        TestCase.assertEquals(false, test3.isAfter(test2));
        try {
            createHourMinPartial().isAfter(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testWithChronologyRetainFields_Chrono() {
        Partial base = createHourMinPartial(TestPartial_Basics.COPTIC_PARIS);
        Partial test = base.withChronologyRetainFields(TestPartial_Basics.BUDDHIST_TOKYO);
        check(base, 10, 20);
        TestCase.assertEquals(TestPartial_Basics.COPTIC_UTC, base.getChronology());
        check(test, 10, 20);
        TestCase.assertEquals(TestPartial_Basics.BUDDHIST_UTC, test.getChronology());
    }

    public void testWithChronologyRetainFields_sameChrono() {
        Partial base = createHourMinPartial(TestPartial_Basics.COPTIC_PARIS);
        Partial test = base.withChronologyRetainFields(TestPartial_Basics.COPTIC_TOKYO);
        TestCase.assertSame(base, test);
    }

    public void testWithChronologyRetainFields_nullChrono() {
        Partial base = createHourMinPartial(TestPartial_Basics.COPTIC_PARIS);
        Partial test = base.withChronologyRetainFields(null);
        check(base, 10, 20);
        TestCase.assertEquals(TestPartial_Basics.COPTIC_UTC, base.getChronology());
        check(test, 10, 20);
        TestCase.assertEquals(TestPartial_Basics.ISO_UTC, test.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testWith1() {
        Partial test = createHourMinPartial();
        Partial result = test.with(DateTimeFieldType.hourOfDay(), 15);
        check(test, 10, 20);
        check(result, 15, 20);
    }

    public void testWith2() {
        Partial test = createHourMinPartial();
        try {
            test.with(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20);
    }

    public void testWith3() {
        Partial test = createHourMinPartial();
        try {
            test.with(DateTimeFieldType.clockhourOfDay(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20);
    }

    public void testWith3a() {
        Partial test = createHourMinPartial();
        Partial result = test.with(DateTimeFieldType.secondOfMinute(), 15);
        check(test, 10, 20);
        TestCase.assertEquals(3, result.size());
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.secondOfMinute()));
        TestCase.assertEquals(DateTimeFieldType.hourOfDay(), result.getFieldType(0));
        TestCase.assertEquals(DateTimeFieldType.minuteOfHour(), result.getFieldType(1));
        TestCase.assertEquals(DateTimeFieldType.secondOfMinute(), result.getFieldType(2));
        TestCase.assertEquals(10, result.get(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(20, result.get(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(15, result.get(DateTimeFieldType.secondOfMinute()));
    }

    public void testWith3b() {
        Partial test = createHourMinPartial();
        Partial result = test.with(DateTimeFieldType.minuteOfDay(), 15);
        check(test, 10, 20);
        TestCase.assertEquals(3, result.size());
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.minuteOfDay()));
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(DateTimeFieldType.hourOfDay(), result.getFieldType(0));
        TestCase.assertEquals(DateTimeFieldType.minuteOfDay(), result.getFieldType(1));
        TestCase.assertEquals(DateTimeFieldType.minuteOfHour(), result.getFieldType(2));
        TestCase.assertEquals(10, result.get(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(20, result.get(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(15, result.get(DateTimeFieldType.minuteOfDay()));
    }

    public void testWith3c() {
        Partial test = createHourMinPartial();
        Partial result = test.with(DateTimeFieldType.dayOfMonth(), 15);
        check(test, 10, 20);
        TestCase.assertEquals(3, result.size());
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.dayOfMonth()));
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(DateTimeFieldType.dayOfMonth(), result.getFieldType(0));
        TestCase.assertEquals(DateTimeFieldType.hourOfDay(), result.getFieldType(1));
        TestCase.assertEquals(DateTimeFieldType.minuteOfHour(), result.getFieldType(2));
        TestCase.assertEquals(10, result.get(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(20, result.get(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(15, result.get(DateTimeFieldType.dayOfMonth()));
    }

    public void testWith3d() {
        Partial test = new Partial(DateTimeFieldType.year(), 2005);
        Partial result = test.with(DateTimeFieldType.monthOfYear(), 6);
        TestCase.assertEquals(2, result.size());
        TestCase.assertEquals(2005, result.get(DateTimeFieldType.year()));
        TestCase.assertEquals(6, result.get(DateTimeFieldType.monthOfYear()));
    }

    public void testWith3e() {
        Partial test = new Partial(DateTimeFieldType.era(), 1);
        Partial result = test.with(DateTimeFieldType.halfdayOfDay(), 0);
        TestCase.assertEquals(2, result.size());
        TestCase.assertEquals(1, result.get(DateTimeFieldType.era()));
        TestCase.assertEquals(0, result.get(DateTimeFieldType.halfdayOfDay()));
        TestCase.assertEquals(0, result.indexOf(DateTimeFieldType.era()));
        TestCase.assertEquals(1, result.indexOf(DateTimeFieldType.halfdayOfDay()));
    }

    public void testWith3f() {
        Partial test = new Partial(DateTimeFieldType.halfdayOfDay(), 0);
        Partial result = test.with(DateTimeFieldType.era(), 1);
        TestCase.assertEquals(2, result.size());
        TestCase.assertEquals(1, result.get(DateTimeFieldType.era()));
        TestCase.assertEquals(0, result.get(DateTimeFieldType.halfdayOfDay()));
        TestCase.assertEquals(0, result.indexOf(DateTimeFieldType.era()));
        TestCase.assertEquals(1, result.indexOf(DateTimeFieldType.halfdayOfDay()));
    }

    public void testWith4() {
        Partial test = createHourMinPartial();
        Partial result = test.with(DateTimeFieldType.hourOfDay(), 10);
        TestCase.assertSame(test, result);
    }

    public void testWith_baseHasNoRange() {
        Partial test = new Partial(DateTimeFieldType.year(), 1);
        Partial result = test.with(DateTimeFieldType.hourOfDay(), 10);
        TestCase.assertEquals(2, result.size());
        TestCase.assertEquals(0, result.indexOf(DateTimeFieldType.year()));
        TestCase.assertEquals(1, result.indexOf(DateTimeFieldType.hourOfDay()));
    }

    public void testWith_argHasNoRange() {
        Partial test = new Partial(DateTimeFieldType.hourOfDay(), 1);
        Partial result = test.with(DateTimeFieldType.year(), 10);
        TestCase.assertEquals(2, result.size());
        TestCase.assertEquals(0, result.indexOf(DateTimeFieldType.year()));
        TestCase.assertEquals(1, result.indexOf(DateTimeFieldType.hourOfDay()));
    }

    public void testWith_baseAndArgHaveNoRange() {
        Partial test = new Partial(DateTimeFieldType.year(), 1);
        Partial result = test.with(DateTimeFieldType.era(), 1);
        TestCase.assertEquals(2, result.size());
        TestCase.assertEquals(0, result.indexOf(DateTimeFieldType.era()));
        TestCase.assertEquals(1, result.indexOf(DateTimeFieldType.year()));
    }

    // -----------------------------------------------------------------------
    public void testWithout1() {
        Partial test = createHourMinPartial();
        Partial result = test.without(DateTimeFieldType.year());
        check(test, 10, 20);
        check(result, 10, 20);
    }

    public void testWithout2() {
        Partial test = createHourMinPartial();
        Partial result = test.without(((DateTimeFieldType) (null)));
        check(test, 10, 20);
        check(result, 10, 20);
    }

    public void testWithout3() {
        Partial test = createHourMinPartial();
        Partial result = test.without(DateTimeFieldType.hourOfDay());
        check(test, 10, 20);
        TestCase.assertEquals(1, result.size());
        TestCase.assertEquals(false, result.isSupported(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(DateTimeFieldType.minuteOfHour(), result.getFieldType(0));
    }

    public void testWithout4() {
        Partial test = createHourMinPartial();
        Partial result = test.without(DateTimeFieldType.minuteOfHour());
        check(test, 10, 20);
        TestCase.assertEquals(1, result.size());
        TestCase.assertEquals(true, result.isSupported(DateTimeFieldType.hourOfDay()));
        TestCase.assertEquals(false, result.isSupported(DateTimeFieldType.minuteOfHour()));
        TestCase.assertEquals(DateTimeFieldType.hourOfDay(), result.getFieldType(0));
    }

    public void testWithout5() {
        Partial test = new Partial(DateTimeFieldType.hourOfDay(), 12);
        Partial result = test.without(DateTimeFieldType.hourOfDay());
        TestCase.assertEquals(0, result.size());
        TestCase.assertEquals(false, result.isSupported(DateTimeFieldType.hourOfDay()));
    }

    // -----------------------------------------------------------------------
    public void testWithField1() {
        Partial test = createHourMinPartial();
        Partial result = test.withField(DateTimeFieldType.hourOfDay(), 15);
        check(test, 10, 20);
        check(result, 15, 20);
    }

    public void testWithField2() {
        Partial test = createHourMinPartial();
        try {
            test.withField(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20);
    }

    public void testWithField3() {
        Partial test = createHourMinPartial();
        try {
            test.withField(DateTimeFieldType.dayOfMonth(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20);
    }

    public void testWithField4() {
        Partial test = createHourMinPartial();
        Partial result = test.withField(DateTimeFieldType.hourOfDay(), 10);
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testWithFieldAdded1() {
        Partial test = createHourMinPartial();
        Partial result = test.withFieldAdded(DurationFieldType.hours(), 6);
        TestCase.assertEquals(createHourMinPartial(), test);
        check(test, 10, 20);
        check(result, 16, 20);
    }

    public void testWithFieldAdded2() {
        Partial test = createHourMinPartial();
        try {
            test.withFieldAdded(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20);
    }

    public void testWithFieldAdded3() {
        Partial test = createHourMinPartial();
        try {
            test.withFieldAdded(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20);
    }

    public void testWithFieldAdded4() {
        Partial test = createHourMinPartial();
        Partial result = test.withFieldAdded(DurationFieldType.hours(), 0);
        TestCase.assertSame(test, result);
    }

    public void testWithFieldAdded5() {
        Partial test = createHourMinPartial();
        try {
            test.withFieldAdded(DurationFieldType.days(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20);
    }

    public void testWithFieldAdded6() {
        Partial test = createHourMinPartial();
        try {
            test.withFieldAdded(DurationFieldType.hours(), 16);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        check(test, 10, 20);
    }

    public void testWithFieldAdded7() {
        Partial test = createHourMinPartial(23, 59, TestPartial_Basics.ISO_UTC);
        try {
            test.withFieldAdded(DurationFieldType.minutes(), 1);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        check(test, 23, 59);
        test = createHourMinPartial(23, 59, TestPartial_Basics.ISO_UTC);
        try {
            test.withFieldAdded(DurationFieldType.hours(), 1);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        check(test, 23, 59);
    }

    public void testWithFieldAdded8() {
        Partial test = createHourMinPartial(0, 0, TestPartial_Basics.ISO_UTC);
        try {
            test.withFieldAdded(DurationFieldType.minutes(), (-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        check(test, 0, 0);
        test = createHourMinPartial(0, 0, TestPartial_Basics.ISO_UTC);
        try {
            test.withFieldAdded(DurationFieldType.hours(), (-1));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
        check(test, 0, 0);
    }

    // -----------------------------------------------------------------------
    public void testWithFieldAddWrapped1() {
        Partial test = createHourMinPartial();
        Partial result = test.withFieldAddWrapped(DurationFieldType.hours(), 6);
        TestCase.assertEquals(createHourMinPartial(), test);
        check(test, 10, 20);
        check(result, 16, 20);
    }

    public void testWithFieldAddWrapped2() {
        Partial test = createHourMinPartial();
        try {
            test.withFieldAddWrapped(null, 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20);
    }

    public void testWithFieldAddWrapped3() {
        Partial test = createHourMinPartial();
        try {
            test.withFieldAddWrapped(null, 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20);
    }

    public void testWithFieldAddWrapped4() {
        Partial test = createHourMinPartial();
        Partial result = test.withFieldAddWrapped(DurationFieldType.hours(), 0);
        TestCase.assertSame(test, result);
    }

    public void testWithFieldAddWrapped5() {
        Partial test = createHourMinPartial();
        try {
            test.withFieldAddWrapped(DurationFieldType.days(), 6);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        check(test, 10, 20);
    }

    public void testWithFieldAddWrapped6() {
        Partial test = createHourMinPartial();
        Partial result = test.withFieldAddWrapped(DurationFieldType.hours(), 16);
        TestCase.assertEquals(createHourMinPartial(), test);
        check(test, 10, 20);
        check(result, 2, 20);
    }

    public void testWithFieldAddWrapped7() {
        Partial test = createHourMinPartial(23, 59, TestPartial_Basics.ISO_UTC);
        Partial result = test.withFieldAddWrapped(DurationFieldType.minutes(), 1);
        check(test, 23, 59);
        check(result, 0, 0);
        test = createHourMinPartial(23, 59, TestPartial_Basics.ISO_UTC);
        result = test.withFieldAddWrapped(DurationFieldType.hours(), 1);
        check(test, 23, 59);
        check(result, 0, 59);
    }

    public void testWithFieldAddWrapped8() {
        Partial test = createHourMinPartial(0, 0, TestPartial_Basics.ISO_UTC);
        Partial result = test.withFieldAddWrapped(DurationFieldType.minutes(), (-1));
        check(test, 0, 0);
        check(result, 23, 59);
        test = createHourMinPartial(0, 0, TestPartial_Basics.ISO_UTC);
        result = test.withFieldAddWrapped(DurationFieldType.hours(), (-1));
        check(test, 0, 0);
        check(result, 23, 0);
    }

    // -----------------------------------------------------------------------
    public void testPlus_RP() {
        Partial test = createHourMinPartial(TestPartial_Basics.BUDDHIST_LONDON);
        Partial result = test.plus(new Period(1, 2, 3, 4, 5, 6, 7, 8));
        check(test, 10, 20);
        check(result, 15, 26);
        result = test.plus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testMinus_RP() {
        Partial test = createHourMinPartial(TestPartial_Basics.BUDDHIST_LONDON);
        Partial result = test.minus(new Period(1, 1, 1, 1, 1, 1, 1, 1));
        check(test, 10, 20);
        check(result, 9, 19);
        result = test.minus(((ReadablePeriod) (null)));
        TestCase.assertSame(test, result);
    }

    // -----------------------------------------------------------------------
    public void testToDateTime_RI() {
        Partial base = createHourMinPartial(TestPartial_Basics.COPTIC_PARIS);
        DateTime dt = new DateTime(0L);// LONDON zone

        TestCase.assertEquals("1970-01-01T01:00:00.000+01:00", dt.toString());
        DateTime test = base.toDateTime(dt);
        check(base, 10, 20);
        TestCase.assertEquals("1970-01-01T01:00:00.000+01:00", dt.toString());
        TestCase.assertEquals("1970-01-01T10:20:00.000+01:00", test.toString());
    }

    public void testToDateTime_nullRI() {
        Partial base = createHourMinPartial(1, 2, TestPartial_Basics.ISO_UTC);
        DateTimeUtils.setCurrentMillisFixed(TEST_TIME2);
        DateTime test = base.toDateTime(((ReadableInstant) (null)));
        check(base, 1, 2);
        TestCase.assertEquals("1970-01-02T01:02:07.008+01:00", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testProperty() {
        Partial test = createHourMinPartial();
        TestCase.assertNotNull(test.property(DateTimeFieldType.hourOfDay()));
        TestCase.assertNotNull(test.property(DateTimeFieldType.minuteOfHour()));
        try {
            test.property(DateTimeFieldType.secondOfDay());
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            test.property(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        Partial test = createHourMinPartial(TestPartial_Basics.COPTIC_PARIS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Partial result = ((Partial) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
        TestCase.assertTrue(Arrays.equals(test.getValues(), result.getValues()));
        TestCase.assertTrue(Arrays.equals(test.getFields(), result.getFields()));
        TestCase.assertEquals(test.getChronology(), result.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testGetFormatter1() {
        Partial test = new Partial(DateTimeFieldType.year(), 2005);
        TestCase.assertEquals("2005", test.getFormatter().print(test));
        test = test.with(DateTimeFieldType.monthOfYear(), 6);
        TestCase.assertEquals("2005-06", test.getFormatter().print(test));
        test = test.with(DateTimeFieldType.dayOfMonth(), 25);
        TestCase.assertEquals("2005-06-25", test.getFormatter().print(test));
        test = test.without(DateTimeFieldType.monthOfYear());
        TestCase.assertEquals("2005--25", test.getFormatter().print(test));
    }

    public void testGetFormatter2() {
        Partial test = new Partial();
        TestCase.assertEquals(null, test.getFormatter());
        test = test.with(DateTimeFieldType.era(), 1);
        TestCase.assertEquals(null, test.getFormatter());
        test = test.with(DateTimeFieldType.halfdayOfDay(), 0);
        TestCase.assertEquals(null, test.getFormatter());
    }

    public void testGetFormatter3() {
        Partial test = new Partial(DateTimeFieldType.dayOfWeek(), 5);
        TestCase.assertEquals("-W-5", test.getFormatter().print(test));
        // contrast with testToString5
        test = test.with(DateTimeFieldType.dayOfMonth(), 13);
        TestCase.assertEquals("---13", test.getFormatter().print(test));
    }

    // -----------------------------------------------------------------------
    public void testToString1() {
        Partial test = createHourMinPartial();
        TestCase.assertEquals("10:20", test.toString());
    }

    public void testToString2() {
        Partial test = new Partial();
        TestCase.assertEquals("[]", test.toString());
    }

    public void testToString3() {
        Partial test = new Partial(DateTimeFieldType.year(), 2005);
        TestCase.assertEquals("2005", test.toString());
        test = test.with(DateTimeFieldType.monthOfYear(), 6);
        TestCase.assertEquals("2005-06", test.toString());
        test = test.with(DateTimeFieldType.dayOfMonth(), 25);
        TestCase.assertEquals("2005-06-25", test.toString());
        test = test.without(DateTimeFieldType.monthOfYear());
        TestCase.assertEquals("2005--25", test.toString());
    }

    public void testToString4() {
        Partial test = new Partial(DateTimeFieldType.dayOfWeek(), 5);
        TestCase.assertEquals("-W-5", test.toString());
        test = test.with(DateTimeFieldType.dayOfMonth(), 13);
        TestCase.assertEquals("[dayOfMonth=13, dayOfWeek=5]", test.toString());
    }

    public void testToString5() {
        Partial test = new Partial(DateTimeFieldType.era(), 1);
        TestCase.assertEquals("[era=1]", test.toString());
        test = test.with(DateTimeFieldType.halfdayOfDay(), 0);
        TestCase.assertEquals("[era=1, halfdayOfDay=0]", test.toString());
    }

    // -----------------------------------------------------------------------
    public void testToString_String() {
        Partial test = createHourMinPartial();
        TestCase.assertEquals("\ufffd\ufffd\ufffd\ufffd 10", test.toString("yyyy HH"));
        TestCase.assertEquals("10:20", test.toString(((String) (null))));
    }

    // -----------------------------------------------------------------------
    public void testToString_String_Locale() {
        Partial test = createHourMinPartial();
        TestCase.assertEquals("10 20", test.toString("H m", Locale.ENGLISH));
        TestCase.assertEquals("10:20", test.toString(null, Locale.ENGLISH));
        TestCase.assertEquals("10 20", test.toString("H m", null));
        TestCase.assertEquals("10:20", test.toString(null, null));
    }

    // -----------------------------------------------------------------------
    public void testToString_DTFormatter() {
        Partial test = createHourMinPartial();
        TestCase.assertEquals("\ufffd\ufffd\ufffd\ufffd 10", test.toString(DateTimeFormat.forPattern("yyyy HH")));
        TestCase.assertEquals("10:20", test.toString(((DateTimeFormatter) (null))));
    }
}

