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


import CalendarConverter.INSTANCE;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.TimeOfDay;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.GJChronology;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.chrono.JulianChronology;


/**
 * This class is a Junit unit test for CalendarConverter.
 *
 * @author Stephen Colebourne
 */
public class TestCalendarConverter extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone MOSCOW = DateTimeZone.forID("Europe/Moscow");

    private static Chronology JULIAN;

    private static Chronology ISO;

    public TestCalendarConverter(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSingleton() throws Exception {
        Class cls = CalendarConverter.class;
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
        TestCase.assertEquals(Calendar.class, INSTANCE.getSupportedType());
    }

    // -----------------------------------------------------------------------
    public void testGetInstantMillis_Object_Chronology() throws Exception {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date(123L));
        TestCase.assertEquals(123L, INSTANCE.getInstantMillis(cal, TestCalendarConverter.JULIAN));
        TestCase.assertEquals(123L, cal.getTime().getTime());
    }

    // -----------------------------------------------------------------------
    public void testGetChronology_Object_Zone() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        TestCase.assertEquals(GJChronology.getInstance(TestCalendarConverter.MOSCOW), INSTANCE.getChronology(cal, TestCalendarConverter.MOSCOW));
        cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Moscow"));
        TestCase.assertEquals(GJChronology.getInstance(), INSTANCE.getChronology(cal, ((DateTimeZone) (null))));
        cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Moscow"));
        cal.setGregorianChange(new Date(0L));
        TestCase.assertEquals(GJChronology.getInstance(TestCalendarConverter.MOSCOW, 0L, 4), INSTANCE.getChronology(cal, TestCalendarConverter.MOSCOW));
        cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Moscow"));
        cal.setGregorianChange(new Date(Long.MAX_VALUE));
        TestCase.assertEquals(JulianChronology.getInstance(TestCalendarConverter.PARIS), INSTANCE.getChronology(cal, TestCalendarConverter.PARIS));
        cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Moscow"));
        cal.setGregorianChange(new Date(Long.MIN_VALUE));
        TestCase.assertEquals(GregorianChronology.getInstance(TestCalendarConverter.PARIS), INSTANCE.getChronology(cal, TestCalendarConverter.PARIS));
        Calendar uc = new MockUnknownCalendar(TimeZone.getTimeZone("Europe/Moscow"));
        TestCase.assertEquals(ISOChronology.getInstance(TestCalendarConverter.PARIS), INSTANCE.getChronology(uc, TestCalendarConverter.PARIS));
        try {
            Calendar bc = ((Calendar) (Class.forName("sun.util.BuddhistCalendar").newInstance()));
            bc.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            TestCase.assertEquals(BuddhistChronology.getInstance(TestCalendarConverter.PARIS), INSTANCE.getChronology(bc, TestCalendarConverter.PARIS));
        } catch (ClassNotFoundException ex) {
            // ignore not Sun JDK
        } catch (IllegalAccessException ex) {
            // ignore JDK 9 modules
        }
    }

    public void testGetChronology_Object_nullChronology() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        TestCase.assertEquals(GJChronology.getInstance(TestCalendarConverter.PARIS), INSTANCE.getChronology(cal, ((Chronology) (null))));
        cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Moscow"));
        cal.setGregorianChange(new Date(0L));
        TestCase.assertEquals(GJChronology.getInstance(TestCalendarConverter.MOSCOW, 0L, 4), INSTANCE.getChronology(cal, ((Chronology) (null))));
        cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Moscow"));
        cal.setGregorianChange(new Date(Long.MAX_VALUE));
        TestCase.assertEquals(JulianChronology.getInstance(TestCalendarConverter.MOSCOW), INSTANCE.getChronology(cal, ((Chronology) (null))));
        cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Moscow"));
        cal.setGregorianChange(new Date(Long.MIN_VALUE));
        TestCase.assertEquals(GregorianChronology.getInstance(TestCalendarConverter.MOSCOW), INSTANCE.getChronology(cal, ((Chronology) (null))));
        cal = new GregorianCalendar(new MockUnknownTimeZone());
        TestCase.assertEquals(GJChronology.getInstance(), INSTANCE.getChronology(cal, ((Chronology) (null))));
        Calendar uc = new MockUnknownCalendar(TimeZone.getTimeZone("Europe/Moscow"));
        TestCase.assertEquals(ISOChronology.getInstance(TestCalendarConverter.MOSCOW), INSTANCE.getChronology(uc, ((Chronology) (null))));
        try {
            Calendar bc = ((Calendar) (Class.forName("sun.util.BuddhistCalendar").newInstance()));
            bc.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
            TestCase.assertEquals(BuddhistChronology.getInstance(TestCalendarConverter.MOSCOW), INSTANCE.getChronology(bc, ((Chronology) (null))));
        } catch (ClassNotFoundException ex) {
            // ignore not Sun JDK
        } catch (IllegalAccessException ex) {
            // ignore JDK 9 modules
        }
    }

    public void testGetChronology_Object_Chronology() throws Exception {
        GregorianCalendar cal = new GregorianCalendar(TimeZone.getTimeZone("Europe/Paris"));
        TestCase.assertEquals(TestCalendarConverter.JULIAN, INSTANCE.getChronology(cal, TestCalendarConverter.JULIAN));
    }

    // -----------------------------------------------------------------------
    public void testGetPartialValues() throws Exception {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Date(12345678L));
        TimeOfDay tod = new TimeOfDay();
        int[] expected = TestCalendarConverter.ISO.get(tod, 12345678L);
        int[] actual = INSTANCE.getPartialValues(tod, cal, TestCalendarConverter.ISO);
        TestCase.assertEquals(true, Arrays.equals(expected, actual));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TestCase.assertEquals("Converter[java.util.Calendar]", INSTANCE.toString());
    }
}

