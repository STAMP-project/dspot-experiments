/**
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
/**
 * This file is available under and governed by the GNU General Public
 * License version 2 only, as published by the Free Software Foundation.
 * However, the following notice accompanied the original version of this
 * file:
 *
 * Copyright (c) 2009-2012, Stephen Colebourne & Michael Nascimento Santos
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  * Neither the name of JSR-310 nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
/**
 * J2ObjC: only "gregorian" and "julian" calendars are supported.
 * import java.time.chrono.JapaneseChronology;
 * import java.time.chrono.MinguoChronology;
 */
package test.java.time.format;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.time.format.SignStyle;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test DateTimeFormatterBuilder.
 */
@RunWith(DataProviderRunner.class)
public class TestDateTimeFormatterBuilder {
    private DateTimeFormatterBuilder builder;

    // -----------------------------------------------------------------------
    @Test
    public void test_toFormatter_empty() throws Exception {
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "");
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_parseCaseSensitive() throws Exception {
        builder.parseCaseSensitive();
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "ParseCaseSensitive(true)");
    }

    @Test
    public void test_parseCaseInsensitive() throws Exception {
        builder.parseCaseInsensitive();
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "ParseCaseSensitive(false)");
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_parseStrict() throws Exception {
        builder.parseStrict();
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "ParseStrict(true)");
    }

    @Test
    public void test_parseLenient() throws Exception {
        builder.parseLenient();
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "ParseStrict(false)");
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_appendValue_1arg() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(DayOfMonth)");
    }

    @Test(expected = NullPointerException.class)
    public void test_appendValue_1arg_null() throws Exception {
        builder.appendValue(null);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_appendValue_2arg() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH, 3);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(DayOfMonth,3)");
    }

    @Test(expected = NullPointerException.class)
    public void test_appendValue_2arg_null() throws Exception {
        builder.appendValue(null, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendValue_2arg_widthTooSmall() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH, 0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendValue_2arg_widthTooBig() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH, 20);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_appendValue_3arg() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH, 2, 3, SignStyle.NORMAL);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(DayOfMonth,2,3,NORMAL)");
    }

    @Test(expected = NullPointerException.class)
    public void test_appendValue_3arg_nullField() throws Exception {
        builder.appendValue(null, 2, 3, SignStyle.NORMAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendValue_3arg_minWidthTooSmall() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH, 0, 2, SignStyle.NORMAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendValue_3arg_minWidthTooBig() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH, 20, 2, SignStyle.NORMAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendValue_3arg_maxWidthTooSmall() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH, 2, 0, SignStyle.NORMAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendValue_3arg_maxWidthTooBig() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH, 2, 20, SignStyle.NORMAL);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendValue_3arg_maxWidthMinWidth() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH, 4, 2, SignStyle.NORMAL);
    }

    @Test(expected = NullPointerException.class)
    public void test_appendValue_3arg_nullSignStyle() throws Exception {
        builder.appendValue(ChronoField.DAY_OF_MONTH, 2, 3, null);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_appendValue_subsequent2_parse3() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NORMAL).appendValue(ChronoField.DAY_OF_MONTH, 2);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear,1,2,NORMAL)Value(DayOfMonth,2)");
        TemporalAccessor parsed = f.parseUnresolved("123", new ParsePosition(0));
        Assert.assertEquals(parsed.getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(parsed.getLong(ChronoField.DAY_OF_MONTH), 23L);
    }

    @Test
    public void test_appendValue_subsequent2_parse4() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NORMAL).appendValue(ChronoField.DAY_OF_MONTH, 2);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear,1,2,NORMAL)Value(DayOfMonth,2)");
        TemporalAccessor parsed = f.parseUnresolved("0123", new ParsePosition(0));
        Assert.assertEquals(parsed.getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(parsed.getLong(ChronoField.DAY_OF_MONTH), 23L);
    }

    @Test
    public void test_appendValue_subsequent2_parse5() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NORMAL).appendValue(ChronoField.DAY_OF_MONTH, 2).appendLiteral('4');
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear,1,2,NORMAL)Value(DayOfMonth,2)'4'");
        TemporalAccessor parsed = f.parseUnresolved("01234", new ParsePosition(0));
        Assert.assertEquals(parsed.getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(parsed.getLong(ChronoField.DAY_OF_MONTH), 23L);
    }

    @Test
    public void test_appendValue_subsequent3_parse6() throws Exception {
        builder.appendValue(ChronoField.YEAR, 4, 10, SignStyle.EXCEEDS_PAD).appendValue(ChronoField.MONTH_OF_YEAR, 2).appendValue(ChronoField.DAY_OF_MONTH, 2);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(Year,4,10,EXCEEDS_PAD)Value(MonthOfYear,2)Value(DayOfMonth,2)");
        TemporalAccessor parsed = f.parseUnresolved("20090630", new ParsePosition(0));
        Assert.assertEquals(parsed.getLong(ChronoField.YEAR), 2009L);
        Assert.assertEquals(parsed.getLong(ChronoField.MONTH_OF_YEAR), 6L);
        Assert.assertEquals(parsed.getLong(ChronoField.DAY_OF_MONTH), 30L);
    }

    // -----------------------------------------------------------------------
    @Test(expected = NullPointerException.class)
    public void test_appendValueReduced_null() throws Exception {
        builder.appendValueReduced(null, 2, 2, 2000);
    }

    @Test
    public void test_appendValueReduced() throws Exception {
        builder.appendValueReduced(ChronoField.YEAR, 2, 2, 2000);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "ReducedValue(Year,2,2,2000)");
        TemporalAccessor parsed = f.parseUnresolved("12", new ParsePosition(0));
        Assert.assertEquals(parsed.getLong(ChronoField.YEAR), 2012L);
    }

    @Test
    public void test_appendValueReduced_subsequent_parse() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR, 1, 2, SignStyle.NORMAL).appendValueReduced(ChronoField.YEAR, 2, 2, 2000);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear,1,2,NORMAL)ReducedValue(Year,2,2,2000)");
        ParsePosition ppos = new ParsePosition(0);
        TemporalAccessor parsed = f.parseUnresolved("123", ppos);
        Assert.assertNotNull(("Parse failed: " + (ppos.toString())), parsed);
        Assert.assertEquals(parsed.getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(parsed.getLong(ChronoField.YEAR), 2023L);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    @Test
    public void test_appendFraction_4arg() throws Exception {
        builder.appendFraction(ChronoField.MINUTE_OF_HOUR, 1, 9, false);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Fraction(MinuteOfHour,1,9)");
    }

    @Test(expected = NullPointerException.class)
    public void test_appendFraction_4arg_nullRule() throws Exception {
        builder.appendFraction(null, 1, 9, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendFraction_4arg_invalidRuleNotFixedSet() throws Exception {
        builder.appendFraction(ChronoField.DAY_OF_MONTH, 1, 9, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendFraction_4arg_minTooSmall() throws Exception {
        builder.appendFraction(ChronoField.MINUTE_OF_HOUR, (-1), 9, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendFraction_4arg_minTooBig() throws Exception {
        builder.appendFraction(ChronoField.MINUTE_OF_HOUR, 10, 9, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendFraction_4arg_maxTooSmall() throws Exception {
        builder.appendFraction(ChronoField.MINUTE_OF_HOUR, 0, (-1), false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendFraction_4arg_maxTooBig() throws Exception {
        builder.appendFraction(ChronoField.MINUTE_OF_HOUR, 1, 10, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_appendFraction_4arg_maxWidthMinWidth() throws Exception {
        builder.appendFraction(ChronoField.MINUTE_OF_HOUR, 9, 3, false);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    @Test
    public void test_appendText_1arg() throws Exception {
        builder.appendText(ChronoField.MONTH_OF_YEAR);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Text(MonthOfYear)");
    }

    @Test(expected = NullPointerException.class)
    public void test_appendText_1arg_null() throws Exception {
        builder.appendText(null);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_appendText_2arg() throws Exception {
        builder.appendText(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Text(MonthOfYear,SHORT)");
    }

    @Test(expected = NullPointerException.class)
    public void test_appendText_2arg_nullRule() throws Exception {
        builder.appendText(null, TextStyle.SHORT);
    }

    @Test(expected = NullPointerException.class)
    public void test_appendText_2arg_nullStyle() throws Exception {
        builder.appendText(ChronoField.MONTH_OF_YEAR, ((TextStyle) (null)));
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_appendTextMap() throws Exception {
        Map<Long, String> map = new HashMap<>();
        map.put(1L, "JNY");
        map.put(2L, "FBY");
        map.put(3L, "MCH");
        map.put(4L, "APL");
        map.put(5L, "MAY");
        map.put(6L, "JUN");
        map.put(7L, "JLY");
        map.put(8L, "AGT");
        map.put(9L, "SPT");
        map.put(10L, "OBR");
        map.put(11L, "NVR");
        map.put(12L, "DBR");
        builder.appendText(ChronoField.MONTH_OF_YEAR, map);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Text(MonthOfYear)");// TODO: toString should be different?

    }

    @Test(expected = NullPointerException.class)
    public void test_appendTextMap_nullRule() throws Exception {
        builder.appendText(null, new HashMap<Long, String>());
    }

    @Test(expected = NullPointerException.class)
    public void test_appendTextMap_nullStyle() throws Exception {
        builder.appendText(ChronoField.MONTH_OF_YEAR, ((Map<Long, String>) (null)));
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    @Test
    public void test_appendOffsetId() throws Exception {
        builder.appendOffsetId();
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Offset(+HH:MM:ss,'Z')");
    }

    @Test(expected = NullPointerException.class)
    public void test_appendOffset_3arg_nullText() throws Exception {
        builder.appendOffset("+HH:MM", null);
    }

    @Test(expected = NullPointerException.class)
    public void test_appendOffset_3arg_nullPattern() throws Exception {
        builder.appendOffset(null, "Z");
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    @Test
    public void test_appendZoneId() throws Exception {
        builder.appendZoneId();
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "ZoneId()");
    }

    @Test
    public void test_appendZoneText_1arg() throws Exception {
        builder.appendZoneText(TextStyle.FULL);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "ZoneText(FULL)");
    }

    @Test(expected = NullPointerException.class)
    public void test_appendZoneText_1arg_nullText() throws Exception {
        builder.appendZoneText(null);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    @Test
    public void test_padNext_1arg() {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).appendLiteral(':').padNext(2).appendValue(ChronoField.DAY_OF_MONTH);
        Assert.assertEquals(builder.toFormatter().format(LocalDate.of(2013, 2, 1)), "2: 1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_padNext_1arg_invalidWidth() throws Exception {
        builder.padNext(0);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_padNext_2arg_dash() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).appendLiteral(':').padNext(2, '-').appendValue(ChronoField.DAY_OF_MONTH);
        Assert.assertEquals(builder.toFormatter().format(LocalDate.of(2013, 2, 1)), "2:-1");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_padNext_2arg_invalidWidth() throws Exception {
        builder.padNext(0, '-');
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_padOptional() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).appendLiteral(':').padNext(5).optionalStart().appendValue(ChronoField.DAY_OF_MONTH).optionalEnd().appendLiteral(':').appendValue(ChronoField.YEAR);
        Assert.assertEquals(builder.toFormatter().format(LocalDate.of(2013, 2, 1)), "2:    1:2013");
        Assert.assertEquals(builder.toFormatter().format(YearMonth.of(2013, 2)), "2:     :2013");
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    @Test
    public void test_optionalStart_noEnd() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).optionalStart().appendValue(ChronoField.DAY_OF_MONTH).appendValue(ChronoField.DAY_OF_WEEK);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear)[Value(DayOfMonth)Value(DayOfWeek)]");
    }

    @Test
    public void test_optionalStart2_noEnd() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).optionalStart().appendValue(ChronoField.DAY_OF_MONTH).optionalStart().appendValue(ChronoField.DAY_OF_WEEK);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear)[Value(DayOfMonth)[Value(DayOfWeek)]]");
    }

    @Test
    public void test_optionalStart_doubleStart() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).optionalStart().optionalStart().appendValue(ChronoField.DAY_OF_MONTH);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear)[[Value(DayOfMonth)]]");
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_optionalEnd() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).optionalStart().appendValue(ChronoField.DAY_OF_MONTH).optionalEnd().appendValue(ChronoField.DAY_OF_WEEK);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear)[Value(DayOfMonth)]Value(DayOfWeek)");
    }

    @Test
    public void test_optionalEnd2() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).optionalStart().appendValue(ChronoField.DAY_OF_MONTH).optionalStart().appendValue(ChronoField.DAY_OF_WEEK).optionalEnd().appendValue(ChronoField.DAY_OF_MONTH).optionalEnd();
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear)[Value(DayOfMonth)[Value(DayOfWeek)]Value(DayOfMonth)]");
    }

    @Test
    public void test_optionalEnd_doubleStartSingleEnd() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).optionalStart().optionalStart().appendValue(ChronoField.DAY_OF_MONTH).optionalEnd();
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear)[[Value(DayOfMonth)]]");
    }

    @Test
    public void test_optionalEnd_doubleStartDoubleEnd() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).optionalStart().optionalStart().appendValue(ChronoField.DAY_OF_MONTH).optionalEnd().optionalEnd();
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear)[[Value(DayOfMonth)]]");
    }

    @Test
    public void test_optionalStartEnd_immediateStartEnd() throws Exception {
        builder.appendValue(ChronoField.MONTH_OF_YEAR).optionalStart().optionalEnd().appendValue(ChronoField.DAY_OF_MONTH);
        DateTimeFormatter f = builder.toFormatter();
        Assert.assertEquals(f.toString(), "Value(MonthOfYear)Value(DayOfMonth)");
    }

    @Test(expected = IllegalStateException.class)
    public void test_optionalEnd_noStart() throws Exception {
        builder.optionalEnd();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_getLocalizedDateTimePatternIAE() {
        DateTimeFormatterBuilder.getLocalizedDateTimePattern(null, null, IsoChronology.INSTANCE, Locale.US);
    }

    @Test(expected = NullPointerException.class)
    public void test_getLocalizedChronoNPE() {
        DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, FormatStyle.SHORT, null, Locale.US);
    }

    @Test(expected = NullPointerException.class)
    public void test_getLocalizedLocaleNPE() {
        DateTimeFormatterBuilder.getLocalizedDateTimePattern(FormatStyle.SHORT, FormatStyle.SHORT, IsoChronology.INSTANCE, null);
    }
}

