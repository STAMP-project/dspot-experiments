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
package org.springframework.format.datetime.joda;


import ISO.DATE_TIME;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.MonthDay;
import org.joda.time.Period;
import org.joda.time.YearMonth;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.DateTimeFormat;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.validation.DataBinder;


/**
 *
 *
 * @author Keith Donald
 * @author Juergen Hoeller
 * @author Phillip Webb
 */
public class JodaTimeFormattingTests {
    private FormattingConversionService conversionService;

    private DataBinder binder;

    @Test
    public void testJodaTimePatternsForStyle() {
        System.out.println(DateTimeFormat.patternForStyle("SS", LocaleContextHolder.getLocale()));
        System.out.println(DateTimeFormat.patternForStyle("MM", LocaleContextHolder.getLocale()));
        System.out.println(DateTimeFormat.patternForStyle("LL", LocaleContextHolder.getLocale()));
        System.out.println(DateTimeFormat.patternForStyle("FF", LocaleContextHolder.getLocale()));
    }

    @Test
    public void testBindLocalDate() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDate", "10/31/09");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("10/31/09", binder.getBindingResult().getFieldValue("localDate"));
    }

    @Test
    public void testBindLocalDateWithSpecificStyle() {
        JodaTimeFormatterRegistrar registrar = new JodaTimeFormatterRegistrar();
        registrar.setDateStyle("L");
        setup(registrar);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDate", "October 31, 2009");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("October 31, 2009", binder.getBindingResult().getFieldValue("localDate"));
    }

    @Test
    public void testBindLocalDateWithSpecificFormatter() {
        JodaTimeFormatterRegistrar registrar = new JodaTimeFormatterRegistrar();
        registrar.setDateFormatter(DateTimeFormat.forPattern("yyyyMMdd"));
        setup(registrar);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDate", "20091031");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("20091031", binder.getBindingResult().getFieldValue("localDate"));
    }

    @Test
    public void testBindLocalDateArray() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDate", new String[]{ "10/31/09" });
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
    }

    @Test
    public void testBindLocalDateAnnotated() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDateAnnotated", "Oct 31, 2009");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("Oct 31, 2009", binder.getBindingResult().getFieldValue("localDateAnnotated"));
    }

    @Test
    public void testBindLocalDateAnnotatedWithError() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDateAnnotated", "Oct 031, 2009");
        binder.bind(propertyValues);
        Assert.assertEquals(1, binder.getBindingResult().getFieldErrorCount("localDateAnnotated"));
        Assert.assertEquals("Oct 031, 2009", binder.getBindingResult().getFieldValue("localDateAnnotated"));
    }

    @Test
    public void testBindNestedLocalDateAnnotated() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("children[0].localDateAnnotated", "Oct 31, 2009");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("Oct 31, 2009", binder.getBindingResult().getFieldValue("children[0].localDateAnnotated"));
    }

    @Test
    public void testBindLocalDateAnnotatedWithDirectFieldAccess() {
        binder.initDirectFieldAccess();
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDateAnnotated", "Oct 31, 2009");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("Oct 31, 2009", binder.getBindingResult().getFieldValue("localDateAnnotated"));
    }

    @Test
    public void testBindLocalDateAnnotatedWithDirectFieldAccessAndError() {
        binder.initDirectFieldAccess();
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDateAnnotated", "Oct 031, 2009");
        binder.bind(propertyValues);
        Assert.assertEquals(1, binder.getBindingResult().getFieldErrorCount("localDateAnnotated"));
        Assert.assertEquals("Oct 031, 2009", binder.getBindingResult().getFieldValue("localDateAnnotated"));
    }

    @Test
    public void testBindLocalTime() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localTime", "12:00 PM");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("12:00 PM", binder.getBindingResult().getFieldValue("localTime"));
    }

    @Test
    public void testBindLocalTimeWithSpecificStyle() {
        JodaTimeFormatterRegistrar registrar = new JodaTimeFormatterRegistrar();
        registrar.setTimeStyle("M");
        setup(registrar);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localTime", "12:00:00 PM");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("12:00:00 PM", binder.getBindingResult().getFieldValue("localTime"));
    }

    @Test
    public void testBindLocalTimeWithSpecificFormatter() {
        JodaTimeFormatterRegistrar registrar = new JodaTimeFormatterRegistrar();
        registrar.setTimeFormatter(DateTimeFormat.forPattern("HHmmss"));
        setup(registrar);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localTime", "130000");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("130000", binder.getBindingResult().getFieldValue("localTime"));
    }

    @Test
    public void testBindLocalTimeAnnotated() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localTimeAnnotated", "12:00:00 PM");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("12:00:00 PM", binder.getBindingResult().getFieldValue("localTimeAnnotated"));
    }

    @Test
    public void testBindLocalDateTime() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDateTime", new LocalDateTime(2009, 10, 31, 12, 0));
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        String value = binder.getBindingResult().getFieldValue("localDateTime").toString();
        Assert.assertTrue(value.startsWith("10/31/09"));
        Assert.assertTrue(value.endsWith("12:00 PM"));
    }

    @Test
    public void testBindLocalDateTimeAnnotated() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDateTimeAnnotated", new LocalDateTime(2009, 10, 31, 12, 0));
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        String value = binder.getBindingResult().getFieldValue("localDateTimeAnnotated").toString();
        Assert.assertTrue(value.startsWith("Oct 31, 2009"));
        Assert.assertTrue(value.endsWith("12:00 PM"));
    }

    @Test
    public void testBindDateTime() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("dateTime", new DateTime(2009, 10, 31, 12, 0, ISOChronology.getInstanceUTC()));
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        String value = binder.getBindingResult().getFieldValue("dateTime").toString();
        Assert.assertTrue(value.startsWith("10/31/09"));
    }

    @Test
    public void testBindDateTimeWithSpecificStyle() {
        JodaTimeFormatterRegistrar registrar = new JodaTimeFormatterRegistrar();
        registrar.setDateTimeStyle("MM");
        setup(registrar);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("localDateTime", new LocalDateTime(2009, 10, 31, 12, 0));
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        String value = binder.getBindingResult().getFieldValue("localDateTime").toString();
        Assert.assertTrue(value.startsWith("Oct 31, 2009"));
        Assert.assertTrue(value.endsWith("12:00:00 PM"));
    }

    @Test
    public void testBindDateTimeISO() {
        JodaTimeFormatterRegistrar registrar = new JodaTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        setup(registrar);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("dateTime", "2009-10-31T12:00:00.000Z");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("2009-10-31T07:00:00.000-05:00", binder.getBindingResult().getFieldValue("dateTime"));
    }

    @Test
    public void testBindDateTimeWithSpecificFormatter() {
        JodaTimeFormatterRegistrar registrar = new JodaTimeFormatterRegistrar();
        registrar.setDateTimeFormatter(DateTimeFormat.forPattern("yyyyMMddHHmmss"));
        setup(registrar);
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("dateTime", "20091031130000");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("20091031130000", binder.getBindingResult().getFieldValue("dateTime"));
    }

    @Test
    public void testBindDateTimeAnnotated() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("dateTimeAnnotated", new DateTime(2009, 10, 31, 12, 0, ISOChronology.getInstanceUTC()));
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        String value = binder.getBindingResult().getFieldValue("dateTimeAnnotated").toString();
        Assert.assertTrue(value.startsWith("Oct 31, 2009"));
    }

    @Test
    public void testBindDateTimeAnnotatedPattern() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("dateTimeAnnotatedPattern", "10/31/09 12:00 PM");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("10/31/09 12:00 PM", binder.getBindingResult().getFieldValue("dateTimeAnnotatedPattern"));
    }

    @Test
    public void testBindDateTimeOverflow() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("dateTimeAnnotatedPattern", "02/29/09 12:00 PM");
        binder.bind(propertyValues);
        Assert.assertEquals(1, binder.getBindingResult().getErrorCount());
    }

    @Test
    public void testBindDateTimeAnnotatedDefault() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("dateTimeAnnotatedDefault", new DateTime(2009, 10, 31, 12, 0, ISOChronology.getInstanceUTC()));
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        String value = binder.getBindingResult().getFieldValue("dateTimeAnnotatedDefault").toString();
        Assert.assertTrue(value.startsWith("10/31/09"));
    }

    @Test
    public void testBindDateWithErrorAvoidingDateConstructor() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("date", "Sat, 12 Aug 1995 13:30:00 GMT");
        binder.bind(propertyValues);
        Assert.assertEquals(1, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("Sat, 12 Aug 1995 13:30:00 GMT", binder.getBindingResult().getFieldValue("date"));
    }

    @Test
    public void testBindDateWithoutErrorFallingBackToDateConstructor() {
        DataBinder binder = new DataBinder(new JodaTimeFormattingTests.JodaTimeBean());
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("date", "Sat, 12 Aug 1995 13:30:00 GMT");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
    }

    @Test
    public void testBindDateAnnotated() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("dateAnnotated", "10/31/09");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("10/31/09", binder.getBindingResult().getFieldValue("dateAnnotated"));
    }

    @Test
    public void testBindCalendarAnnotated() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("calendarAnnotated", "10/31/09");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("10/31/09", binder.getBindingResult().getFieldValue("calendarAnnotated"));
    }

    @Test
    public void testBindLong() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("millis", "1256961600");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("1256961600", binder.getBindingResult().getFieldValue("millis"));
    }

    @Test
    public void testBindLongAnnotated() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("millisAnnotated", "10/31/09");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("10/31/09", binder.getBindingResult().getFieldValue("millisAnnotated"));
    }

    @Test
    public void testBindISODate() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("isoDate", "2009-10-31");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("2009-10-31", binder.getBindingResult().getFieldValue("isoDate"));
    }

    @Test
    public void testBindISOTime() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("isoTime", "12:00:00.000-05:00");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("12:00:00.000", binder.getBindingResult().getFieldValue("isoTime"));
    }

    @Test
    public void testBindISODateTime() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("isoDateTime", "2009-10-31T12:00:00.000Z");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("2009-10-31T07:00:00.000-05:00", binder.getBindingResult().getFieldValue("isoDateTime"));
    }

    @Test
    public void testBindInstantAnnotated() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("instantAnnotated", "2009-10-31T12:00:00.000Z");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("2009-10-31T07:00:00.000-05:00", binder.getBindingResult().getFieldValue("instantAnnotated"));
    }

    @Test
    public void testBindMutableDateTimeAnnotated() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("mutableDateTimeAnnotated", "2009-10-31T12:00:00.000Z");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertEquals("2009-10-31T07:00:00.000-05:00", binder.getBindingResult().getFieldValue("mutableDateTimeAnnotated"));
    }

    @Test
    public void dateToStringWithFormat() {
        JodaTimeFormatterRegistrar registrar = new JodaTimeFormatterRegistrar();
        registrar.setDateTimeFormatter(DateTimeFormat.shortDateTime());
        setup(registrar);
        Date date = new Date();
        Object actual = this.conversionService.convert(date, TypeDescriptor.valueOf(Date.class), TypeDescriptor.valueOf(String.class));
        String expected = JodaTimeContextHolder.getFormatter(DateTimeFormat.shortDateTime(), Locale.US).print(new DateTime(date));
        Assert.assertEquals(expected, actual);
    }

    // SPR-10105
    @Test
    @SuppressWarnings("deprecation")
    public void stringToDateWithoutGlobalFormat() {
        String string = "Sat, 12 Aug 1995 13:30:00 GM";
        Date date = this.conversionService.convert(string, Date.class);
        Assert.assertThat(date, equalTo(new Date(string)));
    }

    // SPR-10105
    @Test
    public void stringToDateWithGlobalFormat() {
        JodaTimeFormatterRegistrar registrar = new JodaTimeFormatterRegistrar();
        DateTimeFormatterFactory factory = new DateTimeFormatterFactory();
        factory.setIso(DATE_TIME);
        registrar.setDateTimeFormatter(factory.createDateTimeFormatter());
        setup(registrar);
        // This is a format that cannot be parsed by new Date(String)
        String string = "2009-10-31T07:00:00.000-05:00";
        Date date = this.conversionService.convert(string, Date.class);
        Assert.assertNotNull(date);
    }

    @Test
    public void testBindPeriod() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("period", "P6Y3M1D");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertTrue(binder.getBindingResult().getFieldValue("period").toString().equals("P6Y3M1D"));
    }

    @Test
    public void testBindDuration() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("duration", "PT72.345S");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertTrue(binder.getBindingResult().getFieldValue("duration").toString().equals("PT72.345S"));
    }

    @Test
    public void testBindYearMonth() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("yearMonth", "2007-12");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertTrue(binder.getBindingResult().getFieldValue("yearMonth").toString().equals("2007-12"));
    }

    @Test
    public void testBindMonthDay() {
        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("monthDay", "--12-03");
        binder.bind(propertyValues);
        Assert.assertEquals(0, binder.getBindingResult().getErrorCount());
        Assert.assertTrue(binder.getBindingResult().getFieldValue("monthDay").toString().equals("--12-03"));
    }

    @SuppressWarnings("unused")
    private static class JodaTimeBean {
        private LocalDate localDate;

        @org.springframework.format.annotation.DateTimeFormat(style = "M-")
        private LocalDate localDateAnnotated;

        private LocalTime localTime;

        @org.springframework.format.annotation.DateTimeFormat(style = "-M")
        private LocalTime localTimeAnnotated;

        private LocalDateTime localDateTime;

        @org.springframework.format.annotation.DateTimeFormat(style = "MS")
        private LocalDateTime localDateTimeAnnotated;

        private DateTime dateTime;

        @org.springframework.format.annotation.DateTimeFormat(style = "MS")
        private DateTime dateTimeAnnotated;

        @org.springframework.format.annotation.DateTimeFormat
        private Date date;

        @org.springframework.format.annotation.DateTimeFormat(style = "S-")
        private Date dateAnnotated;

        @org.springframework.format.annotation.DateTimeFormat(style = "S-")
        private Calendar calendarAnnotated;

        private Long millis;

        @org.springframework.format.annotation.DateTimeFormat
        private DateTime dateTimeAnnotatedDefault;

        private Long millisAnnotated;

        @org.springframework.format.annotation.DateTimeFormat(pattern = "M/d/yy h:mm a")
        private DateTime dateTimeAnnotatedPattern;

        @org.springframework.format.annotation.DateTimeFormat(iso = ISO.DATE)
        private LocalDate isoDate;

        @org.springframework.format.annotation.DateTimeFormat(iso = ISO.TIME)
        private LocalTime isoTime;

        @org.springframework.format.annotation.DateTimeFormat(iso = ISO.DATE_TIME)
        private DateTime isoDateTime;

        @org.springframework.format.annotation.DateTimeFormat(iso = ISO.DATE_TIME)
        private Instant instantAnnotated;

        @org.springframework.format.annotation.DateTimeFormat(iso = ISO.DATE_TIME)
        private Instant mutableDateTimeAnnotated;

        private Period period;

        private Duration duration;

        private YearMonth yearMonth;

        private MonthDay monthDay;

        private final List<JodaTimeFormattingTests.JodaTimeBean> children = new ArrayList<>();

        public LocalDate getLocalDate() {
            return localDate;
        }

        public void setLocalDate(LocalDate localDate) {
            this.localDate = localDate;
        }

        public LocalDate getLocalDateAnnotated() {
            return localDateAnnotated;
        }

        public void setLocalDateAnnotated(LocalDate localDateAnnotated) {
            this.localDateAnnotated = localDateAnnotated;
        }

        public LocalTime getLocalTime() {
            return localTime;
        }

        public void setLocalTime(LocalTime localTime) {
            this.localTime = localTime;
        }

        public LocalTime getLocalTimeAnnotated() {
            return localTimeAnnotated;
        }

        public void setLocalTimeAnnotated(LocalTime localTimeAnnotated) {
            this.localTimeAnnotated = localTimeAnnotated;
        }

        public LocalDateTime getLocalDateTime() {
            return localDateTime;
        }

        public void setLocalDateTime(LocalDateTime localDateTime) {
            this.localDateTime = localDateTime;
        }

        public LocalDateTime getLocalDateTimeAnnotated() {
            return localDateTimeAnnotated;
        }

        public void setLocalDateTimeAnnotated(LocalDateTime localDateTimeAnnotated) {
            this.localDateTimeAnnotated = localDateTimeAnnotated;
        }

        public DateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(DateTime dateTime) {
            this.dateTime = dateTime;
        }

        public DateTime getDateTimeAnnotated() {
            return dateTimeAnnotated;
        }

        public void setDateTimeAnnotated(DateTime dateTimeAnnotated) {
            this.dateTimeAnnotated = dateTimeAnnotated;
        }

        public DateTime getDateTimeAnnotatedPattern() {
            return dateTimeAnnotatedPattern;
        }

        public void setDateTimeAnnotatedPattern(DateTime dateTimeAnnotatedPattern) {
            this.dateTimeAnnotatedPattern = dateTimeAnnotatedPattern;
        }

        public DateTime getDateTimeAnnotatedDefault() {
            return dateTimeAnnotatedDefault;
        }

        public void setDateTimeAnnotatedDefault(DateTime dateTimeAnnotatedDefault) {
            this.dateTimeAnnotatedDefault = dateTimeAnnotatedDefault;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public Date getDateAnnotated() {
            return dateAnnotated;
        }

        public void setDateAnnotated(Date dateAnnotated) {
            this.dateAnnotated = dateAnnotated;
        }

        public Calendar getCalendarAnnotated() {
            return calendarAnnotated;
        }

        public void setCalendarAnnotated(Calendar calendarAnnotated) {
            this.calendarAnnotated = calendarAnnotated;
        }

        public Long getMillis() {
            return millis;
        }

        public void setMillis(Long millis) {
            this.millis = millis;
        }

        @org.springframework.format.annotation.DateTimeFormat(style = "S-")
        public Long getMillisAnnotated() {
            return millisAnnotated;
        }

        public void setMillisAnnotated(@org.springframework.format.annotation.DateTimeFormat(style = "S-")
        Long millisAnnotated) {
            this.millisAnnotated = millisAnnotated;
        }

        public LocalDate getIsoDate() {
            return isoDate;
        }

        public void setIsoDate(LocalDate isoDate) {
            this.isoDate = isoDate;
        }

        public LocalTime getIsoTime() {
            return isoTime;
        }

        public void setIsoTime(LocalTime isoTime) {
            this.isoTime = isoTime;
        }

        public DateTime getIsoDateTime() {
            return isoDateTime;
        }

        public void setIsoDateTime(DateTime isoDateTime) {
            this.isoDateTime = isoDateTime;
        }

        public Instant getInstantAnnotated() {
            return instantAnnotated;
        }

        public void setInstantAnnotated(Instant instantAnnotated) {
            this.instantAnnotated = instantAnnotated;
        }

        public Instant getMutableDateTimeAnnotated() {
            return mutableDateTimeAnnotated;
        }

        public void setMutableDateTimeAnnotated(Instant mutableDateTimeAnnotated) {
            this.mutableDateTimeAnnotated = mutableDateTimeAnnotated;
        }

        public Period getPeriod() {
            return period;
        }

        public void setPeriod(Period period) {
            this.period = period;
        }

        public Duration getDuration() {
            return duration;
        }

        public void setDuration(Duration duration) {
            this.duration = duration;
        }

        public YearMonth getYearMonth() {
            return yearMonth;
        }

        public void setYearMonth(YearMonth yearMonth) {
            this.yearMonth = yearMonth;
        }

        public MonthDay getMonthDay() {
            return monthDay;
        }

        public void setMonthDay(MonthDay monthDay) {
            this.monthDay = monthDay;
        }

        public List<JodaTimeFormattingTests.JodaTimeBean> getChildren() {
            return children;
        }
    }
}

