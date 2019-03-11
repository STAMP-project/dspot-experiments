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
 * This class is a Junit unit test for PeriodFormat.
 *
 * @author Stephen Colebourne
 */
public class TestPeriodFormatParsing extends TestCase {
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

    public TestPeriodFormatParsing(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testParseStandard1() {
        PeriodFormatter parser = PeriodFormat.getDefault();
        Period p = parser.parsePeriod("6 years, 3 months and 2 days");
        TestCase.assertEquals(new Period(6, 3, 0, 2, 0, 0, 0, 0), p);
    }

    public void testParseNegativeMillis1() {
        Period period = new Period(0, 0, 0, (-1));
        String formatted = period.toString();
        TestCase.assertEquals("PT-0.001S", formatted);
        Period parsed = Period.parse(formatted);
        TestCase.assertEquals(period, parsed);
    }

    public void testParseNegativeMillis2() {
        Period period = new Period(0, 0, 0, (-999));
        String formatted = period.toString();
        TestCase.assertEquals("PT-0.999S", formatted);
        Period parsed = Period.parse(formatted);
        TestCase.assertEquals(period, parsed);
    }

    public void testParseCustom1() {
        PeriodFormatter formatter = new PeriodFormatterBuilder().printZeroAlways().appendHours().appendSuffix(":").minimumPrintedDigits(2).appendMinutes().toFormatter();
        Period p;
        p = new Period(47, 55, 0, 0);
        TestCase.assertEquals("47:55", formatter.print(p));
        TestCase.assertEquals(p, formatter.parsePeriod("47:55"));
        TestCase.assertEquals(p, formatter.parsePeriod("047:055"));
        p = new Period(7, 5, 0, 0);
        TestCase.assertEquals("7:05", formatter.print(p));
        TestCase.assertEquals(p, formatter.parsePeriod("7:05"));
        TestCase.assertEquals(p, formatter.parsePeriod("7:5"));
        TestCase.assertEquals(p, formatter.parsePeriod("07:05"));
        p = new Period(0, 5, 0, 0);
        TestCase.assertEquals("0:05", formatter.print(p));
        TestCase.assertEquals(p, formatter.parsePeriod("0:05"));
        TestCase.assertEquals(p, formatter.parsePeriod("0:5"));
        TestCase.assertEquals(p, formatter.parsePeriod("00:005"));
        TestCase.assertEquals(p, formatter.parsePeriod("0:005"));
        p = new Period(0, 0, 0, 0);
        TestCase.assertEquals("0:00", formatter.print(p));
        TestCase.assertEquals(p, formatter.parsePeriod("0:00"));
        TestCase.assertEquals(p, formatter.parsePeriod("0:0"));
        TestCase.assertEquals(p, formatter.parsePeriod("00:00"));
    }
}

