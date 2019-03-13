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


import DateTimeZone.UTC;
import junit.framework.TestCase;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.tz.DateTimeZoneBuilder;

import static DateTimeConstants.MILLIS_PER_HOUR;
import static DateTimeZone.UTC;


/**
 * This class is a JUnit test for DateTimeZone.
 *
 * @author Stephen Colebourne
 */
public class TestDateTimeZoneCutover extends TestCase {
    public TestDateTimeZoneCutover(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    // ------------------------ Bug [1710316] --------------------------------
    // -----------------------------------------------------------------------
    // The behaviour of getOffsetFromLocal is defined in its javadoc
    // However, this definition doesn't work for all DateTimeField operations
    /**
     * Mock zone simulating Asia/Gaza cutover at midnight 2007-04-01
     */
    private static long CUTOVER_GAZA = 1175378400000L;

    private static int OFFSET_GAZA = 7200000;// +02:00


    private static final DateTimeZone MOCK_GAZA = new MockZone(TestDateTimeZoneCutover.CUTOVER_GAZA, TestDateTimeZoneCutover.OFFSET_GAZA, 3600);

    // -----------------------------------------------------------------------
    public void test_MockGazaIsCorrect() {
        DateTime pre = new DateTime(((TestDateTimeZoneCutover.CUTOVER_GAZA) - 1L), TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-03-31T23:59:59.999+02:00", pre.toString());
        DateTime at = new DateTime(TestDateTimeZoneCutover.CUTOVER_GAZA, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", at.toString());
        DateTime post = new DateTime(((TestDateTimeZoneCutover.CUTOVER_GAZA) + 1L), TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-01T01:00:00.001+03:00", post.toString());
    }

    public void test_getOffsetFromLocal_Gaza() {
        doTest_getOffsetFromLocal_Gaza((-1), 23, 0, "2007-03-31T23:00:00.000+02:00");
        doTest_getOffsetFromLocal_Gaza((-1), 23, 30, "2007-03-31T23:30:00.000+02:00");
        doTest_getOffsetFromLocal_Gaza(0, 0, 0, "2007-04-01T01:00:00.000+03:00");
        doTest_getOffsetFromLocal_Gaza(0, 0, 30, "2007-04-01T01:30:00.000+03:00");
        doTest_getOffsetFromLocal_Gaza(0, 1, 0, "2007-04-01T01:00:00.000+03:00");
        doTest_getOffsetFromLocal_Gaza(0, 1, 30, "2007-04-01T01:30:00.000+03:00");
        doTest_getOffsetFromLocal_Gaza(0, 2, 0, "2007-04-01T02:00:00.000+03:00");
        doTest_getOffsetFromLocal_Gaza(0, 3, 0, "2007-04-01T03:00:00.000+03:00");
        doTest_getOffsetFromLocal_Gaza(0, 4, 0, "2007-04-01T04:00:00.000+03:00");
        doTest_getOffsetFromLocal_Gaza(0, 5, 0, "2007-04-01T05:00:00.000+03:00");
        doTest_getOffsetFromLocal_Gaza(0, 6, 0, "2007-04-01T06:00:00.000+03:00");
    }

    public void test_DateTime_roundFloor_Gaza() {
        DateTime dt = new DateTime(2007, 4, 1, 8, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-01T08:00:00.000+03:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundFloorCopy();
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_Gaza() {
        DateTime dt = new DateTime(2007, 3, 31, 20, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-03-31T20:00:00.000+02:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundCeilingCopy();
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", rounded.toString());
    }

    public void test_DateTime_setHourZero_Gaza() {
        DateTime dt = new DateTime(2007, 4, 1, 8, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-01T08:00:00.000+03:00", dt.toString());
        try {
            dt.hourOfDay().setCopy(0);
            TestCase.fail();
        } catch (IllegalFieldValueException ex) {
            // expected
        }
    }

    public void test_DateTime_withHourZero_Gaza() {
        DateTime dt = new DateTime(2007, 4, 1, 8, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-01T08:00:00.000+03:00", dt.toString());
        try {
            dt.withHourOfDay(0);
            TestCase.fail();
        } catch (IllegalFieldValueException ex) {
            // expected
        }
    }

    public void test_DateTime_withDay_Gaza() {
        DateTime dt = new DateTime(2007, 4, 2, 0, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-02T00:00:00.000+03:00", dt.toString());
        DateTime res = dt.withDayOfMonth(1);
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", res.toString());
    }

    public void test_DateTime_minusHour_Gaza() {
        DateTime dt = new DateTime(2007, 4, 1, 8, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-01T08:00:00.000+03:00", dt.toString());
        DateTime minus7 = dt.minusHours(7);
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", minus7.toString());
        DateTime minus8 = dt.minusHours(8);
        TestCase.assertEquals("2007-03-31T23:00:00.000+02:00", minus8.toString());
        DateTime minus9 = dt.minusHours(9);
        TestCase.assertEquals("2007-03-31T22:00:00.000+02:00", minus9.toString());
    }

    public void test_DateTime_plusHour_Gaza() {
        DateTime dt = new DateTime(2007, 3, 31, 16, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-03-31T16:00:00.000+02:00", dt.toString());
        DateTime plus7 = dt.plusHours(7);
        TestCase.assertEquals("2007-03-31T23:00:00.000+02:00", plus7.toString());
        DateTime plus8 = dt.plusHours(8);
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", plus8.toString());
        DateTime plus9 = dt.plusHours(9);
        TestCase.assertEquals("2007-04-01T02:00:00.000+03:00", plus9.toString());
    }

    public void test_DateTime_minusDay_Gaza() {
        DateTime dt = new DateTime(2007, 4, 2, 0, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-02T00:00:00.000+03:00", dt.toString());
        DateTime minus1 = dt.minusDays(1);
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", minus1.toString());
        DateTime minus2 = dt.minusDays(2);
        TestCase.assertEquals("2007-03-31T00:00:00.000+02:00", minus2.toString());
    }

    public void test_DateTime_plusDay_Gaza() {
        DateTime dt = new DateTime(2007, 3, 31, 0, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-03-31T00:00:00.000+02:00", dt.toString());
        DateTime plus1 = dt.plusDays(1);
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", plus1.toString());
        DateTime plus2 = dt.plusDays(2);
        TestCase.assertEquals("2007-04-02T00:00:00.000+03:00", plus2.toString());
    }

    public void test_DateTime_plusDayMidGap_Gaza() {
        DateTime dt = new DateTime(2007, 3, 31, 0, 30, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-03-31T00:30:00.000+02:00", dt.toString());
        DateTime plus1 = dt.plusDays(1);
        TestCase.assertEquals("2007-04-01T01:30:00.000+03:00", plus1.toString());
        DateTime plus2 = dt.plusDays(2);
        TestCase.assertEquals("2007-04-02T00:30:00.000+03:00", plus2.toString());
    }

    public void test_DateTime_addWrapFieldDay_Gaza() {
        DateTime dt = new DateTime(2007, 4, 30, 0, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-30T00:00:00.000+03:00", dt.toString());
        DateTime plus1 = dt.dayOfMonth().addWrapFieldToCopy(1);
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", plus1.toString());
        DateTime plus2 = dt.dayOfMonth().addWrapFieldToCopy(2);
        TestCase.assertEquals("2007-04-02T00:00:00.000+03:00", plus2.toString());
    }

    public void test_DateTime_withZoneRetainFields_Gaza() {
        DateTime dt = new DateTime(2007, 4, 1, 0, 0, 0, 0, UTC);
        TestCase.assertEquals("2007-04-01T00:00:00.000Z", dt.toString());
        DateTime res = dt.withZoneRetainFields(TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", res.toString());
    }

    public void test_MutableDateTime_withZoneRetainFields_Gaza() {
        MutableDateTime dt = new MutableDateTime(2007, 4, 1, 0, 0, 0, 0, UTC);
        TestCase.assertEquals("2007-04-01T00:00:00.000Z", dt.toString());
        dt.setZoneRetainFields(TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-01T01:00:00.000+03:00", dt.toString());
    }

    public void test_LocalDate_new_Gaza() {
        LocalDate date1 = new LocalDate(TestDateTimeZoneCutover.CUTOVER_GAZA, TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-04-01", date1.toString());
        LocalDate date2 = new LocalDate(((TestDateTimeZoneCutover.CUTOVER_GAZA) - 1), TestDateTimeZoneCutover.MOCK_GAZA);
        TestCase.assertEquals("2007-03-31", date2.toString());
    }

    public void test_LocalDate_toDateMidnight_Gaza() {
        LocalDate date = new LocalDate(2007, 4, 1);
        try {
            date.toDateMidnight(TestDateTimeZoneCutover.MOCK_GAZA);
            TestCase.fail();
        } catch (IllegalInstantException ex) {
            TestCase.assertEquals(true, ex.getMessage().startsWith("Illegal instant due to time zone offset transition"));
        }
    }

    public void test_DateTime_new_Gaza() {
        try {
            new DateTime(2007, 4, 1, 0, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
            TestCase.fail();
        } catch (IllegalInstantException ex) {
            TestCase.assertEquals(true, ((ex.getMessage().indexOf("Illegal instant due to time zone offset transition")) >= 0));
        }
    }

    public void test_DateTime_newValid_Gaza() {
        new DateTime(2007, 3, 31, 19, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        new DateTime(2007, 3, 31, 20, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        new DateTime(2007, 3, 31, 21, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        new DateTime(2007, 3, 31, 22, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        new DateTime(2007, 3, 31, 23, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        new DateTime(2007, 4, 1, 1, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        new DateTime(2007, 4, 1, 2, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
        new DateTime(2007, 4, 1, 3, 0, 0, 0, TestDateTimeZoneCutover.MOCK_GAZA);
    }

    public void test_DateTime_parse_Gaza() {
        try {
            new DateTime("2007-04-01T00:00", TestDateTimeZoneCutover.MOCK_GAZA);
            TestCase.fail();
        } catch (IllegalInstantException ex) {
            TestCase.assertEquals(true, ((ex.getMessage().indexOf("Illegal instant due to time zone offset transition")) >= 0));
        }
    }

    // -----------------------------------------------------------------------
    // ------------------------ Bug [1710316] --------------------------------
    // -----------------------------------------------------------------------
    /**
     * Mock zone simulating America/Grand_Turk cutover at midnight 2007-04-01
     */
    private static long CUTOVER_TURK = 1175403600000L;

    private static int OFFSET_TURK = -18000000;// -05:00


    private static final DateTimeZone MOCK_TURK = new MockZone(TestDateTimeZoneCutover.CUTOVER_TURK, TestDateTimeZoneCutover.OFFSET_TURK, 3600);

    // -----------------------------------------------------------------------
    public void test_MockTurkIsCorrect() {
        DateTime pre = new DateTime(((TestDateTimeZoneCutover.CUTOVER_TURK) - 1L), TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-03-31T23:59:59.999-05:00", pre.toString());
        DateTime at = new DateTime(TestDateTimeZoneCutover.CUTOVER_TURK, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", at.toString());
        DateTime post = new DateTime(((TestDateTimeZoneCutover.CUTOVER_TURK) + 1L), TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-01T01:00:00.001-04:00", post.toString());
    }

    public void test_getOffsetFromLocal_Turk() {
        doTest_getOffsetFromLocal_Turk((-1), 23, 0, "2007-03-31T23:00:00.000-05:00", (-5));
        doTest_getOffsetFromLocal_Turk((-1), 23, 30, "2007-03-31T23:30:00.000-05:00", (-5));
        doTest_getOffsetFromLocal_Turk(0, 0, 0, "2007-04-01T01:00:00.000-04:00", (-5));
        doTest_getOffsetFromLocal_Turk(0, 0, 30, "2007-04-01T01:30:00.000-04:00", (-5));
        doTest_getOffsetFromLocal_Turk(0, 1, 0, "2007-04-01T01:00:00.000-04:00", (-4));
        doTest_getOffsetFromLocal_Turk(0, 1, 30, "2007-04-01T01:30:00.000-04:00", (-4));
        doTest_getOffsetFromLocal_Turk(0, 2, 0, "2007-04-01T02:00:00.000-04:00", (-4));
        doTest_getOffsetFromLocal_Turk(0, 3, 0, "2007-04-01T03:00:00.000-04:00", (-4));
        doTest_getOffsetFromLocal_Turk(0, 4, 0, "2007-04-01T04:00:00.000-04:00", (-4));
        doTest_getOffsetFromLocal_Turk(0, 5, 0, "2007-04-01T05:00:00.000-04:00", (-4));
        doTest_getOffsetFromLocal_Turk(0, 6, 0, "2007-04-01T06:00:00.000-04:00", (-4));
    }

    public void test_DateTime_roundFloor_Turk() {
        DateTime dt = new DateTime(2007, 4, 1, 8, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-01T08:00:00.000-04:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundFloorCopy();
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundFloorNotDST_Turk() {
        DateTime dt = new DateTime(2007, 4, 2, 8, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-02T08:00:00.000-04:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundFloorCopy();
        TestCase.assertEquals("2007-04-02T00:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_Turk() {
        DateTime dt = new DateTime(2007, 3, 31, 20, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-03-31T20:00:00.000-05:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundCeilingCopy();
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_setHourZero_Turk() {
        DateTime dt = new DateTime(2007, 4, 1, 8, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-01T08:00:00.000-04:00", dt.toString());
        try {
            dt.hourOfDay().setCopy(0);
            TestCase.fail();
        } catch (IllegalFieldValueException ex) {
            // expected
        }
    }

    public void test_DateTime_withHourZero_Turk() {
        DateTime dt = new DateTime(2007, 4, 1, 8, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-01T08:00:00.000-04:00", dt.toString());
        try {
            dt.withHourOfDay(0);
            TestCase.fail();
        } catch (IllegalFieldValueException ex) {
            // expected
        }
    }

    public void test_DateTime_withDay_Turk() {
        DateTime dt = new DateTime(2007, 4, 2, 0, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-02T00:00:00.000-04:00", dt.toString());
        DateTime res = dt.withDayOfMonth(1);
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", res.toString());
    }

    public void test_DateTime_minusHour_Turk() {
        DateTime dt = new DateTime(2007, 4, 1, 8, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-01T08:00:00.000-04:00", dt.toString());
        DateTime minus7 = dt.minusHours(7);
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", minus7.toString());
        DateTime minus8 = dt.minusHours(8);
        TestCase.assertEquals("2007-03-31T23:00:00.000-05:00", minus8.toString());
        DateTime minus9 = dt.minusHours(9);
        TestCase.assertEquals("2007-03-31T22:00:00.000-05:00", minus9.toString());
    }

    public void test_DateTime_plusHour_Turk() {
        DateTime dt = new DateTime(2007, 3, 31, 16, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-03-31T16:00:00.000-05:00", dt.toString());
        DateTime plus7 = dt.plusHours(7);
        TestCase.assertEquals("2007-03-31T23:00:00.000-05:00", plus7.toString());
        DateTime plus8 = dt.plusHours(8);
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", plus8.toString());
        DateTime plus9 = dt.plusHours(9);
        TestCase.assertEquals("2007-04-01T02:00:00.000-04:00", plus9.toString());
    }

    public void test_DateTime_minusDay_Turk() {
        DateTime dt = new DateTime(2007, 4, 2, 0, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-02T00:00:00.000-04:00", dt.toString());
        DateTime minus1 = dt.minusDays(1);
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", minus1.toString());
        DateTime minus2 = dt.minusDays(2);
        TestCase.assertEquals("2007-03-31T00:00:00.000-05:00", minus2.toString());
    }

    public void test_DateTime_plusDay_Turk() {
        DateTime dt = new DateTime(2007, 3, 31, 0, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-03-31T00:00:00.000-05:00", dt.toString());
        DateTime plus1 = dt.plusDays(1);
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", plus1.toString());
        DateTime plus2 = dt.plusDays(2);
        TestCase.assertEquals("2007-04-02T00:00:00.000-04:00", plus2.toString());
    }

    public void test_DateTime_plusDayMidGap_Turk() {
        DateTime dt = new DateTime(2007, 3, 31, 0, 30, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-03-31T00:30:00.000-05:00", dt.toString());
        DateTime plus1 = dt.plusDays(1);
        TestCase.assertEquals("2007-04-01T01:30:00.000-04:00", plus1.toString());
        DateTime plus2 = dt.plusDays(2);
        TestCase.assertEquals("2007-04-02T00:30:00.000-04:00", plus2.toString());
    }

    public void test_DateTime_addWrapFieldDay_Turk() {
        DateTime dt = new DateTime(2007, 4, 30, 0, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-30T00:00:00.000-04:00", dt.toString());
        DateTime plus1 = dt.dayOfMonth().addWrapFieldToCopy(1);
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", plus1.toString());
        DateTime plus2 = dt.dayOfMonth().addWrapFieldToCopy(2);
        TestCase.assertEquals("2007-04-02T00:00:00.000-04:00", plus2.toString());
    }

    public void test_DateTime_withZoneRetainFields_Turk() {
        DateTime dt = new DateTime(2007, 4, 1, 0, 0, 0, 0, UTC);
        TestCase.assertEquals("2007-04-01T00:00:00.000Z", dt.toString());
        DateTime res = dt.withZoneRetainFields(TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", res.toString());
    }

    public void test_MutableDateTime_setZoneRetainFields_Turk() {
        MutableDateTime dt = new MutableDateTime(2007, 4, 1, 0, 0, 0, 0, UTC);
        TestCase.assertEquals("2007-04-01T00:00:00.000Z", dt.toString());
        dt.setZoneRetainFields(TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-01T01:00:00.000-04:00", dt.toString());
    }

    public void test_LocalDate_new_Turk() {
        LocalDate date1 = new LocalDate(TestDateTimeZoneCutover.CUTOVER_TURK, TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-04-01", date1.toString());
        LocalDate date2 = new LocalDate(((TestDateTimeZoneCutover.CUTOVER_TURK) - 1), TestDateTimeZoneCutover.MOCK_TURK);
        TestCase.assertEquals("2007-03-31", date2.toString());
    }

    public void test_LocalDate_toDateMidnight_Turk() {
        LocalDate date = new LocalDate(2007, 4, 1);
        try {
            date.toDateMidnight(TestDateTimeZoneCutover.MOCK_TURK);
            TestCase.fail();
        } catch (IllegalInstantException ex) {
            TestCase.assertEquals(true, ex.getMessage().startsWith("Illegal instant due to time zone offset transition"));
        }
    }

    public void test_DateTime_new_Turk() {
        try {
            new DateTime(2007, 4, 1, 0, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
            TestCase.fail();
        } catch (IllegalInstantException ex) {
            TestCase.assertEquals(true, ((ex.getMessage().indexOf("Illegal instant due to time zone offset transition")) >= 0));
        }
    }

    public void test_DateTime_newValid_Turk() {
        new DateTime(2007, 3, 31, 23, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        new DateTime(2007, 4, 1, 1, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        new DateTime(2007, 4, 1, 2, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        new DateTime(2007, 4, 1, 3, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        new DateTime(2007, 4, 1, 4, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        new DateTime(2007, 4, 1, 5, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
        new DateTime(2007, 4, 1, 6, 0, 0, 0, TestDateTimeZoneCutover.MOCK_TURK);
    }

    public void test_DateTime_parse_Turk() {
        try {
            new DateTime("2007-04-01T00:00", TestDateTimeZoneCutover.MOCK_TURK);
            TestCase.fail();
        } catch (IllegalInstantException ex) {
            TestCase.assertEquals(true, ((ex.getMessage().indexOf("Illegal instant due to time zone offset transition")) >= 0));
        }
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * America/New_York cutover from 01:59 to 03:00 on 2007-03-11
     */
    private static long CUTOVER_NEW_YORK_SPRING = 1173596400000L;// 2007-03-11T03:00:00.000-04:00


    private static final DateTimeZone ZONE_NEW_YORK = DateTimeZone.forID("America/New_York");

    // DateTime x = new DateTime(2007, 1, 1, 0, 0, 0, 0, ZONE_NEW_YORK);
    // System.out.println(ZONE_NEW_YORK.nextTransition(x.getMillis()));
    // DateTime y = new DateTime(ZONE_NEW_YORK.nextTransition(x.getMillis()), ZONE_NEW_YORK);
    // System.out.println(y);
    // -----------------------------------------------------------------------
    public void test_NewYorkIsCorrect_Spring() {
        DateTime pre = new DateTime(((TestDateTimeZoneCutover.CUTOVER_NEW_YORK_SPRING) - 1L), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T01:59:59.999-05:00", pre.toString());
        DateTime at = new DateTime(TestDateTimeZoneCutover.CUTOVER_NEW_YORK_SPRING, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T03:00:00.000-04:00", at.toString());
        DateTime post = new DateTime(((TestDateTimeZoneCutover.CUTOVER_NEW_YORK_SPRING) + 1L), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T03:00:00.001-04:00", post.toString());
    }

    public void test_getOffsetFromLocal_NewYork_Spring() {
        doTest_getOffsetFromLocal(3, 11, 1, 0, "2007-03-11T01:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(3, 11, 1, 30, "2007-03-11T01:30:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(3, 11, 2, 0, "2007-03-11T03:00:00.000-04:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(3, 11, 2, 30, "2007-03-11T03:30:00.000-04:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(3, 11, 3, 0, "2007-03-11T03:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(3, 11, 3, 30, "2007-03-11T03:30:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(3, 11, 4, 0, "2007-03-11T04:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(3, 11, 5, 0, "2007-03-11T05:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(3, 11, 6, 0, "2007-03-11T06:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(3, 11, 7, 0, "2007-03-11T07:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(3, 11, 8, 0, "2007-03-11T08:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
    }

    public void test_DateTime_setHourAcross_NewYork_Spring() {
        DateTime dt = new DateTime(2007, 3, 11, 0, 0, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T00:00:00.000-05:00", dt.toString());
        DateTime res = dt.hourOfDay().setCopy(4);
        TestCase.assertEquals("2007-03-11T04:00:00.000-04:00", res.toString());
    }

    public void test_DateTime_setHourForward_NewYork_Spring() {
        DateTime dt = new DateTime(2007, 3, 11, 0, 0, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T00:00:00.000-05:00", dt.toString());
        try {
            dt.hourOfDay().setCopy(2);
            TestCase.fail();
        } catch (IllegalFieldValueException ex) {
            // expected
        }
    }

    public void test_DateTime_setHourBack_NewYork_Spring() {
        DateTime dt = new DateTime(2007, 3, 11, 8, 0, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T08:00:00.000-04:00", dt.toString());
        try {
            dt.hourOfDay().setCopy(2);
            TestCase.fail();
        } catch (IllegalFieldValueException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    public void test_DateTime_roundFloor_day_NewYork_Spring_preCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 1, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T01:30:00.000-05:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundFloorCopy();
        TestCase.assertEquals("2007-03-11T00:00:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_day_NewYork_Spring_postCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 3, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T03:30:00.000-04:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundFloorCopy();
        TestCase.assertEquals("2007-03-11T00:00:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_hour_NewYork_Spring_preCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 1, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T01:30:00.000-05:00", dt.toString());
        DateTime rounded = dt.hourOfDay().roundFloorCopy();
        TestCase.assertEquals("2007-03-11T01:00:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_hour_NewYork_Spring_postCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 3, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T03:30:00.000-04:00", dt.toString());
        DateTime rounded = dt.hourOfDay().roundFloorCopy();
        TestCase.assertEquals("2007-03-11T03:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_minute_NewYork_Spring_preCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 1, 30, 40, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T01:30:40.000-05:00", dt.toString());
        DateTime rounded = dt.minuteOfHour().roundFloorCopy();
        TestCase.assertEquals("2007-03-11T01:30:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_minute_NewYork_Spring_postCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 3, 30, 40, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T03:30:40.000-04:00", dt.toString());
        DateTime rounded = dt.minuteOfHour().roundFloorCopy();
        TestCase.assertEquals("2007-03-11T03:30:00.000-04:00", rounded.toString());
    }

    // -----------------------------------------------------------------------
    public void test_DateTime_roundCeiling_day_NewYork_Spring_preCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 1, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T01:30:00.000-05:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundCeilingCopy();
        TestCase.assertEquals("2007-03-12T00:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_day_NewYork_Spring_postCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 3, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T03:30:00.000-04:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundCeilingCopy();
        TestCase.assertEquals("2007-03-12T00:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_hour_NewYork_Spring_preCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 1, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T01:30:00.000-05:00", dt.toString());
        DateTime rounded = dt.hourOfDay().roundCeilingCopy();
        TestCase.assertEquals("2007-03-11T03:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_hour_NewYork_Spring_postCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 3, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T03:30:00.000-04:00", dt.toString());
        DateTime rounded = dt.hourOfDay().roundCeilingCopy();
        TestCase.assertEquals("2007-03-11T04:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_minute_NewYork_Spring_preCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 1, 30, 40, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T01:30:40.000-05:00", dt.toString());
        DateTime rounded = dt.minuteOfHour().roundCeilingCopy();
        TestCase.assertEquals("2007-03-11T01:31:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_minute_NewYork_Spring_postCutover() {
        DateTime dt = new DateTime(2007, 3, 11, 3, 30, 40, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-03-11T03:30:40.000-04:00", dt.toString());
        DateTime rounded = dt.minuteOfHour().roundCeilingCopy();
        TestCase.assertEquals("2007-03-11T03:31:00.000-04:00", rounded.toString());
    }

    // -----------------------------------------------------------------------
    /**
     * America/New_York cutover from 01:59 to 01:00 on 2007-11-04
     */
    private static long CUTOVER_NEW_YORK_AUTUMN = 1194156000000L;// 2007-11-04T01:00:00.000-05:00


    // -----------------------------------------------------------------------
    public void test_NewYorkIsCorrect_Autumn() {
        DateTime pre = new DateTime(((TestDateTimeZoneCutover.CUTOVER_NEW_YORK_AUTUMN) - 1L), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:59:59.999-04:00", pre.toString());
        DateTime at = new DateTime(TestDateTimeZoneCutover.CUTOVER_NEW_YORK_AUTUMN, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:00:00.000-05:00", at.toString());
        DateTime post = new DateTime(((TestDateTimeZoneCutover.CUTOVER_NEW_YORK_AUTUMN) + 1L), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:00:00.001-05:00", post.toString());
    }

    public void test_getOffsetFromLocal_NewYork_Autumn() {
        doTest_getOffsetFromLocal(11, 4, 0, 0, "2007-11-04T00:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 0, 30, "2007-11-04T00:30:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 1, 0, "2007-11-04T01:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 1, 30, "2007-11-04T01:30:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 2, 0, "2007-11-04T02:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 2, 30, "2007-11-04T02:30:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 3, 0, "2007-11-04T03:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 3, 30, "2007-11-04T03:30:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 4, 0, "2007-11-04T04:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 5, 0, "2007-11-04T05:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 6, 0, "2007-11-04T06:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 7, 0, "2007-11-04T07:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
        doTest_getOffsetFromLocal(11, 4, 8, 0, "2007-11-04T08:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_NEW_YORK);
    }

    public void test_DateTime_constructor_NewYork_Autumn() {
        DateTime dt = new DateTime(2007, 11, 4, 1, 30, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:00.000-04:00", dt.toString());
    }

    public void test_DateTime_plusHour_NewYork_Autumn() {
        DateTime dt = new DateTime(2007, 11, 3, 18, 0, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-03T18:00:00.000-04:00", dt.toString());
        DateTime plus6 = dt.plusHours(6);
        TestCase.assertEquals("2007-11-04T00:00:00.000-04:00", plus6.toString());
        DateTime plus7 = dt.plusHours(7);
        TestCase.assertEquals("2007-11-04T01:00:00.000-04:00", plus7.toString());
        DateTime plus8 = dt.plusHours(8);
        TestCase.assertEquals("2007-11-04T01:00:00.000-05:00", plus8.toString());
        DateTime plus9 = dt.plusHours(9);
        TestCase.assertEquals("2007-11-04T02:00:00.000-05:00", plus9.toString());
    }

    public void test_DateTime_minusHour_NewYork_Autumn() {
        DateTime dt = new DateTime(2007, 11, 4, 8, 0, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T08:00:00.000-05:00", dt.toString());
        DateTime minus6 = dt.minusHours(6);
        TestCase.assertEquals("2007-11-04T02:00:00.000-05:00", minus6.toString());
        DateTime minus7 = dt.minusHours(7);
        TestCase.assertEquals("2007-11-04T01:00:00.000-05:00", minus7.toString());
        DateTime minus8 = dt.minusHours(8);
        TestCase.assertEquals("2007-11-04T01:00:00.000-04:00", minus8.toString());
        DateTime minus9 = dt.minusHours(9);
        TestCase.assertEquals("2007-11-04T00:00:00.000-04:00", minus9.toString());
    }

    // -----------------------------------------------------------------------
    public void test_DateTime_roundFloor_day_NewYork_Autumn_preCutover() {
        DateTime dt = new DateTime(2007, 11, 4, 1, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:00.000-04:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundFloorCopy();
        TestCase.assertEquals("2007-11-04T00:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_day_NewYork_Autumn_postCutover() {
        DateTime dt = plusHours(1);
        TestCase.assertEquals("2007-11-04T01:30:00.000-05:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundFloorCopy();
        TestCase.assertEquals("2007-11-04T00:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_hourOfDay_NewYork_Autumn_preCutover() {
        DateTime dt = new DateTime(2007, 11, 4, 1, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:00.000-04:00", dt.toString());
        DateTime rounded = dt.hourOfDay().roundFloorCopy();
        TestCase.assertEquals("2007-11-04T01:00:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_hourOfDay_NewYork_Autumn_postCutover() {
        DateTime dt = plusHours(1);
        TestCase.assertEquals("2007-11-04T01:30:00.000-05:00", dt.toString());
        DateTime rounded = dt.hourOfDay().roundFloorCopy();
        TestCase.assertEquals("2007-11-04T01:00:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_minuteOfHour_NewYork_Autumn_preCutover() {
        DateTime dt = new DateTime(2007, 11, 4, 1, 30, 40, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:40.000-04:00", dt.toString());
        DateTime rounded = dt.minuteOfHour().roundFloorCopy();
        TestCase.assertEquals("2007-11-04T01:30:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_minuteOfHour_NewYork_Autumn_postCutover() {
        DateTime dt = plusHours(1);
        TestCase.assertEquals("2007-11-04T01:30:40.000-05:00", dt.toString());
        DateTime rounded = dt.minuteOfHour().roundFloorCopy();
        TestCase.assertEquals("2007-11-04T01:30:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_secondOfMinute_NewYork_Autumn_preCutover() {
        DateTime dt = new DateTime(2007, 11, 4, 1, 30, 40, 500, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:40.500-04:00", dt.toString());
        DateTime rounded = dt.secondOfMinute().roundFloorCopy();
        TestCase.assertEquals("2007-11-04T01:30:40.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundFloor_secondOfMinute_NewYork_Autumn_postCutover() {
        DateTime dt = plusHours(1);
        TestCase.assertEquals("2007-11-04T01:30:40.500-05:00", dt.toString());
        DateTime rounded = dt.secondOfMinute().roundFloorCopy();
        TestCase.assertEquals("2007-11-04T01:30:40.000-05:00", rounded.toString());
    }

    // -----------------------------------------------------------------------
    public void test_DateTime_roundCeiling_day_NewYork_Autumn_preCutover() {
        DateTime dt = new DateTime(2007, 11, 4, 1, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:00.000-04:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundCeilingCopy();
        TestCase.assertEquals("2007-11-05T00:00:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_day_NewYork_Autumn_postCutover() {
        DateTime dt = plusHours(1);
        TestCase.assertEquals("2007-11-04T01:30:00.000-05:00", dt.toString());
        DateTime rounded = dt.dayOfMonth().roundCeilingCopy();
        TestCase.assertEquals("2007-11-05T00:00:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_hourOfDay_NewYork_Autumn_preCutover() {
        DateTime dt = new DateTime(2007, 11, 4, 1, 30, 0, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:00.000-04:00", dt.toString());
        DateTime rounded = dt.hourOfDay().roundCeilingCopy();
        TestCase.assertEquals("2007-11-04T01:00:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_hourOfDay_NewYork_Autumn_postCutover() {
        DateTime dt = plusHours(1);
        TestCase.assertEquals("2007-11-04T01:30:00.000-05:00", dt.toString());
        DateTime rounded = dt.hourOfDay().roundCeilingCopy();
        TestCase.assertEquals("2007-11-04T02:00:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_minuteOfHour_NewYork_Autumn_preCutover() {
        DateTime dt = new DateTime(2007, 11, 4, 1, 30, 40, 0, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:40.000-04:00", dt.toString());
        DateTime rounded = dt.minuteOfHour().roundCeilingCopy();
        TestCase.assertEquals("2007-11-04T01:31:00.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_minuteOfHour_NewYork_Autumn_postCutover() {
        DateTime dt = plusHours(1);
        TestCase.assertEquals("2007-11-04T01:30:40.000-05:00", dt.toString());
        DateTime rounded = dt.minuteOfHour().roundCeilingCopy();
        TestCase.assertEquals("2007-11-04T01:31:00.000-05:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_secondOfMinute_NewYork_Autumn_preCutover() {
        DateTime dt = new DateTime(2007, 11, 4, 1, 30, 40, 500, TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:40.500-04:00", dt.toString());
        DateTime rounded = dt.secondOfMinute().roundCeilingCopy();
        TestCase.assertEquals("2007-11-04T01:30:41.000-04:00", rounded.toString());
    }

    public void test_DateTime_roundCeiling_secondOfMinute_NewYork_Autumn_postCutover() {
        DateTime dt = plusHours(1);
        TestCase.assertEquals("2007-11-04T01:30:40.500-05:00", dt.toString());
        DateTime rounded = dt.secondOfMinute().roundCeilingCopy();
        TestCase.assertEquals("2007-11-04T01:30:41.000-05:00", rounded.toString());
    }

    // -----------------------------------------------------------------------
    /**
     * Europe/Moscow cutover from 01:59 to 03:00 on 2007-03-25
     */
    private static long CUTOVER_MOSCOW_SPRING = 1174777200000L;// 2007-03-25T03:00:00.000+04:00


    private static final DateTimeZone ZONE_MOSCOW = DateTimeZone.forID("Europe/Moscow");

    // -----------------------------------------------------------------------
    public void test_MoscowIsCorrect_Spring() {
        // DateTime x = new DateTime(2007, 7, 1, 0, 0, 0, 0, ZONE_MOSCOW);
        // System.out.println(ZONE_MOSCOW.nextTransition(x.getMillis()));
        // DateTime y = new DateTime(ZONE_MOSCOW.nextTransition(x.getMillis()), ZONE_MOSCOW);
        // System.out.println(y);
        DateTime pre = new DateTime(((TestDateTimeZoneCutover.CUTOVER_MOSCOW_SPRING) - 1L), TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-03-25T01:59:59.999+03:00", pre.toString());
        DateTime at = new DateTime(TestDateTimeZoneCutover.CUTOVER_MOSCOW_SPRING, TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-03-25T03:00:00.000+04:00", at.toString());
        DateTime post = new DateTime(((TestDateTimeZoneCutover.CUTOVER_MOSCOW_SPRING) + 1L), TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-03-25T03:00:00.001+04:00", post.toString());
    }

    public void test_getOffsetFromLocal_Moscow_Spring() {
        doTest_getOffsetFromLocal(3, 25, 1, 0, "2007-03-25T01:00:00.000+03:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(3, 25, 1, 30, "2007-03-25T01:30:00.000+03:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(3, 25, 2, 0, "2007-03-25T03:00:00.000+04:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(3, 25, 2, 30, "2007-03-25T03:30:00.000+04:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(3, 25, 3, 0, "2007-03-25T03:00:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(3, 25, 3, 30, "2007-03-25T03:30:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(3, 25, 4, 0, "2007-03-25T04:00:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(3, 25, 5, 0, "2007-03-25T05:00:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(3, 25, 6, 0, "2007-03-25T06:00:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(3, 25, 7, 0, "2007-03-25T07:00:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(3, 25, 8, 0, "2007-03-25T08:00:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
    }

    public void test_DateTime_setHourAcross_Moscow_Spring() {
        DateTime dt = new DateTime(2007, 3, 25, 0, 0, 0, 0, TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-03-25T00:00:00.000+03:00", dt.toString());
        DateTime res = dt.hourOfDay().setCopy(4);
        TestCase.assertEquals("2007-03-25T04:00:00.000+04:00", res.toString());
    }

    public void test_DateTime_setHourForward_Moscow_Spring() {
        DateTime dt = new DateTime(2007, 3, 25, 0, 0, 0, 0, TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-03-25T00:00:00.000+03:00", dt.toString());
        try {
            dt.hourOfDay().setCopy(2);
            TestCase.fail();
        } catch (IllegalFieldValueException ex) {
            // expected
        }
    }

    public void test_DateTime_setHourBack_Moscow_Spring() {
        DateTime dt = new DateTime(2007, 3, 25, 8, 0, 0, 0, TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-03-25T08:00:00.000+04:00", dt.toString());
        try {
            dt.hourOfDay().setCopy(2);
            TestCase.fail();
        } catch (IllegalFieldValueException ex) {
            // expected
        }
    }

    // -----------------------------------------------------------------------
    /**
     * America/New_York cutover from 02:59 to 02:00 on 2007-10-28
     */
    private static long CUTOVER_MOSCOW_AUTUMN = 1193526000000L;// 2007-10-28T02:00:00.000+03:00


    // -----------------------------------------------------------------------
    public void test_MoscowIsCorrect_Autumn() {
        DateTime pre = new DateTime(((TestDateTimeZoneCutover.CUTOVER_MOSCOW_AUTUMN) - 1L), TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-10-28T02:59:59.999+04:00", pre.toString());
        DateTime at = new DateTime(TestDateTimeZoneCutover.CUTOVER_MOSCOW_AUTUMN, TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-10-28T02:00:00.000+03:00", at.toString());
        DateTime post = new DateTime(((TestDateTimeZoneCutover.CUTOVER_MOSCOW_AUTUMN) + 1L), TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-10-28T02:00:00.001+03:00", post.toString());
    }

    public void test_getOffsetFromLocal_Moscow_Autumn() {
        doTest_getOffsetFromLocal(10, 28, 0, 0, "2007-10-28T00:00:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 0, 30, "2007-10-28T00:30:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 1, 0, "2007-10-28T01:00:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 1, 30, "2007-10-28T01:30:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 2, 0, "2007-10-28T02:00:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 2, 30, "2007-10-28T02:30:00.000+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 2, 30, 59, 999, "2007-10-28T02:30:59.999+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 2, 59, 59, 998, "2007-10-28T02:59:59.998+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 2, 59, 59, 999, "2007-10-28T02:59:59.999+04:00", 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 3, 0, "2007-10-28T03:00:00.000+03:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 3, 30, "2007-10-28T03:30:00.000+03:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 4, 0, "2007-10-28T04:00:00.000+03:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 5, 0, "2007-10-28T05:00:00.000+03:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 6, 0, "2007-10-28T06:00:00.000+03:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 7, 0, "2007-10-28T07:00:00.000+03:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
        doTest_getOffsetFromLocal(10, 28, 8, 0, "2007-10-28T08:00:00.000+03:00", 3, TestDateTimeZoneCutover.ZONE_MOSCOW);
    }

    public void test_getOffsetFromLocal_Moscow_Autumn_overlap_mins() {
        for (int min = 0; min < 60; min++) {
            if (min < 10) {
                doTest_getOffsetFromLocal(10, 28, 2, min, (("2007-10-28T02:0" + min) + ":00.000+04:00"), 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
            } else {
                doTest_getOffsetFromLocal(10, 28, 2, min, (("2007-10-28T02:" + min) + ":00.000+04:00"), 4, TestDateTimeZoneCutover.ZONE_MOSCOW);
            }
        }
    }

    public void test_DateTime_constructor_Moscow_Autumn() {
        DateTime dt = new DateTime(2007, 10, 28, 2, 30, TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-10-28T02:30:00.000+04:00", dt.toString());
    }

    public void test_DateTime_plusHour_Moscow_Autumn() {
        DateTime dt = new DateTime(2007, 10, 27, 19, 0, 0, 0, TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-10-27T19:00:00.000+04:00", dt.toString());
        DateTime plus6 = dt.plusHours(6);
        TestCase.assertEquals("2007-10-28T01:00:00.000+04:00", plus6.toString());
        DateTime plus7 = dt.plusHours(7);
        TestCase.assertEquals("2007-10-28T02:00:00.000+04:00", plus7.toString());
        DateTime plus8 = dt.plusHours(8);
        TestCase.assertEquals("2007-10-28T02:00:00.000+03:00", plus8.toString());
        DateTime plus9 = dt.plusHours(9);
        TestCase.assertEquals("2007-10-28T03:00:00.000+03:00", plus9.toString());
    }

    public void test_DateTime_minusHour_Moscow_Autumn() {
        DateTime dt = new DateTime(2007, 10, 28, 9, 0, 0, 0, TestDateTimeZoneCutover.ZONE_MOSCOW);
        TestCase.assertEquals("2007-10-28T09:00:00.000+03:00", dt.toString());
        DateTime minus6 = dt.minusHours(6);
        TestCase.assertEquals("2007-10-28T03:00:00.000+03:00", minus6.toString());
        DateTime minus7 = dt.minusHours(7);
        TestCase.assertEquals("2007-10-28T02:00:00.000+03:00", minus7.toString());
        DateTime minus8 = dt.minusHours(8);
        TestCase.assertEquals("2007-10-28T02:00:00.000+04:00", minus8.toString());
        DateTime minus9 = dt.minusHours(9);
        TestCase.assertEquals("2007-10-28T01:00:00.000+04:00", minus9.toString());
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * America/Guatemala cutover from 23:59 to 23:00 on 2006-09-30
     */
    private static long CUTOVER_GUATEMALA_AUTUMN = 1159678800000L;// 2006-09-30T23:00:00.000-06:00


    private static final DateTimeZone ZONE_GUATEMALA = DateTimeZone.forID("America/Guatemala");

    // -----------------------------------------------------------------------
    public void test_GuatemataIsCorrect_Autumn() {
        DateTime pre = new DateTime(((TestDateTimeZoneCutover.CUTOVER_GUATEMALA_AUTUMN) - 1L), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        TestCase.assertEquals("2006-09-30T23:59:59.999-05:00", pre.toString());
        DateTime at = new DateTime(TestDateTimeZoneCutover.CUTOVER_GUATEMALA_AUTUMN, TestDateTimeZoneCutover.ZONE_GUATEMALA);
        TestCase.assertEquals("2006-09-30T23:00:00.000-06:00", at.toString());
        DateTime post = new DateTime(((TestDateTimeZoneCutover.CUTOVER_GUATEMALA_AUTUMN) + 1L), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        TestCase.assertEquals("2006-09-30T23:00:00.001-06:00", post.toString());
    }

    public void test_getOffsetFromLocal_Guatemata_Autumn() {
        doTest_getOffsetFromLocal(2006, 9, 30, 23, 0, "2006-09-30T23:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 9, 30, 23, 30, "2006-09-30T23:30:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 9, 30, 23, 0, "2006-09-30T23:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 9, 30, 23, 30, "2006-09-30T23:30:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 0, 0, "2006-10-01T00:00:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 0, 30, "2006-10-01T00:30:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 1, 0, "2006-10-01T01:00:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 1, 30, "2006-10-01T01:30:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 2, 0, "2006-10-01T02:00:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 2, 30, "2006-10-01T02:30:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 3, 0, "2006-10-01T03:00:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 3, 30, "2006-10-01T03:30:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 4, 0, "2006-10-01T04:00:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 4, 30, "2006-10-01T04:30:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 5, 0, "2006-10-01T05:00:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 5, 30, "2006-10-01T05:30:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 6, 0, "2006-10-01T06:00:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
        doTest_getOffsetFromLocal(2006, 10, 1, 6, 30, "2006-10-01T06:30:00.000-06:00", (-6), TestDateTimeZoneCutover.ZONE_GUATEMALA);
    }

    public void test_DateTime_plusHour_Guatemata_Autumn() {
        DateTime dt = new DateTime(2006, 9, 30, 20, 0, 0, 0, TestDateTimeZoneCutover.ZONE_GUATEMALA);
        TestCase.assertEquals("2006-09-30T20:00:00.000-05:00", dt.toString());
        DateTime plus1 = dt.plusHours(1);
        TestCase.assertEquals("2006-09-30T21:00:00.000-05:00", plus1.toString());
        DateTime plus2 = dt.plusHours(2);
        TestCase.assertEquals("2006-09-30T22:00:00.000-05:00", plus2.toString());
        DateTime plus3 = dt.plusHours(3);
        TestCase.assertEquals("2006-09-30T23:00:00.000-05:00", plus3.toString());
        DateTime plus4 = dt.plusHours(4);
        TestCase.assertEquals("2006-09-30T23:00:00.000-06:00", plus4.toString());
        DateTime plus5 = dt.plusHours(5);
        TestCase.assertEquals("2006-10-01T00:00:00.000-06:00", plus5.toString());
        DateTime plus6 = dt.plusHours(6);
        TestCase.assertEquals("2006-10-01T01:00:00.000-06:00", plus6.toString());
        DateTime plus7 = dt.plusHours(7);
        TestCase.assertEquals("2006-10-01T02:00:00.000-06:00", plus7.toString());
    }

    public void test_DateTime_minusHour_Guatemata_Autumn() {
        DateTime dt = new DateTime(2006, 10, 1, 2, 0, 0, 0, TestDateTimeZoneCutover.ZONE_GUATEMALA);
        TestCase.assertEquals("2006-10-01T02:00:00.000-06:00", dt.toString());
        DateTime minus1 = dt.minusHours(1);
        TestCase.assertEquals("2006-10-01T01:00:00.000-06:00", minus1.toString());
        DateTime minus2 = dt.minusHours(2);
        TestCase.assertEquals("2006-10-01T00:00:00.000-06:00", minus2.toString());
        DateTime minus3 = dt.minusHours(3);
        TestCase.assertEquals("2006-09-30T23:00:00.000-06:00", minus3.toString());
        DateTime minus4 = dt.minusHours(4);
        TestCase.assertEquals("2006-09-30T23:00:00.000-05:00", minus4.toString());
        DateTime minus5 = dt.minusHours(5);
        TestCase.assertEquals("2006-09-30T22:00:00.000-05:00", minus5.toString());
        DateTime minus6 = dt.minusHours(6);
        TestCase.assertEquals("2006-09-30T21:00:00.000-05:00", minus6.toString());
        DateTime minus7 = dt.minusHours(7);
        TestCase.assertEquals("2006-09-30T20:00:00.000-05:00", minus7.toString());
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    /**
     * America/Rio_Branco gap cutover from 2008-06-23T23:59-05:00 to 2008-06-24T01:00-04:00
     */
    private static long CUTOVER_RIO_BRANCO_AUTUMN = 1214283600000L;// 2008-06-24T01:00:00.000-04:00


    private static final DateTimeZone ZONE_RIO_BRANCO = DateTimeZone.forID("America/Rio_Branco");

    // -----------------------------------------------------------------------
    public void test_RioBrancoIsCorrect_Spring() {
        DateTime pre = new DateTime(((TestDateTimeZoneCutover.CUTOVER_RIO_BRANCO_AUTUMN) - 1L), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        TestCase.assertEquals("2008-06-23T23:59:59.999-05:00", pre.toString());
        DateTime at = new DateTime(TestDateTimeZoneCutover.CUTOVER_RIO_BRANCO_AUTUMN, TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        TestCase.assertEquals("2008-06-24T01:00:00.000-04:00", at.toString());
        DateTime post = new DateTime(((TestDateTimeZoneCutover.CUTOVER_RIO_BRANCO_AUTUMN) + 1L), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        TestCase.assertEquals("2008-06-24T01:00:00.001-04:00", post.toString());
    }

    public void test_getOffsetFromLocal_RioBranco_Spring() {
        doTest_getOffsetFromLocal(2008, 6, 23, 23, 0, "2008-06-23T23:00:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        doTest_getOffsetFromLocal(2008, 6, 23, 23, 30, "2008-06-23T23:30:00.000-05:00", (-5), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        doTest_getOffsetFromLocal(2008, 6, 24, 0, 0, "2008-06-24T01:00:00.000-04:00", (-5), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        doTest_getOffsetFromLocal(2008, 6, 24, 0, 30, "2008-06-24T01:30:00.000-04:00", (-5), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        doTest_getOffsetFromLocal(2008, 6, 24, 1, 0, "2008-06-24T01:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        doTest_getOffsetFromLocal(2008, 6, 24, 1, 30, "2008-06-24T01:30:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        doTest_getOffsetFromLocal(2008, 6, 24, 2, 0, "2008-06-24T02:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        doTest_getOffsetFromLocal(2008, 6, 24, 2, 30, "2008-06-24T02:30:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        doTest_getOffsetFromLocal(2008, 6, 24, 5, 0, "2008-06-24T05:00:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
        doTest_getOffsetFromLocal(2008, 6, 24, 5, 30, "2008-06-24T05:30:00.000-04:00", (-4), TestDateTimeZoneCutover.ZONE_RIO_BRANCO);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    public void test_DateTime_JustAfterLastEverOverlap() {
        // based on America/Argentina/Catamarca in file 2009s
        DateTimeZone zone = new DateTimeZoneBuilder().setStandardOffset(((-3) * (MILLIS_PER_HOUR))).addRecurringSavings("SUMMER", (1 * (MILLIS_PER_HOUR)), 2000, 2008, 'w', 4, 10, 0, true, (23 * (MILLIS_PER_HOUR))).addRecurringSavings("WINTER", 0, 2000, 2008, 'w', 8, 10, 0, true, (0 * (MILLIS_PER_HOUR))).toDateTimeZone("Zone", false);
        LocalDate date = new LocalDate(2008, 8, 10);
        TestCase.assertEquals("2008-08-10", date.toString());
        DateTime dt = date.toDateTimeAtStartOfDay(zone);
        TestCase.assertEquals("2008-08-10T00:00:00.000-03:00", dt.toString());
    }

    // public void test_toDateMidnight_SaoPaolo() {
    // // RFE: 1684259
    // DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
    // LocalDate baseDate = new LocalDate(2006, 11, 5);
    // DateMidnight dm = baseDate.toDateMidnight(zone);
    // assertEquals("2006-11-05T00:00:00.000-03:00", dm.toString());
    // DateTime dt = baseDate.toDateTimeAtMidnight(zone);
    // assertEquals("2006-11-05T00:00:00.000-03:00", dt.toString());
    // }
    // -----------------------------------------------------------------------
    private static final DateTimeZone ZONE_PARIS = DateTimeZone.forID("Europe/Paris");

    public void testWithMinuteOfHourInDstChange_mockZone() {
        DateTime cutover = new DateTime(2010, 10, 31, 1, 15, DateTimeZone.forOffsetHoursMinutes(0, 30));
        TestCase.assertEquals("2010-10-31T01:15:00.000+00:30", cutover.toString());
        DateTimeZone halfHourZone = new MockZone(cutover.getMillis(), 3600000, (-1800));
        DateTime pre = new DateTime(2010, 10, 31, 1, 0, halfHourZone);
        TestCase.assertEquals("2010-10-31T01:00:00.000+01:00", pre.toString());
        DateTime post = new DateTime(2010, 10, 31, 1, 59, halfHourZone);
        TestCase.assertEquals("2010-10-31T01:59:00.000+00:30", post.toString());
        DateTime testPre1 = pre.withMinuteOfHour(30);
        TestCase.assertEquals("2010-10-31T01:30:00.000+01:00", testPre1.toString());// retain offset

        DateTime testPre2 = pre.withMinuteOfHour(50);
        TestCase.assertEquals("2010-10-31T01:50:00.000+00:30", testPre2.toString());
        DateTime testPost1 = post.withMinuteOfHour(30);
        TestCase.assertEquals("2010-10-31T01:30:00.000+00:30", testPost1.toString());// retain offset

        DateTime testPost2 = post.withMinuteOfHour(10);
        TestCase.assertEquals("2010-10-31T01:10:00.000+01:00", testPost2.toString());
    }

    public void testWithHourOfDayInDstChange() {
        DateTime dateTime = new DateTime("2010-10-31T02:30:10.123+02:00", TestDateTimeZoneCutover.ZONE_PARIS);
        TestCase.assertEquals("2010-10-31T02:30:10.123+02:00", dateTime.toString());
        DateTime test = dateTime.withHourOfDay(2);
        TestCase.assertEquals("2010-10-31T02:30:10.123+02:00", test.toString());
    }

    public void testWithMinuteOfHourInDstChange() {
        DateTime dateTime = new DateTime("2010-10-31T02:30:10.123+02:00", TestDateTimeZoneCutover.ZONE_PARIS);
        TestCase.assertEquals("2010-10-31T02:30:10.123+02:00", dateTime.toString());
        DateTime test = dateTime.withMinuteOfHour(0);
        TestCase.assertEquals("2010-10-31T02:00:10.123+02:00", test.toString());
    }

    public void testWithSecondOfMinuteInDstChange() {
        DateTime dateTime = new DateTime("2010-10-31T02:30:10.123+02:00", TestDateTimeZoneCutover.ZONE_PARIS);
        TestCase.assertEquals("2010-10-31T02:30:10.123+02:00", dateTime.toString());
        DateTime test = dateTime.withSecondOfMinute(0);
        TestCase.assertEquals("2010-10-31T02:30:00.123+02:00", test.toString());
    }

    public void testWithMillisOfSecondInDstChange_Paris_summer() {
        DateTime dateTime = new DateTime("2010-10-31T02:30:10.123+02:00", TestDateTimeZoneCutover.ZONE_PARIS);
        TestCase.assertEquals("2010-10-31T02:30:10.123+02:00", dateTime.toString());
        DateTime test = dateTime.withMillisOfSecond(0);
        TestCase.assertEquals("2010-10-31T02:30:10.000+02:00", test.toString());
    }

    public void testWithMillisOfSecondInDstChange_Paris_winter() {
        DateTime dateTime = new DateTime("2010-10-31T02:30:10.123+01:00", TestDateTimeZoneCutover.ZONE_PARIS);
        TestCase.assertEquals("2010-10-31T02:30:10.123+01:00", dateTime.toString());
        DateTime test = dateTime.withMillisOfSecond(0);
        TestCase.assertEquals("2010-10-31T02:30:10.000+01:00", test.toString());
    }

    public void testWithMillisOfSecondInDstChange_NewYork_summer() {
        DateTime dateTime = new DateTime("2007-11-04T01:30:00.123-04:00", TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:00.123-04:00", dateTime.toString());
        DateTime test = dateTime.withMillisOfSecond(0);
        TestCase.assertEquals("2007-11-04T01:30:00.000-04:00", test.toString());
    }

    public void testWithMillisOfSecondInDstChange_NewYork_winter() {
        DateTime dateTime = new DateTime("2007-11-04T01:30:00.123-05:00", TestDateTimeZoneCutover.ZONE_NEW_YORK);
        TestCase.assertEquals("2007-11-04T01:30:00.123-05:00", dateTime.toString());
        DateTime test = dateTime.withMillisOfSecond(0);
        TestCase.assertEquals("2007-11-04T01:30:00.000-05:00", test.toString());
    }

    public void testPlusMinutesInDstChange() {
        DateTime dateTime = new DateTime("2010-10-31T02:30:10.123+02:00", TestDateTimeZoneCutover.ZONE_PARIS);
        TestCase.assertEquals("2010-10-31T02:30:10.123+02:00", dateTime.toString());
        DateTime test = dateTime.plusMinutes(1);
        TestCase.assertEquals("2010-10-31T02:31:10.123+02:00", test.toString());
    }

    public void testPlusSecondsInDstChange() {
        DateTime dateTime = new DateTime("2010-10-31T02:30:10.123+02:00", TestDateTimeZoneCutover.ZONE_PARIS);
        TestCase.assertEquals("2010-10-31T02:30:10.123+02:00", dateTime.toString());
        DateTime test = dateTime.plusSeconds(1);
        TestCase.assertEquals("2010-10-31T02:30:11.123+02:00", test.toString());
    }

    public void testPlusMillisInDstChange() {
        DateTime dateTime = new DateTime("2010-10-31T02:30:10.123+02:00", TestDateTimeZoneCutover.ZONE_PARIS);
        TestCase.assertEquals("2010-10-31T02:30:10.123+02:00", dateTime.toString());
        DateTime test = dateTime.plusMillis(1);
        TestCase.assertEquals("2010-10-31T02:30:10.124+02:00", test.toString());
    }

    public void testBug2182444_usCentral() {
        Chronology chronUSCentral = GregorianChronology.getInstance(DateTimeZone.forID("US/Central"));
        Chronology chronUTC = GregorianChronology.getInstance(UTC);
        DateTime usCentralStandardInUTC = new DateTime(2008, 11, 2, 7, 0, 0, 0, chronUTC);
        DateTime usCentralDaylightInUTC = new DateTime(2008, 11, 2, 6, 0, 0, 0, chronUTC);
        TestCase.assertTrue("Should be standard time", chronUSCentral.getZone().isStandardOffset(usCentralStandardInUTC.getMillis()));
        TestCase.assertFalse("Should be daylight time", chronUSCentral.getZone().isStandardOffset(usCentralDaylightInUTC.getMillis()));
        DateTime usCentralStandardInUSCentral = usCentralStandardInUTC.toDateTime(chronUSCentral);
        DateTime usCentralDaylightInUSCentral = usCentralDaylightInUTC.toDateTime(chronUSCentral);
        TestCase.assertEquals(1, usCentralStandardInUSCentral.getHourOfDay());
        TestCase.assertEquals(usCentralStandardInUSCentral.getHourOfDay(), usCentralDaylightInUSCentral.getHourOfDay());
        TestCase.assertTrue(((usCentralStandardInUSCentral.getMillis()) != (usCentralDaylightInUSCentral.getMillis())));
        TestCase.assertEquals(usCentralStandardInUSCentral, usCentralStandardInUSCentral.withHourOfDay(1));
        TestCase.assertEquals(((usCentralStandardInUSCentral.getMillis()) + 3), usCentralStandardInUSCentral.withMillisOfSecond(3).getMillis());
        TestCase.assertEquals(usCentralDaylightInUSCentral, usCentralDaylightInUSCentral.withHourOfDay(1));
        TestCase.assertEquals(((usCentralDaylightInUSCentral.getMillis()) + 3), usCentralDaylightInUSCentral.withMillisOfSecond(3).getMillis());
    }

    public void testBug2182444_ausNSW() {
        Chronology chronAusNSW = GregorianChronology.getInstance(DateTimeZone.forID("Australia/NSW"));
        Chronology chronUTC = GregorianChronology.getInstance(UTC);
        DateTime australiaNSWStandardInUTC = new DateTime(2008, 4, 5, 16, 0, 0, 0, chronUTC);
        DateTime australiaNSWDaylightInUTC = new DateTime(2008, 4, 5, 15, 0, 0, 0, chronUTC);
        TestCase.assertTrue("Should be standard time", chronAusNSW.getZone().isStandardOffset(australiaNSWStandardInUTC.getMillis()));
        TestCase.assertFalse("Should be daylight time", chronAusNSW.getZone().isStandardOffset(australiaNSWDaylightInUTC.getMillis()));
        DateTime australiaNSWStandardInAustraliaNSW = australiaNSWStandardInUTC.toDateTime(chronAusNSW);
        DateTime australiaNSWDaylightInAusraliaNSW = australiaNSWDaylightInUTC.toDateTime(chronAusNSW);
        TestCase.assertEquals(2, australiaNSWStandardInAustraliaNSW.getHourOfDay());
        TestCase.assertEquals(australiaNSWStandardInAustraliaNSW.getHourOfDay(), australiaNSWDaylightInAusraliaNSW.getHourOfDay());
        TestCase.assertTrue(((australiaNSWStandardInAustraliaNSW.getMillis()) != (australiaNSWDaylightInAusraliaNSW.getMillis())));
        TestCase.assertEquals(australiaNSWStandardInAustraliaNSW, australiaNSWStandardInAustraliaNSW.withHourOfDay(2));
        TestCase.assertEquals(((australiaNSWStandardInAustraliaNSW.getMillis()) + 3), australiaNSWStandardInAustraliaNSW.withMillisOfSecond(3).getMillis());
        TestCase.assertEquals(australiaNSWDaylightInAusraliaNSW, australiaNSWDaylightInAusraliaNSW.withHourOfDay(2));
        TestCase.assertEquals(((australiaNSWDaylightInAusraliaNSW.getMillis()) + 3), australiaNSWDaylightInAusraliaNSW.withMillisOfSecond(3).getMillis());
    }

    public void testPeriod() {
        DateTime a = new DateTime("2010-10-31T02:00:00.000+02:00", TestDateTimeZoneCutover.ZONE_PARIS);
        DateTime b = new DateTime("2010-10-31T02:01:00.000+02:00", TestDateTimeZoneCutover.ZONE_PARIS);
        Period period = new Period(a, b, PeriodType.standard());
        TestCase.assertEquals("PT1M", period.toString());
    }

    public void testForum4013394_retainOffsetWhenRetainFields_sameOffsetsDifferentZones() {
        final DateTimeZone fromDTZ = DateTimeZone.forID("Europe/London");
        final DateTimeZone toDTZ = DateTimeZone.forID("Europe/Lisbon");
        DateTime baseBefore = minusHours(1);
        DateTime baseAfter = new DateTime(2007, 10, 28, 1, 15, fromDTZ);
        DateTime testBefore = baseBefore.withZoneRetainFields(toDTZ);
        DateTime testAfter = baseAfter.withZoneRetainFields(toDTZ);
        // toString ignores time-zone but includes offset
        TestCase.assertEquals(baseBefore.toString(), testBefore.toString());
        TestCase.assertEquals(baseAfter.toString(), testAfter.toString());
    }

    // -------------------------------------------------------------------------
    public void testBug3192457_adjustOffset() {
        final DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
        DateTime base = new DateTime(2007, 10, 28, 3, 15, zone);
        DateTime baseBefore = base.minusHours(2);
        DateTime baseAfter = base.minusHours(1);
        TestCase.assertSame(base, base.withEarlierOffsetAtOverlap());
        TestCase.assertSame(base, base.withLaterOffsetAtOverlap());
        TestCase.assertSame(baseBefore, baseBefore.withEarlierOffsetAtOverlap());
        TestCase.assertEquals(baseAfter, baseBefore.withLaterOffsetAtOverlap());
        TestCase.assertSame(baseAfter, baseAfter.withLaterOffsetAtOverlap());
        TestCase.assertEquals(baseBefore, baseAfter.withEarlierOffsetAtOverlap());
    }

    public void testBug3476684_adjustOffset() {
        final DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
        DateTime base = new DateTime(2012, 2, 25, 22, 15, zone);
        DateTime baseBefore = base.plusHours(1);// 23:15 (first)

        DateTime baseAfter = base.plusHours(2);// 23:15 (second)

        TestCase.assertSame(base, base.withEarlierOffsetAtOverlap());
        TestCase.assertSame(base, base.withLaterOffsetAtOverlap());
        TestCase.assertSame(baseBefore, baseBefore.withEarlierOffsetAtOverlap());
        TestCase.assertEquals(baseAfter, baseBefore.withLaterOffsetAtOverlap());
        TestCase.assertSame(baseAfter, baseAfter.withLaterOffsetAtOverlap());
        TestCase.assertEquals(baseBefore, baseAfter.withEarlierOffsetAtOverlap());
    }

    public void testBug3476684_adjustOffset_springGap() {
        final DateTimeZone zone = DateTimeZone.forID("America/Sao_Paulo");
        DateTime base = new DateTime(2011, 10, 15, 22, 15, zone);
        DateTime baseBefore = base.plusHours(1);// 23:15

        DateTime baseAfter = base.plusHours(2);// 01:15

        TestCase.assertSame(base, base.withEarlierOffsetAtOverlap());
        TestCase.assertSame(base, base.withLaterOffsetAtOverlap());
        TestCase.assertSame(baseBefore, baseBefore.withEarlierOffsetAtOverlap());
        TestCase.assertEquals(baseBefore, baseBefore.withLaterOffsetAtOverlap());
        TestCase.assertSame(baseAfter, baseAfter.withLaterOffsetAtOverlap());
        TestCase.assertEquals(baseAfter, baseAfter.withEarlierOffsetAtOverlap());
    }

    // ensure Summer time picked
    // -----------------------------------------------------------------------
    public void testDateTimeCreation_athens() {
        DateTimeZone zone = DateTimeZone.forID("Europe/Athens");
        DateTime base = new DateTime(2011, 10, 30, 3, 15, zone);
        TestCase.assertEquals("2011-10-30T03:15:00.000+03:00", base.toString());
        TestCase.assertEquals("2011-10-30T03:15:00.000+02:00", base.plusHours(1).toString());
    }

    public void testDateTimeCreation_paris() {
        DateTimeZone zone = DateTimeZone.forID("Europe/Paris");
        DateTime base = new DateTime(2011, 10, 30, 2, 15, zone);
        TestCase.assertEquals("2011-10-30T02:15:00.000+02:00", base.toString());
        TestCase.assertEquals("2011-10-30T02:15:00.000+01:00", base.plusHours(1).toString());
    }

    public void testDateTimeCreation_london() {
        DateTimeZone zone = DateTimeZone.forID("Europe/London");
        DateTime base = new DateTime(2011, 10, 30, 1, 15, zone);
        TestCase.assertEquals("2011-10-30T01:15:00.000+01:00", base.toString());
        TestCase.assertEquals("2011-10-30T01:15:00.000Z", base.plusHours(1).toString());
    }

    public void testDateTimeCreation_newYork() {
        DateTimeZone zone = DateTimeZone.forID("America/New_York");
        DateTime base = new DateTime(2010, 11, 7, 1, 15, zone);
        TestCase.assertEquals("2010-11-07T01:15:00.000-04:00", base.toString());
        TestCase.assertEquals("2010-11-07T01:15:00.000-05:00", base.plusHours(1).toString());
    }

    public void testDateTimeCreation_losAngeles() {
        DateTimeZone zone = DateTimeZone.forID("America/Los_Angeles");
        DateTime base = new DateTime(2010, 11, 7, 1, 15, zone);
        TestCase.assertEquals("2010-11-07T01:15:00.000-07:00", base.toString());
        TestCase.assertEquals("2010-11-07T01:15:00.000-08:00", base.plusHours(1).toString());
    }
}

