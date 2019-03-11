/**
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package libcore.icu;


import java.util.Locale;
import junit.framework.TestCase;


public class LocaleDataTest extends TestCase {
    public void testAll() throws Exception {
        // Test that we can get the locale data for all known locales.
        for (Locale l : Locale.getAvailableLocales()) {
            LocaleData d = LocaleData.get(l);
            // System.err.format("%10s %10s %10s\n", l, d.timeFormat12, d.timeFormat24);
        }
    }

    public void test_en_US() throws Exception {
        LocaleData l = LocaleData.get(Locale.US);
        TestCase.assertEquals("AM", l.amPm[0]);
        TestCase.assertEquals("BC", l.eras[0]);
        TestCase.assertEquals("January", l.longMonthNames[0]);
        TestCase.assertEquals("Jan", l.shortMonthNames[0]);
        TestCase.assertEquals("J", l.tinyMonthNames[0]);
        TestCase.assertEquals("January", l.longStandAloneMonthNames[0]);
        TestCase.assertEquals("Jan", l.shortStandAloneMonthNames[0]);
        TestCase.assertEquals("J", l.tinyStandAloneMonthNames[0]);
        TestCase.assertEquals("Sunday", l.longWeekdayNames[1]);
        TestCase.assertEquals("Sun", l.shortWeekdayNames[1]);
        TestCase.assertEquals("S", l.tinyWeekdayNames[1]);
        TestCase.assertEquals("Sunday", l.longStandAloneWeekdayNames[1]);
        TestCase.assertEquals("Sun", l.shortStandAloneWeekdayNames[1]);
        TestCase.assertEquals("S", l.tinyStandAloneWeekdayNames[1]);
        TestCase.assertEquals("Yesterday", l.yesterday);
        TestCase.assertEquals("Today", l.today);
        TestCase.assertEquals("Tomorrow", l.tomorrow);
    }

    public void test_de_DE() throws Exception {
        LocaleData l = LocaleData.get(new Locale("de", "DE"));
        TestCase.assertEquals("Gestern", l.yesterday);
        TestCase.assertEquals("Heute", l.today);
        TestCase.assertEquals("Morgen", l.tomorrow);
    }

    public void test_cs_CZ() throws Exception {
        LocaleData l = LocaleData.get(new Locale("cs", "CZ"));
        TestCase.assertEquals("ledna", l.longMonthNames[0]);
        TestCase.assertEquals("led", l.shortMonthNames[0]);
        TestCase.assertEquals("1", l.tinyMonthNames[0]);
        TestCase.assertEquals("leden", l.longStandAloneMonthNames[0]);
        TestCase.assertEquals("led", l.shortStandAloneMonthNames[0]);
        TestCase.assertEquals("l", l.tinyStandAloneMonthNames[0]);
    }

    public void test_ru_RU() throws Exception {
        LocaleData l = LocaleData.get(new Locale("ru", "RU"));
        TestCase.assertEquals("???????????", l.longWeekdayNames[1]);
        TestCase.assertEquals("??", l.shortWeekdayNames[1]);
        TestCase.assertEquals("??", l.tinyWeekdayNames[1]);
        // Russian stand-alone weekday names get an initial capital.
        TestCase.assertEquals("???????????", l.longStandAloneWeekdayNames[1]);
        TestCase.assertEquals("??", l.shortStandAloneWeekdayNames[1]);
        TestCase.assertEquals("?", l.tinyStandAloneWeekdayNames[1]);
    }

    // http://code.google.com/p/android/issues/detail?id=38844
    public void testDecimalFormatSymbols_es() throws Exception {
        LocaleData es = LocaleData.get(new Locale("es"));
        TestCase.assertEquals(',', es.decimalSeparator);
        TestCase.assertEquals('.', es.groupingSeparator);
        LocaleData es_419 = LocaleData.get(new Locale("es", "419"));
        TestCase.assertEquals('.', es_419.decimalSeparator);
        TestCase.assertEquals(',', es_419.groupingSeparator);
        LocaleData es_US = LocaleData.get(new Locale("es", "US"));
        TestCase.assertEquals('.', es_US.decimalSeparator);
        TestCase.assertEquals(',', es_US.groupingSeparator);
        LocaleData es_MX = LocaleData.get(new Locale("es", "MX"));
        TestCase.assertEquals('.', es_MX.decimalSeparator);
        TestCase.assertEquals(',', es_MX.groupingSeparator);
        LocaleData es_AR = LocaleData.get(new Locale("es", "AR"));
        TestCase.assertEquals(',', es_AR.decimalSeparator);
        TestCase.assertEquals('.', es_AR.groupingSeparator);
    }

    // http://b/7924970
    public void testTimeFormat12And24() throws Exception {
        LocaleData en_US = LocaleData.get(Locale.US);
        TestCase.assertEquals("h:mm a", en_US.timeFormat12);
        TestCase.assertEquals("HH:mm", en_US.timeFormat24);
        LocaleData ja_JP = LocaleData.get(Locale.JAPAN);
        TestCase.assertEquals("aK:mm", ja_JP.timeFormat12);
        TestCase.assertEquals("H:mm", ja_JP.timeFormat24);
    }
}

