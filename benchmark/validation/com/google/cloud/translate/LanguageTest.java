/**
 * Copyright 2016 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.translate;


import com.google.api.services.translate.model.LanguagesResource;
import org.junit.Assert;
import org.junit.Test;


public class LanguageTest {
    private static final String CODE = "en";

    private static final String NAME = "English";

    private static final LanguagesResource LANGUAGE_PB = new LanguagesResource().setLanguage(LanguageTest.CODE).setName(LanguageTest.NAME);

    private static final Language LANGUAGE = Language.fromPb(LanguageTest.LANGUAGE_PB);

    @Test
    public void testFromPb() {
        Assert.assertEquals(LanguageTest.CODE, LanguageTest.LANGUAGE.getCode());
        Assert.assertEquals(LanguageTest.NAME, LanguageTest.LANGUAGE.getName());
        Language language = Language.fromPb(new LanguagesResource().setLanguage(LanguageTest.CODE));
        Assert.assertEquals(LanguageTest.CODE, language.getCode());
        Assert.assertNull(language.getName());
        compareLanguage(LanguageTest.LANGUAGE, Language.fromPb(LanguageTest.LANGUAGE_PB));
    }
}

