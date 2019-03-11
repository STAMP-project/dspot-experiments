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


import ReadableDurationConverter.INSTANCE;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.MutablePeriod;
import org.joda.time.PeriodType;
import org.joda.time.ReadableDuration;
import org.joda.time.chrono.ISOChronology;


/**
 * This class is a Junit unit test for ReadableDurationConverter.
 *
 * @author Stephen Colebourne
 */
public class TestReadableDurationConverter extends TestCase {
    private static final DateTimeZone UTC = DateTimeZone.UTC;

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology ISO_PARIS = ISOChronology.getInstance(TestReadableDurationConverter.PARIS);

    private static Chronology JULIAN;

    private static Chronology ISO;

    private DateTimeZone zone = null;

    public TestReadableDurationConverter(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSingleton() throws Exception {
        Class cls = ReadableDurationConverter.class;
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
        TestCase.assertEquals(ReadableDuration.class, INSTANCE.getSupportedType());
    }

    // -----------------------------------------------------------------------
    public void testGetDurationMillis_Object() throws Exception {
        TestCase.assertEquals(123L, INSTANCE.getDurationMillis(new Duration(123L)));
    }

    // -----------------------------------------------------------------------
    public void testGetPeriodType_Object() throws Exception {
        TestCase.assertEquals(PeriodType.standard(), INSTANCE.getPeriodType(new Duration(123L)));
    }

    public void testSetInto_Object() throws Exception {
        MutablePeriod m = new MutablePeriod(PeriodType.yearMonthDayTime());
        INSTANCE.setInto(m, new Duration((((3L * (DateTimeConstants.MILLIS_PER_DAY)) + (4L * (DateTimeConstants.MILLIS_PER_MINUTE))) + 5L)), null);
        TestCase.assertEquals(0, m.getYears());
        TestCase.assertEquals(0, m.getMonths());
        TestCase.assertEquals(0, m.getWeeks());
        TestCase.assertEquals(0, m.getDays());
        TestCase.assertEquals((3 * 24), m.getHours());
        TestCase.assertEquals(4, m.getMinutes());
        TestCase.assertEquals(0, m.getSeconds());
        TestCase.assertEquals(5, m.getMillis());
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TestCase.assertEquals("Converter[org.joda.time.ReadableDuration]", INSTANCE.toString());
    }
}

