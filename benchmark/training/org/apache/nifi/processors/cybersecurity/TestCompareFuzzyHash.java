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
package org.apache.nifi.processors.cybersecurity;


import CompareFuzzyHash.ATTRIBUTE_NAME;
import CompareFuzzyHash.HASH_ALGORITHM;
import CompareFuzzyHash.HASH_LIST_FILE;
import CompareFuzzyHash.MATCHING_MODE;
import CompareFuzzyHash.MATCH_THRESHOLD;
import CompareFuzzyHash.REL_FAILURE;
import CompareFuzzyHash.REL_FOUND;
import CompareFuzzyHash.REL_NOT_FOUND;
import CompareFuzzyHash.allowableValueSSDEEP;
import CompareFuzzyHash.allowableValueTLSH;
import CompareFuzzyHash.multiMatch;
import CompareFuzzyHash.singleMatch;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.nifi.processors.cybersecurity.matchers.FuzzyHashMatcher;
import org.apache.nifi.processors.cybersecurity.matchers.SSDeepHashMatcher;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;


public class TestCompareFuzzyHash {
    String ssdeepInput = "48:c1xs8Z/m6H0eRH31S8p8bHENANkPrNy4tkPytwPyh2jTytxPythPytNdPytDgYyF:OuO/mg3HFSRHEb44RNMi6uHU2hcq3";

    String tlshInput = "EB519EA4A8F95171A2A409C1DEEB9872AF55C137E00A5289F1CCD0CE4F6CCD784BB4B7";

    final CompareFuzzyHash proc = new CompareFuzzyHash();

    private final TestRunner runner = TestRunners.newTestRunner(proc);

    @Test
    public void testSsdeepCompareFuzzyHash() {
        double matchingSimilarity = 80;
        runner.setProperty(HASH_ALGORITHM, allowableValueSSDEEP.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/ssdeep.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        runner.setProperty(MATCHING_MODE, singleMatch.getValue());
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", ssdeepInput);
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FOUND, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_FOUND).get(0);
        outFile.assertAttributeEquals("fuzzyhash.value.0.match", "\"nifi/nifi-nar-bundles/nifi-beats-bundle/nifi-beats-processors/pom.xml\"");
        double similarity = Double.valueOf(outFile.getAttribute("fuzzyhash.value.0.similarity"));
        Assert.assertTrue((similarity >= matchingSimilarity));
        outFile.assertAttributeNotExists("fuzzyhash.value.1.match");
    }

    @Test
    public void testSsdeepCompareFuzzyHashMultipleMatches() {
        double matchingSimilarity = 80;
        runner.setProperty(HASH_ALGORITHM, allowableValueSSDEEP.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/ssdeep.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        runner.setProperty(MATCHING_MODE, multiMatch.getValue());
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", ssdeepInput);
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FOUND, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_FOUND).get(0);
        outFile.assertAttributeEquals("fuzzyhash.value.0.match", "\"nifi/nifi-nar-bundles/nifi-lumberjack-bundle/nifi-lumberjack-processors/pom.xml\"");
        double similarity = Double.valueOf(outFile.getAttribute("fuzzyhash.value.0.similarity"));
        Assert.assertTrue((similarity >= matchingSimilarity));
        outFile.assertAttributeEquals("fuzzyhash.value.1.match", "\"nifi/nifi-nar-bundles/nifi-beats-bundle/nifi-beats-processors/pom.xml\"");
        similarity = Double.valueOf(outFile.getAttribute("fuzzyhash.value.1.similarity"));
        Assert.assertTrue((similarity >= matchingSimilarity));
    }

    @Test
    public void testSsdeepCompareFuzzyHashWithBlankHashList() {
        double matchingSimilarity = 80;
        runner.setProperty(HASH_ALGORITHM, allowableValueSSDEEP.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/blank_ssdeep.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", "6:hERjIfhRrlB63J0FDw1NBQmEH68xwMSELN:hZrlB62IwMS");
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_NOT_FOUND).get(0);
    }

    @Test
    public void testSsdeepCompareFuzzyHashWithInvalidHashList() {
        // This is different from "BlankHashList series of tests in that the file lacks headers and as such is totally
        // invalid
        double matchingSimilarity = 80;
        runner.setProperty(HASH_ALGORITHM, allowableValueSSDEEP.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/empty.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", "6:hERjIfhRrlB63J0FDw1NBQmEH68xwMSELN:hZrlB62IwMS");
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_NOT_FOUND).get(0);
        outFile.assertAttributeNotExists("fuzzyhash.value.0.match");
    }

    @Test
    public void testSsdeepCompareFuzzyHashWithInvalidHash() {
        double matchingSimilarity = 80;
        runner.setProperty(HASH_ALGORITHM, allowableValueSSDEEP.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/ssdeep.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        runner.setProperty(MATCHING_MODE, singleMatch.getValue());
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", "Test test test chocolate!");
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        outFile.assertAttributeNotExists("fuzzyhash.value.0.match");
    }

    @Test
    public void testTLSHCompareFuzzyHash() {
        double matchingSimilarity = 200;
        runner.setProperty(HASH_ALGORITHM, allowableValueTLSH.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/tlsh.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        runner.setProperty(MATCHING_MODE, singleMatch.getValue());
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", tlshInput);
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FOUND, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_FOUND).get(0);
        outFile.assertAttributeEquals("fuzzyhash.value.0.match", "nifi-nar-bundles/nifi-lumberjack-bundle/nifi-lumberjack-processors/pom.xml");
        double similarity = Double.valueOf(outFile.getAttribute("fuzzyhash.value.0.similarity"));
        Assert.assertTrue((similarity <= matchingSimilarity));
        outFile.assertAttributeNotExists("fuzzyhash.value.1.match");
    }

    @Test
    public void testTLSHCompareFuzzyHashMultipleMatches() {
        double matchingSimilarity = 200;
        runner.setProperty(HASH_ALGORITHM, allowableValueTLSH.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/tlsh.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        runner.setProperty(MATCHING_MODE, multiMatch.getValue());
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", tlshInput);
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FOUND, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_FOUND).get(0);
        outFile.assertAttributeEquals("fuzzyhash.value.0.match", "nifi-nar-bundles/nifi-lumberjack-bundle/nifi-lumberjack-processors/pom.xml");
        double similarity = Double.valueOf(outFile.getAttribute("fuzzyhash.value.0.similarity"));
        Assert.assertTrue((similarity <= matchingSimilarity));
        outFile.assertAttributeEquals("fuzzyhash.value.1.match", "nifi-nar-bundles/nifi-beats-bundle/nifi-beats-processors/pom.xml");
        similarity = Double.valueOf(outFile.getAttribute("fuzzyhash.value.1.similarity"));
        Assert.assertTrue((similarity <= matchingSimilarity));
    }

    @Test
    public void testTLSHCompareFuzzyHashWithBlankFile() {
        // This is different from "BlankHashList series of tests in that the file lacks headers and as such is totally
        // invalid
        double matchingSimilarity = 200;
        runner.setProperty(HASH_ALGORITHM, allowableValueTLSH.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/empty.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", "E2F0818B7AE7173906A72221570E30979B11C0FC47B518A1E89D257E2343CEC02381ED");
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_NOT_FOUND).get(0);
        outFile.assertAttributeNotExists("fuzzyhash.value.0.match");
    }

    @Test
    public void testTLSHCompareFuzzyHashWithEmptyHashList() {
        double matchingSimilarity = 200;
        runner.setProperty(HASH_ALGORITHM, allowableValueTLSH.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/empty.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", "E2F0818B7AE7173906A72221570E30979B11C0FC47B518A1E89D257E2343CEC02381ED");
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_NOT_FOUND, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_NOT_FOUND).get(0);
        outFile.assertAttributeNotExists("fuzzyhash.value.0.match");
    }

    @Test
    public void testTLSHCompareFuzzyHashWithInvalidHash() {
        double matchingSimilarity = 200;
        runner.setProperty(HASH_ALGORITHM, allowableValueTLSH.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/empty.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", "Test test test chocolate");
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        outFile.assertAttributeNotExists("fuzzyhash.value.0.match");
    }

    @Test
    public void testMissingAttribute() {
        double matchingSimilarity = 200;
        runner.setProperty(HASH_ALGORITHM, allowableValueTLSH.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/empty.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        runner.setProperty(MATCHING_MODE, multiMatch.getValue());
        runner.enqueue("bogus".getBytes());
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        outFile.assertAttributeNotExists("fuzzyhash.value.0.match");
    }

    @Test
    public void testAttributeIsEmptyString() {
        double matchingSimilarity = 200;
        runner.setProperty(HASH_ALGORITHM, allowableValueTLSH.getValue());
        runner.setProperty(ATTRIBUTE_NAME, "fuzzyhash.value");
        runner.setProperty(HASH_LIST_FILE, "src/test/resources/empty.list");
        runner.setProperty(MATCH_THRESHOLD, String.valueOf(matchingSimilarity));
        runner.setProperty(MATCHING_MODE, multiMatch.getValue());
        Map<String, String> attributes = new HashMap<>();
        attributes.put("fuzzyhash.value", "");
        runner.enqueue("bogus".getBytes(), attributes);
        runner.run();
        runner.assertQueueEmpty();
        runner.assertAllFlowFilesTransferred(REL_FAILURE, 1);
        final MockFlowFile outFile = runner.getFlowFilesForRelationship(REL_FAILURE).get(0);
        outFile.assertAttributeNotExists("fuzzyhash.value.0.match");
    }

    @Test
    public void testlooksLikeSpamSum() {
        FuzzyHashMatcher matcher = new SSDeepHashMatcher();
        List<String> invalidPayloads = // invalidFirstField
        // emptyFirstField
        // emptySecondField
        // emptyThirdField
        // withoutThirdField
        // Just a simple string
        Arrays.asList("4AD:c1xs8Z/m6H0eRH31S8p8bHENANkPrNy4tkPytwPyh2jTytxPythPytNdPytDgYyF:OuO/mg3HFSRHEb44RNMi6uHU2hcq3", ":c1xs8Z/m6H0eRH31S8p8bHENANkPrNy4tkPytwPyh2jTytxPythPytNdPytDgYyF:OuO/mg3HFSRHEb44RNMi6uHU2hcq3", "48::OuO/mg3HFSRHEb44RNMi6uHU2hcq3", "48:c1xs8Z/m6H0eRH31S8p8bHENANkPrNy4tkPytwPyh2jTytxPythPytNdPytDgYyF:", "48:c1xs8Z/m6H0eRH31S8p8bHENANkPrNy4tkPytwPyh2jTytxPythPytNdPytDgYyF", "c1xs8Z/m6H0eRH31S8p8bHENANkPrNy4tkPytwPyh2jTytxPythPytNdPytDgYyF");
        for (String item : invalidPayloads) {
            Assert.assertTrue((("item '" + item) + "' should have failed validation"), (!(matcher.isValidHash(item))));
        }
        // Now test with a valid string
        Assert.assertTrue(matcher.isValidHash(ssdeepInput));
    }
}

