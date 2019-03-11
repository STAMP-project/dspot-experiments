package org.kairosdb.rollup;


import QueryCallback.DataPointWriter;
import com.google.common.collect.ImmutableSortedMap;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kairosdb.core.DataPoint;
import org.kairosdb.core.datapoints.DoubleDataPoint;
import org.kairosdb.core.datapoints.DoubleDataPointFactory;
import org.kairosdb.core.datastore.KairosDatastore;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.eventbus.Subscribe;
import org.kairosdb.plugin.Aggregator;
import org.kairosdb.testing.ListDataPointGroup;
import org.mockito.Mockito;

import static TimeUnit.DAYS;
import static TimeUnit.HOURS;
import static TimeUnit.MINUTES;


public class RollUpJobTest {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SS", Locale.ENGLISH);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private long lastTimeStamp;

    private KairosDatastore datastore;

    private RollUpJobTest.TestDatastore testDataStore;

    @Test
    public void test_getLastSampling() {
        Sampling sampling1 = new Sampling(1, DAYS);
        Sampling sampling2 = new Sampling(2, MINUTES);
        DoubleDataPointFactory dataPointFactory = Mockito.mock(DoubleDataPointFactory.class);
        MinAggregator minAggregator = new MinAggregator(dataPointFactory);
        minAggregator.setSampling(sampling1);
        MaxAggregator maxAggregator = new MaxAggregator(dataPointFactory);
        maxAggregator.setSampling(sampling2);
        List<Aggregator> aggregators = new ArrayList<>();
        aggregators.add(minAggregator);
        aggregators.add(maxAggregator);
        aggregators.add(new DivideAggregator(dataPointFactory));
        aggregators.add(new DiffAggregator(dataPointFactory));
        Sampling lastSampling = RollUpJob.getLastSampling(aggregators);
        Assert.assertThat(lastSampling, CoreMatchers.equalTo(sampling2));
        aggregators = new ArrayList();
        aggregators.add(maxAggregator);
        aggregators.add(new DivideAggregator(dataPointFactory));
        aggregators.add(new DiffAggregator(dataPointFactory));
        aggregators.add(minAggregator);
        lastSampling = RollUpJob.getLastSampling(aggregators);
        Assert.assertThat(lastSampling, CoreMatchers.equalTo(sampling1));
    }

    @Test
    public void test_getLastSampling_no_sampling() {
        DoubleDataPointFactory dataPointFactory = Mockito.mock(DoubleDataPointFactory.class);
        List<Aggregator> aggregators = new ArrayList<>();
        aggregators.add(new DivideAggregator(dataPointFactory));
        aggregators.add(new DiffAggregator(dataPointFactory));
        Sampling lastSampling = RollUpJob.getLastSampling(aggregators);
        Assert.assertThat(lastSampling, CoreMatchers.equalTo(null));
    }

    @Test
    public void test_getLastRollupDataPoint() throws ParseException, DatastoreException {
        long now = RollUpJobTest.dateFormat.parse("2013-Jan-18 4:59:12.22").getTime();
        String metricName = "foo";
        ImmutableSortedMap<String, String> localHostTags = ImmutableSortedMap.of("host", "localhost");
        List<DataPoint> localhostDataPoints = new ArrayList<>();
        localhostDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 1), 10));
        localhostDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 2), 11));
        localhostDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 3), 12));
        localhostDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 4), 13));
        localhostDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 5), 14));
        ImmutableSortedMap<String, String> remoteTags = ImmutableSortedMap.of("host", "remote");
        List<DataPoint> remoteDataPoints = new ArrayList<>();
        remoteDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 1), 10));
        remoteDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 2), 11));
        testDataStore.clear();
        testDataStore.putDataPoints(metricName, localHostTags, localhostDataPoints);
        testDataStore.putDataPoints(metricName, remoteTags, remoteDataPoints);
        DataPoint lastDataPoint = RollUpJob.getLastRollupDataPoint(datastore, metricName, now);
        // Look back from now and find last data point [4]
        Assert.assertThat(lastDataPoint, CoreMatchers.equalTo(localhostDataPoints.get(4)));
    }

    @Test
    public void test_getLastRollupDataPoint_noDataPoints() throws ParseException, DatastoreException {
        long now = RollUpJobTest.dateFormat.parse("2013-Jan-18 4:59:12.22").getTime();
        String metricName = "foo";
        testDataStore.clear();
        DataPoint lastDataPoint = RollUpJob.getLastRollupDataPoint(datastore, metricName, now);
        Assert.assertThat(lastDataPoint, CoreMatchers.equalTo(null));
    }

    @Test
    public void test_getgetFutureDataPoint() throws ParseException, DatastoreException {
        long now = RollUpJobTest.dateFormat.parse("2013-Jan-18 4:59:12.22").getTime();
        String metricName = "foo";
        ImmutableSortedMap<String, String> localHostTags = ImmutableSortedMap.of("host", "localhost");
        List<DataPoint> localhostDataPoints = new ArrayList<>();
        localhostDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 1), 10));
        localhostDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 2), 11));
        localhostDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 3), 12));
        localhostDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 4), 13));
        localhostDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 5), 14));
        ImmutableSortedMap<String, String> remoteTags = ImmutableSortedMap.of("host", "remote");
        List<DataPoint> remoteDataPoints = new ArrayList<>();
        remoteDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 1), 10));
        remoteDataPoints.add(new DoubleDataPoint(((lastTimeStamp) + 2), 11));
        testDataStore.clear();
        testDataStore.putDataPoints(metricName, localHostTags, localhostDataPoints);
        testDataStore.putDataPoints(metricName, remoteTags, remoteDataPoints);
        // Look from data point [1] forward and return [2]
        DataPoint futureDataPoint = RollUpJob.getFutureDataPoint(datastore, metricName, now, localhostDataPoints.get(1));
        Assert.assertThat(futureDataPoint, CoreMatchers.equalTo(localhostDataPoints.get(2)));
    }

    @Test
    public void test_calculatStartTime_datapointTime() {
        Sampling sampling = new Sampling();
        DoubleDataPoint dataPoint = new DoubleDataPoint(123456L, 10);
        long time = RollUpJob.calculateStartTime(dataPoint, sampling, System.currentTimeMillis());
        Assert.assertThat(time, CoreMatchers.equalTo(123456L));
    }

    @Test
    public void test_calculatStartTime_samplingTime() throws ParseException {
        long now = RollUpJobTest.dateFormat.parse("2013-Jan-18 4:59:12.22").getTime();
        Sampling sampling = new Sampling(1, HOURS);
        long time = RollUpJob.calculateStartTime(null, sampling, now);
        Assert.assertThat(time, CoreMatchers.equalTo(RollUpJobTest.dateFormat.parse("2013-Jan-18 3:59:12.22").getTime()));
    }

    @Test(expected = NullPointerException.class)
    public void test_calculatStartTime_samplingNull_invalid() {
        DoubleDataPoint dataPoint = new DoubleDataPoint(123456L, 10);
        RollUpJob.calculateStartTime(dataPoint, null, System.currentTimeMillis());
    }

    @Test
    public void test_calculatEndTime_datapoint_null() {
        long now = System.currentTimeMillis();
        Duration executionInterval = new Duration();
        long time = RollUpJob.calculateEndTime(null, executionInterval, now);
        Assert.assertThat(time, CoreMatchers.equalTo(now));
    }

    @Test
    public void test_calculatEndTime_datapointNotNull_recentTime() {
        long now = System.currentTimeMillis();
        Duration executionInterval = new Duration();
        DoubleDataPoint dataPoint = new DoubleDataPoint((now - 2000), 10);
        long time = RollUpJob.calculateEndTime(dataPoint, executionInterval, now);
        Assert.assertThat(time, CoreMatchers.equalTo(dataPoint.getTimestamp()));
    }

    @Test
    public void test_calculatEndTime_datapointNotNull_tooOld() throws ParseException {
        long datapointTime = RollUpJobTest.dateFormat.parse("2013-Jan-18 4:59:12.22").getTime();
        long now = System.currentTimeMillis();
        Duration executionInterval = new Duration(1, DAYS);
        DoubleDataPoint dataPoint = new DoubleDataPoint(datapointTime, 10);
        long time = RollUpJob.calculateEndTime(dataPoint, executionInterval, now);
        Assert.assertThat(time, CoreMatchers.equalTo(RollUpJobTest.dateFormat.parse("2013-Jan-22 4:59:12.22").getTime()));
    }

    public static class TestDatastore implements Datastore {
        List<ListDataPointGroup> dataPointGroups = new ArrayList<>();

        void clear() {
            dataPointGroups = new ArrayList<>();
        }

        @Override
        public void close() {
        }

        void putDataPoints(String metricName, ImmutableSortedMap<String, String> tags, List<DataPoint> dataPoints) {
            ListDataPointGroup dataPointGroup = new ListDataPointGroup(metricName);
            for (Map.Entry<String, String> tag : tags.entrySet()) {
                addTag(tag.getKey(), tag.getValue());
            }
            for (DataPoint dataPoint : dataPoints) {
                dataPointGroup.addDataPoint(dataPoint);
            }
            dataPointGroups.add(dataPointGroup);
        }

        @Subscribe
        public void putDataPoint(String metricName, ImmutableSortedMap<String, String> tags, DataPoint dataPoint, int ttl) {
            ListDataPointGroup dataPointGroup = new ListDataPointGroup(metricName);
            dataPointGroup.addDataPoint(dataPoint);
            for (Map.Entry<String, String> tag : tags.entrySet()) {
                addTag(tag.getKey(), tag.getValue());
            }
            dataPointGroups.add(dataPointGroup);
        }

        @Override
        public Iterable<String> getMetricNames(String prefix) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterable<String> getTagNames() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Iterable<String> getTagValues() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void queryDatabase(DatastoreMetricQuery query, QueryCallback queryCallback) throws DatastoreException {
            for (ListDataPointGroup dataPointGroup : dataPointGroups) {
                try {
                    dataPointGroup.sort(query.getOrder());
                    SortedMap<String, String> tags = new TreeMap<>();
                    for (String tagName : dataPointGroup.getTagNames()) {
                        tags.put(tagName, dataPointGroup.getTagValues(tagName).iterator().next());
                    }
                    DataPoint dataPoint = getNext(dataPointGroup, query);
                    if (dataPoint != null) {
                        try (QueryCallback.DataPointWriter dataPointWriter = queryCallback.startDataPointSet(dataPoint.getDataStoreDataType(), tags)) {
                            dataPointWriter.addDataPoint(dataPoint);
                            while (dataPointGroup.hasNext()) {
                                DataPoint next = getNext(dataPointGroup, query);
                                if (next != null) {
                                    dataPointWriter.addDataPoint(next);
                                }
                            } 
                        }
                    }
                } catch (IOException e) {
                    throw new DatastoreException(e);
                }
            }
        }

        private DataPoint getNext(DataPointGroup group, DatastoreMetricQuery query) {
            DataPoint dataPoint = null;
            while (group.hasNext()) {
                DataPoint dp = group.next();
                if ((dp.getTimestamp()) >= (query.getStartTime())) {
                    dataPoint = dp;
                    break;
                }
            } 
            return dataPoint;
        }

        @Override
        public void deleteDataPoints(DatastoreMetricQuery deleteQuery) {
            throw new UnsupportedOperationException();
        }

        @Override
        public TagSet queryMetricTags(DatastoreMetricQuery query) {
            throw new UnsupportedOperationException();
        }
    }
}

