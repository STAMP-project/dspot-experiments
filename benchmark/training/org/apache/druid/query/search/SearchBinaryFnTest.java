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


import Granularities.DAY;
import com.google.common.collect.ImmutableList;
import java.util.Comparator;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.query.Result;
import org.apache.druid.query.ordering.StringComparators;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class SearchBinaryFnTest {
    private final DateTime currTime = DateTimes.nowUtc();

    @Test
    public void testMerge() {
        Result<SearchResultValue> r1 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo"))));
        Result<SearchResultValue> r2 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah2", "foo2"))));
        Result<SearchResultValue> expected = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo"), new SearchHit("blah2", "foo2"))));
        Result<SearchResultValue> actual = new SearchBinaryFn(new SearchSortSpec(StringComparators.LEXICOGRAPHIC), Granularities.ALL, Integer.MAX_VALUE).apply(r1, r2);
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertSearchMergeResult(expected.getValue(), actual.getValue());
    }

    @Test
    public void testMergeDay() {
        Result<SearchResultValue> r1 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo"))));
        Result<SearchResultValue> r2 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah2", "foo2"))));
        Result<SearchResultValue> expected = new Result<SearchResultValue>(DAY.bucketStart(currTime), new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo"), new SearchHit("blah2", "foo2"))));
        Result<SearchResultValue> actual = new SearchBinaryFn(new SearchSortSpec(StringComparators.LEXICOGRAPHIC), Granularities.DAY, Integer.MAX_VALUE).apply(r1, r2);
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertSearchMergeResult(expected.getValue(), actual.getValue());
    }

    @Test
    public void testMergeOneResultNull() {
        Result<SearchResultValue> r1 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo"))));
        Result<SearchResultValue> r2 = null;
        Result<SearchResultValue> expected = r1;
        Result<SearchResultValue> actual = new SearchBinaryFn(new SearchSortSpec(StringComparators.LEXICOGRAPHIC), Granularities.ALL, Integer.MAX_VALUE).apply(r1, r2);
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertSearchMergeResult(expected.getValue(), actual.getValue());
    }

    @Test
    public void testMergeShiftedTimestamp() {
        Result<SearchResultValue> r1 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo"))));
        Result<SearchResultValue> r2 = new Result<SearchResultValue>(currTime.plusHours(2), new SearchResultValue(ImmutableList.of(new SearchHit("blah2", "foo2"))));
        Result<SearchResultValue> expected = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo"), new SearchHit("blah2", "foo2"))));
        Result<SearchResultValue> actual = new SearchBinaryFn(new SearchSortSpec(StringComparators.LEXICOGRAPHIC), Granularities.ALL, Integer.MAX_VALUE).apply(r1, r2);
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertSearchMergeResult(expected.getValue(), actual.getValue());
    }

    @Test
    public void testStrlenMerge() {
        SearchSortSpec searchSortSpec = new SearchSortSpec(StringComparators.STRLEN);
        Comparator<SearchHit> c = searchSortSpec.getComparator();
        Result<SearchResultValue> r1 = new Result<SearchResultValue>(currTime, new SearchResultValue(toHits(c, "blah:thisislong")));
        Result<SearchResultValue> r2 = new Result<SearchResultValue>(currTime, new SearchResultValue(toHits(c, "blah:short")));
        Result<SearchResultValue> expected = new Result<SearchResultValue>(currTime, new SearchResultValue(toHits(c, "blah:short", "blah:thisislong")));
        Result<SearchResultValue> actual = new SearchBinaryFn(searchSortSpec, Granularities.ALL, Integer.MAX_VALUE).apply(r1, r2);
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertSearchMergeResult(expected.getValue(), actual.getValue());
    }

    @Test
    public void testStrlenMerge2() {
        SearchSortSpec searchSortSpec = new SearchSortSpec(StringComparators.STRLEN);
        Comparator<SearchHit> c = searchSortSpec.getComparator();
        Result<SearchResultValue> r1 = new Result<SearchResultValue>(currTime, new SearchResultValue(toHits(c, "blah:short", "blah:thisislong", "blah2:thisislong")));
        Result<SearchResultValue> r2 = new Result<SearchResultValue>(currTime, new SearchResultValue(toHits(c, "blah:short", "blah2:thisislong")));
        Result<SearchResultValue> expected = new Result<SearchResultValue>(currTime, new SearchResultValue(toHits(c, "blah:short", "blah:thisislong", "blah2:thisislong")));
        Result<SearchResultValue> actual = new SearchBinaryFn(searchSortSpec, Granularities.ALL, Integer.MAX_VALUE).apply(r1, r2);
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertSearchMergeResult(expected.getValue(), actual.getValue());
    }

    @Test
    public void testAlphanumericMerge() {
        SearchSortSpec searchSortSpec = new SearchSortSpec(StringComparators.ALPHANUMERIC);
        Comparator<SearchHit> c = searchSortSpec.getComparator();
        Result<SearchResultValue> r1 = new Result<SearchResultValue>(currTime, new SearchResultValue(toHits(c, "blah:a100", "blah:a9", "alah:a100")));
        Result<SearchResultValue> r2 = new Result<SearchResultValue>(currTime, new SearchResultValue(toHits(c, "blah:b0", "alah:c3")));
        Result<SearchResultValue> expected = new Result<SearchResultValue>(currTime, new SearchResultValue(toHits(c, "blah:a9", "alah:a100", "blah:a100", "blah:b0", "alah:c3")));
        Result<SearchResultValue> actual = new SearchBinaryFn(searchSortSpec, Granularities.ALL, Integer.MAX_VALUE).apply(r1, r2);
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertSearchMergeResult(expected.getValue(), actual.getValue());
    }

    @Test
    public void testMergeUniqueResults() {
        Result<SearchResultValue> r1 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo"))));
        Result<SearchResultValue> r2 = r1;
        Result<SearchResultValue> expected = r1;
        Result<SearchResultValue> actual = new SearchBinaryFn(new SearchSortSpec(StringComparators.LEXICOGRAPHIC), Granularities.ALL, Integer.MAX_VALUE).apply(r1, r2);
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertSearchMergeResult(expected.getValue(), actual.getValue());
    }

    @Test
    public void testMergeLimit() {
        Result<SearchResultValue> r1 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo"))));
        Result<SearchResultValue> r2 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah2", "foo2"))));
        Result<SearchResultValue> expected = r1;
        Result<SearchResultValue> actual = new SearchBinaryFn(new SearchSortSpec(StringComparators.LEXICOGRAPHIC), Granularities.ALL, 1).apply(r1, r2);
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertSearchMergeResult(expected.getValue(), actual.getValue());
    }

    @Test
    public void testMergeCountWithNull() {
        Result<SearchResultValue> r1 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo"))));
        Result<SearchResultValue> r2 = new Result<SearchResultValue>(currTime, new SearchResultValue(ImmutableList.of(new SearchHit("blah", "foo", 3))));
        Result<SearchResultValue> expected = r1;
        Result<SearchResultValue> actual = new SearchBinaryFn(new SearchSortSpec(StringComparators.LEXICOGRAPHIC), Granularities.ALL, Integer.MAX_VALUE).apply(r1, r2);
        Assert.assertEquals(expected.getTimestamp(), actual.getTimestamp());
        assertSearchMergeResult(expected.getValue(), actual.getValue());
    }
}

