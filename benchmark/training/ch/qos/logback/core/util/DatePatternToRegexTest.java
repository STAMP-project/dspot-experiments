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
package ch.qos.logback.core.util;


import CoreConstants.ISO8601_PATTERN;
import java.util.Calendar;
import java.util.Locale;
import org.junit.Test;


public class DatePatternToRegexTest {
    static Calendar CAL_2009_08_3_NIGHT = Calendar.getInstance();

    static Calendar CAL_2009_08_3_MORNING = Calendar.getInstance();

    static Locale CZ_LOCALE = new Locale("cs", "CZ");

    static Locale KO_LOCALE = new Locale("ko", "KR");

    @Test
    public void ISO8601() {
        doTest(ISO8601_PATTERN, DatePatternToRegexTest.CAL_2009_08_3_NIGHT);
    }

    @Test
    public void withQuotes() {
        doTest("yyyy-MM-dd'T'HH:mm:ss,SSS", DatePatternToRegexTest.CAL_2009_08_3_NIGHT);
    }

    @Test
    public void month() {
        doTest("yyyy-MMM-dd", DatePatternToRegexTest.CAL_2009_08_3_NIGHT);
        doTest("yyyy-MMM-dd", DatePatternToRegexTest.CAL_2009_08_3_NIGHT, DatePatternToRegexTest.CZ_LOCALE);
        doTest("yyyy-MMM-dd", DatePatternToRegexTest.CAL_2009_08_3_NIGHT, DatePatternToRegexTest.KO_LOCALE);
        doTest("yyyy-MMMM-dd", DatePatternToRegexTest.CAL_2009_08_3_NIGHT);
        doTest("yyyy-MMMM-dd", DatePatternToRegexTest.CAL_2009_08_3_NIGHT, DatePatternToRegexTest.CZ_LOCALE);
        doTest("yyyy-MMMM-dd", DatePatternToRegexTest.CAL_2009_08_3_NIGHT, DatePatternToRegexTest.KO_LOCALE);
    }

    @Test
    public void dot() {
        doTest("yyyy.MMM.dd", DatePatternToRegexTest.CAL_2009_08_3_NIGHT);
    }

    @Test
    public void timeZone() {
        doTest("yyyy-MMM-dd HH:mm:ss z", DatePatternToRegexTest.CAL_2009_08_3_NIGHT);
        doTest("yyyy-MMM-dd HH:mm:ss Z", DatePatternToRegexTest.CAL_2009_08_3_NIGHT);
    }

    @Test
    public void dayInWeek() {
        doTest("EE", DatePatternToRegexTest.CAL_2009_08_3_NIGHT);
        doTest("EE", DatePatternToRegexTest.CAL_2009_08_3_NIGHT, DatePatternToRegexTest.CZ_LOCALE);
        doTest("EE", DatePatternToRegexTest.CAL_2009_08_3_NIGHT, DatePatternToRegexTest.KO_LOCALE);
        doTest("EEEE", DatePatternToRegexTest.CAL_2009_08_3_NIGHT);
        doTest("EEEE", DatePatternToRegexTest.CAL_2009_08_3_NIGHT, DatePatternToRegexTest.CZ_LOCALE);
        doTest("EEEE", DatePatternToRegexTest.CAL_2009_08_3_NIGHT, DatePatternToRegexTest.KO_LOCALE);
    }

    @Test
    public void amPm() {
        doTest("yyyy-MM-dd a", DatePatternToRegexTest.CAL_2009_08_3_NIGHT);
        doTest("yyyy-MM-dd a", DatePatternToRegexTest.CAL_2009_08_3_NIGHT, DatePatternToRegexTest.CZ_LOCALE);
        doTest("yyyy-MM-dd a", DatePatternToRegexTest.CAL_2009_08_3_NIGHT, DatePatternToRegexTest.KO_LOCALE);
        doTest("yyyy-MM-dd a", DatePatternToRegexTest.CAL_2009_08_3_MORNING);
        doTest("yyyy-MM-dd a", DatePatternToRegexTest.CAL_2009_08_3_MORNING, DatePatternToRegexTest.CZ_LOCALE);
        doTest("yyyy-MM-dd a", DatePatternToRegexTest.CAL_2009_08_3_MORNING, DatePatternToRegexTest.KO_LOCALE);
    }

    Locale locale;
}

