/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2015, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 */
package ch.qos.logback.classic.pattern;


import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import java.util.Calendar;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class SyslogStartConverterTest {
    private LoggerContext lc;

    private SyslogStartConverter converter;

    private final String HOSTNAME = SyslogStartConverterTest.findHostname();

    private final Calendar calendar = Calendar.getInstance(Locale.US);

    @Test
    public void datesLessThanTen() {
        // RFC 3164, section 4.1.2:
        // If the day of the month is less than 10, then it MUST be represented as
        // a space and then the number. For example, the 7th day of August would be
        // represented as "Aug  7", with two spaces between the "g" and the "7".
        LoggingEvent le = createLoggingEvent();
        calendar.set(2012, Calendar.AUGUST, 7, 13, 15, 0);
        le.setTimeStamp(calendar.getTimeInMillis());
        Assert.assertEquals((("<191>Aug  7 13:15:00 " + (HOSTNAME)) + " "), converter.convert(le));
    }

    @Test
    public void datesGreaterThanTen() {
        LoggingEvent le = createLoggingEvent();
        calendar.set(2012, Calendar.OCTOBER, 11, 22, 14, 15);
        le.setTimeStamp(calendar.getTimeInMillis());
        Assert.assertEquals((("<191>Oct 11 22:14:15 " + (HOSTNAME)) + " "), converter.convert(le));
    }

    @Test
    public void multipleConversions() {
        LoggingEvent le = createLoggingEvent();
        calendar.set(2012, Calendar.OCTOBER, 11, 22, 14, 15);
        le.setTimeStamp(calendar.getTimeInMillis());
        Assert.assertEquals((("<191>Oct 11 22:14:15 " + (HOSTNAME)) + " "), converter.convert(le));
        Assert.assertEquals((("<191>Oct 11 22:14:15 " + (HOSTNAME)) + " "), converter.convert(le));
        calendar.set(2012, Calendar.OCTOBER, 11, 22, 14, 16);
        le.setTimeStamp(calendar.getTimeInMillis());
        Assert.assertEquals((("<191>Oct 11 22:14:16 " + (HOSTNAME)) + " "), converter.convert(le));
    }

    @Test
    public void ignoreDefaultLocale() {
        Locale originalDefaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.TRADITIONAL_CHINESE);
        try {
            converter.start();
            LoggingEvent le = createLoggingEvent();
            calendar.set(2012, Calendar.OCTOBER, 11, 22, 14, 15);
            le.setTimeStamp(calendar.getTimeInMillis());
            String result = converter.convert(le);
            Assert.assertEquals((("<191>Oct 11 22:14:15 " + (HOSTNAME)) + " "), result);
        } finally {
            Locale.setDefault(originalDefaultLocale);
        }
    }
}

