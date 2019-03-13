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
package org.kairosdb.core.aggregator;


import FilterAggregator.FilterOperation.EQUAL;
import FilterAggregator.FilterOperation.GT;
import FilterAggregator.FilterOperation.GTE;
import FilterAggregator.FilterOperation.LT;
import FilterAggregator.FilterOperation.LTE;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.DataPoint;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.testing.ListDataPointGroup;


public class FilterAggregatorTest {
    @Test
    public void test_LessThanFilter() {
        ListDataPointGroup group = new ListDataPointGroup("test_lt_values");
        group.addDataPoint(new LongDataPoint(1, (-10)));
        group.addDataPoint(new LongDataPoint(2, (-20)));
        group.addDataPoint(new LongDataPoint(3, 30));
        group.addDataPoint(new LongDataPoint(4, 40));
        group.addDataPoint(new LongDataPoint(5, (-50)));
        FilterAggregator filterAggregator = new FilterAggregator();
        filterAggregator.setFilterOp(LT);
        filterAggregator.setThreshold(0.0);
        DataPointGroup results = filterAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(30L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(4L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(40L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_LessThanFilterAllNegative() {
        ListDataPointGroup group = new ListDataPointGroup("test_lt_all_neg_values");
        group.addDataPoint(new LongDataPoint(1, (-10)));
        group.addDataPoint(new LongDataPoint(2, (-20)));
        group.addDataPoint(new LongDataPoint(3, (-30)));
        group.addDataPoint(new LongDataPoint(4, (-40)));
        group.addDataPoint(new LongDataPoint(5, (-50)));
        FilterAggregator filterAggregator = new FilterAggregator();
        filterAggregator.setFilterOp(LT);
        filterAggregator.setThreshold(0.0);
        DataPointGroup results = filterAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_LessThanEqualToFilter() {
        ListDataPointGroup group = new ListDataPointGroup("test_lte_values");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        group.addDataPoint(new LongDataPoint(3, 15));
        group.addDataPoint(new LongDataPoint(4, 30));
        group.addDataPoint(new LongDataPoint(5, 10));
        group.addDataPoint(new LongDataPoint(6, 25));
        FilterAggregator filterAggregator = new FilterAggregator();
        filterAggregator.setFilterOp(LTE);
        filterAggregator.setThreshold(15.0);
        DataPointGroup results = filterAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(20L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(4L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(30L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(6L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(25L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_GreaterThanFilter() {
        ListDataPointGroup group = new ListDataPointGroup("test_gt_values");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        group.addDataPoint(new LongDataPoint(3, 15));
        group.addDataPoint(new LongDataPoint(4, 30));
        group.addDataPoint(new LongDataPoint(5, 10));
        group.addDataPoint(new LongDataPoint(6, 25));
        FilterAggregator filterAggregator = new FilterAggregator();
        filterAggregator.setFilterOp(GT);
        filterAggregator.setThreshold(20.0);
        DataPointGroup results = filterAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(10L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(20L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(15L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(5L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(10L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_GreaterThanEqualToFilter() {
        ListDataPointGroup group = new ListDataPointGroup("test_gte_values");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        group.addDataPoint(new LongDataPoint(3, 15));
        group.addDataPoint(new LongDataPoint(4, 30));
        group.addDataPoint(new LongDataPoint(5, 10));
        group.addDataPoint(new LongDataPoint(6, 25));
        FilterAggregator filterAggregator = new FilterAggregator();
        filterAggregator.setFilterOp(GTE);
        filterAggregator.setThreshold(20.0);
        DataPointGroup results = filterAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(10L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(15L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(5L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(10L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }

    @Test
    public void test_EqualToFilter() {
        ListDataPointGroup group = new ListDataPointGroup("test_eq_values");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        group.addDataPoint(new LongDataPoint(3, 15));
        group.addDataPoint(new LongDataPoint(4, 30));
        group.addDataPoint(new LongDataPoint(5, 10));
        group.addDataPoint(new LongDataPoint(6, 25));
        FilterAggregator filterAggregator = new FilterAggregator();
        filterAggregator.setFilterOp(EQUAL);
        filterAggregator.setThreshold(10.0);
        DataPointGroup results = filterAggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(20L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(3L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(15L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(4L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(30L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dp = results.next();
        Assert.assertThat(dp.getTimestamp(), CoreMatchers.equalTo(6L));
        Assert.assertThat(dp.getLongValue(), CoreMatchers.equalTo(25L));
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(false));
    }
}

