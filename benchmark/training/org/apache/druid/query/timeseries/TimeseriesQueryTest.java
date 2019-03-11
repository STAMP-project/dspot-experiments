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


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.apache.druid.query.Query;
import org.apache.druid.segment.TestHelper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.druid.query.QueryRunnerTestHelper.addRowsIndexConstant;
import static org.apache.druid.query.QueryRunnerTestHelper.dataSource;
import static org.apache.druid.query.QueryRunnerTestHelper.dayGran;
import static org.apache.druid.query.QueryRunnerTestHelper.fullOnIntervalSpec;
import static org.apache.druid.query.QueryRunnerTestHelper.indexDoubleSum;
import static org.apache.druid.query.QueryRunnerTestHelper.rowsCount;


@RunWith(Parameterized.class)
public class TimeseriesQueryTest {
    private static final ObjectMapper jsonMapper = TestHelper.makeJsonMapper();

    private final boolean descending;

    public TimeseriesQueryTest(boolean descending) {
        this.descending = descending;
    }

    @Test
    public void testQuerySerialization() throws IOException {
        Query query = org.apache.druid.query.Druids.newTimeseriesQueryBuilder().dataSource(dataSource).granularity(dayGran).intervals(fullOnIntervalSpec).aggregators(rowsCount, indexDoubleSum).postAggregators(addRowsIndexConstant).descending(descending).build();
        String json = TimeseriesQueryTest.jsonMapper.writeValueAsString(query);
        Query serdeQuery = TimeseriesQueryTest.jsonMapper.readValue(json, Query.class);
        Assert.assertEquals(query, serdeQuery);
    }
}

