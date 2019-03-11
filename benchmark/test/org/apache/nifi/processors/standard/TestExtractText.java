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


import ExtractText.CANON_EQ;
import ExtractText.CASE_INSENSITIVE;
import ExtractText.COMMENTS;
import ExtractText.DOTALL;
import ExtractText.ENABLE_REPEATING_CAPTURE_GROUP;
import ExtractText.INCLUDE_CAPTURE_GROUP_ZERO;
import ExtractText.LITERAL;
import ExtractText.MAX_BUFFER_SIZE;
import ExtractText.MULTILINE;
import ExtractText.REL_MATCH;
import ExtractText.REL_NO_MATCH;
import ExtractText.UNICODE_CASE;
import ExtractText.UNICODE_CHARACTER_CLASS;
import ExtractText.UNIX_LINES;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestExtractText {
    final String SAMPLE_STRING = "foo\r\nbar1\r\nbar2\r\nbar3\r\nhello\r\nworld\r\n";

    @Test
    public void testProcessor() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty("regex.result1", "(?s)(.*)");
        testRunner.setProperty("regex.result2", "(?s).*(bar1).*");
        testRunner.setProperty("regex.result3", "(?s).*?(bar\\d).*");// reluctant gets first

        testRunner.setProperty("regex.result4", "(?s).*?(?:bar\\d).*?(bar\\d).*?(bar3).*");// reluctant w/ repeated pattern gets second

        testRunner.setProperty("regex.result5", "(?s).*(bar\\d).*");// greedy gets last

        testRunner.setProperty("regex.result6", "(?s)^(.*)$");
        testRunner.setProperty("regex.result7", "(?s)(XXX)");
        testRunner.enqueue(SAMPLE_STRING.getBytes("UTF-8"));
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        out.assertAttributeEquals("regex.result1", SAMPLE_STRING);
        out.assertAttributeEquals("regex.result2", "bar1");
        out.assertAttributeEquals("regex.result3", "bar1");
        out.assertAttributeEquals("regex.result4", "bar2");
        out.assertAttributeEquals("regex.result4.0", SAMPLE_STRING);
        out.assertAttributeEquals("regex.result4.1", "bar2");
        out.assertAttributeEquals("regex.result4.2", "bar3");
        out.assertAttributeNotExists("regex.result4.3");
        out.assertAttributeEquals("regex.result5", "bar3");
        out.assertAttributeEquals("regex.result6", SAMPLE_STRING);
        out.assertAttributeEquals("regex.result7", null);
    }

    @Test
    public void testWithUnmatchedOptionalCapturingGroup() {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty("regex", "abc(def)?(g)");
        testRunner.enqueue("abcg");
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        out.assertAttributeNotExists("regex.1");
        out.assertAttributeEquals("regex.2", "g");
        testRunner.clearTransferState();
        testRunner.enqueue("abcdefg");
        testRunner.run();
        final MockFlowFile out2 = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        out2.assertAttributeEquals("regex.1", "def");
        out2.assertAttributeEquals("regex.2", "g");
    }

    @Test
    public void testProcessorWithDotall() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty(DOTALL, "true");
        testRunner.setProperty("regex.result1", "(.*)");
        testRunner.setProperty("regex.result2", ".*(bar1).*");
        testRunner.setProperty("regex.result3", ".*?(bar\\d).*");// reluctant gets first

        testRunner.setProperty("regex.result4", ".*?(?:bar\\d).*?(bar\\d).*");// reluctant w/ repeated pattern gets second

        testRunner.setProperty("regex.result5", ".*(bar\\d).*");// greedy gets last

        testRunner.setProperty("regex.result6", "^(.*)$");
        testRunner.setProperty("regex.result7", "^(XXX)$");
        testRunner.enqueue(SAMPLE_STRING.getBytes("UTF-8"));
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        out.assertAttributeEquals("regex.result1", SAMPLE_STRING);
        out.assertAttributeEquals("regex.result2", "bar1");
        out.assertAttributeEquals("regex.result3", "bar1");
        out.assertAttributeEquals("regex.result4", "bar2");
        out.assertAttributeEquals("regex.result5", "bar3");
        out.assertAttributeEquals("regex.result6", SAMPLE_STRING);
        out.assertAttributeEquals("regex.result7", null);
    }

    @Test
    public void testProcessorWithMultiline() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty(MULTILINE, "true");
        testRunner.setProperty("regex.result1", "(.*)");
        testRunner.setProperty("regex.result2", "(bar1)");
        testRunner.setProperty("regex.result3", ".*?(bar\\d).*");
        testRunner.setProperty("regex.result4", ".*?(?:bar\\d).*?(bar\\d).*");
        testRunner.setProperty("regex.result4b", "bar\\d\\r\\n(bar\\d)");
        testRunner.setProperty("regex.result5", ".*(bar\\d).*");
        testRunner.setProperty("regex.result5b", "(?:bar\\d\\r?\\n)*(bar\\d)");
        testRunner.setProperty("regex.result6", "^(.*)$");
        testRunner.setProperty("regex.result7", "^(XXX)$");
        testRunner.enqueue(SAMPLE_STRING.getBytes("UTF-8"));
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        out.assertAttributeEquals("regex.result1", "foo");// matches everything on the first line

        out.assertAttributeEquals("regex.result2", "bar1");
        out.assertAttributeEquals("regex.result3", "bar1");
        out.assertAttributeEquals("regex.result4", null);// null because no line has two bar's

        out.assertAttributeEquals("regex.result4b", "bar2");// included newlines in regex

        out.assertAttributeEquals("regex.result5", "bar1");// still gets first because no lines with multiple bar's

        out.assertAttributeEquals("regex.result5b", "bar3");// included newlines in regex

        out.assertAttributeEquals("regex.result6", "foo");// matches all of first line

        out.assertAttributeEquals("regex.result7", null);// no match

    }

    @Test
    public void testProcessorWithMultilineAndDotall() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty(MULTILINE, "true");
        testRunner.setProperty(DOTALL, "true");
        testRunner.setProperty("regex.result1", "(.*)");
        testRunner.setProperty("regex.result2", "(bar1)");
        testRunner.setProperty("regex.result3", ".*?(bar\\d).*");
        testRunner.setProperty("regex.result4", ".*?(?:bar\\d).*?(bar\\d).*");
        testRunner.setProperty("regex.result4b", "bar\\d\\r\\n(bar\\d)");
        testRunner.setProperty("regex.result5", ".*(bar\\d).*");
        testRunner.setProperty("regex.result5b", "(?:bar\\d\\r?\\n)*(bar\\d)");
        testRunner.setProperty("regex.result6", "^(.*)$");
        testRunner.setProperty("regex.result7", "^(XXX)$");
        testRunner.enqueue(SAMPLE_STRING.getBytes("UTF-8"));
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        out.assertAttributeEquals("regex.result1", SAMPLE_STRING);
        out.assertAttributeEquals("regex.result2", "bar1");
        out.assertAttributeEquals("regex.result3", "bar1");
        out.assertAttributeEquals("regex.result4", "bar2");
        out.assertAttributeEquals("regex.result4b", "bar2");
        out.assertAttributeEquals("regex.result5", "bar3");
        out.assertAttributeEquals("regex.result5b", "bar3");
        out.assertAttributeEquals("regex.result6", SAMPLE_STRING);
        out.assertAttributeEquals("regex.result7", null);
    }

    @Test
    public void testProcessorWithNoMatches() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty(MULTILINE, "true");
        testRunner.setProperty(DOTALL, "true");
        testRunner.setProperty("regex.result2", "(bar1)");
        testRunner.setProperty("regex.result3", ".*?(bar\\d).*");
        testRunner.setProperty("regex.result4", ".*?(?:bar\\d).*?(bar\\d).*");
        testRunner.setProperty("regex.result4b", "bar\\d\\r\\n(bar\\d)");
        testRunner.setProperty("regex.result5", ".*(bar\\d).*");
        testRunner.setProperty("regex.result5b", "(?:bar\\d\\r?\\n)*(bar\\d)");
        testRunner.setProperty("regex.result7", "^(XXX)$");
        testRunner.enqueue("YYY".getBytes("UTF-8"));
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_NO_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_NO_MATCH).get(0);
        out.assertAttributeEquals("regex.result1", null);
        out.assertAttributeEquals("regex.result2", null);
        out.assertAttributeEquals("regex.result3", null);
        out.assertAttributeEquals("regex.result4", null);
        out.assertAttributeEquals("regex.result4b", null);
        out.assertAttributeEquals("regex.result5", null);
        out.assertAttributeEquals("regex.result5b", null);
        out.assertAttributeEquals("regex.result6", null);
        out.assertAttributeEquals("regex.result7", null);
    }

    @Test
    public void testNoFlowFile() throws UnsupportedEncodingException {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 0);
    }

    @Test
    public void testMatchOutsideBuffer() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty(MAX_BUFFER_SIZE, "3 B");// only read the first 3 chars ("foo")

        testRunner.setProperty("regex.result1", "(foo)");
        testRunner.setProperty("regex.result2", "(world)");
        testRunner.enqueue(SAMPLE_STRING.getBytes("UTF-8"));
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        out.assertAttributeEquals("regex.result1", "foo");
        out.assertAttributeEquals("regex.result2", null);// null because outsk

    }

    @Test
    public void testGetCompileFlags() {
        final ExtractText processor = new ExtractText();
        TestRunner testRunner;
        int flags;
        // NONE
        testRunner = TestRunners.newTestRunner(processor);
        flags = processor.getCompileFlags(testRunner.getProcessContext());
        Assert.assertEquals(0, flags);
        // UNIX_LINES
        testRunner = TestRunners.newTestRunner(processor);
        testRunner.setProperty(UNIX_LINES, "true");
        Assert.assertEquals(Pattern.UNIX_LINES, processor.getCompileFlags(testRunner.getProcessContext()));
        // CASE_INSENSITIVE
        testRunner = TestRunners.newTestRunner(processor);
        testRunner.setProperty(CASE_INSENSITIVE, "true");
        Assert.assertEquals(Pattern.CASE_INSENSITIVE, processor.getCompileFlags(testRunner.getProcessContext()));
        // COMMENTS
        testRunner = TestRunners.newTestRunner(processor);
        testRunner.setProperty(COMMENTS, "true");
        Assert.assertEquals(Pattern.COMMENTS, processor.getCompileFlags(testRunner.getProcessContext()));
        // MULTILINE
        testRunner = TestRunners.newTestRunner(processor);
        testRunner.setProperty(MULTILINE, "true");
        Assert.assertEquals(Pattern.MULTILINE, processor.getCompileFlags(testRunner.getProcessContext()));
        // LITERAL
        testRunner = TestRunners.newTestRunner(processor);
        testRunner.setProperty(LITERAL, "true");
        Assert.assertEquals(Pattern.LITERAL, processor.getCompileFlags(testRunner.getProcessContext()));
        // DOTALL
        testRunner = TestRunners.newTestRunner(processor);
        testRunner.setProperty(DOTALL, "true");
        Assert.assertEquals(Pattern.DOTALL, processor.getCompileFlags(testRunner.getProcessContext()));
        // UNICODE_CASE
        testRunner = TestRunners.newTestRunner(processor);
        testRunner.setProperty(UNICODE_CASE, "true");
        Assert.assertEquals(Pattern.UNICODE_CASE, processor.getCompileFlags(testRunner.getProcessContext()));
        // CANON_EQ
        testRunner = TestRunners.newTestRunner(processor);
        testRunner.setProperty(CANON_EQ, "true");
        Assert.assertEquals(Pattern.CANON_EQ, processor.getCompileFlags(testRunner.getProcessContext()));
        // UNICODE_CHARACTER_CLASS
        testRunner = TestRunners.newTestRunner(processor);
        testRunner.setProperty(UNICODE_CHARACTER_CLASS, "true");
        Assert.assertEquals(Pattern.UNICODE_CHARACTER_CLASS, processor.getCompileFlags(testRunner.getProcessContext()));
        // DOTALL and MULTILINE
        testRunner = TestRunners.newTestRunner(processor);
        testRunner.setProperty(DOTALL, "true");
        testRunner.setProperty(MULTILINE, "true");
        Assert.assertEquals(((Pattern.DOTALL) | (Pattern.MULTILINE)), processor.getCompileFlags(testRunner.getProcessContext()));
    }

    @Test
    public void testGetRelationShips() throws Exception {
        final ExtractText processor = new ExtractText();
        final TestRunner testRunner = TestRunners.newTestRunner(processor);
        testRunner.enqueue("foo".getBytes("UTF-8"));
        testRunner.run();
        Set<Relationship> relationships = processor.getRelationships();
        Assert.assertTrue(relationships.contains(REL_MATCH));
        Assert.assertTrue(relationships.contains(REL_NO_MATCH));
        Assert.assertEquals(2, relationships.size());
    }

    @Test
    public void testIncludeZeroCaptureGroupProperty() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        final String attributeKey = "regex.result";
        testRunner.setProperty(attributeKey, "(?s)(.*)");
        testRunner.enqueue(SAMPLE_STRING.getBytes("UTF-8"));
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        // Ensure the zero capture group is in the resultant attributes
        out.assertAttributeExists((attributeKey + ".0"));
        out.assertAttributeEquals(attributeKey, SAMPLE_STRING);
    }

    @Test
    public void testFindAll() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty(ENABLE_REPEATING_CAPTURE_GROUP, "true");
        final String attributeKey = "regex.result";
        testRunner.setProperty(attributeKey, "(?s)(\\w+)");
        testRunner.enqueue("This is my text".getBytes("UTF-8"));
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        // Ensure the zero capture group is in the resultant attributes
        out.assertAttributeExists((attributeKey + ".0"));
        out.assertAttributeExists((attributeKey + ".1"));
        out.assertAttributeExists((attributeKey + ".2"));
        out.assertAttributeExists((attributeKey + ".3"));
        out.assertAttributeExists((attributeKey + ".4"));
        out.assertAttributeEquals(attributeKey, "This");
        out.assertAttributeEquals((attributeKey + ".0"), "This");
        out.assertAttributeEquals((attributeKey + ".1"), "This");
        out.assertAttributeEquals((attributeKey + ".2"), "is");
        out.assertAttributeEquals((attributeKey + ".3"), "my");
        out.assertAttributeEquals((attributeKey + ".4"), "text");
    }

    @Test
    public void testFindAllPair() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty(ENABLE_REPEATING_CAPTURE_GROUP, "true");
        final String attributeKey = "regex.result";
        testRunner.setProperty(attributeKey, "(\\w+)=(\\d+)");
        testRunner.enqueue("a=1,b=10,c=100".getBytes("UTF-8"));
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        // Ensure the zero capture group is in the resultant attributes
        out.assertAttributeExists((attributeKey + ".0"));
        out.assertAttributeExists((attributeKey + ".1"));
        out.assertAttributeExists((attributeKey + ".2"));
        out.assertAttributeExists((attributeKey + ".3"));
        out.assertAttributeExists((attributeKey + ".4"));
        out.assertAttributeExists((attributeKey + ".5"));
        out.assertAttributeExists((attributeKey + ".6"));
        out.assertAttributeNotExists((attributeKey + ".7"));// Ensure there's no more attributes

        out.assertAttributeEquals(attributeKey, "a");
        out.assertAttributeEquals((attributeKey + ".0"), "a=1");
        out.assertAttributeEquals((attributeKey + ".1"), "a");
        out.assertAttributeEquals((attributeKey + ".2"), "1");
        out.assertAttributeEquals((attributeKey + ".3"), "b");
        out.assertAttributeEquals((attributeKey + ".4"), "10");
        out.assertAttributeEquals((attributeKey + ".5"), "c");
        out.assertAttributeEquals((attributeKey + ".6"), "100");
    }

    @Test
    public void testIgnoreZeroCaptureGroupProperty() throws Exception {
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty(INCLUDE_CAPTURE_GROUP_ZERO, "false");
        final String attributeKey = "regex.result";
        testRunner.setProperty(attributeKey, "(?s)(.*)");
        testRunner.enqueue(SAMPLE_STRING.getBytes("UTF-8"));
        testRunner.run();
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        // Ensure the zero capture group is not in the resultant attributes
        out.assertAttributeNotExists((attributeKey + ".0"));
        out.assertAttributeEquals(attributeKey, SAMPLE_STRING);
    }

    @Test
    public void testShouldAllowNoCaptureGroups() throws Exception {
        // Arrange
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        final String attributeKey = "regex.result";
        testRunner.setProperty(attributeKey, "(?s).*");
        // Act
        testRunner.enqueue(SAMPLE_STRING.getBytes("UTF-8"));
        testRunner.run();
        // Assert
        testRunner.assertAllFlowFilesTransferred(REL_MATCH, 1);
        final MockFlowFile out = testRunner.getFlowFilesForRelationship(REL_MATCH).get(0);
        // There is no global capture group, so only "key.0" exists
        out.assertAttributeNotExists(attributeKey);
        out.assertAttributeEquals((attributeKey + ".0"), SAMPLE_STRING);
    }

    @Test(expected = AssertionError.class)
    public void testShouldNotAllowNoCaptureGroupsIfZeroDisabled() throws Exception {
        // Arrange
        final TestRunner testRunner = TestRunners.newTestRunner(new ExtractText());
        testRunner.setProperty(INCLUDE_CAPTURE_GROUP_ZERO, "false");
        final String attributeKey = "regex.result";
        testRunner.setProperty(attributeKey, "(?s).*");
        // Act
        testRunner.enqueue(SAMPLE_STRING.getBytes("UTF-8"));
        // Validation should fail because nothing will match
        testRunner.run();
    }
}

