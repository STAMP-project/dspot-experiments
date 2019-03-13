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


import RouteText.CONTAINS;
import RouteText.CONTAINS_REGULAR_EXPRESSION;
import RouteText.ENDS_WITH;
import RouteText.EQUALS;
import RouteText.GROUPING_REGEX;
import RouteText.GROUP_ATTRIBUTE_KEY;
import RouteText.IGNORE_CASE;
import RouteText.MATCHES_REGULAR_EXPRESSION;
import RouteText.MATCH_STRATEGY;
import RouteText.PATTERNS_CACHE_MAXIMUM_ENTRIES;
import RouteText.ROUTE_STRATEGY;
import RouteText.ROUTE_TO_MATCHED_WHEN_ALL_PROPERTIES_MATCH;
import RouteText.ROUTE_TO_MATCHED_WHEN_ANY_PROPERTY_MATCHES;
import RouteText.ROUTE_TO_MATCHING_PROPERTY_NAME;
import RouteText.SATISFIES_EXPRESSION;
import RouteText.STARTS_WITH;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.nifi.processor.Relationship;
import org.apache.nifi.util.MockFlowFile;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Assert;
import org.junit.Test;

import static RouteText.PATTERNS_CACHE_MAXIMUM_ENTRIES;


public class TestRouteText {
    @Test
    public void testRelationships() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ANY_PROPERTY_MATCHES);
        runner.setProperty("simple", "start");
        runner.run();
        Set<Relationship> relationshipSet = runner.getProcessor().getRelationships();
        Set<String> expectedRelationships = new HashSet<>(Arrays.asList("matched", "unmatched", "original"));
        Assert.assertEquals(expectedRelationships.size(), relationshipSet.size());
        for (Relationship relationship : relationshipSet) {
            Assert.assertTrue(expectedRelationships.contains(relationship.getName()));
        }
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHING_PROPERTY_NAME);
        relationshipSet = runner.getProcessor().getRelationships();
        expectedRelationships = new HashSet<>(Arrays.asList("simple", "unmatched", "original"));
        Assert.assertEquals(expectedRelationships.size(), relationshipSet.size());
        for (Relationship relationship : relationshipSet) {
            Assert.assertTrue(expectedRelationships.contains(relationship.getName()));
        }
        runner.run();
    }

    @Test
    public void testSeparationStrategyNotKnown() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.assertNotValid();
    }

    @Test
    public void testNotText() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.setProperty("simple", "start");
        Set<Relationship> relationshipSet = runner.getProcessor().getRelationships();
        Set<String> expectedRelationships = new HashSet<>(Arrays.asList("simple", "unmatched", "original"));
        Assert.assertEquals(expectedRelationships.size(), relationshipSet.size());
        for (Relationship relationship : relationshipSet) {
            Assert.assertTrue(expectedRelationships.contains(relationship.getName()));
        }
        runner.enqueue(Paths.get("src/test/resources/simple.jpg"));
        runner.run();
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outOriginal = runner.getFlowFilesForRelationship("original").get(0);
        outOriginal.assertContentEquals(Paths.get("src/test/resources/simple.jpg"));
    }

    @Test
    public void testInvalidRegex() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, MATCHES_REGULAR_EXPRESSION);
        runner.setProperty("simple", "[");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        try {
            runner.run();
            Assert.fail();
        } catch (AssertionError e) {
            // Expect to catch error asserting 'simple' as invalid
        }
    }

    @Test
    public void testSimpleDefaultStarts() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.setProperty("simple", "start");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleCaseSensitiveStartsMatch() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.setProperty(IGNORE_CASE, "false");
        runner.setProperty("simple", "start");
        runner.enqueue("STart middle end\nstart middle end".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("STart middle end\n".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleCaseInsensitiveStartsMatch() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.setProperty(IGNORE_CASE, "true");
        runner.setProperty("simple", "start");
        runner.enqueue("start middle end\nSTart middle end".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 0);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end\nSTart middle end".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleDefaultEnd() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, ENDS_WITH);
        runner.setProperty("simple", "end");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testRouteLineToMultipleRelationships() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, CONTAINS);
        runner.setProperty("t", "t");
        runner.setProperty("e", "e");
        runner.setProperty("z", "z");
        final String originalText = "start middle end\nnot match";
        runner.enqueue(originalText.getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("t", 1);
        runner.assertTransferCount("e", 1);
        runner.assertTransferCount("z", 0);
        runner.assertTransferCount("unmatched", 0);
        runner.assertTransferCount("original", 1);
        runner.getFlowFilesForRelationship("t").get(0).assertContentEquals(originalText);
        runner.getFlowFilesForRelationship("e").get(0).assertContentEquals("start middle end\n");
        runner.getFlowFilesForRelationship("z").isEmpty();
        runner.getFlowFilesForRelationship("original").get(0).assertContentEquals(originalText);
    }

    @Test
    public void testGroupSameRelationship() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, CONTAINS);
        runner.setProperty(GROUPING_REGEX, "(.*?),.*");
        runner.setProperty("o", "o");
        final String originalText = "1,hello\n2,world\n1,good-bye";
        runner.enqueue(originalText.getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("o", 2);
        runner.assertTransferCount("unmatched", 0);
        runner.assertTransferCount("original", 1);
        final List<MockFlowFile> list = runner.getFlowFilesForRelationship("o");
        boolean found1 = false;
        boolean found2 = false;
        for (final MockFlowFile mff : list) {
            if (mff.getAttribute(GROUP_ATTRIBUTE_KEY).equals("1")) {
                mff.assertContentEquals("1,hello\n1,good-bye");
                found1 = true;
            } else {
                mff.assertAttributeEquals(GROUP_ATTRIBUTE_KEY, "2");
                mff.assertContentEquals("2,world\n");
                found2 = true;
            }
        }
        Assert.assertTrue(found1);
        Assert.assertTrue(found2);
    }

    @Test
    public void testMultipleGroupsSameRelationship() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, CONTAINS);
        runner.setProperty(GROUPING_REGEX, "(.*?),(.*?),.*");
        runner.setProperty("o", "o");
        final String originalText = "1,5,hello\n2,5,world\n1,8,good-bye\n1,5,overt";
        runner.enqueue(originalText.getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("o", 3);
        runner.assertTransferCount("unmatched", 0);
        runner.assertTransferCount("original", 1);
        final List<MockFlowFile> list = runner.getFlowFilesForRelationship("o");
        boolean found1 = false;
        boolean found2 = false;
        boolean found3 = false;
        for (final MockFlowFile mff : list) {
            if (mff.getAttribute(GROUP_ATTRIBUTE_KEY).equals("1, 5")) {
                mff.assertContentEquals("1,5,hello\n1,5,overt");
                found1 = true;
            } else
                if (mff.getAttribute(GROUP_ATTRIBUTE_KEY).equals("2, 5")) {
                    mff.assertContentEquals("2,5,world\n");
                    found2 = true;
                } else {
                    mff.assertAttributeEquals(GROUP_ATTRIBUTE_KEY, "1, 8");
                    mff.assertContentEquals("1,8,good-bye\n");
                    found3 = true;
                }

        }
        Assert.assertTrue(found1);
        Assert.assertTrue(found2);
        Assert.assertTrue(found3);
    }

    @Test
    public void testGroupDifferentRelationships() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, CONTAINS);
        runner.setProperty(GROUPING_REGEX, "(.*?),.*");
        runner.setProperty("l", "l");
        final String originalText = "1,hello\n2,world\n1,good-bye\n3,ciao";
        runner.enqueue(originalText.getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("l", 2);
        runner.assertTransferCount("unmatched", 2);
        runner.assertTransferCount("original", 1);
        List<MockFlowFile> lFlowFiles = runner.getFlowFilesForRelationship("l");
        boolean found1 = false;
        boolean found2 = false;
        for (final MockFlowFile mff : lFlowFiles) {
            if (mff.getAttribute(GROUP_ATTRIBUTE_KEY).equals("1")) {
                mff.assertContentEquals("1,hello\n");
                found1 = true;
            } else {
                mff.assertAttributeEquals(GROUP_ATTRIBUTE_KEY, "2");
                mff.assertContentEquals("2,world\n");
                found2 = true;
            }
        }
        Assert.assertTrue(found1);
        Assert.assertTrue(found2);
        List<MockFlowFile> unmatchedFlowFiles = runner.getFlowFilesForRelationship("unmatched");
        found1 = false;
        boolean found3 = false;
        for (final MockFlowFile mff : unmatchedFlowFiles) {
            if (mff.getAttribute(GROUP_ATTRIBUTE_KEY).equals("1")) {
                mff.assertContentEquals("1,good-bye\n");
                found1 = true;
            } else {
                mff.assertAttributeEquals(GROUP_ATTRIBUTE_KEY, "3");
                mff.assertContentEquals("3,ciao");
                found3 = true;
            }
        }
        Assert.assertTrue(found1);
        Assert.assertTrue(found3);
    }

    @Test
    public void testSimpleDefaultContains() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, CONTAINS);
        runner.setProperty("simple", "middle");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleContainsIgnoreCase() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, CONTAINS);
        runner.setProperty(IGNORE_CASE, "true");
        runner.setProperty("simple", "miDDlE");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleDefaultEquals() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, EQUALS);
        runner.setProperty("simple", "start middle end");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleDefaultMatchRegularExpression() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, MATCHES_REGULAR_EXPRESSION);
        runner.setProperty("simple", ".*(mid).*");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleDefaultContainRegularExpression() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, CONTAINS_REGULAR_EXPRESSION);
        runner.setProperty("simple", "(m.d)");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    /* ------------------------------------------------------ */
    @Test
    public void testSimpleAnyStarts() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ANY_PROPERTY_MATCHES);
        runner.setProperty("simple", "start");
        runner.setProperty("no", "no match");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("matched", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("matched").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleAnyEnds() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, ENDS_WITH);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ANY_PROPERTY_MATCHES);
        runner.setProperty("simple", "end");
        runner.setProperty("no", "no match");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("matched", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("matched").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleAnyEquals() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, EQUALS);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ANY_PROPERTY_MATCHES);
        runner.setProperty("simple", "start middle end");
        runner.setProperty("no", "no match");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("matched", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("matched").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleAnyMatchRegularExpression() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, MATCHES_REGULAR_EXPRESSION);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ANY_PROPERTY_MATCHES);
        runner.setProperty("simple", ".*(m.d).*");
        runner.setProperty("no", "no match");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("matched", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("matched").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleAnyContainRegularExpression() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, CONTAINS_REGULAR_EXPRESSION);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ANY_PROPERTY_MATCHES);
        runner.setProperty("simple", "(m.d)");
        runner.setProperty("no", "no match");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("matched", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("matched").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    /* ------------------------------------------------------ */
    @Test
    public void testSimpleAllStarts() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ALL_PROPERTIES_MATCH);
        runner.setProperty("simple", "start middle");
        runner.setProperty("second", "star");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("matched", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("matched").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleAllEnds() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, ENDS_WITH);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ALL_PROPERTIES_MATCH);
        runner.setProperty("simple", "middle end");
        runner.setProperty("second", "nd");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("matched", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("matched").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleAllEquals() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, EQUALS);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ALL_PROPERTIES_MATCH);
        runner.setProperty("simple", "start middle end");
        runner.setProperty("second", "start middle end");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("matched", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("matched").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleAllMatchRegularExpression() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, MATCHES_REGULAR_EXPRESSION);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ALL_PROPERTIES_MATCH);
        runner.setProperty("simple", ".*(m.d).*");
        runner.setProperty("second", ".*(t.*m).*");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("matched", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("matched").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSimpleAllContainRegularExpression() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, CONTAINS_REGULAR_EXPRESSION);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHED_WHEN_ALL_PROPERTIES_MATCH);
        runner.setProperty("simple", "(m.d)");
        runner.setProperty("second", "(t.*m)");
        runner.enqueue("start middle end\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("matched", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("matched").get(0);
        outMatched.assertContentEquals("start middle end\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testRouteOnPropertiesStartsWindowsNewLine() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.setProperty("simple", "start");
        runner.enqueue("start middle end\r\nnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end\r\n".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testRouteOnPropertiesStartsJustCarriageReturn() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.setProperty("simple", "start");
        runner.enqueue("start middle end\rnot match".getBytes("UTF-8"));
        runner.run();
        runner.assertTransferCount("simple", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        final MockFlowFile outMatched = runner.getFlowFilesForRelationship("simple").get(0);
        outMatched.assertContentEquals("start middle end\r".getBytes("UTF-8"));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        outUnmatched.assertContentEquals("not match".getBytes("UTF-8"));
    }

    @Test
    public void testSatisfiesExpression() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, SATISFIES_EXPRESSION);
        runner.setProperty("empty", "${incomplete expression");
        runner.assertNotValid();
        runner.setProperty("empty", "${line:isEmpty()}");
        runner.setProperty("third-line", "${lineNo:equals(3)}");
        runner.setProperty("second-field-you", "${line:getDelimitedField(2):trim():equals('you')}");
        runner.enqueue("hello\n\ngood-bye, you\n    \t\t\n");
        runner.run();
        runner.assertTransferCount("empty", 1);
        runner.assertTransferCount("third-line", 1);
        runner.assertTransferCount("second-field-you", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        runner.getFlowFilesForRelationship("empty").get(0).assertContentEquals("\n    \t\t\n");
        runner.getFlowFilesForRelationship("third-line").get(0).assertContentEquals("good-bye, you\n");
        runner.getFlowFilesForRelationship("second-field-you").get(0).assertContentEquals("good-bye, you\n");
    }

    @Test
    public void testJson() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, STARTS_WITH);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHING_PROPERTY_NAME);
        runner.setProperty("greeting", "\"greeting\"");
        runner.setProperty("address", "\"address\"");
        runner.enqueue(Paths.get("src/test/resources/TestJson/json-sample.json"));
        runner.run();
        runner.assertTransferCount("greeting", 1);
        runner.assertTransferCount("address", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        // Verify text is trimmed
        final MockFlowFile outGreeting = runner.getFlowFilesForRelationship("greeting").get(0);
        String outGreetingString = new String(runner.getContentAsByteArray(outGreeting));
        Assert.assertEquals(7, TestRouteText.countLines(outGreetingString));
        final MockFlowFile outAddress = runner.getFlowFilesForRelationship("address").get(0);
        String outAddressString = new String(runner.getContentAsByteArray(outAddress));
        Assert.assertEquals(7, TestRouteText.countLines(outAddressString));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        String outUnmatchedString = new String(runner.getContentAsByteArray(outUnmatched));
        Assert.assertEquals(400, TestRouteText.countLines(outUnmatchedString));
        final MockFlowFile outOriginal = runner.getFlowFilesForRelationship("original").get(0);
        outOriginal.assertContentEquals(Paths.get("src/test/resources/TestJson/json-sample.json"));
    }

    @Test
    public void testXml() throws IOException {
        final TestRunner runner = TestRunners.newTestRunner(new RouteText());
        runner.setProperty(MATCH_STRATEGY, CONTAINS);
        runner.setProperty(ROUTE_STRATEGY, ROUTE_TO_MATCHING_PROPERTY_NAME);
        runner.setProperty("NodeType", "name=\"NodeType\"");
        runner.setProperty("element", "<xs:element");
        runner.setProperty("name", "name=");
        runner.enqueue(Paths.get("src/test/resources/TestXml/XmlBundle.xsd"));
        runner.run();
        runner.assertTransferCount("NodeType", 1);
        runner.assertTransferCount("element", 1);
        runner.assertTransferCount("name", 1);
        runner.assertTransferCount("unmatched", 1);
        runner.assertTransferCount("original", 1);
        // Verify text is trimmed
        final MockFlowFile outNode = runner.getFlowFilesForRelationship("NodeType").get(0);
        String outNodeString = new String(runner.getContentAsByteArray(outNode));
        Assert.assertEquals(1, TestRouteText.countLines(outNodeString));
        final MockFlowFile outElement = runner.getFlowFilesForRelationship("element").get(0);
        String outElementString = new String(runner.getContentAsByteArray(outElement));
        Assert.assertEquals(4, TestRouteText.countLines(outElementString));
        final MockFlowFile outName = runner.getFlowFilesForRelationship("name").get(0);
        String outNameString = new String(runner.getContentAsByteArray(outName));
        Assert.assertEquals(7, TestRouteText.countLines(outNameString));
        final MockFlowFile outUnmatched = runner.getFlowFilesForRelationship("unmatched").get(0);
        String outUnmatchedString = new String(runner.getContentAsByteArray(outUnmatched));
        Assert.assertEquals(26, TestRouteText.countLines(outUnmatchedString));
        final MockFlowFile outOriginal = runner.getFlowFilesForRelationship("original").get(0);
        outOriginal.assertContentEquals(Paths.get("src/test/resources/TestXml/XmlBundle.xsd"));
    }

    @Test
    public void testPatternCache() throws IOException {
        final RouteText routeText = new RouteText();
        final TestRunner runner = TestRunners.newTestRunner(routeText);
        runner.setProperty(MATCH_STRATEGY, MATCHES_REGULAR_EXPRESSION);
        runner.setProperty("simple", ".*(${someValue}).*");
        runner.enqueue("some text", ImmutableMap.of("someValue", "a value"));
        runner.enqueue("some other text", ImmutableMap.of("someValue", "a value"));
        runner.run(2);
        Assert.assertEquals(("Expected 1 elements in the cache for the patterns, got" + (routeText.patternsCache.size())), 1, routeText.patternsCache.size());
        for (int i = 0; i < ((PATTERNS_CACHE_MAXIMUM_ENTRIES) * 2); ++i) {
            String iString = Long.toString(i);
            runner.enqueue((("some text with " + iString) + "in it"), ImmutableMap.of("someValue", iString));
            runner.run();
        }
        Assert.assertEquals(((("Expected " + (PATTERNS_CACHE_MAXIMUM_ENTRIES)) + " elements in the cache for the patterns, got") + (routeText.patternsCache.size())), PATTERNS_CACHE_MAXIMUM_ENTRIES, routeText.patternsCache.size());
        runner.assertTransferCount("simple", ((PATTERNS_CACHE_MAXIMUM_ENTRIES) * 2));
        runner.assertTransferCount("unmatched", 2);
        runner.assertTransferCount("original", (((PATTERNS_CACHE_MAXIMUM_ENTRIES) * 2) + 2));
        runner.setProperty(IGNORE_CASE, "true");
        Assert.assertEquals("Pattern cache is not cleared after changing IGNORE_CASE", 0, routeText.patternsCache.size());
    }
}

