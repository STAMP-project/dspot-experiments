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


import MillisDurationField.INSTANCE;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import junit.framework.TestCase;
import org.joda.time.DurationField;
import org.joda.time.DurationFieldType;
import org.joda.time.chrono.ISOChronology;

import static MillisDurationField.INSTANCE;


/**
 * This class is a Junit unit test for PeriodFormatterBuilder.
 *
 * @author Stephen Colebourne
 */
public class TestMillisDurationField extends TestCase {
    public TestMillisDurationField(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void test_getType() {
        TestCase.assertEquals(DurationFieldType.millis(), INSTANCE.getType());
    }

    public void test_getName() {
        TestCase.assertEquals("millis", INSTANCE.getName());
    }

    public void test_isSupported() {
        TestCase.assertEquals(true, INSTANCE.isSupported());
    }

    public void test_isPrecise() {
        TestCase.assertEquals(true, INSTANCE.isPrecise());
    }

    public void test_getUnitMillis() {
        TestCase.assertEquals(1, INSTANCE.getUnitMillis());
    }

    public void test_toString() {
        TestCase.assertEquals("DurationField[millis]", INSTANCE.toString());
    }

    // -----------------------------------------------------------------------
    public void test_getValue_long() {
        TestCase.assertEquals(0, INSTANCE.getValue(0L));
        TestCase.assertEquals(1234, INSTANCE.getValue(1234L));
        TestCase.assertEquals((-1234), INSTANCE.getValue((-1234L)));
        try {
            INSTANCE.getValue((((long) (Integer.MAX_VALUE)) + 1L));
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    public void test_getValueAsLong_long() {
        TestCase.assertEquals(0L, INSTANCE.getValueAsLong(0L));
        TestCase.assertEquals(1234L, INSTANCE.getValueAsLong(1234L));
        TestCase.assertEquals((-1234L), INSTANCE.getValueAsLong((-1234L)));
        TestCase.assertEquals((((long) (Integer.MAX_VALUE)) + 1L), INSTANCE.getValueAsLong((((long) (Integer.MAX_VALUE)) + 1L)));
    }

    public void test_getValue_long_long() {
        TestCase.assertEquals(0, INSTANCE.getValue(0L, 567L));
        TestCase.assertEquals(1234, INSTANCE.getValue(1234L, 567L));
        TestCase.assertEquals((-1234), INSTANCE.getValue((-1234L), 567L));
        try {
            INSTANCE.getValue((((long) (Integer.MAX_VALUE)) + 1L), 567L);
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    public void test_getValueAsLong_long_long() {
        TestCase.assertEquals(0L, INSTANCE.getValueAsLong(0L, 567L));
        TestCase.assertEquals(1234L, INSTANCE.getValueAsLong(1234L, 567L));
        TestCase.assertEquals((-1234L), INSTANCE.getValueAsLong((-1234L), 567L));
        TestCase.assertEquals((((long) (Integer.MAX_VALUE)) + 1L), INSTANCE.getValueAsLong((((long) (Integer.MAX_VALUE)) + 1L), 567L));
    }

    // -----------------------------------------------------------------------
    public void test_getMillis_int() {
        TestCase.assertEquals(0, INSTANCE.getMillis(0));
        TestCase.assertEquals(1234, INSTANCE.getMillis(1234));
        TestCase.assertEquals((-1234), INSTANCE.getMillis((-1234)));
    }

    public void test_getMillis_long() {
        TestCase.assertEquals(0L, INSTANCE.getMillis(0L));
        TestCase.assertEquals(1234L, INSTANCE.getMillis(1234L));
        TestCase.assertEquals((-1234L), INSTANCE.getMillis((-1234L)));
    }

    public void test_getMillis_int_long() {
        TestCase.assertEquals(0, INSTANCE.getMillis(0, 567L));
        TestCase.assertEquals(1234, INSTANCE.getMillis(1234, 567L));
        TestCase.assertEquals((-1234), INSTANCE.getMillis((-1234), 567L));
    }

    public void test_getMillis_long_long() {
        TestCase.assertEquals(0L, INSTANCE.getMillis(0L, 567L));
        TestCase.assertEquals(1234L, INSTANCE.getMillis(1234L, 567L));
        TestCase.assertEquals((-1234L), INSTANCE.getMillis((-1234L), 567L));
    }

    // -----------------------------------------------------------------------
    public void test_add_long_int() {
        TestCase.assertEquals(567L, INSTANCE.add(567L, 0));
        TestCase.assertEquals((567L + 1234L), INSTANCE.add(567L, 1234));
        TestCase.assertEquals((567L - 1234L), INSTANCE.add(567L, (-1234)));
        try {
            INSTANCE.add(Long.MAX_VALUE, 1);
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    public void test_add_long_long() {
        TestCase.assertEquals(567L, INSTANCE.add(567L, 0L));
        TestCase.assertEquals((567L + 1234L), INSTANCE.add(567L, 1234L));
        TestCase.assertEquals((567L - 1234L), INSTANCE.add(567L, (-1234L)));
        try {
            INSTANCE.add(Long.MAX_VALUE, 1L);
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void test_getDifference_long_int() {
        TestCase.assertEquals(567, INSTANCE.getDifference(567L, 0L));
        TestCase.assertEquals((567 - 1234), INSTANCE.getDifference(567L, 1234L));
        TestCase.assertEquals((567 + 1234), INSTANCE.getDifference(567L, (-1234L)));
        try {
            INSTANCE.getDifference(Long.MAX_VALUE, 1L);
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    public void test_getDifferenceAsLong_long_long() {
        TestCase.assertEquals(567L, INSTANCE.getDifferenceAsLong(567L, 0L));
        TestCase.assertEquals((567L - 1234L), INSTANCE.getDifferenceAsLong(567L, 1234L));
        TestCase.assertEquals((567L + 1234L), INSTANCE.getDifferenceAsLong(567L, (-1234L)));
        try {
            INSTANCE.getDifferenceAsLong(Long.MAX_VALUE, (-1L));
            TestCase.fail();
        } catch (ArithmeticException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void test_compareTo() {
        TestCase.assertEquals(0, INSTANCE.compareTo(INSTANCE));
        TestCase.assertEquals((-1), INSTANCE.compareTo(ISOChronology.getInstance().seconds()));
        DurationField dummy = new PreciseDurationField(DurationFieldType.seconds(), 0);
        TestCase.assertEquals(1, INSTANCE.compareTo(dummy));
        // try {
        // MillisDurationField.INSTANCE.compareTo("");
        // fail();
        // } catch (ClassCastException ex) {}
        try {
            INSTANCE.compareTo(null);
            TestCase.fail();
        } catch (NullPointerException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testSerialization() throws Exception {
        DurationField test = INSTANCE;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(test);
        oos.close();
        byte[] bytes = baos.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        ObjectInputStream ois = new ObjectInputStream(bais);
        DurationField result = ((DurationField) (ois.readObject()));
        ois.close();
        TestCase.assertSame(test, result);
    }
}

