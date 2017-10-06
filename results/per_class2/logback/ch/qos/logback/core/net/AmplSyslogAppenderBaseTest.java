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
        int int_0 = ch.qos.logback.core.net.SyslogConstants.LOG_KERN;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__2 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint__2)));
        int int_1 = ch.qos.logback.core.net.SyslogConstants.LOG_USER;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__4 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint__4)));
        int int_2 = ch.qos.logback.core.net.SyslogConstants.LOG_MAIL;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__6 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint__6)));
        int int_3 = ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__8 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint__8)));
        int int_4 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTH;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__10 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint__10)));
        int int_5 = ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__12 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint__12)));
        int int_6 = ch.qos.logback.core.net.SyslogConstants.LOG_LPR;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__14 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint__14)));
        int int_7 = ch.qos.logback.core.net.SyslogConstants.LOG_NEWS;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__16 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint__16)));
        int int_8 = ch.qos.logback.core.net.SyslogConstants.LOG_UUCP;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__18 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint__18)));
        int int_9 = ch.qos.logback.core.net.SyslogConstants.LOG_CRON;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__20 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint__20)));
        int int_10 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__22 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint__22)));
        int int_11 = ch.qos.logback.core.net.SyslogConstants.LOG_FTP;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__24 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint__24)));
        int int_12 = ch.qos.logback.core.net.SyslogConstants.LOG_NTP;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__26 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint__26)));
        int int_13 = ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__28 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint__28)));
        int int_14 = ch.qos.logback.core.net.SyslogConstants.LOG_ALERT;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__30 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint__30)));
        int int_15 = ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__32 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint__32)));
        int int_16 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__34 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(128, ((int) (o_testFacilityStringToint__34)));
        int int_17 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__36 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint__36)));
        int int_18 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__38 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint__38)));
        int int_19 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__40 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint__40)));
        int int_20 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__42 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint__42)));
        int int_21 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__44 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint__44)));
        int int_22 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__46 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(176, ((int) (o_testFacilityStringToint__46)));
        int int_23 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint__48 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(184, ((int) (o_testFacilityStringToint__48)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint__38)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint__40)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint__18)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint__22)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint__28)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint__30)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint__32)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint__24)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(128, ((int) (o_testFacilityStringToint__34)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint__44)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint__42)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint__2)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint__36)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(176, ((int) (o_testFacilityStringToint__46)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint__16)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint__26)));
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString15_failAssert11() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int int_360 = ch.qos.logback.core.net.SyslogConstants.LOG_KERN;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            int int_361 = ch.qos.logback.core.net.SyslogConstants.LOG_USER;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            int int_362 = ch.qos.logback.core.net.SyslogConstants.LOG_MAIL;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("M_IL");
            int int_363 = ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            int int_364 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTH;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            int int_365 = ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            int int_366 = ch.qos.logback.core.net.SyslogConstants.LOG_LPR;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            int int_367 = ch.qos.logback.core.net.SyslogConstants.LOG_NEWS;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            int int_368 = ch.qos.logback.core.net.SyslogConstants.LOG_UUCP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            int int_369 = ch.qos.logback.core.net.SyslogConstants.LOG_CRON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            int int_370 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            int int_371 = ch.qos.logback.core.net.SyslogConstants.LOG_FTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            int int_372 = ch.qos.logback.core.net.SyslogConstants.LOG_NTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            int int_373 = ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            int int_374 = ch.qos.logback.core.net.SyslogConstants.LOG_ALERT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            int int_375 = ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            int int_376 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            int int_377 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            int int_378 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            int int_379 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            int int_380 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            int int_381 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            int int_382 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            int int_383 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString15 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString9_failAssert6() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int int_216 = ch.qos.logback.core.net.SyslogConstants.LOG_KERN;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            int int_217 = ch.qos.logback.core.net.SyslogConstants.LOG_USER;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UbER");
            int int_218 = ch.qos.logback.core.net.SyslogConstants.LOG_MAIL;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            int int_219 = ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            int int_220 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTH;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            int int_221 = ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            int int_222 = ch.qos.logback.core.net.SyslogConstants.LOG_LPR;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            int int_223 = ch.qos.logback.core.net.SyslogConstants.LOG_NEWS;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            int int_224 = ch.qos.logback.core.net.SyslogConstants.LOG_UUCP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            int int_225 = ch.qos.logback.core.net.SyslogConstants.LOG_CRON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            int int_226 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            int int_227 = ch.qos.logback.core.net.SyslogConstants.LOG_FTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            int int_228 = ch.qos.logback.core.net.SyslogConstants.LOG_NTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            int int_229 = ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            int int_230 = ch.qos.logback.core.net.SyslogConstants.LOG_ALERT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            int int_231 = ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            int int_232 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            int int_233 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            int int_234 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            int int_235 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            int int_236 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            int int_237 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            int int_238 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            int int_239 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString9 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString51_failAssert41() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int int_1224 = ch.qos.logback.core.net.SyslogConstants.LOG_KERN;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            int int_1225 = ch.qos.logback.core.net.SyslogConstants.LOG_USER;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            int int_1226 = ch.qos.logback.core.net.SyslogConstants.LOG_MAIL;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            int int_1227 = ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            int int_1228 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTH;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            int int_1229 = ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            int int_1230 = ch.qos.logback.core.net.SyslogConstants.LOG_LPR;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            int int_1231 = ch.qos.logback.core.net.SyslogConstants.LOG_NEWS;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            int int_1232 = ch.qos.logback.core.net.SyslogConstants.LOG_UUCP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUP");
            int int_1233 = ch.qos.logback.core.net.SyslogConstants.LOG_CRON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            int int_1234 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            int int_1235 = ch.qos.logback.core.net.SyslogConstants.LOG_FTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            int int_1236 = ch.qos.logback.core.net.SyslogConstants.LOG_NTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            int int_1237 = ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            int int_1238 = ch.qos.logback.core.net.SyslogConstants.LOG_ALERT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            int int_1239 = ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            int int_1240 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            int int_1241 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            int int_1242 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            int int_1243 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            int int_1244 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            int int_1245 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            int int_1246 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            int int_1247 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString51 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString25_failAssert20() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int int_600 = ch.qos.logback.core.net.SyslogConstants.LOG_KERN;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            int int_601 = ch.qos.logback.core.net.SyslogConstants.LOG_USER;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            int int_602 = ch.qos.logback.core.net.SyslogConstants.LOG_MAIL;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            int int_603 = ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            int int_604 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTH;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("");
            int int_605 = ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            int int_606 = ch.qos.logback.core.net.SyslogConstants.LOG_LPR;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            int int_607 = ch.qos.logback.core.net.SyslogConstants.LOG_NEWS;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            int int_608 = ch.qos.logback.core.net.SyslogConstants.LOG_UUCP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            int int_609 = ch.qos.logback.core.net.SyslogConstants.LOG_CRON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            int int_610 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            int int_611 = ch.qos.logback.core.net.SyslogConstants.LOG_FTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            int int_612 = ch.qos.logback.core.net.SyslogConstants.LOG_NTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            int int_613 = ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            int int_614 = ch.qos.logback.core.net.SyslogConstants.LOG_ALERT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            int int_615 = ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            int int_616 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            int int_617 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            int int_618 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            int int_619 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            int int_620 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            int int_621 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            int int_622 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            int int_623 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString25 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString134() throws java.lang.InterruptedException {
        int int_3216 = ch.qos.logback.core.net.SyslogConstants.LOG_KERN;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__2 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint_literalMutationString134__2)));
        int int_3217 = ch.qos.logback.core.net.SyslogConstants.LOG_USER;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__4 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint_literalMutationString134__4)));
        int int_3218 = ch.qos.logback.core.net.SyslogConstants.LOG_MAIL;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__6 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint_literalMutationString134__6)));
        int int_3219 = ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__8 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint_literalMutationString134__8)));
        int int_3220 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTH;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__10 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint_literalMutationString134__10)));
        int int_3221 = ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__12 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint_literalMutationString134__12)));
        int int_3222 = ch.qos.logback.core.net.SyslogConstants.LOG_LPR;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__14 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint_literalMutationString134__14)));
        int int_3223 = ch.qos.logback.core.net.SyslogConstants.LOG_NEWS;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__16 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint_literalMutationString134__16)));
        int int_3224 = ch.qos.logback.core.net.SyslogConstants.LOG_UUCP;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__18 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint_literalMutationString134__18)));
        int int_3225 = ch.qos.logback.core.net.SyslogConstants.LOG_CRON;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__20 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint_literalMutationString134__20)));
        int int_3226 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__22 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint_literalMutationString134__22)));
        int int_3227 = ch.qos.logback.core.net.SyslogConstants.LOG_FTP;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__24 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint_literalMutationString134__24)));
        int int_3228 = ch.qos.logback.core.net.SyslogConstants.LOG_NTP;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__26 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint_literalMutationString134__26)));
        int int_3229 = ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__28 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString134__28)));
        int int_3230 = ch.qos.logback.core.net.SyslogConstants.LOG_ALERT;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__30 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint_literalMutationString134__30)));
        int int_3231 = ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__32 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint_literalMutationString134__32)));
        int int_3232 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__34 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(128, ((int) (o_testFacilityStringToint_literalMutationString134__34)));
        int int_3233 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__36 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint_literalMutationString134__36)));
        int int_3234 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__38 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint_literalMutationString134__38)));
        int int_3235 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__40 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
        int int_3236 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__42 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint_literalMutationString134__42)));
        int int_3237 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__44 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint_literalMutationString134__44)));
        int int_3238 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__46 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString134__46)));
        int int_3239 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString134__48 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(184, ((int) (o_testFacilityStringToint_literalMutationString134__48)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint_literalMutationString134__18)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint_literalMutationString134__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(128, ((int) (o_testFacilityStringToint_literalMutationString134__34)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint_literalMutationString134__22)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint_literalMutationString134__26)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint_literalMutationString134__30)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint_literalMutationString134__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint_literalMutationString134__38)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint_literalMutationString134__36)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint_literalMutationString134__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint_literalMutationString134__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint_literalMutationString134__32)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint_literalMutationString134__2)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint_literalMutationString134__16)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint_literalMutationString134__40)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString134__28)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint_literalMutationString134__24)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint_literalMutationString134__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint_literalMutationString134__44)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint_literalMutationString134__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint_literalMutationString134__42)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint_literalMutationString134__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString134__46)));
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString61_failAssert50() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int int_1464 = ch.qos.logback.core.net.SyslogConstants.LOG_KERN;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            int int_1465 = ch.qos.logback.core.net.SyslogConstants.LOG_USER;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            int int_1466 = ch.qos.logback.core.net.SyslogConstants.LOG_MAIL;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            int int_1467 = ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            int int_1468 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTH;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            int int_1469 = ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            int int_1470 = ch.qos.logback.core.net.SyslogConstants.LOG_LPR;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            int int_1471 = ch.qos.logback.core.net.SyslogConstants.LOG_NEWS;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            int int_1472 = ch.qos.logback.core.net.SyslogConstants.LOG_UUCP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            int int_1473 = ch.qos.logback.core.net.SyslogConstants.LOG_CRON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            int int_1474 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("");
            int int_1475 = ch.qos.logback.core.net.SyslogConstants.LOG_FTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            int int_1476 = ch.qos.logback.core.net.SyslogConstants.LOG_NTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            int int_1477 = ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            int int_1478 = ch.qos.logback.core.net.SyslogConstants.LOG_ALERT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            int int_1479 = ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            int int_1480 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            int int_1481 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            int int_1482 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            int int_1483 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            int int_1484 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            int int_1485 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            int int_1486 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            int int_1487 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString61 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString140() throws java.lang.InterruptedException {
        int int_3360 = ch.qos.logback.core.net.SyslogConstants.LOG_KERN;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__2 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint_literalMutationString140__2)));
        int int_3361 = ch.qos.logback.core.net.SyslogConstants.LOG_USER;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__4 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint_literalMutationString140__4)));
        int int_3362 = ch.qos.logback.core.net.SyslogConstants.LOG_MAIL;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__6 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint_literalMutationString140__6)));
        int int_3363 = ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__8 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint_literalMutationString140__8)));
        int int_3364 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTH;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__10 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint_literalMutationString140__10)));
        int int_3365 = ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__12 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint_literalMutationString140__12)));
        int int_3366 = ch.qos.logback.core.net.SyslogConstants.LOG_LPR;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__14 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint_literalMutationString140__14)));
        int int_3367 = ch.qos.logback.core.net.SyslogConstants.LOG_NEWS;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__16 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint_literalMutationString140__16)));
        int int_3368 = ch.qos.logback.core.net.SyslogConstants.LOG_UUCP;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__18 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint_literalMutationString140__18)));
        int int_3369 = ch.qos.logback.core.net.SyslogConstants.LOG_CRON;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__20 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint_literalMutationString140__20)));
        int int_3370 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__22 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint_literalMutationString140__22)));
        int int_3371 = ch.qos.logback.core.net.SyslogConstants.LOG_FTP;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__24 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint_literalMutationString140__24)));
        int int_3372 = ch.qos.logback.core.net.SyslogConstants.LOG_NTP;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__26 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint_literalMutationString140__26)));
        int int_3373 = ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__28 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
        int int_3374 = ch.qos.logback.core.net.SyslogConstants.LOG_ALERT;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__30 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint_literalMutationString140__30)));
        int int_3375 = ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__32 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint_literalMutationString140__32)));
        int int_3376 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__34 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(128, ((int) (o_testFacilityStringToint_literalMutationString140__34)));
        int int_3377 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__36 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint_literalMutationString140__36)));
        int int_3378 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__38 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint_literalMutationString140__38)));
        int int_3379 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__40 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint_literalMutationString140__40)));
        int int_3380 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__42 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint_literalMutationString140__42)));
        int int_3381 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__44 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
        int int_3382 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__46 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(176, ((int) (o_testFacilityStringToint_literalMutationString140__46)));
        int int_3383 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7;
        // AssertGenerator create local variable with return value of invocation
        int o_testFacilityStringToint_literalMutationString140__48 = ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString140__48)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(16, ((int) (o_testFacilityStringToint_literalMutationString140__6)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(40, ((int) (o_testFacilityStringToint_literalMutationString140__12)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(128, ((int) (o_testFacilityStringToint_literalMutationString140__34)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(160, ((int) (o_testFacilityStringToint_literalMutationString140__42)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(144, ((int) (o_testFacilityStringToint_literalMutationString140__38)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(176, ((int) (o_testFacilityStringToint_literalMutationString140__46)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(88, ((int) (o_testFacilityStringToint_literalMutationString140__24)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(0, ((int) (o_testFacilityStringToint_literalMutationString140__2)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(152, ((int) (o_testFacilityStringToint_literalMutationString140__40)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(96, ((int) (o_testFacilityStringToint_literalMutationString140__26)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(72, ((int) (o_testFacilityStringToint_literalMutationString140__20)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(80, ((int) (o_testFacilityStringToint_literalMutationString140__22)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(168, ((int) (o_testFacilityStringToint_literalMutationString140__44)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(136, ((int) (o_testFacilityStringToint_literalMutationString140__36)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(104, ((int) (o_testFacilityStringToint_literalMutationString140__28)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(32, ((int) (o_testFacilityStringToint_literalMutationString140__10)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(8, ((int) (o_testFacilityStringToint_literalMutationString140__4)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(24, ((int) (o_testFacilityStringToint_literalMutationString140__8)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(112, ((int) (o_testFacilityStringToint_literalMutationString140__30)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(120, ((int) (o_testFacilityStringToint_literalMutationString140__32)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(48, ((int) (o_testFacilityStringToint_literalMutationString140__14)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(64, ((int) (o_testFacilityStringToint_literalMutationString140__18)));
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(56, ((int) (o_testFacilityStringToint_literalMutationString140__16)));
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString1_failAssert0() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int int_24 = ch.qos.logback.core.net.SyslogConstants.LOG_KERN;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("");
            int int_25 = ch.qos.logback.core.net.SyslogConstants.LOG_USER;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            int int_26 = ch.qos.logback.core.net.SyslogConstants.LOG_MAIL;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            int int_27 = ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            int int_28 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTH;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            int int_29 = ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            int int_30 = ch.qos.logback.core.net.SyslogConstants.LOG_LPR;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LPR");
            int int_31 = ch.qos.logback.core.net.SyslogConstants.LOG_NEWS;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            int int_32 = ch.qos.logback.core.net.SyslogConstants.LOG_UUCP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            int int_33 = ch.qos.logback.core.net.SyslogConstants.LOG_CRON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            int int_34 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            int int_35 = ch.qos.logback.core.net.SyslogConstants.LOG_FTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            int int_36 = ch.qos.logback.core.net.SyslogConstants.LOG_NTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            int int_37 = ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            int int_38 = ch.qos.logback.core.net.SyslogConstants.LOG_ALERT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            int int_39 = ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            int int_40 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            int int_41 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            int int_42 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            int int_43 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            int int_44 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            int int_45 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            int int_46 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            int int_47 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString1 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.net.SyslogAppenderBaseTest#testFacilityStringToint */
    @org.junit.Test(timeout = 10000)
    public void testFacilityStringToint_literalMutationString40_failAssert32() throws java.lang.InterruptedException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int int_960 = ch.qos.logback.core.net.SyslogConstants.LOG_KERN;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("KERN");
            int int_961 = ch.qos.logback.core.net.SyslogConstants.LOG_USER;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("USER");
            int int_962 = ch.qos.logback.core.net.SyslogConstants.LOG_MAIL;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("MAIL");
            int int_963 = ch.qos.logback.core.net.SyslogConstants.LOG_DAEMON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("DAEMON");
            int int_964 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTH;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTH");
            int int_965 = ch.qos.logback.core.net.SyslogConstants.LOG_SYSLOG;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("SYSLOG");
            int int_966 = ch.qos.logback.core.net.SyslogConstants.LOG_LPR;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LlPR");
            int int_967 = ch.qos.logback.core.net.SyslogConstants.LOG_NEWS;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NEWS");
            int int_968 = ch.qos.logback.core.net.SyslogConstants.LOG_UUCP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("UUCP");
            int int_969 = ch.qos.logback.core.net.SyslogConstants.LOG_CRON;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CRON");
            int int_970 = ch.qos.logback.core.net.SyslogConstants.LOG_AUTHPRIV;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUTHPRIV");
            int int_971 = ch.qos.logback.core.net.SyslogConstants.LOG_FTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("FTP");
            int int_972 = ch.qos.logback.core.net.SyslogConstants.LOG_NTP;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("NTP");
            int int_973 = ch.qos.logback.core.net.SyslogConstants.LOG_AUDIT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("AUDIT");
            int int_974 = ch.qos.logback.core.net.SyslogConstants.LOG_ALERT;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("ALERT");
            int int_975 = ch.qos.logback.core.net.SyslogConstants.LOG_CLOCK;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("CLOCK");
            int int_976 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL0;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL0");
            int int_977 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL1;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL1");
            int int_978 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL2;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL2");
            int int_979 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL3;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL3");
            int int_980 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL4;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL4");
            int int_981 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL5;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL5");
            int int_982 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL6;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL6");
            int int_983 = ch.qos.logback.core.net.SyslogConstants.LOG_LOCAL7;
            ch.qos.logback.core.net.SyslogAppenderBase.facilityStringToint("LOCAL7");
            org.junit.Assert.fail("testFacilityStringToint_literalMutationString40 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

