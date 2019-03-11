package org.kairosdb.core.aggregator;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.DataPoint;
import org.kairosdb.core.datapoints.DoubleDataPointFactoryImpl;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.testing.ListDataPointGroup;


/**
 * Created by bhawkins on 1/8/15.
 */
public class DiffAggregatorTest {
    @Test(expected = NullPointerException.class)
    public void test_nullSet_invalid() {
        aggregate(null);
    }

    @Test
    public void test_steadyRate() {
        ListDataPointGroup group = new ListDataPointGroup("rate");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        group.addDataPoint(new LongDataPoint(3, 30));
        group.addDataPoint(new LongDataPoint(4, 40));
        DiffAggregator DiffAggregator = new DiffAggregator(new DoubleDataPointFactoryImpl());
        DataPointGroup results = DiffAggregator.aggregate(group);
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(10.0));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(10.0));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(4L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(10.0));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_changingRate() {
        ListDataPointGroup group = new ListDataPointGroup("rate");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 10));
        group.addDataPoint(new LongDataPoint(3, 5));
        group.addDataPoint(new LongDataPoint(4, 20));
        DiffAggregator DiffAggregator = new DiffAggregator(new DoubleDataPointFactoryImpl());
        DataPointGroup results = DiffAggregator.aggregate(group);
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(0.0));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo((-5.0)));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(4L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(15.0));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_steadyRateOver2Sec() {
        ListDataPointGroup group = new ListDataPointGroup("rate");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(3, 20));
        group.addDataPoint(new LongDataPoint(5, 30));
        group.addDataPoint(new LongDataPoint(7, 40));
        DiffAggregator rateAggregator = new DiffAggregator(new DoubleDataPointFactoryImpl());
        DataPointGroup results = rateAggregator.aggregate(group);
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(10.0));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(5L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(10.0));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(7L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(10.0));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_dataPointsAtSameTime() {
        ListDataPointGroup group = new ListDataPointGroup("rate");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(1, 15));
        group.addDataPoint(new LongDataPoint(2, 5));
        group.addDataPoint(new LongDataPoint(2, 20));
        group.addDataPoint(new LongDataPoint(3, 30));
        DiffAggregator DiffAggregator = new DiffAggregator(new DoubleDataPointFactoryImpl());
        DataPointGroup results = DiffAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(5.0));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo((-10.0)));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(15.0));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dp.getDoubleValue(), CoreMatchers.equalTo(10.0));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }
}

