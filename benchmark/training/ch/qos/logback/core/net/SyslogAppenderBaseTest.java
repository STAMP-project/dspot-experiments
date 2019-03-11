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


import SyslogConstants.LOG_ALERT;
import SyslogConstants.LOG_AUDIT;
import SyslogConstants.LOG_AUTH;
import SyslogConstants.LOG_AUTHPRIV;
import SyslogConstants.LOG_CLOCK;
import SyslogConstants.LOG_CRON;
import SyslogConstants.LOG_DAEMON;
import SyslogConstants.LOG_FTP;
import SyslogConstants.LOG_KERN;
import SyslogConstants.LOG_LOCAL0;
import SyslogConstants.LOG_LOCAL1;
import SyslogConstants.LOG_LOCAL2;
import SyslogConstants.LOG_LOCAL3;
import SyslogConstants.LOG_LOCAL4;
import SyslogConstants.LOG_LOCAL5;
import SyslogConstants.LOG_LOCAL6;
import SyslogConstants.LOG_LOCAL7;
import SyslogConstants.LOG_LPR;
import SyslogConstants.LOG_MAIL;
import SyslogConstants.LOG_NEWS;
import SyslogConstants.LOG_NTP;
import SyslogConstants.LOG_SYSLOG;
import SyslogConstants.LOG_USER;
import SyslogConstants.LOG_UUCP;
import org.junit.Assert;
import org.junit.Test;


public class SyslogAppenderBaseTest {
    @Test
    public void testFacilityStringToint() throws InterruptedException {
        Assert.assertEquals(LOG_KERN, SyslogAppenderBase.facilityStringToint("KERN"));
        Assert.assertEquals(LOG_USER, SyslogAppenderBase.facilityStringToint("USER"));
        Assert.assertEquals(LOG_MAIL, SyslogAppenderBase.facilityStringToint("MAIL"));
        Assert.assertEquals(LOG_DAEMON, SyslogAppenderBase.facilityStringToint("DAEMON"));
        Assert.assertEquals(LOG_AUTH, SyslogAppenderBase.facilityStringToint("AUTH"));
        Assert.assertEquals(LOG_SYSLOG, SyslogAppenderBase.facilityStringToint("SYSLOG"));
        Assert.assertEquals(LOG_LPR, SyslogAppenderBase.facilityStringToint("LPR"));
        Assert.assertEquals(LOG_NEWS, SyslogAppenderBase.facilityStringToint("NEWS"));
        Assert.assertEquals(LOG_UUCP, SyslogAppenderBase.facilityStringToint("UUCP"));
        Assert.assertEquals(LOG_CRON, SyslogAppenderBase.facilityStringToint("CRON"));
        Assert.assertEquals(LOG_AUTHPRIV, SyslogAppenderBase.facilityStringToint("AUTHPRIV"));
        Assert.assertEquals(LOG_FTP, SyslogAppenderBase.facilityStringToint("FTP"));
        Assert.assertEquals(LOG_NTP, SyslogAppenderBase.facilityStringToint("NTP"));
        Assert.assertEquals(LOG_AUDIT, SyslogAppenderBase.facilityStringToint("AUDIT"));
        Assert.assertEquals(LOG_ALERT, SyslogAppenderBase.facilityStringToint("ALERT"));
        Assert.assertEquals(LOG_CLOCK, SyslogAppenderBase.facilityStringToint("CLOCK"));
        Assert.assertEquals(LOG_LOCAL0, SyslogAppenderBase.facilityStringToint("LOCAL0"));
        Assert.assertEquals(LOG_LOCAL1, SyslogAppenderBase.facilityStringToint("LOCAL1"));
        Assert.assertEquals(LOG_LOCAL2, SyslogAppenderBase.facilityStringToint("LOCAL2"));
        Assert.assertEquals(LOG_LOCAL3, SyslogAppenderBase.facilityStringToint("LOCAL3"));
        Assert.assertEquals(LOG_LOCAL4, SyslogAppenderBase.facilityStringToint("LOCAL4"));
        Assert.assertEquals(LOG_LOCAL5, SyslogAppenderBase.facilityStringToint("LOCAL5"));
        Assert.assertEquals(LOG_LOCAL6, SyslogAppenderBase.facilityStringToint("LOCAL6"));
        Assert.assertEquals(LOG_LOCAL7, SyslogAppenderBase.facilityStringToint("LOCAL7"));
    }
}

