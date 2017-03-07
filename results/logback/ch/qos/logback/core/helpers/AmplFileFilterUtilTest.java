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


package ch.qos.logback.core.helpers;


public class AmplFileFilterUtilTest {
    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    // see also http://jira.qos.ch/browse/LBCORE-164
    @org.junit.Test
    public void findHighestCounterTest() throws java.text.ParseException {
        java.lang.String[] sa = new java.lang.String[]{ "c:/log/debug-old-2010-08-10.0.log" , "c:/log/debug-old-2010-08-10.1.log" , "c:/log/debug-old-2010-08-10.10.log" , "c:/log/debug-old-2010-08-10.11.log" , "c:/log/debug-old-2010-08-10.12.log" , "c:/log/debug-old-2010-08-10.2.log" , "c:/log/debug-old-2010-08-10.3.log" , "c:/log/debug-old-2010-08-10.4.log" , "c:/log/debug-old-2010-08-10.5.log" , "c:/log/debug-old-2010-08-10.6.log" , "c:/log/debug-old-2010-08-10.7.log" , "c:/log/debug-old-2010-08-10.8.log" , "c:/log/debug-old-2010-08-10.9.log" };
        java.io.File[] matchingFileArray = new java.io.File[sa.length];
        for (int i = 0; i < (sa.length); i++) {
            matchingFileArray[i] = new java.io.File(sa[i]);
        }
        ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("c:/log/debug-old-%d{yyyy-MM-dd}.%i.log", context);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.lang.String rexexp = null;
        rexexp = fnp.toRegexForFixedDate(sdf.parse("2010-08-10"));
        java.lang.String stemRegex = ch.qos.logback.core.rolling.helper.FileFilterUtil.afterLastSlash(rexexp);
        int result = ch.qos.logback.core.rolling.helper.FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
        junit.framework.Assert.assertEquals(12, result);
    }

    // see also http://jira.qos.ch/browse/LBCORE-164
    /* amplification of ch.qos.logback.core.helpers.FileFilterUtilTest#findHighestCounterTest */
    @org.junit.Test
    public void findHighestCounterTest_literalMutation70_failAssert3() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String[] sa = new java.lang.String[]{ "c:/log/debug-old-2010-08-10.0.log" , "c:/log/debug-old-2010-08-10.1.log" , "c:/log/debug-old-2010-08-10.10.log" , "c:/log/debug-old-2010-08-10.11.log" , "c:/log/debug-old-2010-08-10.12.log" , "c:/log/debug-old-2010-08-10.2.log" , "c:/log/debug-old-2010-08-10.3.log" , "c:/log/debug-old-2010-08-10.4.log" , "c:/log/debug-old-2010-08-10.5.log" , "c:/log/debug-old-2010-08-10.6.log" , "c:/log/debug-old-2010-08-10.7.log" , "c:/log/debug-old-2010-08-10.8.log" , "c:/log/debug-old-2010-08-10.9.log" };
            java.io.File[] matchingFileArray = new java.io.File[sa.length];
            for (int i = 0; i < (sa.length); i++) {
                matchingFileArray[i] = new java.io.File(sa[i]);
            }
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("!YL#ZQsb>_1JVt2Y]\\@1u)p]QM-k,I]-r8//GG", context);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.lang.String rexexp = null;
            rexexp = fnp.toRegexForFixedDate(sdf.parse("2010-08-10"));
            java.lang.String stemRegex = ch.qos.logback.core.rolling.helper.FileFilterUtil.afterLastSlash(rexexp);
            int result = ch.qos.logback.core.rolling.helper.FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
            org.junit.Assert.fail("findHighestCounterTest_literalMutation70 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LBCORE-164
    /* amplification of ch.qos.logback.core.helpers.FileFilterUtilTest#findHighestCounterTest */
    @org.junit.Test(timeout = 10000)
    public void findHighestCounterTest_cf213() throws java.text.ParseException {
        java.lang.String[] sa = new java.lang.String[]{ "c:/log/debug-old-2010-08-10.0.log" , "c:/log/debug-old-2010-08-10.1.log" , "c:/log/debug-old-2010-08-10.10.log" , "c:/log/debug-old-2010-08-10.11.log" , "c:/log/debug-old-2010-08-10.12.log" , "c:/log/debug-old-2010-08-10.2.log" , "c:/log/debug-old-2010-08-10.3.log" , "c:/log/debug-old-2010-08-10.4.log" , "c:/log/debug-old-2010-08-10.5.log" , "c:/log/debug-old-2010-08-10.6.log" , "c:/log/debug-old-2010-08-10.7.log" , "c:/log/debug-old-2010-08-10.8.log" , "c:/log/debug-old-2010-08-10.9.log" };
        java.io.File[] matchingFileArray = new java.io.File[sa.length];
        for (int i = 0; i < (sa.length); i++) {
            matchingFileArray[i] = new java.io.File(sa[i]);
        }
        ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("c:/log/debug-old-%d{yyyy-MM-dd}.%i.log", context);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.lang.String rexexp = null;
        rexexp = fnp.toRegexForFixedDate(sdf.parse("2010-08-10"));
        java.lang.String stemRegex = ch.qos.logback.core.rolling.helper.FileFilterUtil.afterLastSlash(rexexp);
        int result = ch.qos.logback.core.rolling.helper.FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
        // StatementAdderOnAssert create literal from method
        int int_vc_5 = 12;
        // AssertGenerator add assertion
        org.junit.Assert.assertEquals(int_vc_5, 12);
        // StatementAdderOnAssert create null value
        java.io.File vc_32 = (java.io.File)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_32);
        // StatementAdderOnAssert create random local variable
        ch.qos.logback.core.rolling.helper.FileFilterUtil vc_31 = new ch.qos.logback.core.rolling.helper.FileFilterUtil();
        // StatementAdderMethod cloned existing statement
        vc_31.removeEmptyParentDirectories(vc_32, int_vc_5);
        junit.framework.Assert.assertEquals(12, result);
    }

    // see also http://jira.qos.ch/browse/LBCORE-164
    /* amplification of ch.qos.logback.core.helpers.FileFilterUtilTest#findHighestCounterTest */
    @org.junit.Test
    public void findHighestCounterTest_literalMutation69_failAssert11() throws java.text.ParseException {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.String[] sa = new java.lang.String[]{ "c:/log/debug-old-2010-08-10.0.log" , "c:/log/debug-old-2010-08-10.1.log" , "c:/log/debug-old-2010-08-10.10.log" , "c:/log/debug-old-2010-08-10.11.log" , "c:/log/debug-old-2010-08-10.12.log" , "c:/log/debug-old-2010-08-10.2.log" , "c:/log/debug-old-2010-08-10.3.log" , "c:/log/debug-old-2010-08-10.4.log" , "c:/log/debug-old-2010-08-10.5.log" , "c:/log/debug-old-2010-08-10.6.log" , "c:/log/debug-old-2010-08-10.7.log" , "c:/log/debug-old-2010-08-10.8.log" , "c:/log/debug-old-2010-08-10.9.log" };
            java.io.File[] matchingFileArray = new java.io.File[sa.length];
            for (int i = 0; i < (sa.length); i++) {
                matchingFileArray[i] = new java.io.File(sa[i]);
            }
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("", context);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.lang.String rexexp = null;
            rexexp = fnp.toRegexForFixedDate(sdf.parse("2010-08-10"));
            java.lang.String stemRegex = ch.qos.logback.core.rolling.helper.FileFilterUtil.afterLastSlash(rexexp);
            int result = ch.qos.logback.core.rolling.helper.FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
            org.junit.Assert.fail("findHighestCounterTest_literalMutation69 should have thrown IllegalStateException");
        } catch (java.lang.IllegalStateException eee) {
        }
    }

    // see also http://jira.qos.ch/browse/LBCORE-164
    /* amplification of ch.qos.logback.core.helpers.FileFilterUtilTest#findHighestCounterTest */
    @org.junit.Test(timeout = 10000)
    public void findHighestCounterTest_cf149() throws java.text.ParseException {
        java.lang.String[] sa = new java.lang.String[]{ "c:/log/debug-old-2010-08-10.0.log" , "c:/log/debug-old-2010-08-10.1.log" , "c:/log/debug-old-2010-08-10.10.log" , "c:/log/debug-old-2010-08-10.11.log" , "c:/log/debug-old-2010-08-10.12.log" , "c:/log/debug-old-2010-08-10.2.log" , "c:/log/debug-old-2010-08-10.3.log" , "c:/log/debug-old-2010-08-10.4.log" , "c:/log/debug-old-2010-08-10.5.log" , "c:/log/debug-old-2010-08-10.6.log" , "c:/log/debug-old-2010-08-10.7.log" , "c:/log/debug-old-2010-08-10.8.log" , "c:/log/debug-old-2010-08-10.9.log" };
        java.io.File[] matchingFileArray = new java.io.File[sa.length];
        for (int i = 0; i < (sa.length); i++) {
            matchingFileArray[i] = new java.io.File(sa[i]);
        }
        ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("c:/log/debug-old-%d{yyyy-MM-dd}.%i.log", context);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.lang.String rexexp = null;
        rexexp = fnp.toRegexForFixedDate(sdf.parse("2010-08-10"));
        java.lang.String stemRegex = ch.qos.logback.core.rolling.helper.FileFilterUtil.afterLastSlash(rexexp);
        int result = ch.qos.logback.core.rolling.helper.FileFilterUtil.findHighestCounter(matchingFileArray, stemRegex);
        // StatementAdderOnAssert create null value
        java.lang.String vc_20 = (java.lang.String)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_20);
        // StatementAdderOnAssert create null value
        java.io.File vc_18 = (java.io.File)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_18);
        // StatementAdderOnAssert create null value
        ch.qos.logback.core.rolling.helper.FileFilterUtil vc_16 = (ch.qos.logback.core.rolling.helper.FileFilterUtil)null;
        // AssertGenerator add assertion
        org.junit.Assert.assertNull(vc_16);
        // StatementAdderMethod cloned existing statement
        vc_16.filesInFolderMatchingStemRegex(vc_18, vc_20);
        junit.framework.Assert.assertEquals(12, result);
    }
}

