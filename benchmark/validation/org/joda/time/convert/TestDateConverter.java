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


import DateConverter.INSTANCE;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Date;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTimeZone;
import org.joda.time.TimeOfDay;
import org.joda.time.chrono.ISOChronology;


/**
 * This class is a Junit unit test for DateConverter.
 *
 * @author Stephen Colebourne
 */
public class TestDateConverter extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology ISO_PARIS = ISOChronology.getInstance(TestDateConverter.PARIS);

    private static Chronology ISO;

    private static Chronology JULIAN;

    private static Chronology COPTIC;

    public TestDateConverter(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSingleton() throws Exception {
        Class cls = DateConverter.class;
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
        TestCase.assertEquals(Date.class, INSTANCE.getSupportedType());
    }

    // -----------------------------------------------------------------------
    public void testGetInstantMillis_Object_Chronology() throws Exception {
        Date date = new Date(123L);
        long millis = INSTANCE.getInstantMillis(date, TestDateConverter.JULIAN);
        TestCase.assertEquals(123L, millis);
        TestCase.assertEquals(123L, INSTANCE.getInstantMillis(date, ((Chronology) (null))));
    }

    // -----------------------------------------------------------------------
    public void testGetChronology_Object_Zone() throws Exception {
        TestCase.assertEquals(TestDateConverter.ISO_PARIS, INSTANCE.getChronology(new Date(123L), TestDateConverter.PARIS));
        TestCase.assertEquals(TestDateConverter.ISO, INSTANCE.getChronology(new Date(123L), ((DateTimeZone) (null))));
    }

    public void testGetChronology_Object_Chronology() throws Exception {
        TestCase.assertEquals(TestDateConverter.JULIAN, INSTANCE.getChronology(new Date(123L), TestDateConverter.JULIAN));
        TestCase.assertEquals(TestDateConverter.ISO, INSTANCE.getChronology(new Date(123L), ((Chronology) (null))));
    }

    // -----------------------------------------------------------------------
    public void testGetPartialValues() throws Exception {
        TimeOfDay tod = new TimeOfDay();
        int[] expected = TestDateConverter.COPTIC.get(tod, 12345678L);
        int[] actual = INSTANCE.getPartialValues(tod, new Date(12345678L), TestDateConverter.COPTIC);
        TestCase.assertEquals(true, Arrays.equals(expected, actual));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TestCase.assertEquals("Converter[java.util.Date]", INSTANCE.toString());
    }
}

