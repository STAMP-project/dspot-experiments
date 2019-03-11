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


import NullConverter.INSTANCE;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.MutableInterval;
import org.joda.time.MutablePeriod;
import org.joda.time.PeriodType;
import org.joda.time.TimeOfDay;
import org.joda.time.chrono.CopticChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.ISOChronology;


/**
 * This class is a Junit unit test for NullConverter.
 *
 * @author Stephen Colebourne
 */
public class TestNullConverter extends TestCase {
    private long TEST_TIME_NOW = ((((20 * (DateTimeConstants.MILLIS_PER_DAY)) + (10L * (DateTimeConstants.MILLIS_PER_HOUR))) + (20L * (DateTimeConstants.MILLIS_PER_MINUTE))) + (30L * (DateTimeConstants.MILLIS_PER_SECOND))) + 40L;

    private static final DateTimeZone UTC = DateTimeZone.UTC;

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology ISO_PARIS = ISOChronology.getInstance(TestNullConverter.PARIS);

    private static Chronology ISO;

    private static Chronology JULIAN;

    private DateTimeZone zone = null;

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestNullConverter(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSingleton() throws Exception {
        Class cls = NullConverter.class;
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
        TestCase.assertEquals(null, INSTANCE.getSupportedType());
    }

    // -----------------------------------------------------------------------
    public void testGetInstantMillis_Object_Chronology() throws Exception {
        TestCase.assertEquals(TEST_TIME_NOW, INSTANCE.getInstantMillis(null, TestNullConverter.JULIAN));
        TestCase.assertEquals(TEST_TIME_NOW, INSTANCE.getInstantMillis(null, ((Chronology) (null))));
    }

    // -----------------------------------------------------------------------
    public void testGetChronology_Object_Zone() throws Exception {
        TestCase.assertEquals(TestNullConverter.ISO_PARIS, INSTANCE.getChronology(null, TestNullConverter.PARIS));
        TestCase.assertEquals(TestNullConverter.ISO, INSTANCE.getChronology(null, ((DateTimeZone) (null))));
    }

    public void testGetChronology_Object_Chronology() throws Exception {
        TestCase.assertEquals(TestNullConverter.JULIAN, INSTANCE.getChronology(null, TestNullConverter.JULIAN));
        TestCase.assertEquals(TestNullConverter.ISO, INSTANCE.getChronology(null, ((Chronology) (null))));
    }

    // -----------------------------------------------------------------------
    public void testGetPartialValues() throws Exception {
        TimeOfDay tod = new TimeOfDay();
        int[] expected = new int[]{ 10 + 1, 20, 30, 40 };// now

        int[] actual = INSTANCE.getPartialValues(tod, null, ISOChronology.getInstance());
        TestCase.assertEquals(true, Arrays.equals(expected, actual));
    }

    // -----------------------------------------------------------------------
    public void testGetDurationMillis_Object() throws Exception {
        TestCase.assertEquals(0L, INSTANCE.getDurationMillis(null));
    }

    // -----------------------------------------------------------------------
    public void testGetPeriodType_Object() throws Exception {
        TestCase.assertEquals(PeriodType.standard(), INSTANCE.getPeriodType(null));
    }

    public void testSetInto_Object() throws Exception {
        MutablePeriod m = new MutablePeriod(PeriodType.millis());
        INSTANCE.setInto(m, null, null);
        TestCase.assertEquals(0L, m.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testIsReadableInterval_Object_Chronology() throws Exception {
        TestCase.assertEquals(false, INSTANCE.isReadableInterval(null, null));
    }

    public void testSetInto_Object_Chronology1() throws Exception {
        MutableInterval m = new MutableInterval(1000L, 2000L, GJChronology.getInstance());
        INSTANCE.setInto(m, null, null);
        TestCase.assertEquals(TEST_TIME_NOW, m.getStartMillis());
        TestCase.assertEquals(TEST_TIME_NOW, m.getEndMillis());
        TestCase.assertEquals(ISOChronology.getInstance(), m.getChronology());
    }

    public void testSetInto_Object_Chronology2() throws Exception {
        MutableInterval m = new MutableInterval(1000L, 2000L, GJChronology.getInstance());
        INSTANCE.setInto(m, null, CopticChronology.getInstance());
        TestCase.assertEquals(TEST_TIME_NOW, m.getStartMillis());
        TestCase.assertEquals(TEST_TIME_NOW, m.getEndMillis());
        TestCase.assertEquals(CopticChronology.getInstance(), m.getChronology());
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TestCase.assertEquals("Converter[null]", INSTANCE.toString());
    }
}

