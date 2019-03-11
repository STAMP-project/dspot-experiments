/**
 * Copyright (C) 2009 The Android Open Source Project
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


import java.util.Arrays;
import java.util.Locale;
import junit.framework.TestCase;


public class ICUTest extends TestCase {
    public void test_getISOLanguages() throws Exception {
        // Check that corrupting our array doesn't affect other callers.
        TestCase.assertNotNull(ICU.getISOLanguages()[0]);
        ICU.getISOLanguages()[0] = null;
        TestCase.assertNotNull(ICU.getISOLanguages()[0]);
    }

    public void test_getISOCountries() throws Exception {
        // Check that corrupting our array doesn't affect other callers.
        TestCase.assertNotNull(ICU.getISOCountries()[0]);
        ICU.getISOCountries()[0] = null;
        TestCase.assertNotNull(ICU.getISOCountries()[0]);
    }

    public void test_getAvailableLocales() throws Exception {
        // Check that corrupting our array doesn't affect other callers.
        TestCase.assertNotNull(ICU.getAvailableLocales()[0]);
        ICU.getAvailableLocales()[0] = null;
        TestCase.assertNotNull(ICU.getAvailableLocales()[0]);
    }

    public void test_getBestDateTimePattern() throws Exception {
        TestCase.assertEquals("d MMMM", ICU.getBestDateTimePattern("MMMMd", "ca_ES"));
        TestCase.assertEquals("d 'de' MMMM", ICU.getBestDateTimePattern("MMMMd", "es_ES"));
        TestCase.assertEquals("d. MMMM", ICU.getBestDateTimePattern("MMMMd", "de_CH"));
        TestCase.assertEquals("MMMM d", ICU.getBestDateTimePattern("MMMMd", "en_US"));
        TestCase.assertEquals("d LLLL", ICU.getBestDateTimePattern("MMMMd", "fa_IR"));
        TestCase.assertEquals("M?d?", ICU.getBestDateTimePattern("MMMMd", "ja_JP"));
    }

    public void test_localeFromString() throws Exception {
        // localeFromString is pretty lenient. Some of these can't be round-tripped
        // through Locale.toString.
        TestCase.assertEquals(Locale.ENGLISH, ICU.localeFromString("en"));
        TestCase.assertEquals(Locale.ENGLISH, ICU.localeFromString("en_"));
        TestCase.assertEquals(Locale.ENGLISH, ICU.localeFromString("en__"));
        TestCase.assertEquals(Locale.US, ICU.localeFromString("en_US"));
        TestCase.assertEquals(Locale.US, ICU.localeFromString("en_US_"));
        TestCase.assertEquals(new Locale("", "US", ""), ICU.localeFromString("_US"));
        TestCase.assertEquals(new Locale("", "US", ""), ICU.localeFromString("_US_"));
        TestCase.assertEquals(new Locale("", "", "POSIX"), ICU.localeFromString("__POSIX"));
        TestCase.assertEquals(new Locale("aa", "BB", "CC"), ICU.localeFromString("aa_BB_CC"));
    }

    public void test_getScript_addLikelySubtags() throws Exception {
        TestCase.assertEquals("Latn", ICU.getScript(ICU.addLikelySubtags("en_US")));
        TestCase.assertEquals("Hebr", ICU.getScript(ICU.addLikelySubtags("he")));
        TestCase.assertEquals("Hebr", ICU.getScript(ICU.addLikelySubtags("he_IL")));
        TestCase.assertEquals("Hebr", ICU.getScript(ICU.addLikelySubtags("iw")));
        TestCase.assertEquals("Hebr", ICU.getScript(ICU.addLikelySubtags("iw_IL")));
    }

    public void test_getDateFormatOrder() throws Exception {
        // lv and fa use differing orders depending on whether you're using numeric or textual months.
        Locale lv = new Locale("lv");
        TestCase.assertEquals("[d, M, y]", Arrays.toString(ICU.getDateFormatOrder(best(lv, "yyyy-M-dd"))));
        TestCase.assertEquals("[y, d, M]", Arrays.toString(ICU.getDateFormatOrder(best(lv, "yyyy-MMM-dd"))));
        TestCase.assertEquals("[d, M, \u0000]", Arrays.toString(ICU.getDateFormatOrder(best(lv, "MMM-dd"))));
        Locale fa = new Locale("fa");
        TestCase.assertEquals("[y, M, d]", Arrays.toString(ICU.getDateFormatOrder(best(fa, "yyyy-M-dd"))));
        TestCase.assertEquals("[d, M, y]", Arrays.toString(ICU.getDateFormatOrder(best(fa, "yyyy-MMM-dd"))));
        TestCase.assertEquals("[d, M, \u0000]", Arrays.toString(ICU.getDateFormatOrder(best(fa, "MMM-dd"))));
        // English differs on each side of the Atlantic.
        Locale en_US = Locale.US;
        TestCase.assertEquals("[M, d, y]", Arrays.toString(ICU.getDateFormatOrder(best(en_US, "yyyy-M-dd"))));
        TestCase.assertEquals("[M, d, y]", Arrays.toString(ICU.getDateFormatOrder(best(en_US, "yyyy-MMM-dd"))));
        TestCase.assertEquals("[M, d, \u0000]", Arrays.toString(ICU.getDateFormatOrder(best(en_US, "MMM-dd"))));
        Locale en_GB = Locale.UK;
        TestCase.assertEquals("[d, M, y]", Arrays.toString(ICU.getDateFormatOrder(best(en_GB, "yyyy-M-dd"))));
        TestCase.assertEquals("[d, M, y]", Arrays.toString(ICU.getDateFormatOrder(best(en_GB, "yyyy-MMM-dd"))));
        TestCase.assertEquals("[d, M, \u0000]", Arrays.toString(ICU.getDateFormatOrder(best(en_GB, "MMM-dd"))));
        TestCase.assertEquals("[y, M, d]", Arrays.toString(ICU.getDateFormatOrder("yyyy - 'why' '' 'ddd' MMM-dd")));
        try {
            ICU.getDateFormatOrder("the quick brown fox jumped over the lazy dog");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ICU.getDateFormatOrder("'");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ICU.getDateFormatOrder("yyyy'");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
        try {
            ICU.getDateFormatOrder("yyyy'MMM");
            TestCase.fail();
        } catch (IllegalArgumentException expected) {
        }
    }
}

