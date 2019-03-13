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
package org.apache.druid.query.timeseries;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.query.Result;
import org.apache.druid.segment.incremental.IncrementalIndex;
import org.apache.druid.segment.incremental.IncrementalIndexSchema;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class TimeseriesQueryRunnerBonusTest {
    private final boolean descending;

    public TimeseriesQueryRunnerBonusTest(boolean descending) {
        this.descending = descending;
    }

    @Test
    public void testOneRowAtATime() throws Exception {
        final IncrementalIndex oneRowIndex = new IncrementalIndex.Builder().setIndexSchema(new IncrementalIndexSchema.Builder().withMinTimestamp(DateTimes.of("2012-01-01T00:00:00Z").getMillis()).build()).setMaxRowCount(1000).buildOnheap();
        List<Result<TimeseriesResultValue>> results;
        oneRowIndex.add(new org.apache.druid.data.input.MapBasedInputRow(DateTimes.of("2012-01-01T00:00:00Z").getMillis(), ImmutableList.of("dim1"), ImmutableMap.of("dim1", "x")));
        results = runTimeseriesCount(oneRowIndex);
        Assert.assertEquals("index size", 1, oneRowIndex.size());
        Assert.assertEquals("result size", 1, results.size());
        Assert.assertEquals("result timestamp", DateTimes.of("2012-01-01T00:00:00Z"), results.get(0).getTimestamp());
        Assert.assertEquals("result count metric", 1, ((long) (results.get(0).getValue().getLongMetric("rows"))));
        oneRowIndex.add(new org.apache.druid.data.input.MapBasedInputRow(DateTimes.of("2012-01-01T00:00:00Z").getMillis(), ImmutableList.of("dim1"), ImmutableMap.of("dim1", "y")));
        results = runTimeseriesCount(oneRowIndex);
        Assert.assertEquals("index size", 2, oneRowIndex.size());
        Assert.assertEquals("result size", 1, results.size());
        Assert.assertEquals("result timestamp", DateTimes.of("2012-01-01T00:00:00Z"), results.get(0).getTimestamp());
        Assert.assertEquals("result count metric", 2, ((long) (results.get(0).getValue().getLongMetric("rows"))));
    }
}

