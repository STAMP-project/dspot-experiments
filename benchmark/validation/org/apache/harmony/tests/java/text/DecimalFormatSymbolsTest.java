/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.harmony.tests.java.text;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;
import junit.framework.TestCase;
import org.apache.harmony.testframework.serialization.SerializationTest;


public class DecimalFormatSymbolsTest extends TestCase {
    DecimalFormatSymbols dfs;

    DecimalFormatSymbols dfsUS;

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#DecimalFormatSymbols()
     */
    public void test_Constructor() {
        // Test for method java.text.DecimalFormatSymbols()
        // Used in tests
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#DecimalFormatSymbols(java.util.Locale)
     */
    public void test_ConstructorLjava_util_Locale() {
        DecimalFormatSymbols dfs = new DecimalFormatSymbols(new Locale("en", "us"));
        TestCase.assertEquals("Returned incorrect symbols", '%', dfs.getPercent());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getAvailableLocales()
     */
    public void test_getAvailableLocales_no_provider() throws Exception {
        Locale[] locales = DecimalFormatSymbols.getAvailableLocales();
        TestCase.assertNotNull(locales);
        // must contain Locale.US
        boolean flag = false;
        for (Locale locale : locales) {
            if (locale.equals(Locale.US)) {
                flag = true;
                break;
            }
        }
        TestCase.assertTrue(flag);
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getInstance()
     */
    public void test_getInstance() {
        TestCase.assertEquals(new DecimalFormatSymbols(), DecimalFormatSymbols.getInstance());
        TestCase.assertEquals(new DecimalFormatSymbols(Locale.getDefault()), DecimalFormatSymbols.getInstance());
        TestCase.assertNotSame(DecimalFormatSymbols.getInstance(), DecimalFormatSymbols.getInstance());
    }

    public void test_getInstanceLjava_util_Locale() {
        try {
            DecimalFormatSymbols.getInstance(null);
            TestCase.fail();
        } catch (NullPointerException expected) {
        }
        TestCase.assertEquals(new DecimalFormatSymbols(Locale.GERMANY), DecimalFormatSymbols.getInstance(Locale.GERMANY));
        Locale locale = new Locale("not exist language", "not exist country");
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
        TestCase.assertNotNull(symbols);
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#equals(java.lang.Object)
     */
    public void test_equalsLjava_lang_Object() {
        TestCase.assertTrue("Equal objects returned false", dfs.equals(dfs.clone()));
        dfs.setDigit('B');
        TestCase.assertTrue("Un-Equal objects returned true", (!(dfs.equals(new DecimalFormatSymbols()))));
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getCurrency()
     */
    public void test_getCurrency() {
        Currency currency = Currency.getInstance("USD");
        TestCase.assertEquals("Returned incorrect currency", dfsUS.getCurrency(), currency);
        Currency currK = Currency.getInstance("KRW");
        Currency currX = Currency.getInstance("XXX");
        Currency currE = Currency.getInstance("EUR");
        // Currency currF = Currency.getInstance("FRF");
        DecimalFormatSymbols dfs1 = new DecimalFormatSymbols(new Locale("ko", "KR"));
        TestCase.assertTrue("Test1: Returned incorrect currency", ((dfs1.getCurrency()) == currK));
        TestCase.assertEquals("Test1: Returned incorrect currencySymbol", "\u20a9", dfs1.getCurrencySymbol());
        TestCase.assertEquals("Test1: Returned incorrect intlCurrencySymbol", "KRW", dfs1.getInternationalCurrencySymbol());
        dfs1 = new DecimalFormatSymbols(new Locale("", "KR"));
        TestCase.assertTrue("Test2: Returned incorrect currency", ((dfs1.getCurrency()) == currK));
        TestCase.assertEquals("Test2: Returned incorrect currencySymbol", "\u20a9", dfs1.getCurrencySymbol());
        TestCase.assertEquals("Test2: Returned incorrect intlCurrencySymbol", "KRW", dfs1.getInternationalCurrencySymbol());
        dfs1 = new DecimalFormatSymbols(new Locale("ko", ""));
        TestCase.assertTrue("Test3: Returned incorrect currency", ((dfs1.getCurrency()) == currX));
        TestCase.assertEquals("Test3: Returned incorrect currencySymbol", "\u00a4", dfs1.getCurrencySymbol());
        TestCase.assertEquals("Test3: Returned incorrect intlCurrencySymbol", "XXX", dfs1.getInternationalCurrencySymbol());
        dfs1 = new DecimalFormatSymbols(new Locale("fr", "FR"));
        TestCase.assertTrue("Test4: Returned incorrect currency", ((dfs1.getCurrency()) == currE));
        TestCase.assertEquals("Test4: Returned incorrect currencySymbol", "\u20ac", dfs1.getCurrencySymbol());
        TestCase.assertEquals("Test4: Returned incorrect intlCurrencySymbol", "EUR", dfs1.getInternationalCurrencySymbol());
        // RI fails these tests since it doesn't have the PREEURO variant
        // dfs1 = new DecimalFormatSymbols(new Locale("fr", "FR","PREEURO"));
        // assertTrue("Test5: Returned incorrect currency", dfs1.getCurrency()
        // == currF);
        // assertTrue("Test5: Returned incorrect currencySymbol",
        // dfs1.getCurrencySymbol().equals("F"));
        // assertTrue("Test5: Returned incorrect intlCurrencySymbol",
        // dfs1.getInternationalCurrencySymbol().equals("FRF"));
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getCurrencySymbol()
     */
    public void test_getCurrencySymbol() {
        TestCase.assertEquals("Returned incorrect currencySymbol", "$", dfsUS.getCurrencySymbol());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getDecimalSeparator()
     */
    public void test_getDecimalSeparator() {
        dfs.setDecimalSeparator('*');
        TestCase.assertEquals("Returned incorrect DecimalSeparator symbol", '*', dfs.getDecimalSeparator());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getDigit()
     */
    public void test_getDigit() {
        dfs.setDigit('*');
        TestCase.assertEquals("Returned incorrect Digit symbol", '*', dfs.getDigit());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getExponentSeparator()
     */
    public void test_getExponentSeparator() {
        dfs.setExponentSeparator("EE");
        TestCase.assertEquals("Returned incorrect Exponent Separator symbol", "EE", dfs.getExponentSeparator());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getGroupingSeparator()
     */
    public void test_getGroupingSeparator() {
        dfs.setGroupingSeparator('*');
        TestCase.assertEquals("Returned incorrect GroupingSeparator symbol", '*', dfs.getGroupingSeparator());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getInfinity()
     */
    public void test_getInfinity() {
        dfs.setInfinity("&");
        TestCase.assertTrue("Returned incorrect Infinity symbol", ((dfs.getInfinity()) == "&"));
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getInternationalCurrencySymbol()
     */
    public void test_getInternationalCurrencySymbol() {
        TestCase.assertEquals("Returned incorrect InternationalCurrencySymbol", "USD", dfsUS.getInternationalCurrencySymbol());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getMinusSign()
     */
    public void test_getMinusSign() {
        dfs.setMinusSign('&');
        TestCase.assertEquals("Returned incorrect MinusSign symbol", '&', dfs.getMinusSign());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getNaN()
     */
    public void test_getNaN() {
        dfs.setNaN("NAN!!");
        TestCase.assertEquals("Returned incorrect nan symbol", "NAN!!", dfs.getNaN());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getPatternSeparator()
     */
    public void test_getPatternSeparator() {
        dfs.setPatternSeparator('X');
        TestCase.assertEquals("Returned incorrect PatternSeparator symbol", 'X', dfs.getPatternSeparator());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getPercent()
     */
    public void test_getPercent() {
        dfs.setPercent('*');
        TestCase.assertEquals("Returned incorrect Percent symbol", '*', dfs.getPercent());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getPerMill()
     */
    public void test_getPerMill() {
        dfs.setPerMill('#');
        TestCase.assertEquals("Returned incorrect PerMill symbol", '#', dfs.getPerMill());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#getZeroDigit()
     */
    public void test_getZeroDigit() {
        dfs.setZeroDigit('*');
        TestCase.assertEquals("Returned incorrect ZeroDigit symbol", '*', dfs.getZeroDigit());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setCurrency(java.util.Currency)
     */
    public void test_setCurrencyLjava_util_Currency() {
        Locale locale = Locale.CANADA;
        DecimalFormatSymbols dfs = ((DecimalFormat) (NumberFormat.getCurrencyInstance(locale))).getDecimalFormatSymbols();
        try {
            dfs.setCurrency(null);
            TestCase.fail("Expected NullPointerException");
        } catch (NullPointerException e) {
        }
        Currency currency = Currency.getInstance("JPY");
        dfs.setCurrency(currency);
        TestCase.assertTrue("Returned incorrect currency", (currency == (dfs.getCurrency())));
        TestCase.assertEquals("Returned incorrect currency symbol", currency.getSymbol(locale), dfs.getCurrencySymbol());
        TestCase.assertTrue("Returned incorrect international currency symbol", currency.getCurrencyCode().equals(dfs.getInternationalCurrencySymbol()));
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setDecimalSeparator(char)
     */
    public void test_setDecimalSeparatorC() {
        dfs.setDecimalSeparator('*');
        TestCase.assertEquals("Returned incorrect DecimalSeparator symbol", '*', dfs.getDecimalSeparator());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setDigit(char)
     */
    public void test_setDigitC() {
        dfs.setDigit('*');
        TestCase.assertEquals("Returned incorrect Digit symbol", '*', dfs.getDigit());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setExponentSeparator(String)
     */
    public void test_setExponentSeparator() {
        try {
            dfs.setExponentSeparator(null);
            TestCase.fail("Should throw NullPointerException");
        } catch (NullPointerException e) {
            // expected
        }
        dfs.setExponentSeparator("");
        TestCase.assertEquals("Returned incorrect Exponent Separator symbol", "", dfs.getExponentSeparator());
        dfs.setExponentSeparator("what ever you want");
        TestCase.assertEquals("Returned incorrect Exponent Separator symbol", "what ever you want", dfs.getExponentSeparator());
        dfs.setExponentSeparator(" E ");
        TestCase.assertEquals("Returned incorrect Exponent Separator symbol", " E ", dfs.getExponentSeparator());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setGroupingSeparator(char)
     */
    public void test_setGroupingSeparatorC() {
        dfs.setGroupingSeparator('*');
        TestCase.assertEquals("Returned incorrect GroupingSeparator symbol", '*', dfs.getGroupingSeparator());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setInfinity(java.lang.String)
     */
    public void test_setInfinityLjava_lang_String() {
        dfs.setInfinity("&");
        TestCase.assertTrue("Returned incorrect Infinity symbol", ((dfs.getInfinity()) == "&"));
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setInternationalCurrencySymbol(java.lang.String)
     */
    public void test_setInternationalCurrencySymbolLjava_lang_String() {
        Locale locale = Locale.CANADA;
        DecimalFormatSymbols dfs = ((DecimalFormat) (NumberFormat.getCurrencyInstance(locale))).getDecimalFormatSymbols();
        Currency currency = Currency.getInstance("JPY");
        dfs.setInternationalCurrencySymbol(currency.getCurrencyCode());
        TestCase.assertTrue("Test1: Returned incorrect currency", (currency == (dfs.getCurrency())));
        TestCase.assertEquals("Test1: Returned incorrect currency symbol", currency.getSymbol(locale), dfs.getCurrencySymbol());
        TestCase.assertTrue("Test1: Returned incorrect international currency symbol", currency.getCurrencyCode().equals(dfs.getInternationalCurrencySymbol()));
        dfs.setInternationalCurrencySymbol("bogus");
        // RI support this legacy country code
        // assertNotNull("Test2: Returned incorrect currency", dfs.getCurrency());
        TestCase.assertEquals("Test2: Returned incorrect international currency symbol", "bogus", dfs.getInternationalCurrencySymbol());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setMinusSign(char)
     */
    public void test_setMinusSignC() {
        dfs.setMinusSign('&');
        TestCase.assertEquals("Returned incorrect MinusSign symbol", '&', dfs.getMinusSign());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setNaN(java.lang.String)
     */
    public void test_setNaNLjava_lang_String() {
        dfs.setNaN("NAN!!");
        TestCase.assertEquals("Returned incorrect nan symbol", "NAN!!", dfs.getNaN());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setPatternSeparator(char)
     */
    public void test_setPatternSeparatorC() {
        dfs.setPatternSeparator('X');
        TestCase.assertEquals("Returned incorrect PatternSeparator symbol", 'X', dfs.getPatternSeparator());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setPercent(char)
     */
    public void test_setPercentC() {
        dfs.setPercent('*');
        TestCase.assertEquals("Returned incorrect Percent symbol", '*', dfs.getPercent());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setPerMill(char)
     */
    public void test_setPerMillC() {
        dfs.setPerMill('#');
        TestCase.assertEquals("Returned incorrect PerMill symbol", '#', dfs.getPerMill());
    }

    /**
     *
     *
     * @unknown java.text.DecimalFormatSymbols#setZeroDigit(char)
     */
    public void test_setZeroDigitC() {
        dfs.setZeroDigit('*');
        TestCase.assertEquals("Set incorrect ZeroDigit symbol", '*', dfs.getZeroDigit());
    }

    // Test serialization mechanism of DecimalFormatSymbols
    public void test_serialization() throws Exception {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        Currency currency = symbols.getCurrency();
        TestCase.assertNotNull(currency);
        // serialize
        ByteArrayOutputStream byteOStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOStream = new ObjectOutputStream(byteOStream);
        objectOStream.writeObject(symbols);
        // and deserialize
        ObjectInputStream objectIStream = new ObjectInputStream(new ByteArrayInputStream(byteOStream.toByteArray()));
        DecimalFormatSymbols symbolsD = ((DecimalFormatSymbols) (objectIStream.readObject()));
        // The associated currency will not persist
        currency = symbolsD.getCurrency();
        TestCase.assertNotNull(currency);
    }

    /**
     * Assert that Harmony can correct read an instance that was created by
     * the Java 1.5 RI. The actual values may differ on Harmony and other JREs,
     * so we only assert the values that are known to be in the serialized data.
     */
    public void test_RIHarmony_compatible() throws Exception {
        DecimalFormatSymbols dfs;
        ObjectInputStream i = null;
        try {
            i = new ObjectInputStream(getClass().getClassLoader().getResourceAsStream("serialization/org/apache/harmony/tests/java/text/DecimalFormatSymbols.ser"));
            dfs = ((DecimalFormatSymbols) (i.readObject()));
        } finally {
            try {
                if (i != null) {
                    i.close();
                }
            } catch (Exception e) {
            }
        }
        DecimalFormatSymbolsTest.assertDecimalFormatSymbolsRIFrance(dfs);
    }

    /**
     *
     *
     * @unknown serialization/deserialization compatibility with RI6.
     */
    public void testSerializationCompatibility() throws Exception {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setExponentSeparator("EE");
        symbols.setNaN("NaN");
        SerializationTest.verifyGolden(this, symbols);
    }

    /**
     *
     *
     * @unknown serialization/deserialization compatibility.
     */
    public void testSerializationSelf() throws Exception {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.ITALIAN);
        SerializationTest.verifySelf(symbols);
    }
}

