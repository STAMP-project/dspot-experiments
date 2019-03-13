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


import TranslateRpc.Option.SOURCE_LANGUAGE;
import TranslateRpc.Option.TARGET_LANGUAGE;
import com.google.cloud.translate.Translate.LanguageListOption;
import com.google.cloud.translate.Translate.TranslateOption;
import org.junit.Assert;
import org.junit.Test;


public class TranslateTest {
    private static final String LANGUAGE = "en";

    @Test
    public void testListOptions() {
        // target language
        LanguageListOption listOption = LanguageListOption.targetLanguage(TranslateTest.LANGUAGE);
        Assert.assertEquals(TARGET_LANGUAGE, listOption.getRpcOption());
        Assert.assertEquals(TranslateTest.LANGUAGE, listOption.getValue());
    }

    @Test
    public void testTranslateOptions() {
        // target language
        TranslateOption translateOption = TranslateOption.targetLanguage(TranslateTest.LANGUAGE);
        Assert.assertEquals(TARGET_LANGUAGE, translateOption.getRpcOption());
        Assert.assertEquals(TranslateTest.LANGUAGE, translateOption.getValue());
        // source language
        translateOption = TranslateOption.sourceLanguage(TranslateTest.LANGUAGE);
        Assert.assertEquals(SOURCE_LANGUAGE, translateOption.getRpcOption());
        Assert.assertEquals(TranslateTest.LANGUAGE, translateOption.getValue());
    }
}

