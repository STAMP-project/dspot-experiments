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
package org.apache.druid.query;


import SegmentMetadataQuery.AnalysisType.CARDINALITY;
import SegmentMetadataQuery.AnalysisType.INTERVAL;
import SegmentMetadataQuery.AnalysisType.MINMAX;
import SegmentMetadataQuery.AnalysisType.SIZE;
import com.google.common.collect.ImmutableList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.InputRowParser;
import org.apache.druid.data.input.impl.MapInputRowParser;
import org.apache.druid.data.input.impl.TimestampSpec;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.query.metadata.SegmentMetadataQueryConfig;
import org.apache.druid.query.metadata.SegmentMetadataQueryRunnerFactory;
import org.apache.druid.query.metadata.metadata.ListColumnIncluderator;
import org.apache.druid.query.metadata.metadata.SegmentAnalysis;
import org.apache.druid.query.metadata.metadata.SegmentMetadataQuery;
import org.apache.druid.query.scan.ScanQuery;
import org.apache.druid.query.scan.ScanQueryConfig;
import org.apache.druid.query.scan.ScanQueryEngine;
import org.apache.druid.query.scan.ScanQueryQueryToolChest;
import org.apache.druid.query.scan.ScanQueryRunnerFactory;
import org.apache.druid.query.scan.ScanQueryRunnerTest;
import org.apache.druid.query.scan.ScanResultValue;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.IndexMergerV9;
import org.apache.druid.segment.QueryableIndex;
import org.apache.druid.segment.TestHelper;
import org.apache.druid.segment.writeout.OffHeapMemorySegmentWriteOutMediumFactory;
import org.apache.druid.timeline.SegmentId;
import org.joda.time.Interval;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class DoubleStorageTest {
    private static final SegmentMetadataQueryRunnerFactory METADATA_QR_FACTORY = new SegmentMetadataQueryRunnerFactory(new org.apache.druid.query.metadata.SegmentMetadataQueryQueryToolChest(new SegmentMetadataQueryConfig()), QueryRunnerTestHelper.NOOP_QUERYWATCHER);

    private static final ScanQueryQueryToolChest scanQueryQueryToolChest = new ScanQueryQueryToolChest(new ScanQueryConfig(), DefaultGenericQueryMetricsFactory.instance());

    private static final ScanQueryRunnerFactory SCAN_QUERY_RUNNER_FACTORY = new ScanQueryRunnerFactory(DoubleStorageTest.scanQueryQueryToolChest, new ScanQueryEngine());

    private static final IndexMergerV9 INDEX_MERGER_V9 = TestHelper.getTestIndexMergerV9(OffHeapMemorySegmentWriteOutMediumFactory.instance());

    private static final IndexIO INDEX_IO = TestHelper.getTestIndexIO();

    private static final Integer MAX_ROWS = 10;

    private static final String TIME_COLUMN = "__time";

    private static final String DIM_NAME = "testDimName";

    private static final String DIM_VALUE = "testDimValue";

    private static final String DIM_FLOAT_NAME = "testDimFloatName";

    private static final SegmentId SEGMENT_ID = SegmentId.dummy("segmentId");

    private static final Interval INTERVAL = Intervals.of("2011-01-13T00:00:00.000Z/2011-01-22T00:00:00.001Z");

    private static final InputRowParser<Map<String, Object>> ROW_PARSER = new MapInputRowParser(new org.apache.druid.data.input.impl.JSONParseSpec(new TimestampSpec(DoubleStorageTest.TIME_COLUMN, "auto", null), new DimensionsSpec(DimensionsSpec.getDefaultSchemas(ImmutableList.of(DoubleStorageTest.DIM_NAME)), ImmutableList.of(DoubleStorageTest.DIM_FLOAT_NAME), ImmutableList.of()), null, null));

    private QueryableIndex index;

    private final SegmentAnalysis expectedSegmentAnalysis;

    private final String storeDoubleAs;

    public DoubleStorageTest(String storeDoubleAs, SegmentAnalysis expectedSegmentAnalysis) {
        this.storeDoubleAs = storeDoubleAs;
        this.expectedSegmentAnalysis = expectedSegmentAnalysis;
    }

    @Test
    public void testMetaDataAnalysis() {
        QueryRunner runner = QueryRunnerTestHelper.makeQueryRunner(DoubleStorageTest.METADATA_QR_FACTORY, DoubleStorageTest.SEGMENT_ID, new org.apache.druid.segment.QueryableIndexSegment(index, DoubleStorageTest.SEGMENT_ID), null);
        SegmentMetadataQuery segmentMetadataQuery = Druids.newSegmentMetadataQueryBuilder().dataSource("testing").intervals(ImmutableList.of(DoubleStorageTest.INTERVAL)).toInclude(new ListColumnIncluderator(Arrays.asList(DoubleStorageTest.TIME_COLUMN, DoubleStorageTest.DIM_NAME, DoubleStorageTest.DIM_FLOAT_NAME))).analysisTypes(CARDINALITY, SIZE, SegmentMetadataQuery.AnalysisType.INTERVAL, MINMAX).merge(true).build();
        List<SegmentAnalysis> results = runner.run(QueryPlus.wrap(segmentMetadataQuery), new HashMap()).toList();
        Assert.assertEquals(Collections.singletonList(expectedSegmentAnalysis), results);
    }

    @Test
    public void testSelectValues() {
        QueryRunner runner = QueryRunnerTestHelper.makeQueryRunner(DoubleStorageTest.SCAN_QUERY_RUNNER_FACTORY, DoubleStorageTest.SEGMENT_ID, new org.apache.druid.segment.QueryableIndexSegment(index, DoubleStorageTest.SEGMENT_ID), null);
        ScanQuery query = newTestQuery().intervals(new org.apache.druid.query.spec.LegacySegmentSpec(DoubleStorageTest.INTERVAL)).virtualColumns().build();
        HashMap<String, Object> context = new HashMap<String, Object>();
        Iterable<ScanResultValue> results = runner.run(QueryPlus.wrap(query), context).toList();
        ScanResultValue expectedScanResult = new ScanResultValue(DoubleStorageTest.SEGMENT_ID.toString(), ImmutableList.of(DoubleStorageTest.TIME_COLUMN, DoubleStorageTest.DIM_NAME, DoubleStorageTest.DIM_FLOAT_NAME), DoubleStorageTest.getStreamOfEvents().collect(Collectors.toList()));
        List<ScanResultValue> expectedResults = Collections.singletonList(expectedScanResult);
        ScanQueryRunnerTest.verify(expectedResults, results);
    }
}

