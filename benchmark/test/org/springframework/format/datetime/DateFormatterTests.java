/**
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.format.datetime;


import ISO.DATE;
import ISO.DATE_TIME;
import ISO.NONE;
import ISO.TIME;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Tests for {@link DateFormatter}.
 *
 * @author Keith Donald
 * @author Phillip Webb
 */
public class DateFormatterTests {
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void shouldPrintAndParseDefault() throws Exception {
        DateFormatter formatter = new DateFormatter();
        formatter.setTimeZone(DateFormatterTests.UTC);
        Date date = getDate(2009, Calendar.JUNE, 1);
        Assert.assertThat(formatter.print(date, Locale.US), is("Jun 1, 2009"));
        Assert.assertThat(formatter.parse("Jun 1, 2009", Locale.US), is(date));
    }

    @Test
    public void shouldPrintAndParseFromPattern() throws ParseException {
        DateFormatter formatter = new DateFormatter("yyyy-MM-dd");
        formatter.setTimeZone(DateFormatterTests.UTC);
        Date date = getDate(2009, Calendar.JUNE, 1);
        Assert.assertThat(formatter.print(date, Locale.US), is("2009-06-01"));
        Assert.assertThat(formatter.parse("2009-06-01", Locale.US), is(date));
    }

    @Test
    public void shouldPrintAndParseShort() throws Exception {
        DateFormatter formatter = new DateFormatter();
        formatter.setTimeZone(DateFormatterTests.UTC);
        formatter.setStyle(DateFormat.SHORT);
        Date date = getDate(2009, Calendar.JUNE, 1);
        Assert.assertThat(formatter.print(date, Locale.US), is("6/1/09"));
        Assert.assertThat(formatter.parse("6/1/09", Locale.US), is(date));
    }

    @Test
    public void shouldPrintAndParseMedium() throws Exception {
        DateFormatter formatter = new DateFormatter();
        formatter.setTimeZone(DateFormatterTests.UTC);
        formatter.setStyle(DateFormat.MEDIUM);
        Date date = getDate(2009, Calendar.JUNE, 1);
        Assert.assertThat(formatter.print(date, Locale.US), is("Jun 1, 2009"));
        Assert.assertThat(formatter.parse("Jun 1, 2009", Locale.US), is(date));
    }

    @Test
    public void shouldPrintAndParseLong() throws Exception {
        DateFormatter formatter = new DateFormatter();
        formatter.setTimeZone(DateFormatterTests.UTC);
        formatter.setStyle(DateFormat.LONG);
        Date date = getDate(2009, Calendar.JUNE, 1);
        Assert.assertThat(formatter.print(date, Locale.US), is("June 1, 2009"));
        Assert.assertThat(formatter.parse("June 1, 2009", Locale.US), is(date));
    }

    @Test
    public void shouldPrintAndParseFull() throws Exception {
        DateFormatter formatter = new DateFormatter();
        formatter.setTimeZone(DateFormatterTests.UTC);
        formatter.setStyle(DateFormat.FULL);
        Date date = getDate(2009, Calendar.JUNE, 1);
        Assert.assertThat(formatter.print(date, Locale.US), is("Monday, June 1, 2009"));
        Assert.assertThat(formatter.parse("Monday, June 1, 2009", Locale.US), is(date));
    }

    @Test
    public void shouldPrintAndParseISODate() throws Exception {
        DateFormatter formatter = new DateFormatter();
        formatter.setTimeZone(DateFormatterTests.UTC);
        formatter.setIso(DATE);
        Date date = getDate(2009, Calendar.JUNE, 1, 14, 23, 5, 3);
        Assert.assertThat(formatter.print(date, Locale.US), is("2009-06-01"));
        Assert.assertThat(formatter.parse("2009-6-01", Locale.US), is(getDate(2009, Calendar.JUNE, 1)));
    }

    @Test
    public void shouldPrintAndParseISOTime() throws Exception {
        DateFormatter formatter = new DateFormatter();
        formatter.setTimeZone(DateFormatterTests.UTC);
        formatter.setIso(TIME);
        Date date = getDate(2009, Calendar.JANUARY, 1, 14, 23, 5, 3);
        Assert.assertThat(formatter.print(date, Locale.US), is("14:23:05.003Z"));
        Assert.assertThat(formatter.parse("14:23:05.003Z", Locale.US), is(getDate(1970, Calendar.JANUARY, 1, 14, 23, 5, 3)));
    }

    @Test
    public void shouldPrintAndParseISODateTime() throws Exception {
        DateFormatter formatter = new DateFormatter();
        formatter.setTimeZone(DateFormatterTests.UTC);
        formatter.setIso(DATE_TIME);
        Date date = getDate(2009, Calendar.JUNE, 1, 14, 23, 5, 3);
        Assert.assertThat(formatter.print(date, Locale.US), is("2009-06-01T14:23:05.003Z"));
        Assert.assertThat(formatter.parse("2009-06-01T14:23:05.003Z", Locale.US), is(date));
    }

    @Test
    public void shouldSupportJodaStylePatterns() throws Exception {
        String[] chars = new String[]{ "S", "M", "-" };
        for (String d : chars) {
            for (String t : chars) {
                String style = d + t;
                if (!(style.equals("--"))) {
                    Date date = getDate(2009, Calendar.JUNE, 10, 14, 23, 0, 0);
                    if (t.equals("-")) {
                        date = getDate(2009, Calendar.JUNE, 10);
                    } else
                        if (d.equals("-")) {
                            date = getDate(1970, Calendar.JANUARY, 1, 14, 23, 0, 0);
                        }

                    testJodaStylePatterns(style, Locale.US, date);
                }
            }
        }
    }

    @Test
    public void shouldThrowOnUnsupportedStylePattern() throws Exception {
        DateFormatter formatter = new DateFormatter();
        formatter.setStylePattern("OO");
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Unsupported style pattern 'OO'");
        formatter.parse("2009", Locale.US);
    }

    @Test
    public void shouldUseCorrectOrder() throws Exception {
        DateFormatter formatter = new DateFormatter();
        formatter.setTimeZone(DateFormatterTests.UTC);
        formatter.setStyle(DateFormat.SHORT);
        formatter.setStylePattern("L-");
        formatter.setIso(DATE_TIME);
        formatter.setPattern("yyyy");
        Date date = getDate(2009, Calendar.JUNE, 1, 14, 23, 5, 3);
        Assert.assertThat("uses pattern", formatter.print(date, Locale.US), is("2009"));
        formatter.setPattern("");
        Assert.assertThat("uses ISO", formatter.print(date, Locale.US), is("2009-06-01T14:23:05.003Z"));
        formatter.setIso(NONE);
        Assert.assertThat("uses style pattern", formatter.print(date, Locale.US), is("June 1, 2009"));
        formatter.setStylePattern("");
        Assert.assertThat("uses style", formatter.print(date, Locale.US), is("6/1/09"));
    }
}

