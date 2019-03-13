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


import java.util.Arrays;
import junit.framework.TestCase;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.ISOChronology;

import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;
import static DateTimeConstants.MILLIS_PER_SECOND;


/**
 * This class is a Junit unit test for Partial.
 *
 * @author Stephen Colebourne
 */
public class TestPartial_Constructors extends TestCase {
    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final Chronology ISO_UTC = ISOChronology.getInstanceUTC();

    private static final Chronology GREGORIAN_PARIS = GregorianChronology.getInstance(TestPartial_Constructors.PARIS);

    private static final Chronology GREGORIAN_UTC = GregorianChronology.getInstanceUTC();

    private long TEST_TIME_NOW = (((10L * (MILLIS_PER_HOUR)) + (20L * (MILLIS_PER_MINUTE))) + (30L * (MILLIS_PER_SECOND))) + 40L;

    private DateTimeZone zone = null;

    public TestPartial_Constructors(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor
     */
    public void testConstructor() throws Throwable {
        Partial test = new Partial();
        TestCase.assertEquals(TestPartial_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(0, test.size());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor
     */
    public void testConstructor_Chrono() throws Throwable {
        Partial test = new Partial(((Chronology) (null)));
        TestCase.assertEquals(TestPartial_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(0, test.size());
        test = new Partial(TestPartial_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestPartial_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(0, test.size());
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor
     */
    public void testConstructor_Type_int() throws Throwable {
        Partial test = new Partial(DateTimeFieldType.dayOfYear(), 4);
        TestCase.assertEquals(TestPartial_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(1, test.size());
        TestCase.assertEquals(4, test.getValue(0));
        TestCase.assertEquals(4, test.get(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfYear()));
    }

    /**
     * Test constructor
     */
    public void testConstructorEx1_Type_int() throws Throwable {
        try {
            new Partial(null, 4);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must not be null");
        }
    }

    /**
     * Test constructor
     */
    public void testConstructorEx2_Type_int() throws Throwable {
        try {
            new Partial(DateTimeFieldType.dayOfYear(), 0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor
     */
    public void testConstructor_Type_int_Chrono() throws Throwable {
        Partial test = new Partial(DateTimeFieldType.dayOfYear(), 4, TestPartial_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestPartial_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(1, test.size());
        TestCase.assertEquals(4, test.getValue(0));
        TestCase.assertEquals(4, test.get(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfYear()));
    }

    /**
     * Test constructor
     */
    public void testConstructorEx_Type_int_Chrono() throws Throwable {
        try {
            new Partial(null, 4, TestPartial_Constructors.ISO_UTC);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must not be null");
        }
    }

    /**
     * Test constructor
     */
    public void testConstructorEx2_Type_int_Chrono() throws Throwable {
        try {
            new Partial(DateTimeFieldType.dayOfYear(), 0, TestPartial_Constructors.ISO_UTC);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor
     */
    public void testConstructor_TypeArray_intArray() throws Throwable {
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.dayOfYear() };
        int[] values = new int[]{ 2005, 33 };
        Partial test = new Partial(types, values);
        TestCase.assertEquals(TestPartial_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2, test.size());
        TestCase.assertEquals(2005, test.getValue(0));
        TestCase.assertEquals(2005, test.get(DateTimeFieldType.year()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.year()));
        TestCase.assertEquals(33, test.getValue(1));
        TestCase.assertEquals(33, test.get(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(true, Arrays.equals(test.getFieldTypes(), types));
        TestCase.assertEquals(true, Arrays.equals(test.getValues(), values));
    }

    /**
     * Test constructor
     */
    public void testConstructor_TypeArray_intArray_year_weekyear() throws Throwable {
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.weekyear() };
        int[] values = new int[]{ 2005, 2006 };
        Partial test = new Partial(types, values);
        TestCase.assertEquals(TestPartial_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(2, test.size());
        TestCase.assertEquals(2005, test.getValue(0));
        TestCase.assertEquals(2005, test.get(DateTimeFieldType.year()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.year()));
        TestCase.assertEquals(2006, test.getValue(1));
        TestCase.assertEquals(2006, test.get(DateTimeFieldType.weekyear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.weekyear()));
        TestCase.assertEquals(true, Arrays.equals(test.getFieldTypes(), types));
        TestCase.assertEquals(true, Arrays.equals(test.getValues(), values));
    }

    /**
     * Test constructor
     */
    public void testConstructor2_TypeArray_intArray() throws Throwable {
        DateTimeFieldType[] types = new DateTimeFieldType[0];
        int[] values = new int[0];
        Partial test = new Partial(types, values);
        TestCase.assertEquals(TestPartial_Constructors.ISO_UTC, test.getChronology());
        TestCase.assertEquals(0, test.size());
    }

    /**
     * Test constructor
     */
    public void testConstructorEx1_TypeArray_intArray() throws Throwable {
        try {
            new Partial(((DateTimeFieldType[]) (null)), new int[]{ 1 });
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must not be null");
        }
    }

    /**
     * Test constructor
     */
    public void testConstructorEx3_TypeArray_intArray() throws Throwable {
        try {
            new Partial(new DateTimeFieldType[]{ DateTimeFieldType.dayOfYear() }, null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must not be null");
        }
    }

    /**
     * Test constructor
     */
    public void testConstructorEx5_TypeArray_intArray() throws Throwable {
        try {
            new Partial(new DateTimeFieldType[]{ DateTimeFieldType.dayOfYear() }, new int[2]);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "same length");
        }
    }

    /**
     * Test constructor
     */
    public void testConstructorEx6_TypeArray_intArray() throws Throwable {
        try {
            new Partial(new DateTimeFieldType[]{ null, DateTimeFieldType.dayOfYear() }, new int[2]);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "contain null");
        }
        try {
            new Partial(new DateTimeFieldType[]{ DateTimeFieldType.dayOfYear(), null }, new int[2]);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "contain null");
        }
    }

    /**
     * Test constructor
     */
    public void testConstructorEx7_TypeArray_intArray_inOrder() throws Throwable {
        int[] values = new int[]{ 1, 1, 1 };
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.dayOfMonth(), DateTimeFieldType.year(), DateTimeFieldType.monthOfYear() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must be in order", "largest-smallest");
        }
        types = new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.dayOfMonth(), DateTimeFieldType.monthOfYear() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must be in order", "largest-smallest");
        }
        types = new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.era(), DateTimeFieldType.monthOfYear() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must be in order", "largest-smallest");
        }
        types = new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.dayOfMonth(), DateTimeFieldType.era() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must be in order", "largest-smallest");
        }
        types = new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.dayOfMonth(), DateTimeFieldType.dayOfYear() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must be in order", "largest-smallest");
        }
        types = new DateTimeFieldType[]{ DateTimeFieldType.yearOfEra(), DateTimeFieldType.year(), DateTimeFieldType.dayOfYear() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must be in order", "largest-smallest");
        }
        types = new DateTimeFieldType[]{ DateTimeFieldType.weekyear(), DateTimeFieldType.yearOfCentury(), DateTimeFieldType.dayOfMonth() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must be in order", "largest-smallest");
        }
        types = new DateTimeFieldType[]{ DateTimeFieldType.weekyear(), DateTimeFieldType.year(), DateTimeFieldType.dayOfMonth() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must be in order", "largest-smallest");
        }
    }

    /**
     * Test constructor
     */
    public void testConstructorEx8_TypeArray_intArray_duplicate() throws Throwable {
        int[] values = new int[]{ 1, 1, 1 };
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.era(), DateTimeFieldType.year(), DateTimeFieldType.year() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must not", "duplicate");
        }
        types = new DateTimeFieldType[]{ DateTimeFieldType.era(), DateTimeFieldType.era(), DateTimeFieldType.monthOfYear() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must not", "duplicate");
        }
        types = new DateTimeFieldType[]{ DateTimeFieldType.dayOfYear(), DateTimeFieldType.dayOfMonth(), DateTimeFieldType.dayOfMonth() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must not", "duplicate");
        }
        types = new DateTimeFieldType[]{ DateTimeFieldType.dayOfMonth(), DateTimeFieldType.clockhourOfDay(), DateTimeFieldType.hourOfDay() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must not", "duplicate");
        }
    }

    /**
     * Test constructor
     */
    public void testConstructorEx9_TypeArray_intArray() throws Throwable {
        int[] values = new int[]{ 3, 0 };
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.dayOfMonth(), DateTimeFieldType.dayOfWeek() };
        try {
            new Partial(types, values);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "Value 0");
        }
    }

    // -----------------------------------------------------------------------
    /**
     * Test constructor
     */
    public void testConstructor_TypeArray_intArray_Chrono() throws Throwable {
        DateTimeFieldType[] types = new DateTimeFieldType[]{ DateTimeFieldType.year(), DateTimeFieldType.dayOfYear() };
        int[] values = new int[]{ 2005, 33 };
        Partial test = new Partial(types, values, TestPartial_Constructors.GREGORIAN_PARIS);
        TestCase.assertEquals(TestPartial_Constructors.GREGORIAN_UTC, test.getChronology());
        TestCase.assertEquals(2, test.size());
        TestCase.assertEquals(2005, test.getValue(0));
        TestCase.assertEquals(2005, test.get(DateTimeFieldType.year()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.year()));
        TestCase.assertEquals(33, test.getValue(1));
        TestCase.assertEquals(33, test.get(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(true, test.isSupported(DateTimeFieldType.dayOfYear()));
        TestCase.assertEquals(true, Arrays.equals(test.getFieldTypes(), types));
        TestCase.assertEquals(true, Arrays.equals(test.getValues(), values));
    }

    /**
     * Test constructor
     */
    public void testConstructorEx_Partial() throws Throwable {
        try {
            new Partial(((ReadablePartial) (null)));
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
            assertMessageContains(ex, "must not be null");
        }
    }
}

