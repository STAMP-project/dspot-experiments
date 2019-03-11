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
 * Copyright (c) 2008-2012, Stephen Colebourne & Michael Nascimento Santos
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
package test.java.time.format;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.text.ParsePosition;
import java.time.format.TextStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Test TextPrinterParser.
 */
@RunWith(DataProviderRunner.class)
public class TestTextParser extends AbstractTestPrinterParser {
    static final Locale RUSSIAN = new Locale("ru");

    static final Locale FINNISH = new Locale("fi");

    // -----------------------------------------------------------------------
    @Test
    public void test_parse_midStr() throws Exception {
        ParsePosition pos = new ParsePosition(3);
        Assert.assertEquals(getFormatter(ChronoField.DAY_OF_WEEK, TextStyle.FULL).parseUnresolved("XxxMondayXxx", pos).getLong(ChronoField.DAY_OF_WEEK), 1L);
        Assert.assertEquals(pos.getIndex(), 9);
    }

    @Test
    public void test_parse_remainderIgnored() throws Exception {
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.DAY_OF_WEEK, TextStyle.SHORT).parseUnresolved("Wednesday", pos).getLong(ChronoField.DAY_OF_WEEK), 3L);
        Assert.assertEquals(pos.getIndex(), 3);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_parse_noMatch1() throws Exception {
        ParsePosition pos = new ParsePosition(0);
        TemporalAccessor parsed = getFormatter(ChronoField.DAY_OF_WEEK, TextStyle.FULL).parseUnresolved("Munday", pos);
        Assert.assertEquals(pos.getErrorIndex(), 0);
        Assert.assertEquals(parsed, null);
    }

    @Test
    public void test_parse_noMatch2() throws Exception {
        ParsePosition pos = new ParsePosition(3);
        TemporalAccessor parsed = getFormatter(ChronoField.DAY_OF_WEEK, TextStyle.FULL).parseUnresolved("Monday", pos);
        Assert.assertEquals(pos.getErrorIndex(), 3);
        Assert.assertEquals(parsed, null);
    }

    @Test
    public void test_parse_noMatch_atEnd() throws Exception {
        ParsePosition pos = new ParsePosition(6);
        TemporalAccessor parsed = getFormatter(ChronoField.DAY_OF_WEEK, TextStyle.FULL).parseUnresolved("Monday", pos);
        Assert.assertEquals(pos.getErrorIndex(), 6);
        Assert.assertEquals(parsed, null);
    }

    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    // -----------------------------------------------------------------------
    @Test
    public void test_parse_full_strict_full_match() throws Exception {
        setStrict(true);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.FULL).parseUnresolved("January", pos).getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(pos.getIndex(), 7);
    }

    @Test
    public void test_parse_full_strict_short_noMatch() throws Exception {
        setStrict(true);
        ParsePosition pos = new ParsePosition(0);
        getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.FULL).parseUnresolved("Janua", pos);
        Assert.assertEquals(pos.getErrorIndex(), 0);
    }

    @Test
    public void test_parse_full_strict_number_noMatch() throws Exception {
        setStrict(true);
        ParsePosition pos = new ParsePosition(0);
        getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.FULL).parseUnresolved("1", pos);
        Assert.assertEquals(pos.getErrorIndex(), 0);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_parse_short_strict_full_match() throws Exception {
        setStrict(true);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT).parseUnresolved("January", pos).getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(pos.getIndex(), 3);
    }

    @Test
    public void test_parse_short_strict_short_match() throws Exception {
        setStrict(true);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT).parseUnresolved("Janua", pos).getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(pos.getIndex(), 3);
    }

    @Test
    public void test_parse_short_strict_number_noMatch() throws Exception {
        setStrict(true);
        ParsePosition pos = new ParsePosition(0);
        getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT).parseUnresolved("1", pos);
        Assert.assertEquals(pos.getErrorIndex(), 0);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_parse_french_short_strict_full_noMatch() throws Exception {
        setStrict(true);
        ParsePosition pos = new ParsePosition(0);
        getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT).withLocale(Locale.FRENCH).parseUnresolved("janvier", pos);
        Assert.assertEquals(pos.getErrorIndex(), 0);
    }

    @Test
    public void test_parse_french_short_strict_short_match() throws Exception {
        setStrict(true);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT).withLocale(Locale.FRENCH).parseUnresolved("janv.", pos).getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(pos.getIndex(), 5);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_parse_full_lenient_full_match() throws Exception {
        setStrict(false);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.FULL).parseUnresolved("January.", pos).getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(pos.getIndex(), 7);
    }

    @Test
    public void test_parse_full_lenient_short_match() throws Exception {
        setStrict(false);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.FULL).parseUnresolved("Janua", pos).getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(pos.getIndex(), 3);
    }

    @Test
    public void test_parse_full_lenient_number_match() throws Exception {
        setStrict(false);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.FULL).parseUnresolved("1", pos).getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(pos.getIndex(), 1);
    }

    // -----------------------------------------------------------------------
    @Test
    public void test_parse_short_lenient_full_match() throws Exception {
        setStrict(false);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT).parseUnresolved("January", pos).getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(pos.getIndex(), 7);
    }

    @Test
    public void test_parse_short_lenient_short_match() throws Exception {
        setStrict(false);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT).parseUnresolved("Janua", pos).getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(pos.getIndex(), 3);
    }

    @Test
    public void test_parse_short_lenient_number_match() throws Exception {
        setStrict(false);
        ParsePosition pos = new ParsePosition(0);
        Assert.assertEquals(getFormatter(ChronoField.MONTH_OF_YEAR, TextStyle.SHORT).parseUnresolved("1", pos).getLong(ChronoField.MONTH_OF_YEAR), 1L);
        Assert.assertEquals(pos.getIndex(), 1);
    }
}

