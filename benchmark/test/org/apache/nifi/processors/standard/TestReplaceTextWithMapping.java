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


import ReplaceTextWithMapping.MAPPING_FILE;
import ReplaceTextWithMapping.MATCHING_GROUP_FOR_LOOKUP_KEY;
import ReplaceTextWithMapping.MAX_BUFFER_SIZE;
import ReplaceTextWithMapping.REGEX;
import ReplaceTextWithMapping.REL_FAILURE;
import ReplaceTextWithMapping.REL_SUCCESS;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.junit.Assert;
import org.junit.Test;


public class TestReplaceTextWithMapping {
    @Test
    public void testSimple() throws IOException {
        final TestRunner runner = getRunner();
        final String mappingFile = Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-mapping.txt").toFile().getAbsolutePath();
        runner.setProperty(MAPPING_FILE, mappingFile);
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors-without-dashes.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "roses are apple\n" + (("violets are blueberry\n" + "something else is grape\n") + "I'm not good at writing poems");
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testExpressionLanguageInText() throws IOException {
        final TestRunner runner = getRunner();
        final String mappingFile = Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-mapping.txt").toFile().getAbsolutePath();
        runner.setProperty(MAPPING_FILE, mappingFile);
        String text = "${foo} red ${baz}";
        runner.enqueue(text.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "${foo} apple ${baz}";
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testExpressionLanguageInText2() throws IOException {
        final TestRunner runner = getRunner();
        final String mappingFile = Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-mapping.txt").toFile().getAbsolutePath();
        runner.setProperty(MAPPING_FILE, mappingFile);
        runner.setProperty(REGEX, "\\|(.*?)\\|");
        runner.setProperty(MATCHING_GROUP_FOR_LOOKUP_KEY, "1");
        String text = "${foo}|red|${baz}";
        runner.enqueue(text.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "${foo}|apple|${baz}";
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testExpressionLanguageInText3() throws IOException {
        final TestRunner runner = getRunner();
        final String mappingFile = Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-mapping.txt").toFile().getAbsolutePath();
        runner.setProperty(MAPPING_FILE, mappingFile);
        runner.setProperty(REGEX, ".*\\|(.*?)\\|.*");
        runner.setProperty(MATCHING_GROUP_FOR_LOOKUP_KEY, "1");
        String text = "${foo}|red|${baz}";
        runner.enqueue(text.getBytes());
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "${foo}|apple|${baz}";
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testWithMatchingGroupAndContext() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REGEX, "-(.*?)-");
        runner.setProperty(MATCHING_GROUP_FOR_LOOKUP_KEY, "1");
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-mapping.txt").toFile().getAbsolutePath());
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "-roses- are -apple-\n" + (("violets are -blueberry-\n" + "something else is -grape-\n") + "I'm not good at writing poems");
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testBackReference() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REGEX, "(\\S+)");
        runner.setProperty(MATCHING_GROUP_FOR_LOOKUP_KEY, "1");
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-backreference-mapping.txt").toFile().getAbsolutePath());
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors-without-dashes.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "roses are red apple\n" + (("violets are blue blueberry\n" + "something else is green grape\n") + "I'm not good at writing poems");
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testRoutesToFailureIfTooLarge() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REGEX, "[123]");
        runner.setProperty(MAX_BUFFER_SIZE, "1 b");
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-mapping.txt").toFile().getAbsolutePath());
        final Map<String, String> attributes = new HashMap<>();
        attributes.put("abc", "Good");
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
    }

    @Test
    public void testBackReferenceWithTooLargeOfIndexIsEscaped() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REGEX, "-(.*?)-");
        runner.setProperty(MATCHING_GROUP_FOR_LOOKUP_KEY, "1");
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-excessive-backreference-mapping.txt").toFile().getAbsolutePath());
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "-roses- are -red$2 apple-\n" + (("violets are -blue$2 blueberry-\n" + "something else is -green$2 grape-\n") + "I'm not good at writing poems");
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testBackReferenceWithTooLargeOfIndexIsEscapedSimple() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-excessive-backreference-mapping-simple.txt").toFile().getAbsolutePath());
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors-without-dashes.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "roses are red$1 apple\n" + (("violets are blue$1 blueberry\n" + "something else is green$1 grape\n") + "I'm not good at writing poems");
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testBackReferenceWithInvalidReferenceIsEscaped() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REGEX, "(\\S+)");
        runner.setProperty(MATCHING_GROUP_FOR_LOOKUP_KEY, "1");
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-invalid-backreference-mapping.txt").toFile().getAbsolutePath());
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors-without-dashes.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "roses are red$d apple\n" + (("violets are blue$d blueberry\n" + "something else is green$d grape\n") + "I'm not good at writing poems");
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testEscapingDollarSign() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REGEX, "-(.*?)-");
        runner.setProperty(MATCHING_GROUP_FOR_LOOKUP_KEY, "1");
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-escaped-dollar-mapping.txt").toFile().getAbsolutePath());
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "-roses- are -$1 apple-\n" + (("violets are -$1 blueberry-\n" + "something else is -$1 grape-\n") + "I'm not good at writing poems");
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testEscapingDollarSignSimple() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-escaped-dollar-mapping.txt").toFile().getAbsolutePath());
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors-without-dashes.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "roses are $1 apple\n" + (("violets are $1 blueberry\n" + "something else is $1 grape\n") + "I'm not good at writing poems");
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testReplaceWithEmptyString() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-blank-mapping.txt").toFile().getAbsolutePath());
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors-without-dashes.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "roses are \n" + (("violets are \n" + "something else is \n") + "I'm not good at writing poems");
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testReplaceWithSpaceInString() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-space-mapping.txt").toFile().getAbsolutePath());
        runner.enqueue(Paths.get("src/test/resources/TestReplaceTextWithMapping/colors-without-dashes.txt"));
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = "roses are really red\n" + (("violets are super blue\n" + "something else is ultra green\n") + "I'm not good at writing poems");
        Assert.assertEquals(expected, outputString);
    }

    @Test
    public void testWithNoMatch() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REGEX, "-(.*?)-");
        runner.setProperty(MATCHING_GROUP_FOR_LOOKUP_KEY, "1");
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-fruit-no-match-mapping.txt").toFile().getAbsolutePath());
        final Path path = Paths.get("src/test/resources/TestReplaceTextWithMapping/colors.txt");
        runner.enqueue(path);
        runner.run();
        runner.assertAllFlowFilesTransferred(REL_SUCCESS, 1);
        final MockFlowFile out = runner.getFlowFilesForRelationship(REL_SUCCESS).get(0);
        String outputString = new String(out.toByteArray());
        String expected = new String(Files.readAllBytes(path));
        Assert.assertEquals(expected, outputString);
    }

    @Test(expected = AssertionError.class)
    public void testMatchingGroupForLookupKeyTooLarge() throws IOException {
        final TestRunner runner = getRunner();
        runner.setProperty(REGEX, "-(.*?)-");
        runner.setProperty(MATCHING_GROUP_FOR_LOOKUP_KEY, "2");
        runner.setProperty(MAPPING_FILE, Paths.get("src/test/resources/TestReplaceTextWithMapping/color-mapping.txt").toFile().getAbsolutePath());
        final Path path = Paths.get("src/test/resources/TestReplaceTextWithMapping/colors.txt");
        runner.enqueue(path);
        runner.run();
    }
}

