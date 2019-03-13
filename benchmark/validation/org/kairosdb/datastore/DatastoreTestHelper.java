/**
 * Copyright 2016 KairosDB Authors
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.datastore;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.KairosRootConfig;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.core.datastore.DatastoreQuery;
import org.kairosdb.core.datastore.KairosDatastore;
import org.kairosdb.core.datastore.QueryMetric;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.core.groupby.TagGroupBy;
import org.kairosdb.datastore.cassandra.CassandraDatastore;
import org.kairosdb.eventbus.FilterEventBus;


public abstract class DatastoreTestHelper {
    protected static KairosDatastore s_datastore;

    protected static FilterEventBus s_eventBus = new FilterEventBus(new org.kairosdb.eventbus.EventBusConfiguration(new KairosRootConfig()));

    protected static final List<String> metricNames = new ArrayList<>();

    private static long s_startTime;

    private static String s_unicodeNameWithSpace = "?? means hello";

    private static String s_unicodeName = "??";

    @Test
    public void test_getMetricNames() throws DatastoreException {
        List<String> metrics = DatastoreTestHelper.listFromIterable(DatastoreTestHelper.s_datastore.getMetricNames(null));
        MatcherAssert.assertThat(metrics, CoreMatchers.hasItem("metric1"));
        MatcherAssert.assertThat(metrics, CoreMatchers.hasItem("metric2"));
        MatcherAssert.assertThat(metrics, CoreMatchers.hasItem("duplicates"));
        MatcherAssert.assertThat(metrics, CoreMatchers.hasItem("old_data"));
    }

    @Test
    public void test_getMetricNames_with_prefix() throws DatastoreException {
        List<String> metrics = DatastoreTestHelper.listFromIterable(DatastoreTestHelper.s_datastore.getMetricNames("m"));
        MatcherAssert.assertThat(metrics, CoreMatchers.hasItem("metric1"));
        MatcherAssert.assertThat(metrics, CoreMatchers.hasItem("metric2"));
        MatcherAssert.assertThat(metrics.size(), CoreMatchers.equalTo(2));
    }

    // names and values not being stored
    /* @Test
    public void test_getTagNames() throws DatastoreException
    {
    List<String> metrics = listFromIterable(s_datastore.getTagNames());

    assertThat(metrics, hasItem("host"));
    assertThat(metrics, hasItem("client"));
    assertThat(metrics, hasItem("month"));
    }
     */
    /* @Test
    public void test_getTagValues() throws DatastoreException
    {
    List<String> metrics = listFromIterable(s_datastore.getTagValues());

    assertThat(metrics, hasItem("A"));
    assertThat(metrics, hasItem("B"));
    assertThat(metrics, hasItem("foo"));
    assertThat(metrics, hasItem("bar"));
    assertThat(metrics, hasItem("April"));
    }
     */
    @Test
    public void test_queryDatabase_stringData() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "string_data");
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("string_data"));
            MatcherAssert.assertThat(dpg.hasNext(), Is.is(true));
            String actual = getValue();
            MatcherAssert.assertThat(actual, Is.is("Hello"));
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryDatabase_stringDataUnicode() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "string_data_unicode");
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("string_data_unicode"));
            MatcherAssert.assertThat(dpg.hasNext(), Is.is(true));
            String actual = getValue();
            MatcherAssert.assertThat(actual, Is.is(DatastoreTestHelper.s_unicodeName));
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryDatabase_noTags() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "metric1");
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("metric1"));
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.size(), Is.is(5));
            SetMultimap<String, String> expectedTags = TreeMultimap.create();
            expectedTags.put("host", "A");
            expectedTags.put("host", "B");
            expectedTags.put("client", "foo");
            expectedTags.put("client", "bar");
            expectedTags.put("month", "April");
            MatcherAssert.assertThat(resTags, Is.is(expectedTags));
            assertValues(dpg, 1, 5, 9, 2, 6, 10, 3, 7, 11, 4, 8, 12);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryDatabase_withTags() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        tags.put("client", "foo");
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "metric1");
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), Is.is(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("metric1"));
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            Assert.assertEquals(4, resTags.size());
            SetMultimap<String, String> expectedTags = TreeMultimap.create();
            expectedTags.put("host", "A");
            expectedTags.put("host", "B");
            expectedTags.put("client", "foo");
            expectedTags.put("month", "April");
            MatcherAssert.assertThat(resTags, Is.is(expectedTags));
            assertValues(dpg, 1, 5, 2, 6, 3, 7, 4, 8);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryDatabase_withTagGroupBy() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "metric1");
        query.addGroupBy(new TagGroupBy(Collections.singletonList("host")));
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), Is.is(2));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("metric1"));
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            SetMultimap<String, String> expectedTags = TreeMultimap.create();
            expectedTags.put("host", "A");
            expectedTags.put("client", "foo");
            expectedTags.put("client", "bar");
            expectedTags.put("month", "April");
            MatcherAssert.assertThat(resTags, CoreMatchers.equalTo(expectedTags));
            assertValues(dpg, 1, 9, 2, 10, 3, 11, 4, 12);
            dpg = results.get(1);
            MatcherAssert.assertThat(dpg.getName(), Is.is("metric1"));
            resTags = DatastoreTestHelper.extractTags(dpg);
            expectedTags = TreeMultimap.create();
            expectedTags.put("host", "B");
            expectedTags.put("client", "foo");
            expectedTags.put("month", "April");
            MatcherAssert.assertThat(resTags, CoreMatchers.equalTo(expectedTags));
            assertValues(dpg, 5, 6, 7, 8);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryDatabase_withMultipleTagGroupBy() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "metric1");
        query.addGroupBy(new TagGroupBy("host", "client"));
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), Is.is(3));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("metric1"));
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.keySet().size(), Is.is(3));
            SetMultimap<String, String> expectedTags = TreeMultimap.create();
            expectedTags.put("host", "A");
            expectedTags.put("client", "bar");
            expectedTags.put("month", "April");
            MatcherAssert.assertThat(resTags, CoreMatchers.equalTo(expectedTags));
            assertValues(dpg, 9, 10, 11, 12);
            dpg = results.get(1);
            MatcherAssert.assertThat(dpg.getName(), Is.is("metric1"));
            resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.keySet().size(), Is.is(3));
            expectedTags = TreeMultimap.create();
            expectedTags.put("host", "A");
            expectedTags.put("client", "foo");
            expectedTags.put("month", "April");
            MatcherAssert.assertThat(resTags, CoreMatchers.equalTo(expectedTags));
            assertValues(dpg, 1, 2, 3, 4);
            dpg = results.get(2);
            MatcherAssert.assertThat(dpg.getName(), Is.is("metric1"));
            resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.keySet().size(), Is.is(3));
            expectedTags = TreeMultimap.create();
            expectedTags.put("host", "B");
            expectedTags.put("client", "foo");
            expectedTags.put("month", "April");
            MatcherAssert.assertThat(resTags, CoreMatchers.equalTo(expectedTags));
            assertValues(dpg, 5, 6, 7, 8);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryDatabase_withGroupBy_nonMatchingTag() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "metric1");
        query.addGroupBy(new TagGroupBy("bogus"));
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), Is.is(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("metric1"));
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.keySet().size(), Is.is(3));
            SetMultimap<String, String> expectedTags = TreeMultimap.create();
            expectedTags.put("host", "A");
            expectedTags.put("host", "B");
            expectedTags.put("client", "foo");
            expectedTags.put("client", "bar");
            expectedTags.put("month", "April");
            MatcherAssert.assertThat(resTags, Is.is(expectedTags));
            assertValues(dpg, 1, 5, 9, 2, 6, 10, 3, 7, 11, 4, 8, 12);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryWithMultipleTagsFilter() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        tags.put("host", "A");
        tags.put("client", "bar");
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "metric1");
        query.addGroupBy(new TagGroupBy(Collections.singletonList("host")));
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), Is.is(1));
            assertValues(results.get(0), 9, 10, 11, 12);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryWithMultipleHostTags() throws DatastoreException {
        SetMultimap<String, String> tags = HashMultimap.create();
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "metric1");
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        tags.put("host", "A");
        tags.put("host", "B");
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("metric1"));
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.size(), Is.is(5));
            SetMultimap<String, String> expectedTags = TreeMultimap.create();
            expectedTags.put("host", "A");
            expectedTags.put("host", "B");
            expectedTags.put("client", "foo");
            expectedTags.put("client", "bar");
            expectedTags.put("month", "April");
            MatcherAssert.assertThat(resTags, Is.is(expectedTags));
            assertValues(dpg, 1, 5, 9, 2, 6, 10, 3, 7, 11, 4, 8, 12);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_duplicateDataPoints() throws DatastoreException {
        SetMultimap<String, String> tags = HashMultimap.create();
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "duplicates");
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("duplicates"));
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.size(), Is.is(1));
            SetMultimap<String, String> expectedTags = TreeMultimap.create();
            expectedTags.put("host", "A");
            MatcherAssert.assertThat(resTags, Is.is(expectedTags));
            assertValues(dpg, 42);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryDatabase_noResults() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        QueryMetric query = new QueryMetric(500, 0, "metric1");
        query.setEndTime(1000);
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("metric1"));
            Assert.assertFalse(dpg.hasNext());
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryNegativeAndPositiveTime() throws DatastoreException {
        QueryMetric query = new QueryMetric((-2000000000L), 0, "old_data");
        query.setEndTime(2000000000L);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("old_data"));
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.size(), Is.is(1));
            SetMultimap<String, String> expectedTags = TreeMultimap.create();
            expectedTags.put("host", "A");
            MatcherAssert.assertThat(resTags, Is.is(expectedTags));
            assertValues(dpg, 80, 40, 20, 3, 33);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryNegativeTime() throws DatastoreException {
        QueryMetric query = new QueryMetric((-2000000000L), 0, "old_data");
        query.setEndTime((-1L));
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("old_data"));
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.size(), Is.is(1));
            SetMultimap<String, String> expectedTags = TreeMultimap.create();
            expectedTags.put("host", "A");
            MatcherAssert.assertThat(resTags, Is.is(expectedTags));
            assertValues(dpg, 80, 40, 20);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_queryWithUnicode() throws DatastoreException {
        SetMultimap<String, String> tags = HashMultimap.create();
        QueryMetric query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, DatastoreTestHelper.s_unicodeNameWithSpace);
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 3000));
        tags.put("host", DatastoreTestHelper.s_unicodeName);
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is(DatastoreTestHelper.s_unicodeNameWithSpace));
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.size(), Is.is(2));
            SetMultimap<String, String> expectedTags = TreeMultimap.create();
            expectedTags.put("host", DatastoreTestHelper.s_unicodeName);
            expectedTags.put("space", "space is cool");
            MatcherAssert.assertThat(resTags, Is.is(expectedTags));
            assertValues(dpg, 42);
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_notReturningTagsForEmptyData() throws InterruptedException, DatastoreException {
        QueryMetric query = new QueryMetric(((DatastoreTestHelper.s_startTime) - 1), 0, "delete_me");
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 1));
        DatastoreTestHelper.s_datastore.delete(query);
        Thread.sleep(1500);
        // Now query for the data
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat(resTags.size(), Is.is(0));
            MatcherAssert.assertThat(dpg.hasNext(), Is.is(false));
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_deleteTimeWindowWithTag() throws InterruptedException, DatastoreException {
        QueryMetric query = new QueryMetric(((DatastoreTestHelper.s_startTime) - (CassandraDatastore.ROW_WIDTH)), 0, "double_delete");
        query.setTags(ImmutableMap.of("tag", "1"));
        query.setEndTime(((DatastoreTestHelper.s_startTime) + (CassandraDatastore.ROW_WIDTH)));
        DatastoreTestHelper.s_datastore.delete(query);
        query = new QueryMetric(DatastoreTestHelper.s_startTime, 0, "double_delete");
        query.setEndTime(((DatastoreTestHelper.s_startTime) + 1));
        Thread.sleep(1500);
        // Now query for the data
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        try {
            List<DataPointGroup> results = dq.execute();
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            SetMultimap<String, String> resTags = DatastoreTestHelper.extractTags(dpg);
            MatcherAssert.assertThat("there is only one tag", resTags.size(), Is.is(1));
            MatcherAssert.assertThat("there is one data point", dpg.hasNext(), Is.is(true));
            dpg.next();
            MatcherAssert.assertThat("there is only one data point", dpg.hasNext(), Is.is(false));
        } finally {
            dq.close();
        }
    }
}

