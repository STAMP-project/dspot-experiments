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
package org.apache.druid.query.groupby;


import Granularities.ALL;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.StringUtils;
import org.apache.druid.java.util.common.io.Closer;
import org.apache.druid.query.Druids;
import org.apache.druid.query.QueryPlus;
import org.apache.druid.query.QueryRunner;
import org.apache.druid.query.QueryRunnerTestHelper;
import org.apache.druid.query.Result;
import org.apache.druid.query.aggregation.DoubleMaxAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleMinAggregatorFactory;
import org.apache.druid.query.timeseries.TimeseriesQuery;
import org.apache.druid.query.timeseries.TimeseriesQueryRunnerTest;
import org.apache.druid.query.timeseries.TimeseriesResultValue;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 *
 */
@RunWith(Parameterized.class)
public class GroupByTimeseriesQueryRunnerTest extends TimeseriesQueryRunnerTest {
    private static final Closer resourceCloser = Closer.create();

    public GroupByTimeseriesQueryRunnerTest(QueryRunner runner) {
        super(runner, false, QueryRunnerTestHelper.commonDoubleAggregators);
    }

    // GroupBy handles timestamps differently when granularity is ALL
    @Override
    @Test
    public void testFullOnTimeseriesMaxMin() {
        TimeseriesQuery query = Druids.newTimeseriesQueryBuilder().dataSource(QueryRunnerTestHelper.dataSource).granularity(ALL).intervals(QueryRunnerTestHelper.fullOnIntervalSpec).aggregators(new DoubleMaxAggregatorFactory("maxIndex", "index"), new DoubleMinAggregatorFactory("minIndex", "index")).descending(descending).build();
        DateTime expectedEarliest = DateTimes.of("1970-01-01");
        DateTime expectedLast = DateTimes.of("2011-04-15");
        Iterable<Result<TimeseriesResultValue>> results = runner.run(QueryPlus.wrap(query), TimeseriesQueryRunnerTest.CONTEXT).toList();
        Result<TimeseriesResultValue> result = results.iterator().next();
        Assert.assertEquals(expectedEarliest, result.getTimestamp());
        Assert.assertFalse(StringUtils.format("Timestamp[%s] > expectedLast[%s]", result.getTimestamp(), expectedLast), result.getTimestamp().isAfter(expectedLast));
        final TimeseriesResultValue value = result.getValue();
        Assert.assertEquals(result.toString(), 1870.061029, value.getDoubleMetric("maxIndex"), (1870.061029 * 1.0E-6));
        Assert.assertEquals(result.toString(), 59.021022, value.getDoubleMetric("minIndex"), (59.021022 * 1.0E-6));
    }
}

