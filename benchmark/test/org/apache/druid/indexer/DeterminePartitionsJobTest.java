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
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.StringInputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.indexer.partitions.SingleDimensionPartitionsSpec;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.granularity.Granularities;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.timeline.partition.SingleDimensionShardSpec;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static HadoopDruidIndexerConfig.JSON_MAPPER;


@RunWith(Parameterized.class)
public class DeterminePartitionsJobTest {
    private HadoopDruidIndexerConfig config;

    private int expectedNumOfSegments;

    private int[] expectedNumOfShardsForEachSegment;

    private String[][][] expectedStartEndForEachShard;

    private File dataFile;

    private File tmpDir;

    public DeterminePartitionsJobTest(boolean assumeGrouped, Long targetPartitionSize, String interval, int expectedNumOfSegments, int[] expectedNumOfShardsForEachSegment, String[][][] expectedStartEndForEachShard, List<String> data) throws IOException {
        this.expectedNumOfSegments = expectedNumOfSegments;
        this.expectedNumOfShardsForEachSegment = expectedNumOfShardsForEachSegment;
        this.expectedStartEndForEachShard = expectedStartEndForEachShard;
        dataFile = File.createTempFile("test_website_data", "tmp");
        dataFile.deleteOnExit();
        tmpDir = Files.createTempDir();
        tmpDir.deleteOnExit();
        FileUtils.writeLines(dataFile, data);
        config = new HadoopDruidIndexerConfig(new HadoopIngestionSpec(new org.apache.druid.segment.indexing.DataSchema("website", JSON_MAPPER.convertValue(new StringInputRowParser(new org.apache.druid.data.input.impl.CSVParseSpec(new TimestampSpec("timestamp", "yyyyMMddHH", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(ImmutableList.of("host", "country")), null, null), null, ImmutableList.of("timestamp", "host", "country", "visited_num"), false, 0), null), Map.class), new AggregatorFactory[]{ new LongSumAggregatorFactory("visited_num", "visited_num") }, new org.apache.druid.segment.indexing.granularity.UniformGranularitySpec(Granularities.DAY, Granularities.NONE, ImmutableList.of(Intervals.of(interval))), null, JSON_MAPPER), new HadoopIOConfig(ImmutableMap.of("paths", dataFile.getCanonicalPath(), "type", "static"), null, tmpDir.getCanonicalPath()), new HadoopTuningConfig(tmpDir.getCanonicalPath(), null, new SingleDimensionPartitionsSpec(null, targetPartitionSize, null, assumeGrouped), null, null, null, null, false, false, false, false, null, false, false, null, null, null, false, false, null, null, null)));
    }

    @Test
    public void testPartitionJob() {
        DeterminePartitionsJob job = new DeterminePartitionsJob(config);
        job.run();
        int shardNum = 0;
        int segmentNum = 0;
        Assert.assertEquals(expectedNumOfSegments, config.getSchema().getTuningConfig().getShardSpecs().size());
        for (Map.Entry<Long, List<HadoopyShardSpec>> entry : config.getSchema().getTuningConfig().getShardSpecs().entrySet()) {
            int partitionNum = 0;
            List<HadoopyShardSpec> specs = entry.getValue();
            Assert.assertEquals(expectedNumOfShardsForEachSegment[segmentNum], specs.size());
            for (HadoopyShardSpec spec : specs) {
                SingleDimensionShardSpec actualSpec = ((SingleDimensionShardSpec) (spec.getActualSpec()));
                Assert.assertEquals(shardNum, spec.getShardNum());
                Assert.assertEquals(expectedStartEndForEachShard[segmentNum][partitionNum][0], actualSpec.getStart());
                Assert.assertEquals(expectedStartEndForEachShard[segmentNum][partitionNum][1], actualSpec.getEnd());
                Assert.assertEquals(partitionNum, actualSpec.getPartitionNum());
                shardNum++;
                partitionNum++;
            }
            segmentNum++;
        }
    }
}

