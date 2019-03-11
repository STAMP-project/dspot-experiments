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
package org.apache.druid.client.indexing;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.timeline.DataSegment;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;


public class ClientMergeQueryTest {
    private static final String DATA_SOURCE = "data_source";

    public static final DateTime START = DateTimes.nowUtc();

    private static final Interval INTERVAL = new Interval(ClientMergeQueryTest.START, ClientMergeQueryTest.START.plus(1));

    private static final DataSegment DATA_SEGMENT = new DataSegment(ClientMergeQueryTest.DATA_SOURCE, ClientMergeQueryTest.INTERVAL, ClientMergeQueryTest.START.toString(), null, null, null, null, 0, 0);

    private static final List<DataSegment> SEGMENT_LIST = Collections.singletonList(ClientMergeQueryTest.DATA_SEGMENT);

    private static final List<AggregatorFactory> AGGREGATOR_LIST = new ArrayList<>();

    private static final ClientMergeQuery CLIENT_MERGE_QUERY = new ClientMergeQuery(ClientMergeQueryTest.DATA_SOURCE, ClientMergeQueryTest.SEGMENT_LIST, ClientMergeQueryTest.AGGREGATOR_LIST);

    @Test
    public void testGetType() {
        Assert.assertEquals("merge", ClientMergeQueryTest.CLIENT_MERGE_QUERY.getType());
    }

    @Test
    public void testGetDataSource() {
        Assert.assertEquals(ClientMergeQueryTest.DATA_SOURCE, ClientMergeQueryTest.CLIENT_MERGE_QUERY.getDataSource());
    }

    @Test
    public void testGetSegments() {
        Assert.assertEquals(ClientMergeQueryTest.SEGMENT_LIST, ClientMergeQueryTest.CLIENT_MERGE_QUERY.getSegments());
    }

    @Test
    public void testGetAggregators() {
        Assert.assertEquals(ClientMergeQueryTest.AGGREGATOR_LIST, ClientMergeQueryTest.CLIENT_MERGE_QUERY.getAggregators());
    }

    @Test
    public void testToString() {
        Assert.assertTrue(ClientMergeQueryTest.CLIENT_MERGE_QUERY.toString().contains(ClientMergeQueryTest.DATA_SOURCE));
        Assert.assertTrue(ClientMergeQueryTest.CLIENT_MERGE_QUERY.toString().contains(ClientMergeQueryTest.SEGMENT_LIST.toString()));
        Assert.assertTrue(ClientMergeQueryTest.CLIENT_MERGE_QUERY.toString().contains(ClientMergeQueryTest.AGGREGATOR_LIST.toString()));
    }
}

