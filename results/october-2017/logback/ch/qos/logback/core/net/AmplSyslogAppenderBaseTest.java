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
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint() throws java.lang.InterruptedException {
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__1 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint__1)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__2 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint__2)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__3 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint__3)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__4 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint__4)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__5 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint__5)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__6 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint__6)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__7 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint__7)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__8 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint__8)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__9 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint__9)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__10 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint__10)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__11 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint__11)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__12 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint__12)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__13 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint__13)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__14 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint__14)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__15 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint__15)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__16 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint__16)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__17 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__18 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint__18)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__19 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint__19)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__20 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint__20)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__21 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__22 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint__22)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__23 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(176, ((int) (o_testFacilityStringToint__23)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__24 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(184, ((int) (o_testFacilityStringToint__24)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(128, ((int) (o_testFacilityStringToint__17)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint__13)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(176, ((int) (o_testFacilityStringToint__23)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint__18)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint__22)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint__15)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint__2)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint__21)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint__16)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint__1)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint__19)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint__11)));
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString49_failAssert40() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString49 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString25_failAssert20() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString25 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString134() throws java.lang.InterruptedException {
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__1 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint_literalMutationString134__1)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__2 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint_literalMutationString134__2)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__3 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint_literalMutationString134__3)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__4 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint_literalMutationString134__4)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__5 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint_literalMutationString134__5)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__6 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint_literalMutationString134__6)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__7 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint_literalMutationString134__7)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__8 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint_literalMutationString134__8)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__9 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint_literalMutationString134__9)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__10 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint_literalMutationString134__10)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__11 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint_literalMutationString134__11)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__12 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint_literalMutationString134__12)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__13 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint_literalMutationString134__13)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__14 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString134__14)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__15 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint_literalMutationString134__15)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__16 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint_literalMutationString134__16)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__17 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__18 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint_literalMutationString134__18)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__19 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint_literalMutationString134__19)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__20 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint_literalMutationString134__20)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__21 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint_literalMutationString134__21)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__22 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint_literalMutationString134__22)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__23 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString134__23)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__24 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(184, ((int) (o_testFacilityStringToint_literalMutationString134__24)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint_literalMutationString134__18)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint_literalMutationString134__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint_literalMutationString134__13)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint_literalMutationString134__21)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint_literalMutationString134__22)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint_literalMutationString134__19)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint_literalMutationString134__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint_literalMutationString134__1)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint_literalMutationString134__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint_literalMutationString134__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint_literalMutationString134__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint_literalMutationString134__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint_literalMutationString134__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint_literalMutationString134__2)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint_literalMutationString134__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint_literalMutationString134__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint_literalMutationString134__16)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint_literalMutationString134__15)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString134__23)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint_literalMutationString134__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(128, ((int) (o_testFacilityStringToint_literalMutationString134__17)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString134__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint_literalMutationString134__10)));
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString12_failAssert9() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("!x*z");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString12 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString13_failAssert10() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString13 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString61_failAssert50() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString61 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString140() throws java.lang.InterruptedException {
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__1 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint_literalMutationString140__1)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__2 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint_literalMutationString140__2)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__3 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint_literalMutationString140__3)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__4 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint_literalMutationString140__4)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__5 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint_literalMutationString140__5)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__6 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint_literalMutationString140__6)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__7 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint_literalMutationString140__7)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__8 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint_literalMutationString140__8)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__9 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint_literalMutationString140__9)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__10 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint_literalMutationString140__10)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__11 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint_literalMutationString140__11)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__12 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint_literalMutationString140__12)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__13 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint_literalMutationString140__13)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__14 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString140__14)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__15 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint_literalMutationString140__15)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__16 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint_literalMutationString140__16)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__17 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(128, ((int) (o_testFacilityStringToint_literalMutationString140__17)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__18 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint_literalMutationString140__18)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__19 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint_literalMutationString140__19)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__20 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint_literalMutationString140__20)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__21 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint_literalMutationString140__21)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__22 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint_literalMutationString140__22)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__23 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(176, ((int) (o_testFacilityStringToint_literalMutationString140__23)));
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__24 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString140__24)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint_literalMutationString140__1)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint_literalMutationString140__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint_literalMutationString140__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint_literalMutationString140__7)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint_literalMutationString140__2)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint_literalMutationString140__5)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint_literalMutationString140__21)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint_literalMutationString140__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(176, ((int) (o_testFacilityStringToint_literalMutationString140__23)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint_literalMutationString140__22)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint_literalMutationString140__3)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint_literalMutationString140__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint_literalMutationString140__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint_literalMutationString140__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint_literalMutationString140__9)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint_literalMutationString140__11)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString140__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint_literalMutationString140__19)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint_literalMutationString140__13)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint_literalMutationString140__15)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint_literalMutationString140__18)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint_literalMutationString140__16)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(128, ((int) (o_testFacilityStringToint_literalMutationString140__17)));
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString1_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString1 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

