/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package tests.api.java.util;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Currency;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import junit.framework.TestCase;


public class CurrencyTest extends TestCase {
    private static Locale defaultLocale = Locale.getDefault();

    /**
     * java.util.Currency#getInstance(java.lang.String)
     */
    public void test_getInstanceLjava_lang_String() {
        // see test_getInstanceLjava_util_Locale() tests
    }

    /**
     * java.util.Currency#getInstance(java.util.Locale)
     */
    public void test_getInstanceLjava_util_Locale() {
        /* the behaviour in all these three cases should be the same since this
        method ignores language and variant component of the locale.
         */
        Currency c0 = Currency.getInstance("CAD");
        Currency c1 = Currency.getInstance(new Locale("en", "CA"));
        TestCase.assertTrue("Currency.getInstance(new Locale(\"en\",\"CA\")) isn\'t equal to Currency.getInstance(\"CAD\")", (c1 == c0));
        Currency c2 = Currency.getInstance(new Locale("fr", "CA"));
        TestCase.assertTrue("Currency.getInstance(new Locale(\"fr\",\"CA\")) isn\'t equal to Currency.getInstance(\"CAD\")", (c2 == c0));
        Currency c3 = Currency.getInstance(new Locale("", "CA"));
        TestCase.assertTrue("Currency.getInstance(new Locale(\"\",\"CA\")) isn\'t equal to Currency.getInstance(\"CAD\")", (c3 == c0));
        c0 = Currency.getInstance("JPY");
        c1 = Currency.getInstance(new Locale("ja", "JP"));
        TestCase.assertTrue("Currency.getInstance(new Locale(\"ja\",\"JP\")) isn\'t equal to Currency.getInstance(\"JPY\")", (c1 == c0));
        c2 = Currency.getInstance(new Locale("", "JP"));
        TestCase.assertTrue("Currency.getInstance(new Locale(\"\",\"JP\")) isn\'t equal to Currency.getInstance(\"JPY\")", (c2 == c0));
        c3 = Currency.getInstance(new Locale("bogus", "JP"));
        TestCase.assertTrue("Currency.getInstance(new Locale(\"bogus\",\"JP\")) isn\'t equal to Currency.getInstance(\"JPY\")", (c3 == c0));
        Locale localeGu = new Locale("gu", "IN");
        Currency cGu = Currency.getInstance(localeGu);
        Locale localeKn = new Locale("kn", "IN");
        Currency cKn = Currency.getInstance(localeKn);
        TestCase.assertTrue((((((("Currency.getInstance(Locale_" + (localeGu.toString())) + "))") + "isn't equal to ") + "Currency.getInstance(Locale_") + (localeKn.toString())) + "))"), (cGu == cKn));
        // some teritories do not have currencies, like Antarctica
        Locale loc = new Locale("", "AQ");
        try {
            Currency curr = Currency.getInstance(loc);
            TestCase.assertNull("Currency.getInstance(new Locale(\"\", \"AQ\")) did not return null", curr);
        } catch (IllegalArgumentException e) {
            TestCase.fail(("Unexpected IllegalArgumentException " + e));
        }
        // unsupported/legacy iso3 countries
        loc = new Locale("", "ZR");
        try {
            Currency curr = Currency.getInstance(loc);
            TestCase.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        loc = new Locale("", "ZAR");
        try {
            Currency curr = Currency.getInstance(loc);
            TestCase.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        loc = new Locale("", "FX");
        try {
            Currency curr = Currency.getInstance(loc);
            TestCase.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
        loc = new Locale("", "FXX");
        try {
            Currency curr = Currency.getInstance(loc);
            TestCase.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
        }
    }

    /**
     * java.util.Currency#getSymbol()
     */
    public void test_getSymbol() {
        Currency currK = Currency.getInstance("KRW");
        Currency currI = Currency.getInstance("IEP");
        Currency currUS = Currency.getInstance("USD");
        Locale.setDefault(Locale.US);
        // BEGIN android-changed
        // KRW currency symbol is \u20a9 since CLDR1.7 release.
        TestCase.assertEquals("currK.getSymbol()", "\u20a9", currK.getSymbol());
        // IEP currency symbol is IEP since CLDR2.0 release.
        TestCase.assertEquals("currI.getSymbol()", "IEP", currI.getSymbol());
        // END android-changed
        TestCase.assertEquals("currUS.getSymbol()", "$", currUS.getSymbol());
        Locale.setDefault(new Locale("en", "IE"));
        // BEGIN android-changed
        TestCase.assertEquals("currK.getSymbol()", "\u20a9", currK.getSymbol());
        TestCase.assertEquals("currI.getSymbol()", "IEP", currI.getSymbol());
        TestCase.assertEquals("currUS.getSymbol()", "$", currUS.getSymbol());
        // END android-changed
        // test what happens if this is an invalid locale,
        // one with Korean country but an India language
        Locale.setDefault(new Locale("kr", "KR"));
        // BEGIN android-changed
        TestCase.assertEquals("currK.getSymbol()", "\u20a9", currK.getSymbol());
        TestCase.assertEquals("currI.getSymbol()", "IEP", currI.getSymbol());
        // END android-changed
        TestCase.assertEquals("currUS.getSymbol()", "$", currUS.getSymbol());
    }

    /**
     * java.util.Currency#getSymbol(java.util.Locale)
     */
    public void test_getSymbolLjava_util_Locale() {
        // Tests was simplified because java specification not
        // includes strong requirements for returnig symbol.
        // on android platform used wrong character for yen
        // sign: \u00a5 instead of \uffe5
        Locale[] desiredLocales = new Locale[]{ Locale.JAPAN, Locale.JAPANESE, Locale.FRANCE, Locale.FRENCH, Locale.US, Locale.UK, Locale.CANADA, Locale.CANADA_FRENCH, Locale.ENGLISH, new Locale("ja", "JP"), new Locale("", "JP"), new Locale("fr", "FR"), new Locale("", "FR"), new Locale("en", "US"), new Locale("", "US"), new Locale("es", "US"), new Locale("ar", "US"), new Locale("ja", "US"), new Locale("en", "CA"), new Locale("fr", "CA"), new Locale("", "CA"), new Locale("ar", "CA"), new Locale("ja", "JP"), new Locale("", "JP"), new Locale("ar", "JP"), new Locale("ja", "AE"), new Locale("en", "AE"), new Locale("ar", "AE"), new Locale("da", "DK"), new Locale("", "DK"), new Locale("da", ""), new Locale("ja", ""), new Locale("en", "") };
        Set<Locale> availableLocales = new HashSet<Locale>(Arrays.asList(Locale.getAvailableLocales()));
        ArrayList<Locale> locales = new ArrayList<Locale>();
        for (Locale desiredLocale : desiredLocales) {
            if (availableLocales.contains(desiredLocale)) {
                locales.add(desiredLocale);
            }
        }
        Locale[] loc1 = locales.toArray(new Locale[locales.size()]);
        String[] euro = new String[]{ "EUR", "\u20ac" };
        // \u00a5 and \uffe5 are actually the same symbol, just different code points.
        // But the RI returns the \uffe5 and Android returns those with \u00a5
        String[] yen = new String[]{ "JPY", "\u00a5", "\u00a5JP", "JP\u00a5", "\uffe5", "\uffe5JP", "JP\uffe5" };
        String[] dollar = new String[]{ "USD", "$", "US$", "$US" };
        // BEGIN android-changed
        // Starting CLDR 1.7 release, currency symbol for CAD changed to CA$ in some locales such as ja.
        String[] cDollar = new String[]{ "CA$", "CAD", "$", "Can$", "$CA" };
        // END android-changed
        Currency currE = Currency.getInstance("EUR");
        Currency currJ = Currency.getInstance("JPY");
        Currency currUS = Currency.getInstance("USD");
        Currency currCA = Currency.getInstance("CAD");
        int i;
        int j;
        int k;
        boolean flag;
        for (k = 0; k < (loc1.length); k++) {
            Locale.setDefault(loc1[k]);
            for (i = 0; i < (loc1.length); i++) {
                flag = false;
                for (j = 0; j < (euro.length); j++) {
                    if (currE.getSymbol(loc1[i]).equals(euro[j])) {
                        flag = true;
                        break;
                    }
                }
                TestCase.assertTrue(((((((("Default Locale is: " + (Locale.getDefault())) + ". For locale ") + (loc1[i])) + " the Euro currency returned ") + (currE.getSymbol(loc1[i]))) + ". Expected was one of these: ") + (Arrays.toString(euro))), flag);
            }
            for (i = 0; i < (loc1.length); i++) {
                flag = false;
                for (j = 0; j < (yen.length); j++) {
                    byte[] b1 = null;
                    byte[] b2 = null;
                    if (currJ.getSymbol(loc1[i]).equals(yen[j])) {
                        flag = true;
                        break;
                    }
                }
                TestCase.assertTrue(((((((("Default Locale is: " + (Locale.getDefault())) + ". For locale ") + (loc1[i])) + " the Yen currency returned ") + (currJ.getSymbol(loc1[i]))) + ". Expected was one of these: ") + (Arrays.toString(yen))), flag);
            }
            for (i = 0; i < (loc1.length); i++) {
                flag = false;
                for (j = 0; j < (dollar.length); j++) {
                    if (currUS.getSymbol(loc1[i]).equals(dollar[j])) {
                        flag = true;
                        break;
                    }
                }
                TestCase.assertTrue(((((((("Default Locale is: " + (Locale.getDefault())) + ". For locale ") + (loc1[i])) + " the Dollar currency returned ") + (currUS.getSymbol(loc1[i]))) + ". Expected was one of these: ") + (Arrays.toString(dollar))), flag);
            }
            for (i = 0; i < (loc1.length); i++) {
                flag = false;
                for (j = 0; j < (cDollar.length); j++) {
                    if (currCA.getSymbol(loc1[i]).equals(cDollar[j])) {
                        flag = true;
                        break;
                    }
                }
                TestCase.assertTrue(((((((("Default Locale is: " + (Locale.getDefault())) + ". For locale ") + (loc1[i])) + " the Canadian Dollar currency returned ") + (currCA.getSymbol(loc1[i]))) + ". Expected was one of these: ") + (Arrays.toString(cDollar))), flag);
            }
        }
    }

    /**
     * java.util.Currency#getDefaultFractionDigits()
     */
    public void test_getDefaultFractionDigits() {
        Currency c1 = Currency.getInstance("TND");
        c1.getDefaultFractionDigits();
        TestCase.assertEquals(((" Currency.getInstance(\"" + c1) + "\") returned incorrect number of digits. "), 3, c1.getDefaultFractionDigits());
        Currency c2 = Currency.getInstance("EUR");
        c2.getDefaultFractionDigits();
        TestCase.assertEquals(((" Currency.getInstance(\"" + c2) + "\") returned incorrect number of digits. "), 2, c2.getDefaultFractionDigits());
        Currency c3 = Currency.getInstance("JPY");
        c3.getDefaultFractionDigits();
        TestCase.assertEquals(((" Currency.getInstance(\"" + c3) + "\") returned incorrect number of digits. "), 0, c3.getDefaultFractionDigits());
        Currency c4 = Currency.getInstance("XXX");
        c4.getDefaultFractionDigits();
        TestCase.assertEquals(((" Currency.getInstance(\"" + c4) + "\") returned incorrect number of digits. "), (-1), c4.getDefaultFractionDigits());
    }

    /**
     * java.util.Currency#getCurrencyCode() Note: lines under remarks
     *        (Locale.CHINESE, Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN,
     *        Locale.ITALIAN, Locale.JAPANESE, Locale.KOREAN) raises exception
     *        on SUN VM
     */
    public void test_getCurrencyCode() {
        final Collection<Locale> locVal = // Locale.CHINESE,
        // Locale.ENGLISH,
        // Locale.FRENCH,
        // Locale.GERMAN,
        // Locale.ITALIAN,
        // Locale.JAPANESE,
        // Locale.KOREAN,
        Arrays.asList(Locale.CANADA, Locale.CANADA_FRENCH, Locale.CHINA, Locale.FRANCE, Locale.GERMANY, Locale.ITALY, Locale.JAPAN, Locale.KOREA, Locale.PRC, Locale.SIMPLIFIED_CHINESE, Locale.TAIWAN, Locale.TRADITIONAL_CHINESE, Locale.UK, Locale.US);
        final Collection<String> locDat = Arrays.asList("CAD", "CAD", "CNY", "EUR", "EUR", "EUR", "JPY", "KRW", "CNY", "CNY", "TWD", "TWD", "GBP", "USD");
        Iterator<String> dat = locDat.iterator();
        for (Locale l : locVal) {
            String d = dat.next().trim();
            TestCase.assertEquals((("For locale " + l) + " currency code wrong"), Currency.getInstance(l).getCurrencyCode(), d);
        }
    }

    /**
     * java.util.Currency#toString() Note: lines under remarks
     *        (Locale.CHINESE, Locale.ENGLISH, Locale.FRENCH, Locale.GERMAN,
     *        Locale.ITALIAN, Locale.JAPANESE, Locale.KOREAN) raises exception
     *        on SUN VM
     */
    public void test_toString() {
        final Collection<Locale> locVal = // Locale.CHINESE,
        // Locale.ENGLISH,
        // Locale.FRENCH,
        // Locale.GERMAN,
        // Locale.ITALIAN,
        // Locale.JAPANESE,
        // Locale.KOREAN,
        Arrays.asList(Locale.CANADA, Locale.CANADA_FRENCH, Locale.CHINA, Locale.FRANCE, Locale.GERMANY, Locale.ITALY, Locale.JAPAN, Locale.KOREA, Locale.PRC, Locale.SIMPLIFIED_CHINESE, Locale.TAIWAN, Locale.TRADITIONAL_CHINESE, Locale.UK, Locale.US);
        final Collection<String> locDat = Arrays.asList("CAD", "CAD", "CNY", "EUR", "EUR", "EUR", "JPY", "KRW", "CNY", "CNY", "TWD", "TWD", "GBP", "USD");
        Iterator<String> dat = locDat.iterator();
        for (Locale l : locVal) {
            String d = dat.next().trim();
            TestCase.assertEquals((("For locale " + l) + " Currency.toString method returns wrong value"), Currency.getInstance(l).toString(), d);
        }
    }
}

