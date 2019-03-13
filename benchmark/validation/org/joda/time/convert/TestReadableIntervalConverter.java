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
package org.joda.time.convert;


import ReadableIntervalConverter.INSTANCE;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.MutableInterval;
import org.joda.time.MutablePeriod;
import org.joda.time.PeriodType;
import org.joda.time.ReadableInterval;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.ISOChronology;


/**
 * This class is a JUnit test for ReadableIntervalConverter.
 *
 * @author Stephen Colebourne
 */
public class TestReadableIntervalConverter extends TestCase {
    private static final DateTimeZone UTC = DateTimeZone.UTC;

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology ISO_PARIS = ISOChronology.getInstance(TestReadableIntervalConverter.PARIS);

    private static Chronology JULIAN;

    private static Chronology ISO;

    private DateTimeZone zone = null;

    public TestReadableIntervalConverter(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSingleton() throws Exception {
        Class cls = ReadableIntervalConverter.class;
        TestCase.assertEquals(false, Modifier.isPublic(cls.getModifiers()));
        TestCase.assertEquals(false, Modifier.isProtected(cls.getModifiers()));
        TestCase.assertEquals(false, Modifier.isPrivate(cls.getModifiers()));
        Constructor con = cls.getDeclaredConstructor(((Class[]) (null)));
        TestCase.assertEquals(1, cls.getDeclaredConstructors().length);
        TestCase.assertEquals(true, Modifier.isProtected(con.getModifiers()));
        Field fld = cls.getDeclaredField("INSTANCE");
        TestCase.assertEquals(false, Modifier.isPublic(fld.getModifiers()));
        TestCase.assertEquals(false, Modifier.isProtected(fld.getModifiers()));
        TestCase.assertEquals(false, Modifier.isPrivate(fld.getModifiers()));
    }

    // -----------------------------------------------------------------------
    public void testSupportedType() throws Exception {
        TestCase.assertEquals(ReadableInterval.class, INSTANCE.getSupportedType());
    }

    // -----------------------------------------------------------------------
    public void testGetDurationMillis_Object() throws Exception {
        Interval i = new Interval(100L, 223L);
        TestCase.assertEquals(123L, INSTANCE.getDurationMillis(i));
    }

    // -----------------------------------------------------------------------
    public void testGetPeriodType_Object() throws Exception {
        Interval i = new Interval(100L, 223L);
        TestCase.assertEquals(PeriodType.standard(), INSTANCE.getPeriodType(i));
    }

    public void testSetIntoPeriod_Object1() throws Exception {
        Interval i = new Interval(100L, 223L);
        MutablePeriod m = new MutablePeriod(PeriodType.millis());
        INSTANCE.setInto(m, i, null);
        TestCase.assertEquals(0, m.getYears());
        TestCase.assertEquals(0, m.getMonths());
        TestCase.assertEquals(0, m.getWeeks());
        TestCase.assertEquals(0, m.getDays());
        TestCase.assertEquals(0, m.getHours());
        TestCase.assertEquals(0, m.getMinutes());
        TestCase.assertEquals(0, m.getSeconds());
        TestCase.assertEquals(123, m.getMillis());
    }

    public void testSetIntoPeriod_Object2() throws Exception {
        Interval i = new Interval(100L, 223L);
        MutablePeriod m = new MutablePeriod(PeriodType.millis());
        INSTANCE.setInto(m, i, CopticChronology.getInstance());
        TestCase.assertEquals(0, m.getYears());
        TestCase.assertEquals(0, m.getMonths());
        TestCase.assertEquals(0, m.getWeeks());
        TestCase.assertEquals(0, m.getDays());
        TestCase.assertEquals(0, m.getHours());
        TestCase.assertEquals(0, m.getMinutes());
        TestCase.assertEquals(0, m.getSeconds());
        TestCase.assertEquals(123, m.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testIsReadableInterval_Object_Chronology() throws Exception {
        Interval i = new Interval(1234L, 5678L);
        TestCase.assertEquals(true, INSTANCE.isReadableInterval(i, null));
    }

    public void testSetIntoInterval_Object1() throws Exception {
        Interval i = new Interval(0L, 123L, CopticChronology.getInstance());
        MutableInterval m = new MutableInterval((-1000L), 1000L, BuddhistChronology.getInstance());
        INSTANCE.setInto(m, i, null);
        TestCase.assertEquals(0L, m.getStartMillis());
        TestCase.assertEquals(123L, m.getEndMillis());
        TestCase.assertEquals(CopticChronology.getInstance(), m.getChronology());
    }

    public void testSetIntoInterval_Object2() throws Exception {
        Interval i = new Interval(0L, 123L, CopticChronology.getInstance());
        MutableInterval m = new MutableInterval((-1000L), 1000L, BuddhistChronology.getInstance());
        INSTANCE.setInto(m, i, GJChronology.getInstance());
        TestCase.assertEquals(0L, m.getStartMillis());
        TestCase.assertEquals(123L, m.getEndMillis());
        TestCase.assertEquals(GJChronology.getInstance(), m.getChronology());
    }

    public void testSetIntoInterval_Object3() throws Exception {
        MutableInterval i = new MutableInterval(0L, 123L) {
            public Chronology getChronology() {
                return null;// bad

            }
        };
        MutableInterval m = new MutableInterval((-1000L), 1000L, BuddhistChronology.getInstance());
        INSTANCE.setInto(m, i, GJChronology.getInstance());
        TestCase.assertEquals(0L, m.getStartMillis());
        TestCase.assertEquals(123L, m.getEndMillis());
        TestCase.assertEquals(GJChronology.getInstance(), m.getChronology());
    }

    public void testSetIntoInterval_Object4() throws Exception {
        MutableInterval i = new MutableInterval(0L, 123L) {
            public Chronology getChronology() {
                return null;// bad

            }
        };
        MutableInterval m = new MutableInterval((-1000L), 1000L, BuddhistChronology.getInstance());
        INSTANCE.setInto(m, i, null);
        TestCase.assertEquals(0L, m.getStartMillis());
        TestCase.assertEquals(123L, m.getEndMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), m.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TestCase.assertEquals("Converter[org.joda.time.ReadableInterval]", INSTANCE.toString());
    }
}

