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


import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory;
import org.joda.time.Interval;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class IndexGeneratorJobTest {
    private static final AggregatorFactory[] aggs1 = new AggregatorFactory[]{ new LongSumAggregatorFactory("visited_num", "visited_num"), new HyperUniquesAggregatorFactory("unique_hosts", "host") };

    private static final AggregatorFactory[] aggs2 = new AggregatorFactory[]{ new CountAggregatorFactory("count") };

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private final boolean useCombiner;

    private final String partitionType;

    private final Interval interval;

    private final Object[][][] shardInfoForEachSegment;

    private final List<String> data;

    private final String inputFormatName;

    private final InputRowParser inputRowParser;

    private final Integer maxRowsInMemory;

    private final Long maxBytesInMemory;

    private final AggregatorFactory[] aggs;

    private final String datasourceName;

    private final boolean forceExtendableShardSpecs;

    private ObjectMapper mapper;

    private HadoopDruidIndexerConfig config;

    private File dataFile;

    private File tmpDir;

    public IndexGeneratorJobTest(boolean useCombiner, String partitionType, String interval, Object[][][] shardInfoForEachSegment, List<String> data, String inputFormatName, InputRowParser inputRowParser, Integer maxRowsInMemory, Long maxBytesInMemory, AggregatorFactory[] aggs, String datasourceName, boolean forceExtendableShardSpecs) {
        this.useCombiner = useCombiner;
        this.partitionType = partitionType;
        this.shardInfoForEachSegment = shardInfoForEachSegment;
        this.interval = Intervals.of(interval);
        this.data = data;
        this.inputFormatName = inputFormatName;
        this.inputRowParser = inputRowParser;
        this.maxRowsInMemory = maxRowsInMemory;
        this.maxBytesInMemory = maxBytesInMemory;
        this.aggs = aggs;
        this.datasourceName = datasourceName;
        this.forceExtendableShardSpecs = forceExtendableShardSpecs;
    }

    @Test
    public void testIndexGeneratorJob() throws IOException {
        verifyJob(new IndexGeneratorJob(config));
    }
}

