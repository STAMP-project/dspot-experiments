/**
 * Copyright 2014 Alexey Andreev.
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


import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.teavm.junit.TeaVMProperties;
import org.teavm.junit.TeaVMProperty;
import org.teavm.junit.TeaVMTestRunner;


@RunWith(TeaVMTestRunner.class)
@TeaVMProperties(@TeaVMProperty(key = "java.util.Locale.available", value = "en, en_US, en_GB, ru, ru_RU"))
public class LocaleTest {
    @Test
    public void availableLocalesFound() {
        Assert.assertNotEquals(0, Locale.getAvailableLocales().length);
    }

    @Test
    public void languageNamesProvided() {
        Locale english = new Locale("en", "");
        Locale usEnglish = new Locale("en", "US");
        Locale russian = new Locale("ru", "RU");
        Assert.assertEquals("English", english.getDisplayLanguage(english));
        Assert.assertEquals("English", english.getDisplayLanguage(usEnglish));
        Assert.assertEquals("Russian", russian.getDisplayLanguage(english));
        Assert.assertEquals("English", english.getDisplayLanguage(usEnglish));
        Assert.assertEquals("Russian", russian.getDisplayLanguage(usEnglish));
        Assert.assertEquals("??????????", english.getDisplayLanguage(russian));
        Assert.assertEquals("???????", russian.getDisplayLanguage(russian));
    }

    @Test
    public void countryNamesProvided() {
        Locale usEnglish = new Locale("en", "US");
        Locale gbEnglish = new Locale("en", "GB");
        Locale russian = new Locale("ru", "RU");
        Assert.assertEquals("United Kingdom", gbEnglish.getDisplayCountry(usEnglish));
        Assert.assertEquals("United States", usEnglish.getDisplayCountry(usEnglish));
        Assert.assertEquals("Russia", russian.getDisplayCountry(usEnglish));
        // JVM gives here name that differs to the name provided by CLDR
        // assertEquals("??????????? ???????????", gbEnglish.getDisplayCountry(russian));
        Assert.assertEquals("??????????? ?????", usEnglish.getDisplayCountry(russian));
        Assert.assertEquals("??????", russian.getDisplayCountry(russian));
    }
}

