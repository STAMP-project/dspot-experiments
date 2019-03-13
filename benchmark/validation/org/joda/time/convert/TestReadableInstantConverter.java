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


import ReadableInstantConverter.INSTANCE;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import junit.framework.TestCase;
import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;
import org.joda.time.ReadableInstant;
import org.joda.time.TimeOfDay;
import org.joda.time.chrono.ISOChronology;


/**
 * This class is a Junit unit test for ReadableInstantConverter.
 *
 * @author Stephen Colebourne
 */
public class TestReadableInstantConverter extends TestCase {
    private static final DateTimeZone UTC = DateTimeZone.UTC;

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology ISO_PARIS = ISOChronology.getInstance(TestReadableInstantConverter.PARIS);

    private static Chronology JULIAN;

    private static Chronology ISO;

    private DateTimeZone zone = null;

    public TestReadableInstantConverter(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSingleton() throws Exception {
        Class cls = ReadableInstantConverter.class;
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
        TestCase.assertEquals(ReadableInstant.class, INSTANCE.getSupportedType());
    }

    // -----------------------------------------------------------------------
    public void testGetInstantMillis_Object_Chronology() throws Exception {
        TestCase.assertEquals(123L, INSTANCE.getInstantMillis(new Instant(123L), TestReadableInstantConverter.JULIAN));
        TestCase.assertEquals(123L, INSTANCE.getInstantMillis(new DateTime(123L), TestReadableInstantConverter.JULIAN));
        TestCase.assertEquals(123L, INSTANCE.getInstantMillis(new Instant(123L), ((Chronology) (null))));
        TestCase.assertEquals(123L, INSTANCE.getInstantMillis(new DateTime(123L), ((Chronology) (null))));
    }

    // -----------------------------------------------------------------------
    public void testGetChronology_Object_Zone() throws Exception {
        TestCase.assertEquals(TestReadableInstantConverter.ISO_PARIS, INSTANCE.getChronology(new Instant(123L), TestReadableInstantConverter.PARIS));
        TestCase.assertEquals(TestReadableInstantConverter.ISO_PARIS, INSTANCE.getChronology(new DateTime(123L), TestReadableInstantConverter.PARIS));
        TestCase.assertEquals(TestReadableInstantConverter.ISO, INSTANCE.getChronology(new Instant(123L), DateTimeZone.getDefault()));
        TestCase.assertEquals(TestReadableInstantConverter.ISO, INSTANCE.getChronology(new DateTime(123L), DateTimeZone.getDefault()));
        TestCase.assertEquals(TestReadableInstantConverter.ISO, INSTANCE.getChronology(new Instant(123L), ((DateTimeZone) (null))));
        TestCase.assertEquals(TestReadableInstantConverter.ISO, INSTANCE.getChronology(new DateTime(123L), ((DateTimeZone) (null))));
        TestCase.assertEquals(TestReadableInstantConverter.ISO_PARIS, INSTANCE.getChronology(new DateTime(123L, new MockBadChronology()), TestReadableInstantConverter.PARIS));
        MutableDateTime mdt = new MutableDateTime() {
            public Chronology getChronology() {
                return null;// bad

            }
        };
        TestCase.assertEquals(TestReadableInstantConverter.ISO_PARIS, INSTANCE.getChronology(mdt, TestReadableInstantConverter.PARIS));
    }

    public void testGetChronology_Object_nullChronology() throws Exception {
        TestCase.assertEquals(TestReadableInstantConverter.ISO.withUTC(), INSTANCE.getChronology(new Instant(123L), ((Chronology) (null))));
        TestCase.assertEquals(TestReadableInstantConverter.ISO, INSTANCE.getChronology(new DateTime(123L), ((Chronology) (null))));
        MutableDateTime mdt = new MutableDateTime() {
            public Chronology getChronology() {
                return null;// bad

            }
        };
        TestCase.assertEquals(TestReadableInstantConverter.ISO, INSTANCE.getChronology(mdt, ((Chronology) (null))));
    }

    public void testGetChronology_Object_Chronology() throws Exception {
        TestCase.assertEquals(TestReadableInstantConverter.JULIAN, INSTANCE.getChronology(new Instant(123L), TestReadableInstantConverter.JULIAN));
        TestCase.assertEquals(TestReadableInstantConverter.JULIAN, INSTANCE.getChronology(new DateTime(123L), TestReadableInstantConverter.JULIAN));
    }

    // -----------------------------------------------------------------------
    public void testGetPartialValues() throws Exception {
        TimeOfDay tod = new TimeOfDay();
        int[] expected = ISOChronology.getInstance().get(tod, 12345678L);
        int[] actual = INSTANCE.getPartialValues(tod, new Instant(12345678L), ISOChronology.getInstance());
        TestCase.assertEquals(true, Arrays.equals(expected, actual));
    }

    // -----------------------------------------------------------------------
    public void testToString() {
        TestCase.assertEquals("Converter[org.joda.time.ReadableInstant]", INSTANCE.toString());
    }
}

