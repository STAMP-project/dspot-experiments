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
package org.kairosdb.core.datastore;


import LegacyDataPointFactory.DATASTORE_TYPE;
import Order.ASC;
import QueryCallback.DataPointWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.DataPoint;
import org.kairosdb.core.KairosDataPointFactory;
import org.kairosdb.core.TestDataPointFactory;
import org.kairosdb.core.aggregator.TestAggregatorFactory;
import org.kairosdb.core.datapoints.LegacyLongDataPoint;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.core.exception.KairosDBException;
import org.kairosdb.core.groupby.TagGroupBy;
import org.kairosdb.core.processingstage.FeatureProcessingFactory;
import org.kairosdb.plugin.Aggregator;


public class KairosDatastoreTest {
    private FeatureProcessingFactory<Aggregator> aggFactory;

    public KairosDatastoreTest() throws KairosDBException {
        aggFactory = new TestAggregatorFactory();
    }

    @Test(expected = NullPointerException.class)
    public void test_query_nullMetricInvalid() throws KairosDBException {
        KairosDatastoreTest.TestDatastore testds = new KairosDatastoreTest.TestDatastore();
        KairosDatastore datastore = new KairosDatastore(testds, new QueryQueuingManager(1, "hostname"), new TestDataPointFactory(), false);
        datastore.createQuery(null);
    }

    @Test
    public void test_query_sumAggregator() throws KairosDBException {
        KairosDatastoreTest.TestDatastore testds = new KairosDatastoreTest.TestDatastore();
        KairosDatastore datastore = new KairosDatastore(testds, new QueryQueuingManager(1, "hostname"), new TestDataPointFactory(), false);
        datastore.init();
        QueryMetric metric = new QueryMetric(1L, 1, "metric1");
        metric.addAggregator(aggFactory.createFeatureProcessor("sum"));
        DatastoreQuery dq = datastore.createQuery(metric);
        List<DataPointGroup> results = dq.execute();
        DataPointGroup group = results.get(0);
        DataPoint dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(72L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(32L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(32L));
        dq.close();
    }

    @Test
    public void test_query_noAggregator() throws KairosDBException {
        KairosDatastoreTest.TestDatastore testds = new KairosDatastoreTest.TestDatastore();
        KairosDatastore datastore = new KairosDatastore(testds, new QueryQueuingManager(1, "hostname"), new TestDataPointFactory(), false);
        datastore.init();
        QueryMetric metric = new QueryMetric(1L, 1, "metric1");
        DatastoreQuery dq = datastore.createQuery(metric);
        List<DataPointGroup> results = dq.execute();
        Assert.assertThat(results.size(), CoreMatchers.is(1));
        DataPointGroup group = results.get(0);
        DataPoint dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(3L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(5L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(10L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(14L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(20L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(20L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(1L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(3L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(5L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(6L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(8L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(9L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(7L));
        dataPoint = group.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(25L));
        dq.close();
    }

    @SuppressWarnings({ "ResultOfMethodCallIgnored", "ConstantConditions" })
    @Test
    public void test_cleanCacheDir() throws IOException, DatastoreException {
        KairosDatastoreTest.TestDatastore testds = new KairosDatastoreTest.TestDatastore();
        KairosDatastore datastore = new KairosDatastore(testds, new QueryQueuingManager(1, "hostname"), new TestDataPointFactory(), false);
        datastore.init();
        // Create files in the cache directory
        File cacheDir = new File(datastore.getCacheDir());
        File file1 = new File(cacheDir, "testFile1");
        file1.createNewFile();
        File file2 = new File(cacheDir, "testFile2");
        file2.createNewFile();
        File[] files = cacheDir.listFiles();
        TestCase.assertTrue(((files.length) > 0));
        datastore.cleanCacheDir(false);
        TestCase.assertFalse(file1.exists());
        TestCase.assertFalse(file2.exists());
    }

    @Test
    public void test_groupByTypeAndTag_SameTagValue() throws DatastoreException {
        KairosDatastoreTest.TestKairosDatastore datastore = new KairosDatastoreTest.TestKairosDatastore(new KairosDatastoreTest.TestDatastore(), new QueryQueuingManager(1, "hostname"), new TestDataPointFactory());
        TagGroupBy groupBy = new TagGroupBy("tag1", "tag2");
        List<DataPointRow> rows = new ArrayList<>();
        DataPointRowImpl row1 = new DataPointRowImpl();
        row1.addTag("tag1", "value");
        row1.addDataPoint(new LongDataPoint(1234, 1));
        DataPointRowImpl row2 = new DataPointRowImpl();
        row2.addTag("tag2", "value");
        row2.addDataPoint(new LongDataPoint(1235, 2));
        rows.add(row1);
        rows.add(row2);
        List<DataPointGroup> dataPointsGroup = datastore.groupByTypeAndTag("metricName", rows, groupBy, ASC);
        Assert.assertThat(dataPointsGroup.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(getTagGroupMap(dataPointsGroup.get(0)), hasEntry("tag1", ""));
        Assert.assertThat(getTagGroupMap(dataPointsGroup.get(0)), hasEntry("tag2", "value"));
        Assert.assertThat(getTagGroupMap(dataPointsGroup.get(1)), hasEntry("tag1", "value"));
        Assert.assertThat(getTagGroupMap(dataPointsGroup.get(1)), hasEntry("tag2", ""));
    }

    @Test
    public void test_groupByTypeAndTag_DifferentTagValues() throws DatastoreException {
        KairosDatastoreTest.TestKairosDatastore datastore = new KairosDatastoreTest.TestKairosDatastore(new KairosDatastoreTest.TestDatastore(), new QueryQueuingManager(1, "hostname"), new TestDataPointFactory());
        TagGroupBy groupBy = new TagGroupBy("tag1", "tag2");
        List<DataPointRow> rows = new ArrayList<>();
        DataPointRowImpl row1 = new DataPointRowImpl();
        row1.addTag("tag1", "value1");
        row1.addDataPoint(new LongDataPoint(1234, 1));
        DataPointRowImpl row2 = new DataPointRowImpl();
        row2.addTag("tag2", "value2");
        row2.addDataPoint(new LongDataPoint(1235, 2));
        rows.add(row1);
        rows.add(row2);
        List<DataPointGroup> dataPoints = datastore.groupByTypeAndTag("metricName", rows, groupBy, ASC);
        Assert.assertThat(dataPoints.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(getTagGroupMap(dataPoints.get(0)), hasEntry("tag1", ""));
        Assert.assertThat(getTagGroupMap(dataPoints.get(0)), hasEntry("tag2", "value2"));
        Assert.assertThat(getTagGroupMap(dataPoints.get(1)), hasEntry("tag1", "value1"));
        Assert.assertThat(getTagGroupMap(dataPoints.get(1)), hasEntry("tag2", ""));
    }

    @Test
    public void test_groupByTypeAndTag_MultipleTags() throws DatastoreException {
        KairosDatastoreTest.TestKairosDatastore datastore = new KairosDatastoreTest.TestKairosDatastore(new KairosDatastoreTest.TestDatastore(), new QueryQueuingManager(1, "hostname"), new TestDataPointFactory());
        /* The order of the returned data must be stored first by tag1 and
        then by tag 2 as specified in the caller group by.
         */
        TagGroupBy groupBy = new TagGroupBy("tag1", "tag2");
        List<DataPointRow> rows = new ArrayList<>();
        DataPointRowImpl row1 = new DataPointRowImpl();
        row1.addTag("tag1", "value1");
        row1.addTag("tag2", "value2");
        row1.addDataPoint(new LongDataPoint(1234, 1));
        DataPointRowImpl row2 = new DataPointRowImpl();
        row2.addTag("tag1", "value1");
        row2.addTag("tag2", "value3");
        row2.addDataPoint(new LongDataPoint(1235, 2));
        DataPointRowImpl row3 = new DataPointRowImpl();
        row3.addTag("tag1", "value4");
        row3.addTag("tag2", "value2");
        row3.addDataPoint(new LongDataPoint(1235, 2));
        rows.add(row1);
        rows.add(row2);
        rows.add(row3);
        List<DataPointGroup> dataPoints = datastore.groupByTypeAndTag("metricName", rows, groupBy, ASC);
        Assert.assertThat(dataPoints.size(), CoreMatchers.equalTo(3));
        Assert.assertThat(getTagGroupMap(dataPoints.get(0)), hasEntry("tag1", "value1"));
        Assert.assertThat(getTagGroupMap(dataPoints.get(0)), hasEntry("tag2", "value2"));
        Assert.assertThat(getTagGroupMap(dataPoints.get(1)), hasEntry("tag1", "value1"));
        Assert.assertThat(getTagGroupMap(dataPoints.get(1)), hasEntry("tag2", "value3"));
        Assert.assertThat(getTagGroupMap(dataPoints.get(2)), hasEntry("tag1", "value4"));
        Assert.assertThat(getTagGroupMap(dataPoints.get(2)), hasEntry("tag2", "value2"));
    }

    private static class TestKairosDatastore extends KairosDatastore {
        TestKairosDatastore(Datastore datastore, QueryQueuingManager queuingManager, KairosDataPointFactory dataPointFactory) throws DatastoreException {
            super(datastore, queuingManager, dataPointFactory, false);
        }
    }

    private static class TestDatastore implements Datastore , ServiceKeyStore {
        TestDatastore() {
        }

        @Override
        public void close() {
        }

        @Override
        public Iterable<String> getMetricNames(String prefix) {
            return null;
        }

        @Override
        public Iterable<String> getTagNames() {
            return null;
        }

        @Override
        public Iterable<String> getTagValues() {
            return null;
        }

        @Override
        public void queryDatabase(DatastoreMetricQuery query, QueryCallback queryCallback) throws DatastoreException {
            try {
                QueryCallback.DataPointWriter dataPointWriter = queryCallback.startDataPointSet(DATASTORE_TYPE, Collections.emptySortedMap());
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(1, 3));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(1, 10));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(1, 20));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(2, 1));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(2, 3));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(2, 5));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(3, 25));
                dataPointWriter.close();
                dataPointWriter = queryCallback.startDataPointSet(DATASTORE_TYPE, Collections.emptySortedMap());
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(1, 5));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(1, 14));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(1, 20));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(2, 6));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(2, 8));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(2, 9));
                dataPointWriter.addDataPoint(new LegacyLongDataPoint(3, 7));
                dataPointWriter.close();
            } catch (IOException e) {
                throw new DatastoreException(e);
            }
        }

        @Override
        public void deleteDataPoints(DatastoreMetricQuery deleteQuery) {
        }

        @Override
        public TagSet queryMetricTags(DatastoreMetricQuery query) {
            return null;// To change body of implemented methods use File | Settings | File Templates.

        }

        @Override
        public void setValue(String service, String serviceKey, String key, String value) {
        }

        @Override
        public ServiceKeyValue getValue(String service, String serviceKey, String key) {
            return null;
        }

        @Override
        public Iterable<String> listServiceKeys(String service) {
            return null;
        }

        @Override
        public Iterable<String> listKeys(String service, String serviceKey) {
            return null;
        }

        @Override
        public Iterable<String> listKeys(String service, String serviceKey, String keyStartsWith) {
            return null;
        }

        @Override
        public void deleteKey(String service, String serviceKey, String key) {
        }

        @Override
        public Date getServiceKeyLastModifiedTime(String service, String serviceKey) {
            return null;
        }
    }
}

