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


import HadoopDruidIndexerConfig.JSON_MAPPER;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.StringInputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.indexer.partitions.HashedPartitionsSpec;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.java.util.common.granularity.Granularity;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.DoubleSumAggregatorFactory;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static HadoopDruidIndexerConfig.JSON_MAPPER;


@RunWith(Parameterized.class)
public class DetermineHashedPartitionsJobTest {
    private HadoopDruidIndexerConfig indexerConfig;

    private int expectedNumTimeBuckets;

    private int[] expectedNumOfShards;

    private int errorMargin;

    public DetermineHashedPartitionsJobTest(String dataFilePath, long targetPartitionSize, String interval, int errorMargin, int expectedNumTimeBuckets, int[] expectedNumOfShards, Granularity segmentGranularity) {
        this.expectedNumOfShards = expectedNumOfShards;
        this.expectedNumTimeBuckets = expectedNumTimeBuckets;
        this.errorMargin = errorMargin;
        File tmpDir = Files.createTempDir();
        ImmutableList<Interval> intervals = null;
        if (interval != null) {
            intervals = ImmutableList.of(Intervals.of(interval));
        }
        HadoopIngestionSpec ingestionSpec = new HadoopIngestionSpec(new org.apache.druid.segment.indexing.DataSchema("test_schema", JSON_MAPPER.convertValue(new StringInputRowParser(new org.apache.druid.data.input.impl.DelimitedParseSpec(new TimestampSpec("ts", null, null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(ImmutableList.of("market", "quality", "placement", "placementish")), null, null), "\t", null, Arrays.asList("ts", "market", "quality", "placement", "placementish", "index"), false, 0), null), Map.class), new AggregatorFactory[]{ new DoubleSumAggregatorFactory("index", "index") }, new org.apache.druid.segment.indexing.granularity.UniformGranularitySpec(segmentGranularity, Granularities.NONE, intervals), null, JSON_MAPPER), new HadoopIOConfig(ImmutableMap.of("paths", dataFilePath, "type", "static"), null, tmpDir.getAbsolutePath()), new HadoopTuningConfig(tmpDir.getAbsolutePath(), null, new HashedPartitionsSpec(targetPartitionSize, null, true, null, null), null, null, null, null, false, false, false, false, null, false, false, null, null, null, false, false, null, null, null));
        this.indexerConfig = new HadoopDruidIndexerConfig(ingestionSpec);
    }

    @Test
    public void testDetermineHashedPartitions() {
        DetermineHashedPartitionsJob determineHashedPartitionsJob = new DetermineHashedPartitionsJob(indexerConfig);
        determineHashedPartitionsJob.run();
        Map<Long, List<HadoopyShardSpec>> shardSpecs = indexerConfig.getSchema().getTuningConfig().getShardSpecs();
        Assert.assertEquals(expectedNumTimeBuckets, shardSpecs.entrySet().size());
        int i = 0;
        for (Map.Entry<Long, List<HadoopyShardSpec>> entry : shardSpecs.entrySet()) {
            Assert.assertEquals(expectedNumOfShards[(i++)], entry.getValue().size(), errorMargin);
        }
    }
}

