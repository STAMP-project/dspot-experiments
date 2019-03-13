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
package org.springframework.format.datetime.standard;


import ISO.DATE;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Phillip Webb
 * @author Sam Brannen
 */
public class DateTimeFormatterFactoryTests {
    // Potential test timezone, both have daylight savings on October 21st
    private static final TimeZone ZURICH = TimeZone.getTimeZone("Europe/Zurich");

    private static final TimeZone NEW_YORK = TimeZone.getTimeZone("America/New_York");

    // Ensure that we are testing against a timezone other than the default.
    private static final TimeZone TEST_TIMEZONE = (DateTimeFormatterFactoryTests.ZURICH.equals(TimeZone.getDefault())) ? DateTimeFormatterFactoryTests.NEW_YORK : DateTimeFormatterFactoryTests.ZURICH;

    private DateTimeFormatterFactory factory = new DateTimeFormatterFactory();

    private LocalDateTime dateTime = LocalDateTime.of(2009, 10, 21, 12, 10, 0, 0);

    @Test
    public void createDateTimeFormatter() {
        Assert.assertThat(factory.createDateTimeFormatter().toString(), is(equalTo(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).toString())));
    }

    @Test
    public void createDateTimeFormatterWithPattern() {
        factory = new DateTimeFormatterFactory("yyyyMMddHHmmss");
        DateTimeFormatter formatter = factory.createDateTimeFormatter();
        Assert.assertThat(formatter.format(dateTime), is("20091021121000"));
    }

    @Test
    public void createDateTimeFormatterWithNullFallback() {
        DateTimeFormatter formatter = factory.createDateTimeFormatter(null);
        Assert.assertThat(formatter, is(nullValue()));
    }

    @Test
    public void createDateTimeFormatterWithFallback() {
        DateTimeFormatter fallback = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG);
        DateTimeFormatter formatter = factory.createDateTimeFormatter(fallback);
        Assert.assertThat(formatter, is(sameInstance(fallback)));
    }

    @Test
    public void createDateTimeFormatterInOrderOfPropertyPriority() {
        factory.setStylePattern("SS");
        String value = applyLocale(factory.createDateTimeFormatter()).format(dateTime);
        Assert.assertTrue(value.startsWith("10/21/09"));
        Assert.assertTrue(value.endsWith("12:10 PM"));
        factory.setIso(DATE);
        Assert.assertThat(applyLocale(factory.createDateTimeFormatter()).format(dateTime), is("2009-10-21"));
        factory.setPattern("yyyyMMddHHmmss");
        Assert.assertThat(factory.createDateTimeFormatter().format(dateTime), is("20091021121000"));
    }

    @Test
    public void createDateTimeFormatterWithTimeZone() {
        factory.setPattern("yyyyMMddHHmmss Z");
        factory.setTimeZone(DateTimeFormatterFactoryTests.TEST_TIMEZONE);
        ZoneId dateTimeZone = DateTimeFormatterFactoryTests.TEST_TIMEZONE.toZoneId();
        ZonedDateTime dateTime = ZonedDateTime.of(2009, 10, 21, 12, 10, 0, 0, dateTimeZone);
        String offset = (DateTimeFormatterFactoryTests.TEST_TIMEZONE.equals(DateTimeFormatterFactoryTests.NEW_YORK)) ? "-0400" : "+0200";
        Assert.assertThat(factory.createDateTimeFormatter().format(dateTime), is(("20091021121000 " + offset)));
    }
}

