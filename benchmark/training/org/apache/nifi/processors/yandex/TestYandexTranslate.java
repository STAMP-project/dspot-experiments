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
package org.apache.nifi.processors.yandex;


import YandexTranslate.CHARACTER_SET;
import YandexTranslate.KEY;
import YandexTranslate.REL_SUCCESS;
import YandexTranslate.REL_TRANSLATION_FAILED;
import YandexTranslate.SOURCE_LANGUAGE;
import YandexTranslate.TARGET_LANGUAGE;
import YandexTranslate.TRANSLATE_CONTENT;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestYandexTranslate {
    private static final Map<String, String> translations = new HashMap<>();

    @Test
    public void testTranslateContent() {
        final TestRunner testRunner = createTestRunner(200);
        testRunner.setProperty(KEY, "a");
        testRunner.setProperty(SOURCE_LANGUAGE, "fr");
        testRunner.setProperty(TARGET_LANGUAGE, "en");
        testRunner.setProperty(TRANSLATE_CONTENT, "true");
        testRunner.setProperty(CHARACTER_SET, "UTF-8");
        testRunner.enqueue("bonjour".getBytes());
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outText = new String(out.toByteArray());
        Assert.assertEquals("hello", outText);
    }

    @Test
    public void testTranslateSingleAttribute() {
        final TestRunner testRunner = createTestRunner(200);
        testRunner.setProperty(KEY, "A");
        testRunner.setProperty(SOURCE_LANGUAGE, "fr");
        testRunner.setProperty(TARGET_LANGUAGE, "en");
        testRunner.setProperty(TRANSLATE_CONTENT, "false");
        testRunner.setProperty(CHARACTER_SET, "UTF-8");
        testRunner.setProperty("translated", "bonjour");
        testRunner.enqueue(new byte[0]);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(0, out.toByteArray().length);
        out.assertAttributeEquals("translated", "hello");
    }

    @Test
    public void testTranslateMultipleAttributes() {
        final TestRunner testRunner = createTestRunner(200);
        testRunner.setProperty(KEY, "A");
        testRunner.setProperty(SOURCE_LANGUAGE, "fr");
        testRunner.setProperty(TARGET_LANGUAGE, "en");
        testRunner.setProperty(TRANSLATE_CONTENT, "false");
        testRunner.setProperty(CHARACTER_SET, "UTF-8");
        testRunner.setProperty("hello", "bonjour");
        testRunner.setProperty("translate", "traduire");
        testRunner.setProperty("fun", "amusant");
        testRunner.enqueue(new byte[0]);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        Assert.assertEquals(0, out.toByteArray().length);
        out.assertAttributeEquals("hello", "hello");
        out.assertAttributeEquals("translate", "translate");
        out.assertAttributeEquals("fun", "fun");
    }

    @Test
    public void testTranslateContentAndMultipleAttributes() {
        final TestRunner testRunner = createTestRunner(200);
        testRunner.setProperty(KEY, "A");
        testRunner.setProperty(SOURCE_LANGUAGE, "fr");
        testRunner.setProperty(TARGET_LANGUAGE, "en");
        testRunner.setProperty(TRANSLATE_CONTENT, "true");
        testRunner.setProperty(CHARACTER_SET, "UTF-8");
        testRunner.setProperty("hello", "bonjour");
        testRunner.setProperty("translate", "traduire");
        testRunner.setProperty("fun", "amusant");
        testRunner.setProperty("nifi", "nifi");
        testRunner.enqueue("ordinateur".getBytes());
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("computer");
        out.assertAttributeEquals("hello", "hello");
        out.assertAttributeEquals("translate", "translate");
        out.assertAttributeEquals("fun", "fun");
        out.assertAttributeEquals("nifi", "nifi");
    }

    @Test
    public void testFailureResponse() {
        final TestRunner testRunner = createTestRunner(403);
        testRunner.setProperty(KEY, "A");
        testRunner.setProperty(SOURCE_LANGUAGE, "fr");
        testRunner.setProperty(TARGET_LANGUAGE, "en");
        testRunner.setProperty(TRANSLATE_CONTENT, "true");
        testRunner.setProperty(CHARACTER_SET, "UTF-8");
        testRunner.setProperty("hello", "bonjour");
        testRunner.setProperty("translate", "traduire");
        testRunner.setProperty("fun", "amusant");
        testRunner.setProperty("nifi", "nifi");
        testRunner.enqueue("ordinateur".getBytes());
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_TRANSLATION_FAILED, 1);
    }
}

