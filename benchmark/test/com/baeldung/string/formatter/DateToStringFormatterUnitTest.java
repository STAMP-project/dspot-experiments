package com.baeldung.string.formatter;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class DateToStringFormatterUnitTest {
    private static final String DATE_FORMAT = "MMM d, yyyy HH:mm a";

    private static final String EXPECTED_STRING_DATE = "Aug 1, 2018 12:00 PM";

    private static Date date;

    @Test
    public void whenDateConvertedUsingSimpleDateFormatToString_thenCorrect() {
        DateFormat formatter = new SimpleDateFormat(DateToStringFormatterUnitTest.DATE_FORMAT);
        String formattedDate = formatter.format(DateToStringFormatterUnitTest.date);
        Assert.assertEquals(DateToStringFormatterUnitTest.EXPECTED_STRING_DATE, formattedDate);
    }

    @Test
    public void whenDateConvertedUsingDateFormatToString_thenCorrect() {
        String formattedDate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT, Locale.US).format(DateToStringFormatterUnitTest.date);
        Assert.assertEquals(DateToStringFormatterUnitTest.EXPECTED_STRING_DATE, formattedDate);
    }

    @Test
    public void whenDateConvertedUsingFormatterToString_thenCorrect() {
        String formattedDate = String.format("%1$tb %1$te, %1$tY %1$tI:%1$tM %1$Tp", DateToStringFormatterUnitTest.date);
        Assert.assertEquals(DateToStringFormatterUnitTest.EXPECTED_STRING_DATE, formattedDate);
    }

    @Test
    public void whenDateConvertedUsingDateTimeApiToString_thenCorrect() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern(DateToStringFormatterUnitTest.DATE_FORMAT);
        Instant instant = DateToStringFormatterUnitTest.date.toInstant();
        LocalDateTime ldt = instant.atZone(ZoneId.of("CET")).toLocalDateTime();
        String formattedDate = ldt.format(fmt);
        Assert.assertEquals(DateToStringFormatterUnitTest.EXPECTED_STRING_DATE, formattedDate);
    }
}

