/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests for {@link LocaleUtils}.
 */
public class LocaleUtilsTest {
    private static final Locale LOCALE_EN = new Locale("en", "");

    private static final Locale LOCALE_EN_US = new Locale("en", "US");

    private static final Locale LOCALE_EN_US_ZZZZ = new Locale("en", "US", "ZZZZ");

    private static final Locale LOCALE_FR = new Locale("fr", "");

    private static final Locale LOCALE_FR_CA = new Locale("fr", "CA");

    private static final Locale LOCALE_QQ = new Locale("qq", "");

    private static final Locale LOCALE_QQ_ZZ = new Locale("qq", "ZZ");

    // -----------------------------------------------------------------------
    /**
     * Test that constructors are public, and work, etc.
     */
    @Test
    public void testConstructor() {
        Assertions.assertNotNull(new LocaleUtils());
        final Constructor<?>[] cons = LocaleUtils.class.getDeclaredConstructors();
        Assertions.assertEquals(1, cons.length);
        Assertions.assertTrue(Modifier.isPublic(cons[0].getModifiers()));
        Assertions.assertTrue(Modifier.isPublic(LocaleUtils.class.getModifiers()));
        Assertions.assertFalse(Modifier.isFinal(LocaleUtils.class.getModifiers()));
    }

    /**
     * Test toLocale() method.
     */
    @Test
    public void testToLocale_1Part() {
        Assertions.assertNull(LocaleUtils.toLocale(null));
        LocaleUtilsTest.assertValidToLocale("us");
        LocaleUtilsTest.assertValidToLocale("fr");
        LocaleUtilsTest.assertValidToLocale("de");
        LocaleUtilsTest.assertValidToLocale("zh");
        // Valid format but lang doesn't exist, should make instance anyway
        LocaleUtilsTest.assertValidToLocale("qq");
        // LANG-941: JDK 8 introduced the empty locale as one of the default locales
        LocaleUtilsTest.assertValidToLocale("");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("Us"), "Should fail if not lowercase");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("uS"), "Should fail if not lowercase");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("u#"), "Should fail if not lowercase");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("u"), "Must be 2 chars if less than 5");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("uu_U"), "Must be 2 chars if less than 5");
    }

    /**
     * Test toLocale() method.
     */
    @Test
    public void testToLocale_2Part() {
        LocaleUtilsTest.assertValidToLocale("us_EN", "us", "EN");
        // valid though doesn't exist
        LocaleUtilsTest.assertValidToLocale("us_ZH", "us", "ZH");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("us-EN"), "Should fail as not underscore");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("us_En"), "Should fail second part not uppercase");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("us_en"), "Should fail second part not uppercase");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("us_eN"), "Should fail second part not uppercase");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("uS_EN"), "Should fail first part not lowercase");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("us_E3"), "Should fail second part not uppercase");
    }

    /**
     * Test toLocale() method.
     */
    @Test
    public void testToLocale_3Part() {
        LocaleUtilsTest.assertValidToLocale("us_EN_A", "us", "EN", "A");
        // this isn't pretty, but was caused by a jdk bug it seems
        // http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4210525
        if (SystemUtils.isJavaVersionAtLeast(JavaVersion.JAVA_1_4)) {
            LocaleUtilsTest.assertValidToLocale("us_EN_a", "us", "EN", "a");
            LocaleUtilsTest.assertValidToLocale("us_EN_SFsafdFDsdfF", "us", "EN", "SFsafdFDsdfF");
        } else {
            LocaleUtilsTest.assertValidToLocale("us_EN_a", "us", "EN", "A");
            LocaleUtilsTest.assertValidToLocale("us_EN_SFsafdFDsdfF", "us", "EN", "SFSAFDFDSDFF");
        }
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("us_EN-a"), "Should fail as not underscore");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("uu_UU_"), "Must be 3, 5 or 7+ in length");
    }

    // -----------------------------------------------------------------------
    /**
     * Test localeLookupList() method.
     */
    @Test
    public void testLocaleLookupList_Locale() {
        LocaleUtilsTest.assertLocaleLookupList(null, null, new Locale[0]);
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_QQ, null, new Locale[]{ LocaleUtilsTest.LOCALE_QQ });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN, null, new Locale[]{ LocaleUtilsTest.LOCALE_EN });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN, null, new Locale[]{ LocaleUtilsTest.LOCALE_EN });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN_US, null, new Locale[]{ LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_EN });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN_US_ZZZZ, null, new Locale[]{ LocaleUtilsTest.LOCALE_EN_US_ZZZZ, LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_EN });
    }

    /**
     * Test localeLookupList() method.
     */
    @Test
    public void testLocaleLookupList_LocaleLocale() {
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_QQ, LocaleUtilsTest.LOCALE_QQ, new Locale[]{ LocaleUtilsTest.LOCALE_QQ });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN, LocaleUtilsTest.LOCALE_EN, new Locale[]{ LocaleUtilsTest.LOCALE_EN });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_EN_US, new Locale[]{ LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_EN });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_QQ, new Locale[]{ LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_EN, LocaleUtilsTest.LOCALE_QQ });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_QQ_ZZ, new Locale[]{ LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_EN, LocaleUtilsTest.LOCALE_QQ_ZZ });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN_US_ZZZZ, null, new Locale[]{ LocaleUtilsTest.LOCALE_EN_US_ZZZZ, LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_EN });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN_US_ZZZZ, LocaleUtilsTest.LOCALE_EN_US_ZZZZ, new Locale[]{ LocaleUtilsTest.LOCALE_EN_US_ZZZZ, LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_EN });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN_US_ZZZZ, LocaleUtilsTest.LOCALE_QQ, new Locale[]{ LocaleUtilsTest.LOCALE_EN_US_ZZZZ, LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_EN, LocaleUtilsTest.LOCALE_QQ });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_EN_US_ZZZZ, LocaleUtilsTest.LOCALE_QQ_ZZ, new Locale[]{ LocaleUtilsTest.LOCALE_EN_US_ZZZZ, LocaleUtilsTest.LOCALE_EN_US, LocaleUtilsTest.LOCALE_EN, LocaleUtilsTest.LOCALE_QQ_ZZ });
        LocaleUtilsTest.assertLocaleLookupList(LocaleUtilsTest.LOCALE_FR_CA, LocaleUtilsTest.LOCALE_EN, new Locale[]{ LocaleUtilsTest.LOCALE_FR_CA, LocaleUtilsTest.LOCALE_FR, LocaleUtilsTest.LOCALE_EN });
    }

    // -----------------------------------------------------------------------
    /**
     * Test availableLocaleList() method.
     */
    @Test
    public void testAvailableLocaleList() {
        final List<Locale> list = LocaleUtils.availableLocaleList();
        final List<Locale> list2 = LocaleUtils.availableLocaleList();
        Assertions.assertNotNull(list);
        Assertions.assertSame(list, list2);
        LocaleUtilsTest.assertUnmodifiableCollection(list);
        final Locale[] jdkLocaleArray = Locale.getAvailableLocales();
        final List<Locale> jdkLocaleList = Arrays.asList(jdkLocaleArray);
        Assertions.assertEquals(jdkLocaleList, list);
    }

    // -----------------------------------------------------------------------
    /**
     * Test availableLocaleSet() method.
     */
    @Test
    public void testAvailableLocaleSet() {
        final Set<Locale> set = LocaleUtils.availableLocaleSet();
        final Set<Locale> set2 = LocaleUtils.availableLocaleSet();
        Assertions.assertNotNull(set);
        Assertions.assertSame(set, set2);
        LocaleUtilsTest.assertUnmodifiableCollection(set);
        final Locale[] jdkLocaleArray = Locale.getAvailableLocales();
        final List<Locale> jdkLocaleList = Arrays.asList(jdkLocaleArray);
        final Set<Locale> jdkLocaleSet = new HashSet<>(jdkLocaleList);
        Assertions.assertEquals(jdkLocaleSet, set);
    }

    // -----------------------------------------------------------------------
    /**
     * Test availableLocaleSet() method.
     */
    // JUnit4 does not support primitive equality testing apart from long
    @SuppressWarnings("boxing")
    @Test
    public void testIsAvailableLocale() {
        final Set<Locale> set = LocaleUtils.availableLocaleSet();
        Assertions.assertEquals(set.contains(LocaleUtilsTest.LOCALE_EN), LocaleUtils.isAvailableLocale(LocaleUtilsTest.LOCALE_EN));
        Assertions.assertEquals(set.contains(LocaleUtilsTest.LOCALE_EN_US), LocaleUtils.isAvailableLocale(LocaleUtilsTest.LOCALE_EN_US));
        Assertions.assertEquals(set.contains(LocaleUtilsTest.LOCALE_EN_US_ZZZZ), LocaleUtils.isAvailableLocale(LocaleUtilsTest.LOCALE_EN_US_ZZZZ));
        Assertions.assertEquals(set.contains(LocaleUtilsTest.LOCALE_FR), LocaleUtils.isAvailableLocale(LocaleUtilsTest.LOCALE_FR));
        Assertions.assertEquals(set.contains(LocaleUtilsTest.LOCALE_FR_CA), LocaleUtils.isAvailableLocale(LocaleUtilsTest.LOCALE_FR_CA));
        Assertions.assertEquals(set.contains(LocaleUtilsTest.LOCALE_QQ), LocaleUtils.isAvailableLocale(LocaleUtilsTest.LOCALE_QQ));
        Assertions.assertEquals(set.contains(LocaleUtilsTest.LOCALE_QQ_ZZ), LocaleUtils.isAvailableLocale(LocaleUtilsTest.LOCALE_QQ_ZZ));
    }

    /**
     * Test for 3-chars locale, further details at LANG-915
     */
    @Test
    public void testThreeCharsLocale() {
        for (final String str : Arrays.asList("udm", "tet")) {
            final Locale locale = LocaleUtils.toLocale(str);
            Assertions.assertNotNull(locale);
            Assertions.assertEquals(str, locale.getLanguage());
            Assertions.assertTrue(StringUtils.isBlank(locale.getCountry()));
            Assertions.assertEquals(new Locale(str), locale);
        }
    }

    /**
     * Test languagesByCountry() method.
     */
    @Test
    public void testLanguagesByCountry() {
        LocaleUtilsTest.assertLanguageByCountry(null, new String[0]);
        LocaleUtilsTest.assertLanguageByCountry("GB", new String[]{ "en" });
        LocaleUtilsTest.assertLanguageByCountry("ZZ", new String[0]);
        LocaleUtilsTest.assertLanguageByCountry("CH", new String[]{ "fr", "de", "it" });
    }

    /**
     * Test countriesByLanguage() method.
     */
    @Test
    public void testCountriesByLanguage() {
        LocaleUtilsTest.assertCountriesByLanguage(null, new String[0]);
        LocaleUtilsTest.assertCountriesByLanguage("de", new String[]{ "DE", "CH", "AT", "LU" });
        LocaleUtilsTest.assertCountriesByLanguage("zz", new String[0]);
        LocaleUtilsTest.assertCountriesByLanguage("it", new String[]{ "IT", "CH" });
    }

    /**
     * Tests #LANG-328 - only language+variant
     */
    @Test
    public void testLang328() {
        LocaleUtilsTest.assertValidToLocale("fr__P", "fr", "", "P");
        LocaleUtilsTest.assertValidToLocale("fr__POSIX", "fr", "", "POSIX");
    }

    @Test
    public void testLanguageAndUNM49Numeric3AreaCodeLang1312() {
        LocaleUtilsTest.assertValidToLocale("en_001", "en", "001");
        LocaleUtilsTest.assertValidToLocale("en_150", "en", "150");
        LocaleUtilsTest.assertValidToLocale("ar_001", "ar", "001");
        // LANG-1312
        LocaleUtilsTest.assertValidToLocale("en_001_GB", "en", "001", "GB");
        LocaleUtilsTest.assertValidToLocale("en_150_US", "en", "150", "US");
    }

    /**
     * Tests #LANG-865, strings starting with an underscore.
     */
    @Test
    public void testLang865() {
        LocaleUtilsTest.assertValidToLocale("_GB", "", "GB", "");
        LocaleUtilsTest.assertValidToLocale("_GB_P", "", "GB", "P");
        LocaleUtilsTest.assertValidToLocale("_GB_POSIX", "", "GB", "POSIX");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("_G"), "Must be at least 3 chars if starts with underscore");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("_Gb"), "Must be uppercase if starts with underscore");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("_gB"), "Must be uppercase if starts with underscore");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("_1B"), "Must be letter if starts with underscore");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("_G1"), "Must be letter if starts with underscore");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("_GB_"), "Must be at least 5 chars if starts with underscore");
        Assertions.assertThrows(IllegalArgumentException.class, () -> LocaleUtils.toLocale("_GBAP"), "Must have underscore after the country if starts with underscore and is at least 5 chars");
    }
}

