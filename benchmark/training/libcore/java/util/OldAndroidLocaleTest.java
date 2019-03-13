/**
 * Copyright (C) 2008 The Android Open Source Project
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
package libcore.java.util;


import java.nio.charset.Charset;
import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import junit.framework.TestCase;


/**
 * Test some locale-dependent stuff for Android. This test mainly ensures that
 * our ICU configuration is correct and contains all the needed locales and
 * resource bundles.
 */
public class OldAndroidLocaleTest extends TestCase {
    // Test basic Locale infrastructure.
    public void testLocale() throws Exception {
        Locale locale = new Locale("en");
        TestCase.assertEquals("en", locale.toString());
        locale = new Locale("en", "US");
        TestCase.assertEquals("en_US", locale.toString());
        locale = new Locale("en", "", "POSIX");
        TestCase.assertEquals("en__POSIX", locale.toString());
        locale = new Locale("en", "US", "POSIX");
        TestCase.assertEquals("en_US_POSIX", locale.toString());
    }

    public void testResourceBundles() throws Exception {
        Locale eng = new Locale("en", "US");
        DateFormatSymbols engSymbols = new DateFormatSymbols(eng);
        Locale deu = new Locale("de", "DE");
        DateFormatSymbols deuSymbols = new DateFormatSymbols(deu);
        TimeZone berlin = TimeZone.getTimeZone("Europe/Berlin");
        TestCase.assertEquals("January", engSymbols.getMonths()[0]);
        TestCase.assertEquals("Januar", deuSymbols.getMonths()[0]);
        TestCase.assertEquals("Sunday", engSymbols.getWeekdays()[Calendar.SUNDAY]);
        TestCase.assertEquals("Sonntag", deuSymbols.getWeekdays()[Calendar.SUNDAY]);
        TestCase.assertEquals("Central European Standard Time", berlin.getDisplayName(false, TimeZone.LONG, eng));
        TestCase.assertEquals("Central European Summer Time", berlin.getDisplayName(true, TimeZone.LONG, eng));
        TestCase.assertEquals("Mitteleurop?ische Normalzeit", berlin.getDisplayName(false, TimeZone.LONG, deu));
        TestCase.assertEquals("Mitteleurop?ische Sommerzeit", berlin.getDisplayName(true, TimeZone.LONG, deu));
        TestCase.assertTrue(((engSymbols.getZoneStrings().length) > 100));
    }

    // This one makes sure we have all necessary locales installed.
    public void testICULocales() {
        String[] locales = new String[]{ // List of locales currently required for Android.
        "en_US", "es_US", "en_GB", "fr_FR", "de_DE", "de_AT", "cs_CZ", "nl_NL" };
        String[] mondays = new String[]{ "Monday", "lunes", "Monday", "lundi", "Montag", "Montag", "pond\u011bl\u00ed", "maandag" };
        String[] currencies = new String[]{ "USD", "USD", "GBP", "EUR", "EUR", "EUR", "CZK", "EUR" };
        for (int i = 0; i < (locales.length); i++) {
            Locale l = new Locale(locales[i].substring(0, 2), locales[i].substring(3));
            // Check language part of locale.
            DateFormatSymbols d = new DateFormatSymbols(l);
            TestCase.assertEquals((("Monday name for " + (locales[i])) + " must match"), mondays[i], d.getWeekdays()[2]);
            // Check country part of locale.
            Currency c = Currency.getInstance(l);
            TestCase.assertEquals((("Currency code for " + (locales[i])) + " must match"), currencies[i], c.getCurrencyCode());
        }
    }

    // Regression test for 1118570: Create test cases for tracking ICU config
    // changes. This one makes sure we have the necessary converters installed
    // and don't lose the changes to the converter alias table.
    public void testICUConverters() {
        // List of encodings currently required for Android.
        String[] encodings = new String[]{ // Encoding required by the language specification.
        "US-ASCII", "UTF-8", "UTF-16", "UTF-16BE", "UTF-16LE", "ISO-8859-1", // Additional encodings included in standard ICU
        "ISO-8859-2", "ISO-8859-3", "ISO-8859-4", "ISO-8859-5", "ISO-8859-6", "ISO-8859-7", "ISO-8859-8", "ISO-8859-8-I", "ISO-8859-9", "ISO-8859-10", "ISO-8859-11", "ISO-8859-13", "ISO-8859-14", "ISO-8859-15", "ISO-2022-JP", "Windows-950", "Windows-1250", "Windows-1251", "Windows-1252", "Windows-1253", "Windows-1254", "Windows-1255", "Windows-1256", "Windows-1257", "Windows-1258", "Big5", "CP864", "CP874", "EUC-CN", "EUC-JP", "KOI8-R", "Macintosh", "GBK", "GB2312", "EUC-KR", "GSM0338" };
        for (String encoding : encodings) {
            TestCase.assertTrue((("Charset " + encoding) + " must be supported"), Charset.isSupported(encoding));
            Charset cs = Charset.forName(encoding);
            Set<String> aliases = cs.aliases();
            System.out.println((((cs.name()) + ": ") + aliases));
        }
    }
}

