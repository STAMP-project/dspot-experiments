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
package org.kairosdb.core.groupby;


import java.util.HashMap;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.datapoints.DoubleDataPoint;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.formatter.FormatterException;


public class ValueGroupByTest {
    @Test(expected = IllegalArgumentException.class)
    public void test_groupSizeZero_invalid() {
        new ValueGroupBy(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_groupSizeNegative_invalid() {
        new ValueGroupBy((-1));
    }

    @Test
    public void test_GroupByResultJson() throws FormatterException {
        ValueGroupBy groupBy = new ValueGroupBy(3);
        GroupByResult groupByResult = groupBy.getGroupByResult(2);
        Assert.assertThat(groupByResult.toJson(), CoreMatchers.equalTo("{\"name\":\"value\",\"range_size\":3,\"group\":{\"group_number\":2}}"));
    }

    @Test
    public void test_getGroupId_longValue() {
        Map<String, String> tags = new HashMap<String, String>();
        ValueGroupBy groupBy = new ValueGroupBy(3);
        Assert.assertThat(groupBy.getGroupId(new LongDataPoint(1, 0L), tags), CoreMatchers.equalTo(0));
        Assert.assertThat(groupBy.getGroupId(new LongDataPoint(1, 1L), tags), CoreMatchers.equalTo(0));
        Assert.assertThat(groupBy.getGroupId(new LongDataPoint(1, 2L), tags), CoreMatchers.equalTo(0));
        Assert.assertThat(groupBy.getGroupId(new LongDataPoint(1, 3L), tags), CoreMatchers.equalTo(1));
        Assert.assertThat(groupBy.getGroupId(new LongDataPoint(1, 4L), tags), CoreMatchers.equalTo(1));
        Assert.assertThat(groupBy.getGroupId(new LongDataPoint(1, 5L), tags), CoreMatchers.equalTo(1));
        Assert.assertThat(groupBy.getGroupId(new LongDataPoint(1, 6L), tags), CoreMatchers.equalTo(2));
    }

    @Test
    public void test_getGroupId_doubleValue() {
        Map<String, String> tags = new HashMap<String, String>();
        ValueGroupBy groupBy = new ValueGroupBy(3);
        Assert.assertThat(groupBy.getGroupId(new DoubleDataPoint(1, 0.0), tags), CoreMatchers.equalTo(0));
        Assert.assertThat(groupBy.getGroupId(new DoubleDataPoint(1, 1.2), tags), CoreMatchers.equalTo(0));
        Assert.assertThat(groupBy.getGroupId(new DoubleDataPoint(1, 2.3), tags), CoreMatchers.equalTo(0));
        Assert.assertThat(groupBy.getGroupId(new DoubleDataPoint(1, 3.3), tags), CoreMatchers.equalTo(1));
        Assert.assertThat(groupBy.getGroupId(new DoubleDataPoint(1, 4.4), tags), CoreMatchers.equalTo(1));
        Assert.assertThat(groupBy.getGroupId(new DoubleDataPoint(1, 5.2), tags), CoreMatchers.equalTo(1));
        Assert.assertThat(groupBy.getGroupId(new DoubleDataPoint(1, 6.1), tags), CoreMatchers.equalTo(2));
    }
}

