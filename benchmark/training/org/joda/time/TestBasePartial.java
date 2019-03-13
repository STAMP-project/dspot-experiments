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
import org.joda.time.base.BasePartial;

import static DateTimeConstants.MILLIS_PER_DAY;
import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeConstants.MILLIS_PER_MINUTE;


/**
 * This class is a Junit unit test for YearMonthDay.
 *
 * @author Stephen Colebourne
 */
public class TestBasePartial extends TestCase {
    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private long TEST_TIME_NOW = ((((((31L + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (MILLIS_PER_DAY);

    private long TEST_TIME1 = ((((((31L + 28L) + 31L) + 6L) - 1L) * (MILLIS_PER_DAY)) + (12L * (MILLIS_PER_HOUR))) + (24L * (MILLIS_PER_MINUTE));

    private long TEST_TIME2 = ((((((((365L + 31L) + 28L) + 31L) + 30L) + 7L) - 1L) * (MILLIS_PER_DAY)) + (14L * (MILLIS_PER_HOUR))) + (28L * (MILLIS_PER_MINUTE));

    private DateTimeZone zone = null;

    public TestBasePartial(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testSetMethods() throws Throwable {
        TestBasePartial.MockPartial mock = new TestBasePartial.MockPartial();
        TestCase.assertEquals(1970, mock.getYear());
        TestCase.assertEquals(1, mock.getMonthOfYear());
        mock.setYear(2004);
        TestCase.assertEquals(2004, mock.getYear());
        TestCase.assertEquals(1, mock.getMonthOfYear());
        mock.setMonthOfYear(6);
        TestCase.assertEquals(2004, mock.getYear());
        TestCase.assertEquals(6, mock.getMonthOfYear());
        mock.set(2005, 5);
        TestCase.assertEquals(2005, mock.getYear());
        TestCase.assertEquals(5, mock.getMonthOfYear());
        try {
            mock.setMonthOfYear(0);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals(2005, mock.getYear());
        TestCase.assertEquals(5, mock.getMonthOfYear());
        try {
            mock.setMonthOfYear(13);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        TestCase.assertEquals(2005, mock.getYear());
        TestCase.assertEquals(5, mock.getMonthOfYear());
    }

    static class MockPartial extends BasePartial {
        MockPartial() {
            super(new int[]{ 1970, 1 }, null);
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

        public int getYear() {
            return getValue(0);
        }

        public void setYear(int year) {
            setValue(0, year);
        }

        public int getMonthOfYear() {
            return getValue(1);
        }

        public void setMonthOfYear(int month) {
            setValue(1, month);
        }

        public void set(int year, int month) {
            setValues(new int[]{ year, month });
        }
    }
}

