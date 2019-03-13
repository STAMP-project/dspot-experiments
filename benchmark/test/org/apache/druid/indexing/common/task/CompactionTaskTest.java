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
package org.apache.druid.indexing.common.task;


import ColumnHolder.TIME_COLUMN_NAME;
import Granularities.ALL;
import Granularities.MONTH;
import Granularities.YEAR;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import org.apache.druid.client.coordinator.CoordinatorClient;
import org.apache.druid.data.input.impl.DimensionSchema;
import org.apache.druid.data.input.impl.DimensionsSpec;
import org.apache.druid.data.input.impl.DoubleDimensionSchema;
import org.apache.druid.data.input.impl.FloatDimensionSchema;
import org.apache.druid.data.input.impl.LongDimensionSchema;
import org.apache.druid.data.input.impl.StringDimensionSchema;
import org.apache.druid.indexing.common.RetryPolicyConfig;
import org.apache.druid.indexing.common.RetryPolicyFactory;
import org.apache.druid.indexing.common.SegmentLoaderFactory;
import org.apache.druid.indexing.common.TaskToolbox;
import org.apache.druid.indexing.common.TestUtils;
import org.apache.druid.indexing.common.actions.SegmentListUsedAction;
import org.apache.druid.indexing.common.actions.TaskAction;
import org.apache.druid.indexing.common.actions.TaskActionClient;
import org.apache.druid.indexing.common.stats.RowIngestionMetersFactory;
import org.apache.druid.indexing.common.task.CompactionTask.Builder;
import org.apache.druid.indexing.common.task.CompactionTask.PartitionConfigurationManager;
import org.apache.druid.indexing.common.task.IndexTask.IndexIngestionSpec;
import org.apache.druid.indexing.common.task.IndexTask.IndexTuningConfig;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.java.util.common.IAE;
import org.apache.druid.java.util.common.Intervals;
import org.apache.druid.java.util.common.Pair;
import org.apache.druid.java.util.common.guava.Comparators;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.CountAggregatorFactory;
import org.apache.druid.query.aggregation.DoubleMaxAggregatorFactory;
import org.apache.druid.query.aggregation.FloatMinAggregatorFactory;
import org.apache.druid.query.aggregation.LongSumAggregatorFactory;
import org.apache.druid.segment.IndexIO;
import org.apache.druid.segment.Metadata;
import org.apache.druid.segment.QueryableIndex;
import org.apache.druid.segment.SimpleQueryableIndex;
import org.apache.druid.segment.column.BaseColumn;
import org.apache.druid.segment.column.BitmapIndex;
import org.apache.druid.segment.column.ColumnCapabilities;
import org.apache.druid.segment.column.ColumnCapabilitiesImpl;
import org.apache.druid.segment.column.ColumnHolder;
import org.apache.druid.segment.column.SpatialIndex;
import org.apache.druid.segment.column.ValueType;
import org.apache.druid.segment.data.CompressionFactory.LongEncodingStrategy;
import org.apache.druid.segment.data.CompressionStrategy;
import org.apache.druid.segment.data.RoaringBitmapSerdeFactory;
import org.apache.druid.segment.loading.SegmentLoadingException;
import org.apache.druid.segment.selector.settable.SettableColumnValueSelector;
import org.apache.druid.segment.writeout.OffHeapMemorySegmentWriteOutMediumFactory;
import org.apache.druid.server.security.AuthTestUtils;
import org.apache.druid.timeline.DataSegment;
import org.hamcrest.CoreMatchers;
import org.joda.time.Interval;
import org.joda.time.Period;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class CompactionTaskTest {
    private static final long SEGMENT_SIZE_BYTES = 100;

    private static final int NUM_ROWS_PER_SEGMENT = 10;

    private static final String DATA_SOURCE = "dataSource";

    private static final String TIMESTAMP_COLUMN = "timestamp";

    private static final String MIXED_TYPE_COLUMN = "string_to_double";

    private static final Interval COMPACTION_INTERVAL = Intervals.of("2017-01-01/2017-07-01");

    private static final List<Interval> SEGMENT_INTERVALS = ImmutableList.of(Intervals.of("2017-01-01/2017-02-01"), Intervals.of("2017-02-01/2017-03-01"), Intervals.of("2017-03-01/2017-04-01"), Intervals.of("2017-04-01/2017-05-01"), Intervals.of("2017-05-01/2017-06-01"), Intervals.of("2017-06-01/2017-07-01"));

    private static final Map<Interval, DimensionSchema> MIXED_TYPE_COLUMN_MAP = new HashMap<>();

    private static final IndexTuningConfig TUNING_CONFIG = CompactionTaskTest.createTuningConfig();

    private static Map<String, DimensionSchema> DIMENSIONS;

    private static List<AggregatorFactory> AGGREGATORS;

    private static List<DataSegment> SEGMENTS;

    private static RowIngestionMetersFactory rowIngestionMetersFactory = new TestUtils().getRowIngestionMetersFactory();

    private static Map<DataSegment, File> segmentMap = new HashMap<>();

    private static CoordinatorClient coordinatorClient = new CompactionTaskTest.TestCoordinatorClient(CompactionTaskTest.segmentMap);

    private static ObjectMapper objectMapper = CompactionTaskTest.setupInjectablesInObjectMapper(new DefaultObjectMapper());

    private static RetryPolicyFactory retryPolicyFactory = new RetryPolicyFactory(new RetryPolicyConfig());

    private final boolean keepSegmentGranularity;

    private TaskToolbox toolbox;

    private SegmentLoaderFactory segmentLoaderFactory;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public CompactionTaskTest(boolean keepSegmentGranularity) {
        this.keepSegmentGranularity = keepSegmentGranularity;
    }

    @Test
    public void testSerdeWithInterval() throws IOException {
        final Builder builder = new Builder(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.objectMapper, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, CompactionTaskTest.rowIngestionMetersFactory, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final CompactionTask task = builder.interval(CompactionTaskTest.COMPACTION_INTERVAL).tuningConfig(CompactionTaskTest.createTuningConfig()).context(ImmutableMap.of("testKey", "testContext")).build();
        final byte[] bytes = CompactionTaskTest.objectMapper.writeValueAsBytes(task);
        final CompactionTask fromJson = CompactionTaskTest.objectMapper.readValue(bytes, CompactionTask.class);
        CompactionTaskTest.assertEquals(task, fromJson);
    }

    @Test
    public void testSerdeWithSegments() throws IOException {
        final Builder builder = new Builder(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.objectMapper, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, CompactionTaskTest.rowIngestionMetersFactory, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final CompactionTask task = builder.segments(CompactionTaskTest.SEGMENTS).tuningConfig(CompactionTaskTest.createTuningConfig()).context(ImmutableMap.of("testKey", "testContext")).build();
        final byte[] bytes = CompactionTaskTest.objectMapper.writeValueAsBytes(task);
        final CompactionTask fromJson = CompactionTaskTest.objectMapper.readValue(bytes, CompactionTask.class);
        CompactionTaskTest.assertEquals(task, fromJson);
    }

    @Test
    public void testSerdeWithDimensions() throws IOException {
        final Builder builder = new Builder(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.objectMapper, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, CompactionTaskTest.rowIngestionMetersFactory, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final CompactionTask task = builder.segments(CompactionTaskTest.SEGMENTS).dimensionsSpec(new DimensionsSpec(ImmutableList.of(new StringDimensionSchema("dim1"), new StringDimensionSchema("dim2"), new StringDimensionSchema("dim3")))).tuningConfig(CompactionTaskTest.createTuningConfig()).context(ImmutableMap.of("testKey", "testVal")).build();
        final byte[] bytes = CompactionTaskTest.objectMapper.writeValueAsBytes(task);
        final CompactionTask fromJson = CompactionTaskTest.objectMapper.readValue(bytes, CompactionTask.class);
        CompactionTaskTest.assertEquals(task, fromJson);
    }

    @Test
    public void testCreateIngestionSchema() throws IOException, SegmentLoadingException {
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.COMPACTION_INTERVAL), new PartitionConfigurationManager(null, CompactionTaskTest.TUNING_CONFIG), null, null, keepSegmentGranularity, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final List<DimensionsSpec> expectedDimensionsSpec = CompactionTaskTest.getExpectedDimensionsSpecForAutoGeneration(keepSegmentGranularity);
        if (keepSegmentGranularity) {
            ingestionSpecs.sort(( s1, s2) -> Comparators.intervalsByStartThenEnd().compare(s1.getDataSchema().getGranularitySpec().inputIntervals().get(0), s2.getDataSchema().getGranularitySpec().inputIntervals().get(0)));
            Assert.assertEquals(6, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, CompactionTaskTest.SEGMENT_INTERVALS, MONTH);
        } else {
            Assert.assertEquals(1, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, Collections.singletonList(CompactionTaskTest.COMPACTION_INTERVAL), ALL);
        }
    }

    @Test
    public void testCreateIngestionSchemaWithTargetPartitionSize() throws IOException, SegmentLoadingException {
        final IndexTuningConfig tuningConfig = new IndexTuningConfig(null, 6, 500000, 1000000L, null, null, null, null, new org.apache.druid.segment.IndexSpec(new RoaringBitmapSerdeFactory(true), CompressionStrategy.LZ4, CompressionStrategy.LZF, LongEncodingStrategy.LONGS), 5000, true, false, true, false, null, 100L, null, null, null, null);
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.COMPACTION_INTERVAL), new PartitionConfigurationManager(null, tuningConfig), null, null, keepSegmentGranularity, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final List<DimensionsSpec> expectedDimensionsSpec = CompactionTaskTest.getExpectedDimensionsSpecForAutoGeneration(keepSegmentGranularity);
        if (keepSegmentGranularity) {
            ingestionSpecs.sort(( s1, s2) -> Comparators.intervalsByStartThenEnd().compare(s1.getDataSchema().getGranularitySpec().inputIntervals().get(0), s2.getDataSchema().getGranularitySpec().inputIntervals().get(0)));
            Assert.assertEquals(6, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, CompactionTaskTest.SEGMENT_INTERVALS, tuningConfig, MONTH);
        } else {
            Assert.assertEquals(1, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, Collections.singletonList(CompactionTaskTest.COMPACTION_INTERVAL), tuningConfig, ALL);
        }
    }

    @Test
    public void testCreateIngestionSchemaWithMaxTotalRows() throws IOException, SegmentLoadingException {
        final IndexTuningConfig tuningConfig = new IndexTuningConfig(null, null, 500000, 1000000L, 6L, null, null, null, new org.apache.druid.segment.IndexSpec(new RoaringBitmapSerdeFactory(true), CompressionStrategy.LZ4, CompressionStrategy.LZF, LongEncodingStrategy.LONGS), 5000, true, false, true, false, null, 100L, null, null, null, null);
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.COMPACTION_INTERVAL), new PartitionConfigurationManager(null, tuningConfig), null, null, keepSegmentGranularity, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final List<DimensionsSpec> expectedDimensionsSpec = CompactionTaskTest.getExpectedDimensionsSpecForAutoGeneration(keepSegmentGranularity);
        if (keepSegmentGranularity) {
            ingestionSpecs.sort(( s1, s2) -> Comparators.intervalsByStartThenEnd().compare(s1.getDataSchema().getGranularitySpec().inputIntervals().get(0), s2.getDataSchema().getGranularitySpec().inputIntervals().get(0)));
            Assert.assertEquals(6, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, CompactionTaskTest.SEGMENT_INTERVALS, tuningConfig, MONTH);
        } else {
            Assert.assertEquals(1, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, Collections.singletonList(CompactionTaskTest.COMPACTION_INTERVAL), tuningConfig, ALL);
        }
    }

    @Test
    public void testCreateIngestionSchemaWithNumShards() throws IOException, SegmentLoadingException {
        final IndexTuningConfig tuningConfig = new IndexTuningConfig(null, null, 500000, 1000000L, null, null, 3, null, new org.apache.druid.segment.IndexSpec(new RoaringBitmapSerdeFactory(true), CompressionStrategy.LZ4, CompressionStrategy.LZF, LongEncodingStrategy.LONGS), 5000, true, false, true, false, null, 100L, null, null, null, null);
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.COMPACTION_INTERVAL), new PartitionConfigurationManager(null, tuningConfig), null, null, keepSegmentGranularity, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final List<DimensionsSpec> expectedDimensionsSpec = CompactionTaskTest.getExpectedDimensionsSpecForAutoGeneration(keepSegmentGranularity);
        if (keepSegmentGranularity) {
            ingestionSpecs.sort(( s1, s2) -> Comparators.intervalsByStartThenEnd().compare(s1.getDataSchema().getGranularitySpec().inputIntervals().get(0), s2.getDataSchema().getGranularitySpec().inputIntervals().get(0)));
            Assert.assertEquals(6, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, CompactionTaskTest.SEGMENT_INTERVALS, tuningConfig, MONTH);
        } else {
            Assert.assertEquals(1, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, Collections.singletonList(CompactionTaskTest.COMPACTION_INTERVAL), tuningConfig, ALL);
        }
    }

    @Test
    public void testCreateIngestionSchemaWithCustomDimensionsSpec() throws IOException, SegmentLoadingException {
        final DimensionsSpec customSpec = new DimensionsSpec(Lists.newArrayList(new LongDimensionSchema("timestamp"), new StringDimensionSchema("string_dim_0"), new StringDimensionSchema("string_dim_1"), new StringDimensionSchema("string_dim_2"), new StringDimensionSchema("string_dim_3"), new StringDimensionSchema("string_dim_4"), new LongDimensionSchema("long_dim_0"), new LongDimensionSchema("long_dim_1"), new LongDimensionSchema("long_dim_2"), new LongDimensionSchema("long_dim_3"), new LongDimensionSchema("long_dim_4"), new FloatDimensionSchema("float_dim_0"), new FloatDimensionSchema("float_dim_1"), new FloatDimensionSchema("float_dim_2"), new FloatDimensionSchema("float_dim_3"), new FloatDimensionSchema("float_dim_4"), new DoubleDimensionSchema("double_dim_0"), new DoubleDimensionSchema("double_dim_1"), new DoubleDimensionSchema("double_dim_2"), new DoubleDimensionSchema("double_dim_3"), new DoubleDimensionSchema("double_dim_4"), new StringDimensionSchema(CompactionTaskTest.MIXED_TYPE_COLUMN)));
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.COMPACTION_INTERVAL), new PartitionConfigurationManager(null, CompactionTaskTest.TUNING_CONFIG), customSpec, null, keepSegmentGranularity, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        if (keepSegmentGranularity) {
            ingestionSpecs.sort(( s1, s2) -> Comparators.intervalsByStartThenEnd().compare(s1.getDataSchema().getGranularitySpec().inputIntervals().get(0), s2.getDataSchema().getGranularitySpec().inputIntervals().get(0)));
            Assert.assertEquals(6, ingestionSpecs.size());
            final List<DimensionsSpec> dimensionsSpecs = new ArrayList<>(6);
            IntStream.range(0, 6).forEach(( i) -> dimensionsSpecs.add(customSpec));
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, dimensionsSpecs, CompactionTaskTest.AGGREGATORS, CompactionTaskTest.SEGMENT_INTERVALS, MONTH);
        } else {
            Assert.assertEquals(1, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, Collections.singletonList(customSpec), CompactionTaskTest.AGGREGATORS, Collections.singletonList(CompactionTaskTest.COMPACTION_INTERVAL), ALL);
        }
    }

    @Test
    public void testCreateIngestionSchemaWithCustomMetricsSpec() throws IOException, SegmentLoadingException {
        final AggregatorFactory[] customMetricsSpec = new AggregatorFactory[]{ new CountAggregatorFactory("custom_count"), new LongSumAggregatorFactory("custom_long_sum", "agg_1"), new FloatMinAggregatorFactory("custom_float_min", "agg_3"), new DoubleMaxAggregatorFactory("custom_double_max", "agg_4") };
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.COMPACTION_INTERVAL), new PartitionConfigurationManager(null, CompactionTaskTest.TUNING_CONFIG), null, customMetricsSpec, keepSegmentGranularity, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final List<DimensionsSpec> expectedDimensionsSpec = CompactionTaskTest.getExpectedDimensionsSpecForAutoGeneration(keepSegmentGranularity);
        if (keepSegmentGranularity) {
            ingestionSpecs.sort(( s1, s2) -> Comparators.intervalsByStartThenEnd().compare(s1.getDataSchema().getGranularitySpec().inputIntervals().get(0), s2.getDataSchema().getGranularitySpec().inputIntervals().get(0)));
            Assert.assertEquals(6, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, Arrays.asList(customMetricsSpec), CompactionTaskTest.SEGMENT_INTERVALS, MONTH);
        } else {
            Assert.assertEquals(1, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, Arrays.asList(customMetricsSpec), Collections.singletonList(CompactionTaskTest.COMPACTION_INTERVAL), ALL);
        }
    }

    @Test
    public void testCreateIngestionSchemaWithCustomSegments() throws IOException, SegmentLoadingException {
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.SEGMENTS), new PartitionConfigurationManager(null, CompactionTaskTest.TUNING_CONFIG), null, null, keepSegmentGranularity, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final List<DimensionsSpec> expectedDimensionsSpec = CompactionTaskTest.getExpectedDimensionsSpecForAutoGeneration(keepSegmentGranularity);
        if (keepSegmentGranularity) {
            ingestionSpecs.sort(( s1, s2) -> Comparators.intervalsByStartThenEnd().compare(s1.getDataSchema().getGranularitySpec().inputIntervals().get(0), s2.getDataSchema().getGranularitySpec().inputIntervals().get(0)));
            Assert.assertEquals(6, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, CompactionTaskTest.SEGMENT_INTERVALS, MONTH);
        } else {
            Assert.assertEquals(1, ingestionSpecs.size());
            CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, Collections.singletonList(CompactionTaskTest.COMPACTION_INTERVAL), ALL);
        }
    }

    @Test
    public void testCreateIngestionSchemaWithDifferentSegmentSet() throws IOException, SegmentLoadingException {
        expectedException.expect(CoreMatchers.instanceOf(IllegalStateException.class));
        expectedException.expectMessage(CoreMatchers.containsString("are different from the current used segments"));
        final List<DataSegment> segments = new ArrayList(CompactionTaskTest.SEGMENTS);
        Collections.sort(segments);
        // Remove one segment in the middle
        segments.remove(((segments.size()) / 2));
        CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(segments), new PartitionConfigurationManager(null, CompactionTaskTest.TUNING_CONFIG), null, null, keepSegmentGranularity, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
    }

    @Test
    public void testMissingMetadata() throws IOException, SegmentLoadingException {
        expectedException.expect(RuntimeException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("Index metadata doesn't exist for segment"));
        final CompactionTaskTest.TestIndexIO indexIO = ((CompactionTaskTest.TestIndexIO) (toolbox.getIndexIO()));
        indexIO.removeMetadata(Iterables.getFirst(indexIO.getQueryableIndexMap().keySet(), null));
        final List<DataSegment> segments = new ArrayList(CompactionTaskTest.SEGMENTS);
        CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(segments), new PartitionConfigurationManager(null, CompactionTaskTest.TUNING_CONFIG), null, null, keepSegmentGranularity, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
    }

    @Test
    public void testEmptyInterval() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(CoreMatchers.containsString("must specify a nonempty interval"));
        final Builder builder = new Builder(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.objectMapper, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, CompactionTaskTest.rowIngestionMetersFactory, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final CompactionTask task = builder.interval(Intervals.of("2000-01-01/2000-01-01")).build();
    }

    @Test
    public void testTargetPartitionSizeWithPartitionConfig() throws IOException, SegmentLoadingException {
        final IndexTuningConfig tuningConfig = new IndexTuningConfig(null, 6, 500000, 1000000L, null, null, null, null, new org.apache.druid.segment.IndexSpec(new RoaringBitmapSerdeFactory(true), CompressionStrategy.LZ4, CompressionStrategy.LZF, LongEncodingStrategy.LONGS), 5000, true, false, true, false, null, 100L, null, null, null, null);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("targetCompactionSizeBytes[6] cannot be used with");
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.COMPACTION_INTERVAL), new PartitionConfigurationManager(6L, tuningConfig), null, null, keepSegmentGranularity, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
    }

    @Test
    public void testSegmentGranularity() throws IOException, SegmentLoadingException {
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.COMPACTION_INTERVAL), new PartitionConfigurationManager(null, CompactionTaskTest.TUNING_CONFIG), null, null, null, new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.months(3), null, null), CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final List<DimensionsSpec> expectedDimensionsSpec = ImmutableList.of(new DimensionsSpec(CompactionTaskTest.getDimensionSchema(new DoubleDimensionSchema("string_to_double"))));
        ingestionSpecs.sort(( s1, s2) -> Comparators.intervalsByStartThenEnd().compare(s1.getDataSchema().getGranularitySpec().inputIntervals().get(0), s2.getDataSchema().getGranularitySpec().inputIntervals().get(0)));
        Assert.assertEquals(1, ingestionSpecs.size());
        CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, Collections.singletonList(CompactionTaskTest.COMPACTION_INTERVAL), new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.months(3), null, null));
    }

    @Test
    public void testSegmentGranularityWithFalseKeepSegmentGranularity() throws IOException, SegmentLoadingException {
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.COMPACTION_INTERVAL), new PartitionConfigurationManager(null, CompactionTaskTest.TUNING_CONFIG), null, null, false, new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.months(3), null, null), CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final List<DimensionsSpec> expectedDimensionsSpec = ImmutableList.of(new DimensionsSpec(CompactionTaskTest.getDimensionSchema(new DoubleDimensionSchema("string_to_double"))));
        ingestionSpecs.sort(( s1, s2) -> Comparators.intervalsByStartThenEnd().compare(s1.getDataSchema().getGranularitySpec().inputIntervals().get(0), s2.getDataSchema().getGranularitySpec().inputIntervals().get(0)));
        Assert.assertEquals(1, ingestionSpecs.size());
        CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, Collections.singletonList(CompactionTaskTest.COMPACTION_INTERVAL), new org.apache.druid.java.util.common.granularity.PeriodGranularity(Period.months(3), null, null));
    }

    @Test
    public void testNullSegmentGranularityAndNullKeepSegmentGranularity() throws IOException, SegmentLoadingException {
        final List<IndexIngestionSpec> ingestionSpecs = CompactionTask.createIngestionSchema(toolbox, new org.apache.druid.indexing.common.task.CompactionTask.SegmentProvider(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.COMPACTION_INTERVAL), new PartitionConfigurationManager(null, CompactionTaskTest.TUNING_CONFIG), null, null, null, null, CompactionTaskTest.objectMapper, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final List<DimensionsSpec> expectedDimensionsSpec = CompactionTaskTest.getExpectedDimensionsSpecForAutoGeneration(true);
        ingestionSpecs.sort(( s1, s2) -> Comparators.intervalsByStartThenEnd().compare(s1.getDataSchema().getGranularitySpec().inputIntervals().get(0), s2.getDataSchema().getGranularitySpec().inputIntervals().get(0)));
        Assert.assertEquals(6, ingestionSpecs.size());
        CompactionTaskTest.assertIngestionSchema(ingestionSpecs, expectedDimensionsSpec, CompactionTaskTest.AGGREGATORS, CompactionTaskTest.SEGMENT_INTERVALS, MONTH);
    }

    @Test
    public void testUseKeepSegmentGranularityAndSegmentGranularityTogether() {
        expectedException.expect(IAE.class);
        expectedException.expectMessage("keepSegmentGranularity and segmentGranularity can't be used together");
        final Builder builder = new Builder(CompactionTaskTest.DATA_SOURCE, CompactionTaskTest.objectMapper, AuthTestUtils.TEST_AUTHORIZER_MAPPER, null, CompactionTaskTest.rowIngestionMetersFactory, CompactionTaskTest.coordinatorClient, segmentLoaderFactory, CompactionTaskTest.retryPolicyFactory);
        final CompactionTask task = builder.interval(CompactionTaskTest.COMPACTION_INTERVAL).keepSegmentGranularity(true).segmentGranularity(YEAR).tuningConfig(CompactionTaskTest.createTuningConfig()).context(ImmutableMap.of("testKey", "testContext")).build();
    }

    @Test
    public void testHugeTargetCompactionSize() {
        final PartitionConfigurationManager manager = new PartitionConfigurationManager(Long.MAX_VALUE, CompactionTaskTest.TUNING_CONFIG);
        final CompactionTaskTest.TestIndexIO indexIO = ((CompactionTaskTest.TestIndexIO) (toolbox.getIndexIO()));
        final Map<File, QueryableIndex> queryableIndexMap = indexIO.getQueryableIndexMap();
        final List<Pair<QueryableIndex, DataSegment>> segments = new ArrayList<>();
        for (Map.Entry<DataSegment, File> entry : CompactionTaskTest.segmentMap.entrySet()) {
            final DataSegment segment = entry.getKey();
            final File file = entry.getValue();
            segments.add(Pair.of(Preconditions.checkNotNull(queryableIndexMap.get(file)), segment));
        }
        expectedException.expect(ArithmeticException.class);
        expectedException.expectMessage(CoreMatchers.startsWith("Estimated maxRowsPerSegment[922337203685477632] is out of integer value range."));
        manager.computeTuningConfig(segments);
    }

    private static class TestCoordinatorClient extends CoordinatorClient {
        private final Map<DataSegment, File> segmentMap;

        TestCoordinatorClient(Map<DataSegment, File> segmentMap) {
            super(null, null);
            this.segmentMap = segmentMap;
        }

        @Override
        public List<DataSegment> getDatabaseSegmentDataSourceSegments(String dataSource, List<Interval> intervals) {
            return new ArrayList(segmentMap.keySet());
        }
    }

    private static class TestTaskToolbox extends TaskToolbox {
        private final Map<DataSegment, File> segmentFileMap;

        TestTaskToolbox(TaskActionClient taskActionClient, IndexIO indexIO, Map<DataSegment, File> segmentFileMap) {
            super(null, taskActionClient, null, null, null, null, null, null, null, null, null, null, null, null, null, null, indexIO, null, null, null, new org.apache.druid.segment.IndexMergerV9(CompactionTaskTest.objectMapper, indexIO, OffHeapMemorySegmentWriteOutMediumFactory.instance()), null, null, null, null, new NoopTestTaskFileWriter());
            this.segmentFileMap = segmentFileMap;
        }

        @Override
        public Map<DataSegment, File> fetchSegments(List<DataSegment> segments) {
            final Map<DataSegment, File> submap = new HashMap(segments.size());
            for (DataSegment segment : segments) {
                final File file = Preconditions.checkNotNull(segmentFileMap.get(segment));
                submap.put(segment, file);
            }
            return submap;
        }
    }

    private static class TestTaskActionClient implements TaskActionClient {
        private final List<DataSegment> segments;

        TestTaskActionClient(List<DataSegment> segments) {
            this.segments = segments;
        }

        @Override
        public <RetType> RetType submit(TaskAction<RetType> taskAction) {
            if (!(taskAction instanceof SegmentListUsedAction)) {
                throw new org.apache.druid.java.util.common.ISE("action[%s] is not supported", taskAction);
            }
            return ((RetType) (segments));
        }
    }

    private static class TestIndexIO extends IndexIO {
        private final Map<File, QueryableIndex> queryableIndexMap;

        TestIndexIO(ObjectMapper mapper, Map<DataSegment, File> segmentFileMap) {
            super(mapper, () -> 0);
            queryableIndexMap = new HashMap(segmentFileMap.size());
            for (Map.Entry<DataSegment, File> entry : segmentFileMap.entrySet()) {
                final DataSegment segment = entry.getKey();
                final List<String> columnNames = new ArrayList(((segment.getDimensions().size()) + (segment.getMetrics().size())));
                columnNames.add(TIME_COLUMN_NAME);
                columnNames.addAll(segment.getDimensions());
                columnNames.addAll(segment.getMetrics());
                final Map<String, ColumnHolder> columnMap = new HashMap<>(columnNames.size());
                final List<AggregatorFactory> aggregatorFactories = new ArrayList(segment.getMetrics().size());
                for (String columnName : columnNames) {
                    if (CompactionTaskTest.MIXED_TYPE_COLUMN.equals(columnName)) {
                        columnMap.put(columnName, CompactionTaskTest.createColumn(CompactionTaskTest.MIXED_TYPE_COLUMN_MAP.get(segment.getInterval())));
                    } else
                        if (CompactionTaskTest.DIMENSIONS.containsKey(columnName)) {
                            columnMap.put(columnName, CompactionTaskTest.createColumn(CompactionTaskTest.DIMENSIONS.get(columnName)));
                        } else {
                            final Optional<AggregatorFactory> maybeMetric = CompactionTaskTest.AGGREGATORS.stream().filter(( agg) -> agg.getName().equals(columnName)).findAny();
                            if (maybeMetric.isPresent()) {
                                columnMap.put(columnName, CompactionTaskTest.createColumn(maybeMetric.get()));
                                aggregatorFactories.add(maybeMetric.get());
                            }
                        }

                }
                final Metadata metadata = new Metadata(null, aggregatorFactories.toArray(new AggregatorFactory[0]), null, null, null);
                queryableIndexMap.put(entry.getValue(), new SimpleQueryableIndex(segment.getInterval(), new org.apache.druid.segment.data.ListIndexed(segment.getDimensions()), null, columnMap, null, metadata));
            }
        }

        @Override
        public QueryableIndex loadIndex(File file) {
            return queryableIndexMap.get(file);
        }

        void removeMetadata(File file) {
            final SimpleQueryableIndex index = ((SimpleQueryableIndex) (queryableIndexMap.get(file)));
            if (index != null) {
                queryableIndexMap.put(file, new SimpleQueryableIndex(index.getDataInterval(), index.getColumnNames(), index.getAvailableDimensions(), index.getBitmapFactoryForDimensions(), index.getColumns(), index.getFileMapper(), null, index.getDimensionHandlers()));
            }
        }

        Map<File, QueryableIndex> getQueryableIndexMap() {
            return queryableIndexMap;
        }
    }

    private static class TestColumn implements ColumnHolder {
        private final ColumnCapabilities columnCapabilities;

        TestColumn(ValueType type) {
            columnCapabilities = // set a fake value to make string columns
            new ColumnCapabilitiesImpl().setType(type).setDictionaryEncoded((type == (ValueType.STRING))).setHasBitmapIndexes((type == (ValueType.STRING))).setHasSpatialIndexes(false).setHasMultipleValues(false);
        }

        @Override
        public ColumnCapabilities getCapabilities() {
            return columnCapabilities;
        }

        @Override
        public int getLength() {
            return CompactionTaskTest.NUM_ROWS_PER_SEGMENT;
        }

        @Override
        public BaseColumn getColumn() {
            return null;
        }

        @Override
        public SettableColumnValueSelector makeNewSettableColumnValueSelector() {
            return null;
        }

        @Override
        public BitmapIndex getBitmapIndex() {
            return null;
        }

        @Override
        public SpatialIndex getSpatialIndex() {
            return null;
        }
    }
}

