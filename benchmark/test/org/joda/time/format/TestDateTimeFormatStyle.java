/**
 * Copyright 2001-2014 Stephen Colebourne
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


import java.text.DateFormat;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import junit.framework.TestCase;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeZone;


/**
 * This class is a Junit unit test for DateTimeFormat styles.
 *
 * @author Stephen Colebourne
 */
public class TestDateTimeFormatStyle extends TestCase {
    private static final Locale UK = Locale.UK;

    private static final Locale US = Locale.US;

    private static final Locale FRANCE = Locale.FRANCE;

    private static final DateTimeZone UTC = DateTimeZone.UTC;

    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    private static final DateTimeZone LONDON = DateTimeZone.forID("Europe/London");

    private static final DateTimeZone TOKYO = DateTimeZone.forID("Asia/Tokyo");

    private static final DateTimeZone NEWYORK = DateTimeZone.forID("America/New_York");

    long y2002days = ((((((((((((((((((((((((((((((365 + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365) + 365) + 365) + 366) + 365;

    // 2002-06-09
    private long TEST_TIME_NOW = ((((((((y2002days) + 31L) + 28L) + 31L) + 30L) + 31L) + 9L) - 1L) * (DateTimeConstants.MILLIS_PER_DAY);

    private DateTimeZone originalDateTimeZone = null;

    private TimeZone originalTimeZone = null;

    private Locale originalLocale = null;

    public TestDateTimeFormatStyle(String name) {
        super(name);
    }

    // -----------------------------------------------------------------------
    public void testForStyle_stringLengths() {
        try {
            DateTimeFormat.forStyle(null);
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeFormat.forStyle("");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeFormat.forStyle("S");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeFormat.forStyle("SSS");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    public void testForStyle_invalidStrings() {
        try {
            DateTimeFormat.forStyle("AA");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeFormat.forStyle("--");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
        try {
            DateTimeFormat.forStyle("ss");
            TestCase.fail();
        } catch (IllegalArgumentException ex) {
        }
    }

    // -----------------------------------------------------------------------
    public void testForStyle_shortDate() throws Exception {
        DateTimeFormatter f = DateTimeFormat.shortDate();
        DateTimeFormatter g = DateTimeFormat.forStyle("S-");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateInstance(DateFormat.SHORT, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateInstance(DateFormat.SHORT, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateInstance(DateFormat.SHORT, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
        DateTime date = new DateTime(DateFormat.getDateInstance(DateFormat.SHORT, TestDateTimeFormatStyle.FRANCE).parse(expect));
        TestCase.assertEquals(date, f.withLocale(TestDateTimeFormatStyle.FRANCE).parseDateTime(expect));
    }

    public void testForStyle_shortTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.shortTime();
        DateTimeFormatter g = DateTimeFormat.forStyle("-S");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getTimeInstance(DateFormat.SHORT, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getTimeInstance(DateFormat.SHORT, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getTimeInstance(DateFormat.SHORT, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
        if ((TimeZone.getDefault()) instanceof SimpleTimeZone) {
            // skip test, as it needs historical time zone info
        } else {
            DateTime date = new DateTime(DateFormat.getTimeInstance(DateFormat.SHORT, TestDateTimeFormatStyle.FRANCE).parse(expect));
            TestCase.assertEquals(date, f.withLocale(TestDateTimeFormatStyle.FRANCE).parseDateTime(expect));
        }
    }

    public void testForStyle_shortDateTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.shortDateTime();
        DateTimeFormatter g = DateTimeFormat.forStyle("SS");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
        DateTime date = new DateTime(DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, TestDateTimeFormatStyle.FRANCE).parse(expect));
        TestCase.assertEquals(date, f.withLocale(TestDateTimeFormatStyle.FRANCE).parseDateTime(expect));
    }

    // -----------------------------------------------------------------------
    public void testForStyle_mediumDate() throws Exception {
        DateTimeFormatter f = DateTimeFormat.mediumDate();
        DateTimeFormatter g = DateTimeFormat.forStyle("M-");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateInstance(DateFormat.MEDIUM, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateInstance(DateFormat.MEDIUM, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateInstance(DateFormat.MEDIUM, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void testForStyle_mediumTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.mediumTime();
        DateTimeFormatter g = DateTimeFormat.forStyle("-M");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getTimeInstance(DateFormat.MEDIUM, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getTimeInstance(DateFormat.MEDIUM, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getTimeInstance(DateFormat.MEDIUM, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void testForStyle_mediumDateTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.mediumDateTime();
        DateTimeFormatter g = DateTimeFormat.forStyle("MM");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    // -----------------------------------------------------------------------
    public void testForStyle_longDate() throws Exception {
        DateTimeFormatter f = DateTimeFormat.longDate();
        DateTimeFormatter g = DateTimeFormat.forStyle("L-");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateInstance(DateFormat.LONG, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateInstance(DateFormat.LONG, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateInstance(DateFormat.LONG, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void testForStyle_longTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.longTime();
        DateTimeFormatter g = DateTimeFormat.forStyle("-L");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getTimeInstance(DateFormat.LONG, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getTimeInstance(DateFormat.LONG, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getTimeInstance(DateFormat.LONG, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void testForStyle_longDateTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.longDateTime();
        DateTimeFormatter g = DateTimeFormat.forStyle("LL");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    // -----------------------------------------------------------------------
    public void testForStyle_fullDate() throws Exception {
        DateTimeFormatter f = DateTimeFormat.fullDate();
        DateTimeFormatter g = DateTimeFormat.forStyle("F-");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateInstance(DateFormat.FULL, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateInstance(DateFormat.FULL, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateInstance(DateFormat.FULL, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void testForStyle_fullTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.fullTime();
        DateTimeFormatter g = DateTimeFormat.forStyle("-F");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getTimeInstance(DateFormat.FULL, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getTimeInstance(DateFormat.FULL, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getTimeInstance(DateFormat.FULL, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void testForStyle_fullDateTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.fullDateTime();
        DateTimeFormatter g = DateTimeFormat.forStyle("FF");
        TestCase.assertSame(g, f);
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    // -----------------------------------------------------------------------
    public void testForStyle_shortMediumDateTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.forStyle("SM");
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void testForStyle_shortLongDateTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.forStyle("SL");
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.LONG, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void testForStyle_shortFullDateTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.forStyle("SF");
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.FULL, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    // -----------------------------------------------------------------------
    public void testForStyle_mediumShortDateTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.forStyle("MS");
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void testForStyle_mediumLongDateTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.forStyle("ML");
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.LONG, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void testForStyle_mediumFullDateTime() throws Exception {
        DateTimeFormatter f = DateTimeFormat.forStyle("MF");
        DateTime dt = new DateTime(2004, 6, 9, 10, 20, 30, 0);
        String expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL, TestDateTimeFormatStyle.UK).format(dt.toDate());
        TestCase.assertEquals(expect, f.print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL, TestDateTimeFormatStyle.US).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.US).print(dt));
        expect = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.FULL, TestDateTimeFormatStyle.FRANCE).format(dt.toDate());
        TestCase.assertEquals(expect, f.withLocale(TestDateTimeFormatStyle.FRANCE).print(dt));
    }

    public void test_patternForStyle() throws Exception {
        String format = DateTimeFormat.patternForStyle("MF", TestDateTimeFormatStyle.UK);
        TestCase.assertNotNull(format);
    }
}

