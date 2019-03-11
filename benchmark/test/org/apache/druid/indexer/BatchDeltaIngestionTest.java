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
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.druid.indexer.hadoop.WindowedDataSegment;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.DateTimes;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.query.aggregation.hyperloglog.HyperUniquesAggregatorFactory;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.timeline.DataSegment;
import org.apache.druid.timeline.partition.HashBasedNumberedShardSpec;
import org.joda.time.Interval;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import static HadoopDruidIndexerConfig.INDEX_IO;


public class BatchDeltaIngestionTest {
    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private static final ObjectMapper MAPPER;

    private static final IndexIO INDEX_IO;

    private static final Interval INTERVAL_FULL = Intervals.of("2014-10-22T00:00:00Z/P1D");

    private static final Interval INTERVAL_PARTIAL = Intervals.of("2014-10-22T00:00:00Z/PT2H");

    private static final DataSegment SEGMENT;

    static {
        MAPPER = new DefaultObjectMapper();
        BatchDeltaIngestionTest.MAPPER.registerSubtypes(new NamedType(HashBasedNumberedShardSpec.class, "hashed"));
        InjectableValues inject = new InjectableValues.Std().addValue(ObjectMapper.class, BatchDeltaIngestionTest.MAPPER).addValue(PruneLoadSpecHolder.class, DEFAULT);
        BatchDeltaIngestionTest.MAPPER.setInjectableValues(inject);
        INDEX_IO = INDEX_IO;
        try {
            SEGMENT = BatchDeltaIngestionTest.MAPPER.readValue(BatchDeltaIngestionTest.class.getClassLoader().getResource("test-segment/descriptor.json"), DataSegment.class).withLoadSpec(ImmutableMap.of("type", "local", "path", BatchDeltaIngestionTest.class.getClassLoader().getResource("test-segment/index.zip").getPath()));
        } catch (IOException e) {
            throw Throwables.propagate(e);
        }
    }

    @Test
    public void testReindexing() throws Exception {
        List<WindowedDataSegment> segments = ImmutableList.of(new WindowedDataSegment(BatchDeltaIngestionTest.SEGMENT, BatchDeltaIngestionTest.INTERVAL_FULL));
        HadoopDruidIndexerConfig config = makeHadoopDruidIndexerConfig(ImmutableMap.of("type", "dataSource", "ingestionSpec", ImmutableMap.of("dataSource", "testds", "interval", BatchDeltaIngestionTest.INTERVAL_FULL), "segments", segments), temporaryFolder.newFolder());
        List<ImmutableMap<String, Object>> expectedRows = ImmutableList.of(ImmutableMap.of("time", DateTimes.of("2014-10-22T00:00:00.000Z"), "host", ImmutableList.of("a.example.com"), "visited_sum", 100L, "unique_hosts", 1.0), ImmutableMap.of("time", DateTimes.of("2014-10-22T01:00:00.000Z"), "host", ImmutableList.of("b.example.com"), "visited_sum", 150L, "unique_hosts", 1.0), ImmutableMap.of("time", DateTimes.of("2014-10-22T02:00:00.000Z"), "host", ImmutableList.of("c.example.com"), "visited_sum", 200L, "unique_hosts", 1.0));
        testIngestion(config, expectedRows, Iterables.getOnlyElement(segments), ImmutableList.of("host"), ImmutableList.of("visited_sum", "unique_hosts"));
    }

    /**
     * By default re-indexing expects same aggregators as used by original indexing job. But, with additional flag
     * "useNewAggs" in DatasourcePathSpec, user can optionally have any set of aggregators.
     * See https://github.com/apache/incubator-druid/issues/5277 .
     */
    @Test
    public void testReindexingWithNewAggregators() throws Exception {
        List<WindowedDataSegment> segments = ImmutableList.of(new WindowedDataSegment(BatchDeltaIngestionTest.SEGMENT, BatchDeltaIngestionTest.INTERVAL_FULL));
        AggregatorFactory[] aggregators = new AggregatorFactory[]{ new LongSumAggregatorFactory("visited_sum2", "visited_sum"), new HyperUniquesAggregatorFactory("unique_hosts2", "unique_hosts") };
        Map<String, Object> inputSpec = ImmutableMap.of("type", "dataSource", "ingestionSpec", ImmutableMap.of("dataSource", "testds", "interval", BatchDeltaIngestionTest.INTERVAL_FULL), "segments", segments, "useNewAggs", true);
        File tmpDir = temporaryFolder.newFolder();
        HadoopDruidIndexerConfig config = makeHadoopDruidIndexerConfig(inputSpec, tmpDir, aggregators);
        List<ImmutableMap<String, Object>> expectedRows = ImmutableList.of(ImmutableMap.of("time", DateTimes.of("2014-10-22T00:00:00.000Z"), "host", ImmutableList.of("a.example.com"), "visited_sum2", 100L, "unique_hosts2", 1.0), ImmutableMap.of("time", DateTimes.of("2014-10-22T01:00:00.000Z"), "host", ImmutableList.of("b.example.com"), "visited_sum2", 150L, "unique_hosts2", 1.0), ImmutableMap.of("time", DateTimes.of("2014-10-22T02:00:00.000Z"), "host", ImmutableList.of("c.example.com"), "visited_sum2", 200L, "unique_hosts2", 1.0));
        testIngestion(config, expectedRows, Iterables.getOnlyElement(segments), ImmutableList.of("host"), ImmutableList.of("visited_sum2", "unique_hosts2"));
    }

    @Test
    public void testReindexingWithPartialWindow() throws Exception {
        List<WindowedDataSegment> segments = ImmutableList.of(new WindowedDataSegment(BatchDeltaIngestionTest.SEGMENT, BatchDeltaIngestionTest.INTERVAL_PARTIAL));
        HadoopDruidIndexerConfig config = makeHadoopDruidIndexerConfig(ImmutableMap.of("type", "dataSource", "ingestionSpec", ImmutableMap.of("dataSource", "testds", "interval", BatchDeltaIngestionTest.INTERVAL_FULL), "segments", segments), temporaryFolder.newFolder());
        List<ImmutableMap<String, Object>> expectedRows = ImmutableList.of(ImmutableMap.of("time", DateTimes.of("2014-10-22T00:00:00.000Z"), "host", ImmutableList.of("a.example.com"), "visited_sum", 100L, "unique_hosts", 1.0), ImmutableMap.of("time", DateTimes.of("2014-10-22T01:00:00.000Z"), "host", ImmutableList.of("b.example.com"), "visited_sum", 150L, "unique_hosts", 1.0));
        testIngestion(config, expectedRows, Iterables.getOnlyElement(segments), ImmutableList.of("host"), ImmutableList.of("visited_sum", "unique_hosts"));
    }

    @Test
    public void testDeltaIngestion() throws Exception {
        File tmpDir = temporaryFolder.newFolder();
        File dataFile1 = new File(tmpDir, "data1");
        FileUtils.writeLines(dataFile1, ImmutableList.of("2014102200,a.example.com,a.example.com,90", "2014102201,b.example.com,b.example.com,25"));
        File dataFile2 = new File(tmpDir, "data2");
        FileUtils.writeLines(dataFile2, ImmutableList.of("2014102202,c.example.com,c.example.com,70"));
        // using a hadoop glob path to test that it continues to work with hadoop MultipleInputs usage and not
        // affected by
        // https://issues.apache.org/jira/browse/MAPREDUCE-5061
        String inputPath = (tmpDir.getPath()) + "/{data1,data2}";
        List<WindowedDataSegment> segments = ImmutableList.of(new WindowedDataSegment(BatchDeltaIngestionTest.SEGMENT, BatchDeltaIngestionTest.INTERVAL_FULL));
        HadoopDruidIndexerConfig config = makeHadoopDruidIndexerConfig(ImmutableMap.of("type", "multi", "children", ImmutableList.of(ImmutableMap.of("type", "dataSource", "ingestionSpec", ImmutableMap.of("dataSource", "testds", "interval", BatchDeltaIngestionTest.INTERVAL_FULL), "segments", segments), ImmutableMap.<String, Object>of("type", "static", "paths", inputPath))), temporaryFolder.newFolder());
        List<ImmutableMap<String, Object>> expectedRows = ImmutableList.of(ImmutableMap.of("time", DateTimes.of("2014-10-22T00:00:00.000Z"), "host", ImmutableList.of("a.example.com"), "visited_sum", 190L, "unique_hosts", 1.0), ImmutableMap.of("time", DateTimes.of("2014-10-22T01:00:00.000Z"), "host", ImmutableList.of("b.example.com"), "visited_sum", 175L, "unique_hosts", 1.0), ImmutableMap.of("time", DateTimes.of("2014-10-22T02:00:00.000Z"), "host", ImmutableList.of("c.example.com"), "visited_sum", 270L, "unique_hosts", 1.0));
        testIngestion(config, expectedRows, Iterables.getOnlyElement(segments), ImmutableList.of("host"), ImmutableList.of("visited_sum", "unique_hosts"));
    }
}

