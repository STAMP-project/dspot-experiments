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
package org.kairosdb.datastore.cassandra;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.SetMultimap;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.KairosDataPointFactory;
import org.kairosdb.core.TestDataPointFactory;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.datastore.DatastoreMetricQueryImpl;
import org.kairosdb.datastore.DatastoreTestHelper;
import org.kairosdb.events.DataPointEvent;

import static CassandraDatastore.ROW_WIDTH;


public class CassandraDatastoreTest extends DatastoreTestHelper {
    private static final String ROW_KEY_TEST_METRIC = "row_key_test_metric";

    private static final String ROW_KEY_BIG_METRIC = "row_key_big_metric";

    private static final String TAG_INDEXED_ROW_KEY_METRIC = "tag_indexed_row_key_metric";

    private static final int MAX_ROW_READ_SIZE = 1024;

    private static final int OVERFLOW_SIZE = ((CassandraDatastoreTest.MAX_ROW_READ_SIZE) * 2) + 10;

    private static KairosDataPointFactory dataPointFactory = new TestDataPointFactory();

    private static Random random = new Random();

    private static CassandraDatastore s_datastore;

    private static long s_dataPointTime;

    public static final HashMultimap<String, String> EMPTY_MAP = HashMultimap.create();

    private static ClusterConnection m_clusterConnection;

    @Test
    public void test_getKeysForQuery() throws DatastoreException {
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(CassandraDatastoreTest.ROW_KEY_TEST_METRIC, HashMultimap.create(), CassandraDatastoreTest.s_dataPointTime, CassandraDatastoreTest.s_dataPointTime);
        List<DataPointsRowKey> keys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(query));
        Assert.assertEquals(4, keys.size());
    }

    @Test
    public void test_getKeysForQuery_withFilter() throws DatastoreException {
        SetMultimap<String, String> tagFilter = HashMultimap.create();
        tagFilter.put("client", "bar");
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(CassandraDatastoreTest.ROW_KEY_TEST_METRIC, tagFilter, CassandraDatastoreTest.s_dataPointTime, CassandraDatastoreTest.s_dataPointTime);
        List<DataPointsRowKey> keys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(query));
        Assert.assertEquals(2, keys.size());
    }

    @Test
    public void test_getKeysForQuery_TagIndexedRowKeys() throws DatastoreException {
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(CassandraDatastoreTest.TAG_INDEXED_ROW_KEY_METRIC, HashMultimap.create(), CassandraDatastoreTest.s_dataPointTime, CassandraDatastoreTest.s_dataPointTime);
        List<DataPointsRowKey> keys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(query));
        Assert.assertEquals(4, keys.size());
    }

    @Test
    public void test_getKeysForQuery_withFilter_TagIndexedRowKeys() throws DatastoreException {
        SetMultimap<String, String> tagFilter = HashMultimap.create();
        tagFilter.put("client", "bar");
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(CassandraDatastoreTest.TAG_INDEXED_ROW_KEY_METRIC, tagFilter, CassandraDatastoreTest.s_dataPointTime, CassandraDatastoreTest.s_dataPointTime);
        List<DataPointsRowKey> keys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(query));
        Assert.assertEquals(2, keys.size());
    }

    @Test
    public void testQueryTagIndexedRowKeyMetric() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        tags.put("client", "foo");
        QueryMetric tagFilteredQueryMetric = new QueryMetric(0, 0, CassandraDatastoreTest.TAG_INDEXED_ROW_KEY_METRIC);
        tagFilteredQueryMetric.setTags(tags);
        DatastoreQuery tagFilteredQuery = DatastoreTestHelper.s_datastore.createQuery(tagFilteredQueryMetric);
        List<DataPointGroup> results = tagFilteredQuery.execute();
        MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
        DataPointGroup dpg = results.get(0);
        MatcherAssert.assertThat(dpg.getName(), Is.is(CassandraDatastoreTest.TAG_INDEXED_ROW_KEY_METRIC));
        MatcherAssert.assertThat(Iterators.size(dpg), Is.is(2));
        tagFilteredQuery.close();
        QueryMetric unfilteredQueryMetric = new QueryMetric(0, 0, CassandraDatastoreTest.TAG_INDEXED_ROW_KEY_METRIC);
        DatastoreQuery unfilteredQuery = DatastoreTestHelper.s_datastore.createQuery(unfilteredQueryMetric);
        results = unfilteredQuery.execute();
        MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
        dpg = results.get(0);
        MatcherAssert.assertThat(dpg.getName(), Is.is(CassandraDatastoreTest.TAG_INDEXED_ROW_KEY_METRIC));
        MatcherAssert.assertThat(Iterators.size(dpg), Is.is(4));
        unfilteredQuery.close();
    }

    @Test
    public void test_rowLargerThanMaxReadSize() throws DatastoreException {
        Map<String, String> tagFilter = new HashMap<>();
        tagFilter.put("host", "E");
        QueryMetric query = new QueryMetric(((CassandraDatastoreTest.s_dataPointTime) - (CassandraDatastoreTest.OVERFLOW_SIZE)), 0, CassandraDatastoreTest.ROW_KEY_BIG_METRIC);
        query.setEndTime(CassandraDatastoreTest.s_dataPointTime);
        query.setTags(tagFilter);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        List<DataPointGroup> results = dq.execute();
        DataPointGroup dataPointGroup = results.get(0);
        int counter = 0;
        int total = 0;
        while (dataPointGroup.hasNext()) {
            DataPoint dp = dataPointGroup.next();
            total += dp.getLongValue();
            counter++;
        } 
        dataPointGroup.close();
        MatcherAssert.assertThat(total, CoreMatchers.equalTo((counter * 42)));
        Assert.assertEquals(CassandraDatastoreTest.OVERFLOW_SIZE, counter);
        dq.close();
    }

    @Test(expected = NullPointerException.class)
    public void test_deleteDataPoints_nullQuery_Invalid() throws IOException, DatastoreException {
        CassandraDatastoreTest.s_datastore.deleteDataPoints(null);
    }

    @Test
    public void test_deleteDataPoints_DeleteEntireRow() throws IOException, InterruptedException, DatastoreException {
        String metricToDelete = "MetricToDelete";
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, Long.MIN_VALUE, Long.MAX_VALUE);
        CachedSearchResult res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        List<DataPointRow> rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(1));
        CassandraDatastoreTest.s_datastore.deleteDataPoints(query);
        Thread.sleep(2000);
        // Verify that all data points are gone
        res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(0));
        // Verify that the index key is gone
        List<DataPointsRowKey> indexRowKeys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(query));
        MatcherAssert.assertThat(indexRowKeys.size(), CoreMatchers.equalTo(0));
        // Verify that the metric name is gone from the Strings column family
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getMetricNames(null), CoreMatchers.not(CoreMatchers.hasItem(metricToDelete)));
    }

    @Test
    public void test_deleteDataPoints_DeleteColumnsSpanningRows() throws IOException, InterruptedException, DatastoreException {
        String metricToDelete = "OtherMetricToDelete";
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, Long.MIN_VALUE, Long.MAX_VALUE);
        CachedSearchResult res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        List<DataPointRow> rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(4));
        CassandraDatastoreTest.s_datastore.deleteDataPoints(query);
        Thread.sleep(2000);
        res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(0));
        // Verify that the index key is gone
        DatastoreMetricQueryImpl queryEverything = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, 0L, Long.MAX_VALUE);
        List<DataPointsRowKey> indexRowKeys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(queryEverything));
        MatcherAssert.assertThat(indexRowKeys.size(), CoreMatchers.equalTo(0));
        // Verify that the metric name is gone from the Strings column family
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getMetricNames(null), CoreMatchers.not(CoreMatchers.hasItem(metricToDelete)));
    }

    @Test
    public void test_deleteDataPoints_DeleteColumnsSpanningRows_rowsLeft() throws IOException, InterruptedException, DatastoreException {
        long rowKeyTime = CassandraDatastore.calculateRowTime(CassandraDatastoreTest.s_dataPointTime);
        String metricToDelete = "MetricToPartiallyDelete";
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, 0L, Long.MAX_VALUE);
        CachedSearchResult res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        List<DataPointRow> rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(4));
        DatastoreMetricQuery deleteQuery = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, 0L, (rowKeyTime + ((3 * (ROW_WIDTH)) - 1)));
        CassandraDatastoreTest.s_datastore.deleteDataPoints(deleteQuery);
        Thread.sleep(2000);
        res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(1));
        // Verify that the index key is gone
        DatastoreMetricQueryImpl queryEverything = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, 0L, Long.MAX_VALUE);
        List<DataPointsRowKey> indexRowKeys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(queryEverything));
        MatcherAssert.assertThat(indexRowKeys.size(), CoreMatchers.equalTo(1));
        // Verify that the metric name still exists in the Strings column family
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getMetricNames(null), CoreMatchers.hasItem(metricToDelete));
    }

    @Test
    public void test_deleteDataPoints_DeleteColumnWithinRow() throws IOException, InterruptedException, DatastoreException {
        long rowKeyTime = CassandraDatastore.calculateRowTime(CassandraDatastoreTest.s_dataPointTime);
        String metricToDelete = "YetAnotherMetricToDelete";
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, rowKeyTime, (rowKeyTime + 2000));
        CachedSearchResult res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        List<DataPointRow> rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(1));
        CassandraDatastoreTest.s_datastore.deleteDataPoints(query);
        Thread.sleep(2000);
        res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(0));
        // Verify that the index key is still there
        DatastoreMetricQueryImpl queryEverything = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, 0L, Long.MAX_VALUE);
        List<DataPointsRowKey> indexRowKeys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(queryEverything));
        MatcherAssert.assertThat(indexRowKeys.size(), CoreMatchers.equalTo(1));
        // Verify that the metric name still exists in the Strings column family
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getMetricNames(null), CoreMatchers.hasItem(metricToDelete));
    }

    @Test
    public void test_deleteDataPoints_DeleteEntireRow2() throws IOException, InterruptedException, DatastoreException {
        CassandraDatastoreTest.m_clusterConnection.psDataPointsDeleteRange = null;
        String metricToDelete = "MetricToDelete2";
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, Long.MIN_VALUE, Long.MAX_VALUE);
        CachedSearchResult res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        List<DataPointRow> rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(1));
        CassandraDatastoreTest.s_datastore.deleteDataPoints(query);
        Thread.sleep(2000);
        // Verify that all data points are gone
        res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(0));
        // Verify that the index key is gone
        List<DataPointsRowKey> indexRowKeys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(query));
        MatcherAssert.assertThat(indexRowKeys.size(), CoreMatchers.equalTo(0));
        // Verify that the metric name is gone from the Strings column family
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getMetricNames(null), CoreMatchers.not(CoreMatchers.hasItem(metricToDelete)));
    }

    @Test
    public void test_deleteDataPoints_DeleteColumnsSpanningRows2() throws IOException, InterruptedException, DatastoreException {
        CassandraDatastoreTest.m_clusterConnection.psDataPointsDeleteRange = null;
        String metricToDelete = "OtherMetricToDelete2";
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, Long.MIN_VALUE, Long.MAX_VALUE);
        CachedSearchResult res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        List<DataPointRow> rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(4));
        CassandraDatastoreTest.s_datastore.deleteDataPoints(query);
        Thread.sleep(2000);
        res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(0));
        // Verify that the index key is gone
        DatastoreMetricQueryImpl queryEverything = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, 0L, Long.MAX_VALUE);
        List<DataPointsRowKey> indexRowKeys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(queryEverything));
        MatcherAssert.assertThat(indexRowKeys.size(), CoreMatchers.equalTo(0));
        // Verify that the metric name is gone from the Strings column family
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getMetricNames(null), CoreMatchers.not(CoreMatchers.hasItem(metricToDelete)));
    }

    @Test
    public void test_deleteDataPoints_DeleteColumnsSpanningRows_rowsLeft2() throws IOException, InterruptedException, DatastoreException {
        CassandraDatastoreTest.m_clusterConnection.psDataPointsDeleteRange = null;
        long rowKeyTime = CassandraDatastore.calculateRowTime(CassandraDatastoreTest.s_dataPointTime);
        String metricToDelete = "MetricToPartiallyDelete2";
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, 0L, Long.MAX_VALUE);
        CachedSearchResult res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        List<DataPointRow> rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(4));
        DatastoreMetricQuery deleteQuery = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, 0L, (rowKeyTime + ((3 * (ROW_WIDTH)) - 1)));
        CassandraDatastoreTest.s_datastore.deleteDataPoints(deleteQuery);
        Thread.sleep(2000);
        res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(1));
        // Verify that the index key is gone
        DatastoreMetricQueryImpl queryEverything = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, 0L, Long.MAX_VALUE);
        List<DataPointsRowKey> indexRowKeys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(queryEverything));
        MatcherAssert.assertThat(indexRowKeys.size(), CoreMatchers.equalTo(1));
        // Verify that the metric name still exists in the Strings column family
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getMetricNames(null), CoreMatchers.hasItem(metricToDelete));
    }

    @Test
    public void test_deleteDataPoints_DeleteColumnWithinRow2() throws IOException, InterruptedException, DatastoreException {
        CassandraDatastoreTest.m_clusterConnection.psDataPointsDeleteRange = null;
        long rowKeyTime = CassandraDatastore.calculateRowTime(CassandraDatastoreTest.s_dataPointTime);
        String metricToDelete = "YetAnotherMetricToDelete2";
        DatastoreMetricQuery query = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, rowKeyTime, (rowKeyTime + 2000));
        CachedSearchResult res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        List<DataPointRow> rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(1));
        CassandraDatastoreTest.s_datastore.deleteDataPoints(query);
        Thread.sleep(2000);
        res = CassandraDatastoreTest.createCache(metricToDelete);
        CassandraDatastoreTest.s_datastore.queryDatabase(query, res);
        rows = res.getRows();
        MatcherAssert.assertThat(rows.size(), CoreMatchers.equalTo(0));
        // Verify that the index key is still there
        DatastoreMetricQueryImpl queryEverything = new DatastoreMetricQueryImpl(metricToDelete, CassandraDatastoreTest.EMPTY_MAP, 0L, Long.MAX_VALUE);
        List<DataPointsRowKey> indexRowKeys = CassandraDatastoreTest.readIterator(CassandraDatastoreTest.s_datastore.getKeysForQueryIterator(queryEverything));
        MatcherAssert.assertThat(indexRowKeys.size(), CoreMatchers.equalTo(1));
        // Verify that the metric name still exists in the Strings column family
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getMetricNames(null), CoreMatchers.hasItem(metricToDelete));
    }

    /**
     * This is here because hbase throws an exception in this case
     *
     * @throws DatastoreException
     * 		bla
     */
    @Test
    public void test_queryDatabase_noMetric() throws DatastoreException {
        Map<String, String> tags = new TreeMap<>();
        QueryMetric query = new QueryMetric(500, 0, "metric_not_there");
        query.setEndTime(3000);
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        List<DataPointGroup> results = dq.execute();
        MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
        DataPointGroup dpg = results.get(0);
        MatcherAssert.assertThat(dpg.getName(), Is.is("metric_not_there"));
        Assert.assertFalse(dpg.hasNext());
        dq.close();
    }

    @Test
    public void test_TimestampsCloseToZero() throws DatastoreException {
        DataPointSet set = new DataPointSet("testMetric");
        set.addDataPoint(new LongDataPoint(1, 1L));
        set.addDataPoint(new LongDataPoint(2, 2L));
        set.addDataPoint(new LongDataPoint(0, 3L));
        set.addDataPoint(new LongDataPoint(3, 4L));
        set.addDataPoint(new LongDataPoint(4, 5L));
        set.addDataPoint(new LongDataPoint(5, 6L));
        CassandraDatastoreTest.putDataPoints(set);
    }

    @Test
    public void test_setTTL() throws InterruptedException, DatastoreException {
        DataPointSet set = new DataPointSet("ttlMetric");
        set.addTag("tag", "value");
        set.addDataPoint(new LongDataPoint(1, 1L));
        set.addDataPoint(new LongDataPoint(2, 2L));
        set.addDataPoint(new LongDataPoint(0, 3L));
        set.addDataPoint(new LongDataPoint(3, 4L));
        set.addDataPoint(new LongDataPoint(4, 5L));
        set.addDataPoint(new LongDataPoint(5, 6L));
        CassandraDatastoreTest.putDataPoints(set);
        DatastoreTestHelper.s_eventBus.createPublisher(DataPointEvent.class).post(new DataPointEvent("ttlMetric", set.getTags(), new LongDataPoint(50, 7L), 1));
        Thread.sleep(2000);
        Map<String, String> tags = new TreeMap<>();
        QueryMetric query = new QueryMetric(0, 500, 0, "ttlMetric");
        query.setTags(tags);
        DatastoreQuery dq = DatastoreTestHelper.s_datastore.createQuery(query);
        List<DataPointGroup> results = dq.execute();
        try {
            MatcherAssert.assertThat(results.size(), CoreMatchers.equalTo(1));
            DataPointGroup dpg = results.get(0);
            MatcherAssert.assertThat(dpg.getName(), Is.is("ttlMetric"));
            MatcherAssert.assertThat(dq.getSampleSize(), CoreMatchers.equalTo(6));
        } finally {
            dq.close();
        }
    }

    @Test
    public void test_serviceKeyStore_singleService() throws DatastoreException {
        CassandraDatastoreTest.s_datastore.setValue("Service", "ServiceKey", "key1", "value1");
        CassandraDatastoreTest.s_datastore.setValue("Service", "ServiceKey", "key2", "value2");
        CassandraDatastoreTest.s_datastore.setValue("Service", "ServiceKey", "foo", "value3");
        // Test setValue and getValue
        assertServiceKeyValue("Service", "ServiceKey", "key1", "value1");
        assertServiceKeyValue("Service", "ServiceKey", "key2", "value2");
        assertServiceKeyValue("Service", "ServiceKey", "foo", "value3");
        // Test lastModified value changes
        long lastModified = CassandraDatastoreTest.s_datastore.getValue("Service", "ServiceKey", "key2").getLastModified().getTime();
        CassandraDatastoreTest.s_datastore.setValue("Service", "ServiceKey", "key2", "changed");
        assertServiceKeyValue("Service", "ServiceKey", "key2", "changed");
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getValue("Service", "ServiceKey", "key2").getLastModified().getTime(), Matchers.greaterThan(lastModified));
        // Test listKeys
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listKeys("Service", "ServiceKey"), CoreMatchers.hasItems("foo", "key1", "key2"));
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listKeys("Service", "ServiceKey", "key"), CoreMatchers.hasItems("key1", "key2"));
        // Test delete
        lastModified = CassandraDatastoreTest.s_datastore.getServiceKeyLastModifiedTime("Service", "ServiceKey").getTime();
        CassandraDatastoreTest.s_datastore.deleteKey("Service", "ServiceKey", "key2");
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listKeys("Service", "ServiceKey"), CoreMatchers.hasItems("key1", "foo"));
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getValue("Service", "ServiceKey", "key2"), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getServiceKeyLastModifiedTime("Service", "ServiceKey").getTime(), Matchers.greaterThan(lastModified));
        lastModified = CassandraDatastoreTest.s_datastore.getServiceKeyLastModifiedTime("Service", "ServiceKey").getTime();
        CassandraDatastoreTest.s_datastore.deleteKey("Service", "ServiceKey", "foo");
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listKeys("Service", "ServiceKey"), CoreMatchers.hasItems("key1"));
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getValue("Service", "ServiceKey", "foo"), Is.is(CoreMatchers.nullValue()));
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.getServiceKeyLastModifiedTime("Service", "ServiceKey").getTime(), Matchers.greaterThan(lastModified));
    }

    @Test
    public void test_serviceKeyStore_multipleServices() throws DatastoreException {
        CassandraDatastoreTest.s_datastore.setValue("Service1", "ServiceKey1", "key1", "value1");
        CassandraDatastoreTest.s_datastore.setValue("Service1", "ServiceKey2", "key1", "value2");
        CassandraDatastoreTest.s_datastore.setValue("Service1", "ServiceKey3", "key1", "value3");
        CassandraDatastoreTest.s_datastore.setValue("Service2", "ServiceKey1", "key1", "value4");
        CassandraDatastoreTest.s_datastore.setValue("Service2", "ServiceKey1", "key2", "value5");
        CassandraDatastoreTest.s_datastore.setValue("Service2", "ServiceKey1", "key3", "value6");
        CassandraDatastoreTest.s_datastore.setValue("Service2", "ServiceKey1", "key4", "value7");
        CassandraDatastoreTest.s_datastore.setValue("Service3", "ServiceKey1", "foo", "value8");
        CassandraDatastoreTest.s_datastore.setValue("Service3", "ServiceKey1", "bar", "value9");
        // Test listKeys
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listKeys("Service1", "ServiceKey1"), CoreMatchers.hasItems("key1"));
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listKeys("Service1", "ServiceKey2"), CoreMatchers.hasItems("key1"));
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listKeys("Service1", "ServiceKey3"), CoreMatchers.hasItems("key1"));
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listKeys("Service2", "ServiceKey1"), CoreMatchers.hasItems("key1", "key2", "key3", "key4"));
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listKeys("Service3", "ServiceKey1"), CoreMatchers.hasItems("foo", "bar"));
        // Test listServiceKeys
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listServiceKeys("Service1"), CoreMatchers.hasItems("ServiceKey1", "ServiceKey2", "ServiceKey3"));
        // Test get
        assertServiceKeyValue("Service1", "ServiceKey1", "key1", "value1");
        assertServiceKeyValue("Service1", "ServiceKey2", "key1", "value2");
        assertServiceKeyValue("Service1", "ServiceKey3", "key1", "value3");
        assertServiceKeyValue("Service2", "ServiceKey1", "key1", "value4");
        assertServiceKeyValue("Service2", "ServiceKey1", "key2", "value5");
        assertServiceKeyValue("Service2", "ServiceKey1", "key3", "value6");
        assertServiceKeyValue("Service2", "ServiceKey1", "key4", "value7");
        assertServiceKeyValue("Service3", "ServiceKey1", "foo", "value8");
        assertServiceKeyValue("Service3", "ServiceKey1", "bar", "value9");
    }

    /**
     * Delete on the last row of a primary key leaves a row with a null (empty) column for the key.
     * Verify that we ignore this row.
     */
    @Test
    public void test_serviceKeyStore_nullKey() throws DatastoreException {
        CassandraDatastoreTest.s_datastore.setValue("Service1", "ServiceKey1", "key1", "value1");
        CassandraDatastoreTest.s_datastore.deleteKey("Service1", "ServiceKey1", "key1");
        MatcherAssert.assertThat(CassandraDatastoreTest.s_datastore.listKeys("Service1", "ServiceKey1").iterator().hasNext(), CoreMatchers.equalTo(false));
    }
}

