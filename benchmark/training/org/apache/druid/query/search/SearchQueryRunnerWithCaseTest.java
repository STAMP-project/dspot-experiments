/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.druid.query.search;


import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.druid.query.Druids;
import org.apache.druid.query.Druids.SearchQueryBuilder;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class SearchQueryRunnerWithCaseTest {
    private final QueryRunner runner;

    public SearchQueryRunnerWithCaseTest(QueryRunner runner) {
        this.runner = runner;
    }

    @Test
    public void testSearch() {
        Druids.SearchQueryBuilder builder = testBuilder();
        Map<String, Set<String>> expectedResults = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        SearchQuery searchQuery;
        searchQuery = builder.query("SPOT").build();
        expectedResults.put(QueryRunnerTestHelper.marketDimension, Sets.newHashSet("spot", "SPot"));
        checkSearchQuery(searchQuery, expectedResults);
        searchQuery = builder.query("spot", true).build();
        expectedResults.put(QueryRunnerTestHelper.marketDimension, Sets.newHashSet("spot"));
        checkSearchQuery(searchQuery, expectedResults);
        searchQuery = builder.query("SPot", true).build();
        expectedResults.put(QueryRunnerTestHelper.marketDimension, Sets.newHashSet("SPot"));
        checkSearchQuery(searchQuery, expectedResults);
    }

    @Test
    public void testSearchSameValueInMultiDims() {
        SearchQuery searchQuery;
        Druids.SearchQueryBuilder builder = testBuilder().dimensions(Arrays.asList(QueryRunnerTestHelper.placementDimension, QueryRunnerTestHelper.placementishDimension));
        Map<String, Set<String>> expectedResults = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        searchQuery = builder.query("PREFERRED").build();
        expectedResults.put(QueryRunnerTestHelper.placementDimension, Sets.newHashSet("PREFERRED", "preferred", "PREFERRed"));
        expectedResults.put(QueryRunnerTestHelper.placementishDimension, Sets.newHashSet("preferred", "Preferred"));
        checkSearchQuery(searchQuery, expectedResults);
        searchQuery = builder.query("preferred", true).build();
        expectedResults.put(QueryRunnerTestHelper.placementDimension, Sets.newHashSet("preferred"));
        expectedResults.put(QueryRunnerTestHelper.placementishDimension, Sets.newHashSet("preferred"));
        checkSearchQuery(searchQuery, expectedResults);
    }

    @Test
    public void testSearchIntervals() {
        SearchQuery searchQuery;
        Druids.SearchQueryBuilder builder = testBuilder().dimensions(Collections.singletonList(QueryRunnerTestHelper.qualityDimension)).intervals("2011-01-12T00:00:00.000Z/2011-01-13T00:00:00.000Z");
        Map<String, Set<String>> expectedResults = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        searchQuery = builder.query("otive").build();
        expectedResults.put(QueryRunnerTestHelper.qualityDimension, Sets.newHashSet("AutoMotive"));
        checkSearchQuery(searchQuery, expectedResults);
    }

    @Test
    public void testSearchNoOverrappingIntervals() {
        SearchQuery searchQuery;
        Druids.SearchQueryBuilder builder = testBuilder().dimensions(Collections.singletonList(QueryRunnerTestHelper.qualityDimension)).intervals("2011-01-10T00:00:00.000Z/2011-01-11T00:00:00.000Z");
        Map<String, Set<String>> expectedResults = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        searchQuery = builder.query("business").build();
        expectedResults.put(QueryRunnerTestHelper.qualityDimension, new HashSet<>());
        checkSearchQuery(searchQuery, expectedResults);
    }

    @Test
    public void testFragmentSearch() {
        Druids.SearchQueryBuilder builder = testBuilder();
        Map<String, Set<String>> expectedResults = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        SearchQuery searchQuery;
        searchQuery = builder.fragments(Arrays.asList("auto", "ve")).build();
        expectedResults.put(QueryRunnerTestHelper.qualityDimension, Sets.newHashSet("automotive", "AutoMotive"));
        checkSearchQuery(searchQuery, expectedResults);
        searchQuery = builder.fragments(Arrays.asList("auto", "ve"), true).build();
        expectedResults.put(QueryRunnerTestHelper.qualityDimension, Sets.newHashSet("automotive"));
        checkSearchQuery(searchQuery, expectedResults);
    }

    @Test
    public void testFallbackToCursorBasedPlan() {
        final SearchQueryBuilder builder = testBuilder();
        final SearchQuery query = builder.filters("qualityLong", "1000").build();
        final Map<String, Set<String>> expectedResults = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        expectedResults.put("qualityLong", Sets.newHashSet("1000"));
        expectedResults.put("qualityDouble", Sets.newHashSet("10000.0"));
        expectedResults.put("qualityFloat", Sets.newHashSet("10000.0"));
        expectedResults.put("qualityNumericString", Sets.newHashSet("100000"));
        expectedResults.put("quality", Sets.newHashSet("AutoMotive", "automotive"));
        expectedResults.put("placement", Sets.newHashSet("PREFERRED", "preferred"));
        expectedResults.put("placementish", Sets.newHashSet("a", "preferred"));
        expectedResults.put("market", Sets.newHashSet("spot"));
        checkSearchQuery(query, expectedResults);
    }
}

