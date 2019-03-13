/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional inparserion regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3.time;


import java.io.Serializable;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.SerializationUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests {@link org.apache.commons.lang3.time.FastDateParser}.
 *
 * @since 3.2
 */
public class FastDateParserTest {
    private static final String SHORT_FORMAT_NOERA = "y/M/d/h/a/m/s/E";

    private static final String LONG_FORMAT_NOERA = "yyyy/MMMM/dddd/hhhh/mmmm/ss/aaaa/EEEE";

    private static final String SHORT_FORMAT = "G/" + (FastDateParserTest.SHORT_FORMAT_NOERA);

    private static final String LONG_FORMAT = "GGGG/" + (FastDateParserTest.LONG_FORMAT_NOERA);

    private static final String yMdHmsSZ = "yyyy-MM-dd'T'HH:mm:ss.SSS Z";

    private static final String DMY_DOT = "dd.MM.yyyy";

    private static final String YMD_SLASH = "yyyy/MM/dd";

    private static final String MDY_DASH = "MM-DD-yyyy";

    private static final String MDY_SLASH = "MM/DD/yyyy";

    private static final TimeZone REYKJAVIK = TimeZone.getTimeZone("Atlantic/Reykjavik");

    private static final TimeZone NEW_YORK = TimeZone.getTimeZone("America/New_York");

    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

    private static final TimeZone INDIA = TimeZone.getTimeZone("Asia/Calcutta");

    private static final Locale SWEDEN = new Locale("sv", "SE");

    @Test
    public void test_Equality_Hash() {
        final DateParser[] parsers = new DateParser[]{ getInstance(FastDateParserTest.yMdHmsSZ, FastDateParserTest.NEW_YORK, Locale.US), getInstance(FastDateParserTest.DMY_DOT, FastDateParserTest.NEW_YORK, Locale.US), getInstance(FastDateParserTest.YMD_SLASH, FastDateParserTest.NEW_YORK, Locale.US), getInstance(FastDateParserTest.MDY_DASH, FastDateParserTest.NEW_YORK, Locale.US), getInstance(FastDateParserTest.MDY_SLASH, FastDateParserTest.NEW_YORK, Locale.US), getInstance(FastDateParserTest.MDY_SLASH, FastDateParserTest.REYKJAVIK, Locale.US), getInstance(FastDateParserTest.MDY_SLASH, FastDateParserTest.REYKJAVIK, FastDateParserTest.SWEDEN) };
        final Map<DateParser, Integer> map = new HashMap<>();
        int i = 0;
        for (final DateParser parser : parsers) {
            map.put(parser, Integer.valueOf((i++)));
        }
        i = 0;
        for (final DateParser parser : parsers) {
            Assertions.assertEquals((i++), map.get(parser).intValue());
        }
    }

    @Test
    public void testParseZone() throws ParseException {
        final Calendar cal = Calendar.getInstance(FastDateParserTest.NEW_YORK, Locale.US);
        cal.clear();
        cal.set(2003, Calendar.JULY, 10, 16, 33, 20);
        final DateParser fdf = getInstance(FastDateParserTest.yMdHmsSZ, FastDateParserTest.NEW_YORK, Locale.US);
        Assertions.assertEquals(cal.getTime(), fdf.parse("2003-07-10T15:33:20.000 -0500"));
        Assertions.assertEquals(cal.getTime(), fdf.parse("2003-07-10T15:33:20.000 GMT-05:00"));
        Assertions.assertEquals(cal.getTime(), fdf.parse("2003-07-10T16:33:20.000 Eastern Daylight Time"));
        Assertions.assertEquals(cal.getTime(), fdf.parse("2003-07-10T16:33:20.000 EDT"));
        cal.setTimeZone(TimeZone.getTimeZone("GMT-3"));
        cal.set(2003, Calendar.FEBRUARY, 10, 9, 0, 0);
        Assertions.assertEquals(cal.getTime(), fdf.parse("2003-02-10T09:00:00.000 -0300"));
        cal.setTimeZone(TimeZone.getTimeZone("GMT+5"));
        cal.set(2003, Calendar.FEBRUARY, 10, 15, 5, 6);
        Assertions.assertEquals(cal.getTime(), fdf.parse("2003-02-10T15:05:06.000 +0500"));
    }

    @Test
    public void testParseLongShort() throws ParseException {
        final Calendar cal = Calendar.getInstance(FastDateParserTest.NEW_YORK, Locale.US);
        cal.clear();
        cal.set(2003, Calendar.FEBRUARY, 10, 15, 33, 20);
        cal.set(Calendar.MILLISECOND, 989);
        cal.setTimeZone(FastDateParserTest.NEW_YORK);
        DateParser fdf = getInstance("yyyy GGGG MMMM dddd aaaa EEEE HHHH mmmm ssss SSSS ZZZZ", FastDateParserTest.NEW_YORK, Locale.US);
        Assertions.assertEquals(cal.getTime(), fdf.parse("2003 AD February 0010 PM Monday 0015 0033 0020 0989 GMT-05:00"));
        cal.set(Calendar.ERA, GregorianCalendar.BC);
        final Date parse = fdf.parse("2003 BC February 0010 PM Saturday 0015 0033 0020 0989 GMT-05:00");
        Assertions.assertEquals(cal.getTime(), parse);
        fdf = getInstance("y G M d a E H m s S Z", FastDateParserTest.NEW_YORK, Locale.US);
        Assertions.assertEquals(cal.getTime(), fdf.parse("03 BC 2 10 PM Sat 15 33 20 989 -0500"));
        cal.set(Calendar.ERA, GregorianCalendar.AD);
        Assertions.assertEquals(cal.getTime(), fdf.parse("03 AD 2 10 PM Saturday 15 33 20 989 -0500"));
    }

    @Test
    public void testAmPm() throws ParseException {
        final Calendar cal = Calendar.getInstance(FastDateParserTest.NEW_YORK, Locale.US);
        cal.clear();
        final DateParser h = getInstance("yyyy-MM-dd hh a mm:ss", FastDateParserTest.NEW_YORK, Locale.US);
        final DateParser K = getInstance("yyyy-MM-dd KK a mm:ss", FastDateParserTest.NEW_YORK, Locale.US);
        final DateParser k = getInstance("yyyy-MM-dd kk:mm:ss", FastDateParserTest.NEW_YORK, Locale.US);
        final DateParser H = getInstance("yyyy-MM-dd HH:mm:ss", FastDateParserTest.NEW_YORK, Locale.US);
        cal.set(2010, Calendar.AUGUST, 1, 0, 33, 20);
        Assertions.assertEquals(cal.getTime(), h.parse("2010-08-01 12 AM 33:20"));
        Assertions.assertEquals(cal.getTime(), K.parse("2010-08-01 0 AM 33:20"));
        Assertions.assertEquals(cal.getTime(), k.parse("2010-08-01 00:33:20"));
        Assertions.assertEquals(cal.getTime(), H.parse("2010-08-01 00:33:20"));
        cal.set(2010, Calendar.AUGUST, 1, 3, 33, 20);
        Assertions.assertEquals(cal.getTime(), h.parse("2010-08-01 3 AM 33:20"));
        Assertions.assertEquals(cal.getTime(), K.parse("2010-08-01 3 AM 33:20"));
        Assertions.assertEquals(cal.getTime(), k.parse("2010-08-01 03:33:20"));
        Assertions.assertEquals(cal.getTime(), H.parse("2010-08-01 03:33:20"));
        cal.set(2010, Calendar.AUGUST, 1, 15, 33, 20);
        Assertions.assertEquals(cal.getTime(), h.parse("2010-08-01 3 PM 33:20"));
        Assertions.assertEquals(cal.getTime(), K.parse("2010-08-01 3 PM 33:20"));
        Assertions.assertEquals(cal.getTime(), k.parse("2010-08-01 15:33:20"));
        Assertions.assertEquals(cal.getTime(), H.parse("2010-08-01 15:33:20"));
        cal.set(2010, Calendar.AUGUST, 1, 12, 33, 20);
        Assertions.assertEquals(cal.getTime(), h.parse("2010-08-01 12 PM 33:20"));
        Assertions.assertEquals(cal.getTime(), K.parse("2010-08-01 0 PM 33:20"));
        Assertions.assertEquals(cal.getTime(), k.parse("2010-08-01 12:33:20"));
        Assertions.assertEquals(cal.getTime(), H.parse("2010-08-01 12:33:20"));
    }

    // Check that all Locales can parse the formats we use
    @Test
    public void testParses() throws Exception {
        for (final String format : new String[]{ FastDateParserTest.LONG_FORMAT, FastDateParserTest.SHORT_FORMAT }) {
            for (final Locale locale : Locale.getAvailableLocales()) {
                for (final TimeZone tz : new TimeZone[]{ FastDateParserTest.NEW_YORK, FastDateParserTest.REYKJAVIK, FastDateParserTest.GMT }) {
                    for (final int year : new int[]{ 2003, 1940, 1868, 1867, 1, -1, -1940 }) {
                        final Calendar cal = getEraStart(year, tz, locale);
                        final Date centuryStart = cal.getTime();
                        cal.set(Calendar.MONTH, 1);
                        cal.set(Calendar.DAY_OF_MONTH, 10);
                        final Date in = cal.getTime();
                        final FastDateParser fdp = new FastDateParser(format, tz, locale, centuryStart);
                        validateSdfFormatFdpParseEquality(format, locale, tz, fdp, in, year, centuryStart);
                    }
                }
            }
        }
    }

    // we cannot use historic dates to test timezone parsing, some timezones have second offsets
    // as well as hours and minutes which makes the z formats a low fidelity round trip
    @Test
    public void testTzParses() throws Exception {
        // Check that all Locales can parse the time formats we use
        for (final Locale locale : Locale.getAvailableLocales()) {
            final FastDateParser fdp = new FastDateParser("yyyy/MM/dd z", TimeZone.getDefault(), locale);
            for (final TimeZone tz : new TimeZone[]{ FastDateParserTest.NEW_YORK, FastDateParserTest.REYKJAVIK, FastDateParserTest.GMT }) {
                final Calendar cal = Calendar.getInstance(tz, locale);
                cal.clear();
                cal.set(Calendar.YEAR, 2000);
                cal.set(Calendar.MONTH, 1);
                cal.set(Calendar.DAY_OF_MONTH, 10);
                final Date expected = cal.getTime();
                final Date actual = fdp.parse(("2000/02/10 " + (tz.getDisplayName(locale))));
                Assertions.assertEquals(expected, actual, ((("tz:" + (tz.getID())) + " locale:") + (locale.getDisplayName())));
            }
        }
    }

    @Test
    public void testLocales_Long_AD() throws Exception {
        testLocales(FastDateParserTest.LONG_FORMAT, false);
    }

    @Test
    public void testLocales_Long_BC() throws Exception {
        testLocales(FastDateParserTest.LONG_FORMAT, true);
    }

    @Test
    public void testLocales_Short_AD() throws Exception {
        testLocales(FastDateParserTest.SHORT_FORMAT, false);
    }

    @Test
    public void testLocales_Short_BC() throws Exception {
        testLocales(FastDateParserTest.SHORT_FORMAT, true);
    }

    @Test
    public void testLocales_LongNoEra_AD() throws Exception {
        testLocales(FastDateParserTest.LONG_FORMAT_NOERA, false);
    }

    @Test
    public void testLocales_LongNoEra_BC() throws Exception {
        testLocales(FastDateParserTest.LONG_FORMAT_NOERA, true);
    }

    @Test
    public void testLocales_ShortNoEra_AD() throws Exception {
        testLocales(FastDateParserTest.SHORT_FORMAT_NOERA, false);
    }

    @Test
    public void testLocales_ShortNoEra_BC() throws Exception {
        testLocales(FastDateParserTest.SHORT_FORMAT_NOERA, true);
    }

    @Test
    public void testJpLocales() throws ParseException {
        final Calendar cal = Calendar.getInstance(FastDateParserTest.GMT);
        cal.clear();
        cal.set(2003, Calendar.FEBRUARY, 10);
        cal.set(Calendar.ERA, GregorianCalendar.BC);
        final Locale locale = LocaleUtils.toLocale("zh");
        // ja_JP_JP cannot handle dates before 1868 properly
        final SimpleDateFormat sdf = new SimpleDateFormat(FastDateParserTest.LONG_FORMAT, locale);
        final DateParser fdf = getInstance(FastDateParserTest.LONG_FORMAT, locale);
        // If parsing fails, a ParseException will be thrown and the test will fail
        checkParse(locale, cal, sdf, fdf);
    }

    @Test
    public void testParseNumerics() throws ParseException {
        final Calendar cal = Calendar.getInstance(FastDateParserTest.NEW_YORK, Locale.US);
        cal.clear();
        cal.set(2003, Calendar.FEBRUARY, 10, 15, 33, 20);
        cal.set(Calendar.MILLISECOND, 989);
        final DateParser fdf = getInstance("yyyyMMddHHmmssSSS", FastDateParserTest.NEW_YORK, Locale.US);
        Assertions.assertEquals(cal.getTime(), fdf.parse("20030210153320989"));
    }

    @Test
    public void testQuotes() throws ParseException {
        final Calendar cal = Calendar.getInstance(FastDateParserTest.NEW_YORK, Locale.US);
        cal.clear();
        cal.set(2003, Calendar.FEBRUARY, 10, 15, 33, 20);
        cal.set(Calendar.MILLISECOND, 989);
        final DateParser fdf = getInstance("''yyyyMMdd'A''B'HHmmssSSS''", FastDateParserTest.NEW_YORK, Locale.US);
        Assertions.assertEquals(cal.getTime(), fdf.parse("'20030210A'B153320989'"));
    }

    @Test
    public void testSpecialCharacters() throws Exception {
        testSdfAndFdp("q", "", true);// bad pattern character (at present)

        testSdfAndFdp("Q", "", true);// bad pattern character

        testSdfAndFdp("$", "$", false);// OK

        testSdfAndFdp("?.d", "?.12", false);// OK

        testSdfAndFdp("''yyyyMMdd'A''B'HHmmssSSS''", "'20030210A'B153320989'", false);// OK

        testSdfAndFdp("''''yyyyMMdd'A''B'HHmmssSSS''", "''20030210A'B153320989'", false);// OK

        testSdfAndFdp("\'$\\Ed\'", "$\\Ed", false);// OK

        // quoted charaters are case sensitive
        testSdfAndFdp("'QED'", "QED", false);
        testSdfAndFdp("'QED'", "qed", true);
        // case sensitive after insensitive Month field
        testSdfAndFdp("yyyy-MM-dd 'QED'", "2003-02-10 QED", false);
        testSdfAndFdp("yyyy-MM-dd 'QED'", "2003-02-10 qed", true);
    }

    @Test
    public void testLANG_832() throws Exception {
        testSdfAndFdp("'d'd", "d3", false);// OK

        testSdfAndFdp("'d'd'", "d3", true);// should fail (unterminated quote)

    }

    @Test
    public void testLANG_831() throws Exception {
        testSdfAndFdp("M E", "3  Tue", true);
    }

    @Test
    public void testDayOf() throws ParseException {
        final Calendar cal = Calendar.getInstance(FastDateParserTest.NEW_YORK, Locale.US);
        cal.clear();
        cal.set(2003, Calendar.FEBRUARY, 10);
        final DateParser fdf = getInstance("W w F D y", FastDateParserTest.NEW_YORK, Locale.US);
        Assertions.assertEquals(cal.getTime(), fdf.parse("3 7 2 41 03"));
    }

    /**
     * Test case for {@link FastDateParser#FastDateParser(String, TimeZone, Locale)}.
     *
     * @throws ParseException
     * 		so we don't have to catch it
     */
    @Test
    public void testShortDateStyleWithLocales() throws ParseException {
        DateParser fdf = getDateInstance(FastDateFormat.SHORT, Locale.US);
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2004, Calendar.FEBRUARY, 3);
        Assertions.assertEquals(cal.getTime(), fdf.parse("2/3/04"));
        fdf = getDateInstance(FastDateFormat.SHORT, FastDateParserTest.SWEDEN);
        Assertions.assertEquals(cal.getTime(), fdf.parse("2004-02-03"));
    }

    /**
     * Tests that pre-1000AD years get padded with yyyy
     *
     * @throws ParseException
     * 		so we don't have to catch it
     */
    @Test
    public void testLowYearPadding() throws ParseException {
        final DateParser parser = getInstance(FastDateParserTest.YMD_SLASH);
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(1, Calendar.JANUARY, 1);
        Assertions.assertEquals(cal.getTime(), parser.parse("0001/01/01"));
        cal.set(10, Calendar.JANUARY, 1);
        Assertions.assertEquals(cal.getTime(), parser.parse("0010/01/01"));
        cal.set(100, Calendar.JANUARY, 1);
        Assertions.assertEquals(cal.getTime(), parser.parse("0100/01/01"));
        cal.set(999, Calendar.JANUARY, 1);
        Assertions.assertEquals(cal.getTime(), parser.parse("0999/01/01"));
    }

    @Test
    public void testMilleniumBug() throws ParseException {
        final DateParser parser = getInstance(FastDateParserTest.DMY_DOT);
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(1000, Calendar.JANUARY, 1);
        Assertions.assertEquals(cal.getTime(), parser.parse("01.01.1000"));
    }

    @Test
    public void testLang303() throws ParseException {
        DateParser parser = getInstance(FastDateParserTest.YMD_SLASH);
        final Calendar cal = Calendar.getInstance();
        cal.set(2004, Calendar.DECEMBER, 31);
        final Date date = parser.parse("2004/11/31");
        parser = SerializationUtils.deserialize(SerializationUtils.serialize(((Serializable) (parser))));
        Assertions.assertEquals(date, parser.parse("2004/11/31"));
    }

    @Test
    public void testLang538() throws ParseException {
        final DateParser parser = getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", FastDateParserTest.GMT);
        final Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT-8"));
        cal.clear();
        cal.set(2009, Calendar.OCTOBER, 16, 8, 42, 16);
        Assertions.assertEquals(cal.getTime(), parser.parse("2009-10-16T16:42:16.000Z"));
    }

    @Test
    public void testEquals() {
        final DateParser parser1 = getInstance(FastDateParserTest.YMD_SLASH);
        final DateParser parser2 = getInstance(FastDateParserTest.YMD_SLASH);
        Assertions.assertEquals(parser1, parser2);
        Assertions.assertEquals(parser1.hashCode(), parser2.hashCode());
        Assertions.assertNotEquals(parser1, new Object());
    }

    @Test
    public void testToStringContainsName() {
        final DateParser parser = getInstance(FastDateParserTest.YMD_SLASH);
        Assertions.assertTrue(parser.toString().startsWith("FastDate"));
    }

    @Test
    public void testPatternMatches() {
        final DateParser parser = getInstance(FastDateParserTest.yMdHmsSZ);
        Assertions.assertEquals(FastDateParserTest.yMdHmsSZ, parser.getPattern());
    }

    @Test
    public void testLocaleMatches() {
        final DateParser parser = getInstance(FastDateParserTest.yMdHmsSZ, FastDateParserTest.SWEDEN);
        Assertions.assertEquals(FastDateParserTest.SWEDEN, parser.getLocale());
    }

    @Test
    public void testTimeZoneMatches() {
        final DateParser parser = getInstance(FastDateParserTest.yMdHmsSZ, FastDateParserTest.REYKJAVIK);
        Assertions.assertEquals(FastDateParserTest.REYKJAVIK, parser.getTimeZone());
    }

    @Test
    public void testLang996() throws ParseException {
        final Calendar expected = Calendar.getInstance(FastDateParserTest.NEW_YORK, Locale.US);
        expected.clear();
        expected.set(2014, Calendar.MAY, 14);
        final DateParser fdp = getInstance("ddMMMyyyy", FastDateParserTest.NEW_YORK, Locale.US);
        Assertions.assertEquals(expected.getTime(), fdp.parse("14may2014"));
        Assertions.assertEquals(expected.getTime(), fdp.parse("14MAY2014"));
        Assertions.assertEquals(expected.getTime(), fdp.parse("14May2014"));
    }

    @Test
    public void test1806Argument() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> getInstance("XXXX"));
    }

    private enum Expected1806 {

        India(FastDateParserTest.INDIA, "+05", "+0530", "+05:30", true),
        Greenwich(FastDateParserTest.GMT, "Z", "Z", "Z", false),
        NewYork(FastDateParserTest.NEW_YORK, "-05", "-0500", "-05:00", false);
        Expected1806(final TimeZone zone, final String one, final String two, final String three, final boolean hasHalfHourOffset) {
            this.zone = zone;
            this.one = one;
            this.two = two;
            this.three = three;
            this.offset = (hasHalfHourOffset) ? (30 * 60) * 1000 : 0;
        }

        final TimeZone zone;

        final String one;

        final String two;

        final String three;

        final long offset;
    }

    @Test
    public void test1806() throws ParseException {
        final String formatStub = "yyyy-MM-dd'T'HH:mm:ss.SSS";
        final String dateStub = "2001-02-04T12:08:56.235";
        for (final FastDateParserTest.Expected1806 trial : FastDateParserTest.Expected1806.values()) {
            final Calendar cal = FastDateParserTest.initializeCalendar(trial.zone);
            final String message = (trial.zone.getDisplayName()) + ";";
            DateParser parser = getInstance((formatStub + "X"), trial.zone);
            Assertions.assertEquals(cal.getTime().getTime(), ((parser.parse((dateStub + (trial.one))).getTime()) - (trial.offset)), (message + (trial.one)));
            parser = getInstance((formatStub + "XX"), trial.zone);
            Assertions.assertEquals(cal.getTime(), parser.parse((dateStub + (trial.two))), (message + (trial.two)));
            parser = getInstance((formatStub + "XXX"), trial.zone);
            Assertions.assertEquals(cal.getTime(), parser.parse((dateStub + (trial.three))), (message + (trial.three)));
        }
    }

    @Test
    public void testLang1121() throws ParseException {
        final TimeZone kst = TimeZone.getTimeZone("KST");
        final DateParser fdp = getInstance("yyyyMMdd", kst, Locale.KOREA);
        Assertions.assertThrows(ParseException.class, () -> fdp.parse("2015"));
        // Wed Apr 29 00:00:00 KST 2015
        Date actual = fdp.parse("20150429");
        final Calendar cal = Calendar.getInstance(kst, Locale.KOREA);
        cal.clear();
        cal.set(2015, 3, 29);
        Date expected = cal.getTime();
        Assertions.assertEquals(expected, actual);
        final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
        df.setTimeZone(kst);
        expected = df.parse("20150429113100");
        // Thu Mar 16 00:00:00 KST 81724
        actual = fdp.parse("20150429113100");
        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testParseOffset() {
        final DateParser parser = getInstance(FastDateParserTest.YMD_SLASH);
        final Date date = parser.parse("Today is 2015/07/04", new ParsePosition(9));
        final Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2015, Calendar.JULY, 4);
        Assertions.assertEquals(cal.getTime(), date);
    }

    @Test
    public void testDayNumberOfWeek() throws ParseException {
        final DateParser parser = getInstance("u");
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(parser.parse("1"));
        Assertions.assertEquals(Calendar.MONDAY, calendar.get(Calendar.DAY_OF_WEEK));
        calendar.setTime(parser.parse("6"));
        Assertions.assertEquals(Calendar.SATURDAY, calendar.get(Calendar.DAY_OF_WEEK));
        calendar.setTime(parser.parse("7"));
        Assertions.assertEquals(Calendar.SUNDAY, calendar.get(Calendar.DAY_OF_WEEK));
    }

    @Test
    public void testLang1380() throws ParseException {
        final Calendar expected = Calendar.getInstance(FastDateParserTest.GMT, Locale.FRANCE);
        expected.clear();
        expected.set(2014, Calendar.APRIL, 14);
        final DateParser fdp = getInstance("dd MMM yyyy", FastDateParserTest.GMT, Locale.FRANCE);
        Assertions.assertEquals(expected.getTime(), fdp.parse("14 avril 2014"));
        Assertions.assertEquals(expected.getTime(), fdp.parse("14 avr. 2014"));
        Assertions.assertEquals(expected.getTime(), fdp.parse("14 avr 2014"));
    }
}

