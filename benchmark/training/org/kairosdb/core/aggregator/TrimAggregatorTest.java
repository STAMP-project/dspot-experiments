package org.kairosdb.core.aggregator;


import TrimAggregator.Trim.BOTH;
import TrimAggregator.Trim.FIRST;
import TrimAggregator.Trim.LAST;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.DataPoint;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.testing.ListDataPointGroup;


/**
 * Created by bhawkins on 8/28/15.
 */
public class TrimAggregatorTest {
    @Test
    public void test_oneDataPointWithTrimFirst() {
        ListDataPointGroup group = new ListDataPointGroup("trim_test");
        group.addDataPoint(new LongDataPoint(1, 10));
        TrimAggregator trimAggregator = new TrimAggregator();
        trimAggregator.setTrim(FIRST);
        DataPointGroup results = trimAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_twoDataPointsWithTrimFirst() {
        ListDataPointGroup group = new ListDataPointGroup("trim_test");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        TrimAggregator trimAggregator = new TrimAggregator();
        trimAggregator.setTrim(FIRST);
        DataPointGroup results = trimAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(20L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_threeDataPointWithTrimFirst() {
        ListDataPointGroup group = new ListDataPointGroup("trim_test");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        group.addDataPoint(new LongDataPoint(3, 30));
        TrimAggregator trimAggregator = new TrimAggregator();
        trimAggregator.setTrim(FIRST);
        DataPointGroup results = trimAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(20L));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(30L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_oneDataPointWithTrimLast() {
        ListDataPointGroup group = new ListDataPointGroup("trim_test");
        group.addDataPoint(new LongDataPoint(1, 10));
        TrimAggregator trimAggregator = new TrimAggregator();
        trimAggregator.setTrim(LAST);
        DataPointGroup results = trimAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_twoDataPointsWithTrimLast() {
        ListDataPointGroup group = new ListDataPointGroup("trim_test");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        TrimAggregator trimAggregator = new TrimAggregator();
        trimAggregator.setTrim(LAST);
        DataPointGroup results = trimAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(10L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_threeDataPointWithTrimLast() {
        ListDataPointGroup group = new ListDataPointGroup("trim_test");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        group.addDataPoint(new LongDataPoint(3, 30));
        TrimAggregator trimAggregator = new TrimAggregator();
        trimAggregator.setTrim(LAST);
        DataPointGroup results = trimAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(10L));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(20L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_oneDataPointWithTrimBoth() {
        ListDataPointGroup group = new ListDataPointGroup("trim_test");
        group.addDataPoint(new LongDataPoint(1, 10));
        TrimAggregator trimAggregator = new TrimAggregator();
        trimAggregator.setTrim(BOTH);
        DataPointGroup results = trimAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_twoDataPointsWithTrimBoth() {
        ListDataPointGroup group = new ListDataPointGroup("trim_test");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        TrimAggregator trimAggregator = new TrimAggregator();
        trimAggregator.setTrim(BOTH);
        DataPointGroup results = trimAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_threeDataPointWithTrimBoth() {
        ListDataPointGroup group = new ListDataPointGroup("trim_test");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        group.addDataPoint(new LongDataPoint(3, 30));
        TrimAggregator trimAggregator = new TrimAggregator();
        trimAggregator.setTrim(BOTH);
        DataPointGroup results = trimAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(20L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }
}

