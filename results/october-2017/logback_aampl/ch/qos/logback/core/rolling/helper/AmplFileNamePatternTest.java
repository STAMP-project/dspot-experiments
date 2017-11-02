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
}

