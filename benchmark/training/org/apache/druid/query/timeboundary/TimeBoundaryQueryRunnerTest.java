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
package org.apache.druid.query.timeboundary;


import Result.MISSING_SEGMENTS_KEY;
import TimeBoundaryQuery.MAX_TIME;
import TimeBoundaryQuery.MIN_TIME;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.query.Druids;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerFactory;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.Result;
import org.apache.druid.query.TableDataSource;
import org.apache.druid.segment.Segment;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class TimeBoundaryQueryRunnerTest {
    private final QueryRunner runner;

    private static final QueryRunnerFactory factory = new TimeBoundaryQueryRunnerFactory(QueryRunnerTestHelper.NOOP_QUERYWATCHER);

    private static Segment segment0;

    private static Segment segment1;

    public TimeBoundaryQueryRunnerTest(QueryRunner runner) {
        this.runner = runner;
    }

    // Adapted from MultiSegmentSelectQueryTest, with modifications to make filtering meaningful
    public static final String[] V_0112 = new String[]{ "2011-01-12T01:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t110000\tpreferred\tb\u0001preferred\t100.000000", "2011-01-12T02:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t120000\tpreferred\te\u0001preferred\t100.000000", "2011-01-13T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t100000\tpreferred\ta\u0001preferred\t100.000000", "2011-01-13T01:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t110000\tpreferred\tb\u0001preferred\t100.000000" };

    public static final String[] V_0113 = new String[]{ "2011-01-14T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t100000\tpreferred\ta\u0001preferred\t94.874713", "2011-01-14T02:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t120000\tpreferred\te\u0001preferred\t110.087299", "2011-01-15T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t100000\tpreferred\ta\u0001preferred\t94.874713", "2011-01-15T01:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t110000\tpreferred\tb\u0001preferred\t103.629399", "2011-01-16T00:00:00.000Z\tspot\tautomotive\t1000\t10000.0\t100000\tpreferred\ta\u0001preferred\t94.874713", "2011-01-16T01:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t110000\tpreferred\tb\u0001preferred\t103.629399", "2011-01-16T02:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t120000\tpreferred\te\u0001preferred\t110.087299", "2011-01-17T01:00:00.000Z\tspot\tbusiness\t1100\t11000.0\t110000\tpreferred\tb\u0001preferred\t103.629399", "2011-01-17T02:00:00.000Z\tspot\tentertainment\t1200\t12000.0\t120000\tpreferred\te\u0001preferred\t110.087299" };

    @Test
    @SuppressWarnings("unchecked")
    public void testFilteredTimeBoundaryQuery() throws IOException {
        QueryRunner customRunner = getCustomRunner();
        TimeBoundaryQuery timeBoundaryQuery = Druids.newTimeBoundaryQueryBuilder().dataSource("testing").filters("quality", "automotive").build();
        Assert.assertTrue(timeBoundaryQuery.hasFilters());
        HashMap<String, Object> context = new HashMap<String, Object>();
        List<Result<TimeBoundaryResultValue>> results = customRunner.run(QueryPlus.wrap(timeBoundaryQuery), context).toList();
        Assert.assertTrue(((Iterables.size(results)) > 0));
        TimeBoundaryResultValue val = results.iterator().next().getValue();
        DateTime minTime = val.getMinTime();
        DateTime maxTime = val.getMaxTime();
        Assert.assertEquals(DateTimes.of("2011-01-13T00:00:00.000Z"), minTime);
        Assert.assertEquals(DateTimes.of("2011-01-16T00:00:00.000Z"), maxTime);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFilteredTimeBoundaryQueryNoMatches() throws IOException {
        QueryRunner customRunner = getCustomRunner();
        TimeBoundaryQuery timeBoundaryQuery = // foobar dimension does not exist
        Druids.newTimeBoundaryQueryBuilder().dataSource("testing").filters("quality", "foobar").build();
        Assert.assertTrue(timeBoundaryQuery.hasFilters());
        HashMap<String, Object> context = new HashMap<String, Object>();
        List<Result<TimeBoundaryResultValue>> results = customRunner.run(QueryPlus.wrap(timeBoundaryQuery), context).toList();
        Assert.assertTrue(((Iterables.size(results)) == 0));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTimeBoundary() {
        TimeBoundaryQuery timeBoundaryQuery = Druids.newTimeBoundaryQueryBuilder().dataSource("testing").build();
        Assert.assertFalse(timeBoundaryQuery.hasFilters());
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<Result<TimeBoundaryResultValue>> results = runner.run(QueryPlus.wrap(timeBoundaryQuery), context).toList();
        TimeBoundaryResultValue val = results.iterator().next().getValue();
        DateTime minTime = val.getMinTime();
        DateTime maxTime = val.getMaxTime();
        Assert.assertEquals(DateTimes.of("2011-01-12T00:00:00.000Z"), minTime);
        Assert.assertEquals(DateTimes.of("2011-04-15T00:00:00.000Z"), maxTime);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTimeBoundaryMax() {
        TimeBoundaryQuery timeBoundaryQuery = Druids.newTimeBoundaryQueryBuilder().dataSource("testing").bound(MAX_TIME).build();
        ConcurrentMap<String, Object> context = new ConcurrentHashMap<>();
        context.put(MISSING_SEGMENTS_KEY, new ArrayList());
        Iterable<Result<TimeBoundaryResultValue>> results = runner.run(QueryPlus.wrap(timeBoundaryQuery), context).toList();
        TimeBoundaryResultValue val = results.iterator().next().getValue();
        DateTime minTime = val.getMinTime();
        DateTime maxTime = val.getMaxTime();
        Assert.assertNull(minTime);
        Assert.assertEquals(DateTimes.of("2011-04-15T00:00:00.000Z"), maxTime);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testTimeBoundaryMin() {
        TimeBoundaryQuery timeBoundaryQuery = Druids.newTimeBoundaryQueryBuilder().dataSource("testing").bound(MIN_TIME).build();
        ConcurrentMap<String, Object> context = new ConcurrentHashMap<>();
        context.put(MISSING_SEGMENTS_KEY, new ArrayList());
        Iterable<Result<TimeBoundaryResultValue>> results = runner.run(QueryPlus.wrap(timeBoundaryQuery), context).toList();
        TimeBoundaryResultValue val = results.iterator().next().getValue();
        DateTime minTime = val.getMinTime();
        DateTime maxTime = val.getMaxTime();
        Assert.assertEquals(DateTimes.of("2011-01-12T00:00:00.000Z"), minTime);
        Assert.assertNull(maxTime);
    }

    @Test
    public void testMergeResults() {
        List<Result<TimeBoundaryResultValue>> results = Arrays.asList(new Result(DateTimes.nowUtc(), new TimeBoundaryResultValue(ImmutableMap.of("maxTime", "2012-01-01", "minTime", "2011-01-01"))), new Result(DateTimes.nowUtc(), new TimeBoundaryResultValue(ImmutableMap.of("maxTime", "2012-02-01", "minTime", "2011-01-01"))));
        TimeBoundaryQuery query = new TimeBoundaryQuery(new TableDataSource("test"), null, null, null, null);
        Iterable<Result<TimeBoundaryResultValue>> actual = query.mergeResults(results);
        Assert.assertTrue(actual.iterator().next().getValue().getMaxTime().equals(DateTimes.of("2012-02-01")));
    }

    @Test
    public void testMergeResultsEmptyResults() {
        List<Result<TimeBoundaryResultValue>> results = new ArrayList<>();
        TimeBoundaryQuery query = new TimeBoundaryQuery(new TableDataSource("test"), null, null, null, null);
        Iterable<Result<TimeBoundaryResultValue>> actual = query.mergeResults(results);
        Assert.assertFalse(actual.iterator().hasNext());
    }
}

