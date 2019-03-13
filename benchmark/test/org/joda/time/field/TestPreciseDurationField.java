/**
 * Copyright 2001-2009 Stephen Colebourne
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
package org.joda.time.field;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.chrono.ISOChronology;


/**
 * This class is a Junit unit test for PreciseDurationField.
 *
 * @author Stephen Colebourne
 */
public class TestPreciseDurationField extends TestCase {
    private static final long LONG_INTEGER_MAX = Integer.MAX_VALUE;

    private static final int INTEGER_MAX = Integer.MAX_VALUE;

    private static final long LONG_MAX = Long.MAX_VALUE;

    private PreciseDurationField iField;

    public TestPreciseDurationField(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void test_constructor() {
        try {
            new PreciseDurationField(null, 10);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void test_getType() {
        TestCase.assertEquals(DurationFieldType.seconds(), iField.getType());
    }

    public void test_getName() {
        TestCase.assertEquals("seconds", iField.getName());
    }

    public void test_isSupported() {
        TestCase.assertEquals(true, iField.isSupported());
    }

    public void test_isPrecise() {
        TestCase.assertEquals(true, iField.isPrecise());
    }

    public void test_getUnitMillis() {
        TestCase.assertEquals(1000, iField.getUnitMillis());
    }

    public void test_toString() {
        TestCase.assertEquals("DurationField[seconds]", iField.toString());
    }

    // -----------------------------------------------------------------------
    public void test_getValue_long() {
        TestCase.assertEquals(0, iField.getValue(0L));
        TestCase.assertEquals(12345, iField.getValue(12345678L));
        TestCase.assertEquals((-1), iField.getValue((-1234L)));
        TestCase.assertEquals(TestPreciseDurationField.INTEGER_MAX, iField.getValue((((TestPreciseDurationField.LONG_INTEGER_MAX) * 1000L) + 999L)));
        try {
            iField.getValue((((TestPreciseDurationField.LONG_INTEGER_MAX) * 1000L) + 1000L));
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    public void test_getValueAsLong_long() {
        TestCase.assertEquals(0L, iField.getValueAsLong(0L));
        TestCase.assertEquals(12345L, iField.getValueAsLong(12345678L));
        TestCase.assertEquals((-1L), iField.getValueAsLong((-1234L)));
        TestCase.assertEquals(((TestPreciseDurationField.LONG_INTEGER_MAX) + 1L), iField.getValueAsLong((((TestPreciseDurationField.LONG_INTEGER_MAX) * 1000L) + 1000L)));
    }

    public void test_getValue_long_long() {
        TestCase.assertEquals(0, iField.getValue(0L, 567L));
        TestCase.assertEquals(12345, iField.getValue(12345678L, 567L));
        TestCase.assertEquals((-1), iField.getValue((-1234L), 567L));
        TestCase.assertEquals(TestPreciseDurationField.INTEGER_MAX, iField.getValue((((TestPreciseDurationField.LONG_INTEGER_MAX) * 1000L) + 999L), 567L));
        try {
            iField.getValue((((TestPreciseDurationField.LONG_INTEGER_MAX) * 1000L) + 1000L), 567L);
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    public void test_getValueAsLong_long_long() {
        TestCase.assertEquals(0L, iField.getValueAsLong(0L, 567L));
        TestCase.assertEquals(12345L, iField.getValueAsLong(12345678L, 567L));
        TestCase.assertEquals((-1L), iField.getValueAsLong((-1234L), 567L));
        TestCase.assertEquals(((TestPreciseDurationField.LONG_INTEGER_MAX) + 1L), iField.getValueAsLong((((TestPreciseDurationField.LONG_INTEGER_MAX) * 1000L) + 1000L), 567L));
    }

    // -----------------------------------------------------------------------
    public void test_getMillis_int() {
        TestCase.assertEquals(0, iField.getMillis(0));
        TestCase.assertEquals(1234000L, iField.getMillis(1234));
        TestCase.assertEquals((-1234000L), iField.getMillis((-1234)));
        TestCase.assertEquals(((TestPreciseDurationField.LONG_INTEGER_MAX) * 1000L), iField.getMillis(TestPreciseDurationField.INTEGER_MAX));
    }

    public void test_getMillis_long() {
        TestCase.assertEquals(0L, iField.getMillis(0L));
        TestCase.assertEquals(1234000L, iField.getMillis(1234L));
        TestCase.assertEquals((-1234000L), iField.getMillis((-1234L)));
        try {
            iField.getMillis(TestPreciseDurationField.LONG_MAX);
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    public void test_getMillis_int_long() {
        TestCase.assertEquals(0L, iField.getMillis(0, 567L));
        TestCase.assertEquals(1234000L, iField.getMillis(1234, 567L));
        TestCase.assertEquals((-1234000L), iField.getMillis((-1234), 567L));
        TestCase.assertEquals(((TestPreciseDurationField.LONG_INTEGER_MAX) * 1000L), iField.getMillis(TestPreciseDurationField.INTEGER_MAX, 567L));
    }

    public void test_getMillis_long_long() {
        TestCase.assertEquals(0L, iField.getMillis(0L, 567L));
        TestCase.assertEquals(1234000L, iField.getMillis(1234L, 567L));
        TestCase.assertEquals((-1234000L), iField.getMillis((-1234L), 567L));
        try {
            iField.getMillis(TestPreciseDurationField.LONG_MAX, 567L);
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void test_add_long_int() {
        TestCase.assertEquals(567L, iField.add(567L, 0));
        TestCase.assertEquals((567L + 1234000L), iField.add(567L, 1234));
        TestCase.assertEquals((567L - 1234000L), iField.add(567L, (-1234)));
        try {
            iField.add(TestPreciseDurationField.LONG_MAX, 1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    public void test_add_long_long() {
        TestCase.assertEquals(567L, iField.add(567L, 0L));
        TestCase.assertEquals((567L + 1234000L), iField.add(567L, 1234L));
        TestCase.assertEquals((567L - 1234000L), iField.add(567L, (-1234L)));
        try {
            iField.add(TestPreciseDurationField.LONG_MAX, 1L);
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
        try {
            iField.add(1L, TestPreciseDurationField.LONG_MAX);
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void test_getDifference_long_int() {
        TestCase.assertEquals(0, iField.getDifference(1L, 0L));
        TestCase.assertEquals(567, iField.getDifference(567000L, 0L));
        TestCase.assertEquals((567 - 1234), iField.getDifference(567000L, 1234000L));
        TestCase.assertEquals((567 + 1234), iField.getDifference(567000L, (-1234000L)));
        try {
            iField.getDifference(TestPreciseDurationField.LONG_MAX, (-1L));
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    public void test_getDifferenceAsLong_long_long() {
        TestCase.assertEquals(0L, iField.getDifferenceAsLong(1L, 0L));
        TestCase.assertEquals(567L, iField.getDifferenceAsLong(567000L, 0L));
        TestCase.assertEquals((567L - 1234L), iField.getDifferenceAsLong(567000L, 1234000L));
        TestCase.assertEquals((567L + 1234L), iField.getDifferenceAsLong(567000L, (-1234000L)));
        try {
            iField.getDifferenceAsLong(TestPreciseDurationField.LONG_MAX, (-1L));
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void test_equals() {
        TestCase.assertEquals(true, iField.equals(iField));
        TestCase.assertEquals(false, iField.equals(ISOChronology.getInstance().minutes()));
        DurationField dummy = new PreciseDurationField(DurationFieldType.seconds(), 0);
        TestCase.assertEquals(false, iField.equals(dummy));
        dummy = new PreciseDurationField(DurationFieldType.seconds(), 1000);
        TestCase.assertEquals(true, iField.equals(dummy));
        dummy = new PreciseDurationField(DurationFieldType.millis(), 1000);
        TestCase.assertEquals(false, iField.equals(dummy));
        TestCase.assertEquals(false, iField.equals(""));
        TestCase.assertEquals(false, iField.equals(null));
    }

    public void test_hashCode() {
        TestCase.assertEquals(true, ((iField.hashCode()) == (iField.hashCode())));
        TestCase.assertEquals(false, ((iField.hashCode()) == (ISOChronology.getInstance().minutes().hashCode())));
        DurationField dummy = new PreciseDurationField(DurationFieldType.seconds(), 0);
        TestCase.assertEquals(false, ((iField.hashCode()) == (dummy.hashCode())));
        dummy = new PreciseDurationField(DurationFieldType.seconds(), 1000);
        TestCase.assertEquals(true, ((iField.hashCode()) == (dummy.hashCode())));
        dummy = new PreciseDurationField(DurationFieldType.millis(), 1000);
        TestCase.assertEquals(false, ((iField.hashCode()) == (dummy.hashCode())));
    }

    // -----------------------------------------------------------------------
    public void test_compareTo() {
        TestCase.assertEquals(0, iField.compareTo(iField));
        TestCase.assertEquals((-1), iField.compareTo(ISOChronology.getInstance().minutes()));
        DurationField dummy = new PreciseDurationField(DurationFieldType.seconds(), 0);
        TestCase.assertEquals(1, iField.compareTo(dummy));
        // try {
        // iField.compareTo("");
        // fail();
        // } catch (ClassCastException ex) {}
        try {
            iField.compareTo(null);
            TestCase.fail();
        } catch (NullPointerException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        DurationField test = iField;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        DurationField result = ((DurationField) (ois.readObject()));
        ois.close();
        TestCase.assertEquals(test, result);
    }
}

