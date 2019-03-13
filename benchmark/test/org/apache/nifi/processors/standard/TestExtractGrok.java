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
package org.apache.nifi.processors.standard;


import ExtractGrok.GROK_EXPRESSION;
import ExtractGrok.GROK_PATTERN_FILE;
import ExtractGrok.KEEP_EMPTY_CAPTURES;
import ExtractGrok.NAMED_CAPTURES_ONLY;
import ExtractGrok.REL_MATCH;
import ExtractGrok.REL_NO_MATCH;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Test;


public class TestExtractGrok {
    private TestRunner testRunner;

    private static final Path GROK_LOG_INPUT = Paths.get("src/test/resources/TestExtractGrok/apache.log");

    private static final Path GROK_TEXT_INPUT = Paths.get("src/test/resources/TestExtractGrok/simple_text.log");

    @Test
    public void testExtractGrokWithMissingPattern() throws Exception {
        testRunner.setProperty(GROK_EXPRESSION, "%{FOOLOG}");
        testRunner.enqueue(TestExtractGrok.GROK_LOG_INPUT);
        testRunner.assertNotValid();
    }

    @Test
    public void testExtractGrokWithMatchedContent() throws IOException {
        testRunner.setProperty(GROK_EXPRESSION, "%{COMMONAPACHELOG}");
        testRunner.setProperty(GROK_PATTERN_FILE, "src/test/resources/TestExtractGrok/patterns");
        testRunner.enqueue(TestExtractGrok.GROK_LOG_INPUT);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH);
        final MockFlowFile matched = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        matched.assertAttributeEquals("grok.verb", "GET");
        matched.assertAttributeEquals("grok.response", "401");
        matched.assertAttributeEquals("grok.bytes", "12846");
        matched.assertAttributeEquals("grok.clientip", "64.242.88.10");
        matched.assertAttributeEquals("grok.auth", "-");
        matched.assertAttributeEquals("grok.timestamp", "07/Mar/2004:16:05:49 -0800");
        matched.assertAttributeEquals("grok.request", "/twiki/bin/edit/Main/Double_bounce_sender?topicparent=Main.ConfigurationVariables");
        matched.assertAttributeEquals("grok.httpversion", "1.1");
    }

    @Test
    public void testExtractGrokKeepEmptyCaptures() throws Exception {
        String expression = "%{NUMBER}|%{NUMBER}";
        testRunner.setProperty(GROK_EXPRESSION, expression);
        testRunner.enqueue("-42");
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH);
        final MockFlowFile matched = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        matched.assertAttributeEquals("grok.NUMBER", "[-42, null]");
    }

    @Test
    public void testExtractGrokDoNotKeepEmptyCaptures() throws Exception {
        String expression = "%{NUMBER}|%{NUMBER}";
        testRunner.setProperty(GROK_EXPRESSION, expression);
        testRunner.setProperty(KEEP_EMPTY_CAPTURES, "false");
        testRunner.enqueue("-42");
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH);
        final MockFlowFile matched = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        matched.assertAttributeEquals("grok.NUMBER", "-42");
    }

    @Test
    public void testExtractGrokWithUnMatchedContent() throws IOException {
        testRunner.setProperty(GROK_EXPRESSION, "%{URI}");
        testRunner.setProperty(GROK_PATTERN_FILE, "src/test/resources/TestExtractGrok/patterns");
        testRunner.enqueue(TestExtractGrok.GROK_TEXT_INPUT);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_NO_MATCH);
        final MockFlowFile notMatched = testRunner.getFlowFilesForRelationship(REL_NO_MATCH).get(0);
        notMatched.assertContentEquals(TestExtractGrok.GROK_TEXT_INPUT);
    }

    @Test
    public void testExtractGrokWithNotFoundPatternFile() throws IOException {
        testRunner.setProperty(GROK_EXPRESSION, "%{COMMONAPACHELOG}");
        testRunner.setProperty(GROK_PATTERN_FILE, "src/test/resources/TestExtractGrok/toto_file");
        testRunner.enqueue(TestExtractGrok.GROK_LOG_INPUT);
        testRunner.assertNotValid();
    }

    @Test
    public void testExtractGrokWithBadGrokExpression() throws IOException {
        testRunner.setProperty(GROK_EXPRESSION, "%{TOTO");
        testRunner.setProperty(GROK_PATTERN_FILE, "src/test/resources/TestExtractGrok/patterns");
        testRunner.enqueue(TestExtractGrok.GROK_LOG_INPUT);
        testRunner.assertNotValid();
    }

    @Test
    public void testExtractGrokWithNamedCapturesOnly() throws IOException {
        testRunner.setProperty(GROK_EXPRESSION, "%{COMMONAPACHELOG}");
        testRunner.setProperty(GROK_PATTERN_FILE, "src/test/resources/TestExtractGrok/patterns");
        testRunner.setProperty(NAMED_CAPTURES_ONLY, "true");
        testRunner.enqueue(TestExtractGrok.GROK_LOG_INPUT);
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH);
        final MockFlowFile matched = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        matched.assertAttributeEquals("grok.verb", "GET");
        matched.assertAttributeEquals("grok.response", "401");
        matched.assertAttributeEquals("grok.bytes", "12846");
        matched.assertAttributeEquals("grok.clientip", "64.242.88.10");
        matched.assertAttributeEquals("grok.auth", "-");
        matched.assertAttributeEquals("grok.timestamp", "07/Mar/2004:16:05:49 -0800");
        matched.assertAttributeEquals("grok.request", "/twiki/bin/edit/Main/Double_bounce_sender?topicparent=Main.ConfigurationVariables");
        matched.assertAttributeEquals("grok.httpversion", "1.1");
        matched.assertAttributeNotExists("grok.INT");
        matched.assertAttributeNotExists("grok.BASE10NUM");
        matched.assertAttributeNotExists("grok.COMMONAPACHELOG");
    }
}

