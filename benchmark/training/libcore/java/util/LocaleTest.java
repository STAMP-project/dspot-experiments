/**
 * Copyright (C) 2010 The Android Open Source Project
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


import java.util.Locale;
import java.util.MissingResourceException;
import junit.framework.TestCase;


public class LocaleTest extends TestCase {
    // http://b/2611311; if there's no display language/country/variant, use the raw codes.
    public void test_getDisplayName_invalid() throws Exception {
        Locale invalid = new Locale("AaBbCc", "DdEeFf", "GgHhIi");
        TestCase.assertEquals("aabbcc", invalid.getLanguage());
        TestCase.assertEquals("DDEEFF", invalid.getCountry());
        TestCase.assertEquals("GgHhIi", invalid.getVariant());
        // Android using icu4c < 49.2 returned empty strings for display language, country,
        // and variant, but a display name made up of the raw strings.
        // Newer releases return slightly different results, but no less unreasonable.
        TestCase.assertEquals("aabbcc", invalid.getDisplayLanguage());
        TestCase.assertEquals("", invalid.getDisplayCountry());
        TestCase.assertEquals("DDEEFF_GGHHII", invalid.getDisplayVariant());
        TestCase.assertEquals("aabbcc (DDEEFF,DDEEFF_GGHHII)", invalid.getDisplayName());
    }

    // http://b/2611311; if there's no display language/country/variant, use the raw codes.
    public void test_getDisplayName_unknown() throws Exception {
        Locale unknown = new Locale("xx", "YY", "Traditional");
        TestCase.assertEquals("xx", unknown.getLanguage());
        TestCase.assertEquals("YY", unknown.getCountry());
        TestCase.assertEquals("Traditional", unknown.getVariant());
        TestCase.assertEquals("xx", unknown.getDisplayLanguage());
        TestCase.assertEquals("YY", unknown.getDisplayCountry());
        TestCase.assertEquals("TRADITIONAL", unknown.getDisplayVariant());
        TestCase.assertEquals("xx (YY,TRADITIONAL)", unknown.getDisplayName());
    }

    public void test_getDisplayName_easy() throws Exception {
        TestCase.assertEquals("English", Locale.ENGLISH.getDisplayLanguage(Locale.ENGLISH));
        TestCase.assertEquals("German", Locale.GERMAN.getDisplayLanguage(Locale.ENGLISH));
        TestCase.assertEquals("Englisch", Locale.ENGLISH.getDisplayLanguage(Locale.GERMAN));
        TestCase.assertEquals("Deutsch", Locale.GERMAN.getDisplayLanguage(Locale.GERMAN));
    }

    public void test_getDisplayCountry_8870289() throws Exception {
        TestCase.assertEquals("Hong Kong", new Locale("", "HK").getDisplayCountry(Locale.US));
        TestCase.assertEquals("Macau", new Locale("", "MO").getDisplayCountry(Locale.US));
        TestCase.assertEquals("Palestine", new Locale("", "PS").getDisplayCountry(Locale.US));
        TestCase.assertEquals("Cocos [Keeling] Islands", new Locale("", "CC").getDisplayCountry(Locale.US));
        TestCase.assertEquals("Congo [DRC]", new Locale("", "CD").getDisplayCountry(Locale.US));
        TestCase.assertEquals("Congo [Republic]", new Locale("", "CG").getDisplayCountry(Locale.US));
        TestCase.assertEquals("Falkland Islands [Islas Malvinas]", new Locale("", "FK").getDisplayCountry(Locale.US));
        TestCase.assertEquals("Macedonia [FYROM]", new Locale("", "MK").getDisplayCountry(Locale.US));
        TestCase.assertEquals("Myanmar [Burma]", new Locale("", "MM").getDisplayCountry(Locale.US));
        TestCase.assertEquals("Taiwan", new Locale("", "TW").getDisplayCountry(Locale.US));
    }

    public void test_tl() throws Exception {
        // In jb-mr1, we had a last-minute hack to always return "Filipino" because
        // icu4c 4.8 didn't have any localizations for fil. (http://b/7291355)
        Locale tl = new Locale("tl");
        Locale tl_PH = new Locale("tl", "PH");
        TestCase.assertEquals("Filipino", tl.getDisplayLanguage(Locale.ENGLISH));
        TestCase.assertEquals("Filipino", tl_PH.getDisplayLanguage(Locale.ENGLISH));
        TestCase.assertEquals("Filipino", tl.getDisplayLanguage(tl));
        TestCase.assertEquals("Filipino", tl_PH.getDisplayLanguage(tl_PH));
        // After the icu4c 4.9 upgrade, we could localize "fil" correctly, though we
        // needed another hack to supply "fil" instead of "tl" to icu4c. (http://b/8023288)
        Locale es_MX = new Locale("es", "MX");
        TestCase.assertEquals("filipino", tl.getDisplayLanguage(es_MX));
        TestCase.assertEquals("filipino", tl_PH.getDisplayLanguage(es_MX));
    }

    // http://b/3452611; Locale.getDisplayLanguage fails for the obsolete language codes.
    public void test_getDisplayName_obsolete() throws Exception {
        // he (new) -> iw (obsolete)
        LocaleTest.assertObsolete("he", "iw", "?????");
        // id (new) -> in (obsolete)
        LocaleTest.assertObsolete("id", "in", "Bahasa Indonesia");
    }

    public void test_getISO3Country() {
        // Empty country code.
        TestCase.assertEquals("", new Locale("en", "").getISO3Country());
        // Invalid country code.
        try {
            TestCase.assertEquals("", new Locale("en", "XX").getISO3Country());
            TestCase.fail();
        } catch (MissingResourceException expected) {
            TestCase.assertEquals("FormatData_en_XX", expected.getClassName());
            TestCase.assertEquals("ShortCountry", expected.getKey());
        }
        // Valid country code.
        TestCase.assertEquals("CAN", new Locale("", "CA").getISO3Country());
        TestCase.assertEquals("CAN", new Locale("en", "CA").getISO3Country());
        TestCase.assertEquals("CAN", new Locale("xx", "CA").getISO3Country());
    }

    public void test_getISO3Language() {
        // Empty language code.
        TestCase.assertEquals("", new Locale("", "US").getISO3Language());
        // Invalid language code.
        try {
            TestCase.assertEquals("", new Locale("xx", "US").getISO3Language());
            TestCase.fail();
        } catch (MissingResourceException expected) {
            TestCase.assertEquals("FormatData_xx_US", expected.getClassName());
            TestCase.assertEquals("ShortLanguage", expected.getKey());
        }
        // Valid language code.
        TestCase.assertEquals("eng", new Locale("en", "").getISO3Language());
        TestCase.assertEquals("eng", new Locale("en", "CA").getISO3Language());
        TestCase.assertEquals("eng", new Locale("en", "XX").getISO3Language());
    }
}

