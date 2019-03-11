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


import LongConverter.INSTANCE;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.TimeOfDay;
import org.joda.time.chrono.ISOChronology;


/**
 * This class is a Junit unit test for LongConverter.
 *
 * @author Stephen Colebourne
 */
public class TestLongConverter extends TestCase {
    private static final DateTimeZone UTC = DateTimeZone.UTC;

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology ISO_PARIS = ISOChronology.getInstance(TestLongConverter.PARIS);

    private static Chronology JULIAN;

    private static Chronology ISO;

    private DateTimeZone zone = null;

    public TestLongConverter(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSingleton() throws Exception {
        Class cls = LongConverter.class;
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
        TestCase.assertEquals(Long.class, INSTANCE.getSupportedType());
    }

    // -----------------------------------------------------------------------
    public void testGetInstantMillis_Object_Chronology() throws Exception {
        TestCase.assertEquals(123L, INSTANCE.getInstantMillis(new Long(123L), TestLongConverter.JULIAN));
        TestCase.assertEquals(123L, INSTANCE.getInstantMillis(new Long(123L), ((Chronology) (null))));
    }

    // -----------------------------------------------------------------------
    public void testGetChronology_Object_Zone() throws Exception {
        TestCase.assertEquals(TestLongConverter.ISO_PARIS, INSTANCE.getChronology(new Long(123L), TestLongConverter.PARIS));
        TestCase.assertEquals(TestLongConverter.ISO, INSTANCE.getChronology(new Long(123L), ((DateTimeZone) (null))));
    }

    public void testGetChronology_Object_Chronology() throws Exception {
        TestCase.assertEquals(TestLongConverter.JULIAN, INSTANCE.getChronology(new Long(123L), TestLongConverter.JULIAN));
        TestCase.assertEquals(TestLongConverter.ISO, INSTANCE.getChronology(new Long(123L), ((Chronology) (null))));
    }

    // -----------------------------------------------------------------------
    public void testGetPartialValues() throws Exception {
        TimeOfDay tod = new TimeOfDay();
        int[] expected = ISOChronology.getInstance().get(tod, 12345678L);
        int[] actual = INSTANCE.getPartialValues(tod, new Long(12345678L), ISOChronology.getInstance());
        TestCase.assertEquals(true, Arrays.equals(expected, actual));
    }

    // -----------------------------------------------------------------------
    public void testGetDurationMillis_Object() throws Exception {
        TestCase.assertEquals(123L, INSTANCE.getDurationMillis(new Long(123L)));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TestCase.assertEquals("Converter[java.lang.Long]", INSTANCE.toString());
    }
}

