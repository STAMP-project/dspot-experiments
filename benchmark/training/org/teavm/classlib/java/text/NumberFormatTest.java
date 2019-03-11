/**
 * Copyright 2017 Alexey Andreev.
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
package org.teavm.classlib.java.text;


import java.text.NumberFormat;
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
public class NumberFormatTest {
    @Test
    public void formatsNumber() {
        NumberFormat format = NumberFormat.getNumberInstance(new Locale("en"));
        Assert.assertEquals("123,456.789", format.format(123456.789123));
        format = NumberFormat.getNumberInstance(new Locale("ru"));
        Assert.assertEquals("123\u00a0456,789", format.format(123456.789123));
    }

    @Test
    @SkipJVM
    public void formatsCurrency() {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("en", "US"));
        format.setCurrency(Currency.getInstance("RUB"));
        Assert.assertEquals("RUB123,456.79", format.format(123456.789123));
        format = NumberFormat.getCurrencyInstance(new Locale("ru", "RU"));
        format.setCurrency(Currency.getInstance("RUB"));
        Assert.assertEquals("123 456,79 \u20bd", format.format(123456.789123).replace('\u00a0', ' '));
    }

    @Test
    @SkipJVM
    public void formatsPercent() {
        NumberFormat format = NumberFormat.getPercentInstance(new Locale("en", "US"));
        Assert.assertEquals("12,345,679%", format.format(123456.789123));
        format = NumberFormat.getPercentInstance(new Locale("ru", "RU"));
        Assert.assertEquals("12 345 679 %", format.format(123456.789123).replace('\u00a0', ' '));
    }
}

