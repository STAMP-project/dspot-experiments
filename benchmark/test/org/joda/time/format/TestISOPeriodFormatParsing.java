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
package org.joda.time.format;


import java.util.Locale;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.joda.time.PeriodType;


/**
 * This class is a Junit unit test for ISOPeriodFormat.
 *
 * @author Stephen Colebourne
 */
public class TestISOPeriodFormatParsing extends TestCase {
    private static final Period PERIOD = new Period(1, 2, 3, 4, 5, 6, 7, 8);

    private static final Period EMPTY_PERIOD = new Period(0, 0, 0, 0, 0, 0, 0, 0);

    private static final Period YEAR_DAY_PERIOD = new Period(1, 0, 0, 4, 5, 6, 7, 8, PeriodType.yearDayTime());

    private static final Period EMPTY_YEAR_DAY_PERIOD = new Period(0, 0, 0, 0, 0, 0, 0, 0, PeriodType.yearDayTime());

    private static final Period TIME_PERIOD = new Period(0, 0, 0, 0, 5, 6, 7, 8);

    private static final Period DATE_PERIOD = new Period(1, 2, 3, 4, 0, 0, 0, 0);

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (DateTimeConstants.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestISOPeriodFormatParsing(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard1() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("P1Y2M3W4DT5H6M7.008S");
        TestCase.assertEquals(new Period(1, 2, 3, 4, 5, 6, 7, 8), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard2() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("P0Y0M0W0DT5H6M7.008S");
        TestCase.assertEquals(new Period(0, 0, 0, 0, 5, 6, 7, 8), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard3() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("P0DT5H6M7.008S");
        TestCase.assertEquals(new Period(0, 0, 0, 0, 5, 6, 7, 8), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard4() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("P2Y3DT5H6M7.008S");
        TestCase.assertEquals(new Period(2, 0, 0, 3, 5, 6, 7, 8), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard5() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("P2YT5H6M7.008S");
        TestCase.assertEquals(new Period(2, 0, 0, 0, 5, 6, 7, 8), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard6() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("PT5H6M7.008S");
        TestCase.assertEquals(new Period(0, 0, 0, 0, 5, 6, 7, 8), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard7() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("P1Y2M3W4D");
        TestCase.assertEquals(new Period(1, 2, 3, 4, 0, 0, 0, 0), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard8() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("PT5H6M7S");
        TestCase.assertEquals(new Period(0, 0, 0, 0, 5, 6, 7, 0), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard9() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("PT0S");
        TestCase.assertEquals(new Period(0, 0, 0, 0, 0, 0, 0, 0), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard10() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("P0D");
        TestCase.assertEquals(new Period(0, 0, 0, 0, 0, 0, 0, 0), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard11() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        Period p = parser.parsePeriod("P0Y");
        TestCase.assertEquals(new Period(0, 0, 0, 0, 0, 0, 0, 0), p);
    }

    // -----------------------------------------------------------------------
    public void testParseStandardFail1() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        try {
            parser.parsePeriod("P1Y2S");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testParseStandardFail2() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        try {
            parser.parsePeriod("PS");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testParseStandardFail3() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        try {
            parser.parsePeriod("PTS");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testParseStandardFail4() {
        PeriodFormatter parser = ISOPeriodFormat.standard();
        try {
            parser.parsePeriod("PXS");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }
}

