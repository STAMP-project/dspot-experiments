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


import ReadablePartialConverter.INSTANCE;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;
import org.joda.time.TimeOfDay;
import org.joda.time.YearMonthDay;
import org.joda.time.base.BasePartial;
import org.joda.time.chrono.ISOChronology;


/**
 * This class is a Junit unit test for ReadablePartialConverter.
 *
 * @author Stephen Colebourne
 */
public class TestReadablePartialConverter extends TestCase {
    private static final DateTimeZone UTC = DateTimeZone.UTC;

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology ISO_PARIS = ISOChronology.getInstance(TestReadablePartialConverter.PARIS);

    private static Chronology JULIAN;

    private static Chronology ISO;

    private static Chronology BUDDHIST;

    private DateTimeZone zone = null;

    public TestReadablePartialConverter(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSingleton() throws Exception {
        Class cls = ReadablePartialConverter.class;
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
        TestCase.assertEquals(ReadablePartial.class, INSTANCE.getSupportedType());
    }

    // -----------------------------------------------------------------------
    public void testGetChronology_Object_Zone() throws Exception {
        TestCase.assertEquals(TestReadablePartialConverter.ISO_PARIS, INSTANCE.getChronology(new TimeOfDay(123L), TestReadablePartialConverter.PARIS));
        TestCase.assertEquals(TestReadablePartialConverter.ISO, INSTANCE.getChronology(new TimeOfDay(123L), DateTimeZone.getDefault()));
        TestCase.assertEquals(TestReadablePartialConverter.ISO, INSTANCE.getChronology(new TimeOfDay(123L), ((DateTimeZone) (null))));
    }

    public void testGetChronology_Object_Chronology() throws Exception {
        TestCase.assertEquals(TestReadablePartialConverter.JULIAN, INSTANCE.getChronology(new TimeOfDay(123L, TestReadablePartialConverter.BUDDHIST), TestReadablePartialConverter.JULIAN));
        TestCase.assertEquals(TestReadablePartialConverter.JULIAN, INSTANCE.getChronology(new TimeOfDay(123L), TestReadablePartialConverter.JULIAN));
        TestCase.assertEquals(TestReadablePartialConverter.BUDDHIST.withUTC(), INSTANCE.getChronology(new TimeOfDay(123L, TestReadablePartialConverter.BUDDHIST), ((Chronology) (null))));
    }

    // -----------------------------------------------------------------------
    public void testGetPartialValues() throws Exception {
        TimeOfDay tod = new TimeOfDay();
        int[] expected = new int[]{ 1, 2, 3, 4 };
        int[] actual = INSTANCE.getPartialValues(tod, new TimeOfDay(1, 2, 3, 4), ISOChronology.getInstance(TestReadablePartialConverter.PARIS));
        TestCase.assertEquals(true, Arrays.equals(expected, actual));
        try {
            INSTANCE.getPartialValues(tod, new YearMonthDay(2005, 6, 9), TestReadablePartialConverter.JULIAN);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            INSTANCE.getPartialValues(tod, new TestReadablePartialConverter.MockTOD(), TestReadablePartialConverter.JULIAN);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    static class MockTOD extends BasePartial {
        protected DateTimeField getField(int index, Chronology chrono) {
            switch (index) {
                case 0 :
                    return chrono.hourOfDay();
                case 1 :
                    return chrono.minuteOfHour();
                case 2 :
                    return chrono.year();
                case 3 :
                    return chrono.era();
            }
            return null;
        }

        public int size() {
            return 4;
        }
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TestCase.assertEquals("Converter[org.joda.time.ReadablePartial]", INSTANCE.toString());
    }
}

