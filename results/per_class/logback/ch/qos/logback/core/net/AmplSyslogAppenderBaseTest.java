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

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test
    public void testFacilityStringToint_literalMutation2_failAssert1() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // MethodAssertGenerator build local variable
            Object o_1_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("K}ERN");
            // MethodAssertGenerator build local variable
            Object o_3_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            // MethodAssertGenerator build local variable
            Object o_5_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            // MethodAssertGenerator build local variable
            Object o_7_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            // MethodAssertGenerator build local variable
            Object o_9_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            // MethodAssertGenerator build local variable
            Object o_11_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            // MethodAssertGenerator build local variable
            Object o_13_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            // MethodAssertGenerator build local variable
            Object o_15_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            // MethodAssertGenerator build local variable
            Object o_17_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            // MethodAssertGenerator build local variable
            Object o_19_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            // MethodAssertGenerator build local variable
            Object o_21_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            // MethodAssertGenerator build local variable
            Object o_23_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            // MethodAssertGenerator build local variable
            Object o_25_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            // MethodAssertGenerator build local variable
            Object o_27_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            // MethodAssertGenerator build local variable
            Object o_29_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            // MethodAssertGenerator build local variable
            Object o_31_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            // MethodAssertGenerator build local variable
            Object o_33_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            // MethodAssertGenerator build local variable
            Object o_35_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            // MethodAssertGenerator build local variable
            Object o_37_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            // MethodAssertGenerator build local variable
            Object o_39_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            // MethodAssertGenerator build local variable
            Object o_41_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            // MethodAssertGenerator build local variable
            Object o_43_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            // MethodAssertGenerator build local variable
            Object o_45_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            // MethodAssertGenerator build local variable
            Object o_47_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutation2 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_cf171_failAssert137_literalMutation14132_failAssert17() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_1_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("d;A,");
                // MethodAssertGenerator build local variable
                Object o_3_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
                // MethodAssertGenerator build local variable
                Object o_5_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
                // MethodAssertGenerator build local variable
                Object o_7_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
                // MethodAssertGenerator build local variable
                Object o_9_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
                // MethodAssertGenerator build local variable
                Object o_11_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
                // MethodAssertGenerator build local variable
                Object o_13_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
                // MethodAssertGenerator build local variable
                Object o_15_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
                // MethodAssertGenerator build local variable
                Object o_17_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
                // MethodAssertGenerator build local variable
                Object o_19_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
                // MethodAssertGenerator build local variable
                Object o_21_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
                // MethodAssertGenerator build local variable
                Object o_23_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
                // MethodAssertGenerator build local variable
                Object o_25_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
                // MethodAssertGenerator build local variable
                Object o_27_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
                // MethodAssertGenerator build local variable
                Object o_29_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
                // MethodAssertGenerator build local variable
                Object o_31_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
                // MethodAssertGenerator build local variable
                Object o_33_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
                // MethodAssertGenerator build local variable
                Object o_35_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
                // MethodAssertGenerator build local variable
                Object o_37_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
                // MethodAssertGenerator build local variable
                Object o_39_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
                // MethodAssertGenerator build local variable
                Object o_41_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
                // MethodAssertGenerator build local variable
                Object o_43_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
                // MethodAssertGenerator build local variable
                Object o_45_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
                // StatementAdderOnAssert create null value
                java.lang.String vc_48 = (java.lang.String)null;
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.net.SyslogAppenderBase vc_46 = (ch.qos.logback.core.net.SyslogAppenderBase)null;
                // StatementAdderMethod cloned existing statement
                vc_46.setSuffixPattern(vc_48);
                // MethodAssertGenerator build local variable
                Object o_53_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
                org.junit.Assert.fail("testFacilityStringToint_cf171 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testFacilityStringToint_cf171_failAssert137_literalMutation14132 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_cf139_failAssert128_literalMutation13059_failAssert31_add45969() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                // MethodAssertGenerator build local variable
                Object o_1_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_1_0, 0);
                // MethodAssertGenerator build local variable
                Object o_3_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_3_0, 8);
                // MethodAssertGenerator build local variable
                Object o_5_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_5_0, 16);
                // MethodAssertGenerator build local variable
                Object o_7_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_7_0, 24);
                // MethodAssertGenerator build local variable
                Object o_9_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_9_0, 32);
                // MethodAssertGenerator build local variable
                Object o_11_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
                // AssertGenerator add assertion
                org.junit.Assert.assertEquals(o_11_0, 40);
                // MethodAssertGenerator build local variable
                Object o_13_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LR");
                // MethodAssertGenerator build local variable
                Object o_15_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
                // MethodAssertGenerator build local variable
                Object o_17_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
                // MethodAssertGenerator build local variable
                Object o_19_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
                // MethodAssertGenerator build local variable
                Object o_21_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
                // MethodAssertGenerator build local variable
                Object o_23_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
                // MethodAssertGenerator build local variable
                Object o_25_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
                // MethodAssertGenerator build local variable
                Object o_27_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
                // MethodAssertGenerator build local variable
                Object o_29_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
                // MethodAssertGenerator build local variable
                Object o_31_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
                // MethodAssertGenerator build local variable
                Object o_33_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
                // MethodAssertGenerator build local variable
                Object o_35_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
                // MethodAssertGenerator build local variable
                Object o_37_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
                // MethodAssertGenerator build local variable
                Object o_39_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
                // MethodAssertGenerator build local variable
                Object o_41_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
                // MethodAssertGenerator build local variable
                Object o_43_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
                // MethodAssertGenerator build local variable
                Object o_45_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
                // StatementAdderOnAssert create null value
                ch.qos.logback.core.net.SyslogAppenderBase vc_16 = (ch.qos.logback.core.net.SyslogAppenderBase)null;
                // StatementAdderMethod cloned existing statement
                // MethodCallAdder
                vc_16.getCharset();
                // StatementAdderMethod cloned existing statement
                vc_16.getCharset();
                // MethodAssertGenerator build local variable
                Object o_51_0 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
                org.junit.Assert.fail("testFacilityStringToint_cf139 should have thrown NullPointerException");
            } catch (java.lang.NullPointerException eee) {
            }
            org.junit.Assert.fail("testFacilityStringToint_cf139_failAssert128_literalMutation13059 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

