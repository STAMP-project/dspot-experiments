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


import ReplaceText.ALWAYS_REPLACE;
import ReplaceText.APPEND;
import ReplaceText.ENTIRE_TEXT;
import ReplaceText.EVALUATION_MODE;
import ReplaceText.LINE_BY_LINE;
import ReplaceText.LITERAL_REPLACE;
import ReplaceText.MAX_BUFFER_SIZE;
import ReplaceText.PREPEND;
import ReplaceText.REGEX_REPLACE;
import ReplaceText.REL_FAILURE;
import ReplaceText.REL_SUCCESS;
import ReplaceText.REPLACEMENT_STRATEGY;
import ReplaceText.REPLACEMENT_VALUE;
import ReplaceText.SEARCH_VALUE;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class TestReplaceText {
    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testConfigurationCornerCase() throws IOException {
        final TestRunner runner = getRunner();
        runner.run();
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(Paths.get("src/test/resources/hello.txt"));
    }

    @Test
    public void testIterativeRegexReplace() {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "\"([a-z]+?)\":\"(.*?)\"");
        runner.setProperty(REPLACEMENT_VALUE, "\"${\'$1\':toUpper()}\":\"$2\"");
        runner.enqueue("{\"name\":\"Smith\",\"middle\":\"nifi\",\"firstname\":\"John\"}");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("{\"NAME\":\"Smith\",\"MIDDLE\":\"nifi\",\"FIRSTNAME\":\"John\"}");
    }

    @Test
    public void testIterativeRegexReplaceLineByLine() {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "\"([a-z]+?)\":\"(.*?)\"");
        runner.setProperty(REPLACEMENT_VALUE, "\"${\'$1\':toUpper()}\":\"$2\"");
        runner.enqueue("{\"name\":\"Smith\",\"middle\":\"nifi\",\"firstname\":\"John\"}");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("{\"NAME\":\"Smith\",\"MIDDLE\":\"nifi\",\"FIRSTNAME\":\"John\"}");
    }

    @Test
    public void testSimple() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "ell");
        runner.setProperty(REPLACEMENT_VALUE, "lle");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Hlleo, World!".getBytes("UTF-8"));
    }

    @Test
    public void testWithEscaped$InReplacement() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(?s:^.*$)");
        runner.setProperty(REPLACEMENT_VALUE, "a\\$b");
        runner.enqueue("a$a,b,c,d");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("a\\$b".getBytes("UTF-8"));
    }

    @Test
    public void testWithUnEscaped$InReplacement() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(?s:^.*$)");
        runner.setProperty(REPLACEMENT_VALUE, "a$b");
        runner.enqueue("a$a,b,c,d");
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("a$b".getBytes("UTF-8"));
    }

    @Test
    public void testPrependSimple() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REPLACEMENT_VALUE, "TEST");
        runner.setProperty(REPLACEMENT_STRATEGY, PREPEND);
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("TESTHello, World!".getBytes("UTF-8"));
    }

    @Test
    public void testPrependLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REPLACEMENT_VALUE, "_");
        runner.setProperty(REPLACEMENT_STRATEGY, PREPEND);
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.enqueue("hello\nthere\nmadam".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("_hello\n_there\n_madam".getBytes("UTF-8"));
    }

    @Test
    public void testAppendSimple() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REPLACEMENT_VALUE, "TEST");
        runner.setProperty(REPLACEMENT_STRATEGY, APPEND);
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Hello, World!TEST".getBytes("UTF-8"));
    }

    @Test
    public void testAppendWithCarriageReturn() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REPLACEMENT_VALUE, "!");
        runner.setProperty(REPLACEMENT_STRATEGY, APPEND);
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.enqueue("hello\rthere\rsir".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("hello!\rthere!\rsir!");
    }

    @Test
    public void testAppendWithNewLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REPLACEMENT_VALUE, "!");
        runner.setProperty(REPLACEMENT_STRATEGY, APPEND);
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.enqueue("hello\nthere\nsir".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("hello!\nthere!\nsir!");
    }

    @Test
    public void testAppendWithCarriageReturnNewLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REPLACEMENT_VALUE, "!");
        runner.setProperty(REPLACEMENT_STRATEGY, APPEND);
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.enqueue("hello\r\nthere\r\nsir".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("hello!\r\nthere!\r\nsir!");
    }

    @Test
    public void testLiteralSimple() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "ell");
        runner.setProperty(REPLACEMENT_VALUE, "lle");
        runner.setProperty(REPLACEMENT_STRATEGY, LITERAL_REPLACE);
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Hlleo, World!".getBytes("UTF-8"));
    }

    @Test
    public void testLiteralBackReference() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "ell");
        runner.setProperty(REPLACEMENT_VALUE, "[$1]");
        runner.setProperty(REPLACEMENT_STRATEGY, LITERAL_REPLACE);
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("H[$1]o, World!");
    }

    @Test
    public void testLiteral() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, ".ell.");
        runner.setProperty(REPLACEMENT_VALUE, "test");
        runner.setProperty(REPLACEMENT_STRATEGY, LITERAL_REPLACE);
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.enqueue("H.ell.o, World! .ell.".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Hello, World!");
        final MockFlowFile out2 = runner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        out2.assertContentEquals("Htesto, World! test");
    }

    @Test
    public void testBackReference() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(ell)");
        runner.setProperty(REPLACEMENT_VALUE, "[$1]");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("H[ell]o, World!");
    }

    @Test
    public void testBackRefFollowedByNumbers() throws IOException {
        final TestRunner runner = getRunner();
        String expected = "Hell23o, World!";
        runner.setProperty(SEARCH_VALUE, "(ell)");
        runner.setProperty(REPLACEMENT_VALUE, "$123");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "notSupported");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String actual = new String(out.toByteArray(), StandardCharsets.UTF_8);
        System.out.println(actual);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void testBackRefWithNoCapturingGroup() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "ell");
        runner.setProperty(REPLACEMENT_VALUE, "$0123");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "notSupported");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String actual = new String(out.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertEquals("Hell123o, World!", actual);
    }

    @Test
    public void testReplacementWithExpressionLanguage() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "${replaceKey}");
        runner.setProperty(REPLACEMENT_VALUE, "GoodBye");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "H.*o");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Hello, World!");
    }

    @Test
    public void testReplacementWithExpressionLanguageIsEscaped() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(ell)");
        runner.setProperty(REPLACEMENT_VALUE, "[${abc}]");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "$1");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("H[$1]o, World!");
    }

    @Test
    public void testRegexWithExpressionLanguage() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "${replaceKey}");
        runner.setProperty(REPLACEMENT_VALUE, "${replaceValue}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "Hello");
        attributes.put("replaceValue", "Good-bye");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Good-bye, World!");
    }

    @Test
    public void testRegexWithExpressionLanguageIsEscaped() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "${replaceKey}");
        runner.setProperty(REPLACEMENT_VALUE, "${replaceValue}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "H.*o");
        attributes.put("replaceValue", "Good-bye");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Hello, World!");
    }

    @Test
    public void testBackReferenceWithTooLargeOfIndexIsEscaped() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(ell)");
        runner.setProperty(REPLACEMENT_VALUE, "$1$2");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "H.*o");
        attributes.put("replaceValue", "Good-bye");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Hell$2o, World!");
    }

    @Test
    public void testBackReferenceWithInvalidReferenceIsEscaped() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(ell)");
        runner.setProperty(REPLACEMENT_VALUE, "$d");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "H.*o");
        attributes.put("replaceValue", "Good-bye");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("H$do, World!");
    }

    @Test
    public void testEscapingDollarSign() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(ell)");
        runner.setProperty(REPLACEMENT_VALUE, "\\$1");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "H.*o");
        attributes.put("replaceValue", "Good-bye");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("H$1o, World!");
    }

    @Test
    public void testReplaceWithEmptyString() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(ell)");
        runner.setProperty(REPLACEMENT_VALUE, "");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Ho, World!");
    }

    @Test
    public void testWithNoMatch() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "Z");
        runner.setProperty(REPLACEMENT_VALUE, "Morning");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Hello, World!");
    }

    @Test
    public void testWithMultipleMatches() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "l");
        runner.setProperty(REPLACEMENT_VALUE, "R");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("HeRRo, WorRd!");
    }

    @Test
    public void testAttributeToContent() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, ".*");
        runner.setProperty(REPLACEMENT_VALUE, "${abc}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "Good");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Good");
    }

    @Test
    public void testRoutesToFailureIfTooLarge() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "[123]");
        runner.setProperty(MAX_BUFFER_SIZE, "1 b");
        runner.setProperty(REPLACEMENT_VALUE, "${abc}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "Good");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testRoutesToSuccessIfTooLargeButRegexIsDotAsterisk() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, ".*");
        runner.setProperty(MAX_BUFFER_SIZE, "1 b");
        runner.setProperty(REPLACEMENT_VALUE, "${abc}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "Good");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Good");
    }

    @Test
    public void testProblematicCase1() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, ".*");
        runner.setProperty(REPLACEMENT_VALUE, "${filename}\t${now():format(\"yyyy/MM/dd\'T\'HHmmss\'Z\'\")}\t${fileSize}\n");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "abc.txt");
        runner.enqueue(Paths.get("src/test/resources/hello.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertTrue(outContent.startsWith("abc.txt\t"));
        System.out.println(outContent);
        Assert.assertTrue(outContent.endsWith("13\n"));
    }

    @Test
    public void testGetExistingContent() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(?s)(^.*)");
        runner.setProperty(REPLACEMENT_VALUE, "attribute header\n\n${filename}\n\ndata header\n\n$1\n\nfooter");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "abc.txt");
        runner.enqueue("Hello\nWorld!".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
        Assert.assertTrue(outContent.equals("attribute header\n\nabc.txt\n\ndata header\n\nHello\nWorld!\n\nfooter"));
        System.out.println(outContent);
    }

    @Test
    public void testReplaceWithinCurlyBraces() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, ".+");
        runner.setProperty(REPLACEMENT_VALUE, "{ ${filename} }");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "abc.txt");
        runner.enqueue("Hello".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("{ abc.txt }");
    }

    @Test
    public void testDefaultReplacement() throws Exception {
        final String defaultValue = "default-replacement-value";
        // leave the default regex settings
        final TestRunner runner = getRunner();
        runner.setProperty(REPLACEMENT_VALUE, defaultValue);
        final Map<String, String> attributes = new HashMap<>();
        runner.enqueue("original-text".getBytes(StandardCharsets.UTF_8), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(defaultValue);
    }

    @Test
    public void testDefaultMultilineReplacement() throws Exception {
        final String defaultValue = "default-replacement-value";
        // leave the default regex settings
        final TestRunner runner = getRunner();
        runner.setProperty(REPLACEMENT_VALUE, defaultValue);
        final Map<String, String> attributes = new HashMap<>();
        runner.enqueue((("original-text-line-1" + (System.lineSeparator())) + "original-text-line-2").getBytes(StandardCharsets.UTF_8), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(defaultValue);
    }

    /* Line by Line */
    @Test
    public void testSimpleLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "odo");
        runner.setProperty(REPLACEMENT_VALUE, "ood");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/food.txt")));
    }

    @Test
    public void testPrependSimpleLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(REPLACEMENT_STRATEGY, PREPEND);
        runner.setProperty(REPLACEMENT_VALUE, "TEST ");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/PrependLineByLineTest.txt")));
    }

    @Test
    public void testAppendSimpleLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(REPLACEMENT_STRATEGY, APPEND);
        runner.setProperty(REPLACEMENT_VALUE, " TEST");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/AppendLineByLineTest.txt")));
    }

    @Test
    public void testAppendEndlineCR() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(REPLACEMENT_VALUE, "TEST");
        runner.setProperty(REPLACEMENT_STRATEGY, APPEND);
        runner.enqueue("Hello \rWorld \r".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Hello TEST\rWorld TEST\r".getBytes("UTF-8"));
    }

    @Test
    public void testAppendEndlineCRLF() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(REPLACEMENT_VALUE, "TEST");
        runner.setProperty(REPLACEMENT_STRATEGY, APPEND);
        runner.enqueue("Hello \r\nWorld \r\n".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Hello TEST\r\nWorld TEST\r\n".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleLiteral() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "odo");
        runner.setProperty(REPLACEMENT_VALUE, "ood");
        runner.setProperty(REPLACEMENT_STRATEGY, LITERAL_REPLACE);
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/food.txt")));
    }

    @Test
    public void testLiteralBackReferenceLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "jo");
        runner.setProperty(REPLACEMENT_VALUE, "[$1]");
        runner.setProperty(REPLACEMENT_STRATEGY, LITERAL_REPLACE);
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/cu[$1]_Po[$1].txt")));
    }

    @Test
    public void testLiteralLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, ".ell.");
        runner.setProperty(REPLACEMENT_VALUE, "test");
        runner.setProperty(REPLACEMENT_STRATEGY, LITERAL_REPLACE);
        runner.enqueue("H.ell.o, World! .ell. \n .ell. .ell.".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Htesto, World! test \n test test");
    }

    @Test
    public void testBackReferenceLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "(DODO)");
        runner.setProperty(REPLACEMENT_VALUE, "[$1]");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/[DODO].txt")));
    }

    @Test
    public void testReplacementWithExpressionLanguageIsEscapedLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "(jo)");
        runner.setProperty(REPLACEMENT_VALUE, "[${abc}]");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "$1");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/cu[$1]_Po[$1].txt")));
    }

    @Test
    public void testRegexWithExpressionLanguageLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "${replaceKey}");
        runner.setProperty(REPLACEMENT_VALUE, "${replaceValue}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "Riley");
        attributes.put("replaceValue", "Spider");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/Spider.txt")));
    }

    @Test
    public void testRegexWithExpressionLanguageIsEscapedLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "${replaceKey}");
        runner.setProperty(REPLACEMENT_VALUE, "${replaceValue}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "R.*y");
        attributes.put("replaceValue", "Spider");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
    }

    @Test
    public void testBackReferenceWithTooLargeOfIndexIsEscapedLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "(lu)");
        runner.setProperty(REPLACEMENT_VALUE, "$1$2");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "R.*y");
        attributes.put("replaceValue", "Spiderman");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/Blu$2e_clu$2e.txt")));
    }

    @Test
    public void testBackReferenceWithInvalidReferenceIsEscapedLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "(ew)");
        runner.setProperty(REPLACEMENT_VALUE, "$d");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "H.*o");
        attributes.put("replaceValue", "Good-bye");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/D$d_h$d.txt")));
    }

    @Test
    public void testEscapingDollarSignLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "(DO)");
        runner.setProperty(REPLACEMENT_VALUE, "\\$1");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("replaceKey", "H.*o");
        attributes.put("replaceValue", "Good-bye");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/$1$1.txt")));
    }

    @Test
    public void testReplaceWithEmptyStringLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "(jo)");
        runner.setProperty(REPLACEMENT_VALUE, "");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/cu_Po.txt")));
    }

    @Test
    public void testWithNoMatchLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "Z");
        runner.setProperty(REPLACEMENT_VALUE, "Morning");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
    }

    @Test
    public void testWithMultipleMatchesLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "l");
        runner.setProperty(REPLACEMENT_VALUE, "R");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(translateNewLines(new File("src/test/resources/TestReplaceTextLineByLine/BRue_cRue_RiRey.txt")));
    }

    @Test
    public void testAttributeToContentLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, ".*");
        runner.setProperty(REPLACEMENT_VALUE, "${abc}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "Good");
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt"), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Good\nGood\nGood\nGood\nGood\nGood\nGood\nGood\nGood\nGood\nGood");
    }

    @Test
    public void testAttributeToContentWindows() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, ".*");
        runner.setProperty(REPLACEMENT_VALUE, "${abc}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "Good");
        runner.enqueue("<<<HEADER>>>\r\n<<BODY>>\r\n<<<FOOTER>>>\r".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("Good\r\nGood\r\nGood\r");
    }

    @Test
    public void testProblematicCase1LineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, ".*");
        runner.setProperty(REPLACEMENT_VALUE, "${filename}\t${now():format(\"yyyy/MM/dd\'T\'HHmmss\'Z\'\")}\t${fileSize}\n");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "abc.txt");
        runner.enqueue(translateNewLines(Paths.get("src/test/resources/TestReplaceTextLineByLine/testFile.txt")), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outContent = translateNewLines(new String(out.toByteArray(), StandardCharsets.UTF_8));
        Assert.assertTrue(outContent.startsWith("abc.txt\t"));
        System.out.println(outContent);
        Assert.assertTrue(((outContent.endsWith("193\n")) || (outContent.endsWith("203\r\n"))));
    }

    @Test
    public void testGetExistingContentLineByLine() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "(?s)(^.*)");
        runner.setProperty(REPLACEMENT_VALUE, "attribute header\n\n${filename}\n\ndata header\n\n$1\n\nfooter\n");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "abc.txt");
        runner.enqueue("Hello\nWorld!".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        final String outContent = new String(out.toByteArray(), StandardCharsets.UTF_8);
        System.out.println(outContent);
        Assert.assertTrue(outContent.equals(("attribute header\n\nabc.txt\n\ndata header\n\nHello\n\n\nfooter\n" + "attribute header\n\nabc.txt\n\ndata header\n\nWorld!\n\nfooter\n")));
    }

    @Test
    public void testCapturingGroupInExpressionLanguage() {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "(.*?),(.*?),(\\d+.*)");
        runner.setProperty(REPLACEMENT_VALUE, "$1,$2,${ '$3':toDate('ddMMMyyyy'):format('yyyy/MM/dd') }");
        final String csvIn = "2006,10-01-2004,10may2004\n" + ("2007,15-05-2006,10jun2005\r\n" + "2009,8-8-2008,10aug2008");
        final String expectedCsvOut = "2006,10-01-2004,2004/05/10\n" + ("2007,15-05-2006,2005/06/10\r\n" + "2009,8-8-2008,2008/08/10");
        runner.enqueue(csvIn.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(expectedCsvOut);
    }

    @Test
    public void testCapturingGroupInExpressionLanguage2() {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(SEARCH_VALUE, "(.*)/(.*?).jpg");
        runner.setProperty(REPLACEMENT_VALUE, "$1/${ '$2':substring(0,1) }.png");
        final String csvIn = "1,2,3,https://123.jpg,email@mydomain.com\n" + "3,2,1,https://321.jpg,other.email@mydomain.com";
        final String expectedCsvOut = "1,2,3,https://1.png,email@mydomain.com\n" + "3,2,1,https://3.png,other.email@mydomain.com";
        runner.enqueue(csvIn.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals(expectedCsvOut);
    }

    @Test
    public void testAlwaysReplaceEntireText() {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, ENTIRE_TEXT);
        runner.setProperty(REPLACEMENT_STRATEGY, ALWAYS_REPLACE);
        runner.setProperty(SEARCH_VALUE, "i do not exist anywhere in the text");
        runner.setProperty(REPLACEMENT_VALUE, "${filename}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "abc.txt");
        runner.enqueue("Hello\nWorld!".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("abc.txt");
    }

    @Test
    public void testAlwaysReplaceLineByLine() {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, LINE_BY_LINE);
        runner.setProperty(REPLACEMENT_STRATEGY, ALWAYS_REPLACE);
        runner.setProperty(SEARCH_VALUE, "i do not exist anywhere in the text");
        runner.setProperty(REPLACEMENT_VALUE, "${filename}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("filename", "abc.txt");
        runner.enqueue("Hello\nWorld!\r\ntoday!\n".getBytes(), attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("abc.txt\nabc.txt\r\nabc.txt\n");
    }

    @Test
    public void testRegexWithBadCaptureGroup() throws IOException {
        // Test the old Default Regex and with a custom Replacement Value that should fail because the
        // Perl regex "(?s:^.*$)" must be written "(?s)(^.*$)" in Java for there to be a capture group.
        // private static final String DEFAULT_REGEX = "(?s:^.*$)";
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(?s:^.*$)");
        runner.setProperty(REPLACEMENT_VALUE, "${'$1':toUpper()}");// should uppercase group but there is none

        runner.setProperty(REPLACEMENT_STRATEGY, REGEX_REPLACE);
        runner.setProperty(EVALUATION_MODE, ENTIRE_TEXT);
        runner.enqueue("testing\n123".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("");
    }

    @Test
    public void testRegexWithGoodCaptureGroup() throws IOException {
        // Test the new Default Regex and with a custom Replacement Values that should succeed.
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(?s)(^.*$)");
        runner.setProperty(REPLACEMENT_VALUE, "${'$1':toUpper()}");// will uppercase group with good Java regex

        runner.setProperty(REPLACEMENT_STRATEGY, REGEX_REPLACE);
        runner.setProperty(EVALUATION_MODE, ENTIRE_TEXT);
        runner.enqueue("testing\n123".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("TESTING\n123");
    }

    @Test
    public void testRegexNoCaptureDefaultReplacement() throws IOException {
        // Test the old Default Regex and new Default Regex with the default replacement.  This should fail
        // because the regex does not create a capture group.
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(?s:^.*$)");
        runner.setProperty(REPLACEMENT_VALUE, "$1");
        runner.setProperty(REPLACEMENT_STRATEGY, REGEX_REPLACE);
        runner.setProperty(EVALUATION_MODE, ENTIRE_TEXT);
        exception.expect(AssertionError.class);
        exception.expectMessage("java.lang.IndexOutOfBoundsException: No group 1");
        runner.enqueue("testing\n123".getBytes());
        runner.run();
    }

    @Test
    public void testProcessorConfigurationRegexNotValid() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(?<!\\),*");
        runner.setProperty(REPLACEMENT_VALUE, "hello");
        runner.setProperty(REPLACEMENT_STRATEGY, REGEX_REPLACE);
        runner.setProperty(EVALUATION_MODE, ENTIRE_TEXT);
        runner.assertNotValid();
        runner.setProperty(REPLACEMENT_STRATEGY, LITERAL_REPLACE);
        runner.assertValid();
        runner.enqueue("(?<!\\),*".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("hello");
        runner.setProperty(SEARCH_VALUE, "");
        runner.assertNotValid();
        runner.setProperty(REPLACEMENT_STRATEGY, APPEND);
        runner.assertValid();
        runner.setProperty(REPLACEMENT_STRATEGY, PREPEND);
        runner.assertValid();
        runner.setProperty(REPLACEMENT_STRATEGY, ALWAYS_REPLACE);
        runner.assertValid();
    }

    @Test
    public void testBackReferenceEscapeWithRegexReplaceUsingEL() throws Exception {
        final TestRunner runner = getRunner();
        runner.setProperty(SEARCH_VALUE, "(?s)(^.*$)");
        runner.setProperty(REPLACEMENT_VALUE, "${'$1':toUpper()}");
        runner.setProperty(REPLACEMENT_STRATEGY, REGEX_REPLACE);
        runner.setProperty(EVALUATION_MODE, ENTIRE_TEXT);
        runner.assertValid();
        runner.enqueue("wo$rd".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        out.assertContentEquals("WO$RD");
        runner.enqueue("wo$1rd".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 2);
        out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(1);
        out.assertContentEquals("WO$1RD");
        runner.enqueue("wo$1r$2d".getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 3);
        out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(2);
        out.assertContentEquals("WO$1R$2D");
    }

    /* A repeated alternation regex such as (A|B)* can lead to StackOverflowError
    on large input strings.
     */
    @Test
    public void testForStackOverflow() throws Exception {
        final TestRunner runner = getRunner();
        runner.setProperty(REPLACEMENT_VALUE, "New text");
        runner.setProperty(REPLACEMENT_STRATEGY, REGEX_REPLACE);
        runner.setProperty(EVALUATION_MODE, ENTIRE_TEXT);
        runner.setProperty(MAX_BUFFER_SIZE, "10 MB");
        runner.setProperty(SEARCH_VALUE, "(?s)(^(A|B)*$)");
        runner.assertValid();
        char[] data = new char[1000000];
        Arrays.fill(data, 'A');
        runner.enqueue(new String(data));
        runner.run();
        // we want the large file to fail, rather than rollback and yield
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    /**
     * Related to
     * <a href="https://issues.apache.org/jira/browse/NIFI-5761">NIFI-5761</a>. It
     * verifies that if a runtime exception is raised during replace text
     * evaluation, it sends the error to failure relationship.
     */
    @Test
    public void testWithInvalidExpression() {
        final TestRunner runner = getRunner();
        runner.setProperty(EVALUATION_MODE, ENTIRE_TEXT);
        runner.setProperty(SEARCH_VALUE, ".*");
        runner.setProperty(REPLACEMENT_VALUE, "${date:toDate(\"yyyy/MM/dd\")}");
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("date", "12");
        runner.enqueue("hi", attributes);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        final String outContent = translateNewLines(new String(out.toByteArray(), StandardCharsets.UTF_8));
        Assert.assertTrue(outContent.equals("hi"));
    }
}

