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
package ch.qos.logback.core.rolling.helper;


/**
 *
 *
 * @author Ceki
 */
public class AmplFileNamePatternTest {
    ch.qos.logback.core.Context context = new ch.qos.logback.core.ContextBase();

    @org.junit.Test
    public void testSmoke() {
        ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("t", context);
        org.junit.Assert.assertEquals("t", pp.convertInt(3));
        pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo", context);
        org.junit.Assert.assertEquals("foo", pp.convertInt(3));
        pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%i foo", context);
        org.junit.Assert.assertEquals("3 foo", pp.convertInt(3));
        pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%i.xixo", context);
        org.junit.Assert.assertEquals("foo3.xixo", pp.convertInt(3));
        pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%i.log", context);
        org.junit.Assert.assertEquals("foo3.log", pp.convertInt(3));
        pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo.%i.log", context);
        org.junit.Assert.assertEquals("foo.3.log", pp.convertInt(3));
        // pp = new FileNamePattern("%i.foo\\%", context);
        // assertEquals("3.foo%", pp.convertInt(3));
        // pp = new FileNamePattern("\\%foo", context);
        // assertEquals("%foo", pp.convertInt(3));
    }

    // test ways for dealing with flowing i converter, as in "foo%ix"
    @org.junit.Test
    public void flowingI() {
        {
            ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%i{}bar%i", context);
            org.junit.Assert.assertEquals("foo3bar3", pp.convertInt(3));
        }
        {
            ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%i{}bar%i", context);
            org.junit.Assert.assertEquals("foo3bar3", pp.convertInt(3));
        }
    }

    @org.junit.Test
    public void date() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2003, 4, 20, 17, 55);
        ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%d{yyyy.MM.dd}", context);
        org.junit.Assert.assertEquals("foo2003.05.20", pp.convert(cal.getTime()));
        pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%d{yyyy.MM.dd HH:mm}", context);
        org.junit.Assert.assertEquals("foo2003.05.20 17:55", pp.convert(cal.getTime()));
        pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%d{yyyy.MM.dd HH:mm} foo", context);
        org.junit.Assert.assertEquals("2003.05.20 17:55 foo", pp.convert(cal.getTime()));
    }

    @org.junit.Test
    public void dateWithTimeZone() {
        java.util.TimeZone utc = java.util.TimeZone.getTimeZone("UTC");
        java.util.Calendar cal = java.util.Calendar.getInstance(utc);
        cal.set(2003, 4, 20, 10, 55);
        ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%d{yyyy-MM-dd'T'HH:mm, Australia/Perth}", context);
        // Perth is 8 hours ahead of UTC
        org.junit.Assert.assertEquals("foo2003-05-20T18:55", fnp.convert(cal.getTime()));
    }

    @org.junit.Test
    public void auxAndTimeZoneShouldNotConflict() {
        java.util.TimeZone utc = java.util.TimeZone.getTimeZone("UTC");
        java.util.Calendar cal = java.util.Calendar.getInstance(utc);
        cal.set(2003, 4, 20, 10, 55);
        {
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%d{yyyy-MM-dd'T'HH:mm, aux, Australia/Perth}", context);
            // Perth is 8 hours ahead of UTC
            org.junit.Assert.assertEquals("foo2003-05-20T18:55", fnp.convert(cal.getTime()));
            org.junit.Assert.assertNull(fnp.getPrimaryDateTokenConverter());
        }
        {
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("folder/%d{yyyy/MM, aux, Australia/Perth}/test.%d{yyyy-MM-dd'T'HHmm, Australia/Perth}.log", context);
            org.junit.Assert.assertEquals("folder/2003/05/test.2003-05-20T1855.log", fnp.convert(cal.getTime()));
            org.junit.Assert.assertNotNull(fnp.getPrimaryDateTokenConverter());
        }
    }

    @org.junit.Test
    public void withBackslash() {
        ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("c:\\foo\\bar.%i", context);
        org.junit.Assert.assertEquals("c:/foo/bar.3", pp.convertInt(3));
    }

    @org.junit.Test
    public void objectListConverter() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2003, 4, 20, 17, 55);
        ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM.dd}-%i.txt", context);
        org.junit.Assert.assertEquals("foo-2003.05.20-79.txt", fnp.convertMultipleArguments(cal.getTime(), 79));
    }

    @org.junit.Test
    public void asRegexByDate() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2003, 4, 20, 17, 55);
        {
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM.dd}-%i.txt", context);
            java.lang.String regex = fnp.toRegexForFixedDate(cal.getTime());
            org.junit.Assert.assertEquals("foo-2003.05.20-(\\d{1,3}).txt", regex);
        }
        {
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("\\toto\\foo-%d{yyyy\\MM\\dd}-%i.txt", context);
            java.lang.String regex = fnp.toRegexForFixedDate(cal.getTime());
            org.junit.Assert.assertEquals("/toto/foo-2003/05/20-(\\d{1,3}).txt", regex);
        }
    }

    @org.junit.Test
    public void asRegex() {
        {
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM.dd}-%i.txt", context);
            java.lang.String regex = fnp.toRegex();
            org.junit.Assert.assertEquals("foo-\\d{4}\\.\\d{2}\\.\\d{2}-\\d{1,2}.txt", regex);
        }
        {
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM.dd'T'}-%i.txt", context);
            java.lang.String regex = fnp.toRegex();
            org.junit.Assert.assertEquals("foo-\\d{4}\\.\\d{2}\\.\\d{2}T-\\d{1,2}.txt", regex);
        }
    }

    @org.junit.Test
    public void convertMultipleDates() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.set(2003, 4, 20, 17, 55);
        ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM, aux}/%d{yyyy.MM.dd}.txt", context);
        org.junit.Assert.assertEquals("foo-2003.05/2003.05.20.txt", fnp.convert(cal.getTime()));
    }

    @org.junit.Test
    public void nullTimeZoneByDefault() {
        ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%d{hh}", context);
        org.junit.Assert.assertNull(fnp.getPrimaryDateTokenConverter().getTimeZone());
    }

    @org.junit.Test
    public void settingTimeZoneOptionHasAnEffect() {
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone("Australia/Perth");
        ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern((("%d{hh, " + (tz.getID())) + "}"), context);
        org.junit.Assert.assertEquals(tz, fnp.getPrimaryDateTokenConverter().getTimeZone());
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#asRegex */
    @org.junit.Test(timeout = 10000)
    public void asRegex_sd29_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM.dd}-%i.txt", context);
                java.lang.String regex = fnp.toRegex();
            }
            {
                int __DSPOT_i_7 = -415120022;
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM.dd'T'}-%i.txt", context);
                java.lang.String regex = fnp.toRegex();
                // StatementAdd: add invocation of a method
                fnp.convertInt(__DSPOT_i_7);
            }
            org.junit.Assert.fail("asRegex_sd29 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#asRegex */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#asRegex_sd15 */
    @org.junit.Test(timeout = 10000)
    public void asRegex_sd15_failAssert0_literalMutationString126_sd9410_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                {
                    int __DSPOT_arg0_7119 = 417991084;
                    java.lang.Object __DSPOT_o_0 = new java.lang.Object();
                    ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("bk*201yCi*OdwpauR%h1,xavU", context);
                    java.lang.String regex = fnp.toRegex();
                    // StatementAdd: generate variable from return value
                    java.lang.String __DSPOT_invoc_10 = // StatementAdd: add invocation of a method
                    fnp.convert(__DSPOT_o_0);
                    // StatementAdd: add invocation of a method
                    __DSPOT_invoc_10.codePointAt(__DSPOT_arg0_7119);
                }
                {
                    ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM.dd'T'}-%i.txt", context);
                    java.lang.String regex = fnp.toRegex();
                }
                org.junit.Assert.fail("asRegex_sd15 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("asRegex_sd15_failAssert0_literalMutationString126_sd9410 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#asRegexByDate */
    @org.junit.Test(timeout = 10000)
    public void asRegexByDate_literalMutationString20079_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(2003, 4, 20, 17, 55);
            {
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM.dd}-%i.txt", context);
                java.lang.String regex = fnp.toRegexForFixedDate(cal.getTime());
            }
            {
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("\\toto\\foo-%d{yyyy\\MM\\dd}-%).txt", context);
                java.lang.String regex = fnp.toRegexForFixedDate(cal.getTime());
            }
            org.junit.Assert.fail("asRegexByDate_literalMutationString20079 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#asRegexByDate */
    @org.junit.Test(timeout = 10000)
    public void asRegexByDate_sd20091_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(2003, 4, 20, 17, 55);
            {
                int __DSPOT_i_15376 = 2082313708;
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM.dd}-%i.txt", context);
                java.lang.String regex = fnp.toRegexForFixedDate(cal.getTime());
                // StatementAdd: add invocation of a method
                fnp.convertInt(__DSPOT_i_15376);
            }
            {
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("\\toto\\foo-%d{yyyy\\MM\\dd}-%i.txt", context);
                java.lang.String regex = fnp.toRegexForFixedDate(cal.getTime());
            }
            org.junit.Assert.fail("asRegexByDate_sd20091 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#auxAndTimeZoneShouldNotConflict */
    @org.junit.Test(timeout = 10000)
    public void auxAndTimeZoneShouldNotConflict_sd43892_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.TimeZone utc = java.util.TimeZone.getTimeZone("UTC");
            java.util.Calendar cal = java.util.Calendar.getInstance(utc);
            cal.set(2003, 4, 20, 10, 55);
            {
                int __DSPOT_i_29116 = -519573128;
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%d{yyyy-MM-dd'T'HH:mm, aux, Australia/Perth}", context);
                fnp.convert(cal.getTime());
                fnp.getPrimaryDateTokenConverter();
                // StatementAdd: add invocation of a method
                fnp.convertInt(__DSPOT_i_29116);
            }
            {
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("folder/%d{yyyy/MM, aux, Australia/Perth}/test.%d{yyyy-MM-dd'T'HHmm, Australia/Perth}.log", context);
                fnp.convert(cal.getTime());
                fnp.getPrimaryDateTokenConverter();
            }
            org.junit.Assert.fail("auxAndTimeZoneShouldNotConflict_sd43892 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#convertMultipleDates */
    @org.junit.Test(timeout = 10000)
    public void convertMultipleDates_sd48753_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_i_32396 = -1798511515;
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(2003, 4, 20, 17, 55);
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM, aux}/%d{yyyy.MM.dd}.txt", context);
            fnp.convert(cal.getTime());
            // StatementAdd: add invocation of a method
            fnp.convertInt(__DSPOT_i_32396);
            org.junit.Assert.fail("convertMultipleDates_sd48753 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#date */
    @org.junit.Test(timeout = 10000)
    public void date_sd79007_failAssert2() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_i_58246 = 1952048914;
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(2003, 4, 20, 17, 55);
            ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%d{yyyy.MM.dd}", context);
            pp.convert(cal.getTime());
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%d{yyyy.MM.dd HH:mm}", context);
            pp.convert(cal.getTime());
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%d{yyyy.MM.dd HH:mm} foo", context);
            pp.convert(cal.getTime());
            // StatementAdd: add invocation of a method
            pp.convertInt(__DSPOT_i_58246);
            org.junit.Assert.fail("date_sd79007 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#dateWithTimeZone */
    @org.junit.Test(timeout = 10000)
    public void dateWithTimeZone_sd81598_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_i_59653 = 687075819;
            java.util.TimeZone utc = java.util.TimeZone.getTimeZone("UTC");
            java.util.Calendar cal = java.util.Calendar.getInstance(utc);
            cal.set(2003, 4, 20, 10, 55);
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%d{yyyy-MM-dd'T'HH:mm, Australia/Perth}", context);
            fnp.convert(cal.getTime());
            // StatementAdd: add invocation of a method
            fnp.convertInt(__DSPOT_i_59653);
            org.junit.Assert.fail("dateWithTimeZone_sd81598 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    // test ways for dealing with flowing i converter, as in "foo%ix"
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#flowingI */
    @org.junit.Test(timeout = 10000)
    public void flowingI_sd82923_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            {
                java.lang.Object __DSPOT_o_60007 = new java.lang.Object();
                ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%i{}bar%i", context);
                pp.convertInt(3);
                // StatementAdd: add invocation of a method
                pp.convert(__DSPOT_o_60007);
            }
            {
                ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%i{}bar%i", context);
                pp.convertInt(3);
            }
            org.junit.Assert.fail("flowingI_sd82923 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault_sd84414 */
    @org.junit.Test(timeout = 10000)
    public void nullTimeZoneByDefault_sd84414_sd84752_failAssert50() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%d{hh}", context);
            // StatementAdd: generate variable from return value
            java.util.TimeZone __DSPOT_invoc_3 = fnp.getPrimaryDateTokenConverter().getTimeZone();
            // StatementAdd: add invocation of a method
            fnp.getIntegerTokenConverter();
            ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledFutures().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusList().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusList().isEmpty();
            // AssertGenerator add assertion
            java.util.HashMap map_1059826414 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1059826414, ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getCopyOfPropertyMap());;
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledExecutorService()).shutdownNow().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getExecutorService()).shutdownNow().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_3.getDSTSavings();
            org.junit.Assert.fail("nullTimeZoneByDefault_sd84414_sd84752 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault_sd84415 */
    @org.junit.Test(timeout = 10000)
    public void nullTimeZoneByDefault_sd84415_literalMutationString84768_failAssert7() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("F_%,Q>", context);
            ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledFutures().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusList().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusList().isEmpty();
            // AssertGenerator add assertion
            java.util.HashMap map_1035202072 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1035202072, ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getCopyOfPropertyMap());;
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledExecutorService()).shutdownNow().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getExecutorService()).shutdownNow().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            fnp.getPrimaryDateTokenConverter().getTimeZone();
            // StatementAdd: add invocation of a method
            fnp.getPattern();
            ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledFutures().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusList().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusList().isEmpty();
            // AssertGenerator add assertion
            java.util.HashMap map_636340872 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_636340872, ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getCopyOfPropertyMap());;
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledExecutorService()).shutdownNow().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getExecutorService()).shutdownNow().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            org.junit.Assert.fail("nullTimeZoneByDefault_sd84415_literalMutationString84768 should have thrown NumberFormatException");
        } catch (java.lang.NumberFormatException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault_sd84415 */
    @org.junit.Test(timeout = 10000)
    public void nullTimeZoneByDefault_sd84415_sd84818_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_arg0_60688 = 1798339584;
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%d{hh}", context);
            ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledFutures().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusList().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusList().isEmpty();
            // AssertGenerator add assertion
            java.util.HashMap map_1035202072 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1035202072, ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getCopyOfPropertyMap());;
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledExecutorService()).shutdownNow().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getExecutorService()).shutdownNow().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            fnp.getPrimaryDateTokenConverter().getTimeZone();
            // StatementAdd: generate variable from return value
            java.lang.String __DSPOT_invoc_130 = // StatementAdd: add invocation of a method
            fnp.getPattern();
            ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledFutures().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusList().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusList().isEmpty();
            // AssertGenerator add assertion
            java.util.HashMap map_636340872 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_636340872, ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getCopyOfPropertyMap());;
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledExecutorService()).shutdownNow().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getExecutorService()).shutdownNow().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_130.codePointAt(__DSPOT_arg0_60688);
            org.junit.Assert.fail("nullTimeZoneByDefault_sd84415_sd84818 should have thrown StringIndexOutOfBoundsException");
        } catch (java.lang.StringIndexOutOfBoundsException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault_sd84412 */
    @org.junit.Test(timeout = 10000)
    public void nullTimeZoneByDefault_sd84412_literalMutationString84633_failAssert33() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.Object[] __DSPOT_objectList_60571 = new java.lang.Object[]{ new java.lang.Object(), new java.lang.Object(), new java.lang.Object() };
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%A{hh}", context);
            ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledFutures().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusList().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusList().isEmpty();
            // AssertGenerator add assertion
            java.util.HashMap map_1298779629 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1298779629, ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getCopyOfPropertyMap());;
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledExecutorService()).shutdownNow().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getExecutorService()).shutdownNow().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            fnp.getPrimaryDateTokenConverter().getTimeZone();
            // AssertGenerator create local variable with return value of invocation
            java.lang.String o_nullTimeZoneByDefault_sd84412__9 = // StatementAdd: add invocation of a method
            fnp.convertMultipleArguments(__DSPOT_objectList_60571);
            ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledFutures().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusList().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusList().isEmpty();
            // AssertGenerator add assertion
            java.util.HashMap map_1224140785 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1224140785, ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getCopyOfPropertyMap());;
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledExecutorService()).shutdownNow().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getExecutorService()).shutdownNow().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            org.junit.Assert.fail("nullTimeZoneByDefault_sd84412_literalMutationString84633 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault_literalMutationString84407 */
    @org.junit.Test(timeout = 10000)
    public void nullTimeZoneByDefault_literalMutationString84407_sd84496_failAssert18() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_i_60576 = 38628163;
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%d{h}", context);
            fnp.getPrimaryDateTokenConverter().getTimeZone();
            ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledFutures().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusList().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusList().isEmpty();
            // AssertGenerator add assertion
            java.util.HashMap map_1499385995 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1499385995, ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getCopyOfPropertyMap());;
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledExecutorService()).shutdownNow().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getExecutorService()).shutdownNow().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            // StatementAdd: add invocation of a method
            fnp.convertInt(__DSPOT_i_60576);
            org.junit.Assert.fail("nullTimeZoneByDefault_literalMutationString84407_sd84496 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault_sd84413 */
    @org.junit.Test(timeout = 10000)
    public void nullTimeZoneByDefault_sd84413_sd84714_failAssert13() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.Object __DSPOT_obj_60572 = new java.lang.Object();
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%d{hh}", context);
            // StatementAdd: generate variable from return value
            java.util.TimeZone __DSPOT_invoc_5 = fnp.getPrimaryDateTokenConverter().getTimeZone();
            // AssertGenerator create local variable with return value of invocation
            boolean o_nullTimeZoneByDefault_sd84413__7 = // StatementAdd: add invocation of a method
            fnp.equals(__DSPOT_obj_60572);
            ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledFutures().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusList().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusList().isEmpty();
            // AssertGenerator add assertion
            java.util.HashMap map_1066796749 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1066796749, ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getCopyOfPropertyMap());;
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledExecutorService()).shutdownNow().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getExecutorService()).shutdownNow().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            // StatementAdd: add invocation of a method
            __DSPOT_invoc_5.getID();
            org.junit.Assert.fail("nullTimeZoneByDefault_sd84413_sd84714 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault_sd84414 */
    @org.junit.Test(timeout = 10000)
    public void nullTimeZoneByDefault_sd84414_sd84738_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_i_60657 = 1512802833;
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%d{hh}", context);
            fnp.getPrimaryDateTokenConverter().getTimeZone();
            // StatementAdd: add invocation of a method
            fnp.getIntegerTokenConverter();
            ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledFutures().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusList().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusList().isEmpty();
            // AssertGenerator add assertion
            java.util.HashMap map_1059826414 = new java.util.HashMap<Object, Object>();	org.junit.Assert.assertEquals(map_1059826414, ((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getCopyOfPropertyMap());;
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getScheduledExecutorService()).shutdownNow().isEmpty();
            ((java.util.concurrent.ScheduledThreadPoolExecutor)((ch.qos.logback.core.ContextBase)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getContext()).getExecutorService()).shutdownNow().isEmpty();
            ((ch.qos.logback.core.BasicStatusManager)((ch.qos.logback.core.rolling.helper.FileNamePattern)fnp).getStatusManager()).getCopyOfStatusListenerList().isEmpty();
            // StatementAdd: add invocation of a method
            fnp.convertInt(__DSPOT_i_60657);
            org.junit.Assert.fail("nullTimeZoneByDefault_sd84414_sd84738 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault_sd84410 */
    @org.junit.Test(timeout = 10000)
    public void nullTimeZoneByDefault_sd84410_failAssert5_literalMutationString85371_failAssert31() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.Object __DSPOT_o_60569 = new java.lang.Object();
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%Md{hh}", context);
                fnp.getPrimaryDateTokenConverter().getTimeZone();
                // StatementAdd: add invocation of a method
                fnp.convert(__DSPOT_o_60569);
                org.junit.Assert.fail("nullTimeZoneByDefault_sd84410 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("nullTimeZoneByDefault_sd84410_failAssert5_literalMutationString85371 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#nullTimeZoneByDefault_sd84410 */
    @org.junit.Test(timeout = 10000)
    public void nullTimeZoneByDefault_sd84410_failAssert5_literalMutationString85369_failAssert25() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.Object __DSPOT_o_60569 = new java.lang.Object();
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("", context);
                fnp.getPrimaryDateTokenConverter().getTimeZone();
                // StatementAdd: add invocation of a method
                fnp.convert(__DSPOT_o_60569);
                org.junit.Assert.fail("nullTimeZoneByDefault_sd84410 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("nullTimeZoneByDefault_sd84410_failAssert5_literalMutationString85369 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#objectListConverter */
    @org.junit.Test(timeout = 10000)
    public void objectListConverter_sd90217_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.Object __DSPOT_o_63525 = new java.lang.Object();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(2003, 4, 20, 17, 55);
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo-%d{yyyy.MM.dd}-%i.txt", context);
            fnp.convertMultipleArguments(cal.getTime(), 79);
            // StatementAdd: add invocation of a method
            fnp.convert(__DSPOT_o_63525);
            org.junit.Assert.fail("objectListConverter_sd90217 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#settingTimeZoneOptionHasAnEffect */
    @org.junit.Test(timeout = 10000)
    public void settingTimeZoneOptionHasAnEffect_literalMutationString91555_failAssert6() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.TimeZone tz = java.util.TimeZone.getTimeZone("Australia/Perth");
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern((("%d{hh, " + (tz.getID())) + "#rfa_collision"), context);
            fnp.getPrimaryDateTokenConverter().getTimeZone();
            org.junit.Assert.fail("settingTimeZoneOptionHasAnEffect_literalMutationString91555 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#settingTimeZoneOptionHasAnEffect */
    @org.junit.Test(timeout = 10000)
    public void settingTimeZoneOptionHasAnEffect_literalMutationString91552_failAssert3() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.TimeZone tz = java.util.TimeZone.getTimeZone("Australia/Perth");
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern((("&7Lv%xn" + (tz.getID())) + "}"), context);
            fnp.getPrimaryDateTokenConverter().getTimeZone();
            org.junit.Assert.fail("settingTimeZoneOptionHasAnEffect_literalMutationString91552 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#settingTimeZoneOptionHasAnEffect */
    @org.junit.Test(timeout = 10000)
    public void settingTimeZoneOptionHasAnEffect_sd91562_failAssert9() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            int __DSPOT_i_64017 = 712172254;
            java.util.TimeZone tz = java.util.TimeZone.getTimeZone("Australia/Perth");
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern((("%d{hh, " + (tz.getID())) + "}"), context);
            fnp.getPrimaryDateTokenConverter().getTimeZone();
            // StatementAdd: add invocation of a method
            fnp.convertInt(__DSPOT_i_64017);
            org.junit.Assert.fail("settingTimeZoneOptionHasAnEffect_sd91562 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#settingTimeZoneOptionHasAnEffect */
    @org.junit.Test(timeout = 10000)
    public void settingTimeZoneOptionHasAnEffect_literalMutationString91553_failAssert4() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.util.TimeZone tz = java.util.TimeZone.getTimeZone("Australia/Perth");
            ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern((("%dkhh, " + (tz.getID())) + "}"), context);
            fnp.getPrimaryDateTokenConverter().getTimeZone();
            org.junit.Assert.fail("settingTimeZoneOptionHasAnEffect_literalMutationString91553 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#settingTimeZoneOptionHasAnEffect */
    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#settingTimeZoneOptionHasAnEffect_sd91561 */
    @org.junit.Test(timeout = 10000)
    public void settingTimeZoneOptionHasAnEffect_sd91561_failAssert8_literalMutationString92004_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            // AssertGenerator generate try/catch block with fail statement
            try {
                java.lang.Object __DSPOT_o_64016 = new java.lang.Object();
                java.util.TimeZone tz = java.util.TimeZone.getTimeZone("Australia/Perth");
                ch.qos.logback.core.rolling.helper.FileNamePattern fnp = new ch.qos.logback.core.rolling.helper.FileNamePattern((("%d{hh, " + (tz.getID())) + "j"), context);
                fnp.getPrimaryDateTokenConverter().getTimeZone();
                // StatementAdd: add invocation of a method
                fnp.convert(__DSPOT_o_64016);
                org.junit.Assert.fail("settingTimeZoneOptionHasAnEffect_sd91561 should have thrown IllegalArgumentException");
            } catch (java.lang.IllegalArgumentException eee) {
            }
            org.junit.Assert.fail("settingTimeZoneOptionHasAnEffect_sd91561_failAssert8_literalMutationString92004 should have thrown NullPointerException");
        } catch (java.lang.NullPointerException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#testSmoke */
    @org.junit.Test(timeout = 10000)
    public void testSmoke_sd93430_failAssert1() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.Object __DSPOT_o_64608 = new java.lang.Object();
            ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("t", context);
            pp.convertInt(3);
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo", context);
            pp.convertInt(3);
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("%i foo", context);
            pp.convertInt(3);
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%i.xixo", context);
            pp.convertInt(3);
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%i.log", context);
            pp.convertInt(3);
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo.%i.log", context);
            pp.convertInt(3);
            // pp = new FileNamePattern("%i.foo\\%", context);
            // assertEquals("3.foo%", pp.convertInt(3));
            // pp = new FileNamePattern("\\%foo", context);
            // assertEquals("%foo", pp.convertInt(3));
            // StatementAdd: add invocation of a method
            pp.convert(__DSPOT_o_64608);
            org.junit.Assert.fail("testSmoke_sd93430 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#testSmoke */
    @org.junit.Test(timeout = 10000)
    public void testSmoke_literalMutationString93382_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("t", context);
            pp.convertInt(3);
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo", context);
            pp.convertInt(3);
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("% foo", context);
            pp.convertInt(3);
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%i.xixo", context);
            pp.convertInt(3);
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo%i.log", context);
            pp.convertInt(3);
            pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("foo.%i.log", context);
            pp.convertInt(3);
            // pp = new FileNamePattern("%i.foo\\%", context);
            // assertEquals("3.foo%", pp.convertInt(3));
            // pp = new FileNamePattern("\\%foo", context);
            // assertEquals("%foo", pp.convertInt(3));
            org.junit.Assert.fail("testSmoke_literalMutationString93382 should have thrown NumberFormatException");
        } catch (java.lang.NumberFormatException eee) {
        }
    }

    /* amplification of ch.qos.logback.core.rolling.helper.FileNamePatternTest#withBackslash */
    @org.junit.Test(timeout = 10000)
    public void withBackslash_sd174176_failAssert0() {
        // AssertGenerator generate try/catch block with fail statement
        try {
            java.lang.Object __DSPOT_o_148051 = new java.lang.Object();
            ch.qos.logback.core.rolling.helper.FileNamePattern pp = new ch.qos.logback.core.rolling.helper.FileNamePattern("c:\\foo\\bar.%i", context);
            pp.convertInt(3);
            // StatementAdd: add invocation of a method
            pp.convert(__DSPOT_o_148051);
            org.junit.Assert.fail("withBackslash_sd174176 should have thrown IllegalArgumentException");
        } catch (java.lang.IllegalArgumentException eee) {
        }
    }
}

