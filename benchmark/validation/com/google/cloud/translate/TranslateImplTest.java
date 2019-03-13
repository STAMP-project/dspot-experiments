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


import TranslateRpc.Option.FORMAT;
import TranslateRpc.Option.MODEL;
import TranslateRpc.Option.SOURCE_LANGUAGE;
import TranslateRpc.Option.TARGET_LANGUAGE;
import com.google.api.gax.retrying.RetrySettings;
import com.google.api.services.translate.model.DetectionsResourceItems;
import com.google.api.services.translate.model.LanguagesResource;
import com.google.api.services.translate.model.TranslationsResource;
import com.google.auth.Credentials;
import com.google.cloud.NoCredentials;
import com.google.cloud.ServiceOptions;
import com.google.cloud.translate.Translate.LanguageListOption;
import com.google.cloud.translate.Translate.TranslateOption;
import com.google.cloud.translate.spi.TranslateRpcFactory;
import com.google.cloud.translate.spi.v2.TranslateRpc;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Map;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TranslateImplTest {
    private static final String API_KEY = "api_key";

    private static final String TARGET_LANGUAGE = "es";

    private static final LanguagesResource LANGUAGE1_PB = new LanguagesResource().setLanguage("en").setName("english");

    private static final LanguagesResource LANGUAGE2_PB = new LanguagesResource().setLanguage("es").setName("spanish");

    private static final LanguagesResource LANGUAGE3_PB = new LanguagesResource().setLanguage("en").setName("ingl?s");

    private static final LanguagesResource LANGUAGE4_PB = new LanguagesResource().setLanguage("es").setName("espa?ol");

    private static final Language LANGUAGE1 = Language.fromPb(TranslateImplTest.LANGUAGE1_PB);

    private static final Language LANGUAGE2 = Language.fromPb(TranslateImplTest.LANGUAGE2_PB);

    private static final Language LANGUAGE3 = Language.fromPb(TranslateImplTest.LANGUAGE3_PB);

    private static final Language LANGUAGE4 = Language.fromPb(TranslateImplTest.LANGUAGE4_PB);

    private static final List<Language> LANGUAGES1 = ImmutableList.of(TranslateImplTest.LANGUAGE1, TranslateImplTest.LANGUAGE2);

    private static final List<Language> LANGUAGES2 = ImmutableList.of(TranslateImplTest.LANGUAGE3, TranslateImplTest.LANGUAGE4);

    private static final DetectionsResourceItems DETECTION1_PB = new DetectionsResourceItems().setLanguage("en").setConfidence(0.9F);

    private static final DetectionsResourceItems DETECTION2_PB = new DetectionsResourceItems().setLanguage("en").setConfidence(0.8F);

    private static final Detection DETECTION1 = Detection.fromPb(TranslateImplTest.DETECTION1_PB);

    private static final Detection DETECTION2 = Detection.fromPb(TranslateImplTest.DETECTION2_PB);

    private static final TranslationsResource TRANSLATION1_PB = new TranslationsResource().setTranslatedText("Hello World!").setDetectedSourceLanguage("es");

    private static final TranslationsResource TRANSLATION2_PB = new TranslationsResource().setTranslatedText("Hello World!").setDetectedSourceLanguage("de");

    private static final Translation TRANSLATION1 = Translation.fromPb(TranslateImplTest.TRANSLATION1_PB);

    private static final Translation TRANSLATION2 = Translation.fromPb(TranslateImplTest.TRANSLATION2_PB);

    // Empty TranslateRpc options
    private static final Map<TranslateRpc.Option, ?> EMPTY_RPC_OPTIONS = ImmutableMap.of();

    // Language list options
    private static final LanguageListOption LANGUAGE_LIST_OPTION = LanguageListOption.targetLanguage(TranslateImplTest.TARGET_LANGUAGE);

    private static final Map<TranslateRpc.Option, ?> LANGUAGE_LIST_OPTIONS = ImmutableMap.of(TranslateRpc.Option.TARGET_LANGUAGE, TranslateImplTest.LANGUAGE_LIST_OPTION.getValue());

    // Translate options
    private static final TranslateOption TARGET_LANGUAGE_OPTION = TranslateOption.targetLanguage("en");

    private static final TranslateOption SOURCE_LANGUAGE_OPTION = TranslateOption.sourceLanguage("de");

    private static final TranslateOption MODEL_OPTION = TranslateOption.model("nmt");

    private static final TranslateOption FORMAT_OPTION = TranslateOption.format("text");

    private static final Map<TranslateRpc.Option, ?> TRANSLATE_OPTIONS = ImmutableMap.of(TranslateRpc.Option.TARGET_LANGUAGE, TranslateImplTest.TARGET_LANGUAGE_OPTION.getValue(), SOURCE_LANGUAGE, TranslateImplTest.SOURCE_LANGUAGE_OPTION.getValue(), MODEL, "nmt", FORMAT, "text");

    private static final RetrySettings NO_RETRY_SETTINGS = ServiceOptions.getNoRetrySettings();

    private TranslateOptions options;

    private TranslateRpcFactory rpcFactoryMock;

    private TranslateRpc translateRpcMock;

    private Translate translate;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testGetOptions() {
        EasyMock.replay(translateRpcMock);
        initializeService();
        Assert.assertSame(options, translate.getOptions());
        verify();
    }

    @Test
    public void testListSupportedLanguages() {
        EasyMock.expect(translateRpcMock.listSupportedLanguages(TranslateImplTest.EMPTY_RPC_OPTIONS)).andReturn(ImmutableList.of(TranslateImplTest.LANGUAGE1_PB, TranslateImplTest.LANGUAGE2_PB));
        EasyMock.replay(translateRpcMock);
        initializeService();
        Assert.assertEquals(TranslateImplTest.LANGUAGES1, translate.listSupportedLanguages());
        verify();
    }

    @Test
    public void testListSupportedLanguagesWithOptions() {
        EasyMock.expect(translateRpcMock.listSupportedLanguages(TranslateImplTest.LANGUAGE_LIST_OPTIONS)).andReturn(ImmutableList.of(TranslateImplTest.LANGUAGE3_PB, TranslateImplTest.LANGUAGE4_PB));
        EasyMock.replay(translateRpcMock);
        initializeService();
        Assert.assertEquals(TranslateImplTest.LANGUAGES2, translate.listSupportedLanguages(LanguageListOption.targetLanguage(TranslateImplTest.TARGET_LANGUAGE)));
        verify();
    }

    @Test
    public void testDetect() {
        String text = "text";
        EasyMock.expect(translateRpcMock.detect(ImmutableList.of(text))).andReturn(ImmutableList.<List<DetectionsResourceItems>>of(ImmutableList.of(TranslateImplTest.DETECTION1_PB)));
        EasyMock.replay(translateRpcMock);
        initializeService();
        Assert.assertEquals(TranslateImplTest.DETECTION1, translate.detect(text));
        verify();
    }

    @Test
    public void testDetectMultipleDetections() {
        String text = "text";
        EasyMock.expect(translateRpcMock.detect(ImmutableList.of(text))).andReturn(ImmutableList.<List<DetectionsResourceItems>>of(ImmutableList.of(TranslateImplTest.DETECTION1_PB, TranslateImplTest.DETECTION2_PB)));
        EasyMock.replay(translateRpcMock);
        initializeService();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Multiple detections found for text: text");
        translate.detect(text);
        verify();
    }

    @Test
    public void testDetectNoDetection() {
        String text = "text";
        EasyMock.expect(translateRpcMock.detect(ImmutableList.of(text))).andReturn(ImmutableList.<List<DetectionsResourceItems>>of(ImmutableList.<DetectionsResourceItems>of()));
        EasyMock.replay(translateRpcMock);
        initializeService();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No detection found for text: text");
        translate.detect(text);
        verify();
    }

    @Test
    public void testDetectList() {
        String text1 = "text";
        String text2 = "other text";
        List<String> texts = ImmutableList.of(text1, text2);
        EasyMock.expect(translateRpcMock.detect(texts)).andReturn(ImmutableList.<List<DetectionsResourceItems>>of(ImmutableList.of(TranslateImplTest.DETECTION1_PB), ImmutableList.of(TranslateImplTest.DETECTION2_PB)));
        EasyMock.replay(translateRpcMock);
        initializeService();
        Assert.assertEquals(ImmutableList.of(TranslateImplTest.DETECTION1, TranslateImplTest.DETECTION2), translate.detect(texts));
        verify();
    }

    @Test
    public void testDetectListMultipleDetections() {
        String text1 = "text";
        String text2 = "other text";
        List<String> texts = ImmutableList.of(text1, text2);
        EasyMock.expect(translateRpcMock.detect(texts)).andReturn(ImmutableList.<List<DetectionsResourceItems>>of(ImmutableList.of(TranslateImplTest.DETECTION1_PB, TranslateImplTest.DETECTION2_PB), ImmutableList.of(TranslateImplTest.DETECTION1_PB)));
        EasyMock.replay(translateRpcMock);
        initializeService();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Multiple detections found for text: text");
        translate.detect(texts);
        verify();
    }

    @Test
    public void testDetectListNoDetection() {
        String text1 = "text";
        String text2 = "other text";
        List<String> texts = ImmutableList.of(text1, text2);
        EasyMock.expect(translateRpcMock.detect(texts)).andReturn(ImmutableList.<List<DetectionsResourceItems>>of(ImmutableList.of(TranslateImplTest.DETECTION1_PB), ImmutableList.<DetectionsResourceItems>of()));
        EasyMock.replay(translateRpcMock);
        initializeService();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No detection found for text: other text");
        translate.detect(texts);
        verify();
    }

    @Test
    public void testDetectVararg() {
        String text1 = "text";
        String text2 = "other text";
        EasyMock.expect(translateRpcMock.detect(ImmutableList.of(text1, text2))).andReturn(ImmutableList.<List<DetectionsResourceItems>>of(ImmutableList.of(TranslateImplTest.DETECTION1_PB), ImmutableList.of(TranslateImplTest.DETECTION2_PB)));
        EasyMock.replay(translateRpcMock);
        initializeService();
        Assert.assertEquals(ImmutableList.of(TranslateImplTest.DETECTION1, TranslateImplTest.DETECTION2), translate.detect(text1, text2));
        verify();
    }

    @Test
    public void testDetectVarargMultipleDetections() {
        String text1 = "text";
        String text2 = "other text";
        EasyMock.expect(translateRpcMock.detect(ImmutableList.of(text1, text2))).andReturn(ImmutableList.<List<DetectionsResourceItems>>of(ImmutableList.of(TranslateImplTest.DETECTION1_PB, TranslateImplTest.DETECTION2_PB), ImmutableList.of(TranslateImplTest.DETECTION1_PB)));
        EasyMock.replay(translateRpcMock);
        initializeService();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Multiple detections found for text: text");
        translate.detect(text1, text2);
        verify();
    }

    @Test
    public void testDetectVarargNoDetection() {
        String text1 = "text";
        String text2 = "other text";
        EasyMock.expect(translateRpcMock.detect(ImmutableList.of(text1, text2))).andReturn(ImmutableList.<List<DetectionsResourceItems>>of(ImmutableList.of(TranslateImplTest.DETECTION1_PB), ImmutableList.<DetectionsResourceItems>of()));
        EasyMock.replay(translateRpcMock);
        initializeService();
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No detection found for text: other text");
        translate.detect(text1, text2);
        verify();
    }

    @Test
    public void testTranslate() {
        String text = "?Hola Mundo!";
        EasyMock.expect(translateRpcMock.translate(ImmutableList.of(text), TranslateImplTest.EMPTY_RPC_OPTIONS)).andReturn(ImmutableList.of(TranslateImplTest.TRANSLATION1_PB));
        EasyMock.replay(translateRpcMock);
        initializeService();
        Assert.assertEquals(TranslateImplTest.TRANSLATION1, translate.translate(text));
        verify();
    }

    @Test
    public void testTranslateWithOptions() {
        String text = "Hallo Welt!";
        EasyMock.expect(translateRpcMock.translate(ImmutableList.of(text), TranslateImplTest.TRANSLATE_OPTIONS)).andReturn(ImmutableList.of(TranslateImplTest.TRANSLATION2_PB));
        EasyMock.replay(translateRpcMock);
        initializeService();
        Assert.assertEquals(TranslateImplTest.TRANSLATION2, translate.translate(text, TranslateImplTest.TARGET_LANGUAGE_OPTION, TranslateImplTest.SOURCE_LANGUAGE_OPTION, TranslateImplTest.MODEL_OPTION, TranslateImplTest.FORMAT_OPTION));
        verify();
    }

    @Test
    public void testTranslateList() {
        String text1 = "?Hola Mundo!";
        String text2 = "Hallo Welt!";
        List<String> texts = ImmutableList.of(text1, text2);
        EasyMock.expect(translateRpcMock.translate(texts, TranslateImplTest.EMPTY_RPC_OPTIONS)).andReturn(ImmutableList.of(TranslateImplTest.TRANSLATION1_PB, TranslateImplTest.TRANSLATION2_PB));
        EasyMock.replay(translateRpcMock);
        initializeService();
        Assert.assertEquals(ImmutableList.of(TranslateImplTest.TRANSLATION1, TranslateImplTest.TRANSLATION2), translate.translate(texts));
        verify();
    }

    @Test
    public void testTranslateListWithOptions() {
        String text = "Hallo Welt!";
        List<String> texts = ImmutableList.of(text);
        EasyMock.expect(translateRpcMock.translate(texts, TranslateImplTest.TRANSLATE_OPTIONS)).andReturn(ImmutableList.of(TranslateImplTest.TRANSLATION2_PB));
        EasyMock.replay(translateRpcMock);
        initializeService();
        Assert.assertEquals(ImmutableList.of(TranslateImplTest.TRANSLATION2), translate.translate(texts, TranslateImplTest.TARGET_LANGUAGE_OPTION, TranslateImplTest.SOURCE_LANGUAGE_OPTION, TranslateImplTest.MODEL_OPTION, TranslateImplTest.FORMAT_OPTION));
        verify();
    }

    @Test
    public void testRetryableException() {
        EasyMock.expect(translateRpcMock.listSupportedLanguages(TranslateImplTest.EMPTY_RPC_OPTIONS)).andThrow(new TranslateException(500, "internalError")).andReturn(ImmutableList.of(TranslateImplTest.LANGUAGE1_PB, TranslateImplTest.LANGUAGE2_PB));
        EasyMock.replay(translateRpcMock);
        translate = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        Assert.assertEquals(TranslateImplTest.LANGUAGES1, translate.listSupportedLanguages());
        verify();
    }

    @Test
    public void testNonRetryableException() {
        String exceptionMessage = "Not Implemented";
        EasyMock.expect(translateRpcMock.listSupportedLanguages(TranslateImplTest.EMPTY_RPC_OPTIONS)).andThrow(new TranslateException(501, exceptionMessage));
        EasyMock.replay(translateRpcMock);
        translate = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        thrown.expect(TranslateException.class);
        thrown.expectMessage(exceptionMessage);
        translate.listSupportedLanguages();
        verify();
    }

    @Test
    public void testRuntimeException() {
        String exceptionMessage = "Artificial runtime exception";
        EasyMock.expect(translateRpcMock.listSupportedLanguages(TranslateImplTest.EMPTY_RPC_OPTIONS)).andThrow(new RuntimeException(exceptionMessage));
        EasyMock.replay(translateRpcMock);
        translate = options.toBuilder().setRetrySettings(ServiceOptions.getDefaultRetrySettings()).build().getService();
        thrown.expect(TranslateException.class);
        thrown.expectMessage(exceptionMessage);
        translate.listSupportedLanguages();
        verify();
    }

    @Test
    public void testCredentialsOverridesApiKey() {
        Credentials credentials = NoCredentials.getInstance();
        TranslateOptions overridden = options.toBuilder().setCredentials(credentials).build();
        Assert.assertSame(overridden.getCredentials(), credentials);
        Assert.assertNull(overridden.getApiKey());
    }
}

