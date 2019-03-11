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
package org.apache.druid.indexer;


import DataSegment.PruneLoadSpecHolder;
import DataSegment.PruneLoadSpecHolder.DEFAULT;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.IOException;
import org.apache.druid.indexer.hadoop.WindowedDataSegment;
import org.apache.druid.indexer.path.DatasourcePathSpec;
import org.apache.druid.indexer.path.PathSpec;
import org.apache.druid.indexer.path.StaticPathSpec;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.NoneShardSpec;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 */
public class HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest {
    private static final String testDatasource = "test";

    private static final String testDatasource2 = "test2";

    private static final Interval testDatasourceInterval = Intervals.of("1970/3000");

    private static final Interval testDatasourceInterval2 = Intervals.of("2000/2001");

    private static final Interval testDatasourceIntervalPartial = Intervals.of("2050/3000");

    private final ObjectMapper jsonMapper;

    public HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest() {
        jsonMapper = new DefaultObjectMapper();
        jsonMapper.setInjectableValues(new InjectableValues.Std().addValue(ObjectMapper.class, jsonMapper).addValue(PruneLoadSpecHolder.class, DEFAULT));
    }

    private static final DataSegment SEGMENT = new DataSegment(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasource, Intervals.of("2000/3000"), "ver", ImmutableMap.of("type", "local", "path", "/tmp/index1.zip"), ImmutableList.of("host"), ImmutableList.of("visited_sum", "unique_hosts"), NoneShardSpec.instance(), 9, 2);

    private static final DataSegment SEGMENT2 = new DataSegment(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasource2, Intervals.of("2000/3000"), "ver2", ImmutableMap.of("type", "local", "path", "/tmp/index2.zip"), ImmutableList.of("host2"), ImmutableList.of("visited_sum", "unique_hosts"), NoneShardSpec.instance(), 9, 2);

    @Test
    public void testUpdateSegmentListIfDatasourcePathSpecIsUsedWithNoDatasourcePathSpec() throws Exception {
        PathSpec pathSpec = new StaticPathSpec("/xyz", null);
        HadoopDruidIndexerConfig config = testRunUpdateSegmentListIfDatasourcePathSpecIsUsed(pathSpec, null);
        Assert.assertTrue(((config.getPathSpec()) instanceof StaticPathSpec));
    }

    @Test
    public void testUpdateSegmentListIfDatasourcePathSpecIsUsedWithJustDatasourcePathSpec() throws Exception {
        PathSpec pathSpec = new DatasourcePathSpec(jsonMapper, null, new org.apache.druid.indexer.hadoop.DatasourceIngestionSpec(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasource, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceInterval, null, null, null, null, null, false, null), null, false);
        HadoopDruidIndexerConfig config = testRunUpdateSegmentListIfDatasourcePathSpecIsUsed(pathSpec, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceInterval);
        Assert.assertEquals(ImmutableList.of(WindowedDataSegment.of(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.SEGMENT)), getSegments());
    }

    @Test
    public void testUpdateSegmentListIfDatasourcePathSpecWithMatchingUserSegments() throws Exception {
        PathSpec pathSpec = new DatasourcePathSpec(jsonMapper, null, new org.apache.druid.indexer.hadoop.DatasourceIngestionSpec(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasource, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceInterval, null, ImmutableList.of(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.SEGMENT), null, null, null, false, null), null, false);
        HadoopDruidIndexerConfig config = testRunUpdateSegmentListIfDatasourcePathSpecIsUsed(pathSpec, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceInterval);
        Assert.assertEquals(ImmutableList.of(WindowedDataSegment.of(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.SEGMENT)), getSegments());
    }

    @Test(expected = IOException.class)
    public void testUpdateSegmentListThrowsExceptionWithUserSegmentsMismatch() throws Exception {
        PathSpec pathSpec = new DatasourcePathSpec(jsonMapper, null, new org.apache.druid.indexer.hadoop.DatasourceIngestionSpec(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasource, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceInterval, null, ImmutableList.of(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.SEGMENT.withVersion("v2")), null, null, null, false, null), null, false);
        testRunUpdateSegmentListIfDatasourcePathSpecIsUsed(pathSpec, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceInterval);
    }

    @Test
    public void testUpdateSegmentListIfDatasourcePathSpecIsUsedWithJustDatasourcePathSpecAndPartialInterval() throws Exception {
        PathSpec pathSpec = new DatasourcePathSpec(jsonMapper, null, new org.apache.druid.indexer.hadoop.DatasourceIngestionSpec(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasource, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceIntervalPartial, null, null, null, null, null, false, null), null, false);
        HadoopDruidIndexerConfig config = testRunUpdateSegmentListIfDatasourcePathSpecIsUsed(pathSpec, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceIntervalPartial);
        Assert.assertEquals(ImmutableList.of(new WindowedDataSegment(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.SEGMENT, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceIntervalPartial)), getSegments());
    }

    @Test
    public void testUpdateSegmentListIfDatasourcePathSpecIsUsedWithMultiplePathSpec() throws Exception {
        PathSpec pathSpec = new org.apache.druid.indexer.path.MultiplePathSpec(ImmutableList.of(new StaticPathSpec("/xyz", null), new DatasourcePathSpec(jsonMapper, null, new org.apache.druid.indexer.hadoop.DatasourceIngestionSpec(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasource, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceInterval, null, null, null, null, null, false, null), null, false), new DatasourcePathSpec(jsonMapper, null, new org.apache.druid.indexer.hadoop.DatasourceIngestionSpec(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasource2, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceInterval2, null, null, null, null, null, false, null), null, false)));
        HadoopDruidIndexerConfig config = testRunUpdateSegmentListIfDatasourcePathSpecIsUsed(pathSpec, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceInterval);
        Assert.assertEquals(ImmutableList.of(WindowedDataSegment.of(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.SEGMENT)), getSegments());
        Assert.assertEquals(ImmutableList.of(new WindowedDataSegment(HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.SEGMENT2, HadoopIngestionSpecUpdateDatasourcePathSpecSegmentsTest.testDatasourceInterval2)), getSegments());
    }
}

