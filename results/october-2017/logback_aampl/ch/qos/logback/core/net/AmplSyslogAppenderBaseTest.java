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
package ch.qos.logback.core.net;


public class AmplSyslogAppenderBaseTest {
    @org.junit.Test
    public void testFacilityStringToint() throws java.lang.InterruptedException {
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_KERN, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_USER, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_MAIL, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_AUTH, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_LPR, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_NEWS, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_UUCP, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_CRON, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_FTP, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_NTP, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_ALERT, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6"));
        org.junit.Assert.assertEquals(ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7, ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7"));
    }
}

