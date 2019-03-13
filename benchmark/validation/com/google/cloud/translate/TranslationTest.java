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


import com.google.api.services.translate.model.TranslationsResource;
import org.junit.Assert;
import org.junit.Test;


public class TranslationTest {
    private static final String TRANSLATED_TEXT = "Hello world";

    private static final String SOURCE_LANGUAGE = "en";

    private static final TranslationsResource TRANSLATION_PB = new TranslationsResource().setTranslatedText(TranslationTest.TRANSLATED_TEXT).setDetectedSourceLanguage(TranslationTest.SOURCE_LANGUAGE);

    private static final Translation TRANSLATION = Translation.fromPb(TranslationTest.TRANSLATION_PB);

    @Test
    public void testFromPb() {
        Assert.assertEquals(TranslationTest.TRANSLATED_TEXT, TranslationTest.TRANSLATION.getTranslatedText());
        Assert.assertEquals(TranslationTest.SOURCE_LANGUAGE, TranslationTest.TRANSLATION.getSourceLanguage());
        compareTranslation(TranslationTest.TRANSLATION, Translation.fromPb(TranslationTest.TRANSLATION_PB));
    }
}

