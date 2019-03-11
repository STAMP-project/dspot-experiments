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
package org.joda.time;


import junit.framework.TestCase;
import org.joda.time.base.AbstractPartial;
import org.joda.time.chrono.BuddhistChronology;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.field.AbstractPartialFieldProperty;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for YearMonthDay.
 *
 * @author Stephen Colebourne
 */
public class TestAbstractPartial extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private long TEST_TIME1 = ((((((31L + 28L) + 31L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2 = ((((((((365L + 31L) + 28L) + 31L) + 30L) + 7L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone zone = null;

    public TestAbstractPartial(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testGetValue() throws Throwable {
        TestAbstractPartial.MockPartial mock = new TestAbstractPartial.MockPartial();
        TestCase.assertEquals(1970, mock.getValue(0));
        TestCase.assertEquals(1, mock.getValue(1));
        try {
            mock.getValue((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            mock.getValue(2);
            TestCase.fail();
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetValues() throws Throwable {
        TestAbstractPartial.MockPartial mock = new TestAbstractPartial.MockPartial();
        int[] vals = getValues();
        TestCase.assertEquals(2, vals.length);
        TestCase.assertEquals(1970, vals[0]);
        TestCase.assertEquals(1, vals[1]);
    }

    public void testGetField() throws Throwable {
        TestAbstractPartial.MockPartial mock = new TestAbstractPartial.MockPartial();
        TestCase.assertEquals(BuddhistChronology.getInstanceUTC().year(), mock.getField(0));
        TestCase.assertEquals(BuddhistChronology.getInstanceUTC().monthOfYear(), mock.getField(1));
        try {
            mock.getField((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            mock.getField(2);
            TestCase.fail();
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetFieldType() throws Throwable {
        TestAbstractPartial.MockPartial mock = new TestAbstractPartial.MockPartial();
        TestCase.assertEquals(DateTimeFieldType.year(), getFieldType(0));
        TestCase.assertEquals(DateTimeFieldType.monthOfYear(), getFieldType(1));
        try {
            getFieldType((-1));
            TestCase.fail();
        } catch (IndexOutOfBoundsException ex) {
        }
        try {
            getFieldType(2);
            TestCase.fail();
        } catch (IndexOutOfBoundsException ex) {
        }
    }

    public void testGetFieldTypes() throws Throwable {
        TestAbstractPartial.MockPartial mock = new TestAbstractPartial.MockPartial();
        DateTimeFieldType[] vals = getFieldTypes();
        TestCase.assertEquals(2, vals.length);
        TestCase.assertEquals(DateTimeFieldType.year(), vals[0]);
        TestCase.assertEquals(DateTimeFieldType.monthOfYear(), vals[1]);
    }

    public void testGetPropertyEquals() throws Throwable {
        TestAbstractPartial.MockProperty0 prop0 = new TestAbstractPartial.MockProperty0();
        TestCase.assertEquals(true, prop0.equals(prop0));
        TestCase.assertEquals(true, prop0.equals(new TestAbstractPartial.MockProperty0()));
        TestCase.assertEquals(false, prop0.equals(new TestAbstractPartial.MockProperty1()));
        TestCase.assertEquals(false, prop0.equals(new TestAbstractPartial.MockProperty0Val()));
        TestCase.assertEquals(false, prop0.equals(new TestAbstractPartial.MockProperty0Field()));
        TestCase.assertEquals(false, prop0.equals(new TestAbstractPartial.MockProperty0Chrono()));
        TestCase.assertEquals(false, prop0.equals(""));
        TestCase.assertEquals(false, prop0.equals(null));
    }

    // -----------------------------------------------------------------------
    static class MockPartial extends AbstractPartial {
        int[] val = new int[]{ 1970, 1 };

        MockPartial() {
            super();
        }

        protected DateTimeField getField(int index, Chronology chrono) {
            switch (index) {
                case 0 :
                    return chrono.year();
                case 1 :
                    return chrono.monthOfYear();
                default :
                    throw new IndexOutOfBoundsException();
            }
        }

        public int size() {
            return 2;
        }

        public int getValue(int index) {
            return val[index];
        }

        public void setValue(int index, int value) {
            val[index] = value;
        }

        public Chronology getChronology() {
            return BuddhistChronology.getInstanceUTC();
        }
    }

    static class MockProperty0 extends AbstractPartialFieldProperty {
        TestAbstractPartial.MockPartial partial = new TestAbstractPartial.MockPartial();

        public DateTimeField getField() {
            return partial.getField(0);
        }

        public ReadablePartial getReadablePartial() {
            return partial;
        }

        public int get() {
            return partial.getValue(0);
        }
    }

    static class MockProperty1 extends AbstractPartialFieldProperty {
        TestAbstractPartial.MockPartial partial = new TestAbstractPartial.MockPartial();

        public DateTimeField getField() {
            return partial.getField(1);
        }

        public ReadablePartial getReadablePartial() {
            return partial;
        }

        public int get() {
            return partial.getValue(1);
        }
    }

    static class MockProperty0Field extends TestAbstractPartial.MockProperty0 {
        public DateTimeField getField() {
            return BuddhistChronology.getInstanceUTC().hourOfDay();
        }
    }

    static class MockProperty0Val extends TestAbstractPartial.MockProperty0 {
        public int get() {
            return 99;
        }
    }

    static class MockProperty0Chrono extends TestAbstractPartial.MockProperty0 {
        public ReadablePartial getReadablePartial() {
            return new TestAbstractPartial.MockPartial() {
                public Chronology getChronology() {
                    return ISOChronology.getInstanceUTC();
                }
            };
        }
    }
}

