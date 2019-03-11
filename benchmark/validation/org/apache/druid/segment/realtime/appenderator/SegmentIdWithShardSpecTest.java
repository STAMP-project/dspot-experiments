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
package org.apache.druid.segment.realtime.appenderator;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.partition.NumberedShardSpec;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;


public class SegmentIdWithShardSpecTest {
    private static final String DATA_SOURCE = "foo";

    private static final Interval INTERVAL = Intervals.of("2000/PT1H");

    private static final String VERSION = "v1";

    private static final NumberedShardSpec SHARD_SPEC_0 = new NumberedShardSpec(0, 2);

    private static final NumberedShardSpec SHARD_SPEC_1 = new NumberedShardSpec(1, 2);

    private static final SegmentIdWithShardSpec ID_0 = new SegmentIdWithShardSpec(SegmentIdWithShardSpecTest.DATA_SOURCE, SegmentIdWithShardSpecTest.INTERVAL, SegmentIdWithShardSpecTest.VERSION, SegmentIdWithShardSpecTest.SHARD_SPEC_0);

    private static final SegmentIdWithShardSpec ID_1 = new SegmentIdWithShardSpec(SegmentIdWithShardSpecTest.DATA_SOURCE, SegmentIdWithShardSpecTest.INTERVAL, SegmentIdWithShardSpecTest.VERSION, SegmentIdWithShardSpecTest.SHARD_SPEC_1);

    @Test
    public void testSerde() throws Exception {
        final ObjectMapper objectMapper = new DefaultObjectMapper();
        objectMapper.registerSubtypes(NumberedShardSpec.class);
        final SegmentIdWithShardSpec id2 = objectMapper.readValue(objectMapper.writeValueAsBytes(SegmentIdWithShardSpecTest.ID_1), SegmentIdWithShardSpec.class);
        Assert.assertEquals(SegmentIdWithShardSpecTest.ID_1, id2);
        Assert.assertEquals(SegmentIdWithShardSpecTest.DATA_SOURCE, id2.getDataSource());
        Assert.assertEquals(SegmentIdWithShardSpecTest.INTERVAL, id2.getInterval());
        Assert.assertEquals(SegmentIdWithShardSpecTest.VERSION, id2.getVersion());
        Assert.assertEquals(SegmentIdWithShardSpecTest.SHARD_SPEC_1.getPartitionNum(), id2.getShardSpec().getPartitionNum());
        Assert.assertEquals(SegmentIdWithShardSpecTest.SHARD_SPEC_1.getPartitions(), getPartitions());
    }

    @Test
    public void testAsString() {
        Assert.assertEquals("foo_2000-01-01T00:00:00.000Z_2000-01-01T01:00:00.000Z_v1", SegmentIdWithShardSpecTest.ID_0.toString());
        Assert.assertEquals("foo_2000-01-01T00:00:00.000Z_2000-01-01T01:00:00.000Z_v1_1", SegmentIdWithShardSpecTest.ID_1.toString());
    }
}

