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
package org.apache.druid.query.aggregation.bloom;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import org.apache.druid.common.config.NullHandling;
import org.apache.druid.guice.BloomFilterExtensionModule;
import org.apache.druid.guice.BloomFilterSerializersModule;
import org.apache.druid.jackson.DefaultObjectMapper;
import org.apache.druid.query.aggregation.AggregatorFactory;
import org.apache.druid.query.aggregation.cardinality.CardinalityAggregatorTest;
import org.apache.druid.query.dimension.DefaultDimensionSpec;
import org.apache.druid.query.dimension.DimensionSpec;
import org.apache.druid.query.extraction.RegexDimExtractionFn;
import org.apache.druid.query.filter.BloomKFilter;
import org.apache.druid.query.monomorphicprocessing.RuntimeShapeInspector;
import org.apache.druid.segment.ColumnValueSelector;
import org.apache.druid.segment.DimensionSelector;
import org.apache.druid.segment.DoubleColumnSelector;
import org.apache.druid.segment.FloatColumnSelector;
import org.apache.druid.segment.LongColumnSelector;
import org.junit.Assert;
import org.junit.Test;


public class BloomFilterAggregatorTest {
    private static final String nullish = (NullHandling.replaceWithDefault()) ? "" : null;

    private static final List<String[]> values1 = BloomFilterAggregatorTest.dimensionValues("a", "b", "c", "a", "a", BloomFilterAggregatorTest.nullish, "b", "b", "b", "b", "a", "a");

    private static final List<String[]> values2 = BloomFilterAggregatorTest.dimensionValues("a", "b", "c", "x", "a", "e", "b", new String[]{ BloomFilterAggregatorTest.nullish, "x" }, new String[]{ "x", BloomFilterAggregatorTest.nullish }, new String[]{ "y", "x" }, new String[]{ "x", "y" }, new String[]{ "x", "y", "a" });

    private static final Double[] doubleValues1 = new Double[]{ 0.1, 1.5, 18.3, 0.1 };

    private static final Float[] floatValues1 = new Float[]{ 0.4F, 0.8F, 23.2F };

    private static final Long[] longValues1 = new Long[]{ 10241L, 12312355L, 0L, 81L };

    private static final int maxNumValues = 15;

    private static BloomKFilter filter1;

    private static BloomKFilter filter2;

    private static String serializedFilter1;

    private static String serializedFilter2;

    private static String serializedCombinedFilter;

    private static String serializedLongFilter;

    private static String serializedDoubleFilter;

    private static String serializedFloatFilter;

    static {
        try {
            BloomFilterAggregatorTest.filter1 = new BloomKFilter(BloomFilterAggregatorTest.maxNumValues);
            BloomFilterAggregatorTest.filter2 = new BloomKFilter(BloomFilterAggregatorTest.maxNumValues);
            BloomKFilter combinedValuesFilter = new BloomKFilter(BloomFilterAggregatorTest.maxNumValues);
            BloomFilterAggregatorTest.createStringFilter(BloomFilterAggregatorTest.values1, BloomFilterAggregatorTest.filter1, combinedValuesFilter);
            BloomFilterAggregatorTest.createStringFilter(BloomFilterAggregatorTest.values2, BloomFilterAggregatorTest.filter2, combinedValuesFilter);
            BloomFilterAggregatorTest.serializedFilter1 = BloomFilterAggregatorTest.filterToString(BloomFilterAggregatorTest.filter1);
            BloomFilterAggregatorTest.serializedFilter2 = BloomFilterAggregatorTest.filterToString(BloomFilterAggregatorTest.filter2);
            BloomFilterAggregatorTest.serializedCombinedFilter = BloomFilterAggregatorTest.filterToString(combinedValuesFilter);
            BloomKFilter longFilter = new BloomKFilter(BloomFilterAggregatorTest.maxNumValues);
            for (long val : BloomFilterAggregatorTest.longValues1) {
                longFilter.addLong(val);
            }
            BloomFilterAggregatorTest.serializedLongFilter = BloomFilterAggregatorTest.filterToString(longFilter);
            BloomKFilter floatFilter = new BloomKFilter(BloomFilterAggregatorTest.maxNumValues);
            for (float val : BloomFilterAggregatorTest.floatValues1) {
                floatFilter.addFloat(val);
            }
            BloomFilterAggregatorTest.serializedFloatFilter = BloomFilterAggregatorTest.filterToString(floatFilter);
            BloomKFilter doubleFilter = new BloomKFilter(BloomFilterAggregatorTest.maxNumValues);
            for (double val : BloomFilterAggregatorTest.doubleValues1) {
                doubleFilter.addDouble(val);
            }
            BloomFilterAggregatorTest.serializedDoubleFilter = BloomFilterAggregatorTest.filterToString(doubleFilter);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private final DimensionSpec dimSpec = new DefaultDimensionSpec("dim1", "dim1");

    private BloomFilterAggregatorFactory valueAggregatorFactory;

    public BloomFilterAggregatorTest() {
        valueAggregatorFactory = new BloomFilterAggregatorFactory("billy", dimSpec, BloomFilterAggregatorTest.maxNumValues);
    }

    @Test
    public void testAggregateValues() throws IOException {
        DimensionSelector dimSelector = new CardinalityAggregatorTest.TestDimensionSelector(BloomFilterAggregatorTest.values1, null);
        StringBloomFilterAggregator agg = new StringBloomFilterAggregator(dimSelector, new BloomKFilter(BloomFilterAggregatorTest.maxNumValues));
        for (int i = 0; i < (BloomFilterAggregatorTest.values1.size()); ++i) {
            BloomFilterAggregatorTest.aggregateDimension(Collections.singletonList(dimSelector), agg);
        }
        BloomKFilter bloomKFilter = ((BloomKFilter) (valueAggregatorFactory.finalizeComputation(agg.get())));
        String serialized = BloomFilterAggregatorTest.filterToString(bloomKFilter);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedFilter1, serialized);
    }

    @Test
    public void testAggregateLongValues() throws IOException {
        BloomFilterAggregatorTest.TestLongColumnSelector selector = new BloomFilterAggregatorTest.TestLongColumnSelector(Arrays.asList(BloomFilterAggregatorTest.longValues1));
        LongBloomFilterAggregator agg = new LongBloomFilterAggregator(selector, new BloomKFilter(BloomFilterAggregatorTest.maxNumValues));
        for (Long ignored : BloomFilterAggregatorTest.longValues1) {
            BloomFilterAggregatorTest.aggregateColumn(Collections.singletonList(selector), agg);
        }
        BloomKFilter bloomKFilter = ((BloomKFilter) (valueAggregatorFactory.finalizeComputation(agg.get())));
        String serialized = BloomFilterAggregatorTest.filterToString(bloomKFilter);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedLongFilter, serialized);
    }

    @Test
    public void testAggregateFloatValues() throws IOException {
        BloomFilterAggregatorTest.TestFloatColumnSelector selector = new BloomFilterAggregatorTest.TestFloatColumnSelector(Arrays.asList(BloomFilterAggregatorTest.floatValues1));
        FloatBloomFilterAggregator agg = new FloatBloomFilterAggregator(selector, new BloomKFilter(BloomFilterAggregatorTest.maxNumValues));
        for (Float ignored : BloomFilterAggregatorTest.floatValues1) {
            BloomFilterAggregatorTest.aggregateColumn(Collections.singletonList(selector), agg);
        }
        BloomKFilter bloomKFilter = ((BloomKFilter) (valueAggregatorFactory.finalizeComputation(agg.get())));
        String serialized = BloomFilterAggregatorTest.filterToString(bloomKFilter);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedFloatFilter, serialized);
    }

    @Test
    public void testAggregateDoubleValues() throws IOException {
        BloomFilterAggregatorTest.TestDoubleColumnSelector selector = new BloomFilterAggregatorTest.TestDoubleColumnSelector(Arrays.asList(BloomFilterAggregatorTest.doubleValues1));
        DoubleBloomFilterAggregator agg = new DoubleBloomFilterAggregator(selector, new BloomKFilter(BloomFilterAggregatorTest.maxNumValues));
        for (Double ignored : BloomFilterAggregatorTest.doubleValues1) {
            BloomFilterAggregatorTest.aggregateColumn(Collections.singletonList(selector), agg);
        }
        BloomKFilter bloomKFilter = ((BloomKFilter) (valueAggregatorFactory.finalizeComputation(agg.get())));
        String serialized = BloomFilterAggregatorTest.filterToString(bloomKFilter);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedDoubleFilter, serialized);
    }

    @Test
    public void testBufferAggregateStringValues() throws IOException {
        DimensionSelector dimSelector = new CardinalityAggregatorTest.TestDimensionSelector(BloomFilterAggregatorTest.values2, null);
        StringBloomFilterBufferAggregator agg = new StringBloomFilterBufferAggregator(dimSelector, BloomFilterAggregatorTest.maxNumValues);
        int maxSize = valueAggregatorFactory.getMaxIntermediateSizeWithNulls();
        ByteBuffer buf = ByteBuffer.allocate((maxSize + 64));
        int pos = 10;
        buf.limit((pos + maxSize));
        agg.init(buf, pos);
        for (int i = 0; i < (BloomFilterAggregatorTest.values2.size()); ++i) {
            BloomFilterAggregatorTest.bufferAggregateDimension(Collections.singletonList(dimSelector), agg, buf, pos);
        }
        BloomKFilter bloomKFilter = ((BloomKFilter) (valueAggregatorFactory.finalizeComputation(agg.get(buf, pos))));
        String serialized = BloomFilterAggregatorTest.filterToString(bloomKFilter);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedFilter2, serialized);
    }

    @Test
    public void testBufferAggregateLongValues() throws IOException {
        BloomFilterAggregatorTest.TestLongColumnSelector selector = new BloomFilterAggregatorTest.TestLongColumnSelector(Arrays.asList(BloomFilterAggregatorTest.longValues1));
        LongBloomFilterBufferAggregator agg = new LongBloomFilterBufferAggregator(selector, BloomFilterAggregatorTest.maxNumValues);
        int maxSize = valueAggregatorFactory.getMaxIntermediateSizeWithNulls();
        ByteBuffer buf = ByteBuffer.allocate((maxSize + 64));
        int pos = 10;
        buf.limit((pos + maxSize));
        agg.init(buf, pos);
        IntStream.range(0, BloomFilterAggregatorTest.longValues1.length).forEach(( i) -> BloomFilterAggregatorTest.bufferAggregateColumn(Collections.singletonList(selector), agg, buf, pos));
        BloomKFilter bloomKFilter = ((BloomKFilter) (valueAggregatorFactory.finalizeComputation(agg.get(buf, pos))));
        String serialized = BloomFilterAggregatorTest.filterToString(bloomKFilter);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedLongFilter, serialized);
    }

    @Test
    public void testBufferAggregateFloatValues() throws IOException {
        BloomFilterAggregatorTest.TestFloatColumnSelector selector = new BloomFilterAggregatorTest.TestFloatColumnSelector(Arrays.asList(BloomFilterAggregatorTest.floatValues1));
        FloatBloomFilterBufferAggregator agg = new FloatBloomFilterBufferAggregator(selector, BloomFilterAggregatorTest.maxNumValues);
        int maxSize = valueAggregatorFactory.getMaxIntermediateSizeWithNulls();
        ByteBuffer buf = ByteBuffer.allocate((maxSize + 64));
        int pos = 10;
        buf.limit((pos + maxSize));
        agg.init(buf, pos);
        IntStream.range(0, BloomFilterAggregatorTest.floatValues1.length).forEach(( i) -> BloomFilterAggregatorTest.bufferAggregateColumn(Collections.singletonList(selector), agg, buf, pos));
        BloomKFilter bloomKFilter = ((BloomKFilter) (valueAggregatorFactory.finalizeComputation(agg.get(buf, pos))));
        String serialized = BloomFilterAggregatorTest.filterToString(bloomKFilter);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedFloatFilter, serialized);
    }

    @Test
    public void testBufferAggregateDoubleValues() throws IOException {
        BloomFilterAggregatorTest.TestDoubleColumnSelector selector = new BloomFilterAggregatorTest.TestDoubleColumnSelector(Arrays.asList(BloomFilterAggregatorTest.doubleValues1));
        DoubleBloomFilterBufferAggregator agg = new DoubleBloomFilterBufferAggregator(selector, BloomFilterAggregatorTest.maxNumValues);
        int maxSize = valueAggregatorFactory.getMaxIntermediateSizeWithNulls();
        ByteBuffer buf = ByteBuffer.allocate((maxSize + 64));
        int pos = 10;
        buf.limit((pos + maxSize));
        agg.init(buf, pos);
        IntStream.range(0, BloomFilterAggregatorTest.doubleValues1.length).forEach(( i) -> BloomFilterAggregatorTest.bufferAggregateColumn(Collections.singletonList(selector), agg, buf, pos));
        BloomKFilter bloomKFilter = ((BloomKFilter) (valueAggregatorFactory.finalizeComputation(agg.get(buf, pos))));
        String serialized = BloomFilterAggregatorTest.filterToString(bloomKFilter);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedDoubleFilter, serialized);
    }

    @Test
    public void testCombineValues() throws IOException {
        DimensionSelector dimSelector1 = new CardinalityAggregatorTest.TestDimensionSelector(BloomFilterAggregatorTest.values1, null);
        DimensionSelector dimSelector2 = new CardinalityAggregatorTest.TestDimensionSelector(BloomFilterAggregatorTest.values2, null);
        StringBloomFilterAggregator agg1 = new StringBloomFilterAggregator(dimSelector1, new BloomKFilter(BloomFilterAggregatorTest.maxNumValues));
        StringBloomFilterAggregator agg2 = new StringBloomFilterAggregator(dimSelector2, new BloomKFilter(BloomFilterAggregatorTest.maxNumValues));
        for (int i = 0; i < (BloomFilterAggregatorTest.values1.size()); ++i) {
            BloomFilterAggregatorTest.aggregateDimension(Collections.singletonList(dimSelector1), agg1);
        }
        for (int i = 0; i < (BloomFilterAggregatorTest.values2.size()); ++i) {
            BloomFilterAggregatorTest.aggregateDimension(Collections.singletonList(dimSelector2), agg2);
        }
        BloomKFilter combined = ((BloomKFilter) (valueAggregatorFactory.finalizeComputation(valueAggregatorFactory.combine(agg1.get(), agg2.get()))));
        String serialized = BloomFilterAggregatorTest.filterToString(combined);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedCombinedFilter, serialized);
    }

    @Test
    public void testMergeValues() throws IOException {
        final BloomFilterAggregatorTest.TestBloomFilterColumnSelector mergeDim = new BloomFilterAggregatorTest.TestBloomFilterColumnSelector(ImmutableList.of(BloomFilterAggregatorTest.filter1, BloomFilterAggregatorTest.filter2));
        BloomFilterMergeAggregator mergeAggregator = new BloomFilterMergeAggregator(mergeDim, new BloomKFilter(BloomFilterAggregatorTest.maxNumValues));
        for (int i = 0; i < 2; ++i) {
            BloomFilterAggregatorTest.aggregateColumn(Collections.singletonList(mergeDim), mergeAggregator);
        }
        BloomKFilter merged = ((BloomKFilter) (valueAggregatorFactory.getCombiningFactory().finalizeComputation(mergeAggregator.get())));
        String serialized = BloomFilterAggregatorTest.filterToString(merged);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedCombinedFilter, serialized);
    }

    @Test
    public void testMergeValuesWithBuffersForGroupByV1() throws IOException {
        final BloomFilterAggregatorTest.TestBloomFilterColumnSelector mergeDim = new BloomFilterAggregatorTest.TestBloomFilterColumnSelector(ImmutableList.of(ByteBuffer.wrap(BloomFilterSerializersModule.bloomKFilterToBytes(BloomFilterAggregatorTest.filter1)), ByteBuffer.wrap(BloomFilterSerializersModule.bloomKFilterToBytes(BloomFilterAggregatorTest.filter2))));
        BloomFilterMergeAggregator mergeAggregator = new BloomFilterMergeAggregator(mergeDim, new BloomKFilter(BloomFilterAggregatorTest.maxNumValues));
        for (int i = 0; i < 2; ++i) {
            BloomFilterAggregatorTest.aggregateColumn(Collections.singletonList(mergeDim), mergeAggregator);
        }
        BloomKFilter merged = ((BloomKFilter) (valueAggregatorFactory.getCombiningFactory().finalizeComputation(mergeAggregator.get())));
        String serialized = BloomFilterAggregatorTest.filterToString(merged);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedCombinedFilter, serialized);
    }

    @Test
    public void testBuferMergeValues() throws IOException {
        final BloomFilterAggregatorTest.TestBloomFilterBufferColumnSelector mergeDim = new BloomFilterAggregatorTest.TestBloomFilterBufferColumnSelector(ImmutableList.of(ByteBuffer.wrap(BloomFilterSerializersModule.bloomKFilterToBytes(BloomFilterAggregatorTest.filter1)), ByteBuffer.wrap(BloomFilterSerializersModule.bloomKFilterToBytes(BloomFilterAggregatorTest.filter2))));
        BloomFilterMergeBufferAggregator mergeAggregator = new BloomFilterMergeBufferAggregator(mergeDim, BloomFilterAggregatorTest.maxNumValues);
        int maxSize = valueAggregatorFactory.getCombiningFactory().getMaxIntermediateSizeWithNulls();
        ByteBuffer buf = ByteBuffer.allocate((maxSize + 64));
        int pos = 10;
        buf.limit((pos + maxSize));
        mergeAggregator.init(buf, pos);
        for (int i = 0; i < 2; ++i) {
            BloomFilterAggregatorTest.bufferAggregateColumn(Collections.singletonList(mergeDim), mergeAggregator, buf, pos);
        }
        BloomKFilter merged = ((BloomKFilter) (valueAggregatorFactory.getCombiningFactory().finalizeComputation(mergeAggregator.get(buf, pos))));
        String serialized = BloomFilterAggregatorTest.filterToString(merged);
        Assert.assertEquals(BloomFilterAggregatorTest.serializedCombinedFilter, serialized);
    }

    @Test
    public void testSerde() throws Exception {
        BloomFilterAggregatorFactory factory = new BloomFilterAggregatorFactory("billy", new DefaultDimensionSpec("b", "b"), BloomFilterAggregatorTest.maxNumValues);
        ObjectMapper objectMapper = new DefaultObjectMapper();
        new BloomFilterExtensionModule().getJacksonModules().forEach(objectMapper::registerModule);
        Assert.assertEquals(factory, objectMapper.readValue(objectMapper.writeValueAsString(factory), AggregatorFactory.class));
        String fieldNamesOnly = "{" + (((("\"type\":\"bloom\"," + "\"name\":\"billy\",") + "\"field\":\"b\",") + "\"maxNumEntries\":15") + "}");
        Assert.assertEquals(factory, objectMapper.readValue(fieldNamesOnly, AggregatorFactory.class));
        BloomFilterAggregatorFactory factory2 = new BloomFilterAggregatorFactory("billy", new org.apache.druid.query.dimension.ExtractionDimensionSpec("b", "b", new RegexDimExtractionFn(".*", false, null)), BloomFilterAggregatorTest.maxNumValues);
        Assert.assertEquals(factory2, objectMapper.readValue(objectMapper.writeValueAsString(factory2), AggregatorFactory.class));
        BloomFilterAggregatorFactory factory3 = new BloomFilterAggregatorFactory("billy", new org.apache.druid.query.dimension.RegexFilteredDimensionSpec(new DefaultDimensionSpec("a", "a"), ".*"), BloomFilterAggregatorTest.maxNumValues);
        Assert.assertEquals(factory3, objectMapper.readValue(objectMapper.writeValueAsString(factory3), AggregatorFactory.class));
    }

    private abstract static class SteppableSelector<T> implements ColumnValueSelector<T> {
        List<T> values;

        int pos;

        public SteppableSelector(List<T> values) {
            this.values = values;
            this.pos = 0;
        }

        @Nullable
        @Override
        public T getObject() {
            return values.get(pos);
        }

        public void increment() {
            (pos)++;
        }

        public void reset() {
            pos = 0;
        }

        @Override
        public double getDouble() {
            throw new UnsupportedOperationException();
        }

        @Override
        public float getFloat() {
            throw new UnsupportedOperationException();
        }

        @Override
        public long getLong() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void inspectRuntimeShape(RuntimeShapeInspector inspector) {
        }

        @Override
        public Class<T> classOfObject() {
            return null;
        }

        @Override
        public boolean isNull() {
            return false;
        }
    }

    public static class TestBloomFilterColumnSelector extends BloomFilterAggregatorTest.SteppableSelector<Object> {
        public TestBloomFilterColumnSelector(List<Object> values) {
            super(values);
        }
    }

    public static class TestBloomFilterBufferColumnSelector extends BloomFilterAggregatorTest.SteppableSelector<ByteBuffer> {
        public TestBloomFilterBufferColumnSelector(List<ByteBuffer> values) {
            super(values);
        }
    }

    public static class TestLongColumnSelector extends BloomFilterAggregatorTest.SteppableSelector<Long> implements LongColumnSelector {
        public TestLongColumnSelector(List<Long> values) {
            super(values);
        }

        @Override
        public long getLong() {
            return values.get(pos);
        }
    }

    public static class TestFloatColumnSelector extends BloomFilterAggregatorTest.SteppableSelector<Float> implements FloatColumnSelector {
        public TestFloatColumnSelector(List<Float> values) {
            super(values);
        }

        @Override
        public float getFloat() {
            return values.get(pos);
        }
    }

    public static class TestDoubleColumnSelector extends BloomFilterAggregatorTest.SteppableSelector<Double> implements DoubleColumnSelector {
        public TestDoubleColumnSelector(List<Double> values) {
            super(values);
        }

        @Override
        public double getDouble() {
            return values.get(pos);
        }
    }
}

