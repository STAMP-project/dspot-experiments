/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.util;


import DateDetector.DATE_FORMAT_TO_REGEXPS;
import DateDetector.DATE_FORMAT_TO_REGEXPS_US;
import java.text.ParseException;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;


public class DateDetectorTest {
    private static final String SAMPLE_REGEXP = "^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$";

    private static final String SAMPLE_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private static Date SAMPLE_DATE;

    private static String SAMPLE_DATE_STRING;

    private static final String LOCALE_en_US = "en_US";

    private static final String LOCALE_es = "es";

    private static final String SAMPLE_REGEXP_US = "^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$";

    private static final String SAMPLE_DATE_FORMAT_US = "MM/dd/yyyy HH:mm:ss";

    private static Date SAMPLE_DATE_US;

    private static String SAMPLE_DATE_STRING_US;

    @Test
    public void testGetRegexpByDateFormat() {
        Assert.assertNull(DateDetector.getRegexpByDateFormat(null));
        Assert.assertEquals(DateDetectorTest.SAMPLE_REGEXP, DateDetector.getRegexpByDateFormat(DateDetectorTest.SAMPLE_DATE_FORMAT));
    }

    @Test
    public void testGetRegexpByDateFormatLocale() {
        Assert.assertNull(DateDetector.getRegexpByDateFormat(null, null));
        Assert.assertNull(DateDetector.getRegexpByDateFormat(null, DateDetectorTest.LOCALE_en_US));
        // return null if we pass US dateformat without locale
        Assert.assertNull(DateDetector.getRegexpByDateFormat(DateDetectorTest.SAMPLE_DATE_FORMAT_US));
        Assert.assertEquals(DateDetectorTest.SAMPLE_REGEXP_US, DateDetector.getRegexpByDateFormat(DateDetectorTest.SAMPLE_DATE_FORMAT_US, DateDetectorTest.LOCALE_en_US));
    }

    @Test
    public void testGetDateFormatByRegex() {
        Assert.assertNull(DateDetector.getDateFormatByRegex(null));
        Assert.assertEquals(DateDetectorTest.SAMPLE_DATE_FORMAT, DateDetector.getDateFormatByRegex(DateDetectorTest.SAMPLE_REGEXP));
    }

    @Test
    public void testGetDateFormatByRegexLocale() {
        Assert.assertNull(DateDetector.getDateFormatByRegex(null, null));
        Assert.assertNull(DateDetector.getDateFormatByRegex(null, DateDetectorTest.LOCALE_en_US));
        // return eu if we pass en_US regexp without locale
        Assert.assertEquals(DateDetectorTest.SAMPLE_DATE_FORMAT, DateDetector.getDateFormatByRegex(DateDetectorTest.SAMPLE_REGEXP_US));
        Assert.assertEquals(DateDetectorTest.SAMPLE_DATE_FORMAT_US, DateDetector.getDateFormatByRegex(DateDetectorTest.SAMPLE_REGEXP_US, DateDetectorTest.LOCALE_en_US));
    }

    @Test
    public void testGetDateFromString() throws ParseException {
        Assert.assertEquals(DateDetectorTest.SAMPLE_DATE_US, DateDetector.getDateFromString(DateDetectorTest.SAMPLE_DATE_STRING_US));
        try {
            DateDetector.getDateFromString(null);
        } catch (ParseException e) {
            // expected exception
        }
    }

    @Test
    public void testGetDateFromStringLocale() throws ParseException {
        Assert.assertEquals(DateDetectorTest.SAMPLE_DATE_US, DateDetector.getDateFromString(DateDetectorTest.SAMPLE_DATE_STRING_US, DateDetectorTest.LOCALE_en_US));
        try {
            DateDetector.getDateFromString(null);
        } catch (ParseException e) {
            // expected exception
        }
        try {
            DateDetector.getDateFromString(null, null);
        } catch (ParseException e) {
            // expected exception
        }
    }

    @Test
    public void testGetDateFromStringByFormat() throws ParseException {
        Assert.assertEquals(DateDetectorTest.SAMPLE_DATE, DateDetector.getDateFromStringByFormat(DateDetectorTest.SAMPLE_DATE_STRING, DateDetectorTest.SAMPLE_DATE_FORMAT));
        try {
            DateDetector.getDateFromStringByFormat(DateDetectorTest.SAMPLE_DATE_STRING, null);
        } catch (ParseException e) {
            // expected exception
        }
        try {
            DateDetector.getDateFromStringByFormat(null, DateDetectorTest.SAMPLE_DATE_FORMAT);
        } catch (ParseException e) {
            // expected exception
        }
    }

    @Test
    public void testDetectDateFormat() {
        Assert.assertEquals(DateDetectorTest.SAMPLE_DATE_FORMAT, DateDetector.detectDateFormat(DateDetectorTest.SAMPLE_DATE_STRING, DateDetectorTest.LOCALE_es));
        Assert.assertNull(DateDetector.detectDateFormat(null));
    }

    @Test
    public void testIsValidDate() {
        Assert.assertTrue(DateDetector.isValidDate(DateDetectorTest.SAMPLE_DATE_STRING_US));
        Assert.assertFalse(DateDetector.isValidDate(null));
        Assert.assertTrue(DateDetector.isValidDate(DateDetectorTest.SAMPLE_DATE_STRING, DateDetectorTest.SAMPLE_DATE_FORMAT));
        Assert.assertFalse(DateDetector.isValidDate(DateDetectorTest.SAMPLE_DATE_STRING, null));
    }

    @Test
    public void testIsValidDateFormatToStringDate() {
        Assert.assertTrue(DateDetector.isValidDateFormatToStringDate(DateDetectorTest.SAMPLE_DATE_FORMAT_US, DateDetectorTest.SAMPLE_DATE_STRING_US));
        Assert.assertFalse(DateDetector.isValidDateFormatToStringDate(null, DateDetectorTest.SAMPLE_DATE_STRING_US));
        Assert.assertFalse(DateDetector.isValidDateFormatToStringDate(DateDetectorTest.SAMPLE_DATE_FORMAT_US, null));
    }

    @Test
    public void testIsValidDateFormatToStringDateLocale() {
        Assert.assertTrue(DateDetector.isValidDateFormatToStringDate(DateDetectorTest.SAMPLE_DATE_FORMAT_US, DateDetectorTest.SAMPLE_DATE_STRING_US, DateDetectorTest.LOCALE_en_US));
        Assert.assertFalse(DateDetector.isValidDateFormatToStringDate(null, DateDetectorTest.SAMPLE_DATE_STRING, DateDetectorTest.LOCALE_en_US));
        Assert.assertFalse(DateDetector.isValidDateFormatToStringDate(DateDetectorTest.SAMPLE_DATE_FORMAT_US, null, DateDetectorTest.LOCALE_en_US));
        Assert.assertTrue(DateDetector.isValidDateFormatToStringDate(DateDetectorTest.SAMPLE_DATE_FORMAT_US, DateDetectorTest.SAMPLE_DATE_STRING_US, null));
    }

    @Test
    public void testAllPatterns() {
        testPatternsFrom(DATE_FORMAT_TO_REGEXPS_US, DateDetectorTest.LOCALE_en_US);
        testPatternsFrom(DATE_FORMAT_TO_REGEXPS, DateDetectorTest.LOCALE_es);
    }
}

