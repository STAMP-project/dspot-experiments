/**
 * Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.util;


import java.util.Currency;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.SkipJVM;
import org.teavm.junit.TeaVMProperties;
import org.teavm.junit.TeaVMProperty;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
@TeaVMProperties(@TeaVMProperty(key = "java.util.Locale.available", value = "en, en_US, en_GB, ru, ru_RU"))
public class CurrencyTest {
    @Test
    public void findsByCode() {
        Currency currency = Currency.getInstance("RUB");
        Assert.assertEquals("RUB", currency.getCurrencyCode());
        Assert.assertEquals(2, currency.getDefaultFractionDigits());
        Assert.assertEquals(643, currency.getNumericCode());
    }

    @Test
    public void findsByLocale() {
        Currency currency = Currency.getInstance(new Locale("ru", "RU"));
        Assert.assertEquals("RUB", currency.getCurrencyCode());
        currency = Currency.getInstance(new Locale("en", "US"));
        Assert.assertEquals("USD", currency.getCurrencyCode());
        currency = Currency.getInstance(new Locale("en", "GB"));
        Assert.assertEquals("GBP", currency.getCurrencyCode());
    }

    @Test
    @SkipJVM
    public void getsDisplayName() {
        Locale russian = new Locale("ru");
        Locale english = new Locale("en");
        Currency currency = Currency.getInstance("USD");
        Assert.assertEquals("US Dollar", currency.getDisplayName(english));
        Assert.assertEquals("?????? ???", currency.getDisplayName(russian));
        currency = Currency.getInstance("RUB");
        Assert.assertEquals("Russian Ruble", currency.getDisplayName(english));
        Assert.assertEquals("?????????? ?????", currency.getDisplayName(russian));
        Assert.assertEquals("CHF", Currency.getInstance("CHF").getDisplayName(new Locale("xx", "YY")));
    }

    @Test
    @SkipJVM
    public void getsSymbol() {
        Locale russian = new Locale("ru");
        Locale english = new Locale("en");
        Currency currency = Currency.getInstance("USD");
        Assert.assertEquals("$", currency.getSymbol(english));
        Assert.assertEquals("$", currency.getSymbol(russian));
        currency = Currency.getInstance("RUB");
        Assert.assertEquals("RUB", currency.getSymbol(english));
        Assert.assertEquals("\u20bd", currency.getSymbol(russian));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsWrongCode() {
        Currency.getInstance("WWW");
    }
}

